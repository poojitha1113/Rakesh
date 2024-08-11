package ui.spog.server;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import io.restassured.response.Response;
import ui.base.common.BasePage;
import ui.base.common.SPOGMenuTreePath;
import ui.base.common.Url;
import ui.base.elements.Link;
import ui.base.factory.BrowserFactory;
import ui.base.factory.ElementFactory;
import ui.spog.pages.protect.DestinationsPage;
import ui.spog.pages.protect.SourcePage;
import ui.spog.pages.protect.DestinationsPage;

public class DestinationHelper extends SPOGUIServer {

	BasePage basepage = new BasePage();
	DestinationsPage DestinationsPage;

	public DestinationHelper(String browserType, int maxTimeWaitSec) {
		super(browserType, maxTimeWaitSec);
		DestinationsPage = new DestinationsPage();
	}

	/**
	 * AddCloudVolume - scenario to add cloud direct volume for the enroll
	 * organizations with given inputs
	 * 
	 * @author Ramya.Nagepalli
	 * @param volume_name
	 * @param datacenter
	 * @param retention
	 * @param Comment
	 * @param exp_msg
	 * @param test
	 */
	public void AddCloudVolume(String volume_name, String datacenter, String retention, String Comment, String exp_msg,
			ExtentTest test) {

		test.log(LogStatus.INFO, "navigate to destinations page from Home");
		if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS, 3)) {

		} else if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_CLOUD_DIRECT_VOLUMES,
				3)) {
			goToCloudDirectDestinationsPage();
		} else if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_CLOUD_HYBRID_VOLUMES,
				3)) {
			DestinationsPage.goToCloudHybridDestinationsPage();
		} else {

			goToDestinationsPage();

			basepage.waitForSeconds(5);
		}

		test.log(LogStatus.INFO, "Click Add Cloud Volume Button");
		DestinationsPage.ClickAddVolumeBtn(test);

		test.log(LogStatus.INFO, "Perform action to Add Cloud Volume providing given inputs");
		String result = DestinationsPage.AddCloudVolume(volume_name, datacenter, retention, Comment, exp_msg, test);

		test.log(LogStatus.INFO, "Check the Result");
		assertEquals(result, "pass");

	}

	/**
	 * CreateSaveSearch - Method is used to save search with given input filters
	 * 
	 * @author Ramya.Nagepalli
	 * @param search_name
	 * @param policy_search
	 * @param test
	 */
	public void CreateSaveSearch(String search_name, String policy_search, String saveSearchName, ExtentTest test) {

		test.log(LogStatus.INFO, "navigate to destinations page from Home");
		if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS, 3)) {

		} else if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_CLOUD_DIRECT_VOLUMES,
				3)) {
			DestinationsPage.goToCloudDirectDestinationsPage();
		} else if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_CLOUD_HYBRID_VOLUMES,
				3)) {
			DestinationsPage.goToCloudHybridDestinationsPage();
		} else {

			basepage.waitForSeconds(5);
		}

		test.log(LogStatus.INFO, "Create Saved Search for the Destinations Page with the given inputs");
		String result = DestinationsPage.CreateSaveSearch(search_name, policy_search, saveSearchName, test);

		test.log(LogStatus.INFO, "Check the Result");
		assertEquals(result, "pass");
	}

	/**
	 * verifyAddedCloudVolume - method used to validate the created cloud volume
	 * 
	 * @author Ramya.Nagepalli
	 * @param response
	 * @param exp_vol
	 * @param exp_datacenter
	 * @param test
	 */
	public void verifyAddedCloudVolume(Response response, String exp_vol, String exp_datacenter, ExtentTest test) {

		test.log(LogStatus.INFO, "Check the created cloud volume details from UI with API response");
		String result = DestinationsPage.verifyAddedCloudVolume(response, exp_vol, exp_datacenter, test);

		test.log(LogStatus.INFO, "Check the Result");
		assertEquals(result, "pass");
	}

	/**
	 * getCreatedSaveSearch - get the created save search applies the search and
	 * validates the output
	 * 
	 * @author Ramya.Nagepalli
	 * @param saveSearchName
	 * @param response
	 * @param exp_volumeName
	 * @param exp_datacenter
	 * @param searchString
	 * @param test
	 */
	public void getCreatedSaveSearch(String saveSearchName, Response response, String exp_volumeName,
			String exp_datacenter, String searchString, ExtentTest test) {

		test.log(LogStatus.INFO, "navigate to destinations page from Home");
		if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS, 3)) {

		} else if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_CLOUD_DIRECT_VOLUMES,
				3)) {
			DestinationsPage.goToCloudDirectDestinationsPage();
		} else if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_CLOUD_HYBRID_VOLUMES,
				3)) {
			DestinationsPage.goToCloudHybridDestinationsPage();
		} else {

			basepage.waitForSeconds(5);
		}

		test.log(LogStatus.INFO, "get the created save search and applies the search");
		DestinationsPage.getSavedSearch(saveSearchName, test);

		test.log(LogStatus.INFO, "Check the created cloud volume details from UI with API response");
		String result = DestinationsPage.validateCreatedSaveSearch(response, exp_volumeName, exp_datacenter,
				searchString, test);

		test.log(LogStatus.INFO, "Check the Result");
		assertEquals(result, "pass");

	}

	/**
	 * manageSaveSearch - method used to navigate to settings-> manage save search,
	 * perform modify inputs and delete the saved searches
	 * 
	 * @author Ramya.Nagepalli
	 * @param savesearch
	 * @param mod_saveSearchName
	 * @param mod_searchString
	 * @param mod_protectionPolicy
	 * @param action
	 * @param test
	 */
	public void manageSaveSearch(String savesearch, String mod_saveSearchName, String mod_searchString,
			String mod_protectionPolicy, String action, ExtentTest test) {

		test.log(LogStatus.INFO, "navigate to destinations page from Home");
		if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS, 3)) {

		} else if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_CLOUD_DIRECT_VOLUMES,
				3)) {
			DestinationsPage.goToCloudDirectDestinationsPage();
		} else if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_CLOUD_HYBRID_VOLUMES,
				3)) {
			DestinationsPage.goToCloudHybridDestinationsPage();
		} else {

			basepage.waitForSeconds(5);
		}

		basepage.waitForSeconds(2);

		test.log(LogStatus.INFO, "navigate to Manage Saved search link in Destinations page");
		DestinationsPage.navigateManageSavedSearch(test);

		test.log(LogStatus.INFO, "modify the Manage saved searches with given inputs");
		String result = DestinationsPage.manageSaveSearch(savesearch, mod_saveSearchName, mod_searchString,
				mod_protectionPolicy, action, test);

		test.log(LogStatus.INFO, "Check the Result");
		assertEquals(result, "pass");
	}

	/**
	 * checkActiveFilter - method used to check the filter is active
	 * 
	 * @author Ramya.Nagepalli
	 * @param SaveSearchName
	 * @param test
	 */
	public void checkActiveFilter(String SaveSearchName, ExtentTest test) {

		test.log(LogStatus.INFO, "Check whether the applied search is active");
		String result = DestinationsPage.checkActiveFilter(SaveSearchName, test);

		test.log(LogStatus.INFO, "Check the Result");
		assertEquals(result, "pass");
	}

	/**
	 * 
	 * @param searchName
	 * @param enable
	 * @param test
	 */
	public void selectSavedSearch(String searchName, boolean enable, ExtentTest test) {
		DestinationsPage.selectSavedSearch(searchName, false, test);
	}
	/*
		*//**
			 * manageDestinationColumns- method used to modify the destination columns with
			 * selected inputs
			 * 
			 * @author Ramya.Nagepalli
			 * @param inputs
			 * @param test
			 *//*
				 * public void manageDestinationColumns(String[] inputs, ExtentTest test) {
				 * 
				 * test.log(LogStatus.INFO, "navigate to destinations page from Home");
				 * goToDestinationsPage();
				 * 
				 * basepage.waitForSeconds(3);
				 * 
				 * test.log(LogStatus.INFO, "Check whether the applied search is active");
				 * String result = DestinationsPage.manageDestinationColumns(inputs, test);
				 * 
				 * test.log(LogStatus.INFO, "Check the Result"); assertEquals(result, "pass"); }
				 */

	/**
	 * ValidateSelectedColumns - method used to validate the selected destination
	 * columns
	 * 
	 * @param Ramya.Nagepalli
	 * @param exp_columns
	 * @param test
	 */
	public void ValidateSelectedColumns(String[] exp_columns, ExtentTest test) {

		test.log(LogStatus.INFO, "Check whether the applied search is active");
		String result = DestinationsPage.ValidateSelectedColumns(exp_columns, test);

		test.log(LogStatus.INFO, "Check the Result");
		assertEquals(result, "pass");
	}

	/**
	 * searchForVolume- method used to search for specific volume name
	 * 
	 * @param Ramya.Nagepalli
	 * @param Volume_name
	 * @param test
	 */
	public void searchForVolume(String volume_name, ExtentTest test) {

		test.log(LogStatus.INFO, "navigate to destinations page from Home");
		if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS, 3)) {

		} else if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_CLOUD_DIRECT_VOLUMES,
				3)) {
			DestinationsPage.goToCloudDirectDestinationsPage();
		} else if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_CLOUD_HYBRID_VOLUMES,
				3)) {
			DestinationsPage.goToCloudHybridDestinationsPage();
		} else {
			goToDestinationsPage();

			basepage.waitForSeconds(5);
		}

		String result = DestinationsPage.SearchforVolume(volume_name, test);

		test.log(LogStatus.INFO, "Check the Result");
		assertEquals(result, "pass");

	}

	/**
	 * checkDefaultPagination
	 * 
	 * @author Ramya.Nagepalli
	 * @param actual_count
	 * @param rows_count
	 * @param test
	 */
	public void checkDefaultPagination(int actual_count, int rows_count, ExtentTest test) {
		test.log(LogStatus.INFO, "navigate to destinations page from Home");
		if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS, 3)) {

		} else if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_CLOUD_DIRECT_VOLUMES,
				3)) {
			DestinationsPage.goToCloudDirectDestinationsPage();
		} else if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_CLOUD_HYBRID_VOLUMES,
				3)) {
			DestinationsPage.goToCloudHybridDestinationsPage();
		} else {
			goToDestinationsPage();
			basepage.waitForSeconds(5);
		}

		test.log(LogStatus.INFO, "check default pagination for the destinations page");
		String result = DestinationsPage.checkDefaultPagination_destination(actual_count, rows_count, test);

		test.log(LogStatus.INFO, "Check the Result");
		assertEquals(result, "pass");

	}

	/**
	 * modifyPagination
	 * 
	 * @author Ramya.Nagepalli
	 * @param actual_count
	 * @param test
	 */
	public int modifyPagination(int input, int actual_count, ExtentTest test) {
		test.log(LogStatus.INFO, "navigate to destinations page from Home");

		if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS, 3)) {

		} else if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_CLOUD_DIRECT_VOLUMES,
				3)) {
			DestinationsPage.goToCloudDirectDestinationsPage();
		} else if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_CLOUD_HYBRID_VOLUMES,
				3)) {
			DestinationsPage.goToCloudHybridDestinationsPage();
		} else {
			goToDestinationsPage();
			basepage.waitForSeconds(5);
		}

		test.log(LogStatus.INFO, "modify and validate pagination for the destinations page");
		int result = DestinationsPage.modifyPagination(input, test);

		return result;

	}

	/**
	 * goToCloudDirectDestinationsPage - navigate to cloud direct destinations page
	 * 
	 * @author Ramya.Nagepalli
	 * @return
	 */
	public void goToCloudDirectDestinationsPage() {

		goToDestinationsPage();
		basepage.waitForSeconds(5);
		Link destinationsCDLink = new Link(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_CLOUD_DIRECT_VOLUMES);
		destinationsCDLink.click();
		ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_CLOUD_DIRECT_VOLUMES, 3);

		WebElement expected_path_ele = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_BREAD_CRUMB);
		expected_path_ele.findElement(By.xpath(".//span[contains(text(), 'Cloud Direct Volumes')]"));
		basepage.waitForSeconds(5);
	}

	/**
	 * goToCloudHybridDestinationsPage - navigate to cloud Hybrid destinations page
	 * 
	 * @author Ramya.Nagepalli
	 * @return
	 */
	public void goToCloudHybridDestinationsPage() {

		goToDestinationsPage();
		basepage.waitForSeconds(2);
		Link destinationsCDLink = new Link(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_CLOUD_HYBRID_VOLUMES);
		destinationsCDLink.click();

		WebElement expected_path_ele = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_BREAD_CRUMB);
		expected_path_ele.findElement(By.xpath(".//span[contains(text(), 'Cloud Hybrid Stores')]"));
		basepage.waitForSeconds(3);
	}

	/**
	 * enableColumnsWithCheck
	 * 
	 * @author Ramya.Nagepalli
	 * @param columns
	 * @param test
	 */
	public void enableColumnsWithCheck(ArrayList<String> columns, ExtentTest test) {

		if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS, 3)) {

		} else if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_CLOUD_DIRECT_VOLUMES,
				3)) {
			DestinationsPage.goToCloudDirectDestinationsPage();
		} else if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_CLOUD_HYBRID_VOLUMES,
				3)) {
			DestinationsPage.goToCloudHybridDestinationsPage();
		} else {
			goToDestinationsPage();
			basepage.waitForSeconds(5);
		}

		DestinationsPage.clickManageColumns(test);

		for (int i = 0; i < columns.size(); i++) {
			DestinationsPage.chooseColumn(columns.get(i), test);
		}

		DestinationsPage.checkEnabledColumns(columns, test);
	}

	/**
	 * Perform a search with given input and save the search Check for the saved
	 * search name in the saved searches: area
	 * 
	 * @author Ramya.Nagepalli
	 * @param searchName
	 * @param search_string
	 * @param filters
	 * @param test
	 */
	public void saveSearchWithCheck(String searchName, String search_string, ArrayList<String> filters,
			ExtentTest test) {

		if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS, 3)) {

		} else if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_CLOUD_DIRECT_VOLUMES,
				3)) {
			DestinationsPage.goToCloudDirectDestinationsPage();
		} else if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_CLOUD_HYBRID_VOLUMES,
				3)) {
			DestinationsPage.goToCloudHybridDestinationsPage();
		} else {
			goToDestinationsPage();
			basepage.waitForSeconds(3);
		}

		FilterCreationCheck(search_string, filters, test);

		DestinationsPage.clickSaveSearchAndSave(searchName);

		DestinationsPage.checkSavedSearch(searchName, true);

	}

	/**
	 * FilterCreationCheck
	 * 
	 * @author Ramya.Nagepalli
	 * @param search_string
	 * @param filters
	 * @param test
	 */
	public void FilterCreationCheck(String search_string, ArrayList<String> filters, ExtentTest test) {

		if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS, 3)) {

		} else if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_CLOUD_DIRECT_VOLUMES,
				3)) {
			DestinationsPage.goToCloudDirectDestinationsPage();
		} else if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_CLOUD_HYBRID_VOLUMES,
				3)) {
			DestinationsPage.goToCloudHybridDestinationsPage();
		} else {
			goToDestinationsPage();

			basepage.waitForSeconds(5);
		}

		test.log(LogStatus.INFO, "apply the filters: search_string=" + search_string + " filters=" + filters);
		DestinationsPage.searchByFilterTypeAndName(search_string, filters, test);

		test.log(LogStatus.INFO, "Check for the applied filters in search results for area");
		DestinationsPage.checkSelectedFilters(search_string, filters, test);

	}

	/**
	 * manageSaveSearchWithCheck
	 * 
	 * @author Ramya.Nagepalli
	 * @param searchName
	 * @param newName
	 * @param search_string
	 * @param makeDefault
	 * @param filters
	 * @param test
	 */
	public void manageSaveSearchWithCheck(String searchName, String newName, String search_string, boolean makeDefault,
			ArrayList<String> filters, ExtentTest test) {

		if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS, 3)) {

		} else if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_CLOUD_DIRECT_VOLUMES,
				3)) {
			DestinationsPage.goToCloudDirectDestinationsPage();
		} else if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_CLOUD_HYBRID_VOLUMES,
				3)) {
			DestinationsPage.goToCloudHybridDestinationsPage();
		} else {
			goToDestinationsPage();

			basepage.waitForSeconds(5);
		}

		DestinationsPage.selectSavedSearch(searchName, false, test);
		DestinationsPage.clickManageSavedSearch();
		DestinationsPage.modifySavedSearch(searchName, newName, search_string, filters, test);

		DestinationsPage.clickManageSavedSearch();
		if (newName != null && !newName.isEmpty()) {
			DestinationsPage.checkSavedSearchDetails(newName, search_string, filters, test);
		} else {
			DestinationsPage.checkSavedSearchDetails(searchName, search_string, filters, test);
		}

	}

	/**
	 * makeSaveSearchDefaultWithCheck
	 * 
	 * @author Ramya.Nagepalli
	 * @param searchName
	 * @param makeDefault
	 * @param test
	 */
	public void makeSaveSearchDefaultWithCheck(String searchName, boolean makeDefault, ExtentTest test) {

		if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS, 3)) {

		} else if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_CLOUD_DIRECT_VOLUMES,
				3)) {
			DestinationsPage.goToCloudDirectDestinationsPage();
		} else if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_CLOUD_HYBRID_VOLUMES,
				3)) {
			DestinationsPage.goToCloudHybridDestinationsPage();
		} else {
			goToDestinationsPage();

			basepage.waitForSeconds(5);
		}
		test.log(LogStatus.INFO, "make the save search inactive to manage save search");
		DestinationsPage.selectSavedSearch(searchName, false, test);
		DestinationsPage.clickManageSavedSearch();
		DestinationsPage.makeSaveSearchDefaultAndSave(searchName);
		DestinationsPage.waitForSeconds(3);
		DestinationsPage.checkSavedSearch(searchName, makeDefault);

		/*// re-navigate and search should be applied
		goToDestinationsPage();

		if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_COMMON_SAVEDSEARCHESDIV, 10))
			DestinationsPage.checkSavedSearch(searchName, makeDefault);*/
	}

	public void deleteSaveSearchWithCheck(String searchName, ExtentTest test) {

		if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS, 3)) {

		} else if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_CLOUD_DIRECT_VOLUMES,
				3)) {
			DestinationsPage.goToCloudDirectDestinationsPage();
		} else if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_CLOUD_HYBRID_VOLUMES,
				3)) {
			DestinationsPage.goToCloudHybridDestinationsPage();
		} else {
			goToDestinationsPage();

			basepage.waitForSeconds(5);
		}

		DestinationsPage.deleteSavedSearch(searchName, test);

	}

	/**
	 * Search for Destinations with given filters input Validate the search result
	 * available in table
	 * 
	 * @author Ramya.Nagepalli
	 * @param search_string
	 * @param filters
	 * @param expectedSourceNames
	 * @param expectedInfo
	 * @param test
	 */
	public void searchDestinationWithCheck(String search_string, ArrayList<String> filters,
			ArrayList<String> expectedSourceNames, ArrayList<HashMap<String, Object>> expectedInfo, ExtentTest test) {

		if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS, 3)) {

		} else if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_CLOUD_DIRECT_VOLUMES,
				3)) {
			DestinationsPage.goToCloudDirectDestinationsPage();
		} else if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_CLOUD_HYBRID_VOLUMES,
				3)) {
			DestinationsPage.goToCloudHybridDestinationsPage();
		} else {
			goToDestinationsPage();

			basepage.waitForSeconds(5);
		}

		FilterCreationCheck(search_string, filters, test);

		test.log(LogStatus.INFO, "Check source details in the table after search");

		for (int i = 0; i < expectedSourceNames.size(); i++) {
			// sourcePage.checkSourceDetailsInTable(expectedSourceNames.get(i),
			// expectedInfo.get(i), test);
		}
	}

	/**
	 * clear the search items without saving
	 * 
	 * @author Ramya.Nagepalli
	 */
	public void clearSearchWithoutSaving() {

		DestinationsPage.clickClearAllFilters();

	}

	/**
	 * BulkActions - method used to perform bulk actions for the selected
	 * destinations
	 * 
	 * @param Ramya.Nagepalli
	 * @param inputs
	 * @param action
	 * @param test
	 */
	public void performBulkActions(ArrayList<String> inputs, String action, ExtentTest test) {
		test.log(LogStatus.INFO, "navigate to destinations page from Home");
		if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS, 3)) {

		} else if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_CLOUD_DIRECT_VOLUMES,
				3)) {
			DestinationsPage.goToCloudDirectDestinationsPage();
		} else if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_CLOUD_HYBRID_VOLUMES,
				3)) {
			DestinationsPage.goToCloudHybridDestinationsPage();
		} else {
			goToDestinationsPage();
			basepage.waitForSeconds(5);
		}

		test.log(LogStatus.INFO, "Perform bulk actions for the given input destinations");
		String result = DestinationsPage.performBulkActions(inputs, action, test);

		test.log(LogStatus.INFO, "Check the Result");
		assertEquals(result, "pass");
	}

	/**
	 * ContextualActions- method used to validate the contextual actions
	 * 
	 * @param Ramya.Nagepalli
	 * @param test
	 */
	public void checkContextualActions(String elementName, ArrayList<String> actions, ExtentTest test) {

		test.log(LogStatus.INFO, "navigate to destinations page from Home");
		if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS, 3)) {

		} else if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_CLOUD_DIRECT_VOLUMES,
				3)) {
			DestinationsPage.goToCloudDirectDestinationsPage();
		} else if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_CLOUD_HYBRID_VOLUMES,
				3)) {
			DestinationsPage.goToCloudHybridDestinationsPage();
		} else {
			goToDestinationsPage();
			basepage.waitForSeconds(5);
		}

		test.log(LogStatus.INFO, "Perform bulk actions for the given input destinations");
		String result = DestinationsPage.checkContextualActions(elementName, actions, test);

		test.log(LogStatus.INFO, "Check the Result");
		assertEquals(result, "pass");
	}

	/**
	 * navigateToSpecifiedPage
	 * 
	 * @author Ramya.Nagepalli
	 * @param page
	 * @param page_size
	 * @param test
	 * @return result
	 */
	public String navigateToSpecifiedPage(int page, int page_size, ExtentTest test) {
		test.log(LogStatus.INFO, "navigate to destinations page from Home");

		if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS, 3)) {

		} else if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_CLOUD_DIRECT_VOLUMES,
				3)) {
			DestinationsPage.goToCloudDirectDestinationsPage();
		} else if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_PROTECT_DESTINATIONS_CLOUD_HYBRID_VOLUMES,
				3)) {
			DestinationsPage.goToCloudHybridDestinationsPage();
		} else {
			goToDestinationsPage();
			basepage.waitForSeconds(5);
		}

		test.log(LogStatus.INFO, "modify and validate pagination for the destinations page");
		String result = DestinationsPage.navigateToSpecifiedPage(page, page_size, test);

		return result;

	}
	
	public String getLastJobTimeFromLatestJobColume(int index,ExtentTest test) {
    	String ret=null;
    	ret=DestinationsPage.getLastJobTimeFromLatestJobColume(index);
    	return ret;
    }
	
	public String getLastJobTimeFromLatestJobColume(ExtentTest test) {
    	String ret=null;
    	ret=DestinationsPage.getLastJobTimeFromLatestJobColume();
    	return ret;
    }
	
	public void checkJobInformationFromLatestJobColume(String lastTime,String jobType,String jobStatus) {
		DestinationsPage.checkJobInformationFromLatestJobColume(lastTime,jobType,jobStatus);
    }
	
	public void checkJobInformationFromLatestJobColume(int index,String lastTime,String jobType,String jobStatus) {
		DestinationsPage.checkJobInformationFromLatestJobColume(index,lastTime,jobType,jobStatus);
    }

}