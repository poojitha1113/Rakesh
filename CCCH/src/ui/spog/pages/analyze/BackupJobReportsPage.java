package ui.spog.pages.analyze;



import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.gargoylesoftware.htmlunit.javascript.host.media.webkitMediaStream;
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

public class BackupJobReportsPage extends BasePage {

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
	/**
	 * @author Nagamalleswari.Sykam
	 * Verify the top 20 sources of backupJobReports
	 */

	public void verifyTop10soucres() {

		for(int i =0;i< widgets().size();i++) {
			if(i==0) {
				String xpath = "."+ElementFactory.getElementByXpath("//g[@class='chart-group']").getText();
				System.out.println(xpath);
			}
		}


	}
	/**
	 * @author Nagamalleswari.Sykam
	 * @param Search_String
	 * 
	 */

	public void searchByString(String search_string) {

		waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_COMMON_SEARCHINPUT);

		TextField search = new TextField(SPOGMenuTreePath.SPOG_COMMON_SEARCHINPUT);
		search.sendKeys(search_string);

		Button searchIcon = new Button(SPOGMenuTreePath.SPOG_COMMON_SEARCHICON);
		searchIcon.click();
	}



	public List<WebElement> filterOptions () {
		List<WebElement> filterOptions= ElementFactory.getElements(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_BACKUPJOBS_TOP10SOURCES_FILTEROPTINS);
		return filterOptions;
	}

	public List<WebElement> widgets(){
		List<WebElement> filterOptions= ElementFactory.getElementsByXpath("//div[@class='stacked-bar-chart-wrapper chart']");
		return filterOptions;

	}


	/**
	 * @author Nagamalleswari.Sykam
	 * Checking the Backup job details with the search string
	 * @param searchString
	 * @param expectedInfo
	 */
	public void checkBackupJobDetailswithSerachString(String searchString,ArrayList<HashMap<String, Object>> expectedInfo ) {

		if(!(expectedInfo.size()==0)) {

			waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_TABLE_HEADERS);
			HashMap<String, Integer> headerOrdr = getTableHeaderOrder(getTableHeaders());

			List<WebElement> rows = getRows();
			//HashMap<String, Integer> headerOrder = getTableHeaderOrder(getSoucregroupheaders());
			for (int i = 0; i < rows.size(); i++) {
				WebElement eachRow = rows.get(i);
				String xpath = "."+ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_TABLE_COLUMNS).get(0).getValue();
				List<WebElement> columns = eachRow.findElements(By.xpath(xpath));
				for(int j =0;j<expectedInfo.size();j++) {
					HashMap<String, Object> details =expectedInfo.get(j);
					String expectedSourceName = (String) details.get(TableHeaders.source);
					if (columns.get(headerOrdr.get(TableHeaders.source)).getText().contains(expectedSourceName)) {
						assertEquals(columns.get(headerOrdr.get(TableHeaders.source)).getText(), expectedSourceName);
						break;
					}
					if(i == rows.size()-1) {
						assertFalse(false, "Source  with name:"+expectedSourceName+" not found in table.");
					}

				}

			}
		}else {
			System.out.println("No rows found for the serch string");
		}
	}


	/**
	 * @author Nagamalleswari.Sykam
	 * @param Search
	 * @param expectedInfo
	 */
	public void checkBackupJobReportsTop10SoucreswithSerachString( String Search, ArrayList<HashMap<String, Object>> expectedInfo ) {
		if(!(expectedInfo.size()==0)) {
			for(int i =0;i<expectedInfo.size();i++) {

				HashMap<String, Object> top10SoucresDetails=expectedInfo.get(i);
				String sourceName = (String) top10SoucresDetails.get("name");

				if(sourceName.contains(Search)) {
					System.out.println("Expected sourcename "+sourceName+" is matched with the actual sourcname"+Search);

				}else {
					System.out.println("Expected sourcename "+sourceName+" doesnot match with the actual sourcname"+Search);

				}

			}

		}

	}


	/**
	 * @author Nagamalleswari.Sykam
	 * @param filters
	 * @param test
	 */

	public void applyFilterDetails(ArrayList<String> filters,ExtentTest test) {

		boolean isClicked= false;
		test.log(LogStatus.INFO, "click on the dropdown of the filter");
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_SELECTARROW, BrowserFactory.getMaxTimeWaitUIElement());
		ElementFactory.getElement(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_SELECTARROW).click();

		for(int i =0;i<filters.size();i++ ) {
			String[] filter = filters.get(i).split(";");
			String actualfilterName = filter[0];
			String filterValue = filter[1];
			List<WebElement> filtersEle = ElementFactory.getElements(SPOGMenuTreePath.SPOG_COMMON_SELECTFILTERDIV);
			for(int j=0; j<filtersEle.size();j++) {
				WebElement filterEle = filtersEle.get(j);
				String expFilterName =filterEle.findElement(By.xpath(".//span")).getText();
				if(actualfilterName.contains(expFilterName)) {
					List<WebElement> dropDownList;
					if(expFilterName.contains("Date Range")) {
						filterEle.findElement(By.xpath(".//button[@role='button']")).click();
						dropDownList = filterEle.findElements(By.xpath("//ul[@class='dropdown-menu']/li"));
					}else {
						filterEle.findElement(By.xpath(".//span[@class='Select-arrow']")).click();
						dropDownList = filterEle.findElements(By.xpath("//div[@role='option']"));
						System.out.println(dropDownList);
					}

					for(int k=0;k<dropDownList.size();k++) {
						WebElement eachFilter = dropDownList.get(k);

						if(dropDownList.get(k).getText().contains(filterValue)) {
							eachFilter.click();
							isClicked =true;
							break;
						}
					}
					if(isClicked==true){
						break;
					}
				}
			}


			waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_COMMON_SEARCHBTN, BrowserFactory.getMaxTimeWaitUIElement());
			ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_SEARCHBTN).click();
		}
	}
}




