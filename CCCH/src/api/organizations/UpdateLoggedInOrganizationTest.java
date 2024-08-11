package api.organizations;

import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

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

import static org.testng.Assert.assertEquals;
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

public class UpdateLoggedInOrganizationTest extends base.prepare.Is4Org{
	private SPOGServer spogServer;
	private UserSpogServer userSpogServer;
	private String csrAdmin;
	private String csrPwd;
//	private ExtentReports rep;
	private ExtentTest test;
	
	private String csr_readonly_email = "csr_readonly_zhaoguo@arcserve.com";
	private String csr_readonly_password = "Caworld_2017";
//	private SQLServerDb bqdb1;
//	public int Nooftest;
//	private long creationTime;
//	private String BQName = null;
//	private String runningMachine;
//	private testcasescount count1;
//	private String buildVersion;

	private String password = "Pa$$w0rd";
	private InitialTestDataImpl initialTestDataImpl;
	private InitialTestData itd;
	private String  org_model_prefix=this.getClass().getSimpleName();

	@BeforeClass
	@Parameters({ "baseURI", "port", "csrAdminUserName", "csrAdminPassword", "logFolder", "runningMachine",
			"buildVersion" })
	public void beforeClass(String baseURI, String port, String csrAdminUserName, String csrAdminPassword,
			String logFolder, String runningMachine, String buildVersion) {

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
                  bqdb1.updateTable(BQName, runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0", String.valueOf(Nooftest), count1.getcreationtime(), "InProgress",author + " and Rest server is "+baseURI.split("//")[1]);
            } catch (ClientProtocolException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
            } catch (IOException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
            }
        }


		rep = ExtentManager.getInstance("GetUsersFromOrganizationTest", logFolder);
		test = rep.startTest("initializing data...");
		spogServer = new SPOGServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		this.csrAdmin = csrAdminUserName;
		this.csrPwd = csrAdminPassword;
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		String prefix = RandomStringUtils.randomAlphanumeric(8);


