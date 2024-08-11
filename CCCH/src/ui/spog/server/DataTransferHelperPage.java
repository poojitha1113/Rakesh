package ui.spog.server;

import java.util.ArrayList;
import java.util.HashMap;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import ui.spog.pages.analyze.AnalyzePage;
import ui.spog.pages.analyze.BackupJobReportsPage;
import ui.spog.pages.analyze.CapacityUsageReportsPage;
import ui.spog.pages.analyze.DataTransferReportsPage;
import ui.spog.pages.analyze.ManagedReportSchedulesPage;
import ui.spog.pages.analyze.RecoveryJobReportsPage;
import ui.spog.pages.analyze.ReportsPage;


public class DataTransferHelperPage extends SPOGUIServer {
	DataTransferReportsPage datatranferReportsPage = new DataTransferReportsPage();
	ReportsPage reportsPage = new  ReportsPage();
	public DataTransferHelperPage(String browserType, int maxTimeWaitSec) {
		super(browserType, maxTimeWaitSec);
		// TODO Auto-generated constructor stub
	}
	/*
	 * Create on Demand DataTranfer Reports 
	 * 
	 * @author Nagamalleswari.Sykam
	 * @param click 
	 */

	public void createOnDemandDataTransferReport(String reportName,
			String [] recepientMailIds,
			String reportSoucres,
			String genratetype,
			String datarange,
			String toastMessage,
			ExtentTest test) {
		DataTransferReportsPage dataTranferPage = goToDataTransferReportsPage();
		test.log(LogStatus.INFO, "Click on Create Report Button");
		reportsPage.clickOnCreateReportBtn();

		test.log(LogStatus.INFO, "Verify the Report title");
		reportsPage.checkCreateReportTitle(reportName);


		test.log(LogStatus.INFO, "Set the ReportName");
		reportsPage.setReportName(reportName);


		test.log(LogStatus.INFO, "Set the reportType means Report for selected Sources Or Report For all sources ");
		reportsPage.setSlectionOfSoucres(reportSoucres);


		test.log(LogStatus.INFO, "Set EmailRecepient Details");
		reportsPage.setEmailRecepients(recepientMailIds);

		test.log(LogStatus.INFO, "Set Generate tyap and Data range to create ond demans Reports");
		reportsPage.setDetailsToCreateOndemandReport(genratetype,datarange);
		reportsPage.createbtn(toastMessage);

	}

	/*
	 * Create schedule backupjob reports 
	 * 
	 * @author Nagamalleswari.Sykam
	 * @param click 
	 */
	public void createScheduleDataTransferReports(String reportName,
			String [] recepientMailIds,
			String reportSoucres,
			String generateType,
			String delevaryTimeInHours,
			String delivaryTimeInMinutes,
			String frequency,
			String toastMessage,
			ExtentTest test) {

		DataTransferReportsPage dataTranferPage = goToDataTransferReportsPage();
		test.log(LogStatus.INFO, "Click on Create Report Button");
		reportsPage.clickOnCreateReportBtn();

		test.log(LogStatus.INFO, "Check the report title");
		reportsPage.checkCreateReportTitle(reportName);
		//Give the ReportName 
		if (reportName!=null) {

			test.log(LogStatus.INFO, "Set the report name ");
			reportsPage.setReportName(reportName);

			test.log(LogStatus.INFO, "Set the repot sources tye(Report for Selected sources or all sources )");
			reportsPage.setSlectionOfSoucres(reportSoucres);

			test.log(LogStatus.INFO, "Set the  Recepient emial details ");
			reportsPage.setEmailRecepients(recepientMailIds);

			test.log(LogStatus.INFO, "Set the generatetype and delevaty tiem details to create schedule reports  ");
			reportsPage.setGenerateTypeDetails(generateType,delevaryTimeInHours,delivaryTimeInMinutes);

			test.log(LogStatus.INFO, "Set Frequency detials ");
			reportsPage.setFrequencyDetails(frequency);

		}else {
			System.out.println("the ReportNmae is: "+reportName + "is not valid");
		}
		test.log(LogStatus.INFO, "Click on CreateReport Button ");
		reportsPage.createbtn(toastMessage);


	}


	/**
	 * @author Nagamalleswari.Sykam
	 * Create CustomReports For BackupJob Reports
	 * @param reportName
	 * @param recepientMail
	 * @param reportSoucres
	 * @param genratetype
	 * @param startDatarnage
	 * @param endDataRange
	 * @param toastMessage
	 * @param test
	 */

