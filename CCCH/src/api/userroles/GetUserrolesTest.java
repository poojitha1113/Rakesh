package api.userroles;

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
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.velocity.runtime.directive.Foreach;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import Constants.SpogConstants;

import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;

public class GetUserrolesTest extends base.prepare.Is4Org{

	private SPOGServer spogServer;
	private UserSpogServer userSpogServer;
	private String csrAdmin;
	private String csrPwd;
	
//	private ExtentReports rep;
	private ExtentTest test;

	private String csr_readonly_email = "zhaoguo.ma+csrreadonly@gmail.com";
	private String csr_readonly_password = "Zetta1234";

	private String password = "Pa$$w0rd";
	private InitialTestDataImpl initialTestDataImpl;
	private InitialTestData itd;

//	private SQLServerDb bqdb1;
//	public int Nooftest;
//	private long creationTime;
//	private String BQName = null;
//	private String runningMachine;
//	private testcasescount count1;
//	private String buildVersion;
	
	private String[] csr_admin_role_list = null;
	private String[] csr_readonly_role_list = null;
	private String[] direct_admin_role_list  = null;
	private String[] msp_admin_role_list = null;
	private String[] msp_account_admin_role_list = null;
	
	private String  org_model_prefix=this.getClass().getSimpleName();
	

