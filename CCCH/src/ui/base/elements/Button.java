package ui.base.elements;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import ui.base.common.AutomationException;
import ui.base.common.UiElementNotFoundException;
import ui.base.factory.BrowserFactory;

public class Button extends BaseElement{
	private WebElement button;
	public Button(String xPath)throws UiElementNotFoundException, AutomationException{
		super(xPath);
		this.button=super.getWebElement();
	}
	public void click(){
		button.click();
	}
	public void doubleClick(){  
        new Actions(BrowserFactory.getBrowser()).doubleClick(button).perform();  
    }
	public void rightClick(){  
        new Actions(BrowserFactory.getBrowser()).contextClick(button).perform();  
    }
	public boolean isEnabled() {
		return button.isEnabled();
	}
	
	
}
