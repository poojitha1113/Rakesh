package ui.spog.server;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import genericutil.ErrorHandler;
import ui.base.common.BrowserOperation;
import ui.base.common.PageName;
import ui.base.common.SPOGMenuTreePath;
import ui.base.common.Url;
import ui.base.factory.BrowserFactory;
import ui.base.factory.ElementFactory;
import ui.spog.pages.HomePage;
import ui.spog.pages.LoginPage;
import ui.spog.pages.SetPasswordPage;
import ui.spog.pages.analyze.BackupJobReportsPage;
import ui.spog.pages.analyze.CapacityUsageReportsPage;
import ui.spog.pages.analyze.DataTransferReportsPage;
import ui.spog.pages.analyze.JobsPage;
import ui.spog.pages.analyze.LogsPage;
import ui.spog.pages.analyze.ManagedReportSchedulesPage;
import ui.spog.pages.analyze.RecoveryJobReportsPage;
import ui.spog.pages.analyze.ReportsPage;
import ui.spog.pages.configure.BrandingPage;
import ui.spog.pages.configure.EntitlementsPage;
import ui.spog.pages.configure.RolesPage;
import ui.spog.pages.configure.SourceGroupsPage;
import ui.spog.pages.configure.UserAccountsPage;
import ui.spog.pages.monitor.MonitorPage;
import ui.spog.pages.protect.CustomerAccountsPage;
import ui.spog.pages.protect.DestinationsPage;
import ui.spog.pages.protect.PolicyPage;
import ui.spog.pages.protect.ProtectPage;
import ui.spog.pages.protect.RecoveredVMsPage;
import ui.spog.pages.protect.SourcePage;

public class SPOGUIServer {

	//	SPOGUIInvoker spogUIInvoker = null;

	static String menuTreeFilePath = "./UI/ObjectRepository/SPOGMenuTree.xml";
	BrowserOperation SPOGBrowser;
	static ErrorHandler errorHandle;
	HomePage hp;

	public SPOGUIServer(String browserType, int maxTimeWaitSec)
	{
		BrowserFactory.setBrowseType(browserType);
		BrowserFactory.setDefaultMaxWaitTimeSec(maxTimeWaitSec);
		SPOGBrowser=new BrowserOperation(BrowserFactory.getBrowser());
		ElementFactory.setDriver(BrowserFactory.getBrowser());
		ElementFactory.setMenuTreeFilePath(menuTreeFilePath);
		hp= new HomePage();
		errorHandle = ErrorHandler.getErrorHandler();

	}

