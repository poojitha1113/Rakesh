package ui.base.common;

import org.openqa.selenium.WebDriver;

public class BrowserOperation {
	private WebDriver driverInstance=null;
	public BrowserOperation(WebDriver driver){
		this.driverInstance=driver;
	}
	public void openUrl(String url){
		driverInstance.get(url);
	}
	public void close(){
		driverInstance.close();
	}
	public void refresh(){
		driverInstance.navigate().refresh();
	}
	
	public void navigateBack() {
		driverInstance.navigate().back();
	}
}
