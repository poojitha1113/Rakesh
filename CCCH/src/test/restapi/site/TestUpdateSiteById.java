package test.restapi.site;

import static invoker.SiteTestHelper.*;
import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import Constants.SpogConstants;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import InvokerServer.ServerResponseCode;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import invoker.SiteTestHelper;
import invoker.SiteTestHelper.siteType;
import io.restassured.response.Response;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import genericutil.ErrorHandler;
import InvokerServer.SPOGServer;

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

public class TestUpdateSiteById extends base.prepare.PrepareOrgInfo{

	  @Parameters({ "pmfKey"})
	  public TestUpdateSiteById(String pmfKey) {
			super(pmfKey);
			// TODO Auto-generated constructor stub
		}
	  private String csrAdminUserName;
	  private String csrAdminPassword;
	  private String        token;
	  private static String TestDataPrefix = "TestUpdateSiteById";
	  private String        NamePrefix = "TestUpdateSiteById";
	  private String directOrg_id;
	  private String mspOrg_id;
	  private String mspOrg_id2;
	  private String account_id;
	  private String mspAccountAdminEmail;
	  private String mspAccountAdminToken;
	  private String csrReadOnlyUser = "liuyu05@arcserve.com";
	  private String sharePassword = "Caworld_2018";
	  private String mspAccountAdmin_id;
	  private ExtentReports extent;
	  private ExtentTest    logger;
	  static ErrorHandler errorHandle = ErrorHandler.getErrorHandler();
	  private SPOGServer spogServer;
	  private UserSpogServer userSpogServer;
//	  private ExtentReports rep;
	  private ExtentTest test;
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
	  @BeforeTest
	  @Parameters({"baseURI", "port", "csrAdminUserName", "csrAdminPassword", "logFolder", "runningMachine", "buildVersion"})
	  public void setSpogServerConnection(String baseURI, String port, String username, String password,
	      String logFolder, String runningMachine, String buildVersion) {
		  
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
		  //rep = ExtentManager.getInstance("setSpogServerConnection",logFolder);
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
		  setEnv(baseURI,  port,  username, password);
		  //end 
		userSpogServer = new UserSpogServer(baseURI, port); 
		spogServer = new SPOGServer(baseURI, port);
		
		rep = ExtentManager.getInstance("TestUpdateSiteById", logFolder);
		test = rep.startTest("beforeClass");
		
	    configSpogServerConnection(baseURI, port);
	    this.csrAdminUserName =username;
	    this.csrAdminPassword =password;
	    token = loginSpogServer(username, password);
	    spogServer.userLogin(username, password);
	    
	    //create org
	    directOrg_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_group_direct1_yuefen")+org_prefix,"direct",spogServer.ReturnRandom("yuefen_direct1_group@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("direct_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
        mspOrg_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_group_yuefen_msp1")+org_prefix,"msp",spogServer.ReturnRandom("yuefen_msp1_group@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("msp_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
        mspOrg_id2 = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_group_yuefen_msp2")+org_prefix,"msp",spogServer.ReturnRandom("yuefen_msp1_group@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("msp_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
        
        account_id = spogServer.createAccountWithCheck(mspOrg_id,spogServer.ReturnRandom("spogqa_accoun1_msp1_yuefen_group")+org_prefix, mspOrg_id, test);
        //create user
        mspAccountAdminEmail=spogServer.ReturnRandom("msp_account_admin@spogqa.com");
        mspAccountAdmin_id=spogServer.createUserAndCheck(mspAccountAdminEmail, sharePassword, "yuefen","liu", "msp_account_admin", mspOrg_id, test);
	    spogServer.userLogin(mspAccountAdminEmail, sharePassword);
	    mspAccountAdminToken=spogServer.getJWTToken();
	    
	    prepare(baseURI, port, logFolder, this.csrAdminUserName,  this.csrAdminPassword, this.getClass().getSimpleName() );
	  }
	  
	  @DataProvider(name="siteInfo10")
      public final Object[][] getSiteInfo10() {
    	  return new Object[][] { 
    		  {this.final_root_msp_user_name_email, this.common_password, this.sub_msp1_org_id},
    		  {this.final_root_msp_user_name_email, this.common_password, this.sub_msp1_account1_id},
    		  {this.final_root_msp_user_name_email, this.common_password, this.mspOrg_id},
    		  {this.final_root_msp_user_name_email, this.common_password, this.directOrg_id},
    		  
    		  {this.final_sub_msp1_user_name_email, this.common_password, this.sub_msp2_org_id},
    		  {this.final_sub_msp1_user_name_email, this.common_password, this.sub_msp2_account1_id},
    		  {this.final_sub_msp1_user_name_email, this.common_password, this.root_msp_direct_org_id},
    		  {this.final_sub_msp1_user_name_email, this.common_password, this.root_msp_org_id},
    		  {this.final_sub_msp1_user_name_email, this.common_password, this.mspOrg_id},
    		  {this.final_sub_msp1_user_name_email, this.common_password, this.directOrg_id},
    		  
    		  {this.final_root_msp_direct_org_user_email, this.common_password, this.sub_msp1_org_id},
    		  {this.final_root_msp_direct_org_user_email, this.common_password, this.sub_msp1_account1_id},
    		  {this.final_root_msp_direct_org_user_email, this.common_password, this.root_msp_org_id},
    		  
    		  {this.final_sub_msp1_account1_user_email, this.common_password, this.sub_msp2_org_id},
    		  {this.final_sub_msp1_account1_user_email, this.common_password, this.sub_msp2_account1_id},
    		  {this.final_sub_msp1_account1_user_email, this.common_password, this.root_msp_direct_org_id},
    		  {this.final_sub_msp1_account1_user_email, this.common_password, this.root_msp_org_id},
    		  
    	  };
    	  }
	  @Test(dataProvider="siteInfo10")
	  //case 3: MSP account admin can't update site in other org
	  public void updateSiteFail(String loginUser, String loginPassword, String org) {
		  System.out.println("updateSiteFail");
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("liu, yuefen");
	
	    logger = rep.startTest("updateSiteFail");
	    //log in
	    spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		String csr_token = spogServer.getJWTToken();
	    // create site
	    String siteName =spogServer.ReturnRandom("site_name");
	    Response response = createSite(siteName, siteType.cloud_direct.toString(), org, csr_token);
	    System.out.println(" orginal sitename: " + siteName);
	    response.then().statusCode(ServerResponseCode.Success_Post);
	    
	    String siteId = response.then().extract().path("data.site_id");
	    
	    //log in
	    spogServer.userLogin(loginUser, loginPassword);
		String token = spogServer.getJWTToken();
        //update site
	    String newSiteName = getRandomSiteName(TestDataPrefix);
	    System.out.println(" new sitename: "+ newSiteName);
	    
	    response = updateSiteById(siteId,newSiteName,token);
	    spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS);
	    spogServer.checkErrorCode(response, "00100101");
	  }
	  
	  @DataProvider(name="siteInfo11")
      public final Object[][] getSiteInfo11() {
    	  return new Object[][] { 
    		  {this.final_root_msp_user_name_email, this.common_password, this.root_msp_org_id},
    		  {this.final_root_msp_user_name_email, this.common_password, this.root_msp_direct_org_id},
   
    		  {this.final_sub_msp1_user_name_email, this.common_password, this.sub_msp1_org_id},
    		  {this.final_sub_msp1_user_name_email, this.common_password, this.sub_msp1_account1_id},

    		  {this.final_root_msp_direct_org_user_email, this.common_password, this.root_msp_direct_org_id},
    		  {this.final_sub_msp1_account1_user_email, this.common_password, this.sub_msp1_account1_id},	  
    	  };
    	  }
	  @Test(dataProvider="siteInfo11")
	  //case 3: MSP account admin can't update site in other org
	  public void updateSiteSuccess(String loginUser, String loginPassword, String org) {
		  System.out.println("updateSiteSuccess");
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("liu, yuefen");
	
	    logger = rep.startTest("updateSiteSuccess");
	    //log in
	    spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		String csr_token = spogServer.getJWTToken();
	    // create site
	    String siteName =spogServer.ReturnRandom("site_name");
	    Response response = createSite(siteName, siteType.cloud_direct.toString(), org, csr_token);
	    System.out.println(" orginal sitename: " + siteName);
	    response.then().statusCode(ServerResponseCode.Success_Post);
	    
	    String siteId = response.then().extract().path("data.site_id");
	    
	    //log in
	    spogServer.userLogin(loginUser, loginPassword);
		String token = spogServer.getJWTToken();
        //update site
	    String newSiteName = getRandomSiteName(TestDataPrefix);
	    System.out.println(" new sitename: "+ newSiteName);
	    
	    response = updateSiteById(siteId,newSiteName,token);
	    spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
	  }
	  
	  @Test
	  public void csrReadOnlyUpdateSite() {
		  System.out.println("csrReadOnlyUpdateSite");
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("liu, yuefen");
		
		String csrToken = this.token;
	    logger = rep.startTest("Given_MspAdmin_when_UpdateSiteInOtherOrg_Should_Failed");
	    // create site
	    String siteName =spogServer.ReturnRandom("site_name");
	    Response response = createSite(siteName, siteType.cloud_direct.toString(), directOrg_id, csrToken);
	    System.out.println(" orginal sitename: " + siteName);
	    response.then().statusCode(ServerResponseCode.Success_Post);
	    
	    String siteId = response.then().extract().path("data.site_id");
	    
	    //login with csr readonly user
	    spogServer.userLogin(csrReadOnlyUser, sharePassword);
	    String csr_readonly_token = spogServer.getJWTToken();
        //update site
	    String newSiteName = getRandomSiteName(TestDataPrefix);
	    System.out.println(" new sitename: "+ newSiteName);
	   
	    response = updateSiteById(siteId,newSiteName,csr_readonly_token);
	    spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS);
	    spogServer.checkErrorCode(response, "00100101");
	  }
	  
	  @Test
	  // case 2:MSP account admin can update site in its managed account org
	  public void Given_MspAccountAdmin_when_UpdateAccountSite_Should_Successful() {
		  System.out.println("Given_MspAccountAdmin_when_UpdateAccountSite_Should_Successful");
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("liu, yuefen");
  
	  String csrToken = this.token;
	  spogServer.setToken(csrToken);
	  logger = rep.startTest("Given_MspAdmin_when_UpdateItsSite_Should_Successful");
	  // create a org
	  String orgName = getRandomOrganizationName(TestDataPrefix);

	  String orgId = spogServer.CreateOrganizationWithCheck(orgName+org_prefix, SpogConstants.MSP_ORG,RandomStringUtils.randomAlphanumeric(8) + "@SpogQa", 
		       RandomStringUtils.randomAlphanumeric(8)+"Ab1", "yuefen", "liu");
	  System.out.println("create org successfully");

	    // create msp account admin
	    spogServer.setToken(csrToken);
	    String mspAccountAdmin = "TestUpdateSiteById_" + RandomStringUtils.randomAlphanumeric(8)+ "@SpogQa";
	    String mspPassword = RandomStringUtils.randomAlphanumeric(8)+"Ab1";
	    String msp_account_admin_id=spogServer.createUserAndCheck(mspAccountAdmin, mspPassword,  RandomStringUtils.randomAlphanumeric(4),
		        RandomStringUtils.randomAlphanumeric(4), "msp_account_admin", orgId, test);
		  System.out.println("create mspaccountadmin successfully");
        //create account org
		String subOrg_id =spogServer.createAccountWithCheck(orgId, spogServer.ReturnRandom("subOrg_name")+org_prefix, orgId);
		//assign
		userSpogServer.assignMspAccountAdmins(orgId, subOrg_id, msp_account_admin_id, csrToken);
		
	    // login as msp account admin
		spogServer.userLogin(mspAccountAdmin, mspPassword);
	    String mspAccountAdminToken = spogServer.getJWTToken();
	  
	    // create a site 
	    String siteName = getRandomSiteName(TestDataPrefix);
	    Response response = createSite(siteName, siteType.cloud_direct.toString(), subOrg_id, mspAccountAdminToken);
	    System.out.println(" orginal sitename: " + siteName);
	    response.then().statusCode(ServerResponseCode.Success_Post);
	    
	    String siteId = response.then().extract().path("data.site_id");
	    
	    System.out.println("siteId: " + siteId);
	    //update the site by Id
	    String newSiteName = getRandomSiteName(TestDataPrefix);
	    System.out.println(" new sitename: "+ newSiteName);
	    response = updateSiteById(siteId,newSiteName,mspAccountAdminToken);
	    response.then().assertThat().body("data.site_name", equalTo(newSiteName));

	  // response.then().log().all();

	  }
	  
      @DataProvider(name="siteInfo")
      public final Object[][] getSiteInfo() {
    	  return new Object[][] { 
    		  {directOrg_id},
    		  {mspOrg_id2},
    		  {account_id}
    	  };
    	  }
	  @Test(dataProvider="siteInfo")
	  //case 3: MSP account admin can't update site in other org
	  public void Given_MspAccountAdmin_when_UpdateSiteInOtherOrg_Should_Failed(String org) {
		  System.out.println("Given_MspAccountAdmin_when_UpdateSiteInOtherOrg_Should_Failed");
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("liu, yuefen");
		
		String csrToken = this.token;
	    logger = rep.startTest("Given_MspAdmin_when_UpdateSiteInOtherOrg_Should_Failed");
	    // create site
	    String siteName =spogServer.ReturnRandom("site_name");
	    Response response = createSite(siteName, siteType.cloud_direct.toString(), org, csrToken);
	    System.out.println(" orginal sitename: " + siteName);
	    response.then().statusCode(ServerResponseCode.Success_Post);
	    
	    String siteId = response.then().extract().path("data.site_id");
        //update site
	    String newSiteName = getRandomSiteName(TestDataPrefix);
	    System.out.println(" new sitename: "+ newSiteName);
	   
	    response = updateSiteById(siteId,newSiteName,mspAccountAdminToken);
	    spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS);
	    spogServer.checkErrorCode(response, "00100101");
	  }
      

