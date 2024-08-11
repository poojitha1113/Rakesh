package api.organizations;

import invoker.SiteTestHelper;
import io.restassured.response.Response;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;

import Constants.DestinationStatus;
import Constants.DestinationType;
import Constants.ErrorCode;
import Constants.SpogConstants;
import InvokerServer.GatewayServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import InvokerServer.GatewayServer.siteType;
import api.preparedata.InitialTestData;
import api.preparedata.InitialTestDataImpl;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class GetAmountAndPolicyTypeForSpecifiedOrganizationTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private SPOGDestinationServer spogDestinationServer;
	private GatewayServer gatewayServer;
	private Policy4SPOGServer policy4SPOGServer;
	private UserSpogServer userSpogServer;

	private String csrAdminUserName;
	private String csrAdminPassword;
	private ExtentTest test;

	private String csr_token = null;
	private String postfix_email = "@arcserve.com";
	private String common_password = "Welcome*02";
	
	private String csr_readonly_email = "csr_readonly_zhaoguo@arcserve.com";
	  private String csr_readonly_password = "Caworld_2017";
	  
		private InitialTestDataImpl initialTestDataImpl;
		private InitialTestData itd;
		private String password = "Pa$$w0rd";
		
	private String prefix_direct = "spogqa_leiyu_direct";
	private String direct_org_name = prefix_direct + "_org";
	private String direct_org_id = null;
	private String direct_user_name = prefix_direct + "_admin";
	private String direct_user_name_email = direct_user_name + postfix_email;
	private String direct_user_first_name = direct_user_name + "_first_name";
	private String direct_user_last_name = direct_user_name + "_last_name";
	private String direct_user_id = null;
	private String final_direct_user_name_email = null;
	private String direct_site_id;
	private String direct_user_cloud_account_id;

	private String prefix_msp = "spogqa_leiyu_msp";
	private String msp_org_name = prefix_msp + "_org";
	private String msp_org_email = msp_org_name + postfix_email;
	private String msp_org_first_name = msp_org_name + "_first_name";
	private String msp_org_last_name = msp_org_name + "_last_name";
	private String msp_user_name = prefix_msp + "_admin";
	private String msp_user_name_email = prefix_msp + "_admin" + postfix_email;
	private String msp_user_first_name = msp_user_name + "_first_name";
	private String msp_user_last_name = msp_user_name + "_last_name";
	private String msp_user_id = null;
	private String msp_org_id = null;
	private String final_msp_user_name_email = null;
	private String msp_site_id;
	private String msp_user_cloud_account_id;

	private String msp_sub_org_name = prefix_msp + "_sub_org";
	private String msp_sub_org_id = null;

	private String prefix_msp_account = "spogqa_leiyu_msp_account_admin";
	private String msp_account_admin_name = prefix_msp_account + "_admin";
	private String msp_account_admin_email = msp_account_admin_name + postfix_email;
	private String msp_account_admin_first_name = msp_account_admin_name + "_first_name";
	private String msp_account_admin_last_name = msp_account_admin_name + "_last_name";
	private String msp_account_admin_id;
	private String final_msp_account_admin_email;

	private String account_id;
	private String account_user_email;
	private String account_user_id;
	private String account_site_id;
	private String another_direct_site_id;
	private String another_msp_site_id;
	private String another_account_site_id;
	private String account_user_cloud_account_id;

	private String prefix_csr = "spogqa_leiyu_csr";
	private String csr_user_name = prefix_csr + "_admin";
	private String csr_user_name_email = prefix_csr + "_admin" + postfix_email;
	private String csr_user_first_name = csr_user_name + "_first_name";
	private String csr_user_last_name = csr_user_name + "_last_name";

	private String prefix_destination = "dest_leiyu";

	private String destination_id_ret = "";

	private String org_model_prefix = this.getClass().getSimpleName();

	/**
	 * create destination
	 */
	/*
	 * @Test(dataProvider = "DestinationParameter", enabled=true, priority=1) public
	 * void createDestination(String username, String password,String
	 * destination_id,String organization_id,String site_id, String
	 * destination_type, String destination_status,String dedupe_savings,
	 * //cloud_direct_volume parameters String cloud_account_id, String
	 * volume_type,String hostname, String retention_id, String retention_name,
	 * String age_hours_max,String age_four_hours_max,String age_days_max,String
	 * age_weeks_max,String age_months_max, String age_years_max,
	 * //cloud_dedupe_volume parameters String concurrent_active_node,String
	 * is_deduplicated, String block_size,String is_compressed){
	 * 
	 * test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.
	 * currentThread() .getStackTrace()[1].getMethodName());
	 * test.assignAuthor("leiyu.wang"); String destination_name=
	 * prefix_destination+RandomStringUtils.randomAlphanumeric(3);
	 * 
	 * spogServer.userLogin(username, password);
	 * 
	 * spogDestinationServer.setToken(spogServer.getJWTToken()); destination_id_ret
	 * = spogDestinationServer.createDestinationWithCheck(destination_id,
	 * organization_id, site_id,
	 * "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c",destination_type,
	 * destination_status,dedupe_savings, cloud_account_id, volume_type, hostname,
	 * retention_id,retention_name, age_hours_max, age_four_hours_max, age_days_max,
	 * age_weeks_max, age_months_max, age_years_max, concurrent_active_node,
	 * is_deduplicated, block_size, is_compressed, destination_name, test);
	 * 
	 * }
	 * 
	 * @DataProvider(name="DestinationParameter") public final Object[][]
	 * DestinationParameter(){ return new Object[][]{ { csrAdminUserName,
	 * csrAdminPassword, "",direct_org_id, direct_site_id,"cloud_direct_volume",
	 * "running", "1", direct_user_cloud_account_id,"normal", "wanle05-win7",
	 * "custom","custom", "0", "0", "7", "0", "0", "0", "", "", "", "" } }; }
	 */
	/**
	 * create policy with different policy_type
	 * 
	 * @author leiyu.wang
	 * @param username
	 * @param password
	 * @param policy_type
	 * @param count
	 */
	/*
	 * @Test(dataProvider = "PolicyParameter", enabled=true,priority=2) public void
	 * createPolicy(String username, String password,String policy_type,int count){
	 * 
	 * test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.
	 * currentThread() .getStackTrace()[1].getMethodName());
	 * test.assignAuthor("wang,leiyu");
	 * 
	 * spogServer.userLogin(username, password); String
	 * organization_id=spogServer.GetLoggedinUserOrganizationID();
	 * 
	 * 
	 * String schedule_id=spogServer.returnRandomUUID(); String
	 * task_id=spogServer.returnRandomUUID(); String
	 * task_type="cloud_direct_image_backup"; String
	 * policy_name=spogServer.ReturnRandom("policy_name"); String
	 * policy_description=spogServer.ReturnRandom("description"); String
	 * policy_id=spogServer.returnRandomUUID(); String
	 * policy_type="cloud_direct_draas";
	 * 
	 * HashMap<String, Object> cloudDirectScheduleDTO=policy4SPOGServer.
	 * createCloudDirectScheduleDTO("0 6 * * *", test); HashMap<String, Object>
	 * scheduleSettingDTO=policy4SPOGServer.createScheduleSettingDTO(
	 * cloudDirectScheduleDTO, null,test); ArrayList<HashMap<String,Object>>
	 * schedules =policy4SPOGServer.createPolicyScheduleDTO(null,schedule_id, "1d",
	 * task_id, this.msp_ac1_zero_id, scheduleSettingDTO, "06:00", "12:00",
	 * task_type ,this.msp_ac1_zero_name,test);
	 * 
	 * HashMap<String, Object>
	 * cloudDirectLocalBackupDTO=policy4SPOGServer.createCloudDirectLocalBackupDTO(
	 * "*", "true", test); HashMap<String, Object>
	 * cloudDirectImageBackupTaskInfoDTO=policy4SPOGServer.
	 * createCloudDirectImageBackupTaskInfoDTO("*", cloudDirectLocalBackupDTO,
	 * test); ArrayList<HashMap<String,Object>> destinations=
	 * policy4SPOGServer.createPolicyTaskDTO(null, task_id, task_type,
	 * this.msp_ac1_zero_id, "none", cloudDirectImageBackupTaskInfoDTO, null, null,
	 * test); ArrayList<HashMap<String,Object>> throttles
	 * =policy4SPOGServer.createPolicyThrottleDTO(null,
	 * spogServer.returnRandomUUID(), task_id, "network", "1200", "1", "06:00",
	 * "18:00", task_type , this.msp_ac1_zero_id,this.msp_ac1_zero_name,test);
	 * //spogServer.userLogin(this.csrAdmin, this.csrPwd); String
	 * user_token=spogServer.getJWTToken(); policy4SPOGServer.setToken(user_token);
	 * Response response=null;
	 * 
	 * if(hasschedule){ if(hasthrottle){
	 * response=policy4SPOGServer.createPolicy(policy_name, policy_description,
	 * policy_type, null, "true", this.direct_source_id, destinations, schedules,
	 * throttles, policy_id, organization_id, test); }else{
	 * response=policy4SPOGServer.createPolicy(policy_name, policy_description,
	 * policy_type, null, "true", this.direct_source_id, destinations, schedules,
	 * null, policy_id, organization_id, test); } }else{ if(hasthrottle){
	 * response=policy4SPOGServer.createPolicy(policy_name, policy_description,
	 * policy_type, null, "true", this.direct_source_id, destinations, null,
	 * throttles, policy_id, organization_id, test); }else{
	 * response=policy4SPOGServer.createPolicy(policy_name, policy_description,
	 * policy_type, null, "true", this.direct_source_id, destinations, null, null,
	 * policy_id, organization_id, test); } }
	 * policy4SPOGServer.checkPolicyWithErrorInfo(response,status,errorcode,errormes
	 * ,test);
	 * 
	 * }
	 */

	@DataProvider(name = "PolicyParameter")
	public final Object[][] PolicyParameter() {
		return new Object[][] { { final_direct_user_name_email, common_password, "backup_recovery", 1 },
				{ final_direct_user_name_email, common_password, "backup_recovery", 1 },
				{ final_direct_user_name_email, common_password, "disaster_recovery", 2 },
				{ final_direct_user_name_email, common_password, "replication_and_high_availability", 1 },

				{ final_msp_user_name_email, common_password, "archiving", 1 },
				{ final_msp_user_name_email, common_password, "archiving", 2 },

				{ account_user_email, common_password, "replication_and_high_availability", 1 },
				{ account_user_email, common_password, "replication_and_high_availability", 2 },
				{ account_user_email, common_password, "disaster_recovery", 1 },

		};
	}

	/**
	 * Test cases: direct admin could get amount and policy type for its
	 * organization MSP admin could get amount and policy type for its organization
	 * MSP admin could get amount and policy type for its sub-organization csr admin
	 * could get amount and policy type for direct organization csr admin could get
	 * amount and policy type for msp organization csr admin could get amount and
	 * policy type for msp sub-organization account admin could get amount and
	 * policy type forits organization
	 * 
	 * @param username
	 * @param password
	 * @param org_id
	 * @param backup_recovery_count
	 * @param disaster_recovery_count
	 * @param archiving_count
	 * @param rha_policy_count
	 */
	@Test(dataProvider = "GetPolicyAmountParameter", enabled = true, priority = 2)
	public void GetAmountAndPolicyType(String username, String password, String org_id, int backup_recovery_count,
			int disaster_recovery_count, int archiving_count, int rha_policy_count) {
		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("leiyu.wang");

		test.log(LogStatus.INFO, "admin login");
		spogServer.userLogin(username, password);
		policy4SPOGServer.setToken(spogServer.getJWTToken());

		if (username.equals(final_msp_account_admin_email))
			userSpogServer.assignMspAccountAdmins(msp_org_id, account_id, msp_account_admin_id, csr_token);

		policy4SPOGServer.getAmountAndPolicyTypeWithCheck(org_id, backup_recovery_count, disaster_recovery_count,
				archiving_count, rha_policy_count, test);
	}

	@DataProvider(name = "GetPolicyAmountParameter")
	public Object[][] GetPolicyAmountParameter() {
		return new Object[][] { 
				{ csrAdminUserName, csrAdminPassword, direct_org_id, 0, 0, 0, 0 },
				{ csrAdminUserName, csrAdminPassword, msp_org_id, 0, 0, 0, 0 },
				{ csrAdminUserName, csrAdminPassword, account_id, 0, 0, 0, 0 },
				
				{ csr_readonly_email, csr_readonly_password, direct_org_id, 0, 0, 0, 0 },
				{ csr_readonly_email, csr_readonly_password, msp_org_id, 0, 0, 0, 0 },
				{ csr_readonly_email, csr_readonly_password, account_id, 0, 0, 0, 0 },
				
				{ final_direct_user_name_email, common_password, direct_org_id, 0, 0, 0, 0 },
				{ final_msp_user_name_email, common_password, msp_org_id, 0, 0, 0, 0 },
				{ final_msp_user_name_email, common_password, account_id, 0, 0, 0, 0 },
				{ account_user_email, common_password, account_id, 0, 0, 0, 0 },

				{ final_msp_account_admin_email, common_password, account_id, 0, 0, 0, 0 },
				{ final_msp_account_admin_email, common_password, msp_org_id, 0, 0, 0, 0 },
				
				//root msp related
				{ csrAdminUserName, csrAdminPassword, itd.getRoot_msp_org_1(), 0, 0, 0, 0 },
				{ csrAdminUserName, csrAdminPassword, itd.getRoot_msp_org_1_sub_msp_1(), 0, 0, 0, 0 },
				{ csrAdminUserName, csrAdminPassword, itd.getRoot_msp_org_1_sub_msp_1_account_1(), 0, 0, 0, 0 },
				{ csrAdminUserName, csrAdminPassword, itd.getRoot_msp_org_1_account_1(), 0, 0, 0, 0 },
				{ itd.getRoot_msp_org_1_account_admin_1_email(), password, itd.getRoot_msp_org_1_account_1(), 0, 0, 0, 0 },
				{ itd.getRoot_msp_org_1_user_1_email(), password, itd.getRoot_msp_org_1_account_1(), 0, 0, 0, 0 },
				{ itd.getRoot_msp_org_1_sub_msp_1_account_admin_1_email(), password, itd.getRoot_msp_org_1_sub_msp_1_account_1(), 0, 0, 0, 0 },
				{ itd.getRoot_msp_org_1_sub_msp_1_user_1_email(), password, itd.getRoot_msp_org_1_sub_msp_1_account_1(), 0, 0, 0, 0 },
		 };	
	}

	/**
	 * Test cases: direct admin could not get amount and policy type of msp
	 * organization direct admin could not get amount and policy type of msp sub
	 * organization, report 403 msp admin could not get amount and policy type of
	 * direct organization, report 403 sub org admin could not get amount and policy
	 * type of its parent organization, report 403 sub org admin could not get
	 * amount and policy type of direct organization, report 403 MSP account admin
	 * could not get amount and policy type of direct organization, report 403 MSP
	 * account admin could not get amount and policy type of msp organization,
	 * report 403 MSP account admin could not get amount and policy type of other
	 * sub organization, report 403 set orgnization id as invalid uuid in url,
	 * report 400
	 * 
	 * @return
	 */
	@Test(dataProvider = "GetPolicyAmountFail", enabled = true, priority = 1)
	public void GetPolicyAmountFail(String username, String password, String org_id, int statuscode) {
		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("leiyu.wang");

		test.log(LogStatus.INFO, "admin login");
		spogServer.userLogin(username, password);
		policy4SPOGServer.setToken(spogServer.getJWTToken());
		policy4SPOGServer.getAmountforPolicyTypeFail(org_id, statuscode, test);

	}

	@DataProvider(name = "GetPolicyAmountFail")
	public Object[][] GetPolicyAmountFail() {
		return new Object[][] {

				{ final_direct_user_name_email, common_password, msp_org_id, 403 },
				{ final_direct_user_name_email, common_password, account_id, 403 },
				{ final_msp_user_name_email, common_password, direct_org_id, 403 },
				{ account_user_email, common_password, msp_org_id, 403 },
				{ account_user_email, common_password, direct_org_id, 403 },

				{ final_msp_account_admin_email, common_password, direct_org_id, 403 },

				{ final_msp_account_admin_email, common_password, account_id, 403 },

				{ csrAdminUserName, csrAdminPassword, "xxxxxxx", 400 },
				//root msp related
				{itd.getRoot_msp_org_1_user_1_email(), password, itd.getRoot_msp_org_1_sub_msp_1(), 403},
				{itd.getRoot_msp_org_1_user_1_email(), password, itd.getRoot_msp_org_1_sub_msp_1_account_1(), 403},
				{itd.getRoot_msp_org_1_sub_msp_1_account_admin_2_email(), password, itd.getRoot_msp_org_1_sub_msp_1_account_1(), 403},
				{itd.getRoot_msp_org_1_account_admin_2_email(), password, itd.getRoot_msp_org_1_account_1(), 403},
				{itd.getRoot_msp_org_1_user_1_email(), password, itd.getRoot_msp_org_2(), 403},
				{itd.getRoot_msp_org_1_user_1_email(), password, itd.getRoot_msp_org_2(), 403},
				{itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1_email(), password, itd.getRoot_msp_org_1_sub_msp_1_account_2(), 403},
				{itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1_email(), password, itd.getRoot_msp_org_1_sub_msp_2_account_1(), 403},
		};
	}

	// could not get amount and policy type without token, report401
	@Test(enabled = true, priority = 3)
	public void GetPolicyAmountwithoutToken() {
		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("leiyu.wang");

		test.log(LogStatus.INFO, "admin login");
		policy4SPOGServer.setToken("");
		policy4SPOGServer.getAmountforPolicyTypeFail(spogServer.GetLoggedinUserOrganizationID(), 401, test);
	}

	// could not get amount and policy type with invalid token
	@Test(enabled = true, priority = 4)
	public void GetPolicyAmountwithoutInvalidToken() {
		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("leiyu.wang");

		test.log(LogStatus.INFO, "admin login");
		policy4SPOGServer.setToken("*****************************");
		policy4SPOGServer.getAmountforPolicyTypeFail(spogServer.GetLoggedinUserOrganizationID(), 401, test);
	}

	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "csrAdminUserName", "csrAdminPassword", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String adminUserName, String adminPassword,
			String buildVersion) throws UnknownHostException {

		spogServer = new SPOGServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		policy4SPOGServer = new Policy4SPOGServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		rep = ExtentManager.getInstance("GetAmountAndPolicyTypeForSpecifiedOrganizationTest", logFolder);
		this.csrAdminUserName = adminUserName;
		this.csrAdminPassword = adminPassword;

		test = rep.startTest("beforeClass");
		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
		csr_token = spogServer.getJWTToken();
		// *******************create direct org,user,site**********************/
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		test.log(LogStatus.INFO, "create a direct org");
		direct_org_id = spogServer.CreateOrganizationWithCheck(prefix + direct_org_name + org_model_prefix,
				SpogConstants.DIRECT_ORG, null, common_password, null, null, test);
		final_direct_user_name_email = prefix + direct_user_name_email;

		test.log(LogStatus.INFO, "create a admin under direct org");
		direct_user_id = spogServer.createUserAndCheck(final_direct_user_name_email, common_password,
				prefix + direct_user_first_name, prefix + direct_user_last_name, SpogConstants.DIRECT_ADMIN,
				direct_org_id, test);
		spogServer.userLogin(final_direct_user_name_email, common_password);

		test.log(LogStatus.INFO, "Getting the JWTToken for the direct user");
		String direct_user_validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "The token is :" + direct_user_validToken);

		String siteName = spogServer.getRandomSiteName("TestCreate");
		test.log(LogStatus.INFO, "Generated a Random SiteName " + siteName);
		String sitetype = siteType.gateway.toString();
		test.log(LogStatus.INFO, "The siteType :" + sitetype);

		test.log(LogStatus.INFO, "Creating a site For direct org");
		direct_site_id = gatewayServer.createsite_register_login(direct_org_id, direct_user_validToken, direct_user_id,
				"leiyu_directSite1", "1.0.0", spogServer, test);
		String direct_site_token = gatewayServer.getJWTToken();
		test.log(LogStatus.INFO, "The site token is " + direct_site_token);

		siteName = spogServer.getRandomSiteName("TestCreate");
		test.log(LogStatus.INFO, "Generated a Random SiteName " + siteName);
		sitetype = siteType.gateway.toString();
		test.log(LogStatus.INFO, "The siteType :" + sitetype);

		test.log(LogStatus.INFO, "Creating another site For direct org");
		another_direct_site_id = gatewayServer.createsite_register_login(direct_org_id, direct_user_validToken,
				direct_user_id, "leiyu_directSite2", "1.0.0", spogServer, test);
		String another_direct_site_token = gatewayServer.getJWTToken();
		test.log(LogStatus.INFO, "The site token is " + another_direct_site_token);

		test.log(LogStatus.INFO, "creating cloudaccount for the user");
		// direct_user_cloud_account_id=spogServer.createCloudAccountWithCheck(prefix+"cloudAccountKey",
		// prefix+"cloudAccountSecret", prefix+"cloudAccountName", "cloud_direct",
		// direct_org_id, "SKUTESTDATA_1_0_0_0_"+prefix,
		// "SKUTESTDATA_1_0_0_0_"+prefix,"91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);

		// ************************create msp
		// org,user,site*************************************
		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
		prefix = RandomStringUtils.randomAlphanumeric(8);
		test.log(LogStatus.INFO, "create a msp org");
		msp_org_id = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name + org_model_prefix,
				SpogConstants.MSP_ORG, null, common_password, null, null, test);
		final_msp_user_name_email = prefix + msp_user_name_email;

		test.log(LogStatus.INFO, "create a admin under msp org");
		msp_user_id = spogServer.createUserAndCheck(final_msp_user_name_email, common_password,
				prefix + msp_user_first_name, prefix + msp_user_last_name, SpogConstants.MSP_ADMIN, msp_org_id, test);
		spogServer.userLogin(final_msp_user_name_email, common_password);

		test.log(LogStatus.INFO, "Getting the JWTToken for the msp user");
		String msp_user_validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "The token is :" + msp_user_validToken);

		siteName = spogServer.getRandomSiteName("TestCreate");
		test.log(LogStatus.INFO, "Generated a Random SiteName " + siteName);
		sitetype = siteType.gateway.toString();
		test.log(LogStatus.INFO, "The siteType :" + sitetype);

		siteName = spogServer.getRandomSiteName("TestCreate");
		test.log(LogStatus.INFO, "Generated a Random SiteName " + siteName);
		sitetype = siteType.gateway.toString();
		test.log(LogStatus.INFO, "The siteType :" + sitetype);

		// create MSP account admin
		test.log(LogStatus.INFO, "create a msp account admin under msp org");
		final_msp_account_admin_email = prefix + this.msp_account_admin_email;
		msp_account_admin_id = spogServer.createUserAndCheck(final_msp_account_admin_email, common_password,
				prefix + msp_account_admin_first_name, prefix + msp_account_admin_last_name,
				SpogConstants.MSP_ACCOUNT_ADMIN, msp_org_id, test);

		// create account, account user and site
		test.log(LogStatus.INFO, "Creating a account For msp org");
		account_id = spogServer.createAccountWithCheck(msp_org_id, "sub_" + prefix + msp_org_name, "", test);

		prefix = RandomStringUtils.randomAlphanumeric(8);

		test.log(LogStatus.INFO, "Creating a account user For account org");
		account_user_email = prefix + msp_user_name_email;
		account_user_id = spogServer.createUserAndCheck(account_user_email, common_password,
				prefix + msp_user_first_name, prefix + msp_user_last_name, SpogConstants.DIRECT_ADMIN, account_id,
				test);
		spogServer.userLogin(account_user_email, common_password);

		test.log(LogStatus.INFO, "Getting the JWTToken for the account user");
		String account_user_validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "The token is :" + account_user_validToken);

		siteName = spogServer.getRandomSiteName("TestCreate");
		test.log(LogStatus.INFO, "Generated a Random SiteName " + siteName);
		sitetype = siteType.gateway.toString();
		test.log(LogStatus.INFO, "The siteType :" + sitetype);

		test.log(LogStatus.INFO, "Creating a site For account org");
		account_site_id = gatewayServer.createsite_register_login(account_id, account_user_validToken, account_user_id,
				"leiyu_accountSite1", "1.0.0", spogServer, test);
		String account_site_token = gatewayServer.getJWTToken();
		test.log(LogStatus.INFO, "The site token is " + account_site_token);

		// account_user_cloud_account_id=spogServer.createCloudAccountWithCheck(prefix+"cloudAccountKey",
		// prefix+"cloudAccountSecret", prefix+"cloudAccountName", "cloud_direct",
		// account_id,"SKUTESTDATA_1_0_0_0_123", "SKUTESTDATA_1_0_0_0_123", test);

		siteName = spogServer.getRandomSiteName("TestCreate");
		test.log(LogStatus.INFO, "Generated a Random SiteName " + siteName);
		sitetype = siteType.gateway.toString();
		test.log(LogStatus.INFO, "The siteType :" + sitetype);

		test.log(LogStatus.INFO, "Creating another site For account org");
		another_account_site_id = gatewayServer.createsite_register_login(account_id, account_user_validToken,
				account_user_id, "leiyu_accountSite2", "1.0.0", spogServer, test);
		String another_account_site_token = gatewayServer.getJWTToken();
		test.log(LogStatus.INFO, "The site token is " + another_account_site_token);
		
		String prefix_a = RandomStringUtils.randomAlphanumeric(8) + this.getClass().getSimpleName();		
		initialTestDataImpl = new InitialTestDataImpl(baseURI, port, csrAdminUserName, csrAdminPassword, prefix_a, password);
		itd = initialTestDataImpl.initialize();

		// this is for update portal
		this.BQName = this.getClass().getSimpleName();
		String author = "leiyu.wang";
		this.runningMachine = InetAddress.getLocalHost().getHostName();
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date = new java.util.Date();
		this.buildVersion = buildVersion + "_" + dateFormater.format(date);
		Nooftest = 0;
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		if (count1.isstarttimehit() == 0) {
			creationTime = System.currentTimeMillis();
			count1.setcreationtime(creationTime);
			try {
				bqdb1.updateTable(this.BQName, runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0",
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
			count1.setskippedcount();
			test.log(LogStatus.SKIP, "Test Case Skipped is " + result.getName());
		} else if (result.getStatus() == ITestResult.SUCCESS) {
			count1.setpassedcount();
		}
		// ending test
		// endTest(logger) : It ends the current test and prepares to create HTML report
		rep.endTest(test);
	}

}
