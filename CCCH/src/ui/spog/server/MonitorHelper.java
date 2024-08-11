package ui.spog.server;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import ui.base.common.BasePage;
import ui.spog.pages.monitor.MonitorPage;

public class MonitorHelper extends SPOGUIServer {

	BasePage basepage = new BasePage();
	MonitorPage monitorPage = new MonitorPage();

	public MonitorHelper(String browserType, int maxTimeWaitSec) {
		super(browserType, maxTimeWaitSec);
		// TODO Auto-generated constructor stub
	}

	/**
	 * validateSourceSummaryPane - method to validate the sources count, hyperlink
	 * navigation, labels on the sources summary pane
	 * 
	 * @author Ramya.Nagepalli
	 * @param total_sources
	 * @param protected_count
	 * @param not_protected
	 * @param offline
	 * @param test
	 */
	public void validateSourceSummaryPane(int total_sources, int protected_count, int not_protected, int offline,
			ExtentTest test) {
		String result = "fail";

		test.log(LogStatus.INFO, "validate the source summary pane on Monitor");
		result = monitorPage.validateSourceSummary(test);

		test.log(LogStatus.INFO, "Check the Result");
		assertEquals(result, "pass");

		test.log(LogStatus.INFO, "validate the sources count in the source summary pane");
		result = monitorPage.validateSourcesCount(total_sources, protected_count, not_protected, offline, test);

		test.log(LogStatus.INFO, "Check the Result");
		assertEquals(result, "pass");

		test.log(LogStatus.INFO, "click on the hyperlinks on the pane");
		result = monitorPage.ClickOnSourceHyperlinks(test);

		test.log(LogStatus.INFO, "Check the Result");
		assertEquals(result, "pass");

	}

	/**
	 * validatePolicySummaryPane - method to validate the policy count, hyperlink
	 * navigation, labels on the policy summary pane
	 * 
	 * @author Ramya.Nagepalli
	 * @param total_Policy
	 * @param success
	 * @param failure
	 * @param deploying
	 * @param test
	 */
	public void validatePolicySummaryPane(int total_Policy, int success, int failure, int deploying, ExtentTest test) {

		String result = "fail";

		test.log(LogStatus.INFO, "validate the policy summary pane on Monitor");
		result = monitorPage.validatePolicySummary(test);

		test.log(LogStatus.INFO, "Check the Result");
		assertEquals(result, "pass");

		test.log(LogStatus.INFO, "validate the policy count in the policy summary pane");
		result = monitorPage.validatePolicyCount(total_Policy, success, failure, deploying, test);

		test.log(LogStatus.INFO, "Check the Result");
		assertEquals(result, "pass");

		test.log(LogStatus.INFO, "click on the hyperlinks on the pane");
		result = monitorPage.ClickOnPolicyHyperlinks(test);

		test.log(LogStatus.INFO, "Check the Result");
		assertEquals(result, "pass");
	}

	/**
	 * maximize_validate_widgets
	 * 
	 * @author Ramya.Nagepalli
	 * @param expected_widgets
	 * @param test
	 */
	public void maximize_validate_widgets(ArrayList<String> expected_widgets, ExtentTest test) {
		String result = "fail";

		test.log(LogStatus.INFO, "validate the maximize widgets on Monitor");
		result = monitorPage.maximize_validate_widgets(expected_widgets, test);

		test.log(LogStatus.INFO, "Check the Result");
		assertEquals(result, "pass");
	}

	/**
	 * validateWidgets
	 * 
	 * @author Ramya.Nagepalli
	 * @param total_Widgets_direct
	 * @param test
	 */
	public void validateWidgets(ArrayList<String> total_Widgets_direct, ExtentTest test) {
		String result = "fail";

		test.log(LogStatus.INFO, "validate the widgets on Monitor");
		result = monitorPage.validateWidgets(total_Widgets_direct, test);

		test.log(LogStatus.INFO, "Check the Result");
		assertEquals(result, "pass");
	}

	/**
	 * validateAlerts- validate the labels and total alerts count
	 * 
	 * @author Ramya.Nagepalli
	 * @param expected_input
	 * @param test
	 */
	public void validateAlerts(String expected_input, ExtentTest test) {
		String result = "fail";

		test.log(LogStatus.INFO, "validate the Alerts on Monitor");
		result = monitorPage.validateAlerts(test);

		test.log(LogStatus.INFO, "Check the Result");
		assertEquals(result, "pass");

		test.log(LogStatus.INFO, "validate the Alerts on Monitor");
		result = monitorPage.AcknowledgeAlerts(expected_input, test);

		test.log(LogStatus.INFO, "Check the Result");
		assertEquals(result, "pass");
	}