	  @Test
	  //case 1: csr admin can update site in its prg /case 
	  //case 50.  Can NOT call API if not logged in - 401when JWT is missing
      //case 08. site_secret IS NEVER returned in an API
	  public void Given_CsrAdmin_when_UpdateSiteOfMspOrgnization_Should_Successful() {
		  System.out.println("Given_CsrAdmin_when_UpdateSiteOfMspOrgnization_Should_Successful");
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("liu, yuefen");
		System.out.println("case 1");
		String csrToken = this.token;
		spogServer.setToken(csrToken);
	    logger = rep.startTest("Given_CsrAdmin_UpdateSiteOfMspOrgnization_Should_Successful");
	    // create a org
	    String orgName = getRandomOrganizationName(TestDataPrefix);
        
		String orgId = spogServer.CreateOrganizationWithCheck(orgName+org_prefix, SpogConstants.MSP_ORG,RandomStringUtils.randomAlphanumeric(8) + "@SpogQa", 
			       RandomStringUtils.randomAlphanumeric(8)+"Ab1", "yuefen", "liu");

	    // create a site for org
	    String siteName = getRandomSiteName(TestDataPrefix);
	    Response response = createSite(siteName, siteType.cloud_direct.toString(), orgId, csrToken);
	    response.then().statusCode(ServerResponseCode.Success_Post);
	    //response.then().log().all();
	    String siteId = response.then().extract().path("data.site_id");

	   //Update a site by Id
	    String newSiteName = getRandomSiteName(TestDataPrefix);
	    System.out.println(newSiteName);
	    response = updateSiteById(siteId,newSiteName,csrToken);

	    //response.then().log().all();
	    response.then().assertThat().body("data.site_name", equalTo(newSiteName));
	    
	    //case 08. site_secret IS NEVER returned in an API
	    System.out.println("case 8");
        response.then().assertThat().body("data.site_secret", equalTo(null));
        
	    //401 case, not logged in and call update site api directly
	    System.out.println("case 50");
	    String newToken = "";
	    Response response2 = response = updateSiteById(siteId,newSiteName,newToken);
	    response2.then().assertThat().statusCode(ServerResponseCode.Not_login);
	  }
	  
