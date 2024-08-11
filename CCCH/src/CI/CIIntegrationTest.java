package CI;

import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Base64.Base64Coder;
import Constants.ConnectionStatus;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.Log4GatewayServer;
import InvokerServer.Org4SPOGServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.ServerResponseCode;
import InvokerServer.Source4SPOGServer;
import InvokerServer.UserSpogServer;
import api.logs.GetLogs;
import api.users.jobs.columns.CreateLoggedInUserJobColumnsTest;
import genericutil.ExtentManager;
import genericutil.testcasescount;
import invoker.CI4LoadingPageInvoker;
import invoker.SiteTestHelper;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;

public class CIIntegrationTest {
	private SPOGServer spogServer;
	private CI4LoadingPageInvoker cI4LoadingPageInvoker;
	private GatewayServer gatewayServer;
	private Log4GatewayServer log4GatewayServer;
	private Org4SPOGServer org4SPOGServer;
	private Policy4SPOGServer policy4SPOGServer;	
	private Source4SPOGServer source4spogServer;
	private UserSpogServer userSpogServer;
	private SPOGDestinationServer spogDestinationServer;
	private String csrAdmin;
	private String csrPwd;
	private ExtentReports rep;
	private ExtentTest test;
	private testcasescount count1;
	private String csr_token=null;
	private String initial_msp_org_name = "ci_msp1_do_not_delete";
	private String initial_msp_email = "ci_msp01@arcserve.com";
	private String initial_msp_org_id = "9f052f78-b8a7-4a66-9a13-9e88a30c7c4a";
	private String initial_msp_user_id = "125185bc-00ba-4bf2-99c8-09ad4276e09a";
	
	private String initial_sub_org_name_1 = "ci_msp1_account1";
	private String sub_normal_id1="e8e62f05-1107-40dd-9b1e-8d44f1082878";
	private String sub_normal_name1="vol_ztst-3734.zetta.net";
	private String initial_sub_email_1 = "ci_account1_msp1@arcserve.com";
	private String initial_sub_org_id_1 = "c7f4b69d-94a8-4fa9-9a84-4e472c66ea97";
	private String initial_sub_email_user_id1="831733be-6131-4d42-82fa-b4c211feda2f";
	
	private String initial_sub_org_name_2 = "ci_msp1_account2";
	private String sub_normal_id2="f528f7c3-8850-4ddd-86c8-4dd2834d2489";
	private String sub_normal_name2="vol_ztst-2337.zetta.net";
	private String initial_sub_email_2 = "ci_account2_msp1@arcserve.com";
	private String initial_sub_org_id_2 = "216a9980-78ca-4a71-b0e2-661bfba3c43e";
	private String initial_sub_email_user_id2="0a7aa4ef-f7a9-4191-afe9-a4f7366e2125";
	
	private String initial_msp_account_admin_email = "ci_msp1_account_admin@arcserve.com";
	private String initial_msp_account_admin_user_id="35087902-dd35-4b7c-b597-0b2affa7f6cf";
	
	private String initial_direct_org_name = "ci_direct1_do_not_delete";
	private String direct_source_name="LinuxCIM4";
	private String direct_source_id="59cdee1d-5913-486d-8a67-ce1b66e27c66";
	private String initial_direct_email = "ci_direct01@arcserve.com";
	private String initial_direct_user_id = "e1a1604d-0dde-4e0d-b3fc-6722e3dbabd7";
	private String initial_direct_org_id = "e61e1661-4f6c-42e2-84b1-6428f94b4abe";
	private String OrgPwdForPrepare = "Caworld_2017";
	private String direct_normal_id="5eca4341-fae1-4910-95dd-dd2e97e648c0";
	private String direct_normal_name="ci_direct_n1";
	private String datacenter_id="91a9b48e-6ac6-4c47-8202-614b5cdcfe0c";
	private boolean login_ok=false;

