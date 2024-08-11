package api.organization.support;

import static org.testng.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
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
import InvokerServer.BrandingSpogSever;
import InvokerServer.GatewayServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGJobServer;
import InvokerServer.SPOGReportServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.CreateOrgsInfo;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class GetOrganizationSupportForSpecifiedOrganizationTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private BrandingSpogSever brandingSpogSever;
	private SPOGJobServer spogJobServer;

	private ExtentTest test;

	private TestOrgInfo ti;

	@BeforeClass
	@Parameters({ "baseURI", "port",  "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine,
			String buildVersion) {

		spogServer = new SPOGServer(baseURI, port);
		spogJobServer = new SPOGJobServer(baseURI, port);
		brandingSpogSever = new BrandingSpogSever(baseURI, port);

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

		spogServer = new SPOGServer(baseURI, port);
		spogJobServer = new SPOGJobServer(baseURI, port);
		brandingSpogSever = new BrandingSpogSever(baseURI, port);
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(), logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();

		Nooftest = 0;
		// Used for creating a build number with dateformat

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
		ti = new TestOrgInfo(spogServer, test);
		spogServer.setToken(ti.csr_token);

	}

	// This information is related to the getOrganizationSupportTest valid
	// scenario
	@DataProvider(name = "getOrganizationSupportForSpecifiedOrganization_valid")
	public final Object[][] getOrganizationSupportForSpecifiedOrganization_valid() {
		return new Object[][] {
				{ ti.csr_readonly_token, "csr", ti.csr_org_id, "Arcserve CSR", "ramya.nagepalli507@arcserve.com",
						"+1 877-469-3882", "https://www.arcserve.com/supportchat",
						"https://www.arcserve.com/saleschat" },
				{ ti.direct_org1_user1_token, "direct", ti.direct_org1_id, ti.direct_org1_name,
						"ramya.nagepalli123@arcserve.com", "+1 877-469-3882", "https://www.arcserve.com/supportchat",
						"https://www.arcserve.com/saleschat" },
				{ ti.normal_msp_org1_user1_token, "msp", ti.normal_msp_org1_id, ti.normal_msp_org1_name,
						"ramya.nagepalli456@arcserve.com", "+1 877-469-3882", "https://www.arcserve.com/supportchat",
						"https://www.arcserve.com/saleschat" },
				{ ti.normal_msp1_suborg1_user1_token, "msp_child", ti.normal_msp1_suborg1_id,
						ti.normal_msp1_suborg1_name, "ramya.nagepalli789@arcserve.com", "+1 877-469-3882",
						"https://www.arcserve.com/supportchat", "https://www.arcserve.com/saleschat" },
				// monitor cases
				{ ti.direct_org1_monitor_user1_token, "direct", ti.direct_org1_id, ti.direct_org1_name,
						"ramya.nagepalli123@arcserve.com", "+1 877-469-3882", "https://www.arcserve.com/supportchat",
						"https://www.arcserve.com/saleschat" },
				{ ti.normal_msp_org1_monitor_user1_token, "msp", ti.normal_msp_org1_id, ti.normal_msp_org1_name,
						"ramya.nagepalli456@arcserve.com", "+1 877-469-3882", "https://www.arcserve.com/supportchat",
						"https://www.arcserve.com/saleschat" },
				{ ti.normal_msp1_suborg1_monitor_user1_token, "msp_child", ti.normal_msp1_suborg1_id,
						ti.normal_msp1_suborg1_name, "ramya.nagepalli789@arcserve.com", "+1 877-469-3882",
						"https://www.arcserve.com/supportchat", "https://www.arcserve.com/saleschat" },
				{ ti.root_msp_org1_monitor_user1_token, "msp", ti.root_msp_org1_id, ti.root_msp_org1_name,
						"ramya.nagepalli456@arcserve.com", "+1 877-469-3882", "https://www.arcserve.com/supportchat",
						"https://www.arcserve.com/saleschat" },
				{ ti.root_msp1_suborg1_monitor_user1_token, "msp_child", ti.root_msp1_suborg1_id,
						ti.root_msp1_suborg1_name, "ramya.nagepalli789@arcserve.com", "+1 877-469-3882",
						"https://www.arcserve.com/supportchat", "https://www.arcserve.com/saleschat" },
				{ ti.root_msp1_submsp1_monitor_user1_token, "msp", ti.root_msp1_submsp_org1_id,
						ti.root_msp1_submsp_org1_name, "ramya.nagepalli789@arcserve.com", "+1 877-469-3882",
						"https://www.arcserve.com/supportchat", "https://www.arcserve.com/saleschat" },
				{ ti.msp1_submsp1_suborg1_monitor_user1_token, "msp_child", ti.msp1_submsp1_sub_org1_id,
						ti.msp1_submsp1_sub_org1_name, "ramya.nagepalli789@arcserve.com", "+1 877-469-3882",
						"https://www.arcserve.com/supportchat", "https://www.arcserve.com/saleschat" }, };
	}

	@Test(dataProvider = "getOrganizationSupportForSpecifiedOrganization_valid")
	public void getOrganizationSupportForSpecifiedOrganization_valid(String adminToken, String organization_type,
			String organization_id, String organization_name, String email, String phone_number, String support_url,
			String sales_url) {

		test = ExtentManager.getNewTest(
				organization_type + "Organization getOrganizationSupportDetailsForLoggedInOrganization_valid");

		test.log(LogStatus.INFO, "create branding for the organization with valid token");
		brandingSpogSever.setToken(adminToken);

		Response response = null;
		String blocked = null;

		if (organization_type.equals("csr")) {
			email = "cloudsupport@arcserve.com";
			phone_number = "+1 877-469-3882";
			support_url = "https://www.arcserve.com/supportchat";
			sales_url = "https://www.arcserve.com/saleschat";

		} else {
			email = "cloudsupport@arcserve.com";
			response = brandingSpogSever.createBrandingemailerForOrganization(organization_id, "", email, "",
					phone_number, support_url, sales_url, "", "", "", "", "", "", "", "", "", "SELF", test);
			blocked = "false";
		}

		test.log(LogStatus.INFO, "get OrganizationSupportDetailsForSpecifiedOrganization with valid token");
		response = spogJobServer.getOrganizationSupportDetailsForSpecifiedOrganization(adminToken, organization_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, test);

		test.log(LogStatus.INFO, "checkOrganizationSupportDetails with valid token");
		spogJobServer.checkOrganizationSupportDetails(response, organization_id, organization_name, organization_type,
				blocked, email, phone_number, support_url, sales_url, SpogConstants.SUCCESS_GET_PUT_DELETE,
				SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

	}

	@DataProvider(name = "getOrganizationSupportForSpecifiedOrganization_invalid")
	public final Object[][] getOrganizationSupportForSpecifiedOrganization_invalid() {
		return new Object[][] {
				{ "Invalid Authorization with junk token", "Junk", ti.direct_org1_name, "direct", ti.direct_org1_id,
						SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT },
				{ "Invalid Authorization with missing token", "", ti.normal_msp_org1_name, "msp", ti.normal_msp_org1_id,
						SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED },

				{ "INSUFFICIENT_PERMISSIONS of Direct_msp", ti.normal_msp_org1_user1_token, ti.direct_org1_name,
						"direct", ti.direct_org1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "INSUFFICIENT_PERMISSIONS of Direct_direct_b", ti.direct_org2_user1_token, ti.direct_org1_name,
						"direct", ti.direct_org1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "INSUFFICIENT_PERMISSIONS of Direct_sub-org", ti.normal_msp1_suborg1_user1_token, ti.direct_org1_name,
						"direct", ti.direct_org1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "INSUFFICIENT_PERMISSIONS of msp-mspb", ti.normal_msp_org2_user1_token, ti.normal_msp_org1_name,
						"msp", ti.normal_msp_org1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "INSUFFICIENT_PERMISSIONS of msp-direct", ti.direct_org2_user1_token, ti.normal_msp_org1_name, "msp",
						ti.normal_msp_org1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "INSUFFICIENT_PERMISSIONS of msp-suborg", ti.normal_msp1_suborg1_user1_token, ti.normal_msp_org1_name,
						"msp", ti.normal_msp_org1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "INSUFFICIENT_PERMISSIONS of suborg-direct", ti.direct_org2_user1_token, ti.normal_msp1_suborg1_name,
						"msp_child", ti.normal_msp1_suborg1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "INSUFFICIENT_PERMISSIONS of suborg-mspb", ti.normal_msp_org2_user1_token, ti.normal_msp1_suborg1_id,
						"msp_child", ti.normal_msp1_suborg1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "INSUFFICIENT_PERMISSIONS of suborg-suborgb", ti.normal_msp1_suborg2_user1_token,
						ti.normal_msp1_suborg1_name, "msp_child", ti.normal_msp1_suborg1_id,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "INSUFFICIENT_PERMISSIONS of csr-direct", ti.direct_org2_user1_token, ti.csr_org_name, "csr",
						ti.csr_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "INSUFFICIENT_PERMISSIONS of csr-mspb", ti.normal_msp_org2_user1_token, ti.csr_org_name, "csr",
						ti.csr_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "INSUFFICIENT_PERMISSIONS of csr-suborgb", ti.normal_msp1_suborg1_user1_token, ti.csr_org_name, "csr",
						ti.csr_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },

				{ "organization_id_invalid_uuid", ti.csr_token, ti.csr_org_name, "csr", "123456",
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_ORGANIZATIONID_IS_NOT_UUID },
				{ "organization_id_random_uuid", ti.csr_token, ti.csr_org_name, "csr", UUID.randomUUID().toString(),
						SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED },

				{ "organization_id_invalid_uuid-csr-read-only", ti.csr_readonly_token, ti.csr_org_name, "csr-read-only",
						"123456", SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_ORGANIZATIONID_IS_NOT_UUID },
				{ "organization_id_random_uuid-csr-read-only", ti.csr_readonly_token, ti.csr_org_name, "csr-read-only",
						UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST,
						SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED },

				// 3 tier architecture cases
				{ "INSUFFICIENT_PERMISSIONS of root msp-suborgb", ti.root_msp1_suborg1_user1_token,
						ti.root_msp_org1_name, "root_msp", ti.root_msp_org1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },

				{ "INSUFFICIENT_PERMISSIONS of submsp1-root_suborgb", ti.root_msp1_submsp1_user1_token,
						ti.root_msp1_suborg1_name, "sub_msp", ti.root_msp1_suborg1_id,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },

				{ "INSUFFICIENT_PERMISSIONS of root_suborgb-submsp1", ti.root_msp1_suborg1_user1_token,
						ti.root_msp1_submsp_org1_name, "root_msp", ti.root_msp1_submsp_org1_id,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },

				{ "INSUFFICIENT_PERMISSIONS of submsp1-submsp1_suborg", ti.msp1_submsp1_suborg1_user1_token,
						ti.root_msp1_submsp_org1_name, "root_msp", ti.root_msp1_submsp_org1_id,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },

				{ "INSUFFICIENT_PERMISSIONS of root_msp-submsp1", ti.root_msp1_submsp1_user1_token,
						ti.root_msp_org1_name, "root_msp", ti.root_msp_org1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },

				{ "INSUFFICIENT_PERMISSIONS of root_msp-submsp1_suborg", ti.msp1_submsp1_suborg1_user1_token,
						ti.root_msp_org1_name, "root_msp", ti.root_msp_org1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },

		};
	}

	@Test(dataProvider = "getOrganizationSupportForSpecifiedOrganization_invalid")
	public void getOrganizationSupportForSpecifiedOrganization_invalid(String invalidTestCase, String InValidToken,
			String organization_name, String organization_type, String organization_id, int ExpectedStatusCode,
			SpogMessageCode Errormessage

	) {

		String blocked = "false";
		test = ExtentManager.getNewTest(
				organization_type + "Organization getOrganizationSupportTest_invalid test case" + invalidTestCase);

		test.log(LogStatus.INFO, "get OrganizationSupportDetailsForSpecifiedOrganization with valid token");
		Response response = spogJobServer.getOrganizationSupportDetailsForSpecifiedOrganization(InValidToken,
				organization_id, ExpectedStatusCode, test);

		test.log(LogStatus.INFO, "checkOrganizationSupportDetails with valid token");
		spogJobServer.checkOrganizationSupportDetails(response, organization_id, organization_name, organization_type,
				blocked, "", "", "", "", ExpectedStatusCode, Errormessage, test);
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
			test.log(LogStatus.SKIP, "Test Case Skipped is " + result.getName());
			count1.setskippedcount();
		} else if (result.getStatus() == ITestResult.SUCCESS) {
			count1.setpassedcount();

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
