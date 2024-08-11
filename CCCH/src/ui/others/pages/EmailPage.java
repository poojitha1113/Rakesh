package ui.others.pages;

import static org.testng.Assert.assertEquals;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.testng.Assert;

import genericutil.ErrorHandler;
import ui.base.common.BasePage;
import ui.base.common.EmailMenuTreePath;
import ui.base.elements.Button;
import ui.base.elements.TextField;
import ui.base.factory.BrowserFactory;
import ui.base.factory.ElementFactory;

public class EmailPage extends BasePage{
	private ErrorHandler errorHandle;
	public EmailPage(){
		super();
		errorHandle = ErrorHandler.getErrorHandler();
	}
	
	/**
	 * @author shuo.zhang
	 * @param email
	 * @param password
	 */
	public void loginHotMail(String email, String password){
		errorHandle.printInfoMessageInDebugFile("**************loginHotMail*****************");
				
		try{
			TextField usernameFd=new TextField(EmailMenuTreePath.MICROSOFTEMAIL_EMAILADDRESS);
			waitUntilElementIsUsable(EmailMenuTreePath.MICROSOFTEMAIL_EMAILADDRESS,4 );
			usernameFd.clear();
			usernameFd.sendKeys(email);
			
			Button nextBtn=new Button(EmailMenuTreePath.MICROSOFTEMAIL_NEXT);
			nextBtn.click();
			
			this.waitForSeconds(5);
			waitUntilElementIsUsable(EmailMenuTreePath.MICROSOFTEMAIL_PASSWORD,20 );
			TextField passwordFd=new TextField(EmailMenuTreePath.MICROSOFTEMAIL_PASSWORD);
			passwordFd.sendKeys(password);
			
			Button signInBtn=new Button(EmailMenuTreePath.MICROSOFTEMAIL_SIGNIN);
			signInBtn.click();
			int count=0;
			/*		do{
				this.waitForSeconds(30);
				waitUntilElementAppear(EmailMenuTreePath.MICROSOFTEMAIL_MOREACTIONS);
				waitUntilElementIsUsable(EmailMenuTreePath.MICROSOFTEMAIL_MOREACTIONS,20 );
				Button moreActionsBtn=new Button(EmailMenuTreePath.MICROSOFTEMAIL_MOREACTIONS);
				moreActionsBtn.click();
				count++;
				
			}while(!ElementFactory.checkElementExists(EmailMenuTreePath.MICROSOFTEMAIL_VIEWINBOX));
			System.out.println(count);
			waitUntilElementIsUsable(EmailMenuTreePath.MICROSOFTEMAIL_VIEWINBOX,20 );
			WebElement viewInboxSpan= ElementFactory.getElement(EmailMenuTreePath.MICROSOFTEMAIL_VIEWINBOX);
			ElementFactory.clickByAction(viewInboxSpan);
			
			//viewInboxSpan.click();
			BrowserFactory.switchToNewOpenedTab();*/
			do{
				this.waitForSeconds(30);
				count++;
			}while(!ElementFactory.checkElementExists(EmailMenuTreePath.MICROSOFTEMAIL_MAILBOX_INBOX) && (count<10));
			System.out.println(count);
			
			
		}catch(Exception e){
			e.printStackTrace();
			Assert.fail(e.getMessage(), e);
		
		}
	}
	
	/**
	 * @author shuo.zhang
	 */
	public void openActivateAccountPage(String firstName, String lastName, String orgName,  String email, String createdByEmail){
		
		errorHandle.printInfoMessageInDebugFile("**************openActivateAccountPage*****************");
		try{
			waitUntilElementIsUsable(EmailMenuTreePath.MICROSOFTEMAIL_MAILBOX_FOCUSED,5 );
		/*	WebElement inboxSpan = ElementFactory.getElement(EmailMenuTreePath.MICROSOFTEMAIL_MAILBOX_FOCUSED);
			inboxSpan.click();*/
			int count=0; 
			do{
				List<WebElement> emailTitles = ElementFactory.getElements(EmailMenuTreePath.MICROSOFTEMAIL_MAILBOX_SPOGEMAILTITLE);
				emailTitles.get(0).click();
				this.waitForSeconds(5);
				count++;
			}while(!ElementFactory.checkElementExists(EmailMenuTreePath.MICROSOFTEMAIL_MAILBOX_ACTIVATEACCOUNT) && (count<100));
		
			
		//	waitUntilElementAppear(EmailMenuTreePath.MICROSOFTEMAIL_MAILBOX_ACTIVATEACCOUNT);
			waitUntilElementIsClickable(EmailMenuTreePath.MICROSOFTEMAIL_MAILBOX_ACTIVATEACCOUNT,100 );
			
			checkActivateEmailInfo(firstName,lastName, orgName, email, createdByEmail);
			Button activateBtn =new Button(EmailMenuTreePath.MICROSOFTEMAIL_MAILBOX_ACTIVATEACCOUNT);
			activateBtn.click();
			
			waitForSeconds(3);
		//	BrowserFactory.switchToNewOpenedTab();
			
		}catch(Exception e){
			e.printStackTrace();
			Assert.fail(e.getMessage(), e);
		
		}
	}
	
	public void checkActivateEmailInfo(String firstName, String lastName, String orgName, String email, String createdByEmail){
		
		
	
		WebElement nameElement = ElementFactory.getElement(EmailMenuTreePath.MICROSOFTEMAIL_MAILBOX_SPOGEMAILFIRSTLASTNAME);
		Assert.assertTrue( nameElement.getText().contains(firstName + " "+ lastName ), ("name is correct + name is "+ nameElement.getText()));

		WebElement orgElement = ElementFactory.getElement(EmailMenuTreePath.MICROSOFTEMAIL_MAILBOX_SPOGEMAILORGNAME);
		Assert.assertTrue( orgElement.getText().contains(orgName), ("org name is correct"));

		WebElement accountNameElement =  ElementFactory.getElement(EmailMenuTreePath.MICROSOFTEMAIL_MAILBOX_SPOGEAMILACCOUNTNAME);
		Assert.assertTrue( accountNameElement.getText().contains(email.toLowerCase()), ("account name is correct"));
	

		WebElement accountCreatedByElement =  ElementFactory.getElement(EmailMenuTreePath.MICROSOFTEMAIL_MAILBOX_SPOGEAMILACCOUNTCREATEDBY);
		Assert.assertTrue( accountCreatedByElement.getText().contains(createdByEmail.toLowerCase()), ("create by account is correct"));
	}

}
