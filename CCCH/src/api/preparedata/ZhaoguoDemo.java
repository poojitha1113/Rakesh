package api.preparedata;

import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.deser.Deserializers.Base;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.SpogConstants;
import InvokerServer.Org4SPOGServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import bsh.org.objectweb.asm.Constants;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import groovyjarjarantlr.PreservingFileWriter;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;

import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import Constants.SpogConstants;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;

public class ZhaoguoDemo extends base.prepare.Is4Org {

	private SPOGServer spogServer;
	private UserSpogServer userSpogServer;
	
	
	private String csrAdmin;
	private String csrPwd;
	private String password = "Pa$$w0rd";
	private String org_model_prefix=this.getClass().getSimpleName();

//	private ExtentReports rep;
	private ExtentTest test;
//	private SQLServerDb bqdb1;
//	public int Nooftest;
//	private long creationTime;
//	private String BQName = null;
//	private String runningMachine;
//	private testcasescount count1;
//	private String buildVersion;
	private String uuid1 = UUID.randomUUID().toString();
	private String uuid2 = UUID.randomUUID().toString();
	
	private InitialTestDataImpl initialTestDataImpl;
	private InitialTestData itd;
	
	@BeforeClass
	@Parameters({ "baseURI", "port", "csrAdminUserName", "csrAdminPassword", "logFolder", "runningMachine",
			"buildVersion" })
	public void beforeClass(String baseURI, String port, String csrAdminUserName, String csrAdminPassword,
			String logFolder, String runningMachine, String buildVersion) {
		rep = ExtentManager.getInstance("GetSpecificFilterFromSpecificUsersTest", logFolder);
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
				
		String prefix_a = RandomStringUtils.randomAlphanumeric(8) + this.getClass().getSimpleName();		
		initialTestDataImpl = new InitialTestDataImpl(baseURI, port, csrAdminUserName, csrAdminPassword, prefix_a, password);
		itd = initialTestDataImpl.initialize();
	}
	
	/**
	 * 2018-01-04
	 * there is some change during development, that user can get/update filters for other users in same organizations, so remove the invalid params. 
	 * @return
	 */
	@DataProvider(name = "basicfiltersInfo_403")
	public final Object[][] getvalidFiltersInfo_403() {
		return new Object[][] {
				//msp admin cannot get filter for other msp admin;
				{itd.getMsp_org_1_user_1(), itd.getMsp_org_2_user_1_email(), 403},
				//msp admin cannot get filter for other admin in same organization;
				{itd.getMsp_org_1_user_1(), itd.getMsp_org_1_user_2_email(), 200},
				//msp admin cannot get filter for direct admin;
				{itd.getDirect_org_1_user_1(), itd.getMsp_org_2_user_1_email(), 403},
				//msp admin can get filter for its sub organization admin;
				{itd.getMsp_org_1_sub_1_user_1(), itd.getMsp_org_1_user_1_email(), 200},
				//direct admin cannot get filter for other direct admin;
				{itd.getDirect_org_1_user_1(), itd.getDirect_org_2_user_1_email(), 403},
				//direct admin cannot get filter for other admin in same organization;
				{itd.getDirect_org_1_user_1(), itd.getDirect_org_1_user_2_email(), 200},
				//direct admin cannot get filter for msp admin;
				{itd.getMsp_org_1_user_1(), itd.getDirect_org_1_user_2_email(), 403},
				//direct admin cannot get filter for sub org;
				{itd.getMsp_org_1_sub_1_user_1(), itd.getDirect_org_1_user_2_email(), 403},
				//sub org admin cannot get filter for msp admin;
				{itd.getMsp_org_1_user_1(), itd.getMsp_org_1_sub_1_user_1_email(), 403},
				//sub org admin cannot get filter for direct admin;
				{itd.getDirect_org_1_user_1(), itd.getMsp_org_1_sub_1_user_1_email(), 403},
				//sub org admin can get filter for other sub org admin; 
				{itd.getMsp_org_1_sub_1_user_2(), itd.getMsp_org_1_sub_1_user_1_email(), 200},
				//sub org admin cannot get filter for admin in same orgrazation;
				{itd.getMsp_org_1_sub_1_user_2(), itd.getMsp_org_1_sub_2_user_1_email(), 403},
				
				{itd.getRoot_msp_org_1_account_1_user_1(), itd.getRoot_msp_org_1_account_admin_1_email(), 200}, 
				{itd.getRoot_msp_org_1_account_2_user_1(), itd.getRoot_msp_org_1_account_admin_1_email(), 403},
				{itd.getRoot_msp_org_1_account_1_user_1(), itd.getRoot_msp_org_1_account_admin_2_email(), 403},
				{itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1(), itd.getRoot_msp_org_1_sub_msp_1_account_admin_1_email(), 200}, 
				{itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1(), itd.getRoot_msp_org_1_sub_msp_1_account_admin_2_email(), 403},
				{itd.getRoot_msp_org_1_sub_msp_1_account_2_user_1(), itd.getRoot_msp_org_1_sub_msp_1_account_admin_1_email(), 403}, 
		};}
	
	@SuppressWarnings({ "unlikely-arg-type", "unchecked" })
	@Test(dataProvider = "basicfiltersInfo_403")
	public void subvalidfilterTest_403(String user_id, String userName, int status_code) {
		spogServer.userLogin(userName, password);
		spogServer.getFiltersByUserIDWithExpectedcode(user_id, status_code);
	}
	
