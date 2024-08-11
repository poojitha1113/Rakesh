package api.policies.sources;

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

public class UnassignPolicyTest  extends base.prepare.PrepareOrgInfo{
	@Parameters({ "pmfKey"})
	public UnassignPolicyTest(String pmfKey) {
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
	  private String one_cloud_direct_policy_id;
	  private String csr_token;
	  
	  //this is creating msp org or direct org for preparation
	  private String mspOrgNameForPrepare="d_jing_spogqa_msp_org_prepare";
	  private String csrOrgNameForPrepare="d_jing_spogqa_csr_org_prepare";
	  private String csrOrgEmailForPrepare,mspOrgEmailForPrepare,mspOrgEmailForPrepare1,accountEmailForPrepare,accountEmailForPrepare1;
	  private String directOrgNameForPrepare="d_jing_spogqa_direct_org_prepare";
	  private String directOrgEmailForPrepare,directOrgEmailForPrepare1;
	  private String OrgFistNameForPrepare="jing";
	  private String OrgLastNameForPrepare="org_prepare";
	  private String OrgPwdForPrepare="Caworld2017";
	  private String sub_msp_pwd="Welcome*02";
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
	  //private ExtentReports rep;
	  private String cloudOrgID="d06f197a-6e36-4436-a0ad-ea18e9e0b319";
	  private String cloudEmail = "cloud_jing_spogqa_direct_org_prepare@arcserve.com";
	  private String cloudAccountID = "c709f16b-f687-480b-a21b-f68f401e04b4";
	  private String cloud_account_key = "cloudAccountKey";
	  private String cloud_account_name = "cloudAccountName_Jing";
	  private String cloud_user_id = "24588182-ea27-493b-a89f-c67c389655bd";
	  private String direct_cloud_org_name=null;
	  
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
		  this.accountEmailForPrepare = spogServer.ReturnRandom("d_jing_spogqa_account_org_prepare@arcserve.com");
		  this.accountEmailForPrepare1 = spogServer.ReturnRandom("d_jing_spogqa_account_org_prepare@arcserve.com");
		  this.csrOrgEmailForPrepare = spogServer.ReturnRandom("d_jing_spogqa_csr_org_prepar@arcserve.come");
		  this.mspOrgEmailForPrepare = spogServer.ReturnRandom("d_jing_spogqa_msp_org_prepar@arcserve.come");
		  this.mspOrgEmailForPrepare1 = spogServer.ReturnRandom("d_jing_spogqa_msp_org_prepare@arcserve.com");
		  
		  this.directOrgEmailForPrepare1 = spogServer.ReturnRandom("d_jing_spogqa_direct_org_prepare0327@arcserve.com");
		  this.directOrgEmailForPrepare = spogServer.ReturnRandom("d_jing_spogqa_direct_org_prepare0328@arcserve.com");
		  spogServer.userLogin(this.csrAdmin, this.csrPwd);
		  String schedule_id=spogServer.returnRandomUUID();
			String task_id=spogServer.returnRandomUUID();
			String task_type="cloud_direct_file_folder_backup";
			String throttle_id=spogServer.returnRandomUUID();
			String policy_name=spogServer.ReturnRandom("test");
			String policy_description=spogServer.ReturnRandom("description");
			String policy_type="cloud_direct_baas";
			one_cloud_direct_policy_id=spogServer.returnRandomUUID();
		  spogServer.userLogin(this.csrAdmin, this.csrPwd);
		  String csr_token=spogServer.getJWTToken();
		  
        String pmfKey="shaji02";
		  String pmfKey_prefix=spogServer.ReturnRandom(pmfKey);
		 
		  spogServer.userLogin(direct_admin_email1, direct_password);
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
			prepare(baseURI, port, logFolder, csrAdminUserName, csrAdminPassword, this.getClass().getSimpleName() );
	      
	  }
	  
	  @DataProvider(name = "getRightInformation")
	  public final Object[][] getRightInformation() {
			return new Object[][] { 
				{one_cloud_direct_policy_id,this.direct_source_name1}};
	  }
	  @Test(dataProvider = "getRightInformation")
	  public void assignPolicyWithRightPolicyIdCloud(String policyId,String sourceId){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  spogServer.userLogin(direct_admin_email1, direct_password);
		  policy4SPOGServer.setToken(spogServer.getJWTToken());
		  Response response=policy4SPOGServer.unassignPolicy(policyId, sourceId);
	  }
	  	  
	  @DataProvider(name = "getPolicyIdForMultipleSourcesNegtive")
	  public final Object[][] getPolicyIdForMultipleSourcesNegtive() {
			return new Object[][] { 
				{this.one_cloud_direct_policy_id,this.direct_udp_source_id,403,"00100101","The current user does not have permissions to manage the resource."},
				{this.one_cloud_direct_policy_id,"fdsdf",400,"40000005","The element <list element> is not a UUID."},
				{this.one_cloud_direct_policy_id,spogServer.returnRandomUUID(),404,"00100201","Unable to find resource with ID"}};
	  }
	  @Test(dataProvider = "getPolicyIdForMultipleSourcesNegtive")
	  public void assignPolicyWithPolicyIdForMultipleSourcesNegtive(String policyId,String sourceId,int statusCode,String errorCode,String message){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  spogServer.userLogin(direct_admin_email1, direct_password);
		  policy4SPOGServer.setToken(spogServer.getJWTToken());
		  Response response=policy4SPOGServer.unassignPolicy(policyId, sourceId);
		  policy4SPOGServer.checkPolicyWithErrorInfo(response,statusCode,errorCode,message,test);
	  }
	  
