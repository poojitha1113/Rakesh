package api.users.search;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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

import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;

public class PostUsersSearchTest extends base.prepare.Is4Org {

	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private SPOGDestinationServer spogDestinationServer;
	private UserSpogServer userSpogServer;
	private TestOrgInfo ti;
	private ExtentTest test;


	LocalDate date = LocalDate.now();
	LocalDate yesterday = date.minusDays(1);
	LocalDate tomorrow = yesterday.plusDays(2);

	ArrayList<String> u_email = new ArrayList<>();
	ArrayList<HashMap<String, Object>> userData = new ArrayList<>();
	String prefix = RandomStringUtils.randomAlphanumeric(8);

	@BeforeClass
	@Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword","csrReadOnlyAdminName","csrReadOnlyAdminPassword","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword,String csrReadOnlyAdminName,String csrReadOnlyAdminPassword, String logFolder,String runningMachine,String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		rep = ExtentManager.getInstance("PostUsersSearchTest", logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Malleswari.Sykam";

		Nooftest = 0;
		// Used for creating a build number with dateformat
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date = new java.util.Date();
		this.buildVersion = buildVersion + "_" + dateFormater.format(date);

		BQName = this.getClass().getSimpleName();
		this.runningMachine = runningMachine;
		System.out.println("The value of hit is " + count1.isstarttimehit());
		ti = new TestOrgInfo(spogServer, test);	
		if (count1.isstarttimehit() == 0) {
			System.out.println("into creation time");
			creationTime = System.currentTimeMillis();
			count1.setcreationtime(creationTime);

			try {
				bqdb1.updateTable(BQName, runningMachine, buildVersion, String.valueOf(Nooftest), "0", "0",
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



	@DataProvider(name = "UsersSearch")
	public Object[][] usersSearch() {
		return new Object[][] {
			{ "direct", "spog_udp_qa_" + prefix + ti.direct_org1_name, ti.direct_org1_id,SpogConstants.DIRECT_ADMIN, 4 },
			{ "msp", "spog_udp_qa_" + prefix + ti.normal_msp_org1_name, ti.normal_msp_org1_id, SpogConstants.MSP_ADMIN, 4 },
			{ "msp_child", "spog_udp_qa_" + prefix + ti.normal_msp1_suborg1_name,ti.normal_msp1_suborg1_id, SpogConstants.DIRECT_ADMIN, 4 },

		};
	}

	@Test(dataProvider = "UsersSearch")
	public void usersSearchTest_200(String organizationType, String organization_name, String orgId, String role_id,
			int noofUsersToCreate) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);
		HashMap<String, Object> mspDetails = new HashMap<>();
		HashMap<String, Object> composedInfo = new HashMap<>();

		spogServer.setToken(ti.csr_token);

		String first_name = "1@@" + "SPOG_QA_MALLESWARI_FIRST_" + organizationType;
		String last_name = "1@@" + "SPOG_QA_MALLESWARI_LAST_" + organizationType;
		String email = "11a" + "SPOG_QA_MALLESWARI_" + organizationType + "@arc.com";

		if (organizationType == "msp_child") {
			mspDetails.put("organization_id", ti.normal_msp_org1_id);
			mspDetails.put("organization_name", "spog_udp_qa_spog_udp_qa_" + prefix + ti.normal_msp_org1_name);
			mspDetails.put("type", "msp");
			mspDetails.put("blocked", false);
		}

		for (int i = 0; i < noofUsersToCreate; i++) {

			u_email.add(spogServer.createUserAndCheck((prefix + email + i).toLowerCase(), ti.common_password,
					(prefix + first_name + i), prefix + last_name + i, role_id, orgId, test));
			spogServer.userLogin((prefix + email + i), ti.common_password);

			userSpogServer.setToken(spogServer.getJWTToken());
			composedInfo = userSpogServer.composeUserInfo((prefix + first_name + i), prefix + last_name + i,
					(prefix + email + i).toLowerCase(), ti.common_password, role_id, false, "verified", organization_name,
					organizationType, false, mspDetails, test);
			userData.add(composedInfo);
		}

		// Search users with csr read only token

		// seacrhstring on first name
		userSpogServer.postUsersSearch(userData, "_2$4", "1", "100", ti.csr_readonly_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		userSpogServer.postUsersSearch(userData, (prefix + "ABCDEFGHI"), "2", "20", ti.csr_readonly_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		userSpogServer.postUsersSearch(userData, prefix + "ABCDEFGHI", "1", "100", ti.csr_readonly_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		userSpogServer.postUsersSearch(userData, prefix + "ABCDEFGHI", "-1", "100", ti.csr_readonly_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		userSpogServer.postUsersSearch(userData, prefix + "ABCDEFGHI", "20", "100", ti.csr_readonly_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		// searching on the last name
		userSpogServer.postUsersSearch(userData, prefix + "IJKLMNOP", "1", "20", ti.csr_readonly_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		userSpogServer.postUsersSearch(userData, prefix + "IJKLMNOP", "1", "100", ti.csr_readonly_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		userSpogServer.postUsersSearch(userData, prefix + "IJKLMNOP", "20", "1", ti.csr_readonly_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		userSpogServer.postUsersSearch(userData, prefix + "IJKLMNOP", "-1", "1", ti.csr_readonly_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		// search on the email
		userSpogServer.postUsersSearch(userData, "qrstuvwxy" + "SPOG_QA_MALLESWARI_" + organizationType + "@arc.com",
				"1", "20", ti.csr_readonly_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		userSpogServer.postUsersSearch(userData, "qrstuvwxy" + "SPOG_QA_MALLESWARI_" + organizationType + "@arc.com",
				"2", "20", ti.csr_readonly_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		userSpogServer.postUsersSearch(userData, "qrstuvwxy" + "SPOG_QA_MALLESWARI_" + organizationType + "@arc.com",
				"1", "100", ti.csr_readonly_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		userSpogServer.postUsersSearch(userData, "qrstuvwxy" + "SPOG_QA_MALLESWARI_" + organizationType + "@arc.com",
				"0", "20", ti.csr_readonly_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		// search string on the doesn't exist first name
		userSpogServer.postUsersSearch(userData, "_ABCDEFGHI", "0", "2", ti.csr_readonly_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		userSpogServer.postUsersSearch(userData, "_ABCF", "2", "20", ti.csr_readonly_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		// userSpogServer.postUsersSearch(userData, "_ABCD_", "1", "100",
		// ti.csr_readonly_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		userSpogServer.postUsersSearch(userData, "_ABCDEFGHI_", "-1", "100", ti.csr_readonly_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		userSpogServer.postUsersSearch(userData, "_ABCDEFGHIG_", "20", "100", ti.csr_readonly_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		// searching on the does not exist last name
		userSpogServer.postUsersSearch(userData, "_IJKLMNOP", "1", "20", ti.csr_readonly_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		userSpogServer.postUsersSearch(userData, "_IJKLM_", "1", "100", ti.csr_readonly_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		userSpogServer.postUsersSearch(userData, prefix + "IJKLMNOP_", "20", "1", ti.csr_readonly_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		userSpogServer.postUsersSearch(userData, prefix + "IJKLMNOP_", "-1", "1", ti.csr_readonly_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		// search on the email
		userSpogServer.postUsersSearch(userData, "qrstuvwxy" + "SPOG_QA_MALLESWARI_" + organizationType + "@Arc.com",
				"1", "20", ti.csr_readonly_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		userSpogServer.postUsersSearch(userData, "qrstuvwxy" + "SPOG_QA_MALLESWARI_" + organizationType + "@Arc.com",
				"2", "20", ti.csr_readonly_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		userSpogServer.postUsersSearch(userData, "qrstuvwxy" + "SPOG_QA_MALLESWARI_" + organizationType + "@Arc.com",
				"1", "100", ti.csr_readonly_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		userSpogServer.postUsersSearch(userData, "qrstuvwxy" + "SPOG_QA_MALLESWARI_" + organizationType + "@Arc.com",
				"0", "20", ti.csr_readonly_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		userSpogServer.postUsersSearch(userData, prefix + "qrstuvwxy", "0", "20", ti.csr_readonly_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		// Search users with the CSR Token with differen Secnarioes

		// seacrhstring on first name
		userSpogServer.postUsersSearch(userData, "eswari+ABCDEF", "1", "20", ti.csr_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		userSpogServer.postUsersSearch(userData, (prefix + "ABCDEFGHI"), "2", "20", ti.csr_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		userSpogServer.postUsersSearch(userData, prefix + "ABCDEFGHI", "1", "100", ti.csr_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		userSpogServer.postUsersSearch(userData, prefix + "ABCDEFGHI", "-1", "100", ti.csr_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		userSpogServer.postUsersSearch(userData, prefix + "ABCDEFGHI", "20", "100", ti.csr_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		// searching on the last name
		userSpogServer.postUsersSearch(userData, prefix + "IJKLMNOP", "1", "20", ti.csr_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		userSpogServer.postUsersSearch(userData, prefix + "IJKLMNOP", "1", "100", ti.csr_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		userSpogServer.postUsersSearch(userData, prefix + "IJKLMNOP", "20", "1", ti.csr_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		userSpogServer.postUsersSearch(userData, prefix + "IJKLMNOP", "-1", "1", ti.csr_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		// search on the email
		userSpogServer.postUsersSearch(userData, "qrstuvwxy" + "SPOG_QA_MALLESWARI_" + organizationType + "@arc.com",
				"1", "20", ti.csr_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		userSpogServer.postUsersSearch(userData, "qrstuvwxy" + "SPOG_QA_MALLESWARI_" + organizationType + "@arc.com",
				"2", "20", ti.csr_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		userSpogServer.postUsersSearch(userData, "qrstuvwxy" + "SPOG_QA_MALLESWARI_" + organizationType + "@arc.com",
				"1", "100", ti.csr_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		userSpogServer.postUsersSearch(userData, "qrstuvwxy" + "SPOG_QA_MALLESWARI_" + organizationType + "@arc.com",
				"0", "20", ti.csr_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		// search string on the doesn't exist first name
		userSpogServer.postUsersSearch(userData, "_ABCDEFGHI", "0", "2", ti.csr_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		userSpogServer.postUsersSearch(userData, "_ABCF", "2", "20", ti.csr_token, SpogConstants.SUCCESS_GET_PUT_DELETE,
				null, test);
		// userSpogServer.postUsersSearch(userData, "_ABCD_", "1", "100", ti.csr_token,
		// SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		userSpogServer.postUsersSearch(userData, "_ABCDEFGHI_", "-1", "100", ti.csr_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		userSpogServer.postUsersSearch(userData, "_ABCDEFGHIG_", "20", "100", ti.csr_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		// searching on the does not exist last name
		userSpogServer.postUsersSearch(userData, "_IJKLMNOP", "1", "20", ti.csr_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		userSpogServer.postUsersSearch(userData, "_IJKLM_", "1", "100", ti.csr_token, SpogConstants.SUCCESS_GET_PUT_DELETE,
				null, test);
		userSpogServer.postUsersSearch(userData, prefix + "IJKLMNOP_", "20", "1", ti.csr_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		userSpogServer.postUsersSearch(userData, prefix + "IJKLMNOP_", "-1", "1", ti.csr_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		// search on the email
		userSpogServer.postUsersSearch(userData, "qrstuvwxy" + "SPOG_QA_MALLESWARI_" + organizationType + "@Arc.com",
				"1", "20", ti.csr_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		userSpogServer.postUsersSearch(userData, "qrstuvwxy" + "SPOG_QA_MALLESWARI_" + organizationType + "@Arc.com",
				"2", "20", ti.csr_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		userSpogServer.postUsersSearch(userData, "qrstuvwxy" + "SPOG_QA_MALLESWARI_" + organizationType + "@Arc.com",
				"1", "100", ti.csr_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		userSpogServer.postUsersSearch(userData, "qrstuvwxy" + "SPOG_QA_MALLESWARI_" + organizationType + "@Arc.com",
				"0", "20", ti.csr_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		userSpogServer.postUsersSearch(userData, prefix + "qrstuvwxy", "0", "20", ti.csr_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

	}

	@DataProvider(name = "getUsersInfo_invalid")
	public final Object[][] getusersInfoInfo1() {
		return new Object[][] {
			{ ti.csr_token, 1, 20, "orgd_id", "csr" },
			{ ti.direct_org1_user1_token, 1, 20, "orgd_id", "direct" },
			{ ti.normal_msp_org2_user1_token, 1, 20, "orgd_id", "msp" },
			{ ti.normal_msp1_suborg1_user1_token, 1, 20, "orgd_id", "msp_child" },
			
		};
	}

	// get Source by id For different scenarios
	@Test(dataProvider = "getUsersInfo_invalid")
	public void getUsersInfoBySearchString_invalid(String token, 
												int currPage,
												int pageSize,
												String search_string,
												String organizationType) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		test.assignAuthor("sykam.nagamalleswari");
		if (!organizationType.equals("csr")) {

			test.log(LogStatus.INFO, "Post user search with other org token.");
			userSpogServer.postUsersSearch(userData, prefix + "ABCDEFGHI", "0", "2", token,
					SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY, test);
		}

		// search_stirng is missed
		token = ti.csr_token;
		test.log(LogStatus.INFO, "POST user search with empty search_stirng");
		userSpogServer.postUsersSearch(userData, "", "0", "2", ti.csr_token, SpogConstants.REQUIRED_INFO_NOT_EXIST,
				SpogMessageCode.SEARCH_STRING_CANNOT_BLANK, test);

		// seacrh_string_given lessthan 3 charatcers
		test.log(LogStatus.INFO, "POST user search with seacrh_string_given lessthan 3 charatcers");
		userSpogServer.postUsersSearch(userData, "12", "0", "2", ti.csr_token, SpogConstants.REQUIRED_INFO_NOT_EXIST,
				SpogMessageCode.AT_LEAST_THREE_CHARACTRES_SEARCH_STRING, test);

		// invalid token
		token = ti.csr_token + "JUNK";
		test.log(LogStatus.INFO, "POST user search with invalid token");
		userSpogServer.postUsersSearch(userData, prefix + "ABCDEFGHI", "0", "2", token, SpogConstants.NOT_LOGGED_IN,
				SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

		// missed token
		token = "";
		test.log(LogStatus.INFO, "POST user search with missing token");
		userSpogServer.postUsersSearch(userData, prefix + "ABCDEFGHI", "0", "2", token, SpogConstants.NOT_LOGGED_IN,
				SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);

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
