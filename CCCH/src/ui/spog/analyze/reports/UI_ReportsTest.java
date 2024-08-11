package ui.spog.analyze.reports;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;
import java.util.TimeZone;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.xalan.xsltc.compiler.sym;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.google.common.collect.ObjectArrays;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.SpogConstants;
import InvokerServer.Org4SPOGServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;
import ui.spog.server.ReportHelperPage;
import ui.spog.server.BrandingHelperpage;
import ui.spog.server.CustomerAccountsPageHelper;
import ui.spog.server.SPOGUIServer;



/**
 * @author Nagamalleswari.Sykam
 * Created on demand all type of Reports 
 * Created Scheduled  all Types of Reports 
 * Validated onDemand reports in the Table of reports page
 * Validated Schedule reports in the table of Scheduled reports
 * Deleted the on demand reports 
 * Deleted scheduled reports
 * 
 * 
 *
 */
public class UI_ReportsTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private ReportHelperPage ReportHelperPage;
	private UserSpogServer userSpogServer;
	private SPOGUIServer spogUIServer;
	private CustomerAccountsPageHelper customerAccountsPageHelper;
	private String csrAdminUserName;
	private String csrAdminPassword;
	private ExtentTest test;

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

	String ReportName = "Backup_UI_Schedule"+RandomStringUtils.randomAlphanumeric(2);
	String[] timeInHours =  /*{"00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15",}*/{"16","17","18","19","20","21","22","23","24"};
	String[] timeInMinutes =  /*{"00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31","32","33","34","35","36","37","38","39","40","41","42","43","44","45","46","47","48","49",}*/{"50","51","52","53","54","55","56","57","58","59"};
	//String [] timeTypes = {"PM"};//"AM"};
	String [] frequecny = {"Daily"};//,"Weekly","Monthly"};



	@BeforeClass
	@Parameters({ "baseURI", "port",   "logFolder", "csrAdminUserName", "csrAdminPassword", "buildVersion", "uiBaseURI", "browserType", "maxWaitTimeSec"})
	public void beforeClass(String baseURI, String port, String logFolder, String adminUserName, String adminPassword,  String buildVersion,
			String uiBaseURI, String browserType, int maxWaitTimeSec) throws UnknownHostException {

		org4SpogServer = new Org4SPOGServer(baseURI, port);
		spogServer = new SPOGServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		this.url = uiBaseURI;
		this.csrAdminUserName = adminUserName;
		this.csrAdminPassword = adminPassword;
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(),logFolder);
		test = rep.startTest("beforeClass");

		prepareEnv();

		ReportHelperPage = new ReportHelperPage(browserType, maxWaitTimeSec);
		ReportHelperPage.openUrl(uiBaseURI);

		this.BQName = this.getClass().getSimpleName();
		String author = "mallleswari.sykam";
		this.runningMachine =  InetAddress.getLocalHost().getHostName();
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date=new java.util.Date();
		this.buildVersion=buildVersion+"_"+dateFormater.format(date);
		Nooftest=0;
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		Date dateObj = new Date(creationTime);

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
		ReportHelperPage.login_Spog(direct_org_email, common_password);
	}

	//@BeforeMethod
	@Parameters({"uiBaseURI", "browserType", "maxWaitTimeSec"})
	public void login(String uiBaseURI, String browserType, int maxWaitTimeSec) {
		//		brandingpageHelper = new BrandingpageHelper(browserType, maxWaitTimeSec);
		//		brandingpageHelper.openUrl(uiBaseURI);
		ReportHelperPage.login_Spog(direct_org_email, common_password);


		//nop37xuyspogqa_mallleswari_msp_admin@arcserve.com
		//brandingpageHelper.login_Spog("nop37xuyspogqa_mallleswari_msp_admin@arcserve.com", common_password);
	}



	@DataProvider(name = "OnDemandbackupreports-info")
	public final Object[][] ReportsInfo() {
		return new Object[][] { 	
			//BackupJobs Reports 
			{"Success",direct_org_email, common_password,"BKP_UI_OndemandLast24hour","Backup Jobs",new String[] {"eswari@gmail.com"},"allSources","ondemand","Last 24 Hours","Report created successfully."},
			{"Success",direct_org_email, direct_org_email,"BKP_UI_Ondemand_Last7days","Backup Jobs",new String[] {"eswari@gmail.com"},"allSources","ondemand","Last 7 Days","Report created successfully."},
			{"Success",direct_org_email,direct_org_email,"BKP_UI_Ondemand_Last1Month","Backup Jobs",new String[] {"eswari@gmail.com"},"allSources","ondemand","Last 1 Month","Report created successfully."},

			//RecoveryJobs Reports 
			{"Success",direct_org_email, common_password,"RJ_UI_OndemandLast24hour","Recovery Jobs",new String[] {"eswari@gmail.com"},"allSources","ondemand","Last 24 Hours","Report created successfully."},
			{"Success",direct_org_email, direct_org_email,"RJ_UI_Ondemand_Last7days","Recovery Jobs",new String[] {"eswari@gmail.com"},"allSources","ondemand","Last 7 Days","Report created successfully."},
			{"Success",direct_org_email,direct_org_email,"RJ_UI_Ondemand_Last1Month","Recovery Jobs",new String[] {"eswari@gmail.com"},"allSources","ondemand","Last 1 Month","Report created successfully."},

			// DataTransfer Reports 
			{"Success",direct_org_email, common_password,"DT_UI_OndemandLast24hour","Data Transfer",new String[] {"eswari@gmail.com"},"allSources","ondemand","Last 24 Hours","Report created successfully."},
			{"Success",direct_org_email, direct_org_email,"DT_UI_Ondemand_Last7days","Data Transfer",new String[] {"eswari@gmail.com"},"allSources","ondemand","Last 7 Days","Report created successfully."},
			{"Success",direct_org_email,direct_org_email,"DT_UI_Ondemand_Last1Month","Data Transfer",new String[] {"eswari@gmail.com"},"allSources","ondemand","Last 1 Month","Report created successfully."},

			//CapacityUsageReports
			{"Success",direct_org_email, common_password,"DT_UI_OndemandLast24hour","Capacity Usage",new String[] {"eswari@gmail.com"},"allSources","ondemand","Last 24 Hours","Report created successfully."},
			{"Success",direct_org_email, direct_org_email,"DT_UI_Ondemand_Last7days","Capacity Usage",new String[] {"eswari@gmail.com"},"allSources","ondemand","Last 7 Days","Report created successfully."},
			{"Success",direct_org_email,direct_org_email,"DT_UI_Ondemand_Last1Month","Capacity Usage",new String[] {"eswari@gmail.com"},"allSources","ondemand","Last 1 Month","Report created successfully."},



		};
	}
	@Test(dataProvider = "OnDemandbackupreports-info", enabled=true)
	public void createOnDemandreports(String caseType,
			String username,String password,
			String reportName,
			String reportType,
			String [] recepientMail,
			String reportSoucresType,
			String genratetype,
			String datarange,
			String toastMessage
			)
	{

		test=ExtentManager.getNewTest(caseType);
		//Login to the could console 
		//	ReportHelperPage.login_Spog(Username, password);

		//ReportHelperPage.login_Spog("malleswari.sykam101@gmail.com", "Mclaren@2016");

		ReportHelperPage.ondemandReportCreationForallReportTypes(reportName, reportType,recepientMail, reportSoucresType, genratetype, datarange, toastMessage, test);
		ReportHelperPage.checkreportnameinreportsTable(reportName, test);
		ReportHelperPage.deleteReport(reportName, "Report deleted successfully.", test);

	}

	@DataProvider(name = "backupreportsschedule-info")
	public final Object[][] ScheduleBackupjobsInfo() {
		return new Object[][] { 	


			{"UI-Automation :Daily schedule report at 1AM ",direct_org_email, common_password,"Backup Jobs",new String[] {"123@gmail.com"},"allSources","Schedule","Report schedule created successfully.","Schedule report deleted successfully."},
		};
	}
	@Test(dataProvider = "backupreportsschedule-info", enabled=true)
	public void createschedulereports(String caseType,
			String Username,String password,
			String reportType,
			String [] recepientMail,
			String reportSoucresType,
			String generatetype,
			String toastMessage,
			String DeletedReportToastMessgae
			)
	{
		test=ExtentManager.getNewTest(caseType);
		for(int i=0;i<timeInHours.length;i++) {
			for(int j=0;j<timeInMinutes.length;j++) {
				for(int k=0;k<frequecny.length;k++) {

					String ReportName =  "_UI_Schedule"+RandomStringUtils.randomAlphanumeric(2)+ "_"+timeInHours[i]+ "_"+timeInMinutes[j]+"_"+ frequecny[k];

					ReportHelperPage.createScheduleReportsforallReporTypes(ReportName,reportType,recepientMail, reportSoucresType, generatetype, timeInHours[i], timeInMinutes[j], frequecny[k], test);
					ReportHelperPage.checkreportnameInManageReportScheduleReportsTable(ReportName, test);
					ReportHelperPage.deletereportByNameinManagedReportSchedulesTable(ReportName, DeletedReportToastMessgae, test);
				}
			}
		}
	}


	@DataProvider(name = "Recoveryreportsschedule-info")
	public final Object[][] ScheduleRecoveryjobsInfo() {
		return new Object[][] { 	


			{"UI-Automation :Daily schedule report at 1AM ",direct_org_email, common_password,"Recovery Jobs",new String[] {"123@gmail.com"},"allSources","Schedule","Report schedule created successfully.","Schedule report deleted successfully."},
		};
	}
	@Test(dataProvider = "Recoveryreportsschedule-info", enabled=true)
	public void createscheduleRecveoryJobreports(String caseType,
			String Username,String password,
			String reportType,
			String [] recepientMail,
			String reportSoucresType,
			String generatetype,
			String toastMessage,
			String DeletedReportToastMessgae
			)
	{

		test=ExtentManager.getNewTest(caseType);
		for(int i=0;i<timeInHours.length;i++) {
			for(int j=0;j<timeInMinutes.length;j++) {
				for(int k=0;k<frequecny.length;k++) {

					String ReportName =  "_UI_Schedule"+RandomStringUtils.randomAlphanumeric(2)+ "_"+timeInHours[i]+ "_"+timeInMinutes[j]+"_"+ frequecny[k];

					ReportHelperPage.createScheduleReportsforallReporTypes(ReportName,reportType,recepientMail, reportSoucresType, generatetype, timeInHours[i], timeInMinutes[j], frequecny[k], test);
					ReportHelperPage.checkreportnameInManageReportScheduleReportsTable(ReportName, test);
					ReportHelperPage.deletereportByNameinManagedReportSchedulesTable(ReportName, DeletedReportToastMessgae, test);
				}
			}
		}
	}

	@DataProvider(name = "DataTransferreportsschedule-info")
	public final Object[][] ScheduleDataTransferInfo() {
		return new Object[][] { 	


			{"UI-Automation :Daily schedule report at 1AM ",direct_org_email, common_password,"Data Transfer",new String[] {"123@gmail.com"},"allSources","Schedule","Report schedule created successfully.","Schedule report deleted successfully."},
			

		};
	}
	@Test(dataProvider = "DataTransferreportsschedule-info", enabled=true)
	public void createschedulereportForDataTransferReport(String caseType,
			String Username,String password,
			String reportType,
			String [] recepientMail,
			String reportSoucresType,
			String generatetype,
			String toastMessage,
			String DeletedReportToastMessgae
			)
	{

		test=ExtentManager.getNewTest(caseType);
		for(int i=0;i<timeInHours.length;i++) {
			for(int j=0;j<timeInMinutes.length;j++) {
				for(int k=0;k<frequecny.length;k++) {

					String ReportName =  "_UI_Schedule"+RandomStringUtils.randomAlphanumeric(2)+ "_"+timeInHours[i]+ "_"+timeInMinutes[j]+"_"+ frequecny[k];

					ReportHelperPage.createScheduleReportsforallReporTypes(ReportName,reportType,recepientMail, reportSoucresType, generatetype, timeInHours[i], timeInMinutes[j], frequecny[k], test);
					ReportHelperPage.checkreportnameInManageReportScheduleReportsTable(ReportName, test);
					ReportHelperPage.deletereportByNameinManagedReportSchedulesTable(ReportName, DeletedReportToastMessgae, test);
				}
			}
		}

	}

	@DataProvider(name = "CapacityUsagereportsschedule-info")
	public final Object[][] ScheduleCapacityUsageInfo() {
		return new Object[][] { 	


			{"UI-Automation :Daily schedule report at 1AM ",direct_org_email, common_password,"_UI_Schedule"+RandomStringUtils.randomAlphanumeric(2),"Capacity Usage",new String[] {"123@gmail.com"},"allSources","Schedule","01:00","AM","Daily","Report schedule created successfully.","Schedule report deleted successfully."},
			
		};
	}
	@Test(dataProvider = "CapacityUsagereportsschedule-info", enabled=true)
	public void createschedulereportForCapacityUsageReport(String caseType,
			String Username,String password,
			String reportType,
			String [] recepientMail,
			String reportSoucresType,
			String generatetype,
			String toastMessage,
			String DeletedReportToastMessgae
			)
	{

		test=ExtentManager.getNewTest(caseType);
		for(int i=0;i<timeInHours.length;i++) {
			for(int j=0;j<timeInMinutes.length;j++) {
				for(int k=0;k<frequecny.length;k++) {

					String ReportName =  "_UI_Schedule"+RandomStringUtils.randomAlphanumeric(2)+ "_"+timeInHours[i]+ "_"+timeInMinutes[j]+"_"+ frequecny[k];

					ReportHelperPage.createScheduleReportsforallReporTypes(ReportName,reportType,recepientMail, reportSoucresType, generatetype, timeInHours[i], timeInMinutes[j], frequecny[k], test);
					ReportHelperPage.checkreportnameInManageReportScheduleReportsTable(ReportName, test);
					ReportHelperPage.deletereportByNameinManagedReportSchedulesTable(ReportName, DeletedReportToastMessgae, test);
				}
			}
		}


	}




	@DataProvider(name = "Custom-info")
	public final Object[][] CustomReportsInfo() {
		return new Object[][] { 	
			//BackupJobs Reports 
			//Valid DateRanges
			{"Valid Strat and End DataRanges to create Cutsom reports for BackupJobs ","BKP_UI_Schedule","Backup Jobs",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2018","Dec","20"},new String[]{"2019","Mar","1"},"Report created successfully."},
			{"Valid Strat and End DataRanges to create Cutsom reports for BackupJobs","BKP_UI_Schedule","Backup Jobs",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2018","Dec","15"},new String[]{"2018","Feb","9"},"Report created successfully."},
			{"Valid Strat and End DataRanges to create Cutsom reports for BackupJobs","BKP_UI_Schedule","Backup Jobs",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2019","Feb","21"},new String[]{"2019","Mar","1"},"Report created successfully."},
			{"Valid Strat and End DataRanges to create Cutsom reports for BackupJobs","BKP_UI_Schedule","Backup Jobs",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2019","Feb","8"},new String[]{"2019","Mar","2"},"Report created successfully."},
			{"Valid Strat and End DataRanges to create Cutsom reports for BackupJobs","BKP_UI_Schedule","Backup Jobs",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2019","Jan","8"},new String[]{"2019","Jan","9"},"Report created successfully."},

			//Invalid DateRanges
			{"InValid Start DateRange Year to Cutsom reports for BackupJobs ","BKP_UI_Schedule","Backup Jobs",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2019","Dec","20"},new String[]{"2020","Mar","1"},"Report created successfully."},
			{"InValid End DateRange Year to Cutsom reports for BackupJobs ","BKP_UI_Schedule","Backup Jobs",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2018","Dec","20"},new String[]{"2020","Mar","1"},"Report created successfully."},
			{"InValid Start DateRange Month to Cutsom reports for BackupJobs ","BKP_UI_Schedule","Backup Jobs",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2018","Jan","20"},new String[]{"2019","Mar","1"},"Report created successfully."},
			{"InValid End DateRange Month to Cutsom reports for BackupJobs ","BKP_UI_Schedule","Backup Jobs",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2018","Dec","20"},new String[]{"2020","Jul","1"},"Report created successfully."},
			{"InValid Start DateRange Date to Cutsom reports for BackupJobs ","BKP_UI_Schedule","Backup Jobs",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2018","Dec","11"},new String[]{"2019","Mar","1"},"Report created successfully."},
			{"InValid End DateRange Date to Cutsom reports for BackupJobs ","BKP_UI_Schedule","Backup Jobs",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2018","Dec","20"},new String[]{"2020","Mar","31"},"Report created successfully."},


			//Current Date and current Month 
			//{"InValid Start DateRange Date to Cutsom reports for BackupJobs ","BKP_UI_Schedule","Backup Jobs",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"var month = currentTime.getMonth() + 1","Dec","11"},new String[]{"2019","Mar","1"},"Report created successfully."},


			//RecoveryJobReports 
			{"Valid Strat and End DataRanges to create Cutsom reports for RecoveryJobs ","RC_UI_Schedule","Recovery Jobs",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2018","Dec","20"},new String[]{"2019","Mar","1"},"Report created successfully."},
			{"Valid Strat and End DataRanges to create Cutsom reports for RecoveryJobs","RC_UI_Schedule","Recovery Jobs",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2018","Dec","15"},new String[]{"2018","Feb","9"},"Report created successfully."},
			{"Valid Strat and End DataRanges to create Cutsom reports for RecoveryJobs","RC_UI_Schedule","Recovery Jobs",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2019","Feb","21"},new String[]{"2019","Mar","1"},"Report created successfully."},
			{"Valid Strat and End DataRanges to create Cutsom reports for RecoveryJobs","RC_UI_Schedule","Recovery Jobs",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2019","Feb","8"},new String[]{"2019","Mar","2"},"Report created successfully."},
			{"Valid Strat and End DataRanges to create Cutsom reports for RecoveryJobs","RC_UI_Schedule","Recovery Jobs",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2019","Jan","8"},new String[]{"2019","Jan","9"},"Report created successfully."},

			//Invalid DateRanges
			{"InValid Start DateRange Year to Cutsom reports for RecoveryJobs ","RC_UI_Schedule","Recovery Jobs",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2019","Dec","20"},new String[]{"2020","Mar","1"},"Report created successfully."},
			{"InValid End DateRange Year to Cutsom reports for RecoveryJobs ","RC_UI_Schedule","Recovery Jobs",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2018","Dec","20"},new String[]{"2020","Mar","1"},"Report created successfully."},
			{"InValid Start DateRange Month to Cutsom reports for RecoveryJobs ","RC_UI_Schedule","Recovery Jobs",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2018","Jan","20"},new String[]{"2019","Mar","1"},"Report created successfully."},
			{"InValid End DateRange Month to Cutsom reports for RecoveryJobs ","RC_UI_Schedule","Recovery Jobs",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2018","Dec","20"},new String[]{"2020","Jul","1"},"Report created successfully."},
			{"InValid Start DateRange Date to Cutsom reports for RecoveryJobs ","RC_UI_Schedule","Recovery Jobs",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2018","Dec","11"},new String[]{"2019","Mar","1"},"Report created successfully."},
			{"InValid End DateRange Date to Cutsom reports for RecoveryJobs ","RC_UI_Schedule","Recovery Jobs",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2018","Dec","20"},new String[]{"2020","Mar","31"},"Report created successfully."},


			//Current Date and current Month 
			//	{"InValid Start DateRange Date to Cutsom reports for RecoveryJobs ","RC_UI_Schedule","Recovery Jobs",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"var month = currentTime.getMonth() + 1","Dec","11"},new String[]{"2019","Mar","1"},"Report created successfully."},


			//Data Transfer Reports 
			{"Valid Strat and End DataRanges to create Cutsom reports for DataTransfer ","DT_UI_Schedule","Data Transfer",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2018","Dec","20"},new String[]{"2019","Mar","1"},"Report created successfully."},
			{"Valid Strat and End DataRanges to create Cutsom reports for DataTransfer","DT_UI_Schedule","Data Transfer",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2018","Dec","15"},new String[]{"2018","Feb","9"},"Report created successfully."},
			{"Valid Strat and End DataRanges to create Cutsom reports for DataTransfer","DT_UI_Schedule","Data Transfer",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2019","Feb","21"},new String[]{"2019","Mar","1"},"Report created successfully."},
			{"Valid Strat and End DataRanges to create Cutsom reports for DataTransfer","DT_UI_Schedule","Data Transfer",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2019","Feb","8"},new String[]{"2019","Mar","2"},"Report created successfully."},
			{"Valid Strat and End DataRanges to create Cutsom reports for DataTransfer","DT_UI_Schedule","Data Transfer",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2019","Jan","8"},new String[]{"2019","Jan","9"},"Report created successfully."},

			//Invalid DateRanges
			{"InValid Start DateRange Year to Cutsom reports for DataTransfer ","DT_UI_Schedule","Data Transfer",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2019","Dec","20"},new String[]{"2020","Mar","1"},"Report created successfully."},
			{"InValid End DateRange Year to Cutsom reports for DataTransfer ","DT_UI_Schedule","Data Transfer",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2018","Dec","20"},new String[]{"2020","Mar","1"},"Report created successfully."},
			{"InValid Start DateRange Month to Cutsom reports for DataTransfer ","DT_UI_Schedule","Data Transfer",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2018","Jan","20"},new String[]{"2019","Mar","1"},"Report created successfully."},
			{"InValid End DateRange Month to Cutsom reports for DataTransfer ","DT_UI_Schedule","Data Transfer",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2018","Dec","20"},new String[]{"2020","Jul","1"},"Report created successfully."},
			{"InValid Start DateRange Date to Cutsom reports for DataTransfer ","DT_UI_Schedule","Data Transfer",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2018","Dec","11"},new String[]{"2019","Mar","1"},"Report created successfully."},
			{"InValid End DateRange Date to Cutsom reports for DataTransfer ","DT_UI_Schedule","Data Transfer",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2018","Dec","20"},new String[]{"2020","Mar","31"},"Report created successfully."},


			//Current Date and current Month 
			//{"InValid Start DateRange Date to Cutsom reports for DataTransfer ","DT_UI_Schedule","Data Transfer",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"var month = currentTime.getMonth() + 1","Dec","11"},new String[]{"2019","Mar","1"},"Report created successfully."},



			//CapacityUsage Reports 
			//CapacityJobReports 
			{"Valid Strat and End CapacityRanges to create Cutsom reports for CapacityUsage ","Cap_UI_Schedule","Capacity Usage",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2018","Dec","20"},new String[]{"2019","Mar","1"},"Report created successfully."},
			{"Valid Strat and End CapacityRanges to create Cutsom reports for CapacityUsage","Cap_UI_Schedule","Capacity Usage",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2018","Dec","15"},new String[]{"2018","Feb","9"},"Report created successfully."},
			{"Valid Strat and End CapacityRanges to create Cutsom reports for CapacityUsage","Cap_UI_Schedule","Capacity Usage",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2019","Feb","21"},new String[]{"2019","Mar","1"},"Report created successfully."},
			{"Valid Strat and End CapacityRanges to create Cutsom reports for CapacityUsage","Cap_UI_Schedule","Capacity Usage",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2019","Feb","8"},new String[]{"2019","Mar","2"},"Report created successfully."},
			{"Valid Strat and End CapacityRanges to create Cutsom reports for CapacityUsage","Cap_UI_Schedule","Capacity Usage",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2019","Jan","8"},new String[]{"2019","Jan","9"},"Report created successfully."},

			//Invalid DateRanges
			{"InValid Start DateRange Year to Cutsom reports for CapacityUsage ","Cap_UI_Schedule","Capacity Usage",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2019","Dec","20"},new String[]{"2020","Mar","1"},"Report created successfully."},
			{"InValid End DateRange Year to Cutsom reports for CapacityUsage ","Cap_UI_Schedule","Capacity Usage",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2018","Dec","20"},new String[]{"2020","Mar","1"},"Report created successfully."},
			{"InValid Start DateRange Month to Cutsom reports for CapacityUsage ","Cap_UI_Schedule","Capacity Usage",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2018","Jan","20"},new String[]{"2019","Mar","1"},"Report created successfully."},
			{"InValid End DateRange Month to Cutsom reports for CapacityUsage ","Cap_UI_Schedule","Capacity Usage",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2018","Dec","20"},new String[]{"2020","Jul","1"},"Report created successfully."},
			{"InValid Start DateRange Date to Cutsom reports for CapacityUsage ","Cap_UI_Schedule","Capacity Usage",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2018","Dec","11"},new String[]{"2019","Mar","1"},"Report created successfully."},
			{"InValid End DateRange Date to Cutsom reports for CapacityUsage ","Cap_UI_Schedule","Capacity Usage",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2018","Dec","20"},new String[]{"2020","Mar","31"},"Report created successfully."},


			//Current Date and current Month 
			//{"InValid Start DateRange Date to Cutsom reports for CapacityUsage ","Cap_UI_Schedule","Capacity Usage",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"","Dec","11"},new String[]{"2019","Mar","1"},"Report created successfully."},



		};
	}
	@Test(dataProvider = "Custom-info", enabled=true)
	public void Customreports(String secnario,
			String reportName,
			String reportType,
			String []recepientMail,
			String generatetype,
			String reportSoucresType,
			String []startDataRange,
			String[]endDataRange,
			String toastMessage

			) throws ParseException
	{

		test=ExtentManager.getNewTest(secnario);
		//Login to the could console 
		//	ReportHelperPage.login_Spog(Username, password);

		ReportHelperPage.createCustomReportsForAllreportTypes(reportName, reportType, recepientMail, reportSoucresType, generatetype, startDataRange, endDataRange, toastMessage,test);
		ReportHelperPage.deleteReport(reportName, "Report deleted successfully.", test);

	}



	@DataProvider(name = "DeleteScheduleInstances-info")
	public final Object[][] DeleteScheduleInfo() {
		return new Object[][] { 	
			//BackupJobs Reports 
			{"UI-Automation :Monthly schedule report at 10:30 PM ","_UI_Schedule"+RandomStringUtils.randomAlphanumeric(2),"Capacity Usage",new String[] {"123@gmail.com"},"allSources","Schedule","10","30","Monthly","Report schedule created successfully.","Schedule report deleted successfully."},
			{"UI-Automation :Monthly schedule report at 11:30 PM ","_UI_Schedule"+RandomStringUtils.randomAlphanumeric(2),"Capacity Usage",new String[] {"123@gmail.com"},"allSources","Schedule","11","30","Monthly","Report schedule created successfully.","Schedule report deleted successfully."},
			{"UI-Automation :Monthly schedule report at 12:30 PM ","_UI_Schedule"+RandomStringUtils.randomAlphanumeric(2),"Capacity Usage",new String[] {"123@gmail.com"},"allSources","Schedule","12","30","Monthly","Report schedule created successfully.","Schedule report deleted successfully."},

		};
	}
	@Test(dataProvider = "DeleteScheduleInstances-info", enabled=true)
	public void deleteReporInstcanse(String caseType,
			String reportName,
			String reportType,
			String [] recepientMail,
			String reportSoucresType,
			String generatetype,
			String delivaryTime,
			String 	timeType,
			String frequency,
			String toastMessage,
			String DeletedReportToastMessgae
			)
	{

		test=ExtentManager.getNewTest(caseType);
		//Login to the could console 
		//	ReportHelperPage.login_Spog(Username, password);

		ReportHelperPage.createScheduleReportsforallReporTypes(reportName, reportType, recepientMail, reportSoucresType, generatetype, delivaryTime, timeType, frequency, test);
		ReportHelperPage.checkreportnameInManageReportScheduleReportsTable(reportName, test);
		ReportHelperPage.deletScheduleReportInstance(reportName, "Report deleted successfully.");

	}

	@DataProvider(name = "RandomDates-info")
	public final Object[][] RandomDatesInfo() {
		return new Object[][] { 	
			//BackupJobs Reports 
			{"UI-Automation :Create Random Reports","_UI_Schedule"+RandomStringUtils.randomAlphanumeric(2),new String[] {"123@gmail.com"},"allSources","Schedule","Report schedule created successfully.","Schedule report deleted successfully."},
			{"UI-Automation :Create Random Reports","_UI_Schedule"+RandomStringUtils.randomAlphanumeric(2),new String[] {"123@gmail.com"},"allSources","Schedule","Report schedule created successfully.","Schedule report deleted successfully."},
			{"UI-Automation :Create Random Reports","_UI_Schedule"+RandomStringUtils.randomAlphanumeric(2),new String[] {"123@gmail.com"},"allSources","Schedule","Report schedule created successfully.","Schedule report deleted successfully."},

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

		String[] reportTypes = {"Backup Jobs","Recovery Jobs","Data Transfer","Capacity Usage"};
		String randomReporType = (reportTypes[new Random().nextInt(reportTypes.length)]);


		String randomTime= (timeInHours[new Random().nextInt(timeInHours.length)]);
		String randomtype=(timeInMinutes[new Random().nextInt(timeInMinutes.length)]);

		String [] frequecny = {"Daily","Weekly","Monthly"};
		String setRandomFrequecny =(frequecny[new Random().nextInt(frequecny.length)]);

		ReportHelperPage.createScheduleReportsforallReporTypes(reportName, randomReporType, recepientMail, reportSoucresType, generatetype, randomTime, randomtype, setRandomFrequecny, test);
		ReportHelperPage.checkreportnameInManageReportScheduleReportsTable(reportName, test);
		ReportHelperPage.deletScheduleReportInstance(reportName, "Schedule report deleted successfully.");

	}
	//	@AfterMethod
	public void afterMethod(){
		//ReportHelperPage.logout();
		//		ReportHelperPage.destroy();
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
		direct_org_id = spogServer.CreateOrganizationWithCheck(prefix + direct_org_name + org_model_prefix,
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
		ReportHelperPage.logout();

		ReportHelperPage.destroy();
		recycleVolumeInCDandDestroyOrg(org_model_prefix);

	}



	/*****************************Generic**********************************/
	private static String convertTimeStampToDateAndTime(int timeStamp) {

		Date dateTime = new Date(timeStamp * 1000L );

		SimpleDateFormat dateFormater = new SimpleDateFormat("MMMM/dd/yyyy");
		dateFormater.setTimeZone(TimeZone.getTimeZone("IST"));
		String date = dateFormater.format(dateTime);

		return date;
	}



}
