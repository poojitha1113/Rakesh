package api.users.picture;

import org.testng.annotations.Test;

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
import io.restassured.response.ResponseBody;

public class DeleteLoggedInUserPictureTest  extends base.prepare.PrepareOrgInfo{
	
	@Parameters({ "pmfKey"})
	public DeleteLoggedInUserPictureTest(String pmfKey) {
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
	  private String  org_model_prefix=this.getClass().getSimpleName();
	  //this is for update portal, each testng class is taken as BQ set
/*	private SQLServerDb bqdb1;
	public int Nooftest;
	private long creationTime;
	private String BQName=null;
	private String runningMachine;
	private testcasescount count1;
	private String buildVersion;
	private ExtentReports rep;
	*/
	
	private Org4SPOGServer org4SpogServer;
	
	  @BeforeClass
	  @Parameters({ "baseURI", "port", "logFolder", "csrAdminUserName", "csrAdminPassword", "buildVersion", "csrReadOnlyAdminUserName","csrReadOnlyAdminPassword"})
	  public void beforeClass(String baseURI, String port, String logFolder, String adminUserName, String adminPassword ,  String buildVersion, String csrROAdminUserName, String csrROPwd ) throws UnknownHostException {
			  
		  rep = ExtentManager.getInstance("DeleteLogginUserPictureTest",logFolder);
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
	  
	  /**
	   * 1. direct org's loggin user could delete its picture
	   * 2. msp org's loggin user could delete its picture
	   * 3. account org's loggin user could delete its picture
	   * 4. user with invalid token can not delete its picture
	   * 5. user without loggin can not delete its picture
	   * 6. csr admin could delete its own picture
	   * @author shuo.zhang
	   * @param organizationName
	   * @param organizationType
	   * @param userEmail
	   * @param userPassword
	   * @param userFirstName
	   * @param userLastName
	   * @param role_id
	   * @param picturePath
	   */
	  @Test(dataProvider = "organizationAndUserInfo", priority=0, enabled= true)
	  public void deleteLogginUserPicture(String organizationName, 
			  String organizationType, String userEmail, String userPassword, String userFirstName, String userLastName, String role_id, String picturePath)
		  {
			  test = rep.startTest("deleteLogginUserPicture");
			  test.assignAuthor("shuo.zhang");
			  spogServer.errorHandle.printInfoMessageInDebugFile("/****************deleteLogginUserPicture**************/");
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
			  
			  //post user picture
			  test.log(LogStatus.INFO,"post first user picture");
			  userSpogServer.uploadPictureForLoginUser(picturePath, test);
			  
			  userSpogServer.deleteLoggedInUserPictureWithCheck(UUID.randomUUID().toString(), SpogConstants.NOT_LOGGED_IN, ErrorCode.AUTHORIZATION_HEADER_BLANK, test);
			  userSpogServer.deleteLoggedInUserPictureWithoutLogin(test);
			  			  
			  //delete user picture
			  test.log(LogStatus.INFO,"delete user picture");
			  userSpogServer.deleteLoggedInUserPictureWithCheck(adminToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			  
	
			  
			  if(role_id.equalsIgnoreCase(SpogConstants.MSP_ADMIN)){		
					
				  test.log(LogStatus.INFO,"create sub organization");			  
				  String sub_organization_id = spogServer.createAccountWithCheck(organization_id, "sub_"+ finalOrgName , "", test);
				  //create create user
				  test.log(LogStatus.INFO,"create account user");
				  String sub_finalUserEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;
				  String account_user_id  = spogServer.createUserAndCheck(sub_finalUserEmail ,userPassword, userFirstName, userLastName, SpogConstants.DIRECT_ADMIN, sub_organization_id, test);	
							  
				  			  
				  //account admin login
				  test.log(LogStatus.INFO,"account admin user login");
				  spogServer.userLogin(sub_finalUserEmail, userPassword, test);
				  adminToken=spogServer.getJWTToken();
				  userSpogServer.setToken(adminToken);
				  //post account admin picture
				  test.log(LogStatus.INFO,"account admin post account admin picture");
				  userSpogServer.uploadPictureForLoginUser(picturePath, test);
				  
				  			  
				  //delete user picture
				  test.log(LogStatus.INFO,"account admin delete account admin picture");
				  userSpogServer.deleteLoggedInUserPictureWithCheck(adminToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
				  				  
				  
				  //msp admin login
				  test.log(LogStatus.INFO,"admin user login");
				  spogServer.userLogin(finalUserEmail, userPassword, test);
				  
				  //create a msp account admin
				  test.log(LogStatus.INFO,"create a msp account admin under msp org");
				  String prefix = RandomStringUtils.randomAlphanumeric(8);
				  final_msp_account_admin_email = prefix + this.msp_account_admin_email;
				  msp_account_admin_id = spogServer.createUserAndCheck(final_msp_account_admin_email, common_password, prefix + msp_account_admin_first_name, prefix + msp_account_admin_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, organization_id, test);
					
				  //msp account admin login
				  spogServer.userLogin(final_msp_account_admin_email, common_password, test);	
				  userSpogServer.setToken(spogServer.getJWTToken());
				  
				  //msp account admin post picture
				  test.log(LogStatus.INFO,"msp account admin post its picture");
				  userSpogServer.uploadPictureForLoginUser(picturePath, test);
				  
				  //delete msp account admin picture
				  test.log(LogStatus.INFO,"msp account admin delete its picture");
				  userSpogServer.deleteLoggedInUserPictureWithCheck(spogServer.getJWTToken(), SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
				  			  
				  //delete account user
				  test.log(LogStatus.INFO,"delete account user");
				  spogServer.DeleteUserById(account_user_id, test);
				 
				  spogServer.userLogin(finalUserEmail, userPassword, test);
				  
				  //delete account
				  test.log(LogStatus.INFO,"delete sub organization");
				  spogServer.DeleteOrganizationWithCheck(sub_organization_id, test);
				  
				  test.log(LogStatus.INFO,"delete msp account admin");
				  spogServer.DeleteUserById(msp_account_admin_id, test);
			  }
			  
			  spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
			  adminToken = spogServer.getJWTToken();
			  userSpogServer.setToken(adminToken);
			  org4SpogServer.setToken(adminToken);
			  //delete user
			  test.log(LogStatus.INFO,"delete user");
			  spogServer.DeleteUserById(user_id, test);
			  
			  //delete organization
			  test.log(LogStatus.INFO,"destroy org");
			//  spogServer.DeleteOrganizationWithCheck(organization_id, test);
			  org4SpogServer.destroyOrganization(organization_id, test);
			  //csr
			  test.log(LogStatus.INFO,"csr admin post its picture");
			  userSpogServer.uploadPictureForLoginUser(picturePath, test);
			  
			  test.log(LogStatus.INFO,"delete user picture");
			  userSpogServer.deleteLoggedInUserPictureWithCheck(adminToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		  }
	  //8. csr read only user could delete its own picture
	  @Test( enabled= true)
	  public void csrReadOnlyDeleteLogginUserPicture()
		  {
			  test = rep.startTest("deleteLogginUserPicture");
			  test.assignAuthor("shuo.zhang");
			  spogServer.errorHandle.printInfoMessageInDebugFile("/****************deleteLogginUserPicture**************/");
			  spogServer.userLogin(this.csrReadOnlyAdminUserName, this.csrReadOnlyAdminPassword, test);
			  test.log(LogStatus.INFO,"csr read only admin post its picture");
			  userSpogServer.setToken(spogServer.getJWTToken());
			  userSpogServer.uploadPictureForLoginUser("testdata\\images\\2.jpg", test);
			  test.log(LogStatus.INFO,"csr read only admin delete its picture");
			  userSpogServer.deleteLoggedInUserPictureWithCheck(spogServer.getJWTToken(), SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			  
		  
		  }

	  
	  @DataProvider(name = "organizationAndUserInfo2")
		public final Object[][] getOrganizationAndUserInfo2() {
			return new Object[][] { {this.final_root_msp_user_name_email}, 
				 {this.final_root_msp_account_admin_user_name_email},
				 {this.final_root_msp_direct_org_user_email},
				 {this.final_sub_msp1_msp_account_user_name_email},
				 {this.final_sub_msp1_user_name_email},
				 {this.final_sub_msp1_account1_user_email},
							 
				};
		}
	  @Test(dataProvider = "organizationAndUserInfo2")
	  public void deleteLoggedInUserPicture4RootMSP(String adminEmail ){
		  
		  test = rep.startTest("deleteLoggedInUserPicture4RootMSP");
		  test.assignAuthor("shuo.zhang");
		  spogServer.errorHandle.printInfoMessageInDebugFile("/****************deleteLoggedInUserPicture4RootMSP**************/");
			  
		  spogServer.userLogin(adminEmail, common_password);	  	
		  userSpogServer.setToken(spogServer.getJWTToken());
		  userSpogServer.uploadPictureForLoginUser("testdata\\images\\2.jpg", test);
		  
		  userSpogServer.deleteLoggedInUserPictureWithCheck(spogServer.getJWTToken(), SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
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