	  @Test
	  // case 2:MSP admin can update site in its org
	  public void Given_MspAdmin_when_UpdateItsSite_Should_Successful() {
		  System.out.println("Given_MspAdmin_when_UpdateItsSite_Should_Successful");
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("liu, yuefen");
	  System.out.println("case 2");
  
	  String csrToken = this.token;
	  spogServer.setToken(csrToken);
	  logger = rep.startTest("Given_MspAdmin_when_UpdateItsSite_Should_Successful");
	  // create a org
	  String orgName = getRandomOrganizationName(TestDataPrefix);

	  String orgId = spogServer.CreateOrganizationWithCheck(orgName+org_prefix, SpogConstants.MSP_ORG,RandomStringUtils.randomAlphanumeric(8) + "@SpogQa", 
		       RandomStringUtils.randomAlphanumeric(8)+"Ab1", "yuefen", "liu");
	  System.out.println("create org successfully");

	    // create msp admin
	    String mspAdmin = "TestUpdateSiteById_" + RandomStringUtils.randomAlphanumeric(8)+ "@SpogQa";
	    String mspPassword = RandomStringUtils.randomAlphanumeric(8)+"Ab1";
	    createMspAdminUser(mspAdmin, mspPassword, RandomStringUtils.randomAlphanumeric(4),
	        RandomStringUtils.randomAlphanumeric(4), orgId, csrToken);
		  System.out.println("create mspadmin successfully");

	    // login as msp admin
	    String mspToken = loginSpogServer(mspAdmin, mspPassword);
	  
	    // create a site for org as msp admin
	    String siteName = getRandomSiteName(TestDataPrefix);
	    Response response = createSite(siteName, siteType.cloud_direct.toString(), "", mspToken);
	    spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);
	   // spogServer.checkErrorCode(response, "00100024");
	    System.out.println(" orginal sitename: " + siteName);
	    
