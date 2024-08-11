package ui.spog.server;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.swing.plaf.metal.MetalBorders.TableHeaderBorder;

import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import InvokerServer.SPOGServer;
import genericutil.ExtentManager;
import ui.base.common.ContextualAction;
import ui.base.common.SPOGMenuTreePath;
import ui.base.common.TableHeaders;
import ui.base.common.Url;
import ui.base.factory.BrowserFactory;
import ui.base.factory.ElementFactory;
import ui.spog.pages.ColumnPage;
import ui.spog.pages.protect.SourcePage;

public class SourcePageHelper extends SPOGUIServer{

	static String url = Url.sources;
	private String lastJobTime;
	SourcePage sourcePage;
	
	public SourcePageHelper(String browserType, int maxTimeWaitSec) {
		super(browserType, maxTimeWaitSec);
		sourcePage = new SourcePage();
	}
	
	/** Downloads the agent for the OS and system type provided
	 * 	checks for the file in provided path
	 * 	Deletes the file 
	 * 
	 * @author Rakesh.Chalamala
	 * @param osType
	 * @param systemType
	 * @param downloadPath
	 * @param fileName
	 * @param test
	 */
	public void downloadAgentWithCheck(String osType, String systemType, String downloadPath, String fileName, ExtentTest test) {
		
		test.log(LogStatus.INFO, "Navigate to source page");
		goToSourcesPage();
		
		test.log(LogStatus.INFO, "click on download agent");
		sourcePage.clickDownloadAgent();
		
		test.log(LogStatus.INFO, "Select the specified agent type and download");
		sourcePage.selectAgentTypeAndDownload(osType, systemType, test);
		
		if (systemType.equalsIgnoreCase("64 bit")) {
			systemType = "x64";
		}else if (systemType.equalsIgnoreCase("32 bit")) {
			systemType = "x86";
		}
				
		for (int i = 0; i < 10; i++) {
			this.sourcePage.waitForSeconds(10);
			boolean result = sourcePage.isFileDownloaded(downloadPath, fileName, systemType);
	
			if (result) {
				test.log(LogStatus.PASS, "File found in the specified directory and deleted successfully.");
				break;
			}else if (i == 9) {
				assertTrue(false, "file not found in the specified path.");
			}else {
				continue;
			}			
		}
	}
	
	/** Downloads the OVA
	 * 	checks for the file in provided path
	 * 	Deletes the file 
	 * 
	 * @author Rakesh.Chalamala
	 * @param downloadPath
	 * @param fileName
	 * @param test
	 */
	public void downloadOVAWithCheck(String downloadPath, String fileName, ExtentTest test) {
		
		test.log(LogStatus.INFO, "Navigate to source page");
		goToSourcesPage();
		
		test.log(LogStatus.INFO, "click on download agent");
		sourcePage.clickDownloadAgent();
		
		test.log(LogStatus.INFO, "Click on download OVA form the download agent window");
		sourcePage.clickDownloadOVA(test);
		
		for (int i = 0; i < 5; i++) {
			sourcePage.waitForSeconds(5);
			boolean result = sourcePage.isFileDownloaded(downloadPath, fileName, fileName);
	
			if (result) {
				test.log(LogStatus.PASS, "File found in the specified directory and deleted successfully.");
				break;
			}else if (i == 4) {
				assertTrue(false, "file not found in the specified path.");
			}else {
				continue;
			}			
		}
	}
	
	@SuppressWarnings("unchecked")
	public void checkAddedSources(ArrayList<String> sourceNames, ArrayList<HashMap<String, Object>> expectedInfo, ExtentTest test) {
		
		SourcePage sourcePage = goToSourcesPage();
		
		ColumnPage columnPage = new ColumnPage();
		columnPage.clickManageColumns();
		columnPage.chooseAllColumns(true);
		
		test.log(LogStatus.INFO, "validate the details for the sources in the table");
		sourcePage.checkSourceDetailsInTable(sourceNames, expectedInfo, test);
				
		
		ArrayList<String> actions = null;
		
		for (int i = 0; i < sourceNames.size(); i++) {
			
			actions = (ArrayList<String>) expectedInfo.get(i).get("available_actions");
			
			test.log(LogStatus.INFO, "validate the contextual actions for the source:"+sourceNames.get(i)+" in the table");
			sourcePage.checkContextualActionsForSpecifiedElement(sourceNames.get(i), actions);
		}
		
	}
	
