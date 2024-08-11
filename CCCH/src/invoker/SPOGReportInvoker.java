package invoker;

import static io.restassured.RestAssured.given;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.seleniumhq.jetty9.server.UserIdentity;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class SPOGReportInvoker {
	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	public SPOGReportInvoker(String baseURI, String port) {
		RestAssured.baseURI = baseURI;
		RestAssured.port = Integer.valueOf(port);
		RestAssured.basePath = "/api";
	}
	
	/* Invoke REST API get REPORT LIST FILTERS
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param test
	 * @return response
	 */
	public Response getReportListFilters(String token, ExtentTest test) {
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get("/reportlistfilters");

		response.then().log().all();
		return response;
	}
	
	/* Invoke REST API get USER REPORT LIST FILTERS FOR LOGGED IN USER
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param test
	 * @return response
	 */
	public Response getReportListFiltersForLoggedInUser(String token, String additionalURL, ExtentTest test) {

		String requestUrl = "/user/reportlistfilters";
		if((additionalURL != null) && (!additionalURL.equals(""))) {
			requestUrl += "?" + additionalURL;
		}
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get(requestUrl);

		response.then().log().all();
		return response;
	}
	
	/* Invoke REST API get USER REPORT LIST FILTERS FOR LOGGED IN USER BY FILTER ID
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param test
	 * @return response
	 */
	public Response getReportListFiltersForLoggedInUserByFilterId(String token, String filter_id, ExtentTest test) {

		String requestURL = "/user/reportlistfilters/"+filter_id;
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get(requestURL);
		return response;
	}
	
	/* Invoke REST API get USERS REPORT LIST FILTERS FOR SPECIFIED USER
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param user_id
	 * @param test
	 * @return response
	 */
	public Response getReportListFilteresForSpecifiedUser(String token, String user_id,String additionalURL, ExtentTest test) {

		String requestURL = "/users/"+user_id+"/reportlistfilters";
	
		if((additionalURL != null) && (!additionalURL.equals(""))) {
			requestURL += "?" + additionalURL;
		}
		
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get(requestURL);

		response.then().log().all();
		return response;
	}
	
	/* Invoke REST API get USERS REPORT LIST FILTERS FOR SPECIFIED USER BY FILTER ID
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param user_id
	 * @param filter_id
	 * @param test
	 * @return response
	 */
	public Response getReportListFilteresForSpecifiedUserByFilterId(String token, String user_id, String filter_id, ExtentTest test) {

		String requestURL = "/users/"+user_id+"/reportlistfilters/"+filter_id;
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get(requestURL);

		response.then().log().all();
		return response;
	}
	
	/* Invoke REST API create USER REPORT LIST FILTERS FOR LOGGED IN USER
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param reportlistfiltersInfo
	 * @param test
	 * @return response
	 */
	public Response createReportListFiltersForLoggedInUser(String token, HashMap<String, Object> reportlistfiltersInfo, ExtentTest test) {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(reportlistfiltersInfo).when()
				.post("/user/reportlistfilters");

		response.then().log().all();
		return response;
	}
	
	/* Invoke REST API create USER REPORT LIST FILTERS FOR SPECIFIED USER
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param user_id
	 * @param reportlistfiltersInfo
	 * @param test
	 * @return response
	 */
	public Response createReportListFiltersForSpecifiedUser(String token,String user_id, HashMap<String, Object> reportlistfiltersInfo, ExtentTest test) {

		String requestURL = "/users/"+user_id+"/reportlistfilters/";
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(reportlistfiltersInfo).when()
				.post(requestURL);

		response.then().log().all();
		return response;
	}
	
	/* Invoke REST API delete USER REPORT LIST FILTERS FOR LOGGEDIN USER BY FILTER ID
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param filter_id
	 * @param test
	 * @return response
	 */
	public Response deleteReportListFiltersForLoggedInUserByFilterId(String token, String filter_id, ExtentTest test) {
		
		String requestURL = "/user/reportlistfilters/"+filter_id;
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.delete(requestURL);

		response.then().log().all();
		return response;
	}
	
	/* Invoke REST API delete USER REPORT LIST FILTERS FOR SPECIFIED USER BY FILTER ID
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param user_id
	 * @param filter_id
	 * @param test
	 * @return response
	 */
	public Response deleteReportListFiltersForSpecifiedUserByFilterId(String token, String user_id,String filter_id, ExtentTest test) {
		
		String requestURL = "/users/"+user_id+"/reportlistfilters/"+filter_id;
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.delete(requestURL);

		response.then().log().all();
		return response;
	}
	
	/* Invoke REST API update USER REPORT LIST FILTERS FOR LOGGED IN USER
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param filter_id
	 * @param test
	 * @return response
	 */
	public Response updateReportListFiltersForLoggedInUser(String token, String filter_id, HashMap<String, Object> updateReportListFiltersInfo, ExtentTest test) {
		
		String requestURL = "/user/reportlistfilters/"+filter_id;
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(updateReportListFiltersInfo).when()
				.put(requestURL);

		response.then().log().all();
		return response;
	}
	
	/* Invoke REST API update USER REPORT LIST FILTERS FOR SPECIFIED USER
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param user_id
	 * @param filter_id
	 * @param test
	 * @return response
	 */
	public Response updateReportListFiltersForSpecifiedUser(String token, String user_id, String filter_id, HashMap<String, Object> updateReportListFiltersInfo, ExtentTest test) {
		
		String requestURL = "/users/"+user_id+"/reportlistfilters/"+filter_id;
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(updateReportListFiltersInfo).when()
				.put(requestURL);

		response.then().log().all();
		return response;
	}

	/**
	 * 
	 * Call REST Web service to getBackupReportsDetails
	 * 
	 * @author ramya.nagepalli
	 * @param token
	 * @param test
	 * @return
	 */
	public Response getBackupReportsDetails(String token,String additionalURL, ExtentTest test) {
		// TODO Auto-generated method stub
		
		String requestUrl = "/backupjobreports/details";
		if((additionalURL != null) && (!additionalURL.equals(""))) {
			requestUrl += "?" + additionalURL;
		}
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get(requestUrl);

		response.then().log().all();
		return response;
	}

	/**
	 * 
	 * Call REST Web service to getRestoreJobReportsDetails
	 * 
	 * @author ramya.nagepalli
	 * @param token
	 * @param test
	 * @return
	 */
	public Response getRestoreJobReportsDetails(String token,String additionalURL, ExtentTest test) {
		// TODO Auto-generated method stub
		
		String requestUrl = "/restorejobreports/details";
		if((additionalURL != null) && (!additionalURL.equals(""))) {
			requestUrl += "?" + additionalURL;
		}
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get(requestUrl);

		response.then().log().all();
		return response;
	}

	
	/**
	 * 
	 * Call REST Web service to getBackupJobReportsDetailsStatusSummary
	 * 
	 * @author ramya.nagepalli
	 * @param token
	 * @param additionalURL
	 * @param test
	 * @return
	 */
	public Response getBackupJobReportsDetailsStatusSummary(String token, String additionalURL,ExtentTest test) {
		// TODO Auto-generated method stub

		String requestUrl = "/backupjobreports/jobstatussummary";
		if((additionalURL != null) && (!additionalURL.equals(""))) {
			requestUrl += "?" + additionalURL;
		}
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get(requestUrl);

		response.then().log().all();
		return response;
	}
	
	/*
	 * Call REST API GET: /restorejobreports/columns
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param test
	 * @return response
	 */
	public Response getSystemRestoreJobReportsColumns(String token, ExtentTest test) {
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get("/restorejobreports/columns");

		response.then().log().all();
		return response;
	}
	
	/*
	 * Call REST API GET: user/restorejobreports/columns
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param test
	 * @return response
	 */
	public Response getRestoreJobReportsColumnsForLoggedInUser(String token, ExtentTest test) {
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get("/user/restorejobreports/columns");

		response.then().log().all();
		return response;
	}
	
	/*
	 * Call REST API GET: users/{id}/restorejobreports/columns
	 * 
	 * @author Rakesh.Chalamala
	 * @param user_id
	 * @param token
	 * @param test
	 * @return response
	 */
	public Response getRestoreJobReportsColumnsForSpecifiedUser(String user_id,String token, ExtentTest test) {
		
		String requestURL = "/users/"+user_id+"/restorejobreports/columns";
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get(requestURL);

		response.then().log().all();
		return response;
	}
	
	/**
	 * Call REST API POST: users/{id}/restorejobreports/columns
	 * 
	 * @author Rakesh.Chalamala
	 * @param user_id
	 * @param token
	 * @param test
	 * @return response
	 */
	public Response createRestoreJobReportsColumnsForSpecifiedUser(String user_id,String token,Map<String, ArrayList<HashMap<String, Object>>> columnsInfo, ExtentTest test) {
		
		String requestURL = "/users/"+user_id+"/restorejobreports/columns";
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(columnsInfo).when()
				.post(requestURL);

		response.then().log().all();
		return response;
	}
	
	/**
	 * Call REST API DELETE: users/{id}/restorejobreports/columns
	 * 
	 * @author Rakesh.Chalamala
	 * @param user_id
	 * @param token
	 * @param test
	 * @return response
	 */
	public Response deleteRestoreJobReportsColumnsForSpecifiedUser(String user_id,String token, ExtentTest test) {
		
		String requestURL = "/users/"+user_id+"/restorejobreports/columns";
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.delete(requestURL);

		response.then().log().all();
		return response;
	}
	
	/**
	 * Call REST API PUT: users/{id}/restorejobreports/columns
	 * 
	 * @author Rakesh.Chalamala
	 * @param user_id
	 * @param token
	 * @param test
	 * @return response
	 */
	public Response updateRestoreJobReportsColumnsForSpecifiedUser(String user_id,String token,Map<String, ArrayList<HashMap<String, Object>>> columnsInfo, ExtentTest test) {
		
		String requestURL = "/users/"+user_id+"/restorejobreports/columns";
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(columnsInfo).when()
				.put(requestURL);

		response.then().log().all();
		return response;
	}
	
	/**
	 * Call REST API POST: user/restorejobreports/columns
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param test
	 * @return response
	 */
	public Response createRestoreJobReportsColumnsForLoggedInUser(String token, Map<String, ArrayList<HashMap<String, Object>>> columnsInfo, ExtentTest test) {
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(columnsInfo).when()
				.post("/user/restorejobreports/columns");

		response.then().log().all();
		return response;
	}
	
	/**
	 * Call REST API DELETE: user/restorejobreports/columns
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param test
	 * @return response
	 */
	public Response deleteRestoreJobReportsColumnsForLoggedInUser(String token, ExtentTest test) {
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.delete("/user/restorejobreports/columns");

		response.then().log().all();
		return response;
	}
	
	/**
	 * Call REST API PUT: user/restorejobreports/columns
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param test
	 * @return response
	 */
	public Response updateRestoreJobReportsColumnsForLoggedInUser(String token,Map<String, ArrayList<HashMap<String, Object>>> columnsInfo, ExtentTest test) {
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(columnsInfo).when()
				.put("/user/restorejobreports/columns");

		response.then().log().all();
		return response;
	}
	/**
	 * Call REST Web service to createBackUpJobReportsColumnsforLoggedInUser
	 * 
	 * @author Ramya.Nagepalli
	 * 
	 * @param adminToken
	 * @param columnsList
	 * @return response
	 */
	public Response createBackUpJobReportsColumnsforLoggedInUser(String adminToken,
			Map<String, ArrayList<HashMap<String, Object>>> columnsList, ExtentTest test) {
		// TODO Auto-generated method stub
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + adminToken).body(columnsList).when()
				.post("/user/backupjobreports/columns");

		response.then().log().all();
		return response;
	}
	
	/**
	 * Call REST Web service to updateBackUpJobReportsColumnsforLoggedInUser
	 * 
	 * @author Ramya.Nagepalli
	 * 
	 * @param adminToken
	 * @param columnsList
	 * 
	 * @return response
	 */
	public Response updateBackUpJobReportsColumnsforLoggedInUser(String adminToken,
			Map<String, ArrayList<HashMap<String, Object>>> columnsList, ExtentTest test) {
		// TODO Auto-generated method stub
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + adminToken).body(columnsList).when()
				.put("/user/backupjobreports/columns");

		response.then().log().all();
		return response;
	}
	
	/**
	 * Call REST Web service to getBackUpJobReportsColumnsforLoggedInUser
	 * 
	 * @author Ramya.Nagepalli
	 * @param adminToken
	 * @param test
	 * @return response
	 */
	public Response getBackUpJobReportsColumnsforLoggedInUser(String adminToken,
		 ExtentTest test) {
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + adminToken).when()
				.get("/user/backupjobreports/columns");
		response.then().log().all();

		return response;
	}

	/**
	 * Call REST Web service to deleteBackUpJobReportsColumnsForLoggedInUser
	 * 
	 * @author Ramya.Nagepalli
	 * @param adminToken
	 * @param test
	 * @return response
	 */
	public Response deleteBackUpJobReportsColumnsForLoggedInUser(String adminToken, ExtentTest test) {
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + adminToken).when()
				.delete("/user/backupjobreports/columns");

		response.then().log().all();
		return response;
	}

	/**
	 * Call REST Web service to getSystemBackUpJobReportsColumns
	 * 
	 * @author Ramya.Nagepalli
	 * @param adminToken
	 * @param test
	 * @return response
	 */
	public Response getSystemBackUpJobReportsColumns(String token, ExtentTest test) {
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get("/backupjobreports/columns");

		response.then().log().all();
		return response;
	}
	
	/**
	 * Call REST Web service to createBackUpJobReportsColumnsforSpecifiedUser
	 * 
	 * @author Ramya.Nagepalli
	 * 
	 * @param adminToken
	 * @param columnsList
	 * @return response
	 */
	public Response createBackUpJobReportsColumnsforSpecifiedUser(String adminToken,String user_id,
			Map<String, ArrayList<HashMap<String, Object>>> columnsList, ExtentTest test) {
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + adminToken).body(columnsList).when()
				.post("/users/"+user_id+"/backupjobreports/columns");
		response.then().log().all();
		return response;
	}
	
	/**
	 * Call REST Web service to updateBackUpJobReportsColumnsforSpecifiedUser
	 * 
	 * @author Ramya.Nagepalli
	 * 
	 * @param adminToken
	 * @param columnsList
	 * 
	 * @return response
	 */
	public Response updateBackUpJobReportsColumnsforSpecifiedUser(String adminToken,String user_id,
			Map<String, ArrayList<HashMap<String, Object>>> columnsList, ExtentTest test) {
		// TODO Auto-generated method stub
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + adminToken).body(columnsList).when()
				.put("/users/"+user_id+"/backupjobreports/columns");

		return response;
	}
	
	/**
	 * Call REST Web service to getBackUpJobReportsColumnsforSpecifiedUser
	 * 
	 * @author Ramya.Nagepalli
	 * @param adminToken
	 * @param test
	 * @return response
	 */
	public Response getBackUpJobReportsColumnsforSpecifiedUser(String adminToken,String user_id,
		 ExtentTest test) {
		// TODO Auto-generated method stub
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + adminToken).when()
				.get("/users/"+user_id+"/backupjobreports/columns");
		response.then().log().all();
		return response;
	}

	/**
	 * Call REST Web service to deleteBackUpJobReportsColumnsForSpecifiedUser
	 * 
	 * @author Ramya.Nagepalli
	 * @param adminToken
	 * @param test
	 * @return response
	 */
	public Response deleteBackUpJobReportsColumnsForSpecifiedUser(String adminToken,String user_id, ExtentTest test) {
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + adminToken).when()
				.delete("/users/"+user_id+"/backupjobreports/columns");

		response.then().log().all();
		return response;
	}

	
	/* Call REST API POST: /reportschedules
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param scheduleInfo
	 * @param test
	 * @return response
	 * 
	 */
	public Response createReportSchedule(String token, HashMap<String, Object> scheduleInfo, ExtentTest test) {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(scheduleInfo).when()
				.post("/reportschedules");

		response.then().log().all();
		return response;
	}
	
	/* Call REST API POST: /reportschedules/{id}/generatereportnow
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param schedule_id
	 * @param test
	 * @return response
	 */
	public Response generateReportNow(String token, String schedule_id, ExtentTest test) {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.post("/reportschedules/"+schedule_id+"/generatereportnow");

		response.then().log().all();
		return response;
	}
	
	/* Call REST API PUT: /reportschedules/{id}
	 * 
	 * @author Rakesh.Chalamala
	 * @param schedule_id
	 * @param token
	 * @param scheduleInfo
	 * @param test
	 * @return response
	 * 
	 */
	public Response updateReportSchedule(String schedule_id,String token, HashMap<String, Object> scheduleInfo, ExtentTest test) {

		String requestURL = "/reportschedules/"+schedule_id;
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(scheduleInfo).when()
				.put(requestURL);

		response.then().log().all();
		return response;
	}
	
	/* Call REST API DELETE: /reportschedules/{id}
	 * 
	 * @author Rakesh.Chalamala
	 * @param schedule_id
	 * @param token
	 * @param test
	 * @return response
	 * 
	 */
	public Response deleteReportSchedule(String schedule_id, String token, ExtentTest test) {

		String requestURL = "/reportschedules/"+schedule_id;
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.delete(requestURL);

		response.then().log().all();
		return response;
	}
	
	/* Call REST API DELETE: /reportschedules/{id}/reports
	 * 
	 * @author Rakesh.Chalamala
	 * @param schedule_id
	 * @param token
	 * @param test
	 * @return response
	 * 
	 */
	public Response deleteReportsByScheduleId(String schedule_id, String token, ExtentTest test) {

		String requestURL = "/reportschedules/"+schedule_id+"/reports";
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.delete(requestURL);

		response.then().log().all();
		return response;
	}

	/* Call REST API GET: /reportschedules/{id}
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param test
	 * @return response
	 * 
	 */
	public Response getReportSchedule(String token, String additionalURL, ExtentTest test) {
		
		String requestURL = "/reportschedules";
		if((additionalURL != null) && (!additionalURL.equals(""))) {
			requestURL += "?" + additionalURL;
		}
		
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get(requestURL);

		response.then().log().all();
		return response;
	}
	

	/**
	 * Call REST Web service to deleteReportFiltersForLoggedInUser
	 * 
	 * @author Ramya.Nagepalli
	 * @param adminToken
	 * @param filterId
	 * @param test
	 * @return response
	 */
	public Response deleteReportFiltersForLoggedInUser(String adminToken,String filterId, ExtentTest test) {
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + adminToken).when()
				.delete("/user/reportfilters/"+filterId);

		response.then().log().all();
		return response;
	}
	/**
	 * Call REST Web service to deleteReportFiltersForSpecifiedUser
	 * 
	 * @author Ramya.Nagepalli
	 * @param adminToken
	 * @param user_id
	 * @param filterId
	 * @param test
	 * @return response
	 */
	public Response deleteReportFiltersForSpecifiedUser(String adminToken,String user_id,String filterId, ExtentTest test) {
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + adminToken).when()
				.delete("/users/"+user_id+"/reportfilters/"+filterId);

		response.then().log().all();
		return response;
	}


    /**
    *  Call REST API PUT: user/reportfilters
    * 
     * @author Ramya.Nagepalli
    * @param token
    * @param filter_id
    * @param filter_info
    * @param test
    */
    public Response updateReportFiltersForLoggedInUser(String token,String filter_id, Map<String, Object> filter_info, ExtentTest test) {
          
    	  String requestURL = "/user/reportfilters/"+filter_id;
          test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);
         
          Response response = given().header("Content-Type", "application/json")
                      .header("Authorization", "Bearer " + token).body(filter_info).when()
                      .put(requestURL);

          response.then().log().all();
          return response;
    }
    
    /**
    *  Call REST API PUT: users/{id}/reportfilters
    * 
     * @author Ramya.Nagepalli
    * @param user_id
    * @param filter_id
    * @param token
    * @param filter_info
    * @param test
    */
    public Response updateReportFiltersForSpecifiedUser(String user_id,String filter_id, String token, Map<String, Object> filter_info, ExtentTest test) {
          
          String requestURL = "/users/"+user_id+"/reportfilters/"+filter_id;
          test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);
          
          Response response = given().header("Content-Type", "application/json")
                      .header("Authorization", "Bearer " + token).body(filter_info).when()
                      .put(requestURL);

          response.then().log().all();
          return response;
    }

	
	/**
	 *  Call REST API POST: user/reportfilters
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param filterInfo
	 * @param test
	 */
	public Response createReportFiltersForLoggedInUser(String token, Map<String, Object> filter_info, ExtentTest test) {
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(filter_info).when()
				.post("/user/reportfilters");

		response.then().log().all();
		return response;
	}
	
	/** Call REST API POST: users/{id}/reportfilters
	 * 
	 * @author Rakesh.Chalamala
	 * @param user_id
	 * @param token
	 * @param filterInfo
	 * @param test
	 */
	public Response createReportFiltersForSpecifiedUser(String user_id, String token, HashMap<String, Object> filterInfo, ExtentTest test) {
		
		String requestURL = "/users/"+user_id+"/reportfilters";
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(filterInfo).when()
				.post(requestURL);

		response.then().log().all();
		return response;
	}
	
	/**
	 * Call REST Web service to getReportFiltersForLoggedInUserByFilterId
	 * 
	 * @author Rakesh.Chalamala
	 * @param adminToken
	 * @param filterId
	 * @param test
	 * @return response
	 */
	public Response getReportFiltersForLoggedInUserByFilterId(String adminToken,String filterId, ExtentTest test) {
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + adminToken).when()
				.get("/user/reportfilters/"+filterId);

		response.then().log().all();
		return response;
	}
	
	/**
	 * Call REST Web service to getReportFiltersForSpecifiedUserByFilterId
	 * 
	 * @author Rakesh.Chalamala
	 * @param adminToken
	 * @param user_id
	 * @param filterId
	 * @param test
	 * @return response
	 */
	public Response getReportFiltersForSpecifiedUserByFilterId(String adminToken,String user_id,String filterId, ExtentTest test) {
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + adminToken).when()
				.get("/users/"+user_id+"/reportfilters/"+filterId);

		response.then().log().all();
		return response;
	}
	
	/**
	 * Call REST Web service to getReportFiltersForLoggedInUser
	 * 
	 * @author Rakesh.Chalamala
	 * @param adminToken
	 * @param additionalURL
	 * @param test
	 * @return response
	 */
	public Response getReportFiltersForLoggedInUser(String adminToken,String additionalURL, ExtentTest test) {
		
		String requestUrl = "/user/reportfilters";
		if((additionalURL != null) && (!additionalURL.equals(""))) {
			requestUrl += "?" + additionalURL;
		}
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + adminToken).when()
				.get(requestUrl);

		response.then().log().all();
		return response;
	}
	
	/**
	 * Call REST Web service to getReportFiltersForSpecifiedUser
	 * 
	 * @author Rakesh.Chalamala
	 * @param adminToken
	 * @param user_id
	 * @param additionalURL
	 * @param test
	 * @return response
	 */
	public Response getReportFiltersForSpecifiedUser(String adminToken, String user_id, String additionalURL, ExtentTest test) {
		
		String requestUrl = "/users/"+user_id+"/reportfilters";
		if((additionalURL != null) && (!additionalURL.equals(""))) {
			requestUrl += "?" + additionalURL;
		}
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + adminToken).when()
				.get(requestUrl);

		response.then().log().all();
		return response;
	}
	
	/* Invoke REST API get REPORT FILTERS
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param test
	 * @return response
	 */
	public Response getReportFilters(String token, ExtentTest test) {
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get("/reportfilters");

		response.then().log().all();
		return response;
	}

	 /**
     * Call REST Web service to getBackupJobStatusSummary
     * 
     * @author Ramya.Nagepalli
     * @param adminToken
     * @param test
     * @return response
     */
    public Response getBackupJobStatusSummary(String adminToken, ExtentTest test) {
    	// TODO Auto-generated method stub
    	Response response = given().header("Content-Type", "application/json")
    			.header("Authorization", "Bearer " + adminToken).when()
    			.get("/dashboards/backupjobstatussummary");

    	response.then().log().all();
    	return response;
    }

	
     /**
      * 
       * Call REST Web service to GetRestoreJobReportsDetailsStatusSummaryTest
      * 
       * @author ramya.nagepalli
      * @param token
      * @param AdditionalURL
      * @param test
      * @return
      */
     
      public Response GetRestoreJobReportsStatusSummaryTest(String token,String AdditionalURL, ExtentTest test) {
            // TODO Auto-generated method stub
         
            
            String requestURL = "/restorejobreports/jobstatussummary"+"?time_resolution="+AdditionalURL;
            test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);
            
            Response response = given().header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + token).when()
                        .get(requestURL);

            response.then().log().all();
            return response;
      }
      
      

      /**
       * 
       * Call REST Web service to getRestoreJobReportsDetailsStatusSummary
       * 
       * @author ramya.nagepalli
       * @param token
       * @param test
       * @return
       */
      public Response getRestoreJobReportsTopSourcesForLoggedInUser(String adminToken, String AdditionalURL, ExtentTest test) {
    	  // TODO Auto-generated method stub
    	  
    	  String requestUrl = "/restorejobreports/topsources";
  		if((AdditionalURL != null) && (!AdditionalURL.equals(""))) {
  			requestUrl += "?" + AdditionalURL;
  		}
  		
    	  
    	/*  String requestURL = "/restorejobreports/topsources"+"?"+AdditionalURL;
          test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);*/
          
    	  Response response = given().header("Content-Type", "application/json")
    			  .header("Authorization", "Bearer " + adminToken).when()
    			  .get(requestUrl);

    	  response.then().log().all();
    	  return response;
      }

  	/**
  	 * 
  	 * Call REST Web service to getBackupJobReportsTopSourcesForLoggedInUser
  	 * 
  	 * @author ramya.nagepalli
  	 * @param token
  	 * @param AdditionalURL 
  	 * @param test
  	 * @return
  	 */
  	public Response getBackupJobReportsTopSourcesForLoggedInUser(String token, String AdditionalURL, ExtentTest test) {
  		// TODO Auto-generated method stub
  		
  		
  		  String requestUrl ="/backupjobreports/topsources";
  	  		if((AdditionalURL != null) && (!AdditionalURL.equals(""))) {
  	  			requestUrl += "?" + AdditionalURL;
  	  		}
     
  		Response response = given().header("Content-Type", "application/json")
  				.header("Authorization", "Bearer " + token).when()
  				.get(requestUrl);

  		response.then().log().all();
  		return response;
  	}
	
	/* GET /businessintelligencereports/types
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param test
	 * @return response
	 */
	public Response getBIReportsTypes(String token, ExtentTest test) {
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get("/businessintelligencereports/types");

		response.then().log().all();
		return response;
	}
	
	/* GET /businessintelligencereports/types/cd_near_capacity
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param test
	 * @return response
	 */
	public Response getBIReportsTypesCdNearCapacity(String token, ExtentTest test) {
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get("/businessintelligencereports/types/cd_near_capacity");

		response.then().log().all();
		return response;
	}
	
	/* GET /businessintelligencereports/types/cd_over_capacity
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param test
	 * @return response
	 */
	public Response getBIReportsTypesCdOverCapacity(String token, ExtentTest test) {
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get("/businessintelligencereports/types/cd_over_capacity");

		response.then().log().all();
		return response;
	}
	
	/* GET /businessintelligencereports/types/cd_trial
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param test
	 * @return response
	 */
	public Response getBIReportsTypesCdTrial(String token, ExtentTest test) {
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get("/businessintelligencereports/types/cd_trial");

		response.then().log().all();
		return response;
	}
	
	/* GET /businessintelligencereports/types/ch_near_capacity
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param test
	 * @return response
	 */
	public Response getBIReportsTypesChNearCapacity(String token, ExtentTest test) {
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get("/businessintelligencereports/types/ch_near_capacity");

		response.then().log().all();
		return response;
	}
	
	/* GET /businessintelligencereports/types/ch_over_capacity
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param test
	 * @return response
	 */
	public Response getBIReportsTypesChOverCapacity(String token, ExtentTest test) {
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get("/businessintelligencereports/types/ch_over_capacity");

		response.then().log().all();
		return response;
	}
	
	/* GET /businessintelligencereports/types/ch_trial
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param test
	 * @return response
	 */
	public Response getBIReportsTypesChTrial(String token, ExtentTest test) {
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get("/businessintelligencereports/types/ch_trial");

		response.then().log().all();
		return response;
	}
	
	/* GET /businessintelligencereports/types/cd_near_capacity?format=csv
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param test
	 * @return response
	 */
	public Response getBIRCdNearCapacityExportCSV(String token, ExtentTest test) {
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get("/businessintelligencereports/types/cd_near_capacity?format=csv");

		response.then().log().all();
		return response;
	}
	
	/* GET /businessintelligencereports/types/cd_over_capacity?format=csv
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param test
	 * @return response
	 */
	public Response getBIRCdOverCapacityExportCSV(String token, ExtentTest test) {
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get("/businessintelligencereports/types/cd_over_capacity?format=csv");

		response.then().log().all();
		return response;
	}
	
	/* GET /businessintelligencereports/types/cd_trial?format=csv
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param test
	 * @return response
	 */
	public Response getBIRCdTrialExportCSV(String token, ExtentTest test) {
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get("/businessintelligencereports/types/cd_trial?format=csv");

		response.then().log().all();
		return response;
	}
	
	/* GET /businessintelligencereports/types/ch_near_capacity?format=csv
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param test
	 * @return response
	 */
	public Response getBIRChNearCapacityExportCSV(String token, ExtentTest test) {
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get("/businessintelligencereports/types/ch_near_capacity?format=csv");

		response.then().log().all();
		return response;
	}
	
	/* GET /businessintelligencereports/types/ch_over_capacity?format=csv
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param test
	 * @return response
	 */
	public Response getBIRChOverCapacityExportCSV(String token, ExtentTest test) {
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get("/businessintelligencereports/types/ch_over_capacity?format=csv");

		response.then().log().all();
		return response;
	}
	
	/* GET /businessintelligencereports/types/ch_trial?format=csv
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param test
	 * @return response
	 */
	public Response getBIRChTrialExportCSV(String token, ExtentTest test) {
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get("/businessintelligencereports/types/ch_trial?format=csv");

		response.then().log().all();
		return response;
	}
	
	/** GET /datatransferreport/summary 
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param test
	 * @return response
	 */
	public Response getDataTransferReportSummary(String token, String additionalURL, ExtentTest test) {
		
		String requestURL="";

		if(additionalURL.equals("")||additionalURL.equals(null))
		{

			requestURL="/datatransferreport/summary";
		}
		else
		{
			requestURL="/datatransferreport/summary?"+additionalURL;
		}

		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get(requestURL);
		
		response.then().log().all();
		return response;
	}
	
	/* GET /datatransferreport/details 
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param test
	 * @return response
	 */
	public Response getDataTransferReportDetails(String token, String additionalURL, ExtentTest test) {
		
		String requestUrl = "/datatransferreport/details";
		if((additionalURL != null) && (!additionalURL.equals(""))) {
			requestUrl += "?" + additionalURL;
		}
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get(requestUrl);
		
		response.then().log().all();
		return response;
	}
	
	/* GET: /reports
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param additionalURL
	 * @param test
	 * @return response
	 */
	public Response getReports(String token, String additionalURL, ExtentTest test) {
		
		String requestUrl = "/reports";
		if((additionalURL != null) && (!additionalURL.equals(""))) {
			requestUrl += "?" + additionalURL;
		}
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get(requestUrl);
		
		response.then().log().all();
		return response;
	}
	
	/* DELETE: /reports
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param test
	 * @return response
	 */
	public Response deleteReports(String token, ArrayList<String> report_ids, ExtentTest test) {
		
		String requestUrl = "/reports";
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(report_ids)
				.when().delete(requestUrl);
		
		response.then().log().all();
		return response;
	}
	
	/* DELETE: /reports/{id}
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param report_id
	 * @param test
	 * @return response
	 */
	public Response deleteReportsById(String token, String report_id, ExtentTest test) {
		
		String requestUrl = "/reports/"+report_id;
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.delete(requestUrl);
		
		response.then().log().all();
		return response;
	}


    /**
     * Call REST Web service to getBackupJobStatusSummary
     * 
     * @author Ramya.Nagepalli
     * @param adminToken
     * @param additionalUrl 
     * @param test
     * @return response
     */
    public Response getBackupJobStatusSummary(String adminToken, String additionalUrl, ExtentTest test) {
    	// TODO Auto-generated method stub

    	String requestURL =null;
    	if(additionalUrl=="")
    	{
    		requestURL = "/dashboards/backupjobstatussummary";
    	}
    	else
    	{
    		requestURL = "/dashboards/backupjobstatussummary"+"?"+additionalUrl;
    	}
    	test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);


    	Response response = given().header("Content-Type", "application/json")
    			.header("Authorization", "Bearer " + adminToken).when()
    			.get(requestURL);

    	response.then().log().all();
    	return response;
    }

    /**
     * Call REST Web service to getDashboardTopSources
     * 
     * @author Ramya.Nagepalli
     * @param adminToken
     * @param additionalUrl 
     * @param test
     * @return response
     */
	public Response getDashboardTopSources(String adminToken, String additionalUrl, ExtentTest test) {
		// TODO Auto-generated method stub
		String requestURL =null;
		if(additionalUrl.equals(""))
		{
			requestURL = "/dashboards/topsources";
		}
		else
		{
			requestURL = "/dashboards/topsources?"+additionalUrl;
		}
		 
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);
		
		Response response = given().header("Content-Type", "application/json")
    			.header("Authorization", "Bearer " + adminToken).when()
    			.get(requestURL);

    	response.then().log().all();
    	return response;
	}

	 /**
     * Call REST Web service to getDashboardSourceSummary
     * 
     * @author Ramya.Nagepalli
     * @param adminToken
	 * @param additionalUrl 
     * @param test
     * @return response
     */
	public Response getDashboardSourceSummary(String adminToken, String additionalUrl, ExtentTest test) {
		// TODO Auto-generated method stub
		
		String requestURL =null;
		if(additionalUrl.equals(""))
		{
			requestURL = "/dashboards/sourcesummary";
		}
		else
		{
			requestURL = "/dashboards/sourcesummary?"+additionalUrl;
		}
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + adminToken).when()
				.get(requestURL);

		response.then().log().all();
		return response;
	}

	 /**
     * Call REST Web service to getDashboardTopPolicies
     * 
     * @author Ramya.Nagepalli
     * @param adminToken
	 * @param additionalUrl 
     * @param test
     * @return response
     */
	public Response getDashboardTopPolicies(String adminToken, String additionalUrl, ExtentTest test) {
		// TODO Auto-generated method stub

		String requestURL =null;
		if(additionalUrl.equals(""))
		{
			requestURL = "/dashboards/toppolicies";
		}
		else
		{
			requestURL =  "/dashboards/toppolicies?"+additionalUrl;
		}
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + adminToken).when()
				.get(requestURL);

		response.then().log().all();
		return response;
	}

	 /**
     * Call REST Web service to getDashboardPolicySummaryDetails
     * 
     * @author Ramya.Nagepalli
     * @param adminToken
	 * @param additionalUrl 
     * @param test
     * @return response
     */
	public Response getDashboardPolicySummaryDetails(String adminToken, String additionalUrl, ExtentTest test) {
		// TODO Auto-generated method stub
		String requestURL =null;
		if(additionalUrl.equals(""))
		{
			requestURL = "/dashboards/policysummary";
		}
		else
		{
			requestURL =  "/dashboards/policysummary?"+additionalUrl;
		}
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + adminToken).when()
				.get(requestURL);

		response.then().log().all();
		return response;
	}

	/** Call REST API GET: /capacityusage/report/details
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param additionalUrl
	 * @param test
	 * @return response
	 * 
	 */
	public Response getCapacityUsageReportsDetails(String adminToken, String additionalUrl, ExtentTest test) {
		// TODO Auto-generated method stub
		
		String requestURL = null;
		
		if(additionalUrl.equals(""))
		{
			requestURL = "/capacityusage/report/details";
		}
		else
		{
			requestURL =  "/capacityusage/report/details?"+additionalUrl;
		}
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get(requestURL);

		response.then().log().all();
		return response;
	
	}

	/** Call REST API GET: /capacityusage/cloudhybrid/usage
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param additionalUrl
	 * @param test
	 * @return response
	 * 
	 */
	public Response getCloudHybridCapacityUsageDetails(String adminToken, String additionalUrl, ExtentTest test) {
		// TODO Auto-generated method stub
		String requestURL = null;
		if(additionalUrl.equals(""))
		{
			requestURL = "/capacityusage/cloudhybrid/usage";
		}
		else
		{
			requestURL =  "/capacityusage/cloudhybrid/usage?"+additionalUrl;
		}
		/*test.log(LogStatus.INFO, "The URI is " + requestURL);*/
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get(requestURL);

		response.then().log().all();
		return response;
	}

	/** Call REST API GET: /capacityusage/cloudhybrid/dedupesavings
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param additionalUrl
	 * @param test
	 * @return response
	 * 
	 */
	public Response getCloudHybridDedupeUsageDetails(String adminToken, String additionalUrl, ExtentTest test) {
		// TODO Auto-generated method stub
		String requestURL = null;
		if(additionalUrl.equals(""))
		{
			requestURL = "/capacityusage/cloudhybrid/dedupesavings";
		}
		else
		{
			requestURL =  "/capacityusage/cloudhybrid/dedupesavings?"+additionalUrl;
		}
		/*test.log(LogStatus.INFO, "The URI is " + requestURL);*/
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get(requestURL);

		response.then().log().all();
		return response;
	}

	/* Call REST Web service to GET: /dashboards/topcustomers
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param org_id
	 * @param test
	 * @return response
	 */
	public Response getTopCustomers(String token,String org_id, ExtentTest test) {
		
		String requestURL = "/dashboards/topcustomers";
		if(org_id != null && org_id != "") {
			requestURL += "?organization_id="+org_id;
		}		 
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get(requestURL);

		response.then().log().all();
		return response;
	}
	
	/* Call REST Web service GET: /organizations/{id}/accountsummary
	 * 
	 *  @author Rakesh.Chalamala
	 *  @param token
	 *  @param org_id
	 *  @param test
	 *  @return response
	 */
	public Response getOrganizationAccountSummary(String token, String org_id, ExtentTest test) {
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get("/organizations/"+org_id+"/accountsummary");

		response.then().log().all();
		return response;
	}
	/**
	 * GET: /dashboard/Widgets
	 * 
	 * @author Ramya.Nagepalli
	 * @param validToken
	 * @param test
	 * @return response
	 */
	public Response getSystemDashboardWidgets(String validToken, ExtentTest test) {
		
		String requestURL="/dashboard/widgets";
		
		Response response=given().header("Content-Type","application/json")
				.header("Authorization","Bearer "+validToken)
				.when().get(requestURL);
		
		response.then().log().all();
		
		return response;
	}

	/**
	 * POST: /user/dashboard/widgets
	 * 
	 * @author Ramya.Nagepalli
	 * @param composed_expected_Widgets
	 * @param validToken
	 * @param test
	 * @return response
	 */
	public Response postDashboardWidgetsForLoggedInUser(Map<String, ArrayList<HashMap<String, Object>>> composed_expected_Widgets,
			String validToken,
			ExtentTest test) {

		String requestURL="/user/dashboard/widgets";

		Response response=given().header("Content-Type","application/json")
				.header("Authorization","Bearer "+validToken)
				.body(composed_expected_Widgets)
				.when()
				.post(requestURL);

		response.then().log().all();

		return response;

	}
	/**
	 * DELETE: /user/dashboard/widgets
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param test
	 * @return response
	 */

	public Response deleteDashboardWidgetsForLoggedInUser(String token, ExtentTest test) {
		// TODO Auto-generated method stub
		String requestURL="/user/dashboard/widgets";

		Response response=given().header("Content-Type","application/json")
				.header("Authorization","Bearer "+token)
				.when()
				.delete(requestURL);

		response.then().log().all();

		return response;
	}

	/**
	 * GET: /user/dashboard/widgets
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param test
	 * @return response
	 */
	public Response getDashboardWidgetsForLoggedInUser(String token, ExtentTest test) {
		String requestURL="/user/dashboard/widgets";

		Response response=given().header("Content-Type","application/json")
				.header("Authorization","Bearer "+token)
				.when()
				.get(requestURL);

		response.then().log().all();

		return response;
	}
