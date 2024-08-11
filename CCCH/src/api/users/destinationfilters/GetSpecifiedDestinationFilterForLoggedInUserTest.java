package api.users.destinationfilters;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
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

import Constants.DestinationStatus;
import Constants.DestinationType;
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

public class GetSpecifiedDestinationFilterForLoggedInUserTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private SPOGDestinationServer spogDestinationServer;
	private UserSpogServer userSpogServer;
	private TestOrgInfo ti;
	private ExtentTest test;

	private String site_version = "1.0.0";
	private String gateway_hostname = "ramesh";

	private String org_model_prefix = this.getClass().getSimpleName();
	// used for test case count like passed,failed,remaining cases
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
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		rep = ExtentManager.getInstance("GetSpecifiedDestinationFilterForLoggedInUserTest", logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Ramesh.Pendurthy";
		ti = new TestOrgInfo(spogServer, test);	
		Nooftest = 0;
		// Used for creating a build number with dateformat
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date = new java.util.Date();
		this.buildVersion = buildVersion + "_" + dateFormater.format(date);

		this.BQName = this.getClass().getSimpleName();
		this.runningMachine = runningMachine;
		System.out.println("The value of hit is " + count1.isstarttimehit());

		/*if (count1.isstarttimehit() == 0) {
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

	@DataProvider(name = "getdestinationfilter", parallel = false)
	public final Object[][] getdestinationfilter_Invaid() {
		return new Object[][] {
			{ "Get Destination filters of Direct org user  of Normal msp  of Normal msp with the Direct user token", ti.normal_msp1_suborg1_user1_id,ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token,ti.direct_org2_user1_token, DestinationType.cloud_direct_volume.toString(), null, RandomStringUtils.randomAlphanumeric(4) + "prasad-test34", "none" },
			{ "Get Destination filters of Direct org user  of Normal msp with the Normal msp user token", ti.normal_msp1_suborg1_user1_id,ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token,ti.normal_msp_org1_user1_token, DestinationType.cloud_direct_volume.toString(), null, RandomStringUtils.randomAlphanumeric(4) + "prasad-test34", "none" },
			{ "Get Destination filters of Direct org user  of Normal msp with the Normal msp account admin user token", ti.normal_msp1_suborg1_user1_id,ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token,ti.normal_msp_org2_msp_accountadmin1_token, DestinationType.cloud_direct_volume.toString(), null, RandomStringUtils.randomAlphanumeric(4) + "prasad-test34", "none" },
			{ "Get Destination filters of Direct org user  of Normal msp with the Direct user of normal msp  token", ti.normal_msp1_suborg1_user1_id,ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token,ti.normal_msp2_suborg1_user1_token, DestinationType.cloud_direct_volume.toString(), null, RandomStringUtils.randomAlphanumeric(4) + "prasad-test34", "none" },
			{ "Get Destination filters of Direct org user  of Normal msp with the Root msp  user token", ti.normal_msp1_suborg1_user1_id,ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token,ti.root_msp_org1_user1_token, DestinationType.cloud_direct_volume.toString(), null, RandomStringUtils.randomAlphanumeric(4) + "prasad-test34", "none" },
			{ "Get Destination filters of Direct org user  of Normal msp with the Root msp account admin  user token", ti.normal_msp1_suborg1_user1_id,ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token,ti.root_msp_org1_msp_accountadmin2_token, DestinationType.cloud_direct_volume.toString(), null, RandomStringUtils.randomAlphanumeric(4) + "prasad-test34", "none" },
			{ "Get Destination filters of Direct org user  of Normal msp with the Direct user of root msp token", ti.normal_msp1_suborg1_user1_id,ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_token, DestinationType.cloud_direct_volume.toString(), null, RandomStringUtils.randomAlphanumeric(4) + "prasad-test34", "none" },
			{ "Get Destination filters of Direct org user  of Normal msp with the Sub msp  user token", ti.normal_msp1_suborg1_user1_id,ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token,ti.root_msp1_submsp1_user1_token, DestinationType.cloud_direct_volume.toString(), null, RandomStringUtils.randomAlphanumeric(4) + "prasad-test34", "none" },
			{ "Get Destination filters of Direct org user  of Normal msp with the sub msp account adim user token", ti.normal_msp1_suborg1_user1_id,ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token,ti.root_msp1_submsp1_account_admin_token, DestinationType.cloud_direct_volume.toString(), null, RandomStringUtils.randomAlphanumeric(4) + "prasad-test34", "none" },
			{ "Get Destination filters of Direct org user  of Normal msp with the  Direct user of sub msp token", ti.normal_msp1_suborg1_user1_id,ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token,ti.msp1_submsp1_suborg1_user1_token, DestinationType.cloud_direct_volume.toString(), null, RandomStringUtils.randomAlphanumeric(4) + "prasad-test34", "none" },

			{ "Get Destination filters of Direct org user with the Direct user token", ti.direct_org1_user1_id,ti.direct_org1_id, ti.direct_org1_user1_token,ti.direct_org2_user1_token, DestinationType.cloud_direct_volume.toString(), null, RandomStringUtils.randomAlphanumeric(4) + "prasad-test34", "none" },
			{ "Get Destination filters of Direct org user with the Normal msp user token", ti.direct_org1_user1_id,ti.direct_org1_id, ti.direct_org1_user1_token,ti.normal_msp_org1_user1_token, DestinationType.cloud_direct_volume.toString(), null, RandomStringUtils.randomAlphanumeric(4) + "prasad-test34", "none" },
			{ "Get Destination filters of Direct org user with the Normal msp account admin user token", ti.direct_org1_user1_id,ti.direct_org1_id, ti.direct_org1_user1_token,ti.normal_msp_org2_msp_accountadmin1_token, DestinationType.cloud_direct_volume.toString(), null, RandomStringUtils.randomAlphanumeric(4) + "prasad-test34", "none" },
			{ "Get Destination filters of Direct org user with the Direct user of normal msp  token", ti.direct_org1_user1_id,ti.direct_org1_id, ti.direct_org1_user1_token,ti.normal_msp1_suborg1_user1_token, DestinationType.cloud_direct_volume.toString(), null, RandomStringUtils.randomAlphanumeric(4) + "prasad-test34", "none" },
			{ "Get Destination filters of Direct org user with the Root msp  user token", ti.direct_org1_user1_id,ti.direct_org1_id, ti.direct_org1_user1_token,ti.root_msp_org1_user1_token, DestinationType.cloud_direct_volume.toString(), null, RandomStringUtils.randomAlphanumeric(4) + "prasad-test34", "none" },
			{ "Get Destination filters of Direct org user with the Root msp account admin  user token", ti.direct_org1_user1_id,ti.direct_org1_id, ti.direct_org1_user1_token,ti.root_msp_org1_msp_accountadmin2_token, DestinationType.cloud_direct_volume.toString(), null, RandomStringUtils.randomAlphanumeric(4) + "prasad-test34", "none" },
			{ "Get Destination filters of Direct org user with the Direct user of root msp token", ti.direct_org1_user1_id,ti.direct_org1_id, ti.direct_org1_user1_token,ti.root_msp1_suborg1_user1_token, DestinationType.cloud_direct_volume.toString(), null, RandomStringUtils.randomAlphanumeric(4) + "prasad-test34", "none" },
			{ "Get Destination filters of Direct org user with the Sub msp  user token", ti.direct_org1_user1_id,ti.direct_org1_id, ti.direct_org1_user1_token,ti.root_msp1_submsp1_user1_token, DestinationType.cloud_direct_volume.toString(), null, RandomStringUtils.randomAlphanumeric(4) + "prasad-test34", "none" },
			{ "Get Destination filters of Direct org user with the sub msp account adim user token", ti.direct_org1_user1_id,ti.direct_org1_id, ti.direct_org1_user1_token,ti.root_msp1_submsp1_account_admin_token, DestinationType.cloud_direct_volume.toString(), null, RandomStringUtils.randomAlphanumeric(4) + "prasad-test34", "none" },
			{ "Get Destination filters of Direct org user with the  Direct user of sub msp token", ti.direct_org1_user1_id,ti.direct_org1_id, ti.direct_org1_user1_token,ti.msp1_submsp1_suborg1_user1_token, DestinationType.cloud_direct_volume.toString(), null, RandomStringUtils.randomAlphanumeric(4) + "prasad-test34", "none" },


			{ "Get Destination filters of Direct org user  of root msp  of Normal msp with the Direct user token", ti.root_msp1_suborg1_user1_id,ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_token,ti.direct_org2_user1_token, DestinationType.cloud_direct_volume.toString(), null, RandomStringUtils.randomAlphanumeric(4) + "prasad-test34", "none" },
			{ "Get Destination filters of Direct org user  of root msp with the Normal msp user token", ti.root_msp1_suborg1_user1_id,ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_token,ti.normal_msp_org1_user1_token, DestinationType.cloud_direct_volume.toString(), null, RandomStringUtils.randomAlphanumeric(4) + "prasad-test34", "none" },
			{ "Get Destination filters of Direct org user  of root msp with the Normal msp account admin user token", ti.root_msp1_suborg1_user1_id,ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_token,ti.normal_msp_org2_msp_accountadmin1_token, DestinationType.cloud_direct_volume.toString(), null, RandomStringUtils.randomAlphanumeric(4) + "prasad-test34", "none" },
			{ "Get Destination filters of Direct org user  of root msp with the Direct user of normal msp  token", ti.root_msp1_suborg1_user1_id,ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_token,ti.normal_msp2_suborg1_user1_token, DestinationType.cloud_direct_volume.toString(), null, RandomStringUtils.randomAlphanumeric(4) + "prasad-test34", "none" },
			{ "Get Destination filters of Direct org user  of root msp with the Root msp  user token", ti.root_msp1_suborg1_user1_id,ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_token,ti.root_msp_org1_user1_token, DestinationType.cloud_direct_volume.toString(), null, RandomStringUtils.randomAlphanumeric(4) + "prasad-test34", "none" },
			{ "Get Destination filters of Direct org user  of root msp with the Root msp account admin  user token", ti.root_msp1_suborg1_user1_id,ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_token,ti.root_msp_org1_msp_accountadmin2_token, DestinationType.cloud_direct_volume.toString(), null, RandomStringUtils.randomAlphanumeric(4) + "prasad-test34", "none" },
			{ "Get Destination filters of Direct org user  of root msp with the Direct user of root msp token", ti.root_msp1_suborg1_user1_id,ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_token,ti.root_msp2_suborg1_user1_token, DestinationType.cloud_direct_volume.toString(), null, RandomStringUtils.randomAlphanumeric(4) + "prasad-test34", "none" },
			{ "Get Destination filters of Direct org user  of root msp with the Sub msp  user token", ti.root_msp1_suborg1_user1_id,ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_token,ti.root_msp1_submsp1_user1_token, DestinationType.cloud_direct_volume.toString(), null, RandomStringUtils.randomAlphanumeric(4) + "prasad-test34", "none" },
			{ "Get Destination filters of Direct org user  of root msp with the sub msp account adim user token", ti.root_msp1_suborg1_user1_id,ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_token,ti.root_msp1_submsp1_account_admin_token, DestinationType.cloud_direct_volume.toString(), null, RandomStringUtils.randomAlphanumeric(4) + "prasad-test34", "none" },
			{ "Get Destination filters of Direct org user  of root msp with the  Direct user of sub msp token", ti.root_msp1_suborg1_user1_id,ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_token,ti.msp1_submsp1_suborg1_user1_token, DestinationType.cloud_direct_volume.toString(), null, RandomStringUtils.randomAlphanumeric(4) + "prasad-test34", "none" },

			{ "Get Destination filters of Direct org user  of submsp  of Normal msp with the Direct user token", ti.msp1_submsp1_suborg1_user1_id,ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_token,ti.direct_org2_user1_token, DestinationType.cloud_direct_volume.toString(), null, RandomStringUtils.randomAlphanumeric(4) + "prasad-test34", "none" },
			{ "Get Destination filters of Direct org user  of submsp with the Normal msp user token", ti.msp1_submsp1_suborg1_user1_id,ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_token,ti.normal_msp_org1_user1_token, DestinationType.cloud_direct_volume.toString(), null, RandomStringUtils.randomAlphanumeric(4) + "prasad-test34", "none" },
			{ "Get Destination filters of Direct org user  of submsp with the Normal msp account admin user token", ti.msp1_submsp1_suborg1_user1_id,ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_token,ti.normal_msp_org2_msp_accountadmin1_token, DestinationType.cloud_direct_volume.toString(), null, RandomStringUtils.randomAlphanumeric(4) + "prasad-test34", "none" },
			{ "Get Destination filters of Direct org user  of submsp with the Direct user of normal msp  token", ti.msp1_submsp1_suborg1_user1_id,ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_token,ti.normal_msp2_suborg1_user1_token, DestinationType.cloud_direct_volume.toString(), null, RandomStringUtils.randomAlphanumeric(4) + "prasad-test34", "none" },
			{ "Get Destination filters of Direct org user  of submsp with the Root msp  user token", ti.msp1_submsp1_suborg1_user1_id,ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_token,ti.root_msp_org1_user1_token, DestinationType.cloud_direct_volume.toString(), null, RandomStringUtils.randomAlphanumeric(4) + "prasad-test34", "none" },
			{ "Get Destination filters of Direct org user  of submsp with the Root msp account admin  user token", ti.msp1_submsp1_suborg1_user1_id,ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_token,ti.root_msp_org1_msp_accountadmin2_token, DestinationType.cloud_direct_volume.toString(), null, RandomStringUtils.randomAlphanumeric(4) + "prasad-test34", "none" },
			{ "Get Destination filters of Direct org user  of submsp with the Direct user of root msp token", ti.msp1_submsp1_suborg1_user1_id,ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_token,ti.msp2_submsp1_suborg1_user1_token, DestinationType.cloud_direct_volume.toString(), null, RandomStringUtils.randomAlphanumeric(4) + "prasad-test34", "none" },
			{ "Get Destination filters of Direct org user  of submsp with the Sub msp  user token", ti.msp1_submsp1_suborg1_user1_id,ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_token,ti.root_msp1_submsp1_user1_token, DestinationType.cloud_direct_volume.toString(), null, RandomStringUtils.randomAlphanumeric(4) + "prasad-test34", "none" },
			{ "Get Destination filters of Direct org user  of submsp with the sub msp account adim user token", ti.msp1_submsp1_suborg1_user1_id,ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_token,ti.root_msp1_submsp1_account_admin_token, DestinationType.cloud_direct_volume.toString(), null, RandomStringUtils.randomAlphanumeric(4) + "prasad-test34", "none" },
			{ "Get Destination filters of Direct org user  of submsp with the  Direct user of sub msp token", ti.msp1_submsp1_suborg1_user1_id,ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_token,ti.msp2_submsp1_suborg1_user1_token, DestinationType.cloud_direct_volume.toString(), null, RandomStringUtils.randomAlphanumeric(4) + "prasad-test34", "none" },


		};
	}

	@Test(dataProvider = "getdestinationfilter")
	public void getdestinationbyfilterIdforloggedinuser_clouddirect(String organizationType,
			String user_id,
			String organization_id,
			String validToken,
			String OtherOrgToken,
			String destination_type,
			String destination_status,
			String destination_name,
			String filter_policy_id) {
		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);
		String prefix = RandomStringUtils.randomAlphanumeric(4);
		HashMap<String, Object> expected_response = new HashMap<>();
		String dest_name = destination_name;
		int count = 1;
		spogDestinationServer.setToken(validToken);

		test.log(LogStatus.INFO, "Create the destination filter for destination type");

		String filter_Id = spogDestinationServer.createDestinationFilterWithCheck(user_id, prefix + "filter", dest_name,
				filter_policy_id, destination_type, "true", test);

		test.log(LogStatus.INFO, "Compose destination filter info");

		if (organizationType.equals("suborg-mspAccAdminT")) {
			spogServer.setToken(validToken);
			organization_id = spogServer.GetLoggedinUserOrganizationID();
		}

		expected_response = composefilterinfo(filter_Id, organization_id, user_id, prefix + "filter", dest_name,
				filter_policy_id, destination_type, count);

		test.log(LogStatus.INFO, "get destination filter for logged in user of org " + organizationType);
		spogDestinationServer.getspecifiedDestinationFilterForLoggedInUserwithCheck(filter_Id, validToken,
				expected_response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		test.log(LogStatus.INFO, "delete destination filter by filter id");
		spogDestinationServer.deletedestinationfilterbyfilterId(user_id, filter_Id, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

	}




	@Test(dataProvider = "getdestinationfilter")
	public void getDestinationsFilters_404(String organizationType,
			String user_id,
			String organization_id,
			String validToken,
			String OtherOrgToken,
			String destination_type,
			String destination_status,
			String destination_name,
			String filter_policy_id) {
		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);
		String prefix = RandomStringUtils.randomAlphanumeric(4);
		HashMap<String, Object> expected_response = new HashMap<>();
		String dest_name = destination_name;
		int count = 1;
		spogDestinationServer.setToken(validToken);

		test.log(LogStatus.INFO, "Create the destination filter for destination type");

		String filter_Id = spogDestinationServer.createDestinationFilterWithCheck(user_id, prefix + "filter", dest_name,filter_policy_id, destination_type, "true", test);

		test.log(LogStatus.INFO, "Compose destination filter info");
		expected_response = composefilterinfo(filter_Id, organization_id, user_id, prefix + "filter", dest_name,filter_policy_id, destination_type, count);

		spogDestinationServer.setToken(OtherOrgToken);
		test.log(LogStatus.INFO, "get destination filter for logged in user of org " + organizationType);
		spogDestinationServer.getspecifiedDestinationFilterForLoggedInUserwithCheck(filter_Id, OtherOrgToken,expected_response,  SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.DESTINATION_FILTER_NOT_FOUND_WITH_USER_ID, test);

		test.log(LogStatus.INFO, "delete destination filter by filter id");
		spogDestinationServer.deletedestinationfilterbyfilterId(user_id, filter_Id, validToken,SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		test.log(LogStatus.INFO, "get destination filter by filter id where the filter does not exist using token of "+ organizationType);
		spogDestinationServer.getspecifiedDestinationFilterForLoggedInUserwithCheck(UUID.randomUUID().toString(),validToken, expected_response, SpogConstants.RESOURCE_NOT_EXIST,
				SpogMessageCode.DESTINATION_FILTER_NOT_FOUND_WITH_USER_ID, test);

		test.log(LogStatus.INFO,"get destination filter by filter id where the filter does not exist using token of csr");
		spogDestinationServer.getspecifiedDestinationFilterForLoggedInUserwithCheck(filter_Id,validToken, expected_response, SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.DESTINATION_FILTER_NOT_FOUND_WITH_USER_ID, test);

	}


	@DataProvider(name = "getdestinationfilter_401", parallel = false)
	public final Object[][] getdestinationfilter_401() {
		return new Object[][] {
			{ "Get Destination filters of Direct org user with the Direct user token", ti.direct_org1_user1_id,ti.direct_org1_id, ti.direct_org1_user1_token,ti.direct_org2_user1_token, DestinationType.cloud_direct_volume.toString(), null, RandomStringUtils.randomAlphanumeric(4) + "prasad-test34", "none" },
			{ "Get Destination filters of Direct org user  of Normal msp  of Normal msp with the Direct user token", ti.normal_msp1_suborg1_user1_id,ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token,ti.direct_org2_user1_token, DestinationType.cloud_direct_volume.toString(), null, RandomStringUtils.randomAlphanumeric(4) + "prasad-test34", "none" },
		};
	}

	@Test(dataProvider = "getdestinationfilter")
	public void getdestinationbyfilterIdforloggedinuser_401(String organizationType,
			String user_id,
			String organization_id,
			String validToken,
			String OtherOrgToken,
			String destination_type,
			String destination_status,
			String destination_name,
			String filter_policy_id) {
		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);
		HashMap<String, Object> expected_response = new HashMap<>();

		spogDestinationServer.setToken(validToken);
		String filter_Id=UUID.randomUUID().toString();
		test.log(LogStatus.INFO, "get destination filter by filter id using invalid token of " + organizationType);
		spogDestinationServer.getspecifiedDestinationFilterForLoggedInUserwithCheck(filter_Id, validToken + "J",
				expected_response, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT,
				test);

		test.log(LogStatus.INFO, "get destination filter by filter id using missing token of " + organizationType);
		spogDestinationServer.getspecifiedDestinationFilterForLoggedInUserwithCheck(filter_Id, "", expected_response,
				SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);


	}



	public HashMap<String, Object> composefilterinfo(String filter_Id, String organization_Id, String user_Id,
			String filter_name, String destination_name, String policy_id, String destination_type, int count) {
		HashMap<String, Object> expected_response = new HashMap<>();
		expected_response.put("filter_id", filter_Id);
		expected_response.put("organization_id", organization_Id);
		expected_response.put("user_id", user_Id);
		expected_response.put("filter_name", filter_name);
		expected_response.put("policy_id", policy_id);
		expected_response.put("destination_name", destination_name);
		expected_response.put("destination_type", destination_type);
		expected_response.put("count", count);
		return expected_response;
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
		// ending test
		// endTest(logger) : It ends the current test and prepares to create
		// HTML report
		rep.endTest(test);
		// rep.flush();
	}

}
