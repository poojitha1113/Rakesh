package ui.base.factory;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.TimeUnit;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;

import org.openqa.selenium.remote.DesiredCapabilities;


public class BrowserFactory {
	static WebDriver driver=null;
	static String parentUUID=null;
	static int maxTimeWaitUIElementSec = 20;
	static String browseType="chrome";
	
	public static void setBrowseType(String browserType){
		browseType = browserType;
	}
	
	public static void setDefaultMaxWaitTimeSec(int maxTimeWaitSec){
		maxTimeWaitUIElementSec = maxTimeWaitSec;
	}
	
	
	public static WebDriver getBrowser()
	{
		if (driver==null){
			
			System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
			System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "warn");
			
			driver= createBrowser(browseType);
		}
		return driver;
	}
	
    public static WebDriver createBrowser( String browserType )
    {
        if (driver==null){
            maxTimeWaitUIElementSec = 5;
            
            if(browserType.equalsIgnoreCase("ie"))
            {
                DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
                ieCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
                
                File file = new File("./Util/IEDriverServer.exe");
                System.setProperty("webdriver.ie.driver", file.getAbsolutePath());
                driver = new InternetExplorerDriver(ieCapabilities);
                //Set the max time out here.
                driver.manage().timeouts().implicitlyWait(maxTimeWaitUIElementSec, TimeUnit.SECONDS);
                parentUUID= driver.getWindowHandle();
                return driver;
            } else if(browserType.equalsIgnoreCase("firefox"))
            {
                //Add code to return Firefox driver here.
                return null;
            } else if(browserType.equalsIgnoreCase("chrome"))
            {
                                
            	System.setProperty("webdriver.chrome.driver", ".\\lib\\chromedriver.exe");
            	 ChromeOptions options = new ChromeOptions();
            	  options.addArguments("--allow-running-insecure-content");
            	//  DesiredCapabilities capabilities = DesiredCapabilities.chrome();
            	  
            	WebDriver driver = new ChromeDriver(options);               
                driver.manage().window().maximize();                 
                driver.manage().timeouts().implicitlyWait(maxTimeWaitUIElementSec, TimeUnit.SECONDS);    
                parentUUID= driver.getWindowHandle();
                return driver;
            } else
            {
                //Unknown browser type, user error.
                return null;
            }
        }
        return driver;
    }

 
    public static String getParentUUID(){
		return parentUUID;
	}
	
    public static void destroyBrowser()
	{
		driver.quit();
		driver=null;
	}
	
	public static void closeBrowser()
	{
		driver.close();
	}
	
	public static boolean isWebDriverNull()
	{
		if(driver == null)
			return true;
		else 
			return false;
	}
	
	public static int getMaxTimeWaitUIElement()
	{
		return maxTimeWaitUIElementSec;
	}
	public static void setMaxTimeWaitUIElement(int value)
	{
		maxTimeWaitUIElementSec = value;
		driver.manage().timeouts().implicitlyWait(maxTimeWaitUIElementSec, TimeUnit.SECONDS);
	}
	
	public static WebDriver getWebDriver() {
	    return BrowserFactory.driver;
	}
	
	public static ArrayList<String>  getWindowsHandlers(){
		Set<String> handlers = driver.getWindowHandles();
		ArrayList<String> windowsHandlers = new ArrayList<String>();
		windowsHandlers.addAll(handlers);
		return windowsHandlers;
	}
	
	public static void switchToNewOpenedTab(){
		ArrayList<String>  handlers = getWindowsHandlers();
		driver.switchTo().window(handlers.get(handlers.size()-1));
	}
	
	public static String getCurrentPageUrl() {
		return driver.getCurrentUrl();
	}
	
}
