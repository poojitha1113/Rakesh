package api.policies;

import static org.testng.Assert.assertTrue;

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

import InvokerServer.GatewayServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class PostPoliciesByTypeUnderSourcesTest extends base.prepare.PrepareOrgInfo{
	@Parameters({ "pmfKey"})
	public PostPoliciesByTypeUnderSourcesTest(String pmfKey) {
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
	  private String direct_org_id="ee7390ba-e2e9-4217-af4a-fb4375ea5654";
	  private String direct_org_name="arcserve163";
	  private String direct_admin_email="belly_sj@163.com";
	  private String cloud_direct_account_id="debf660e-4d83-413f-93c2-74744a2f2d8e";
	  private String direct_zero_id="fc400b17-9cbe-450a-84b4-21193726f625";
	  private String direct_normal_id="1678fba4-51b0-423e-8ff6-71bd7510be13";
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
	  
	  private String direct_source_id="90ad0cf4-6cb3-4d08-b33e-530f44818263";
	  private String direct_source_name="LinuxCIM2";
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
	  private String zhao_user="zhaoguo.ma@arcserve.com";
	  private String zhao_pwd="Zetta1234";
	  private String vm_id="999cd9d3-ca98-47c7-ae13-5887a3db2484";
	
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
	  
	  @DataProvider(name = "loginUserToGetPolicy4ValidSource")
	  public final Object[][] getLoginUserToGetPolicy4ValidSource() {
			return new Object[][] { 
//				{"csr",this.direct_source_id+","+this.direct_source_win10},
//				{"csr",this.direct_source_win10+","+this.direct_source_id},
//				{"csr",this.direct_source_id+","+this.direct_source_2003},
//				{"csr",this.direct_source_2003+","+this.direct_source_xp},
//				{"csr",this.direct_source_xp+","+direct_source_2000},
//				{"csr",this.direct_source_2000},
//				{"direct",this.direct_source_2003},
//				{"direct",this.direct_source_xp+","+this.direct_source_win10},
//				{"direct",this.direct_source_2000+","+this.direct_source_xp},
//				{"direct",this.direct_source_win7+","+this.direct_source_2003},
//				{"direct",this.direct_source_win10+","+this.direct_source_win7},
//				{"direct",this.direct_source_id+","+this.direct_source_2003},
				{"msp",this.direct_source_2003},
				{"msp",this.direct_source_xp+","+this.direct_source_win10},
				{"msp",this.direct_source_2000+","+this.direct_source_xp},
				{"msp",this.direct_source_win7+","+this.direct_source_2003},
				{"msp",this.direct_source_win10+","+this.direct_source_win7},

				{"account",this.direct_source_2003},
				{"account",this.direct_source_xp+","+this.direct_source_win10},
				{"account",this.direct_source_2000+","+this.direct_source_xp},
				{"account",this.direct_source_win7+","+this.direct_source_2003},
				{"account",this.direct_source_win10+","+this.direct_source_win7},
				};
	  }
	  @Test(dataProvider = "loginUserToGetPolicy4ValidSource")
	  public void getPolicy4ValidSource(String loginUserType,String source_id){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  Response response=null;
		  ArrayList<HashMap<String, Object>> sourceColumnsInfo=null;
		  if (loginUserType.equalsIgnoreCase("csr")){			 
			  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
		  }else if(loginUserType.equalsIgnoreCase("direct")){
			  spogServer.userLogin(direct_admin_email1, direct_password);
		  }else if(loginUserType.equalsIgnoreCase("msp")){
			  spogServer.userLogin(this.msp_admin_email, OrgPwdForPrepare,test);
		  }else if(loginUserType.equalsIgnoreCase("account")){
			  spogServer.userLogin(this.msp_ac1_email, OrgPwdForPrepare,test);
		  }
		  policy4SPOGServer.setToken(spogServer.getJWTToken());
		  response=policy4SPOGServer.postPoliciesByTypeUnderSources(source_id,  test);
		  if ((loginUserType.equalsIgnoreCase("csr"))||(loginUserType.equalsIgnoreCase("direct"))){
			  sourceColumnsInfo = response.then().extract().path("data");
			  if(sourceColumnsInfo!=null){
				  assertTrue(true, "get policies is right");
			  }else{
				  assertTrue(false, "get policies is right");
			  }
		  }else if(loginUserType.equalsIgnoreCase("msp")){
			  policy4SPOGServer.checkPolicyWithErrorInfo(response,404,"00100201","Unable to find resource with ID",test);		
		  }else if(loginUserType.equalsIgnoreCase("account")){
			  policy4SPOGServer.checkPolicyWithErrorInfo(response,404,"00100201","Unable to find resource with ID",test);		
		  }
	  }	
	  
	  @DataProvider(name = "loginUserToGetPolicy4ValidSource1")
	  public final Object[][] getLoginUserToGetPolicy4ValidSource1() {
			return new Object[][] { 
				
				{"msp",this.direct_source_id+","+this.direct_source_2003},
				
				{"account",this.direct_source_id+","+this.direct_source_2003}
				};
	  }
	  @Test(dataProvider = "loginUserToGetPolicy4ValidSource1")
	  public void getPolicy4ValidSource1(String loginUserType,String source_id){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  Response response=null;
		  ArrayList<HashMap<String, Object>> sourceColumnsInfo=null;
		  if (loginUserType.equalsIgnoreCase("csr")){			 
			  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
		  }else if(loginUserType.equalsIgnoreCase("direct")){
			  spogServer.userLogin(direct_admin_email1, direct_password);
		  }else if(loginUserType.equalsIgnoreCase("msp")){
			  spogServer.userLogin(this.msp_admin_email, OrgPwdForPrepare,test);
		  }else if(loginUserType.equalsIgnoreCase("account")){
			  spogServer.userLogin(this.msp_ac1_email, OrgPwdForPrepare,test);
		  }
		  policy4SPOGServer.setToken(spogServer.getJWTToken());
		  response=policy4SPOGServer.postPoliciesByTypeUnderSources(source_id,  test);
		  if ((loginUserType.equalsIgnoreCase("csr"))||(loginUserType.equalsIgnoreCase("direct"))){
			  sourceColumnsInfo = response.then().extract().path("data");
			  if(sourceColumnsInfo!=null){
				  assertTrue(true, "get policies is right");
			  }else{
				  assertTrue(false, "get policies is right");
			  }
		  }else if(loginUserType.equalsIgnoreCase("msp")){
			  policy4SPOGServer.checkPolicyWithErrorInfo(response,403,"00100101","The current user does not have permissions to manage the resource.",test);		
		  }else if(loginUserType.equalsIgnoreCase("account")){
			  policy4SPOGServer.checkPolicyWithErrorInfo(response,403,"00100101","The current user does not have permissions to manage the resource.",test);		
		  }
	  }	
	  
	  @DataProvider(name = "loginUserToGetPolicy4InvalidSource")
	  public final Object[][] getLoginUserToGetPolicy4InvalidSource() {
			return new Object[][] { 
				
				{"csr",this.direct_udp_source_id+","+this.direct_source_win10},
				{"csr",this.direct_udp_source_id1+","+this.direct_source_2003},
				{"msp",this.direct_source_2003},
				{"msp",this.direct_source_xp},
				{"msp",this.direct_source_2000},
			
				{"account",this.direct_source_2003},
				{"account",this.direct_source_xp},
				{"account",this.direct_source_2000},
				
				};
	  }
	  @Test(dataProvider = "loginUserToGetPolicy4InvalidSource")
	  public void getPolicy4InvalidSource(String loginUserType,String source_id){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  Response response=null;
		  ArrayList<HashMap<String, Object>> sourceColumnsInfo=null;
		  if (loginUserType.equalsIgnoreCase("csr")){			 
			  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
		  }else if(loginUserType.equalsIgnoreCase("direct")){
			  spogServer.userLogin(direct_admin_email1, direct_password);
		  }else if(loginUserType.equalsIgnoreCase("msp")){
			  spogServer.userLogin(this.msp_admin_email, OrgPwdForPrepare,test);
		  }else if(loginUserType.equalsIgnoreCase("account")){
			  spogServer.userLogin(this.msp_ac1_email, OrgPwdForPrepare,test);
		  }
		  policy4SPOGServer.setToken(spogServer.getJWTToken());
		  response=policy4SPOGServer.postPoliciesByTypeUnderSources(source_id,  test);
		  if ((loginUserType.equalsIgnoreCase("csr"))||(loginUserType.equalsIgnoreCase("direct"))){
			  sourceColumnsInfo = response.then().extract().path("data");
			  if(sourceColumnsInfo.size()>0){
				  assertTrue(false, "get policies is right");
			  }else{
				  assertTrue(true, "get policies is right");
			  }
		  }else if(loginUserType.equalsIgnoreCase("msp")){
			  policy4SPOGServer.checkPolicyWithErrorInfo(response,404,"00100201","Unable to find resource with ID",test);		
		  }else if(loginUserType.equalsIgnoreCase("account")){
			  policy4SPOGServer.checkPolicyWithErrorInfo(response,404,"00100201","Unable to find resource with ID",test);		
		  }
	  }	
	  
	  @DataProvider(name = "loginUserToGetPolicy4InvalidSource1")
	  public final Object[][] getLoginUserToGetPolicy4InvalidSource1() {
			return new Object[][] { 
				
				
				{"msp",this.direct_udp_source_id},
				{"msp",this.direct_udp_source_id1},
				
				{"account",this.direct_udp_source_id},
				{"account",this.direct_udp_source_id1},
				{"submspaccountadmin",this.direct_udp_source_id},
				{"submspaccountadmin",this.direct_udp_source_id1},
				{"submspaccount",this.direct_udp_source_id},
				{"submspaccount",this.direct_udp_source_id1},
				{"submsp",this.direct_udp_source_id},
				{"submsp",this.direct_udp_source_id1},
				{"rootmspaccountadmin",this.direct_udp_source_id},
				{"rootmspaccountadmin",this.direct_udp_source_id1},
				{"rootmspaccount",this.direct_udp_source_id},
				{"rootmspaccount",this.direct_udp_source_id1},
				{"rootmsp",this.direct_udp_source_id},
				{"rootmsp",this.direct_udp_source_id1},
				};
	  }
	  @Test(dataProvider = "loginUserToGetPolicy4InvalidSource1")
	  public void getPolicy4InvalidSource1(String loginUserType,String source_id){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  Response response=null;
		  ArrayList<HashMap<String, Object>> sourceColumnsInfo=null;
		  if (loginUserType.equalsIgnoreCase("csr")){			 
			  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
		  }else if(loginUserType.equalsIgnoreCase("direct")){
			  spogServer.userLogin(direct_admin_email1, direct_password);
		  }else if(loginUserType.equalsIgnoreCase("msp")){
			  spogServer.userLogin(this.msp_admin_email, OrgPwdForPrepare,test);
		  }else if(loginUserType.equalsIgnoreCase("account")){
			  spogServer.userLogin(this.msp_ac1_email, OrgPwdForPrepare,test);
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
		  policy4SPOGServer.setToken(spogServer.getJWTToken());
		  response=policy4SPOGServer.postPoliciesByTypeUnderSources(source_id,  test);
		  if ((loginUserType.equalsIgnoreCase("csr"))||(loginUserType.equalsIgnoreCase("direct"))){
			  sourceColumnsInfo = response.then().extract().path("data");
			  if(sourceColumnsInfo.size()>0){
				  assertTrue(false, "get policies is right");
			  }else{
				  assertTrue(true, "get policies is right");
			  }
		  }else if(loginUserType.equalsIgnoreCase("msp")){
			  policy4SPOGServer.checkPolicyWithErrorInfo(response,403,"00100101","The current user does not have permissions to manage the resource.",test);	
		  }else if(loginUserType.equalsIgnoreCase("account")){
			  policy4SPOGServer.checkPolicyWithErrorInfo(response,403,"00100101","The current user does not have permissions to manage the resource.",test);	
		  }
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
