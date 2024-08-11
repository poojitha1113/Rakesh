package api.users.policy;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
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

import Constants.ErrorCode;
import Constants.SpogConstants;
import InvokerServer.Org4SPOGServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGServer;
import InvokerServer.Source4SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;

public class CreateLoggedInUserPolicyColumnsTest  extends base.prepare.PrepareOrgInfo{
	
	@Parameters({ "pmfKey"})
		public CreateLoggedInUserPolicyColumnsTest(String pmfKey) {
		super(pmfKey);
		// TODO Auto-generated constructor stub
	}



		private SPOGServer spogServer;
		private Policy4SPOGServer policy4SpogServer;
		private UserSpogServer userSpogServer;
		  private Source4SPOGServer sourceSpogServer;
	
	  private String csrAdminUserName;
	  private String csrAdminPassword;
	  private String csrReadOnlyAdminUserName;
	  private String csrReadOnlyAdminPassword;
	  private ExtentTest test;
	  
	  private String postfix_email = "@arcserve.com";
	  private String common_password = "Welcome*02";
	  
	  private String prefix_direct = "spogqa_shuo_direct";
	  private String direct_org_name = prefix_direct + "_org";
	  private String direct_org_id=null;
	  private String direct_user_name = prefix_direct + "_admin";
	  private String direct_user_name_email = direct_user_name + postfix_email;
	  private String direct_user_first_name = direct_user_name + "_first_name";
	  private String direct_user_last_name = direct_user_name + "_last_name";
	  private String direct_user_id =null;
	  private String final_direct_user_name_email = null;

	  private String prefix_msp = "spogqa_shuo_msp";
	  private String msp_org_name = prefix_msp + "_org";

	  private String msp_user_name = prefix_msp + "_admin";
	  private String msp_user_name_email = prefix_msp + "_admin" + postfix_email;
	  private String msp_user_first_name = msp_user_name + "_first_name";
	  private String msp_user_last_name = msp_user_name + "_last_name";
	  
	  private String prefix_msp_account = "spogqa_shuo_msp_account";
	  private String msp_account_admin_name = prefix_msp_account + "_admin";
	  private String msp_account_admin_email =msp_account_admin_name + postfix_email;
	  private String msp_account_admin_first_name = msp_account_admin_name + "_first_name";
	  private String msp_account_admin_last_name = msp_account_admin_name + "_last_name";
	  private String msp_account_admin_id;
	  private String final_msp_account_admin_email;

	  private String msp_org_id=null;
	  private String final_msp_user_name_email=null;	    
	  private String account_id;
	  private String account_user_email;
	  private String msp_user_id;
	  private String account_user_id;
	  private String another_account_id;
	  private String another_account_user_email;
	  private String another_account_user_id;
		

