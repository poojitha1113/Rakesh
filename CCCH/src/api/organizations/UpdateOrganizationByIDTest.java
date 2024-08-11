package api.organizations;

import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.ErrorCode;
import Constants.SpogConstants;
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
import Constants.SpogConstants;

import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;


public class UpdateOrganizationByIDTest extends base.prepare.Is4Org {

	private SPOGServer spogServer;
	private UserSpogServer userSpogServer;
	private String csrAdmin;
	private String csrPwd;
//	private ExtentReports rep;
	private ExtentTest test;
	
	private String csr_readonly_email = "csr_readonly_zhaoguo@arcserve.com";
	private String csr_readonly_password = "Caworld_2017";

	private InitialTestDataImpl initialTestDataImpl;
	private InitialTestData itd;
	private String deleted_direct_org_name = "spog_qa_deleted_zhaoguo";
	private String deleted_direct_email = "spog_qa_deleted_zhaoguo@arcserve.com";
	private String deleted_direct_email_full="";
	private String deleted_direct_first_name = "spog_qa_deleted_ma";
	private String deleted_direct_last_name = "spog_qa_deleted_zhaoguo";
	private String initial_deleted_orgID;
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
	
	@DataProvider(name = "validorganizationInfo")
	public final Object[][] getvalidOrganizationInfo(){
		return new Object[][] { 
			{ itd.getMsp_org_1(), csrAdmin, csrPwd, "changed_by_csr_msp" + org_model_prefix },
			{ itd.getDirect_org_1(), csrAdmin, csrPwd, "changed_by_csr_direct" + org_model_prefix },
				// it is commented out due to bug 820551;
			{ itd.getMsp_org_1(), itd.getMsp_org_1_user_1_email(), password, "changed_by_admin_msp" + org_model_prefix },
			{ itd.getDirect_org_1(), itd.getDirect_org_1_user_1_email(), password, "changed_by_admin_direct" + org_model_prefix },
			{ itd.getMsp_org_1_sub_2(), csrAdmin, csrPwd, "changed_by_csr_sub" + org_model_prefix },
			{ itd.getMsp_org_1_sub_1(), itd.getMsp_org_1_user_1_email(), password, "changed_by_msp_admin_sub" + org_model_prefix },
			{ itd.getMsp_org_1_sub_1(), itd.getMsp_org_1_account_admin_1_email(), password,	"changed_by_msp_account_admin_sub" + org_model_prefix },
			{ itd.getRoot_msp_org_1(), itd.getRoot_msp_org_1_user_1_email(), password,	"changed_by_msp_account_admin_sub" + org_model_prefix },
			{ itd.getRoot_msp_org_1_account_1(), itd.getRoot_msp_org_1_user_1_email(), password,	"changed_by_msp_account_admin_sub" + org_model_prefix },
			{ itd.getRoot_msp_org_1_account_1(), itd.getRoot_msp_org_1_account_admin_1_email(), password,	"changed_by_msp_account_admin_sub" + org_model_prefix },
			{ itd.getRoot_msp_org_1_sub_msp_1(), itd.getRoot_msp_org_1_sub_msp_1_user_1_email(), password,	"changed_by_msp_account_admin_sub" + org_model_prefix },
			{ itd.getRoot_msp_org_1_sub_msp_1_account_1(), itd.getRoot_msp_org_1_sub_msp_1_user_1_email(), password,	"changed_by_msp_account_admin_sub" + org_model_prefix },
			{ itd.getRoot_msp_org_1_sub_msp_1_account_1(), itd.getRoot_msp_org_1_sub_msp_1_account_admin_1_email(), password,	"changed_by_msp_account_admin_sub" + org_model_prefix },
			{ itd.getRoot_msp_org_1_sub_msp_1_account_1(), itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1_email(), password,	"changed_by_msp_account_admin_sub" + org_model_prefix },
		};
	
		}
	
