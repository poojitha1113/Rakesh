package ui.base.common;

import genericutil.ErrorHandler;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.xalan.templates.ElemFallback;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import ui.base.elements.BaseElement;
import ui.base.elements.Button;
import ui.base.elements.Link;
import ui.base.elements.TextField;
import ui.base.factory.BrowserFactory;
import ui.base.factory.ElementFactory;

public class BasePage {
	private WebDriverWait wait;
	static ErrorHandler errorHandle;

	public BasePage() {
		errorHandle = ErrorHandler.getErrorHandler();
		this.wait = new WebDriverWait(BrowserFactory.getBrowser(), BrowserFactory.getMaxTimeWaitUIElement());
	}

	public void waitUntilElementDisplayAndEnable(final WebElement element) {

		wait.until(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver d) {
				return element.isEnabled() && element.isDisplayed();
			}
		});
	}

	public void waitForSeconds(final long seconds) {
		try {
			Thread.currentThread().sleep(seconds * 1000);
		} catch (InterruptedException e) {
			errorHandle.printInfoMessageInDebugFile("There is wait for seconds exception.");
		}
	}

	public void waitForMilSeconds(final long milSeconds) {
		try {
			Thread.currentThread().sleep(milSeconds);
		} catch (InterruptedException e) {
			errorHandle.printInfoMessageInDebugFile("There is wait for seconds exception.");
		}
	}

	public void waitUntilElementDisappear(final WebElement element) {
		wait.until(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver d) {
				return !element.isDisplayed();
			}
		});

	}

	public void waitUntilElementDisappear(final String xpath) {
		if (ElementFactory.checkElementExists(xpath)) {
			try {
				wait.until(new ExpectedCondition<Boolean>() {
					@Override
					public Boolean apply(WebDriver d) {
						BaseElement element = new BaseElement(xpath);
						return !element.isDisplayed();
					}
				});
			} catch (org.openqa.selenium.TimeoutException e) {
				if (ElementFactory.checkElementExists(xpath)) {
					try {
						wait.until(new ExpectedCondition<Boolean>() {
							@Override
							public Boolean apply(WebDriver d) {
								BaseElement element = new BaseElement(xpath);
								return !element.isDisplayed();
							}
						});
					} catch (org.openqa.selenium.TimeoutException e1) {
						errorHandle
								.printInfoMessageInDebugFile("There is TimeoutException for element xpath is:" + xpath);
					}
				}
			} catch (org.openqa.selenium.StaleElementReferenceException e2) {
				errorHandle.printInfoMessageInDebugFile(
						"There is StaleElementReferenceException for element xpath is:" + xpath);
			}
		}
	}

	public void waitUntilElementIsUsable(final String xpath) {
		try {
			wait.until((new ExpectedCondition<Boolean>() {
				@Override
				public Boolean apply(WebDriver d) {
					if (ElementFactory.checkElementExists(xpath)) {
						BaseElement resourceLink = new BaseElement(xpath);
						return !resourceLink.isLocked();
					} else {
						return false;
					}
				}
			}));
		} catch (org.openqa.selenium.TimeoutException e) {
			errorHandle.printInfoMessageInDebugFile("There is TimeoutException for element xpath is:" + xpath);
		} catch (org.openqa.selenium.StaleElementReferenceException e2) {
			errorHandle.printInfoMessageInDebugFile(
					"There is StaleElementReferenceException for element xpath is:" + xpath);
		}
	}

	public void waitUntilElementDisappear(final String xpath, int seconds) {
		WebDriverWait myWait = new WebDriverWait(BrowserFactory.getBrowser(), seconds);
		errorHandle.printInfoMessageInDebugFile("start of the first time of waitUntilElementDisappear");
		if (ElementFactory.checkElementExists(xpath, seconds)) {
			try {
				myWait.until(new ExpectedCondition<Boolean>() {
					@Override
					public Boolean apply(WebDriver d) {
						BaseElement element = new BaseElement(xpath);
						return !element.isDisplayed();
					}
				});
			} catch (org.openqa.selenium.TimeoutException e) {
				errorHandle.printInfoMessageInDebugFile("start of the second time of waitUntilElementDisappear");
				if (ElementFactory.checkElementExists(xpath, 1)) {
					try {
						myWait.until(new ExpectedCondition<Boolean>() {
							@Override
							public Boolean apply(WebDriver d) {
								BaseElement element = new BaseElement(xpath);
								return !element.isDisplayed();
							}
						});
					} catch (org.openqa.selenium.TimeoutException e1) {
						errorHandle
								.printInfoMessageInDebugFile("There is TimeoutException for element xpath is:" + xpath);
					}
				}
			} catch (org.openqa.selenium.StaleElementReferenceException e2) {
				errorHandle.printInfoMessageInDebugFile(
						"There is StaleElementReferenceException for element xpath is:" + xpath);
			}
		}
	}

	public void waitUntilElementIsUsable(final String xpath, int seconds) {
		WebDriverWait myWait = new WebDriverWait(BrowserFactory.getBrowser(), seconds);
		try {
			myWait.until((new ExpectedCondition<Boolean>() {
				@Override
				public Boolean apply(WebDriver d) {
					if (ElementFactory.checkElementExists(xpath)) {
						BaseElement resourceLink = new BaseElement(xpath);
						errorHandle.printInfoMessageInDebugFile("rps tabke status:" + resourceLink.isLocked());
						return !resourceLink.isLocked();
					} else {
						return false;
					}
				}
			}));
		} catch (org.openqa.selenium.TimeoutException e) {
			errorHandle.printInfoMessageInDebugFile("There is TimeoutException for element xpath is:" + xpath);
		} catch (org.openqa.selenium.StaleElementReferenceException e2) {
			errorHandle.printInfoMessageInDebugFile(
					"There is StaleElementReferenceException for element xpath is:" + xpath);
		}
	}

	public void waitUntilElementIsLocked(final String xpath, final int seconds) {
		WebDriverWait myWait = new WebDriverWait(BrowserFactory.getBrowser(), seconds);
		try {
			myWait.until((new ExpectedCondition<Boolean>() {
				@Override
				public Boolean apply(WebDriver d) {
					if (ElementFactory.checkElementExists(xpath, seconds)) {
						BaseElement resourceLink = new BaseElement(xpath);
						return resourceLink.isLocked();
					} else {
						return false;
					}
				}
			}));
		} catch (org.openqa.selenium.TimeoutException e) {
			errorHandle.printInfoMessageInDebugFile("There is TimeoutException for element xpath is:" + xpath);
		} catch (org.openqa.selenium.StaleElementReferenceException e2) {
			errorHandle.printInfoMessageInDebugFile(
					"There is StaleElementReferenceException for element xpath is:" + xpath);
		}
	}

	public static ErrorHandler getErrorHandler() {
		return errorHandle;
	}

	public void mouseHover(WebElement targetElement) {
		String javaScript = "var eventObj = document.createEvent( 'MouseEvents' );"
				+ "eventObj.initMouseEvent( \"mouseover\", true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null );"
				+ "arguments[0].dispatchEvent( eventObj );";
		JavascriptExecutor jsExecutor = (JavascriptExecutor) (BrowserFactory.getBrowser());
		jsExecutor.executeScript(javaScript, targetElement);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
	}

	public WebElement getElement( String xpath,int seconds)
	{	
		return ElementFactory.getElement(xpath, seconds);
	}
	
	public void waitUntilElementAppear(final String xpath) {
		if (!ElementFactory.checkElementExists(xpath)) {
			try {
				wait.until(new ExpectedCondition<Boolean>() {
					@Override
					public Boolean apply(WebDriver d) {
						BaseElement element = new BaseElement(xpath);
						return element.isDisplayed();
					}
				});
			} catch (org.openqa.selenium.TimeoutException e) {
				if (!ElementFactory.checkElementExists(xpath)) {
					try {
						wait.until(new ExpectedCondition<Boolean>() {
							@Override
							public Boolean apply(WebDriver d) {
								BaseElement element = new BaseElement(xpath);
								return element.isDisplayed();
							}
						});
					} catch (org.openqa.selenium.TimeoutException e1) {
						errorHandle
								.printInfoMessageInDebugFile("There is TimeoutException for element xpath is:" + xpath);
					}
				}
			} catch (org.openqa.selenium.StaleElementReferenceException e2) {
				errorHandle.printInfoMessageInDebugFile(
						"There is StaleElementReferenceException for element xpath is:" + xpath);
			}
		}
	}

	public void waitUntilElementIsClickable(final String xpath, int seconds) {
		WebDriverWait myWait = new WebDriverWait(BrowserFactory.getBrowser(), seconds);
		try {
			myWait.until((new ExpectedCondition<Boolean>() {
				@Override
				public Boolean apply(WebDriver d) {
					if (ElementFactory.checkElementExists(xpath)) {
						wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
						return true;
					} else {
						return false;
					}
				}
			}));

		} catch (org.openqa.selenium.TimeoutException e) {
			errorHandle.printInfoMessageInDebugFile("There is TimeoutException for element xpath is:" + xpath);
		} catch (org.openqa.selenium.StaleElementReferenceException e2) {
			errorHandle.printInfoMessageInDebugFile(
					"There is StaleElementReferenceException for element xpath is:" + xpath);
		}
	}

	/**
	 * Generic method to click on the contextual action for a specified
	 * element(primary item of row) of the table.
	 * 
	 * @author Rakesh.Chalamala
	 * 
	 * @param elementName
	 * @param action
	 * @return
	 */
	public String clickContextualActionForSpecifiedElement(String elementName, String action) {

		String result = "fail";

		waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_TABLE_ROWS);
		List<WebElement> rows = getRows();

		for (int i = 1; i <= rows.size(); i++) {

			try {
				String xpath = ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_TABLE_ROWS).get(0)
						.getValue();
				xpath = xpath + "[" + i + "]";

				// check for row existence
				ElementFactory.getElementByXpath(xpath);
				// check for name existence in row
				ElementFactory.getElementByXpath(xpath + "//*[text()='" + elementName + "']");

				String contextualsXpath = ElementFactory
						.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_CONTEXTUALACTIONS).get(0).getValue();

				System.out.println(xpath + contextualsXpath);

				ElementFactory.getElementByXpath(xpath + contextualsXpath).click();
				;

				ElementFactory.getElementByXpath(xpath + "//span[text()='" + action + "']").click();
				;

				this.waitForSeconds(2);
				result = "pass";
				break;

			} catch (NoSuchElementException e) {
				if (i == rows.size()) {
					assertTrue(false, "Element with name:" + elementName + " not found in table.");
				}

				continue;
			} catch (Exception e) {
				assertFalse(true, e.getMessage());
				continue;
			}
		}

		return result;
	}

	/**
	 * Checks for the contextual actions availability for a specified element
	 * 
	 * @author Rakesh.Chalamala
	 * @param elementName
	 * @param actions
	 * @return
	 */
	public String checkContextualActionsForSpecifiedElement(String elementName, ArrayList<String> actions) {

		String result = "fail";

		waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_TABLE_ROWS);
		List<WebElement> rows = getRows();

		for (int i = 1; i <= rows.size(); i++) {

			try {
				String xpath = ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_TABLE_ROWS).get(0)
						.getValue();
				xpath = xpath + "[" + i + "]";

				// check for row existence
				ElementFactory.getElementByXpath(xpath);
				// check for name existence in row
				ElementFactory.getElementByXpath(xpath + "//*[text()='" + elementName + "']");

				String contextualsXpath = ElementFactory
						.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_CONTEXTUALACTIONS).get(0).getValue();

				ElementFactory.getElementByXpath(xpath + contextualsXpath).click();
				;

				for (int j = 0; j < actions.size(); j++) {

					try {
						ElementFactory.getElementByXpath(xpath + "//span[text()='" + actions.get(j) + "']");
					} catch (NoSuchElementException e) {
						assertFalse(true, "Contextual action:" + actions.get(j) + " not found");
					} catch (Exception e) {
						e.printStackTrace();
						assertFalse(true, e.getMessage());
					}
				}

				this.waitForSeconds(1);
				ElementFactory.getElementByXpath(xpath + contextualsXpath).click();

				result = "pass";
				break;

			} catch (NoSuchElementException e) {
				if (i == rows.size()) {
					assertTrue(false, "Element with name:" + elementName + " not found in table.");
				}

				continue;
			} catch (Exception e) {
				assertFalse(true, e.getMessage());
			}
		}

		return result;
	}

	/**
	 * Gets the header elements order
	 * 
	 * @author Rakesh.Chalamala
	 * @param headerTable
	 * @return
	 */
	public HashMap<String, Integer> getTableHeaderOrder(List<WebElement> headerTable) {

		HashMap<String, Integer> headerOrderMap = new HashMap<String, Integer>();

		for (int i = 0; i < headerTable.size(); i++) {
			WebElement eachHeader = headerTable.get(i);
			String text = eachHeader.getText();
			if ((text != null) && !text.equalsIgnoreCase("")) {
				headerOrderMap.put(text, i);
			}
		}
		return headerOrderMap;
	}

	public List<WebElement> getTableHeaders() {

		return ElementFactory.getElements(SPOGMenuTreePath.SPOG_COMMON_TABLE_HEADERS);
	}

	/**
	 * Gets all the rows of a table
	 * 
	 * @author Rakesh.Chalamala
	 * @return
	 */
	public List<WebElement> getRows() {

		return ElementFactory.getElements(SPOGMenuTreePath.SPOG_COMMON_TABLE_ROWS);
	}
	
	/**
	 * Gets all the latest row of a table
	 * 
	 * @author Jing.Shan
	 * @return
	 */
	public WebElement getRow() {

		return ElementFactory.getElements(SPOGMenuTreePath.SPOG_COMMON_TABLE_ROWS).get(0);
	}
	
	public String getLastJobTimeFromLatestJobColume() {
    	String ret=null;
    	WebElement tableBody = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_TABLE_ROWS);
    	try {
    		WebElement latestJobColumeContent = tableBody.findElement(By.xpath(".//div[@class='multi-line-text']"));
    		List<WebElement> latestJobItems = latestJobColumeContent.findElements(By.xpath(".//div//span"));
        	if (latestJobItems.size() == 0) {
        		return ret;
    		}else {
    			ret=latestJobItems.get(1).getText().toString();
    		}    		
    	} catch (NoSuchElementException e) {
			errorHandle.printErrorMessageInDebugFile("The source has no latest job");
		}
    	return ret;
    }
	
	public String getLastJobTimeFromLatestJobColume(int index) {
    	String ret=null;
    	List<WebElement>  tableBodyList = ElementFactory.getElements(SPOGMenuTreePath.SPOG_COMMON_TABLE_ROWS);
    	WebElement tableBody=tableBodyList.get(index);
    	try {
    		WebElement latestJobColumeContent = tableBody.findElement(By.xpath(".//div[@class='multi-line-text']"));
    		List<WebElement> latestJobItems = latestJobColumeContent.findElements(By.xpath(".//div//span"));
        	if (latestJobItems.size() == 0) {
        		return ret;
    		}else {
    			ret=latestJobItems.get(1).getText().toString();
    		}    		
    	} catch (NoSuchElementException e) {
			errorHandle.printErrorMessageInDebugFile("The source has no latest job");
		}
    	return ret;
    }
	
	public void checkJobInformationFromLatestJobColume(String lastTime,String jobType,String jobStatus) {
    	String ret=null;
    	WebElement  tableBody = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_TABLE_ROWS);
    	try {
    		WebElement latestJobColumeContent = tableBody.findElement(By.xpath(".//div[@class='multi-line-text']"));
    		List<WebElement> latestJobItems = latestJobColumeContent.findElements(By.xpath(".//div//span"));
        	if (latestJobItems.size() == 0) {
    		}else {
    			ret=latestJobItems.get(1).getText().toString();
    			System.out.println("actual job time is"+ret);
    			System.out.println("previous job time is"+lastTime);
    			if(!ret.equalsIgnoreCase(lastTime)) {
    				assertTrue(true, "check job time is expected.");
    			}else {
    				assertTrue(false, "check job time is expected.");
    			}
    			if(latestJobItems.get(0).getText().toString().equalsIgnoreCase(jobStatus)) {
    				assertTrue(true, "check job status is expected.");
    			}else {
    				assertTrue(false, "check job status is expected.");
    			}
    		}  
        	latestJobItems = latestJobColumeContent.findElements(By.xpath(".//a//span"));
        	if (latestJobItems.size() == 0) {
    			System.out.println("row in getLastJobTypeFromLatestJobColume is 0");
    			assertTrue(false, "check job type is expected.");
    		}else {
    			ret=latestJobItems.get(0).getText().toString();
    			System.out.println("job type in destination page is:"+ret);
    			if(ret.toLowerCase().contains(jobType.toLowerCase())) {
    				assertTrue(true, "check job type is expected.");
    			}else {
    				assertTrue(false, "check job type is expected.");
    			}    			
    		}    
    	} catch (NoSuchElementException e) {
			errorHandle.printErrorMessageInDebugFile("The source has no latest job");
		}
    }
	
	public void checkJobInformationFromLatestJobColume(int index,String lastTime,String jobType,String jobStatus) {
    	String ret=null;
    	List<WebElement>  tableBodyList = ElementFactory.getElements(SPOGMenuTreePath.SPOG_COMMON_TABLE_ROWS);
    	WebElement tableBody=tableBodyList.get(index);
    	try {
    		WebElement latestJobColumeContent = tableBody.findElement(By.xpath(".//div[@class='multi-line-text']"));
    		List<WebElement> latestJobItems = latestJobColumeContent.findElements(By.xpath(".//div//span"));
        	if (latestJobItems.size() == 0) {
    		}else {
    			ret=latestJobItems.get(1).getText().toString();
    			if(!ret.equalsIgnoreCase(lastTime)) {
    				assertTrue(true, "check job time is expected.");
    			}else {
    				assertTrue(false, "check job time is expected.");
    			}
    			if(latestJobItems.get(0).getText().toString().equalsIgnoreCase(jobStatus)) {
    				assertTrue(true, "check job status is expected.");
    			}else {
    				assertTrue(false, "check job status is expected.");
    			}
    		}  
        	latestJobItems = latestJobColumeContent.findElements(By.xpath(".//a//span"));
        	if (latestJobItems.size() == 0) {
    			System.out.println("row in getLastJobTypeFromLatestJobColume is 0");
    			assertTrue(false, "check job type is expected.");
    		}else {
    			ret=latestJobItems.get(0).getText().toString();
    			System.out.println("job type in destination page is:"+ret);
    			if(ret.toLowerCase().contains(jobType.toLowerCase())) {
    				assertTrue(true, "check job type is expected.");
    			}else {
    				assertTrue(false, "check job type is expected.");
    			}    			
    		}    
    	} catch (NoSuchElementException e) {
			errorHandle.printErrorMessageInDebugFile("The source has no latest job");
		}
    }
	
	public WebElement getRowWebElementByColumeValue(String columnName,String expectedValue) {
		List<WebElement> headers = getTableHeaders();
		HashMap<String, Integer> headerOrder = getTableHeaderOrder(headers);
		String xpath = "."+ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_TABLE_COLUMNS).get(0).getValue();
		List<WebElement> rows = getRows();		
		for (int i = 0; i < rows.size(); i++) {
			WebElement row = rows.get(i);
			List<WebElement> cols = row.findElements(By.xpath(xpath));
			int a = headerOrder.get(columnName);
			String actualColValue = cols.get(a).getText();
			if(actualColValue.equalsIgnoreCase(expectedValue)) {
				return row;
			}	
		}			
		return null;
	}
	
	public WebElement getColumnWebElementByColumeValue(String columnName) {
		List<WebElement> headers = getTableHeaders();
		HashMap<String, Integer> headerOrder = getTableHeaderOrder(headers);
		String xpath = "."+ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_TABLE_COLUMNS).get(0).getValue();
		WebElement row = getRow();
		List<WebElement> cols = row.findElements(By.xpath(xpath));
		int a = headerOrder.get(columnName);
		WebElement actualColItem = cols.get(a);
		return actualColItem;		
	}

	/**
	 * Performs search by specified string
	 * 
	 * @author Rakesh.Chalamala
	 * @param search_string
	 */
	public void searchByString(String search_string) {

		waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_COMMON_SEARCHINPUT);
		TextField search = new TextField(SPOGMenuTreePath.SPOG_COMMON_SEARCHINPUT);
		search.setText(search_string);

		waitForMilSeconds(2000);
		Button searchIcon = new Button(SPOGMenuTreePath.SPOG_COMMON_SEARCHICON);
		searchIcon.click();
	}

	/**
	 * searches by specified string and checks filter visibility in search results
	 * for area
	 * 
	 * @author Rakesh.Chalamala
	 * @param search_string
	 * @param test
	 */
	public void searchByStringWithCheck(String search_string, ExtentTest test) {

		test.log(LogStatus.INFO, "Search by string:" + search_string);
		searchByString(search_string);

		test.log(LogStatus.INFO, "Check for applied search in the search results for area");
		checkSelectedFilters(search_string,null,  test);
	}
	
	public void searchByStringWithoutCheck(String search_string, ExtentTest test) {
		waitForSeconds(2);
		test.log(LogStatus.INFO, "Search by string:" + search_string);
		searchByString(search_string);

	}
	
	

	/**
	 * Generic search method for any page to search by name and number of filters
	 * 
	 * Filter format should be
	 * "filter_name1;filtervalue1|value2,filter_name2;filtervalue1|filtervalue2.."
	 * 
	 * @author Rakesh.Chalamala
	 * @param search_string
	 * @param filters
	 * @param test
	 */
	public void searchByFilterTypeAndName(String search_string, ArrayList<String> filters, ExtentTest test) {

		if (search_string != null && !search_string.isEmpty()) {
			test.log(LogStatus.INFO, "Perform search by string:" + search_string);
			searchByString(search_string);
			
			waitForSeconds(2);
		}

		if (filters != null && !filters.isEmpty()) {

			waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_COMMON_SEARCHDROPDOWN,
					BrowserFactory.getMaxTimeWaitUIElement());
			ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_SEARCHDROPDOWN).click();

			List<WebElement> filterEles = ElementFactory.getElements(SPOGMenuTreePath.SPOG_COMMON_SELECTFILTERDIV);

			for (int i = 0; i < filters.size(); i++) {
				String[] filter = filters.get(i).split(";");
				String filterName = filter[0];
				String filterValue = filter[1];

				for (int j = 0; j < filterEles.size(); j++) {

					WebElement filterEle = filterEles.get(j);

					try {
						String actFilterName = filterEle.findElement(By.xpath(".//span")).getText();

						if (actFilterName.equalsIgnoreCase(filterName)) {
							
							filterEle.findElement(By.xpath(".//span[@class='Select-arrow']")).click();
							String[] filterItems = filterValue.split("\\|");

							for (int k = 0; k < filterItems.length; k++) {

								filterEle.findElement(By.xpath(".//span[text()='" + filterItems[k] + "']")).click();
								waitForSeconds(1);
							}
							
							filterEle.findElement(By.xpath(".//span[@class='Select-arrow']")).click();
							break;
						}

					} catch (NoSuchElementException e) {
						if (j == filterEles.size() - 1) {
							assertFalse(true,
									"Filter name: " + filterValue + " does not match with any of displayed in UI.");
						}

						continue;
					} catch (Exception e) {
						e.printStackTrace();
						assertFalse(true, e.getMessage());
					}

				}

			}
