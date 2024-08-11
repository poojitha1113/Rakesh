package api.policies;

import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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

import Constants.SpogConstants;
import InvokerServer.GatewayServer;
import InvokerServer.Log4GatewayServer;
import InvokerServer.Org4SPOGServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.Source4SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import invoker.CI4LoadingPageInvoker;
import io.restassured.response.Response;

public class ResourceMultitenancyTest extends base.prepare.PrepareOrgInfo{
	@Parameters({ "pmfKey"})
	public ResourceMultitenancyTest(String pmfKey) {
		super(pmfKey);
		// TODO Auto-generated constructor stub
	}
	private SPOGServer spogServer;
	private CI4LoadingPageInvoker cI4LoadingPageInvoker;
	private GatewayServer gatewayServer;
	private Log4GatewayServer log4GatewayServer;
	private Org4SPOGServer org4SPOGServer;
	private Policy4SPOGServer policy4SPOGServer;	
	private Source4SPOGServer source4spogServer;
	private UserSpogServer userSpogServer;
	private SPOGDestinationServer spogDestinationServer;
	private String csrAdmin;
	private String csrPwd;
	private ExtentTest test;
	private String sub_msp_pwd="Welcome*02";
	private String csr_token=null;
	private String initial_msp_org_name = "ci_msp1_do_not_delete";
	private String initial_msp_email = "ci_msp01@arcserve.com";
	private String initial_msp_org_id = "9f052f78-b8a7-4a66-9a13-9e88a30c7c4a";
	private String initial_msp_user_id = "125185bc-00ba-4bf2-99c8-09ad4276e09a";
	
	private String initial_sub_org_name_1 = "ci_msp1_account1";
	private String sub_normal_id1="e8e62f05-1107-40dd-9b1e-8d44f1082878";
	private String sub_normal_name1="vol_ztst-3734.zetta.net";
	private String initial_sub_email_1 = "jing_account_ui_1@arcserve.com";
	private String initial_sub_org_id_1 = "043fa68b-82df-4139-b586-2b09b7b73bd4";
	private String initial_sub_email_user_id1="c5e8cfce-9dfd-4cc7-a250-926d251e47ff";
	
	private String initial_sub_org_name_2 = "ci_msp1_account2";
	private String sub_normal_id2="f528f7c3-8850-4ddd-86c8-4dd2834d2489";
	private String sub_normal_name2="vol_ztst-2337.zetta.net";
	private String initial_sub_email_2 = "ci_account2_msp1@arcserve.com";
	private String initial_sub_org_id_2 = "216a9980-78ca-4a71-b0e2-661bfba3c43e";
	private String initial_sub_email_user_id2="0a7aa4ef-f7a9-4191-afe9-a4f7366e2125";
	
	private String initial_msp_account_admin_email = "ci_msp1_account_admin@arcserve.com";
	private String initial_msp_account_admin_user_id="35087902-dd35-4b7c-b597-0b2affa7f6cf";
	
	private String initial_direct_org_name = "ci_direct1_do_not_delete";
	private String direct_source_name="LinuxCIM4";
	private String direct_source_id="59cdee1d-5913-486d-8a67-ce1b66e27c66";
//	private String initial_direct_email = "ci_direct01@arcserve.com";
//	private String initial_direct_user_id = "e1a1604d-0dde-4e0d-b3fc-6722e3dbabd7";
//	private String initial_direct_org_id = "e61e1661-4f6c-42e2-84b1-6428f94b4abe";
	private String initial_direct_email = "jing_direct_ui@arcserve.com";
	private String initial_direct_user_id = "02e500b2-ac52-4d3e-9010-145cc8cf185a";
	private String initial_direct_org_id = "1ab97067-24f2-452e-b816-b42781cc2c09";
	private String OrgPwdForPrepare = "Caworld_2017";
	private String direct_normal_id="5eca4341-fae1-4910-95dd-dd2e97e648c0";
	private String direct_normal_name="ci_direct_n1";
	private String datacenter_id="91a9b48e-6ac6-4c47-8202-614b5cdcfe0c";
	private boolean login_ok=false;
//	private SQLServerDb bqdb1;
//	public int Nooftest;
//    private long creationTime;
//	private String BQName=null;
//	private String runningMachine;
//	private String buildVersion;
//	private ExtentReports rep;
//	private testcasescount count1;

