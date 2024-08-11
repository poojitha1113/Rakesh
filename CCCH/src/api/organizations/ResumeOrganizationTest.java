package api.organizations;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.UUID;

import org.apache.http.client.ClientProtocolException;
import org.testng.Assert;
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
import InvokerServer.Org4SPOGServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGServer;
import base.prepare.Is4Org;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class ResumeOrganizationTest extends Is4Org{
	
	private TestOrgInfo ti;
	private SPOGServer spogServer;
	private Org4SPOGServer org4SPOGServer;
	private Policy4SPOGServer policy4SPOGServer;
	private String organization_status;
	private ExtentTest test;
	
	
	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine, String buildVersion) {
		organization_status= null;
		spogServer = new SPOGServer(baseURI, port);
		org4SPOGServer= new Org4SPOGServer(baseURI, port);
		policy4SPOGServer= new Policy4SPOGServer(baseURI, port);
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
	}
	
	
	@DataProvider(name="testcasesForResumeOrganizationAfterDisable")
	public Object[][] testDataForResumeOrganizationAfterDisable(){
		return new Object[][] {
				{"Make sure that CSR Admin able to disable policies of direct organization after resuming direct organization",ti.csr_token,ti.direct_org1_id,ti.csr_token,ti.direct_org1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null },
				{"Make sure that CSR Admin able to disable policies of normal msp organization after resuming normal msp organization",ti.csr_token,ti.normal_msp_org1_id,ti.csr_token,ti.normal_msp_org1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null },
				{"Make sure that CSR Admin able to disable policies of sub org of normal organization after resuming sub organization of normal organization",ti.csr_token,ti.normal_msp1_suborg1_id,ti.csr_token,ti.normal_msp1_suborg1_id, SpogConstants.SUCCESS_GET_PUT_DELETE,null },
				{"Make sure that CSR Admin able to disable policies of root msp organization after resuming root msp organization",ti.csr_token,ti.root_msp_org1_id,ti.csr_token,ti.root_msp_org1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null },
				{"Make sure that CSR Admin able to disable policies of sub org of root msp organization after resuming sub org of root msp organization",ti.csr_token,ti.root_msp1_suborg1_id,ti.csr_token,ti.root_msp1_suborg1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null },
				{"Make sure that CSR Admin able to disable policies of sub msp organization after resuming sub msp organization",ti.csr_token,ti.root_msp1_suborg1_id,ti.csr_token,ti.root_msp1_suborg1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null },
				{"Make sure that CSR Admin able to disable policies of sub org of sub msp organization after resuming sub org of sub msp organization",ti.csr_token,ti.msp1_submsp1_sub_org1_id,ti.csr_token,ti.msp1_submsp1_sub_org1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null },
				
				{"Make sure that Root MSP Admin able to disable policies of sub org of root msp organization after resuming sub org of root msp organization",ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_id,ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null },
				{"Make sure that Root MSP Admin able to disable policies of sub msp organization after resuming sub msp organization",ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_id,ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null },
				{"Make sure that Root MSP Account Admin able to disable policies of sub org of root msp organization after resuming sub org of root msp organization",ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp1_suborg1_id,ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp1_suborg1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null },
				{"Make sure that Root MSP Account Admin able to disable policies of sub msp organization after resuming sub msp organization",ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp1_suborg1_id,ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp1_suborg1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null },
				
				{"Make sure that sub MSP Admin able to disable policies of sub org of sub msp organization after resuming sub org of sub msp organization",ti.root_msp1_submsp1_user1_token,ti.msp1_submsp1_sub_org1_id,ti.root_msp1_submsp1_user1_token,ti.msp1_submsp1_sub_org1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null },
				{"Make sure that sub MSP Account Admin able to disable policies of sub org of sub msp organization after resuming sub org of sub msp organization",ti.root_msp1_submsp1_account_admin_token,ti.msp1_submsp1_sub_org1_id,ti.root_msp1_submsp1_account_admin_token,ti.msp1_submsp1_sub_org1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null },
				
				{"Make sure that normal MSP Admin able to disable policies of sub org of normaml msp organization after resuming sub org of normal msp organization",ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_id,ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null },
				{"Make sure that normal MSP Account Admin able to disable policies of sub org of normal msp organization after resuming sub org of normal msp organization",ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp1_suborg1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp1_suborg1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null },
				
				{"Make sure that CSR Admin able to disable policies of normal msp organization after performing normal msp admin resumes sub organization of normal organization",ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_id,ti.csr_token,ti.normal_msp1_suborg1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null },
				{"Make sure that CSR Admin able to disable policies of normal msp organization after performing normal msp account admin resumes sub organization of normal organization",ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp1_suborg1_id,ti.csr_token,ti.normal_msp1_suborg1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null },
				{"Make sure that CSR Admin able to disable policies of sub org of normal msp organization after performing normal msp admin resumes sub organization of normal organization",ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_id,ti.csr_token,ti.normal_msp1_suborg1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null },
				
			};

		}
	
		
	
	@DataProvider(name="testcasesForResumeOrganizationAfterEnable")
	public Object[][] testDataForResumeOrganizationAfterEnable(){
		return new Object[][] {
				{"Make sure that CSR Admin able to enable policies of direct organization after resuming direct organization",ti.csr_token,ti.direct_org1_id,ti.csr_token,ti.direct_org1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null },
				{"Make sure that CSR Admin able to enable policies of normal msp organization after resuming normal msp organization",ti.csr_token,ti.normal_msp_org1_id,ti.csr_token,ti.normal_msp_org1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null },
				{"Make sure that CSR Admin able to enable policies of sub org of normal organization after resuming sub organization of normal organization",ti.csr_token,ti.normal_msp1_suborg1_id,ti.csr_token,ti.normal_msp1_suborg1_id, SpogConstants.SUCCESS_GET_PUT_DELETE,null },
				{"Make sure that CSR Admin able to enable policies of root msp organization after resuming root msp organization",ti.csr_token,ti.root_msp_org1_id,ti.csr_token,ti.root_msp_org1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null },
				{"Make sure that CSR Admin able to enable policies of sub org of root msp organization after resuming sub org of root msp organization",ti.csr_token,ti.root_msp1_suborg1_id,ti.csr_token,ti.root_msp1_suborg1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null },
				{"Make sure that CSR Admin able to enable policies of sub msp organization after resuming sub msp organization",ti.csr_token,ti.root_msp1_suborg1_id,ti.csr_token,ti.root_msp1_suborg1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null },
				{"Make sure that CSR Admin able to enable policies of sub org of sub msp organization after resuming sub org of sub msp organization",ti.csr_token,ti.msp1_submsp1_sub_org1_id,ti.csr_token,ti.msp1_submsp1_sub_org1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null },
				
				{"Make sure that Root MSP Admin able to enable policies of sub org of root msp organization after resuming sub org of root msp organization",ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_id,ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null },
				{"Make sure that Root MSP Admin able to enable policies of sub msp organization after resuming sub msp organization",ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_id,ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null },
				{"Make sure that Root MSP Account Admin able to enable policies of sub org of root msp organization after resuming sub org of root msp organization",ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp1_suborg1_id,ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp1_suborg1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null },
				{"Make sure that Root MSP Account Admin able to enable policies of sub msp organization after resuming sub msp organization",ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp1_suborg1_id,ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp1_suborg1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null },
				
				{"Make sure that sub MSP Admin able to enable policies of sub org of sub msp organization after resuming sub org of sub msp organization",ti.root_msp1_submsp1_user1_token,ti.msp1_submsp1_sub_org1_id,ti.root_msp1_submsp1_user1_token,ti.msp1_submsp1_sub_org1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null },
				{"Make sure that sub MSP Account Admin able to enable policies of sub org of sub msp organization after resuming sub org of sub msp organization",ti.root_msp1_submsp1_account_admin_token,ti.msp1_submsp1_sub_org1_id,ti.root_msp1_submsp1_account_admin_token,ti.msp1_submsp1_sub_org1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null },
				
				{"Make sure that normal MSP Admin able to enable policies of sub org of normaml msp organization after resuming sub org of normal msp organization",ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_id,ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null },
				{"Make sure that normal MSP Account Admin able to enable policies of sub org of normal msp organization after resuming sub org of normal msp organization",ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp1_suborg1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp1_suborg1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null },
				
				{"Make sure that CSR Admin able to enable policies of normal msp organization after performing normal msp admin resumes sub organization of normal organization",ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_id,ti.csr_token,ti.normal_msp1_suborg1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null },
				{"Make sure that CSR Admin able to enable policies of normal msp organization after performing normal msp account admin resumes sub organization of normal organization",ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp1_suborg1_id,ti.csr_token,ti.normal_msp1_suborg1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null },
				{"Make sure that CSR Admin able to enable policies of sub org of normal msp organization after performing normal msp admin resumes sub organization of normal organization",ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_id,ti.csr_token,ti.normal_msp1_suborg1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null },
				
			};

		}
	
	
	

	@DataProvider(name="testcasesForResumeOrganizationMis")
	public Object[][] testDataForResumeOrganizationMis(){
		return new Object[][] {
			{"Make sure that unable to resume suspended sub organization of sub msp using sub org admin token where sub msp organization is suspended using root msp admin",ti.root_msp_org1_user1_token,ti.msp1_submsp1_suborg1_user1_token,ti.root_msp1_submsp_org1_id,ti.msp1_submsp1_sub_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to resume suspended sub organization of sub msp using sub msp admin token where sub msp organization is suspended using root msp admin",ti.root_msp_org1_user1_token,ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp_org1_id,ti.msp1_submsp1_sub_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to resume suspended sub organization of sub msp using sub org admin token where sub msp organization is suspended using csr admin",ti.csr_token,ti.msp1_submsp1_suborg1_user1_token,ti.root_msp1_submsp_org1_id,ti.msp1_submsp1_sub_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to resume suspended sub organization of sub msp using sub msp admin token where sub msp organization is suspended using csr admin",ti.csr_token,ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp_org1_id,ti.msp1_submsp1_sub_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to resume suspended sub organization of sub msp using root msp admin token where sub org of sub msp is suspended using sub msp admin",ti.msp1_submsp1_suborg1_user1_token,ti.root_msp_org1_user1_token,ti.msp1_submsp1_sub_org1_id,ti.root_msp1_submsp_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that able to resume suspended sub organization of sub msp using csr token where sub msp organization is suspended using root msp admin",ti.root_msp_org1_user1_token,ti.csr_token,ti.root_msp1_submsp_org1_id,ti.msp1_submsp1_sub_org1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"Make sure that able to resume suspended sub organization of normal msp using csr token where normal msp organization is suspended using csr admin",ti.csr_token,ti.csr_token,ti.normal_msp_org1_id,ti.normal_msp1_suborg1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null}
		};

	}
	
	
	
	@Test(dataProvider="testcasesForResumeOrganization")
	public void testResumeOrganizationMis(String testCase, String parenttoken, String childToken,String parenOrgID,String orgId, int expectedStatusCode,SpogMessageCode expectedErrorMessage)
	{

		test = ExtentManager.getNewTest(testCase);
		test.assignAuthor("Kanamarlapudi, Chandra Kanth");
		test.log(LogStatus.INFO, "Suspending the organization with id:"+orgId);
		org4SPOGServer.suspendOrganization(parenttoken, parenOrgID, SpogConstants.SUCCESS_GET_PUT_DELETE, test);

		test.log(LogStatus.INFO, "Perfoming resume operation test case with organization id:"+orgId);
		org4SPOGServer.resumeOrganizationWithCheck(childToken, orgId, expectedStatusCode, expectedErrorMessage, test);
		try 
		{
			if(expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
				test.log(LogStatus.INFO, "Validating the organization status parameter in organization properties");
				Response response=spogServer.getSpecifiedOrgProperties(ti.csr_token, orgId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);

				organization_status = response.then().extract().path("data.organization_status");
				Assert.assertNull(organization_status,"Organization_Status Field should not be displayed for the Resume/Enabled Organization");

			}
		}catch(Exception e)
		{

		}
		finally {
			org4SPOGServer.resumeOrganization(parenttoken, parenOrgID, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
			test.log(LogStatus.INFO, "Resume the organization with id:"+parenOrgID);
		}
	}
	
		
	
	
	@DataProvider(name="testcasesForResumeOrganization")
	public Object[][] testDataForResumeOrganization(){
		return new Object[][] {

			//200 Test scenerios
			{"Make sure that CSR Admin can able to resume the suspended direct organization",ti.csr_token,ti.direct_org1_id, SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"Make sure that CSR Admin can able to resume the suspended Normal MSP organization",ti.csr_token,ti.normal_msp_org1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"Make sure that CSR Admin can able to resume the suspended sub org of Normal MSP organization",ti.csr_token,ti.normal_msp1_suborg1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"Make sure that CSR Admin can able to resume the suspended root MSP organization",ti.csr_token,ti.root_msp_org1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"Make sure that CSR Admin can able to resume the suspended sub org of Root MSP organization",ti.csr_token,ti.root_msp1_suborg1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"Make sure that CSR Admin can able to resume the suspended sub MSP organization",ti.csr_token,ti.root_msp1_submsp_org1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"Make sure that CSR Admin can able to resume the suspended sub org of Sub MSP organization",ti.csr_token,ti.msp1_submsp1_sub_org1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"Make sure that Normal MSP Admin can able to resume the suspended sub organization",ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"Make sure that Normal MSP account can able to resume the suspended assigned sub organization",ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp1_suborg1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"Make sure that Root MSP Admin can able to resume the suspended sub MSP organization",ti.root_msp_org1_user1_token,ti.root_msp1_submsp_org1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"Make sure that Root MSP Admin can able to resume the suspended sub organization",ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"Make sure that Root MSP account Admin can able to resume the suspended assigned sub msp's sub organization",ti.root_msp1_submsp1_account_admin_token,ti.msp1_submsp1_sub_org1_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null},

			//400 error Code
			{"Make sure that CSR Admin unable to resume invalid organization",ti.csr_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_ORGANIZATIONID_IS_NOT_UUID},
			{"Make sure that Root MSP Admin unable to resume invalid sub organization",ti.root_msp_org1_user1_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_ORGANIZATIONID_IS_NOT_UUID},
			{"Make sure that Root MSP account Admin unable to resume invalid organization",ti.root_msp_org1_msp_accountadmin1_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_ORGANIZATIONID_IS_NOT_UUID},
			{"Make sure that normal MSP Admin unable to resume invalid organization",ti.normal_msp_org1_user1_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_ORGANIZATIONID_IS_NOT_UUID},
			{"Make sure that normal account Admin unable to resume invalid organization",ti.normal_msp_org1_msp_accountadmin1_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_ORGANIZATIONID_IS_NOT_UUID},
			{"Make sure that sub msp Admin unable to resume invalid organization",ti.root_msp1_submsp1_user1_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_ORGANIZATIONID_IS_NOT_UUID},
			{"Make sure that sub msp account Admin unable to resume invalid organization",ti.root_msp1_submsp1_account_admin_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_ORGANIZATIONID_IS_NOT_UUID},


			//401 Error Code
			{"Make sure that, user unable to resume the valid suspended organization with invalid Token","invalid", ti.root_msp1_suborg1_id, SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT},
			{"Make sure that, user unable to resume the valid suspended organization with empty Token","", ti.root_msp1_suborg1_id, SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT},
			{"Make sure that, user unable to resume the valid suspended organization invalid Token",null, ti.root_msp1_suborg1_id, SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT},


			//403 Error Codes
			{"Make sure that unable to resume direct organization using direct Admin Token",ti.direct_org1_user1_token,  ti.direct_org2_id,  SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to resume root msp organization using direct Admin Token",ti.direct_org1_user1_token,ti.root_msp_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to resume sub msp organization using direct Admin Token",ti.direct_org1_user1_token,ti.root_msp1_submsp_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to resume sub org of root msp organization using direct Admin Token",ti.direct_org1_user1_token, ti.root_msp1_suborg1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to resume sub org of sub msp organization using direct Admin Token",ti.direct_org1_user1_token, ti.msp1_submsp1_sub_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to resume sub org of normal msp organization using direct Admin Token",ti.direct_org1_user1_token, ti.normal_msp1_suborg1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to resume normal msp organization using direct Admin Token",ti.direct_org1_user1_token,  ti.normal_msp_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},

			{"Make sure that unable to resume unassigned other sub organization using normal msp Admin token",ti.root_msp_org1_user1_token,ti.normal_msp1_suborg2_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to resume unassigned sub organization using normal msp account Admin token",ti.root_msp_org1_msp_accountadmin1_token,ti.normal_msp1_suborg2_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to resume unassigned other sub organization using sub msp Admin token",ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp_org2_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to resume unassigned sub organization using sub msp account Admin token",ti.root_msp1_submsp1_account_admin_token,ti.msp1_submsp1_sub_org2_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to resume some other sub organization which is not added to root msp organization using root msp Admin token",ti.root_msp_org1_user1_token,ti.root_msp2_suborg2_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to resume unassigned sub organization using root msp account Admin token",ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp1_suborg2_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to resume some other sub organization which is not added to root msp organization other sub organization using csr Admin token",ti.csr_token,ti.root_msp2_suborg2_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},



			{"Make sure that unable to resume direct organization using CSR Read-only Token",ti.csr_readonly_token,ti.direct_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to resume root msp organization using CSR Read-only Token",ti.csr_readonly_token,ti.root_msp_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to resume sub msp organization using CSR Read-only Token",ti.csr_readonly_token,ti.root_msp1_submsp_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to resume sub org of sub msp using CSR Read-only Token",ti.csr_readonly_token,ti.msp1_submsp1_sub_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to resume sub org of root msp organization using CSR Read-only Token",ti.csr_readonly_token,ti.root_msp1_suborg1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to resume normal msp organization using CSR Read-only  Token",ti.csr_readonly_token,ti.normal_msp_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to resume sub org of normal msp account using CSR Read-only Token",ti.csr_readonly_token,ti.normal_msp1_suborg1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},


			{"Make sure that unable to resume direct organization using root msp monitor token",ti.root_msp_org1_monitor_user1_token,ti.direct_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to resume root msp organization using root msp monitor token",ti.root_msp_org1_monitor_user1_token,ti.root_msp_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to resume sub msp organization using root msp monitor token",ti.root_msp_org1_monitor_user1_token,ti.root_msp1_submsp_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to resume sub org of sub msp using root msp monitor token",ti.root_msp_org1_monitor_user1_token,ti.msp1_submsp1_sub_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to resume sub org of root msp organization using root msp monitor token",ti.root_msp_org1_monitor_user1_token,ti.root_msp1_suborg1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to resume normal msp organization using root msp monitor token",ti.root_msp_org1_monitor_user1_token,ti.normal_msp1_suborg1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to resume sub org of normal msp account using root msp monitor token",ti.root_msp_org1_monitor_user1_token,ti.normal_msp1_suborg1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to resume normal msp organization using normal msp monitor token",ti.normal_msp_org1_monitor_user1_token,ti.normal_msp_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to resume sub org of normal msp organization using normal msp monitor token",ti.normal_msp_org1_monitor_user1_token,ti.normal_msp1_suborg1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to resume root msp organization using sub msp monitor token",ti.root_msp1_submsp1_monitor_user1_token,ti.root_msp_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to resume sub org of root msp organization using sub msp monitor token",ti.root_msp1_submsp1_monitor_user1_token,ti.root_msp1_suborg1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to resume sub msp organization using sub msp monitor token",ti.root_msp1_submsp1_monitor_user1_token,ti.root_msp1_submsp_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Make sure that unable to resume sub org of sub msp organization using sub msp monitor token",ti.root_msp1_submsp1_monitor_user1_token,ti.msp1_submsp1_sub_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},



			//404 Error Code
			{"Make sure that unable to resume organization which is doesn’t exit using CSR Token",ti.csr_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED},
			{"Make sure that unable to resume organization which is doesn’t exit using Root msp Admin Token",ti.root_msp_org1_user1_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED},
			{"Make sure that unable to resume organization which is doesn’t exit using Root msp account Admin Token",ti.root_msp_org1_msp_accountadmin1_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED},
			{"Make sure that unable to resume organization which is doesn’t exit using normal msp Admin Token",ti.normal_msp_org1_user1_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED},
			{"Make sure that unable to resume organization which is doesn’t exit using normal msp account Admin Token",ti.normal_msp_org1_msp_accountadmin1_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED},
			{"Make sure that unable to resume organization which is doesn’t exit using sub msp Admin Token",ti.root_msp1_submsp1_user1_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED},
			{"Make sure that unable to resume organization which is doesn’t exit using sub msp account admin Token",ti.root_msp1_submsp1_account_admin_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED}
		};

	}
			
			


	
	/*@DataProvider(name="testcasesForResumeOrganization")
	public Object[][] testDataForResumeOrganization(){
		return new Object[][] {
				//200-- CSRToken
				{"resume the sub organization using CSR Admin", ti.csr_token, ti.root_msp1_suborg1_id, SpogConstants.SUCCESS_GET_PUT_DELETE,null},
				{"resume the sub msp's sub organization using CSR Admin", ti.csr_token, ti.msp1_submsp1_sub_org1_id, SpogConstants.SUCCESS_GET_PUT_DELETE,null},
				//200-- Root MSP Token
				{"resume the sub organization using root MSP Admin", ti.root_msp_org1_user1_token, ti.root_msp1_suborg1_id, SpogConstants.SUCCESS_GET_PUT_DELETE,null},
				{"resume the sub organization using root MSP Account Admin", ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp1_suborg1_id, SpogConstants.SUCCESS_GET_PUT_DELETE,null},
				//200-- Sub MSP Token
				{"resume the sub msp's sub organization using sub MSP Admin", ti.root_msp1_submsp1_user1_token, ti.msp1_submsp1_sub_org1_id, SpogConstants.SUCCESS_GET_PUT_DELETE,null},
				//200-- Sub MSP account Token
				{"resume the organization using sub MSP Account Admin sub organization", ti.root_msp1_submsp1_account_admin_token, ti.msp1_submsp1_sub_org1_id, SpogConstants.SUCCESS_GET_PUT_DELETE,null},
				//200-- Normal MSP Token
				{"resume the sub organization using normal MSP Admin", ti.normal_msp_org1_user1_token, ti.normal_msp1_suborg1_id, SpogConstants.SUCCESS_GET_PUT_DELETE,null},
				//200-- Normal MSP Account Token
				{"resume the sub organization using normal MSP Account Admin", ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp1_suborg1_id, SpogConstants.SUCCESS_GET_PUT_DELETE,null},

				 //400
	            {"resume the organization using CSR Admin with invalid organization",ti.csr_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_ORGANIZATIONID_IS_NOT_UUID},
	            {"resume the organization using root MSP Admin with invalid organization", ti.root_msp_org1_user1_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_ORGANIZATIONID_IS_NOT_UUID},
	            {"resume the organization using root MSP Account Admin with invalid organization", ti.root_msp_org1_msp_accountadmin1_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_ORGANIZATIONID_IS_NOT_UUID},
	            {"resume the organization using sub MSP Admin with invalid organization", ti.root_msp1_submsp1_user1_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_ORGANIZATIONID_IS_NOT_UUID},
	            {"resume the organization using sub MSP Account Admin with invalid organization", ti.root_msp1_submsp1_account_admin_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_ORGANIZATIONID_IS_NOT_UUID},
	            {"resume the organization using Normal MSP Admin with invalid organization", ti.normal_msp_org1_user1_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_ORGANIZATIONID_IS_NOT_UUID},
	            {"resume the organization using Normal MSP Account Admin with invalid organization", ti.normal_msp_org1_msp_accountadmin1_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_ORGANIZATIONID_IS_NOT_UUID},
	          
  			  //401
	            {"resume the organization using invalid Token with valid organization", "invalid", ti.root_msp1_suborg1_id, SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT},
	            {"resume the organization using empty Token with valid organization", "", ti.root_msp1_suborg1_id, SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTHENTICATION_FAILED},
	            {"resume the organization using null Token with valid organization", null, ti.root_msp1_suborg1_id, SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT},

	            
	            //404
	            {"resume the organization using valid CSR Admin Token where organization doesnot exit", ti.csr_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED},
	            {"resume the organization using valid MSP Admin Token where organization doesnot exit", ti.root_msp_org1_user1_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED},
	            {"resume the organization using valid MSP Account Admin Token where organization doesnot exit", ti.root_msp_org1_msp_accountadmin1_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED},
	            {"resume the organization using valid sub MSP Admin where organization doesnot exit", ti.root_msp1_submsp1_user1_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED},
	            {"resume the organization using valid sub MSP Account Admin where organization doesnot exit", ti.root_msp1_submsp1_account_admin_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED},
	            {"resume the organization using valid normal MSP Admin where organization doesnot exit", ti.normal_msp_org1_user1_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED},

	 
	            //403
				{"resume the organization using direct org user token with sub msp sub orgnazation1",ti.direct_org1_user1_token,   ti.msp1_submsp1_sub_org1_id,  SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
				{"resume the organization using msp org user token with root msp organzation1",ti.root_msp_org1_user1_token,ti.msp2_submsp1_sub_org1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
				{"resume the organization using sub org user token with with sub msp sub orgnazation1",ti.root_msp1_suborg1_user1_token,ti.msp2_submsp2_sub_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
				{"resume the organization using sub msp org user token with msp2 submsp2 sub org1 id",ti.root_msp1_submsp1_user1_token,ti.msp2_submsp2_sub_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
				{"resume the organization using sub msp sub org user token with msp2 submsp2 sub org2 id",ti.msp1_submsp1_suborg1_user1_token,ti.msp2_submsp2_sub_org2_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
				{"resume the organization using msp_account_admin token msp2 submsp2 sub org1 id",ti.root_msp_org1_msp_accountadmin1_token,ti.msp2_submsp2_sub_org1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
				

	         
	        };
				
	}
	
	*/
	
	
	
	
	@Test(dataProvider="testcasesForResumeOrganization")
	public void testResumeOrganization(String testCase, String token, String orgId, int expectedStatusCode,SpogMessageCode expectedErrorMessage)
	{
		test = ExtentManager.getNewTest(testCase);
		test.assignAuthor("Kanamarlapudi, Chandra Kanth");

		if(expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.INFO, "Suspending the organization with id:"+orgId+" to check resume operation");
			org4SPOGServer.suspendOrganization(token, orgId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		}

		test.log(LogStatus.INFO, "Perfoming resume operation test case with organization id:"+orgId);
		org4SPOGServer.resumeOrganizationWithCheck(token, orgId, expectedStatusCode, expectedErrorMessage, test);

		if(expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.INFO, "Validating the organization status parameter in organization properties");
			Response response=spogServer.getSpecifiedOrgProperties(token, orgId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);

			organization_status = response.then().extract().path("data.organization_status");
			Assert.assertNull(organization_status,"Organization_Status Field should not be displayed for the Resume/Enabled Organization");

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