	  @DataProvider(name = "getPolicyIdForMultipleSourcesNegtiveFor3ties")
	  public final Object[][] getPolicyIdForMultipleSourcesNegtiveFor3ties() {
			return new Object[][] { 
				{"submspaccount",this.one_cloud_direct_policy_id,this.direct_udp_source_id,403,"00100101","The current user does not have permissions to manage the resource."},
				{"submspaccount",this.one_cloud_direct_policy_id,"fdsdf",400,"40000005","The element <list element> is not a UUID."},
				{"submspaccount",this.one_cloud_direct_policy_id,spogServer.returnRandomUUID(),404,"00100201","Unable to find resource with ID"},
				{"submspaccountadmin",this.one_cloud_direct_policy_id,this.direct_udp_source_id,403,"00100101","The current user does not have permissions to manage the resource."},
				{"submspaccountadmin",this.one_cloud_direct_policy_id,"fdsdf",400,"40000005","The element <list element> is not a UUID."},
				{"submspaccountadmin",this.one_cloud_direct_policy_id,spogServer.returnRandomUUID(),404,"00100201","Unable to find resource with ID"},
				{"submsp",this.one_cloud_direct_policy_id,this.direct_udp_source_id,403,"00100101","The current user does not have permissions to manage the resource."},
				{"submsp",this.one_cloud_direct_policy_id,"fdsdf",400,"40000005","The element <list element> is not a UUID."},
				{"submsp",this.one_cloud_direct_policy_id,spogServer.returnRandomUUID(),404,"00100201","Unable to find resource with ID"},
				
				{"rootmspaccount",this.one_cloud_direct_policy_id,this.direct_udp_source_id,403,"00100101","The current user does not have permissions to manage the resource."},
				{"rootmspaccount",this.one_cloud_direct_policy_id,"fdsdf",400,"40000005","The element <list element> is not a UUID."},
				{"rootmspaccount",this.one_cloud_direct_policy_id,spogServer.returnRandomUUID(),404,"00100201","Unable to find resource with ID"},
				{"rootmspaccountadmin",this.one_cloud_direct_policy_id,this.direct_udp_source_id,403,"00100101","The current user does not have permissions to manage the resource."},
				{"rootmspaccountadmin",this.one_cloud_direct_policy_id,"fdsdf",400,"40000005","The element <list element> is not a UUID."},
				{"rootmspaccountadmin",this.one_cloud_direct_policy_id,spogServer.returnRandomUUID(),404,"00100201","Unable to find resource with ID"},
				{"rootmsp",this.one_cloud_direct_policy_id,this.direct_udp_source_id,403,"00100101","The current user does not have permissions to manage the resource."},
				{"rootmsp",this.one_cloud_direct_policy_id,"fdsdf",400,"40000005","The element <list element> is not a UUID."},
				{"rootmsp",this.one_cloud_direct_policy_id,spogServer.returnRandomUUID(),404,"00100201","Unable to find resource with ID"},
				};
	  }
	  @Test(dataProvider = "getPolicyIdForMultipleSourcesNegtiveFor3ties")
	  public void assignPolicyWithPolicyIdForMultipleSourcesNegtiveFor3ties(String loginUserType,String policyId,String sourceId,int statusCode,String errorCode,String message){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  if(loginUserType.equalsIgnoreCase("submspaccount")) {
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
		  policy4SPOGServer.setToken(spogServer.getJWTToken());
		  Response response=policy4SPOGServer.unassignPolicy(policyId, sourceId);
		  policy4SPOGServer.checkPolicyWithErrorInfo(response,statusCode,errorCode,message,test);
	  }
	  
	  
	  @DataProvider(name = "getNegativePolicyId")
	  public final Object[][] getNegativePolicyId() {
			return new Object[][] { 
				{"",400,"40000005","The element policyId is not a UUID."},{null,400,"40000005","The element policyId is not a UUID."},
				{"random",404,"00E00008","Unable to find policy"},{"fsadfa",400,"40000005","The element policyId is not a UUID."}};
	  }
	  @Test(dataProvider = "getNegativePolicyId")
	  public void assignPolicyWithPolicyIdNegative(String policyId,int statusCode,String errorCode,String message){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  if(policyId!=null && policyId.equalsIgnoreCase("random")){
			  policyId=spogServer.returnRandomUUID();
		  }
		  spogServer.userLogin(direct_admin_email1, direct_password);
		  policy4SPOGServer.setToken(spogServer.getJWTToken());
		  Response response=policy4SPOGServer.unassignPolicy(policyId, real_cd_source_id);
		  policy4SPOGServer.checkPolicyWithErrorInfo(response,statusCode,errorCode,message,test);
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
