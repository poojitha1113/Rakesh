package invoker;

import static io.restassured.RestAssured.given;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class Log4SPOGInvoker {
	private String token;
	public Log4SPOGInvoker(String baseURI, String port) {
		// TODO Auto-generated constructor stub
		 RestAssured.baseURI = baseURI;
		 RestAssured.port = Integer.valueOf(port);
		 RestAssured.basePath = "/api";
	}	  
	public String getToken() {
	    return token;
	}
 
	public void setToken(String token) {
		this.token = token;
	}
	
	/**
    * REST API to create log filter
    * @author shan.jing
    * @param log_id 
    * @param logFilterInfo
    * @param token; site token
    * @return
    */
    public Response createLogFilter(String user_id, Map<String, Object> logFilterInfo ) {
    	Response response = given().header("Content-Type", "application/json")
    	        .header("Authorization", "Bearer " + this.token).body(logFilterInfo).when()
    	        .post("/users/" + user_id + "/logfilters");

	    response.then().log().all();
	    return response;
    }
    
    /**
     * REST API to create log filter
     * @author shan.jing
     * @param log_id 
     * @param logFilterInfo
     * @param token; site token
     * @return
     */
     public Response createLogFilterForLoggedInUser(Map<String, Object> logFilterInfo ) {
     	Response response = given().header("Content-Type", "application/json")
     	        .header("Authorization", "Bearer " + this.token).body(logFilterInfo).when()
     	        .post("/user/logfilters");

 	    response.then().log().all();
 	    return response;
     }
    
    /**
     * REST API to update log filter
     * 
     * @author shan.jing
     * @param user_id
     * @param filter_id
     * @param filterInfo
     * @return
     */
    public Response updateLogFilter(String user_id, String filter_id, Map<String, Object> logFilterInfo) {
      String url = "/users/" + user_id + "/logfilters/" + filter_id;
      Response response = given().header("Content-Type", "application/json")
          .header("Authorization", "Bearer " + this.token).body(logFilterInfo).when()
          .put(url.replace("//", "/"));
      response.then().log().all();
      return response;
    }
    
    /**
     * REST API to update log filter
     * 
     * @author shan.jing
     * @param user_id
     * @param filter_id
     * @param filterInfo
     * @return
     */
    public Response updateLogFilterForLoggedInUser(String filter_id, Map<String, Object> logFilterInfo) {
      String url = "/user/logfilters/" + filter_id;
      Response response = given().header("Content-Type", "application/json")
          .header("Authorization", "Bearer " + this.token).body(logFilterInfo).when()
          .put(url.replace("//", "/"));
      response.then().log().all();
      return response;
    }
    /**
     * Call the Rest API  For getSourceIdLogs
     * @param source_id
     * @param adminToken
     * @param test
     * @return
     */
    /**
     * Call the Rest API  For getSourceIdLogs
     * @param source_id
     * @param adminToken
     * @param test
     * @return
     */
	public Response getSourceIdLogs(String source_id, String adminToken, String extendUrl ,
			ExtentTest test) {
		// TODO Auto-generated method stub
		  
		String url = "/sources/" + source_id + "/logs";
		
		if ((extendUrl != null) && (!extendUrl.equals(""))) {
			url += "?" + extendUrl;
		    }
	    test.log(LogStatus.INFO, "url is " + url);
	    Response response = given()
	    		           . header("Content-Type", "application/json")
	                       .header("Authorization", "Bearer " + adminToken)
	                       .when()
	                       .get(url);
	   
    
	    response.then().log().all();
        test.log(LogStatus.INFO,"The value of the response is:"+response.getBody().asString());
	    return response;
	}
	/**
	 * Call REST API to get all log filters for specified userId
	 * 
	 * @author Kiran.Sripada
	 * @return response
	 */
	public Response getLogFiltersForUser(String userID, String token) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get("/users/" + userID + "/logfilters");

		response.then().log().all();
		return response;
	}
	
	/**
	 * Call REST API to get all log filters for specified userId when apply filter on is_default
	 * 
	 * @author Kiran.Sripada
	 * @return response
	 */
	public Response getLogFiltersForUser(String userID, String token, String additionalURL) {
		String requestUrl = "/users/" + userID + "/logfilters";
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
	 * Call Rest API to create job columns for specified user
	 * 
	 */
	public Response createLogColumnsForSpecifiedUser(String user_ID, String token, Map<String, ArrayList<HashMap<String, Object>>> jobColumnInfo, ExtentTest test ) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(jobColumnInfo).when()
				.post("/users/"+user_ID+"/logs/columns" );

		response.then().log().all();
		
		return response;
	}
	
	/**
	 * Call Rest API to create job columns for specified user
	 * 
	 */
	public Response createLogColumnsForLoggedInUser( String token, Map<String, ArrayList<HashMap<String, Object>>> jobColumnInfo, ExtentTest test ) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(jobColumnInfo).when()
				.post("/user/logs/columns" );

		response.then().log().all();
		
		return response;
	}
	
	/**
	 * Call Rest API to update log columns for specified user
	 * 
	 */
	public Response updateLogColumnsForSpecifiedUser(String user_ID, String token, Map<String, ArrayList<HashMap<String, Object>>> logColumnInfo, ExtentTest test ) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(logColumnInfo).when()
				.put("/users/"+user_ID+"/logs/columns" );

		response.then().log().all();
		
		return response;
	}
	
	
	/**
	 * Call Rest API to update log columns for specified user
	 * 
	 */
	public Response updateLogColumnsForloggedinUser( String token, Map<String, ArrayList<HashMap<String, Object>>> logColumnInfo, ExtentTest test ) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(logColumnInfo).when()
				.put("/user/logs/columns" );

		response.then().log().all();
		
		return response;
	}
	
	
	/**
	 * Call REST API to get specified log filter for specified userId 
	 * @author Kiran.Sripada
	 * @return response
	 */
	public Response getspecifiedLogFilterforspecifieduserID(String userID, String filterID, String token) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get("/users/" + userID + "/logfilters/" + filterID);

		response.then().log().all();
		return response;
	}
	
	
	/**
	 * Call REST API to get all log filters for logged in user
	 * 
	 * @author Kiran.Sripada
	 * @return response
	 */
	public Response getLogFiltersForLoggedInUser(String additionalURL,ExtentTest test) {
		
		//test.log(LogStatus.INFO,"Call the rest API to get the Log Filters for the Logged in user");
		test.log(LogStatus.INFO,
		        "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + "/user/logfilters"); 
          String url = "/user/logfilters";
		
		if ((additionalURL != null) && (!additionalURL.equals(""))) {
			url += "?" + additionalURL;
		    }
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when()
				.get(url);	
		test.log(LogStatus.INFO,"The resposne generated is :"+response.getBody().asString());
		response.then().log().all();
		return response;
	}
	
	//Get specified job filter for logged in user
	/**
	 * Call REST API to get specified log filter for logged in user
	 * 
	 * @author Kiran.Sripada
	 * @param filterID
	 * @param token
	 * @return response
	 */
	public Response getspecifiedLogFilterForLoggedInUser(String filter_Id,String token) {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get("/user/logfilters/"+filter_Id);

		response.then().log().all();
		return response;
	}
	
	
	/**
	 * Call REST API to delete the log filter for a specified filterId
	 * 
	 * @author Kiran.Sripada
	 * @param userID
	 * @param filterID
	 * @param token
	 * @return response
	 */
	public Response deleteLogFilterByFilterID(String userID, String filterID, String token) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.delete("/users/" + userID + "/logfilters/" + filterID);

		response.then().log().all();
		return response;
	}
	
	/**
	 * Call REST API to delete the log filter for a logged in user by specified filterId
	 * 
	 * @author Kiran.Sripada
	 * @param userID
	 * @param filterID
	 * @param token
	 * @return response
	 */
	public Response deleteLogFilterforloggedinuser(String filterID, String token) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.delete("/user/logfilters/" + filterID);

		response.then().log().all();
		return response;
	}
	
	/**
	 * Call REST API to get the system log filter
	 * 
	 * @author Kiran.Sripada
	 * @param token
	 * @return response
	 */
	public Response getSystemLogFilters(String token) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get("/logfilters");

		response.then().log().all();
		return response;
	}
	
	/**
	 * Call REST API to get the log columns for logged in user
	 * 
	 * @author Kiran.Sripada
	 * @param token
	 * @return response
	 */
	public Response getLogColumnsForLoggedInUser(String token) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get("user/logs/columns");

		response.then().log().all();
		return response;
	}
	
	/**
	 * Call REST API to get the log columns for specified user
	 * 
	 * @author Kiran.Sripada
	 * @param token
	 * @return response
	 */
	public Response getLogColumnsForSpecificiedUser(String User_Id,String token) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get("users/"+User_Id+"/logs/columns");

		response.then().log().all();
		return response;
	}
	
	/**
	 * Call REST API to delete the log columns for specified user
	 * 
	 * @author Kiran.Sripada
	 * @param token
	 * @return response
	 */
	public Response deleteLogColumnsForSpecificiedUser(String User_Id,String token) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.delete("users/"+User_Id+"/logs/columns");

		response.then().log().all();
		return response;
	}
	
	/**
	 * Call REST API to delete the log columns for logged in user
	 * 
	 * @author Kiran.Sripada
	 * @param token
	 * @return response
	 */
	public Response deletetLogColumnsForLoggedInUser(String token) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.delete("user/logs/columns");

		response.then().log().all();
		return response;
	}
	
	
	/**
	 * Call REST API to update the log columns for logged in user
	 * 
	 * @author Kiran.Sripada
	 * @param token
	 * @return response
	 */
	public Response updatetLogColumnsForLoggedInUser(HashMap<String,Object> updatelogcolumns,String token) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(updatelogcolumns).when()
				.put("user/logs/columns");

		response.then().log().all();
		return response;
	}
	
	/**
	 * Call REST API to update the log columns for specified user
	 * 
	 * @author Kiran.Sripada
	 * @param token
	 * @return response
	 */
	public Response updatetLogColumnsForSpecifiedUser(String user_Id, HashMap<String,Object> updatelogcolumns,String token) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(updatelogcolumns).when()
				.put("users/"+user_Id+"/logs/columns");

		response.then().log().all();
		return response;
	}
	/**
	 * @author Bharadwaj.Ghadiam
	 * Call the Rest API to export the Logs to CSV
	 * @param admin_Token
	 * @param test
	 * @return
	 */
	public Response exportCsvLogs(String admin_Token, ExtentTest test) {
		// TODO Auto-generated method stub
		String URL="/logs?export=csv";
		test.log(LogStatus.INFO,
		        "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + URL); 
       
		Response response = given()
				.header("Authorization", "Bearer " + admin_Token).when()
				.get(URL);

		response.then().log().all();
		return response;
	}
	
	/**
	 * @author Bharadwaj.Ghadiam
	 * Call the Rest API to export the Logs to CSV
	 * @param admin_Token
	 * @param test
	 * @return
	 */
	public Response exportCsvLogsByOrganizationId(String admin_Token,String organization_id, ExtentTest test) {
		// TODO Auto-generated method stub
		String URL=  "/organizations/"+organization_id+"/logs?export=csv";
		test.log(LogStatus.INFO,
		        "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + URL);       
		Response response = given()
				.header("Authorization", "Bearer " + admin_Token).when()
				.get(URL);
		response.then().log().all();
		return response;
	}
	
	/**
	 * updateLogFilterForLoggedInUser
	 * 
	 * @author Ramya.Nagepalli
	 * @param additionalURL
	 * @param filter_id
	 * @param logFilterInfo
	 * @return
	 */
	public Response updateLogFilterForLoggedInUser(String additionalURL, String filter_id,
			Map<String, Object> logFilterInfo) {

		String requestUrl = "/user/logfilters/" + filter_id;

		if (!((additionalURL.equals(null)) || (additionalURL.equals(""))))
			requestUrl += "?" + additionalURL;

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(logFilterInfo).when()
				.put(requestUrl);
		response.then().log().all();
		return response;
	}

}
