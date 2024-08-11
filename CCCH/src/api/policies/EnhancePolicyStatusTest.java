package api.policies;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.ConnectionStatus;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import InvokerServer.GatewayServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.GatewayServer.siteType;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class EnhancePolicyStatusTest extends base.prepare.PrepareOrgInfo{
	@Parameters({ "pmfKey"})
	public EnhancePolicyStatusTest(String pmfKey) {
		super(pmfKey);
		// TODO Auto-generated constructor stub
	}
	private SPOGDestinationServer spogDestinationServer;
	  private SPOGServer spogServer;
	  private GatewayServer gatewayServer;
	  private Policy4SPOGServer policy4SPOGServer;
	  
	  private String csrAdmin;
	  private String csrPwd;
	  private String existing_schedule_id="54d2f50b-abaa-409f-a216-9a3444e561f8";
	  private String existing_task_id="715039a2-5b3b-4dee-9297-cc34b58fd870";
	  private String existing_throttle_id="b1610ab0-5311-4ed7-b41b-460c1ab3aa6b";
	  private String existing_policy_id="7d7c64a2-a70a-43fa-9468-126bba813ede";
	  
	  
	  //this is creating msp org or direct org for preparation
	  
	  private String OrgPwdForPrepare="Caworld2017";
	  private String sub_msp_pwd="Welcome*02";
	  private String csrOrgId,csrOrgEmailForPrepare;
	  
	  private ExtentTest test;
	  //this is for update portal, each testng class is taken as BQ set
//	  private SQLServerDb bqdb1;
//	  public int Nooftest;
//	  private long creationTime;
//	  private String BQName=null;
//	  private String runningMachine;
//	  private testcasescount count1;
//	  private String buildVersion;
//	  private ExtentReports rep;
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
	  
	  private String real_cd_source_id="e0f833be-d4af-4670-95c4-b79369fb58d2";
	  
	  
	  private String source_name_1="udp_Jing.Shan_PthxuumX";
	  private String source_id_1="ffd0cb04-47bd-4507-9594-d02aebf22139";
	  private String destination_id_1="1bf5aa1f-6802-4ad5-9825-d6356314dc0c";
	  private String destination_normal_id="135e29e6-6a05-4e0a-8e18-5294fe7c0a76";
	  //private String destination_normal_id="6a89f898-381b-4e4e-92bc-eb7fddb077b2";
	  private String job_id_1="cd6ff192-805c-43b5-b2ec-8ca3bc61e05f";
	  private String log_id_1="4ddb2da0-3c6e-446d-aec9-bde02f1a3d7a";
	  private String account_user_id,siteName,sitetype,account_site_id,csr_token,udp_source_id,udp_source_id1,destination_store_id,destination_store_id1;
	  private String destination_name="shaji02-test",destination_volume_id,recycle_destination_volume_id;
	  
	  private String cloud_direct_policy_id,replicate_policy_id;
	  private String one_cloud_direct_policy_id,one_replicate_policy_id;
	  private String two_cloud_direct_policy_id,two_replicate_policy_id;
	  //end 
	  @BeforeClass
	  @Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword","logFolder","runningMachine","buildVersion"})
	  public void beforeClass(String baseURI, String port, String csrAdminUserName, String csrAdminPassword, String logFolder, String runningMachine, String buildVersion) {
		//this is for update portal
		  this.BQName = this.getClass().getSimpleName();
		  String author = "Jing.Shan";
		  this.runningMachine = runningMachine;
		  this.csrOrgEmailForPrepare=csrAdminUserName;
		  SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		  java.util.Date date=new java.util.Date();
		  this.buildVersion=buildVersion+"_"+dateFormater.format(date);
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
		  String schedule_id=spogServer.returnRandomUUID();
			String task_id=spogServer.returnRandomUUID();
			String task_type="cloud_direct_file_folder_backup";
			String throttle_id=spogServer.returnRandomUUID();
			String policy_name=spogServer.ReturnRandom("test");
			String policy_description=spogServer.ReturnRandom("description");
			String policy_type="cloud_direct_baas";
			one_cloud_direct_policy_id=spogServer.returnRandomUUID();
		  spogServer.userLogin(this.csrAdmin, this.csrPwd);
		  csr_token=spogServer.getJWTToken();
		  
        String pmfKey="shaji02";
		  String pmfKey_prefix=spogServer.ReturnRandom(pmfKey);
		 
		  spogServer.userLogin(direct_admin_email1, direct_password);
		//create baas policy
			HashMap<String, Object> cloudDirectScheduleDTO=policy4SPOGServer.createCloudDirectScheduleDTO("0 6 * * *", test);
			HashMap<String, Object> scheduleSettingDTO=policy4SPOGServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null,test);
			ArrayList<HashMap<String,Object>> schedules =policy4SPOGServer.createPolicyScheduleDTO(null,schedule_id, 
					  "1d", task_id, this.direct_normal_id1, scheduleSettingDTO, "06:00", "12:00",task_type ,"", test);
			ArrayList<HashMap<String,Object>> excludes=policy4SPOGServer.createExcludeInfoDTO(null, "path", "c:\\api_policy_source1", test);
			//HashMap<String, Object> cloudDirectLocalBackupDTO=policy4SPOGServer.createCloudDirectLocalBackupDTO("E:\\tmp1", "true", test);
			HashMap<String, Object> cloudDirectFileBackupTaskInfoDTO=policy4SPOGServer.createCloudDirectFileBackupTaskInfoDTO("c:\\api_policy_source1\\tmp", null, excludes, test);
			ArrayList<HashMap<String,Object>>  destinations= policy4SPOGServer.createPolicyTaskDTO(null, task_id, task_type, this.direct_normal_id1, "none", null, cloudDirectFileBackupTaskInfoDTO, null, test);
			ArrayList<HashMap<String,Object>> throttles =policy4SPOGServer.createPolicyThrottleDTO(null, throttle_id, task_id, "network", "1200", "1", "06:00", "18:00",task_type , this.direct_normal_id1,"",test);
			String user_token=spogServer.getJWTToken();
			
			//policy4SPOGServer.deletePolicybyPolicyId(csr_token, "755d5bfc-8a23-498a-9fee-7178c8b6eda6", 200, null, test);
			policy4SPOGServer.setToken(user_token);
			Response response=policy4SPOGServer.createPolicy(policy_name, "", policy_type, null, "true",  null, destinations, schedules, throttles, one_cloud_direct_policy_id, this.direct_org_id1, test);
			prepare(baseURI, port, logFolder, csrAdminUserName, csrAdminPassword, this.getClass().getSimpleName() );
	  }
	  
	  @DataProvider(name = "loginUserToCreateCloudPolicy")
	  public final Object[][] getLoginUserToCreateCloudPolicy() {
			return new Object[][] { 
				{"direct"},{"csr"}};
	  }
	  @Test(dataProvider = "loginUserToCreateCloudPolicy")
	  public void updatePolicyWhenDeploying(String loginUserType){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  if (loginUserType.equalsIgnoreCase("csr")){
			  spogServer.userLogin(this.direct_admin_email1, direct_password);
		  }else if(loginUserType.equalsIgnoreCase("direct")){
			  spogServer.userLogin(this.direct_admin_email1, direct_password);
		  }
		    String schedule_id=spogServer.returnRandomUUID();
			String task_id=spogServer.returnRandomUUID();
			String task_type="cloud_direct_file_folder_backup";
			String throttle_id=spogServer.returnRandomUUID();
			String policy_name=spogServer.ReturnRandom("test");
			String policy_description=spogServer.ReturnRandom("description");
			String policy_type="cloud_direct_baas";
			one_cloud_direct_policy_id=spogServer.returnRandomUUID();
		  
		    //create baas policy
			HashMap<String, Object> cloudDirectScheduleDTO=policy4SPOGServer.createCloudDirectScheduleDTO("0 6 * * *", test);
			HashMap<String, Object> scheduleSettingDTO=policy4SPOGServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null,test);
			ArrayList<HashMap<String,Object>> schedules =policy4SPOGServer.createPolicyScheduleDTO(null,schedule_id, 
					  "1d", task_id, this.direct_normal_id1, scheduleSettingDTO, "06:00", "12:00",task_type ,"", test);
			ArrayList<HashMap<String,Object>> excludes=policy4SPOGServer.createExcludeInfoDTO(null, "path", "c:\\api_policy_source1\\tmp", test);
			//HashMap<String, Object> cloudDirectLocalBackupDTO=policy4SPOGServer.createCloudDirectLocalBackupDTO("E:\\tmp1", "true", test);
			HashMap<String, Object> cloudDirectFileBackupTaskInfoDTO=policy4SPOGServer.createCloudDirectFileBackupTaskInfoDTO("c:\\api_policy_source1", null, excludes, test);
			ArrayList<HashMap<String,Object>>  destinations= policy4SPOGServer.createPolicyTaskDTO(null, task_id, task_type, this.direct_normal_id1, "none", null, cloudDirectFileBackupTaskInfoDTO, null, test);
			ArrayList<HashMap<String,Object>> throttles =policy4SPOGServer.createPolicyThrottleDTO(null, throttle_id, task_id, "network", "1200", "1", "06:00", "18:00",task_type , this.direct_normal_id1,"",test);
			String user_token=spogServer.getJWTToken();
			
			//policy4SPOGServer.deletePolicybyPolicyId(csr_token, "755d5bfc-8a23-498a-9fee-7178c8b6eda6", 200, null, test);
			policy4SPOGServer.setToken(user_token);
			Response response=policy4SPOGServer.createPolicy(policy_name, "", policy_type, null, "true",  this.direct_source_id1, destinations, schedules, throttles, one_cloud_direct_policy_id, this.direct_org_id1, test);
			policy4SPOGServer.checkPolicyDestinations(response,SpogConstants.SUCCESS_POST,destinations,test);
		    try {
				Thread.sleep(5000);
			
			  } catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			  }
			policy4SPOGServer.deletePolicybyPolicyId(user_token, one_cloud_direct_policy_id, 200, null, test);
			
	  }	 
	  
	  @DataProvider(name = "loginUserToCreatePolicy4Permission")
	  public final Object[][] getLoginUserToCreatePolicy4Permission() {
			return new Object[][] { 
				{"submspaccount","none","network","1200", "1", "06:00", "18:00",403,"00100101","The current user does not have permissions to manage the resource"},
				{"submspaccount","random","network","1200", "2", "07:00", "23:00",403,"00100101","The current user does not have permissions to manage the resource."},
				{"submspaccountadmin","none","network","1200", "1", "06:00", "18:00",400,"00100025","MSP cannot operate the API"},
				{"submspaccountadmin","random","network","1200", "2", "07:00", "23:00",400,"00100025","MSP cannot operate the API"},
				{"submsp","none","network","1200", "1", "06:00", "18:00",403,"00100101","The current user does not have permissions to manage the resource"},
				{"submsp","random","network","1200", "2", "07:00", "23:00",403,"00100101","The current user does not have permissions to manage the resource."},
				{"rootmspaccount","none","network","1200", "1", "06:00", "18:00",403,"00100101","The current user does not have permissions to manage the resource"},
				{"rootmspaccount","random","network","1200", "2", "07:00", "23:00",403,"00100101","The current user does not have permissions to manage the resource."},
				{"rootmspaccountadmin","none","network","1200", "1", "06:00", "18:00",400,"00100025","MSP cannot operate the API"},
				{"rootmspaccountadmin","random","network","1200", "2", "07:00", "23:00",400,"00100025","MSP cannot operate the API"},
				{"rootmsp","none","network","1200", "1", "06:00", "18:00",403,"00100101","The current user does not have permissions to manage the resource"},
				{"rootmsp","random","network","1200", "2", "07:00", "23:00",403,"00100101","The current user does not have permissions to manage the resource."},
				
			};
	  }
	  @Test(dataProvider = "loginUserToCreatePolicy4Permission")
	  public void createPolicy4Permission(String loginUserType,String throttle_id,String throttle_type,
			  String rate,String days_of_week,String start_time,String end_time,int statusCode,String errorCode,String message ){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  String policy_id=spogServer.returnRandomUUID();
		  String schedule_id=spogServer.returnRandomUUID();
		  String task_id=spogServer.returnRandomUUID();
		  if(throttle_id.equalsIgnoreCase("random")){
			  throttle_id=spogServer.returnRandomUUID();
		  }
		  String task_type="cloud_direct_file_folder_backup";
		  String policy_name=spogServer.ReturnRandom("test");
		  String policy_description=spogServer.ReturnRandom("description");
		  String policy_type="cloud_direct_baas";
		  if (loginUserType.equalsIgnoreCase("csr")){
			  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
		  }else if(loginUserType.equalsIgnoreCase("submspaccount")) {
			  spogServer.userLogin(this.final_sub_msp2_account1_user_email, sub_msp_pwd,test);
			   
		  }else if(loginUserType.equalsIgnoreCase("submspaccountadmin")) {
			  spogServer.userLogin(this.final_sub_msp1_msp_account_user_name_email, sub_msp_pwd,test);
			  
		  }else if(loginUserType.equalsIgnoreCase("submsp")) {
			  spogServer.userLogin(this.final_sub_msp2_user_name_email, sub_msp_pwd,test);
			  
		  }else if(loginUserType.equalsIgnoreCase("rootmspaccount")) {
			  spogServer.userLogin(this.final_root_msp_direct_org_user_email, sub_msp_pwd,test);
			  
		  }else if(loginUserType.equalsIgnoreCase("rootmspaccountadmin")) {
			  spogServer.userLogin(this.final_root_msp_account_admin_user_name_email, sub_msp_pwd,test);
			  
		  }else if(loginUserType.equalsIgnoreCase("rootmsp")) {
			  spogServer.userLogin(this.final_root_msp_user_name_email, sub_msp_pwd,test);
			  
		  }
		  String organization_id=spogServer.GetLoggedinUserOrganizationID();
		  if (loginUserType.equalsIgnoreCase("csr")){
			  organization_id= this.direct_org_id1;
		  }
		  HashMap<String, Object> cloudDirectScheduleDTO=policy4SPOGServer.createCloudDirectScheduleDTO("0 6 * * *", test);
		  HashMap<String, Object> scheduleSettingDTO=policy4SPOGServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null,test);
		  ArrayList<HashMap<String,Object>> schedules =policy4SPOGServer.createPolicyScheduleDTO(null,schedule_id, 
				  "1d", task_id, this.direct_normal_id1, scheduleSettingDTO, "06:00", "12:00",task_type ,"test", test);
		  ArrayList<HashMap<String,Object>> excludes=policy4SPOGServer.createExcludeInfoDTO(null, "path", "c:\\api_policy_source\\tmp", test);
			
			//HashMap<String, Object> cloudDirectLocalBackupDTO=policy4SPOGServer.createCloudDirectLocalBackupDTO("E:\\tmp1", "true", test);
		  HashMap<String, Object> cloudDirectFileBackupTaskInfoDTO=policy4SPOGServer.createCloudDirectFileBackupTaskInfoDTO("c:\\api_policy_source", null, excludes, test);
			
		  ArrayList<HashMap<String,Object>>  destinations= policy4SPOGServer.createPolicyTaskDTO(null, task_id, task_type, this.direct_normal_id1, "none", null, cloudDirectFileBackupTaskInfoDTO, null, test);
		  
		  ArrayList<HashMap<String,Object>> throttles =policy4SPOGServer.createPolicyThrottleDTO(null, throttle_id, task_id, throttle_type, rate, days_of_week, start_time, end_time,task_type ,this.direct_normal_id1,"test",test);
		  String user_token=spogServer.getJWTToken();
		  policy4SPOGServer.setToken(user_token);
		  Response response=policy4SPOGServer.createPolicy(policy_name, policy_description, policy_type, null, "true",  this.direct_source_id1, destinations, schedules, throttles, policy_id, organization_id, test);
		  policy4SPOGServer.checkPolicyWithErrorInfo(response,statusCode,errorCode,message,test);		
	  }
	  
	  @DataProvider(name = "loginUserToCreateCloudPolicyWithoutSource")
	  public final Object[][] getLoginUserToCreateCloudPolicyWithoutSource() {
			return new Object[][] { 
				{"csr"},{"direct"}};
	  }
	  @Test(dataProvider = "loginUserToCreateCloudPolicyWithoutSource")
	  public void getPolicyStatusWithoutSource(String loginUserType){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  if (loginUserType.equalsIgnoreCase("csr")){
			  spogServer.userLogin(this.direct_admin_email1, direct_password);
		  }else if(loginUserType.equalsIgnoreCase("direct")){
			  spogServer.userLogin(this.direct_admin_email1, direct_password);
		  }
		  String schedule_id=spogServer.returnRandomUUID();
			String task_id=spogServer.returnRandomUUID();
			String task_type="cloud_direct_file_folder_backup";
			String throttle_id=spogServer.returnRandomUUID();
			String policy_name=spogServer.ReturnRandom("test");
			String policy_description=spogServer.ReturnRandom("description");
			String policy_type="cloud_direct_baas";
			one_cloud_direct_policy_id=spogServer.returnRandomUUID();
		  
		    //create baas policy
			HashMap<String, Object> cloudDirectScheduleDTO=policy4SPOGServer.createCloudDirectScheduleDTO("0 6 * * *", test);
			HashMap<String, Object> scheduleSettingDTO=policy4SPOGServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null,test);
			ArrayList<HashMap<String,Object>> schedules =policy4SPOGServer.createPolicyScheduleDTO(null,schedule_id, 
					  "1d", task_id, this.direct_normal_id1, scheduleSettingDTO, "06:00", "12:00",task_type ,"", test);
			ArrayList<HashMap<String,Object>> excludes=policy4SPOGServer.createExcludeInfoDTO(null, "path", "c:\\api_policy_source\\tmp", test);
			
			//HashMap<String, Object> cloudDirectLocalBackupDTO=policy4SPOGServer.createCloudDirectLocalBackupDTO("E:\\tmp1", "true", test);
			HashMap<String, Object> cloudDirectFileBackupTaskInfoDTO=policy4SPOGServer.createCloudDirectFileBackupTaskInfoDTO("c:\\api_policy_source", null, excludes, test);
			
			ArrayList<HashMap<String,Object>>  destinations= policy4SPOGServer.createPolicyTaskDTO(null, task_id, task_type, this.direct_normal_id1, "none", null, cloudDirectFileBackupTaskInfoDTO, null, test);
			ArrayList<HashMap<String,Object>> throttles =policy4SPOGServer.createPolicyThrottleDTO(null, throttle_id, task_id, "network", "1200", "1", "06:00", "18:00",task_type , this.direct_normal_id1,"",test);
			String user_token=spogServer.getJWTToken();
			
			//policy4SPOGServer.deletePolicybyPolicyId(csr_token, "755d5bfc-8a23-498a-9fee-7178c8b6eda6", 200, null, test);
			policy4SPOGServer.setToken(user_token);
			Response response=policy4SPOGServer.createPolicy(policy_name, "", policy_type, null, "true",  this.direct_source_id1, destinations, schedules, throttles, one_cloud_direct_policy_id, this.direct_org_id1, test);
			policy4SPOGServer.checkPolicyDestinations(response,SpogConstants.SUCCESS_POST,destinations,test);
		  //spogDestinationServer.RecycleDirectVolume(destination_volume_id, test);
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
