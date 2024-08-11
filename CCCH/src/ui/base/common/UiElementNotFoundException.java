package ui.base.common;

import org.openqa.selenium.NoSuchElementException;

public class UiElementNotFoundException extends NoSuchElementException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1195240317431491234L;
	private String eleXpath = "";
	
	public UiElementNotFoundException(String xpath) {
		super("UI element is not found.");
		eleXpath = xpath;
	}
	
	public String getMessage()
	{
		return eleXpath;
	}
}
