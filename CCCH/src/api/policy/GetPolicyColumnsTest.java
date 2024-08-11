package api.policy;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

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
import InvokerServer.Org4SPOGServer;
import InvokerServer.Policy4SPOGServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;

public class GetPolicyColumnsTest  extends base.prepare.PrepareOrgInfo{

	@Parameters({ "pmfKey"})
	  public GetPolicyColumnsTest(String pmfKey) {
		super(pmfKey);
		// TODO Auto-generated constructor stub
	}

	private SPOGServer spogServer;
	  private Policy4SPOGServer policy4SPOGServer;
	  private String csrAdminUserName;
	  private String csrAdminPassword;
	  private String csrReadOnlyAdminUserName;
	  private String csrReadOnlyAdminPassword;
	  private ExtentTest test;
	  //this is for update portal, each testng class is taken as BQ set
	  /*private SQLServerDb bqdb1;
	  public int Nooftest;
	  private long creationTime;
	  private String BQName=null;
	  private String runningMachine;
	  private testcasescount count1;
	  private String buildVersion;
	  private ExtentReports rep;
	  */
	  private String postfix_email = "@arcserve.com";
	  private String common_password = "Welcome*02";
	  
	  private String prefix_direct = "spogqa_shuo_direct";
	  private String direct_org_name = prefix_direct + "_org";
	  private String direct_org_id=null;
	  private String direct_user_name = prefix_direct + "_admin";
	  private String direct_user_name_email = direct_user_name + postfix_email;
	  private String direct_user_first_name = direct_user_name + "_first_name";
	  private String direct_user_last_name = direct_user_name + "_last_name";
	  private String final_direct_user_name_email = null;

	  private String prefix_msp = "spogqa_shuo_msp";
	  private String msp_org_name = prefix_msp + "_org";
	  private String msp_user_name = prefix_msp + "_admin";
	  private String msp_user_name_email = prefix_msp + "_admin" + postfix_email;
	  private String msp_user_first_name = msp_user_name + "_first_name";
	  private String msp_user_last_name = msp_user_name + "_last_name";
	  private String msp_org_id=null;
	  private String final_msp_user_name_email=null;	  

	  private String prefix_msp_account = "spogqa_shuo_msp_account";
	  private String msp_account_admin_name = prefix_msp_account + "_admin";
	  private String msp_account_admin_email =msp_account_admin_name + postfix_email;
	  private String msp_account_admin_first_name = msp_account_admin_name + "_first_name";
	  private String msp_account_admin_last_name = msp_account_admin_name + "_last_name";
	  private String msp_account_admin_id;
	  private String final_msp_account_admin_email;
	  
	  private String account_id;
	  private String account_user_email;
	  private String direct_user_id;
	  private String msp_user_id;
	  private String account_user_id;
	private Org4SPOGServer org4SpogServer;
	private String  org_model_prefix=this.getClass().getSimpleName();
	  
