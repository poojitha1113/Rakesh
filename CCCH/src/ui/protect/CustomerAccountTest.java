package ui.protect;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

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

import Constants.SpogConstants;
import Constants.UIConstants;
import InvokerServer.Org4SPOGServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import ui.base.common.TableHeaders;
import ui.spog.server.CustomerAccountsPageHelper;
import ui.spog.server.SPOGUIServer;

/**
 * Tests the MSP > Protect > CustomerAccounts page
 * Adds customer and verifies details
 * Sets usage threshold and verifies 
 * Assigns msp account admin 
 * Views as end user admin
 * Delete test
 * 
 * @author Rakesh.Chalamala
 *
 */
public class CustomerAccountTest extends base.prepare.Is4Org{

	private SPOGServer spogServer;
	private UserSpogServer userSpogServer;
	private SPOGUIServer spogUIServer;
	private Org4SPOGServer org4SpogServer;
	private CustomerAccountsPageHelper customerAccountsPageHelper;
	private String csrAdminUserName;
	private String csrAdminPassword;
	private String csr_token;
	private ExtentTest test;

	/*private ExtentReports rep;
  	private SQLServerDb bqdb1;
  	public int Nooftest;
  	private long creationTime;
  	private String BQName=null;
  	private String runningMachine;
  	private testcasescount count1;
  	private String buildVersion;*/
	
	private String postfix_email = "@arcserve.com";
	private String common_password = "Welcome*02";

	private String prefix_msp = "spogqa_rakesh_msp";
	private String msp_org_name = prefix_msp + "_org";
	private String msp_user_name = prefix_msp + "_admin";
	private String msp_user_name_email = prefix_msp + "_admin" + postfix_email;
	private String msp_user_first_name = msp_user_name + "_first_name";
	private String msp_user_last_name = msp_user_name + "_last_name";
	private String msp_org_id=null;
	private String final_msp_user_name_email=null;
	private String msp_user_id;
	private String msp_user_validToken;
	
	private String prefix_msp_b = "spog_rakesh_msp_b";
	private String msp_org_name_b = prefix_msp_b + "_org";
	private String msp_user_name_email_b = msp_org_name_b + postfix_email;
	private String msp_user_first_name_b = msp_org_name_b + "_first_name";
	private String msp_user_last_name_b = msp_org_name_b + "_last_name";
	private String msp_org_id_b=null;
	private String final_msp_user_name_email_b=null;
	private String msp_user_id_b;
	private String msp_user_b_validToken;

	private String initial_sub_org_name_a = "SPOG_QA_RAKESH_BQ_sub_a";
	private String initial_sub_email_a = "spog_qa_sub_RAKESH_a@arcserve.com";
	private String initial_sub_first_name_a = "spog_qa_sub_RAKESH_a";
	private String initial_sub_last_name_a = "spog_qa_sub_CHALAMALA_a";
	private String sub_orga_user_validToken;
	private String suborga_id;
	private String suborga_user_id;;
	
	private String initial_sub_org_name_b = "SPOG_QA_RAKESH_BQ_sub_b";
	private String initial_sub_email_b = "spog_qa_sub_RAKESH_b@arcserve.com";
	private String initial_sub_first_name_b = "spog_qa_sub_RAKESH_b";
	private String initial_sub_last_name_b = "spog_qa_sub_CHALAMALA_b";
	private String sub_orgb_user_validToken;
	private String suborgb_id;
	private String suborgb_user_id;;
	
	private String initial_sub_org_name_1 = "SPOG_QA_RAKESH_BQ_sub_1";
	private String initial_sub_email_1 = "spog_qa_sub_RAKESH_1@arcserve.com";
	private String initial_sub_first_name_1 = "spog_qa_sub_RAKESH_1";
	private String initial_sub_last_name_1 = "spog_qa_sub_CHALAMALA_";
	private String sub_org1_user_validToken;
	private String suborg1_id;
	private String suborg1_user_id;;
	
