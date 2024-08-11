package ui.analyze;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.ConnectionStatus;
import Constants.JobType4LatestJob;
import Constants.OSMajor;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import InvokerServer.GatewayServer;
import InvokerServer.Org4SPOGServer;
import InvokerServer.SPOGServer;
import InvokerServer.Source4SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;
import ui.base.common.BasePage;
import ui.base.common.PageName;
import ui.base.common.TableHeaders;
import ui.spog.server.ColumnHelper;
import ui.spog.server.JobsHelper;
import ui.spog.server.SPOGUIServer;


public class JobsTest extends base.prepare.Is4Org{

	private SPOGServer spogServer;
	private UserSpogServer userSpogServer;
	private SPOGUIServer spogUIServer;
	private Org4SPOGServer org4SpogServer;
	private Source4SPOGServer source4spogServer;
	private GatewayServer gatewayServer;
	private JobsHelper jobsHelper;
	private ColumnHelper columnHelper;
	private String csrAdminUserName;
	private String csrAdminPassword;
	private String csr_token;
	private ExtentTest test;

	/*private ExtentReports rep;
  	private SQLServerDb bqdb1;
  	public int Nooftest;
  	private long creationTime;
  	private String BQName=null;
  	private String runningMachine;
  	private testcasescount count1;
  	private String buildVersion;*/
	
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
	private String direct_user_validToken;
	private String direct_site_id;
	
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
	
	private String  org_model_prefix = "UI_"+this.getClass().getSimpleName();
	ArrayList<HashMap<String, Object>> jobs = null;
	ArrayList<String> jobNames = new ArrayList<>();
	ArrayList<String> sourceNames = new ArrayList<>();

	@BeforeClass
	@Parameters({ "baseURI", "port",   "logFolder", "csrAdminUserName", "csrAdminPassword", "buildVersion", "uiBaseURI", "browserType", "maxWaitTimeSec", "testName"})
	public void beforeClass(String baseURI, String port, String logFolder, String adminUserName, String adminPassword,  String buildVersion,
							String uiBaseURI, String browserType, int maxWaitTimeSec, String testName) throws UnknownHostException {

		org4SpogServer = new Org4SPOGServer(baseURI, port);
		spogServer = new SPOGServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		source4spogServer = new Source4SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		this.csrAdminUserName = adminUserName;
		this.csrAdminPassword = adminPassword;
		rep = ExtentManager.getInstance(testName,logFolder);
		test = rep.startTest("beforeClass");
		
		this.BQName = "UI_"+this.getClass().getSimpleName();
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
			/*try {
				bqdb1.updateTable(this.BQName, runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0", String.valueOf(Nooftest), count1.getcreationtime(), "InProgress",author+" and Rest server is "+baseURI.split("//")[1]);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}*/
		}

		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
		csr_token = spogServer.getJWTToken();
		
		if (uiBaseURI.contains("lccitc")) {
			jobsHelper = new JobsHelper(browserType, maxWaitTimeSec);
			columnHelper = new ColumnHelper(browserType, maxWaitTimeSec);
			jobsHelper.openUrl(uiBaseURI);
			
			jobsHelper.login_Spog("test.direct10001+dirs27@gmail.com", "Mclaren@2020");
		}else {
			//Create the organizations and users
			prepareEnv();
			
			jobsHelper = new JobsHelper(browserType, maxWaitTimeSec);
			columnHelper = new ColumnHelper(browserType, maxWaitTimeSec);
			jobsHelper.openUrl(uiBaseURI);
			jobsHelper.login_Spog(final_direct_user_name_email, common_password);	
		}
		