	//@BeforeSuite
	public void beforeSuit() {
		try {
			Thread.sleep(300000);
		
		  } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		  }
		System.out.println("enter before suite");
		//spogServer.checkSwagDocIsActive("http://qaspog2.zetta.net", 8080, SpogConstants.SUCCESS_GET_PUT_DELETE);
		// spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("test"),
		// SpogConstants.CSR_ORG, csrAdminUserName, csrAdminPassword, "", "");
	}

	@BeforeClass
	@Parameters({ "baseURI", "port", "csrAdminUserName", "csrAdminPassword", "logFolder", "runningMachine",
			"buildVersion" })
	public void beforeClass(String baseURI, String port, String csrAdminUserName, String csrAdminPassword,
			String logFolder, String runningMachine, String buildVersion) {

		rep = ExtentManager.getInstance("CIIntegrationTest", logFolder);
		test = rep.startTest("initializing data...");
		this.csrAdmin = csrAdminUserName;
		this.csrPwd = csrAdminPassword;
		count1 = new testcasescount();
		System.out.println("enter before class");
		int retry_time = 15;
		
		for(int i=0;i<retry_time;i++){
			try {
				Thread.sleep(20000);
				spogServer = new SPOGServer(baseURI, port);
				spogServer.userLogin(this.csrAdmin, this.csrPwd);
				System.out.println("csr user login for times is:"+i);
				login_ok=true;
				if(login_ok){
					break;
				}
			  } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			  }
		}
		cI4LoadingPageInvoker= new CI4LoadingPageInvoker(baseURI, port);
		System.out.println("create cI4LoadingPageInvoker");
		policy4SPOGServer=new Policy4SPOGServer(baseURI, port);
		System.out.println("create policy4SPOGServer");
		source4spogServer=new Source4SPOGServer(baseURI, port);
		System.out.println("create source4spogServer");
		
		gatewayServer = new GatewayServer(baseURI, port);
		System.out.println("create gatewayServer");
		log4GatewayServer = new Log4GatewayServer(baseURI, port);
		System.out.println("create log4GatewayServer");
		org4SPOGServer = new Org4SPOGServer(baseURI, port);
		System.out.println("create org4SPOGServer");
		userSpogServer = new UserSpogServer(baseURI, port);
		System.out.println("create userSpogServer");
		spogDestinationServer= new SPOGDestinationServer(baseURI, port);
		System.out.println("create spogDestinationServer");
		
		//spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spog_ci"), SpogConstants.CSR_ORG, csrAdminUserName, csrAdminPassword, "ci", "ci");
		
	}

	@Test
	public void BaasPolicyTestForDirect() throws InterruptedException{
		System.out.println("the test class:BaasPolicyTestForDirect");
		assertTrue(login_ok,"login csr failed");
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		System.out.println("csr user login");
		csr_token=spogServer.getJWTToken();
		org4SPOGServer.setToken(spogServer.getJWTToken());
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		// create msp org and one msp admin user for get and update
		String prefix = this.getClass().getSimpleName()+RandomStringUtils.randomAlphanumeric(8);
		//create direct user
		String direct_admin="direct_"+prefix+"@arcserve.com";
		String schedule_id=spogServer.returnRandomUUID();
		String task_id=spogServer.returnRandomUUID();
		String task_type="cloud_direct_file_folder_backup";
		String throttle_id=spogServer.returnRandomUUID();
		String policy_name=spogServer.ReturnRandom("test");
		//String policy_name="12345678123456781234567812345678123456781234567812345678123456781234567812345678123456781234567812345678123456781234567812345678123456781234567812345678123456781234567812345678123456781234567812345678123456781234567812345678123456781234567812345678123456781";
		String policy_description=spogServer.ReturnRandom("description");
		String policy_type="cloud_direct_baas";
		String policy_id=spogServer.returnRandomUUID();
		String backup_job_id=null;
		spogServer.userLogin(this.initial_direct_email, OrgPwdForPrepare);
//		String job_id="89edac2c-6234-4fe7-b5b4-8a951299449b";
//		String job_id1="dc30629d-65b6-4461-94ea-91aeeb889c4f";
		
		
		//spogServer.DeleteUserById("f4854edb-1d12-4a7a-acb9-0fefe0ea18da");
		spogServer.createUserAndCheck(direct_admin, OrgPwdForPrepare, "dd", "gg", SpogConstants.DIRECT_ADMIN, this.initial_direct_org_id,test);
		//login as dirct user
		spogServer.userLogin(direct_admin, OrgPwdForPrepare);
		String direct_user_id=spogServer.GetLoggedinUser_UserID();
//		cI4LoadingPageInvoker.setToken(spogServer.getJWTToken());
//		cI4LoadingPageInvoker.monitorPageCheck(this.initial_direct_org_id);
//		cI4LoadingPageInvoker.protectPageCheck(this.initial_direct_org_id, direct_user_id);
//		cI4LoadingPageInvoker.analyzeCheck(this.initial_direct_org_id, direct_user_id);
//		cI4LoadingPageInvoker.configurationCheck(this.initial_direct_org_id, direct_user_id);
		
		//create baas policy
		HashMap<String, Object> cloudDirectScheduleDTO=policy4SPOGServer.createCloudDirectScheduleDTO("0 6 * * *", test);
		HashMap<String, Object> scheduleSettingDTO=policy4SPOGServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null,test);
		ArrayList<HashMap<String,Object>> schedules =policy4SPOGServer.createPolicyScheduleDTO(null,schedule_id, 
				  "1d", task_id, this.direct_normal_id, scheduleSettingDTO, "06:00", "12:00",task_type ,this.direct_normal_name, test);
		ArrayList<HashMap<String,Object>> excludes=policy4SPOGServer.createExcludeInfoDTO(null, "path", "E:\\backup\\exclude", test);
		//HashMap<String, Object> cloudDirectLocalBackupDTO=policy4SPOGServer.createCloudDirectLocalBackupDTO("E:\\tmp1", "true", test);
		//HashMap<String, Object> cloudDirectFileBackupTaskInfoDTO=policy4SPOGServer.createCloudDirectFileBackupTaskInfoDTO("E:\\backup", null, excludes, test);
		HashMap<String, Object> cloudDirectFileBackupTaskInfoDTO=policy4SPOGServer.createCloudDirectFileBackupTaskInfoDTO("E:\\backup", null, null, test);
		ArrayList<HashMap<String,Object>>  destinations= policy4SPOGServer.createPolicyTaskDTO(null, task_id, task_type, this.direct_normal_id, "none", null, cloudDirectFileBackupTaskInfoDTO, null, test);
		ArrayList<HashMap<String,Object>> throttles =policy4SPOGServer.createPolicyThrottleDTO(null, throttle_id, task_id, "network", "1200", "1", "06:00", "18:00",task_type , this.direct_normal_id,this.direct_normal_name,test);
		//spogServer.userLogin(this.csrAdmin, this.csrPwd);
		String user_token=spogServer.getJWTToken();
		
		//policy4SPOGServer.deletePolicybyPolicyId(csr_token, "755d5bfc-8a23-498a-9fee-7178c8b6eda6", 200, null, test);
		policy4SPOGServer.setToken(user_token);
		Response response=policy4SPOGServer.createPolicy(policy_name, "", policy_type, null, "true",  this.direct_source_id, destinations, schedules, throttles, policy_id, this.initial_direct_org_id, test);
		
		  policy4SPOGServer.checkPolicyDestinations(response,SpogConstants.SUCCESS_POST,destinations,test);
		  
		  //policy4SPOGServer.checkPolicyThrottles(response,SpogConstants.SUCCESS_POST,throttles,test);
		  //policy4SPOGServer.checkPolicyCommon(response, SpogConstants.SUCCESS_POST, policy_name, policy_description, policy_type, null, "true", "success", this.direct_source_id, policy_id, this.initial_direct_org_id, test);
		  //policy4SPOGServer.checkPolicySchedules(response,SpogConstants.SUCCESS_POST,schedules,test);
		  response =policy4SPOGServer.getPolicyById(user_token, policy_id, test);
		  try {
			Thread.sleep(5000);
		
		  } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		  }
		  String check_policyStatus=response.then().extract().path("data.policy_status");
		  for(int loop=1;loop<10;loop++){
			  if(check_policyStatus.equalsIgnoreCase("deploying")){
				  try {
					Thread.sleep(5000);
				
				  } catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				  }
				  response =policy4SPOGServer.getPolicyById(user_token, policy_id, test);
				  check_policyStatus=response.then().extract().path("data.policy_status");
				  if(!check_policyStatus.equalsIgnoreCase("deploying")){
					  break;
				  }				  
			  }else if(check_policyStatus.equalsIgnoreCase("success")){
				  break;
			  }
		  }
		  String check_not_job_status=null;
		 
		  if(check_policyStatus!=null){
			  if(check_policyStatus.equalsIgnoreCase("failure")){
				  check_not_job_status="finished";
			  }else{
				  check_not_job_status="failed";
			  }
			  response = spogServer.getJobs(user_token, "", test);
			  ArrayList<Map<String, Object>> jobs = response.then().extract().path("data");
			  ArrayList<Map<String, Object>> policies = response.then().extract().path("data.policy");
			  String jobID=jobs.get(0).get("job_id").toString();
			  String jobStatus=jobs.get(0).get("job_status").toString();
			  String ret_policyId=policies.get(0).get("policy_id").toString();
			  assertEquals(ret_policyId, policy_id,"check return policy id is right");
			  assertNotEquals(jobStatus, check_not_job_status,"check return job status is right");
		  }
		 
		  Response res=policy4SPOGServer.getSourcesUnderOrgByPolicyType(this.initial_direct_org_id, policy_type, test);
		  policy4SPOGServer.checkSourcesUnderOrgByPolicyType(res, this.initial_direct_org_id, this.initial_direct_org_name, policy_name, policy_id, this.direct_source_id, this.direct_source_name, test);
		  spogServer.userLogin(direct_admin, OrgPwdForPrepare);
		  source4spogServer.setToken(spogServer.getJWTToken());
		  

		  backup_job_id=source4spogServer.submitCDBackupJobAndWaitForRecoveryPoint(this.direct_source_id, test);
		  //check log for backup job
		  Response resLog=GetLogs("job_type;in;backup_full|backup_incremental","log_ts;desc");
		  findMessageFromLog(resLog,backup_job_id,"successfully");
	      
	      
	      String randomA=RandomStringUtils.randomAlphanumeric(20);
		  String restore_job_id=source4spogServer.submitCDRestoreJob(this.direct_source_id, this.direct_source_id, "", "E:\\tmp"+randomA, null, true, true, test);
		  //check log for restore job
		  Response resLog1=GetLogs("job_type;in;restore","log_ts;desc");
		  findMessageFromLog(resLog1,restore_job_id,"successfully");
		  

		  
		  //modify policy
		  policy_name=policy_name+"_modified";
		  response=policy4SPOGServer.updatePolicy(policy_name, "modified", policy_type, null, "true",  this.direct_source_id, destinations, schedules, throttles, policy_id, this.initial_direct_org_id, user_token, test);
		  policy4SPOGServer.checkPolicyDestinations(response,SpogConstants.SUCCESS_GET_PUT_DELETE,destinations,test);
		  response =policy4SPOGServer.getPolicyById(user_token, policy_id, test);
		  try {
			Thread.sleep(5000);
		
		  } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		  }
		  check_policyStatus=response.then().extract().path("data.policy_status");
		  for(int loop=1;loop<10;loop++){
			  if(check_policyStatus.equalsIgnoreCase("deploying")){
				  try {
					Thread.sleep(5000);
				
				  } catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				  }
				  response =policy4SPOGServer.getPolicyById(user_token, policy_id, test);
				  check_policyStatus=response.then().extract().path("data.policy_status");
				  if(!check_policyStatus.equalsIgnoreCase("deploying")){
					  break;
				  }				  
			  }else if(check_policyStatus.equalsIgnoreCase("success")){
				  break;
			  }
		  }
