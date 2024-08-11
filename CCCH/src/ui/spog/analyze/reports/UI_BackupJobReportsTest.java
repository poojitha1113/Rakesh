package ui.spog.analyze.reports;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.DestinationStatus;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import Constants.volume_type;
import InvokerServer.GatewayServer;
import InvokerServer.Org4SPOGServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGReportServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;
import ui.base.common.TableHeaders;
import ui.spog.server.BackupjobreportsHelperPage;

import ui.spog.server.CustomerAccountsPageHelper;
import ui.spog.server.SPOGUIServer;



/**
 * Created on demand backpup jobs
 * Created Scheduled backup jobs
 * Validated onDemand reports in the Table of reports page
 * Validated Shculed reports in the table of Scheduled reports
 * Deleted the on demand reports 
 * Deleted scheduled reports
 * 
 * @author Nagamalleswari.Sykam
 *
 */
public class UI_BackupJobReportsTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private	SPOGReportServer spogreportserver;
	private BackupjobreportsHelperPage backupjobreportsHelperPage;
	private UserSpogServer userSpogServer;
	private SPOGUIServer spogUIServer;
	private CustomerAccountsPageHelper customerAccountsPageHelper;
	private String csrAdminUserName;
	private String csrAdminPassword;
	private ExtentTest test;



	private GatewayServer gatewayServer;

	private Policy4SPOGServer policy4SpogServer; 
	private SPOGDestinationServer spogDestinationServer;
	private SPOGReportServer spogreportServer;

	/*private ExtentReports rep;
  private SQLServerDb bqdb1;
  public int Nooftest;
  private long creationTime;
  private String BQName=null;
  private String runningMachine;
  private testcasescount count1;
  private String buildVersion;*/
	private String url;

	private String postfix_email = "@arcserve.com";
	private String common_password = "Mclaren@2016";

	private String prefix_direct = "SPOG_QA_MALLESWARI_BQ_DIRECT_ORG";
	private String direct_org_name = prefix_direct + "_org";
	private String direct_org_email = direct_org_name + postfix_email;
	private String direct_org_first_name = direct_org_name + "_first_name";
	private String direct_org_last_name = direct_org_name + "_last_name";
	private String direct_user_name = prefix_direct + "_admin";
	private String direct_user_name_email = prefix_direct + "_admin" + postfix_email;
	private String direct_user_first_name = direct_user_name + "_first_name";
	private String direct_user_last_name = direct_user_name + "_last_name";
	private String direct_user_validToken,direct_org_id;

	private String prefix_msp = "spog_qa_malleswari_msp";
	private String msp_org_name = prefix_msp + "_org";
	private String msp_user_name = prefix_msp + "_admin";
	private String msp_user_name_email = prefix_msp + "_admin" + postfix_email;
	private String msp_user_first_name = msp_user_name + "_first_name";
	private String msp_user_last_name = msp_user_name + "_last_name";
	private String msp_org_id=null;
	private String final_msp_user_name_email=null;	  

	private String account_id;
	private String account_user_email;
	private String direct_user_id;
	private String msp_user_id;
	private String account_user_id;
	private Org4SPOGServer org4SpogServer;
	private String  org_model_prefix=this.getClass().getSimpleName();
	private String suborg_user_validToken,validToken,direct_site_id,direct_site_token,direct_organization_id,Direct_cloud_id,source_id,msp_user_validToken,destination_id_direct;

	String ReportName = "Backup_UI_Schedule"+RandomStringUtils.randomAlphanumeric(2);
	String[] timeInHours =  /*{"00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15",}{"16","17","18","19","20","21","22","23",*/{"23"};
	String[] timeInMinutes =  /*{"00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31","32","33","34","35","36","37","38","39","40","41","42","43","44","45","46","47","48","49",}{"50","51","52","53","54","55","56",*/{"57","58","59"};
	//String [] timeTypes = {"PM"};//"AM"};
	String [] frequecny = {"Daily"};//,"Weekly","Monthly"};

	ArrayList<String> soucres = new ArrayList<String>();
	ArrayList<String> policyName = new ArrayList<>();
	ArrayList<String> sourceGroups = new ArrayList<>();
	
	
	@BeforeClass
	@Parameters({ "baseURI", "port",   "logFolder", "csrAdminUserName", "csrAdminPassword", "buildVersion", "uiBaseURI", "browserType", "maxWaitTimeSec"})
	public void beforeClass(String baseURI, String port, String logFolder, String adminUserName, String adminPassword,  String buildVersion,
			String uiBaseURI, String browserType, int maxWaitTimeSec) throws UnknownHostException {

		org4SpogServer = new Org4SPOGServer(baseURI, port);
		spogServer = new SPOGServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		spogreportserver = new SPOGReportServer(baseURI, port);
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		policy4SpogServer=new Policy4SPOGServer(baseURI,port); 
		spogDestinationServer=	new SPOGDestinationServer(baseURI,port); 

		this.url = uiBaseURI;
		this.csrAdminUserName = adminUserName;
		this.csrAdminPassword = adminPassword;
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(),logFolder);
		test = rep.startTest("beforeClass");

		//prepareEnv();

		backupjobreportsHelperPage = new BackupjobreportsHelperPage(browserType, maxWaitTimeSec);
		backupjobreportsHelperPage.openUrl(uiBaseURI);

		this.BQName = this.getClass().getSimpleName()+"_"+"Malleswari";
		String author = "mallleswari.sykam";
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//backupjobreportsHelperPage.login_Spog(direct_org_email, common_password);
		backupjobreportsHelperPage.login_Spog("eswari.sykam102+s35sub@gmail.com", "Mclaren@2016");


	}

	//@BeforeMethod
	@Parameters({"uiBaseURI", "browserType", "maxWaitTimeSec"})
	public void login(String uiBaseURI, String browserType, int maxWaitTimeSec) {
		//		brandingpageHelper = new BrandingpageHelper(browserType, maxWaitTimeSec);
		//		brandingpageHelper.openUrl(uiBaseURI);
		//backupjobreportsHelperPage.login_Spog("malleswari.sykam101@gmail.com", "Mclaren@2016");


		//nop37xuyspogqa_mallleswari_msp_admin@arcserve.com
		//brandingpageHelper.login_Spog("nop37xuyspogqa_mallleswari_msp_admin@arcserve.com", common_password);
	}
	@DataProvider(name = "OnDemandbackupreports-info")
	public final Object[][] BackupjobsInfo() {
		return new Object[][] { 	

			{"Success",direct_org_email, common_password,"UI_Ondemand_Backup_Last24hour",new String[] {"eswari@gmail.com"},"allSources","ondemand","Last 24 Hours","Report created successfully."},
			{"Success",direct_org_email, direct_org_email,"UI_Ondemand_Backup_Last7days",new String[] {"eswari@gmail.com"},"allSources","ondemand","Last 7 Days","Report created successfully."},
			{"Success",direct_org_email,direct_org_email,"UI_Ondemand_Backup_Last1Month",new String[] {"eswari@gmail.com"},"allSources","ondemand","Last 1 Month","Report created successfully."},
		};
	}
	@Test(dataProvider = "OnDemandbackupreports-info", enabled=true)
	public void CreateOnDemandBackupjobreports(String caseType,
			String Username,String password,
			String ReportName,
			String [] recepientMail,
			String reportSoucresType,
			String Genratetype,
			String Datarange,
			String toastMessage
			)
	{

		test=ExtentManager.getNewTest(caseType);
		backupjobreportsHelperPage.createOnDemandBackupJobReport(ReportName,recepientMail,reportSoucresType,Genratetype,Datarange,toastMessage,test);
		backupjobreportsHelperPage.checkreportnameinreportsTable(ReportName, test);
		backupjobreportsHelperPage.deleteReport(ReportName, "Report deleted successfully.", test);

	}
	@DataProvider(name = "backupreportsschedule-info")
	public final Object[][] ScheduleBackupjobsInfo() {
		return new Object[][] { 	
			{"UI-Automation :Create scheduleReports ",new String[] {"nagamalleswari.sykam@arcserve.com"},"allSources","Schedule","Report schedule created successfully.","Schedule report deleted successfully."}

		};
	}
	@Test(dataProvider = "backupreportsschedule-info", enabled=true)
	public void CreatescheduleBackupjobreports(String caseType,

			String [] recepientMail,
			String reportSoucresType,
			String Generatetype,
			String toastMessage,
			String DeletedReportToastMessgae
			)
	{
		test=ExtentManager.getNewTest(caseType);
		for(int i=0;i<timeInHours.length;i++) {
			for(int j=0;j<timeInMinutes.length;j++) {
				for(int k=0;k<frequecny.length;k++) {

					String ReportName =  "Backup" +"UI"+RandomStringUtils.randomAlphanumeric(2)+ "_"+timeInHours[i]+ "_"+timeInMinutes[j]+"_"+ frequecny[k];

					backupjobreportsHelperPage.createScheduleBackupJobreports(ReportName,recepientMail, reportSoucresType, Generatetype, timeInHours[i], timeInMinutes[j], frequecny[k], toastMessage,test);
					backupjobreportsHelperPage.checkreportnameInManageReportScheduleReportsTable(ReportName, test);
					backupjobreportsHelperPage.deletereportByNameinManagedReportSchedulesTable(ReportName, DeletedReportToastMessgae, test);

				}
			}

		}

	}

	@DataProvider(name = "Custom-info")
	public final Object[][] CustomReportsInfo() {
		return new Object[][] { 	
			//BackupJobs Reports 
			//Valid DateRanges
			{"Valid Strat and End DataRanges to create Cutsom reports for BackupJobs ","Backup_UI_Schedule",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2018","Dec","20"},new String[]{"2019","Mar","1"},"Report created successfully."},
			{"Valid Strat and End DataRanges to create Cutsom reports for BackupJobs","Backup_UI_Schedule",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2018","Dec","15"},new String[]{"2018","Feb","9"},"Report created successfully."},
			{"Valid Strat and End DataRanges to create Cutsom reports for BackupJobs","Backup_UI_Schedule",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2019","Feb","21"},new String[]{"2019","Mar","1"},"Report created successfully."},
			{"Valid Strat and End DataRanges to create Cutsom reports for BackupJobs","Backup_UI_Schedule",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2019","Feb","8"},new String[]{"2019","Mar","2"},"Report created successfully."},
			{"Valid Strat and End DataRanges to create Cutsom reports for BackupJobs","Backup_UI_Schedule",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2019","Jan","8"},new String[]{"2019","Jan","9"},"Report created successfully."},

			//Invalid DateRanges
			{"InValid Start DateRange Year to Cutsom reports for BackupJobs ","Backup_UI_Schedule",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2019","Dec","20"},new String[]{"2020","Mar","1"},"Report created successfully."},
			{"InValid End DateRange Year to Cutsom reports for BackupJobs ","Backup_UI_Schedule",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2018","Dec","20"},new String[]{"2020","Mar","1"},"Report created successfully."},
			{"InValid Start DateRange Month to Cutsom reports for BackupJobs ","Backup_UI_Schedule",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2018","Jan","20"},new String[]{"2019","Mar","1"},"Report created successfully."},
			{"InValid End DateRange Month to Cutsom reports for BackupJobs ","Backup_UI_Schedule",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2018","Dec","20"},new String[]{"2020","Jul","1"},"Report created successfully."},
			{"InValid Start DateRange Date to Cutsom reports for BackupJobs ","Backup_UI_Schedule",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2018","Dec","11"},new String[]{"2019","Mar","1"},"Report created successfully."},
			{"InValid End DateRange Date to Cutsom reports for BackupJobs ","Backup_UI_Schedule",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2018","Dec","20"},new String[]{"2020","Mar","31"},"Report created successfully."},
		};
	}
	@Test(dataProvider = "Custom-info", enabled=true)
	public void Customreports(String Secnario,
			String reportName,
			String []recepientMail,
			String generatetype,
			String reportSoucresType,
			String []startDataRange,
			String[] endDataRange,
			String toastMessage

			)
	{

		test=ExtentManager.getNewTest(Secnario);
		//Login to the could console 
		//	ReportHelperPage.login_Spog(Username, password);

		backupjobreportsHelperPage.createCustomReportsForBackupJobs(reportName, recepientMail, reportSoucresType, generatetype, startDataRange, endDataRange, toastMessage,test);
		backupjobreportsHelperPage.deleteReport(reportName, "Report deleted successfully.", test);

	}
	@DataProvider(name = "DeleteScheduleInstances-info")
	public final Object[][] DeleteScheduleInfo() {
		return new Object[][] { 	
			//BackupJobs Reports 
			{"UI-Automation :Monthly schedule report  ","Backup_UI_Schedule"+RandomStringUtils.randomAlphanumeric(2),new String[] {"123@gmail.com"},"allSources","Schedule","10","30","Monthly","Report schedule created successfully.","Schedule report deleted successfully."},
			{"UI-Automation :Monthly schedule report  ","Backup_UI_Schedule"+RandomStringUtils.randomAlphanumeric(2),new String[] {"123@gmail.com"},"allSources","Schedule","11","30","Monthly","Report schedule created successfully.","Schedule report deleted successfully."},
			{"UI-Automation :Monthly schedule report  ","Backup_UI_Schedule"+RandomStringUtils.randomAlphanumeric(2),new String[] {"123@gmail.com"},"allSources","Schedule","12","30","Monthly","Report schedule created successfully.","Schedule report deleted successfully."},


			{"UI-Automation :Weekly schedule report ","Backup_UI_Schedule"+RandomStringUtils.randomAlphanumeric(2),new String[] {"123@gmail.com"},"allSources","Schedule","13","30","Weekly","Report schedule created successfully.","Schedule report deleted successfully."},
			{"UI-Automation :Weekly schedule report ","Backup_UI_Schedule"+RandomStringUtils.randomAlphanumeric(2),new String[] {"123@gmail.com"},"allSources","Schedule","14","30","Weekly","Report schedule created successfully.","Schedule report deleted successfully."},
			{"UI-Automation :Weekly schedule report ","Backup_UI_Schedule"+RandomStringUtils.randomAlphanumeric(2),new String[] {"123@gmail.com"},"allSources","Schedule","12:","30","Weekly","Report schedule created successfully.","Schedule report deleted successfully."},

			{"UI-Automation :Daily schedule report  ","Backup_UI_Schedule"+RandomStringUtils.randomAlphanumeric(2),new String[] {"123@gmail.com"},"allSources","Schedule","15","30","Daily","Report schedule created successfully.","Schedule report deleted successfully."},
			{"UI-Automation :Daily schedule report ","Backup_UI_Schedule"+RandomStringUtils.randomAlphanumeric(2),new String[] {"123@gmail.com"},"allSources","Schedule","16","30","Daily","Report schedule created successfully.","Schedule report deleted successfully."},
			{"UI-Automation :Daily schedule report  ","Backup_UI_Schedule"+RandomStringUtils.randomAlphanumeric(2),new String[] {"123@gmail.com"},"allSources","Schedule","17","30","Daily","Report schedule created successfully.","Schedule report deleted successfully."},

		};
	}
	@Test(dataProvider = "DeleteScheduleInstances-info", enabled=true)
	public void deleteReporInstancesofBackupJobReports(String caseType,
			String reportName,
			String []recepientMail,
			String reportSoucresType,
			String generatetype,
			String delivaryTimeInhours,
			String delivaryTimeInminutes,
			String frequency,
			String toastMessage,
			String deletedReportToastMessgae
			)
	{

		test=ExtentManager.getNewTest(caseType+delivaryTimeInhours+delivaryTimeInminutes);
		//Login to the could console 
		//	ReportHelperPage.login_Spog(Username, password);

		backupjobreportsHelperPage.createScheduleBackupJobreports(reportName, recepientMail, reportSoucresType, generatetype, delivaryTimeInhours, delivaryTimeInminutes, frequency, toastMessage, test);
		backupjobreportsHelperPage.checkreportnameInManageReportScheduleReportsTable(reportName, test);
		backupjobreportsHelperPage.deletScheduleReportInstance(reportName, "Schedule report deleted successfully.");

	}


	@DataProvider(name = "RandomDates-info")
	public final Object[][] RandomDatesInfo() {
		return new Object[][] { 	
			//BackupJobs Reports 
			{"UI-Automation :Create Random Reports","Backup_UI_Schedule"+RandomStringUtils.randomAlphanumeric(2),new String[] {"123@gmail.com"},"allSources","Schedule","Report schedule created successfully.","Schedule report deleted successfully."},
			{"UI-Automation :Create Random Reports","Backup_UI_Schedule"+RandomStringUtils.randomAlphanumeric(2),new String[] {"123@gmail.com"},"allSources","Schedule","Report schedule created successfully.","Schedule report deleted successfully."},
			{"UI-Automation :Create Random Reports","Backup_UI_Schedule"+RandomStringUtils.randomAlphanumeric(2),new String[] {"123@gmail.com"},"allSources","Schedule","Report schedule created successfully.","Schedule report deleted successfully."},

		};
	}
	@Test(dataProvider = "RandomDates-info", enabled=true)
	public void createOndemandReportswithRandomDetials(String caseType,
			String reportName,
			String [] recepientMail,
			String reportSoucresType,
			String generatetype,
			String toastMessage,
			String DeletedReportToastMessgae
			)
	{

		test=ExtentManager.getNewTest(caseType);
		//Login to the could console 
		//	ReportHelperPage.login_Spog(Username, password);



		String randomTime= (timeInHours[new Random().nextInt(timeInHours.length)]);
		String randomtype=(timeInMinutes[new Random().nextInt(timeInMinutes.length)]);

		String [] frequecny = {"Daily","Weekly","Monthly"};
		String setRandomFrequecny =(frequecny[new Random().nextInt(frequecny.length)]);

		backupjobreportsHelperPage.createScheduleBackupJobreports(reportName, recepientMail, reportSoucresType, generatetype, randomTime, randomtype, setRandomFrequecny, toastMessage, test);
		backupjobreportsHelperPage.checkreportnameInManageReportScheduleReportsTable(reportName, test);
		backupjobreportsHelperPage.deletScheduleReportInstance(reportName, "Schedule report deleted successfully.");

	}

	/*@DataProvider(name = "To10soucres-info")
	public final Object[][] To10soucresInfo() {
		return new Object[][] { 	
			//BackupJobs Reports 
			{"UI-Automation :Create Random Reports","Backup"},
			{"UI-Automation :Create Random Reports","Events"},
			{"UI-Automation :Create Random Reports","Duration"},
			{"UI-Automation :Create Random Reports","Transferred"},

		};
	}
	@Test(dataProvider = "To10soucres-info", enabled=true)
	public void applyFilterOnTop10Soucres(String caseType,
			String fiterName
			)
	{
		test=ExtentManager.getNewTest(caseType);
		backupjobreportsHelperPage.applyFilterOnTop10soucres(fiterName);

	}*/

	@DataProvider(name = "searchBySoucrName-info")
	public final Object[][] searchBysoucresInfo() {
		return new Object[][] { 	
			//BackupJobs Reports 
			{"UI-Automation :Create Random Reports","HY1","Filter_HY"},
			{"UI-Automation :Create Random Reports","2019","Filter_2019"},
			{"UI-Automation :Create Random Reports","abc","Filter_abc"},
			{"UI-Automation :Create Random Reports","cdg","Filter_cdg"},
			{"UI-Automation :Create Random Reports","_VM1","Filter__VM1"},
			{"UI-Automation :Create Random Reports","WIN20","Filter_WIN20"},
		};
	}
	@Test(dataProvider = "searchBySoucrName-info", enabled=true)
	public void searchBySourcName(String caseType,
			String sourceName,
			String saveSeacrhName
			){

		test=ExtentManager.getNewTest(caseType);
		ArrayList<HashMap<String, Object>> expectedInfo = null;
		String aditional_Url=spogServer.PrepareURL("source_name;=;"+sourceName, "", 0, 100, test);

		backupjobreportsHelperPage.searchbackupJobsWithSearchName(sourceName,saveSeacrhName);

		expectedInfo = getBackupJobDetailsInfo(aditional_Url, null, test);
		backupjobreportsHelperPage.GetBackupJobDetailswithSourceName(sourceName,saveSeacrhName,expectedInfo);

		expectedInfo = getTop10sourcesDetailsInfo(sourceName, aditional_Url);
		backupjobreportsHelperPage.getBackupJobReportsTop10Sourceswithcheck(sourceName,expectedInfo);

	}




	@DataProvider(name="Filters")
	public final Object[][]applyFilterOnBackupJobReportsPage(){
		return new Object[][] {
			//{"UI_Autoamtions:apply filters for backup job reports",soucres.get(0),"Date Range;Last 24 Hours,Protection Policy;policyName.get(0),Destination;Volume6,Sources Groups;SourceGroup1"},
			{"UI_Autoamtions:apply filters for backup job reports","LAST_7_Days","Date Range;Last 7 Days"},
			{"UI_Autoamtions:apply filters for backup job reports","Last_2_weeks","Date Range;Last 2 Weeks"},
			{"UI_Autoamtions:apply filters for backup job reports","Last_1_month","Date Range;Last 1 Month"}
		};
	}
	@Test(dataProvider="Filters")
	public void applyfilteronBackupJobReports(String caseType,
			String serach_String,
			String filter) {
		test = ExtentManager.getNewTest(caseType);
		ArrayList<String>filters= new ArrayList<>();

		if (filter != null && !filter.isEmpty()) {
			String[] multipleFilters = filter.split(",");
			for (int i = 0; i < multipleFilters.length; i++) {
				filters.add(multipleFilters[i]);
			}	
		}				

		//String additonalUrl = spogServer.PrepareURL(filter, "", 0, 100, test);
		backupjobreportsHelperPage.applyfiltestoGetBackupJobDetails(serach_String, filters, test);

	}

	@DataProvider(name = "getbakupjobreports")
	public final Object[][] getbakupjobreports() {
		return new Object[][] { { "Direct", direct_user_validToken, direct_user_validToken, direct_organization_id,direct_user_id, Direct_cloud_id, Direct_cloud_id, System.currentTimeMillis() / 1000,
			System.currentTimeMillis() / 1000, direct_user_validToken, "1", 30.20, "3", "02", "2", "3", "3", "3" },

		};
	}

	@Test(dataProvider = "getbakupjobreports",enabled=false)
	public void getBackupJobReports(String organization_type, String token_Destination_creation,
			String adminToken, String organization_id, String create_user_id, String site_id, String cloud_id,
			Long start_time_ts, Long endTimeTS, String site_Token, String job_seq, Double percent_complete,
			String protected_data_size, String raw_data_size, String sync_read_size, String ntfs_volume_size,
			String virtual_disk_provision_size, String dedupe_savings) {

		String job_id = null, log_id = null, group_id = null;
		String job_Type = "restore,restore,restore,restore,restore,restore,restore,restore,restore,restore,restore,restore";
		String job_Status = "canceled,canceled,failed,incomplete,finished,waiting,crash,license_failed,backupjob_proc_exit,skipped,stop,missed";
		String job_Method = "full,incremental,resync,archive,copy,full,incremental,full,incremental,resync,archive,copy";
		String Job_severity = "success,warning,error,critical,informationsuccess, warning,success, warning, error, critical, information";

		String job_type[] = job_Type.split(",");
		String job_method1[] = job_Method.split(",");
		String job_status[] = job_Status.split(",");
		String job_severity[] = Job_severity.split(",");

		

		HashMap<String, ArrayList<HashMap<String, Object>>> last_job = new HashMap<String, ArrayList<HashMap<String, Object>>>();
		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());

		test.assignAuthor("malleswari.sykam");

		ArrayList<String> jobs = new ArrayList<String>();
		ArrayList<HashMap<String, Object>> ExpectedReports = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> temp = new HashMap<String, Object>();

		HashMap<String, String> retention = spogDestinationServer.composeRetention("0", "0", "0", "0", "0", "0");
		HashMap<String, Object> cloud_direct_volume = spogDestinationServer.composeCloudDirectInfo(cloud_id,
				"dest_cloud_direct", "7D", "7 Days", 26.0, 24.0, 50.0, volume_type.normal.toString(), "windows8",
				retention);

		// create destination for direct
		spogDestinationServer.setToken(token_Destination_creation);
		String prefix = spogServer.ReturnRandom("dest");
		Response response = spogDestinationServer.createDestination(UUID.randomUUID().toString(),
				token_Destination_creation, cloud_id, organization_id, cloud_id, "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c",
				"20", "cloud_direct_volume", DestinationStatus.running.toString(), prefix + "destination_direct_volume",
				cloud_direct_volume, test);
		String destiantion_id = response.then().extract().path("data.destination_id");
		String destination_name = prefix + "eswari_cloud_direct_volume";
		String policy_id = null;

		spogServer.setToken(adminToken);
		String protectionStatus = "unprotect,unprotect,unprotect,unprotect,unprotect,unprotect,unprotect,unprotect,unprotect,unprotect,unprotect,unprotect,unprotect,unprotect,unprotect";
		String connectionStatus = "online,offline,online,offline,online,online,offline,online,offline,online,online,offline,online,offline,online";
		String osmajor = "windows,linux,mac,unknown,windows,windows,linux,mac,unknown,windows,windows,linux,mac,unknown,windows";
		String SourceType = "machine,machine,machine,machine,machine,machine,machine,machine,machine,machine,machine,machine,machine,machine,machine";
		String SourceProduct = "cloud_direct,cloud_direct,cloud_direct,cloud_direct,cloud_direct,cloud_direct,cloud_direct,cloud_direct,cloud_direct,cloud_direct,cloud_direct,cloud_direct,cloud_direct,cloud_direct,cloud_direct";

		String[] protection = protectionStatus.split(",");
		String[] connection = connectionStatus.split(",");
		String[] Osmajor = osmajor.split(",");
		String[] sourceType = SourceType.split(",");
		String[] sourceProduct = SourceProduct.split(",");
		/* String destination_id = UUID.randomUUID().toString(); */

		String[] arrayofsourcenodes = new String[1];
		ArrayList<String>Soucre_id= new ArrayList<>();

		
		for (int i = 0; i < 4; i++) {

			// String destination_name="cloud_direct_hybrid"+i;
			String schedule_id = UUID.randomUUID().toString();
			String task_id = UUID.randomUUID().toString();
			String throttle_id = UUID.randomUUID().toString();
			/* destination_id = UUID.randomUUID().toString(); */
			String rps_id = UUID.randomUUID().toString();

			spogServer.setToken(adminToken);
			String sourceName = spogServer.ReturnRandom("SPOG_ARCSEVRE");
			test.log(LogStatus.INFO, "create source");

			source_id = spogServer.createSourceWithCheck(sourceName + i, sourceType[i], sourceProduct[i],
					organization_id, cloud_id, protection[i], connection[i], Osmajor[i], "SQL_SERVER", create_user_id,
					SpogConstants.SUCCESS_POST, null, true, test);
			soucres.add(sourceName + i);
			
			arrayofsourcenodes[0] = source_id;
			Soucre_id.add(source_id);
			String group_name = prefix + "test" + i;
			sourceGroups.add(group_name);
			// CREATED SOURCE TO THE GROUP
			test.log(LogStatus.INFO, "Create a source group");
			group_id = spogServer.createGroupWithCheck(organization_id, group_name, "add sources", test);
			spogServer.setToken(adminToken);
			// add sources to the sourcegroup
			test.log(LogStatus.INFO, "Add the sources to the sourcegroup " + group_id);
			spogServer.addSourcetoSourceGroupwithCheck(group_id, arrayofsourcenodes, adminToken,
					SpogConstants.SUCCESS_POST, SpogMessageCode.SUCCESS_POST, test);

			// create cloud hybrid destination
			spogDestinationServer.setToken(adminToken);

			if (organization_type.equals("sub")) {
				spogDestinationServer.setToken(msp_user_validToken);
			}

			prefix = RandomStringUtils.randomAlphanumeric(8);

			test.log(LogStatus.INFO, "Create custom settings");
			HashMap<String, Object> customScheduleSettingDTO = policy4SpogServer.createCustomScheduleDTO("1522068700422", "full", "1", "true", "10", "minutes", test);

			HashMap<String, Object> scheduleSettingDTO = policy4SpogServer.createScheduleSettingDTO(null,customScheduleSettingDTO, null, null, test);

			test.log(LogStatus.INFO, "Create cloud direct schedule");

			HashMap<String, Object> cloudDirectScheduleDTO = policy4SpogServer.createCloudDirectScheduleDTO("0 6 * * *",test);

			test.log(LogStatus.INFO, "Create schedule settings");
			scheduleSettingDTO = policy4SpogServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null, null, null,test);

			ArrayList<HashMap<String, Object>> schedules = policy4SpogServer.createPolicyScheduleDTO(null, schedule_id,"1d", task_id, destiantion_id, scheduleSettingDTO, "06:00", "12:00","cloud_direct_file_folder_backup", destination_name, test);

			test.log(LogStatus.INFO, "Exclude the folders from cloud direct file backup");

			ArrayList<HashMap<String, Object>> excludes = policy4SpogServer.createExcludeInfoDTO(null, "path","c:\\tmp", test);

			HashMap<String, Object> cloudDirectLocalBackupDTO = policy4SpogServer.createCloudDirectLocalBackupDTO("d:\\tmp", "true", test);

			HashMap<String, Object> cloudDirectFileBackupTaskInfoDTO = policy4SpogServer.createCloudDirectFileBackupTaskInfoDTO("d:\\tmp", cloudDirectLocalBackupDTO, excludes, test);
			HashMap<String, Object> perform_ar_test = policy4SpogServer.createPerformARTestOption("1", "1", "1", "1",test);

			HashMap<String, Object> retention_policy = policy4SpogServer.createRetentionPolicyOption("2", "2", "2", "2",test);

			HashMap<String, Object> udp_replication_from_remote_DTO = policy4SpogServer.createUdpReplicationFromRemoteInfoDTO(perform_ar_test, retention_policy, test);

			ArrayList<HashMap<String, Object>> destinations = policy4SpogServer.createPolicyTaskDTO(null, task_id,"cloud_direct_file_folder_backup", destiantion_id, "none", null, cloudDirectFileBackupTaskInfoDTO,
					null, test);
			ArrayList<HashMap<String, Object>> throttles = policy4SpogServer.createPolicyThrottleDTO(null, throttle_id,task_id, "network", "1200", "1", "06:00", "18:00", "cloud_direct_file_folder_backup",destiantion_id, destination_name, test);
			// create cloud direct policy
			policy4SpogServer.setToken(adminToken);
			String policy_name = UUID.randomUUID().toString()+spogServer.ReturnRandom("eswari");
			response = policy4SpogServer.createPolicy(policy_name, policy_name, "cloud_direct_baas", null,"true", source_id, destinations, schedules, throttles, UUID.randomUUID().toString(),
					organization_id, test);
			policy_id = response.then().extract().path("data.policy_id").toString();
			policy_name = response.then().extract().path("data.policy_name").toString();
			policyName.add(policy_name);
			
			for (int k = 0; k < 1; k++) {


				gatewayServer.setToken(site_Token);
				test.log(LogStatus.INFO, "creating job_type " + job_type[k] + "job_staus" + job_status[k] + "job_method"+ job_method1[k]);
				job_id = gatewayServer.postJobWithCheck(start_time_ts, endTimeTS, organization_id, source_id, source_id,rps_id, destiantion_id, policy_id, job_type[k], job_method1[k], job_status[k], site_Token,
						test);
				//spogDestinationServer.setToken(site_Token);

				last_job = spogDestinationServer.composeLastJob(start_time_ts, endTimeTS, percent_complete,job_status[k], job_type[k], job_method1[k]);
				jobs.add(job_id);


			}

		}


	}


	/***
	 * 
	 * @param aditional_Url
	 * @param sort
	 * @param test
	 * @return
	 */
	public	ArrayList<HashMap<String, Object>> getBackupJobDetailsInfo(String aditional_Url, String sort, ExtentTest test){


		spogServer.userLogin("eswari.sykam102+s35sub@gmail.com", "Mclaren@2016");
		String validToken = spogServer.getJWTToken();

		String filterStr = null;
		HashMap<String, Object> backupJobDetailsInfo = null;
		ArrayList<HashMap<String, Object>> expectedBackUpJobDetails= new ArrayList<>(); 
		int backupJobCount;


		Response response=spogreportserver.getBackupReportsDetails(validToken,aditional_Url,SpogConstants.SUCCESS_GET_PUT_DELETE,null,test);




		//Response response=spogreportserver.getBackupReportsDetails(suborg_user_validToken,APIfilter,SpogConstants.SUCCESS_GET_PUT_DELETE,null,test);
		ArrayList<HashMap<String, Object>> actualInfo = response.then().extract().path("data");
		backupJobCount=actualInfo.size();


		for(int i =0;i<backupJobCount;i++) {

			backupJobDetailsInfo = new HashMap<>();
			backupJobDetailsInfo.put(TableHeaders.source, response.then().extract().path("data["+i+"].source.source_name"));
			/*backupJobDetailsInfo.put(TableHeaders.job_status, response.then().extract().path("data["+i+"].job_status"));
			backupJobDetailsInfo.put(TableHeaders.destination, response.then().extract().path("data["+i+"].destination.destination_name"));
			backupJobDetailsInfo.put(TableHeaders.warnings, response.then().extract().path("data["+i+"].warnings"));
			backupJobDetailsInfo.put(TableHeaders.errors, response.then().extract().path("data["+i+"].errors"));

			if (response.then().extract().path("data["+i+"].policy") == null) {
				backupJobDetailsInfo.put(TableHeaders.policy, "-");
			}else {
				backupJobDetailsInfo.put(TableHeaders.policy,response.then().extract().path("data["+i+"].policy.policy_name") );
			}

			ArrayList<HashMap<String, Object>> SG = response.then().extract().path("data["+i+"].source_groups");

			if (SG.size() == 0) {
				backupJobDetailsInfo.put(TableHeaders.source_group_name, "-");	
			}else if (SG.size() == 1) {
				backupJobDetailsInfo.put(TableHeaders.source_group_name, SG.get(0).get("source_group_name"));
			}else if (SG.size() > 1) {
				backupJobDetailsInfo.put(TableHeaders.source_group_name, SG.size()+" Groups");
			}*/

			System.out.println(backupJobDetailsInfo.put(TableHeaders.source, response.then().extract().path("data["+i+"].source.source_name")));
			expectedBackUpJobDetails.add(backupJobDetailsInfo);
		}
		return expectedBackUpJobDetails;


	}

	@SuppressWarnings("null")
	public ArrayList<HashMap<String, Object>> getTop10sourcesDetailsInfo(String SourceName,String additionalUrl){
		spogServer.userLogin("eswari.sykam102+s35sub@gmail.com", "Mclaren@2016");
		String validToken = spogServer.getJWTToken();
		HashMap<String, Object> top10SourcesInfo = null;

		ArrayList<HashMap<String, Object>>expctedTop10SoucresDetails = new ArrayList<>();



		Response response = spogreportserver.getBackupJobreportsTop10soucres(validToken,additionalUrl,SpogConstants.SUCCESS_GET_PUT_DELETE,null,test);

		ArrayList<HashMap<String, Object>> actualInfo = response.then().extract().path("data");
		int Top10soucresCount= actualInfo.size();


		for(int i=0;i<Top10soucresCount;i++) {
			top10SourcesInfo = new HashMap<>();
			top10SourcesInfo.put(TableHeaders.Name,response.then().extract().path("data["+i+"].name"));
			System.out.println(top10SourcesInfo.put(TableHeaders.Name,response.then().extract().path("data["+i+"].name")));
			expctedTop10SoucresDetails.add(top10SourcesInfo);
		}


		return expctedTop10SoucresDetails;
	}
	//	@AfterMethod
	public void afterMethod(){
		//backupjobreportsHelperPage.logout();
		//		backupjobreportsHelperPage.destroy();
	}
	private void prepareEnv(){

		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);

		//************************create msp org,user *************************************
		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		test.log(LogStatus.INFO,"create a msp org");
		msp_org_id = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name+org_model_prefix , SpogConstants.MSP_ORG, null, null, null, null, test);
		final_msp_user_name_email = prefix + msp_user_name_email;

		test.log(LogStatus.INFO,"create a admin under msp org");
		msp_user_id = spogServer.createUserAndCheck(final_msp_user_name_email, common_password, prefix + msp_user_first_name, prefix + msp_user_last_name, SpogConstants.MSP_ADMIN, msp_org_id, test);
		spogServer.userLogin(final_msp_user_name_email, common_password);

		//*********************Create Direct Org user****************************
		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);


		this.direct_org_email = prefix + this.direct_org_email;
		direct_organization_id = spogServer.CreateOrganizationByEnrollWithCheck(prefix + direct_org_name + org_model_prefix,
				SpogConstants.DIRECT_ORG, this.direct_org_email, common_password, prefix + direct_org_first_name,
				prefix + direct_org_last_name, test);
		spogServer.userLogin(this.direct_org_email, common_password);
		test.log(LogStatus.INFO, "Getting the JWTToken for the Logged in user");
		direct_user_validToken = spogServer.getJWTToken();
		// direct_user_validToken=validToken;
		test.log(LogStatus.INFO, "The token is :" + direct_user_validToken);

		test.log(LogStatus.INFO, "Get the logged in userid ");
		direct_user_id = spogServer.GetLoggedinUser_UserID();
		test.log(LogStatus.INFO, "The direct org user id is " + direct_user_id);
		test.log(LogStatus.INFO,"The token is :"+ direct_user_validToken );

		test.log(LogStatus.INFO, "Create a site/register/login to the site");
		direct_site_id = gatewayServer.createsite_register_login(direct_organization_id, direct_user_validToken, direct_user_id, "ts", "1.0.0", spogServer, test);
		direct_site_token = gatewayServer.getJWTToken();

		/*response=gatewayServer.getSiteConfiguration(direct_site_id, direct_user_validToken, test);
		direct_site_name=response.then().extract().path("data.site_name");

		 */

		//create a cloud account under direct

		Response	response=spogServer.getCloudAccounts(direct_user_validToken, "", test);
		Direct_cloud_id=response.then().extract().path("data["+0+"].cloud_account_id");

		//	direct_cloud_account_id=createCloudAccount(UUID.randomUUID().toString(),cloudAccountSecret_direct,"CloudAccountData", "cloud_direct",direct_organization_id,"SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_","91a9b48e-6ac6-4c47-8202-614b5cdcfe0c",spogServer,test);

		spogDestinationServer.setToken(direct_user_validToken);
		HashMap<String, String> retention = spogDestinationServer.composeRetention("0","0","0","0","0","0");
		HashMap<String, Object> cloud_direct_volume = spogDestinationServer.composeCloudDirectInfo(Direct_cloud_id,"dest_cloud_direct","7D","7 Days",26.0,24.0,50.0,volume_type.normal.toString(),"windows8",retention);	


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
	public void destroy_UI_instance() {
		backupjobreportsHelperPage.logout();

		backupjobreportsHelperPage.destroy();
		recycleVolumeInCDandDestroyOrg(org_model_prefix);

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

}
