package api.recoveredresources;

import invoker.SiteTestHelper;
import io.restassured.response.Response;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;

import Constants.ConnectionStatus;
import Constants.DestinationStatus;
import Constants.DestinationType;
import Constants.ErrorCode;
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
import api.preparedata.InitialTestData;
import api.preparedata.InitialTestDataImpl;
import InvokerServer.Source4SPOGServer;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class GetAmountForVSBAndIVMSourceTest  extends base.prepare.Is4Org{
	  private SPOGServer spogServer;
	  private SPOGDestinationServer spogDestinationServer;
	  private GatewayServer gatewayServer;
	  private Source4SPOGServer source4SPOGServer;
	  private String csrAdminUserName;
	  private String csrAdminPassword;
	  //private ExtentReports rep;
	  private ExtentTest test;
	  
	  private String csr_token=null;
	  private String postfix_email = "@arcserve.com";
	  private String common_password = "Welcome*02";
		private String csr_readonly_email = "csr_readonly_zhaoguo@arcserve.com";
		  private String csr_readonly_password = "Caworld_2017";
		  
			private InitialTestDataImpl initialTestDataImpl;
			private InitialTestData itd;
			private String password = "Pa$$w0rd";
	  
	  private String prefix_direct = "SPOG_QA_Leiyu_BQ_direct";
	  private String direct_org_name = prefix_direct + "_org";
	  private String direct_org_id=null;
	  private String direct_user_name = prefix_direct + "_admin";
	  private String direct_user_name_email = direct_user_name + postfix_email;
	  private String direct_user_first_name = direct_user_name + "_first_name";
	  private String direct_user_last_name = direct_user_name + "_last_name";
	  private String direct_user_id =null;
	  private String final_direct_user_name_email = null;
	  private String direct_site_id;
	  private String direct_user_cloud_account_id;
	  
	  private String prefix_msp = "SPOG_QA_Leiyu_BQ_msp";
	  private String msp_org_name = prefix_msp + "_org";
	  private String msp_org_email = msp_org_name + postfix_email;
	  private String msp_org_first_name = msp_org_name + "_first_name";
	  private String msp_org_last_name = msp_org_name + "_last_name";
	  private String msp_user_name = prefix_msp + "_admin";
	  private String msp_user_name_email = prefix_msp + "_admin" + postfix_email;
	  private String msp_user_first_name = msp_user_name + "_first_name";
	  private String msp_user_last_name = msp_user_name + "_last_name";
	  private String msp_user_id =null;
	  private String msp_org_id=null;
	  private String final_msp_user_name_email=null;	  
	  private String msp_site_id;
	  private String msp_user_cloud_account_id;
	  
	  private String msp_sub_org_name=prefix_msp + "_sub_org";
	  private String msp_sub_org_id=null;
	  
	  private String prefix_msp_account="SPOG_QA_Leiyu_BQ_msp_account_admin";
	  private String msp_account_admin_name = prefix_msp_account + "_admin";
	  private String msp_account_admin_email =msp_account_admin_name + postfix_email;
	  private String msp_account_admin_first_name = msp_account_admin_name + "_first_name";
	  private String msp_account_admin_last_name = msp_account_admin_name + "_last_name";
	  private String msp_account_admin_id;
	  private String final_msp_account_admin_email;
	  
	  private String account_id;
	  private String account_user_email;
	  private String account_user_id;
	  private String account_site_id;
	  private String another_direct_site_id;
	  private String another_msp_site_id;
	  private String another_account_site_id;
		 
	  private String prefix_source="dsource_leiyu";

	  private String  org_model_prefix=this.getClass().getSimpleName();
  /**
   * create source 
   */
  @Test(dataProvider = "SourceParameter", enabled=true, priority=1)
  public void createSource(String userName, String password, SourceType sourceType, SourceProduct sourceProduct, String org_id, String siteId, ProtectionStatus protectionStatus, 
		  ConnectionStatus connectionStatus, String os_major, String applications,String vm_name ,String hypervisor_id ,String agent_name ,String os_name ,
			String os_architecture ,String agent_current_version ,String agent_upgrade_version ,String agent_upgrade_link, String source_id){
	  	
	  	test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
	  	test.assignAuthor("leiyu.wang");
	  	String source_name= prefix_source+RandomStringUtils.randomAlphanumeric(3);
	  	
	  	spogServer.userLogin(userName, password);			
		
		String new_source_id = spogServer.createSourceWithCheck(source_name, sourceType, sourceProduct, org_id, siteId,  protectionStatus, 
	  			connectionStatus, os_major, applications, vm_name, hypervisor_id, agent_name, os_name, os_architecture, agent_current_version,
	  			agent_upgrade_version, agent_upgrade_link, source_id, SpogConstants.SUCCESS_POST, "", test);
		
		//Response response = spogServer.deleteSourcesById(spogServer.getJWTToken(), new_source_id, test);
		//spogServer.deleteSourcesWithCheck(response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
  }
  
 @DataProvider(name="SourceParameter")
 public final Object[][] SourceParameter(){
	 return new Object[][]{
			 {final_direct_user_name_email,common_password,SourceType.instant_vm,  SourceProduct.udp,  direct_org_id, direct_site_id, ProtectionStatus.unprotect, ConnectionStatus.offline,
	 				OSMajor.windows.name(),	"exchange", null, null, null, null, null,null,null, null , null}, 
	 		 {final_direct_user_name_email,common_password, SourceType.virtual_standby,  SourceProduct.udp,  direct_org_id, direct_site_id, ProtectionStatus.unprotect, ConnectionStatus.offline,
 					OSMajor.windows.name(),	"sql",  null, null, null, "windows 2008 r2", null,null,null, null, null }, 						
	             {final_msp_user_name_email, common_password, SourceType.virtual_standby,  SourceProduct.udp,  account_id, account_site_id, ProtectionStatus.unprotect, ConnectionStatus.offline,
		 				OSMajor.windows.name(),	"sql",  null, null, null, "windows 2008 r2", null,null,null, null, null },
		 		{account_user_email, common_password, SourceType.virtual_standby,  SourceProduct.udp,  account_id, account_site_id, ProtectionStatus.unprotect, ConnectionStatus.offline,
			 				OSMajor.windows.name(),	"sql",  null, null, null, "windows 2008 r2", null,null,null, null, null },			 				
				{account_user_email, common_password, SourceType.instant_vm,  SourceProduct.udp,  account_id, account_site_id, ProtectionStatus.unprotect, ConnectionStatus.offline,
				 				OSMajor.windows.name(),	"exchange", null, null, null, null, null,null,null, null , null}
				
	 };
 }
 
 /**
  * test cases
  * direct admin could get amount for source type virtual_standby and instant_vm for its organization
	MSP admin could get amount for source type virtual_standby and instant_vm for its organization
	MSP admin could get amount for source type virtual_standby and instant_vm for its sub-organization
	csr admin could get amount for source type virtual_standby and instant_vm for direct organization
	csr admin could get amount for source type virtual_standby and instant_vm for msp organization
	csr admin could get amount for source type virtual_standby and instant_vm for msp sub-organization
	account admin could get amount for source type virtual_standby and instant_vm for its organization
  * @author leiyu.wang
  * @param UserName
  * @param password
  * @param orgID
  * @param IVMCount
  * @param VSBCount
  */
 @Test(dataProvider="GetVSBAndIVMAmountParameter",priority=2)
 public void GetIVMansVSBSourceAmount(String UserName,String password, String orgID,int IVMCount, int VSBCount){
	 
	 test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
	 test.assignAuthor("leiyu.wang");
	  
	 test.log(LogStatus.INFO,"admin login");
	 spogServer.userLogin(UserName, password);		
	 source4SPOGServer.setToken(spogServer.getJWTToken());
	 source4SPOGServer.getAmountForVSBAndIVMSourceWithCheck(orgID, IVMCount, VSBCount, test);
 }
 
 @DataProvider(name="GetVSBAndIVMAmountParameter")
 public Object[][] GetPolicyAmountParameter(){
	 return new Object[][]{
			 {csrAdminUserName, csrAdminPassword,direct_org_id,1,1},
			 {csrAdminUserName, csrAdminPassword,msp_org_id,0,2},
			 {csrAdminUserName, csrAdminPassword,account_id,1,0},	
			 
			 {csr_readonly_email, csr_readonly_password,direct_org_id,1,1},
			 {csr_readonly_email, csr_readonly_password,msp_org_id,0,2},
			 {csr_readonly_email, csr_readonly_password,account_id,1,0},	
			 
			 {final_direct_user_name_email, common_password,direct_org_id,1,1},			 
			 {final_msp_user_name_email, common_password,msp_org_id,0,2},
			 {final_msp_user_name_email, common_password,account_id,1,0},
			 {account_user_email, common_password,account_id,1,0},
			 
			 //root msp related
			 {csrAdminUserName, csrAdminPassword,itd.getRoot_msp_org_1(), 0, 2},
			 {csrAdminUserName, csrAdminPassword,itd.getRoot_msp_org_1_sub_msp_1(),0,2},
			 {csrAdminUserName, csrAdminPassword,itd.getRoot_msp_org_1_account_1(),1,0},
			 {csrAdminUserName, csrAdminPassword,itd.getRoot_msp_org_1_sub_msp_1_account_1(),1,0},	
			 {itd.getRoot_msp_org_1_user_1_email(), password, itd.getRoot_msp_org_1_account_1(), 1, 0},
			 {itd.getRoot_msp_org_1_account_admin_1_email(), password, itd.getRoot_msp_org_1_account_1(), 1, 0},
			 {itd.getRoot_msp_org_1_sub_msp_1_account_admin_1_email(), password, itd.getRoot_msp_org_1_sub_msp_1_account_1(), 1, 0},
			 {itd.getRoot_msp_org_1_sub_msp_1_user_1_email(), password, itd.getRoot_msp_org_1_sub_msp_1_account_1(), 1, 0},
	 };
 }
 
 /**
  * test cases:
  * direct admin could not get amount for source type virtual_standby and instant_vm of msp organization
	direct admin could not get amount for source type virtual_standby and instant_vm of msp sub organization, report 403
	msp admin could not get amount for source type virtual_standby and instant_vm of direct organization, report 403
	sub org admin could not get amount for source type virtual_standby and instant_vm of its parent organization, report 403
	sub org admin could not get amount for source type virtual_standby and instant_vm of direct organization, report 403
	MSP account admin could not get amount for source type virtual_standby and instant_vm of direct organization, report 403
	MSP account admin could not get amount for source type virtual_standby and instant_vm of msp organization, report 403
	MSP account admin could not get amount for source type virtual_standby and instant_vm of other sub organization, report 403
  * @author leiyu.wang
  * @param username
  * @param password
  * @param org_id
  * @param statusCode
  */
 @Test(dataProvider = "GetPolicyAmountFail", enabled=true,priority=3)
 public void GetPolicyAmountFail(String username, String password,String org_id,int statusCode){
	 	test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
	  	test.assignAuthor("leiyu.wang");
	  
		test.log(LogStatus.INFO,"admin login");
		spogServer.userLogin(username, password);		
		source4SPOGServer.setToken(spogServer.getJWTToken());		
		source4SPOGServer.getAmountForVSBAndIVMSourceFail(org_id,statusCode, test);
		
 }

 @DataProvider(name="GetPolicyAmountFail")
 public Object[][] GetPolicyAmountFail(){
	 return new Object[][]{
			 		 
			 {final_direct_user_name_email, common_password,msp_org_id,403},	
			 {final_direct_user_name_email, common_password,account_id,403},			 
			 {final_msp_user_name_email, common_password,direct_org_id,403},			 
			 {account_user_email, common_password,msp_org_id,403},
			 {account_user_email, common_password,direct_org_id,403},
			 
			 {final_msp_account_admin_email,common_password,direct_org_id,403},
//			 {final_msp_account_admin_email,common_password,msp_org_id,403},
			 {final_msp_account_admin_email,common_password,account_id,403},
			 
			 //root msp related
			 {itd.getRoot_msp_org_1_user_1_email(),password,itd.getRoot_msp_org_1_sub_msp_1(),403},
			 {itd.getRoot_msp_org_1_user_1_email(),password,itd.getRoot_msp_org_1_sub_msp_1_account_1(),403},
			 {itd.getRoot_msp_org_1_account_admin_2_email(),password,itd.getRoot_msp_org_1_account_1(),403},
			 {itd.getRoot_msp_org_1_sub_msp_1_account_admin_2_email(),password,itd.getRoot_msp_org_1_sub_msp_1_account_1(),403},
			 {itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1_email(),password,itd.getRoot_msp_org_1_sub_msp_1_account_2(),403},
			 {itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1_email(),password,itd.getRoot_msp_org_1_sub_msp_2_account_1(),403},
	 };
 } 
 
 //could not get amount and policy type without token, report401
 @Test(enabled=true,priority=4)
 public void GetPolicyAmountwithoutToken(){
	 	test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
	  	test.assignAuthor("leiyu.wang");
	  
		test.log(LogStatus.INFO,"admin login");					
		source4SPOGServer.setToken("");
		source4SPOGServer.getAmountForVSBAndIVMSourceFail(account_id, 401, test);	
 } 
 
 //could not get amount and policy type with invalid token
 @Test(enabled=true,priority=5)
 public void GetPolicyAmountwithoutInvalidToken(){
	 	test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
	  	test.assignAuthor("leiyu.wang");
	  
		test.log(LogStatus.INFO,"admin login");
		source4SPOGServer.setToken("*****************************");
		source4SPOGServer.getAmountForVSBAndIVMSourceFail(account_id,401, test);	
 }
 
  @BeforeClass
  @Parameters({ "baseURI", "port", "logFolder", "csrAdminUserName", "csrAdminPassword", "buildVersion"})
  public void beforeClass(String baseURI, String port, String logFolder, String adminUserName, String adminPassword ,  String buildVersion) throws UnknownHostException {
	
	  	spogServer = new SPOGServer(baseURI, port);
	  	spogDestinationServer=new SPOGDestinationServer(baseURI,port);
	  	gatewayServer =new GatewayServer(baseURI,port);
	  	source4SPOGServer= new Source4SPOGServer(baseURI, port);
	  	rep = ExtentManager.getInstance("GetAmountForVSBAndIVMSourceTest",logFolder);
	  	this.csrAdminUserName = adminUserName;
	  	this.csrAdminPassword = adminPassword;
	  	
	  	test = rep.startTest("beforeClass");
		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
		csr_token=spogServer.getJWTToken();
		//*******************create direct org,user,site**********************/
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		test.log(LogStatus.INFO,"create a direct org");
		direct_org_id = spogServer.CreateOrganizationWithCheck(prefix+direct_org_name +org_model_prefix, SpogConstants.DIRECT_ORG, null, common_password, null, null, test);
		final_direct_user_name_email = prefix + direct_user_name_email;
		
		test.log(LogStatus.INFO,"create a admin under direct org");
		direct_user_id = spogServer.createUserAndCheck(final_direct_user_name_email, common_password, prefix + direct_user_first_name, prefix + direct_user_last_name, SpogConstants.DIRECT_ADMIN, direct_org_id, test);
		spogServer.userLogin(final_direct_user_name_email, common_password);
	  	
		test.log(LogStatus.INFO,"Getting the JWTToken for the direct user");
		String direct_user_validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The token is :"+ direct_user_validToken );
		
		String siteName= spogServer.getRandomSiteName("TestCreate");
		test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
		String sitetype=siteType.gateway.toString();
		test.log(LogStatus.INFO,"The siteType :"+sitetype);
		
		test.log(LogStatus.INFO,"Creating a site For direct org");
		direct_site_id = gatewayServer.createsite_register_login(direct_org_id, direct_user_validToken, direct_user_id, "leiyu_directSite1", "1.0.0", spogServer, test);
		String direct_site_token=gatewayServer.getJWTToken();
		test.log(LogStatus.INFO, "The site token is "+direct_site_token);
		
		siteName= spogServer.getRandomSiteName("TestCreate");
		test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
		sitetype=siteType.gateway.toString();
		test.log(LogStatus.INFO,"The siteType :"+sitetype);
		
		test.log(LogStatus.INFO,"Creating another site For direct org");
		another_direct_site_id = gatewayServer.createsite_register_login(direct_org_id, direct_user_validToken, direct_user_id, "leiyu_directSite2", "1.0.0", spogServer, test);
		String another_direct_site_token=gatewayServer.getJWTToken();
		test.log(LogStatus.INFO, "The site token is "+another_direct_site_token);
		
		
		//************************create msp org,user,site*************************************
		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
		prefix = RandomStringUtils.randomAlphanumeric(8);
		test.log(LogStatus.INFO,"create a msp org");
		msp_org_id = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name +org_model_prefix, SpogConstants.MSP_ORG, null, common_password, null, null, test);
		final_msp_user_name_email = prefix + msp_user_name_email;
		
		test.log(LogStatus.INFO,"create a admin under msp org");
		msp_user_id = spogServer.createUserAndCheck(final_msp_user_name_email, common_password, prefix + msp_user_first_name, prefix + msp_user_last_name, SpogConstants.MSP_ADMIN, msp_org_id, test);
		spogServer.userLogin(final_msp_user_name_email, common_password);
	  	
		test.log(LogStatus.INFO,"Getting the JWTToken for the msp user");
		String msp_user_validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The token is :"+ msp_user_validToken );
		
		siteName= spogServer.getRandomSiteName("TestCreate");
		test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
		sitetype=siteType.gateway.toString();
		test.log(LogStatus.INFO,"The siteType :"+sitetype);
		
//		test.log(LogStatus.INFO,"Creating a site For msp org");
//		msp_site_id = gatewayServer.createsite_register_login(msp_org_id, msp_user_validToken, msp_user_id, "leiyu_MSPSite1", "1.0.0", spogServer, test);
//		String msp_site_token=gatewayServer.getJWTToken();
//		test.log(LogStatus.INFO, "The site token is "+ msp_site_token);
				
		//create MSP account admin
		test.log(LogStatus.INFO,"create a msp account admin under msp org");
		final_msp_account_admin_email = prefix + this.msp_account_admin_email;
		msp_account_admin_id = spogServer.createUserAndCheck(final_msp_account_admin_email, common_password, prefix + msp_account_admin_first_name, prefix + msp_account_admin_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, msp_org_id, test);
		
		
		//create account, account user and site
		test.log(LogStatus.INFO,"Creating a account For msp org");
		account_id = spogServer.createAccountWithCheck(msp_org_id, "sub_" + prefix + msp_org_name, "", test);
		
		
		prefix = RandomStringUtils.randomAlphanumeric(8);
	
		test.log(LogStatus.INFO,"Creating a account user For account org");
		account_user_email = prefix + msp_user_name_email;
		account_user_id = spogServer.createUserAndCheck(account_user_email, common_password, prefix + msp_user_first_name, prefix + msp_user_last_name, SpogConstants.DIRECT_ADMIN, account_id, test);
		spogServer.userLogin(account_user_email, common_password);
		
		test.log(LogStatus.INFO,"Getting the JWTToken for the account user");
		String account_user_validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The token is :"+ account_user_validToken );
		
		siteName= spogServer.getRandomSiteName("TestCreate");
		test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
		sitetype=siteType.gateway.toString();
		test.log(LogStatus.INFO,"The siteType :"+sitetype);
		
		test.log(LogStatus.INFO,"Creating a site For account org");
		account_site_id = gatewayServer.createsite_register_login(account_id, account_user_validToken, account_user_id, "leiyu_accountSite1", "1.0.0", spogServer, test);
		String account_site_token=gatewayServer.getJWTToken();
		test.log(LogStatus.INFO, "The site token is "+ account_site_token);
				
		String prefix_a = RandomStringUtils.randomAlphanumeric(8) + this.getClass().getSimpleName();		
		initialTestDataImpl = new InitialTestDataImpl(baseURI, port, csrAdminUserName, csrAdminPassword, prefix_a, password);
		itd = initialTestDataImpl.initialize();
				
	  	//this is for update portal
	  	this.BQName = this.getClass().getSimpleName();
	    String author = "leiyu.wang";
	    this.runningMachine =  InetAddress.getLocalHost().getHostName();
	    SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
	    java.util.Date date=new java.util.Date();
	    this.buildVersion=buildVersion+"_"+dateFormater.format(date);
	    Nooftest=0;
	    bqdb1 = new SQLServerDb();
	    count1 = new testcasescount();
	    if(count1.isstarttimehit()==0) {      
	            creationTime=System.currentTimeMillis();
	            count1.setcreationtime(creationTime);
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
  }
	
	@AfterMethod
	public void afterMethodTest() {
		rep.endTest(test);
		rep.flush();
	}

	@AfterMethod
	public void getResult(ITestResult result) {
		if (result.getStatus() == ITestResult.FAILURE) {
			count1.setfailedcount();
			test.log(LogStatus.FAIL, "Test Case Failed is " + result.getName() + " with parameters as "
					+ Arrays.asList(result.getParameters()));
			test.log(LogStatus.FAIL, result.getThrowable().getMessage());
		} else if (result.getStatus() == ITestResult.SKIP) {
			count1.setskippedcount();
			test.log(LogStatus.SKIP, "Test Case Skipped is " + result.getName());
		} else if (result.getStatus() == ITestResult.SUCCESS) {
			count1.setpassedcount();
		}
		// ending test
		// endTest(logger) : It ends the current test and prepares to create HTML report
		rep.endTest(test);
	}
 }
