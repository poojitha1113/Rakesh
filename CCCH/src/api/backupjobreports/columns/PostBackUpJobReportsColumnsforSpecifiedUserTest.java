
package api.backupjobreports.columns;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
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

import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGJobServer;
import InvokerServer.SPOGReportServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class PostBackUpJobReportsColumnsforSpecifiedUserTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private SPOGJobServer spogJobServer;
	private SPOGReportServer spogReportServer;
	private SPOGDestinationServer spogDestinationServer;
	private Policy4SPOGServer policy4SpogServer;

	private ExtentTest test;
	private TestOrgInfo ti;

	LocalDate date = LocalDate.now();
	LocalDate yesterday = date.minusDays(1);
	LocalDate tomorrow = yesterday.plusDays(2);

	ArrayList<String> columnIdList = new ArrayList<String>();
	ArrayList<String> MSPColumnIdList = new ArrayList<String>();

	ArrayList<HashMap<String, Object>> columnsHeadContent = new ArrayList<HashMap<String, Object>>();
	ArrayList<HashMap<String, Object>> MSPColumnsHeadContent = new ArrayList<HashMap<String, Object>>();

	@BeforeClass
	@Parameters({ "baseURI", "port", "csrAdminUserName", "csrAdminPassword", "csrReadOnlyAdminName",
			"csrReadOnlyAdminPassword", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword,
			String readOnlyUserName, String readOnlyPassword, String logFolder, String runningMachine,
			String buildVersion) {

		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogJobServer = new SPOGJobServer(baseURI, port);
		spogReportServer = new SPOGReportServer(baseURI, port);
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(), logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Ramya.Nagepalli";

		Nooftest = 0;
		// Used for creating a build number with dateformat
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date = new java.util.Date();
		this.buildVersion = buildVersion + "_" + dateFormater.format(date);

		BQName = this.getClass().getSimpleName();
		this.runningMachine = runningMachine;
		System.out.println("The value of hit is " + count1.isstarttimehit());
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);

		policy4SpogServer = new Policy4SPOGServer(baseURI, port);
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

		ti = new TestOrgInfo(spogServer, test);
		spogServer.setToken(ti.csr_token);

		Response response = spogReportServer.getSystemBackUpJobReportsColumns(ti.csr_token, "csr",
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		columnsHeadContent = response.then().extract().path("data");
		int length = columnsHeadContent.size();
		for (int i = 0; i < length; i++) {
			HashMap<String, Object> HeadContent = columnsHeadContent.get(i);

			columnIdList.add((String) HeadContent.get("column_id"));

		}

		response = spogReportServer.getSystemBackUpJobReportsColumns(ti.normal_msp_org1_user1_token, "msp",
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		MSPColumnsHeadContent = response.then().extract().path("data");
		length = MSPColumnsHeadContent.size();
		for (int i = 0; i < length; i++) {
			HashMap<String, Object> HeadContent = MSPColumnsHeadContent.get(i);

			MSPColumnIdList.add((String) HeadContent.get("column_id"));

		}

	}

	// This information is related to the destination information of the
	// cloudDedupe volume
	@DataProvider(name = "postBackUpJobReportsColumnsforSpecifiedUser_valid")
	public final Object[][] postBackUpJobReportsColumnsforSpecifiedUser_valid() {
		return new Object[][] {
				{ ti.csr_readonly_admin_user_id, ti.csr_readonly_token, "CSR-readonly", ti.csr_org_id,
						new String[] { "true", "false", "none" },
						new String[] { "1", "7", "2", "9", "4", "6", "1", "2", "6" } },
				{ ti.direct_org1_user1_id, ti.direct_org1_user1_token, "Direct", ti.direct_org1_id,
						new String[] { "true", "false", "none" },
						new String[] { "1", "8", "9", "5", "3", "4", "7", "2", "6" } },
				{ ti.normal_msp_org2_user1_id, ti.normal_msp_org2_user1_token, "MSP", ti.normal_msp_org2_id,
						new String[] { "true", "false", "none" },
						new String[] { "7", "2", "9", "4", "6", "5", "1", "8", "3" } },
				{ ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg1_user1_token, "SUB_ORG",
						ti.normal_msp1_suborg1_id, new String[] { "true", "false", "none" },
						new String[] { "7", "2", "9", "4", "6", "5", "1", "8", "3" } },
				{ ti.normal_msp1_suborg1_user1_id, ti.normal_msp_org1_user1_token, "SUB_MSP", ti.normal_msp1_suborg1_id,
						new String[] { "true", "false", "none" },
						new String[] { "7", "2", "9", "4", "6", "5", "1", "8", "3" } },
				{ ti.normal_msp1_suborg1_user1_id, ti.normal_msp_org1_msp_accountadmin1_token, "SUB_MSPAccount",
						ti.normal_msp1_suborg1_id, new String[] { "true", "false", "none" },
						new String[] { "7", "2", "9", "4", "6", "5", "1", "8", "3" } },

				// 3 tier cases
				{ ti.root_msp_org1_user1_id, ti.root_msp_org1_user1_token, "ROOT-MSP", ti.root_msp_org1_id,
						new String[] { "true", "false", "none" },
						new String[] { "7", "2", "9", "4", "6", "5", "1", "8", "3" } },
				{ ti.root_msp1_submsp1_user1_id, ti.root_msp1_submsp1_user1_token, "submsp",
						ti.root_msp1_submsp_org1_id, new String[] { "true", "false", "none" },
						new String[] { "7", "2", "9", "4", "6", "5", "1", "8", "3" } },

				{ ti.msp1_submsp2_suborg1_user1_id, ti.msp1_submsp2_suborg1_user1_token, "submsp",
						ti.msp1_submsp2_sub_org1_id, new String[] { "true", "false", "none" },
						new String[] { "7", "2", "9", "4", "6", "5", "1", "8", "3" } }, };
	}

	@Test(dataProvider = "postBackUpJobReportsColumnsforSpecifiedUser_valid")
	public void postBackUpJobReportsColumnsforSpecifiedUser_valid(String user_id, String adminToken,
			String organization_type, String organization_id, String[] visible, String[] order_id) {

		test = ExtentManager
				.getNewTest(organization_type + "Organization createBackUpJobReportsColumnsforSpecifiedUser");

		spogJobServer.setToken(adminToken);

		ArrayList<HashMap<String, Object>> expected_columns = new ArrayList<>();

		HashMap<String, Object> temp = new HashMap<>();

		if (organization_type.equalsIgnoreCase("msp")) {
			columnIdList = MSPColumnIdList;
			columnsHeadContent = MSPColumnsHeadContent;

		}

		test.log(LogStatus.INFO,
				"composing the createBackUpJobReportsColumnsforSpecifiedUser in org: " + organization_type);

		for (int j = 0; j < 2; j++) {
			temp = spogReportServer.composeBackUpJobReportsColumns(columnIdList.get(j), visible[j], order_id[j]);

			expected_columns.add(temp);
		}

		test.log(LogStatus.INFO,
				"Create the createBackUpJobReportsColumnsforSpecifiedUser in org: " + organization_type);
		Response response = spogReportServer.createBackUpJobReportsColumnsForSpecifiedUser(adminToken, user_id,
				expected_columns, SpogConstants.SUCCESS_POST, test);

		test.log(LogStatus.INFO, "check the backup job reports columns");
		spogJobServer.compareJobColumnsContent(response, expected_columns, columnsHeadContent,
				SpogConstants.SUCCESS_POST, null, test);

		test.log(LogStatus.INFO, "delete the backup job reports columns");
		spogReportServer.deleteBackUpJobReportsColumnsForSpecifiedUser(adminToken, user_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

	}

	@DataProvider(name = "postBackUpJobReportsColumnsforSpecifiedUser_invalid")
	public final Object[][] postBackUpJobReportsColumnsforSpecifiedUser_invalid() {
		return new Object[][] {

				{ "Invalid Authorization with junk token", ti.direct_org1_user1_token, ti.direct_org1_user1_id, "Junk",
						"Direct", ti.direct_org1_id, new String[] { "true", "false", "none" },
						new String[] { "1", "8", "9", "5", "3", "4", "7", "2", "6" }, SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT },
				{ "Invalid Authorization with missing token", ti.normal_msp_org1_user1_token,
						ti.normal_msp_org1_user1_id, "", "MSP", ti.normal_msp_org1_id,
						new String[] { "true", "false", "none" },
						new String[] { "7", "2", "9", "4", "6", "5", "1", "8", "3" }, SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTHENTICATION_FAILED },

				{ "INSUFFICIENT_PERMISSIONS of Direct_msp", ti.direct_org1_user1_token, ti.direct_org1_user1_id,
						ti.normal_msp_org1_user1_token, "Direct", ti.direct_org1_id,
						new String[] { "true", "false", "none" },
						new String[] { "1", "8", "9", "5", "3", "4", "7", "2", "6" },
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "INSUFFICIENT_PERMISSIONS of Direct_direct_b", ti.direct_org1_user1_token, ti.direct_org1_user1_id,
						ti.direct_org2_user1_token, "Direct", ti.direct_org1_id,
						new String[] { "true", "false", "none" },
						new String[] { "1", "8", "9", "5", "3", "4", "7", "2", "6" },
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "INSUFFICIENT_PERMISSIONS of Direct_sub-org", ti.direct_org1_user1_token, ti.direct_org1_user1_id,
						ti.normal_msp1_suborg1_user1_token, "Direct", ti.direct_org1_id,
						new String[] { "true", "false", "none" },
						new String[] { "1", "8", "9", "5", "3", "4", "7", "2", "6" },
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "INSUFFICIENT_PERMISSIONS of msp-mspb", ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,
						ti.normal_msp_org2_user1_token, "MSP", ti.normal_msp_org1_id,
						new String[] { "true", "false", "none" },
						new String[] { "7", "2", "9", "4", "6", "5", "1", "8", "3" },
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "INSUFFICIENT_PERMISSIONS of msp-direct", ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,
						ti.direct_org2_user1_token, "MSP", ti.normal_msp_org1_id,
						new String[] { "true", "false", "none" },
						new String[] { "7", "2", "9", "4", "6", "5", "1", "8", "3" },
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "INSUFFICIENT_PERMISSIONS of msp-suborg", ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,
						ti.normal_msp1_suborg1_user1_token, "MSP", ti.normal_msp_org1_id,
						new String[] { "true", "false", "none" },
						new String[] { "7", "2", "9", "4", "6", "5", "1", "8", "3" },
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "INSUFFICIENT_PERMISSIONS of suborg-direct", ti.normal_msp1_suborg1_user1_token,
						ti.normal_msp1_suborg1_user1_id, ti.direct_org2_user1_token, "SUB_ORG",
						ti.normal_msp1_suborg1_id, new String[] { "true", "false", "none" },
						new String[] { "1", "4", "5", "7", "6", "8", "3", "9", "2" },
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "INSUFFICIENT_PERMISSIONS of suborg-mspb", ti.normal_msp1_suborg1_user1_token,
						ti.normal_msp1_suborg1_user1_id, ti.normal_msp_org2_user1_token, "SUB_ORG",
						ti.normal_msp1_suborg1_id, new String[] { "true", "false", "none" },
						new String[] { "1", "4", "5", "7", "6", "8", "3", "9", "2" },
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "INSUFFICIENT_PERMISSIONS of suborg-suborgb", ti.normal_msp1_suborg1_user1_token,
						ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg2_user1_token, "SUB_ORG",
						ti.normal_msp1_suborg1_id, new String[] { "true", "false", "none" },
						new String[] { "1", "4", "5", "7", "6", "8", "3", "9", "2" },
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },

				{ "column_id_does_not_exist", ti.direct_org1_user1_token, ti.direct_org1_user1_id,
						ti.direct_org1_user1_token, "Direct", ti.direct_org1_id,
						new String[] { "true", "false", "none" },
						new String[] { "1", "8", "9", "5", "3", "4", "7", "2", "6" }, SpogConstants.RESOURCE_NOT_EXIST,
						SpogMessageCode.COLUMN_ID_DOESNOT_EXIST },
				{ "user_id_is_invalid", ti.direct_org1_user1_token, "invalid", ti.direct_org1_user1_token, "Direct",
						ti.direct_org1_id, new String[] { "true", "false", "none" },
						new String[] { "1", "8", "9", "5", "3", "4", "7", "2", "6" },
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_USERID_IS_NOT_UUID },
				{ "order_id_greaterthan_default_column_size", ti.direct_org1_user1_token, ti.direct_org1_user1_id,
						ti.direct_org1_user1_token, "Direct", ti.direct_org1_id,
						new String[] { "true", "false", "none" },
						new String[] { "1", "8", "9", "5", "3", "4", "7", "2", "6" },
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ORDER_ID_SHOULD_BE_LESS_THAN_MAX_COUNT },
				{ "order_id_lessthan_1", ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id,
						ti.normal_msp1_suborg1_user1_token, "SUB_ORG", ti.normal_msp1_suborg1_id,
						new String[] { "true", "false", "none" },
						new String[] { "1", "8", "9", "5", "3", "4", "7", "2", "6" },
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ORDER_ID_ATLEAST_1 },
				{ "user_id_is_Random_UUID", ti.normal_msp_org1_user1_token, UUID.randomUUID().toString(),
						ti.normal_msp_org1_user1_token, "MSP", ti.normal_msp_org1_id,
						new String[] { "true", "false", "none" },
						new String[] { "7", "2", "9", "4", "6", "5", "1", "8", "3" }, SpogConstants.RESOURCE_NOT_EXIST,
						SpogMessageCode.USER_ID_DOESNOT_EXIST },
				/*{ "column_id_already_exist", ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,
						ti.normal_msp_org1_user1_token, "MSP", ti.normal_msp_org1_id,
						new String[] { "true", "false", "none" },
						new String[] { "1", "8", "9", "5", "3", "4", "7", "2", "6" }, SpogConstants.SUCCESS_POST,
						SpogMessageCode.SUCCESS_POST },
				{ "order_id_already_exist", ti.normal_msp_org2_user1_token, ti.normal_msp_org2_user1_id,
						ti.normal_msp_org2_user1_token, "MSP", ti.normal_msp_org1_id,
						new String[] { "true", "false", "none" },
						new String[] { "1", "8", "9", "5", "3", "4", "7", "2", "6" }, SpogConstants.SUCCESS_POST,
						SpogMessageCode.SUCCESS_POST },*/

				{ "INSUFFICIENT_PERMISSIONS of csrreadonly-direct", ti.csr_readonly_token,
						ti.csr_readonly_admin_user_id, ti.direct_org2_user1_token, "csrreadonly", ti.csr_org_id,
						new String[] { "true", "false", "none" },
						new String[] { "1", "4", "5", "7", "6", "8", "3", "9", "2" },
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "INSUFFICIENT_PERMISSIONS of csrreadonly-mspb", ti.csr_readonly_token, ti.csr_readonly_admin_user_id,
						ti.normal_msp_org2_user1_token, "csrreadonly", ti.csr_org_id,
						new String[] { "true", "false", "none" },
						new String[] { "1", "4", "5", "7", "6", "8", "3", "9", "2" },
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "INSUFFICIENT_PERMISSIONS of csrreadonly-suborgb", ti.csr_readonly_token,
						ti.csr_readonly_admin_user_id, ti.normal_msp1_suborg2_user1_token, "csrreadonly", ti.csr_org_id,
						new String[] { "true", "false", "none" },
						new String[] { "1", "4", "5", "7", "6", "8", "3", "9", "2" },
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },

				{ "column_id_does_not_exist", ti.csr_readonly_token, ti.csr_readonly_admin_user_id,
						ti.csr_readonly_token, "csrreadonly", ti.csr_org_id, new String[] { "true", "false", "none" },
						new String[] { "1", "8", "9", "5", "3", "4", "7", "2", "6" }, SpogConstants.RESOURCE_NOT_EXIST,
						SpogMessageCode.COLUMN_ID_DOESNOT_EXIST },
				{ "user_id_is_invalid", ti.csr_readonly_token, "invalid", ti.csr_readonly_token, "csrreadonly",
						ti.csr_org_id, new String[] { "true", "false", "none" },
						new String[] { "1", "8", "9", "5", "3", "4", "7", "2", "6" },
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_USERID_IS_NOT_UUID },
				{ "order_id_greaterthan_default_column_size", ti.csr_readonly_token, ti.csr_readonly_admin_user_id,
						ti.csr_readonly_token, "csrreadonly", ti.csr_org_id, new String[] { "true", "false", "none" },
						new String[] { "1", "8", "9", "5", "3", "4", "7", "2", "6" },
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ORDER_ID_SHOULD_BE_LESS_THAN_MAX_COUNT },
				{ "order_id_lessthan_1", ti.csr_readonly_token, ti.csr_readonly_admin_user_id, ti.csr_readonly_token,
						"csrreadonly", ti.csr_org_id, new String[] { "true", "false", "none" },
						new String[] { "1", "8", "9", "5", "3", "4", "7", "2", "6" },
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ORDER_ID_ATLEAST_1 },
				{ "user_id_is_Random_UUID", ti.csr_readonly_token, UUID.randomUUID().toString(), ti.csr_readonly_token,
						"csrreadonly", ti.csr_org_id, new String[] { "true", "false", "none" },
						new String[] { "7", "2", "9", "4", "6", "5", "1", "8", "3" }, SpogConstants.RESOURCE_NOT_EXIST,
						SpogMessageCode.USER_ID_DOESNOT_EXIST },
				/*{ "column_id_already_exist", ti.csr_readonly_token, ti.csr_readonly_admin_user_id,
						ti.csr_readonly_token, "csrreadonly", ti.csr_org_id, new String[] { "true", "false", "none" },
						new String[] { "1", "8", "9", "5", "3", "4", "7", "2", "6" }, SpogConstants.SUCCESS_POST,
						SpogMessageCode.SUCCESS_POST },
				{ "order_id_already_exist", ti.csr_readonly_token, ti.csr_readonly_admin_user_id, ti.csr_readonly_token,
						"csrreadonly", ti.csr_org_id, new String[] { "true", "false", "none" },
						new String[] { "1", "8", "9", "5", "3", "4", "7", "2", "6" }, SpogConstants.SUCCESS_POST,
						SpogMessageCode.SUCCESS_POST },*/

				// 3 tier cases

				{ "INSUFFICIENT_PERMISSIONS of rootmsp-suborg", ti.root_msp_org1_user1_token, ti.root_msp_org1_user1_id,
						ti.root_msp1_suborg1_user1_token, "rootmsp", ti.root_msp_org1_id,
						new String[] { "true", "false", "none" },
						new String[] { "1", "4", "5", "7", "6", "8", "3", "9", "2" },
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "INSUFFICIENT_PERMISSIONS of rootmsp-submsp", ti.root_msp_org1_user1_token, ti.root_msp_org1_user1_id,
						ti.root_msp1_submsp1_user1_token, "rootmsp", ti.root_msp_org1_id,
						new String[] { "true", "false", "none" },
						new String[] { "1", "4", "5", "7", "6", "8", "3", "9", "2" },
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },

				{ "INSUFFICIENT_PERMISSIONS of rootmsp-submsp_suborg", ti.root_msp_org1_user1_token,
						ti.root_msp_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token, "rootmsp", ti.root_msp_org1_id,
						new String[] { "true", "false", "none" },
						new String[] { "1", "4", "5", "7", "6", "8", "3", "9", "2" },
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },

				{ "INSUFFICIENT_PERMISSIONS of root_suborg-submsp", ti.root_msp1_suborg1_user1_token,
						ti.root_msp1_suborg1_user1_id, ti.msp1_submsp1_suborg1_user1_token, "rootmsp",
						ti.root_msp1_suborg1_id, new String[] { "true", "false", "none" },
						new String[] { "1", "4", "5", "7", "6", "8", "3", "9", "2" },
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },

				{ "INSUFFICIENT_PERMISSIONS of submsp-root_suborg", ti.msp1_submsp1_suborg1_user1_token,
						ti.msp1_submsp1_suborg1_user1_id, ti.root_msp1_suborg1_user1_token, "rootmsp",
						ti.msp1_submsp1_sub_org1_id, new String[] { "true", "false", "none" },
						new String[] { "1", "4", "5", "7", "6", "8", "3", "9", "2" },
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },

				{ "INSUFFICIENT_PERMISSIONS of submsp-submsp_suborg", ti.root_msp1_submsp1_user1_token,
						ti.root_msp1_submsp1_user1_id, ti.msp1_submsp1_suborg1_user1_token, "rootmsp",
						ti.root_msp1_submsp_org1_id, new String[] { "true", "false", "none" },
						new String[] { "1", "4", "5", "7", "6", "8", "3", "9", "2" },
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },

		};
	}

	@Test(dataProvider = "postBackUpJobReportsColumnsforSpecifiedUser_invalid")
	public void postBackUpJobReportsColumnsforSpecifiedUser_invalid(String invalidTestCase, String adminToken,
			String user_id, String InValidToken, String organization_type, String organization_id, String[] visible,
			String[] order_id, int ExpectedStatusCode, SpogMessageCode Errormessage

	) {

		test = ExtentManager.getNewTest(
				organization_type + "Organization createBackUpJobReportsColumnsforSpecifiedUser" + invalidTestCase);

		spogJobServer.setToken(adminToken);

		ArrayList<HashMap<String, Object>> expected_columns = new ArrayList<>();

		HashMap<String, Object> temp = new HashMap<>();

		test.log(LogStatus.INFO,
				"composing the createBackUpJobReportsColumnsforSpecifiedUser in org: " + organization_type);

		for (int j = 3; j < 5; j++) {
			int index1 = gen_random_index(visible);

			if (invalidTestCase == "column_id_does_not_exist") {
				temp = spogReportServer.composeBackUpJobReportsColumns(UUID.randomUUID().toString(), visible[index1],
						order_id[j]);

				j = 5;
			} else if (invalidTestCase == "column_id_already_exist") {
				temp = spogReportServer.composeBackUpJobReportsColumns(columnIdList.get(1), visible[index1],
						order_id[j]);
			} else if (invalidTestCase == "order_id_already_exist") {
				temp = spogReportServer.composeBackUpJobReportsColumns(columnIdList.get(j), visible[index1], "1");
			} else if (invalidTestCase == "order_id_greaterthan_default_column_size") {
				temp = spogReportServer.composeBackUpJobReportsColumns(columnIdList.get(j), visible[index1], "20");
			} else if (invalidTestCase == "order_id_lessthan_1") {
				temp = spogReportServer.composeBackUpJobReportsColumns(columnIdList.get(j), visible[index1], "0");
			} else if (invalidTestCase == "order_id_null") {
				temp = spogReportServer.composeBackUpJobReportsColumns(columnIdList.get(j), visible[index1], " ");
			} else {
				temp = spogReportServer.composeBackUpJobReportsColumns(columnIdList.get(j), visible[index1],
						order_id[j]);
			}

			expected_columns.add(temp);
		}

		test.log(LogStatus.INFO,
				"Create the createBackUpJobReportsColumnsforSpecifiedUser in org: " + organization_type);
		Response response = spogReportServer.createBackUpJobReportsColumnsForSpecifiedUser(InValidToken, user_id,
				expected_columns, ExpectedStatusCode, test);

		test.log(LogStatus.INFO, "check the backup job reports columns");
		spogJobServer.compareJobColumnsContent(response, expected_columns, columnsHeadContent, ExpectedStatusCode,
				Errormessage, test);

		test.log(LogStatus.INFO, "delete the backup job reports columns");
		spogReportServer.deleteBackUpJobReportsColumnsForLoggedInUser(adminToken, SpogConstants.SUCCESS_GET_PUT_DELETE,
				null, test);

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
		rep.flush();
	}

	/******************************************************************
	 * RandomFunction
	 ******************************************************************************/
	public int gen_random_index(String[] job_status) {
		Random generator = new Random();
		int randomindx = generator.nextInt(job_status.length);

		return randomindx;
	}

}
