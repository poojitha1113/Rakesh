package ui.spog.pages;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.annotation.WebListener;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import com.gargoylesoftware.htmlunit.javascript.host.media.webkitMediaStream;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import genericutil.ErrorHandler;
import ui.base.common.BasePage;
import ui.base.common.SPOGMenuTreePath;
import ui.base.elements.Button;
import ui.base.factory.BrowserFactory;
import ui.base.factory.ElementFactory;

public class ColumnPage extends BasePage{
	
	private ErrorHandler errorHandle = BasePage.getErrorHandler();

	public void clickManageColumns() {
		
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_COMMON_SETTINGSBTN, BrowserFactory.getMaxTimeWaitUIElement());
		Button btn = new Button(SPOGMenuTreePath.SPOG_COMMON_SETTINGSBTN);
		btn.click();		
		
		waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_MANAGESAVEDSEARCHES);
		waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_COLUMNLABELS);
	}
	
	public void chooseColumn(String column, boolean select) {
				
		List<WebElement> columnLabels = ElementFactory.getElements(SPOGMenuTreePath.SPOG_COMMON_COLUMNLABELS);
		String xpath = ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_COLUMNOPTIONSDIV).get(0).getValue();
		
		for (int i = 0; i < columnLabels.size(); i++) {
		
			try {
				
				WebElement columnEle = ElementFactory.getElementByXpath(xpath+"//label[@for='gridSettings"+i+"']//span[text()='"+column+"']");
				
				WebElement checkBoxEle = ElementFactory.getElementByXpath(xpath+"//input[@id='gridSettings"+i+"']");
				if (select) {
					if (!checkBoxEle.isSelected()) {
						columnEle.click();
					}		
				} else {
					if (checkBoxEle.isSelected()) {
						columnEle.click();
					}
				}				
			
				break;
	
			}catch (NoSuchElementException e) {
				if(i==columnLabels.size()-1) {
					e.printStackTrace();
					assertFalse(true, "column with name:"+column+" not found");
				}					
			}catch (Exception e) {
				e.printStackTrace();
				errorHandle.printErrorMessageInDebugFile(e.getMessage());
			}
		}
		
	}	
	
	public void chooseAllColumns(boolean select) {
		
		List<WebElement> columnLabels = ElementFactory.getElements(SPOGMenuTreePath.SPOG_COMMON_COLUMNLABELS);
		String xpath = ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_COLUMNOPTIONSDIV).get(0).getValue();
		
		for (int i = 0; i < columnLabels.size(); i++) {
		
			try {
						
				List<WebElement> checkBoxEles = ElementFactory.getElementsByXpath(xpath+"//input");
				if (select) {
					if (!checkBoxEles.get(i).isSelected()) {
						columnLabels.get(i).click();
					}	
				}else {
					if (checkBoxEles.get(i).isSelected()) {
						columnLabels.get(i).click();
					}
				}				
				
			}catch (NoSuchElementException e) {
				assertFalse(true, "No check box elements found");					
			}catch (Exception e) {
				e.printStackTrace();
				errorHandle.printErrorMessageInDebugFile(e.getMessage());
			}
		}	
	}
	
	public void checkEnabledColumns(ArrayList<String> expectedColumns, ExtentTest test) {

		HashMap<String, Integer> actualColumns = getTableHeaderOrder(getTableHeaders());

		for (int i = 0; i < expectedColumns.size(); i++) {

			if(actualColumns.containsKey(expectedColumns.get(i))) {
				test.log(LogStatus.INFO, "column with name:"+expectedColumns.get(i)+" availble in table");

			}else {
				if(i == expectedColumns.size()-1) {
					test.log(LogStatus.FAIL, "Column with name:"+expectedColumns.get(i)+" not found in table");
				}
			}
		}
	}
	
	public void checkDefaultColumns(ArrayList<String> defaultColumns, ExtentTest test) {
		
		HashMap<String, Integer> actualColumns = getTableHeaderOrder(getTableHeaders());

		if (actualColumns.size() == defaultColumns.size()) {
			for (int i = 0; i < defaultColumns.size(); i++) {
				if(actualColumns.containsKey(defaultColumns.get(i))) {
					test.log(LogStatus.INFO, "column with name:"+defaultColumns.get(i)+" availble in table");

				}else {
					if(i == defaultColumns.size()-1) {
						test.log(LogStatus.FAIL, "Column with name:"+defaultColumns.get(i)+" not found in table");
						assertFalse(true, "Column with name:"+defaultColumns.get(i)+" not found in table");
					}
				}
			}	
		}else {
			test.log(LogStatus.FAIL, "Actual column size:"+actualColumns.size()+" does not match with expected column size:"+defaultColumns.size());
			test.log(LogStatus.INFO, "Un select all the enabled columns and try");
			
			errorHandle.printErrorMessageInDebugFile("Actual column size:"+actualColumns.size()+" does not match with expected column size:"+defaultColumns.size());
			assertFalse(true);
		}			
	}
	
	public void checkColumnSelectedOrNot(String column, boolean selected, ExtentTest test) {
		
		
	List<WebElement> columnLabels = ElementFactory.getElements(SPOGMenuTreePath.SPOG_COMMON_COLUMNLABELS);
	String xpath = ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_COLUMNOPTIONSDIV).get(0).getValue();
	
	for (int i = 0; i < columnLabels.size(); i++) {
	
		try {
			
			WebElement columnEle = ElementFactory.getElementByXpath(xpath+"//label[@for='gridSettings"+i+"']//span[text()='"+column+"']");
			
			WebElement checkBoxEle = ElementFactory.getElementByXpath(xpath+"//input[@id='gridSettings"+i+"']");
			
			if (selected) {
				assertTrue(checkBoxEle.isSelected());
			} else {
				assertTrue(!checkBoxEle.isSelected());
			}
			break;

		}catch (NoSuchElementException e) {
			if(i==columnLabels.size()-1) {
				e.printStackTrace();
				assertFalse(true, "column with name:"+column+" not found");
			}					
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	}
	
	public boolean checkIsSettingsBtnExpanded() {
		
		WebElement settingsBtn = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_SETTINGSBTN);
		boolean isExpanded = Boolean.valueOf(settingsBtn.getAttribute("aria-expanded").toString());
		
		return isExpanded;
	}
	
	public boolean checkColumnExisted(String columnName) {
		
		HashMap<String, Integer> headers = getTableHeaderOrder(getTableHeaders());
		
		if(headers.containsKey(columnName))
			return true;
		
		return false;
	}
	
	public void clickOnColumnName(String columnName) {
		
		List<WebElement> headers = getTableHeaders();
		HashMap<String, Integer> headersOrder = getTableHeaderOrder(headers);
		
		headers.get(headersOrder.get(columnName)).click();		
	}
	
	public void checkColumnValues(ArrayList<String> expected, ArrayList<String> actual) {
		
		if (actual.size() == expected.size()) {
			
			for (int i = 0; i < actual.size(); i++) {
				assertEquals(actual.get(i), expected.get(i));
			}
			
		} else {
			assertFalse(true, "actual row count does not match with expected row count");
		}
		
	}
	
	public ArrayList<String> getValuesInSpecifiedColumn(String columnName) {
		
		ArrayList<String> Names = new ArrayList<>();
		
		List<WebElement> headers = getTableHeaders();
		HashMap<String, Integer> headerOrder = getTableHeaderOrder(headers);
		
		List<WebElement> rows = getRows();
		
		String xpath = "."+ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_TABLE_COLUMNS).get(0).getValue();
		
		for (int i = 0; i < rows.size(); i++) {
			WebElement row = rows.get(i);
			
			List<WebElement> cols = row.findElements(By.xpath(xpath));
			
			int a = headerOrder.get(columnName);
			
			String name = cols.get(a).getText();
			
			Names.add(name);	
		}			
		return Names;
	}
	
    public String getValueInSpecifiedColumnFromFirstRow(String columnName) {
		List<WebElement> headers = getTableHeaders();
		HashMap<String, Integer> headerOrder = getTableHeaderOrder(headers);
		String xpath = "."+ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_TABLE_COLUMNS).get(0).getValue();
		List<WebElement> cols = getRow().findElements(By.xpath(xpath));
		int a = headerOrder.get(columnName);
		String name = cols.get(a).getText();
		return name;
	}
    
    
	
	
}
