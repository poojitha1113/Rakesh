package ui.base.elements;

import org.openqa.selenium.WebElement;
import ui.base.common.AutomationException;
import ui.base.common.UiElementNotFoundException;

public class CheckBox extends BaseElement{
	private WebElement checkBox;
	private boolean isChecked;

	public CheckBox(String xPath)throws UiElementNotFoundException, AutomationException{
		super(xPath);
		this.checkBox=super.getWebElement();
	}
	public void click(){
		checkBox.click();
	}
	public void clear(){  
		checkBox.clear();
    }
	public boolean isSelected(){
		return checkBox.isSelected();
	}
	
	public boolean isChecked() {
	    return isChecked;
	}
	
	public void setChecked( boolean checked ) {
	    isChecked = checked;
	}
}
