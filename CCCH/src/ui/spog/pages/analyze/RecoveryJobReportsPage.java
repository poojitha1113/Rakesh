package ui.spog.pages.analyze;



import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import java.lang.reflect.WildcardType;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import genericutil.ErrorHandler;
import ui.base.common.BasePage;
import ui.base.common.SPOGMenuTreePath;
import ui.base.common.TableHeaders;
import ui.base.elements.TextField;
import ui.base.factory.BrowserFactory;
import ui.base.factory.ElementFactory;
import ui.base.elements.Button;

import ui.spog.pages.analyze.BackupJobReportsPage;



public class RecoveryJobReportsPage extends BasePage {

	
	
	private static final int WebElement = 0;
	private ErrorHandler errorHandle = BasePage.getErrorHandler();
	private int datarangemenu;

	public void  applyingFilterOnTop10Sources(String filterName) {
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_BACKUPJOBS_TOP10SOURCES_FILTER, BrowserFactory.getMaxTimeWaitUIElement());
		WebElement top10sourcesFilter = ElementFactory.getElement(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_BACKUPJOBS_TOP10SOURCES_FILTER);
		top10sourcesFilter.click();


		for(int i =0 ; i<filterOptions().size();i++) {
			WebElement ele= filterOptions().get(i);
			String name = filterOptions().get(i).toString();
			if(name.contains(filterName)) {
				ele.click();
			}
		}
	}

	public void veriryTop10soucres() {

		for(int i =0;i< widgets().size();i++) {
			if(i==0) {
				String xpath = "."+ElementFactory.getElementByXpath("//g[@class='chart-group']").getText();
				System.out.println(xpath);
			}
		}


	}
	public List<WebElement> filterOptions () {
		List<WebElement> filterOptions= ElementFactory.getElements(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_BACKUPJOBS_TOP10SOURCES_FILTEROPTINS);
		return filterOptions;
	}

	public List<WebElement> widgets(){
		List<WebElement> filterOptions= ElementFactory.getElementsByXpath("//div[@class='stacked-bar-chart-wrapper chart']");
		return filterOptions;

	}
}
