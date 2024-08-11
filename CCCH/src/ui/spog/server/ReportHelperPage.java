package ui.spog.server;

import java.util.HashMap;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import InvokerServer.SPOGServer;
import ui.base.common.BasePage;
import ui.spog.pages.analyze.AnalyzePage;
import ui.spog.pages.analyze.BackupJobReportsPage;
import ui.spog.pages.analyze.ManagedReportSchedulesPage;
import ui.spog.pages.analyze.RecoveryJobReportsPage;
import ui.spog.pages.analyze.ReportsPage;


public class ReportHelperPage  extends SPOGUIServer{

	static String reportsPage= "https://tcc.arcserve.com/analyze/reports/all";

	public ReportHelperPage(String browserType, int maxTimeWaitSec) {
		super(browserType, maxTimeWaitSec);
		// TODO Auto-generated constructor stub
	}


	/**
	 * @author Nagamalleswari.Sykam 
	 * create OnDemand Reports with out ReportType
	 * @param ReportName
	 * @param recepientMail
	 * @param reportSoucres
	 * @param Genratetype
	 * @param Datarange
	 * @param toastMessage
	 * @param test
	 */
	public void createOnDemandReportWithOutReportType(String reportName,
			String [] recepientMail,
			String reportSoucres,
			String genratetype,
			String datarange,
			String toastMessage,
			ExtentTest test) {
		ReportsPage reportPage= goToReportspage();
		test.log(LogStatus.INFO, "Click on Create Report Button");
		reportPage.clickOnCreateReportBtn();

		test.log(LogStatus.INFO, "Verify the Report title");
		reportPage.checkCreateReportTitle(reportName);


		test.log(LogStatus.INFO, "Set the ReportName");
		reportPage.setReportName(reportName);


		test.log(LogStatus.INFO, "Set the reportType means Report for selected Sources Or Report For all sources ");
		reportPage.setSlectionOfSoucres(reportSoucres);


		test.log(LogStatus.INFO, "Set EmailRecepient Details");
		reportPage.setEmailRecepients(recepientMail);

		test.log(LogStatus.INFO, "Set Generate tyap and Data range to create ond demans Reports");
		reportPage.setDetailsToCreateOndemandReport(genratetype,datarange);
		reportPage.createbtn(toastMessage);

	}


	/**
	 * @author Nagamalleswari.Sykam
	 * 
	 * Create ScheduleReports 
	 * @param reportName
	 * @param recepientMail
	 * @param reportSoucres
	 * @param generatetype
	 * @param delevaryTime
	 * @param timeType
	 * @param frequency
	 * @param test
	 */
	public void createScheduleReportsWithOutReportType(String reportName,
			String [] recepientMail,
			String reportSoucres,
			String generatetype,
			String delevaryTimeInHours,
			String delevaryTimeInMinutes,
			String frequency,ExtentTest test) {
		ReportsPage reportPage= goToReportspage();

		test.log(LogStatus.INFO, "Click on Create Report Button");
		reportPage.clickOnCreateReportBtn();

		test.log(LogStatus.INFO, "Check the report title");
		reportPage.checkCreateReportTitle(reportName);
		//Give the ReportName 
		if (reportName!=null) {

			test.log(LogStatus.INFO, "Set the report name ");
			reportPage.setReportName(reportName);

			test.log(LogStatus.INFO, "Set the repot sources tye(Report for Selected sources or all sources )");
			reportPage.setSlectionOfSoucres(reportSoucres);

			test.log(LogStatus.INFO, "Set the  Recepient emial details ");
			reportPage.setEmailRecepients(recepientMail);

			test.log(LogStatus.INFO, "Set the generatetype and delevaty tiem details to create schedule reports  ");
			reportPage.setGenerateTypeDetails(generatetype,delevaryTimeInHours,delevaryTimeInMinutes);

			test.log(LogStatus.INFO, "Set Frequency detials ");
			reportPage.setFrequencyDetails(frequency);

		}else {
			System.out.println("the ReportNmae is: "+reportName + "is not valid");
		}

	}