	public void createCustomReportsForDataTranferReports( String reportName,
			String[] recepientMail,
			String  reportSoucres,
			String  genratetype,
			String[]startDatarnage, 
			String[]endDataRange,
			String toastMessage,
			ExtentTest test) {

		DataTransferReportsPage dataTranferPage = goToDataTransferReportsPage();
		//reportPage.createCustomReportsWithReportType(reportName,ReportType, recepientMail, reportSoucres, genratetype, startDatarnage, endDataRange, test);

		test.log(LogStatus.INFO, "Click on Create Report Button");
		reportsPage.clickOnCreateReportBtn();

		test.log(LogStatus.INFO, "Verify the Report title");
		reportsPage.checkCreateReportTitle(reportName);


		test.log(LogStatus.INFO, "Set the ReportName");
		reportsPage.setReportName(reportName);


		test.log(LogStatus.INFO, "Set the Report for selected Sources Or Report For all sources ");
		reportsPage.setSlectionOfSoucres(reportSoucres);


		test.log(LogStatus.INFO, "Set mEmailRecepient Details");
		reportsPage.setEmailRecepients(recepientMail);

		test.log(LogStatus.INFO, "Set DataRange and Generate Type");
		reportsPage.setDetailsToCreateOndemandReport(genratetype, "Custom");


		test.log(LogStatus.INFO, "Set Start and end datarange details ");
		boolean calenderDeatails = reportsPage.setCustomReportDetails(startDatarnage,endDataRange);
		if(calenderDeatails) {
			reportsPage.applyBtn();
		}else {
			reportsPage.cancelBtn();
		}

		reportsPage.createbtn(null);
	}

	/**
	 * @author Nagamalleswari.Sykam
	 * @param reportname
	 * @param toastMessage
	 */
	public void deletScheduleReportInstance(String reportname,String toastMessage,ExtentTest test) {

		test.log(LogStatus.INFO, "Go To Managed Reports schedules page ");
		ManagedReportSchedulesPage managedReportsSchedulesPage= goToManagedReportSchedulesPage();

		test.log(LogStatus.INFO, "Delete Schedule reports with instance ");
		managedReportsSchedulesPage.deleteReportScheduleInstance(reportname, toastMessage);
	}

	/**
	 * Check onDemand reports in the Reports Table
	 * @author Nagamalleswari.Sykam
	 * @param ReportName
	 * @param test
	 */
	public void checkreportnameinreportsTable(String ReportName,ExtentTest test) {
		test.log(LogStatus.INFO, "Go to Reports Page");
		ReportsPage reportPage= goToReportspage();

		test.log(LogStatus.INFO, "Check reports in the Reports Page ");
		reportPage.checkreportsinReportsTable(ReportName,test);

	}

	/**
	 * @author Nagamalleswari.Sykam
	 * @param reportName
	 * @param deletedReportToastMessage
	 * @param test
	 */
	public void checkreportnameInManageReportScheduleReportsTable(String reportName,String deletedReportToastMessage,ExtentTest test) {
		test.log(LogStatus.INFO, "Go to the Managed Reports schedules page");
		ManagedReportSchedulesPage managedReportsSchedulesPage= goToManagedReportSchedulesPage();

		test.log(LogStatus.INFO, "Check ScheduleReports in the Managed Reports Schedule Table");
		managedReportsSchedulesPage.checkreportsinReportsMangedReportschedulesTable(reportName, test);

		test.log(LogStatus.INFO, "Delete Schedule reports form the Managed Reports Schedules Page");
		managedReportsSchedulesPage.deletereportByNameinManagedReportSchedulesTable(reportName, deletedReportToastMessage);


	}

	/**
	 * @author Nagamalleswari.Sykam
	 * @param reportname
	 * @param toastMessage
	 * @param test
	 */
	public void deleteReport(String reportname,String toastMessage,ExtentTest test) {

		test.log(LogStatus.INFO, "Go to Reports Page ");
		ReportsPage reportPage= goToReportspage();

		test.log(LogStatus.INFO, "Delete Report by Report Name ");
		reportPage.deletereportByName(reportname,toastMessage);
	}


	/**
	 * Search DataTranfer Reports with Name
	 * @author Nagamalleswari.Sykam
	 * @param searchString
	 * @param saveSearchName
	 * @param test
	 */
	public void searchDataTranferReortsWithSearchName(String searchString,ExtentTest test) {
		test.log(LogStatus.INFO, "Got to DataTranfer Reports ");
		DataTransferReportsPage datatranferReportsPage = new DataTransferReportsPage();
		
		test.log(LogStatus.INFO, "Search DataTransfer reports by name");
		datatranferReportsPage.searchByString(searchString);
		
	}
	
