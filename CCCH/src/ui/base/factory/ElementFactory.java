package ui.base.factory;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ui.base.elements.Link;
import ui.base.factory.BrowserFactory;
import ui.base.common.AutomationException;
import ui.base.common.UiElementNotFoundException;
import genericutil.ErrorHandler;
import genericutil.XMLUpdatter;

public class ElementFactory {

	static String menuTreeFilePath = "";
//	static WebDriver driverInstance = BrowserFactory.getBrowser();
	static WebDriver driverInstance = null;
	
	static ErrorHandler errorHandler = new ErrorHandler();
	
	public static void setDriver(WebDriver value)
	{
		driverInstance = value;
	}
	
	public static void setMenuTreeFilePath(String value)
	{
		menuTreeFilePath = value;
	}
	
	public static WebElement getElement(String xpath) throws AutomationException, UiElementNotFoundException
	{
		WebElement ele = null;
				
		ele = getElement(driverInstance, xpath);

		return ele;
	}
	
	public static List<Attribute> getElementAttrFromXML(String xpath) throws AutomationException{
		
		XMLUpdatter xmlUp = null;
		Vector<Element> eles = null;
		try {
			xmlUp = new XMLUpdatter(menuTreeFilePath);
			eles = xmlUp.getElements(xpath);
		} catch (JDOMException e1) {
			errorHandler.printErrorMessageInDebugFile("JDomException caught: " + e1.getMessage());
			throw new AutomationException(e1.getMessage());
		} catch (IOException e2) {
			errorHandler.printErrorMessageInDebugFile("IOException caught: " + e2.getMessage());
			throw new AutomationException(e2.getMessage());
		}
		
		if(eles.size() > 1)
		{
			errorHandler.printErrorMessageInDebugFile("Seems duplicated elements are defined in MenuTree file, getting: " 
					+ eles.size() + " elements by using xpath: " + xpath + ", expected only get one.");
			errorHandler.printErrorMessageInDebugFile("Now using the first element here.");
		}
		
		Element ele = eles.firstElement();
		List<Attribute> attrs = ele.getAttributes();
		if(attrs.size() > 1)
		{
			//For now we assume that we use only 1 attribute to identify element, maybe expend later, if needed.
				errorHandler.printErrorMessageInDebugFile("There are more than one attribute defined for element which xpath is: " + xpath);
				errorHandler.printErrorMessageInDebugFile("Now using the first attribute here.");
			}
		return attrs;
	}
	
	
	public static WebElement getElement(WebDriver driver,String xpath) throws AutomationException, UiElementNotFoundException
	{		
		 WebElement element = null;
		 List<Attribute> attrs = getElementAttrFromXML(xpath);
	
		Attribute att = (Attribute) attrs.get(0);
		String attName = att.getName();
		String attValue = att.getValue();
		
		if(attName.equalsIgnoreCase("id"))
		{
			try
			{
				element = driver.findElement(By.id(attValue));
			} catch (NoSuchElementException e)
			{
				errorHandler.printErrorMessageInDebugFile("UI element with xpath: " + attValue + " is not found.");
				throw new UiElementNotFoundException(xpath);
			}
		} else if(attName.equalsIgnoreCase("xpath"))
		{
			try
			{
				element = driver.findElement(By.xpath(attValue));
			} catch (NoSuchElementException e)
			{
				errorHandler.printErrorMessageInDebugFile("UI element with xpath: " + attValue + " is not found.");
				throw new UiElementNotFoundException(xpath);
			}
		} else if(attName.equalsIgnoreCase("css"))
		{
			try
			{
				element = driver.findElement(By.cssSelector(attValue));
			} catch (NoSuchElementException e)
			{
				errorHandler.printErrorMessageInDebugFile("UI element with xpath: " + attValue + " is not found.");
				throw new UiElementNotFoundException(xpath);
			}
		} else
		{
			errorHandler.printErrorMessageInDebugFile("Element which xpath is: " + xpath + ", is having an invalid attribute: " +
					attName + " which is not expected.");
		}

		return element;
	}

	public static List<WebElement> getElements(String xpath) throws AutomationException, UiElementNotFoundException
	{
		List<WebElement> ele = null;
				
		ele = getElements(driverInstance, xpath);

		return ele;
	}
	
	public static List<WebElement> getElements(WebDriver driver, String xpath) throws AutomationException, UiElementNotFoundException
	{	
		
		 List<Attribute> attrs = getElementAttrFromXML(xpath);
		
	
		Attribute att = (Attribute) attrs.get(0);
		String attName = att.getName();
		String attValue = att.getValue();
		
		List<WebElement> element = null;
		if(attName.equalsIgnoreCase("id"))
		{
			try
			{
				element  = driver.findElements(By.id(attValue));
			} catch (NoSuchElementException e)
			{
				errorHandler.printErrorMessageInDebugFile("UI element with xpath: " + attValue + " is not found.");
				throw new UiElementNotFoundException(xpath);
			}
		} else if(attName.equalsIgnoreCase("xpath"))
		{
			try
			{
				element = driver.findElements(By.xpath(attValue));
			} catch (NoSuchElementException e)
			{
				errorHandler.printErrorMessageInDebugFile("UI element with xpath: " + attValue + " is not found.");
				throw new UiElementNotFoundException(xpath);
			}
		} else if(attName.equalsIgnoreCase("css"))
		{
			try
			{
				element = driver.findElements(By.cssSelector(attValue));
			} catch (NoSuchElementException e)
			{
				errorHandler.printErrorMessageInDebugFile("UI element with xpath: " + attValue + " is not found.");
				throw new UiElementNotFoundException(xpath);
			}
		} else
		{
			errorHandler.printErrorMessageInDebugFile("Element which xpath is: " + xpath + ", is having an invalid attribute: " +
					attName + " which is not expected.");
		}

		return element;
	}
	
