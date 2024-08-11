package ui.spog.pages.protect;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.SourceColumnConstants;
import genericutil.ErrorHandler;
import ui.base.common.BasePage;
import ui.base.common.SPOGMenuTreePath;
import ui.base.common.TableHeaders;
import ui.base.common.Url;
import ui.base.elements.Button;
import ui.base.elements.Link;
import ui.base.factory.BrowserFactory;
import ui.base.factory.ElementFactory;

public class SourcePage extends BasePage {

	private ErrorHandler errorHandle = BasePage.getErrorHandler();

	public boolean checkSourceExists(String sourceName) {
		boolean isExist = false;
		try {
			waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_TABLE_ROWS);
			List<WebElement> rows = getRows();
			System.out.println(rows.size());
			WebElement tableBody = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_TABLE_ROWS);
			//WebElement customerElement = tableBody.findElement(By.xpath(".//span[text()='" + sourceName + "']"));
			WebElement customerElement1 = rows.get(0).findElement(By.xpath(".//div[@class='rt-tr ']"));
			WebElement customerElement2 = rows.get(0).findElement(By.xpath(".//div[@class='rt-tr ']//div[@class='rt-td active-sort']"));
			WebElement customerElement3 = rows.get(0).findElement(By.xpath(".//div[@class='rt-tr ']//div[@class='rt-td active-sort']//a[text()='" + sourceName + "']"));
			WebElement customerElement = rows.get(0).findElement(By.xpath(".//a[text()='" + sourceName + "']"));
			assertEquals(customerElement.getText(), sourceName);

