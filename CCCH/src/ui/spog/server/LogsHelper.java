package ui.spog.server;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import ui.base.common.ContextualAction;
import ui.base.common.TableHeaders;
import ui.base.common.Url;
import ui.base.factory.BrowserFactory;
import ui.spog.pages.ColumnPage;
import ui.spog.pages.analyze.LogsPage;

public class LogsHelper extends SPOGUIServer{

	static String url = Url.sources;
	LogsPage logsPage;
	
	public LogsHelper(String browserType, int maxTimeWaitSec) {
		super(browserType, maxTimeWaitSec);
		logsPage = new LogsPage();
	}
	
	public void checkLogInformation(String logName, HashMap<String, Object> expectedInfo, ExtentTest test) {
		
		test.log(LogStatus.INFO, "Navigate to Log page ");
		goToLogsPage();
				
		test.log(LogStatus.INFO, "validate the details for the sources in the table");
		logsPage.checkLogDetailsInTable(logName, expectedInfo, test);
		
	}
	
	public void validateLogsInfomation(ArrayList<HashMap<String, Object>> expectedInfo, ExtentTest test) {

		test.log(LogStatus.INFO, "validate the log details in the table");
		if(logsPage.getRows().size() == expectedInfo.size()) {
			for (int i = 0; i < expectedInfo.size(); i++) {
				String logName = expectedInfo.get(i).get(TableHeaders.message).toString();
				logsPage.checkLogDetailsInTable(logName, expectedInfo.get(i), test);

				/*@SuppressWarnings("unchecked")
				ArrayList<String> availableActions = (ArrayList<String>) expectedInfo.get("available_actions");
				sourcePage.checkContextualActionsForSpecifiedElement(sourceName, availableActions);*/
			}		
	
		}else {
			assertTrue(false, "Actual results size:"+logsPage.getRows().size()+" does not match with expected size:"+expectedInfo.size());
		}		

	}
	
	
	
	public void logFilterCreationCheck(String search_string, ArrayList<String> filters, ExtentTest test) {
		
		test.log(LogStatus.INFO, "Navigate to logs page");
		LogsPage logsPage = goToLogsPage();
		
		test.log(LogStatus.INFO, "apply the filters: search_string="+search_string+" filters="+filters);
		logsPage.searchByFilterTypeAndName(search_string, filters, test);
		
		test.log(LogStatus.INFO, "Check for the applied filters in search results for area");
		logsPage.checkSelectedFilters(search_string, filters, test);	
		
	}
	/** Perform a search with given input and save the search
	 *  Check for the saved search name in the saved searches: area
	 * 
	 * @author Rakesh.Chalamala
	 * @param searchName
	 * @param search_string
	 * @param filters
	 * @param test
	 */
	public void saveSearchWithCheck(String searchName, String search_string, ArrayList<String> filters, ExtentTest test) {
				
		test.log(LogStatus.INFO, "Create filterse on logs with check");
		logFilterCreationCheck(search_string, filters, test);
		
		test.log(LogStatus.INFO, "Save the search with name:"+searchName);
		logsPage.clickSaveSearchAndSave(searchName);
		
		test.log(LogStatus.INFO, "Check the save search applied");
		logsPage.checkSavedSearch(searchName, true);
	}
	
	/** Search for logs with given filters input
	 *  Validate the search result available in table
	 * 
	 * @author Rakesh.Chalamala
	 * @param search_string
	 * @param filters
	 * @param expectedLogNames
	 * @param expectedInfo
	 * @param test
	 */
	public void searchLogWithCheck(String search_string, ArrayList<String> filters,
										ArrayList<HashMap<String, Object>> expectedInfo,
										ExtentTest test) {
		
		test.log(LogStatus.INFO, "Perform search");				
		logFilterCreationCheck(search_string, filters, test);
		
		test.log(LogStatus.INFO, "Check Log details in the table after search");
		for (int i = 0; i < expectedInfo.size(); i++) {
			String logName = expectedInfo.get(i).get(TableHeaders.message).toString();
			logsPage.checkLogDetailsInTable(logName, expectedInfo.get(i), test);
		}
	}
	
	/** clear the search items without saving
	 * 
	 * @author Rakesh.Chalamala 
	 */
	public void clearSearchWithoutSaving() {	
	
		logsPage.clickClearAllFilters();
	}
	