		String prefix_a = RandomStringUtils.randomAlphanumeric(8) + this.getClass().getSimpleName();
		initialTestDataImpl = new InitialTestDataImpl(baseURI, port, csrAdminUserName, csrAdminPassword, prefix_a,
				password);
		itd = initialTestDataImpl.initialize();

	}

	@DataProvider(name = "validorganizationInfo")
	public final Object[][] getvalidOrganizationInfo() {
		return new Object[][] { 
				{ itd.getMsp_org_1(), itd.getMsp_org_1_user_1_email(), password, "changed_by_admin_msp" + org_model_prefix },
				{ itd.getDirect_org_1(), itd.getDirect_org_1_user_1_email(), password, "changed_by_admin_direct" + org_model_prefix },
				{ itd.getMsp_org_1_sub_1(), itd.getMsp_org_1_sub_1_user_1_email(), password, "changed_by_sub_admin_direct" + org_model_prefix },
				{ itd.getRoot_msp_org_1(), itd.getRoot_msp_org_1_user_1_email(), password, "changed_by_sub_admin_direct" + org_model_prefix },
				{ itd.getRoot_msp_org_1_account_1(), itd.getRoot_msp_org_1_account_1_user_1_email(), password, "changed_by_sub_admin_direct" + org_model_prefix },
				{ itd.getRoot_msp_org_1_sub_msp_1(), itd.getRoot_msp_org_1_sub_msp_1_user_1_email(), password, "changed_by_sub_admin_direct" + org_model_prefix },
				{ itd.getRoot_msp_org_1_sub_msp_1_account_1(), itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1_email(), password, "changed_by_sub_admin_direct" + org_model_prefix },
		};
	}

	@Test(dataProvider = "validorganizationInfo")
	public void validupdateOrgByID(String orgID, String username, String password, String newName) {
		test.assignAuthor("Zhaoguo.Ma");
		spogServer.userLogin(username, password);
		test = rep.startTest("updateOrgByID");
		spogServer.UpdateLoggedInOrganization(newName + org_model_prefix, test);
		String orgName = spogServer.getOrganizationNameByID(orgID);

		if (orgName.equalsIgnoreCase(newName + org_model_prefix)) {
			test.log(LogStatus.INFO, "compare organization name " + newName);
			assertTrue("compare organization name " + newName + "passed", true);
			test.log(LogStatus.PASS, "compare organization name " + newName + " passed");
		} else {
			test.log(LogStatus.INFO, "compare organization name " + newName);
			assertTrue("compare organization name " + newName + "failed", false);
			test.log(LogStatus.FAIL, "compare organization name " + newName + " failed");
		}
	}

	@DataProvider(name = "withouttoken_organizationInfo")
	public final Object[][] withouttoken_OrganizationInfo() {
		return new Object[][] { { itd.getMsp_org_1(), itd.getMsp_org_1_user_1_email(), password, "changed_by_admin_msp", 401 },
				{ itd.getDirect_org_1(), itd.getDirect_org_1_user_1_email(), password, "changed_by_admin_direct", 401 },
				{ itd.getMsp_org_1_sub_2(), itd.getMsp_org_1_sub_1_user_1_email(), password, "changed_by_sub_admin_direct", 401 } };

	}

	@Test(dataProvider = "withouttoken_organizationInfo")
	// csr admin: update organization for direct/msp
	public void withouttoken_updateOrgByID(String orgID, String username, String password, String newName,
			int expectedStatusCode) {
		test.assignAuthor("Zhaoguo.Ma");
		spogServer.userLogin(username, password);
		test = rep.startTest("updateOrgByID");
		spogServer.setToken("");
		spogServer.UpdateLoggedInOrganizationWithExpectedStatusCode(newName + org_model_prefix, expectedStatusCode, test);

	}
	
	@DataProvider(name = "organization_update_block")
	public final Object[][] updateBlockStatus() {
		return new Object[][] { 
			{itd.getDirect_org_1_user_1_email(), password},
			
			{itd.getMsp_org_1_user_1_email(), password},
			{itd.getMsp_org_1_sub_1_user_1_email(), password},
		};

	}
	@Test (dataProvider = "organization_update_block")
	public void updateBlockStatus(String userName, String password) {
		spogServer.userLogin(userName, password);
		spogServer.UpdateLoggedInOrganization("updated", "true", test);
//		String organizationID = spogServer.GetLoggedinUserOrganizationID();
//		spogServer.userLogin(csrAdmin, csrPwd);
//		assertEquals(false, spogServer.getOrganizationStatusByID(organizationID));
	}
	
	@DataProvider(name = "organization_update_block_csr")
	public final Object[][] updateBlockStatusCSR() {
		return new Object[][] { 
			{csrAdmin, csrPwd, 403, "00300020"},
		};

	}
	@Test (dataProvider = "organization_update_block_csr")
	public void updateBlockStatusCSR(String userName, String password, int status, String error_code) {
		spogServer.userLogin(userName, password);
		spogServer.UpdateLoggedInOrganizationWithExpectedStatusCode("updated", "true", status, error_code, test);
//		String organizationID = spogServer.GetLoggedinUserOrganizationID();
//		spogServer.userLogin(csrAdmin, csrPwd);
//		assertEquals(false, spogServer.getOrganizationStatusByID(organizationID));
	}
	
	@Test
	public void mspAccountAdminTest() {
		spogServer.userLogin(itd.getMsp_org_1_account_admin_1_email(), password);
		spogServer.UpdateLoggedInOrganizationWithExpectedStatusCode("newName", 403, test);
		spogServer.userLogin(itd.getRoot_msp_org_1_account_admin_1_email(), password);
		spogServer.UpdateLoggedInOrganizationWithExpectedStatusCode("newName", 403, test);
		spogServer.userLogin(itd.getRoot_msp_org_1_sub_msp_1_account_admin_1_email(), password);
		spogServer.UpdateLoggedInOrganizationWithExpectedStatusCode("newName", 403, test);
	}
	
	@Test
	public void csrReadonlyTest() {
		spogServer.userLogin(csr_readonly_email, csr_readonly_password);
		spogServer.UpdateLoggedInOrganizationWithExpectedStatusCode("newName", 403, test);
	}

	@AfterMethod
	public void afterMethodTest() {
		rep.endTest(test);
		rep.flush();
	}

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