			isExist = true;
		} catch (NoSuchElementException e) {
			errorHandle.printErrorMessageInDebugFile("Source with name: " + sourceName + " does not exist");
		}

		return isExist;
	}

	public void checkSourceDetailsInTable(ArrayList<String> sourceName,
			ArrayList<HashMap<String, Object>> expectedInfos, ExtentTest test) {

		HashMap<String, Integer> sourceTableHeaderOrder = getTableHeaderOrder(getTableHeaders());

		List<WebElement> rows = getRows();

		if (rows.size() == 0) {
			test.log(LogStatus.WARNING, "no rows found");
		}

		if (expectedInfos.size() >= rows.size()) {
			for (int i = 0; i < rows.size(); i++) {
				WebElement eachRow = rows.get(i);

				String xpath = "." + ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_TABLE_COLUMNS)
						.get(0).getValue();
				List<WebElement> columns = eachRow.findElements(By.xpath(xpath));

				for (int j = 0; j < expectedInfos.size(); j++) {
					HashMap<String, Object> expectedInfo = expectedInfos.get(j);
					if (columns.get(sourceTableHeaderOrder.get(TableHeaders.name)).getText()
							.equalsIgnoreCase(expectedInfo.get(TableHeaders.name).toString())) {

						System.out.println(columns.get(sourceTableHeaderOrder.get(TableHeaders.connection)).getText()
								+ " : " + (expectedInfo.get(TableHeaders.connection).toString()));
						System.out.println(columns.get(sourceTableHeaderOrder.get(TableHeaders.latest_recovery_point))
								.getText().contains(expectedInfo.get(TableHeaders.latest_recovery_point).toString()));
						System.out.println(columns.get(sourceTableHeaderOrder.get(TableHeaders.latest_job)).getText()
								.contains(expectedInfo.get(TableHeaders.latest_job).toString()));
						System.out.println(columns.get(sourceTableHeaderOrder.get(TableHeaders.policy)).getText()
								+ " : " + (expectedInfo.get(TableHeaders.policy).toString()));
						System.out.println(columns.get(sourceTableHeaderOrder.get(TableHeaders.source_group)).getText()
								+ " : " + (expectedInfo.get(TableHeaders.source_group).toString()));

						// assertTrue(columns.get(sourceTableHeaderOrder.get(TableHeaders.type)).getText().equalsIgnoreCase(expectedInfo.get(TableHeaders.type).toString()));
						assertTrue(columns.get(sourceTableHeaderOrder.get(TableHeaders.name)).getText()
								.equalsIgnoreCase(expectedInfo.get(TableHeaders.name).toString()));
						// assertTrue(columns.get(sourceTableHeaderOrder.get(TableHeaders.os)).getText().equalsIgnoreCase(expectedInfo.get(TableHeaders.os).toString()));
						// assertTrue(columns.get(sourceTableHeaderOrder.get(TableHeaders.status)).getText().equalsIgnoreCase(expectedInfo.get(TableHeaders.status).toString()));
						// assertTrue(columns.get(sourceTableHeaderOrder.get(TableHeaders.connection)).getText().equalsIgnoreCase(expectedInfo.get(TableHeaders.connection).toString()));
						assertTrue(columns.get(sourceTableHeaderOrder.get(TableHeaders.latest_recovery_point)).getText()
								.contains(expectedInfo.get(TableHeaders.latest_recovery_point).toString()));
						assertTrue(columns.get(sourceTableHeaderOrder.get(TableHeaders.latest_job)).getText()
								.contains(expectedInfo.get(TableHeaders.latest_job).toString()));
						assertTrue(columns.get(sourceTableHeaderOrder.get(TableHeaders.policy)).getText()
								.equalsIgnoreCase(expectedInfo.get(TableHeaders.policy).toString()));
						assertTrue(columns.get(sourceTableHeaderOrder.get(TableHeaders.source_group)).getText()
								.equalsIgnoreCase(expectedInfo.get(TableHeaders.source_group).toString()));
						assertTrue(columns.get(sourceTableHeaderOrder.get(TableHeaders.vm_name)).getText()
								.equalsIgnoreCase(expectedInfo.get(TableHeaders.vm_name).toString()));
						assertTrue(columns.get(sourceTableHeaderOrder.get(TableHeaders.agent)).getText()
								.equalsIgnoreCase(expectedInfo.get(TableHeaders.agent).toString()));
						assertTrue(columns.get(sourceTableHeaderOrder.get(TableHeaders.hypervisor)).getText()
								.equalsIgnoreCase(expectedInfo.get(TableHeaders.hypervisor).toString()));

						break;
					}

					if (j == expectedInfos.size() - 1) {
						assertTrue(false,
								" source with name: "
										+ columns.get(sourceTableHeaderOrder.get(TableHeaders.name)).getText()
										+ " not matched");
					}

				}
			}
		}
	}

	/**
	 * Verifies the details of each source in table
	 * 
	 * @author Rakesh.Chalamala
	 * @param sourceName
	 * @param expectedInfo
	 * @param test
	 */
	public void checkSourceDetailsInTable(String sourceName, HashMap<String, Object> expectedInfo, ExtentTest test) {

		HashMap<String, Integer> sourceTableHeaderOrder = getTableHeaderOrder(getTableHeaders());

		List<WebElement> rows = getRows();

		if (rows.size() == 0 && expectedInfo != null && !expectedInfo.isEmpty()) {
			test.log(LogStatus.WARNING, "no rows found");
			assertTrue(false);
		}

		for (int i = 0; i < rows.size(); i++) {
			WebElement eachRow = rows.get(i);

			String xpath = "." + ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_TABLE_COLUMNS).get(0)
					.getValue();
			List<WebElement> columns = eachRow.findElements(By.xpath(xpath));

			if (columns.get(sourceTableHeaderOrder.get(TableHeaders.name)).getText()
					.equalsIgnoreCase(expectedInfo.get(TableHeaders.name).toString())) {

				// assertTrue(columns.get(sourceTableHeaderOrder.get(TableHeaders.type)).getText().equalsIgnoreCase(expectedInfo.get(TableHeaders.type).toString()));
				assertTrue(columns.get(sourceTableHeaderOrder.get(TableHeaders.name)).getText()
						.equalsIgnoreCase(expectedInfo.get(TableHeaders.name).toString()));
				// assertTrue(columns.get(sourceTableHeaderOrder.get(TableHeaders.os)).getText().equalsIgnoreCase(expectedInfo.get(TableHeaders.os).toString()));
				// assertTrue(columns.get(sourceTableHeaderOrder.get(TableHeaders.status)).getText().equalsIgnoreCase(expectedInfo.get(TableHeaders.status).toString()));
				// assertTrue(columns.get(sourceTableHeaderOrder.get(TableHeaders.connection)).getText().equalsIgnoreCase(expectedInfo.get(TableHeaders.connection).toString()));
				assertTrue(columns.get(sourceTableHeaderOrder.get(TableHeaders.latest_recovery_point)).getText()
						.contains(expectedInfo.get(TableHeaders.latest_recovery_point).toString()));
				assertTrue(columns.get(sourceTableHeaderOrder.get(TableHeaders.latest_job)).getText().toLowerCase()
						.contains(expectedInfo.get(TableHeaders.latest_job).toString().toLowerCase()));
				assertTrue(columns.get(sourceTableHeaderOrder.get(TableHeaders.policy)).getText()
						.equalsIgnoreCase(expectedInfo.get(TableHeaders.policy).toString()));
				assertTrue(columns.get(sourceTableHeaderOrder.get(TableHeaders.source_group)).getText()
						.equalsIgnoreCase(expectedInfo.get(TableHeaders.source_group).toString()));
				assertTrue(columns.get(sourceTableHeaderOrder.get(TableHeaders.vm_name)).getText()
						.equalsIgnoreCase(expectedInfo.get(TableHeaders.vm_name).toString()));
				assertTrue(columns.get(sourceTableHeaderOrder.get(TableHeaders.agent)).getText()
						.equalsIgnoreCase(expectedInfo.get(TableHeaders.agent).toString()));
				assertTrue(columns.get(sourceTableHeaderOrder.get(TableHeaders.hypervisor)).getText()
						.equalsIgnoreCase(expectedInfo.get(TableHeaders.hypervisor).toString()));

				break;
			}

			if (i == rows.size() - 1) {
				assertTrue(false, " source with name: " + expectedInfo.get(TableHeaders.name) + " not matched");
			}
		}
	}

	public void clickDownloadAgent() {

		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_PROTECT_SOURCE_DOWNLOADAGENT,
				BrowserFactory.getMaxTimeWaitUIElement());
		Button btn = new Button(SPOGMenuTreePath.SPOG_PROTECT_SOURCE_DOWNLOADAGENT);
		btn.click();
	}

	public void selectAgentTypeAndDownload(String osType, String systemType, ExtentTest test) {

		test.log(LogStatus.INFO, "wait until the title element is visible");
		waitUntilElementAppear(SPOGMenuTreePath.SPOG_PROTECT_SOURCE_DOWNLOADAGENT_TITLE);

		WebElement formEle = ElementFactory.getElement(SPOGMenuTreePath.SPOG_PROTECT_SOURCE_DOWNLOADAGENT_FORM);

		try {

			test.log(LogStatus.INFO, "Click on the os type");
			formEle.findElement(By.xpath(".//*[text()='" + osType + "']")).click();
			;

			test.log(LogStatus.INFO, "Click on the system type");
			formEle.findElement(By.xpath(".//*[text()='" + systemType + "']")).click();
			;

			test.log(LogStatus.INFO, "Click on download agent");
			ElementFactory.getElement(SPOGMenuTreePath.SPOG_PROTECT_SOURCE_DOWNLOADAGENT_AGENT).click();

		} catch (NoSuchElementException e) {
			closeModalDialog();
			assertTrue(false, "Not found:" + osType + ", " + systemType);
		}
	}

	public void clickDownloadOVA(ExtentTest test) {

		test.log(LogStatus.INFO, "wait until the title element is visible");
		waitUntilElementAppear(SPOGMenuTreePath.SPOG_PROTECT_SOURCE_DOWNLOADAGENT_TITLE);

		test.log(LogStatus.INFO, "Click on download OVA");
		ElementFactory.getElement(SPOGMenuTreePath.SPOG_PROTECT_SOURCE_DOWNLOADAGENT_OVA).click();

	}

	public void clickAddSources() {

		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_PROTECT_SOURCE_ADDSOURCES,
				BrowserFactory.getMaxTimeWaitUIElement());
		Button btn = new Button(SPOGMenuTreePath.SPOG_PROTECT_SOURCE_ADDSOURCES);
		btn.click();

		waitUntilElementAppear(SPOGMenuTreePath.SPOG_PROTECT_SOURCE_SELECTHYPERVISORLABEL);
	}

	/**
	 * checks the file is downloaded in specified path and deletes the file
	 * 
	 * @param downloadPath
	 * @param fileName
	 * @return
	 */
	public boolean isFileDownloaded(String downloadPath, String fileName, String systemType) {
		File dir = new File(downloadPath);
		File[] dirContents = dir.listFiles();

		for (int i = 0; i < dirContents.length; i++) {
			if (dirContents[i].getName().contains(fileName) && dirContents[i].getName().contains(systemType)) {
				/*
				 * long fileSize = dirContents[i].getTotalSpace(); int sizeInMB =
				 * (int)((((fileSize/8)/1000)/1000)/1000); System.out.println(sizeInMB);
				 * assertTrue(sizeInMB, 9);
				 */

				// File has been found, it can now be deleted:
				dirContents[i].delete();
				return true;
			}
		}
		return false;
	}
	
	public void clickDeleteSource() {
		
		waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_DIALOGTITLE);
		String title = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_DIALOGTITLE).getText();
		
		assertTrue(title.equalsIgnoreCase("Delete Source"));
		
		ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_DELETE).click();
		waitForSeconds(1);		
	}
	
    public void clickStartBackup() {
		
    	WebElement tableBody = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_TABLE_ROWS);
    	WebElement drogButton = tableBody.findElement(By.xpath(".//div[@class='dropdown btn-group']//button[@role='button']//span[@class='caret']"));
    	drogButton.click();
    	WebElement startBackupButton = tableBody.findElement(By.xpath(".//a[@role='menuitem']//span[text()='Start Backup']"));
    	startBackupButton.click();
		clickConfrim();
			
	}
    
    public void clickStartRecovery() {
		
    	WebElement tableBody = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_TABLE_ROWS);
    	WebElement drogButton = tableBody.findElement(By.xpath(".//div[@class='dropdown btn-group']//button[@role='button']//span[@class='caret']"));
    	drogButton.click();
    	WebElement startRecoveryButton = tableBody.findElement(By.xpath(".//a[@role='menuitem']//span[text()='Start Recovery']"));
    	startRecoveryButton.click();
    	waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_RECOVERYPOINTS);
			
	}
    
    
    
    public String getLatestRecoveryPointJobTimeFromLatestJobColume() {
    	WebElement colEle = getColumnWebElementByColumeValue(SourceColumnConstants.SOURCE_LATEST_RECOVERY_POINT);
    	try {
    		WebElement latestJobColumeContent = colEle.findElement(By.xpath(".//div//span"));
    		return latestJobColumeContent.getText().toString();  		
    	} catch (NoSuchElementException e) {
			errorHandle.printErrorMessageInDebugFile("The source has no latest job");
		}
    	return null;
    }
    
    public String getLastJobStatusFromLatestJobColume() {
    	String ret=null;
    	WebElement tableBody = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_TABLE_ROWS);
    	try {
    		WebElement latestJobColumeContent = tableBody.findElement(By.xpath(".//div[@class='multi-line-text']"));
    		List<WebElement> latestJobItems = latestJobColumeContent.findElements(By.xpath(".//div//span"));
        	if (latestJobItems.size() == 0) {
    			System.out.println("row in getLastJobStatusFromLatestJobColume is 0");
        		return ret;
    		}else {
    			ret=latestJobItems.get(0).getText().toString();
    		}    		
    	} catch (NoSuchElementException e) {
    		WebElement latestJobColumeContentWhenInProgress = tableBody.findElement(By.xpath(".//div[@class='latest-jb-progress-bar']"));
    		List<WebElement> latestJobItemsWhenInProgress = latestJobColumeContentWhenInProgress.findElements(By.xpath(".//span[@class='progress-content']"));
    		if (latestJobItemsWhenInProgress.size() == 0) {
    			System.out.println("row in getLastJobStatusFromLatestJobColume is 0");
        		return ret;
    		}else {
    			ret=latestJobItemsWhenInProgress.get(0).getText().toString();
    		} 
		}
    	return ret;
    }
    
    public String getLastJobTypeFromLatestJobColume() {
    	String ret=null;
    	WebElement tableBody = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_TABLE_ROWS);
    	try {
    		WebElement latestJobColumeContent = tableBody.findElement(By.xpath(".//div[@class='multi-line-text']"));
    		List<WebElement> latestJobItems = latestJobColumeContent.findElements(By.xpath(".//a//span"));
        	if (latestJobItems.size() == 0) {
    			System.out.println("row in getLastJobTypeFromLatestJobColume is 0");
        		return ret;
    		}else {
    			ret=latestJobItems.get(0).getText().toString();
    		}    		
    	} catch (NoSuchElementException e) {
    		WebElement latestJobColumeContentWhenInProgress = tableBody.findElement(By.xpath(".//div[@class='latest-jb-progress-bar']"));
    		List<WebElement> latestJobItemsWhenInProgress = latestJobColumeContentWhenInProgress.findElements(By.xpath(".//a//span"));
    		if (latestJobItemsWhenInProgress.size() == 0) {
    			System.out.println("row in getLastJobTypeFromLatestJobColume is 0");
        		return ret;
    		}else {
    			ret=latestJobItemsWhenInProgress.get(0).getText().toString();
    		} 
		}
    	return ret;
    }
    
    public void checkLastJobStatusFromLatestJobColume(String expectedJobStatus) {
    	String ret_job_status=getLastJobStatusFromLatestJobColume();
    	System.out.println("ret job status from source page:"+ret_job_status);
    	if(ret_job_status.toLowerCase().indexOf(expectedJobStatus.toLowerCase())!=-1) {
    		assertTrue(true,"check job status from latest job column");
    	}else {
    		//assertTrue(false,"check job status from latest job column");
    	}
    }
    
    public void checkLastJobTypeFromLatestJobColume(String expectedJobType) {
    	String ret_job_type=getLastJobTypeFromLatestJobColume();
    	System.out.println("ret job type from source page:"+ret_job_type);
    	assertTrue(ret_job_type.toLowerCase().contains(expectedJobType.toLowerCase()),"check job type from latest job column");
    }
    
    public void waitUntilJobUpdatedFromLatestJobColume(String job_type,String lastJobTime,String expectedJobStatus,String contentFromLatestJobColume,int waitMinutes) {
    	String ret=null;
    	WebElement tableBody = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_TABLE_ROWS);
    	try {
	    	WebElement latestJobColumeContent = tableBody.findElement(By.xpath(".//div[@class='rt-td']//span[text()='"+contentFromLatestJobColume+"']"));
	    	latestJobColumeContent.click();
    	
	    	WebElement latestJobPopoverContent = ElementFactory.getElement(SPOGMenuTreePath.SPOG_PROTECT_SOURCE_LATESTJOBPOPOVERCONTENT);
	    	List<WebElement> latestJobItems = latestJobPopoverContent.findElements(By.xpath(".//div[@class='latest-job-popver-text']"));
	    	for(int waitTimes=0;waitTimes<20*waitMinutes;waitTimes++) {
	    		if (latestJobItems.size() == 0) {
	    			waitForMilSeconds(3000);
	    		}else {
	    			for (int i = 0; i < latestJobItems.size(); i++) {
	    				WebElement eachJob = latestJobItems.get(i);
	    				WebElement jobType = eachJob.findElement(By.xpath(".//a"));
	    				if(jobType.getText().toString().toLowerCase().indexOf(job_type.toLowerCase())>0) {
	    					ret=eachJob.findElement(By.xpath(".//div")).getText().toString();
	    					if(!ret.equalsIgnoreCase(lastJobTime)) {
	    						String retStatus=eachJob.findElement(By.xpath(".//div//span")).getText().toString();
	    						if(retStatus.equalsIgnoreCase(expectedJobStatus)) {
	    							assertTrue(true,"Job status is right under latest job colume in source page");
	    						}else {
	    							assertTrue(false,"Job status is right under latest job colume in source page");
	    						}
	    						latestJobColumeContent.click();
	    						return;
	    					}else {
	    						waitForMilSeconds(3000);
	    						break;
	    					}
	    				}
	    			}
	    		}
	    		latestJobColumeContent.click();
	    		latestJobColumeContent.click();
	    	}
	    	assertTrue(false,"Job status is right under latest job colume in source page");
    	}catch (NoSuchElementException e) {
    		checkLastJobStatusFromLatestJobColume(expectedJobStatus);
    		checkLastJobTypeFromLatestJobColume(job_type);
    	}
    }
    
    public void checkJobStatusFromLatestJobColume(String job_type,String job_status,String last_job_time,String contentFromLatestJobColume) {
    	String retStatus;
    	String currenJobTime;
    	WebElement tableBody = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_TABLE_ROWS);
    	WebElement latestJobColumeContent = tableBody.findElement(By.xpath(".//div[@class='rt-td']//span[text()='"+contentFromLatestJobColume+"']"));
    	latestJobColumeContent.click();
    	WebElement latestJobPopoverContent = ElementFactory.getElement(SPOGMenuTreePath.SPOG_PROTECT_SOURCE_LATESTJOBPOPOVERCONTENT);
    	List<WebElement> latestJobItems = latestJobPopoverContent.findElements(By.xpath(".//div[@class='latest-job-popver-text']"));
    	if (latestJobItems.size() == 0) {
    		assertTrue(false,"No job under latest job colume in source page");
		}else {
			for (int i = 0; i < latestJobItems.size(); i++) {
				WebElement eachJob = latestJobItems.get(i);
				WebElement jobType = eachJob.findElement(By.xpath(".//a"));
				if(jobType.getText().toString().toLowerCase().indexOf(job_type.toLowerCase())>0) {
					retStatus=eachJob.findElement(By.xpath(".//div//span")).getText().toString();
					currenJobTime=eachJob.findElement(By.xpath(".//div")).getText().toString();
					if(retStatus.equalsIgnoreCase(job_status)||currenJobTime.equalsIgnoreCase(last_job_time)) {
						assertTrue(true,"Job status is right under latest job colume in source page");
					}else {
						assertTrue(false,"Job status is right under latest job colume in source page");
					}
					break;
				}
			}
		}
    }
	
	public void clickCancel() {
		
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_COMMON_CANCEL, BrowserFactory.getMaxTimeWaitUIElement());
		ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_CANCEL).click();
		
		waitForSeconds(2);
	}
	
    public void clickConfrim() {
		
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_COMMON_CONFIRM, BrowserFactory.getMaxTimeWaitUIElement());
		ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_CONFIRM).click();
		
		waitForSeconds(2);
	}
	
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

}
