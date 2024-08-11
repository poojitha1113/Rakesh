package api.sources.groups;

import java.io.IOException;
import java.text.SimpleDateFormat;
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

import Constants.ConnectionStatus;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
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
import io.restassured.response.Response;

public class GetGroupById extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private UserSpogServer userSpogServer;
	private ExtentTest test;
	private TestOrgInfo ti;

	private Response response;

	int num_total = 0, num_protected = 0, num_unprotected = 0, num_partial_protected = 0;
	HashMap<String, Object> status = new HashMap<String, Object>();
	private String Direct_cloud_id;
	private String msp_cloud_id;
	private String root_cloud_id;

	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine, String buildVersion) {

		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(), logFolder);
		Nooftest = 0;
		bqdb1 = new SQLServerDb();
		String author = "Ramya.Nagepalli";
		count1 = new testcasescount();
		test = rep.startTest("beforeClass");
		test.assignAuthor("Ramya");

		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date = new java.util.Date();
		this.buildVersion = buildVersion + "_" + dateFormater.format(date);
		this.BQName = this.getClass().getSimpleName();
		this.runningMachine = runningMachine;
		System.out.println("The value of hit is " + count1.isstarttimehit());
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

		status.put("num_total", num_total);
		status.put("num_protected", num_protected);
		status.put("num_unprotected", num_unprotected);
		status.put("num_partial_protected", num_partial_protected);

		ti = new TestOrgInfo(spogServer, test);
		spogServer.setToken(ti.direct_org1_user1_token);

		Response response = spogServer.getCloudAccounts(ti.direct_org1_user1_token, "", test);
		Direct_cloud_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");

		// create cloud account for the msp organization
		spogServer.setToken(ti.normal_msp_org1_user1_token);
		response = spogServer.getCloudAccounts(ti.normal_msp_org1_user1_token, "", test);
		msp_cloud_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");

		response = spogServer.getCloudAccounts(ti.root_msp_org1_user1_token, "", test);
		root_cloud_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");

	}

	// used by sub_user
	@DataProvider(name = "groupInfo2")
	public final Object[][] getGroupinfo2() {
		return new Object[][] {
				{ "Direct - valid scenario to get group by id ", spogServer.ReturnRandom("spog_getgroup_byid"),
						spogServer.ReturnRandom("subuser_group"), ti.direct_org1_user1_token, ti.direct_org1_id,
						Direct_cloud_id, SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL,
						ti.direct_org1_user1_token, null },
				{ "suborg - valid scenario to get group by id ", spogServer.ReturnRandom("spog_getgroup_byid"),
						spogServer.ReturnRandom("subuser_group"), ti.normal_msp1_suborg1_user1_token,
						ti.normal_msp1_suborg1_id, msp_cloud_id, SpogConstants.SUCCESS_GET_PUT_DELETE,
						SpogMessageCode.SUCCESS_GET_PUT_DEL, ti.normal_msp1_suborg1_user1_token, null },
				{ "suborg-msp - valid scenario to get group by id ", spogServer.ReturnRandom("spog_getgroup_byid"),
						spogServer.ReturnRandom("subuser_group"), ti.normal_msp1_suborg1_user1_token,
						ti.normal_msp1_suborg1_id, msp_cloud_id, SpogConstants.SUCCESS_GET_PUT_DELETE,
						SpogMessageCode.SUCCESS_GET_PUT_DEL, ti.normal_msp_org1_user1_token, null },
				{ "suborg-maa - valid scenario to get group by id ", spogServer.ReturnRandom("spog_getgroup_byid"),
						spogServer.ReturnRandom("subuser_group"), ti.normal_msp1_suborg1_user1_token,
						ti.normal_msp1_suborg1_id, msp_cloud_id, SpogConstants.SUCCESS_GET_PUT_DELETE,
						SpogMessageCode.SUCCESS_GET_PUT_DEL, ti.normal_msp_org1_msp_accountadmin1_token, null },
				{ "rootsuborg - valid scenario to get group by id ", spogServer.ReturnRandom("spog_getgroup_byid"),
						spogServer.ReturnRandom("subuser_group"), ti.root_msp1_suborg1_user1_token,
						ti.root_msp1_suborg1_id, root_cloud_id, SpogConstants.SUCCESS_GET_PUT_DELETE,
						SpogMessageCode.SUCCESS_GET_PUT_DEL, ti.root_msp1_suborg1_user1_token, null },
				{ "rootsuborg-rootmsp - valid scenario to get group by id ",
						spogServer.ReturnRandom("spog_getgroup_byid"), spogServer.ReturnRandom("subuser_group"),
						ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_id, root_cloud_id,
						SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL,
						ti.root_msp_org1_user1_token, null },
				{ "rootsuborg-rootmsp - valid scenario to get group by id ",
						spogServer.ReturnRandom("spog_getgroup_byid"), spogServer.ReturnRandom("subuser_group"),
						ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_id, root_cloud_id,
						SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL,
						ti.root_msp_org1_msp_accountadmin1_token, null },
				{ "submsp_sub - valid scenario to get group by id ", spogServer.ReturnRandom("spog_getgroup_byid"),
						spogServer.ReturnRandom("subuser_group"), ti.msp1_submsp1_suborg1_user1_token,
						ti.msp1_submsp1_sub_org1_id, root_cloud_id, SpogConstants.SUCCESS_GET_PUT_DELETE,
						SpogMessageCode.SUCCESS_GET_PUT_DEL, ti.msp1_submsp1_suborg1_user1_token, null },
				{ "submsp_sub-maa - valid scenario to get group by id ", spogServer.ReturnRandom("spog_getgroup_byid"),
						spogServer.ReturnRandom("subuser_group"), ti.msp1_submsp1_suborg1_user1_token,
						ti.msp1_submsp1_sub_org1_id, root_cloud_id, SpogConstants.SUCCESS_GET_PUT_DELETE,
						SpogMessageCode.SUCCESS_GET_PUT_DEL, ti.root_msp1_submsp1_user1_token, null },

				// invalid scenarios
				{ "invalidToken - invalid scenario to get group by id ", spogServer.ReturnRandom("spog_getgroup_byid"),
						spogServer.ReturnRandom("subuser_group"), ti.direct_org1_user1_token, ti.direct_org1_id,
						Direct_cloud_id, SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, " asdf", null },
				{ "missing token - invalid scenario to get group by id ", spogServer.ReturnRandom("spog_getgroup_byid"),
						spogServer.ReturnRandom("subuser_group"), ti.direct_org1_user1_token, ti.direct_org1_id,
						Direct_cloud_id, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED, "",
						null },
				{ "Direct-rootmsp - invalid scenario to get group by id ",
						spogServer.ReturnRandom("spog_getgroup_byid"), spogServer.ReturnRandom("subuser_group"),
						ti.direct_org1_user1_token, ti.direct_org1_id, Direct_cloud_id,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY,
						ti.root_msp_org1_user1_token, null },
				{ "Direct-sub - invalid scenario to get group by id ", spogServer.ReturnRandom("spog_getgroup_byid"),
						spogServer.ReturnRandom("subuser_group"), ti.direct_org1_user1_token, ti.direct_org1_id,
						Direct_cloud_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY, ti.normal_msp1_suborg1_user1_token, null },
				{ "Direct-submsp - invalid scenario to get group by id ", spogServer.ReturnRandom("spog_getgroup_byid"),
						spogServer.ReturnRandom("subuser_group"), ti.direct_org1_user1_token, ti.direct_org1_id,
						Direct_cloud_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY, ti.root_msp1_submsp1_user1_token, null },
				{ "Direct-submsp_sub - invalid scenario to get group by id ",
						spogServer.ReturnRandom("spog_getgroup_byid"), spogServer.ReturnRandom("subuser_group"),
						ti.direct_org1_user1_token, ti.direct_org1_id, Direct_cloud_id,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY,
						ti.msp1_submsp1_suborg1_user1_token, null },

				{ "suborg-root - invalid scenario to get group by id ", spogServer.ReturnRandom("spog_getgroup_byid"),
						spogServer.ReturnRandom("subuser_group"), ti.normal_msp1_suborg1_user1_token,
						ti.normal_msp1_suborg1_id, msp_cloud_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY, ti.root_msp1_suborg1_user1_token, null },
				{ "suborg-direct - invalid scenario to get group by id ", spogServer.ReturnRandom("spog_getgroup_byid"),
						spogServer.ReturnRandom("subuser_group"), ti.normal_msp1_suborg1_user1_token,
						ti.normal_msp1_suborg1_id, msp_cloud_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY, ti.direct_org1_user1_token, null },
				{ "suborg-submsp - invalid scenario to get group by id ", spogServer.ReturnRandom("spog_getgroup_byid"),
						spogServer.ReturnRandom("subuser_group"), ti.normal_msp1_suborg1_user1_token,
						ti.normal_msp1_suborg1_id, msp_cloud_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY, ti.root_msp1_submsp1_user1_token, null },
				{ "suborg-submsp_sub - invalid scenario to get group by id ",
						spogServer.ReturnRandom("spog_getgroup_byid"), spogServer.ReturnRandom("subuser_group"),
						ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_id, msp_cloud_id,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY,
						ti.msp1_submsp1_suborg1_user1_token, null },

				{ "rootsuborg-direct - invalid scenario to get group by id ",
						spogServer.ReturnRandom("spog_getgroup_byid"), spogServer.ReturnRandom("subuser_group"),
						ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_id, root_cloud_id,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY,
						ti.direct_org1_user1_token, null },
				{ "rootsuborg-msp - invalid scenario to get group by id ",
						spogServer.ReturnRandom("spog_getgroup_byid"), spogServer.ReturnRandom("subuser_group"),
						ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_id, root_cloud_id,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY,
						ti.normal_msp_org1_user1_token, null },
				{ "rootsuborg-submsp - invalid scenario to get group by id ",
						spogServer.ReturnRandom("spog_getgroup_byid"), spogServer.ReturnRandom("subuser_group"),
						ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_id, root_cloud_id,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY,
						ti.root_msp1_submsp1_user1_token, null },
				{ "rootsuborg-submsp_sub - invalid scenario to get group by id ",
						spogServer.ReturnRandom("spog_getgroup_byid"), spogServer.ReturnRandom("subuser_group"),
						ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_id, root_cloud_id,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY,
						ti.msp1_submsp1_suborg1_user1_token, null },
				{ "rootsuborg-submsp_maa - invalid scenario to get group by id ",
						spogServer.ReturnRandom("spog_getgroup_byid"), spogServer.ReturnRandom("subuser_group"),
						ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_id, root_cloud_id,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY,
						ti.root_msp1_submsp1_account_admin_token, null },
				{ "submsp_sub-root - invalid scenario to get group by id ",
						spogServer.ReturnRandom("spog_getgroup_byid"), spogServer.ReturnRandom("subuser_group"),
						ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_sub_org1_id, root_cloud_id,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY,
						ti.root_msp_org1_user1_token, null },
				{ "submsp_sub-root_sub - invalid scenario to get group by id ",
						spogServer.ReturnRandom("spog_getgroup_byid"), spogServer.ReturnRandom("subuser_group"),
						ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_sub_org1_id, root_cloud_id,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY,
						ti.root_msp1_suborg1_user1_token,null },
				{ "submsp_sub-direct - invalid scenario to get group by id ",
						spogServer.ReturnRandom("spog_getgroup_byid"), spogServer.ReturnRandom("subuser_group"),
						ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_sub_org1_id, root_cloud_id,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY,
						ti.direct_org1_user1_token, null },
				{ "submsp_sub-msp - invalid scenario to get group by id ",
						spogServer.ReturnRandom("spog_getgroup_byid"), spogServer.ReturnRandom("subuser_group"),
						ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_sub_org1_id, root_cloud_id,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY,
						ti.normal_msp_org1_user1_token, null },
				{ "submsp_sub-sub - invalid scenario to get group by id ",
						spogServer.ReturnRandom("spog_getgroup_byid"), spogServer.ReturnRandom("subuser_group"),
						ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_sub_org1_id, root_cloud_id,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY,
						ti.normal_msp1_suborg1_user1_token, null },
				{ "submsp_sub-maa - invalid scenario to get group by id ",
						spogServer.ReturnRandom("spog_getgroup_byid"), spogServer.ReturnRandom("subuser_group"),
						ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_sub_org1_id, root_cloud_id,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY,
						ti.root_msp_org1_msp_accountadmin1_token, null },
				{ "Direct- random UUID as group_id - invalid scenario to get group by id ",
						spogServer.ReturnRandom("spog_getgroup_byid"), spogServer.ReturnRandom("subuser_group"),
						ti.direct_org1_user1_token, ti.direct_org1_id, Direct_cloud_id,
						SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.SOURCE_GROUP_NOT_EXIST,
						ti.direct_org1_user1_token, UUID.randomUUID().toString() },

		};
	}

	@Test(dataProvider = "groupInfo2")
	public void getGroupById(String testcase, String groupName, String groupDescription, String validToken,
			String org_id, String cloud_id, int errorCode, SpogMessageCode errorMessage, String invalidToken,
			String group_id) {
		test.log(LogStatus.INFO, "Testcase" + testcase);

		String[] arrayofsourcenodes = new String[4];
		HashMap<String, Object> status = new HashMap<String, Object>();

		String sourceName = "";
	

		spogServer.setToken(validToken);
		for (int i = 0; i < 4; i++) {
			sourceName = spogServer.ReturnRandom("ramya");
			arrayofsourcenodes[i] = spogServer.createSourceWithCheck(sourceName + "_" + i, SourceType.machine,
					SourceProduct.udp, org_id, cloud_id, ProtectionStatus.unprotect, ConnectionStatus.online, "windows",
					"SQLSERVER", test);
			num_unprotected++;
			num_total++;
		}
		if (group_id == null) {
			group_id = spogServer.createGroupWithCheck2(validToken, org_id, groupName, groupDescription, test);
			test.log(LogStatus.INFO, "The value of the group_id:" + group_id);
			
			response = spogServer.addSourcetoSourceGroupwithCheck(group_id, arrayofsourcenodes, validToken,
					SpogConstants.SUCCESS_POST, SpogMessageCode.SUCCESS_POST, test);
		}

		status.put("num_total", num_total);
		status.put("num_protected", num_protected);
		status.put("num_unprotected", num_unprotected);
		status.put("num_partial_protected", num_partial_protected);
		

		test.log(LogStatus.INFO, "get group by id : " + group_id);
		response = spogServer.getGroupById(invalidToken, group_id, test);
		spogServer.checkGetGroupById(response, status, errorCode, group_id, groupName, groupDescription, org_id,
				errorMessage, test);

		num_protected = 0;
		num_unprotected = 0;
		num_total = 0;
		num_partial_protected = 0;

		test.log(LogStatus.INFO, "delete created sources");

		for (String arrayofsourcenode : arrayofsourcenodes)
			spogServer.deleteSourceByID(arrayofsourcenode, test);

		test.log(LogStatus.INFO, "delete created group");
		spogServer.deleteGroup(group_id, test);
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

}
