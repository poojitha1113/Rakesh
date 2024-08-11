package api.policies;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.codehaus.groovy.classgen.genArrayAccess;
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
import Constants.ErrorCode;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.GatewayServer;
import InvokerServer.GatewayServer.siteType;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class CreatePolicy4ReplicateTest extends base.prepare.PrepareOrgInfo{
	@Parameters({ "pmfKey"})
	public CreatePolicy4ReplicateTest(String pmfKey) {
		super(pmfKey);
		// TODO Auto-generated constructor stub
	}
	private SPOGDestinationServer spogDestinationServer;
	  private SPOGServer spogServer;
	  private GatewayServer gatewayServer;
	  private Policy4SPOGServer policy4SPOGServer;
	  private String directOrgId,directOrgId1;
	  private String mspOrgId;
	  private String mspOrgId1;
	  private String accountOrgId,accountOrgId1;
	  private String csrAdmin;
	  private String csrPwd;
	  
	  //this is creating msp org or direct org for preparation
	  private String mspOrgNameForPrepare="d_jing_spogqa_msp_org_prepare";
	  private String csrOrgNameForPrepare="d_jing_spogqa_csr_org_prepare";
	  private String csrOrgEmailForPrepare,mspOrgEmailForPrepare,mspOrgEmailForPrepare1,accountEmailForPrepare,accountEmailForPrepare1;
	  private String directOrgNameForPrepare="d_jing_spogqa_direct_org_prepare";
	  private String directOrgEmailForPrepare,directOrgEmailForPrepare1;
	  private String OrgFistNameForPrepare="jing";
	  private String OrgLastNameForPrepare="org_prepare";
	  private String OrgPwdForPrepare="welcomeA02";
	  private String csrOrgId;
	  
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
	  private String remote_plan_global_uuid="a3bab4d9-d122-49fe-ab4d-574476d9afee";
      private String remote_plan_uuid= "a3bab4d9-d122-49fe-ab4d-574476d9afee";
      private String remote_plan_name= "shaji02-plan";
      private String remote_console_name="10.57.48.83";
      private String remote_plan_rpspolicy_uuid= "b7c3d23b-1a85-49e8-860f-c8f566481f95";
      private String remote_plan_rpspolicy_name= "shared_plan_replicate";
      private String user_name= "administrator";
      private String password="cnbjrdqa1!";
      private String port= "8015";
      private String protocol="https";
	  
	  
	  private String direct_org_id="1ab97067-24f2-452e-b816-b42781cc2c09";
	  private String direct_admin_email="jing_direct_ui@arcserve.com";
	  private String direct_admin_password="Caworld_2017";
	  private String direct_admin_user_id="97f4972b-53f5-4065-98e6-9b9cf6823b54";	  
	  private String direct_hybrid_store_name="shaji02-2016-11_replicate";
	  private String direct_hybrid_store_id="1e5cd683-80b9-4674-ac89-1cb0887d16f4";
	  private String direct_org_name="jing_spogqa_direct_org_ui_1";
	  
	  
	  
	  
	  private String account_org_id="146bce60-21c0-4352-9638-0e17cb728cc0";
	  
	  private String account_admin_email="jing_account_ui_1@arcserve.com";
	  private String account_admin_password="Caworld_2017";
	  private String account_admin_user_id="b364ea12-0234-473a-9423-e378e0805367";	  
	  private String account_hybrid_store_name="jing-hy-suborg";
	  private String account_hybrid_store_id="eb1e7640-6aa0-40e8-bd1f-a26ea961dfe6";
	  private String account_org_name="cc_rw_yuefen";
	  
	  private String msp_org_id="5e52e8f6-891c-41fa-8bd0-df2ca7263eb1";
	  private String msp_admin_email="jing_msp_ui@arcserve.com";
	  private String msp_admin_password="Caworld_2017";
	  private String msp_admin_user_id="57287088-8b40-4c73-bf67-b4fcf3d81287";	  
	  private String msp_org_name="Arcserve SPOG QA";
	  
	  
	  
	  //private String destination_normal_id="6a89f898-381b-4e4e-92bc-eb7fddb077b2";
	  private String job_id_1="cd6ff192-805c-43b5-b2ec-8ca3bc61e05f";
	  private String log_id_1="4ddb2da0-3c6e-446d-aec9-bde02f1a3d7a";
	  private String account_user_id,siteName,sitetype,account_site_id,csr_token,udp_source_id,udp_source_id1,destination_store_id,destination_store_id1;
	  private String destination_name="shaji02-test",destination_volume_id,recycle_destination_volume_id;
	  private String existing_schedule_id,existing_task_id, existing_throttle_id,existing_policy_id;
	  //end 
	  @BeforeClass
	  @Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword","logFolder","runningMachine","buildVersion"})
	  public void beforeClass(String baseURI, String port, String csrAdminUserName, String csrAdminPassword, String logFolder, String runningMachine, String buildVersion) {
		//this is for update portal
		  this.BQName = this.getClass().getSimpleName();
		  String author = "Jing.Shan";
		  this.runningMachine = runningMachine;
		  this.csrOrgEmailForPrepare=csrAdminUserName;
		  this.OrgPwdForPrepare=csrAdminPassword;
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
		  
		  rep = ExtentManager.getInstance("CreateAccountTest",logFolder);
		  test = rep.startTest("beforeClass");
		  this.csrAdmin = csrAdminUserName;
		  this.csrPwd = csrAdminPassword;
		  prepare(baseURI, port, logFolder, csrAdminUserName, csrAdminPassword, this.getClass().getSimpleName() );
		  
	  }
	  
	  public boolean isNullOrEmpty(String checkValue){
		  if(checkValue==null || checkValue==""){
			  return true;
		  }else{
			  return false;
		  }
		  
	  }
	  
	  public void waitPolicyStatus(String user_token,String policy_id){
		  Response response =policy4SPOGServer.getPolicyById(user_token, policy_id, test);
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
		  
	  }
	  
	  @DataProvider(name = "permissionTest")
	  public final Object[][] permissionTest() {
			return new Object[][] {
									{"msp"},{"account"},
									{"submspaccount"},{"submspaccountadmin"},{"submsp"},
									{"rootmspaccount"},{"rootmspaccountadmin"},{"rootmsp"},
									
      };
	  }
	  @Test(dataProvider = "permissionTest")
	  public void permissionTest(String usertype){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  String errorCode="00100101";
		  String spogToken;
		  if(usertype.equalsIgnoreCase("msp")) {
			  spogServer.userLogin(this.msp_admin_email, msp_admin_password,test);
			  spogToken=spogServer.getJWTToken();
			  
			  
		  }else if(usertype.equalsIgnoreCase("account")) {
			  spogServer.userLogin(this.account_admin_email, account_admin_password,test);
			  spogToken=spogServer.getJWTToken();
			  
			  
		  }else if(usertype.equalsIgnoreCase("submspaccount")) {
			  spogServer.userLogin(this.final_sub_msp2_account1_user_email, OrgPwdForPrepare,test);
			  spogToken=spogServer.getJWTToken();
			  
		  }else if(usertype.equalsIgnoreCase("submspaccountadmin")) {
			  spogServer.userLogin(this.final_sub_msp1_msp_account_user_name_email, OrgPwdForPrepare,test);
			  spogToken=spogServer.getJWTToken();
			  
		  }else if(usertype.equalsIgnoreCase("submsp")) {
			  spogServer.userLogin(this.final_sub_msp2_user_name_email, OrgPwdForPrepare,test);
			  spogToken=spogServer.getJWTToken();
			  
		  }else if(usertype.equalsIgnoreCase("rootmspaccount")) {
			  spogServer.userLogin(this.final_root_msp_direct_org_user_email, OrgPwdForPrepare,test);
			  spogToken=spogServer.getJWTToken();
			  
		  }else if(usertype.equalsIgnoreCase("rootmspaccountadmin")) {
			  spogServer.userLogin(this.final_root_msp_account_admin_user_name_email, OrgPwdForPrepare,test);
			  spogToken=spogServer.getJWTToken();
			  
		  }else if(usertype.equalsIgnoreCase("rootmsp")) {
			  spogServer.userLogin(this.final_root_msp_user_name_email, OrgPwdForPrepare,test);
			  spogToken=spogServer.getJWTToken();
			  
		  }
		  
		  String policy_id=spogServer.returnRandomUUID();
		  String schedule_id=spogServer.returnRandomUUID();
		  String task_id=spogServer.returnRandomUUID();
		  String replicateToTask_id=spogServer.returnRandomUUID();
		  String throttle_id=spogServer.returnRandomUUID();
		  String destination_store_id=null;
		  String organization_id=this.direct_org_id;;
		  String task_type="udp_replication_from_remote";
		  String to_task_type="udp_replication_to_remote";
		  String policy_type="cloud_hybrid_replication";
		  String policy_name=spogServer.ReturnRandom("test");
		  String policy_description=spogServer.ReturnRandom("description");
		  String destination_name="shanjing-cloud-hybridDS4";
		  HashMap<String, Object> CustomScheduleDTO=policy4SPOGServer.createCustomScheduleDTO("1524469596000", "full","1","true","10","minutes",test);
		  HashMap<String, Object> scheduleSettingDTO=policy4SPOGServer.createScheduleSettingDTO( null,CustomScheduleDTO,test);
		  ArrayList<HashMap<String,Object>> schedules =policy4SPOGServer.createPolicyScheduleDTO(null,spogServer.returnRandomUUID(), 
				  "custom", task_id, destination_store_id, scheduleSettingDTO, "06:00", "12:00", task_type ,destination_name,test);
		  HashMap<String, Object> performARTestOption=policy4SPOGServer.createPerformARTestOption("true","true","true","true", test);
		  //HashMap<String, Object> retention_policy=policy4SPOGServer.createRetentionPolicyOption("6","7","","", test);
		  HashMap<String, Object> udpReplicationFromRemoteInfoDTO=policy4SPOGServer.createUdpReplicationFromRemoteInfoDTO(performARTestOption, null,test);
		  HashMap<String, Object> udpReplicationToRemoteInfoDTO=policy4SPOGServer.createUdpReplicationToRemoteInfoDTO(remote_console_name, user_name ,password ,port ,protocol ,remote_plan_global_uuid ,remote_plan_uuid ,remote_plan_name ,remote_plan_rpspolicy_uuid,remote_plan_rpspolicy_name,"10","3",test);
		  String  ranm_de_id=spogServer.returnRandomUUID();
//		  ArrayList<HashMap<String,Object>>  destinations= policy4SPOGServer.createPolicyTaskDTO(null, task_id, task_type, destination_normal_id, "none", null, null, udpReplicationFromRemoteInfoDTO, test);
		  ArrayList<HashMap<String,Object>>  destinations= policy4SPOGServer.createPolicyTaskDTO(null, task_id, task_type, destination_store_id, "none", null, null, udpReplicationFromRemoteInfoDTO, test);
		  destinations= policy4SPOGServer.createPolicyTaskDTO(destinations, replicateToTask_id, to_task_type, destination_store_id, task_id, null, null, null,udpReplicationToRemoteInfoDTO, test);
		  //spogServer.userLogin(this.csrAdmin, this.csrPwd);
		  String user_token=spogServer.getJWTToken();
		  policy4SPOGServer.setToken(user_token);
		  ArrayList<HashMap<String,Object>> throttles =policy4SPOGServer.createPolicyThrottleDTO(null, throttle_id, replicateToTask_id, "network", "1200", "1", "06:00", "18:00",task_type ,destination_store_id,destination_name, test);
		  
		  policy4SPOGServer.setToken(user_token);
		  //Response response=policy4SPOGServer.createPolicy(policy_name, policy_description, policy_type, null, "true", "successful", source_id_1, destinations, schedules, throttle, policy_id, organization_id, test);
		  Response response=policy4SPOGServer.createPolicy(policy_name, policy_description, policy_type, null, true,  null, destinations, schedules, throttles, policy_id, organization_id, test);
		  policy4SPOGServer.checkPolicyWithErrorInfo(response,403,"00100101","The current user does not have permissions to manage the resource.",test);	
		  		  
	  }
	  
	  @DataProvider(name = "loginUserToCreatePolicy")
	  public final Object[][] loginUserToCreatePolicy() {
			return new Object[][] { 
				{"direct"},
				//{"account"},
				//{"msp"},
				{"csr"}};
	  }
	  //@Test(dataProvider = "loginUserToCreatePolicy")
	  public void createPolicy(String loginUserType ){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  String policy_id=spogServer.returnRandomUUID();
		  String schedule_id=spogServer.returnRandomUUID();
		  String task_id=spogServer.returnRandomUUID();
		  String replicateToTask_id=spogServer.returnRandomUUID();
		  String throttle_id=spogServer.returnRandomUUID();
		  String destination_store_id=null;
		  String organization_id=null;
		  String task_type="udp_replication_from_remote";
		  String to_task_type="udp_replication_to_remote";
		  String policy_type="cloud_hybrid_replication";
		  String policy_name=spogServer.ReturnRandom("test");
		  String policy_description=spogServer.ReturnRandom("description");
		  String destination_name="shanjing-cloud-hybridDS4";
		  if (loginUserType.equalsIgnoreCase("csr")){
			  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
			  organization_id=this.direct_org_id;
			  destination_store_id=this.direct_hybrid_store_id;
		  }else if(loginUserType.equalsIgnoreCase("direct")){
			  destination_name="shaji02-2016-11_replicate";
			  spogServer.userLogin(this.direct_admin_email, this.direct_admin_password);
			  organization_id=this.direct_org_id;
			  destination_store_id=this.direct_hybrid_store_id;
		  }else if(loginUserType.equalsIgnoreCase("msp")){
			  spogServer.userLogin(this.msp_admin_email, this.msp_admin_password,test);
			  organization_id=this.account_org_id;
			  destination_store_id=this.account_hybrid_store_id;
		  }else if(loginUserType.equalsIgnoreCase("account")){
			  spogServer.userLogin(this.account_admin_email, this.account_admin_password,test);
			  organization_id=this.account_org_id;
			  destination_store_id=this.account_hybrid_store_id;
		  }
		  HashMap<String, Object> CustomScheduleDTO=policy4SPOGServer.createCustomScheduleDTO("1524469596000", "full","1","true","10","minutes",test);
		  HashMap<String, Object> scheduleSettingDTO=policy4SPOGServer.createScheduleSettingDTO( null,CustomScheduleDTO,test);
		  ArrayList<HashMap<String,Object>> schedules =policy4SPOGServer.createPolicyScheduleDTO(null,spogServer.returnRandomUUID(), 
				  "custom", task_id, destination_store_id, scheduleSettingDTO, "06:00", "12:00", task_type ,destination_name,test);
		  HashMap<String, Object> performARTestOption=policy4SPOGServer.createPerformARTestOption("true","true","true","true", test);
		  //HashMap<String, Object> retention_policy=policy4SPOGServer.createRetentionPolicyOption("6","7","","", test);
		  HashMap<String, Object> udpReplicationFromRemoteInfoDTO=policy4SPOGServer.createUdpReplicationFromRemoteInfoDTO(performARTestOption, null,test);
		  HashMap<String, Object> udpReplicationToRemoteInfoDTO=policy4SPOGServer.createUdpReplicationToRemoteInfoDTO(remote_console_name, user_name ,password ,port ,protocol ,remote_plan_global_uuid ,remote_plan_uuid ,remote_plan_name ,remote_plan_rpspolicy_uuid,remote_plan_rpspolicy_name,"10","3",test);
		  String  ranm_de_id=spogServer.returnRandomUUID();
//		  ArrayList<HashMap<String,Object>>  destinations= policy4SPOGServer.createPolicyTaskDTO(null, task_id, task_type, destination_normal_id, "none", null, null, udpReplicationFromRemoteInfoDTO, test);
		  ArrayList<HashMap<String,Object>>  destinations= policy4SPOGServer.createPolicyTaskDTO(null, task_id, task_type, destination_store_id, "none", null, null, udpReplicationFromRemoteInfoDTO, test);
		  destinations= policy4SPOGServer.createPolicyTaskDTO(destinations, replicateToTask_id, to_task_type, destination_store_id, task_id, null, null, null,udpReplicationToRemoteInfoDTO, test);
		  //spogServer.userLogin(this.csrAdmin, this.csrPwd);
		  String user_token=spogServer.getJWTToken();
		  policy4SPOGServer.setToken(user_token);
		  ArrayList<HashMap<String,Object>> throttles =policy4SPOGServer.createPolicyThrottleDTO(null, throttle_id, replicateToTask_id, "network", "1200", "1", "06:00", "18:00",task_type ,destination_store_id,destination_name, test);
		  
		  policy4SPOGServer.setToken(user_token);
		  //Response response=policy4SPOGServer.createPolicy(policy_name, policy_description, policy_type, null, "true", "successful", source_id_1, destinations, schedules, throttle, policy_id, organization_id, test);
		  Response response=policy4SPOGServer.createPolicy(policy_name, policy_description, policy_type, null, true,  null, destinations, schedules, throttles, policy_id, organization_id, test);
		  //waitPolicyStatus(user_token, policy_id);
		  //policy4SPOGServer.checkPolicyThrottles(response,SpogConstants.SUCCESS_POST,throttles,test);
		  policy4SPOGServer.checkPolicySchedules(response,SpogConstants.SUCCESS_POST,schedules,test);
		  policy4SPOGServer.checkReplicatePolicyDestination(response,SpogConstants.SUCCESS_POST,destinations,performARTestOption,null,test);
		  policy4SPOGServer.checkPolicyCommon(response, SpogConstants.SUCCESS_POST, policy_name, policy_description, policy_type, null, "true", "success", null, policy_id, organization_id, test);
		  policy4SPOGServer.deletePolicybyPolicyId(user_token, policy_id, 200, null, test);
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