//		  //remove policy
		  policy4SPOGServer.removeSourceFromPolicy(this.direct_source_id, test);
		  
		  // zhaoguo: added 2018/08/13, according to Xiang: remove source from policy triggers policy deployment which may makes delete policy fail, sleep 3 seconds.
		  try{
			  Thread.sleep(3000);
		  } catch(InterruptedException e){
			  e.printStackTrace();
		  }
	
		  policy4SPOGServer.deletePolicybyPolicyId(user_token, policy_id, 200, null, test);
		  //delete the direct admin
		  spogServer.userLogin(this.csrAdmin, this.csrPwd);
		  System.out.println("direct admin is:"+direct_admin);
		  //delete account user
		  spogServer.CheckDeleteUserByIdStatus(direct_user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  
		  
	}
	
	@Test
	public void organizationTest() {
		  System.out.println("the test organizationTest");
		  assertTrue(login_ok,"login csr failed");
		  spogServer.userLogin(this.csrAdmin, this.csrPwd);
		  String csr_token=spogServer.getJWTToken();
		  String pmfKey="shaji02";
		  String direct_name=spogServer.ReturnRandom("useless_shanjing_direct_ci_test");
		  String direct_email=spogServer.ReturnRandom("direct_ci_test@arcserve.com");
		  String msp_name=spogServer.ReturnRandom("msp_ci_test");
		  String msp_email=spogServer.ReturnRandom("msp_ci_test@arcserve.com");
		  String msp_account_email=spogServer.ReturnRandom("msp_account_ci_test@arcserve.com");
		  String account1_name=spogServer.ReturnRandom("useless_shanjing_account1_ci_test");
		  String account1_email=spogServer.ReturnRandom("account1_ci_test@arcserve.com");
		  String account2_name=spogServer.ReturnRandom("useless_shanjing_account2_ci_test");
		  String account2_email=spogServer.ReturnRandom("account2_ci_test@arcserve.com");
		  String account3_email=spogServer.ReturnRandom("account3_ci_test@arcserve.com");
		  spogDestinationServer.setToken(csr_token);
		  String directid=spogServer.CreateOrganizationByEnrollWithCheck(direct_name, "direct", direct_email, OrgPwdForPrepare, "ci_direct1_first_name", "ci_direct1_last_name");
		  spogServer.userLogin(direct_email, OrgPwdForPrepare);
//		  spogDestinationServer.createDestinationWithCheck(  "",directid, "",
//	    		  "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c","cloud_direct_volume", "running", "1",
//	    		  "", "normal", spogServer.ReturnRandom(pmfKey), "custom","custom", "0", "0", "7", "0",
//							"0", "0", "", "", "","","ci_test",test);

		  //check direct user for ui
		  
		  String direct_user_id=spogServer.GetLoggedinUser_UserID();
		  cI4LoadingPageInvoker.setToken(spogServer.getJWTToken());
	      cI4LoadingPageInvoker.monitorPageCheck(directid);
		  cI4LoadingPageInvoker.protectPageCheck(directid, direct_user_id);
		  cI4LoadingPageInvoker.analyzeCheck(directid, direct_user_id);
		  cI4LoadingPageInvoker.configurationCheck(directid, direct_user_id);
		  
		  //create msp org
		  spogServer.userLogin(this.csrAdmin, this.csrPwd);
		  String mspid=spogServer.CreateOrganizationByEnrollWithCheck(msp_name, "msp", msp_email, OrgPwdForPrepare, "ci_msp1_first_name", "ci_msp1_last_name");
		  
		  //check msp admin for ui
		  spogServer.userLogin(msp_email, OrgPwdForPrepare);
		  String msp_admin_user_id=spogServer.GetLoggedinUser_UserID();
		  cI4LoadingPageInvoker.setToken(spogServer.getJWTToken());
	      cI4LoadingPageInvoker.monitorPageCheck(mspid);
		  cI4LoadingPageInvoker.protectPageCheck(mspid, msp_admin_user_id);
		  cI4LoadingPageInvoker.analyzeCheck(mspid, msp_admin_user_id);
		  cI4LoadingPageInvoker.configurationCheck(mspid, msp_admin_user_id);
		  
		  String accountOrgId= spogServer.createAccountWithCheck(mspid,account1_name,"");
//		  spogDestinationServer.createDestinationWithCheck(  "",accountOrgId, "",
//	    		  "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c","cloud_direct_volume", "running", "1",
//	    		  "", "normal", spogServer.ReturnRandom(pmfKey), "custom","custom", "0", "0", "7", "0",
//							"0", "0", "", "", "","","ci_test",test);
		  String accountOrgId2= spogServer.createAccountWithCheck(mspid,account2_name,"");
		  String csrtoken=spogServer.getJWTToken();
		  String[] mspAccountAdminUserIds=new String[1];
		  String OrgPwdForPrepare="Caworld_2017";
		  spogServer.createUserAndCheck(account1_email, OrgPwdForPrepare, "ci_msp1_account1_firstname", "ci_msp1_account1_lastname", SpogConstants.DIRECT_ADMIN, accountOrgId,test);
		  String del_account_user_id=spogServer.createUserAndCheck(account3_email, OrgPwdForPrepare, "ci_msp1_account1_firstname", "ci_msp1_account1_lastname", SpogConstants.DIRECT_ADMIN, accountOrgId,test);
		  spogServer.createUserAndCheck(account2_email, OrgPwdForPrepare, "ci_msp1_account2_firstname", "ci_msp1_account2_lastname", SpogConstants.DIRECT_ADMIN, accountOrgId2,test);
		  mspAccountAdminUserIds[0]=spogServer.createUserAndCheck(msp_account_email, OrgPwdForPrepare, "ci_msp1_account_admin_firstname", "ci_msp1_account_admin_lastname", "msp_account_admin", mspid, test);
		  
		  spogServer.userLogin(msp_email, OrgPwdForPrepare);
		  String mspToken=spogServer.getJWTToken();
		  Response response = userSpogServer.assignMspAccountAdmins(mspid, accountOrgId, mspAccountAdminUserIds, mspToken);
		  userSpogServer.assignMspAccountAdminsWithCheck(response, mspAccountAdminUserIds, mspToken);
		  
		  //check msp account admin for ui
		  spogServer.userLogin(msp_account_email, OrgPwdForPrepare);
		  String msp_account_admin_user_id=spogServer.GetLoggedinUser_UserID();
		  cI4LoadingPageInvoker.setToken(spogServer.getJWTToken());
	      cI4LoadingPageInvoker.monitorPageCheck(mspid);
		  cI4LoadingPageInvoker.protectPageCheck(mspid, msp_account_admin_user_id);
		  cI4LoadingPageInvoker.analyzeCheck(mspid, msp_account_admin_user_id);
		  cI4LoadingPageInvoker.configurationCheck(mspid, msp_account_admin_user_id);
		  
		  //check msp sub admin for ui
		  spogServer.userLogin(account1_email, OrgPwdForPrepare);
		  String msp_sub_user_id=spogServer.GetLoggedinUser_UserID();
		  cI4LoadingPageInvoker.setToken(spogServer.getJWTToken());
	      cI4LoadingPageInvoker.monitorPageCheck(accountOrgId);
		  cI4LoadingPageInvoker.protectPageCheck(accountOrgId, msp_sub_user_id);
		  cI4LoadingPageInvoker.analyzeCheck(accountOrgId, msp_sub_user_id);
		  cI4LoadingPageInvoker.configurationCheck(accountOrgId, msp_sub_user_id);
		  spogServer.userLogin(msp_email, OrgPwdForPrepare);
		  spogServer.CheckDeleteUserByIdStatus(del_account_user_id,SpogConstants.SUCCESS_GET_PUT_DELETE,test);
		  //login as csr admin to destroy organizations
		  spogServer.userLogin(this.csrAdmin, this.csrPwd);
		  org4SPOGServer.setToken(spogServer.getJWTToken());
		  //detroy direct org
		  org4SPOGServer.destroyOrganization(directid,test);
		  //destroy msp org
		  org4SPOGServer.destroyOrganization(mspid,test);
	}
	
	//@Test
	public void BaasPolicyTestForMspAccount() throws InterruptedException{
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		// create msp org and one msp admin user for get and update
		String prefix = this.getClass().getSimpleName()+RandomStringUtils.randomAlphanumeric(8);
		//create direct user
		String account_admin="account_"+prefix+"@arcserve.com";
		String schedule_id=spogServer.returnRandomUUID();
		String task_id=spogServer.returnRandomUUID();
		String task_type="cloud_direct_file_folder_backup";
		String throttle_id=spogServer.returnRandomUUID();
		String policy_name=spogServer.ReturnRandom("test");
		String policy_description=spogServer.ReturnRandom("description");
		String policy_type="cloud_direct_baas";
		String policy_id=spogServer.returnRandomUUID();
		String backup_job_id=null;
		spogServer.userLogin(this.initial_msp_email, OrgPwdForPrepare);
//		String job_id="89edac2c-6234-4fe7-b5b4-8a951299449b";
//		String job_id1="dc30629d-65b6-4461-94ea-91aeeb889c4f";
		
		
		spogServer.DeleteUserById("f4854edb-1d12-4a7a-acb9-0fefe0ea18da");
		spogServer.createUserAndCheck(account_admin, OrgPwdForPrepare, "dd", "gg", SpogConstants.DIRECT_ADMIN, this.initial_sub_org_id_1,test);
		//login as dirct user
		spogServer.userLogin(account_admin, OrgPwdForPrepare);
		String account_user_id=spogServer.GetLoggedinUser_UserID();
		
		//create baas policy
		HashMap<String, Object> cloudDirectScheduleDTO=policy4SPOGServer.createCloudDirectScheduleDTO("0 6 * * *", test);
		HashMap<String, Object> scheduleSettingDTO=policy4SPOGServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null,test);
		ArrayList<HashMap<String,Object>> schedules =policy4SPOGServer.createPolicyScheduleDTO(null,schedule_id, 
				  "1d", task_id, this.direct_normal_id, scheduleSettingDTO, "06:00", "12:00",task_type ,this.direct_normal_name, test);
		ArrayList<HashMap<String,Object>> excludes=policy4SPOGServer.createExcludeInfoDTO(null, "path", "E:\\tmp\\backup", test);
		//HashMap<String, Object> cloudDirectLocalBackupDTO=policy4SPOGServer.createCloudDirectLocalBackupDTO("E:\\tmp1", "true", test);
		HashMap<String, Object> cloudDirectFileBackupTaskInfoDTO=policy4SPOGServer.createCloudDirectFileBackupTaskInfoDTO("E:\\tmp1", null, excludes, test);
		ArrayList<HashMap<String,Object>>  destinations= policy4SPOGServer.createPolicyTaskDTO(null, task_id, task_type, this.direct_normal_id, "none", null, cloudDirectFileBackupTaskInfoDTO, null, test);
		ArrayList<HashMap<String,Object>> throttles =policy4SPOGServer.createPolicyThrottleDTO(null, throttle_id, task_id, "network", "1200", "1", "06:00", "18:00",task_type , this.direct_normal_id,this.direct_normal_name,test);
		String user_token=spogServer.getJWTToken();
		
		policy4SPOGServer.deletePolicybyPolicyId(csr_token, "755d5bfc-8a23-498a-9fee-7178c8b6eda6", 200, null, test);
		policy4SPOGServer.setToken(user_token);
		Response response=policy4SPOGServer.createPolicy(policy_name, "", policy_type, null, "true",  this.direct_source_id, destinations, schedules, throttles, policy_id, this.initial_direct_org_id, test);
		  policy4SPOGServer.checkPolicyDestinations(response,SpogConstants.SUCCESS_POST,destinations,test);
		  //policy4SPOGServer.checkPolicyThrottles(response,SpogConstants.SUCCESS_POST,throttles,test);
		  //policy4SPOGServer.checkPolicyCommon(response, SpogConstants.SUCCESS_POST, policy_name, policy_description, policy_type, null, "true", "success", this.direct_source_id, policy_id, this.initial_direct_org_id, test);
		  //policy4SPOGServer.checkPolicySchedules(response,SpogConstants.SUCCESS_POST,schedules,test);
		  response =policy4SPOGServer.getPolicyById(user_token, policy_id, test);
		  try {
			Thread.sleep(5000);
		
		  } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		  }
		  String check_policyStatus=response.then().extract().path("data.policy_status");
		  for(int loop=1;loop<10;loop++){
			  if(check_policyStatus.equalsIgnoreCase("deploying")){
				  try {
					Thread.sleep(5000);
				
				  } catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				  }
				  response =policy4SPOGServer.getPolicyById(user_token, policy_id, test);
				  check_policyStatus=response.then().extract().path("data.policy_status");
				  if(!check_policyStatus.equalsIgnoreCase("deploying")){
					  break;
				  }				  
			  }else if(check_policyStatus.equalsIgnoreCase("success")){
				  break;
			  }
		  }
		  String check_not_job_status=null;
		 
		  if(check_policyStatus!=null){
			  if(check_policyStatus.equalsIgnoreCase("failure")){
				  check_not_job_status="finished";
			  }else{
				  check_not_job_status="failed";
			  }
			  response = spogServer.getJobs(user_token, "", test);
			  ArrayList<Map<String, Object>> jobs = response.then().extract().path("data");
			  String jobID=jobs.get(0).get("job_id").toString();
			  String jobStatus=jobs.get(0).get("job_status").toString();
			  String ret_policyId=jobs.get(0).get("policy_id").toString();
			  assertEquals(ret_policyId, policy_id,"check return policy id is right");
			  assertNotEquals(jobStatus, check_not_job_status,"check return job status is right");
		  }
		  //modified
		  
		  
		  Response res=policy4SPOGServer.getSourcesUnderOrgByPolicyType(this.initial_direct_org_id, policy_type, test);
		  policy4SPOGServer.checkSourcesUnderOrgByPolicyType(res, this.initial_direct_org_id, this.initial_direct_org_name, policy_name, policy_id, this.direct_source_id, this.direct_source_name, test);
		  source4spogServer.setToken(spogServer.getJWTToken());
		  backup_job_id=source4spogServer.submitCDBackupJobAndWaitForRecoveryPoint(this.direct_source_id, test);
		  //check log for backup job
		  Response resLog=GetLogs("job_type;in;backup_full|backup_incremental","log_ts;desc");
		  findMessageFromLog(resLog,backup_job_id,"successfully");
	      
	      
	      String randomA=RandomStringUtils.randomAlphanumeric(20);
		  String restore_job_id=source4spogServer.submitCDRestoreJob(this.direct_source_id, this.direct_source_id, "", "E:\\tmp"+randomA, null, true, true, test);
		  //check log for restore job
		  Response resLog1=GetLogs("job_type;in;restore","log_ts;desc");
		  findMessageFromLog(resLog1,restore_job_id,"successfully");
		  
		  policy_name=policy_name+"_modified";
		  response=policy4SPOGServer.updatePolicy(policy_name, "modified", policy_type, null, "true",  this.direct_source_id, destinations, schedules, throttles, policy_id, this.initial_direct_org_id, user_token, test);
		  policy4SPOGServer.checkPolicyDestinations(response,SpogConstants.SUCCESS_GET_PUT_DELETE,destinations,test);
		  response =policy4SPOGServer.getPolicyById(user_token, policy_id, test);
		  try {
			Thread.sleep(5000);
		
		  } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		  }
		  check_policyStatus=response.then().extract().path("data.policy_status");
		  for(int loop=1;loop<10;loop++){
			  if(check_policyStatus.equalsIgnoreCase("deploying")){
				  try {
					Thread.sleep(5000);
				
				  } catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				  }
				  response =policy4SPOGServer.getPolicyById(user_token, policy_id, test);
				  check_policyStatus=response.then().extract().path("data.policy_status");
				  if(!check_policyStatus.equalsIgnoreCase("deploying")){
					  break;
				  }				  
			  }else if(check_policyStatus.equalsIgnoreCase("success")){
				  break;
			  }
		  }
		  //remove policy
		  policy4SPOGServer.removeSourceFromPolicy(this.direct_source_id, test);
		  res=policy4SPOGServer.getSourcesUnderOrgByPolicyType(this.initial_direct_org_id, policy_type, test);
		  policy4SPOGServer.checkSourcesUnderOrgByPolicyType(res, this.initial_direct_org_id, this.initial_direct_org_name,  this.direct_source_id, this.direct_source_name, test);
		  //delete policy
		  //policy4SPOGServer.updatePolicy(policy_name+"_modified", "modified", policy_type, null, "true",  this.direct_source_id, destinations, schedules, throttles, policy_id, this.initial_direct_org_id, user_token, test);
		  policy4SPOGServer.deletePolicybyPolicyId(user_token, policy_id, 200, null, test);
		  //delete the direct admin
		  spogServer.userLogin(this.csrAdmin, this.csrPwd);
		  System.out.println("direct admin is:"+account_admin);
		  spogServer.DeleteUserById(account_user_id);
		  
		  	
	}
	
	public Response GetLogs(String filterStr,String SortStr){
		ArrayList<HashMap<String,Object>> Logsinfo=new ArrayList<HashMap<String,Object>>();
		String[] filterArray=null;
		String[] filters=null;
		String filtersdata=null;
		String filterName = null,filterOperator = null, filterValue = null;
		ArrayList<HashMap<String,Object>> Logs_info = new ArrayList<>();
		//It is related to the Filter on the single value 
		if(filterStr!=""&&filterStr!=null&&!filterStr.contains(",")){
			filterArray = filterStr.split(";");
			filterName = filterArray[0];filterOperator = filterArray[1];filterValue = filterArray[2];
			Logs_info=spogServer.getLogsInfo(Logsinfo,filterName,filterOperator,filterValue);
			Logsinfo=Logs_info;		

			if(SortStr!=""&&SortStr.contains("log_ts")&&!(filterStr.contains("log_ts"))){
				Logsinfo=spogServer.getLogsTimeSortInfo(SortStr, Logsinfo, test);	
			}
		}//It is related to the filtering on the multiple values 
		else if(filterStr!=""&& filterStr!=null&&filterStr.contains(",")) {
			filters = filterStr.split(",");	
			for(int i=0;i<filters.length;i++){
				filtersdata=filters[i];
				filterArray = filtersdata.split(";");
				filterName = filterArray[0];filterOperator = filterArray[1];filterValue = filterArray[2];		
				Logs_info=spogServer.getLogsInfo(Logsinfo,filterName,filterOperator,filterValue);
				Logsinfo=Logs_info;			
			}			
			if(SortStr!=""&&SortStr.contains("log_ts")&&!(filterStr.contains("log_ts"))){
				Logsinfo=spogServer.getLogsTimeSortInfo(SortStr, Logsinfo, test);	
			}
		}	
		//For the sorting based on log_severity_type and job_type and default is (Log_ts)
		if(SortStr.contains("log_severity_type")||SortStr.contains("job_type")) {
			Logsinfo=spogServer.getLogsSortInfo(SortStr, Logsinfo, test);
		}
		//preparing the URL and validating the Response For 
		String additionalURL=spogServer.PrepareURL(filterStr,SortStr,1, 20, test);	
		Response response=spogServer.getLogs(spogServer.getJWTToken(),additionalURL,test);
		response.then().log().all();
		return response;
	}
	
	public void findMessageFromLog(Response res,String job_id,String log_message){
		boolean ret=false;
		if(res!=null){
			int totalsize=res.then().extract().path("pagination.total_size");
			if(totalsize>=1){
				System.out.println("log totalsize is:"+totalsize);
				System.out.println("search job id:"+job_id);
				System.out.println("search log message:"+log_message);
				for(int i=0;i<totalsize;i++){
					if(res.then().extract().path("data["+i+"].job_data.job_id")!=null){
						System.out.println(res.then().extract().path("data["+i+"].job_data.job_id").toString());
					}
					if(res.then().extract().path("data["+i+"].message")!=null){
						System.out.println(res.then().extract().path("data["+i+"].message").toString());
					}
//					if(res.then().extract().path("data["+i+"].job_data.job_id").toString().equalsIgnoreCase(job_id)&&(res.then().extract().path("data["+i+"].message").toString().indexOf(log_message)!=-1)){
//						ret=true;
//						break;
//					}
				}			
			} 
		}else{
			System.out.println("return log is null");
		}		 
		//assertTrue(ret,"check log has the given message failed");
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
		rep.endTest(test);
	}
	
	
	@AfterTest
	public void aftertest() {
		test.log(LogStatus.INFO, "The total test cases passed are " + count1.getpassedcount());
		test.log(LogStatus.INFO, "the total test cases failed are " + count1.getfailedcount());
		rep.flush();
		
	}
}