	@Test(dataProvider = "validorganizationInfo")
	// csr admin: update organization for direct/msp
	public void validupdateOrgByID(String orgID, String username, String password, String newName)
			{
		test.assignAuthor("Zhaoguo.Ma");
		spogServer.userLogin(username, password);
		test = rep.startTest("updateOrgByID");
		spogServer.updateOrganizationInfoByID(orgID, newName, test);
		String orgName = spogServer.getOrganizationNameByID(orgID);
		
		if (orgName.equalsIgnoreCase(newName)) {
			test.log(LogStatus.INFO, "compare organization name " + newName);
			assertTrue("compare organization name " + newName + "passed", true);
			test.log(LogStatus.PASS, "compare organization name " + newName + " passed");
		} else {
			test.log(LogStatus.INFO, "compare organization name " + newName);
			assertTrue("compare organization name " + newName + "failed", false);
			test.log(LogStatus.FAIL, "compare organization name " + newName + " failed");
		}
	}
	
	@DataProvider(name = "invalidorganizationInfo")
	public final Object[][] getinvalidOrganizationInfo() {
		return new Object[][] {
			// msp account admin cannot update other organizations;
			{ itd.getMsp_org_1(), itd.getMsp_org_1_account_admin_1_email(), password, "changed_by_different_msp_admin", 403, "00100101" },
			{ itd.getDirect_org_1(), itd.getMsp_org_1_account_admin_1_email(), password, "changed_by_different_msp_admin", 403, "00100101"},
			{ itd.getMsp_org_1_sub_2(), itd.getMsp_org_1_account_admin_1_email(), password, "changed_by_different_msp_admin", 403, "00100101"},
			{ itd.getMsp_org_1_sub_2(), itd.getMsp_org_1_account_admin_1_email(), password, "changed_by_different_msp_admin", 403, "00100101" },
				// msp admin cannot update other msp organization;
				{ itd.getMsp_org_1(), itd.getMsp_org_2_user_1_email(), password, "changed_by_different_msp_admin", 403, "00100101" },
				// direct admin cannot update msp organization;
				{ itd.getMsp_org_1(), itd.getDirect_org_1_user_1_email(), password, "changed_by_direct_admin", 403, "00100101" },
				// msp admin cannot update direct organization;
				{ itd.getDirect_org_1(), itd.getMsp_org_1_user_1_email(), password, "changed_by_admin_msp", 403, "00100101" },
				// direct admin cannot update other direct organization;
				{ itd.getDirect_org_1(), itd.getDirect_org_2_user_1_email(), password, "changed_by_different_direct_admin", 403, "00100101" },
				// msp admin cannot update sub organization in other msp;
				{ itd.getMsp_org_2_sub_1(), itd.getMsp_org_1_user_1_email(), password, "changed_by_different_direct_admin", 403, "00100101" },
				// direct admin cannot update sub organization;
				{ itd.getMsp_org_1_sub_2(), itd.getDirect_org_1_user_1_email(), password, "changed_by_different_direct_admin", 403, "00100101" },
				// sub organization admin cannot update its msp organization;
				{ itd.getMsp_org_1(), itd.getMsp_org_1_sub_1_user_1_email(), password, "changed_by_different_direct_admin", 403, "00100101" },
				// sub organization admin cannot update other msp organization;
				{ itd.getMsp_org_2(), itd.getMsp_org_1_sub_1_user_1_email(), password, "changed_by_different_direct_admin", 403, "00100101" },
				// sub organization admin cannot update direct organization;
				{ itd.getDirect_org_1(), itd.getMsp_org_1_sub_1_user_1_email(), password, "changed_by_different_direct_admin", 403, "00100101" },
				// sub organization admin cannot update other sub organization in same msp;
				{ itd.getMsp_org_1_sub_2(), itd.getMsp_org_1_sub_1_user_1_email(), password, "changed_by_different_direct_admin", 403, "00100101" },
				// sub organization admin cannot update other sub organization in different msp;
				{ itd.getMsp_org_1_sub_2(), itd.getMsp_org_1_sub_1_user_1_email(), password, "changed_by_different_direct_admin", 403, "00100101" },
				// invalid uuid - by csr;
				{ "e5d22d93-9b54-43a4-a2cc-2280f293207b", csrAdmin, csrPwd, "changed_by_csr_msp", 404, "0030000A"},
				// invalid uuid - by direct admin;
				{ "e5d22d93-9b54-43a4-a2cc-2280f293207b", itd.getDirect_org_1_user_1_email(), password,
						"changed_by_direct_admin", 404, "0030000A" },
				// invalid uuid - by msp admin;
				{ "e5d22d93-9b54-43a4-a2cc-2280f29320b7", itd.getMsp_org_1_user_1_email(), password, "changed_by_msp_admin",	404, "0030000A" },
				// invalid uuid - by sub org admin;
				{ "e5d22d93-9b54-43a4-a2cc-2280f293207b", itd.getMsp_org_1_sub_1_user_1_email(), password, "changed_by_sub_admin", 404, "0030000A" },
				// deleted organization - by csr;
				{ initial_deleted_orgID, csrAdmin, csrPwd, "changed_by_csr_msp", 404, "0030000A" },
				// deleted organization - by direct admin;
				{ initial_deleted_orgID, itd.getDirect_org_1_user_1_email(), password, "changed_by_direct_admin", 404, "0030000A" },
				// deleted organization - by msp admin;
				{ initial_deleted_orgID, itd.getMsp_org_1_user_1_email(), password, "changed_by_msp_admin", 404, "0030000A" },
				// deleted organization - by sub org admin;
				{ initial_deleted_orgID, itd.getMsp_org_1_sub_1_user_1_email(), password, "changed_by_sub_admin", 404, "0030000A" }, 
				
				// root msp related
				{ itd.getRoot_msp_org_1_sub_msp_1(), itd.getRoot_msp_org_1_user_1_email(), password, "changed_by_different_msp_admin", 403, "00100101" },
				{ itd.getRoot_msp_org_1_sub_msp_1_account_1(), itd.getRoot_msp_org_1_user_1_email(), password, "changed_by_different_msp_admin", 403, "00100101" },
				{ itd.getRoot_msp_org_1_account_1(), itd.getRoot_msp_org_1_sub_msp_1_user_1_email(), password, "changed_by_different_msp_admin", 403, "00100101" },
				{ itd.getRoot_msp_org_1_sub_msp_1(), itd.getRoot_msp_org_1_account_1_user_1_email(), password, "changed_by_different_msp_admin", 403, "00100101" },
				{ itd.getRoot_msp_org_1_account_2(), itd.getRoot_msp_org_1_account_1_user_1_email(), password, "changed_by_different_msp_admin", 403, "00100101" },
				{ itd.getRoot_msp_org_1_sub_msp_2(), itd.getRoot_msp_org_1_sub_msp_1_user_1_email(), password, "changed_by_different_msp_admin", 403, "00100101" },
				{ itd.getRoot_msp_org_1_sub_msp_1_account_1(), itd.getRoot_msp_org_1_sub_msp_2_user_1_email(), password, "changed_by_different_msp_admin", 403, "00100101" },
				{ itd.getRoot_msp_org_1_sub_msp_1_account_2(), itd.getRoot_msp_org_1_sub_msp_1_account_admin_1_email(), password, "changed_by_different_msp_admin", 403, "00100101" },
				{ itd.getRoot_msp_org_1_sub_msp_1_account_2(), itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1_email(), password, "changed_by_different_msp_admin", 403, "00100101" },
				{ itd.getRoot_msp_org_1_sub_msp_2_account_1(), itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1_email(), password, "changed_by_different_msp_admin", 403, "00100101" },
		};
	}

