package api.sources.groups;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.SpogConstants;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import InvokerServer.GatewayServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import java.text.SimpleDateFormat;

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

public class GetSourceListFromOneGroupTest extends base.prepare.PrepareOrgInfo {
	@Parameters({ "pmfKey"})
	  public  GetSourceListFromOneGroupTest(String pmfKey) {
			super(pmfKey);
			// TODO Auto-generated constructor stub
		}
	  private GatewayServer gatewayServer;
	  private SPOGServer spogServer;
	  private UserSpogServer userSpogServer;
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
	  private String csrReadOnlyUser = "liuyu05@arcserve.com";
	  private String sharePassword = "Caworld_2018";
//	  private ExtentReports rep;
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
	  private String mspAccountAdminToken;
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
	  private String mspAccountAdminName;
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
	  private String  org_prefix=this.getClass().getSimpleName();
	  
	  //this is for update portal, each testng class is taken as BQ set
//	  private SQLServerDb bqdb1;
//	  public int Nooftest;
//	  private long creationTime;
//	  private String BQName=null;
//	  private String runningMachine;
//	  private testcasescount count1;
//	  private String buildVersion;
	  
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
			System.out.println("GetSourceListFromOneGroupTest");
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
		  setEnv(baseURI,  port,  userName, password);
		  //end 
		  rep = ExtentManager.getInstance("GetSourceListFromOneGroupTest",logFolder);
		  test = rep.startTest("beforeClass");
		  spogServer = new SPOGServer(baseURI, port);
		  userSpogServer = new UserSpogServer(baseURI, port);
		  gatewayServer =new GatewayServer(baseURI,port);
		  spogServer.userLogin(userName, password);
		  String csr_token = spogServer.getJWTToken();
		  
		  this.csrGlobalLoginUser = userName;
		  this.csrGlobalLoginPassword = password;
		  
		  //create direct org, user, site and group
		  directOrg_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_group_direct1_yuefen")+org_prefix,"direct",spogServer.ReturnRandom("yuefen_direct1_group@spogqa.com"),sharePassword,"yuefen","liu");
		  
		  this.directUserName= spogServer.ReturnRandom("getGroup_direct_admin_yuefen@spogqa.com");
		  String direct_userId = spogServer.createUserAndCheck(directUserName, sharePassword, "yuefen", "liu", "direct_admin", directOrg_id, test);
		  spogServer.userLogin(directUserName, sharePassword);
		  this.directAdminToken = spogServer.getJWTToken();
		  
		  this.directSite_id = gatewayServer.createsite_register_login(directOrg_id, directAdminToken, direct_userId, "yuefen", "1.0.0", spogServer, test);
		  
		  String directGroupName = spogServer.ReturnRandom("direct_groupx_yuefen");
		  this.directGroup_id = spogServer.createGroupWithCheck(directOrg_id, directGroupName, "", test);
		  