//			waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_COMMON_SEARCHBTN, BrowserFactory.getMaxTimeWaitUIElement());
			Button searchBtn = new Button(SPOGMenuTreePath.SPOG_COMMON_SEARCHBTN);
			if (searchBtn.isEnabled()) {
				searchBtn.click();
			} else {
				assertFalse(true, "Search button is disabled.");
			}
		}

	}
	/**
	 * check the selected filters visibility in search results for area
	 * 
	 * Filter format should be
	 * "filter_name1;filtervalue1|value2,filter_name2;filtervalue1|filtervalue2.."
	 * 
	 * @author Rakesh.Chalamala
	 * @param search_string
	 * @param allFilters
	 * @param test
	 */
	public void checkSelectedFilters(String search_string, ArrayList<String> allFilters, ExtentTest test) {

		WebElement resultsForEle = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_SEARCHRESULTSFORDIV);

		if (search_string != null && !search_string.isEmpty()) {

			try {
				test.log(LogStatus.INFO, "search for applied search_string in search results for area");
				resultsForEle.findElement(By.xpath(".//div[@id='" + 0 + "']//span[text()='" + search_string + "']"));

			} catch (NoSuchElementException e) {

				test.log(LogStatus.ERROR, "search string:" + search_string + " not found in search results for area");
				assertFalse(true, "search string:" + search_string + " not found in search results for area");
			}
		}

		int filtersCount = allFilters.size();

		for (int i = 0; i < filtersCount; i++) {
			String[] filter = allFilters.get(i).split(";");

			List<WebElement> filterEles = resultsForEle.findElements(By.xpath(".//div[@id]"));
			for (int j = 0; j < filterEles.size(); j++) {

				WebElement filterEle = filterEles.get(j);

				try {
					filterEles.get(j).findElement(By.xpath("./label/span[text()='" + filter[0] + "']"));

					String[] filterValues = (filter[1]).split("\\|");
					test.log(LogStatus.INFO, "search for filter values in search results for area");
					if (filterValues.length == 1) {
						filterEle.findElement(By.xpath(".//span[text()='" + filterValues[0] + "']"));
						;
					} else if (filterValues.length > 1) {
						filterEle.findElement(By.xpath(".//span[text()='" + filterValues.length + " selected" + "']"));
						;
					}
					break;

				} catch (NoSuchElementException e) {

					if (j == filterEles.size() - 1) {
						test.log(LogStatus.ERROR,
								"filter with name:" + filter[0] + " does not found in the search results for area.");
						assertFalse(true,
								"filter with name:" + filter[0] + " does not found in the search results for area.");
					}
					continue;
				}

			}

		}

	}



	/**
	 * Saves the applied search
	 * 
	 * @author Rakesh.Chalamala
	 * @param searchName
	 */
	public String clickSaveSearchAndSave(String searchName) {

		String result = "false";

		Button saveSearchBtn = new Button(SPOGMenuTreePath.SPOG_COMMON_SAVESEARCH);
		saveSearchBtn.click();

		waitForMilSeconds(500);
		ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_COMMON_SAVESEARCH_TITLE, BrowserFactory.getMaxTimeWaitUIElement());
		
		WebElement saveBtn = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_SAVESEARCH_SAVEBTN);

		assertFalse(saveBtn.isEnabled());

		TextField searchNameFd = new TextField(SPOGMenuTreePath.SPOG_COMMON_SAVESEARCH_NAMEFIELD);
		searchNameFd.setText(searchName);

		if (saveBtn.isEnabled()) {
			saveBtn.click();

			waitForSeconds(2);
			
		/*	String message = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_TOASTMESSAGE).getText();
			assertEquals(message, ToastMessage.saved_search_created_successfully);
			ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_TOASTMESSAGE_CLOSE).click();*/

			result = "true";
		}
		return result;
	}
	/**
	 * Checks saved search is applied or not
	 * 
	 * @author Rakesh.Chalamala
	 * @param searchName
	 * @param test
	 * @return
	 */
	public boolean isSavedSearchActive(String searchName, ExtentTest test) {

		test.log(LogStatus.INFO, "Check is saved search active");

		waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_SAVEDSEARCHESLABEL);
		String xpath = ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_SAVEDSEARCHESDIV).get(0)
				.getValue();

		WebElement ele = ElementFactory.getElementByXpath(xpath + "//button[text()='" + searchName + "']");
		String attrValue = ele.getAttribute("class");

		if (attrValue.equalsIgnoreCase("selectlabel")) {
			return true;
		} else {
			test.log(LogStatus.INFO, "saved search with name:" + searchName + " is not active");
		}

		return false;
	}

	/**
	 * applies a specified savesearch
	 * 
	 * @param searchName
	 * @param enable
	 * @param test
	 */
	public void selectSavedSearch(String searchName, boolean enable, ExtentTest test) {

		waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_SAVEDSEARCHESLABEL);

		String xpath = ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_SAVEDSEARCHESDIV).get(0)
				.getValue();

		WebElement savedSearchEle = ElementFactory.getElementByXpath(xpath + "//button[text()='" + searchName + "']");

		String attrValue = savedSearchEle.getAttribute("class");

		if (enable) {
			if (attrValue.equalsIgnoreCase("selectlabel")) {
				System.out.println("saved search element is active");
			} else {
				savedSearchEle.click();
				assertTrue(isSavedSearchActive(searchName, test));
			}
		} else {
			if (attrValue.equalsIgnoreCase("selectlabel")) {
				savedSearchEle.click();
			} else {
				System.out.println("saved search element is not active");
				assertTrue(!isSavedSearchActive(searchName, test));
			}
		}

	}

	/**
	 * Click on the clear all button after performing search
	 * 
	 * @author Rakesh.Chalamala
	 * 
	 */
	public void clickClearAllFilters() {

		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_COMMON_CLEARALLFILTERS,
				BrowserFactory.getMaxTimeWaitUIElement());

		Button clearAllBtn = new Button(SPOGMenuTreePath.SPOG_COMMON_CLEARALLFILTERS);
		clearAllBtn.click();

		// ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_CLEARALLFILTERS).click();

		waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_SEARCHINPUT);
	}

	/**
	 * Check for saved search in the quick search area
	 * 
	 * @author Rakesh.Chalamala
	 * @param name
	 * @param isSelected
	 */
	public boolean checkSavedSearch(String name, boolean isSelected) {

		WebElement savedSearchesDiv = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_SAVEDSEARCHESDIV);
		try {
			String className = savedSearchesDiv.findElement(By.xpath(".//button[text()='" + name + "']"))
					.getAttribute("class");

			if (isSelected) {
				assertTrue(className != null && !className.isEmpty());
			} else {
				assertTrue(className.isEmpty());
			}
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	/**
	 * Click on manage save search link
	 * 
	 * @author Rakesh.Chalamala
	 */
	public void clickManageSavedSearch() {

		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_COMMON_SETTINGSBTN, BrowserFactory.getMaxTimeWaitUIElement());
		ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_SETTINGSBTN).click();

		WebElement manageSavedSearchesBtn = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_MANAGESAVEDSEARCHES);

		if (manageSavedSearchesBtn.isEnabled()) {
			manageSavedSearchesBtn.click();
			waitForSeconds(1);
			waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_MANAGESAVEDSEARCHES_TITLE);
		} else {
			assertFalse(true, "Manage saved searches button is not enabled");
		}
	}

	/**
	 * Click on the saved search from the quick links available above the table
	 * 
	 * @author Rakesh.Chalamala
	 * @param saveSearchName
	 */
	public void applySavedSearch(String saveSearchName) {

		WebElement savedSearchesDiv = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_SAVEDSEARCHESDIV);
		try {
			WebElement ele = savedSearchesDiv.findElement(By.xpath(".//button[text()='" + saveSearchName + "']"));

			String classAttr = ele.getAttribute("class");

			if (classAttr.isEmpty()) {
				ele.click();
				waitForSeconds(2);
			} else {
				assertTrue(classAttr.contains("selectlabel"));
			}

		} catch (NoSuchElementException e) {
			assertTrue(false, "Saved search with name:" + saveSearchName + " not available.");
		} catch (Exception e) {
			assertFalse(true, e.getMessage());
		}

	}

	/**
	 * Click on the delete button in the manage saved search window
	 * 
	 * @author Rakesh.Chalamala
	 * @param name
	 */
	public void deleteSavedSearch(String name, ExtentTest test) {

		if (isSavedSearchActive(name, test)) {
			selectSavedSearch(name, false, test);
		}

		if (!ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_COMMON_MANAGESAVEDSEARCHES_TITLE)) {
			clickManageSavedSearch();
		}

		waitForSeconds(1);
		WebElement bodyEle = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_MANAGESAVEDSEARCHES_BODYDIV);

		try {
			bodyEle.findElement(By.xpath(".//button[text()='" + name + "']")).click();
			ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_MANAGESAVEDSEARCHES_DELETE).click();

			waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_TOASTMESSAGE);
		} catch (NoSuchElementException e) {
			assertTrue(false, "Saved search with name:" + name + " not available.");
		} catch (Exception e) {
			assertFalse(true, e.getMessage());
		}
	}

	/**
	 * Checks Manage savesearch button is clickable or not
	 * 
	 * @author Rakesh.Chalamala
	 * @return
	 */
	public boolean isManageSavedSearchBtnClickable() {

		waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_MANAGESAVEDSEARCHES);
		if (ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_MANAGESAVEDSEARCHES).isEnabled()) {
			return true;
		}

		return false;
	}

	/**
	 * Updates the savesearch name and doesn't save
	 * 
	 * @author Rakesh.Chalamala
	 * @param oldName
	 * @param newName
	 * @return
	 */
	public boolean updateSaveSearchName(String oldName, String newName) {

		if (!ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_COMMON_MANAGESAVEDSEARCHES_TITLE)) {
			clickManageSavedSearch();
		}

		waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_COMMON_MANAGESAVEDSEARCHES_BODYDIV);
		String xpath = ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_MANAGESAVEDSEARCHES_BODYDIV)
				.get(0).getValue();

		ElementFactory.getElementByXpath(xpath + "//button[text()='" + oldName + "']").click();
		waitForSeconds(1);

		WebElement nameEle = ElementFactory.getElementByXpath(xpath + "//input[@value='" + oldName + "']");
		if (ElementFactory.checkElementClickable(nameEle)) {
			nameEle.clear();
			waitForSeconds(1);
			nameEle.sendKeys(newName);
			return true;
		}

		return false;
	}

	/**
	 * Updates the save search name to a new value and saves
	 * 
	 * @author Rakesh.Chalamala
	 * @param oldName
	 * @param newName
	 * @return
	 */
	public String updateSaveSearchNameAndSave(String oldName, String newName) {

		String result = "fail";

		boolean isUpdated = updateSaveSearchName(oldName, newName);

		if (isUpdated) {
			WebElement saveEle = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_SAVE);
			if (ElementFactory.checkElementClickable(saveEle)) {
				saveEle.click();

				waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_TOASTMESSAGE);
				String message = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_TOASTMESSAGE).getText();

				assertEquals(message, ToastMessage.saved_search_updated_successfully);
				ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_TOASTMESSAGE_CLOSE).click();

				result = "pass";
			} else {
				assertTrue(false, "Sava element is not enabled.");
			}
		}

		return result;
	}

	/**
	 * Makes the save search default by name
	 * 
	 * @author Rakesh.Chalamala
	 * @param searchName
	 * @return
	 */
	public String makeSaveSearchDefault(String searchName) {

		String result = "fail";

		if (!ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_COMMON_MANAGESAVEDSEARCHES_TITLE)) {
			clickManageSavedSearch();
		}

		waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_COMMON_MANAGESAVEDSEARCHES_BODYDIV);
		String bodyXpath = ElementFactory
				.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_MANAGESAVEDSEARCHES_BODYDIV).get(0).getValue();

		ElementFactory.getElementByXpath(bodyXpath + "//button[text()='" + searchName + "']").click();
		waitForSeconds(1);

		String xpath = ElementFactory
				.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_MANAGESAVEDSEARCHES_MAKEDEFAULTDIV).get(0)
				.getValue();

		WebElement checkBoxEle = ElementFactory.getElementByXpath(xpath + "//input[@type='checkbox']");

		if (!checkBoxEle.isSelected()) {

			ElementFactory.getElementByXpath(xpath + "/label").click();
			result = "pass";
		} else {
			result = "pass";
		}

		return result;
	}

	/**
	 * Makes the savedsearch default by it's name and saves
	 * 
	 * @author Rakesh.Chalamala
	 * @param searchName
	 */
	public void makeSaveSearchDefaultAndSave(String searchName) {

		String result = makeSaveSearchDefault(searchName);
		if (result.equalsIgnoreCase("pass")) {
			WebElement saveEle = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_SAVE);
			if (ElementFactory.checkElementClickable(saveEle)) {
				saveEle.click();
			} else {
				assertTrue(false, "Sava element is not enabled.");
			}
		} else {
			assertTrue(false, "failed to make save search default");
		}

	}


	/**
	 * Modifies the saved search info as per the new data
	 * 
	 * Clears the existing selection and selects the new filters
	 * 
	 * Filter format should be
	 * "filter_name1;filtervalue1|value2,filter_name2;filtervalue1|filtervalue2.."
	 * 
	 * @author Rakesh.Chalamala
	 * @param searchName
	 * @param newName
	 * @param search_string
	 * @param filters
	 * @param test
	 */
	public void modifySavedSearch(String searchName, String newName, String search_string, ArrayList<String> filters,
			ExtentTest test) {

		if (newName != null && !newName.isEmpty()) {
			String result = updateSaveSearchNameAndSave(searchName, newName);
			assertEquals(result, "pass");

			clickManageSavedSearch();
			searchName = newName;
		}

		waitForSeconds(2);
		String bodyXpath = ElementFactory
				.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_MANAGESAVEDSEARCHES_BODYDIV).get(0).getValue();
		WebElement searchEle = ElementFactory.getElementByXpath(bodyXpath + "//button[text()='" + searchName + "']");
		if (!searchEle.getAttribute("class").equalsIgnoreCase("active")) {
			searchEle.click();
			waitForSeconds(1);
		}
		

		if (search_string != null && !search_string.isEmpty()) {

			WebElement inputEle = ElementFactory
					.getElement(SPOGMenuTreePath.SPOG_COMMON_MANAGESAVEDSEARCHES_SEARCHSTRING);
			inputEle.clear();
			inputEle.sendKeys(search_string);
		}

		if (filters != null && !filters.isEmpty()) {

			String filterXpath = ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_SELECTFILTERDIV)
					.get(0).getValue();

			List<WebElement> filterEles = ElementFactory.getElementsByXpath(bodyXpath + filterXpath);

			for (int i = 0; i < filterEles.size(); i++) {
				//Clears the existing selection of filter
				WebElement clearBtn = getSelectionClearBtnForSpecifiedFilter(filterEles.get(i));
				if (clearBtn != null) {
					clearBtn.click();
				}
			}
			
			for (int i = 0; i < filters.size(); i++) {
				String[] filter = filters.get(i).split(";");
				String filterName = filter[0];
				String filterValue = filter[1];

				for (int j = 0; j < filterEles.size(); j++) {

					WebElement filterEle = filterEles.get(j);

					try {
						String actFilterName = filterEle.findElement(By.xpath(".//span")).getText();

						if (actFilterName.equalsIgnoreCase(filterName)) {

							filterEle.findElement(By.xpath(".//span[@class='Select-arrow']")).click();
							String[] filterItems = filterValue.split("\\|");

							List<WebElement> listEles = filterEle.findElements(By.xpath(".//div[@role='option']"));

							for (int k = 0; k < filterItems.length; k++) {

								for (int m = 0; m < listEles.size(); m++) {

									WebElement listEle = listEles.get(m);

									String actFilterValue = listEle.findElement(By.xpath(".//span")).getText();
									if (actFilterValue.equalsIgnoreCase(filterItems[k])) {
										String classAttr = listEle.getAttribute("class");

										if (!classAttr.contains("is-selected")) {
											listEle.findElement(By.xpath(".//span[text()='" + filterItems[k] + "']"))
													.click();
										}
										break;
									}

								}
							}
							
							filterEle.findElement(By.xpath(".//span[@class='Select-arrow']")).click();
							break;
						}

					} catch (NoSuchElementException e) {
						if (j == filterEles.size() - 1) {
							assertFalse(true,
									"Filter name: " + filterName + " does not match with any of displayed in UI.");
						}

						continue;
					} catch (Exception e) {
						e.printStackTrace();
						assertFalse(true, e.getMessage());
					}
				}
			}

		}

		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_COMMON_SAVE, BrowserFactory.getMaxTimeWaitUIElement());
		ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_SAVE).click();
	}
	
	/**
	 * Checks whether clear button in filter drop-down is displayed or not 
	 * 
	 * @author Rakesh.Chalamala
	 * @param filterEle
	 * @return
	 */
	public WebElement getSelectionClearBtnForSpecifiedFilter(WebElement filterEle) {
		
		try {
			WebElement selectionClearBtn = filterEle.findElement(By.xpath(".//span[@class='Select-clear']"));
			return selectionClearBtn;
		}catch (NoSuchElementException e) {
			return null;
		}
	}

	/**
	 * Checks the savedsearch(filter) values in manage saved search window a saved
	 * search should be created to verify details
	 * 
	 * Filter format should be
	 * "filter_name1;filtervalue1|value2,filter_name2;filtervalue1|filtervalue2.."
	 * 
	 * @author Rakesh.Chalamala
	 * @param searchName
	 * @param search_string
	 * @param filters
	 * @param test
	 */
	public void checkSavedSearchDetails(String searchName, String search_string, ArrayList<String> filters,
			ExtentTest test) {

		waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_COMMON_MANAGESAVEDSEARCHES_BODYDIV);
		String bodyXpath = ElementFactory
				.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_MANAGESAVEDSEARCHES_BODYDIV).get(0).getValue();

		WebElement searchEle = ElementFactory.getElementByXpath(bodyXpath + "//button[text()='" + searchName + "']");
		if (!searchEle.getAttribute("class").equalsIgnoreCase("active")) {
			searchEle.click();
			waitForSeconds(1);
		}

		if (searchName != null && !searchName.isEmpty()) {

			WebElement nameEle = ElementFactory.getElementByXpath(bodyXpath + "//input[@value='" + searchName + "']");
			assertEquals(searchName, nameEle.getAttribute("value"));
		}

		if (search_string != null && !search_string.isEmpty()) {

			WebElement inputEle = ElementFactory
					.getElement(SPOGMenuTreePath.SPOG_COMMON_MANAGESAVEDSEARCHES_SEARCHSTRING);
			assertEquals(search_string, inputEle.getAttribute("value"));
		}

		if (filters != null && !filters.isEmpty()) {

			String filterXpath = ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_SELECTFILTERDIV)
					.get(0).getValue();

			List<WebElement> filterEles = ElementFactory.getElementsByXpath(bodyXpath + filterXpath);

			for (int i = 0; i < filters.size(); i++) {
				String[] filter = filters.get(i).split(";");
				String filterName = filter[0];
				String filterValue = filter[1];

				for (int j = 0; j < filterEles.size(); j++) {

					WebElement filterEle = filterEles.get(j);

					try {
						String actFilterName = filterEle.findElement(By.xpath(".//span")).getText();

						if (actFilterName.equalsIgnoreCase(filterName)) {

							filterEle.findElement(By.xpath(".//span[@class='Select-arrow']")).click();
							String[] filterItems = filterValue.split("\\|");

							List<WebElement> listEles = filterEle.findElements(By.xpath(".//div[@role='option']"));

							for (int k = 0; k < filterItems.length; k++) {

								for (int m = 0; m < listEles.size(); m++) {

									WebElement listEle = listEles.get(m);

									String actFilterValue = listEle.findElement(By.xpath(".//span")).getText();
									if (actFilterValue.equalsIgnoreCase(filterItems[k])) {
										//waitForSeconds(2);
										String classAttr = listEle.getAttribute("class");

										assertTrue(classAttr.contains("is-selected"));
										break;
									}
								}
							}
							
							filterEle.findElement(By.xpath(".//span[@class='Select-arrow']")).click();
							break;
						}

					} catch (NoSuchElementException e) {
						if (j == filterEles.size() - 1) {
							assertFalse(true,
									"Filter name: " + filterValue + " does not match with any of displayed in UI.");
						}

						continue;
					} catch (Exception e) {
						e.printStackTrace();
						assertFalse(true, e.getMessage());
					}
				}
			}
		}

		clickCloseDialog();
	}
	
	/**
	 * Clicks on the cancel element visible in UI Cancel should be a span element
	 * 
	 * @author Rakesh.Chalamala
	 */
	public void clickCloseDialog() {

		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_COMMON_CLOSEDIALOG, BrowserFactory.getMaxTimeWaitUIElement());
		ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_CLOSEDIALOG).click();
		waitForSeconds(1);
	}

	/**
	 * Clicks on the cancel element visible in UI Cancel should be a span element
	 * 
	 * @author Rakesh.Chalamala
	 */
	public void clickCancel() {

		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_COMMON_CANCEL, BrowserFactory.getMaxTimeWaitUIElement());
		ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_CANCEL);
		waitForSeconds(1);
	}

	/**
	 * checkPagination - check the pagination that applied for the page and
	 * validates from API response
	 * 
	 * @author Ramya.Nagepalli
	 * @param expected
	 *            - expected items count that must be displayed per page
	 * @param exp_rows_count
	 *            - expected total items from API call
	 * @param test
	 * @return
	 */
	public String checkPagination(int expected, int exp_rows_count, ExtentTest test) {
		// TODO Auto-generated method stub

		String result = "fail";

		waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_PAGINATION_PAGINATIONBUTTON);
		WebElement default_ele = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_PAGINATION_PAGINATIONBUTTON);

		int actual = Integer.parseInt(default_ele.getText());

		if (actual == expected) {

			waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_SELECTEDCOUNTLABEL);
			
			int actual_selected_count = selectAllElements(test);

			WebElement rows_count = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_PAGINATION_ROWSPERPAGE);
			int count = Integer.parseInt(rows_count.getText());

			WebElement UI_rows_count = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_PAGINATION_TOTALROWS);
			int UI_count = Integer.parseInt(UI_rows_count.getText());

			if ((actual_selected_count <= actual) && (count == actual_selected_count)
					&& (exp_rows_count == actual_selected_count) && (UI_count == actual_selected_count))
				result = "pass";
		}

		return result;
	}
	
	/**
	 * Updates the page size and verifies
	 * 
	 * @author Rakesh.Chalamala
	 * @param input
	 * @param test
	 */
	public void updatePageSizeWithCheck(int input, ExtentTest test) {

		if (input%5 != 0) {
			assertTrue(false, "Given page size: "+input+ " not a valid value i.e., not in 5,10,20,25,50,100");
		}
		
		waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_COMMON_PAGINATION_PAGINATIONBUTTON);
		WebElement pagination_dropdown_ele = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_PAGINATION_PAGINATIONBUTTON);
		
		if (input != Integer.parseInt(pagination_dropdown_ele.getText())) {
			pagination_dropdown_ele.click();
			
			WebElement input_ele = null;
			String inputXpath = "//a/span[text()='" + input + "']"; 
			
			try {
				test.log(LogStatus.INFO, "set the pagination with given input  : " + input);
				input_ele = ElementFactory.getElementByXpath(inputXpath);
				input_ele.click();
				waitForSeconds(2);
				
			} catch (NoSuchElementException e) {
				System.out.println("Element not found Exception" + e);
				throw new UiElementNotFoundException(inputXpath);
			}	
		}	
		
		WebElement rowsCountEle = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_PAGINATION_ROWSPERPAGE);
		int rowCount = Integer.parseInt(rowsCountEle.getText());
		int paginationDropdownValue = Integer.parseInt(pagination_dropdown_ele.getText());
		assertTrue(input >= rowCount && input == paginationDropdownValue);
	}
	
	

	/**
	 * modifyPagination
	 * 
	 * @author Ramya.Nagepalli
	 * @param page_size
	 * @param test
	 * @return expected_pages
	 */
	public int modifyPagination(int page_size, ExtentTest test) {
		String result = "fail";

		int count = 0;
		int exp_pages = 0;
		int last_row_count = 0;
		int total_size = 0;

		if (page_size == 0)
			page_size = 20;

		test.log(LogStatus.INFO, "get the total size count from UI");
		if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_COMMON_PAGINATION_TOTALROWS, 3))
			total_size = Integer
					.parseInt(ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_PAGINATION_TOTALROWS).getText());
		else
			total_size = 1;

		// 20 is default pagination size, 100 is max limit of any page
		if (((total_size > page_size) && (total_size <= 20)) || ((total_size > 100) && (page_size > 20))
				|| ((total_size > page_size) && (total_size > 20))) {
			last_row_count = total_size % page_size;

			if (last_row_count != 0)
				count = 1;
			else
				last_row_count = page_size;

			exp_pages = (total_size / page_size) + count;
		} else if ((total_size < page_size) && (total_size > 20) && (total_size < 100)
				|| ((total_size < page_size) && (total_size <= 20))) {
			last_row_count = total_size;
			exp_pages = 1;

		} else if ((total_size <= page_size) && (total_size <= 20)) {
			last_row_count = total_size;
			exp_pages = 1;
		}

		test.log(LogStatus.INFO, "apply pagination with the given input page_size : " + page_size);
		result = applyPagination(page_size, test);

		int actual_selected_count = selectAllElements(test);
		test.log(LogStatus.INFO, "get the rows count per page : " + actual_selected_count);

		if (result.equals("pass")) {
			result = "fail";

			if (((exp_pages > 1) && (actual_selected_count == page_size)
					|| ((exp_pages == 1) && (actual_selected_count <= page_size))))
				result = "pass";

		} else {
			test.log(LogStatus.INFO, "Unable to apply pagination with the given input page_size : " + page_size);
			assertFalse(true);
		}

		test.log(LogStatus.INFO, "Check the Result");
		assertEquals(result, "pass");

		return exp_pages;
	}

	/**
	 * applyPagination - modify pagination with given page_size
	 * 
	 * @author Ramya.Nagepalli
	 * @param page_size
	 * @param test
	 * @return
	 */
	public String applyPagination(int page_size, ExtentTest test) {
		String result = "fail";
		waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_PAGINATION_ROWSPERPAGELABEL);
		WebElement pagination_dropdown_ele = ElementFactory
				.getElement(SPOGMenuTreePath.SPOG_COMMON_PAGINATION_PAGINATIONBUTTON);
		pagination_dropdown_ele.click();

		WebElement input_ele = null;
		try {
			test.log(LogStatus.INFO, "set the pagination with given input  : " + page_size);
			input_ele = ElementFactory.getElementByXpath("//a/span[text()='" + page_size + "']");
			input_ele.click();

			result = "pass";

		} catch (NoSuchElementException e) {

			waitForSeconds(1);
			System.out.println("Element not found Exception" + e);
			test.log(LogStatus.INFO, "page_size with given input not found : " + page_size);
			assertFalse(true);

		}

		return result;
	}

	/**
	 * navigateToSpecifiedPage
	 * 
	 * @author Ramya.Nagepalli
	 * @param page
	 * @param page_size
	 * @param test
	 * @return result
	 */
	public String navigateToSpecifiedPage(int page, int page_size, ExtentTest test) {
		String result = "fail";

		test.log(LogStatus.INFO, "Navigate to specified page and get the page size");
		try {

			ElementFactory.getElementByXpath("//div[@class='-pagination align-items-center']//a[text()='" + page + "']")
					.click();

			int act_count = selectAllElements(test);

			if ((act_count == page_size) && (Integer.parseInt(
					ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_PAGINATION_ACTIVEBUTTON).getText()) == page))
				result = "pass";
			else
				assertFalse(true,
						"expected page_size : " + page_size + " not matched with actual page_size : " + act_count);

		} catch (NoSuchElementException e) {

			test.log(LogStatus.INFO, "pagination with page : " + page + " not available");
			assertFalse(true, e.getMessage());
		}

		return result;

	}
	/**
	 * navigateToSpecifiedLink- method used to verify that if click on link it
	 * navigates to valid path
	 * 
	 * @author Ramya.Nagepalli
	 * @param ele_link
	 *            - WebElement of Link
	 * @param exp_path
	 *            - expected navigation xpath
	 * @param test
	 * @return result
	 */
	public String navigateToSpecifiedLink(WebElement ele_link, String exp_path, ExtentTest test) {
		String result = "false";

		test.log(LogStatus.INFO, "Click on the specified link");
		ele_link.click();

		test.log(LogStatus.INFO, "Navigate to the specified path");
		try {

			ElementFactory.getElementByXpath(exp_path);
			result = "true";
		} catch (NoSuchElementException e) {
			// TODO: handle exception
			System.out.println("Not found specified Element" + e);
			assertFalse(true);
		}

		return result;

	}

	/**
	 * checkSelectedElements - click on checkbox of the given selected elements
	 * 
	 * @author Ramya.Nagepalli
	 * @param selected_elements
	 * @param test
	 * @return result
	 */
	public String checkSelectedElements(String[] selected_elements, ExtentTest test) {
		String result = "fail";

		int count = 0;

		waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_TABLE_ROWS);
		List<WebElement> rows = getRows();
		String xpath = "";
		for (int i = 1; i <= rows.size(); i++) {
			xpath = ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_TABLE_ROWS).get(0).getValue();
			xpath = xpath + "[" + i + "]";

			// check for row existence
			ElementFactory.getElementByXpath(xpath);
			// check for name existence in row

			String exp_xpath = xpath + "//a[text()]";
			test.log(LogStatus.INFO, "get the element from the row and check the name of the element");
			WebElement act_ele = ElementFactory.getElementByXpath(exp_xpath);

			String act_element = act_ele.getText();

			for (int j = 0; j < selected_elements.length; j++) {
				if (act_element.contains(selected_elements[j])) {

					String select_eleXpath = ElementFactory
							.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_SELECT_ELEMENT_CHECKBOX).get(0)
							.getValue();

					System.out.println(xpath + select_eleXpath);

					test.log(LogStatus.INFO, "get the checkbox xpath for the Specified element and select the element");
					ElementFactory.getElementByXpath(xpath + select_eleXpath).click();

					count = count + 1;
				}
			}

			if (count == selected_elements.length)
				result = "pass";

		}
		return result;
	}

	/**
	 * ClickBulkAction - Click on bulk actions button on page
	 * 
	 * @author Ramya.Nagepalli
	 * @param test
	 */
	public String clickBulkAction(ExtentTest test) {

		String result = "fail";

		waitForSeconds(1);
		test.log(LogStatus.INFO, "wait until Action button is able to clickable on UI");
		waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_ACTIONS_BUTTON);

		Button Actions_button_ele = null;
		try {
			test.log(LogStatus.INFO, "Click on Actions Button after selecting destinations");
			Actions_button_ele = new Button(SPOGMenuTreePath.SPOG_COMMON_ACTIONS_BUTTON);

			if (Actions_button_ele.isEnabled()) {
				Actions_button_ele.click();

				waitForSeconds(2);
				result = "pass";
			}
			test.log(LogStatus.INFO, "unable to click on Actions Button");

		} catch (ElementNotFoundException e) {
			test.log(LogStatus.INFO, e.getMessage());
			assertFalse(true, "not found element on UI :" + Actions_button_ele);

		}

		return result;
	}

	/**
	 * checkSelectedElements - click on checkbox of the given selected elements
	 * 
	 * @author Ramya.Nagepalli
	 * @param selected_elements
	 * @param test
	 * @return result
	 */
	public String selectRowsWithCheck(ArrayList<String> selected_elements, ExtentTest test) {
		String result = "fail";

		int count = 0;

		waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_TABLE_ROWS);
		List<WebElement> rows = getRows();
		String xpath = "";
		for (int i = 1; i <= rows.size(); i++) {
			xpath = ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_TABLE_ROWS).get(0).getValue();
			xpath = xpath + "[" + i + "]";

			// check for row existence
			ElementFactory.getElementByXpath(xpath);
			// check for name existence in row

			String exp_xpath = xpath + "//a[text()]";
			test.log(LogStatus.INFO, "get the element from the row and check the name of the element");
			WebElement act_ele = ElementFactory.getElementByXpath(exp_xpath);

			String act_element = act_ele.getText();

			for (int j = 0; j < selected_elements.size(); j++) {
				if (act_element.contains(selected_elements.get(j))) {

					String select_eleXpath = ElementFactory
							.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_SELECT_ELEMENT_CHECKBOX).get(0)
							.getValue();

					System.out.println(xpath + select_eleXpath);

					test.log(LogStatus.INFO, "get the checkbox xpath for the Specified element and select the element");
					ElementFactory.getElementByXpath(xpath + select_eleXpath).click();

					count = Integer.parseInt(
							ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_SELECTEDCOUNTLABEL).getText());
				}
			}

			if (count == selected_elements.size())
				result = "pass";

		}
		return result;
	}

	/**
	 * BulkActions- perform bulk actions for the selected elements
	 *
	 * @author Ramya.Nagepalli
	 * @param selected_elements
	 * @param actions
	 * @param test
	 * @return result
	 */

	public String performBulkActionForSpecifiedElements(ArrayList<String> elements, String action, ExtentTest test) {
		String result = "fail";

		test.log(LogStatus.INFO, "Select only specific elements on the page");
		result = selectRowsWithCheck(elements, test);

		WebElement act_action = null;
		if (result.equalsIgnoreCase("pass")) {
			result = "fail";

			test.log(LogStatus.INFO, "Click on the Bulk action button ");
			result = clickBulkAction(test);

			if (result.equals("pass")) {
				result = "fail";

				String xpath = ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_GRIDHEADERDIV).get(0)
						.getValue();
				if ((action == null && action.isEmpty()) || action.contains("No actions supported")) {

					test.log(LogStatus.WARNING, "No action provided.");
					try {
						if (ElementFactory
								.checkElementExists(SPOGMenuTreePath.SPOG_COMMON_ACTIONS_BUTTON_NO_ACTIONS_SUPPORT))
							result = "pass";
					} catch (Exception e) {
						System.out.println("Element not found  with xpath :" + xpath
								+ "//span[contains(text(),'No actions supported')]");
						assertTrue(false, e.getMessage());
					}

				} else {

					test.log(LogStatus.INFO, "Check for the available action :" + action + " and click on the action");

					try {
						act_action = ElementFactory
								.getElementByXpath(xpath + "//span[contains(text(),'" + action + "')]");
					} catch (Exception e) {

						System.out.println("Element not found  with xpath :" + xpath + "//span[contains(text(),'"
								+ action + "')]");
						assertTrue(false, e.getMessage());
					}

					test.log(LogStatus.INFO, "Click on the available bulk action");
					act_action.click();

					result = submitBulkAction(action, test);

				}
			} else {
				test.log(LogStatus.FAIL, "unable to click on bulk action button");
				assertFalse(true, "unable to click on bulk action button");
			}

		} else {
			test.log(LogStatus.FAIL, "given Element is not available on UI" + elements);

			System.out.println("Selected Elements are not checked on UI" + elements);
		}

		return result;

	}

	/**
	 * performBulkActionForAllElements - perform bulk actions for all the elements
	 * on the page
	 * 
	 * @author Ramya.Nagepalli
	 * @param action
	 * @param test
	 * @return result
	 */
	public String performBulkActionForAllElements(String action, ExtentTest test) {
		String result = "fail";

		int count = selectAllElements(test);

		WebElement act_action = null;
		if (count > 0) {

			test.log(LogStatus.INFO, "Click on the Bulk action button ");
			result = clickBulkAction(test);

			if (result.equals("pass")) {
				result = "fail";

				String xpath = ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_GRIDHEADERDIV).get(0)
						.getValue();
				if ((action == null) || (action.isEmpty()) || action.contains("No actions supported")) {

					test.log(LogStatus.WARNING, "No action provided.");
					try {

						if (ElementFactory
								.checkElementExists(SPOGMenuTreePath.SPOG_COMMON_ACTIONS_BUTTON_NO_ACTIONS_SUPPORT))
							result = "pass";

					} catch (NoSuchElementException e) {
						System.out.println("Element not found  with xpath :" + xpath
								+ "//span[contains(text(),'No actions supported')]");
						assertTrue(false, e.getMessage());
					}

				} else {

					test.log(LogStatus.INFO, "Check for the available action :" + action + " and click on the action");

					try {
						act_action = ElementFactory
								.getElementByXpath(xpath + "//span[contains(text(),'" + action + "')]");
					} catch (Exception e) {
						assertTrue(false, e.getMessage());
					}

					test.log(LogStatus.INFO, "Click on the available bulk action");
					act_action.click();

					result = submitBulkAction(action, test);

				}
			} else {
				System.out.println("unable to click on bulk action button");
				assertFalse(true, "unable to click on bulk action button");
			}

		} else {
			System.out.println("There are no rows availale on page to select");
			assertFalse(true, "There are no rows availale on page to select");
		}

		return result;
	}

	/**
	 * selectAllElements - enable checkbox to select all elements and return the
	 * size per page
	 * 
	 * @author Ramya.Nagepalli
	 * @param test
	 * @return
	 */
	public int selectAllElements(ExtentTest test) {
		test.log(LogStatus.INFO, "select all items in current page and return count");
		WebElement select_all_items = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_SELECT_ALL_CHECKBOXES);
		select_all_items.click();

		waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_SELECTEDCOUNTLABEL);
		WebElement selected_count = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_SELECTEDCOUNTLABEL);
		return Integer.parseInt(selected_count.getText().toString());
	}

	/**
	 * submitBulkAction - submit bulk action for the selected elements
	 * 
	 * @author Ramya.Nagepalli
	 * @param action
	 * @param test
	 * @return
	 */
	private String submitBulkAction(String action, ExtentTest test) {

		String result = "fail";

		try {
			test.log(LogStatus.INFO, "Confirm Bulk action for the selected rows :" + action);
			if (ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_DIALOGTITLE).isDisplayed()) {
				String xpath = ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_DIALOGTITLE).get(0)
						.getValue();

				if (ElementFactory.checkElementExists(xpath + "//span[contains(text(),'" + action + "')]")) {

					test.log(LogStatus.INFO, "click on confirm button to submit bulk action");
					ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_CONFIRM).click();

					test.log(LogStatus.INFO, "check the toast message that appears on UI");
					if (ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_TOASTMESSAGE).getText()
							.contains("processed successfully for all selected "))
						result = "pass";

				} else {
					assertFalse(true, "Dialog box not displayed on UI");
				}
			}

		} catch (NoSuchElementException e) {
			test.log(LogStatus.INFO, "Form to confirm bulk action not appeared on UI");
			assertFalse(true);
		}

		return result;
	}

	/**
	 * checkBulkActionsForSpecifiedElements
	 * 
	 * @author Ramya.Nagepalli
	 * @param elements
	 * @param actions
	 * @param test
	 * @return
	 */
	public String checkBulkActionsForSpecifiedElements(ArrayList<String> elements, ArrayList<String> actions,
			ExtentTest test) {
		String result = "fail";

		test.log(LogStatus.INFO, "Select only specific elements on the page");
		result = selectRowsWithCheck(elements, test);

		WebElement act_action = null;
		if (result.equalsIgnoreCase("pass")) {
			result = "fail";

			test.log(LogStatus.INFO, "Click on the Bulk action button ");
			result = clickBulkAction(test);

			if (result.equals("pass")) {
				result = "fail";

				String xpath = ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_GRIDHEADERDIV).get(0)
						.getValue();

				for (int i = 0; i < actions.size(); i++) {

					if ((actions.get(i) == null && actions.get(i).isEmpty())
							|| actions.contains("No actions supported")) {

						test.log(LogStatus.WARNING, "No action provided.");
						try {
							if (ElementFactory
									.checkElementExists(SPOGMenuTreePath.SPOG_COMMON_ACTIONS_BUTTON_NO_ACTIONS_SUPPORT))
								result = "pass";
						} catch (Exception e) {
							System.out.println("Element not found  with xpath :" + xpath
									+ "//span[contains(text(),'No actions supported')]");
							assertTrue(false, e.getMessage());
						}

					} else {

						test.log(LogStatus.INFO,
								"Check for the available action :" + actions.get(i) + " and click on the action");

						try {
							act_action = ElementFactory
									.getElementByXpath(xpath + "//span[contains(text(),'" + actions.get(i) + "')]");
						} catch (Exception e) {

							System.out.println("Element not found  with xpath :" + xpath + "//span[contains(text(),'"
									+ actions.get(i) + "')]");
							assertTrue(false, e.getMessage());
						}
					}
				}
			} else {
				test.log(LogStatus.FAIL, "unable to click on bulk action button");
				assertFalse(true, "unable to click on bulk action button");
			}

		} else {
			test.log(LogStatus.FAIL, "given Element is not available on UI" + elements);

			System.out.println("Selected Elements are not checked on UI" + elements);
		}

		return result;
	}

	/**
	 * Clicks on the X icon in any opened dialog box
	 * 
	 * @author Rakesh.Chalamala
	 * 
	 */
	public void closeModalDialog() {
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_COMMON_CLOSEDIALOG, BrowserFactory.getMaxTimeWaitUIElement());
		ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_CLOSEDIALOG).click();
		waitForSeconds(2);
	}
	
}
