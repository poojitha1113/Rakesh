package ui.others.helper;

import static org.testng.Assert.assertTrue;

import genericutil.ErrorHandler;
import ui.base.common.BrowserOperation;
import ui.base.common.EmailMenuTreePath;
import ui.base.common.SPOGMenuTreePath;
import ui.base.elements.Button;
import ui.base.factory.BrowserFactory;
import ui.base.factory.ElementFactory;
import ui.others.pages.EmailPage;
import ui.spog.pages.HomePage;

public class EmailHelper {

	static String menuTreeFilePath = "./UI/ObjectRepository/EmailMenuTree.xml";
	BrowserOperation emailBrowser;
	static ErrorHandler errorHandle;
	EmailPage ep = null;
	
	public EmailHelper(String browserType, int maxTimeWaitSec)
	{
		BrowserFactory.setBrowseType(browserType);
		BrowserFactory.setDefaultMaxWaitTimeSec(maxTimeWaitSec);
		emailBrowser=new BrowserOperation(BrowserFactory.getBrowser());
	    ElementFactory.setDriver(BrowserFactory.getBrowser());
		ElementFactory.setMenuTreeFilePath(menuTreeFilePath);
		errorHandle = ErrorHandler.getErrorHandler();

	}
	
	/**
	 * @author shuo.zhang
	 */
	public void openHotMail(){
		
		errorHandle.printInfoMessageInDebugFile("************Invoking operation: openHotMail***************");
		boolean result=false;
	/*	emailBrowser.openUrl("https://login.live.com/");
	
		if (ElementFactory.checkElementExists(EmailMenuTreePath.MICROSOFTEMAIL_EMAILADDRESS,40)){
			result=true;
		}*/
		emailBrowser.openUrl("https://outlook.live.com/");
		int count=0;
		do{
			if (ElementFactory.checkElementExists(EmailMenuTreePath.MICROSOFTEMAIL_OUTLOOKSIGNIN,40)){
				Button outlookSigninBtn = new Button(EmailMenuTreePath.MICROSOFTEMAIL_OUTLOOKSIGNIN);
				outlookSigninBtn.click();
				if (ElementFactory.checkElementExists(EmailMenuTreePath.MICROSOFTEMAIL_EMAILADDRESS,40)){
					result=true;
				}
			}
			this.refresh();
			count++;
			
		}while((result==false) && (count<10) );
		
		ep = new EmailPage();
		assertTrue(result, "open hotmail successfully");
	}
	

	/**
	 * @author shuo.zhang
	 * @param email
	 * @param password
	 */
	public void loginMailboxAndOpenActivateAccountPage(String email, String password, String firstName, String lastName, String orgName, String createdByEmail){
		
		errorHandle.printInfoMessageInDebugFile("************Invoking operation: loginMailboxAndOpenActivateAccountPage***************");
		openHotMail();
		ep.loginHotMail(email, password);
		ep.openActivateAccountPage(firstName, lastName, orgName, email, createdByEmail);
	}
	
	public void close(){
		emailBrowser.close();
	}
	
	public void destroy(){
	
		BrowserFactory.destroyBrowser();
	}
	
	public void refresh(){
		emailBrowser.refresh();
	}
	
	
}
