package api.cloudaccounts;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.SPOGServer;
import InvokerServer.GatewayServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.Log4GatewayServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;

import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.text.SimpleDateFormat;

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
import Constants.ConnectionStatus;
import Constants.OSMajor;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
public class CloudAccountJwtCallApiTest {
	  private GatewayServer gatewayServer;
	  private SPOGServer spogServer;
	  private SPOGDestinationServer spogDestinationServer;
	  private Log4GatewayServer log4GatewayServer;
	  private String csrOrg_id;
	  private String directOrg_id;
	  private String directOrg2_id;
	  private String directOrg3_id;
	  private String mspOrg_id;
	  private String mspOrg2_id;
	  private String account_id;
	  private String account2_id;
	  private String msp_email;
	  private String direct_email;
	  private String accountDirect_email;
	  private String sharePassword = "Caworld_2017";
	  private ExtentReports rep;
	  private ExtentTest test;
	  private Response response;
	  private String csrGlobalLoginUser;
	  private String csrGlobalLoginPassword;
	  private String csrAdminToken;
	  private String directAdminToken;
	  private String directAdminToken2;
	  private String directAdminToken3;
	  private String mspAdminToken;
	  private String mspAdminToken2;
	  private String accountAdminToken;
	  private String accountAdminToken2;
	  private String directSite_id;
	  private String mspSite_id;
	  private String accountSite_id;
	  private String directSite2_id;
	  private String directSite3_id;
	  private String mspSite2_id;
	  private String accountSite2_id;
	  private String directUserName;
	  private String directUserName2;
	  private String directUserName3;
	  private String mspUserName;
	  private String mspUserName2;
	  private String accountUserName;
	  private String accountUserName2;
	  private String csrSiteToken;
	  private String directSiteToken;
	  private String directSiteToken2;
	  private String directSiteToken3;
	  private String mspSiteToken;
	  private String mspSiteToken2;
	  private String accountSiteToken;
	  private String accountSiteToken2;
	  private String directGroup_id;
	  private String directGroup2_id;
	  private String directGroup3_id;
	  private String mspGroup2_id;
	  private String mspGroup_id;
	  private String accountGroup_id;
	  private String accountGroup2_id;
	  private String directCloudAccountToken;
	  private String mspCloudAccountToken;
	  private String directCloudAccount_id;
	  private String mspCloudAccount_id;
	  private String sourceID;
	//this is for update portal, each testng class is taken as BQ set
	  private SQLServerDb bqdb1;
	  public int Nooftest;
	  private long creationTime;
	  private String BQName=null;
	  private String runningMachine;
	  private testcasescount count1;
	  private String buildVersion;
	  
