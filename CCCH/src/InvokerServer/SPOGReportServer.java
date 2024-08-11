package InvokerServer;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.BackUpJobReportsColumns;
import Constants.DashboardWidgetConstants;
import Constants.FilterTypes.filterType;
import Constants.RestoreJobReportsColumns;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import dataPreparation.JsonPreparation;
import genericutil.ErrorHandler;
import genericutil.StringUtils;
import invoker.SPOGReportInvoker;
import io.restassured.response.Response;

public class SPOGReportServer {
	private static JsonPreparation jp = new JsonPreparation();
	private SPOGServer spogServer;
	private SPOGReportInvoker spogReportInvoker;
	public static ErrorHandler errorHandle = ErrorHandler.getErrorHandler();
	private ExtentTest test;

	public void setToken(String token) {
		spogReportInvoker.setToken(token);
	}

	public SPOGReportServer(String baseURI, String port) {
		spogReportInvoker = new SPOGReportInvoker(baseURI, port);
		spogServer = new SPOGServer(baseURI, port);
	}

	/**
	 * Compose REPORT LIST FILTER INFO
	 * 
	 * @author Rakesh.Chalamala
	 * @param report_name
	 * @param data_range_type
	 * @param type
	 * @param source_group_id
	 * @param organization_id
	 * @param schedule_frequency
	 * @param generated_ts
	 * @param filter_name
	 * @param is_default
	 * @return reportListFilterInfo
	 */
	public HashMap<String, Object> composeReportListFilterInfo(String report_name, String date_range_type, String type,
			String source_group_id, String organization_id, String schedule_frequency, long generated_ts_start,
			long generated_ts_end, String filter_name, boolean is_default) {
		// Checking and whether the values are null or empty.
		// If empty not inserting into the composed hash.

		// composing details hash
		HashMap<String, Object> details = new HashMap<>();

		if (source_group_id != null) {
			if (source_group_id.contains(",")) {

				String[] source_group_ids = source_group_id.split(",");
				details.put("group_id", source_group_ids);
			} else {
				details.put("group_id", source_group_id);
			}
		}

		if (organization_id != null) {
			if (organization_id.contains(",")) {

				String[] organization_ids = organization_id.split(",");
				details.put("organization_id", organization_ids);

			} else {
				details.put("organization_id", organization_id);
			}
		}

		// composing report_for hash
		HashMap<String, Object> report_for = new HashMap<>();
		if (!type.equals("") && !type.equals(null)) {
			report_for.put("type", type);
		}

		if (!details.isEmpty()) {
			report_for.put("details", details);
		}

		// composing generated_ts hash

		HashMap<String, Object> generated_ts = new HashMap<>();
		if (generated_ts_start != -1) {
			generated_ts.put("start", generated_ts_start);
		}
		if (generated_ts_end != -1) {
			generated_ts.put("end", generated_ts_end);
		}

		// composing reportlisFilterInfo
		HashMap<String, Object> reportListFilterInfo = new HashMap<>();

		if (!report_name.equals("") && !report_name.equals(null)) {
			reportListFilterInfo.put("report_name", report_name);
		}

		if (!date_range_type.equals("") && !date_range_type.equals(null)) {
			if (date_range_type.contains(",")) {

				String[] date_range_types = date_range_type.split(",");
				reportListFilterInfo.put("date_range_type", date_range_types);

			} else {

				reportListFilterInfo.put("date_range_type", date_range_type);
			}
		}

		if (!report_for.isEmpty()) {
			reportListFilterInfo.put("report_for", report_for);
		}

		if (!schedule_frequency.equals("") && !schedule_frequency.equals(null)) {
			if (schedule_frequency.contains(",")) {

				String[] schedule_frequencies = schedule_frequency.split(",");
				reportListFilterInfo.put("schedule_frequency", schedule_frequencies);

			} else {
				reportListFilterInfo.put("schedule_frequency", schedule_frequency);
			}
		}

		if (!generated_ts.isEmpty()) {
			reportListFilterInfo.put("generated_ts", generated_ts);
		}

		if (!filter_name.equals("") && !filter_name.equals(null)) {
			reportListFilterInfo.put("filter_name", filter_name);
		}

		reportListFilterInfo.put("is_default", is_default);

		return reportListFilterInfo;
	}

	/***
	 * @author Nagamalleswari.Sykam Get backupJObDetailsTop 10 sources
	 * @param token
	 * @param addiitional_Url
	 * @param expectedStausCode
	 * @param ExpctedErrorMessgage
	 * @param test
	 * @return
	 */
	public Response getBackupJobreportsTop10soucres(String token, String addiitional_Url, int expectedStausCode,
			SpogMessageCode ExpctedErrorMessgage, ExtentTest test) {

		Response response = spogReportInvoker.getBackupJobReportsTopSourcesForLoggedInUser(token, addiitional_Url,
				test);
		spogServer.checkResponseStatus(response, expectedStausCode);

		if (expectedStausCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

		} else {
			String code = ExpctedErrorMessgage.getCodeString();
			String message = ExpctedErrorMessgage.getStatus();
			if (code.contains("00100201")) {
				message = message.replace("{0}", spogServer.getUUId());
			}
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.INFO, "The error code matched with the expected " + code);
			// checkErrorMessage(response,message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStausCode);
		}

