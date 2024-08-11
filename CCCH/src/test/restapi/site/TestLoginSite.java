package test.restapi.site;

import static invoker.SiteTestHelper.*;
import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import Constants.SpogConstants;
import InvokerServer.SPOGServer;
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

import Base64.Base64Coder;
import genericutil.ErrorHandler;
import InvokerServer.SPOGServer;

public class TestLoginSite extends base.prepare.PrepareOrgInfo {
	  @Parameters({ "pmfKey"})
	  public TestLoginSite(String pmfKey) {
			super(pmfKey);
			// TODO Auto-generated constructor stub
		}
	  private String        token;
	  private String csrAdminUserName;
	  private String csrAdminPassword;
	  private static String TestDataPrefix = "TestUpdateSiteById";
	  private ExtentReports extent;
	  private ExtentTest    logger;
	  static ErrorHandler errorHandle = ErrorHandler.getErrorHandler();
	  String site_registration_key=null;
	  private ExtentTest test;
//	  private ExtentReports rep;
	  private String site_version="1.0.0";
	  private String gateway_hostname="yongjun";
	  private String csrReadOnlyUser = "liuyu05@arcserve.com";
	  private String sharePassword = "Caworld_2018";
	  private SPOGServer spogServer;
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
	  public void setSpogServerConnection(String baseUri, String port, String username, String password,
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
                bqdb1.updateTable(this.BQName, runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0", String.valueOf(Nooftest), count1.getcreationtime(), "InProgress",author+" and Rest server is "+baseUri.split("//")[1]);
          } catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  }
		  setEnv(baseUri,  port,  username, password);
		  //end 
	    configSpogServerConnection(baseUri, port);
	    this.csrAdminUserName =username;
	    this.csrAdminPassword =password;
	    token = loginSpogServer(username, password);
	    spogServer = new SPOGServer(baseUri, port);
	    spogServer.login(username, password);

	    rep = ExtentManager.getInstance("TestLoginSite", logFolder);
	    test = rep.startTest("beforeClass");
	    
