package ui.spog.pages.analyze;

import java.util.ArrayList;

import com.relevantcodes.extentreports.ExtentTest;

import genericutil.ErrorHandler;
import ui.base.common.BasePage;

public class CapacityUsageReportsPage extends BasePage {

	private static final int WebElement = 0;
	private ErrorHandler errorHandle = BasePage.getErrorHandler();
	private int datarangemenu;

	
	public void saveSearchfiltersforCapacityUsageReport(String search_string,ArrayList<String> filters,ExtentTest test ) {
	checkSelectedFilters(search_string, filters, test);
	}

}
