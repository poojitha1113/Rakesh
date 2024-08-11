package ui.spog.server;

import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import ui.base.common.TableHeaders;
import ui.base.common.Url;
import ui.base.factory.BrowserFactory;
import ui.spog.pages.ColumnPage;
import ui.spog.pages.analyze.JobsPage;

public class JobsHelper extends SPOGUIServer{

	static String url = Url.sources;
	JobsPage jobsPage;
	
	public JobsHelper(String browserType, int maxTimeWaitSec) {
		super(browserType, maxTimeWaitSec);
		jobsPage = new JobsPage();
	}
	
	public void checkJobInformation(String jobName, HashMap<String, Object> expectedInfo, ExtentTest test) {
		
		test.log(LogStatus.INFO, "Navigate to Source page");
		goToJobsPage();
				
		test.log(LogStatus.INFO, "validate the details for the sources in the table");
		jobsPage.checkJobDetailsInTable(jobName, expectedInfo, test);
		
		/*@SuppressWarnings("unchecked")
		ArrayList<String> availableActions = (ArrayList<String>) expectedInfo.get("available_actions");
		sourcePage.checkContextualActionsForSpecifiedElement(sourceName, availableActions);*/
	}
	
    public void checkPolicyJobInformation(String jobName, HashMap<String, Object> expectedInfo, ExtentTest test) {
		test.log(LogStatus.INFO, "Navigate to Job page");
		goToJobsPage();
				
		test.log(LogStatus.INFO, "validate the details for the policy in the table");
		jobsPage.checkPolicyJobDetailsInTable(jobName, expectedInfo, test);
		
	}
    
    public void checkEssentialJobInformation(String jobName, HashMap<String, Object> expectedInfo, ExtentTest test) {
		test.log(LogStatus.INFO, "Navigate to Job page");
		goToJobsPage();
				
		test.log(LogStatus.INFO, "validate the details for the policy in the table");
		jobsPage.checkEssentialJobDetailsInTable(jobName, expectedInfo, test);
		
	}
    
    public void checkLogDetailsInTable(int rowNumber, HashMap<String, Object> expectedInfo, ExtentTest test) {
    	test.log(LogStatus.INFO, "validate the details for the log in the table");
		jobsPage.checkLogDetailsInTable(rowNumber, expectedInfo, test);
    }
    
    public void checkFirstLogDetailsInTable( HashMap<String, Object> expectedInfo, ExtentTest test) {
    	test.log(LogStatus.INFO, "validate the details for the first log in the table");
		jobsPage.checkLogDetailsInTable(0, expectedInfo, test);
    }
    
    public void checkLastLogDetailsInTable( HashMap<String, Object> expectedInfo, ExtentTest test) {
    	test.log(LogStatus.INFO, "validate the details for the first log in the table");
		jobsPage.checkLastLogDetailsInTable( expectedInfo, test);
    }
	
    public void waitExpectedJobInSavedSearch(String searchName, String jobName, int waitMinutes, ExtentTest test) {
		boolean findJob=false;
		test.log(LogStatus.INFO, "Navigate to Job page");
		goToJobsPage();
		System.out.println("waitExpectedJobInSavedSearch.goToJobsPage");		
		test.log(LogStatus.INFO, "go to the saved searches page");
		jobsPage.selectSavedSearch(searchName, true,test);
		System.out.println("waitExpectedJobInSavedSearch.selectSavedSearch");	
		findJob=jobsPage.waitExpectedJobInSavedSearch(searchName,jobName, waitMinutes,test);
		System.out.println("waitExpectedJobInSavedSearch.waitExpectedJobInSavedSearch");
        assertTrue(findJob, "The job is showing in the saved search job page");		
	}
    
    public void clickViewLogsForGivenJob(String jobName,ExtentTest test) {
    	test.log(LogStatus.INFO, "Navigate to Job page");
		goToJobsPage();
		jobsPage.clickViewLogsForGivenJob(jobName,test);
	}
    
    public void waitJobDone(String jobName, int waitMinutes, ExtentTest test) {
		boolean findJob=false;
		test.log(LogStatus.INFO, "Navigate to Job page");
		goToJobsPage();
				
		test.log(LogStatus.INFO, "go to job in progress searches page");
		jobsPage.selectSavedSearch("Jobs in Progress", true,test);
		
		findJob=jobsPage.waitJobDone(jobName, waitMinutes,test);
        assertTrue(findJob, "The job is done in the saved search job page");		
	}
    
