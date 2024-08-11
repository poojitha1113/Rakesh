package ui.spog.pages.configure;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.sql.Driver;
import java.util.HashMap;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.google.common.io.Files;

import genericutil.ErrorHandler;
import ui.base.common.BasePage;
import ui.base.common.BrowserOperation;
import ui.base.common.SPOGMenuTreePath;
import ui.base.elements.Button;
import ui.base.elements.Link;
import ui.base.elements.TextField;
import ui.base.factory.BrowserFactory;
import ui.base.factory.ElementFactory;

public class BrandingPage  extends BasePage{

	//	public static String facebook = "Facebook";
	public static String fromemailname = "From Email Name";
	public static String fromemaileaddress="From Email Address";
	public static String saleschat = "Sales Chat";
	public static String supportchat = "Support Chat";
	public static String supportsales = "support_sales";
	public static String socialmediaplatform ="Social Media Platform";
	public static String facebook = "Facebook";
	public static String twitter= "Twitter";
	public static String linkedin = "Linkedin";
	public static String contactus = "Contact Us";
	public static String legalnotice= "Legal Notice";
	public static String privacy = "Privacy";
	public static String  brandingmessgae=null;





	private ErrorHandler errorHandle = BasePage.getErrorHandler();	


	/* Set the Organization name 
	 * @author Nagamalleswari.sykam
	 * 
	 */
	public void setBradningdetails(String organizationName,String primaryColor,String secondaryColor) {

		//Set the organization name if the organization name doesn't have null

		if(organizationName!=null ){

			errorHandle.printInfoMessageInDebugFile("set Oraganization name");
			//Waiting in the your branding page until the organization name is usable
			waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_ORGANIZATIONNAME);
			//Verifying the Textfiled of organization name 
			TextField OraganziationName = new TextField(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_ORGANIZATIONNAME);
			OraganziationName.click();
			//Clear the organization name 
			OraganziationName.clear();
			//Send the input of organization name 
			OraganziationName.setText(organizationName);
		}	

