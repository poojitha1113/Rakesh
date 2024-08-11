package api.policies;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.SpogConstants;
import InvokerServer.GatewayServer;
import InvokerServer.Log4GatewayServer;
import InvokerServer.Org4SPOGServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.Source4SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.FolderOrFileUtils;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import invoker.CI4LoadingPageInvoker;
import io.restassured.response.Response;

public class Action4SourceTest {
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
	  private String existing_schedule_id="54d2f50b-abaa-409f-a216-9a3444e561f8";
	  private String existing_task_id="715039a2-5b3b-4dee-9297-cc34b58fd870";
	  private String existing_throttle_id="b1610ab0-5311-4ed7-b41b-460c1ab3aa6b";
	  private String existing_policy_id="7d7c64a2-a70a-43fa-9468-126bba813ede";
	  
	  
	  //this is creating msp org or direct org for preparation
	  
	  private String OrgPwdForPrepare="Caworld2017";
	  private String csrOrgId,csrOrgEmailForPrepare;
	  private ExtentReports rep;
	  private ExtentTest test;
	  //this is for update portal, each testng class is taken as BQ set
	  private SQLServerDb bqdb1;
	  public int Nooftest;
	  private long creationTime;
	  private String BQName=null;
	  private String runningMachine;
	  private testcasescount count1;
	  private String buildVersion;
	  private String password=OrgPwdForPrepare;
	  private String datacenter_id="91a9b48e-6ac6-4c47-8202-614b5cdcfe0c";
	  
	  //the first direct
	  private String direct_org_id="30d9c2c4-25fd-44a0-8e43-2288d06c4f14";
	  private String direct_org_name="shaji02-arcserve-direct1";
	  private String direct_admin_email="belly_sj@163.com";
	  private String cloud_direct_account_id="debf660e-4d83-413f-93c2-74744a2f2d8e";
	  private String direct_zero_id="fc400b17-9cbe-450a-84b4-21193726f625";
	  private String direct_normal_id="4a47f3f8-79e2-4656-a9d4-e9e8123bf82f";
	  private String recycle_normal_id="51a59fae-920a-47fb-8390-6666c26fc33e";
	  private String recycle_zero_id="ffc01997-a041-4e0c-9dc7-7735bdab570a";
	  //the second direct
	  private String direct_org_id1="1ab97067-24f2-452e-b816-b42781cc2c09";
	  private String direct_org_name1="jing_spogqa_direct_org_ui_1";
	  private String direct_admin_email1="jing_direct_ui@arcserve.com";
	  private String direct_password = "Caworld_2017";
		
//	  private String cloud_direct_account_id1="9aeb9c55-fb96-4e75-a166-73ac47ce3f9f";
	  private String direct_zero_id1="b92aa6ca-f0e3-48c5-abdc-97a282e68366";
	  private String direct_normal_id1="715ff56d-1047-4ccf-9c58-ae58f846c1fa";
//	  private String direct_source_id="9606e1e1-6dba-46c6-a286-6a242da5ee6b";
//	  private String direct_source_name="shaji02-spog2";
	  private String direct_source_id1="90ad0cf4-6cb3-4d08-b33e-530f44818263";
	  private String direct_source_linux_id="e02630fd-aae2-4a24-a098-efbb30f7fece";
	  private String direct_source_linux_name="linux-ubuntu";
	  private String direct_source_name1="LinuxCIM2";
	  private String direct_source_2003="05f6268b-8b52-471b-b1d4-21367e21e7e2";
	  private String direct_source_win10="9dc7a6b2-d504-4578-b1f0-677442ff1cc9";
	  private String direct_source_xp="b172a523-9173-4de2-9adc-2d70118fee62";
	  private String direct_source_2000="6b77ae5e-6df4-4951-83b4-6068013b332e";
	  private String direct_source_win7="d8dca02a-3f55-474c-a6cc-d9f65b283cbe";
	  private String cloud_source_2003="shaji02-2003";
	  private String cloud_source_win10="shaji02-win10";
	  private String cloud_source_xp="shaji02-xp";
	  private String cloud_source_2000="shaji02-2000";
	  private String cloud_source_7="shaji02-win7";
	  private String udp_source_name="shaji02_direct_udp_source";
	  private String direct_udp_source_id = "06ecf17e-d845-4c08-a79b-52a57f85a244";
	  private String udp_source_name1="shaji02_direct_udp_source1";
	  private String direct_udp_source_id1 = "73ede615-9ee5-4545-b0d8-2d112123d380";
	  