	/**
	 * @author Nagamalleswari.Sykam
	 * @param reportName
	 * @param reportType
	 * @param recepientMail
	 * @param reportSoucres
	 * @param generatetype
	 * @param delevaryTime
	 * @param timeType
	 * @param frequency
	 * @param test
	 */

	public void createScheduleReportsforallReporTypes(String reportName,
			String reportType,
			String [] recepientMail,
			String reportSoucres,
			String generatetype,
			String delevaryTimeInHours,
			String delevaryTimeInMinutes,
			String frequency,
			ExtentTest test) {
		ReportsPage reportPage= goToReportspage();

		test.log(LogStatus.INFO, "Click on Create Report Button");
		reportPage.clickOnCreateReportBtn();

		test.log(LogStatus.INFO, "Click on Report type");
		reportPage.setReportType(reportType);

		test.log(LogStatus.INFO, "Check the report title");
		reportPage.checkCreateReportTitle(reportName);
		//Give the ReportName 
		if (reportName!=null) {

			test.log(LogStatus.INFO, "Set the report name ");
			reportPage.setReportName(reportName);

			test.log(LogStatus.INFO, "Set the repot sources tye(Report for Selected sources or all sources )");
			reportPage.setSlectionOfSoucres(reportSoucres);

			test.log(LogStatus.INFO, "Set the  Recepient emial details ");
			reportPage.setEmailRecepients(recepientMail);

			test.log(LogStatus.INFO, "Set the generatetype and delevaty tiem details to create schedule reports  ");
			reportPage.setGenerateTypeDetails(generatetype,delevaryTimeInHours,delevaryTimeInMinutes);


			test.log(LogStatus.INFO, "Set Frequency detials ");
			reportPage.setFrequencyDetails(frequency);

			test.log(LogStatus.INFO, "Click on create button");
			reportPage.createbtn(null);

		}else {
			System.out.println("the ReportNmae is: "+reportName + "is not valid");
		}

	}


	/*
	 * @author Naga  Malleswari
	 * 
	 * Creation of on demand reports for all reports WithOutReportType
	 */

	public void ondemandReportCreationForallReportTypes(String reportName,
			String reportType,
			String [] recepientMail,
			String reportSoucres,
			String  genratetype,
			String datarange,
			String toastMessage,
			ExtentTest test) {
		ReportsPage reportPage= goToReportspage();


		if(reportName!=null) {
			test.log(LogStatus.INFO, "Click on Create Report Button");
			reportPage.clickOnCreateReportBtn();

			test.log(LogStatus.INFO, "Verify the Report title");
			reportPage.checkCreateReportTitle(reportName);


			test.log(LogStatus.INFO, "Set the ReportName");
			reportPage.setReportName(reportName);


			test.log(LogStatus.INFO, "Set the Report for selected Sources Or Report For all sources ");
			reportPage.setSlectionOfSoucres(reportSoucres);


			test.log(LogStatus.INFO, "Set EmailRecepient Details");
			reportPage.setEmailRecepients(recepientMail);

			test.log(LogStatus.INFO, "Set DataRange and Generate Type");
			reportPage.setDetailsToCreateOndemandReport(genratetype, datarange);

			test.log(LogStatus.INFO, "Set DataRange and Generate Type");
			reportPage.setReportType(reportType);

			test.log(LogStatus.INFO, "Click on Create Button");
			reportPage.createbtn(toastMessage);
		}
		reportPage.cancelBtn();
	}




	/**
	 * create custom Reports for all reportType 
	 * @param reportName
	 * @param ReportType
	 * @param recepientMail
	 * @param reportSoucres
	 * @param genratetype
	 * @param startDatarnage
	 * @param endDataRange
	 * @param toastMessage
	 * @param test
	 */


