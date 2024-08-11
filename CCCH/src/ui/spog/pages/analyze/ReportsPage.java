package ui.spog.pages.analyze;
/*
 * @author Naga Malleswari
 * 
 * Create on demand reports  
 * Create Schedule report actions
 *  On demand reports Validation in the reports Page
 * Schedule reports validation in  the Managed reports Page 
 * 
 */


import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.gargoylesoftware.htmlunit.javascript.host.media.webkitMediaStream;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import InvokerServer.SPOGServer;
import genericutil.ErrorHandler;
import ui.base.common.BasePage;
import ui.base.common.BrowserOperation;
import ui.base.common.SPOGMenuTreePath;
import ui.base.common.TableHeaders;
import ui.base.elements.Button;
import ui.base.elements.TextField;
import ui.base.factory.BrowserFactory;
import ui.base.factory.ElementFactory;



public class ReportsPage  extends BasePage{

	private ErrorHandler errorHandle = BasePage.getErrorHandler();
	/**
	 * @author Nagamalleswari.Sykam
	 *validate the customReports in the ReportsTable
	 * @param reportName
	 * @param datRangeDetails
	 * @param test
	 */
	public void  checkCustomreportsinReportsTable(String reportName,  String [] datRangeDetails,ExtentTest test) {


		waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_TABLE_HEADERS);

		HashMap<String, Integer> headerOrdr = getTableHeaderOrder(getTableHeaders());

		List<WebElement> rows = getRows();
		//HashMap<String, Integer> headerOrder = getTableHeaderOrder(getSoucregroupheaders());

		for (int i = 0; i < rows.size(); i++) {
			WebElement eachRow = rows.get(i);
			String xpath = "."+ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_TABLE_COLUMNS).get(0).getValue();
			List<WebElement> columns = eachRow.findElements(By.xpath(xpath));



