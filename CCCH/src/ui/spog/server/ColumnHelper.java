package ui.spog.server;

import static org.testng.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import ui.base.common.PageName;
import ui.base.common.SPOGMenuTreePath;
import ui.base.common.Url;
import ui.base.factory.BrowserFactory;
import ui.base.factory.ElementFactory;
import ui.spog.pages.ColumnPage;
import ui.spog.pages.protect.SourcePage;

public class ColumnHelper extends SPOGUIServer{
	
	private ColumnPage columnPage;
	
	public ColumnHelper(String browserType, int maxTimeWaitSec) {
		super(browserType, maxTimeWaitSec);
		columnPage = new ColumnPage();
	}
	
	
	
	public void chooseAllColumns(boolean select, ExtentTest test) {
		
		if (!columnPage.checkIsSettingsBtnExpanded()) {
			test.log(LogStatus.INFO, "expand Settings button to manage the columns");
			columnPage.clickManageColumns();	
		}
		
		test.log(LogStatus.INFO, "enable the selected columns");
		columnPage.chooseAllColumns(select);
		
		if (columnPage.checkIsSettingsBtnExpanded()) {
			test.log(LogStatus.INFO, "un-expand Settings button");
			columnPage.clickManageColumns();	
		}
	}
	
	public void enableColumnsWithCheck(ArrayList<String> columns, ExtentTest test) {
		
		if (!columnPage.checkIsSettingsBtnExpanded()) {
			test.log(LogStatus.INFO, "expand Settings button to manage the columns");
			columnPage.clickManageColumns();	
		}
		
		test.log(LogStatus.INFO, "enable the selected columns");
		for (int i = 0; i < columns.size(); i++) {
			columnPage.chooseColumn(columns.get(i), true);	
		}		
		
		if (columnPage.checkIsSettingsBtnExpanded()) {
			test.log(LogStatus.INFO, "un-expand Settings button");
			columnPage.clickManageColumns();	
		}
		
		test.log(LogStatus.INFO, "Check enabled columns displaying in table");
		columnPage.checkEnabledColumns(columns, test);
	}
	
	public void unSelectAllColumnsWithCheck(ArrayList<String> columns, ArrayList<String> defaultColumns, ExtentTest test) {
		
		if (!columnPage.checkIsSettingsBtnExpanded()) {
			test.log(LogStatus.INFO, "expand Settings button to manage the columns");
			columnPage.clickManageColumns();	
		}
		
		test.log(LogStatus.INFO, "Un select all columns");
		for (int i = 0; i < columns.size(); i++) {
			columnPage.chooseColumn(columns.get(i), false);
		}
		
		
		
		if (columnPage.checkIsSettingsBtnExpanded()) {
			test.log(LogStatus.INFO, "un-expand Settings button");
			columnPage.clickManageColumns();	
		}
		
		test.log(LogStatus.INFO, "Check only default columns exist after unselecting all columns");
		columnPage.checkDefaultColumns(defaultColumns, test);
	}	
	
	public void sortByColumnName(String columnName, String sortOrder) {
		
		columnPage.clickOnColumnName(columnName);	
				
		if (sortOrder.equalsIgnoreCase("desc")) {
			columnPage.clickOnColumnName(columnName);	
		}
	}
	
	public void sortByColumnNameWithCheck(String columnName, String sortOrder, ExtentTest test) {
		
		chooseAllColumns(true, test);
				
		if(columnPage.checkColumnExisted(columnName)) {

			ArrayList<String> expectedValues = columnPage.getValuesInSpecifiedColumn(columnName);

			columnPage.clickOnColumnName(columnName);	
			Collections.sort(expectedValues);
			
			if (sortOrder.equalsIgnoreCase("desc")) {
				columnPage.clickOnColumnName(columnName);	
				Collections.reverse(expectedValues);				
			}
			
			ArrayList<String> actualValues = columnPage.getValuesInSpecifiedColumn(columnName);

			columnPage.checkColumnValues(expectedValues, actualValues);
		}else {
			assertFalse(true, "column with name:"+ columnName+" does not exist.");
		}
	}
		
	
	public void createFilter(String search_string, ArrayList<String> filters, ExtentTest test) {
		
		columnPage.searchByFilterTypeAndName(search_string, filters, test);
		
		
		
	}
}