	/**
	 * Apply Filters on DataTranfer Reports
	 * @author Nagamalleswari.Sykam
	 * @param searchString
	 * @param filters
	 * @param test
	 */

	public void applyfiltersonDataTransferReport(String searchString,ArrayList<String> filters,ExtentTest test) {
		test.log(LogStatus.INFO, "Go to DataTranfer Reports Page ");
		DataTransferReportsPage datatranferReportsPage = goToDataTransferReportsPage();
		
		test.log(LogStatus.INFO, "Search DataTranfer Reports By Name ");
		datatranferReportsPage.searchByString(searchString);

		
		test.log(LogStatus.INFO, "Apply dataRange Filters");
		datatranferReportsPage.applyFilterDetails(filters, test);

	}

	/**
	 * Check Applied Data Range Reports 
	 * @author Nagamalleswari.Sykam
	 * @param searchString
	 * @param expectedInfo
	 * @param test
	 */
	public void checkAppiledFiltersOnDatarangeReports(String searchString,ArrayList<HashMap<String, Object>> expectedInfo,ExtentTest test) {

		test.log(LogStatus.INFO, "Check DataTranfer Reports with Search String");
		datatranferReportsPage.checkDataTranferDetailswithSerachString(searchString, expectedInfo);

	}

	
	/***
	 * Save applied Filters Of Data Transfer Reports
	 * @author Nagamalleswari.Sykam
	 * @param serachString
	 * @param saveSeacrhName
	 * @param test
	 */

	public void savesearchfiltersofDataTranferReport(String serachString,String saveSeacrhName,ExtentTest test) {
		
		test.log(LogStatus.INFO, "Go to Data Tranfer Reports");
		DataTransferReportsPage datatranferReportsPage = goToDataTransferReportsPage();
		test.log(LogStatus.INFO, "Serach Data tranfer Reports with the source nmae");
		searchDataTranferReortsWithSearchName(serachString, test);
		test.log(LogStatus.INFO, "Save search Ffilters");
		datatranferReportsPage.clickSaveSearchAndSave(saveSeacrhName);

	}

	/**
	 * Click on Save search
	 * @author Nagamalleswari.Sykam
	 * @param saveSerachName
	 */

	public void clickonSaveAndSeacrh(String saveSerachName) {
		datatranferReportsPage.clickSaveSearchAndSave(saveSerachName);

	}

	
	/**
	 * Clcik on Managed Save Search button
	 * @author Nagamalleswari.Sykam
	 * @param serachString
	 * @param saveSeacrhName
	 * @param test
	 */

	public void clickOnManagedSaveSeacrh(String serachString,String saveSeacrhName,ExtentTest test) {
		datatranferReportsPage.clickManageSavedSearch();
	}
	
	
	/**
	 * Delete Managed Save Search Filter
	 * @author Nagamalleswari.Sykam
	 * @param saveSeacrhName
	 * @param test
	 */

	public void deleteSaveSerachFilter(String saveSeacrhName,ExtentTest test) {
		test.log(LogStatus.INFO, "Delete Save search filters");
		datatranferReportsPage.deleteSavedSearch( saveSeacrhName,  test);
	}

	
	/**
	 * Update Managed Save search filters
	 * @author Nagamalleswari.Sykam
	 * @param saveserachName
	 * @param updateSave_SearchName
	 * @param updateSave_SearchString
	 * @param updatefilters
	 * @param test
	 */
	public void updateManagedSaveSerachFilter(String saveserachName,String updateSave_SearchName,String updateSave_SearchString,ArrayList<String> updatefilters,ExtentTest test) {
		test.log(LogStatus.INFO, " Check the saveserach filter is in active");
		datatranferReportsPage.selectSavedSearch(saveserachName, false, test);
		
		test.log(LogStatus.INFO, " Update Managed save search filters");
		datatranferReportsPage.updateManagedSaveSearchFilters(saveserachName, updateSave_SearchName, updateSave_SearchString, updatefilters, test);
	}

	
	
	public void clickonSoucreNameWithCheck(String SoucreName,ExtentTest test) {
		test.log(LogStatus.INFO, "Go to Data Tranfer Reports");
		DataTransferReportsPage datatranferReportsPage = goToDataTransferReportsPage();
		
		test.log(LogStatus.INFO, "Serach Data tranfer Reports with the source nmae");
		datatranferReportsPage.clickonSoucreName(SoucreName);
		
		test.log(LogStatus.INFO, "Check source name in the Protect page");
	//	datatranferReportsPage.checkSoucrenameWihCheckInProtectPage(SoucreName);
	}


}