	/**
	 * @author shuo.zhang
	 * @param url
	 */
	public void openUrl(String url){

		errorHandle.printInfoMessageInDebugFile("************Invoking operation: openUrl***************");
		boolean result=false;
		SPOGBrowser.openUrl(url);
		if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_USERNAME)){
			result=true;
		}
		assertTrue(result, "open url successfully");
	}

	public void close(){
		SPOGBrowser.close();
	}


	public void destroy(){

		BrowserFactory.destroyBrowser();
	}

	public void refresh(){
		SPOGBrowser.refresh();
	}

	/**
	 * @author shuo.zhang
	 * @param username
	 * @param password
	 */
	public void login_Spog( String username, String password){

		LoginPage loginPage=new LoginPage();
		String result=loginPage.login_Spog(username, password);
		assertEquals(result,"pass");
		hp= new HomePage();
	}

	/**
	 * @author shuo.zhang
	 * @param username
	 * @param password
	 * @param expectedErrorMsg
	 */
	public void login_Spog_with_error( String username, String password, String expectedErrorMsg){

		LoginPage loginPage=new LoginPage();
		String result=loginPage.login_Spog_with_error(username, password, expectedErrorMsg);
		assertEquals(result,"pass");
	}

	/**
	 * @author shuo.zhang
	 */
	public void checkLoginBtnIsDisabled(){

		LoginPage loginPage=new LoginPage();
		boolean result =loginPage.isLoginBtnEnabled();
		assertFalse(result);
	}

	/**
	 * @author shuo.zhang
	 * @param response
	 */

	public UserAccountsPage goToUserAccountPage(){

		return hp.goToConfigurePage().goToUserAccountsPage();
	}


	/**
	 *  @author SHUO.ZHANG
	 */
	public void logout(){

		hp.logout();
	}

	public void createAccount(String password){

		SetPasswordPage setPwdPage= new SetPasswordPage();
		setPwdPage.createAccount(password);
	}

	public PolicyPage goToPolicyPage(){

		return hp.goToProtectPage().goToPolicyPage();
	}
	
	public ProtectPage viewAsCustomerEndUser(String accountName){

		return hp.viewAsCustomerEndUser(accountName);
	}

	public CustomerAccountsPage goToCustomerAccountsPage() {

		return hp.goToProtectPage().goToCustomerAccountsPage();
	}

	public SourcePage goToSourcesPage() {
//		if (!BrowserFactory.getCurrentPageUrl().equalsIgnoreCase(Url.sources) || BrowserFactory.getCurrentPageUrl().toLowerCase().contains(Url.sources))
			return hp.goToProtectPage().goToSourcePage();

//		return new SourcePage();
	}

	public DestinationsPage goToDestinationsPage() {
		HomePage pg = new HomePage();
		ProtectPage pp = pg.goToProtectPage();
		return pp.goToDestinationPage();
		/*return hp.goToProtectPage().goToDestinationPage();*/
	}
	
	public MonitorPage goToMonitorPage() {
		return hp.goToMonitorPage();
	}
	
	public RecoveredVMsPage goToRecoveredVMsPage() {
		return hp.goToProtectPage().goToRecoveredVMsPage();
	}

	public JobsPage goToJobsPage() {
		return hp.goToAnalyzePage().goToJobsPage();
	}

	public LogsPage goToLogsPage() {
		return hp.goToAnalyzePage().goToLogsPage();
	}

	public ReportsPage goToReportsPage() {
		return hp.goToAnalyzePage().goToReportsPage();
	}

	public BackupJobReportsPage goToBackupJobReportsPage() {
		return hp.goToAnalyzePage().goToBackupJobReportsPage();
	}

	public RecoveryJobReportsPage goToRecoveryJobReportsPage() {
		return hp.goToAnalyzePage().goToRecoveryJobReportsPage();
	}

	public void goToPage(String pageName) {

		if (pageName.equalsIgnoreCase(PageName.SOURCE)) {
			
			if (!BrowserFactory.getCurrentPageUrl().contains(Url.sources))
				goToSourcesPage();
			else new BrowserOperation(BrowserFactory.getBrowser()).refresh();
			
		} else if(pageName.equalsIgnoreCase(PageName.DESTINATION)){
			
			if (!BrowserFactory.getCurrentPageUrl().contains(Url.destinations))
				goToDestinationsPage();
			else new BrowserOperation(BrowserFactory.getBrowser()).refresh();
			
		} else if(pageName.equalsIgnoreCase(PageName.RECOVEREDVMS)){
			
			if (!BrowserFactory.getCurrentPageUrl().contains(Url.RecoveredVMs))
				goToRecoveredVMsPage();
			else new BrowserOperation(BrowserFactory.getBrowser()).refresh();
			
		}else if(pageName.equalsIgnoreCase(PageName.POLICIES)){
			
			if (!BrowserFactory.getCurrentPageUrl().contains(Url.policies))
				goToPolicyPage();
			else new BrowserOperation(BrowserFactory.getBrowser()).refresh();
			
		} else if (pageName.equalsIgnoreCase(PageName.JOBS)) {
			
			if (!BrowserFactory.getCurrentPageUrl().contains(Url.jobs))
				goToJobsPage();
			else new BrowserOperation(BrowserFactory.getBrowser()).refresh();
			
		} else if (pageName.equalsIgnoreCase(PageName.LOGS)) {
			
			if (!BrowserFactory.getCurrentPageUrl().contains(Url.logs))
				goToLogsPage();
			else new BrowserOperation(BrowserFactory.getBrowser()).refresh();
			
		}else if (pageName.equalsIgnoreCase(PageName.BACKUPJOB_REPORTS)) {
			
			if (!BrowserFactory.getCurrentPageUrl().contains(Url.backupJobReports))
				goToBackupJobReportsPage();
			else new BrowserOperation(BrowserFactory.getBrowser()).refresh();
			
		}else if (pageName.equalsIgnoreCase(PageName.RECOVERYJOB_REPORTS)) {
			
			if (!BrowserFactory.getCurrentPageUrl().contains(Url.recoveryJobReports))
				goToRecoveryJobReportsPage();
			else new BrowserOperation(BrowserFactory.getBrowser()).refresh();
		}else if (pageName.equalsIgnoreCase(PageName.USERACCOUNTS)) {
			
			if (!BrowserFactory.getCurrentPageUrl().contains(Url.userAccounts))
				goToUserAccountPage();
			else new BrowserOperation(BrowserFactory.getBrowser()).refresh();
		}

	}

	
	public DataTransferReportsPage goToDataTransferReportsPage() {
		return hp.goToAnalyzePage().goToDataTransferReportsPage();
	}

	public CapacityUsageReportsPage goToCapacityUsageReportPage() {
		return hp.goToAnalyzePage().goToCapacityUsageReportPage();
	}
	
	public BrandingPage goToBrandingPage() {
		return hp.goToConfigurePage().goToBrandingPage();
	}
	
	public ReportsPage goToReportspage() {
		return hp.goToAnalyzePage().goToReportsPage();
	}

	public ManagedReportSchedulesPage goToManagedReportSchedulesPage() {
		return hp.goToAnalyzePage().goToManagedReportSchedulesPage();
	}

	public   RolesPage goToRolesPage() {
		return (hp.goToConfigurePage()).goToRolesPage();
	}
	
	public   SourceGroupsPage goToSourceGroupPage() {
		return (hp.goToConfigurePage()).goToSourceGroupsPage();
	}
	
	public EntitlementsPage goToEntitlementsPage() {
		return hp.goToConfigurePage().goTOEntitlementsPage();
	}
	
}