	  @BeforeClass
	  @Parameters({ "baseURI", "port" , "csrAdminUserName", "csrAdminPassword","logFolder","runningMachine", "buildVersion"})
	  public void beforeClass(String baseURI, String port, String userName, String password, String logFolder, String runningMachine, String buildVersion) {
		//this is for update portal
		  this.BQName = this.getClass().getSimpleName();
		  String author = "yuefen.liu";
		  this.runningMachine = runningMachine;
		  SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		  java.util.Date date=new java.util.Date();
		  this.buildVersion=buildVersion+"_"+dateFormater.format(date);
		  Nooftest=0;
		  bqdb1 = new SQLServerDb();
		  count1 = new testcasescount();
		  if(count1.isstarttimehit()==0) {
			System.out.println("CloudAccountJwtCallApiTest");
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
		  rep = ExtentManager.getInstance("CloudAccountJwtCallApiTest",logFolder);
		  test = rep.startTest("beforeClass");
		  spogServer = new SPOGServer(baseURI, port);
		  spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		  gatewayServer =new GatewayServer(baseURI,port);
		  spogServer.userLogin(userName, password);
		  
		  
		  this.csrGlobalLoginUser = userName;
		  this.csrGlobalLoginPassword = password;
		  
		  //create direct org, user, site, group, cloud account, source
		  System.out.println("create direct org, user, site, group and cloud account");
		  this.directOrg_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_group_direct1_yuefen"),"direct",spogServer.ReturnRandom("yuefen_direct1_group@spogqa.com"),sharePassword,"yuefen","liu");
		  
		  this.directUserName= spogServer.ReturnRandom("getGroup_direct_admin_yuefen@spogqa.com");
		  String direct_userId = spogServer.createUserAndCheck(directUserName, sharePassword, "yuefen", "liu", "direct_admin", directOrg_id, test);
		  spogServer.userLogin(directUserName, sharePassword);
		  this.directAdminToken = spogServer.getJWTToken();
		  
		  this.directSite_id = gatewayServer.createsite_register_login(directOrg_id, directAdminToken, direct_userId, "yuefen", "1.0.0", spogServer, test);
		  
//		  String directGroupName = spogServer.ReturnRandom("direct_groupx_yuefen");
//		  this.directGroup_id = spogServer.createGroupWithCheck(directOrg_id, directGroupName, "", test);
		  
		  String key = spogServer.ReturnRandom("cloud_account_key_direct_yuefen");
		  String secret = spogServer.ReturnRandom("cloud_account_secret_direct_yuefen");
		  this.directCloudAccount_id = spogServer.createCloudAccountWithCheck(key, secret, spogServer.ReturnRandom("account_name_direct_yuefen"), "cloud_direct", directOrg_id, test);
		  this.directCloudAccountToken = spogServer.cloudDirectAccountLoginSuccess(key, secret, test);
		
		  response = spogServer.createSourceWithCloudToken(spogServer.ReturnRandom("yuefen_sourcexx_directSite"), SourceType.machine,  SourceProduct.udp,  directOrg_id, directSite_id, 
		 			 ProtectionStatus.protect, ConnectionStatus.online,"windows","sql", null, null, null, null, null, null, null, null, directCloudAccountToken, test);
		  this.sourceID = response.then().extract().path("data.source_id");
		  
		  //create another direct org, user, site and group
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword);
		  
		  directOrg2_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_group_direct2_yuefen"),"direct",spogServer.ReturnRandom("yuefen_direct2_group@spogqa.com"),sharePassword,"yuefen","liu");
		
		  this.directUserName2= spogServer.ReturnRandom("getGroup_direct_admin2_yuefen@spogqa.com");
		  String direct_user2Id = spogServer.createUserAndCheck(directUserName2, sharePassword, "yuefen", "liu", "direct_admin", directOrg2_id, test);
		  spogServer.userLogin(directUserName2, sharePassword);
		  this.directAdminToken2 = spogServer.getJWTToken();
		  
		  this.directSite2_id = gatewayServer.createsite_register_login(directOrg2_id, directAdminToken2, direct_user2Id, "yuefen", "1.0.0", spogServer, test);
//		  
//		  String directGroupName2 = spogServer.ReturnRandom("direct_groupx2_yuefen");
//		  this.directGroup2_id = spogServer.createGroupWithCheck(directOrg2_id, directGroupName2, "", test);
//		  
//		  //create direct org3 and group3
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword);
		  directOrg3_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_group_direct3_yuefen"),"direct",spogServer.ReturnRandom("yuefen_direct3_group@spogqa.com"),sharePassword,"yuefen","liu");
//		  this.directUserName3= spogServer.ReturnRandom("getGroup_direct_admin3_yuefen@spogqa.com");
//		  String direct_user3Id = spogServer.createUserAndCheck(directUserName3, sharePassword, "yuefen", "liu", "direct_admin", directOrg3_id, test);
//		  spogServer.userLogin(directUserName3, sharePassword);
//		  this.directAdminToken3 = spogServer.getJWTToken();
//		  
//		  this.directSite3_id = gatewayServer.createsite_register_login(directOrg3_id, directAdminToken3, direct_user3Id, "yuefen", "1.0.0", spogServer, test);
//		  
//		  String directGroupName3 = spogServer.ReturnRandom("direct_groupx3_yuefen");
//		  this.directGroup3_id = spogServer.createGroupWithCheck(directOrg3_id, directGroupName3, "", test);
//		  
		  //create msp org, user, site, group and cloud token
		  System.out.println("create msp org, user, site, group and cloud account");
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword);
		  this.mspOrg_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_group_yuefen_msp1"),"msp",spogServer.ReturnRandom("yuefen_msp1_groupx@spogqa.com"),sharePassword,"yuefen","liu");
		  
		  this.mspUserName = spogServer.ReturnRandom("msp_admin_yuefen@spogqa.com");
		  String msp_userId = spogServer.createUserAndCheck(mspUserName, sharePassword, "yuefen", "liu", "msp_admin", mspOrg_id, test);
		  spogServer.userLogin(mspUserName, sharePassword);
		  this.mspAdminToken = spogServer.getJWTToken();
		  
		  this.mspSite_id = gatewayServer.createsite_register_login(mspOrg_id, mspAdminToken, msp_userId, "yuefen", "1.0.0", spogServer, test);
		  
