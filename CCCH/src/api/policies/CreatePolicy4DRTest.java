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
import Constants.OSMajor;
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

public class CreatePolicy4DRTest extends base.prepare.PrepareOrgInfo{
	@Parameters({ "pmfKey"})
	public CreatePolicy4DRTest(String pmfKey) {
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
	  
	  private String OrgPwdForPrepare="Caworld_2017";
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
	  private String direct_org_id="ee7390ba-e2e9-4217-af4a-fb4375ea5654";
	  private String direct_org_name="arcserve163";
	  private String direct_admin_email="jing_direct_ui@arcserve.com";
	  private String cloud_direct_account_id="debf660e-4d83-413f-93c2-74744a2f2d8e";
	  private String direct_zero_id="b92aa6ca-f0e3-48c5-abdc-97a282e68366";
	  private String direct_normal_id="1678fba4-51b0-423e-8ff6-71bd7510be13";
	  private String recycle_normal_id="51a59fae-920a-47fb-8390-6666c26fc33e";
	  private String recycle_zero_id="ffc01997-a041-4e0c-9dc7-7735bdab570a";
	  //the second direct
	  private String direct_org_id1="ec0dc39b-23a8-437a-be79-3f027aa64906";
	  private String direct_org_name1="shaji02-arcserve";
	  private String direct_admin_email1="jing_direct_ui@arcserve.com";
	  private String cloud_direct_account_id1="9aeb9c55-fb96-4e75-a166-73ac47ce3f9f";
	  private String direct_zero_id1="f0593c52-6fa1-441e-a401-1636d02d44e6";
	  private String direct_normal_id1="0871c073-515f-40ee-8da4-6884c9321d56";
//	  private String direct_source_id="9606e1e1-6dba-46c6-a286-6a242da5ee6b";
//	  private String direct_source_name="shaji02-spog2";
	  private String direct_source_id="90ad0cf4-6cb3-4d08-b33e-530f44818263";
	  private String direct_source_name="shaji02-spog1";
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
	  
	  private String msp_admin_email="jing_msp_ui@arcserve.com";
	  private String msp_org_name="shaji02-msp01_do_not_delete";
	  private String msp_ac_admin_email="shaji02-msp-account@arcserve.com";
	  private String msp_ac1_email="jing_account_ui_1@arcserve.com";
	  private String msp_ac2_email="shaji02-account-2@arcserve.com";
	  private String msp_ac1_org_name="msp_ac1_normal";
	  private String msp_ac2_org_name="msp_ac2_normal";
	 
	  private String msp_account_org_id_1="dc7721ff-41ca-4e59-9af9-28e4ae0ca44b";
	  private String msp_account_org_id_2="a002c19c-9c9d-4a39-bf53-bf7d245296f0";

	  private String msp_ac1_normal_id="271c7ccb-4382-4246-9d68-e1c0fac5fcaf";

	  private String msp_ac2_normal_id="257a71cd-8842-4b84-ac5a-09d71ec46c8d";

	  private String msp_ac1_zero_id="86454795-c06c-4c56-a812-d3c61ebcf866";
	  private String msp_ac2_zero_id="9e61b900-cb23-43c3-a917-77eb1799a939";
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
	  private String msp_ac3_org_name="shaji02-account-3";
	  private String msp_ac4_org_name="shaji02-account-4";
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
	  
	  
	  
//	  private String real_source_id="749d558d-9183-4c2e-9a0c-7eedcc0aa5e1";
//	  private String real_source_name="shaji02-spog1";
//	  private String real_user="shaji02vilrxf8o@arcserve.com";
//	  private String real_pwd=OrgPwdForPrepare;
//	  private String real_org_id="903edf2e-1cae-4b44-83dd-f085aa075f36";
//	  private String real_cloud_account_id="f0e985f3-7fe1-45f7-a1e4-73a518feab0e";
//	  private String real_cloud_secret_key="cloudAccountSecret";
	  
	  
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
		  prepare(baseURI, port, logFolder, csrAdminUserName, csrAdminPassword, this.getClass().getSimpleName() );
		  
	  }
	  
