package api.policies;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.apache.commons.lang.Validate;
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
import Constants.volume_type;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import base.prepare.Is4Org;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;import net.bytebuddy.implementation.bytecode.assign.Assigner.EqualTypesOnly;

public class GetPoliciesOfLoggedUsersOrganization extends Is4Org {

	private TestOrgInfo ti;
	private SPOGServer spogServer;
	private SPOGDestinationServer spogDestinationServer;
	private Policy4SPOGServer policy4SPOGServer;
	private ExtentTest test;
	private String[] datacenters;

	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine, String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
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
		spogDestinationServer.setToken(ti.csr_token);
		datacenters = spogDestinationServer.getDestionationDatacenterID();

	}

	@DataProvider(name = "testCasesForGetPolicies")
	public Object[][] testCasesForGetPolicies() {
		return new Object[][] {
				// 200
				{ "Get Policies in direct org", ti.direct_org1_user1_token, ti.direct_org1_id,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Policies in direct org with csr token", ti.csr_token, ti.direct_org1_id,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Policies in direct org with csr read only token", ti.csr_readonly_token, ti.direct_org1_id,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Policies in msp sub org using msp token", ti.root_msp_org1_user1_token, ti.root_msp1_suborg1_id,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Policies in msp sub org using msp account admin token", ti.root_msp_org1_msp_accountadmin1_token,
						ti.root_msp1_suborg1_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Policies in msp sub org using sub org token", ti.root_msp1_suborg1_user1_token,
						ti.root_msp1_suborg1_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Policies in msp sub org using csr token", ti.csr_token, ti.root_msp1_suborg1_id,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Policies in msp sub org using csr read only token", ti.csr_readonly_token,
						ti.root_msp1_suborg1_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Policies in msp sub org using sub msp token", ti.root_msp1_submsp1_user1_token,
						ti.root_msp1_suborg1_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Policies in msp sub org using sub msp account admin token",
						ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_suborg1_id, SpogConstants.SUCCESS_GET_PUT_DELETE,
						null },
				{ "Get Policies in msp sub org using sub msp sub org token", ti.msp1_submsp1_suborg1_user1_token,
						ti.root_msp1_suborg1_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Policies in msp sub org using csr token", ti.csr_token, ti.root_msp1_suborg1_id,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Policies in msp sub org using csr read only token", ti.csr_readonly_token,
						ti.root_msp1_suborg1_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null },

				// 400
				{ "Get Policies filter by invalid organization id and direct org user token",
						ti.direct_org1_user1_token, null, SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.INVALID_ORGANIZATION_ID },
				{ "Get Policies filter by invalid organization id and msp org user token",
						ti.normal_msp_org1_user1_token, null, SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.INVALID_ORGANIZATION_ID },
				{ "Get Policies filter by invalid organization id and msp org account admin user token",
						ti.normal_msp_org1_msp_accountadmin1_token, null, SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.INVALID_ORGANIZATION_ID },
				{ "Get Policies filter by invalid organization id and msp sub org user token",
						ti.root_msp1_suborg1_user1_token, null, SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.INVALID_ORGANIZATION_ID },
				{ "Get Policies filter by invalid organization id and sub msp org user token",
						ti.root_msp1_submsp1_user1_token, null, SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.INVALID_ORGANIZATION_ID },
				{ "Get Policies filter by invalid organization id and submsp account admin user token",
						ti.root_msp1_submsp1_account_admin_token, null, SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.INVALID_ORGANIZATION_ID },
				{ "Get Policies filter by invalid organization id and sub msp sub org user token",
						ti.msp1_submsp1_suborg1_user1_token, null, SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.INVALID_ORGANIZATION_ID },
				{ "Get Policies filter by invalid organization id and csr user token", ti.csr_token, null,
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.INVALID_ORGANIZATION_ID },
				{ "Get Policies filter by invalid organization id and csr readonly user token", ti.csr_readonly_token,
						null, SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.INVALID_ORGANIZATION_ID },

				// 401
				{ "Get policies with invalid token", "invalid", ti.direct_org1_id, SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT },
				{ "Get policies with missing token", "", ti.direct_org1_id, SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTHENTICATION_FAILED },
				{ "Get policies with null as token", "invalid", ti.direct_org1_id, SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTHENTICATION_FAILED },

				// 403
				{ "Get Policies filter by other organization id and direct org user token", ti.direct_org1_user1_token,
						ti.root_msp1_suborg1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get Policies filter by other organization id and msp org user token", ti.normal_msp_org1_user1_token,
						ti.direct_org1_id, SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.INVALID_ORGANIZATION_ID },
				{ "Get Policies filter by other organization id and msp org account admin user token",
						ti.normal_msp_org1_msp_accountadmin1_token, ti.msp1_submsp1_sub_org1_id,
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.INVALID_ORGANIZATION_ID },
				{ "Get Policies filter by other organization id and msp sub org user token",
						ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg2_id,
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.INVALID_ORGANIZATION_ID },
				{ "Get Policies filter by other organization id and sub msp org user token",
						ti.root_msp1_submsp1_user1_token, ti.direct_org1_id, SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.INVALID_ORGANIZATION_ID },
				{ "Get Policies filter by other organization id and submsp account admin user token",
						ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp_org1_id,
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.INVALID_ORGANIZATION_ID },
				{ "Get Policies filter by other organization id and sub msp sub org user token",
						ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_sub_org2_id,
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.INVALID_ORGANIZATION_ID },

				// 404
				{ "Get Policies filter by random organization id and direct org user token", ti.direct_org1_user1_token,
						UUID.randomUUID().toString(), SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.INVALID_ORGANIZATION_ID },
				{ "Get Policies filter by random organization id and msp org user token",
						ti.normal_msp_org1_user1_token, UUID.randomUUID().toString(),
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.INVALID_ORGANIZATION_ID },
				{ "Get Policies filter by random organization id and msp org account admin user token",
						ti.normal_msp_org1_msp_accountadmin1_token, UUID.randomUUID().toString(),
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.INVALID_ORGANIZATION_ID },
				{ "Get Policies filter by random organization id and msp sub org user token",
						ti.root_msp1_suborg1_user1_token, UUID.randomUUID().toString(),
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.INVALID_ORGANIZATION_ID },
				{ "Get Policies filter by random organization id and sub msp org user token",
						ti.root_msp1_submsp1_user1_token, UUID.randomUUID().toString(),
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.INVALID_ORGANIZATION_ID },
				{ "Get Policies filter by random organization id and submsp account admin user token",
						ti.root_msp1_submsp1_account_admin_token, UUID.randomUUID().toString(),
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.INVALID_ORGANIZATION_ID },
				{ "Get Policies filter by random organization id and sub msp sub org user token",
						ti.msp1_submsp1_suborg1_user1_token, UUID.randomUUID().toString(),
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.INVALID_ORGANIZATION_ID },
				{ "Get Policies filter by random organization id and csr user token", ti.csr_token,
						UUID.randomUUID().toString(), SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.INVALID_ORGANIZATION_ID },
				{ "Get Policies filter by invalid organization id and csr readonly user token", ti.csr_readonly_token,
						UUID.randomUUID().toString(), SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.INVALID_ORGANIZATION_ID },

		};
	}

	@Test(dataProvider = "testCasesForGetPolicies")
	public void testGetPolicie(String testCase, String token, String orgId, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage) {

		test = ExtentManager.getNewTest(testCase);
		test.assignAuthor("Kanamarlapudi, Chandra Kanth");
		String filter = "organization_id="+orgId;
		
		Response response = policy4SPOGServer.getPolicies(token, filter, expectedStatusCode, test);
		validate(response, expectedStatusCode, expectedErrorMessage);
	}
	
	public void validate(Response response, int expectedStatusCode, SpogMessageCode expectedErrorMessage){
		
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			response.then().body("data.policy_id", not(isEmptyOrNullString()))
					.body("data.policy_name",
					equalTo("Cloud Direct Full System"));
			
			
		}else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
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
	
}
