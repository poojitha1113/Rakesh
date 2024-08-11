package api.users.sourcefilters;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.ConnectionStatus;
import Constants.ProtectionStatus;
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

public class DeletesourceFilterForLoggedInUserTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private Source4SPOGServer spogSource4SPOGServer;
	private UserSpogServer userSpogServer;
	private TestOrgInfo ti;

	private ExtentTest test;


	private String site_version = "1.0.0";
	private String gateway_hostname = "ramesh";
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
	@Parameters({ "baseURI", "port", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine,
			String buildVersion) {


		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogSource4SPOGServer = new Source4SPOGServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		rep = ExtentManager.getInstance("DeletesourceFilterForLoggedInUserTest", logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Ramesh.Pendurthy";

		Nooftest = 0;
		
		// Used for creating a build number with dateformat
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date = new java.util.Date();
		this.buildVersion = buildVersion + "_" + dateFormater.format(date);

		this.BQName = this.getClass().getSimpleName();
		this.runningMachine = runningMachine;
		System.out.println("The value of hit is " + count1.isstarttimehit());
		
		ti = new TestOrgInfo(spogServer, test);
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
			}
		}
	}
		

	@DataProvider(name = "organizationAndUserInfo")
	public final Object[][] getOrganizationAndUserInfo() {
		return new Object[][] {
				{ "direct",ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id,
						RandomStringUtils.randomAlphanumeric(4) + "ramesh-test12" },
				{ "msp", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,
						RandomStringUtils.randomAlphanumeric(4) + "ramesh-test4" },
				{ "suborg", ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id,
						RandomStringUtils.randomAlphanumeric(4) + "ramesh-test4" },
				{ "csrreadonly", ti.csr_org_id , ti.csr_readonly_token, ti.csr_readonly_admin_user_id, 
						RandomStringUtils.randomAlphanumeric(4) + "ramesh-test4" }, };
	}

	@Test(dataProvider = "organizationAndUserInfo")
	public void deleteSourceFilterForLoggedInUser(String organizationType, String org_Id, String validToken,
			String user_id, String filtername) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);
		test.assignAuthor("ramesh.pendurthy");

		test.log(LogStatus.INFO, "Create a filter");
		spogServer.setToken(validToken);

		test.log(LogStatus.INFO, "Create source filter for the logged in user of org: " + organizationType);
		String filter_Id = spogServer.createFilterwithCheck(user_id, filtername, ProtectionStatus.protect.name(),
				ConnectionStatus.online.name(), UUID.randomUUID().toString(), "finished", "emptyarray", "windows",
				"emptyarray", SourceType.machine.name(), "true", test);

		test.log(LogStatus.INFO, "The filter id is " + filter_Id);

		// spogSource4SPOGServer.setToken(validToken);
		test.log(LogStatus.INFO, "Delete the source filter in org: " + organizationType);
		spogSource4SPOGServer.deletesourcefilterforLoggedInuser(filter_Id, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		if (organizationType.equals("suborg")) {
			spogServer.setToken(ti.normal_msp_org1_msp_accountadmin1_token);
			user_id = spogServer.GetLoggedinUser_UserID();
			test.log(LogStatus.INFO, "Create source filter for the logged in user of org: " + organizationType);
			filter_Id = spogServer.createFilterwithCheck(user_id, filtername, ProtectionStatus.protect.name(),
					ConnectionStatus.online.name(), UUID.randomUUID().toString(), "finished", "emptyarray", "windows",
					"emptyarray", SourceType.machine.name(), "true", test);

			test.log(LogStatus.INFO, "The filter id is " + filter_Id);

			// spogSource4SPOGServer.setToken(validToken);
			test.log(LogStatus.INFO, "Delete the source filter in org: " + organizationType);
			spogSource4SPOGServer.deletesourcefilterforLoggedInuser(filter_Id, ti.normal_msp_org1_msp_accountadmin1_token,
					SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		}

	}

	@Test(dataProvider = "organizationAndUserInfo")
	public void deleteSourceFilterForLoggedInUser_401(String organizationType, String org_Id, String validToken,
			String user_id, String filtername) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);
		test.assignAuthor("prasad.deverakonda");

		test.log(LogStatus.INFO, "Create a filter");
		spogServer.setToken(validToken);

		test.log(LogStatus.INFO, "Create source filter for the logged in user of org: " + organizationType);
		String filter_Id = spogServer.createFilterwithCheck(user_id, filtername, ProtectionStatus.protect.name(),
				ConnectionStatus.online.name(), UUID.randomUUID().toString(), "finished", "emptyarray", "windows",
				"emptyarray", SourceType.machine.name(), "true", test);

		test.log(LogStatus.INFO, "The filter id is " + filter_Id);

		// spogSource4SPOGServer.setToken(validToken);
		test.log(LogStatus.INFO, "Delete the source filter in org: " + organizationType + " using invalid JWT");
		spogSource4SPOGServer.deletesourcefilterforLoggedInuser(filter_Id, validToken + "J",
				SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

		test.log(LogStatus.INFO, "Delete the source filter in org: " + organizationType + " using missing JWT");
		spogSource4SPOGServer.deletesourcefilterforLoggedInuser(filter_Id, "", SpogConstants.NOT_LOGGED_IN,
				SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);

		test.log(LogStatus.INFO, "Delete the source filter in org: " + organizationType);
		spogSource4SPOGServer.deletesourcefilterforLoggedInuser(filter_Id, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

	}

	@Test(dataProvider = "organizationAndUserInfo")
	public void deleteSourceFilterForLoggedInUser_404_doesnotexist(String organizationType, String org_Id,
			String validToken, String user_id, String filtername) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);
		test.assignAuthor("prasad.deverakonda");

		test.log(LogStatus.INFO, "Create a filter");
		spogServer.setToken(validToken);

		test.log(LogStatus.INFO, "Create source filter for the logged in user of org: " + organizationType);
		String filter_Id = spogServer.createFilterwithCheck(user_id, filtername, ProtectionStatus.protect.name(),
				ConnectionStatus.online.name(), UUID.randomUUID().toString(), "finished", "emptyarray", "windows",
				"emptyarray", SourceType.machine.name(), "true", test);

		test.log(LogStatus.INFO,
				"Delete the source filter in org: " + organizationType + " using filter ID that does not exist");
		spogSource4SPOGServer.deletesourcefilterforLoggedInuser(UUID.randomUUID().toString(), validToken,
				SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.SOURCE_FILTER_NOT_FOUND_WITH_USER_ID, test);

		test.log(LogStatus.INFO, "Delete the source filter in org: " + organizationType
				+ " using filter ID tha does not exist and token of csr");
		spogSource4SPOGServer.deletesourcefilterforLoggedInuser(UUID.randomUUID().toString(),ti.csr_token ,
				SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.SOURCE_FILTER_NOT_FOUND_WITH_USER_ID, test);

		test.log(LogStatus.INFO,
				"Delete the source filter in org: " + organizationType + " using valid filter ID and token of csr");
		spogSource4SPOGServer.deletesourcefilterforLoggedInuser(filter_Id, ti.csr_token, SpogConstants.RESOURCE_NOT_EXIST,
				SpogMessageCode.SOURCE_FILTER_NOT_FOUND_WITH_USER_ID, test);

		test.log(LogStatus.INFO, "Delete the source filter in org: " + organizationType);
		spogSource4SPOGServer.deletesourcefilterforLoggedInuser(filter_Id, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

	} 

	@DataProvider(name = "sourcefilter_otherorg", parallel = false)
	public final Object[][] sourcefilter_otherorg() {
		return new Object[][] {
				{ "direct_msp", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id, ti.normal_msp_org1_user1_token,
						RandomStringUtils.randomAlphanumeric(4) + "ramesh-test12" },
				{ "direct_suborg", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id,
						ti.normal_msp1_suborg1_user1_token, RandomStringUtils.randomAlphanumeric(4) + "ramesh-test12" },
				{ "msp_direct", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id, ti.direct_org1_user1_token,
						RandomStringUtils.randomAlphanumeric(4) + "ramesh-test12" },
				{ "msp_suborg", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id, ti.normal_msp1_suborg1_user1_token,
						RandomStringUtils.randomAlphanumeric(4) + "ramesh-test12" },
				{ "msp_mspb", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id, ti.normal_msp_org2_user1_token,
						RandomStringUtils.randomAlphanumeric(4) + "ramesh-test12" },
				{ "suborg_direct", ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id, ti.direct_org1_user1_token,
						RandomStringUtils.randomAlphanumeric(4) + "ramesh-test12" },
				{ "suborg_suborgb", ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg2_user1_token,
						RandomStringUtils.randomAlphanumeric(4) + "ramesh-test12" },
				{ "suborg_mspb", ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id, ti.normal_msp_org2_user1_token,
						RandomStringUtils.randomAlphanumeric(4) + "ramesh-test12" },

				// csr read only cases
				{ "csrreadonly_direct", ti.csr_org_id, ti.csr_readonly_token, ti.csr_readonly_admin_user_id,
						ti.direct_org1_user1_token, RandomStringUtils.randomAlphanumeric(4) + "ramesh-test12" },
				{ "csrreadonly_msp", ti.csr_org_id, ti.csr_readonly_token, ti.csr_readonly_admin_user_id, ti.normal_msp_org1_user1_token,
						RandomStringUtils.randomAlphanumeric(4) + "ramesh-test12" },
				{ "csrreadonly_suborg", ti.csr_org_id, ti.csr_readonly_token, ti.csr_readonly_admin_user_id,
						ti.normal_msp1_suborg1_user1_token, RandomStringUtils.randomAlphanumeric(4) + "ramesh-test12" }, };
	}

	@Test(dataProvider = "sourcefilter_otherorg")
	public void deleteSourceFilterForLoggedInUser_404_otherorgtoken(String organizationType, String org_Id,
			String validToken, String user_Id, String otherorgtoken, String filtername) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);
		test.assignAuthor("ramesh.pendurthy");

		test.log(LogStatus.INFO, "Create a filter");
		spogServer.setToken(validToken);

		test.log(LogStatus.INFO, "Create source filter for the logged in user of org: " + organizationType);
		String filter_Id = spogServer.createFilterwithCheck(user_Id, filtername, ProtectionStatus.protect.name(),
				ConnectionStatus.online.name(), UUID.randomUUID().toString(), "finished", "emptyarray", "windows",
				"emptyarray", SourceType.machine.name(), "true", test);

		test.log(LogStatus.INFO, "Delete the source filter in org: " + organizationType + " using other org token");
		spogSource4SPOGServer.deletesourcefilterforLoggedInuser(filter_Id, otherorgtoken,
				SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.SOURCE_FILTER_NOT_FOUND_WITH_USER_ID, test);

		test.log(LogStatus.INFO, "Delete the source filter in org: " + organizationType + " using valid token");
		spogSource4SPOGServer.deletesourcefilterforLoggedInuser(filter_Id, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

	}

	@Test(dataProvider = "organizationAndUserInfo")
	public void deleteSourceFilterForLoggedInUser_400(String organizationType, String org_Id, String validToken,
			String user_id, String filtername) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);
		test.assignAuthor("prasad.deverakonda");

		spogServer.setToken(validToken);
		test.log(LogStatus.INFO, "Create source filter for the logged in user of org: " + organizationType);
		String filter_Id = spogServer.createFilterwithCheck(user_id, filtername, ProtectionStatus.protect.name(),
				ConnectionStatus.online.name(), UUID.randomUUID().toString(), "finished", "emptyarray", "windows",
				"emptyarray", SourceType.machine.name(), "true", test);

		test.log(LogStatus.INFO, "Delete the source filter in org: " + organizationType + " using invalid filter Id");
		spogSource4SPOGServer.deletesourcefilterforLoggedInuser("123", validToken,
				SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ERROR_MESSAGE_ELEMENT_FILTER_ID_NOT_UUID, test);

		test.log(LogStatus.INFO,
				"Delete the source filter in org: " + organizationType + " using invalid filter ID and token of csr");
		spogSource4SPOGServer.deletesourcefilterforLoggedInuser("123", ti.csr_token, SpogConstants.REQUIRED_INFO_NOT_EXIST,
				SpogMessageCode.ERROR_MESSAGE_ELEMENT_FILTER_ID_NOT_UUID, test);

		test.log(LogStatus.INFO, "Delete the source filter in org: " + organizationType);
		spogSource4SPOGServer.deletesourcefilterforLoggedInuser(filter_Id, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

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