	//@BeforeSuite
	public void beforeSuit() {
		try {
			Thread.sleep(300000);
		
		  } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		  }
		System.out.println("enter before suite");
		//spogServer.checkSwagDocIsActive("http://qaspog2.zetta.net", 8080, SpogConstants.SUCCESS_GET_PUT_DELETE);
		// spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("test"),
		// SpogConstants.CSR_ORG, csrAdminUserName, csrAdminPassword, "", "");
	}

	@BeforeClass
	@Parameters({ "baseURI", "port", "csrAdminUserName", "csrAdminPassword", "logFolder", "runningMachine",
			"buildVersion" })
	public void beforeClass(String baseURI, String port, String csrAdminUserName, String csrAdminPassword,
			String logFolder, String runningMachine, String buildVersion) {

		rep = ExtentManager.getInstance("CIIntegrationTest", logFolder);
		test = rep.startTest("initializing data...");
		this.csrAdmin = csrAdminUserName;
		this.csrPwd = csrAdminPassword;
		count1 = new testcasescount();
		System.out.println("enter before class");
		int retry_time = 15;
		
		this.BQName = this.getClass().getSimpleName();
		  String author = "Jing.Shan";
		  this.runningMachine = runningMachine;
		  this.csrAdmin=csrAdminUserName;
		  this.csrPwd=csrAdminPassword;
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
		
		for(int i=0;i<retry_time;i++){
			try {
				Thread.sleep(20000);
				spogServer = new SPOGServer(baseURI, port);
				spogServer.userLogin(this.csrAdmin, this.csrPwd);
				System.out.println("csr user login for times is:"+i);
				login_ok=true;
				if(login_ok){
					break;
				}
			  } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			  }
		}
		cI4LoadingPageInvoker= new CI4LoadingPageInvoker(baseURI, port);
		System.out.println("create cI4LoadingPageInvoker");
		policy4SPOGServer=new Policy4SPOGServer(baseURI, port);
		System.out.println("create policy4SPOGServer");
		source4spogServer=new Source4SPOGServer(baseURI, port);
		System.out.println("create source4spogServer");
		
		gatewayServer = new GatewayServer(baseURI, port);
		System.out.println("create gatewayServer");
		log4GatewayServer = new Log4GatewayServer(baseURI, port);
		System.out.println("create log4GatewayServer");
		org4SPOGServer = new Org4SPOGServer(baseURI, port);
		System.out.println("create org4SPOGServer");
		userSpogServer = new UserSpogServer(baseURI, port);
		System.out.println("create userSpogServer");
		spogDestinationServer= new SPOGDestinationServer(baseURI, port);
		System.out.println("create spogDestinationServer");
		
		//spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spog_ci"), SpogConstants.CSR_ORG, csrAdminUserName, csrAdminPassword, "ci", "ci");
		
	}
	
	
	@Test
	public void direct01() throws InterruptedException{
		spogServer.userLogin(this.initial_direct_email, this.OrgPwdForPrepare);
		String errorcode="00100101";
		String error_message="Permission required to manage the resource for current user";
		spogServer.getSourcesWithOrgIdFailed(this.initial_msp_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
        spogServer.getPoliciesWithOrgIdFailed(this.initial_msp_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getJobsWithOrgIdFailed(this.initial_msp_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getLogsWithOrgIdFailed(this.initial_msp_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getUsersWithOrgIdFailed(this.initial_msp_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getDestinationsWithOrgIdFailed(this.initial_msp_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getCloudaccountsWithOrgIdFailed(this.initial_msp_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getReportsWithOrgIdFailed(this.initial_msp_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getAlertsWithOrgIdFailed(this.initial_msp_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		
	}
	
	//@Test
	public void direct02() throws InterruptedException{
		spogServer.userLogin(this.initial_direct_email, this.OrgPwdForPrepare);
		String errorcode="00100101";
		String error_message="Permission required to manage the resource for current user";
		
		spogServer.getSourcesWithOrgIdFailed(this.initial_sub_org_id_1, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getPoliciesWithOrgIdFailed(this.initial_sub_org_id_1, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getJobsWithOrgIdFailed(this.initial_sub_org_id_1, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getLogsWithOrgIdFailed(this.initial_sub_org_id_1, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getUsersWithOrgIdFailed(this.initial_sub_org_id_1, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getDestinationsWithOrgIdFailed(this.initial_sub_org_id_1, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getCloudaccountsWithOrgIdFailed(this.initial_sub_org_id_1, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getReportsWithOrgIdFailed(this.initial_sub_org_id_1, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getAlertsWithOrgIdFailed(this.initial_sub_org_id_1, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		
	}
	
	@Test
	public void direct03() throws InterruptedException{
		spogServer.userLogin(this.initial_direct_email, this.OrgPwdForPrepare);
		String errorcode="00100101";
		String error_message="Permission required to manage the resource for current user";
				
		spogServer.userLogin(this.initial_msp_email, this.OrgPwdForPrepare);
		spogServer.getSourcesWithOrgIdFailed(this.initial_direct_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getPoliciesWithOrgIdFailed(this.initial_direct_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getJobsWithOrgIdFailed(this.initial_direct_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getLogsWithOrgIdFailed(this.initial_direct_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getUsersWithOrgIdFailed(this.initial_direct_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getDestinationsWithOrgIdFailed(this.initial_direct_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getCloudaccountsWithOrgIdFailed(this.initial_direct_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getReportsWithOrgIdFailed(this.initial_direct_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getAlertsWithOrgIdFailed(this.initial_direct_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
	}
	
	@Test
	public void msp01() throws InterruptedException{
		String errorcode="00100101";
		String error_message="Permission required to manage the resource for current user";
		spogServer.userLogin(this.initial_msp_account_admin_email, this.OrgPwdForPrepare);
		spogServer.getSourcesWithOrgIdFailed(this.initial_direct_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getPoliciesWithOrgIdFailed(this.initial_direct_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getJobsWithOrgIdFailed(this.initial_direct_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getLogsWithOrgIdFailed(this.initial_direct_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		//spogServer.getUsersWithOrgIdFailed(this.initial_direct_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getDestinationsWithOrgIdFailed(this.initial_direct_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getCloudaccountsWithOrgIdFailed(this.initial_direct_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getReportsWithOrgIdFailed(this.initial_direct_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getAlertsWithOrgIdFailed(this.initial_direct_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
	}
	
	@Test
	public void msp02() throws InterruptedException{
		String errorcode="00100101";
		String error_message="Permission required to manage the resource for current user";
		spogServer.userLogin(this.initial_msp_account_admin_email, this.OrgPwdForPrepare);
		spogServer.getSourcesWithOrgIdFailed(this.initial_sub_org_id_1, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getPoliciesWithOrgIdFailed(this.initial_sub_org_id_1, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getJobsWithOrgIdFailed(this.initial_sub_org_id_1, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getLogsWithOrgIdFailed(this.initial_sub_org_id_1, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		//spogServer.getUsersWithOrgIdFailed(this.initial_sub_org_id_1, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getDestinationsWithOrgIdFailed(this.initial_sub_org_id_1, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getCloudaccountsWithOrgIdFailed(this.initial_sub_org_id_1, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getReportsWithOrgIdFailed(this.initial_sub_org_id_1, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getAlertsWithOrgIdFailed(this.initial_sub_org_id_1, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
	}
	
	@Test
	public void msp03() throws InterruptedException{
		String errorcode="00100101";
		String error_message="Permission required to manage the resource for current user";
		spogServer.userLogin(this.initial_sub_email_1, this.OrgPwdForPrepare);
		spogServer.getSourcesWithOrgIdFailed(this.initial_direct_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getPoliciesWithOrgIdFailed(this.initial_direct_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getJobsWithOrgIdFailed(this.initial_direct_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getLogsWithOrgIdFailed(this.initial_direct_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getUsersWithOrgIdFailed(this.initial_direct_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getDestinationsWithOrgIdFailed(this.initial_direct_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getCloudaccountsWithOrgIdFailed(this.initial_direct_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getReportsWithOrgIdFailed(this.initial_direct_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getAlertsWithOrgIdFailed(this.initial_direct_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
	}
	
	//@Test
	public void msp04() throws InterruptedException{
		String errorcode="00100101";
		String error_message="Permission required to manage the resource for current user";
		spogServer.userLogin(this.initial_sub_email_1, this.OrgPwdForPrepare);
		//spogServer.getSourcesWithOrgIdFailed(this.initial_sub_org_id_2, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		//spogServer.getPoliciesWithOrgIdFailed(this.initial_sub_org_id_2, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getJobsWithOrgIdFailed(this.initial_sub_org_id_2, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getLogsWithOrgIdFailed(this.initial_sub_org_id_2, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		//spogServer.getUsersWithOrgIdFailed(this.initial_sub_org_id_2, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getDestinationsWithOrgIdFailed(this.initial_sub_org_id_2, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getCloudaccountsWithOrgIdFailed(this.initial_sub_org_id_2, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getReportsWithOrgIdFailed(this.initial_sub_org_id_2, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getAlertsWithOrgIdFailed(this.initial_sub_org_id_2, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
	}
	
	@Test
	public void msp05() throws InterruptedException{
		String errorcode="00100101";
		String error_message="Permission required to manage the resource for current user";
		spogServer.userLogin(this.initial_sub_email_1, this.OrgPwdForPrepare);
		spogServer.getSourcesWithOrgIdFailed(this.initial_msp_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getPoliciesWithOrgIdFailed(this.initial_msp_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getJobsWithOrgIdFailed(this.initial_msp_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getLogsWithOrgIdFailed(this.initial_msp_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getUsersWithOrgIdFailed(this.initial_msp_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getDestinationsWithOrgIdFailed(this.initial_msp_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getCloudaccountsWithOrgIdFailed(this.initial_msp_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getReportsWithOrgIdFailed(this.initial_msp_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
		spogServer.getAlertsWithOrgIdFailed(this.initial_msp_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, errorcode, error_message);
	}
	
	public Response GetLogs(String filterStr,String SortStr){
		ArrayList<HashMap<String,Object>> Logsinfo=new ArrayList<HashMap<String,Object>>();
		String[] filterArray=null;
		String[] filters=null;
		String filtersdata=null;
		String filterName = null,filterOperator = null, filterValue = null;
		ArrayList<HashMap<String,Object>> Logs_info = new ArrayList<>();
		//It is related to the Filter on the single value 
		if(filterStr!=""&&filterStr!=null&&!filterStr.contains(",")){
			filterArray = filterStr.split(";");
			filterName = filterArray[0];filterOperator = filterArray[1];filterValue = filterArray[2];
			Logs_info=spogServer.getLogsInfo(Logsinfo,filterName,filterOperator,filterValue);
			Logsinfo=Logs_info;		

			if(SortStr!=""&&SortStr.contains("log_ts")&&!(filterStr.contains("log_ts"))){
				Logsinfo=spogServer.getLogsTimeSortInfo(SortStr, Logsinfo, test);	
			}
		}//It is related to the filtering on the multiple values 
		else if(filterStr!=""&& filterStr!=null&&filterStr.contains(",")) {
			filters = filterStr.split(",");	
			for(int i=0;i<filters.length;i++){
				filtersdata=filters[i];
				filterArray = filtersdata.split(";");
				filterName = filterArray[0];filterOperator = filterArray[1];filterValue = filterArray[2];		
				Logs_info=spogServer.getLogsInfo(Logsinfo,filterName,filterOperator,filterValue);
				Logsinfo=Logs_info;			
			}			
			if(SortStr!=""&&SortStr.contains("log_ts")&&!(filterStr.contains("log_ts"))){
				Logsinfo=spogServer.getLogsTimeSortInfo(SortStr, Logsinfo, test);	
			}
		}	
		//For the sorting based on log_severity_type and job_type and default is (Log_ts)
		if(SortStr.contains("log_severity_type")||SortStr.contains("job_type")) {
			Logsinfo=spogServer.getLogsSortInfo(SortStr, Logsinfo, test);
		}
		//preparing the URL and validating the Response For 
		String additionalURL=spogServer.PrepareURL(filterStr,SortStr,1, 20, test);	
		Response response=spogServer.getLogs(spogServer.getJWTToken(),additionalURL,test);
		return response;
	}
	
	public void findMessageFromLog(Response res,String job_id,String log_message){
		boolean ret=false;
		int totalsize=res.then().extract().path("pagination.total_size");
		if(totalsize>=1){
			for(int i=0;i<totalsize;i++){
				if(res.then().extract().path("data["+i+"].job_data.job_id").toString().equalsIgnoreCase(job_id)&&(res.then().extract().path("data["+i+"].message").toString().indexOf(log_message)!=-1)){
					ret=true;
					break;
				}
			}			
		}  
		assertTrue(ret,"check log has the given message failed");
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
		rep.endTest(test);
	}
	
}