	 @BeforeClass
	  @Parameters({ "baseURI", "port","logFolder","buildVersion" ,"csrAdminUserName", "csrAdminPassword", "csrReadOnlyAdminUserName","csrReadOnlyAdminPassword"})
	  public void beforeClass(String baseURI, String port, String logFolder,  String buildVersion,  String adminUserName, String adminPassword, String csrROAdminUserName, String csrROPwd ) throws UnknownHostException {
		  spogServer = new SPOGServer(baseURI, port);
		  policy4SPOGServer = new Policy4SPOGServer(baseURI, port);
			org4SpogServer = new Org4SPOGServer(baseURI, port);
		  this.csrAdminUserName = adminUserName;
		  this.csrAdminPassword = adminPassword;
		  this.csrReadOnlyAdminUserName = csrROAdminUserName;
		  this.csrReadOnlyAdminPassword = csrROPwd;
		  rep = ExtentManager.getInstance("GetPolicyColumnsTest", logFolder);
		  test = rep.startTest("beforeClass");
			
		  prepareEnv();
		  prepare(baseURI, port, logFolder, this.csrAdminUserName,  this.csrAdminPassword, this.getClass().getSimpleName() );
		  //this is for update portal
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
	  

	  private void prepareEnv(){
		
		  	spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
			//*******************create direct org,user,site**********************/
			String prefix = RandomStringUtils.randomAlphanumeric(8);
			test.log(LogStatus.INFO,"create a direct org");
			direct_org_id = spogServer.CreateOrganizationWithCheck(prefix+direct_org_name +org_model_prefix, SpogConstants.DIRECT_ORG, null, null, null, null, test);
			final_direct_user_name_email = prefix + direct_user_name_email;
			
			test.log(LogStatus.INFO,"create a admin under direct org");
			direct_user_id = spogServer.createUserAndCheck(final_direct_user_name_email, common_password, prefix + direct_user_first_name, prefix + direct_user_last_name, SpogConstants.DIRECT_ADMIN, direct_org_id, test);
			spogServer.userLogin(final_direct_user_name_email, common_password);
		  	

			//************************create msp org,user,site*************************************
			spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
			prefix = RandomStringUtils.randomAlphanumeric(8);
			test.log(LogStatus.INFO,"create a msp org");
			msp_org_id = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name+org_model_prefix , SpogConstants.MSP_ORG, null, null, null, null, test);
			final_msp_user_name_email = prefix + msp_user_name_email;
			
			test.log(LogStatus.INFO,"create a admin under msp org");
			msp_user_id = spogServer.createUserAndCheck(final_msp_user_name_email, common_password, prefix + msp_user_first_name, prefix + msp_user_last_name, SpogConstants.MSP_ADMIN, msp_org_id, test);
			spogServer.userLogin(final_msp_user_name_email, common_password);
		  	
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

			
			
	  }
	  @DataProvider(name = "userInfo")
		public final Object[][] getOrganizationAndUserInfo1() {
			return new Object[][] { 	{this.final_direct_user_name_email, common_password},
				{this.final_msp_user_name_email, common_password},
				{this.final_msp_account_admin_email, common_password},
				{this.account_user_email, common_password},						
				{this.csrAdminUserName, this.csrAdminPassword},	
				{this.csrReadOnlyAdminUserName, this.csrReadOnlyAdminPassword},
				{final_root_msp_user_name_email, common_password},
				{final_root_msp_account_admin_user_name_email, common_password},
				{final_root_msp_direct_org_user_email, common_password},
				{final_sub_msp1_user_name_email, common_password},
				{final_sub_msp1_msp_account_user_name_email, common_password},
				{final_sub_msp1_account1_user_email, common_password},
			};
		}		  

	  @Test(dataProvider = "userInfo", enabled=true)
	  /**
	   * 1. login as csr admin could get policy column
	   * 2. login as direct admin could get policy column
	   * 3. login as msp  admin could get policy column
	   * 4. login as msp account admin could get policy column
	   * 5. login as account admin could get policy column
	   * 6. user can not get policy column without login
	   * 7. check all policy columns' count
	   * 8. check all policy columns' long lable
	   * 9. check all policy columns' short label
	   * 10. check all policy columns' filter name

	   * @param userName
	   * @param password
	   */
	  public void adminCanGetPolicyColumns(String userName, String password){
		  
		  test = rep.startTest("adminCanGetPolicyColumns");
		  test.assignAuthor("shuo.zhang");
		  spogServer.userLogin(userName, password);
		  policy4SPOGServer.getPolicyColumnsWithCheck(spogServer.getJWTToken(),SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		  policy4SPOGServer.getPolicyColumnsWithoutLoginWithCheck(test);
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

	 	  private void deleteEnv(){

	 			spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
	 			org4SpogServer.setToken(spogServer.getJWTToken());			
	 			test.log(LogStatus.INFO,"delete direct org");
				spogServer.errorHandle.printInfoMessageInDebugFile("destroy direct organization");
				  org4SpogServer.destroyOrganization(direct_org_id , test);
				
				test.log(LogStatus.INFO,"delete msp org");
				spogServer.errorHandle.printInfoMessageInDebugFile("destroy msp organization");
				org4SpogServer.destroyOrganization(msp_org_id , test);
	 			
	 	  }

}
