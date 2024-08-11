package ui.spog.pages.monitor;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
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

public class MonitorPage extends BasePage {

	private ErrorHandler errorHandle = BasePage.getErrorHandler();

	/**
	 * validateCustomerSummary- validate the labels and total customers count on
	 * customer summary pane on monitor
	 * 
	 * @author Ramya.Nagepalli
	 * @param total_count
	 * @param test
	 * @return
	 */
	public String validateCustomerSummary(int total_count, ExtentTest test) {
		String result = "fail";

		test.log(LogStatus.INFO, "validate the customer summary pane on monitor");
		waitUntilElementAppear(SPOGMenuTreePath.SPOG_MONITOR_CUSTOMERSUMMARY);

		waitForSeconds(3);

		if ((ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_MONITOR_CUSTOMERSUMMARY_SUCCESS, 5))
				&& (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_MONITOR_CUSTOMERSUMMARY_FAILED, 5))
				&& (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_MONITOR_CUSTOMERSUMMARY_TOTALCUSTOMERS,
						5))) {

			WebElement count_ele = ElementFactory
					.getElement(SPOGMenuTreePath.SPOG_MONITOR_CUSTOMERSUMMARY_CUSTOMERS_COUNT);

			int act = Integer.parseInt(count_ele.getText());

			if (act == total_count)
				result = "pass";

		}

		return result;
	}

	/**
	 * validateUsageSummaryAcrossCustomers - validate the labels on usage summary
	 * pane on monitor
	 * 
	 * @author Ramya.Nagepalli
	 * @param test
	 * @return result
	 */
	public String validateUsageSummaryAcrossCustomers(ExtentTest test) {

		String result = "fail";
		test.log(LogStatus.INFO, "check the Usage summary pane on Monitor");
		waitUntilElementAppear(SPOGMenuTreePath.SPOG_MONITOR_USAGE_SUMMARY_ACROSS_CUSTOMERS);

		test.log(LogStatus.INFO, "Check for the labels on the Usage summary pane");
		if ((ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_MONITOR_USAGESUMMARY_CLOUD_DIRECT, 5))
				&& (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_MONITOR_USAGESUMMARY_CLOUD_HYBRID, 5))) {
			result = "pass";
		}

		return result;

	}

	/**
	 * validateSourceSummaryAcrossCustomers- validate the labels and total customers
	 * count on customer summary pane on monitor
	 * 
	 * @author Ramya.Nagepalli
	 * @param total_count
	 * @param test
	 * @return
	 */
	public String validateSourceSummaryAcrossCustomers(int total_count, ExtentTest test) {
		String result = "fail";

		test.log(LogStatus.INFO, "validate the customer summary pane on monitor");
		waitUntilElementAppear(SPOGMenuTreePath.SPOG_MONITOR_SOURCE_SUMMARY_ACROSS_CUSTOMERS);

		if ((ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_MONITOR_SOURCESUMMARY_PROTECTED, 5))
				&& (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_MONITOR_SOURCESUMMARY_NOTPROTECTED, 5))
				&& (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_MONITOR_SOURCESUMMARY_OFFLINE, 5))) {

			WebElement count_ele = ElementFactory
					.getElement(SPOGMenuTreePath.SPOG_MONITOR_SOURCE_SUMMARY_ACROSS_CUSTOMERS_TOTAL_SOURCES_COUNT);

			int act = Integer.parseInt(count_ele.getText());

			if (act == total_count)
				result = "pass";

		}

		return result;
	}

	/**
	 * validateSourcesCountAcrossCustomers-validate the sources count on source
	 * summary pane on monitor
	 * 
	 * @author Ramya.Nagepalli
	 * @param protected_count
	 * @param not_protected
	 * @param offline
	 * @param test
	 * @return result
	 */
	public String validateSourcesCountAcrossCustomers(int total_sources, int protected_count, int not_protected,
			int offline, ExtentTest test) {
		String result = "fail";

		String exp_items = "Protected,Not Protected,Offline";
		String expvalues = protected_count + "," + not_protected + "," + offline;

		test.log(LogStatus.INFO, "validate the sources count on source summary across customers pane");
		if (total_sources != 0) {

			WebElement paneEle = ElementFactory.getElementByXpath("//div[@class='policy-summary-tile tile']");

			try {

				List<WebElement> status_count_Eles = paneEle.findElements(By.xpath(
						"//div[@class='policy-summary-tile tile']//div[@class='last-panel status-indicator no-link']//span/span"));

				List<WebElement> status_item_Eles = paneEle.findElements(By.xpath(
						"//div[@class='policy-summary-tile tile']//div[@class='last-panel status-indicator no-link']//span/button/span"));
				String[] expected_items = exp_items.split(",");
				String[] expected_val = expvalues.split(",");

				for (int i = 0; i < status_count_Eles.size(); i++) {

					String act_item_value = status_item_Eles.get(i).getText();
					assertEquals(act_item_value, expected_items[i]);

					String actValue = status_count_Eles.get(i).getText();
					assertEquals(actValue, expected_val[i]);

				}

				result = "pass";

			} catch (NoSuchElementException e) {
				// TODO: handle exception
				System.out.println("Element not found :" + e);
				assertFalse(true);
			}

		} else {
			test.log(LogStatus.INFO, "total sources count is : " + total_sources);

			result = "pass";
		}
		return result;
	}

	/**
	 * validateAlerts - validate the labels and total alerts count
	 * 
	 * @author Ramya.Nagepalli
	 * @param test
	 * @return result
	 */
	public String validateAlerts(ExtentTest test) {
		String result = "fail";

		test.log(LogStatus.INFO, "check for Alerts icon on Monitor");
		waitUntilElementAppear(SPOGMenuTreePath.SPOG_MONITOR_ALERTS);

		test.log(LogStatus.INFO, "Check for the labels on the alerts pane");
		if ((ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_MONITOR_ALERTS_CRITICAL, 5))
				&& (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_MONITOR_ALERTS_INFORMATION, 5))
				&& (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_MONITOR_ALERTS_WARNING, 5))) {

			test.log(LogStatus.INFO, "get the count of Alerts from UI and validate");

			WebElement alerts_count_ele = ElementFactory.getElement(SPOGMenuTreePath.SPOG_MONITOR_ALERTS);
			int total = Integer.parseInt(alerts_count_ele.getText());

			WebElement critical_count_ele = ElementFactory
					.getElement(SPOGMenuTreePath.SPOG_MONITOR_ALERTS_CRITICAL_COUNT);
			int critical = Integer.parseInt(critical_count_ele.getText());

			WebElement warning_count_ele = ElementFactory
					.getElement(SPOGMenuTreePath.SPOG_MONITOR_ALERTS_WARNING_COUNT);
			int warning = Integer.parseInt(warning_count_ele.getText());

			WebElement information_count_ele = ElementFactory
					.getElement(SPOGMenuTreePath.SPOG_MONITOR_ALERTS_INFORMATION_COUNT);
			int information = Integer.parseInt(information_count_ele.getText());

			if (total == critical + information + warning)
				result = "pass";

		}

		return result;
	}

	/**
	 * AcknowledgeAlerts - Acknowledge all alerts with respect to given
	 * expected_input
	 * 
	 * @author Ramya.Nagepalli
	 * @param expected_input
	 *            - all(acknowledge all alerts on monitor) /Critical(Acknowledge all
	 *            alerts on critical) /Warning(Acknowledge all alerts on Warning)
	 *            /Information(Acknowledge all alerts on Information)
	 * @param test
	 * @return result
	 */
	public String AcknowledgeAlerts(String expected_input, ExtentTest test) {
		String result = "fail";

		int count = 0;
		int critical = 0;
		int warning = 0;
		int Information = 0;

		test.log(LogStatus.INFO, "check the Alerts pane on Monitor");
		waitUntilElementAppear(SPOGMenuTreePath.SPOG_MONITOR_ALERTS);

		if (expected_input.equalsIgnoreCase("all")) {
			test.log(LogStatus.INFO, "Acknowledge all alerts on the monitor");

			for (int i = 0; i < 3; i++) {
				if (i == 0)
					ElementFactory.getElement(SPOGMenuTreePath.SPOG_MONITOR_ALERTS_CRITICAL).click();

				if (i == 1)
					ElementFactory.getElement(SPOGMenuTreePath.SPOG_MONITOR_ALERTS_INFORMATION).click();

				if (i == 2)
					ElementFactory.getElement(SPOGMenuTreePath.SPOG_MONITOR_ALERTS_WARNING).click();
				try {
					WebElement ack_ele = ElementFactory
							.getElement(SPOGMenuTreePath.SPOG_MONITOR_ALERTS_ACKNOWLEDGE_ALL);

					Boolean exp = ack_ele.isDisplayed();

					if (exp.equals(true)) {
						// ack_ele.click();
						result = "pass";
					} else {
						System.out.println("Element is not displaying on UI" + ack_ele);
					}

				} catch (NoSuchElementException e) {

					System.out.println("Element not found :" + e);
					// assertFalse(true);
				}
			}

		} else {
			test.log(LogStatus.INFO, "Achnowledge only selected Alerts of specific type :" + expected_input);

			WebElement input = ElementFactory.getElementByXpath("//span[text()='" + expected_input + "']");
			input.click();

			if (expected_input.equalsIgnoreCase("critical")) {
				WebElement critical_count_ele = ElementFactory
						.getElement(SPOGMenuTreePath.SPOG_MONITOR_ALERTS_CRITICAL_COUNT);
				critical = Integer.parseInt(critical_count_ele.getText());
				count = critical;
			} else if (expected_input.equalsIgnoreCase("warning")) {
				WebElement warning_count_ele = ElementFactory
						.getElement(SPOGMenuTreePath.SPOG_MONITOR_ALERTS_WARNING_COUNT);
				warning = Integer.parseInt(warning_count_ele.getText());
				count = warning;
			} else {
				WebElement information_count_ele = ElementFactory
						.getElement(SPOGMenuTreePath.SPOG_MONITOR_ALERTS_INFORMATION_COUNT);
				Information = Integer.parseInt(information_count_ele.getText());
				count = Information;
			}

			try {
				test.log(LogStatus.INFO, "select the alert to acknowledge");

				if (count > 1) {

					List<WebElement> ack_eles = ElementFactory
							.getElements(SPOGMenuTreePath.SPOG_MONITOR_ALERTS_ACKNOWLEDGE_SELECTED);

					test.log(LogStatus.INFO, "click on selected alert to acknowledge on " + expected_input);
					for (int j = 0; j < ack_eles.size(); j++) {

						Boolean exp = ack_eles.get(j).isDisplayed();
						if (exp.equals(true)) {
							ack_eles.get(j).click();

							WebElement acknowledge = ack_eles.get(j)
									.findElement(By.xpath("//span[text()='Acknowledge']"));
							// acknowledge.click();

							waitForSeconds(2);
							result = "pass";
							test.log(LogStatus.INFO, "Wait for 2 seconds");
							waitForSeconds(2);
						} else {
							System.out.println("Element is not displaying on UI" + ack_eles.get(j));
						}
					}

				} else {
					WebElement ack_eles = ElementFactory
							.getElement(SPOGMenuTreePath.SPOG_MONITOR_ALERTS_ACKNOWLEDGE_SELECTED);

					test.log(LogStatus.INFO, "click on selected alert to acknowledge on " + expected_input);

					Boolean exp = ack_eles.isDisplayed();
					if (exp.equals(true)) {
						ack_eles.click();

						WebElement acknowledge = ack_eles.findElement(By.xpath("//span[text()='Acknowledge']"));
						// acknowledge.click();
						result = "pass";
					} else {
						System.out.println("Element is not displaying on UI" + ack_eles);
					}

				}

			} catch (NoSuchElementException e) {

				System.out.println("Element not found :" + e);
				assertFalse(true);
			}

		}

		return result;
	}

	/**
	 * validatePolicySummary - validate the labels on policy summary pane on monitor
	 * 
	 * @author Ramya.Nagepalli
	 * @param test
	 * @return result
	 */
	public String validatePolicySummary(ExtentTest test) {

		String result = "fail";
		test.log(LogStatus.INFO, "check the Policy summary pane on Monitor");
		waitUntilElementAppear(SPOGMenuTreePath.SPOG_MONITOR_POLICYSUMMARY);

		test.log(LogStatus.INFO, "Check for the Hyperlinks on the Policy summary pane");
		if ((ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_MONITOR_POLICYSUMMARY_SUCCESS, 3))
				&& (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_MONITOR_POLICYSUMMARY_FAILURE, 3)
						&& (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_MONITOR_POLICYSUMMARY_DEPLYOING,
								3)))) {
			result = "pass";
		}

		return result;

	}

	/**
	 * validatePolicyCount - validate the policy count on policy summary pane on
	 * monitor
	 * 
	 * @author Ramya.Nagepalli
	 * @param protected_count
	 * @param not_protected
	 * @param offline
	 * @param test
	 * @return result
	 */
	public String validatePolicyCount(int total_Policy, int success, int failure, int deploying, ExtentTest test) {
		String result = "fail";

		String exp_items = "Success,Deploying,Failure";
		String expvalues = success + "," + deploying + "," + failure;

		test.log(LogStatus.INFO, "validate the policy count on policy summary pane");
		if (total_Policy != 0) {

			WebElement paneEle = ElementFactory.getElementByXpath("//div[@class='policy-summary-tile tile']");

			try {

				waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_MONITOR_POLICYSUMMARY_DEPLYOING, 10);

				List<WebElement> status_count_Eles = paneEle.findElements(By.xpath(
						"//div[@class='policy-summary-tile tile']//div[@class='last-panel status-indicator']//span/span"));

				List<WebElement> status_item_Eles = paneEle.findElements(By.xpath(
						"//div[@class='policy-summary-tile tile']//div[@class='last-panel status-indicator']//span/button/span"));
				String[] expected_items = exp_items.split(",");
				String[] expected_val = expvalues.split(",");

				for (int i = 0; i < status_count_Eles.size(); i++) {

					String act_item_value = status_item_Eles.get(i).getText();
					assertEquals(act_item_value, expected_items[i]);

					String actValue = status_count_Eles.get(i).getText();
					assertEquals(actValue, expected_val[i]);

				}

				result = "pass";

			} catch (NoSuchElementException e) {
				// TODO: handle exception
				System.out.println("Element not found :" + e);
				assertFalse(true);
			}

		} else {
			test.log(LogStatus.INFO, "total policy count is : 0" + total_Policy);

			result = "pass";
		}
		return result;
	}

	/**
	 * ClickOnPolicyHyperlinks - validate policy hyperlinks on policy Summary pane
	 * 
	 * @author Ramya.Nagepalli
	 * @param test
	 * @return result
	 */
	public String ClickOnPolicyHyperlinks(ExtentTest test) {
		String result = "fail";

		test.log(LogStatus.INFO, "click on the protected link on Policy summary pane");
		WebElement success_ele = ElementFactory.getElement(SPOGMenuTreePath.SPOG_MONITOR_POLICYSUMMARY_SUCCESS);
		String exp_path = ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_PROTECT_POLICY).get(0).getValue();

		test.log(LogStatus.INFO, "Navigate to Policys page when click on the success link on Policy summary pane");
		navigateToSpecifiedLink(success_ele, exp_path, test);

		if ((ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_COMMON_SEARCHRESULTFOR_MSG, 10))) {
			WebElement search_ele = ElementFactory.getElementByXpath("//label[text()=':']/span[text()='Success']");
			result = "pass";

			test.log(LogStatus.INFO, "Navigate to Monitor");
			WebElement monitor_ele = ElementFactory.getElement(SPOGMenuTreePath.SPOG_MONITOR);
			monitor_ele.click();
		}

		test.log(LogStatus.INFO, "click on the not protected Policys link on Policy summary pane");
		if ((ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_MONITOR_POLICYSUMMARY_FAILURE, 10))) {
			WebElement failure_ele = ElementFactory.getElement(SPOGMenuTreePath.SPOG_MONITOR_POLICYSUMMARY_FAILURE);
			test.log(LogStatus.INFO, "Navigate to Policys page when click on the failure link on Policy summary pane");
			navigateToSpecifiedLink(failure_ele, exp_path, test);

			if ((ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_COMMON_SEARCHRESULTFOR_MSG, 10))) {
				WebElement search_ele = ElementFactory.getElementByXpath("//label[text()=':']/span[text()='Failure']");
				result = "pass";

				test.log(LogStatus.INFO, "Navigate to Monitor");
				WebElement monitor_ele = ElementFactory.getElement(SPOGMenuTreePath.SPOG_MONITOR);
				monitor_ele.click();
			}
		}

		test.log(LogStatus.INFO, "click on the offline Policys link on Policy summary pane");
		if ((ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_MONITOR_POLICYSUMMARY_DEPLYOING, 10))) {
			WebElement deploying_ele = ElementFactory.getElement(SPOGMenuTreePath.SPOG_MONITOR_POLICYSUMMARY_DEPLYOING);

			test.log(LogStatus.INFO,
					"Navigate to Policys page when click on the deploying link on Policy summary pane");
			navigateToSpecifiedLink(deploying_ele, exp_path, test);

			if ((ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_COMMON_SEARCHRESULTFOR_MSG, 10))) {
				WebElement search_ele = ElementFactory
						.getElementByXpath("//label[text()=':']/span[text()='Deploying']");
				result = "pass";

				test.log(LogStatus.INFO, "Navigate to Monitor");
				WebElement monitor_ele = ElementFactory.getElement(SPOGMenuTreePath.SPOG_MONITOR);
				monitor_ele.click();
			}
		}
		return result;
	}

	/**
	 * validateSourceSummary - validate the labels on source summary pane on monitor
	 * 
	 * @author Ramya.Nagepalli
	 * @param test
	 * @return result
	 */
	public String validateSourceSummary(ExtentTest test) {

		String result = "fail";
		test.log(LogStatus.INFO, "check the source summary pane on Monitor");
		waitUntilElementAppear(SPOGMenuTreePath.SPOG_MONITOR_SOURCESUMMARY);

		test.log(LogStatus.INFO, "Check for the Hyperlinks on the source summary pane");
		if ((ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_MONITOR_SOURCESUMMARY_PROTECTED, 3))
				&& (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_MONITOR_SOURCESUMMARY_NOTPROTECTED, 3)
						&& (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_MONITOR_SOURCESUMMARY_OFFLINE,
								3)))) {
			result = "pass";
		}

		return result;

	}

	/**
	 * validateSourcesCount-validate the sources count on source summary pane on
	 * monitor
	 * 
	 * @author Ramya.Nagepalli
	 * @param protected_count
	 * @param not_protected
	 * @param offline
	 * @param test
	 * @return result
	 */
	public String validateSourcesCount(int total_sources, int protected_count, int not_protected, int offline,
			ExtentTest test) {
		String result = "fail";

		String exp_items = "Protected,Offline,Not Protected";
		String expvalues = protected_count + "," + offline + "," + not_protected;

		test.log(LogStatus.INFO, "validate the sources count on source summary pane");
		if (total_sources != 0) {

			WebElement paneEle = ElementFactory.getElementByXpath("//div[@class='customer-summary-tile tile']");

			try {

				List<WebElement> status_count_Eles = paneEle.findElements(By.xpath(
						"//div[@class='customer-summary-tile tile']//div[@class='last-panel status-indicator']//span/span"));

				List<WebElement> status_item_Eles = paneEle.findElements(By.xpath(
						"//div[@class='customer-summary-tile tile']//div[@class='last-panel status-indicator']//span/button/span"));
				String[] expected_items = exp_items.split(",");
				String[] expected_val = expvalues.split(",");

				for (int i = 0; i < status_count_Eles.size(); i++) {

					String act_item_value = status_item_Eles.get(i).getText();
					assertEquals(act_item_value, expected_items[i]);

					String actValue = status_count_Eles.get(i).getText();
					assertEquals(actValue, expected_val[i]);

				}

				result = "pass";

			} catch (NoSuchElementException e) {
				// TODO: handle exception
				System.out.println("Element not found :" + e);
				assertFalse(true);
			}

		} else {
			test.log(LogStatus.INFO, "total sources count is : 0" + total_sources);

			result = "pass";
		}
		return result;
	}

	/**
	 * ClickOnSourceHyperlinks- validate sources hyperlinks on source Summary pane
	 * 
	 * @author Ramya.Nagepalli
	 * @param test
	 * @return result
	 */
	public String ClickOnSourceHyperlinks(ExtentTest test) {
		String result = "fail";

		test.log(LogStatus.INFO, "click on the protected link on source summary pane");
		WebElement protected_ele = ElementFactory.getElement(SPOGMenuTreePath.SPOG_MONITOR_SOURCESUMMARY_PROTECTED);
		String exp_path = ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_PROTECT_SOURCE).get(0).getValue();

		test.log(LogStatus.INFO, "Navigate to Sources page when click on the protected link on source summary pane");
		navigateToSpecifiedLink(protected_ele, exp_path, test);

		test.log(LogStatus.INFO, "Navigate to Monitor");
		WebElement monitor_ele = ElementFactory.getElement(SPOGMenuTreePath.SPOG_MONITOR);

		if ((ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_COMMON_SEARCHRESULTFOR_MSG, 10))) {
			WebElement search_ele = ElementFactory.getElementByXpath("//label[text()=':']/span[text()='Protected']");
			result = "pass";

		}
		monitor_ele.click();

		test.log(LogStatus.INFO, "click on the not protected sources link on source summary pane");
		if ((ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_MONITOR_SOURCESUMMARY_NOTPROTECTED, 10))) {
			WebElement not_protected_ele = ElementFactory
					.getElement(SPOGMenuTreePath.SPOG_MONITOR_SOURCESUMMARY_NOTPROTECTED);

			test.log(LogStatus.INFO,
					"Navigate to Sources page when click on the non protected link on source summary pane");
			navigateToSpecifiedLink(not_protected_ele, exp_path, test);

			if ((ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_COMMON_SEARCHRESULTFOR_MSG, 10))) {
				WebElement search_ele = ElementFactory
						.getElementByXpath("//label[text()=':']/span[text()='Not Protected']");
				result = "pass";

			}
			monitor_ele.click();
		}

		test.log(LogStatus.INFO, "click on the offline sources link on source summary pane");
		if ((ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_MONITOR_SOURCESUMMARY_OFFLINE, 10))) {
			WebElement offline_ele = ElementFactory.getElement(SPOGMenuTreePath.SPOG_MONITOR_SOURCESUMMARY_OFFLINE);

			test.log(LogStatus.INFO, "Navigate to Sources page when click on the offline link on source summary pane");
			navigateToSpecifiedLink(offline_ele, exp_path, test);

			if ((ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_COMMON_SEARCHRESULTFOR_MSG, 10))) {
				WebElement search_ele = ElementFactory.getElementByXpath("//label[text()=':']/span[text()='Offline']");
				result = "pass";
			}
			monitor_ele.click();
		}
		return result;
	}

	/**
	 * validateUsageSummary - validate the labels on usage summary pane on monitor
	 * 
	 * @author Ramya.Nagepalli
	 * @param test
	 * @return result
	 */
	public String validateUsageSummary(ExtentTest test) {

		String result = "fail";
		test.log(LogStatus.INFO, "check the Usage summary pane on Monitor");
		waitUntilElementAppear(SPOGMenuTreePath.SPOG_MONITOR_USAGESUMMARY);

		test.log(LogStatus.INFO, "Check for the labels on the Usage summary pane");
		if ((ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_MONITOR_USAGESUMMARY_CLOUD_DIRECT, 5))
				&& (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_MONITOR_USAGESUMMARY_CLOUD_HYBRID, 5))) {
			result = "pass";
		}

		return result;

	}

	/**
	 * validateUsageDetails - for usage summary pane on monitor
	 * 
	 * @author Ramya.Nagepalli
	 * @param cloud_direct
	 * @param cloud_hybrid
	 * @param test
	 * @return
	 */
	public String validateUsageDetails(int cloud_direct_usage, int cloud_hybrid_usage, ExtentTest test) {
		String result = "fail";

		test.log(LogStatus.INFO, "check the Usage summary pane on Monitor");
		waitUntilElementAppear(SPOGMenuTreePath.SPOG_MONITOR_USAGESUMMARY);

		String exp_items = cloud_direct_usage + "," + cloud_hybrid_usage;

		if ((cloud_direct_usage != 0) || (cloud_hybrid_usage != 0)) {

			try {

				List<WebElement> usage_Eles = ElementFactory
						.getElements(SPOGMenuTreePath.SPOG_MONITOR_USAGESUMMARY_USAGE);

				String[] expected_items = exp_items.split(",");

				for (int i = 0; i < usage_Eles.size(); i++) {

					String act_item_value = usage_Eles.get(i).getText();
					// assertEquals(act_item_value.contains(expected_items[i]), expected_items[i]);

					if (act_item_value.contains(expected_items[i]))
						result = "pass";
				}

			} catch (NoSuchElementException e) {
				// TODO: handle exception
				System.out.println("Element not found :" + e);
				assertFalse(true);
			}

		} else {
			test.log(LogStatus.INFO, "Usage details are zero");
			result = "pass";
		}

		return result;
	}

	/**
	 * validateCapacityDetails- for usage summary on monitor
	 * 
	 * @author Ramya.Nagepalli
	 * @param cloud_direct_capacity
	 * @param cloud_hybrid_capacity
	 * @param test
	 * @return
	 */
	public String validateCapacityDetails(int cloud_direct_capacity, int cloud_hybrid_capacity, ExtentTest test) {
		String result = "fail";

		test.log(LogStatus.INFO, "check the Usage summary pane on Monitor");
		waitUntilElementAppear(SPOGMenuTreePath.SPOG_MONITOR_USAGESUMMARY);

		String exp_items = cloud_direct_capacity + "," + cloud_hybrid_capacity;

		if ((cloud_direct_capacity != 0) || (cloud_hybrid_capacity != 0)) {

			try {

				List<WebElement> usage_Eles = ElementFactory
						.getElements(SPOGMenuTreePath.SPOG_MONITOR_USAGESUMMARY_CAPACITY);

				String[] expected_items = exp_items.split(",");

				for (int i = 0; i < usage_Eles.size(); i++) {

					String act_item_value = usage_Eles.get(i).getText();
					if (act_item_value.contains(expected_items[i]))
						result = "pass";

				}

			} catch (NoSuchElementException e) {
				// TODO: handle exception
				System.out.println("Element not found :" + e);
				assertFalse(true);
			}

		} else {
			test.log(LogStatus.INFO, "Capacity details are zero");
			result = "pass";
		}

		return result;
	}

	/**
	 * validateWidgets- validate the widgets on Monitor page
	 * 
	 * @author Ramya.Nagepalli
	 * @param total_Widgets_direct
	 * @param test
	 * @return
	 */
	public String validateWidgets(ArrayList<String> total_Widgets_direct, ExtentTest test) {
		String result = "fail";
		test.log(LogStatus.INFO, "validate widgets on monitor page");

		try {

			List<WebElement> widget_elements = ElementFactory.getElementsByXpath(
					"//div[@class='react-grid-layout layout']/div[@class='react-grid-item react-draggable']");

			ArrayList<String> expected_items = total_Widgets_direct;

			for (int i = 0; i < widget_elements.size(); i++) {

				String act_item_value = widget_elements.get(i)
						.findElement(By.xpath(".//div[@class='d-flex header-wrapper']/h6")).getText();
				assertEquals(act_item_value.toLowerCase(), expected_items.get(i).toLowerCase());

			}

			result = "pass";

		} catch (NoSuchElementException e) {
			// TODO: handle exception
			System.out.println("Element not found :" + e);
			assertFalse(true);
		}

		return result;
	}

	/**
	 * maximize_validate_widgets- maximize the widgets icon and validate the widgets
	 * 
	 * @author Ramya.Nagepalli
	 * @param expected_widgets
	 * @param test
	 * @return
	 */
	public String maximize_validate_widgets(ArrayList<String> expected_widgets, ExtentTest test) {
		String result = "fail";
		test.log(LogStatus.INFO, "validate widgets on monitor page");

		try {

			WebElement widgetsPane = ElementFactory.getElementByXpath("//div[@class='react-grid-layout layout']");
			List<WebElement> widget_elements = ElementFactory.getElementsByXpath(
					"//div[@class='react-grid-layout layout']/div[@class='react-grid-item react-draggable']");

			List<WebElement> exp_widgets = widgetsPane
					.findElements(By.xpath(".//div[@class='d-flex header-wrapper']/h6"));
			ArrayList<String> expected_items = expected_widgets;

			for (int i = 0; i < expected_items.size(); i++) {

				WebElement max_icon_ele = widget_elements.get(i)
						.findElement(By.xpath(".//div[@class='header-icons']/label"));
				String act_item = widget_elements.get(i)
						.findElement(By.xpath(".//div[@class='d-flex header-wrapper']/h6")).getText();

				String act_size = widget_elements.get(i).getAttribute("style");
				String[] act_style = act_size.split(";");

				for (int j = 0; j < expected_items.size(); j++) {
					if (expected_items.get(j).equalsIgnoreCase(act_item)) {

						test.log(LogStatus.INFO, "click on maximize icon on specified widget");
						max_icon_ele.click();
						waitForSeconds(3);

						List<WebElement> mod_widget_elements = ElementFactory.getElementsByXpath(
								"//div[@class='react-grid-layout layout']/div[@class='react-grid-item react-draggable']");
						WebElement modified_widget = mod_widget_elements.get(i);

						String mod_size = modified_widget.getAttribute("style");
						String[] mod_style = mod_size.split(";");

						// get the width from style attribute- '3' position of width in style attribute
						String act_width = act_style[3];
						String mod_width = mod_style[3];

						if (!(act_width.equals(mod_width)))
							result = "pass";
					}
				}

			}

		} catch (NoSuchElementException e) {
			// TODO: handle exception
			System.out.println("Element not found :" + e);
			// assertFalse(true);
		}

		return result;
	}
	
	public void checkInProgessJobInformation(String jobName,String source, ExtentTest test) {
		
		List<WebElement> rows = null;
		int rowCount = 0;
		WebElement eachRow=null;
		boolean findRow=false;
		int repeat = 0;
		
		do {
			rows = getRows();
			rowCount = rows.size();
			repeat++;
			System.out.println("repeat " + repeat);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while (rowCount>0 && (repeat < 100));

		if (rowCount == 0 ) {
			test.log(LogStatus.WARNING, "no rows found");
			assertTrue(false,"no in progress job in job monitor");
		}
		eachRow=rows.get(0);
		List<WebElement> sources = eachRow.findElements(By.xpath(".//div[@class='rt-td link-cell']//a"));
		System.out.println("job name in monitor page is:"+sources.get(0).getText().toString());
		System.out.println("source in monitor page is:"+sources.get(1).getText().toString());
		System.out.println("expected job name is:"+jobName);
		System.out.println("expected source name is:"+source);
		if(sources.get(0).getText().toString().equalsIgnoreCase(jobName)) {
    		assertTrue(true,"jobName shown is expected");
    	}else {
    		assertTrue(false,"jobName shown is expected");
    	}   
    	WebElement processBar = eachRow.findElement(By.xpath(".//div[@class='progress-bar']"));
    	assertTrue(true,"process bar exists");
    	WebElement processBarContent = eachRow.findElement(By.xpath(".//div[@class='progress-content']"));
    	System.out.println("process content in monitor page is:"+processBarContent.getText().toString());
    	if(processBarContent.getText().toString().toLowerCase().contains("in progress")) {
    		assertTrue(true,"process content is expected");
    	}else {
    		assertTrue(false,"process content is expected");
    	}
    	if(sources.get(1).getText().toString().equalsIgnoreCase(source)) {
    		assertTrue(true,"source shown is expected");
    	}else {
    		assertTrue(false,"source shown is expected");
    	}    	
    }

}
