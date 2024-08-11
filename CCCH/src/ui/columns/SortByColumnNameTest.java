package ui.columns;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import javax.print.DocFlavor.STRING;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.codehaus.groovy.ast.expr.CastExpression;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.ErrorCode;
import Constants.SourceColumnConstants;
import Constants.SpogConstants;
import InvokerServer.Org4SPOGServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import ui.base.common.PageName;
import ui.base.common.TableHeaders;
import ui.spog.pages.protect.CustomerAccountsPage;
import ui.spog.server.ColumnHelper;
import ui.spog.server.CustomerAccountsPageHelper;
import ui.spog.server.SPOGUIServer;
import ui.spog.server.SourcePageHelper;


public class SortByColumnNameTest extends base.prepare.Is4Org{

	private SPOGServer spogServer;
	private UserSpogServer userSpogServer;
	private SPOGUIServer spogUIServer;
	private Org4SPOGServer org4SpogServer;
	private SourcePageHelper sourcePageHelper;
	private ColumnHelper columnHelper;
	private CustomerAccountsPageHelper customerAccountsPageHelper;
	private String csrAdminUserName;
	private String csrAdminPassword;
	private String csr_token;
	private ExtentTest test;
	
	private String postfix_email = "@arcserve.com";
	private String common_password = "Welcome*02";

	private String prefix_direct = "spogqa_rakesh_direct";
	private String direct_org_name = prefix_direct + "_org";
	private String direct_org_id=null;
	private String direct_user_name = prefix_direct + "_admin";
	private String direct_user_name_email = direct_user_name + postfix_email;
	private String direct_user_first_name = direct_user_name + "_first_name";
	private String direct_user_last_name = direct_user_name + "_last_name";
	private String final_direct_user_name_email = null;
	private String direct_user_id;
	private String direct_user_token;

	private String prefix_msp = "spogqa_rakesh_msp";
	private String msp_org_name = prefix_msp + "_org";
	private String msp_user_name = prefix_msp + "_admin";
	private String msp_user_name_email = prefix_msp + "_admin" + postfix_email;
	private String msp_user_first_name = msp_user_name + "_first_name";
	private String msp_user_last_name = msp_user_name + "_last_name";
	private String msp_org_id=null;
	private String final_msp_user_name_email=null;
	private String msp_user_id;
	private String msp_user_validToken;
	
	private String prefix_msp_b = "spog_rakesh_msp_b";
	private String msp_org_name_b = prefix_msp_b + "_org";
	private String msp_user_name_email_b = msp_org_name_b + postfix_email;
	private String msp_user_first_name_b = msp_org_name_b + "_first_name";
	private String msp_user_last_name_b = msp_org_name_b + "_last_name";
	private String msp_org_id_b=null;
	private String final_msp_user_name_email_b=null;
	private String msp_user_id_b;
	private String msp_user_b_validToken;

	private String initial_sub_org_name_a = "SPOG_QA_RAKESH_BQ_sub_a";
	private String initial_sub_email_a = "spog_qa_sub_RAKESH_a@arcserve.com";
	private String initial_sub_first_name_a = "spog_qa_sub_RAKESH_a";
	private String initial_sub_last_name_a = "spog_qa_sub_CHALAMALA_a";
	private String sub_orga_user_validToken;
	private String suborga_id;
	private String suborga_user_id;;
	
	private String initial_sub_org_name_b = "SPOG_QA_RAKESH_BQ_sub_b";
	private String initial_sub_email_b = "spog_qa_sub_RAKESH_b@arcserve.com";
	private String initial_sub_first_name_b = "spog_qa_sub_RAKESH_b";
	private String initial_sub_last_name_b = "spog_qa_sub_CHALAMALA_b";
	private String sub_orgb_user_validToken;
	private String suborgb_id;
	private String suborgb_user_id;;
	