	public void manageSaveSearchWithCheck(String searchName, String newName, String search_string, boolean makeDefault, ArrayList<String> filters, ExtentTest test) {
		
		test.log(LogStatus.INFO, "Navigate to logs page");
		goToLogsPage();
		
		test.log(LogStatus.INFO, "Check if save search is applied, un-apply it");
		logsPage.selectSavedSearch(searchName, false, test);
		
		test.log(LogStatus.INFO, "Click on manage save search");
		logsPage.clickManageSavedSearch();
		
		test.log(LogStatus.INFO, "Modify the save search params and save");
		logsPage.modifySavedSearch(searchName, newName, search_string, filters, test);
		
		test.log(LogStatus.INFO, "Open manage save search window again and check the details");
		logsPage.clickManageSavedSearch();
		if (newName != null && !newName.isEmpty()) {
			logsPage.checkSavedSearchDetails(newName, search_string, filters, test);
		}else {
			logsPage.checkSavedSearchDetails(searchName, search_string, filters, test);
		}
		
	}
	
	public void applySaveSearchWithCheck(String searchName, ExtentTest test) {
		
		test.log(LogStatus.INFO, "Navigate to logs page");
		goToLogsPage();
		
		logsPage.waitForSeconds(4);
		test.log(LogStatus.INFO, "Make save search with name:"+searchName+" active");
		logsPage.applySavedSearch(searchName);
		
		test.log(LogStatus.INFO, "Check save search with name:"+searchName+" is highlighted or not");
		assertTrue(logsPage.isSavedSearchActive(searchName, test));
		
	}
	
	public void makeSaveSearchDefaultWithCheck(String searchName, boolean makeDefault, ExtentTest test) {
		
		test.log(LogStatus.INFO, "Navigate to logs page");
		goToLogsPage();
		
		test.log(LogStatus.INFO, "Click manage save search");
		logsPage.clickManageSavedSearch();
		
		test.log(LogStatus.INFO, "Make the save search default and save");
		logsPage.makeSaveSearchDefaultAndSave(searchName);
		logsPage.waitForSeconds(3);
		logsPage.checkSavedSearch(searchName, makeDefault);
		
		test.log(LogStatus.INFO, "Re-navigate to logs page and check whether filter applied default or not");
		//re-navigate and search should be applied
		goToLogsPage();
		logsPage.checkSavedSearch(searchName, makeDefault);
	}
	
	public void deleteSaveSearchWithCheck(String searchName, ExtentTest test) {
		if (!BrowserFactory.getCurrentPageUrl().equalsIgnoreCase(Url.logs)) {
			goToLogsPage();	
		}		
		
		test.log(LogStatus.INFO, "Delete save search:"+ searchName);
		logsPage.deleteSavedSearch(searchName, test);
	}
	
	public void modifyPaginationWithCheck(int pageSize, int totalSize, ExtentTest test) {
		
		test.log(LogStatus.INFO, "Navigate to logs page");
		goToLogsPage();
		
		test.log(LogStatus.INFO, "Modify pagination with check");
		logsPage.modifyPagination(pageSize, test);
	}
	
	/**
	 * Sort the Grid items by a specified column name and order
	 * 
	 * @author Rakesh.Chalamala
	 * @param columnName
	 * @param sortOrder
	 * @param test
	 */
	public void sortByColumnNameWithCheck(String columnName, String sortOrder, ExtentTest test) {
		
		ColumnPage columnPage = new ColumnPage();
				
		if(columnPage.checkColumnExisted(columnName)) {

			ArrayList<String> expectedValues = columnPage.getValuesInSpecifiedColumn(columnName);

			columnPage.clickOnColumnName(columnName);	
			Collections.sort(expectedValues);
			
			if (sortOrder.equalsIgnoreCase("desc")) {
				columnPage.clickOnColumnName(columnName);	
				Collections.reverse(expectedValues);				
			}
			
			columnPage.waitForSeconds(4);
			ArrayList<String> actualValues = columnPage.getValuesInSpecifiedColumn(columnName);

			columnPage.checkColumnValues(expectedValues, actualValues);
		}else {
			assertFalse(true, "column with name:"+ columnName+" does not exist.");
		}
	}
	
}
