package ui.spog.pages.protect;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

import com.gargoylesoftware.htmlunit.javascript.host.URL;

import genericutil.ErrorHandler;
import ui.base.common.BasePage;
import ui.base.common.BrowserOperation;
import ui.base.common.ContextualAction;
import ui.base.common.SPOGMenuTreePath;
import ui.base.common.TableHeaders;
import ui.base.common.ToastMessage;
import ui.base.common.UiElementNotFoundException;
import ui.base.common.Url;
import ui.base.elements.Button;
import ui.base.elements.ComboxList;
import ui.base.elements.Link;
import ui.base.elements.TextField;
import ui.base.factory.BrowserFactory;
import ui.base.factory.ElementFactory;

public class CustomerAccountsPage extends BasePage{
	
	private ErrorHandler errorHandle = BasePage.getErrorHandler();	
	
	/** To click on Add customer account button
	 * 
	 * @author Rakesh.Chalamala
	 * @return
	 */
	public void clickAddCustomerAccount() {
		errorHandle.printInfoMessageInDebugFile("click add customer account button");
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_PROTECT_ADDCUSTOMERACCOUNT, BrowserFactory.getMaxTimeWaitUIElement());
		Button addPolicyBtn = new Button(SPOGMenuTreePath.SPOG_PROTECT_ADDCUSTOMERACCOUNT);
		addPolicyBtn.click();
		
	}
	
	/** Sends the customer name to the text field
	 * 
	 * @author Rakesh.Chalamala
	 * @param customerName
	 */
	public void setCustomerName(String customerName) {
		errorHandle.printInfoMessageInDebugFile("set customer name");
		TextField policyNameEle = new TextField(SPOGMenuTreePath.SPOG_PROTECT_ADDCUSTOMERACCOUNT_CUSTOMERNAME);
		policyNameEle.clear();
		policyNameEle.setText(customerName);

	}
	
	/** Clicks on add customer button after setting the name
	 * 
	 * @author Rakesh.Chalamala
	 */
	public void clickAddCustomer() {
		
		errorHandle.printInfoMessageInDebugFile("click add customer button");
		Button addPolicyBtn = new Button(SPOGMenuTreePath.SPOG_PROTECT_ADDCUSTOMERACCOUNT_ADDCUSTOMER);
		addPolicyBtn.click();
	}
	
	public List<WebElement>  getCustomerTableHeaders(){
		 
		return ElementFactory.getElements(SPOGMenuTreePath.SPOG_COMMON_TABLE_HEADERS);
	}
	
	public HashMap<String, Integer> getTableHeaderOrder(List<WebElement> headerTable){
		
		HashMap<String, Integer> headerOrderMap = new HashMap<String, Integer> ();
		for(int i=0; i<headerTable.size();i++){
			WebElement eachHeader = headerTable.get(i);
			String text = eachHeader.getText();
			if((text!=null) && !text.equalsIgnoreCase("")){
				headerOrderMap.put(text, i);
			}
		}
		return headerOrderMap;
	}
	
	/** Returns the customer list from table
	 * 
	 * @return
	 */
	public List<WebElement>  getCustomerList(){
		
		waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_SEARCHRESULTFOR_MSG);
		return ElementFactory.getElements(SPOGMenuTreePath.SPOG_PROTECT_POLICY_POLICYTABLE_ROWS);
	}
	
	public boolean checkCustomerExists(String customerName) {
		boolean isExist = false;
		try{
			waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_TABLE_ROWS);
			WebElement tableBody = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_TABLE_BODY);
			WebElement customerElement = tableBody.findElement(By.xpath(".//span[text()='" + customerName+ "']"));
			assertEquals(customerElement.getText(), customerName);
			
			isExist=true;
		}catch(NoSuchElementException e){
			errorHandle.printErrorMessageInDebugFile("Customer with name: "+customerName+" does not exist");		
		}
		
		return isExist;
	}
	
	/** Verifies the customer details
	 * 
	 * @author Rakesh.Chalamala
	 * 
	 * @param customerName
	 * @param expectedInfo
	 */
	public void checkCustomerDetails(String custName, ArrayList<HashMap<String, Object>> expectedInfo){
		
		HashMap<String, Object> accountInfo = expectedInfo.get(0);
		System.out.println(accountInfo);
		
		try{
			
			waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_TABLE_ROWS);
			WebElement tableBody = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_TABLE_BODY);
			WebElement customerElement = tableBody.findElement(By.xpath(".//span[text()='" + custName+ "']"));
			customerElement.click();
			
			checkTabActive("Information");
			WebElement formEle = ElementFactory.getElement(SPOGMenuTreePath.SPOG_PROTECT_CUSTOMERACCOUNTS_CUSTOMERACCOUNTDETAILS_FORM);
			
			List<WebElement> contentEles = formEle.findElements(By.xpath(".//div[@class='content']"));
			
			//first row
			List<WebElement> contentItemEles = contentEles.get(0).findElements(By.xpath(".//div[contains(@class,'col-md-4')]"));
			
			String actTitle = contentItemEles.get(0).findElement(By.xpath(".//span[@class='content-item-title']")).getText();
			String actValue = contentItemEles.get(0).findElement(By.xpath(".//span[@class='content-item-option']")).getText();
			assertTrue(actTitle.equalsIgnoreCase(TableHeaders.status));
