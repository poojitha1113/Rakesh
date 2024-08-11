package ui.spog.pages.analyze;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import genericutil.ErrorHandler;
import ui.base.common.BasePage;
import ui.base.common.SPOGMenuTreePath;
import ui.base.common.TableHeaders;
import ui.base.factory.ElementFactory;

public class LogsPage extends BasePage{

	private ErrorHandler errorHandle = BasePage.getErrorHandler();
	
	public boolean checkSourceExists(String sourceName) {
		boolean isExist = false;
		try {
			waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_TABLE_ROWS);
			WebElement tableBody = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_TABLE_BODY);
			WebElement customerElement = tableBody.findElement(By.xpath(".//span[text()='" + sourceName + "']"));
			assertEquals(customerElement.getText(), sourceName);

			isExist = true;
		} catch (NoSuchElementException e) {
			errorHandle.printErrorMessageInDebugFile("Source with name: " + sourceName + " does not exist");
		}

		return isExist;
	}
	
	public boolean checkLogExists(String logName) {
		boolean isExist = false;
		try {
			waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_TABLE_ROWS);
			WebElement tableBody = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_TABLE_BODY);
			WebElement customerElement = tableBody.findElement(By.xpath(".//span[text()='" + logName + "']"));
			assertEquals(customerElement.getText(), logName);

			isExist = true;
		} catch (NoSuchElementException e) {
			errorHandle.printErrorMessageInDebugFile("Log with name: " + logName + " does not exist");
		}

		return isExist;
	}	
	
	/**
	 * Verifies the details of each log in table
	 * 
	 * @author Rakesh.Chalamala
	 * @param logName
	 * @param expectedInfo
	 * @param test
	 */
	public void checkLogDetailsInTable(String logName, HashMap<String, Object> expectedInfo, ExtentTest test) {

		HashMap<String, Integer> logTableHeaderOrder = getTableHeaderOrder(getTableHeaders());

		List<WebElement> rows = getRows();

		if (rows.size() == 0 && expectedInfo != null && !expectedInfo.isEmpty()) {
			test.log(LogStatus.WARNING, "no rows found");
			assertTrue(false);
		}

		for (int i = 0; i < rows.size(); i++) {
			WebElement eachRow = rows.get(i);

			String xpath = "." + ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_TABLE_COLUMNS).get(0)
					.getValue();
			List<WebElement> columns = eachRow.findElements(By.xpath(xpath));

			if (columns.get(logTableHeaderOrder.get(TableHeaders.message_id)).getText()
					.equalsIgnoreCase(expectedInfo.get(TableHeaders.message_id).toString())) {

				assertTrue(columns.get(logTableHeaderOrder.get(TableHeaders.date)).getText().trim().equalsIgnoreCase(expectedInfo.get(TableHeaders.date).toString()));
//				assertTrue(columns.get(logTableHeaderOrder.get(TableHeaders.severity)).getText().equalsIgnoreCase(expectedInfo.get(TableHeaders.severity).toString()));
				assertTrue(columns.get(logTableHeaderOrder.get(TableHeaders.source)).getText().equalsIgnoreCase(expectedInfo.get(TableHeaders.source).toString()));
				assertTrue(columns.get(logTableHeaderOrder.get(TableHeaders.generated_from)).getText().equalsIgnoreCase(expectedInfo.get(TableHeaders.generated_from).toString()));
				assertTrue(columns.get(logTableHeaderOrder.get(TableHeaders.job_type)).getText().equalsIgnoreCase(expectedInfo.get(TableHeaders.job_type).toString()));
				assertTrue(columns.get(logTableHeaderOrder.get(TableHeaders.message_id)).getText().equalsIgnoreCase(expectedInfo.get(TableHeaders.message_id).toString()));
				assertTrue(columns.get(logTableHeaderOrder.get(TableHeaders.message)).getText().toLowerCase().contains(expectedInfo.get(TableHeaders.message).toString().toLowerCase()));
				assertTrue(columns.get(logTableHeaderOrder.get(TableHeaders.job_name)).getText().equalsIgnoreCase(expectedInfo.get(TableHeaders.job_name).toString()));

				break;
			}

			if (i == rows.size() - 1) {
				assertTrue(false, "log with message: " + expectedInfo.get(TableHeaders.message) + " not matched");
			}
		}
	}
}