	/** Navigates to sources, enable all columns, checks for source details in table
	 * 
	 * @author Rakesh.Chalamala
	 * @param sourceName
	 * @param expectedInfo
	 * @param test
	 */
	public void checkAddedSourceInformation(String sourceName, HashMap<String, Object> expectedInfo, ExtentTest test) {
		
		test.log(LogStatus.INFO, "Navigate to Source page");
		goToSourcesPage();
				
		test.log(LogStatus.INFO, "validate the details for the sources in the table");
		sourcePage.checkSourceDetailsInTable(sourceName, expectedInfo, test);
		
		@SuppressWarnings("unchecked")
		ArrayList<String> availableActions = (ArrayList<String>) expectedInfo.get("available_actions");
		sourcePage.checkContextualActionsForSpecifiedElement(sourceName, availableActions);
	}
	
	public void validateSourceInfomation(ArrayList<HashMap<String, Object>> expectedInfo, ExtentTest test) {

		test.log(LogStatus.INFO, "validate the details for the sources in the table");
		if(sourcePage.getRows().size() == expectedInfo.size()) {
			for (int i = 0; i < expectedInfo.size(); i++) {
				String sourceName = expectedInfo.get(i).get(TableHeaders.name).toString();
				sourcePage.checkSourceDetailsInTable(sourceName, expectedInfo.get(i), test);	
			}		

			/*@SuppressWarnings("unchecked")
			ArrayList<String> availableActions = (ArrayList<String>) expectedInfo.get("available_actions");
			sourcePage.checkContextualActionsForSpecifiedElement(sourceName, availableActions);*/	
		}else {
			assertTrue(false, "Actual results size:"+sourcePage.getRows().size()+" does not match with expected size:"+expectedInfo.size());
		}		

	}
	
	public void navigateToSourcePageAndEnableAllColumns() {
				
		goToSourcesPage();
		
		ColumnPage columnPage = new ColumnPage();
		columnPage.waitForSeconds(1);
		
		columnPage.clickManageColumns();
		columnPage.chooseAllColumns(true);
		columnPage.clickManageColumns();
	}
	
	public void sourceFilterCreationCheck(String search_string, ArrayList<String> filters, ExtentTest test) {
		
		test.log(LogStatus.INFO, "Navigate to source page");
		SourcePage sourcePage = goToSourcesPage();
		
		
		sourcePage.waitForSeconds(2);
		test.log(LogStatus.INFO, "apply the filters: search_string="+search_string+" filters="+filters);
		sourcePage.searchByFilterTypeAndName(search_string, filters, test);
		
		sourcePage.waitForSeconds(2);
		
		test.log(LogStatus.INFO, "Check for the applied filters in search results for area");
		sourcePage.checkSelectedFilters(search_string, filters, test);	
		
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
		
		test.log(LogStatus.INFO, "Create filterse on source with check");
		sourceFilterCreationCheck(search_string, filters, test);
		
		test.log(LogStatus.INFO, "Save the search with name:"+searchName);
		sourcePage.clickSaveSearchAndSave(searchName);
		
		test.log(LogStatus.INFO, "Check the save search applied");
		sourcePage.checkSavedSearch(searchName, true);		
	}
	
	/** Search for sources with given filters input
	 *  Validate the search result available in table
	 * 
	 * @author Rakesh.Chalamala
	 * @param search_string
	 * @param filters
	 * @param expectedSourceNames
	 * @param expectedInfo
	 * @param test
	 */
	public void searchSourceWithCheck(String search_string, ArrayList<String> filters,
										ArrayList<HashMap<String, Object>> expectedInfo,
										ExtentTest test) {
				
		test.log(LogStatus.INFO, "Perform search");				
		sourceFilterCreationCheck(search_string, filters, test);
		
		test.log(LogStatus.INFO, "Check source details in the table after search");
		for (int i = 0; i < expectedInfo.size(); i++) {
			String sourceName = expectedInfo.get(i).get(TableHeaders.name).toString();
			sourcePage.checkSourceDetailsInTable(sourceName, expectedInfo.get(i), test);
		}
		
	}
	