	    prepare(baseUri, port, logFolder, this.csrAdminUserName,  this.csrAdminPassword, this.getClass().getSimpleName() );
	  }
	  
	  @DataProvider(name = "orgInfo")
	  public final Object[][] getOrgInfo() {
		  return new Object[][] { 
			  {this.root_msp_org_id},
			  {this.root_msp_direct_org1_id},
			  {this.sub_msp1_org_id},
			  {this.sub_msp1_account1_id},
	          };
		}
	  
	  @Test (dataProvider = "orgInfo") 
	  public void checkSiteLoginInDiffOrg(String orgId){	 
		  System.out.println("checkSiteLoginInDiffOrg");
		  test = rep.startTest("checkSiteLoginInDiffOrg");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login
		  spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		  String token = spogServer.getJWTToken();
		  
		// create a site for org
		    String siteName = getRandomSiteName(TestDataPrefix);
		    Response response = createSite(siteName, siteType.cloud_direct.toString(), orgId, token);
		    response.then().statusCode(ServerResponseCode.Success_Post);
		    String siteId = response.then().extract().path("data.site_id");
		    System.out.println("siteId:"+siteId);
		    // get site registration_key after create site
		    String registration_basecode = response.then().extract().path("data.registration_basecode");
		    System.out.println("registration_basecode:"+registration_basecode);
			try {

				String decoded = URLDecoder.decode( registration_basecode.trim(), "UTF-8" );
				Base64Coder base64 = new Base64Coder();
				String decrypted = base64.decode( decoded );
				String[] parts    = decrypted.split( "\\n", -2 );
				site_registration_key=parts[1];
				System.out.println("site_registration_key:"+site_registration_key);
				//test.log(LogStatus.INFO, "Decoded base64code is : "+parts[1]);   
				
			} catch(UnsupportedEncodingException e ){
				logger.log(LogStatus.FAIL,"The value of the error Message :"+e.getMessage());
			}
		     //Register a site 
			String gateway_id = UUID.randomUUID().toString();
			System.out.println("gateway_id:"+gateway_id);
			gateway_hostname=RandomStringUtils.randomAlphanumeric(4)+"_"+gateway_hostname;
			Response response2=registerSiteWithInfo(site_registration_key,gateway_id,gateway_hostname,site_version,siteId);
			String site_secret= response2.then().extract().path("data.site_secret");
			System.out.println("site_secret:"+site_secret);
	        
			//login a site
			Response response3 = LoginSiteWithInfo(siteId, site_secret);
			response3.then().statusCode(ServerResponseCode.Succes_Login);

		  }

	  @Test
	  //case 1: login site successfully with correct secret key and gateway ID
	  public void Given_CsrAdmin_when_LoginSiteOfMspOrgnization_Should_Successful() {
		System.out.println("case 1");
		String csrToken = this.token;
	    logger = rep.startTest("Given_CsrAdmin_UpdateSiteOfMspOrgnization_Should_Successful");
	    
	    spogServer.setToken(csrToken);
	    // create a org
	    String orgName = getRandomOrganizationName(TestDataPrefix);

	    String orgId = spogServer.CreateOrganizationWithCheck(orgName+org_prefix, SpogConstants.MSP_ORG, RandomStringUtils.randomAlphanumeric(8) + "@SpogQa", 
	    		 RandomStringUtils.randomAlphanumeric(8)+"Ab1", "yuefen", "liu");
	    		

	    // create a site for org
	    String siteName = getRandomSiteName(TestDataPrefix);
	    Response response = createSite(siteName, siteType.cloud_direct.toString(), orgId, csrToken);
	    response.then().statusCode(ServerResponseCode.Success_Post);
	    String siteId = response.then().extract().path("data.site_id");
	    System.out.println("siteId:"+siteId);
	    // get site registration_key after create site
	    String registration_basecode = response.then().extract().path("data.registration_basecode");
	    System.out.println("registration_basecode:"+registration_basecode);
		try {

			String decoded = URLDecoder.decode( registration_basecode.trim(), "UTF-8" );
			Base64Coder base64 = new Base64Coder();
			String decrypted = base64.decode( decoded );
			String[] parts    = decrypted.split( "\\n", -2 );
			site_registration_key=parts[1];
			System.out.println("site_registration_key:"+site_registration_key);
			//test.log(LogStatus.INFO, "Decoded base64code is : "+parts[1]);   
			
		} catch(UnsupportedEncodingException e ){
			logger.log(LogStatus.FAIL,"The value of the error Message :"+e.getMessage());
		}
	     //Register a site 
		String gateway_id = UUID.randomUUID().toString();
		System.out.println("gateway_id:"+gateway_id);
		gateway_hostname=RandomStringUtils.randomAlphanumeric(4)+"_"+gateway_hostname;
		Response response2=registerSiteWithInfo(site_registration_key,gateway_id,gateway_hostname,site_version,siteId);
		String site_secret= response2.then().extract().path("data.site_secret");
		System.out.println("site_secret:"+site_secret);
        
		//login a site
		Response response3 = LoginSiteWithInfo(siteId, site_secret);
		response3.then().statusCode(ServerResponseCode.Succes_Login);
        //case 4.site_secret IS NEVER returned in an API
        System.out.println("case 4");
        String site_secret1 = response3.then().extract().path("data.site_secret");
        System.out.println("site_secret:"+site_secret1);
		
		//case 2: Login site failed with invalid site ID
		System.out.println("case 2");
        String InvalidSite_id = "00000000-0000-0000-0000-000000000000";
        Response response4 = LoginSiteWithInfo(InvalidSite_id, site_secret);
        response4.then().body("errors.code[0]", equalTo("00400201"));
        
  
        //case 3: Login site failed with blank site ID
        System.out.println("case 3");
        Response response5 = LoginSiteWithInfo("", site_secret);  
        response5.then().statusCode(ServerResponseCode.Bad_Request);
        
        //case 5": login site failed with blank site_secret
        System.out.println("case 5");
        Response response6 = LoginSiteWithInfo(siteId, null);  
        response6.then().statusCode(ServerResponseCode.Not_login);
        response6.then().body("errors.code[0]", equalTo("00900006"));
        //case 6: login site failed with invalid site_secret
        System.out.println("case 6");
        Response response7 = LoginSiteWithInfo(siteId, "abc");  
        response7.then().statusCode(ServerResponseCode.Bad_Request);
        response7.then().body("errors.code[0]", equalTo("00400002"));
        
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
