package invoker;

import static io.restassured.RestAssured.given;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.hamcrest.core.IsNull;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class SPOGJobInvoker {
	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public SPOGJobInvoker(String baseURI, String port) {
		RestAssured.baseURI = baseURI;
		RestAssured.port = Integer.valueOf(port);
		 RestAssured.basePath = "/api";
	}
	
	/**
	 * Call Rest Web Service to get system job filters for user
	 * @param token
	 * @return response
	 */

	public Response getSystemJobFilters(String validToken) {
		// TODO Auto-generated method stub
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + validToken).when().get("/jobfilters");

		response.then().log().all();

		return response;
	}
	
	public Response createJobFilter(String userID, Map<String, Object> jobFilterInfo) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(jobFilterInfo).when()
				.post("/users/" + userID + "/jobfilters");

		response.then().log().all();
		return response;
	}
	
	public Response  createJobFilterForLoggedinUser(Map<String, Object> jobFilterInfo) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(jobFilterInfo).when()
				.post("/user/jobfilters");

		response.then().log().all();
		return response;
	}
	
	public Response updateJobFilter(String userID, String filterID, Map<String, Object> jobFilterInfo) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(jobFilterInfo).when()
				.put("/users/" + userID + "/jobfilters/" + filterID);

		response.then().log().all();
		return response;
	}
	
	public Response updateJobFilterForLoggedinUser(String filterID, Map<String, Object> jobFilterInfo) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(jobFilterInfo).when()
				.put("/user/jobfilters/" + filterID);

		response.then().log().all();
		return response;
	}
	
	
	public Response getJobFilterByID(String userID, String filterID, String token) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get("/users/" + userID + "/jobfilters/" + filterID);

		response.then().log().all();
		return response;
	}
	
	public Response getJobFilterByID(String userID, String filterID) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when()
				.get("/users/" + userID + "/jobfilters/" + filterID);

		response.then().log().all();
		return response;
	}
	
	public Response getJobFilterForLoggedinUserByID(String filterID) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when()
				.get("/user/jobfilters/" + filterID);

		response.then().log().all();
		return response;
	}
	
	
	public Response getJobFiltersForUser(String userID, String token) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get("/users/" + userID + "/jobfilters");

		response.then().log().all();
		return response;
	}
	
	/**
	 * Call REST Web service to get all job filters for specified user, with filter by is_default
	 * 
	 * @author Kiran.Sripada
	 * @return response
	 */
	public Response getJobFiltersForUser(String userID, String token, String additionalURL) {
		String requestUrl = "/users/" + userID + "/jobfilters";
		if((additionalURL != null) && (!additionalURL.equals(""))) {
			requestUrl += "?" + additionalURL;
		
		}
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get(requestUrl);

		response.then().log().all();
		return response;
	}
	
	//Get job filters for logged in user
	/**
	 * Call REST Web service to get all job filters for logged in user
	 * 
	 * @author Kiran.Sripada
	 * @return response
	 */
	public Response getJobFiltersForLoggedInUser() {
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when()
				.get("/user/jobfilters");

		response.then().log().all();
		return response;
	}
	
	
	
	/**
	 * Call REST Web service to get all job filters for logged in user by applying filter on is_default
	 * 
	 * @author Kiran.Sripada
	 * @return response
	 */
	public Response getJobFiltersForLoggedInUser(String additionalURL) {
		
		String requestUrl = "/user/jobfilters";
		if((additionalURL != null) && (!additionalURL.equals(""))) {
			requestUrl += "?" + additionalURL;
		
		}
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when()
				.get(requestUrl);

		response.then().log().all();
		return response;
	}
	
	//Get specified job filter for logged in user
	/**
	 * Call REST Web service to get specified job filter for logged in user
	 * 
	 * @author Kiran.Sripada
	 * @param filterID
	 * @param token
	 * @return response
	 */
	public Response getspecifiedJobFilterForLoggedInUser(String filter_Id,String token) {
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get("/user/jobfilters/"+filter_Id);

		response.then().log().all();
		return response;
	}
	
	/**
	 * Call REST Web service to delete the job filter for a specified filterId
	 * 
	 * @author Kiran.Sripada
	 * @param userID
	 * @param filterID
	 * @param token
	 * @return response
	 */
	public Response deleteJobFilterByFilterID(String userID, String filterID, String token) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.delete("/users/" + userID + "/jobfilters/" + filterID);

		response.then().log().all();
		return response;
	}
	
	/**
	 * Call REST Web service to delete the job filter for a logged in user by filterId
	 * 
	 * @author Kiran.Sripada
	 * @param userID
	 * @param filterID
	 * @param token
	 * @return response
	 */
	public Response deleteJobFilterforloggedInuser(String filterID, String token) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.delete("/user/jobfilters/" + filterID);

		response.then().log().all();
		return response;
	}
	
	/**
	 * Call Rest Web Service to create job columns for specified user
	 * 
	 */
	public  Response createJobColumnsForSpecifiedUser(String user_ID, String token, Map<String, ArrayList<HashMap<String, Object>>> jobColumnInfo, ExtentTest test ) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(jobColumnInfo).when()
				.post("/users/"+user_ID+"/jobs/columns" );

		response.then().log().all();
		
		return response;
	}
	
	/**
	 * Call Rest Web Service to create job columns for logged in user
	 * 
	 */
	public  Response createJobColumnsForLoggedInUser(String token, Map<String, ArrayList<HashMap<String, Object>>> jobColumnInfo, ExtentTest test ) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(jobColumnInfo).when()
				.post("/user/jobs/columns" );
		
		response.then().log().all();
		
		 test.log(LogStatus.INFO,
			        "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + "/user");
		return response;
	}
	
	/**
	 * Call Rest Web Service to get job columns for logged in user
	 * @param token
	 * @return response
	 */
	public Response getJobsColumnsForLoggedInUser(String validToken) {
		// TODO Auto-generated method stub
		
		 System.out.println("token: " + validToken);
		 
		    Response response = given().header("Content-Type", "application/json")
		    		
		        .header("Authorization", "Bearer " + validToken).when().get("/user/jobs/columns");
		    
		    response.then().log().all();
		    
		    return response;
	
	}
	/**
	 * Call Rest Web Service to get job columns for Specified user
	 * @param token
	 * @param user_id
	 * @return response
	 */
	public Response getJobsColumnsForSpecifiedUser(String validToken, String user_Id) {
		// TODO Auto-generated method stub
		
 		 System.out.println("token: " + validToken);
 		 
		    Response response = given().header("Content-Type", "application/json")
		    		
		        .header("Authorization", "Bearer " + validToken).when().get("/users/"+user_Id+"/jobs/columns");
		    
		    response.then().log().all();
		    
		    return response;
	}
	
	/**
	 * Call Rest Web Service to update job columns for logged in user
	 * @param columnInformation -- Hashmap of ArrayList of HashMap, that conatins all the columns
	 * @param token
	 * @return response
	 */
	public  Response updateJobColumnsForLoggedInUser(String token, Map<String, ArrayList<HashMap<String, Object>>> jobColumnInfo, ExtentTest test ) {
		
		Response response = given().header("Content-Type", "application/json")
				
				.header("Authorization", "Bearer " + token).body(jobColumnInfo).when()
				
				.put("/user/jobs/columns" );
		
		response.then().log().all();
		
		 test.log(LogStatus.INFO,
			        "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + "/user");
		return response;
	}
	
	/**
	 * Call Rest Web Service to update job columns for Specified user
	 * @param columnInformation -- Hashmap of ArrayList of HashMap, that conatins all the columns
	 * @param token
	 * @return response
	 */
	public  Response updateJobColumnsForSpecifiedUser(String user_id,String token, Map<String, ArrayList<HashMap<String, Object>>> jobColumnInfo, ExtentTest test ) {
		
		Response response = given().header("Content-Type", "application/json")
				
				.header("Authorization", "Bearer " + token).body(jobColumnInfo).when()
				
				.put("/users/"+user_id+"/jobs/columns" );
		
		response.then().log().all();
		
		 test.log(LogStatus.INFO,
			        "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + "/user");
		return response;
	}
	
	/**
	 * Call REST Web service to delete the job column for a specified userid
	 * 
	 * @author Kiran.Sripada
	 * @param userID
	 * @param token
	 * @return response
	 */
	public Response deleteJobColumnsForSpecifiedUser(String userID, String token) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.delete("/users/" + userID + "/jobs/columns");

		response.then().log().all();
		return response;
	}
	
	/**
	 * Call REST Web service to delete the job column for a logged in user
	 * 
	 * @author Kiran.Sripada
	 * @param token
	 * @return response
	 */
	public Response deleteJobColumnsForLoggedInUser(String token) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.delete("/user/jobs/columns");

		response.then().log().all();
		return response;
	}

	/**
	 * Call REST Web service to createBackUpJobReportsColumnsforLoggedInUser
	 * 
	 * @author Ramya.Nagepalli
	 * @param adminToken
	 * @param test
	 * @return response
	 */
	public Response getOrganizationSupport(String adminToken,String additionalURL, ExtentTest test){
		// TODO Auto-generated method stub
		
		String requestURL="";
		
		if(additionalURL.equals(""))
		{
			requestURL="/organization/support";
		}
		else
		{
			requestURL="/organization/support?"+additionalURL;
		}
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + adminToken).when()
				.get(requestURL);
		response.then().log().all();
		return response;
	}
	/**
	 * Call REST Web service to getOrganizationSupportDetailsForSpecifiedOrganization
	 * 
	 * @author Ramya.Nagepalli
	 * @param adminToken
	 * @param test
	 * @return response
	 */
	public Response getOrganizationSupportDetailsForSpecifiedOrganization(String adminToken, String organization_id,
			ExtentTest test) {
		// TODO Auto-generated method stub
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + adminToken).when()
				.get("/organizations/"+organization_id+"/support/");
		response.then().log().all();
		return response;
	}

	/**
	 * createJobFilter
	 * 
	 * @author Ramya.Nagepalli
	 * @param additionalURL
	 * @param userID
	 * @param jobFilterInfo
	 * @return
	 */
	public Response createJobFilter(String additionalURL, String userID, Map<String, Object> jobFilterInfo) {

		String requestUrl = "/users/" + userID + "/jobfilters";

		if (!(additionalURL.equals(null)) || !(additionalURL.equals("")))
			requestUrl += "?" + additionalURL;

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(jobFilterInfo).when().post(requestUrl);

		response.then().log().all();
		return response;
	}

	/**
	 * updateJobFilter
	 * 
	 * @author Ramya.Nagepalli
	 * @param additionalURL
	 * @param userID
	 * @param filterID
	 * @param jobFilterInfo
	 * @return
	 */
	public Response updateJobFilter(String additionalURL, String userID, String filterID,
			Map<String, Object> jobFilterInfo) {
		// TODO Auto-generated method stub
		String requestUrl = "/users/" + userID + "/jobfilters/" + filterID;

		if (!(additionalURL.equals(null)) || !(additionalURL.equals("")))
			requestUrl += "?" + additionalURL;

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(jobFilterInfo).when().put(requestUrl);

		response.then().log().all();
		return response;
	}
}