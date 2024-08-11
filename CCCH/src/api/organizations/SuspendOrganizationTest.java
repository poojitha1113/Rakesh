package api.organizations;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.ConnectionStatus;
import Constants.DestinationStatus;
import Constants.DestinationType;
import Constants.OSMajor;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import Constants.volume_type;
import InvokerServer.GatewayServer;
import InvokerServer.Org4SPOGServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.ServerResponseCode;
import InvokerServer.UserSpogServer;
import base.prepare.Is4Org;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import invoker.SPOGDestinationInvoker;
import io.restassured.response.Response;
import ui.base.common.SPOGMenuTreePath;

public class SuspendOrganizationTest extends Is4Org{

	private TestOrgInfo ti;
	private SPOGServer spogServer;
	private Org4SPOGServer org4SPOGServer;
	private Policy4SPOGServer policy4SPOGServer;
	private String organization_status;
	private ExtentTest test;
	private String Direct_cloud_id;
	private String msp_cloud_account_id;
	private String root_cloud_id;
	private String destination_id_direct;
	private String destination_id_suborg;
	private String destination_id_rootsub;
	private String destination_id_submsp_sub;
	private String directPolicyId;
	private String normalSubPolicyId;
	private String rootSubPolicyId;
	private String submspSubPolicyId;
	private SPOGDestinationServer spogDestinationServer;
	private String[] datacenters=new String[3];
	private SPOGDestinationInvoker spogDestinationInvoker;

