package api.users;

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

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.ErrorCode;
import Constants.SpogConstants;
import InvokerServer.Org4SPOGServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class AssignAccountsTest extends base.prepare.PrepareOrgInfo {
	
	 @Parameters({ "pmfKey"})
	  public AssignAccountsTest(String pmfKey) {
		super(pmfKey);
		// TODO Auto-generated constructor stub
	}

	  private SPOGServer spogServer;
	  private UserSpogServer userSpogServer;
	  private Org4SPOGServer org4SpogServer;
	  private String csrAdminUserName;
	  private String csrAdminPassword;
	  private String csrReadOnlyAdminUserName;
	  private String csrReadOnlyAdminPassword;
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
	  
	  private String csr_org_id;
	  //this is for update portal, each testng class is taken as BQ set
	/*  private SQLServerDb bqdb1;
	  public int Nooftest;
	  private long creationTime;
	  private String BQName=null;
	  private String runningMachine;
	  private testcasescount count1;
	  private String buildVersion;
	  private ExtentReports rep;
	  
	  */
	private String another_msp_account_id;
	private String another_msp_final_msp_account_admin_email;
	private String another_msp_msp_account_admin_id;
	private String  org_model_prefix=this.getClass().getSimpleName();


	  
	  
	  @BeforeClass
	  @Parameters({ "baseURI", "port","logFolder","buildVersion" ,"csrAdminUserName", "csrAdminPassword", "csrReadOnlyAdminUserName","csrReadOnlyAdminPassword"})
	  public void beforeClass(String baseURI, String port, String logFolder,  String buildVersion,  String adminUserName, String adminPassword, String csrROAdminUserName, String csrROPwd ) throws UnknownHostException {
		
		
	 	  spogServer = new SPOGServer(baseURI, port);
	 	  userSpogServer = new UserSpogServer(baseURI, port);
		  org4SpogServer = new Org4SPOGServer(baseURI, port);
		  rep = ExtentManager.getInstance("CreateUserTest",logFolder);
		  this.csrAdminUserName = adminUserName;
		  this.csrAdminPassword = adminPassword;
		  this.csrReadOnlyAdminUserName = csrROAdminUserName;
		  this.csrReadOnlyAdminPassword = csrROPwd;
			 
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
			//*******************create direct org,user,**********************/

			String prefix = RandomStringUtils.randomAlphanumeric(8);
			test.log(LogStatus.INFO,"create a direct org");
			direct_org_id = spogServer.CreateOrganizationWithCheck(prefix+direct_org_name+org_model_prefix , SpogConstants.DIRECT_ORG, null, null, null, null, test);
			final_direct_user_name_email = prefix + direct_user_name_email;
			
			test.log(LogStatus.INFO,"create a admin under direct org");
			direct_user_id = spogServer.createUserAndCheck(final_direct_user_name_email, common_password, prefix + direct_user_first_name, prefix + direct_user_last_name, SpogConstants.DIRECT_ADMIN, direct_org_id, test);
			spogServer.userLogin(final_direct_user_name_email, common_password);
		  	

			//************************create msp org,user,*************************************
			spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
			prefix = RandomStringUtils.randomAlphanumeric(8);
			test.log(LogStatus.INFO,"create a msp org");
			msp_org_id = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name+org_model_prefix , SpogConstants.MSP_ORG, null, null, null, null, test);
			final_msp_user_name_email = prefix + msp_user_name_email;
			
			test.log(LogStatus.INFO,"create a msp admin under msp org");
			msp_user_id = spogServer.createUserAndCheck(final_msp_user_name_email, common_password, prefix + msp_user_first_name, prefix + msp_user_last_name, SpogConstants.MSP_ADMIN, msp_org_id, test);
			
			test.log(LogStatus.INFO,"create a msp account admin under msp org");
			final_msp_account_admin_email = prefix + this.msp_account_admin_email;
			msp_account_admin_id = spogServer.createUserAndCheck(final_msp_account_admin_email, common_password, prefix + msp_account_admin_first_name, prefix + msp_account_admin_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, msp_org_id, test);
			
		
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
			
			//create acount and user  another msp org
			spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
			prefix = RandomStringUtils.randomAlphanumeric(8);
			test.log(LogStatus.INFO,"create a another msp org");
			another_msp_org_id = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name+org_model_prefix , SpogConstants.MSP_ORG, null, null, null, null, test);
			
			
			test.log(LogStatus.INFO,"create a msp account admin under another  msp org");
			another_msp_final_msp_account_admin_email = prefix + this.msp_account_admin_email;
			another_msp_msp_account_admin_id = spogServer.createUserAndCheck(another_msp_final_msp_account_admin_email, common_password, prefix + msp_account_admin_first_name, prefix + msp_account_admin_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, another_msp_org_id, test);
			

			test.log(LogStatus.INFO,"Creating a account For another msp org");
			prefix = RandomStringUtils.randomAlphanumeric(8);
			another_msp_account_id = spogServer.createAccountWithCheck(another_msp_org_id, "sub_" + prefix + msp_org_name, "", test);

		  	
			
			test.log(LogStatus.INFO,"assign account to msp account admin");
			String[] mspAccountAdmins = new String []{msp_account_admin_id};
			userSpogServer.assignMspAccountAdmins(msp_org_id, account_id, mspAccountAdmins , spogServer.getJWTToken()); 
			
	
			csr_org_id = spogServer.GetLoggedinUserOrganizationID();
			prepare(baseURI, port, logFolder, this.csrAdminUserName,  this.csrAdminPassword, this.getClass().getSimpleName() );
		

	  }
	  
	  
	  @DataProvider(name = "organizationAndUserInfo6")
	  	public final Object[][] getOrganizationAndUserInfo6() {
	  		return new Object[][] { 
	  			{final_direct_user_name_email, common_password,"new", "true",  this.account_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,msp_org_id},
	  			{final_msp_user_name_email, common_password,"new", "true",this.account_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null,msp_org_id},
	  			{final_msp_user_name_email, common_password, "new","true",this.account_id + ";" + this.another_account_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null,msp_org_id},
	  			{final_msp_user_name_email, common_password,  UUID.randomUUID().toString(), "true",this.account_id, SpogConstants.RESOURCE_NOT_EXIST, ErrorCode.USER_NOT_EXIST_OR_INACTIVE,msp_org_id},
	  			{final_msp_user_name_email, common_password,  this.msp_user_id, "true",this.account_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.USER_IS_NOT_MSP_ACCOUNT_ADMIN,msp_org_id},
	  			{final_msp_user_name_email, common_password,  this.account_user_id, "true",this.account_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.USER_IS_NOT_MSP_ACCOUNT_ADMIN,msp_org_id},
	  			{final_msp_user_name_email, common_password,  null, "true",this.account_id, SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.ELEMENT_NOT_UUID,msp_org_id},
	  			{final_msp_user_name_email, common_password,  "", "true",this.account_id, SpogConstants.NOT_ALLOWED_ON_RESOURCE, ErrorCode.NO_ALLOWED_GET_RESOURCE,msp_org_id},
	  			{final_msp_user_name_email, common_password,  this.another_msp_msp_account_admin_id, "true",this.another_msp_account_id, 
	  				SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,msp_org_id},
	  			{final_msp_user_name_email, common_password,"new", "true",this.account_id+ ";" + account_id, SpogConstants.REQUIRED_INFO_NOT_EXIST, 
	  				ErrorCode.MULTIPLE_OCCURENCE_ORG_ID,msp_org_id},
	  			{final_msp_user_name_email, common_password,"new", "true",  UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST, ErrorCode.CAN_NOT_FIND_ORG,msp_org_id},
	  			{final_msp_user_name_email, common_password,"new", "true",  null, SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.UUID_NULL_EMPTY,msp_org_id},
	  			{final_msp_user_name_email, common_password,"new", "true",  "", SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.UUID_NULL_EMPTY,msp_org_id},
	  			{final_msp_user_name_email, common_password,"new", "true",  this.another_msp_account_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
	  			 ErrorCode.RESOURCE_PERMISSION,msp_org_id},
	  			{final_msp_user_name_email, common_password,"new", "true",  this.msp_org_id, SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.NOT_VALID_MSP_ACCOUNT,msp_org_id},
	  			{this.final_msp_account_admin_email, common_password,"new", "true",  this.account_id, SpogConstants.INSUFFICIENT_PERMISSIONS, 
	  				ErrorCode.RESOURCE_PERMISSION,msp_org_id},
	  			{this.account_user_email, common_password,"new", "true",  this.account_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,msp_org_id},
	  			{this.csrReadOnlyAdminUserName, this.csrReadOnlyAdminPassword,this.msp_account_admin_id, "true",  this.account_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,msp_org_id},
	  			{final_root_msp_user_name_email, common_password,this.root_msp_account_admin_user_id, "true", this.root_msp_direct_org1_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null,null},
	  			{final_root_msp_user_name_email, common_password,this.sub_msp1_msp_account_admin_id, "true", this.sub_msp1_account2_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,null},
	  			{final_root_msp_account_admin_user_name_email, common_password,"new", "true", this.root_msp_direct_org1_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,this.root_msp_org_id},
	  			{final_root_msp_account_admin_user_name_email, common_password,this.sub_msp1_msp_account_admin_id, "true", this.sub_msp1_account2_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,null},	  			
	  			{final_root_msp_direct_org_user_email, common_password,this.root_msp_account_admin_user_id, "true", this.root_msp_direct_org1_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,null},
	  			{final_root_msp_direct_org_user_email, common_password,this.sub_msp1_msp_account_admin_id, "true", this.sub_msp1_account2_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,null},
	  			{final_sub_msp1_user_name_email, common_password,this.root_msp_account_admin_user_id, "true", this.root_msp_direct_org1_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,null},
	  			{final_sub_msp1_user_name_email, common_password,"new", "true", this.sub_msp1_account2_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null, this.sub_msp1_org_id},
	  			{final_sub_msp1_msp_account_user_name_email, common_password,this.root_msp_account_admin_user_id, "true", this.root_msp_direct_org1_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,null},
	  			{final_sub_msp1_msp_account_user_name_email, common_password,sub_msp1_msp_account_admin_id, "true", this.sub_msp1_account2_id,  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,null},
	  			
	  			{final_sub_msp2_user_name_email, common_password,sub_msp1_msp_account_admin_id, "true", this.sub_msp1_account2_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,null},
	  			{this.final_sub_msp1_account1_user_email, common_password,this.root_msp_account_admin_user_id, "true", this.root_msp_direct_org1_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,null},
	  			{final_sub_msp1_account1_user_email, common_password,sub_msp1_msp_account_admin_id, "true", this.sub_msp1_account2_id,  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,null},
	  			
	  	
	  			
	  		};
	  	}
	  @Test(enabled=true, dataProvider = "organizationAndUserInfo6")
	  
	  /**
	   * 1. direct admin can't call the api, will report 403, Error Code:00100101
	   * 2. msp admin assign one account to msp account admin will be sucessful
	   * 3. msp admin assign multiple accounts to msp account admin will be sucessful
	   * 4. msp admin assign accounts to a non-exist msp account admin will be report 400. Error Code:00200007
	   * 5. msp admin assign accounts to msp admin will be failed. Error Code::00200029
	   * 6. msp admin assign accounts to account admin will be failed. Error Code::00200029
	   * 7.set {id} as null,  msp admin assign accounts will be failed.
	   * 8.set {id} as "",  msp admin assign accounts will be failed.
	   * 9. msp admin assign accounts to another msp org's msp account admin, will be failed. Error Code:00300017
	   * 11. set the organization id twice in request, will be failed. Error Code:00200034
	   * 12. set the organization id as random uuid, the calling will be failed.
	   * 13. set the organization id as null, the calling will be failed.
	   * 14. set the organization id as "", the calling will be failed.
	   * 15. set the organization id as other msp org's acount, the calling will be failed.  Error Code:00300017
	   * 16. set the organization id as direct org or msp org,  the calling will be failed.  Error Code:0030000E
	   * 17. msp account admin try to assign accounts to itself, the calling will be failed. Error Code:00200031
	   * 18. account admin can't call the api, report 403
	   * 
	   * root msp admin can assign accounts to root msp msp account admin
	   * root msp admin can assign accounts to sub msp1 msp account admin
	  
	   * 
	   * @param loginUserName
	   * @param loginPassword
	   * @param createdUserEmail
	   * @param createdUserPassword
	   * @param createdUserFirstName
	   * @param createdUserLastName
	   * @param createdUserRoleId
	   * @param createdUserOrgId
	   * @param enableMspAccountInfo
	   * @param enableOrg
	   * @param masteredAccountIds
	   * @param expectedStatusCode
	   * @param expectedErrorCode
	   */
	  public void createUserAndAssignAccountsInfo(String loginUserName, String loginPassword, String mspAccountId,  String enableOrg,  String masteredAccountIds, 
			   int expectedStatusCode, String expectedErrorCode, String orgID) {
		   		
		   	test = rep.startTest("createUserWithMspAccountsInfo");
		 	test.assignAuthor("shuo.zhang");
		 	spogServer.errorHandle.printInfoMessageInDebugFile("/****************createUserAndAssignAccountsInfo**************/");
		 	spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword,test);
		 	String user_id = mspAccountId;
		 	if((mspAccountId!=null) && mspAccountId.equalsIgnoreCase("new")){
		 		user_id = spogServer.createUserAndCheck(RandomStringUtils.randomAlphanumeric(8)+this.msp_user_name_email, common_password,
			    		this.msp_user_first_name, this.msp_user_last_name,  SpogConstants.MSP_ACCOUNT_ADMIN, orgID, test);
		 	}
		  
		    spogServer.userLogin(loginUserName, loginPassword,test);	    
		
		    Response response = userSpogServer.assignAccounts(user_id, enableOrg, masteredAccountIds, spogServer.getJWTToken(), test);
		    userSpogServer.checkAssignAccounts(response, masteredAccountIds, expectedStatusCode, expectedErrorCode, test);
	

		 }
		 
	  
	  @Test(enabled=true)
	  /**
	   * 10. call  assignaccount api twice, the second time will be failed. Error Code:00300012
	   * 19. when the msp account admin is not active, msp admin call the api will be failed.  Error Code:00200007
	   * 21. call api without login, authentification fail
	   * 20. call api with invalid token, authentification fail
	   */
	  public void errorHandling() {
		   		
		   	test = rep.startTest("errorHandling");
		 	test.assignAuthor("shuo.zhang");
		 	spogServer.errorHandle.printInfoMessageInDebugFile("/****************errorHandling**************/");
		 	spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword,test);
			 	
		 	String	user_id = spogServer.createUserAndCheck(RandomStringUtils.randomAlphanumeric(8)+this.msp_user_name_email, common_password,
			    		this.msp_user_first_name, this.msp_user_last_name,  SpogConstants.MSP_ACCOUNT_ADMIN, msp_org_id, test);
		 	Response response = null;
		 	for(int i=0; i<2; i++){
		 		response = userSpogServer.assignAccounts(user_id, "true", this.account_id, spogServer.getJWTToken(), test);
		 	}
		 	// call  assignaccount api twice check the response
		    userSpogServer.checkAssignAccounts(response, account_id, SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.MSP_AA_EXISITS, test);
		    
		    
		    //check response when assign to blocked msp account admin 
		    user_id = spogServer.createUserAndCheck(RandomStringUtils.randomAlphanumeric(8)+this.msp_user_name_email, common_password,
		    		this.msp_user_first_name, this.msp_user_last_name,  SpogConstants.MSP_ACCOUNT_ADMIN, msp_org_id, test);
		    spogServer.updateUserByIdForBlockedStatus(user_id, "true", spogServer.getJWTToken(), test);		  
		    response = userSpogServer.assignAccounts(user_id, "true", this.account_id, spogServer.getJWTToken(), test);
		    userSpogServer.checkAssignAccounts(response, account_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.USER_IS_BLOCKED, test);
		    
		    
		    
		    //without login
			response = userSpogServer.assignAccountsWithoutLogin(user_id,  "true", this.account_id, test);
			userSpogServer.checkAssignAccounts(response, null, SpogConstants.NOT_LOGGED_IN, ErrorCode.AUTHORIZATION_HEADER_BLANK, test);
			
			//random token
			response = userSpogServer.assignAccounts(user_id, "true", this.account_id, UUID.randomUUID().toString(), test);
			userSpogServer.checkAssignAccounts(response, null, SpogConstants.NOT_LOGGED_IN, ErrorCode.AUTHORIZATION_HEADER_BLANK, test);

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