    public void waitJobDone(String jobName, String jobType,String startTime,int waitMinutes, ExtentTest test) {
		boolean findJob=false;
		test.log(LogStatus.INFO, "Navigate to Job page");
		goToJobsPage();
				
		test.log(LogStatus.INFO, "go to job in progress searches page");
		jobsPage.selectSavedSearch("Successful Jobs", true,test);
		
		findJob=jobsPage.waitJobDone(jobName, jobType,startTime,waitMinutes,test);
        assertTrue(findJob, "The job is done in the saved search job page");		
	}
    
    public String getCurrentJobStartTime(String columeName, ExtentTest test) {
    	return jobsPage.getColumnValueFromFirstRow(columeName, test);
    }
    
    public String getLastColumeValueFromSuccessfulJobs(String columnName,String sourceName, ExtentTest test) {
    	goToJobsPage();
    	jobsPage.selectSavedSearch("Successful Jobs", true,test);
		System.out.println("waitExpectedJobInSavedSearch.selectSavedSearch");
    	//jobsPage.searchByStringWithoutCheck(sourceName, test);
    	String ret=null;
    	ret=jobsPage.getColumnValueFromFirstRow(columnName,test);
    	return ret;
    }
    
    public void clickRecoveryFromFristRow(ExtentTest test) {
    	try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	jobsPage.clickRecoveryFromFristRow( test);
    }
    
    public void clickRecoveryByColumnValue(String columnName,String expectedValue,ExtentTest test) {
    	jobsPage.clickRecoveryByColumnValue( columnName,expectedValue,test);
    }
    
    public void selectRestoreItems(String imgName,String restoreItems) {
    	jobsPage.clickBrowseRecoveryPoints();
    	jobsPage.doubleClickRestoreImg(imgName);
    	jobsPage.selectRestoreItems(restoreItems);
    	jobsPage.clickRestoreUsingDirectAgentButton();
    }
    public void recoveryToOriginal(String destPath,ExtentTest test) {
    	jobsPage.setRecoverDestination( destPath,test);
    	jobsPage.clickNextInRecoverWizard(test);
    	jobsPage.clickRestoreToOriginalInRecoverWizard(test);
    	jobsPage.clickRestoreInRecoverWizard(test);
    }
    
    public void recoveryToAlterMachine(String destPath,String sourceName,ExtentTest test) {
    	jobsPage.setRecoverDestination(destPath,test);
    	jobsPage.clickNextInRecoverWizard(test);
    	jobsPage.clickRestoreToAlterInRecoverWizard(sourceName,test);
    	jobsPage.clickRestoreInRecoverWizard(test);
    }

	public void validateJobsInfomation(ArrayList<HashMap<String, Object>> expectedInfo, ExtentTest test) {

		test.log(LogStatus.INFO, "validate the job details in the table");
		if(jobsPage.getRows().size() == expectedInfo.size()) {
			for (int i = 0; i < expectedInfo.size(); i++) {
				String jobName = expectedInfo.get(i).get(TableHeaders.job_name).toString();
				jobsPage.checkJobDetailsInTable(jobName, expectedInfo.get(i), test);

				/*@SuppressWarnings("unchecked")
				ArrayList<String> availableActions = (ArrayList<String>) expectedInfo.get("available_actions");
				sourcePage.checkContextualActionsForSpecifiedElement(sourceName, availableActions);*/
			}		
	
		}else {
			assertTrue(false, "Actual results size:"+jobsPage.getRows().size()+" does not match with expected size:"+expectedInfo.size());
		}		

	}
	
	public void navigateToJobsPageAndEnableAllColumns() {
		
		goToJobsPage();
		
		ColumnPage columnPage = new ColumnPage();
		columnPage.clickManageColumns();
		columnPage.chooseAllColumns(true);
		columnPage.clickManageColumns();
	}
	
	public void jobFilterCreationCheck(String search_string, ArrayList<String> filters, ExtentTest test) {
		
		test.log(LogStatus.INFO, "Navigate to jobs page");
		JobsPage jobsPage = goToJobsPage();
		
		test.log(LogStatus.INFO, "apply the filters: search_string="+search_string+" filters="+filters);
		jobsPage.searchByFilterTypeAndName(search_string, filters, test);
		
		test.log(LogStatus.INFO, "Check for the applied filters in search results for area");
		jobsPage.checkSelectedFilters(search_string, filters, test);	
		
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
				
		test.log(LogStatus.INFO, "Create filterse on jobs with check");
		jobFilterCreationCheck(search_string, filters, test);
		
		test.log(LogStatus.INFO, "Save the search with name:"+searchName);
		jobsPage.clickSaveSearchAndSave(searchName);
		
		test.log(LogStatus.INFO, "Check the save search applied");
		jobsPage.checkSavedSearch(searchName, true);
	}
	
