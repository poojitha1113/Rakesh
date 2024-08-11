package api.jobs;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Base64.Base64Coder;
import Constants.ConnectionStatus;
import Constants.DestinationStatus;
import Constants.DestinationType;
import Constants.JobStatus;
import Constants.Job_method;
import Constants.OSMajor;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import Constants.volume_type;
import InvokerServer.GatewayServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.ServerResponseCode;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import InvokerServer.GatewayServer.siteType;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;

/**
 * Test class to test the API - GET: jobs/{id}/data
 * 
 * Prerequisites: Csr token Direct, MSP, Sub organizations and users Sources,
 * Sites and CD Destinations Jobs posted
 * 
 * @author Rakesh.Chalamala refractored on 22/12/2018
 *
 */
public class GetJobDatabyJobIdTest extends base.prepare.Is4Org {
	private SPOGDestinationServer spogDestinationServer;
	private UserSpogServer userSpogServer;

	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	// public int Nooftest;
	private ExtentTest test;
	private String org_model_prefix = this.getClass().getSimpleName();
	private String[] datacenters;
	private TestOrgInfo ti;
	private Response response;
	private String direct_cloud_id;
	private String msp_cloud_id;
	private String direct_baas_destionation_ID;
	private String sub_orga_baas_destionation_ID;
	private String submsp_suborg_baas_destionation_ID;

	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine, String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(), logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Kanamarlapudi, Chandra Kanth";

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
		ti = new TestOrgInfo(spogServer, test);

		response = spogServer.getCloudAccounts(ti.direct_org1_user1_token, "", test);
		direct_cloud_id = response.then().extract().path("data[0].cloud_account_id");

		response = spogServer.getCloudAccounts(ti.root_msp_org1_user1_token, "", test);
		msp_cloud_id = response.then().extract().path("data[0].cloud_account_id");

		spogDestinationServer.setToken(ti.csr_token);
		datacenters = spogDestinationServer.getDestionationDatacenterID();

		response = spogDestinationServer.getDestinations(ti.direct_org1_user1_token, "destination_type="+DestinationType.cloud_direct_volume, test);
		direct_baas_destionation_ID = response.then().extract().path("data[0].destination_id");

		response = spogDestinationServer.getDestinations(ti.root_msp1_suborg1_user1_token, "destination_type="+DestinationType.cloud_direct_volume, test);
		sub_orga_baas_destionation_ID = response.then().extract().path("data[0].destination_id");