	  public boolean isNullOrEmpty(String checkValue){
		  if(checkValue==null || checkValue==""){
			  return true;
		  }else{
			  return false;
		  }
		  
	  }
	  @DataProvider(name = "loginUserToCreateDifferentCommonSettingNegative")
	  public final Object[][] getLoginUserToCreateDifferentCommonSettingNegative() {
			return new Object[][] { 
				{"csr","1234567812345678123456781234567812345678123456781234567812345678123456781234567812345678123456781234567812345678123456781234567812345678123456781234567","","cloud_direct_draas","true",  "", "","dr",true,400,"40000002","The length of policyName is incorrect."},
//				
				{"msp","test","","cloud_direct_draas","true","", "random","dr",true,404,"0030000A","not found or has been removed"},
				{"msp","","","cloud_direct_draas","true",  "", "","dr",true,400,"40000001","The policyName cannot be blank."},
				{"msp",null,"","cloud_direct_draas","true",  "", "","dr",true,400,"40000001","The policyName cannot be blank."},
				{"msp","test","","","true",  "", "","dr",true,400,"40000001","The policyType cannot be blank"},
				{"msp","test","",null,"true",  "", "","dr",true,400,"40000001","The policyType cannot be blank"},
				{"msp","test","","","true",  "", "","dr",false,400,"40000001","The policyType cannot be blank"},
				{"msp","test","","","false",  "", "","trst",true,400,"40000006","is not a valid value."},
     			
				{"msp","test","","","true",  "", "","",true,400,"40000001","The taskType cannot be blank"},
				{"msp","test","","","true",  "", "",null,true,400,"40000001","The taskType cannot be blank"},
				{"msp","test","","","true",  "", "","",true,400,"40000001","The policyType cannot be blank"},
				{"msp","test","","","true",  "", "","trst",true,400,"40000006","is not a valid value."},
				{"msp","1234567812345678123456781234567812345678123456781234567812345678123456781234567812345678123456781234567812345678123456781234567812345678123456781234567","","cloud_direct_draas","true",  "", "","dr",true,400,"40000002","The length of policyName is incorrect."},
				
				{"direct","test","","cloud_direct_draas","true","", "random","dr",true,404,"0030000A","not found or has been removed"},
				{"direct","","","cloud_direct_draas","true",  "", "","dr",true,400,"40000001","The length of policyName is incorrect"},
				{"direct",null,"","cloud_direct_draas","true",  "", "","dr",true,400,"40000001","The policyName cannot be blank"},
				{"direct","test","","","true",  "", "","dr",true,400,"40000001","The policyType cannot be blank"},
				{"direct","test","",null,"true",  "", "","dr",true,400,"40000001","The policyType cannot be blank"},
				{"direct","test","","","true",  "", "","dr",false,400,"40000001","The policyType cannot be blank"},
				{"direct","test","","","false",  "", "","trst",true,400,"40000006","is not a valid value."},
				
				{"direct","test","","","true",  "", "","",true,400,"40000001","The policyType cannot be blank"},
				{"direct","test","","","true",  "", "",null,true,400,"40000001","The policyType cannot be blank"},
				{"direct","test","","","true",  "", "","",true,400,"40000001","The policyType cannot be blank"},
				{"direct","test","","","true",  "", "","trst",true,400,"40000006","is not a valid value."},
				{"direct","1234567812345678123456781234567812345678123456781234567812345678123456781234567812345678123456781234567812345678123456781234567812345678123456781234567","","cloud_direct_draas","true",  "", "","dr",true,400,"40000002","The length of policyName is incorrect."},
					
				{"account","test","","cloud_direct_draas","true","", "random","dr",true,404,"0030000A","either not found or has been removed"},
				{"account","","","cloud_direct_draas","true",  "", "","dr",true,400,"40000001","The policyName cannot be blank"},
				{"account",null,"","cloud_direct_draas","true",  "", "","dr",true,400,"40000001","The policyName cannot be blank"},
				{"account","test","","","true",  "", "","dr",true,400,"40000001","The policyType cannot be blank"},
				{"account","test","",null,"true",  "", "","dr",true,400,"40000001","The policyType cannot be blank"},
				{"account","test","","","true",  "", "","dr",false,400,"40000001","The policyType cannot be blank"},
				{"account","test","","","false",  "", "","trst",true,400,"40000006","is not a valid value."},
				
				{"account","test","","","true",  "", "","",true,400,"40000001","The policyType cannot be blank"},
				{"account","test","","","true",  "", "",null,true,400,"40000001","The policyType cannot be blank"},
				{"account","test","","","true",  "", "","",true,400,"40000001","The taskType cannot be blank"},
				{"account","test","","","true",  "", "","trst",true,400,"40000006","is not a valid value."},
				{"account","1234567812345678123456781234567812345678123456781234567812345678123456781234567812345678123456781234567812345678123456781234567812345678123456781234567","","cloud_direct_draas","true",  "", "","dr",true,400,"40000002","The length of policyName is incorrect."}};
	  }
	  @Test(dataProvider = "loginUserToCreateDifferentCommonSettingNegative")	  
	  public void createPolicyWithCommonSettingNegative(String loginUserType,String policy_name,String policy_description ,String policy_type ,
			  String is_draft,String policy_id , String organization_id,String task_type,Boolean hasdest,int statusCode,String errorCode,String message ){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  String task_id=spogServer.returnRandomUUID();
		  if(!isNullOrEmpty(task_type)){
			  if(task_type.equalsIgnoreCase("dr")){
				  task_type="cloud_direct_image_backup";
			  }else{
				  task_type=spogServer.ReturnRandom("fadfa");
			  }
		  }
		  String throttle_id=spogServer.returnRandomUUID();
		  
		  if(!isNullOrEmpty(policy_description)){
			  policy_description=spogServer.ReturnRandom("description");
		  }		  
		  if(isNullOrEmpty(policy_id)){
			  policy_id=spogServer.returnRandomUUID();
		  }
		  if (loginUserType.equalsIgnoreCase("csr")){
			  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
		  }else if(loginUserType.equalsIgnoreCase("direct")){
			  spogServer.userLogin(direct_admin_email1, password);
		  }else if(loginUserType.equalsIgnoreCase("msp")){
			  spogServer.userLogin(this.msp_admin_email, OrgPwdForPrepare,test);
		  }else if(loginUserType.equalsIgnoreCase("account")){
			  spogServer.userLogin(this.msp_ac1_email, OrgPwdForPrepare,test);
		  }
		  if(isNullOrEmpty(organization_id)){
			  organization_id=spogServer.GetLoggedinUserOrganizationID();
			  if(loginUserType.equalsIgnoreCase("msp")){
				  organization_id=this.msp_account_org_id_1;
			  }else if(loginUserType.equalsIgnoreCase("csr")){
				  organization_id=this.direct_org_id1;
			  }
		  }else{
			  organization_id=spogServer.returnRandomUUID();
		  }
		  HashMap<String, Object> cloudDirectScheduleDTO=policy4SPOGServer.createCloudDirectScheduleDTO("0 6 * * *", test);
		  HashMap<String, Object> scheduleSettingDTO=policy4SPOGServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null,test);
		  ArrayList<HashMap<String,Object>> schedules =policy4SPOGServer.createPolicyScheduleDTO(null,spogServer.returnRandomUUID(), 
				  "1d", task_id, this.direct_zero_id1, scheduleSettingDTO, "06:00", "12:00", task_type ,this.msp_ac1_zero_name,test);
		  
