package api.organizations;

import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.ErrorCode;
import Constants.SpogConstants;
import InvokerServer.Org4SPOGServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import api.preparedata.InitialTestData;
import api.preparedata.InitialTestDataImpl;
import bsh.org.objectweb.asm.Constants;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import groovyjarjarantlr.PreservingFileWriter;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;

import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import Constants.SpogConstants;

import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;


public class DeactiveOrganizationTest extends base.prepare.Is4Org{

	private SPOGServer spogServer;
	private UserSpogServer userSpogServer;
	private Org4SPOGServer org4spogServer;
	private String csrAdmin;
	private String csrPwd;
//	private ExtentReports rep;
	private ExtentTest test;
	
	private String initial_deleted_orgID; 
	private String deleted_direct_org_name = "spog_qa_deleted_zhaoguo";
	private String deleted_direct_email = "spog_qa_deleted_zhaoguo@arcserve.com";
	private String deleted_direct_email_full="";
	private String deleted_direct_first_name = "spog_qa_deleted_ma";
	private String deleted_direct_last_name = "spog_qa_deleted_zhaoguo";
	

	private String csr_readonly_email = "zhaoguo.ma+csrreadonly@gmail.com";
	private String csr_readonly_password = "Zetta1234";

	private InitialTestDataImpl initialTestDataImpl;
	private InitialTestData itd;

	private String password = "Pa$$w0rd";

	
//	private SQLServerDb bqdb1;
//	  public int Nooftest;
//	  private long creationTime;
//	  private String BQName=null;
//	  private String runningMachine;
//	  private testcasescount count1;
//	  private String buildVersion;

	private String  org_model_prefix=this.getClass().getSimpleName();
	@BeforeClass
	@Parameters({ "baseURI", "port", "csrAdminUserName", "csrAdminPassword", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String csrAdminUserName, String csrAdminPassword,
			String logFolder, String runningMachine, String buildVersion) {
		rep = ExtentManager.getInstance("GetUsersFromOrganizationTest", logFolder);
		test = rep.startTest("initializing data...");
		
		this.BQName = this.getClass().getSimpleName();
        String author = "Zhaoguo.Ma";
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
                  bqdb1.updateTable(BQName, runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0", String.valueOf(Nooftest), count1.getcreationtime(), "InProgress", author + " and Rest server is "+baseURI.split("//")[1]);
            } catch (ClientProtocolException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
            } catch (IOException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
            }
        }


		
		spogServer = new SPOGServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		org4spogServer = new Org4SPOGServer(baseURI, port);
		this.csrAdmin = csrAdminUserName;
		this.csrPwd = csrAdminPassword;
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		String prefix = RandomStringUtils.randomAlphanumeric(8);

		String prefix_a = RandomStringUtils.randomAlphanumeric(8) + this.getClass().getSimpleName();		
		initialTestDataImpl = new InitialTestDataImpl(baseURI, port, csrAdminUserName, csrAdminPassword, prefix_a, password);
		itd = initialTestDataImpl.initialize();
		
		this.initial_deleted_orgID = spogServer.CreateOrganizationWithCheck(prefix + deleted_direct_org_name  + org_model_prefix,
				SpogConstants.DIRECT_ORG, prefix + deleted_direct_email , password, prefix + deleted_direct_first_name ,
				prefix + deleted_direct_last_name);
		
		spogServer.DeleteOrganizationWithCheck(initial_deleted_orgID, test);

	}
	@DataProvider(name = "deactive_organization_params")
	public final Object[][] withouttoken_OrganizationInfo() {
		return new Object[][] { 
				{ itd.getMsp_org_1_user_1_email(), password, itd.getMsp_org_1()},
				{ itd.getDirect_org_1_user_1_email(), password, itd.getDirect_org_1()},
				{ itd.getMsp_org_1_sub_1_user_1_email(), password, itd.getMsp_org_1_sub_1()},
				
				// root msp related
				{ itd.getRoot_msp_org_1_user_1_email(), password, itd.getRoot_msp_org_1()},
				{ itd.getRoot_msp_org_1_sub_msp_1_user_1_email(), password, itd.getRoot_msp_org_1_sub_msp_1()},
				{ itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1_email(), password, itd.getRoot_msp_org_1_sub_msp_1_account_1()},
				{ itd.getRoot_msp_org_1_account_1_user_1_email(), password, itd.getRoot_msp_org_1_account_1()},
				
		};
	}
	@Test (dataProvider = "deactive_organization_params")
	public void deactiveOrg(String username, String password, String organizationID) {
		
		spogServer.userLogin(username, password);
		org4spogServer.setToken(spogServer.getJWTToken());
		org4spogServer.deactiveOrganizationWithErrorCheck(organizationID, 403, "00900003", test);
		
		spogServer.userLogin(csr_readonly_email, csr_readonly_password);
		org4spogServer.setToken(spogServer.getJWTToken());
		org4spogServer.deactiveOrganizationWithErrorCheck(organizationID, 403, "00100101", test);
		
		
		spogServer.userLogin(csrAdmin, csrPwd);
		org4spogServer.setToken(spogServer.getJWTToken());
		org4spogServer.deactiveOrganization(organizationID, test);
		org4spogServer.deactiveOrganizationWithErrorCheck(UUID.randomUUID().toString(), 404, "0030000A", test);
		org4spogServer.deactiveOrganizationWithErrorCheck("uuid", 400, "40000005", test);
		org4spogServer.setToken("");
		org4spogServer.deactiveOrganizationWithErrorCheck(organizationID, 401, "00900006", test);
	}
	
	@AfterMethod
	public void afterMethodTest() {
		rep.endTest(test);
		rep.flush();
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
