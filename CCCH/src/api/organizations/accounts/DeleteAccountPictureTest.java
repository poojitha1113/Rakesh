package api.organizations.accounts;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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

import Constants.ErrorCode;
import Constants.SpogConstants;
import InvokerServer.SPOGServer;

import InvokerServer.UserSpogServer;

import InvokerServer.Org4SPOGServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;


public class DeleteAccountPictureTest  extends base.prepare.PrepareOrgInfo {

	@Parameters({ "pmfKey"})
	  public DeleteAccountPictureTest(String pmfKey) {
		super(pmfKey);
		// TODO Auto-generated constructor stub
	}


	private SPOGServer spogServer;
	  private UserSpogServer userSpogServer;
	  private Org4SPOGServer org4SpogServer;
	  
	  private String postfix_email = "@arcserve.com";
	  private String common_password = "Welcome*02";
	  
	  private String prefix_direct = "spogqa_shuo_direct";
	  private String direct_org_name = prefix_direct + "_org";
	  private String direct_user_name = prefix_direct + "_admin";
	  private String direct_user_name_email  =direct_user_name + postfix_email;
	  private String direct_user_first_name = direct_user_name + "_first_name";
	  private String direct_user_last_name = direct_user_name + "_last_name";
	  private String direct_org_id;
	  private String direct_user_id;
	  private String final_direct_user_name_email;

	  private String prefix_msp = "spogqa_shuo_msp";
	  private String msp_org_name = prefix_msp + "_org";
	  private String msp_user_name = prefix_msp + "_admin";
	  private String msp_user_name_email = prefix_msp + "_admin" + postfix_email;
	  private String msp_user_first_name = msp_user_name + "_first_name";
	  private String msp_user_last_name = msp_user_name + "_last_name";
	  private String msp_user_id;

	  private String msp_org_id;
	  private String final_msp_user_name_email;
	  
	  private String prefix_msp_account = "spogqa_shuo_msp_account";
	  private String msp_account_admin_name = prefix_msp_account + "_admin";
	  private String msp_account_admin_email =msp_account_admin_name + postfix_email;
	  private String msp_account_admin_first_name = msp_account_admin_name + "_first_name";
	  private String msp_account_admin_last_name = msp_account_admin_name + "_last_name";
	  private String msp_account_admin_id;
	  private String final_msp_account_admin_email;
	  
	  
	  private String prefix_account = "spogqa_shuo_account";
	  private String account_user_name = prefix_account + "_admin";
	  private String account_user_name_email = prefix_account + "_admin" + postfix_email;
	  private String account_user_first_name = account_user_name + "_first_name";
	  private String account_user_last_name = account_user_name + "_last_name";
	  private String account_id;
	  private String account_user_email;
	  private String account_user_id;
	  private String another_account_id;
	  
