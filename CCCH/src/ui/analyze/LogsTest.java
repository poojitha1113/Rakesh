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
import Constants.LogSeverityType;
import Constants.LogSourceType;
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
import ui.spog.server.LogsHelper;
import ui.spog.server.SPOGUIServer;


public class LogsTest extends base.prepare.Is4Org{

	private SPOGServer spogServer;
	private UserSpogServer userSpogServer;
	private SPOGUIServer spogUIServer;
	private Org4SPOGServer org4SpogServer;
	private Source4SPOGServer source4spogServer;
	private GatewayServer gatewayServer;
	private LogsHelper logsHelper;
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
	private String direct_site_token;
	
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
	ArrayList<HashMap<String, Object>> logs = null;
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
			logsHelper = new LogsHelper(browserType, maxWaitTimeSec);
			columnHelper = new ColumnHelper(browserType, maxWaitTimeSec);
			logsHelper.openUrl(uiBaseURI);
			
			logsHelper.login_Spog("test.direct10001+dirs27@gmail.com", "Mclaren@2020");
		}else {
			//Create the organizations and users
			prepareEnv();
			
			logsHelper = new LogsHelper(browserType, maxWaitTimeSec);
			columnHelper = new ColumnHelper(browserType, maxWaitTimeSec);
			logsHelper.openUrl(uiBaseURI);
			logsHelper.login_Spog(final_direct_user_name_email, common_password);	
		}
				
	}
	
	@DataProvider(name="logInfo")
	public Object[][] logInfo()	{
		return new Object[][] {
			{jobNames.get(0), logs.get(0)},
			{jobNames.get(1), logs.get(1)},
			{jobNames.get(2), logs.get(2)},
			{jobNames.get(3), logs.get(3)},
			{jobNames.get(4), logs.get(4)},
			{jobNames.get(5), logs.get(5)},
			{jobNames.get(6), logs.get(6)},
			{jobNames.get(7), logs.get(7)},
			{jobNames.get(8), logs.get(8)},
			{jobNames.get(9), logs.get(9)},
			{jobNames.get(10), logs.get(10)},
			{jobNames.get(11), logs.get(11)},
		};
	}
	
	@Test(/*dataProvider="logInfo", */enabled=true)
	public void logInformatoionTest(/*String jobName, HashMap<String, Object> logInfo*/){
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		
		test.log(LogStatus.INFO, "Validate the log information in UI");
		
		for (int i = 0; i < logs.size(); i++) {
			logsHelper.checkLogInformation(jobNames.get(i), logs.get(i), test);	
		}
		
		test.log(LogStatus.PASS, "All the created logs are displaying in logs page");
				
	}
	
	@DataProvider(name="sortInfo")
	public Object[][] sortInfo(){
		return new Object[][] {
			{"Sort by Start Time in ascending order", "Date", "asc", "log_ts;asc"},
			{"Sort by Start Time in descending order", "Date", "desc", "log_ts;desc"},
			/*{"Sort by Status in ascending order", "Severity", "asc", "log_severity_type;asc"},
			{"Sort by Status in descending order", "Severity", "desc", "log_severity_type;desc"},*/
			{"Sort by Job Type in ascending order", "Job Type", "asc", "job_type;asc"},
			{"Sort by Job Type in descending order", "Job Type", "desc", "job_type;desc"},
			
		};
	}
	@Test(dataProvider="sortInfo")
	public void sortTest(String caseType, String columnName, String sortOrder, String apiSortStr) {
		
		test=ExtentManager.getNewTest(caseType);
		
		logsHelper.goToPage(PageName.LOGS);
		BasePage bp = new BasePage();
		bp.updatePageSizeWithCheck(100, test);
		
		test.log(LogStatus.INFO, "Sort by: "+columnName+" in order:"+sortOrder); 
		logsHelper.sortByColumnNameWithCheck(columnName, sortOrder, test);
		
		test.log(LogStatus.PASS, "Sort by "+columnName+" in order "+sortOrder+" is working");
	}
	
	
	@DataProvider(name="filterInfo")
	public Object[][] filterInfo(){
		return new Object[][] {
			{"Search by source name", sourceNames.get(0), null, "message;=;"+sourceNames.get(0)},
	
			{"Search by Job Type;Backup-Full", null, "Job Type;Backup-Full", "job_type;=;"+JobType4LatestJob.backup_full.name()},
			{"Search by Job Type;Backup-Incremental", null, "Job Type;Backup-Incremental", "job_type;=;"+JobType4LatestJob.backup_incremental.name()},
			{"Search by Job Type;Replication(IN)", null, "Job Type;Replication(IN)", "job_type;=;"+JobType4LatestJob.rps_replicate_in_bound.name()},
			{"Search by Job Type;Replication(OUT)", null, "Job Type;Replication(OUT)", "job_type;=;"+JobType4LatestJob.rps_replicate.name()},
			{"Search by Job Type;Restore", null, "Job Type;Restore", "job_type;=;"+JobType4LatestJob.restore.name()},
			{"Search by Job Type;Policy Deployment", null, "Job Type;Policy Deployment", "job_type;=;"+JobType4LatestJob.deploy_policy_to_cloud_direct.name()},
			{"Search by Job Type;Merge", null, "Job Type;Merge", "job_type;=;"+JobType4LatestJob.rps_merge.name()},
			{"Search by Job Type;Unassign Cloud Direct Policy", null, "Job Type;Unassign Cloud Direct Policy", "job_type;=;"+JobType4LatestJob.undeploy_policy_to_cloud_direct.name()},
			
			/*{"Search by Date Range;Last 24 Hours", null, "Date Range;Last 24 Hours", "job_status;in;waiting"},
			{"Search by Date Range;Last 7 Days", null, "Date Range;Last 7 Days", "job_status;in;waiting"},
			{"Search by Date Range;Last 2 Weeks", null, "Date Range;Last 2 Weeks", "job_status;in;waiting"},
			{"Search by Date Range;Last 1 Month", null, "Date Range;Last 1 Month", "job_status;in;waiting"},
//			{"Search by Date Range;Custom", null, "Date Range;Custom", ""},
*/					
			{"Search by Severity;Information", null, "Severity;Information", "log_severity_type;=;information"},
			{"Search by Severity;Warning", null, "Severity;Warning", "log_severity_type;=;warning"},
			{"Search by Severity;Error", null, "Severity;Error", "log_severity_type;=;error"},
			
		};
	}
	
	@Test(dataProvider="filterInfo")
	public void logSearchTest(String caseType, String sourceName, String filter, String apiFilter) {

		test=ExtentManager.getNewTest(caseType);
		ArrayList<HashMap<String, Object>> expectedInfo = null;			
		ArrayList<String> filters = new ArrayList<>();
		
		expectedInfo= getLogsInfo(apiFilter, null, 1, 20, test);
		
		if (filter != null && !filter.isEmpty()) {
			String[] multipleFilters = filter.split("&");
			for (int i = 0; i < multipleFilters.length; i++) {
				filters.add(multipleFilters[i]);
			}	
		}				

		logsHelper.searchLogWithCheck(sourceName, filters, expectedInfo, test);

		test.log(LogStatus.INFO, "Clear the search infromation");		
		logsHelper.clearSearchWithoutSaving();
		
		test.log(LogStatus.PASS, caseType);
	}

	
	@SuppressWarnings("unchecked")
	public ArrayList<HashMap<String, Object>> getLogsInfo(String filter, String sort, int page, int page_size, ExtentTest test) {
		
			String filterStr = null;
		HashMap<String, Object> logInfo = null;
		ArrayList<HashMap<String, Object>> expectedLogsInfo = new ArrayList<>(); 
		int logCount;
		
		if (filter != null && !filter.isEmpty()) {
			filterStr = "organization_id;=;"+direct_org_id+","+filter;	
		}else {
			filterStr = "organization_id;=;"+direct_org_id;
		}
		
		String additionalURL = spogServer.PrepareURL(filterStr, sort, page, page_size, test);
		Response response = spogServer.getLogs(csr_token, additionalURL, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		
		logCount = ((ArrayList<HashMap<String,Object>>) response.then().extract().path("data")).size();
	
		for (int i = 0; i < logCount; i++) {
			
			logInfo = new HashMap<>();
			
			String date  = convertTimeStampToDateAndTime(Long.parseLong(response.then().extract().path("data["+i+"].log_ts").toString()));			
			logInfo.put(TableHeaders.date, date);
			
			logInfo.put(TableHeaders.severity, response.then().extract().path("data["+i+"].log_severity_type"));
			
			logInfo.put(TableHeaders.source, response.then().extract().path("data["+i+"].job_data.source_name"));
			
			String generatedFrom = response.then().extract().path("data["+i+"].job_data.generated_from");
			if (generatedFrom != null && !generatedFrom.isEmpty()) {
				logInfo.put(TableHeaders.generated_from, generatedFrom);	
			}else {
				logInfo.put(TableHeaders.generated_from, "-");
			}			
			
			String job_type = response.then().extract().path("data["+i+"].job_data.job_type").toString();
			
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
			logInfo.put(TableHeaders.job_type, job_type);
			
			
			
			
			String messageId = response.then().extract().path("data["+i+"].help_message_id");
			if (messageId != null && !messageId.isEmpty()) {
				logInfo.put(TableHeaders.message_id, messageId);	
			}else {
				logInfo.put(TableHeaders.message_id, "-");	
			}
			
			String message = response.then().extract().path("data["+i+"].message");
			if (message != null && !message.isEmpty()) {
				logInfo.put(TableHeaders.message, message);	
			}else {
				logInfo.put(TableHeaders.message, "-");
			}			
			
			logInfo.put(TableHeaders.job_name, response.then().extract().path("data["+i+"].job_data.job_name"));			
			
			sourceNames.add(logInfo.get(TableHeaders.source).toString());
			jobNames.add(logInfo.get(TableHeaders.job_name).toString());
			expectedLogsInfo.add(logInfo);			
		}
		
		return expectedLogsInfo;
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
		direct_site_token=gatewayServer.getJWTToken();
		
		createSourcesAndPostJobs();
		logs = getLogsInfo(null, null,1,20, test);
		
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
				
							String jobType = allJobType[new Random().nextInt(allJobType.length)];
							String jobMethod = allJobMethod[new Random().nextInt(allJobMethod.length)];
							String jobStatus = allJobStatus[new Random().nextInt(allJobStatus.length)];
							
							response = gatewayServer.postJob(System.currentTimeMillis()/1000, (System.currentTimeMillis()/1000)+10, org_id, source_id, source_id, null, UUID.randomUUID().toString(), UUID.randomUUID().toString(), jobType, jobMethod, jobStatus, direct_user_validToken, test);
							spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);
							String job_id = response.then().extract().path("data.job_id");
							
							
							String[] LogSeverity = new String[] { LogSeverityType.information.name(),LogSeverityType.warning.name(),LogSeverityType.error.name()};
							String[] log_message_data = new String[] { "node", srcName};
							String[] logSourceType =  new String[] {LogSourceType.spog.toString(),LogSourceType.udp.toString(),LogSourceType.cloud_direct.toString()};
							String[] log_message_ids = new String[] {"testLogMessage", "connect_node_failed_test_message"};
									
							long log_generate_time = System.currentTimeMillis();
							
							for(int m=0;m<LogSeverity.length;m++){
								
								log_generate_time= log_generate_time+1;
								String log_message_id = log_message_ids[new Random().nextInt(log_message_ids.length)];
								String help_message_id = "Spog@UdpIntegration";
								
								test.log(LogStatus.INFO,"create log for the job");	
								String log_data="";
								for(int p=0;p<log_message_data.length;p++) {
									log_data+=log_message_data[p]+",";
								}		
	
								gatewayServer.createLogwithCheck(log_generate_time, job_id,org_id, org_id, source_id,  LogSeverity[m], sourceProducts[j].toString(),
																	log_message_id, log_data.substring(0,log_data.length()-1),RandomStringUtils.randomNumeric(4), direct_site_token,test);
								
							}
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
		logsHelper.destroy();
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
