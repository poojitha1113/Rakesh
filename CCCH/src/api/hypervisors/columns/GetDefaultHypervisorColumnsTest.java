package api.hypervisors.columns;
import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.ErrorCode;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.Org4SPOGServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;
import ui.base.common.SPOGMenuTreePath;

import com.relevantcodes.extentreports.ExtentReports;

import InvokerServer.Source4SPOGServer;
import dataPreparation.JsonPreparation;

import org.testng.annotations.AfterTest;


public class GetDefaultHypervisorColumnsTest extends base.prepare.PrepareOrgInfo {
	  @Parameters({ "pmfKey"})
	  public GetDefaultHypervisorColumnsTest(String pmfKey) {
			super(pmfKey);
			// TODO Auto-generated constructor stub
		}

	  private static JsonPreparation jp = new JsonPreparation();
	  private SPOGServer spogServer;
	  private Source4SPOGServer source4SpogServer;
	  private Org4SPOGServer org4SpogServer;
	  private String csrAdminUserName;
	  private String csrAdminPassword;
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
	 // private String common_password="Welcome*02";
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
			System.out.println("GetDefaultHypervisorColumnsTest");
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
		  org4SpogServer = new Org4SPOGServer(baseURI, port);
		  rep = ExtentManager.getInstance("GetDefaultHypervisorColumnsTest",logFolder);
		  test = rep.startTest("beforeClass");
		  spogServer.userLogin(userName, password);
		  this.csrGlobalLoginUser = userName;
		  this.csrGlobalLoginPassword =password;
		  this.csrAdminUserName = userName;
		  this.csrAdminPassword = password;
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
		  this.msp_account_admin = spogServer.ReturnRandom("msp_account_admin_yuefen@spogqa.com");
		  spogServer.createUserAndCheck(msp_account_admin, sharePassword, "yuefen", "liu", "msp_account_admin", mspOrg_id, test);
		  //create account user
		  this.accountDirect_email = spogServer.ReturnRandom("account1_msp1_yuefen_group@spogqa.com");
		  spogServer.createUserAndCheck(accountDirect_email, sharePassword, "yuefen", "liu", "direct_admin", account_id, test);  
		  
		  prepare(baseURI, port, logFolder, this.csrAdminUserName,  this.csrAdminPassword, this.getClass().getSimpleName() );
	  }

	  @DataProvider(name = "userInfo")
	  public final Object[][] getColumnsInfo() {
		  return new Object[][] { 
			  {csrGlobalLoginUser, csrGlobalLoginPassword},
			  {csrReadOnlyUser, sharePassword},
			  {direct_email, sharePassword},
			  {msp_email, sharePassword},
			  {msp_account_admin, sharePassword},
			  {accountDirect_email, sharePassword},
			  
			  {this.final_root_msp_user_name_email, this.common_password},
			  {this.final_root_msp_account_admin_user_name_email, this.common_password},
			  {this.final_root_msp_direct_org_user_email, this.common_password},
			  {this.final_sub_msp1_msp_account_user_name_email, this.common_password},
			  {this.final_sub_msp1_user_name_email, this.common_password},
			  {this.final_sub_msp1_account1_user_email, this.common_password},
	          };
		}
	  
	  @Test (dataProvider = "userInfo") 
	  public void HypervsiorNameCheck(String name, String password){	 
		  System.out.println("HypervsiorNameCheck");
		  test = rep.startTest("HypervsiorNameCheck");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login
		  spogServer.userLogin(name, password, SpogConstants.SUCCESS_LOGIN, test);
		  String token = spogServer.getJWTToken();
		  
		  //get the default columns
		  source4SpogServer.setToken(token);
		  source4SpogServer.getDefaultHypervisorColumnsWithCheck(token, test);
		  }
	  
	  @Test 
	  public void HypervsiorNameCheckWithoutLogin401(){	 
		  System.out.println("HypervsiorNameCheck");
		  test = rep.startTest("HypervsiorNameCheck");
		  test.assignAuthor("Liu Yuefen");
		  
		  //set token
		  String token = "";
		  
		  //get the default columns
		  Response response = source4SpogServer.getDefaultHypervisorColumns(token, test);
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
