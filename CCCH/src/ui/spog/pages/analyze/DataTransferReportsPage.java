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

import javax.security.auth.Refreshable;

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

import ui.spog.pages.analyze.BackupJobReportsPage;



public class DataTransferReportsPage extends BasePage {

	private static final int WebElement = 0;
	private ErrorHandler errorHandle = BasePage.getErrorHandler();
	private int datarangemenu;


	public void searchDataTransferReportsByName(String search_Name) {
		waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_COMMON_SEARCHINPUT);

		TextField search = new TextField(SPOGMenuTreePath.SPOG_COMMON_SEARCHINPUT);
		search.sendKeys(search_Name);

		Button searchIcon = new Button(SPOGMenuTreePath.SPOG_COMMON_SEARCHICON);
		searchIcon.click();
	}


	public void setFiltersOnDataTranferReports(ArrayList<String> filters,ExtentTest test) {

		boolean isClicked=false;
		test.log(LogStatus.INFO, "click on the dropdown of the filter");
		for(int i =0;i<filters.size();i++ ) {
			String[] filter = filters.get(i).split(";");
			String actualfilterName = filter[0];
			String filterValue = filter[1];
			List<WebElement> filtersEle = ElementFactory.getElements(SPOGMenuTreePath.SPOG_COMMON_SELECTFILTERDIV);
			for(int j=0; j<filtersEle.size();j++) {
				WebElement filterEle = filtersEle.get(j);
				String expFilterName =filterEle.findElement(By.xpath(".//span")).getText();

				if(!(expFilterName.equals(""))&&((!expFilterName.equals(null)))){
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
			}

		}
	}


	public void clickOnSearchButton(ExtentTest test) {
		test.log(LogStatus.INFO, "click on search Button");
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_COMMON_SEARCHBTN, BrowserFactory.getMaxTimeWaitUIElement());
		ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_SEARCHBTN).click();
	}
	/**
	 * @author Nagamalleswari.Sykam
	 * 
	 * @param filters
	 * @param test
	 */

	public void applyFilterDetails(ArrayList<String> filters,ExtentTest test) {

		boolean isClicked= false;
		test.log(LogStatus.INFO, "click on the dropdown of the filter");
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_SELECTARROW, BrowserFactory.getMaxTimeWaitUIElement());
		ElementFactory.getElement(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_SELECTARROW).click();

		setFiltersOnDataTranferReports(filters, test);
		clickOnSearchButton(test);
	}


	/**
	 * @author Nagamalleswari.Sykam
	 * Checking the Backup job details with the search string
	 * @param searchString
	 * @param expectedInfo
	 */
	public void checkDataTranferDetailswithSerachString(String searchString,ArrayList<HashMap<String, Object>> expectedInfo ) {

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


	/***
	 * 
	 * Update Managed SaveSeacrh Filters
	 * @author Nagamalleswari.Sykam
	 * @param saveserachName
	 * @param updateSave_SearchName
	 * @param updateSave_SearchString
	 * @param updatefilters
	 * @param test
	 */

	public void updateManagedSaveSearchFilters(String saveserachName,String  updateSave_SearchName,String  updateSave_SearchString,ArrayList<String> updatefilters, ExtentTest test) {
		boolean	isClicked = false;
		if (updateSave_SearchName != null && !updateSave_SearchName.isEmpty()) {
			String result = updateSaveSearchNameAndSave(saveserachName, updateSave_SearchName);
			assertEquals(result, "pass");

			clickManageSavedSearch();
			saveserachName = updateSave_SearchName;
		}

		waitForSeconds(2);
		String bodyXpath = ElementFactory
				.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_MANAGESAVEDSEARCHES_BODYDIV).get(0).getValue();
		WebElement searchEle = ElementFactory.getElementByXpath(bodyXpath + "//button[text()='" + saveserachName + "']");
		if (!searchEle.getAttribute("class").equalsIgnoreCase("active")) {
			searchEle.click();
			waitForSeconds(1);
		}


		if (updateSave_SearchString != null && !updateSave_SearchString.isEmpty()) {

			WebElement inputEle = ElementFactory
					.getElement(SPOGMenuTreePath.SPOG_COMMON_MANAGESAVEDSEARCHES_SEARCHSTRING);
			inputEle.clear();
			inputEle.sendKeys(updateSave_SearchString);
		}

		setFiltersOnDataTranferReports(updatefilters, test);
		WebElement saveEle = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_SAVE);
		saveEle.click();
	}

	public void clickonSoucreName(String SoucreName) {


		waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_TABLE_HEADERS);
		HashMap<String, Integer> headerOrdr = getTableHeaderOrder(getTableHeaders());

		List<WebElement> rows = getRows();

		//HashMap<String, Integer> headerOrder = getTableHeaderOrder(getSoucregroupheaders());
		for (int i = 0; i < rows.size(); i++) {

			WebElement eachRow = rows.get(i);
			String xpath = "."+ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_TABLE_COLUMNS).get(0).getValue();
			List<WebElement> columns = eachRow.findElements(By.xpath(xpath));

			if (columns.get(headerOrdr.get(TableHeaders.source)).getText().contains(SoucreName)) {

				WebElement eachColumn = columns.get(0);
				eachColumn.findElement(By.xpath("//a[@id='undefined-source_name']")).click();;
				break;
			}
			if(i == rows.size()-1) {
				assertFalse(false, "Source  with name:"+SoucreName+" not found in table.");
			}

		}



	}

	
	/**
	 * Check Sourcename in the Protect Page
	 * @author Nagamalleswari.Sykam
	 * @param SoucreName
	 *//*
	public void checkSoucrenameWihCheckInProtectPage(String SoucreName) {
		waitForSeconds(5);
		WebElement ele =(org.openqa.selenium.WebElement) ElementFactory.getElementsByXpath("//div[@class='col-md-12']//span[text()='"+SoucreName+"']");
		assertTrue(ele.getText().contains(SoucreName));
	}
*/

}
