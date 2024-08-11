package ui.spog.pages.protect;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.net.smtp.SMTP;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.SpogMessageCode;
import genericutil.ErrorHandler;
import io.restassured.response.Response;
import ui.base.common.BasePage;
import ui.base.common.SPOGMenuTreePath;
import ui.base.elements.Button;
import ui.base.elements.Link;
import ui.base.elements.TextField;
import ui.base.factory.BrowserFactory;
import ui.base.factory.ElementFactory;

public class DestinationsPage extends BasePage {

	private String ManageSavedSearch_tooltip_info_msg = "This window shows all the Saved Searches for this table view. Select a Saved Search from the list on the left to view its relevant details. You can modify the search criteria, rename or delete from here.";
	private String updated_savedSearch_msg = "Saved search updated successfully.";
	private String deleted_savedSearch_msg = "Saved search deleted successfully.";
	private String created_savedSearch_msg = "Saved search created successfully.";

	private ErrorHandler errorHandle = BasePage.getErrorHandler();

	/**
	 * goToDestinationsPage - navigate to destinations page
	 * 
	 * @author Ramya.Nagepalli
	 * @return destinations page
	 *//*
	public DestinationsPage goToDestinationsPage() {

		Link destinationsLink = new Link(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS);
		destinationsLink.click();
		waitUntilElementAppear(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_ADD_CLOUD_VOLUME);
		DestinationsPage destinationsPage = new DestinationsPage();
		return destinationsPage;
	}*/

	/**
	 * goToCloudDirectDestinationsPage - navigate to destinations page
	 * 
	 * @author Ramya.Nagepalli
	 * @return destinations page
	 */
	public DestinationsPage goToCloudDirectDestinationsPage() {

		Link destinationsCDLink = new Link(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_CLOUD_DIRECT_VOLUMES);
		destinationsCDLink.click();
		waitUntilElementAppear(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_ADD_CLOUD_VOLUME);
		DestinationsPage destinationsPage = new DestinationsPage();
		return destinationsPage;
	}

	/**
	 * goToCloudHybridDestinationsPage - navigate to destinations page
	 * 
	 * @author Ramya.Nagepalli
	 * @return destinations page
	 */
	public DestinationsPage goToCloudHybridDestinationsPage() {

		Link destinationsCDLink = new Link(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_CLOUD_HYBRID_VOLUMES);
		destinationsCDLink.click();
		DestinationsPage destinationsPage = new DestinationsPage();
		return destinationsPage;
	}

	/**
	 * ClickAddVolumeBtn - Click the 'Add Cloud Volume' Button which pop up new
	 * window
	 * 
	 * @author Ramya.Nagepalli
	 * @param test
	 */
	public void ClickAddVolumeBtn(ExtentTest test) {

		test.log(LogStatus.INFO, "wait until Add Cloud Volume Button is clickable");
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_ADD_CLOUD_VOLUME,
				BrowserFactory.getMaxTimeWaitUIElement());
		errorHandle.printInfoMessageInDebugFile("click Add Cloud Volume button");

