package ui.spog.pages.analyze;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import genericutil.ErrorHandler;
import ui.base.common.BasePage;
import ui.base.common.SPOGMenuTreePath;
import ui.base.common.TableHeaders;
import ui.base.factory.ElementFactory;
import ui.spog.pages.ColumnPage;
import ui.spog.server.SPOGUIServer;

public class JobsPage extends BasePage{

	private ErrorHandler errorHandle = BasePage.getErrorHandler();
	
	public boolean checkSourceExists(String sourceName) {
		boolean isExist = false;
		try {
			waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_TABLE_ROWS);
			WebElement tableBody = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_TABLE_BODY);
			WebElement customerElement = tableBody.findElement(By.xpath(".//span[text()='" + sourceName + "']"));
			assertEquals(customerElement.getText(), sourceName);

			isExist = true;
		} catch (NoSuchElementException e) {
			errorHandle.printErrorMessageInDebugFile("Source with name: " + sourceName + " does not exist");
		}

		return isExist;
	}
	
	public boolean checkJobExists(String jobName) {
		boolean isExist = false;
		try {
			waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_TABLE_ROWS);
			WebElement tableBody = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_TABLE_BODY);
			WebElement customerElement = tableBody.findElement(By.xpath(".//a[text()='" + jobName + "']"));
			assertEquals(customerElement.getText(), jobName);

			isExist = true;
		} catch (NoSuchElementException e) {
			errorHandle.printErrorMessageInDebugFile("Job with name: " + jobName + " does not exist");
		}

		return isExist;
	}
	
	public boolean checkJobExistsInSuccessfulJobs(String jobName,String jobType,String startTime) {
		try {
			waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_TABLE_ROWS);
			HashMap<String, Integer> jobTableHeaderOrder = getTableHeaderOrder(getTableHeaders());
			String xpath = "." + ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_TABLE_COLUMNS).get(0)
					.getValue();
			WebElement eachRow = getRow();
			List<WebElement> columns = eachRow.findElements(By.xpath(xpath));
			if (columns.get(jobTableHeaderOrder.get(TableHeaders.job_name)).getText().equalsIgnoreCase(jobName)&& columns.get(jobTableHeaderOrder.get(TableHeaders.job_type)).getText().equalsIgnoreCase(jobType)&&columns.get(jobTableHeaderOrder.get(TableHeaders.status)).getText().equalsIgnoreCase("Finished")) {
				if(!columns.get(jobTableHeaderOrder.get(TableHeaders.start_time)).getText().equalsIgnoreCase(startTime)) {
					System.out.println("get current restore job from successful is:"+columns.get(jobTableHeaderOrder.get(TableHeaders.start_time)).getText());
					return true;
				}else {
					return false;
				}
			}else {
				return false;
			}
		} catch (NoSuchElementException e) {
			errorHandle.printErrorMessageInDebugFile("Job with name: " + jobName + " does not exist");
		}
		return false;
	}
	
	public boolean checkInProgressJobExists(String jobName) {
		boolean isExist = false;
		try {
			waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_TABLE_ROWS);
			WebElement tableBody = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_TABLE_BODY);
			WebElement customerElement = tableBody.findElement(By.xpath(".//a[text()='" + jobName + "']"));
			WebElement customerProgressElement = tableBody.findElement(By.xpath(".//div[@class='progress-content']"));
			String progressContent=customerProgressElement.getText().toString();
			System.out.println("progress:"+progressContent);
			if(progressContent.toLowerCase().indexOf("in progress")!=-1) {
				assertTrue(true, "job progress bar is existing.");
			}else {
				assertTrue(false, "job progress bar is existing.");
			}			
			assertEquals(customerElement.getText(), jobName);
			isExist = true;
		} catch (NoSuchElementException e) {
			errorHandle.printErrorMessageInDebugFile("Job with name: " + jobName + " does not exist");
		}

		return isExist;
	}
	
	public boolean checkNoJobExists() {
		boolean isNonExist = false;
		try {
			waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_TABLE_BODY);
			waitForMilSeconds(5000);
			WebElement totalnumber = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_TOTALNUMBER);
			waitForMilSeconds(5000);
			if(totalnumber.getText().toString().equalsIgnoreCase("0")) {
				isNonExist = true;
			}			
		} catch (NoSuchElementException e) {
			errorHandle.printErrorMessageInDebugFile("There is no table in job page");
		}
		return isNonExist;
	}	
	
    public boolean waitExpectedJobInSavedSearch(String searchName,String jobName, int waitMinutes, ExtentTest test) {
		boolean findJob=false;
		waitForMilSeconds(5000);
		for(int waitTimes=0;waitTimes<10*waitMinutes;waitTimes++) {
			if (checkNoJobExists()) {
				System.out.println("waitExpectedJobInSavedSearch.checkNoJobExists for time:"+waitTimes);
				selectSavedSearch(searchName, false,test);
    			waitForMilSeconds(3000);
    		}else {
    			if(checkInProgressJobExists(jobName)) {
    				System.out.println("waitExpectedJobInSavedSearch.checkJobExists");
    				findJob=true;
        			break;
    			}    			
    		}
			selectSavedSearch(searchName, true,test);    
			waitForMilSeconds(3000);
    	}
		return findJob;
	}
    
    public boolean waitJobDone(String jobName, int waitMinutes, ExtentTest test) {
		boolean jobDone=false;
		waitForMilSeconds(5000);
		for(int waitTimes=0;waitTimes<20*waitMinutes;waitTimes++) {
			if (checkNoJobExists()) {
				jobDone=true;
				break;
    		}else {
    			if(checkJobExists(jobName)) {
    				selectSavedSearch("Jobs in Progress", false,test);
    				System.out.println("This is "+waitTimes+ " time.");
        			waitForMilSeconds(3000);
    			}else {
    				jobDone=true;
    				break;
    			}
    		}
			selectSavedSearch("Jobs in Progress", true,test);    		
    	}
		return jobDone;
	}
    
    public boolean waitJobDone(String jobName, String jobType,String startTime,int waitMinutes, ExtentTest test) {
		boolean jobDone=false;
		waitForMilSeconds(2000);
		for(int waitTimes=0;waitTimes<20*waitMinutes;waitTimes++) {
			if(checkJobExistsInSuccessfulJobs(jobName,jobType,startTime)) {
				return true;
			}else {
				selectSavedSearch("Successful Jobs", false,test);
    			waitForMilSeconds(3000);
			}
			selectSavedSearch("Successful Jobs", true,test);    		
    	}
		return jobDone;
	}
	
	/**
	 * Verifies the details of each job in table
	 * 
	 * @author Rakesh.Chalamala
	 * @param jobName
	 * @param expectedInfo
	 * @param test
	 */
	public void checkJobDetailsInTable(String jobName, HashMap<String, Object> expectedInfo, ExtentTest test) {

		HashMap<String, Integer> jobTableHeaderOrder = getTableHeaderOrder(getTableHeaders());

		List<WebElement> rows = getRows();
		int rowCount = rows.size();

		if (rowCount == 0 && expectedInfo != null && !expectedInfo.isEmpty()) {
			test.log(LogStatus.WARNING, "no rows found");
			assertTrue(false);
		}

		for (int i = 0; i < rowCount; i++) {
			WebElement eachRow = rows.get(i);

			String xpath = "." + ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_TABLE_COLUMNS).get(0)
					.getValue();
			List<WebElement> columns = eachRow.findElements(By.xpath(xpath));
			/*List<WebElement> columns = null;
			try {
				columns = eachRow.findElements(By.xpath(xpath));	
			} catch (StaleElementReferenceException e) {
				rows.clear();
				rows = getRows();
				eachRow = rows.get(i);
				columns = eachRow.findElements(By.xpath(xpath));
			}*/
			

			if (columns.get(jobTableHeaderOrder.get(TableHeaders.job_name)).getText()
					.equalsIgnoreCase(expectedInfo.get(TableHeaders.job_name).toString())) {
				
				assertTrue(columns.get(jobTableHeaderOrder.get(TableHeaders.source)).getText().equalsIgnoreCase(expectedInfo.get(TableHeaders.source).toString()));
				assertTrue(columns.get(jobTableHeaderOrder.get(TableHeaders.job_name)).getText().equalsIgnoreCase(expectedInfo.get(TableHeaders.job_name).toString()));
				assertTrue(columns.get(jobTableHeaderOrder.get(TableHeaders.end_time)).getText().trim().equalsIgnoreCase(expectedInfo.get(TableHeaders.end_time).toString()));
				assertTrue(columns.get(jobTableHeaderOrder.get(TableHeaders.start_time)).getText().trim().equalsIgnoreCase(expectedInfo.get(TableHeaders.start_time).toString()));
//				assertTrue(columns.get(jobTableHeaderOrder.get(TableHeaders.destination)).getText().equalsIgnoreCase(expectedInfo.get(TableHeaders.destination).toString()));
//				assertTrue(columns.get(jobTableHeaderOrder.get(TableHeaders.policy)).getText().equalsIgnoreCase(expectedInfo.get(TableHeaders.policy).toString()));
				assertTrue(columns.get(jobTableHeaderOrder.get(TableHeaders.status)).getText().toLowerCase().contains(expectedInfo.get(TableHeaders.status).toString().toLowerCase()));
				assertTrue(columns.get(jobTableHeaderOrder.get(TableHeaders.job_type)).getText().equalsIgnoreCase(expectedInfo.get(TableHeaders.job_type).toString()));
//				assertTrue(columns.get(jobTableHeaderOrder.get(TableHeaders.duration)).getText().equalsIgnoreCase(expectedInfo.get(TableHeaders.duration).toString()));

				break;
			}

			if (i == rowCount - 1) {
				assertTrue(false, "job with name: " + expectedInfo.get(TableHeaders.name) + " not matched");
			}
		}
	}
	
	/**
	 * Verifies the details of each job in table
	 * 
	 * @author Jing.Shan
	 * @param jobName
	 * @param expectedInfo
	 * @param test
	 */
	public void checkEssentialJobDetailsInTable(String jobName, HashMap<String, Object> expectedInfo, ExtentTest test) {

		HashMap<String, Integer> jobTableHeaderOrder = getTableHeaderOrder(getTableHeaders());

		List<WebElement> rows = getRows();
		int rowCount = rows.size();

		if (rowCount == 0 && expectedInfo != null && !expectedInfo.isEmpty()) {
			test.log(LogStatus.WARNING, "no rows found");
			assertTrue(false);
		}

		for (int i = 0; i < rowCount; i++) {
			WebElement eachRow = rows.get(i);

			String xpath = "." + ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_TABLE_COLUMNS).get(0)
					.getValue();
			List<WebElement> columns = eachRow.findElements(By.xpath(xpath));
			if (columns.get(jobTableHeaderOrder.get(TableHeaders.job_name)).getText()
					.equalsIgnoreCase(expectedInfo.get(TableHeaders.job_name).toString())) {
				
				assertTrue(columns.get(jobTableHeaderOrder.get(TableHeaders.source)).getText().trim().equalsIgnoreCase(expectedInfo.get(TableHeaders.source).toString()),"check source");
				assertTrue(columns.get(jobTableHeaderOrder.get(TableHeaders.start_time)).getText().trim().equalsIgnoreCase(expectedInfo.get(TableHeaders.start_time).toString()),"check start time");
				assertTrue(columns.get(jobTableHeaderOrder.get(TableHeaders.destination)).getText().equalsIgnoreCase(expectedInfo.get(TableHeaders.destination).toString()),"check destination");
				assertTrue(columns.get(jobTableHeaderOrder.get(TableHeaders.policy)).getText().equalsIgnoreCase(expectedInfo.get(TableHeaders.policy).toString()),"check policy");
				assertTrue(columns.get(jobTableHeaderOrder.get(TableHeaders.status)).getText().toLowerCase().contains(expectedInfo.get(TableHeaders.status).toString().toLowerCase()),"check job status");
				assertTrue(columns.get(jobTableHeaderOrder.get(TableHeaders.job_type)).getText().toLowerCase().contains(expectedInfo.get(TableHeaders.job_type).toString().toLowerCase()),"check job type");
				break;
			}

			if (i == rowCount - 1) {
				assertTrue(false, "job with name: " + expectedInfo.get(TableHeaders.name) + " not matched");
			}
		}
	}
	
	/**
	 * Verifies the log details of given row number in table
	 * 
	 * @author Jing.Shan
	 * @param rownumber
	 * @param expectedInfo
	 * @param test
	 */
	public void checkLogDetailsInTable(int rowNumber, HashMap<String, Object> expectedInfo, ExtentTest test) {

		HashMap<String, Integer> jobTableHeaderOrder = getTableHeaderOrder(getTableHeaders());

		List<WebElement> rows = getRows();
		int rowCount = rows.size();

		if (rowCount == 0) {
			test.log(LogStatus.WARNING, "no rows found");
			assertTrue(false);
		}
		if (rowCount <= rowNumber) {
			test.log(LogStatus.WARNING, "has no given row");
			assertTrue(false);
		}
		WebElement eachRow = rows.get(rowNumber);
		String xpath = "." + ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_TABLE_COLUMNS).get(0)
				.getValue();
		List<WebElement> columns = eachRow.findElements(By.xpath(xpath));
		if (columns.get(jobTableHeaderOrder.get(TableHeaders.job_name)).getText()
				.equalsIgnoreCase(expectedInfo.get(TableHeaders.job_name).toString())) {
			assertTrue(columns.get(jobTableHeaderOrder.get(TableHeaders.source)).getText().trim().equalsIgnoreCase(expectedInfo.get(TableHeaders.source).toString()),"check source");
			assertTrue(columns.get(jobTableHeaderOrder.get(TableHeaders.generated_from)).getText().trim().equalsIgnoreCase(expectedInfo.get(TableHeaders.generated_from).toString()),"check start time");
			assertTrue(columns.get(jobTableHeaderOrder.get(TableHeaders.message)).getText().toLowerCase().contains(expectedInfo.get(TableHeaders.message).toString().toLowerCase()),"check job status");
			assertTrue(columns.get(jobTableHeaderOrder.get(TableHeaders.job_type)).getText().toLowerCase().contains(expectedInfo.get(TableHeaders.job_type).toString().toLowerCase()),"check job type");
		}

	}
	
	public void checkLastLogDetailsInTable( HashMap<String, Object> expectedInfo, ExtentTest test) {

		HashMap<String, Integer> jobTableHeaderOrder = getTableHeaderOrder(getTableHeaders());

		List<WebElement> rows = getRows();
		int rowCount = rows.size();

		if (rowCount == 0) {
			test.log(LogStatus.WARNING, "no rows found");
			assertTrue(false);
		}
		
		WebElement eachRow = rows.get(rowCount-1);
		String xpath = "." + ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_TABLE_COLUMNS).get(0)
				.getValue();
		List<WebElement> columns = eachRow.findElements(By.xpath(xpath));
		if (columns.get(jobTableHeaderOrder.get(TableHeaders.job_name)).getText()
				.equalsIgnoreCase(expectedInfo.get(TableHeaders.job_name).toString())) {
			System.out.println("messge is:"+columns.get(jobTableHeaderOrder.get(TableHeaders.message)).getText().toLowerCase());
			assertTrue(columns.get(jobTableHeaderOrder.get(TableHeaders.source)).getText().trim().equalsIgnoreCase(expectedInfo.get(TableHeaders.source).toString()),"check source");
			assertTrue(columns.get(jobTableHeaderOrder.get(TableHeaders.generated_from)).getText().trim().equalsIgnoreCase(expectedInfo.get(TableHeaders.generated_from).toString()),"check start time");
			assertTrue(columns.get(jobTableHeaderOrder.get(TableHeaders.message)).getText().toLowerCase().contains(expectedInfo.get(TableHeaders.message).toString().toLowerCase()),"check job message");
			assertTrue(columns.get(jobTableHeaderOrder.get(TableHeaders.job_type)).getText().toLowerCase().contains(expectedInfo.get(TableHeaders.job_type).toString().toLowerCase()),"check job type");
		}

	}
	
	public void clickViewLogsForGivenJob(String jobName,ExtentTest test) {

		WebElement eachRow = getRow();
		WebElement clickLogBtn = eachRow.findElement(By.xpath(".//div[@class='dropdown btn-group']//button[@role='button']//span[@class='caret']"));
		clickLogBtn.click();
		WebElement viewLogBtn = eachRow.findElement(By.xpath(".//ul[@class='dropdown-menu']//span[text()='View Logs']"));
		viewLogBtn.click();			
	}
	
	/**
	 * Verifies the details of each job in table
	 * 
	 * @author Jing.shan
	 * @param jobName
	 * @param expectedInfo
	 * @param test
	 */
	public void checkPolicyJobDetailsInTable(String jobName, HashMap<String, Object> expectedInfo, ExtentTest test) {
		HashMap<String, Integer> jobTableHeaderOrder = getTableHeaderOrder(getTableHeaders());

		List<WebElement> rows = getRows();
		int rowCount = rows.size();

		if (rowCount == 0 && expectedInfo != null && !expectedInfo.isEmpty()) {
			test.log(LogStatus.WARNING, "no rows found");
			assertTrue(false);
		}

		for (int i = 0; i < rowCount; i++) {
			WebElement eachRow = rows.get(i);

			String xpath = "." + ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_TABLE_COLUMNS).get(0)
					.getValue();
			List<WebElement> columns = eachRow.findElements(By.xpath(xpath));
			
			if (columns.get(jobTableHeaderOrder.get(TableHeaders.policy)).getText()
					.equalsIgnoreCase(expectedInfo.get(TableHeaders.policy).toString())) {
				String jobString=columns.get(jobTableHeaderOrder.get(TableHeaders.job_name)).getText();
				String policyString=columns.get(jobTableHeaderOrder.get(TableHeaders.policy)).getText();
				String statusString=columns.get(jobTableHeaderOrder.get(TableHeaders.status)).getText();
				String job_typeString=columns.get(jobTableHeaderOrder.get(TableHeaders.job_type)).getText();
				String a=expectedInfo.get(TableHeaders.job_name).toString();
				String b=expectedInfo.get(TableHeaders.policy).toString();
				String c=expectedInfo.get(TableHeaders.status).toString().toLowerCase();
				String d=expectedInfo.get(TableHeaders.job_type).toString();
				assertTrue(columns.get(jobTableHeaderOrder.get(TableHeaders.job_name)).getText().equalsIgnoreCase(expectedInfo.get(TableHeaders.job_name).toString()));
				assertTrue(columns.get(jobTableHeaderOrder.get(TableHeaders.policy)).getText().equalsIgnoreCase(expectedInfo.get(TableHeaders.policy).toString()));
				assertTrue(columns.get(jobTableHeaderOrder.get(TableHeaders.status)).getText().toLowerCase().contains(expectedInfo.get(TableHeaders.status).toString().toLowerCase()));
				assertTrue(columns.get(jobTableHeaderOrder.get(TableHeaders.job_type)).getText().equalsIgnoreCase(expectedInfo.get(TableHeaders.job_type).toString()));
				break;
			}

			if (i == rowCount - 1) {
				assertTrue(false, "job with name: " + expectedInfo.get(TableHeaders.name) + " not matched");
			}
		}
	}
	
    public String getColumnValueFromFirstRow(String columnName,  ExtentTest test) {
    	String expectedValue=null;
		ColumnPage columnPage = new ColumnPage();
		if(columnPage.checkColumnExisted(columnName)) {

			expectedValue = columnPage.getValueInSpecifiedColumnFromFirstRow(columnName);			

		}else {
			assertFalse(true, "column with name:"+ columnName+" does not exist.");
		}
		return expectedValue;
	}
    
    public void clickRecoveryButton(WebElement row) {
    	System.out.println(row.getAttribute("class"));
    	System.out.println(row.getAttribute("role"));
    	WebElement drogButton1 = row.findElement(By.xpath(".//div[@class='rt-td no-ellipsis']"));
    	WebElement drogButton2 = drogButton1.findElement(By.xpath(".//div[@class='dropdown btn-group']"));
    	WebElement drogButton = row.findElement(By.xpath(".//button[@class='no-text dropdown-toggle btn btn-default']"));
    	drogButton.click();
    	WebElement startRecoveryButton = row.findElement(By.xpath(".//div[@class='dropdown open btn-group']//a[@role='menuitem']//span[text()='Recover']"));
    	startRecoveryButton.click();
    }
    
    public void clickRecoveryFromFristRow(ExtentTest test) {
    	WebElement row=getRow();
    	clickRecoveryButton(row);
    }
    
    public void clickRecoveryByColumnValue(String columnName,String expectedValue,ExtentTest test) {
    	WebElement row=getRowWebElementByColumeValue(columnName,expectedValue);
    	clickRecoveryButton(row);
    }
    
    public void clickBrowseRecoveryPoints() {
    	WebElement browseRecoveryPoints = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_RECOVERYWIZARD_BROWSERECOVERYPOINT);
    	browseRecoveryPoints.click();
    	waitForMilSeconds(10000);
    }
    
    public void doubleClickRestoreImg(String imgName) {
    	//waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_RECOVERYWIZARD_IMGTABLE);
    	WebElement restoreTable = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_RECOVERYWIZARD_IMGTABLE);
    	WebElement restoreImg=restoreTable.findElement(By.xpath(".//div[@class='dojoxGridCellData']//span[text()='C.img']"));
    	restoreImg.click();
    	restoreImg.sendKeys(Keys.ENTER);
    	waitForMilSeconds(10000);
    }
    
    public void selectRestoreItems(String folderPath) {
    	String[] outputArray = folderPath.split(";");
    	WebElement restoreContent=null;
    	WebElement restoreImg=null;
    	for(int i=0;i<outputArray.length;i++) {
    		waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_RECOVERYWIZARD_IMGCONTENT);
        	restoreContent = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_RECOVERYWIZARD_IMGCONTENT);
        	restoreImg=restoreContent.findElement(By.xpath(".//div[@class='dojoxGridCellData']//span[text()='"+outputArray[0]+"']"));
        	restoreImg.click();
        	if(i<(outputArray.length-1)) {
        		restoreImg.sendKeys(Keys.ENTER);
        	}        	
        	waitForMilSeconds(10000);
    	}    	
    }
    
    public void clickRestoreUsingDirectAgentButton() {    	
    	WebElement restoreUsingDirectAgentButton = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_RECOVERYWIZARD_RESTOREUSINGCLOUDDIRECTAGENT);
    	restoreUsingDirectAgentButton.click();
    }
    
    public void setRecoverDestination(String destPath,ExtentTest test) {
    	waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_RECOVERYWIZARD_DESTINATIONPATH);
    	WebElement recoverDest = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_RECOVERYWIZARD_DESTINATIONPATH);
    	System.out.println("destination is:"+recoverDest.getText());
    	recoverDest.clear();
    	
    	recoverDest.sendKeys(destPath);
    	recoverDest.clear();
    	recoverDest.sendKeys(destPath);
    }
    
    public void clickNextInRecoverWizard(ExtentTest test) {
    	waitForMilSeconds(5000);
    	WebElement nextBtn=ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_RECOVERYWIZARD_NEXTBUTTON);
    	nextBtn.click();
    }
    
    public void clickCancelInRecoverWizard(ExtentTest test) {
    	WebElement cancelBtn=ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_RECOVERYWIZARD_CANCELBUTTON);
    	cancelBtn.click();
    }
    
    public void clickRestoreToOriginalInRecoverWizard(ExtentTest test) {
    	waitForMilSeconds(2000);
    	WebElement restoreToOriginalRadio=ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_RECOVERYWIZARD_RECOVERONTHEORIGINALSOURCEMACHINE);
    	restoreToOriginalRadio.click();
    }
    
    public void clickRestoreToAlterInRecoverWizard(String sourceName,ExtentTest test) {
    	WebElement restoreToAlterRadio=ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_RECOVERYWIZARD_RECOVERONTOANOTHERMACHINE);
    	restoreToAlterRadio.click();
    	searchByStringWithoutCheck(sourceName, test);    	
    }
    
    public void selectRestoreAlterMachineInRecoverWizard(String sourceName,ExtentTest test) {
    	WebElement searchedRow=getRow();
    	WebElement searchedRadio = searchedRow.findElement(By.xpath(".//input[@type='radio']"));
    	searchedRadio.click();
    }
    
    public void clickRestoreInRecoverWizard(ExtentTest test) {
    	WebElement restoreBtn=ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_RECOVERYWIZARD_RESTOREBUTTON);
    	restoreBtn.click();
    }
}
