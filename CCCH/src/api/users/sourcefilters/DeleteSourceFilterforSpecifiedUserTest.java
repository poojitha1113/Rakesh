package api.users.sourcefilters;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

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

import Constants.ConnectionStatus;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.SPOGServer;
import InvokerServer.Source4SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import invoker.SiteTestHelper;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;

public class DeleteSourceFilterforSpecifiedUserTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private Source4SPOGServer spogSource4SPOGServer;
	private UserSpogServer userSpogServer;

	private ExtentTest test;
	private TestOrgInfo ti;

	// used for test case count like passed,failed,remaining cases
	private String org_model_refix = this.getClass().getSimpleName();
	// private SQLServerDb bqdb1;
	// public int Nooftest;
	int passedcases = 0;
	int failedcases = 0;
	int skippedcases = 0;
	// long creationTime;
	String buildnumber = null;
	// String BQame=null;
	// private testcasescount count1;



	LocalDate date = LocalDate.now();
	LocalDate yesterday = date.minusDays(1);
	LocalDate tomorrow = yesterday.plusDays(2);

	@BeforeClass
	@Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword","csrReadOnlyAdminName","csrReadOnlyAdminPassword","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword,String csrReadOnlyAdminName,String csrReadOnlyAdminPassword, String logFolder,String runningMachine,String buildVersion) throws UnknownHostException {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogSource4SPOGServer = new Source4SPOGServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		rep = ExtentManager.getInstance("DeleteSourceFilterforSpecifiedUserTest", logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Prasad.Deverakonda";

		Nooftest = 0;
		// Used for creating a build number with dateformat
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date = new java.util.Date();
		this.buildVersion = buildVersion + "_" + dateFormater.format(date);

		this.BQName = this.getClass().getSimpleName();
		this.runningMachine = runningMachine;
		System.out.println("The value of hit is " + count1.isstarttimehit());
		ti = new TestOrgInfo(spogServer, test);	
		/*
		if (count1.isstarttimehit() == 0) {
			System.out.println("into creation time");
			creationTime = System.currentTimeMillis();
			count1.setcreationtime(creationTime);

			// creationTime = System.currentTimeMillis();
			try {
				bqdb1.updateTable(BQName, this.runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0",
						String.valueOf(Nooftest), count1.getcreationtime(), "InProgress",
						author + " and Rest server is " + baseURI.split("//")[1]);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
	}


	@DataProvider(name = "organizationAndUserInfo")
	public final Object[][] getOrganizationAndUserInfo() {
		return new Object[][] {

			//Direct Org
			{"Delete Direct User source filter with direct user token",ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },

			//Customer account of Normal msp
			{"Delete Customer account of normal msp source filter with direct user token",ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },
			{"Delete Customer account of normal msp source filter with Normla msp user token",ti.normal_msp1_suborg1_id,ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_user1_id,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },
			{"Delete Customer account of normal msp source filter with Normal msp accountadmin  user token",ti.normal_msp1_suborg1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp1_suborg1_user1_id,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },

			//Customer account of root msp
			{"Delete Customer account of root msp source filter with customer account user token",ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },
			{"Delete Customer account of root msp source filter with root msp user token",ti.root_msp1_suborg1_id,ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_user1_id,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },
			{"Delete Customer account of root msp source filter with root msp account admin user token",ti.root_msp1_suborg1_id,ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp1_suborg1_user1_id,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },

			//Customer account of sub msp
			{"Delete Customer account of sub msp source filter with customer account user token",ti.msp1_submsp1_sub_org1_id,ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_suborg1_user1_id,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },
			{"Delete Customer account of sub msp source filter with  sub msp user token",ti.msp1_submsp1_sub_org1_id,ti.root_msp1_submsp1_user1_token,ti.msp1_submsp1_suborg1_user1_id,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },
			{"Delete Customer account of sub msp source filter with submsp account admin token",ti.msp1_submsp1_sub_org1_id,ti.root_msp1_submsp1_account_admin_token,ti.msp1_submsp1_suborg1_user1_id,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },

		};

	}

	@Test(dataProvider = "organizationAndUserInfo")
	public void deleteSourceFilterForSpecifiedUser(String organizationType,
			String org_Id, 
			String validToken,
			String user_id,
			String filtername) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);
		test.assignAuthor("prasad.deverakonda");

		test.log(LogStatus.INFO, "Create a filter");
		spogServer.setToken(validToken);

		test.log(LogStatus.INFO, "Create source filter for the logged in user of org: " + organizationType);
		String filter_Id = spogServer.createFilterwithCheck(user_id, filtername, ProtectionStatus.protect.name(),ConnectionStatus.online.name(), UUID.randomUUID().toString(), "finished", "emptyarray", "windows",
				"emptyarray", SourceType.machine.name(), "true", test);

		test.log(LogStatus.INFO, "The filter id is " + filter_Id);

		// spogSource4SPOGServer.setToken(validToken);
		test.log(LogStatus.INFO, "Delete the source filter in org: " + organizationType);
		spogServer.deletefilterspecifiedbyUserIdwithCheck(user_id, filter_Id, validToken,SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		test.log(LogStatus.INFO,
				"Delete the source filter in org: " + organizationType + " using invalid(UUID) user ID and token of valid token");
		spogServer.deletefilterspecifiedbyUserIdwithCheck(UUID.randomUUID().toString(),filter_Id, ti.csr_token, SpogConstants.RESOURCE_NOT_EXIST,
				SpogMessageCode.USER_ID_DOESNOT_EXIST, test);

		/*if (organizationType.equals("suborg")) {
			spogServer.setToken(msp_account_admin_validToken);
			user_id = spogServer.GetLoggedinUser_UserID();
			test.log(LogStatus.INFO, "Create source filter for the logged in user of org: " + organizationType);
			filter_Id = spogServer.createFilterwithCheck(user_id, filtername, ProtectionStatus.protect.name(),
					ConnectionStatus.online.name(), UUID.randomUUID().toString(), "finished", "emptyarray", "windows",
					"emptyarray", SourceType.machine.name(), "true", test);

			test.log(LogStatus.INFO, "The filter id is " + filter_Id);

			// spogSource4SPOGServer.setToken(validToken);
			test.log(LogStatus.INFO, "Delete the source filter in org: " + organizationType);
			spogServer.deletefilterspecifiedbyUserIdwithCheck(user_id, filter_Id, validToken,SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		}
		 */
	}


	@DataProvider(name = "DeleteSourcFilter")
	public final Object[][] DeleteSourceFiltersParams() {
		return new Object[][] {

			//Direct Org
			{"Delete Direct User source filter with direct user token",ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },

			//Customer account of Normal msp
			{"Delete Customer account of normal msp source filter with direct user token",ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },

			//Customer account of root msp
			{"Delete Customer account of root msp source filter with customer account user token",ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },

			//Customer account of sub msp
			{"Delete Customer account of sub msp source filter with customer account user token",ti.msp1_submsp1_sub_org1_id,ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_suborg1_user1_id,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },
		};

	}
	@Test(dataProvider = "DeleteSourcFilter")
	public void deletefilterspecifiedbyUserIdwithCheck_401(String organizationType, String org_Id, String validToken,
			String user_id, String filtername) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);
		test.assignAuthor("prasad.deverakonda");

		test.log(LogStatus.INFO, "Create a filter");

		String Random_filterId= UUID.randomUUID().toString();
		// spogSource4SPOGServer.setToken(validToken);
		test.log(LogStatus.INFO, "Delete the source filter in org: " + organizationType + " using invalid JWT");
		spogServer.deletefilterspecifiedbyUserIdwithCheck(user_id, Random_filterId,  validToken + "J",SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

		test.log(LogStatus.INFO, "Delete the source filter in org: " + organizationType + " using missing JWT");
		spogServer.deletefilterspecifiedbyUserIdwithCheck(user_id, Random_filterId,"",SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);

		test.log(LogStatus.INFO,
				"Delete the source filter in org: " + organizationType + " using valid filter ID and token of csr");
		spogServer.deletefilterspecifiedbyUserIdwithCheck(user_id,Random_filterId, validToken, SpogConstants.RESOURCE_NOT_EXIST,
				SpogMessageCode.SOURCE_FILTER_NOT_FOUND_WITH_USER_ID, test);


		test.log(LogStatus.INFO,
				"Delete the source filter in org: " + organizationType + " using invalid user ID and token of valid token");
		spogServer.deletefilterspecifiedbyUserIdwithCheck("123",Random_filterId, ti.csr_token, SpogConstants.REQUIRED_INFO_NOT_EXIST,
				SpogMessageCode.ELEMENT_USER_ID_NOT_UUID, test);


	}


	@DataProvider(name = "sourcefilter_otherorg", parallel = false)
	public final Object[][] sourcefilter_otherorg() {
		return new Object[][] {



			//Direct Org
			{"Delete Direct User source filter with another direct user token",ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id,ti.direct_org2_user2_token,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },
			{"Delete Direct User source filter with normal msp user token",ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id,ti.normal_msp_org1_user1_token,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },
			{"Delete Direct User source filter with normal msp account admin direct user token",ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id,ti.normal_msp_org1_msp_accountadmin1_token,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },
			{"Delete Direct User source filter with  direct user of normal msp token",ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id,ti.normal_msp1_suborg1_user1_token,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },
			{"Delete Direct User source filter with root msp user token",ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id,ti.root_msp_org1_user1_token,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },
			{"Delete Direct User source filter with root msp account admin user token",ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id,ti.root_msp_org1_msp_accountadmin1_token,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },
			{"Delete Direct User source filter with  direct user of root msp token",ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id,ti.root_msp1_suborg1_user1_token,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },
			{"Delete Direct User source filter with sub msp user token",ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id,ti.root_msp1_submsp1_user1_token,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },
			{"Delete Direct User source filter with sub msp account admin user token",ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id,ti.root_msp1_submsp1_account_admin_token,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },
			{"Delete Direct User source filter with another direct user of sub msp token",ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id,ti.msp1_submsp1_suborg1_user1_token,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },

			//Direct Org of normal msp
			{"Delete Direct User of normal msp source filter with another direct user token",ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id,ti.direct_org1_user2_token,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },
			{"Delete Direct User of normal msp source filter with normal msp user token",ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id,ti.normal_msp_org2_user1_token,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },
			{"Delete Direct User of normal msp source filter with normal msp account admin direct user token",ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id,ti.normal_msp_org2_msp_accountadmin1_token,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },
			{"Delete Direct User of normal msp source filter with  direct user of normal msp token",ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id,ti.normal_msp1_suborg2_user1_token,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },
			{"Delete Direct User of normal msp source filter with root msp user token",ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id,ti.root_msp_org1_user1_token,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },
			{"Delete Direct User of normal msp source filter with root msp account admin user token",ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id,ti.root_msp_org1_msp_accountadmin1_token,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },
			{"Delete Direct User of normal msp source filter with  direct user of root msp token",ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id,ti.root_msp1_suborg1_user1_token,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },
			{"Delete Direct User of normal msp source filter with sub msp user token",ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id,ti.root_msp1_submsp1_user1_token,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },
			{"Delete Direct User of normal msp source filter with sub msp account admin user token",ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id,ti.root_msp1_submsp1_account_admin_token,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },
			{"Delete Direct User of normal msp source filter with another direct user of sub msp token",ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id,ti.msp1_submsp1_suborg1_user1_token,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },


			//Customer account of root msp
			{"Delete Direct User of root msp source filter with another direct user token",ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id,ti.direct_org1_user2_token,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },
			{"Delete Direct User of root msp source filter with normal msp user token",ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id,ti.normal_msp_org1_user1_token,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },
			{"Delete Direct User of root msp source filter with normal msp account admin direct user token",ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id,ti.normal_msp_org1_msp_accountadmin1_token,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },
			{"Delete Direct User of root msp source filter with  direct user of normal msp token",ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id,ti.normal_msp1_suborg2_user1_token,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },
			{"Delete Direct User of root msp source filter with root msp user token",ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id,ti.root_msp_org2_user1_token,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },
			{"Delete Direct User of root msp source filter with root msp account admin user token",ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id,ti.root_msp_org2_msp_accountadmin1_token,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },
			{"Delete Direct User of root msp source filter with  direct user of root msp token",ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id,ti.root_msp2_suborg1_user2_token,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },
			{"Delete Direct User of root msp source filter with sub msp user token",ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id,ti.root_msp1_submsp1_user1_token,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },
			{"Delete Direct User of root msp source filter with sub msp account admin user token",ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id,ti.root_msp1_submsp1_account_admin_token,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },
			{"Delete Direct User of root msp source filter with another direct user of sub msp token",ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id,ti.msp1_submsp1_suborg1_user1_token,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },

			//Customer account of sub msp
			//Direct Org of sub msp
			{"Delete Direct User of sub msp source filter with another direct user token",ti.msp1_submsp1_sub_org1_id,ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_suborg1_user1_id,ti.direct_org1_user2_token,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },
			{"Delete Direct User of sub msp source filter with normal msp user token",ti.msp1_submsp1_sub_org1_id,ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_suborg1_user1_id,ti.normal_msp_org1_user1_token,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },
			{"Delete Direct User of sub msp source filter with normal msp account admin direct user token",ti.msp1_submsp1_sub_org1_id,ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_suborg1_user1_id,ti.normal_msp_org1_msp_accountadmin1_token,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },
			{"Delete Direct User of sub msp source filter with  direct user of normal msp token",ti.msp1_submsp1_sub_org1_id,ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_suborg1_user1_id,ti.normal_msp1_suborg2_user1_token,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },
			{"Delete Direct User of sub msp source filter with root msp user token",ti.msp1_submsp1_sub_org1_id,ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_suborg1_user1_id,ti.root_msp_org1_user1_token,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },
			{"Delete Direct User of sub msp source filter with root msp account admin user token",ti.msp1_submsp1_sub_org1_id,ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_suborg1_user1_id,ti.root_msp_org1_msp_accountadmin1_token,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },
			{"Delete Direct User of sub msp source filter with  direct user of root msp token",ti.msp1_submsp1_sub_org1_id,ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_suborg1_user1_id,ti.root_msp1_suborg1_user2_token,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },
			{"Delete Direct User of sub msp source filter with sub msp user token",ti.msp1_submsp1_sub_org1_id,ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_suborg1_user1_id,ti.root_msp1_submsp2_user1_token,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },
			{"Delete Direct User of sub msp source filter with sub msp account admin user token",ti.msp1_submsp1_sub_org1_id,ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_suborg1_user1_id,ti.root_msp2_submsp1_account_admin_token,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },
			{"Delete Direct User of sub msp source filter with another direct user of sub msp token",ti.msp1_submsp1_sub_org1_id,ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_suborg1_user1_id,ti.msp1_submsp1_suborg2_user1_token,RandomStringUtils.randomAlphanumeric(4) + "Test_123" },


		};
	}

	@Test(dataProvider = "sourcefilter_otherorg")
	public void deletefilterspecifiedbyUserIdwithCheck_404_otherorgtoken(String organizationType, 
			String org_Id,
			String validToken,
			String user_Id, 
			String otherorgtoken, 
			String filtername) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);
		test.assignAuthor("prasad.deverakonda");

		test.log(LogStatus.INFO, "Create a filter");
		spogServer.setToken(validToken);

		test.log(LogStatus.INFO, "Create source filter for the logged in user of org: " + organizationType);
		String filter_Id = spogServer.createFilterwithCheck(user_Id, filtername, ProtectionStatus.protect.name(),ConnectionStatus.online.name(), UUID.randomUUID().toString(), "finished", "emptyarray", "windows",
				"emptyarray", SourceType.machine.name(), "true", test);

		test.log(LogStatus.INFO, "Delete the source filter in org: " + organizationType + " using other org token");
		spogServer.deletefilterspecifiedbyUserIdwithCheck(user_Id,filter_Id, otherorgtoken,
				SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY, test);
		
		
		test.log(LogStatus.INFO, "Delete the source filter in org: " + organizationType + " using other org token");
		spogServer.deletefilterspecifiedbyUserIdwithCheck(user_Id,filter_Id, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE,null, test);
		

	}



	@AfterMethod
	public void getResult(ITestResult result) {
		if (result.getStatus() == ITestResult.FAILURE) {
			count1.setfailedcount();
			test.log(LogStatus.FAIL, "Test Case Failed is " + result.getName() + " with parameters as "
					+ Arrays.asList(result.getParameters()));
			test.log(LogStatus.FAIL, result.getThrowable().getMessage());

		} else if (result.getStatus() == ITestResult.SKIP) {
			test.log(LogStatus.SKIP, "Test Case Skipped is " + result.getName());
			count1.setskippedcount();
		} else if (result.getStatus() == ITestResult.SUCCESS) {
			count1.setpassedcount();
		}

		rep.endTest(test);
		rep.flush();
	}
}
