package ui.spog.pages.configure;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.seleniumhq.jetty9.security.authentication.SpnegoAuthenticator;

import ui.base.common.BasePage;
import ui.base.common.SPOGMenuTreePath;
import ui.base.factory.BrowserFactory;
import ui.base.factory.ElementFactory;

import ui.spog.server.SPOGUIServer;

public class EntitlementsPage  extends BasePage {

	public void clickActivateBtn() {
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_CONFIGURE_ENTITLEMENTS_ACTIVATE,BrowserFactory.getMaxTimeWaitUIElement());
		WebElement ActivateBtn =ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_ENTITLEMENTS_ACTIVATE);
		ActivateBtn.click();

	}
	public void clickActiavteNewOrderBtn() {
		//Click on new order
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_CONFIGURE_ENTITLEMENTS_ACTIVATENEWORDER,BrowserFactory.getMaxTimeWaitUIElement());
		WebElement ActivateNewOrderBtn =ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_ENTITLEMENTS_ACTIVATENEWORDER);
		ActivateNewOrderBtn.click();


	}
	
	public void cancelorderActiavtionBtn() {
		//Click on new order
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_CONFIGURE_ENTITLEMENTS_CANCEL,BrowserFactory.getMaxTimeWaitUIElement());
		WebElement CancelOrderActiavtion =ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_ENTITLEMENTS_CANCEL);
		CancelOrderActiavtion.click();


	}
	
	public void closeorderActiavtionmenu() {
		//Click on new order
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_CONFIGURE_ENTITLEMENTS_CLOSEACTIVATEORDERMENU,BrowserFactory.getMaxTimeWaitUIElement());
		WebElement closeorderactivationmenu =ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_ENTITLEMENTS_CLOSEACTIVATEORDERMENU);
		closeorderactivationmenu.click();


	}
	
	
	

	public void activateNewOrder(String orderid, String fulfilmentNumber) {

		waitUntilElementAppear(SPOGMenuTreePath.SPOG_CONFIGURE_ENTITLEMENTS);
		clickActiavteNewOrderBtn();
		//Enter orderId and 

		if(!(orderid==null&&orderid.contains(""))){
			waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_CONFIGURE_ENTITLEMENTS_ORDERID, BrowserFactory.getMaxTimeWaitUIElement());
			WebElement OrderIdBtn =ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_ENTITLEMENTS_ACTIVATENEWORDER);
			OrderIdBtn.click();
			OrderIdBtn.clear();
			OrderIdBtn.sendKeys(orderid);
		}
		//Enter fillfullment Number
		if(!(fulfilmentNumber==null&&fulfilmentNumber.contains(""))){
			waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_CONFIGURE_ENTITLEMENTS_ORDERID, BrowserFactory.getMaxTimeWaitUIElement());
			WebElement filFullMentBtn =ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_ENTITLEMENTS_ACTIVATENEWORDER);
			filFullMentBtn.click();
			filFullMentBtn.clear();
			filFullMentBtn.sendKeys(fulfilmentNumber);
		}
		

	}
	

	

	public void checkEntitlementsDetails(String sku, String fulfilmentNumber,ArrayList<String> CDexpInfo,ArrayList<String> CHexpInfo) {

		String[] skuInfo = sku.split("_");

		List<WebElement>  summaryEles = ElementFactory.getElements(SPOGMenuTreePath.SPOG_CONFIGURE_ENTITLEMENTS_SUMMARYDIV);

	
		
		WebElement CDEntitlementEle = 	summaryEles.get(1);
		WebElement CHEntitlementEle = 	summaryEles.get(2);

		try {
			CDEntitlementEle.findElements(By.xpath(".//span[text()='Entitlements for Cloud Direct']"));

			List<WebElement> EntitleEles = CDEntitlementEle.findElements(By.xpath(".//span[@class='content-item-title']"));
			List<WebElement> EntitleEleValues = CDEntitlementEle.findElements(By.xpath(".//span[@class='content-item-option']"));


			for (int i = 0; i < EntitleEles.size(); i++) {

				String actTitle  = EntitleEles.get(i).getText();
				String actValue = EntitleEleValues.get(i).getText();

				String[] expectedValues = CDexpInfo.get(i).split(";");

				assertEquals(actTitle, expectedValues[0]);
				assertEquals(actValue, expectedValues[1]);

			}
		}catch (NoSuchElementException e) {
			assertFalse(true, e.getMessage());
		}
	

	
	try {
		CHEntitlementEle.findElements(By.xpath(".//span[text()='Entitlements for Cloud Hybrid']"));

		List<WebElement> EntitleEles = CHEntitlementEle.findElements(By.xpath(".//span[@class='content-item-title']"));
		List<WebElement> EntitleEleValues = CHEntitlementEle.findElements(By.xpath(".//span[@class='content-item-option']"));


		for (int i = 0; i < EntitleEles.size(); i++) {

			String actTitle  = EntitleEles.get(i).getText();
			String actValue = EntitleEleValues.get(i).getText();

			String[] expectedValues = CHexpInfo.get(i).split(";");

			assertEquals(actTitle, expectedValues[0]);
			assertEquals(actValue, expectedValues[1]);

		}
	}catch (NoSuchElementException e) {
		assertFalse(true, e.getMessage());
	}
}
	
	
	



	
	public List<WebElement> informationdetails() {
		List<WebElement> informationEle =ElementFactory.getElements(SPOGMenuTreePath.SPOG_CONFIGURE_ENTITLEMENTS_INFORMATIONDIV);
		return informationEle;
	}


}