		response = spogDestinationServer.getDestinations(ti.msp1_submsp1_suborg1_user1_token, "destination_type="+DestinationType.cloud_direct_volume, test);
		submsp_suborg_baas_destionation_ID = response.then().extract().path("data[0].destination_id");

	}

	@DataProvider(name = "JobInfo")
	public final Object[][] JobInfo() {
		return new Object[][] {
				// 200
				{ "Get job data by job_id with direct user token", ti.direct_org1_user1_token,
						ti.direct_org1_user1_token, ti.direct_org1_id, direct_cloud_id, direct_baas_destionation_ID,
						System.currentTimeMillis(), System.currentTimeMillis() + 360000, "backup", "incremental",
						"finished", SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get job data by job_id with csr user token", ti.direct_org1_user1_token, ti.csr_token,
						ti.direct_org1_id, direct_cloud_id, direct_baas_destionation_ID, System.currentTimeMillis(),
						System.currentTimeMillis() + 360000, "backup", "incremental", "finished",
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get job data by job_id with csr readonly user token", ti.direct_org1_user1_token, ti.csr_readonly_token,
						ti.direct_org1_id, direct_cloud_id, direct_baas_destionation_ID, System.currentTimeMillis(),
						System.currentTimeMillis() + 360000, "backup", "incremental", "finished",
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },

				{ "Get job data by job_id with suborg user token", ti.root_msp1_suborg1_user1_token,
						ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_id, msp_cloud_id,
						sub_orga_baas_destionation_ID, System.currentTimeMillis(), System.currentTimeMillis() + 360000,
						"backup", "incremental", "finished", SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get job data by job_id with msp user token in sub organization", ti.root_msp1_suborg1_user1_token,
						ti.root_msp_org1_user1_token, ti.root_msp1_suborg1_id, msp_cloud_id,
						sub_orga_baas_destionation_ID, System.currentTimeMillis(), System.currentTimeMillis() + 360000,
						"backup", "incremental", "finished", SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get job data by job_id with msp account admin token in sub organization",
						ti.root_msp1_suborg1_user1_token, ti.root_msp_org1_msp_accountadmin1_token,
						ti.root_msp1_suborg1_id, msp_cloud_id, sub_orga_baas_destionation_ID,
						System.currentTimeMillis(), System.currentTimeMillis() + 360000, "backup", "incremental",
						"finished", SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get job data by job_id with csr user token", ti.root_msp1_suborg1_user1_token, ti.csr_token,
						ti.root_msp1_suborg1_id, msp_cloud_id, sub_orga_baas_destionation_ID,
						System.currentTimeMillis(), System.currentTimeMillis() + 360000, "backup", "incremental",
						"finished", SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get job data by job_id with csr readonly user token", ti.root_msp1_suborg1_user1_token,
						ti.csr_readonly_token, ti.root_msp1_suborg1_id, msp_cloud_id, sub_orga_baas_destionation_ID,
						System.currentTimeMillis(), System.currentTimeMillis() + 360000, "backup", "incremental",
						"finished", SpogConstants.SUCCESS_GET_PUT_DELETE, null },

				{ "Get job data by job_id with submsp suborg user token", ti.msp1_submsp1_suborg1_user1_token,
						ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_sub_org1_id, msp_cloud_id,
						submsp_suborg_baas_destionation_ID, System.currentTimeMillis(),
						System.currentTimeMillis() + 360000, "backup", "incremental", "finished",
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get job data by job_id with msp user token in sub organization", ti.msp1_submsp1_suborg1_user1_token,
						ti.root_msp1_submsp1_user1_token, ti.msp1_submsp1_sub_org1_id, msp_cloud_id,
						submsp_suborg_baas_destionation_ID, System.currentTimeMillis(),
						System.currentTimeMillis() + 360000, "backup", "incremental", "finished",
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get job data by job_id with msp account admin token in sub organization",
						ti.msp1_submsp1_suborg1_user1_token, ti.root_msp1_submsp1_account_admin_token,
						ti.msp1_submsp1_sub_org1_id, msp_cloud_id, submsp_suborg_baas_destionation_ID,
						System.currentTimeMillis(), System.currentTimeMillis() + 360000, "backup", "incremental",
						"finished", SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get job data by job_id with csr user token", ti.msp1_submsp1_suborg1_user1_token, ti.csr_token,
						ti.msp1_submsp1_sub_org1_id, msp_cloud_id, submsp_suborg_baas_destionation_ID,
						System.currentTimeMillis(), System.currentTimeMillis() + 360000, "backup", "incremental",
						"finished", SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get job data by job_id with csr readonly user token", ti.msp1_submsp1_suborg1_user1_token,
						ti.csr_readonly_token, ti.msp1_submsp1_sub_org1_id, msp_cloud_id,
						submsp_suborg_baas_destionation_ID, System.currentTimeMillis(),
						System.currentTimeMillis() + 360000, "backup", "incremental", "finished",
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },

				// 403
				{ "Get job data by job_id with msp user token in direct organization", ti.direct_org1_user1_token,
						ti.root_msp_org1_user1_token, ti.direct_org1_id, direct_cloud_id, direct_baas_destionation_ID,
						System.currentTimeMillis(), System.currentTimeMillis() + 360000, "backup", "incremental",
						"finished", SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get job data by job_id with suborg user token in direct organization", ti.direct_org1_user1_token,
						ti.root_msp1_suborg1_user1_token, ti.direct_org1_id, direct_cloud_id,
						direct_baas_destionation_ID, System.currentTimeMillis(), System.currentTimeMillis() + 360000,
						"backup", "incremental", "finished", SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get job data by job_id with msp account admin token in direct organization",
						ti.direct_org1_user1_token, ti.root_msp_org1_msp_accountadmin1_token, ti.direct_org1_id,
						direct_cloud_id, direct_baas_destionation_ID, System.currentTimeMillis(),
						System.currentTimeMillis() + 360000, "backup", "incremental", "finished",
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get job data by job_id with direct user token in sub organization", ti.root_msp1_suborg1_user1_token,
						ti.direct_org1_user1_token, ti.root_msp1_suborg1_id, msp_cloud_id,
						sub_orga_baas_destionation_ID, System.currentTimeMillis(), System.currentTimeMillis() + 360000,
						"backup", "incremental", "finished", SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get job data by job_id with suborg2 user token in sub organization",
						ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg2_user1_token, ti.root_msp1_suborg1_id,
						msp_cloud_id, sub_orga_baas_destionation_ID, System.currentTimeMillis(),
						System.currentTimeMillis() + 360000, "backup", "incremental", "finished",
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get job data by job_id with root msp user token in sub msp sub organization", ti.msp1_submsp1_suborg1_user1_token,
							ti.root_msp_org1_user1_token, ti.msp1_submsp1_sub_org1_id, msp_cloud_id,
							submsp_suborg_baas_destionation_ID, System.currentTimeMillis(), System.currentTimeMillis() + 360000,
							"backup", "incremental", "finished", SpogConstants.INSUFFICIENT_PERMISSIONS,
							SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get job data by job_id with suborg1 of rootmsp user token in submsp sub organization",
							ti.msp1_submsp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_token, ti.msp1_submsp1_sub_org1_id,
							msp_cloud_id, submsp_suborg_baas_destionation_ID, System.currentTimeMillis(),
							System.currentTimeMillis() + 360000, "backup", "incremental", "finished",
							SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY }, };
	}

	@Test(dataProvider = "JobInfo")
	public void getJobDatabyJobId_200(String caseType, String validToken, // used to post data
			String otherToken, // used to call the get api
			// String site_token,
			String organization_id, String site_id, String destination_id, long startTimeTS, long endTimeTS,
			String jobType, String jobMethod, String jobStatus, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.log(LogStatus.INFO, caseType);

		String source_id = null, job_id = null, policy_id = UUID.randomUUID().toString(),
				rps_id = UUID.randomUUID().toString();
		ArrayList<HashMap<String, Object>> jobsandjobdata = new ArrayList<>();
		HashMap<String, Object> job_data = null;

		spogServer.setToken(validToken);
		response = spogServer.createSource(spogServer.ReturnRandom("src"), SourceType.machine, SourceProduct.cloud_direct, organization_id, site_id, ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(), "",  test);
		source_id = response.then().extract().path("data.source_id");
		
		job_id = gatewayServer.postJobWithCheck(startTimeTS, endTimeTS, organization_id, source_id, source_id, rps_id,
				destination_id, policy_id, jobType, jobMethod, jobStatus, validToken, test);
		test.log(LogStatus.INFO, "The job id is " + job_id);

		response = spogServer.getJobsById(validToken, job_id, test);

		job_data = spogServer.composeJobData(response, test);
		jobsandjobdata.add(job_data);

		test.log(LogStatus.INFO, "Post Jobs data to site under org ");
		response = gatewayServer.postJobData(job_id, "1", "success", "99", "0", "0", "0", "0", "0", validToken, test);
		job_data = spogServer.composeJobData(response, test);
		jobsandjobdata.add(job_data);

		spogServer.getjobdatabyjobIdwithcheck(otherToken, job_id, expectedStatusCode, jobsandjobdata,
				expectedErrorMessage, test);
		
		spogServer.setToken(ti.csr_token);
		spogServer.deleteSourceByID(source_id, test);
	}

	@DataProvider(name = "JobInfo1")
	public final Object[][] JobInfo1() {
		return new Object[][] {
				// 400
				{ "Get job data by job_id with direct user token & invalid job_id", ti.direct_org1_user1_token,
						"invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID },
				{ "Get job data by job_id with suborg user token & invalid job_id", ti.root_msp1_suborg1_user1_token,
						"invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID },
				{ "Get job data by job_id with msp user token & invalid job_id", ti.root_msp_org1_user1_token,
						"invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID },
				{ "Get job data by job_id with msp account admin token & invalid job_id",
						ti.root_msp_org1_msp_accountadmin1_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_IS_NOT_UUID },
				{ "Get job data by job_id with sub msp suborg user token & invalid job_id",
						ti.msp1_submsp1_suborg1_user1_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_IS_NOT_UUID },
				{ "Get job data by job_id with sub msp user token & invalid job_id", ti.root_msp1_submsp1_user1_token,
						"invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID },
				{ "Get job data by job_id with sub msp account admin token & invalid job_id",
						ti.root_msp1_submsp1_account_admin_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_IS_NOT_UUID },
				{ "Get job data by job_id with csr token & invalid job_id", ti.csr_token, "invalid",
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID },
				{ "Get job data by job_id with csr_readonly token & invalid job_id", ti.csr_readonly_token, "invalid",
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID },

				// 401
				{ "Get job data by job_id with invalid token", "invalid", UUID.randomUUID().toString(),
						SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT },
				{ "Get job data by job_id with missing token", "", UUID.randomUUID().toString(),
						SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED },
				{ "Get job data by job_id with null as token", null, UUID.randomUUID().toString(),
						SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED },

				// 404
				{ "Get job data by job_id with direct user token & job_id that does not exist",
						ti.direct_org1_user1_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST,
						SpogMessageCode.JOB_NOT_FOUND },
				{ "Get job data by job_id with suborg user token & job_id that does not exist",
						ti.root_msp1_suborg1_user1_token, UUID.randomUUID().toString(),
						SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.JOB_NOT_FOUND },
				{ "Get job data by job_id with msp user token & job_id that does not exist",
						ti.root_msp_org1_user1_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST,
						SpogMessageCode.JOB_NOT_FOUND },
				{ "Get job data by job_id with msp account admin token & job_id that does not exist",
						ti.root_msp_org1_msp_accountadmin1_token, UUID.randomUUID().toString(),
						SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.JOB_NOT_FOUND },
				{ "Get job data by job_id with sub msp suborg user token & job_id that does not exist",
						ti.msp1_submsp1_suborg1_user1_token, UUID.randomUUID().toString(),
						SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.JOB_NOT_FOUND },
				{ "Get job data by job_id with sub msp user token & job_id that does not exist",
						ti.root_msp1_submsp1_user1_token, UUID.randomUUID().toString(),
						SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.JOB_NOT_FOUND },
				{ "Get job data by job_id with sub msp account admin token & job_id that does not exist",
						ti.root_msp1_submsp1_account_admin_token, UUID.randomUUID().toString(),
						SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.JOB_NOT_FOUND },
				{ "Get job data by job_id with csr token & job_id that does not exist", ti.csr_token,
						UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.JOB_NOT_FOUND },
				{ "Get job data by job_id with csr_readonly token & job_id that does not exist", ti.csr_readonly_token,
						UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.JOB_NOT_FOUND },

		};
	}

	@Test(dataProvider = "JobInfo1")
	public void getJobDatabyJobId_Invalid(String caseType, String token, String job_id, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.log(LogStatus.INFO, caseType);

		spogServer.getjobdatabyjobIdwithcheck(token, job_id, expectedStatusCode, null, expectedErrorMessage, test);

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
		// endTest(logger) : It ends the current test and prepares to create HTML report
		rep.endTest(test);
		// rep.flush();
	}

}