		if(primaryColor!=null) {

			//Print the  given text
			errorHandle.printInfoMessageInDebugFile("setPriamryColour");
			//Wait  until the Primary color filed is usable
			waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_PRIMARYCOLOR);
			//Verify the text field of primary color
			TextField PrimaryColorFd = new TextField(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_PRIMARYCOLOR);
			PrimaryColorFd.click();
			//clear the Exist primary color
			PrimaryColorFd.clear();
			//Set the input of primary color
			PrimaryColorFd.setText(primaryColor);
		}
		if(secondaryColor!=null) {
			//Print the  given text
			errorHandle.printInfoMessageInDebugFile("set Secondary Colour");
			//Wait  until the Primary color filed is usable
			waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_SECONDARYCOLOR);
			//Verify the text field of primary color
			TextField secondaryFd = new TextField(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_SECONDARYCOLOR);
			secondaryFd.click();
			//clear the Exist primary color
			secondaryFd.clear();
			//Set the input of primary color
			secondaryFd.setText(secondaryColor);

		}

		//		
	}




	/* Click on the Save Changes
	 * @author Nagamalleswari.sykam
	 * 
	 */

	public void SaveChanges() {

		errorHandle.printInfoMessageInDebugFile("Click on the save changes button");
		//Waiting until the Save button is clickable 
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_SAVE, BrowserFactory.getMaxTimeWaitUIElement());
		//Getting the web element of the save button
		WebElement SaveBtn= ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_SAVE);
		//Click on the save button
		SaveBtn.click();
		waitForMilSeconds(5);

	}


	/* Checking the branding details
	 * @author nagamalleswari.Sykam
	 * 
	 * Verify the Organization name 
	 * 
	 * Verify the Primary color
	 * 
	 * Verify the secondary color
	 * 
	 */
	public void  checkBradningDetails(String orgName, String primarycolor,String secondaryColor) {

		if (orgName != null) {
			waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_ORGANIZATIONNAME);
			TextField orgNameEle = new TextField(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_ORGANIZATIONNAME);
			String actOrgName = orgNameEle.getValue();
			assertEquals(actOrgName, orgName);	
		}

		if (primarycolor != null) {
			waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_PRIMARYCOLOR);

			String actprimarycolor = new TextField(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_PRIMARYCOLOR).getValue();

			assertEquals(actprimarycolor, primarycolor);	
		}

		if(secondaryColor!=null) {
			waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_SECONDARYCOLOR);
			String  actsecondaryColorEle = new TextField(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_SECONDARYCOLOR).getValue();
			assertEquals(actsecondaryColorEle, secondaryColor);	
		}

		//		clickCancelBtn();
	}



	public String setOragnizationNameWithCheck(String OrgName, String errorMessgae) {

		String result="fail";
		waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_ORGANIZATIONNAME,BrowserFactory.getMaxTimeWaitUIElement() );
		TextField orgnameFd=new TextField(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_ORGANIZATIONNAME);
		orgnameFd.click();
		orgnameFd.clear();
		orgnameFd.setText(OrgName);

		orgnameFd.sendKeys(Keys.TAB.toString());
		OrgName.trim();

		if (OrgName.length()==0) {
			if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_REQUIREDERROR,BrowserFactory.getMaxTimeWaitUIElement())){
				errorHandle.printInfoMessageInDebugFile("The message: "+SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_REQUIREDERROR);
				result="pass";	 
			}
		}else if (OrgName.length() < 3) {
			if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_MINLENGTHERROR,BrowserFactory.getMaxTimeWaitUIElement())){
				errorHandle.printErrorMessageInDebugFile("The message: "+SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_MINLENGTHERROR);
				result="pass";
			}
		}else if (OrgName.length()>128) {
			if(ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_EXCEEDORGNAMEERROR,BrowserFactory.getMaxTimeWaitUIElement()))
				errorHandle.printErrorMessageInDebugFile("The message: "+SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_EXCEEDORGNAMEERROR);
			result="pass";
		}else {
			assertFalse(ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_REQUIREDERROR,BrowserFactory.getMaxTimeWaitUIElement()));
			assertFalse(ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_MINLENGTHERROR,BrowserFactory.getMaxTimeWaitUIElement()));
			assertFalse(ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_EXCEEDORGNAMEERROR,BrowserFactory.getMaxTimeWaitUIElement()));
		}

		return result;
	}

	public String setPriamryColorWithCheck(String primaryColor, String errorMessgae) {

		String result= "fail";
		waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_PRIMARYCOLOR,BrowserFactory.getMaxTimeWaitUIElement());
		TextField primaryColorFd=new TextField(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_PRIMARYCOLOR);
		waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_PRIMARYCOLOR,BrowserFactory.getMaxTimeWaitUIElement() );
		primaryColorFd.clear();
		primaryColorFd.sendKeys(primaryColor);
		primaryColorFd.sendKeys(Keys.TAB.toString());
		primaryColor.trim();
		if (primaryColor.length()!=7) {
			if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_INVALIDHEXCODE,BrowserFactory.getMaxTimeWaitUIElement())){
				errorHandle.printInfoMessageInDebugFile("The message: "+SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_INVALIDHEXCODE);
				result="pass";	 
			}

		}
		return result;
	}

	public String setSecondaryColorwithCheck(String secondaryColor, String errorMessgae) {

		String result= null;
		waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_SECONDARYCOLOR,BrowserFactory.getMaxTimeWaitUIElement() );
		TextField secondaryColorFd=new TextField(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_SECONDARYCOLOR);
		waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_SECONDARYCOLOR,BrowserFactory.getMaxTimeWaitUIElement());
		secondaryColorFd.clear();
		secondaryColorFd.sendKeys(secondaryColor);
		secondaryColorFd.sendKeys(Keys.TAB.toString());
		secondaryColor.trim();
		if (secondaryColor.length()!=7) {
			if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_INVALIDHEXCODE,BrowserFactory.getMaxTimeWaitUIElement())){
				errorHandle.printInfoMessageInDebugFile("The message: "+SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_INVALIDHEXCODE);
				result="pass";	 
			}

		}


		return result;
	}


	public  void clickCancelBtn()
	{
		waitForMilSeconds(5);
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_COMMON_CANCEL,BrowserFactory.getMaxTimeWaitUIElement());
		WebElement cancelBtn=ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_CANCEL);
		cancelBtn.click();
	}

	/* Validating the FormEmailaddress
	 * 
	 */



	public void goToBrandingEmailerTab() {


		errorHandle.printDebugMessageInDebugFile("click on Emails tab");
		waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_EMAILTAB,BrowserFactory.getMaxTimeWaitUIElement());

		ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_EMAILTAB).click();
		waitUntilElementAppear(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_EMAILMESSAGE);
		assertTrue(ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_EMAILMESSAGE));

	}
	public void  setBrandingEmilerFileds(String FormEmailerName,
			String FromEmailAddress,
			String SalesChart,
			String SupportChat,
			String Facebook,
			String Twitter,
			String Linkedin,
			String SocialMediaPlatform,
			String ContactUs,
			String LegalNotice,
			String Privacy
			) {

		HashMap<String, Object> defaultInfo = getDefaultBrandingDetails(); 


		if (FormEmailerName!=null)
		{

			errorHandle.printDebugMessageInDebugFile("The FormEmail address");
			waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_FOREMAILERNAME, BrowserFactory.getMaxTimeWaitUIElement());
			TextField  fromEmailNameFd =  new TextField(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_FOREMAILERNAME);
			fromEmailNameFd.clear();
			fromEmailNameFd.setText(FormEmailerName);
			System.out.println(FormEmailerName);


		}
		if(FromEmailAddress!=null) 
		{
			errorHandle.printDebugMessageInDebugFile("The From email address");
			waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_FOREMAILADRESS, BrowserFactory.getMaxTimeWaitUIElement());
			TextField  FromEmailAddressFd=  new TextField(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_FOREMAILADRESS);
			FromEmailAddressFd.clear();
			FromEmailAddressFd.setText(FromEmailAddress);


		}

		if( SalesChart!=null)
		{
			errorHandle.printDebugMessageInDebugFile(" Set the sales chart");
			waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_SALESCHART, BrowserFactory.getMaxTimeWaitUIElement());
			TextField  SalesChartFd=  new TextField(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_SALESCHART);
			SalesChartFd.click();
			SalesChartFd.clear();
			SalesChartFd.setText(SalesChart);

		}
		if( SupportChat!=null) {
			errorHandle.printDebugMessageInDebugFile(" Set the Support chart");
			waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_SUUPPORTCHART, BrowserFactory.getMaxTimeWaitUIElement());
			TextField  SupportChartFd=  new TextField(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_SUUPPORTCHART);
			SupportChartFd.clear();
			SupportChartFd.setText(SupportChat);


		}
		if(Facebook!=null)
		{
			errorHandle.printDebugMessageInDebugFile(" Set the Facebook Field");
			waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_FACEBOOKLINK, BrowserFactory.getMaxTimeWaitUIElement());
			TextField  FacebookFd=  new TextField(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_FACEBOOKLINK);
			FacebookFd.clear();
			FacebookFd.setText(Facebook);



		}

		if( Twitter!=null) {

			errorHandle.printDebugMessageInDebugFile(" Set the Twitter Field");
			waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_TWITTERLINK, BrowserFactory.getMaxTimeWaitUIElement());
			TextField  TwitterFd=  new TextField(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_TWITTERLINK);
			TwitterFd.clear();
			TwitterFd.setText(Twitter);

		}

		if( Linkedin!=null) {

			errorHandle.printDebugMessageInDebugFile(" Set the Linkedin Field");
			waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_LINKEDINLINK, BrowserFactory.getMaxTimeWaitUIElement());
			TextField  LinkedinFd=  new TextField(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_LINKEDINLINK);
			LinkedinFd.clear();
			LinkedinFd.setText(Linkedin);
			System.out.println(Linkedin);

		}
		if( SocialMediaPlatform!=null) {

			errorHandle.printDebugMessageInDebugFile(" Set the SocialMediaPlatform Field");
			waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_SOCIALMEDIAPLATFORM, BrowserFactory.getMaxTimeWaitUIElement());
			TextField  SocialMediaPlatformFd=  new TextField(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_SOCIALMEDIAPLATFORM);
			SocialMediaPlatformFd.clear();
			SocialMediaPlatformFd.setText(SocialMediaPlatform);

		}
		if( ContactUs!=null) {

			errorHandle.printDebugMessageInDebugFile(" Set the Contact Us Field");
			waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_CONTACTUS, BrowserFactory.getMaxTimeWaitUIElement());
			TextField  ContactUsFd=  new TextField(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_CONTACTUS);
			ContactUsFd.clear();
			ContactUsFd.setText(ContactUs);

		}
		if( LegalNotice!=null) {

			errorHandle.printDebugMessageInDebugFile(" Set the LegalNoticeField");
			waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_LEGALNOTICE, BrowserFactory.getMaxTimeWaitUIElement());
			TextField  LegalNoticeFd=  new TextField(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_LEGALNOTICE);
			LegalNoticeFd.clear();
			LegalNoticeFd.setText(LegalNotice);

		}
		if( Privacy!=null) {

			errorHandle.printDebugMessageInDebugFile(" Set the Privacy Field");
			waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_PRIVACY, BrowserFactory.getMaxTimeWaitUIElement());
			TextField  PrivacyFd=  new TextField(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_PRIVACY);
			PrivacyFd.clear();
			PrivacyFd.setText(Privacy);

		}

	}

	public void getBrandingEmailerTabFileds(String FormEmailerName,String FromEmailAddress,
			String SalesChart,
			String SupportChart,
			String Facebook,
			String Twitter,
			String Linkedin,
			String SocialMediaPlatform,
			String ContactUs,
			String LegalNotice,
			String Privacy  ) {

		HashMap<String, Object> defaultInfo = getDefaultBrandingDetails();

		if(FormEmailerName!=null &&!(FormEmailerName.isEmpty()))
		{
			waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_FOREMAILERNAME, BrowserFactory.getMaxTimeWaitUIElement());
			String  fromEmailNameFd =  new TextField(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_FOREMAILERNAME).getValue();
			assertEquals(FormEmailerName, fromEmailNameFd);

			if (FormEmailerName.isEmpty()) {

				waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_FOREMAILERNAME, BrowserFactory.getMaxTimeWaitUIElement());
				String  actfromEmailNameFd =  new TextField(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_FOREMAILERNAME).getValue();
				String  DefaultFromemailerName = defaultInfo.get(fromemailname).toString();
				assertEquals(actfromEmailNameFd, DefaultFromemailerName);

			}else {

				System.out.println("The value is not a empty string");
			}

		}
		if(FromEmailAddress!=null &&!(FromEmailAddress.isEmpty()))
		{
			waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_FOREMAILADRESS, BrowserFactory.getMaxTimeWaitUIElement());
			String FromEmailAddressFd=  new TextField(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_FOREMAILADRESS).getValue();
			assertEquals(FromEmailAddress, FromEmailAddressFd);

			if (FromEmailAddress.isEmpty()) {

				waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_FOREMAILADRESS, BrowserFactory.getMaxTimeWaitUIElement());
				String  actFromEmailAddressFd =  new TextField(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_FOREMAILADRESS).getValue();
				String  DefaultFromEmailAddress = defaultInfo.get(fromemaileaddress).toString();
				assertEquals(actFromEmailAddressFd, DefaultFromEmailAddress);

			}else {

				System.out.println("The value is not a empty string");
			}
		}

		if(SalesChart!=null &&!(SalesChart.isEmpty())) 
		{

			waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_SALESCHART, BrowserFactory.getMaxTimeWaitUIElement());
			String   SalesChartFd=  new TextField(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_SALESCHART).getValue();
			assertEquals(SalesChart, SalesChartFd);
			if (SalesChart.isEmpty()) {

				waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_SALESCHART, BrowserFactory.getMaxTimeWaitUIElement());
				String  actSalesChartFd =  new TextField(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_SALESCHART).getValue();
				String  DefaultSalesChartName = defaultInfo.get(saleschat).toString();
				assertEquals(actSalesChartFd, DefaultSalesChartName);

			}else {

				System.out.println("The value is not a empty string");
			}

		}

		if(SupportChart!=null &&!(SupportChart.isEmpty()))  {
			errorHandle.printDebugMessageInDebugFile(" Verify the actula and expected Supportchart details");
			waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_SUUPPORTCHART, BrowserFactory.getMaxTimeWaitUIElement());
			String  SupportChartFd=  new TextField(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_SUUPPORTCHART).getValue();
			assertEquals(SupportChart, SupportChartFd);
			if (SupportChart.isEmpty()) {

				waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_SUUPPORTCHART, BrowserFactory.getMaxTimeWaitUIElement());
				String  actSupportChartFd =  new TextField(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_SUUPPORTCHART).getValue();
				String  DefaultSupportChartName = defaultInfo.get(supportchat).toString();
				assertEquals(actSupportChartFd, DefaultSupportChartName);

			}else {

				System.out.println("The value is not a empty string");
			}
		}

		if(Facebook!=null&&!(Facebook.isEmpty())) {

			errorHandle.printDebugMessageInDebugFile(" Verify the actual and expcted Facebook Field");
			waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_FACEBOOKLINK, BrowserFactory.getMaxTimeWaitUIElement());
			String  FacebookFd=  new TextField(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_FACEBOOKLINK).getValue();
			assertEquals(FacebookFd, Facebook);
			if (Facebook.isEmpty()) {

				waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_FACEBOOKLINK, BrowserFactory.getMaxTimeWaitUIElement());
				String  actFacebookFd =  new TextField(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_FACEBOOKLINK).getValue();
				String  DefaultFacebookName = defaultInfo.get(facebook).toString();
				assertEquals(actFacebookFd, DefaultFacebookName);

			}else {

				System.out.println("The value is not a empty string");
			}
		}

		if(Twitter!=null&&!(Twitter.isEmpty())) {

			errorHandle.printDebugMessageInDebugFile(" Verify the actual and expcted Twitter Field");
			waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_TWITTERLINK, BrowserFactory.getMaxTimeWaitUIElement());
			String  TwitterFd=  new TextField(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_TWITTERLINK).getValue();
			assertEquals(Twitter, TwitterFd);
			if (Twitter.isEmpty()) {

				waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_TWITTERLINK, BrowserFactory.getMaxTimeWaitUIElement());
				String  actTwitterFd =  new TextField(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_TWITTERLINK).getValue();
				String  DefaultTwitterName = defaultInfo.get(twitter).toString();
				assertEquals(actTwitterFd, DefaultTwitterName);

			}else {

				System.out.println("The value is not a empty string");
			}
		}

		if(Linkedin!=null&&!(Linkedin.isEmpty())) {
			errorHandle.printDebugMessageInDebugFile(" Verify the actual and expcted Linkedin Field");
			waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_LINKEDINLINK, BrowserFactory.getMaxTimeWaitUIElement());
			String LinkedinFd=  new TextField(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_LINKEDINLINK).getValue();
			assertEquals(LinkedinFd, Linkedin);
			if (Linkedin.isEmpty()) {

				waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_LINKEDINLINK, BrowserFactory.getMaxTimeWaitUIElement());
				String  actLinkedinFd =  new TextField(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_LINKEDINLINK).getValue();
				String  DefaultLinkedinName = defaultInfo.get(linkedin).toString();
				assertEquals(actLinkedinFd, DefaultLinkedinName);

			}else {

				System.out.println("The value is not a empty string");
			}
		}

		if(SocialMediaPlatform!=null && !(SocialMediaPlatform.isEmpty())) {
			errorHandle.printDebugMessageInDebugFile(" Set the SocialMediaPlatform Field");
			waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_SOCIALMEDIAPLATFORM, BrowserFactory.getMaxTimeWaitUIElement());
			String  SocialMediaPlatformFd=  new TextField(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_SOCIALMEDIAPLATFORM).getValue();
			assertEquals(SocialMediaPlatform, SocialMediaPlatformFd);

			if (SocialMediaPlatform.isEmpty()) {

				waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_SOCIALMEDIAPLATFORM, BrowserFactory.getMaxTimeWaitUIElement());
				String  actSocialMediaPlatformFd =  new TextField(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_SOCIALMEDIAPLATFORM).getValue();
				String  DefaultSocialMediaPlatformName = defaultInfo.get(socialmediaplatform).toString();
				assertEquals(actSocialMediaPlatformFd, DefaultSocialMediaPlatformName);

			}else {

				System.out.println("The value is not a empty string");
			}
		}

		if(ContactUs!=null && !(ContactUs.isEmpty())) {
			errorHandle.printDebugMessageInDebugFile(" Set the Contact Us Field");
			waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_CONTACTUS, BrowserFactory.getMaxTimeWaitUIElement());
			String  ContactUsFd=  new TextField(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_CONTACTUS).getValue();
			assertEquals(ContactUs, ContactUsFd);

			if (ContactUs.isEmpty()) {

				waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_CONTACTUS, BrowserFactory.getMaxTimeWaitUIElement());
				String  actContactUsFd =  new TextField(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_CONTACTUS).getValue();
				String  DefaultContactUsName = defaultInfo.get(contactus).toString();
				assertEquals(actContactUsFd, DefaultContactUsName);

			}else {

				System.out.println("The value is not a empty string");
			}
		}
		if(LegalNotice!=null && !(LegalNotice.isEmpty())) {

			errorHandle.printDebugMessageInDebugFile(" Set the LegalNoticeField");
			waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_LEGALNOTICE, BrowserFactory.getMaxTimeWaitUIElement());
			String  LegalNoticeFd=  new TextField(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_LEGALNOTICE).getValue();
			assertEquals(LegalNotice, LegalNoticeFd);

			if (LegalNotice.isEmpty()) {

				waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_LEGALNOTICE, BrowserFactory.getMaxTimeWaitUIElement());
				String  actLinkedinFd =  new TextField(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_LEGALNOTICE).getValue();
				String  DefaultLinkedinName = defaultInfo.get(legalnotice).toString();
				assertEquals(actLinkedinFd, DefaultLinkedinName);

			}else {

				System.out.println("The value is not a empty string");
			}
		}

		if(Privacy!=null &&!(Privacy.isEmpty())) {

			errorHandle.printDebugMessageInDebugFile(" Set the Privacy Field");
			waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_PRIVACY, BrowserFactory.getMaxTimeWaitUIElement());
			String  PrivacyFd=  new TextField(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_PRIVACY).getValue();
			assertEquals(Privacy, PrivacyFd);

			if (Privacy.isEmpty()) {

				waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_PRIVACY, BrowserFactory.getMaxTimeWaitUIElement());
				String  actPrivacyFd =  new TextField(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_PRIVACY).getValue();
				String  DefaultPrivacyname = defaultInfo.get(privacy).toString();
				assertEquals(actPrivacyFd, DefaultPrivacyname);

			}else {

				System.out.println("The value is not a empty string");
			}
		}

		waitForMilSeconds(5);

	}



	public void uploadLogo(String uploadlogopath,String FileType,int FileSize) {

		HashMap<String, Object> defaultInfo = getDefaultBrandingDetails();
		errorHandle.printDebugMessageInDebugFile("Set the Upload logo");
		waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_UPLOADLOGO, BrowserFactory.getMaxTimeWaitUIElement());
		Button UploadlogoPath =new Button(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_UPLOADLOGO);
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_UPLOADLOGO, BrowserFactory.getMaxTimeWaitUIElement());
		UploadlogoPath.click();
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_CHOOSEPHOTO, BrowserFactory.getMaxTimeWaitUIElement());
		Button ChoosePhotoPath =new Button(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_CHOOSEPHOTO);
		ChoosePhotoPath.click();

		String FileSizemessgae= ElementFactory.getElements(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_VALIDFILESIZE).toString();
		System.out.println(FileSizemessgae);



		try {
			Runtime.getRuntime().exec(uploadlogopath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(FileType.contains("jpg")&& FileType.contains("png")&&1572864>=FileSize ) {

			waitForSeconds(6);
			waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_UPDATEPHOTO);
			waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_UPDATEPHOTO, BrowserFactory.getMaxTimeWaitUIElement());
			WebElement UpadtePhotoBtn =ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_UPDATEPHOTO);
			UpadtePhotoBtn.click();

		}

		else {
			waitForMilSeconds(6);

			waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_COMMON_CANCEL);
			waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_CANCEL, BrowserFactory.getMaxTimeWaitUIElement());
			WebElement CancelBtn= ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_CANCEL);
			CancelBtn.click();
		}
	}




	public void uploadLogInImage( String PortalURL,String uploadloginImagepath,String FileType,int FileSize) {

		boolean isSelected=false;
		errorHandle.printDebugMessageInDebugFile("Set the Portal Url");
		waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_PORTALURL, BrowserFactory.getMaxTimeWaitUIElement());

		//Verifying the Textfiled of organization name 
		TextField PortalUrlFd = new TextField(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_PORTALURL);
		PortalUrlFd.click();
		PortalUrlFd.clear();
		PortalUrlFd.setText(PortalURL);

		//Button UploadlogoPath =new Button(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_UPLOADLOGO);
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_LOGINBTN, BrowserFactory.getMaxTimeWaitUIElement());
		Button LoginBtn =new Button(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_LOGINBTN);
		LoginBtn.click();

		//Unchcek the Use same logo as Your Brand Section
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_USESAMELOGOASYOURBRANDSECTION, BrowserFactory.getMaxTimeWaitUIElement());
		WebElement CheckBox = ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_USESAMELOGOASYOURBRANDSECTION);

		if(CheckBox.isSelected()) {
			CheckBox.click();
		}

		//UploadLoginImage 

		waitForSeconds(2);
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_UPLOADLOGINIMAGE, BrowserFactory.getMaxTimeWaitUIElement());
		WebElement UpadtePhotoBtn =ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_UPLOADLOGINIMAGE);
		UpadtePhotoBtn.click();


		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_CHOOSEPHOTO, BrowserFactory.getMaxTimeWaitUIElement());
		Button ChoosePhotoPath =new Button(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_CHOOSEPHOTO);
		ChoosePhotoPath.click();

		waitForSeconds(5);
		try {
			Runtime.getRuntime().exec(uploadloginImagepath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		waitForSeconds(12);
		if((FileType.contains("jpg")||FileType.contains("png"))&&1572864>=FileSize ) {
			waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_UPLOADLOGO, BrowserFactory.getMaxTimeWaitUIElement());
			WebElement UpadteLogInBtn =ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_UPLOADLOGO);
			UpadteLogInBtn.click();

		}

		else {

			//waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_COMMON_CANCEL, BrowserFactory.getMaxTimeWaitUIElement());
			//WebElement cancelBtn= ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_CANCEL);

			//cancelBtn.click();
		}

		//Unchcek the Use same logo as Your Brand Section
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_USESAMELOGOASYOURBRANDSECTION, BrowserFactory.getMaxTimeWaitUIElement());
		WebElement logoCheckBox = ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_USESAMELOGOASYOURBRANDSECTION);

		if(logoCheckBox.isSelected()) {
			logoCheckBox.click();
		}


		//Click on the 
		//waitForSeconds(2);
		//waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_COMMON_SAVECHANGES, BrowserFactory.getMaxTimeWaitUIElement());
		//WebElement SaveChangesBtn= ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_SAVECHANGES);
		//SaveChangesBtn.click();
	}


	public HashMap<String,Object> getDefaultBrandingDetails (){
		HashMap<String, Object> DefaulValues = new HashMap<>();



		DefaulValues.put(BrandingPage.fromemailname, ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_FOREMAILERNAME).getAttribute("value"));
		DefaulValues.put(BrandingPage.fromemaileaddress, ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_FOREMAILADRESS).getAttribute("value"));
		DefaulValues.put(BrandingPage.saleschat,ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_SALESCHART).getAttribute("value"));
		DefaulValues.put(BrandingPage.supportchat,ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_SUUPPORTCHART).getAttribute("value"));
		DefaulValues.put(BrandingPage.facebook,ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_FACEBOOKLINK).getAttribute("value"));
		DefaulValues.put(BrandingPage.twitter,ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_TWITTERLINK).getAttribute("value"));
		DefaulValues.put(BrandingPage.linkedin,ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_LINKEDINLINK).getAttribute("value"));
		DefaulValues.put(BrandingPage.socialmediaplatform,ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_SOCIALMEDIAPLATFORM).getAttribute("value"));
		DefaulValues.put(BrandingPage.legalnotice,ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_LEGALNOTICE).getAttribute("value"));
		DefaulValues.put(BrandingPage.privacy,ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_PRIVACY).getAttribute("value"));
		DefaulValues.put(BrandingPage.contactus,ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_CONTACTUS).getAttribute("value"));

		return DefaulValues;
	}


	public void uploadLoginimage() {
		//Unchcek the Use same logo as Your Brand Section
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_USESAMELOGOASYOURBRANDSECTION, BrowserFactory.getMaxTimeWaitUIElement());
		Button UseSamelogoCheckBtn =new Button(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_USESAMELOGOASYOURBRANDSECTION);
		UseSamelogoCheckBtn.click();

	}




}