	@Test(dataProvider = "invalidorganizationInfo")
	// csr admin: update organization for direct/msp
	public void invalidupdateOrgByID(String orgID, String username, String password, String newName, int expectedStatusCode, String errorCode)
			{
		test.assignAuthor("Zhaoguo.Ma");
		spogServer.userLogin(username, password);
		test = rep.startTest("updateOrgByID");
		spogServer.updateOrganizationInfoByIDWithErrorCheck(orgID, newName, expectedStatusCode, errorCode, test);
	}
	
	
	@DataProvider(name = "withouttoken_organizationInfo")
	public final Object[][] withouttoken_validOrganizationInfo(){
		return new Object[][] {{itd.getMsp_org_1(), csrAdmin, csrPwd, "changed_by_csr_msp", 401, "00900006"},
								{itd.getDirect_org_1(), csrAdmin, csrPwd, "changed_by_csr_direct", 401, "00900006"},
								{itd.getMsp_org_1(), itd.getMsp_org_1_user_1_email(), password, "changed_by_admin_msp", 401, "00900006"},
								{itd.getDirect_org_1(), itd.getDirect_org_1_user_1_email(), password, "changed_by_admin_direct", 401, "00900006"}};
			
		}
	
	@Test(dataProvider = "withouttoken_organizationInfo")
	// csr admin: update organization for direct/msp
	public void withouttoken_updateOrgByID(String orgID, String username, String password, String newName, int expectedStatusCode, String errorCode)
			{
		test.assignAuthor("Zhaoguo.Ma");
		spogServer.userLogin(username, password);
		test = rep.startTest("updateOrgByID");
		spogServer.setToken("");
		spogServer.updateOrganizationInfoByIDWithErrorCheck(orgID, newName, expectedStatusCode, errorCode, test);
		
	}
	
