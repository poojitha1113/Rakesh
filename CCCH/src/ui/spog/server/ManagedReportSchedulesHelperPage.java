package ui.spog.server;

import ui.spog.pages.analyze.ManagedReportSchedulesPage;

public class ManagedReportSchedulesHelperPage extends SPOGUIServer {

	public ManagedReportSchedulesHelperPage(String browserType, int maxTimeWaitSec) {
		super(browserType, maxTimeWaitSec);
		// TODO Auto-generated constructor stub
	}

	
	public void mangedreportSchedulesFilters() {
		ManagedReportSchedulesPage mangaedreportSchuledpage = goToManagedReportSchedulesPage();
		
	
	}
	
	
	public void checkReportsinManagedReportShculesTables() {
		ManagedReportSchedulesPage mangaedreportSchuledpage = goToManagedReportSchedulesPage();
		
	}
}