//		  String mspGroupName = spogServer.ReturnRandom("msp_groupx_yuefen");
//		  this.mspGroup_id = spogServer.createGroupWithCheck(mspOrg_id, mspGroupName, "", test);
		  
		  String mspkey = spogServer.ReturnRandom("cloud_account_key_msp_yuefen");
		  String mspsecret = spogServer.ReturnRandom("cloud_account_secret_msp_yuefen");
		  this.mspCloudAccount_id = spogServer.createCloudAccountWithCheck(mspkey, mspsecret, spogServer.ReturnRandom("account_name_msp_yuefen"), "cloud_direct", mspOrg_id, test);
		  this.mspCloudAccountToken = spogServer.cloudDirectAccountLoginSuccess(mspkey, mspsecret, test);
//		  
//		  //create another msp org, user, site and group
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword);
		  mspOrg2_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_group_org2_yuefen_msp2"),"msp",spogServer.ReturnRandom("yuefen_msp2_groupx2@spogqa.com"),sharePassword,"yuefen","liu");
//		  
//		  this.mspUserName2 = spogServer.ReturnRandom("getGroup_msp2_admin_yuefen@spogqa.com");
//		  String msp_user2Id = spogServer.createUserAndCheck(mspUserName2, sharePassword, "yuefen", "liu", "msp_admin", mspOrg2_id, test);
//		  spogServer.userLogin(mspUserName2, sharePassword);
//		  this.mspAdminToken2 = spogServer.getJWTToken();
//		  
//		  this.mspSite2_id = gatewayServer.createsite_register_login(mspOrg2_id, mspAdminToken2, msp_user2Id, "yuefen", "1.0.0", spogServer, test);
//		  
//		  String mspGroupName2 = spogServer.ReturnRandom("msp_groupx2_yuefen");
//		  this.mspGroup2_id = spogServer.createGroupWithCheck(mspOrg2_id, mspGroupName, "", test);
//		  
		  //create account, user, site and group
		  System.out.println("create account org, user, site, group and cloud account"); 
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword);
		  this.account_id = spogServer.createAccountWithCheck(mspOrg_id,spogServer.ReturnRandom("spogqa_accoun1_msp1_yuefen"), mspOrg_id, test);
		  
		  this.accountUserName= spogServer.ReturnRandom("account_admin_yuefen@spogqa.com");
		  String account_userId = spogServer.createUserAndCheck(accountUserName, sharePassword, "yuefen", "liu", "direct_admin", account_id, test);
		  spogServer.userLogin(accountUserName, sharePassword);
		  this.accountAdminToken = spogServer.getJWTToken();
//		  
//		  this.accountSite_id = gatewayServer.createsite_register_login(account_id, accountAdminToken, account_userId, "yuefen", "1.0.0", spogServer, test);
		  
