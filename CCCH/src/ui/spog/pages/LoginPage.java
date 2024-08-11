package ui.spog.pages;

import genericutil.ErrorHandler;
import ui.base.common.BasePage;
import ui.base.common.SPOGMenuTreePath;
import ui.base.elements.Button;
import ui.base.elements.TextField;
import ui.base.factory.ElementFactory;

public class LoginPage extends BasePage{
	private ErrorHandler errorHandle;
	public LoginPage(){
		super();
		errorHandle = ErrorHandler.getErrorHandler();
	}
	
	/**
	 * @author shuo.zhang
	 * @param username
	 * @param password
	 * @return
	 */
	public String login_Spog( String username, String password){
		
		errorHandle.printInfoMessageInDebugFile("**************login_Spog*****************");
		String result = "fail";
		TextField usernameFd=new TextField(SPOGMenuTreePath.SPOG_USERNAME);
		waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_USERNAME,4 );
		usernameFd.clear();
		usernameFd.sendKeys(username);
		
		TextField passwordFd=new TextField(SPOGMenuTreePath.SPOG_PASSWORD);
		passwordFd.clear();
		passwordFd.sendKeys(password);
		
		Button login=new Button(SPOGMenuTreePath.SPOG_LOGINBTN);
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_LOGINBTN,4 );
		login.click();
		
		if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_MONITOR,3)){
			 result="pass";
		}
		return result;
	}
	
	/**
	 * @author shuo.zhang
	 * @param username
	 * @param password
	 * @param expectedErrorMsg
	 * @return
	 */
	public String login_Spog_with_error( String username, String password, String expectedErrorMsg){
		
		errorHandle.printInfoMessageInDebugFile("**************login_Spog_with_error*****************");
		String result = "fail";
		TextField usernameFd=new TextField(SPOGMenuTreePath.SPOG_USERNAME);
		waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_USERNAME,4 );
		usernameFd.clear();
		usernameFd.sendKeys(username);
		
		TextField passwordFd=new TextField(SPOGMenuTreePath.SPOG_PASSWORD);
		passwordFd.clear();
		passwordFd.sendKeys(password);
		
		Button login=new Button(SPOGMenuTreePath.SPOG_LOGINBTN);
		waitUntilElementIsUsable(SPOGMenuTreePath.SPOG_LOGINBTN,5);
		login.click();
		
		if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_LOGIN_ERROR_MSG)){
			if( ElementFactory.getElement(SPOGMenuTreePath.SPOG_LOGIN_ERROR_MSG).getText().equalsIgnoreCase(expectedErrorMsg)){
				result = "pass";
			}else{
				 result="fail";
			}
	
		}else if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_MONITOR,3)){
			 result="fail";
		}
		return result;
	}
	
	/**
	 * @author shuo.zhang
	 * @return
	 */
	public boolean isLoginBtnEnabled(){
		
		errorHandle.printInfoMessageInDebugFile("**************isLoginBtnEnabled*****************");

		if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_LOGINBTN)){
			 if(ElementFactory.getElement(SPOGMenuTreePath.SPOG_LOGINBTN).isEnabled()){
				 return true;
			 }else{
				 return false;
			 }
		}else{
			return false;
		}

	}
	
	
}