//			assertTrue(actValue.equalsIgnoreCase(accountInfo.get(TableHeaders.status).toString()));
			
			actTitle = contentItemEles.get(1).findElement(By.xpath(".//span[@class='content-item-title']")).getText();
			actValue = contentItemEles.get(1).findElement(By.xpath(".//span[@class='content-item-option']")).getText();
			assertTrue(actTitle.equalsIgnoreCase(TableHeaders.added_by));
			assertTrue(actValue.equalsIgnoreCase(accountInfo.get(TableHeaders.added_by).toString()));
			
			actTitle = contentItemEles.get(2).findElement(By.xpath(".//span[@class='content-item-title']")).getText();
			actValue = contentItemEles.get(2).findElement(By.xpath(".//input")).getAttribute("value");
			assertTrue(actTitle.equalsIgnoreCase(TableHeaders.customer_name));
			assertTrue(actValue.equalsIgnoreCase(accountInfo.get(TableHeaders.customer_name).toString()));
			
			//second row
			contentItemEles = contentEles.get(1).findElements(By.xpath(".//div[contains(@class,'col-md-4')]"));
			
			actTitle = contentItemEles.get(0).findElement(By.xpath(".//span[@class='content-item-title']")).getText();
			actValue = contentItemEles.get(0).findElement(By.xpath(".//span[@class='content-item-option']")).getText();
			assertTrue(actTitle.equalsIgnoreCase(TableHeaders.added_on));
			assertTrue(actValue.contains(accountInfo.get(TableHeaders.added_on).toString()));
			
			actTitle = contentItemEles.get(1).findElement(By.xpath(".//span[@class='content-item-title']")).getText();
			actValue = contentItemEles.get(1).findElement(By.xpath(".//div[@class='progress-content']")).getText();
			assertTrue(actTitle.equalsIgnoreCase(TableHeaders.cloud_direct_usage));
//			assertTrue(actValue.equalsIgnoreCase(accountInfo.get(TableHeaders.cloud_direct_usage).toString()));
			
			actTitle = contentItemEles.get(2).findElement(By.xpath(".//span[@class='content-item-title']")).getText();
			actValue = contentItemEles.get(2).findElement(By.xpath(".//div[@class='progress-content']")).getText();
			assertTrue(actTitle.equalsIgnoreCase(TableHeaders.cloud_hybrid_usage));
