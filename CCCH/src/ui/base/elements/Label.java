package ui.base.elements;

import org.openqa.selenium.WebElement;
import ui.base.common.AutomationException;
import ui.base.common.UiElementNotFoundException;

public class Label extends BaseElement{
	private WebElement label;
	public Label(String xPath)throws UiElementNotFoundException, AutomationException{
		super(xPath);
		this.label=super.getWebElement();
	}	
}
