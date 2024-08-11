package ui.spog.pages;

import org.openqa.selenium.WebElement;
import org.testng.Assert;

import genericutil.ErrorHandler;
import ui.base.common.BasePage;
import ui.base.common.SPOGMenuTreePath;
import ui.base.elements.Button;
import ui.base.elements.TextField;
import ui.base.factory.BrowserFactory;
import ui.base.factory.ElementFactory;

public class SetPasswordPage extends BasePage {

	private ErrorHandler errorHandle;
	public SetPasswordPage(){
		super();
		errorHandle = ErrorHandler.getErrorHandler();
	}
	
	public void createAccount(String password){
		
		errorHandle.printInfoMessageInDebugFile("**************createAccount*****************");
		try{
			BrowserFactory.switchToNewOpenedTab();
			waitUntilElementAppear(SPOGMenuTreePath.SPOG_SETPASSWORD_CREATEPASSWORD);
			waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_SETPASSWORD_CREATEPASSWORD,4 );
			
			TextField createPwdFd=new TextField(SPOGMenuTreePath.SPOG_SETPASSWORD_CREATEPASSWORD);
			createPwdFd.sendKeys(password);
			TextField confirmPwdFd=new TextField(SPOGMenuTreePath.SPOG_SETPASSWORD_CONFIRMPASSWORD);
			confirmPwdFd.sendKeys(password);
			
			WebElement agreeEle =   ElementFactory.getElement(SPOGMenuTreePath.SPOG_SETPASSWORD_AGREE);
			agreeEle.click();
			
			Button createAccount=new Button(SPOGMenuTreePath.SPOG_SETPASSWORD_CREATEACCOUNT);
			createAccount.click();
			
			waitUntilElementAppear(SPOGMenuTreePath.SPOG_MONITOR);
			if(!ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_MONITOR)){
				Assert.fail("after click create account, not jump to home page");
			}
			
		}catch(Exception e){
			e.printStackTrace();
			Assert.fail(e.getMessage(), e);
		
		}
		
		
	}
}