/**
 * POST: /user/{id}/dashboard/widgets
 * 
 * @param composed_expected_Widgets
 * @param user_id
 * @param validToken
 * @param test
 * @return
 */
	public Response postDashboardWidgetsForSpecifiedUser(
			Map<String, ArrayList<HashMap<String, Object>>> composed_expected_Widgets, String user_id,
			String validToken, ExtentTest test) {
		
		String requestURL="/users/"+user_id+"/dashboard/widgets";
		
		test.log(LogStatus.INFO, "the request URL is:"+requestURL);
		
		Response response=given().header("Content-Type","application/json")
				.header("Authorization","Bearer "+validToken)
				.body(composed_expected_Widgets)
				.when()
				.post(requestURL);
		response.then().log().all();
		
		return response;
	}
	/**
	 *  DELETE: /user/{id}/dashboard/widgets
	 * 
	 * @author Ramya.Nagepalli
	 * @param validToken
	 * @param user_id
	 * @param test
	 * @return response
	 */
	public Response deleteDashboardWidgetsForSpecifiedUser(String validToken, String user_id, ExtentTest test) {

		String requestURL="/users/"+user_id+"/dashboard/widgets";
		
		test.log(LogStatus.INFO, "the request URL is:"+requestURL);
		Response response=given().header("Content-Type","application/json")
				.header("Authorization","Bearer "+validToken)
				.when()
				.delete(requestURL);
		
		response.then().log().all();
		
		return response;
	}
	/**
	 * GET: /user/{id}/dashboard/widgets
	 *  
	 * @author Ramya.Nagepalli
	 * @param validToken
	 * @param user_id
	 * @param test
	 * @return
	 */

	public Response getDashboardWidgetsForSpecifiedUser(String validToken, String user_id, ExtentTest test) {

		String requestURL="/users/"+user_id+"/dashboard/widgets";
		
		test.log(LogStatus.INFO, "the request URL is:"+requestURL);
		Response response=given().header("Content-Type","application/json")
				.header("Authorization","Bearer "+validToken)
				.when()
				.get(requestURL);
		
		response.then().log().all();
		
		return response;
	
	}

	/**
	 * PUT: /users/{id}/dashboard/widgets
	 * 
	 * @author Ramya.Nagepalli
	 * @param composed_expected_Widgets
	 * @param user_id
	 * @param validToken
	 * @param test
	 * @return
	 */
	public Response putDashboardWidgetsForSpecifiedUser(
			Map<String, ArrayList<HashMap<String, Object>>> composed_expected_Widgets, String user_id,
			String validToken, ExtentTest test) {
		// TODO Auto-generated method stub
		String requestURL="/users/"+user_id+"/dashboard/widgets";

		test.log(LogStatus.INFO, "the request URL is:"+requestURL);
		Response response=given().header("Content-Type","application/json")
				.header("Authorization","Bearer "+validToken)
				.body(composed_expected_Widgets)
				.when()
				.put(requestURL);

		response.then().log().all();

		return response;
	}
	/**
	 * PUT: /user/dashboard/widgets
	 * 
	 * @author Ramya.Nagepalli
	 * @param composed_expected_Widgets
	 * @param user_id
	 * @param test
	 * @return
	 */
	public Response putDashboardWidgetsForLoggedInUser(
			Map<String, ArrayList<HashMap<String, Object>>> composed_expected_Widgets, String validToken,
			ExtentTest test) {
		// TODO Auto-generated method stub
		String requestURL="/user/dashboard/widgets";

		test.log(LogStatus.INFO, "the request URL is:"+requestURL);
		Response response=given().header("Content-Type","application/json")
				.header("Authorization","Bearer "+validToken)
				.body(composed_expected_Widgets)
				.when()
				.put(requestURL);

		response.then().log().all();

		return response;
	}
	/**
	 * createReportFiltersForSpecifiedUser
	 * 
	 * @author Ramya.Nagepalli
	 * @param additionalURL
	 * @param user_id
	 * @param token
	 * @param filterInfo
	 * @param test
	 * @return
	 */
	public Response createReportFiltersForSpecifiedUser(String additionalURL, String user_id, String token,
			HashMap<String, Object> filterInfo, ExtentTest test) {

		String requestURL = "/users/" + user_id + "/reportfilters";

		if ((additionalURL != null) && (!additionalURL.equals(""))) {
			requestURL += "?" + additionalURL;
		}
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(filterInfo).when().post(requestURL);

		response.then().log().all();
		return response;
	}
	/**
	 * getReportFiltersForLoggedInUserByFilterId
	 * 
	 * @param adminToken
	 * @param filterId
	 * @param additionalURL
	 * @param test
	 * @return
	 */
	public Response getReportFiltersForLoggedInUserByFilterId(String adminToken, String filterId, String additionalURL,
			ExtentTest test) {

		String requestURL = "/user/reportfilters/" + filterId;

		if ((additionalURL != null) && (!additionalURL.equals(""))) {
			requestURL += "?" + additionalURL;
		}

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + adminToken).when().get(requestURL);

		response.then().log().all();
		return response;
	}

	/**
	 * updateReportFiltersForLoggedInUser
	 * 
	 * @author Ramya.Nagepalli
	 * @param additionalURL
	 * @param token
	 * @param filter_id
	 * @param filterInfo
	 * @param test
	 * @return
	 */
	public Response updateReportFiltersForLoggedInUser(String additionalURL, String token, String filter_id,
			HashMap<String, Object> filterInfo, ExtentTest test) {

		String requestURL = "/user/reportfilters/" + filter_id;

		if ((additionalURL != null) && (!additionalURL.equals(""))) {
			requestURL += "?" + additionalURL;
		}
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(filterInfo).when().put(requestURL);

		response.then().log().all();
		return response;
	}

	
	/**
	 * createReportListFiltersForLoggedInUser
	 * 
	 * @author Ramya.Nagepalli
	 * @param additionalURL
	 * @param token
	 * @param reportlistfiltersInfo
	 * @param test
	 * @return
	 */
	public Response createReportListFiltersForLoggedInUser(String additionalURL, String token,
			HashMap<String, Object> reportlistfiltersInfo, ExtentTest test) {

		String requestURL = "/user/reportlistfilters";

		if ((additionalURL != null) && (!additionalURL.equals(""))) {
			requestURL += "?" + additionalURL;
		}
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(reportlistfiltersInfo).when().post(requestURL);

		response.then().log().all();
		return response;
	}

	/**
	 * \ getReportListFiltersForLoggedInUserByFilterId
	 * 
	 * @author Ramya.Nagepalli
	 * @param additionalURL
	 * @param token
	 * @param filter_id
	 * @param test
	 * @return
	 */
	public Response getReportListFiltersForLoggedInUserByFilterId(String additionalURL, String token, String filter_id,
			ExtentTest test) {

		String requestURL = "/user/reportlistfilters/" + filter_id;

		if ((additionalURL != null) && (!additionalURL.equals(""))) {
			requestURL += "?" + additionalURL;
		}
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().get(requestURL);
		return response;
	}

	/**
	 * updateReportListFiltersForLoggedInUser
	 * 
	 * @author Ramya.Nagepalli
	 * @param additionalURL
	 * @param token
	 * @param filter_id
	 * @param updateReportListFiltersInfo
	 * @param test
	 * @return
	 */
	public Response updateReportListFiltersForLoggedInUser(String additionalURL, String token, String filter_id,
			HashMap<String, Object> updateReportListFiltersInfo, ExtentTest test) {

		String requestURL = "/user/reportlistfilters/" + filter_id;
		if ((additionalURL != null) && (!additionalURL.equals(""))) {
			requestURL += "?" + additionalURL;
		}
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(updateReportListFiltersInfo).when().put(requestURL);

		response.then().log().all();
		return response;
	}
	
	/**
	 * Gets the business intelligence report for Cloud Direct organizations that are exempted from Trial
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param test
	 * @return
	 */
	public Response getBIRCDExemption(String token, ExtentTest test) {
	
		String requestURL = "/businessintelligencereports/types/cd_exemption";
		
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);

		Response response = given()
							.header("Content-Type", "application/json")
							.header("Authorization", "Bearer " + token)
							.when()
							.get(requestURL);

		response.then().log().all();
		return response;		
	}
	
	public Response getBIRCDExemptionExportCSV(String token, ExtentTest test) {
		
		String requestURL = "/businessintelligencereports/types/cd_exemption?format=csv";
		
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);

		Response response = given()
							.header("Content-Type", "application/json")
							.header("Authorization", "Bearer " + token)
							.when()
							.get(requestURL);

		response.then().log().all();
		return response;		
	}
	
	/**
	 * Gets the business intelligence report for Cloud Direct organizations that are going to expire in next few days
	 * and already expired but not deleted org info
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param test
	 * @return
	 */
	public Response getBIRCDExpiration(String token, ExtentTest test) {
		
		String requestURL = "/businessintelligencereports/types/cd_expiration";
		
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);

		Response response = given()
							.header("Content-Type", "application/json")
							.header("Authorization", "Bearer " + token)
							.when()
							.get(requestURL);

		response.then().log().all();
		return response;		
	}
	
	public Response getBIRCDExpirationExportCSV(String token, ExtentTest test) {
		
		String requestURL = "/businessintelligencereports/types/cd_expiration?format=csv";
		
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);

		Response response = given()
							.header("Content-Type", "application/json")
							.header("Authorization", "Bearer " + token)
							.when()
							.get(requestURL);

		response.then().log().all();
		return response;		
	}
	
	/**
	 * Gets the business intelligence report for Cloud Hybrid organizations that are exempted from Trial
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param test
	 * @return
	 */
	public Response getBIRCHExemption(String token, ExtentTest test) {
	
		String requestURL = "/businessintelligencereports/types/ch_exemption";
		
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);

		Response response = given()
							.header("Content-Type", "application/json")
							.header("Authorization", "Bearer " + token)
							.when()
							.get(requestURL);

		response.then().log().all();
		return response;		
	}
	
	public Response getBIRCHExemptionExportCSV(String token, ExtentTest test) {
		
		String requestURL = "/businessintelligencereports/types/ch_exemption?format=csv";
		
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);

		Response response = given()
							.header("Content-Type", "application/json")
							.header("Authorization", "Bearer " + token)
							.when()
							.get(requestURL);

		response.then().log().all();
		return response;		
	}
	
	/**
	 * Gets the business intelligence report for Cloud HYbrid organizations that are going to expire in next few days
	 * and already expired but not deleted org info
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param test
	 * @return
	 */
	public Response getBIRCHExpiration(String token, ExtentTest test) {
		
		String requestURL = "/businessintelligencereports/types/ch_expiration";
		
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);

		Response response = given()
							.header("Content-Type", "application/json")
							.header("Authorization", "Bearer " + token)
							.when()
							.get(requestURL);

		response.then().log().all();
		return response;		
	}
	
	public Response getBIRCHExpirationExportCSV(String token, ExtentTest test) {
		
		String requestURL = "/businessintelligencereports/types/ch_expiration?format=csv";
		
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);

		Response response = given()
							.header("Content-Type", "application/json")
							.header("Authorization", "Bearer " + token)
							.when()
							.get(requestURL);

		response.then().log().all();
		return response;		
	}

}