		  //create another direct org, user, site and group
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword);
		  
		  directOrg2_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_group_direct2_yuefen")+org_prefix,"direct",spogServer.ReturnRandom("yuefen_direct2_group@spogqa.com"),sharePassword,"yuefen","liu");
		
		  this.directUserName2= spogServer.ReturnRandom("getGroup_direct_admin2_yuefen@spogqa.com");
		  String direct_user2Id = spogServer.createUserAndCheck(directUserName2, sharePassword, "yuefen", "liu", "direct_admin", directOrg2_id, test);
		  spogServer.userLogin(directUserName2, sharePassword);
		  this.directAdminToken2 = spogServer.getJWTToken();
		  
		  this.directSite2_id = gatewayServer.createsite_register_login(directOrg2_id, directAdminToken2, direct_user2Id, "yuefen", "1.0.0", spogServer, test);
		  
		  String directGroupName2 = spogServer.ReturnRandom("direct_groupx2_yuefen");
		  this.directGroup2_id = spogServer.createGroupWithCheck(directOrg2_id, directGroupName2, "", test);
		  
		  //create direct org3 and group3
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword);
		  directOrg3_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_group_direct3_yuefen")+org_prefix,"direct",spogServer.ReturnRandom("yuefen_direct3_group@spogqa.com"),sharePassword,"yuefen","liu");
		  this.directUserName3= spogServer.ReturnRandom("getGroup_direct_admin3_yuefen@spogqa.com");
		  String direct_user3Id = spogServer.createUserAndCheck(directUserName3, sharePassword, "yuefen", "liu", "direct_admin", directOrg3_id, test);
		  spogServer.userLogin(directUserName3, sharePassword);
		  this.directAdminToken3 = spogServer.getJWTToken();
		  
		  this.directSite3_id = gatewayServer.createsite_register_login(directOrg3_id, directAdminToken3, direct_user3Id, "yuefen", "1.0.0", spogServer, test);
		  
		  String directGroupName3 = spogServer.ReturnRandom("direct_groupx3_yuefen");
		  this.directGroup3_id = spogServer.createGroupWithCheck(directOrg3_id, directGroupName3, "", test);
		  
		  //create msp org, user, site and group
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword);
		  mspOrg_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_group_yuefen_msp1")+org_prefix,"msp",spogServer.ReturnRandom("yuefen_msp1_groupx@spogqa.com"),sharePassword,"yuefen","liu");
		  
		  this.mspUserName = spogServer.ReturnRandom("getGroup_msp_admin_yuefen@spogqa.com");
		  String msp_userId = spogServer.createUserAndCheck(mspUserName, sharePassword, "yuefen", "liu", "msp_admin", mspOrg_id, test);
		  spogServer.userLogin(mspUserName, sharePassword);
		  this.mspAdminToken = spogServer.getJWTToken();
		  
		  this.mspAccountAdminName = spogServer.ReturnRandom("Group_msp_account_admin_yuefen@spogqa.com");
		  String msp_account_admin_Id = spogServer.createUserAndCheck(mspAccountAdminName, sharePassword, "yuefen", "liu", "msp_account_admin", mspOrg_id, test);

		  this.mspSite_id = gatewayServer.createsite_register_login(mspOrg_id, mspAdminToken, msp_userId, "yuefen", "1.0.0", spogServer, test);
		  
		  String mspGroupName = spogServer.ReturnRandom("msp_groupx_yuefen");
		  this.mspGroup_id = spogServer.createGroupWithCheck(mspOrg_id, mspGroupName, "", test);
		  
		  //create another msp org, user, site and group
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword);
		  mspOrg2_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_group_org2_yuefen_msp2")+org_prefix,"msp",spogServer.ReturnRandom("yuefen_msp2_groupx2@spogqa.com"),sharePassword,"yuefen","liu");
		  
		  this.mspUserName2 = spogServer.ReturnRandom("getGroup_msp2_admin_yuefen@spogqa.com");
		  String msp_user2Id = spogServer.createUserAndCheck(mspUserName2, sharePassword, "yuefen", "liu", "msp_admin", mspOrg2_id, test);
		  spogServer.userLogin(mspUserName2, sharePassword);
		  this.mspAdminToken2 = spogServer.getJWTToken();
		  
		  this.mspSite2_id = gatewayServer.createsite_register_login(mspOrg2_id, mspAdminToken2, msp_user2Id, "yuefen", "1.0.0", spogServer, test);
		  
		  String mspGroupName2 = spogServer.ReturnRandom("msp_groupx2_yuefen");
		  this.mspGroup2_id = spogServer.createGroupWithCheck(mspOrg2_id, mspGroupName, "", test);
		  
		  //create account, user, site and group
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword);
		  this.account_id = spogServer.createAccountWithCheck(mspOrg_id,spogServer.ReturnRandom("spogqa_accoun1_msp1_yuefen_groupx")+org_prefix, mspOrg_id, test);
		  
		  String[] userIds = {msp_account_admin_Id};
		  userSpogServer.assignMspAccountAdmins(mspOrg_id, account_id, userIds, csr_token);
		  
		  spogServer.userLogin(mspAccountAdminName, sharePassword);
		  this.mspAccountAdminToken = spogServer.getJWTToken();
		  
		  this.accountUserName= spogServer.ReturnRandom("getGroup_account_admin_yuefen@spogqa.com");
		  String account_userId = spogServer.createUserAndCheck(accountUserName, sharePassword, "yuefen", "liu", "direct_admin", account_id, test);
		  spogServer.userLogin(accountUserName, sharePassword);
		  this.accountAdminToken = spogServer.getJWTToken();
		  
		  this.accountSite_id = gatewayServer.createsite_register_login(account_id, accountAdminToken, account_userId, "yuefen", "1.0.0", spogServer, test);
		  
		  String accountGroupName = spogServer.ReturnRandom("account_groupx_yuefen");
		  this.accountGroup_id = spogServer.createGroupWithCheck(account_id, accountGroupName, "", test);
		  
		  //create another account, user, site and group
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword);
		  this.account2_id = spogServer.createAccountWithCheck(mspOrg2_id,spogServer.ReturnRandom("spogqa_accoun2_msp2_yuefen_groupx2")+org_prefix, mspOrg2_id, test);
		  
		  this.accountUserName2= spogServer.ReturnRandom("getGroup_account_admin2_yuefen@spogqa.com");
		  String account_user2Id = spogServer.createUserAndCheck(accountUserName2, sharePassword, "yuefen", "liu", "direct_admin", account2_id, test);
		  spogServer.userLogin(accountUserName2, sharePassword);
		  this.accountAdminToken2 = spogServer.getJWTToken();
		  
		  this.accountSite2_id = gatewayServer.createsite_register_login(account2_id, accountAdminToken2, account_user2Id, "yuefen", "1.0.0", spogServer, test);
		  
		  String accountGroupName2 = spogServer.ReturnRandom("account_groupx2_yuefen");
		  this.accountGroup2_id = spogServer.createGroupWithCheck(account2_id, accountGroupName2, "", test);
		  
		  //csr admin token
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword);
		  this.csrAdminToken = spogServer.getJWTToken();
		  
		  prepare(baseURI, port, logFolder, this.csrGlobalLoginUser,  this.csrGlobalLoginPassword, this.getClass().getSimpleName() );
	  }
	  
	  @DataProvider(name = "sourceInfo100")
	  public final Object[][] getSourceInfo100() {
	 	 return new Object[][]   {
	 		 {this.final_root_msp_user_name_email, this.common_password, spogServer.ReturnRandom("yuefen_sourcex1_mspSite"), SourceType.machine,  SourceProduct.udp,  this.root_msp_direct_org_id, this.root_msp_direct_org_site1_siteId, ProtectionStatus.protect, ConnectionStatus.online,
	 		"windows",	"sql"},
	 		 {this.final_sub_msp1_user_name_email, this.common_password, spogServer.ReturnRandom("yuefen_sourcex1_accountSite"), SourceType.machine,  SourceProduct.udp,  this.sub_msp1_account1_id, this.sub_msp1_account1_site1_siteId, ProtectionStatus.protect, ConnectionStatus.online,
	 			"windows",	"sql"}
	 	 };
	  }
	  
	  @Test(dataProvider = "sourceInfo100")
	  public void rootMspGetSourceListSuccess(String loginUser, String loginPassword,String source_name, SourceType source_type, SourceProduct source_product, String organization_id,
			  String site_id, ProtectionStatus protection_status, ConnectionStatus connection_status, String os_major, String applications){	 
		  System.out.println("rootMspGetSourceListSuccess");
		  test = rep.startTest("rootMspGetSourceListSuccess");
		  test.assignAuthor("Liu Yuefen");
		  
		  int num=2;
		  int i=0;
		  String[] arrayOfSourceNodes= new String[num];
		  ArrayList<ResponseBody> sourceInfo = new ArrayList<ResponseBody>();
		  
		  //login with csr
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword);
		  String csr_token =spogServer.getJWTToken();
		  //create source
			for(i=0;i<num;i++) {
				Response response = spogServer.createSource(source_name+"_"+i, source_type, source_product, organization_id, site_id, protection_status, connection_status, os_major, applications, null, null, null, null, null, null, null, null, test);
				sourceInfo.add(response);
				arrayOfSourceNodes[i] = response.then().extract().path("data.source_id");			
			}
			
		  //create group
		  String groupId=spogServer.createGroupWithCheck(organization_id, spogServer.ReturnRandom("account_groupx_yuefen"), "", test);
		  
		  //add source to group
		  spogServer.addSourcetoSourceGroupwithCheck(groupId, arrayOfSourceNodes, csr_token, SpogConstants.SUCCESS_POST, null, test);
		
		  //login with msp user
		  spogServer.userLogin(loginUser, loginPassword);
		  String token=spogServer.getJWTToken();
		  //get the source list
		  Response response = spogServer.getSourceListFromOneGroup(groupId, 1, -1, test);
		  //check the source list
		  spogServer.getSourceListFromOneGroupWithCheck(response, SpogConstants.SUCCESS_GET_PUT_DELETE, sourceInfo, 1, -1, 2, "", test);
		  //remove the source from group
		  for (i=0; i<num; i++) {
		  spogServer.deleteSourcefromSourceGroupwithCheck(groupId, arrayOfSourceNodes[i], token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		  }
	  }
	  
	  @DataProvider(name = "sourceInfo101")
	  public final Object[][] getSourceInfo101() {
	 	 return new Object[][]   {
	 		 {this.final_sub_msp1_user_name_email, this.common_password, spogServer.ReturnRandom("yuefen_sourcex1_mspSite"), SourceType.machine,  SourceProduct.udp,  this.root_msp_direct_org_id, this.root_msp_direct_org_site1_siteId, ProtectionStatus.protect, ConnectionStatus.online,
	 		"windows",	"sql"},
	 		 {this.mspUserName, sharePassword, spogServer.ReturnRandom("yuefen_sourcex1_mspSite"), SourceType.machine,  SourceProduct.udp,  this.root_msp_direct_org_id, this.root_msp_direct_org_site1_siteId, ProtectionStatus.protect, ConnectionStatus.online,
		 		"windows",	"sql"},
	 		 {this.directUserName, sharePassword, spogServer.ReturnRandom("yuefen_sourcex1_mspSite"), SourceType.machine,  SourceProduct.udp,  this.root_msp_direct_org_id, this.root_msp_direct_org_site1_siteId, ProtectionStatus.protect, ConnectionStatus.online,
			 		"windows",	"sql"},
	 		
	 		
	 		 {this.final_root_msp_user_name_email, this.common_password, spogServer.ReturnRandom("yuefen_sourcex1_accountSite"), SourceType.machine,  SourceProduct.udp,  this.sub_msp1_account1_id, this.sub_msp1_account1_site1_siteId, ProtectionStatus.protect, ConnectionStatus.online,
	 			"windows",	"sql"},
	 		 {this.mspUserName, sharePassword, spogServer.ReturnRandom("yuefen_sourcex1_accountSite"), SourceType.machine,  SourceProduct.udp,  this.sub_msp1_account1_id, this.sub_msp1_account1_site1_siteId, ProtectionStatus.protect, ConnectionStatus.online,
		 			"windows",	"sql"},	
	 		 {this.directUserName, sharePassword, spogServer.ReturnRandom("yuefen_sourcex1_accountSite"), SourceType.machine,  SourceProduct.udp,  this.sub_msp1_account1_id, this.sub_msp1_account1_site1_siteId, ProtectionStatus.protect, ConnectionStatus.online,
			 			"windows",	"sql"},
	 		 
	 		 
	 		 {this.final_root_msp_user_name_email, this.common_password, spogServer.ReturnRandom("yuefen_sourcex1_accountSite"), SourceType.machine,  SourceProduct.udp,  this.account_id, this.accountSite_id, ProtectionStatus.protect, ConnectionStatus.online,
				 			"windows",	"sql"},
	 		 {this.final_sub_msp1_user_name_email, this.common_password, spogServer.ReturnRandom("yuefen_sourcex1_mspSite"), SourceType.machine,  SourceProduct.udp,  this.account_id, this.accountSite_id, ProtectionStatus.protect, ConnectionStatus.online,
			 		 		"windows",	"sql"},
	 	 };
	  }
	  
	  @Test(dataProvider = "sourceInfo101")
	  public void rootMspGetSourceListFail(String loginUser, String loginPassword,String source_name, SourceType source_type, SourceProduct source_product, String organization_id,
			  String site_id, ProtectionStatus protection_status, ConnectionStatus connection_status, String os_major, String applications ){	 
		  System.out.println("rootMspGetSourceListFail");
		  test = rep.startTest("rootMspGetSourceListFail");
		  test.assignAuthor("Liu Yuefen");
		  
		  int num=2;
		  int i=0;
		  String[] arrayOfSourceNodes= new String[num];
		  ArrayList<ResponseBody> sourceInfo = new ArrayList<ResponseBody>();
		  
		  //login with csr
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword);
		  String csr_token =spogServer.getJWTToken();
		  //create source
			for(i=0;i<num;i++) {
				Response response = spogServer.createSource(source_name+"_"+i, source_type, source_product, organization_id, site_id, protection_status, connection_status, os_major, applications, null, null, null, null, null, null, null, null, test);
				sourceInfo.add(response);
				arrayOfSourceNodes[i] = response.then().extract().path("data.source_id");			
			}
			
		  //create group
		  String groupId=spogServer.createGroupWithCheck(organization_id, spogServer.ReturnRandom("account_groupx_yuefen"), "", test);
		  
		  //add source to group
		  spogServer.addSourcetoSourceGroupwithCheck(groupId, arrayOfSourceNodes, csr_token, SpogConstants.SUCCESS_POST, null, test);
		
		  //login with msp user
		  spogServer.userLogin(loginUser, loginPassword);
		  String token=spogServer.getJWTToken();

		  //get the source list
		  Response response = spogServer.getSourceListFromOneGroup(groupId, 1, -1, test);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);

		  //remove the source from group
		  for (i=0; i<num; i++) {
		  spogServer.deleteSourcefromSourceGroupwithCheck(groupId, arrayOfSourceNodes[i], csr_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		  }
	  }
	  
	  @DataProvider(name = "sourceInfo16")
	  public final Object[][] getSourceInfo16() {
		 	 return new Object[][]   {{spogServer.ReturnRandom("yuefen_sourcex6_directSite"), SourceType.machine,  SourceProduct.udp,  directOrg_id, directSite_id, ProtectionStatus.protect, ConnectionStatus.online,
		 		"windows",	"sql",  directGroup_id, directAdminToken},
		 	
		 		                     {spogServer.ReturnRandom("yuefen_sourcex6_accountSite2222"), SourceType.machine,  SourceProduct.udp,  account2_id, accountSite2_id, ProtectionStatus.protect, ConnectionStatus.online,
		 				"windows",	"sql",  accountGroup2_id, accountAdminToken2}};
	 }
		 
	  
	  @Test(dataProvider = "sourceInfo16")
	  //page = 1, page size = default:20
	  public void csrReadOnlyUserGetSourceList(String source_name, SourceType source_type, SourceProduct source_product, String organization_id,
			  String site_id, ProtectionStatus protection_status, ConnectionStatus connection_status, String os_major,
		      String applications, String groupId, String token ){	 
		  System.out.println("csrReadOnlyUserGetSourceList");
		  test = rep.startTest("csrReadOnlyUserGetSourceList");
		  test.assignAuthor("Liu Yuefen");
		  
		  int num=2;
		  int i=0;
		  String[] arrayOfSourceNodes= new String[num];
		  ArrayList<ResponseBody> sourceInfo = new ArrayList<ResponseBody>();
		 
		  //login with csr
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword);
		  
		  //create source
			for(i=0;i<num;i++) {
				Response response = spogServer.createSource(source_name+"_"+i, source_type, source_product, organization_id, site_id, protection_status, connection_status, os_major, applications, null, null, null, null, null, null, null, null, test);
				sourceInfo.add(response);
				arrayOfSourceNodes[i] = response.then().extract().path("data.source_id");			
			}
	
		  //add source to group
		  spogServer.addSourcetoSourceGroupwithCheck(groupId, arrayOfSourceNodes, token, SpogConstants.SUCCESS_POST, null, test);
		  
		  //login with csr read-only user
		  spogServer.userLogin(csrReadOnlyUser, sharePassword);
		   
		  //get the source list
		  Response response = spogServer.getSourceListFromOneGroup(groupId, 1, -1, test);
	
		  
		  //check the source list
		  spogServer.getSourceListFromOneGroupWithCheck(response, SpogConstants.SUCCESS_GET_PUT_DELETE, sourceInfo, 1, -1, 2, "", test);
		  
		  //login with csr admin
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword);
		  //remove the source from group
		  for (i=0; i<num; i++) {
		  spogServer.deleteSourcefromSourceGroupwithCheck(groupId, arrayOfSourceNodes[i], token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		   }
	  }
	  
	  @DataProvider(name = "sourceInfo14")
	  public final Object[][] getSourceInfo14() {
	 	 return new Object[][]   {
	 		 {spogServer.ReturnRandom("yuefen_sourcex1_mspSite"), SourceType.machine,  SourceProduct.udp,  mspOrg_id, mspSite_id, ProtectionStatus.protect, ConnectionStatus.online,
	 		"windows",	"sql", mspGroup_id, mspAdminToken},
	 		 {spogServer.ReturnRandom("yuefen_sourcex1_accountSite"), SourceType.machine,  SourceProduct.udp,  account_id, accountSite_id, ProtectionStatus.protect, ConnectionStatus.online,
	 			"windows",	"sql", accountGroup_id, accountAdminToken}};
	  }

	  @Test(dataProvider = "sourceInfo14")
	  public void mspAccountAdminGetSourceListSuccess(String source_name, SourceType source_type, SourceProduct source_product, String organization_id,
			  String site_id, ProtectionStatus protection_status, ConnectionStatus connection_status, String os_major,
		      String applications, String groupId, String token ){	 
		  System.out.println("mspAccountAdminGetSourceListSuccess");
		  test = rep.startTest("mspAccountAdminGetSourceListSuccess");
		  test.assignAuthor("Liu Yuefen");
		  
		  int num=2;
		  int i=0;
		  String[] arrayOfSourceNodes= new String[num];
		  ArrayList<ResponseBody> sourceInfo = new ArrayList<ResponseBody>();
		  
		  //login with csr
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword);
		  
		  //create source
			for(i=0;i<num;i++) {
				Response response = spogServer.createSource(source_name+"_"+i, source_type, source_product, organization_id, site_id, protection_status, connection_status, os_major, applications, null, null, null, null, null, null, null, null, test);
				sourceInfo.add(response);
				arrayOfSourceNodes[i] = response.then().extract().path("data.source_id");			
			}
	
		  //add source to group
		  spogServer.addSourcetoSourceGroupwithCheck(groupId, arrayOfSourceNodes, token, SpogConstants.SUCCESS_POST, null, test);
		
		  //login with msp user
		  spogServer.userLogin(mspAccountAdminName, sharePassword);
		  //get the source list
		  Response response = spogServer.getSourceListFromOneGroup(groupId, 1, -1, test);
		  //check the source list
		  spogServer.getSourceListFromOneGroupWithCheck(response, SpogConstants.SUCCESS_GET_PUT_DELETE, sourceInfo, 1, -1, 2, "", test);
		  //remove the source from group
		  for (i=0; i<num; i++) {
		  spogServer.deleteSourcefromSourceGroupwithCheck(groupId, arrayOfSourceNodes[i], token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		  }
	  }
	  
	  
	  @DataProvider(name = "sourceInfo15")
	  public final Object[][] getSourceInfo15() {
		 	 return new Object[][]   {
//	 		                          {spogServer.ReturnRandom("yuefen_sourcex1_mspSite"), SourceType.machine,  SourceProduct.udp,  mspOrg_id, mspSite_id, ProtectionStatus.protect, ConnectionStatus.online,
//			 		"windows",	"sql", mspGroup_id, mspAdminToken},
		 		                      {spogServer.ReturnRandom("yuefen_sourcex2_directSite"), SourceType.machine,  SourceProduct.udp,  directOrg_id, directSite_id, ProtectionStatus.protect, ConnectionStatus.online,
		 		"windows",	"sql", directGroup_id, directAdminToken},
		 		                      {spogServer.ReturnRandom("yuefen_sourcex2_mspSite2"), SourceType.machine,  SourceProduct.udp,  mspOrg2_id, mspSite2_id, ProtectionStatus.protect, ConnectionStatus.online,
		 			"windows",	"sql",  mspGroup2_id, mspAdminToken2},
		 		                      {spogServer.ReturnRandom("yuefen_sourcex2_accountSite2"), SourceType.machine,  SourceProduct.udp,  account2_id, accountSite2_id, ProtectionStatus.protect, ConnectionStatus.online,
		 				"windows",	"sql",  accountGroup2_id, accountAdminToken2}};
		  
	  }
	  @Test(dataProvider = "sourceInfo15")
	  public void mspAccountAdminGetSourceListFail(String source_name, SourceType source_type, SourceProduct source_product, String organization_id,
			  String site_id, ProtectionStatus protection_status, ConnectionStatus connection_status, String os_major,
		      String applications, String groupId, String token ){	 
		  System.out.println("mspAccountAdminGetSourceListFail");
		  test = rep.startTest("mspAccountAdminGetSourceListFail");
		  test.assignAuthor("Liu Yuefen");
		  
		  int num=2;
		  int i=0;
		  String[] arrayOfSourceNodes= new String[num];
		  
		  ArrayList<ResponseBody> sourceInfo = new ArrayList<ResponseBody>();
		  //login with csr
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword);
		  
		  //create source
			for(i=0;i<num;i++) {
				Response response = spogServer.createSource(source_name+"_"+i, source_type, source_product, organization_id, site_id, protection_status, connection_status, os_major, applications, null, null, null, null, null, null, null, null, test);
				sourceInfo.add(response);
				arrayOfSourceNodes[i] = response.then().extract().path("data.source_id");			
			}
	
		  //add source to group
		  spogServer.addSourcetoSourceGroupwithCheck(groupId, arrayOfSourceNodes, token, SpogConstants.SUCCESS_POST, null, test);
		
		  //login with msp user
		  spogServer.userLogin(mspAccountAdminName, sharePassword);
		  //get the source list
		  Response response = spogServer.getSourceListFromOneGroup(groupId, 1, -1, test);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
		  spogServer.checkErrorCode(response, "00100101");
		  
		  //login with csr
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword);
		  //remove the source from group
		  for (i=0; i<num; i++) {
		  spogServer.deleteSourcefromSourceGroupwithCheck(groupId, arrayOfSourceNodes[i], token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		  }
	  }
	  
	  //used by csr
	  @DataProvider(name = "sourceInfo")
	  public final Object[][] getSourceInfo() {
	 	 return new Object[][]   {
	 		 {spogServer.ReturnRandom("yuefen_source_directSite"), SourceType.machine,  SourceProduct.udp,  directOrg_id, directSite_id, ProtectionStatus.protect, ConnectionStatus.online,
		 				"windows",	"sql", directGroup_id, csrAdminToken},
	 		                      {spogServer.ReturnRandom("yuefen_source_mspSite"), SourceType.machine,  SourceProduct.udp,  mspOrg_id, mspSite_id, ProtectionStatus.protect, ConnectionStatus.online,
			 				"windows",	"sql",  mspGroup_id, csrAdminToken},
	 		                      {spogServer.ReturnRandom("yuefen_source_accountSite"), SourceType.machine,  SourceProduct.udp,  account_id, accountSite_id, ProtectionStatus.protect, ConnectionStatus.online,
				 				"windows","sql",  accountGroup_id, csrAdminToken}};
	  }
	 
	  //used by msp
	  @DataProvider(name = "sourceInfo1")
	  public final Object[][] getSourceInfo1() {
	 	 return new Object[][]   {{spogServer.ReturnRandom("yuefen_sourcex1_mspSite"), SourceType.machine,  SourceProduct.udp,  mspOrg_id, mspSite_id, ProtectionStatus.protect, ConnectionStatus.online,
	 		"windows",	"sql", mspGroup_id, mspAdminToken},
	 		                      {spogServer.ReturnRandom("yuefen_sourcex1_accountSite"), SourceType.machine,  SourceProduct.udp,  account_id, accountSite_id, ProtectionStatus.protect, ConnectionStatus.online,
	 			"windows",	"sql", accountGroup_id, accountAdminToken}};
	  }


	  @DataProvider(name = "sourceInfo2")
	  public final Object[][] getSourceInfo2() {
		 	 return new Object[][]   {{spogServer.ReturnRandom("yuefen_sourcex2_directSite"), SourceType.machine,  SourceProduct.udp,  directOrg_id, directSite_id, ProtectionStatus.protect, ConnectionStatus.online,
		 		"windows",	"sql", directGroup_id, directAdminToken},
		 		                      {spogServer.ReturnRandom("yuefen_sourcex2_mspSite2"), SourceType.machine,  SourceProduct.udp,  mspOrg2_id, mspSite2_id, ProtectionStatus.protect, ConnectionStatus.online,
		 			"windows",	"sql",  mspGroup2_id, mspAdminToken2},
		 		                      {spogServer.ReturnRandom("yuefen_sourcex2_accountSite2"), SourceType.machine,  SourceProduct.udp,  account2_id, accountSite2_id, ProtectionStatus.protect, ConnectionStatus.online,
		 				"windows",	"sql",  accountGroup2_id, accountAdminToken2}};
		  
	  }
	  //used by direct
	  @DataProvider(name = "sourceInfo3")
	  public final Object[][] getSourceInfo3() {
	 	 return new Object[][]   {{spogServer.ReturnRandom("yuefen_sourcex3_directSite"), SourceType.machine,  SourceProduct.udp,  directOrg_id, directSite_id, ProtectionStatus.protect, ConnectionStatus.online,
	 		"windows",	"sql",  directGroup_id, directAdminToken}};
	  }
	  @DataProvider(name = "sourceInfo4")
	  public final Object[][] getSourceInfo4() {
	 	 return new Object[][]   {{spogServer.ReturnRandom("yuefen_sourcex4_directSite2"), SourceType.machine,  SourceProduct.udp,  directOrg2_id, directSite2_id, ProtectionStatus.protect, ConnectionStatus.online,
	 		"windows",	"sql",  directGroup2_id, directAdminToken2},
	 		                      {spogServer.ReturnRandom("yuefen_sourcex4_mspSite"), SourceType.machine,  SourceProduct.udp,  mspOrg_id, mspSite_id, ProtectionStatus.protect, ConnectionStatus.online,
	 			"windows",	"sql", mspGroup_id, mspAdminToken},
	 		                      {spogServer.ReturnRandom("yuefen_sourcex4_accountSite"), SourceType.machine,  SourceProduct.udp,  account_id, accountSite_id, ProtectionStatus.protect, ConnectionStatus.online,
	 				"windows",	"sql",  accountGroup_id, accountAdminToken}};
	  }
	  //used by account
	  @DataProvider(name = "sourceInfo5")
	  public final Object[][] getSourceInfo5() {
	 	 return new Object[][]   {{spogServer.ReturnRandom("yuefen_sourcex5_accountSite"), SourceType.machine,  SourceProduct.udp,  account_id, accountSite_id, ProtectionStatus.protect, ConnectionStatus.online,
	 		"windows",	"sql",  accountGroup_id, accountAdminToken}};
	  }
	  @DataProvider(name = "sourceInfo6")
	  public final Object[][] getSourceInfo6() {
		 	 return new Object[][]   {{spogServer.ReturnRandom("yuefen_sourcex6_directSite"), SourceType.machine,  SourceProduct.udp,  directOrg_id, directSite_id, ProtectionStatus.protect, ConnectionStatus.online,
		 		"windows",	"sql",  directGroup_id, directAdminToken},
		 		                      {spogServer.ReturnRandom("yuefen_sourcex6_mspSite"), SourceType.machine,  SourceProduct.udp,  mspOrg_id, mspSite_id, ProtectionStatus.protect, ConnectionStatus.online,
		 			"windows",	"sql",  mspGroup_id, mspAdminToken},
		 		                     {spogServer.ReturnRandom("yuefen_sourcex6_accountSite2222"), SourceType.machine,  SourceProduct.udp,  account2_id, accountSite2_id, ProtectionStatus.protect, ConnectionStatus.online,
		 				"windows",	"sql",  accountGroup2_id, accountAdminToken2}};
	 }
		 
	  
	  @Test(dataProvider = "sourceInfo")
	  //page = 1, page size = default:20
	  public void csrGetSourceList(String source_name, SourceType source_type, SourceProduct source_product, String organization_id,
			  String site_id, ProtectionStatus protection_status, ConnectionStatus connection_status, String os_major,
		      String applications, String groupId, String token ){	 
		  System.out.println("csrGetSourceList");
		  test = rep.startTest("csrGetSourceList");
		  test.assignAuthor("Liu Yuefen");
		  
		  int num=2;
		  int i=0;
		  String[] arrayOfSourceNodes= new String[num];
		  ArrayList<ResponseBody> sourceInfo = new ArrayList<ResponseBody>();
		 
		  //login with csr
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword);
		  
		  //create source
			for(i=0;i<num;i++) {
				Response response = spogServer.createSource(source_name+"_"+i, source_type, source_product, organization_id, site_id, protection_status, connection_status, os_major, applications, null, null, null, null, null, null, null, null, test);
				sourceInfo.add(response);
				arrayOfSourceNodes[i] = response.then().extract().path("data.source_id");			
			}
	
		  //add source to group
		  spogServer.addSourcetoSourceGroupwithCheck(groupId, arrayOfSourceNodes, token, SpogConstants.SUCCESS_POST, null, test);
		   
		  //get the source list
		  Response response = spogServer.getSourceListFromOneGroup(groupId, 1, -1, test);
	
		  
		  //check the source list
		  spogServer.getSourceListFromOneGroupWithCheck(response, SpogConstants.SUCCESS_GET_PUT_DELETE, sourceInfo, 1, -1, 2, "", test);
		  
		  //remove the source from group
		  for (i=0; i<num; i++) {
		  spogServer.deleteSourcefromSourceGroupwithCheck(groupId, arrayOfSourceNodes[i], token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		   }
	  }
	  
	  
	  @Test(dataProvider = "sourceInfo1")
	  public void mspGetSourceListSuccess(String source_name, SourceType source_type, SourceProduct source_product, String organization_id,
			  String site_id, ProtectionStatus protection_status, ConnectionStatus connection_status, String os_major,
		      String applications, String groupId, String token ){	 
		  System.out.println("mspGetSourceList");
		  test = rep.startTest("mspGetSourceList");
		  test.assignAuthor("Liu Yuefen");
		  
		  int num=2;
		  int i=0;
		  String[] arrayOfSourceNodes= new String[num];
		  ArrayList<ResponseBody> sourceInfo = new ArrayList<ResponseBody>();
		  
		  //login with csr
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword);
		  
		  //create source
			for(i=0;i<num;i++) {
				Response response = spogServer.createSource(source_name+"_"+i, source_type, source_product, organization_id, site_id, protection_status, connection_status, os_major, applications, null, null, null, null, null, null, null, null, test);
				sourceInfo.add(response);
				arrayOfSourceNodes[i] = response.then().extract().path("data.source_id");			
			}
	
		  //add source to group
		  spogServer.addSourcetoSourceGroupwithCheck(groupId, arrayOfSourceNodes, token, SpogConstants.SUCCESS_POST, null, test);
		
		  //login with msp user
		  spogServer.userLogin(mspUserName, sharePassword);
		  //get the source list
		  Response response = spogServer.getSourceListFromOneGroup(groupId, 1, -1, test);
		  //check the source list
		  spogServer.getSourceListFromOneGroupWithCheck(response, SpogConstants.SUCCESS_GET_PUT_DELETE, sourceInfo, 1, -1, 2, "", test);
		  //remove the source from group
		  for (i=0; i<num; i++) {
		  spogServer.deleteSourcefromSourceGroupwithCheck(groupId, arrayOfSourceNodes[i], token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		  }
	  }
	  
	  @Test(dataProvider = "sourceInfo2")
	  public void mspGetSourceListFail(String source_name, SourceType source_type, SourceProduct source_product, String organization_id,
			  String site_id, ProtectionStatus protection_status, ConnectionStatus connection_status, String os_major,
		      String applications, String groupId, String token ){	 
		  System.out.println("mspGetSourceListFail");
		  test = rep.startTest("mspGetSourceListFail");
		  test.assignAuthor("Liu Yuefen");
		  
		  int num=2;
		  int i=0;
		  String[] arrayOfSourceNodes= new String[num];
		  
		  ArrayList<ResponseBody> sourceInfo = new ArrayList<ResponseBody>();
		  //login with csr
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword);
		  
		  //create source
			for(i=0;i<num;i++) {
				Response response = spogServer.createSource(source_name+"_"+i, source_type, source_product, organization_id, site_id, protection_status, connection_status, os_major, applications, null, null, null, null, null, null, null, null, test);
				sourceInfo.add(response);
				arrayOfSourceNodes[i] = response.then().extract().path("data.source_id");			
			}
	
		  //add source to group
		  spogServer.addSourcetoSourceGroupwithCheck(groupId, arrayOfSourceNodes, token, SpogConstants.SUCCESS_POST, null, test);
		
		  //login with msp user
		  spogServer.userLogin(mspUserName, sharePassword);
		  //get the source list
		  Response response = spogServer.getSourceListFromOneGroup(groupId, 1, -1, test);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);

		  //remove the source from group
		  for (i=0; i<num; i++) {
		  spogServer.deleteSourcefromSourceGroupwithCheck(groupId, arrayOfSourceNodes[i], token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		  }
	  }
	  
	  @Test(dataProvider = "sourceInfo3")
	  public void directGetSourceListSuccess(String source_name, SourceType source_type, SourceProduct source_product, String organization_id,
			  String site_id, ProtectionStatus protection_status, ConnectionStatus connection_status, String os_major,
		      String applications, String groupId, String token ){	 
		  System.out.println("directGetSourceListSuccess");
		  test = rep.startTest("directGetSourceListSuccess");
		  test.assignAuthor("Liu Yuefen");
		  
		  int num=2;
		  int i=0;
		  String[] arrayOfSourceNodes= new String[num];
		  ArrayList<ResponseBody> sourceInfo = new ArrayList<ResponseBody>();
		  
		  //login with csr
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword);
		  
		  //create source
			for(i=0;i<num;i++) {
				Response response = spogServer.createSource(source_name+"_"+i, source_type, source_product, organization_id, site_id, protection_status, connection_status, os_major, applications, null, null, null, null, null, null, null, null, test);
				sourceInfo.add(response);
				arrayOfSourceNodes[i] = response.then().extract().path("data.source_id");			
			}
	
		  //add source to group
		  spogServer.addSourcetoSourceGroupwithCheck(groupId, arrayOfSourceNodes, token, SpogConstants.SUCCESS_POST, null, test);
		
		  //login with direct user
		  spogServer.userLogin(directUserName, sharePassword);
		  //get the source list
		  Response response = spogServer.getSourceListFromOneGroup(groupId, 1, -1, test);
		  //check the source list
		  spogServer.getSourceListFromOneGroupWithCheck(response, SpogConstants.SUCCESS_GET_PUT_DELETE, sourceInfo, 1, -1, 2, "", test);
		  //remove the source from group
		  for (i=0; i<num; i++) {
		  spogServer.deleteSourcefromSourceGroupwithCheck(groupId, arrayOfSourceNodes[i], token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		  }
	  }
	  
	  @Test(dataProvider = "sourceInfo4")
	  public void directGetSourceListFail(String source_name, SourceType source_type, SourceProduct source_product, String organization_id,
			  String site_id, ProtectionStatus protection_status, ConnectionStatus connection_status, String os_major,
		      String applications, String groupId, String token){	 
		  System.out.println("directGetSourceListFail");
		  test = rep.startTest("directGetSourceListFail");
		  test.assignAuthor("Liu Yuefen");
		  
		  int num=2;
		  int i=0;
		  String[] arrayOfSourceNodes= new String[num];
		  
		  ArrayList<ResponseBody> sourceInfo = new ArrayList<ResponseBody>();
		  //login with csr
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword);
		  
		  //create source
			for(i=0;i<num;i++) {
				Response response = spogServer.createSource(source_name+"_"+i, source_type, source_product, organization_id, site_id, protection_status, connection_status, os_major, applications, null, null, null, null, null, null, null, null, test);
				sourceInfo.add(response);
				arrayOfSourceNodes[i] = response.then().extract().path("data.source_id");			
			}
	
		  //add source to group
		  spogServer.addSourcetoSourceGroupwithCheck(groupId, arrayOfSourceNodes, token, SpogConstants.SUCCESS_POST, null, test);
		
		  //login with direct user
		  spogServer.userLogin(directUserName, sharePassword);
		  //get the source list
		  Response response = spogServer.getSourceListFromOneGroup(groupId, 1, -1, test);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);

		  //remove the source from group
		  for (i=0; i<num; i++) {
		  spogServer.deleteSourcefromSourceGroupwithCheck(groupId, arrayOfSourceNodes[i], token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		  }
	  }
	  
	  @Test(dataProvider = "sourceInfo5")
	  public void accountGetSourceListSuccess(String source_name, SourceType source_type, SourceProduct source_product, String organization_id,
			  String site_id, ProtectionStatus protection_status, ConnectionStatus connection_status, String os_major,
		      String applications, String groupId, String token ){	 
		  System.out.println("accountGetSourceListSuccess");
		  test = rep.startTest("accountGetSourceListSuccess");
		  test.assignAuthor("Liu Yuefen");
		  
		  int num=2;
		  int i=0;
		  String[] arrayOfSourceNodes= new String[num];
		  ArrayList<ResponseBody> sourceInfo = new ArrayList<ResponseBody>();
		  
		  //login with csr
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword);
		  
		  //create source
			for(i=0;i<num;i++) {
				Response response = spogServer.createSource(source_name+"_"+i, source_type, source_product, organization_id, site_id, protection_status, connection_status, os_major, applications, null, null, null, null, null, null, null, null, test);
				sourceInfo.add(response);
				arrayOfSourceNodes[i] = response.then().extract().path("data.source_id");			
			}
	
		  //add source to group
		  spogServer.addSourcetoSourceGroupwithCheck(groupId, arrayOfSourceNodes, token, SpogConstants.SUCCESS_POST, null, test);
		
		  //login with account user
		  spogServer.userLogin(accountUserName, sharePassword);
		  //get the source list
		  Response response = spogServer.getSourceListFromOneGroup(groupId, 1, -1, test);
		  //check the source list
		  spogServer.getSourceListFromOneGroupWithCheck(response, SpogConstants.SUCCESS_GET_PUT_DELETE, sourceInfo, 1, -1, 2, "", test);
		  //remove the source from group
		  for (i=0; i<num; i++) {
		  spogServer.deleteSourcefromSourceGroupwithCheck(groupId, arrayOfSourceNodes[i], token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		  }
	  }
	  
	  @Test(dataProvider = "sourceInfo6")
	  public void accountGetSourceListFail(String source_name, SourceType source_type, SourceProduct source_product, String organization_id,
			  String site_id, ProtectionStatus protection_status, ConnectionStatus connection_status, String os_major,
		      String applications, String groupId, String token ){	 
		  System.out.println("accountGetSourceListFail");
		  test = rep.startTest("accountGetSourceListFail");
		  test.assignAuthor("Liu Yuefen");
		  
		  int num=2;
		  int i=0;
		  String[] arrayOfSourceNodes= new String[num];
		  
		  ArrayList<ResponseBody> sourceInfo = new ArrayList<ResponseBody>();
		  //login with csr
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword);
		  
		  //create source
			for(i=0;i<num;i++) {
				Response response = spogServer.createSource(source_name+"_"+i, source_type, source_product, organization_id, site_id, protection_status, connection_status, os_major, applications, null, null, null, null, null, null, null, null, test);
				sourceInfo.add(response);
				arrayOfSourceNodes[i] = response.then().extract().path("data.source_id");			
			}
	
		  //add source to group
		  spogServer.addSourcetoSourceGroupwithCheck(groupId, arrayOfSourceNodes, token, SpogConstants.SUCCESS_POST, null, test);
		
		  //login with account user
		  spogServer.userLogin(accountUserName, sharePassword);
		  //get the source list
		  Response response = spogServer.getSourceListFromOneGroup(groupId, 1, -1, test);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);

		  //remove the source from group
		  for (i=0; i<num; i++) {
		  spogServer.deleteSourcefromSourceGroupwithCheck(groupId, arrayOfSourceNodes[i], token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		  }
	  }
	  
	  @DataProvider(name = "sourceInfo7")
	  public final Object[][] getSourceInfo7() {
	 	 return new Object[][]   {{spogServer.ReturnRandom("yuefen_sourcex7_directSite"), SourceType.machine,  SourceProduct.udp,  directOrg_id, directSite_id, ProtectionStatus.protect, ConnectionStatus.online,
	 		"windows",	"sql",  directGroup_id, directAdminToken}};
	  }
	  @Test(dataProvider = "sourceInfo7")
	  //page = last page, page size=5
	  public void getSourceListPagination(String source_name, SourceType source_type, SourceProduct source_product, String organization_id,
			  String site_id, ProtectionStatus protection_status, ConnectionStatus connection_status, String os_major,
		      String applications, String groupId, String token  ){	 
		  System.out.println("getSourceListPagination");
		  test = rep.startTest("getSourceListPagination");
		  test.assignAuthor("Liu Yuefen");
		  
		  int num=11;
		  int i=0;
		  String[] arrayOfSourceNodes= new String[num];
		  ArrayList<ResponseBody> sourceInfo = new ArrayList<ResponseBody>();
		  
		  //login with csr
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword);
		  
		  //create the fist source
		  Response response = spogServer.createSource(source_name+"_0", source_type, source_product, organization_id, site_id, protection_status, connection_status, os_major, applications, null, null, null, null, null, null, null, null, test);
		  sourceInfo.add(response);
		  arrayOfSourceNodes[0] = response.then().extract().path("data.source_id");	
		  //create source
		  for(i=1;i<num;i++) {
				response = spogServer.createSource(source_name+"_"+i, source_type, source_product, organization_id, site_id, protection_status, connection_status, os_major, applications, null, null, null, null, null, null, null, null, test);
				arrayOfSourceNodes[i] = response.then().extract().path("data.source_id");			
		 }
		
		  //add source to group
		  spogServer.addSourcetoSourceGroupwithCheck(groupId, arrayOfSourceNodes, token, SpogConstants.SUCCESS_POST, null, test);
		
		  //login with direct user
		  spogServer.userLogin(directUserName, sharePassword);
		  //get the source list
		  response = spogServer.getSourceListFromOneGroup(groupId, 3, 5, test);
		  //check the source list
		  spogServer.getSourceListFromOneGroupWithCheck(response, SpogConstants.SUCCESS_GET_PUT_DELETE, sourceInfo, 3, 5, 11, "", test);
		  //remove the source from group
		  for (i=0; i<num; i++) {
		  spogServer.deleteSourcefromSourceGroupwithCheck(groupId, arrayOfSourceNodes[i], token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		  }
	  }
	  
	  @DataProvider(name = "sourceInfo8")
	  public final Object[][] getSourceInfo8() {
	 	 return new Object[][]   {{spogServer.ReturnRandom("yuefen_sourcex8_directSite"), SourceType.machine,  SourceProduct.udp,  directOrg_id, directSite_id, ProtectionStatus.protect, ConnectionStatus.online,
	 		"windows",	"sql",  directGroup_id, directAdminToken}};
	  }
	  @Test(dataProvider = "sourceInfo8")
	  //page = specified one, page size=1 // page=invalid(not exist page)
	  public void getSourceListPagination2(String source_name, SourceType source_type, SourceProduct source_product, String organization_id,
			  String site_id, ProtectionStatus protection_status, ConnectionStatus connection_status, String os_major,
		      String applications, String groupId, String token ){	 
		  System.out.println("getSourceListPagination2");
		  test = rep.startTest("getSourceListPagination2");
		  test.assignAuthor("Liu Yuefen");
		  
		  int num=6;
		  int i=0;
		  String[] arrayOfSourceNodes= new String[num];
		  ArrayList<ResponseBody> sourceInfo = new ArrayList<ResponseBody>();
		  ArrayList<ResponseBody> tempInfo = new ArrayList<ResponseBody>();
		  
		  //login with csr
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword);
		  
		  //create source
		  for(i=0;i<(num/2);i++) {
				Response response = spogServer.createSource(source_name+"_"+i, source_type, source_product, organization_id, site_id, protection_status, connection_status, os_major, applications, null, null, null, null, null, null, null, null, test);
				arrayOfSourceNodes[i] = response.then().extract().path("data.source_id");			
		 }
			
		  response = spogServer.createSource(source_name+"_aa", source_type, source_product, organization_id, site_id, protection_status, connection_status, os_major, applications, null, null, null, null, null, null, null, null, test);
		  sourceInfo.add(response);
		  arrayOfSourceNodes[num/2] = response.then().extract().path("data.source_id");	
		  
		  for(i=(num/2+1);i<num;i++) {
				Response response = spogServer.createSource(source_name+"_"+i, source_type, source_product, organization_id, site_id, protection_status, connection_status, os_major, applications, null, null, null, null, null, null, null, null, test);
				arrayOfSourceNodes[i] = response.then().extract().path("data.source_id");			
		 }
		  //add source to group
		  spogServer.addSourcetoSourceGroupwithCheck(groupId, arrayOfSourceNodes, token, SpogConstants.SUCCESS_POST, null, test);
		
		  //login with direct user
		  spogServer.userLogin(directUserName, sharePassword);
		  //get the source list
		  Response response = spogServer.getSourceListFromOneGroup(groupId, 3, 1, test);
		  //check the source list
		  spogServer.getSourceListFromOneGroupWithCheck(response, SpogConstants.SUCCESS_GET_PUT_DELETE, sourceInfo, 3, 1, 6, "", test);
		  
		  //page = not exist page
		  response = spogServer.getSourceListFromOneGroup(groupId, 10, 1, test);
		  
		 
		  //remove the source from group
		  for (i=0; i<num; i++) {
		  spogServer.deleteSourcefromSourceGroupwithCheck(groupId, arrayOfSourceNodes[i], token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		  }
	  }
	  
	  @DataProvider(name = "sourceInfo9")
	  public final Object[][] getSourceInfo9() {
	 	 return new Object[][]   {{spogServer.ReturnRandom("yuefen_sourcex9_directSite"), SourceType.machine,  SourceProduct.udp,  directOrg_id, directSite_id, ProtectionStatus.protect, ConnectionStatus.online,
	 		"windows",	"sql",  directGroup_id, directAdminToken}};
	  }
	  @Test(dataProvider = "sourceInfo9")
	  //page size>100
	  public void getSourceListPagination3(String source_name, SourceType source_type, SourceProduct source_product, String organization_id,
			  String site_id, ProtectionStatus protection_status, ConnectionStatus connection_status, String os_major,
		      String applications, String groupId, String token ){	 
		  System.out.println("getSourceListPagination3");
		  test = rep.startTest("getSourceListPagination3");
		  test.assignAuthor("Liu Yuefen");
		  
		  int num=3;
		  int i=0;
		  String[] arrayOfSourceNodes= new String[num];
		  ArrayList<ResponseBody> sourceInfo = new ArrayList<ResponseBody>();
		  ArrayList<ResponseBody> tempInfo = new ArrayList<ResponseBody>();
		  
		  //login with csr
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword);

		  //create source
		  for(i=0;i<num;i++) {
				Response response = spogServer.createSource(source_name+"_"+i, source_type, source_product, organization_id, site_id, protection_status, connection_status, os_major, applications, null, null, null, null, null, null, null, null, test);
				arrayOfSourceNodes[i] = response.then().extract().path("data.source_id");	
				
		 }

		  //add source to group
		  spogServer.addSourcetoSourceGroupwithCheck(groupId, arrayOfSourceNodes, token, SpogConstants.SUCCESS_POST, null, test);
		
		  //login with direct user
		  spogServer.userLogin(directUserName, sharePassword);
		  //get the source list
		  response = spogServer.getSourceListFromOneGroup(groupId, 1, 200, test);
		  
		  //check the source list
		  spogServer.getSourceListFromOneGroupWithCheck(response, SpogConstants.SUCCESS_GET_PUT_DELETE, sourceInfo, 1, 200, 3, "", test);
		  
		  //remove the source from group
		  for (i=0; i<num; i++) {
		  spogServer.deleteSourcefromSourceGroupwithCheck(groupId, arrayOfSourceNodes[i], token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		  }
	  }
	  
	  @DataProvider(name = "sourceInfo10")
	  public final Object[][] getSourceInfo10() {
	 	 return new Object[][]   {{spogServer.ReturnRandom("yuefen_sourcex10_directSite"), SourceType.machine,  SourceProduct.udp,  directOrg_id, directSite_id, ProtectionStatus.protect, ConnectionStatus.online,
	 		"windows",	"sql",  directGroup_id, directAdminToken}};
	  }
	  @Test(dataProvider = "sourceInfo10")
	  //page=-1, page size =-1; not provide page and page size.
	  public void getSourceListPagination4(String source_name, SourceType source_type, SourceProduct source_product, String organization_id,
			  String site_id, ProtectionStatus protection_status, ConnectionStatus connection_status, String os_major,
		      String applications, String groupId, String token ){	 
		  System.out.println("getSourceListPagination4");
		  test = rep.startTest("getSourceListPagination4");
		  test.assignAuthor("Liu Yuefen");
		  
		  int num=21;
		  int i=0;
		  String[] arrayOfSourceNodes= new String[num];
		  ArrayList<ResponseBody> sourceInfo = new ArrayList<ResponseBody>();
		  
		  //login with csr
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword);
		  
		  //create the first source
			Response response = spogServer.createSource(source_name+"_0", source_type, source_product, organization_id, site_id, protection_status, connection_status, os_major, applications, null, null, null, null, null, null, null, null, test);
			arrayOfSourceNodes[0] = response.then().extract().path("data.source_id");
		  //create source
			for(i=1;i<num;i++) {
				response = spogServer.createSource(source_name+"_"+i, source_type, source_product, organization_id, site_id, protection_status, connection_status, os_major, applications, null, null, null, null, null, null, null, null, test);
				sourceInfo.add(response);
				arrayOfSourceNodes[i] = response.then().extract().path("data.source_id");			
			}
			
		  //add source to group
		  spogServer.addSourcetoSourceGroupwithCheck(groupId, arrayOfSourceNodes, token, SpogConstants.SUCCESS_POST, null, test);
		
		  //get the source list
		  response = spogServer.getSourceListFromOneGroup(groupId, -1, -1, test);
		  spogServer.getSourceListFromOneGroupWithCheck(response, SpogConstants.SUCCESS_GET_PUT_DELETE, sourceInfo, -1, -1, 21, "", test);

		  //remove the source from group
		  for (i=0; i<num; i++) {
		  spogServer.deleteSourcefromSourceGroupwithCheck(groupId, arrayOfSourceNodes[i], token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		  }
	  }
	  
	  @DataProvider(name = "sourceInfo11")
	  public final Object[][] getSourceInfo11() {
	 	 return new Object[][]   {{spogServer.ReturnRandom("yuefen_sourcex11_directSite"), SourceType.machine,  SourceProduct.udp,  directOrg_id, directSite_id, ProtectionStatus.protect, ConnectionStatus.online,
	 		"windows",	"sql",  directGroup_id, directAdminToken}};
	  }
	  @Test(dataProvider = "sourceInfo11")
	  //page=2, page size=100
	  public void getSourceListPagination5(String source_name, SourceType source_type, SourceProduct source_product, String organization_id,
			  String site_id, ProtectionStatus protection_status, ConnectionStatus connection_status, String os_major,
		      String applications, String groupId, String token){	 
		  System.out.println("getSourceListPagination5");
		  test = rep.startTest("getSourceListPagination5");
		  test.assignAuthor("Liu Yuefen");
		  
		  int num=101;
		  int i=0;
		  String[] arrayOfSourceNodes= new String[num];
		  ArrayList<ResponseBody> sourceInfo = new ArrayList<ResponseBody>();
		  
		  //login with csr
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword);
		  
		  //create the first source
			Response response = spogServer.createSource(source_name, source_type, source_product, organization_id, site_id, protection_status, connection_status, os_major, applications, null, null, null, null, null, null, null, null, test);
			arrayOfSourceNodes[0] = response.then().extract().path("data.source_id");
			
		  //create source
			for(i=1;i<num;i++) {
				response = spogServer.createSource(source_name+"_"+i, source_type, source_product, organization_id, site_id, protection_status, connection_status, os_major, applications, null, null, null, null, null, null, null, null, test);
				sourceInfo.add(response);
				arrayOfSourceNodes[i] = response.then().extract().path("data.source_id");			
			}
			
		  //add source to group
		  spogServer.addSourcetoSourceGroupwithCheck(groupId, arrayOfSourceNodes, token, SpogConstants.SUCCESS_POST, null, test);
		
		  //get the source list
		  response = spogServer.getSourceListFromOneGroup(groupId, 1, 100, test);
		  spogServer.getSourceListFromOneGroupWithCheck(response, SpogConstants.SUCCESS_GET_PUT_DELETE, sourceInfo, 1, 100, 101, "", test);

		  //remove the source from group
		  for (i=0; i<num; i++) {
		  spogServer.deleteSourcefromSourceGroupwithCheck(groupId, arrayOfSourceNodes[i], token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		  }
	  }
	  
	  @DataProvider(name = "sourceInfo12")
	  public final Object[][] getSourceInfo12() {
	 	 return new Object[][]   {{spogServer.ReturnRandom("yuefen_sourcex12_directSite"), SourceType.machine,  SourceProduct.udp,  directOrg_id, directSite_id, ProtectionStatus.protect, ConnectionStatus.online,
	 		"windows",	"sql",  directGroup_id, directAdminToken}};
	  }
	  @Test(dataProvider = "sourceInfo12")
	  public void GetSourceListFail401(String source_name, SourceType source_type, SourceProduct source_product, String organization_id,
			  String site_id, ProtectionStatus protection_status, ConnectionStatus connection_status, String os_major,
		      String applications, String groupId, String token ){	 
		  System.out.println("GetSourceListFail401");
		  test = rep.startTest("GetSourceListFail401");
		  test.assignAuthor("Liu Yuefen");
		  
		  int num=2;
		  int i=0;
		  String[] arrayOfSourceNodes= new String[num];
		  ArrayList<ResponseBody> sourceInfo = new ArrayList<ResponseBody>();
		  
		  //login with csr
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword);
		  
		  //create source
			for(i=0;i<num;i++) {
				Response response = spogServer.createSource(source_name+"_"+i, source_type, source_product, organization_id, site_id, protection_status, connection_status, os_major, applications, null, null, null, null, null, null, null, null, test);
				sourceInfo.add(response);
				arrayOfSourceNodes[i] = response.then().extract().path("data.source_id");			
			}
	
		  //add source to group
		  spogServer.addSourcetoSourceGroupwithCheck(groupId, arrayOfSourceNodes, token, SpogConstants.SUCCESS_POST, null, test);
		
		  //set token as ""
		  spogServer.setToken("");
		  //get the source list
		  Response response = spogServer.getSourceListFromOneGroup(groupId, 1, -1, test);
		  spogServer.checkResponseStatus(response, SpogConstants.NOT_LOGGED_IN, test);

		  //remove the source from group
		  for (i=0; i<num; i++) {
		  spogServer.deleteSourcefromSourceGroupwithCheck(groupId, arrayOfSourceNodes[i], token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		  }
	  }
	  
	  @DataProvider(name = "sourceInfo13")
	  public final Object[][] getSourceInfo13() {
	 	 return new Object[][]   {{spogServer.ReturnRandom("yuefen_sourcex13_directSite"), SourceType.machine,  SourceProduct.udp,  directOrg3_id, directSite3_id, ProtectionStatus.protect, ConnectionStatus.online,
	 		"windows",	"sql",  directGroup3_id, directAdminToken3}};
	  }
	  @Test(dataProvider = "sourceInfo13")
	  public void GetSourceListNormalCheck(String source_name, SourceType source_type, SourceProduct source_product, String organization_id,
			  String site_id, ProtectionStatus protection_status, ConnectionStatus connection_status, String os_major,
		      String applications, String groupId, String token ){	 
		  System.out.println("GetSourceListNormalCheck");
		  test = rep.startTest("GetSourceListNormalCheck");
		  test.assignAuthor("Liu Yuefen");
		  
		  int num=2;
		  int i=0;
		  String[] arrayOfSourceNodes= new String[num];
		  ArrayList<ResponseBody> sourceInfo = new ArrayList<ResponseBody>();
		  
		  //login with csr
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword);
		  
		  //create source
			for(i=0;i<num;i++) {
				Response response = spogServer.createSource(source_name+"_"+i, source_type, source_product, organization_id, site_id, protection_status, connection_status, os_major, applications, null, null, null, null, null, null, null, null, test);
				sourceInfo.add(response);
				arrayOfSourceNodes[i] = response.then().extract().path("data.source_id");			
			}
	
		  //add source to group
		  spogServer.addSourcetoSourceGroupwithCheck(groupId, arrayOfSourceNodes, token, SpogConstants.SUCCESS_POST, null, test);
		

		  //get the source list
		  Response response = spogServer.getSourceListFromOneGroup("", 1, -1, test);
		  spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
		  spogServer.checkErrorCode(response, "40000005");

		  //remove the source from group
		  for (i=0; i<num; i++) {
		  spogServer.deleteSourcefromSourceGroupwithCheck(groupId, arrayOfSourceNodes[i], token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		  }
		  
		  //delete the group
		  spogServer.deleteGroupWithExpectedStatusCode(groupId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  
		  //get the source list from one deleted group
		  response = spogServer.getSourceListFromOneGroup(groupId, -1, -1, test);
		  spogServer.checkResponseStatus(response, SpogConstants.RESOURCE_NOT_EXIST, test);
		  spogServer.checkErrorCode(response, "00500007");
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
