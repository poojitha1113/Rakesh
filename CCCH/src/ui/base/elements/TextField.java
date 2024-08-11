package ui.base.elements;

import org.openqa.selenium.WebElement;
import ui.base.common.AutomationException;
import ui.base.common.UiElementNotFoundException;

public class TextField extends BaseElement {
  private WebElement textField;


  public TextField(String xPath) throws UiElementNotFoundException, AutomationException {
    super(xPath);
    this.textField = super.getWebElement();
  }


  public void clear() {

    textField.clear();
  }


  public void sendKeys(String keys) {

    textField.sendKeys(keys);
  }


  public void setText(String text) {

    textField.clear();
    textField.sendKeys(text);
  }


  public void click() {

    textField.click();
  }
}
