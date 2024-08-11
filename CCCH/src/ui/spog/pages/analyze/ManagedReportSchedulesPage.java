package ui.spog.pages.analyze;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.gargoylesoftware.htmlunit.javascript.host.media.webkitMediaStream;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import ui.base.common.BasePage;
import ui.base.common.SPOGMenuTreePath;
import ui.base.common.TableHeaders;
import ui.base.factory.BrowserFactory;
import ui.base.factory.ElementFactory;

public class ManagedReportSchedulesPage  extends BasePage {



	public void MangedReportSchedulesSearchFiletrs() {
		waitUntilElementAppear(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_MANAGEDREPORTSCHEDULES);


	}


	public void  checkreportsinReportsMangedReportschedulesTable(String reoprtname, ExtentTest test) {

		waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_TABLE_HEADERS);

		HashMap<String, Integer> headerOrdr = getTableHeaderOrder(getTableHeaders());

		List<WebElement> rows = getRows();
		//HashMap<String, Integer> headerOrder = getTableHeaderOrder(getSoucregroupheaders());

		for (int i = 0; i < rows.size(); i++) {
			WebElement eachRow = rows.get(i);
			String xpath = "."+ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_TABLE_COLUMNS).get(0).getValue();
			List<WebElement> columns = eachRow.findElements(By.xpath(xpath));

			if (columns.get(headerOrdr.get(TableHeaders.report_name)).getText().contains(reoprtname)) {
				assertEquals(columns.get(headerOrdr.get(TableHeaders.report_name)).getText(), reoprtname);
				break;
			}

			if(i == rows.size()-1) {
				test.log(LogStatus.FAIL, "Reports with name:"+reoprtname+" not found in table.");
				assertFalse(false, "Reports with name:"+reoprtname+" not found in table.");
			}

		}

	}

	public void deletereportByNameinManagedReportSchedulesTable(String reportName,String toastmessgae) {

		if (checkReportExist(reportName)) {

			String result = clickContextualActionForSpecifiedElement(reportName,"Delete" );
			assertEquals(result, "pass");
			clickConfirmBtn();
			waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_TOASTMESSAGE);
			String ActualToastMessage = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_TOASTMESSAGE).getText();
			assertEquals(ActualToastMessage, toastmessgae);

		} else {
			assertTrue(true, "Report with name:"+reportName+" not found in the table.");
		}

	}


	
	/**
	 * @author Nagamalleswari.Sykam
	 * @param reportName
	 * @return
	 */

	
	public void deleteReportScheduleInstance(String reportName,String toastmessage) {
		
		if (checkReportExist(reportName)) {

			String result = clickContextualActionForSpecifiedElement(reportName,"Delete" );
			assertEquals(result, "pass");
			
			waitForSeconds(3);
			waitUntilElementAppear(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_MANAGEDREPORTSCHEDULES_DELETEREPORTSCHEDULETITLE);
			assertTrue(ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_MANAGEDREPORTSCHEDULES_DELETEREPORTSCHEDULETITLE));
			waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_MANAGEDREPORTSCHEDULES_DELETEREPORTINSTANCE,3);
			WebElement deletInstanceEle = ElementFactory.getElement(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_MANAGEDREPORTSCHEDULES_DELETEREPORTINSTANCE);
			deletInstanceEle.click();
			
			clickConfirmBtn();
			waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_TOASTMESSAGE);
			String actToastMessage = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_TOASTMESSAGE).getText();
			assertEquals(actToastMessage, toastmessage);

		} else {
			assertTrue(true, "Report with name:"+reportName+" not found in the table.");
		}
		
	
	}
	public boolean checkReportExist(String reportName) {

		waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_TABLE_HEADERS);
		HashMap<String, Integer> headerOrdr = getTableHeaderOrder(getTableHeaders());
		List<WebElement> rows = getRows();

		for (int i = 0; i < rows.size(); i++) {
			WebElement eachRow = rows.get(i);
			String xpath = "."+ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_TABLE_COLUMNS).get(0).getValue();
			List<WebElement> columns = eachRow.findElements(By.xpath(xpath));

			if (columns.get(headerOrdr.get(TableHeaders.report_name)).getText().contains(reportName)) {
				return true;
			}
		}
		return false;
	}
/**
 * @author Nagamalleswari.Sykam
 * Click on tHE Confirm Button
 */

	public void clickConfirmBtn() {
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_COMMON_CONFIRM, BrowserFactory.getMaxTimeWaitUIElement());
		WebElement ConfirmBtn =ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_CONFIRM);
		ConfirmBtn.click();

	}


 public void deleteReportInstance() {
	 
 }

}
