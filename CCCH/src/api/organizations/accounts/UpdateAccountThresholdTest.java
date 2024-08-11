package api.organizations.accounts;

import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
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

import CI.prepareUIData;
import Constants.ErrorCode;
import Constants.SpogConstants;
import InvokerServer.GatewayServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGHypervisorsServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class UpdateAccountThresholdTest extends base.prepare.PrepareOrgInfo{
	@Parameters({ "pmfKey"})
	public UpdateAccountThresholdTest(String pmfKey) {
		super(pmfKey);
		// TODO Auto-generated constructor stub
	}
	private SPOGDestinationServer spogDestinationServer;
	  private SPOGServer spogServer;
	  private UserSpogServer userSpogServer;
	  private Policy4SPOGServer policy4SPOGServer;
	  private SPOGHypervisorsServer spogHypervisorsServer;
	  private GatewayServer gatewayServer;
	  private String directOrgEmailForPrepare,directOrgId;
	  private String accountOrgId,accountOrgId1;
	  private String csrAdmin;
	  private String csrPwd;
	  private String[] mspAccountAdminUserIds=new String[1];
	  
	  private String mspOrgNameForPrepare="d_jing_spogqa_msp_org_prepare_jing";
	  private String accountEmailForPrepare ="d_jing_spogqa_account_org_prepare_jing_1@arcserve.com";
	  private String mspOrgEmailForPrepare="d_jing_spogqa_msp_org_prepar_jing@arcserve.come";
	  private String mspAccountAdminEmailForPrepare="d_jing_spogqa_msp_account_org_prepar_jing@arcserve.come";
	  private String accountEmailForPrepare1="d_jing_spogqa_account_org_prepare_jing_2@arcserve.com";
	  private String OrgFistNameForPrepare="jing";
	  private String OrgLastNameForPrepare="org_prepare";
	  private String OrgPwdForPrepare="welcomeA02";
	  private String sub_msp_pwd="Welcome*02";
	  private String  org_prefix=this.getClass().getSimpleName();
	  private ExtentTest test;
	  //this is for update portal, each testng class is taken as BQ set
//	  private ExtentReports rep;
//	  private SQLServerDb bqdb1;
//	  public int Nooftest;
//	  private long creationTime;
//	  private String BQName=null;
//	  private String runningMachine;
//	  private testcasescount count1;
//	  private String buildVersion;
	  private String mspOrgId="bc2ffd20-0c5e-4c4e-8531-c2cad1da8618";
	  private String account_id1="cc17e814-d93e-464a-82c0-8dcf14730949";
	  private String account_id2="50ae0702-48ed-45d1-958f-c89d3f3a4afc";
	  
	  //end 
	  @BeforeClass
	  @Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword","logFolder","runningMachine","buildVersion"})
	  public void beforeClass(String baseURI, String port, String csrAdminUserName, String csrAdminPassword, String logFolder, String runningMachine, String buildVersion) {
		//this is for update portal
		  this.BQName = this.getClass().getSimpleName();
		  String author = "Jing.Shan";
		  this.runningMachine = runningMachine;
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
		  userSpogServer = new UserSpogServer(baseURI, port);
		  spogHypervisorsServer = new SPOGHypervisorsServer(baseURI, port);
		  spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		  policy4SPOGServer= new Policy4SPOGServer(baseURI, port);
		  rep = ExtentManager.getInstance("CreateAccountTest",logFolder);
		  test = rep.startTest("beforeClass");
		  this.csrAdmin = csrAdminUserName;
		  this.csrPwd = csrAdminPassword;
		  String directOrgNameForPrepare="direct_jing";
		  spogServer.userLogin(this.csrAdmin, this.csrPwd);
		  prepare(baseURI, port, logFolder, csrAdminUserName, csrAdminPassword, this.getClass().getSimpleName() );
//		  String csrAdmin="tmp_xiang.li@arcserve.com";
//		  String csrPwd="Caworld_2017";
//		  spogServer.createUserAndCheck(csrAdmin, csrPwd, "xiang", "li", SpogConstants.CSR_ADMIN, spogServer.GetLoggedinUserOrganizationID(),test);
//		  spogServer.userLogin(csrAdmin, csrPwd);
		  directOrgEmailForPrepare=spogServer.ReturnRandom("d_jing_spogqa_direct_org_prepar_jing@arcserve.come");
		  accountEmailForPrepare=spogServer.ReturnRandom(accountEmailForPrepare);
		  accountEmailForPrepare1=spogServer.ReturnRandom(accountEmailForPrepare1);
		  mspOrgNameForPrepare=spogServer.ReturnRandom(mspOrgNameForPrepare);
		  mspOrgEmailForPrepare=spogServer.ReturnRandom(mspOrgEmailForPrepare);
		  this.directOrgId = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(directOrgNameForPrepare)+org_prefix, SpogConstants.DIRECT_ORG, directOrgEmailForPrepare, OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare);
		  this.gatewayServer = new GatewayServer(baseURI, port);
		  
		  
		  this.mspOrgId = spogServer.CreateOrganizationByEnrollWithCheck(mspOrgNameForPrepare+org_prefix, SpogConstants.MSP_ORG, mspOrgEmailForPrepare, OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare);
		  //this.mspOrgId1 = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(mspOrgNameForPrepare), SpogConstants.MSP_ORG, mspOrgEmailForPrepare1, OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare);
		  this.accountOrgId= spogServer.createAccountWithCheck(this.mspOrgId, spogServer.ReturnRandom("spogqa_account_jing_1")+org_prefix,"");
		  this.accountOrgId1= spogServer.createAccountWithCheck(this.mspOrgId, spogServer.ReturnRandom("spogqa_account_jing_2")+org_prefix,"");
		  //this.cloudOrgID = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(directOrgNameForPrepare), SpogConstants.DIRECT_ORG, cloudEmail, OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare);
		  spogServer.createUserAndCheck(this.accountEmailForPrepare, OrgPwdForPrepare, "dd", "gg", SpogConstants.DIRECT_ADMIN, accountOrgId,test);
		  spogServer.createUserAndCheck(this.accountEmailForPrepare1, OrgPwdForPrepare, "dd", "gg", SpogConstants.DIRECT_ADMIN, accountOrgId1,test);
		  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
		  spogDestinationServer.setToken(spogServer.getJWTToken());
		  
		  
		  mspAccountAdminEmailForPrepare = spogServer.ReturnRandom("d_jing_spogqa_msp_org_prepar_jing@arcserve.come");
		  String csrToken= spogServer.getJWTToken();
		  mspAccountAdminUserIds[0]=spogServer.createUserAndCheck(mspAccountAdminEmailForPrepare, OrgPwdForPrepare, "ff", "ff", "msp_account_admin", mspOrgId, test);
		  spogServer.userLogin(mspAccountAdminEmailForPrepare, OrgPwdForPrepare);
		  
		  Response response = userSpogServer.assignMspAccountAdmins(mspOrgId, accountOrgId, mspAccountAdminUserIds, csrToken);
		  userSpogServer.assignMspAccountAdminsWithCheck(response, mspAccountAdminUserIds, csrToken);
	  }
	  
	  public boolean isNullOrEmpty(String checkValue){
		  if(checkValue==null || checkValue==""){
			  return true;
		  }else{
			  return false;
		  }
		  
	  }
	  
	  @DataProvider(name = "update_threshold_params_valid")
	  public final Object[][] updateThresholdValidParams() {
			return new Object[][] {
				
				{"mspAccountAdmin",mspOrgId, accountOrgId, "200", ""},
				{"mspAccountAdmin",mspOrgId, accountOrgId, "200", ""},
				{"mspAccountAdmin",mspOrgId, accountOrgId, "", ""},
				{"csr",mspOrgId, accountOrgId, "200", ""},
				{"csr",mspOrgId, accountOrgId1, "200", ""},
				{"csr",mspOrgId, accountOrgId, "", ""},
				{"csr",mspOrgId, accountOrgId1, "", ""},
				
				{"msp",mspOrgId, accountOrgId, "200", ""},
				{"msp",mspOrgId, accountOrgId1, "200", ""},
				{"msp",mspOrgId, accountOrgId, "", ""},
				{"msp",mspOrgId, accountOrgId1, "", ""},				
				};	
		}
	  
	  @Test(dataProvider = "update_threshold_params_valid")
	  public void updateThresholdInfo(
			  String loginUserType,
			  String parentId, 
			  String childId,
			  String cloud_direct_volume, 
			  String cloud_hybrid_store) {
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  if (loginUserType.equalsIgnoreCase("csr")){
			  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);  
		  }else if(loginUserType.equalsIgnoreCase("msp")){
			  spogServer.userLogin(this.mspOrgEmailForPrepare, OrgPwdForPrepare,test);
		  }else if(loginUserType.equalsIgnoreCase("mspAccountAdmin")){
			  spogServer.userLogin(this.mspAccountAdminEmailForPrepare, OrgPwdForPrepare,test);
		  }
		  spogServer.updateThresholdWithCheck(parentId, childId, cloud_direct_volume, cloud_hybrid_store,test);
		  Response response=spogServer.getsuborgaccountinfobyId(spogServer.getJWTToken(),parentId,childId,test);
		  int check_cloud_direct_volume,check_cloud_hybrid_store;
		  if (cloud_direct_volume==""){
			  check_cloud_direct_volume=0;
		  }else{
			  check_cloud_direct_volume=Integer.parseInt(cloud_direct_volume);
		  }
		  if (cloud_hybrid_store==""){
			  check_cloud_hybrid_store=0;
		  }else{
			  check_cloud_hybrid_store=Integer.parseInt(cloud_hybrid_store);
		  }
		  response.then().body("data.cloud_direct_usage.threshold", equalTo(check_cloud_direct_volume));
		  response.then().body("data.cloud_hybrid_usage.threshold", equalTo(check_cloud_hybrid_store));
		  Response response1=spogServer.getsuborgaccountinfo(spogServer.getJWTToken(),parentId,test);
		  ArrayList<HashMap<String, Object>> data=response1.then().extract().path("data"); 
		  if(data==null){
			  assertNotNull(data,"can't get account information");
		  }else{
			  for(int i=0;i<data.size();i++){
				if(data.get(i).get("organization_id").toString().equalsIgnoreCase(childId)){
					response1.then().body("data["+i+"].cloud_hybrid_usage.threshold", equalTo(check_cloud_hybrid_store));
					response1.then().body("data["+i+"].cloud_direct_usage.threshold", equalTo(check_cloud_direct_volume));
				}
			  }
		  }
		  
      }
	
	  @DataProvider(name = "update_threshold_params_invalid")
	  public final Object[][] updateThresholdInvalidParams() {
			return new Object[][] {
				{"", accountOrgId, "200", "",404,"00900001","Unable to find the resource."},
				{null, accountOrgId1, "200", "",400,"40000005","is not a UUID."},
				{mspOrgId, "", "", "",405,"00900002","Not allowed to get the resource."},
				{mspOrgId, null, "", "",400,"40000005","is not a UUID."},
				{"random", accountOrgId, "200", "200",404,"0030000A","not found or has been removed"},
				{mspOrgId, "random", "200", "200",404,"0030000A","not found or has been removed"}
			};	
		}
	  
	  @Test(dataProvider = "update_threshold_params_invalid")
	  public void updateThresholdInvalidInfo(
			  String parentId, 
			  String childId,
			  String cloud_direct_volume, 
			  String cloud_hybrid_store,
			  int expectedStatusCode,
		      String expectedErrorCode, 
		      String expectedErrorMessage) {
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  if(!isNullOrEmpty(parentId)&&(parentId.equalsIgnoreCase("random"))){
			  parentId=spogServer.returnRandomUUID();
		  }
		  if(!isNullOrEmpty(childId)&&(childId.equalsIgnoreCase("random"))){
			  childId=spogServer.returnRandomUUID();
		  }
		  spogServer.userLogin(mspOrgEmailForPrepare, OrgPwdForPrepare);
		  spogServer.updateThresholdWithExpectedStatusCode(parentId, childId, cloud_direct_volume, cloud_hybrid_store,expectedStatusCode,expectedErrorCode,expectedErrorMessage,test);
      }
	  
	  @DataProvider(name = "update_threshold_params_no_permission")
	  public final Object[][] updateThresholdNoPermission() {
			return new Object[][] {
				{"direct","random", accountOrgId, "200", "200",404,"0030000A","not found or has been removed"},
				{"account",mspOrgId, "random", "200", "200",404,"0030000A","not found or has been removed"},
				{"mspAccountAdmin",mspOrgId, "random", "200", "200",404,"0030000A","not found or has been removed"},
				{"direct",mspOrgId, "accountOrgId", "200", "200",400,"40000005","is not a UUID."},
				{"direct",mspOrgId, accountOrgId, "200", "200",403,"00100101","Permission required to manage the resource for current user"},
				{"account",mspOrgId, accountOrgId, "200", "200",403,"00100101","Permission required to manage the resource for current user"},
				{"account",mspOrgId, accountOrgId1, "200", "200",403,"00100101","Permission required to manage the resource for current user"},
				{"mspAccountAdmin",mspOrgId, accountOrgId1, "200", "200",403,"00100101","Permission required to manage the resource for current user"},
				
				{"submspaccount",mspOrgId, accountOrgId, "200", "200",403,"00100101","Permission required to manage the resource for current user"},
				{"submspaccountadmin",mspOrgId, accountOrgId, "200", "200",403,"00100101","Permission required to manage the resource for current user"},
				{"submsp",mspOrgId, accountOrgId1, "200", "200",403,"00100101","Permission required to manage the resource for current user"},
				{"rootmspaccount",mspOrgId, accountOrgId, "200", "200",403,"00100101","Permission required to manage the resource for current user"},
				{"rootmspaccountadmin",mspOrgId, accountOrgId, "200", "200",403,"00100101","Permission required to manage the resource for current user"},
				{"rootmsp",mspOrgId, accountOrgId1, "200", "200",403,"00100101","Permission required to manage the resource for current user"},
			};	
		}
	  
	  @Test(dataProvider = "update_threshold_params_no_permission")
	  public void updateThresholdNoPermission(
			  String loginUserType,
			  String parentId, 
			  String childId,
			  String cloud_direct_volume, 
			  String cloud_hybrid_store,
			  int expectedStatusCode,
		      String expectedErrorCode, 
		      String expectedErrorMessage) {
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  if (loginUserType.equalsIgnoreCase("direct")){
			  spogServer.userLogin(this.directOrgEmailForPrepare, OrgPwdForPrepare,test); 
		  }else if(loginUserType.equalsIgnoreCase("account")){
			  spogServer.userLogin(this.accountEmailForPrepare, OrgPwdForPrepare,test);
		  }else if(loginUserType.equalsIgnoreCase("mspAccountAdmin")){
			  spogServer.userLogin(this.mspAccountAdminEmailForPrepare, OrgPwdForPrepare,test);
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
		  if(!isNullOrEmpty(parentId)&&(parentId.equalsIgnoreCase("random"))){
			  parentId=spogServer.returnRandomUUID();
		  }
		  if(!isNullOrEmpty(childId)&&(childId.equalsIgnoreCase("random"))){
			  childId=spogServer.returnRandomUUID();
		  }
		  //spogServer.userLogin(mspOrgEmailForPrepare, OrgPwdForPrepare);
		  spogServer.updateThresholdWithExpectedStatusCode(parentId, childId, cloud_direct_volume, cloud_hybrid_store,expectedStatusCode,expectedErrorCode,expectedErrorMessage,test);
      }
	  
	  
	  //@Test()
	  public void prepareUIData(){
		//create cloud account and create destination
		  spogServer.userLogin(mspOrgEmailForPrepare, OrgPwdForPrepare);
		  String organization_id=spogServer.GetLoggedinUserOrganizationID();
		  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
		  String destination_normal_name="destination_normal_0329";
		  spogDestinationServer.setToken(spogServer.getJWTToken());
		  String[] datacenterIDs=spogDestinationServer.getDestionationDatacenterID();
		    int index=(int)Math.random()*datacenterIDs.length;
		    String datacenterID=datacenterIDs[index];
		    String cloud_prefix = "cloud_0329";
			String cloud_direct_account_id=spogServer.createCloudAccountWithCheck("cloudAccountKey", "cloudAccountSecret", cloud_prefix+"cloudAccountName", "cloud_direct", organization_id, 
					"SKUTESTDATA_1_4_0_0_"+cloud_prefix, "SKUTESTDATA_1_0_0_0_"+cloud_prefix, "SKUTESTDATA_1_0_0_0_"+cloud_prefix, "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
			
		     String destination_id_ret1=spogDestinationServer.createDestinationWithCheck(  "",organization_id, cloud_direct_account_id,datacenterID,"cloud_direct_volume", "running", "1",
						  cloud_direct_account_id,"normal", "shaji02-win10", "custom","custom", "0", "0", "7", "0",
							"0", "0", "", "", "", "", destination_normal_name,test); 
		  
		     spogDestinationServer.setToken(spogServer.getJWTToken());
             String baas_destionation_ID_normal = spogDestinationServer.createDestinationWithCheck(null, organization_id,cloud_direct_account_id,
                   "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", "cloud_direct_volume", "running",null, cloud_direct_account_id, "zero_copy", "mabzh02-vm001", "7Dhf","7Dhf", "0", "0", "7", "0",
                   "0", "0", "", "", "", "",  "ddd", test);
             Response response = gatewayServer.LoginSite(cloud_direct_account_id, "cloudAccountSecret", test);
     		String token = response.then().extract().path("data.token");
     		spogHypervisorsServer.setToken(token);
     		
     		String prefix = RandomStringUtils.randomAlphanumeric(8);
     		String vmware_hypervisorID = spogHypervisorsServer.createHypervisorWithCheck("none", "hypervisor_name1", "vmware", "cloud_direct", "none", cloud_direct_account_id, organization_id, "false", String.valueOf(System.currentTimeMillis()), 
    				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
    				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", test);
    		
     		String hyperv_hypervisorID = spogHypervisorsServer.createHypervisorWithCheck(UUID.randomUUID().toString(), "hypervisor_name9a", "hyperv", "cloud_direct", "none", cloud_direct_account_id, organization_id, "false", String.valueOf(System.currentTimeMillis()),
    				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "none", "none", "none", "none", "none",
    				"none", "none", "emptyarray", "emptyarray", "emptyarray", "emptyarray", "none", "none", test);
    		
     		 String aString="";
		     
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
