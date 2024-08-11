package ui.spog.pages.configure;

import ui.base.common.BasePage;
import ui.base.common.SPOGMenuTreePath;
import ui.base.elements.Link;
import ui.base.factory.BrowserFactory;;

public class ConfigurePage extends BasePage {
	
	BrandingPage brandingPage=null; 
	public BrandingPage getBrandingPageObject() {
		if (brandingPage==null) {
			brandingPage = new BrandingPage();
		}		

		return brandingPage;
	}

	/**
	 *  @author SHUO.ZHANG
	 * @return
	 */
	public UserAccountsPage goToUserAccountsPage() {
		
		Link userAccountsLink = new Link(SPOGMenuTreePath.SPOG_CONFIGURE_USERACCOUNTS);
		userAccountsLink.click();
		waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_USERACCOUNTS_ADDUSER)  ;
		waitUntilElementAppear(SPOGMenuTreePath.SPOG_CONFIGURE_USERACCOUNTS_USERTABLE_HEADERS);
		waitForSeconds(3);
	    UserAccountsPage  	userAccountsPage = new UserAccountsPage();	   
	    return userAccountsPage;
	}
	

	public BrandingPage goToBrandingPage() {

		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING, BrowserFactory.getMaxTimeWaitUIElement());
		Link BrandingLink = new Link(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING);
		BrandingLink.click();
		
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_ORGANIZATIONNAME, BrowserFactory.getMaxTimeWaitUIElement());
		BrandingPage brandingPage= getBrandingPageObject();
		return brandingPage;
	}
	
	public RolesPage goToRolesPage() {
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_CONFIGURE_ROLES, BrowserFactory.getMaxTimeWaitUIElement());
		Link RolesLink = new Link(SPOGMenuTreePath.SPOG_CONFIGURE_ROLES);
		RolesLink.click();
		
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_CONFIGURE_ROLES, BrowserFactory.getMaxTimeWaitUIElement());
		RolesPage rolesPage =new RolesPage();
		return rolesPage;
	
		
	}

	
	public EntitlementsPage goTOEntitlementsPage() {
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_CONFIGURE_ENTITLEMENTS, BrowserFactory.getMaxTimeWaitUIElement());
		Link EntitlemnetsLink = new Link(SPOGMenuTreePath.SPOG_CONFIGURE_ENTITLEMENTS);
		EntitlemnetsLink.click();
		
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_CONFIGURE_ENTITLEMENTS, BrowserFactory.getMaxTimeWaitUIElement());
		EntitlementsPage entitlemnetsPage =new EntitlementsPage();
		return entitlemnetsPage;
		
	}
	
	
	public SourceGroupsPage goToSourceGroupsPage() {
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_CONFIGURE_SOURCEGROUPS, BrowserFactory.getMaxTimeWaitUIElement());
		Link SoucregroupsLink = new Link(SPOGMenuTreePath.SPOG_CONFIGURE_SOURCEGROUPS);
		SoucregroupsLink.click();
		
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_CONFIGURE_SOURCEGROUPS, BrowserFactory.getMaxTimeWaitUIElement());
		SourceGroupsPage soucregrouppage = new SourceGroupsPage();
		return soucregrouppage;
	}
	
	

}