	@DataProvider(name = "testDTO")
	public final Object[][] testDTO() {
		return new Object[][] {
				//msp admin cannot get filter for other msp admin;
				{itd.getMsp_org_1_user_1_email(), itd.getMsp_org_1_user_1(), itd.getMsp_org_1()},
				{itd.getMsp_org_1_user_2_email(), itd.getMsp_org_1_user_2(), itd.getMsp_org_1()},
				{itd.getMsp_org_2_user_1_email(), itd.getMsp_org_2_user_1(), itd.getMsp_org_2()},
				{itd.getDirect_org_1_user_1_email(), itd.getDirect_org_1_user_1(), itd.getDirect_org_1()},
				{itd.getDirect_org_1_user_2_email(), itd.getDirect_org_1_user_2(), itd.getDirect_org_1()},
				{itd.getDirect_org_2_user_1_email(), itd.getDirect_org_2_user_1(), itd.getDirect_org_2()},
				{itd.getMsp_org_1_account_admin_1_email(), itd.getMsp_org_1_account_admin_1(), itd.getMsp_org_1()},
				{itd.getMsp_org_1_account_admin_2_email(), itd.getMsp_org_1_account_admin_2(), itd.getMsp_org_1()},
				{itd.getMsp_org_1_sub_1_user_1_email(), itd.getMsp_org_1_sub_1_user_1(), itd.getMsp_org_1_sub_1()},
				{itd.getMsp_org_1_sub_1_user_2_email(), itd.getMsp_org_1_sub_1_user_2(), itd.getMsp_org_1_sub_1()},
				
				{itd.getRoot_msp_org_1_user_1_email(), itd.getRoot_msp_org_1_user_1(), itd.getRoot_msp_org_1()},
				{itd.getRoot_msp_org_1_user_2_email(), itd.getRoot_msp_org_1_user_2(), itd.getRoot_msp_org_1()},
				{itd.getRoot_msp_org_1_account_admin_1_email(), itd.getRoot_msp_org_1_account_admin_1(), itd.getRoot_msp_org_1()},
				{itd.getRoot_msp_org_1_account_admin_2_email(), itd.getRoot_msp_org_1_account_admin_2(), itd.getRoot_msp_org_1()},
				
				{itd.getRoot_msp_org_2_user_1_email(), itd.getRoot_msp_org_2_user_1(), itd.getRoot_msp_org_2()},
				{itd.getRoot_msp_org_2_account_admin_1_email(), itd.getRoot_msp_org_2_account_admin_1(), itd.getRoot_msp_org_2()},
				
				{itd.getRoot_msp_org_1_account_1_user_1_email(), itd.getRoot_msp_org_1_account_1_user_1(), itd.getRoot_msp_org_1_account_1()},
				{itd.getRoot_msp_org_1_account_1_user_2_email(), itd.getRoot_msp_org_1_account_1_user_2(), itd.getRoot_msp_org_1_account_1()},
				{itd.getRoot_msp_org_1_account_2_user_1_email(), itd.getRoot_msp_org_1_account_2_user_1(), itd.getRoot_msp_org_1_account_2()},
				
				{itd.getRoot_msp_org_1_sub_msp_1_user_1_email(), itd.getRoot_msp_org_1_sub_msp_1_user_1(), itd.getRoot_msp_org_1_sub_msp_1()},
				{itd.getRoot_msp_org_1_sub_msp_1_user_2_email(), itd.getRoot_msp_org_1_sub_msp_1_user_2(), itd.getRoot_msp_org_1_sub_msp_1()},
				{itd.getRoot_msp_org_1_sub_msp_2_user_1_email(), itd.getRoot_msp_org_1_sub_msp_2_user_1(), itd.getRoot_msp_org_1_sub_msp_2()},
				
				{itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1_email(), itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1(), itd.getRoot_msp_org_1_sub_msp_1_account_1()}, 
				{itd.getRoot_msp_org_1_sub_msp_1_account_1_user_2_email(), itd.getRoot_msp_org_1_sub_msp_1_account_1_user_2(), itd.getRoot_msp_org_1_sub_msp_1_account_1()},
				{itd.getRoot_msp_org_1_sub_msp_1_account_2_user_1_email(), itd.getRoot_msp_org_1_sub_msp_1_account_2_user_1(), itd.getRoot_msp_org_1_sub_msp_1_account_2()},
				
				{itd.getRoot_msp_org_2_account_1_user_1_email(), itd.getRoot_msp_org_2_account_1_user_1(), itd.getRoot_msp_org_2_account_1()},
				{itd.getRoot_msp_org_2_sub_msp_1_user_1_email(), itd.getRoot_msp_org_2_sub_msp_1_user_1(), itd.getRoot_msp_org_2_sub_msp_1()},	
				{itd.getRoot_msp_org_2_sub_msp_1_account_1_user_1_email(), itd.getRoot_msp_org_2_sub_msp_1_account_1_user_1(), itd.getRoot_msp_org_2_sub_msp_1_account_1()},
			
				{itd.getRoot_msp_org_1_sub_msp_1_account_admin_1_email(), itd.getRoot_msp_org_1_sub_msp_1_account_admin_1(), itd.getRoot_msp_org_1_sub_msp_1()},
				{itd.getRoot_msp_org_1_sub_msp_1_account_admin_2_email(), itd.getRoot_msp_org_1_sub_msp_1_account_admin_2(), itd.getRoot_msp_org_1_sub_msp_1()},
				{itd.getRoot_msp_org_2_sub_msp_1_account_admin_1_email(), itd.getRoot_msp_org_2_sub_msp_1_account_admin_1(), itd.getRoot_msp_org_2_sub_msp_1()},
				
		};
	}
	
	@Test(dataProvider = "testDTO")
	public void testDTO(String userEmail, String userId, String orgId) {
		spogServer.userLogin(userEmail, password);
		assertEquals(userId, spogServer.GetLoggedinUser_UserID());
		assertEquals(orgId, spogServer.GetLoggedinUserOrganizationID());
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
