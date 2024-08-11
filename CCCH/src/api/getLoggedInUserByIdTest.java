package api;

import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;

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
import InvokerServer.Log4SPOGServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class getLoggedInUserByIdTest extends base.prepare.Is4Org {

	private SPOGServer spogServer;
	private UserSpogServer userSpogServer;
	private GatewayServer gatewayServer;
	private Log4SPOGServer spogLogServer;
	private TestOrgInfo ti;

	private ExtentTest test;

	LocalDate date = LocalDate.now();
	LocalDate yesterday = date.minusDays(1);
	LocalDate tomorrow = yesterday.plusDays(2);
	int passedcases = 0;
	int failedcases = 0;
	int skippedcases = 0;
	private String org_model_prefix = this.getClass().getSimpleName();

	@BeforeClass
	@Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword","csrReadOnlyAdminName","csrReadOnlyAdminPassword","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword,String csrReadOnlyAdminName,String csrReadOnlyAdminPassword, String logFolder,String runningMachine,String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogLogServer = new Log4SPOGServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(), logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Sykam.Malleswari";

		Nooftest = 0;
		// Used for creating a build number with dateformat
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date = new java.util.Date();
		this.buildVersion = buildVersion + "_" + dateFormater.format(date);

		BQName = this.getClass().getSimpleName();
		this.runningMachine = runningMachine;
		System.out.println("The value of hit is " + count1.isstarttimehit());
		ti = new TestOrgInfo(spogServer, test);	
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
		
	}


	@DataProvider(name = "organizationAndUserInfo_1")
	public final Object[][] getOrganizationAndUserInfo_valid1() {
		return new Object[][] {
			//Direct Organization
			{ "Get the direct organization user with Direct token", ti.direct_org1_user1_id, ti.direct_org1_user1_token,ti.direct_org1_user1_email, ti.direct_org1_id },
			{ "Get the direct organization user with Csr token", ti.direct_org1_user1_id, ti.csr_token,ti.direct_org1_user1_email, ti.direct_org1_id },
			{ "Get the direct organization user with Csr read only  token", ti.direct_org1_user1_id, ti.csr_readonly_token,ti.direct_org1_user1_email, ti.direct_org1_id },

			//Normal Msp Organziarion
			{ "Get the Normal msp organization user with msp org token", ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_email, ti.normal_msp_org1_id },
			{ "Get the Normal msp organization user with csr org token", ti.normal_msp_org1_user1_id, ti.csr_token,ti.normal_msp_org1_user1_email, ti.normal_msp_org1_id },
			{ "Get the Normal msp organization user with csr read only user token", ti.normal_msp_org1_user1_id, ti.csr_readonly_token,ti.normal_msp_org1_user1_email, ti.normal_msp_org1_id },

			//Normal msp account admin user
			{ "Get the Normal msp account admin  user with Normal msp account admin token", ti.normal_msp_org1_msp_accountadmin1_id, ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_email, ti.normal_msp_org1_id },
			{ "Get the Normal msp account admin  user with Normal msp  token", ti.normal_msp_org1_msp_accountadmin1_id, ti.normal_msp_org1_user1_token,ti.normal_msp_org1_msp_accountadmin1_email, ti.normal_msp_org1_id },
			{ "Get the Normal msp account admin  user with Csr  token", ti.normal_msp_org1_msp_accountadmin1_id, ti.csr_token,ti.normal_msp_org1_msp_accountadmin1_email, ti.normal_msp_org1_id },
			{ "Get the Normal msp account admin  user with Csr  token", ti.normal_msp_org1_msp_accountadmin1_id, ti.csr_readonly_token,ti.normal_msp_org1_msp_accountadmin1_email, ti.normal_msp_org1_id },

			//Custome account of Normal MSP organization 
			{ "Get the Customer account of Normal MSP Organization with the Customer account user", ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_email, ti.normal_msp1_suborg1_id },
			{ "Get the Customer account of Normal MSP Organization with the Normal Msp user", ti.normal_msp1_suborg1_user1_id, ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_user1_email, ti.normal_msp1_suborg1_id },
			{ "Get the Customer account of Normal MSP Organization with the Normal Msp account admin  user", ti.normal_msp1_suborg1_user1_id, ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp1_suborg1_user1_email, ti.normal_msp1_suborg1_id },
			{ "Get the Customer account of Normal MSP Organization with the CSR  user", ti.normal_msp1_suborg1_user1_id, ti.csr_token,ti.normal_msp1_suborg1_user1_email, ti.normal_msp1_suborg1_id },
			{ "Get the Customer account of Normal MSP Organization with the CSR Read Only user", ti.normal_msp1_suborg1_user1_id, ti.csr_readonly_token,ti.normal_msp1_suborg1_user1_email, ti.normal_msp1_suborg1_id },

			//SUB Msp Organization
			{ "Get Sub Msp user with the Sub msp user token", ti.root_msp1_submsp1_user1_id,ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_email,ti.root_msp1_submsp_org1_id },
			{ "Get Sub Msp user with the  CSR  token", ti.root_msp1_submsp1_user1_id, ti.csr_token,ti.root_msp1_submsp1_user1_email, ti.root_msp1_submsp_org1_id },
			{ "Get Sub Msp user with the CSR  Read Only token", ti.root_msp1_submsp1_user1_id, ti.csr_readonly_token,ti.root_msp1_submsp1_user1_email, ti.root_msp1_submsp_org1_id },

			//Sub Msp account admin user 
			{ "Get Sub Msp account admin user with the Sub msp  account adminuser token", ti.root_msp1_submsp1_account_admin_1_id, ti.root_msp1_submsp1_account_admin_token,ti.root_msp1_submsp1_account_admin_1 ,ti.root_msp1_submsp_org1_id },
			{ "Get Sub Msp account admin user with the Sub msp user token", ti.root_msp1_submsp1_account_admin_1_id, ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp1_account_admin_1 ,ti.root_msp1_submsp_org1_id },
			{ "Get Sub Msp account admin user with the csr token", ti.root_msp1_submsp1_account_admin_1_id, ti.csr_token,ti.root_msp1_submsp1_account_admin_1 ,ti.root_msp1_submsp_org1_id },
			{ "Get Sub Msp account admin user with the csr read only  token", ti.root_msp1_submsp1_account_admin_1_id,ti.csr_readonly_token,ti.root_msp1_submsp1_account_admin_1 ,ti.root_msp1_submsp_org1_id },

			//Customer account of Sub Msp Organization
			{ "Get Customer account of Sub Msp Organization with the Sub msp  account adminuser token", ti.msp1_submsp1_suborg1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_suborg1_user1_email ,ti.msp1_submsp1_sub_org1_id },
			{ "Get Customer account of Sub Msp Organization with the MSP user token", ti.msp1_submsp1_suborg1_user1_id, ti.root_msp1_submsp1_user1_token,ti.msp1_submsp1_suborg1_user1_email ,ti.msp1_submsp1_sub_org1_id },
			{ "Get Customer account of Sub Msp Organization with the MSP account admin token", ti.msp1_submsp1_suborg1_user1_id, ti.root_msp1_submsp1_account_admin_token,ti.msp1_submsp1_suborg1_user1_email ,ti.msp1_submsp1_sub_org1_id },
			{ "Get Customer account of Sub Msp Organization with the CSR User token", ti.msp1_submsp1_suborg1_user1_id, ti.csr_token,ti.msp1_submsp1_suborg1_user1_email ,ti.msp1_submsp1_sub_org1_id },
			{ "Get Customer account of Sub Msp Organization with the CSR User token", ti.msp1_submsp1_suborg1_user1_id, ti.csr_readonly_token,ti.msp1_submsp1_suborg1_user1_email ,ti.msp1_submsp1_sub_org1_id },

			//Root Msp User 
			{ "Get Root msp user with the valid token", ti.root_msp_org1_user1_id, ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_email, ti.root_msp_org1_id },
			{ "Get Root msp user with the csr token", ti.root_msp_org1_user1_id, ti.csr_token,ti.root_msp_org1_user1_email, ti.root_msp_org1_id },
			{ "Get Root msp user with the csr token", ti.root_msp_org1_user1_id, ti.csr_readonly_token,ti.root_msp_org1_user1_email, ti.root_msp_org1_id },

			//Root msp account admin user  
			{ "Get Root Msp account admin user with the root msp  account adminuser token", ti.root_msp_org1_msp_accountadmin1_id, ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp_org1_msp_accountadmin1_email ,ti.root_msp_org1_id },
			{ "Get Root Msp account admin user with the root msp  token", ti.root_msp_org1_msp_accountadmin1_id, ti.root_msp_org1_user1_token,ti.root_msp_org1_msp_accountadmin1_email ,ti.root_msp_org1_id },
			{ "Get Root Msp account admin user with the CSR token", ti.root_msp_org1_msp_accountadmin1_id, ti.csr_token,ti.root_msp_org1_msp_accountadmin1_email ,ti.root_msp_org1_id },
			{ "Get Root Msp account admin user with the CSR  Read only token", ti.root_msp_org1_msp_accountadmin1_id, ti.csr_readonly_token,ti.root_msp_org1_msp_accountadmin1_email ,ti.root_msp_org1_id },


			//Customer account of  root msp user ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_token, ti.root_msp1_submsp1_user1_id,
			{ "Get Customer account of Root msp organization with the customer account user token", ti.root_msp1_suborg1_user1_id, ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_email ,ti.root_msp1_suborg1_id },
			{ "Get Customer account of Root msp organization with the root msp user token", ti.root_msp1_suborg1_user1_id, ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_user1_email ,ti.root_msp1_suborg1_id },
			{ "Get Customer account of Root msp organization with the root msp account admin user token", ti.root_msp1_suborg1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp1_suborg1_user1_email ,ti.root_msp1_suborg1_id },
			{ "Get Customer account of Root msp organization with the root csr token", ti.root_msp1_suborg1_user1_id, ti.csr_token,ti.root_msp1_suborg1_user1_email ,ti.root_msp1_suborg1_id },
			{ "Get Customer account of Root msp organization with the root csr read only  token", ti.root_msp1_suborg1_user1_id, ti.csr_readonly_token,ti.root_msp1_suborg1_user1_email ,ti.root_msp1_suborg1_id },

		};

	}

	@Test(dataProvider = "organizationAndUserInfo_1")
	public void GetloggedInById_with_csr_read_only_1(String testcase, 
			String user_id,
			String Validtoken,
			String user_eamil,
			String organization_id) {
		test = ExtentManager.getNewTest(testcase);
		Response response = spogServer.getLoggedUserInfoByID(Validtoken, SpogConstants.SUCCESS_GET_PUT_DELETE, user_id,
				test);
		test.log(LogStatus.INFO, "Validate the logged in user by id");
		spogServer.checkLoggedInUser(response, SpogConstants.SUCCESS_GET_PUT_DELETE, "", user_eamil, user_id,
				organization_id, test);

	}

	@DataProvider(name = "organizationAndUserInfo_2")
	public final Object[][] getOrganizationAndUserInfo_valid2() {
		return new Object[][] {
			
			
			//Direct Organization
			{ "Get the direct organization user with another Direct token", ti.direct_org1_user1_id, ti.direct_org1_user1_token,ti.direct_org2_user1_token,ti.direct_org1_user1_email, ti.direct_org1_id },
			{ "Get the direct organization user with another normal msp token", ti.direct_org1_user1_id, ti.direct_org1_user1_token,ti.normal_msp_org1_user1_token,ti.direct_org1_user1_email, ti.direct_org1_id },
			{ "Get the direct organization user with another normal msp account admin token", ti.direct_org1_user1_id, ti.direct_org1_user1_token,ti.normal_msp_org1_msp_accountadmin1_token,ti.direct_org1_user1_email, ti.direct_org1_id },
			{ "Get the direct organization user with normal msp customer account user token", ti.direct_org1_user1_id, ti.direct_org1_user1_token,ti.normal_msp1_suborg1_user1_token,ti.direct_org1_user1_email, ti.direct_org1_id },
			{ "Get the direct organization user with root msp user token", ti.direct_org1_user1_id, ti.direct_org1_user1_token,ti.root_msp_org1_user1_token,ti.direct_org1_user1_email, ti.direct_org1_id },
			{ "Get the direct organization user with root msp account admin user token", ti.direct_org1_user1_id, ti.direct_org1_user1_token,ti.root_msp_org1_msp_accountadmin1_token,ti.direct_org1_user1_email, ti.direct_org1_id },
			{ "Get the direct organization user with Customer account of root msp organization", ti.direct_org1_user1_id, ti.direct_org1_user1_token,ti.root_msp1_suborg1_user1_token,ti.direct_org1_user1_email, ti.direct_org1_id },
			{ "Get the direct organization user with Sub msp user token", ti.direct_org1_user1_id, ti.direct_org1_user1_token,ti.root_msp1_submsp1_user1_token,ti.direct_org1_user1_email, ti.direct_org1_id },
			{ "Get the direct organization user with sub msp account user token", ti.direct_org1_user1_id, ti.direct_org1_user1_token,ti.root_msp1_submsp1_account_admin_token,ti.direct_org1_user1_email, ti.direct_org1_id },
			{ "Get the direct organization user with customer account of  sub msp account user token", ti.direct_org1_user1_id, ti.direct_org1_user1_token,ti.msp1_submsp1_suborg1_user1_token,ti.direct_org1_user1_email, ti.direct_org1_id },
			
			//Get Normal Msp user Information
			{ "Get the Normal msp organization user with another Direct token", ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_email,ti.direct_org2_user1_token,ti.normal_msp_org1_user1_email, ti.normal_msp_org1_id },
			{ "Get the Normal msp organization user with another normal msp token", ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_email,ti.normal_msp_org2_user1_token,ti.normal_msp_org1_user1_email, ti.normal_msp_org1_id },
			{ "Get the Normal msp organization user with another normal msp account admin token", ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_email,ti.normal_msp_org2_msp_accountadmin1_token,ti.normal_msp_org1_user1_email, ti.normal_msp_org1_id },
			{ "Get the Normal msp organization user with normal msp customer account user token", ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_email,ti.normal_msp2_suborg1_user1_token,ti.normal_msp_org1_user1_email, ti.normal_msp_org1_id },
			{ "Get the Normal msp organization user with root msp user token", ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_email,ti.root_msp_org1_user1_token,ti.normal_msp_org1_user1_email, ti.normal_msp_org1_id },
			{ "Get the Normal msp organization user with root msp account admin user token", ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_email,ti.root_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_user1_email, ti.normal_msp_org1_id },
			{ "Get the Normal msp organization user with Customer account of root msp organization", ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_email,ti.root_msp1_suborg1_user1_token,ti.normal_msp_org1_user1_email, ti.normal_msp_org1_id },
			{ "Get the Normal msp organization user with Sub msp user token", ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_email,ti.root_msp1_submsp1_user1_token,ti.normal_msp_org1_user1_email, ti.normal_msp_org1_id },
			{ "Get the Normal msp organization user with sub msp account user token", ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_email,ti.root_msp1_submsp1_account_admin_token,ti.normal_msp_org1_user1_email, ti.normal_msp_org1_id },
			{ "Get the Normal msp organization user with customer account of  sub msp account user token", ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_email,ti.msp1_submsp1_suborg1_user1_token,ti.normal_msp_org1_user1_email, ti.normal_msp_org1_id },
			
			
			//Get Root MSP User
			
			{ "Get the root msp organization user with another Direct token", ti.root_msp_org1_user1_id, ti.root_msp_org1_user1_token,ti.direct_org2_user1_token,ti.root_msp_org1_user1_email, ti.root_msp_org1_id },
			{ "Get the root msporganization user with another normal msp token", ti.root_msp_org1_user1_id, ti.root_msp_org1_user1_token,ti.normal_msp_org2_user1_token,ti.root_msp_org1_user1_email, ti.root_msp_org1_id },
			{ "Get the root msporganization user with another normal msp account admin token", ti.root_msp_org1_user1_id, ti.root_msp_org1_user1_token,ti.normal_msp_org2_msp_accountadmin1_token,ti.root_msp_org1_user1_email, ti.root_msp_org1_id },
			{ "Get the root msporganization user with normal msp customer account user token", ti.root_msp_org1_user1_id, ti.root_msp_org1_user1_token,ti.normal_msp2_suborg1_user1_token,ti.root_msp_org1_user1_email, ti.root_msp_org1_id },
			{ "Get the root msporganization user with root msp user token", ti.root_msp_org1_user1_id, ti.root_msp_org1_user1_token,ti.root_msp_org2_user1_token,ti.root_msp_org1_user1_email, ti.root_msp_org1_id },
			{ "Get the root msporganization user with root msp account admin user token", ti.root_msp_org1_user1_id, ti.root_msp_org1_user1_token,ti.root_msp_org2_msp_accountadmin1_token,ti.root_msp_org1_user1_email, ti.root_msp_org1_id },
			{ "Get the root msporganization user with Customer account of root msp organization", ti.root_msp_org1_user1_id, ti.root_msp_org1_user1_token,ti.root_msp2_suborg1_user1_token,ti.root_msp_org1_user1_email, ti.root_msp_org1_id },
			{ "Get the root msporganization user with Sub msp user token", ti.root_msp_org1_user1_id, ti.root_msp_org1_user1_token,ti.root_msp1_submsp1_user1_token,ti.root_msp_org1_user1_email, ti.root_msp_org1_id },
			{ "Get the root msporganization user with sub msp account user token", ti.root_msp_org1_user1_id, ti.root_msp_org1_user1_token,ti.root_msp1_submsp1_account_admin_token,ti.root_msp_org1_user1_email, ti.root_msp_org1_id },
			{ "Get the root msporganization user with customer account of  sub msp account user token", ti.root_msp_org1_user1_id, ti.root_msp_org1_user1_token,ti.msp1_submsp1_suborg1_user1_token,ti.root_msp_org1_user1_email, ti.root_msp_org1_id },
		
			
			//Get Sub msp user
			{ "Get the Sub msp organization user with another Direct token", ti.root_msp1_submsp1_user1_id, ti.root_msp1_submsp1_user1_token,ti.direct_org2_user1_token,ti.root_msp1_submsp1_user1_email, ti.root_msp1_submsp_org1_id },
			{ "Get the Sub msporganization user with another normal msp token", ti.root_msp1_submsp1_user1_id, ti.root_msp1_submsp1_user1_token,ti.normal_msp_org2_user1_token,ti.root_msp1_submsp1_user1_email, ti.root_msp1_submsp_org1_id },
			{ "Get the Sub msporganization user with another normal msp account admin token", ti.root_msp1_submsp1_user1_id, ti.root_msp1_submsp1_user1_token,ti.normal_msp_org2_msp_accountadmin1_token,ti.root_msp1_submsp1_user1_email, ti.root_msp1_submsp_org1_id },
			{ "Get the Sub msporganization user with normal msp customer account user token", ti.root_msp1_submsp1_user1_id, ti.root_msp1_submsp1_user1_token,ti.normal_msp2_suborg1_user1_token,ti.root_msp1_submsp1_user1_email, ti.root_msp1_submsp_org1_id },
			{ "Get the Sub msporganization user with root msp user token", ti.root_msp1_submsp1_user1_id, ti.root_msp1_submsp1_user1_token,ti.root_msp_org1_user1_token,ti.root_msp1_submsp1_user1_email, ti.root_msp1_submsp_org1_id },
			{ "Get the Sub msporganization user with root msp account admin user token", ti.root_msp1_submsp1_user1_id, ti.root_msp1_submsp1_user1_token,ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp1_submsp1_user1_email, ti.root_msp1_submsp_org1_id },
			{ "Get the Sub msporganization user with Customer account of root msp organization", ti.root_msp1_submsp1_user1_id, ti.root_msp1_submsp1_user1_token,ti.root_msp1_suborg1_user1_token,ti.root_msp1_submsp1_user1_email, ti.root_msp1_submsp_org1_id },
			{ "Get the Sub msporganization user with Sub msp user token", ti.root_msp1_submsp1_user1_id, ti.root_msp1_submsp1_user1_token,ti.root_msp2_submsp1_user1_token,ti.root_msp1_submsp1_user1_email, ti.root_msp1_submsp_org1_id },
			{ "Get the Sub msporganization user with sub msp account user token", ti.root_msp1_submsp1_user1_id, ti.root_msp1_submsp1_user1_token,ti.root_msp2_submsp1_account_admin_token,ti.root_msp1_submsp1_user1_email, ti.root_msp1_submsp_org1_id },
			{ "Get the Sub msporganization user with customer account of  sub msp account user token", ti.root_msp1_submsp1_user1_id, ti.root_msp1_submsp1_user1_token,ti.msp2_submsp1_suborg1_user1_token,ti.root_msp1_submsp1_user1_email, ti.root_msp1_submsp_org1_id },
			
		};

	}

	@Test(dataProvider = "organizationAndUserInfo_2")
	public void GetloggedInById_with_csr_read_only_403(String testcase,
			String user_id, 
			String Validtoken,
			String another_org_token, 
			String user_eamil,
			String organization_id) {
		test = ExtentManager.getNewTest(testcase);
		Response response = spogServer.getLoggedUserInfoByID(another_org_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
				user_id, test);
		test.log(LogStatus.INFO, "Validate the logged in user by id");
		spogServer.checkLoggedInUser(response, SpogConstants.INSUFFICIENT_PERMISSIONS,
				"Permission required to manage the resource for current user", user_eamil, user_id, organization_id,
				test);

	}

	@DataProvider(name = "organizationAndUserInfo_3")
	public final Object[][] getOrganizationAndUserInfo_valid3() {
		return new Object[][] {
			//Direct Organization
			{ "Get the direct organization user with Direct token", ti.direct_org1_user1_id, ti.direct_org1_user1_token,ti.direct_org1_user1_email, ti.direct_org1_id },
			
			//Normal Msp Organziarion
			{ "Get the Normal msp organization user with msp org token", ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_email, ti.normal_msp_org1_id },
			
			//Normal msp account admin user
			{ "Get the Normal msp account admin  user with Normal msp account admin token", ti.normal_msp_org1_msp_accountadmin1_id, ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_user1_email, ti.normal_msp_org1_id },
			
			//Custome account of Normal MSP organization 
			{ "Get the Customer account of Normal MSP Organization with the Customer account user", ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_email, ti.normal_msp1_suborg1_id },
			
			//SUB Msp Organization
			{ "Get Sub Msp user with the Sub msp user token", ti.root_msp1_submsp1_user1_id,ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_email,ti.root_msp1_submsp_org1_id },
			
			//Sub Msp account admin user 
			{ "Get Sub Msp account admin user with the Sub msp  account adminuser token", ti.root_msp1_submsp1_user1_id, ti.root_msp1_submsp1_account_admin_token,ti.root_msp1_submsp1_account_admin_1 ,ti.root_msp1_submsp_org1_id },
			
			//Customer account of Sub Msp Organization
			{ "Get Sub Msp account admin user with the Sub msp  account adminuser token", ti.msp1_submsp1_suborg1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_suborg1_user1_email ,ti.msp1_submsp1_sub_org1_id },
			
			//Root Msp User 
			{ "Get Root msp user with the valid token", ti.root_msp_org1_user1_id, ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_email, ti.root_msp_org1_id },
			
			//Root msp account admin user  
			{ "Get Root Msp account admin user with the root msp  account adminuser token", ti.root_msp_org1_msp_accountadmin1_id, ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp_org1_msp_accountadmin1_email ,ti.root_msp_org1_id },
			

			//Customer account of  root msp user ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_token, ti.root_msp1_submsp1_user1_id,
			{ "Get Customer account of Root msp organization with the customer account user token", ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_token,ti.root_msp1_submsp1_user1_email ,ti.root_msp1_suborg1_id },
			
		};

	}

	@Test(dataProvider = "organizationAndUserInfo_3")
	public void GetloggedInById_with_csr_read_only_invalid_missedtoken(String testcase,
			String user_id,
			String validtoken, 
			String user_eamil,
			String organization_id) {
		test = ExtentManager.getNewTest(testcase);

		// Invalid token
		Response response = spogServer.getLoggedUserInfoByID(validtoken + 123, SpogConstants.NOT_LOGGED_IN, user_id,
				test);
		test.log(LogStatus.INFO, "Validate the logged in user by id");
		spogServer.checkLoggedInUser(response, SpogConstants.NOT_LOGGED_IN, "", user_eamil, user_id, organization_id,
				test);

		// Missed token
		response = spogServer.getLoggedUserInfoByID("", SpogConstants.NOT_LOGGED_IN, user_id, test);
		test.log(LogStatus.INFO, "Validate the logged in user by id");
		spogServer.checkLoggedInUser(response, SpogConstants.NOT_LOGGED_IN, "", user_eamil, user_id, organization_id,
				test);

	}

	/**************************** Preference language cases - Sprint 34 ********************************************/
	@DataProvider(name="getUserPreferenceLanguageCases")
	public Object[][] getUserPreferenceLanguageCases(){
		return new Object[][] {
			//200
			{"GET user in direct organization having preferred language English ", 
				ti.direct_org1_user1_email, ti.common_password, SpogConstants.DIRECT_ADMIN, ti.direct_org1_id, "en", SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"GET user in direct organization having preferred language Japanese", 
					ti.direct_org1_user1_email, ti.common_password, SpogConstants.DIRECT_ADMIN, ti.direct_org1_id, "ja", SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"GET msp user in MSP organization having preferred language English", 
						ti.normal_msp_org1_user1_email, ti.common_password, SpogConstants.MSP_ADMIN, ti.normal_msp_org1_id, "en", SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"GET msp user in MSP organization having preferred language Japanese", 
							ti.normal_msp_org1_user1_email, ti.common_password, SpogConstants.MSP_ADMIN, ti.normal_msp_org1_id, "ja", SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"GET msp account admin user in MSP organization having preferred language English", 
								ti.normal_msp_org1_user1_email, ti.common_password, SpogConstants.MSP_ACCOUNT_ADMIN, ti.normal_msp_org1_id, "en", SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"GET msp account admin user in MSP organization having preferred language Japanese", 
									ti.normal_msp_org1_user1_email, ti.common_password, SpogConstants.MSP_ACCOUNT_ADMIN, ti.normal_msp_org1_id, "ja", SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"GET customer admin user in sub organization using msp token having preferred language English", 
										ti.normal_msp_org1_user1_email, ti.common_password, SpogConstants.DIRECT_ADMIN, ti.normal_msp1_suborg1_id, "en", SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"GET customer user in sub organization using msp token having preferred language Japanese", 
											ti.normal_msp_org1_user1_email, ti.common_password, SpogConstants.DIRECT_ADMIN, ti.normal_msp1_suborg1_id, "ja", SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"GET direct admin user in sub organization using sub org token having preferred language English", 
												ti.normal_msp1_suborg1_user1_email , ti.common_password, SpogConstants.DIRECT_ADMIN, ti.normal_msp1_suborg1_id, "en", SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"GET direct admin user in sub organization using sub org token having preferred language Japanese", 
													ti.normal_msp1_suborg1_user1_email, ti.common_password, SpogConstants.DIRECT_ADMIN, ti.normal_msp1_suborg1_id, "ja", SpogConstants.SUCCESS_GET_PUT_DELETE, null},

			//preferred language value null
			{"GET user in direct organization having preferred language null", 
														ti.direct_org1_user1_email, ti.common_password, SpogConstants.DIRECT_ADMIN, ti.direct_org1_id, null, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"GET msp user in MSP organization having preferred language null", 
															ti.normal_msp_org1_user1_email, ti.common_password, SpogConstants.MSP_ADMIN, ti.normal_msp_org1_id, null, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"GET msp account admin user in MSP organization having preferred language null", 
																ti.normal_msp_org1_user1_email, ti.common_password, SpogConstants.MSP_ACCOUNT_ADMIN, ti.normal_msp_org1_id, null, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"GET customer admin user in sub organization using msp token having preferred language null", 
																	ti.normal_msp_org1_user1_email, ti.common_password, SpogConstants.DIRECT_ADMIN, ti.normal_msp1_suborg1_id, null, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"GET direct admin user in sub organization using sub org token having preferred language null", 
																		ti.normal_msp1_suborg1_user1_email , ti.common_password, SpogConstants.DIRECT_ADMIN, ti.normal_msp1_suborg1_id, null, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
		};
	}

	@Test(dataProvider="getUserPreferenceLanguageCases")
	public void getUserTestPreferredLang(String caseType,
			String loginUserName,
			String loginPassword,
			String role_id,
			String organization_id,
			String preference_language,
			int expectedStatusCode,
			SpogMessageCode expectedErrorMessage ) {

		test = rep.startTest(caseType);
		test.assignAuthor("Rakesh.Chalamala");
		spogServer.userLogin(loginUserName, loginPassword,test);

		String first_name = spogServer.ReturnRandom("first");
		String last_name = spogServer.ReturnRandom("last");
		String email = spogServer.ReturnRandom("email")+"@spogqa.com";
		String phone = RandomStringUtils.randomNumeric(10);
		String password = ti.common_password;
		String actions = "delete,resetpassword";

		if (role_id.equalsIgnoreCase(SpogConstants.MSP_ACCOUNT_ADMIN)) {
			actions += ",assignaccount";
		}

		String user_id = spogServer.createUserWithCheck(first_name, last_name, email, phone, role_id, organization_id, preference_language, password, SpogConstants.SUCCESS_POST, null, test);

		Response response = spogServer.getLoggedUserInfoByID(spogServer.getJWTToken(),SpogConstants.SUCCESS_GET_PUT_DELETE,user_id, test);
		if (preference_language == null) {
			preference_language = "en";
		}
		response.then().body("data.preference_language", equalTo(preference_language));

		spogServer.checkLoggedInUserInformation(response, expectedStatusCode, expectedErrorMessage, email, user_id, organization_id, false, "verified", actions, test);

	}

	/************************************end********************************************/

	@AfterMethod
	public void getResult(ITestResult result) {
		if (result.getStatus() == ITestResult.FAILURE) {
			count1.setfailedcount();

			test.log(LogStatus.FAIL, "Test Case Failed is " + result.getName() + " with parameters as "
					+ Arrays.asList(result.getParameters()));
			test.log(LogStatus.FAIL, result.getThrowable().getMessage());
		} else if (result.getStatus() == ITestResult.SKIP) {
			count1.setskippedcount();
			test.log(LogStatus.SKIP, "Test Case Skipped is " + result.getName());

		} else if (result.getStatus() == ITestResult.SUCCESS) {
			count1.setpassedcount();

		}
		rep.endTest(test);

	}

}