	/** Search for jobs with given filters input
	 *  Validate the search result available in table
	 * 
	 * @author Rakesh.Chalamala
	 * @param search_string
	 * @param filters
	 * @param expectedJobNames
	 * @param expectedInfo
	 * @param test
	 */
	public void searchJobWithCheck(String search_string, ArrayList<String> filters,
										ArrayList<HashMap<String, Object>> expectedInfo,
										ExtentTest test) {
		
		test.log(LogStatus.INFO, "Perform search");				
		jobFilterCreationCheck(search_string, filters, test);
		
		test.log(LogStatus.INFO, "Check Job details in the table after search");
		for (int i = 0; i < expectedInfo.size(); i++) {
			String jobName = expectedInfo.get(i).get(TableHeaders.job_name).toString();
			jobsPage.checkJobDetailsInTable(jobName, expectedInfo.get(i), test);
		}
	}
	
	/** clear the search items without saving
	 * 
	 * @author Rakesh.Chalamala 
	 */
	public void clearSearchWithoutSaving() {	
	
		jobsPage.clickClearAllFilters();

	}
	
	public void manageSaveSearchWithCheck(String searchName, String newName, String search_string, boolean makeDefault, ArrayList<String> filters, ExtentTest test) {
		
		test.log(LogStatus.INFO, "Navigate to jobs page");
		goToJobsPage();
		
		test.log(LogStatus.INFO, "Check if save search is applied, un-apply it");
		jobsPage.selectSavedSearch(searchName, false, test);
		
		test.log(LogStatus.INFO, "Click on manage save search");
		jobsPage.clickManageSavedSearch();
		
		test.log(LogStatus.INFO, "Modify the save search params and save");
		jobsPage.modifySavedSearch(searchName, newName, search_string, filters, test);
		
		test.log(LogStatus.INFO, "Open manage save search window again and check the details");
		jobsPage.clickManageSavedSearch();
		if (newName != null && !newName.isEmpty()) {
			jobsPage.checkSavedSearchDetails(newName, search_string, filters, test);
		}else {
			jobsPage.checkSavedSearchDetails(searchName, search_string, filters, test);
		}
		
	}
	
	public void applySaveSearchWithCheck(String searchName, ExtentTest test) {
		
		test.log(LogStatus.INFO, "Navigate to jobs page");
		goToJobsPage();
		
		jobsPage.waitForSeconds(4);
		test.log(LogStatus.INFO, "Make save search with name:"+searchName+" active");
		jobsPage.applySavedSearch(searchName);
		
		test.log(LogStatus.INFO, "Check save search with name:"+searchName+" is highlighted or not");
		assertTrue(jobsPage.isSavedSearchActive(searchName, test));
		
	}
	
	public void makeSaveSearchDefaultWithCheck(String searchName, boolean makeDefault, ExtentTest test) {
		
		test.log(LogStatus.INFO, "Navigate to jobs page");
		goToJobsPage();
		
		test.log(LogStatus.INFO, "Click manage save search");
		jobsPage.clickManageSavedSearch();
		
		test.log(LogStatus.INFO, "Make the save search default and save");
		jobsPage.makeSaveSearchDefaultAndSave(searchName);
		jobsPage.waitForSeconds(3);
		jobsPage.checkSavedSearch(searchName, makeDefault);
		
		test.log(LogStatus.INFO, "Re-navigate to jobs page and check whether filter applied default or not");
		//re-navigate and search should be applied
		goToJobsPage();
		jobsPage.checkSavedSearch(searchName, makeDefault);
	}
	
	public void deleteSaveSearchWithCheck(String searchName, ExtentTest test) {
		if (!BrowserFactory.getCurrentPageUrl().equalsIgnoreCase(Url.jobs)) {
			goToJobsPage();	
		}		
		
		test.log(LogStatus.INFO, "Delete save search:"+ searchName);
		jobsPage.deleteSavedSearch(searchName, test);
	}
	
	public void modifyPaginationWithCheck(int pageSize, int totalSize, ExtentTest test) {
		
		test.log(LogStatus.INFO, "Navigate to jobs page");
		goToJobsPage();
		
		test.log(LogStatus.INFO, "Modify pagination with check");
		jobsPage.modifyPagination(pageSize, test);
	}
	
}