			if(i == rows.size()-1) {
				test.log(LogStatus.FAIL, "Source group with name:"+reportName+" not found in table.");
				assertFalse(false, "Source group with name:"+reportName+" not found in table.");
			}
		}

	}


	/**
	 * @author Nagamalleswari.Sykam
	 * Validate the On demand Reports in the Reports Table 
	 * @param reoprtname
	 * @param test
	 */
	public void  checkreportsinReportsTable(String reoprtname, ExtentTest test) {

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
				test.log(LogStatus.FAIL, "Source group with name:"+reoprtname+" not found in table.");
				assertFalse(false, "Source group with name:"+reoprtname+" not found in table.");
			}
		}

	}

	/**
	 * @author Nagamalleswari.Sykam
	 * @param ReportName
	 * @param toastmessgae
	 */
	public void deletereportByName(String ReportName,String toastmessgae) {

		if (checkReportExist(ReportName)) {
			String result = clickContextualActionForSpecifiedElement(ReportName,"Delete Report" );
			assertEquals(result, "pass");

			//Click on the Confirm button to delete the Report
			waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_COMMON_CONFIRM, BrowserFactory.getMaxTimeWaitUIElement());
			WebElement ConfirmBtn =ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_CONFIRM);
			ConfirmBtn.click();
			//Validate the Toast  Message 
			waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_TOASTMESSAGE);
			String ActualToastMessage = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_TOASTMESSAGE).getText();
			assertEquals(ActualToastMessage, toastmessgae);

		} else {
			assertTrue(true, "Report with name:"+ReportName+" not found in the table.");
		}

	}
	/**
	 * @author Nagamalleswari.Sykam
	 * Check the ReportName is Exist 
	 * @param reportName
	 * @return
	 */

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
	 * SetCustomReport details 
	 * @param startDataRange
	 * @param endDataRange
	 */

	public boolean setCustomReportDetails(String [] startDataRange ,String [] endDataRange) {
		boolean isCustomDetailsCorrect = false;
		if(isStartDataRangeYearExist(startDataRange)&&isStartDatarangeMonthExist(startDataRange)&& isStartDateExist(startDataRange)&&isEndDataRangeYearExist(endDataRange)&&isEndDateRangeMonthExist(endDataRange)&&isEndDateExist(startDataRange,endDataRange)){
			isCustomDetailsCorrect=true;
		}
		return isCustomDetailsCorrect;

	}

	/**
	 * @author Nagamalleswari.Sykam 
	 * Check the startDataRangeYear is Exist
	 * @param startDataRange
	 * @return
	 */
	public boolean isStartDataRangeYearExist(String [] startDataRange) {
		boolean isYearExist = false;
		//Click on the Calendar Button 
		calenderBtn();
		List<WebElement> get_Calender =  getCalendars();
		for(int i =0;i<get_Calender.size();i++) {
			if(i==0) {

				WebElement leftCalenderBtn = ElementFactory.getElement(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_CALENDAR_LEFTCALENDARYEARBTN);
				leftCalenderBtn.click();

				//Set the year 
				List<WebElement>leftCalenderAvaiableYears = ElementFactory.getElements(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_CALENDAR_LEFTCALENDARAVAILABLEYEARS);
				for(int k=0;k<leftCalenderAvaiableYears.size();k++) {
					WebElement	eachYear = leftCalenderAvaiableYears.get(k);
					String actYear = eachYear.getText();
					if(actYear.equalsIgnoreCase(startDataRange[0])) {
						eachYear.click();
						isYearExist = true;
						break;
					}else {
						//the given year is not available 

					}
				}
				if(isYearExist) {
					break;
				}
			}

		}
		return isYearExist;

	}

	/**
	 * @author Nagamalleswari.Sykam
	 * check the startDataRange month is exist
	 * @param startDataRange
	 * @return
	 */

	public boolean isStartDatarangeMonthExist(String [] startDataRange) {

		boolean isMonthEnabled = false;
		//Click on the Month Button 
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_CALENDAR_LEFTCALENDARMONTHBTN, 3);
		WebElement monthBtn = ElementFactory.getElement(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_CALENDAR_LEFTCALENDARMONTHBTN);
		monthBtn.click();

		//Get the all Month details 
		List<WebElement> leftCalAvailMonths = ElementFactory.getElements(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_CALENDAR_LEFTCALENDARAVAILMONTHS);
		for(int m=0;m<leftCalAvailMonths.size();m++) {
			String exp_Month_name= startDataRange[1];
			WebElement each_month = leftCalAvailMonths.get(m);
			String each_month_Name= each_month.getText();
			String act_month_status= each_month.getAttribute("disabled");
			String monthStatus= each_month.getAttribute("selected");
			if(exp_Month_name.contains(each_month_Name)) {
				if (act_month_status!=null){
					// code to select l
				}else {
					each_month.click();
					isMonthEnabled =true;
					break;
				}
				break;
			}
			if(isMonthEnabled) {
				break;

			}
		}
		return isMonthEnabled;
	}


	/**
	 * @author Nagamalleswari.Sykam
	 * Check the startDate is Exist 
	 * @param startDataRange
	 * @return
	 */

	public boolean isStartDateExist(String [] startDataRange) {
		boolean isdateSelected= false;
		WebElement expDate = ElementFactory.getElementByXpath("//div[@class='calendar left']//table[@class='table-condensed']/tbody/tr/td[text()='"+startDataRange[2]+"']");
		if(startDataRange[2]!=null) {
			if(isStartDateRangeDateEnable(startDataRange)) {
				isdateSelected = true;
				expDate.click();
			}
		}
		return isdateSelected;

	}

	/**
	 * @author Nagamalleswari.Sykam
	 *  Check the StartDateRangeDate is Enable
	 * @param startDataRange
	 * @return
	 */
	public boolean isStartDateRangeDateEnable(String [] startDataRange) {
		boolean isDateEnable = false;

		List<WebElement>leftCalAvialDates = ElementFactory.getElements(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_CALENDAR_LEFTCALAVAILDATES);

		//Get SelectedMonth Details 
		WebElement selctedMonthEle = ElementFactory.getElementByXpath("//div[@class='calendar left']//table[@class='table-condensed']//select[@class='monthselect']//option[@selected='selected']");
		//WebElement selctedMonthEle = ElementFactory.getElement(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_CALENDAR_LEFTCALSELECTEDMONTH);
		String selectedMonthName = selctedMonthEle.getText();
		System.out.println(selectedMonthName);
		for(int l=0;l<leftCalAvialDates.size();l++) {
			WebElement disbaledDate = leftCalAvialDates.get(l);
			String dateName = disbaledDate.getText();
			if(dateName.equalsIgnoreCase(startDataRange[2])&&selectedMonthName.contains(startDataRange[1])) {
				isDateEnable=true;
				break;
			}
		}
		return isDateEnable;
	}


	//Set EndDataRange details
	/**
	 * @author Nagamalleswari.Sykam
	 * Check the EndDataRange Year is Exist 
	 * @param endDataRange
	 * @return
	 */

	public boolean isEndDataRangeYearExist(String [] endDataRange) {
		boolean isYearExist = false;
		String actEndDataRangeYear = endDataRange[0];

		int actEndYear = Integer.parseInt(actEndDataRangeYear);
		List<WebElement> get_Calender =  getCalendars();

		//Selected startDateRangeYear details 
		WebElement leftCalSelctedYear = ElementFactory.getElement(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_CALENDAR_LEFTCALSELECTEDYEAR);
		String selctedYear = leftCalSelctedYear.getText();
		int startYear = Integer.parseInt(selctedYear);
		System.out.println(startYear);

		for(int i=0;i<get_Calender.size();i++) {

			if(i==1) {
				if(selctedYear!=null&& actEndYear>=startYear) {
					WebElement rightCalYearBtn = ElementFactory.getElement(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_CALENDAR_RIGHTCALYEARBTN);
					rightCalYearBtn.click();
					//Set the year 
					List<WebElement> RigthCalAviailYears = ElementFactory.getElements(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_CALENDAR_RIGHTCALAVAILYEARS);

					for(int k=0;k<RigthCalAviailYears.size();k++) {
						WebElement	eachYear = RigthCalAviailYears.get(k);
						String actYear = eachYear.getText();
						if(actYear.contains(endDataRange[0])) {
							eachYear.click();
							isYearExist = true;
							break;
						}else {
							System.out.println("The given year "+endDataRange[0] +" Doesn't  exist ");
						}
					}
				}
			}
		}
		return isYearExist;
	}


	/**
	 * @author Nagamalleswari.Sykam
	 * Checking the EndDateRangeMonth is Exist
	 * @param endDataRange
	 * @return
	 */
	public boolean isEndDateRangeMonthExist(String [] endDataRange) {
		boolean isMonthEnabled = false;
		WebElement rightCalMonthBtn = ElementFactory.getElement(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_CALENDAR_RIGHTCALMONTHBTN);
		rightCalMonthBtn.click();
		//Get the all Month details 
		List<WebElement>rightCalAvailMonths = ElementFactory.getElements(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_CALENDAR_RIGHTCALENDARAVAILMONTHS);
		for(int m=0;m<rightCalAvailMonths.size();m++) {
			String exp_Month_name= endDataRange[1];
			WebElement each_month = rightCalAvailMonths.get(m);
			String each_month_Name= each_month.getText();

			String act_month_status= each_month.getAttribute("disabled");
			String monthStatus= each_month.getAttribute("selected");
			if(exp_Month_name.contains(each_month_Name)) {
				if (act_month_status != null && monthStatus != null) {
					// code to select l
				}
				each_month.click();
				isMonthEnabled =true;
				break;
			}
		}
		return isMonthEnabled;
	}
	/**
	 * @author Nagamalleswari.Sykam
	 * 
	 * Verify the  DateRange End Date  is Exist 
	 * 
	 * @param startDataRange
	 * @return
	 */

	public boolean isEndDateExist(String [] startDataRange,String [] endDataRange) {
		boolean isdateSelected= false;
		WebElement expDate = ElementFactory.getElementByXpath("//div[@class='calendar right']//table[@class='table-condensed']/tbody/tr/td[text()='"+endDataRange[2]+"']");
		if(endDataRange[2]!=null) {
			if(isEndDateRangeDateEnable(startDataRange,endDataRange)) {
				isdateSelected = true;
				expDate.click();
			}
		}
		return isdateSelected;

	}


	/**
	 * @author Nagamalleswari.Sykam 
	 * Checking the EndDateRangeDate is Enable 
	 * @param startDataRange
	 * @param endDataRange
	 * @return
	 */
	public boolean isEndDateRangeDateEnable(String []startDataRange,String [] endDataRange) {
		boolean isDateEnable = false;
		//EndDateRange YEAR,DATE and MONTH
		int endYear =Integer.parseInt(endDataRange[0]);
		int endDate = Integer.parseInt(endDataRange[2]);
		String endMonth = endDataRange[1];

		//EndDateRange YEAR,DATE and MONTH
		int startYear = Integer.parseInt(startDataRange[0]);
		int startDate = Integer.parseInt(startDataRange[2]);
		String startMonth = startDataRange[1];

		//Get the Start Date range selected month
		WebElement selectedMonth = ElementFactory.getElementByXpath("//div[@class='calendar right']//select[@class='monthselect']//option[@selected='selected']");
		String selectedMonthName = selectedMonth.getText();
		System.out.println(selectedMonthName);

		//Get the all available Dates 
		List<WebElement>availDates = ElementFactory.getElements(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_CALENDAR_RIGHTCALAVAILDATES);
		for(int l=0;l<availDates.size();l++) {
			WebElement disbaledDate = availDates.get(l);
			String dateName = disbaledDate.getText();
			if(dateName.equalsIgnoreCase(endDataRange[2])&&selectedMonthName.contains(endDataRange[1])) {
				if(startYear==endYear&&startMonth==endMonth&&startDate>endDate) {
					//Don't click on the Date
				}
				isDateEnable=true;
				break;
			}
		}
		return isDateEnable;
	}


	/**
	 * @author Nagamalleswari.Sykam
	 * Set the Details to create the onDemandReports 
	 * @param Genratetype
	 * @param Datarange
	 */
	public void setDetailsToCreateOndemandReport(String genratetype,String dateRange) {
		//Ondemand report creation
		if (genratetype.contains("ondemand")) {
			waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_GENERATENOW,BrowserFactory.getMaxTimeWaitUIElement());
			WebElement generateNowBtn =ElementFactory.getElement(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_GENERATENOW);
			generateNowBtn.click();

			//waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_SELECTDATARANGE, BrowserFactory.getMaxTimeWaitUIElement());
			assertTrue(ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_MENUTITLE));
			WebElement dataRangeMenu =ElementFactory.getElement(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_DATARANGEMENU);
			dataRangeMenu.click();

			try {
				waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_SELECTDATARANGE, BrowserFactory.getMaxTimeWaitUIElement());
				List<WebElement> availdataRangeDetails = daterangedetails();
				for(int i=0;i<availdataRangeDetails.size();i++) {
					WebElement eachDateRangeFd = availdataRangeDetails.get(i);
					String eachDateRangeName =availdataRangeDetails.get(i).getText();
					if(eachDateRangeName.equalsIgnoreCase(dateRange)) {
						eachDateRangeFd.click();
						break;
					}

				}


			}catch (Exception e) {
				errorHandle.printErrorMessageInDebugFile("There is no filter: "+dateRange+" is not found"); 
			}
		}
	}

	/**
	 * @author Nagamalleswari.Sykam
	 * 
	 * ClickOn CreateReport Button 
	 */
	public void clickOnCreateReportBtn() {
		//Click on Create reports
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_CREATEREPORT,BrowserFactory.getMaxTimeWaitUIElement());
		WebElement ceateReportBtn = ElementFactory.getElement(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_CREATEREPORT);
		ceateReportBtn.click();

	}


	/**
	 * @author Nagamalleswari.Sykam
	 * set the ReportType
	 * @param reportType
	 */

	public void setReportType(String reportType) {

		//Click on the ReportType Filed DropDown
		assertTrue(ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_REPORTTYPETITLE));
		WebElement reporTypeDropDown = ElementFactory.getElement(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_REPORTTYPEBUTTON);
		reporTypeDropDown.click();

		List<WebElement> menu =ElementFactory.getElements(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_DROPDOWNMENU);
		for(int i=0; i<	menu.size();i++) {
			String actreportType = menu.get(i).getText();
			if(actreportType.equalsIgnoreCase(reportType)) {
				WebElement getElement = menu.get(i);
				getElement.click();
				break;
			}
			else {
				if(i==menu.size()-1) {
					System.out.println("Given ReportType "+reportType+" is Doesn't exist");
				}

			}
		}

	}
	/**
	 * @author Nagamalleswari.Sykam 
	 * check the createReportTitle 
	 * @param reportName
	 */
	//Check the report titles 
	public void checkCreateReportTitle(String reportName) {
		if(reportName.contains("Backup")) {
			assertTrue(ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_BACKUPJOBREPORTS_TITLE));
		} else if(reportName.contains("Recovery")) {
			assertTrue(ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_RECOVERYJOBREPORTS_TITLE));
		}else if(reportName.contains("DataTransfer")) {
			assertTrue(ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_DATATRANSFERREPORTS_TITLE));
		}else if(reportName.contains("Capacity")) {
			assertTrue(ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_CAPACITYUSAGEREPORTS_TITLE));
		}	else {
			assertTrue(ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_TITLE));
		}

	}
	/**
	 * @author Nagamalleswari.Sykam
	 * set the ReportName 
	 * @param reportName
	 */
	//Set the Report Name 
	public void setReportName(String reportName){

		if(reportName!=null) {
			waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_REPORTNAME,BrowserFactory.getMaxTimeWaitUIElement());
			TextField reportNameFd = new TextField(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_REPORTNAME);
			reportNameFd.clear();
			reportNameFd.sendKeys(reportName);
		}
	}


	/**
	 * @author Nagamalleswari.Sykam
	 * set the selectionofSources 
	 * 
	 * @param reportSoucres
	 */

	public void setSlectionOfSoucres(String reportSoucres) {

		//create report for selected sources

		if(reportSoucres.contains("SlectedSources")) {
			waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_SELECTEDSOURCES, BrowserFactory.getMaxTimeWaitUIElement());
			WebElement SelctedSoucreGroups =ElementFactory.getElement(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_SELECTEDSOURCES);
			SelctedSoucreGroups.click();

			waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_SELECTEDSOURCEGROUPMENU, BrowserFactory.getMaxTimeWaitUIElement());
			WebElement menu = ElementFactory.getElement(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_SELECTEDSOURCEGROUPMENU);
			menu.click();

			waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_ADD, BrowserFactory.getMaxTimeWaitUIElement());
			WebElement addBtn = ElementFactory.getElement(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_ADD);
			addBtn.click();


		}else {

			// report is creating for the allSources

		}
	}

	/**
	 * @author Nagamalleswari.Sykam
	 * set the Recipient email
	 * @param recepientMail
	 */
	//Set the recipient email details
	public void setEmailRecepients(String [] recepientMail) {

		//Recipient Email Title
		String reportTitle = ElementFactory.getElement(SPOGMenuTreePath.SPOG_REPORTS_COMMON_RECEPIENTTITLE).getText();
		assertTrue(reportTitle.contains("Enter recipient's email address to receive"));

		for(int i=0;i<recepientMail.length;i++) {
			if((recepientMail[i]!=null && recepientMail[i].contains(""))) {

				if(recepientMail[i].contains("@")&&recepientMail[i].contains(".com")){
					waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_RECEPIENTID, BrowserFactory.getMaxTimeWaitUIElement());

					WebElement recepientFd = ElementFactory.getElement(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_RECEPIENTID);
					waitForMilSeconds(5);
					recepientFd.click();
					recepientFd.sendKeys(recepientMail[i]);

					waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_ADD,BrowserFactory.getMaxTimeWaitUIElement());
					WebElement addBtn = ElementFactory.getElement(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_ADD);
					addBtn.click();
				}else {
					System.out.println("Given Emial_id :"+  recepientMail+" is not a valid ");
				}


			}

		}
	}

	/*public void setScheduleDelivaryTimeBtn() {

		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_SCHEDULEREPORTDELIVERYTIME, BrowserFactory.getMaxTimeWaitUIElement());
		WebElement scheuleDelivaryTime =ElementFactory.getElement(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_SCHEDULEREPORTDELIVERYTIME);
		scheuleDelivaryTime.click();

	}*/
	/**
	 * @author Nagamalleswari.Sykam
	 * Set GenerateTypeDetials 
	 * @param generatetype
	 * @param delevaryTime
	 */

	public void setGenerateTypeDetails(String generatetype,String delevaryTimeInHours , String delevaryTimeInMinutes) {
		if(generatetype.contains("Schedule")) {
			waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_SCHEDULEREPORT, BrowserFactory.getMaxTimeWaitUIElement());
			WebElement scheduleReportBtn = ElementFactory.getElement(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_SCHEDULEREPORT);
			scheduleReportBtn.click();

			//Click on the scheduleDelicary Time
			//setScheduleDelivaryTimeBtn();

			try {
				//Set the delivary time to the Schedule report
				List<WebElement> delivaryMenu = delivaryTimedetails();

				for(int i=0;i<delivaryMenu.size();i++) {
					if(i==0) {

						WebElement timeInHours = delivaryMenu.get(i);
						timeInHours.click();
						String timeFd =delivaryMenu.get(i).getText();
						List<WebElement> allHours = ElementFactory.getElementsByXpath("//div[@class='dropdown open btn-group']/ul[@class='dropdown-menu']/li");
						for(int y=0;y<allHours.size();y++) {
							WebElement eachHour = allHours.get(y);
							String eachHourDetails = eachHour.getText();
							if(eachHourDetails.equalsIgnoreCase((delevaryTimeInHours))){
								eachHour.click();
								break;
							}
						}

					}
					if(i==1) {
						WebElement timeInminutes = delivaryMenu.get(i);
						timeInminutes.click();
						String timeFd =delivaryMenu.get(i).getText();
						List<WebElement> allminutes = ElementFactory.getElementsByXpath("//div[@class='dropdown open btn-group']/ul[@class='dropdown-menu']/li");
						for(int y=0;y<allminutes.size();y++) {
							WebElement eachminute = allminutes.get(y);
							String eachMinuteDetails = eachminute.getText();
							if(eachMinuteDetails.equalsIgnoreCase((delevaryTimeInMinutes))){
								eachminute.click();
								break;
							}
						}
					}


				}
			}catch (Exception e) {
				errorHandle.printDebugMessageInDebugFile("There is no time: " + generatetype+" is not found");
			}
		}

	}


	/*	public void setRandomDelivaryType(String generatetype,String delevaryTime) {
		if(generatetype.contains("Schedule")) {
			waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_SCHEDULEREPORT, BrowserFactory.getMaxTimeWaitUIElement());
			WebElement scheduleReportBtn = ElementFactory.getElement(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_SCHEDULEREPORT);
			scheduleReportBtn.click();

			//Click on the scheduleDelicary Time
			setScheduleDelivaryTimeBtn();

			try {
				//Set the delivary time to the Schedule report
				List<WebElement> delivaryMenu = delivaryTimedetails();



				for(int i=0;i<delivaryMenu.size();i++) {
					int randomdate = Math.random();
				}
			}catch (Exception e) {
				errorHandle.printDebugMessageInDebugFile("There is no time: " + generatetype+" is not found");
			}
		}
}*/
	/**
	 * @author Nagamalleswari.Sykam
	 * SetTimeType Details
	 * @param timeType
	 */

	public void SetTimeTypeDetails(String timeType) {
		//Set TimeType
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_TIMETYPE, BrowserFactory.getMaxTimeWaitUIElement());
		WebElement setTimeType = ElementFactory.getElement(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_TIMETYPE);
		setTimeType.click();

		try {
			//Set the  time to the Schedule report
			List<WebElement> TimeMenu = timeTypeMenu();
			for(int i=0;i<TimeMenu.size();i++) {
				WebElement time = TimeMenu.get(i);
				String acttimeType =TimeMenu.get(i).getText();
				if(acttimeType.contains(timeType)) {
					time.click();
					break;
				}
			}
		}catch (Exception e) {
			errorHandle.printDebugMessageInDebugFile("There is no time: " + timeType+" is not found");
		}
	}

	/**
	 * @author Nagamalleswari.Sykam
	 * Set Frequency Details
	 * 
	 * @param frequency
	 */
	public void setFrequencyDetails(String frequency) {

		assertTrue(ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_FREQUENCYTITLE));
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_FREQUENCY, BrowserFactory.getMaxTimeWaitUIElement());
		WebElement frequencyFd = ElementFactory.getElement(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_FREQUENCY);
		frequencyFd.click();

		try {
			/*	waitForSeconds(10);
			waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_FREQUENCYMENU,BrowserFactory.getMaxTimeWaitUIElement());
			WebElement frequencyMenu = ElementFactory.getElement(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_FREQUENCYMENU);
			frequencyMenu.findElement(By.xpath(".//span[text()="+frequency+"]")).click();
			 */
			List<WebElement> frequencymenu = FrequecnyDetials();
			System.out.println(frequencymenu);

			for(int i=0;i<frequencymenu.size();i++) {
				WebElement menu = frequencymenu.get(i);
				String actFrequency =frequencymenu.get(i).getText();
				if(actFrequency.contains(frequency)) {
					menu.click();
					break;
				}

			}
		}catch (Exception e) {
			//	errorHandle.printDebugMessageInDebugFile("There is no time: " + frequency+" is not found");
			assertTrue(false,"The applied "+ frequency+" is not found");
		}
	}

	/**
	 * @author Nagamalleswari.Sykam
	 * @param click 
	 */
	public void cancelBtn() {
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_CANCELBTN, BrowserFactory.getMaxTimeWaitUIElement());
		WebElement cancelBtn = ElementFactory.getElement(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_CANCELBTN);
		cancelBtn.click();
	}

	/**
	 * @author Nagamalleswari.Sykam 
	 * Click on ApplyButton 
	 */

	public void applyBtn() {
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_APPLYBTN, BrowserFactory.getMaxTimeWaitUIElement());
		WebElement applyBtn = ElementFactory.getElement(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_APPLYBTN);
		applyBtn.click();
	}

	/**
	 * @author Nagamalleswari.Sykam 
	 * setDelivaryTime Details
	 * @return
	 */

	public List<WebElement> delivaryTimedetails(){
		List<WebElement> details =ElementFactory.getElements(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_SCHEDULEREPORTDELIVERYTIME);
		return details;
	}

	/**
	 * @author Nagamalleswari.Sykam
	 * Get TimeType Details
	 * @return
	 */
	public  List<WebElement> timeTypeMenu(){
		List<WebElement> timetype =ElementFactory.getElements(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_TIMETYPEMENU);
		return timetype;

	}
	/**
	 * @author Nagamalleswari.Sykam
	 * Set Frequency Details 
	 * @return
	 */

	public List<WebElement>FrequecnyDetials(){
		List<WebElement> frequency =ElementFactory.getElements(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_FREQUENCYITEMS);
		return frequency;

	}

	/**
	 * @author Nagamalleswari.Sykam
	 * Get the DateRange Details 
	 * @return
	 */
	public List<WebElement> daterangedetails(){
		List<WebElement>details =ElementFactory.getElements(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_SELECTDATARANGE);
		return details;
	}

	/**
	 * @author Nagamalleswari.Sykam
	 * 
	 * @param startDataRange
	 * @param endDataRange
	 * @param click on the calendar button 
	 */


	public  void calenderBtn () {
		//Click on calendar button 
		WebElement calenderBtn = ElementFactory.getElement(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_CALENDAR_CALENDERBTN);
		calenderBtn.click();
	}

	/**
	 * @author Nagamalleswari.Sykam
	 * Click on Create Button 
	 * @param toastMessgage
	 */
	public void createbtn(String toastMessgage) {
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_CREATEBTN,BrowserFactory.getMaxTimeWaitUIElement());
		WebElement createBtn = ElementFactory.getElement(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_CREATEBTN);
		createBtn.click();
		waitForSeconds(5);

		/*waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_TOAST_MESSGAE);
		String ActualToastMessage = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_TOAST_MESSGAE).getText();
		assertEquals(ActualToastMessage, toastMessgage);
		 */}

	/**
	 * @author Nagamalleswari.Sykam
	 * 
	 * Click on Cancel Button 
	 */

	public void cancel() {
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_COMMON_CANCEL, BrowserFactory.getMaxTimeWaitUIElement());
		WebElement cancelBtn =ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_CANCEL);
		cancelBtn.click();
	}

	/**
	 * @author Nagamalleswari.Sykam
	 * Get All available calendars 
	 * @return
	 */

	public List<WebElement> getCalendars() {
		List<WebElement> details = ElementFactory.getElements(SPOGMenuTreePath.SPOG_ANALYZE_REPORTS_COMMON_CALENDAR_ALLCALENDARS);
		return details;
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
