package api.users;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.SPOGServer;
import base.prepare.TestOrgInfo;
import InvokerServer.Org4SPOGServer;
import bsh.org.objectweb.asm.Constants;
import dataPreparation.JsonPreparation;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import groovyjarjarantlr.PreservingFileWriter;
import io.restassured.response.Response;

import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
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
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;


	 
public class ChangePasswordForLoggedInUserTest  extends base.prepare.Is4Org {

	private SPOGServer spogServer;
	private JsonPreparation jp;
	private TestOrgInfo ti;
	private ExtentTest test;
	protected ExtentReports rep;

	int passedcases = 0;
	int failedcases = 0;
	int skippedcases = 0;


	String csr_token, csr_org_id;
	String prefix = RandomStringUtils.randomAlphabetic(4);
	String userEmail =prefix+"eswari.sykam100@gmail.com";
	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine,
			String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		jp = new JsonPreparation();
		rep = ExtentManager.getInstance(getClass().getName(), logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Sykam, Naga Malleswari";
		ti = new TestOrgInfo(spogServer, test);
		Nooftest = 0;
		// Used for creating a build number with dateformat
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date = new java.util.Date();
		this.buildVersion = buildVersion + "_" + dateFormater.format(date);

		BQName = this.getClass().getSimpleName();
		this.runningMachine = runningMachine;
		System.out.println("The value of hit is " + count1.isstarttimehit());

		ti = new TestOrgInfo(spogServer, test);


		/*if (count1.isstarttimehit() == 0) {
			System.out.println("into creation time");
			creationTime = System.currentTimeMillis();
			count1.setcreationtime(creationTime);

			try {
				bqdb1.updateTable(BQName, this.runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0",
						String.valueOf(Nooftest), count1.getcreationtime(), "InProgress",
						author + " and Rest server is " + baseURI.split("//")[1]);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/

	}



	@DataProvider(name = "ChangePassword")
	public final Object[][]getOrgInfoParams() {

		return new Object[][] {
			//200 Scenarios
			{ "Change Password for direct organization user with valid token ",ti.direct_org1_user1_token,ti.common_password,"Mclaren@2020",SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{ "Change Password for normal msp user token ",ti.normal_msp_org1_user1_token,ti.common_password,"Mclaren@2020",SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{ "Change Password for normal msp accoutn admin user  ",ti.normal_msp_org1_msp_accountadmin1_token,ti.common_password,"Mclaren@2020",SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{ "Change Password for customer account of normal msp   ",ti.normal_msp1_suborg1_user1_token,ti.common_password,"Mclaren@2020",SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{ "Change Password for root msp user ",ti.root_msp_org1_user1_token,ti.common_password,"Mclaren@2020",SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{ "Change Password for root msp account admin user ",ti.root_msp_org1_msp_accountadmin1_token,ti.common_password,"Mclaren@2020",SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{ "Change Password for customer account of root msp  ",ti.root_msp1_suborg1_user1_token,ti.common_password,"Mclaren@2020",SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{ "Change Password for submsp user token ",ti.root_msp1_submsp1_user1_token,ti.common_password,"Mclaren@2020",SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{ "Change Password for sub msp account admin user ",ti.root_msp1_submsp1_account_admin_token,ti.common_password,"Mclaren@2020",SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{ "Change Password for customer account of root msp  ",ti.msp1_submsp1_suborg1_user1_token,ti.common_password,"Mclaren@2020",SpogConstants.SUCCESS_GET_PUT_DELETE,null},

		};
	}

	@Test(dataProvider = "ChangePassword", enabled = true)
	public void getOrganizationDetailsForLoggedInUser(String testCase,
			String token,
			String oldPassword,
			String newPassword,
			int expectedstatuccode,
			SpogMessageCode errormessage
			) {

		test = ExtentManager.getNewTest(testCase);

		test.log(LogStatus.INFO, "Compose info for create soucre group");
		HashMap<String, Object> composeOrgInfo = jp.updatepasswordInfo(oldPassword, newPassword);

		spogServer.setToken(token);
		test.log(LogStatus.INFO, "Get OrganizationInfo For LoggedInuser");
		spogServer.changePasswordForLoggedinUserwithCheck(token,composeOrgInfo, expectedstatuccode, errormessage, test);

	}


	@DataProvider(name = "ChangePassword_invalid")
	public final Object[][]chnagePasswordInfo() {

		return new Object[][] {
			//200 Scenarios
			{ "Change Password for direct organization user with valid token ",ti.direct_org1_user1_token,ti.common_password,"Mclaren@2020",SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{ "Change Password for customer account of normal msp   ",ti.normal_msp1_suborg1_user1_token,ti.common_password,"Mclaren@2020",SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{ "Change Password for customer account of root msp  ",ti.root_msp1_suborg1_user1_token,ti.common_password,"Mclaren@2020",SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{ "Change Password for customer account of root msp  ",ti.msp1_submsp1_suborg1_user1_token,ti.common_password,"Mclaren@2020",SpogConstants.SUCCESS_GET_PUT_DELETE,null},

		};
	}

	@Test(dataProvider = "ChangePassword_invalid", enabled = true)
	public void changepasswordfortheuser_invalidSecnarioes(String testCase,
			String token,
			String oldPassword,
			String newPassword,
			int expectedstatuccode,
			SpogMessageCode errormessage
			) {

		test = ExtentManager.getNewTest(testCase);

		test.log(LogStatus.INFO, "Compose info for change user passwrod ");
		HashMap<String, Object> composeOrgInfo = jp.updatepasswordInfo(oldPassword, newPassword);

		spogServer.setToken("");
		test.log(LogStatus.INFO, "Change user password for user with the no token");
		spogServer.changePasswordForLoggedinUserwithCheck("",composeOrgInfo,SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);

		spogServer.setToken("Invalid");
		test.log(LogStatus.INFO, "Change user password for user with the invalid token");
		spogServer.changePasswordForLoggedinUserwithCheck("Invalid",composeOrgInfo,SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

	}


	@AfterMethod
	public void getResult(ITestResult result) {
		if (result.getStatus() == ITestResult.FAILURE) {
			count1.setfailedcount();
			// remaincases=Nooftest-passedcases-failedcases;
			test.log(LogStatus.FAIL, "Test Case Failed is " + result.getName() + " with parameters as "
					+ Arrays.asList(result.getParameters()));
			test.log(LogStatus.FAIL, result.getThrowable().getMessage());

		} else if (result.getStatus() == ITestResult.SKIP) {
			test.log(LogStatus.SKIP, "Test Case Skipped is " + result.getName());
			count1.setskippedcount();
		} else if (result.getStatus() == ITestResult.SUCCESS) {
			count1.setpassedcount();
			// remaincases=Nooftest-passedcases-failedcases;

		}
		rep.endTest(test);

	}

}