	/** clear the search items without saving
	 * 
	 * @author Rakesh.Chalamala 
	 */
	public void clearSearchWithoutSaving() {	
	
		sourcePage.clickClearAllFilters();
	}
	
	public void manageSaveSearchWithCheck(String searchName, String newName, String search_string, boolean makeDefault, ArrayList<String> filters, ExtentTest test) {
		
		test.log(LogStatus.INFO, "Navigate to source page");
		goToSourcesPage();
		
		test.log(LogStatus.INFO, "Check if save search is applied, un-apply it");
		sourcePage.selectSavedSearch(searchName, false, test);
		
		test.log(LogStatus.INFO, "Click on manage save search");
		sourcePage.clickManageSavedSearch();
		
		test.log(LogStatus.INFO, "Modify the save search params and save");
		sourcePage.modifySavedSearch(searchName, newName, search_string, filters, test);
		
		test.log(LogStatus.INFO, "Open manage save search window again and check the details");
		sourcePage.clickManageSavedSearch();
		if (newName != null && !newName.isEmpty()) {
			sourcePage.checkSavedSearchDetails(newName, search_string, filters, test);
		}else {
			sourcePage.checkSavedSearchDetails(searchName, search_string, filters, test);
		}
		
	}
	
	public void applySaveSearchWithCheck(String searchName, ExtentTest test) {
		
		test.log(LogStatus.INFO, "Navigate to source page");
		goToSourcesPage();
		
		test.log(LogStatus.INFO, "Make save search with name:"+searchName+" active");
		sourcePage.applySavedSearch(searchName);
		
		test.log(LogStatus.INFO, "Check save search with name:"+searchName+" is highlighted or not");
		assertTrue(sourcePage.isSavedSearchActive(searchName, test));
		
		sourcePage.waitForSeconds(2);
	}
	
	
	public void makeSaveSearchDefaultWithCheck(String searchName, boolean makeDefault, ExtentTest test) {
		
		test.log(LogStatus.INFO, "Navigate to source page");
		goToSourcesPage();
		
		test.log(LogStatus.INFO, "Click manage save search");
		sourcePage.clickManageSavedSearch();
		
		test.log(LogStatus.INFO, "Make the save search default and save");
		sourcePage.makeSaveSearchDefaultAndSave(searchName);
		sourcePage.waitForSeconds(3);
		sourcePage.checkSavedSearch(searchName, makeDefault);
		
		test.log(LogStatus.INFO, "Re-navigate to source page and check whether filter applied default or not");
		//re-navigate and search should be applied
		goToSourcesPage();
		sourcePage.checkSavedSearch(searchName, makeDefault);
	}
	
	public void deleteSaveSearchWithCheck(String searchName, ExtentTest test) {
		if (!BrowserFactory.getCurrentPageUrl().equalsIgnoreCase(Url.sources)) {
			goToSourcesPage();	
		}		
		test.log(LogStatus.INFO, "Delete save search:"+ searchName);
		sourcePage.deleteSavedSearch(searchName, test);
		
		test.log(LogStatus.INFO, "Check that saved search is not available after deletion");
		assertFalse(sourcePage.checkSavedSearch(searchName, false));
		
		test.log(LogStatus.PASS, "Saved search with name:"+ searchName+" deleted successfully");
	}
	
	public void modifyPaginationWithCheck(int pageSize, int totalSize, ExtentTest test) {
		
		test.log(LogStatus.INFO, "Navigate to source page");
		goToSourcesPage();
		
		test.log(LogStatus.INFO, "Modify pagination with check");
		sourcePage.modifyPagination(pageSize, test);
		
	}
	