	  //this is for update portal, each testng class is taken as BQ set
	 /* private SQLServerDb bqdb1;
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
	  @Parameters({ "baseURI", "port","logFolder","buildVersion" ,"csrAdminUserName", "csrAdminPassword", "csrReadOnlyAdminUserName","csrReadOnlyAdminPassword"})
	  public void beforeClass(String baseURI, String port, String logFolder,  String buildVersion,  String adminUserName, String adminPassword, String csrROAdminUserName, String csrROPwd ) throws UnknownHostException {
		
		
		  	spogServer = new SPOGServer(baseURI, port);
		  	userSpogServer = new UserSpogServer(baseURI, port);
		  	policy4SpogServer =new Policy4SPOGServer(baseURI,port);
		 	sourceSpogServer =new Source4SPOGServer(baseURI,port);
			org4SpogServer = new Org4SPOGServer(baseURI, port);
			this.BQName = this.getClass().getSimpleName();
		  	rep = ExtentManager.getInstance(BQName,logFolder);
		  	this.csrAdminUserName = adminUserName;
		  	this.csrAdminPassword = adminPassword;
		  	 this.csrReadOnlyAdminUserName = csrROAdminUserName;
			 this.csrReadOnlyAdminPassword = csrROPwd;
			 
		    test = rep.startTest("beforeClass");
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
		
			test.log(LogStatus.INFO,"Creating a account admin For account org");
			account_user_email = prefix + msp_user_name_email;
			account_user_id = spogServer.createUserAndCheck(account_user_email, common_password, prefix + msp_user_first_name, prefix + msp_user_last_name, SpogConstants.DIRECT_ADMIN, account_id, test);
		
			
			//create account, account user 
			test.log(LogStatus.INFO,"Creating another account For msp org");
			prefix = RandomStringUtils.randomAlphanumeric(8);
			another_account_id = spogServer.createAccountWithCheck(msp_org_id, "sub_" + prefix + msp_org_name, "", test);
		  	
			test.log(LogStatus.INFO,"Creating a account admin For another account org");
			another_account_user_email = prefix + msp_user_name_email;
			another_account_user_id = spogServer.createUserAndCheck(another_account_user_email, common_password, prefix + msp_user_first_name, prefix + msp_user_last_name, SpogConstants.DIRECT_ADMIN, another_account_id, test);
			

			spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
			test.log(LogStatus.INFO,"assign account to msp account admin");
			String[] mspAccountAdmins = new String []{msp_account_admin_id};
			userSpogServer.assignMspAccountAdmins(msp_org_id, account_id, mspAccountAdmins , spogServer.getJWTToken()); 
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
	  
	  
	  @DataProvider(name = "organizationAndUserInfo")
		public final Object[][] getOrganizationAndUserInfo() {
			return new Object[][] {
				{final_direct_user_name_email, common_password,"Name;true;1,Protected Sources;false;2"}, 
				 {final_msp_user_name_email,  common_password,"Name;true;1"},		
				 {final_msp_account_admin_email, common_password,"Protected Sources;false;2"},
				 {account_user_email,  common_password, "Latest Job;false;3,Protected Sources;false;2"},	
				{final_direct_user_name_email, common_password,"Name;none;none"},
				{final_direct_user_name_email, common_password,"Name;false;none"},
				{final_direct_user_name_email, common_password,"Name;none;1"},
				{final_direct_user_name_email, common_password,"Name;true;1,Name;false;11" },
				{csrReadOnlyAdminUserName, csrReadOnlyAdminPassword,"Name;true;1,Name;false;11" },
				 {final_root_msp_user_name_email, common_password,"Name;true;1,Protected Sources;false;2"},
				 {final_root_msp_account_admin_user_name_email, common_password,"Name;true;1,Protected Sources;false;2"},
				 {final_root_msp_direct_org_user_email, common_password,"Name;true;1,Protected Sources;false;2"},
				 {final_sub_msp1_user_name_email, common_password,"Name;true;1,Protected Sources;false;2"},
				 {final_sub_msp1_msp_account_user_name_email, common_password,"Name;true;1,Protected Sources;false;2"},
				 {final_sub_msp1_account1_user_email, common_password,"Name;true;1,Protected Sources;false;2"},
				 
			};
		}
	  @Test(dataProvider ="organizationAndUserInfo", enabled=true)
	  /**
	   * 4. account admin could create  its own policy columns
		3. msp account admin could create its own policy columns
		2. msp admin could create  its own policy columns
		1. direct admin could create its own policy columns
		11. set visible as true
		10. set visible as false
		17. create policy column with column_id and order_id
		16. create policy column with column_id and visible
		15. create policy column only with column_id
		19. set two columns with same column_id, it should use the first one
		18. the second call for the create user policy columns should report 400

	   * @param userName
	   * @param password
	   * @param specifiedPolicyColumns
	   * @param statusCode
	   * @param errorCode
	   */
	  public void adminCanCreateLoggedInUserPolicyColumns(String userName, String password, String specifiedPolicyColumns){
		  
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("shuo.zhang");
		  spogServer.errorHandle.printInfoMessageInDebugFile("/****************" + 
		  ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()) + "**************/");
		  
		  spogServer.userLogin(userName, password, test);
		  
		  String token = spogServer.getJWTToken();
		  test.log(LogStatus.INFO,"get policy column array list");		
		  ArrayList<HashMap<String, Object>> columnsList =   policy4SpogServer.getPolicyColumnArrayList(specifiedPolicyColumns, token, test);
		