//		  String accountGroupName = spogServer.ReturnRandom("account_groupx_yuefen");
//		  this.accountGroup_id = spogServer.createGroupWithCheck(account_id, accountGroupName, "", test);
//		  
//		  //create another account, user, site and group
//		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword);
//		  this.account2_id = spogServer.createAccountWithCheck(mspOrg2_id,spogServer.ReturnRandom("spogqa_accoun2_msp2_yuefen_groupx2"), mspOrg2_id, test);
//		  
//		  this.accountUserName2= spogServer.ReturnRandom("getGroup_account_admin2_yuefen@spogqa.com");
//		  String account_user2Id = spogServer.createUserAndCheck(accountUserName2, sharePassword, "yuefen", "liu", "direct_admin", account2_id, test);
//		  spogServer.userLogin(accountUserName2, sharePassword);
//		  this.accountAdminToken2 = spogServer.getJWTToken();
//		  
//		  this.accountSite2_id = gatewayServer.createsite_register_login(account2_id, accountAdminToken2, account_user2Id, "yuefen", "1.0.0", spogServer, test);
//		  
//		  String accountGroupName2 = spogServer.ReturnRandom("account_groupx2_yuefen");
//		  this.accountGroup2_id = spogServer.createGroupWithCheck(account2_id, accountGroupName2, "", test);
//		  
//		  //csr admin token
//		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword);
//		  this.csrAdminToken = spogServer.getJWTToken();
	  }
	  
	  @DataProvider(name = "cloudAccountInfo")
	  public final Object[][] getCloudAccountInfo() {
		  return new Object[][] { 
//			  {spogServer.ReturnRandom("spogqa_direct_directaccountkey"),spogServer.ReturnRandom("direct_directaccount_secret"),
//				  spogServer.ReturnRandom("direct_directaccount_name"),"cloud_direct",directOrg_id, directCloudAccountToken, directCloudAccount_id},
			  {spogServer.ReturnRandom("spogqa_msp_directaccountkey"),spogServer.ReturnRandom("msp_directaccount_secret"),
					  spogServer.ReturnRandom("msp_directaccount_name"),"cloud_direct",mspOrg_id, mspCloudAccountToken, mspCloudAccount_id}
	          };
		}
	  
	  @Test(dataProvider = "cloudAccountInfo")
	  public void cloudAccountManagement(String cloudAccountKey, String cloudAccountSecret, String cloudAccountName, String cloudAccountType, String organizationID, 
			   String token, String cloudAccountID ){	 
		  System.out.println("cloudAccountManagement");
		  test = rep.startTest("cloudAccountManagement");
		  test.assignAuthor("Liu Yuefen");
		  
		  test.log(LogStatus.INFO, "Using cloud token to call cloud account related api testing");
		  
		  //create cloud account fail, because only allow create one cloud direct per org.
		  System.out.println("token is:"+token); 
		  test.log(LogStatus.INFO, "Using cloud token to call create cloud direct");
		  Response response = spogServer.createCloudAccount(cloudAccountKey, cloudAccountSecret, cloudAccountName, cloudAccountType, organizationID, token, test);
		  spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
          spogServer.checkErrorCode(response, "00B00009");
		  
		  //update cloud account
		  spogServer.setToken(token);
		  String newName = UUID.randomUUID().toString();
		  response = spogServer.updateCloudAccount(cloudAccountID, "", "", newName, "", "", test);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  spogServer.checkBodyItemValue(response, "cloud_account_name", newName);
		  
		  //get cloud account by id
		  response = spogServer.getCloudAccountById(token, cloudAccountID, test);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  
		  //get cloud accounts
		  response = spogServer.getCloudAccounts(token, "organization_id=" + organizationID, test);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  
		  //delete cloud account fail, can't delete itself.
		  spogServer.setToken(token);
		  spogServer.deleteCloudAccountFailWithCheck(cloudAccountID, SpogConstants.INSUFFICIENT_PERMISSIONS, "00900003", test);
	  }
	  
	 
	  @DataProvider(name = "cloudAccountS3Info")
	  public final Object[][] getCloudAccountS3Info() {
		  return new Object[][] { 
//			  {spogServer.ReturnRandom("spogqa_direct_s3accountkey"),spogServer.ReturnRandom("direct_s3account_secret"),
//				  spogServer.ReturnRandom("direct_s3taccount_name"),"aws_s3",directOrg_id, directCloudAccountToken, directCloudAccount_id, directAdminToken},
			  {spogServer.ReturnRandom("spogqa_msp_s3accountkey"),spogServer.ReturnRandom("msp_s3account_secret"),
					  spogServer.ReturnRandom("msp_s3account_name"),"aws_s3",mspOrg_id, mspCloudAccountToken, mspCloudAccount_id, mspAdminToken}
	          };
		}
	  
	  @Test(dataProvider = "cloudAccountS3Info")
	  public void cloudAccountS3Management(String cloudAccountKey, String cloudAccountSecret, String cloudAccountName, String cloudAccountType, 
			  String organizationID, String token, String cloudAccountID, String userToken){	 
		  System.out.println("cloudAccountS3Management");
		  test = rep.startTest("cloudAccountS3Management");
		  test.assignAuthor("Liu Yuefen");
		  
		  test.log(LogStatus.INFO, "Using cloud token to call cloud account s3 related api testing");
		  
		  //create cloud account s3 under the same org
		  System.out.println("token is:"+token);
		  Response response = spogServer.createCloudAccount(cloudAccountKey, cloudAccountSecret, cloudAccountName, cloudAccountType, organizationID, token, test);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
		  spogServer.checkErrorCode(response, "00100101");
		  
		  //update cloud account
		  response = spogServer.createCloudAccount(cloudAccountKey, cloudAccountSecret, cloudAccountName, cloudAccountType, organizationID, userToken, test);
		  String accountID = response.then().extract().path("data.cloud_account_id");
		  spogServer.setToken(token);
		  String newName = UUID.randomUUID().toString();
		  response = spogServer.updateCloudAccount(accountID, "", "", newName, "", "", test);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
		  spogServer.checkErrorCode(response, "00900003");
		  
		  //get cloud account by id
		  response = spogServer.getCloudAccountById(token, accountID, test);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
		  spogServer.checkErrorCode(response, "00900003");
		  
		  //get cloud accounts
		  response = spogServer.getCloudAccounts(token, "organization_id=" + organizationID, test);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  
		  //delete cloud account fail, can't delete itself.
		  spogServer.setToken(token);
		  spogServer.deleteCloudAccountWithExpectedStatusCode(accountID, SpogConstants.INSUFFICIENT_PERMISSIONS, test); 
		  spogServer.checkErrorCode(response, "00900003");
	  }
	  
	  @DataProvider(name = "sourceInfo")
	  public final Object[][] getSourceInfo() {
	 	 return new Object[][]   {
	 		 {spogServer.ReturnRandom("yuefen_sourcexx_directSite"), SourceType.machine,  SourceProduct.udp,  directOrg_id, directSite_id, 
	 			 ProtectionStatus.protect, ConnectionStatus.online, OSMajor.windows,	"sql", null, null, null, null, null, null, null, null, 
	 			 directCloudAccountToken,directCloudAccount_id},
	 		 {spogServer.ReturnRandom("yuefen_sourcexx_mspSite"), SourceType.machine,  SourceProduct.udp,  mspOrg_id, mspSite_id, 
		 			 ProtectionStatus.protect, ConnectionStatus.online, OSMajor.windows,	"sql", null, null, null, null, null, null, null, null, 
		 			 mspCloudAccountToken, mspCloudAccount_id}
	 	 };
	  }
	 
	  @Test(dataProvider = "sourceInfo")
	  public void callSourcesApiTest(String source_name, SourceType source_type, SourceProduct source_product, String organization_id, String site_id, 
			  ProtectionStatus protection_status, ConnectionStatus connection_status, String os_major, String applications, String vm_name, 
			  String hypervisor_id, String agent_name, String os_name, String os_architecture, String agent_current_version, String agent_upgrade_version, 
			  String agent_upgrade_link, String Token, String cloudAccountID){	 
		  System.out.println("callSourcesApiTest");
		  test = rep.startTest("callSourcesApiTest");
		  test.assignAuthor("Liu Yuefen");
		  
		  test.log(LogStatus.INFO, "Using cloud token to call sources related api testing");
		  
		  //create source
	      Response response = spogServer.createSourceWithCloudToken(source_name, source_type, source_product, organization_id, site_id, protection_status, connection_status, os_major, applications, vm_name, hypervisor_id, agent_name, os_name, os_architecture, agent_current_version, agent_upgrade_version, agent_upgrade_link, Token, test);
	      response.then().body("data.create_user_id", equalTo(cloudAccountID));
	      String source_id =  response.then().extract().path("data.source_id");
	      System.out.println("source_id ="+source_id);
	    		 
		  //update source
	      String newSourceName = spogServer.ReturnRandom("source_new_name_spogqa");
	      spogServer.updateSourcebysourceIdwithcheck(source_id, newSourceName, source_type, source_product, organization_id, site_id, UUID.randomUUID().toString(), protection_status, connection_status, os_major, Token, cloudAccountID, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		  //spogServer.updateSourcebysourceIdwithcheck(source_id, newSourceName, null, null, null, null, null, null, null, null, Token, null, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		  
		  //get source by id
		  response = spogServer.getSourceById(Token, source_id, test);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  response.then().body("data.source_id", equalTo(source_id));
		  
		  //delete source by id
		  response = spogServer.deleteSourcesById(Token, source_id, test);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	  }
	  
	  @DataProvider(name = "destinationInfo")
	  public final Object[][] getDestinationInfo() {
	 	 return new Object[][]   {
	 		 {directOrg_id, directSite_id, UUID.randomUUID().toString(),"cloud_direct_volume", "creating",
	 			 "10", "2", "5","archive", "liuyu05-win10", "7D", "0", "0", "7", "0","","", directCloudAccountToken},
	 		{mspOrg_id, mspSite_id, UUID.randomUUID().toString(),"cloud_direct_volume", "creating",
	 				"10", "2", "5","archive", "liuyu05-win10", "7D", "0", "0", "7", "0","","", mspCloudAccountToken}
	 	 };
	  }
	  @Test
	  public void callDestinationApiTest(String organization_id,String site_id, String datacenterID,String destination_type, 
			  String destination_status,
			  String primary_usage,String snapshot_usage,String total_usage, String volume_type,String hostname, String  retention_id, 
			  String age_hours_max,String age_four_hours_max,String age_days_max,String age_weeks_max,String age_months_max, String age_years_max,
			  String token) {
		  System.out.println("callSourcesApiTest");
		  test = rep.startTest("callSourcesApiTest");
		  test.assignAuthor("Liu Yuefen");
		  
		  test.log(LogStatus.INFO, "Using cloud token to call destinations related api testing");
		  
		  //create destination
//		  spogDestinationServer.setToken(token);
//		  Response response = spogDestinationServer.createDestination(organization_id, site_id, datacenterID, destination_type, destination_status, primary_usage, snapshot_usage, total_usage, volume_type, hostname, retention_id, age_hours_max, age_four_hours_max, age_days_max, age_weeks_max, age_months_max, age_years_max, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, test);
//		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
//		  String destinationID = response.then().extract().path("data.destination_id");
		  
		  //update destination
//		  String destinationName = spogServer.ReturnRandom("cloud_direct_volume_name");
//		  spogDestinationServer.updatedestinationinfobydestinationIdwithCheck(destinationID, token, organization_id, site_id, datacenterID, destination_type, destination_status, destinationName, null, null, "", SpogConstants.SUCCESS_GET_PUT_DELETE, null, null, test);
//		  
//		  //get destination by id
//		  spogDestinationServer.getDestinationById(token, destinationID, test);
		  
	  }
	 /* 
	 @DataProvider(name = "postJobInfo")
	 public final Object[][] postJob() {
			return new Object[][] {
					{ System.currentTimeMillis(), System.currentTimeMillis(), directOrg_id, UUID.randomUUID().toString(), sourceID,
							UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "full", "active",
							directCloudAccountToken},
					{ System.currentTimeMillis(), System.currentTimeMillis(), mspOrg_id, UUID.randomUUID().toString(), sourceID,
								UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "full", "active",
								mspCloudAccountToken}
			};
	 }
	  
	  @Test(dataProvider = "postJobInfo")
	  public void callJobsApiTest(long startTimeTS, long endTimeTS, String organizationID, String serverID, String resourceID, String rpsID,
				String destinationID, String policyID, String jobType, String jobMethod, String jobStatus, 
				String Token) {
		  System.out.println("callJobsApiTest");
		  test = rep.startTest("callJobsApiTest");
		  test.assignAuthor("Liu Yuefen");
		  
		  test.log(LogStatus.INFO, "Using cloud token to call jobs related api testing");
		  
		  //post job
		  
		  String jobID = gatewayServer.postJobWithCheck(startTimeTS, endTimeTS, organizationID, serverID, resourceID, rpsID, destinationID, policyID, jobType, jobMethod, jobStatus, Token, test);
		  
		 //update job
		  String newJobStatus = "finished";
		  gatewayServer.updateJobWithCheck(jobID, startTimeTS, endTimeTS, organizationID, serverID, resourceID, rpsID, destinationID, policyID, jobType, jobMethod, newJobStatus, Token, test);
		  
		  //get job by id
		  response = spogServer.getJobsById(Token, jobID, test);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  
		  //get jobs
		  String additionalURL = spogServer.PrepareURL("organization_id;=;"+organizationID, "create_ts;asc", 1, 20, test);
		  spogServer.getJobs(Token, additionalURL, test);
		  
	  }
	  
	  @DataProvider(name = "postJobDataInfo")
	  public final Object[][] postJobData() {
			return new Object[][] {
					{"100", "warning", "20", "120000", "130000", "140000", "150000", "160000", directCloudAccountToken},
					{"100", "warning", "20", "120000", "130000", "140000", "150000", "160000", mspCloudAccountToken}
			};
	  }
	  
	  @Test(dataProvider = "postJobDataInfo")
	  public void callJobDataApiTest(String job_seq, String severity, String percent_complete, String protected_data_size,
			  String raw_data_size, String sync_read_size, String ntfs_volume_size, String virtual_disk_provision_size, String token) {
		  System.out.println("callJobDataApiTest");
		  test = rep.startTest("callJobDataApiTest");
		  test.assignAuthor("Liu Yuefen");
		  
		  test.log(LogStatus.INFO, "Using cloud token to call job data related api testing");
		  
		  //post job
		  String jobID = gatewayServer.postJobWithCheck(System.currentTimeMillis(), System.currentTimeMillis(), directOrg_id, UUID.randomUUID().toString(), 
				  sourceID, UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "incremental", "active", directCloudAccountToken, test);
		  //post job data
		  gatewayServer.postJobDataWithCheck(jobID, job_seq, severity, percent_complete, protected_data_size, raw_data_size, sync_read_size, ntfs_volume_size, virtual_disk_provision_size, token, test);
		 
		  //update job data
		  String newJobStatus = "finished";
		  gatewayServer.updateJobDataWithCheck(jobID, jobID, null, null, null, newJobStatus, null, null, null, null, null, null, token, test);
	  }
	  
	  @DataProvider(name = "postLogInfo")
	  public final Object[][] postLogInfo() {
			return new Object[][] {
					{System.currentTimeMillis(), directOrg_id, directOrg_id, sourceID, "information", "spog", "testLogMessage", new String[] { "node", "agent" }, directCloudAccountToken },
					{System.currentTimeMillis(), mspOrg_id, mspOrg_id, sourceID, "information", "spog", "testLogMessage", new String[] { "node", "agent" }, mspCloudAccountToken }
//					{accountSite_id, account_id, mspCloudAccountToken, "information", "spog", "testLogMessage", new String[] { "node", "agent" } }
			};
	  }
	  @Test(dataProvider = "postLogInfo")
	  public void callJoblogApiTest(long log_generate_time, String expected_organization_id,
		      String organization_id, String source_id,String log_severity_type, String log_source_type, String message_id,
		      String[] message_data, String token) {
		  System.out.println("callJoblogApiTest");
		  test = rep.startTest("callJoblogApiTest");
		  test.assignAuthor("Liu Yuefen");
		  
		  test.log(LogStatus.INFO, "Using cloud token to call job log related api testing");
		  
		  //create job 
		  test.log(LogStatus.INFO, "create job");
		  
          String jobID = gatewayServer.postJobWithCheck(System.currentTimeMillis(), System.currentTimeMillis(), organization_id, UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "Full", "finished", token, test);
		  
		  //create job log
		  test.log(LogStatus.INFO, "create job log");
		  String log_id = gatewayServer.createLogwithCheck(log_generate_time, jobID, expected_organization_id, organization_id, source_id, log_severity_type, log_source_type, message_id, message_data, token, test);
		  
		  //update job log
		  test.log(LogStatus.INFO, "update job log");
		  log4GatewayServer.updateLogWithCheck(log_id, jobID, organization_id, expected_organization_id, log_severity_type, log_source_type, message_id, message_data, true, token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		  
		  //get log by id
		  test.log(LogStatus.INFO, "get job log by id");
		  response = spogServer.getjoblogbyjobId(token, jobID, test);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  
		  //delete logs by log id
		  test.log(LogStatus.INFO, "delete logs by log id");
		  spogServer.deletelogbylogId(token, log_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		  
	  }
	  
	  @DataProvider(name = "cloudAccountInfo2")
	  public final Object[][] getCloudAccountInfo2() {
		  return new Object[][] { 
			  {spogServer.ReturnRandom("spogqa_direct_directaccountkey"),spogServer.ReturnRandom("direct_directaccount_secret"),
				  spogServer.ReturnRandom("direct_directaccount_name"),"cloud_direct",directOrg_id, mspCloudAccountToken},
			  {spogServer.ReturnRandom("spogqa_accountOrg_directaccountkey"),spogServer.ReturnRandom("accountOrg_directaccount_secret"),
					  spogServer.ReturnRandom("accountOrg_directaccount_name"),"cloud_direct",account_id, mspCloudAccountToken},
			  {spogServer.ReturnRandom("spogqa_msp_directaccountkey"),spogServer.ReturnRandom("msp_directaccount_secret"),
					  spogServer.ReturnRandom("msp_directaccount_name"),"cloud_direct",mspOrg_id, directCloudAccountToken},
			  {spogServer.ReturnRandom("spogqa_account_directaccountkey"),spogServer.ReturnRandom("account_directaccount_secret"),
						  spogServer.ReturnRandom("account_directaccount_name"),"cloud_direct",account_id, directCloudAccountToken}
	          };
		}
	  @Test (dataProvider = "cloudAccountInfo2")
	  public void DifferentOrgCloudTokenCallApiFail(String cloudAccountKey, String cloudAccountSecret, String cloudAccountName, String cloudAccountType, String organizationID, 
			   String token) {
		  System.out.println("DifferentOrgCloudTokenCallApiFail");
		  test = rep.startTest("DifferentOrgCloudTokenCallApiFail");
		  test.assignAuthor("Liu Yuefen");
		  
		  test.log(LogStatus.INFO, "Using cloud token to call cloud account related api testing");
		  
		  //create cloud account
		  Response response = spogServer.createCloudAccount(cloudAccountKey, cloudAccountSecret, cloudAccountName, cloudAccountType, organizationID, token, test);
	      spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
	      spogServer.checkErrorCode(response, "00B00004"); 
	  }
	  */
	  @Test
	  public void InvalidTokenCallApisFail() {
		  System.out.println("InvalidTokenCallApisFail");
		  test = rep.startTest("cloudAccountManagement");
		  test.assignAuthor("Liu Yuefen");
		  
		  test.log(LogStatus.INFO, "Using invalid cloud token to call cloud account related api testing");
		  
		  //create cloud account using invalid token
		  test.log(LogStatus.INFO, "Using invalid cloud token to add a new cloud account ");
		  String invalidToken = directCloudAccountToken+"_invalid";
		  Response response = spogServer.createCloudAccount(null, null,spogServer.ReturnRandom("cloud_account_name_yuefen_invalidcase"), "cloud_direct", directOrg2_id, invalidToken, test);
	      spogServer.checkResponseStatus(response, SpogConstants.NOT_LOGGED_IN, test);
	      spogServer.checkErrorCode(response, "00900006");
	      
	      //create cloud account in direct org2 fail
	      test.log(LogStatus.INFO, "Using cloud token to call cloud account related api testing");
	      String account_key = UUID.randomUUID().toString();
	      String account_secret = UUID.randomUUID().toString();
	      response = spogServer.createCloudAccount(account_key, account_secret, spogServer.ReturnRandom("cloud_account_name_yuefen_deleted"), "cloud_direct", directOrg2_id, directCloudAccountToken, test);
	      spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
	      spogServer.checkErrorCode(response, "00B00004"); 
	      
	      //create cloud account in direct org2
	      response = spogServer.createCloudAccount(account_key, account_secret, spogServer.ReturnRandom("cloud_account_name_yuefen_deleted"), "cloud_direct", directOrg2_id, directAdminToken2, test);
	      String CloudAccountID = response.then().extract().path("data.cloud_account_id");
	      //login
	      test.log(LogStatus.INFO, "cloud direct account login");
	      String token = spogServer.cloudDirectAccountLoginSuccess(account_key, account_secret, test);
	    
	      //delete cloud account
	      test.log(LogStatus.INFO, "starting to delete cloud account");
	      spogServer.setToken(directAdminToken2);
	      spogServer.deleteCloudAccountWithExpectedStatusCode(CloudAccountID, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	      
	      //use the deleted token to call api
	      test.log(LogStatus.INFO, "using the deleted token to call create cloud account api");
	      response = spogServer.createSourceWithCloudToken(spogServer.ReturnRandom("yuefen_source_direct2_directSite"), SourceType.machine,  SourceProduct.udp,  directOrg2_id, directSite2_id, 
		 			 ProtectionStatus.protect, ConnectionStatus.online,"windows","sql", null, null, null, null, null, null, null, null, token, test);
	      spogServer.checkResponseStatus(response, SpogConstants.NOT_LOGGED_IN);
	      spogServer.checkErrorCode(response, "");
	      
	      //create user with cloud token
	      test.log(LogStatus.INFO, "using cloud account token to call user api");
	      spogServer.setToken(directCloudAccountToken);
	      response = spogServer.createUser(spogServer.ReturnRandom("user_yuefen_liu_email1@spogqa.com"), sharePassword, "yuefen", "liu", "direct_admin", directOrg_id, test);
	      spogServer.checkResponseStatus(response, SpogConstants.NOT_LOGGED_IN);
	      spogServer.checkErrorCode(response, "");
	      
	      //create org with cloud token
	      test.log(LogStatus.INFO, "using cloud account token to call org api");
	      spogServer.setToken(directCloudAccountToken);
	      response = spogServer.CreateOrganization(spogServer.ReturnRandom("org_yuefen_spogqa"), "direct", null, null, "yuefen", "liu");
	      spogServer.checkResponseStatus(response, SpogConstants.NOT_LOGGED_IN);
	      spogServer.checkErrorCode(response, "");
	  }
	  
}