	@BeforeClass
	@Parameters({ "baseURI", "port", "csrAdminUserName", "csrAdminPassword", "logFolder", "runningMachine",
			"buildVersion" })
	public void beforeClass(String baseURI, String port, String csrAdminUserName, String csrAdminPassword,
			String logFolder, String runningMachine, String buildVersion) {
		rep = ExtentManager.getInstance("GetUserrolesTest", logFolder);
		test = rep.startTest("initializing data...");

		this.BQName = this.getClass().getSimpleName();
		String author = "Zhaoguo.Ma";
		this.runningMachine = runningMachine;
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date = new java.util.Date();
		this.buildVersion = buildVersion + "_" + dateFormater.format(date);
		Nooftest = 0;
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		if (count1.isstarttimehit() == 0) {
			System.out.println("Into get loggedInUserById");
			creationTime = System.currentTimeMillis();
			count1.setcreationtime(creationTime);
			// creationTime = System.currentTimeMillis();
			try {
				bqdb1.updateTable(BQName, runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0",
						String.valueOf(Nooftest), count1.getcreationtime(), "InProgress", author + " and Rest server is "+baseURI.split("//")[1]);
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
	
		csr_admin_role_list = new String[] {"direct_admin", "msp_admin", "csr_admin", "msp_account_admin", "csr_read_only", "msp_monitor", "direct_monitor"};
		csr_readonly_role_list = new String[] {"direct_admin", "msp_admin", "csr_admin", "msp_account_admin", "csr_read_only", "msp_monitor", "direct_monitor"};
		direct_admin_role_list  = new String[]{"direct_admin", "direct_monitor"};
		msp_account_admin_role_list = new String[] {};
		msp_admin_role_list = new String[] {"msp_admin", "msp_account_admin", "msp_monitor"};
	}

	@DataProvider(name = "basicUserroleInfo")
	public final Object[][] getvalidUserRoleInfo() {
		return new Object[][] { 
				{ this.csrAdmin, this.csrPwd, this.csr_admin_role_list,"","",-1,-1 }, 
				{ this.csr_readonly_email, this.csr_readonly_password, this.csr_readonly_role_list, "", "", -1, -1}, 
				{ itd.getMsp_org_1_user_1_email(), password, this.msp_admin_role_list,"","",-1,-1},
				{ itd.getMsp_org_1_account_admin_1(), password, this.msp_account_admin_role_list,"","",-1,-1},
				{ itd.getDirect_org_1_user_1_email(), password, this.direct_admin_role_list,"","",-1,-1 }, 
				{ itd.getMsp_org_1_sub_1_user_1_email(), password, this.direct_admin_role_list,"","",-1,-1 },
				{ itd.getRoot_msp_org_1_account_1_user_1_email(), password, this.direct_admin_role_list,"","",-1,-1 },
				{ itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1_email(), password, this.direct_admin_role_list,"","",-1,-1 },
				{ itd.getRoot_msp_org_1_user_1_email(), password, this.msp_admin_role_list,"","",-1,-1},
				{ itd.getRoot_msp_org_1_account_admin_1(), password, this.msp_account_admin_role_list,"","",-1,-1},
				{ itd.getRoot_msp_org_1_sub_msp_1_account_admin_1(), password, this.msp_account_admin_role_list,"","",-1,-1},

		};
	}

	@Test(dataProvider = "basicUserroleInfo")
	public void checkBasicuserRoleInfo(String username, String password, String[] roles,
			String filterStr, String sortStr, int page, int pageSize) {
		spogServer.userLogin(username, password);
		test = rep.startTest("getUsersroles");
		Response response = spogServer.getUserroles(filterStr, sortStr, page, pageSize, test);
		 
		ArrayList<String> actualRoleList= response.then().extract().path("data.role_id");
		int length = actualRoleList.size();
		if (length != roles.length) {
			
			test.log(LogStatus.INFO, "roles do not match");
			test.log(LogStatus.INFO, "actual roles are: ");
			for (String role : actualRoleList) {
				test.log(LogStatus.INFO, role);
			}
			test.log(LogStatus.INFO, "expected roles are: ");
			for (String role : roles) {
				test.log(LogStatus.INFO, role);
			}

			assertTrue("compare role info failed", false);
		}
		
		if (actualRoleList.containsAll(new ArrayList<String> (Arrays.asList(roles)))) {
			assertTrue("compare role info passed", true);
		} else {
		
			test.log(LogStatus.INFO, "roles do not match");
			test.log(LogStatus.INFO, "actual roles are: ");
			for (String role : actualRoleList) {
				test.log(LogStatus.INFO, role);
			}
			test.log(LogStatus.INFO, "expected roles are: ");
			for (String role : roles) {
				test.log(LogStatus.INFO, role);
			}
				assertTrue("compare role info failed", false);
		}
	}
	
	@DataProvider(name = "basicUserroleInfonoToken")
	public final Object[][] noTokenUserRoleInfo() {
		return new Object[][] { { this.csrAdmin, this.csrPwd, this.csr_admin_role_list, "", "", -1, -1, 401 } };
	}

	@Test(dataProvider = "basicUserroleInfonoToken")
	public void checkBasicuserRoleInfonoToken(String username, String password,
			String[] roles, String filterStr, String sortStr, int page,
			int pageSize, int expected_stauts_code) {
		spogServer.userLogin(username, password);
		test = rep.startTest("getUsersroles");
		spogServer.setToken("");
		spogServer.getUserrolesWithExpectedStatus(filterStr, sortStr, page, pageSize, expected_stauts_code, test);
	}
	
	@DataProvider(name = "invalidpaginationInfo")
	public final Object[][] invalidpaginationInfo(){
		return new Object[][] {{this.csrAdmin, this.csrPwd, this.csr_admin_role_list, "", "", 2, 50}};
	}
	
	@Test(dataProvider = "invalidpaginationInfo")
	public void invalidPaginationTest(String username, String password, String[] roles,	String filterStr, String sortStr, int page, int pageSize) {
		spogServer.userLogin(username, password);
		test = rep.startTest("getUsersroles");
		Response response = spogServer.getUserroles(filterStr, sortStr, page, pageSize, test);
		ArrayList<HashMap> actualRoleList = new ArrayList<HashMap>();
		actualRoleList= response.then().extract().path("data.role_id");
		int length = actualRoleList.size();
		
		if (length ==0) {
			test.log(LogStatus.INFO, "the returned role is 0 as expected");
			assertTrue("the returned role is 0 as expected", true);
		} else {
			assertTrue("the returned role is not 0: " + length, false);
		}
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
