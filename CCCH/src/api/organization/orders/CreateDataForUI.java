package api.organization.orders;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;

import Constants.ConnectionStatus;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.GatewayServer;
import InvokerServer.Source4SPOGServer;
import InvokerServer.UserSpogServer;
import InvokerServer.GatewayServer.siteType;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class CreateDataForUI {
	  private SPOGDestinationServer spogDestinationServer;
	  private SPOGDestinationServer userSpogServer;
	  private SPOGServer spogServer;
	  private Policy4SPOGServer policy4SPOGServer;
	  private Source4SPOGServer source4SpogServer;
	  private GatewayServer gatewayServer;
	  private ExtentReports rep;
	  private ExtentTest test;
	  private String csrAdmin;
	  private String csrPwd;
	  
	  @BeforeClass
	  @Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword","logFolder","runningMachine","buildVersion"})
	  public void beforeClass(String baseURI, String port, String csrAdminUserName, String csrAdminPassword, String logFolder, String runningMachine, String buildVersion) {
		
		  spogServer = new SPOGServer(baseURI, port);
		  spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		  UserSpogServer userSpogServer =new UserSpogServer(baseURI, port);
		  policy4SPOGServer= new Policy4SPOGServer(baseURI, port);
		  source4SpogServer= new Source4SPOGServer(baseURI, port);
		  gatewayServer = new GatewayServer(baseURI, port);
		  rep = ExtentManager.getInstance("CreateAccountTest",logFolder);
		  test = rep.startTest("beforeClass");
		  this.csrAdmin = csrAdminUserName;
		  this.csrPwd = csrAdminPassword;
		  spogServer.userLogin(this.csrAdmin, this.csrPwd);
		  
	  }
	  
//	  @Test 
//	  public void sample(){
//		 
//		 
//		  String pmfKey="liuyu05";
//		  
//		  String pmfKey_prefix=spogServer.ReturnRandom(pmfKey);
//		  String policy_id=spogServer.returnRandomUUID();
//		  String task_id=spogServer.returnRandomUUID();
//		  String csrAdmin="tmp_xiang.li@arcserve.com";
//		  String csrPwd= "Caworld_2017";
//		  String baseURI = "http://tspog.zetta.net";
//	      String port = "8080";
//		  SPOGServer spogServer = new SPOGServer(baseURI, port);
//		  SPOGDestinationServer spogDestinationServer = new SPOGDestinationServer(baseURI, port);
//		  String directOrgNameForPrepare="spogqa_direct_org_"+pmfKey_prefix;
//		  String directOrgEmailForPrepare1=pmfKey_prefix+"@arcserve.com";
//		  String orgPwdForPrepare= "Caworld_2017";
//		  ExtentTest test = new ExtentTest("testname", "description");
//		  spogServer.userLogin(csrAdmin, csrPwd);
//		  String user_token=spogServer.getJWTToken();
//		  //create cloud account and create destination
//		  String directOrgId1 = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("DO_NOT_DELETE_YUEFEN"), SpogConstants.DIRECT_ORG, directOrgEmailForPrepare1, orgPwdForPrepare, "dd", "dd");
//		  System.out.println("orgID:"+directOrgId1);
//		  spogServer.userLogin(directOrgEmailForPrepare1, orgPwdForPrepare);
//		  System.out.println("username:"+directOrgEmailForPrepare1);
//		  System.out.println("password:"+orgPwdForPrepare);
//		  String cloud_direct_account_id=spogServer.createCloudAccountWithCheck("cloudAccountKey", "cloudAccountSecret", pmfKey_prefix+"cloudAccountName", "cloud_direct", directOrgId1, 
//					"SKUTESTDATA_1_0_0_0_"+pmfKey_prefix, "SKUTESTDATA_1_0_0_0_"+pmfKey_prefix, "SKUTESTDATA_1_0_0_0_"+pmfKey_prefix, "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
//		  System.out.println("cloudID:"+cloud_direct_account_id);
//	      spogDestinationServer.setToken(user_token);
//	      String destination_id_ret1=spogDestinationServer.createDestinationWithCheck(  "",directOrgId1, cloud_direct_account_id,"91a9b48e-6ac6-4c47-8202-614b5cdcfe0c","cloud_direct_volume", "running", "1",
//						  cloud_direct_account_id, "normal", pmfKey_prefix, "2M", "2M", "0", "0", "31", "0", "2", "0", "4",
//							"1", "4", "1", "DO_NOT_DELETE_YUEFEN", test); 
//	      System.out.println("destinationId:"+destination_id_ret1);
//		  HashMap<String, Object> cloudDirectScheduleDTO=policy4SPOGServer.createCloudDirectScheduleDTO("0 0 * * *", test);
//		  HashMap<String, Object> scheduleSettingDTO=policy4SPOGServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null,test);
//		  ArrayList<HashMap<String,Object>> schedules =policy4SPOGServer.createPolicyScheduleDTO(null,spogServer.returnRandomUUID(), 
//				  "1d", task_id, destination_id_ret1, scheduleSettingDTO, "06:00", "12:00", "cloud_direct_file_folder_backup","DO_NOT_DELETE_YUEFEN",test);
//		  ArrayList<HashMap<String,Object>> excludes=policy4SPOGServer.createExcludeInfoDTO(null, "path", "c:\\tmp", test);
//		  HashMap<String, Object> cloudDirectLocalBackupDTO=policy4SPOGServer.createCloudDirectLocalBackupDTO("d:\\tmp", "true", excludes, test);
//		  HashMap<String, Object> cloudDirectFileBackupTaskInfoDTO=policy4SPOGServer.createCloudDirectFileBackupTaskInfoDTO("d:\\tmp", cloudDirectLocalBackupDTO, test);
//		  ArrayList<HashMap<String,Object>>  destinations= policy4SPOGServer.createPolicyTaskDTO(null, task_id, "cloud_direct_file_folder_backup", destination_id_ret1, "none", null, cloudDirectFileBackupTaskInfoDTO, null, test);
//		  ArrayList<HashMap<String,Object>> throttles =policy4SPOGServer.createPolicyThrottleDTO(null, spogServer.returnRandomUUID(), task_id, "network", "1200", "1", "06:00", "18:00", "cloud_direct_file_folder_backup",destination_id_ret1,"DO_NOT_DELETE_YUEFEN",test);
//		  Policy4SPOGServer policy4SPOGServer= new Policy4SPOGServer("http://tspog.zetta.net", "8080");
//		  
//		  policy4SPOGServer.setToken(user_token);
//		  String cloud_source_id = spogServer.createSourceWithCheck(pmfKey_prefix, SourceType.machine, SourceProduct.cloud_direct,
//				  directOrgId1, cloud_direct_account_id, ProtectionStatus.protect, ConnectionStatus.online, "windows", null,
//					test);
//		  Response response=policy4SPOGServer.createPolicy(spogServer.ReturnRandom(pmfKey), spogServer.ReturnRandom(pmfKey), "backup_recovery", null, "true",  cloud_source_id, destinations, schedules, throttles, policy_id, directOrgId1, test);
//		  policy4SPOGServer.checkPolicyDestinations(response,SpogConstants.SUCCESS_POST,destinations,test);
//	  }
	  
//	  @Test 
//	  public void sample1(){
//		 
//		 
//		  String pmfKey="liuyu05";
//		  
//		  String pmfKey_prefix=spogServer.ReturnRandom(pmfKey);
//		  String policy_id=spogServer.returnRandomUUID();
//		  String task_id=spogServer.returnRandomUUID();
//		  String csrAdmin="tmp_xiang.li@arcserve.com";
//		  String csrPwd= "Caworld_2017";
//		  String baseURI = "http://tspog.zetta.net";
//	      String port = "8080";
//		  SPOGServer spogServer = new SPOGServer(baseURI, port);
//		  SPOGDestinationServer spogDestinationServer = new SPOGDestinationServer(baseURI, port);
//		  String directOrgNameForPrepare="spogqa_direct_org_"+pmfKey_prefix;
//		  String directOrgEmailForPrepare1=pmfKey_prefix+"@arcserve.com";
//		  String orgPwdForPrepare= "Caworld_2017";
//		  ExtentTest test = new ExtentTest("testname", "description");
//		  spogServer.userLogin(csrAdmin, csrPwd);
//		  String user_token=spogServer.getJWTToken();
//		  //create cloud account and create destination
//		  String directOrgId1 = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("DO_NOT_DELETE_YUEFEN"), SpogConstants.DIRECT_ORG, directOrgEmailForPrepare1, orgPwdForPrepare, "dd", "dd");
//		  System.out.println("orgID:"+directOrgId1);
//		  spogServer.userLogin(directOrgEmailForPrepare1, orgPwdForPrepare);
//		  System.out.println("username:"+directOrgEmailForPrepare1);
//		  System.out.println("password:"+orgPwdForPrepare);
//		  String cloud_direct_account_id=spogServer.createCloudAccountWithCheck("cloudAccountKey", "cloudAccountSecret", pmfKey_prefix+"cloudAccountName", "cloud_direct", directOrgId1, 
//					"SKUTESTDATA_1_0_0_0_"+pmfKey_prefix, "SKUTESTDATA_1_0_0_0_"+pmfKey_prefix, "SKUTESTDATA_1_0_0_0_"+pmfKey_prefix, "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
//		  System.out.println("cloudID:"+cloud_direct_account_id);
//	      spogDestinationServer.setToken(user_token);
//	      String destination_id_ret1=spogDestinationServer.createDestinationWithCheck(  "",directOrgId1, cloud_direct_account_id,"91a9b48e-6ac6-4c47-8202-614b5cdcfe0c","cloud_direct_volume", "running", "1",
//						  cloud_direct_account_id, "normal", pmfKey_prefix, "2M", "2M", "0", "0", "31", "0", "2", "0", "4",
//							"1", "4", "1", "DO_NOT_DELETE_YUEFEN", test); 
//	      System.out.println("destinationId:"+destination_id_ret1);
//		  HashMap<String, Object> cloudDirectScheduleDTO=policy4SPOGServer.createCloudDirectScheduleDTO("0 0 * * *", test);
//		  HashMap<String, Object> scheduleSettingDTO=policy4SPOGServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null,test);
//		  ArrayList<HashMap<String,Object>> schedules =policy4SPOGServer.createPolicyScheduleDTO(null,spogServer.returnRandomUUID(), 
//				  "1d", task_id, destination_id_ret1, scheduleSettingDTO, "06:00", "12:00", "cloud_direct_file_folder_backup","DO_NOT_DELETE_YUEFEN",test);
//		  ArrayList<HashMap<String,Object>> excludes=policy4SPOGServer.createExcludeInfoDTO(null, "path", "c:\\tmp", test);
//		  HashMap<String, Object> cloudDirectLocalBackupDTO=policy4SPOGServer.createCloudDirectLocalBackupDTO("d:\\tmp", "true", excludes, test);
//		  HashMap<String, Object> cloudDirectFileBackupTaskInfoDTO=policy4SPOGServer.createCloudDirectFileBackupTaskInfoDTO("d:\\tmp", cloudDirectLocalBackupDTO, test);
//		  ArrayList<HashMap<String,Object>>  destinations= policy4SPOGServer.createPolicyTaskDTO(null, task_id, "cloud_direct_file_folder_backup", destination_id_ret1, "none", null, cloudDirectFileBackupTaskInfoDTO, null, test);
//		  ArrayList<HashMap<String,Object>> throttles =policy4SPOGServer.createPolicyThrottleDTO(null, spogServer.returnRandomUUID(), task_id, "network", "1200", "1", "06:00", "18:00", "cloud_direct_file_folder_backup",destination_id_ret1,"DO_NOT_DELETE_YUEFEN",test);
//		  Policy4SPOGServer policy4SPOGServer= new Policy4SPOGServer("http://tspog.zetta.net", "8080");
//		  
//		  policy4SPOGServer.setToken(user_token);
//		  String cloud_source_id = spogServer.createSourceWithCheck(pmfKey_prefix, SourceType.machine, SourceProduct.cloud_direct,
//				  directOrgId1, cloud_direct_account_id, ProtectionStatus.protect, ConnectionStatus.online, "windows", null,
//					test);
//		  Response response=policy4SPOGServer.createPolicy(spogServer.ReturnRandom(pmfKey), spogServer.ReturnRandom(pmfKey), "backup_recovery", null, "true",  cloud_source_id, destinations, null, null, policy_id, directOrgId1, test);
////		  policy4SPOGServer.checkPolicyDestinations(response,SpogConstants.SUCCESS_POST,destinations,test);
//	  }
	  
//	  @Test 
//	  public void CDAgentFileBackup(){
//		 
//		 
//		  String pmfKey="liuyu05";
//		  
//		  String pmfKey_prefix=spogServer.ReturnRandom(pmfKey);
//		  String policy_id=spogServer.returnRandomUUID();
//		  String task_id=spogServer.returnRandomUUID();
//		  String csrAdmin="tmp_xiang.li@arcserve.com";
//		  String csrPwd= "Caworld_2017";
//		  String baseURI = "http://tspog.zetta.net";
//	      String port = "8080";
//		  SPOGServer spogServer = new SPOGServer(baseURI, port);
//		  SPOGDestinationServer spogDestinationServer = new SPOGDestinationServer(baseURI, port);
//
//		  String orgPwdForPrepare= "Caworld_2017";
//		  ExtentTest test = new ExtentTest("testname", "description");
//		  spogServer.userLogin(csrAdmin, csrPwd);
//		  String user_token=spogServer.getJWTToken();
//		  //create cloud account and create destination
//		  String directOrgId1 = "9d0f9061-7847-4616-94f0-56882a1608cb";
//	
//		  spogServer.userLogin("liuyu05Gu6HH2LT@arcserve.com", "Caworld_2017");
//
//		  String cloud_direct_account_id= "7e7b7ee2-226c-43b7-b463-4082bff0d45e";
//	      spogDestinationServer.setToken(user_token);
//	      String destination_id_ret1= "673c2241-8a8f-40c1-8428-22695b7f7ad5";
//	     
//		  HashMap<String, Object> cloudDirectScheduleDTO=policy4SPOGServer.createCloudDirectScheduleDTO("0 0 * * *", test);
//		  HashMap<String, Object> scheduleSettingDTO=policy4SPOGServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null,test);
//		  ArrayList<HashMap<String,Object>> schedules =policy4SPOGServer.createPolicyScheduleDTO(null,spogServer.returnRandomUUID(), 
//				  "1d", task_id, destination_id_ret1, scheduleSettingDTO, "06:00", "12:00", "cloud_direct_file_folder_backup","DO_NOT_DELETE_YUEFEN",test);
//		  ArrayList<HashMap<String,Object>> excludes=policy4SPOGServer.createExcludeInfoDTO(null, "path", "c:\\tmp", test);
//		  HashMap<String, Object> cloudDirectLocalBackupDTO=policy4SPOGServer.createCloudDirectLocalBackupDTO("d:\\tmp", "true", excludes, test);
//		  HashMap<String, Object> cloudDirectFileBackupTaskInfoDTO=policy4SPOGServer.createCloudDirectFileBackupTaskInfoDTO("d:\\tmp", cloudDirectLocalBackupDTO, test);
//		  ArrayList<HashMap<String,Object>>  destinations= policy4SPOGServer.createPolicyTaskDTO(null, task_id, "cloud_direct_file_folder_backup", destination_id_ret1, "none", null, cloudDirectFileBackupTaskInfoDTO, null, test);
//		  ArrayList<HashMap<String,Object>> throttles =policy4SPOGServer.createPolicyThrottleDTO(null, spogServer.returnRandomUUID(), task_id, "network", "1200", "1", "06:00", "18:00", "cloud_direct_file_folder_backup",destination_id_ret1,"DO_NOT_DELETE_YUEFEN",test);
//		  Policy4SPOGServer policy4SPOGServer= new Policy4SPOGServer("http://tspog.zetta.net", "8080");
//		  
//		  policy4SPOGServer.setToken(user_token);
//		  String cloud_source_id = "4a644667-4868-4965-9ada-e9d1f64d0e42";
//		  Response response=policy4SPOGServer.createPolicy(spogServer.ReturnRandom("liuyu05-vm7009"), spogServer.ReturnRandom(pmfKey), "backup_recovery", null, "true",  cloud_source_id, destinations, schedules, null, policy_id, directOrgId1, test);
////		  policy4SPOGServer.checkPolicyDestinations(response,SpogConstants.SUCCESS_POST,destinations,test);
//	  }
	  
//	  @Test 
//	  public void StartBackupJob(){
//		  String csrAdmin="tmp_xiang.li@arcserve.com";
//		  String csrPwd= "Caworld_2017";
//		  String baseURI = "http://tspog.zetta.net";
//	      String port = "8080";
//		  SPOGServer spogServer = new SPOGServer(baseURI, port);
//		  SPOGDestinationServer spogDestinationServer = new SPOGDestinationServer(baseURI, port);
//
//		  ExtentTest test = new ExtentTest("testname", "description");
//		  spogServer.userLogin(csrAdmin, csrPwd);
//		  String user_token=spogServer.getJWTToken();
//		  
//		  spogServer.userLogin("liuyu05Gu6HH2LT@arcserve.com", "Caworld_2017");
//		  String token =spogServer.getJWTToken();
//		  
//		  source4SpogServer.submitBackupForSource("4a644667-4868-4965-9ada-e9d1f64d0e42", token);
//		  
//	  }
	  /*
	  @Test 
	  public void createDRPolicy(){
		 
		 
		  String pmfKey="liuyu05";
		  
		  String pmfKey_prefix=spogServer.ReturnRandom(pmfKey);
		  String policy_id=spogServer.returnRandomUUID();
		  String task_id=spogServer.returnRandomUUID();
		  String csrAdmin="tmp_xiang.li@arcserve.com";
		  String csrPwd= "Caworld_2017";
		  String baseURI = "http://tspog.zetta.net";
	      String port = "8080";
		  SPOGServer spogServer = new SPOGServer(baseURI, port);
		  SPOGDestinationServer spogDestinationServer = new SPOGDestinationServer(baseURI, port);

		  String orgPwdForPrepare= "Caworld_2017";
		  ExtentTest test = new ExtentTest("testname", "description");
		  spogServer.userLogin(csrAdmin, csrPwd);
		  String user_token=spogServer.getJWTToken();
		  //create cloud account and create destination, below is normal volume
		  String directOrgId1 = "9d0f9061-7847-4616-94f0-56882a1608cb";
		  
		  spogServer.userLogin("liuyu05Gu6HH2LT@arcserve.com", "Caworld_2017");

		  String cloud_direct_account_id= "7e7b7ee2-226c-43b7-b463-4082bff0d45e";
		  
	      spogDestinationServer.setToken(user_token);
	      //create destination, zero_copy
	      String destination_id_ret1= spogDestinationServer.createDestinationWithCheck("", directOrgId1, "7e7b7ee2-226c-43b7-b463-4082bff0d45e", "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", "cloud_direct_volume", "running", "1",
				   cloud_direct_account_id, "zero_copy", "liuyu05-DR", "2M", "2M", "0", "0", "31", "0", "2", "0", "4",
					"1", "4", "1", "liuyu05-DR-Destination", test);
	      
		  //String destination_id_ret1= "673c2241-8a8f-40c1-8428-22695b7f7ad5";
	     
		  HashMap<String, Object> cloudDirectScheduleDTO=policy4SPOGServer.createCloudDirectScheduleDTO("0 0 * * *", test);
		  HashMap<String, Object> scheduleSettingDTO=policy4SPOGServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null,test);
		  ArrayList<HashMap<String,Object>> schedules =policy4SPOGServer.createPolicyScheduleDTO(null,spogServer.returnRandomUUID(), 
				  "1d", task_id, destination_id_ret1, scheduleSettingDTO, "06:00", "12:00", "cloud_direct_image_backup","DO_NOT_DELETE_YUEFEN",test);
		  //ArrayList<HashMap<String,Object>> excludes=policy4SPOGServer.createExcludeInfoDTO(null, "path", "c:\\tmp", test);
		  HashMap<String, Object> cloudDirectImageBackupTaskInfoDTO=policy4SPOGServer.createCloudDirectImageBackupTaskInfoDTO("*", null, test);
		
		  ArrayList<HashMap<String,Object>>  destinations= policy4SPOGServer.createPolicyTaskDTO(null, task_id, "cloud_direct_image_backup",destination_id_ret1, "none", cloudDirectImageBackupTaskInfoDTO,null, null, test);
		  
		  Policy4SPOGServer policy4SPOGServer= new Policy4SPOGServer("http://tspog.zetta.net", "8080");
		  
		  policy4SPOGServer.setToken(user_token);
		  String cloud_source_id = "76a7f608-3ae4-4fc2-8831-06647946ace9";
		  policy4SPOGServer.createPolicy(spogServer.ReturnRandom("liuyu05-vm8004"), spogServer.ReturnRandom(pmfKey+"DR"), "cloud_direct_draas", null, "true",  cloud_source_id, destinations, schedules, null, policy_id, directOrgId1, test);
//		  policy4SPOGServer.checkPolicyDestinations(response,SpogConstants.SUCCESS_POST,destinations,test);
	  }
	  */
//	  @Test 
//	  public void createReplicationInPolicy(){
//		 
//		 
//		  String pmfKey="liuyu05";
//		  
//		  String pmfKey_prefix=spogServer.ReturnRandom(pmfKey);
//		  String policy_id=spogServer.returnRandomUUID();
//		  String task_id=spogServer.returnRandomUUID();
//		  String csrAdmin="tmp_xiang.li@arcserve.com";
//		  String csrPwd= "Caworld_2017";
//		  String baseURI = "http://tspog.zetta.net";
//	      String port = "8080";
//		  SPOGServer spogServer = new SPOGServer(baseURI, port);
//		  SPOGDestinationServer spogDestinationServer = new SPOGDestinationServer(baseURI, port);
//
//		  String orgPwdForPrepare= "Caworld_2017";
//		  ExtentTest test = new ExtentTest("testname", "description");
//		  spogServer.userLogin(csrAdmin, csrPwd);
//		  String user_token=spogServer.getJWTToken();
//		  //create cloud account and create destination
//		  String directOrgId1 = "9d0f9061-7847-4616-94f0-56882a1608cb";
//	
//		  spogServer.userLogin("liuyu05Gu6HH2LT@arcserve.com", "Caworld_2017");
//
//		  String cloud_direct_account_id= "7e7b7ee2-226c-43b7-b463-4082bff0d45e";
//	      spogDestinationServer.setToken(user_token);
//	      String destination_id_ret1= "673c2241-8a8f-40c1-8428-22695b7f7ad5";
//
//	      spogServer.setToken(user_token);
//		  String direct_user_id = "07d62283-6da3-4c08-b1ff-5fb2df38fa5e";
//		  spogServer.userLogin("liuyu05_direct123@arcserve.com", "Caworld_2017");
//		  String direct_user_validToken = spogServer.getJWTToken();
//
//		  String direct_site_id = "e029349a-0dba-44c4-9648-c0b22327324c";
//		  String direct_site_token=gatewayServer.getJWTToken();
//		  
//		  spogDestinationServer.setToken(user_token);
//		  String destination_store_id =  "06322b38-cd0e-4048-b1b8-8336855beaf6";
//		  
//		  policy4SPOGServer.setToken(user_token);
//		  String task_id_replicate=spogServer.returnRandomUUID();
//		  String task_type="udp_replication_from_remote";
//		  String policy_name=spogServer.ReturnRandom("liuyu05_replicationIn");
//		  String policy_description=spogServer.ReturnRandom("description");
//		  String policy_type="cloud_hybrid_replication";
//		  HashMap<String, Object> CustomScheduleDTO=policy4SPOGServer.createCustomScheduleDTO("1524469596000", "full","1","true","10","minutes",test);
//		  HashMap<String, Object> scheduleSettingDTO_replicate=policy4SPOGServer.createScheduleSettingDTO( null,CustomScheduleDTO,test);
//		  ArrayList<HashMap<String,Object>> schedules_replicate =policy4SPOGServer.createPolicyScheduleDTO(null,spogServer.returnRandomUUID(), 
//				  "custom", task_id_replicate, destination_store_id, scheduleSettingDTO_replicate, "06:00", "12:00", test);
//		  HashMap<String, Object> PerformARTestOption=policy4SPOGServer.createPerformARTestOption("true", "true", "true", "true", test);
//		  HashMap<String, Object> RetentionPolicyOption =policy4SPOGServer.createRetentionPolicyOption ("2", "2", "2", "2", test);
//		  HashMap<String, Object> udp_replication_from_remote =policy4SPOGServer.createUdpReplicationFromRemoteInfoDTO(PerformARTestOption, RetentionPolicyOption, test);
//		  ArrayList<HashMap<String,Object>>  destinations_replicate= policy4SPOGServer.createPolicyTaskDTO(null, task_id_replicate, task_type, destination_store_id, "none", null, null, udp_replication_from_remote, test);
//		  ArrayList<HashMap<String,Object>> throttles_replicate =policy4SPOGServer.createPolicyThrottleDTO(null, spogServer.returnRandomUUID(), task_id_replicate, "network", "1200", "1", "06:00", "18:00", task_type , destination_store_id,pmfKey_prefix+"-test",test);
//		  //create cloud repolicate from remote rps policy
//		  System.out.println("start create policy");
//		  policy4SPOGServer.createPolicy(policy_name, policy_description, policy_type, null, "true",  null, destinations_replicate, schedules_replicate, throttles_replicate, spogServer.returnRandomUUID(), directOrgId1, test);
//	}
	  /*
	  @Test
	  public void sample(){
		//only need modify parameter:pmfKey
		  String pmfKey="liuyu05";
		  
		  String pmfKey_prefix=spogServer.ReturnRandom(pmfKey);
		  String policy_id=spogServer.returnRandomUUID();
		  String task_id=spogServer.returnRandomUUID();
		  String csrAdmin="tmp_xiang.li@arcserve.com";
		  String csrPwd= "Caworld_2017";
		  String baseURI = "http://tspog.zetta.net";
	      String port = "8080";
		  SPOGServer spogServer = new SPOGServer(baseURI, port);
		  SPOGDestinationServer spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		  String directOrgNameForPrepare="DO_NOT_DELETE_spogqa_direct_org_"+pmfKey_prefix;
		  String mspOrgNameForPrepare="spogqa_msp_org_"+pmfKey_prefix;
		  String directOrgEmailForPrepare1=pmfKey_prefix+"@arcserve.com";
		  String mspOrgEmailForPrepare= pmfKey_prefix+"_msp@arcserve.com";
		  String accountEmailForPrepare =pmfKey_prefix+"_account@arcserve.com";
		  String gateway_hostname=pmfKey_prefix+"_accountSite1";
		  String orgPwdForPrepare= "Caworld_2017";
		  ExtentTest test = new ExtentTest("testname", "description");
		  spogServer.userLogin(csrAdmin, csrPwd);
		  String user_token=spogServer.getJWTToken();

		
		  spogServer.setToken(user_token);
		  
		  String mspOrgId = spogServer.CreateOrganizationWithCheck(mspOrgNameForPrepare, SpogConstants.MSP_ORG, mspOrgEmailForPrepare, orgPwdForPrepare, "tt", "tt");
		  String accountOrgId= spogServer.createAccountWithCheck(mspOrgId, spogServer.ReturnRandom("spogqa_account"),"");
		  String account_user_id = spogServer.createUserAndCheck(accountEmailForPrepare, orgPwdForPrepare, "dd", "tt", SpogConstants.DIRECT_ADMIN, accountOrgId, test);
		  spogServer.userLogin(accountEmailForPrepare, orgPwdForPrepare);
		  String account_user_validToken = spogServer.getJWTToken();
		  String siteName= spogServer.getRandomSiteName("TestCreate");
		  String sitetype=siteType.gateway.toString();
		  String account_site_id = gatewayServer.createsite_register_login(accountOrgId, account_user_validToken, account_user_id, gateway_hostname, "1.0.0", spogServer, test);
		  String account_site_token=gatewayServer.getJWTToken();
		  spogDestinationServer.setToken(user_token);
		  String destination_store_id = spogDestinationServer.createDestinationWithCheck("",accountOrgId, account_site_id, 
		    		"91a9b48e-6ac6-4c47-8202-614b5cdcfe0c","cloud_hybrid_store", "running","9",
		    		"", "normal", "shaji02-win7", "2M", "2M", 
		    		"0","0", "31", "0", "2", "0", "5", "true", "1", "true" ,pmfKey_prefix+"-test",		
				test);
		  policy4SPOGServer.setToken(user_token);
		  String task_id_replicate=spogServer.returnRandomUUID();
		  String task_type="udp_replication_from_remote";
		  String policy_name=spogServer.ReturnRandom("policy_name");
		  String policy_description=spogServer.ReturnRandom("description");
		  String policy_type="cloud_hybrid_replication";
		  HashMap<String, Object> CustomScheduleDTO=policy4SPOGServer.createCustomScheduleDTO("1524469596000", "full","1","true","10","minutes",test);
		  HashMap<String, Object> scheduleSettingDTO_replicate=policy4SPOGServer.createScheduleSettingDTO( null,CustomScheduleDTO,test);
		  ArrayList<HashMap<String,Object>> schedules_replicate =policy4SPOGServer.createPolicyScheduleDTO(null,spogServer.returnRandomUUID(), 
				  "custom", task_id_replicate, destination_store_id, scheduleSettingDTO_replicate, "06:00", "12:00", test);
		  HashMap<String, Object> PerformARTestOption=policy4SPOGServer.createPerformARTestOption("true", "true", "true", "true", test);
		  HashMap<String, Object> RetentionPolicyOption =policy4SPOGServer.createRetentionPolicyOption ("2", "2", "2", "2", test);
		  HashMap<String, Object> udp_replication_from_remote =policy4SPOGServer.createUdpReplicationFromRemoteInfoDTO(PerformARTestOption, RetentionPolicyOption, test);
		  ArrayList<HashMap<String,Object>>  destinations_replicate= policy4SPOGServer.createPolicyTaskDTO(null, task_id_replicate, task_type, destination_store_id, "none", null, null, udp_replication_from_remote, test);
		  ArrayList<HashMap<String,Object>> throttles_replicate =policy4SPOGServer.createPolicyThrottleDTO(null, spogServer.returnRandomUUID(), task_id_replicate, "network", "1200", "1", "06:00", "18:00", task_type , destination_store_id,pmfKey_prefix+"-test",test);
		  //create cloud repolicate from remote rps policy
		  policy4SPOGServer.createPolicy(policy_name, policy_description, policy_type, null, "true",  null, destinations_replicate, schedules_replicate, throttles_replicate, spogServer.returnRandomUUID(), accountOrgId, test);
		  
	  }
	  
	  
	  @Test
	  public void recycleVolume() {
		  spogDestinationServer.recycleCloudVolumesAndDelOrg("655561d6-975c-4a56-8b74-9bebd6866733",test);
	  }
*/
	  /*
	  @Test
	  public void createsite() {
		  String csrAdmin="tmp_xiang.li@arcserve.com";
		  String csrPwd= "Caworld_2017";
		  String baseURI = "http://tspog.zetta.net";
	      String port = "8080";
		  SPOGServer spogServer = new SPOGServer(baseURI, port);
		  spogServer.userLogin(csrAdmin, csrPwd);
		  String csrOrg_id = spogServer.GetLoggedinUserOrganizationID();
		  String user_token=spogServer.getJWTToken();
		  String user_id ="01aa7641-c732-4de7-b4be-f3123771ae32";
				  
		  
		  String siteName= spogServer.getRandomSiteName("yuefen-site");
		  String sitetype=siteType.gateway.toString();
		  String site_id = gatewayServer.createsite_register_login(csrOrg_id, user_token, user_id, "", "1.0.0", spogServer, test);
		  String site_token=gatewayServer.getJWTToken();
		  System.out.println("sitetoken is:"+site_token);
	  }
	  */
	  @Test
	  public void createsite() {
		  String csrAdmin="tmp_xiang.li@arcserve.com";
		  String csrPwd= "Caworld_2017";
		  String baseURI = "http://tspog.zetta.net";
	      String port = "8080";
		  SPOGServer spogServer = new SPOGServer(baseURI, port);
		  spogServer.userLogin(csrAdmin, csrPwd);
		  String Org_id = "cce55126-7269-4833-9bbf-432e663820a7";
		  String user_token=spogServer.getJWTToken();
		  String user_id ="01aa7641-c732-4de7-b4be-f3123771ae32";
				  
		  
		  String siteName= spogServer.getRandomSiteName("csr-yuefen-spogEnv-site");
		  String sitetype=siteType.gateway.toString();
		  String site_id = gatewayServer.createsite_register_login(Org_id, user_token, user_id, "", "1.0.0", spogServer, test);
		  String site_token=gatewayServer.getJWTToken();
		  System.out.println("sitetoken is:"+site_token);
	  }
	  
	  /*
	  @Test
	  public void test(){
		//only need modify parameter:pmfKey
		  String pmfKey="liuyu05";
		  
		  String csrAdmin="tmp_xiang.li@arcserve.com";
		  String csrPwd= "Caworld_2017";
		  String baseURI = "http://tspog.zetta.net";
	      String port = "8080";
		  SPOGServer spogServer = new SPOGServer(baseURI, port);
		  //spogServer.userLogin(csrAdmin, csrPwd);
		  spogServer.userLogin("ming.yang@arcserve.com", "Caworld_2017");
		  String user_token=spogServer.getJWTToken();
		  
		  //String directOrgId = spogServer.CreateOrganizationWithCheck("liuyu05-direct-org-replicate", "direct", "liuyu05_arcserve_test@spogqa.com", "Caworld_2017", "tt", "tt");
		 
		 // String direct_user_id = spogServer.createUserAndCheck("liuyu05_arcserve@spogqa.com", "Caworld_2017", "dd", "tt", "direct_admin", directOrgId, test);
//		  spogServer.userLogin("liuyu05_arcserve@spogqa.com", "Caworld_2017");
//		  String token = spogServer.getJWTToken();
//		  String siteName= spogServer.getRandomSiteName("TestCreate");
//		  String sitetype=siteType.gateway.toString();
//		  String direct_site_id = gatewayServer.createsite_register_login("95ba6127-6e3b-4ac2-bc4b-673c857606f8", token, "ad5bebe4-9879-479a-bb49-0a25cab5e4d2", "WIN-DR8RPOGU57J", "1.0.0", spogServer, test);
//		  String direct_site_token=gatewayServer.getJWTToken();
		  
		  policy4SPOGServer.setToken(user_token);
		  String task_id_replicate=spogServer.returnRandomUUID();
		  String task_type="udp_replication_from_remote";
		  String policy_name=spogServer.ReturnRandom("policy_name_liuyu05");
		  String policy_description=spogServer.ReturnRandom("description");
		  String policy_type="cloud_hybrid_replication";
		  HashMap<String, Object> CustomScheduleDTO=policy4SPOGServer.createCustomScheduleDTO("1524469596000", "full","1","true","10","minutes",test);
		  HashMap<String, Object> scheduleSettingDTO_replicate=policy4SPOGServer.createScheduleSettingDTO( null,CustomScheduleDTO,test);
		  ArrayList<HashMap<String,Object>> schedules_replicate =policy4SPOGServer.createPolicyScheduleDTO(null,spogServer.returnRandomUUID(), 
				  "custom", task_id_replicate, "8a99cc25-4f8a-4e52-9a68-b3d9dd5d58d4", scheduleSettingDTO_replicate, "06:00", "12:00", test);
		  HashMap<String, Object> PerformARTestOption=policy4SPOGServer.createPerformARTestOption("true", "true", "true", "true", test);
		  HashMap<String, Object> RetentionPolicyOption =policy4SPOGServer.createRetentionPolicyOption ("2", "2", "2", "2", test);
		  HashMap<String, Object> udp_replication_from_remote =policy4SPOGServer.createUdpReplicationFromRemoteInfoDTO(PerformARTestOption, RetentionPolicyOption, test);
		  ArrayList<HashMap<String,Object>>  destinations_replicate= policy4SPOGServer.createPolicyTaskDTO(null, task_id_replicate, task_type, "8a99cc25-4f8a-4e52-9a68-b3d9dd5d58d4", "none", null, null, udp_replication_from_remote, test);
		  ArrayList<HashMap<String,Object>> throttles_replicate =policy4SPOGServer.createPolicyThrottleDTO(null, spogServer.returnRandomUUID(), task_id_replicate, "network", "1200", "1", "06:00", "18:00", task_type , "8a99cc25-4f8a-4e52-9a68-b3d9dd5d58d4","liuyu05-hybridDS101",test);
		  //create cloud repolicate from remote rps policy
		  policy4SPOGServer.createPolicy("liuyu05-replicate-cloud2", policy_description, policy_type, null, "false",  null, destinations_replicate, schedules_replicate, throttles_replicate, spogServer.returnRandomUUID(),"899c693b-5b95-438d-a160-b1c5dd978567", test);
		  
	  }
	  */
	 /*
	  @Test 
	  public void createDRpolicy(){
		 
		 
		  String pmfKey="liuyu05";
		  
		  String pmfKey_prefix=spogServer.ReturnRandom(pmfKey);
		  String policy_id=spogServer.returnRandomUUID();
		  String task_id=spogServer.returnRandomUUID();
		  String csrAdmin="tmp_xiang.li@arcserve.com";
		  String csrPwd= "Caworld_2017";
		  //String baseURI = "http://tspog.zetta.net";
		  String baseURI="http://tspog.zetta.net";
	
	      String port = "8080";
		  SPOGServer spogServer = new SPOGServer(baseURI, port);
		  SPOGDestinationServer spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		  UserSpogServer userSpogServer =new UserSpogServer(baseURI, port);
		  //String directOrgNameForPrepare="DO_NOT_DELETE_spogqa_direct_org_"+pmfKey_prefix;
		  String userName="yuefen.liu@arcserve.com";
		  String password= "Caworld_2017";
		  ExtentTest test = new ExtentTest("testname", "description");
		  spogServer.userLogin(userName, password);
		  String user_token=spogServer.getJWTToken();
		  //create cloud account and create destination
//		  String directOrgId1 = spogServer.CreateOrganizationWithCheck(directOrgNameForPrepare, SpogConstants.DIRECT_ORG, directOrgEmailForPrepare1, orgPwdForPrepare, "dd", "dd");
//		  System.out.println("orgID:"+directOrgId1);
//		  spogServer.userLogin(directOrgEmailForPrepare1, orgPwdForPrepare);
//		  System.out.println("username:"+directOrgEmailForPrepare1);
//		  System.out.println("password:"+orgPwdForPrepare);
//		  String cloud_direct_account_id=spogServer.createCloudAccountWithCheck("cloudAccountKey", "cloudAccountSecret", pmfKey_prefix+"cloudAccountName", "cloud_direct", directOrgId1, 
//					"SKUTESTDATA_8_8_0_0_"+pmfKey_prefix, "SKUTESTDATA_8_8_0_0_"+pmfKey_prefix, "SKUTESTDATA_8_8_0_0_"+pmfKey_prefix, "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
//		  System.out.println("cloudID:"+cloud_direct_account_id);
//		  String siteName= spogServer.getRandomSiteName("yuefen-site");
//		  String sitetype=siteType.gateway.toString();
//		  String site_id = gatewayServer.createsite_register_login("8b6113a1-d046-4e73-96d9-a2547f373853", user_token, "8e6bcc15-08df-4844-8364-5ce931432d08", "liuyu05-gateway123", "1.0.0", spogServer, test);
//		  String site_token=gatewayServer.getJWTToken();
//		  System.out.println("sitetoken is:"+site_token);
		  
	      spogDestinationServer.setToken(user_token);
	      userSpogServer.setToken(user_token);
	      userSpogServer.addOrderByOrgId("d9110ff5-0752-458a-beae-697ed31bf064", "SKUTESTDATA_8_8_0_0_"+pmfKey_prefix, "SKUTESTDATA_8_8_0_0_"+pmfKey_prefix, test);
	      
	      String destination_id_ret1=spogDestinationServer.createDestinationWithCheck(  "","340f59da-f82d-4d78-9161-ff26da861e11", "","91a9b48e-6ac6-4c47-8202-614b5cdcfe0c","cloud_direct_volume", "running", "1",
	    		  "716f48ee-3865-46c8-9a48-2519439b9827", "zero_copy", pmfKey_prefix, "7Dhf", "7Dhf", "0", "0", "31", "0", "2", "0", "4",
							"1", "4", "1", "liuyu05-destination_drtest3", test); 
	      System.out.println("destinationId:"+destination_id_ret1);
		  HashMap<String, Object> cloudDirectScheduleDTO=policy4SPOGServer.createCloudDirectScheduleDTO("0 0 * * *", test);
		  HashMap<String, Object> scheduleSettingDTO=policy4SPOGServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null,test);
		  ArrayList<HashMap<String,Object>> schedules =policy4SPOGServer.createPolicyScheduleDTO(null,spogServer.returnRandomUUID(), 
				  "1d", task_id, destination_id_ret1, scheduleSettingDTO, "06:00", "12:00", "cloud_direct_image_backup","DO_NOT_DELETE_YUEFEN",test);
		  ArrayList<HashMap<String,Object>> excludes=policy4SPOGServer.createExcludeInfoDTO(null, "path", "c:\\tmp", test);
//		  HashMap<String, Object> cloudDirectLocalBackupDTO=policy4SPOGServer.createCloudDirectLocalBackupDTO("d:\\tmp", "true", excludes, test);
//		  HashMap<String, Object> cloudDirectFileBackupTaskInfoDTO=policy4SPOGServer.createCloudDirectFileBackupTaskInfoDTO("d:\\tmp", cloudDirectLocalBackupDTO, test);
//		  ArrayList<HashMap<String,Object>>  destinations= policy4SPOGServer.createPolicyTaskDTO(null, task_id, "cloud_direct_image_backup", destination_id_ret1, "none", null, cloudDirectFileBackupTaskInfoDTO, null, test);
		  HashMap<String, Object> cloudDirectLocalBackupDTO=policy4SPOGServer.createCloudDirectLocalBackupDTO("*", "true", test);
		  HashMap<String, Object> cloudDirectImageBackupTaskInfoDTO=policy4SPOGServer.createCloudDirectImageBackupTaskInfoDTO("*", cloudDirectLocalBackupDTO, test);
		  ArrayList<HashMap<String,Object>>  destinations= policy4SPOGServer.createPolicyTaskDTO(null, task_id, "cloud_direct_image_backup", destination_id_ret1, "none", cloudDirectImageBackupTaskInfoDTO, null, null, test);
		  ArrayList<HashMap<String,Object>> throttles =policy4SPOGServer.createPolicyThrottleDTO(null, spogServer.returnRandomUUID(), task_id, "network", "1200", "1", "06:00", "18:00", "cloud_direct_image_backup",destination_id_ret1,"DO_NOT_DELETE_YUEFEN_trottle",test);
		  Policy4SPOGServer policy4SPOGServer= new Policy4SPOGServer("http://tspog.zetta.net", "8080");
		  
		  policy4SPOGServer.setToken(user_token);
		  String cloud_source_id ="10a2982c-1de6-4652-b7a8-cd076daa2c9b";
		  policy4SPOGServer.createPolicy(spogServer.ReturnRandom(pmfKey+"DR"), spogServer.ReturnRandom(pmfKey), "cloud_direct_draas", null, "true",  cloud_source_id, destinations, schedules, null, policy_id, "340f59da-f82d-4d78-9161-ff26da861e11", test);
		  
	  }
*/	 
}
	  