	//ac1 assign to msp account admin
	  private String msp_org_id="f2c1631d-cec1-4b0e-aa70-69f5fc500ec7";
	  private String msp_admin_email="shaji02-msp01@arcserve.com";
	  private String msp_org_name="shaji02-msp01_do_not_delete";
	  private String msp_ac_admin_email="shaji02-msp-account@arcserve.com";
	  private String msp_ac1_email="shaji02-account-1@arcserve.com";
	  private String msp_ac2_email="shaji02-account-2@arcserve.com";
	  private String cloud_msp_account_id="5f6a48be-5616-4dfe-95de-53a2439e952c";
	  private String msp_account_org_id_1="dc7721ff-41ca-4e59-9af9-28e4ae0ca44b";
	  private String msp_account_org_id_2="a002c19c-9c9d-4a39-bf53-bf7d245296f0";
	  private String msp_zero_id="fc400b17-9cbe-450a-84b4-21193726f625";
	  private String msp_ac1_normal_id="271c7ccb-4382-4246-9d68-e1c0fac5fcaf";
	  private String msp_ac1_normal_name="vol_ztst-4987.zetta.net";
	  private String msp_ac2_normal_id="257a71cd-8842-4b84-ac5a-09d71ec46c8d";
	  private String msp_ac2_normal_name="vol_ztst-4988.zetta.net";
	  private String msp_ac1_zero_id="6a1a8031-7caa-4d10-b200-d0edbfeb3987";
	  private String msp_ac2_zero_id="ecbe3a7b-95b4-4eee-8f34-c604dce8945d";
	  private String msp_ac1_zero_name="Disaster Recovery";
	  
	  
	  //ac3 assign to msp account admin
	  private String msp_org_id1="3ed9ce7c-95a3-495d-8419-71866aac0b3d";
	  private String msp_admin_email1="shaji02-msp2@arcserve.com";
	  private String msp_org_name1="shaji02-msp2_do_not_delete";
	  private String msp_ac_admin_email1="shaji02-msp-account1@arcserve.com";
	  private String msp_ac3_email="shaji02-account-3@arcserve.com";
	  private String msp_ac4_email="shaji02-account-4@arcserve.com";
	  private String cloud_msp_account_id1="75f70564-d346-48ce-8967-608e0f330b13";
	  private String msp_account_org_id_3="a364c1e3-2b5a-41b7-a7b5-247fc2028399";
	  private String msp_account_org_id_4="d533541a-9898-4ada-8062-359e4c61cfc5";
	  private String msp_zero_id1="fc400b17-9cbe-450a-84b4-21193726f625";
	  private String msp_ac3_normal_id="4a563569-7581-4f00-a963-c59f2fb8dfc9";
	  private String msp_ac4_normal_id="d73f216d-b75a-4db1-b49e-05f0a0ac6e32";
	  
	  
	  private String source_name_1="udp_Jing.Shan_PthxuumX";
	  private String source_id_1="ffd0cb04-47bd-4507-9594-d02aebf22139";
	  private String destination_id_1="1bf5aa1f-6802-4ad5-9825-d6356314dc0c";
	  private String destination_normal_id="135e29e6-6a05-4e0a-8e18-5294fe7c0a76";
	  //private String destination_normal_id="6a89f898-381b-4e4e-92bc-eb7fddb077b2";
	  private String job_id_1="cd6ff192-805c-43b5-b2ec-8ca3bc61e05f";
	  private String log_id_1="4ddb2da0-3c6e-446d-aec9-bde02f1a3d7a";
	  private String account_user_id,siteName,sitetype,account_site_id,csr_token,udp_source_id,udp_source_id1,destination_store_id,destination_store_id1;
	  private String destination_name="shaji02-test",destination_volume_id,recycle_destination_volume_id;
	  
	  
	  private String real_cd_user="d_jing_spogqa_direct_org_prepare0327@arcserve.comg2ZPITWH";
	  private String real_cd_pwd=OrgPwdForPrepare;
	  private String real_cd_org_id="19b5ac9a-7250-4f58-83b7-7e1d34d12dbd";
	  private String real_cloud_account_id="7e0e0db3-cbee-4989-a272-42bf6439cbfb";
	  private String real_cloud_secret_key="cloudAccountSecret";
	  //end 
	  @BeforeClass
	  @Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword","logFolder","runningMachine","buildVersion"})
	  public void beforeClass(String baseURI, String port, String csrAdminUserName, String csrAdminPassword, String logFolder, String runningMachine, String buildVersion) {
		//this is for update portal
		  this.BQName = this.getClass().getSimpleName();
		  String author = "Jing.Shan";
		  this.runningMachine = runningMachine;
		  this.csrAdmin=csrAdminUserName;
		  this.csrPwd=csrAdminPassword;
		  SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		  java.util.Date date=new java.util.Date();
		  this.buildVersion=buildVersion+"_"+dateFormater.format(date);
		  //FolderOrFileUtils.deleteFolder("Z:\\ztst-3371");
		  Nooftest=0;
		  bqdb1 = new SQLServerDb();
		  count1 = new testcasescount();
		  if(count1.isstarttimehit()==0) {
			System.out.println("Into get loggedInUserById");
			creationTime=System.currentTimeMillis();
			count1.setcreationtime(creationTime);
			//creationTime = System.currentTimeMillis();
			try {
				bqdb1.updateTable(this.BQName, runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0", String.valueOf(Nooftest), count1.getcreationtime(), "InProgress",author+" and Rest server is "+baseURI.split("//")[1]);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  }
		  //end 
		  spogServer = new SPOGServer(baseURI, port);
		  spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		  policy4SPOGServer= new Policy4SPOGServer(baseURI, port);
		  gatewayServer= new GatewayServer(baseURI, port);
		  rep = ExtentManager.getInstance("CreateAccountTest",logFolder);
		  test = rep.startTest("beforeClass");
		  this.csrAdmin = csrAdminUserName;
		  this.csrPwd = csrAdminPassword;
		  
		  spogServer.userLogin(this.csrAdmin, this.csrPwd);
		  csr_token=spogServer.getJWTToken();
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
	  }

	@Test
	public void BaasPolicyTestForDirect() throws InterruptedException{
		System.out.println("the test class:BaasPolicyTestForDirect");
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
		String policy_description=spogServer.ReturnRandom("description");
		String policy_type="cloud_direct_baas";
		String policy_id=spogServer.returnRandomUUID();
		String backup_job_id=null;
		spogServer.userLogin(this.direct_admin_email1, direct_password);

		//spogServer.DeleteUserById("f4854edb-1d12-4a7a-acb9-0fefe0ea18da");
		spogServer.createUserAndCheck(direct_admin, OrgPwdForPrepare, "dd", "gg", SpogConstants.DIRECT_ADMIN, this.direct_org_id1,test);
		//login as dirct user
		spogServer.userLogin(direct_admin, OrgPwdForPrepare);
		String direct_user_id=spogServer.GetLoggedinUser_UserID();

		//create baas policy
		HashMap<String, Object> cloudDirectScheduleDTO=policy4SPOGServer.createCloudDirectScheduleDTO("0 6 * * *", test);
		HashMap<String, Object> scheduleSettingDTO=policy4SPOGServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null,test);
		ArrayList<HashMap<String,Object>> schedules =policy4SPOGServer.createPolicyScheduleDTO(null,schedule_id, 
				  "1d", task_id, this.direct_normal_id1, scheduleSettingDTO, "06:00", "12:00",task_type ,"test", test);
		ArrayList<HashMap<String,Object>> excludes=policy4SPOGServer.createExcludeInfoDTO(null, "path", "c:\\api_policy_source\\tmp", test);
		//HashMap<String, Object> cloudDirectLocalBackupDTO=policy4SPOGServer.createCloudDirectLocalBackupDTO("E:\\tmp1", "true", test);
		HashMap<String, Object> cloudDirectFileBackupTaskInfoDTO=policy4SPOGServer.createCloudDirectFileBackupTaskInfoDTO("c:\\api_policy_source", null, excludes, test);
		ArrayList<HashMap<String,Object>>  destinations= policy4SPOGServer.createPolicyTaskDTO(null, task_id, task_type, this.direct_normal_id1, "none", null, cloudDirectFileBackupTaskInfoDTO, null, test);
		ArrayList<HashMap<String,Object>> throttles =policy4SPOGServer.createPolicyThrottleDTO(null, throttle_id, task_id, "network", "1200", "1", "06:00", "18:00",task_type , this.direct_normal_id1,"test",test);
		//spogServer.userLogin(this.csrAdmin, this.csrPwd);
		String user_token=spogServer.getJWTToken();
		
		  
		//policy4SPOGServer.deletePolicybyPolicyId(csr_token, "755d5bfc-8a23-498a-9fee-7178c8b6eda6", 200, null, test);
		policy4SPOGServer.setToken(user_token);
		Response response=policy4SPOGServer.createPolicy(policy_name, "", policy_type, null, "true",  null, destinations, schedules, throttles, policy_id, this.direct_org_id1, test);
		response=policy4SPOGServer.assignPolicy(policy_id, this.direct_source_id1);
		response.then().statusCode(SpogConstants.SUCCESS_POST);
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
		 
		  Response res=policy4SPOGServer.getSourcesUnderOrgByPolicyType(this.direct_org_id1, policy_type, test);
		  policy4SPOGServer.checkSourcesUnderOrgByPolicyType(res, this.direct_org_id1, this.direct_org_name1, policy_name, policy_id, this.direct_source_id1, this.direct_source_name1, test);
		  source4spogServer.setToken(spogServer.getJWTToken());
//		  String cancel_backup_job_id=source4spogServer.cancelCDBackupJobAndWaitForRecoveryPoint(this.direct_source_id1, test);
//		  System.out.println("cancel backup job id is:"+cancel_backup_job_id);
//		  backup_job_id=source4spogServer.submitCDBackupJobAndWaitForRecoveryPoint(this.direct_source_id1, test);
//		  System.out.println("backup job id is:"+cancel_backup_job_id);
//		  //check log for backup job
//		  Response resLog=GetLogs("job_type;in;backup_full|backup_incremental","log_ts;desc");
//		  findMessageFromLog(resLog,backup_job_id,"successfully");
//	      
//	      
//	      String randomA=RandomStringUtils.randomAlphanumeric(20);
//		  String cancle_restore_job_id=source4spogServer.submitCDRestoreJob(this.direct_source_id1, this.direct_source_id1, "", "c:\\tmp"+randomA, null, true, false, test);
//		  System.out.println("cancel restore job id is:"+cancle_restore_job_id);
//		  source4spogServer.cancelCDRestoreJob(this.direct_source_id1, test);
//		  String restore_job_id=source4spogServer.submitCDRestoreJob(this.direct_source_id1, this.direct_source_id1, "", "c:\\tmp"+randomA, null, true, true, test);
//		  System.out.println("restore job id is:"+restore_job_id);
//		  System.out.println("restore job location is:"+randomA);
//		  //check log for restore job
//		  Response resLog1=GetLogs("job_type;in;restore","log_ts;desc");
//		  findMessageFromLog(resLog1,restore_job_id,"successfully");
		  //modify policy
		  policy_name=policy_name+"_modified";
		  response=policy4SPOGServer.updatePolicy(policy_name, "modified", policy_type, null, "true",  this.direct_source_id1, destinations, schedules, throttles, policy_id, this.direct_org_id1, user_token, test);
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
		  policy4SPOGServer.removeSourceFromPolicy(this.direct_source_id1, test);
		  //policy4SPOGServer.deletePolicybyPolicyId(user_token, policy_id, 200, null, test);
		  //delete the direct admin
		  spogServer.userLogin(this.csrAdmin, this.csrPwd);
		  System.out.println("direct admin is:"+direct_admin);
		  //delete account user
		  spogServer.CheckDeleteUserByIdStatus(direct_user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  
		  
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
		return response;
	}
	
	public void findMessageFromLog(Response res,String job_id,String log_message){
		boolean ret=false;
		int totalsize=res.then().extract().path("pagination.total_size");
		if(totalsize>=1){
			for(int i=0;i<totalsize;i++){
				if(res.then().extract().path("data["+i+"].job_data.job_id").toString().equalsIgnoreCase(job_id)&&(res.then().extract().path("data["+i+"].message").toString().indexOf(log_message)!=-1)){
					ret=true;
					break;
				}
			}			
		}  
		assertTrue(ret,"check log has the given message failed");
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
	  
	  @AfterTest
	  public void aftertest() {
		  test.log(LogStatus.INFO, "The total test cases passed are "+count1.getpassedcount());
		  test.log(LogStatus.INFO, "the total test cases failed are "+count1.getfailedcount());
		  //spogDestinationServer.recycleCloudVolumesByOrgName(direct_cloud_org_name,test);
		  rep.flush();
		  
	  }
	  
	  @AfterClass
	  public void updatebd() {
		try {
			if(count1.getfailedcount()>0) {
				Nooftest=count1.getpassedcount()+count1.getfailedcount()+count1.getskippedcount();
				bqdb1.updateTable(this.BQName, this.runningMachine, this.buildVersion, String.valueOf(Nooftest), Integer.toString(count1.getpassedcount()), Integer.toString(count1.getfailedcount()), String.valueOf(count1.getskippedcount()), count1.getcreationtime(), "Failed");
			}else {
				Nooftest=count1.getpassedcount()+count1.getfailedcount()+count1.getskippedcount();
				bqdb1.updateTable(this.BQName, this.runningMachine, this.buildVersion, String.valueOf(Nooftest), Integer.toString(count1.getpassedcount()), Integer.toString(count1.getfailedcount()), String.valueOf(count1.getskippedcount()), count1.getcreationtime(), "Passed");
			}
		}catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }
}
