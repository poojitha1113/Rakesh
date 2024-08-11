package ui.base.elements;

import org.codehaus.groovy.transform.sc.StaticCompilationMetadataKeys;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.WebElement;
import ui.base.common.AutomationException;
import ui.base.common.UiElementNotFoundException;
import ui.base.factory.BrowserFactory;
import ui.base.factory.ElementFactory;

public class BaseElement {
  private WebElement element;
  private String     xPath;


  public BaseElement(String xPath) throws UiElementNotFoundException, AutomationException {
    this.element = ElementFactory.getElement(BrowserFactory.getBrowser(), xPath);
    this.xPath = xPath;
  }


  public BaseElement refresh() {

    this.element = ElementFactory.getElement(BrowserFactory.getBrowser(), xPath);
    return this;
  }


  public boolean isLocked() {

    String parentPath = "..";
    String classValue;
    WebElement node = element;

    boolean ifGoOn = true;

    while (ifGoOn) {
      classValue = node.getAttribute("class");
      // System.out.println( "Tag: " + node.getTagName() + " Class: " + classValue );

      if (classValue.contains("x-masked")) {
        return true;
      } else if (classValue.contains("x-masked-relative")) {
        return true;
      } else if (classValue.contains("x-modal")) {
        return true;
      }

      try {
        node = node.findElement(By.xpath(parentPath));
      } catch (InvalidSelectorException e) {
        return false;
      }
    }

    return false;
  }


  public boolean isDisplayed() {

    return element.isDisplayed();
  }


  public boolean isEnabled() {

    return element.isEnabled();
  }


  public String getText() {

    return element.getText();
  }


  public static String getText(WebElement aElement) {

    return aElement.getAttribute("value");
  }


  public String getTagName() {

    return element.getTagName();
  }


  public String getValue() {

    return element.getAttribute("value");
  }


  public WebElement getWebElement() {

    return this.element;
  }


  public String getClassValue() {

    return element.getAttribute("class");
  }


  public void click() {

    element.click();
  }


  public static void setText(WebElement aElement, String aString) {

    aElement.clear();
    aElement.sendKeys(aString);
  }


  public static void click(WebElement aElement) {

    aElement.click();
  }


  public static boolean isEnable(WebElement aElement) {

    return aElement.isEnabled();
  }
  /** To check element whether clickable at point or not
   * 
   * @param ele
   * @return
   */
  public boolean isClickable() {

	  try {
		  element.click();
		  return true;
	  } catch (Exception e) {
		  return false;
	  }
  }
}
