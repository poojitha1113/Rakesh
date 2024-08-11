package api;

import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.ErrorCode;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.Org4SPOGServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;
import ui.base.common.SPOGMenuTreePath;

public class CreateUserTest extends base.prepare.PrepareOrgInfo{

  @Parameters({ "pmfKey"})
  public CreateUserTest(String pmfKey) {
		super(pmfKey);
		// TODO Auto-generated constructor stub
	}

  private SPOGServer spogServer;
  private UserSpogServer userSpogServer;
  private Org4SPOGServer org4SpogServer;
  private String csrAdminUserName;
  private String csrAdminPassword;

  private ExtentTest test;

  private String postfix_email = "@arcserve.com";
  private String common_password = "Welcome*02";
  
  private String direct_org_id;
  private String final_direct_user_name_email = null;

  private String msp_org_id=null;
  private String final_msp_user_name_email=null;	  
 
  private String account_id;
  private String account_user_email;

  private String prefix_direct = "spogqa_shuo_direct";
  private String direct_org_name = prefix_direct + "_org";
  private String direct_org_email = direct_org_name + postfix_email;
  private String direct_org_first_name = direct_org_name + "_first_name";
  private String direct_org_last_name = direct_org_name + "_last_name";
  private String direct_user_name = prefix_direct + "_admin";
  private String direct_user_name_email = prefix_direct + "_admin" + postfix_email;
  private String direct_user_first_name = direct_user_name + "_first_name";
  private String direct_user_last_name = direct_user_name + "_last_name";
  
  private String prefix_msp = "spogqa_shuo_msp";
  private String msp_org_name = prefix_msp + "_org";
  private String msp_org_email = msp_org_name + postfix_email;
  private String msp_org_first_name = msp_org_name + "_first_name";
  private String msp_org_last_name = msp_org_name + "_last_name";
  private String msp_user_name = prefix_msp + "_admin";
  private String msp_user_name_email = prefix_msp + "_admin" + postfix_email;
  private String msp_user_first_name = msp_user_name + "_first_name";
  private String msp_user_last_name = msp_user_name + "_last_name";
  
  
  private String prefix_msp_account = "spogqa_shuo_msp_account";
  private String msp_account_admin_name = prefix_msp_account + "_admin";
  private String msp_account_admin_email =msp_account_admin_name + postfix_email;
  private String msp_account_admin_first_name = msp_account_admin_name + "_first_name";
  private String msp_account_admin_last_name = msp_account_admin_name + "_last_name";
  
  private String direct_user_id;
  private String msp_user_id;
  private String account_user_id;
  private String another_msp_org_id;
  private String final_msp_account_admin_email;
  private String msp_account_admin_id;
  private String another_account_id;
  private String another_msp_account_id;
  private String csr_org_id;
  

	
  //this is for update portal, each testng class is taken as BQ set
/*  private ExtentReports rep;
  private SQLServerDb bqdb1;
  public int Nooftest;
  private long creationTime;
  private String BQName=null;
  private String runningMachine;
  private testcasescount count1;
  private String buildVersion;*/


private String  org_model_prefix=this.getClass().getSimpleName();
private String csr_read_only_mail;
private String csr_read_only_admin_id;


  
  
  @BeforeClass
  @Parameters({ "baseURI", "port", "logFolder", "csrAdminUserName", "csrAdminPassword", "buildVersion"})
  public void beforeClass(String baseURI, String port, String logFolder, String adminUserName, String adminPassword ,  String buildVersion) throws UnknownHostException {
	
	
 	  spogServer = new SPOGServer(baseURI, port);
 	  userSpogServer = new UserSpogServer(baseURI, port);
	  org4SpogServer = new Org4SPOGServer(baseURI, port);
	  rep = ExtentManager.getInstance("CreateUserTest",logFolder);
	  this.csrAdminUserName = adminUserName;
	  this.csrAdminPassword = adminPassword;
	    test = rep.startTest("beforeClass");
	    this.BQName = this.getClass().getSimpleName();
	      String author = "Shuo.Zhang";
	      this.runningMachine =  InetAddress.getLocalHost().getHostName();
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
	      
	    spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
		String csrToken = spogServer.getJWTToken();
		String prefix = RandomStringUtils.randomAlphanumeric(8);
	    test.log(LogStatus.INFO,"Creating csr readonly user ");
		csr_read_only_mail= prefix+ "_shuo_csr_read_only@arcserve.com";
		
		
		csr_org_id = spogServer.GetLoggedinUserOrganizationID();
		
		csr_read_only_admin_id = spogServer.createUserAndCheck(csr_read_only_mail, common_password, prefix + "_first_name_csr", prefix + "_last_name_csr", SpogConstants.CSR_READ_ONLY_ADMIN, csr_org_id, test);
		
	    
		//*******************create direct org,user,**********************/


		test.log(LogStatus.INFO,"create a direct org");
		direct_org_id = spogServer.CreateOrganizationWithCheck(prefix+direct_org_name+ org_model_prefix , SpogConstants.DIRECT_ORG, null, null, null, null, test);
		final_direct_user_name_email = prefix + direct_user_name_email;
		
		test.log(LogStatus.INFO,"create a admin under direct org");
		direct_user_id = spogServer.createUserAndCheck(final_direct_user_name_email, common_password, prefix + direct_user_first_name, prefix + direct_user_last_name, SpogConstants.DIRECT_ADMIN, direct_org_id, test);
		spogServer.userLogin(final_direct_user_name_email, common_password);
	  	

		//************************create msp org,user,*************************************
		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
		prefix = RandomStringUtils.randomAlphanumeric(8);
		test.log(LogStatus.INFO,"create a msp org");
		msp_org_id = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name + org_model_prefix, SpogConstants.MSP_ORG, null, null, null, null, test);
		final_msp_user_name_email = prefix + msp_user_name_email;
		
		test.log(LogStatus.INFO,"create a msp admin under msp org");
		msp_user_id = spogServer.createUserAndCheck(final_msp_user_name_email, common_password, prefix + msp_user_first_name, prefix + msp_user_last_name, SpogConstants.MSP_ADMIN, msp_org_id, test);
		