//			assertTrue(actValue.equalsIgnoreCase(accountInfo.get(TableHeaders.cloud_hybrid_usage).toString()));
			
			
			//third row
			contentItemEles = contentEles.get(2).findElements(By.xpath(".//div[contains(@class,'col-md-4')]"));
			
			actTitle = contentItemEles.get(0).findElement(By.xpath(".//span[@class='content-item-title']")).getText();
			actValue = contentItemEles.get(0).findElement(By.xpath(".//span[@class='content-item-option']")).getText();
			assertTrue(actTitle.equalsIgnoreCase(TableHeaders.total_sources));
			assertTrue(actValue.equalsIgnoreCase(accountInfo.get(TableHeaders.total_sources).toString()));
						
			HashMap<String, Integer> headerOrder = getTableHeaderOrder(ElementFactory.getElements(SPOGMenuTreePath.SPOG_COMMON_TABLE_HEADERS));
			List<WebElement> rows = getAssignedAdminsList();
		
			String xpath = ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_TABLE_ROWS).get(0).getValue();
			
			for (int i = 0, j=rows.size()-1; i <rows.size(); i++, j--) {
				WebElement eachRow = ElementFactory.getElementByXpath(xpath+"["+(i+1)+"]");
				xpath = "." + ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_TABLE_COLUMNS).get(0).getValue();
				List<WebElement> columns = eachRow.findElements(By.xpath(xpath));
								
				assertTrue(columns.get(headerOrder.get(TableHeaders.name)).getText().equalsIgnoreCase(expectedInfo.get(j).get(TableHeaders.name).toString()));
				assertTrue(columns.get(headerOrder.get(TableHeaders.role)).getText().equalsIgnoreCase(expectedInfo.get(j).get(TableHeaders.role).toString()));
				assertTrue(columns.get(headerOrder.get(TableHeaders.phone)).getText().equalsIgnoreCase(expectedInfo.get(j).get(TableHeaders.phone).toString()));
				assertTrue(columns.get(headerOrder.get(TableHeaders.email)).getText().equalsIgnoreCase(expectedInfo.get(j).get(TableHeaders.email).toString()));
				assertTrue(columns.get(headerOrder.get(TableHeaders.assigned_on)).getText().contains(expectedInfo.get(j).get(TableHeaders.assigned_on).toString()));
			}
						
		}catch(Exception e){
			e.printStackTrace();
			Assert.fail("exception", e);
		}
	}
	
	/** Clicks on customer name from table to view the details
	 * 
	 * @param customerName
	 */
	public void viewCustomerDetails(String customerName) {

		waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_TABLE_ROWS);
		WebElement tableBody = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_TABLE_BODY);
		WebElement customerElement = tableBody.findElement(By.xpath(".//span[text()='" + customerName+ "']"));
		customerElement.click();
		
		waitForMilSeconds(500);
		ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_CUSTOMERACCOUNTS_CUSTOMERACCOUNTDETAILS_CUSTOMERNAME);
	}
	
	/** Verifies the MSP account admin details in the customer details page
	 * 
	 * @author Rakesh.Chalamala
	 * @param customerName
	 * @param expectedInfo
	 */
	public void checkAssignedAdminDetails(String customerName, ArrayList<HashMap<String, Object>> expectedInfo) {
		
		viewCustomerDetails(customerName);
		
		waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_TABLE_ROWS);
		HashMap<String, Integer> headerOrder = getTableHeaderOrder(ElementFactory.getElements(SPOGMenuTreePath.SPOG_COMMON_TABLE_HEADERS));
		
		List<WebElement> rows = getAssignedAdminsList();
		int row_count = rows.size();
				
		for (int i = 0, j=row_count-1; i < row_count ; i++,j--) {
		
			String xpath = ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_TABLE_ROWS).get(0).getValue();
			WebElement eachRow = ElementFactory.getElementByXpath(xpath+"["+(i+1)+"]");
			
			xpath = "." + ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_TABLE_COLUMNS).get(0).getValue();
			List<WebElement> columns = eachRow.findElements(By.xpath(xpath));

			assertTrue(columns.get(headerOrder.get(TableHeaders.name)).getText().trim().equalsIgnoreCase(expectedInfo.get(j).get(TableHeaders.name).toString().trim()));
			assertTrue(columns.get(headerOrder.get(TableHeaders.role)).getText().equalsIgnoreCase(expectedInfo.get(j).get(TableHeaders.role).toString()));
			assertTrue(columns.get(headerOrder.get(TableHeaders.phone)).getText().equalsIgnoreCase(expectedInfo.get(j).get(TableHeaders.phone).toString()));
			assertTrue(columns.get(headerOrder.get(TableHeaders.email)).getText().equalsIgnoreCase(expectedInfo.get(j).get(TableHeaders.email).toString()));
			assertTrue(columns.get(headerOrder.get(TableHeaders.assigned_on)).getText().contains(expectedInfo.get(i).get(TableHeaders.assigned_on).toString()));	
				
		}
	}
	
	public List<WebElement> getAssignedAdminsList() {
		return ElementFactory.getElements(SPOGMenuTreePath.SPOG_COMMON_TABLE_ROWS);
	}
	
	public void checkTabActive(String tabName) {
		try {
			String flag = null;
			flag = ElementFactory.getElementByXpath("//a[text()='"+tabName+"']").getAttribute("class");
			assertTrue(flag.equalsIgnoreCase("active"), tabName+" is active");
		} catch (Exception e) {
			assertTrue(false, "Tab with name: "+tabName+"not found");
		}		
	}
	
	public void checkCustomerDetailsInTable(String customerName, HashMap<String, Object> expectedInfo) {
		
		searchByCustomerFullName(customerName);
		
		HashMap<String, Integer> customerTableHeaderOrder = getTableHeaderOrder(getTableHeaders());
				
		List<WebElement> rows = getCustomerList();
		for(int i=0; i<rows.size() ;i++) {
				WebElement eachRow = rows.get(i);
				String xpath = "." + ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_TABLE_COLUMNS).get(0).getValue();
				List<WebElement> columns = eachRow.findElements(By.xpath(xpath));
				
				assertTrue(columns.get(customerTableHeaderOrder.get(TableHeaders.customer_name)).getText().contains(expectedInfo.get(TableHeaders.customer_name).toString()));
				assertTrue(columns.get(customerTableHeaderOrder.get(TableHeaders.total_sources)).getText().contains(expectedInfo.get(TableHeaders.total_sources).toString()));
//				assertTrue(columns.get(customerTableHeaderOrder.get(TableHeaders.cloud_direct_usage)).getText().contains(expectedInfo.get(TableHeaders.cloud_direct_usage).toString()));
				assertTrue(columns.get(customerTableHeaderOrder.get(TableHeaders.cloud_hybrid_usage)).getText().contains(expectedInfo.get(TableHeaders.cloud_hybrid_usage).toString()));
				assertTrue(columns.get(customerTableHeaderOrder.get(TableHeaders.added_by)).getText().contains(expectedInfo.get(TableHeaders.added_by).toString()));
				assertTrue(columns.get(customerTableHeaderOrder.get(TableHeaders.added_on)).getText().contains(expectedInfo.get(TableHeaders.added_on).toString()));
				
		}
	}
	
	/** Update the specified customer name
	 * 
	 * @author Rakesh.Chalamala
	 * @param customerName
	 * @param customerNewName
	 */
	public void editCustomerName(String customerName, String customerNewName){
		
		try{
			WebElement tableBody = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_TABLE_BODY);
			WebElement custEle = tableBody.findElement(By.xpath(".//span[text()='" + customerName+ "']"));
			custEle.click();
			
			if(customerNewName!=null){
				getErrorHandler().printInfoMessageInDebugFile("edit customer name");
				WebElement firstNameEle = ElementFactory.getElement(SPOGMenuTreePath.SPOG_PROTECT_CUSTOMERACCOUNTS_CUSTOMERACCOUNTDETAILS_CUSTOMERNAME);
				firstNameEle.clear();
				firstNameEle.sendKeys(customerNewName);
						
				getErrorHandler().printInfoMessageInDebugFile("click save");
				Button saveBtn = new Button(SPOGMenuTreePath.SPOG_COMMON_SAVE);
				saveBtn.click();
				waitForSeconds(5);
			}

		}catch(Exception e){
			Assert.fail("exception", e);
			getErrorHandler().printErrorMessageInDebugFile(e.getStackTrace().toString());
			if(ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_CONFIGURE_USERACCOUNTS_USERACCOUNTDETAILS_CANCEL)){
				Button cancelBtn = new Button(SPOGMenuTreePath.SPOG_CONFIGURE_USERACCOUNTS_USERACCOUNTDETAILS_CANCEL);
				cancelBtn.click();
			}
			
		}
		
	}
	
	/** Searches by customer full name
	 * 
	 * @author Rakesh.Chalamala
	 * @param customerName
	 * @return
	 */
	public WebElement searchByCustomerFullName(String customerName) {
		
		waitUntilElementAppear(SPOGMenuTreePath.SPOG_PROTECT_CUSTOMERACCOUNTS_TOTALCUSTOMERACCOUNTS_COUNT);
		int totalCustomerCount = Integer.parseInt(ElementFactory.getElement(SPOGMenuTreePath.SPOG_PROTECT_CUSTOMERACCOUNTS_TOTALCUSTOMERACCOUNTS_COUNT).getText());
		
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_COMMON_SEARCHINPUT, BrowserFactory.getMaxTimeWaitUIElement());
		TextField searchFd = new TextField(SPOGMenuTreePath.SPOG_COMMON_SEARCHINPUT);
		searchFd.setText(customerName);
		searchFd.sendKeys(Keys.ENTER.toString());
		
		waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_SEARCHRESULTFOR_MSG);
		WebElement ele = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_SEARCHRESULTSFORDIV);
		assertTrue(ele.findElement(By.xpath(".//span[text()='"+customerName+"']")).isDisplayed());
				
		int customersCountAfterSearch = Integer.parseInt(ElementFactory.getElement(SPOGMenuTreePath.SPOG_PROTECT_CUSTOMERACCOUNTS_TOTALCUSTOMERACCOUNTS_COUNT).getText());
		
		for (int i = 0; i < BrowserFactory.getMaxTimeWaitUIElement(); i++) {
			if (customersCountAfterSearch < totalCustomerCount) {
				break;
			}else {
				waitForSeconds(1);
				continue;
			}
		}

		return getCustomerList().get(0);
	}
	
	public List<WebElement> searchCustomerByName(String customerName) {
		
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_COMMON_SEARCHINPUT, BrowserFactory.getMaxTimeWaitUIElement());
		TextField searchFd = new TextField(SPOGMenuTreePath.SPOG_COMMON_SEARCHINPUT);
		searchFd.setText(customerName);
		searchFd.sendKeys(Keys.ENTER.toString());
		
		waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_SEARCHRESULTFOR_MSG);
		WebElement ele = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_SEARCHRESULTSFORDIV);
		assertTrue(ele.findElement(By.xpath(".//span[text()='"+customerName+"']")).isDisplayed());
		
		return getCustomerList();
	}
	
		
	public void clickCustomerContextualActions(String customerName, String action) {
		
			List<WebElement> headers = getCustomerTableHeaders();
			HashMap<String, Integer> customerTableHeaderOrder = getTableHeaderOrder(headers);
			Set<String> keySet = customerTableHeaderOrder.keySet();
			Iterator<String> keyIter = keySet.iterator();
			int customerNameOrder =-1;
			while(keyIter.hasNext()){
				String headerName = keyIter.next();
				int order = customerTableHeaderOrder.get(headerName);	
				if(headerName.equalsIgnoreCase("Customer Name")){
					customerNameOrder=order;
					break;
				}
			}
			List<WebElement> rows = getCustomerList();
			for(int i=0; i<rows.size();i++) {
					WebElement eachRow = rows.get(i);
					String xpath = "." + ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_TABLE_COLUMNS).get(0).getValue();
					List<WebElement> columns = eachRow.findElements(By.xpath(xpath));
					
					if(columns.get(customerNameOrder).getText().contains(customerName)){
						xpath = "." + ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_CONTEXTUALACTIONS).get(0).getValue();
						columns.get(columns.size()-1).findElement(By.xpath(xpath)).click();
						waitForSeconds(2);
						if (action != null) {
							columns.get(columns.size()-1).findElement(By.xpath("//span[text()='"+action+"']")).click();;	
						}
						
						break;
					}
			}
	}
	