		test.log(LogStatus.INFO, "Click Add Cloud Volume Button");
		Button addCloudVolBtn = new Button(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_ADD_CLOUD_VOLUME);
		addCloudVolBtn.click();
	}

	/**
	 * CreateSaveSearch - Method is used to save search with given input filters
	 * 
	 * @author Ramya.Nagepalli
	 * @param search_name
	 * @param policy_search
	 * @param saveSearchName
	 * @param test
	 * @return result
	 */
	public String CreateSaveSearch(String search_name, String policy_search, String saveSearchName, ExtentTest test) {

		String Result = "fail";

		test.log(LogStatus.INFO, "search the destination with volume_name and policy_name");
		errorHandle.printInfoMessageInDebugFile("search the destination with volume_name and policy_name");
		waitUntilElementAppear(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_SEARCH_INPUT);

		if (!(search_name.equals("") || search_name.equals(null))) {

			test.log(LogStatus.INFO, "get the Element of search string ");
			WebElement search_ele = ElementFactory.getElement(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_SEARCH_INPUT);
			search_ele.clear();
			waitForSeconds(2);
			search_ele.sendKeys(search_name);

			test.log(LogStatus.INFO, "click search string button");
			WebElement submit_ele = ElementFactory
					.getElement(SPOGMenuTreePath.SPOGPROTECT_DESTINATIONS_SEARCH_STRING_SUBMIT_BUTTON);
			submit_ele.sendKeys(Keys.ENTER);

		}

		if (!(policy_search.equals("") || policy_search.equals(null))) {

			test.log(LogStatus.INFO, "get the Element of policy search ");
			Button search_filter_ele = new Button(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_SEARCH_FILTER_BUTTON);
			search_filter_ele.click();

			test.log(LogStatus.INFO, "wait until policy dropdown appears ");
			waitUntilElementAppear(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_POLICYFILTER_DROPDOWN);

			test.log(LogStatus.INFO, "get and click on policy dropdown");
			Button filter_ele = new Button(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_POLICYFILTER_DROPDOWN);
			filter_ele.click();

			test.log(LogStatus.INFO, "get the textfeild and send given input on policy dropdown");
			WebElement policy_filter_input = ElementFactory
					.getElement(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_POLICY_SEARCH_INPUT);

			policy_filter_input.sendKeys(policy_search);
			policy_filter_input.sendKeys(Keys.TAB);

			test.log(LogStatus.INFO, "click on search button in search filter form ");
			Button search_button = new Button(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_SEARCH_FILTER);
			if (search_button.isEnabled())
				search_button.click();
		}

		test.log(LogStatus.INFO, "wait for save search button appears on form ");
		errorHandle.printInfoMessageInDebugFile("wait for save search button appears on form");
		waitUntilElementAppear(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_SAVE_SEARCH);

		test.log(LogStatus.INFO, "get the element of savesearch button");
		Button Savesearch_button = new Button(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_SAVE_SEARCH);

		if (Savesearch_button.isEnabled()) {

			test.log(LogStatus.INFO, "Click savesearch button");
			Savesearch_button.click();

			test.log(LogStatus.INFO, "Enter savesearch name on form");
			WebElement save_search_input = ElementFactory
					.getElement(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_SAVE_SEARCH_INPUT);
			save_search_input.sendKeys(saveSearchName);

			test.log(LogStatus.INFO, "Click savesearch button on form");
			WebElement save_search_button = ElementFactory
					.getElement(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_SAVE_SEARCH_BUTTON);
			if (save_search_button.isEnabled()) {
				save_search_button.click();

				test.log(LogStatus.INFO, "get the alert msg compare with the expected : " + created_savedSearch_msg);
				waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_TOASTMESSAGE);
				WebElement toast_ele = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_TOASTMESSAGE);
				assertEquals(toast_ele.getText(), created_savedSearch_msg);

				Result = "pass";
			}
		}

		return Result;
	}

	/**
	 * verifyAddedCloudVolume - method used to validate the added cloud volume
	 * inputs on UI with API response
	 * 
	 * @author Ramya.Nagepalli
	 * @param response
	 * @param exp_volumeName
	 * @param exp_datacenter
	 * @param test
	 * @return result
	 */
	public String verifyAddedCloudVolume(Response response, String exp_volumeName, String exp_datacenter,
			ExtentTest test) {

		String result = "fail";

		test.log(LogStatus.INFO, "get the data from API response");
		ArrayList<HashMap<String, Object>> VolumesHashMap = new ArrayList<HashMap<String, Object>>();
		if (response != null) {
			VolumesHashMap = response.then().extract().path("data");
		}
		waitForSeconds(3);

		test.log(LogStatus.INFO, "get list of Destination table headers");
		List<WebElement> DestinationTableHeaders = getVolumeTableHeaders();
		HashMap<String, Integer> DestinationTableHeaderOrder = getTableHeaderOrder(DestinationTableHeaders);

		test.log(LogStatus.INFO, "get list of Destination table rows");
		List<WebElement> rows = getDestinationList();

		test.log(LogStatus.INFO, "Check the count of response data with rows ");
		assertEquals(1, VolumesHashMap.size());

		String exp_datacenter_Location = exp_datacenter;
		String exp_datacenter_region = null;

		if (exp_datacenter.equals("Zetta Test") || exp_datacenter.equals("Zetta Stage")) {
			exp_datacenter_region = "US";
		} else {
			exp_datacenter_region = "EU";
		}

		/*
		 * test.log(LogStatus.INFO,
		 * "sort on name to get the created destination on the top"); WebElement
		 * sort_ele =
		 * ElementFactory.getElement(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_SORTNAME
		 * ); sort_ele.click();
		 */

		test.log(LogStatus.INFO, "Compare the created volume data in the rows from UI with response data");
		for (int i = 0; i < 1; i++) {

			// get UI item
			WebElement eachRow = rows.get(i);
			String xpath = "." + ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_TABLE_COLUMNS).get(0)
					.getValue();
			List<WebElement> columns = eachRow.findElements(By.xpath(xpath));

			HashMap<String, Object> apiDestination = VolumesHashMap.get(i);

			System.out.println("Destination_name :" + apiDestination.get("destination_name"));
			assertEquals(apiDestination.get("destination_name"), exp_volumeName);

			System.out.println("datacenter_location :" + apiDestination.get("datacenter_location"));
			assertEquals(apiDestination.get("datacenter_location"), exp_datacenter_Location);

			System.out.println("datacenter_region :" + apiDestination.get("datacenter_region"));
			assertEquals(apiDestination.get("datacenter_region"), exp_datacenter_region);

			Set<String> keySet = DestinationTableHeaderOrder.keySet();
			Iterator<String> keyIter = keySet.iterator();
			while (keyIter.hasNext()) {
				String headerName = keyIter.next();
				int order = DestinationTableHeaderOrder.get(headerName);
				if (headerName.equalsIgnoreCase("Name")) {
					if (apiDestination.get("destination_name").toString().equalsIgnoreCase(exp_volumeName)) {
						assertEquals(columns.get(order).getText(), exp_volumeName);
					}
				} else if (headerName.equalsIgnoreCase("Location")) {
					assertEquals(columns.get(order).getText(), apiDestination.get("datacenter_location"));
				} else if (headerName.equalsIgnoreCase("Data Center Region")) {
					assertEquals(columns.get(order).getText(), apiDestination.get("datacenter_region"));
				} else if (headerName.equalsIgnoreCase("Storage Usage")) {
					if (!(columns.get(order).getText().equals("-"))) {
						assertEquals(columns.get(order).getText(), apiDestination.get("storage_usage"));
					}
				} else if (headerName.equalsIgnoreCase("Latest Job")) {
					if (!(columns.get(order).getText().equals("-"))) {
						assertEquals(columns.get(order).getText(), apiDestination.get("last_job"));
					}
				} else if (headerName.equalsIgnoreCase("Type")) {
					assertEquals(columns.get(order).getText(), apiDestination.get("destination_type"));
				} else if (headerName.equalsIgnoreCase("Retention")) {
					assertEquals(columns.get(order).getText(), apiDestination.get("retention"));
				} else if (headerName.equalsIgnoreCase("Source Count")) {
					if (!(columns.get(order).getText().equals("-")))
						assertEquals(columns.get(order).getText(), apiDestination.get("source_count"));
				} else if (headerName.equalsIgnoreCase("Protection Policy")) {
					if (!(columns.get(order).getText().equals("-")))
						assertEquals(columns.get(order).getText(), apiDestination.get("policy_name"));
				}
			}
			result = "pass";
		}

		return result;
	}

	/**
	 * get rows of Destination table
	 * 
	 * @author Ramya.Nagepalli
	 * @return
	 */
	public List<WebElement> getDestinationList() {

		return ElementFactory.getElements(SPOGMenuTreePath.SPOG_COMMON_TABLE_ROWS);
	}

	/**
	 * @author Ramya.Nagepalli
	 * @param headerTable
	 * @return
	 */
	public HashMap<String, Integer> getTableHeaderOrder(List<WebElement> headerTable) {

		HashMap<String, Integer> headerOrderMap = new HashMap<String, Integer>();
		for (int i = 0; i < headerTable.size(); i++) {
			WebElement eachHeader = headerTable.get(i);
			String text = eachHeader.getText();
			if ((text != null) && !text.equalsIgnoreCase("")) {
				headerOrderMap.put(text, i);
			}
		}
		return headerOrderMap;
	}

	/**
	 * Get user table headers
	 * 
	 * @author Ramya.Nagepalli
	 */
	public List<WebElement> getVolumeTableHeaders() {

		List<WebElement> headers = ElementFactory.getElements(SPOGMenuTreePath.SPOG_COMMON_TABLE_HEADERS);
		return headers;
	}

	/**
	 * cancelAddCloudVolume - Method used to close the Add Cloud Volume Window
	 * 
	 * @author Ramya.Nagepalli
	 * @param test
	 */
	public void cancelAddCloudVolume(ExtentTest test) {

		test.log(LogStatus.INFO, " click on cancel button on the Add Cloud Volume form");
		WebElement cancel_element = ElementFactory
				.getElement(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_ADD_CLOUD_VOLUME_CANCEL);
		cancel_element.click();
	}

	/**
	 * AddCloudVolume_invalid - method for invalid scenarios to add cloud volume for
	 * the organization
	 * 
	 * @author Ramya.Nagepalli
	 * @param volume_name
	 * @param datacenter
	 * @param retention
	 * @param Comment
	 * @param expected_msg
	 * @param test
	 * @return
	 */
	public String AddCloudVolume(String volume_name, String datacenter, String retention, String Comment,
			String expected_msg, ExtentTest test) {

		String result = "fail";

		test.log(LogStatus.INFO, "wait Add cloud volume form apppears");
		errorHandle.printInfoMessageInDebugFile("wait Add cloud volume form apppears");
		waitUntilElementAppear(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_ADD_CLOUD_VOLUME_ACCOUNTNAME);

		test.log(LogStatus.INFO, "wait for Add cloud volume button apppears");
		waitUntilElementAppear(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_ADD_CLOUD_VOLUME_BUTTON);

		test.log(LogStatus.INFO, "Get the textfield of volume name and set the text with given input :" + volume_name);
		TextField VolumeNameEle = new TextField(
				SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_ADD_CLOUD_VOLUME_VOLUME_NAME);
		VolumeNameEle.clear();

		if (!(volume_name.equals(null) || volume_name.equals(""))) {
			VolumeNameEle.setText(volume_name);
		} else {

			test.log(LogStatus.INFO, "get the volume_name element and pass the given input");
			WebElement vol_ele = ElementFactory
					.getElement(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_ADD_CLOUD_VOLUME_COMMENT);
			vol_ele.clear();
			vol_ele.sendKeys(Keys.TAB);

			test.log(LogStatus.INFO, "wait for volume error message apppears on the form");
			waitUntilElementAppear(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_ADD_CLOUD_VOLUME_VOLUME_NAME_ERROR);
		}

		if (!(retention.equals(""))) {

			test.log(LogStatus.INFO, "get the retention dropdown element and pass the given input");
			WebElement retention_element = ElementFactory
					.getElement(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_ADD_CLOUD_VOLUME_RETENTION);
			retention_element.click();
			WebElement Retention = ElementFactory.getElementByXpath(".//span[text()='" + retention + "']");
			Retention.click();
		}

		if (!(datacenter.equals(""))) {

			test.log(LogStatus.INFO, "get the Datacenter dropdown element and pass the given input");
			WebElement datacenter_element = ElementFactory
					.getElement(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_ADD_CLOUD_VOLUME_DATACENTER);
			datacenter_element.click();
			WebElement Datacenter = ElementFactory
					.getElementByXpath("//a[@id='MenuItem_0']//span[text()='" + datacenter + "']");
			Datacenter.click();
		}

		test.log(LogStatus.INFO, "get the Comments textfield element and pass the given input");
		TextField Comments_ele = new TextField(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_ADD_CLOUD_VOLUME_COMMENT);
		Comments_ele.setText(Comment);

		test.log(LogStatus.INFO, "wait Add cloud volume button enables on form");
		errorHandle.printInfoMessageInDebugFile("wait Add cloud volume button enables on form");
		Button addCloudVolumeBtn = new Button(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_ADD_CLOUD_VOLUME_BUTTON);

		if (volume_name.length() > 256) {

			if (addCloudVolumeBtn.isEnabled()) {
				addCloudVolumeBtn.click();
				waitForSeconds(1);

				test.log(LogStatus.INFO, "wait for error message appears on form :" + expected_msg);
				waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_TOASTMESSAGE);

				WebElement toast_msg_ele = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_TOASTMESSAGE);

				String act_msg = toast_msg_ele.getText().toString();

				assertEquals(act_msg, expected_msg);

				result = "pass";

			}
		} else if (addCloudVolumeBtn.isEnabled() && expected_msg.equals("")) {
			addCloudVolumeBtn.click();
			waitForSeconds(1);

			test.log(LogStatus.INFO, "wait until toast message displayed on UI");
			waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_TOASTMESSAGE);

			WebElement toast_msg_ele = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_TOASTMESSAGE);

			String act_msg = toast_msg_ele.getText().toString();

			String exp_msg = "Destination " + volume_name + " added successfully.";

			test.log(LogStatus.INFO, "Validate the actual message with the expected message");
			assertEquals(act_msg, exp_msg);

			result = "pass";
		} else {
			Boolean state = addCloudVolumeBtn.isEnabled();

			if (state.equals(true)) {
				result = "fail";
			} else {
				result = "pass";
			}

			test.log(LogStatus.INFO, "Click on 'Cancel' button to close the Form");
			cancelAddCloudVolume(test);
		}

		return result;

	}

	/**
	 * validateCreatedSaveSearch - method used to validate the created save search
	 * 
	 * @author Ramya.Nagepalli
	 * @param response
	 * @param exp_volumeName
	 * @param exp_datacenter
	 * @param searchString
	 * @param test
	 * @return Result
	 */
	public String validateCreatedSaveSearch(Response response, String exp_volumeName, String exp_datacenter,
			String searchString, ExtentTest test) {
		String result = "fail";

		test.log(LogStatus.INFO, "get the data from API response");
		ArrayList<HashMap<String, Object>> VolumesHashMap = new ArrayList<HashMap<String, Object>>();
		if (response != null) {
			VolumesHashMap = response.then().extract().path("data");
		}
		waitForSeconds(3);

		test.log(LogStatus.INFO, "get list of Destination table headers");
		List<WebElement> DestinationTableHeaders = getVolumeTableHeaders();
		HashMap<String, Integer> DestinationTableHeaderOrder = getTableHeaderOrder(DestinationTableHeaders);

		test.log(LogStatus.INFO, "get list of Destination table rows");
		List<WebElement> rows = getDestinationList();

		test.log(LogStatus.INFO, "Check the count of response data with rows ");
		assertEquals(rows.size(), VolumesHashMap.size());

		String exp_datacenter_Location = exp_datacenter;
		String exp_datacenter_region = null;

		if (exp_datacenter.equals("Zetta Test") || exp_datacenter.equals("Zetta Stage")) {
			exp_datacenter_region = "US";
		} else {
			exp_datacenter_region = "EU";
		}
		test.log(LogStatus.INFO, "Compare the data in the rows from UI with response data");

		for (int i = 0; i < 1; i++) {

			// get UI item
			WebElement eachRow = rows.get(i);
			String xpath = "." + ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_TABLE_COLUMNS).get(0)
					.getValue();
			List<WebElement> columns = eachRow.findElements(By.xpath(xpath));

			HashMap<String, Object> apiDestination = VolumesHashMap.get(i);

			System.out.println(apiDestination.get("destination_name"));
			assertEquals(apiDestination.get("destination_name"), exp_volumeName);

			Set<String> keySet = DestinationTableHeaderOrder.keySet();
			Iterator<String> keyIter = keySet.iterator();
			while (keyIter.hasNext()) {
				String headerName = keyIter.next();
				int order = DestinationTableHeaderOrder.get(headerName);
				if (headerName.equalsIgnoreCase("Name")) {
					if (apiDestination.get("destination_name").toString().equalsIgnoreCase(exp_volumeName)) {
						assertEquals(columns.get(order).getText(), exp_volumeName);
					}
				} else if (headerName.equalsIgnoreCase("Location")) {
					assertEquals(columns.get(order).getText(), exp_datacenter_Location);
				} else if (headerName.equalsIgnoreCase("Data Center Region")) {
					assertEquals(columns.get(order).getText(), exp_datacenter_region);
				} else if (headerName.equalsIgnoreCase("Storage Usage")) {
					if (!(columns.get(order).getText().equals("-"))) {
						// assertEquals(columns.get(order).getText(), "-");
					}
				} else if (headerName.equalsIgnoreCase("Latest Job")) {
					if (!(columns.get(order).getText().equals("-"))) {
						// assertEquals(columns.get(order).getText(), apiDestination.get("last_job"));
					}
				} else if (headerName.equalsIgnoreCase("Type")) {

					if (apiDestination.get("destination_type").equals("cloud_direct_volume"))
						assertEquals(columns.get(order).getText(), "Cloud Direct Volume");
				} else if (headerName.equalsIgnoreCase("Retention")) {

					HashMap<String, String> cd_vol = new HashMap<>();
					cd_vol = (HashMap<String, String>) apiDestination.get("cloud_direct_volume");
					assertEquals(columns.get(order).getText(), cd_vol.get("retention_name"));
				} else if (headerName.equalsIgnoreCase("Source Count")) {
					if (!(columns.get(order).getText().equals("-")))
						assertEquals(columns.get(order).getText(), apiDestination.get("source_count"));
				} else if (headerName.equalsIgnoreCase("Protection Policy")) {
					if (!(columns.get(order).getText().equals("-"))) {
						HashMap<String, String> cd_policy = new HashMap<>();
						cd_policy = (HashMap<String, String>) apiDestination.get("policies");
						assertEquals(columns.get(order).getText(), cd_policy.get("policy_name"));
					}
				}
			}
			result = "pass";
		}
		return result;
	}

	/**
	 * getSavedSearch - method used to get the created save search and applies the
	 * search by clicking the save search link
	 * 
	 * @author Ramya.Nagepalli
	 * @param SaveSearchName
	 * @param test
	 */
	public void getSavedSearch(String SaveSearchName, ExtentTest test) {

		test.log(LogStatus.INFO, "wait Add cloud volume form apppears");
		errorHandle.printInfoMessageInDebugFile("wait Add cloud volume form apppears");
		waitUntilElementAppear(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_CREATED_SAVED_SEARCHES);

		test.log(LogStatus.INFO, "get the element and click on the given saved search from UI");
		WebElement saveSearch_ele = ElementFactory.getElementByXpath(
				"//div[@class='quick-filter-wrapper d-flex']//button[text()='" + SaveSearchName + "']");

		if (!(saveSearch_ele.isEnabled()))
			saveSearch_ele.click();
	}
	/*
		*//**
			 * manageDestinationColumns - method used to select destination columns with the
			 * given inputs
			 * 
			 * @author Ramya.Nagepalli
			 * @param inputs
			 * @param test
			 * @return result
			 *//*
				 * public String manageDestinationColumns(String[] inputs, ExtentTest test) {
				 * String Result = "fail";
				 * 
				 * String given_inputs[] = inputs;
				 * 
				 * waitForSeconds(3); test.log(LogStatus.INFO,
				 * "Click on settings button on Destinations page "); WebElement settings_ele =
				 * ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_SETTINGSBTN);
				 * settings_ele.click();
				 * 
				 * for (int i = 0; i < given_inputs.length; i++) { if
				 * (given_inputs[i].contains("type")) {
				 * 
				 * test.log(LogStatus.INFO,
				 * "wait for columns checkboxes appear when click on settings button");
				 * waitUntilElementAppear(SPOGMenuTreePath.
				 * SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGE_COLUMNS_TYPE); WebElement
				 * input_ele = ElementFactory .getElement(SPOGMenuTreePath.
				 * SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGE_COLUMNS_TYPE);
				 * 
				 * test.log(LogStatus.INFO, "select the column: type "); if
				 * (!(input_ele.isSelected())) input_ele.click(); } else if
				 * (given_inputs[i].contains("retention")) {
				 * 
				 * test.log(LogStatus.INFO,
				 * "wait for columns checkboxes appear when click on settings button");
				 * waitUntilElementAppear( SPOGMenuTreePath.
				 * SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGE_COLUMNS_RETENTION);
				 * WebElement input_ele = ElementFactory.getElement( SPOGMenuTreePath.
				 * SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGE_COLUMNS_RETENTION);
				 * 
				 * test.log(LogStatus.INFO, "select the column: retention "); if
				 * (!(input_ele.isSelected())) input_ele.click();
				 * 
				 * } else if (given_inputs[i].contains("protected_data")) {
				 * 
				 * test.log(LogStatus.INFO,
				 * "wait for columns checkboxes appear when click on settings button");
				 * waitUntilElementAppear( SPOGMenuTreePath.
				 * SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGE_COLUMNS_PROTECTED_DATA);
				 * WebElement input_ele = ElementFactory.getElement( SPOGMenuTreePath.
				 * SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGE_COLUMNS_PROTECTED_DATA);
				 * 
				 * test.log(LogStatus.INFO, "select the column: protected_data "); if
				 * (!(input_ele.isSelected())) input_ele.click();
				 * 
				 * } else if (given_inputs[i].contains("source_count")) {
				 * 
				 * test.log(LogStatus.INFO,
				 * "wait for columns checkboxes appear when click on settings button");
				 * waitUntilElementAppear( SPOGMenuTreePath.
				 * SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGE_COLUMNS_SOURCE_COUNT);
				 * WebElement input_ele = ElementFactory.getElement( SPOGMenuTreePath.
				 * SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGE_COLUMNS_SOURCE_COUNT);
				 * 
				 * test.log(LogStatus.INFO, "select the column: source_count "); if
				 * (!(input_ele.isSelected())) input_ele.click();
				 * 
				 * } else if (given_inputs[i].contains("protection_policy")) {
				 * 
				 * test.log(LogStatus.INFO,
				 * "wait for columns checkboxes appear when click on settings button");
				 * waitUntilElementAppear( SPOGMenuTreePath.
				 * SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGE_COLUMNS_PROTECTION_POLICY);
				 * WebElement input_ele = ElementFactory.getElement( SPOGMenuTreePath.
				 * SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGE_COLUMNS_PROTECTION_POLICY);
				 * 
				 * test.log(LogStatus.INFO, "select the column: protection_policy "); if
				 * (!(input_ele.isSelected())) input_ele.click();
				 * 
				 * } else if (given_inputs[i].contains("latest_job")) {
				 * 
				 * test.log(LogStatus.INFO,
				 * "wait for columns checkboxes appear when click on settings button");
				 * waitUntilElementAppear( SPOGMenuTreePath.
				 * SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGE_COLUMNS_LATEST_JOB);
				 * WebElement input_ele = ElementFactory.getElement( SPOGMenuTreePath.
				 * SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGE_COLUMNS_LATEST_JOB);
				 * 
				 * test.log(LogStatus.INFO, "select the column: latest_job "); if
				 * (!(input_ele.isSelected())) input_ele.click();
				 * 
				 * } else if (given_inputs[i].contains("location")) {
				 * 
				 * test.log(LogStatus.INFO,
				 * "wait for columns checkboxes appear when click on settings button");
				 * waitUntilElementAppear( SPOGMenuTreePath.
				 * SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGE_COLUMNS_LOCATION);
				 * WebElement input_ele = ElementFactory .getElement(SPOGMenuTreePath.
				 * SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGE_COLUMNS_LOCATION);
				 * 
				 * test.log(LogStatus.INFO, "select the column: location "); if
				 * (!(input_ele.isSelected())) input_ele.click();
				 * 
				 * } else if (given_inputs[i].contains("datacenter_region")) {
				 * 
				 * test.log(LogStatus.INFO,
				 * "wait for columns checkboxes appear when click on settings button");
				 * waitUntilElementAppear( SPOGMenuTreePath.
				 * SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGE_COLUMNS_DATACENTER_REGION);
				 * WebElement input_ele = ElementFactory.getElement( SPOGMenuTreePath.
				 * SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGE_COLUMNS_DATACENTER_REGION);
				 * 
				 * test.log(LogStatus.INFO, "select the column: datacenter_region "); if
				 * (!(input_ele.isSelected())) input_ele.click();
				 * 
				 * } Result = "pass"; } waitForSeconds(3);
				 * 
				 * test.log(LogStatus.INFO, "click on settings button"); settings_ele.click();
				 * 
				 * return Result; }
				 */

	/**
	 * navigateManageSavedSearch - method used to navigate to manageSavedSearch in
	 * Destinations page and pop up the form
	 * 
	 * @author Ramya.Nagepalli
	 * @param test
	 */
	public void navigateManageSavedSearch(ExtentTest test) {

		test.log(LogStatus.INFO, "Click on settings button on Destinations page ");
		WebElement settings_ele = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_SETTINGSBTN);
		settings_ele.click();

		test.log(LogStatus.INFO, "wait for manage saved search link appears");
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_COMMON_MANAGESAVEDSEARCHES,
				BrowserFactory.getMaxTimeWaitUIElement());

		waitForSeconds(3);
		test.log(LogStatus.INFO, "Click on manage saved search link");
		WebElement manage_save_search_ele = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_MANAGESAVEDSEARCHES);
		manage_save_search_ele.click();

		waitUntilElementAppear(
				SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGESAVESEARCH_WINDOW_TITLE);
	}

	/**
	 * manageSaveSearch - method used to perform modify inputs and delete the saved
	 * searches
	 * 
	 * @author Ramya.Nagepalli
	 * @param savesearch
	 * @param mod_saveSearchName
	 * @param mod_searchString
	 * @param mod_protectionPolicy
	 * @param action
	 * @param test
	 * @return result
	 */
	public String manageSaveSearch(String savesearch, String mod_saveSearchName, String mod_searchString,
			String mod_protectionPolicy, String action, ExtentTest test) {

		String result = "fail";

		test.log(LogStatus.INFO, "wait for manage save search title appears on the form");
		waitUntilElementAppear(
				SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGESAVESEARCH_WINDOW_TITLE);

		test.log(LogStatus.INFO, "wait for default filter label appears on the form");
		waitUntilElementAppear(
				SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGESAVESEARCH_DEFAULT_FILTER_LABEL);

		test.log(LogStatus.INFO, "select a saved search from the list on form");

		// waitUntilElementAppear("//div[@class='col-3 left-section']//button[text()='"
		// + savesearch + "']");
		waitForSeconds(2);
		WebElement save_search_ele = ElementFactory
				.getElementByXpath("//div[@class='col-3 left-section']//button[text()='" + savesearch + "']");
		save_search_ele.click();

		if (action.equalsIgnoreCase("save")) {

			if (!(mod_saveSearchName.equals("") || mod_saveSearchName.equals(null))) {

				test.log(LogStatus.INFO, "modify the save search name with the given input : " + mod_saveSearchName);
				TextField save_search_input_ele = new TextField(
						SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGESAVESEARCH_SAVESEARCH_INPUT);

				waitForSeconds(2);
				save_search_input_ele.setText(mod_saveSearchName);

			}
			if (!(mod_searchString.equals("") || mod_searchString.equals(null))) {

				test.log(LogStatus.INFO, "modify the search string with the given input : " + mod_searchString);
				WebElement search_string_input_ele = ElementFactory.getElement(
						SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGESAVESEARCH_SEARCHSTRING_INPUT);

				search_string_input_ele.clear();
				search_string_input_ele.sendKeys(mod_searchString);
			}
			if (!(mod_protectionPolicy.equals("") || mod_protectionPolicy.equals(null))) {

				test.log(LogStatus.INFO, "modify the protection policy with the given input : " + mod_protectionPolicy);
				WebElement protectionPolicy_input_ele = ElementFactory.getElement(
						SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGESAVESEARCH_PROTECTIONPOLICY_INPUT);

				protectionPolicy_input_ele.clear();
				protectionPolicy_input_ele.sendKeys(mod_protectionPolicy);
				protectionPolicy_input_ele.sendKeys(Keys.TAB);
			}
			WebElement fltr_label_ele = ElementFactory.getElement(
					SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGESAVESEARCH_DEFAULT_FILTER_LABEL);

			test.log(LogStatus.INFO, "select the default filter label for the saved search");
			if (!(fltr_label_ele.isSelected()))
				fltr_label_ele.click();

			test.log(LogStatus.INFO, "click on save button on the form");
			WebElement Save_ele = ElementFactory.getElement(
					SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGESAVESEARCH_SAVE_BUTTON);
			Save_ele.click();

			test.log(LogStatus.INFO, "get the alert msg compare with the expected : " + updated_savedSearch_msg);
			waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_TOASTMESSAGE);
			WebElement toast_ele = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_TOASTMESSAGE);
			assertEquals(toast_ele.getText(), updated_savedSearch_msg);

			result = "pass";

		} else if (action.equalsIgnoreCase("delete")) {

			test.log(LogStatus.INFO, "click on delete button on the form");
			WebElement delete_ele = ElementFactory.getElement(
					SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGESAVESEARCH_DELETE_BUTTON);
			delete_ele.click();

			test.log(LogStatus.INFO, "get the alert msg compare with the expected : " + deleted_savedSearch_msg);
			waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_TOASTMESSAGE);
			WebElement toast_ele = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_TOASTMESSAGE);
			assertEquals(toast_ele.getText(), deleted_savedSearch_msg);

			result = "pass";

		} else {

			waitForSeconds(2);
			test.log(LogStatus.INFO, "click on tool tip info on the form");
			WebElement tooltip_ele = ElementFactory.getElement(
					SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGESAVESEARCH_TOOLTIP_LINK);
			tooltip_ele.click();

			waitForSeconds(2);
			WebElement tooltip_msg_ele = ElementFactory.getElement(
					SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGESAVESEARCH_TOOLTIP_INFO);

			String actual_msg = tooltip_msg_ele.getText();

			waitForSeconds(2);
			test.log(LogStatus.INFO, "get tool tip info on the form and compare with the expected");
			assertEquals(actual_msg, ManageSavedSearch_tooltip_info_msg);

			tooltip_ele.click();

			test.log(LogStatus.INFO, "click on cancel button on the form");
			WebElement cancel_ele = ElementFactory.getElement(
					SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGESAVESEARCH_CANCEL_BUTTON);
			cancel_ele.click();

			result = "pass";
		}

		return result;

	}

	/**
	 * checkActiveFilter - method used to check the created save search is active
	 * 
	 * @author Ramya.Nagepalli
	 * @param savedSearchName
	 * @param test
	 */

	public String checkActiveFilter(String SaveSearchName, ExtentTest test) {

		String result = "fail";

		test.log(LogStatus.INFO, "wait for destination title appears on the form");
		waitUntilElementAppear(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_TITLE);

		Button filter_ele = new Button(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_ACTIVE_FILTER);

		test.log(LogStatus.INFO, "Check for active filter is enabled");
		if (filter_ele.isEnabled()) {
			assertEquals(filter_ele.getText(), SaveSearchName);

			result = "pass";
		}

		return result;
	}

	/**
	 * ValidateSelectedColumns - method used to validate the selected inputs of
	 * Destination Columns
	 * 
	 * @author Ramya.Nagepalli
	 * @param exp_columns
	 * @param test
	 * @return
	 */
	public String ValidateSelectedColumns(String[] exp_columns, ExtentTest test) {
		String result = "fail";

		test.log(LogStatus.INFO, "get list of Destination table headers");
		List<WebElement> DestinationTableHeaders = getVolumeTableHeaders();
		HashMap<String, Integer> DestinationTableHeaderOrder = getTableHeaderOrder(DestinationTableHeaders);

		test.log(LogStatus.INFO, "get list of Destination table rows");
		List<WebElement> rows = getDestinationList();

		for (int i = 0; i < 1; i++) {

			// get UI item
			/* WebElement eachRow = rows.get(i); */
			/*
			 * String xpath = "." + ElementFactory .getElementAttrFromXML(SPOGMenuTreePath.
			 * SPOG_COMMON_TABLE_COLUMNS).get(0) .getValue();
			 */
			/* List<WebElement> columns = eachRow.findElements(By.xpath(xpath)); */
			Set<String> keySet = DestinationTableHeaderOrder.keySet();
			Iterator<String> keyIter = keySet.iterator();
			while (keyIter.hasNext()) {
				String headerName = keyIter.next();
				int order = DestinationTableHeaderOrder.get(headerName);
				if (headerName.equalsIgnoreCase("Name")) {

					test.log(LogStatus.INFO, "Column :name is displaying on UI");

				} else if (headerName.equalsIgnoreCase("Location")) {

					test.log(LogStatus.INFO, "Column : Location is displaying on UI");

					for (int j = 0; j < exp_columns.length; j++)
						if (exp_columns[j].contains("location"))
							result = "pass";

				} else if (headerName.equalsIgnoreCase("Data Center Region")) {

					test.log(LogStatus.INFO, "Column : datacenter_region is displaying on UI");

					for (int j = 0; j < exp_columns.length; j++)
						if (exp_columns[j].contains("datacenter_region"))
							result = "pass";

				} else if (headerName.equalsIgnoreCase("Storage Usage")) {
					test.log(LogStatus.INFO, "Column :Storage Usage is displaying on UI");

				} else if (headerName.equalsIgnoreCase("Latest Job")) {

					test.log(LogStatus.INFO, "Column : latest_job is displaying on UI");

					for (int j = 0; j < exp_columns.length; j++)
						if (exp_columns[j].contains("latest_job"))
							result = "pass";

				} else if (headerName.equalsIgnoreCase("Type")) {

					test.log(LogStatus.INFO, "Column : type is displaying on UI");

					for (int j = 0; j < exp_columns.length; j++)
						if (exp_columns[j].contains("type"))
							result = "pass";

				} else if (headerName.equalsIgnoreCase("Retention")) {

					test.log(LogStatus.INFO, "Column : retention is displaying on UI");

					for (int j = 0; j < exp_columns.length; j++)
						if (exp_columns[j].contains("retention"))
							result = "pass";

				} else if (headerName.equalsIgnoreCase("Source Count")) {

					test.log(LogStatus.INFO, "Column : source_count is displaying on UI");

					for (int j = 0; j < exp_columns.length; j++)
						if (exp_columns[j].contains("source_count"))
							result = "pass";

				} else if (headerName.equalsIgnoreCase("Protection Policy")) {

					test.log(LogStatus.INFO, "Column : protection_policy is displaying on UI");

					for (int j = 0; j < exp_columns.length; j++)
						if (exp_columns[j].contains("protection_policy"))
							result = "pass";
				} else {
					test.log(LogStatus.FAIL, "Column with headername:" + headerName + " does not exist");
					assertFalse(true, "Column with headername:" + headerName + " does not exist");
				}
			}
		}
		return result;
	}

	/**
	 * BulkActions - method used to perform bulk actions for the selected
	 * destinations
	 * 
	 * @author Ramya.Nagepalli
	 * @param action
	 * @param test
	 */

	public String BulkActions(String[] selected_inputs, String action, ExtentTest test) {

		String result = "fail";

		test.log(LogStatus.INFO, "get list of Destination table rows");
		List<WebElement> rows = getDestinationList();
		// action = "selected";

		test.log(LogStatus.INFO, "Select the Destinations to be deleted");
		if (action.equalsIgnoreCase("all")) {

			test.log(LogStatus.INFO, "select all destinations on page");
			WebElement select_all_ele = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_SELECT_ALL_CHECKBOXES);
			select_all_ele.click();

			test.log(LogStatus.INFO, "Click on Bulk Actions button on page");
			ClickBulkAction(test);

			test.log(LogStatus.INFO,
					"check the message 'No Actions Support' appeared on the UI after click on Actions Button");
			waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_ACTIONS_BUTTON_NO_ACTIONS_SUPPORT);
			result = "pass";

		} else if (action.equalsIgnoreCase("selected")) {

			test.log(LogStatus.INFO, "Select only given destination inputs");

			waitForSeconds(3);

			if (selected_inputs.length > 0) {

				WebElement eachRow = null;
				for (int j = 0; j < rows.size(); j++) {
					eachRow = rows.get(j);
					try {
						eachRow.findElement(By.xpath(".//a[text()='" + selected_inputs[j] + "']"));
						// eachRow = rows.get(i);
						break;
					} catch (Exception e) {
						continue;
					}

				}

				String bulk_action_xpath = ElementFactory
						.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_BULK_ACTION_CHECKBOX).get(0).getValue();
				WebElement bulk_action_ele = eachRow.findElement(By.xpath(bulk_action_xpath));

				if (!(bulk_action_ele.isSelected()))
					bulk_action_ele.click();

			}
			test.log(LogStatus.INFO, "Click on Bulk Actions button on page");
			ClickBulkAction(test);

			test.log(LogStatus.INFO, "check the action 'Delete' appeared on the UI and able to clickable");
			waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_COMMON_ACTIONS_BUTTON_NO_ACTIONS_SUPPORT,
					BrowserFactory.getMaxTimeWaitUIElement());
			result = "pass";

		} else {

			test.log(LogStatus.INFO, "Click on Bulk Actions button on page without selecting any destinations");
			result = ClickBulkAction(test);

			if (result.equals("fail"))
				result = "pass";
		}

		return result;
	}

	/**
	 * ClickBulkAction - Click on bulk actions on Destination page
	 * 
	 * @author Ramya.Nagepalli
	 * @param test
	 */
	public String ClickBulkAction(ExtentTest test) {

		String result = "fail";

		test.log(LogStatus.INFO, "wait until Action button is able to clickable on UI");
		waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_ACTIONS_BUTTON);

		test.log(LogStatus.INFO, "Click on Actions Button after selecting destinations");
		Button Actions_button_ele = new Button(SPOGMenuTreePath.SPOG_COMMON_ACTIONS_BUTTON);

		if (Actions_button_ele.isEnabled()) {
			Actions_button_ele.click();
			result = "pass";
		} else {
			result = "fail";
		}

		return result;
	}

	/**
	 * clickContextualAction - Click on Contextual actions on Destination page
	 * 
	 * @author Ramya.Nagepalli
	 * @param test
	 */
	public String clickContextualAction(ExtentTest test) {
		String result = "fail";

		test.log(LogStatus.INFO, "get list of Destination table rows");
		List<WebElement> rows = getDestinationList();

		WebElement eachRow = null;
		for (int j = 0; j < rows.size(); j++) {
			eachRow = rows.get(j);
			waitForSeconds(3);

			test.log(LogStatus.INFO, "click on the contextual actions button");
			String bulk_action_xpath = ElementFactory
					.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_CONTEXTUALACTIONS).get(0).getValue();
			WebElement bulk_action_ele = eachRow.findElement(By.xpath(bulk_action_xpath));

			waitForSeconds(2);

			if ((bulk_action_ele.isEnabled()))
				bulk_action_ele.click();

		}

		return result;
	}

	public boolean checkElementExists(String xpath) {
		if (xpath != null) {
			WebElement ele = ElementFactory.getElement(xpath);
			if (ele != null) {
				return true;
			} else {
				return false;
			}
		} else {
			errorHandle.printWarnMessageInDebugFile("Given xpath is invalid");
		}
		return false;
	}

	/**
	 * SearchforVolume - method used to search for specific volume name
	 * 
	 * @author Ramya.Nagepalli
	 * @param volume_name
	 * @param test
	 * @return
	 */
	public String SearchforVolume(String volume_name, ExtentTest test) {
		String result = "fail";

		if (!(volume_name.equals("") || volume_name.equals(null))) {

			test.log(LogStatus.INFO, "get the Element of search string ");
			WebElement search_ele = ElementFactory.getElement(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_SEARCH_INPUT);
			search_ele.clear();
			waitForSeconds(2);
			search_ele.sendKeys(volume_name);

			test.log(LogStatus.INFO, "click search string button");
			WebElement submit_ele = ElementFactory
					.getElement(SPOGMenuTreePath.SPOGPROTECT_DESTINATIONS_SEARCH_STRING_SUBMIT_BUTTON);
			submit_ele.sendKeys(Keys.ENTER);

			this.waitForSeconds(4);
			waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_SEARCHRESULTFOR_MSG);

			result = "pass";
		}

		return result;
	}

	/**
	 * checkDefaultPagination for destinations page
	 * 
	 * @author Ramya.Nagepalli
	 * @param rows_count
	 * @param test
	 * @return
	 */
	public String checkDefaultPagination_destination(int actual, int rows_count, ExtentTest test) {
		// TODO Auto-generated method stub
		return checkPagination(actual, rows_count, test);
	}

	/**
	 * modifyPagination
	 * 
	 * @author Ramya.Nagepalli
	 * @param input
	 * @param actual_count
	 * @param test
	 * @return
	 */
	public int modifyPagination(int input, int actual_count, ExtentTest test) {
		// TODO Auto-generated method stub
		return modifyPagination(input, test);
	}

	/**
	 * clickManageColumns
	 * 
	 * @author Ramya.Nagepalli
	 * @param test
	 */
	public void clickManageColumns(ExtentTest test) {

		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_COMMON_SETTINGSBTN, BrowserFactory.getMaxTimeWaitUIElement());
		Button btn = new Button(SPOGMenuTreePath.SPOG_COMMON_SETTINGSBTN);
		btn.click();

		waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_MANAGESAVEDSEARCHES);
	}

	/**
	 * chooseColumn
	 * 
	 * @author Ramya.Nagepalli
	 * @param columns
	 * @param test
	 */
	public void chooseColumn(String columns, ExtentTest test) {

		List<WebElement> columnLabels = ElementFactory.getElements(SPOGMenuTreePath.SPOG_COMMON_COLUMNLABELS);
		String xpath = ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_COLUMNOPTIONSDIV).get(0)
				.getValue();

		for (int i = 0; i < columnLabels.size(); i++) {

			try {

				WebElement columnEle = ElementFactory.getElementByXpath(
						xpath + "//label[@for='gridSettings" + i + "']//span[text()='" + columns + "']");

				WebElement checkBoxEle = ElementFactory
						.getElementByXpath(xpath + "//input[@id='gridSettings" + i + "']");

				if (!checkBoxEle.isSelected())
					columnEle.click();

				break;

			} catch (NoSuchElementException e) {
				if (i == columnLabels.size() - 1) {
					e.printStackTrace();
					assertFalse(true, "column with name:" + columns + " not found");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * checkEnabledColumns
	 * 
	 * @author Ramya.Nagepalli
	 * @param expectedColumns
	 * @param test
	 */
	public void checkEnabledColumns(ArrayList<String> expectedColumns, ExtentTest test) {

		HashMap<String, Integer> actualColumns = getTableHeaderOrder(getTableHeaders());

		for (int i = 0; i < expectedColumns.size(); i++) {

			if (actualColumns.containsKey(expectedColumns.get(i))) {
				test.log(LogStatus.INFO, "column with name:" + expectedColumns.get(i) + " availble in table");

			} else {
				if (i == expectedColumns.size() - 1) {
					test.log(LogStatus.FAIL, "Column with name:" + expectedColumns.get(i) + " not found in table");
				}
			}
		}
	}

	/**
	 * Perform_Bulk_Action
	 * 
	 * @author Ramya.Nagepalli
	 * @param elements
	 * @param action
	 * @param test
	 * @return
	 */
	public String performBulkActions(ArrayList<String> elements, String action, ExtentTest test) {

		if (elements.get(0).equalsIgnoreCase("all"))
			return performBulkActionForAllElements(action, test);
		else
			return performBulkActionForSpecifiedElements(elements, action, test);
	}

	/**
	 * ContextualActions - method used to validate the contextual actions
	 * 
	 * @author Ramya.Nagepalli
	 * @param test
	 * @param elementName
	 * @param actions
	 * @return
	 */
	public String checkContextualActions(String elementName, ArrayList<String> actions, ExtentTest test) {

		return checkContextualActionsForSpecifiedElement(elementName, actions);

	}

	/**
	 * navigateToSpecifiedPage
	 * 
	 * @author Ramya.Nagepalli
	 */
	public String navigateToSpecifiedPage(int page, int page_size, ExtentTest test) {
		return navigateToSpecifiedPage(page, page_size, test);
	}
}