	public static boolean checkElementExists( String xpath,int seconds)
	{	
		int original = BrowserFactory.getMaxTimeWaitUIElement();
		try {
			BrowserFactory.setMaxTimeWaitUIElement(seconds);
			getElement(xpath);
			BrowserFactory.setMaxTimeWaitUIElement(original);
		} catch (UiElementNotFoundException e) {
			errorHandler.printErrorMessageInDebugFile(e.getMessage());
			BrowserFactory.setMaxTimeWaitUIElement(original);
			return false;
		} catch (AutomationException e) {
			errorHandler.printErrorMessageInDebugFile(e.getMessage());
			BrowserFactory.setMaxTimeWaitUIElement(original);
			return false;
		} 
		return true;
	}
	
	public static WebElement getElement( String xpath,int seconds)
	{	
		WebElement element=null;
		if(checkElementExists(xpath,seconds)) {
			element=getElement(xpath);
		}
		return element;
	}
	
	public static boolean checkElementExists( String xpath)
	{
		try {
			getElement(xpath);
		} catch (UiElementNotFoundException e) {
			errorHandler.printErrorMessageInDebugFile(e.getMessage());
			return false;
		} catch (AutomationException e) {
			errorHandler.printErrorMessageInDebugFile(e.getMessage());
			return false;
		} 
		return true;
	}
	
	public static boolean checkElementsExists( String xpath)
	{
		try {
			List<WebElement> els=getElements(xpath);
			if (els==null||els.size()==0){
				return false;
			}
		} catch (UiElementNotFoundException e) {
			errorHandler.printErrorMessageInDebugFile(e.getMessage());
			return false;
		} catch (AutomationException e) {
			errorHandler.printErrorMessageInDebugFile(e.getMessage());
			return false;
		} 
		return true;
	}
	
	public static boolean checkLinkExistByText(String textValue)throws AutomationException{
		boolean ret=true;
		if ((textValue== null) || StringUtils.isEmpty(textValue)){
			throw new AutomationException("Node name is not provided.");
		}else{
			try
			{
				BrowserFactory.getBrowser().findElement(By.linkText(textValue));
			} catch (NoSuchElementException e)
			{
				errorHandler.printErrorMessageInDebugFile(e.getMessage());
				return false;
			}
		}
		return ret;
	}
	
	public static boolean checkLinkExistByText(String textValue,int seconds)throws AutomationException{
		boolean ret=true;
		int original = BrowserFactory.getMaxTimeWaitUIElement();
		if ((textValue== null) || StringUtils.isEmpty(textValue)){
			throw new AutomationException("Node name is not provided.");
		}else{
			try
			{
				BrowserFactory.setMaxTimeWaitUIElement(seconds);
				BrowserFactory.getBrowser().findElement(By.linkText(textValue));
				BrowserFactory.setMaxTimeWaitUIElement(original);
			} catch (NoSuchElementException e)
			{
				errorHandler.printErrorMessageInDebugFile(e.getMessage());
				BrowserFactory.setMaxTimeWaitUIElement(original);
				return false;
			}
		}
		return ret;
	}
	
	public static Link getWebElementByLinkText(String textValue) throws AutomationException,UiElementNotFoundException{
		if ((textValue== null) || StringUtils.isEmpty(textValue)){
			throw new AutomationException("Node name is not providded.");
		}else{
			return (Link)BrowserFactory.getBrowser().findElement(By.linkText(textValue));
		}
	}
	
	public static WebElement getElementByLinkText(String textValue) throws AutomationException,UiElementNotFoundException
	{
		if ((textValue== null) || StringUtils.isEmpty(textValue)){
			throw new AutomationException("Node name is not providded.");
		}else{
			return BrowserFactory.getBrowser().findElement(By.linkText(textValue));
		}
	}
	
	public static WebElement getElementByXpath(String xpath){
		try
		{
			WebElement element = driverInstance.findElement(By.xpath(xpath));
			return element;
		} catch (NoSuchElementException e)
		{
			errorHandler.printErrorMessageInDebugFile("UI element with xpath: " + xpath + " is not found.");
			throw new UiElementNotFoundException(xpath);
		}
	
	}
	
	public static List<WebElement> getElementsByXpath(String xpath){
		try
		{
			List<WebElement> elements = driverInstance.findElements(By.xpath(xpath));
			return elements;
		} catch (NoSuchElementException e)
		{
			errorHandler.printErrorMessageInDebugFile("UI element with xpath: " + xpath + " is not found.");
			throw new UiElementNotFoundException(xpath);
		}
	
	}
	
	
	 public static void scrollTo( WebElement element) {
	        JavascriptExecutor js = (JavascriptExecutor) driverInstance;
	        js.executeScript("arguments[0].scrollIntoView(true);", element);
	    }
	 
	 
	 public static void clickByAction(WebElement element){
		 Actions actions = new Actions(driverInstance);

		 actions.moveToElement(element).click().perform();
	 }
	 
	public static void main(String[] args) {
		ElementFactory eleFac = new ElementFactory();
		try {
			ElementFactory.getElement("/Login/Resources/AllNodes");
		} catch (NoSuchElementException e) {
			e.printStackTrace();
		} catch (AutomationException e) {
			e.printStackTrace();
		} 
	}
	
	public static boolean checkElementClickable(WebElement ele) {

		try{
			WebDriverWait wait = new WebDriverWait(driverInstance, 5);
			wait.until(ExpectedConditions.elementToBeClickable(ele));
			return true;
		}catch (Exception e){
			return false;
		}
	}

}
