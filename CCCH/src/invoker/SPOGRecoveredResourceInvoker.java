package invoker;

import static io.restassured.RestAssured.given;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class SPOGRecoveredResourceInvoker {

	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public SPOGRecoveredResourceInvoker(String baseURI, String port) {
		RestAssured.baseURI = baseURI;
		RestAssured.port = Integer.valueOf(port);
		 RestAssured.basePath = "/api";
	}
	
	/* Get default RECOVERED RESOURCE COLUMNS
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param test
	 * @return response
	 */
	public Response getRecoveredResourceColumns(String token, ExtentTest test) {
		
		Response response = given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token)
			    .when()
				.get("/recoveredresources/columns");
		
		response.then().log().all();
		test.log(LogStatus.INFO,"The response generated is :"+response.getBody().asString());                   
		return response;
	}
	
	/* Get RECOVERED RESOURCE COLUMNS by user id
	 * @author Rakesh.Chalamala
	 * @param userid
	 * @param token
	 * @param test
	 * @return response
	 */
	public Response getRecoveredResourceColumnsByUserId(String userId,String token,ExtentTest test) {
		
		String requestURL = "/users/"+userId+"/recoveredresources/columns";
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);
		
		Response response = given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token)
			    .when()
				.get(requestURL);
		
		response.then().log().all();
		test.log(LogStatus.INFO,"The response generated is :"+response.getBody().asString());                   
		return response;
	}
	
	/* Get RECOVERED RESOURCE COLUMNS for logged in user
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param test
	 * @return response
	 */
	public Response getRecoveredResourceColumnsForLoggedInUser(String token,ExtentTest test) {
		
		String requestURL = "/user/recoveredresources/columns";
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);
		
		Response response = given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token)
			    .when()
				.get(requestURL);
		
		response.then().log().all();
		test.log(LogStatus.INFO,"The response generated is :"+response.getBody().asString());                   
		return response;
	}
	
	/* Create RECOVERED RESOURCE COLUMNS by user id
	 * @author Rakesh.Chalamala
	 * @param userid
	 * @param token
	 * @param ColumnsInfo
	 * @param test
	 * @return response
	 */
	public Response createRecoveredResourceColumnsByUserId(String userId,String token, Map<String,ArrayList<HashMap<String, Object>>> ColumnsInfo, ExtentTest test) {
		
		String requestURL = "/users/"+userId+"/recoveredresources/columns";
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);
		
		Response response = given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token)
			    .body(ColumnsInfo).when()
				.post(requestURL);
		
		response.then().log().all();
		test.log(LogStatus.INFO,"The response generated is :"+response.getBody().asString());                   
		return response;
	}
	
	/* Create RECOVERED RESOURCE COLUMNS for logged in user
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param ColumnsInfo
	 * @param test
	 * @return response
	 */
	public Response createRecoveredResourceColumnsForLoggedInUser(String token,Map<String,ArrayList<HashMap<String, Object>>> ColumnsInfo, ExtentTest test) {
		
		String requestURL = "/user/recoveredresources/columns";
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);
		
		Response response = given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token)
				.body(ColumnsInfo).when()
				.post(requestURL);
		
		response.then().log().all();
		test.log(LogStatus.INFO,"The response generated is :"+response.getBody().asString());                   
		return response;
	}
	
	/* Delete RECOVERED RESOURCE COLUMNS by user id
	 * @author Rakesh.Chalamala
	 * @param userid
	 * @param token
	 * @param test
	 * @return response
	 */
	public Response deleteRecoveredResourceColumnsByUserId(String userId,String token,ExtentTest test) {
		
		String requestURL = "/users/"+userId+"/recoveredresources/columns";
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);
		
		Response response = given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token)
			    .when()
				.delete(requestURL);
		
		response.then().log().all();
		test.log(LogStatus.INFO,"The response generated is :"+response.getBody().asString());                   
		return response;
	}
	
	/* Delete RECOVERED RESOURCE COLUMNS for logged in user
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param test
	 * @return response
	 */
	public Response deleteRecoveredResourceColumnsForLoggedInUser(String token,ExtentTest test) {
		
		String requestURL = "/user/recoveredresources/columns";
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);
		
		Response response = given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token)
				.when()
				.delete(requestURL);
		
		response.then().log().all();
		test.log(LogStatus.INFO,"The response generated is :"+response.getBody().asString());                   
		return response;
	}
	
	/* Update RECOVERED RESOURCE COLUMNS by user id
	 * @author Rakesh.Chalamala
	 * @param userid
	 * @param token
	 * @param ColumnsInfo
	 * @param test
	 * @return response
	 */
	public Response updateRecoveredResourceColumnsByUserId(String userId,String token, Map<String, ArrayList<HashMap<String, Object>>> ColumnsInfo, ExtentTest test) {
		
		String requestURL = "/users/"+userId+"/recoveredresources/columns";
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);
		
		Response response = given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token)
			    .body(ColumnsInfo).when()
				.put(requestURL);
		
		response.then().log().all();
		test.log(LogStatus.INFO,"The response generated is :"+response.getBody().asString());                   
		return response;
	}
	
	/* Update RECOVERED RESOURCE COLUMNS for logged in user
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param ColumnsInfo
	 * @param test
	 * @return response
	 */
	public Response updateRecoveredResourceColumnsForLoggedInUser(String token,Map<String, ArrayList<HashMap<String, Object>>> ColumnsInfo, ExtentTest test) {
		
		String requestURL = "/user/recoveredresources/columns";
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);
		
		Response response = given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token)
				.body(ColumnsInfo).when()
				.put(requestURL);
		
		response.then().log().all();
		test.log(LogStatus.INFO,"The response generated is :"+response.getBody().asString());                   
		return response;
	}
	/**
	 * Call REST Web service to getSystemRecoveredResourcesFilters
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @return response
	 */
	public Response getSystemRecoveredResourcesFilters(String validToken) {
		// TODO Auto-generated method stub
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + validToken).when().get("/recoveredresourcesfilters");

		response.then().log().all();

		return response;
	}

	/**
	 * Call REST Web service to create recovered resources filters for specified user
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @return response
	 */
	public Response createSpecifiedUserRecoveredResourcesFilters(String user_id, String validToken,
			HashMap<String, Object> compose_filter, ExtentTest test) {
		// TODO Auto-generated method stub
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + validToken).body(compose_filter).when()
				.post("/users/"+user_id+"/recoveredresourcesfilters" );

		response.then().log().all();
		
		return response;
	}
	/**
	 * Call REST Web service to delete recovered resources filters for specified user
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @return response
	 */

	public Response deleteSpecifiedUserRecoveredResourcesFiltersByFilterId(String user_id,String filter_id, String validToken, ExtentTest test) {
		// TODO Auto-generated method stub
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + validToken).when()
				.delete("/users/" + user_id + "/recoveredresourcesfilters/"+filter_id);

		response.then().log().all();
		return response;
	}

	/**
	 * Call REST Web service to create recovered resources filters for logged in user
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param compose_filter
	 * @return response
	 */
	public Response createLoggedInUserRecoveredResourcesFilters(String validToken,HashMap<String, Object> compose_filter, ExtentTest test) {
		// TODO Auto-generated method stub
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + validToken).body(compose_filter).when()
				.post("/user/recoveredresourcesfilters/");

		response.then().log().all();
		
		return response;
	}
	
	/**
	 * Call REST Web service to delete recovered resources filters for logged in user
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param filter_id
	 * @return response
	 */

	public Response deleteLoggedInUserRecoveredResourcesFilters(String validToken, String filter_id, ExtentTest test) {
		// TODO Auto-generated method stub
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + validToken).when()
				.delete("/user/recoveredresourcesfilters/"+filter_id);

		response.then().log().all();
		return response;
	}

	/**
	 * Call REST Web service to update recovered resources filters for logged in user
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param filter_id
	 * @param compose_filter
	 * @return response
	 */
	public Response updateSpecifiedUserRecoveredResourcesFilters(String user_id, String validToken, String filter_id,
			HashMap<String, Object> compose_filter1, ExtentTest test) {
		// TODO Auto-generated method stub
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + validToken).body(compose_filter1).when()
				.put("/users/"+user_id+"/recoveredresourcesfilters/"+filter_id);

		response.then().log().all();
		
		return response;
	}

	/**
	 * Call REST Web service to update recovered resources filters for logged in user
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param filter_id
	 * @param compose_filter
	 * @return response
	 */
	public Response updateLoggedInUserRecoveredResourcesFilters(String validToken, String filter_id,
			HashMap<String, Object> compose_filter1, ExtentTest test) {
		// TODO Auto-generated method stub
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + validToken).body(compose_filter1).when()
				.put("/user/recoveredresourcesfilters/"+filter_id);

		response.then().log().all();
		
		return response;
	}
	/**
	 * Call REST Web service to get recovered resources filters for specified user
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param user_id
	 * @return response
	 */
	public Response getSpecifiedUserRecoveredResourcesFilters(String validToken, String user_id, ExtentTest test) {
		// TODO Auto-generated method stub
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + validToken).when()
				.get("/users/" + user_id + "/recoveredresourcesfilters/");

		response.then().log().all();
		return response;
	}

	/**
	 * Call REST Web service to get recovered resources filters for specified user
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param user_id
	 * @param filter_id
	 * @return response
	 */
	public Response getSpecifiedUserRecoveredResourcesFilterByFilterId(String validToken, String user_id,
			String filter_id, ExtentTest test) {
		// TODO Auto-generated method stub
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + validToken).when()
				.get("/users/" + user_id + "/recoveredresourcesfilters/"+filter_id);

		response.then().log().all();
		return response;
	}

	/**
	 * Call REST Web service to get recovered resources filters for logged in user
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @return response
	 */
	public Response getLoggedInUserRecoveredResourcesFilters(String validToken, ExtentTest test) {
		// TODO Auto-generated method stub
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + validToken).when()
				.get("/user/recoveredresourcesfilters/");

		response.then().log().all();
		return response;
	}
	/**
	 * Call REST Web service to get recovered resources filter by filter_id for logged in user
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param filter_id
	 * @return response
	 */
	public Response getLoggedInUserRecoveredResourcesFilterByFilterId(String validToken, String filter_id,
			ExtentTest test) {
		// TODO Auto-generated method stub
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + validToken).when()
				.get("/user/recoveredresourcesfilters/"+filter_id);

		response.then().log().all();
		return response;
	}


}