		test.log(LogStatus.INFO,"create a msp account admin under msp org");
		final_msp_account_admin_email = prefix + this.msp_account_admin_email;
		msp_account_admin_id = spogServer.createUserAndCheck(final_msp_account_admin_email, common_password, prefix + msp_account_admin_first_name, prefix + msp_account_admin_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, msp_org_id, test);
		
				
		prefix = RandomStringUtils.randomAlphanumeric(8);
		test.log(LogStatus.INFO,"create a another msp org");
		another_msp_org_id = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name+ org_model_prefix , SpogConstants.MSP_ORG, null, null, null, null, test);
		

		spogServer.userLogin(final_msp_user_name_email, common_password);
	  	

		//create account, account user and site
		test.log(LogStatus.INFO,"Creating a account For msp org");
		account_id = spogServer.createAccountWithCheck(msp_org_id, "sub_" + prefix + msp_org_name, "", test);
		prefix = RandomStringUtils.randomAlphanumeric(8);
	
		test.log(LogStatus.INFO,"Creating a account user For account org");
		account_user_email = prefix + msp_user_name_email;
		account_user_id = spogServer.createUserAndCheck(account_user_email, common_password, prefix + msp_user_first_name, prefix + msp_user_last_name, SpogConstants.DIRECT_ADMIN, account_id, test);
		
		//create account, account user and site
		test.log(LogStatus.INFO,"Creating another account For msp org");
		prefix = RandomStringUtils.randomAlphanumeric(8);
		another_account_id = spogServer.createAccountWithCheck(msp_org_id, "sub_" + prefix + msp_org_name, "", test);
		
		//create acount fro another msp org
		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
		test.log(LogStatus.INFO,"Creating a account For another msp org");
		prefix = RandomStringUtils.randomAlphanumeric(8);
		another_msp_account_id = spogServer.createAccountWithCheck(another_msp_org_id, "sub_" + prefix + msp_org_name, "", test);

	  	
		
		test.log(LogStatus.INFO,"assign account to msp account admin");
		String[] mspAccountAdmins = new String []{msp_account_admin_id};
		userSpogServer.assignMspAccountAdmins(msp_org_id, account_id, mspAccountAdmins , spogServer.getJWTToken()); 
		
		/*test.log(LogStatus.INFO,"Creating a account For msp org");
		account_id = spogServer.createAccountWithCheck(msp_org_id, "sub_" + prefix + msp_org_name, "", test);
		prefix = RandomStringUtils.randomAlphanumeric(8);
	
		test.log(LogStatus.INFO,"Creating a account user For account org");
		account_user_email = prefix + msp_user_name_email;
		account_user_id = spogServer.createUserAndCheck(account_user_email, common_password, prefix + msp_user_first_name, prefix + msp_user_last_name, SpogConstants.DIRECT_ADMIN, account_id, test);
		spogServer.userLogin(account_user_email, common_password);
*/
		prepare(baseURI, port, logFolder, this.csrAdminUserName,  this.csrAdminPassword, this.getClass().getSimpleName() );
	

  }
  

  @DataProvider(name = "organizationAndUserInfo")
	public final Object[][] getOrganizationAndUserInfo() {
		return new Object[][] { {direct_org_name, SpogConstants.DIRECT_ORG, null, null,null, null, 
								direct_user_name_email, common_password,direct_user_first_name, direct_user_last_name, SpogConstants.DIRECT_ADMIN }, 
								{msp_org_name, SpogConstants.MSP_ORG, null, null,null, null, 
								msp_user_name_email.toUpperCase(), common_password,msp_user_first_name, msp_user_last_name, SpogConstants.MSP_ADMIN },
												
		};
							
	}
  
  
  /**
   * @testcase 
   *   11-a. Create user will use the provided valid organization ID.
   *   14. Csr admin can create direct admin.
   *   15. Csr admin can create MSP admin.
   *   18. Email is case insensitive - it will use lower case for email no matter user providing lower/upper case.
   *   
   * @author shuo.zhang
   * @param organizationName
   * @param organizationType
   * @param organizationEmail
   * @param organizationPwd
   * @param organizationFirstName
   * @param organizationLastName
   * @param userEmail
   * @param userPassword
   * @param userFirstName
   * @param userLastName
   * @param role_id
   */
  @Test(dataProvider = "organizationAndUserInfo", priority=0, enabled=true)

  public void csrAdminCreateAdmin(String organizationName, 
	  String organizationType,
	  String organizationEmail, 
	  String organizationPwd,
	  String organizationFirstName, 
	  String organizationLastName,
	  String userEmail, String userPassword, String userFirstName, String userLastName, String role_id)
  {
	  test = rep.startTest("csrAdminCreateAdmin");
	  test.assignAuthor("shuo.zhang");
	  spogServer.errorHandle.printInfoMessageInDebugFile("/****************csrAdminCreateAdmin**************/");
	  spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
  
	  String finalOrgName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName+ org_model_prefix;
	  //create organization
	  test.log(LogStatus.INFO,"create organization");
	  String organization_id = spogServer.CreateOrganizationWithCheck(finalOrgName, organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName, test);
	 
	  //create create user
	  test.log(LogStatus.INFO,"create user");
	  String finalUserEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;
	  Response response = spogServer.createUser(finalUserEmail ,userPassword, userFirstName, userLastName, role_id, organization_id, test);	
	  String user_id = spogServer.checkCreateUser(response, SpogConstants.SUCCESS_POST, finalUserEmail, userFirstName, userLastName, role_id, organization_id, "", test);
	  
	  //delete user
	  test.log(LogStatus.INFO,"delete user");
	  spogServer.DeleteUserById(user_id, test);
	  
	  //delete organization
	  test.log(LogStatus.INFO,"organization");
	  spogServer.DeleteOrganizationWithCheck(organization_id, test);
	
  }
  
  
  

  
  @DataProvider(name = "organizationAndUserInfo1")
 	public final Object[][] getOrganizationAndUserInfo1() {
 		return new Object[][] { {direct_org_name, SpogConstants.DIRECT_ORG, null, null,null, null, 
 			prefix_direct, common_password, direct_user_first_name, direct_user_last_name, SpogConstants.DIRECT_ADMIN ,"+14082234567", SpogConstants.SUCCESS_POST}, 
 								{msp_org_name, SpogConstants.MSP_ORG, null, null,null, null, 
 				prefix_msp, common_password,msp_user_first_name, msp_user_last_name, SpogConstants.MSP_ADMIN,"+86-01080255321", SpogConstants.SUCCESS_POST },
 								{msp_org_name, SpogConstants.MSP_SUB_ORG, null, null,null, null, 
 	 				prefix_msp, common_password,msp_user_first_name, msp_user_last_name, SpogConstants.DIRECT_ADMIN, "+81-720-812-3456", SpogConstants.SUCCESS_POST },
 								{direct_org_name, SpogConstants.DIRECT_ORG, null, null,null, null, 
 	 			prefix_direct, null, direct_user_first_name, direct_user_last_name, SpogConstants.DIRECT_ADMIN ,"+14082234567", SpogConstants.SUCCESS_POST}, 
 								{msp_org_name, SpogConstants.MSP_ORG, null, null,null, null, 
 	 				prefix_msp, null,msp_user_first_name, msp_user_last_name, SpogConstants.MSP_ADMIN,"+86-01080255321", SpogConstants.SUCCESS_POST },
 								{msp_org_name, SpogConstants.MSP_SUB_ORG, null, null,null, null, 
 	 	 				prefix_msp, null,msp_user_first_name, msp_user_last_name, SpogConstants.DIRECT_ADMIN, "+81-720-812-3456", SpogConstants.SUCCESS_POST },
 				
 		
 		};
 	}		  
  
  /**
   * @testcase 
   *   //1. Direct admin can create users in its organization -201.
   *   //3. MSP admin can create users in its organization -201.
   *   33. account's admin can create account admin
   *   7. msp admin can login
   *   8. direct admin can login
   *   9. account' admin can login
   *   1. create user with phone number = valid format, success
   *   4. create users with phone number in every org type success.
   *   
   *   1. create a direct admin set password as blank
   *   2. create a msp admin set password as blank
   *   3. create a account admin set password as blank
   *   
   *   root msp admin could create user in its org
   *   root msp account admin could create user
   *   msp sub admin could create user in its org
   *   msp sub 
   *   
   *   1. MSP admin could create its org's MSP account admin 

   * @author shuo.zhang
   * @param organizationName
   * @param organizationType
   * @param organizationEmail
   * @param organizationPwd
   * @param organizationFirstName
   * @param organizationLastName
   * @param userPrefix
   * @param userPassword
   * @param userFirstName
   * @param userLastName
   * @param role_id
   * @param expectStatusCode
   */
  @Test(enabled=true, dataProvider = "organizationAndUserInfo1")

  public void directAdminAndMspAdminCreateUser(String organizationName, 
		  String organizationType,
		  String organizationEmail, 
		  String organizationPwd,
		  String organizationFirstName, 
		  String organizationLastName,
		  String userPrefix,  String userPassword, String userFirstName, String userLastName, String role_id, String phone_number, int expectStatusCode) {
	  
	  test = rep.startTest("directAdminAndMspAdminCreateUser");
	  test.assignAuthor("shuo.zhang");
	  spogServer.errorHandle.printInfoMessageInDebugFile("/****************directAdminAndMspAdminCreateUser**************/");
	  spogServer.userLogin(csrAdminUserName, csrAdminPassword,test);
	  
	  if(!organizationType.equalsIgnoreCase(SpogConstants.MSP_SUB_ORG)){
		  //create organization
		  test.log(LogStatus.INFO,"create organization");
		  String finalOrgName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName+ org_model_prefix;
		  String organization_id = spogServer.CreateOrganizationWithCheck(finalOrgName,organizationType, organizationEmail, organizationPwd ,organizationFirstName, organizationLastName, test);
		  
		  //create create user
		  test.log(LogStatus.INFO,"create user");
		  String adminEmail = userPrefix + "_" + RandomStringUtils.randomAlphanumeric(8)+ postfix_email;	
		  Response response = spogServer.createUser(adminEmail,userPassword, userFirstName, userLastName, role_id, organization_id, phone_number, test);
		  String status = "verified";
		  if(userPassword==null){
			  status = "unverified";
		  }
		  String user_id = spogServer.checkCreateUser(response, SpogConstants.SUCCESS_POST, adminEmail ,  userFirstName, userLastName, role_id, organization_id, phone_number, status, "", test);
		  
		  if(userPassword!=null){
			  //change to direct admin
			  test.log(LogStatus.INFO,"user login");
			  spogServer.userLogin(adminEmail, userPassword);
			  String userEmail = userPrefix + "_" + RandomStringUtils.randomAlphanumeric(8)+ postfix_email;
			  String newUserFirstName = userFirstName + "_" + RandomStringUtils.randomAlphanumeric(8);
			  String newUserLastName = userLastName + "_" + RandomStringUtils.randomAlphanumeric(8);
			  response = spogServer.createUser(userEmail,userPassword, newUserFirstName ,newUserLastName , role_id, organization_id, test);
			  String new_user_id = spogServer.checkCreateUser(response, expectStatusCode,userEmail, newUserFirstName ,newUserLastName , role_id, organization_id, "", test);
			  
			  //delete user
			  if(expectStatusCode == SpogConstants.SUCCESS_POST ){
				  spogServer.DeleteUserById(new_user_id, test);
			  }
		  }
		 

		  spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		  //delete user
		  test.log(LogStatus.INFO,"delete user");
		  spogServer.DeleteUserById(user_id, test);
		  //delete organization
		  test.log(LogStatus.INFO,"delete organization");
		  spogServer.DeleteOrganizationWithCheck(organization_id, test);

		
	  }else{
		 
		  test.log(LogStatus.INFO,"create organization");
	 	  String finalOrgName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName+ org_model_prefix;
	 	  //create organization
	 	  String organization_id = spogServer.CreateOrganizationWithCheck(finalOrgName, SpogConstants.MSP_ORG, null, null, null, null, test);
	 	  
	 	  test.log(LogStatus.INFO,"create sub organization");
	 	  //create organization
	 	  String subOrgName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
	 	  String sub_organization_id = spogServer.createAccountWithCheck(organization_id, subOrgName, "", test);
	 	 
	 	  test.log(LogStatus.INFO,"create sub organization admin");

	 	  String subAdminEmail = userPrefix + "_" + RandomStringUtils.randomAlphanumeric(8)+ postfix_email;	
	 	  String subAdminUserId = spogServer.createUserAndCheck(subAdminEmail, userPassword, userFirstName, userLastName, role_id, sub_organization_id, test);
	 	  
	 	  if(userPassword!=null){
	 		  test.log(LogStatus.INFO,"sub organization admin login");
		 	   spogServer.userLogin(subAdminEmail, userPassword, test);
		 	  
		 	  String newUserEmail =userPrefix + "_" + RandomStringUtils.randomAlphanumeric(8)+ postfix_email;	
		 	  String newUserId = spogServer.createUserAndCheck(newUserEmail, userPassword, userFirstName, userLastName, role_id, sub_organization_id, test);
	 	  }
		 

 	 	  //delete organization
	 	  spogServer.userLogin(csrAdminUserName, csrAdminPassword,test);
 	 	  spogServer.DeleteOrganizationWithCheck(organization_id, test);
	  }
	
	  
  }
  
  
  @DataProvider(name = "organizationAndUserInfo3")
 	public final Object[][] getOrganizationAndUserInfo3() {
 		return new Object[][] {   {direct_org_name, SpogConstants.DIRECT_ORG, prefix_direct, common_password, direct_user_first_name, direct_user_last_name, 
 			SpogConstants.DIRECT_ADMIN ,  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 
 								{msp_org_name, SpogConstants.MSP_SUB_ORG, 	prefix_msp, common_password,msp_user_first_name, msp_user_last_name, 
 				SpogConstants.DIRECT_ADMIN,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION },
 									{msp_org_name, SpogConstants.MSP_ORG, prefix_msp, common_password,msp_user_first_name, msp_user_last_name, 
 					SpogConstants.MSP_ADMIN,SpogConstants.INSUFFICIENT_PERMISSIONS , ErrorCode.RESOURCE_PERMISSION},
 									
 				};
 	}	
  /**
   * @testcase 
   *  2. Direct admin can't create users in other organization -403.
   *  7. MSP sub organization admin can't create users in MSP's organization -403.(future sprint)
   *  16. Csr admin can create MSP sub admin.(future sprint)
   *  
   * @author shuo.zhang
   */
  @Test(enabled=true, dataProvider = "organizationAndUserInfo3")
  //2. Direct admin can't create users in other organization -403.
  public void adminCannotCreateUserInOtherOrganization(String organizationName, 
		  String organizationType,
		  String userPrefix,  String userPassword, String userFirstName, String userLastName, String role_id, int expectStatusCode, String expectedErrorCode) {
	  
	  test = rep.startTest("adminCannotCreateUserInOtherOrganization");
	  test.assignAuthor("shuo.zhang");
	  spogServer.errorHandle.printInfoMessageInDebugFile("/****************adminCannotCreateUserInOtherOrganization**************/");
	  spogServer.userLogin(csrAdminUserName, csrAdminPassword);
	  

	  if(organizationType.equalsIgnoreCase(SpogConstants.DIRECT_ORG) ||organizationType.equalsIgnoreCase(SpogConstants.MSP_ORG)){
		  //create organization
		  test.log(LogStatus.INFO,"create organization");
		  String OrgName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName+ org_model_prefix;
		  String organization_id = spogServer.CreateOrganizationWithCheck(OrgName,organizationType, null, null , null, null, test);
		  
		  //create create user
		  test.log(LogStatus.INFO,"create user");
		  String directAdminEmail = userPrefix + "_" + RandomStringUtils.randomAlphanumeric(8)+ postfix_email;
		  String userId = spogServer.createUserAndCheck(directAdminEmail,userPassword, userFirstName, userLastName, role_id, organization_id, test);	
			 
		  //create organization
		  test.log(LogStatus.INFO,"create another organization");
		  String new_direct_org_name = organizationName + RandomStringUtils.randomAlphanumeric(8)+ org_model_prefix;
		  String another_organization_id = spogServer.CreateOrganizationWithCheck(new_direct_org_name,organizationType, null, null ,"", "", test);
		  		  
		  //change to direct admin
		  test.log(LogStatus.INFO,"change to login with admin ");
		  spogServer.userLogin(directAdminEmail, common_password);
		  
		  //create new user
		  test.log(LogStatus.INFO,"create user ");
		  String userEmail = userPrefix + "_" + RandomStringUtils.randomAlphanumeric(8)+ postfix_email;
		  String userFirstName1 = userFirstName + "_" + RandomStringUtils.randomAlphanumeric(8);
		  String userLastName1 = userLastName + "_" + RandomStringUtils.randomAlphanumeric(8);
		  Response response = spogServer.createUser(userEmail,userPassword, userFirstName1 ,userLastName1 , role_id, another_organization_id, test);
		  spogServer.checkCreateUser(response, expectStatusCode, userEmail ,  userFirstName1 , userLastName1, role_id, another_organization_id,
				  expectedErrorCode, test);
	  }else if(organizationType.equalsIgnoreCase(SpogConstants.MSP_SUB_ORG)){
		  
		  //create organization
		  test.log(LogStatus.INFO,"create organization");
		  String OrgName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName+ org_model_prefix;
		  String organization_id = spogServer.CreateOrganizationWithCheck(OrgName,SpogConstants.MSP_ORG, null, null , null, null, test);
		  
		  //create organization
		  test.log(LogStatus.INFO,"create sub organization");
		  String new_direct_org_name = organizationName + RandomStringUtils.randomAlphanumeric(8);
		  String another_organization_id = spogServer.createAccountWithCheck(organization_id, new_direct_org_name, "", test);
		
		  //create msp sub admin
		  test.log(LogStatus.INFO,"create sub organization admin");
		  String userEmail = userPrefix + "_" + RandomStringUtils.randomAlphanumeric(8)+ postfix_email;
		  String userFirstName1 = userFirstName + "_" + RandomStringUtils.randomAlphanumeric(8);
		  String userLastName1 = userLastName + "_" + RandomStringUtils.randomAlphanumeric(8);
		  spogServer.createUserAndCheck(userEmail, userPassword, userFirstName1, userLastName1, role_id, another_organization_id, test);
		  
		  //change to msp sub admin
		  test.log(LogStatus.INFO,"sub admin login");
		  spogServer.userLogin(userEmail, common_password);
		  
		  test.log(LogStatus.INFO,"create to create admin in another org");
		  String userEmail1 = userPrefix + "_" + RandomStringUtils.randomAlphanumeric(8)+ postfix_email;
		  String userFirstName2 = userFirstName + "_" + RandomStringUtils.randomAlphanumeric(8);
		  String userLastName2 = userLastName + "_" + RandomStringUtils.randomAlphanumeric(8);
		  Response response = spogServer.createUser(userEmail1,userPassword, userFirstName2 ,userLastName2 , role_id, organization_id, test);
		  spogServer.checkCreateUser(response, expectStatusCode, userEmail1 ,  userFirstName2 , userLastName2, role_id, organization_id,
				  expectedErrorCode, test);
		  
	  }
	  
	
  }
  
  
  @DataProvider(name = "userInfo")
 	public final Object[][] getUserInfo() {
 		return new Object[][]   {{csrAdminUserName, csrAdminPassword, direct_user_name_email, common_password,direct_user_first_name, direct_user_last_name, SpogConstants.DIRECT_ADMIN,"", SpogConstants.INSUFFICIENT_PERMISSIONS , ErrorCode.CREATE_USER_INVALID_ORG},
 								{csrAdminUserName, csrAdminPassword, direct_user_name_email, common_password,direct_user_first_name, direct_user_last_name, SpogConstants.MSP_ADMIN,"", SpogConstants.INSUFFICIENT_PERMISSIONS , ErrorCode.CREATE_USER_INVALID_ORG},
 								{csrAdminUserName, csrAdminPassword, direct_user_name_email, common_password,direct_user_first_name, direct_user_last_name, "baby","", SpogConstants.REQUIRED_INFO_NOT_EXIST , ErrorCode.ROLE_ID_INVALID}, 
 								{csrAdminUserName, csrAdminPassword, null, common_password,direct_user_first_name, direct_user_last_name, SpogConstants.CSR_ADMIN, "", SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.ELEMENT_BLANK},
 								{csrAdminUserName, csrAdminPassword, "", common_password,direct_user_first_name, direct_user_last_name, SpogConstants.CSR_ADMIN, "", SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.ELEMENT_BLANK},
 					
 								{csrAdminUserName, csrAdminPassword, direct_user_name_email, "" ,direct_user_first_name, direct_user_last_name, SpogConstants.CSR_ADMIN, "", SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.PASSWORD_PATTERN },
 								{csrAdminUserName, csrAdminPassword, direct_user_name_email, common_password ,null, direct_user_last_name, SpogConstants.CSR_ADMIN,"",  SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.ELEMENT_BLANK },
 								{csrAdminUserName, csrAdminPassword, direct_user_name_email, common_password ,"", direct_user_last_name, SpogConstants.CSR_ADMIN, "", SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.ELEMENT_BLANK },
 								{csrAdminUserName, csrAdminPassword, direct_user_name_email, common_password ,direct_user_first_name, null, SpogConstants.CSR_ADMIN, "", SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.ELEMENT_BLANK },
 								{csrAdminUserName, csrAdminPassword, direct_user_name_email, common_password ,direct_user_first_name, "", SpogConstants.CSR_ADMIN, "", SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.ELEMENT_BLANK },
 								
 								{csrAdminUserName, csrAdminPassword, direct_user_name_email, common_password,direct_user_first_name, direct_user_last_name, null,"",SpogConstants.REQUIRED_INFO_NOT_EXIST , ErrorCode.ELEMENT_BLANK}, 
 								{csrAdminUserName, csrAdminPassword, direct_user_name_email, common_password,direct_user_first_name, direct_user_last_name, "","", SpogConstants.REQUIRED_INFO_NOT_EXIST , ErrorCode.ELEMENT_BLANK}, 
 								{csrAdminUserName, csrAdminPassword, direct_user_name_email, common_password,direct_user_first_name, direct_user_last_name, SpogConstants.DIRECT_ADMIN,UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST , ErrorCode.CAN_NOT_FIND_ORG}, 
 								{csrAdminUserName, csrAdminPassword, direct_user_name_email, "1qW",direct_user_first_name, direct_user_last_name, SpogConstants.CSR_ADMIN,"", SpogConstants.REQUIRED_INFO_NOT_EXIST , ErrorCode.NOT_FIT_LENGTH},
 								{csrAdminUserName, csrAdminPassword, direct_user_name_email, "1qWokleouuhkshfuersfewert",direct_user_first_name, direct_user_last_name, SpogConstants.CSR_ADMIN,"", SpogConstants.REQUIRED_INFO_NOT_EXIST , ErrorCode.NOT_FIT_LENGTH},
 								{csrAdminUserName, csrAdminPassword, direct_user_name_email, "1qasbdl8",direct_user_first_name, direct_user_last_name, SpogConstants.CSR_ADMIN,"", SpogConstants.REQUIRED_INFO_NOT_EXIST , ErrorCode.PASSWORD_PATTERN},
 								{csrAdminUserName, csrAdminPassword, direct_user_name_email, "Pqasbdlo",direct_user_first_name, direct_user_last_name, SpogConstants.CSR_ADMIN,"", SpogConstants.REQUIRED_INFO_NOT_EXIST , ErrorCode.PASSWORD_PATTERN},
 								{csrAdminUserName, csrAdminPassword, direct_user_name_email, "OIU1878984",direct_user_first_name, direct_user_last_name, SpogConstants.CSR_ADMIN,"", SpogConstants.REQUIRED_INFO_NOT_EXIST , ErrorCode.PASSWORD_PATTERN},
 								{csrAdminUserName, csrAdminPassword, "123.COM", common_password,direct_user_first_name, direct_user_last_name, SpogConstants.CSR_ADMIN, "", SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.EMAIL_FORMAT },
 								{csr_read_only_mail, common_password, direct_user_name_email, common_password,direct_user_first_name, direct_user_last_name, SpogConstants.DIRECT_ADMIN,this.direct_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS , ErrorCode.RESOURCE_PERMISSION},
 								{csr_read_only_mail, common_password, this.msp_user_name_email, common_password,this.msp_user_first_name, this.msp_user_last_name, SpogConstants.MSP_ADMIN,this.msp_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS , ErrorCode.RESOURCE_PERMISSION},
 								{csr_read_only_mail, common_password, this.msp_user_name_email, common_password,this.msp_user_first_name, this.msp_user_last_name, SpogConstants.MSP_ACCOUNT_ADMIN,this.msp_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS , ErrorCode.RESOURCE_PERMISSION},
 								{csr_read_only_mail, common_password, this.account_user_email, common_password,this.msp_user_first_name, this.msp_user_last_name, SpogConstants.DIRECT_ADMIN,this.account_id, SpogConstants.INSUFFICIENT_PERMISSIONS , ErrorCode.RESOURCE_PERMISSION},
 								{csr_read_only_mail, common_password, this.csr_read_only_mail, common_password,this.msp_user_first_name, this.msp_user_last_name, SpogConstants.CSR_READ_ONLY_ADMIN,this.csr_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS , ErrorCode.RESOURCE_PERMISSION},
 								{csr_read_only_mail, common_password, this.csr_read_only_mail, common_password,this.msp_user_first_name, this.msp_user_last_name, SpogConstants.CSR_ADMIN,this.csr_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS , ErrorCode.RESOURCE_PERMISSION},

 								 
 								{this.final_root_msp_user_name_email, common_password, spogServer.ReturnRandom("shuo")+ "@arcserve.com", common_password,  this.msp_user_first_name, this.msp_user_last_name, SpogConstants.MSP_ADMIN, this.sub_msp1_org_id,  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
 								{this.final_root_msp_account_admin_user_name_email, common_password,spogServer.ReturnRandom("shuo")+ "@arcserve.com", common_password,  this.msp_user_first_name, this.msp_user_last_name, SpogConstants.MSP_ADMIN, this.root_msp_org_id,  SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.MSP_CAN_NOT_OPERATE},
 								{this.final_root_msp_account_admin_user_name_email, common_password, spogServer.ReturnRandom("shuo")+ "@arcserve.com", common_password,  this.msp_user_first_name, this.msp_user_last_name, SpogConstants.MSP_ADMIN, this.sub_msp1_org_id,  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
 								 								
 								{this.final_sub_msp1_user_name_email, common_password, spogServer.ReturnRandom("shuo")+ "@arcserve.com", common_password,  this.msp_user_first_name, this.msp_user_last_name, SpogConstants.MSP_ADMIN, this.sub_msp2_org_id,  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
 								{this.final_sub_msp1_user_name_email, common_password, spogServer.ReturnRandom("shuo")+ "@arcserve.com", common_password,  this.msp_user_first_name, this.msp_user_last_name, SpogConstants.DIRECT_ADMIN, this.root_msp_direct_org_id,  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
 								{this.final_sub_msp1_user_name_email, common_password, spogServer.ReturnRandom("shuo")+ "@arcserve.com", common_password,  this.msp_user_first_name, this.msp_user_last_name, SpogConstants.DIRECT_ADMIN, this.sub_msp2_account1_id,  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
 								{this.final_sub_msp1_msp_account_user_name_email, common_password, spogServer.ReturnRandom("shuo")+ "@arcserve.com", common_password,  this.msp_user_first_name, this.msp_user_last_name, SpogConstants.MSP_ADMIN, this.sub_msp1_org_id,  SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.MSP_CAN_NOT_OPERATE},
 								{this.final_sub_msp1_msp_account_user_name_email, common_password, spogServer.ReturnRandom("shuo")+ "@arcserve.com", common_password,  this.msp_user_first_name, this.msp_user_last_name, SpogConstants.MSP_ADMIN, this.sub_msp1_account2_id,  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
 								{this.final_sub_msp1_account1_user_email, common_password, spogServer.ReturnRandom("shuo")+ "@arcserve.com", common_password,  this.msp_user_first_name, this.msp_user_last_name, SpogConstants.DIRECT_ADMIN, this.sub_msp1_account2_id,  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
 								{this.final_sub_msp1_account1_user_email, common_password, spogServer.ReturnRandom("shuo")+ "@arcserve.com", common_password,  this.msp_user_first_name, this.msp_user_last_name, SpogConstants.DIRECT_ADMIN, this.sub_msp2_account1_id,  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
 								{this.final_sub_msp1_account1_user_email, common_password, spogServer.ReturnRandom("shuo")+ "@arcserve.com", common_password,  this.msp_user_first_name, this.msp_user_last_name, SpogConstants.DIRECT_ADMIN, this.root_msp_direct_org_id,  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
 								{this.final_root_msp_direct_org_user_email, common_password, spogServer.ReturnRandom("shuo")+ "@arcserve.com", common_password,  this.msp_user_first_name, this.msp_user_last_name, SpogConstants.MSP_ADMIN, this.sub_msp1_org_id,  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
 								{this.final_root_msp_direct_org_user_email, common_password, spogServer.ReturnRandom("shuo")+ "@arcserve.com", common_password,  this.msp_user_first_name, this.msp_user_last_name, SpogConstants.DIRECT_ADMIN, this.sub_msp1_account1_id,  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
 								{this.final_root_msp_direct_org_user_email, common_password, spogServer.ReturnRandom("shuo")+ "@arcserve.com", common_password,  this.msp_user_first_name, this.msp_user_last_name, SpogConstants.MSP_ADMIN, this.root_msp_org_id,  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},

 		};
 	}

  /**
   * @testcase
   *   //20. Password can be accepted (8 charactors<=length<=20 charactors, must contains upper case/lower case/number).
   *   //12. Can NOT use a role ID to create user when role id is not exist in that organization.
   *   //11-b. Create user will be failed if provided organization ID is invalid, not existed org id or invalid permission.
   *   //13. Can NOT create user when the required data not provided (email/pwd/first name/last name/role ID)
   *   //22. Email must include '@' (ie,local-part@domain), or it will fail to create.
   *   29. csr_admin can not create direct_admin in csr org
   *   30.  csr_admin can not create msp_admin in csr org
   *   csr read only admin can't create direct admin
   *   csr read only admin can't create msp admin
   *   csr read only admin can't create msp account admin
   *   csr read only admin can't create sub org admin
   *   csr read only admin can't create csr admin
   *   csr read only admin can't create csr readonly admin
   *   {"root msp admin could not create user in sub msp org", 
   *   "root msp account admin could not create user in its own org",
   *   "root msp account admin could not create user in sub msp org", 
   *   sub msp1 admin could not create user in sub msp2
   *   sub msp1 admin could not create user in account under root msp
   *   sub msp1 admin could not create user in account under sub msp2
   *   sub msp1 account admin could not create user in its own org",
   *   sub msp1 account admin could not create user in not managed org
   *   account1 admin could not create user in account2
   *   account1 admin could not create user in account under sub msp2
   *   account1 admin could not create user in account under root msp
   *   account admin for account under root msp can't create user in sub msp1
   *   account admin for account under root msp can't create user in account 1 under sub msp1
   *   account admin for account under root msp can't create user in root msp 
   * @author shuo.zhang
   * @param userEmail
   * @param userPassword
   * @param userFirstName
   * @param userLastName
   * @param role_id
   * @param organization_id
   * @param statusCode
   * @param errorMessage
   */
  @Test(enabled=true, dataProvider = "userInfo" )

  public void CannotCreateUser(String loginUser, String loginPwd, String userEmail, String userPassword, String userFirstName, String userLastName, String role_id , String organization_id, int statusCode, String expectedErrorCode) {
	  // user login
	  test = rep.startTest("CannotCreateUser");
	  test.assignAuthor("shuo.zhang");
	  spogServer.errorHandle.printInfoMessageInDebugFile("/****************CannotCreateUser**************/");
	  spogServer.userLogin(loginUser, loginPwd);
	  
	  //create user
	  String finalUserEmail = userEmail;
	 if((finalUserEmail !=null) && (!finalUserEmail.equals(""))){
		  finalUserEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;
	  }

	  Response response = spogServer.createUser(finalUserEmail ,userPassword, userFirstName, userLastName, role_id, organization_id, test);	
	  String user_id = spogServer.checkCreateUser(response, statusCode, finalUserEmail, userFirstName, userLastName, role_id, organization_id, expectedErrorCode, test);
//	  String user_id = spogServer.checkCreateUser(response, statusCode, finalUserEmail, userFirstName, userLastName, role_id, organization_id, expectedErrorCode, expectedErrorMessage,expectedDetailErrorMessage, test);
	  
	 
  }
  
  /**
   * @testcase 
   *   //10. Create user will use the logged in user's organization by default when it doesn't provide the organization ID.
   *   23. Csr admin can create csr admin
   *  @author shuo.zhang
   */
  @Test(enabled=true)

  public void csrAdminCreateUserWithoutOrgnizationID() {
	  
	  test = rep.startTest("csrAdminCreateUserWithoutOrgnizationID");
	  test.assignAuthor("shuo.zhang");
	  spogServer.errorHandle.printInfoMessageInDebugFile("/****************csrAdminCreateUserWithoutOrgnizationID**************/");
	  spogServer.userLogin(csrAdminUserName, csrAdminPassword);
	  
	
	  //create create user
	  String directAdminEmail = prefix_direct + "_" + RandomStringUtils.randomAlphanumeric(8)+ postfix_email;
	  String newUserFirstName = direct_user_first_name + "_" + RandomStringUtils.randomAlphanumeric(8);
	  String newUserLastName = direct_user_last_name + "_" + RandomStringUtils.randomAlphanumeric(8);
	  
	  test.log(LogStatus.INFO,"create admin without org id");	
	  String user_id = spogServer.createUserAndCheck(directAdminEmail, common_password, newUserFirstName, newUserLastName, SpogConstants.CSR_ADMIN, null, test);
	  spogServer.DeleteUserById(user_id, test);
  }
  
  
  /**
   * @testcase 
   *     //19. Email is insensitive - it can't create two same user (ie. spog@ci.com/SPOG@ci.com).
   *     5. MSP admin and Direct admin Can NOT create the user with same name in sub organization. (for future sprint)
   *     6. MSP sub organization admin can create users in its organization -201. (for future sprint)
   *  @author shuo.zhang
   */
  
  @DataProvider(name = "organizationAndUserInfo2")
 	public final Object[][] getOrganizationAndUserInfo2() {
 		return new Object[][] { {null, null, prefix_direct, common_password, direct_user_first_name, direct_user_last_name, SpogConstants.CSR_ADMIN ,
 			SpogConstants.REQUIRED_INFO_NOT_EXIST,  ErrorCode.EMAIL_EXISTS}, 
 								  {direct_org_name, SpogConstants.DIRECT_ORG, prefix_direct, common_password, direct_user_first_name, direct_user_last_name,
 				SpogConstants.DIRECT_ADMIN ,SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.EMAIL_EXISTS}, 
 								 {msp_org_name, SpogConstants.MSP_SUB_ORG, 	prefix_msp, common_password,msp_user_first_name, msp_user_last_name,
 					SpogConstants.DIRECT_ADMIN,SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.EMAIL_EXISTS }
 				};
 	}		  
  
  
  @Test(enabled=true,  dataProvider = "organizationAndUserInfo2")

  public void CannotCreateUserWithSameEmail(String organizationName, 
		  String organizationType,
		  String userPrefix,  String userPassword, String userFirstName, String userLastName, String role_id, int expectStatusCode, String expectedErrorCode) {
	  // user login
	  test = rep.startTest("CannotCreateUserWithSameEmail");
	  test.assignAuthor("shuo.zhang");
	  spogServer.errorHandle.printInfoMessageInDebugFile("/****************CannotCreateUserWithSameEmail**************/");
	  spogServer.userLogin(csrAdminUserName, csrAdminPassword);
	  
	  if(organizationType==null){
		  //create create user
		  test.log(LogStatus.INFO,"create csr admin user");
		  String csrAdminEmail = userPrefix + "_" + RandomStringUtils.randomAlphanumeric(8)+ postfix_email;
		  String newUserFirstName = userFirstName + "_" + RandomStringUtils.randomAlphanumeric(8);
		  String newUserLastName = userLastName + "_" + RandomStringUtils.randomAlphanumeric(8);

		  String user_id = spogServer.createUserAndCheck(csrAdminEmail, common_password, newUserFirstName, newUserLastName, role_id, null, test);
		 
		  test.log(LogStatus.INFO,"create csr admin user again with same email");
		  newUserFirstName = userFirstName + "_" + RandomStringUtils.randomAlphanumeric(8);
		  newUserLastName = userLastName + "_" + RandomStringUtils.randomAlphanumeric(8);
		  Response response = spogServer.createUser(csrAdminEmail ,"12wQ875547", newUserFirstName, newUserLastName,role_id, null, test);	
		  test.log(LogStatus.INFO,"check the response");
		  spogServer.checkCreateUser(response, expectStatusCode, csrAdminEmail, newUserFirstName, newUserLastName,  role_id, null, expectedErrorCode, test);
		  spogServer.DeleteUserById(user_id, test);
		  
	  }else if(organizationType.equalsIgnoreCase(SpogConstants.DIRECT_ORG)){
		
		  //create organization
		  test.log(LogStatus.INFO,"create direct org");
		  String finalOrgName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName+ org_model_prefix;
		  String organization_id = spogServer.CreateOrganizationWithCheck(finalOrgName,organizationType, null, null ,null, null, test);
		  
		  //create create user
		  test.log(LogStatus.INFO,"create direct admin user");
		  String adminEmail = userPrefix + "_" + RandomStringUtils.randomAlphanumeric(8)+ postfix_email;	
		  String user_id = spogServer.createUserAndCheck(adminEmail,userPassword, userFirstName, userLastName, role_id, organization_id, test);
		 		  
		  test.log(LogStatus.INFO,"create direct admin user again");
		  Response response = spogServer.createUser(adminEmail ,"12wQ875547", userFirstName, userLastName,role_id, null, test);	
		  spogServer.checkCreateUser(response, expectStatusCode, adminEmail, userFirstName, userLastName,  role_id, null, expectedErrorCode, test);
		  spogServer.DeleteOrganizationWithCheck(organization_id, test);
	  }else{
		  //create organization
		  test.log(LogStatus.INFO,"create msp org");
		  String finalOrgName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName+ org_model_prefix;
		  String organization_id = spogServer.CreateOrganizationWithCheck(finalOrgName,SpogConstants.MSP_ORG, null, null ,null, null, test);
		  
		  //create create user
		  test.log(LogStatus.INFO,"create msp admin user");
		  String adminEmail = userPrefix + "_" + RandomStringUtils.randomAlphanumeric(8)+ postfix_email;	
		  String user_id = spogServer.createUserAndCheck(adminEmail,userPassword, userFirstName, userLastName, SpogConstants.MSP_ADMIN, organization_id, test);
		  
		  test.log(LogStatus.INFO," msp admin user login");
		  spogServer.userLogin(adminEmail, userPassword, test);
		  
		  test.log(LogStatus.INFO,"create msp sub org");
		  String subOrgName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
		  String sub_organization_id = spogServer.createAccountWithCheck(organization_id, subOrgName, "", test);
		  
		  test.log(LogStatus.INFO,"create msp sub admin user");
		  String sub_adminEmail = userPrefix + "_" + RandomStringUtils.randomAlphanumeric(8)+ postfix_email;	
		  String sub_user_id = spogServer.createUserAndCheck(sub_adminEmail,userPassword, userFirstName, userLastName, role_id, sub_organization_id, test);
		  
		  test.log(LogStatus.INFO,"create msp sub admin user again");
		  Response response = spogServer.createUser(sub_adminEmail ,"12wQ875547", userFirstName, userLastName,role_id, sub_organization_id, test);	
		  spogServer.checkCreateUser(response, expectStatusCode, sub_adminEmail, userFirstName, userLastName,  role_id, sub_organization_id, expectedErrorCode, test);
		  
		  spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		  spogServer.DeleteOrganizationWithCheck(organization_id, test);
	  }
	
  }
 

  
  @DataProvider(name = "token")
	public final Object[][] getToken() {
		return new Object[][] { {"",  ErrorCode.AUTHORIZATION_HEADER_BLANK}, 
							  {"123" , ErrorCode.AUTHORIZATION_HEADER_BLANK}};
	}		  

  /**
   * @testcase
   *   //8. the Invalid/missing admin can't create user
   *    //17.  Can NOT call API if not logged in - 401( eg JWT is invalid, missing or expired, invalid token )
   * @author shuo.zhang
   * @param token
   * @param errorMessage
   */
  @Test(enabled=true,  dataProvider = "token")
  public void notLoggedCreateUserFail(String token, String expectedErrorCode){	 
	  test = rep.startTest("notLoggedCreateUserFail");
	  test.assignAuthor("shuo.zhang");
	  spogServer.errorHandle.printInfoMessageInDebugFile("/****************notLoggedCreateUserFail**************/");
	  //set token as null
	  spogServer.setToken(token);
	  
	  //create create user
	  String csrAdminEmail = prefix_direct + "_" + RandomStringUtils.randomAlphanumeric(8)+ postfix_email;
	  String newUserFirstName = direct_user_first_name + "_" + RandomStringUtils.randomAlphanumeric(8);
	  String newUserLastName = direct_user_last_name + "_" + RandomStringUtils.randomAlphanumeric(8);

	  Response response = spogServer.createUser(csrAdminEmail ,common_password, newUserFirstName, newUserLastName, SpogConstants.CSR_ADMIN, null, test);	
	  spogServer.checkCreateUser(response, SpogConstants.NOT_LOGGED_IN, csrAdminEmail, newUserFirstName, newUserLastName,  SpogConstants.CSR_ADMIN, null, expectedErrorCode, test);
  }
    
  /**
   * @testcase
   *   //8. the Invalid/missing admin can't create user.
   * @author shuo.zhang
   */
  @Test(enabled=true)

  public void csrMissingAdminCannotCreateUser() {
	  
	  test = rep.startTest("csrMissingAdminCannotCreateUser");
	  test.assignAuthor("shuo.zhang");
	  spogServer.errorHandle.printInfoMessageInDebugFile("/****************csrMissingAdminCannotCreateUser**************/");
	  spogServer.userLogin(csrAdminUserName, csrAdminPassword);
	  

	  String csrAdminEmail = prefix_direct + "_" + RandomStringUtils.randomAlphanumeric(8)+ postfix_email;
	  String newUserFirstName = direct_user_first_name + "_" + RandomStringUtils.randomAlphanumeric(8);
	  String newUserLastName = direct_user_last_name + "_" + RandomStringUtils.randomAlphanumeric(8);
	  
	 //create csr admin	
	  test.log(LogStatus.INFO,"create csr admin user");
	  String user_id = spogServer.createUserAndCheck(csrAdminEmail, common_password, newUserFirstName, newUserLastName, SpogConstants.CSR_ADMIN, null, test);
	  //login
	  test.log(LogStatus.INFO,"csr admin login");
	  spogServer.userLogin(csrAdminEmail, common_password, test);
	  String token = spogServer.getJWTToken();
	  
	  //swith to original csr admin
	  test.log(LogStatus.INFO,"swith to original csr admin");
	  spogServer.userLogin(csrAdminUserName, csrAdminPassword);
	  //delete created admin
	  test.log(LogStatus.INFO,"delete created admin");
	  spogServer.DeleteUserById(user_id, test);
	  
	  //try to use deleted admin to create user
	  test.log(LogStatus.INFO,"try to use deleted admin to create user");
	  spogServer.setToken(token);
	  
	  String csrAdminEmail1 = prefix_direct + "_" + RandomStringUtils.randomAlphanumeric(8)+ postfix_email;
	  String newUserFirstName1 = direct_user_first_name + "_" + RandomStringUtils.randomAlphanumeric(8);
	  String newUserLastName1 = direct_user_last_name + "_" + RandomStringUtils.randomAlphanumeric(8);
	  Response response = spogServer.createUser(csrAdminEmail1 ,common_password, newUserFirstName1, newUserLastName1, SpogConstants.CSR_ADMIN, null, test);
	  spogServer.checkCreateUser(response, SpogConstants.NOT_LOGGED_IN, csrAdminEmail1, newUserFirstName1, newUserLastName1,  SpogConstants.CSR_ADMIN, null, ErrorCode.UNAUTHORIZED, test);
  }
  
  @DataProvider(name = "organizationAndUserInfo4")
 	public final Object[][] getOrganizationAndUserInfo4() {
 		return new Object[][] { {direct_org_name, SpogConstants.DIRECT_ORG,  direct_user_name_email, common_password,direct_user_first_name, direct_user_last_name, SpogConstants.DIRECT_ADMIN, 
 			SpogConstants.MSP_ADMIN, null, SpogConstants.INSUFFICIENT_PERMISSIONS,ErrorCode.CREATE_USER_INVALID_ROLE},
 			 					{direct_org_name, SpogConstants.DIRECT_ORG,  direct_user_name_email, common_password,direct_user_first_name, direct_user_last_name, SpogConstants.DIRECT_ADMIN,  
 				SpogConstants.CSR_ADMIN, null, SpogConstants.INSUFFICIENT_PERMISSIONS,ErrorCode.CREATE_USER_INVALID_ROLE},
 								{msp_org_name, SpogConstants.MSP_ORG, 	msp_user_name_email, common_password,msp_user_first_name, msp_user_last_name, SpogConstants.MSP_ADMIN, 	SpogConstants.CSR_ADMIN ,
 					null, SpogConstants.INSUFFICIENT_PERMISSIONS,ErrorCode.CREATE_USER_INVALID_ROLE},
 								{msp_org_name, SpogConstants.MSP_ORG, 	msp_user_name_email, common_password,msp_user_first_name, msp_user_last_name, SpogConstants.MSP_ADMIN, SpogConstants.DIRECT_ADMIN,
 						null, SpogConstants.INSUFFICIENT_PERMISSIONS,ErrorCode.CREATE_USER_INVALID_ORG},
 								{msp_org_name, SpogConstants.MSP_SUB_ORG, 	msp_user_name_email, common_password,msp_user_first_name, msp_user_last_name, SpogConstants.DIRECT_ADMIN, 
 									SpogConstants.CSR_ADMIN , null, SpogConstants.INSUFFICIENT_PERMISSIONS,ErrorCode.CREATE_USER_INVALID_ROLE},
 								{msp_org_name, SpogConstants.MSP_SUB_ORG, 	msp_user_name_email, common_password,msp_user_first_name, msp_user_last_name, SpogConstants.DIRECT_ADMIN, 
 	 									SpogConstants.MSP_ADMIN   ,null, SpogConstants.INSUFFICIENT_PERMISSIONS,ErrorCode.CREATE_USER_INVALID_ROLE}	,
 					{direct_org_name, SpogConstants.DIRECT_ORG,  direct_user_name_email, common_password,direct_user_first_name, direct_user_last_name, SpogConstants.DIRECT_ADMIN, 
 	 			SpogConstants.DIRECT_ADMIN, "abcde", SpogConstants.SUCCESS_POST,null},
 					{direct_org_name, SpogConstants.DIRECT_ORG,  direct_user_name_email, common_password,direct_user_first_name, direct_user_last_name, SpogConstants.DIRECT_ADMIN, 
 	 	 			SpogConstants.DIRECT_ADMIN, "8601050789034", SpogConstants.SUCCESS_POST,null},
 						
 		};
 	}
   
   
   /**
    * @testcase 
    * 24. MSP cannot create direct admin
    * 25. MSP cannot create csr admin
    * 27. direct admin cannot create msp admin
    * 28. direct admin cannot create csr admin
    * 31. account's admin cannot create csr admin
    * 32. account's admin cannot create msp admin
    * 2. create user set phone number as alphabet, successful
    * 3. create user with phone number = invalid format,successful, igonore checking
    * @author shuo.zhang
    * @param organizationName
    * @param organizationType
    * @param organizationEmail
    * @param organizationPwd
    * @param organizationFirstName
    * @param organizationLastName
    * @param userEmail
    * @param userPassword
    * @param userFirstName
    * @param userLastName
    * @param role_id
    */
   @Test(dataProvider = "organizationAndUserInfo4", enabled=true)

   public void adminCannotCreateUser(String organizationName,   String organizationType,  String userEmail, String userPassword, String userFirstName, String userLastName,
		   String role_id, String create_role_id, String phone_number, 	  int expectedStatusCode, String expectedErrorCode)
   {
 	  test = rep.startTest("adminCannotCreateUser");
 	  test.assignAuthor("shuo.zhang");
 	  spogServer.errorHandle.printInfoMessageInDebugFile("/****************adminCannotCreateUser**************/");
 	  spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
   
 	  if(!organizationType.equalsIgnoreCase(SpogConstants.MSP_SUB_ORG)){
 		  test.log(LogStatus.INFO,"create organization");
 	 	  String finalOrgName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName+ org_model_prefix;
 	 	  //create organization
 	 	  String organization_id = spogServer.CreateOrganizationWithCheck(finalOrgName, organizationType, null, null, null, null, test);
 	 	 
 	 	  //create create user
 	 	  test.log(LogStatus.INFO,"create user");
 	 	  String finalUserEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;
 	 	  Response response = spogServer.createUser(finalUserEmail ,userPassword, userFirstName, userLastName, role_id,organization_id,   test);	
 	 	  String user_id = spogServer.checkCreateUser(response, SpogConstants.SUCCESS_POST, finalUserEmail, userFirstName, userLastName, role_id, organization_id,   "", test);
 	 	  
 	 	  spogServer.userLogin(finalUserEmail, userPassword, test);
 	 	  String newUserEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;
 	 	  response = spogServer.createUser(newUserEmail ,userPassword, userFirstName, userLastName, create_role_id, organization_id,phone_number,  test);	
 	 	  String status = "verified";
		  if(userPassword==null){
			  status = "unverified";
		  }
 	 	  spogServer.checkCreateUser(response, expectedStatusCode,newUserEmail , userFirstName, userLastName, create_role_id, organization_id,phone_number,status, expectedErrorCode, test);
 	 	  
 		  spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
 		   
 	 	  //delete user
 	 	  spogServer.DeleteUserById(user_id, test);
 	 	  
 	 	  //delete organization
 	 	  spogServer.DeleteOrganizationWithCheck(organization_id, test);
 	 	
 	  }else{
 		  test.log(LogStatus.INFO,"create organization");
	 	  String finalOrgName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName+ org_model_prefix;
	 	  //create organization
	 	  String organization_id = spogServer.CreateOrganizationWithCheck(finalOrgName, SpogConstants.MSP_ORG, null, null, null, null, test);
	 	  test.log(LogStatus.INFO,"create sub organization");
	 	  String subOrgName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
	 	  //create organization
	 	  String sub_organization_id = spogServer.createAccountWithCheck(organization_id, subOrgName, "", test);
	 	  test.log(LogStatus.INFO,"create sub organization admin");
	 	  String subAdminEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;
	 	  String subAdminUserId = spogServer.createUserAndCheck(subAdminEmail, userPassword, userFirstName, userLastName, role_id,  sub_organization_id, test);
	 	  
		  test.log(LogStatus.INFO,"sub organization admin login");
	 	  spogServer.userLogin(subAdminEmail, userPassword, test);
	 	  
	 	  String newUserEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;
	 	  Response response = spogServer.createUser(newUserEmail ,userPassword, userFirstName, userLastName, create_role_id, sub_organization_id, test);	
	 	  spogServer.checkCreateUser(response, expectedStatusCode,newUserEmail , userFirstName, userLastName, create_role_id, sub_organization_id, expectedErrorCode, test);

	 	 spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
 	 	  //delete organization
 	 	  spogServer.DeleteOrganizationWithCheck(organization_id, test);
 	  }
 	
   }
   
   @DataProvider(name = "organizationAndUserInfo5")
	public final Object[][] getOrganizationAndUserInfo5() {
		return new Object[][] { {final_msp_user_name_email, common_password, RandomStringUtils.randomAlphanumeric(8)+msp_account_admin_email, common_password, msp_account_admin_first_name,
			msp_account_admin_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, msp_org_id, "+86-01080255321", SpogConstants.SUCCESS_POST, null},
			{csrAdminUserName, csrAdminPassword, RandomStringUtils.randomAlphanumeric(8)+msp_account_admin_email, common_password, msp_account_admin_first_name,
				msp_account_admin_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, msp_org_id, "+86-01080255321", SpogConstants.SUCCESS_POST, null},
			{final_direct_user_name_email, common_password, RandomStringUtils.randomAlphanumeric(8)+msp_account_admin_email, common_password, msp_account_admin_first_name,
					msp_account_admin_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, msp_org_id, "+86-01080255321", SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},		
			{final_direct_user_name_email, common_password, RandomStringUtils.randomAlphanumeric(8)+msp_account_admin_email, common_password, msp_account_admin_first_name,
						msp_account_admin_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, direct_org_id, "+86-01080255321", SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.CREATE_USER_INVALID_ROLE},
			
			{final_msp_user_name_email, common_password, RandomStringUtils.randomAlphanumeric(8)+msp_account_admin_email, common_password, msp_account_admin_first_name,
							msp_account_admin_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, another_msp_org_id, "+86-01080255321", SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},	
			{account_user_email, common_password, RandomStringUtils.randomAlphanumeric(8)+msp_account_admin_email, common_password, msp_account_admin_first_name,
								msp_account_admin_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, msp_org_id, "+86-01080255321", SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
			{account_user_email, common_password, RandomStringUtils.randomAlphanumeric(8)+msp_account_admin_email, common_password, msp_account_admin_first_name,
									msp_account_admin_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, account_id, "+86-01080255321", SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.CREATE_USER_INVALID_ROLE},
			
			{final_msp_account_admin_email, common_password, RandomStringUtils.randomAlphanumeric(8)+msp_account_admin_email, common_password, msp_account_admin_first_name,
				msp_account_admin_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, msp_org_id, "+86-01080255321", SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.MSP_CAN_NOT_OPERATE},
			{final_msp_account_admin_email, common_password, RandomStringUtils.randomAlphanumeric(8)+msp_user_name_email, common_password, msp_user_first_name,
				msp_user_last_name, SpogConstants.DIRECT_ADMIN, account_id, "+86-01080255321", SpogConstants.SUCCESS_POST, null},
			{final_msp_account_admin_email, common_password, RandomStringUtils.randomAlphanumeric(8)+msp_user_name_email, common_password, msp_user_first_name,
				msp_user_last_name, SpogConstants.DIRECT_ADMIN, another_account_id, "+86-01080255321", SpogConstants.INSUFFICIENT_PERMISSIONS,  ErrorCode.RESOURCE_PERMISSION},
			{final_msp_account_admin_email, common_password, RandomStringUtils.randomAlphanumeric(8)+direct_user_name_email, common_password, direct_user_first_name,
				direct_user_last_name, SpogConstants.DIRECT_ADMIN, direct_org_id, "+86-01080255321", SpogConstants.INSUFFICIENT_PERMISSIONS,  ErrorCode.RESOURCE_PERMISSION},
			
			{final_direct_user_name_email, common_password, RandomStringUtils.randomAlphanumeric(8)+msp_user_name_email, common_password, msp_user_first_name,
			msp_user_last_name, SpogConstants.DIRECT_ADMIN, another_account_id, "+86-01080255321", SpogConstants.INSUFFICIENT_PERMISSIONS,  ErrorCode.RESOURCE_PERMISSION},			
			{final_msp_account_admin_email, common_password, RandomStringUtils.randomAlphanumeric(8)+msp_user_name_email, common_password, msp_user_first_name,
				msp_user_last_name, SpogConstants.MSP_ADMIN, msp_org_id, "+86-01080255321", SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.MSP_CAN_NOT_OPERATE},
			{account_user_email, common_password, RandomStringUtils.randomAlphanumeric(8)+direct_user_name_email, common_password, direct_user_first_name,
					direct_user_last_name, SpogConstants.DIRECT_ADMIN, direct_org_id, "+86-01080255321", SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
			{final_msp_account_admin_email, common_password, RandomStringUtils.randomAlphanumeric(8)+msp_user_name_email, common_password, msp_user_first_name,
						msp_user_last_name, SpogConstants.CSR_ADMIN, csr_org_id, "+86-01080255321", SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
			{final_msp_account_admin_email, common_password, RandomStringUtils.randomAlphanumeric(8)+msp_account_admin_email, common_password, msp_account_admin_first_name,
				msp_account_admin_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, "", "+86-01080255321", SpogConstants.INSUFFICIENT_PERMISSIONS,  ErrorCode.CREATE_USER_INVALID_ROLE},
			{final_msp_account_admin_email, common_password, RandomStringUtils.randomAlphanumeric(8)+msp_account_admin_email, common_password, msp_account_admin_first_name,
					msp_account_admin_last_name, SpogConstants.MSP_ADMIN, "", "+86-01080255321", SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.CREATE_USER_INVALID_ROLE},
			{final_msp_account_admin_email, common_password, RandomStringUtils.randomAlphanumeric(8)+msp_account_admin_email, common_password, msp_account_admin_first_name,
						msp_account_admin_last_name, SpogConstants.DIRECT_ADMIN, "", "+86-01080255321", SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.MSP_ACCOUNT_ID_REQUIRED},
			{final_msp_account_admin_email, common_password, RandomStringUtils.randomAlphanumeric(8)+msp_account_admin_email, common_password, msp_account_admin_first_name,
							msp_account_admin_last_name, SpogConstants.CSR_ADMIN, "", "+86-01080255321", SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.CREATE_USER_INVALID_ROLE},

		};
	}
   
   
   @Test(enabled=true, dataProvider = "organizationAndUserInfo5")
   /**
    * 1. MSP admin could create its org's MSP account admin 
    * 2. CSR admin could create MSP account admin 
    * 3. direct admin couldn't create MSP account admin in msp org, report 403
    * 4. direct admin couldn't create MSP account admin in its org, report 403
    * 5. MSP admin could not create other org's MSP account admin, report 403
    * 6. account admin can't create MSP account admin in its parent org, report 403
    * 7. account admin can't create MSP account admin in its org, report 403
    * 8. MSP account admin could not create MSP account admin in its org, report 403
    * 9. MSP account admin could create mastered sub org's Account admin
    * 10. MSP account admin could not create not mastered org's account admin, report 403
    * 11. MSP account admin could not create direct admin in other direct org, report 403
    * 
    * 34. direct admin can't create account admin
    * 12.  MSP account admin could not create MSP admin in its org, report 403
    * 35. account's admin cannot create direct admin in direct org
    * 13. MSP account admin could not create csr admin, report 403
    * 14. MSP account admin could create its org's MSP account admin without set org id
    * 15. MSP account admin could not create its org's MSP admin without set org id, report 403
    * 16. MSP account admin could not create its org's direct admin without set org id, report 403
    * 17. MSP account admin could not create its org's csr admin without set org id, report 403
    * 

    * @param loginUserName
    * @param loginPassword
    * @param createdUserEmail
    * @param createdUserPassword
    * @param createdUserFirstName
    * @param createdUserLastName
    * @param createdUserRoleId
    * @param createdUserOrgId
    * @param createdUserPhoneNumber
    * @param expectedStatusCode
    * @param expectedErrorCode
    */

   public void mspAccountAdminTest(String loginUserName, String loginPassword, String createdUserEmail, String createdUserPassword, String createdUserFirstName, String createdUserLastName,
		   String createdUserRoleId, String createdUserOrgId, String createdUserPhoneNumber,  int expectedStatusCode, String expectedErrorCode) {
 	  
 	  test = rep.startTest("mspAccountAdminTest");
 	  test.assignAuthor("shuo.zhang");
 	  spogServer.errorHandle.printInfoMessageInDebugFile("/****************mspAccountAdminTest**************/");
 	  spogServer.userLogin(loginUserName, loginPassword,test);
 	
 	 String status = "verified";
	 if(createdUserPassword==null){
		  status = "unverified";
	 }
 	Response response = spogServer.createUser(createdUserEmail, createdUserPassword, createdUserFirstName, createdUserLastName, createdUserRoleId, createdUserOrgId, createdUserPhoneNumber, test);	
 	String msp_account_admin_id_1 = spogServer.checkCreateUser(response, expectedStatusCode, createdUserEmail,  createdUserFirstName, createdUserLastName, createdUserRoleId, createdUserOrgId, 
 			createdUserPhoneNumber, status, expectedErrorCode, test);

 	spogServer.DeleteUserById(msp_account_admin_id_1, test);
 	
   }
   
   
   @DataProvider(name = "organizationAndUserInfo6")
  	public final Object[][] getOrganizationAndUserInfo6() {
  		return new Object[][] { {final_msp_user_name_email, common_password, RandomStringUtils.randomAlphanumeric(8)+msp_account_admin_email, common_password, msp_account_admin_first_name,
  			msp_account_admin_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, msp_org_id, "+86-01080255321", "true","true", this.account_id, SpogConstants.SUCCESS_POST, null},
  			{final_direct_user_name_email, common_password, RandomStringUtils.randomAlphanumeric(8)+ direct_user_name_email, common_password, this.direct_user_first_name,
  	  			this.direct_user_last_name, SpogConstants.DIRECT_ADMIN, direct_org_id, "+86-01080255321", "true","true", this.account_id, SpogConstants.SUCCESS_POST, null},
  			{final_msp_user_name_email, common_password, RandomStringUtils.randomAlphanumeric(8)+msp_account_admin_email, common_password, msp_account_admin_first_name,
  	  			msp_account_admin_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, msp_org_id, "+86-01080255321", "true","true", this.account_id+";" + this.another_account_id, SpogConstants.SUCCESS_POST, null},
  			{final_msp_user_name_email, common_password, RandomStringUtils.randomAlphanumeric(8)+msp_account_admin_email, common_password, msp_account_admin_first_name,
  	  			msp_account_admin_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, msp_org_id, "+86-01080255321", "true", "true","",  SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.UUID_NULL_EMPTY},
  			{final_msp_user_name_email, common_password, RandomStringUtils.randomAlphanumeric(8)+msp_account_admin_email, common_password, msp_account_admin_first_name,
  	  			msp_account_admin_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, msp_org_id, "+86-01080255321", "true", "true",null,  SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.UUID_NULL_EMPTY},
  				{final_msp_user_name_email, common_password, RandomStringUtils.randomAlphanumeric(8)+msp_account_admin_email, common_password, msp_account_admin_first_name,
  	  			msp_account_admin_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, msp_org_id, "+86-01080255321", "true","true", another_msp_account_id, 
  	  			SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
  			{final_msp_user_name_email, common_password, RandomStringUtils.randomAlphanumeric(8)+this.msp_user_name_email, common_password, this.msp_user_first_name,
  	  			this.msp_user_last_name, SpogConstants.MSP_ADMIN, msp_org_id, "+86-01080255321", "true","true", this.account_id, SpogConstants.SUCCESS_POST, null},
  			{this.account_user_email, common_password, RandomStringUtils.randomAlphanumeric(8)+this.account_user_email, common_password, this.msp_user_first_name,
  	  			this.msp_user_last_name, SpogConstants.DIRECT_ADMIN, account_id, "+86-01080255321", "true","true", this.another_account_id, SpogConstants.SUCCESS_POST, null},
  			{this.final_msp_account_admin_email, common_password, RandomStringUtils.randomAlphanumeric(8)+this.account_user_email, common_password, this.msp_user_first_name,
  	  			this.msp_user_last_name, SpogConstants.DIRECT_ADMIN, account_id, "+86-01080255321", "true","true", this.account_id, SpogConstants.SUCCESS_POST, null},
  			{this.csrAdminUserName, this.csrAdminPassword, RandomStringUtils.randomAlphanumeric(8)+msp_account_admin_email, common_password, msp_account_admin_first_name,
  	  			msp_account_admin_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, msp_org_id, "+86-01080255321", "true","true", this.account_id, SpogConstants.SUCCESS_POST, null},
  			{csrAdminUserName, csrAdminPassword, RandomStringUtils.randomAlphanumeric(8)+msp_account_admin_email, common_password, msp_account_admin_first_name,
  	  	  			msp_account_admin_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, msp_org_id, "+86-01080255321", "true","true", this.account_id+";" + this.another_account_id, 
  	  	  			SpogConstants.SUCCESS_POST, null},
  			{csrAdminUserName, csrAdminPassword, RandomStringUtils.randomAlphanumeric(8)+msp_account_admin_email, common_password, msp_account_admin_first_name,
  	  	  	  			msp_account_admin_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, msp_org_id, "+86-01080255321", "true","true", this.another_msp_account_id,
  	  	  	  	SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.MSP_AA_NOT_BELONG_TO_MSP_ORG,},
  			{final_msp_user_name_email, common_password, RandomStringUtils.randomAlphanumeric(8)+msp_account_admin_email, common_password, msp_account_admin_first_name,
  	  			msp_account_admin_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, msp_org_id, "+86-01080255321", "true", null,"",  SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.PARAMETER_EMPTY},
  			
  			{final_msp_user_name_email, common_password, RandomStringUtils.randomAlphanumeric(8)+msp_account_admin_email, common_password, msp_account_admin_first_name,
  	  			msp_account_admin_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, msp_org_id, "+86-01080255321", null, null,"",  SpogConstants.SUCCESS_POST, null},
  			
  			
  			/*
  			{final_msp_user_name_email, common_password, RandomStringUtils.randomAlphanumeric(8)+msp_account_admin_email, common_password, msp_account_admin_first_name,
  	  	  			msp_account_admin_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, msp_org_id, "+86-01080255321", "true", "","",  SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.INVALID_PARAMETERS},*/
  			
  		};
  	}
   @Test(enabled=true, dataProvider = "organizationAndUserInfo6")
   /**
    * 2. msp admin create msp account admin with assign one account will be successful
    * 1. direct admin create user with account assignment, the user should be created successfully, but igore the assignment
    * 3. msp admin create msp account admin with assign multiple account will be successful
    * 4. msp admin create msp account admin with account id as "",  will be failed
    * 5. msp admin create msp account admin with account id as null,  will be failed
    * 6. msp admin create msp account admin with account id as other msp'org's account id,  will be failed
    * 7. msp admin create msp admin with account id, the user should be created successfully, but igore the assignment
    * 8. account admin create direct admin with account id, the user should be created successfully, but igore the assignment
    * 9. msp account admin create direct admin with account id, the user should be created successfully, but igore the assignment
    * 10. csr admin create msp account admin with assign one account will be successful
    * 11. csr admin create msp account admin with assign multiple account will be successful
    * 12. csr admin create msp account admin with account id as other msp'org's account id,  will be failed
    * 13. msp admin create msp account admin with organizations as null,  will be failed
    * 14. msp admin create msp account admin with organizations as "",  will be failed
    * 15. msp admin create msp account admin with msp_accounts as null,  will be successful
    * 
    * 
    * @param loginUserName
    * @param loginPassword
    * @param createdUserEmail
    * @param createdUserPassword
    * @param createdUserFirstName
    * @param createdUserLastName
    * @param createdUserRoleId
    * @param createdUserOrgId
    * @param createdUserPhoneNumber
    * @param enableMspAccountInfo
    * @param masteredAccountIds
    * @param expectedStatusCode
    * @param expectedErrorCode
    */
   public void createUserWithMspAccountsInfo(String loginUserName, String loginPassword, String createdUserEmail, String createdUserPassword, String createdUserFirstName, String createdUserLastName,
		   String createdUserRoleId, String createdUserOrgId, String createdUserPhoneNumber, String enableMspAccountInfo,  String enableOrg,  String masteredAccountIds, 
		   int expectedStatusCode, String expectedErrorCode) {
	   		
	   	test = rep.startTest("createUserWithMspAccountsInfo");
	 	test.assignAuthor("shuo.zhang");
	 	spogServer.errorHandle.printInfoMessageInDebugFile("/****************createUserWithMspAccountsInfo**************/");
	 	spogServer.userLogin(loginUserName, loginPassword,test);
	 
	 	String[] orgIds = null;
	 	HashMap<String, Object> msp_accounts_info = null;	
	 	if(enableMspAccountInfo!=null){
	 		msp_accounts_info = new  HashMap<String, Object> ();
	 		if(enableOrg==null){
	 			msp_accounts_info.put("organizations", null);
	 		}else if(enableOrg.equals("")){
	 			 msp_accounts_info.put("organizations", "");
	 		}else{
	 			HashMap<String, String>  orgMap = new HashMap<String, String> ();
	 			Object[] orgArray = null;
	 			if(masteredAccountIds == null){
	 				orgArray = new Object[1];
	 				orgMap.put("organization_id", null);
	 				orgArray[0]=orgMap;
	 				
	 			}else if(masteredAccountIds.equals("")){
	 				orgArray = new Object[1];
	 				orgMap.put("organization_id", "");
	 				orgArray[0]=orgMap;
	 			}else{
	 				orgIds = masteredAccountIds.split(";");
		 			orgArray = new Object[orgIds.length];
		 			for(int i=0; i<orgIds.length;i++){
		 				orgMap = new HashMap<String, String> ();
		 				orgMap.put("organization_id", orgIds[i]);
		 				orgArray[i] = orgMap;
		 			}
		 			
	 			}
	 		
	 			msp_accounts_info.put("organizations", orgArray);
	 		}
	 		

	 }
	 	
	 	
	 	 Response response = spogServer.createUser(createdUserEmail, createdUserPassword, createdUserFirstName, createdUserLastName, createdUserRoleId,
	 			createdUserOrgId, createdUserPhoneNumber, msp_accounts_info, test);	
	 	 String user_id = spogServer.checkCreateUser(response, expectedStatusCode, createdUserEmail,  createdUserFirstName, createdUserLastName, createdUserRoleId, createdUserOrgId, 
	 			createdUserPhoneNumber, "verified", expectedErrorCode, test);
	 	 if(expectedStatusCode == SpogConstants.SUCCESS_POST ){
	 		 response = userSpogServer.getAllSubOrgsAssignedToMspAccountAdmin(user_id, spogServer.getJWTToken());
	 		 if(createdUserRoleId==SpogConstants.MSP_ACCOUNT_ADMIN) { 		
				  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
				  if(enableMspAccountInfo!=null){
					  ArrayList<Map<String,Object>> orgList = new ArrayList<>();
					  orgList= response.then().extract().path("data");
					  for(int j=0;j<orgIds.length;j++)
					  {
						  if (orgList.get(j).containsValue(orgIds[j])){
							  assertTrue("It's correct", true);
						  }
					  }
				  }
				
	 		 }else{
	 			  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS);
	 		 }
	 		
		 	spogServer.DeleteUserById(user_id, test);
	 	}else{
	 		 response = spogServer.createUser(createdUserEmail, createdUserPassword, createdUserFirstName, createdUserLastName, createdUserRoleId,
	 	 			createdUserOrgId, createdUserPhoneNumber, msp_accounts_info, test);	
	 	 	 user_id = spogServer.checkCreateUser(response, expectedStatusCode, createdUserEmail,  createdUserFirstName, createdUserLastName, createdUserRoleId, createdUserOrgId, 
	 	 			createdUserPhoneNumber, "verified", expectedErrorCode, test);
	 	}
	 	
   }  
   
   /**************************** Preference language cases - Sprint 34 ********************************************/
   @DataProvider(name="createUserPreferenceLanguageCases")
   public Object[][] createUserPreferenceLanguageCases(){
	   return new Object[][] {
		   //200
		   {"Create user in direct organization with preferred language English", 
			   		final_direct_user_name_email, common_password, SpogConstants.DIRECT_ADMIN, direct_org_id, "en", SpogConstants.SUCCESS_POST, null},
		   {"Create user in direct organization with preferred language Japanese", 
			   		final_direct_user_name_email, common_password, SpogConstants.DIRECT_ADMIN, direct_org_id, "ja", SpogConstants.SUCCESS_POST, null},
		   {"Create msp user in MSP organization with preferred language English", 
					final_msp_user_name_email, common_password, SpogConstants.MSP_ADMIN, msp_org_id, "en", SpogConstants.SUCCESS_POST, null},
		   {"Create msp user in MSP organization with preferred language Japanese", 
					final_msp_user_name_email, common_password, SpogConstants.MSP_ADMIN, msp_org_id, "ja", SpogConstants.SUCCESS_POST, null},
		   {"Create msp account admin user in MSP organization with preferred language English", 
					final_msp_user_name_email, common_password, SpogConstants.MSP_ACCOUNT_ADMIN, msp_org_id, "en", SpogConstants.SUCCESS_POST, null},
		   {"Create msp account admin user in MSP organization with preferred language Japanese", 
					final_msp_user_name_email, common_password, SpogConstants.MSP_ACCOUNT_ADMIN, msp_org_id, "ja", SpogConstants.SUCCESS_POST, null},
		   {"Create customer admin user in sub organization using msp token with preferred language English", 
					final_msp_user_name_email, common_password, SpogConstants.DIRECT_ADMIN, account_id, "en", SpogConstants.SUCCESS_POST, null},
		   {"Create customer user in sub organization using msp token with preferred language Japanese", 
					final_msp_user_name_email, common_password, SpogConstants.DIRECT_ADMIN, account_id, "ja", SpogConstants.SUCCESS_POST, null},
		   {"Create direct admin user in sub organization using sub org token with preferred language English", 
					account_user_email , common_password, SpogConstants.DIRECT_ADMIN, account_id, "en", SpogConstants.SUCCESS_POST, null},
		   {"Create direct admin user in sub organization using sub org token with preferred language Japanese", 
					account_user_email, common_password, SpogConstants.DIRECT_ADMIN, account_id, "ja", SpogConstants.SUCCESS_POST, null},

		   //preferred language value none - not adding to payload
		   {"Create user in direct organization with preferred language none", 
				   		final_direct_user_name_email, common_password, SpogConstants.DIRECT_ADMIN, direct_org_id, "none", SpogConstants.SUCCESS_POST, null},
		   {"Create msp user in MSP organization with preferred language none", 
							final_msp_user_name_email, common_password, SpogConstants.MSP_ADMIN, msp_org_id, "none", SpogConstants.SUCCESS_POST, null},
		   {"Create msp account admin user in MSP organization with preferred language none", 
								final_msp_user_name_email, common_password, SpogConstants.MSP_ACCOUNT_ADMIN, msp_org_id, "none", SpogConstants.SUCCESS_POST, null},
		   {"Create customer admin user in sub organization using msp token with preferred language none", 
									final_msp_user_name_email, common_password, SpogConstants.DIRECT_ADMIN, account_id, "none", SpogConstants.SUCCESS_POST, null},
		   {"Create direct admin user in sub organization using sub org token with preferred language none", 
										account_user_email , common_password, SpogConstants.DIRECT_ADMIN, account_id, "none", SpogConstants.SUCCESS_POST, null},
		   
		   //preferred language value null
		   {"Create user in direct organization with preferred language null", 
				   		final_direct_user_name_email, common_password, SpogConstants.DIRECT_ADMIN, direct_org_id, null, SpogConstants.SUCCESS_POST, null},
		   {"Create msp user in MSP organization with preferred language null", 
							final_msp_user_name_email, common_password, SpogConstants.MSP_ADMIN, msp_org_id, null, SpogConstants.SUCCESS_POST, null},
		   {"Create msp account admin user in MSP organization with preferred language null", 
								final_msp_user_name_email, common_password, SpogConstants.MSP_ACCOUNT_ADMIN, msp_org_id, null, SpogConstants.SUCCESS_POST, null},
		   {"Create customer admin user in sub organization using msp token with preferred language null", 
									final_msp_user_name_email, common_password, SpogConstants.DIRECT_ADMIN, account_id, null, SpogConstants.SUCCESS_POST, null},
		   {"Create direct admin user in sub organization using sub org token with preferred language null", 
										account_user_email , common_password, SpogConstants.DIRECT_ADMIN, account_id, null, SpogConstants.SUCCESS_POST, null},
		   
		   //preferred language value empty string
		   {"Create user in direct organization with preferred language empty string", 
				   		final_direct_user_name_email, common_password, SpogConstants.DIRECT_ADMIN, direct_org_id, "", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.PREFERENCE_LANGUAGE_FORMAT_DOESNOT_MATCH},
		   {"Create msp user in MSP organization with preferred language empty string", 
							final_msp_user_name_email, common_password, SpogConstants.MSP_ADMIN, msp_org_id, "", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.PREFERENCE_LANGUAGE_FORMAT_DOESNOT_MATCH},
		   {"Create msp account admin user in MSP organization with preferred language empty string", 
								final_msp_user_name_email, common_password, SpogConstants.MSP_ACCOUNT_ADMIN, msp_org_id, "", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.PREFERENCE_LANGUAGE_FORMAT_DOESNOT_MATCH},
		   {"Create customer admin user in sub organization using msp token with preferred language empty string", 
									final_msp_user_name_email, common_password, SpogConstants.DIRECT_ADMIN, account_id, "", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.PREFERENCE_LANGUAGE_FORMAT_DOESNOT_MATCH},
		   {"Create direct admin user in sub organization using sub org token with preferred language empty string", 
									account_user_email , common_password, SpogConstants.DIRECT_ADMIN, account_id, "", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.PREFERENCE_LANGUAGE_FORMAT_DOESNOT_MATCH},
		  	
		   //root msp
		   {"root msp admin could create user in its own org", 
											this.final_root_msp_user_name_email, common_password, SpogConstants.MSP_ADMIN, this.root_msp_org_id, null, SpogConstants.SUCCESS_POST, null},
		   {"root msp admin could create user in account org", 
												this.final_root_msp_user_name_email, common_password, SpogConstants.DIRECT_ADMIN, this.root_msp_direct_org_id, null, SpogConstants.SUCCESS_POST, null},
		  
		   { "root msp account admin could create user in managed org",	this.final_root_msp_account_admin_user_name_email, common_password, SpogConstants.DIRECT_ADMIN, this.root_msp_direct_org_id, null, SpogConstants.SUCCESS_POST, null},
		   
		   {"sub1 msp admin could create user in its own org", 
				this.final_sub_msp1_user_name_email, common_password, SpogConstants.MSP_ADMIN, this.sub_msp1_org_id, null, SpogConstants.SUCCESS_POST, null}, 
		  {"sub1 msp admin could create user in account org",
				this.final_sub_msp1_user_name_email, common_password, SpogConstants.DIRECT_ADMIN, this.sub_msp1_account1_id, null, SpogConstants.SUCCESS_POST, null},
		   {"sub1 msp account admin could create user in managed org", 
					this.final_sub_msp1_msp_account_user_name_email, common_password, SpogConstants.DIRECT_ADMIN, this.sub_msp1_account1_id, null, SpogConstants.SUCCESS_POST, null},
		      {"account1 admin could create user in its org", 
						this.final_sub_msp1_account1_user_email, common_password, SpogConstants.DIRECT_ADMIN, this.sub_msp1_account1_id, null, SpogConstants.SUCCESS_POST, null},
		   {"account admin could create user in its org", 
							this.final_root_msp_direct_org_user_email, common_password, SpogConstants.DIRECT_ADMIN, this.root_msp_direct_org_id, null, SpogConstants.SUCCESS_POST, null},
	  
	   };
   }
   
   @Test(enabled=true,dataProvider="createUserPreferenceLanguageCases")
   public void createUserTest(String caseType,
		   						String loginUserName,
		   						String loginPassword,
		   						String role_id,
		   						String organization_id,
		   						String preference_language,
		   						int expectedStatusCode,
		   						SpogMessageCode expectedErrorMessage ) {
	
	   	test = rep.startTest(caseType);
	 	test.assignAuthor("Rakesh.Chalamala");
	 	spogServer.userLogin(loginUserName, loginPassword,test);
	 	
	 	String first_name = spogServer.ReturnRandom("first");
	 	String last_name = spogServer.ReturnRandom("last");
	 	String email = spogServer.ReturnRandom("email")+"@spogqa.com";
	 	String phone = "9696969696";
	 	String password = common_password;
	 		 
	 	spogServer.createUserWithCheck(first_name, last_name, email, phone, role_id, organization_id, preference_language, password, expectedStatusCode, expectedErrorMessage, test);   
	   
   }
   
   /************************************end********************************************/

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

	 @AfterClass
	  public void subUpdatebd() {
		  
		  spogServer.DeleteUserById(this.csr_read_only_admin_id);
		  this.updatebd();
		 
	  }

}
