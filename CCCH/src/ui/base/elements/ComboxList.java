package ui.base.elements;

import java.util.List;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import ui.base.common.AutomationException;
import ui.base.common.UiElementNotFoundException;

public class ComboxList extends BaseElement{
	private Select comBox;
	public ComboxList(String xPath)throws UiElementNotFoundException, AutomationException{
		super(xPath);
		this.comBox=new Select(super.getWebElement());
	}
	public void deSelectAll(){
		comBox.deselectAll();
	}
	public void deSelectByIndex(int index){
		comBox.deselectByIndex(index);
	}
	public void deselectByValue(String value){
		comBox.deselectByValue(value);
	}
	public void deselectByVisibleText(String text){
		comBox.deselectByVisibleText(text);
	}
	public void SelectByIndex(int index){
		comBox.selectByIndex(index);
	}
	public void selectByValue(String value){
		comBox.selectByValue(value);
	}
	public void selectByVisibleText(String text){
		comBox.selectByVisibleText(text);
	}
	public boolean isMultiple(){
		return comBox.isMultiple();
	}
	public List<WebElement> getAllSelectedOptions(){
		return comBox.getAllSelectedOptions();
	}
	public List<WebElement> getOptions(){
		return comBox.getOptions();
	}
	public WebElement getFirstSelectedOption(){
		return comBox.getFirstSelectedOption();
	}
}