	    String siteId = response.then().extract().path("data.site_id");
	    
	    System.out.println("siteId: " + siteId);
	    //update the site by Id
	    String newSiteName = getRandomSiteName(TestDataPrefix);
	    System.out.println(" new sitename: "+ newSiteName);
	    response = updateSiteById(siteId,newSiteName,mspToken);
	    response.then().assertThat().body("data.site_name", equalTo(newSiteName));

	  // response.then().log().all();

	  }
      
	  @Test
	  //case 3: MSP admin can't update site in other org
	  public void Given_MspAdmin_when_UpdateSiteInOtherOrg_Should_Failed() {
		  System.out.println("Given_MspAdmin_when_UpdateSiteInOtherOrg_Should_Failed");
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("liu, yuefen");
		System.out.println("case 3");
		String csrToken = this.token;
		spogServer.setToken(csrToken);
	    logger = rep.startTest("Given_MspAdmin_when_UpdateSiteInOtherOrg_Should_Failed");
	    // create a org
	    String orgName = getRandomOrganizationName(TestDataPrefix);

		  String orgIdA = spogServer.CreateOrganizationWithCheck(orgName+org_prefix, SpogConstants.MSP_ORG,RandomStringUtils.randomAlphanumeric(8) + "@SpogQa", 
			       RandomStringUtils.randomAlphanumeric(8)+"Ab1", "yuefen", "liu");

		  String orgIdB = spogServer.CreateOrganizationWithCheck(orgName+org_prefix, SpogConstants.MSP_ORG,RandomStringUtils.randomAlphanumeric(8) + "@SpogQa", 
			       RandomStringUtils.randomAlphanumeric(8)+"Ab1", "yuefen", "liu");

	    // create msp A admin
	    String mspAAdmin = "TestUpdateSiteById_" + RandomStringUtils.randomAlphanumeric(8)+ "MSPadmin@SpogQa";
	    String mspAPassword = RandomStringUtils.randomAlphanumeric(8)+"Ab1";
	    createMspAdminUser(mspAAdmin, mspAPassword, RandomStringUtils.randomAlphanumeric(4),
	        RandomStringUtils.randomAlphanumeric(4), orgIdA, csrToken);

	    // login as msp admin
	    String mspAToken = loginSpogServer(mspAAdmin, mspAPassword);

	    // create a site for org B as csr admin
	    String siteName = getRandomSiteName(TestDataPrefix);
	    Response response = createSite(siteName, siteType.cloud_direct.toString(), orgIdB, csrToken);

	    response.then().statusCode(ServerResponseCode.Success_Post);

	    String siteId_OrgB = response.then().extract().path("data.site_id");

	    //try to update the site by Id
	    String newSiteName = getRandomSiteName(TestDataPrefix);
	    response = updateSiteById(siteId_OrgB,newSiteName,mspAToken);
	   
	    response.then().assertThat().statusCode(ServerResponseCode.Insufficient_Permissions);

	  }
      
	  @Test
	  // case 4: Direct admin can update site in its org
	  public void Given_DirectAdmin_when_UpdateSiteInItsOrg_Should_Successful() {
		  System.out.println("Given_DirectAdmin_when_UpdateSiteInItsOrg_Should_Successful");
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("liu, yuefen");
		System.out.println("case 4");
		String csrToken = this.token;
	    logger = rep.startTest("Given_DirectAdmin_when_UpdateSiteInItsOrg_Should_Successful");
	    // create a  direct org
	    String directOrgName = getRandomOrganizationName(TestDataPrefix);
		  String orgId = spogServer.CreateOrganizationWithCheck(directOrgName+org_prefix, SpogConstants.DIRECT_ORG,RandomStringUtils.randomAlphanumeric(8) + "direct@SpogQa", 
			       RandomStringUtils.randomAlphanumeric(8)+"Ab1", "yuefen", "liu");

	  String directAdmin_A = "TestUpdateSiteById_" + RandomStringUtils.randomAlphanumeric(8) + "directAdmin@SpogQa";
	    String directPassword_A = RandomStringUtils.randomAlphanumeric(8)+"Ab1";
	   createDirectAdminUser(directAdmin_A, directPassword_A, RandomStringUtils.randomAlphanumeric(4),
	      RandomStringUtils.randomAlphanumeric(4), orgId, csrToken);
	    System.out.println(" create direct admin A done. ");

	    String directToken_A = loginSpogServer(directAdmin_A, directPassword_A);

	    // create a site for org A as direct admin A
	    String siteName_A = getRandomSiteName(TestDataPrefix);
	    Response response = createSite(siteName_A, siteType.cloud_direct.toString(), "", directToken_A);

	    response.then().statusCode(ServerResponseCode.Success_Post);

	    String siteId_A = response.then().extract().path("data.site_id");

	    //update the site by Id
	    String newSiteName = getRandomSiteName(TestDataPrefix);
	    System.out.println(" new sitename: "+ newSiteName);
	    response = updateSiteById(siteId_A,newSiteName,directToken_A);
	    
	    response.then().assertThat().body("data.site_name", equalTo(newSiteName));
	   
	  }
      
	  @Test
	  // case 5: direct user can't update site in other org
	  public void Given_DirectAdmin_when_UpdateSiteInOtherOrg_Should_Failed() {
		  System.out.println("Given_DirectAdmin_when_UpdateSiteInOtherOrg_Should_Failed");
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("liu, yuefen");
			System.out.println("case 5");
			String csrToken = this.token;
		    logger = rep.startTest("Given_DirectAdmin_when_UpdateSiteInOtherOrg_Should_Failed");
		    // create a  direct org
		    String directOrgName = getRandomOrganizationName(TestDataPrefix);

		    String directOrgId_A = spogServer.CreateOrganizationWithCheck(directOrgName+org_prefix, SpogConstants.DIRECT_ORG,RandomStringUtils.randomAlphanumeric(8) + "directA@SpogQa", 
				       RandomStringUtils.randomAlphanumeric(8)+"Ab1", "yuefen", "liu");
		    String directOrgId_B = spogServer.CreateOrganizationWithCheck(directOrgName+org_prefix, SpogConstants.DIRECT_ORG,RandomStringUtils.randomAlphanumeric(8) + "directB@SpogQa", 
				       RandomStringUtils.randomAlphanumeric(8)+"Ab1", "yuefen", "liu");

		  String directAdmin_A = "TestUpdateSiteById_" + RandomStringUtils.randomAlphanumeric(8) + "@SpogQa";
		    String directPassword_A = RandomStringUtils.randomAlphanumeric(8)+"Ab1";
		   createDirectAdminUser(directAdmin_A, directPassword_A, RandomStringUtils.randomAlphanumeric(4),
		      RandomStringUtils.randomAlphanumeric(4), directOrgId_A, csrToken);
		    System.out.println(" create direct admin A done. ");

		    String directToken_A = loginSpogServer(directAdmin_A, directPassword_A);

		    // create a site for org A as direct admin A
		    String siteName_A = getRandomSiteName(TestDataPrefix);
		    Response response = createSite(siteName_A, siteType.cloud_direct.toString(), "", directToken_A);

		    response.then().statusCode(ServerResponseCode.Success_Post);

		    String siteId_A = response.then().extract().path("data.site_id");
		    
		    String directAdmin_B = "TestUpdateSiteById_" + RandomStringUtils.randomAlphanumeric(8)+"@SpogQa";
		    String directPassword_B = RandomStringUtils.randomAlphanumeric(8)+"Ab1";
		    createDirectAdminUser(directAdmin_B, directPassword_B, RandomStringUtils.randomAlphanumeric(4),
		        RandomStringUtils.randomAlphanumeric(4), directOrgId_B, csrToken);
		    System.out.println(" create direct admin B done. ");
		    String directToken_B = loginSpogServer(directAdmin_B, directPassword_B);
		    
		    //update the site by Id
		    String newSiteName = getRandomSiteName(TestDataPrefix);
			System.out.println("Site new name: " + newSiteName);
		    response = updateSiteById(siteId_A ,newSiteName,directToken_B);

		   

   	    response.then().statusCode(ServerResponseCode.Insufficient_Permissions);
	
	  }
      
	  @Test
	  // case 6: update site with invalid site id failed/case 7: update a site not in DB
	  public void Given_CsrAdmin_when_SiteIdNotExist_UpdateSite_Should_Failed() {
		  System.out.println("Given_CsrAdmin_when_SiteIdNotExist_UpdateSite_Should_Failed");
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("liu, yuefen");
	    String csrToken = this.token;
	    logger = rep.startTest("Given_CsrAdmin_when_SiteIdNotExist_UpdateSite_Should_Failed");
	    String siteId = "19474b6a-c3f4-4261-a532-notexistsite";

	    //update the site by Id
	    String newSiteName = getRandomSiteName(TestDataPrefix);
	    Response response = updateSiteById(siteId ,newSiteName,csrToken);
	    int statusCode = response.getStatusCode();
	    System.out.println("status code: " + statusCode);
	   // response.then().statusCode(ServerResponseCode.Resource_Does_Not_Exist);
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
