package api.users.picture;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import InvokerServer.Org4SPOGServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import invoker.SiteTestHelper;
import io.restassured.response.Response;

	
public class DeleteUsersPictureTest  extends base.prepare.PrepareOrgInfo {
	
	@Parameters({ "pmfKey"})
	 	public DeleteUsersPictureTest(String pmfKey) {
		super(pmfKey);
		// TODO Auto-generated constructor stub
	}


		private SPOGServer spogServer;
	 	private UserSpogServer userSpogServer;
		private String csrAdminUserName;
		private String csrAdminPassword;
		  private String csrReadOnlyAdminUserName;
		  private String csrReadOnlyAdminPassword;
		private ExtentTest test;

	  private String postfix_email = "@arcserve.com";
	  private String common_password = "Welcome*02";
	  
	  private String prefix_direct = "spogqa_shuo_direct";
	  private String direct_org_name = prefix_direct + "_org";
	  private String direct_user_name = prefix_direct + "_admin";
	  private String direct_user_name_email = direct_user_name + postfix_email;
	  private String direct_user_first_name = direct_user_name + "_first_name";
	  private String direct_user_last_name = direct_user_name + "_last_name";
	  
	  private String prefix_msp = "spogqa_shuo_msp";
	  private String msp_org_name = prefix_msp + "_org";
	  private String msp_user_name = prefix_msp + "_admin";
	  private String msp_user_name_email = msp_user_name + postfix_email;
	  private String msp_user_first_name = msp_user_name + "_first_name";
	  private String msp_user_last_name = msp_user_name + "_last_name";
	  
	  private String prefix_msp_account = "spogqa_shuo_msp_account";
	  private String msp_account_admin_name = prefix_msp_account + "_admin";
	  private String msp_account_admin_email =msp_account_admin_name + postfix_email;
	  private String msp_account_admin_first_name = msp_account_admin_name + "_first_name";
	  private String msp_account_admin_last_name = msp_account_admin_name + "_last_name";
	  private String msp_account_admin_id;
	  private String final_msp_account_admin_email;
	  
	  //this is for update portal, each testng class is taken as BQ set
/*	  private SQLServerDb bqdb1;
	  public int Nooftest;
	  private long creationTime;
	  private String BQName=null;
	  private String runningMachine;
	  private testcasescount count1;
	  private String buildVersion;
		private ExtentReports rep;
		
		*/
	private Org4SPOGServer org4SpogServer;
	private String  org_model_prefix=this.getClass().getSimpleName();
	  
	  
	  @BeforeClass
	  @Parameters({ "baseURI", "port", "logFolder", "csrAdminUserName", "csrAdminPassword", "buildVersion", "csrReadOnlyAdminUserName","csrReadOnlyAdminPassword"})
	  public void beforeClass(String baseURI, String port, String logFolder, String adminUserName, String adminPassword ,  String buildVersion, String csrROAdminUserName, String csrROPwd ) throws UnknownHostException {
		
			  
		  rep = ExtentManager.getInstance("DeleteUsersPictureTest",logFolder);
			org4SpogServer = new Org4SPOGServer(baseURI, port);
		  spogServer = new SPOGServer(baseURI, port);
		  userSpogServer = new UserSpogServer(baseURI, port);
		  this.csrAdminUserName = adminUserName;
		  this.csrAdminPassword = adminPassword;
		  this.csrReadOnlyAdminUserName = csrROAdminUserName;
		  this.csrReadOnlyAdminPassword = csrROPwd;
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
	  	prepare(baseURI, port, logFolder, this.csrAdminUserName,  this.csrAdminPassword, this.getClass().getSimpleName() );
	  }
	
		
	  
	  