		  ArrayList<HashMap<String,Object>> excludes=policy4SPOGServer.createExcludeInfoDTO(null, "path", "c:\\tmp", test);
		  HashMap<String, Object> cloudDirectLocalBackupDTO=policy4SPOGServer.createCloudDirectLocalBackupDTO("d:\\tmp", "true", test);
		  HashMap<String, Object> cloudDirectImageBackupTaskInfoDTO=policy4SPOGServer.createCloudDirectImageBackupTaskInfoDTO("*", cloudDirectLocalBackupDTO, test);
		  ArrayList<HashMap<String,Object>>  destinations= policy4SPOGServer.createPolicyTaskDTO(null, task_id, task_type, this.direct_zero_id1, "none", cloudDirectImageBackupTaskInfoDTO, null, null, test);
		  ArrayList<HashMap<String,Object>> throttles =policy4SPOGServer.createPolicyThrottleDTO(null, throttle_id, task_id, "network", "1200", "1", "06:00", "18:00", task_type , this.direct_zero_id1,this.msp_ac1_zero_name,test);
		  spogServer.userLogin(this.csrAdmin, this.csrPwd);
		  String user_token=spogServer.getJWTToken();
		  policy4SPOGServer.setToken(user_token);
		  Response response=null;
		  if(hasdest){
			  response=policy4SPOGServer.createPolicy(policy_name, policy_description, policy_type, null, is_draft,  this.direct_source_id, destinations, schedules, throttles, policy_id, organization_id, test);
		  }else{
			  response=policy4SPOGServer.createPolicy(policy_name, policy_description, policy_type, null, is_draft,  this.direct_source_id, null, schedules, throttles, policy_id, organization_id, test);
		  }
		  //Response response=policy4SPOGServer.createPolicy(policy_name, policy_description, policy_type, null, "true", "successful", source_id_1, destinations, schedules, throttle, policy_id, organization_id, test);
		  policy4SPOGServer.checkPolicyWithErrorInfo(response,statusCode,errorCode,message,test);		  
	  }
	  
	  @DataProvider(name = "loginUserToCreateDRPolicy4SrcAndDestDiffOrg")
	  public final Object[][] getLoginUserToCreateDRPolicy4SrcAndDestDiffOrg() {
			return new Object[][] { 
				{"csr",true,true},
				{"csr",true,false},
				{"msp",true,true},
				{"msp",true,false},
				{"account",true,true},
				{"account",true,false},
				{"direct",true,true},
				{"direct",true,false},
				{"submspaccount",true,true},
				{"submspaccount",true,false},
				{"submspaccountadmin",true,true},
				{"submspaccountadmin",true,false},
				{"submsp",true,true},
				{"submsp",true,false},
				{"rootmspaccount",true,true},
				{"rootmspaccount",true,false},
				{"rootmspaccountadmin",true,true},
				{"rootmspaccountadmin",true,false},
				{"rootmsp",true,true},
				{"rootmsp",true,false}};
	  }
	  @Test(dataProvider = "loginUserToCreateDRPolicy4SrcAndDestDiffOrg")
	  public void createPolicyWithDRPolicy4SrcAndDestDiffOrg(String loginUserType,Boolean hasschedule,Boolean hasthrottle){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  String schedule_id=spogServer.returnRandomUUID();
		  String task_id=spogServer.returnRandomUUID();
		  String task_type="cloud_direct_image_backup";
		  String policy_name=spogServer.ReturnRandom("policy_name");
		  String policy_description=spogServer.ReturnRandom("description");
		  String policy_id=spogServer.returnRandomUUID();
		  String policy_type="cloud_direct_draas";
		  int status=403;
		  String errorcode="00C00001";
		  String errormes="Permission required to manage the resource for current user";
		  if (loginUserType.equalsIgnoreCase("csr")){
			  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
		  }else if(loginUserType.equalsIgnoreCase("direct")){
			  status=400;
			  errorcode="00E0004A";
			  errormes="Source, destination and policy are not in the same organization.";
			  spogServer.userLogin(direct_admin_email1, password);
		  }else if(loginUserType.equalsIgnoreCase("msp")){
			  errorcode="00100101";
			  spogServer.userLogin(this.msp_admin_email, OrgPwdForPrepare,test);
			  errormes="The current user does not have permissions to manage the resource.";
		  }else if(loginUserType.equalsIgnoreCase("account")){
			  errorcode="00100101";
			  spogServer.userLogin(this.msp_ac1_email, OrgPwdForPrepare,test);
			  errormes="The current user does not have permissions to manage the resource.";			  
		  }else if(loginUserType.equalsIgnoreCase("submspaccount")) {
			  spogServer.userLogin(this.final_sub_msp2_account1_user_email, sub_msp_pwd,test);
			  errorcode="00100101";
			  errormes="The current user does not have permissions to manage the resource.";	
			  
		  }else if(loginUserType.equalsIgnoreCase("submspaccountadmin")) {
			  status=400;
			  spogServer.userLogin(this.final_sub_msp1_msp_account_user_name_email, sub_msp_pwd,test);
			  errorcode="00100025";
			  errormes="MSP cannot operate the API";	
			  
		  }else if(loginUserType.equalsIgnoreCase("submsp")) {
			  spogServer.userLogin(this.final_sub_msp2_user_name_email, sub_msp_pwd,test);
			  errorcode="00100101";
			  errormes="The current user does not have permissions to manage the resource.";	
			  
		  }else if(loginUserType.equalsIgnoreCase("rootmspaccount")) {
			  spogServer.userLogin(this.final_root_msp_direct_org_user_email, sub_msp_pwd,test);
			  errorcode="00100101";
			  errormes="The current user does not have permissions to manage the resource.";	
			  
		  }else if(loginUserType.equalsIgnoreCase("rootmspaccountadmin")) {
			  status=400;
			  spogServer.userLogin(this.final_root_msp_account_admin_user_name_email, sub_msp_pwd,test);
			  errorcode="00100025";
			  errormes="MSP cannot operate the API";	
			  
		  }else if(loginUserType.equalsIgnoreCase("rootmsp")) {
			  spogServer.userLogin(this.final_root_msp_user_name_email, sub_msp_pwd,test);
			  errorcode="00100101";
			  errormes="The current user does not have permissions to manage the resource.";	
			  
		  }
		  String organization_id=spogServer.GetLoggedinUserOrganizationID();	
		  if(loginUserType.equalsIgnoreCase("msp")){
			  organization_id=this.msp_account_org_id_1;
		  }else if(loginUserType.equalsIgnoreCase("csr")){
			  organization_id=this.direct_org_id1;
		  }
		  if (loginUserType.equalsIgnoreCase("csr")){
			  status=403;
			  errorcode="00E00059";
			  errormes="Can not create policy due to organization expired.";	
			  organization_id= this.direct_org_id1;
		  }
		  HashMap<String, Object> cloudDirectScheduleDTO=policy4SPOGServer.createCloudDirectScheduleDTO("0 6 * * *", test);
		  HashMap<String, Object> scheduleSettingDTO=policy4SPOGServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null,test);
		  ArrayList<HashMap<String,Object>> schedules =policy4SPOGServer.createPolicyScheduleDTO(null,schedule_id, 
				  "1d", task_id, this.msp_ac1_zero_id, scheduleSettingDTO, "06:00", "12:00", task_type ,this.msp_ac1_zero_name,test);
		  
		  HashMap<String, Object> cloudDirectLocalBackupDTO=policy4SPOGServer.createCloudDirectLocalBackupDTO("*", "true", test);
		  HashMap<String, Object> cloudDirectImageBackupTaskInfoDTO=policy4SPOGServer.createCloudDirectImageBackupTaskInfoDTO("*", cloudDirectLocalBackupDTO, test);
		  ArrayList<HashMap<String,Object>>  destinations= policy4SPOGServer.createPolicyTaskDTO(null, task_id, task_type, this.msp_ac1_zero_id, "none", cloudDirectImageBackupTaskInfoDTO, null, null, test);
		  ArrayList<HashMap<String,Object>> throttles =policy4SPOGServer.createPolicyThrottleDTO(null, spogServer.returnRandomUUID(), task_id, "network", "1200", "1", "06:00", "18:00", task_type , this.msp_ac1_zero_id,this.msp_ac1_zero_name,test);
		  //spogServer.userLogin(this.csrAdmin, this.csrPwd);
		  String user_token=spogServer.getJWTToken();
		  policy4SPOGServer.setToken(user_token);
		  Response response=null;
		  
		  if(hasschedule){
			  if(hasthrottle){
				  response=policy4SPOGServer.createPolicy(policy_name, policy_description, policy_type, null, "true",  this.direct_source_id, destinations, schedules, throttles, policy_id, organization_id, test); 
			  }else{
				  response=policy4SPOGServer.createPolicy(policy_name, policy_description, policy_type, null, "true",  this.direct_source_id, destinations, schedules, null, policy_id, organization_id, test);
			  }
		  }else{
			  if(hasthrottle){
				  response=policy4SPOGServer.createPolicy(policy_name, policy_description, policy_type, null, "true",  this.direct_source_id, destinations, null, throttles, policy_id, organization_id, test);
			  }else{
				  response=policy4SPOGServer.createPolicy(policy_name, policy_description, policy_type, null, "true",  this.direct_source_id, destinations, null, null, policy_id, organization_id, test);
			  }
		  }		  	
		  policy4SPOGServer.checkPolicyWithErrorInfo(response,status,errorcode,errormes,test);
	  }	
	  
	  
	  //user has no permission to use the source and destination
	  @DataProvider(name = "loginUserToCreateDRPolicy4Permission")
	  public final Object[][] getLoginUserToCreateDRPolicy4Permission() {
			return new Object[][] { 
				{"msp",true,true},
				{"msp",true,false},
				{"account",true,true},
				{"account",true,false}};
	  }
	  @Test(dataProvider = "loginUserToCreateDRPolicy4Permission")
	  public void createPolicyWithDRPolicy4Permission(String loginUserType,Boolean hasschedule,Boolean hasthrottle){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  String schedule_id=spogServer.returnRandomUUID();
		  String task_id=spogServer.returnRandomUUID();
		  String task_type="cloud_direct_image_backup";
		  String policy_name=spogServer.ReturnRandom("policy_name");
		  String policy_description=spogServer.ReturnRandom("description");
		  String policy_id=spogServer.returnRandomUUID();
		  String policy_type="cloud_direct_draas";
		  if (loginUserType.equalsIgnoreCase("csr")){
			  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
		  }else if(loginUserType.equalsIgnoreCase("direct")){
			  spogServer.userLogin(direct_admin_email, password);
		  }else if(loginUserType.equalsIgnoreCase("msp")){
			  spogServer.userLogin(this.msp_admin_email, OrgPwdForPrepare,test);
		  }else if(loginUserType.equalsIgnoreCase("account")){
			  spogServer.userLogin(this.msp_ac1_email, OrgPwdForPrepare,test);
		  }
		  String organization_id=spogServer.GetLoggedinUserOrganizationID(); 		  
		  if (loginUserType.equalsIgnoreCase("csr")){
			  organization_id= this.direct_org_id;
		  }
		  HashMap<String, Object> cloudDirectScheduleDTO=policy4SPOGServer.createCloudDirectScheduleDTO("0 6 * * *", test);
		  HashMap<String, Object> scheduleSettingDTO=policy4SPOGServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null,test);
		  ArrayList<HashMap<String,Object>> schedules =policy4SPOGServer.createPolicyScheduleDTO(null,schedule_id, 
				  "1d", task_id, this.direct_zero_id1, scheduleSettingDTO, "06:00", "12:00", task_type ,this.msp_ac1_zero_name,test);
		  
		  HashMap<String, Object> cloudDirectLocalBackupDTO=policy4SPOGServer.createCloudDirectLocalBackupDTO("*", "true", test);
		  HashMap<String, Object> cloudDirectImageBackupTaskInfoDTO=policy4SPOGServer.createCloudDirectImageBackupTaskInfoDTO("*", cloudDirectLocalBackupDTO, test);
		  ArrayList<HashMap<String,Object>>  destinations= policy4SPOGServer.createPolicyTaskDTO(null, task_id, task_type, this.direct_zero_id1, "none", cloudDirectImageBackupTaskInfoDTO, null, null, test);
		  ArrayList<HashMap<String,Object>> throttles =policy4SPOGServer.createPolicyThrottleDTO(null, spogServer.returnRandomUUID(), task_id, "network", "1200", "1", "06:00", "18:00", task_type , this.direct_zero_id1,this.msp_ac1_zero_name,test);
		  //spogServer.userLogin(this.csrAdmin, this.csrPwd);
		  String user_token=spogServer.getJWTToken();
		  policy4SPOGServer.setToken(user_token);
		  Response response=null;
		  
		  if(hasschedule){
			  if(hasthrottle){
				  response=policy4SPOGServer.createPolicy(policy_name, policy_description, policy_type, null, "true",  this.direct_source_id, destinations, schedules, throttles, policy_id, organization_id, test);
				  policy4SPOGServer.checkPolicyWithErrorInfo(response,403,"00100101","The current user does not have permissions to manage the resource.",test);
			  }else{
				  response=policy4SPOGServer.createPolicy(policy_name, policy_description, policy_type, null, "true",  this.direct_source_id, destinations, schedules, null, policy_id, organization_id, test);
				  policy4SPOGServer.checkPolicyWithErrorInfo(response,403,"00100101","The current user does not have permissions to manage the resource.",test);
			  }
		  }else{
			  if(hasthrottle){
				  response=policy4SPOGServer.createPolicy(policy_name, policy_description, policy_type, null, "true",  this.direct_source_id, destinations, null, throttles, policy_id, organization_id, test);
				  policy4SPOGServer.checkPolicyWithErrorInfo(response,403,"00100101","Permission required to manage the resource for current user",test);
			  }else{
				  response=policy4SPOGServer.createPolicy(policy_name, policy_description, policy_type, null, "true",  this.direct_source_id, destinations, null, null, policy_id, organization_id, test);
				  policy4SPOGServer.checkPolicyWithErrorInfo(response,403,"00100101","Permission required to manage the resource for current user",test);
			  }
		  }		  	  
	  }	
	  
    @DataProvider(name = "loginUserToCreateDRPolicy")
	  public final Object[][] getLoginUserToCreateDRPolicy() {
			return new Object[][] { 
				{"direct",true,true},
				{"direct",true,false},
				{"direct",false,true},
				{"direct",false,false},
				{"csr",true,true},
				{"csr",true,false},
				{"csr",false,true},
				{"csr",false,false}
				};
	  }
	  //@Test(dataProvider = "loginUserToCreateDRPolicy")
	  public void createPolicyWithDRPolicy(String loginUserType,Boolean hasschedule,Boolean hasthrottle){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  String schedule_id=spogServer.returnRandomUUID();
		  String task_id=spogServer.returnRandomUUID();
		  String task_type="cloud_direct_image_backup";
		  String policy_name=spogServer.ReturnRandom("policy_name");
		  String policy_description=spogServer.ReturnRandom("description");
		  String policy_id=spogServer.returnRandomUUID();
		  String policy_type="cloud_direct_draas";
		  if (loginUserType.equalsIgnoreCase("csr")){
			  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
		  }else if(loginUserType.equalsIgnoreCase("direct")){
			  spogServer.userLogin(direct_admin_email1, password);
		  }else if(loginUserType.equalsIgnoreCase("msp")){
			  spogServer.userLogin(this.msp_admin_email1, OrgPwdForPrepare,test);
		  }else if(loginUserType.equalsIgnoreCase("account")){
			  spogServer.userLogin(this.msp_ac1_email, OrgPwdForPrepare,test);
		  }
		  String organization_id=spogServer.GetLoggedinUserOrganizationID();	
		  if(loginUserType.equalsIgnoreCase("msp")){
			  organization_id=this.msp_account_org_id_1;
		  }else if(loginUserType.equalsIgnoreCase("csr")){
			  organization_id=this.direct_org_id1;
		  }
		  HashMap<String, Object> cloudDirectScheduleDTO=policy4SPOGServer.createCloudDirectScheduleDTO("0 6 * * *", test);
		  HashMap<String, Object> scheduleSettingDTO=policy4SPOGServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null,test);
		  ArrayList<HashMap<String,Object>> schedules =policy4SPOGServer.createPolicyScheduleDTO(null,schedule_id, 
				  "1d", task_id, this.direct_zero_id1, scheduleSettingDTO, "06:00", "12:00", task_type ,this.msp_ac1_zero_name,test);
		  