	private String prefix_msp_account_admin = "spog_rakesh_msp_account";
	private String msp_account_admin_email = prefix_msp_account_admin+postfix_email;
	private String msp_account_admin_first_name = prefix_msp_account_admin+"_first_name";
	private String msp_account_admin_last_name = prefix_msp_account_admin+"_last_name";
	private String msp_account_admin_id;
	private String msp_account_admin_validToken;
	
	private String prefix_msp_account_admin_b = "spog_rakesh_msp_account_b";
	private String msp_account_admin_email_b = prefix_msp_account_admin_b+postfix_email;
	private String msp_account_admin_first_name_b = prefix_msp_account_admin_b+"_first_name";
	private String msp_account_admin_last_name_b = prefix_msp_account_admin_b+"_last_name";
	private String msp_account_admin_id_b;
	private String msp_account_admin_b_validToken;
	
	private String  org_model_prefix=this.getClass().getSimpleName();


	@BeforeClass
	@Parameters({ "baseURI", "port",   "logFolder", "csrAdminUserName", "csrAdminPassword", "buildVersion", "uiBaseURI", "browserType", "maxWaitTimeSec"})
	public void beforeClass(String baseURI, String port, String logFolder, String adminUserName, String adminPassword,  String buildVersion,
							String uiBaseURI, String browserType, int maxWaitTimeSec) throws UnknownHostException {

		org4SpogServer = new Org4SPOGServer(baseURI, port);
		spogServer = new SPOGServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		this.csrAdminUserName = adminUserName;
		this.csrAdminPassword = adminPassword;
		rep = ExtentManager.getInstance("Test",logFolder);
		test = rep.startTest("beforeClass");

		//Create the organizations and users
		prepareEnv();
		
		customerAccountsPageHelper = new CustomerAccountsPageHelper(browserType, maxWaitTimeSec);
		customerAccountsPageHelper.openUrl(uiBaseURI);
		customerAccountsPageHelper.login_Spog(final_msp_user_name_email, common_password);
		
		this.BQName = "UI_"+this.getClass().getSimpleName();
		String author = "Kanamarlapudi, Chandra Kanth";
		this.runningMachine =  InetAddress.getLocalHost().getHostName();
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date=new java.util.Date();
		this.buildVersion=buildVersion+"_"+dateFormater.format(date);
		Nooftest=0;
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		if(count1.isstarttimehit()==0) {

			creationTime=System.currentTimeMillis();
			count1.setcreationtime(creationTime);
			//creationTime = System.currentTimeMillis();
			try {
				bqdb1.updateTable(this.BQName, runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0", String.valueOf(Nooftest), count1.getcreationtime(), "InProgress",author+" and Rest server is "+baseURI.split("//")[1]);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	@DataProvider(name = "customerInfo9")
	public final Object[][] customerInfo9() {
		return new Object[][] { 	
			{final_msp_user_name_email,"cust1", "1", "TB", "20", "GB", msp_account_admin_first_name+" "+msp_account_admin_last_name, msp_account_admin_email},			
		};
	}
	@Test(dataProvider = "customerInfo9", enabled=false)
	public void customerPageTest(String userEmail,
									String custName,
									String cdUsage,
									String cdCapacity,
									String chUsage,
									String chCapacity,
									String adminName,
									String adminEmail
									) {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		
		customerAccountsPageHelper.login_Spog(userEmail, common_password);
		spogServer.userLogin(userEmail, common_password);
		
		SimpleDateFormat dateFormater = new SimpleDateFormat("MM/dd/yyyy");
		String date = dateFormater.format(new Date());
		
		ArrayList<HashMap<String, Object>> allInfo = new ArrayList<>();
		HashMap<String, Object> expectedInfo = new HashMap<>();
		expectedInfo.put(TableHeaders.customer_name, custName);
		expectedInfo.put(TableHeaders.total_sources, "0");
		expectedInfo.put(TableHeaders.cloud_direct_usage, cdUsage);
		expectedInfo.put(TableHeaders.cloud_hybrid_usage, chUsage);
		expectedInfo.put(TableHeaders.added_by, spogServer.GetLoggedinUser_UserName());
		expectedInfo.put(TableHeaders.added_on, date);
	
		expectedInfo.put(TableHeaders.name, adminName);
		expectedInfo.put(TableHeaders.phone, "-");
		expectedInfo.put(TableHeaders.email, adminEmail);
		expectedInfo.put(TableHeaders.assigned_on, "");
		expectedInfo.put(TableHeaders.role, "MSP Account Admin");
		
		allInfo.add(expectedInfo);
		
		customerAccountsPageHelper.addCustomersWithCheck(custName,test);
		customerAccountsPageHelper.setUsageThresholdWithCheck(custName, cdUsage, cdCapacity, chUsage, chCapacity, false, test);
		customerAccountsPageHelper.assignMspAccountAdminWithCheck(custName, adminEmail,allInfo, test);
		customerAccountsPageHelper.viewAsEndUserAdminWithCheck(custName,test);
		customerAccountsPageHelper.verifyCustomerDetails(custName, allInfo, test);
		customerAccountsPageHelper.deleteCustomerAccount(custName, test);
	}
	

	@DataProvider(name = "customerInfo")
	public final Object[][] customerInfo() {
		return new Object[][] { 	
			{"Success - Add customer valid",final_msp_user_name_email,msp_account_admin_email,"cust1", "Valid"},
			{"Error - Add customer with name lenght 0",final_msp_user_name_email,null, "", "Required"},
			{"Error - Add customer with name lenght 1",final_msp_user_name_email,null, "a", "Must be 3 characters or more"},
			{"Error - Add customer with name lenght 2",final_msp_user_name_email,null, "ab", "Must be 3 characters or more"},
//			{"Error - Add customer with name lenght 2 with a space",final_msp_user_name_email,null, "ab ", "Must be 3 characters or more"},
			{"Error - Add customer with name lenght 1 with a space",final_msp_user_name_email,null, "a ", "Must be 3 characters or more"},
			{"Error - Add customer with name lenght more than 128",final_msp_user_name_email,null, "cust"+RandomStringUtils.randomAlphanumeric(128), "Must be 128 characters or less"},
		};
	}
	@Test(dataProvider = "customerInfo", enabled=true)
	public void addCustomerTest(String caseType, String userEmail, String accountAdminEmail, String custName, String expectedErrorMsg) {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		
		if (caseType.contains("Success")) {
			
			ArrayList<HashMap<String, Object>> allInfo = new ArrayList<>();
			HashMap<String, Object> expectedInfo = composeExpectedInfo(custName, userEmail, accountAdminEmail);
			allInfo.add(expectedInfo);
			
			customerAccountsPageHelper.addCustomersWithCheck(custName, test);
			customerAccountsPageHelper.assignMspAccountAdminWithCheck(custName, accountAdminEmail, allInfo, test);
			customerAccountsPageHelper.verifyCustomerDetails(custName, allInfo, test);
			
		}else if (caseType.contains("Error")) {
			customerAccountsPageHelper.addCustomerFail(custName, expectedErrorMsg, test);
		}
	}
	
	HashMap<String, Object> composeExpectedInfo(String custName, String userEmail, String accountAdminEmail){
		
		HashMap<String, Object> expectedInfo = new HashMap<>();
		
		expectedInfo.put(TableHeaders.customer_name, custName);
		expectedInfo.put(TableHeaders.total_sources, "0");
		expectedInfo.put(TableHeaders.cloud_direct_usage, "-");
		expectedInfo.put(TableHeaders.cloud_hybrid_usage, "-");
		
		spogServer.userLogin(userEmail, common_password);
		expectedInfo.put(TableHeaders.added_by, spogServer.GetLoggedinUser_UserName());
		
		SimpleDateFormat dateFormater = new SimpleDateFormat("MM/dd/yyyy");
		String date = dateFormater.format(new Date());
		expectedInfo.put(TableHeaders.added_on, date);
		
		spogServer.userLogin(accountAdminEmail, common_password);
		expectedInfo.put(TableHeaders.name, spogServer.GetLoggedinUser_UserName());
		
		expectedInfo.put(TableHeaders.phone, "-");
		expectedInfo.put(TableHeaders.email, accountAdminEmail);
		expectedInfo.put(TableHeaders.assigned_on, "");
		expectedInfo.put(TableHeaders.role, UIConstants.MSP_ACCOUNTS_ADMINISTRATOR_ROLE);
		
		return expectedInfo;
	}
	
	@Test(enabled=true/*,dependsOnMethods= {"addCustomerTest","assignMspAccountAdminsTest","setUsageThresholdTest","viewAsEndUserTest"}*/)
	public void zdeleteCustomerTest() {
		String mspToken = msp_user_b_validToken;
		String mspEmail = final_msp_user_name_email_b;
		String mspOrgId = msp_org_id_b;		
				
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		
		ArrayList<String> customers = createCustomersWithAPI("cust", mspToken, mspOrgId, 5);
		
		customerAccountsPageHelper.logout();
		customerAccountsPageHelper.login_Spog(mspEmail, common_password);
		
		for (int i = 0; i < customers.size(); i++) {
			customerAccountsPageHelper.deleteCustomerAccount(customers.get(i), test);	
		}		
		
		customerAccountsPageHelper.logout();
	}
	
	@Test(enabled=true)
	public void viewAsEndUserTest() {
		String mspToken = msp_user_validToken;
		String mspEmail = msp_user_name_email;
		String mspOrgId = msp_org_id;		
		ArrayList<String> customers = new ArrayList<>();
		
		customers.add(initial_sub_org_name_a);
		customers.add(initial_sub_org_name_b);
				
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		
		spogServer.getAllaccountsForSpecifiedMsp(mspToken, mspOrgId, null, test);
		for (int j = 0; j < customers.size(); j++) {
			customerAccountsPageHelper.viewAsEndUserAdminWithCheck(customers.get(j), test);	
		}
	}
	
	
	@DataProvider(name = "usageThresholdInfo")
	public final Object[][] usageThresholdInfo(){
		return new Object[][] {
			{"Set CD usage threshold valid case",final_msp_user_name_email, initial_sub_org_name_a, "10", "GB", null, null, false},
			{"Set CH usage threshold valid case",final_msp_user_name_email, initial_sub_org_name_a, null, null, "10", "GB", false},
			{"Set CD and CH usage threshold valid case",final_msp_user_name_email, initial_sub_org_name_a, "1", "TB", "2", "TB", false},
			
		};
	}
	
	@Test(dataProvider = "usageThresholdInfo", enabled=true)
	public void setUsageThresholdTest(String caseType, String userEmail, String customerName, String cdUsage, String cdCapacity, String chUsage, String chCapacity, boolean cancel) {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		test.log(LogStatus.INFO, caseType);
		
		customerAccountsPageHelper.setUsageThresholdWithCheck(customerName, cdUsage, cdCapacity, chUsage, chCapacity, cancel, test);
	}

	@DataProvider(name="mspAccountAdminsInfo")
	public Object[][] mspAccountAdminsInfo(){
		return new Object[][] {
			{"assign 1 Msp Account admin valid",final_msp_user_name_email, initial_sub_org_name_a, msp_account_admin_email, msp_account_admin_first_name+" "+msp_account_admin_last_name},
			{"assign 1 Msp Account admin valid",final_msp_user_name_email, initial_sub_org_name_b, msp_account_admin_email+","+msp_account_admin_email_b,msp_account_admin_first_name+" "+msp_account_admin_last_name+","+msp_account_admin_first_name_b+" "+msp_account_admin_last_name_b}
		};
	}
	
	@Test(dataProvider="mspAccountAdminsInfo", enabled=true)
	public void assignMspAccountAdminsTest(String caseType, String userEmail, String customerName, String adminEmail, String adminName) {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		test.log(LogStatus.INFO, caseType);
		
		ArrayList<HashMap<String, Object>> expectedInfo = new ArrayList<>();
		HashMap<String, Object> adminInfo = null;
		
		String[] adminNames = adminName.split(",");
		String[] adminEmails = adminEmail.split(",");
		for (int i = 0; i < adminNames.length && i < adminEmails.length; i++) {
			adminInfo = new HashMap<>();
			
			adminInfo.put(TableHeaders.role, "MSP Account Admin");
			adminInfo.put(TableHeaders.phone, "-");
			adminInfo.put(TableHeaders.assigned_on, getTodayDate());
			
			adminInfo.put(TableHeaders.name, adminNames[i]);
			adminInfo.put(TableHeaders.email, adminEmails[i]);
			expectedInfo.add(adminInfo);
		}
		
		customerAccountsPageHelper.assignMspAccountAdminWithCheck(customerName, adminEmail,expectedInfo, test);
	}
		
	@DataProvider(name = "searchCustomerTestInfo")
	public final Object[][] searchCustomerTestInfo() {
		return new Object[][] { 	
			{"Create customers with name prefix cust1","sub",msp_user_b_validToken,final_msp_user_name_email_b, msp_org_id_b, 4},
			{"Create customers with name prefix cust2","sub_b",msp_user_b_validToken,final_msp_user_name_email_b, msp_org_id_b, 2},
			
		};
	}
	@Test(dataProvider = "searchCustomerTestInfo", enabled=true)
	public void searchByCustomerNameTest(String caseType, String accountPrefix, String token, String userMail, String mspOrgId, int noOfAccountsToCreate) {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		
		ArrayList<String> expectedCustomers = new ArrayList<>();
		expectedCustomers.add(initial_sub_org_name_a);
		expectedCustomers.add(initial_sub_org_name_b);
		
		customerAccountsPageHelper.searchCustomerNameWithCheck(accountPrefix, expectedCustomers);
	}
	
	public ArrayList<String> createCustomersWithAPI(String orgPrefix, String token, String mspOrgId, int noOfAccountsToCreate) {
	
		ArrayList<String> customerNames = new ArrayList<>();
		String name=null;
		
		spogServer.setToken(token);
		for (int i = 0; i < noOfAccountsToCreate; i++) {
			name = spogServer.ReturnRandom(orgPrefix);
			spogServer.createAccountWithCheck(mspOrgId, name,mspOrgId);	
			customerNames.add(name);
		}
		
		return customerNames;
	}
	
	
	
	/*@AfterMethod
	public void afterMethod(){
		customerAccountsPageHelper.logout();
		customerAccountsPageHelper.destroy();
	}*/
	private void prepareEnv(){

		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
		csr_token = spogServer.getJWTToken();
		
		//************************create msp org,user *************************************
		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		test.log(LogStatus.INFO,"create a msp org");
		msp_org_id = spogServer.CreateOrganizationByEnrollWithCheck(prefix + msp_org_name+org_model_prefix , SpogConstants.MSP_ORG, null, null, null, null, test);
		final_msp_user_name_email = prefix + msp_user_name_email;

		test.log(LogStatus.INFO,"create a admin under msp org");
		msp_user_id = spogServer.createUserAndCheck(final_msp_user_name_email, common_password, prefix + msp_user_first_name, prefix + msp_user_last_name, SpogConstants.MSP_ADMIN, msp_org_id, test);
		spogServer.userLogin(final_msp_user_name_email, common_password);
		msp_user_validToken = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Create a suborg A under msp org");
		initial_sub_org_name_a = prefix+initial_sub_org_name_a;
		suborga_id = spogServer.createAccountWithCheck(msp_org_id, initial_sub_org_name_a, msp_org_id);
		
		test.log(LogStatus.INFO, "Create a suborg B under msp org");
		initial_sub_org_name_b = prefix+initial_sub_org_name_b;
		suborgb_id = spogServer.createAccountWithCheck(msp_org_id, initial_sub_org_name_b, msp_org_id);
		
		//Create msp_account_admin
		test.log(LogStatus.INFO, "Create msp account admins under msp org");
		msp_account_admin_email = prefix+msp_account_admin_email;
		spogServer.setToken(msp_user_validToken);
		msp_account_admin_id = spogServer.createUserAndCheck(msp_account_admin_email, common_password, msp_account_admin_first_name, msp_account_admin_last_name, "msp_account_admin", msp_org_id, test);
		spogServer.userLogin(msp_account_admin_email, common_password);
		this.msp_account_admin_validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The token is :"+ msp_account_admin_validToken );
//		userSpogServer.assignMspAccountAdmins(msp_org_id, suborga_id, new String[] {msp_account_admin_id}, msp_user_validToken);
		
		msp_account_admin_email_b = prefix+msp_account_admin_email_b;
		spogServer.setToken(msp_user_validToken);
		msp_account_admin_id_b = spogServer.createUserAndCheck(msp_account_admin_email_b, common_password, msp_account_admin_first_name_b, msp_account_admin_last_name_b, "msp_account_admin", msp_org_id, test);
		spogServer.userLogin(msp_account_admin_email_b, common_password);
		this.msp_account_admin_b_validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The token is :"+ msp_account_admin_b_validToken );
//		userSpogServer.assignMspAccountAdmins(msp_org_id, suborga_id, new String[] {msp_account_admin_id_b}, msp_user_validToken);
		
		
		//************************create msp org B,user *************************************
		test.log(LogStatus.INFO,"create another msp org");
		spogServer.setToken(csr_token);
		msp_org_id_b = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name_b+org_model_prefix , SpogConstants.MSP_ORG, null, null, null, null, test);
		final_msp_user_name_email_b = prefix + msp_user_name_email_b;

		test.log(LogStatus.INFO,"create a admin under msp org b");
		msp_user_id_b = spogServer.createUserAndCheck(final_msp_user_name_email_b, common_password, prefix + msp_user_first_name_b, prefix + msp_user_last_name_b, SpogConstants.MSP_ADMIN, msp_org_id_b, test);
		spogServer.userLogin(final_msp_user_name_email_b, common_password);
		msp_user_b_validToken = spogServer.getJWTToken();
		
		test.log(LogStatus.INFO, "Create a suborg A under msp org");
		initial_sub_org_name_1 = prefix+initial_sub_org_name_1;
		suborg1_id = spogServer.createAccountWithCheck(msp_org_id_b, initial_sub_org_name_1,
				msp_org_id_b);
		
		test.log(LogStatus.INFO, "Enable CH trial for MSP org with id: "+msp_org_id);
		spogServer.postCloudhybridInFreeTrial(csr_token, msp_org_id, SpogConstants.MSP_ORG, false, false);
		
		test.log(LogStatus.INFO, "Enable CH trial for MSP org with id: "+msp_org_id_b);
		spogServer.postCloudhybridInFreeTrial(csr_token, msp_org_id_b, SpogConstants.MSP_ORG, false, false);
	}
	
	@AfterMethod
	public void getResult(ITestResult result){
//		customerAccountsPageHelper.logout();
		
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
	
	@AfterClass
	public void afterClass() {
		customerAccountsPageHelper.destroy();
		recycleVolumeInCDandDestroyOrg(org_model_prefix);
	}

	/*****************************Generic**********************************/
	private String getTodayDate() {
		
		SimpleDateFormat dateFormater = new SimpleDateFormat("MM/dd/yyyy");
		String date = dateFormater.format(new Date());
		
		return date;
	}
	
	
	
}