		jobsHelper.navigateToJobsPageAndEnableAllColumns();
		
	}
	
	@DataProvider(name="jobInfo")
	public Object[][] jobInfo()	{
		return new Object[][] {
			{jobNames.get(0), jobs.get(0)},
			{jobNames.get(1), jobs.get(1)},
			{jobNames.get(2), jobs.get(2)},
			{jobNames.get(3), jobs.get(3)},
			{jobNames.get(4), jobs.get(4)},
			{jobNames.get(5), jobs.get(5)},
			{jobNames.get(6), jobs.get(6)},
			{jobNames.get(7), jobs.get(7)},
			{jobNames.get(8), jobs.get(8)},
			{jobNames.get(9), jobs.get(9)},
			{jobNames.get(10), jobs.get(10)},
			{jobNames.get(11), jobs.get(11)},
		};
	}
	
	@Test(enabled=true)
	public void jobInformatoionTest(String jobName, HashMap<String, Object> jobInfo){
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		
		
		test.log(LogStatus.INFO, "Validate the job information in UI");
		jobsHelper.checkJobInformation(jobName, jobInfo, test);		
	}
	
	@DataProvider(name="sortInfo")
	public Object[][] sortInfo(){
		return new Object[][] {
			{"Sort by Start Time in ascending order", "Start Time", "asc", "start_time_ts;asc"},
			{"Sort by Start Time in descending order", "Start Time", "desc", "start_time_ts;desc"},
			{"Sort by Status in ascending order", "Status", "asc", "job_status;asc"},
			{"Sort by Status in descending order", "Status", "desc", "job_status;desc"},
			{"Sort by Job Type in ascending order", "Job Type", "asc", "job_type;asc"},
			{"Sort by Job Type in descending order", "Job Type", "desc", "job_type;desc"},
			
		};
	}
	
	
	@Test(dataProvider="sortInfo")
	public void sortTest(String caseType, String columnName, String sortOrder, String apiSortStr) {
		
		test=ExtentManager.getNewTest(caseType);
				
//		test.log(LogStatus.INFO, "Call GET: /sources api with sort to get validate the content in UI");
//		ArrayList<HashMap<String, Object>> expectedInfo = getSourcesInfo(null, apiSortStr, test);
		
//		columnHelper.sortByColumnName(columnName, sortOrder);
		
		jobsHelper.goToPage(PageName.JOBS);
		BasePage bp = new BasePage();
		bp.updatePageSizeWithCheck(100, test);
		
		test.log(LogStatus.INFO, "Sort by: "+columnName+" in order:"+sortOrder); 
		columnHelper.sortByColumnNameWithCheck(columnName, sortOrder, test);
	}
	
	@DataProvider(name="filterInfo")
	public Object[][] filterInfo(){
		return new Object[][] {
			{"Search by source name", sourceNames.get(0), null, "source_name;=;"+sourceNames.get(0)},
	
			{"Search by Job Type;Backup-Full", null, "Job Type;Backup-Full", "job_type;=;"+JobType4LatestJob.backup_full.name()},
			{"Search by Job Type;Backup-Incremental", null, "Job Type;Backup-Incremental", "job_type;=;"+JobType4LatestJob.backup_incremental.name()},
			{"Search by Job Type;Replication(IN)", null, "Job Type;Replication(IN)", "job_type;=;"+JobType4LatestJob.rps_replicate_in_bound.name()},
			{"Search by Job Type;Replication(OUT)", null, "Job Type;Replication(OUT)", "job_type;=;"+JobType4LatestJob.rps_replicate.name()},
			{"Search by Job Type;Restore", null, "Job Type;Restore", "job_type;=;"+JobType4LatestJob.restore.name()},
			{"Search by Job Type;Policy Deployment", null, "Job Type;Policy Deployment", "job_type;=;"+JobType4LatestJob.deploy_policy_to_cloud_direct.name()},
			{"Search by Job Type;Merge", null, "Job Type;Merge", "job_type;=;"+JobType4LatestJob.rps_merge.name()},
			{"Search by Job Type;Unassign Cloud Direct Policy", null, "Job Type;Unassign Cloud Direct Policy", "job_type;=;"+JobType4LatestJob.undeploy_policy_to_cloud_direct.name()},


			{"Search by job status finished", null, "Status;Finished", "job_status;=;finished"},
			{"Search by job status In progress", null, "Status;In progress", "job_status;=;active"},
			{"Search by job status Canceled", null, "Status;Canceled", "job_status;=;canceled"},
			{"Search by job status Failed", null, "Status;Failed", "job_status;=;failed"},
			{"Search by job status Warning", null, "Status;Warning", "job_status;=;warning"},
			{"Search by job status Skipped", null, "Status;Skipped", "job_status;=;skipped"},
			{"Search by job status Stopped", null, "Status;Stopped", "job_status;=;stop"},
			{"Search by job status Waiting", null, "Status;Waiting", "job_status;=;waiting"},
			
			{"Search by Date Range;Last 24 Hours", null, "Date Range;Last 24 Hours", "job_status;in;waiting"},
			{"Search by Date Range;Last 7 Days", null, "Date Range;Last 7 Days", "job_status;in;waiting"},
			{"Search by Date Range;Last 2 Weeks", null, "Date Range;Last 2 Weeks", "job_status;in;waiting"},
			{"Search by Date Range;Last 1 Month", null, "Date Range;Last 1 Month", "job_status;in;waiting"},
//			{"Search by Date Range;Custom", null, "Date Range;Custom", "last_job;in;waiting"},
			
			
		};
	}
	
	@Test(dataProvider="filterInfo")
	public void jobSearchTest(String caseType, String sourceName, String filter, String apiFilter) {

		test=ExtentManager.getNewTest(caseType);
		ArrayList<HashMap<String, Object>> expectedInfo = null;			
		ArrayList<String> filters = new ArrayList<>();
		
		expectedInfo= getJobsInfo(apiFilter, null, test);
		
		if (filter != null && !filter.isEmpty()) {
			String[] multipleFilters = filter.split("&");
			for (int i = 0; i < multipleFilters.length; i++) {
				filters.add(multipleFilters[i]);
			}	
		}				

		jobsHelper.searchJobWithCheck(sourceName, filters, expectedInfo, test);
		
		jobsHelper.clearSearchWithoutSaving();
	}

	
	@DataProvider(name="manageSaveSearchInfo")
	public Object[][] manageSaveSearchInfo(){
		
		return new Object[][] {
			
			/*{"Create a save search with filters: Job Status;In progress,Job Type;Backup-Full and update with filters: Job Status;In progress|Finished,Job Type;Backup-Full,Backup-Incremental",
				spogServer.ReturnRandom("ss"), spogServer.ReturnRandom("ss"), null, false,
				"Status;In progress,Job Type;Backup-Full",												//UI filter
				"job_status;=;active,job_type;=;backup_full",			// api filter
				"Status;In progress|Finished,Job Type;Backup-Full,Backup-Incremental",						//UI update filter
				"job_status;=;active|finished,job_type;=;backup_full|backup_incremental"}, //api update filter
			*/
			
			{"Create a save search with filters: Job Status;Failed,Job Type;Replication(IN) and update with filters: Job Status;Finished|Failed,Job Type;Replication(IN)|Replication(OUT)",
					spogServer.ReturnRandom("ss"), spogServer.ReturnRandom("ss"), null, false,
					"Status;Failed,Job Type;Replication(IN)",												//UI filter
					"job_status;=;Failed,job_type;=;rps_replicate_in_bound",			// api filter
					"Status;Finished|Failed,Job Type;Replication(IN)|Replication(Out)",						//UI update filter
					"job_status;=;failed|finished,job_type;=;rps_replicate_in_bound|rps_replicate"}, //api update filter
			
			
			{"Create a save search with filters: Job Status;Warning|Canceled,Job Type;Restore and update with filters: Job Status;Warning|Failed|Canceled,Job Type;Restore|Merge",
						spogServer.ReturnRandom("ss"), spogServer.ReturnRandom("ss"), null, false,
						"Status;Warning|Canceled,Job Type;Restore",												//UI filter
						"job_status;=;warning|canceled,job_type;=;restore",			// api filter
						"Status;Warning|Failed|Canceled,Job Type;Restore|Merge",						//UI update filter
						"job_status;=;failed|warning|canceled,job_type;=;restore|rps_merge"},  //api update filter
			
		};
	}
	
	@Test(dataProvider="manageSaveSearchInfo")
	public void manageSaveSearchTest(String caseType,
										String searchName,
										String newSearchName,
										String search_string,
										boolean makeDefault,
										String filter,
										String apiFilter,
										String updateFilter,
										String apiUpdateFilter
										) {
		
		test=ExtentManager.getNewTest(caseType);
		ArrayList<HashMap<String, Object>> expectedInfo = null;
		ArrayList<String> filters = null;
		ArrayList<String> updatefilters = null;
		
		filters = getFilterArray(filter);	
		jobsHelper.saveSearchWithCheck(searchName, search_string, filters, test);
		jobsHelper.applySaveSearchWithCheck(searchName, test);
		
		test.log(LogStatus.INFO, "validate the savesearch info after creating");
		expectedInfo = getJobsInfo(apiFilter, null, test);
		jobsHelper.validateJobsInfomation(expectedInfo, test);
				
		test.log(LogStatus.INFO, "Modify the save search");
		updatefilters = getFilterArray(updateFilter);
		jobsHelper.manageSaveSearchWithCheck(searchName, newSearchName, search_string, makeDefault, updatefilters, test);
		
		if (!(newSearchName != null && !newSearchName.isEmpty())) {
			newSearchName = searchName;
		}
		jobsHelper.applySaveSearchWithCheck(newSearchName, test);
		
		test.log(LogStatus.INFO, "validate the savesearch info after updating");
		expectedInfo = getJobsInfo(apiUpdateFilter, null, test);
		jobsHelper.validateJobsInfomation(expectedInfo, test);		
		
		jobsHelper.deleteSaveSearchWithCheck(newSearchName, test);	
		
	}
	
	@DataProvider(name="makeSaveSearchDefaultInfo")
	public Object[][] makeSaveSearchDefaultInfo(){
		return new Object[][] {
			{"Create save search and make it default with params Job Type;Backup-Full,Backup-Incremental&Status;Finished,Canceled,Skipped", spogServer.ReturnRandom("as"), "searchstring", true, 
				"Job Type;Backup-Full,Backup-Incremental&Status;Finished,Canceled,Skipped"}
		};
	}
	
	@Test(dataProvider="makeSaveSearchDefaultInfo")
	public void makeSavedSearchDefault(String caseType,
										String searchName,
										String search_string,
										boolean makeDefault,
										String filter
										) {
		
		test=ExtentManager.getNewTest(caseType);
		ArrayList<String> filters = new ArrayList<>();
		
		if (filter != null && !filter.isEmpty()) {
			String[] multipleFilters = filter.split("&");
			for (int i = 0; i < multipleFilters.length; i++) {
				filters.add(multipleFilters[i]);
			}	
		}		
		jobsHelper.saveSearchWithCheck(searchName, search_string, filters, test);

		jobsHelper.makeSaveSearchDefaultWithCheck(searchName, makeDefault, test);
		jobsHelper.deleteSaveSearchWithCheck(searchName, test);
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<HashMap<String, Object>> getJobsInfo(String filter, String sort, ExtentTest test) {
		
		String filterStr = null;
		HashMap<String, Object> jobInfo = null;
		ArrayList<HashMap<String, Object>> expectedJobsInfo = new ArrayList<>(); 
		int jobCount;
		
		if (filter != null && !filter.isEmpty()) {
			filterStr = "organization_id;=;"+direct_org_id+","+filter;	
		}else {
			filterStr = "organization_id;=;"+direct_org_id;
		}
		
		String additionalURL = spogServer.PrepareURL(filterStr, sort, 1, 20, test);
		Response response = spogServer.getJobs(csr_token, additionalURL, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		
		jobCount = ((ArrayList<HashMap<String,Object>>) response.then().extract().path("data")).size();
	
		for (int i = 0; i < jobCount; i++) {
			
			jobInfo = new HashMap<>();
			
			jobInfo.put(TableHeaders.source, response.then().extract().path("data["+i+"].source_name"));
			jobInfo.put(TableHeaders.job_name, response.then().extract().path("data["+i+"].job_name"));
			
			String endTime = convertTimeStampToDateAndTime(Long.parseLong(response.then().extract().path("data["+i+"].end_time_ts").toString()));			
			jobInfo.put(TableHeaders.end_time, endTime);
			
			String startTime = convertTimeStampToDateAndTime(Long.parseLong(response.then().extract().path("data["+i+"].start_time_ts").toString()));			
			jobInfo.put(TableHeaders.start_time, startTime);
			
			jobInfo.put(TableHeaders.destination, response.then().extract().path("data["+i+"].destination_name"));
			jobInfo.put(TableHeaders.policy, response.then().extract().path("data["+i+"].policy.policy_name"));
			
			//change case
			jobInfo.put(TableHeaders.status, response.then().extract().path("data["+i+"].job_status"));
			
			String job_type = response.then().extract().path("data["+i+"].job_type").toString();
			
			if (job_type.equals(JobType4LatestJob.backup_full.toString())) {
					job_type = "Backup-Full";
				}else if (job_type.equals(JobType4LatestJob.backup_incremental.toString())) {
					job_type = "Backup-Incremental";
				}else if (job_type.equals(JobType4LatestJob.restore.toString())) {
					job_type = "Restore";
				}else if (job_type.equals(JobType4LatestJob.rps_replicate_in_bound.toString())) {
					job_type = "Replication(IN)";
				}else if (job_type.equals(JobType4LatestJob.rps_replicate.toString())) {
					job_type = "Replication(Out)";
				}else if (job_type.equals(JobType4LatestJob.rps_merge.toString())) {
					job_type = "Merge";
				}
			jobInfo.put(TableHeaders.job_type, job_type);
			
			int durationInSecs = response.then().extract().path("data["+i+"].duration");
			jobInfo.put(TableHeaders.duration, durationConverter(durationInSecs));
			
			sourceNames.add(jobInfo.get(TableHeaders.source).toString());
			jobNames.add(jobInfo.get(TableHeaders.job_name).toString());
			expectedJobsInfo.add(jobInfo);
			
		}
		
		return expectedJobsInfo;
	}
	
	private void prepareEnv(){

		//************************create direct organization and users*************************************

		String prefix = RandomStringUtils.randomAlphanumeric(8);
		spogServer.setToken(csr_token);
		this.final_direct_user_name_email = prefix+this.direct_user_name_email;
		direct_org_id = spogServer.CreateOrganizationWithCheck(prefix+direct_org_name+org_model_prefix, SpogConstants.DIRECT_ORG, this.final_direct_user_name_email, common_password, prefix+direct_user_first_name, prefix+direct_user_last_name,test);
		spogServer.userLogin(this.final_direct_user_name_email, common_password);
		test.log(LogStatus.INFO,"Getting the JWTToken for the Logged in user");
		direct_user_validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The token is :"+ direct_user_validToken);
		direct_user_id = spogServer.GetLoggedinUser_UserID();
		
		test.log(LogStatus.INFO,"Creating a site For direct org and logging and registering and logging a site ");
		direct_site_id = gatewayServer.createsite_register_login(direct_org_id, direct_user_validToken, direct_user_id, "rakesh", "1.0.0", spogServer, test);
		test.log(LogStatus.INFO,"The direct_site_id:"+direct_site_id);
		
		createSourcesAndPostJobs();
		jobs = getJobsInfo(null, null, test);
	}
	
	@SuppressWarnings("unchecked")
	public void createSourcesAndPostJobs() {
		
		String org_id = direct_org_id;
		String site_id = direct_site_id;
		SourceType[] sourceTypes = {SourceType.machine, SourceType.agentless_vm};
		SourceProduct[] sourceProducts = {SourceProduct.udp, SourceProduct.cloud_direct};
		ProtectionStatus[] protectionStatus = {/*ProtectionStatus.protect,*/ ProtectionStatus.unprotect/*, ProtectionStatus.partial_protect*/};
		ConnectionStatus[] connectionStatus = {ConnectionStatus.online, ConnectionStatus.offline};
		String[] os = {OSMajor.windows.name(), OSMajor.linux.name(), OSMajor.mac.name()};
		
		String[] allJobType = {"backup", "restore", "rps_merge", "rps_replicate", "rps_replicate_in_bound"};
		String[] allJobMethod = {"full","incremental"};
		String[] allJobStatus = {/*"active",*/ "finished", "canceled", "failed", "waiting", "skipped", "stop", "missed"};
		
		
		
		for (int i = 0; i < sourceTypes.length; i++) {
			for (int j = 0; j < sourceProducts.length; j++) {
				for (int j2 = 0; j2 < protectionStatus.length; j2++) {
					for (int k = 0; k < connectionStatus.length; k++) {
						for (int k2 = 0; k2 < os.length; k2++) {
							
							String srcName =spogServer.ReturnRandom("src"); 
							
							Response response = spogServer.createSource(srcName, sourceTypes[i], sourceProducts[j], org_id, site_id, protectionStatus[j2], connectionStatus[k], os[k2], "", test);
							spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);
							
							String source_id = response.then().extract().path("data.source_id");
							
							//sourceIds.add(source_id);
							
							String jobType = allJobType[new Random().nextInt(allJobType.length)];
							String jobMethod = allJobMethod[new Random().nextInt(allJobMethod.length)];
							String jobStatus = allJobStatus[new Random().nextInt(allJobStatus.length)];
							
							gatewayServer.postJob(System.currentTimeMillis()/1000, (System.currentTimeMillis()/1000)+10, org_id, source_id, source_id, null, UUID.randomUUID().toString(), UUID.randomUUID().toString(), jobType, jobMethod, jobStatus, csr_token, test);
						}
					}
				}
			}
		}
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
		jobsHelper.destroy();
		recycleVolumeInCDandDestroyOrg(org_model_prefix);
	}

	/*****************************Generic**********************************/
	private static String convertTimeStampToDateAndTime(long timeStamp) {
			
		Date dateTime = new Date(timeStamp * 1000L );
		
		SimpleDateFormat dateFormater = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
		dateFormater.setTimeZone(TimeZone.getTimeZone("IST"));
		String date = dateFormater.format(dateTime);
		
		date = date.replaceFirst("^* 0*", " ");
		
		return date;
	}
	
	private static ArrayList<String> getFilterArray(String filter) {
		
		ArrayList<String> filters = new ArrayList<>();
		
		if (filter != null && !filter.isEmpty()) {
			String[] multipleFilters = filter.split(",");
			for (int i = 0; i < multipleFilters.length; i++) {
				filters.add(multipleFilters[i]);
			}	
		}	
		
		return filters;
	}
	
	private static String durationConverter(int seconds) {
		
		String result = "";

		if (seconds == 0) {
			return "0 seconds";
		}
		
		LocalTime time = LocalTime.MIN.plusSeconds(seconds);
		
		String[] fullTime = time.toString().split(":");
		String h = fullTime[0];
		String m = fullTime[1];
		String s = fullTime[2];
		
		h = h.replaceFirst("^0*", "");
		m = m.replaceFirst("^0*", "");
		s = s.replaceFirst("^0*", "");
		
		if (!h.isEmpty()) {
			if (Integer.parseInt(h) == 1) {
				result += h+" hour ";
			}else {
				result += h+" hours ";
			}
		}
		
		if (!m.isEmpty()) {
			if (Integer.parseInt(m) == 1) {
				result += m+" minute ";
			}else {
				result += m+" minutes ";
			}
		}
		
		if (!s.isEmpty()) {
			if (Integer.parseInt(s) == 1) {
				result += s+" second ";
			}else {
				result += s+" seconds ";
			}
		}
		
		return result.trim();
	}
	
	public static void main(String[] args) {
		convertTimeStampToDateAndTime(1550485148);
	}
	
}
