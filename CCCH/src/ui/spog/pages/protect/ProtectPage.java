package ui.spog.pages.protect;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.server.handler.GetCurrentUrl;

import ui.base.common.BasePage;
import ui.base.common.SPOGMenuTreePath;
import ui.base.common.Url;
import ui.base.elements.Link;
import ui.base.factory.BrowserFactory;
import ui.base.factory.ElementFactory;


public class ProtectPage  extends BasePage{


	/**
	 *  @author SHUO.ZHANG
	 * @return
	 */
	public PolicyPage goToPolicyPage() {
		
		Link policyLink = new Link(SPOGMenuTreePath.SPOG_PROTECT_POLICY);
		policyLink.click();
		waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_COMMON_SAVEDSEARCHESLABEL);
	    PolicyPage  policyPage = new PolicyPage();	   
	    return policyPage;
	}
	
	/** For MSP to reach customer accounts page we need to click on Policy link only
	 * 
	 * @author Rakesh.Chalamala
	 * @return
	 */
	public CustomerAccountsPage goToCustomerAccountsPage() {
		
		if (!BrowserFactory.getCurrentPageUrl().equalsIgnoreCase(Url.customerAccounts)) {
			waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_PROTECT);
			Link protectLink = new Link(SPOGMenuTreePath.SPOG_PROTECT);
			protectLink.click();

			waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_PROTECT_ADDCUSTOMERACCOUNT, BrowserFactory.getMaxTimeWaitUIElement());
			assertTrue(ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_ADDCUSTOMERACCOUNT));	
		}
				
		CustomerAccountsPage  customerAccountsPage = new CustomerAccountsPage();	   
	    return customerAccountsPage;
	}
	
	public void viewAsCustomerEndUser(String accountName) {
    	waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_TABLE_ROWS);
		List<WebElement> rows = getRows();
		if (0 == rows.size()) {
			assertTrue(false, "There is no rows in the table");
		}
		for (int i = 0; i <= rows.size(); i++) {
			try {
				WebElement customerName = rows.get(i).findElement(By.xpath(".//div[@class='rt-td active-sort']//span[@id='undefined-organization_name-name']"));
				//WebElement customerName1 = customerName.findElement(By.xpath(".//div[@class='d-flex align-items-center']"));
				//WebElement customerName2 = customerName1.findElement(By.xpath(".//span"));
	    		if(customerName.getText().toString().equalsIgnoreCase(accountName)) {
	    			WebElement viewAsEndUser = rows.get(i).findElement(By.xpath(".//div[@class='rt-td customer-switch-cell smallSvg']//a"));
	    			viewAsEndUser.click();
	    			return;
	    		}
			} catch (NoSuchElementException e) {
				if (i == rows.size()) {
					assertTrue(false, "There is no matched row in the table");
				}
		    }
		}    	
    }
	
	public SourcePage goToSourcePage() {
		
		if (!BrowserFactory.getCurrentPageUrl().equalsIgnoreCase(Url.sources)) {
			waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_PROTECT);
			Link protectLink = new Link(SPOGMenuTreePath.SPOG_PROTECT);
			protectLink.click();
		}
		
		SourcePage  sourcePage = new SourcePage();	   
	    return sourcePage;
	}
	
	public DestinationsPage goToDestinationPage() {
		
		if (!BrowserFactory.getCurrentPageUrl().equalsIgnoreCase(Url.destinations)) {
			waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS);
			Link protectLink = new Link(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS);
			protectLink.click();

			waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_SELECTEDROWSLABEL);
			assertTrue(ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_COMMON_SELECTEDROWSLABEL));	
		}	
		DestinationsPage  DestinationsPage = new DestinationsPage();	
		return new DestinationsPage();
	}
	
	public RecoveredVMsPage goToRecoveredVMsPage() {

		if (!BrowserFactory.getCurrentPageUrl().equalsIgnoreCase(Url.RecoveredVMs)) {
			waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_PROTECT_RECOVEREDVMS);
			Link protectLink = new Link(SPOGMenuTreePath.SPOG_PROTECT_RECOVEREDVMS);
			protectLink.click();

			waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_SELECTEDROWSLABEL);
			assertTrue(ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_COMMON_SELECTEDROWSLABEL));	
		}
		
		return new RecoveredVMsPage();
	}
}