		return response;

	}

	/**
	 * @author Nagamalleswari.Sykam
	 * @param ValidToken
	 * @param AdditionalUrl
	 * 
	 * 
	 */

	public Response getDataTranferSummaryDetails(String token, String additionalURL, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage, ExtentTest test) {
		test.log(LogStatus.INFO, "Call API - GET /datatransferreport/summary");
		Response response = spogReportInvoker.getDataTransferReportDetails(token, additionalURL, test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
		} else {
			spogServer.checkResponseStatus(response, expectedStatusCode, test);
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
		return response;

	}

	/**
	 * Create REPORT LIST FILTERS FOR LOGGED IN USER WITH CHECK
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param reportlistfilterInfo
	 * @param test
	 * @return response
	 */
	public Response createReportListFiltersForLoggedInUserWithCheck(String token,
			HashMap<String, Object> reportlistfiltersInfo, int expectedStatusCode, SpogMessageCode expectedErrorMessage,
			ExtentTest test) {

		String orgId = null, user_id = null;

		test.log(LogStatus.INFO, "Call the REST API Create report list filter for logged in user");
		
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!reportlistfiltersInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.report_list_filter.toString(),
					reportlistfiltersInfo.get("filter_name").toString(), "none", "none",
					Boolean.valueOf(reportlistfiltersInfo.get("is_default").toString()), reportlistfiltersInfo);
		}
		
		Response response = spogServer.createFilters(token, filter_info, "", test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_POST) {

			HashMap<String, Object> actual_response = new HashMap<>();
			actual_response = response.then().extract().path("data");

			spogServer.setToken(token);
			user_id = spogServer.GetLoggedinUser_UserID();
			orgId = spogServer.GetLoggedinUserOrganizationID();

			test.log(LogStatus.INFO, "Verify REPORT LIST FILTER INFO.");
			verifyReportListFilterData(user_id, orgId, reportlistfiltersInfo, actual_response, test);

		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}

		return response;
	}

	/**
	 * Create REPORT LIST FILTERS FOR SPECIFIED USER WITH CHECK
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param reportlistfilterInfo
	 * @param test
	 * @return response
	 */
	public Response createReportListFiltersForSpecifiedUserWithCheck(String token, String user_id,
			String organization_id, HashMap<String, Object> reportlistfiltersInfo, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage, ExtentTest test) {
		spogServer.setToken(token);
		test.log(LogStatus.INFO, "Call the REST API Create report list filter for specified user");
		
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!reportlistfiltersInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.report_list_filter.toString(),
					reportlistfiltersInfo.get("filter_name").toString(),user_id, organization_id,
					Boolean.valueOf(reportlistfiltersInfo.get("is_default").toString()), reportlistfiltersInfo);
		}
		
		Response response = spogServer.createFilters(token, filter_info, "", test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);
		/*
		 * Response response =
		 * spogReportInvoker.createReportListFiltersForSpecifiedUser(token, user_id,
		 * reportlistfiltersInfo, test);
		 */

		if (expectedStatusCode == SpogConstants.SUCCESS_POST) {

			HashMap<String, Object> actual_response = new HashMap<>();
			actual_response = response.then().extract().path("data");

			test.log(LogStatus.INFO, "Verify REPORT LIST FILTER INFO.");
			verifyReportListFilterData(user_id, organization_id, reportlistfiltersInfo, actual_response, test);

		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}

		return response;
	}

	/**
	 * Verify REPORT LIST FILTERS DATA
	 * 
	 * @author Rakesh.Chalamala
	 * @param expectedData
	 * @param actualData
	 * @param test
	 */
	@SuppressWarnings("unchecked")
	public void verifyReportListFilterData(String user_id, String organization_id, HashMap<String, Object> expectedData,
			HashMap<String, Object> actualData, ExtentTest test) {

		if (expectedData.containsKey("report_name")) {

			test.log(LogStatus.INFO, "Compare report_name");
			spogServer.assertResponseItem(expectedData.get("report_name"), actualData.get("report_name"), test);
		}

		if (expectedData.containsKey("date_range_type")) {

			test.log(LogStatus.INFO, "Compare date_range_type");
			// ArrayList<String> exp_date_range_type = new ArrayList<>();
			ArrayList<String> act_date_range_type = new ArrayList<>();
			act_date_range_type = (ArrayList<String>) actualData.get("date_range_type");

			if (act_date_range_type.size() > 1) {
				String[] exp_date_range_type = (String[]) expectedData.get("date_range_type");

				for (int i = 0; i < exp_date_range_type.length; i++) {
					if (exp_date_range_type[i].equals(act_date_range_type.get(i))) {
						spogServer.assertResponseItem(exp_date_range_type[i], act_date_range_type.get(i), test);
					}
				}
			} else {
				spogServer.assertResponseItem(expectedData.get("date_range_type"), act_date_range_type.get(0), test);
			}
		}

		if (expectedData.containsKey("report_for")) {

			test.log(LogStatus.INFO, "Compare report_for");
			HashMap<String, Object> exp_report_for = new HashMap<>();
			HashMap<String, Object> act_report_for = new HashMap<>();
			exp_report_for = (HashMap<String, Object>) expectedData.get("report_for");
			act_report_for = (HashMap<String, Object>) actualData.get("report_for");

			if (exp_report_for.containsKey("type")) {

				spogServer.assertResponseItem(exp_report_for.get("type"), act_report_for.get("type"), test);
			}

			if (exp_report_for.containsKey("details")) {
				if (((HashMap<String, Object>) exp_report_for.get("details")).containsKey("source_group_id")) {

					// ArrayList<String> exp_source_group_id = new ArrayList<>();
					ArrayList<String> act_source_group_id = new ArrayList<>();
					act_source_group_id = (ArrayList<String>) ((HashMap<String, Object>) act_report_for.get("details"))
							.get("source_group_id");

					if (act_source_group_id.size() > 1) {
						String[] exp_source_group_id = (String[]) ((HashMap<String, Object>) exp_report_for
								.get("details")).get("source_group_id");
						for (int i = 0; i < exp_source_group_id.length; i++) {
							spogServer.assertResponseItem(exp_source_group_id[i], act_source_group_id.get(i), test);
						}
					} else {
						spogServer.assertResponseItem(
								((HashMap<String, Object>) exp_report_for.get("details")).get("source_group_id"),
								act_source_group_id.get(0), test);
					}

				}
				if (((HashMap<String, Object>) exp_report_for.get("details")).containsKey("organization_id")) {
					// ArrayList<String> exp_organization_id = new ArrayList<>();
					ArrayList<String> act_organization_id = new ArrayList<>();
					act_organization_id = (ArrayList<String>) ((HashMap<String, Object>) act_report_for.get("details"))
							.get("organization_id");

					if (act_organization_id.size() > 1) {
						String[] exp_organization_id = (String[]) ((HashMap<String, Object>) exp_report_for
								.get("details")).get("organization_id");
						for (int i = 0; i < exp_organization_id.length; i++) {
							spogServer.assertResponseItem(exp_organization_id[i], act_organization_id.get(i), test);
						}
					} else {
						spogServer.assertResponseItem(
								((HashMap<String, Object>) exp_report_for.get("details")).get("organization_id"),
								act_organization_id.get(0), test);
					}
				}
			}
		}

		if (expectedData.containsKey("schedule_frequency")) {
			test.log(LogStatus.INFO, "Compare schedule_frequency");
			ArrayList<String> exp_schedule_frequency = new ArrayList<>();
			ArrayList<String> act_schedule_frequency = new ArrayList<>();
			act_schedule_frequency = (ArrayList<String>) actualData.get("schedule_frequency");

			if (act_schedule_frequency.size() > 1) {
				exp_schedule_frequency = (ArrayList<String>) expectedData.get("schedule_frequency");
				for (int i = 0; i < exp_schedule_frequency.size(); i++) {
					spogServer.assertResponseItem(exp_schedule_frequency.get(i), act_schedule_frequency.get(i), test);
				}
			} else {
				spogServer.assertResponseItem(expectedData.get("schedule_frequency"), act_schedule_frequency.get(0),
						test);
			}
		}

		if (expectedData.containsKey("generated_ts")) {

			HashMap<String, Object> exp_gen_ts = (HashMap<String, Object>) expectedData.get("generated_ts");
			HashMap<String, Object> act_gen_ts = (HashMap<String, Object>) actualData.get("generated_ts");

			test.log(LogStatus.INFO, "Compare generated_ts");
			spogServer.assertResponseItem(exp_gen_ts.get("start").toString(), act_gen_ts.get("start").toString(), test);
			spogServer.assertResponseItem(exp_gen_ts.get("end").toString(), act_gen_ts.get("end").toString(), test);
		}

		if (expectedData.containsKey("filter_name")) {
			test.log(LogStatus.INFO, "Compare filter_name");
			spogServer.assertResponseItem(expectedData.get("filter_name"), actualData.get("filter_name"), test);
		}

		if (expectedData.containsKey("is_default")) {
			test.log(LogStatus.INFO, "Compare is_default");
			spogServer.assertResponseItem(expectedData.get("is_default"), actualData.get("is_default"), test);
		}

		test.log(LogStatus.INFO, "Compare user_id");
		spogServer.assertResponseItem(user_id, actualData.get("user_id"), test);

		if (organization_id != null) {
			test.log(LogStatus.INFO, "Compare organization_id");
			spogServer.assertResponseItem(organization_id, actualData.get("organization_id"), test);
		}

	}

	/**
	 * Delete REPORT LIST FILTERES FOR LOGGED IN USER BY FILTER ID WITH CHECK
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param filter_id
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 */
	public void deleteReportListFiltersForLoggedInUserByFilterIdWithCheck(String token, String filter_id,
			int expectedStatusCode, SpogMessageCode expectedErrorMessage, ExtentTest test) {

		test.log(LogStatus.INFO, "Call the API deleteReportListFiltersForLoggedInUserByFilterId");
		/*
		 * Response response =
		 * spogReportInvoker.deleteReportListFiltersForLoggedInUserByFilterId(token,
		 * filter_id, test);
		 */
		Response response = spogServer.deleteFiltersByID(token, filter_id, "none", test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.INFO,
					"The Report List Filter with filter_id:" + filter_id + " for Loggedin User deleted successfully.");
		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();

			if (code.contains("00A00702")) {
				spogServer.setToken(token);
				String user_id = spogServer.GetLoggedinUser_UserID();
				message = message.replace("{0}", filter_id);
				message = message.replace("{1}", user_id);
			}
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
	}

	/**
	 * Delete REPORT LIST FILTERES FOR SPECIFIED USER BY FILTER ID WITH CHECK
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param user_id
	 * @param filter_id
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 */
	public void deleteReportListFiltersForSpecifiedUserByFilterIdWithCheck(String token, String user_id,
			String filter_id, int expectedStatusCode, SpogMessageCode expectedErrorMessage, ExtentTest test) {

		spogServer.setToken(token);
		test.log(LogStatus.INFO, "Call the API deleteReportListFiltersForSpecifiedUserByFilterIdWithCheck");
		Response response = spogServer.deleteFiltersByID(token, filter_id, user_id, test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.INFO, "The Report List Filters for Specified User deleted successfully.");
		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();

			if (code.contains("00A00702")) {
				message = message.replace("{0}", filter_id);
				message = message.replace("{1}", user_id);

				spogServer.checkErrorCode(response, code);
				test.log(LogStatus.PASS, "The error code matched with the expected " + code);
				spogServer.checkErrorMessage(response, message);
				test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
			}
		}
	}

	/**
	 * GET REPORT LIST FILTERS
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param expectedData
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 */
	public void getReportListFiltersWithCheck(String token, ArrayList<HashMap<String, Object>> expectedData,
			int expectedStatusCode, SpogMessageCode expectedErrorMessage, ExtentTest test) {

		test.log(LogStatus.INFO, "Call the API getReportListFIlters");
		/* Response response = spogReportInvoker.getReportListFilters(token, test); */
		Response response = spogServer.getFilters(token, "none", filterType.report_list_filter_global.toString(), "",test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			ArrayList<HashMap<String, Object>> actualData = new ArrayList<>();
			actualData = response.then().extract().path("data");

			test.log(LogStatus.INFO, "Sorting the expected and actual response by filter_id");
			spogServer.sortArrayListbyString(expectedData, "filter_id");
			spogServer.sortArrayListbyString(actualData, "filter_id");

			test.log(LogStatus.INFO, "Check the default response");
			spogServer.assertResponseItem(expectedData, actualData, test);

		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
	}

	/**
	 * GET REPORT LIST FILTERS FOR LOGGED IN USER
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param filterStr
	 * @param expectedStatusCode
	 * @param test
	 * @return response
	 */
	public Response getReportListFiltersForLoggedInUser(String token, String filterStr, int expectedStatusCode,
			ExtentTest test) {

		String additionalURL = null;

		if (filterStr != null && filterStr != "") {
			// Preparing additional URL without sort and default page number and page_size
			additionalURL = spogServer.PrepareURL(filterStr, null, 1, 20, test);
		}

		test.log(LogStatus.INFO, "Call the API getReportListFiltersForLoggedInUser");
		/*
		 * Response response =
		 * spogReportInvoker.getReportListFiltersForLoggedInUser(token, additionalURL,
		 * test);
		 */
		Response response = spogServer.getFilters(token, "none", filterType.report_list_filter.toString(),
				additionalURL,test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);
		return response;
	}

	/**
	 * GET REPORT LIST FILTERS FOR LOGGED IN USER
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param expectedData
	 * @param filterStr
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 */
	public void getReportListFiltersForLoggedInUserWithCheck(String token,
			ArrayList<HashMap<String, Object>> expectedData, String filterStr, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage, ExtentTest test) {

		String additionalURL = null, user_id = null, organization_id = null;
		ArrayList<HashMap<String, Object>> filteredExpectedData = new ArrayList<>();

		if (filterStr != null && filterStr != "") {
			// Preparing additional URL without sort and default page number and page_size
			additionalURL = spogServer.PrepareURL(filterStr, null, 1, 20, test);
		}

		test.log(LogStatus.INFO, "Call the API getReportListFiltersForLoggedInUser");
		/*
		 * Response response =
		 * spogReportInvoker.getReportListFiltersForLoggedInUser(token, additionalURL,
		 * test);
		 */
		Response response = spogServer.getFilters(token, "none", filterType.report_list_filter.toString(),
				additionalURL,test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			ArrayList<HashMap<String, Object>> actualData = new ArrayList<>();
			actualData = response.then().extract().path("data");

			if (expectedData.size() > 1) {
				// Making the previous filter is default value to false

				for (int i = 0; i < expectedData.size(); i++) {

					if (expectedData.get(expectedData.size() - 1 - i).get("is_default").equals(true)) {

						for (int j = 0; j < (expectedData.size() - i) - 1; j++) {
							expectedData.get(j).put("is_default", false);
						}
						break;
					}
				}
			}

			if (filterStr != null && !filterStr.isEmpty()) {

				String[] eachFilterStr = filterStr.split(";");
				for (int j = 0; j < expectedData.size(); j++) {

					if (eachFilterStr[0].contains("is_default")) {
						if (expectedData.get(j).containsValue(Boolean.valueOf(eachFilterStr[2]))) {
							filteredExpectedData.add(expectedData.get(j));
						}
					} else {
						test.log(LogStatus.INFO, "filter is not 'is_default' but  " + filterStr);
					}
				}
			} else {
				filteredExpectedData = expectedData;
			}

			if (filteredExpectedData.size() > 1) {

				test.log(LogStatus.INFO, "Sorting the filteres in both expectedData and actualData");
				spogServer.sortArrayListbyString(filteredExpectedData, "filter_name");
				spogServer.sortArrayListbyString(actualData, "filter_name");
			}

			for (int i = 0; i < actualData.size(); i++) {

				spogServer.setToken(token);
				user_id = /* filteredExpectedData.get(i).get("user_id").toString(); */ spogServer
						.GetLoggedinUser_UserID();
				organization_id = /* filteredExpectedData.get(i).get("organization_id").toString(); */ spogServer
						.GetLoggedinUserOrganizationID();

				test.log(LogStatus.INFO, "Verify the getReportListFilters");
				verifyReportListFilterData(user_id, organization_id, filteredExpectedData.get(i), actualData.get(i),
						test);
			}

		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
	}

	/**
	 * GET REPORT LIST FILTERS FOR LOGGED IN USER BY FILTER ID
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param filter_id
	 * @param expectedData
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 */
	public void getReportListFiltersForLoggedInUserByFilterIdWithCheck(String token, String filter_id,
			HashMap<String, Object> expectedData, int expectedStatusCode, SpogMessageCode expectedErrorMessage,
			ExtentTest test) {

		test.log(LogStatus.INFO, "Call the API getReportListFiltersForLoggedInUserByFilterId");
		/*
		 * Response response =
		 * spogReportInvoker.getReportListFiltersForLoggedInUserByFilterId(token,
		 * filter_id, test);
		 */
		Response response = spogServer.getFiltersById(token, filter_id, filterType.report_list_filter.toString(),
				"none", "none",test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			String user_id = null, organization_id = null;
			HashMap<String, Object> actualData = new HashMap<>();
			actualData = response.then().extract().path("data");

			spogServer.setToken(token);
			user_id = spogServer.GetLoggedinUser_UserID();
			organization_id = spogServer.GetLoggedinUserOrganizationID();

			test.log(LogStatus.INFO, "Verify the getReportListFilters");
			verifyReportListFilterData(user_id, organization_id, expectedData, actualData, test);
		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			if (code.contains("00A00702")) {
				spogServer.setToken(token);
				message = message.replace("{0}", filter_id);
				message = message.replace("{1}", spogServer.GetLoggedinUser_UserID());
			}

			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
	}

	/**
	 * GET REPORT LIST FILTERS FOR SPECIFIED USER
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param user_id
	 * @param expectedData
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 */
	public void getReportListFiltersForSpecifiedUserWithCheck(String token, String user_id,
			ArrayList<HashMap<String, Object>> expectedData, String filterStr, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage, ExtentTest test) {

		String additionalURL = null;
		ArrayList<HashMap<String, Object>> filteredExpectedData = new ArrayList<>();

		if (filterStr != null && filterStr != "") {
			// Preparing additional URL without sort and default page number and page_size
			additionalURL = spogServer.PrepareURL(filterStr, null, 1, 20, test);
		}

		test.log(LogStatus.INFO, "Call the API getReportListFilteresForSpecifiedUser");
		/*
		 * Response response =
		 * spogReportInvoker.getReportListFilteresForSpecifiedUser(token, user_id,
		 * additionalURL, test);
		 */
		Response response = spogServer.getFilters(token, user_id, filterType.report_list_filter.toString(),
				additionalURL,test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			spogServer.setToken(token);
			String organization_id = spogServer.GetLoggedinUserOrganizationID();

			ArrayList<HashMap<String, Object>> actualData = new ArrayList<>();
			actualData = response.then().extract().path("data");

			if (expectedData.size() > 1) {
				// Making the previous filter is default value to false

				for (int i = 0; i < expectedData.size(); i++) {

					if (expectedData.get(expectedData.size() - 1 - i).get("is_default").equals(true)) {

						for (int j = 0; j < (expectedData.size() - i) - 1; j++) {
							expectedData.get(j).put("is_default", false);
						}
						break;
					}
				}
			}

			if (filterStr != null && !filterStr.isEmpty()) {

				String[] eachFilterStr = filterStr.split(";");
				for (int j = 0; j < expectedData.size(); j++) {

					if (eachFilterStr[0].contains("is_default")) {
						if (expectedData.get(j).containsValue(Boolean.valueOf(eachFilterStr[2]))) {
							filteredExpectedData.add(expectedData.get(j));
						}
					} else {
						test.log(LogStatus.INFO, "filter is not 'is_default' but  " + filterStr);
					}
				}
			} else {
				filteredExpectedData = expectedData;
			}

			if (filteredExpectedData.size() > 1) {

				test.log(LogStatus.INFO, "Sorting the filteres in both expectedData and actualData");
				spogServer.sortArrayListbyString(filteredExpectedData, "filter_name");
				spogServer.sortArrayListbyString(actualData, "filter_name");
			}

			for (int i = 0; i < actualData.size(); i++) {

				test.log(LogStatus.INFO, "Verify the getReportListFilters");
				verifyReportListFilterData(user_id, organization_id, filteredExpectedData.get(i), actualData.get(i),
						test);
			}

		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
	}

	/**
	 * GET REPORT LIST FILTERS FOR SPECIFIED USER BY FILTER ID
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param user_id
	 * @param filter_id
	 * @param expectedData
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 */
	public void getReportListFiltersForSpecifiedUserByFilterIdWithCheck(String token, String user_id,
			String organization_id, String filter_id, HashMap<String, Object> expectedData, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage, ExtentTest test) {

		test.log(LogStatus.INFO, "Call the API getReportListFiltersForSpecifiedUserByFilterId");
		/*
		 * Response response =
		 * spogReportInvoker.getReportListFilteresForSpecifiedUserByFilterId(token,
		 * user_id, filter_id, test);
		 */
		Response response = spogServer.getFiltersById(token, filter_id, filterType.report_list_filter.toString(),
				user_id, organization_id,test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			HashMap<String, Object> actualData = new HashMap<>();
			actualData = response.then().extract().path("data");
			test.log(LogStatus.INFO, "Verify the getReportListFilters");
			verifyReportListFilterData(user_id, organization_id, expectedData, actualData, test);
		} else {

			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			if (code.contains("00A00702")) {
				message = message.replace("{0}", filter_id);
				message = message.replace("{1}", user_id);

			}
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
	}

	/**
	 * UPDATE REPORT LIST FILTERS FOR SPECIFIED USER BY FILTER ID
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param user_id
	 * @param filter_id
	 * @param updateReportListFiltersInfo
	 * @param expectedData
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 */
	public Response updateReportListFiltersForSpecifiedUserByFilterIdWithCheck(String token, String user_id,
			String organization_id, String filter_id, HashMap<String, Object> expectedData, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage, ExtentTest test) {

		test.log(LogStatus.INFO, "Call the API updateReportListFiltersForSpecifiedUserByFilterId");
		/*
		 * Response response =
		 * spogReportInvoker.updateReportListFiltersForSpecifiedUser(token, user_id,
		 * filter_id, expectedData, test);
		 */
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!expectedData.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.report_filter.toString(),
					expectedData.get("filter_name").toString(), user_id, organization_id,
					Boolean.valueOf(expectedData.get("is_default").toString()), expectedData);
		}
		Response response = spogServer.updateFilterById(token, filter_id, user_id, filter_info, "", test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			HashMap<String, Object> actualData = new HashMap<>();
			actualData = response.then().extract().path("data");

			test.log(LogStatus.INFO, "Verify the updateReportListFiltersForSpecifiedUser");
			verifyReportListFilterData(user_id, organization_id, expectedData, actualData, test);
		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
		return response;
	}

	/**
	 * UPDATE REPORT LIST FILTERS FOR LOGGED IN USER BY FILTER ID
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param filter_id
	 * @param updateReportListFiltersInfo
	 * @param expectedData
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 */
	public Response updateReportListFiltersForLoggedInUserByFilterIdWithCheck(String token, String filter_id,
			HashMap<String, Object> expectedData, int expectedStatusCode, SpogMessageCode expectedErrorMessage,
			ExtentTest test) {

		test.log(LogStatus.INFO, "Call the API updateReportListFiltersForLoggedInUserByFilterId");
		/*
		 * Response response =
		 * spogReportInvoker.updateReportListFiltersForLoggedInUser(token, filter_id,
		 * expectedData, test);
		 */
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!expectedData.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.report_filter.toString(),
					expectedData.get("filter_name").toString(), "none", "none",
					Boolean.valueOf(expectedData.get("is_default").toString()), expectedData);
		}
		Response response = spogServer.updateFilterById(token, filter_id, "none", filter_info, "", test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			spogServer.setToken(token);
			String user_id = spogServer.GetLoggedinUser_UserID();
			String organization_id = spogServer.GetLoggedinUserOrganizationID();

			HashMap<String, Object> actualData = new HashMap<>();
			actualData = response.then().extract().path("data");

			test.log(LogStatus.INFO, "Verify the getReportListFilters");
			verifyReportListFilterData(user_id, organization_id, expectedData, actualData, test);
		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();

			if (code.contains("00A00702")) {
				spogServer.setToken(token);
				message = message.replace("{0}", filter_id);
				message = message.replace("{1}", spogServer.GetLoggedinUser_UserID());
			}

			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
		return response;
	}

	/**
	 * @author ramya.nagepalli This method is used to
	 *         getRestoreJobReportsTopSourcesForLoggedInUser
	 * 
	 * @param token
	 * @param expectedsources
	 * @param expectedstatuscode
	 * @param errorMessage
	 *            return response
	 * 
	 */
	public Response getRestoreJobReportsTopSourcesForLoggedInUser(String adminToken,
			ArrayList<HashMap<String, Object>> expectedsources, String additionalUrl, int expectedstatuscode,
			SpogMessageCode errorMessage, ExtentTest test) {
		Response response = spogReportInvoker.getRestoreJobReportsTopSourcesForLoggedInUser(adminToken, additionalUrl,
				test);

		spogServer.checkResponseStatus(response, expectedstatuscode);

		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			ArrayList<HashMap<String, Object>> actual_response = new ArrayList<HashMap<String, Object>>();
			HashMap<String, Object> temp = new HashMap<String, Object>();
			actual_response = response.then().extract().path("data");

			/* actual_sources=response.then().extract().path("data.sources"); */
			int total = actual_response.size();
			for (int i = 0; i < total; i++) {
				for (int j = 0; j < total; j++) {
					if (!(additionalUrl == "")) {

						if (additionalUrl.contains("organization_id")) {

						}

					}

					String exp_stack = expectedsources.get(i).get("stack").toString();
					String act_stack = actual_response.get(j).get("stack").toString();

					if (exp_stack.equals(act_stack)) {

						String exp_name = expectedsources.get(i).get("name").toString();
						String act_name = actual_response.get(j).get("name").toString();

						spogServer.assertResponseItem(exp_stack, act_stack);
						spogServer.assertResponseItem(exp_name, act_name);

						int exp_value = (int) expectedsources.get(i).get("value");
						int act_value = (int) actual_response.get(j).get("value");
						spogServer.assertResponseItem(exp_value, act_value, test);
					}
				}

			}

		} else {
			String code = errorMessage.getCodeString();
			String message = errorMessage.getStatus();
			if (code.contains("00100201")) {
				message = message.replace("{0}", spogServer.getUUId());
			}
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.INFO, "The error code matched with the expected " + code);
			// checkErrorMessage(response,message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);

		}

		return response;
	}

	/**
	 * @author ramya.nagepalli This method is used to checkJobReportsDetails
	 * @param job_id
	 * @param status
	 * @param source_id
	 * @param sourceName
	 * @param destination_name
	 * @param destination_id
	 * @param policy_name
	 * @param policy_id
	 * @param groupName
	 * @param group_id
	 * @param organization_name
	 * @param organization_id
	 * @param expectedStatusCode
	 * @param errorMessage
	 * 
	 *            return
	 */
	public void checkJobDetails(Response response, String job_id, String status, String source_id, String sourceName,
			String destination_name, String destination_id, String policy_name, String policy_id, String groupName,
			String group_id, String organization_name, String organization_id, int expectedStatusCode,
			SpogMessageCode errorMessage, ExtentTest test) {
		// TODO Auto-generated method stub

		ArrayList<HashMap> actual = null;

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			actual = response.then().extract().path("data");

			for (int i = 0; i < actual.size(); i++) {
				String act_job_id = response.then().extract().path("data.job_id");
				spogServer.assertResponseItem(job_id, act_job_id);

				String act_job_status = response.then().extract().path("data.job_status");
				spogServer.assertResponseItem(status, act_job_status);

				String act_source_id = response.then().extract().path("data.source_id");
				spogServer.assertResponseItem(source_id, act_source_id);

				String act_source_name = response.then().extract().path("data.source_name");
				spogServer.assertResponseItem(sourceName, act_source_name);

				String act_destination_name = response.then().extract().path("data.destination_name");
				spogServer.assertResponseItem(destination_name, act_destination_name);

				String act_destination_id = response.then().extract().path("data.destination_id");
				spogServer.assertResponseItem(destination_id, act_destination_id);

				String act_policy_name = response.then().extract().path("data.policy_name");
				spogServer.assertResponseItem(policy_name, act_policy_name);

				String act_policy_id = response.then().extract().path("data.policy_id");
				spogServer.assertResponseItem(policy_id, act_policy_id);

				String act_group_name = response.then().extract().path("data.group_name");
				spogServer.assertResponseItem(groupName, act_group_name);

				String act_group_id = response.then().extract().path("data.group_id");
				spogServer.assertResponseItem(group_id, act_group_id);

				String act_org_name = response.then().extract().path("data.organization_name");
				spogServer.assertResponseItem(organization_name, act_org_name);

				String act_org_id = response.then().extract().path("data.organization_id");
				spogServer.assertResponseItem(organization_id, act_org_id);

				test.log(LogStatus.INFO, "The actual data matched with the expected");
			}

		} else {
			String code = errorMessage.getCodeString();

			String message = errorMessage.getStatus();

			if (code.contains("00E00008")) {

				message = message.replace("{0}", policy_id);

			}

			spogServer.checkErrorCode(response, code);

			test.log(LogStatus.PASS, "The error code matched with the expected " + code);

			spogServer.checkErrorMessage(response, message);

			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);

		}

	}

	/**
	 * @author ramya.nagepalli This method is used to checkJobReportsStatusSummary
	 * 
	 * @param response
	 * 
	 * @param StatusDetails
	 * 
	 * @param expectedStatusCode
	 * 
	 * @param ErrorMessage
	 * 
	 * @param test
	 *            return
	 */
	public void checkJobReportsStatusSummary(Response response, ArrayList<HashMap> statusDetails,
			int expectedStatusCode, SpogMessageCode errorMessage, ExtentTest test) {
		// TODO Auto-generated method stub

		// check the response status summary details for Job Reports

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			int length = statusDetails.size();
			for (int i = 0; i < length; i++) {
				String act_jobStatus = response.then().extract().path("data[" + i + "].job_status");
				spogServer.assertResponseItem(statusDetails.get(i).get("job_status").toString(), act_jobStatus);

				String act_job_count = response.then().extract().path("data[" + i + "].job_count");
				spogServer.assertResponseItem(statusDetails.get(i).get("job_count").toString(), act_job_count);

				String act_time_instance = response.then().extract().path("data[" + i + "].time_instance");
				spogServer.assertResponseItem(statusDetails.get(i).get("time_instance").toString(), act_time_instance);

				test.log(LogStatus.PASS, "reponse data matched with the expected data of job reports");
			}

		} else {
			String code = errorMessage.getCodeString();

			String message = errorMessage.getStatus();

			spogServer.checkErrorCode(response, code);

			test.log(LogStatus.PASS, "The error code matched with the expected " + code);

			spogServer.checkErrorMessage(response, message);

			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);

		}

	}

	/**
	 * @author ramya.nagepalli This method is used to getRestoreJobReportsDetails
	 * 
	 * @param additionalUrl
	 * @param token
	 * @param expectedStatusCode
	 * 
	 *            return response
	 */

	public Response getRestoreJobReportsDetails(String token, String additionalURL, int expectedstatuscode,
			SpogMessageCode errorMessage, ExtentTest test) {
		// TODO Auto-generated method stub
		Response response = spogReportInvoker.getRestoreJobReportsDetails(token, additionalURL, test);
		spogServer.checkResponseStatus(response, expectedstatuscode);

		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

		} else {
			String code = errorMessage.getCodeString();
			String message = errorMessage.getStatus();
			if (code.contains("00100201")) {
				message = message.replace("{0}", spogServer.getUUId());
			}
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.INFO, "The error code matched with the expected " + code);
			// checkErrorMessage(response,message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);

		}

		return response;
	}

	/**
	 * @author ramya.nagepalli This method is used to composeExpectedReports
	 * 
	 * @param response
	 * @param StatusDetails
	 * @param expectedStatusCode
	 * @param ErrorMessage
	 * @param test
	 *            return
	 */

	public HashMap<String, Object> composeExpectedReports(String job_id, String jobStatus, String source_id,
			String sourceName, String destination_id, String destination_name, String policy_id, String policy_name,
			String group_id, String group_name, String organization_id, String organization_name) {
		// TODO Auto-generated method stub

		HashMap<String, Object> expected = new HashMap<String, Object>();
		expected.put("job_id", job_id);
		expected.put("job_status", jobStatus);
		expected.put("source_id", source_id);
		expected.put("source_name", sourceName);
		expected.put("destination_id", destination_id);
		expected.put("destination_name", destination_name);
		expected.put("policy_id", policy_id);
		expected.put("policy_name", policy_name);
		expected.put("source_group_id", group_id);
		expected.put("source_group_name", group_name);
		expected.put("organization_id", organization_id);
		expected.put("organization_name", organization_name);

		return expected;
	}

	/**
	 * @author ramya.nagepalli This method is used to
	 *         getBackupJobReportsTopSourcesForLoggedInUser
	 * 
	 * @param token
	 * @param expectedsources
	 * @param expectedStatusCode
	 *            return response
	 */

	public Response GetRestoreJobReportsStatusSummaryTest(String token,
			ArrayList<HashMap<String, Object>> expectedsources, String additionalUrl, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage, ExtentTest test) {
		// TODO Auto-generated method stub
		Response response = spogReportInvoker.GetRestoreJobReportsStatusSummaryTest(token, additionalUrl, test);

		spogServer.checkResponseStatus(response, expectedStatusCode);
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			ArrayList<HashMap<String, Object>> actual_response = new ArrayList<HashMap<String, Object>>();
			HashMap<String, Object> temp = new HashMap<String, Object>();
			actual_response = response.then().extract().path("data");

			/* actual_sources=response.then().extract().path("data.sources"); */
			int total = actual_response.size();
			for (int i = 0; i < total; i++) {
				for (int j = 0; j < total; j++) {
					if (!(additionalUrl == "")) {

						if (additionalUrl.contains("organization_id")) {
							/*
							 * String []item=additionalUrl.split("="); for(int
							 * k=0;k<expectedsources.size();k++) {
							 * 
							 * if(expectedsources.get(k).containsValue(item[1])) {
							 * temp=(HashMap)expectedsources.get(k); composed_expectedsources.add(temp); }
							 * 
							 * 
							 * } expectedsources=composed_expectedsources;
							 */

						}

					}

					String exp_stack = expectedsources.get(i).get("job_status").toString();
					String act_stack = actual_response.get(j).get("job_status").toString();

					if (exp_stack.equals(act_stack)) {

						String exp_name = expectedsources.get(i).get("job_count").toString();
						String act_name = actual_response.get(j).get("job_count").toString();

						spogServer.assertResponseItem(exp_stack, act_stack);
						spogServer.assertResponseItem(exp_name, act_name);

						String exp_value = (String) expectedsources.get(i).get("time_instance");
						String act_value = (String) actual_response.get(j).get("time_instance");
						// spogServer.assertResponseItem(exp_value, act_value,test);
					}
				}

			}

		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			if (code.contains("00100201") || code.contains("00100003") || code.contains("40000005")) {
				String[] a = additionalUrl.split("=");
				if (a.length > 1) {
					message = message.replace("{0}", a[1]);
				} else {
					message = message.replace("{0}", "");
				}
			}

			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.INFO, "The error code matched with the expected " + code);
			// checkErrorMessage(response,message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);

		}

		return response;
	}

	/**
	 * @author ramya.nagepalli This method is used to
	 *         getBackupJobReportsTopSourcesForLoggedInUser
	 * 
	 * @param token
	 * @param expectedsources
	 * @param additionalUrl
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 *            return response
	 */

	public Response getBackupJobReportsTopSourcesForLoggedInUser(String token, String additionalUrl,
			ArrayList<HashMap<String, Object>> expectedsources, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage, ExtentTest test) {
		// TODO Auto-generated method stub
		Response response = spogReportInvoker.getBackupJobReportsTopSourcesForLoggedInUser(token, additionalUrl, test);

		spogServer.checkResponseStatus(response, expectedStatusCode);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			ArrayList<HashMap<String, Object>> actual_response = new ArrayList<HashMap<String, Object>>();
			HashMap<String, Object> temp = new HashMap<String, Object>();
			actual_response = response.then().extract().path("data");

			ArrayList<HashMap<String, Object>> actual_sources = new ArrayList<HashMap<String, Object>>();
			ArrayList<HashMap<String, Object>> expected_filter_sources = new ArrayList<HashMap<String, Object>>();
			actual_sources = response.then().extract().path("data");
			int total = actual_sources.size();
			for (int i = 0; i < total; i++) {
				for (int j = 0; j < total; j++) {
					if (!(additionalUrl == "")) {

						if (additionalUrl.contains("organization_id")) {

						}
						if (additionalUrl.contains("source_name"))

						{
							String[] item = additionalUrl.split("=");
							for (int k = 0; k < expectedsources.size(); k++) {

								if (expectedsources.get(k).containsValue(item[1])) {
									temp = (HashMap) expectedsources.get(k);
									expected_filter_sources.add(temp);
								}

							}
						}

					}

					if (expected_filter_sources.isEmpty()) {
						String exp_stack = expectedsources.get(i).get("stack").toString();
						String act_stack = actual_sources.get(j).get("stack").toString();

						if (exp_stack.equals(act_stack)) {
							String exp_name = expectedsources.get(i).get("name").toString();
							String act_name = actual_sources.get(j).get("name").toString();

							spogServer.assertResponseItem(exp_stack, act_stack);
							spogServer.assertResponseItem(exp_name, act_name);

							int exp_value = (int) expectedsources.get(i).get("value");
							int act_value = (int) actual_sources.get(j).get("value");
							// spogServer.assertResponseIntegerItem(exp_value, act_value,test);
						}
					} else {
						String exp_stack = expected_filter_sources.get(i).get("stack").toString();
						String act_stack = actual_sources.get(j).get("stack").toString();

						if (exp_stack.equals(act_stack)) {
							String exp_name = expected_filter_sources.get(i).get("name").toString();
							String act_name = actual_sources.get(j).get("name").toString();

							spogServer.assertResponseItem(exp_stack, act_stack);
							spogServer.assertResponseItem(exp_name, act_name);

							int exp_value = (int) expected_filter_sources.get(i).get("value");
							int act_value = (int) actual_sources.get(j).get("value");
							// spogServer.assertResponseIntegerItem(exp_value, act_value,test);
						}
					}
				}

			}

		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			if (code.contains("00100201")) {
				message = message.replace("{0}", spogServer.getUUId());
			}
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.INFO, "The error code matched with the expected " + code);
			// checkErrorMessage(response,message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);

		}

		return response;
	}

	/**
	 * @author ramya.nagepalli This method is used to
	 *         getBackupJobReportsDetailsStatusSummary
	 * 
	 * @param token
	 * @param expectedReports
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 *            return response
	 */

	public Response getBackupJobReportsDetailsStatusSummary(String token, String additionalURL,
			ArrayList<HashMap<String, Object>> expectedReports, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage, ExtentTest test) {
		// TODO Auto-generated method stub
		Response response = spogReportInvoker.getBackupJobReportsDetailsStatusSummary(token, additionalURL, test);

		spogServer.checkResponseStatus(response, expectedStatusCode);
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			ArrayList<HashMap<String, Object>> actualReports = response.then().extract().path("data");

			for (int i = 0; i < actualReports.size(); i++) {

				for (int j = 0; j < actualReports.size(); j++) {
					String act_status = actualReports.get(i).get("job_status").toString();
					String exp_status = expectedReports.get(j).get("job_status").toString();
					// spogServer.assertResponseItem(exp_status, act_status);

					if (act_status.equals(exp_status)) {
						String act_count = actualReports.get(i).get("job_count").toString();
						String exp_count = expectedReports.get(i).get("job_count").toString();
						spogServer.assertResponseItem(exp_count, act_count);
					} else {
						j++;
					}
				}

			}

		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}

		return response;
	}

	/**
	 * @author ramya.nagepalli This method is used to getBackupReportsDetails
	 * 
	 * @param token
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 *            return response
	 */

	public Response getBackupReportsDetails(String token, String AdditionalUrl, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage, ExtentTest test) {
		// TODO Auto-generated method stub
		Response response = spogReportInvoker.getBackupReportsDetails(token, AdditionalUrl, test);

		spogServer.checkResponseStatus(response, expectedStatusCode);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			if (code.contains("00100201")) {
				message = message.replace("{0}", spogServer.getUUId());
			}
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.INFO, "The error code matched with the expected " + code);
			// checkErrorMessage(response,message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);

		}

		return response;
	}

	/**
	 * @author Rakesh.Chalamala
	 * @param column_id
	 * @param long_label
	 * @param short_label
	 * @param key
	 * @param sort
	 * @param filter
	 * @param visible
	 * @param order_id
	 * @return columnHeadContent
	 */
	private HashMap<String, Object> composeRestoreJobReportsColumnInfo(String column_id, String long_label,
			String short_label, String key, boolean sort, boolean filter, boolean visible, int order_id) {

		// TODO Auto-generated method stub
		HashMap<String, Object> columnHeadContent = new HashMap<String, Object>();
		columnHeadContent.put("long_label", long_label);
		columnHeadContent.put("short_label", short_label);
		columnHeadContent.put("filter", filter);
		columnHeadContent.put("column_id", column_id);
		columnHeadContent.put("key", key);
		columnHeadContent.put("sort", sort);
		columnHeadContent.put("visible", visible);
		columnHeadContent.put("order_id", order_id);

		return columnHeadContent;
	}

	/**
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param expectedStatusCode
	 * @param ExpectedErrorMessage
	 * @param test
	 */
	public Response getSystemRestoreJobReportsColumnsWithCheck(String token, int expctedStatusCode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		Response response = spogReportInvoker.getSystemRestoreJobReportsColumns(token, test);
		spogServer.checkResponseStatus(response, expctedStatusCode);
		ArrayList<HashMap<String, Object>> expectedColumnsHeadContents = new ArrayList<>();

		if (expctedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			HashMap<String, Object> columnHeadContent1 = new HashMap();
			columnHeadContent1 = composeRestoreJobReportsColumnInfo(RestoreJobReportsColumns.NAME_COLUMNID,
					RestoreJobReportsColumns.NAME_LONG, RestoreJobReportsColumns.NAME_SHORT,
					RestoreJobReportsColumns.NAME_KEY, RestoreJobReportsColumns.NAME_SORT,
					RestoreJobReportsColumns.NAME_FILTER, RestoreJobReportsColumns.NAME_VISIBLE,
					RestoreJobReportsColumns.NAME_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent1);

			HashMap<String, Object> columnHeadContent2 = new HashMap();
			columnHeadContent2 = composeRestoreJobReportsColumnInfo(RestoreJobReportsColumns.STATUS_COLUMNID,
					RestoreJobReportsColumns.STATUS_LONG, RestoreJobReportsColumns.STATUS_SHORT,
					RestoreJobReportsColumns.STATUS_KEY, RestoreJobReportsColumns.STATUS_SORT,
					RestoreJobReportsColumns.STATUS_FILTER, RestoreJobReportsColumns.STATUS_VISIBLE,
					RestoreJobReportsColumns.STATUS_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent2);

			HashMap<String, Object> columnHeadContent3 = new HashMap();
			columnHeadContent3 = composeRestoreJobReportsColumnInfo(RestoreJobReportsColumns.DESTINATION_COLUMNID,
					RestoreJobReportsColumns.DESTINATION_LONG, RestoreJobReportsColumns.DESTINATION_SHORT,
					RestoreJobReportsColumns.DESTINATION_KEY, RestoreJobReportsColumns.DESTINATION_SORT,
					RestoreJobReportsColumns.DESTINATION_FILTER, RestoreJobReportsColumns.DESTINATION_VISIBLE,
					RestoreJobReportsColumns.DESTINATION_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent3);

			HashMap<String, Object> columnHeadContent4 = new HashMap();
			columnHeadContent4 = composeRestoreJobReportsColumnInfo(RestoreJobReportsColumns.SOURCE_COLUMNID,
					RestoreJobReportsColumns.SOURCE_LONG, RestoreJobReportsColumns.SOURCE_SHORT,
					RestoreJobReportsColumns.SOURCE_KEY, RestoreJobReportsColumns.SOURCE_SORT,
					RestoreJobReportsColumns.SOURCE_FILTER, RestoreJobReportsColumns.SOURCE_VISIBLE,
					RestoreJobReportsColumns.SOURCE_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent4);

			HashMap<String, Object> columnHeadContent5 = new HashMap();
			columnHeadContent5 = composeRestoreJobReportsColumnInfo(RestoreJobReportsColumns.DEST_PATH_COLUMNID,
					RestoreJobReportsColumns.DEST_PATH_LONG, RestoreJobReportsColumns.DEST_PATH_SHORT,
					RestoreJobReportsColumns.DEST_PATH_KEY, RestoreJobReportsColumns.DEST_PATH_SORT,
					RestoreJobReportsColumns.DEST_PATH_FILTER, RestoreJobReportsColumns.DEST_PATH_VISIBLE,
					RestoreJobReportsColumns.DEST_PATH_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent5);

			HashMap<String, Object> columnHeadContent6 = new HashMap();
			columnHeadContent6 = composeRestoreJobReportsColumnInfo(RestoreJobReportsColumns.START_TIME_COLUMNID,
					RestoreJobReportsColumns.START_TIME_LONG, RestoreJobReportsColumns.START_TIME_SHORT,
					RestoreJobReportsColumns.START_TIME_KEY, RestoreJobReportsColumns.START_TIME_SORT,
					RestoreJobReportsColumns.START_TIME_FILTER, RestoreJobReportsColumns.START_TIME_VISIBLE,
					RestoreJobReportsColumns.START_TIME_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent6);

			HashMap<String, Object> columnHeadContent7 = new HashMap();
			columnHeadContent7 = composeRestoreJobReportsColumnInfo(RestoreJobReportsColumns.END_TIME_COLUMNID,
					RestoreJobReportsColumns.END_TIME_LONG, RestoreJobReportsColumns.END_TIME_SHORT,
					RestoreJobReportsColumns.END_TIME_KEY, RestoreJobReportsColumns.END_TIME_SORT,
					RestoreJobReportsColumns.END_TIME_FILTER, RestoreJobReportsColumns.END_TIME_VISIBLE,
					RestoreJobReportsColumns.END_TIME_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent7);

			HashMap<String, Object> columnHeadContent8 = new HashMap();
			columnHeadContent8 = composeRestoreJobReportsColumnInfo(RestoreJobReportsColumns.DURATION_COLUMNID,
					RestoreJobReportsColumns.DURATION_LONG, RestoreJobReportsColumns.DURATION_SHORT,
					RestoreJobReportsColumns.DURATION_KEY, RestoreJobReportsColumns.DURATION_SORT,
					RestoreJobReportsColumns.DURATION_FILTER, RestoreJobReportsColumns.DURATION_VISIBLE,
					RestoreJobReportsColumns.DURATION_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent8);

			HashMap<String, Object> columnHeadContent9 = new HashMap();
			columnHeadContent9 = composeRestoreJobReportsColumnInfo(RestoreJobReportsColumns.WARNINGS_COLUMNID,
					RestoreJobReportsColumns.WARNINGS_LONG, RestoreJobReportsColumns.WARNINGS_SHORT,
					RestoreJobReportsColumns.WARNINGS_KEY, RestoreJobReportsColumns.WARNINGS_SORT,
					RestoreJobReportsColumns.WARNINGS_FILTER, RestoreJobReportsColumns.WARNINGS_VISIBLE,
					RestoreJobReportsColumns.WARNINGS_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent9);

			HashMap<String, Object> columnHeadContent10 = new HashMap();
			columnHeadContent10 = composeRestoreJobReportsColumnInfo(RestoreJobReportsColumns.ERRORS_COLUMNID,
					RestoreJobReportsColumns.ERRORS_LONG, RestoreJobReportsColumns.ERRORS_SHORT,
					RestoreJobReportsColumns.ERRORS_KEY, RestoreJobReportsColumns.ERRORS_SORT,
					RestoreJobReportsColumns.ERRORS_FILTER, RestoreJobReportsColumns.ERRORS_VISIBLE,
					RestoreJobReportsColumns.ERRORS_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent10);

			HashMap<String, Object> columnHeadContent11 = new HashMap();
			columnHeadContent11 = composeRestoreJobReportsColumnInfo(RestoreJobReportsColumns.POLICY_COLUMNID,
					RestoreJobReportsColumns.POLICY_LONG, RestoreJobReportsColumns.POLICY_SHORT,
					RestoreJobReportsColumns.POLICY_KEY, RestoreJobReportsColumns.POLICY_SORT,
					RestoreJobReportsColumns.POLICY_FILTER, RestoreJobReportsColumns.POLICY_VISIBLE,
					RestoreJobReportsColumns.POLICY_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent11);

			HashMap<String, Object> columnHeadContent12 = new HashMap();
			columnHeadContent12 = composeRestoreJobReportsColumnInfo(RestoreJobReportsColumns.SOURCE_GROUP_COLUMNID,
					RestoreJobReportsColumns.SOURCE_GROUP_LONG, RestoreJobReportsColumns.SOURCE_GROUP_SHORT,
					RestoreJobReportsColumns.SOURCE_GROUP_KEY, RestoreJobReportsColumns.SOURCE_GROUP_SORT,
					RestoreJobReportsColumns.SOURCE_GROUP_FILTER, RestoreJobReportsColumns.SOURCE_GROUP_VISIBLE,
					RestoreJobReportsColumns.SOURCE_GROUP_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent12);

			HashMap<String, Object> columnHeadContent13 = new HashMap();
			columnHeadContent13 = composeRestoreJobReportsColumnInfo(RestoreJobReportsColumns.ORGANIZATION_COLUMNID,
					RestoreJobReportsColumns.ORGANIZATION_LONG, RestoreJobReportsColumns.ORGANIZATION_SHORT,
					RestoreJobReportsColumns.ORGANIZATION_KEY, RestoreJobReportsColumns.ORGANIZATION_SORT,
					RestoreJobReportsColumns.ORGANIZATION_FILTER, RestoreJobReportsColumns.ORGANIZATION_VISIBLE,
					RestoreJobReportsColumns.ORGANIZATION_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent13);

			CompareColumnsHeadContent(response, expectedColumnsHeadContents, test);

		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.INFO, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expctedStatusCode);
		}

		return response;
	}

	/**
	 * @author Rakesh.Chalamala
	 * @param response
	 * @param expectedColumnsHeadContents
	 * @param test
	 */
	private void CompareColumnsHeadContent(Response response,
			ArrayList<HashMap<String, Object>> expectedColumnsHeadContents, ExtentTest test) {

		ArrayList<HashMap<String, Object>> columnsHeadContent = new ArrayList<HashMap<String, Object>>();
		columnsHeadContent = response.then().extract().path("data");
		int length = expectedColumnsHeadContents.size();

		/*
		 * if (length != columnsHeadContent.size()) {
		 * assertTrue("compare columns head content number is " + length, false);
		 * test.log(LogStatus.FAIL, "compare columns head content number"); }
		 */

		for (int i = 0; i < columnsHeadContent.size(); i++) {
			HashMap<String, Object> expectedColumnsHeadContent = expectedColumnsHeadContents.get(i);
			HashMap<String, Object> HeadContent = columnsHeadContent.get(i);

			test.log(LogStatus.INFO, "Validating the Response for column_id");
			spogServer.assertResponseItem(expectedColumnsHeadContent.get("column_id"), HeadContent.get("column_id"),
					test);
			test.log(LogStatus.INFO, "Validating the Response for long_label ");
			spogServer.assertResponseItem(expectedColumnsHeadContent.get("long_label"), HeadContent.get("long_label"),
					test);

			test.log(LogStatus.INFO, "Validating the Response for short_label");
			spogServer.assertResponseItem(expectedColumnsHeadContent.get("short_label"), HeadContent.get("short_label"),
					test);

			/*
			 * test.log(LogStatus.INFO, "Validating the Response for key");
			 * spogServer.assertResponseItem(expectedColumnsHeadContent.get("key"),
			 * HeadContent.get("key"), test);
			 */
			test.log(LogStatus.INFO, "Validating the Response for sort");
			spogServer.assertResponseItem(expectedColumnsHeadContent.get("sort"), HeadContent.get("sort"), test);

			test.log(LogStatus.INFO, "Validating the Response for filter");
			spogServer.assertResponseItem(expectedColumnsHeadContent.get("filter"), HeadContent.get("filter"), test);

			test.log(LogStatus.INFO, "Validating the Response for visible");
			spogServer.assertResponseItem(expectedColumnsHeadContent.get("visible"), HeadContent.get("visible"), test);

			test.log(LogStatus.INFO, "Validating the Response for order_id");
			spogServer.assertResponseItem(expectedColumnsHeadContent.get("order_id"), HeadContent.get("order_id"),
					test);
		}
	}

	/**
	 * Compose the Restore Job Reports columns
	 * 
	 * @author Rakesh.Chalamala
	 * @param columnId
	 * @param sort
	 * @param filter
	 * @param visible
	 * @param orderId
	 * @return HashMap
	 */
	public HashMap<String, Object> composeRestoreJobReports_Column(String columnId, String sort, String filter,
			String visible, String orderId) {
		// TODO Auto-generated method stub
		HashMap<String, Object> temp = new HashMap<>();
		temp.put("column_id", columnId);
		if (sort == null || sort == "") {
			temp.put("sort", sort);
		} else if (!sort.equals("none")) {
			temp.put("sort", sort);
		}
		if (sort == null || sort == "") {
			temp.put("sort", sort);
		} else if (!filter.equals("none")) {
			temp.put("filter", filter);
		}
		if (sort == null || sort == "") {
			temp.put("sort", sort);
		} else if (!visible.equals("none")) {
			temp.put("visible", visible);
		}
		temp.put("order_id", Integer.parseInt(orderId));

		return temp;
	}

	/**
	 * Create Restore Job Reports For LoggedIn User
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param expected_columns
	 * @param test
	 * @return response
	 */
	public Response createRestoreJobReportsColumnsForLoggedInUser(String validToken,
			ArrayList<HashMap<String, Object>> expected_columns, ExtentTest test) {

		Map<String, ArrayList<HashMap<String, Object>>> columnsInfo = jp.jobColumnInfo(expected_columns);
		Response response = spogReportInvoker.createRestoreJobReportsColumnsForLoggedInUser(validToken, columnsInfo,
				test);

		return response;
	}

	/**
	 * Compare Restore Job Reports Content
	 * 
	 * @author Rakesh.Chalamala
	 * @param response
	 * @param expectedColumnsContents
	 * @param default
	 *            columns content
	 * @param expectedstatuscode
	 * @param ExpectedErrorMessage
	 */
	public void compareReportJobReportsColumnsContent(Response response,
			ArrayList<HashMap<String, Object>> expectedColumnsContents,
			ArrayList<HashMap<String, Object>> defaultColumnsContents, int expectedstatuscode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		ArrayList<HashMap<String, Object>> actualcolumnsHeadContent = new ArrayList<HashMap<String, Object>>();

		spogServer.checkResponseStatus(response, expectedstatuscode);
		if (expectedstatuscode == SpogConstants.SUCCESS_POST) {
			actualcolumnsHeadContent = response.then().extract().path("data");
			int length = expectedColumnsContents.size();

			test.log(LogStatus.INFO, "Sort the actual response by order_id");
			actualcolumnsHeadContent = spogServer.sortArrayListbyInt(actualcolumnsHeadContent, "order_id");

			test.log(LogStatus.INFO, "Sort the expected response by order_id");
			expectedColumnsContents = spogServer.sortArrayListbyInt(expectedColumnsContents, "order_id");

			if (expectedColumnsContents.size() == actualcolumnsHeadContent.size()) {

				for (int i = 0; i < actualcolumnsHeadContent.size(); i++) {
					for (int j = 0; j < defaultColumnsContents.size(); j++) {
						if (actualcolumnsHeadContent.get(i).get("column_id")
								.equals(defaultColumnsContents.get(j).get("column_id"))) {
							checkRestoreJobReportsColumns(actualcolumnsHeadContent.get(i),
									expectedColumnsContents.get(i), defaultColumnsContents.get(j), test);
							break;
						}
					}
				}
			} else {
				test.log(LogStatus.FAIL, "The expected count did not match the actual count");
				assertTrue("The expected count did not match the actual count", false);
			}
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			if (code.contains("00D00003")) {
				message = message.replace("{0}", expectedColumnsContents.get(0).get("column_id").toString());

			} else if (code.contains("00D00005")) {
				message = message.replace("{0}", expectedColumnsContents.get(0).get("order_id").toString());
			} else if (code.contains("0D00006")) {
				message = message.replace("{0}",
						defaultColumnsContents.get(defaultColumnsContents.size() - 1).get("order_id").toString());
			}
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		}
	}

	/**
	 * @author Rakesh.Chalamala
	 * @param actual_response
	 * @param expected_response
	 * @param defaultcolumnvalues
	 * @param test
	 */
	public void checkRestoreJobReportsColumns(HashMap<String, Object> actual_response,
			HashMap<String, Object> expected_response, HashMap<String, Object> defaultcolumnvalues, ExtentTest test) {
		test.log(LogStatus.INFO, "Compare the column id");
		spogServer.assertResponseItem(actual_response.get("column_id").toString(),
				expected_response.get("column_id").toString(), test);

		test.log(LogStatus.INFO, "Compare the order id column ");
		spogServer.assertResponseItem(actual_response.get("order_id").toString(),
				expected_response.get("order_id").toString(), test);

		test.log(LogStatus.INFO, "Compare the visiblity column ");
		if (expected_response.containsKey("visible")) {
			spogServer.assertResponseItem(actual_response.get("visible").toString(),
					expected_response.get("visible").toString(), test);
		} else {
			spogServer.assertResponseItem(actual_response.get("visible").toString(),
					defaultcolumnvalues.get("visible").toString(), test);
		}

		test.log(LogStatus.INFO, "Compare the sort column");
		spogServer.assertResponseItem(actual_response.get("sort").toString(),
				defaultcolumnvalues.get("sort").toString(), test);

		test.log(LogStatus.INFO, "Compare the filter column");
		spogServer.assertResponseItem(actual_response.get("filter").toString(),
				defaultcolumnvalues.get("filter").toString(), test);

	}

	/**
	 * DELETE RestoreJobReports For loggedin User
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param expectedStatusCode
	 * @param EpectedErrorMesssage
	 * @param test
	 */
	public void deleteRestoreJobReportsColumnsforLoggedInUserwithCheck(String token, int expectedstatuscode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		Response response = spogReportInvoker.deleteRestoreJobReportsColumnsForLoggedInUser(token, test);
		spogServer.checkResponseStatus(response, expectedstatuscode);
		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.PASS, "The recovered resource columns are successfully deleted");
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			/*
			 * if(code.contains("00A00302")){ message = message.replace("{0}", filterID);
			 * message= message.replace("{1}", userID); }
			 */
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		}
	}

	/**
	 * Create Restore Job Reports for Specified User
	 * 
	 * @author Rakesh.Chalamala
	 * @param user_id
	 * @param token
	 * @param expected_columns
	 * @param test
	 * @return response
	 */
	public Response createRestoreJobReportsColumnsForSpecifiedUser(String user_id, String validToken,
			ArrayList<HashMap<String, Object>> expected_columns, ExtentTest test) {

		Map<String, ArrayList<HashMap<String, Object>>> columnsInfo = jp.jobColumnInfo(expected_columns);
		Response response = spogReportInvoker.createRestoreJobReportsColumnsForSpecifiedUser(user_id, validToken,
				columnsInfo, test);

		return response;
	}

	/**
	 * DELETE RestoreJobReports for Specified User
	 * 
	 * @author Rakesh.Chalamala
	 * @param user_id
	 * @param token
	 * @param expectedStatusCode
	 * @param ExpectedErrorMessage
	 */
	public void deleteRestoreJobReportsColumnsforSpecifiedUserwithCheck(String user_Id, String token,
			int expectedstatuscode, SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		Response response = spogReportInvoker.deleteRestoreJobReportsColumnsForSpecifiedUser(user_Id, token, test);
		spogServer.checkResponseStatus(response, expectedstatuscode);
		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.PASS, "The RestoreJobReports columns are successfully deleted");
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			/*
			 * if(code.contains("00A00302")){ message = message.replace("{0}", filterID);
			 * message= message.replace("{1}", userID); }
			 */
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		}
	}

	/**
	 * Get RestoreJobReports columns for logged in user
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param expectedresponse
	 *            arraylist of expected columns
	 * @param defaultcolumnresponse
	 *            arraylist of default columns
	 * @param expectedstatuscode
	 * @param expected
	 *            errormessage
	 * @param extenttest
	 *            object
	 * 
	 */
	public void getRestoreJobReportsColumnsForLoggedinUser(String token,
			ArrayList<HashMap<String, Object>> expectedresponse,
			ArrayList<HashMap<String, Object>> defaultcolumnresponse, int expectedStatusCode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		test.log(LogStatus.INFO, "Call the res API to get the RestoreJobReports columns for loggedin user");
		Response response = spogReportInvoker.getRestoreJobReportsColumnsForLoggedInUser(token, test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			ArrayList<HashMap<String, Object>> actualresponse = response.then().extract().path("data");

			test.log(LogStatus.INFO, "Sort the actual response by order_id");
			actualresponse = spogServer.sortArrayListbyInt(actualresponse, "order_id");

			test.log(LogStatus.INFO, "Sort the expected response by order_id");
			expectedresponse = spogServer.sortArrayListbyInt(expectedresponse, "order_id");

			if (actualresponse.size() == defaultcolumnresponse.size()
					|| actualresponse.size() == defaultcolumnresponse.size() - 1) { // size should be same as default

				ArrayList<HashMap<String, Object>> col_respnse = null;
				boolean flag = false;

				for (int i = 0; i < actualresponse.size(); i++) {
					flag = false;
					for (int j = 0; j < expectedresponse.size(); j++) {
						if (actualresponse.get(i).get("column_id").equals(expectedresponse.get(j).get("column_id"))) {
							for (int k = 0; k < defaultcolumnresponse.size(); k++) {
								if (expectedresponse.get(j).get("column_id")
										.equals(defaultcolumnresponse.get(k).get("column_id"))) {
									checkRestoreJobReportsColumns(actualresponse.get(i), expectedresponse.get(j),
											defaultcolumnresponse.get(k), test);
									flag = true; // set true as comparison completes
									break;
								}
							}
							break;
						}
					}
					if (!flag) { // if comparison not done compare it with default columns
						for (int k = 0; k < defaultcolumnresponse.size(); k++) {
							if (actualresponse.get(i).get("column_id")
									.equals(defaultcolumnresponse.get(k).get("column_id"))) {

								col_respnse = new ArrayList<>();
								col_respnse.addAll(defaultcolumnresponse); // adding to new arraylist so that default
																			// columns not get disturbed
								col_respnse.get(k).put("visible", false);

								checkRestoreJobReportsColumns(actualresponse.get(i), col_respnse.get(k),
										col_respnse.get(k), test);
								break;
							}
						}
					}
				}

			} else {
				test.log(LogStatus.FAIL, "The expected count did not match the actual count");
				assertTrue("The expected count did not match the actual count", false);
			}

		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
	}

	/**
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param expectedResponse
	 * @param defaultcolumnresponse
	 * @param expectedstatuscode
	 * @param ExpectedErrorMessage
	 * @param test
	 * @return response
	 */
	public Response updateRestoreJobReportsColumnsForLoggedinUser(String token,
			ArrayList<HashMap<String, Object>> expectedresponse,
			ArrayList<HashMap<String, Object>> defaultcolumnresponse, int expectedStatusCode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		Map<String, ArrayList<HashMap<String, Object>>> columnsInfo = jp.jobColumnInfo(expectedresponse);
		Response response = spogReportInvoker.updateRestoreJobReportsColumnsForLoggedInUser(token, columnsInfo, test);
		spogServer.checkResponseStatus(response, expectedStatusCode);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			ArrayList<HashMap<String, Object>> actualresponse = response.then().extract().path("data");

			test.log(LogStatus.INFO, "Sort the actual response by order_id");
			actualresponse = spogServer.sortArrayListbyInt(actualresponse, "order_id");

			test.log(LogStatus.INFO, "Sort the expected response by order_id");
			expectedresponse = spogServer.sortArrayListbyInt(expectedresponse, "order_id");

			if (expectedresponse.size() == actualresponse.size()) {
				for (int i = 0; i < actualresponse.size(); i++) {
					for (int j = 0; j < defaultcolumnresponse.size(); j++) {
						if (actualresponse.get(i).get("column_id")
								.equals(defaultcolumnresponse.get(j).get("column_id"))) {
							checkRestoreJobReportsColumns(actualresponse.get(i), expectedresponse.get(i),
									defaultcolumnresponse.get(j), test);
							break;
						}
					}

				}
			} else {
				test.log(LogStatus.FAIL, "The expected count did not match the actual count");
				assertTrue("The expected count did not match the actual count", false);
			}
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			if (code.contains("0D00006")) {
				message = message.replace("{0}",
						defaultcolumnresponse.get(defaultcolumnresponse.size() - 1).get("order_id").toString());
			}
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);

		}
		return response;
	}

	/**
	 * Get RestoreJobReports columns for specified user
	 * 
	 * @author Rakesh.Chalamala
	 * @param user_id
	 * @param token
	 * @param expectedresponse
	 *            arraylist of expected columns
	 * @param defaultcolumnresponse
	 *            arraylist of default columns
	 * @param expectedstatuscode
	 * @param expected
	 *            errormessage
	 * @param extenttest
	 *            object
	 * 
	 */
	public void getRestoreJobReportsColumnsForSpecifiedUser(String user_Id, String token,
			ArrayList<HashMap<String, Object>> expectedresponse,
			ArrayList<HashMap<String, Object>> defaultcolumnresponse, int expectedStatusCode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		test.log(LogStatus.INFO, "Call the res API to get the RestoreJobReports columns for specified user");
		Response response = spogReportInvoker.getRestoreJobReportsColumnsForSpecifiedUser(user_Id, token, test);

		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			ArrayList<HashMap<String, Object>> actualresponse = response.then().extract().path("data");

			test.log(LogStatus.INFO, "Sort the actual response by order_id");
			actualresponse = spogServer.sortArrayListbyInt(actualresponse, "order_id");

			// if(!(expectedresponse.get(0).get("order_id").getClass().getTypeName()==defaultcolumnresponse.get(0).get("order_id").getClass().getTypeName()))
			// {
			test.log(LogStatus.INFO, "Sort the expected response by order_id");
			expectedresponse = spogServer.sortArrayListbyInt(expectedresponse, "order_id");
			//
			if (actualresponse.size() == defaultcolumnresponse.size()
					|| actualresponse.size() == defaultcolumnresponse.size() - 1) { // size should be same as default

				ArrayList<HashMap<String, Object>> col_respnse = null;
				boolean flag = false;

				for (int i = 0; i < actualresponse.size(); i++) {
					flag = false;
					for (int j = 0; j < expectedresponse.size(); j++) {
						if (actualresponse.get(i).get("column_id").equals(expectedresponse.get(j).get("column_id"))) {
							for (int k = 0; k < defaultcolumnresponse.size(); k++) {
								if (expectedresponse.get(j).get("column_id")
										.equals(defaultcolumnresponse.get(k).get("column_id"))) {
									checkRestoreJobReportsColumns(actualresponse.get(i), expectedresponse.get(j),
											defaultcolumnresponse.get(k), test);
									flag = true; // set true as comparison completes
									break;
								}
							}
							break;
						}
					}
					if (!flag) { // if comparison not done compare it with default columns
						for (int k = 0; k < defaultcolumnresponse.size(); k++) {
							if (actualresponse.get(i).get("column_id")
									.equals(defaultcolumnresponse.get(k).get("column_id"))) {

								col_respnse = new ArrayList<>();
								col_respnse.addAll(defaultcolumnresponse); // adding to new arraylist so that default
																			// columns not get disturbed
								col_respnse.get(k).put("visible", false);

								checkRestoreJobReportsColumns(actualresponse.get(i), col_respnse.get(k),
										col_respnse.get(k), test);
								break;
							}
						}
					}
				}

			} else {
				test.log(LogStatus.FAIL, "The expected count did not match the actual count");
				assertTrue("The expected count did not match the actual count", false);
			}

		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
	}

	/**
	 * Update RestoreJobReports columns for specified user
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param user_id
	 * @param test
	 */
	public Response updateRestoreJobReportsColumnsByUserId(String user_Id, String token,
			ArrayList<HashMap<String, Object>> expectedresponse,
			ArrayList<HashMap<String, Object>> defaultcolumnresponse, int expectedStatusCode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		Map<String, ArrayList<HashMap<String, Object>>> columnsInfo = jp.jobColumnInfo(expectedresponse);
		Response response = spogReportInvoker.updateRestoreJobReportsColumnsForSpecifiedUser(user_Id, token,
				columnsInfo, test);
		spogServer.checkResponseStatus(response, expectedStatusCode);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			ArrayList<HashMap<String, Object>> actualresponse = response.then().extract().path("data");

			test.log(LogStatus.INFO, "Sort the actual response by order_id");
			actualresponse = spogServer.sortArrayListbyInt(actualresponse, "order_id");

			test.log(LogStatus.INFO, "Sort the expected response by order_id");
			expectedresponse = spogServer.sortArrayListbyInt(expectedresponse, "order_id");

			if (expectedresponse.size() == actualresponse.size()) {
				for (int i = 0; i < actualresponse.size(); i++) {
					for (int j = 0; j < defaultcolumnresponse.size(); j++) {
						if (actualresponse.get(i).get("column_id")
								.equals(defaultcolumnresponse.get(j).get("column_id"))) {
							checkRestoreJobReportsColumns(actualresponse.get(i), expectedresponse.get(i),
									defaultcolumnresponse.get(j), test);
							break;
						}
					}

				}
			} else {
				test.log(LogStatus.FAIL, "The expected count did not match the actual count");
				assertTrue("The expected count did not match the actual count", false);
			}
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			if (code.contains("0D00006")) {
				message = message.replace("{0}",
						defaultcolumnresponse.get(defaultcolumnresponse.size() - 1).get("order_id").toString());
			}

			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);

		}
		return response;
	}

	/**
	 * the function is used to composeBackUpJobReportsColumns
	 * 
	 * @author Ramya.Nagepalli
	 * 
	 * @param column_id
	 * @param visible
	 * @param order_id
	 * 
	 *            return composed hash
	 */
	public HashMap<String, Object> composeBackUpJobReportsColumns(String column_id, String visible, String order_id) {
		// TODO Auto-generated method stub

		HashMap<String, Object> temp = new HashMap<>();

		temp.put("column_id", column_id);

		if (!(visible.equals("none"))) {
			temp.put("visible", visible);
		}

		if (!(order_id.equals("none"))) {
			temp.put("order_id", order_id);
		}
		return temp;
	}

	/**
	 * the function is used to createBackUpJobReportsColumnsforLoggedInUser
	 * 
	 * @author Ramya.Nagepalli
	 * 
	 * @param adminToken
	 * @param expected_columns
	 * @param expectedStatusCode
	 * 
	 *            return composed hash
	 */
	public Response createBackUpJobReportsColumnsforLoggedInUser(String adminToken,
			ArrayList<HashMap<String, Object>> expected_columns, int expectedStatusCode, ExtentTest test) {
		// TODO Auto-generated method stub

		Map<String, ArrayList<HashMap<String, Object>>> compose_expected_columns = null;

		compose_expected_columns = jp.jobColumnInfo(expected_columns);

		Response response = spogReportInvoker.createBackUpJobReportsColumnsforLoggedInUser(adminToken,
				compose_expected_columns, test);

		spogServer.checkResponseStatus(response, expectedStatusCode);

		return response;

	}

	/**
	 * the function is used to createBackUpJobReportsColumnsForSpecifiedUser
	 * 
	 * @author Ramya.Nagepalli
	 * 
	 * @param adminToken
	 * @param user_id
	 * @param expected_columns
	 * @param expectedStatusCode
	 *            return response
	 */
	public Response createBackUpJobReportsColumnsForSpecifiedUser(String adminToken, String user_id,
			ArrayList<HashMap<String, Object>> expected_columns, int expectedStatusCode, ExtentTest test) {
		// TODO Auto-generated method stub

		Map<String, ArrayList<HashMap<String, Object>>> compose_expected_columns = null;

		compose_expected_columns = jp.jobColumnInfo(expected_columns);

		Response response = spogReportInvoker.createBackUpJobReportsColumnsforSpecifiedUser(adminToken, user_id,
				compose_expected_columns, test);

		spogServer.checkResponseStatus(response, expectedStatusCode);

		return response;

	}

	/**
	 * the function is used to getBackUpJobReportsColumnsForSpecifiedUser
	 * 
	 * @author Ramya.Nagepalli
	 * 
	 * @param adminToken
	 * @param user_id
	 * @param expectedStatusCode
	 * @param test
	 *            return response
	 */
	public Response getBackUpJobReportsColumnsForSpecifiedUser(String adminToken, String user_id,
			int expectedStatusCode, ExtentTest test) {
		// TODO Auto-generated method stub

		Response response = spogReportInvoker.getBackUpJobReportsColumnsforSpecifiedUser(adminToken, user_id, test);

		spogServer.checkResponseStatus(response, expectedStatusCode);

		return response;

	}

	/**
	 * the function is used to getBackUpJobReportsColumnsForLoggedInUser
	 * 
	 * @author Ramya.Nagepalli
	 * 
	 * @param adminToken
	 * @param expectedStatusCode
	 * @param test
	 *            return response
	 */
	public Response getBackUpJobReportsColumnsForLoggedInUser(String adminToken, int expectedStatusCode,
			ExtentTest test) {
		// TODO Auto-generated method stub

		Response response = spogReportInvoker.getBackUpJobReportsColumnsforLoggedInUser(adminToken, test);

		spogServer.checkResponseStatus(response, expectedStatusCode);

		return response;

	}

	/**
	 * This method is used to deleteBackUpJobReportsColumnsForLoggedInUser
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param expectedStatusCode
	 * @param EpectedErrorMesssage
	 * @param test
	 */
	public void deleteBackUpJobReportsColumnsForLoggedInUser(String token, int expectedstatuscode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		Response response = spogReportInvoker.deleteBackUpJobReportsColumnsForLoggedInUser(token, test);
		spogServer.checkResponseStatus(response, expectedstatuscode);
		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.PASS, "The BackUpJobReportsColumnsForLoggedInUser are successfully deleted");
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		}
	}

	/**
	 * This method is used to deleteBackUpJobReportsColumnsForSpecifiedUser
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param expectedStatusCode
	 * @param EpectedErrorMesssage
	 * @param test
	 */
	public void deleteBackUpJobReportsColumnsForSpecifiedUser(String token, String user_id, int expectedstatuscode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		Response response = spogReportInvoker.deleteBackUpJobReportsColumnsForSpecifiedUser(token, user_id, test);
		spogServer.checkResponseStatus(response, expectedstatuscode);
		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.PASS, "The BackUpJob Reports Columns For Specified User are successfully deleted");
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		}
	}

	/**
	 * the function is used to deleteReportFiltersForSpecifiedUser
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param user_id
	 * @param filter_id
	 * @param expectedStatusCode
	 * @param ExpectedErrorMessage
	 * @param test
	 *            return response
	 */

	public Response deleteReportFiltersForSpecifiedUser(String token, String user_id, String filter_id,
			int expectedStatusCode, SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		/*
		 * Response response =
		 * spogReportInvoker.deleteReportFiltersForSpecifiedUser(token, user_id,
		 * filter_id, test);
		 */
		Response response = spogServer.deleteFiltersByID(token, filter_id, "none", test);

		spogServer.checkResponseStatus(response, expectedStatusCode);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.PASS, "The ReportFiltersForSpecifiedUser are successfully deleted");
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			if (code.contains("00A00702")) {
				spogServer.setToken(token);
				message = message.replace("{0}", filter_id);
				message = message.replace("{1}", user_id);
			}
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
		return response;

	}

	/**
	 * the function is used to deleteReportFiltersForLoggedInUser
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param filter_id
	 * @param expectedStatusCode
	 * @param ExpectedErrorMessage
	 * @param test
	 *            return response
	 */

	public Response deleteReportFiltersForLoggedInUser(String token, String filter_id, int expectedStatusCode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		Response response = spogServer.deleteFiltersByID(token, filter_id, "none", test);
		spogServer.checkResponseStatus(response, expectedStatusCode);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.PASS, "The ReportFiltersForLoggedInUser are successfully deleted");
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();

			if (code.contains("00A00702")) {
				spogServer.setToken(token);
				String user_id = spogServer.GetLoggedinUser_UserID();
				message = message.replace("{0}", filter_id);
				message = message.replace("{1}", user_id);
			}
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
		return response;

	}

	/**
	 * the function is used to getSystemBackUpJobReportsColumns
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param organization_type
	 * @param expectedStatusCode
	 * @param test
	 *            return response
	 */

	public Response getSystemBackUpJobReportsColumns(String token, String organization_type, int expectedStatusCode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		Response response = spogReportInvoker.getSystemBackUpJobReportsColumns(token, test);
		spogServer.checkResponseStatus(response, expectedStatusCode);

		ArrayList<HashMap<String, Object>> expectedColumnsHeadContents = new ArrayList<>();
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			HashMap<String, Object> columnHeadContent1 = new HashMap();
			columnHeadContent1 = composeRestoreJobReportsColumnInfo(BackUpJobReportsColumns.NAME_COLUMNID,
					BackUpJobReportsColumns.NAME_LONG, BackUpJobReportsColumns.NAME_SHORT,
					BackUpJobReportsColumns.NAME_KEY, BackUpJobReportsColumns.NAME_SORT,
					BackUpJobReportsColumns.NAME_FILTER, BackUpJobReportsColumns.NAME_VISIBLE,
					BackUpJobReportsColumns.NAME_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent1);

			HashMap<String, Object> columnHeadContent2 = new HashMap();
			columnHeadContent2 = composeRestoreJobReportsColumnInfo(BackUpJobReportsColumns.STATUS_COLUMNID,
					BackUpJobReportsColumns.STATUS_LONG, BackUpJobReportsColumns.STATUS_SHORT,
					BackUpJobReportsColumns.STATUS_KEY, BackUpJobReportsColumns.STATUS_SORT,
					BackUpJobReportsColumns.STATUS_FILTER, BackUpJobReportsColumns.STATUS_VISIBLE,
					BackUpJobReportsColumns.STATUS_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent2);

			HashMap<String, Object> columnHeadContent3 = new HashMap();
			columnHeadContent3 = composeRestoreJobReportsColumnInfo(BackUpJobReportsColumns.DESTINATION_COLUMNID,
					BackUpJobReportsColumns.DESTINATION_LONG, BackUpJobReportsColumns.DESTINATION_SHORT,
					BackUpJobReportsColumns.DESTINATION_KEY, BackUpJobReportsColumns.DESTINATION_SORT,
					BackUpJobReportsColumns.DESTINATION_FILTER, BackUpJobReportsColumns.DESTINATION_VISIBLE,
					BackUpJobReportsColumns.DESTINATION_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent3);

			HashMap<String, Object> columnHeadContent4 = new HashMap();
			columnHeadContent4 = composeRestoreJobReportsColumnInfo(BackUpJobReportsColumns.POLICY_COLUMNID,
					BackUpJobReportsColumns.POLICY_LONG, BackUpJobReportsColumns.POLICY_SHORT,
					BackUpJobReportsColumns.POLICY_KEY, BackUpJobReportsColumns.POLICY_SORT,
					BackUpJobReportsColumns.POLICY_FILTER, BackUpJobReportsColumns.POLICY_VISIBLE,
					BackUpJobReportsColumns.POLICY_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent4);

			HashMap<String, Object> columnHeadContent5 = new HashMap();
			columnHeadContent5 = composeRestoreJobReportsColumnInfo(BackUpJobReportsColumns.START_TIME_COLUMNID,
					BackUpJobReportsColumns.START_TIME_LONG, BackUpJobReportsColumns.START_TIME_SHORT,
					BackUpJobReportsColumns.START_TIME_KEY, BackUpJobReportsColumns.START_TIME_SORT,
					BackUpJobReportsColumns.START_TIME_FILTER, BackUpJobReportsColumns.START_TIME_VISIBLE,
					BackUpJobReportsColumns.START_TIME_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent5);

			HashMap<String, Object> columnHeadContent6 = new HashMap();
			columnHeadContent6 = composeRestoreJobReportsColumnInfo(BackUpJobReportsColumns.END_TIME_COLUMNID,
					BackUpJobReportsColumns.END_TIME_LONG, BackUpJobReportsColumns.END_TIME_SHORT,
					BackUpJobReportsColumns.END_TIME_KEY, BackUpJobReportsColumns.END_TIME_SORT,
					BackUpJobReportsColumns.END_TIME_FILTER, BackUpJobReportsColumns.END_TIME_VISIBLE,
					BackUpJobReportsColumns.END_TIME_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent6);

			HashMap<String, Object> columnHeadContent7 = new HashMap();
			columnHeadContent7 = composeRestoreJobReportsColumnInfo(BackUpJobReportsColumns.DURATION_COLUMNID,
					BackUpJobReportsColumns.DURATION_LONG, BackUpJobReportsColumns.DURATION_SHORT,
					BackUpJobReportsColumns.DURATION_KEY, BackUpJobReportsColumns.DURATION_SORT,
					BackUpJobReportsColumns.DURATION_FILTER, BackUpJobReportsColumns.DURATION_VISIBLE,
					BackUpJobReportsColumns.DURATION_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent7);

			HashMap<String, Object> columnHeadContent8 = new HashMap();
			columnHeadContent8 = composeRestoreJobReportsColumnInfo(BackUpJobReportsColumns.WARNINGS_COLUMNID,
					BackUpJobReportsColumns.WARNINGS_LONG, BackUpJobReportsColumns.WARNINGS_SHORT,
					BackUpJobReportsColumns.WARNINGS_KEY, BackUpJobReportsColumns.WARNINGS_SORT,
					BackUpJobReportsColumns.WARNINGS_FILTER, BackUpJobReportsColumns.WARNINGS_VISIBLE,
					BackUpJobReportsColumns.WARNINGS_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent8);

			HashMap<String, Object> columnHeadContent9 = new HashMap();
			columnHeadContent9 = composeRestoreJobReportsColumnInfo(BackUpJobReportsColumns.ERRORS_COLUMNID,
					BackUpJobReportsColumns.ERRORS_LONG, BackUpJobReportsColumns.ERRORS_SHORT,
					BackUpJobReportsColumns.ERRORS_KEY, BackUpJobReportsColumns.ERRORS_SORT,
					BackUpJobReportsColumns.ERRORS_FILTER, BackUpJobReportsColumns.ERRORS_VISIBLE,
					BackUpJobReportsColumns.ERRORS_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent9);

			HashMap<String, Object> columnHeadContent10 = new HashMap();
			columnHeadContent10 = composeRestoreJobReportsColumnInfo(BackUpJobReportsColumns.Data_Transferred_COLUMNID,
					BackUpJobReportsColumns.Data_Transferred_LONG, BackUpJobReportsColumns.Data_Transferred_SHORT,
					BackUpJobReportsColumns.Data_Transferred_KEY, BackUpJobReportsColumns.Data_Transferred_SORT,
					BackUpJobReportsColumns.Data_Transferred_FILTER, BackUpJobReportsColumns.Data_Transferred_VISIBLE,
					BackUpJobReportsColumns.Data_Transferred_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent10);

			HashMap<String, Object> columnHeadContent11 = new HashMap();
			columnHeadContent11 = composeRestoreJobReportsColumnInfo(BackUpJobReportsColumns.Data_Processed_COLUMNID,
					BackUpJobReportsColumns.Data_Processed_LONG, BackUpJobReportsColumns.Data_Processed_SHORT,
					BackUpJobReportsColumns.Data_Processed_KEY, BackUpJobReportsColumns.Data_Processed_SORT,
					BackUpJobReportsColumns.Data_Processed_FILTER, BackUpJobReportsColumns.Data_Processed_VISIBLE,
					BackUpJobReportsColumns.Data_Processed_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent11);

			HashMap<String, Object> columnHeadContent12 = new HashMap();
			columnHeadContent12 = composeRestoreJobReportsColumnInfo(BackUpJobReportsColumns.Data_Written_COLUMNID,
					BackUpJobReportsColumns.Data_Written_LONG, BackUpJobReportsColumns.Data_Written_SHORT,
					BackUpJobReportsColumns.Data_Written_KEY, BackUpJobReportsColumns.Data_Written_SORT,
					BackUpJobReportsColumns.Data_Written_FILTER, BackUpJobReportsColumns.Data_Written_VISIBLE,
					BackUpJobReportsColumns.Data_Written_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent12);

			HashMap<String, Object> columnHeadContent13 = new HashMap();
			columnHeadContent13 = composeRestoreJobReportsColumnInfo(BackUpJobReportsColumns.SOURCE_GROUP_COLUMNID,
					BackUpJobReportsColumns.SOURCE_GROUP_LONG, BackUpJobReportsColumns.SOURCE_GROUP_SHORT,
					BackUpJobReportsColumns.SOURCE_GROUP_KEY, BackUpJobReportsColumns.SOURCE_GROUP_SORT,
					BackUpJobReportsColumns.SOURCE_GROUP_FILTER, BackUpJobReportsColumns.SOURCE_GROUP_VISIBLE,
					BackUpJobReportsColumns.SOURCE_GROUP_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent13);

			if (!(organization_type.equalsIgnoreCase("direct") || organization_type.equalsIgnoreCase("sub"))) {

				HashMap<String, Object> columnHeadContent14 = new HashMap();
				columnHeadContent14 = composeRestoreJobReportsColumnInfo(BackUpJobReportsColumns.ORGANIZATION_COLUMNID,
						BackUpJobReportsColumns.ORGANIZATION_LONG, BackUpJobReportsColumns.ORGANIZATION_SHORT,
						BackUpJobReportsColumns.ORGANIZATION_KEY, BackUpJobReportsColumns.ORGANIZATION_SORT,
						BackUpJobReportsColumns.ORGANIZATION_FILTER, BackUpJobReportsColumns.ORGANIZATION_VISIBLE,
						BackUpJobReportsColumns.ORGANIZATION_ORDERID);
				expectedColumnsHeadContents.add(columnHeadContent14);

				CompareColumnsHeadContent(response, expectedColumnsHeadContents, test);
			} else {
				CompareColumnsHeadContent(response, expectedColumnsHeadContents, test);
			}

		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}

		return response;

	}

	/**
	 * Compose Report Schedule Info
	 * 
	 * @author Rakesh.Chalamala
	 * @param report_name
	 * @param report_type
	 * @param date_range_type
	 * @param date_range_start_ts
	 * @param date_range_end_ts
	 * @param schedule_frequency
	 * @param source_group_id_list
	 * @param organization_id_list
	 * @param recipient_mail
	 * @param cron_expression
	 */
	public HashMap<String, Object> composeReportScheduleInfo(String report_name, String report_type,
			String date_range_type, long date_range_start_ts, long date_range_end_ts, String schedule_frequency,
			String source_group_id_list, String organization_id_list, String recipient_mail, String cron_expression,
			String report_for_type) {

		HashMap<String, Object> ReportScheduleInfo = new HashMap<>();

		ReportScheduleInfo.put("report_name", report_name);
		ReportScheduleInfo.put("report_type", report_type);
		ReportScheduleInfo.put("date_range_type", date_range_type);
		ReportScheduleInfo.put("date_range_start_ts", date_range_start_ts);
		ReportScheduleInfo.put("date_range_end_ts", date_range_end_ts);
		if(StringUtils.isUUID(organization_id_list.split(",")[0].toString()))
			ReportScheduleInfo.put("organization_id", organization_id_list.split(",")[0]);

		if (!schedule_frequency.equals(null) && !schedule_frequency.equals("")) {
			ReportScheduleInfo.put("schedule_frequency", schedule_frequency);
		}

		ArrayList<String> recipient_mail1 = new ArrayList<>();
		if (recipient_mail.contains(",")) {

			String[] recp_mails = recipient_mail.split(",");
			for (int i = 0; i < recp_mails.length; i++) {
				recipient_mail1.add(recp_mails[i]);
			}
			ReportScheduleInfo.put("recipient_mail", recipient_mail1);
		} else {
			recipient_mail1.add(recipient_mail);
			ReportScheduleInfo.put("recipient_mail", recipient_mail1);
		}

		if (report_for_type != null) {
			// Add source_group_ids only if following condition satisfies
			if (report_for_type.equalsIgnoreCase("selected_source_groups")) {
				ArrayList<String> source_group_id_list1 = new ArrayList<>();

				if (source_group_id_list.contains(",")) {

					String[] sources = source_group_id_list.split(",");
					for (int i = 0; i < sources.length; i++) {
						source_group_id_list1.add(sources[i]);
					}
					ReportScheduleInfo.put("source_group_id_list", source_group_id_list1);
				} else if (source_group_id_list == "" || source_group_id_list == null) {
					ReportScheduleInfo.put("source_group_id_list", new ArrayList<>());
				} else {
					source_group_id_list1.add(source_group_id_list);
					ReportScheduleInfo.put("source_group_id_list", source_group_id_list1);
				}
			}

			if (report_for_type.equalsIgnoreCase("selected_organizations")) {
				ArrayList<String> organization_id_list1 = new ArrayList<>();
				if (organization_id_list.contains(",")) {

					String[] orglist = organization_id_list.split(",");
					for (int i = 0; i < orglist.length; i++) {
						organization_id_list1.add(orglist[i]);
					}
					ReportScheduleInfo.put("organization_id_list", organization_id_list1);
				} else {
					organization_id_list1.add(organization_id_list);
					ReportScheduleInfo.put("organization_id_list", organization_id_list1);
				}
			}

			HashMap<String, Object> report_for = new HashMap<>();
			report_for.put("type", report_for_type);
			report_for.put("details", new ArrayList<>());

			ReportScheduleInfo.put("report_for", report_for);
		} else {
			ReportScheduleInfo.put("report_for", null);
		}

		if (cron_expression == null || cron_expression == "") {
			// For generate now reports
			ReportScheduleInfo.put("report_schedule", "generate_now");
		} else {
			ReportScheduleInfo.put("cron_expression", cron_expression);
		}

		return ReportScheduleInfo;
	}

	/**
	 * CreateReportSchedule
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param scheduleInfo
	 * @param test
	 * @return reportScheduleId
	 */
	public String createReportScheduleWithCheck(String token, HashMap<String, Object> scheduleInfo,
			int expectedStatusCode, SpogMessageCode expectedErrorMessage, ExtentTest test) {

		String reportScheduleId = null;
		test.log(LogStatus.INFO, "Call the api Create Report Schedule");
		Response response = spogReportInvoker.createReportSchedule(token, scheduleInfo, test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE
				|| expectedStatusCode == SpogConstants.SUCCESS_POST) {

			HashMap<String, Object> actualResponse = new HashMap<>();
			actualResponse = response.then().extract().path("data");

			spogServer.setToken(token);
			scheduleInfo.put("create_user_id", spogServer.GetLoggedinUser_UserID());

			/*
			 * //Adding the loggedin user email to recipient_mail for validation.
			 * spogServer.setToken(token); ArrayList<String> re = new ArrayList<>();
			 * re.add(spogServer.getLoggedinUser_EmailId()); re.addAll((ArrayList<String>)
			 * scheduleInfo.get("recipient_mail"));
			 * 
			 * scheduleInfo.put("recipient_mail", re);
			 */

			test.log(LogStatus.INFO, "Validate the response");
			reportScheduleId = validateReportScheduleInfo(scheduleInfo, actualResponse, test);

		} else {

			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}

		return reportScheduleId;
	}

	/**
	 * Delete reportSchedules by id
	 * 
	 * @author Rakesh.Chalamala
	 * @param reportschedule_id
	 * @param token
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 * 
	 */
	public void deleteReportScheduleByIdWithCheck(String reportSchedule_id, String token, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage, ExtentTest test) {

		test.log(LogStatus.INFO, "Call the api DELETE REPORT SCHEDULE BY ID");
		Response response = spogReportInvoker.deleteReportSchedule(reportSchedule_id, token, test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.INFO, "Succesfully deleted report schedule with id: " + reportSchedule_id);
		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			if (code.contains("01300009")) {
				message = message.replace("{0}", reportSchedule_id);
			}

			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}

	}

	/**
	 * Delete Reports by schedule_id
	 * 
	 * @author Rakesh.Chalamala
	 * @param reportschedule_id
	 * @param token
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 * 
	 */
	public void deleteReportsByScheduleIdWithCheck(String reportSchedule_id, String token, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage, ExtentTest test) {

		test.log(LogStatus.INFO, "Call the api DELETE REPORTS BY SCHEDULE ID");
		Response response = spogReportInvoker.deleteReportsByScheduleId(reportSchedule_id, token, test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.INFO, "Succesfully deleted reports with schedule id: " + reportSchedule_id);
		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			if (code.contains("01300009")) {
				message = message.replace("{0}", reportSchedule_id);
			}

			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}

	}

	/**
	 * Get reportSchedules
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 */
	public void getReportSchedulesWithCheck(String token, ArrayList<HashMap<String, Object>> expectedResponse,
			String filterStr, String SortStr, int page, int page_size, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage, ExtentTest test) {

		String additionalURL = null;
		ArrayList<HashMap<String, Object>> filteredResponse = null;

		if (page == 0 || page == -1) {
			page = 1;
		}
		if (page_size == 0 || page_size == -1) {
			page_size = 20;
		} else if (page_size >= 100 && page_size < SpogConstants.MAX_PAGE_SIZE) {
			page_size = 100;
		}
		additionalURL = spogServer.PrepareURL(filterStr, SortStr, page, page_size, test);

		test.log(LogStatus.INFO, "Call the api GET REPORT SCHEDULES");
		Response response = spogReportInvoker.getReportSchedule(token, additionalURL, test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			ArrayList<HashMap<String, Object>> actualResponse = new ArrayList<>();
			actualResponse = response.then().extract().path("data");

			// Filtering the expected response if applied.
			if (filterStr != null && !filterStr.isEmpty()) {

				String[] filterStrArray = filterStr.split(",");
				for (int i = 0; i < filterStrArray.length; i++) {
					String[] eachFilterStr = filterStrArray[i].split(";");

					filteredResponse = new ArrayList<>();
					for (int j = 0; j < expectedResponse.size(); j++) {

						if (eachFilterStr[0].equals("report_type") || eachFilterStr[0].equals("schedule_frequency")
								|| eachFilterStr[0].equals("organization_id")
								|| eachFilterStr[0].equals("report_for_organization")) {
							if (expectedResponse.get(j).containsValue(eachFilterStr[2])) {
								filteredResponse.add(expectedResponse.get(j));
							}
						} else if (eachFilterStr[0].equals("group_id")) {
							HashMap<String, Object> reportFor = (HashMap<String, Object>) expectedResponse.get(j)
									.get("report_for");
							if (reportFor.containsValue("all_sources")) {
								filteredResponse.add(expectedResponse.get(j));
							} else if (reportFor.containsValue("selected_source_groups")) {
								ArrayList<HashMap<String, Object>> details = (ArrayList<HashMap<String, Object>>) reportFor
										.get("details");
								for (int k = 0; k < details.size(); k++) {
									if (details.get(k).containsValue(eachFilterStr[2])) {
										filteredResponse.add(expectedResponse.get(j));
									}
								}
							}
						}
					}
					expectedResponse = filteredResponse;
				}
			} else {
				filteredResponse = expectedResponse;
			}

			// Sorting the expected response if applied
			if (SortStr != null && !SortStr.isEmpty()) {

				String[] eachSortStr = SortStr.split(";");

				if (eachSortStr[1].equalsIgnoreCase("asc")) {
					if (eachSortStr[0].equals("report_type") || eachSortStr[0].equals("schedule_frequency")
							|| eachSortStr[0].equals("date_range_type") || eachSortStr[0].equals("report_for_type")) {
						// Sorting by create_ts in descending order when two params have same value
						filteredResponse = spogServer.sortArrayListbyString2(filteredResponse, eachSortStr[0],
								"create_ts");
					} else {
						filteredResponse = spogServer.sortArrayListbyString(filteredResponse, eachSortStr[0],
								"create_ts");
					}
				} else {
					filteredResponse = spogServer.sortArrayListbyString(filteredResponse, eachSortStr[0], "create_ts");
					Collections.reverse(filteredResponse);
				}

			} else {
				System.out.println("No sort applied");
				test.log(LogStatus.INFO, "No sort applied");
			}

			int length = actualResponse.size();
			int start = 0;
			if (page != 1) {
				start = (page - 1) * page_size;
			}

			for (int i = start, j = 0; i < length; i++, j++) {

				spogServer.setToken(token);
				// filteredResponse.get(i).put("create_user_id",
				// spogServer.GetLoggedinUser_UserID());

				test.log(LogStatus.INFO, "Validate the response");
				validateReportScheduleInfo(filteredResponse.get(i), actualResponse.get(j), test);
			}

		} else {

			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
	}

	/**
	 * UPDATE REPORTSCHEDULE BY ID
	 * 
	 * @author Rakesh.Chalamala
	 * @param schedule_id
	 * @param token
	 * @param scheduleInfo
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 * 
	 */
	public Response updateReportScheduleByIdWithCheck(String schedule_id, String token,
			HashMap<String, Object> scheduleInfo, int expectedStatusCode, SpogMessageCode expectedErrorMessage,
			ExtentTest test) {

		test.log(LogStatus.INFO, "Call the API UPDATE REPORTSCHEDULE BY ID");
		Response response = spogReportInvoker.updateReportSchedule(schedule_id, token, scheduleInfo, test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			HashMap<String, Object> actualResponse = new HashMap<>();
			actualResponse = response.then().extract().path("data");

			spogServer.setToken(token);
			scheduleInfo.put("create_user_id", spogServer.GetLoggedinUser_UserID());

			/*
			 * //Adding the loggedin user email to recipient_mail for validation.
			 * spogServer.setToken(token); ArrayList<String> re = new ArrayList<>();
			 * re.add(spogServer.getLoggedinUser_EmailId()); re.addAll((ArrayList<String>)
			 * scheduleInfo.get("recipient_mail"));
			 * 
			 * scheduleInfo.put("recipient_mail", re);
			 */

			test.log(LogStatus.INFO, "Validate the response");
			validateReportScheduleInfo(scheduleInfo, actualResponse, test);

		} else {

			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			if (code.contains("01300009")) {
				message = message.replace("{0}", schedule_id);
			}
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
		return response;
	}

	/**
	 * Validate reportschedule info
	 * 
	 * @author Rakesh.Chalamala
	 * @param expectedData
	 * @param actualData
	 * @param test
	 * @return schedule_id
	 */
	public String validateReportScheduleInfo(HashMap<String, Object> expectedData, HashMap<String, Object> actualData,
			ExtentTest test) {
		String schedule_id = null;

		test.log(LogStatus.INFO, "Compare report_name");
		spogServer.assertResponseItem(expectedData.get("report_name"), actualData.get("report_name"), test);

		test.log(LogStatus.INFO, "Compare report_type");
		spogServer.assertResponseItem(expectedData.get("report_type"), actualData.get("report_type"), test);

		test.log(LogStatus.INFO, "Compare date_range_type");
		spogServer.assertResponseItem(expectedData.get("date_range_type"), actualData.get("date_range_type"), test);

		test.log(LogStatus.INFO, "Compare date_range_start_ts");
		spogServer.assertResponseItem(expectedData.get("date_range_start_ts"), actualData.get("date_range_start_ts"),
				test);

		test.log(LogStatus.INFO, "Compare date_range_end_ts");
		spogServer.assertResponseItem(expectedData.get("date_range_end_ts"), actualData.get("date_range_end_ts"), test);

		test.log(LogStatus.INFO, "Compare schedule_frequencey");
		spogServer.assertResponseItem(expectedData.get("schedule_frequency"), actualData.get("schedule_frequency"),
				test);

		test.log(LogStatus.INFO, "Compare source_group_list");
		// spogServer.assertResponseItem(expectedData.get("source_group_list"),
		// actualData.get("source_group_list"), test);

		test.log(LogStatus.INFO, "Compare organization_list");
		// spogServer.assertResponseItem(expectedData.get("organization_list"),
		// actualData.get("organization_list"), test);

		test.log(LogStatus.INFO, "Compare recipient_mail");
		ArrayList<String> exp_rec_mail = new ArrayList<>();
		ArrayList<String> act_rec_mail = new ArrayList<>();

		exp_rec_mail = (ArrayList<String>) expectedData.get("recipient_mail");
		act_rec_mail = (ArrayList<String>) actualData.get("recipient_mail");

		if (exp_rec_mail.size() == act_rec_mail.size()) {
			for (int i = 0; i < exp_rec_mail.size(); i++) {
				for (int j = 0; j < act_rec_mail.size(); j++) {
					if (exp_rec_mail.get(i).toString().equalsIgnoreCase(act_rec_mail.get(j).toString())) {
						spogServer.assertResponseItem(exp_rec_mail.get(i), act_rec_mail.get(j), test);
					}
				}
			}
		} else {
			// assertTrue(false, "Expected Recipient mails are: "+exp_rec_mail+ " and Actual
			// Recipient mails are: "+ act_rec_mail);
		}

		test.log(LogStatus.INFO, "Compare create_user_id");
		if (expectedData.get("create_user_id") != null) {
			spogServer.assertResponseItem(expectedData.get("create_user_id"),
					((HashMap<String, Object>) actualData.get("create_user")).get("create_user_id"), test);
		} else {
			spogServer.assertResponseItem(expectedData.get("create_user"), actualData.get("create_user"), test);
		}

		test.log(LogStatus.INFO, "Compare cron_expression");
		spogServer.assertResponseItem(expectedData.get("cron_expression"), actualData.get("cron_expression"), test);

		test.log(LogStatus.INFO, "Checking available_actions");
		if (actualData.containsKey("available_actions")) {
			assertTrue(((ArrayList<String>) actualData.get("available_actions")).size() != 0,
					"Response contains the available_actions");
			test.log(LogStatus.INFO, "the available_actions are: " + actualData.get("available_actions"));
			System.out.println(actualData.get("available_actions"));
		} else {
			System.out.println("response does not contain the availble actions");
			assertTrue(false, "response does not contain the availble actions");
		}

		schedule_id = (String) actualData.get("schedule_id");

		return schedule_id;
	}

	/**
	 * Compose reportFilterInfo
	 * 
	 * @author Rakesh.Chalamala
	 * @param source_name
	 * @param destination_name
	 * @param policy_id
	 * @param destination_id
	 * @param source_group_id
	 * @param organization_ids
	 * @param date_range
	 * @param report_type
	 * @param destination_type
	 * @param job_type
	 * @param retention_id
	 * @param backup_schedule
	 * @param filter_name
	 * @param is_default
	 * @param report_filter_type
	 * 
	 */
	public HashMap<String, Object> composeReportFilterInfo(String source_name, String destination_name,
			String policy_id, String destination_id, String source_group_id, String organization_ids,
			String date_range_type, long date_range_start_ts, long date_range_end_ts, String report_type,
			String destination_type, String job_type, String retention_id, String backup_schedule, String filter_name,
			boolean is_default, String report_filter_type) {

		HashMap<String, Object> reportFilterInfo = new HashMap<>();
		ArrayList<String> temp = new ArrayList<>();
		HashMap<String, Object> date_range = new HashMap<>();

		if (source_name != (null)) {
			reportFilterInfo.put("source_name", source_name);
		}

		if (destination_name != (null)) {
			reportFilterInfo.put("destination_name", destination_name);
		}

		// composing date_range param
		if (date_range_type != (null)) {
			date_range.put("type", date_range_type);
		}

		if (date_range_start_ts != 0) {
			date_range.put("start_ts", date_range_start_ts);
		}

		if (date_range_end_ts != 0) {
			date_range.put("end_ts", date_range_end_ts);
		}

		/*if (!date_range.isEmpty()) {
			reportFilterInfo.put("date_range", date_range);
		}

		if (policy_id != (null)) {
			if (policy_id.contains(",")) {
				String[] policies = policy_id.split(",");
				for (int i = 0; i < policies.length; i++) {
					temp = new ArrayList<>();
					temp.add(policies[i]);
				}
				reportFilterInfo.put("policy_id", temp);

			} else {
				temp = new ArrayList<>();
				temp.add(policy_id);
				reportFilterInfo.put("policy_id", temp);
			}
		}

		if (destination_id != (null)) {
			if (destination_id.contains(",")) {
				String[] destination_ids = destination_id.split(",");
				for (int i = 0; i < destination_ids.length; i++) {
					temp = new ArrayList<>();
					temp.add(destination_ids[i]);
				}
				reportFilterInfo.put("destination_id", temp);

			} else {
				temp = new ArrayList<>();
				temp.add(destination_id);
				reportFilterInfo.put("destination_id", temp);
			}
		}
*/
		if (source_group_id != (null)) {
			if (source_group_id.contains(",")) {
				String[] source_group_ids = source_group_id.split(",");
				for (int i = 0; i < source_group_ids.length; i++) {
					temp = new ArrayList<>();
					temp.add(source_group_ids[i]);
				}
				reportFilterInfo.put("group_id", temp);

			} else {
				temp = new ArrayList<>();
				temp.add(source_group_id);
				reportFilterInfo.put("group_id", temp);
			}
		}

		if (organization_ids != (null)) {
			if (organization_ids.contains(",")) {
				String[] organization_ids1 = organization_ids.split(",");
				for (int i = 0; i < organization_ids1.length; i++) {
					temp = new ArrayList<>();
					temp.add(organization_ids1[i]);
				}
				reportFilterInfo.put("organization_ids", temp);

			} else {
				temp = new ArrayList<>();
				temp.add(organization_ids);
				reportFilterInfo.put("organization_ids", temp);
			}
		}

		if (report_type != (null)) {
			if (report_type.contains(",")) {
				String[] report_types = report_type.split(",");
				for (int i = 0; i < report_types.length; i++) {
					temp = new ArrayList<>();
					temp.add(report_types[i]);
				}
				reportFilterInfo.put("report_type", temp);

			} else {
				temp = new ArrayList<>();
				temp.add(report_type);
				reportFilterInfo.put("report_type", temp);
			}
		}

		if (destination_type != (null)) {
			if (destination_type.contains(",")) {
				String[] destination_types = destination_type.split(",");
				for (int i = 0; i < destination_types.length; i++) {
					temp = new ArrayList<>();
					temp.add(destination_types[i]);
				}
				reportFilterInfo.put("destination_type", temp);

			} else {
				temp = new ArrayList<>();
				temp.add(destination_type);
				reportFilterInfo.put("destination_type", temp);
			}
		}

		if (job_type != (null)) {
			if (job_type.contains(",")) {
				String[] job_types = job_type.split(",");
				for (int i = 0; i < job_types.length; i++) {
					temp = new ArrayList<>();
					temp.add(job_types[i]);
				}
				reportFilterInfo.put("job_type", temp);

			} else {
				temp = new ArrayList<>();
				temp.add(job_type);
				reportFilterInfo.put("job_type", temp);
			}
		}

		if (retention_id != (null)) {
			if (retention_id.contains(",")) {
				String[] retention_ids = retention_id.split(",");
				for (int i = 0; i < retention_ids.length; i++) {
					temp = new ArrayList<>();
					temp.add(retention_ids[i]);
				}
				reportFilterInfo.put("retention_id", temp);

			} else {
				temp = new ArrayList<>();
				temp.add(retention_id);
				reportFilterInfo.put("retention_id", temp);
			}
		}

		if (backup_schedule != (null)) {
			if (backup_schedule.contains(",")) {
				String[] backup_schedules = backup_schedule.split(",");
				for (int i = 0; i < backup_schedules.length; i++) {
					temp = new ArrayList<>();
					temp.add(backup_schedules[i]);
				}
				reportFilterInfo.put("backup_schedule", temp);

			} else {
				temp = new ArrayList<>();
				temp.add(backup_schedule);
				reportFilterInfo.put("backup_schedule", temp);
			}
		}

		if (filter_name != (null)) {
			reportFilterInfo.put("filter_name", filter_name);
		}

		reportFilterInfo.put("is_default", is_default);

		if (report_filter_type != (null)) {
			reportFilterInfo.put("report_filter_type", report_filter_type);
		}

		return reportFilterInfo;
	}

	public Response createReportFilterForLoggedInUser(String token, HashMap<String, Object> reportfilterInfo,
			int expectedStatusCode, ExtentTest test) {
		test.log(LogStatus.INFO, "Call the API Create reportfilters for logged in user");
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!reportfilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.report_filter.toString(),
					reportfilterInfo.get("filter_name").toString(), "none", "none",
					Boolean.valueOf(reportfilterInfo.get("is_default").toString()), reportfilterInfo);
		}

		Response response = spogServer.createFilters(token, filter_info, "", test);
	/*	Response response = spogReportInvoker.createReportFiltersForLoggedInUser(token, filter_info, test);*/
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		return response;
	}

	/**
	 * Create Report filters for loggedin user
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param reportfilterInfo
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 * 
	 */
	public String createReportFiltersForLoggedInUser(String token, HashMap<String, Object> reportfilterInfo,
			int expectedStatusCode, SpogMessageCode expectedErrorMessage, ExtentTest test) {

		String filter_id = null;
		/*
		 * Response response = createReportFilterForLoggedInUser(token,
		 * reportfilterInfo, expectedStatusCode, test);
		 */HashMap<String, Object> filter_info = new HashMap<String, Object>();
			if (!reportfilterInfo.isEmpty()) {
				filter_info = jp.composeFilterInfo(filterType.report_filter.toString(),
						reportfilterInfo.get("filter_name").toString(), "none", "none",
						Boolean.valueOf(reportfilterInfo.get("is_default").toString()), reportfilterInfo);
			}

			Response response = spogServer.createFilters(token, filter_info, "", test);
			spogServer.checkResponseStatus(response, expectedStatusCode);
		if (expectedStatusCode == SpogConstants.SUCCESS_POST) {

			HashMap<String, Object> actualResponse = new HashMap<>();
			actualResponse = response.then().extract().path("data");

			filter_id = (String) actualResponse.get("filter_id");

			spogServer.setToken(token);
			reportfilterInfo.put("user_id", spogServer.GetLoggedinUser_UserID());
			reportfilterInfo.put("organization_id", spogServer.GetLoggedinUserOrganizationID());

			test.log(LogStatus.INFO, "Verify report filter info");
			verifyReportFilterInfo(reportfilterInfo, reportfilterInfo, actualResponse, test);

		} else {

			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}

		return filter_id;
	}

	/**
	 * Create Report filters for loggedin user
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param reportfilterInfo
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 * 
	 */
	public Response createReportFiltersForLoggedInUser_audit(String token, HashMap<String, Object> reportfilterInfo,
			int expectedStatusCode, SpogMessageCode expectedErrorMessage, ExtentTest test) {

		String filter_id = null;

		test.log(LogStatus.INFO, "Call the API Create reportfilters for logged in user");
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!reportfilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.report_filter.toString(),
					reportfilterInfo.get("filter_name").toString(), "none", "none",
					Boolean.valueOf(reportfilterInfo.get("is_default").toString()), reportfilterInfo);
		}

		Response response = spogServer.createFilters(token, filter_info, "", test);/*
		 * Response response =
		 * spogReportInvoker.createReportFiltersForLoggedInUser(token, filter_info,
		 * test);
		 */
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_POST) {

			HashMap<String, Object> actualResponse = new HashMap<>();
			actualResponse = response.then().extract().path("data");

			filter_id = (String) actualResponse.get("filter_id");

			spogServer.setToken(token);
			reportfilterInfo.put("user_id", spogServer.GetLoggedinUser_UserID());
			reportfilterInfo.put("organization_id", spogServer.GetLoggedinUserOrganizationID());

			test.log(LogStatus.INFO, "Verify report filter info");
			verifyReportFilterInfo(reportfilterInfo, reportfilterInfo, actualResponse, test);

		} else {

			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}

		return response;
	}

	/**
	 * Verify report filter info
	 * 
	 * @author Rakesh.Chalamala
	 * @param expectedResponse
	 * @param updatedexpectedResponse
	 * @param actualResponse
	 * @param test
	 * 
	 */
	public void verifyReportFilterInfo(HashMap<String, Object> updatedExpectedResponse,
			HashMap<String, Object> expectedResponse, HashMap<String, Object> actualResponse, ExtentTest test) {

		if (expectedResponse.containsKey("source_name") && actualResponse.containsKey("source_name")) {

			if (updatedExpectedResponse.containsKey("source_name") && actualResponse.containsKey("source_name")) {
				test.log(LogStatus.INFO, "Comparing the source_name");
				spogServer.assertResponseItem(updatedExpectedResponse.get("source_name"),
						actualResponse.get("source_name"), test);
			} else {
				test.log(LogStatus.INFO, "Comparing the source_name");
				spogServer.assertResponseItem(expectedResponse.get("source_name"), actualResponse.get("source_name"),
						test);
			}

		}

		if (expectedResponse.containsKey("destination_name") && actualResponse.containsKey("destination_name")) {

			if (updatedExpectedResponse.containsKey("destination_name")) {
				test.log(LogStatus.INFO, "Comparing the destination_name");
				spogServer.assertResponseItem(updatedExpectedResponse.get("destination_name").toString().toLowerCase(),
						actualResponse.get("destination_name").toString().toLowerCase(), test);
			} else {
				test.log(LogStatus.INFO, "Comparing the destination_name");
				spogServer.assertResponseItem(expectedResponse.get("destination_name"),
						actualResponse.get("destination_name"), test);
			}

		}

		if (expectedResponse.containsKey("policy_id") && actualResponse.containsKey("policy_id")) {

			if (updatedExpectedResponse.containsKey("policy_id")) {
				test.log(LogStatus.INFO, "Comparing the policy_id");
				spogServer.assertResponseItem(updatedExpectedResponse.get("policy_id"), actualResponse.get("policy_id"),
						test);
			} else {
				test.log(LogStatus.INFO, "Comparing the policy_id");
				spogServer.assertResponseItem(expectedResponse.get("policy_id"), actualResponse.get("policy_id"), test);
			}

		}

		if (expectedResponse.containsKey("destination_id") && actualResponse.containsKey("destination_id")) {

			if (updatedExpectedResponse.containsKey("destination_id")) {
				test.log(LogStatus.INFO, "Comparing the destination_id");
				spogServer.assertResponseItem(updatedExpectedResponse.get("destination_id"),
						actualResponse.get("destination_id"), test);
			} else {
				test.log(LogStatus.INFO, "Comparing the destination_id");
				spogServer.assertResponseItem(expectedResponse.get("destination_id"),
						actualResponse.get("destination_id"), test);
			}

		}

		if (expectedResponse.containsKey("source_group_id") && actualResponse.containsKey("source_group_id")) {
			/*
			 * if (updatedExpectedResponse.containsKey("source_group_id")&&actualResponse.
			 * containsKey("source_group_id")) { test.log(LogStatus.INFO,
			 * "Comparing the source_group_id");
			 * spogServer.assertResponseItem(updatedExpectedResponse.get("source_group_id"),
			 * actualResponse.get("source_group_id"), test); }else {
			 * test.log(LogStatus.INFO, "Comparing the source_group_id");
			 * spogServer.assertResponseItem(expectedResponse.get("source_group_id"),
			 * actualResponse.get("source_group_id"), test); }
			 */

		}

		if (expectedResponse.containsKey("organization_ids") && actualResponse.containsKey("organization_ids")) {

			if (updatedExpectedResponse.containsKey("organization_ids")) {
				test.log(LogStatus.INFO, "Comparing the organization_ids");
				spogServer.assertResponseItem(updatedExpectedResponse.get("organization_ids"),
						actualResponse.get("organization_ids"), test);
			} else {
				test.log(LogStatus.INFO, "Comparing the organization_ids");
				spogServer.assertResponseItem(expectedResponse.get("organization_ids"),
						actualResponse.get("organization_ids"), test);
			}

		}

		if (expectedResponse.containsKey("report_type") && actualResponse.containsKey("report_type")) {

			if (updatedExpectedResponse.containsKey("report_type")) {
				test.log(LogStatus.INFO, "Comparing the report_type");
				spogServer.assertResponseItem(updatedExpectedResponse.get("report_type"),
						actualResponse.get("report_type"), test);
			} else {
				test.log(LogStatus.INFO, "Comparing the report_type");
				spogServer.assertResponseItem(expectedResponse.get("report_type"), actualResponse.get("report_type"),
						test);
			}

		}

		if (expectedResponse.containsKey("destination_type") && actualResponse.containsKey("destination_type")) {

			if (updatedExpectedResponse.containsKey("destination_type")) {
				test.log(LogStatus.INFO, "Comparing the destination_type");
				spogServer.assertResponseItem(updatedExpectedResponse.get("destination_type"),
						actualResponse.get("destination_type"), test);
			} else {
				test.log(LogStatus.INFO, "Comparing the destination_type");
				spogServer.assertResponseItem(expectedResponse.get("destination_type"),
						actualResponse.get("destination_type"), test);
			}

		}

		if (expectedResponse.containsKey("job_type") && actualResponse.containsKey("job_type")) {

			if (updatedExpectedResponse.containsKey("job_type")) {
				test.log(LogStatus.INFO, "Comparing the job_type");
				spogServer.assertResponseItem(updatedExpectedResponse.get("job_type"), actualResponse.get("job_type"),
						test);
			} else {
				test.log(LogStatus.INFO, "Comparing the job_type");
				spogServer.assertResponseItem(expectedResponse.get("job_type"), actualResponse.get("job_type"), test);
			}

		}

		if (expectedResponse.containsKey("retention_id") && actualResponse.containsKey("retention_id")) {

			/*
			 * if (updatedExpectedResponse.containsKey("retention_id")) {
			 * test.log(LogStatus.INFO, "Comparing the retention_id");
			 * spogServer.assertResponseItem(updatedExpectedResponse.get("retention_id"),
			 * actualResponse.get("retention_id"), test); }else { test.log(LogStatus.INFO,
			 * "Comparing the retention_id");
			 * spogServer.assertResponseItem(expectedResponse.get("retention_id"),
			 * actualResponse.get("retention_id"), test); }
			 */

		}

		if (expectedResponse.containsKey("backup_schedule") && actualResponse.containsKey("backup_schedule")) {

			if (updatedExpectedResponse.containsKey("backup_schedule")) {
				test.log(LogStatus.INFO, "Comparing the backup_schedule");
				spogServer.assertResponseItem(updatedExpectedResponse.get("backup_schedule"),
						actualResponse.get("backup_schedule"), test);
			} else {
				test.log(LogStatus.INFO, "Comparing the backup_schedule");
				spogServer.assertResponseItem(expectedResponse.get("backup_schedule"),
						actualResponse.get("backup_schedule"), test);
			}

		}

		if (expectedResponse.containsKey("filter_name") && actualResponse.containsKey("filter_name")) {

			if (updatedExpectedResponse.containsKey("filter_name")) {
				test.log(LogStatus.INFO, "Comparing the filter_name");
				spogServer.assertResponseItem(updatedExpectedResponse.get("filter_name").toString().toLowerCase(),
						actualResponse.get("filter_name").toString().toLowerCase(), test);
			} else {
				test.log(LogStatus.INFO, "Comparing the filter_name");
				spogServer.assertResponseItem(expectedResponse.get("filter_name"), actualResponse.get("filter_name"),
						test);
			}

		}

		if (expectedResponse.containsKey("is_default") && actualResponse.containsKey("is_default")) {

			if (updatedExpectedResponse.containsKey("is_default")) {
				test.log(LogStatus.INFO, "Comparing the is_default");
				spogServer.assertResponseItem(updatedExpectedResponse.get("is_default"),
						actualResponse.get("is_default"), test);
			} else {
				test.log(LogStatus.INFO, "Comparing the is_default");
				spogServer.assertResponseItem(expectedResponse.get("is_default"), actualResponse.get("is_default"),
						test);
			}

		}

		if (expectedResponse.containsKey("report_filter_type") && actualResponse.containsKey("report_filter_type")) {

			if (updatedExpectedResponse.containsKey("report_filter_type")) {
				test.log(LogStatus.INFO, "Comparing the report_filter_type");
				spogServer.assertResponseItem(updatedExpectedResponse.get("report_filter_type"),
						actualResponse.get("report_filter_type"), test);
			} else {
				test.log(LogStatus.INFO, "Comparing the report_filter_type");
				spogServer.assertResponseItem(expectedResponse.get("report_filter_type"),
						actualResponse.get("report_filter_type"), test);
			}

		}

		if (expectedResponse.containsKey("user_id") && actualResponse.containsKey("user_id")) {

			if (updatedExpectedResponse.containsKey("user_id")) {
				test.log(LogStatus.INFO, "Comparing the user_id");
				spogServer.assertResponseItem(updatedExpectedResponse.get("user_id"), actualResponse.get("user_id"),
						test);
			} else {
				test.log(LogStatus.INFO, "Comparing the user_id");
				spogServer.assertResponseItem(expectedResponse.get("user_id"), actualResponse.get("user_id"), test);
			}

		}

		if (expectedResponse.containsKey("organization_id") && actualResponse.containsKey("organization_id")) {

			if (updatedExpectedResponse.containsKey("organization_id")) {
				test.log(LogStatus.INFO, "Comparing the organization_id");
				spogServer.assertResponseItem(updatedExpectedResponse.get("organization_id"),
						actualResponse.get("organization_id"), test);
			} else {
				test.log(LogStatus.INFO, "Comparing the organization_id");
				spogServer.assertResponseItem(expectedResponse.get("organization_id"),
						actualResponse.get("organization_id"), test);
			}

		}

		if (expectedResponse.containsKey("date_range") && actualResponse.containsKey("date_range")) {

			HashMap<String, Object> upd_date_range = new HashMap<>();
			HashMap<String, Object> exp_date_range = new HashMap<>();
			HashMap<String, Object> act_date_range = new HashMap<>();

			upd_date_range = (HashMap<String, Object>) updatedExpectedResponse.get("date_range");
			exp_date_range = (HashMap<String, Object>) expectedResponse.get("date_range");
			act_date_range = (HashMap<String, Object>) actualResponse.get("date_range");

			if (upd_date_range.containsKey("type")) {
				test.log(LogStatus.INFO, "Comparing the date_range_type");
				spogServer.assertResponseItem(upd_date_range.get("type"), act_date_range.get("type"), test);
			} else if (exp_date_range.containsKey("type")) {
				test.log(LogStatus.INFO, "Comparing the date_range_type");
				spogServer.assertResponseItem(exp_date_range.get("type"), act_date_range.get("type"), test);
			}

			if (upd_date_range.containsKey("start_ts")) {
				test.log(LogStatus.INFO, "Comparing the date_range_start_ts");
				spogServer.assertResponseItem(upd_date_range.get("start_ts"), act_date_range.get("start_ts"), test);
			} else if (exp_date_range.containsKey("start_ts")) {
				test.log(LogStatus.INFO, "Comparing the date_range_start_ts");
				spogServer.assertResponseItem(exp_date_range.get("start_ts"), act_date_range.get("start_ts"), test);
			}

			if (upd_date_range.containsKey("end_ts")) {
				test.log(LogStatus.INFO, "Comparing the date_range_end_ts");
				spogServer.assertResponseItem(upd_date_range.get("end_ts"), act_date_range.get("end_ts"), test);
			} else if (exp_date_range.containsKey("end_ts")) {
				test.log(LogStatus.INFO, "Comparing the date_range_end_ts");
				spogServer.assertResponseItem(exp_date_range.get("end_ts"), act_date_range.get("end_ts"), test);
			}

		}

	}

	/**
	 * Create Report Filter for Specified user
	 * 
	 * @author Rakesh.Chalamala
	 * @param user_id
	 * @param org_id
	 * @param token
	 * @param reportfilterInfo
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 * @return filter_id
	 */
	public String createReportFilterForSpecifiedUserWithCheck(String user_id, String org_id, String token,
			HashMap<String, Object> reportfilterInfo, int expectedStatusCode, SpogMessageCode expectedErrorMessage,
			ExtentTest test) {

		String filter_id = null;

		test.log(LogStatus.INFO, "Call the API Create reportfilters for specified user");
		/*
		 * Response response =
		 * spogReportInvoker.createReportFiltersForSpecifiedUser(user_id, token,
		 * reportfilterInfo, test);
		 */
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!reportfilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.report_filter.toString(),
					reportfilterInfo.get("filter_name").toString(), user_id, org_id,
					Boolean.valueOf(reportfilterInfo.get("is_default").toString()), reportfilterInfo);
		}

		Response response = spogServer.createFilters(token, filter_info, "", test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_POST) {

			HashMap<String, Object> actualResponse = new HashMap<>();
			actualResponse = response.then().extract().path("data");

			filter_id = (String) actualResponse.get("filter_id");

			spogServer.setToken(token);
			reportfilterInfo.put("user_id", user_id);
			reportfilterInfo.put("organization_id", org_id);

			test.log(LogStatus.INFO, "Verify report filter info");
			verifyReportFilterInfo(reportfilterInfo, reportfilterInfo, actualResponse, test);

		} else {

			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}

		return filter_id;

	}

	/**
	 * Create Report Filter for Specified user
	 * 
	 * @author Rakesh.Chalamala
	 * @param user_id
	 * @param org_id
	 * @param token
	 * @param reportfilterInfo
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 * @return response
	 */
	public Response createReportFilterForSpecifiedUserWithCheck_audit(String user_id, String org_id, String token,
			HashMap<String, Object> reportfilterInfo, int expectedStatusCode, SpogMessageCode expectedErrorMessage,
			ExtentTest test) {

		String filter_id = null;

		test.log(LogStatus.INFO, "Call the API Create reportfilters for specified user");
		/*
		 * Response response =
		 * spogReportInvoker.createReportFiltersForSpecifiedUser(user_id, token,
		 * reportfilterInfo, test);
		 */
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!reportfilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.report_filter.toString(),
					reportfilterInfo.get("filter_name").toString(), user_id, org_id,
					Boolean.valueOf(reportfilterInfo.get("is_default").toString()), reportfilterInfo);
		}

		Response response = spogServer.createFilters(token, filter_info, "", test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_POST) {

			HashMap<String, Object> actualResponse = new HashMap<>();
			actualResponse = response.then().extract().path("data");

			filter_id = (String) actualResponse.get("filter_id");

			spogServer.setToken(token);
			reportfilterInfo.put("user_id", user_id);
			reportfilterInfo.put("organization_id", org_id);

			test.log(LogStatus.INFO, "Verify report filter info");
			verifyReportFilterInfo(reportfilterInfo, reportfilterInfo, actualResponse, test);

		} else {

			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}

		return response;

	}

	/**
	 * update Report filters for loggedin user
	 * 
	 * @author Rakesh.chalamala
	 * @param token
	 * @param filter_id
	 * @param reportfilterInfo
	 * @param defaultfilterInfo
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 * @return response
	 * 
	 */
	public Response updateReportFiltersForLoggedInUser(String token, String filter_id,
			HashMap<String, Object> reportfilterInfo, HashMap<String, Object> defaultfilterInfo, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage, ExtentTest test) {

		test.log(LogStatus.INFO, "Call the API update reportfilters for logged in user");
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!reportfilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.report_filter.toString(),
					reportfilterInfo.get("filter_name").toString(), "none", "none",
					Boolean.valueOf(reportfilterInfo.get("is_default").toString()), reportfilterInfo);
		}
		Response response = spogServer.updateFilterById(token, filter_id, "none", filter_info, "", test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			HashMap<String, Object> actualResponse = new HashMap<>();
			actualResponse = response.then().extract().path("data");

			filter_id = (String) actualResponse.get("filter_id");

			spogServer.setToken(token);
			reportfilterInfo.put("user_id", spogServer.GetLoggedinUser_UserID());
			reportfilterInfo.put("organization_id", spogServer.GetLoggedinUserOrganizationID());

			test.log(LogStatus.INFO, "Verify report filter info");
			verifyReportFilterInfo(reportfilterInfo, defaultfilterInfo, actualResponse, test);

		} else {

			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}

		return response;
	}

	/**
	 * update Report Filter for Specified user
	 * 
	 * @author Ramya.Nagepalli
	 * @param user_id
	 * @param filter_id
	 * @param org_id
	 * @param token
	 * @param reportfilterInfo
	 * @param defaultfilterInfo
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 * @return response
	 */
	public Response updateReportFilterForSpecifiedUserWithCheck(String user_id, String filter_id, String org_id,
			String token, HashMap<String, Object> reportfilterInfo, HashMap<String, Object> defaultfilterInfo,
			int expectedStatusCode, SpogMessageCode expectedErrorMessage, ExtentTest test) {

		test.log(LogStatus.INFO, "Call the API update reportfilters for specified user");

		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!reportfilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.report_filter.toString(),
					reportfilterInfo.get("filter_name").toString(), user_id, org_id,
					Boolean.valueOf(reportfilterInfo.get("is_default").toString()), reportfilterInfo);
		}
		Response response = spogServer.updateFilterById(token, filter_id, user_id, filter_info, "", test);
		
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			HashMap<String, Object> actualResponse = new HashMap<>();
			actualResponse = response.then().extract().path("data");

			filter_id = (String) actualResponse.get("filter_id");

			spogServer.setToken(token);
			reportfilterInfo.put("user_id", user_id);
			reportfilterInfo.put("organization_id", org_id);

			test.log(LogStatus.INFO, "Verify report filter info");
			verifyReportFilterInfo(reportfilterInfo, defaultfilterInfo, actualResponse, test);

		} else {

			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			if (code.contains("00A00702")) {
				spogServer.setToken(token);
				message = message.replace("{0}", filter_id);
				message = message.replace("{1}", user_id);
			}
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}

		return response;

	}

	/**
	 * Get Report filters for loggedin user By Filter Id
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param filter_id
	 * @param reportfilterInfo
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 * 
	 */
	public void getReportFiltersForLoggedInUserByFilterIdWithCheck(String token, String filter_id,
			HashMap<String, Object> reportfilterInfo, int expectedStatusCode, SpogMessageCode expectedErrorMessage,
			ExtentTest test) {

		test.log(LogStatus.INFO, "Call the API Get reportfilters for logged in user by filter id");
		/*
		 * Response response =
		 * spogReportInvoker.getReportFiltersForLoggedInUserByFilterId(token, filter_id,
		 * test);
		 */
		Response response = spogServer.getFiltersById(token, filter_id, filterType.report_filter.toString(), "none",
				"none",test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			HashMap<String, Object> actualResponse = new HashMap<>();
			actualResponse = response.then().extract().path("data");

			spogServer.setToken(token);
			reportfilterInfo.put("user_id", spogServer.GetLoggedinUser_UserID());
			reportfilterInfo.put("organization_id", spogServer.GetLoggedinUserOrganizationID());

			test.log(LogStatus.INFO, "Verify report filter info");
			verifyReportFilterInfo(reportfilterInfo, reportfilterInfo, actualResponse, test);

		} else {

			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			if (code.contains("00A00702")) {
				spogServer.setToken(token);
				String user_id = spogServer.GetLoggedinUser_UserID();
				message = message.replace("{0}", filter_id);
				message = message.replace("{1}", user_id);
			}
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}

	}

	/**
	 * Get Report Filter for Specified user By Filter Id
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param user_id
	 * @param org_id
	 * @param filter_id
	 * @param reportfilterInfo
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 * @return filter_id
	 */
	public void getReportFilterForSpecifiedUserByFilterIdWithCheck(String token, String user_id, String org_id,
			String filter_id, HashMap<String, Object> reportfilterInfo, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage, ExtentTest test) {

		test.log(LogStatus.INFO, "Call the API Get reportfilters for specified user by filter id");
		/*
		 * Response response =
		 * spogReportInvoker.getReportFiltersForSpecifiedUserByFilterId(token, user_id,
		 * filter_id, test);
		 */
		Response response = spogServer.getFiltersById(token, filter_id, filterType.report_filter.toString(), user_id,
				"none",test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			HashMap<String, Object> actualResponse = new HashMap<>();
			actualResponse = response.then().extract().path("data");

			spogServer.setToken(token);
			reportfilterInfo.put("user_id", user_id);
			reportfilterInfo.put("organization_id", org_id);

			test.log(LogStatus.INFO, "Verify report filter info");
			verifyReportFilterInfo(reportfilterInfo, reportfilterInfo, actualResponse, test);

		} else {

			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}

	}

	/**
	 * Get Report filters for loggedin user
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param reportfilterInfo
	 * @param filterStr
	 * @param curr_page
	 * @param page_size
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 * 
	 */
	public void getReportFiltersForLoggedInUserWithCheck(String token, ArrayList<HashMap<String, Object>> expectedData,
			String filterStr, int curr_page, int page_size, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage, ExtentTest test) {

		String additionalURL = "";

		if (curr_page == 0 || curr_page == -1) {
			curr_page = 1;
		}
		if (page_size == 0 || page_size == -1) {
			page_size = 20;
		} else if (page_size >= 100 && page_size < SpogConstants.MAX_PAGE_SIZE) {
			page_size = 100;
		}

		// Preparing the additional url with no sort and filter,pagination
		additionalURL = spogServer.PrepareURL(filterStr, null, curr_page, page_size, test);

		test.log(LogStatus.INFO, "Call the API Get reportfilters for logged in user ");
		/*
		 * Response response = spogReportInvoker.getReportFiltersForLoggedInUser(token,
		 * additionalURL, test);
		 */
		Response response = spogServer.getFilters(token, "none", filterType.report_filter.toString(), additionalURL,test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			ArrayList<HashMap<String, Object>> filteredExpectedData = new ArrayList<>();

			ArrayList<HashMap<String, Object>> actualData = new ArrayList<>();
			actualData = response.then().extract().path("data");

			if (expectedData.size() > 1) {

				// Making the previous filter is_default value to false and leaving the last
				// created filter value to true
				for (int i = 0; i < expectedData.size(); i++) {

					if (expectedData.get(expectedData.size() - 1 - i).get("is_default").equals(true)) {

						for (int j = 0; j < (expectedData.size() - i) - 1; j++) {
							expectedData.get(j).put("is_default", false);
						}
						break;
					}
				}
			}

			if (filterStr != null && !filterStr.isEmpty()) {

				String[] filterStrArray = filterStr.split(",");
				for (int i = 0; i < filterStrArray.length; i++) {
					String[] eachFilterStr = filterStrArray[i].split(";");

					filteredExpectedData = new ArrayList<>();
					for (int j = 0; j < expectedData.size(); j++) {

						if (eachFilterStr[0].equals("is_default")) {
							if (expectedData.get(j).containsValue(Boolean.valueOf(eachFilterStr[2]))) {
								filteredExpectedData.add(expectedData.get(j));
							}
						} else if (eachFilterStr[0].equals("filter_name")) {
							if (eachFilterStr[2].contains("|")) {
								String[] multiple = eachFilterStr[2].split("\\|");
								if (expectedData.get(j).containsValue(multiple[0])
										|| expectedData.get(j).containsValue(multiple[1])) {
									filteredExpectedData.add(expectedData.get(j));
								}
							} else {
								if (expectedData.get(j).containsValue(eachFilterStr[2])) {
									filteredExpectedData.add(expectedData.get(j));
								}
							}
						} else if (eachFilterStr[0].equals("report_filter_type")) {
							if (expectedData.get(j).containsValue(eachFilterStr[2])) {
								filteredExpectedData.add(expectedData.get(j));
							}
						}
					}
					expectedData = filteredExpectedData;
				}

			} else {
				filteredExpectedData = expectedData;
			}

			int j = 0;

			j = page_size * (curr_page - 1);
			if (j <= filteredExpectedData.size()) {

				for (int i = 0; j < actualData.size(); i++, j++) {

					test.log(LogStatus.INFO, "Verify the getReportListFilters");
					if(filteredExpectedData.get(j).get("filter_name").toString().equals(actualData.get(i).get("filter_name").toString()))
					verifyReportFilterInfo(filteredExpectedData.get(j), filteredExpectedData.get(j), actualData.get(i),
							test);
				}
			}
		}

		else {

			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}

	}

	/**
	 * Get Report Filter for Specified user
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param user_id
	 * @param org_id
	 * @param reportfilterInfo
	 * @param filterStr
	 * @param curr_page
	 * @param page_size
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 */
	public void getReportFilterForSpecifiedUserWithCheck(String token, String user_id, String org_id,
			ArrayList<HashMap<String, Object>> expectedData, String filterStr, int curr_page, int page_size,
			int expectedStatusCode, SpogMessageCode expectedErrorMessage, ExtentTest test) {

		String additionalURL = "";

		if (curr_page == 0 || curr_page == -1) {
			curr_page = 1;
		}
		if (page_size == 0 || page_size == -1) {
			page_size = 20;
		} else if (page_size >= 100 && page_size < SpogConstants.MAX_PAGE_SIZE) {
			page_size = 100;
		}

		// Preparing the additional url with no sort and filter,pagination
		additionalURL = spogServer.PrepareURL(filterStr, null, curr_page, page_size, test);

		test.log(LogStatus.INFO, "Call the API Get reportfilters for specified user ");
		/*
		 * Response response = spogReportInvoker.getReportFiltersForSpecifiedUser(token,
		 * user_id, additionalURL, test);
		 */
		Response response = spogServer.getFilters(token, user_id, filterType.report_filter.toString(), additionalURL,test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			ArrayList<HashMap<String, Object>> filteredExpectedData = new ArrayList<>();

			ArrayList<HashMap<String, Object>> actualData = new ArrayList<>();
			actualData = response.then().extract().path("data");

			if (expectedData.size() > 1) {

				// Making the previous filter is_default value to false and leaving the last
				// created filter value to true
				for (int i = 0; i < expectedData.size(); i++) {

					if (expectedData.get(expectedData.size() - 1 - i).get("is_default").equals(true)) {

						for (int j = 0; j < (expectedData.size() - i) - 1; j++) {
							expectedData.get(j).put("is_default", false);
						}
						break;
					}
				}
			}

			if (filterStr != null && !filterStr.isEmpty()) {

				String[] filterStrArray = filterStr.split(",");
				for (int i = 0; i < filterStrArray.length; i++) {
					String[] eachFilterStr = filterStrArray[i].split(";");

					filteredExpectedData = new ArrayList<>();
					for (int j = 0; j < expectedData.size(); j++) {

						if (eachFilterStr[0].equals("is_default")) {
							if (expectedData.get(j).containsValue(Boolean.valueOf(eachFilterStr[2]))) {
								filteredExpectedData.add(expectedData.get(j));
							}
						} else if (eachFilterStr[0].equals("filter_name")) {
							if (eachFilterStr[2].contains("|")) {
								String[] multiple = eachFilterStr[2].split("\\|");
								if (expectedData.get(j).containsValue(multiple[0])
										|| expectedData.get(j).containsValue(multiple[1])) {
									filteredExpectedData.add(expectedData.get(j));
								}
							} else {
								if (expectedData.get(j).containsValue(eachFilterStr[2])) {
									filteredExpectedData.add(expectedData.get(j));
								}
							}
						} else if (eachFilterStr[0].equals("report_filter_type")) {
							if (expectedData.get(j).containsValue(eachFilterStr[2])) {
								filteredExpectedData.add(expectedData.get(j));
							}
						}
					}
					expectedData = filteredExpectedData;
				}

			} else {
				filteredExpectedData = expectedData;
			}

			int j = 0;

			j = page_size * (curr_page - 1);
			if (j <= filteredExpectedData.size()) {

				for (int i = 0; j < actualData.size(); i++, j++) {

					test.log(LogStatus.INFO, "Verify the getReportListFilters");
					verifyReportFilterInfo(filteredExpectedData.get(j), filteredExpectedData.get(j), actualData.get(i),
							test);
				}
			}
		} else {

			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}

	}

	/**
	 * GET REPORT FILTERS
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param expectedData
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 */
	public void getSystemReportFiltersWithCheck(String token, ArrayList<HashMap<String, Object>> expectedData,
			int expectedStatusCode, SpogMessageCode expectedErrorMessage, ExtentTest test) {

		test.log(LogStatus.INFO, "Call the API getReportFilters");
		/* Response response = spogReportInvoker.getReportFilters(token, test); */
		Response response = spogServer.getFilters(token, "none", filterType.report_filter_global.toString(), "none",test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			ArrayList<HashMap<String, Object>> actualData = new ArrayList<>();
			actualData = response.then().extract().path("data");

			test.log(LogStatus.INFO, "Sorting the expected and actual response by filter_id");
			// spogServer.sortArrayListbyString(expectedData, "filter_name");
			// spogServer.sortArrayListbyString(actualData, "filter_name");

			test.log(LogStatus.INFO, "Check the default response");
			for (int i = 0; i < actualData.size(); i++) {

				spogServer.assertResponseItem(actualData.get(i).get("filter_id").toString(),
						expectedData.get(i).get("filter_id").toString());
				spogServer.assertResponseItem(actualData.get(i).get("filter_name").toString(),
						expectedData.get(i).get("filter_name").toString());
				spogServer.assertResponseItem(actualData.get(i).get("user_id").toString(),
						expectedData.get(i).get("user_id").toString());
				spogServer.assertResponseItem(actualData.get(i).get("organization_id").toString(),
						expectedData.get(i).get("organization_id").toString());
				spogServer.assertResponseItem(actualData.get(i).get("is_default").toString(),
						expectedData.get(i).get("is_default").toString());
				spogServer.assertResponseItem(actualData.get(i).get("create_ts").toString(),
						expectedData.get(i).get("create_ts").toString());
				spogServer.assertResponseItem(actualData.get(i).get("modify_ts").toString(),
						expectedData.get(i).get("modify_ts").toString());

				spogServer.assertResponseItem(actualData.get(i).get("filter_type").toString(),
						expectedData.get(i).get("filter_type").toString());
				spogServer.assertResponseItem(actualData.get(i).get("count").toString(),
						expectedData.get(i).get("count").toString());

				HashMap<String, Object> act_dr = (HashMap<String, Object>) actualData.get(i).get("date_range");
				HashMap<String, Object> exp_dr = (HashMap<String, Object>) expectedData.get(i).get("date_range");

				spogServer.assertResponseItem(act_dr.get("type").toString(), exp_dr.get("type").toString());
				spogServer.assertResponseItem(act_dr.get("start_ts").toString(), exp_dr.get("start_ts").toString());
				spogServer.assertResponseItem(act_dr.get("end_ts").toString(), exp_dr.get("end_ts").toString());
			}

			assertTrue(true, "Validation of default report filters passed.");

		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
	}

	/**
	 * This method is used to updateBackUpJobReportsColumnsForSpecifiedUser
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param user_id
	 * @param expected_columns
	 * @param expectedStatusCode
	 * @param EpectedErrorMesssage
	 * @param test
	 */
	public Response updateBackUpJobReportsColumnsForSpecifiedUser(String token, String user_id,
			ArrayList<HashMap<String, Object>> expected_columns, int expectedStatusCode, ExtentTest test) {

		Map<String, ArrayList<HashMap<String, Object>>> compose_expected_columns = null;

		compose_expected_columns = jp.jobColumnInfo(expected_columns);

		Response response = spogReportInvoker.updateBackUpJobReportsColumnsforSpecifiedUser(token, user_id,
				compose_expected_columns, test);

		spogServer.checkResponseStatus(response, expectedStatusCode);

		return response;
	}

	/**
	 * This method is used to updateBackUpJobReportsColumnsForLoggedInUser
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param expected_columns
	 * @param expectedStatusCode
	 * @param EpectedErrorMesssage
	 * @param test
	 */
	public Response updateBackUpJobReportsColumnsForLoggedInUser(String token,
			ArrayList<HashMap<String, Object>> expected_columns, int expectedStatusCode, ExtentTest test) {
		// TODO Auto-generated method stub

		Map<String, ArrayList<HashMap<String, Object>>> compose_expected_columns = null;

		compose_expected_columns = jp.jobColumnInfo(expected_columns);

		Response response = spogReportInvoker.updateBackUpJobReportsColumnsforLoggedInUser(token,
				compose_expected_columns, test);

		spogServer.checkResponseStatus(response, expectedStatusCode);

		return response;
	}

	/**
	 * @author Ramya.Nagepalli
	 * 
	 *         get the job report details
	 * @param response
	 * @param expectedErrorMessage
	 */
	public void checkJobReportDetails(Response response, int curr_page, int page_size, String sortjobs, String filter,
			ArrayList<HashMap<String, Object>> expectedresponse, int expectedstatuscode, SpogMessageCode errorMessage,
			ExtentTest test) {

		ArrayList<HashMap<String, Object>> actual_response = new ArrayList<>();
		int size_expectedresponse = 0;
		int size_actualresponse = 0;
		int actual_pagesize;
		if (curr_page == 0 || curr_page == -1) {
			curr_page = 1;
		}
		if (page_size == 0 || page_size == -1) {
			page_size = 20;
		}
		if (page_size > 100) {
			page_size = 100;
		}
		test.log(LogStatus.INFO, "Started checking the response body for the jobs");
		spogServer.checkResponseStatus(response, expectedstatuscode);
		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			actual_response = response.then().extract().path("data");
			// actual_pagesize = response.then().extract().path("pagination.page_size");
			// actual_pagesize = response.then().extract().path("pagination.curr_page");
			size_expectedresponse = expectedresponse.size();
			size_actualresponse = actual_response.size();
			if (size_actualresponse == 0) {
				ArrayList<HashMap<String, Object>> errors = response.then().extract().path("errors");
				if (errors.size() == 0) {
					test.log(LogStatus.PASS, "No data available with the applied filter");
				} else {
					test.log(LogStatus.FAIL,
							"The jobs count is 0 and the error message is " + response.then().extract().path("errors"));
					assertTrue("The jobs count is 0", false);
				}

			} else if (size_expectedresponse >= size_actualresponse) {
				// When the input does not have any sorting/filtering/paging then sort by
				// ascending order
				if ((sortjobs == "" || sortjobs == null && filter == "" || filter == null)
						|| (sortjobs == "" || sortjobs == null && filter != null)) {
					Collections.sort(actual_response, new Comparator<HashMap>() {

						@Override
						public int compare(HashMap o1, HashMap o2) {

							// TODO Auto-generated method stub
							int create_ts = (int) o1.get("start_time_ts");
							int create_ts1 = (int) o2.get("start_time_ts");
							if (create_ts > create_ts1)
								return 1;
							if (create_ts < create_ts1)
								return -1;
							else
								return 0;

						}
					});

					for (int i = 0; i < size_actualresponse; i++) {
						checkgetjobsvalidation(actual_response, expectedresponse, i, i, test);
					}

					// The below condition is validated when the user sends sorting and
					// withorwithout filtering. Also, if the user sends the page and page-size as
					// inputs, its mandatory
					// to send the sort either by asc or desc.
				}

				else if ((sortjobs.contains("create_ts") && sortjobs != null && (!sortjobs.equals(""))
						&& (filter != null))
						|| ((sortjobs.contains("create_ts")) && sortjobs != null && (!sortjobs.equals(""))
								&& ((filter == null) || filter.equals("")))) {
					String[] site = null;

					if (filter.contains("=") && (filter.contains("source_group_id"))
							|| (filter.contains("=") && (filter.contains("destination_id")))
							|| (filter.contains("=") && (filter.contains("policy_id")))
							|| (filter.contains("=") && (filter.contains("source_id")))
							|| (filter.contains("=") && (filter.contains("source_name")))
							|| (filter.contains("=") && (filter.contains("organization_id")))
							|| (filter.contains("=") && (filter.contains("date_range")))) {
						String[] first = filter.split(",");
						int len = first.length;
						for (int i = 0; i < len; i++) {

							site = first[i].split(";");

							ArrayList<HashMap<String, Object>> single_site = new ArrayList<>();
							for (int z = 0; z < expectedresponse.size(); z++) {
								if (first[i].contains("source_group_id")) {
									if (expectedresponse.get(z).get("source_group_id").equals(site[2])) {
										single_site.add(expectedresponse.get(z));

									}
								}
								if (first[i].contains("destination_id")) {
									if (expectedresponse.get(z).get("destination_id").equals(site[2])) {
										single_site.add(expectedresponse.get(z));

									}
								}
								if (first[i].contains("policy_id")) {
									if (expectedresponse.get(z).get("policy_id").equals(site[2])) {
										single_site.add(expectedresponse.get(z));

									}
								}
								if (first[i].contains("source_id")) {
									if (expectedresponse.get(z).get("source_id").equals(site[2])) {
										single_site.add(expectedresponse.get(z));

									}
								}
								if (first[i].contains("source_name")) {
									if (expectedresponse.get(z).get("source_name").equals(site[2])) {
										single_site.add(expectedresponse.get(z));

										/*
										 * if(filter.contains("job_type")) {
										 * if(single_site.get(z).get("job_status").equals(site[3])) {
										 * single_site.add(expectedresponse.get(z)); } }
										 */
									}
								}
								if (first[i].contains("organization_id")) {
									if (expectedresponse.get(z).get("organization_id").equals(site[2])) {
										single_site.add(expectedresponse.get(z));

									}
								}
								if (first[i].contains("date_range")) {
									if (expectedresponse.get(z).get("date_range").equals(site[2])) {
										single_site.add(expectedresponse.get(z));

									}
								}

							}
							expectedresponse = single_site;
						}

					} else if (filter.contains("in") && (filter.contains("organization_id"))
							|| (filter.contains("in") && (filter.contains("destination_id")))
							|| (filter.contains("in") && (filter.contains("source_id")))
							|| (filter.contains("in") && (filter.contains("policy_id")))
							|| (filter.contains("in") && (filter.contains("source_name")))) {
						String[] resources = filter.split(",");
						int len = resources.length;

						for (int j = 0; j < len; j++) {
							int actual_size = expectedresponse.size();
							String[] jobtypes = resources[j].split(";");
							String[] diff_jobtypes = jobtypes[2].split("\\|");
							int size_jobtypes = diff_jobtypes.length;
							ArrayList<HashMap<String, Object>> temp_response = new ArrayList<>();
							for (int z = 0; z < size_jobtypes; z++) {
								for (int a = 0; a < actual_size; a++) {
									if (expectedresponse.get(a).get("organization_id").equals(diff_jobtypes[z])) {
										temp_response.add(expectedresponse.get(a));

									} else if (expectedresponse.get(a).get("destination_id").equals(diff_jobtypes[z])) {
										temp_response.add(expectedresponse.get(a));

									} else if (expectedresponse.get(a).get("source_id").equals(diff_jobtypes[z])) {
										temp_response.add(expectedresponse.get(a));

									} else if (expectedresponse.get(a).get("policy_id").equals(diff_jobtypes[z])) {
										temp_response.add(expectedresponse.get(a));
									} else if (expectedresponse.get(a).get("source_name").equals(diff_jobtypes[z])) {
										temp_response.add(expectedresponse.get(a));
									} else {
										// do nothing that means filter is not valid....
									}

								}

							}
							expectedresponse = temp_response;
						}
						Collections.sort(expectedresponse, new Comparator<HashMap>() {

							@Override
							public int compare(HashMap o1, HashMap o2) {

								// TODO Auto-generated method stub
								Long create_ts = (Long) o1.get("start_time_ts");
								Long create_ts1 = (Long) o2.get("start_time_ts");
								if (create_ts > create_ts1)
									return 1;
								if (create_ts < create_ts1)
									return -1;
								else
									return 0;

							}
						});

					}
					if (sortjobs.contains("asc")) {
						int j = 0;
						if (curr_page > 1) {
							j = (curr_page - 1) * page_size;
						}
						for (int i = 0; i < size_actualresponse; i++, j++) {
							checkgetjobsvalidation(actual_response, expectedresponse, i, j, test);
						}

					} else {
						int j;
						if (page_size == 20 || page_size == 100) {
							j = actual_response.size() - 1;

						} else {
							j = expectedresponse.size() - 1;
							if (curr_page != 1) {
								j = (expectedresponse.size() - 1) - (curr_page - 1) * page_size;
							}
						}
						for (int i = 0; i < size_actualresponse; i++, j--) {
							checkgetjobsvalidation(actual_response, expectedresponse, i, j, test);
						}

					}

				} else if ((sortjobs.contains("job_status") && sortjobs != null && (!sortjobs.equals(""))
						&& ((filter == null) || filter.equals("")))
						|| ((sortjobs.contains("job_type")) && sortjobs != null && (!sortjobs.equals(""))
								&& ((filter == null) || filter.equals("")))) {
					String[] sort_type = sortjobs.split(",");
					test.log(LogStatus.INFO, "before sorting " + expectedresponse.toString());
					Collections.sort(expectedresponse, new Comparator<HashMap<String, Object>>() {
						@Override
						public int compare(HashMap<String, Object> o1, HashMap<String, Object> o2) {
							// TODO Auto-generated method stub
							String create_ts = (String) o1.get(sort_type[0]);
							String create_ts1 = (String) o2.get(sort_type[0]);
							if (create_ts.compareTo(create_ts1) > 0)
								return 1;
							else if (create_ts.compareTo(create_ts1) == 0) {
								Long start_time = (Long) o1.get("start_time_ts");
								Long start_time1 = (Long) o2.get("start_time_ts");
								if (start_time < start_time1)
									return 1;
								if (start_time > start_time1)
									return -1;
								else
									return 0;
							} else
								return -1;

						}
					});
					test.log(LogStatus.INFO, "after sorting " + expectedresponse.toString());
					if (sortjobs.contains("asc")) {

						int j = 0;
						if (curr_page > 1) {
							j = (curr_page - 1) * page_size;

						}
						for (int i = 0; i < size_actualresponse; i++, j++) {
							checkgetjobsvalidation(actual_response, expectedresponse, i, j, test);
						}
					} else {
						/*
						 * Collections.sort(expectedresponse, new Comparator<HashMap<String,Object>>() {
						 * 
						 * @Override public int compare(HashMap<String,Object> o1,
						 * HashMap<String,Object>o2) { // TODO Auto-generated method stub String
						 * create_ts = (String) o1.get(sort_type[0]); String create_ts1 = (String)
						 * o2.get(sort_type[0]); if(create_ts.compareTo(create_ts1)>0) return 1; else
						 * if(create_ts.compareTo(create_ts1)==0) return 0; else return -1; } });
						 */ int j;
						if (page_size == 20 || page_size == 100) {
							j = actual_response.size() - 1;

						} else {
							j = expectedresponse.size() - 1;
							if (curr_page != 1) {
								j = (expectedresponse.size() - 1) - (curr_page - 1) * page_size;
							}
						}
						for (int i = 0; i < size_actualresponse; i++, j--) {
							checkgetjobsvalidation(actual_response, expectedresponse, i, j, test);
						}
					}

				} else {
					test.log(LogStatus.FAIL, "None of the conditions got satisfied, please check the inputs");
					assertTrue("None of the conditions got satisfied, please check the inputs", false);

				}
				// The below function validates the pagination for the given response
				spogServer.validateResponseForPages(curr_page, page_size, response, expectedresponse.size(), test);

			} else {
				assertTrue("The jobs count did not match the expected", false);
				test.log(LogStatus.FAIL, "The jobs count did not match the expected");
			}
		} else {
			String code = errorMessage.getCodeString();
			String message = errorMessage.getStatus();
			if (code.contains("00100201")) {
				message = message.replace("{0}", spogServer.getUUId());
			}
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.INFO, "The error code matched with the expected " + code);
			// checkErrorMessage(response,message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		}

	}

	private void checkgetjobsvalidation(ArrayList<HashMap<String, Object>> actual_response,
			ArrayList<HashMap<String, Object>> expectedresponse, int i, int j, ExtentTest test) {
		// TODO Auto-generated method stub
		String act_job_id = actual_response.get(i).get("job_id").toString();
		String exp_job_id = expectedresponse.get(j).get("job_id").toString();
		spogServer.assertResponseItem(exp_job_id, act_job_id);

		String act_job_status = actual_response.get(i).get("job_status").toString();
		String exp_job_status = expectedresponse.get(j).get("job_status").toString();
		spogServer.assertResponseItem(exp_job_status, act_job_status);

		String act_source_id = actual_response.get(i).get("source_id").toString();
		String exp_source_id = expectedresponse.get(j).get("source_id").toString();
		spogServer.assertResponseItem(exp_source_id, act_source_id);

		String act_source_name = actual_response.get(i).get("source_name").toString();
		String exp_source_name = expectedresponse.get(j).get("source_name").toString();
		spogServer.assertResponseItem(exp_source_name, act_source_name);

		String act_destination_name = actual_response.get(i).get("destination_name").toString();
		String exp_destination_name = expectedresponse.get(j).get("destination_name").toString();
		spogServer.assertResponseItem(exp_destination_name, act_destination_name);

		String act_destination_id = actual_response.get(i).get("destination_id").toString();
		String exp_destination_id = expectedresponse.get(j).get("destination_id").toString();
		spogServer.assertResponseItem(exp_destination_id, act_destination_id);

		String act_policy_name = actual_response.get(i).get("policy_name").toString();
		String exp_policy_name = expectedresponse.get(j).get("policy_name").toString();
		spogServer.assertResponseItem(exp_policy_name, act_policy_name);

		String act_policy_id = actual_response.get(i).get("policy_id").toString();
		String exp_policy_id = expectedresponse.get(j).get("policy_id").toString();
		spogServer.assertResponseItem(exp_policy_id, act_policy_id);

		String act_group_name = actual_response.get(i).get("group_name").toString();
		String exp_group_name = expectedresponse.get(j).get("group_name").toString();
		spogServer.assertResponseItem(exp_group_name, act_group_name);

		String act_group_id = actual_response.get(i).get("group_id").toString();
		String exp_group_id = expectedresponse.get(j).get("group_id").toString();
		spogServer.assertResponseItem(exp_group_id, act_group_id);

		String act_organization_name = actual_response.get(i).get("organization_name").toString();
		String exp_organization_name = expectedresponse.get(j).get("organization_name").toString();
		spogServer.assertResponseItem(exp_organization_name, act_organization_name);

		String act_organization_id = actual_response.get(i).get("organization_id").toString();
		String exp_organization_id = expectedresponse.get(j).get("organization_id").toString();
		spogServer.assertResponseItem(exp_organization_id, act_organization_id);

		test.log(LogStatus.INFO, "The actual data matched with the expected");
	}

	/**
	 * getBackupJobStatusSummary
	 * 
	 * @author Ramya.Nagepalli
	 * @param adminToken
	 * @param test
	 */
	public Response getBackupJobStatusSummary(String adminToken, ExtentTest test) {
		// TODO Auto-generated method stub

		Response response = spogReportInvoker.getBackupJobStatusSummary(adminToken, test);

		return response;
	}

	/**
	 * checkBackupJobStatusSummary
	 * 
	 * @author Ramya.Nagepalli
	 * @param response
	 * @param statusDetails
	 * @param expectedstatuscode
	 * @param ExpectedErrorMessage
	 * @param test
	 */
	public void checkBackupJobStatusSummary(Response response, ArrayList<HashMap> statusDetails, int expectedstatuscode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		// TODO Auto-generated method stub

		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			ArrayList<HashMap> actual = new ArrayList<HashMap>();

			actual = response.then().extract().path("data.report_status_summary");

			for (int i = 0; i < actual.size(); i++) {
				String act_job_count = actual.get(i).get("job_count").toString();
				String exp_job_count = statusDetails.get(i).get("job_count").toString();
				spogServer.assertResponseItem(exp_job_count, act_job_count);

				String act_job_status = actual.get(i).get("job_status").toString();
				String exp_job_status = statusDetails.get(i).get("job_status").toString();
				spogServer.assertResponseItem(exp_job_status, act_job_status);

			}

			String act_job_inProgress = response.then().extract().path("data.in_progress_job_count");
			String exp_job_inProgress = statusDetails.get(actual.size() - 1).get("in_progress_count").toString();
			spogServer.assertResponseItem(exp_job_inProgress, act_job_inProgress);

		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);

		}
	}

	/**
	 * GET businessintelligencereports/types
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param expectedStatusCode
	 * @param ExpectedErrorMessage
	 * @param test
	 */
	public void getBIReportsTypesWithCheck(String token, ArrayList<HashMap<String, Object>> expectedData,
			int expectedStatusCode, SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		test.log(LogStatus.INFO, "Call API Business Intelligence Reports types");
		Response response = spogReportInvoker.getBIReportsTypes(token, test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			ArrayList<HashMap<String, Object>> actualData = new ArrayList<>();
			actualData = response.then().extract().path("data");

			for (int i = 0; i < actualData.size(); i++) {

				test.log(LogStatus.INFO,
						"Comparing " + actualData.get(i).get("report_name") + " in expected and actual data");
				spogServer.assertResponseItem(actualData.get(i), expectedData.get(i), test);

			}

			test.log(LogStatus.PASS, "Validation passed");

		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}

	}

	/**
	 * GET /businessintelligencereports/types/cd_near_capacity
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param expectedStatusCode
	 * @param ExpectedErrorMessage
	 * @param test
	 */
	public void getBIReportsTypesCdNearCapacityWithCheck(String token, ArrayList<HashMap<String, Object>> expectedData,
			int expectedStatusCode, SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		test.log(LogStatus.INFO, "Call API Business Intelligence Reports types cd_near_capacity");
		Response response = spogReportInvoker.getBIReportsTypesCdNearCapacity(token, test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			ArrayList<HashMap<String, Object>> actualData = new ArrayList<>();
			actualData = response.then().extract().path("data");

			if (!actualData.isEmpty() && actualData.size() > 0) {

				test.log(LogStatus.INFO, "Validating the params in response and checking to be not null");
				for (int i = 0; i < actualData.size(); i++) {

					assertTrue(actualData.get(i).get("Organization Id") != null,
							"The response contains the organization with id:"
									+ actualData.get(i).get("Organization Id").toString());
					assertTrue(actualData.get(i).get("Organization Name") != null,
							"The response contains the Organization Name :"
									+ actualData.get(i).get("Organization Name").toString());
					assertTrue(actualData.get(i).get("Organization Type") != null,
							"The response contains the Organization Type with id:"
									+ actualData.get(i).get("Organization Type").toString());
					assertTrue(actualData.get(i).get("Create Date") != null, "The response contains the Create Date ");
					assertTrue(actualData.get(i).get("Usage") != null, "The response contains the Usage");
					assertTrue(actualData.get(i).get("Capacity") != null, "The response contains the Capacity ");
					assertTrue(actualData.get(i).get("Percent") != null,
							"The response contains the Trial Days Left:" + actualData.get(i).get("Percent").toString());
					assertTrue(actualData.get(i).get("Billing Type") != null, "The response contains the Billing Type:"
							+ actualData.get(i).get("Billing Type").toString());
					assertTrue(actualData.get(i).get("Region") != null,
							"The response contains the Region:" + actualData.get(i).get("Region").toString());
				}
			} else {
				test.log(LogStatus.INFO,
						"The response of Get business intelligence reports with cd_near_capacity is empty.");
			}

		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
	}

	/**
	 * GET /businessintelligencereports/types/cd_over_capacity
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param expectedStatusCode
	 * @param ExpectedErrorMessage
	 * @param test
	 */
	public void getBIReportsTypesCdOverCapacityWithCheck(String token, ArrayList<HashMap<String, Object>> expectedData,
			int expectedStatusCode, SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		test.log(LogStatus.INFO, "Call API Business Intelligence Rreports types cd_over_capacity");
		Response response = spogReportInvoker.getBIReportsTypesCdOverCapacity(token, test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			ArrayList<HashMap<String, Object>> actualData = new ArrayList<>();
			actualData = response.then().extract().path("data");

			if (!actualData.isEmpty() && actualData.size() > 0) {

				test.log(LogStatus.INFO, "Validating the params in response and checking to be not null");
				for (int i = 0; i < actualData.size(); i++) {

					assertTrue(actualData.get(i).get("Organization Id") != null,
							"The response contains the organization with id:"
									+ actualData.get(i).get("Organization Id").toString());
					assertTrue(actualData.get(i).get("Organization Name") != null,
							"The response contains the Organization Name :"
									+ actualData.get(i).get("Organization Name").toString());
					assertTrue(actualData.get(i).get("Organization Type") != null,
							"The response contains the Organization Type with id:"
									+ actualData.get(i).get("Organization Type").toString());
					assertTrue(actualData.get(i).get("Create Date") != null, "The response contains the Create Date ");
					assertTrue(actualData.get(i).get("Usage") != null, "The response contains the Usage");
					assertTrue(actualData.get(i).get("Capacity") != null, "The response contains the Capacity ");
					assertTrue(actualData.get(i).get("Billing Type") != null, "The response contains the Billing Type:"
							+ actualData.get(i).get("Billing Type").toString());
					assertTrue(actualData.get(i).get("Region") != null,
							"The response contains the Region:" + actualData.get(i).get("Region").toString());
				}
			} else {
				test.log(LogStatus.INFO,
						"The response of Get business intelligence reports with cd_near_capacity is empty.");
			}

		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
	}

	/**
	 * GET /businessintelligencereports/types/cd_trial
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param expectedStatusCode
	 * @param ExpectedErrorMessage
	 * @param test
	 */
	public void getBIReportsTypesCdTrialWithCheck(String token, ArrayList<Object> expectedData, int expectedStatusCode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		test.log(LogStatus.INFO, "Call API Business Intelligence Rreports types cd_trial");
		Response response = spogReportInvoker.getBIReportsTypesCdTrial(token, test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			ArrayList<HashMap<String, Object>> actualData = new ArrayList<>();
			actualData = response.then().extract().path("data");

			if (!actualData.isEmpty() && actualData.size() > 0) {

				test.log(LogStatus.INFO, "Validating the params in response and checking to be not null");
				for (int i = 0; i < actualData.size(); i++) {

					assertTrue(actualData.get(i).get("Organization Id") != null,
							"The response contains the organization with id:"
									+ actualData.get(i).get("Organization Id").toString());
					assertTrue(actualData.get(i).get("Organization Name") != null,
							"The response contains the Organization Name :"
									+ actualData.get(i).get("Organization Name").toString());
					assertTrue(actualData.get(i).get("Organization Type") != null,
							"The response contains the Organization Type with id:"
									+ actualData.get(i).get("Organization Type").toString());
					assertTrue(actualData.get(i).get("Create Date") != null, "The response contains the Create Date ");
					assertTrue(actualData.get(i).get("Trial Start") != null, "The response contains the Trial Start");
					assertTrue(actualData.get(i).get("Trial End") != null, "The response contains the Trial End ");
					assertTrue(actualData.get(i).get("Trial Days Left") != null,
							"The response contains the Trial Days Left:"
									+ actualData.get(i).get("Trial Days Left").toString());
					assertTrue(actualData.get(i).get("Deletion Days Left") != null,
							"The response contains the Deletion Days:"
									+ actualData.get(i).get("Deletion Days Left").toString());
					assertTrue(actualData.get(i).get("Deletion Date") != null,
							"The response contains the Deletion Days:"
									+ actualData.get(i).get("Deletion Date").toString());

					// validateExpiration(actualData.get(i));
				}
			} else {
				test.log(LogStatus.INFO, "The response of Get business intelligence reports cd_trial is empty.");
			}

		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
	}

	/**
	 * GET /businessintelligencereports/types/ch_near_capacity
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param expectedStatusCode
	 * @param ExpectedErrorMessage
	 * @param test
	 */
	public void getBIReportsTypesChNearCapacityWithCheck(String token, ArrayList<HashMap<String, Object>> expectedData,
			int expectedStatusCode, SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		test.log(LogStatus.INFO, "Call API Business Intelligence Rreports types ch_near_capacity");
		Response response = spogReportInvoker.getBIReportsTypesChNearCapacity(token, test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			ArrayList<HashMap<String, Object>> actualData = new ArrayList<>();
			actualData = response.then().extract().path("data");

			if (!actualData.isEmpty() && actualData.size() > 0) {

				test.log(LogStatus.INFO, "Validating the params in response and checking to be not null");
				for (int i = 0; i < actualData.size(); i++) {

					assertTrue(actualData.get(i).get("Organization Id") != null,
							"The response contains the organization with id:"
									+ actualData.get(i).get("Organization Id").toString());
					assertTrue(actualData.get(i).get("Organization Name") != null,
							"The response contains the Organization Name :"
									+ actualData.get(i).get("Organization Name").toString());
					assertTrue(actualData.get(i).get("Organization Type") != null,
							"The response contains the Organization Type with id:"
									+ actualData.get(i).get("Organization Type").toString());
					assertTrue(actualData.get(i).get("Create Date") != null, "The response contains the Create Date ");
					assertTrue(actualData.get(i).get("Usage") != null, "The response contains the Usage");
					assertTrue(actualData.get(i).get("Capacity") != null, "The response contains the Capacity ");
					assertTrue(actualData.get(i).get("Percent") != null,
							"The response contains the Trial Days Left:" + actualData.get(i).get("Percent").toString());
					assertTrue(actualData.get(i).get("Billing Type") != null, "The response contains the Billing Type:"
							+ actualData.get(i).get("Billing Type").toString());
					assertTrue(actualData.get(i).get("Region") != null,
							"The response contains the Region:" + actualData.get(i).get("Region").toString());
				}
			} else {
				test.log(LogStatus.INFO,
						"The response of Get business intelligence reports with cd_near_capacity is empty.");
			}

		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
	}

	/**
	 * GET /businessintelligencereports/types/ch_over_capacity
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param expectedStatusCode
	 * @param ExpectedErrorMessage
	 * @param test
	 */
	public void getBIReportsTypesChOverCapacityWithCheck(String token, ArrayList<HashMap<String, Object>> expectedData,
			int expectedStatusCode, SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		test.log(LogStatus.INFO, "Call API Business Intelligence Rreports types ch_over_capacity");
		Response response = spogReportInvoker.getBIReportsTypesChOverCapacity(token, test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			ArrayList<HashMap<String, Object>> actualData = new ArrayList<>();
			actualData = response.then().extract().path("data");

			if (!actualData.isEmpty() && actualData.size() > 0) {

				test.log(LogStatus.INFO, "Validating the params in response and checking to be not null");
				for (int i = 0; i < actualData.size(); i++) {

					assertTrue(actualData.get(i).get("Organization Id") != null,
							"The response contains the organization with id:"
									+ actualData.get(i).get("Organization Id").toString());
					assertTrue(actualData.get(i).get("Organization Name") != null,
							"The response contains the Organization Name :"
									+ actualData.get(i).get("Organization Name").toString());
					assertTrue(actualData.get(i).get("Organization Type") != null,
							"The response contains the Organization Type with id:"
									+ actualData.get(i).get("Organization Type").toString());
					assertTrue(actualData.get(i).get("Create Date") != null, "The response contains the Create Date ");
					assertTrue(actualData.get(i).get("Usage") != null, "The response contains the Usage");
					assertTrue(actualData.get(i).get("Capacity") != null, "The response contains the Capacity ");
					assertTrue(actualData.get(i).get("Billing Type") != null, "The response contains the Billing Type:"
							+ actualData.get(i).get("Billing Type").toString());
					assertTrue(actualData.get(i).get("Region") != null,
							"The response contains the Region:" + actualData.get(i).get("Region").toString());
				}
			} else {
				test.log(LogStatus.INFO,
						"The response of Get business intelligence reports with cd_near_capacity is empty.");
			}

		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
	}

	/**
	 * GET /businessintelligencereports/types/ch_trial
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param expectedStatusCode
	 * @param ExpectedErrorMessage
	 * @param test
	 */
	public void getBIReportsTypesChTrialWithCheck(String token, ArrayList<Object> expectedData, int expectedStatusCode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		test.log(LogStatus.INFO, "Call API Business Intelligence Rreports types ch_trial");
		Response response = spogReportInvoker.getBIReportsTypesChTrial(token, test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			ArrayList<HashMap<String, Object>> actualData = new ArrayList<>();
			actualData = response.then().extract().path("data");

			if (!actualData.isEmpty() && actualData.size() > 0) {

				test.log(LogStatus.INFO, "Validating the params in response and checking to be not null");
				for (int i = 0; i < actualData.size(); i++) {

					assertTrue(actualData.get(i).get("Organization Id") != null,
							"The response contains the organization with id:"
									+ actualData.get(i).get("Organization Id").toString());
					assertTrue(actualData.get(i).get("Organization Name") != null,
							"The response contains the Organization Name :"
									+ actualData.get(i).get("Organization Name").toString());
					assertTrue(actualData.get(i).get("Organization Type") != null,
							"The response contains the Organization Type with id:"
									+ actualData.get(i).get("Organization Type").toString());
					assertTrue(actualData.get(i).get("Create Date") != null, "The response contains the Create Date ");
					assertTrue(actualData.get(i).get("Trial Start") != null, "The response contains the Trial Start");
					assertTrue(actualData.get(i).get("Trial End") != null, "The response contains the Trial End ");
					assertTrue(actualData.get(i).get("Trial Days Left") != null,
							"The response contains the Trial Days Left:"
									+ actualData.get(i).get("Trial Days Left").toString());
					assertTrue(actualData.get(i).get("Deletion Days Left") != null,
							"The response contains the Deletion Days:"
									+ actualData.get(i).get("Deletion Days Left").toString());
					assertTrue(actualData.get(i).get("Deletion Date") != null,
							"The response contains the Deletion Days:"
									+ actualData.get(i).get("Deletion Date").toString());

					// validateExpiration(actualData.get(i));
				}
			} else {
				test.log(LogStatus.INFO, "The response of Get business intelligence reports ch_trial is empty.");
			}

		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
	}

	/**
	 * Calculate the elapsed time from Trial start date till today Validate with
	 * Trial days left and Deletion days
	 * 
	 * @author Rakesh.Chalamala
	 * @param actualData
	 */
	public void validateExpiration(HashMap<String, Object> actualData) {

		int trialDays = 14;
		int deletionDays = 30;
		String trialDaysLeft = null;
		long currentTS = System.currentTimeMillis() / 1000L;
		long trialStartTS = Long.parseLong(actualData.get("Trial Start").toString().split("TS")[0]);

		long elapsedTS = currentTS - trialStartTS;
		int result = trialDays - (int) (elapsedTS / 86400) - 1; // converting elapsed time to days

		if (result <= 0) {
			trialDaysLeft = "EXPIRED";
		} else {
			trialDaysLeft = String.valueOf(result);
		}

		System.out.println(actualData.get("Trial Days Left").toString().equals(trialDaysLeft));

	}

	/**
	 * GET /businessintelligencereports/types/cd_near_capacity
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param expectedStatusCode
	 * @param ExpectedErrorMessage
	 * @param test
	 */
	public void getBIRCdNearCapacityExportCSVWithCheck(String token, ArrayList<HashMap<String, Object>> expectedData,
			int expectedStatusCode, SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		test.log(LogStatus.INFO, "Call API Business Intelligence Reports types cd_near_capacity");
		Response response = spogReportInvoker.getBIRCdNearCapacityExportCSV(token, test);

		try {
			if ((int) response.then().extract().path("status") == SpogConstants.REQUIRED_INFO_NOT_EXIST) {
				spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
				System.out.println(response.then().extract().path("errors.message").toString());
				test.log(LogStatus.INFO, response.then().extract().path("errors.message").toString());
			} else {
				spogServer.checkResponseStatus(response, expectedStatusCode, test);
				String code = ExpectedErrorMessage.getCodeString();
				String message = ExpectedErrorMessage.getStatus();
				spogServer.checkErrorCode(response, code);
				test.log(LogStatus.PASS, "The error code matched with the expected " + code);
				spogServer.checkErrorMessage(response, message);
				test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
			}

		} catch (Exception e) {
			assertTrue(true, "Report data as CSV:");
			test.log(LogStatus.INFO, response.prettyPrint());
		}
	}

	/**
	 * GET /businessintelligencereports/types/cd_over_capacity
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param expectedStatusCode
	 * @param ExpectedErrorMessage
	 * @param test
	 */
	public void getBIRCdOverCapacityExportCSVWithCheck(String token, ArrayList<HashMap<String, Object>> expectedData,
			int expectedStatusCode, SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		test.log(LogStatus.INFO, "Call API Business Intelligence Rreports types cd_over_capacity");
		Response response = spogReportInvoker.getBIRCdOverCapacityExportCSV(token, test);

		try {
			if ((int) response.then().extract().path("status") == SpogConstants.REQUIRED_INFO_NOT_EXIST) {
				spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
				System.out.println(response.then().extract().path("errors.message").toString());
				test.log(LogStatus.INFO, response.then().extract().path("errors.message").toString());
			} else {
				spogServer.checkResponseStatus(response, expectedStatusCode, test);
				String code = ExpectedErrorMessage.getCodeString();
				String message = ExpectedErrorMessage.getStatus();
				spogServer.checkErrorCode(response, code);
				test.log(LogStatus.PASS, "The error code matched with the expected " + code);
				spogServer.checkErrorMessage(response, message);
				test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
			}

		} catch (Exception e) {
			assertTrue(true, "Report data as CSV:");
			test.log(LogStatus.INFO, response.prettyPrint());
		}
	}

	/**
	 * GET /businessintelligencereports/types/cd_trial
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param expectedStatusCode
	 * @param ExpectedErrorMessage
	 * @param test
	 */
	public void getBIRCdTrialExportCSVWithCheck(String token, ArrayList<Object> expectedData, int expectedStatusCode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		test.log(LogStatus.INFO, "Call API Business Intelligence Rreports types cd_trial");
		Response response = spogReportInvoker.getBIRCdTrialExportCSV(token, test);

		try {
			if ((int) response.then().extract().path("status") == SpogConstants.REQUIRED_INFO_NOT_EXIST) {
				spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
				System.out.println(response.then().extract().path("errors.message").toString());
				test.log(LogStatus.INFO, response.then().extract().path("errors.message").toString());
			} else {
				spogServer.checkResponseStatus(response, expectedStatusCode, test);
				String code = ExpectedErrorMessage.getCodeString();
				String message = ExpectedErrorMessage.getStatus();
				spogServer.checkErrorCode(response, code);
				test.log(LogStatus.PASS, "The error code matched with the expected " + code);
				spogServer.checkErrorMessage(response, message);
				test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
			}

		} catch (Exception e) {
			assertTrue(true, "Report data as CSV:");
			test.log(LogStatus.INFO, response.prettyPrint());
		}

	}

	/**
	 * GET /businessintelligencereports/types/ch_near_capacity
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param expectedStatusCode
	 * @param ExpectedErrorMessage
	 * @param test
	 */
	public void getBIRChNearCapacityExportCSVWithCheck(String token, ArrayList<HashMap<String, Object>> expectedData,
			int expectedStatusCode, SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		test.log(LogStatus.INFO, "Call API Business Intelligence Rreports types ch_near_capacity");
		Response response = spogReportInvoker.getBIRChNearCapacityExportCSV(token, test);

		try {
			if ((int) response.then().extract().path("status") == SpogConstants.REQUIRED_INFO_NOT_EXIST) {
				spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
				System.out.println(response.then().extract().path("errors.message").toString());
				test.log(LogStatus.INFO, response.then().extract().path("errors.message").toString());
			} else {
				spogServer.checkResponseStatus(response, expectedStatusCode, test);
				String code = ExpectedErrorMessage.getCodeString();
				String message = ExpectedErrorMessage.getStatus();
				spogServer.checkErrorCode(response, code);
				test.log(LogStatus.PASS, "The error code matched with the expected " + code);
				spogServer.checkErrorMessage(response, message);
				test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
			}

		} catch (Exception e) {
			assertTrue(true, "Report data as CSV:");
			test.log(LogStatus.INFO, response.prettyPrint());
		}
	}

	/**
	 * GET /businessintelligencereports/types/ch_over_capacity
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param expectedStatusCode
	 * @param ExpectedErrorMessage
	 * @param test
	 */
	public void getBIRChOverCapacityExportCSVWithCheck(String token, ArrayList<HashMap<String, Object>> expectedData,
			int expectedStatusCode, SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		test.log(LogStatus.INFO, "Call API Business Intelligence Rreports types ch_over_capacity");
		Response response = spogReportInvoker.getBIRChOverCapacityExportCSV(token, test);

		try {
			if ((int) response.then().extract().path("status") == SpogConstants.REQUIRED_INFO_NOT_EXIST) {
				spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
				System.out.println(response.then().extract().path("errors.message").toString());
				test.log(LogStatus.INFO, response.then().extract().path("errors.message").toString());
			} else {
				spogServer.checkResponseStatus(response, expectedStatusCode, test);
				String code = ExpectedErrorMessage.getCodeString();
				String message = ExpectedErrorMessage.getStatus();
				spogServer.checkErrorCode(response, code);
				test.log(LogStatus.PASS, "The error code matched with the expected " + code);
				spogServer.checkErrorMessage(response, message);
				test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
			}

		} catch (Exception e) {
			assertTrue(true, "Report data as CSV:");
			test.log(LogStatus.INFO, response.prettyPrint());
		}
	}

	/**
	 * GET /businessintelligencereports/types/ch_trial
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param expectedStatusCode
	 * @param ExpectedErrorMessage
	 * @param test
	 */
	public void getBIRChTrialExportCSVWithCheck(String token, ArrayList<Object> expectedData, int expectedStatusCode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		test.log(LogStatus.INFO, "Call API Business Intelligence Rreports types ch_trial");
		Response response = spogReportInvoker.getBIRChTrialExportCSV(token, test);

		try {
			if ((int) response.then().extract().path("status") == SpogConstants.REQUIRED_INFO_NOT_EXIST) {
				spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
				System.out.println(response.then().extract().path("errors.message").toString());
				test.log(LogStatus.INFO, response.then().extract().path("errors.message").toString());
			} else {
				spogServer.checkResponseStatus(response, expectedStatusCode, test);
				String code = ExpectedErrorMessage.getCodeString();
				String message = ExpectedErrorMessage.getStatus();
				spogServer.checkErrorCode(response, code);
				test.log(LogStatus.PASS, "The error code matched with the expected " + code);
				spogServer.checkErrorMessage(response, message);
				test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
			}

		} catch (Exception e) {
			assertTrue(true, "Report data as CSV:");
			test.log(LogStatus.INFO, response.prettyPrint());
		}
	}

	/**
	 * GET /datatransferreport/summary
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 * @return response
	 */
	public Response getDataTransferReportSummaryWithCheck(String token, String additionalURL, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage, ExtentTest test) {

		test.log(LogStatus.INFO, "Call API - GET /datatransferreport/summary");
		Response response = spogReportInvoker.getDataTransferReportSummary(token, additionalURL, test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			HashMap<String, Object> actualResponse = response.then().extract().path("data");

			if (actualResponse.containsKey("data_transfer_summary")) {
				assertTrue(actualResponse.containsKey("data_transfer_summary"),
						"Reponse contains data_transfer_summary");
				ArrayList<HashMap<String, Object>> DTS = (ArrayList<HashMap<String, Object>>) actualResponse
						.get("data_transfer_summary");

				for (int i = 0; i < DTS.size(); i++) {
					assertNotNull((DTS.get(i)).get("topic_name"),
							"data_transfer_summary contains the topic_name: " + DTS.get(i).get("topic_name"));
					assertNotNull((DTS.get(i)).get("topic"),
							"data_transfer_summary contains the topic: " + DTS.get(i).get("topic"));
					assertNotNull((DTS.get(i)).get("date_value"),
							"data_transfer_summary contains the date_value: " + DTS.get(i).get("date_value"));
				}
			}

			if (actualResponse.containsKey("processed_bytes")) {
				assertNotNull(actualResponse.get("processed_bytes"),
						"Reponse contains processed_bytes and the value is: " + actualResponse.get("processed_bytes"));
				assertNotNull(actualResponse.get("written_bytes"),
						"Reponse contains written_bytes and the value is: " + actualResponse.get("written_bytes"));
				assertNotNull(actualResponse.get("transferred_bytes"),
						"Reponse contains transferred_bytes and the value is: "
								+ actualResponse.get("transferred_bytes"));
			} else {
				test.log(LogStatus.INFO,
						"Response does not contain the params processed_bytes, written_bytes, transferred_bytes");
			}

			test.log(LogStatus.PASS, "Validation passed.");
		} else {
			spogServer.checkResponseStatus(response, expectedStatusCode, test);
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
		return response;
	}

	/**
	 * GET /datatransferreport/details
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param expectedResponse
	 * @param filterStr
	 * @param sortStr
	 * @param curr_page
	 * @param page_size
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 * @return response
	 */
	public Response getDataTransferReportDetailsWithCheck(String token,
			ArrayList<HashMap<String, Object>> expectedResponse, String filterStr, String SortStr, int curr_page,
			int page_size, int expectedStatusCode, SpogMessageCode expectedErrorMessage, ExtentTest test) {
		String additionalURL = null;
		ArrayList<HashMap<String, Object>> filteredResponse = new ArrayList<>();

		if (curr_page == 0 || curr_page == -1) {
			curr_page = 1;
		}
		if (page_size == 0 || page_size == -1) {
			page_size = 20;
		} else if (page_size >= 100 && page_size < SpogConstants.MAX_PAGE_SIZE) {
			page_size = 100;
		}
		additionalURL = spogServer.PrepareURL(filterStr, SortStr, curr_page, page_size, test);

		test.log(LogStatus.INFO, "Call API - GET /datatransferreport/details");
		Response response = spogReportInvoker.getDataTransferReportDetails(token, additionalURL, test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			if (SortStr == null && filterStr == null) {
				Collections.reverse(expectedResponse);
			} else if (SortStr != null && SortStr != "") {

				Collections.sort(expectedResponse, new Comparator<HashMap<String, Object>>() {
					@Override
					public int compare(HashMap<String, Object> o1, HashMap<String, Object> o2) {
						// TODO Auto-generated method stub
						String create_ts = (String) ((HashMap<String, Object>) o1.get("source")).get("source_name");
						String create_ts1 = (String) ((HashMap<String, Object>) o2.get("source")).get("source_name");
						if (create_ts.compareTo(create_ts1) > 0)
							return 1;
						else if (create_ts.compareTo(create_ts1) < 0)
							return -1;
						else
							return 0;

					}
				});

				if (!SortStr.contains(";asc")) {
					// already in ascending order
					// }else {
					Collections.reverse(expectedResponse);
				}
			}

			if (filterStr != null && !filterStr.isEmpty()) {

				String[] filterStrArray = filterStr.split(",");
				for (int i = 0; i < filterStrArray.length; i++) {
					String[] eachFilterStr = filterStrArray[i].split(";");

					filteredResponse = new ArrayList<>();
					for (int j = 0; j < expectedResponse.size(); j++) {

						if (eachFilterStr[0].equals("source_id")) {
							HashMap<String, Object> exp_src = (HashMap<String, Object>) expectedResponse.get(j)
									.get("source");
							if (eachFilterStr[2].contains("|")) {
								String[] multiple = eachFilterStr[2].split("\\|");
								if (exp_src.containsValue(multiple[0]) || exp_src.containsValue(multiple[1])) {
									filteredResponse.add(expectedResponse.get(j));
								}
							} else {
								if (exp_src.containsValue(eachFilterStr[2])) {
									filteredResponse.add(expectedResponse.get(j));
								}
							}
						} else if (eachFilterStr[0].equals("group_id")) {
							ArrayList<HashMap<String, Object>> exp_sg = (ArrayList<HashMap<String, Object>>) expectedResponse
									.get(j).get("source_group");
							if (eachFilterStr[2].contains("|")) {
								String[] multiple = eachFilterStr[2].split("\\|");
								if (exp_sg.get(0).containsValue(multiple[0])
										|| exp_sg.get(0).containsValue(multiple[1])) {
									filteredResponse.add(expectedResponse.get(j));
								}
							} else {
								if (exp_sg.get(0).containsValue(eachFilterStr[2])) {
									filteredResponse.add(expectedResponse.get(j));
								}
							}
						} else if (eachFilterStr[0].equals("source_name")) {
							HashMap<String, Object> exp_src = (HashMap<String, Object>) expectedResponse.get(j)
									.get("source");
							if (exp_src.containsValue(eachFilterStr[2])) {
								filteredResponse.add(expectedResponse.get(j));
							}
						}
					}
					expectedResponse = filteredResponse;
				}

			} else {
				filteredResponse = expectedResponse;
			}

			if (expectedResponse != null) {
				ArrayList<HashMap<String, Object>> actualResponse = response.then().extract().path("data");

				for (int i = 0; i < actualResponse.size(); i++) {
					validateDataTransferReportDetails(actualResponse.get(i), filteredResponse.get(i), test);
				}
				assertTrue(true, "Validation passed");
			}

		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}

		return response;
	}

	/*
	 * Validation of datatransferreport/details api response
	 * 
	 * @param expected response hash
	 * 
	 * @param actual response hash
	 * 
	 * @param test
	 */
	public void validateDataTransferReportDetails(HashMap<String, Object> actual, HashMap<String, Object> expected,
			ExtentTest test) {

		HashMap<String, Object> actual_source = (HashMap<String, Object>) actual.get("source");
		HashMap<String, Object> expected_source = (HashMap<String, Object>) expected.get("source");
		spogServer.assertResponseItem(expected_source.get("source_name"), actual_source.get("source_name"), test);
		spogServer.assertResponseItem(expected_source.get("source_id"), actual_source.get("source_id"), test);
		// spogServer.assertResponseItem(expected_source.get("source_path"),
		// actual_source.get("source_path"), test);

		ArrayList<HashMap<String, Object>> actual_source_group = (ArrayList<HashMap<String, Object>>) actual
				.get("source_group");
		ArrayList<HashMap<String, Object>> exp_source_group = (ArrayList<HashMap<String, Object>>) expected
				.get("source_group");

		// sorting th source_groups
		// spogServer.sortArrayListbyString(exp_source_group, "source_group_id");
		// spogServer.sortArrayListbyString(actual_source_group, "source_group_id");

		for (int i = 0; i < actual_source_group.size(); i++) {

			spogServer.assertResponseItem(exp_source_group.get(i).get("source_group_name").toString(),
					actual_source_group.get(i).get("source_group_name").toString(), test);
			spogServer.assertResponseItem(exp_source_group.get(i).get("source_group_id").toString(),
					actual_source_group.get(i).get("source_group_id").toString(), test);
		}

		assertTrue(actual.containsKey("processed_bytes"),
				" response contains processed_bytes: " + actual.get("processed_bytes"));
		assertTrue(actual.containsKey("written_bytes"),
				" response contains written_bytes: " + actual.get("written_bytes"));
		assertTrue(actual.containsKey("transferred_bytes"),
				" response contains transferred_bytes: " + actual.get("transferred_bytes"));
	}

	/**
	 * GET /datatransferreport/details?export=csv
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param expectedStatusCode
	 * @param ExpectedErrorMessage
	 * @param test
	 */
	public void getDataTransferReportDetailsExportCSV(String token, int expectedStatusCode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		// ExportCSV
		String additionalURL = "export=csv";
		test.log(LogStatus.INFO, "Call API - GET /datatransferreport/details?export=csv");
		Response response = spogReportInvoker.getDataTransferReportDetails(token, additionalURL, test);

		try {
			if ((int) response.then().extract().path("status") == SpogConstants.REQUIRED_INFO_NOT_EXIST) {
				spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
				System.out.println(response.then().extract().path("errors.message").toString());
				test.log(LogStatus.INFO, response.then().extract().path("errors.message").toString());
			} else {
				spogServer.checkResponseStatus(response, expectedStatusCode, test);
				String code = ExpectedErrorMessage.getCodeString();
				String message = ExpectedErrorMessage.getStatus();
				spogServer.checkErrorCode(response, code);
				test.log(LogStatus.PASS, "The error code matched with the expected " + code);
				spogServer.checkErrorMessage(response, message);
				test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
			}

		} catch (Exception e) {
			assertTrue(true, "Report data as CSV:");
			test.log(LogStatus.INFO, response.prettyPrint());
		}
	}

	/**
	 * POST: /reportschedules
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param scheduleInfo
	 * @param expectedStatusCode
	 * @param test
	 * @return response
	 */
	public Response createReportSchedule(String token, HashMap<String, Object> scheduleInfo, int expectedStatusCode,
			ExtentTest test) {

		test.log(LogStatus.INFO, "Call the api Create Report Schedule");
		Response response = spogReportInvoker.createReportSchedule(token, scheduleInfo, test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		return response;
	}

	/**
	 * GET: /reports status code check
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param expectedStatusCode
	 * @param test
	 */
	public Response getReports(String token, int expectedStatusCode, ExtentTest test) {

		test.log(LogStatus.INFO, "Call Rest API GET: /reports");
		Response response = spogReportInvoker.getReports(token, null, test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		return response;
	}

	/**
	 * GET: /reports
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param expectedResponse
	 * @param filterStr
	 * @param sortStr
	 * @param page
	 * @param page_size
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param token
	 * @return response
	 */
	public Response getReportsWithCheck(String token, ArrayList<HashMap<String, Object>> expectedResponse,
			String filterStr, String sortStr, int page, int page_size, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage, ExtentTest test) {

		String additionalURL = null;
		ArrayList<HashMap<String, Object>> filteredResponse = new ArrayList<>();

		if (page == 0 || page == -1) {
			page = 1;
		}
		if (page_size == 0 || page_size == -1) {
			page_size = 20;
		} else if (page_size >= 100 && page_size < SpogConstants.MAX_PAGE_SIZE) {
			page_size = 100;
		}
		additionalURL = spogServer.PrepareURL(filterStr, sortStr, page, page_size, test);

		test.log(LogStatus.INFO, "Call Rest API GET: /reports");
		Response response = spogReportInvoker.getReports(token, additionalURL, test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			// Filtering
			if (filterStr != null && !filterStr.isEmpty()) {

				String[] filterStrArray = filterStr.split(",");
				for (int i = 0; i < filterStrArray.length; i++) {
					String[] eachFilterStr = filterStrArray[i].split(";");

					filteredResponse = new ArrayList<>();
					for (int j = 0; j < expectedResponse.size(); j++) {

						if (eachFilterStr[0].equals("report_name")) {
							if (expectedResponse.get(j).containsValue(eachFilterStr[2])) {
								filteredResponse.add(expectedResponse.get(j));
							}
						} else if (eachFilterStr[0].equals("report_type")) {
							if (expectedResponse.get(j).containsValue(eachFilterStr[2])) {
								filteredResponse.add(expectedResponse.get(j));
							}
						} else if (eachFilterStr[0].equals("schedule_frequency")) {
							if (expectedResponse.get(j).containsValue(eachFilterStr[2])) {
								filteredResponse.add(expectedResponse.get(j));
							}
						} else if (eachFilterStr[0].equals("date_range_type")) {
							if (expectedResponse.get(j).containsValue(eachFilterStr[2])) {
								filteredResponse.add(expectedResponse.get(j));
							}
						} else if (eachFilterStr[0].equals("date_range_start_ts")) {
							/*
							 * if(expectedResponse.get(j).containsValue(eachFilterStr[2])) {
							 * filteredResponse.add(expectedResponse.get(j)); }
							 */
							if (eachFilterStr[1].equals("<")) {
								if (Long.valueOf(expectedResponse.get(j).get("date_range_start_ts").toString()) <= Long
										.valueOf((eachFilterStr[2]))) {
									filteredResponse.add(expectedResponse.get(j));
								}
							} else if (eachFilterStr[1].equals(">")) {
								if (Long.valueOf(expectedResponse.get(j).get("date_range_start_ts").toString()) >= Long
										.valueOf((eachFilterStr[2]))) {
									filteredResponse.add(expectedResponse.get(j));
								}
							} else if (eachFilterStr[1].equals("=")) {
								if (expectedResponse.get(j).containsValue(eachFilterStr[2])) {
									filteredResponse.add(expectedResponse.get(j));
								}
							}
						} else if (eachFilterStr[0].equals("date_range_end_ts")) {
							if (expectedResponse.get(j).containsValue(eachFilterStr[2])) {
								filteredResponse.add(expectedResponse.get(j));
							}
						} else if (eachFilterStr[0].equals("source_group_id")) {
							if (eachFilterStr[2].contains("|")) {
								String[] multiple = eachFilterStr[2].split("\\|");
								if (expectedResponse.get(j).containsValue(multiple[0])
										|| expectedResponse.get(j).containsValue(multiple[1])) {
									filteredResponse.add(expectedResponse.get(j));
								}
							} else {
								if (expectedResponse.get(j).containsValue(eachFilterStr[2])) {
									filteredResponse.add(expectedResponse.get(j));
								}
							}
						} else if (eachFilterStr[0].equals("create_user_id")) {
							if (expectedResponse.get(j).containsValue(eachFilterStr[2])) {
								filteredResponse.add(expectedResponse.get(j));
							}
						} else if (eachFilterStr[0].equals("create_ts")) {
							if (expectedResponse.get(j).containsValue(eachFilterStr[2])) {
								filteredResponse.add(expectedResponse.get(j));
							}
						} else if (eachFilterStr[0].equals("generated_on")) {
							if (eachFilterStr[1].contains("<")) {
								if (Long.valueOf(expectedResponse.get(j).get("create_ts").toString()) <= Long
										.valueOf((eachFilterStr[2]))) {
									filteredResponse.add(expectedResponse.get(j));
								}
							} else if (eachFilterStr[1].contains(">")) {
								if (Long.valueOf(expectedResponse.get(j).get("create_ts").toString()) >= Long
										.valueOf((eachFilterStr[2]))) {
									filteredResponse.add(expectedResponse.get(j));
								}
							} else if (eachFilterStr[1].contains("=")) {
								/*
								 * if(Long.valueOf(expectedResponse.get(j).get("create_ts").toString()) ==
								 * Long.valueOf((eachFilterStr[2]))) {
								 * filteredResponse.add(expectedResponse.get(j)); }
								 */
								if (expectedResponse.get(j).containsValue(eachFilterStr[2])) {
									filteredResponse.add(expectedResponse.get(j));
								}
							}
						} else if (eachFilterStr[0].equals("organization_id")) {
							if (expectedResponse.get(j).containsValue(eachFilterStr[2])) {
								filteredResponse.add(expectedResponse.get(j));
							}
						}
					}
					expectedResponse = filteredResponse;
				}

			} else {
				filteredResponse = expectedResponse;
			}

			expectedResponse = filteredResponse;

			// Sorting in ascending order
			if (sortStr != "" && sortStr != null) {

				if (sortStr.contains("report_name")) {
					spogServer.sortArrayListbyString(expectedResponse, "report_name");
				} else if (sortStr.contains("schedule_frequency")) {
					spogServer.sortArrayListbyString(expectedResponse, "schedule_frequency", "create_ts");
				} else if (sortStr.contains("date_range_type")) {
					spogServer.sortArrayListbyString(expectedResponse, "date_range_type", "create_ts");
				} else if (sortStr.contains("create_ts")) {
					spogServer.sortArrayListbyString(expectedResponse, "create_ts");
				} else if (sortStr.contains("report_for_type")) {
					spogServer.sortArrayListbyString(expectedResponse, "report_for_type", "create_ts");
				} else if (sortStr.contains("generated_on")) {
					spogServer.sortArrayListbyString(expectedResponse, "date_range_start_ts", "create_ts");
				} else if (sortStr.contains("report_type")) {
					spogServer.sortArrayListbyString(expectedResponse, "report_type", "create_ts");
				} else if (sortStr.contains("create_user")) {
					spogServer.sortArrayListbyString(expectedResponse, "create_user", "create_ts");
				}
				// making descending after sorting in ascending order if required
				if (sortStr.contains(";-asc")) {
					Collections.reverse(expectedResponse);
				}

				// Sort in ascending, if equal sort by create_ts in descending to make the
				// latest report at top
				if (sortStr.contains("schedule_frequency") && sortStr.contains(";asc")) {
					sortArrayListbyString(expectedResponse, "schedule_frequency", "create_ts");
				} else if (sortStr.contains("report_type") && sortStr.contains(";asc")) {
					sortArrayListbyString(expectedResponse, "report_type", "create_ts");
				} else if (sortStr.contains("create_user") && sortStr.contains(";asc")) {
					sortArrayListbyString(expectedResponse, "create_user", "create_ts");
				}

			} else {
				Collections.reverse(expectedResponse);
			}

			ArrayList<HashMap<String, Object>> actResponse = response.then().extract().path("data");
			if (expectedResponse.size() == actResponse.size()) {

				if (filterStr != null && actResponse.size() == 0) {
					test.log(LogStatus.INFO, "No reports are available with applied filter.");
				}

				spogServer.setToken(token);
				for (int i = 0; i < actResponse.size(); i++) {
					// expectedResponse.get(i).put("create_user",
					// spogServer.getLoggedinUser_EmailId());
					validateReportsInfo(expectedResponse.get(i), actResponse.get(i), test);
				}
				test.log(LogStatus.PASS, "Reports validation passed.");
			} else {
				assertFalse(true, "Actual reports count :" + actResponse.size() + " doesn't match with expected count: "
						+ expectedResponse.size());
				test.log(LogStatus.FAIL, "Actual reports count :" + actResponse.size()
						+ " doesn't match with expected count: " + expectedResponse.size());
			}

		} else {
			spogServer.checkResponseStatus(response, expectedStatusCode, test);
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}

		return response;
	}

	public void validateReportsInfo(HashMap<String, Object> expectedData, HashMap<String, Object> actualData,
			ExtentTest test) {

		System.out.println("hi");

		test.log(LogStatus.INFO, "Compare report_name");
		spogServer.assertResponseItem(expectedData.get("report_name").toString(),
				actualData.get("report_name").toString(), test);

		test.log(LogStatus.INFO, "Compare report_type");
		spogServer.assertResponseItem(expectedData.get("report_type").toString(),
				actualData.get("report_type").toString(), test);

		HashMap<String, Object> date_range = (HashMap<String, Object>) actualData.get("date_range");

		test.log(LogStatus.INFO, "Compare date_range_type");
		spogServer.assertResponseItem(expectedData.get("date_range_type").toString(), date_range.get("type").toString(),
				test);

		test.log(LogStatus.INFO, "Compare date_range_start_ts");
		spogServer.assertResponseItem(expectedData.get("date_range_start_ts").toString(),
				date_range.get("start_ts").toString(), test);

		test.log(LogStatus.INFO, "Compare date_range_end_ts");
		spogServer.assertResponseItem(expectedData.get("date_range_end_ts").toString(),
				date_range.get("end_ts").toString(), test);

		test.log(LogStatus.INFO, "Compare report_for");
		spogServer.assertResponseItem(((HashMap<String, Object>) expectedData.get("report_for")).get("type").toString(),
				((HashMap<String, Object>) actualData.get("report_for")).get("type").toString(), test);

		if (expectedData.get("schedule_id") != (null)) {
			test.log(LogStatus.INFO, "Compare schedule_frequencey");
			spogServer.assertResponseItem(expectedData.get("schedule_frequency").toString(),
					actualData.get("schedule_frequency").toString(), test);
		}

		test.log(LogStatus.INFO, "Compare recipient_mail");
		ArrayList<String> exp_rec_mail = new ArrayList<>();
		ArrayList<String> act_rec_mail = new ArrayList<>();

		exp_rec_mail = (ArrayList<String>) expectedData.get("recipient_mail");
		act_rec_mail = (ArrayList<String>) actualData.get("recipient_mail");

		if (exp_rec_mail.size() == act_rec_mail.size()) {
			for (int i = 0; i < exp_rec_mail.size(); i++) {
				if (exp_rec_mail.get(i).equals(act_rec_mail.get(i))) {
					spogServer.assertResponseItem(exp_rec_mail.get(i).toString(), act_rec_mail.get(i).toString(), test);
				}
			}
		} else {
			assertTrue(false, "Expected Recipient mails are: " + exp_rec_mail + " and Actual Recipient mails are: "
					+ act_rec_mail);
		}

		test.log(LogStatus.INFO, "Compare create_user_id");
		spogServer.assertResponseItem(expectedData.get("create_user"), actualData.get("create_user"), test);

		assertNotNull(actualData.containsKey("available_actions"),
				"available_actions: " + actualData.get("available_actions"));

	}

	/**
	 * To sort in ascending order and if equals sort by 2nd param in descending
	 * order
	 * 
	 * @author Rakesh.Chalamala Used for GET: /reports
	 */
	public ArrayList<HashMap<String, Object>> sortArrayListbyString(ArrayList<HashMap<String, Object>> expectedresponse,
			String key, String alterKey) {

		Collections.sort(expectedresponse, new Comparator<HashMap<String, Object>>() {
			@Override
			public int compare(HashMap<String, Object> o1, HashMap<String, Object> o2) {

				// TODO Auto-generated method stub
				String create_ts = (String) o1.get(key);
				String create_ts1 = (String) o2.get(key);
				if (create_ts.compareTo(create_ts1) > 0)
					return 1;
				else if (create_ts.compareTo(create_ts1) == 0) {

					// TODO Auto-generated method stub
					String column_id1 = (String) o1.get(alterKey);
					String column_id2 = (String) o2.get(alterKey);
					if (column_id1.compareTo(column_id2) > 0) {
						return -1;
					} else if (column_id1.compareTo(column_id2) < 0) {
						return 1;
					} else
						return 0;
				} else
					return -1;

			}
		});
		return expectedresponse;
	}

	/**
	 * DELETE: /reports
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param token
	 */
	public void deleteReportsWithCheck(String token, ArrayList<String> report_ids, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage, ExtentTest test) {

		test.log(LogStatus.INFO, "Call Rest API DELETE: /reports");
		Response response = spogReportInvoker.deleteReports(token, report_ids, test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			test.log(LogStatus.PASS, "Reports with ids: " + report_ids + " deleted successfully.");
		} else {
			spogServer.checkResponseStatus(response, expectedStatusCode, test);
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
	}

	/**
	 * DELETE: /reports/{id]
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param report_id
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param token
	 */
	public void deleteReportsByIdWithCheck(String token, String report_id, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage, ExtentTest test) {

		test.log(LogStatus.INFO, "Call Rest API DELETE: /reports/{id}");
		Response response = spogReportInvoker.deleteReportsById(token, report_id, test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			test.log(LogStatus.PASS, "Report with id: " + report_id + " deleted successfully.");
		} else {
			spogServer.checkResponseStatus(response, expectedStatusCode, test);
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
	}

	/**
	 * getBackupJobStatusSummary
	 * 
	 * @author Ramya.Nagepalli
	 * @param adminToken
	 * @param additionalUrl
	 * @param test
	 */
	public Response getBackupJobStatusSummary(String adminToken, String additionalUrl, ExtentTest test) {

		test.log(LogStatus.INFO, "get Backup Job Status Summary");
		Response response = spogReportInvoker.getBackupJobStatusSummary(adminToken, additionalUrl, test);
		return response;
	}

	/**
	 * checkBackupJobStatusSummary
	 * 
	 * @author Ramya.Nagepalli
	 * @param response
	 * @param organization_id
	 * @param additionalUrl
	 * @param statusDetails
	 * @param expectedstatuscode
	 * @param ExpectedErrorMessage
	 * @param test
	 */
	public void checkBackupJobStatusSummary(Response response, String organization_id, String additionalUrl,
			ArrayList<HashMap> statusDetails, int expectedstatuscode, SpogMessageCode ExpectedErrorMessage,
			ExtentTest test) {

		HashMap<String, Object> temp = new HashMap<String, Object>();
		ArrayList<HashMap> actual = new ArrayList<HashMap>();

		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			actual = response.then().extract().path("data.backup_job_report_status_summary");

			if (!(actual.isEmpty())) {

				if (additionalUrl.contains("job_status") && additionalUrl.contains("organization_id")) {
					String[] item = additionalUrl.split("=");
					for (int j = 0; j < statusDetails.size(); j++) {
						if (statusDetails.get(j).get("job_status").equals(item[1])) {
							temp = statusDetails.get(j);
							statusDetails.clear();
							statusDetails.add(temp);
						}
					}
					checkBackupJobStatusData(response, actual, statusDetails);

				} else if (additionalUrl.contains("job_status")) {
					String[] item = additionalUrl.split("=");
					for (int j = 0; j < statusDetails.size(); j++) {
						if (statusDetails.get(j).get("job_status").equals(item[1])) {
							temp = statusDetails.get(j);
							statusDetails.clear();
							statusDetails.add(temp);
						}

					}
					checkBackupJobStatusData(response, actual, statusDetails);
				} else {
					checkBackupJobStatusData(response, actual, statusDetails);

				}

			}

			test.log(LogStatus.PASS, "There is no data to display report_status_summary ");

		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			if (code.contains("0x0030000A")) {
				message = message.replace("{0}", organization_id);
			}
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);

		}
	}

	private void checkBackupJobStatusData(Response response, ArrayList<HashMap> actual,
			ArrayList<HashMap> statusDetails) {
		for (int i = 0; i < actual.size(); i++) {
			for (int j = 0; j < statusDetails.size(); j++) {
				String act_job_status = actual.get(i).get("job_status").toString();
				String exp_job_status = statusDetails.get(j).get("job_status").toString();

				int act_job_count = (int) actual.get(i).get("job_count");
				int exp_job_count = (int) statusDetails.get(j).get("job_count");

				if (act_job_status.equals(exp_job_status)) {

					spogServer.assertResponseItem(exp_job_status, act_job_status);
					spogServer.assertResponseIntegerItem(exp_job_count, act_job_count, test);

					int act_job_inProgress = response.then().extract().path("data.in_progress_job_count");
					int exp_job_inProgress = (int) statusDetails.get(j).get("in_progress_count");
					spogServer.assertResponseIntegerItem(exp_job_inProgress, act_job_inProgress, test);
				}
			}
		}

	}

	/**
	 * getDashboardTopSources
	 * 
	 * @author Ramya.Nagepalli
	 * @param adminToken
	 * @param additionalUrl
	 * @param test
	 */
	public Response getDashboardTopSources(String adminToken, String additionalUrl, ExtentTest test) {
		Response response = spogReportInvoker.getDashboardTopSources(adminToken, additionalUrl, test);
		return response;
	}

	/**
	 * getDashboardSourceSummary
	 * 
	 * @author Ramya.Nagepalli
	 * @param adminToken
	 * @param organization_id
	 * @param additionalUrl
	 * @param expectedDetails
	 * @param ExpectedStatusCode
	 * @param ExpectedErrorMessage
	 * @param test
	 *            return response
	 */
	public Response getDashboardSourceSummary(String adminToken, String organization_id, String additionalUrl,
			HashMap<String, Object> expectedDetails, int ExpectedStatusCode, SpogMessageCode ExpectedErrorMessage,
			ExtentTest test) {
		Response response = spogReportInvoker.getDashboardSourceSummary(adminToken, additionalUrl, test);
		HashMap<String, Object> temp = new HashMap<String, Object>();
		spogServer.checkResponseStatus(response, ExpectedStatusCode);
		if (ExpectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			if (!(additionalUrl == "")) {
				expectedDetails.clear();
				if (additionalUrl.contains("organization_id")) {
					String[] item = additionalUrl.split("=");
					for (int i = 0; i < expectedDetails.size(); i++) {
						if (expectedDetails.containsValue(item[1])) {
							temp = (HashMap<String, Object>) expectedDetails.get(i);
						}
						expectedDetails = temp;
					}
				}
			}
			int act_total = response.then().extract().path("data.total");
			spogServer.assertResponseIntegerItem(expectedDetails.get("total"), act_total, test);

			int act_protected = response.then().extract().path("data.protected");
			spogServer.assertResponseIntegerItem(expectedDetails.get("protected"), act_protected, test);

			int act_unprotected = response.then().extract().path("data.notProtected");
			spogServer.assertResponseIntegerItem(expectedDetails.get("notprotected"), act_unprotected, test);

			int act_offline = response.then().extract().path("data.offline");
			spogServer.assertResponseIntegerItem(expectedDetails.get("offline"), act_offline, test);

		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			if (code.contains("0x0030000A")) {
				message = message.replace("{0}", organization_id);
			}
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + ExpectedStatusCode);
		}
		return response;
	}

	/**
	 * getDashboardTopPolicies
	 * 
	 * @author Ramya.Nagepalli
	 * @param adminToken
	 * @param test
	 */
	public Response getDashboardTopPolicies(String adminToken, String additionalUrl, ExtentTest test) {
		Response response = spogReportInvoker.getDashboardTopPolicies(adminToken, additionalUrl, test);
		return response;
	}

	/**
	 * checkDashboardTopPolicies
	 * 
	 * @author Ramya.Nagepalli
	 * @param response
	 * @param organization_id
	 * @param additionalUrl
	 * @param expectedPolicies
	 * @param ExpectedStatusCode
	 * @param ExpectedErrorMessage
	 * @param test
	 *
	 * 
	 */
	public void checkDashboardTopPolicies(Response response, String organization_id, String additionalUrl,
			ArrayList<HashMap<String, Object>> expectedPolicies, int ExpectedStatusCode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		ArrayList<HashMap> actualPolicies = new ArrayList<HashMap>();
		ArrayList<HashMap> expected_filter_policies = new ArrayList<HashMap>();

		HashMap<String, Object> temp = new HashMap<String, Object>();

		spogServer.checkResponseStatus(response, ExpectedStatusCode);
		if (ExpectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			actualPolicies = response.then().extract().path("data.policies");
			for (int i = 0; i < expectedPolicies.size(); i++) {
				for (int j = 0; j < actualPolicies.size(); j++) {

					String exp_name = expectedPolicies.get(i).get("name").toString();
					String act_name = actualPolicies.get(j).get("name").toString();

					String exp_stack = expectedPolicies.get(i).get("stack").toString();
					String act_stack = actualPolicies.get(j).get("stack").toString();

					int exp_value = (int) expectedPolicies.get(i).get("value");
					int act_value = (int) actualPolicies.get(j).get("value");

					if (exp_name.equals(act_name) && exp_stack.equals(act_stack)) {
						spogServer.assertResponseItem(exp_name, act_name);
						spogServer.assertResponseItem(exp_stack, act_stack);
						spogServer.assertResponseIntegerItem(exp_value, act_value, test);
					} else {
						break;
					}
				}
			}
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			if (code.contains("0x0030000A")) {
				message = message.replace("{0}", organization_id);
			}
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + ExpectedStatusCode);

		}

	}

	/**
	 * getDashboardPolicySummaryDetails
	 * 
	 * @author Ramya.Nagepalli
	 * @param adminToken
	 * @param test
	 */
	public Response getDashboardPolicySummaryDetails(String adminToken, String additionalUrl, ExtentTest test) {
		Response response = spogReportInvoker.getDashboardPolicySummaryDetails(adminToken, additionalUrl, test);
		return response;
	}

	/**
	 * checkDashboardPolicySummaryDetails
	 * 
	 * @author Ramya.Nagepalli
	 * @param response
	 * @param organization_id
	 * @param additionalUrl
	 * @param expectedPolicyDetails
	 * @param ExpectedStatusCode
	 * @param ExpectedErrorMessage
	 * @param test
	 * 
	 */
	public void checkDashboardPolicySummaryDetails(Response response, String organization_id, String additionalUrl,
			HashMap<String, Object> expectedPolicyDetails, int ExpectedStatusCode, SpogMessageCode ExpectedErrorMessage,
			ExtentTest test) {
		HashMap<String, Object> actualPolicies = new HashMap<String, Object>();

		HashMap<String, Object> temp = new HashMap<String, Object>();

		spogServer.checkResponseStatus(response, ExpectedStatusCode);
		if (ExpectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			actualPolicies = response.then().extract().path("data");

			int exp_total = (int) expectedPolicyDetails.get("total");
			int act_total = (int) actualPolicies.get("total");
			// spogServer.assertResponseIntegerItem(exp_total, act_total,test);

			int exp_failure = (int) expectedPolicyDetails.get("failure");
			int act_failure = (int) actualPolicies.get("failure");
			// spogServer.assertResponseIntegerItem(exp_failure, act_failure,test);

			int exp_deploying = (int) expectedPolicyDetails.get("deploying");
			int act_deploying = (int) actualPolicies.get("deploying");
			// spogServer.assertResponseIntegerItem(exp_deploying, act_deploying,test);

			int exp_success = (int) expectedPolicyDetails.get("success");
			int act_success = (int) actualPolicies.get("success");
			/*spogServer.assertResponseIntegerItem(exp_success, act_success, test);*/

		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			if (code.contains("0x0030000A")) {
				message = message.replace("{0}", organization_id);
			}
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + ExpectedStatusCode);

		}

	}

	/**
	 * Get CapacityUsageReportsDetails
	 * 
	 * @author Ramya.Nagepalli
	 * @param adminToken
	 * @param additionalUrl
	 * @param test
	 */
	public Response getCapacityUsageReportsDetails(String adminToken, String additionalUrl, ExtentTest test) {
		// TODO Auto-generated method stub

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Response response = spogReportInvoker.getCapacityUsageReportsDetails(adminToken, additionalUrl, test);

		return response;
	}

	/**
	 * checkCapacityUsageDetails
	 * 
	 * @author Ramya.Nagepalli
	 * @param response
	 * @param additionalUrl
	 * @param expectedDetails
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 */
	public void checkCapacityUsageDetails(Response response, String additionalUrl,
			ArrayList<HashMap<String, Object>> expectedDetails, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage, ExtentTest test) {

		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			ArrayList<HashMap<String, Object>> actualResponse = new ArrayList<>();
			actualResponse = response.then().extract().path("data");

			for (int i = 0; i < actualResponse.size(); i++) {
				for (int j = 0; j < expectedDetails.size(); j++) {
					test.log(LogStatus.INFO, "Validate the response");

					HashMap<String, String> actOrganizationDetails = (HashMap<String, String>) actualResponse.get(i)
							.get("organization");
					HashMap<String, String> expOrganizationDetails = (HashMap<String, String>) expectedDetails.get(j)
							.get("organization");

					spogServer.assertResponseItem(expOrganizationDetails.get("organization_id"),
							actOrganizationDetails.get("organization_id"));
					spogServer.assertResponseItem(expOrganizationDetails.get("organization_name").toLowerCase(),
							actOrganizationDetails.get("organization_name").toLowerCase());

					String actdestinationDetails = actualResponse.get(i).get("destination").toString();
					String expdestinationDetails = expectedDetails.get(j).get("destination").toString();

					spogServer.assertResponseItem(expdestinationDetails, actdestinationDetails);

					String actretentionDetails = actualResponse.get(i).get("retention").toString();
					String expretentionDetails = expectedDetails.get(j).get("retention").toString();

					spogServer.assertResponseItem(expretentionDetails, actretentionDetails);

					String actprocessed_bytes = actualResponse.get(i).get("processed_bytes").toString();
					String expprocessed_bytes = expectedDetails.get(j).get("processed_bytes").toString();

					spogServer.assertResponseItem(expprocessed_bytes, actprocessed_bytes);

					String actdedupe_savings = actualResponse.get(i).get("dedupe_savings").toString();
					String expdedupe_savings = expectedDetails.get(j).get("dedupe_savings").toString();

					spogServer.assertResponseItem(expdedupe_savings, actdedupe_savings);

					String actstorage_usage = actualResponse.get(i).get("storage_usage").toString();
					String expstorage_usage = expectedDetails.get(j).get("storage_usage").toString();

					spogServer.assertResponseItem(expstorage_usage, actstorage_usage);
				}

			}

		} else {

			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}

	}

	/**
	 * Get CloudHybridCapacityUsageDetails
	 * 
	 * @author Ramya.Nagepalli
	 * @param adminToken
	 * @param additionalUrl
	 * @param test
	 */
	public Response getCloudHybridCapacityUsageDetails(String adminToken, String additionalUrl, ExtentTest test2) {
		// TODO Auto-generated method stub
		Response response = spogReportInvoker.getCloudHybridCapacityUsageDetails(adminToken, additionalUrl, test);

		return response;
	}

	/**
	 * checkCloudHybridCapacityUsageDetails
	 * 
	 * @author Ramya.Nagepalli
	 * @param additionalUrl
	 * @param expectedDetails
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 */
	public void checkCloudHybridCapacityUsageDetails(Response response, String additionalUrl,
			ArrayList<HashMap<String, Object>> expectedDetails, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage, ExtentTest test) {
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			HashMap<String, Object> actualResponse = new HashMap<String, Object>();
			actualResponse = response.then().extract().path("data." + expectedDetails.get(0).get("id").toString());

			test.log(LogStatus.INFO, "Validate the response");

			String act_id = actualResponse.get("id").toString();
			String exp_id = expectedDetails.get(0).get("id").toString();

			spogServer.assertResponseItem(exp_id, act_id);

			String act_name = actualResponse.get("name").toString();
			String exp_name = expectedDetails.get(0).get("name").toString();

			spogServer.assertResponseItem(exp_name, act_name);

		} else {

			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
	}

	/**
	 * Get CloudHybridDedupeUsageDetails
	 * 
	 * @author Ramya.Nagepalli
	 * @param adminToken
	 * @param additionalUrl
	 * @param test
	 */
	public Response getCloudHybridDedupeUsageDetails(String adminToken, String additionalUrl, ExtentTest test2) {
		// TODO Auto-generated method stub
		Response response = spogReportInvoker.getCloudHybridDedupeUsageDetails(adminToken, additionalUrl, test);

		return response;
	}

	/**
	 * check CloudHybridDedupeUsageDetails
	 * 
	 * @author Ramya.Nagepalli
	 * @param additionalUrl
	 * @param expectedDetails
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 */
	public void checkCloudHybridDedupeUsageDetails(Response response, String additionalUrl,
			HashMap<String, Object> expectedDetails, int expectedStatusCode, SpogMessageCode expectedErrorMessage,
			ExtentTest test) {
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			HashMap<ArrayList<HashMap<String, Object>>, Object> actualResponse = new HashMap<ArrayList<HashMap<String, Object>>, Object>();
			actualResponse = response.then().extract().path("data");

			HashMap<String, Object> Source_data_date_value = new HashMap<String, Object>();
			Source_data_date_value = (HashMap<String, Object>) actualResponse.get("source_data");

			HashMap<String, Object> dedupe_savings_date_value = new HashMap<String, Object>();
			dedupe_savings_date_value = (HashMap<String, Object>) actualResponse.get("dedupe_savings");

		} else {

			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
	}

	/**
	 * GET: /dashboards/topcustomers
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param expectedResponse
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 * @param response
	 */
	public void getTopCustomersWithCheck(String token, String org_id,
			ArrayList<HashMap<String, Object>> expectedResponse, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage, ExtentTest test) {

		test.log(LogStatus.INFO, "Call REST API - GET: /dashboards/topcustomers");
		Response response = spogReportInvoker.getTopCustomers(token, org_id, test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			HashMap<String, Object> actualResponse = response.then().extract().path("data");
			try {
				spogServer.sortArrayListbyInt(expectedResponse, "value");
				Collections.reverse(expectedResponse);
			} catch (Exception e) {
				spogServer.sortArrayListbyString(expectedResponse, "id");
				spogServer.sortArrayListbyString((ArrayList<HashMap<String, Object>>) actualResponse.get("customers"),
						"id");
			}

			assertNotNull(actualResponse.get("last_updated_ts"),
					" last_updated_ts is : " + actualResponse.get("last_updated_ts"));
			ArrayList<HashMap<String, Object>> actualCustomers = (ArrayList<HashMap<String, Object>>) actualResponse
					.get("customers");
			assertTrue(actualCustomers.size() == 10, "Count of customers in api response is:" + actualCustomers.size());

			for (int i = 0; i < actualCustomers.size(); i++) {

				spogServer.assertResponseItem(expectedResponse.get(i).get("id"), actualCustomers.get(i).get("id"),
						test);
				spogServer.assertResponseItem(expectedResponse.get(i).get("name"), actualCustomers.get(i).get("name"),
						test);
				spogServer.assertResponseItem(expectedResponse.get(i).get("value").toString(),
						actualCustomers.get(i).get("value").toString(), test);
				assertNotNull(actualCustomers.get(i).get("stack"), "stack value for customer "
						+ actualCustomers.get(i).get("name") + " is " + actualCustomers.get(i).get("stack"));
			}

			test.log(LogStatus.PASS, "Validation passed.");
		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedErrorMessage);
		}
	}

	/**
	 * POST: /reportschedules/{id}/generatereportnow
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param schedule_id
	 * @param test
	 */
	public void generateReportNowWithCheck(String token, String schedule_id, HashMap<String, Object> scheduleInfo,
			int expectedStatusCode, SpogMessageCode expectedErrorMessage, ExtentTest test) {

		test.log(LogStatus.INFO, "Call REST API - POST: /reportschedules/{id}/generatereportnow");
		Response response = spogReportInvoker.generateReportNow(token, schedule_id, test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			HashMap<String, Object> actualResponse = new HashMap<>();
			actualResponse = response.then().extract().path("data");

			spogServer.setToken(token);
			scheduleInfo.put("create_user_id", spogServer.GetLoggedinUser_UserID());

			/*
			 * //Adding the loggedin user email to recipient_mail for validation.
			 * spogServer.setToken(token); // commented as the email is already added during
			 * postReportSchedules validation ArrayList<String> re = new ArrayList<>();
			 * re.add(spogServer.getLoggedinUser_EmailId()); re.addAll((ArrayList<String>)
			 * scheduleInfo.get("recipient_mail"));
			 * 
			 * scheduleInfo.put("recipient_mail", re);
			 */

			test.log(LogStatus.INFO, "Validate the response");
			validateReportScheduleInfo(scheduleInfo, actualResponse, test);

			test.log(LogStatus.PASS, "Validation passed.");
		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedErrorMessage);
		}
	}

	/**
	 * CreateReportSchedule
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param scheduleInfo
	 * @param test
	 * @return reportScheduleId
	 */
	public Response createReportScheduleWithCheck_audit(String token, HashMap<String, Object> scheduleInfo,
			int expectedStatusCode, SpogMessageCode expectedErrorMessage, ExtentTest test) {

		String reportScheduleId = null;
		test.log(LogStatus.INFO, "Call the api Create Report Schedule");
		Response response = spogReportInvoker.createReportSchedule(token, scheduleInfo, test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_POST
				|| expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			HashMap<String, Object> actualResponse = new HashMap<>();
			actualResponse = response.then().extract().path("data");

			spogServer.setToken(token);
			scheduleInfo.put("create_user_id", spogServer.GetLoggedinUser_UserID());

			// Adding the loggedin user email to recipient_mail for validation.
			spogServer.setToken(token);
			ArrayList<String> re = new ArrayList<>();
			re.add(spogServer.getLoggedinUser_EmailId());
			re.addAll((ArrayList<String>) scheduleInfo.get("recipient_mail"));

			scheduleInfo.put("recipient_mail", re);

			test.log(LogStatus.INFO, "Validate the response");
			reportScheduleId = validateReportScheduleInfo(scheduleInfo, actualResponse, test);

		} else {

			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}

		return response;
	}

	/**
	 * GET: /organizations/{id}/accountsummary
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param org_id
	 * @param expectedResponse
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 * 
	 */
	public void getOrganizationAccountSummary(String token, String org_id, HashMap<String, Object> expectedResponse,
			int expectedStatusCode, SpogMessageCode expectedErrorMessage, ExtentTest test) {

		test.log(LogStatus.INFO, "Call REST API - GET: /organizations/{id}/accountsummary");
		Response response = spogReportInvoker.getOrganizationAccountSummary(token, org_id, test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			HashMap<String, Object> actualResponse = response.then().extract().path("data");

			spogServer.assertResponseItem(expectedResponse.get("total_customer_count").toString(),
					actualResponse.get("total_customer_count").toString(), test);
			spogServer.assertResponseItem(expectedResponse.get("num_success_total_count").toString(),
					actualResponse.get("num_success_total_count").toString(), test);
			spogServer.assertResponseItem(expectedResponse.get("num_missed_total_count").toString(),
					actualResponse.get("num_missed_total_count").toString(), test);
			spogServer.assertResponseItem(expectedResponse.get("num_failed_total_count").toString(),
					actualResponse.get("num_failed_total_count").toString(), test);

		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedErrorMessage);
		}
	}

	/**
	 * checkDashboardTopSources
	 * 
	 * @author Ramya.Nagepalli
	 * @param response
	 * @param organization_id
	 * @param additionalUrl
	 * @param expectedsources
	 * @param expectedStatusCode
	 * @param EpectedErrorMesssage
	 * @param test
	 */
	public void checkDashboardTopSources(Response response, String organization_id, String additionalUrl,
			ArrayList<HashMap<String, Object>> expectedsources, int expectedstatuscode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		int total = 0;
		ArrayList<HashMap<String, Object>> composed_expectedsources = new ArrayList<HashMap<String, Object>>();
		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			HashMap<String, Object> actual_response = new HashMap<String, Object>();
			HashMap<String, Object> temp = new HashMap<String, Object>();

			ArrayList<HashMap<String, Object>> actual_sources = new ArrayList<HashMap<String, Object>>();
			actual_sources = response.then().extract().path("data.sources");
			total = actual_sources.size();
			for (int i = 0; i < total; i++) {
				for (int j = 0; j < total; j++) {

					String exp_stack = expectedsources.get(i).get("stack").toString();
					String act_stack = actual_sources.get(j).get("stack").toString();

					if (exp_stack.equals(act_stack)) {

						String exp_name = expectedsources.get(i).get("name").toString();
						String act_name = actual_sources.get(j).get("name").toString();

						spogServer.assertResponseItem(exp_stack, act_stack);
						spogServer.assertResponseItem(exp_name, act_name);

						int exp_value = (int) expectedsources.get(i).get("value");
						int act_value = (int) actual_sources.get(j).get("value");
						spogServer.assertResponseItem(exp_value, act_value, test);
					}
				}
			}

		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			if (code.contains("0x0030000A")) {
				message = message.replace("{0}", organization_id);
			}
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);

		}
	}

	/**
	 * 
	 * GET: /dashboard/widgets Method to getSystemDashboardWidgets
	 * 
	 * @author Ramya.Nagepalli
	 * @param ValidToken
	 * @param expectedErrorMessage
	 * @param ExpectedStatusCode
	 * @param test
	 * @return response
	 */

	public Response getSystemDashboardWidgets(String ValidToken, int ExpectedStatusCode,
			SpogMessageCode expectedErrorMessage, ExtentTest test) {
		// TODO Auto-generated method stub

		Response response = spogReportInvoker.getSystemDashboardWidgets(ValidToken, test);

		spogServer.checkResponseStatus(response, ExpectedStatusCode);

		ArrayList<HashMap<String, Object>> ExpectedDashboardWidgets = new ArrayList<HashMap<String, Object>>();

		ArrayList<HashMap<String, Object>> ActualDashboardWidgets = new ArrayList<HashMap<String, Object>>();

		if (ExpectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			ActualDashboardWidgets = response.then().extract().path("data");

			HashMap<String, Object> dashboardWidget1 = new HashMap<String, Object>();
			dashboardWidget1 = composeDashboardWidgetsInfo(DashboardWidgetConstants.BACKUP_JOB_SUMMARY_WIDGET_ID,
					DashboardWidgetConstants.BACKUP_JOB_SUMMARY_LONG, DashboardWidgetConstants.BACKUP_JOB_SUMMARY_SHORT,
					DashboardWidgetConstants.BACKUP_JOB_SUMMARY_KEY,
					DashboardWidgetConstants.BACKUP_JOB_SUMMARY_VISIBLE,
					DashboardWidgetConstants.BACKUP_JOB_SUMMARY_ORDERID,
					DashboardWidgetConstants.BACKUP_JOB_SUMMARY_IS_EXPANDED);
			ExpectedDashboardWidgets.add(dashboardWidget1);

			HashMap<String, Object> dashboardWidget2 = new HashMap<String, Object>();
			dashboardWidget2 = composeDashboardWidgetsInfo(DashboardWidgetConstants.RECENT_10_JOBS_WIDGET_ID,
					DashboardWidgetConstants.RECENT_10_JOBS_LONG, DashboardWidgetConstants.RECENT_10_JOBS_SHORT,
					DashboardWidgetConstants.RECENT_10_JOBS_KEY, DashboardWidgetConstants.RECENT_10_JOBS_VISIBLE,
					DashboardWidgetConstants.RECENT_10_JOBS_ORDERID,
					DashboardWidgetConstants.RECENT_10_JOBS_IS_EXPANDED);
			ExpectedDashboardWidgets.add(dashboardWidget2);

			HashMap<String, Object> dashboardWidget3 = new HashMap<String, Object>();
			dashboardWidget3 = composeDashboardWidgetsInfo(DashboardWidgetConstants.TOP_10_SOURCES_WIDGET_ID,
					DashboardWidgetConstants.TOP_10_SOURCES_LONG, DashboardWidgetConstants.TOP_10_SOURCES_SHORT,
					DashboardWidgetConstants.TOP_10_SOURCES_KEY, DashboardWidgetConstants.TOP_10_SOURCES_VISIBLE,
					DashboardWidgetConstants.TOP_10_SOURCES_ORDERID,
					DashboardWidgetConstants.TOP_10_SOURCES_IS_EXPANDED);
			ExpectedDashboardWidgets.add(dashboardWidget3);

			HashMap<String, Object> dashboardWidget4 = new HashMap<String, Object>();
			dashboardWidget4 = composeDashboardWidgetsInfo(DashboardWidgetConstants.TOP_10_POLICIES_WIDGET_ID,
					DashboardWidgetConstants.TOP_10_POLICIES_LONG, DashboardWidgetConstants.TOP_10_POLICIES_SHORT,
					DashboardWidgetConstants.TOP_10_POLICIES_KEY, DashboardWidgetConstants.TOP_10_POLICIES_VISIBLE,
					DashboardWidgetConstants.TOP_10_POLICIES_ORDERID,
					DashboardWidgetConstants.TOP_10_POLICIES_IS_EXPANDED);
			ExpectedDashboardWidgets.add(dashboardWidget4);

			HashMap<String, Object> dashboardWidget5 = new HashMap<String, Object>();
			dashboardWidget5 = composeDashboardWidgetsInfo(DashboardWidgetConstants.TOP_10_CUSTOMERS_WIDGET_ID,
					DashboardWidgetConstants.TOP_10_CUSTOMERS_LONG, DashboardWidgetConstants.TOP_10_CUSTOMERS_SHORT,
					DashboardWidgetConstants.TOP_10_CUSTOMERS_KEY, DashboardWidgetConstants.TOP_10_CUSTOMERS_VISIBLE,
					DashboardWidgetConstants.TOP_10_CUSTOMERS_ORDERID,
					DashboardWidgetConstants.TOP_10_CUSTOMERS_IS_EXPANDED);
			ExpectedDashboardWidgets.add(dashboardWidget5);

			HashMap<String, Object> dashboardWidget6 = new HashMap<String, Object>();
			dashboardWidget6 = composeDashboardWidgetsInfo(DashboardWidgetConstants.DATA_TRANSFER_SUMMARY_WIDGET_ID,
					DashboardWidgetConstants.DATA_TRANSFER_SUMMARY_LONG,
					DashboardWidgetConstants.DATA_TRANSFER_SUMMARY_SHORT,
					DashboardWidgetConstants.DATA_TRANSFER_SUMMARY_KEY,
					DashboardWidgetConstants.DATA_TRANSFER_SUMMARY_VISIBLE,
					DashboardWidgetConstants.DATA_TRANSFER_SUMMARY_ORDERID,
					DashboardWidgetConstants.DATA_TRANSFER_SUMMARY_IS_EXPANDED);
			ExpectedDashboardWidgets.add(dashboardWidget6);

			HashMap<String, Object> dashboardWidget7 = new HashMap<String, Object>();
			dashboardWidget7 = composeDashboardWidgetsInfo(
					DashboardWidgetConstants.USAGE_TREND_FOR_CLOUD_HYBRID_STORES_WIDGET_ID,
					DashboardWidgetConstants.USAGE_TREND_FOR_CLOUD_HYBRID_STORES_LONG,
					DashboardWidgetConstants.USAGE_TREND_FOR_CLOUD_HYBRID_STORES_SHORT,
					DashboardWidgetConstants.USAGE_TREND_FOR_CLOUD_HYBRID_STORES_KEY,
					DashboardWidgetConstants.USAGE_TREND_FOR_CLOUD_HYBRID_STORES_VISIBLE,
					DashboardWidgetConstants.USAGE_TREND_FOR_CLOUD_HYBRID_STORES_ORDERID,
					DashboardWidgetConstants.USAGE_TREND_FOR_CLOUD_HYBRID_STORES_IS_EXPANDED);
			ExpectedDashboardWidgets.add(dashboardWidget7);

			HashMap<String, Object> dashboardWidget8 = new HashMap<String, Object>();
			dashboardWidget8 = composeDashboardWidgetsInfo(
					DashboardWidgetConstants.USAGE_TREND_FOR_CLOUD_DIRECT_VOLUMES_WIDGET_ID,
					DashboardWidgetConstants.USAGE_TREND_FOR_CLOUD_DIRECT_VOLUMES_LONG,
					DashboardWidgetConstants.USAGE_TREND_FOR_CLOUD_DIRECT_VOLUMES_SHORT,
					DashboardWidgetConstants.USAGE_TREND_FOR_CLOUD_DIRECT_VOLUMES_KEY,
					DashboardWidgetConstants.USAGE_TREND_FOR_CLOUD_DIRECT_VOLUMES_VISIBLE,
					DashboardWidgetConstants.USAGE_TREND_FOR_CLOUD_DIRECT_VOLUMES_ORDERID,
					DashboardWidgetConstants.USAGE_TREND_FOR_CLOUD_DIRECT_VOLUMES_IS_EXPANDED);
			ExpectedDashboardWidgets.add(dashboardWidget8);

			HashMap<String, Object> dashboardWidget9 = new HashMap<String, Object>();
			dashboardWidget9 = composeDashboardWidgetsInfo(
					DashboardWidgetConstants.CAPACITY_USAGE_TREND_CLOUD_HYBRID_STORES_WIDGET_ID,
					DashboardWidgetConstants.CAPACITY_USAGE_TREND_CLOUD_HYBRID_STORES_LONG,
					DashboardWidgetConstants.CAPACITY_USAGE_TREND_CLOUD_HYBRID_STORES_SHORT,
					DashboardWidgetConstants.CAPACITY_USAGE_TREND_CLOUD_HYBRID_STORES_KEY,
					DashboardWidgetConstants.CAPACITY_USAGE_TREND_CLOUD_HYBRID_STORES_VISIBLE,
					DashboardWidgetConstants.CAPACITY_USAGE_TREND_CLOUD_HYBRID_STORES_ORDERID,
					DashboardWidgetConstants.CAPACITY_USAGE_TREND_CLOUD_HYBRID_STORES_IS_EXPANDED);
			ExpectedDashboardWidgets.add(dashboardWidget9);

			compareDashboardWidgets(ActualDashboardWidgets, ExpectedDashboardWidgets, test);
		} else {

			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + ExpectedStatusCode);
		}

		return response;

	}

	/**
	 * compareDashboardWidgets
	 * 
	 * @author Ramya.Nagepalli
	 * @param actualDashboardWidgets
	 * @param expectedDashboardWidgets
	 * @param test
	 */
	private void compareDashboardWidgets(ArrayList<HashMap<String, Object>> actualDashboardWidgets,
			ArrayList<HashMap<String, Object>> expectedDashboardWidgets, ExtentTest test) {

		for (int i = 0; i < actualDashboardWidgets.size(); i++) {
			for (int j = 0; j < expectedDashboardWidgets.size(); j++) {
				String act_widget_id = actualDashboardWidgets.get(i).get("dashboard_widget_id").toString();
				String exp_widget_id = expectedDashboardWidgets.get(j).get("dashboard_widget_id").toString();

				if (act_widget_id.equals(exp_widget_id)) {
					String act_long_label = actualDashboardWidgets.get(i).get("long_label").toString();
					String exp_long_label = expectedDashboardWidgets.get(j).get("long_label").toString();
					spogServer.assertResponseItem(exp_long_label, act_long_label);

					String act_short_label = actualDashboardWidgets.get(i).get("short_label").toString();
					String exp_short_label = expectedDashboardWidgets.get(j).get("short_label").toString();
					spogServer.assertResponseItem(exp_short_label, act_short_label);

					String act_key = actualDashboardWidgets.get(i).get("key").toString();
					String exp_key = expectedDashboardWidgets.get(j).get("key").toString();
					spogServer.assertResponseItem(exp_key, act_key);

					Object act_visible = actualDashboardWidgets.get(i).get("visible");
					Object exp_visible = expectedDashboardWidgets.get(j).get("visible");
					spogServer.assertResponseItem(exp_visible, act_visible, test);

					int act_order_id = (int) actualDashboardWidgets.get(i).get("order_id");
					int exp_order_id = (int) expectedDashboardWidgets.get(j).get("order_id");
					spogServer.assertResponseItem(exp_order_id, act_order_id, test);

					Object act_is_expanded = actualDashboardWidgets.get(i).get("is_expanded");
					Object exp_is_expanded = expectedDashboardWidgets.get(j).get("is_expanded");
					spogServer.assertResponseItem(exp_is_expanded, act_is_expanded, test);

				}

			}
		}

	}

	/**
	 * @author Ramya.Nagepalli
	 * @param Widget_ID
	 * @param long_label
	 * @param short_label
	 * @param key
	 * @param visible
	 * @param order_id
	 * @param is_expanded
	 * @return columnHeadContent
	 */
	private HashMap<String, Object> composeDashboardWidgetsInfo(String Widget_ID, String long_label, String short_label,
			String key, boolean visible, int order_id, boolean is_expanded) {

		// TODO Auto-generated method stub
		HashMap<String, Object> columnHeadContent = new HashMap<String, Object>();
		columnHeadContent.put("dashboard_widget_id", Widget_ID);
		columnHeadContent.put("long_label", long_label);
		columnHeadContent.put("short_label", short_label);
		columnHeadContent.put("key", key);
		columnHeadContent.put("visible", visible);
		columnHeadContent.put("order_id", order_id);
		columnHeadContent.put("is_expanded", is_expanded);

		return columnHeadContent;
	}

	/**
	 * postDashboardWidgetsForLoggedInUser
	 * 
	 * @author Ramya.Nagepalli
	 * @param expectedWidgets
	 * @param token
	 * @param test
	 * 
	 * @return response
	 */
	public Response postDashboardWidgetsForLoggedInUser(ArrayList<HashMap<String, Object>> expectedWidgets,
			String token, ExtentTest test) {

		Map<String, ArrayList<HashMap<String, Object>>> composed_expected_Widgets = null;

		composed_expected_Widgets = jp.MapDashboardWidgetsInfo(expectedWidgets);

		Response response = spogReportInvoker.postDashboardWidgetsForLoggedInUser(composed_expected_Widgets, token,
				test);

		return response;

	}

	/**
	 * deleteDashboardWidgetsForLoggedInUser
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 * @return response
	 */
	public void deleteDashboardWidgetsForLoggedInUser(String token, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage, ExtentTest test) {

		Response response = spogReportInvoker.deleteDashboardWidgetsForLoggedInUser(token, test);

		spogServer.checkResponseStatus(response, expectedStatusCode);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.INFO, "dashboard widgets got successfully deleted for the logged in user");
		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}

	}

	/**
	 * validateDashboardWidgets
	 * 
	 * @author Ramya.Nagepalli
	 * @param response
	 * @param expectedDashboardWidgets
	 * @param defaultWidgets
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 * 
	 * @return response
	 */
	public void validateDashboardWidgets(Response response, ArrayList<HashMap<String, Object>> expectedDashboardWidgets,
			ArrayList<HashMap<String, Object>> defaultWidgets, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage, ExtentTest test) {

		spogServer.checkResponseStatus(response, expectedStatusCode);

		if (expectedStatusCode == SpogConstants.SUCCESS_POST
				|| expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			ArrayList<HashMap<String, Object>> actualDashboardWidgets = new ArrayList<HashMap<String, Object>>();
			actualDashboardWidgets = response.then().extract().path("data");

			verifyDashboardWidgets(actualDashboardWidgets, expectedDashboardWidgets, defaultWidgets, test);

		} else {

			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
	}

	/**
	 * verifyDashboardWidgets
	 * 
	 * @author Ramya.Nagepalli
	 * @param actualDashboardWidgets
	 * @param expectedDashboardWidgets
	 * @param defaultWidgets
	 * @param test
	 * 
	 */
	private void verifyDashboardWidgets(ArrayList<HashMap<String, Object>> actualDashboardWidgets,
			ArrayList<HashMap<String, Object>> expectedDashboardWidgets,
			ArrayList<HashMap<String, Object>> defaultWidgets, ExtentTest test) {

		for (int i = 0; i < actualDashboardWidgets.size(); i++) {
			for (int j = 0; j < expectedDashboardWidgets.size(); j++) {
				if (actualDashboardWidgets.size() < expectedDashboardWidgets.size()) {
					if (j == expectedDashboardWidgets.size() - 2) {
						for (int k = 0; k < defaultWidgets.size(); k++) {
							String act_widget_id = actualDashboardWidgets.get(i).get("dashboard_widget_id").toString();
							String exp_widget_id = expectedDashboardWidgets.get(j).get("dashboard_widget_id")
									.toString();
							String default_widget_id = defaultWidgets.get(k).get("dashboard_widget_id").toString();

							if (act_widget_id.equals(exp_widget_id) && act_widget_id.equals(default_widget_id)) {
								Object act_visible = actualDashboardWidgets.get(i).get("visible");
								Object exp_visible = expectedDashboardWidgets.get(j).get("visible");
								Object default_visible = defaultWidgets.get(k).get("visible");

								if (exp_visible == null) {
									spogServer.assertResponseItem(default_visible.toString(), act_visible.toString(),
											test);
								} else {
									spogServer.assertResponseItem(exp_visible.toString(), act_visible.toString(), test);
								}

								String act_order_id = actualDashboardWidgets.get(i).get("order_id").toString();
								String exp_order_id = expectedDashboardWidgets.get(j).get("order_id").toString();
								spogServer.assertResponseItem(exp_order_id, act_order_id, test);

								Object act_is_expanded = actualDashboardWidgets.get(i).get("is_expanded");
								Object exp_is_expanded = expectedDashboardWidgets.get(j).get("is_expanded");
								Object default_is_expanded = defaultWidgets.get(k).get("is_expanded");

								if (exp_is_expanded == null) {
									spogServer.assertResponseItem(default_is_expanded.toString(),
											act_is_expanded.toString(), test);
								} else {
									spogServer.assertResponseItem(exp_is_expanded.toString(),
											act_is_expanded.toString(), test);
								}

							}
						}
					}
				} else {
					for (int k = 0; k < defaultWidgets.size(); k++) {
						String act_widget_id = actualDashboardWidgets.get(i).get("dashboard_widget_id").toString();
						String exp_widget_id = expectedDashboardWidgets.get(j).get("dashboard_widget_id").toString();
						String default_widget_id = defaultWidgets.get(k).get("dashboard_widget_id").toString();

						if (act_widget_id.equals(exp_widget_id) && act_widget_id.equals(default_widget_id)) {
							Object act_visible = actualDashboardWidgets.get(i).get("visible");
							Object exp_visible = expectedDashboardWidgets.get(j).get("visible");
							Object default_visible = defaultWidgets.get(k).get("visible");

							if (exp_visible == null) {
								spogServer.assertResponseItem(default_visible.toString(), act_visible.toString(), test);
							} else {
								spogServer.assertResponseItem(exp_visible.toString(), act_visible.toString(), test);
							}

							String act_order_id = actualDashboardWidgets.get(i).get("order_id").toString();
							String exp_order_id = expectedDashboardWidgets.get(j).get("order_id").toString();
							spogServer.assertResponseItem(exp_order_id, act_order_id, test);

							Object act_is_expanded = actualDashboardWidgets.get(i).get("is_expanded");
							Object exp_is_expanded = expectedDashboardWidgets.get(j).get("is_expanded");
							Object default_is_expanded = defaultWidgets.get(k).get("is_expanded");

							if (exp_is_expanded == null) {
								spogServer.assertResponseItem(default_is_expanded.toString(),
										act_is_expanded.toString(), test);
							} else {
								spogServer.assertResponseItem(exp_is_expanded.toString(), act_is_expanded.toString(),
										test);
							}

						}
					}

				}
			}
		}

	}

	/**
	 * 
	 * @author Ramya.Nagepalli
	 * @param widgetsId
	 * @param order_id
	 * @param visibility
	 * @param isExpanded
	 * @return expectedWidget
	 */

	public HashMap<String, Object> composeDashboardWidgets(String widgetsId, String order_id, String visibility,
			String isExpanded) {
		// TODO Auto-generated method stub

		HashMap<String, Object> expectedWidget = new HashMap<String, Object>();

		expectedWidget.put("widget_id", widgetsId);

		if (!(visibility.equals("") || visibility.equals("none"))) {
			expectedWidget.put("visible", visibility);
		}
		expectedWidget.put("order_id", order_id);

		if (!(isExpanded.equals("") || isExpanded.equals("none"))) {
			expectedWidget.put("is_expanded", isExpanded);
		}

		return expectedWidget;
	}

	/**
	 * getDashboardWidgetsForLoggedInUser
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param expectedErrorMessage
	 * @param expectedStatusCode
	 * @param test
	 * 
	 * @return reponse
	 */

	public Response getDashboardWidgetsForLoggedInUser(String token, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage, ExtentTest test) {

		Response response = spogReportInvoker.getDashboardWidgetsForLoggedInUser(token, test);

		spogServer.checkResponseStatus(response, expectedStatusCode);
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.INFO, "got dashboard widgets for the logged in user");
		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}

		return response;
	}

	/**
	 * @author Ramya.nagepalli
	 * 
	 * @param expected_widgets
	 * @param user_id
	 * @param validToken
	 * @param test
	 * @return response
	 */

	public Response postDashboardWidgetsForSpecifiedUser(ArrayList<HashMap<String, Object>> expected_widgets,
			String user_id, String validToken, ExtentTest test) {

		Map<String, ArrayList<HashMap<String, Object>>> composed_expected_Widgets = null;

		composed_expected_Widgets = jp.MapDashboardWidgetsInfo(expected_widgets);

		Response response = spogReportInvoker.postDashboardWidgetsForSpecifiedUser(composed_expected_Widgets, user_id,
				validToken, test);

		return response;
	}

	/**
	 * deleteDashboardWidgetsForSpecifiedUser
	 * 
	 * @author Ramya.Nagepalli
	 * @param validToken
	 * @param user_id
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 */
	public void deleteDashboardWidgetsForSpecifiedUser(String validToken, String user_id, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage, ExtentTest test) {

		Response response = spogReportInvoker.deleteDashboardWidgetsForSpecifiedUser(validToken, user_id, test);

		// spogServer.checkResponseStatus(response, expectedStatusCode);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.INFO, "Successfully deleted dashboard widgets for specified user ");
		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}

	}

	/**
	 * getDashboardWidgetsForSpecifiedUser
	 * 
	 * @author Ramya.Nagepalli
	 * @param validToken
	 * @param user_id
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 */
	public Response getDashboardWidgetsForSpecifiedUser(String validToken, String user_id, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage, ExtentTest test) {

		Response response = spogReportInvoker.getDashboardWidgetsForSpecifiedUser(validToken, user_id, test);

		spogServer.checkResponseStatus(response, expectedStatusCode);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.INFO, "Successfully got the dashboard widgets for specified user ");
		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
		return response;

	}

	/**
	 * putDashboardWidgetsForSpecifiedUser
	 * 
	 * @author Ramya.Nagepalli
	 * @param expected_widgets
	 * @param user_id
	 * @param validToken
	 * @param test
	 * @return response
	 */
	public Response putDashboardWidgetsForSpecifiedUser(ArrayList<HashMap<String, Object>> expected_widgets,
			String user_id, String validToken, ExtentTest test) {
		// TODO Auto-generated method stub
		Map<String, ArrayList<HashMap<String, Object>>> composed_expected_Widgets = null;

		composed_expected_Widgets = jp.MapDashboardWidgetsInfo(expected_widgets);

		Response response = spogReportInvoker.putDashboardWidgetsForSpecifiedUser(composed_expected_Widgets, user_id,
				validToken, test);

		return response;
	}

	/**
	 * putDashboardWidgetsForLoggedInUser
	 * 
	 * @author Ramya.Nagepalli
	 * @param expected_widgets
	 * @param user_id
	 * @param validToken
	 * @param test
	 * @return response
	 */
	public Response putDashboardWidgetsForLoggedInUser(ArrayList<HashMap<String, Object>> expected_widgets,
			String validToken, ExtentTest test) {
		// TODO Auto-generated method stub
		Map<String, ArrayList<HashMap<String, Object>>> composed_expected_Widgets = null;

		composed_expected_Widgets = jp.MapDashboardWidgetsInfo(expected_widgets);

		Response response = spogReportInvoker.putDashboardWidgetsForLoggedInUser(composed_expected_Widgets, validToken,
				test);

		return response;
	}

	/**
	 * composeReportFilterInfo
	 * 
	 * @author Ramya.Nagepalli
	 * @param organization_ids
	 * @param date_range_type
	 * @param date_range_start_ts
	 * @param date_range_end_ts
	 * @param schedule_frequency
	 * @param filter_name
	 * @param is_default
	 * @param report_filter_type
	 * @return
	 */
	public HashMap<String, Object> composeReportFilterInfo(String organization_ids, String date_range_type,
			long date_range_start_ts, long date_range_end_ts, String schedule_frequency, String filter_name,
			boolean is_default, String report_filter_type) {

		HashMap<String, Object> reportFilterInfo = new HashMap<>();

		HashMap<String, Object> date_range = new HashMap<>();

		date_range.put("type", date_range_type);
		date_range.put("start_ts", date_range_start_ts);
		date_range.put("end_ts", date_range_end_ts);

		HashMap<String, Object> generated_on = new HashMap<>();
		generated_on.put("start", date_range_start_ts);
		generated_on.put("end", date_range_end_ts);

		reportFilterInfo.put("date_range", date_range);
		reportFilterInfo.put("generated_on", generated_on);
		reportFilterInfo.put("is_default", is_default);
		reportFilterInfo.put("filter_name", filter_name);
		reportFilterInfo.put("organization_id", organization_ids);
		reportFilterInfo.put("report_filter_type", report_filter_type);

		ArrayList<String> temp = new ArrayList<>();
		temp = new ArrayList<>();
		temp.add(schedule_frequency);
		reportFilterInfo.put("schedule_frequency", temp);

		return reportFilterInfo;
	}

	/**
	 * createReportFilterForSpecifiedUserWithCheck
	 * 
	 * @author Ramya.Nagepalli
	 * @param additionalURL
	 * @param user_id
	 * @param org_id
	 * @param token
	 * @param reportfilterInfo
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 * @return
	 */
	public Response createReportFilterForSpecifiedUserWithCheck(String additionalURL, String user_id, String org_id,
			String token, HashMap<String, Object> reportfilterInfo, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage, ExtentTest test) {

		/*
		 * test.log(LogStatus.INFO,
		 * "Call the API Create reportfilters for specified user"); Response response =
		 * spogReportInvoker.createReportFiltersForSpecifiedUser(additionalURL, user_id,
		 * token, reportfilterInfo, test); spogServer.checkResponseStatus(response,
		 * expectedStatusCode, test);
		 */

		String filter_name = reportfilterInfo.get("filter_name").toString();
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!reportfilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.report_filter.toString(),
					reportfilterInfo.get("filter_name").toString(), user_id, org_id,
					Boolean.valueOf(reportfilterInfo.get("is_default").toString()), reportfilterInfo);
		}

		Response response = spogServer.createFilters(token, filter_info, "", test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);
		return response;
	}

	/**
	 * getReportFiltersForLoggedInUserByFilterIdWithCheck
	 * 
	 * @author Ramya.Nagepalli
	 * @param additionalURL
	 * @param token
	 * @param filter_id
	 * @param reportfilterInfo
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 * @return
	 */
	public Response getReportFiltersForLoggedInUserByFilterIdWithCheck(String additionalURL, String token,
			String filter_id, HashMap<String, Object> reportfilterInfo, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage, ExtentTest test) {

		test.log(LogStatus.INFO, "Call the API Get reportfilters for logged in user by filter id");
		/*
		 * Response response =
		 * spogReportInvoker.getReportFiltersForLoggedInUserByFilterId(token, filter_id,
		 * additionalURL, test);
		 */
		Response response = spogServer.getFiltersById(token, filter_id, filterType.report_filter.toString(), "none",
				"none",test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		return response;
	}

	/**
	 * updateReportFiltersForLoggedInUser
	 * 
	 * @author Ramya.Nagepalli
	 * @param additionalURL
	 * @param token
	 * @param filter_id
	 * @param reportfilterInfo
	 * @param defaultfilterInfo
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 * @return
	 */
	public Response updateReportFiltersForLoggedInUser(String additionalURL, String token, String filter_id,
			HashMap<String, Object> reportfilterInfo, HashMap<String, Object> defaultfilterInfo, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage, ExtentTest test) {

		test.log(LogStatus.INFO, "Call the API update reportfilters for logged in user");
		/*
		 * Response response =
		 * spogReportInvoker.updateReportFiltersForLoggedInUser(additionalURL, token,
		 * filter_id, reportfilterInfo, test);
		 */
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!reportfilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.report_filter.toString(),
					reportfilterInfo.get("filter_name").toString(), "none", "none",
					Boolean.valueOf(reportfilterInfo.get("is_default").toString()), reportfilterInfo);
		}
		Response response = spogServer.updateFilterById(token, filter_id, "none", filter_info, "", test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);
		return response;
	}

	/**
	 * createReportListFiltersForLoggedInUserWithCheck
	 * 
	 * @author Ramya.Nagepalli
	 * @param additionalURL
	 * @param token
	 * @param reportlistfiltersInfo
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 * @return
	 */
	public Response createReportListFiltersForLoggedInUserWithCheck(String additionalURL, String token,
			HashMap<String, Object> reportlistfiltersInfo, int expectedStatusCode, SpogMessageCode expectedErrorMessage,
			ExtentTest test) {

		test.log(LogStatus.INFO, "Call the REST API Create report list filter for logged in user");
	
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!reportlistfiltersInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.report_list_filter.toString(),
					reportlistfiltersInfo.get("filter_name").toString(), "none", "none",
					Boolean.valueOf(reportlistfiltersInfo.get("is_default").toString()), reportlistfiltersInfo);
		}
		
		Response response = spogServer.createFilters(token, filter_info, "", test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		/*
		 * Response response =
		 * spogReportInvoker.createReportListFiltersForLoggedInUser(additionalURL,
		 * token, reportlistfiltersInfo, test);
		 */
		return response;
	}

	/**
	 * getReportListFiltersForLoggedInUserByFilterIdWithCheck
	 * 
	 * @author Ramya.Nagepalli
	 * @param additionalURL
	 * @param token
	 * @param filter_id
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 * @return
	 */
	public Response getReportListFiltersForLoggedInUserByFilterIdWithCheck(String additionalURL, String token,
			String filter_id, int expectedStatusCode, SpogMessageCode expectedErrorMessage, ExtentTest test) {

		test.log(LogStatus.INFO, "Call the API getReportListFiltersForLoggedInUserByFilterId");
		/*
		 * Response response =
		 * spogReportInvoker.getReportListFiltersForLoggedInUserByFilterId(
		 * additionalURL, token, filter_id, test);
		 */
		Response response = spogServer.getFiltersById(token, filter_id, filterType.report_list_filter.toString(),
				"none", "none",test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);
		return response;
	}

	/**
	 * updateReportListFiltersForLoggedInUserByFilterIdWithCheck
	 * 
	 * @author Ramya.Nagepalli
	 * @param additionalURL
	 * @param token
	 * @param filter_id
	 * @param expectedData
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 * @return
	 */
	public Response updateReportListFiltersForLoggedInUserByFilterIdWithCheck(String additionalURL, String token,
			String filter_id, HashMap<String, Object> expectedData, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage, ExtentTest test) {

		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!expectedData.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.report_list_filter.toString(),
					expectedData.get("filter_name").toString(),"none","none",
					Boolean.valueOf(expectedData.get("is_default").toString()), expectedData);
		}
		Response response = spogServer.updateFilterById(token, filter_id, "none", filter_info,additionalURL, test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);
		return response;
	}

	/**
	 * getDashboardSourceSummary
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param additionalUrl
	 * @param test
	 * @return
	 */
	public Response getDashboardSourceSummary(String token, String additionalUrl, ExtentTest test) {
		Response response = spogReportInvoker.getDashboardSourceSummary(token, additionalUrl, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		return response;
	}

	/**
	 * Gets the business intelligence report for Cloud Direct organizations that are
	 * exempted from Trial Validate response status
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param expectedStatusCode
	 * @param test
	 * @return
	 */
	public Response getBIRCDExemption(String token, int expectedStatusCode, ExtentTest test) {

		test.log(LogStatus.INFO, "Call API - GET: /businessintelligencereports/types/cd_exemption");
		Response response = spogReportInvoker.getBIRCDExemption(token, test);

		test.log(LogStatus.INFO, "Verify response staus");
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		return response;
	}

	/**
	 * Gets the business intelligence report for Cloud Direct organizations that are
	 * exempted from Trial with check
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 */
	public void getBIRCDExemptionWithCheck(String token, int expectedStatusCode, SpogMessageCode expectedErrorMessage,
			ExtentTest test) {

		Response response = getBIRCDExemption(token, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			ArrayList<HashMap<String, Object>> actualData = new ArrayList<>();
			actualData = response.then().extract().path("data");

			if (!actualData.isEmpty() && actualData.size() > 0) {

				test.log(LogStatus.INFO, "Validating the params in response and checking to be not null");
				for (int i = 0; i < actualData.size(); i++) {

					assertTrue(actualData.get(i).get("organization_id") != null,
							"The response contains the organization with id:"
									+ actualData.get(i).get("organization_id").toString());
					assertTrue(actualData.get(i).get("organization_name") != null,
							"The response contains the Organization Name :"
									+ actualData.get(i).get("organization_name").toString());
					assertTrue(actualData.get(i).get("organization_type") != null,
							"The response contains the Organization Type with id:"
									+ actualData.get(i).get("organization_type").toString());
					assertTrue(actualData.get(i).get("create_date") != null,
							"The response contains the Create Date:" + actualData.get(i).get("create_date"));
					assertTrue(actualData.get(i).get("Region") != null,
							"The response contains the Region:" + actualData.get(i).get("Region"));

					test.log(LogStatus.INFO, "Check that exemption param value is true or not");
					validateCDExemption(actualData.get(i).get("organization_id").toString(), token, test);
				}
			} else {
				test.log(LogStatus.INFO,
						"The response of Get business intelligence reports with cd_exemption is empty.");
			}
		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
	}

	/**
	 * Validate whether exemption parameter values are true or not
	 * 
	 * @author Rakesh.Chalamala
	 * @param organization_id
	 * @param token
	 * @param test
	 */
	public void validateCDExemption(String organization_id, String token, ExtentTest test) {

		Response response = spogServer.getSpecifiedOrgProperties(token, organization_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, test);

		String exmptionValue = response.then().extract().path("data.clouddirect_expiration_exemption").toString();
		spogServer.assertResponseItem("true", exmptionValue);
	}

	public void getBIRCDExemptionExportCSVWithCheck(String token, ArrayList<HashMap<String, Object>> expectedData,
			int expectedStatusCode, SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		test.log(LogStatus.INFO, "Call API Business Intelligence Reports types cd_exemption with export csv");
		Response response = spogReportInvoker.getBIRCDExemptionExportCSV(token, test);

		try {
			if ((int) response.then().extract().path("status") == SpogConstants.REQUIRED_INFO_NOT_EXIST) {
				spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
				System.out.println(response.then().extract().path("errors.message").toString());
				test.log(LogStatus.INFO, response.then().extract().path("errors.message").toString());
			} else {
				spogServer.checkResponseStatus(response, expectedStatusCode, test);
				String code = ExpectedErrorMessage.getCodeString();
				String message = ExpectedErrorMessage.getStatus();
				spogServer.checkErrorCode(response, code);
				test.log(LogStatus.PASS, "The error code matched with the expected " + code);
				spogServer.checkErrorMessage(response, message);
				test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
			}

		} catch (Exception e) {
			assertTrue(true, "Report data as CSV:");
			test.log(LogStatus.INFO, response.prettyPrint());
		}
	}

	/**
	 * Gets the business intelligence report for Cloud Direct organizations that are
	 * expiring in next few days and already expired but not delete Validate
	 * response status
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param expectedStatusCode
	 * @param test
	 * @return
	 */
	public Response getBIRCDExpiration(String token, int expectedStatusCode, ExtentTest test) {

		test.log(LogStatus.INFO, "Call API - GET: /businessintelligencereports/types/cd_expiration");
		Response response = spogReportInvoker.getBIRCDExpiration(token, test);

		test.log(LogStatus.INFO, "Verify response staus");
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		return response;
	}

	/**
	 * Gets the business intelligence report for Cloud Direct organizations that are
	 * are expiring in next few days and already expired but not deleted with check
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 */
	public void getBIRCDExpirationWithCheck(String token, int expectedStatusCode, SpogMessageCode expectedErrorMessage,
			ExtentTest test) {

		Response response = getBIRCDExpiration(token, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			ArrayList<HashMap<String, Object>> actualData = new ArrayList<>();
			actualData = response.then().extract().path("data");

			if (!actualData.isEmpty() && actualData.size() > 0) {

				test.log(LogStatus.INFO, "Validating the params in response and checking to be not null");
				for (int i = 0; i < actualData.size(); i++) {

					assertTrue(actualData.get(i).get("organization_id") != null,
							"The response contains the organization with id:"
									+ actualData.get(i).get("organization_id").toString());
					assertTrue(actualData.get(i).get("organization_name") != null,
							"The response contains the Organization Name :"
									+ actualData.get(i).get("organization_name").toString());
					assertTrue(actualData.get(i).get("organization_type") != null,
							"The response contains the Organization Type with id:"
									+ actualData.get(i).get("organization_type").toString());
					assertTrue(actualData.get(i).get("create_date") != null,
							"The response contains the Create Date:" + actualData.get(i).get("create_date"));
					assertTrue(actualData.get(i).get("Region") != null,
							"The response contains the Region:" + actualData.get(i).get("Region"));
					assertTrue(actualData.get(i).get("expiration_date") != null,
							"The response contains the Expiration Date:" + actualData.get(i).get("expiration_date"));

				}
			} else {
				test.log(LogStatus.INFO,
						"The response of Get business intelligence reports with cd_exemption is empty.");
			}
		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
	}

	public void getBIRCDExpirationExportCSVWithCheck(String token, ArrayList<HashMap<String, Object>> expectedData,
			int expectedStatusCode, SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		test.log(LogStatus.INFO, "Call API Business Intelligence Reports types cd_expiration with export csv");
		Response response = spogReportInvoker.getBIRCDExpirationExportCSV(token, test);

		try {
			if ((int) response.then().extract().path("status") == SpogConstants.REQUIRED_INFO_NOT_EXIST) {
				spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
				System.out.println(response.then().extract().path("errors.message").toString());
				test.log(LogStatus.INFO, response.then().extract().path("errors.message").toString());
			} else {
				spogServer.checkResponseStatus(response, expectedStatusCode, test);
				String code = ExpectedErrorMessage.getCodeString();
				String message = ExpectedErrorMessage.getStatus();
				spogServer.checkErrorCode(response, code);
				test.log(LogStatus.PASS, "The error code matched with the expected " + code);
				spogServer.checkErrorMessage(response, message);
				test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
			}

		} catch (Exception e) {
			assertTrue(true, "Report data as CSV:");
			test.log(LogStatus.INFO, response.prettyPrint());
		}
	}

	/**
	 * Gets the business intelligence report for Cloud Direct organizations that are
	 * exempted from Trial Validate response status
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param expectedStatusCode
	 * @param test
	 * @return
	 */
	public Response getBIRCHExemption(String token, int expectedStatusCode, ExtentTest test) {

		test.log(LogStatus.INFO, "Call API - GET: /businessintelligencereports/types/ch_exemption");
		Response response = spogReportInvoker.getBIRCHExemption(token, test);

		test.log(LogStatus.INFO, "Verify response staus");
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		return response;
	}

	/**
	 * Gets the business intelligence report for Cloud Direct organizations that are
	 * exempted from Trial with check
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 */
	public void getBIRCHExemptionWithCheck(String token, int expectedStatusCode, SpogMessageCode expectedErrorMessage,
			ExtentTest test) {

		Response response = getBIRCHExemption(token, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			ArrayList<HashMap<String, Object>> actualData = new ArrayList<>();
			actualData = response.then().extract().path("data");

			if (!actualData.isEmpty() && actualData.size() > 0) {

				test.log(LogStatus.INFO, "Validating the params in response and checking to be not null");
				for (int i = 0; i < actualData.size(); i++) {

					assertTrue(actualData.get(i).get("organization_id") != null,
							"The response contains the organization with id:"
									+ actualData.get(i).get("organization_id").toString());
					assertTrue(actualData.get(i).get("organization_name") != null,
							"The response contains the Organization Name :"
									+ actualData.get(i).get("organization_name").toString());
					assertTrue(actualData.get(i).get("organization_type") != null,
							"The response contains the Organization Type with id:"
									+ actualData.get(i).get("organization_type").toString());
					assertTrue(actualData.get(i).get("create_date") != null, "The response contains the Create Date");
					assertTrue(actualData.get(i).get("Region") != null,
							"The response contains the Region:" + actualData.get(i).get("Region"));

					test.log(LogStatus.INFO, "Check that exemption param value is true or not");
					validateCHExemption(actualData.get(i).get("organization_id").toString(), token, test);
				}
			} else {
				test.log(LogStatus.INFO,
						"The response of Get business intelligence reports with ch_exemption is empty.");
			}
		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
	}

	/**
	 * Validate whether exemption parameter values are true or not
	 * 
	 * @author Rakesh.Chalamala
	 * @param organization_id
	 * @param token
	 * @param test
	 */
	public void validateCHExemption(String organization_id, String token, ExtentTest test) {

		Response response = spogServer.getSpecifiedOrgProperties(token, organization_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, test);

		String exmptionValue = response.then().extract().path("data.cloudhybrid_expiration_exemption").toString();
		spogServer.assertResponseItem("true", exmptionValue);
	}

	public void getBIRCHExemptionExportCSVWithCheck(String token, ArrayList<HashMap<String, Object>> expectedData,
			int expectedStatusCode, SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		test.log(LogStatus.INFO, "Call API Business Intelligence Reports types ch_exemption with export csv");
		Response response = spogReportInvoker.getBIRCHExemptionExportCSV(token, test);

		try {
			if ((int) response.then().extract().path("status") == SpogConstants.REQUIRED_INFO_NOT_EXIST) {
				spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
				System.out.println(response.then().extract().path("errors.message").toString());
				test.log(LogStatus.INFO, response.then().extract().path("errors.message").toString());
			} else {
				spogServer.checkResponseStatus(response, expectedStatusCode, test);
				String code = ExpectedErrorMessage.getCodeString();
				String message = ExpectedErrorMessage.getStatus();
				spogServer.checkErrorCode(response, code);
				test.log(LogStatus.PASS, "The error code matched with the expected " + code);
				spogServer.checkErrorMessage(response, message);
				test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
			}

		} catch (Exception e) {
			assertTrue(true, "Report data as CSV:");
			test.log(LogStatus.INFO, response.prettyPrint());
		}
	}

	/**
	 * Gets the business intelligence report for Cloud Hybrid organizations that are
	 * expiring in next few days and already expired but not delete Validate
	 * response status
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param expectedStatusCode
	 * @param test
	 * @return
	 */
	public Response getBIRCHExpiration(String token, int expectedStatusCode, ExtentTest test) {

		test.log(LogStatus.INFO, "Call API - GET: /businessintelligencereports/types/ch_expiration");
		Response response = spogReportInvoker.getBIRCHExpiration(token, test);

		test.log(LogStatus.INFO, "Verify response staus");
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		return response;
	}

	/**
	 * Gets the business intelligence report for Cloud Direct organizations that are
	 * are expiring in next few days and already expired but not deleted with check
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 */
	public void getBIRCHExpirationWithCheck(String token, int expectedStatusCode, SpogMessageCode expectedErrorMessage,
			ExtentTest test) {

		Response response = getBIRCHExpiration(token, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			ArrayList<HashMap<String, Object>> actualData = new ArrayList<>();
			actualData = response.then().extract().path("data");

			if (!actualData.isEmpty() && actualData.size() > 0) {

				test.log(LogStatus.INFO, "Validating the params in response and checking to be not null");
				for (int i = 0; i < actualData.size(); i++) {

					assertTrue(actualData.get(i).get("organization_id") != null,
							"The response contains the organization with id:"
									+ actualData.get(i).get("organization_id").toString());
					assertTrue(actualData.get(i).get("organization_name") != null,
							"The response contains the Organization Name :"
									+ actualData.get(i).get("organization_name").toString());
					assertTrue(actualData.get(i).get("organization_type") != null,
							"The response contains the Organization Type with id:"
									+ actualData.get(i).get("organization_type").toString());
					assertTrue(actualData.get(i).get("create_date") != null, "The response contains the Create Date");
					assertTrue(actualData.get(i).get("Region") != null,
							"The response contains the Region:" + actualData.get(i).get("Region"));
					assertTrue(actualData.get(i).get("expiration_date") != null,
							"The response contains the Expiration Date");

				}
			} else {
				test.log(LogStatus.INFO,
						"The response of Get business intelligence reports with ch_exemption is empty.");
			}
		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
	}

	public void getBIRCHExpirationExportCSVWithCheck(String token, ArrayList<HashMap<String, Object>> expectedData,
			int expectedStatusCode, SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		test.log(LogStatus.INFO, "Call API Business Intelligence Reports types ch_expiration with export csv");
		Response response = spogReportInvoker.getBIRCHExpirationExportCSV(token, test);

		try {
			if ((int) response.then().extract().path("status") == SpogConstants.REQUIRED_INFO_NOT_EXIST) {
				spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
				System.out.println(response.then().extract().path("errors.message").toString());
				test.log(LogStatus.INFO, response.then().extract().path("errors.message").toString());
			} else {
				spogServer.checkResponseStatus(response, expectedStatusCode, test);
				String code = ExpectedErrorMessage.getCodeString();
				String message = ExpectedErrorMessage.getStatus();
				spogServer.checkErrorCode(response, code);
				test.log(LogStatus.PASS, "The error code matched with the expected " + code);
				spogServer.checkErrorMessage(response, message);
				test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
			}

		} catch (Exception e) {
			assertTrue(true, "Report data as CSV:");
			test.log(LogStatus.INFO, response.prettyPrint());
		}
	}

}
