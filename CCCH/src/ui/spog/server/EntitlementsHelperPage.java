package ui.spog.server;

import java.util.ArrayList;
import java.util.HashMap;

import ui.spog.pages.configure.EntitlementsPage;

public class EntitlementsHelperPage extends SPOGUIServer {

	public EntitlementsHelperPage(String browserType, int maxTimeWaitSec) {
		super(browserType, maxTimeWaitSec);
		// TODO Auto-generated constructor stub
	}

	public void activateOrder( String orderid,String fulfillmentNumber,ArrayList<String> CDcomposeinfo,ArrayList<String> CHcomposeinfo) {
		EntitlementsPage entitlemnetsPage = goToEntitlementsPage();
		entitlemnetsPage.clickActiavteNewOrderBtn();
		entitlemnetsPage.activateNewOrder(orderid,fulfillmentNumber);
		entitlemnetsPage.clickActivateBtn();
		entitlemnetsPage.checkEntitlementsDetails(orderid,fulfillmentNumber,CDcomposeinfo,CHcomposeinfo);
	}

	public void CancelOrderactivation( String orderid,String fulfillmentNumber,ArrayList<String> CDcomposeinfo,ArrayList<String> CHcomposeinfo) {
		EntitlementsPage entitlemnetsPage = goToEntitlementsPage();
		entitlemnetsPage.activateNewOrder(orderid,fulfillmentNumber);
		entitlemnetsPage.cancelorderActiavtionBtn();
	}

	public void CloseOrderactivation( String orderid,String fulfillmentNumber,ArrayList<String> CDcomposeinfo,ArrayList<String> CHcomposeinfo) {
		EntitlementsPage entitlemnetsPage = goToEntitlementsPage();
		entitlemnetsPage.activateNewOrder(orderid,fulfillmentNumber);
		entitlemnetsPage.closeorderActiavtionmenu();
	}
}
