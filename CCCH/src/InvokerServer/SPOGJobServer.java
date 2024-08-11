package InvokerServer;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.PrivateBinder;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.CloudDirectRetentionValues;
import Constants.ConnectionStatus;
import Constants.DestinationStatus;
import Constants.DestinationType;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import Constants.FilterTypes.filterType;
import dataPreparation.JsonPreparation;
import genericutil.ErrorHandler;
import invoker.SPOGDestinationInvoker;
import invoker.SPOGInvoker;
import invoker.SPOGJobInvoker;
import io.restassured.response.Response;

public class SPOGJobServer {

	private static JsonPreparation jp = new JsonPreparation();
	private SPOGServer spogServer;
	private SPOGJobInvoker spogJobInvoker;
	public static ErrorHandler errorHandle = ErrorHandler.getErrorHandler();
	private ExtentTest test;

	private String Uuid;

	public void setToken(String token) {
		spogJobInvoker.setToken(token);
	}

	public SPOGJobServer(String baseURI, String port) {
		spogJobInvoker = new SPOGJobInvoker(baseURI, port);
		spogServer = new SPOGServer(baseURI, port);
	}

	/**
	 * create job filter and return the filter_id
	 * 
	 * @author Zhaoguo.Ma
	 * @param userID
	 * @param jobStatus
	 * @param policyID
	 * @param resourceID
	 * @param jobType
	 * @param startTimeTSStart
	 * @param startTimeTSEnd
	 * @param endTimeTSStart
	 * @param endTimeTSEnd
	 * @param filterName
	 * @param isDefault
	 * @param test
	 * @return
	 */
	public String createJobFilterWithCheck(String userID, String jobStatus, String policyID, String resourceID,
			String jobType, String startTimeTSStart, String startTimeTSEnd, String endTimeTSStart, String endTimeTSEnd,
			String filterName, String isDefault, ExtentTest test) {
		Map<String, Object> jobFilterInfo = new HashMap<String, Object>();
		jobFilterInfo = jp.composeJobFilter(jobStatus, policyID, resourceID, jobType, startTimeTSStart, startTimeTSEnd,
				endTimeTSStart, endTimeTSEnd, filterName, isDefault);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!jobFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.job_filter.toString(),
					filterName, userID, "none",
					Boolean.valueOf(isDefault), (HashMap<String, Object>)jobFilterInfo);
		}
		test.log(LogStatus.INFO, "Call the Rest API to create job filters");
		Response response = spogServer.createFilters(spogJobInvoker.getToken(), filter_info, "", test);
		
		/*Response response = spogJobInvoker.createJobFilter(userID, jobFilterInfo);*/
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		// TODO: compare the response body
		response.then().body("data.filter_name", equalTo(filterName));

		ArrayList<String> responseJobStatus = response.then().extract().path("data.job_status");
		spogServer.assertFilterItem(jobStatus, responseJobStatus);

		ArrayList<String> responsePolicyID = response.then().extract().path("data.policy_id");
		spogServer.assertFilterItem(policyID, responsePolicyID);

		ArrayList<String> responseResourceID = response.then().extract().path("data.source_id");
		spogServer.assertFilterItem(resourceID, responseResourceID);

		ArrayList<String> responseJobType = response.then().extract().path("data.job_type");
		spogServer.assertFilterItem(jobType, responseJobType);

		String responseStartTimeTSStart = response.then().extract().path("data.start_time_ts.start_ts").toString();
		spogServer.assertResponseItem(startTimeTSStart, responseStartTimeTSStart);

		String responseStartTimeTSEnd = response.then().extract().path("data.start_time_ts.end_ts").toString();
		spogServer.assertResponseItem(startTimeTSEnd, responseStartTimeTSEnd);

		// String responseEndTimeTSStart =
		// response.then().extract().path("data.end_time_ts.start");
		// spogServer.assertResponseItem(endTimeTSStart, responseEndTimeTSStart);
		//
		// String responseEndTimeTSEnd =
		// response.then().extract().path("data.end_time_ts.end");
		// spogServer.assertResponseItem(endTimeTSEnd, responseEndTimeTSEnd);

		boolean responseIsDefault = response.then().extract().path("data.is_default");
		if (isDefault == null || isDefault.equalsIgnoreCase("none") || isDefault == ""
				|| isDefault.equalsIgnoreCase("false")) {
			assertEquals(responseIsDefault, false);
		} else if (isDefault.equalsIgnoreCase("true")) {
			assertEquals(responseIsDefault, true);
		}
		String filterID = response.then().extract().path("data.filter_id");
		return filterID;
	}

	/**
	 * create job filter, removed the endtimets
	 * 
	 * @param userID
	 * @param jobStatus
	 * @param policyID
	 * @param resourceID
	 * @param jobType
	 * @param startTimeTSStart
	 * @param startTimeTSEnd
	 * @param filterName
	 * @param isDefault
	 * @param test
	 * @return
	 */
	public String createJobFilterWithCheck(String userID, String jobStatus, String policyID, String resourceID,
			String jobType, String startTimeTSStart, String startTimeTSEnd, String filterName, String isDefault,
			ExtentTest test) {
		Map<String, Object> jobFilterInfo = new HashMap<String, Object>();
		jobFilterInfo = jp.composeJobFilter(jobStatus, policyID, resourceID, jobType, startTimeTSStart, startTimeTSEnd,
				filterName, isDefault);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!jobFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.job_filter.toString(),
					filterName, userID, "none",
					Boolean.valueOf(isDefault), (HashMap<String, Object>)jobFilterInfo);
		}
		test.log(LogStatus.INFO, "Call the Rest API to create job filters");
		Response response = spogServer.createFilters(spogJobInvoker.getToken(), filter_info, "", test);
		/*Response response = spogJobInvoker.createJobFilter(userID, jobFilterInfo);*/
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		// TODO: compare the response body
		response.then().body("data.filter_name", equalTo(filterName));

		ArrayList<String> responseJobStatus = response.then().extract().path("data.job_status");
		spogServer.assertFilterItem(jobStatus, responseJobStatus);

		ArrayList<String> responsePolicyID = response.then().extract().path("data.policy_id");
		spogServer.assertFilterItem(policyID, responsePolicyID);

		ArrayList<String> responseResourceID = response.then().extract().path("data.source_id");
		spogServer.assertFilterItem(resourceID, responseResourceID);

		ArrayList<String> responseJobType = response.then().extract().path("data.job_type");
		spogServer.assertFilterItem(jobType, responseJobType);

		String responseStartTimeTSStart = response.then().extract().path("data.start_time_ts.start");
		spogServer.assertResponseItem(startTimeTSStart, responseStartTimeTSStart);

		String responseStartTimeTSEnd = response.then().extract().path("data.start_time_ts.end");
		spogServer.assertResponseItem(startTimeTSEnd, responseStartTimeTSEnd);

		boolean responseIsDefault = response.then().extract().path("data.is_default");
		if (isDefault == null || isDefault.equalsIgnoreCase("none") || isDefault == ""
				|| isDefault.equalsIgnoreCase("false")) {
			assertEquals(responseIsDefault, false);
		} else if (isDefault.equalsIgnoreCase("true")) {
			assertEquals(responseIsDefault, true);
		}
		String filterID = response.then().extract().path("data.filter_id");
		return filterID;
	}

	/**
	 * create job filter and return the response
	 * 
	 * @author Zhaoguo.Ma
	 * @param userID
	 * @param jobStatus
	 * @param policyID
	 * @param resourceID
	 * @param jobType
	 * @param startTimeTSStart
	 * @param startTimeTSEnd
	 * @param endTimeTSStart
	 * @param endTimeTSEnd
	 * @param filterName
	 * @param isDefault
	 * @param test
	 * @return
	 */
	public Response createJobFilterWithCheckResponse(String userID, String jobStatus, String policyID,
			String resourceID, String jobType, String startTimeTSStart, String startTimeTSEnd, String endTimeTSStart,
			String endTimeTSEnd, String filterName, String isDefault, ExtentTest test) {
		Map<String, Object> jobFilterInfo = new HashMap<String, Object>();
		jobFilterInfo = jp.composeJobFilter(jobStatus, policyID, resourceID, jobType, startTimeTSStart, startTimeTSEnd,
				endTimeTSStart, endTimeTSEnd, filterName, isDefault);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!jobFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.job_filter.toString(),
					filterName, userID, "none",
					Boolean.valueOf(isDefault), (HashMap<String, Object>)jobFilterInfo);
		}
		test.log(LogStatus.INFO, "Call the Rest API to create job filters");
		Response response = spogServer.createFilters(spogJobInvoker.getToken(), filter_info, "", test);
	/*	Response response = spogJobInvoker.createJobFilter(userID, jobFilterInfo);*/
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		// TODO: compare the response body
		response.then().body("data.filter_name", equalTo(filterName));

		ArrayList<String> responseJobStatus = response.then().extract().path("data.job_status");
		spogServer.assertFilterItem(jobStatus, responseJobStatus);

		ArrayList<String> responsePolicyID = response.then().extract().path("data.policy_id");
		spogServer.assertFilterItem(policyID, responsePolicyID);

		ArrayList<String> responseResourceID = response.then().extract().path("data.source_id");
		spogServer.assertFilterItem(resourceID, responseResourceID);

		ArrayList<String> responseJobType = response.then().extract().path("data.job_type");
		spogServer.assertFilterItem(jobType, responseJobType);

		String responseStartTimeTSStart = response.then().extract().path("data.start_time_ts.start");
		spogServer.assertResponseItem(startTimeTSStart, responseStartTimeTSStart);

		String responseStartTimeTSEnd = response.then().extract().path("data.start_time_ts.end");
		spogServer.assertResponseItem(startTimeTSEnd, responseStartTimeTSEnd);

		// String responseEndTimeTSStart =
		// response.then().extract().path("data.end_time_ts.start");
		// spogServer.assertResponseItem(endTimeTSStart, responseEndTimeTSStart);
		//
		// String responseEndTimeTSEnd =
		// response.then().extract().path("data.end_time_ts.end");
		// spogServer.assertResponseItem(endTimeTSEnd, responseEndTimeTSEnd);

		boolean responseIsDefault = response.then().extract().path("data.is_default");
		if (isDefault == null || isDefault.equalsIgnoreCase("none") || isDefault == ""
				|| isDefault.equalsIgnoreCase("false")) {
			assertEquals(responseIsDefault, false);
		} else if (isDefault.equalsIgnoreCase("true")) {
			assertEquals(responseIsDefault, true);
		}
		// String filterID = response.then().extract().path("data.filter_id");
		return response;
	}

	/**
	 * create job filter, removed the endtimets
	 * 
	 * @author Zhaoguo.Ma
	 * @param userID
	 * @param jobStatus
	 * @param policyID
	 * @param resourceID
	 * @param jobType
	 * @param startTimeTSStart
	 * @param startTimeTSEnd
	 * @param filterName
	 * @param isDefault
	 * @param test
	 * @return
	 */
	public Response createJobFilterWithCheckResponse(String userID, String jobStatus, String policyID,
			String resourceID, String jobType, String startTimeTSStart, String startTimeTSEnd, String filterName,
			String isDefault, ExtentTest test) {
		Map<String, Object> jobFilterInfo = new HashMap<String, Object>();
		jobFilterInfo = jp.composeJobFilter(jobStatus, policyID, resourceID, jobType, startTimeTSStart, startTimeTSEnd,
				filterName, isDefault);
		
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!jobFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.job_filter.toString(),
					filterName, userID, "none",
					Boolean.valueOf(isDefault), (HashMap<String, Object>)jobFilterInfo);
		}
		test.log(LogStatus.INFO, "Call the Rest API to create job filters");
		Response response = spogServer.createFilters(spogJobInvoker.getToken(), filter_info, "", test);
		/*Response response = spogJobInvoker.createJobFilter(userID, jobFilterInfo);*/
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		// TODO: compare the response body
		response.then().body("data.filter_name", equalTo(filterName));

		ArrayList<String> responseJobStatus = response.then().extract().path("data.job_status");
		spogServer.assertFilterItem(jobStatus, responseJobStatus);

		ArrayList<String> responsePolicyID = response.then().extract().path("data.policy_id");
		spogServer.assertFilterItem(policyID, responsePolicyID);

		ArrayList<String> responseResourceID = response.then().extract().path("data.source_id");
		spogServer.assertFilterItem(resourceID, responseResourceID);

		ArrayList<String> responseJobType = response.then().extract().path("data.job_type");
		spogServer.assertFilterItem(jobType, responseJobType);

		String responseStartTimeTSStart = response.then().extract().path("data.start_time_ts.start");
		spogServer.assertResponseItem(startTimeTSStart, responseStartTimeTSStart);

		String responseStartTimeTSEnd = response.then().extract().path("data.start_time_ts.end");
		spogServer.assertResponseItem(startTimeTSEnd, responseStartTimeTSEnd);

		boolean responseIsDefault = response.then().extract().path("data.is_default");
		if (isDefault == null || isDefault.equalsIgnoreCase("none") || isDefault == ""
				|| isDefault.equalsIgnoreCase("false")) {
			assertEquals(responseIsDefault, false);
		} else if (isDefault.equalsIgnoreCase("true")) {
			assertEquals(responseIsDefault, true);
		}
		// String filterID = response.then().extract().path("data.filter_id");
		return response;
	}

	/**
	 * create job filter with error and check the status_code/error_code
	 * 
	 * @author Zhaoguo.Ma
	 * @param userID
	 * @param jobStatus
	 * @param policyID
	 * @param resourceID
	 * @param jobType
	 * @param startTimeTSStart
	 * @param startTimeTSEnd
	 * @param endTimeTSStart
	 * @param endTimeTSEnd
	 * @param filterName
	 * @param isDefault
	 * @param statusCode
	 * @param errorCode
	 * @param test
	 */
	public void createJobFilterWithCodeCheck(String userID, String jobStatus, String policyID, String resourceID,
			String jobType, String startTimeTSStart, String startTimeTSEnd, String endTimeTSStart, String endTimeTSEnd,
			String filterName, String isDefault, int statusCode, String errorCode, ExtentTest test) {
		Map<String, Object> jobFilterInfo = new HashMap<String, Object>();
		jobFilterInfo = jp.composeJobFilter(jobStatus, policyID, resourceID, jobType, startTimeTSStart, startTimeTSEnd,
				endTimeTSStart, endTimeTSEnd, filterName, isDefault);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!jobFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.job_filter.toString(),
					filterName, userID, "none",
					Boolean.valueOf(isDefault), (HashMap<String, Object>)jobFilterInfo);
		}
		test.log(LogStatus.INFO, "Call the Rest API to create job filters");
		Response response = spogServer.createFilters(spogJobInvoker.getToken(), filter_info, "", test);
		/*Response response = spogJobInvoker.createJobFilter(userID, jobFilterInfo);*/
		spogServer.checkResponseStatus(response, statusCode);
		spogServer.checkErrorCode(response, errorCode);
	}

	public void createJobFilterWithCodeCheck(String userID, String jobStatus, String policyID, String resourceID,
			String jobType, String startTimeTSStart, String startTimeTSEnd, String filterName, String isDefault,
			int statusCode, String errorCode, ExtentTest test) {
		Map<String, Object> jobFilterInfo = new HashMap<String, Object>();
		jobFilterInfo = jp.composeJobFilter(jobStatus, policyID, resourceID, jobType, startTimeTSStart, startTimeTSEnd,
				filterName, isDefault);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!jobFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.job_filter.toString(),
					filterName, userID, "none",
					Boolean.valueOf(isDefault), (HashMap<String, Object>)jobFilterInfo);
		}
		test.log(LogStatus.INFO, "Call the Rest API to create job filters");
		Response response = spogServer.createFilters(spogJobInvoker.getToken(), filter_info, "", test);
		/*Response response = spogJobInvoker.createJobFilter(userID, jobFilterInfo);*/
		spogServer.checkResponseStatus(response, statusCode);
		spogServer.checkErrorCode(response, errorCode);
	}

	/**
	 * update job filter and return the response
	 * 
	 * @author Zhaoguo.Ma
	 * @param userID
	 * @param filterID
	 * @param jobStatus
	 * @param policyID
	 * @param resourceID
	 * @param jobType
	 * @param startTimeTSStart
	 * @param startTimeTSEnd
	 * @param endTimeTSStart
	 * @param endTimeTSEnd
	 * @param filterName
	 * @param isDefault
	 * @param test
	 * @return
	 */
	public void updateJobFilterWithCheck(String userID, String filterID, String jobStatus, String policyID,
			String resourceID, String jobType, String startTimeTSStart, String startTimeTSEnd, String endTimeTSStart,
			String endTimeTSEnd, String filterName, String isDefault, ExtentTest test) {
		Map<String, Object> jobFilterInfo = new HashMap<String, Object>();
		jobFilterInfo = jp.composeJobFilter(jobStatus, policyID, resourceID, jobType, startTimeTSStart, startTimeTSEnd,
				endTimeTSStart, endTimeTSEnd, filterName, isDefault);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!jobFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.job_filter.toString(),
					filterName, userID, "none",
					Boolean.valueOf(isDefault), (HashMap<String, Object>)jobFilterInfo);
		}
		Response response = spogServer.updateFilterById(spogJobInvoker.getToken(), filterID, userID, filter_info, "", test);
	/*	Response response = spogJobInvoker.updateJobFilter(userID, filterID, jobFilterInfo);*/
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);

		response.then().body("data.filter_name", equalTo(filterName));

		ArrayList<String> responseJobStatus = response.then().extract().path("data.job_status");
		spogServer.assertFilterItem(jobStatus, responseJobStatus);

		ArrayList<String> responsePolicyID = response.then().extract().path("data.policy_id");
		spogServer.assertFilterItem(policyID, responsePolicyID);

		ArrayList<String> responseResourceID = response.then().extract().path("data.source_id");
		spogServer.assertFilterItem(resourceID, responseResourceID);

		ArrayList<String> responseJobType = response.then().extract().path("data.job_type");
		spogServer.assertFilterItem(jobType, responseJobType);

		String responseStartTimeTSStart = response.then().extract().path("data.start_time_ts.start");
		spogServer.assertResponseItem(startTimeTSStart, responseStartTimeTSStart);

		String responseStartTimeTSEnd = response.then().extract().path("data.start_time_ts.end");
		spogServer.assertResponseItem(startTimeTSEnd, responseStartTimeTSEnd);

		// String responseEndTimeTSStart =
		// response.then().extract().path("data.end_time_ts.start");
		// spogServer.assertResponseItem(endTimeTSStart, responseEndTimeTSStart);
		//
		// String responseEndTimeTSEnd =
		// response.then().extract().path("data.end_time_ts.end");
		// spogServer.assertResponseItem(endTimeTSEnd, responseEndTimeTSEnd);

		boolean responseIsDefault = response.then().extract().path("data.is_default");
		if (isDefault == null || isDefault.equalsIgnoreCase("none") || isDefault == ""
				|| isDefault.equalsIgnoreCase("false")) {
			assertEquals(responseIsDefault, false);
		} else if (isDefault.equalsIgnoreCase("true")) {
			assertEquals(responseIsDefault, true);
		}
	}

	/**
	 * update job filter, removed the endtimets
	 * 
	 * @author Zhaoguo.Ma
	 * @param userID
	 * @param filterID
	 * @param jobStatus
	 * @param policyID
	 * @param resourceID
	 * @param jobType
	 * @param startTimeTSStart
	 * @param startTimeTSEnd
	 * @param filterName
	 * @param isDefault
	 * @param test
	 */
	public Response updateJobFilterWithCheck(String userID, String filterID, String jobStatus, String policyID,
			String resourceID, String jobType, String startTimeTSStart, String startTimeTSEnd, String filterName,
			String isDefault, ExtentTest test) {
		Map<String, Object> jobFilterInfo = new HashMap<String, Object>();
		jobFilterInfo = jp.composeJobFilter(jobStatus, policyID, resourceID, jobType, startTimeTSStart, startTimeTSEnd,
				filterName, isDefault);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!jobFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.job_filter.toString(),
					filterName,  userID, "none",
					Boolean.valueOf(isDefault), (HashMap<String, Object>)jobFilterInfo);
		}
		Response response = spogServer.updateFilterById(spogJobInvoker.getToken(), filterID,userID, filter_info, "", test);
		/*Response response = spogJobInvoker.updateJobFilter(userID, filterID, jobFilterInfo);*/
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);

		response.then().body("data.filter_name", equalTo(filterName));

		ArrayList<String> responseJobStatus = response.then().extract().path("data.job_status");
		spogServer.assertFilterItem(jobStatus, responseJobStatus);

		ArrayList<String> responsePolicyID = response.then().extract().path("data.policy_id");
		spogServer.assertFilterItem(policyID, responsePolicyID);

		ArrayList<String> responseResourceID = response.then().extract().path("data.source_id");
		spogServer.assertFilterItem(resourceID, responseResourceID);

		ArrayList<String> responseJobType = response.then().extract().path("data.job_type");
		spogServer.assertFilterItem(jobType, responseJobType);

		String responseStartTimeTSStart = response.then().extract().path("data.start_time_ts.start");
		spogServer.assertResponseItem(startTimeTSStart, responseStartTimeTSStart);

		String responseStartTimeTSEnd = response.then().extract().path("data.start_time_ts.end");
		spogServer.assertResponseItem(startTimeTSEnd, responseStartTimeTSEnd);

		boolean responseIsDefault = response.then().extract().path("data.is_default");
		if (isDefault == null || isDefault.equalsIgnoreCase("none") || isDefault == ""
				|| isDefault.equalsIgnoreCase("false")) {
			assertEquals(responseIsDefault, false);
		} else if (isDefault.equalsIgnoreCase("true")) {
			assertEquals(responseIsDefault, true);
		}
		return response;
	}

	public Response updateJobFilterForLoggedinUserWithCheck(String filterID, String jobStatus, String policyID,
			String resourceID, String jobType, String startTimeTSStart, String startTimeTSEnd, String filterName,
			String isDefault, ExtentTest test) {
		Map<String, Object> jobFilterInfo = new HashMap<String, Object>();
		jobFilterInfo = jp.composeJobFilter(jobStatus, policyID, resourceID, jobType, startTimeTSStart, startTimeTSEnd,
				filterName, isDefault);
		
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!jobFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.job_filter.toString(),
					filterName,  "none", "none",
					Boolean.valueOf(isDefault), (HashMap<String, Object>)jobFilterInfo);
		}
		Response response = spogServer.updateFilterById(spogJobInvoker.getToken(), filterID,"none", filter_info, "", test);
	/*	Response response = spogJobInvoker.updateJobFilterForLoggedinUser(filterID, jobFilterInfo);*/
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);

		response.then().body("data.filter_name", equalTo(filterName));

		ArrayList<String> responseJobStatus = response.then().extract().path("data.job_status");
		spogServer.assertFilterItem(jobStatus, responseJobStatus);

		ArrayList<String> responsePolicyID = response.then().extract().path("data.policy_id");
		spogServer.assertFilterItem(policyID, responsePolicyID);

		ArrayList<String> responseResourceID = response.then().extract().path("data.source_id");
		spogServer.assertFilterItem(resourceID, responseResourceID);

		ArrayList<String> responseJobType = response.then().extract().path("data.job_type");
		spogServer.assertFilterItem(jobType, responseJobType);

		String responseStartTimeTSStart = response.then().extract().path("data.start_time_ts.start");
		spogServer.assertResponseItem(startTimeTSStart, responseStartTimeTSStart);

		String responseStartTimeTSEnd = response.then().extract().path("data.start_time_ts.end");
		spogServer.assertResponseItem(startTimeTSEnd, responseStartTimeTSEnd);

		boolean responseIsDefault = response.then().extract().path("data.is_default");
		if (isDefault == null || isDefault.equalsIgnoreCase("none") || isDefault == ""
				|| isDefault.equalsIgnoreCase("false")) {
			assertEquals(responseIsDefault, false);
		} else if (isDefault.equalsIgnoreCase("true")) {
			assertEquals(responseIsDefault, true);
		}
		return response;
	}

	/**
	 * update job filter with error and check the status_code/error_code
	 * 
	 * @author Zhaoguo.Ma
	 * @param userID
	 * @param filterID
	 * @param jobStatus
	 * @param policyID
	 * @param resourceID
	 * @param jobType
	 * @param startTimeTSStart
	 * @param startTimeTSEnd
	 * @param endTimeTSStart
	 * @param endTimeTSEnd
	 * @param filterName
	 * @param isDefault
	 * @param statusCode
	 * @param errorCode
	 * @param test
	 */
	public void updateJobFilterWithCodeCheck(String userID, String filterID, String jobStatus, String policyID,
			String resourceID, String jobType, String startTimeTSStart, String startTimeTSEnd, String endTimeTSStart,
			String endTimeTSEnd, String filterName, String isDefault, int statusCode, String errorCode,
			ExtentTest test) {
		Map<String, Object> jobFilterInfo = new HashMap<String, Object>();
		jobFilterInfo = jp.composeJobFilter(jobStatus, policyID, resourceID, jobType, startTimeTSStart, startTimeTSEnd,
				endTimeTSStart, endTimeTSEnd, filterName, isDefault);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!jobFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.job_filter.toString(),
					filterName,  "none", "none",
					Boolean.valueOf(isDefault), (HashMap<String, Object>)jobFilterInfo);
		}
		Response response = spogServer.updateFilterById(spogJobInvoker.getToken(), filterID,"none", filter_info, "", test);
	/*	Response response = spogJobInvoker.updateJobFilter(userID, filterID, jobFilterInfo);*/
		spogServer.checkResponseStatus(response, statusCode);
		spogServer.checkErrorCode(response, errorCode);
	}

	public void updateJobFilterWithCodeCheck(String userID, String filterID, String jobStatus, String policyID,
			String resourceID, String jobType, String startTimeTSStart, String startTimeTSEnd, String filterName,
			String isDefault, int statusCode, String errorCode, ExtentTest test) {
		Map<String, Object> jobFilterInfo = new HashMap<String, Object>();
		jobFilterInfo = jp.composeJobFilter(jobStatus, policyID, resourceID, jobType, startTimeTSStart, startTimeTSEnd,
				filterName, isDefault);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!jobFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.job_filter.toString(),
					filterName,  "none", "none",
					Boolean.valueOf(isDefault), (HashMap<String, Object>)jobFilterInfo);
		}
		Response response = spogServer.updateFilterById(spogJobInvoker.getToken(), filterID,"none", filter_info, "", test);
		/*Response response = spogJobInvoker.updateJobFilter(userID, filterID, jobFilterInfo);*/
		spogServer.checkResponseStatus(response, statusCode);
		spogServer.checkErrorCode(response, errorCode);
	}

	public void updateJobFilterForLoggedinUserWithCodeCheck(String filterID, String jobStatus, String policyID,
			String resourceID, String jobType, String startTimeTSStart, String startTimeTSEnd, String filterName,
			String isDefault, int statusCode, String errorCode, ExtentTest test) {
		Map<String, Object> jobFilterInfo = new HashMap<String, Object>();
		jobFilterInfo = jp.composeJobFilter(jobStatus, policyID, resourceID, jobType, startTimeTSStart, startTimeTSEnd,
				filterName, isDefault);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!jobFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.job_filter.toString(),
					filterName,  "none", "none",
					Boolean.valueOf(isDefault), (HashMap<String, Object>)jobFilterInfo);
		}
		Response response = spogServer.updateFilterById(spogJobInvoker.getToken(), filterID,"none", filter_info, "", test);
		/*Response response = spogJobInvoker.updateJobFilterForLoggedinUser(filterID, jobFilterInfo);*/
		spogServer.checkResponseStatus(response, statusCode);
		spogServer.checkErrorCode(response, errorCode);
	}

	/**
	 * get job filter by ID and return response
	 * 
	 * @author Zhaoguo.Ma
	 * @param userID
	 * @param filterID
	 * @param test
	 * @return
	 */
	public Response getJobFilterByID(String userID, String filterID, String token, ExtentTest test) {
		/*Response response = spogJobInvoker.getJobFilterByID(userID, filterID, token);*/
		Response response = spogServer.getFiltersById(token, filterID, filterType.job_filter.toString(), userID, "none", test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		return response;
	}

	/**
	 * get job filter by ID and return response
	 * 
	 * @author Zhaoguo.Ma
	 * @param userID
	 * @param filterID
	 * @param test
	 * @return
	 */
	public Response getJobFilterByID(String userID, String filterID, ExtentTest test) {
	/*	Response response = spogJobInvoker.getJobFilterByID(userID, filterID);*/
		Response response = spogServer.getFiltersById(spogJobInvoker.getToken(), filterID, filterType.job_filter.toString(), userID, "none", test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		return response;
	}

	/**
	 * get job filter by ID for logged in user and return response
	 * 
	 * @author Zhaoguo.Ma
	 * @param filterID
	 * @param test
	 * @return
	 */
	public Response getJobFilterForLoggedinUserByID(String filterID, ExtentTest test) {
		/*Response response = spogJobInvoker.getJobFilterForLoggedinUserByID(filterID);*/
		Response response = spogServer.getFiltersById(spogJobInvoker.getToken(), filterID, filterType.job_filter.toString(),  "none", "none", test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		return response;
	}

	/**
	 * get job filter by ID and return response
	 * 
	 * @author Kiran.Sripada
	 * @param userID
	 * @param filterID
	 * @param token
	 * @param hash
	 *            of expected response
	 * @param expected
	 *            status code
	 * @param expected
	 *            error message
	 * @param test
	 * @return
	 */
	public void getspecifiedJobFilterByUserIDwithCheck(String userID, String filterID, String token,
			HashMap<String, Object> expected_response, int expectedstatuscode, SpogMessageCode ExpectedErrorMessage,
			ExtentTest test) {
		HashMap<String, Object> actual_response = new HashMap<String, Object>();
	/*	Response response = spogJobInvoker.getJobFilterByID(userID, filterID, token);*/
		Response response = spogServer.getFiltersById(token, filterID,filterType.job_filter.toString(), userID, "none", test);
		spogServer.checkResponseStatus(response, expectedstatuscode);
		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			actual_response = response.then().extract().path("data");
			checkjobFilters(actual_response, expected_response, test);
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			if (code.contains("00100201")) {
				message = message.replace("{0}", filterID);
			}
			if (code.contains("00A00202")) {
				message = message.replace("{0}", filterID);
				message = message.replace("{1}", userID);
			}
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.INFO, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		}

	}

	/**
	 * get job filters for user and return response
	 * 
	 * @author Zhaoguo.Ma
	 * @param userID
	 * @param test
	 * @return
	 */
	public Response getJobFiltersForUser(String userID, String token, ExtentTest test) {
		/*Response response = spogJobInvoker.getJobFiltersForUser(userID, token);*/
		Response response = spogServer.getFilters(token, userID, filterType.job_filter.toString(), "", test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		return response;
	}

	/**
	 * get job filters for user and return response
	 * 
	 * @author Kiran.Sripada
	 * @param userID
	 * @param test
	 * @return
	 */
	public void getJobFiltersForUserwithCheck(String userID, String token,
			ArrayList<HashMap<String, Object>> expected_response, int expectedstatuscode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		ArrayList<HashMap<String, Object>> actual_response = new ArrayList<>();
		HashMap<String, Object> data_response = new HashMap<>();
		Response response = spogServer.getFilters(token, userID, filterType.job_filter.toString(), "", test);
		/*Response response = spogJobInvoker.getJobFiltersForUser(userID, token);*/
		spogServer.checkResponseStatus(response, expectedstatuscode);
		test.log(LogStatus.PASS, "The response status matched and the status code is " + expectedstatuscode);
		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			actual_response = response.then().extract().path("data");
			/*
			 * Collections.sort(actual_response, new Comparator<HashMap<String,Object>>() {
			 * 
			 * @Override public int compare(HashMap<String,Object> o1,
			 * HashMap<String,Object> o2) { // TODO Auto-generated method stub long
			 * create_ts = (long) o1.get("create_ts"); long create_ts1 = (long)
			 * o2.get("create_ts"); if (create_ts > create_ts1) return 1; if (create_ts <
			 * create_ts1) return -1; else return 0; } });
			 */
			for (int i = 0; i < actual_response.size(); i++) {
				data_response = actual_response.get(i);

				for (int j = 0; j < expected_response.size(); j++) {
					String act = (String) actual_response.get(i).get("filter_id");
					String exp = (String) expected_response.get(j).get("filter_id");

					if (act.equals(exp)) {
						checkjobFilters(data_response, expected_response.get(j), test);
					} else {
						test.log(LogStatus.INFO, "The actual job filter not matched with the expected ");
					}
				}
			}
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
	 * get job filters for user and return response
	 * 
	 * @author Kiran.Sripada
	 * @param userID
	 * @param test
	 * @return
	 */
	public void getJobFiltersForUserwithCheck_isdefault(String userID, String token, String additionalURL,
			ArrayList<HashMap<String, Object>> expected_response, int expectedstatuscode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		ArrayList<HashMap<String, Object>> actual_response = new ArrayList<>();
		HashMap<String, Object> data_response = new HashMap<>();
		Response response = spogServer.getFilters(token, userID, filterType.job_filter.toString(),additionalURL, test);
	/*	Response response = spogJobInvoker.getJobFiltersForUser(userID, token, additionalURL);*/
		spogServer.checkResponseStatus(response, expectedstatuscode);
		test.log(LogStatus.PASS, "The response status matched and the status code is " + expectedstatuscode);
		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			actual_response = response.then().extract().path("data");
			/*
			 * Collections.sort(actual_response, new Comparator<HashMap<String,Object>>() {
			 * 
			 * @Override public int compare(HashMap<String,Object> o1,
			 * HashMap<String,Object> o2) { // TODO Auto-generated method stub long
			 * create_ts = (long) o1.get("create_ts"); long create_ts1 = (long)
			 * o2.get("create_ts"); if (create_ts > create_ts1) return 1; if (create_ts <
			 * create_ts1) return -1; else return 0; } });
			 */
			String[] expected_data = additionalURL.split("\\&");
			for (int i = 0; i < actual_response.size(); i++) {
				data_response = actual_response.get(i);
				if (additionalURL.contains("is_default") && (additionalURL.contains("true"))
						&& (!additionalURL.contains("filter_name"))) {
					boolean responseIsDefault = (boolean) data_response.get("is_default");
					if (!responseIsDefault) {
						continue;
					} else {
						checkjobFilters(data_response, expected_response.get(expected_response.size() - 1), test);
						break;
					}
				} else if (additionalURL.contains("is_default") && (additionalURL.contains("false"))
						&& (!additionalURL.contains("filter_name"))) {
					boolean responseIsDefault = (boolean) data_response.get("is_default");
					if (responseIsDefault) {
						test.log(LogStatus.FAIL, "Still there are entries with is_default=true");
						assertTrue("Still there are entries with is_default=true", false);
					} else {
						expected_response=spogServer.sortArrayListbyString(expected_response, "filter_name");
						/*data_response=spogServer.sortArrayListbyString(data_response, "filter_name");*/
						checkjobFilters(data_response, expected_response.get(i), test);
						continue;
					}
				} else if (!additionalURL.contains("is_default") && (additionalURL.contains("filter_name"))) {
					String[] filtername = expected_data[0].split("=");

					String responseIsDefault = data_response.get("filter_name").toString();
					if (responseIsDefault.equals(filtername[1])) {

						for (int j = 0; j < expected_response.size(); j++) {
							if ((expected_response.get(j).get("filter_name").toString()).equals(filtername[1])) {
								checkjobFilters(data_response, expected_response.get(j), test);
								break;
							} else {
								continue;
							}
						}
					} else {
						continue;
					}
				} else if (additionalURL.contains("is_default") && (additionalURL.contains("filter_name"))) {
					String[] filtername = expected_data[1].split("=");
					String responseIsDefault = data_response.get("filter_name").toString();
					if (responseIsDefault.equals(filtername[1])) {
						for (int j = 0; j < expected_response.size(); j++) {
							if ((expected_response.get(j).get("filter_name").toString()).equals(filtername[1])) {
								checkjobFilters(data_response, expected_response.get(j), test);
								break;
							} else {
								continue;
							}
						}
					} else {
						continue;
					}
				}

			}
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
	 * Delete job filter by ID
	 * 
	 * @author Kiran.Sripada
	 * @param userID
	 * @param filterID
	 * @param token
	 * @param expectedstatuscode
	 * @param expectederrormessage
	 * @param test
	 */
	public void deleteJobFilterByFilterID(String userID, String filterID, String token, int expectedstatuscode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		/*Response response = spogJobInvoker.deleteJobFilterByFilterID(userID, filterID, token);*/
		Response response = spogServer.deleteFiltersByID(token, filterID, userID, test);
		spogServer.checkResponseStatus(response, expectedstatuscode);
		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.PASS, "The job filter is successfully deleted");
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			if (code.contains("00A00202")) {
				message = message.replace("{0}", filterID);
				message = message.replace("{1}", userID);
			}
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		}
	}

	/**
	 * Delete job filter for logged in user by filter Id
	 * 
	 * @author Kiran.Sripada
	 * @param userID
	 * @param filterID
	 * @param token
	 * @param expectedstatuscode
	 * @param expectederrormessage
	 * @param test
	 */
	public void deleteJobFilterforloggedInUser(String filterID, String token, int expectedstatuscode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		/*Response response = spogJobInvoker.deleteJobFilterforloggedInuser(filterID, token);*/
		Response response = spogServer.deleteFiltersByID(token, filterID, "none", test);
		spogServer.checkResponseStatus(response, expectedstatuscode);
		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.PASS, "The job filter is successfully deleted");
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			if (code.contains("00A00202")) {
				Response response1 = spogServer.getLoggedInUser(token, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
				String userID = response1.then().extract().path("data.user_id");
				message = message.replace("{0}", filterID);
				message = message.replace("{1}", userID);
			}
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		}
	}

	/**
	 * get job filters for logged in user
	 * 
	 * @author Kiran.Sripada
	 * @param array
	 *            list of expectedresponse
	 * @param expectedstatuscode
	 * @param expectederrormessage
	 * @param extenttest
	 * @return void
	 */
	public void getJobFiltersForLoggedInUser(ArrayList<HashMap<String, Object>> expected_response,
			int expectedstatuscode, SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		ArrayList<HashMap<String, Object>> actual_response = new ArrayList<>();
		HashMap<String, Object> data_response = new HashMap<>();
		int actual_size = expected_response.size();
	/*	Response response = spogJobInvoker.getJobFiltersForLoggedInUser();*/
		Response response = spogServer.getFilters(spogJobInvoker.getToken(), "none", filterType.job_filter.toString(), "", test);
		test.log(LogStatus.INFO, "Check the response status");
		spogServer.checkResponseStatus(response, expectedstatuscode);
		test.log(LogStatus.PASS, "The response status matched and the status code is " + expectedstatuscode);
		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			actual_response = response.then().extract().path("data");

			for (int i = 0; i < actual_response.size(); i++) {
				data_response = actual_response.get(i);

				for (int j = 0; j < expected_response.size(); j++) {
					String act = (String) actual_response.get(i).get("filter_id");
					String exp = (String) expected_response.get(j).get("filter_id");

					if (act.equals(exp)) {
						checkjobFilters(data_response, expected_response.get(j), test);
					} else {
						test.log(LogStatus.INFO, "The actual job filter not matched with the expected ");
					}
				}
			}
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
	 * get job filters for logged in user by applying filter on is_default
	 * 
	 * @author Kiran.Sripada
	 * @param additionalURL
	 *            ?is_default=true or ?is_default=false
	 * @param array
	 *            list of expectedresponse
	 * @param expectedstatuscode
	 * @param expectederrormessage
	 * @param extenttest
	 * @return void
	 */
	public void getJobFiltersForLoggedInUser_isdefault(String additionalURL,
			ArrayList<HashMap<String, Object>> expected_response, int expectedstatuscode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		ArrayList<HashMap<String, Object>> actual_response = new ArrayList<>();
		HashMap<String, Object> data_response = new HashMap<>();
		int actual_size = expected_response.size();
		/*Response response = spogJobInvoker.getJobFiltersForLoggedInUser(additionalURL);*/
		Response response = spogServer.getFilters(spogJobInvoker.getToken(), "none", filterType.job_filter.toString(), additionalURL, test);

		test.log(LogStatus.INFO, "Check the response status");
		spogServer.checkResponseStatus(response, expectedstatuscode);
		test.log(LogStatus.PASS, "The response status matched and the status code is " + expectedstatuscode);
		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			actual_response = response.then().extract().path("data");
			/*
			 * Collections.sort(actual_response, new Comparator<HashMap<String,Object>>() {
			 * 
			 * @Override public int compare(HashMap<String,Object> o1,
			 * HashMap<String,Object> o2) { // TODO Auto-generated method stub long
			 * create_ts = (long) o1.get("create_ts"); long create_ts1 = (long)
			 * o2.get("create_ts"); if (create_ts > create_ts1) return 1; if (create_ts <
			 * create_ts1) return -1; else return 0; } });
			 */
			String[] expected_data = additionalURL.split("\\&");
			for (int i = 0; i < actual_response.size(); i++) {
				data_response = actual_response.get(i);
				if (additionalURL.contains("is_default") && (additionalURL.contains("true"))
						&& (!additionalURL.contains("filter_name"))) {
					boolean responseIsDefault = (boolean) data_response.get("is_default");
					if (!responseIsDefault) {
						continue;
					} else {
						checkjobFilters(data_response, expected_response.get(actual_size - 1), test);
						break;
					}
				} else if (additionalURL.contains("is_default") && (additionalURL.contains("false"))
						&& (!additionalURL.contains("filter_name"))) {
					boolean responseIsDefault = (boolean) data_response.get("is_default");
					if (responseIsDefault) {
						test.log(LogStatus.FAIL, "Still there are entries with is_default=true");
						assertTrue("Still there are entries with is_default=true", false);
					} else {
						checkjobFilters(data_response, expected_response.get(i), test);
						continue;
					}
				} else if (!additionalURL.contains("is_default") && (additionalURL.contains("filter_name"))) {
					String[] filtername = expected_data[1].split("=");

					String responseIsDefault = data_response.get("filter_name").toString();
					if (responseIsDefault.equals(filtername[1])) {

						for (int j = 0; j < expected_response.size(); j++) {
							if ((expected_response.get(j).get("filter_name").toString()).equals(filtername[1])) {
								checkjobFilters(data_response, expected_response.get(j), test);
								break;
							} else {
								continue;
							}
						}
						// checkjobFilters(data_response,expected_response.get(i),test);
					} else {
						continue;
					}
				} else if (additionalURL.contains("is_default") && (additionalURL.contains("filter_name"))) {
					String[] filtername = expected_data[1].split("=");
					String responseIsDefault = data_response.get("filter_name").toString();
					if (responseIsDefault.equals(filtername[1])) {
						for (int j = 0; j < expected_response.size(); j++) {
							if ((expected_response.get(j).get("filter_name").toString()).equals(filtername[1])) {
								checkjobFilters(data_response, expected_response.get(j), test);
								break;
							} else {
								continue;
							}
						}
					} else {
						continue;
					}
				}

			}
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
	 * get specified job filter for logged in user
	 * 
	 * @author Kiran.Sripada
	 * @param filter_Id
	 * @param token
	 * @param HashMap
	 *            of expectedresponse
	 * @param expectedstatuscode
	 * @param expectederrormessage
	 * @param extenttest
	 * @return void
	 */
	public void getspecifiedJobFilterForLoggedInUser(String filter_Id, String token,
			HashMap<String, Object> expected_response, int expectedstatuscode, SpogMessageCode ExpectedErrorMessage,
			ExtentTest test) {
		HashMap<String, Object> actual_response = new HashMap<>();
		int actual_size = expected_response.size();
	/*	Response response = spogJobInvoker.getspecifiedJobFilterForLoggedInUser(filter_Id, token);*/
		Response response = spogServer.getFiltersById(token, filter_Id, filterType.job_filter.toString(), "none", "none", test);

		test.log(LogStatus.INFO, "Check the response status");
		spogServer.checkResponseStatus(response, expectedstatuscode);
		test.log(LogStatus.PASS, "The response status matched and the status code is " + expectedstatuscode);
		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			// place holder for validating the response
			actual_response = response.then().extract().path("data");
			checkjobFilters(actual_response, expected_response, test);
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			if (code.contains("00A00202")) {
				Response response1 = spogServer.getLoggedInUser(token, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
				String response_user_id = response1.then().extract().path("data.user_id");
				message = message.replace("{0}", filter_Id);
				message = message.replace("{1}", response_user_id);

			}
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.INFO, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		}
	}

	public void checkjobFilters(HashMap<String, Object> actual_response, HashMap<String, Object> expected_response,
			ExtentTest test) {

		test.log(LogStatus.INFO, "Compare the filter id");

		spogServer.assertResponseItem(actual_response.get("filter_id").toString(),
				expected_response.get("filter_id").toString(), test);

		test.log(LogStatus.INFO, "Compare the filter name");
		spogServer.assertResponseItem(actual_response.get("filter_name").toString(),
				expected_response.get("filter_name").toString(), test);

		test.log(LogStatus.INFO, "Compare the user id");
		spogServer.assertResponseItem(actual_response.get("user_id").toString(),
				expected_response.get("user_id").toString(), test);

		test.log(LogStatus.INFO, "Compare the organization id");
		spogServer.assertResponseItem(actual_response.get("organization_id").toString(),
				expected_response.get("organization_id").toString(), test);

		test.log(LogStatus.INFO, "Compare the job status");
		if (expected_response.get("job_status") == null || expected_response.get("job_status") == "none") {
			test.log(LogStatus.PASS, "filter not applied on job status");
			assertNull(null, " filter not applied on job status");
		} else {
			ArrayList<String> job_status = (ArrayList<String>) actual_response.get("job_status");
			String.join(",", job_status);
			spogServer.assertResponseItem(String.join(",", job_status), expected_response.get("job_status").toString(),
					test);
		}

		test.log(LogStatus.INFO, "Compare the job type");
		if (expected_response.get("job_type") == null || expected_response.get("job_type") == "none") {
			test.log(LogStatus.PASS, "filter not applied on job type");
			assertNull(null, " filter not applied on job type");
		} else {
			ArrayList<String> job_type = (ArrayList<String>) actual_response.get("job_type");
			String.join(",", job_type);
			spogServer.assertResponseItem(String.join(",", job_type), expected_response.get("job_type").toString(),
					test);
		}

		test.log(LogStatus.INFO, "Compare the policy id");
		if (expected_response.get("policy_id") == null || expected_response.get("policy_id") == "none") {
			test.log(LogStatus.PASS, "filter not applied on policy id");
			assertNull(null, " filter not applied on policy id");
		} else {
			ArrayList<String> policy_id = (ArrayList<String>) actual_response.get("policy_id");
			String.join(",", policy_id);
			spogServer.assertResponseItem(String.join(",", policy_id), expected_response.get("policy_id").toString(),
					test);
		}

		test.log(LogStatus.INFO, "Compare the resource id");
		if (expected_response.get("source_id") == null || expected_response.get("source_id") == "none") {
			test.log(LogStatus.PASS, "filter not applied on resource id");
			assertNull(null, " filter not applied on resource id");
		} else {
			ArrayList<String> resource_id = (ArrayList<String>) actual_response.get("source_id");
			String.join(",", resource_id);
			spogServer.assertResponseItem(String.join(",", resource_id), expected_response.get("source_id").toString(),
					test);
		}

		HashMap<String, String> responseMessageData = (HashMap<String, String>) actual_response.get("start_time_ts");

		HashMap<String, String> expectedtimerange = (HashMap<String, String>) expected_response.get("start_time_ts");
		test.log(LogStatus.INFO, "Compare the time range");
		if (expectedtimerange.get("start") == null || expectedtimerange.get("end") == null) {
			test.log(LogStatus.PASS, "filter not applied on time range");
			assertNull(null, " filter not applied on time range");
		} else {

			String log_ts_start = null;
			String log_ts_end = null;
			log_ts_start = responseMessageData.get("start");
			log_ts_end = responseMessageData.get("end");
			/*
			 * if (!log_ts_start.equalsIgnoreCase(expectedtimerange.get("start"))) {
			 * test.log(LogStatus.FAIL, "compare start time " +
			 * expectedtimerange.get("start") + "failed"); assertTrue("compare start time "
			 * + expectedtimerange.get("start") + "failed", false); } if
			 * (!log_ts_end.equalsIgnoreCase(expectedtimerange.get("end"))) {
			 * test.log(LogStatus.FAIL, "compare end time " + expectedtimerange.get("end") +
			 * "failed"); assertTrue("compare end time " + expectedtimerange.get("end") +
			 * "failed", false); }
			 */
		}

		test.log(LogStatus.INFO, "Compare the count");
		// spogServer.assertResponseItem(actual_response.get("count"),expected_response.get("count"),test);

	}

	public HashMap<String, Object> composeExpectedJobFilter(String filter_id, String filter_name, String user_id,
			String organization_id, String job_status, String job_type, String policy_id, String resource_id,
			String startTimeTSStart, String startTimeTSEnd, String startTimeType, String sourceName, String isDefault,
			int count) {
		HashMap<String, Object> expected_response = new HashMap<>();
		expected_response.put("filter_id", filter_id);
		expected_response.put("filter_name", filter_name);
		expected_response.put("user_id", user_id);
		expected_response.put("organization_id", organization_id);
		expected_response.put("job_status", job_status);
		expected_response.put("job_type", job_type);
		expected_response.put("policy_id", policy_id);
		expected_response.put("source_id", resource_id);

		HashMap<String, String> startTimeTSInfo = new HashMap<String, String>();
		if (!"none".equalsIgnoreCase(startTimeTSStart)) {
			startTimeTSInfo.put("start", startTimeTSStart);
		}
		if (!"none".equalsIgnoreCase(startTimeTSEnd)) {
			startTimeTSInfo.put("end", startTimeTSEnd);
		}
		if (startTimeTSInfo.size() != 0) {
			expected_response.put("start_time_ts", startTimeTSInfo);
		}

		// HashMap<String, String> endTimeTSInfo = new HashMap<String, String>();
		// if (!"none".equalsIgnoreCase(endTimeTSStart)) {
		// endTimeTSInfo.put("start", endTimeTSStart);
		// }
		// if (!"none".equalsIgnoreCase(endTimeTSEnd)) {
		// endTimeTSInfo.put("end", endTimeTSEnd);
		// }
		// if (startTimeTSInfo.size()!=0) {
		// expected_response.put("end_time_ts", endTimeTSInfo);
		// }
		expected_response.put("is_default", Boolean.valueOf(isDefault));
		expected_response.put("count", count);
		return expected_response;
	}

	/**
	 * Compose the job columns
	 * 
	 * @author Kiran.Sripada
	 * @param column
	 *            id
	 * @param sort
	 *            -- true/false
	 * @param filter
	 *            -- true/false
	 * @param visible
	 *            -- true/false
	 * @param orderid
	 *            -- An integer value
	 */
	public HashMap<String, Object> composejob_Column(String columnId, String sort, String filter, String visible,
			String orderId) {
		// TODO Auto-generated method stub
		HashMap<String, Object> temp = new HashMap<>();
		temp.put("column_id", columnId);
		if (!sort.equals("none")) {

			temp.put("sort", sort);

		}
		if (!filter.equals("none")) {

			temp.put("filter", filter);
		}

		if (!visible.equals("none")) {
			temp.put("visible", visible);
		}

		temp.put("order_id", orderId);

		return temp;

	}

	/**
	 * Create job columns for specified user
	 * 
	 * @author Kiran.Sripada
	 * @param userid
	 * @param token
	 * @param List
	 *            of columns
	 * @param ExtentTest
	 */
	public Response createJobColumnsForSpecifiedUser(String user_id, String validToken,
			ArrayList<HashMap<String, Object>> expected_columns, ExtentTest test) {

		Map<String, ArrayList<HashMap<String, Object>>> userInfo = jp.jobColumnInfo(expected_columns);
		Response response = spogJobInvoker.createJobColumnsForSpecifiedUser(user_id, validToken, userInfo, test);

		return response;

	}

	/**
	 * Create job columns for logged in user
	 * 
	 * @author N.Ramya
	 * @param token
	 * @param columns
	 *            -- ArrayList of HashMap, that conatins all the columns
	 * @param test
	 */

	public Response createJobColumnsForLoggedInUser(String validToken,
			ArrayList<HashMap<String, Object>> expected_columns, ExtentTest test) {
		// TODO Auto-generated method stub
		Map<String, ArrayList<HashMap<String, Object>>> userInfo = jp.jobColumnInfo(expected_columns);
		Response response = spogJobInvoker.createJobColumnsForLoggedInUser(validToken, userInfo, test);

		return response;
	}

	/**
	 * get job columns for logged in user
	 * 
	 * @author N.Ramya
	 * @param token
	 * @param test
	 */
	public Response getJobColumnsForLoggedInUser(String validToken, ExtentTest test) {
		// TODO Auto-generated method stub
		Response response = spogJobInvoker.getJobsColumnsForLoggedInUser(validToken);

		errorHandle.printDebugMessageInDebugFile("Response status is " + response.getStatusCode());

		return response;

	}

	/**
	 * get job columns for specified user
	 * 
	 * @author N.Ramya
	 * @param user_id
	 * @param token
	 * @param test
	 */
	public Response getJobColumnsForSpecifiedUser(String user_id, String validToken, ExtentTest test) {
		// TODO Auto-generated method stub
		Response response = spogJobInvoker.getJobsColumnsForSpecifiedUser(validToken, user_id);

		errorHandle.printDebugMessageInDebugFile("Response of get job columns for specified user is " + response);

		return response;
	}

	/**
	 * update job columns for logged in user
	 * 
	 * @author N.Ramya
	 * @param token
	 * @param columns
	 *            -- ArrayList of HashMap, that conatins all the columns
	 * @param test
	 */

	public Response updateJobColumnsForLoggedInUser(String validToken,
			ArrayList<HashMap<String, Object>> expected_columns, ExtentTest test) {

		Map<String, ArrayList<HashMap<String, Object>>> userInfo = jp.jobColumnInfo(expected_columns);

		Response response = spogJobInvoker.updateJobColumnsForLoggedInUser(validToken, userInfo, test);

		errorHandle
				.printDebugMessageInDebugFile("response for updating job columns for logged in user  is " + response);

		return response;
	}

	/**
	 * update the job columns for specified user
	 * 
	 * @author N.Ramya
	 * @param user_id
	 * @param validToken
	 * @param expected_columns
	 *            -- ArrayList of HashMap, that conatins all the columns
	 * @param Test
	 * @return response
	 */

	public Response updateJobColumnsForSpecifiedUser(String user_id, String validToken,
			ArrayList<HashMap<String, Object>> expected_columns, ExtentTest test) {

		Map<String, ArrayList<HashMap<String, Object>>> userInfo = jp.jobColumnInfo(expected_columns);

		Response response = spogJobInvoker.updateJobColumnsForSpecifiedUser(user_id, validToken, userInfo, test);

		errorHandle
				.printDebugMessageInDebugFile("response for updating job columns for Specified user  is " + response);

		return response;
	}

	/**
	 * Compare the job columns
	 * 
	 * @author Ramya.Nagepalli
	 * @param response
	 *            -- Actual response
	 * @param expected
	 *            columns
	 * @param default
	 *            columns
	 * @param expected
	 *            status code
	 * @param expectederrormessage
	 * @param test
	 */

	public void compareJobColumnsContent(Response response, ArrayList<HashMap<String, Object>> expectedColumnsContents,
			ArrayList<HashMap<String, Object>> defaultColumnsContents, int expectedstatuscode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		// TODO Auto-generated method stub
		ArrayList<HashMap<String, Object>> actualcolumnsHeadContent = new ArrayList<HashMap<String, Object>>();

		ArrayList<HashMap<String, Object>> remain_actualcolumnsHead = new ArrayList<HashMap<String, Object>>();

		spogServer.checkResponseStatus(response, expectedstatuscode);
		if (expectedstatuscode == SpogConstants.SUCCESS_POST
				|| expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			actualcolumnsHeadContent = response.then().extract().path("data");

			String exp_order_id = expectedColumnsContents.get(0).get("order_id").toString();

			if (!(exp_order_id.equals(""))) {
				if (!(expectedColumnsContents == defaultColumnsContents)
						&& !(expectedColumnsContents.size() > actualcolumnsHeadContent.size())) {

					// Since the actual reponse order is jumbled, sort them by order_id

					Collections.sort(actualcolumnsHeadContent, new Comparator<HashMap<String, Object>>() {
						@Override
						public int compare(HashMap<String, Object> o1, HashMap<String, Object> o2) {
							// TODO Auto-generated method stub
							int create_ts = (int) o1.get("order_id");
							int create_ts1 = (int) o2.get("order_id");
							if (create_ts > create_ts1)
								return 1;
							if (create_ts < create_ts1)
								return -1;
							else
								return 0;

						}
					});

					Collections.sort(expectedColumnsContents, new Comparator<HashMap<String, Object>>() {
						@Override
						public int compare(HashMap<String, Object> o1, HashMap<String, Object> o2) {
							// TODO Auto-generated method stub
							String create_ts = (String) o1.get("order_id");
							String create_ts1 = (String) o2.get("order_id");
							if (create_ts.compareTo(create_ts1) > 0)
								return 1;
							else if (create_ts.compareTo(create_ts1) == 0) {

								return 0;
							} else
								return -1;

						}
					});
				}
				if (expectedColumnsContents.size() == actualcolumnsHeadContent.size()) {

					for (int i = 0; i < actualcolumnsHeadContent.size(); i++) {

						for (int j = 0; j < defaultColumnsContents.size(); j++) {

							for (int k = 0; k < expectedColumnsContents.size(); k++) {

								if (actualcolumnsHeadContent.get(i).get("column_id").toString()
										.equals(defaultColumnsContents.get(j).get("column_id").toString())
										&& (actualcolumnsHeadContent.get(i).get("column_id").toString()
												.equals(expectedColumnsContents.get(k).get("column_id").toString()))) {

									checkjobcolumns(actualcolumnsHeadContent.get(i), expectedColumnsContents.get(k),
											defaultColumnsContents.get(j), test);

									break;

								}
							}
						}
					}

				} else if (expectedColumnsContents.size() > actualcolumnsHeadContent.size()) {

					for (int i = 0; i < actualcolumnsHeadContent.size(); i++) {

						for (int j = 0; j < defaultColumnsContents.size(); j++) {

							if ((actualcolumnsHeadContent.get(i).get("column_id")
									.equals(defaultColumnsContents.get(j).get("column_id")))
									&& (actualcolumnsHeadContent.get(i).get("column_id")
											.equals(expectedColumnsContents.get(j).get("column_id")))) {

								checkjobcolumns(actualcolumnsHeadContent.get(i), expectedColumnsContents.get(j),
										defaultColumnsContents.get(j), test);

							} else if ((actualcolumnsHeadContent.get(i).get("column_id")
									.equals(defaultColumnsContents.get(j).get("column_id")))) {
								checkjobcolumns(actualcolumnsHeadContent.get(i), defaultColumnsContents.get(j),
										defaultColumnsContents.get(j), test);
								break;
							}
						}
					}

				}

				else {
					for (int i = 0; i < actualcolumnsHeadContent.size(); i++) {

						for (int j = 0; j < expectedColumnsContents.size(); j++) {
							if (!(actualcolumnsHeadContent.get(i).get("column_id").toString()
									.equals(expectedColumnsContents.get(j).get("column_id").toString()))) {

								remain_actualcolumnsHead.add(actualcolumnsHeadContent.get(i));
								break;
							}

							for (int k = 0; k < defaultColumnsContents.size(); k++) {

								if ((actualcolumnsHeadContent.get(i).get("column_id")
										.equals(expectedColumnsContents.get(j).get("column_id")))
										&& actualcolumnsHeadContent.get(i).get("column_id")
												.equals(defaultColumnsContents.get(k).get("column_id"))) {

									checkjobcolumns(actualcolumnsHeadContent.get(i), expectedColumnsContents.get(j),
											defaultColumnsContents.get(k), test);

								}

							}
						}
					}
					for (int i = 0; i < actualcolumnsHeadContent.size(); i++) {

						for (int j = 0; j < remain_actualcolumnsHead.size(); j++) {

							for (int k = 0; k < defaultColumnsContents.size(); k++) {

								if ((actualcolumnsHeadContent.get(i).get("column_id")
										.equals(remain_actualcolumnsHead.get(j).get("column_id")))
										&& actualcolumnsHeadContent.get(i).get("column_id")
												.equals(defaultColumnsContents.get(k).get("column_id"))) {

									checkjobcolumns(actualcolumnsHeadContent.get(i), remain_actualcolumnsHead.get(j),
											defaultColumnsContents.get(k), test);

								}
							}
						}
					}
				}
			} else {
				test.log(LogStatus.PASS, "The Order Id is null ");
			}
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();

			if (code.contains("00D00003") || code.contains("00D00004")) {

				String Column_id = expectedColumnsContents.get(0).get("column_id").toString();

				message = message.replace("{0}", Column_id);

			}
			if (code.contains("00D00005")) {

				String order_id = expectedColumnsContents.get(0).get("order_id").toString();

				message = message.replace("{0}", order_id);

			}
			if (code.contains("00D00006")) {

				message = message.replace("{0}", "9");

			}

			spogServer.checkErrorCode(response, code);

			test.log(LogStatus.PASS, "The error code matched with the expected " + code);

			spogServer.checkErrorMessage(response, message);

			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		}

	}

	/**
	 * Delete job columns for specified user ID
	 * 
	 * @author Kiran.Sripada
	 * @param userID
	 * @param token
	 * @param expectedstatuscode
	 * @param expectederrormessage
	 * @param test
	 */
	public void deleteJobcolumnsforspecifeduser(String userID, String token, int expectedstatuscode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		Response response = spogJobInvoker.deleteJobColumnsForSpecifiedUser(userID, token);
		spogServer.checkResponseStatus(response, expectedstatuscode);
		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.PASS, "The job filter is successfully deleted");
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			/*
			 * if(code.contains("00A00202")){ message = message.replace("{0}", filterID);
			 * message= message.replace("{1}", userID); }
			 */
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		}
	}

	/**
	 * Delete job columns for logged in user
	 * 
	 * @author Kiran.Sripada
	 * @param token
	 * @param expectedstatuscode
	 * @param expectederrormessage
	 * @param test
	 */
	public void deleteJobColumnsForLoggedInUser(String token, int expectedstatuscode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		Response response = spogJobInvoker.deleteJobColumnsForLoggedInUser(token);
		spogServer.checkResponseStatus(response, expectedstatuscode);
		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.PASS, "The job filter is successfully deleted");
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			/*
			 * if(code.contains("00A00202")){ message = message.replace("{0}", filterID);
			 * message= message.replace("{1}", userID); }
			 */
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		}
	}

	/**
	 * Generic function that compares the content of the response with the expected
	 * values
	 * 
	 * @author Kiran.Sripada
	 * @param actual
	 *            response which is HashMap
	 * @param expected
	 *            response which is hashMap
	 * @param default
	 *            columns response which is hashMap
	 * @param test
	 */

	public void checkjobcolumns(HashMap<String, Object> actual_response, HashMap<String, Object> expected_response,
			HashMap<String, Object> defaultcolumnvalues, ExtentTest test) {
		/*
		 * test.log(LogStatus.INFO,"Compare the column id");
		 * spogServer.assertResponseItem(actual_response.get("column_id").toString(),
		 * expected_response.get("column_id").toString(),test);
		 * 
		 * test.log(LogStatus.INFO, "Compare the order id column ");
		 * spogServer.assertResponseItem(actual_response.get("order_id").toString(),
		 * expected_response.get("order_id").toString(),test);
		 * 
		 * test.log(LogStatus.INFO,"Compare the visiblity column ");
		 * if(expected_response.containsKey("visible")) {
		 * spogServer.assertResponseItem(actual_response.get("visible").toString(),
		 * expected_response.get("visible").toString(),test); }else {
		 * spogServer.assertResponseItem(actual_response.get("visible").toString(),
		 * defaultcolumnvalues.get("visible").toString(),test); }
		 * 
		 * test.log(LogStatus.INFO,"Compare the sort column");
		 * spogServer.assertResponseItem(actual_response.get("sort").toString(),
		 * defaultcolumnvalues.get("sort").toString(),test);
		 * 
		 * test.log(LogStatus.INFO,"Compare the filter column");
		 * spogServer.assertResponseItem(actual_response.get("filter").toString(),
		 * defaultcolumnvalues.get("filter").toString(),test);
		 */

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
	 * getSystemJobFilters
	 * 
	 * @author Ramya.Nagepalli
	 * @param filter
	 * @param user_id
	 * @param org_id
	 * @param validToken
	 * @param expectedstatuscode
	 * @param ExpectedErrorMessage
	 * @param test
	 * @return
	 */

	public Response getSystemJobFilters(ArrayList<String> filter, String user_id, String org_id, String validToken,
			int expectedstatuscode, SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		Response response = spogServer.getFilters(validToken, filterType.job_filter_global.toString());
		/*Response response = spogJobInvoker.getSystemJobFilters(validToken);*/
		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE
				|| expectedstatuscode == SpogConstants.SUCCESS_POST) {

			ArrayList<HashMap> ExpectedHeadContent = new ArrayList<HashMap>();

			ArrayList<HashMap<String, Object>> ActualHeadContent = new ArrayList<HashMap<String, Object>>();

			HashMap<String, Object> columnHeadContent = new HashMap<String, Object>();
			columnHeadContent.put("filter_id", filter.get(0));
			columnHeadContent.put("filter_name", "Jobs in Progress");
			columnHeadContent.put("user_id", "5c6d9135-f445-4582-a166-87f6075f8f37");
			columnHeadContent.put("organization_id", "cce55126-7269-4833-9bbf-432e663820a7");
			columnHeadContent.put("is_default", false);
			columnHeadContent.put("count", 0);
			ExpectedHeadContent.add(columnHeadContent);
			HashMap<String, Object> columnHeadContent1 = new HashMap<String, Object>();
			columnHeadContent1.put("filter_id", filter.get(1));
			columnHeadContent1.put("filter_name", "Failed Jobs");
			columnHeadContent1.put("user_id", "5c6d9135-f445-4582-a166-87f6075f8f37");
			columnHeadContent1.put("organization_id", "cce55126-7269-4833-9bbf-432e663820a7");
			columnHeadContent1.put("is_default", false);
			columnHeadContent1.put("count", 0);
			ExpectedHeadContent.add(columnHeadContent1);
			HashMap<String, Object> columnHeadContent2 = new HashMap<String, Object>();
			columnHeadContent2.put("filter_id", filter.get(2));
			columnHeadContent2.put("filter_name", "Missed Jobs");
			columnHeadContent2.put("user_id", "5c6d9135-f445-4582-a166-87f6075f8f37");
			columnHeadContent2.put("organization_id", "cce55126-7269-4833-9bbf-432e663820a7");
			columnHeadContent2.put("is_default", false);
			columnHeadContent2.put("count", "0");
			ExpectedHeadContent.add(columnHeadContent2);
			HashMap<String, Object> columnHeadContent3 = new HashMap<String, Object>();
			columnHeadContent3.put("filter_id", filter.get(3));
			columnHeadContent3.put("filter_name", "Cancelled Jobs");
			columnHeadContent3.put("user_id", "5c6d9135-f445-4582-a166-87f6075f8f37");
			columnHeadContent3.put("organization_id", "cce55126-7269-4833-9bbf-432e663820a7");
			columnHeadContent3.put("is_default", false);
			columnHeadContent3.put("count", 0);
			ExpectedHeadContent.add(columnHeadContent3);
			HashMap<String, Object> columnHeadContent4 = new HashMap<String, Object>();
			columnHeadContent4.put("filter_id", filter.get(4));
			columnHeadContent4.put("filter_name", "Successful Jobs");
			columnHeadContent4.put("user_id", "5c6d9135-f445-4582-a166-87f6075f8f37");
			columnHeadContent4.put("organization_id", "cce55126-7269-4833-9bbf-432e663820a7");
			columnHeadContent4.put("is_default", false);
			columnHeadContent4.put("count", 0);
			ExpectedHeadContent.add(columnHeadContent4);

			ActualHeadContent = response.then().extract().path("data");

			int length = ActualHeadContent.size();

			for (int i = 0; i < length; i++) {

				if (ActualHeadContent.get(i).get("filter_id").toString()
						.equals(ExpectedHeadContent.get(i).get("filter_id").toString())
						&& ActualHeadContent.get(i).get("filter_name").toString()
								.equals(ExpectedHeadContent.get(i).get("filter_name").toString())
						&& ActualHeadContent.get(i).get("user_id").toString()
								.equals(ExpectedHeadContent.get(i).get("user_id").toString())
						&& ActualHeadContent.get(i).get("organization_id").toString()
								.equals(ExpectedHeadContent.get(i).get("organization_id").toString())
						&& ActualHeadContent.get(i).get("is_default").toString()
								.equals(ExpectedHeadContent.get(i).get("is_default").toString())
						&& ActualHeadContent.get(i).get("count").toString()
								.equals(ExpectedHeadContent.get(i).get("count").toString())) {
					test.log(LogStatus.PASS, "comparision of system filters succeeded with default filters ");
				} else {
					test.log(LogStatus.FAIL, "comparision of system filters failed with default filters ");
				}
			}

			errorHandle.printDebugMessageInDebugFile("Response of get System job filters is " + response);
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		}

		return response;
	}

	public Response getSystemJobFilter(String token) {
		/*Response response = spogJobInvoker.getSystemJobFilters(token);*/
		Response response = spogServer.getFilters(token, "none", filterType.job_filter_global.toString(), "", test);
		return response;
	}

	public String createJobFilterForLoggedinUserWithCheck(String jobStatus, String policyID, String resourceID,
			String jobType, String startTimeTSStart, String startTimeTSEnd, String filterName, String isDefault,
			ExtentTest test) {
		Map<String, Object> jobFilterInfo = new HashMap<String, Object>();
		jobFilterInfo = jp.composeJobFilter(jobStatus, policyID, resourceID, jobType, startTimeTSStart, startTimeTSEnd,
				filterName, isDefault);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!jobFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.job_filter.toString(),
					filterName, "none", "none",
					Boolean.valueOf(isDefault), (HashMap<String, Object>)jobFilterInfo);
		}
		test.log(LogStatus.INFO, "Call the Rest API to create job filters");
		Response response = spogServer.createFilters(spogJobInvoker.getToken(), filter_info, "", test);
		/*Response response = spogJobInvoker.createJobFilterForLoggedinUser(jobFilterInfo);*/
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		// TODO: compare the response body
		response.then().body("data.filter_name", equalTo(filterName));

		ArrayList<String> responseJobStatus = response.then().extract().path("data.job_status");
		spogServer.assertFilterItem(jobStatus, responseJobStatus);

		ArrayList<String> responsePolicyID = response.then().extract().path("data.policy_id");
		spogServer.assertFilterItem(policyID, responsePolicyID);

		ArrayList<String> responseResourceID = response.then().extract().path("data.source_id");
		spogServer.assertFilterItem(resourceID, responseResourceID);

		ArrayList<String> responseJobType = response.then().extract().path("data.job_type");
		spogServer.assertFilterItem(jobType, responseJobType);

		String responseStartTimeTSStart = response.then().extract().path("data.start_time_ts.start");
		spogServer.assertResponseItem(startTimeTSStart, responseStartTimeTSStart);

		String responseStartTimeTSEnd = response.then().extract().path("data.start_time_ts.end");
		spogServer.assertResponseItem(startTimeTSEnd, responseStartTimeTSEnd);

		boolean responseIsDefault = response.then().extract().path("data.is_default");
		if (isDefault == null || isDefault.equalsIgnoreCase("none") || isDefault == ""
				|| isDefault.equalsIgnoreCase("false")) {
			assertEquals(responseIsDefault, false);
		} else if (isDefault.equalsIgnoreCase("true")) {
			assertEquals(responseIsDefault, true);
		}
		String filterID = response.then().extract().path("data.filter_id");
		return filterID;
	}

	public Response createJobFilterForLoggedinUser(String jobStatus, String policyID, String resourceID, String jobType,
			String startTimeTSStart, String startTimeTSEnd, String filterName, String isDefault, ExtentTest test) {
		Map<String, Object> jobFilterInfo = new HashMap<String, Object>();
		jobFilterInfo = jp.composeJobFilter(jobStatus, policyID, resourceID, jobType, startTimeTSStart, startTimeTSEnd,
				filterName, isDefault);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!jobFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.job_filter.toString(),
					filterName, "none", "none",
					Boolean.valueOf(isDefault), (HashMap<String, Object>)jobFilterInfo);
		}
		test.log(LogStatus.INFO, "Call the Rest API to create job filters");
		Response response = spogServer.createFilters(spogJobInvoker.getToken(), filter_info, "", test);
		/*Response response = spogJobInvoker.createJobFilterForLoggedinUser(jobFilterInfo);*/
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		// TODO: compare the response body
		response.then().body("data.filter_name", equalTo(filterName));

		ArrayList<String> responseJobStatus = response.then().extract().path("data.job_status");
		spogServer.assertFilterItem(jobStatus, responseJobStatus);

		ArrayList<String> responsePolicyID = response.then().extract().path("data.policy_id");
		spogServer.assertFilterItem(policyID, responsePolicyID);

		ArrayList<String> responseResourceID = response.then().extract().path("data.source_id");
		spogServer.assertFilterItem(resourceID, responseResourceID);

		ArrayList<String> responseJobType = response.then().extract().path("data.job_type");
		spogServer.assertFilterItem(jobType, responseJobType);

		String responseStartTimeTSStart = response.then().extract().path("data.start_time_ts.start");
		spogServer.assertResponseItem(startTimeTSStart, responseStartTimeTSStart);

		String responseStartTimeTSEnd = response.then().extract().path("data.start_time_ts.end");
		spogServer.assertResponseItem(startTimeTSEnd, responseStartTimeTSEnd);

		boolean responseIsDefault = response.then().extract().path("data.is_default");
		if (isDefault == null || isDefault.equalsIgnoreCase("none") || isDefault == ""
				|| isDefault.equalsIgnoreCase("false")) {
			assertEquals(responseIsDefault, false);
		} else if (isDefault.equalsIgnoreCase("true")) {
			assertEquals(responseIsDefault, true);
		}
		// String filterID = response.then().extract().path("data.filter_id");
		return response;
	}

	public void createJobFilterForLoggedinUserWithCodeCheck(String jobStatus, String policyID, String resourceID,
			String jobType, String startTimeTSStart, String startTimeTSEnd, String filterName, String isDefault,
			int statusCode, String errorCode, ExtentTest test) {
		Map<String, Object> jobFilterInfo = new HashMap<String, Object>();
		jobFilterInfo = jp.composeJobFilter(jobStatus, policyID, resourceID, jobType, startTimeTSStart, startTimeTSEnd,
				filterName, isDefault);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!jobFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.job_filter.toString(),
					filterName, "none", "none",
					Boolean.valueOf(isDefault), (HashMap<String, Object>)jobFilterInfo);
		}
		test.log(LogStatus.INFO, "Call the Rest API to create job filters");
		Response response = spogServer.createFilters(spogJobInvoker.getToken(), filter_info, "", test);
		/*Response response = spogJobInvoker.createJobFilterForLoggedinUser(jobFilterInfo);*/
		spogServer.checkResponseStatus(response, statusCode);
		spogServer.checkErrorCode(response, errorCode);
	}


	/**
	 * the function is used to compose alert information
	 * 
	 * @author Ramya.Nagepalli
	 * @param response
	 * @param alert_id
	 * @param org_id
	 * @param job_id
	 * @param jobtype
	 * @param jobSeverity
	 *            return response
	 * @param job_severity
	 * @param alert_data
	 * @param alert_details
	 * @param exp
	 * @param errorMessage
	 * @param expectedStstusCode
	 */
	public void checkAlertData(Response response, String alert_id, String organization_id, String org_name,
			String job_id, String jobtype, String jobSeverity, HashMap<String, Object> alert_details,
			ArrayList<String> alert_data, ArrayList<String> expected_actions, int expectedStatusCode,
			SpogMessageCode errorCode, ExtentTest test) {

		String description = null;

		String title = null;
		if (jobtype.contains("backup")&&alert_details.containsKey("source_name")) {
			if (jobtype == "backup_missed") {
				description =  alert_details.get("source_name").toString()  + " could have been disconnected or powered off.";
				title = "Source Missed Scheduled Backup";
			} else if (jobSeverity.equals("critical")) {
				description = alert_details.get("source_name").toString()  + " backup completed with errors.";
				title = "Backup Errors";
			} else {
				description =  alert_details.get("source_name").toString()  + " backup failed.";
				title = "Backup Failed";
			}
		} else if(jobtype.contains("recovery")&&alert_details.containsKey("source_name")){
			if (jobSeverity.equals("critical")) {
				description = alert_details.get("source_name").toString() + " recovery completed with errors.";
				title = "Recovery Errors";
			} else {
				description = alert_details.get("source_name").toString()  + " recovery failed.";
				title = "Recovery Failed";
			}
		}
		if (expectedStatusCode == SpogConstants.SUCCESS_POST) {

			String actual_alert_id = response.then().extract().path("data.alert_id").toString();
			spogServer.assertResponseItem(alert_id, actual_alert_id);

			String actual_org_id = response.then().extract().path("data.organization_id").toString();
			spogServer.assertResponseItem(organization_id, actual_org_id);

			String actual_job_id = response.then().extract().path("data.job_id");
			spogServer.assertResponseItem(job_id, actual_job_id);

			String actual_jobtype = response.then().extract().path("data.type");
			spogServer.assertResponseItem(jobtype, actual_jobtype);

			ArrayList<String> actual_alert_data = response.then().extract().path("data.alert_data");
			HashMap<String, Object> actual_details = response.then().extract().path("data.alert_details");
			if (!alert_details.isEmpty() && !alert_details.equals(null)) {
				spogServer.assertResponseItem(alert_details.get("source_name").toString(),
						actual_details.get("source_name").toString());
				spogServer.assertResponseItem(alert_details.get("destination_name").toString(),
						actual_details.get("destination_name").toString());
				spogServer.assertResponseItem(alert_details.get("policy_name").toString(),
						actual_details.get("policy_name").toString());
				test.log(LogStatus.PASS, "The expected data matched with the actual data");
			}

		} else if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			String actual_alert_id = response.then().extract().path("data.alert_id");
			spogServer.assertResponseItem(alert_id, actual_alert_id);

			String actual_jobtype = response.then().extract().path("data.type");
			spogServer.assertResponseItem(jobtype, actual_jobtype);

			String actual_description = response.then().extract().path("data.description").toString();
			spogServer.assertResponseItem(description, actual_description);

			String actual_title = response.then().extract().path("data.title");
			spogServer.assertResponseItem(title, actual_title);

			String actual_job_id = response.then().extract().path("data.job_id");
			spogServer.assertResponseItem(job_id, actual_job_id);

			String actual_Severity = response.then().extract().path("data.severity");
			spogServer.assertResponseItem(jobSeverity, actual_Severity);

			ArrayList<String> actual_available_actions = response.then().extract().path("data.allowed_actions");
			spogServer.assertResponseItem(expected_actions.toString(), actual_available_actions.toString());

			String actual_org_id = response.then().extract().path("data.organization.organization_id").toString();
			spogServer.assertResponseItem(organization_id, actual_org_id);

			String actual_org_name = response.then().extract().path("data.organization.organization_name")
					.toString();
			// spogServer.assertResponseItem("spog_udp_qa_"+org_name,actual_org_name);

			String actual_type = response.then().extract().path("data.organization.type");
			spogServer.assertResponseItem(null, actual_type);

			Boolean actual_blocked = response.then().extract().path("data.organization.blocked");
			spogServer.assertResponseItem("false", actual_blocked.toString());

			HashMap<String, Object> actual_details = response.then().extract().path("data.details");
			if (!alert_details.isEmpty() && !alert_details.equals(null)) {
				/*spogServer.assertResponseItem(alert_details.get("source_name").toString(),
						actual_details.get("source_name").toString());
				spogServer.assertResponseItem(alert_details.get("destination_name").toString(),
						actual_details.get("destination_name").toString());
				spogServer.assertResponseItem(alert_details.get("policy_name").toString(),
						actual_details.get("policy_name").toString());
				test.log(LogStatus.PASS, "The expected data matched with the actual data");*/
			}

		} else {
			String code = errorCode.getCodeString();
			String message = errorCode.getStatus();
			if (code.contains("0030000A")) {
				message = message.replace("{0}", organization_id);
			}
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
	}

	public void checkAlertData(Response response, String alert_id, String organization_id, String job_id,
			String jobtype, String jobSeverity, HashMap<String, Object> alert_details, ArrayList<String> alert_data,
			ArrayList<String> expected_actions, int expectedStatusCode, SpogMessageCode errorCode, ExtentTest test) {

		String description = null;

		String title = null;
		if (expectedStatusCode == SpogConstants.SUCCESS_POST) {

			if (jobtype.contains("backup")) {
				if (jobtype == "backupmissed") {
					description = alert_data.get(0).toString() + " could have been disconnected or powered off.";
					title = "Source Missed Scheduled Backup";
				} else if (jobSeverity.equals("critical")) {
					description = alert_data.get(0).toString() + " backup completed with errors.";
					title = "Backup Errors";
				} else {
					description = alert_data.get(0).toString() + " backup failed.";
					title = "Backup Failed";
				}
			} else {
				if (jobSeverity.equals("critical")) {
					description = alert_data.get(0).toString() + " recovery completed with errors.";
					title = "Recovery Errors";
				} else {
					description = alert_data.get(0).toString() + " recovery failed.";
					title = "Recovery Failed";
				}
			}

			String actual_alert_id = response.then().extract().path("data.alert_id").toString();
			spogServer.assertResponseItem(alert_id, actual_alert_id);

			String actual_org_id = response.then().extract().path("data.organization_id").toString();
			spogServer.assertResponseItem(organization_id, actual_org_id);

			String actual_job_id = response.then().extract().path("data.job_id");
			spogServer.assertResponseItem(job_id, actual_job_id);

			String actual_jobtype = response.then().extract().path("data.type");
			spogServer.assertResponseItem(jobtype, actual_jobtype);

			ArrayList<String> actual_alert_data = response.then().extract().path("data.alert_data");
			HashMap<String, Object> actual_details = response.then().extract().path("data.alert_details");
			if (!alert_details.isEmpty() && !alert_details.equals(null)) {
				spogServer.assertResponseItem(alert_details.get("source_name").toString(),
						actual_details.get("source_name").toString());
				spogServer.assertResponseItem(alert_details.get("destination_name").toString(),
						actual_details.get("destination_name").toString());
				spogServer.assertResponseItem(alert_details.get("policy_name").toString(),
						actual_details.get("policy_name").toString());
				test.log(LogStatus.PASS, "The expected data matched with the actual data");
			}

		} else if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			if (jobtype == "backupmissed") {
				description = alert_details.get("source_name").toString()
						+ " could have been disconnected or powered off.";
				title = "Source Missed Scheduled Backup";
			} else if (jobSeverity.equals("critical")) {
				description = alert_details.get("source_name").toString() + " backup completed with errors.";
				title = "Backup Errors";
			} else {
				description = alert_details.get("source_name").toString() + " backup failed.";
				title = "Backup Failed";
			}

			ArrayList<String> actual_alert_id = response.then().extract().path("data.alert_id");
			spogServer.assertFilterItem(alert_id, actual_alert_id);

			String actual_jobtype = response.then().extract().path("data[" + 0 + "].type");
			spogServer.assertResponseItem(jobtype, actual_jobtype);

			String actual_description = response.then().extract().path("data[" + 0 + "].description").toString();
			spogServer.assertResponseItem(description, actual_description);

			String actual_title = response.then().extract().path("data[" + 0 + "].title");
			spogServer.assertResponseItem(title, actual_title);

			String actual_job_id = response.then().extract().path("data[" + 0 + "].job_id");
			spogServer.assertResponseItem(job_id, actual_job_id);

			String actual_Severity = response.then().extract().path("data[" + 0 + "].severity");
			spogServer.assertResponseItem(jobSeverity, actual_Severity);

			ArrayList<String> actual_available_actions = response.then().extract()
					.path("data[" + 0 + "].allowed_actions");

			// HashMap<String, Object>
			// actual_alertDetail=response.then().extract().path("data.alertDetail");

			HashMap<String, Object> actual_details = response.then().extract().path("data[" + 0 + "].details");

			String actual_org_id = response.then().extract().path("data[" + 0 + "].organization.organization_id")
					.toString();
			spogServer.assertResponseItem(organization_id, actual_org_id);

			String actual_org_name = response.then().extract().path("data[" + 0 + "].organization.organization_name")
					.toString();
			// spogServer.assertResponseItem("spog_udp_qa_"+org_name,actual_org_name);

			String actual_type = response.then().extract().path("data[" + 0 + "].organization.type");
			spogServer.assertResponseItem(null, actual_type);

			Boolean actual_blocked = response.then().extract().path("data[0].organization.blocked");
			spogServer.assertResponseItem("false", actual_blocked.toString());

			actual_details = response.then().extract().path("data[0].details");
			if (!alert_details.isEmpty() && !alert_details.equals(null)) {
				spogServer.assertResponseItem(alert_details.get("source_name").toString(),
						actual_details.get("source_name").toString());
				spogServer.assertResponseItem(alert_details.get("destination_name").toString(),
						actual_details.get("destination_name").toString());
				spogServer.assertResponseItem(alert_details.get("policy_name").toString(),
						actual_details.get("policy_name").toString());
				test.log(LogStatus.PASS, "The expected data matched with the actual data");
			}

		} else {
			String code = errorCode.getCodeString();
			String message = errorCode.getStatus();
			if (code.contains("0030000A")) {
				message = message.replace("{0}", organization_id);
			}
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
	}

	/**
	 * the function is used to getOrganizationSupport
	 * 
	 * @author Ramya.Nagepalli
	 * @param adminToken
	 * @param expectedStatusCode
	 *            return response
	 */
	public Response getOrganizationSupport(String adminToken, String additionalURL, int expectedStatusCode,
			ExtentTest test) {

		Response response = spogJobInvoker.getOrganizationSupport(adminToken, additionalURL, test);

		spogServer.checkResponseStatus(response, expectedStatusCode);

		return response;
	}

	/**
	 * the function is used to checkOrganizationSupportDetails
	 * 
	 * @author Ramya.Nagepalli
	 * @param response
	 * @param organization_id
	 * @param organization_name
	 * @param organization_type
	 * @param blocked
	 * @param sales_url
	 * @param support_url
	 * @param phone_number
	 * @param email
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 *            return
	 */
	public void checkOrganizationSupportDetails(Response response, String organization_id, String organization_name,
			String organization_type, String blocked, String Email, String Phone_number, String Support_url,
			String Sales_url, int expectedStatusCode, SpogMessageCode expectedErrorMessage, ExtentTest test) {

		String pin = null;
		String knowledgebase_url, documentation_url;

		/*
		 * long ts = (long) (System.currentTimeMillis() / 1000L) ; String salt=
		 * "wu5bpq";// this should come from application.properties pin= ts - ts%3600;
		 * pin= substring(md5(organization_id + pin + salt), 0, 8); pin= hexToDec(pin);
		 * pin= substring((String)pin, 0, 6);// return the first 6 digits
		 */
		if (Email == "" || Email == null) {
			Email = "support@zetta.net";
			Phone_number = "650-590-0967";
			Support_url = "https://arcserve.com/support-chat";
			Sales_url = "https://arcserve.com/sales";
		}
		knowledgebase_url = "https://arcserve.zendesk.com/hc/en-us/requests/new?ticket_form_id=182986";
		documentation_url = "http://documentation.arcserve.com/Arcserve-Cloud/Available/ENU/Bookshelf_Files/HTML/olh/default.htm";

		spogServer.checkResponseStatus(response, expectedStatusCode);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			HashMap<HashMap<String, Object>, Object> actual_response = new HashMap<HashMap<String, Object>, Object>();
			HashMap<String, Object> act_org_details = new HashMap<String, Object>();

			actual_response = response.then().extract().path("data");
			act_org_details = (HashMap<String, Object>) actual_response.get("organization");

			String act_organization_id = act_org_details.get("organization_id").toString();
			spogServer.assertResponseItem(organization_id, act_organization_id);

			String act_organization_name = act_org_details.get("organization_name").toString().toLowerCase();

			if (!(organization_type
					.equals("csr"))) {/*
										 * if (organization_type.contains("child")) { organization_name =
										 * organization_name.toLowerCase();
										 * 
										 * } else if (!(organization_type.contains("child"))){ organization_name =
										 * "spog_udp_qa_" + organization_name.toLowerCase();
										 * 
										 * }
										 */
				// spogServer.assertResponseItem(organization_name.toLowerCase(),
				// act_organization_name);
			}

			String act_type = act_org_details.get("organization_type").toString();
			spogServer.assertResponseItem(organization_type, act_type);

			String act_blocked = act_org_details.get("blocked").toString();
			spogServer.assertResponseItem(blocked, act_blocked);

			String act_email = actual_response.get("email").toString();
			spogServer.assertResponseItem(Email, act_email);

			String act_phone_number = actual_response.get("phone_number").toString();
			spogServer.assertResponseItem(Phone_number, act_phone_number);

			String act_pin = actual_response.get("pin").toString();
			// spogServer.assertResponseItem(pin, act_pin);

			String act_knowledgebase_url = actual_response.get("knowledgebase_url").toString();
			spogServer.assertResponseItem(knowledgebase_url, act_knowledgebase_url);

			String act_support_url = actual_response.get("support_url").toString();
			spogServer.assertResponseItem(Support_url, act_support_url);

			String act_sales_url = actual_response.get("sales_url").toString();
			spogServer.assertResponseItem(Sales_url, act_sales_url);

			String act_documentation_url = actual_response.get("documentation_url").toString();
			spogServer.assertResponseItem(documentation_url, act_documentation_url);

			test.log(LogStatus.PASS, "The expected data matched with actual data " + expectedStatusCode);
		} else {
			String code = expectedErrorMessage.getCodeString();

			String message = expectedErrorMessage.getStatus();

			if (code.contains("0030000A")) {
				message = message.replace("{0}", organization_id);
			}

			spogServer.checkErrorCode(response, code);

			test.log(LogStatus.PASS, "The error code matched with the expected " + code);

			spogServer.checkErrorMessage(response, message);

			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);

		}
	}

	/**
	 * the function is used to getOrganizationSupportForSpecifiedOrganization
	 * 
	 * @author Ramya.Nagepalli
	 * @param adminToken
	 * @param organization_id
	 * @param expectedStatusCode
	 *            return response
	 */
	public Response getOrganizationSupportDetailsForSpecifiedOrganization(String adminToken, String organization_id,
			int expectedStatusCode, ExtentTest test) {
		// TODO Auto-generated method stub
		Response response = spogJobInvoker.getOrganizationSupportDetailsForSpecifiedOrganization(adminToken,
				organization_id, test);

		spogServer.checkResponseStatus(response, expectedStatusCode);

		return response;
	}

	public String createJobFilterForLoggedinUserWithCheckEx(String jobStatus, String policyID, String resourceID,
			String jobType, String startTimeType, String startTimeTSStart, String startTimeTSEnd, String sourceName,
			String filterName, String isDefault, ExtentTest test) {
		Map<String, Object> jobFilterInfo = new HashMap<String, Object>();
		jobFilterInfo = jp.composeJobFilterEx(jobStatus, policyID, resourceID, jobType, startTimeType, startTimeTSStart,
				startTimeTSEnd, sourceName, filterName, isDefault);

		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!jobFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.job_filter.toString(),
					filterName, "none", "none",
					Boolean.valueOf(isDefault), (HashMap<String, Object>)jobFilterInfo);
		}
		test.log(LogStatus.INFO, "Call the Rest API to create job filters");
		Response response = spogServer.createFilters(spogJobInvoker.getToken(), filter_info, "", test);
		/*Response response = spogJobInvoker.createJobFilterForLoggedinUser(jobFilterInfo);*/
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		// TODO: compare the response body
		response.then().body("data.filter_name", equalTo(filterName));

		ArrayList<String> responseJobStatus = response.then().extract().path("data.job_status");
		spogServer.assertFilterItem(jobStatus, responseJobStatus);

		ArrayList<String> responsePolicyID = response.then().extract().path("data.policy_id");
		spogServer.assertFilterItem(policyID, responsePolicyID);

		ArrayList<String> responseResourceID = response.then().extract().path("data.source_id");
		spogServer.assertFilterItem(resourceID, responseResourceID);

		ArrayList<String> responseJobType = response.then().extract().path("data.job_type");
		spogServer.assertFilterItem(jobType, responseJobType);

		String responseStartTimeType = response.then().extract().path("data.start_time_ts.type");
		spogServer.assertResponseItem(startTimeType, responseStartTimeType);

		String responseStartTimeTSStart = response.then().extract().path("data.start_time_ts.start_ts").toString();
		spogServer.assertResponseItem(startTimeTSStart, responseStartTimeTSStart);

		String responseStartTimeTSEnd = response.then().extract().path("data.start_time_ts.end_ts").toString();
		spogServer.assertResponseItem(startTimeTSEnd, responseStartTimeTSEnd);

		String responseSourceName = response.then().extract().path("data.source_name");
		spogServer.assertResponseItem(sourceName, responseSourceName);

		boolean responseIsDefault = response.then().extract().path("data.is_default");
		if (isDefault == null || isDefault.equalsIgnoreCase("none") || isDefault == ""
				|| isDefault.equalsIgnoreCase("false")) {
			assertEquals(responseIsDefault, false);
		} else if (isDefault.equalsIgnoreCase("true")) {
			assertEquals(responseIsDefault, true);
		}
		String filterID = response.then().extract().path("data.filter_id");
		return filterID;
	}

	public void createJobFilterForLoggedinUserWithCodeCheckEx(String jobStatus, String policyID, String resourceID,
			String jobType, String startTimeType, String startTimeTSStart, String startTimeTSEnd, String sourceName,
			String filterName, String isDefault, int statusCode, String errorCode, ExtentTest test) {
		Map<String, Object> jobFilterInfo = new HashMap<String, Object>();
		jobFilterInfo = jp.composeJobFilterEx(jobStatus, policyID, resourceID, jobType, startTimeType, startTimeTSStart,
				startTimeTSEnd, sourceName, filterName, isDefault);

		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!jobFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.job_filter.toString(),
					filterName, "none", "none",
					Boolean.valueOf(isDefault), (HashMap<String, Object>)jobFilterInfo);
		}
		test.log(LogStatus.INFO, "Call the Rest API to create job filters");
		Response response = spogServer.createFilters(spogJobInvoker.getToken(), filter_info, "", test);
	/*	Response response = spogJobInvoker.createJobFilterForLoggedinUser(jobFilterInfo);*/

		spogServer.checkResponseStatus(response, statusCode);
		spogServer.checkErrorCode(response, errorCode);
	}

	public String createJobFilterWithCheckEx(String userID, String jobStatus, String policyID, String resourceID,
			String jobType, String startTimeType, String startTimeTSStart, String startTimeTSEnd, String sourceName,
			String filterName, String isDefault, ExtentTest test) {
		Map<String, Object> jobFilterInfo = new HashMap<String, Object>();
		jobFilterInfo = jp.composeJobFilterEx(jobStatus, policyID, resourceID, jobType, startTimeType, startTimeTSStart,
				startTimeTSEnd, sourceName, filterName, isDefault);
		
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!jobFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.job_filter.toString(),
					filterName, userID, "none",
					Boolean.valueOf(isDefault), (HashMap<String, Object>)jobFilterInfo);
		}
		test.log(LogStatus.INFO, "Call the Rest API to create job filters");
		Response response = spogServer.createFilters(spogJobInvoker.getToken(), filter_info, "", test);

		/*Response response = spogJobInvoker.createJobFilter(userID, jobFilterInfo);*/
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		// TODO: compare the response body
		response.then().body("data.filter_name", equalTo(filterName));

		ArrayList<String> responseJobStatus = response.then().extract().path("data.job_status");
		spogServer.assertFilterItem(jobStatus, responseJobStatus);

		ArrayList<String> responsePolicyID = response.then().extract().path("data.policy_id");
		spogServer.assertFilterItem(policyID, responsePolicyID);

		ArrayList<String> responseResourceID = response.then().extract().path("data.source_id");
		spogServer.assertFilterItem(resourceID, responseResourceID);

		ArrayList<String> responseJobType = response.then().extract().path("data.job_type");
		spogServer.assertFilterItem(jobType, responseJobType);

		String responseStartTimeType = response.then().extract().path("data.start_time_ts.type");
		spogServer.assertResponseItem(startTimeType, responseStartTimeType);

		String responseStartTimeTSStart = response.then().extract().path("data.start_time_ts.start_ts").toString();
		spogServer.assertResponseItem(startTimeTSStart, responseStartTimeTSStart);

		String responseStartTimeTSEnd = response.then().extract().path("data.start_time_ts.end_ts").toString();
		spogServer.assertResponseItem(startTimeTSEnd, responseStartTimeTSEnd);

		String responseSourceName = response.then().extract().path("data.source_name");
		spogServer.assertResponseItem(sourceName, responseSourceName);

		boolean responseIsDefault = response.then().extract().path("data.is_default");
		if (isDefault == null || isDefault.equalsIgnoreCase("none") || isDefault == ""
				|| isDefault.equalsIgnoreCase("false")) {
			assertEquals(responseIsDefault, false);
		} else if (isDefault.equalsIgnoreCase("true")) {
			assertEquals(responseIsDefault, true);
		}
		String filterID = response.then().extract().path("data.filter_id");
		return filterID;
	}

	public void createJobFilterWithCodeCheckEx(String userID, String jobStatus, String policyID, String resourceID,
			String jobType, String startTimeType, String startTimeTSStart, String startTimeTSEnd, String sourceName,
			String filterName, String isDefault, int statusCode, String errorCode, ExtentTest test) {
		Map<String, Object> jobFilterInfo = new HashMap<String, Object>();
		jobFilterInfo = jp.composeJobFilterEx(jobStatus, policyID, resourceID, jobType, startTimeType, startTimeTSStart,
				startTimeTSEnd, sourceName, filterName, isDefault);

		/*Response response = spogJobInvoker.createJobFilter(userID, jobFilterInfo);*/
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!jobFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.job_filter.toString(),
					filterName, userID, "none",
					Boolean.valueOf(isDefault), (HashMap<String, Object>)jobFilterInfo);
		}
		test.log(LogStatus.INFO, "Call the Rest API to create job filters");
		Response response = spogServer.createFilters(spogJobInvoker.getToken(), filter_info, "", test);
		spogServer.checkResponseStatus(response, statusCode);
		spogServer.checkErrorCode(response, errorCode);
	}

	public Response updateJobFilterForLoggedinUserWithCheckEx(String filterID, String jobStatus, String policyID,
			String resourceID, String jobType, String startTimeType, String startTimeTSStart, String startTimeTSEnd,
			String sourceName, String filterName, String isDefault, ExtentTest test) {

		Map<String, Object> jobFilterInfo = new HashMap<String, Object>();
		jobFilterInfo = jp.composeJobFilterEx(jobStatus, policyID, resourceID, jobType, startTimeType, startTimeTSStart,
				startTimeTSEnd, sourceName, filterName, isDefault);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!jobFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.job_filter.toString(),
					filterName,  "none", "none",
					Boolean.valueOf(isDefault), (HashMap<String, Object>)jobFilterInfo);
		}
		Response response = spogServer.updateFilterById(spogJobInvoker.getToken(), filterID, "none", filter_info, "", test);
		/*Response response = spogJobInvoker.updateJobFilterForLoggedinUser(filterID, jobFilterInfo);*/
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);

		response.then().body("data.filter_name", equalTo(filterName));

		ArrayList<String> responseJobStatus = response.then().extract().path("data.job_status");
		spogServer.assertFilterItem(jobStatus, responseJobStatus);

		ArrayList<String> responsePolicyID = response.then().extract().path("data.policy_id");
		spogServer.assertFilterItem(policyID, responsePolicyID);

		ArrayList<String> responseResourceID = response.then().extract().path("data.source_id");
		spogServer.assertFilterItem(resourceID, responseResourceID);

		ArrayList<String> responseJobType = response.then().extract().path("data.job_type");
		spogServer.assertFilterItem(jobType, responseJobType);

		String responseStartTimeType = response.then().extract().path("data.start_time_ts.type");
		spogServer.assertResponseItem(startTimeType, responseStartTimeType);

		String responseStartTimeTSStart = response.then().extract().path("data.start_time_ts.start_ts").toString();
		spogServer.assertResponseItem(startTimeTSStart, responseStartTimeTSStart);

		String responseStartTimeTSEnd = response.then().extract().path("data.start_time_ts.end_ts").toString();
		spogServer.assertResponseItem(startTimeTSEnd, responseStartTimeTSEnd);

		String responseSourceName = response.then().extract().path("data.source_name");
		spogServer.assertResponseItem(sourceName, responseSourceName);

		boolean responseIsDefault = response.then().extract().path("data.is_default");
		if (isDefault == null || isDefault.equalsIgnoreCase("none") || isDefault == ""
				|| isDefault.equalsIgnoreCase("false")) {
			assertEquals(responseIsDefault, false);
		} else if (isDefault.equalsIgnoreCase("true")) {
			assertEquals(responseIsDefault, true);
		}
		return response;
	}

	public void updateJobFilterForLoggedinUserWithCodeCheckEx(String filterID, String jobStatus, String policyID,
			String resourceID, String jobType, String startTimeType, String startTimeTSStart, String startTimeTSEnd,
			String sourceName, String filterName, String isDefault, int statusCode, String errorCode, ExtentTest test) {

		Map<String, Object> jobFilterInfo = new HashMap<String, Object>();
		jobFilterInfo = jp.composeJobFilterEx(jobStatus, policyID, resourceID, jobType, startTimeType, startTimeTSStart,
				startTimeTSEnd, sourceName, filterName, isDefault);
		/*Response response = spogJobInvoker.updateJobFilterForLoggedinUser(filterID, jobFilterInfo);*/
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!jobFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.job_filter.toString(),
					filterName,  "none", "none",
					Boolean.valueOf(isDefault), (HashMap<String, Object>)jobFilterInfo);
		}
		Response response = spogServer.updateFilterById(spogJobInvoker.getToken(), filterID, "none", filter_info, "", test);
		spogServer.checkResponseStatus(response, statusCode);
		spogServer.checkErrorCode(response, errorCode);
	}

	public Response updateJobFilterWithCheckEx(String userID, String filterID, String jobStatus, String policyID,
			String resourceID, String jobType, String startTimeType, String startTimeTSStart, String startTimeTSEnd,
			String sourceName, String filterName, String isDefault, ExtentTest test) {

		Map<String, Object> jobFilterInfo = new HashMap<String, Object>();
		jobFilterInfo = jp.composeJobFilterEx(jobStatus, policyID, resourceID, jobType, startTimeType, startTimeTSStart,
				startTimeTSEnd, sourceName, filterName, isDefault);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!jobFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.job_filter.toString(),
					filterName,  userID, "none",
					Boolean.valueOf(isDefault), (HashMap<String, Object>)jobFilterInfo);
		}
		Response response = spogServer.updateFilterById(spogJobInvoker.getToken(), filterID, userID, filter_info, "", test);
		/*Response response = spogJobInvoker.updateJobFilter(userID, filterID, jobFilterInfo);*/
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);

		response.then().body("data.filter_name", equalTo(filterName));

		ArrayList<String> responseJobStatus = response.then().extract().path("data.job_status");
		spogServer.assertFilterItem(jobStatus, responseJobStatus);

		ArrayList<String> responsePolicyID = response.then().extract().path("data.policy_id");
		spogServer.assertFilterItem(policyID, responsePolicyID);

		ArrayList<String> responseResourceID = response.then().extract().path("data.source_id");
		spogServer.assertFilterItem(resourceID, responseResourceID);

		ArrayList<String> responseJobType = response.then().extract().path("data.job_type");
		spogServer.assertFilterItem(jobType, responseJobType);

		String responseStartTimeType = response.then().extract().path("data.start_time_ts.type");
		spogServer.assertResponseItem(startTimeType, responseStartTimeType);

		String responseStartTimeTSStart = response.then().extract().path("data.start_time_ts.start_ts").toString();
		spogServer.assertResponseItem(startTimeTSStart, responseStartTimeTSStart);

		String responseStartTimeTSEnd = response.then().extract().path("data.start_time_ts.end_ts").toString();
		spogServer.assertResponseItem(startTimeTSEnd, responseStartTimeTSEnd);

		String responseSourceName = response.then().extract().path("data.source_name");
		spogServer.assertResponseItem(sourceName, responseSourceName);

		boolean responseIsDefault = response.then().extract().path("data.is_default");
		if (isDefault == null || isDefault.equalsIgnoreCase("none") || isDefault == ""
				|| isDefault.equalsIgnoreCase("false")) {
			assertEquals(responseIsDefault, false);
		} else if (isDefault.equalsIgnoreCase("true")) {
			assertEquals(responseIsDefault, true);
		}
		return response;
	}

	public void updateJobFilterWithCodeCheckEx(String userID, String filterID, String jobStatus, String policyID,
			String resourceID, String jobType, String startTimeType, String startTimeTSStart, String startTimeTSEnd,
			String sourceName, String filterName, String isDefault, int statusCode, String errorCode, ExtentTest test) {

		Map<String, Object> jobFilterInfo = new HashMap<String, Object>();
		jobFilterInfo = jp.composeJobFilterEx(jobStatus, policyID, resourceID, jobType, startTimeType, startTimeTSStart,
				startTimeTSEnd, sourceName, filterName, isDefault);
		/*Response response = spogJobInvoker.updateJobFilter(userID, filterID, jobFilterInfo);*/
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!jobFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.job_filter.toString(),
					filterName,  userID, "none",
					Boolean.valueOf(isDefault), (HashMap<String, Object>)jobFilterInfo);
		}
		Response response = spogServer.updateFilterById(spogJobInvoker.getToken(), filterID, userID, filter_info, "", test);

		spogServer.checkResponseStatus(response, statusCode);
		spogServer.checkErrorCode(response, errorCode);
	}


	/**
	 * the function is used to validate alert information
	 * 
	 * @author ramya.Nagepalli
	 * @param response
	 * @param org_id
	 * @param org_name
	 * @param org_type
	 * @param job_id
	 * @param jobSeverity
	 * @param alert_details
	 * @param allowed_actions
	 * @param expectedStstusCode
	 * @param errorCode
	 *            return response
	 */
	public void checkGetAlertsData(Response response, String organization_id, String org_name, String org_type,
			String job_id, String Severity, HashMap<String, Object> alert_details, ArrayList<String> allowed_actions,
			int expectedStatusCode, SpogMessageCode errorCode, ExtentTest test) {

		spogServer.checkResponseStatus(response, expectedStatusCode);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			ArrayList<HashMap> actual = response.then().extract().path("data");
			String description = null;

			String title = null;
			int size = actual.size();

			for (int i = 0; i < size; i++) {
				String exp_type = response.then().extract().path("data[" + i + "].type").toString();

				if (exp_type.equals("clouddirect_trial_start")) {
					description = "Your Cloud Direct trial has started.";
					title = "Cloud Direct Trial Started";
				} else if (exp_type.equals("cloudhybrid_trial_start")) {
					description = "Your Cloud Hybrid trial has started.";
					title = "Cloud Hybrid Trail Started";
				}
				if (exp_type.contains("recovery")) {
					description = alert_details.get("source_name").toString() + " recovery completed with errors.";
					title = "Recovery Errors";
				}

				String actual_org_id = response.then().extract().path("data[" + i + "].organization.organization_id")
						.toString();
				spogServer.assertResponseItem(organization_id, actual_org_id);

				String actual_org_name = response.then().extract()
						.path("data[" + i + "].organization.organization_name").toString();
				// spogServer.assertResponseItem(org_name,actual_org_name);

				Object actual_org_type = response.then().extract().path("data[" + i + "].organization.type");
				// spogServer.assertResponseItem(org_type,actual_org_type);

				Object actual_blocked = response.then().extract().path("data[" + i + "].organization.blocked");

				String actual_alerttype = response.then().extract().path("data[" + i + "].type").toString();
				spogServer.assertResponseItem(actual_alerttype, actual_alerttype);

				String actual_Severity = response.then().extract().path("data[" + i + "].severity").toString();
				spogServer.assertResponseItem(Severity, actual_Severity);

				String actual_title = response.then().extract().path("data[" + i + "].title").toString().toLowerCase();
				spogServer.assertResponseItem(title.toLowerCase(), actual_title);

				String actual_description = response.then().extract().path("data[" + i + "].description").toString()
						.toLowerCase();
				spogServer.assertResponseItem(description.toLowerCase(), actual_description);

			}
			test.log(LogStatus.PASS, "The expected data is null in the response");
		}

		else {
			String code = errorCode.getCodeString();
			String message = errorCode.getStatus();
			if (code.contains("0030000A")) {
				message = message.replace("{0}", organization_id);
			}
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);

		}
	}

	/**
	 * createJobFilterForSpecifiedUserWithCheckEx_savedsearch
	 * 
	 * @author Ramya.Nagepalli
	 * @param additionalURL
	 * @param userID
	 * @param organization_id
	 * @param jobStatus
	 * @param policyID
	 * @param jobType
	 * @param startTimeType
	 * @param startTimeTSStart
	 * @param startTimeTSEnd
	 * @param job_filter_type
	 * @param filterName
	 * @param isDefault
	 * @param test
	 * @return
	 */
	public Response createJobFilterForSpecifiedUserWithCheckEx_savedsearch(String additionalURL, String userID,
			String organization_id, String jobStatus, String policyID, String jobType, String startTimeType,
			String startTimeTSStart, String startTimeTSEnd, String job_filter_type, String filterName, String isDefault,
			ExtentTest test) {
		Map<String, Object> jobFilterInfo = new HashMap<String, Object>();
		jobFilterInfo = jp.composeJobFilterEx_savesearch(jobStatus, policyID, jobType, startTimeType, startTimeTSStart,
				startTimeTSEnd, organization_id, filterName, isDefault, job_filter_type);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!jobFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.job_filter.toString(),
					filterName, userID, "none",
					Boolean.valueOf(isDefault), (HashMap<String, Object>)jobFilterInfo);
		}
		test.log(LogStatus.INFO, "Call the Rest API to create job filters");
		Response response = spogServer.createFilters(spogJobInvoker.getToken(), filter_info, additionalURL, test);
		/*Response response = spogJobInvoker.createJobFilter(additionalURL, userID, jobFilterInfo);*/
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		return response;
	}

	/**
	 * updateJobFilterWithCheck
	 * 
	 * @author Ramya.Nagepalli
	 * @param additionalURL
	 * @param userID
	 * @param filterID
	 * @param jobStatus
	 * @param policyID
	 * @param resourceID
	 * @param jobType
	 * @param startTimeTSStart
	 * @param startTimeTSEnd
	 * @param filterName
	 * @param isDefault
	 * @param test
	 * @return
	 */
	public Response updateJobFilterWithCheck(String additionalURL, String userID, String filterID, String jobStatus,
			String policyID, String resourceID, String jobType, String startTimeTSStart, String startTimeTSEnd,
			String filterName, String isDefault, ExtentTest test) {
		Map<String, Object> jobFilterInfo = new HashMap<String, Object>();
		jobFilterInfo = jp.composeJobFilter(jobStatus, policyID, resourceID, jobType, startTimeTSStart, startTimeTSEnd,
				filterName, isDefault);
		
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!jobFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.job_filter.toString(),
					filterName,  userID, "none",
					Boolean.valueOf(isDefault), (HashMap<String, Object>)jobFilterInfo);
		}
		Response response = spogServer.updateFilterById(spogJobInvoker.getToken(), filterID, userID, filter_info, "", test);
		/*Response response = spogJobInvoker.updateJobFilter(additionalURL, userID, filterID, jobFilterInfo);*/
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);

		return response;
	}

	/**
	 * create job filter and return the response - Added "view_type" param
	 * 
	 * @author Ramya.Nagepalli
	 * @param userID
	 * @param jobStatus
	 * @param policyID
	 * @param resourceID
	 * @param jobType
	 * @param startTimeTSStart
	 * @param startTimeTSEnd
	 * @param endTimeTSStart
	 * @param endTimeTSEnd
	 * @param filterName
	 * @param isDefault
	 * @param group_id
	 * @param organization_id
	 * @param view_type
	 * @param test
	 * @return
	 */
	public Response createJobFilterWithCheckResponse(String userID, String jobStatus, String policyID,
			String resourceID, String jobType, String startTimeTSStart, String startTimeTSEnd, String endTimeTSStart,
			String endTimeTSEnd, String filterName, String isDefault, String group_id, String organization_id,
			String view_type, ExtentTest test) {
		Map<String, Object> jobFilterInfo = new HashMap<String, Object>();
		jobFilterInfo = jp.composeJobFilterEx(jobStatus, policyID, resourceID, jobType, startTimeTSStart,
				startTimeTSEnd, endTimeTSStart, endTimeTSEnd, filterName, isDefault, group_id, organization_id,
				view_type);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!jobFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.job_filter.toString(),
					filterName, userID, "none",
					Boolean.valueOf(isDefault), (HashMap<String, Object>)jobFilterInfo);
		}
		test.log(LogStatus.INFO, "Call the Rest API to create job filters");
		Response response = spogServer.createFilters(spogJobInvoker.getToken(), filter_info, "", test);
		/*Response response = spogJobInvoker.createJobFilter(userID, jobFilterInfo);*/
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		// TODO: compare the response body
		response.then().body("data.filter_name", equalTo(filterName));

		ArrayList<String> responseJobStatus = response.then().extract().path("data.job_status");
		spogServer.assertFilterItem(jobStatus, responseJobStatus);

		ArrayList<String> responsePolicyID = response.then().extract().path("data.policy_id");
		spogServer.assertFilterItem(policyID, responsePolicyID);

		ArrayList<String> responseResourceID = response.then().extract().path("data.source_id");
		spogServer.assertFilterItem(resourceID, responseResourceID);

		ArrayList<String> responseGroupID = response.then().extract().path("data.group_id");
		spogServer.assertFilterItem(group_id, responseGroupID);

		ArrayList<String> responseJobType = response.then().extract().path("data.job_type");
		spogServer.assertFilterItem(jobType, responseJobType);

		String responseStartTimeTSStart = response.then().extract().path("data.start_time_ts.start_ts").toString();
		// spogServer.assertResponseItem(startTimeTSStart, responseStartTimeTSStart);

		String responseStartTimeTSEnd = response.then().extract().path("data.start_time_ts.end_ts").toString();
		// spogServer.assertResponseItem(startTimeTSEnd, responseStartTimeTSEnd);

		boolean responseIsDefault = response.then().extract().path("data.is_default");
		if (isDefault == null || isDefault.equalsIgnoreCase("none") || isDefault == ""
				|| isDefault.equalsIgnoreCase("false")) {
			assertEquals(responseIsDefault, false);
		} else if (isDefault.equalsIgnoreCase("true")) {
			assertEquals(responseIsDefault, true);
		}

		String responseOrg_id = response.then().extract().path("data.organization_id");
		spogServer.assertResponseItem(organization_id, responseOrg_id);

		String responseView_type = response.then().extract().path("data.view_type");
		spogServer.assertResponseItem(view_type, responseView_type);

		return response;
	}

	/**
	 * composeExpectedJobFilter - added "view_type" and "group_id" params
	 * 
	 * @author Ramya.Nagepalli
	 * @param filter_id
	 * @param filter_name
	 * @param user_id
	 * @param organization_id
	 * @param job_status
	 * @param job_type
	 * @param policy_id
	 * @param resource_id
	 * @param startTimeTSStart
	 * @param startTimeTSEnd
	 * @param startTimeType
	 * @param sourceName
	 * @param isDefault
	 * @param count
	 * @param group_id
	 * @param view_type
	 * @return
	 */
	public HashMap<String, Object> composeExpectedJobFilter(String filter_id, String filter_name, String user_id,
			String organization_id, String job_status, String job_type, String policy_id, String resource_id,
			String startTimeTSStart, String startTimeTSEnd, String startTimeType, String sourceName, String isDefault,
			int count, String group_id, String view_type) {
		HashMap<String, Object> expected_response = new HashMap<>();
		expected_response.put("filter_id", filter_id);
		expected_response.put("filter_name", filter_name);
		expected_response.put("user_id", user_id);
		expected_response.put("organization_id", organization_id);
		expected_response.put("job_status", job_status);
		expected_response.put("job_type", job_type);
		expected_response.put("policy_id", policy_id);
		expected_response.put("source_id", resource_id);
		expected_response.put("group_id", group_id);
		expected_response.put("view_type", view_type);

		HashMap<String, String> startTimeTSInfo = new HashMap<String, String>();
		if (!"none".equalsIgnoreCase(startTimeTSStart)) {
			startTimeTSInfo.put("start", startTimeTSStart);
		}
		if (!"none".equalsIgnoreCase(startTimeTSEnd)) {
			startTimeTSInfo.put("end", startTimeTSEnd);
		}
		if (startTimeTSInfo.size() != 0) {
			expected_response.put("start_time_ts", startTimeTSInfo);
		}
		expected_response.put("is_default", Boolean.valueOf(isDefault));
		expected_response.put("count", count);
		return expected_response;
	}

}
