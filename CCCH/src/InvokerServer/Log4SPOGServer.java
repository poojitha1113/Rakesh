package InvokerServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import Constants.SpogConstants;
import Constants.SpogMessageCode;
import Constants.FilterTypes.filterType;

import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.SpogConstants;
import Constants.SpogMessageCode;
import dataPreparation.JsonPreparation;
import genericutil.ErrorHandler;
import invoker.Log4GatewayInvoker;
import invoker.Log4SPOGInvoker;
import io.restassured.response.Response;

public class Log4SPOGServer {
	private Log4SPOGInvoker log4SPOGInvoker;
	private GatewayServer gatewayServer;
	private SPOGServer spogServer;
	private static JsonPreparation jp = new JsonPreparation();
	static ErrorHandler errorHandle = ErrorHandler.getErrorHandler();

	public Log4SPOGServer(String baseURI, String port) {
		log4SPOGInvoker = new Log4SPOGInvoker(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);

		spogServer = new SPOGServer(baseURI, port);

		spogServer = new SPOGServer(baseURI, port);

	}

	public void setToken(String token) {
		log4SPOGInvoker.setToken(token);
		spogServer.setToken(token);
	}

	/**
	 * create log filter and check body items
	 * 
	 * @author shan.jing
	 * @param user_id:
	 *            UUID
	 * @param expected_organization_id:
	 *            user's organization id;
	 * @param filter_name:
	 *            String
	 * @param time_range:
	 *            like ["2018-01-02 12:03:30","2018-01-22 12:40:40"], the first
	 *            item is start_time and the second item is end_time
	 * @param job_type
	 * @param log_severity_type:
	 *            like "error", "warning","information"
	 * @param is_default:
	 *            "true" or "false"
	 * @param test
	 * @return filter_id
	 */
	public String createLogFilterwithCheck(String user_id, String expected_organization_id, String filter_name,
			String type, long[] time_range, String job_type, String log_severity_type, String is_default,
			ExtentTest test) {
		Map<String, Object> logTimeRange = null;
		if (null != time_range) {
			logTimeRange = jp.composeLogTimeRangeInfo(type, time_range);
		}
		Map<String, Object> logFilterInfo = jp.composeLogFilterInfo(filter_name, logTimeRange, job_type,
				log_severity_type, is_default, test);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!logFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.log_filter.toString(),
					filter_name, "none", "none",
					(boolean)logFilterInfo.get("is_default"),(HashMap<String, Object>) logFilterInfo);
		}
		test.log(LogStatus.INFO, "Call the Rest API to create log filters");
		Response response = spogServer.createFilters(log4SPOGInvoker.getToken(), filter_info, "", test);
	/*	Response response = log4SPOGInvoker.createLogFilter(user_id, logFilterInfo);*/
		response.then().statusCode(SpogConstants.SUCCESS_POST);
		response.then().body("data.filter_name", equalTo(filter_name));
		String responseOrganizationID = response.then().extract().path("data.organization_id");
		spogServer.assertResponseItem(expected_organization_id, responseOrganizationID);

		ArrayList<String> response_job_type = response.then().extract().path("data.job_type");
		if (job_type != null && job_type.equalsIgnoreCase("")) {
			job_type = "emptyarray";
		}
		spogServer.assertFilterItem(job_type, response_job_type);

		ArrayList<String> response_log_severity_type = response.then().extract().path("data.log_severity_type");
		if (log_severity_type != null && log_severity_type.equalsIgnoreCase("")) {
			log_severity_type = "emptyarray";
		}
		spogServer.assertFilterItem(log_severity_type, response_log_severity_type);

		String responseLogSourceType = response.then().extract().path("data.is_default").toString();
		spogServer.assertResponseItem(is_default, responseLogSourceType);

		HashMap<String, String> responseMessageData = response.then().extract().path("data.log_ts");
		if (null == time_range || (time_range.toString() == "" || time_range.length == 0)) {
			assertNull(responseMessageData, "compare " + time_range + " passed once we don't set it");
		} else {
			String log_ts_start = response.then().extract().path("data.log_ts.start_ts").toString();
			String log_ts_end = response.then().extract().path("data.log_ts.end_ts").toString();
			String log_ts_type = response.then().extract().path("data.log_ts.type").toString();
			if (Long.parseLong(log_ts_start) != time_range[0]) {
				assertTrue("compare message_data " + time_range[0] + "failed", false);
			}
			if (Long.parseLong(log_ts_end) != time_range[1]) {
				assertTrue("compare message_data " + time_range[1] + "failed", false);
			}
			if (!log_ts_type.equalsIgnoreCase(type)) {
				assertTrue("compare message_data for type in log_ts failed", false);
			}
		}
		String logID = response.then().extract().path("data.filter_id");
		return logID;
	}

	/**
	 * create log filter and check body items
	 * 
	 * @author shan.jing
	 * @param user_id:
	 *            UUID
	 * @param expected_organization_id:
	 *            user's organization id;
	 * @param filter_name:
	 *            String
	 * @param time_range:
	 *            like ["2018-01-02 12:03:30","2018-01-22 12:40:40"], the first
	 *            item is start_time and the second item is end_time
	 * @param job_type
	 * @param log_severity_type:
	 *            like "error", "warning","information"
	 * @param is_default:
	 *            "true" or "false"
	 * @param message_id:
	 * @param source_id
	 * @param message:
	 * @param source_name:
	 *            "true" or "false"
	 * @param origin:
	 * @param test
	 * @return filter_id
	 */
	public String createLogFilterwithCheck(String user_id, String expected_organization_id, String filter_name,
			String type, long[] time_range, String job_type, String log_severity_type, String is_default,
			String message_id, String source_id, String message, String source_name, String origin, ExtentTest test) {
		Map<String, Object> logTimeRange = null;
		if (null != time_range) {
			logTimeRange = jp.composeLogTimeRangeInfo(type, time_range);
		}
		Map<String, Object> logFilterInfo = jp.composeLogFilterInfo(filter_name, logTimeRange, job_type,
				log_severity_type, is_default, message_id, source_id, message, source_name, origin, test);

		HashMap<String, Object> filter_info = new HashMap<String, Object>();
	if (!logFilterInfo.isEmpty()) {
		filter_info = jp.composeFilterInfo(filterType.log_filter.toString(),
				filter_name, user_id, "none",
				(boolean)logFilterInfo.get("is_default"),(HashMap<String, Object>) logFilterInfo);
	}
	test.log(LogStatus.INFO, "Call the Rest API to create log filters");
	Response response = spogServer.createFilters(log4SPOGInvoker.getToken(), filter_info, "", test);
		/*Response response = log4SPOGInvoker.createLogFilter(user_id, logFilterInfo);*/
		response.then().statusCode(SpogConstants.SUCCESS_POST);
		response.then().body("data.filter_name", equalTo(filter_name));
		String responseOrganizationID = response.then().extract().path("data.organization_id");
		spogServer.assertResponseItem(expected_organization_id, responseOrganizationID);

		ArrayList<String> response_job_type = response.then().extract().path("data.job_type");
		if (job_type != null && job_type.equalsIgnoreCase("")) {
			job_type = "emptyarray";
		}
		spogServer.assertFilterItem(job_type, response_job_type);

		ArrayList<String> response_log_severity_type = response.then().extract().path("data.log_severity_type");
		if (log_severity_type != null && log_severity_type.equalsIgnoreCase("")) {
			log_severity_type = "emptyarray";
		}
		spogServer.assertFilterItem(log_severity_type, response_log_severity_type);

		String responseLogIsDefault = response.then().extract().path("data.is_default").toString();
		spogServer.assertResponseItem(is_default, responseLogIsDefault);
		ArrayList<String> responseMessageId = response.then().extract().path("data.message_id");
		if (message_id != null && message_id.equalsIgnoreCase("")) {
			message_id = "emptyarray";
		}
		spogServer.assertFilterItem(message_id, responseMessageId);
		ArrayList<String> responseSourceId = response.then().extract().path("data.source_id");
		if (source_id != null && source_id.equalsIgnoreCase("")) {
			source_id = "emptyarray";
			assertEquals(responseSourceId.size(), 1, "it return value has one item.");
			assertEquals(responseSourceId.get(0), "");
		} else {
			spogServer.assertFilterItem(source_id, responseSourceId);
		}

		String responseMessage = response.then().extract().path("data.message");
		spogServer.assertResponseItem(message, responseMessage);
		String responseSourceName = response.then().extract().path("data.source_name");
		spogServer.assertResponseItem(source_name, responseSourceName);

		String responseOrigin = response.then().extract().path("data.origin");
		spogServer.assertResponseItem(origin, responseOrigin);

		HashMap<String, String> responseMessageData = response.then().extract().path("data.log_ts");
		if (null == time_range || (time_range.toString() == "" || time_range.length == 0)) {
			assertNull(responseMessageData, "compare " + time_range + " passed once we don't set it");
		} else {
			String log_ts_start = response.then().extract().path("data.log_ts.start_ts").toString();
			String log_ts_end = response.then().extract().path("data.log_ts.end_ts").toString();
			String log_ts_type = response.then().extract().path("data.log_ts.type").toString();
			if (Long.parseLong(log_ts_start) != time_range[0]) {
				assertTrue("compare message_data " + time_range[0] + "failed", false);
			}
			if (Long.parseLong(log_ts_end) != time_range[1]) {
				assertTrue("compare message_data " + time_range[1] + "failed", false);
			}
			if (!log_ts_type.equalsIgnoreCase(type)) {
				assertTrue("compare message_data for type in log_ts failed", false);
			}
		}
		String logID = response.then().extract().path("data.filter_id");
		return logID;
	}

	/**
	 * @author Nagamalleswari.Sykam
	 * @param user_id
	 * @param expected_organization_id
	 * @param filter_name
	 * @param type
	 * @param time_range
	 * @param log_severity_type
	 * @param is_default
	 * @param origin
	 * @param test
	 * @return
	 */

	public String createLogFilters(String user_id,
			String expected_organization_id,
			String filter_name,
			String type, 
			long[] time_range, 
			String log_severity_type,
			String is_default,

			ExtentTest test) {
		Map<String, Object> logTimeRange = null;
		if (null != time_range) {
			logTimeRange = jp.composeLogTimeRangeInfo(type, time_range);
		}
		Map<String, Object> logFilterInfo = jp.composeLogFiltersInfo(filter_name, logTimeRange,
				log_severity_type, is_default,  test);
		/*Response response = log4SPOGInvoker.createLogFilter(user_id, logFilterInfo);*/
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!logFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.log_filter.toString(),
					filter_name, user_id, "none",
					(boolean)logFilterInfo.get("is_default"),(HashMap<String, Object>) logFilterInfo);
		}
		test.log(LogStatus.INFO, "Call the Rest API to create log filters");
		Response response = spogServer.createFilters(log4SPOGInvoker.getToken(), filter_info, "", test);
		response.then().statusCode(SpogConstants.SUCCESS_POST);
		response.then().body("data.filter_name", equalTo(filter_name));
		String responseOrganizationID = response.then().extract().path("data.organization_id");
		spogServer.assertResponseItem(expected_organization_id, responseOrganizationID);


		ArrayList<String> response_log_severity_type = response.then().extract().path("data.log_severity_type");
		if (log_severity_type != null && log_severity_type.equalsIgnoreCase("")) {
			log_severity_type = "emptyarray";
		}
		spogServer.assertFilterItem(log_severity_type, response_log_severity_type);

		String responseLogIsDefault = response.then().extract().path("data.is_default").toString();
		spogServer.assertResponseItem(is_default, responseLogIsDefault);


		HashMap<String, String> responseMessageData = response.then().extract().path("data.log_ts");
		if (null == time_range || (time_range.toString() == "" || time_range.length == 0)) {
			assertNull(responseMessageData, "compare " + time_range + " passed once we don't set it");
		} else {
			String log_ts_start = response.then().extract().path("data.log_ts.start_ts").toString();
			String log_ts_end = response.then().extract().path("data.log_ts.end_ts").toString();
			String log_ts_type = response.then().extract().path("data.log_ts.type").toString();
			if (Long.parseLong(log_ts_start) != time_range[0]) {
				assertTrue("compare message_data " + time_range[0] + "failed", false);
			}
			if (Long.parseLong(log_ts_end) != time_range[1]) {
				assertTrue("compare message_data " + time_range[1] + "failed", false);
			}
			if (!log_ts_type.equalsIgnoreCase(type)) {
				assertTrue("compare message_data for type in log_ts failed", false);
			}
		}
		String logID = response.then().extract().path("data.filter_id");
		return logID;
	}

	public Response createLogFilter(String user_id, String expected_organization_id, String filter_name, String type,
			long[] time_range, String job_type, String log_severity_type, String is_default, String message_id,
			String source_id, String message, String source_name, String origin, ExtentTest test) {
		Map<String, Object> logTimeRange = null;
		if (null != time_range) {
			logTimeRange = jp.composeLogTimeRangeInfo(type, time_range);
		}
		Map<String, Object> logFilterInfo = jp.composeLogFilterInfo(filter_name, logTimeRange, job_type,
				log_severity_type, is_default, message_id, source_id, message, source_name, origin, test);
		/*Response response = log4SPOGInvoker.createLogFilter(user_id, logFilterInfo);*/
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!logFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.log_filter.toString(),
					filter_name, user_id, "none",
					(boolean)logFilterInfo.get("is_default"),(HashMap<String, Object>) logFilterInfo);
		}
		test.log(LogStatus.INFO, "Call the Rest API to create log filters");
		Response response = spogServer.createFilters(log4SPOGInvoker.getToken(), filter_info, "", test);
		response.then().statusCode(SpogConstants.SUCCESS_POST);
		response.then().body("data.filter_name", equalTo(filter_name));
		String responseOrganizationID = response.then().extract().path("data.organization_id");
		spogServer.assertResponseItem(expected_organization_id, responseOrganizationID);

		ArrayList<String> response_job_type = response.then().extract().path("data.job_type");
		if (job_type != null && job_type.equalsIgnoreCase("")) {
			job_type = "emptyarray";
		}
		spogServer.assertFilterItem(job_type, response_job_type);

		ArrayList<String> response_log_severity_type = response.then().extract().path("data.log_severity_type");
		if (log_severity_type != null && log_severity_type.equalsIgnoreCase("")) {
			log_severity_type = "emptyarray";
		}
		spogServer.assertFilterItem(log_severity_type, response_log_severity_type);

		String responseLogIsDefault = response.then().extract().path("data.is_default").toString();
		spogServer.assertResponseItem(is_default, responseLogIsDefault);
		ArrayList<String> responseMessageId = response.then().extract().path("data.message_id");
		if (message_id != null && message_id.equalsIgnoreCase("")) {
			message_id = "emptyarray";
		}
		spogServer.assertFilterItem(message_id, responseMessageId);
		ArrayList<String> responseSourceId = response.then().extract().path("data.source_id");
		if (source_id != null && source_id.equalsIgnoreCase("")) {
			source_id = "emptyarray";
			assertEquals(responseSourceId.size(), 1, "it return value has one item.");
			assertEquals(responseSourceId.get(0), "");
		} else {
			spogServer.assertFilterItem(source_id, responseSourceId);
		}

		String responseMessage = response.then().extract().path("data.message");
		spogServer.assertResponseItem(message, responseMessage);
		String responseSourceName = response.then().extract().path("data.source_name");
		spogServer.assertResponseItem(source_name, responseSourceName);

		String responseOrigin = response.then().extract().path("data.origin");
		spogServer.assertResponseItem(origin, responseOrigin);
		HashMap<String, String> responseMessageData = response.then().extract().path("data.log_ts");
		if (null == time_range || (time_range.toString() == "" || time_range.length == 0)) {
			assertNull(responseMessageData, "compare " + time_range + " passed once we don't set it");
		} else {
			String log_ts_start = response.then().extract().path("data.log_ts.start_ts").toString();
			String log_ts_end = response.then().extract().path("data.log_ts.end_ts").toString();
			String log_ts_type = response.then().extract().path("data.log_ts.type").toString();
			if (Long.parseLong(log_ts_start) != time_range[0]) {
				assertTrue("compare message_data " + time_range[0] + "failed", false);
			}
			if (Long.parseLong(log_ts_end) != time_range[1]) {
				assertTrue("compare message_data " + time_range[1] + "failed", false);
			}
			if (!log_ts_type.equalsIgnoreCase(type)) {
				assertTrue("compare message_data for type in log_ts failed", false);
			}
		}
		return response;
	}

	/**
	 * create log filter failed and check status code and error code
	 * 
	 * @author shan.jing
	 * @param user_id:
	 *            UUID
	 * @param expected_organization_id:
	 *            user's organization id;
	 * @param filter_name:
	 *            String
	 * @param time_range:
	 *            like ["2018-01-02 12:03:30","2018-01-22 12:40:40"], the first
	 *            item is start_time and the second item is end_time
	 * @param job_type
	 * @param log_severity_type:
	 *            like "error", "warning","information"
	 * @param is_default:
	 *            "true" or "false"
	 * @param expectedStatusCode
	 * @param expectedErrorCode
	 * @param test
	 * @return
	 */
	public void createLogFilterFailedWithExpectedStatusCode(String user_id, String expected_organization_id,
			String filter_name, String type, long[] time_range, String job_type, String log_severity_type,
			String is_default, int expectedStatusCode, String expectedErrorCode, ExtentTest test) {
		Map<String, Object> logTimeRange = null;
		if (null != time_range) {
			logTimeRange = jp.composeLogTimeRangeInfo(type, time_range);
		}
		Map<String, Object> logFilterInfo = jp.composeLogFilterInfo(filter_name, logTimeRange, job_type,
				log_severity_type, is_default, test);
	/*	Response response = log4SPOGInvoker.createLogFilter(user_id, logFilterInfo);*/
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!logFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.log_filter.toString(),
					filter_name, user_id, "none",
					(boolean)logFilterInfo.get("is_default"),(HashMap<String, Object>) logFilterInfo);
		}
		test.log(LogStatus.INFO, "Call the Rest API to create log filters");
		Response response = spogServer.createFilters(log4SPOGInvoker.getToken(), filter_info, "", test);
		response.then().statusCode(expectedStatusCode);
		spogServer.checkErrorCode(response, expectedErrorCode);
	}

	public void createLogFilterFailedWithExpectedStatusCode(String user_id, String expected_organization_id,
			String filter_name, String type, long[] time_range, String job_type, String log_severity_type,
			String is_default, String message_id, String source_id, String message, String source_name, String origin,
			int expectedStatusCode, String expectedErrorCode, ExtentTest test) {
		Map<String, Object> logTimeRange = null;
		if (null != time_range) {
			logTimeRange = jp.composeLogTimeRangeInfo(type, time_range);
		}
		Map<String, Object> logFilterInfo = jp.composeLogFilterInfo(filter_name, logTimeRange, job_type,
				log_severity_type, is_default, message_id, source_id, message, source_name, origin, test);
		/*Response response = log4SPOGInvoker.createLogFilter(user_id, logFilterInfo);*/
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!logFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.log_filter.toString(),
					filter_name, user_id, "none",
					(boolean)logFilterInfo.get("is_default"),(HashMap<String, Object>) logFilterInfo);
		}
		test.log(LogStatus.INFO, "Call the Rest API to create log filters");
		Response response = spogServer.createFilters(log4SPOGInvoker.getToken(), filter_info, "", test);
		response.then().statusCode(expectedStatusCode);
		spogServer.checkErrorCode(response, expectedErrorCode);
	}

	/**
	 * create log filter failed and check status code and error code
	 * 
	 * @author shan.jing
	 * @param user_id:
	 *            UUID
	 * @param expected_organization_id:
	 *            user's organization id;
	 * @param filter_name:
	 *            String
	 * @param time_range:
	 *            like ["2018-01-02 12:03:30","2018-01-22 12:40:40"], the first
	 *            item is start_time and the second item is end_time
	 * @param job_type
	 * @param log_severity_type:
	 *            like "error", "warning","information"
	 * @param is_default:
	 *            "true" or "false"
	 * @param expectedStatusCode
	 * @param expectedErrorCode
	 * @param test
	 * @return
	 */
	public void createLogFilterForLoggedInUserFailedWithExpectedStatusCode(String user_id,
			String expected_organization_id, String filter_name, String type, long[] time_range, String job_type,
			String log_severity_type, String is_default, int expectedStatusCode, String expectedErrorCode,
			ExtentTest test) {
		Map<String, Object> logTimeRange = null;
		if (null != time_range) {
			logTimeRange = jp.composeLogTimeRangeInfo(type, time_range);
		}
		Map<String, Object> logFilterInfo = jp.composeLogFilterInfo(filter_name, logTimeRange, job_type,
				log_severity_type, is_default, test);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!logFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.log_filter.toString(),
					filter_name, "none", "none",
					(boolean)logFilterInfo.get("is_default"),(HashMap<String, Object>) logFilterInfo);
		}
		test.log(LogStatus.INFO, "Call the Rest API to create log filters");
		Response response = spogServer.createFilters(log4SPOGInvoker.getToken(), filter_info, "", test);
		/*Response response = log4SPOGInvoker.createLogFilterForLoggedInUser(logFilterInfo);*/
		response.then().statusCode(expectedStatusCode);
		spogServer.checkErrorCode(response, expectedErrorCode);
	}

	public void createLogFilterForLoggedInUserFailedWithExpectedStatusCode(String user_id,
			String expected_organization_id, String filter_name, String type, long[] time_range, String job_type,
			String log_severity_type, String is_default, String message_id, String source_id, String message,
			String source_name, String origin, int expectedStatusCode, String expectedErrorCode, ExtentTest test) {
		Map<String, Object> logTimeRange = null;
		if (null != time_range) {
			logTimeRange = jp.composeLogTimeRangeInfo(type, time_range);
		}
		Map<String, Object> logFilterInfo = jp.composeLogFilterInfo(filter_name, logTimeRange, job_type,
				log_severity_type, is_default, message_id, source_id, message, source_name, origin, test);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!logFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.log_filter.toString(),
					filter_name, "none", "none",
					(boolean)logFilterInfo.get("is_default"),(HashMap<String, Object>) logFilterInfo);
		}
		test.log(LogStatus.INFO, "Call the Rest API to create log filters");
		Response response = spogServer.createFilters(log4SPOGInvoker.getToken(), filter_info, "", test);
	/*	Response response = log4SPOGInvoker.createLogFilterForLoggedInUser(logFilterInfo);*/
		response.then().statusCode(expectedStatusCode);
		spogServer.checkErrorCode(response, expectedErrorCode);
	}

	/**
	 * create log filter and check body items
	 * 
	 * @author shan.jing
	 * @param user_id:
	 *            UUID
	 * @param expected_organization_id:
	 *            user's organization id;
	 * @param filter_name:
	 *            String
	 * @param time_range:
	 *            like ["2018-01-02 12:03:30","2018-01-22 12:40:40"], the first
	 *            item is start_time and the second item is end_time
	 * @param job_type
	 * @param log_severity_type:
	 *            like "error", "warning","information"
	 * @param is_default:
	 *            "true" or "false"
	 * @param test
	 * @return filter_id
	 */
	public String createLogFilterForLoggedInUserwithCheck(String user_id, String expected_organization_id,
			String filter_name, String type, long[] time_range, String job_type, String log_severity_type,
			String is_default, ExtentTest test) {
		Map<String, Object> logTimeRange = null;
		if (null != time_range) {
			logTimeRange = jp.composeLogTimeRangeInfo(type, time_range);
		}
		Map<String, Object> logFilterInfo = jp.composeLogFilterInfo(filter_name, logTimeRange, job_type,
				log_severity_type, is_default, test);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!logFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.log_filter.toString(),
					filter_name, "none", "none",
					(boolean)logFilterInfo.get("is_default"),(HashMap<String, Object>) logFilterInfo);
		}
		test.log(LogStatus.INFO, "Call the Rest API to create log filters");
		Response response = spogServer.createFilters(log4SPOGInvoker.getToken(), filter_info, "", test);
	/*	Response response = log4SPOGInvoker.createLogFilterForLoggedInUser(logFilterInfo);*/
		response.then().statusCode(SpogConstants.SUCCESS_POST);
		response.then().body("data.filter_name", equalTo(filter_name));
		String responseOrganizationID = response.then().extract().path("data.organization_id");
		spogServer.assertResponseItem(expected_organization_id, responseOrganizationID);

		ArrayList<String> response_job_type = response.then().extract().path("data.job_type");
		if (job_type != null && job_type.equalsIgnoreCase("")) {
			job_type = "emptyarray";
		}
		spogServer.assertFilterItem(job_type, response_job_type);

		ArrayList<String> response_log_severity_type = response.then().extract().path("data.log_severity_type");
		if (log_severity_type != null && log_severity_type.equalsIgnoreCase("")) {
			log_severity_type = "emptyarray";
		}
		spogServer.assertFilterItem(log_severity_type, response_log_severity_type);

		String responseLogSourceType = response.then().extract().path("data.is_default").toString();
		spogServer.assertResponseItem(is_default, responseLogSourceType);

		HashMap<String, String> responseMessageData = response.then().extract().path("data.log_ts");
		if (null == time_range || (time_range.toString() == "" || time_range.length == 0)) {
			assertNull(responseMessageData, "compare " + time_range + " passed once we don't set it");
		} else {
			String log_ts_start = response.then().extract().path("data.log_ts.start_ts").toString();
			String log_ts_end = response.then().extract().path("data.log_ts.end_ts").toString();
			String log_ts_type = response.then().extract().path("data.log_ts.type").toString();
			if (Long.parseLong(log_ts_start) != time_range[0]) {
				assertTrue("compare message_data " + time_range[0] + "failed", false);
			}
			if (Long.parseLong(log_ts_end) != time_range[1]) {
				assertTrue("compare message_data " + time_range[1] + "failed", false);
			}
			if (!log_ts_type.equalsIgnoreCase(type)) {
				assertTrue("compare message_data for type in log_ts failed", false);
			}
		}
		String logID = response.then().extract().path("data.filter_id");
		return logID;
	}

	/**
	 * create log filter and check body items
	 * 
	 * @author shan.jing
	 * @param user_id:
	 *            UUID
	 * @param expected_organization_id:
	 *            user's organization id;
	 * @param filter_name:
	 *            String
	 * @param time_range:
	 *            like ["2018-01-02 12:03:30","2018-01-22 12:40:40"], the first
	 *            item is start_time and the second item is end_time
	 * @param job_type
	 * @param log_severity_type:
	 *            like "error", "warning","information"
	 * @param is_default:
	 *            "true" or "false"
	 * @param message_id:
	 * @param source_id
	 * @param message:
	 * @param source_name:
	 *            "true" or "false"
	 * @param origin:
	 * @param test
	 * @return filter_id
	 */
	public String createLogFilterForLoggedInUserwithCheck(String user_id, String expected_organization_id,
			String filter_name, String type, long[] time_range, String job_type, String log_severity_type,
			String is_default, String message_id, String source_id, String message, String source_name, String origin,
			ExtentTest test) {
		Map<String, Object> logTimeRange = null;
		if (null != time_range) {
			logTimeRange = jp.composeLogTimeRangeInfo(type, time_range);
		}
		Map<String, Object> logFilterInfo = jp.composeLogFilterInfo(filter_name, logTimeRange, job_type,
				log_severity_type, is_default, message_id, source_id, message, source_name, origin, test);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!logFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.log_filter.toString(),
					filter_name, "none", "none",
					(boolean)logFilterInfo.get("is_default"),(HashMap<String, Object>) logFilterInfo);
		}
		test.log(LogStatus.INFO, "Call the Rest API to create log filters");
		Response response = spogServer.createFilters(log4SPOGInvoker.getToken(), filter_info, "", test);
		/*Response response = log4SPOGInvoker.createLogFilterForLoggedInUser(logFilterInfo);*/
		response.then().statusCode(SpogConstants.SUCCESS_POST);
		response.then().body("data.filter_name", equalTo(filter_name));
		String responseOrganizationID = response.then().extract().path("data.organization_id");
		spogServer.assertResponseItem(expected_organization_id, responseOrganizationID);

		ArrayList<String> response_job_type = response.then().extract().path("data.job_type");
		if (job_type != null && job_type.equalsIgnoreCase("")) {
			job_type = "emptyarray";
		}
		spogServer.assertFilterItem(job_type, response_job_type);

		ArrayList<String> response_log_severity_type = response.then().extract().path("data.log_severity_type");
		if (log_severity_type != null && log_severity_type.equalsIgnoreCase("")) {
			log_severity_type = "emptyarray";
		}
		spogServer.assertFilterItem(log_severity_type, response_log_severity_type);

		String responseLogIsDefault = response.then().extract().path("data.is_default").toString();
		spogServer.assertResponseItem(is_default, responseLogIsDefault);

		ArrayList<String> responseMessageId = response.then().extract().path("data.message_id");
		if (message_id != null && message_id.equalsIgnoreCase("")) {
			message_id = "emptyarray";
		}
		spogServer.assertFilterItem(message_id, responseMessageId);
		ArrayList<String> responseSourceId = response.then().extract().path("data.source_id");
		if (source_id != null && source_id.equalsIgnoreCase("")) {
			source_id = "emptyarray";
			assertEquals(responseSourceId.size(), 1, "it return value has one item.");
			assertEquals(responseSourceId.get(0), "");
		} else {
			spogServer.assertFilterItem(source_id, responseSourceId);
		}

		String responseMessage = response.then().extract().path("data.message");
		spogServer.assertResponseItem(message, responseMessage);
		String responseSourceName = response.then().extract().path("data.source_name");
		spogServer.assertResponseItem(source_name, responseSourceName);

		String responseOrigin = response.then().extract().path("data.origin");
		spogServer.assertResponseItem(origin, responseOrigin);

		HashMap<String, String> responseMessageData = response.then().extract().path("data.log_ts");
		if (null == time_range || (time_range.toString() == "" || time_range.length == 0)) {
			assertNull(responseMessageData, "compare " + time_range + " passed once we don't set it");
		} else {
			String log_ts_start = response.then().extract().path("data.log_ts.start_ts").toString();
			String log_ts_end = response.then().extract().path("data.log_ts.end_ts").toString();
			String log_ts_type = response.then().extract().path("data.log_ts.type").toString();
			if (Long.parseLong(log_ts_start) != time_range[0]) {
				assertTrue("compare message_data " + time_range[0] + "failed", false);
			}
			if (Long.parseLong(log_ts_end) != time_range[1]) {
				assertTrue("compare message_data " + time_range[1] + "failed", false);
			}
			if (!log_ts_type.equalsIgnoreCase(type)) {
				assertTrue("compare message_data for type in log_ts failed", false);
			}
		}
		String logID = response.then().extract().path("data.filter_id");
		return logID;
	}

	/**
	 * update log filter failed and check status code and error code
	 * 
	 * @author shan.jing
	 * @param user_id:
	 *            UUID
	 * @param expected_organization_id:
	 *            user's organization id;
	 * @param filter_name:
	 *            String
	 * @param time_range:
	 *            like ["2018-01-02 12:03:30","2018-01-22 12:40:40"], the first
	 *            item is start_time and the second item is end_time
	 * @param job_type
	 * @param log_severity_type:
	 *            like "error", "warning","information"
	 * @param is_default:
	 *            "true" or "false"
	 * @param expectedStatusCode
	 * @param expectedErrorCode
	 * @param test
	 * @return
	 */
	public void updateLogFilterFailedWithExpectedStatusCode(String user_id, String filter_id,
			String expected_organization_id, String filter_name, String type, long[] time_range, String job_type,
			String log_severity_type, String is_default, int expectedStatusCode, String expectedErrorCode,
			ExtentTest test) {
		Map<String, Object> logTimeRange = null;
		if (null != time_range) {
			logTimeRange = jp.composeLogTimeRangeInfo(type, time_range);
		}
		Map<String, Object> logFilterInfo = jp.composeLogFilterInfo(filter_name, logTimeRange, job_type,
				log_severity_type, is_default, test);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!logFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.log_filter.toString(),
					filter_name,user_id, "none",
					(boolean)logFilterInfo.get("is_default"),(HashMap<String, Object>) logFilterInfo);
		}
		test.log(LogStatus.INFO, "Call the Rest API to update log filters");
		Response response = spogServer.updateFilterById(log4SPOGInvoker.getToken(), filter_id, user_id, filter_info, "", test);
		/*Response response = log4SPOGInvoker.updateLogFilter(user_id, filter_id, logFilterInfo);*/
		response.then().statusCode(expectedStatusCode);
		spogServer.checkErrorCode(response, expectedErrorCode);
	}

	public void updateLogFilterFailedWithExpectedStatusCode(String user_id, String filter_id,
			String expected_organization_id, String filter_name, String type, long[] time_range, String job_type,
			String log_severity_type, String is_default, String message_id, String source_id, String message,
			String source_name, String origin, int expectedStatusCode, String expectedErrorCode, ExtentTest test) {
		Map<String, Object> logTimeRange = null;
		if (null != time_range) {
			logTimeRange = jp.composeLogTimeRangeInfo(type, time_range);
		}
		Map<String, Object> logFilterInfo = jp.composeLogFilterInfo(filter_name, logTimeRange, job_type,
				log_severity_type, is_default, message_id, source_id, message, source_name, origin, test);
	/*	Response response = log4SPOGInvoker.updateLogFilter(user_id, filter_id, logFilterInfo);*/
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!logFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.log_filter.toString(),
					filter_name,user_id, "none",
					(boolean)logFilterInfo.get("is_default"),(HashMap<String, Object>) logFilterInfo);
		}
		test.log(LogStatus.INFO, "Call the Rest API to update log filters");
		Response response = spogServer.updateFilterById(log4SPOGInvoker.getToken(), filter_id, user_id, filter_info, "", test);
		response.then().statusCode(expectedStatusCode);
		spogServer.checkErrorCode(response, expectedErrorCode);
	}

	/**
	 * update log filter failed and check status code and error code
	 * 
	 * @author shan.jing
	 * @param user_id:
	 *            UUID
	 * @param expected_organization_id:
	 *            user's organization id;
	 * @param filter_name:
	 *            String
	 * @param time_range:
	 *            like ["2018-01-02 12:03:30","2018-01-22 12:40:40"], the first
	 *            item is start_time and the second item is end_time
	 * @param job_type
	 * @param log_severity_type:
	 *            like "error", "warning","information"
	 * @param is_default:
	 *            "true" or "false"
	 * @param expectedStatusCode
	 * @param expectedErrorCode
	 * @param test
	 * @return
	 */
	public void updateLogFilterForLoggedInUserFailedWithExpectedStatusCode(String user_id, String filter_id,
			String expected_organization_id, String filter_name, String type, long[] time_range, String job_type,
			String log_severity_type, String is_default, String message_id, String source_id, String message,
			String source_name, String origin, int expectedStatusCode, String expectedErrorCode, ExtentTest test) {
		Map<String, Object> logTimeRange = null;
		if (null != time_range) {
			logTimeRange = jp.composeLogTimeRangeInfo(type, time_range);
		}
		Map<String, Object> logFilterInfo = jp.composeLogFilterInfo(filter_name, logTimeRange, job_type,
				log_severity_type, is_default, message_id, source_id, message, source_name, origin, test);
		/*Response response = log4SPOGInvoker.updateLogFilterForLoggedInUser(filter_id, logFilterInfo);*/
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!logFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.log_filter.toString(),
					filter_name,"none", "none",
					(boolean)logFilterInfo.get("is_default"),(HashMap<String, Object>) logFilterInfo);
		}
		test.log(LogStatus.INFO, "Call the Rest API to update log filters");
		Response response = spogServer.updateFilterById(log4SPOGInvoker.getToken(), filter_id, "none", filter_info, "", test);
		response.then().statusCode(expectedStatusCode);
		spogServer.checkErrorCode(response, expectedErrorCode);
	}

	public void updateLogFilterForLoggedInUserFailedWithExpectedStatusCode(String user_id, String filter_id,
			String expected_organization_id, String filter_name, String type, long[] time_range, String job_type,
			String log_severity_type, String is_default, int expectedStatusCode, String expectedErrorCode,
			ExtentTest test) {
		Map<String, Object> logTimeRange = null;
		if (null != time_range) {
			logTimeRange = jp.composeLogTimeRangeInfo(type, time_range);
		}
		Map<String, Object> logFilterInfo = jp.composeLogFilterInfo(filter_name, logTimeRange, job_type,
				log_severity_type, is_default, test);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!logFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.log_filter.toString(),
					filter_name,"none", "none",
					(boolean)logFilterInfo.get("is_default"),(HashMap<String, Object>) logFilterInfo);
		}
		test.log(LogStatus.INFO, "Call the Rest API to update log filters");
		Response response = spogServer.updateFilterById(log4SPOGInvoker.getToken(), filter_id, "none", filter_info, "", test);
		/*Response response = log4SPOGInvoker.updateLogFilterForLoggedInUser(filter_id, logFilterInfo);*/
		response.then().statusCode(expectedStatusCode);
		spogServer.checkErrorCode(response, expectedErrorCode);
	}

	/**
	 * update log filter and check body items
	 * 
	 * @author shan.jing
	 * @param user_id:
	 *            UUID
	 * @param expected_organization_id:
	 *            user's organization id;
	 * @param filter_name:
	 *            String
	 * @param time_range:
	 *            like ["2018-01-02 12:03:30","2018-01-22 12:40:40"], the first
	 *            item is start_time and the second item is end_time
	 * @param job_type
	 * @param log_severity_type:
	 *            like "error", "warning","information"
	 * @param is_default:
	 *            "true" or "false"
	 * @param test
	 * @return
	 */
	public void updateLogFilterwithCheck(String user_id, String filter_id, String expected_organization_id,
			String filter_name, String type, long[] time_range, String job_type, String log_severity_type,
			String is_default, ExtentTest test) {
		Map<String, Object> logTimeRange = null;
		if (null != time_range) {
			logTimeRange = jp.composeLogTimeRangeInfo(type, time_range);
		}
		Map<String, Object> logFilterInfo = jp.composeLogFilterInfo(filter_name, logTimeRange, job_type,
				log_severity_type, is_default, test);
	/*	Response response = log4SPOGInvoker.updateLogFilter(user_id, filter_id, logFilterInfo);*/
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!logFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.log_filter.toString(),
					filter_name,user_id, "none",
					(boolean)logFilterInfo.get("is_default"),(HashMap<String, Object>) logFilterInfo);
		}
		test.log(LogStatus.INFO, "Call the Rest API to update log filters");
		Response response = spogServer.updateFilterById(log4SPOGInvoker.getToken(), filter_id, user_id, filter_info, "", test);
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		response.then().body("data.filter_name", equalTo(filter_name));
		String responseOrganizationID = response.then().extract().path("data.organization_id");
		spogServer.assertResponseItem(expected_organization_id, responseOrganizationID);

		ArrayList<String> response_job_type = response.then().extract().path("data.job_type");
		if (job_type != null && job_type.equalsIgnoreCase("")) {
			job_type = "emptyarray";
		}
		spogServer.assertFilterItem(job_type, response_job_type);

		ArrayList<String> response_log_severity_type = response.then().extract().path("data.log_severity_type");
		if (log_severity_type != null && log_severity_type.equalsIgnoreCase("")) {
			log_severity_type = "emptyarray";
		}
		spogServer.assertFilterItem(log_severity_type, response_log_severity_type);

		String responseLogSourceType = response.then().extract().path("data.is_default").toString();
		spogServer.assertResponseItem(is_default, responseLogSourceType);

		HashMap<String, String> responseMessageData = response.then().extract().path("data.log_ts");
		if (null == time_range || (time_range.toString() == "" || time_range.length == 0)) {
			assertNull(responseMessageData, "compare " + time_range + " passed once we don't set it");
		} else {
			String log_ts_start = response.then().extract().path("data.log_ts.start_ts").toString();
			String log_ts_end = response.then().extract().path("data.log_ts.end_ts").toString();
			String log_ts_type = response.then().extract().path("data.log_ts.type").toString();
			if (Long.parseLong(log_ts_start) != time_range[0]) {
				assertTrue("compare message_data " + time_range[0] + "failed", false);
			}
			if (Long.parseLong(log_ts_end) != time_range[1]) {
				assertTrue("compare message_data " + time_range[1] + "failed", false);
			}
			if (!log_ts_type.equalsIgnoreCase(type)) {
				assertTrue("compare message_data for type in log_ts failed", false);
			}
		}
	}

	public void updateLogFilterwithCheck(String user_id, String filter_id, String expected_organization_id,
			String filter_name, String type, long[] time_range, String job_type, String log_severity_type,
			String is_default, String message_id, String source_id, String message, String source_name, String origin,
			ExtentTest test) {
		Map<String, Object> logTimeRange = null;
		if (null != time_range) {
			logTimeRange = jp.composeLogTimeRangeInfo(type, time_range);
		}
		Map<String, Object> logFilterInfo = jp.composeLogFilterInfo(filter_name, logTimeRange, job_type,
				log_severity_type, is_default, message_id, source_id, message, source_name, origin, test);
		/*Response response = log4SPOGInvoker.updateLogFilter(user_id, filter_id, logFilterInfo);*/
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!logFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.log_filter.toString(),
					filter_name,user_id, "none",
					(boolean)logFilterInfo.get("is_default"),(HashMap<String, Object>) logFilterInfo);
		}
		test.log(LogStatus.INFO, "Call the Rest API to update log filters");
		Response response = spogServer.updateFilterById(log4SPOGInvoker.getToken(), filter_id, user_id, filter_info, "", test);
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		response.then().body("data.filter_name", equalTo(filter_name));
		String responseOrganizationID = response.then().extract().path("data.organization_id");
		spogServer.assertResponseItem(expected_organization_id, responseOrganizationID);

		ArrayList<String> response_job_type = response.then().extract().path("data.job_type");
		if (job_type != null && job_type.equalsIgnoreCase("")) {
			job_type = "emptyarray";
		}
		spogServer.assertFilterItem(job_type, response_job_type);

		ArrayList<String> response_log_severity_type = response.then().extract().path("data.log_severity_type");
		if (log_severity_type != null && log_severity_type.equalsIgnoreCase("")) {
			log_severity_type = "emptyarray";
		}
		spogServer.assertFilterItem(log_severity_type, response_log_severity_type);

		ArrayList<String> responseMessageId = response.then().extract().path("data.message_id");
		if (message_id != null && message_id.equalsIgnoreCase("")) {
			message_id = "emptyarray";
		}
		spogServer.assertFilterItem(message_id, responseMessageId);
		ArrayList<String> responseSourceId = response.then().extract().path("data.source_id");
		if (source_id != null && source_id.equalsIgnoreCase("")) {
			source_id = "emptyarray";
			assertEquals(responseSourceId.size(), 1, "it return value has one item.");
			assertEquals(responseSourceId.get(0), "");
		} else {
			spogServer.assertFilterItem(source_id, responseSourceId);
		}

		String responseMessage = response.then().extract().path("data.message");
		spogServer.assertResponseItem(message, responseMessage);
		String responseSourceName = response.then().extract().path("data.source_name");
		spogServer.assertResponseItem(source_name, responseSourceName);

		String responseOrigin = response.then().extract().path("data.origin");
		spogServer.assertResponseItem(origin, responseOrigin);

		String responseLogSourceType = response.then().extract().path("data.is_default").toString();
		spogServer.assertResponseItem(is_default, responseLogSourceType);

		HashMap<String, String> responseMessageData = response.then().extract().path("data.log_ts");
		if (null == time_range || (time_range.toString() == "" || time_range.length == 0)) {
			assertNull(responseMessageData, "compare " + time_range + " passed once we don't set it");
		} else {
			String log_ts_start = response.then().extract().path("data.log_ts.start_ts").toString();
			String log_ts_end = response.then().extract().path("data.log_ts.end_ts").toString();
			String log_ts_type = response.then().extract().path("data.log_ts.type").toString();
			if (Long.parseLong(log_ts_start) != time_range[0]) {
				assertTrue("compare message_data " + time_range[0] + "failed", false);
			}
			if (Long.parseLong(log_ts_end) != time_range[1]) {
				assertTrue("compare message_data " + time_range[1] + "failed", false);
			}
			if (!log_ts_type.equalsIgnoreCase(type)) {
				assertTrue("compare message_data for type in log_ts failed", false);
			}
		}

	}

	public Response updateLogFilter(String user_id, String filter_id, String expected_organization_id,
			String filter_name, String type, long[] time_range, String job_type, String log_severity_type,
			String is_default, String message_id, String source_id, String message, String source_name, String origin,
			ExtentTest test) {
		Map<String, Object> logTimeRange = null;
		if (null != time_range) {
			logTimeRange = jp.composeLogTimeRangeInfo(type, time_range);
		}
		Map<String, Object> logFilterInfo = jp.composeLogFilterInfo(filter_name, logTimeRange, job_type,
				log_severity_type, is_default, message_id, source_id, message, source_name, origin, test);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!logFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.log_filter.toString(),
					filter_name,user_id, "none",
					(boolean)logFilterInfo.get("is_default"),(HashMap<String, Object>) logFilterInfo);
		}
		test.log(LogStatus.INFO, "Call the Rest API to update log filters");
		Response response = spogServer.updateFilterById(log4SPOGInvoker.getToken(), filter_id, user_id, filter_info, "", test);
		/*Response response = log4SPOGInvoker.updateLogFilter(user_id, filter_id, logFilterInfo);*/
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		response.then().body("data.filter_name", equalTo(filter_name));
		String responseOrganizationID = response.then().extract().path("data.organization_id");
		spogServer.assertResponseItem(expected_organization_id, responseOrganizationID);

		ArrayList<String> response_job_type = response.then().extract().path("data.job_type");
		if (job_type != null && job_type.equalsIgnoreCase("")) {
			job_type = "emptyarray";
		}
		spogServer.assertFilterItem(job_type, response_job_type);

		ArrayList<String> response_log_severity_type = response.then().extract().path("data.log_severity_type");
		if (log_severity_type != null && log_severity_type.equalsIgnoreCase("")) {
			log_severity_type = "emptyarray";
		}
		spogServer.assertFilterItem(log_severity_type, response_log_severity_type);

		ArrayList<String> responseMessageId = response.then().extract().path("data.message_id");
		if (message_id != null && message_id.equalsIgnoreCase("")) {
			message_id = "emptyarray";
		}
		spogServer.assertFilterItem(message_id, responseMessageId);
		ArrayList<String> responseSourceId = response.then().extract().path("data.source_id");
		if (source_id != null && source_id.equalsIgnoreCase("")) {
			source_id = "emptyarray";
			assertEquals(responseSourceId.size(), 1, "it return value has one item.");
			assertEquals(responseSourceId.get(0), "");
		} else {
			spogServer.assertFilterItem(source_id, responseSourceId);
		}

		String responseMessage = response.then().extract().path("data.message");
		spogServer.assertResponseItem(message, responseMessage);
		String responseSourceName = response.then().extract().path("data.source_name");
		spogServer.assertResponseItem(source_name, responseSourceName);

		String responseOrigin = response.then().extract().path("data.origin");
		spogServer.assertResponseItem(origin, responseOrigin);

		String responseLogSourceType = response.then().extract().path("data.is_default").toString();
		spogServer.assertResponseItem(is_default, responseLogSourceType);

		HashMap<String, String> responseMessageData = response.then().extract().path("data.log_ts");
		if (null == time_range || (time_range.toString() == "" || time_range.length == 0)) {
			assertNull(responseMessageData, "compare " + time_range + " passed once we don't set it");
		} else {
			String log_ts_start = response.then().extract().path("data.log_ts.start_ts").toString();
			String log_ts_end = response.then().extract().path("data.log_ts.end_ts").toString();
			String log_ts_type = response.then().extract().path("data.log_ts.type").toString();
			if (Long.parseLong(log_ts_start) != time_range[0]) {
				assertTrue("compare message_data " + time_range[0] + "failed", false);
			}
			if (Long.parseLong(log_ts_end) != time_range[1]) {
				assertTrue("compare message_data " + time_range[1] + "failed", false);
			}
			if (!log_ts_type.equalsIgnoreCase(type)) {
				assertTrue("compare message_data for type in log_ts failed", false);
			}
		}
		return response;

	}

	/**
	 * update log filter and check body items
	 * 
	 * @author shan.jing
	 * @param user_id:
	 *            UUID
	 * @param expected_organization_id:
	 *            user's organization id;
	 * @param filter_name:
	 *            String
	 * @param time_range:
	 *            like ["2018-01-02 12:03:30","2018-01-22 12:40:40"], the first
	 *            item is start_time and the second item is end_time
	 * @param job_type
	 * @param log_severity_type:
	 *            like "error", "warning","information"
	 * @param is_default:
	 *            "true" or "false"
	 * @param test
	 * @return
	 */
	public void updateLogFilterForLoggedInUserwithCheck(String user_id, String filter_id,
			String expected_organization_id, String filter_name, String type, long[] time_range, String job_type,
			String log_severity_type, String is_default, ExtentTest test) {
		Map<String, Object> logTimeRange = null;
		if (null != time_range) {
			logTimeRange = jp.composeLogTimeRangeInfo(type, time_range);
		}
		Map<String, Object> logFilterInfo = jp.composeLogFilterInfo(filter_name, logTimeRange, job_type,
				log_severity_type, is_default, test);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!logFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.log_filter.toString(),
					filter_name,"none", "none",
					(boolean)logFilterInfo.get("is_default"),(HashMap<String, Object>) logFilterInfo);
		}
		test.log(LogStatus.INFO, "Call the Rest API to update log filters");
		Response response = spogServer.updateFilterById(log4SPOGInvoker.getToken(), filter_id, "none", filter_info, "", test);
	/*	Response response = log4SPOGInvoker.updateLogFilterForLoggedInUser(filter_id, logFilterInfo);*/
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		response.then().body("data.filter_name", equalTo(filter_name));
		String responseOrganizationID = response.then().extract().path("data.organization_id");
		spogServer.assertResponseItem(expected_organization_id, responseOrganizationID);

		ArrayList<String> response_job_type = response.then().extract().path("data.job_type");
		if (job_type != null && job_type.equalsIgnoreCase("")) {
			job_type = "emptyarray";
		}
		spogServer.assertFilterItem(job_type, response_job_type);

		ArrayList<String> response_log_severity_type = response.then().extract().path("data.log_severity_type");
		if (log_severity_type != null && log_severity_type.equalsIgnoreCase("")) {
			log_severity_type = "emptyarray";
		}
		spogServer.assertFilterItem(log_severity_type, response_log_severity_type);

		String responseLogSourceType = response.then().extract().path("data.is_default").toString();
		spogServer.assertResponseItem(is_default, responseLogSourceType);

		HashMap<String, String> responseMessageData = response.then().extract().path("data.log_ts");
		if (null == time_range || (time_range.toString() == "" || time_range.length == 0)) {
			assertNull(responseMessageData, "compare " + time_range + " passed once we don't set it");
		} else {
			String log_ts_start = response.then().extract().path("data.log_ts.start_ts").toString();
			String log_ts_end = response.then().extract().path("data.log_ts.end_ts").toString();
			String log_ts_type = response.then().extract().path("data.log_ts.type").toString();
			if (Long.parseLong(log_ts_start) != time_range[0]) {
				assertTrue("compare message_data " + time_range[0] + "failed", false);
			}
			if (Long.parseLong(log_ts_end) != time_range[1]) {
				assertTrue("compare message_data " + time_range[1] + "failed", false);
			}
			if (!log_ts_type.equalsIgnoreCase(type)) {
				assertTrue("compare message_data for type in log_ts failed", false);
			}
		}
	}

	public void updateLogFilterForLoggedInUserwithCheck(String user_id, String filter_id,
			String expected_organization_id, String filter_name, String type, long[] time_range, String job_type,
			String log_severity_type, String is_default, String message_id, String source_id, String message,
			String source_name, String origin, ExtentTest test) {
		Map<String, Object> logTimeRange = null;
		if (null != time_range) {
			logTimeRange = jp.composeLogTimeRangeInfo(type, time_range);
		}
		Map<String, Object> logFilterInfo = jp.composeLogFilterInfo(filter_name, logTimeRange, job_type,
				log_severity_type, is_default, message_id, source_id, message, source_name, origin, test);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!logFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.log_filter.toString(),
					filter_name,"none", "none",
					(boolean)logFilterInfo.get("is_default"),(HashMap<String, Object>) logFilterInfo);
		}
		test.log(LogStatus.INFO, "Call the Rest API to update log filters");
		Response response = spogServer.updateFilterById(log4SPOGInvoker.getToken(), filter_id, "none", filter_info, "", test);
	/*	Response response = log4SPOGInvoker.updateLogFilterForLoggedInUser(filter_id, logFilterInfo);*/
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		response.then().body("data.filter_name", equalTo(filter_name));
		String responseOrganizationID = response.then().extract().path("data.organization_id");
		spogServer.assertResponseItem(expected_organization_id, responseOrganizationID);

		ArrayList<String> response_job_type = response.then().extract().path("data.job_type");
		if (job_type != null && job_type.equalsIgnoreCase("")) {
			job_type = "emptyarray";
		}
		spogServer.assertFilterItem(job_type, response_job_type);

		ArrayList<String> response_log_severity_type = response.then().extract().path("data.log_severity_type");
		if (log_severity_type != null && log_severity_type.equalsIgnoreCase("")) {
			log_severity_type = "emptyarray";
		}
		spogServer.assertFilterItem(log_severity_type, response_log_severity_type);

		String responseLogSourceType = response.then().extract().path("data.is_default").toString();
		spogServer.assertResponseItem(is_default, responseLogSourceType);

		ArrayList<String> responseMessageId = response.then().extract().path("data.message_id");
		if (message_id != null && message_id.equalsIgnoreCase("")) {
			message_id = "emptyarray";
		}
		spogServer.assertFilterItem(message_id, responseMessageId);
		ArrayList<String> responseSourceId = response.then().extract().path("data.source_id");
		if (source_id != null && source_id.equalsIgnoreCase("")) {
			source_id = "emptyarray";
			assertEquals(responseSourceId.size(), 1, "it return value has one item.");
			assertEquals(responseSourceId.get(0), "");
		} else {
			spogServer.assertFilterItem(source_id, responseSourceId);
		}

		String responseMessage = response.then().extract().path("data.message");
		spogServer.assertResponseItem(message, responseMessage);
		String responseSourceName = response.then().extract().path("data.source_name");
		spogServer.assertResponseItem(source_name, responseSourceName);

		String responseOrigin = response.then().extract().path("data.origin");
		spogServer.assertResponseItem(origin, responseOrigin);

		HashMap<String, String> responseMessageData = response.then().extract().path("data.log_ts");
		if (null == time_range || (time_range.toString() == "" || time_range.length == 0)) {
			assertNull(responseMessageData, "compare " + time_range + " passed once we don't set it");
		} else {
			String log_ts_start = response.then().extract().path("data.log_ts.start_ts").toString();
			String log_ts_end = response.then().extract().path("data.log_ts.end_ts").toString();
			String log_ts_type = response.then().extract().path("data.log_ts.type").toString();
			if (Long.parseLong(log_ts_start) != time_range[0]) {
				assertTrue("compare message_data " + time_range[0] + "failed", false);
			}
			if (Long.parseLong(log_ts_end) != time_range[1]) {
				assertTrue("compare message_data " + time_range[1] + "failed", false);
			}
			if (!log_ts_type.equalsIgnoreCase(type)) {
				assertTrue("compare message_data for type in log_ts failed", false);
			}
		}
	}

	/**
	 * @author Bharadwaj.Ghadiam Call the Invoker class to invoke the rest API
	 * @param source_id
	 * @param adminToken
	 * @param test
	 * @return response
	 */
	public Response getSourceIdLogs(String source_id, String adminToken, String additionalURL, ExtentTest test) {
		// TODO Auto-generated method stub
		spogServer.setUUID(source_id);
		test.log(LogStatus.INFO, "calling the api get source Id by Logs");
		Response response = log4SPOGInvoker.getSourceIdLogs(source_id, adminToken, additionalURL, test);
		return response;
	}

	/**
	 * @author bharadwajReddy Check the response for getCloudAccounts
	 * @param response
	 * @param expectedStatusCode
	 * @param DestinationsInfo
	 * @param curr_page
	 * @param page_size
	 * @param FilterStr
	 * @param SortStr
	 * @param Info
	 * @param api
	 * @param test
	 */
	@SuppressWarnings("null")
	public void checkGetSourcesIdLogs(Response response, int expectedStatusCode,
			ArrayList<HashMap<String, Object>> LogsInfo, int curr_page, int page_size, String FilterStr, String SortStr,
			SpogMessageCode Info, ExtentTest test) {
		// TODO Auto-generated method stub
		@SuppressWarnings("unused")
		int total_page = 0, return_size = 0, total_size = 0;
		if (curr_page == 0 || curr_page == -1) {
			curr_page = 1;
		}
		if (page_size == 0 || page_size == -1) {
			page_size = 20;
		} else if (page_size >= 100 && page_size < SpogConstants.MAX_PAGE_SIZE) {
			page_size = 100;
		}

		ArrayList<HashMap<String, Object>> logs = new ArrayList<HashMap<String, Object>>();
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			logs = response.then().extract().path("data");
			return_size = logs.size();
			// get The total Size for the pages
			total_size = LogsInfo.size();
			test.log(LogStatus.INFO, "The actual total size is " + total_size);
			errorHandle.printInfoMessageInDebugFile("expect status code is " + expectedStatusCode);
			spogServer.checkResponseStatus(response, expectedStatusCode);
		}
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE && LogsInfo.size() >= logs.size()) {
			test.log(LogStatus.INFO, "check response For the Logs ");

			if (((SortStr == null) || (SortStr.equals(""))) && ((FilterStr == null) || (FilterStr.equals("")))) {

				// If we Don't mention any Sorting the default sorting is
				// descending order
				// validating the response for Destinations
				int j = LogsInfo.size() - 1;
				if (curr_page != 1) {
					j = (logs.size() - 1) - (curr_page - 1) * page_size;
				}
				for (int i = 0; i < logs.size() && j >= 0; i++, j--) {
					HashMap<String, Object> actual_logs_data = logs.get(i), expected_logs_data = LogsInfo.get(j);
					// validate the response for the Logs
					checkForLogsData(actual_logs_data, expected_logs_data, test);
				}

			}
			// SortStr
			else if ( // For sorting only and no filtering but includes
					// pagination
					(((SortStr != null) || (!SortStr.equals("")))
							&& (SortStr.contains("log_ts") || SortStr.contains("log_severity_type"))
							&& ((FilterStr == null) || (FilterStr.equals(""))))// For
					// Sorting
					// and
					// Filtering
					// both
					|| (((SortStr != null) || (!SortStr.equals(""))) && SortStr.contains("log_ts")
							&& ((FilterStr != null) || (!FilterStr.equals(""))))) {
				// validating the response for all the users who are sorted
				// based on response in ascending
				// order and descending order
				if (SortStr.contains("asc")) {
					int j = 0;
					if (curr_page != 1) {
						j = (curr_page - 1) * page_size;
					}

					for (int i = 0; i < logs.size(); i++, j++) {
						HashMap<String, Object> actual_logs_data = logs.get(i), expected_logs_data = LogsInfo.get(j);

						if ((SortStr.contains("log_severity_type")) && !SortStr.contains("log_ts")) {
							checkForLogsData(actual_logs_data, expected_logs_data, test);
						} else {
							// validate the response for the Logs
							checkForLogsData(actual_logs_data, expected_logs_data, test);
						}
					}
				} else if (SortStr.contains("desc") || (FilterStr.contains(">")) || (FilterStr.contains("<"))) {
					int j = 0;
					if (page_size == 20) {
						j = logs.size() - 1;
					} else {
						j = LogsInfo.size() - 1;
						if (curr_page != 1) {
							j = (logs.size() - 1) - (curr_page - 1) * page_size;
						}
					}
					for (int i = 0; i < logs.size() && j >= 0; i++, j--) {
						// validate the response for the Logs
						if ((SortStr.contains("log_severity_type") || SortStr.contains("job_type"))) {
							HashMap<String, Object> actual_logs_data = logs.get(i),
									expected_logs_data = LogsInfo.get(i);
							checkForLogsData(actual_logs_data, expected_logs_data, test);
						} else {
							HashMap<String, Object> actual_logs_data = logs.get(i),
									expected_logs_data = LogsInfo.get(j);
							// validate the response for the Logs
							checkForLogsData(actual_logs_data, expected_logs_data, test);
						}

					}
				}
			} // Only filtering and no sorting (For now supports only for the
			// organization id )
			else if (((SortStr == null) || (SortStr.equals(""))) && ((FilterStr != null) || (!FilterStr.equals("")))) {
				int j = 0;
				if (page_size == 20) {
					j = logs.size() - 1;
				} else {
					j = LogsInfo.size() - 1;
					if (curr_page != 1) {
						j = (logs.size() - 1) - (curr_page - 1) * page_size;
					}
				}
				for (int i = 0; i < logs.size() && j >= 0; i++, j--) {
					HashMap<String, Object> actual_logs_data = logs.get(i), expected_logs_data = LogsInfo.get(j);
					// validate the response for the Logs
					checkForLogsData(actual_logs_data, expected_logs_data, test);
				}
			}
			// check the response validation for pages,page_size,total_size
			spogServer.validateResponseForPages(curr_page, page_size, response, total_size, test);
		} else {
			String expectedErrorMessage = "", expectedErrorCode = "";

			if (Info.getStatus() != "0010000") {
				expectedErrorMessage = Info.getStatus();
				if (expectedErrorMessage.contains("{0}")) {
					expectedErrorMessage = expectedErrorMessage.replace("{0}", spogServer.getUUId());
					System.out.println(expectedErrorMessage);
				}
				expectedErrorCode = Info.getCodeString();
			}
			spogServer.checkErrorMessage(response, expectedErrorMessage);
			test.log(LogStatus.PASS, "The value of the message is " + expectedErrorMessage);
			spogServer.checkErrorCode(response, expectedErrorCode);
			test.log(LogStatus.PASS, "The value of the code  generated  is " + expectedErrorCode);
			test.log(LogStatus.INFO, "The value of the  response generated actually is :" + response.getStatusCode());
		}
	}

	/**
	 * @author Bharadwaj.Ghadiam Validating the response for the log_filters
	 * @param response
	 * @param expectedStatusCode
	 * @param logfilters
	 * @param curr_page
	 * @param page_size
	 * @param filterStr
	 * @param sortStr
	 * @param Info
	 * @param test
	 */
	public void getlogFiltersLoginUserWithCheck(String additionalURL,
			ArrayList<HashMap<String, Object>> expected_response, int expectedstatuscode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		ArrayList<HashMap<String, Object>> actual_response = new ArrayList<>();
		HashMap<String, Object> data_response = new HashMap<>();

		Response response = spogServer.getFilters(log4SPOGInvoker.getToken(), "none", filterType.log_filter.toString(), additionalURL, test);
		/*Response response = log4SPOGInvoker.getLogFiltersForLoggedInUser(additionalURL, test);*/
		spogServer.checkResponseStatus(response, expectedstatuscode);
		test.log(LogStatus.PASS, "The response status matched and the status code is " + expectedstatuscode);
		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			actual_response = response.then().extract().path("data");
			actual_response = sort(actual_response);
			for (int i = 0; i < expected_response.size(); i++) {
				data_response = actual_response.get(i);
				System.out.println(data_response);

				if(data_response.get("filter_id").toString().contains(expected_response.get(i).get("filter_id").toString())) {
					checkLogFiltersInfo(data_response, expected_response.get(i), test);
					break;
				}else {
					i++;
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
	 * @author Bharadwaj.Ghadiam
	 * @param additionalURL
	 * @param expected_response
	 * @param expectedstatuscode
	 * @param ExpectedErrorMessage
	 * @param test
	 */
	public void getlogFiltersForLogginUser_isdefault(String additionalURL,
			ArrayList<HashMap<String, Object>> expected_response, int expectedstatuscode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		ArrayList<HashMap<String, Object>> actual_response = new ArrayList<>();
		HashMap<String, Object> data_response = new HashMap<>();
		Response response = spogServer.getFilters(log4SPOGInvoker.getToken(), "none", filterType.log_filter.toString(), additionalURL, test);
		/*Response response = log4SPOGInvoker.getLogFiltersForLoggedInUser(additionalURL, test);*/
		spogServer.checkResponseStatus(response, expectedstatuscode);
		test.log(LogStatus.PASS, "The response status matched and the status code is " + expectedstatuscode);
		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			actual_response = response.then().extract().path("data");
			actual_response = sort(actual_response);
			String[] expected_data = additionalURL.split("\\&");
			for (int i = 0; i < actual_response.size(); i++) {
				data_response = actual_response.get(i);
				if (additionalURL.contains("is_default") && (additionalURL.contains("true"))
						&& (!additionalURL.contains("filter_name"))) {
					boolean responseIsDefault = (boolean) data_response.get("is_default");
					if (!responseIsDefault) {
						continue;
					} else {
						for (int k = 0; k < expected_response.size(); k++) {
							if (data_response.get("filter_id").equals(expected_response.get(i).get("filter_id"))) {
								checklogFilters(data_response, expected_response.get(i), test);
								break;
							} else {
								continue;
							}
						}
					}
				} else if (additionalURL.contains("is_default") && (additionalURL.contains("false"))
						&& (!additionalURL.contains("filter_name"))) {
					boolean responseIsDefault = (boolean) data_response.get("is_default");
					if (responseIsDefault) {
						test.log(LogStatus.FAIL, "Still there are entries with is_default=true");
						assertTrue("Still there are entries with is_default=true", false);
					} else {
						for (int k = 0; k < expected_response.size(); k++) {
							if (data_response.get("filter_id").equals(expected_response.get(i).get("filter_id"))) {
								checklogFilters(data_response, expected_response.get(i), test);
								break;
							} else {
								continue;
							}
						}
						continue;
					}
				} else if (!additionalURL.contains("is_default") && (additionalURL.contains("filter_name"))) {
					String[] filtername = expected_data[0].split("=");

					String responseIsDefault = data_response.get("filter_name").toString();
					if (responseIsDefault.equals(filtername[1])) {

						for (int j = 0; j < expected_response.size(); j++) {
							if ((expected_response.get(j).get("filter_name").toString()).equals(filtername[1])) {
								checklogFilters(data_response, expected_response.get(j), test);
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
						checklogFilters(data_response, expected_response.get(i), test);
						for (int j = 0; j < expected_response.size(); j++) {
							if ((expected_response.get(j).get("filter_name").toString()).equals(filtername[1])) {
								checklogFilters(data_response, expected_response.get(j), test);
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
	 * @author Bharadwaj.Ghadiam Validate the data inside the logs
	 * @param actual_logs_data
	 * @param expected_logs_data
	 * @param test
	 */
	@SuppressWarnings("unchecked")
	private void checkForLogsData(HashMap<String, Object> actual_logs_data, HashMap<String, Object> expected_logs_data,
			ExtentTest test) {
		// TODO Auto-generated method stub
		HashMap<String, Object> expected_job_data, actual_job_data = new HashMap<String, Object>();
		test.log(LogStatus.INFO, "Validating the response for log_id");
		spogServer.assertResponseItem(actual_logs_data.get("log_id"), expected_logs_data.get("log_id"), test);
		test.log(LogStatus.INFO, "Validating the response for log_severity_type");
		spogServer.assertResponseItem(actual_logs_data.get("log_severity_type"),
				expected_logs_data.get("log_severity_type"), test);
		test.log(LogStatus.INFO, "Validating the response for log_source_type");
		spogServer.assertResponseItem(actual_logs_data.get("log_source_type"),
				expected_logs_data.get("log_source_type"), test);
		test.log(LogStatus.INFO, "Validating the response for site_id");
		spogServer.assertResponseItem(actual_logs_data.get("site_id"), expected_logs_data.get("site_id"), test);
		test.log(LogStatus.INFO, "Validating the response for organization_id");
		spogServer.assertResponseItem(actual_logs_data.get("organization_id"),
				expected_logs_data.get("organization.organization_id"), test);

		// validation for the job_id
		expected_job_data = (HashMap<String, Object>) expected_logs_data.get("job_data");
		actual_job_data = (HashMap<String, Object>) actual_logs_data.get("job_data");

		test.log(LogStatus.INFO, "Validating the response for job_id");
		spogServer.assertResponseItem(actual_job_data.get("job_id"), expected_job_data.get("job_id"), test);

		test.log(LogStatus.INFO, "Validating the response for rps_id");
		// spogServer.assertResponseItem(actual_job_data.get("rps_id"),expected_job_data.get("rps_id"),test);
		assertNull(actual_job_data.get("rps_id"));

		test.log(LogStatus.INFO, "validating the resposne for server_id");

		test.log(LogStatus.INFO, "Validating the response for source_id");
		spogServer.assertResponseItem(actual_job_data.get("source_id"), expected_job_data.get("source_id"), test);

		test.log(LogStatus.INFO, "Validating the response for source_name");
		spogServer.assertResponseItem(actual_job_data.get("source_name"), expected_job_data.get("source_name"), test);

		test.log(LogStatus.INFO, "Validating the response for job_type");
		spogServer.assertResponseItem(actual_job_data.get("job_type"), expected_job_data.get("job_type"), test);

	}

	/**
	 * get specified log filter by specified user id
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
	 * @return void
	 */
	public void getspecifiedLogFilterByUserIDwithCheck(String userID, String filterID, String token,
			HashMap<String, Object> expected_response, int expectedstatuscode, SpogMessageCode ExpectedErrorMessage,
			ExtentTest test) {
		HashMap<String, Object> actual_response = new HashMap<String, Object>();
		Response response = spogServer.getFiltersById(log4SPOGInvoker.getToken(), filterID, filterType.log_filter.toString(), userID, "none", test);
		/*Response response = log4SPOGInvoker.getspecifiedLogFilterforspecifieduserID(userID, filterID, token);*/
		spogServer.checkResponseStatus(response, expectedstatuscode);
		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			actual_response = response.then().extract().path("data");
			checklogFilters(actual_response, expected_response, test);
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			if (code.contains("00A00302")) {
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
	 * get specified log filter for logged in user
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
	public void getspecifiedLogFilterForLoggedInUser(String filter_Id, String token,
			HashMap<String, Object> expected_response, int expectedstatuscode, SpogMessageCode ExpectedErrorMessage,
			ExtentTest test) {
		HashMap<String, Object> actual_response = new HashMap<>();
		int actual_size = expected_response.size();
		/*Response response = log4SPOGInvoker.getspecifiedLogFilterForLoggedInUser(filter_Id, token);*/
		Response response = spogServer.getFiltersById(log4SPOGInvoker.getToken(), filter_Id, filterType.log_filter.toString(),"none", "none", test);

		test.log(LogStatus.INFO, "Check the response status");
		spogServer.checkResponseStatus(response, expectedstatuscode);
		test.log(LogStatus.PASS, "The response status matched and the status code is " + expectedstatuscode);
		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			// place holder for validating the response
			actual_response = response.then().extract().path("data");
			checklogFilters(actual_response, expected_response, test);
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			if (code.contains("00A00302")) {
				Response response1 = spogServer.getLoggedInUser(token, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
				String expected_user_Id = response1.then().extract().path("data.user_id");
				message = message.replace("{0}", filter_Id);
				message = message.replace("{1}", expected_user_Id);
			}
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.INFO, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		}
	}

	/**
	 * get log filters for user and return response
	 * 
	 * @author Kiran.Sripada
	 * @param userID
	 * @param test
	 * @return
	 */
	public void getlogFiltersForUserwithCheck(String userID, String token,
			ArrayList<HashMap<String, Object>> expectedresponse, int expectedstatuscode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		ArrayList<HashMap<String, Object>> actual_response = new ArrayList<>();
		HashMap<String, Object> data_response = new HashMap<>();
		int expSize =expectedresponse.size();
		System.out.println(expSize);
		Response response = spogServer.getFilters(token, userID, filterType.log_filter.toString(), "", test);
	/*	Response response = log4SPOGInvoker.getLogFiltersForUser(userID, token);*/
		spogServer.checkResponseStatus(response, expectedstatuscode);
		test.log(LogStatus.PASS, "The response status matched and the status code is " + expectedstatuscode);
		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			actual_response = response.then().extract().path("data");
			//actual_response = sort(actual_response);
			for (int i = 0; i < expectedresponse.size(); i++) {
				data_response = actual_response.get(i);
				System.out.println(data_response);

				if(data_response.get("filter_id").toString().contains(expectedresponse.get(i).get("filter_id").toString())) {
					checkLogFiltersInfo(data_response, expectedresponse.get(i), test);
					break;
				}else {
					i++;
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
	 * get log filters for user when apply filter on is_default and return
	 * response
	 * 
	 * @author Kiran.Sripada
	 * @param userID
	 * @param token
	 * @param additionalURL
	 *            ?is_direct=true or ?is_direct=false or
	 *            ?filter_name=nameoffilter or
	 *            ?is_default=true,filter_name=nameoffilter
	 * @param expectedresponse
	 *            arraylist of expectedfiltervalues
	 * @param statuscode
	 * @param errormessage
	 *            , for success case, pass the value as null
	 * @test object of ExtentTest, this is to print to the html file
	 * @return
	 */
	public void getlogFiltersForUserwithCheck_isdefault(String userID, String token, String additionalURL,
			ArrayList<HashMap<String, Object>> expected_response, int expectedstatuscode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		ArrayList<HashMap<String, Object>> actual_response = new ArrayList<>();
		HashMap<String, Object> data_response = new HashMap<>();

		/*Response response = log4SPOGInvoker.getLogFiltersForUser(userID, token, additionalURL);*/
		Response response = spogServer.getFilters(token, userID, filterType.log_filter_global.toString(), additionalURL, test);
		spogServer.checkResponseStatus(response, expectedstatuscode);
		test.log(LogStatus.PASS, "The response status matched and the status code is " + expectedstatuscode);
		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			actual_response = response.then().extract().path("data");
			actual_response = sort(actual_response);
			String[] expected_data = additionalURL.split("\\&");
			for (int i = 0; i < actual_response.size(); i++) {
				data_response = actual_response.get(i);
				if (additionalURL.contains("is_default") && (additionalURL.contains("true"))
						&& (!additionalURL.contains("filter_name"))) {
					boolean responseIsDefault = (boolean) data_response.get("is_default");
					if (!responseIsDefault) {
						continue;
					} else {
						for (int k = 0; k < expected_response.size(); k++) {
							if (data_response.get("filter_id").equals(expected_response.get(i).get("filter_id"))) {
								checklogFilters(data_response, expected_response.get(i), test);
								break;
							} else {
								continue;
							}
						}
					}
				} else if (additionalURL.contains("is_default") && (additionalURL.contains("false"))
						&& (!additionalURL.contains("filter_name"))) {
					boolean responseIsDefault = (boolean) data_response.get("is_default");
					if (responseIsDefault) {
						test.log(LogStatus.FAIL, "Still there are entries with is_default=true");
						assertTrue("Still there are entries with is_default=true", false);
					} else {
						for (int k = 0; k < expected_response.size(); k++) {
							if (data_response.get("filter_id").equals(expected_response.get(i).get("filter_id"))) {
								checklogFilters(data_response, expected_response.get(i), test);
								break;
							} else {
								continue;
							}
						}
						continue;
					}
				} else if (!additionalURL.contains("is_default") && (additionalURL.contains("filter_name"))) {
					String[] filtername = expected_data[0].split("=");

					String responseIsDefault = data_response.get("filter_name").toString();
					if (responseIsDefault.equals(filtername[1])) {

						for (int j = 0; j < expected_response.size(); j++) {
							if ((expected_response.get(j).get("filter_name").toString()).equals(filtername[1])) {
								checklogFilters(data_response, expected_response.get(j), test);
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
						checklogFilters(data_response, expected_response.get(i), test);
						for (int j = 0; j < expected_response.size(); j++) {
							if ((expected_response.get(j).get("filter_name").toString()).equals(filtername[1])) {
								checklogFilters(data_response, expected_response.get(j), test);
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
	 * Delete log filter by ID
	 * 
	 * @author Kiran.Sripada
	 * @param userID
	 * @param filterID
	 * @param token
	 * @param expectedstatuscode
	 * @param expectederrormessage
	 * @param test
	 */
	public void deleteLogFilterByFilterID(String userID, String filterID, String token, int expectedstatuscode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		/*Response response = log4SPOGInvoker.deleteLogFilterByFilterID(userID, filterID, token);*/
		Response response = spogServer.deleteFiltersByID(token, filterID, userID, test);
		spogServer.checkResponseStatus(response, expectedstatuscode);
		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.PASS, "The job filter is successfully deleted");
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			if (code.contains("00A00302")) {
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
	 * Delete log filter by ID
	 * 
	 * @author Kiran.Sripada
	 * @param userID
	 * @param filterID
	 * @param token
	 * @param expectedstatuscode
	 * @param expectederrormessage
	 * @param test
	 */
	public void deleteLogFilterforloggedinUser(String filterID, String token, int expectedstatuscode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		/*Response response = log4SPOGInvoker.deleteLogFilterforloggedinuser(filterID, token);*/
		Response response = spogServer.deleteFiltersByID(token, filterID, "none", test);
		spogServer.checkResponseStatus(response, expectedstatuscode);
		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.PASS, "The job filter is successfully deleted");
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();

			if (code.contains("00A00302")) {
				Response response1 = spogServer.getLoggedInUser(token, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
				String response_user_id = response1.then().extract().path("data.user_id");
				message = message.replace("{0}", filterID);
				message = message.replace("{1}", response_user_id);

			}
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		}
	}

	/**
	 * get system log filters
	 * 
	 * @author Kiran.Sripada
	 * @param token
	 * @param expectedstatuscode
	 * @param expectederrormessage
	 * @param test
	 */

	public void getSystemLogFilters(String token, int expectedStatusCode, SpogMessageCode ExpectedErrorMessage,
			ExtentTest test) {
		ArrayList<HashMap<String, Object>> actual_response = new ArrayList<>();
		Response response=spogServer.getFilters(token, filterType.log_filter_global.toString());
		/*Response response = log4SPOGInvoker.getSystemLogFilters(token);*/
		spogServer.checkResponseStatus(response, expectedStatusCode, test);
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.INFO, "validating the response");
			actual_response = response.then().extract().path("data");

			test.log(LogStatus.INFO, "Check the log_severiy_type");
			ArrayList<String> log_status = (ArrayList<String>) actual_response.get(0).get("log_severity_type");
			spogServer.assertResponseItem(String.join(",", log_status), "error_warning", test);

			test.log(LogStatus.INFO, "Check the filter name");
			spogServer.assertResponseItem(actual_response.get(0).get("filter_name"), "warning and Error", test);

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
	 * @author Nagamalleswari.Sykam
	 * @param actual_response
	 * @param expected_response
	 * @param test
	 */

	public void checkLogFiltersInfo(HashMap<String, Object> actual_response,HashMap<String, Object> expected_response,
			ExtentTest test) {
		System.out.println(expected_response.size());
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

		test.log(LogStatus.INFO, "Compare the log severity");
		if (expected_response.get("log_severity") == null || expected_response.get("log_severity") == "none") {
			test.log(LogStatus.PASS, "filter not applied on job status");
			assertNull(null, " filter not applied on job status");
		} else {
			ArrayList<String> log_status = (ArrayList<String>) actual_response.get("log_severity_type");
			spogServer.assertResponseItem(String.join(",", log_status),
					expected_response.get("log_severity").toString(), test);
		}
	}




	public void checklogFilters(HashMap<String, Object> actual_response, HashMap<String, Object> expected_response,
			ExtentTest test) {

		test.log(LogStatus.INFO, "Compare the filter id");

		spogServer.assertResponseItem(actual_response.get("filter_id").toString(),
				expected_response.get("filter_id").toString(), test);

		/*test.log(LogStatus.INFO, "Compare the count");
		spogServer.assertResponseItem(actual_response.get("count").toString(),
				expected_response.get("count").toString(), test);
*/
		test.log(LogStatus.INFO, "Compare the filter name");
		spogServer.assertResponseItem(actual_response.get("filter_name").toString(),
				expected_response.get("filter_name").toString(), test);

		test.log(LogStatus.INFO, "Compare the user id");
		spogServer.assertResponseItem(actual_response.get("user_id").toString(),
				expected_response.get("user_id").toString(), test);

		test.log(LogStatus.INFO, "Compare the organization id");
		spogServer.assertResponseItem(actual_response.get("organization_id").toString(),
				expected_response.get("organization_id").toString(), test);

		// test.log(LogStatus.INFO,"Compare the source_id");
		// spogServer.assertResponseItem(actual_response.get("source_id").toString(),expected_response.get("source_id").toString(),test);

		test.log(LogStatus.INFO, "Compare the message_id");
		if (expected_response.get("Message_ID") == null || expected_response.get("Message_ID") == "none") {
			test.log(LogStatus.PASS, "filter not applied on job status");
			assertNull(null, " filter not applied on job status");
		} else {
			ArrayList<String> log_status = (ArrayList<String>) actual_response.get("message_id");
			spogServer.assertResponseItem(String.join(",", log_status), expected_response.get("Message_ID").toString(),
					test);
		}

		if (expected_response.get("source_name") == null || expected_response.get("source_name") == "none") {
			test.log(LogStatus.PASS, "filter not applied on job status");
			assertNull(null, " filter not applied on job status");
		} else {
			spogServer.assertResponseItem(actual_response.get("source_name").toString(),
					expected_response.get("source_name").toString(), test);
		}

		String origin = (String) expected_response.get("Origin");
		if (origin == null || origin == "none") {
			test.log(LogStatus.PASS, "filter not applied on job status");
			assertNull(null, " filter not applied on job status");
		} else {
			spogServer.assertResponseItem(actual_response.get("origin").toString(),
					expected_response.get("Origin").toString(), test);

		}
		test.log(LogStatus.INFO, "Compare the log severity");
		if (expected_response.get("log_severity") == null || expected_response.get("log_severity") == "none") {
			test.log(LogStatus.PASS, "filter not applied on job status");
			assertNull(null, " filter not applied on job status");
		} else {
			ArrayList<String> log_status = (ArrayList<String>) actual_response.get("log_severity_type");
			spogServer.assertResponseItem(String.join(",", log_status),
					expected_response.get("log_severity").toString(), test);
		}

		if (expected_response.get("Message") == null || expected_response.get("Message") == "none") {
			test.log(LogStatus.PASS, "filter not applied on job status");
			assertNull(null, " filter not applied on job status");
		} else {
			spogServer.assertResponseItem(actual_response.get("message").toString(),
					expected_response.get("Message").toString(), test);
		}

		test.log(LogStatus.INFO, "Compare the job type");
		if (expected_response.get("job_type") == null || expected_response.get("job_type") == "none") {
			test.log(LogStatus.PASS, "filter not applied on job type");
			assertNull(null, " filter not applied on job type");
		} else {
			ArrayList<String> job_type = (ArrayList<String>) actual_response.get("job_type");
			spogServer.assertResponseItem(String.join(",", job_type), expected_response.get("job_type").toString(),
					test);
		}

		HashMap<String, String> responseMessageData = (HashMap<String, String>) actual_response.get("log_ts");

		HashMap<String, Object> expectedtimerange = (HashMap<String, Object>) expected_response.get("start_time_ts");
		test.log(LogStatus.INFO, "Compare the time range");
		if (responseMessageData == null) {
			test.log(LogStatus.PASS, "filter not applied on time range");
			assertNull(null, " filter not applied on time range");
		} else {

			String log_ts_start = null;
			String log_ts_end = null;
			log_ts_start = responseMessageData.get("start");
			log_ts_end = responseMessageData.get("end");
			Long log_ts_start_expected = (long) expectedtimerange.get("start");
			spogServer.assertResponseItem(log_ts_start, log_ts_start_expected.toString(), test);

		}

	}

	/*
	 * public void checklogFilters(HashMap<String,Object> actual_response,
	 * HashMap<String,Object> expected_response, ExtentTest test) {
	 * 
	 * test.log(LogStatus.INFO, "Compare the filter id");
	 * 
	 * spogServer.assertResponseItem(actual_response.get("filter_id").toString()
	 * ,expected_response.get("filter_id").toString(),test);
	 * 
	 * test.log(LogStatus.INFO,"Compare the filter name");
	 * spogServer.assertResponseItem(actual_response.get("filter_name").toString
	 * (),expected_response.get("filter_name").toString(),test);
	 * 
	 * test.log(LogStatus.INFO,"Compare the user id");
	 * spogServer.assertResponseItem(actual_response.get("user_id").toString(),
	 * expected_response.get("user_id").toString(),test);
	 * 
	 * test.log(LogStatus.INFO,"Compare the organization id");
	 * spogServer.assertResponseItem(actual_response.get("organization_id").
	 * toString(),expected_response.get("organization_id").toString(),test);
	 * 
	 * test.log(LogStatus.INFO,"Compare the log severity");
	 * if(expected_response.get("log_severity_type")==null||
	 * expected_response.get("log_severity")=="none") { test.log(LogStatus.PASS,
	 * "filter not applied on job status"); assertNull(null,
	 * " filter not applied on job status"); }else { ArrayList<String>
	 * log_status = (ArrayList<String>)
	 * actual_response.get("log_severity_type");
	 * spogServer.assertResponseItem(String.join(",", log_status),
	 * expected_response.get("log_severity").toString(),test); }
	 * 
	 * test.log(LogStatus.INFO,"Compare the job type");
	 * if(expected_response.get("job_type")==null||
	 * expected_response.get("job_type")=="none") { test.log(LogStatus.PASS,
	 * "filter not applied on job type"); assertNull(null,
	 * " filter not applied on job type"); }else { ArrayList<String> job_type =
	 * (ArrayList<String>) actual_response.get("job_type");
	 * spogServer.assertResponseItem(String.join(",", job_type),
	 * expected_response.get("job_type").toString(),test); }
	 * 
	 * HashMap<String, String> responseMessageData = (HashMap<String, String>)
	 * actual_response.get("log_ts");
	 * 
	 * HashMap<String, String> expectedtimerange = (HashMap<String, String>)
	 * expected_response.get("start_time_ts"); test.log(LogStatus.INFO,
	 * "Compare the time range");
	 * if(expectedtimerange.get("start")==null||expectedtimerange.get("end")==
	 * null) { test.log(LogStatus.PASS, "filter not applied on time range");
	 * assertNull(null," filter not applied on time range"); }else {
	 * 
	 * String log_ts_start=null; String log_ts_end=null;
	 * log_ts_start=responseMessageData.get("start");
	 * log_ts_end=responseMessageData.get("end"); if
	 * (!log_ts_start.equalsIgnoreCase(expectedtimerange.get("start"))) {
	 * test.log(LogStatus.FAIL, "compare start time " +
	 * expectedtimerange.get("start") + "failed"); assertTrue(
	 * "compare start time " + expectedtimerange.get("start") + "failed",
	 * false); } if (!log_ts_end.equalsIgnoreCase(expectedtimerange.get("end")))
	 * { test.log(LogStatus.FAIL, "compare end time " +
	 * expectedtimerange.get("end") + "failed"); assertTrue("compare end time "
	 * + expectedtimerange.get("end") + "failed", false); } }
	 * test.log(LogStatus.INFO,"Compare the count");
	 * spogServer.assertResponseItem(actual_response.get("count").toString(),"0"
	 * ,test);
	 * 
	 * }
	 */
	public HashMap<String, Object> composeExpectedLogFilter(String filter_id, String filter_name, String user_id,
			String organization_id, String job_type, String log_severity, long startTimeTSStart, long startTimeTSEnd,
			String endTimeTSStart, String endTimeTSEnd, String isDefault, int count) {

		HashMap<String, Object> expected_response = new HashMap<>();
		expected_response.put("filter_id", filter_id);
		expected_response.put("filter_name", filter_name);
		expected_response.put("user_id", user_id);
		expected_response.put("organization_id", organization_id);
		expected_response.put("job_type", job_type);
		expected_response.put("log_severity", log_severity);
		expected_response.put("count", count);

		HashMap<String, Object> startTimeTSInfo = new HashMap<String, Object>();
		if ((startTimeTSStart) > 0) {
			startTimeTSInfo.put("start", startTimeTSStart);
		}
		if (startTimeTSEnd > 0) {
			startTimeTSInfo.put("end", startTimeTSEnd);
		}
		if (startTimeTSInfo.size() != 0) {
			expected_response.put("start_time_ts", startTimeTSInfo);
		}

		/*
		 * HashMap<String, String> endTimeTSInfo = new HashMap<String,
		 * String>(); if (!"none".equalsIgnoreCase(endTimeTSStart)) {
		 * endTimeTSInfo.put("start", endTimeTSStart); } if
		 * (!"none".equalsIgnoreCase(endTimeTSEnd)) { endTimeTSInfo.put("end",
		 * endTimeTSEnd); } if (startTimeTSInfo.size()!=0) {
		 * expected_response.put("end_time_ts", endTimeTSInfo); }
		 */
		expected_response.put("is_default", Boolean.valueOf(isDefault));

		return expected_response;
	}

	public HashMap<String, Object> composeExpectedLogFilter(String filter_id, String filter_name, String user_id,
			String organization_id, String job_type, String log_severity, String startTimeTSStart,
			String startTimeTSEnd, String endTimeTSStart, String endTimeTSEnd, String isDefault) {

		HashMap<String, Object> expected_response = new HashMap<>();
		expected_response.put("filter_id", filter_id);
		expected_response.put("filter_name", filter_name);
		expected_response.put("user_id", user_id);
		expected_response.put("organization_id", organization_id);
		expected_response.put("job_type", job_type);
		expected_response.put("log_severity", log_severity);

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

		/*
		 * HashMap<String, String> endTimeTSInfo = new HashMap<String,
		 * String>(); if (!"none".equalsIgnoreCase(endTimeTSStart)) {
		 * endTimeTSInfo.put("start", endTimeTSStart); } if
		 * (!"none".equalsIgnoreCase(endTimeTSEnd)) { endTimeTSInfo.put("end",
		 * endTimeTSEnd); } if (startTimeTSInfo.size()!=0) {
		 * expected_response.put("end_time_ts", endTimeTSInfo); }
		 */
		expected_response.put("is_default", Boolean.valueOf(isDefault));
		return expected_response;
	}

	public HashMap<String, Object> composeExpectedLogFilter(String filter_id, String filter_name, String user_id,
			String organization_id, String job_type, String log_severity, String Message_ID, String source_id,
			String Message, String source_name, String Origin, long startTimeTSStart, long startTimeTSEnd,
			String endTimeTSStart, String endTimeTSEnd, String isDefault, int count) {

		HashMap<String, Object> expected_response = new HashMap<>();
		expected_response.put("filter_id", filter_id);
		expected_response.put("filter_name", filter_name);
		expected_response.put("user_id", user_id);
		expected_response.put("organization_id", organization_id);
		expected_response.put("job_type", job_type);
		expected_response.put("log_severity", log_severity);
		expected_response.put("Message_ID", Message_ID);
		expected_response.put("source_id", source_id);
		expected_response.put("Message", Message);
		expected_response.put("source_name", source_name);
		expected_response.put("Origin", Origin);
		expected_response.put("count", count);

		HashMap<String, Object> startTimeTSInfo = new HashMap<String, Object>();
		if ((startTimeTSStart) > 0) {
			startTimeTSInfo.put("start", startTimeTSStart);
		}
		if (startTimeTSEnd > 0) {
			startTimeTSInfo.put("end", startTimeTSEnd);
		}
		if (startTimeTSInfo.size() != 0) {
			expected_response.put("start_time_ts", startTimeTSInfo);
		}

		/*
		 * HashMap<String, String> endTimeTSInfo = new HashMap<String,
		 * String>(); if (!"none".equalsIgnoreCase(endTimeTSStart)) {
		 * endTimeTSInfo.put("start", endTimeTSStart); } if
		 * (!"none".equalsIgnoreCase(endTimeTSEnd)) { endTimeTSInfo.put("end",
		 * endTimeTSEnd); } if (startTimeTSInfo.size()!=0) {
		 * expected_response.put("end_time_ts", endTimeTSInfo); }
		 */
		expected_response.put("is_default", Boolean.valueOf(isDefault));

		return expected_response;
	}
	/**
	 * @author Nagamalleswari.Sykam
	 * @param filter_id
	 * @param filter_name
	 * @param user_id
	 * @param organization_id
	 * @param log_severity
	 * @param Origin
	 * @param startTimeTSStart
	 * @param startTimeTSEnd
	 * @param endTimeTSStart
	 * @param endTimeTSEnd
	 * @param isDefault
	 * @param count
	 * @return
	 */

	public HashMap<String, Object> composeExpectedLogFiltersWithCheck(String filter_id, String filter_name, String user_id,
			String organization_id,  
			String log_severity,  
			long startTimeTSStart,
			long startTimeTSEnd,
			String endTimeTSStart,
			String endTimeTSEnd,
			String isDefault,
			int count) {

		HashMap<String, Object> expected_response = new HashMap<>();
		expected_response.put("filter_id", filter_id);
		expected_response.put("filter_name", filter_name);
		expected_response.put("user_id", user_id);
		expected_response.put("organization_id", organization_id);
		expected_response.put("log_severity", log_severity);


		HashMap<String, Object> startTimeTSInfo = new HashMap<String, Object>();
		if ((startTimeTSStart) > 0) {
			startTimeTSInfo.put("start", startTimeTSStart);
		}
		if (startTimeTSEnd > 0) {
			startTimeTSInfo.put("end", startTimeTSEnd);
		}
		if (startTimeTSInfo.size() != 0) {
			expected_response.put("start_time_ts", startTimeTSInfo);
		}

		/*
		 * HashMap<String, String> endTimeTSInfo = new HashMap<String,
		 * String>(); if (!"none".equalsIgnoreCase(endTimeTSStart)) {
		 * endTimeTSInfo.put("start", endTimeTSStart); } if
		 * (!"none".equalsIgnoreCase(endTimeTSEnd)) { endTimeTSInfo.put("end",
		 * endTimeTSEnd); } if (startTimeTSInfo.size()!=0) {
		 * expected_response.put("end_time_ts", endTimeTSInfo); }
		 */
		expected_response.put("is_default", Boolean.valueOf(isDefault));

		return expected_response;
	}


	public Response getLogFiltersForLoggedInUser(String additionalURL, ExtentTest test) {
		test.log(LogStatus.INFO, "Call the rest API to get the filters  for Logged in user ");
		/*Response response = log4SPOGInvoker.getLogFiltersForLoggedInUser(additionalURL, test);*/
		Response response=spogServer.getFilters(log4SPOGInvoker.getToken(), "none", filterType.log_filter.toString(), additionalURL, test);
		return response;
	}

	/**
	 * Create Log columns for specified user
	 * 
	 * @author Kiran.Sripada
	 * @param userid
	 * @param token
	 * @param List
	 *            of columns
	 * @param ExtentTest
	 */
	public Response createLogColumnsForSpecifiedUser(String user_id, String validToken,
			ArrayList<HashMap<String, Object>> expected_columns, ExtentTest test) {

		Map<String, ArrayList<HashMap<String, Object>>> logcolumnsInfo = jp.jobColumnInfo(expected_columns);
		Response response = log4SPOGInvoker.createLogColumnsForSpecifiedUser(user_id, validToken, logcolumnsInfo, test);

		return response;

	}

	/**
	 * Create Log columns for specified user
	 * 
	 * @author Kiran.Sripada
	 * @param userid
	 * @param token
	 * @param List
	 *            of columns
	 * @param ExtentTest
	 */
	public Response createLogColumnsForLoggedInUser(String validToken,
			ArrayList<HashMap<String, Object>> expected_columns, ExtentTest test) {

		Map<String, ArrayList<HashMap<String, Object>>> logcolumnsInfo = jp.jobColumnInfo(expected_columns);
		Response response = log4SPOGInvoker.createLogColumnsForLoggedInUser(validToken, logcolumnsInfo, test);

		return response;

	}

	public HashMap<String, Object> composelog_Column(String columnId, String sort, String filter, String visible,
			String orderId) {
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

		temp.put("order_id", orderId);

		return temp;

	}

	/**
	 * Compare the job columns
	 * 
	 * @author Kiran.Sripada
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

	public void CompareLogColumnsContent(Response response, ArrayList<HashMap<String, Object>> expectedColumnsContents,
			ArrayList<HashMap<String, Object>> defaultColumnsContents, int expectedstatuscode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		// TODO Auto-generated method stub
		ArrayList<HashMap<String, Object>> actualcolumnsHeadContent = new ArrayList<HashMap<String, Object>>();

		spogServer.checkResponseStatus(response, expectedstatuscode);
		if (expectedstatuscode == SpogConstants.SUCCESS_POST
				|| expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			actualcolumnsHeadContent = response.then().extract().path("data");
			int length = expectedColumnsContents.size();

			test.log(LogStatus.INFO, "Sort the actual response by order_id");
			actualcolumnsHeadContent = spogServer.sortArrayListbyInt(actualcolumnsHeadContent, "order_id");

			test.log(LogStatus.INFO, "Sort the expected response by order_id");
			expectedColumnsContents = spogServer.sortArrayListbyString(expectedColumnsContents, "order_id");

			if (expectedColumnsContents.size() == actualcolumnsHeadContent.size()) {

				for (int i = 0; i < actualcolumnsHeadContent.size(); i++) {
					for (int j = 0; j < defaultColumnsContents.size(); j++) {
						if (actualcolumnsHeadContent.get(i).get("column_id")
								.equals(defaultColumnsContents.get(j).get("column_id"))) {
							checklogcolumns(actualcolumnsHeadContent.get(i), expectedColumnsContents.get(i),
									defaultColumnsContents.get(j), test);
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
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		}

	}

	/**
	 * get logs Columns for specified user
	 * 
	 * @author Kiran.Sripada
	 * @param user
	 *            id
	 * @param token
	 * @param test
	 */
	public void getlogColumnsforspecifiedUserwithcheck(String user_Id, String token,
			ArrayList<HashMap<String, Object>> expectedresponse,
			ArrayList<HashMap<String, Object>> defaultcolumnresponse, int expectedStatusCode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		test.log(LogStatus.INFO, "Call the res API to get the log columns for specified user");
		Response response = log4SPOGInvoker.getLogColumnsForSpecificiedUser(user_Id, token);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			ArrayList<HashMap<String, Object>> actualresponse = response.then().extract().path("data");

			test.log(LogStatus.INFO, "Sort the actual response by order_id");
			actualresponse = spogServer.sortArrayListbyInt(actualresponse, "order_id");

			if (!(expectedresponse.size() == defaultcolumnresponse.size())) {
				test.log(LogStatus.INFO, "Sort the expected response by order_id");
				expectedresponse = spogServer.sortArrayListbyString(expectedresponse, "order_id");
			} else {
				if (expectedresponse.get(0).get("order_id").getClass() == String.class) {
					test.log(LogStatus.INFO, "Value is a string needs sorting based on string ");
					expectedresponse = spogServer.sortArrayListbyString(expectedresponse, "order_id");
				} else {
					test.log(LogStatus.INFO, "Value is an int needs sorting based on int");
					expectedresponse = spogServer.sortArrayListbyInt(expectedresponse, "order_id");
				}
			}

			if (expectedresponse.size() == actualresponse.size()) {

				for (int i = 0; i < actualresponse.size(); i++) {
					for (int j = 0; j < defaultcolumnresponse.size(); j++) {
						if (actualresponse.get(i).get("column_id")
								.equals(defaultcolumnresponse.get(j).get("column_id"))) {
							checklogcolumns(actualresponse.get(i), expectedresponse.get(i),
									defaultcolumnresponse.get(j), test);
							break;
						}

					}
				}

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
	 * get logs Columns for specified user
	 * 
	 * @author Kiran.Sripada
	 * @param user
	 *            id
	 * @param token
	 * @param test
	 */
	public void getlogColumnsforLoggedInUserwithcheck(String token, ArrayList<HashMap<String, Object>> expectedresponse,
			ArrayList<HashMap<String, Object>> defaultcolumnresponse, int expectedStatusCode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		test.log(LogStatus.INFO, "Call the res API to get the log columns for specified user");
		Response response = log4SPOGInvoker.getLogColumnsForLoggedInUser(token);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			ArrayList<HashMap<String, Object>> actualresponse = response.then().extract().path("data");

			test.log(LogStatus.INFO, "Sort the actual response by order_id");
			actualresponse = spogServer.sortArrayListbyInt(actualresponse, "order_id");

			if (!(expectedresponse.size() == defaultcolumnresponse.size())) {
				test.log(LogStatus.INFO, "Sort the expected response by order_id");
				expectedresponse = spogServer.sortArrayListbyString(expectedresponse, "order_id");
			} else {
				if (expectedresponse.get(0).get("order_id").getClass() == String.class) {
					test.log(LogStatus.INFO, "Value is a string needs sorting based on string ");
					expectedresponse = spogServer.sortArrayListbyString(expectedresponse, "order_id");
				} else {
					test.log(LogStatus.INFO, "Value is an int needs sorting based on int");
					expectedresponse = spogServer.sortArrayListbyInt(expectedresponse, "order_id");
				}
			}

			if (expectedresponse.size() == actualresponse.size()) {

				for (int i = 0; i < actualresponse.size(); i++) {
					for (int j = 0; j < defaultcolumnresponse.size(); j++) {
						if (actualresponse.get(i).get("column_id")
								.equals(defaultcolumnresponse.get(j).get("column_id"))) {
							checklogcolumns(actualresponse.get(i), expectedresponse.get(i),
									defaultcolumnresponse.get(j), test);
							break;
						} else {
							continue;
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
	 * Delete log columns for specified user ID
	 * 
	 * @author Kiran.Sripada
	 * @param userID
	 * @param token
	 * @param expectedstatuscode
	 * @param expectederrormessage
	 * @param test
	 */
	public void deleteLogColumnsforSpecifiedUserwithCheck(String user_Id, String token, int expectedstatuscode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		Response response = log4SPOGInvoker.deleteLogColumnsForSpecificiedUser(user_Id, token);
		spogServer.checkResponseStatus(response, expectedstatuscode);
		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.PASS, "The log columns are successfully deleted");
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			/*
			 * if(code.contains("00A00302")){ message = message.replace("{0}",
			 * filterID); message= message.replace("{1}", userID); }
			 */
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		}

	}

	/**
	 * Delete log columns for logged in user
	 * 
	 * @author Kiran.Sripada
	 * @param token
	 * @param expectedstatuscode
	 * @param expectederrormessage
	 * @param test
	 */
	public void deleteLogColumnsforLoggedInUserwithCheck(String token, int expectedstatuscode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		Response response = log4SPOGInvoker.deletetLogColumnsForLoggedInUser(token);
		spogServer.checkResponseStatus(response, expectedstatuscode);
		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.PASS, "The log columns are successfully deleted");
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			/*
			 * if(code.contains("00A00302")){ message = message.replace("{0}",
			 * filterID); message= message.replace("{1}", userID); }
			 */
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		}

	}

	public void checklogcolumns(HashMap<String, Object> actual_response, HashMap<String, Object> expected_response,
			HashMap<String, Object> defaultcolumnvalues, ExtentTest test) {
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
	 * @author Bharadwaj.Ghadiam Validate the Response for checkExportLogs
	 * @param response
	 * @param LogsInfo
	 * @param test
	 */
	/*
	 * Date utcDate=new Date((String) Keys[k]); Date
	 * jobStartTime=fromUTCTime(utcDate); String Time2=jobStartTime.toString();
	 * String Time5= (String)Keys[k]; Date utcDate1=new Date((long)
	 * log_data.get(Request.get(k))); Date jobEndTime=toUTCTime(utcDate1); Date
	 * EndTime=fromUTCTime(utcDate1); String time6=EndTime.toString(); String
	 * time4=jobEndTime.toString(); String Time =utcDate1.toString();
	 */

	@SuppressWarnings("deprecation")
	public void checkExportCsvResponse(Response response, int expectedStatusCode, String tempFileURL,
			ArrayList<HashMap<String, Object>> LogsInfo, SpogMessageCode errorInfo, ExtentTest test) {

		ArrayList<String> Request = new ArrayList<String>();
		test.log(LogStatus.INFO, "validating the response for data from the clipbord ");

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			spogServer.checkResponseStatus(response, expectedStatusCode, test);
			FileInputStream fstream = null;
			try {
				fstream = new FileInputStream(tempFileURL);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				test.log(LogStatus.INFO, "Error Message" + e.getMessage());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String strLine = null;
			// Read File Line By Line
			int count = 0, j = LogsInfo.size() - 1;
			try {
				while ((strLine = br.readLine()) != null) {
					if (count++ == 0) {
						String Keys[] = strLine.split(",");
						for (int i = 0; i < Keys.length; i++) {
							Request.add(Keys[i]);
						}
					} else {
						// validate the Response inside the body
						HashMap<String, Object> log_data = LogsInfo.get(j--);
						Object Keys[] = strLine.split(",");
						for (int k = 0; k < Request.size(); k++) {
							test.log(LogStatus.INFO, "The response for the data:" + Request.get(k));
							// validating the response for log_ts get logs txt
							if (Request.get(k).equals("log_ts")) {
								Date utcDate = new Date((String) Keys[k]);
								Date jobStartTime = fromUTCTime(utcDate);
								Date utcDate1 = new Date((long) log_data.get(Request.get(k)));
								Date EndTime = fromUTCTime(utcDate1);
								spogServer.assertResponseItem(EndTime.toString(), jobStartTime.toString(), test);
							} else if (Request.get(k).equals("message")) {
								// spogServer.assertResponseItem(log_data.get(Request.get(k)),Keys[k],
								// test);
							} else {

								HashMap<String, Object> job_data = (HashMap<String, Object>) log_data.get("job_data");

								if (Request.get(k).toString().equals("Log Id")) {
									spogServer.assertResponseItem(log_data.get("log_id"), Keys[k], test);
								} else if (Request.get(k).toString().equals("Severity")) {
									spogServer.assertResponseItem(log_data.get("log_severity_type"), Keys[k], test);
								} else if (Request.get(k).toString().equals("Job Id")) {
									spogServer.assertResponseItem(job_data.get("job_id"), Keys[k], test);
								} else if (Request.get(k).toString().equals("Job Name")) {
									spogServer.assertResponseItem(job_data.get("job_name"), Keys[k], test);
								} else if (Request.get(k).toString().equals("Job Type")) {
									assertTrue(job_data.get("job_type").toString().toLowerCase()
											.contains(Keys[k].toString().toLowerCase()));
								} else if (Request.get(k).toString().equals("Source")) {
									spogServer.assertResponseItem(job_data.get("source_name"), Keys[k], test);
								} else if (Request.get(k).toString().equals("Message ID")) {
									spogServer.assertResponseItem(log_data.get("help_message_id"), Keys[k], test);
								} else if (Request.get(k).toString().equals("Message Link")) {
									assertTrue(
											Keys[k].toString().contains(log_data.get("help_message_link").toString()));
								}
							}
						}

					}
				}
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			String expectedErrorMessage = "", expectedErrorCode = "";
			if (errorInfo.getStatus() != "0010000") {
				expectedErrorMessage = errorInfo.getStatus();
				if (expectedErrorMessage.contains("{0}")) {
					expectedErrorMessage = expectedErrorMessage.replace("{0}", spogServer.getUUId());
				}
				expectedErrorCode = errorInfo.getCodeString();
			}
			spogServer.checkErrorMessage(response, expectedErrorMessage);
			test.log(LogStatus.PASS, "The value of the message is " + expectedErrorMessage);
			spogServer.checkErrorCode(response, expectedErrorCode);
			test.log(LogStatus.PASS, "The value of the code  generated  is " + expectedErrorCode);
			test.log(LogStatus.INFO, "The value of the  response generated actually is :" + response.getStatusCode());
		}
		// Close the input stream
	}

	/**
	 * @author Bharadwaj.Ghadiam This method is used to change UTC time to local
	 *         time before return Date to UI Convert UTC time to local time
	 * @param utcDate
	 * @return LocalTime
	 */
	public Date fromUTCTime(Date utcDate) {
		if (utcDate == null) {
			return null;
		}
		LocalDateTime ldt = LocalDateTime.ofInstant(utcDate.toInstant(), ZoneOffset.systemDefault());
		return Date.from(ldt.atZone(ZoneOffset.UTC).toInstant());
	}

	/**
	 * @author Bharadwaj.Ghadiam This method is used to change local time to UTC
	 *         time when get Date from UI Convert local time to UTC time
	 * @param localDate
	 * @return UTCTime
	 */
	public Date toUTCTime(Date localDate) {
		if (localDate == null) {
			return null;
		}
		LocalDateTime ldt = LocalDateTime.ofInstant(localDate.toInstant(), ZoneOffset.UTC);
		return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
	}

	/**
	 * Export the logs to the CSV File
	 * 
	 * @author Bharadwaj.Ghadiam
	 * @param admin_Token
	 * @param test
	 * @return response
	 */
	public Response LogsexportCsv(String admin_Token, String tempFileURL, ExtentTest test) {
		// TODO Auto-generated method stub
		Response response = log4SPOGInvoker.exportCsvLogs(admin_Token, test);

		if (response.getStatusCode() == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			try {
				FileOutputStream fileOutputStream = new FileOutputStream(new File(tempFileURL));
				fileOutputStream.write(response.asByteArray());
				fileOutputStream.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return response;
	}

	/**
	 * Export the Organization Id logs To CSV File
	 * 
	 * @author Bharadwaj.Ghadiam
	 * @param admin_Token
	 * @param organization_id
	 * @param test
	 * @return
	 */
	public Response OrganizationIdLogsexportCsv(String admin_Token, String tempFileURL, String organization_id,
			ExtentTest test) {
		// TODO Auto-generated method stub
		spogServer.setUUID(organization_id);
		Response response = log4SPOGInvoker.exportCsvLogsByOrganizationId(admin_Token, organization_id, test);
		if (response.getStatusCode() == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			try {
				FileOutputStream fileOutputStream = new FileOutputStream(new File(tempFileURL));
				fileOutputStream.write(response.asByteArray());
				fileOutputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return response;
	}

	/**
	 * @author Bharadwaj.Ghadiam This method is used to update the count
	 * @param expectedresponse
	 * @param actual_Logs_data
	 * @param test
	 * @return expected Updated Response
	 */
	public ArrayList<HashMap<String, Object>> UpdateCountForFilters(ArrayList<HashMap<String, Object>> expectedResponse,
			ArrayList<HashMap<String, Object>> actual_Logs_data, ExtentTest test) {
		// TODO Auto-generated method stub
		test.log(LogStatus.INFO, "Composing the count information for the get Log Filters API");
		for (int i = 0; i < expectedResponse.size(); i++) {
			expectedResponse.get(i).put("count", composeCount1(expectedResponse.get(i), actual_Logs_data, test));
		}
		return expectedResponse;
	}

	public int composeCount1(HashMap<String, Object> logFilterData, ArrayList<HashMap<String, Object>> actual_Logs_data,
			ExtentTest test) {
		test.log(LogStatus.INFO, "validating the response");
		ArrayList<HashMap<String, Object>> actual_Logs = new ArrayList<HashMap<String, Object>>();
		actual_Logs = actual_Logs_data;
		// TODO Auto-generated method stub
		test.log(LogStatus.INFO, "Applying the Filters on the Logs");
		ArrayList<HashMap<String, Object>> temp_data = new ArrayList<HashMap<String, Object>>();
		int count = 0;
		if (logFilterData.get("log_severity") != null) {
			count = 0;
			for (int i = 0; i < actual_Logs.size(); i++) {
				HashMap<String, Object> actualLogData = actual_Logs.get(i);
				if (logFilterData.get("log_severity").equals(actualLogData.get("log_severity_type"))) {
					temp_data.add(actual_Logs.get(i));
					++count;
				} else {
					continue;
				}
			}
			actual_Logs = null;
			actual_Logs = new ArrayList<HashMap<String, Object>>();
			if (!temp_data.isEmpty()) {
				actual_Logs.addAll(temp_data);
			}
			temp_data = null;
		}

		if (logFilterData.get("job_type") != null) {
			count = 0;
			temp_data = new ArrayList<HashMap<String, Object>>();
			if (!actual_Logs.isEmpty()) {
				for (int i = 0; i < actual_Logs.size(); i++) {
					@SuppressWarnings("unchecked")
					HashMap<String, Object> job_data = (HashMap<String, Object>) actual_Logs.get(i).get("job_data");
					if (logFilterData.get("job_type").equals(job_data.get("job_type"))) {
						temp_data.add(actual_Logs.get(i));
						++count;
					} else {
						continue;
					}
				}
			} else {
				return count;
			}
			actual_Logs = null;
			actual_Logs = new ArrayList<HashMap<String, Object>>();
			if (!temp_data.isEmpty()) {
				actual_Logs.addAll(temp_data);
			}
			temp_data = null;
		}

		if (logFilterData.get("Origin") != null) {
			count = 0;
			temp_data = new ArrayList<HashMap<String, Object>>();
			if (!actual_Logs.isEmpty()) {
				for (int i = 0; i < actual_Logs.size(); i++) {
					HashMap<String, Object> actualLogData = actual_Logs.get(i);
					if (logFilterData.get("Origin").equals(actualLogData.get("generated_from"))) {
						temp_data.add(actual_Logs.get(i));
						++count;
					} else {
						continue;
					}
				}
			} else {
				return count;
			}
			actual_Logs = new ArrayList<HashMap<String, Object>>();
			if (!temp_data.isEmpty()) {
				actual_Logs.addAll(temp_data);
			}
			temp_data = null;
		}
		if (logFilterData.get("Message") != null) {
			count = 0;
			temp_data = new ArrayList<HashMap<String, Object>>();
			if (!actual_Logs.isEmpty()) {
				for (int i = 0; i < actual_Logs.size(); i++) {
					HashMap<String, Object> actualLogData = actual_Logs.get(i);
					String value = "(.*)" + "(" + (String) logFilterData.get("Message") + ")" + "(.*)";
					String search_name1 = (String) actualLogData.get("message");

					try {
						final Pattern pattern = Pattern.compile(value);
						Matcher match = pattern.matcher(search_name1);
						if (match.matches()) {
							temp_data.add(actual_Logs.get(i));
							++count;
						} else {
							continue;
						}
					} catch (Exception e) {
						test.log(LogStatus.ERROR, "The value of the message :" + e.getMessage());
					}
				}
			} else {
				return count;
			}
			actual_Logs = new ArrayList<HashMap<String, Object>>();
			if (!temp_data.isEmpty()) {
				actual_Logs.addAll(temp_data);
			}
			temp_data = null;
		}

		if (logFilterData.get("Message_ID") != null) {
			count = 0;
			temp_data = new ArrayList<HashMap<String, Object>>();

			if (!actual_Logs.isEmpty()) {
				for (int i = 0; i < actual_Logs.size(); i++) {
					HashMap<String, Object> actualLogData = actual_Logs.get(i);
					if (logFilterData.get("Message_ID").equals(actualLogData.get("message_id"))) {
						temp_data.add(actual_Logs.get(i));
						++count;
					} else {
						continue;
					}
				}
			} else {
				return count;
			}
			actual_Logs = null;
			actual_Logs = new ArrayList<HashMap<String, Object>>();
			if (!temp_data.isEmpty()) {
				actual_Logs.addAll(temp_data);
			}
			temp_data = null;
		}
		if (logFilterData.get("source_name") != null) {
			count = 0;
			temp_data = new ArrayList<HashMap<String, Object>>();
			if (!actual_Logs.isEmpty()) {
				for (int i = 0; i < actual_Logs.size(); i++) {
					@SuppressWarnings("unchecked")
					HashMap<String, Object> actualLogData = actual_Logs.get(i);
					HashMap<String, Object> job_data = (HashMap<String, Object>) actual_Logs.get(i).get("job_data");
					if (logFilterData.get("source_name").equals(job_data.get("source_name"))) {
						temp_data.add(actual_Logs.get(i));
						++count;
					} else {
						continue;
					}
				}
			} else {
				return count;
			}
			actual_Logs = null;
			actual_Logs = new ArrayList<HashMap<String, Object>>();
			if (!temp_data.isEmpty()) {
				actual_Logs.addAll(temp_data);
			}
			temp_data = null;
		}
		return count;
	}

	/**
	 * @author Bharadwaj.Ghadiam This method is used to compose the count
	 *         information(The no of the matched logs related to the applied
	 *         Filters )
	 * @param logFilterData
	 * @param actual_Logs_Data
	 * @param test
	 * @return count (Logs Matched )
	 */
	public int composeCount(HashMap<String, Object> logFilterData, ArrayList<HashMap<String, Object>> actual_Logs_Data,
			ExtentTest test) {
		// TODO Auto-generated method stub
		test.log(LogStatus.INFO, "Applying the Filters on the Logs");
		int count = 0;
		for (int i = 0; i < actual_Logs_Data.size(); i++) {
			HashMap<String, Object> actualLogData = actual_Logs_Data.get(i);
			@SuppressWarnings("unchecked")
			HashMap<String, Object> job_data = (HashMap<String, Object>) actual_Logs_Data.get(i).get("job_data");
			if (logFilterData.get("log_severity") != null && logFilterData.get("job_type") != null) {
				if (logFilterData.get("job_type").equals(job_data.get("job_type"))
						&& logFilterData.get("log_severity").equals(actualLogData.get("log_severity_type"))) {
					++count;
				} else {
					continue;
				}
			} else if (logFilterData.get("log_severity") != null && logFilterData.get("job_type") == null) {
				if (logFilterData.get("log_severity").equals(actualLogData.get("log_severity_type"))) {
					++count;
				} else {
					continue;
				}
			} else if (logFilterData.get("log_severity") == null && logFilterData.get("job_type") != null) {

				if (logFilterData.get("job_type").equals(job_data.get("job_type"))) {
					++count;
				} else {
					continue;
				}
			}
		}
		return count;
	}

	/**
	 * Create Log columns for specified user with check
	 * 
	 * @author Kiran.Sripada
	 * @param userid
	 * @param token
	 * @param List
	 *            of columns
	 * @param status
	 *            code
	 * @param expectederrrmessage
	 * @param ExtentTest
	 */

	public void CreateLogColumnsForSpecifiedUserwithcheck(String user_id, String token,
			ArrayList<HashMap<String, Object>> expected_columns, int expectedstatuscode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		Map<String, ArrayList<HashMap<String, Object>>> logcolumnsInfo = jp.jobColumnInfo(expected_columns);
		Response response = log4SPOGInvoker.createLogColumnsForSpecifiedUser(user_id, token, logcolumnsInfo, test);
		if (expectedstatuscode == SpogConstants.SUCCESS_POST) {
			test.log(LogStatus.PASS, "The log columns are successfully updated");
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			if (code.contains("00D00003") || code.contains("00D00004")) {

				String Column_id = expected_columns.get(0).get("column_id").toString();
				message = message.replace("{0}", Column_id);

			}
			if (code.contains("00D00005")) {

				String order_id = expected_columns.get(0).get("order_id").toString();
				message = message.replace("{0}", order_id);

			}
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		}

	}

	/**
	 * Create Log columns for logged in user with check
	 * 
	 * @author Kiran.Sripada
	 * @param userid
	 * @param token
	 * @param List
	 *            of columns
	 * @param status
	 *            code
	 * @param expectederrrmessage
	 * @param ExtentTest
	 */

	public void createLogColumnsForLoggedInUserwithcheck(String token,
			ArrayList<HashMap<String, Object>> expected_columns, int expectedstatuscode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		Map<String, ArrayList<HashMap<String, Object>>> logcolumnsInfo = jp.jobColumnInfo(expected_columns);
		Response response = log4SPOGInvoker.createLogColumnsForLoggedInUser(token, logcolumnsInfo, test);
		if (expectedstatuscode == SpogConstants.SUCCESS_POST) {
			test.log(LogStatus.PASS, "The log columns are successfully updated");
		} else {

			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			if (code.contains("00D00003") || code.contains("00D00004")) {

				String Column_id = expected_columns.get(0).get("column_id").toString();
				message = message.replace("{0}", Column_id);

			}
			if (code.contains("00D00005")) {

				String order_id = expected_columns.get(0).get("order_id").toString();
				message = message.replace("{0}", order_id);

			}

			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		}

	}

	/**
	 * Update Log columns for specified user with check
	 * 
	 * @author Kiran.Sripada
	 * @param userid
	 * @param token
	 * @param List
	 *            of columns
	 * @param ExtentTest
	 */
	public Response updateLogColumnsForSpecifiedUser(String user_id, String validToken,
			ArrayList<HashMap<String, Object>> expected_columns, ExtentTest test) {

		Map<String, ArrayList<HashMap<String, Object>>> logcolumnsInfo = jp.jobColumnInfo(expected_columns);
		Response response = log4SPOGInvoker.updateLogColumnsForSpecifiedUser(user_id, validToken, logcolumnsInfo, test);

		return response;

	}

	/**
	 * Update Log columns for specified in user with check
	 * 
	 * @author Kiran.Sripada
	 * @param userid
	 * @param token
	 * @param List
	 *            of columns
	 * @param status
	 *            code
	 * @param expectederrrmessage
	 * @param ExtentTest
	 */

	public void updateLogColumnsForSpecifiedUserwithusercheck(String user_id, String token,
			ArrayList<HashMap<String, Object>> expected_columns, int expectedstatuscode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		Map<String, ArrayList<HashMap<String, Object>>> logcolumnsInfo = jp.jobColumnInfo(expected_columns);
		Response response = log4SPOGInvoker.updateLogColumnsForSpecifiedUser(user_id, token, logcolumnsInfo, test);
		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.PASS, "The log columns are successfully updated");
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			if (code.contains("00D00003") || code.contains("00D00004")) {

				String Column_id = expected_columns.get(0).get("column_id").toString();
				message = message.replace("{0}", Column_id);

			}
			if (code.contains("00D00005")) {

				String order_id = expected_columns.get(0).get("order_id").toString();
				message = message.replace("{0}", order_id);

			}
			if (code.contains("00D00006")) {

				String order_id = expected_columns.get(0).get("order_id").toString();
				message = message.replace("{7}", order_id);

			}

			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		}

	}

	/**
	 * Update Log columns for logged in user with check
	 * 
	 * @author Kiran.Sripada
	 * @param userid
	 * @param token
	 * @param List
	 *            of columns
	 * @param ExtentTest
	 */
	public Response updateLogColumnsForloggedinUser(String validToken,
			ArrayList<HashMap<String, Object>> expected_columns, ExtentTest test) {

		Map<String, ArrayList<HashMap<String, Object>>> logcolumnsInfo = jp.jobColumnInfo(expected_columns);
		Response response = log4SPOGInvoker.updateLogColumnsForloggedinUser(validToken, logcolumnsInfo, test);

		return response;

	}

	/**
	 * Update Log columns for logged in user with check
	 * 
	 * @author Kiran.Sripada
	 * @param userid
	 * @param token
	 * @param List
	 *            of columns
	 * @param status
	 *            code
	 * @param expectederrrmessage
	 * @param ExtentTest
	 */

	public void updateLogColumnsForLoggedInUserwithusercheck(String token,
			ArrayList<HashMap<String, Object>> expected_columns, int expectedstatuscode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		Map<String, ArrayList<HashMap<String, Object>>> logcolumnsInfo = jp.jobColumnInfo(expected_columns);
		Response response = log4SPOGInvoker.updateLogColumnsForloggedinUser(token, logcolumnsInfo, test);
		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.PASS, "The log columns are successfully updated");
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			if (code.contains("00D00003") || code.contains("00D00004")) {

				String Column_id = expected_columns.get(0).get("column_id").toString();
				message = message.replace("{0}", Column_id);

			}
			if (code.contains("00D00005")) {

				String order_id = expected_columns.get(0).get("order_id").toString();
				message = message.replace("{0}", order_id);

			}

			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		}

	}

	public ArrayList<HashMap<String, Object>> sort(ArrayList<HashMap<String, Object>> expectedresponse1) {
		// TODO Auto-generated method stub

		System.out.println("The value of the resposne is ");
		Collections.sort(expectedresponse1, new Comparator<HashMap<String, Object>>() {
			@Override
			public int compare(HashMap<String, Object> o1, HashMap<String, Object> o2) {
				// TODO Auto-generated method stub
				String create_ts = (String) o1.get("filter_name");
				String create_ts1 = (String) o2.get("filter_name");
				if (create_ts.compareTo(create_ts1) > 0)
					return 1;
				else if (create_ts.compareTo(create_ts1) == 0) {
					return 0;
				} else
					return -1;

			}
		});
		return expectedresponse1;
	}

	/**
	 * createLogFilterForLoggedInUserwithCheck_savedsearch
	 * 
	 * this method is used to create log filters with return type response
	 * 
	 * @author Ramya.Nagepalli
	 * @param expected_organization_id
	 * @param filter_name
	 * @param type
	 * @param time_range
	 * @param job_type
	 * @param log_severity_type
	 * @param is_default
	 * @param test
	 * @return
	 */
	public Response createLogFilterForLoggedInUserwithCheck_savedsearch(String expected_organization_id,
			String filter_name, String type, long[] time_range, String job_type, String log_severity_type,
			String is_default, ExtentTest test) {
		Map<String, Object> logTimeRange = null;
		if (null != time_range) {
			logTimeRange = jp.composeLogTimeRangeInfo(type, time_range);
		}
		Map<String, Object> logFilterInfo = jp.composeLogFilterInfo(expected_organization_id, filter_name, logTimeRange,
				job_type, log_severity_type, is_default, test);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!logFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.log_filter.toString(),
					filter_name, "none", "none",
					(boolean)logFilterInfo.get("is_default"),(HashMap<String, Object>) logFilterInfo);
		}
		test.log(LogStatus.INFO, "Call the Rest API to create log filters");
		Response response = spogServer.createFilters(log4SPOGInvoker.getToken(), filter_info, "", test);
	/*	Response response = log4SPOGInvoker.createLogFilterForLoggedInUser(logFilterInfo);*/
		response.then().statusCode(SpogConstants.SUCCESS_POST);
		return response;

	}
	/**
	 * updateLogFilterForLoggedInUserwithCheck
	 * 
	 * @author Ramya.Nagepalli
	 * @param additionalURL
	 * @param user_id
	 * @param filter_id
	 * @param expected_organization_id
	 * @param filter_name
	 * @param type
	 * @param time_range
	 * @param job_type
	 * @param log_severity_type
	 * @param is_default
	 * @param test
	 * @return
	 */
	public Response updateLogFilterForLoggedInUserwithCheck(String additionalURL,String user_id, String filter_id,String expected_organization_id,
			String filter_name, String type,long[] time_range, String job_type,
			String log_severity_type, String is_default, ExtentTest test) {
		Map<String, Object> logTimeRange = null;
		if (null != time_range) {
			logTimeRange = jp.composeLogTimeRangeInfo(type,time_range);
		}
		Map<String, Object> logFilterInfo =
				jp.composeLogFilterInfo(filter_name, logTimeRange, job_type,log_severity_type, is_default,test);
		Response response = log4SPOGInvoker.updateLogFilterForLoggedInUser(additionalURL,filter_id, logFilterInfo);
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		response.then().body("data.filter_name", equalTo(filter_name));
		return response;
	}

}