	/**
	 * validateCustomerSummary
	 * 
	 * @author Ramya.Nagepalli
	 * @param total_count
	 *            - total customers count from API
	 * @param test
	 */
	public void validateCustomerSummary(int total_count, ExtentTest test) {
		String result = "fail";

		test.log(LogStatus.INFO, "validate the custmoer summary pane on Monitor");
		result = monitorPage.validateCustomerSummary(total_count, test);

		test.log(LogStatus.INFO, "Check the Result");
		assertEquals(result, "pass");
	}

	/**
	 * validateUsageSummaryAcrossCustomers
	 * 
	 * @author Ramya.Nagepalli
	 *
	 * @param cloud_direct_usage
	 * @param cloud_hybrid_usage
	 * @param test
	 */
	public void validateUsageSummaryAcrossCustomers(int cloud_direct_usage, int cloud_hybrid_usage,
			int cloud_direct_capacity, int cloud_hybrid_capacity, ExtentTest test) {
		String result = "fail";

		test.log(LogStatus.INFO, "validate the Usage summary across customers pane on Monitor");
		result = monitorPage.validateUsageSummaryAcrossCustomers(test);

		test.log(LogStatus.INFO, "Check the Result");
		assertEquals(result, "pass");

		test.log(LogStatus.INFO, "validate the Usage summary across customers pane on Monitor");
		result = monitorPage.validateUsageDetails(cloud_direct_usage, cloud_hybrid_usage, test);

		test.log(LogStatus.INFO, "Check the Result");
		assertEquals(result, "pass");

		test.log(LogStatus.INFO, "validate the Usage summary across customers pane on Monitor");
		result = monitorPage.validateCapacityDetails(cloud_direct_capacity, cloud_hybrid_capacity, test);

		test.log(LogStatus.INFO, "Check the Result");
		assertEquals(result, "pass");
	}

	/**
	 * validateSourceSummaryAcrossCustomers
	 * 
	 * @author Ramya.Nagepalli
	 * @param total_count
	 *            - total customers count from API
	 * @param test
	 */
	public void validateSourceSummaryAcrossCustomers(int total_count, int protected_count, int not_protected,
			int offline, ExtentTest test) {
		String result = "fail";

		test.log(LogStatus.INFO, "validate the source summary across customers pane on Monitor");
		result = monitorPage.validateSourceSummaryAcrossCustomers(total_count, test);

		test.log(LogStatus.INFO, "Check the Result");
		assertEquals(result, "pass");

		test.log(LogStatus.INFO, "validate the count of source summary across customers pane on Monitor");
		result = monitorPage.validateSourcesCountAcrossCustomers(total_count, protected_count, not_protected, offline,
				test);

		test.log(LogStatus.INFO, "Check the Result");
		assertEquals(result, "pass");
	}

	/**
	 * validateUsageSummary
	 * 
	 * @author Ramya.Nagepalli
	 *
	 * @param cloud_direct_usage
	 * @param cloud_hybrid_usage
	 * @param test
	 */
	public void validateUsageSummary(int cloud_direct_usage, int cloud_hybrid_usage, int cloud_direct_capacity,
			int cloud_hybrid_capacity, ExtentTest test) {
		String result = "fail";

		test.log(LogStatus.INFO, "validate the Usage summary across customers pane on Monitor");
		result = monitorPage.validateUsageSummary(test);

		test.log(LogStatus.INFO, "Check the Result");
		assertEquals(result, "pass");

		test.log(LogStatus.INFO, "validate the Usage summary across customers pane on Monitor");
		result = monitorPage.validateUsageDetails(cloud_direct_usage, cloud_hybrid_usage, test);

		test.log(LogStatus.INFO, "Check the Result");
		assertEquals(result, "pass");

		test.log(LogStatus.INFO, "validate the Usage summary across customers pane on Monitor");
		result = monitorPage.validateCapacityDetails(cloud_direct_capacity, cloud_hybrid_capacity, test);

		test.log(LogStatus.INFO, "Check the Result");
		assertEquals(result, "pass");
	}
	
	public void checkInProgessJobInformation(String jobName,String source, ExtentTest test) {
		test.log(LogStatus.INFO, "Navigate to monitor page");
		for(int i=0;i<8;i++) {
			goToMonitorPage();
			try {
				Thread.sleep(3000);
				System.out.println("this is "+i+" time to go to minotor page");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
		test.log(LogStatus.INFO, "validate in progress job information on Monitor");
		monitorPage.checkInProgessJobInformation( jobName, source,test);
	}
}