	public void createCustomReportsForAllreportTypes( String reportName,
			String ReportType,
			String[] recepientMail,
			String  reportSoucres,
			String  genratetype,
			String[] startDatarnage, 
			String[] endDataRange,
			String toastMessage,
			ExtentTest test) {

		ReportsPage reportPage= goToReportspage();
		//reportPage.createCustomReportsWithReportType(reportName,ReportType, recepientMail, reportSoucres, genratetype, startDatarnage, endDataRange, test);

		test.log(LogStatus.INFO, "Click on Create Report Button");
		reportPage.clickOnCreateReportBtn();

		test.log(LogStatus.INFO, "Verify the Report title");
		reportPage.checkCreateReportTitle(reportName);


		test.log(LogStatus.INFO, "Set ReportType");
		reportPage.setReportType(ReportType);

		test.log(LogStatus.INFO, "Set the ReportName");
		reportPage.setReportName(reportName);


		test.log(LogStatus.INFO, "Set the Report for selected Sources Or Report For all sources ");
		reportPage.setSlectionOfSoucres(reportSoucres);


		test.log(LogStatus.INFO, "Set mEmailRecepient Details");
		reportPage.setEmailRecepients(recepientMail);

		test.log(LogStatus.INFO, "Set DataRange and Generate Type");
		reportPage.setDetailsToCreateOndemandReport(genratetype, "Custom");


		test.log(LogStatus.INFO, "Set Start and end datarange details ");
		boolean calenderDeatails = reportPage.setCustomReportDetails(startDatarnage,endDataRange);
		if(calenderDeatails) {
			reportPage.applyBtn();
		}else {
			reportPage.cancelBtn();
		}

		reportPage.createbtn(null);


	}


	public void radomDelevaryTime() {

	}


	/**
	 * @author Nagamalleswari.Sykam
	 * Create ReportName in the ReportsTable
	 * @param ReportName
	 * @param test
	 */
	public void checkreportnameinreportsTable(String ReportName,ExtentTest test) {
		ReportsPage reportPage= goToReportspage();
		reportPage.checkreportsinReportsTable(ReportName,test);

	}

	/**
	 * @author Nagamalleswari.Sykam
	 * Check the Schedule ReportsName in theManaged ReportsSchedules Table 
	 * @param reportName
	 * @param test
	 */
	public void checkreportnameInManageReportScheduleReportsTable(String reportName,ExtentTest test) {
		ManagedReportSchedulesPage managedReportsSchedulesPage= goToManagedReportSchedulesPage();
		managedReportsSchedulesPage.checkreportsinReportsMangedReportschedulesTable(reportName, test);


	}

	/**
	 * @author Nagamalleswari.Sykam
	 * delete the Report
	 * @param reportname
	 * @param toastMessage
	 * @param test
	 */
	public void deleteReport(String reportname,String toastMessage,ExtentTest test) {
		ReportsPage reportPage= goToReportspage();

		//reportPage.checkreportsinReportsTable(reportName,test);
		reportPage.deletereportByName(reportname,toastMessage);
	}

	/**
	 * Delete Report in  the Managed ReportSchedules Table 
	 * @param reportName
	 * @param toastMessage
	 * @param test
	 */
	public void deletereportByNameinManagedReportSchedulesTable(String reportName,String toastMessage,ExtentTest test) {
		ManagedReportSchedulesPage managedReportsSchedulesPage= goToManagedReportSchedulesPage();
		managedReportsSchedulesPage.deletereportByNameinManagedReportSchedulesTable(reportName, toastMessage);
	}


	/**
	 * @author Nagamalleswari.Sykam
	 * 
	 */


	public void deletScheduleReportInstance(String reportname,String toastMessage) {
		ManagedReportSchedulesPage managedReportsSchedulesPage= goToManagedReportSchedulesPage();
		managedReportsSchedulesPage.deleteReportScheduleInstance(reportname, toastMessage);
	}
	/**
	 * Check Custome
	 * @param reportName
	 * @param datRangeDetails
	 * @param test
	 *//*

	public void checkCustomreportsinReportsTable(String reportName, String[] datRangeDetails, ExtentTest test) {
		// TODO Auto-generated method stub
		ReportsPage reportPage= goToReportspage();

		//reportPage.checkreportsinReportsTable(reportName,test);
		reportPage.checkCustomreportsinReportsTable(reportName,datRangeDetails,test);
	}*/

}
