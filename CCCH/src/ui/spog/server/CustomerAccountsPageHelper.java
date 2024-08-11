package ui.spog.server;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import com.fasterxml.jackson.core.sym.Name;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import ui.base.common.BrowserOperation;
import ui.base.common.ContextualAction;
import ui.base.common.SPOGMenuTreePath;
import ui.base.common.UiElementNotFoundException;
import ui.base.factory.BrowserFactory;
import ui.base.factory.ElementFactory;
import ui.spog.pages.HomePage;
import ui.spog.pages.protect.CustomerAccountsPage;

public class CustomerAccountsPageHelper extends SPOGUIServer{

	static String accountsPage = "https://tcc.arcserve.com/protect/customer-accounts/all";
		
	public CustomerAccountsPageHelper(String browserType, int maxTimeWaitSec) {
		super(browserType, maxTimeWaitSec);
	}
	
	/** add customer and check for customer existence in table
	 * 
	 * @author Rakesh.Chalamala
	 * @param custName
	 * @param test
	 */
	public void addCustomersWithCheck(String custName, ExtentTest test) {
		
		CustomerAccountsPage customerAccountsPage = goToCustomerAccountsPage();
		
		String[] Names = custName.split(",");
		for (int i = 0; i < Names.length; i++) {
			test.log(LogStatus.INFO, "Click on add customer account button");
			customerAccountsPage.clickAddCustomerAccount();
			customerAccountsPage.waitForMilSeconds(500);
			
			assertTrue(ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_ADDCUSTOMERACCOUNT_TITLE));
			
			test.log(LogStatus.INFO, "Enter customer name: "+Names[i]);
			customerAccountsPage.setCustomerName(Names[i]);						
				
			test.log(LogStatus.INFO, "Click add button");
			customerAccountsPage.clickAddCustomer();
			
			customerAccountsPage.waitForSeconds(2);
			test.log(LogStatus.INFO, "Check for customer with name: "+Names[i]+" in the table");
			assertTrue(customerAccountsPage.checkCustomerExists(Names[i]));
			
			test.log(LogStatus.PASS, "Customer with name: "+Names[i]+" added successfully");
		}
	}
	
	/** add customer fail for invalid cases with less customer name input
	 * 
	 * @author Rakesh.Chalamala
	 * @param custName
	 * @param errorMessage
	 */
	public void addCustomerFail(String custName, String errorMessage, ExtentTest test) {
	
		CustomerAccountsPage customerAccountsPage = goToCustomerAccountsPage();
		
		test.log(LogStatus.INFO, "Click on add customer account button");
		customerAccountsPage.clickAddCustomerAccount();
			
		test.log(LogStatus.INFO, "add customer account");
		String result = customerAccountsPage.addCustomerFail(custName, errorMessage);
		assertEquals(result, "pass");
		test.log(LogStatus.PASS, "test case passed");
		
	}
	
	/** Delete the specified customer
	 * 
	 * @param customerName
	 * @param test
	 */
	public void deleteCustomerAccount(String customerName, ExtentTest test) {

		CustomerAccountsPage customerAccountsPage = goToCustomerAccountsPage();
			
		test.log(LogStatus.INFO, "Click on customer contextual action: "+ContextualAction.DELETE);
		customerAccountsPage.clickContextualActionForSpecifiedElement(customerName,ContextualAction.DELETE);
		
		test.log(LogStatus.INFO, "Click delete");
		customerAccountsPage.clickDelete();	
		customerAccountsPage.waitForSeconds(3);

		assertFalse(customerAccountsPage.checkCustomerExists(customerName));
		
		test.log(LogStatus.INFO, "Customer with name: "+customerName+" deleted succesfully");
	}
	
	public void deleteCustomerAccount(String customerName) {
		CustomerAccountsPage customerAccountsPage = goToCustomerAccountsPage();
		
		customerAccountsPage.clickContextualActionForSpecifiedElement(customerName, ContextualAction.DELETE);
		
		customerAccountsPage.clickDelete();	
		
		customerAccountsPage.clearAllFilters();
		assertFalse(customerAccountsPage.checkCustomerExists(customerName));
		
		errorHandle.printInfoMessageInDebugFile("Customer with name: "+customerName+" deleted successfully.");
	}
	
	/** Set usage threshold for the specified customer
	 * 
	 * @author Rakesh.Chalamala
	 * @param customerName
	 * @param cdUsage
	 * @param cdCapacity
	 * @param chUsage
	 * @param chCapacity
	 * @param cancel
	 * @param test
	 */
	public void setUsageThresholdWithCheck(String customerName, String cdUsage, String cdCapacity, String chUsage, String chCapacity, boolean cancel, ExtentTest test) {
		CustomerAccountsPage customerAccountsPage = goToCustomerAccountsPage();

		if (customerAccountsPage.checkCustomerExists(customerName)) {
			
			test.log(LogStatus.INFO, "Click on customer contextual action: "+ContextualAction.SET_USAGE_THRESHOLD);
			customerAccountsPage.clickContextualActionForSpecifiedElement(customerName, ContextualAction.SET_USAGE_THRESHOLD);
			customerAccountsPage.waitForSeconds(1);
			
			
			test.log(LogStatus.INFO, "Set usage threshold as CD usage: "+cdUsage+", CD capacity: "+cdCapacity+", CH usage: "+chUsage+", CH capacity: "+chCapacity);
			customerAccountsPage.setUsageThreshold(cdUsage,cdCapacity,chUsage,chCapacity,cancel);
			if (!cancel) {
				test.log(LogStatus.INFO, "Check the usage threshold values");
				customerAccountsPage.checkSetUsageThreshold(customerName, cdUsage,cdCapacity,chUsage,chCapacity,true);	
			}
				
		} else {
			test.log(LogStatus.ERROR, "Customer with name: "+customerName+" does not exist");
			errorHandle.printErrorMessageInDebugFile("Customer with name: "+customerName+" does not exist");
			assertTrue(customerAccountsPage.checkCustomerExists(customerName));
		}
		
	}
	
	/** Assign the list of account adimns to the specified customer 
	 * 
	 * @author Rakesh.Chalamala
	 * @param customerName
	 * @param adminEmail
	 * @param expectedInfo
	 * @param test
	 */
	public void assignMspAccountAdminWithCheck(String customerName, String adminEmail,ArrayList<HashMap<String, Object>> expectedInfo, ExtentTest test) {

		CustomerAccountsPage customerAccountsPage = goToCustomerAccountsPage();
				
		if (customerAccountsPage.checkCustomerExists(customerName)) {
			
			test.log(LogStatus.INFO, "Click on customer contextual action: "+ContextualAction.ASSIGN_MSP_ACCOUNT_ADMIN);
			customerAccountsPage.clickContextualActionForSpecifiedElement(customerName,ContextualAction.ASSIGN_MSP_ACCOUNT_ADMIN);
			
			test.log(LogStatus.INFO, "Assign the admin with email: "+adminEmail+" to customer "+customerName);
			customerAccountsPage.assignMspAdmin(adminEmail);
			
			customerAccountsPage.checkAssignedAdminDetails(customerName, expectedInfo);
			
		}else {
			test.log(LogStatus.ERROR, "Customer with name: "+customerName+" does not exist");
			errorHandle.printErrorMessageInDebugFile("Customer with name: "+customerName+" does not exist");
			assertTrue(customerAccountsPage.checkCustomerExists(customerName));
		}
	}
	
	/** Check the view as end user admin
	 * 
	 * @author Rakesh.Chalamala
	 * @param customerName
	 * @param test
	 */
	public void viewAsEndUserAdminWithCheck(String customerName, ExtentTest test) {
		
		CustomerAccountsPage customerAccountsPage = goToCustomerAccountsPage();
		
		if (customerAccountsPage.checkCustomerExists(customerName)) {
			
			test.log(LogStatus.INFO, "Click on customer contextual action: "+ContextualAction.VIEW_AS_ENDUSER_ADMIN);
			customerAccountsPage.clickContextualActionForSpecifiedElement(customerName,ContextualAction.VIEW_AS_ENDUSER_ADMIN);
			
			test.log(LogStatus.INFO, "Check the view as end user admin");
			customerAccountsPage.checkViewAsEndUserAdmin(customerName);
			
			customerAccountsPage.navigateFromAccountToMsp();
			
		}else {
			test.log(LogStatus.ERROR, "Customer with name: "+customerName+" does not exist");
			errorHandle.printErrorMessageInDebugFile("Customer with name: "+customerName+" does not exist");
			assertTrue(customerAccountsPage.checkCustomerExists(customerName));
		}
	}
	
	/** Search by customer name and validate the result in table
	 * 
	 * @author Rakesh.Chalamala
	 * @param search_string
	 * @param customerNames
	 */
	public void searchCustomerNameWithCheck(String search_string, ArrayList<String> customerNames) {
		CustomerAccountsPage customerAccountsPage = goToCustomerAccountsPage();
		
		List<WebElement> result = customerAccountsPage.searchCustomerByName(search_string);
		for (int i = 0; i < result.size(); i++) {
			WebElement row = result.get(i);
			for (int j = 0; j < customerNames.size(); j++) {
				try{
					if (j!=customerNames.size()) {
						row.findElement(By.xpath("//span[text()='"+customerNames.get(j)+"']"));
						break;	
					}else {
						assertTrue(false, "Customers with names:"+customerNames+" not found in search results.");
					}
					
				}catch (NoSuchElementException e) {
					continue;
				}catch (Exception e) {
					e.printStackTrace();
				}
			}			
		}
	}
	
	/** verify the customer details in customer table and in details page of customer
	 * 
	 * @author Rakesh.Chalamala
	 * @param customerName
	 * @param expectedInfo
	 * @param test
	 */
	public void verifyCustomerDetails(String customerName, ArrayList<HashMap<String, Object>> expectedInfo, ExtentTest test) {
		CustomerAccountsPage customerAccountsPage = goToCustomerAccountsPage();
		
		test.log(LogStatus.INFO, "Check customer details in the table");
		customerAccountsPage.checkCustomerDetailsInTable(customerName, expectedInfo.get(0));
		
		test.log(LogStatus.INFO, "Check customer details in the Information page of customer");
		customerAccountsPage.checkCustomerDetails(customerName, expectedInfo);
	
		test.log(LogStatus.PASS, "Customer account details validated successfully.");
		customerAccountsPage.waitForSeconds(1);
	}
	
}
