package ui.base.elements;

import org.openqa.selenium.WebElement;
import ui.base.common.AutomationException;
import ui.base.common.UiElementNotFoundException;

public class RadioButton extends BaseElement{
	private WebElement radioButton;
	public RadioButton(String xPath)throws UiElementNotFoundException, AutomationException{
		super(xPath);
		this.radioButton=super.getWebElement();
	}
	public void click(){
		radioButton.click();
	}
	public void clear(){  
		radioButton.clear();
    }
	public boolean isSelected(){
		return radioButton.isSelected();
	}
}
