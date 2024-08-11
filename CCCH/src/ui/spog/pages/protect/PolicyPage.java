package ui.spog.pages.protect;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.UIConstants;
import genericutil.ErrorHandler;
import ui.base.common.BasePage;
import ui.base.common.SPOGMenuTreePath;
import ui.base.elements.Button;
import ui.base.elements.CheckBox;
import ui.base.elements.Link;
import ui.base.elements.TextField;
import ui.base.factory.ElementFactory;
import ui.spog.server.SPOGUIServer;

public class PolicyPage extends BasePage {

	private ErrorHandler errorHandle = BasePage.getErrorHandler();
	private SPOGUIServer spogUIServer;

	public void clickAddPolicy() {

		errorHandle.printInfoMessageInDebugFile("click add policy button");
		Button addPolicyBtn = new Button(SPOGMenuTreePath.SPOG_PROTECT_POLICY_ADDPOLICY);
		addPolicyBtn.click();

		errorHandle.printInfoMessageInDebugFile("wait policy name textbox apppears");
		waitUntilElementAppear(SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_BASICS_POLICYNAME);

	}

	public void editBasicInfo(String policyName, String policyType, String policyDescription) {

		Link basicEle = new Link(SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_BASICS);
		basicEle.click();

		errorHandle.printInfoMessageInDebugFile("set policy name");
		TextField policyNameEle = new TextField(SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_BASICS_POLICYNAME);
		policyNameEle.clear();
		policyNameEle.setText(policyName);

		errorHandle.printInfoMessageInDebugFile("click policy type");
		if (policyType.equalsIgnoreCase(UIConstants.POLICY_BAAS)) {
			waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_BASICS_BAASPOLICY, 10);
			WebElement baasEle = ElementFactory
					.getElement(SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_BASICS_BAASPOLICY);
			baasEle.click();
		} else if (policyType.equalsIgnoreCase(UIConstants.POLICY_DRAAS)) {
			waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_BASICS_DRAASPOLICY, 10);
			WebElement draasEle = ElementFactory
					.getElement(SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_BASICS_DRAASPOLICY);
			draasEle.click();
		} else {
			WebElement repEle = ElementFactory
					.getElement(SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_BASICS_REPLICATIONPOLICY);
			repEle.click();
		}
		if ((policyDescription != null) && (policyDescription.equals(""))) {
			TextField descriptionEle = new TextField(
					SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_BASICS_DESCRIPTION);
			descriptionEle.clear();
			descriptionEle.setText(policyDescription);

		}

	}

	public void editDestinationWhatToProtectInfo4BaasPolicy(String activeType, String activeOption, String drives,
			String paths) {

		Link destinationEle = new Link(SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION);
		destinationEle.click();

		this.waitForSeconds(2);
		WebElement activeTypeEle = ElementFactory
				.getElement(SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHATTOPROTECT_ACTIVETYPE);
		activeTypeEle.click();
		if (activeType.equalsIgnoreCase(UIConstants.ACTIVE_TYPE_IMAGE)) {
			this.waitForSeconds(2);
			WebElement imageEle = ElementFactory
					.getElement(SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHATTOPROTECT_WINDOWSIMAGE);
			imageEle.click();

			if (activeOption.equalsIgnoreCase(UIConstants.ACTIVE_OPTION_FULLSYSTEM)) {
				WebElement fullSystemEle = ElementFactory.getElement(
						SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHATTOPROTECT_WINDOWSIMAGE_FULLSYSTEM);
				fullSystemEle.click();
			} else {
				WebElement selectDriveEle = ElementFactory.getElement(
						SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHATTOPROTECT_WINDOWSIMAGE_SELECTDRIVE);
				selectDriveEle.click();

				// clear flag for drives. this is need for modify policy.

				// choose drive
				if ((drives != null) && (!drives.equalsIgnoreCase(""))) {
					String[] drivesArray = drives.split(";");
					for (int i = 0; i < drivesArray.length; i++) {
						WebElement containerEle = ElementFactory.getElement(
								SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHATTOPROTECT_WINDOWSIMAGE_SELECTDRIVE_DRIVECONTAINER);
						WebElement driveElement = containerEle
								.findElement(By.xpath(".//label[@for='" + drivesArray[i] + "']"));
						driveElement.click();
					}
				}
			}

		} else {
			this.waitForSeconds(2);
			WebElement backupFileEle = ElementFactory
					.getElement(SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHATTOPROTECT_BACKUPFILE);
			backupFileEle.click();

			// remove old setting

			// add new setting
			String[] path = paths.split(";");
			for (int i = 0; i < path.length; i++) {
				if (i != 0) {
					WebElement addEle = ElementFactory.getElement(
							SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHATTOPROTECT_BACKUPFILEADD);
					addEle.click();
				}

				List<WebElement> lineEles = ElementFactory.getElements(
						SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHATTOPROTECT_PATHLINE);
				WebElement lineEle = lineEles.get(i);
				WebElement pathEle = lineEle.findElement(
						By.xpath(".//input[@id='destinations[0].cloud_direct_file_backup.path[" + i + "]']"));
				pathEle.sendKeys(path[i]);
			}

			// need to password
		}

	}

	public void editDestinationWhereToProtect(String activeType, String destVolumeName, String enableLocal,
			String localDestination) {
		// where to protect
		WebElement whereEle = ElementFactory
				.getElement(SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHERETOPROTECT);
		whereEle.click();
		this.waitForSeconds(2);

		WebElement destEle = ElementFactory
				.getElement(SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHERETOPROTECT_DESTINATION);
		destEle.click();

		WebElement destVolumeEle = ElementFactory.getElementByXpath(".//a[text()='" + destVolumeName + "']");
		destVolumeEle.click();

		if (activeType.equalsIgnoreCase(UIConstants.ACTIVE_TYPE_IMAGE)) {
			WebElement enableLocalBackupEle = ElementFactory.getElement(
					SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHERETOPROTECT_CREATELOCALBACKUP4IMAGE);
			String value = enableLocalBackupEle.getAttribute("value");
			if (!value.equalsIgnoreCase(enableLocal)) {
				ElementFactory.getElement(
						SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHERETOPROTECT_CREATELOCALBACKUPLABEL4IMAGE)
						.click();
			}

			if (enableLocal.equalsIgnoreCase("true") && (localDestination != null)) {
				TextField localDestEle = new TextField(
						SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHERETOPROTECT_LOCALBACKUPPATH4IMAGE);
				localDestEle.clear();
				localDestEle.setText(localDestination);
			}

		} else {

			WebElement enableLocalBackupEle = ElementFactory.getElement(
					SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHERETOPROTECT_CREATELOCALBACKUP4FILE);
			String value = enableLocalBackupEle.getAttribute("value");
			if (!value.equalsIgnoreCase(enableLocal)) {
				ElementFactory.getElement(
						SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHERETOPROTECT_CREATELOCALBACKUPLABEL4FILE)
						.click();
			}

			if (enableLocal.equalsIgnoreCase("true") && (localDestination != null)) {
				TextField localDestEle = new TextField(
						SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHERETOPROTECT_LOCALBACKUPPATH4FILE);
				localDestEle.clear();
				localDestEle.setText(localDestination);
			}

		}

	}

	public void editDestinationWhenToProtect(String policyType, String backupSchedule, String hour, String minute,
			String clock, ArrayList<HashMap<String, String>> throttleScheduleArray) {

		WebElement whenEle = ElementFactory
				.getElement(SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHENTOPROTECT);
		whenEle.click();
		waitForSeconds(2);

		if (policyType.equalsIgnoreCase(UIConstants.POLICY_DRAAS)) {
			WebElement scheduleEle = ElementFactory.getElement(
					SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHENTOPROTECT_BACKUPSCHEDULE);
			scheduleEle.click();
			scheduleEle.findElement(By.xpath(".//following-sibling::ul//span[text()='" + backupSchedule + "']"))
					.click();

		}

		if ((backupSchedule == null) || backupSchedule.equalsIgnoreCase(UIConstants.BACKUP_EVERY_6_HOURS)
				|| backupSchedule.equalsIgnoreCase(UIConstants.BACKUP_EVERY_1_DAY)) {
			WebElement hourEle = ElementFactory.getElement(
					SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHENTOPROTECT_STARTTIMEHOUR);
			hourEle.click();
			hourEle.findElement(By.xpath(".//following-sibling::ul//a[text()='" + hour + "']")).click();
		}
		WebElement minuteEle = ElementFactory
				.getElement(SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHENTOPROTECT_STARTTIMEMINUTE);
		minuteEle.click();
		minuteEle.findElement(By.xpath(".//following-sibling::ul//a[text()='" + minute + "']")).click();

		/*
		 * if ((backupSchedule == null) ||
		 * backupSchedule.equalsIgnoreCase(UIConstants.BACKUP_EVERY_1_DAY)) { WebElement
		 * clockEle = ElementFactory.getElement( SPOGMenuTreePath.
		 * SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHENTOPROTECT_STARTTIMECLOCK);
		 * clockEle.click();
		 * clockEle.findElement(By.xpath(".//following-sibling::ul//a[text()='" + clock
		 * + "']")).click(); }
		 */

		if (throttleScheduleArray != null) {
			editDestinationThrottlingSchedule(throttleScheduleArray);
		}

	}

	public void editDestinationThrottlingSchedule(ArrayList<HashMap<String, String>> throttleScheduleArray) {

		// remove the throttling if exist used for modify policy

		int index = 1;
		// add throttling
		for (int i = 0; i < throttleScheduleArray.size(); i++) {
			HashMap<String, String> throttleSchedule = throttleScheduleArray.get(i);
			WebElement addEle = ElementFactory.getElement(
					SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHENTOPROTECT_THROTTLESCHEDULEADD);
			addEle.click();

			// set rate
			String xpath = ElementFactory.getElementAttrFromXML(
					SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHENTOPROTECT_THROTTLESCHEDULELINE)
					.get(0).getValue();
			System.out.println(xpath);
			List<WebElement> lineEles = ElementFactory
					.getElementsByXpath("//div[@class='destinationFormOverlayContainer']" + xpath);
			WebElement lineEle = lineEles.get(i);

			WebElement rateEle = lineEle.findElement(By.xpath(".//input[@id='throttles[" + i + "].rate']"));
			rateEle.clear();
			rateEle.sendKeys(throttleSchedule.get("rate"));

			//
			// set run schedule
			String runSchedule = throttleSchedule.get("schedule");
			String[] schedules = runSchedule.split(";");
			WebElement scheduleEle = lineEle.findElement(
					By.xpath(".//div[@class='Select has-value is-clearable is-searchable Select--multi']"));

			WebElement schedule_input_ele = null;

			try {
				schedule_input_ele = scheduleEle.findElement(By.xpath(".//div[@class='Select-control']"));

			} catch (NoSuchElementException e) {

				System.out.println("Element not found with : " + e);
			}
			// clear the schedule
			WebElement clear_all_ele = schedule_input_ele.findElement(By.xpath(".//span[@class='Select-clear']"));
			clear_all_ele.click();

			// click on dropdown
			WebElement dropdown_ele = schedule_input_ele.findElement(By.xpath(".//span[@class='Select-arrow']"));
			dropdown_ele.click();

			for (int j = 0; j < schedules.length; j++) {

				try {
					String path = ".//div[@class='Select-menu-outer']//*[text()='" + schedules[j] + "']";
					WebElement selectEle = scheduleEle.findElement(By.xpath(path));
					selectEle.click();
				} catch (NoSuchElementException e) {
					System.out.println("Element not found : " + e);
				}
			}
			WebElement startTimeEle = lineEle.findElement(By.xpath(
					"((.//div[@class='time-wrapper-left hoursAndMinutes'])[1]//button[@class='dropdown-toggle btn btn-default'])[1]"));
			startTimeEle.sendKeys(Keys.ENTER);

			String[] starTime = throttleSchedule.get("starTime").split(":");
			String startTimeHour = starTime[0];
			lineEle.findElement(By.xpath(
					"((.//div[@class='time-wrapper-left hoursAndMinutes'])[1]//div[@class='dropdown open btn-group'])[1]//a[@value='"
							+ startTimeHour + "']"))
					.click();

			startTimeEle = lineEle.findElement(By.xpath(
					"((.//div[@class='time-wrapper-left hoursAndMinutes'])[1]//button[@class='dropdown-toggle btn btn-default'])[2]"));
			startTimeEle.sendKeys(Keys.ENTER);
			String startTimeMin = starTime[1];
			lineEle.findElement(By.xpath(
					"(.//div[@class='time-wrapper-left hoursAndMinutes']//div[@class='dropdown open btn-group'])[1]//a[@value='"
							+ startTimeMin + "']"))
					.click();

			/*
			 * index++; WebElement startTimeClockEle = lineEle.findElement( By.
			 * xpath("(//div[@class='time-wrapper-right']//button[@class='dropdown-toggle btn btn-default'])["
			 * + index + "]")); startTimeClockEle.click(); startTimeClockEle
			 * .findElement(By.xpath( ".//following-sibling::ul//a[@value='" +
			 * throttleSchedule.get("startTimeClock") + "']")) .click();
			 */

			// set end time

			WebElement endTimeEle = lineEle.findElement(By.xpath(
					"((.//div[@class='time-wrapper-left hoursAndMinutes'])[2]//button[@class='dropdown-toggle btn btn-default'])[1]"));
			endTimeEle.sendKeys(Keys.ENTER);

			String[] endTime = throttleSchedule.get("endTime").split(":");
			String endTimeHour = endTime[0];
			lineEle.findElement(By.xpath(
					"((.//div[@class='time-wrapper-left hoursAndMinutes'])[2]//div[@class='dropdown open btn-group'])[1]//a[@value='"
							+ endTimeHour + "']"))
					.click();

			endTimeEle = lineEle.findElement(By.xpath(
					"(.//div[@class='time-wrapper-left hoursAndMinutes']//button[@class='dropdown-toggle btn btn-default'])[4]"));
			endTimeEle.sendKeys(Keys.ENTER);
			String endTimeMin = endTime[1];
			lineEle.findElement(By.xpath(
					"((.//div[@class='time-wrapper-left hoursAndMinutes'])[2]//div[@class='dropdown open btn-group'])[1]//a[@value='"
							+ endTimeMin + "']"))
					.click();

			/*
			 * index++; WebElement endTimeClockEle = lineEle.findElement( By.
			 * xpath("(//div[@class='time-wrapper-right']//button[@class='dropdown-toggle btn btn-default'])["
			 * + index + "]")); endTimeClockEle.click(); endTimeClockEle
			 * .findElement(By.xpath( ".//following-sibling::ul//a[@value='" +
			 * throttleSchedule.get("endTimeClock") + "']")) .click();
			 */

		}

	}

	public void clickCreatePolicy() {

		Button createPolicyBtn = new Button(SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_CREATEPOLICYBTN);
		createPolicyBtn.click();
		ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_COMMON_SAVEDSEARCHESLABEL, 10);

	}

	public String getPolicyStatus(String policyName) {

		List<WebElement> headers = getPolicyTableHeaders();
		HashMap<String, Integer> policyTableHeaderOrder = getTableHeaderOrder(headers);
		Set<String> keySet = policyTableHeaderOrder.keySet();
		Iterator<String> keyIter = keySet.iterator();
		int statusOrder = -1;
		int policyNameOrder = -1;
		while (keyIter.hasNext()) {
			String headerName = keyIter.next();
			int order = policyTableHeaderOrder.get(headerName);
			if (headerName.equalsIgnoreCase("policy name")) {
				policyNameOrder = order;
			} else if (headerName.equalsIgnoreCase("status")) {
				statusOrder = order;
			}
		}

		List<WebElement> rows = getPolicyList();
		for (int i = 0; i < rows.size(); i++) {
			WebElement eachRow = rows.get(i);
			String xpath = "." + ElementFactory
					.getElementAttrFromXML(SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYTABLE_COLUMNS).get(0).getValue();
			List<WebElement> columns = eachRow.findElements(By.xpath(xpath));
			if (columns.get(policyNameOrder).getText().equalsIgnoreCase(policyName)) {
				return columns.get(statusOrder).getText();
			}

		}
		return null;
	}

	public List<WebElement> getPolicyTableHeaders() {

		return ElementFactory.getElements(SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYTABLE_HEADERS);

	}

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

	public List<WebElement> getPolicyList() {

		return ElementFactory.getElements(SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYTABLE_ROWS);
	}

	public void addSources(String sourcesName) {

		String[] names = sourcesName.split(";");
		List<WebElement> headers = getPolicyTableHeaders();
		HashMap<String, Integer> policyTableHeaderOrder = getTableHeaderOrder(headers);
		Set<String> keySet = policyTableHeaderOrder.keySet();
		Iterator<String> keyIter = keySet.iterator();
		int sourceNameOrder = -1;
		while (keyIter.hasNext()) {
			String headerName = keyIter.next();
			int order = policyTableHeaderOrder.get(headerName);
			if (headerName.equalsIgnoreCase("Name")) {
				sourceNameOrder = order;
				break;
			}
		}
		List<WebElement> rows = getPolicyList();
		for (int i = 0; i < names.length; i++)
			for (int j = 0; j < rows.size(); j++) {
				WebElement eachRow = rows.get(j);
				String xpath = "."
						+ ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYTABLE_COLUMNS)
								.get(0).getValue();
				List<WebElement> columns = eachRow.findElements(By.xpath(xpath));
				if (columns.get(sourceNameOrder).getText().equalsIgnoreCase(names[i])) {
					columns.get(0).click();

				}
			}

		Button addSourceBtn = new Button(SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_SOURCE_ADDSOURCEBTN);
		addSourceBtn.click();
		waitForSeconds(2);

	}

	public void editSourceInfo(String names) {

		if ((names != null) && (!names.equals(""))) {
			Link sourceEle = new Link(SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_SOURCE);
			waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_SOURCE, 10);
			sourceEle.click();
			waitForSeconds(2);
			Button selectSourceBtn = new Button(SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_SOURCE_SELECTSOURCEBTN);
			selectSourceBtn.click();
			waitForSeconds(10);
			addSources(names);
		}

	}

	public void editAdditionalSetting(String excludeRules) {

		if ((excludeRules != null) && (!excludeRules.equals(""))) {
			WebElement whenEle = ElementFactory
					.getElement(SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_ADDITIONALSETTING);
			whenEle.click();
			waitForSeconds(2);

			String[] excludeRulesArray = excludeRules.split(";");
			for (int i = 0; i < excludeRulesArray.length; i++) {
				String temp = excludeRulesArray[i];
				String type = temp.split("@")[0];
				WebElement addEle = ElementFactory.getElement(
						SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_ADDITIONALSETTING_EXCLUDERULEADD);
				addEle.click();

				List<WebElement> lineEles = ElementFactory.getElements(
						SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_ADDITIONALSETTING_EXCLUDERULELINE);
				WebElement lineEle = lineEles.get(i);

				WebElement typeEle = lineEle.findElement(By.xpath(".//button/span[text()='Select']"));
				typeEle.click();

				lineEle.findElement(By.xpath(".//span[text()='" + type + "']")).click();

				lineEle.findElement(
						By.xpath(".//input[@id='destinations[0].cloud_direct_file_backup.excludes[" + i + "].value']"))
						.sendKeys(temp.split("@")[1]);
				;

			}
		}

	}

	public void editDestinationWhatToProtectInfo4DraasPolicy(String activeOption, String drives) {

		Link destinationEle = new Link(SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION);
		destinationEle.click();

		this.waitForSeconds(2);
		try {
			// test.log(LogStatus.INFO, "Select the activity type for the Cloud Hybrid
			// Replication Policy");
			WebElement select_active_type_ele = ElementFactory
					.getElement(SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHATTOPROTECT_ACTIVETYPE);
			select_active_type_ele.click();

			if (ElementFactory.checkElementExists(
					SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHATTOPROTECT_WINDOWSIMAGE, 3)) {
				ElementFactory
						.getElement(
								SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHATTOPROTECT_WINDOWSIMAGE)
						.click();
			}
		} catch (NoSuchElementException e) {

			System.out.println("Element not found : " + e);
			assertFalse(true, e.getMessage());
		}

		if (activeOption.equalsIgnoreCase(UIConstants.ACTIVE_OPTION_FULLSYSTEM)) {
			WebElement fullSystemEle = ElementFactory.getElement(
					SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHATTOPROTECT_WINDOWSIMAGE_FULLSYSTEM);
			fullSystemEle.click();
		} else {
			WebElement selectDriveEle = ElementFactory.getElement(
					SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHATTOPROTECT_WINDOWSIMAGE_SELECTDRIVE);
			selectDriveEle.click();

			// clear flag for drives. this is need for modify policy.

			// choose drive
			if ((drives != null) && (!drives.equalsIgnoreCase(""))) {
				String[] drivesArray = drives.split(";");
				for (int i = 0; i < drivesArray.length; i++) {
					WebElement containerEle = ElementFactory.getElement(
							SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHATTOPROTECT_WINDOWSIMAGE_SELECTDRIVE_DRIVECONTAINER);
					WebElement driveElement = containerEle
							.findElement(By.xpath(".//label[@for='" + drivesArray[i] + "']"));
					driveElement.click();
				}
			}
		}

	}

	/**
	 * @author Ramya.Nagepalli
	 * @param test
	 * @param expectedScheduleArray
	 */
	public void editDestinationWhenToProtect4ReplicatioFromTask(
			ArrayList<HashMap<String, String>> expectedScheduleArray, ExtentTest test) {
		WebElement whenEle = ElementFactory
				.getElement(SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHENTOPROTECT);
		whenEle.click();
		waitForSeconds(2);
		if (!expectedScheduleArray.equals(null) || !expectedScheduleArray.isEmpty()
				|| !expectedScheduleArray.equals(""))
			editRunSchedule(expectedScheduleArray, test);
	}

	/**
	 * @author Ramya.Nagepalli
	 * @param expectedScheduleArray
	 * @param test
	 */
	public void editRunSchedule(ArrayList<HashMap<String, String>> expectedScheduleArray, ExtentTest test) {

		int index = 1;

		for (int i = 0; i < expectedScheduleArray.size(); i++) {
			HashMap<String, String> throttleSchedule = expectedScheduleArray.get(i);
			WebElement addEle = ElementFactory
					.getElementByXpath("//div[@class='destinationFormOverlayContainer']//span[text()='Add']");
			addEle.click();

			// get number of run schedules
			List<WebElement> lineEles = ElementFactory.getElements(
					SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHENTOPROTECT_SCHEDULELINE);
			WebElement lineEle = lineEles.get(i);

			// set run schedule
			String runSchedule = throttleSchedule.get("schedule");
			String[] schedules = runSchedule.split(";");
			WebElement scheduleEle = lineEle.findElement(
					By.xpath(".//div[@class='Select has-value is-clearable is-searchable Select--multi']"));

			WebElement schedule_input_ele = null;

			try {
				schedule_input_ele = scheduleEle.findElement(By.xpath(".//div[@class='Select-control']"));

			} catch (NoSuchElementException e) {

				System.out.println("Element not found with : " + e);
			}

			// clear the schedule
			WebElement clear_all_ele = schedule_input_ele.findElement(By.xpath(".//span[@class='Select-clear']"));
			clear_all_ele.click();

			// click on dropdown
			WebElement dropdown_ele = schedule_input_ele.findElement(By.xpath(".//span[@class='Select-arrow']"));
			dropdown_ele.click();

			for (int j = 0; j < schedules.length; j++) {

				try {
					String path = ".//div[@class='Select-menu-outer']//*[text()='" + schedules[j] + "']";
					WebElement selectEle = scheduleEle.findElement(By.xpath(path));
					selectEle.click();
				} catch (NoSuchElementException e) {
					System.out.println("Element not found : " + e);
				}
			}

			// set start time hour
			WebElement startTimeEle = lineEle.findElement(By.xpath(
					"((.//div[@class='time-wrapper-left hoursAndMinutes'])[1]//button[@class='dropdown-toggle btn btn-default'])[1]"));
			startTimeEle.sendKeys(Keys.ENTER);

			String[] starTime = throttleSchedule.get("starTime").split(":");
			String startTimeHour = starTime[0];
			lineEle.findElement(By.xpath(
					"((.//div[@class='time-wrapper-left hoursAndMinutes'])[1]//div[@class='dropdown open btn-group'])[1]//a[@value='"
							+ startTimeHour + "']"))
					.click();

			startTimeEle = lineEle.findElement(By.xpath(
					"((.//div[@class='time-wrapper-left hoursAndMinutes'])[1]//button[@class='dropdown-toggle btn btn-default'])[2]"));
			startTimeEle.sendKeys(Keys.ENTER);
			String startTimeMin = starTime[1];
			lineEle.findElement(By.xpath(
					"(.//div[@class='time-wrapper-left hoursAndMinutes']//div[@class='dropdown open btn-group'])[1]//a[@value='"
							+ startTimeMin + "']"))
					.click();

			/*
			 * WebElement startTimeClockEle = lineEle.findElement( By.
			 * xpath("(.//div[@class='time-wrapper-right']//button[@class='dropdown-toggle btn btn-default'])["
			 * + index + "]")); startTimeClockEle.click(); startTimeClockEle
			 * .findElement(By.xpath( ".//following-sibling::ul//a[@value='" +
			 * throttleSchedule.get("startTimeClock") + "']")) .click(); index++;
			 */

			// set end time

			WebElement endTimeEle = lineEle.findElement(By.xpath(
					"((.//div[@class='time-wrapper-left hoursAndMinutes'])[2]//button[@class='dropdown-toggle btn btn-default'])[1]"));
			endTimeEle.sendKeys(Keys.ENTER);

			String[] endTime = throttleSchedule.get("endTime").split(":");
			String endTimeHour = endTime[0];
			lineEle.findElement(By.xpath(
					"((.//div[@class='time-wrapper-left hoursAndMinutes'])[2]//div[@class='dropdown open btn-group'])[1]//a[@value='"
							+ endTimeHour + "']"))
					.click();

			endTimeEle = lineEle.findElement(By.xpath(
					"(.//div[@class='time-wrapper-left hoursAndMinutes']//button[@class='dropdown-toggle btn btn-default'])[4]"));
			endTimeEle.sendKeys(Keys.ENTER);
			String endTimeMin = endTime[1];
			lineEle.findElement(By.xpath(
					"((.//div[@class='time-wrapper-left hoursAndMinutes'])[2]//div[@class='dropdown open btn-group'])[1]//a[@value='"
							+ endTimeMin + "']"))
					.click();

			/*
			 * WebElement endTimeClockEle = lineEle.findElement( By.
			 * xpath("(//div[@class='time-wrapper-right']//button[@class='dropdown-toggle btn btn-default'])["
			 * + index + "]")); endTimeClockEle.click(); endTimeClockEle
			 * .findElement(By.xpath( ".//following-sibling::ul//a[@value='" +
			 * throttleSchedule.get("endTimeClock") + "']")) .click(); index++;
			 */

		}
	}

	/**
	 * editAdditionalSettings4ReplicationFromTask
	 * 
	 * @author Ramya.Nagepalli
	 * @param retentionInputs
	 */
	public void editAdditionalSettings4ReplicationFromTask(HashMap<String, String> retentionInputs, ExtentTest test) {
		test.log(LogStatus.INFO, "set the retention values for Replication from task");

		WebElement additonal_settings_ele = ElementFactory
				.getElement(SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_ADDITIONALSETTING);
		additonal_settings_ele.click();

		try {
			waitUntilElementAppear(
					SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_ADDITIONALSETTING_RETENTIONPOLICY);

			if (retentionInputs.containsKey("daily")
					&& (!retentionInputs.get("daily").equals(null) || !retentionInputs.get("daily").equals(""))) {
				WebElement daily_ele = ElementFactory.getElement(
						SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_ADDITIONALSETTING_DAILYBACKUPSRETENTION);
				waitForSeconds(2);
				daily_ele.clear();
				daily_ele.sendKeys(retentionInputs.get("daily"));
			}

			if (retentionInputs.containsKey("monthly")
					&& (!retentionInputs.get("monthly").equals(null) || !retentionInputs.get("monthly").equals(""))) {
				WebElement monthly_ele = ElementFactory.getElement(
						SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_ADDITIONALSETTING_MONTHLYBACKUPSRETENTION);
				monthly_ele.clear();
				monthly_ele.sendKeys(retentionInputs.get("monthly"));
			}

			if (retentionInputs.containsKey("weekly")
					&& (!retentionInputs.get("weekly").equals(null) || !retentionInputs.get("weekly").equals(""))) {
				WebElement weekly_ele = ElementFactory.getElement(
						SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_ADDITIONALSETTING_WEEKLYBACKUPSRETENTION);
				weekly_ele.clear();
				weekly_ele.sendKeys(retentionInputs.get("weekly"));
			}

			if (retentionInputs.containsKey("manual")
					&& (!retentionInputs.get("manual").equals(null) || !retentionInputs.get("manual").equals(""))) {
				WebElement manual_ele = ElementFactory.getElement(
						SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_ADDITIONALSETTING_MANUALBACKUPSRETENTION);
				manual_ele.clear();
				manual_ele.sendKeys(retentionInputs.get("manual"));
			}

		} catch (NoSuchElementException e) {

			test.log(LogStatus.INFO, "No such element found exception : " + e);
			assertFalse(true);
		}

	}

	/**
	 * addNewDestinationInPolicy
	 * 
	 * @author Ramya.Nagepalli
	 * @param test
	 */

	public void addNewDestinationInPolicy(ExtentTest test) {
		test.log(LogStatus.INFO, "click on destination connector to add another destination block");
		WebElement connector_ele = ElementFactory
				.getElement(SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_DESTINATIONCONNECTOR);
		connector_ele.click();

		test.log(LogStatus.INFO, "select the new destination block to edit configuration");
		WebElement destination_block_ele = ElementFactory
				.getElement(SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_NEWDESTINATIONBLOCK);
		destination_block_ele.click();

	}

	/**
	 * editDestinationWhatToProtectInfo4ReplicationToRemotelyManagedRPSTask
	 * 
	 * @author Ramya.Nagepalli
	 * @param test
	 */
	public void editDestinationWhatToProtectInfo4ReplicationToRemotelyManagedRPSTask(ExtentTest test) {

		test.log(LogStatus.INFO, "navigate to Destinations tab to set the policy configuration");
		WebElement destinationEle = ElementFactory
				.getElement(SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION);
		destinationEle.click();

		this.waitForSeconds(2);
		try {
			test.log(LogStatus.INFO, "Select the activity type for the Cloud Hybrid Replication Policy");
			WebElement select_active_type_ele = ElementFactory
					.getElement(SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHATTOPROTECT_ACTIVETYPE);
			select_active_type_ele.click();

			if (ElementFactory.checkElementExists(
					SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHATTOPROTECT_REPLICATETO, 3)) {
				ElementFactory
						.getElement(
								SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHATTOPROTECT_REPLICATETO)
						.click();
			}
		} catch (NoSuchElementException e) {

			System.out.println("Element not found : " + e);
			assertFalse(true, e.getMessage());
		}
	}

	/**
	 * editDestinationWhereToProtectInfo4ReplicationToRemotelyManagedRPSTask
	 * 
	 * @author Ramya.Nagepalli
	 * @param severname
	 * @param server_username
	 * @param server_password
	 * @param server_port
	 * @param server_protocal
	 * @param remote_policy
	 * @param retry_minutes
	 * @param retry_num
	 * @param test
	 */
	public void editDestinationWhereToProtectInfo4ReplicationToRemotelyManagedRPSTask(String severname,
			String server_username, String server_password, String server_port, String server_protocal,
			String remote_policy, String retry_minutes, String retry_num, ExtentTest test) {

		test.log(LogStatus.INFO, "edit destination where to protect settings");
		WebElement where_ele = ElementFactory
				.getElementByXpath("//div[@class='destinationFormOverlayContainer']//*[text()='2. Where to protect']");
		where_ele.click();
		waitForSeconds(2);

		try {
			if (!severname.equals("") || !severname.equals(null)) {
				WebElement server_ele = ElementFactory.getElement(
						SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHERETOPROTECT_REMOTECONSOLESERVERNAME);
				server_ele.clear();
				server_ele.sendKeys(severname);
			}

			if (!server_username.equals("") || !server_username.equals(null)) {
				WebElement username_ele = ElementFactory.getElement(
						SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHERETOPROTECT_REMOTECONSOLEUSERNAME);
				username_ele.clear();
				username_ele.sendKeys(server_username);
			}

			if (!server_password.equals("") || !server_password.equals(null)) {
				WebElement password_ele = ElementFactory.getElement(
						SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHERETOPROTECT_REMOTECONSOLEPASSWORD);
				password_ele.clear();
				password_ele.sendKeys(server_password);
			}

			if (!server_port.equals("") || !server_port.equals(null)) {
				WebElement port_ele = ElementFactory.getElement(
						SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHERETOPROTECT_REMOTECONSOLEPORT);
				port_ele.clear();
				port_ele.sendKeys(server_port);
			}

			if (server_protocal.equalsIgnoreCase("http"))
				ElementFactory
						.getElement(
								SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHERETOPROTECT_HTTPPROTOCOL)
						.click();
			else
				ElementFactory.getElement(
						SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHERETOPROTECT_HTTPSPROTOCOL)
						.click();
			// connect button
			ElementFactory
					.getElement(
							SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHERETOPROTECT_CONNECTBUTTON)
					.click();

			if (!remote_policy.equals("") || !remote_policy.equals(null)) {
				WebElement remote_policy_ele = ElementFactory.getElement(
						SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHERETOPROTECT_SELECTPOLICYDROPDOWN);
				String xpath = ElementFactory.getElementAttrFromXML(
						SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHERETOPROTECT_SELECTPOLICYDROPDOWN)
						.get(0).getValue();
				waitForSeconds(9);
				remote_policy_ele.click();
				ElementFactory.getElementByXpath("//span[text()='" + remote_policy + "']").click();
			}

			if (!retry_minutes.equals("") || !retry_minutes.equals(null)) {
				WebElement retry_minutes_ele = ElementFactory.getElement(
						SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHERETOPROTECT_RETRYMINUTES);
				retry_minutes_ele.clear();
				retry_minutes_ele.sendKeys(retry_minutes);
			}

			if (!retry_num.equals("") || !retry_num.equals(null)) {
				WebElement retry_num_ele = ElementFactory.getElement(
						SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHERETOPROTECT_RETRYNUMBER);
				retry_num_ele.clear();
				retry_num_ele.sendKeys(retry_num);
			}

		} catch (NoSuchElementException e) {
			System.out.println("Element not found :" + e);
			test.log(LogStatus.INFO, "Element not found :" + e);
		}

	}

	/**
	 * editDestinationWhenToProtectInfo4ReplicationToRemotelyManagedRPSTask
	 * 
	 * @author Ramya.Nagepalli
	 * @param throttleScheduleArray
	 * @param mergeScheduleArray
	 * @param test
	 */
	public void editDestinationWhenToProtectInfo4ReplicationToRemotelyManagedRPSTask(
			ArrayList<HashMap<String, String>> throttleScheduleArray,
			ArrayList<HashMap<String, String>> mergeScheduleArray, ExtentTest test) {

		test.log(LogStatus.INFO, "Click on when to protect");
		WebElement whenEle = ElementFactory
				.getElementByXpath("//div[@class='destinationFormOverlayContainer']//*[text()='3. When to protect']");
		whenEle.click();
		waitForSeconds(2);

		test.log(LogStatus.INFO, "edit run schedule to trigger replication jobs from Cloud");
		if (mergeScheduleArray != null) {
			for (int i = 0; i < mergeScheduleArray.size(); i++) {
				HashMap<String, String> mergeSchedule = mergeScheduleArray.get(i);
				WebElement addEle = ElementFactory
						.getElementByXpath("//div[@class='destinationFormOverlayContainer']//span[text()='Add']");
				addEle.click();

				// get number of run schedules

				String xpath = ElementFactory
						.getElementAttrFromXML(
								SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHENTOPROTECT_SCHEDULELINE)
						.get(0).getValue();
				List<WebElement> lineEles = ElementFactory
						.getElementsByXpath("//div[@class='destinationFormOverlayContainer']" + xpath);
				WebElement lineEle = lineEles.get(i);

				// set run schedule
				String runSchedule = mergeSchedule.get("schedule");
				String[] schedules = runSchedule.split(";");
				WebElement scheduleEle = lineEle.findElement(
						By.xpath(".//div[@class='Select has-value is-clearable is-searchable Select--multi']"));

				WebElement schedule_input_ele = null;

				try {
					schedule_input_ele = scheduleEle.findElement(By.xpath(".//div[@class='Select-control']"));

				} catch (NoSuchElementException e) {

					System.out.println("Element not found with : " + e);
				}

				// clear the schedule
				WebElement clear_all_ele = schedule_input_ele.findElement(By.xpath(".//span[@class='Select-clear']"));
				clear_all_ele.click();

				// click on dropdown
				WebElement dropdown_ele = schedule_input_ele.findElement(By.xpath(".//span[@class='Select-arrow']"));
				dropdown_ele.click();

				for (int j = 0; j < schedules.length; j++) {

					try {
						String path = ".//div[@class='Select-menu-outer']//*[text()='" + schedules[j] + "']";
						WebElement selectEle = scheduleEle.findElement(By.xpath(path));
						selectEle.click();
					} catch (NoSuchElementException e) {
						System.out.println("Element not found : " + e);
					}
				}

				// set start time hour
				WebElement startTimeEle = lineEle.findElement(By.xpath(
						"((.//div[@class='time-wrapper-left hoursAndMinutes'])[1]//button[@class='dropdown-toggle btn btn-default'])[1]"));
				startTimeEle.sendKeys(Keys.ENTER);

				String[] starTime = mergeSchedule.get("starTime").split(":");
				String startTimeHour = starTime[0];
				lineEle.findElement(By.xpath(
						"((.//div[@class='time-wrapper-left hoursAndMinutes'])[1]//div[@class='dropdown open btn-group'])[1]//a[@value='"
								+ startTimeHour + "']"))
						.click();

				startTimeEle = lineEle.findElement(By.xpath(
						"((.//div[@class='time-wrapper-left hoursAndMinutes'])[1]//button[@class='dropdown-toggle btn btn-default'])[2]"));
				startTimeEle.sendKeys(Keys.ENTER);
				String startTimeMin = starTime[1];
				lineEle.findElement(By.xpath(
						"(.//div[@class='time-wrapper-left hoursAndMinutes']//div[@class='dropdown open btn-group'])[1]//a[@value='"
								+ startTimeMin + "']"))
						.click();

				// set end time

				WebElement endTimeEle = lineEle.findElement(By.xpath(
						"((.//div[@class='time-wrapper-left hoursAndMinutes'])[2]//button[@class='dropdown-toggle btn btn-default'])[1]"));
				endTimeEle.sendKeys(Keys.ENTER);

				String[] endTime = mergeSchedule.get("endTime").split(":");
				String endTimeHour = endTime[0];
				lineEle.findElement(By.xpath(
						"((.//div[@class='time-wrapper-left hoursAndMinutes'])[2]//div[@class='dropdown open btn-group'])[1]//a[@value='"
								+ endTimeHour + "']"))
						.click();

				endTimeEle = lineEle.findElement(By.xpath(
						"(.//div[@class='time-wrapper-left hoursAndMinutes']//button[@class='dropdown-toggle btn btn-default'])[4]"));
				endTimeEle.sendKeys(Keys.ENTER);
				String endTimeMin = endTime[1];
				lineEle.findElement(By.xpath(
						"((.//div[@class='time-wrapper-left hoursAndMinutes'])[2]//div[@class='dropdown open btn-group'])[1]//a[@value='"
								+ endTimeMin + "']"))
						.click();

			}
		}

		test.log(LogStatus.INFO, "edit throttle schedule to trigger replication jobs from Cloud");
		if (throttleScheduleArray != null) {
			editDestinationThrottlingSchedule(throttleScheduleArray);
		}

	}

	/**
	 * editDestinationWhereToProtectInfo4replicationFromTask
	 * 
	 * @param destVolumeName
	 * @param test
	 */
	public void editDestinationWhereToProtectInfo4replicationFromTask(String destVolumeName, ExtentTest test) {
		test.log(LogStatus.INFO, " set destination for where to protect in Replication policy");
		setDestinationForWhereToProtectInPolicy(destVolumeName, test);
	}

	/**
	 * setDestinationForWhereToProtectInPolicy
	 * 
	 * @author Ramya.Nagepalli
	 * @param destVolumeName
	 * @param test
	 */
	public void setDestinationForWhereToProtectInPolicy(String destVolumeName, ExtentTest test) {
		test.log(LogStatus.INFO,
				" navigate to Where to protect and set on given destination input : " + destVolumeName);
		try {
			// where to protect
			WebElement whereEle = ElementFactory
					.getElement(SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHERETOPROTECT);
			whereEle.click();
			this.waitForSeconds(2);

			WebElement destEle = ElementFactory
					.getElement(SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHERETOPROTECT_DESTINATION);
			destEle.click();

			WebElement destVolumeEle = ElementFactory.getElementByXpath(".//a[text()='" + destVolumeName + "']");
			destVolumeEle.click();

		} catch (NoSuchElementException e) {
			assertFalse(true, "Element Not found : " + e);
		}

	}

	/**
	 * clickSavePolicy
	 * 
	 * @author Ramya.Nagepalli
	 */
	public void clickSavePolicy() {

		Button savePolicyBtn = new Button(SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_CREATEPOLICYBTN);
		savePolicyBtn.click();

		if (!ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_COMMON_SAVEDSEARCHESLABEL, 10))
			spogUIServer.goToPolicyPage();

	}

	/**
	 * editDestinationWhatToProtectInfo4ReplicationFromTask
	 * 
	 * @author Ramya.Nagepalli
	 * @param test
	 */
	public void editDestinationWhatToProtectInfo4ReplicationFromTask(ExtentTest test) {
		test.log(LogStatus.INFO, "navigate to Destinations tab to set the policy configuration");
		WebElement destinationEle = ElementFactory
				.getElement(SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION);
		destinationEle.click();

		this.waitForSeconds(1);
		try {
			test.log(LogStatus.INFO, "Select the activity type for the Cloud Hybrid Replication Policy");
			WebElement select_active_type_ele = ElementFactory
					.getElement(SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHATTOPROTECT_ACTIVETYPE);
			select_active_type_ele.click();

			waitForSeconds(1);
			/*ElementFactory
					.getElement(SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHATTOPROTECT_REPLICATEFROM)
					.click();*/
			WebElement element = ElementFactory.getElementByXpath("//span[text()='Replicate from a remotely-managed RPS']");
			element.click();

		} catch (NoSuchElementException e) {

			System.out.println("Element not found : " + e);
			assertFalse(true, e.getMessage());
		}

	}
}
