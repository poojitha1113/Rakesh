package ui.spog.pages;

import org.openqa.selenium.WebElement;

import ui.base.common.BasePage;
import ui.base.common.BrowserOperation;
import ui.base.common.SPOGMenuTreePath;
import ui.base.elements.Link;
import ui.base.factory.BrowserFactory;
import ui.base.factory.ElementFactory;
import ui.spog.pages.analyze.AnalyzePage;
import ui.spog.pages.configure.ConfigurePage;
import ui.spog.pages.monitor.MonitorPage;
import ui.spog.pages.protect.ProtectPage;

public class HomePage extends BasePage {

	public HomePage(){
		super();
		BrowserFactory.getBrowser().switchTo().window(BrowserFactory.getParentUUID());
	}

	/**
	 * @author SHUO.ZHANG
	 * @return
	 */
	public ConfigurePage goToConfigurePage(){

		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_CONFIGURE, BrowserFactory.getMaxTimeWaitUIElement());
		Link configureLink = new Link(SPOGMenuTreePath.SPOG_CONFIGURE);

		configureLink.click();
		waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_CAPACITY_USAGE_TREND);
		ConfigurePage configurePage = new ConfigurePage();
		return configurePage;
	}

	/**
	 *  @author SHUO.ZHANG
	 */
	public void logout(){
		boolean flag;
		do{
			try{
				flag=false;
				WebElement logWrapper = ElementFactory.getElement(SPOGMenuTreePath.SPOG_USERLOGOUTWRAPPER);
				logWrapper.click();
				getErrorHandler().printInfoMessageInDebugFile("click logout");

				waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_USERLOGOUTWRAPPER_LOGOUT);
				Link logOutLink = new Link(SPOGMenuTreePath.SPOG_USERLOGOUTWRAPPER_LOGOUT);
				logOutLink.click();
				getErrorHandler().printInfoMessageInDebugFile("finished logout");
			}catch(Exception e){
				getErrorHandler().printInfoMessageInDebugFile("catch exception: "+ e.getMessage());
				new BrowserOperation(BrowserFactory.getBrowser()).refresh();
				getErrorHandler().printInfoMessageInDebugFile("refresh page");
				flag=true;
			}
		}while(flag==true);


	}

	/**
	 * @author shuo.zhang
	 * @return
	 */
	public ProtectPage goToProtectPage(){

		waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_PROTECT);
		Link protectLink = new Link(SPOGMenuTreePath.SPOG_PROTECT);
		protectLink.click();
		waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_COMMON_SAVEDSEARCHESLABEL);
		ProtectPage protectPage = new ProtectPage();
		return protectPage;
	}

	public ProtectPage viewAsCustomerEndUser(String accountName){

		waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_PROTECT);
		Link protectLink = new Link(SPOGMenuTreePath.SPOG_PROTECT);
		protectLink.click();
		waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_COMMON_SAVEDSEARCHESLABEL);
		ProtectPage protectPage = new ProtectPage();
		protectPage.viewAsCustomerEndUser(accountName);
		return protectPage;
	}

	public AnalyzePage goToAnalyzePage(){

		waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_ANALYZE);
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_ANALYZE,BrowserFactory.getMaxTimeWaitUIElement());
		Link analyzeLink = new Link(SPOGMenuTreePath.SPOG_ANALYZE);
	
		analyzeLink.click();
		waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_COMMON_SAVEDSEARCHESLABEL);
		AnalyzePage analyzePage = new AnalyzePage();
		return analyzePage;
	}

	public MonitorPage goToMonitorPage(){

		waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_MONITOR);
		Link monitorLink = new Link(SPOGMenuTreePath.SPOG_MONITOR);
		monitorLink.click();
		waitUntilElementAppear(SPOGMenuTreePath.SPOG_MONITOR_WIDGETS_RECENT_10_JOBS_IN_PROGRESS);
		MonitorPage monitorPage = new MonitorPage();
		return monitorPage;
	}
}
