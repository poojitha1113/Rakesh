package api.organizations.freetrial;

import java.io.IOException;
import java.text.SimpleDateFormat;
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

import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;

public class PostCloudHybridTrailRequestTest extends base.prepare.Is4Org {

	private SPOGServer spogServer;
	private UserSpogServer userSpogServer;
	private ExtentTest test;

	private TestOrgInfo ti;

	@BeforeClass
	@Parameters({ "baseURI", "port", "csrAdminUserName", "csrAdminPassword", "csrReadOnlyAdminName",
			"csrReadOnlyAdminPassword", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword,
			String readOnlyUserName, String readOnlyPassword, String logFolder, String runningMachine,
			String buildVersion) {

		spogServer = new SPOGServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);

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
	}

	@DataProvider(name = "PostCloudHybridTrailRequest")
	public final Object[][] PostCloudHybridTrailRequest() {
		return new Object[][] {
				// 200
				{ "Request cloud hybrid trial for direct org with valid token", "direct", ti.direct_org1_user1_token,
						ti.direct_org1_id, SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL },
				{ "Request cloud hybrid trial for msp org with msp token", "msp", ti.normal_msp_org1_user1_token,
						ti.normal_msp_org1_id, SpogConstants.SUCCESS_GET_PUT_DELETE,
						SpogMessageCode.SUCCESS_GET_PUT_DEL },
			/*	{ "Request cloud hybrid trial for sub org with msp token", "sub", ti.normal_msp_org1_user1_token,
						ti.normal_msp1_suborg1_id, SpogConstants.SUCCESS_GET_PUT_DELETE,
						SpogMessageCode.SUCCESS_GET_PUT_DEL },
			*/	{ "Request cloud hybrid trial for direct org with csr token", "direct", ti.csr_token, ti.direct_org1_id,
						SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL },
				{ "Request cloud hybrid trial for msp org with csr token", "msp", ti.csr_token, ti.normal_msp_org1_id,
						SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL },
				/*{ "Request cloud hybrid trial for sub org with csr token", "sub", ti.csr_token,
						ti.normal_msp1_suborg1_id, SpogConstants.SUCCESS_GET_PUT_DELETE,
						SpogMessageCode.SUCCESS_GET_PUT_DEL },
				{ "Request cloud hybrid trial for sub org with ti.normal_msp_org1_msp_accountadmin1_token", "sub",
						ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp1_suborg1_id,
						SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL },
				// 3 tier cases
				{ "Request cloud hybrid trial for root sub org with ti.root_msp_org1_msp_accountadmin1_token", "sub",
						ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp1_suborg1_id,
						SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL },
				*/{ "Request cloud hybrid trial for root sub org with ti.root_msp_org1_user1_token", "root",
						ti.root_msp_org1_user1_token, ti.root_msp_org1_id, SpogConstants.SUCCESS_GET_PUT_DELETE,
						SpogMessageCode.SUCCESS_GET_PUT_DEL },
				/*{ "Request cloud hybrid trial for root sub org with ti.root_msp_org1_user1_token", "sub",
						ti.root_msp_org1_user1_token, ti.root_msp1_suborg2_id, SpogConstants.SUCCESS_GET_PUT_DELETE,
						SpogMessageCode.SUCCESS_GET_PUT_DEL },
				{ "Request cloud hybrid trial for sub msp org with ti.root_msp1_submsp1_user1_token", "sub",
						ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp_org1_id,
						SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL },
				{ "Request cloud hybrid trial for sub msp sub org with ti.root_msp1_submsp1_user1_token", "sub",
						ti.root_msp1_submsp1_user1_token, ti.msp1_submsp1_sub_org2_id,
						SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL },
*/
				// 403 - 3 tier cases
				{ "Request cloud hybrid trial for direct org with ti.root_msp_org1_msp_accountadmin1_token", "direct",
						ti.root_msp_org1_msp_accountadmin1_token, ti.direct_org1_id,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Request cloud hybrid trial for direct org with root sub org token", "direct",
						ti.root_msp1_suborg1_user1_token, ti.direct_org1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Request cloud hybrid trial for direct org with root msp org token", "direct",
						ti.root_msp_org1_user1_token, ti.direct_org1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },

				{ "Request cloud hybrid trial for msp sub org with root msp org token", "direct",
						ti.root_msp_org1_user1_token, ti.normal_msp1_suborg1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Request cloud hybrid trial for rootmsp sub org with other root msp org token", "direct",
						ti.root_msp_org1_user1_token, ti.root_msp2_suborg1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },

				{ "Request cloud hybrid trial for root sub org with root msp org token", "direct",
						ti.root_msp2_suborg1_user1_token, ti.root_msp1_suborg1_id,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
			/*	{ "Request cloud hybrid trial for root sub org with sub msp org token", "direct",
						ti.root_msp1_submsp1_user1_token, ti.root_msp1_suborg1_id,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },*/

				{ "Request cloud hybrid trial for root sub org with sub msp sub org token", "direct",
						ti.msp1_submsp1_suborg1_user1_token, ti.root_msp1_suborg1_id,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },

				{ "Request cloud hybrid trial for root org with sub msp sub org token", "direct",
						ti.msp1_submsp1_suborg1_user1_token, ti.root_msp_org1_id,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },

				{ "Request cloud hybrid trial for root org with root sub org token", "direct",
						ti.root_msp1_suborg1_user1_token, ti.root_msp_org1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },

				{ "Request cloud hybrid trial for root org with submsp org token", "direct",
						ti.root_msp1_submsp1_user1_token, ti.root_msp_org1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Request cloud hybrid trial for root org with submsp org token", "direct",
						ti.msp1_submsp1_suborg1_user1_token, ti.root_msp_org1_id,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },

				// 403
				{ "Request cloud hybrid trial for direct org with ti.normal_msp_org1_msp_accountadmin1_token", "direct",
						ti.normal_msp_org1_msp_accountadmin1_token, ti.direct_org1_id,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Request cloud hybrid trial for direct org with sub org token", "direct",
						ti.normal_msp1_suborg1_user1_token, ti.direct_org1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Request cloud hybrid trial for direct org with msp org token", "direct",
						ti.normal_msp_org1_user1_token, ti.direct_org1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Request cloud hybrid trial for direct org with csr read only token", "direct", ti.csr_readonly_token,
						ti.direct_org1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Request cloud hybrid trial for direct org with other direct org token", "direct",
						ti.direct_org2_user1_token, ti.direct_org1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },

				{ "Request cloud hybrid trial for msp org with ti.normal_msp_org1_msp_accountadmin1_token", "msp",
						ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_id,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Request cloud hybrid trial for msp org with sub org token", "msp",
						ti.normal_msp1_suborg1_user1_token, ti.normal_msp_org1_id,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Request cloud hybrid trial for msp org with other msp org token", "msp",
						ti.normal_msp_org2_user1_token, ti.normal_msp_org1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Request cloud hybrid trial for msp org with csr read only token", "msp", ti.csr_readonly_token,
						ti.normal_msp_org1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },

				{ "Request cloud hybrid trial for sub org with ti.normal_msp_org1_msp_accountadmin1_token", "sub",
						ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp1_suborg2_id,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Request cloud hybrid trial for sub org with sub org token", "sub",
						ti.normal_msp1_suborg2_user1_token, ti.normal_msp1_suborg1_id,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Request cloud hybrid trial for sub org with other msp org token", "sub",
						ti.normal_msp_org2_user1_token, ti.normal_msp1_suborg1_id,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Request cloud hybrid trial for sub org with csr read only token", "sub", ti.csr_readonly_token,
						ti.normal_msp1_suborg1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },

				// 401
				{ "Request cloud hybrid trial for sub org with invalid token", "sub", "abc", ti.normal_msp1_suborg1_id,
						SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT },
				{ "Request cloud hybrid trial for sub org with missed token", "sub", "", ti.normal_msp1_suborg1_id,
						SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED },

				// 404
				{ "Request cloud hybrid trial for sub org with valid token and random UUID as org id", "sub",
						ti.normal_msp1_suborg1_user1_token, UUID.randomUUID().toString(),
						SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED },
				// 400
				{ "Request cloud hybrid trial for sub org with valid token and  as org id", "sub",
						ti.normal_msp1_suborg1_user1_token, "12345", SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_ORGANIZATIONID_IS_NOT_UUID },

		};

	}

	/**
	 * Post Cloud Hybrid Trail Request
	 * 
	 * Prerequisites: Need to create Direct, MSP, Sub organizations to request cloud
	 * hybrid trail for the organizations
	 * 
	 * @param testcase
	 * @param orgType
	 * @param ValidToken
	 * @param Organization_id
	 * @param ExpectedStatusCode
	 * @param ExpectedErrorMessage
	 */
	@Test(dataProvider = "PostCloudHybridTrailRequest")
	public void PostCloudHybridTrailRequest_valid(String testcase, String orgType, String ValidToken,
			String Organization_id, int ExpectedStatusCode, SpogMessageCode ExpectedErrorMessage) {
		test.log(LogStatus.INFO, testcase);

		test.log(LogStatus.INFO, "Request to Cloud Hybrid Trail for the Organization :" + orgType);
		spogServer.postCloudHybridTrailRequest(ValidToken, Organization_id, ExpectedStatusCode, ExpectedErrorMessage,
				test);

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