/*	public void clickCustomerAction(String action) {
		if (action.equalsIgnoreCase(ContextualAction.DELETE)) {
			errorHandle.printInfoMessageInDebugFile("click delete customer action");
			Button delete = new Button(SPOGMenuTreePath.SPOG_PROTECT_CUSTOMERACCOUNTS_CONTEXTUALACTIONS_DELETEACTION);
			delete.click();
			
			errorHandle.printInfoMessageInDebugFile("wait policy name textbox apppears");
			waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_PROTECT_CUSTOMERACCOUNTS_CONTEXTUALACTIONS_DELETEACTION_DELETE, 4);
		}else if (action.equalsIgnoreCase(ContextualAction.SET_USAGE_THRESHOLD)) {
			errorHandle.printInfoMessageInDebugFile("click customer set usage threshold action");
			Link set = new Link(SPOGMenuTreePath.SPOG_PROTECT_CUSTOMERACCOUNTS_CONTEXTUALACTIONS_SETUSAGETHRESHOLD);
			set.click();
			
			errorHandle.printInfoMessageInDebugFile("wait policy name textbox apppears");
			waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_COMMON_CANCEL, BrowserFactory.getMaxTimeWaitUIElement());
		}else if (action.equalsIgnoreCase(ContextualAction.ASSIGN_MSP_ACCOUNT_ADMIN)) {
			errorHandle.printInfoMessageInDebugFile("click customer set usage threshold action");
			Link set = new Link(SPOGMenuTreePath.SPOG_PROTECT_CUSTOMERACCOUNTS_CONTEXTUALACTIONS_ASSIGNMSPACCOUNTADMIN);
			set.click();
			
			errorHandle.printInfoMessageInDebugFile("wait policy name textbox apppears");
			waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_COMMON_CANCEL, BrowserFactory.getMaxTimeWaitUIElement());
		}else {
			assertTrue(false, "Invalid action: "+action);
		}
				
		
		
		
	}*/
	
	public void clickDelete() {
		errorHandle.printInfoMessageInDebugFile("click delete customer button");
		Button delete = new Button(SPOGMenuTreePath.SPOG_COMMON_DELETE);
		delete.click();		
		
		errorHandle.printInfoMessageInDebugFile("wait till toast message appears");
		waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_TOASTMESSAGE);
		
		assertTrue(ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_TOASTMESSAGE).getText().contains(ToastMessage.is_successfully_removed));		
	}
	
	/**
	 * @author Rakesh.Chalamala
	 * @return
	 */
	public String addCustomer(String customerName){
		
		String result = "fail";
//		waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_PROTECT_ADDCUSTOMERACCOUNT,4 );
		Button add=new Button(SPOGMenuTreePath.SPOG_PROTECT_ADDCUSTOMERACCOUNT);
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_PROTECT_ADDCUSTOMERACCOUNT,4);
		add.click();
		
		TextField custnameFd=new TextField(SPOGMenuTreePath.SPOG_PROTECT_ADDCUSTOMERACCOUNT_CUSTOMERNAME);
