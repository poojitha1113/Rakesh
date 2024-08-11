package ui.base.elements;

import org.openqa.selenium.WebElement;
import ui.base.common.AutomationException;
import ui.base.common.UiElementNotFoundException;

public class Link extends BaseElement{
	private WebElement link;
	public Link(String xPath)throws UiElementNotFoundException, AutomationException{
		super(xPath);
		this.link=super.getWebElement();
	}
	public void click(){
		link.click();
	}
	
}