	private String initial_sub_org_name_1 = "SPOG_QA_RAKESH_BQ_sub_1";
	private String initial_sub_email_1 = "spog_qa_sub_RAKESH_1@arcserve.com";
	private String initial_sub_first_name_1 = "spog_qa_sub_RAKESH_1";
	private String initial_sub_last_name_1 = "spog_qa_sub_CHALAMALA_";
	private String sub_org1_user_validToken;
	private String suborg1_id;
	private String suborg1_user_id;;
	
	private String prefix_msp_account_admin = "spog_rakesh_msp_account";
	private String msp_account_admin_email = prefix_msp_account_admin+postfix_email;
	private String msp_account_admin_first_name = prefix_msp_account_admin+"_first_name";
	private String msp_account_admin_last_name = prefix_msp_account_admin+"_last_name";
	private String msp_account_admin_id;
	private String msp_account_admin_validToken;
	
	private String prefix_msp_account_admin_b = "spog_rakesh_msp_account_b";
	private String msp_account_admin_email_b = prefix_msp_account_admin_b+postfix_email;
	private String msp_account_admin_first_name_b = prefix_msp_account_admin_b+"_first_name";
	private String msp_account_admin_last_name_b = prefix_msp_account_admin_b+"_last_name";
	private String msp_account_admin_id_b;
	private String msp_account_admin_b_validToken;
	
	private String  org_model_prefix=this.getClass().getSimpleName();

	//source
	ArrayList<String> allSourceColumns = new ArrayList<>(
												Arrays.asList("Hypervisor", "Agent", "VM Name", "Source Group", "Policy", 
												"Latest Job", "Latest Recovery Point", "Connection", "Status", "OS"));
	ArrayList<String> defaultSourceColumns = new ArrayList<>(Arrays.asList("Name", "Type"));
	
	//destination
	ArrayList<String> allDestinationColumns = new ArrayList<>(
													Arrays.asList("Data Center Region", "Protection Policy", "Location", "Type",
													"Retention", "Latest Job", "Protected Data", "Source Count"));
	ArrayList<String> defaultDestinationColumns = new ArrayList<>(Arrays.asList("Name", "Storage Usage"));
	
	//policy
	ArrayList<String> allPolicyColumns = new ArrayList<>(
			Arrays.asList("Description", "Latest Job", "Source Group", "Protected Sources", "Unprotected Sources"));
	ArrayList<String> defaultPolicyColumns = new ArrayList<>(Arrays.asList("Policy Name", "Status"));
	
	//Recovered Resources
	ArrayList<String> allRecoveredResourcesColumns = new ArrayList<>(
												Arrays.asList("Provisioned on", "IP Address", "Recovery Point", "OS", "State",
														"Type", "Policy", "Allocated RAM", "Allocated vCPU"));
	ArrayList<String> defaultRecoveredResourcesColumns = new ArrayList<>(Arrays.asList("Name"));
	
	//Jobs
	ArrayList<String> allJobColumns = new ArrayList<>(
											Arrays.asList("End Time", "Start Time", "Destination", "Policy", "Status",
													"Job Type", "Duration"));
	ArrayList<String> defaultJobColumns = new ArrayList<>(Arrays.asList("Job Name", "Source"));
	
	//backup job report columns
	ArrayList<String> allBackupJobReportsColumns = new ArrayList<>(
			Arrays.asList("Source Group Name", "Data Transferred (MB)", "Errors", "Warnings", "End Time","Start Time",
					"Policy Name","Destination","Data Processed (MB)", "Data Written (MB)", "Duration"));
	ArrayList<String> defaultBackupJobReportsColumns = new ArrayList<>(Arrays.asList("Job Status", "Source"));
	
	//Recovery job report columns
	ArrayList<String> allRecoveryJobReportsColumns = new ArrayList<>(
				Arrays.asList("Source Group Name", "Errors", "Warnings", "End Time","Start Time",
						"Policy Name","Destination", "Duration", "Destination Path", "Source Path"));
	ArrayList<String> defaultRecoveryJobReportsColumns = new ArrayList<>(Arrays.asList("Job Status", "Source"));
		
