package invoker;

import static io.restassured.RestAssured.given;

import java.util.ArrayList;
import java.util.HashMap;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class SPOGCloudRPSInvoker {
	private String token;


	  public SPOGCloudRPSInvoker(String baseURI, String port) {
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
	   * REST API to create cloud RPS
	   * 
	   * @author Kiran Sripada
	   * @param cloudRPSInfo
	   * @param validToken; user token
	   * @return response
	   */
	  public Response createCloudRPS(HashMap<String,Object> cloudRPSInfo, String validToken) {
		  Response response = given()
				  			 .header("Content-Type", "application/json")
				  			 .header("Authorization", "Bearer " + validToken)
				  			 .body(cloudRPSInfo)
				  			 .when()
				  			 .post("/recoverypointservers");

		    response.then().log().all();
		    return response;
	  }
	  
	  /**
	   * REST API to update cloud RPS
	   * 
	   * @author Kiran Sripada
	   * @param cloudRPSInfo
	   * @param validToken; user token
	   * @return response
	   */
	  public Response updateCloudRPS(String cloudRPS_id, HashMap<String,Object> cloudRPSInfo, String validToken) {
		  Response response = given()
				  			 .header("Content-Type", "application/json")
				  			 .header("Authorization", "Bearer " + validToken)
				  			 .body(cloudRPSInfo)
				  			 .when()
				  			 .put("/recoverypointservers/"+cloudRPS_id);

		    response.then().log().all();
		    return response;
	  }
	  
	  /**
	   * REST API to delete cloud RPS
	   * 
	   * @author Kiran Sripada
	   * @param cloudRPS_id
	   * @param validToken; user token
	   * @return response
	   */
	  public Response deleteCloudRPS(String cloudRPS_id, String validToken) {
		  Response response = given()
				  			 .header("Content-Type", "application/json")
				  			 .header("Authorization", "Bearer " + validToken)
				  			 .when()
				  			 .delete("/recoverypointservers/"+cloudRPS_id);

		    response.then().log().all();
		    return response;
	  }
	  
	  /**
	   * REST API to delete cloud RPS data store
	   * 
	   * @author Kiran Sripada
	   * @param datastore_id
	   * @param validToken; user token
	   * @return response
	   */
	  public Response deleteCloudRPSDataStore(String datastore_id, String validToken) {
		  Response response = given()
				  			 .header("Content-Type", "application/json")
				  			 .header("Authorization", "Bearer " + validToken)
				  			 .when()
				  			 .delete("/datastores/"+datastore_id);

		    response.then().log().all();
		    return response;
	  }
	  
	  /**
	   * REST API to destroy cloud RPS data store
	   * 
	   * @author Rakesh.Chalamala
	   * @param datastore_id
	   * @param validToken; user token
	   * @return response
	   */
	  public Response destroyCloudRPSDataStore(String datastore_id, String validToken) {
		  Response response = given()
				  			 .header("Content-Type", "application/json")
				  			 .header("Authorization", "Bearer " + validToken)
				  			 .when()
				  			 .post("/datastores/"+datastore_id+"/destroy");

		    response.then().log().all();
		    return response;
	  }
	  
	  /*
	   * @author Rakesh.Chalamala
	   * @param cloudRPS_id
	   * @param validToken
	   * @return response
	   */
	  public Response getCloudRPSbyId(String cloudRPS_id, String validToken) {
		  Response response = given()
		  			 .header("Content-Type", "application/json")
		  			 .header("Authorization", "Bearer " + validToken)
		  			 .when()
		  			 .get("/recoverypointservers/"+cloudRPS_id);

		  response.then().log().all();		  
		  return response;
	  }
	  
	  /*
	   * @author Rakesh.Chalamala
	   * @param validToken
	   * @param addittionalURL
	   * @param test
	   * @return response
	   */
	  public Response getCloudRPS(String validToken, String additionalURL, ExtentTest test) {
		 
		  String requestUrl = "/recoverypointservers";
			if((additionalURL != null) && (!additionalURL.equals(""))) {
				requestUrl += "?" + additionalURL;
			}
		  test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestUrl);
		  Response response = given()
		  			 .header("Content-Type", "application/json")
		  			 .header("Authorization", "Bearer " + validToken)
		  			 .when()
		  			 .get(requestUrl);

		  response.then().log().all();		  
		  return response;
	  }
	  
	  /**
	   * REST API to create cloud RPS data store
	   * 
	   * @author Kiran Sripada
	   * @param cloudRPSInfo
	   * @param validToken; user token
	   * @return response
	   */
	  public Response createCloudRPSDataStore(HashMap<String,Object> datastoreInfo, String validToken) {
		  Response response = given()
				  			 .header("Content-Type", "application/json")
				  			 .header("Authorization", "Bearer " + validToken)
				  			 .body(datastoreInfo)
				  			 .when()
				  			 .post("/datastores");

		    response.then().log().all();
		    return response;
	  }
	  
	  /**
	   * REST API to update cloud RPS data store
	   * 
	   * @author Kiran Sripada
	   * @param cloudRPSInfo
	   * @param validToken; user token
	   * @return response
	   */
	  public Response updateCloudRPSDataStore(HashMap<String,Object> datastoreInfo, String datastore_id, String validToken) {
		  String requestUrl = "/datastores/"+datastore_id;
		  Response response = given()
				  			 .header("Content-Type", "application/json")
				  			 .header("Authorization", "Bearer " + validToken)
				  			 .body(datastoreInfo)
				  			 .when()
				  			 .put(requestUrl);

		    response.then().log().all();
		    return response;
	  }
	  
	  /*
	   * REST API to Get Cloud RPS data store
	   * 
	   * @author Rakesh.Chalamala
	   * @param token
	   * @param additionalURL
	   * @param test
	   * @return response
	   */
	  public Response getCloudRPSDataStore(String token, String additionalURL, ExtentTest test) {
		  	
		  String requestUrl = "/datastores";
			if((additionalURL != null) && (!additionalURL.equals(""))) {
				requestUrl += "?" + additionalURL;
			}
		  test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestUrl);
		  Response response = given()
		  			 .header("Content-Type", "application/json")
		  			 .header("Authorization", "Bearer " + token)
		  			 .when()
		  			 .get(requestUrl);
		  response.then().log().all();
		  return response;
	  }
	  
	  /*
	   * REST API to Get Cloud RPS data store for specified RPS
	   * 
	   * @author Rakesh.Chalamala
	   * @param token
	   * @param additionalURL
	   * @param rps_server_id
	   * @param test
	   * @return response
	   */
	  public Response getCloudRPSDataStoresForSpecifiedRPS(String token, String rps_server_id, String additionalURL, ExtentTest test) {
		  
		  String requestUrl = "/recoverypointservers/"+rps_server_id+"/datastores";
			if((additionalURL != null) && (!additionalURL.equals(""))) {
				requestUrl += "?" + additionalURL;
			}
		  test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestUrl);
		  Response response = given()
		  			 .header("Content-Type", "application/json")
		  			 .header("Authorization", "Bearer " + token)
		  			 .when()
		  			.get(requestUrl);
		  response.then().log().all();
		  return response;
	  }
	  
	  /*
	   * REST API to Get Cloud RPS data store by datastore id
	   * 
	   * @author Rakesh.Chalamala
	   * @param token
	   * @param datastore_id
	   * @param test
	   * @return response
	   */
	  public Response getCloudRPSDataStoreById(String token, String datastore_id, ExtentTest test) {
		  
		  String requestUrl = "/datastores/"+datastore_id;
		  test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestUrl);
		  Response response = given()
		  			 .header("Content-Type", "application/json")
		  			 .header("Authorization", "Bearer " + token)
		  			 .when()
		  			.get(requestUrl);
		  response.then().log().all();
		  return response;
	  }
	  
	  /* REST API - GET: DATASTORE FOR SPECIFIED DESTINATION 
	   * 
	   * @author Rakesh.Chalamala
	   * @param token
	   * @param destination_id
	   * @param test
	   * @return response
	   */
	  public Response getDatastoreForSpecifiedDestination(String token, String destination_id, ExtentTest test) {
		  
		  String requestURL = "/destinations/"+destination_id+"/datastore";
		  test.log(LogStatus.INFO, "The URI is "+ RestAssured.baseURI+":"+ RestAssured.port+requestURL);
		  Response response = given()
		  			 .header("Content-Type", "application/json")
		  			 .header("Authorization", "Bearer " + token)
		  			 .when()
		  			 .get(requestURL);
		  response.then().log().all();
		  return response;
	  }
	  
	  /* REST API - POST: /datastore/{id}/start
	   * 
	   * @author Rakesh.Chalamala
	   * @param token
	   * @param datastore_id
	   * @param test
	   * @return response
	   */
	  public Response postDataStoreStart(String token, String datastore_id, ExtentTest test) {
		  
		  String requestURL = "/datastores/"+datastore_id+"/start";
		  test.log(LogStatus.INFO, "The URI is "+ RestAssured.baseURI+":"+ RestAssured.port+requestURL);
		  Response response = given()
		  			 .header("Content-Type", "application/json")
		  			 .header("Authorization", "Bearer " + token)
		  			 .when()
		  			 .post(requestURL);
		  response.then().log().all();
		  return response;
	  }
	  
	  /* REST API - POST: /datastore/{id}/stop
	   * 
	   * @author Rakesh.Chalamala
	   * @param token
	   * @param datastore_id
	   * @param test
	   * @return response
	   */
	  public Response postDataStoreStop(String token, String datastore_id, ExtentTest test) {
		  
		  String requestURL = "/datastores/"+datastore_id+"/stop";
		  test.log(LogStatus.INFO, "The URI is "+ RestAssured.baseURI+":"+ RestAssured.port+requestURL);
		  Response response = given()
		  			 .header("Content-Type", "application/json")
		  			 .header("Authorization", "Bearer " + token)
		  			 .when()
		  			 .post(requestURL);
		  response.then().log().all();
		  return response;
	  }
	  
	  /* REST API - PUT: /datastores/usage
	   * 
	   * @authore Rakesh.Chalamala
	   * @param token
	   * @param datastoreInfo
	   * @param test
	   * @return response
	   * 
	   */
	  public Response putDataStoresUsage(String token, ArrayList<HashMap<String, Object>> datastoreInfo, ExtentTest test) {
		  
		  String requestURL = "/datastores/usage";
		  test.log(LogStatus.INFO, "The URI is "+ RestAssured.baseURI+":"+ RestAssured.port+requestURL);
		  Response response = given()
		  			 .header("Content-Type", "application/json")
		  			 .header("Authorization", "Bearer " + token)
		  			 .body(datastoreInfo)
		  			 .when()
		  			 .put(requestURL);
		  response.then().log().all();
		  return response;
	  }
	  
	  /** Create CLOUD HYBRID STORES 
	   *  - Creates a destination of type CH and a datastore with specified volume on RPS and assigns the datastore to destination
	   *  - On creation of this a CH auto policy will be created with name "CH_dest_name + Policy"
	   * 
	   * @param validToken
	   * @param datastoreInfo
	   * @param test
	   * @return
	   */
	  public Response createCloudHybridStore(String validToken, HashMap<String,Object> hybridStoreInfo, ExtentTest test) {
		  
		  String requestURL = "/cloudhybridstores";
		  test.log(LogStatus.INFO, "The URI is "+ RestAssured.baseURI+":"+ RestAssured.port+requestURL);
		  Response response = given().header("Content-Type", "application/json")
				  			 .header("Authorization", "Bearer " + validToken)
				  			 .body(hybridStoreInfo).when().post(requestURL);

		    response.then().log().all();    
		    return response;
	  }
	  
	  /** Get the information of Cloud hybrid store created by its id
	   * 
	   * @param validToken
	   * @param hybridStore_id
	   * @param test
	   * @return
	   */
	  public Response getCloudHybridStoreById(String validToken, String hybridStore_id, ExtentTest test) {
		  
		  String requestURL = "/cloudhybridstores/"+hybridStore_id;
		  test.log(LogStatus.INFO, "The URI is "+ RestAssured.baseURI+":"+ RestAssured.port+requestURL);
		  Response response = given().header("Content-Type", "application/json")
				  			 .header("Authorization", "Bearer " + validToken)
				  			 .when().get(requestURL);

		    response.then().log().all();	    
		    return response;
	  }
	  
	  /** Get all the cloud hybrid stores on specified RPS using rps_server_id
	   * 
	   * @param validToken
	   * @param rps_server_id
	   * @param test
	   * @return
	   */
	  public Response getCloudHybridStoresOnSpecifedRPS(String validToken, String rps_server_id, ExtentTest test) {
		  
		  String requestURL = "/recoverypointservers/"+rps_server_id+"/cloudhybridstores";
		  test.log(LogStatus.INFO, "The URI is "+ RestAssured.baseURI+":"+ RestAssured.port+requestURL);
		  Response response = given().header("Content-Type", "application/json")
				  			 .header("Authorization", "Bearer " + validToken)
				  			 .when().get(requestURL);

		    response.then().log().all();	    
		    return response;
	  }
	  
	  /**Updates the Cloud hybrid store properties 
	   * 
	   * @param validToken
	   * @param hybridStore_id
	   * @param hybridStoreInfo
	   * @param test
	   * @return
	   */
	  public Response updateCloudHybridStoreById(String validToken, String hybridStore_id, HashMap<String, Object> hybridStoreInfo, ExtentTest test) {
		  
		  String requestURL = "/cloudhybridstores/"+hybridStore_id;
		  test.log(LogStatus.INFO, "The URI is "+ RestAssured.baseURI+":"+ RestAssured.port+requestURL);
		  Response response = given().header("Content-Type", "application/json")
				  			 .header("Authorization", "Bearer " + validToken)
				  			 .body(hybridStoreInfo).when().put(requestURL);

		    response.then().log().all();	    
		    return response;
	  }
	  
	  
	  /** Deletes the CH destination, Removes the datastore volume on RPS along with the auto policy created
	   * 
	   * @param validToken
	   * @param hybridStore_id
	   * @param test
	   * @return
	   */
	  public Response destroyCloudHybridStoreById(String validToken, String hybridStore_id, ExtentTest test) {

		  String requestURL = "/cloudhybridstores/"+hybridStore_id+"/destroy";
		  test.log(LogStatus.INFO, "The URI is "+ RestAssured.baseURI+":"+ RestAssured.port+requestURL);
		  Response response = given().header("Content-Type", "application/json")
				  			 .header("Authorization", "Bearer " + validToken)
				  			 .when().delete(requestURL);

		    response.then().log().all();	    
		    return response;	  
	  }
	  
	  /** Start the Cloud hybrid store that is in stopped state
	   * 
	   * @param validToken
	   * @param hybridStoreId
	   * @param test
	   * @return
	   */
	  public Response startCloudHybridStore(String validToken, String hybridStoreId, ExtentTest test) {
		  
		  String requestURL = "/cloudhybridstores/"+hybridStoreId+"/start";
		  test.log(LogStatus.INFO, "The URI is "+ RestAssured.baseURI+":"+ RestAssured.port+requestURL);
		  Response response = given().header("Content-Type", "application/json")
				  			 .header("Authorization", "Bearer " + validToken)
				  			 .when().post(requestURL);

		    response.then().log().all();    
		    return response;
	  }
	  
	  /** Stop the Cloud hybrid Store that is in running state 
	   * 
	   * @param validToken
	   * @param hybridStoreId
	   * @param test
	   * @return
	   */
	  public Response stopCloudHybridStore(String validToken, String hybridStoreId, ExtentTest test) {
		  
		  String requestURL = "/cloudhybridstores/"+hybridStoreId+"/stop";
		  test.log(LogStatus.INFO, "The URI is "+ RestAssured.baseURI+":"+ RestAssured.port+requestURL);
		  Response response = given().header("Content-Type", "application/json")
				  			 .header("Authorization", "Bearer " + validToken)
				  			 .when().post(requestURL);

		  response.then().log().all();    
		  return response;
	  }
	  
	  
}
