package api.policies;

import static org.testng.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.DestinationStatus;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import Constants.volume_type;
import InvokerServer.GatewayServer;
import InvokerServer.Org4SPOGServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGCloudRPSServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGHypervisorsServer;
import InvokerServer.SPOGServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class EnableSpecifiedOrgPoliciesTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private SPOGDestinationServer spogDestinationServer;
	private Policy4SPOGServer policy4SPOGServer;
	private SPOGHypervisorsServer spogHypervisorsServer;
	private Org4SPOGServer org4SpogServer;
	private SPOGCloudRPSServer spogcloudRPSServer;

	private TestOrgInfo ti;
	private ExtentTest test;

	private String cloud_rps_name;
	private String cloud_rps_port;
	private String cloud_rps_protocol;
	private String cloud_rps_password;
	private String cloud_rps_username;
	private String csr_site_id;
	private String csr_site_name;
	private String rps_server_name, rps_server_id;

	String datacenters[] = new String[5];
	private String Direct_cloud_id;
	private String msp_cloud_id;
	private String root_cloud_id;


	@BeforeClass
	@Parameters({ "baseURI", "port","logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine,
			String buildVersion) {

		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		policy4SPOGServer = new Policy4SPOGServer(baseURI, port);
		spogHypervisorsServer = new SPOGHypervisorsServer(baseURI, port);
		org4SpogServer = new Org4SPOGServer(baseURI, port);
		spogcloudRPSServer = new SPOGCloudRPSServer(baseURI, port);

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

		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream("src\\CloudRPS.properties"));

			cloud_rps_name = prop.getProperty("cloud_rps_name");
			cloud_rps_port = prop.getProperty("cloud_rps_port");
			cloud_rps_protocol = prop.getProperty("cloud_rps_protocol");
			cloud_rps_username = prop.getProperty("cloud_rps_username");
			cloud_rps_password = prop.getProperty("cloud_rps_password");
			csr_site_id = prop.getProperty("csr_site_id");
			csr_site_name = prop.getProperty("csr_site_name");

		} catch (IOException e) {
			e.printStackTrace();
			test.log(LogStatus.INFO, "Failed to load RPS properties file");
		}
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

		response = spogServer.getLoggedInUser(ti.csr_token, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		ti.csr_admin_user_id = response.then().extract().path("data.user_id");
		String csr_organization_id = response.then().extract().path("data.organization_id");
		String csr_organizationEmail = response.then().extract().path("data.email");

		spogDestinationServer.setToken(ti.csr_token);
		datacenters=spogDestinationServer.getDestionationDatacenterID();
		// create cloud rps with csr token
		spogcloudRPSServer.setToken(ti.csr_token);
		test.log(LogStatus.INFO, " create cloud rps with csr token");
		response = spogcloudRPSServer.createCloudRPS(cloud_rps_name, cloud_rps_port, cloud_rps_protocol,
				cloud_rps_username, cloud_rps_password, csr_organization_id, csr_site_id, datacenters[0], ti.csr_token);
		rps_server_name = response.then().extract().path("data.server_name");
		rps_server_id = response.then().extract().path("data.server_id");

	}

	@DataProvider(name = "datastore_info")
	public final Object[][] datastore_info() {
		return new Object[][] {
				{ "Direct", ti.direct_org1_id, spogServer.ReturnRandom("destination"), "", datacenters[0], "0.001" },
				{ "sub", ti.normal_msp1_suborg1_id, spogServer.ReturnRandom("destination"), "", datacenters[0],
						"0.001" },

				{ "rootsub", ti.root_msp1_suborg1_id, spogServer.ReturnRandom("destination"), "", datacenters[0],
						"0.001" },
				{ "submsp_sub", ti.msp1_submsp1_sub_org1_id, spogServer.ReturnRandom("destination"), "", datacenters[0],
						"0.001" },

		};
	}

	@Test(dataProvider = "datastore_info")
	public void createCloudHybridPolicies(String org_type, String organization_id, String destination_name,
			String volume, String datacenter_id, String capacity) {
		test.log(LogStatus.INFO, "create CH policy for org :" + org_type);

		HashMap<String, Object> hybridStoreInfo = spogcloudRPSServer.composeCloudHybridStoreInfo(ti.csr_token,
				destination_name, rps_server_id, organization_id, volume, datacenter_id, capacity);

		spogcloudRPSServer.createCloudHybridStoreWithCheck(ti.csr_token, hybridStoreInfo, SpogConstants.SUCCESS_POST,
				SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
	}

	@DataProvider(name = "org_info")
	public final Object[][] org_info() {
		return new Object[][] {
				{ "Valid - direct org policies", "Direct", ti.direct_org1_id, ti.direct_org1_user1_token,
						SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL },

				{ "Valid CSR - direct org  policies", "Direct", ti.direct_org1_id, ti.csr_token,
						SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL },

				{ "Valid - sub org policies with MSP token", "MSP", ti.normal_msp1_suborg1_id,
						ti.normal_msp_org1_user1_token, SpogConstants.SUCCESS_GET_PUT_DELETE,
						SpogMessageCode.SUCCESS_GET_PUT_DEL },
				{ "Valid - sub org  policies", "Sub", ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token,
						SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL },
				{ "Valid CSR - sub org  policies", "Sub", ti.normal_msp1_suborg1_id, ti.csr_token,
						SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL },
				// 3 tier cases
				{ "Valid - rootsub org policies with root MSP token", "root_msp", ti.root_msp1_suborg1_id,
						ti.root_msp_org1_user1_token, SpogConstants.SUCCESS_GET_PUT_DELETE,
						SpogMessageCode.SUCCESS_GET_PUT_DEL },
				{ "Valid - rootsub org  policies", "rootsub", ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_token,
						SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL },
				{ "Valid CSR - rootsub org  policies", "rootsub", ti.root_msp1_suborg1_id, ti.csr_token,
						SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL },

				{ "Valid - rootsub org policies with root maa token", "root_msp", ti.root_msp1_suborg1_id,
						ti.root_msp_org1_msp_accountadmin1_token, SpogConstants.SUCCESS_GET_PUT_DELETE,
						SpogMessageCode.SUCCESS_GET_PUT_DEL },
				{ "Valid - submsp_sub org  policies", "submsp_sub", ti.msp1_submsp1_sub_org1_id,
						ti.msp1_submsp1_suborg1_user1_token, SpogConstants.SUCCESS_GET_PUT_DELETE,
						SpogMessageCode.SUCCESS_GET_PUT_DEL },
				{ "Valid CSR - submsp_sub org  policies with csr token", "submsp_sub", ti.msp1_submsp1_sub_org1_id,
						ti.csr_token, SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL },
				{ "Valid - submsp_sub org  policies with submsp maa token", "submsp_sub", ti.msp1_submsp1_sub_org1_id,
						ti.root_msp1_submsp1_account_admin_token, SpogConstants.SUCCESS_GET_PUT_DELETE,
						SpogMessageCode.SUCCESS_GET_PUT_DEL },
				{ "Valid - submsp_sub org  policies with submsp token", "submsp_sub", ti.msp1_submsp1_sub_org1_id,
						ti.root_msp1_submsp1_user1_token, SpogConstants.SUCCESS_GET_PUT_DELETE,
						SpogMessageCode.SUCCESS_GET_PUT_DEL },

				// 403
				{ "invalid CSR Read Only-  direct org policies", "Direct", ti.direct_org1_id, ti.csr_readonly_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "invalid CSR Read Only- sub org policies", "Sub", ti.normal_msp1_suborg1_id, ti.csr_readonly_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },

				{ "invalid - direct org policies with other direct org", "Direct", ti.direct_org1_id,
						ti.direct_org2_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "invalid - direct org policies with  msp org", "Direct", ti.direct_org1_id,
						ti.normal_msp_org1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "invalid - direct org policies with  sub org", "Direct", ti.direct_org1_id,
						ti.normal_msp1_suborg1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },

				{ "invalid - Sub org policies with  direct org", "Sub", ti.normal_msp1_suborg1_id,
						ti.direct_org2_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "invalid - Sub org policies with  msp org", "Sub", ti.normal_msp1_suborg1_id,
						ti.normal_msp_org2_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "invalid - Sub org policies with other sub org", "Sub", ti.normal_msp1_suborg1_id,
						ti.normal_msp1_suborg2_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },

				{ "invalid token- direct org policies", "Direct", ti.direct_org1_id, "abc", SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT },
				{ "invalid token- sub org policies", "Sub", ti.normal_msp1_suborg1_id, "", SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTHENTICATION_FAILED },

				// 3 tier cases
				{ "invalid CSR Read Only- rootsub org policies", "rootsub", ti.root_msp1_suborg1_id,
						ti.csr_readonly_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "invalid - direct org policies with root msp org", "Direct", ti.direct_org1_id,
						ti.root_msp_org1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "invalid - direct org policies with  rootsub org", "Direct", ti.direct_org1_id,
						ti.root_msp1_suborg1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },

				{ "invalid - rootsub org policies with  direct org", "rootsub", ti.root_msp1_suborg1_id,
						ti.direct_org2_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "invalid - rootsub org policies with  rootmsp org", "rootsub", ti.root_msp1_suborg1_id,
						ti.root_msp_org2_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "invalid - rootsub org policies with other rootsub org", "rootsub", ti.root_msp1_suborg1_id,
						ti.root_msp1_suborg2_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "invalid - rootsub org policies with submsp_sub org", "rootsub", ti.root_msp1_suborg1_id,
						ti.msp1_submsp1_suborg1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "invalid - rootsub org policies with submsp org", "rootsub", ti.root_msp1_suborg1_id,
						ti.root_msp1_submsp1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },

				{ "invalid - submsp_sub org policies with  direct org", "submsp_sub", ti.msp1_submsp1_sub_org1_id,
						ti.direct_org2_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "invalid - submsp_sub org policies with  rootmsp org", "submsp_sub", ti.msp1_submsp1_sub_org1_id,
						ti.root_msp_org2_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "invalid - submsp_sub org policies with other rootsub org", "submsp_sub", ti.msp1_submsp1_sub_org1_id,
						ti.root_msp1_suborg2_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "invalid - submsp_sub org policies with other submsp_sub org", "submsp_sub",
						ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp2_suborg1_user1_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "invalid - submsp_sub org policies with other submsp org", "submsp_sub", ti.msp1_submsp1_sub_org1_id,
						ti.root_msp1_submsp2_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },

		};
	}

	@Test(dataProvider = "org_info", dependsOnMethods = "createCloudHybridPolicies")
	public void enableSpecifiedOrgPolicies(String testcase, String org_type, String organization_id, String validtoken,
			int ErrorCode, SpogMessageCode errormessage) {

		ArrayList<HashMap> policies = new ArrayList<HashMap>();
		Response response = null;
		
		test.log(LogStatus.INFO, testcase);

		if (testcase.contains("Valid")) {

			test.log(LogStatus.INFO, "disable the policies of org and check the status");
			policy4SPOGServer.disableSpecifiedOrgPolicies(validtoken, organization_id, ErrorCode, test);

			response = policy4SPOGServer.getPolicies(validtoken, "organization_id=" + organization_id);
			policies = response.then().extract().path("data");

			if (!policies.isEmpty()) {
				policies.stream().forEach(policy -> {
					assertEquals("disabled", policy.get("usage_status").toString());
				});
			}

			test.log(LogStatus.INFO, "enable policies of org type : " + org_type);
			policy4SPOGServer.enableSpecifiedOrgPoliciesWithCheck(validtoken, organization_id, ErrorCode, errormessage,
					test);

			test.log(LogStatus.INFO, "get the policies of org and check the status");
			response = policy4SPOGServer.getPolicies(validtoken, "organization_id=" + organization_id);
			policies = response.then().extract().path("data");

			if (!policies.isEmpty()) {
				policies.stream().forEach(policy -> {
					assertEquals("enabled", policy.get("usage_status").toString());
				});
			}

		} else {
			test.log(LogStatus.INFO, testcase);
			policy4SPOGServer.enableSpecifiedOrgPoliciesWithCheck(validtoken, organization_id, ErrorCode, errormessage,
					test);
		}

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

	@AfterClass
	public void updatebd() {

		ArrayList<String> policies = new ArrayList<String>();

		test.log(LogStatus.INFO, "get the policies of each org and delete the policies");
		policy4SPOGServer.setToken(ti.direct_org1_user1_token);
		Response response = policy4SPOGServer.getPolicies(null);
		policies = response.then().extract().path("data.policy_id");

		if (!policies.isEmpty()) {
			policies.stream().forEach(policy -> {
				policy4SPOGServer.deletePolicybyPolicyId(ti.csr_token, policy);
			});
		}

		policy4SPOGServer.setToken(ti.normal_msp1_suborg1_user1_token);
		response = policy4SPOGServer.getPolicies(null);
		policies = response.then().extract().path("data.policy_id");

		if (!policies.isEmpty()) {
			policies.stream().forEach(policy -> {
				policy4SPOGServer.deletePolicybyPolicyId(ti.csr_token, policy);
			});
		}

		policy4SPOGServer.setToken(ti.root_msp1_suborg1_user1_token);
		response = policy4SPOGServer.getPolicies(null);
		policies = response.then().extract().path("data.policy_id");

		if (!policies.isEmpty()) {
			policies.stream().forEach(policy -> {
				policy4SPOGServer.deletePolicybyPolicyId(ti.csr_token, policy);
			});
		}
		policy4SPOGServer.setToken(ti.msp1_submsp1_suborg1_user1_token);
		response = policy4SPOGServer.getPolicies(null);
		policies = response.then().extract().path("data.policy_id");

		if (!policies.isEmpty()) {
			policies.stream().forEach(policy -> {
				policy4SPOGServer.deletePolicybyPolicyId(ti.csr_token, policy);
			});
		}

		test.log(LogStatus.INFO, "Get the cloud RPS datastores and delete");
		response = spogcloudRPSServer.getCloudHybridStoresOnSpecifiedRPS(ti.csr_token, rps_server_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		ArrayList<HashMap<String, Object>> datastores = response.then().extract().path("data");
		if (!datastores.isEmpty()) {
			datastores.stream().forEach(datastore -> {
				spogcloudRPSServer.destroyCloudHybridStoreById(ti.csr_token, datastore.get("destination_id").toString(),
						SpogConstants.SUCCESS_GET_PUT_DELETE, test);
				try {
					Thread.sleep(2500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});
		}
		test.log(LogStatus.INFO, "Delete  the RPS");
		spogcloudRPSServer.deleteCloudRPS(rps_server_id, ti.csr_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);

		try {
			if (count1.getfailedcount() > 0) {
				Nooftest = count1.getpassedcount() + count1.getfailedcount() + count1.getskippedcount();
				bqdb1.updateTable(this.BQName, this.runningMachine, this.buildVersion, String.valueOf(Nooftest),
						Integer.toString(count1.getpassedcount()), Integer.toString(count1.getfailedcount()),
						String.valueOf(count1.getskippedcount()), count1.getcreationtime(), "Failed");
			} else {
				Nooftest = count1.getpassedcount() + count1.getfailedcount() + count1.getskippedcount();
				bqdb1.updateTable(this.BQName, this.runningMachine, this.buildVersion, String.valueOf(Nooftest),
						Integer.toString(count1.getpassedcount()), Integer.toString(count1.getfailedcount()),
						String.valueOf(count1.getskippedcount()), count1.getcreationtime(), "Passed");
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String orgHasString = this.getClass().getSimpleName();
		System.out.println(orgHasString);
		System.out.println("in father afterclass");
		System.out.println("class in father is:" + orgHasString);
		System.out.println("in father after class");
		destroyOrg(orgHasString);
	}

}