		  test.log(LogStatus.INFO,"create logged in user policy columns");
		  policy4SpogServer.createLoggedInUserPolicyColumnsWithCheck( columnsList, token, SpogConstants.SUCCESS_POST, null);
		  policy4SpogServer.createLoggedInUserPolicyColumnsWithCheck( columnsList, token, SpogConstants.REQUIRED_INFO_NOT_EXIST,  ErrorCode.COLUMN_EXIST);
		  
		
		  test.log(LogStatus.INFO,"delete user policy columns");
		  policy4SpogServer.deleteLoggedInUserPolicyColumnsWithCheck(token, SpogConstants.SUCCESS_GET_PUT_DELETE, null);

	 }
	  
	  @DataProvider(name = "organizationAndUserInfo1")
			public final Object[][] getOrganizationAndUserInfo1() {
				return new Object[][] {
					{UUID.randomUUID().toString(),"true", "3", SpogConstants.RESOURCE_NOT_EXIST, ErrorCode.COLUMN_NOT_EXIST}, 
					{null,"true","3",SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.ELEMENT_BLANK},
					{"","true", "3",SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.ELEMENT_BLANK}, 
					{"e9c969d4-fd6f-467e-bf68-eb70becdf22d", "true","0",  SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.INVALID_ORDER_ID}, 
					{"e9c969d4-fd6f-467e-bf68-eb70becdf22d", "true", "-5",  SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.INVALID_ORDER_ID},	
					{"e9c969d4-fd6f-467e-bf68-eb70becdf22d","true", "8", SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.ORDER_ID_INVALID},
				};
			}


		/**
		 * 
			9. set column id as  "", report 400
			8. set column id as null, report 400
			7. set column_id as non-exsiting id, report 404
			14. set order_id more than column default size, report 400
			13. set order_id as negative number, it should report error 400
			12. set order_id as 0, it should report error 400
			
					 * 
		 */
		@Test(dataProvider ="organizationAndUserInfo1", enabled=true)
		
		public void errorHandling( String column_id, String visible, String order_id, int statusCode, String errorCode){
			  
			  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
			  test.assignAuthor("shuo.zhang");
			  spogServer.errorHandle.printInfoMessageInDebugFile("/****************" + 
					  ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()) + "**************/");
			  
			  spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
			  
			  System.out.println("column id is " + column_id);		
			  String token = spogServer.getJWTToken();
			  
			  policy4SpogServer.deleteLoggedInUserPolicyColumnsWithCheck(token, SpogConstants.SUCCESS_GET_PUT_DELETE, null);
			  test.log(LogStatus.INFO,"get policy column info");
			  ArrayList<HashMap<String, Object>> columnsList = new ArrayList<HashMap<String, Object>> ();
			  HashMap<String, Object> columnInfo = sourceSpogServer.getSourceColumnInfo(column_id, visible, order_id);
			  columnsList.add(columnInfo);
			  
			  test.log(LogStatus.INFO,"createLoggedInUserSourcesColumnsWithCheck");
			  policy4SpogServer.createLoggedInUserPolicyColumnsWithCheck( columnsList, token, statusCode, errorCode);
			  
		}
		  @Test(enabled=true)
		  /**
		   *6. fail to create loggin user's with invalid token, report 401
			5. fail to create loggin user's policy columns without login, report 401

		   */
		  public void createLoggedInUserPolicyColumnsWithoutLogin(){
			  
			  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
			  test.assignAuthor("shuo.zhang");
			  spogServer.errorHandle.printInfoMessageInDebugFile("/****************createLoggedInUserPolicyColumnsWithoutLogin**************/");
			  spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword, test);
			  		
			  String token = spogServer.getJWTToken();
			  test.log(LogStatus.INFO,"createLoggedInUserPolicyColumnsWithoutLogin");
			  policy4SpogServer.createLoggedInUserPolicyColumnsWithoutLoginAndCheck();
			  test.log(LogStatus.INFO,"createLoggedInUserPolicyColumns With invalid token");
			  ArrayList<HashMap<String, Object>> columnsList = new ArrayList<HashMap<String, Object>>();
			  policy4SpogServer.createLoggedInUserPolicyColumnsWithCheck( columnsList, UUID.randomUUID().toString(), SpogConstants.NOT_LOGGED_IN,  ErrorCode.AUTHORIZATION_HEADER_BLANK);
			
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