	  private String csrAdminUserName;
	  private String csrAdminPassword;
	  private String csrReadOnlyAdminUserName;
	  private String csrReadOnlyAdminPassword;
	  private ExtentTest test;
	  
/*	  private ExtentReports rep;
	  private SQLServerDb bqdb1;
	  public int Nooftest;
	  private long creationTime;
	  private String BQName=null;
	  private String runningMachine;
	  private testcasescount count1;
	  private String buildVersion;*/
	private String another_msp_org_id;
	private String another_final_msp_user_name_email;
	private String another_msp_user_id;
	private String  org_model_prefix=this.getClass().getSimpleName();
	
	  
	  @BeforeClass
	  @Parameters({ "baseURI", "port", "logFolder", "csrAdminUserName", "csrAdminPassword", "buildVersion", "csrReadOnlyAdminUserName","csrReadOnlyAdminPassword"})
	  public void beforeClass(String baseURI, String port, String logFolder, String adminUserName, String adminPassword ,  String buildVersion, String csrROAdminUserName, String csrROPwd ) throws UnknownHostException {
		
		 	  spogServer = new SPOGServer(baseURI, port);
		 	  userSpogServer = new UserSpogServer(baseURI, port);
		 	  org4SpogServer = new Org4SPOGServer(baseURI, port);
		 
			  rep = ExtentManager.getInstance("DeleteAccountPictureTest",logFolder);
			  this.csrAdminUserName = adminUserName;
			  this.csrAdminPassword = adminPassword;
			  this.csrReadOnlyAdminUserName = csrROAdminUserName;
			  this.csrReadOnlyAdminPassword = csrROPwd;
			  test = rep.startTest("beforeClass");
			  spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
			
			
			//*******************create direct org,user,**********************/
			
			String prefix = RandomStringUtils.randomAlphanumeric(8);
			test.log(LogStatus.INFO,"create a direct org");
			direct_org_id = spogServer.CreateOrganizationWithCheck(prefix+direct_org_name+org_model_prefix, SpogConstants.DIRECT_ORG, null, null, null, null, test);
			final_direct_user_name_email = prefix + direct_user_name_email;
			
			test.log(LogStatus.INFO,"create a admin under direct org");
			direct_user_id = spogServer.createUserAndCheck(final_direct_user_name_email, common_password, prefix + direct_user_first_name, prefix + direct_user_last_name, SpogConstants.DIRECT_ADMIN, direct_org_id, test);
					  	

			//************************create msp org,user,*************************************
	
			prefix = RandomStringUtils.randomAlphanumeric(8);
			test.log(LogStatus.INFO,"create a msp org");
			msp_org_id = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name+org_model_prefix , SpogConstants.MSP_ORG, null, null, null, null, test);
			final_msp_user_name_email = prefix + msp_user_name_email;
			
			test.log(LogStatus.INFO,"create a msp admin under msp org");
			msp_user_id = spogServer.createUserAndCheck(final_msp_user_name_email, common_password, prefix + msp_user_first_name, prefix + msp_user_last_name, SpogConstants.MSP_ADMIN, msp_org_id, test);
			
			test.log(LogStatus.INFO,"create a msp account admin under msp org");
			final_msp_account_admin_email = prefix + this.msp_account_admin_email;
			msp_account_admin_id = spogServer.createUserAndCheck(final_msp_account_admin_email, common_password, prefix + msp_account_admin_first_name, prefix + msp_account_admin_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, msp_org_id, test);
			
		

			//create account, account user and site
			test.log(LogStatus.INFO,"Creating a account For msp org");
			account_id = spogServer.createAccountWithCheck(msp_org_id, "sub_" + prefix + msp_org_name, "", test);
			prefix = RandomStringUtils.randomAlphanumeric(8);
		
			test.log(LogStatus.INFO,"Creating a account user For account org");
			account_user_email = prefix + account_user_name_email;
			account_user_id = spogServer.createUserAndCheck(account_user_email, common_password, prefix + account_user_first_name, prefix + account_user_last_name, SpogConstants.DIRECT_ADMIN, account_id, test);
			
				
		  	
			test.log(LogStatus.INFO,"assign account to msp account admin");
			String[] mspAccountAdmins = new String []{msp_account_admin_id};
			userSpogServer.assignMspAccountAdmins(msp_org_id, account_id, mspAccountAdmins , spogServer.getJWTToken()); 
			
			//create account, account user 
			test.log(LogStatus.INFO,"Creating another account For msp org");
			prefix = RandomStringUtils.randomAlphanumeric(8);
			another_account_id = spogServer.createAccountWithCheck(msp_org_id, "sub_" + prefix + msp_org_name, "", test);
		
	
			//************************create another msp org,user,*************************************
			