	@DataProvider(name = "update_block_status")
	public final Object[][] updateBlockStatusParams(){
		return new Object[][] {
			{itd.getDirect_org_1_user_1_email(), password, itd.getDirect_org_1()},
			{itd.getMsp_org_1_user_1_email(), password, itd.getMsp_org_1()},
			{itd.getMsp_org_1_sub_1_user_1_email(), password, itd.getMsp_org_1_sub_1()}
			};
		}
	
	@Test (dataProvider = "update_block_status", priority = 1000)
	public void updateBlockStatus(String username, String password, String organizationID) {
		
		spogServer.userLogin(username, password);
		// only csr admin can block organization;
		spogServer.updateOrganizationInfoByID(organizationID, "orgName_updated" + org_model_prefix, "true", test);
		spogServer.userLogin(csrAdmin, csrPwd);
		spogServer.updateOrganizationInfoByID(organizationID, "orgName_updated" + org_model_prefix, "true", test);
		// cannot login organization after it is blocked;
		spogServer.userLoginWithErrorCheck(username, password, 401, "00300019", test);
		
		spogServer.userLogin(csrAdmin, csrPwd);
		spogServer.updateOrganizationInfoByID(organizationID, "orgName_updated_2nd" + org_model_prefix, "false", test);
		
		// invalid block status;
		spogServer.updateOrganizationInfoByIDWithErrorCheck(organizationID, "orgName_updated_3rd" + org_model_prefix, "bool", 400, "00100001", test);
		
		// user can login organization after it is unblocked;
		spogServer.userLogin(username, password);
	}
	
	@DataProvider(name = "csr_readonly_params")
	public final Object[][] getFilters_csr_readonly() {
		return new Object[][] {
			{this.itd.getDirect_org_1()},
			{this.itd.getMsp_org_1()},
			{this.itd.getMsp_org_1_sub_1()},
		};}
	
	@Test(dataProvider = "csr_readonly_params")
	public void csrReadonlyTest(String orgID) {
		spogServer.userLogin(csr_readonly_email, csr_readonly_password);
		spogServer.updateOrganizationInfoByIDWithErrorCheck(orgID, "orgName_updated_3rd" + org_model_prefix, "bool", 403, "00100101", test);
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