//		  HashMap<String, Object> cloudDirectLocalBackupDTO=policy4SPOGServer.createCloudDirectLocalBackupDTO("*", "true", test);
//		  HashMap<String, Object> cloudDirectImageBackupTaskInfoDTO=policy4SPOGServer.createCloudDirectImageBackupTaskInfoDTO("*", cloudDirectLocalBackupDTO, test);
		  HashMap<String, Object> cloudDirectLocalBackupDTO=policy4SPOGServer.createCloudDirectLocalBackupDTO("c:\\tmp", "true", test);
		  HashMap<String, Object> cloudDirectImageBackupTaskInfoDTO=policy4SPOGServer.createCloudDirectImageBackupTaskInfoDTO("c:\\tmp1", cloudDirectLocalBackupDTO, test);
		  ArrayList<HashMap<String,Object>>  destinations= policy4SPOGServer.createPolicyTaskDTO(null, task_id, task_type, this.direct_zero_id1, "none", cloudDirectImageBackupTaskInfoDTO, null, null, test);
		  ArrayList<HashMap<String,Object>> throttles =policy4SPOGServer.createPolicyThrottleDTO(null, spogServer.returnRandomUUID(), task_id, "network", "1200", "1", "06:00", "18:00", task_type , this.direct_zero_id1,this.msp_ac1_zero_name,test);
		  String user_token=spogServer.getJWTToken();
		  policy4SPOGServer.setToken(user_token);
		  Response response=null;
		  
		  if(hasschedule){
			  if(hasthrottle){
				  response=policy4SPOGServer.createPolicy(policy_name, policy_description, policy_type, null, "true",  this.direct_source_id, destinations, schedules, throttles, policy_id, organization_id, test);
				  policy4SPOGServer.checkPolicyThrottles(response,SpogConstants.SUCCESS_POST,throttles,test);
			  }else{
				  response=policy4SPOGServer.createPolicy(policy_name, policy_description, policy_type, null, "true",  this.direct_source_id, destinations, schedules, null, policy_id, organization_id, test);
			  }
			  policy4SPOGServer.checkPolicySchedules(response,SpogConstants.SUCCESS_POST,schedules,test);
			  policy4SPOGServer.checkPolicyDestinations(response,SpogConstants.SUCCESS_POST,destinations,test);
			  //policy4SPOGServer.checkPolicyCommon(response, SpogConstants.SUCCESS_POST, policy_name, policy_description, policy_type, null, "true", "deploying", this.direct_source_id, policy_id, organization_id, test);
			  String check_policyStatus=response.then().extract().path("data.policy_status");
			  for(int loop=1;loop<60;loop++){
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
			  spogServer.userLogin(direct_admin_email1, password);
			  user_token=spogServer.getJWTToken();
			  if(check_policyStatus!=null){
				  if(check_policyStatus.equalsIgnoreCase("failure")){
					  check_not_job_status="finished";
				  }else{
					  check_not_job_status="failed";
				  }
				  response = spogServer.getJobs(user_token, "", test);
				  ArrayList<Map<String, Object>> jobs = response.then().extract().path("data");
				  ArrayList<Map<String, Object>> policis = response.then().extract().path("data");
				  String jobID=jobs.get(0).get("job_id").toString();
				  String jobStatus=jobs.get(0).get("job_status").toString();
				  
				  String ret_policyId=policis.get(0).get("policy_id").toString();
				  assertEquals(ret_policyId, policy_id,"check return policy id is right");
				  assertNotEquals(jobStatus, check_not_job_status,"check return job status is right");
			  }
			  
			  Response res=policy4SPOGServer.getSourcesUnderOrgByPolicyType(organization_id, policy_type, test);
			  policy4SPOGServer.checkSourcesUnderOrgByPolicyType(res, organization_id, this.direct_org_name1, policy_name, policy_id, this.direct_source_id, this.direct_source_name, test);
			  policy4SPOGServer.removeSourceFromPolicy(this.direct_source_id, test);
			  res=policy4SPOGServer.getSourcesUnderOrgByPolicyType(organization_id, policy_type, test);
			  policy4SPOGServer.checkSourcesUnderOrgByPolicyType(res, organization_id, this.direct_org_name1,  this.direct_source_id, this.direct_source_name, test);
			  policy4SPOGServer.deletePolicybyPolicyId(user_token, policy_id, 200, null, test);
		  }else{
			  if(hasthrottle){
				  response=policy4SPOGServer.createPolicy(policy_name, policy_description, policy_type, null, "true",  this.direct_source_id, destinations, null, throttles, policy_id, organization_id, test);
//				  /policy4SPOGServer.checkPolicyThrottles(response,SpogConstants.SUCCESS_POST,throttles,test);
			  }else{
				  response=policy4SPOGServer.createPolicy(policy_name, policy_description, policy_type, null, "true",  this.direct_source_id, destinations, null, null, policy_id, organization_id, test);
			  }
			  policy4SPOGServer.checkPolicyWithErrorInfo(response,400,"00E00011","Schedule is required for [cloud_direct_draas] policy.",test);
		  }
		  //Response response=policy4SPOGServer.createPolicy(policy_name, policy_description, policy_type, null, "true", "successful", source_id_1, destinations, schedules, throttle, policy_id, organization_id, test);
	  }	

	  
	  @DataProvider(name = "loginUserToCreateDifferentThrottleSetting")
	  public final Object[][] getLoginUserToCreateDifferentThrottleSetting() {
			return new Object[][] { {"csr","none","network","1200", "1", "06:00", "18:00"},{"csr","random","network","1200", "2", "07:00", "23:00"},
			{"direct","none","network","1200", "1", "06:00", "18:00"},{"direct","random","network","1200", "2", "07:00", "23:00"}};
	  }
	  //@Test(dataProvider = "loginUserToCreateDifferentThrottleSetting")
	  public void createPolicyWithThrottleSetting(String loginUserType,String throttle_id,String throttle_type,
			  String rate,String days_of_week,String start_time,String end_time){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  String policy_id=spogServer.returnRandomUUID();
		  String schedule_id=spogServer.returnRandomUUID();
		  String task_id=spogServer.returnRandomUUID();
		  if(throttle_id.equalsIgnoreCase("random")){
			  throttle_id=spogServer.returnRandomUUID();
		  }
		  String task_type="cloud_direct_image_backup";
		  String policy_name=spogServer.ReturnRandom("test");
		  String policy_description=spogServer.ReturnRandom("description");
		  String policy_type="cloud_direct_draas";
		  
		  if (loginUserType.equalsIgnoreCase("csr")){
			  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
		  }else if(loginUserType.equalsIgnoreCase("direct")){
			  spogServer.userLogin(direct_admin_email1, password);
		  }else if(loginUserType.equalsIgnoreCase("msp")){
			  spogServer.userLogin(this.msp_admin_email, OrgPwdForPrepare,test);
		  }else if(loginUserType.equalsIgnoreCase("account")){
			  spogServer.userLogin(this.msp_ac1_email, OrgPwdForPrepare,test);
		  }
		  String organization_id=spogServer.GetLoggedinUserOrganizationID();
		  if(loginUserType.equalsIgnoreCase("msp")){
			  organization_id=this.msp_account_org_id_1;
		  }else if(loginUserType.equalsIgnoreCase("csr")){
			  organization_id=this.direct_org_id1;
		  }
		  HashMap<String, Object> cloudDirectScheduleDTO=policy4SPOGServer.createCloudDirectScheduleDTO("0 6 * * *", test);
		  HashMap<String, Object> scheduleSettingDTO=policy4SPOGServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null,test);
		  ArrayList<HashMap<String,Object>> schedules =policy4SPOGServer.createPolicyScheduleDTO(null,schedule_id, 
				  "1d", task_id, this.direct_zero_id1, scheduleSettingDTO, "06:00", "12:00", task_type ,this.msp_ac1_zero_name,test);
		  HashMap<String, Object> cloudDirectLocalBackupDTO=policy4SPOGServer.createCloudDirectLocalBackupDTO("d:\\tmp", "true", test);
		  HashMap<String, Object> cloudDirectImageBackupTaskInfoDTO=policy4SPOGServer.createCloudDirectImageBackupTaskInfoDTO("*", cloudDirectLocalBackupDTO, test);
		  ArrayList<HashMap<String,Object>>  destinations= policy4SPOGServer.createPolicyTaskDTO(null, task_id, task_type, this.direct_zero_id1, "none", cloudDirectImageBackupTaskInfoDTO, null, null, test);
		  
		  ArrayList<HashMap<String,Object>> throttles =policy4SPOGServer.createPolicyThrottleDTO(null, throttle_id, task_id, throttle_type, rate, days_of_week, start_time, end_time,task_type , this.direct_zero_id1,this.msp_ac1_zero_name,test);
		  spogServer.userLogin(this.csrAdmin, this.csrPwd);
		  String user_token=spogServer.getJWTToken();
		  policy4SPOGServer.setToken(user_token);
		  //Response response=policy4SPOGServer.createPolicy(policy_name, policy_description, policy_type, null, "true", "successful", source_id_1, destinations, schedules, throttle, policy_id, organization_id, test);
		  Response response=policy4SPOGServer.createPolicy(policy_name, policy_description, policy_type, null, "true",  null, destinations, schedules, throttles, policy_id, organization_id, test);
		  policy4SPOGServer.checkPolicyDestinations(response,SpogConstants.SUCCESS_POST,destinations,test);
		  policy4SPOGServer.checkPolicyThrottles(response,SpogConstants.SUCCESS_POST,throttles,test);
		  policy4SPOGServer.checkPolicyCommon(response, SpogConstants.SUCCESS_POST, policy_name, policy_description, policy_type, null, "true", "success", null, policy_id, organization_id, test);
		  //policy4SPOGServer.checkPolicySchedules(response,SpogConstants.SUCCESS_POST,schedules,test);
		  policy4SPOGServer.deletePolicybyPolicyId(user_token, policy_id, 200, null, test);
	  }	  

	  @DataProvider(name = "loginUserToCreateNegativeCustomScheduleSetting")
	  public final Object[][] getLoginUserToCreateNegativeCustomScheduleSetting() {
			return new Object[][] { {"csr","custom"}};
	  }
	  @Test(dataProvider = "loginUserToCreateNegativeCustomScheduleSetting")
	  public void createPolicyWithNegativeScheduleType(String loginUserType,String schedule_type){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  String policy_id=spogServer.returnRandomUUID();
		  String task_id=spogServer.returnRandomUUID();
		  String throttle_id=spogServer.returnRandomUUID();
		  String policy_name=spogServer.ReturnRandom("test");
		  String policy_description=spogServer.ReturnRandom("description");
		  String task_type="cloud_direct_image_backup";
		  String policy_type="cloud_direct_draas";
		  if (loginUserType.equalsIgnoreCase("csr")){
			  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
		  }else if(loginUserType.equalsIgnoreCase("direct")){
			  spogServer.userLogin(direct_admin_email1, password);
		  }else if(loginUserType.equalsIgnoreCase("msp")){
			  spogServer.userLogin(this.msp_admin_email, OrgPwdForPrepare,test);
		  }else if(loginUserType.equalsIgnoreCase("account")){
			  spogServer.userLogin(this.msp_ac1_email, OrgPwdForPrepare,test);
		  }
		  String organization_id=spogServer.GetLoggedinUserOrganizationID();
		  if (loginUserType.equalsIgnoreCase("csr")){
			  organization_id= this.direct_org_id1;
		  }
		  HashMap<String, Object> CustomScheduleDTO=policy4SPOGServer.createCustomScheduleDTO("1524469596000", "full","1","true","10","minutes",test);
		  HashMap<String, Object> scheduleSettingDTO=policy4SPOGServer.createScheduleSettingDTO( null,CustomScheduleDTO,test);
		  ArrayList<HashMap<String,Object>> schedules =policy4SPOGServer.createPolicyScheduleDTO(null,spogServer.returnRandomUUID(), 
				  schedule_type, task_id, this.direct_zero_id1, scheduleSettingDTO, "06:00", "12:00", task_type ,this.msp_ac1_zero_name,test);
		  
		  HashMap<String, Object> cloudDirectLocalBackupDTO=policy4SPOGServer.createCloudDirectLocalBackupDTO("d:\\tmp", "true", test);
		  HashMap<String, Object> cloudDirectImageBackupTaskInfoDTO=policy4SPOGServer.createCloudDirectImageBackupTaskInfoDTO("*", cloudDirectLocalBackupDTO, test);
		  ArrayList<HashMap<String,Object>>  destinations= policy4SPOGServer.createPolicyTaskDTO(null, task_id, task_type, this.direct_zero_id1, "none", cloudDirectImageBackupTaskInfoDTO, null, null, test);
		  
		  String user_token=spogServer.getJWTToken();
		  policy4SPOGServer.setToken(user_token);
		  ArrayList<HashMap<String,Object>> throttles =policy4SPOGServer.createPolicyThrottleDTO(null, throttle_id, task_id, "network", "1200", "1", "06:00", "18:00",task_type ,this.direct_zero_id1,this.msp_ac1_zero_name, test);
		  Response response=policy4SPOGServer.createPolicy(policy_name, policy_description, policy_type, null, "true", this.direct_source_id, destinations, schedules, throttles, policy_id, organization_id, test);
		  policy4SPOGServer.checkPolicyWithErrorInfo(response,403,"00E00059","Can not create policy due to organization expired.",test);	
	  }
	  
	  @DataProvider(name = "loginUserToCreateSourceId")
	  public final Object[][] getLoginUserToCreateSourceId() {
			return new Object[][] {
				{"csr","random","00100201","Unable to find resource with ID"},
				{"csr",this.direct_source_2000,"00100201","Unable to find resource with"},
				{"csr",this.direct_source_2003,"00100201","Unable to find resource with"},
				{"csr",this.direct_source_win10,"00100201","Unable to find resource with"},
				{"csr",this.direct_source_win7,"00100201","Unable to find resource with"},
				{"csr",this.direct_source_xp,"00100201","Unable to find resource with"},
				{"direct","random","00100201","Unable to find resource with ID"},
				{"direct",this.direct_source_2000,"00100201","Unable to find resource with"},
				{"direct",this.direct_source_2003,"00100201","Unable to find resource with"},
				{"direct",this.direct_source_win10,"00100201","Unable to find resource with"},
				{"direct",this.direct_source_win7,"00100201","Unable to find resource with"},
				{"direct",this.direct_source_xp,"00100201","Unable to find resource with"}};
	  }
	  @Test(dataProvider = "loginUserToCreateSourceId")
	  public void createPolicyWithNegativeSourceId(String loginUserType,String source_id,
			  String errorcode, String message){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  int status=404;
		  String policy_id=spogServer.returnRandomUUID();
		  if(source_id.equalsIgnoreCase("random")){
			  source_id=spogServer.returnRandomUUID();
			  status=404;
		  }
		  String task_id=spogServer.returnRandomUUID();
		  String throttle_id=spogServer.returnRandomUUID();
		  String policy_name=spogServer.ReturnRandom("test");
		  String policy_description=spogServer.ReturnRandom("description");
		  String task_type="cloud_direct_image_backup";
		  String policy_type="cloud_direct_draas";
		  if (loginUserType.equalsIgnoreCase("csr")){
			  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
		  }else if(loginUserType.equalsIgnoreCase("direct")){
			  spogServer.userLogin(direct_admin_email1, password);
		  }else if(loginUserType.equalsIgnoreCase("msp")){
			  spogServer.userLogin(this.msp_admin_email, OrgPwdForPrepare,test);
		  }else if(loginUserType.equalsIgnoreCase("account")){
			  spogServer.userLogin(this.msp_ac1_email, OrgPwdForPrepare,test);
		  }
		  String organization_id=spogServer.GetLoggedinUserOrganizationID();
		  if(loginUserType.equalsIgnoreCase("msp")){
			  organization_id=this.msp_account_org_id_1;
		  }else if(loginUserType.equalsIgnoreCase("csr")){
			  organization_id=this.direct_org_id1;
		  }
		  HashMap<String, Object> CustomScheduleDTO=policy4SPOGServer.createCustomScheduleDTO("1524469596000", "full","1","true","10","minutes",test);
		  HashMap<String, Object> cloudDirectScheduleDTO=policy4SPOGServer.createCloudDirectScheduleDTO("0 6 * * *", test);
		  HashMap<String, Object> scheduleSettingDTO=policy4SPOGServer.createScheduleSettingDTO( cloudDirectScheduleDTO,CustomScheduleDTO,test);
		  ArrayList<HashMap<String,Object>> schedules =policy4SPOGServer.createPolicyScheduleDTO(null,spogServer.returnRandomUUID(), 
				  "1d", task_id, this.direct_zero_id1, scheduleSettingDTO, "06:00", "12:00", task_type ,this.msp_ac1_zero_name,test);	  
		  
		  HashMap<String, Object> cloudDirectLocalBackupDTO=policy4SPOGServer.createCloudDirectLocalBackupDTO("d:\\tmp", "true", test);
		  HashMap<String, Object> cloudDirectImageBackupTaskInfoDTO=policy4SPOGServer.createCloudDirectImageBackupTaskInfoDTO("*", cloudDirectLocalBackupDTO, test);
		  
//		  ArrayList<HashMap<String,Object>>  destinations= policy4SPOGServer.createPolicyTaskDTO(null, task_id, task_type, destination_normal_id, "none", null, null, udpReplicationFromRemoteInfoDTO, test);
		  ArrayList<HashMap<String,Object>>  destinations= policy4SPOGServer.createPolicyTaskDTO(null, task_id, task_type, this.direct_zero_id1, "none", cloudDirectImageBackupTaskInfoDTO, null, null, test);
		  
		  String user_token=spogServer.getJWTToken();
		  policy4SPOGServer.setToken(user_token);
		  ArrayList<HashMap<String,Object>> throttles =policy4SPOGServer.createPolicyThrottleDTO(null, throttle_id, task_id, "network", "1200", "1", "06:00", "18:00",task_type ,this.direct_zero_id1,this.msp_ac1_zero_name, test);
		  
		  //Response response=policy4SPOGServer.createPolicy(policy_name, policy_description, policy_type, null, "true", "successful", source_id_1, destinations, schedules, throttle, policy_id, organization_id, test);
		  Response response=policy4SPOGServer.createPolicy(policy_name, policy_description, policy_type, null, "true", source_id, destinations, schedules, throttles, policy_id, organization_id, test);
		  if(errorcode.equalsIgnoreCase("201")){
			  response.then().statusCode(201);
		  }else{
			  policy4SPOGServer.checkPolicyWithErrorInfo(response,status,errorcode,message,test);	
		  }		  
	  }
	  
	  
	  
	  @DataProvider(name = "loginUserToCreateDestinationId")
	  public final Object[][] getLoginUserToCreateDestinationId() {
			return new Object[][] {
				{"direct","diff",404,"00C00001","Unable to find destination with ID"}, 
				{"direct",this.recycle_zero_id,404,"00C00001","Unable to find destination with ID"},
				{"direct",this.direct_normal_id1,404,"00C00001","Unable to find destination with ID"},
				{"direct","random",404,"00C00001","Unable to find destination with ID"}};
	  }
	  @Test(dataProvider = "loginUserToCreateDestinationId")
	  public void createPolicyWithNegativeDestinationId(String loginUserType,String destionation_id,
			  int status,String errorcode, String message){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  String policy_id=spogServer.returnRandomUUID();
		  String throttle_dest_id=destionation_id;
		  String schedule_dest_id=destionation_id;
		  if(destionation_id!=null && destionation_id!=""){
			  if(destionation_id.equalsIgnoreCase("random")){
				  destionation_id=spogServer.returnRandomUUID();
				  throttle_dest_id=destionation_id;
				  schedule_dest_id=destionation_id;
			  }else if(destionation_id.equalsIgnoreCase("diff")){
				  destionation_id=this.direct_zero_id1;
				  schedule_dest_id=this.direct_zero_id;
				  throttle_dest_id=this.direct_zero_id1;
			  }
		  }
		  String schedule_id=spogServer.returnRandomUUID();
		  String task_id=spogServer.returnRandomUUID();
		  String throttle_id=spogServer.returnRandomUUID();
		  String policy_name=spogServer.ReturnRandom("test");
		  String policy_description=spogServer.ReturnRandom("description");
		  String task_type="cloud_direct_image_backup";
		  String policy_type="cloud_direct_draas";
		  if (loginUserType.equalsIgnoreCase("csr")){
			  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
		  }else if(loginUserType.equalsIgnoreCase("direct")){
			  spogServer.userLogin(direct_admin_email1, password);
		  }else if(loginUserType.equalsIgnoreCase("msp")){
			  spogServer.userLogin(this.msp_admin_email, OrgPwdForPrepare,test);
		  }else if(loginUserType.equalsIgnoreCase("account")){
			  spogServer.userLogin(this.msp_ac1_email, OrgPwdForPrepare,test);
		  }
		  String organization_id=spogServer.GetLoggedinUserOrganizationID();
		  if(loginUserType.equalsIgnoreCase("msp")){
			  organization_id=this.msp_account_org_id_1;
		  }else if(loginUserType.equalsIgnoreCase("csr")){
			  organization_id=this.direct_org_id1;
		  }
		  HashMap<String, Object> cloudDirectScheduleDTO=policy4SPOGServer.createCloudDirectScheduleDTO("0 6 * * *", test);
		  HashMap<String, Object> scheduleSettingDTO=policy4SPOGServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null,test);
		  ArrayList<HashMap<String,Object>> schedules =policy4SPOGServer.createPolicyScheduleDTO(null,schedule_id, 
				  "1d", task_id, schedule_dest_id, scheduleSettingDTO, "06:00", "12:00", task_type ,destination_name,test);
		  HashMap<String, Object> cloudDirectLocalBackupDTO=policy4SPOGServer.createCloudDirectLocalBackupDTO("d:\\tmp", "true", test);
		  HashMap<String, Object> cloudDirectImageBackupTaskInfoDTO=policy4SPOGServer.createCloudDirectImageBackupTaskInfoDTO("*", cloudDirectLocalBackupDTO, test);
		  ArrayList<HashMap<String,Object>>  destinations= policy4SPOGServer.createPolicyTaskDTO(null, task_id, task_type, destionation_id, "none", cloudDirectImageBackupTaskInfoDTO, null, null, test);
		  
		  String user_token=spogServer.getJWTToken();
		  policy4SPOGServer.setToken(user_token);
		  ArrayList<HashMap<String,Object>> throttles =policy4SPOGServer.createPolicyThrottleDTO(null, throttle_id, task_id, "network", "1200", "1", "06:00", "18:00",task_type ,throttle_dest_id,this.msp_ac1_zero_name, test);
		  
		  //Response response=policy4SPOGServer.createPolicy(policy_name, policy_description, policy_type, null, "true", "successful", source_id_1, destinations, schedules, throttle, policy_id, organization_id, test);
		  Response response=policy4SPOGServer.createPolicy(policy_name, policy_description, policy_type, null, "true", this.direct_source_id, destinations, schedules, throttles, policy_id, organization_id, test);
		  policy4SPOGServer.checkPolicyWithErrorInfo(response,status,errorcode,message,test);	
	  }
	  
	  @DataProvider(name = "loginUserToCreateTaskId")
	  public final Object[][] getLoginUserToCreateTaskId() {
			return new Object[][] { {"csr","existing",403,"00E00059","Can not create policy due to organization expired."},
			{"csr","diff",403,"00E00059","Can not create policy due to organization expired."},
			{"csr","",400,"40000001","The taskId cannot be blank"},
			{"csr",null,400,"40000001","The taskId cannot be blank"},
			{"csr","invalid",400,"00100001","Invalid input parameter"},
			{"direct","",400,"40000001","The taskId cannot be blank"},
			{"direct",null,400,"40000001","The taskId cannot be blank"},
			{"direct","invalid",400,"00100001","Invalid input parameter"}};
	  }
	  @Test(dataProvider = "loginUserToCreateTaskId")
	  public void createPolicyWithNegativeTaskId(String loginUserType,String task_id,int status,
			  String errorcode, String message){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  String policy_id=spogServer.returnRandomUUID();
		  String throttle_task_id=task_id;
		  String schedule_task_id=task_id;
		  if(task_id!=null && task_id!=""){
			  if(task_id.equalsIgnoreCase("existing")){
				  task_id=existing_task_id;
				  throttle_task_id=task_id;
				  schedule_task_id=task_id;
			  }else if(task_id.equalsIgnoreCase("diff")){
				  task_id=spogServer.returnRandomUUID();
				  throttle_task_id=task_id;
				  schedule_task_id=spogServer.returnRandomUUID();
			  }
		  }
		  String schedule_id=spogServer.returnRandomUUID();
		  String throttle_id=spogServer.returnRandomUUID();
		  String policy_name=spogServer.ReturnRandom("test");
		  String policy_description=spogServer.ReturnRandom("description");
		  String task_type="cloud_direct_image_backup";
		  String policy_type="cloud_direct_draas";
		  if (loginUserType.equalsIgnoreCase("csr")){
			  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
		  }else if(loginUserType.equalsIgnoreCase("direct")){
			  spogServer.userLogin(direct_admin_email1, password);
		  }else if(loginUserType.equalsIgnoreCase("msp")){
			  spogServer.userLogin(this.msp_admin_email, OrgPwdForPrepare,test);
		  }else if(loginUserType.equalsIgnoreCase("account")){
			  spogServer.userLogin(this.msp_ac1_email, OrgPwdForPrepare,test);
		  }
		  String organization_id=spogServer.GetLoggedinUserOrganizationID();
		  if (loginUserType.equalsIgnoreCase("csr")){
			  organization_id= this.direct_org_id1;
		  }
		  HashMap<String, Object> cloudDirectScheduleDTO=policy4SPOGServer.createCloudDirectScheduleDTO("0 6 * * *", test);
		  HashMap<String, Object> scheduleSettingDTO=policy4SPOGServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null,test);
		  ArrayList<HashMap<String,Object>> schedules =policy4SPOGServer.createPolicyScheduleDTO(null,schedule_id, 
				  "1d", schedule_task_id, this.direct_zero_id1, scheduleSettingDTO, "06:00", "12:00", task_type ,this.msp_ac1_zero_name,test);
		  HashMap<String, Object> cloudDirectLocalBackupDTO=policy4SPOGServer.createCloudDirectLocalBackupDTO("d:\\tmp", "true", test);
		  HashMap<String, Object> cloudDirectImageBackupTaskInfoDTO=policy4SPOGServer.createCloudDirectImageBackupTaskInfoDTO("*", cloudDirectLocalBackupDTO, test);
		  
		  ArrayList<HashMap<String,Object>>  destinations= policy4SPOGServer.createPolicyTaskDTO(null, task_id, task_type, this.direct_zero_id1, "none", cloudDirectImageBackupTaskInfoDTO, null, null, test);
		  String user_token=spogServer.getJWTToken();
		  policy4SPOGServer.setToken(user_token);
		  ArrayList<HashMap<String,Object>> throttles =policy4SPOGServer.createPolicyThrottleDTO(null, throttle_id, throttle_task_id, "network", "1200", "1", "06:00", "18:00",task_type ,this.direct_zero_id1,this.msp_ac1_zero_name, test);
		  
		  //Response response=policy4SPOGServer.createPolicy(policy_name, policy_description, policy_type, null, "true", "successful", source_id_1, destinations, schedules, throttle, policy_id, organization_id, test);
		  Response response=policy4SPOGServer.createPolicy(policy_name, policy_description, policy_type, null, "true", this.direct_source_id, destinations, schedules, throttles, policy_id, organization_id, test);
		  policy4SPOGServer.checkPolicyWithErrorInfo(response,status,errorcode,message,test);	
	  }
	  
	  @DataProvider(name = "loginUserToCreateDRPolicy4NegativeOrgid")
	  public final Object[][] getLoginUserToCreateDRPolicy4NegativeOrgid() {
			return new Object[][] { 
				{"csr",this.msp_account_org_id_1},
				{"csr",this.msp_account_org_id_2},
				{"csr",this.msp_org_id},
				{"direct",this.msp_account_org_id_1},
				{"direct",this.msp_account_org_id_2},
				{"direct",this.msp_org_id}
				};
	  }
	  //@Test(dataProvider = "loginUserToCreateDRPolicy4NegativeOrgid")
	  public void createPolicyWithDRPolicy4NegativeOrgid(String loginUserType,String organization_id){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  String schedule_id=spogServer.returnRandomUUID();
		  String task_id=spogServer.returnRandomUUID();
		  String task_type="cloud_direct_image_backup";
		  String policy_name=spogServer.ReturnRandom("policy_name");
		  String policy_description=spogServer.ReturnRandom("description");
		  String policy_id=spogServer.returnRandomUUID();
		  String policy_type="cloud_direct_draas";
		  String errorcode=null,message=null;
		  int status=404;
		  errorcode="00E0004A";
		  message="Source, destination and policy are not in the same organization.";
		  if (loginUserType.equalsIgnoreCase("csr")){
			  status=403;
			  errorcode="00E00059";
			  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
			  message="Source, destination and policy are not in the same organization";
		  }else if(loginUserType.equalsIgnoreCase("direct")){
			  status=403;
			  errorcode="00100101";
			  spogServer.userLogin(direct_admin_email1, password);
			  message="The current user does not have permissions to manage the resource.";
		  }else if(loginUserType.equalsIgnoreCase("msp")){
			  spogServer.userLogin(this.msp_admin_email, OrgPwdForPrepare,test);
			  message="Permission required to manage the resource for current user";
		  }else if(loginUserType.equalsIgnoreCase("account")){
			  spogServer.userLogin(this.msp_ac1_email, OrgPwdForPrepare,test);
			  message="Permission required to manage the resource for current user";
		  }
		  HashMap<String, Object> cloudDirectScheduleDTO=policy4SPOGServer.createCloudDirectScheduleDTO("0 6 * * *", test);
		  HashMap<String, Object> scheduleSettingDTO=policy4SPOGServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null,test);
		  ArrayList<HashMap<String,Object>> schedules =policy4SPOGServer.createPolicyScheduleDTO(null,schedule_id, 
				  "1d", task_id, this.direct_zero_id1, scheduleSettingDTO, "06:00", "12:00", task_type ,this.msp_ac1_zero_name,test);
		  
		  HashMap<String, Object> cloudDirectLocalBackupDTO=policy4SPOGServer.createCloudDirectLocalBackupDTO("*", "true", test);
		  HashMap<String, Object> cloudDirectImageBackupTaskInfoDTO=policy4SPOGServer.createCloudDirectImageBackupTaskInfoDTO("*", cloudDirectLocalBackupDTO, test);
		  ArrayList<HashMap<String,Object>>  destinations= policy4SPOGServer.createPolicyTaskDTO(null, task_id, task_type, this.direct_zero_id1, "none", cloudDirectImageBackupTaskInfoDTO, null, null, test);
		  ArrayList<HashMap<String,Object>> throttles =policy4SPOGServer.createPolicyThrottleDTO(null, spogServer.returnRandomUUID(), task_id, "network", "1200", "1", "06:00", "18:00", task_type , this.direct_zero_id1,this.msp_ac1_zero_name,test);
		  //spogServer.userLogin(this.csrAdmin, this.csrPwd);
		  String user_token=spogServer.getJWTToken();
		  policy4SPOGServer.setToken(user_token);
		  Response response=null;
		  response=policy4SPOGServer.createPolicy(policy_name, policy_description, policy_type, null, "true",  this.direct_source_id, destinations, schedules, throttles, policy_id, organization_id, test);
		  policy4SPOGServer.checkPolicyWithErrorInfo(response,status,errorcode,message,test);	
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
