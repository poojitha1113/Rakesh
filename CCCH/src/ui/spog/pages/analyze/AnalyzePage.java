package ui.spog.pages.analyze;

import static org.testng.Assert.assertTrue;

import org.openqa.selenium.WebElement;

import ui.base.common.BasePage;
import ui.base.common.SPOGMenuTreePath;
import ui.base.common.Url;
import ui.base.elements.Link;
import ui.base.factory.BrowserFactory;
import ui.base.factory.ElementFactory;
import ui.spog.pages.protect.CustomerAccountsPage;

public class AnalyzePage extends BasePage{


	public JobsPage goToJobsPage() {
		if (!BrowserFactory.getCurrentPageUrl().equalsIgnoreCase(Url.jobs)) {
			waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_ANALYZE_JOBS);
			Link protectLink = new Link(SPOGMenuTreePath.SPOG_ANALYZE_JOBS);
			protectLink.click();

			waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_SELECTEDROWSLABEL);
			assertTrue(ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_COMMON_SELECTEDROWSLABEL));	
		}

		JobsPage  jobsPage = new JobsPage();	   
		waitForMilSeconds(3000);
		return jobsPage;
	}

	public LogsPage goToLogsPage() {
		if (!BrowserFactory.getCurrentPageUrl().equalsIgnoreCase(Url.logs)) {
			waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_ANALYZE_LOGS);

			Link logsLink = new Link(SPOGMenuTreePath.SPOG_ANALYZE_LOGS);
			logsLink.click();

			waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_SEARCHINPUT);
			assertTrue(ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_COMMON_SEARCHINPUT));	
		}

		LogsPage  logsPage = new LogsPage();	   
		return logsPage;
	}

	public ReportsPage goToReportsPage() {
		if (!BrowserFactory.getCurrentPageUrl().equalsIgnoreCase(Url.reports)) {
			waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS);
			Link protectLink = new Link(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS);
			protectLink.click();

			waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_SEARCHINPUT);
			assertTrue(ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_COMMON_SEARCHINPUT));	
		}

		ReportsPage  reportsPage = new ReportsPage();	   
		return reportsPage;
	}

	public ManagedReportSchedulesPage goToManagedReportSchedulesPage() {


		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_MANAGEDREPORTSCHEDULES, BrowserFactory.getMaxTimeWaitUIElement());

		Link MangedReportsLink = new Link(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_MANAGEDREPORTSCHEDULES);
		MangedReportsLink.click();
		ManagedReportSchedulesPage  managedReportSchedulesPage = new ManagedReportSchedulesPage();	   
		return managedReportSchedulesPage;
	}
	public BackupJobReportsPage goToBackupJobReportsPage() {
		if (!BrowserFactory.getCurrentPageUrl().equalsIgnoreCase(Url.backupJobReports)) {
			waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_BACKUPJOBREPORTS);
			WebElement  backupJobReportsLink = ElementFactory.getElement(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_BACKUPJOBREPORTS);
			backupJobReportsLink.click();

		}

		BackupJobReportsPage  backupJobReports = new BackupJobReportsPage();	   
		return backupJobReports;
	}


	public RecoveryJobReportsPage goToRecoveryJobReportsPage() {
		if (!BrowserFactory.getCurrentPageUrl().equalsIgnoreCase(Url.recoveryJobReports)) {
			waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_RECOVERYJOBREPORTS);
			Link rescoveryJobsPageLink = new Link(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_RECOVERYJOBREPORTS);
			rescoveryJobsPageLink.click();

		}

		RecoveryJobReportsPage  recoveryJobReportsPage = new RecoveryJobReportsPage();	   
		return recoveryJobReportsPage;
	}

	public DataTransferReportsPage goToDataTransferReportsPage() {
		if (!BrowserFactory.getCurrentPageUrl().equalsIgnoreCase(Url.dataTransferReports)) {
			waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_DATATRANSFERREPORT, BrowserFactory.getMaxTimeWaitUIElement());
			Link dataTranferPageLink = new Link(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_DATATRANSFERREPORT);
			dataTranferPageLink.click();

		}

		DataTransferReportsPage  dataTransferReportsPage = new DataTransferReportsPage();	   
		return dataTransferReportsPage;
	}

	public CapacityUsageReportsPage goToCapacityUsageReportPage() {
		if (!BrowserFactory.getCurrentPageUrl().equalsIgnoreCase(Url.capacityUsageReports)) {
			waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_CAPACITYUSAGEREPORTS);
			Link capacityUsageReporyLink = new Link(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_CAPACITYUSAGEREPORTS);
			capacityUsageReporyLink.click();

		}

		CapacityUsageReportsPage  capacityUsageReportPage = new CapacityUsageReportsPage();	   
		return capacityUsageReportPage;
	}
}