	public void deleteSourceWithCheck(String sourceName, boolean delete, ExtentTest test) {
		
		test.log(LogStatus.INFO, "Navigate to source page");
		goToSourcesPage();
		
		test.log(LogStatus.INFO, "search for source with name:"+sourceName);
		sourcePage.searchByStringWithCheck(sourceName, test);
		
		if (delete) {
			
			test.log(LogStatus.INFO, "Click on contextual action of source to delete");
			sourcePage.clickContextualActionForSpecifiedElement(sourceName, ContextualAction.DELETE);
			
			test.log(LogStatus.INFO, "Delete source specified");
			sourcePage.clickDeleteSource();	
		}else {
			
			test.log(LogStatus.INFO, "Click on contextual action of source to delete");
			sourcePage.clickContextualActionForSpecifiedElement(sourceName, ContextualAction.DELETE);
			
			test.log(LogStatus.INFO, "Delete source specified");
			sourcePage.clickCancel();
			
			assertTrue(sourcePage.checkSourceExists(sourceName));
		}		
	}
	
	
    public void startBackup(String sourceName, ExtentTest test) {
		
		test.log(LogStatus.INFO, "Navigate to source page");
		goToSourcesPage();
		
		test.log(LogStatus.INFO, "search for source with name:"+sourceName);
		sourcePage.searchByStringWithoutCheck(sourceName, test);
		sourcePage.checkSourceExists(sourceName);
		test.log(LogStatus.INFO, "Click on contextual action of source to start backup");
		sourcePage.clickStartBackup();
		test.log(LogStatus.INFO, "start backup for the given source");
		//sourcePage.waitUntilJobUpdatedFromLatestJobColume("backup",this.lastJobTime,"In Progress","2 Jobs",10);
	}
    
    public void startRecover(String sourceName, ExtentTest test) {
		
		test.log(LogStatus.INFO, "Navigate to source page");
		goToSourcesPage();
		
		test.log(LogStatus.INFO, "search for source with name:"+sourceName);
		sourcePage.searchByStringWithoutCheck(sourceName, test);
		sourcePage.checkSourceExists(sourceName);
		test.log(LogStatus.INFO, "Click on contextual action of source to start backup");
		sourcePage.clickStartRecovery();
		test.log(LogStatus.INFO, "start recover for the given source");
		//sourcePage.waitUntilJobUpdatedFromLatestJobColume("backup",this.lastJobTime,"In Progress","2 Jobs",10);
	}
    
    
    public void waitUntilJobUpdatedFromLatestJobColume(String sourceName,String job_type,String last_jobtime,String expectedJobStatus,String contentFromLatestJobColume,int waitMinutes, ExtentTest test) {
    	test.log(LogStatus.INFO, "Navigate to source page");
		goToSourcesPage();
		
		test.log(LogStatus.INFO, "search for source with name:"+sourceName);
		sourcePage.searchByStringWithoutCheck(sourceName, test);
		sourcePage.waitUntilJobUpdatedFromLatestJobColume(job_type,last_jobtime,expectedJobStatus,contentFromLatestJobColume,waitMinutes);
    }
    
    public void checkJobinformationFromLatestJobColume(String sourceName,String job_type,String job_status, ExtentTest test) {
    	test.log(LogStatus.INFO, "Navigate to source page");
		goToSourcesPage();
		
		test.log(LogStatus.INFO, "search for source with name:"+sourceName);
		sourcePage.searchByStringWithoutCheck(sourceName, test);
		sourcePage.checkLastJobStatusFromLatestJobColume(job_status);
		sourcePage.checkLastJobTypeFromLatestJobColume(job_type);
    }
    
    public String getLastJobTimeFromLatestJobColume(String sourceName, ExtentTest test) {
    	goToSourcesPage();
		sourcePage.searchByStringWithoutCheck(sourceName, test);
    	String ret=null;
    	ret=sourcePage.getLastJobTimeFromLatestJobColume();
    	return ret;
    }
    
    public String getLatestRecoveryPointJobTimeFromLatestJobColume(String sourceName, ExtentTest test) {
    	goToSourcesPage();
		sourcePage.searchByStringWithoutCheck(sourceName, test);
    	String ret=null;
    	ret=sourcePage.getLatestRecoveryPointJobTimeFromLatestJobColume();
    	return ret;
    }
}