	  @DataProvider(name = "organizationAndUserInfo")
		public final Object[][] getOrganizationAndUserInfo() {
			return new Object[][] { {direct_org_name, SpogConstants.DIRECT_ORG, direct_user_name_email, common_password,direct_user_first_name, 
										direct_user_last_name, SpogConstants.DIRECT_ADMIN, "testdata\\images\\1.PNG" }, 
									{msp_org_name, SpogConstants.MSP_ORG, msp_user_name_email, common_password,msp_user_first_name, 
											msp_user_last_name, SpogConstants.MSP_ADMIN, "testdata\\images\\2.jpg" },
			};
		}
	  
	  @Test(dataProvider = "organizationAndUserInfo", priority=0, enabled=true)
	  /**
	   * 1. direct admin can delete picture in its org
	   * 2. MSP admin can delete picture in its org
	   * 3. MSP admin can delete picture in its sub org
	   * 4. account admin can delete picture in its  org
	   * 
	   * 13. MSP account admin can delete picuture for mastered org's direct admin
	   * 19. msp admin can delete msp account admin's picture
	   * 25. msp account admin can delete msp account admin's picture
	   * 
	   * @author shuo.zhang
	   * @param organizationName
	   * @param organizationType
	   * @param userEmail
	   * @param userPassword
	   * @param userFirstName
	   * @param userLastName
	   * @param role_id
	   * @param pictures
	   */
	  public void adminDeleteUsersPicture(String organizationName, 
		  String organizationType,
		  String userEmail, String userPassword, String userFirstName, String userLastName, String role_id, String picturePath)
	  {
		  test = rep.startTest("adminDeleteUserPictures");
		  test.assignAuthor("shuo.zhang");
		  spogServer.errorHandle.printInfoMessageInDebugFile("/****************adminDeleteUserPictures**************/");
		  spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
	  
		  String finalOrgName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName+ org_model_prefix;
		  //create organization
		  test.log(LogStatus.INFO,"create organization");
		  String organization_id = spogServer.CreateOrganizationWithCheck(finalOrgName, organizationType, null, null, null, null, test);
		 
		  //create create user
		  test.log(LogStatus.INFO,"create user");
		  String finalUserEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;
		  String user_id = spogServer.createUserAndCheck(finalUserEmail, userPassword, userFirstName, userLastName, role_id, organization_id, test);
		  
		  //admin user login
		  test.log(LogStatus.INFO,"admin user login");
		  spogServer.userLogin(finalUserEmail, userPassword, test);
		  String adminToken=spogServer.getJWTToken();
		  userSpogServer.setToken(adminToken);
		  
		  //create another user
		  test.log(LogStatus.INFO,"create another admin user");
		  finalUserEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;
		  String sec_user_id = spogServer.createUserAndCheck(finalUserEmail, userPassword, userFirstName, userLastName, role_id, organization_id, test);
		  
		  //post user picture
		  test.log(LogStatus.INFO,"post first user picture");
		  userSpogServer.uploadPictureByUserID(user_id, picturePath, test);
		  
		  //post user picture
		  test.log(LogStatus.INFO,"post second user picture");
		  userSpogServer.uploadPictureByUserID(sec_user_id, picturePath, test);
		  
	  
		  //delete user picture
		  test.log(LogStatus.INFO,"delete first user picture");
		  userSpogServer.deleteUsersPictureWithCheck(user_id, adminToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		  
		  //delete user picture
		  test.log(LogStatus.INFO,"delete second user picture");
		  userSpogServer.deleteUsersPictureWithCheck(sec_user_id, adminToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		  
		  if(role_id.equalsIgnoreCase(SpogConstants.MSP_ADMIN)){
			  test.log(LogStatus.INFO,"create sub organization");			  
			  String sub_organization_id = spogServer.createAccountWithCheck(organization_id, "sub_"+ finalOrgName , "", test);
			  
			  ArrayList<String> userIdList= new ArrayList<String>();
			  ArrayList<String> userEmailList= new ArrayList<String>();
			  for(int i=0; i<3; i++){
				  //create create user
				  test.log(LogStatus.INFO,"create account user");
				  String sub_finalUserEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;
				  userEmailList.add(sub_finalUserEmail);
				  String account_user_id  = spogServer.createUserAndCheck(sub_finalUserEmail ,userPassword, userFirstName, userLastName, SpogConstants.DIRECT_ADMIN, sub_organization_id, test);	
				  userIdList.add(account_user_id);			  
			  }
			 
			  //post user picture
			  test.log(LogStatus.INFO,"msp admin post account admin picture");
			  userSpogServer.uploadPictureByUserID(userIdList.get(0), picturePath, test);
			  
			  //delete user picture
			  test.log(LogStatus.INFO,"msp admin delete account admin picture");
			  userSpogServer.deleteUsersPictureWithCheck(userIdList.get(0), adminToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		  
			  //account admin login
			  test.log(LogStatus.INFO,"account admin user login");
			  spogServer.userLogin(userEmailList.get(1), userPassword, test);
			  adminToken=spogServer.getJWTToken();
			  userSpogServer.setToken(adminToken);
			  
			  for(int i=1; i<3;i++){
				  test.log(LogStatus.INFO,"account admin post account admin picture");
				  userSpogServer.uploadPictureByUserID(userIdList.get(i), picturePath, test);
				  //delete user picture
				  test.log(LogStatus.INFO,"account admin delete account admin picture");
				  userSpogServer.deleteUsersPictureWithCheck(userIdList.get(i), adminToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			  }
	 
 
			  //msp admin login
			  test.log(LogStatus.INFO,"admin user login");
			  spogServer.userLogin(finalUserEmail, userPassword, test);
			  userSpogServer.setToken(spogServer.getJWTToken()); 
			  
			  //create a msp account admin
			  test.log(LogStatus.INFO,"create a msp account admin under msp org");
			  String prefix = RandomStringUtils.randomAlphanumeric(8);
			  final_msp_account_admin_email = prefix + this.msp_account_admin_email;
			  msp_account_admin_id = spogServer.createUserAndCheck(final_msp_account_admin_email, common_password, prefix + msp_account_admin_first_name, prefix + msp_account_admin_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, organization_id, test);
			  
			  test.log(LogStatus.INFO,"assign account to msp account admin");
			  String[] mspAccountAdmins = new String []{msp_account_admin_id};
			  userSpogServer.assignMspAccountAdmins(organization_id, sub_organization_id, mspAccountAdmins , spogServer.getJWTToken()); 
			  
			  //msp admin post msp account admin picture
			  test.log(LogStatus.INFO,"msp admin post msp account admin picture");
			  userSpogServer.uploadPictureByUserID(msp_account_admin_id, picturePath, test);
			  
			  //msp admin delete msp account admin picture
			  test.log(LogStatus.INFO,"msp admin delete msp account admin picture");
			  userSpogServer.deleteUsersPictureWithCheck(msp_account_admin_id, spogServer.getJWTToken(), SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			  
			  
		    
			  
			  test.log(LogStatus.INFO,"msp account admin  login");
			  spogServer.userLogin(final_msp_account_admin_email, common_password, test);
			  userSpogServer.setToken(spogServer.getJWTToken()); 
			  
			  for(int i=0; i<3;i++){
				  test.log(LogStatus.INFO,"account admin post account admin picture");
				  userSpogServer.uploadPictureByUserID(userIdList.get(i), picturePath, test);
				  //delete user picture
				  test.log(LogStatus.INFO,"account admin delete account admin picture");
				  userSpogServer.deleteUsersPictureWithCheck(userIdList.get(i), adminToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			  }
			  
			  //msp account admin post msp account admin picture
			  test.log(LogStatus.INFO,"msp account admin post msp account admin picture");
			  userSpogServer.uploadPictureByUserID(msp_account_admin_id, picturePath, test);
			  
			  //msp account admin delete msp account admin picture
			  test.log(LogStatus.INFO,"msp account admin delete msp account admin picture");
			  userSpogServer.deleteUsersPictureWithCheck(msp_account_admin_id, spogServer.getJWTToken(), SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			  
			  
			  
			  //msp admin login
			  test.log(LogStatus.INFO,"admin user login");
			  spogServer.userLogin(finalUserEmail, userPassword, test);
			  userSpogServer.setToken(spogServer.getJWTToken()); 
			
			  //delete account
			  test.log(LogStatus.INFO,"delete sub organization");
			  spogServer.DeleteOrganizationWithCheck(sub_organization_id, test);
		  }
		  
		  spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		  org4SpogServer.setToken(spogServer.getJWTToken()); 
		  //delete organization
		  test.log(LogStatus.INFO,"delete organization");
		//  spogServer.DeleteOrganizationWithCheck(organization_id, test);
		  org4SpogServer.destroyOrganization(organization_id , test);
		
	  }
	  
	  @Test(enabled=true)
	  /**
	   * 5. direct admin cannot delete msp admin picture , report 403
	   * 20. direct admin cannot delete account admin picture , report 403
	   * 6. MSP admin cannot delete direct admin's picture, report 403
	   * 7. account admin cannot delete direct admin's picture, report 403
	   * 21. account admin cannot delete msp admin's picture, report 403
	   * 8. CSR admin can delete direct admin's picture 
	   * 22. CSR admin can delete msp admin's picture 
	   * 23. CSR admin can delete msp account admin's picture 
	   * 
	   * 14. MSP account admin can not delete picuture for not mastered org's direct admin
	   * 15. MSP account admin can not delete picuture for its org's msp admin
	   * 16. MSP account admin can not delete picuture for direct org's admin
	   * 17. direct admin cannot delete msp account admin's picture
	   * 18. account admin cannot delete msp account admin's picture
	   * 19. msp admin can delete msp account admin's picture
	   */
	  public void adminCannotDeletePicInOtherOrg(){
		  
		  test = rep.startTest("csrAdminCreateAdmin");
		  test.assignAuthor("shuo.zhang");
		  spogServer.errorHandle.printInfoMessageInDebugFile("/****************adminCannotDeletePicInOtherOrg**************/");
		  spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		  String finalOrgName = RandomStringUtils.randomAlphanumeric(8) + "_" + direct_org_name+ org_model_prefix;
		  //create direct organization
		  test.log(LogStatus.INFO,"create direct organization");
		  String direct_organization_id = spogServer.CreateOrganizationWithCheck(finalOrgName, SpogConstants.DIRECT_ORG, null, null, null, null, test);

		  //create create user
		  test.log(LogStatus.INFO,"create direct user");
		  String direct_finalUserEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + direct_user_name_email;
		  String direct_user_id = spogServer.createUserAndCheck(direct_finalUserEmail, common_password, direct_user_first_name, direct_user_last_name, SpogConstants.DIRECT_ADMIN, direct_organization_id, test);
		  
		  //create msp organization
		  test.log(LogStatus.INFO,"create msp organization");
		  String msp_organization_id = spogServer.CreateOrganizationWithCheck(finalOrgName, SpogConstants.MSP_ORG, null, null, null, null, test);

		  //create create user
		  test.log(LogStatus.INFO,"create msp user");
		  String msp_finalUserEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + msp_user_name_email;
		  String msp_user_id = spogServer.createUserAndCheck(msp_finalUserEmail, common_password, msp_user_first_name, msp_user_last_name, SpogConstants.MSP_ADMIN, msp_organization_id, test);
		  
		  
		  test.log(LogStatus.INFO,"create sub organization");			  
		  String sub_organization_id = spogServer.createAccountWithCheck(msp_organization_id, "sub_"+ finalOrgName , "", test);
		  String sub_finalUserEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + msp_user_name_email;
		  String account_user_id  = spogServer.createUserAndCheck(sub_finalUserEmail ,common_password, msp_user_first_name, msp_user_last_name, SpogConstants.DIRECT_ADMIN, sub_organization_id, test);
		  
		  //create a msp account admin
		  test.log(LogStatus.INFO,"create a msp account admin under msp org");
		  String prefix = RandomStringUtils.randomAlphanumeric(8);
		  final_msp_account_admin_email = prefix + this.msp_account_admin_email;
		  msp_account_admin_id = spogServer.createUserAndCheck(final_msp_account_admin_email, common_password, prefix + msp_account_admin_first_name, prefix + msp_account_admin_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, msp_organization_id, test);
		
		  
		  userSpogServer.setToken(spogServer.getJWTToken());
		  test.log(LogStatus.INFO,"upload pictures for all users");
		  userSpogServer.uploadPictureByUserID(direct_user_id, "testdata\\images\\3.gif", test);
		  userSpogServer.uploadPictureByUserID(msp_user_id, "testdata\\images\\3.gif", test);
		  userSpogServer.uploadPictureByUserID(account_user_id, "testdata\\images\\3.gif", test);
		  userSpogServer.uploadPictureByUserID(msp_account_admin_id, "testdata\\images\\3.gif", test);
		  
		  spogServer.userLogin(this.csrReadOnlyAdminUserName, this.csrReadOnlyAdminPassword);
		  userSpogServer.setToken(spogServer.getJWTToken());
		  String csr_read_only_id = spogServer.GetLoggedinUser_UserID();
		  userSpogServer.uploadPictureByUserID(csr_read_only_id, "testdata\\images\\3.gif", test);
		  
		  //direct admin can't delete user's picture in other org
		  test.log(LogStatus.INFO,"direct admin login");
		  spogServer.userLogin(direct_finalUserEmail, common_password);
		  String adminToken= spogServer.getJWTToken();
		  userSpogServer.setToken(adminToken);
		  test.log(LogStatus.INFO,"direct admin can't delete msp admin and sub admin picture");
		  userSpogServer.deleteUsersPictureWithCheck(msp_user_id, adminToken, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION, test);
		  userSpogServer.deleteUsersPictureWithCheck(account_user_id, adminToken, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION, test);
		  userSpogServer.deleteUsersPictureWithCheck(msp_account_admin_id, adminToken, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION, test);
			  
		  //msp admin can't delete user's picture in other org
		  test.log(LogStatus.INFO,"msp admin login");
		  spogServer.userLogin(msp_finalUserEmail, common_password);
		  adminToken= spogServer.getJWTToken();
		  userSpogServer.setToken(adminToken);
		  test.log(LogStatus.INFO,"msp admin can't delete direct admin picture");
		  userSpogServer.deleteUsersPictureWithCheck(direct_user_id, adminToken, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION, test);
		  
		  //sub org admin can't delete user's picture in other org
		  test.log(LogStatus.INFO,"sub-org admin login");
		  spogServer.userLogin(sub_finalUserEmail, common_password);
		  adminToken= spogServer.getJWTToken();
		  userSpogServer.setToken(adminToken);
		  test.log(LogStatus.INFO,"sub-org admin can't delete direct admin and msp admin picture");
		  userSpogServer.deleteUsersPictureWithCheck(msp_user_id, adminToken, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION, test);
		  userSpogServer.deleteUsersPictureWithCheck(direct_user_id, adminToken, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION, test);
		  userSpogServer.deleteUsersPictureWithCheck(msp_account_admin_id, adminToken, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION, test);
		  
		  //msp account admin can't delete user's picture in other org
		  test.log(LogStatus.INFO,"msp account admin login");
		  spogServer.userLogin(final_msp_account_admin_email, common_password);
		  adminToken= spogServer.getJWTToken();
		  userSpogServer.setToken(adminToken);
		  test.log(LogStatus.INFO,"msp account admin can't delete direct admin, msp admin, not mastered account's admin picture");
		  userSpogServer.deleteUsersPictureWithCheck(msp_user_id, adminToken, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION, test);
		  userSpogServer.deleteUsersPictureWithCheck(direct_user_id, adminToken, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION, test);
		  userSpogServer.deleteUsersPictureWithCheck(account_user_id, adminToken, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION, test);
		  
		  
		  test.log(LogStatus.INFO,"csr admin login");
		  spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		  adminToken= spogServer.getJWTToken();
		  String csr_admin_id = spogServer.GetLoggedinUser_UserID();
		  test.log(LogStatus.INFO,"csr admin can delete picture for all users");
		  userSpogServer.deleteUsersPictureWithCheck(direct_user_id, adminToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		  userSpogServer.deleteUsersPictureWithCheck(msp_user_id, adminToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		  userSpogServer.deleteUsersPictureWithCheck(account_user_id, adminToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		  userSpogServer.deleteUsersPictureWithCheck(msp_account_admin_id, adminToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		  userSpogServer.deleteUsersPictureWithCheck(csr_read_only_id, adminToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		  
		  test.log(LogStatus.INFO,"csr readonly admin login");
		  spogServer.userLogin(this.csrReadOnlyAdminUserName, this.csrReadOnlyAdminPassword, test);
		  adminToken= spogServer.getJWTToken();
		  test.log(LogStatus.INFO,"csr readonly admin can not delete picture for all users");
		  userSpogServer.deleteUsersPictureWithCheck(direct_user_id, adminToken, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION, test);
		  userSpogServer.deleteUsersPictureWithCheck(msp_user_id, adminToken, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION, test);
		  userSpogServer.deleteUsersPictureWithCheck(account_user_id, adminToken, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION, test);
		  userSpogServer.deleteUsersPictureWithCheck(msp_account_admin_id, adminToken, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION, test);
		  userSpogServer.deleteUsersPictureWithCheck(csr_admin_id, adminToken, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION, test);
		  test.log(LogStatus.INFO,"csr readonly admin can delete picture for all users");
		  userSpogServer.setToken(adminToken);
		  userSpogServer.uploadPictureByUserID(csr_read_only_id, "testdata\\images\\3.gif", test);
		  userSpogServer.deleteUsersPictureWithCheck(csr_read_only_id, adminToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		  
		  //delete organization
		  test.log(LogStatus.INFO,"delete organization");
		  spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		  adminToken= spogServer.getJWTToken();
		  org4SpogServer.setToken(adminToken);
		  org4SpogServer.destroyOrganization(direct_organization_id , test);
		  org4SpogServer.destroyOrganization(msp_organization_id , test);
		  
	  }
	  
	  @Test(enabled=true)
	  /**
	   * 9. admin can not delete picture without login, report 401
	   * 10. admin can not delete picture with non-existing user id, report 404
	   * 11. admin can not delete picture with empty user id, report 400
	   * 12. admin can not delete picture with invalid token, report 401
	   */
	  public void errorHandlingTest(){
		  
		  test = rep.startTest("csrAdminCreateAdmin");
		  test.assignAuthor("shuo.zhang");
		  spogServer.errorHandle.printInfoMessageInDebugFile("/****************errorHandlingTest**************/");
		 
		  test.log(LogStatus.INFO,"csr admin login");
		  spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		  String adminToken= spogServer.getJWTToken();
		  
		  test.log(LogStatus.INFO,"csr admin delete picture without login");
		  userSpogServer.deleteUsersPictureWithoutLogin(spogServer.getUUId(),test);
		  
		  test.log(LogStatus.INFO,"csr admin delete picture with non-existing user id");
		  userSpogServer.deleteUsersPictureWithCheck(UUID.randomUUID().toString(), adminToken, SpogConstants.RESOURCE_NOT_EXIST, ErrorCode.USER_NOT_EXIST_OR_INACTIVE, test);
		  
		  userSpogServer.deleteUsersPictureWithCheck("", adminToken, SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.ELEMENT_NOT_UUID, test);
		  userSpogServer.deleteUsersPictureWithCheck(spogServer.getUUId(), UUID.randomUUID().toString(), SpogConstants.NOT_LOGGED_IN, ErrorCode.AUTHORIZATION_HEADER_BLANK, test);
	  }

	  @DataProvider(name = "organizationAndUserInfo2")
		public final Object[][] getOrganizationAndUserInfo2() {
			return new Object[][] { {this.final_root_msp_user_name_email, this.root_msp_user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null}, 
			 {this.final_root_msp_user_name_email, root_msp_account_admin_user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null}, 
				{this.final_root_msp_user_name_email, this.root_msp_direct_org_user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null}, 
				{this.final_root_msp_user_name_email, this.sub_msp1_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 
				{this.final_root_msp_user_name_email, this.sub_msp1_msp_account_admin_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{this.final_root_msp_user_name_email, this.sub_msp1_account1_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				
				{this.final_root_msp_account_admin_user_name_email, this.root_msp_account_admin_user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null}, 
				{this.final_root_msp_account_admin_user_name_email, this.root_msp_direct_org_user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null}, 
				{this.final_root_msp_account_admin_user_name_email, this.root_msp_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 
				{this.final_root_msp_account_admin_user_name_email, this.sub_msp1_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 
				{this.final_root_msp_account_admin_user_name_email, this.sub_msp1_msp_account_admin_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 
				{this.final_root_msp_account_admin_user_name_email, this.sub_msp1_account1_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				
				{this.final_root_msp_direct_org_user_email, this.root_msp_direct_org_user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null}, 
				{this.final_root_msp_direct_org_user_email, this.root_msp_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 
				{this.final_root_msp_direct_org_user_email, this.sub_msp1_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{this.final_root_msp_direct_org_user_email, this.sub_msp1_account1_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				
				{this.final_sub_msp1_user_name_email, this.sub_msp1_user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null}, 
				{this.final_sub_msp1_user_name_email, this.sub_msp1_msp_account_admin_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null}, 
				{this.final_sub_msp1_user_name_email, this.sub_msp1_account1_user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				{this.final_sub_msp1_user_name_email, this.root_msp_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 
				{this.final_sub_msp1_user_name_email, this.root_msp_direct_org_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				
				{this.final_sub_msp1_msp_account_user_name_email, this.sub_msp1_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 
				{this.final_sub_msp1_msp_account_user_name_email, this.sub_msp1_msp_account_admin_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null}, 
				{this.final_sub_msp1_msp_account_user_name_email, this.sub_msp1_account1_user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				{this.final_sub_msp1_msp_account_user_name_email, this.root_msp_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 
				{this.final_sub_msp1_msp_account_user_name_email, this.root_msp_direct_org_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
		
				{this.final_sub_msp1_account1_user_email, this.sub_msp1_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 
				{this.final_sub_msp1_account1_user_email, this.sub_msp1_msp_account_admin_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 
				{this.final_sub_msp1_account1_user_email, this.sub_msp1_account1_user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				{this.final_sub_msp1_account1_user_email, this.root_msp_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 
				{this.final_sub_msp1_account1_user_email, this.root_msp_direct_org_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				/* {this.final_root_msp_account_admin_user_name_email},
				 {this.final_root_msp_direct_org_user_email},
				 {this.final_sub_msp1_msp_account_user_name_email},
				 {this.final_sub_msp1_user_name_email},
				 {this.final_sub_msp1_account1_user_email},*/
							 
				};
		}
	  @Test(dataProvider = "organizationAndUserInfo2")
	  /**
	   * 1. root msp admin could delete its own picture
	   * root msp admin could delete root msp account admin's picture
	   * 2. root msp admin could delete its direct org admin's picture
	   * 3. root msp admin could not delete sub msp1 admin's picture
	   * root msp admin could not delete sub msp1 account admin's picture
	   * 4. root msp admin could not delete sub msp1 account1's admin's picture
	   * 
	   * 5. root msp account admin could delete its own picture
	   * 6. root msp account admin could delete its direct org admin's picture
	   * root msp account admin could not delete root msp admin's picture
	   * 7. root msp account admin could not delete sub msp1 admin's picture
	   * root msp account admin could not delete sub msp1 account admin's picture
	   * 8. root msp account admin could not delete sub msp1 account1 admin's picture
	   * 
	   * 
	   * 9. root msp's direct org account admin's could delete its own picture
	   * 10. root msp's direct org account admin's could not delete root msp admin's picture
	   * 11. root msp's direct org account admin's could not delete sub msp1 admin's picture
	   * 12. root msp's direct org account admin's could not delete account1 admin's picture
	   * 
	   * 13. sub msp1 admin could delete its own picture
	   * 14.  sub msp1 admin could  delete sub msp1 account admin's picture
	   * 15.  sub msp1 admin could  delete account1 admin's picture
	   * 16.  sub msp1 admin  could not delete root msp admin's picture
	   * 17.  sub msp1 admin  could not delete root direct org's admin's picture
	   * 
	   * 18. sub msp1 account admin could delete its own picture
	   * 19.  sub msp1 account admin could not  delete sub msp1  admin's picture
	   * 20.  sub msp1 account admin could  delete account1 admin's picture
	   * 21.  sub msp1 account admin  could not delete root msp admin's picture
	   * 22.  sub msp1 accountadmin  could not delete root direct org's admin's picture
	   * 
	   * 
	   * @param adminEmail
	   * @param user_id
	   * @param expectedStatusCode
	   * @param errorCode
	   */
	  public void deleteUserPicture4RootMSP(String adminEmail, String user_id, int expectedStatusCode , String errorCode){
		  
		  test = rep.startTest("deleteUserPicture4RootMSP");
		  test.assignAuthor("shuo.zhang");
		  spogServer.errorHandle.printInfoMessageInDebugFile("/****************deleteUserPicture4RootMSP**************/");
			  
		  spogServer.userLogin(adminEmail, common_password);	  	
		  userSpogServer.setToken(spogServer.getJWTToken());
		  if(expectedStatusCode== SpogConstants.SUCCESS_GET_PUT_DELETE)
			  userSpogServer.uploadPictureByUserID(user_id, "testdata\\images\\3.gif", test);
		  userSpogServer.deleteUsersPictureWithCheck(user_id, spogServer.getJWTToken(),expectedStatusCode, errorCode, test);
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


		@AfterClass
		public void aftertest() {

		    test.log(LogStatus.INFO, "The total test cases passed are "+count1.getpassedcount());
		    test.log(LogStatus.INFO, "the total test cases failed are "+count1.getfailedcount());
		    rep.flush();
		    updatebd() ;
		}

		
		public void updatebd() {
		  try {
		        if(count1.getfailedcount()>0) {
		              Nooftest=count1.getpassedcount()+count1.getfailedcount()+count1.getskippedcount();
		              bqdb1.updateTable(this.BQName, this.runningMachine, this.buildVersion, String.valueOf(Nooftest), Integer.toString(count1.getpassedcount()), Integer.toString(count1.getfailedcount()), String.valueOf(count1.getskippedcount()), count1.getcreationtime(), "Failed");
		        }else {
		              Nooftest=count1.getpassedcount()+count1.getfailedcount()+count1.getskippedcount();
		              bqdb1.updateTable(this.BQName, this.runningMachine, this.buildVersion, String.valueOf(Nooftest), Integer.toString(count1.getpassedcount()), Integer.toString(count1.getfailedcount()), String.valueOf(count1.getskippedcount()), count1.getcreationtime(), "Passed");
		        }
		  }catch (ClientProtocolException e) {
		        // TODO Auto-generated catch block
		        e.printStackTrace();
		  }catch (IOException e) {
		        // TODO Auto-generated catch block
		        e.printStackTrace();
		  }
		}
	  
}