			prefix = RandomStringUtils.randomAlphanumeric(8);
			test.log(LogStatus.INFO,"create another msp org");
			another_msp_org_id = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name +org_model_prefix, SpogConstants.MSP_ORG, null, null, null, null, test);
			another_final_msp_user_name_email = prefix + msp_user_name_email;
			
			test.log(LogStatus.INFO,"create a msp admin under another msp org");
			another_msp_user_id = spogServer.createUserAndCheck(another_final_msp_user_name_email, common_password, prefix + msp_user_first_name, prefix + msp_user_last_name, SpogConstants.MSP_ADMIN, another_msp_org_id, test);
			
			 prepare(baseURI, port, logFolder, this.csrAdminUserName,  this.csrAdminPassword, this.getClass().getSimpleName() );
		  	
		  
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

	  }
	  @DataProvider(name = "orgInfo1")
	  public final Object[][] getOrgInfo1() {
	 	 return new Object[][]   {
	 		 {final_msp_user_name_email,common_password, final_msp_user_name_email,common_password, msp_org_id, account_id, "testdata\\images\\1.png",  SpogConstants.SUCCESS_GET_PUT_DELETE, null},
  			 {final_msp_account_admin_email,common_password, final_msp_account_admin_email,common_password, msp_org_id, account_id, "testdata\\images\\2.jpg",  SpogConstants.SUCCESS_GET_PUT_DELETE, null},
	 		 {account_user_email,common_password, account_user_email,common_password, msp_org_id, account_id, "testdata\\images\\2.jpg",  SpogConstants.SUCCESS_GET_PUT_DELETE, null},
	 		 {csrAdminUserName,csrAdminPassword, csrAdminUserName,csrAdminPassword, msp_org_id, account_id, "testdata\\images\\2.jpg",  SpogConstants.SUCCESS_GET_PUT_DELETE, null},
	 		
	 		 
	 		 {final_msp_user_name_email,common_password, final_direct_user_name_email,common_password, msp_org_id, account_id, "testdata\\images\\1.png",  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
	 		 {csrAdminUserName,csrAdminPassword, final_msp_account_admin_email,common_password, msp_org_id, another_account_id, "testdata\\images\\1.png",  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
	 		 {csrAdminUserName,csrAdminPassword, account_user_email,common_password, msp_org_id, another_account_id, "testdata\\images\\1.png",  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
	 		 {csrAdminUserName,csrAdminPassword, another_final_msp_user_name_email,common_password, msp_org_id, another_account_id, "testdata\\images\\1.png",  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
	 		
	 		 {final_msp_user_name_email,common_password, final_msp_user_name_email,common_password, null, account_id, "testdata\\images\\1.png",  SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.ELEMENT_NOT_UUID},
	 		 {final_msp_user_name_email,common_password, final_msp_user_name_email,common_password, "", account_id, "testdata\\images\\1.png",  SpogConstants.RESOURCE_NOT_EXIST, ErrorCode.RESOURCE_NOT_FOUND},
	 		 {final_msp_user_name_email,common_password, final_msp_user_name_email,common_password, "random", account_id, "testdata\\images\\1.png",  SpogConstants.RESOURCE_NOT_EXIST, ErrorCode.RESOURCE_NOT_FOUND_OR_REMOVED},
	 		 {final_msp_user_name_email,common_password, final_msp_user_name_email,common_password, msp_org_id, null, "testdata\\images\\1.png",  SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.ELEMENT_NOT_UUID},
	 		 {final_msp_user_name_email,common_password, final_msp_user_name_email,common_password, msp_org_id, "", "testdata\\images\\1.png",  SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.ELEMENT_NOT_UUID},
	 		 {final_msp_user_name_email,common_password, final_msp_user_name_email,common_password, msp_org_id,  "random", "testdata\\images\\1.png",  SpogConstants.RESOURCE_NOT_EXIST, ErrorCode.RESOURCE_NOT_FOUND_OR_REMOVED},
	 		 
	 		 {final_msp_user_name_email,common_password, this.csrReadOnlyAdminUserName,this.csrReadOnlyAdminPassword, msp_org_id, account_id, "testdata\\images\\1.png",  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
	 	 
	 		 
	 		{this.final_root_msp_user_name_email,common_password, final_root_msp_user_name_email,common_password, this.root_msp_org_id, this.root_msp_direct_org_id, "testdata\\images\\1.png",  SpogConstants.SUCCESS_GET_PUT_DELETE, null},
	 		{this.final_sub_msp1_user_name_email,common_password, final_root_msp_user_name_email,common_password, this.sub_msp1_org_id, this.sub_msp1_account1_id, "testdata\\images\\1.png",  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
	 		{this.final_root_msp_user_name_email,common_password, final_root_msp_account_admin_user_name_email,common_password, this.root_msp_org_id, this.root_msp_direct_org_id, "testdata\\images\\1.png",  SpogConstants.SUCCESS_GET_PUT_DELETE, null},
	 		{this.final_sub_msp1_user_name_email,common_password, final_root_msp_account_admin_user_name_email,common_password, this.sub_msp1_org_id, this.sub_msp1_account1_id, "testdata\\images\\1.png",  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},

	 		{final_sub_msp1_user_name_email,common_password, final_sub_msp1_user_name_email,common_password, this.sub_msp1_org_id, this.sub_msp1_account1_id, "testdata\\images\\1.png",  SpogConstants.SUCCESS_GET_PUT_DELETE, null},
	 		{final_sub_msp2_user_name_email,common_password, final_sub_msp1_user_name_email,common_password, this.sub_msp2_org_id, this.sub_msp2_account1_id, "testdata\\images\\1.png",  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
	 		{final_root_msp_user_name_email,common_password, final_sub_msp1_user_name_email,common_password, this.root_msp_org_id, this.root_msp_direct_org_id, "testdata\\images\\1.png",  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
	 	 
	 		{this.final_sub_msp1_msp_account_user_name_email,common_password, final_sub_msp1_msp_account_user_name_email,common_password, this.sub_msp1_org_id, this.sub_msp1_account1_id, "testdata\\images\\1.png",  SpogConstants.SUCCESS_GET_PUT_DELETE, null},
	 		{final_sub_msp2_user_name_email,common_password, final_sub_msp1_msp_account_user_name_email,common_password, this.sub_msp2_org_id, this.sub_msp2_account1_id, "testdata\\images\\1.png",  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
	 		{final_sub_msp1_user_name_email,common_password, final_sub_msp1_msp_account_user_name_email,common_password, this.sub_msp1_org_id, this.sub_msp1_account2_id, "testdata\\images\\1.png",  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
	 		{final_root_msp_user_name_email,common_password, final_sub_msp1_msp_account_user_name_email,common_password, this.root_msp_org_id, this.root_msp_direct_org_id, "testdata\\images\\1.png",  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
	 	 
	 		{this.final_sub_msp1_msp_account_user_name_email,common_password,this.final_sub_msp1_account1_user_email,common_password, this.sub_msp1_org_id, this.sub_msp1_account1_id, "testdata\\images\\1.png",  SpogConstants.SUCCESS_GET_PUT_DELETE, null},
	 		{final_sub_msp2_user_name_email,common_password, final_sub_msp1_account1_user_email,common_password, this.sub_msp2_org_id, this.sub_msp2_account1_id, "testdata\\images\\1.png",  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
	 		{final_sub_msp1_user_name_email,common_password, final_sub_msp1_account1_user_email,common_password, this.sub_msp1_org_id, this.sub_msp1_account2_id, "testdata\\images\\1.png",  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
	 		{final_root_msp_user_name_email,common_password, final_sub_msp1_account1_user_email,common_password, this.root_msp_org_id, this.root_msp_direct_org_id, "testdata\\images\\1.png",  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
	 
	 		
	 		{this.final_root_msp_direct_org_user_email,common_password, final_root_msp_direct_org_user_email,common_password, this.root_msp_org_id, this.root_msp_direct_org_id, "testdata\\images\\1.png", SpogConstants.SUCCESS_GET_PUT_DELETE, null},
	 		{final_sub_msp1_user_name_email,common_password, final_root_msp_direct_org_user_email,common_password, this.sub_msp1_org_id, this.sub_msp1_account2_id, "testdata\\images\\1.png",  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
	 	 };
	 	}
	  
	  @Test(dataProvider = "orgInfo1", enabled=true)
	  /**
	   * 1. msp admin could delete pic for msp account
	   * 2. msp account admin could delete pic for msp account
	   * 3. account admin could delete pic for msp account
	   * 4. csr admin could delete pic for msp account
	   * 
	   * 5. direct admin could not delete pic for msp account
	   * 12. msp account admin could not delete pic for not masterd msp account
	   * 13.  account admin could not delete pic for not other account's msp account
	   * 14. msp admin could not delete pic for other org's msp account
	   * 
	   * 6. set parent id as null in url, report 400
	   * 7. set parent id as "" in url, report 404
	   * 8. set parent id as random string in url, report 404
	   * 10. set child id as null in url, report 400
	   * 9. set child id as "" in url, report 404
	   * 11. set child id as random string in url, report 404
	   * 
	   * 
	   * root msp admin could delete pic for its direct org
	   * root msp admin could not delete pic for sub msp1's account
	   * root msp msp account admin could delete pic for its direct org
	   * root msp msp account admin could not delete pic for sub msp1's account
	   * 
	   * sub msp1 admin could delete pic for its account1
	   * sub msp1 admin  could not delete pic for sub msp2's account
	   * sub msp1 admin  could not delete pic for root msp's direct org
	   * sub msp1 msp account admin could delete pic for its account1
	   * sub msp1 msp account  could not delete pic for sub msp2's account
	   * sub msp1 msp account  could not delete pic for sub msp1 account2
	   * sub msp1 msp account   could not delete pic for root msp's direct org
	   * 
	   * sub msp1 account1 admin could delete pic for its account1
	   * sub msp1 account1 admin   could not delete pic for sub msp2's account
	   *sub msp1 account1 admin  could not delete pic for sub msp1 account2
	   * sub msp1 account1 admin  could not delete pic for root msp's direct org
	   * 
	   * root msp direct org's admin could delete its org's pic
	   * root msp direct org's admin could not delete sub msp1 account1's picture
	   * 
	   * @param userName
	   * @param password
	   * @param parentId
	   * @param childId
	   * @param expectedStatusCode
	   * @param expectedErrorCode
	   */
	  
	  public void deleteAccountId(String uploadUserName, String uploadUserPassword, String deleteUserName, String deleteUserPassword,
			  String parentId, String childId, String picPath,   int expectedStatusCode, String expectedErrorCode) {
		  
		  spogServer.userLogin(uploadUserName, uploadUserPassword);
		  test.log(LogStatus.INFO,"upload picture for account ");

		  if((parentId!=null) && (!parentId.equalsIgnoreCase("")) && (!parentId.equalsIgnoreCase("random")) &&
			 (childId!=null) && (!childId.equalsIgnoreCase(""))	 && (!childId.equalsIgnoreCase("random")) ) {
			  spogServer.uploadPictureForAccount(parentId, childId, picPath, test);
		  }
	  
		  spogServer.userLogin(deleteUserName, deleteUserPassword);
		  test.log(LogStatus.INFO,"delete picture for account ");
		  org4SpogServer.setToken(spogServer.getJWTToken());
		  if((parentId!=null) && parentId.equalsIgnoreCase("random")){
			  parentId =UUID.randomUUID().toString();
		  }
		  if((childId!=null) && childId.equalsIgnoreCase("random")){
			  childId =UUID.randomUUID().toString();
		  }
		  org4SpogServer.deleteAccountPictureWithCheck(parentId, childId,spogServer.getJWTToken() , expectedStatusCode, expectedErrorCode);
	  }
	  
	  @Test
	  public void deleteAccountIdWithoutLogin(){
		  
		  test.log(LogStatus.INFO,"delete picture without login ");
		  org4SpogServer.deleteAccountPictureWithoutLoginWithCheck(msp_org_id, account_id);
		  test.log(LogStatus.INFO,"delete picture with invalid token ");
		  org4SpogServer.deleteAccountPictureWithCheck(msp_org_id, account_id, UUID.randomUUID().toString(), SpogConstants.NOT_LOGGED_IN, ErrorCode.AUTHORIZATION_HEADER_BLANK);
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
	    rep.endTest(test);    
	}
	
}
