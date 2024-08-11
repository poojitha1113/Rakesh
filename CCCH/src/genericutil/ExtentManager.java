package genericutil;

import java.io.File;
import java.text.SimpleDateFormat;

import org.codehaus.groovy.transform.sc.StaticCompilationMetadataKeys;

import com.relevantcodes.extentreports.DisplayOrder;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;

public class ExtentManager {
	private static ExtentReports extent;
	private static String logFolderFullPath;
	private static String reportFileName;
	public static ExtentReports getInstance(String nameoftest,String logFolder) {
		/*if (extent == null) {
			Date d=new Date();
			String fileName=nameoftest+"_"+d.toString().replace(":", "_").replace(" ", "_")+".html";
			extent = new ExtentReports(logFolder+"\\"+ fileName, true, DisplayOrder.NEWEST_FIRST);

			//extent = new ExtentReports("E:\\automation-SPOG\\"+fileName);
			extent.loadConfig(new File(System.getProperty("user.dir")+"//ReportsConfig.xml"));
			// optional
			extent.addSystemInfo("Selenium Version", "2.53.0").addSystemInfo(
					"Environment", "QA");
		}*/
		if (extent == null) {
			SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date date=new java.util.Date();
			logFolderFullPath = logFolder+"\\"+dateFormater.format(date);
			FolderOrFileUtils.createDirecotry(logFolderFullPath);
			reportFileName =nameoftest+".html";
			extent = new ExtentReports(logFolderFullPath+"\\"+ reportFileName, true, DisplayOrder.NEWEST_FIRST);
			extent.loadConfig(new File(System.getProperty("user.dir")+"//ReportsConfig.xml"));			
		}
		return extent;
	}
	
	public static ExtentTest getNewTest(String testCaseName) {
		return extent.startTest(testCaseName);
	}
	
	public static String returnLogFolderFullPath(){
		return logFolderFullPath;
	}
	
	public static String returnReportFileName(){
		return reportFileName;
	}
}
