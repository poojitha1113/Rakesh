package ui.spog.analyze.reports;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Array;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

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
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.SpogConstants;
import InvokerServer.Org4SPOGServer;
import InvokerServer.SPOGReportServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;
import net.bytebuddy.utility.RandomString;
import ui.spog.server.DataTransferHelperPage;
import ui.base.common.TableHeaders;
import ui.spog.server.BrandingHelperpage;
import ui.spog.server.CustomerAccountsPageHelper;
import ui.spog.server.DataTransferHelperPage;
import ui.spog.server.SPOGUIServer;



/**
 *  @author Nagamalleswari.Sykam
 *  
 * Created on demand DataTransfer Reports 
 * Created Scheduled DataTransfer Reports 
 * Validated onDemand reports in the Table of reports page
 * Validated Schedule reports in the table of Scheduled reports
 * Deleted the on demand reports 
 * Deleted scheduled reports

 */
public class UI_DataTransferReportsTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private DataTransferHelperPage dataTransferHelperPage;
	private UserSpogServer userSpogServer;
	private SPOGUIServer spogUIServer;
	private SPOGReportServer spogreportserver;
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
	public String curTime,oldTime;


	String ReportName = "Backup_UI_Schedule"+RandomStringUtils.randomAlphanumeric(2);
	String[] timeInHours = /* {"00","01",}*/{"02","03"};//,"04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23"};
	String[] timeInMinutes =  /*{"00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27",}*/{"29","30","31","32","33","34","35","36","37","38"}/*{"39","40","41","42","43","44","45","46","47","48","49","50","51","52","53","54","55","56","57","58","59"}*/;

	String [] frequecny = {"Daily"};//,"Weekly","Monthly"};

	ArrayList<Object> timeDetails = new ArrayList<>();
	ArrayList<String>time_resloution = new ArrayList<>();
	String prefix = RandomStringUtils.randomAlphabetic(6);

	@BeforeClass
	@Parameters({ "baseURI", "port",   "logFolder", "csrAdminUserName", "csrAdminPassword", "buildVersion", "uiBaseURI", "browserType", "maxWaitTimeSec"})
	public void beforeClass(String baseURI, String port, String logFolder, String adminUserName, String adminPassword,  String buildVersion,
			String uiBaseURI, String browserType, int maxWaitTimeSec) throws UnknownHostException {

		org4SpogServer = new Org4SPOGServer(baseURI, port);
		spogServer = new SPOGServer(baseURI, port);
		spogreportserver = new SPOGReportServer(baseURI, port);

		userSpogServer = new UserSpogServer(baseURI, port);
		this.url = uiBaseURI;
		this.csrAdminUserName = adminUserName;
		this.csrAdminPassword = adminPassword;
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(),logFolder);
		test = rep.startTest("beforeClass");

		//prepareEnv();

		dataTransferHelperPage = new DataTransferHelperPage(browserType, maxWaitTimeSec);
		dataTransferHelperPage.openUrl(uiBaseURI);

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
		//	DataTransferHelperPage.login_Spog(direct_org_email, common_password);
		dataTransferHelperPage.login_Spog("eswari.sykam104+rmsp1@gmail.com", "Mclaren@2016");

	}

	//@BeforeMethod
	@Parameters({"uiBaseURI", "browserType", "maxWaitTimeSec"})
	public void login(String uiBaseURI, String browserType, int maxWaitTimeSec) {
		//		brandingpageHelper = new BrandingpageHelper(browserType, maxWaitTimeSec);
		//		brandingpageHelper.openUrl(uiBaseURI);
		//	DataTransferHelperPage.login_Spog("malleswari.sykam101@gmail.com", "Mclaren@2016");


		//nop37xuyspogqa_mallleswari_msp_admin@arcserve.com
		//brandingpageHelper.login_Spog("nop37xuyspogqa_mallleswari_msp_admin@arcserve.com", common_password);
	}



	@DataProvider(name = "OnDemandDataTransferReportsTests-info")
	public final Object[][]  DataTrnaferInfo() {
		return new Object[][] { 	

			{"Success",direct_org_email, common_password,"DataTransfer_Last24hour",new String[] {"eswari@gmail.com"},"allSources","ondemand","Last 24 Hours","Report created successfully."},
			{"Success",direct_org_email, direct_org_email,"DataTransfer_Last7days",new String[] {"eswari@gmail.com"},"allSources","ondemand","Last 7 Days","Report created successfully."},
			{"Success",direct_org_email,direct_org_email,"DataTransfer_Last1Month",new String[] {"eswari@gmail.com"},"allSources","ondemand","Last 1 Month","Report created successfully."},
		};
	}
	@Test(dataProvider = "OnDemandDataTransferReportsTests-info", enabled=false)
	public void CreateOnDemandDataTransferreports(String caseType,
			String username,String password,
			String reportName,
			String [] recepientMailIds,
			String reportSoucresType,
			String genratetype,
			String datarange,
			String toastMessage
			)
	{

		test=ExtentManager.getNewTest(caseType);

		test.log(LogStatus.INFO, "Create On demand dataTransfer reports");
		dataTransferHelperPage.createOnDemandDataTransferReport(reportName,recepientMailIds,reportSoucresType,genratetype,datarange,toastMessage,test);

		test.log(LogStatus.INFO, "Check reports in the Reports Table");
		dataTransferHelperPage.checkreportnameinreportsTable(reportName, test);

		test.log(LogStatus.INFO, "Delete Report from the Reports table");
		dataTransferHelperPage.deleteReport(reportName, "Report deleted successfully.", test);

	}
	@DataProvider(name = "DataTransferReportsschedule-info")
	public final Object[][] ScheduleDataTransferInfo() {
		return new Object[][] { 	

			{"UI-Automation :Monthly schedule report at 2AM ",direct_org_email, common_password,"DataTransfer"+RandomStringUtils.randomAlphanumeric(2),new String[] {"123@gmail.com"},
				"allSources","Schedule","Report schedule created successfully.","Schedule report deleted successfully."},
		};
	}
	@Test(dataProvider = "DataTransferReportsschedule-info", enabled=true)
	public void CreatescheduleDataTransferreports(String caseType,
			String username,String password,
			String reportName,
			String[]recepientMailIds,
			String reportSoucresType,
			String generatetype,
			String toastMessage,
			String deletedReportToastMessage
			)
	{

		for(int i=0;i<timeInHours.length;i++) {
			for(int j=0;j<timeInMinutes.length;j++) {
				for(int k=0;k<frequecny.length;k++) {

					String ReportName =  "DataTransfer" +"UI"+RandomStringUtils.randomAlphanumeric(2)+ "_"+timeInHours[i]+ "_"+timeInMinutes[j]+"_"+ frequecny[k];
					test.log(LogStatus.INFO, "Create Schedule DataTransfer Reports");
					dataTransferHelperPage.createScheduleDataTransferReports(ReportName,recepientMailIds, reportSoucresType, generatetype, timeInHours[i], timeInMinutes[j], frequecny[k], toastMessage, test);

					test.log(LogStatus.INFO, "Check schedule reports in the Managed reports schedules table");
					//dataTransferHelperPage.checkreportnameInManageReportScheduleReportsTable(ReportName,deletedReportToastMessage, test);

				}
			}

		}
	}

	@DataProvider(name = "Custom-info")
	public final Object[][] CustomReportsInfo() {
		return new Object[][] { 	
			//DataTransferJobs Reports 
			//Valid DateRanges
			{"Valid Strat and End DataRanges to create Cutsom reports for DataTransferJobs ","DataTransfer_UI_Schedule",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2018","Dec","20"},new String[]{"2019","Mar","1"},"Report created successfully."},
			{"Valid Strat and End DataRanges to create Cutsom reports for DataTransferJobs","DataTransfer_UI_Schedule",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2018","Dec","15"},new String[]{"2018","Feb","9"},"Report created successfully."},
			{"Valid Strat and End DataRanges to create Cutsom reports for DataTransferJobs","DataTransfer_UI_Schedule",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2019","Feb","21"},new String[]{"2019","Mar","1"},"Report created successfully."},
			{"Valid Strat and End DataRanges to create Cutsom reports for DataTransferJobs","DataTransfer_UI_Schedule",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2019","Feb","8"},new String[]{"2019","Mar","2"},"Report created successfully."},
			{"Valid Strat and End DataRanges to create Cutsom reports for DataTransferJobs","DataTransfer_UI_Schedule",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2019","Jan","8"},new String[]{"2019","Jan","9"},"Report created successfully."},

			//Invalid DateRanges
			{"InValid Start DateRange Year to Cutsom reports for DataTransferJobs ","DataTransfer_UI_Schedule",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2019","Dec","20"},new String[]{"2020","Mar","1"},"Report created successfully."},
			{"InValid End DateRange Year to Cutsom reports for DataTransferJobs ","DataTransfer_UI_Schedule",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2018","Dec","20"},new String[]{"2020","Mar","1"},"Report created successfully."},
			{"InValid Start DateRange Month to Cutsom reports for DataTransferJobs ","DataTransfer_UI_Schedule",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2018","Jan","20"},new String[]{"2019","Mar","1"},"Report created successfully."},
			{"InValid End DateRange Month to Cutsom reports for DataTransferJobs ","DataTransfer_UI_Schedule",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2018","Dec","20"},new String[]{"2020","Jul","1"},"Report created successfully."},
			{"InValid Start DateRange Date to Cutsom reports for DataTransferJobs ","DataTransfer_UI_Schedule",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2018","Dec","11"},new String[]{"2019","Mar","1"},"Report created successfully."},
			{"InValid End DateRange Date to Cutsom reports for DataTransferJobs ","DataTransfer_UI_Schedule",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"2018","Dec","20"},new String[]{"2020","Mar","31"},"Report created successfully."},


			//Current Date and current Month 
			//{"InValid Start DateRange Date to Cutsom reports for DataTransferJobs ","BKP_UI_Schedule","DataTransfer Jobs",new String[] {"eswari@gmail.com"},"ondemand","allSources",new String[]{"var month = currentTime.getMonth() + 1","Dec","11"},new String[]{"2019","Mar","1"},"Report created successfully."},



		};
	}
	@Test(dataProvider = "Custom-info", enabled=false)
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

		test.log(LogStatus.INFO, "Create custom Data transfer Reports ");
		dataTransferHelperPage.createCustomReportsForDataTranferReports(reportName, recepientMail, reportSoucresType, generatetype, startDataRange, endDataRange, toastMessage,test);
		test.log(LogStatus.INFO, "Delete reports from the Reports table");
		dataTransferHelperPage.deleteReport(reportName, "Report deleted successfully.", test);

	}



	@DataProvider(name = "DeleteScheduleInstances-info")
	public final Object[][] DeleteScheduleInfo() {
		return new Object[][] { 	
			//DataTransferJobs Reports 
			{"UI-Automation :Monthly schedule report at 10:30 PM ","DataTransfer_UI_Schedule"+RandomStringUtils.randomAlphanumeric(2),new String[] {"123@gmail.com"},"allSources","Schedule","10","30","Monthly","Report schedule created successfully.","Schedule report deleted successfully."},
			{"UI-Automation :Monthly schedule report at 11:30 PM ","DataTransfer_UI_Schedule"+RandomStringUtils.randomAlphanumeric(2),new String[] {"123@gmail.com"},"allSources","Schedule","11","30","Monthly","Report schedule created successfully.","Schedule report deleted successfully."},
			{"UI-Automation :Monthly schedule report at 12:30 PM ","DataTransfer_UI_Schedule"+RandomStringUtils.randomAlphanumeric(2),new String[] {"123@gmail.com"},"allSources","Schedule","12","30","Monthly","Report schedule created successfully.","Schedule report deleted successfully."},


			{"UI-Automation :Weekly schedule report at 10:30 PM ","DataTransfer_UI_Schedule"+RandomStringUtils.randomAlphanumeric(2),new String[] {"123@gmail.com"},"allSources","Schedule","10","30","Weekly","Report schedule created successfully.","Schedule report deleted successfully."},
			{"UI-Automation :Weekly schedule report at 11:30 PM ","DataTransfer_UI_Schedule"+RandomStringUtils.randomAlphanumeric(2),new String[] {"123@gmail.com"},"allSources","Schedule","11","30","Weekly","Report schedule created successfully.","Schedule report deleted successfully."},
			{"UI-Automation :Weekly schedule report at 12:30 PM ","DataTransfer_UI_Schedule"+RandomStringUtils.randomAlphanumeric(2),new String[] {"123@gmail.com"},"allSources","Schedule","12","30","Weekly","Report schedule created successfully.","Schedule report deleted successfully."},

			{"UI-Automation :Daily schedule report at 10:30 PM ","DataTransfer_UI_Schedule"+RandomStringUtils.randomAlphanumeric(2),new String[] {"123@gmail.com"},"allSources","Schedule","10","30","Daily","Report schedule created successfully.","Schedule report deleted successfully."},
			{"UI-Automation :Daily schedule report at 11:30 PM ","DataTransfer_UI_Schedule"+RandomStringUtils.randomAlphanumeric(2),new String[] {"123@gmail.com"},"allSources","Schedule","11","30","Daily","Report schedule created successfully.","Schedule report deleted successfully."},
			{"UI-Automation :Daily schedule report at 12:30 PM ","DataTransfer_UI_Schedule"+RandomStringUtils.randomAlphanumeric(2),new String[] {"123@gmail.com"},"allSources","Schedule","12","30","Daily","Report schedule created successfully.","Schedule report deleted successfully."},

		};
	}
	@Test(dataProvider = "DeleteScheduleInstances-info", enabled=false)
	public void deleteReporInstancesofDataTransferJobReports(String caseType,
			String reportName,
			String []recepientMail,
			String reportSoucresType,
			String generatetype,
			String delivaryTimeInHours,
			String delivaryTimeInMinutes,
			String frequency,
			String toastMessage,
			String deletedReportToastMessgae
			)
	{

		test=ExtentManager.getNewTest(caseType);

		test.log(LogStatus.INFO, "Create Schedule DataTransfer Reports");
		dataTransferHelperPage.createScheduleDataTransferReports(reportName, recepientMail, reportSoucresType, generatetype, delivaryTimeInHours, delivaryTimeInMinutes, frequency, toastMessage, test);

		test.log(LogStatus.INFO, "Check reports in the Managed Reports schedule page");
		dataTransferHelperPage.checkreportnameInManageReportScheduleReportsTable(reportName,deletedReportToastMessgae, test);

	}


	@DataProvider(name = "RandomDates-info")
	public final Object[][] RandomDatesInfo() {
		return new Object[][] { 	
			//DataTransferJobs Reports 
			{"UI-Automation :Create Random Reports","DataTransfer_UI_Schedule"+RandomStringUtils.randomAlphanumeric(2),new String[] {"123@gmail.com"},"allSources","Schedule","Report schedule created successfully.","Schedule report deleted successfully."},
			{"UI-Automation :Create Random Reports","DataTransfer_UI_Schedule"+RandomStringUtils.randomAlphanumeric(2),new String[] {"123@gmail.com"},"allSources","Schedule","Report schedule created successfully.","Schedule report deleted successfully."},
			{"UI-Automation :Create Random Reports","DataTransfer_UI_Schedule"+RandomStringUtils.randomAlphanumeric(2),new String[] {"123@gmail.com"},"allSources","Schedule","Report schedule created successfully.","Schedule report deleted successfully."},

		};
	}
	@Test(dataProvider = "RandomDates-info", enabled=false)
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
		test.log(LogStatus.INFO, "Sending random time in hours");
		String randomTime= (timeInHours[new Random().nextInt(timeInHours.length)]);

		test.log(LogStatus.INFO, "Sending random time in minutes");
		String randomtype=(timeInMinutes[new Random().nextInt(timeInMinutes.length)]);

		String [] frequecny = {"Daily","Weekly","Monthly"};
		test.log(LogStatus.INFO, "Sending random frequency details");
		String setRandomFrequecny =(frequecny[new Random().nextInt(frequecny.length)]);

		test.log(LogStatus.INFO,"Create scheule DataTransfer Reports");
		dataTransferHelperPage.createScheduleDataTransferReports(reportName, recepientMail, reportSoucresType, generatetype, randomTime, randomtype, setRandomFrequecny, toastMessage, test);

		test.log(LogStatus.INFO,"Check Schedule reports in the managed reports scheduled page ");
		dataTransferHelperPage.checkreportnameInManageReportScheduleReportsTable(reportName, DeletedReportToastMessgae,test);

	}


	@DataProvider(name="SearchByDestnationName_Info")
	public final Object[][]searhcByName(){
		return new Object[][] {
			{"UI_Automation : SerachByName","Data"},
			{"UI_Automation : SerachByName","SoucreName"}
		};
	}
	@Test(dataProvider="SearchByDestnationName_Info")
	public void searchByDestaintionName(String testCase,
			String search_String
			) {
		test = ExtentManager.getNewTest(testCase);

		test.log(LogStatus.INFO,"Search DataTranfer Reports By Soucre Name");
		dataTransferHelperPage.searchDataTranferReortsWithSearchName(search_String, test);
	}



	@DataProvider(name="ApplyDataRangeFilters")
	public final Object[][]DataRangeFilter(){
		return new Object[][] {
			{"UI_Autoamtions:apply filters for DataTransfer reports","HYP2","Date Range;Last 24 Hours","Last 24 Hours",prefix+"filter"},
			{"UI_Autoamtions:apply filters for DataTansfer reports","HYP2","Date Range;Last 7 Days","Last 7 Days",prefix+"SaveSerach2"},
			{"UI_Autoamtions:apply filters for DataTransfer reports","HYP2","Date Range;Last 2 Weeks","Last 2 Weeks",prefix+"SaveSerach3"},
			{"UI_Autoamtions:apply filters for DataTranfer reports","HYP2","Date Range;Last 1 Month","Last 1 Month",prefix+"SaveSerach4"},
			{"UI_Autoamtions:apply filters for DataTranfer reports","HYP","Date Range;Last 1 Month","Last 1 Month",prefix+"SaveSerach5"}

		};
	}

	@Test(dataProvider = "ApplyDataRangeFilters")
	public void saveSerachFilterbyname(String testcase,
			String search_String,
			String filter,
			String DataRange,
			String saveserachName) {

		test = ExtentManager.getNewTest(testcase);

		ArrayList<String>filters= new ArrayList<>();
		ArrayList<HashMap<String, Object>> expectedInfo = null;
		if (filter != null && !filter.isEmpty()) {
			String[] multipleFilters = filter.split(",");
			for (int i = 0; i < multipleFilters.length; i++) {
				filters.add(multipleFilters[i]);
			}	
		}		

		test.log(LogStatus.INFO, "Sending start and end time data range details in milliseconds");
		setDaterangeDetails(DataRange);

		test.log(LogStatus.INFO, "Converting the long  of start time to string ");
		String startTime=Long.toString((long) timeDetails.get(0)); 

		test.log(LogStatus.INFO, "Converting the long  of end time to string ");
		String endTime =Long.toString((long) timeDetails.get(1));

		test.log(LogStatus.INFO, "Send additional url to get the API details for Datatranfer reports");
		String aditional_Url=spogServer.PrepareURL("source_name;=;"+search_String+",create_ts;=;%3C"+startTime+",create_ts;=;%3E"+endTime+",time_resolution;=;"+time_resloution.get(0), "", 0, 100, test);

		test.log(LogStatus.INFO, "Save the DataTransfer reports filters  ");
		dataTransferHelperPage.savesearchfiltersofDataTranferReport(search_String, saveserachName,  test);

		test.log(LogStatus.INFO, "Getting dataTransfer details Information ");
		expectedInfo=getDataTransferSummaryDetails(aditional_Url, test);

		test.log(LogStatus.INFO, "Validate the API and ui Data tranfer reports");
		dataTransferHelperPage.checkAppiledFiltersOnDatarangeReports(search_String, expectedInfo, test);
	}


	@DataProvider(name="ManagedSaveSearch_Info")
	public final Object[][]UpdateMagedSaveSearch(){
		return new Object[][] {
			{"UI_Autoamtions:apply filters for DataTransfer reports","HYP2","Date Range;Last 24 Hours","Last 24 Hours",prefix+"SaveSerach1","HYP11","Date Range;Last 7 Days","Last 7 Days",prefix+"SaveSerach13"+prefix},

			{"UI_Autoamtions:apply filters for DataTransfer reports","HYP2","Date Range;Last 24 Hours","Last 24 Hours",prefix+"SaveSerach2","HYP11","Date Range;Last 2 Weeks","Last 2 Weeks",prefix+"SaveSerach14"+prefix},
			{"UI_Autoamtions:apply filters for DataTransfer reports","HYP2","Date Range;Last 24 Hours","Last 24 Hours",prefix+"SaveSerach3","HYP11","Date Range;Last 1 Month","Last 1 Month",prefix+"SaveSerach15"+prefix},

			{"UI_Autoamtions:apply filters for DataTansfer reports","HYP","Date Range;Last 7 Days","Last 7 Days",prefix+"SaveSerach4","HYP11","Date Range;Last 24 Hours","Last 24 Hours",prefix+"SaveSerach16"+prefix},
			{"UI_Autoamtions:apply filters for DataTansfer reports","HYP","Date Range;Last 7 Days","Last 7 Days",prefix+"SaveSerach5","HYP11","Date Range;Last 2 Weeks","Last 2 Weeks",prefix+"SaveSerach17"+prefix},
			{"UI_Autoamtions:apply filters for DataTansfer reports","HYP","Date Range;Last 7 Days","Last 7 Days",prefix+"SaveSerach6","HYP11","Date Range;Last 1 Month","Last 1 Month",prefix+"SaveSerach18"+prefix},

			{"UI_Autoamtions:apply filters for DataTransfer reports","win10","Date Range;Last 2 Weeks","Last 2 Weeks",prefix+"SaveSerach7","HYP11","Date Range;Last 24 Hours","Last 24 Hours",prefix+"SaveSerach19"+prefix},
			{"UI_Autoamtions:apply filters for DataTransfer reports","win10","Date Range;Last 2 Weeks","Last 2 Weeks",prefix+"SaveSerach8","HYP11","Date Range;Last 7 Days","Last 7 Days",prefix+"SaveSerach20"+prefix},
			{"UI_Autoamtions:apply filters for DataTransfer reports","win10","Date Range;Last 2 Weeks","Last 2 Weeks",prefix+"SaveSerach9","HYP11","Date Range;Last 1 Month","Last 1 Month",prefix+"SaveSerach21"+prefix},


			{"UI_Autoamtions:apply filters for DataTranfer reports","HYP2","Date Range;Last 1 Month","Last 1 Month",prefix+"SaveSerach10","HYP11","Date Range;Last 2 Weeks","Last 2 Weeks",prefix+"SaveSerach22"+prefix},
			{"UI_Autoamtions:apply filters for DataTranfer reports","HYP2","Date Range;Last 1 Month","Last 1 Month",prefix+"SaveSerach11","HYP11","Date Range;Last 24 Hours","Last 24 Hours",prefix+"SaveSerach23"+prefix},
			{"UI_Autoamtions:apply filters for DataTranfer reports","HYP2","Date Range;Last 1 Month","Last 1 Month",prefix+"SaveSerach12","HYP11","Date Range;Last 7 Days","Last 7 Days",prefix+"SaveSerach24"+prefix},

		};
	}

	@Test(dataProvider = "ManagedSaveSearch_Info",enabled=true)
	public void managedSaveSearchFilters(String testcase,
			String search_String,
			String filter,
			String APIDataRange,
			String saveserachName,
			String UpdateSearch_String,
			String updateFilter,
			String updateAPIDateRange,
			String updateSave_SearchName) {

		test = ExtentManager.getNewTest(testcase);

		ArrayList<String>filters= new ArrayList<>();

		ArrayList<HashMap<String, Object>> expectedInfo = null;
		if (filter != null && !filter.isEmpty()) {
			String[] multipleFilters = filter.split(",");
			for (int i = 0; i < multipleFilters.length; i++) {
				filters.add(multipleFilters[i]);
			}	
		}		

		test.log(LogStatus.INFO, "Sending start and end time data range details in milliseconds");
		setDaterangeDetails(APIDataRange);
		test.log(LogStatus.INFO, "Converting the long  of start time to string ");

		String startTime=Long.toString((long) timeDetails.get(0)); 
		test.log(LogStatus.INFO, "Converting the long  of end time to string ");

		String endTime =Long.toString((long) timeDetails.get(1));
		dataTransferHelperPage.applyfiltersonDataTransferReport(search_String, filters, test);
		dataTransferHelperPage.clickonSaveAndSeacrh(saveserachName);

		test.log(LogStatus.INFO, "Send additional url to get the API details for Datatranfer reports");
		String aditional_Url=spogServer.PrepareURL("source_name;=;"+search_String+",create_ts;=;%3C"+startTime+",create_ts;=;%3E"+endTime+",time_resolution;=;"+time_resloution.get(0), "", 0, 100, test);

		test.log(LogStatus.INFO, "Getting dataTransfer details Information ");
		expectedInfo=getDataTransferSummaryDetails(aditional_Url, test);

		test.log(LogStatus.INFO, "Check applied filter details with API details");
		dataTransferHelperPage.checkAppiledFiltersOnDatarangeReports(search_String, expectedInfo, test);

		ArrayList<String>upDateFilters= new ArrayList<>();

		if (updateFilter != null && !updateFilter.isEmpty()) {
			String[] multipleFilters = updateFilter.split(",");
			for (int i = 0; i < multipleFilters.length; i++) {
				upDateFilters.add(multipleFilters[i]);
			}	
		}		

		test.log(LogStatus.INFO, "Update Managed save search DataTranfer Details");
		dataTransferHelperPage.updateManagedSaveSerachFilter(saveserachName, updateSave_SearchName,UpdateSearch_String,upDateFilters,test);

		test.log(LogStatus.INFO, "Sending start and end time data range details in milliseconds");
		setDaterangeDetails(updateAPIDateRange);

		test.log(LogStatus.INFO, "Converting the long  of start time to string ");
		startTime=Long.toString((long) timeDetails.get(0));

		test.log(LogStatus.INFO, "Converting the long  of end time to string ");
		endTime =Long.toString((long) timeDetails.get(1));

		test.log(LogStatus.INFO, "Send additional url to get the API details for Datatranfer reports");
		aditional_Url=spogServer.PrepareURL("source_name;=;"+UpdateSearch_String+",create_ts;=;%3C"+startTime+",create_ts;=;%3E"+endTime+",time_resolution;=;"+time_resloution.get(0), "", 0, 100, test);

		test.log(LogStatus.INFO, "Getting dataTransfer details Information ");
		expectedInfo=getDataTransferSummaryDetails(aditional_Url, test);

		test.log(LogStatus.INFO, "Check applied filter details with API details");
		dataTransferHelperPage.checkAppiledFiltersOnDatarangeReports(UpdateSearch_String, expectedInfo, test);



	}

	@Test(dataProvider="ApplyDataRangeFilters")
	public void deleteSaveSeacrchFilter(String testcase,
			String search_String,
			String filter,
			String DataRange,
			String saveserachName) {

		test = ExtentManager.getNewTest(testcase);

		ArrayList<String>filters= new ArrayList<>();

		ArrayList<HashMap<String, Object>> expectedInfo = null;
		if (filter != null && !filter.isEmpty()) {
			String[] multipleFilters = filter.split(",");
			for (int i = 0; i < multipleFilters.length; i++) {
				filters.add(multipleFilters[i]);
			}	
		}
		setDaterangeDetails(DataRange);
		test.log(LogStatus.INFO, "Converting the long  of start time to string ");
		String startTime=Long.toString((long) timeDetails.get(0)); 

		test.log(LogStatus.INFO, "Converting the long  of end time to string ");
		String endTime =Long.toString((long) timeDetails.get(1));

		test.log(LogStatus.INFO, "Applying filetrs on DataTranfer reports");
		dataTransferHelperPage.applyfiltersonDataTransferReport(search_String, filters, test);

		test.log(LogStatus.INFO, "Save the applied filter details ");
		dataTransferHelperPage.clickonSaveAndSeacrh(saveserachName);

		test.log(LogStatus.INFO, "Preapare additonal url to get the API details for DataTranfer Reports ");
		String aditional_Url=spogServer.PrepareURL("source_name;=;"+search_String+",create_ts;=;%3C"+startTime+",create_ts;=;%3E"+endTime+",time_resolution;=;"+time_resloution.get(0), "", 0, 100, test);

		test.log(LogStatus.INFO, "Getting dataTransfer details Information ");
		expectedInfo=getDataTransferSummaryDetails(aditional_Url, test);

		test.log(LogStatus.INFO, "Validating the DataTranfer details ");
		dataTransferHelperPage.checkAppiledFiltersOnDatarangeReports(search_String, expectedInfo, test);

		test.log(LogStatus.INFO, "Delete Save Seacrh filter");
		dataTransferHelperPage.deleteSaveSerachFilter(saveserachName, test);

	}


	@DataProvider(name="ClickOnSourceName")
	public final Object[][]ClickOnSourceName(){
		return new Object[][] {
			{"UI_Autoamtions:apply filters for DataTransfer reports","DIRECTAGENT"},

		};
	}

	@Test(dataProvider = "ClickOnSourceName",enabled=false)
	public void clickOnSourceNameInDataTranferReports(String testcase,
			String Source_Name
			) {

		test = ExtentManager.getNewTest(testcase);
		
		test.log(LogStatus.INFO, "Click On the Source Name ");
		dataTransferHelperPage.clickonSoucreNameWithCheck(Source_Name, test);
	}
	//	@AfterMethod
	public void afterMethod(){
		//DataTransferHelperPage.logout();
		//		DataTransferHelperPage.destroy();
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
		dataTransferHelperPage.logout();

		//DataTransferHelperPage.destroy();
		//recycleVolumeInCDandDestroyOrg(org_model_prefix);

	}

	public void setDaterangeDetails(String DataRange) {

		int daysToDecrement = 0;
		String time_Resolution = null;

		Calendar cal = Calendar.getInstance();
		System.out.println("Now : " + cal.getTime());

		Date currentTime = cal.getTime();
		long curTime = currentTime.getTime()/1000;
		if(DataRange=="Last 24 Hours") {
			daysToDecrement = -1;	
			time_Resolution="hour";
		}else if(DataRange=="Last 7 Days") {
			daysToDecrement = -7;
			time_Resolution="day";
		}else if(DataRange=="Last 2 Weeks") {
			daysToDecrement = -14;	
			time_Resolution ="day";
		}else if(DataRange=="Last 1 Month") {
			daysToDecrement = -30;	
			time_Resolution="day";
		}
		cal.add(Calendar.DATE, daysToDecrement);
		System.out.println("Date after increment: " + cal.getTime());
		Date Date = cal.getTime();
		long oldTime=Date.getTime()/1000;
		timeDetails.add(curTime);
		timeDetails.add(oldTime);
		time_resloution.add(time_Resolution);
	}


	@SuppressWarnings("null")
	public ArrayList<HashMap<String, Object>> getDataTransferSummaryDetails(String additionalUrl,ExtentTest test){
		spogServer.userLogin("eswari.sykam102+s35sub@gmail.com", "Mclaren@2016");
		String validToken = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Get DataTrnafer summary details");
		Response response=spogreportserver.getDataTranferSummaryDetails(validToken,additionalUrl,SpogConstants.SUCCESS_GET_PUT_DELETE,null,test);


		ArrayList<HashMap<String, Object>>dataTrannsferDetails = new ArrayList<>();
		HashMap<String, Object> dataTranferDetailsInfo=null;
		ArrayList<HashMap<String, Object>> actualInfo = response.then().extract().path("data");
		int detailsCount = actualInfo.size();

		for(int i = 0;i<detailsCount;i++) {

			dataTranferDetailsInfo = new HashMap<>();
			dataTranferDetailsInfo.put(TableHeaders.source,response.then().extract().path("data["+i+"].source.source_name"));
			System.out.println(dataTranferDetailsInfo.put(TableHeaders.source,response.then().extract().path("data["+i+"].source.source_name")));
			dataTrannsferDetails.add(dataTranferDetailsInfo);
		}

		return dataTrannsferDetails;
	}

}