	//user columns
		ArrayList<String> allUsersColumns = new ArrayList<>(
					Arrays.asList("Is Blocked", "Last Logged in", "Added on", "Status", "Role"));
		ArrayList<String> defaultUsersColumns = new ArrayList<>(Arrays.asList("Name", "Email"));
	
	@BeforeClass
	@Parameters({ "baseURI", "port",   "logFolder", "csrAdminUserName", "csrAdminPassword", "buildVersion", "uiBaseURI", "browserType", "maxWaitTimeSec"})
	public void beforeClass(String baseURI, String port, String logFolder, String adminUserName, String adminPassword,  String buildVersion,
							String uiBaseURI, String browserType, int maxWaitTimeSec) throws UnknownHostException {

		org4SpogServer = new Org4SPOGServer(baseURI, port);
		spogServer = new SPOGServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		this.csrAdminUserName = adminUserName;
		this.csrAdminPassword = adminPassword;
		rep = ExtentManager.getInstance("Test",logFolder);
		test = rep.startTest("beforeClass");

		//Create the organizations and users
//		prepareEnv();
		
		columnHelper = new ColumnHelper(browserType, maxWaitTimeSec);
		columnHelper.openUrl(uiBaseURI);
//		columnHelper.login_Spog(final_direct_user_name_email, common_password);
		columnHelper.login_Spog("test.direct10001+dirs27@gmail.com", "Mclaren@2020");
				
		this.BQName = this.getClass().getSimpleName();
		String author = "Rakesh.Chalamala";
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
			//creationTime = System.currentTimeMillis();
			try {
				bqdb1.updateTable(this.BQName, runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0", String.valueOf(Nooftest), count1.getcreationtime(), "InProgress",author+" and Rest server is "+baseURI.split("//")[1]);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	
	
	@DataProvider(name="columnsInfo")
	public Object[][] columnsInfo(){
		return new Object[][] {
			{PageName.SOURCE, TableHeaders.name, "desc"},
			{PageName.SOURCE, TableHeaders.name, "asc"},
			{PageName.DESTINATION, TableHeaders.name, "desc"},
			{PageName.DESTINATION, TableHeaders.name, "asc"},
		};
	}
	
	@Test(dataProvider="columnsInfo")
	public void sortByColumnName(String page, String columnName, String sortOrder) {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		
		columnHelper.goToPage(page);
				
		columnHelper.sortByColumnNameWithCheck(columnName, sortOrder, test);
		
	}
	
	
	
	private void prepareEnv(){

		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
		csr_token = spogServer.getJWTToken();
		
		//************************create direct org,users *************************************
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		test.log(LogStatus.INFO,"create a direct org");
		direct_org_id= spogServer.CreateOrganizationWithCheck(prefix + direct_org_name+org_model_prefix , SpogConstants.DIRECT_ORG, null, null, null, null, test);
		final_direct_user_name_email = prefix + direct_user_name_email;

		test.log(LogStatus.INFO,"create a admin under direct org");
		direct_user_id = spogServer.createUserAndCheck(final_direct_user_name_email, common_password, prefix + direct_user_first_name, prefix + direct_user_last_name, SpogConstants.DIRECT_ADMIN, direct_org_id, test);
		spogServer.userLogin(final_direct_user_name_email, common_password);
		direct_user_token = spogServer.getJWTToken();

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
	public void afterClass() {
		columnHelper.destroy();
		recycleVolumeInCDandDestroyOrg(org_model_prefix);
	}

	/*****************************Generic**********************************/
	private String getTodayDate() {
		
		SimpleDateFormat dateFormater = new SimpleDateFormat("MM/dd/yyyy");
		String date = dateFormater.format(new Date());
		
		return date;
	}	
}
