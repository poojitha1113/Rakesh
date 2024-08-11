package ui.spog.server;
import io.restassured.response.Response;
import ui.base.common.BrowserOperation;
import ui.base.common.SPOGMenuTreePath;
import ui.base.elements.Button;
import ui.base.elements.TextField;
import ui.base.factory.BrowserFactory;
import ui.base.factory.ElementFactory;
import ui.spog.pages.HomePage;
import ui.spog.pages.LoginPage;
import ui.spog.pages.SetPasswordPage;
import ui.spog.pages.analyze.BackupJobReportsPage;
import ui.spog.pages.analyze.ManagedReportSchedulesPage;
import ui.spog.pages.configure.BrandingPage;

import ui.spog.pages.configure.UserAccountsPage;
import ui.spog.pages.protect.CustomerAccountsPage;
import ui.spog.pages.protect.PolicyPage;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import ui.base.common.BasePage;
import org.openqa.selenium.Keys;

import com.relevantcodes.extentreports.ExtentTest;



public class BrandingHelperpage extends SPOGUIServer{
	static String accountsPage = "https://tcc.arcserve.com/configure/branding/all";


	public BrandingHelperpage(String browserType, int maxTimeWaitSec) {
		super(browserType, maxTimeWaitSec);
		// TODO Auto-generated constructor stub
	}
	/*
	 * malleswari.sykam
	 */
	public void createBradningwithCheck(String Orgname,String primaryColor,String  secondaryColor) {

		//Going to the branding page
		BrandingPage brandingPage = goToBrandingPage();	

		brandingPage.setBradningdetails(Orgname,primaryColor,secondaryColor);
		brandingPage.SaveChanges();
		brandingPage.checkBradningDetails(Orgname, primaryColor, secondaryColor );
	}

	/*
	 * malleswari.sykam
	 */
	public void createBradningEmailerwithCheck(String FromEmailName,
			String FromEmailAddress,
			String SalesChart,
			String SupportChart,
			String Facebook,
			String Twitter,
			String Linkedin,
			String SocialMediaPlatform,
			String ContactUs,
			String LegalNotice,
			String Privacy
			) {

		//BrandingPage brandingPage = goToBrandingPage();	
		BrandingPage brandingPage = goToBrandingPage();
		brandingPage.goToBrandingEmailerTab();

		brandingPage.setBrandingEmilerFileds(FromEmailName,FromEmailAddress,SalesChart,SupportChart,Facebook,Twitter,Linkedin,
				SocialMediaPlatform	,ContactUs,LegalNotice,Privacy);
		brandingPage.SaveChanges();

		brandingPage.getBrandingEmailerTabFileds(FromEmailName, FromEmailAddress, SalesChart, SupportChart, Facebook, Twitter, Linkedin, SocialMediaPlatform, ContactUs, LegalNotice, Privacy);

	}


	/**
	 * 
	 */


	public void  getBrandingFailwithcheck(String OrgName,String primaryColor,String secondaryColor, String errorMessgae) {
		BrandingPage BrandingPage = goToBrandingPage();

		if(OrgName!=null) {
			String result = BrandingPage.setOragnizationNameWithCheck(OrgName, errorMessgae);
			assertEquals(result, "pass");

		}

		if(primaryColor!=null) {
			String result = BrandingPage.setPriamryColorWithCheck(primaryColor, errorMessgae);
			assertEquals(result, "pass");
		}

		if(secondaryColor!=null) {
			String result = BrandingPage.setSecondaryColorwithCheck(secondaryColor, errorMessgae);
			assertEquals(result, "pass");


		}


		BrandingPage.clickCancelBtn();
	}


	
	/**
	 * @author Nagamalleswari.Sykam
	 * @param NavigationPage
	 * @param Orgname
	 * @param primaryColor
	 * @param secondaryColor
	 */
	public void createBrandingWhenNvaigateFromOtherPages(String NavigationPage,String Orgname,String primaryColor,String  secondaryColor) {
		
		checktheNavigationPage(NavigationPage);
		createBradningwithCheck(Orgname, primaryColor, secondaryColor);
	}
	
	/**
	 * 
	 * @param NavigationPage
	 * @param Orgname
	 * @param primaryColor
	 * @param secondaryColor
	 */
	public void createBrandingwithcheckWhenNvaigateFromOtherPages(String NavigationPage,String Orgname,String primaryColor,String  secondaryColor) {
		
		checktheNavigationPage(NavigationPage);
		getBrandingFailwithcheck(Orgname, primaryColor, secondaryColor,null);
	}
	
	
	
public void createBradningEmailerWhenNavigateFromOtherPages(
		String navigationPage,
		String fromEmailName,
		String fromEmailAddress,
		String salesChart,
		String supportChart,
		String facebook,
		String twitter,
		String linkedin,
		String locialMediaPlatform,
		String contactUs,
		String legalNotice,
		String privacy) {
	checktheNavigationPage(navigationPage);
	createBradningEmailerwithCheck(fromEmailName, fromEmailAddress, salesChart, supportChart, facebook, twitter, linkedin, locialMediaPlatform, contactUs, legalNotice,privacy);
}
	/**
	 * @author Nagamalleswari.Sykam
	 * @param NavigatePage
	 */

	public void checktheNavigationPage(String NavigatePage) {
		if (NavigatePage.contains("Backup")) {
			BackupJobReportsPage backupJobReprots = goToBackupJobReportsPage();
			
		}else if(NavigatePage.contains("Customer")){
			CustomerAccountsPage customer = goToCustomerAccountsPage();
		}else if(NavigatePage.contains("Policy")) {
			PolicyPage policyPage =goToPolicyPage();
		}else if(NavigatePage.contains("UserAccount")) {
			UserAccountsPage useraccountPage = goToUserAccountPage();
		}else if (NavigatePage.contains("ManageReports")) {
		ManagedReportSchedulesPage managedReportSchedulePage= goToManagedReportSchedulesPage();
		}else if (NavigatePage.contains("Entitlement")) {
			//EntitlementsPage entitlementPage= goToEntitlementsPage();
		}else if (NavigatePage.contains("Reports")) {
			goToReportspage();
		}
	}
	public void checkUploadLogo(String LogoPath,String FileType,int FileSize,ExtentTest test) {
		BrandingPage brandingPage = goToBrandingPage();
		brandingPage.uploadLogo(LogoPath,FileType,FileSize);
		
		//UserAccountsPage anotherPage = goToUserAccountPage();		
	}


	public void checkUploadLogInImageTest(String PortalURL,String LogoPath,String FileType,int FileSize,ExtentTest test) {
		BrandingPage brandingPage = goToBrandingPage();
		brandingPage.uploadLogInImage(PortalURL,LogoPath,FileType,FileSize);

	}
}
