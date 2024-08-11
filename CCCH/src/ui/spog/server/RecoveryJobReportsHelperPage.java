package ui.spog.server;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import ui.spog.pages.analyze.AnalyzePage;
import ui.spog.pages.analyze.BackupJobReportsPage;
import ui.spog.pages.analyze.CapacityUsageReportsPage;
import ui.spog.pages.analyze.ManagedReportSchedulesPage;
import ui.spog.pages.analyze.RecoveryJobReportsPage;
import ui.spog.pages.analyze.ReportsPage;

public class RecoveryJobReportsHelperPage extends SPOGUIServer {

	ReportsPage reportsPage = new  ReportsPage();
	public RecoveryJobReportsHelperPage(String browserType, int maxTimeWaitSec) {
		super(browserType, maxTimeWaitSec);
		// TODO Auto-generated constructor stub
	}
	/*
	 * Create on Demand reports
	 * 
	 * @author Nagamalleswari.Sykam
	 * @param click 
	 */

	public void createOnDemandRecoveryJobReports(String reportName,
			String [] recepientMailIds,
			String reportSoucres,
			String genratetype,
			String datarange,
			String toastMessage,
			ExtentTest test) {
		RecoveryJobReportsPage recoveryJobsReports = goToRecoveryJobReportsPage();
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
	public void createScheduleRecoveryJobReports(String reportName,
			String [] recepientMailIds,
			String reportSoucres,
			String generateType,
			String delevaryTimeInHours,
			String delevaryTimeInMinutes,
			String frequency,
			String toastMessage,
			ExtentTest test) {

		RecoveryJobReportsPage recoveryJobsReports = goToRecoveryJobReportsPage();
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
			reportsPage.setGenerateTypeDetails(generateType,delevaryTimeInHours,delevaryTimeInMinutes);

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

	public void createCustomReportsForRecoveryJobs( String reportName,
			String[] recepientMail,
			String  reportSoucres,
			String  genratetype,
			String[] startDatarnage, 
			String[] endDataRange,
			String toastMessage,
			ExtentTest test) {

		RecoveryJobReportsPage recoveryJobsReports = goToRecoveryJobReportsPage();
		
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
	public void deletScheduleReportInstance(String reportname,String toastMessage) {
		ManagedReportSchedulesPage managedReportsSchedulesPage= goToManagedReportSchedulesPage();
		managedReportsSchedulesPage.deleteReportScheduleInstance(reportname, toastMessage);
	}

	/*
	 * Check  report name in the reports table 
	 * 
	 * @author Nagamalleswari.Sykam
	 *  
	 */
	public void checkreportnameinreportsTable(String ReportName,ExtentTest test) {
		ReportsPage reportPage= goToReportspage();
		reportPage.checkreportsinReportsTable(ReportName,test);

	}

	/*
	 * Check  Schedule reports in  the managedreportsSchedules Page 
	 * 
	 * @author Nagamalleswari.Sykam
	 * 
	 */
	public void checkreportnameInManageReportScheduleReportsTable(String reportName,String toastMessage,ExtentTest test) {
		ManagedReportSchedulesPage managedReportsSchedulesPage= goToManagedReportSchedulesPage();
		managedReportsSchedulesPage.checkreportsinReportsMangedReportschedulesTable(reportName, test);
		managedReportsSchedulesPage.deleteReportScheduleInstance(reportName, toastMessage);

	}

	/*
	 * Delete on demand report form the reports Table 
	 * 
	 * @author Nagamalleswari.Sykam
	 * @param click 
	 */
	public void deleteReport(String reportname,String toastMessage,ExtentTest test) {
		ReportsPage reportPage= goToReportspage();

		//reportPage.checkreportsinReportsTable(report name,test);
		reportPage.deletereportByName(reportname,toastMessage);
	}


	/*
	 * Delete on Schedule report form the ManagedReportSchedules table
	 * 
	 * @author Nagamalleswari.Sykam
	 *
	 */
	public void deletereportByNameinManagedReportSchedulesTable(String reportName,String toastMessage,ExtentTest test) {
		ManagedReportSchedulesPage managedReportsSchedulesPage= goToManagedReportSchedulesPage();
		managedReportsSchedulesPage.deletereportByNameinManagedReportSchedulesTable(reportName, toastMessage);
	}


	public void applyFilterOnTop10soucres(String filterName) {
		RecoveryJobReportsPage  recoveryJobReports = goToRecoveryJobReportsPage();
		recoveryJobReports.applyingFilterOnTop10Sources(filterName);
		//backupjobreports.veriryTop10soucres();
	}
}