//		waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_PROTECT_ADDCUSTOMERACCOUNT_CUSTOMERNAME,4 );
		custnameFd.clear();
		custnameFd.sendKeys(customerName);
	
		Button submit=new Button(SPOGMenuTreePath.SPOG_PROTECT_ADDCUSTOMERACCOUNT_ADDCUSTOMER);
		submit.click();
		
		if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_ADDCUSTOMERACCOUNT,3)){
			 result="pass";
		}
		return result;
	}
	
	/**
	 * @author Rakesh.Chalamala
	 * @return
	 */
	public boolean isAddAccountBtnEnabled(){
		
		errorHandle.printInfoMessageInDebugFile("**************isAddAccountBtnEnabled*****************");
		if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_ADDCUSTOMERACCOUNT_ADDCUSTOMER)){
			 if(ElementFactory.getElement(SPOGMenuTreePath.SPOG_PROTECT_ADDCUSTOMERACCOUNT_ADDCUSTOMER).isEnabled()){
				 return true;
			 }else{
				 return false;
			 }
		}else{
			return false;
		}

	}
	
	/**
	 * @author Rakesh.Chalamala
	 * 
	 */
	public String addCustomerFail(String customerName, String errorMessage) {
		
		String result = "fail";
		
		if (customerName == null) {
			if (isAddAccountBtnEnabled()) {
				return result;
			}else {
				return result="pass";
			}
		}
		
		TextField custnameFd=new TextField(SPOGMenuTreePath.SPOG_PROTECT_ADDCUSTOMERACCOUNT_CUSTOMERNAME);
		custnameFd.setText(customerName);
		custnameFd.sendKeys(Keys.TAB.toString());
					
		customerName = customerName.trim();
		
		if (customerName.length() == 0) {
			if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_COMMON_ERROR_REQUIRED,BrowserFactory.getMaxTimeWaitUIElement())){
				errorHandle.printInfoMessageInDebugFile("Add account failed with message: "+SPOGMenuTreePath.SPOG_COMMON_ERROR_REQUIRED);
				result="pass";	 
			}
		}else if (customerName.length() < 3) {
			System.out.println();
			if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_COMMON_ERROR_MINLENGTH,BrowserFactory.getMaxTimeWaitUIElement())){
				errorHandle.printErrorMessageInDebugFile("Add account succeeded for failure case with message: "+SPOGMenuTreePath.SPOG_COMMON_ERROR_MINLENGTH);
				result="pass";
			}
		} else if (customerName.length() > 128) {
			if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_COMMON_ERROR_MAXLENGTH,BrowserFactory.getMaxTimeWaitUIElement())){
				errorHandle.printErrorMessageInDebugFile("Add account succeeded for failure case with message: "+SPOGMenuTreePath.SPOG_COMMON_ERROR_MAXLENGTH);
				result="pass";
			}
		}
		waitForMilSeconds(500);
		Button cancel = new Button(SPOGMenuTreePath.SPOG_COMMON_FORM_CANCEL);
		cancel.click();
		
		return result;		
	}
	
	public void checkDeleteCustomer(String customerName) {
		
		boolean result = ElementFactory.checkElementExists(".//span[text()='" + customerName+ "']");
		if (result) {
			errorHandle.printErrorMessageInDebugFile("Customer with name "+customerName+" still exists");
			assertFalse(result, "Customer with name "+customerName+" still exists");
		}else {
			errorHandle.printInfoMessageInDebugFile("Customer with name "+customerName+" deleted successfully");
			assertFalse(result, "Customer with name "+customerName+" deleted successfully");
		}
		
	}
	
	public void setUsageThreshold(String cdUsage, String cdCapacity,
									String chUsage, String chCapacity, boolean cancel) {
				
		//checking by default save button is disable or not
		assertTrue(ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_CUSTOMERACCOUNTS_SETUSAGETHRESHOLD_TITLE));
		assertFalse(ElementFactory.checkElementClickable(ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_FORM_SAVE)));
		assertTrue(ElementFactory.checkElementClickable(ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_FORM_CANCEL)));
		
		
		if (cdUsage!=null && !cdUsage.equals("")) {
			Button cdCapacityEle = new Button(SPOGMenuTreePath.SPOG_PROTECT_CUSTOMERACCOUNTS_SETUSAGETHRESHOLD_CDUSAGETHRESHOLD_CAPACITY);
			cdCapacityEle.click();
			
			cdCapacity = cdCapacity.toUpperCase();
			ElementFactory.getElementByXpath(".//span[text()='"+cdCapacity+"']").click();;
			
			TextField cdUsageEle = new TextField(SPOGMenuTreePath.SPOG_PROTECT_CUSTOMERACCOUNTS_SETUSAGETHRESHOLD_CDUSAGETHRESHOLD);
			cdUsageEle.setText(cdUsage);
		}
	
		if (chUsage!=null && !chUsage.equals("")) {
			ElementFactory.getElement(SPOGMenuTreePath.SPOG_PROTECT_CUSTOMERACCOUNTS_SETUSAGETHRESHOLD_TITLE).click();
			Button chCapacityEle = new Button(SPOGMenuTreePath.SPOG_PROTECT_CUSTOMERACCOUNTS_SETUSAGETHRESHOLD_CHUSAGETHRESHOLD_CAPACITY);
			chCapacityEle.click();
			
			chCapacity.toUpperCase();
			String xpath = ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_PROTECT_CUSTOMERACCOUNTS_SETUSAGETHRESHOLD_CHUSAGETHRESHOLD_DIV).get(0).getValue();
			ElementFactory.getElementByXpath(xpath+"//span[text()='"+chCapacity+"']").click();;
			
			TextField chUsageEle = new TextField(SPOGMenuTreePath.SPOG_PROTECT_CUSTOMERACCOUNTS_SETUSAGETHRESHOLD_CHUSAGETHRESHOLD);
			chUsageEle.setText(chUsage);
		}
		
		if (cancel) {
			Button cancelEle = new Button(SPOGMenuTreePath.SPOG_COMMON_FORM_CANCEL);
			cancelEle.click();
		}else {
			waitForMilSeconds(500);
			errorHandle.printInfoMessageInDebugFile("check Save button enables");
			assertTrue(ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_SAVE).isEnabled());
			
			Button saveEle = new Button(SPOGMenuTreePath.SPOG_COMMON_SAVE);
			saveEle.click();
		}
		
	}
	
	/** Check usage threshold for the customer in table
	 * 
	 * @param customerName
	 * @param cdUsage
	 * @param cdCapacity
	 * @param chUsage
	 * @param chCapacity
	 * @param cancel
	 */
	public void checkSetUsageThreshold(String customerName, String cdUsage, String cdCapacity, String chUsage, String chCapacity, boolean cancel) {
		
		errorHandle.printInfoMessageInDebugFile("check Usage Threshold values in table");
		List<WebElement> rows = ElementFactory.getElements(SPOGMenuTreePath.SPOG_COMMON_TABLE_ROWS);
		
		this.waitForSeconds(2);
		
		if (cdUsage != null && cdCapacity != null) {
			String actCDInfo = rows.get(0).findElement(By.xpath("//div[@id='undefined-show_cd_used_capacity-content']")).getText();
			assertTrue(actCDInfo.contains(cdUsage));
			assertTrue(actCDInfo.contains(cdCapacity));	
		}
		if (chUsage != null && chCapacity != null) {
			String actCHInfo = rows.get(0).findElement(By.xpath("//div[@id='undefined-show_ch_used_capacity-content']")).getText();
			assertTrue(actCHInfo.contains(chUsage));
			assertTrue(actCHInfo.contains(chCapacity));
		}
		
	}
	
	public void assignMspAdmin(String adminEmail) {
		
		//checking default items
		ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_CUSTOMERACCOUNTS_ASSIGNMSPACCOUNTADMIN_TITLE);
		assertFalse(ElementFactory.getElement(SPOGMenuTreePath.SPOG_PROTECT_CUSTOMERACCOUNTS_ASSIGNMSPACCOUNTADMIN_ASSIGN).isEnabled());
		assertTrue(ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_CANCEL).isEnabled());
		
		String search_string=null;
		String[] admins = adminEmail.split(",");
		for (int i = 0; i < admins.length; i++) {
			
			search_string = admins[i];
			TextField searchAdminFd = new TextField(SPOGMenuTreePath.SPOG_PROTECT_CUSTOMERACCOUNTS_ASSIGNMSPACCOUNTADMIN_SEARCHADMIN);
			searchAdminFd.setText(search_string);
			waitForMilSeconds(500);
			
			searchAdminFd.sendKeys(Keys.TAB.toString());
			waitForMilSeconds(500);
			
			Button addBtn = new Button(SPOGMenuTreePath.SPOG_PROTECT_CUSTOMERACCOUNTS_ASSIGNMSPACCOUNTADMIN_ADD);
			addBtn.click();
			
			waitForMilSeconds(500);
			String xpath = ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_PROTECT_CUSTOMERACCOUNTS_ASSIGNMSPACCOUNTADMIN_CONTENTDIV).get(0).getValue();	
			String selectedAdmin = ElementFactory.getElementByXpath(xpath+"//div[@id='"+i+"']/label").getText();
			
			assertTrue(selectedAdmin.toLowerCase().contains(search_string.toLowerCase()));
		}
		
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_PROTECT_CUSTOMERACCOUNTS_ASSIGNMSPACCOUNTADMIN_ASSIGN, BrowserFactory.getMaxTimeWaitUIElement());
		Button assignBtn = new Button(SPOGMenuTreePath.SPOG_PROTECT_CUSTOMERACCOUNTS_ASSIGNMSPACCOUNTADMIN_ASSIGN);
		assignBtn.click();
				
		waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_TOASTMESSAGE);
		assertEquals(ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_TOASTMESSAGE).getText().toLowerCase(), ToastMessage.admins_assigned_successfully.toLowerCase());
	}
	
	public void checkViewAsEndUserAdmin(String customerName) {
		
		waitUntilElementAppear(SPOGMenuTreePath.SPOG_PROTECT_CUSTOMERACCOUNTS_VIEWASENDUSERADMIN_BANNERDIV);
		String xpath = ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_PROTECT_CUSTOMERACCOUNTS_VIEWASENDUSERADMIN_BANNERDIV).get(0).getValue();
		String actContent = ElementFactory.getElementByXpath(xpath+"/span").getText();
		
		assertTrue(actContent.toLowerCase().contains(customerName.toLowerCase()));
		
		String actURL = BrowserFactory.getCurrentPageUrl();
		assertTrue(actURL.toLowerCase().contains(Url.sources.toLowerCase()));
	}
	
	public void navigateFromAccountToMsp() {
		
		String xpath = ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_PROTECT_CUSTOMERACCOUNTS_VIEWASENDUSERADMIN_BANNERDIV).get(0).getValue();
		ElementFactory.getElementByXpath(xpath+"//span[text()='here']").click();
		
		String actURL = BrowserFactory.getCurrentPageUrl();
		assertTrue(actURL.toLowerCase().contains(Url.customerAccounts.toLowerCase()));		
	}
	
	public void clearAllFilters() {
		if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_COMMON_CLEARALLFILTERS)) {
			waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_COMMON_CLEARALLFILTERS, BrowserFactory.getMaxTimeWaitUIElement());
			ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_CLEARALLFILTERS).click();
			
			assertFalse(ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_COMMON_SEARCHRESULTFOR_MSG));
			assertFalse(ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_COMMON_CLEARALLFILTERS));	
		}
		
	}
}