	public void resumeOrg()
	{
		test.log(LogStatus.INFO, "Going to resume the Root MSP Organization using CSR Token");
		org4SPOGServer.resumeOrganization(ti.csr_token, ti.root_msp_org1_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		test.log(LogStatus.INFO, "Going to resume the Normal MSP Organization using CSR Token");
		org4SPOGServer.resumeOrganization(ti.csr_token, ti.normal_msp_org1_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		test.log(LogStatus.INFO, "Going to resume the Direct MSP Organization using CSR Token");
		org4SPOGServer.resumeOrganization(ti.csr_token, ti.direct_org1_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		test.log(LogStatus.INFO, "Resumed all Root,Normal MSP and Direct Organization succsffully");
	}

	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine, String buildVersion) {

		organization_status = null;
		spogDestinationInvoker= new SPOGDestinationInvoker(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		spogServer = new SPOGServer(baseURI, port);
		org4SPOGServer = new Org4SPOGServer(baseURI, port);
		policy4SPOGServer = new Policy4SPOGServer(baseURI, port);
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


		// get cloud account for the direct organization
		Response response = spogServer.getCloudAccounts(ti.direct_org1_user1_token, "", test);
		Direct_cloud_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");

		// get cloud account for the msp organization
		response = spogServer.getCloudAccounts(ti.normal_msp_org1_user1_token, "", test);
		msp_cloud_account_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");

		// get cloud account for the root msp organization
		response = spogServer.getCloudAccounts(ti.root_msp_org1_user1_token, "", test);
		root_cloud_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");

		// get destinations
		response = spogDestinationServer.getDestinations(ti.direct_org1_user1_token, "", test);
		destination_id_direct = response.then().extract().path("data[" + 0 + "].destination_id");

		response = spogDestinationServer.getDestinations(ti.normal_msp1_suborg1_user1_token, "", test);
		destination_id_suborg = response.then().extract().path("data[" + 0 + "].destination_id");

		response = spogDestinationServer.getDestinations(ti.root_msp1_suborg1_user1_token, "", test);
		destination_id_rootsub = response.then().extract().path("data[" + 0 + "].destination_id");

		response = spogDestinationServer.getDestinations(ti.msp1_submsp1_suborg1_user1_token, "", test);
		destination_id_submsp_sub = response.then().extract().path("data[" + 0 + "].destination_id");

		datacenters=spogDestinationServer.getDestionationDatacenterID();

		response = policy4SPOGServer.getPolicies(ti.direct_org1_user1_token, "");
		directPolicyId = response.then().extract().path("data[" + 0 + "].policy_id");

		response = policy4SPOGServer.getPolicies(ti.normal_msp1_suborg1_user1_token, "");
		normalSubPolicyId = response.then().extract().path("data[" + 0 + "].policy_id");

		response = policy4SPOGServer.getPolicies(ti.root_msp1_suborg1_user1_token, "");
		rootSubPolicyId = response.then().extract().path("data[" + 0 + "].policy_id");

		response = policy4SPOGServer.getPolicies(ti.msp1_submsp1_suborg1_user1_token, "");
		submspSubPolicyId = response.then().extract().path("data[" + 0 + "].policy_id");
	}


	@DataProvider(name="testcasesForSuspendOrganizationAfterDisablingPolicies")
	public Object[][] testDataForSuspendOrganizationAfterDisablingPolicies(){
		return new Object[][] {

			{"Make sure that CSR Admin able to suspend root msp organization after disabling the policies of root msp organization",
				ti.csr_token,ti.root_msp_org1_id, SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"Make sure that CSR Admin able to suspend sub msp organization after disabling the policies of sub msp organization",
					ti.csr_token,ti.root_msp1_submsp_org1_id, SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"Make sure that CSR Admin able to suspend the sub organization of root msp organization after disabling the policies of sub organization of root msp organization",
						ti.csr_token,ti.root_msp1_suborg1_id, SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"Make sure that CSR Admin able to suspend the sub organization sub msp organization after disabling the policies of sub organization of sub msp organization",
							ti.csr_token,ti.msp1_submsp1_sub_org1_id, SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"Make sure that CSR Admin able to suspend the direct organization after disabling the policies of direct organization",
								ti.csr_token,ti.direct_org1_id, SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"Make sure that CSR Admin able to suspend the Normal MSP organization after disabling the policies of Normal MSP organization",
									ti.csr_token,ti.normal_msp_org1_id, SpogConstants.SUCCESS_GET_PUT_DELETE,null}	,
			{"Make sure that CSR Admin able to suspend the sub org of Normal MSP organization after disabling the policies of sub org of Normal MSP organization",
										ti.csr_token,ti.normal_msp1_suborg1_id, SpogConstants.SUCCESS_GET_PUT_DELETE,null}	
		};
	}

	@Test(dataProvider="testcasesForSuspendOrganizationAfterDisablingPolicies",priority=1)
	public void testSuspendOrganizationAfterDisablePolicies(String testCase, String token, String orgId, int expectedStatusCode,SpogMessageCode expectedErrorMessage)
	{
		test = ExtentManager.getNewTest(testCase);
		test.assignAuthor("Kanamarlapudi, Chandra Kanth");
		System.out.println(testCase);
		resumeOrg();
		test.log(LogStatus.INFO, testCase);
		policy4SPOGServer.disableSpecifiedOrgPolicies(token, orgId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		ArrayList<HashMap> policies = new ArrayList<HashMap>();
		Response response = policy4SPOGServer.getPolicies(token, "organization_id=" + orgId);
		policies = response.then().extract().path("data");

		if (!policies.isEmpty()) {
			policies.stream().forEach(policy -> {
				assertEquals("disabled", policy.get("usage_status").toString());
			});
		}
		org4SPOGServer.suspendOrganizationWithCheck(token, orgId, expectedStatusCode, expectedErrorMessage, test);
		if(expectedStatusCode==SpogConstants.SUCCESS_GET_PUT_DELETE)
		{
			response=spogServer.getSpecifiedOrgProperties(ti.csr_token, orgId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
			organization_status = response.then().extract().path("data.organization_status");
			if(organization_status.equals(SpogConstants.SUSPEND_ORGANIZATION))
			{
				if(validatePolicyStatus(ti.csr_token, orgId, test))
				{
					test.log(LogStatus.PASS, "Organization with id: "+SpogConstants.SUSPEND_ORGANIZATION+" is suspeneded successfully and In the Organization Properties shows ["+organization_status+"] State");
					org4SPOGServer.resumeOrganization(token, orgId, expectedStatusCode, test);
				}
				else
				{
					test.log(LogStatus.FAIL,"Organization with id: "+SpogConstants.SUSPEND_ORGANIZATION+" is suspeneded successfully but Policies for that organization doesnot shows disabled state");
					org4SPOGServer.resumeOrganization(token, orgId, expectedStatusCode, test);
				}
			}
			else
			{
				test.log(LogStatus.FAIL, "Organization with id: "+SpogConstants.SUSPEND_ORGANIZATION+" is suspeneded successfully But In the Organization Properties shows ["+organization_status+"] State instead of ["+SpogConstants.SUSPEND_ORGANIZATION+"]");
				Assert.fail("Organization with id: "+SpogConstants.SUSPEND_ORGANIZATION+" is suspeneded successfully But In the Organization Properties shows ["+organization_status+"] State instead of ["+SpogConstants.SUSPEND_ORGANIZATION+"]");
			}
		}
	}

	@DataProvider(name="testcasesForSuspendOrganizationAfterEnablingPolicies")
	public Object[][] testDataForSuspendOrganizationAfterEnablingPolicies(){
		return new Object[][] {
			{"Make sure that CSR Admin able to suspend the root msp organization after enabling the policies of root msp organization",ti.csr_token,ti.root_msp_org1_id, SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"Make sure that CSR Admin able to suspend the sub msp organization after enabling the policies of sub msp organization",ti.csr_token,ti.root_msp1_submsp_org1_id, SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"Make sure that CSR Admin able to suspend the sub organization of root msp organization after enabling the policies of sub organization of root msp organization",ti.csr_token,ti.root_msp1_suborg1_id, SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"Make sure that CSR Admin able to suspend the sub organization of sub msp organization after enabling the policies of sub organization of sub msp organization",ti.csr_token,ti.msp1_submsp1_sub_org1_id, SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"Make sure that CSR Admin able to suspend the direct organization after enabling the policies of direct organization",ti.csr_token,ti.direct_org1_id, SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"Make sure that CSR Admin able to suspend the Normal MSP organization after enabling the policies of Normal MSP organization",	ti.csr_token,ti.normal_msp_org1_id, SpogConstants.SUCCESS_GET_PUT_DELETE,null}	,
			{"Make sure that CSR Admin able to suspend the sub org of Normal MSP organization after enabling the policies of sub org of Normal MSP organization",ti.csr_token,ti.normal_msp1_suborg1_id, SpogConstants.SUCCESS_GET_PUT_DELETE,null}	
		};
	}



	@Test(dataProvider="testcasesForSuspendOrganizationAfterEnablingPolicies",priority=2)
	public void testSuspendOrganizationAfterEnablingPolicies(String testCase, String token, String orgId, int expectedStatusCode,SpogMessageCode expectedErrorMessage)
	{
	
		ArrayList<HashMap> policies = new ArrayList<HashMap>();
		test = ExtentManager.getNewTest(testCase);
		test.assignAuthor("Kanamarlapudi, Chandra Kanth");
		System.out.println(testCase);
		test.log(LogStatus.INFO, testCase);
		resumeOrg();
		policy4SPOGServer.enableSpecifiedOrgPoliciesWithCheck(token, orgId, SpogConstants.SUCCESS_GET_PUT_DELETE, null,test);
		test.log(LogStatus.INFO, "get the policies of org and check the status");
		Response response = policy4SPOGServer.getPolicies(token, "organization_id=" + orgId);
		policies = response.then().extract().path("data");

		if (!policies.isEmpty()) {
			policies.stream().forEach(policy -> {
				assertEquals("enabled", policy.get("usage_status").toString());
			});
		}
		org4SPOGServer.suspendOrganizationWithCheck(token, orgId, expectedStatusCode, expectedErrorMessage, test);
		if(expectedStatusCode==SpogConstants.SUCCESS_GET_PUT_DELETE)
		{
			response=spogServer.getSpecifiedOrgProperties(ti.csr_token, orgId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
			organization_status = response.then().extract().path("data.organization_status");
			if(organization_status.equals(SpogConstants.SUSPEND_ORGANIZATION))
			{
				if(validatePolicyStatus(ti.csr_token, orgId, test))
				{
					test.log(LogStatus.PASS, "Organization with id: "+SpogConstants.SUSPEND_ORGANIZATION+" is suspeneded successfully and In the Organization Properties shows ["+organization_status+"] State");
					org4SPOGServer.resumeOrganization(token, orgId, expectedStatusCode, test);
				}
				else
				{
					test.log(LogStatus.FAIL,"Organization with id: "+SpogConstants.SUSPEND_ORGANIZATION+" is suspeneded successfully but Policies for that organization doesnot shows disabled state");
					org4SPOGServer.resumeOrganization(token, orgId, expectedStatusCode, test);
				}
			}
			else
			{
				test.log(LogStatus.FAIL, "Organization with id: "+SpogConstants.SUSPEND_ORGANIZATION+" is suspeneded successfully But In the Organization Properties shows ["+organization_status+"] State instead of ["+SpogConstants.SUSPEND_ORGANIZATION+"]");
				Assert.fail("Organization with id: "+SpogConstants.SUSPEND_ORGANIZATION+" is suspeneded successfully But In the Organization Properties shows ["+organization_status+"] State instead of ["+SpogConstants.SUSPEND_ORGANIZATION+"]");
			}
		}
	}



	/*@DataProvider(name="testcasesForSuspendOrganizationMiscellaneousSet1")
	public Object[][] testDataForSuspendOrganizationMiscellaneousSet1(){
		return new Object[][] {
			{"Make sure that CSR Admin able to suspend root MSP organization after disabling the policies of sub org of root msp organization using root msp admin",
				ti.csr_token,ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.root_msp1_suborg1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null},

			{"Make sure that CSR Admin able to suspend root MSP organization after disabling the policies of sub org of root msp organization using root msp account admin",
				ti.csr_token,ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp_org1_id,ti.root_msp1_suborg1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null},

			{"Make sure that CSR Admin able to suspend sub MSP organization after disabling the policies of sub org of sub msp organization using sub msp admin",
				ti.csr_token,ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp_org1_id,ti.msp1_submsp1_sub_org1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null},

			{"Make sure that CSR Admin able to suspend sub MSP organization after disabling the policies of sub org of sub msp organization using sub msp account admin",
					ti.csr_token,ti.root_msp1_submsp1_account_admin_token,ti.root_msp1_submsp_org1_id,ti.msp1_submsp1_sub_org1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null}
		};
	}

	@Test(dataProvider="testcasesForSuspendOrganizationMiscellaneousSet1")
	public void testForSuspendOrganizationMiscellaneousSet1(String testCase, String csr_token, String user_token,String parentOrgId, String childOrgId,int expectedStatusCode,SpogMessageCode expectedErrorMessage)
	{
		test = ExtentManager.getNewTest(testCase);
		test.assignAuthor("Kanamarlapudi, Chandra Kanth");
		policy4SPOGServer.disableSpecifiedOrgPolicies(user_token, childOrgId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		ArrayList<HashMap> policies = new ArrayList<HashMap>();
		Response response = policy4SPOGServer.getPolicies(user_token, "organization_id=" + childOrgId);
		policies = response.then().extract().path("data");

		if (!policies.isEmpty()) {
			policies.stream().forEach(policy -> {
				assertEquals("disabled", policy.get("usage_status").toString());
			});
		}
		String token=csr_token;
		String orgId=parentOrgId;
		org4SPOGServer.suspendOrganizationWithCheck(token, orgId, expectedStatusCode, expectedErrorMessage, test);
		if(expectedStatusCode==SpogConstants.SUCCESS_GET_PUT_DELETE)
		{
			response=spogServer.getSpecifiedOrgProperties(token, orgId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
			organization_status = response.then().extract().path("data.organization_status");
			if(organization_status.equals(SpogConstants.SUSPEND_ORGANIZATION))
			{
				if(validatePolicyStatus(token, orgId, test))
				{
					test.log(LogStatus.PASS, "Organization with id: "+SpogConstants.SUSPEND_ORGANIZATION+" is suspeneded successfully and In the Organization Properties shows ["+organization_status+"] State");
					org4SPOGServer.resumeOrganization(token, orgId, expectedStatusCode, test);
				}
				else
				{
					test.log(LogStatus.FAIL,"Organization with id: "+SpogConstants.SUSPEND_ORGANIZATION+" is suspeneded successfully but Policies for that organization doesnot shows disabled state");
					org4SPOGServer.resumeOrganization(token, orgId, expectedStatusCode, test);
				}
			}
			else
			{
				test.log(LogStatus.FAIL, "Organization with id: "+SpogConstants.SUSPEND_ORGANIZATION+" is suspeneded successfully But In the Organization Properties shows ["+organization_status+"] State instead of ["+SpogConstants.SUSPEND_ORGANIZATION+"]");
				Assert.fail("Organization with id: "+SpogConstants.SUSPEND_ORGANIZATION+" is suspeneded successfully But In the Organization Properties shows ["+organization_status+"] State instead of ["+SpogConstants.SUSPEND_ORGANIZATION+"]");
			}
		}
	}

	@DataProvider(name="testcasesForSuspendOrganizationMiscellaneousSet2")
	public Object[][] testDataForSuspendOrganizationMiscellaneousSet2(){
		return new Object[][] {
			{"Make sure that CSR Admin able to suspend root MSP organization after enabling the policies of sub org of root msp organization using root msp admin",
				ti.csr_token,ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.root_msp1_suborg1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null},

			{"Make sure that CSR Admin able to suspend root MSP organization after enabling the policies of sub org of root msp organization using root msp account admin",
				ti.csr_token,ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp_org1_id,ti.root_msp1_suborg1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null},

			{"Make sure that CSR Admin able to suspend sub MSP organization after enabling the policies of sub org of sub msp organization using root msp admin",
				ti.csr_token,ti.root_msp_org1_user1_token,ti.root_msp1_submsp_org1_id,ti.msp1_submsp1_sub_org1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null},

			{"Make sure that CSR Admin able to suspend sub MSP organization after enabling the policies of sub org of sub msp organization using root msp account admin",
				ti.csr_token,ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp1_submsp_org1_id,ti.msp1_submsp1_sub_org1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
		};
	}

	@Test(dataProvider="testcasesForSuspendOrganizationMiscellaneousSet2")
	public void testcasesForSuspendOrganizationMiscellaneousSet2(String testCase, String csr_token, String user_token,String parentOrgId, String childOrgId,int expectedStatusCode,SpogMessageCode expectedErrorMessage)
	{
		ArrayList<HashMap> policies = new ArrayList<HashMap>();
		test = ExtentManager.getNewTest(testCase);
		test.assignAuthor("Kanamarlapudi, Chandra Kanth");
		policy4SPOGServer.enableSpecifiedOrgPolicies(user_token, childOrgId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		test.log(LogStatus.INFO, "get the policies of org and check the status");
		Response response = policy4SPOGServer.getPolicies(user_token, "organization_id=" + childOrgId);
		policies = response.then().extract().path("data");

		if (!policies.isEmpty()) {
			policies.stream().forEach(policy -> {
				assertEquals("enabled", policy.get("usage_status").toString());
			});
		}
		String token=csr_token;
		String orgId=parentOrgId;
		org4SPOGServer.suspendOrganizationWithCheck(token, orgId, expectedStatusCode, expectedErrorMessage, test);
		if(expectedStatusCode==SpogConstants.SUCCESS_GET_PUT_DELETE)
		{
			response=spogServer.getSpecifiedOrgProperties(token, orgId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
			organization_status = response.then().extract().path("data.organization_status");
			if(organization_status.equals(SpogConstants.SUSPEND_ORGANIZATION))
			{
				if(validatePolicyStatus(token, orgId, test))
				{
					test.log(LogStatus.PASS, "Organization with id: "+SpogConstants.SUSPEND_ORGANIZATION+" is suspeneded successfully and In the Organization Properties shows ["+organization_status+"] State");
					org4SPOGServer.resumeOrganization(token, orgId, expectedStatusCode, test);
				}
				else
				{
					test.log(LogStatus.FAIL,"Organization with id: "+SpogConstants.SUSPEND_ORGANIZATION+" is suspeneded successfully but Policies for that organization doesnot shows disabled state");
					org4SPOGServer.resumeOrganization(token, orgId, expectedStatusCode, test);
				}
			}
			else
			{
				test.log(LogStatus.FAIL, "Organization with id: "+SpogConstants.SUSPEND_ORGANIZATION+" is suspeneded successfully But In the Organization Properties shows ["+organization_status+"] State instead of ["+SpogConstants.SUSPEND_ORGANIZATION+"]");
				Assert.fail("Organization with id: "+SpogConstants.SUSPEND_ORGANIZATION+" is suspeneded successfully But In the Organization Properties shows ["+organization_status+"] State instead of ["+SpogConstants.SUSPEND_ORGANIZATION+"]");
			}
		}
	}
	 */

	@DataProvider(name="testcasesForSuspendOrganization")
	public Object[][] testDataForSuspendOrganization(){
		return new Object[][] {


			//200 Test scenerios
			{"Make sure that CSR Admin can able to suspend direct organization",ti.csr_token,ti.direct_org1_id, SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"Make sure that CSR Admin can able to suspend Normal MSP organization",ti.csr_token,ti.normal_msp_org1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"Make sure that CSR Admin can able to suspend sub org of Normal MSP organization",ti.csr_token,ti.normal_msp1_suborg1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"Make sure that CSR Admin can able to suspend root MSP organization",ti.csr_token,ti.root_msp_org1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"Make sure that CSR Admin can able to suspend sub org of Root MSP organization",ti.csr_token,ti.root_msp1_suborg1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"Make sure that CSR Admin can able to suspend sub MSP organization",ti.csr_token,ti.root_msp1_submsp_org1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"Make sure that CSR Admin can able to suspend sub org of Sub MSP organization",ti.csr_token,ti.msp1_submsp1_sub_org1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null},

			{"Make sure that Normal MSP Admin can able to suspend sub organization",ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"Make sure that Normal MSP account can able to suspend assigned sub organization",ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp1_suborg1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null},

			{"Make sure that Root MSP Admin can able to suspend sub MSP organization",ti.root_msp_org1_user1_token,ti.root_msp1_submsp_org1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"Make sure that Root MSP Admin can able to suspend sub organization of root msp organization",ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null},

			{"Make sure that Root MSP account Admin can able to suspend assigned sub organization of root msp organization",ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp1_suborg1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null},

			{"Make sure that sub MSP Admin can able to suspend sub organization of sub msp organization",ti.root_msp1_submsp1_user1_token,ti.msp1_submsp1_sub_org1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"Make sure that sub MSP account Admin can able to suspend sub organization of sub msp organization",ti.root_msp1_submsp1_account_admin_token,ti.msp1_submsp1_sub_org1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null},

			//400 error Code
			{"Make sure that CSR Admin unable to suspend invalid organization",ti.csr_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_ORGANIZATIONID_IS_NOT_UUID},
			{"Make sure that Root MSP Admin unable to suspend invalid sub organization",ti.root_msp_org1_user1_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_ORGANIZATIONID_IS_NOT_UUID},
			{"Make sure that Root MSP account Admin unable to suspend invalid organization",ti.root_msp_org1_msp_accountadmin1_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_ORGANIZATIONID_IS_NOT_UUID},
			{"Make sure that normal MSP Admin unable to suspend invalid organization",ti.normal_msp_org1_user1_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_ORGANIZATIONID_IS_NOT_UUID},
			{"Make sure that normal msp account Admin unable to suspend invalid organization",ti.normal_msp_org1_msp_accountadmin1_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_ORGANIZATIONID_IS_NOT_UUID},
			{"Make sure that sub msp Admin unable to suspend invalid organization",ti.root_msp1_submsp1_user1_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_ORGANIZATIONID_IS_NOT_UUID},
			{"Make sure that sub msp account Admin unable to suspend invalid organization",ti.root_msp1_submsp1_account_admin_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_ORGANIZATIONID_IS_NOT_UUID},


			//401 Error Code
			{"Make sure that, user unable to valid suspend organization with invalid Token","invalid", ti.root_msp1_suborg1_id, SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT},
			{"Make sure that, user unable to valid suspend organization with empty Token","", ti.root_msp1_suborg1_id, SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTHENTICATION_FAILED},
			{"Make sure that, user unable to valid suspend organization invalid Token",null, ti.root_msp1_suborg1_id, SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT},


			//403 Error Codes
			//{"Make sure that unable to suspend direct organization using direct Admin Token",ti.direct_org1_user1_token,  ti.direct_org2_id,  SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to suspend root msp organization using direct Admin Token",ti.direct_org1_user1_token,ti.root_msp_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to suspend sub msp organization using direct Admin Token",ti.direct_org1_user1_token,ti.root_msp1_submsp_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to suspend sub org of root msp organization using direct Admin Token",ti.direct_org1_user1_token, ti.root_msp1_suborg1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to suspend sub org of sub msp organization using direct Admin Token",ti.direct_org1_user1_token, ti.msp1_submsp1_sub_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to suspend sub org of normal msp organization using direct Admin Token",ti.direct_org1_user1_token, ti.normal_msp1_suborg1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to suspend normal msp organization using direct Admin Token",ti.direct_org1_user1_token,  ti.normal_msp_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},

			{"Make sure that unable to suspend unassigned other sub organization using normal msp Admin token",ti.root_msp_org1_user1_token,ti.normal_msp1_suborg2_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to suspend unassigned sub organization using normal msp account Admin token",ti.root_msp_org1_msp_accountadmin1_token,ti.normal_msp1_suborg2_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to suspend unassigned other sub organization using sub msp Admin token",ti.root_msp1_submsp1_user1_token,ti.root_msp2_submsp_org2_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to suspend unassigned sub organization using sub msp account Admin token",ti.root_msp1_submsp1_account_admin_token,ti.msp1_submsp1_sub_org2_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to suspend unassigned other sub organization using root msp Admin token",ti.root_msp_org1_user1_token,ti.root_msp2_suborg1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to suspend unassigned sub organization using root msp account Admin token",ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp1_suborg2_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			//{"Make sure that unable to suspend unassigned other sub organization using csr Admin token",ti.csr_token,ti.root_msp1_suborg2_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},



			{"Make sure that unable to suspend direct organization using CSR Read-only Token",ti.csr_readonly_token,ti.direct_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to suspend root msp organization using CSR Read-only Token",ti.csr_readonly_token,ti.root_msp_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to suspend sub msp organization using CSR Read-only Token",ti.csr_readonly_token,ti.root_msp1_submsp_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to suspend sub org of sub msp using CSR Read-only Token",ti.csr_readonly_token,ti.msp1_submsp1_sub_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to suspend sub org of root msp organization using CSR Read-only Token",ti.csr_readonly_token,ti.root_msp1_suborg1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to suspend normal msp organization using CSR Read-only  Token",ti.csr_readonly_token,ti.normal_msp_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to suspend sub org of normal msp account using CSR Read-only Token",ti.csr_readonly_token,ti.normal_msp1_suborg1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},


			{"Make sure that unable to suspend direct organization using root msp monitor token",ti.root_msp_org1_monitor_user1_token,ti.direct_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to suspend root msp organization using root msp monitor token",ti.root_msp_org1_monitor_user1_token,ti.root_msp_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to suspend sub msp organization using root msp monitor token",ti.root_msp_org1_monitor_user1_token,ti.root_msp1_submsp_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to suspend sub org of sub msp using root msp monitor token",ti.root_msp_org1_monitor_user1_token,ti.msp1_submsp1_sub_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to suspend sub org of root msp organization using root msp monitor token",ti.root_msp_org1_monitor_user1_token,ti.root_msp1_suborg1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to suspend normal msp organization using root msp monitor token",ti.root_msp_org1_monitor_user1_token,ti.normal_msp1_suborg1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to suspend sub org of normal msp account using root msp monitor token",ti.root_msp_org1_monitor_user1_token,ti.normal_msp1_suborg1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to suspend normal msp organization using normal msp monitor token",ti.normal_msp_org1_monitor_user1_token,ti.normal_msp_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to suspend sub org of normal msp organization using normal msp monitor token",ti.normal_msp_org1_monitor_user1_token,ti.normal_msp1_suborg1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to suspend root msp organization using sub msp monitor token",ti.root_msp1_submsp1_monitor_user1_token,ti.root_msp_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to suspend sub org of root msp organization using sub msp monitor token",ti.root_msp1_submsp1_monitor_user1_token,ti.root_msp1_suborg1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to suspend sub msp organization using sub msp monitor token",ti.root_msp1_submsp1_monitor_user1_token,ti.root_msp1_submsp_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to suspend sub org of sub msp organization using sub msp monitor token",ti.root_msp1_submsp1_monitor_user1_token,ti.msp1_submsp1_sub_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},



			//404 Error Code
			{"Make sure that unable to suspend organization which is doesn’t exit using CSR Token",ti.csr_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED},
			{"Make sure that unable to suspend organization which is doesn’t exit using Root msp Admin Token",ti.root_msp_org1_user1_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED},
			{"Make sure that unable to suspend organization which is doesn’t exit using Root msp account Admin Token",ti.root_msp_org1_msp_accountadmin1_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED},
			{"Make sure that unable to suspend organization which is doesn’t exit using normal msp Admin Token",ti.normal_msp_org1_user1_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED},
			{"Make sure that unable to suspend organization which is doesn’t exit using normal msp account Admin Token",ti.normal_msp_org1_msp_accountadmin1_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED},
			{"Make sure that unable to suspend organization which is doesn’t exit using sub msp Admin Token",ti.root_msp1_submsp1_user1_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED},
			{"Make sure that unable to suspend organization which is doesn’t exit using sub msp account admin Token",ti.root_msp1_submsp1_account_admin_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED},

		};

	}

	@Test(dataProvider="testcasesForSuspendOrganization",priority=3)
	public void testSuspendOrganization(String testCase, String token, String orgId, int expectedStatusCode,SpogMessageCode expectedErrorMessage)
	{
	
		test = ExtentManager.getNewTest(testCase);
		test.assignAuthor("Kanamarlapudi, Chandra Kanth");
		test.log(LogStatus.INFO, testCase);
		resumeOrg();
		org4SPOGServer.suspendOrganizationWithCheck(token, orgId, expectedStatusCode, expectedErrorMessage, test);
		if(expectedStatusCode==SpogConstants.SUCCESS_GET_PUT_DELETE)
		{
			test.log(LogStatus.INFO, "Going to Validate the Organziation properties consists of organization_status");
			Response response=spogServer.getSpecifiedOrgProperties(ti.csr_token, orgId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
			organization_status = response.then().extract().path("data.organization_status");
			if(organization_status.equals(SpogConstants.SUSPEND_ORGANIZATION))
			{
				test.log(LogStatus.INFO,"organization_status Property field present in the organization properties after suspended an organziation");
				test.log(LogStatus.INFO, "Going to Validate the status of each Policies present in Organization");
				boolean stat=validatePolicyStatus(ti.csr_token, orgId, test);
				if(stat)
				{
					test.log(LogStatus.PASS, "Organization with id: "+SpogConstants.SUSPEND_ORGANIZATION+" is suspeneded successfully and In the Organization Properties shows ["+organization_status+"] State");
				}
				else
				{
					test.log(LogStatus.FAIL,"Organization with id: "+SpogConstants.SUSPEND_ORGANIZATION+" is suspeneded successfully but Policies for that organization doesnot shows disabled state");
				}
			}
			else
			{
				test.log(LogStatus.FAIL, "Organization with id: "+SpogConstants.SUSPEND_ORGANIZATION+" is suspeneded successfully But In the Organization Properties shows ["+organization_status+"] State instead of ["+SpogConstants.SUSPEND_ORGANIZATION+"]");
				Assert.fail("Organization with id: "+SpogConstants.SUSPEND_ORGANIZATION+" is suspeneded successfully But In the Organization Properties shows ["+organization_status+"] State instead of ["+SpogConstants.SUSPEND_ORGANIZATION+"]");
			}
		}

	}


	public boolean validatePolicyStatus(String token, String organization_id, ExtentTest test) 
	{
		String filter = "organization_id="+organization_id;
		Response response = policy4SPOGServer.getPolicies(token, filter);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		ArrayList<HashMap<String, Object>> policies = response.then().extract().path("data");
		int count=0;
		for (int i = 0; i < policies.size(); i++)
		{
			if(policies.get(i).get("usage_status").toString().equalsIgnoreCase("disabled"))
			{
				test.log(LogStatus.INFO, "Check policy with id:"+policies.get(i).get("policy_id")+" is disabled");
				count++;
			}
			else
			{
				test.log(LogStatus.FAIL, "policy with id:"+policies.get(i).get("policy_id")+" is not disabled");
			}
		}
		if(count==policies.size())
			return true;
		else
			return false;
	}


	@AfterMethod
	public void getResult(ITestResult result){
		if(result.getStatus() == ITestResult.FAILURE){
			count1.setfailedcount();            
			test.log(LogStatus.FAIL, "Test Case Failed is "+result.getName()+" with parameters as "+Arrays.asList(result.getParameters()) );
			test.log(LogStatus.FAIL, result.getThrowable().getMessage());
		}else if(result.getStatus() == ITestResult.SKIP){
			count1.setskippedcount();
			test.log(LogStatus.SKIP, "Test Case Skipped is "+result.getName());
		}else if(result.getStatus()==ITestResult.SUCCESS){
			count1.setpassedcount();
		}
		// ending test
		//endTest(logger) : It ends the current test and prepares to create HTML report
		rep.endTest(test);    
	}





	@DataProvider(name="testcasesForSuspendOrganizationMSPEnableCustomerUsingCSR")
	public Object[][] testDataForSuspendOrganizationMSPEnableCustomerUsingCSR(){
		return new Object[][] {

			{"Make sure that CSR Admin unable to resume sub org of normal msp organization,after performing csr admin suspends normal msp organization",
				ti.csr_token,ti.normal_msp_org1_id,ti.csr_token,ti.normal_msp1_suborg1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.CANNOT_RESUME_ORG_ITS_PARENT_ORG_SUSPENDED},

			{"Make sure that CSR Admin unable to resume sub org of sub msp organization, after performing csr admin suspends sub msp organization",
					ti.csr_token,ti.root_msp1_submsp_org1_id, ti.csr_token, ti.msp1_submsp1_sub_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.CANNOT_RESUME_ORG_ITS_PARENT_ORG_SUSPENDED},

			{"Make sure that CSR Admin unable to resume sub org of root msp organization, after performing csr admin suspends root msp organization",
						ti.csr_token,ti.root_msp_org1_id, ti.csr_token, ti. root_msp1_suborg1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.CANNOT_RESUME_ORG_ITS_PARENT_ORG_SUSPENDED},

			{"Make sure that CSR Admin unable to resume sub msp of root msp organization, after performing csr admin suspends root msp organization",
							ti.csr_token,ti.root_msp_org1_id,ti.csr_token,ti.root_msp1_submsp_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.CANNOT_RESUME_ORG_ITS_PARENT_ORG_SUSPENDED},

			{"Make sure that CSR Admin unable to resume sub org of sub msp organization, after performing csr admin suspends root msp organization",
								ti.csr_token,ti.root_msp_org1_id,ti.csr_token,ti.msp1_submsp1_sub_org1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.CANNOT_RESUME_ORG_ITS_PARENT_ORG_SUSPENDED}
		};
	}



	@Test(dataProvider="testcasesForSuspendOrganizationMSPEnableCustomerUsingCSR",priority=4)
	public void testForSuspendOrganizationMSPEnableCustomerUsingCSR(String testCase, String token1, String orgId1, String token2, String orgId2,int expectedStatusCode,SpogMessageCode expectedErrorMessage)
	{
		test = ExtentManager.getNewTest(testCase);
		test.assignAuthor("Kanamarlapudi, Chandra Kanth");
		test.log(LogStatus.INFO, testCase);
		System.out.println(testCase);
		resumeOrg();
		org4SPOGServer.suspendOrganizationWithCheck(token1, orgId1, SpogConstants.SUCCESS_GET_PUT_DELETE,null,test);
		test.log(LogStatus.INFO, "Going to verify the organization_status properties for the suspended organization ["+orgId1+"]");
		Response response=spogServer.getSpecifiedOrgProperties(ti.csr_token, orgId1, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		organization_status = response.then().extract().path("data.organization_status");
		if(organization_status.equals(SpogConstants.SUSPEND_ORGANIZATION))
		{
			test.log(LogStatus.INFO, "Going to verify the policies status for the suspended organization ["+orgId1+"]");
			boolean stat=validatePolicyStatus(ti.csr_token, orgId1, test);
			if(stat)
			{
				test.log(LogStatus.INFO, "Organization with id: "+SpogConstants.SUSPEND_ORGANIZATION+" is suspeneded successfully and In the Organization Properties shows ["+organization_status+"] State");
				test.log(LogStatus.INFO, "Going to perform resume Operation for the organization ["+orgId2+"] using token ["+token2+"]");
				org4SPOGServer.resumeOrganizationWithCheck(token2, orgId2, expectedStatusCode, expectedErrorMessage, test);

				if(expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE)
				{
					test.log(LogStatus.INFO, "Validating the organization status parameter in organization properties for resumed Organization");
					Response response2=spogServer.getSpecifiedOrgProperties(token2, orgId2, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
					organization_status = response2.then().extract().path("data.organization_status");
					Assert.assertNull(organization_status,"Organization_Status Field should not be displayed for the Resume/Enabled Organization");
					test.log(LogStatus.PASS, "Organization_Status Field is not displayed");
				}
			}
			else
			{
				test.log(LogStatus.FAIL,"Organization with id: "+SpogConstants.SUSPEND_ORGANIZATION+" is suspeneded successfully but Policies for that organization doesnot shows disabled state");
				Assert.fail("Organization with id: "+SpogConstants.SUSPEND_ORGANIZATION+" is suspeneded successfully but Policies for that organization doesnot shows disabled state");
			}
		}
		else
		{
			test.log(LogStatus.FAIL, "Organization with id: "+SpogConstants.SUSPEND_ORGANIZATION+" is suspeneded successfully But In the Organization Properties shows ["+organization_status+"] State instead of ["+SpogConstants.SUSPEND_ORGANIZATION+"]");
			Assert.fail("Organization with id: "+SpogConstants.SUSPEND_ORGANIZATION+" is suspeneded successfully But In the Organization Properties shows ["+organization_status+"] State instead of ["+SpogConstants.SUSPEND_ORGANIZATION+"]");
		}
	}





	@DataProvider(name="testcasesForSuspendResumeOrganizations")
	public Object[][] testDataForSuspendResumeOrganizations(){
		return new Object[][] {
			{"Make sure that CSR Admin able to resume normal MSP Organization after performing normal MSP admin suspends sub org of normal msp organization and csr admin suspends normal msp organization",
				ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_id,ti.csr_token,ti.normal_msp_org1_id,ti.csr_token,ti.normal_msp_org1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null},


			{"Make sure that CSR Admin able to resume normal MSP Organization after performing normal MSP account admin suspends sub org of normal msp organization and csr admin suspends normal msp organization",
					ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp1_suborg1_id,ti.csr_token,ti.normal_msp_org1_id,ti.csr_token,ti.normal_msp_org1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null},


			{"Make sure that CSR Admin unable to resume sub org of normal MSP Organization after performing normal MSP admin suspends sub org of normal msp organization and csr admin suspends normal msp organization",
				ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_id,ti.csr_token,ti.normal_msp_org1_id,ti.csr_token,ti.normal_msp_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.CANNOT_RESUME_ORG_ITS_PARENT_ORG_SUSPENDED},

			{"Make sure that CSR Admin unable to resume sub org of normal MSP Organization after performing normal MSP account admin suspends sub org of normal msp organization and csr admin suspends normal msp organization",
				ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp1_suborg1_id,ti.csr_token,ti.normal_msp_org1_id,ti.csr_token,ti.normal_msp_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.CANNOT_RESUME_ORG_ITS_PARENT_ORG_SUSPENDED},


			{"Make sure that CSR Admin able to resume sub MSP Organization after performing sub MSP admin suspends sub org of sub msp organization and csr admin suspends sub msp organization",
				ti.root_msp1_submsp1_user1_token,ti.msp1_submsp1_sub_org1_id,ti.csr_token,ti.root_msp1_submsp_org1_id,ti.csr_token,ti.root_msp1_submsp_org1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null},


			{"Make sure that CSR Admin able to resume sub MSP Organization after performing sub MSP account admin suspends sub org of sub msp organization and csr admin suspends sub msp organization",
				ti.root_msp1_submsp1_account_admin_token,ti.msp1_submsp1_sub_org1_id,ti.csr_token,ti.root_msp1_submsp_org1_id,ti.csr_token,ti.root_msp1_submsp_org1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null},


			{"Make sure that CSR Admin unable to resume sub org of sub msp Organization after performing sub MSP admin suspends sub org of sub msp organization and csr admin suspends sub msp organization",
				ti.root_msp1_submsp1_user1_token,ti.msp1_submsp1_sub_org1_id,ti.csr_token,ti.root_msp1_submsp_org1_id,ti.csr_token,ti.msp1_submsp1_sub_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.CANNOT_RESUME_ORG_ITS_PARENT_ORG_SUSPENDED},


			{"Make sure that CSR Admin unable to resume sub org of sub msp Organization after performing sub MSP account admin suspends sub org of sub msp organization and csr admin suspends sub msp organization",
				ti.root_msp1_submsp1_account_admin_token,ti.msp1_submsp1_sub_org1_id,ti.csr_token,ti.root_msp1_submsp_org1_id,ti.csr_token,ti.msp1_submsp1_sub_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.CANNOT_RESUME_ORG_ITS_PARENT_ORG_SUSPENDED},



			{"Make sure that CSR Admin able to resume root MSP Organization after performing root MSP admin suspends sub org of root msp organization and csr admin suspends root msp organization",
				ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_id,ti.csr_token,ti.root_msp_org1_id,ti.csr_token,ti.root_msp_org1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null},


			{"Make sure that CSR Admin able to resume root MSP Organization after performing root MSP account admin suspends sub org of root msp organization and csr admin suspends root msp organization",
				ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp1_suborg1_id,ti.csr_token,ti.root_msp_org1_id,ti.csr_token,ti.root_msp_org1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null},


			{"Make sure that CSR Admin able to resume root MSP Organization after performing root MSP admin suspends sub msp organization of root msp organization and csr admin suspends root msp organization",
				ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_id,ti.csr_token,ti.root_msp_org1_id,ti.csr_token,ti.root_msp_org1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null},


			{"Make sure that CSR Admin able to resume root MSP Organization after performing root MSP account admin suspends sub msp organization of root msp organization and csr admin suspends root msp organization",
															ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp1_suborg1_id,ti.csr_token,ti.root_msp_org1_id,ti.csr_token,ti.root_msp_org1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null},

			{"Make sure that CSR Admin unable to resume sub MSP of Root MSP Organization after performing Root MSP admin suspends sub MSP organization and csr admin suspends root msp organization",
				ti.root_msp_org1_user1_token,ti.root_msp1_submsp_org1_id,ti.csr_token,ti.root_msp_org1_id,ti.csr_token,ti.root_msp1_submsp_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.CANNOT_RESUME_ORG_ITS_PARENT_ORG_SUSPENDED},

			{"Make sure that CSR Admin unable to resume sub org of Root MSP Organization after performing Root MSP admin suspends sub org of Root MSP and csr admin suspends root msp organization",
																	ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_id,ti.csr_token,ti.root_msp_org1_id,ti.csr_token,ti.root_msp1_suborg1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.CANNOT_RESUME_ORG_ITS_PARENT_ORG_SUSPENDED},

			{"Make sure that CSR Admin unable to resume sub org of Root MSP Organization after performing Root MSP account admin suspends sub org of Root MSP and csr admin suspends root msp organization",
																		ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp1_suborg1_id,ti.csr_token,ti.root_msp_org1_id,ti.csr_token,ti.root_msp1_suborg1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.CANNOT_RESUME_ORG_ITS_PARENT_ORG_SUSPENDED},


		};
	}

	@Test(dataProvider="testcasesForSuspendResumeOrganizations",priority=5)
	public void testForSuspendResumeOrganizations(String testCase, String token1, String orgId1, String token2, String orgId2,String token3,String orgId3,int expectedStatusCode,SpogMessageCode expectedErrorMessage)
	{
		test = ExtentManager.getNewTest(testCase);
		test.assignAuthor("Kanamarlapudi, Chandra Kanth");
		test.log(LogStatus.INFO, testCase);
		resumeOrg();
		System.out.println(testCase);
		org4SPOGServer.suspendOrganizationWithCheck(token1, orgId1, SpogConstants.SUCCESS_GET_PUT_DELETE,null,test);
		Response response=spogServer.getSpecifiedOrgProperties(ti.csr_token, orgId1, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		organization_status = response.then().extract().path("data.organization_status");
		test.log(LogStatus.INFO, "Going to validate the organization_status parameter for suspended organization");
		if(organization_status.equals(SpogConstants.SUSPEND_ORGANIZATION))
		{
			test.log(LogStatus.INFO, "Validated organization_status parameter for suspended organization");
			test.log(LogStatus.INFO, "Going to validate the policies status for the suspended organization");
			boolean stat=validatePolicyStatus(ti.csr_token, orgId1, test);
			if(stat)
			{
				test.log(LogStatus.INFO, "Organization with id: "+SpogConstants.SUSPEND_ORGANIZATION+" is suspeneded successfully and In the Organization Properties shows ["+organization_status+"] State");

				test.log(LogStatus.INFO, "Going to perform 2 operation suspend");
				org4SPOGServer.suspendOrganizationWithCheck(token2, orgId2, SpogConstants.SUCCESS_GET_PUT_DELETE,null,test);
				if(expectedStatusCode==SpogConstants.SUCCESS_GET_PUT_DELETE)
				{
					test.log(LogStatus.INFO, "2nd operation suspend the organization successfully. Going to validate organization_status properties for the 2nd suspended organzation");
					response=spogServer.getSpecifiedOrgProperties(ti.csr_token, orgId2, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
					organization_status = response.then().extract().path("data.organization_status");
					if(organization_status.equals(SpogConstants.SUSPEND_ORGANIZATION))
					{
						test.log(LogStatus.INFO, "validated 2nd Organization's organization_status properties successfully");
						test.log(LogStatus.INFO, "Going to validate the policies status for the 2nd organzation");
						stat=validatePolicyStatus(ti.csr_token, orgId2, test);
						if(stat)
						{
							test.log(LogStatus.INFO, "Organization with id: "+SpogConstants.SUSPEND_ORGANIZATION+" is suspeneded successfully and In the Organization Properties shows ["+organization_status+"] State");
							test.log(LogStatus.INFO, "Going to perform 3rd operation Resume on the Organzation ["+orgId3+"] using token ["+token3+"]");
							org4SPOGServer.resumeOrganizationWithCheck(token3, orgId3, expectedStatusCode, expectedErrorMessage, test);
							if(expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE)
							{
								test.log(LogStatus.INFO, "Validating the organization status parameter in organization properties");
								response=spogServer.getSpecifiedOrgProperties(token3, orgId3, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
								organization_status = response.then().extract().path("data.organization_status");
								Assert.assertNull(organization_status,"Organization_Status Field should not be displayed for the Resume/Enabled Organization");
								test.log(LogStatus.PASS, "Organization_Status Field is not displayed for Resumed Organization");
							}
						}
						else
						{
							test.log(LogStatus.FAIL,"Organization with id: "+SpogConstants.SUSPEND_ORGANIZATION+" is suspeneded successfully but Policies for that organization doesnot shows disabled state");
							Assert.fail("Organization with id: "+SpogConstants.SUSPEND_ORGANIZATION+" is suspeneded successfully but Policies for that organization doesnot shows disabled state");
						}
					}
					else
					{
						test.log(LogStatus.FAIL,"Organization with id: "+SpogConstants.SUSPEND_ORGANIZATION+" is suspeneded successfully but Policies for that organization doesnot shows disabled state");
						Assert.fail("Organization with id: "+SpogConstants.SUSPEND_ORGANIZATION+" is suspeneded successfully but Policies for that organization doesnot shows disabled state");
					}
				}
			}
			else
			{
				test.log(LogStatus.FAIL,"Organization with id: "+SpogConstants.SUSPEND_ORGANIZATION+" is suspeneded successfully but Policies for that organization doesnot shows disabled state");
				Assert.fail("Organization with id: "+SpogConstants.SUSPEND_ORGANIZATION+" is suspeneded successfully but Policies for that organization doesnot shows disabled state");
			}
		}
		else
		{
			test.log(LogStatus.FAIL,"Organization with id: "+SpogConstants.SUSPEND_ORGANIZATION+" is suspeneded successfully but Policies for that organization doesnot shows disabled state");
			Assert.fail("Organization with id: "+SpogConstants.SUSPEND_ORGANIZATION+" is suspeneded successfully but Policies for that organization doesnot shows disabled state");
		}
	}







	@DataProvider(name = "testcasesForSuspendOrganizationsPerformCRUDPolicies")
	public final Object[][] testDataForSuspendOrganizationsPerformCRUDPolicies() {
		return new Object[][] {

			{ "Direct-CRUD Policies using CSR Token After Suspension of Direct Organization using CSR Token", ti.csr_token,ti.direct_org1_id, ti.csr_token,ti.direct_org1_id,"direct",
				destination_id_direct,Direct_cloud_id, UUID.randomUUID().toString(), "cloud_direct_baas",
				"cloud_direct_file_folder_backup", Direct_cloud_id, "daily", directPolicyId},

			{ "Direct-CRUD Policies using CSR TOken After Suspension of Direct Organization using Direct Token", ti.direct_org1_user1_token,ti.direct_org1_id, ti.csr_token,ti.direct_org1_id,"direct",
				destination_id_direct,Direct_cloud_id, UUID.randomUUID().toString(), "cloud_direct_baas",
				"cloud_direct_file_folder_backup", Direct_cloud_id, "daily", directPolicyId},

			{ "Direct-CRUD Policies using Customer Admin After Suspension of Direct Organization using Direct Token", ti.direct_org1_user1_token,ti.direct_org1_id, ti.direct_org1_user1_token,ti.direct_org1_id,"direct",
				destination_id_direct,Direct_cloud_id, UUID.randomUUID().toString(), "cloud_direct_baas",
				"cloud_direct_file_folder_backup", Direct_cloud_id, "daily", directPolicyId},

			{ "Sub Organization-CRUD Policies using CSR Token After Suspension of sub Organization of Normal MSP Org using CSR Token", ti.csr_token,ti.normal_msp1_suborg1_id, ti.csr_token,ti.normal_msp1_suborg1_id,"direct",
				destination_id_suborg,msp_cloud_account_id, UUID.randomUUID().toString(), "cloud_direct_baas",
				"cloud_direct_file_folder_backup", msp_cloud_account_id, "daily", normalSubPolicyId},

			{ "Sub Organization-CRUD Policies using CSR Token After Suspension of Normal MSP Org using CSR Token", ti.csr_token,ti.normal_msp_org1_id, ti.csr_token,ti.normal_msp1_suborg1_id,"direct",
				destination_id_suborg,msp_cloud_account_id, UUID.randomUUID().toString(), "cloud_direct_baas",
				"cloud_direct_file_folder_backup", msp_cloud_account_id, "daily", normalSubPolicyId},


			{ "Sub Organization-CRUD Policies using Normal MSP Token After Suspension of sub Organization of Normal MSP Org using Normal MSP Token", ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_id, ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_id,"direct",
				destination_id_suborg,msp_cloud_account_id, UUID.randomUUID().toString(), "cloud_direct_baas",
				"cloud_direct_file_folder_backup", msp_cloud_account_id, "daily", normalSubPolicyId},

			{ "Sub Organization-CRUD Policies using Normal MSP Token After Suspension of Normal MSP Org using Normal MSP Token", ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_id,"direct",
				destination_id_suborg,msp_cloud_account_id, UUID.randomUUID().toString(), "cloud_direct_baas",
				"cloud_direct_file_folder_backup", msp_cloud_account_id, "daily", normalSubPolicyId},


			{ "Sub Organization-CRUD Policies using Normal MSP Account Admin Token After Suspension of sub Organization of Normal MSP Org using Normal MSP Account Admin Token", ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp1_suborg1_id, ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp1_suborg1_id,"direct",
				destination_id_suborg,msp_cloud_account_id, UUID.randomUUID().toString(), "cloud_direct_baas",
				"cloud_direct_file_folder_backup", msp_cloud_account_id, "daily", normalSubPolicyId},

			{ "Sub Organization-CRUD Policies using Normal MSP Account Admin Token After Suspension of sub Organization of Normal MSP Org using Normal MSP Admin Token", ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_id, ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp1_suborg1_id,"direct",
				destination_id_suborg,msp_cloud_account_id, UUID.randomUUID().toString(), "cloud_direct_baas",
				"cloud_direct_file_folder_backup", msp_cloud_account_id, "daily", normalSubPolicyId},

			{ "Sub Organization-CRUD Policies using Normal MSP Admin After Suspension of sub Organization of Normal MSP Org using CSR Token", ti.csr_token,ti.normal_msp1_suborg1_id, ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_id,"direct",
				destination_id_suborg,msp_cloud_account_id, UUID.randomUUID().toString(), "cloud_direct_baas",
				"cloud_direct_file_folder_backup", msp_cloud_account_id, "daily", normalSubPolicyId},

			{ "Sub Organization-CRUD Policies using Normal MSP Account Admin After Suspension of sub Organization of Normal MSP Org using CSR Token", ti.csr_token,ti.normal_msp1_suborg1_id, ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp1_suborg1_id,"direct",
				destination_id_suborg,msp_cloud_account_id, UUID.randomUUID().toString(), "cloud_direct_baas",
				"cloud_direct_file_folder_backup", msp_cloud_account_id, "daily", normalSubPolicyId},

			{ "Sub Organization Of Root MSP -CRUD Policies using CSR Token After Suspension of Root MSP Org using CSR Token", ti.csr_token,ti.root_msp_org1_id, ti.csr_token,ti.root_msp1_suborg1_id,"direct",
				destination_id_rootsub,root_cloud_id, UUID.randomUUID().toString(), "cloud_direct_baas",
				"cloud_direct_file_folder_backup", root_cloud_id, "daily", rootSubPolicyId},

			{ "Sub Organization Of Root MSP -CRUD Policies using Root MSP Token After Suspension of Root MSP Org using CSR Token", ti.csr_token,ti.root_msp_org1_id, ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_id,"direct",
				destination_id_rootsub,root_cloud_id, UUID.randomUUID().toString(), "cloud_direct_baas",
				"cloud_direct_file_folder_backup", root_cloud_id, "daily", rootSubPolicyId},

			{ "Sub Organization Of Root MSP -CRUD Policies using Root MSP Token After Suspension of Root MSP Org using root msp Token", ti.root_msp_org1_user1_token,ti.root_msp_org1_id, ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_id,"direct",
				destination_id_rootsub,root_cloud_id, UUID.randomUUID().toString(), "cloud_direct_baas",
				"cloud_direct_file_folder_backup", root_cloud_id, "daily", rootSubPolicyId},

			{ "Sub Organization Of Root MSP -CRUD Policies using Root MSP Account Token After Suspension of Root MSP Org using CSR Token", ti.csr_token,ti.root_msp_org1_id, ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp1_suborg1_id,"direct",
				destination_id_rootsub,root_cloud_id, UUID.randomUUID().toString(), "cloud_direct_baas",
				"cloud_direct_file_folder_backup", root_cloud_id, "daily", rootSubPolicyId},

			{ "Sub Organization Of Root MSP -CRUD Policies using Root MSP Account Token After Suspension of Root MSP Org using Root MSP Token", ti.root_msp_org1_user1_token,ti.root_msp_org1_id, ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp1_suborg1_id,"direct",
				destination_id_rootsub,root_cloud_id, UUID.randomUUID().toString(), "cloud_direct_baas",
				"cloud_direct_file_folder_backup", root_cloud_id, "daily", rootSubPolicyId},


			 { "Sub Organization Of sub MSP -CRUD Policies using CSR Token After Suspension of Root MSP Org using CSR Token", ti.csr_token,ti.root_msp_org1_id, ti.csr_token,ti.msp1_submsp1_sub_org1_id,"direct",
				destination_id_submsp_sub,root_cloud_id, UUID.randomUUID().toString(), "cloud_direct_baas",
				"cloud_direct_file_folder_backup", root_cloud_id, "daily", submspSubPolicyId},

			{ "Sub Organization Of sub MSP -CRUD Policies using CSR Token After Suspension of Root MSP Org using Root MSP Token", ti.root_msp_org1_user1_token,ti.root_msp_org1_id, ti.csr_token,ti.msp1_submsp1_sub_org1_id,"direct",
				destination_id_submsp_sub,root_cloud_id, UUID.randomUUID().toString(), "cloud_direct_baas",
				"cloud_direct_file_folder_backup", root_cloud_id, "daily", submspSubPolicyId},

			{ "Sub Organization Of sub MSP -CRUD Policies using CSR Token After Suspension of Sub MSP Org using CSR Token", ti.csr_token,ti.root_msp1_submsp_org1_id, ti.csr_token,ti.msp1_submsp1_sub_org1_id,"direct",
				destination_id_submsp_sub,root_cloud_id, UUID.randomUUID().toString(), "cloud_direct_baas",
				"cloud_direct_file_folder_backup", root_cloud_id, "daily", submspSubPolicyId},

			{ "Sub Organization Of sub MSP -CRUD Policies using CSR Token After Suspension of Sub MSP Org using Root MSP Token", ti.root_msp_org1_user1_token,ti.root_msp1_submsp_org1_id, ti.csr_token,ti.msp1_submsp1_sub_org1_id,"direct",
				destination_id_submsp_sub,root_cloud_id, UUID.randomUUID().toString(), "cloud_direct_baas",
				"cloud_direct_file_folder_backup", root_cloud_id, "daily", submspSubPolicyId},

		};
	}



	@Test(dataProvider = "testcasesForSuspendOrganizationsPerformCRUDPolicies",priority=6)
	public void testForSuspendOrganizationsPerformCRUDPolicies(String testCase, String suspendUserToken, String suspendOrgID, String AfterSuspendUsertoken,
			String AfterSuspendOrgID, String organizationType, String destination_id,String cloud_id, String policy_id,String policyType, String taskType,String site_id,String schedule, String updatedPolicyID) {
		
		test = ExtentManager.getNewTest(testCase);
		test.assignAuthor("Kanamarlapudi, Chandra Kanth");
		test.log(LogStatus.INFO, testCase);
		resumeOrg();
		test.log(LogStatus.INFO, "Going to Suspend organization");
		org4SPOGServer.suspendOrganizationWithCheck(suspendUserToken, suspendOrgID, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		Response response = spogServer.getSpecifiedOrgProperties(ti.csr_token, suspendOrgID, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		organization_status = response.then().extract().path("data.organization_status");
		test.log(LogStatus.INFO, "Going to valiadate the organization_status property for the suspended organization");
		if (organization_status.equals(SpogConstants.SUSPEND_ORGANIZATION)) 
		{
			test.log(LogStatus.INFO, "validated the organization_status property successfully for the suspended organization");
			test.log(LogStatus.INFO, "Going to validate the Policies status for the suspended organization");
			boolean stat = validatePolicyStatus(ti.csr_token, suspendOrgID, test);
			if (stat)
			{
				test.log(LogStatus.INFO,"Organization with id: " + SpogConstants.SUSPEND_ORGANIZATION+ " is suspeneded successfully and In the Organization Properties shows ["+ organization_status + "] State");
				test.log(LogStatus.INFO, "Going to Perform Policies CRUD");
				CRUDPolicies(organizationType, AfterSuspendOrgID, AfterSuspendUsertoken, destination_id, cloud_id, policy_id, policyType, taskType, cloud_id, schedule, updatedPolicyID);
				test.log(LogStatus.PASS, "Successfully Performed Policies CRUD, Unable to create,update and delete Policies for the suspended Organization");
			} 
			else
			{
				test.log(LogStatus.FAIL, "Organization with id: " + SpogConstants.SUSPEND_ORGANIZATION
						+ " is suspeneded successfully but Policies for that organization doesnot shows disabled state");
				Assert.fail("Organization with id: " + SpogConstants.SUSPEND_ORGANIZATION
						+ " is suspeneded successfully but Policies for that organization doesnot shows disabled state");
			}
		} 
		else 
		{
			test.log(LogStatus.FAIL, "Organization with id: " + SpogConstants.SUSPEND_ORGANIZATION
					+ " is suspeneded successfully But In the Organization Properties shows [" + organization_status
					+ "] State instead of [" + SpogConstants.SUSPEND_ORGANIZATION + "]");
			Assert.fail("Organization with id: " + SpogConstants.SUSPEND_ORGANIZATION
					+ " is suspeneded successfully But In the Organization Properties shows [" + organization_status
					+ "] State instead of [" + SpogConstants.SUSPEND_ORGANIZATION + "]");
		}
	}






	@DataProvider(name = "org_info")
	public final Object[][] org_info() {
		return new Object[][] {

			{ ti.direct_org1_id, ti.direct_org1_user1_token },
			{ ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token },
			{ ti.root_msp_org1_id, ti.root_msp_org1_user1_token },
			{ ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_token },
			{ ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_token },
			{ ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_token },

		};
	}

	/*@Test(dataProvider = "org_info")*/
	public void deleteResources(String org_id, String validToken) {

		policy4SPOGServer.setToken(validToken);
		Response response = policy4SPOGServer.getPolicies(null);
		ArrayList<String> policies = new ArrayList<>();
		policies = response.then().extract().path("data.policy_id");

		if (!policies.isEmpty()) {
			policies.stream().forEach(policy -> {
				policy4SPOGServer.deletePolicybyPolicyId(ti.csr_token, policy);
			});
		}

		spogServer.setToken(validToken);
		response = spogServer.getSources("", "", 1, 20, true, test);

		ArrayList<String> sources = new ArrayList<>();
		sources = response.then().extract().path("data.source_id");
		if (!sources.isEmpty()) {
			sources.stream().forEach(source -> {
				spogServer.deleteSourceByID(source, test);
			});
		}
	}

	public void CRUDPolicies(String organizationType, String organization_id, String validToken,
			String destination_id, String cloud_id, String policy_id, String policyType, String taskType,
			String site_id, String schedule,String updatedPolicyID) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		String source_id = null;

		ArrayList<HashMap<String, Object>> schedules = new ArrayList<>();

		String schedule_id = spogServer.returnRandomUUID();

		String task_id = spogServer.returnRandomUUID();

		String throttle_id = spogServer.returnRandomUUID();

		String throttle_type = "network";

		String policy_name = spogServer.ReturnRandom("test");

		String policy_description = spogServer.ReturnRandom("description");

		String resource_name = spogServer.ReturnRandom("Chandra_Resource") + "_";

		test.log(LogStatus.INFO, "The token is :" + validToken);

		test.log(LogStatus.INFO, "post the destination");

		String destination_name = RandomStringUtils.randomAlphanumeric(4) + "Chandra-test34";

		policy4SPOGServer.setToken(validToken);

		test.log(LogStatus.INFO, "Create schedule settings");

		HashMap<String, Object> scheduleSettingDTO = new HashMap<String, Object>();

		HashMap<String, Object> customScheduleSettingDTO = policy4SPOGServer.createCustomScheduleDTO("1522068700422",
				"full", "1", "true", "10", "minutes", test);

		test.log(LogStatus.INFO, "Create schedules");

		test.log(LogStatus.INFO, "Create cloud direct schedule");

		HashMap<String, Object> cloudDirectScheduleDTO = new HashMap<>();
		if (schedule.equals("daily")) {
			cloudDirectScheduleDTO = policy4SPOGServer.createCloudDirectScheduleDTO("0 6 * * *", test);
			test.log(LogStatus.INFO, "Create schedule settings");
			scheduleSettingDTO = policy4SPOGServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null, null, null,
					null, test);
			schedules = policy4SPOGServer.createPolicyScheduleDTO(null, schedule_id, "1d", task_id, destination_id,
					scheduleSettingDTO, "06:00", "12:00", taskType, destination_name, test);
		} else {
			cloudDirectScheduleDTO = policy4SPOGServer.createCloudDirectScheduleDTO("0 6 * * 1 *", test);
			test.log(LogStatus.INFO, "Create schedule settings");
			scheduleSettingDTO = policy4SPOGServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null, null, null,
					null, test);
			schedules = policy4SPOGServer.createPolicyScheduleDTO(null, schedule_id, "1w", task_id, destination_id,
					scheduleSettingDTO, "06:00", "12:00", taskType, destination_name, test);
		}

		test.log(LogStatus.INFO, "Exclude the folders from cloud direct file backup");

		ArrayList<HashMap<String, Object>> excludes = policy4SPOGServer.createExcludeInfoDTO(null, "path", "c:\\tmp",
				test);

		HashMap<String, Object> cloudDirectLocalBackupDTO = policy4SPOGServer.createCloudDirectLocalBackupDTO("d:\\tmp",
				"true", test);

		HashMap<String, Object> cloudDirectFileBackupTaskInfoDTO = policy4SPOGServer
				.createCloudDirectFileBackupTaskInfoDTO("d:\\tmp", cloudDirectLocalBackupDTO, excludes, test);

		ArrayList<String> drivers = new ArrayList<>();

		drivers.add("C");

		ArrayList<HashMap<String, Object>> destinations = new ArrayList<HashMap<String, Object>>();

		HashMap<String, Object> perform_ar_test = policy4SPOGServer.createPerformARTestOption("true", "true", "true",
				"true", test);

		HashMap<String, Object> retention_policy = policy4SPOGServer.createRetentionPolicyOption("2", "2", "2", "2",
				test);

		HashMap<String, Object> udp_replication_from_remote_DTO = policy4SPOGServer
				.createUdpReplicationFromRemoteInfoDTO(perform_ar_test, retention_policy, test);

		HashMap<String, Object> cloudDirectimageBackupTaskInfoDTO = policy4SPOGServer
				.createCloudDirectImageBackupTaskInfoDTO(drivers, cloudDirectLocalBackupDTO, test);

		spogServer.setToken(validToken);

		if (taskType.equals("cloud_direct_hypervisor") || taskType.equals("cloud_direct_file_folder_backup")) {

			source_id = spogServer.createSourceWithCheck(resource_name, SourceType.machine, SourceProduct.cloud_direct,
					organization_id, cloud_id, ProtectionStatus.unprotect, ConnectionStatus.online, "windows",
					"SQLSERVER", test);

			test.log(LogStatus.INFO, "Create task type and link it to the destination ");
			destinations = policy4SPOGServer.createPolicyTaskDTO(null, task_id, taskType, destination_id, "none", null,
					cloudDirectFileBackupTaskInfoDTO, null, test);

		} else if (taskType.equals("cloud_direct_image_backup")) {

			test.log(LogStatus.INFO, "Adding sources of type " + SourceType.machine + " to org " + organizationType);
			source_id = spogServer.createSourceWithCheck(resource_name, SourceType.machine, SourceProduct.cloud_direct,
					organization_id, site_id, ProtectionStatus.unprotect, ConnectionStatus.online, "windows",
					"SQLSERVER", test);

			test.log(LogStatus.INFO, "Create task type and link it to the destination ");

			destinations = policy4SPOGServer.createPolicyTaskDTO(null, task_id, taskType, destination_id, "none",
					cloudDirectimageBackupTaskInfoDTO, null, null, test);

		} else if (taskType.equalsIgnoreCase("cloud_direct_vmware_backup")) {
			String destination_type = "cloud_direct_volume";
			destinations = policy4SPOGServer.createPolicyTaskDTO(destination_name, null, task_id, taskType,
					destination_id, null, null, null, null, null, null, null, destination_type, test);

		} else {
			HashMap<String, Object> cloud_hybrid_store = new HashMap<String, Object>();
			cloud_hybrid_store = spogDestinationServer.composeCloudHybridInfo(4, true, 64, true);
			test.log(LogStatus.INFO, "Creating a destination of type cloud_hybrid_store");
			Response response = spogDestinationServer.createDestination(UUID.randomUUID().toString(), validToken,
					cloud_id, organization_id, site_id, UUID.randomUUID().toString(), "20", "cloud_hybrid_store",
					"running", destination_name, cloud_hybrid_store, test);
			destination_id = response.then().extract().path("data.destination_id");

			destinations = policy4SPOGServer.createPolicyTaskDTO(null, task_id, taskType, destination_id, null, null,
					null, udp_replication_from_remote_DTO, test);

			scheduleSettingDTO = policy4SPOGServer.createScheduleSettingDTO(null, customScheduleSettingDTO, null, null,
					null, test);

			schedules = policy4SPOGServer.createPolicyScheduleDTO(null, spogServer.returnRandomUUID(), "custom",
					task_id, destination_id, scheduleSettingDTO, "06:00", "12:00", taskType, destination_name, test);

		}
		test.log(LogStatus.INFO, "Create network throttle ");

		ArrayList<HashMap<String, Object>> throttles = policy4SPOGServer.createPolicyThrottleDTO(null, throttle_id,
				task_id, throttle_type, "1200", "1", "06:00", "18:00", taskType, destination_id, destination_name,
				test);

		test.log(LogStatus.INFO, "Create a policy of type cloud_direct_baas");

		Response response = null;

		if (taskType.equals("cloud_direct_vmware_backup")) {
			String hypervisor_id = UUID.randomUUID().toString();
			response = policy4SPOGServer.createPolicy(policy_name, policy_description, policyType, "true", source_id,
					destinations, schedules, null, policy_id, organization_id, hypervisor_id, test);
		} else {
			response = policy4SPOGServer.createPolicy(policy_name, policy_description, policyType, null, "true",
					source_id, destinations, schedules, throttles, policy_id, organization_id, test);
		}
		test.log(LogStatus.INFO, "Unable to create Policy for the Suspended Organizations");
		spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
		spogServer.checkErrorMessage(response, SpogMessageCode.CANNOT_CREATE_POLICY_SUSPENDED_ORGANIZATION.getStatus());
		///NEED TO ADD LOG FILE
		//test.log(LogStatus.INFO, "Going to Validate Update Policy for the Suspended Organizations");
		/*schedules = policy4SPOGServer.createPolicyScheduleDTO(null, spogServer.returnRandomUUID(), "custom",
				task_id, destination_id, scheduleSettingDTO, "06:00", "12:00", taskType, destination_name, test);
		response=policy4SPOGServer.updatePolicy(policy_name, policy_description, policyType, null, "false", null, destinations, schedules, throttles, updatedPolicyID, organization_id, validToken, test);
		spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);


		spogServer.checkErrorMessage(response, SpogMessageCode.CANNOT_EDIT_POLICY_SUSPENDED_ORGANIZATION.getCodeString());
		test.log(LogStatus.INFO, "Unable to Edit existing Policy for the Suspended Organizations");
*/
		//	----spogServer.checkErrorMessage(response, SpogMessageCode.);
		//==============================
		test.log(LogStatus.INFO, "Unable to delete Policy for the Suspended Organizations");
		response=policy4SPOGServer.deletePolicybyPolicyId(validToken, updatedPolicyID)	;
		spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
		//	----spogServer.checkErrorMessage(response, SpogMessageCode.);		
		spogServer.checkErrorMessage(response, SpogMessageCode.CANNOT_DELETE_POLICY_SUSPENDED_ORGANIZATION.getStatus());
		test.log(LogStatus.INFO, "Unable to Delete existing Policy for the Suspended Organizations");
		//==============================		


	}




	@DataProvider(name = "testCasesSuspendOrganizationPerformCRUDSources")
	public Object[][] testDataSuspendOrganizationPerformCRUDSources() {
		return new Object[][] {
			{
				"Make sure that CSR Admin can create/get/edit/delete Sources for the suspended direct organization",
				ti.csr_token, ti.direct_org1_id,ti.csr_token,ti.direct_org1_id,Direct_cloud_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null 
			},

			{
				"Make sure that Direct Admin can create/get/edit/delete Sources for the suspended direct organization",
				ti.csr_token, ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_id,Direct_cloud_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null 
			},
			{
				"Make sure that Direct Admin can create/get/edit/delete Sources for the suspended direct organization by Direct Admin",
				ti.direct_org1_user1_token, ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_id,Direct_cloud_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null 
			},



			{
				"Make sure that Normal MSP Admin can create/get/edit/delete Sources for the sub org where normal msp organization is suspended by CSR Admin",
				ti.csr_token, ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_id,msp_cloud_account_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null 
			},

			{
				"Make sure that Normal MSP Admin can create/get/edit/delete Sources for the sub org where normal msp organization is suspended by Normal MSP Admin",
				ti.normal_msp_org1_user1_token, ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_id,msp_cloud_account_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null 
			},


			{
				"Make sure that Normal MSP Admin can create/get/edit/delete Sources for the suspended sub org where sub org organization is suspended by CSR Admin",
				ti.csr_token, ti.normal_msp1_suborg1_id,ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_id,msp_cloud_account_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null 
			},

			{
				"Make sure that Normal MSP Admin can create/get/edit/delete Sources for the suspended sub org where sub org organization is suspended by Normal MSP Admin",
				ti.normal_msp_org1_user1_token, ti.normal_msp1_suborg1_id,ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_id,msp_cloud_account_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null 
			},

			{
				"Make sure that Normal MSP Admin can create/get/edit/delete Sources for the suspended sub org where sub org organization is suspended by Normal MSP Account Admin",
				ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp1_suborg1_id,ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_id,msp_cloud_account_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null 
			},

			{
				"Make sure that Normal MSP Account Admin can create/get/edit/delete Sources for the suspended sub org where sub org organization is suspended by Normal MSP Account Admin",
				ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp1_suborg1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp1_suborg1_id,msp_cloud_account_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null 
			},

			{//need to check
				"Make sure that customer Admin can create/get/edit/delete Sources for the suspended sub org where sub org organization is suspended by Customer Admin",
				ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_id,msp_cloud_account_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null 
			},

			///Suspend ROOT MSP ORG
			{
				"Make sure that root msp Admin can create/get/edit/delete Sources for the sub org of suspended root msp org where root msp org organization is suspended by CSR Admin",
				ti.csr_token, ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_id,root_cloud_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null 
			},

			{
				"Make sure that root msp Admin can create/get/edit/delete Sources for the sub org of suspended root msp org where root msp org organization is suspended by Root MSP Admin",
				ti.root_msp_org1_user1_token, ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_id,root_cloud_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null 
			},

			{
				"Make sure that root msp account Admin can create/get/edit/delete Sources for the sub org of suspended root msp org where root msp org organization is suspended by Root MSP Admin",
				ti.root_msp_org1_user1_token, ti.root_msp_org1_id,ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp1_suborg1_id,root_cloud_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null 
			},

			{
				"Make sure that CSR Admin can create/get/edit/delete Sources for the sub org of suspended root msp org where root msp org organization is suspended by Root MSP Admin",
				ti.root_msp_org1_user1_token, ti.root_msp_org1_id,ti.csr_token,ti.root_msp1_suborg1_id,root_cloud_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null 
			},


			///Suspend sub MSP ORG
			{
				"Make sure that sub msp Admin can create/get/edit/delete Sources for the sub org of suspended sub msp org where sub msp org organization is suspended by CSR Admin",
				ti.csr_token, ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,ti.msp1_submsp1_sub_org1_id,root_cloud_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null 
			},

			{
				"Make sure that sub msp account Admin can create/get/edit/delete Sources for the sub org of suspended sub msp org where sub msp org organization is suspended by CSR Admin",
				ti.csr_token, ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_account_admin_token,ti.msp1_submsp1_sub_org1_id,root_cloud_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null 
			},

			{
				"Make sure that Customer Admin can create/get/edit/delete Sources for the sub org of suspended sub msp org where sub msp org organization is suspended by CSR Admin",
				ti.csr_token, ti.root_msp1_submsp_org1_id,ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_sub_org1_id,root_cloud_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null 
			},

			{//need to check
				"Make sure that sub msp Admin can create/get/edit/delete Sources for the sub org of suspended sub msp org where sub msp org organization is suspended by Root MSP Admin",
				ti.root_msp_org1_user1_token, ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,ti.msp1_submsp1_sub_org1_id,root_cloud_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null 
			},

			{//need to check
				"Make sure that sub msp Account Admin can create/get/edit/delete Sources for the sub org of suspended sub msp org where sub msp org organization is suspended by Root MSP Admin",
				ti.root_msp_org1_user1_token, ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_account_admin_token,ti.msp1_submsp1_sub_org1_id,root_cloud_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null 
			},

			{//need to check
				"Make sure that customer Admin can create/get/edit/delete Sources for the sub org of suspended sub msp org where sub msp org organization is suspended by Root MSP Admin",
				ti.root_msp_org1_user1_token, ti.root_msp1_submsp_org1_id,ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_sub_org1_id,root_cloud_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null 
			},
		};

	}

	@Test(dataProvider = "testCasesSuspendOrganizationPerformCRUDSources",priority=7)
	public void testSuspendOrganizationPerformCRUDSources(String testCase, String suspendUserToken, String suspendOrgID, String AfterSuspendUsertoken,
			String AfterSuspendOrgID, String cloudID,int expectedStatusCode, SpogMessageCode expectedErrorMessage) {

	
		test = ExtentManager.getNewTest(testCase);
		test.assignAuthor("Kanamarlapudi, Chandra Kanth");
		test.log(LogStatus.INFO, testCase);
		System.out.println(testCase);
		resumeOrg();
		org4SPOGServer.suspendOrganizationWithCheck(suspendUserToken, suspendOrgID, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE)
		{
			Response response = spogServer.getSpecifiedOrgProperties(ti.csr_token, suspendOrgID, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
			organization_status = response.then().extract().path("data.organization_status");
			test.log(LogStatus.INFO, "Going to validate organization_status properties for the Organization");
			if (organization_status.equals(SpogConstants.SUSPEND_ORGANIZATION))
			{
				test.log(LogStatus.INFO, "Going to validate policy status for the suspended Organization");
				boolean stat = validatePolicyStatus(ti.csr_token, suspendOrgID, test);
				if (stat)
				{
					test.log(LogStatus.INFO,"Organization with id: " + SpogConstants.SUSPEND_ORGANIZATION+ " is suspeneded successfully and In the Organization Properties shows ["+ organization_status + "] State");
					test.log(LogStatus.INFO,"Going to perform CRUD Operations on the Sources after suspended organization");
					CRUDSources(AfterSuspendUsertoken, AfterSuspendOrgID, cloudID);
					test.log(LogStatus.PASS,"Performed CRUD Operations on the Sources after suspended organization successfully");
				}
				else
				{
					test.log(LogStatus.FAIL, "Organization with id: " + SpogConstants.SUSPEND_ORGANIZATION
							+ " is suspended successfully but Policies for that organization doesnot shows disabled state");
					Assert.fail("Organization with id: " + SpogConstants.SUSPEND_ORGANIZATION
							+ " is suspended successfully but Policies for that organization doesnot shows disabled state");
				}
			} else {
				test.log(LogStatus.FAIL, "Organization with id: " + SpogConstants.SUSPEND_ORGANIZATION
						+ " is suspeneded successfully But In the Organization Properties shows [" + organization_status
						+ "] State instead of [" + SpogConstants.SUSPEND_ORGANIZATION + "]");
				Assert.fail("Organization with id: " + SpogConstants.SUSPEND_ORGANIZATION
						+ " is suspeneded successfully But In the Organization Properties shows [" + organization_status
						+ "] State instead of [" + SpogConstants.SUSPEND_ORGANIZATION + "]");
			}
		}
	}


	public void CRUDSources(String validToken, String org_id, String cloud_id) {
		String sourceID = UUID.randomUUID().toString();
		System.out.println(sourceID);
		spogServer.setToken(validToken);
		Response response = spogServer.createSource(spogServer.ReturnRandom("sourceName"), SourceType.machine,
				SourceProduct.cloud_direct, org_id, cloud_id, ProtectionStatus.unprotect, ConnectionStatus.offline,
				OSMajor.windows.name(), "", spogServer.ReturnRandom("VirtualMachine"), null,
				spogServer.ReturnRandom("AgentName"), null, null, null, null, null, sourceID, test);
		System.out.println(response.getStatusCode());
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		test.log(LogStatus.INFO, "Created Source for the Suspended Organization");
		test.log(LogStatus.INFO, "Going to validate get sources for the Suspended Organizations");
		response = spogServer.getSourceById(validToken, sourceID, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		test.log(LogStatus.INFO, "Able to get created Source for the Suspended Organization");
		test.log(LogStatus.INFO, "Going to validate update sources for the Suspended Organizations");
		response = spogServer.updateSourcebysourceId(sourceID, spogServer.ReturnRandom("sourceName"), SourceType.machine,
				SourceProduct.cloud_direct, org_id, cloud_id, UUID.randomUUID().toString(), ProtectionStatus.unprotect,
				ConnectionStatus.online, OSMajor.windows.name().toString(), validToken, test);

		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		test.log(LogStatus.INFO, "Updated Source for the Suspended Organization successfully");
		response = spogServer.getSourceById(validToken, sourceID, test);
		test.log(LogStatus.INFO, "Going to validate delete sources for the Suspended Organizations");
		spogServer.deleteSourceByID(sourceID, test);
		test.log(LogStatus.INFO, "Deleted Source for the Suspended Organization successfully");

	}



	@DataProvider(name = "testCasesSuspendOrganizationPerformCRUDDestinations")
	public Object[][] testDataSuspendOrganizationPerformCRUDDestinations() {
		return new Object[][] {
			{
				"Make sure that CSR Admin can create/get/edit/delete Cloud Direct destination for the suspended direct organization",
				ti.csr_token, ti.direct_org1_id,ti.csr_token,ti.direct_org1_id,datacenters[0],Direct_cloud_id,DestinationType.cloud_direct_volume.toString(),
				SpogConstants.SUCCESS_GET_PUT_DELETE, null 
			},

			{
				"Make sure that Direct Admin can create/get/edit/delete Cloud Direct destination for the suspended direct organization",
				ti.csr_token, ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_id,datacenters[0],Direct_cloud_id,DestinationType.cloud_direct_volume.toString(),
				SpogConstants.SUCCESS_GET_PUT_DELETE, null 
			},
			{
				"Make sure that Direct Admin can create/get/edit/delete Cloud Direct destination for the suspended direct organization by Direct Admin Token",
				ti.direct_org1_user1_token, ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_id,datacenters[0],Direct_cloud_id,DestinationType.cloud_direct_volume.toString(),
				SpogConstants.SUCCESS_GET_PUT_DELETE, null 
			},
			{
				"Make sure that CSR Admin can create/get/edit/delete Cloud Direct destination for the suspended sub organization of Normal MSP organization",
				ti.csr_token, ti.normal_msp1_suborg1_id,ti.csr_token,ti.normal_msp1_suborg1_id,datacenters[0],Direct_cloud_id,DestinationType.cloud_direct_volume.toString(),
				SpogConstants.SUCCESS_GET_PUT_DELETE, null 
			},

			{
				"Make sure that CSR Admin can create/get/edit/delete Cloud Direct destination for the suspended sub organization of Normal MSP organization where sub org is suspended by Normal MSP Token",
				ti.normal_msp_org1_user1_token, ti.normal_msp1_suborg1_id,ti.csr_token,ti.normal_msp1_suborg1_id,datacenters[0],Direct_cloud_id,DestinationType.cloud_direct_volume.toString(),
				SpogConstants.SUCCESS_GET_PUT_DELETE, null 
			},

			{
				"Make sure that Normal MSP Admin can create/get/edit/delete Cloud Direct destination for the suspended sub organization of Normal MSP organization where sub org is suspended by Normal MSP Token",
				ti.normal_msp_org1_user1_token, ti.normal_msp1_suborg1_id,ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_id,datacenters[0],Direct_cloud_id,DestinationType.cloud_direct_volume.toString(),
				SpogConstants.SUCCESS_GET_PUT_DELETE, null 
			},

			{
				"Make sure that Normal MSP Account Admin can create/get/edit/delete Cloud Direct destination for the suspended sub organization of Normal MSP organization where sub org is suspended by Normal MSP Token",
				ti.normal_msp_org1_user1_token, ti.normal_msp1_suborg1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp1_suborg1_id,datacenters[0],Direct_cloud_id,DestinationType.cloud_direct_volume.toString(),
				SpogConstants.SUCCESS_GET_PUT_DELETE, null 
			},

			{
				"Make sure that Normal MSP Account Admin can create/get/edit/delete Cloud Direct destination for the suspended Normal MSP organization where Normal MSP org is suspended by Normal MSP Token",
				ti.normal_msp_org1_user1_token, ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp1_suborg1_id,datacenters[0],Direct_cloud_id,DestinationType.cloud_direct_volume.toString(),
				SpogConstants.SUCCESS_GET_PUT_DELETE, null 
			},

			{
				"Make sure that Normal MSP Admin can create/get/edit/delete Cloud Direct destination for the suspended Normal MSP organization where Normal MSP org is suspended by Normal MSP Token",
				ti.normal_msp_org1_user1_token, ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_id,datacenters[0],Direct_cloud_id,DestinationType.cloud_direct_volume.toString(),
				SpogConstants.SUCCESS_GET_PUT_DELETE, null 
			},
			{
				"Make sure that Normal MSP Admin can create/get/edit/delete Cloud Direct destination for the suspended Normal MSP organization where Normal MSP org is suspended by CSR Token",
				ti.csr_token, ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_id,datacenters[0],Direct_cloud_id,DestinationType.cloud_direct_volume.toString(),
				SpogConstants.SUCCESS_GET_PUT_DELETE, null 
			},

			{
				"Make sure that Root MSP Admin can create/get/edit/delete Cloud Direct destination of sub organization of the suspended Root MSP organization where Root MSP org is suspended by CSR Token",
				ti.csr_token, ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_id,datacenters[0],Direct_cloud_id,DestinationType.cloud_direct_volume.toString(),
				SpogConstants.SUCCESS_GET_PUT_DELETE, null 
			},

			{
				"Make sure that Root MSP Account Admin can create/get/edit/delete Cloud Direct destination of sub organization of the suspended Root MSP organization where Root MSP org is suspended by CSR Token",
				ti.csr_token, ti.root_msp_org1_id,ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp1_suborg1_id,datacenters[0],Direct_cloud_id,DestinationType.cloud_direct_volume.toString(),
				SpogConstants.SUCCESS_GET_PUT_DELETE, null 
			},


			{
				"Make sure that sub MSP Admin can create/get/edit/delete Cloud Direct destination of sub organization of the suspended Root MSP organization where Root MSP org is suspended by Root MSP Token",
				ti.root_msp_org1_user1_token, ti.root_msp_org1_id,ti.root_msp1_submsp1_user1_token,ti.msp1_submsp1_sub_org1_id,datacenters[0],Direct_cloud_id,DestinationType.cloud_direct_volume.toString(),
				SpogConstants.SUCCESS_GET_PUT_DELETE, null 
			},

			{
				"Make sure that sub MSP Account Admin can create/get/edit/delete Cloud Direct destination of sub organization of the suspended Root MSP organization where Root MSP org is suspended by Root MSP Token",
				ti.root_msp_org1_user1_token, ti.root_msp_org1_id,ti.root_msp1_submsp1_account_admin_token,ti.msp1_submsp1_sub_org1_id,datacenters[0],Direct_cloud_id,DestinationType.cloud_direct_volume.toString(),
				SpogConstants.SUCCESS_GET_PUT_DELETE, null 
			},


		};

	}

	@Test(dataProvider = "testCasesSuspendOrganizationPerformCRUDDestinations",priority=8)
	public void testForSuspendOrganizationPerformCRUDDestinations(String testCase, String suspendUserToken, String suspendOrgID, String AfterSuspendUsertoken,
			String AfterSuspendOrgID, String dataCenterID,String cloudID,String DestinationType,int expectedStatusCode, SpogMessageCode expectedErrorMessage) {

		test = ExtentManager.getNewTest(testCase);
		test.assignAuthor("Kanamarlapudi, Chandra Kanth");
		test.log(LogStatus.INFO, testCase);

		org4SPOGServer.suspendOrganizationWithCheck(suspendUserToken, suspendOrgID, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.INFO,"Going to validate organization_status parameter for the suspended organization");
			Response response = spogServer.getSpecifiedOrgProperties(ti.csr_token, suspendOrgID, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
			organization_status = response.then().extract().path("data.organization_status");
			if (organization_status.equals(SpogConstants.SUSPEND_ORGANIZATION)) {
				test.log(LogStatus.INFO,"validated organization_status parameter for the suspended organization Successfully");
				test.log(LogStatus.INFO,"Going to validate policy status for the suspended organization");
				boolean stat = validatePolicyStatus(ti.csr_token, suspendOrgID, test);
				if (stat) {
					test.log(LogStatus.PASS,"Organization with id: " + SpogConstants.SUSPEND_ORGANIZATION+ " is suspeneded successfully and In the Organization Properties shows ["+ organization_status + "] State");
					test.log(LogStatus.INFO,"Going to perform CRUD Operations for the Destinations for suspended Organzations");
					CRUDDestinations( AfterSuspendUsertoken, dataCenterID,  cloudID, AfterSuspendOrgID, DestinationType);
					test.log(LogStatus.PASS,"Sucessfully performed CRUD Operations for the Destinations for suspended Organzations");
				} else {
					test.log(LogStatus.FAIL, "Organization with id: " + SpogConstants.SUSPEND_ORGANIZATION
							+ " is suspeneded successfully but Policies for that organization doesnot shows disabled state");
					Assert.fail("Organization with id: " + SpogConstants.SUSPEND_ORGANIZATION
							+ " is suspeneded successfully but Policies for that organization doesnot shows disabled state");
				}
			} else {
				test.log(LogStatus.FAIL, "Organization with id: " + SpogConstants.SUSPEND_ORGANIZATION
						+ " is suspeneded successfully But In the Organization Properties shows [" + organization_status
						+ "] State instead of [" + SpogConstants.SUSPEND_ORGANIZATION + "]");
				Assert.fail("Organization with id: " + SpogConstants.SUSPEND_ORGANIZATION
						+ " is suspeneded successfully But In the Organization Properties shows [" + organization_status
						+ "] State instead of [" + SpogConstants.SUSPEND_ORGANIZATION + "]");
			}
		}
	}


	public void CRUDDestinations(String validToken,String dataCenterID, String cloudID,String orgID, String DestinationType) 
	{
		spogDestinationServer.setToken(validToken);
		HashMap<String, String> retention=null;
		retention= spogDestinationServer.composeRetention("0","0","7","0","0","0");
		String dest_id = spogDestinationServer.createCdDestination(validToken, orgID, dataCenterID, DestinationType, spogServer.ReturnRandom("dest"), DestinationStatus.running.toString(), volume_type.normal.toString(), 
				"7D", "7 Days", retention, SpogConstants.SUCCESS_POST, test);
		test.log(LogStatus.INFO, "Create Destionation for organzation is successfully");
		Response response = spogDestinationServer.getDestinationById(validToken, dest_id, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		test.log(LogStatus.INFO, "Get Destionation By ID for organzation is successfully");
		HashMap<String, Object> destinationInfo = new HashMap<>();
		HashMap<String, Object> cloudvolumeInfo = new HashMap<>();
		String prefix = spogServer.ReturnRandom("dest");
		destinationInfo.put("destination_name", prefix);
		cloudvolumeInfo.put("retention_id", "2M");
		cloudvolumeInfo.put("retention_name", "2 Months");
		destinationInfo.put("cloud_direct_volume", cloudvolumeInfo);
		spogDestinationServer.updateDestinationById(validToken, dest_id, destinationInfo,
				SpogConstants.SUCCESS_GET_PUT_DELETE);

		test.log(LogStatus.INFO, "Updated Destionations By ID for organzation is successfully");

		/*
		 * Delete proccess may not recycle. So this cannot be automated
		 * 
		 * spogDestinationServer.deletedestinationbydestination_Id(dest_id, validToken,
		 * test);*/
		 
		test.log(LogStatus.INFO, "Going to Recycle created Destination ID ["+dest_id+"]");
		spogDestinationInvoker.setToken(ti.csr_token);
		spogDestinationInvoker.recycleCDVolume(dest_id); 
		test.log(LogStatus.INFO, "Completed to Recycle created Destination ID");
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		test.log(LogStatus.INFO,"Waited 10 seconds");
	}


	@DataProvider(name = "testCasesForSuspendOrganizationPerformCRUDUsers")
	public Object[][] testDataForSuspendOrganizationPerformCRUDUsers() {
		return new Object[][] {
			{
				"Make sure that CSR Admin can create/get/edit/delete administrator user type for the suspended direct organization",
				ti.csr_token, ti.direct_org1_id,ti.csr_token,ti.direct_org1_id,SpogConstants.DIRECT_ADMIN,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null 
			},

			{
				"Make sure that CSR Admin can create/get/edit/delete monitor user type for the suspended direct organization",
				ti.csr_token, ti.direct_org1_id,ti.csr_token,ti.direct_org1_id,SpogConstants.DIRECT_MONITOR,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},

			{
				"Make sure that Direct Admin can create/get/edit/delete administrator user type for the suspended direct organization",
				ti.csr_token, ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_id,SpogConstants.DIRECT_ADMIN,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that Direct Admin can create/get/edit/delete monitor user type for the suspended direct organization",
				ti.csr_token, ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_id,SpogConstants.DIRECT_MONITOR,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},

			{
				"Make sure that CSR Admin can create/get/edit/delete normal msp administrator user type for the suspended normal organization",
				ti.csr_token,ti.normal_msp_org1_id,ti.csr_token,ti.normal_msp_org1_id,SpogConstants.MSP_ADMIN,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that CSR Admin can create/get/edit/delete normal msp account administrator user type for the suspended normal organization",
				ti.csr_token,ti.normal_msp_org1_id,ti.csr_token,ti.normal_msp_org1_id,SpogConstants.MSP_ACCOUNT_ADMIN,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},


			{
				"Make sure that CSR Admin can create/get/edit/delete normal msp monitor user type for the suspended normal organization",
				ti.csr_token,ti.normal_msp_org1_id,ti.csr_token,ti.normal_msp_org1_id,SpogConstants.MSP_MONITOR,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that CSR Admin can create/get/edit/delete administrator user type of sub organization of the suspended normal msp organization",
				ti.csr_token,ti.normal_msp_org1_id,ti.csr_token,ti.normal_msp1_suborg1_id,SpogConstants.DIRECT_ADMIN,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that CSR Admin can create/get/edit/delete monitor user type of sub organization of the suspended normal msp organization",
				ti.csr_token,ti.normal_msp_org1_id,ti.csr_token,ti.normal_msp1_suborg1_id,SpogConstants.DIRECT_MONITOR,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that CSR Admin can create/get/edit/delete normal msp administrator user type for the suspended sub organization of normal msp organization",
				ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_id,ti.csr_token,ti.normal_msp_org1_id,SpogConstants.MSP_ADMIN,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that CSR Admin can create/get/edit/delete normal msp account administrator user type for the suspended sub organization of normal msp organization",
				ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_id,ti.csr_token,ti.normal_msp_org1_id,SpogConstants.MSP_ACCOUNT_ADMIN,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that CSR Admin can create/get/edit/delete normal msp monitor user type for the suspended sub organization of normal msp organization",
				ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_id,ti.csr_token,ti.normal_msp_org1_id,SpogConstants.MSP_MONITOR,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that CSR Admin can create/get/edit/delete administrator user type of sub organization of the suspended sub organization of normal msp organization",
				ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_id,ti.csr_token,ti.normal_msp1_suborg1_id,SpogConstants.DIRECT_ADMIN,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that CSR Admin can create/get/edit/delete monitor user type of sub organization of the suspended sub organization of normal msp organization",
				ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_id,ti.csr_token,ti.normal_msp1_suborg1_id,SpogConstants.DIRECT_MONITOR,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that Normal MSP Admin can create/get/edit/delete normal msp administrator user type for the suspended normal organization",
				ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,SpogConstants.MSP_ADMIN,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that Normal MSP Admin can create/get/edit/delete normal msp account administrator user type for the suspended normal organization",
				ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,SpogConstants.MSP_ACCOUNT_ADMIN,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that Normal MSP Admin can create/get/edit/delete normal msp monitor user type for the suspended normal organization",
				ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,SpogConstants.MSP_MONITOR,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that Normal MSP Admin can create/get/edit/delete administrator user type of sub organization of the suspended normal msp organization",
				ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_id,SpogConstants.DIRECT_ADMIN,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that Normal MSP Admin can create/get/edit/delete monitor user type of sub organization of the suspended normal msp organization",
				ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_id,SpogConstants.DIRECT_MONITOR,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that Normal MSP Admin can create/get/edit/delete normal msp administrator user type for the suspended sub organization of normal msp organization",
				ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,SpogConstants.MSP_ADMIN,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that Normal MSP Admin can create/get/edit/delete normal msp account administrator user type for the suspended sub organization of normal msp organization",
				ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,SpogConstants.MSP_ACCOUNT_ADMIN,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that Normal MSP Admin can create/get/edit/delete normal msp monitor user type for the suspended sub organization of normal msp organization",
				ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,SpogConstants.MSP_MONITOR,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that Normal MSP Admin can create/get/edit/delete administrator user type of sub organization of the suspended sub organization of normal msp organization",
				ti.csr_token,ti.normal_msp1_suborg1_id,ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_id,SpogConstants.DIRECT_ADMIN,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that Normal MSP Admin can create/get/edit/delete monitor user type of sub organization of the suspended sub organization of normal msp organization",
				ti.csr_token,ti.normal_msp1_suborg1_id,ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_id,SpogConstants.DIRECT_MONITOR,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that Normal MSP Account Admin can create/get/edit/delete administrator user type of sub organization of the suspended normal msp organization",
				ti.csr_token,ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp1_suborg1_id,SpogConstants.DIRECT_ADMIN,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that Normal MSP Account Admin can create/get/edit/delete monitor user type of sub organization of the suspended normal msp organization",
				ti.csr_token,ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp1_suborg1_id,SpogConstants.DIRECT_MONITOR,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that Normal MSP Account Admin can create/get/edit/delete administrator user type of sub organization of the suspended sub organization of normal msp organization",
				ti.csr_token,ti.normal_msp1_suborg1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp1_suborg1_id,SpogConstants.DIRECT_ADMIN,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that Normal MSP Account Admin can create/get/edit/delete monitor user type of sub organization of the suspended sub organization of normal msp organization",
				ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp1_suborg1_id,SpogConstants.DIRECT_MONITOR,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that CSR Admin can create/get/edit/delete root msp administrator user type for the suspended root msp organization",
				ti.csr_token,ti.root_msp_org1_id,ti.csr_token,ti.root_msp_org1_id,SpogConstants.MSP_ADMIN,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that CSR Admin can create/get/edit/delete root msp account administrator user type for the suspended root msp organization",
				ti.csr_token,ti.root_msp_org1_id,ti.csr_token,ti.root_msp_org1_id,SpogConstants.MSP_ACCOUNT_ADMIN,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that CSR Admin can create/get/edit/delete root msp monitor user type for the suspended root msp organization",
				ti.csr_token,ti.root_msp_org1_id,ti.csr_token,ti.root_msp_org1_id,SpogConstants.MSP_MONITOR,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that CSR Admin can create/get/edit/delete root msp administrator user type for the suspended sub msp organization",
				ti.csr_token,ti.root_msp1_submsp_org1_id,ti.csr_token,ti.root_msp_org1_id,SpogConstants.MSP_ADMIN,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that CSR Admin can create/get/edit/delete root msp account administrator user type for the suspended sub msp organization",
				ti.csr_token,ti.root_msp1_submsp_org1_id,ti.csr_token,ti.root_msp_org1_id,SpogConstants.MSP_ACCOUNT_ADMIN,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that CSR Admin can create/get/edit/delete root msp monitor user type for the suspended sub msp organization",
				ti.csr_token,ti.root_msp1_submsp_org1_id,ti.csr_token,ti.root_msp_org1_id,SpogConstants.MSP_MONITOR,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that CSR Admin can create/get/edit/delete root msp administrator user type for the suspended sub organization of root msp organization",
				ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_id,ti.csr_token,ti.root_msp_org1_id,SpogConstants.MSP_ADMIN,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that CSR Admin can create/get/edit/delete root msp account administrator user type for the suspended sub organization of root msp organization",
				ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_id,ti.csr_token,ti.root_msp_org1_id,SpogConstants.MSP_ACCOUNT_ADMIN,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that CSR Admin can create/get/edit/delete root msp monitor user type for the suspended sub organization of root msp organization",
				ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_id,ti.csr_token,ti.root_msp_org1_id,SpogConstants.MSP_MONITOR,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that CSR Admin can create/get/edit/delete root msp administrator user type for the suspended sub organization of sub msp organization",
				ti.root_msp1_submsp1_user1_token,ti.msp1_submsp1_sub_org1_id,ti.csr_token,ti.root_msp_org1_id,SpogConstants.MSP_ADMIN,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that CSR Admin can create/get/edit/delete root msp account administrator user type for the suspended sub organization of sub msp organization",
				ti.root_msp1_submsp1_user1_token,ti.msp1_submsp1_sub_org1_id,ti.csr_token,ti.root_msp_org1_id,SpogConstants.MSP_ACCOUNT_ADMIN,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that CSR Admin can create/get/edit/delete root msp monitor user type for the suspended sub organization of sub msp organization",
				ti.root_msp1_submsp1_user1_token,ti.msp1_submsp1_sub_org1_id,ti.csr_token,ti.root_msp_org1_id,SpogConstants.MSP_MONITOR,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that CSR Admin can create/get/edit/delete sub msp administrator user type for the suspended root msp organization",
				ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.csr_token,ti.root_msp1_submsp_org1_id,SpogConstants.MSP_ADMIN,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that CSR Admin can create/get/edit/delete sub msp account administrator user type for the suspended root msp organization",
				ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.csr_token,ti.root_msp1_submsp_org1_id,SpogConstants.MSP_ACCOUNT_ADMIN,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that CSR Admin can create/get/edit/delete sub msp monitor user type for the suspended root msp organization",
				ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.csr_token,ti.root_msp1_submsp_org1_id,SpogConstants.MSP_MONITOR,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that CSR Admin can create/get/edit/delete sub msp administrator user type for the suspended sub organization of root msp organization",
				ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_id,ti.csr_token,ti.root_msp1_submsp_org1_id,SpogConstants.MSP_ADMIN,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that CSR Admin can create/get/edit/delete sub msp account administrator user type for the suspended sub organization of root msp organization",
				ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_id,ti.csr_token,ti.root_msp1_submsp_org1_id,SpogConstants.MSP_ACCOUNT_ADMIN,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that CSR Admin can create/get/edit/delete sub msp monitor user type for the suspended sub organization of root msp organization",
				ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_id,ti.csr_token,ti.root_msp1_submsp_org1_id,SpogConstants.MSP_MONITOR,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that CSR Admin can create/get/edit/delete sub msp administrator user type for the suspended sub organization of sub msp organization",
				ti.root_msp1_submsp1_user1_token,ti.msp1_submsp1_sub_org1_id,ti.csr_token,ti.root_msp1_submsp_org1_id,SpogConstants.MSP_ADMIN,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that CSR Admin can create/get/edit/delete sub msp account administrator user type for the suspended sub organization of sub msp organization",
				ti.root_msp1_submsp1_user1_token,ti.msp1_submsp1_sub_org1_id,ti.csr_token,ti.root_msp1_submsp_org1_id,SpogConstants.MSP_ACCOUNT_ADMIN,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that CSR Admin can create/get/edit/delete sub msp monitor user type for the suspended sub organization of sub msp organization",
				ti.root_msp1_submsp1_user1_token,ti.msp1_submsp1_sub_org1_id,ti.csr_token,ti.root_msp1_submsp_org1_id,SpogConstants.MSP_MONITOR,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that CSR Admin can create/get/edit/delete administrator user type of sub organization of suspended root msp organization",
				ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_id,ti.csr_token,ti.root_msp1_suborg1_id,SpogConstants.DIRECT_ADMIN,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that CSR Admin can create/get/edit/delete monitor user type for the suspended root msp organization",
				ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_id,ti.csr_token,ti.root_msp1_suborg1_id,SpogConstants.DIRECT_MONITOR,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that CSR Admin can create/get/edit/delete administrator user type of sub organization of suspended sub msp organization",
				ti.root_msp_org1_user1_token,ti.root_msp1_submsp_org1_id,ti.csr_token,ti.msp1_submsp1_sub_org1_id,SpogConstants.DIRECT_ADMIN,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that CSR Admin can create/get/edit/delete monitor user type for the suspended sub msp organization",
				ti.root_msp_org1_user1_token,ti.root_msp1_submsp_org1_id,ti.csr_token,ti.msp1_submsp1_sub_org1_id,SpogConstants.DIRECT_MONITOR,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that Root MSP Admin can create/get/edit/delete root msp administrator user type for the suspended root msp organization",
				ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp_org1_id,SpogConstants.MSP_ADMIN,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that Root MSP Admin can create/get/edit/delete root msp account administrator user type for the suspended root msp organization",
				ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp_org1_id,SpogConstants.MSP_ACCOUNT_ADMIN,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that Root MSP Admin can create/get/edit/delete root msp monitor user type for the suspended root msp organization",
				ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp_org1_id,SpogConstants.MSP_MONITOR,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that Root MSP Admin can create/get/edit/delete sub msp administrator user type for the suspended root msp organization",
				ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp1_submsp_org1_id,SpogConstants.MSP_ADMIN,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that Root MSP Admin can create/get/edit/delete monitor user type for the suspended root msp organization",
				ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_id,SpogConstants.DIRECT_MONITOR,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that Root MSP Account Admin can create/get/edit/delete administrator user type of sub organization of suspended root msp organization",
				ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp1_suborg1_id,SpogConstants.DIRECT_ADMIN,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
			{
				"Make sure that Root MSP Account Admin can create/get/edit/delete monitor user type for the suspended root msp organization",
				ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp1_suborg1_id,SpogConstants.DIRECT_MONITOR,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null
			},
		};
	}

	@Test(dataProvider = "testCasesForSuspendOrganizationPerformCRUDUsers",priority=9)
	public void testForSuspendOrganizationPerformCRUDUsers(String testCase, String suspendUserToken, String suspendOrgID, String AfterSuspendUsertoken,
			String AfterSuspendOrgID, String role_id,int expectedStatusCode, SpogMessageCode expectedErrorMessage) {


		test = ExtentManager.getNewTest(testCase);
		test.assignAuthor("Kanamarlapudi, Chandra Kanth");
		System.out.println(testCase);
		resumeOrg();
		test.log(LogStatus.INFO, testCase);

		org4SPOGServer.suspendOrganizationWithCheck(suspendUserToken, suspendOrgID, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		test.log(LogStatus.INFO, "Going to validate the organization_status parameter for the suspended organization");
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			Response response = spogServer.getSpecifiedOrgProperties(ti.csr_token, suspendOrgID, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
			organization_status = response.then().extract().path("data.organization_status");
			if (organization_status.equals(SpogConstants.SUSPEND_ORGANIZATION)) {
				test.log(LogStatus.INFO, "validated the organization_status parameter for the suspended organization succesfully");

				test.log(LogStatus.INFO, "Going to validate the policy status for the suspended organization");
				boolean stat = validatePolicyStatus(ti.csr_token, suspendOrgID, test);
				if (stat) {
					test.log(LogStatus.INFO,"Organization with id: " + SpogConstants.SUSPEND_ORGANIZATION+ " is suspeneded successfully and In the Organization Properties shows ["+ organization_status + "] State");
					CRUDOperationsForUsers( AfterSuspendUsertoken, AfterSuspendOrgID, role_id);
					test.log(LogStatus.PASS, "Successfully Completed CRUD Operations for Users for the Suspended Organization");
				} else {
					test.log(LogStatus.FAIL, "Organization with id: " + SpogConstants.SUSPEND_ORGANIZATION
							+ " is suspended successfully but Policies for that organization doesnot shows disabled state");
					//org4SPOGServer.resumeOrganization(suspendUserToken, suspendOrgID, expectedStatusCode, test);
					Assert.fail("Organization with id: " + SpogConstants.SUSPEND_ORGANIZATION
							+ " is suspended successfully but Policies for that organization doesnot shows disabled state");
				}
			} else {
				test.log(LogStatus.FAIL, "Organization with id: " + SpogConstants.SUSPEND_ORGANIZATION
						+ " is suspended successfully But In the Organization Properties shows [" + organization_status
						+ "] State instead of [" + SpogConstants.SUSPEND_ORGANIZATION + "]");
				Assert.fail("Organization with id: " + SpogConstants.SUSPEND_ORGANIZATION
						+ " is suspended successfully But In the Organization Properties shows [" + organization_status
						+ "] State instead of [" + SpogConstants.SUSPEND_ORGANIZATION + "]");
			}
		}
	}

	public void CRUDOperationsForUsers(String validToken,String organization_id,String role_id)
	{
		//create user
		spogServer.setToken(validToken);

		String first_name = spogServer.ReturnRandom("first");
		String last_name = spogServer.ReturnRandom("last");
		String email = spogServer.ReturnRandom("email")+"@spogqa.com";
		String phone = "9042760429";
		String password = "Mclaren@2020";
		String preference_language= "en";
		String user_id = spogServer.createUserWithCheck(first_name, last_name, email, phone, role_id, organization_id, preference_language, password, SpogConstants.SUCCESS_POST, null, test);
		test.log(LogStatus.INFO, "Able to create User Successfully after Suspended Organization");

		// update user by firstname
		Response response = spogServer.updateUserById(user_id, "", "", "newFirstName", "", "", "", test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		test.log(LogStatus.INFO, "Able to update created User Successfully after Suspended Organization");
		//delete user by user id
		test.log(LogStatus.INFO,"delete user");
		spogServer.setToken(validToken);
		spogServer.DeleteUserById(user_id, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		test.log(LogStatus.INFO, "Able to delete created,modified User Successfully after Suspended Organization");
	}

	

}
