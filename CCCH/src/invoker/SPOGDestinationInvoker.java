package invoker;

import static io.restassured.RestAssured.given;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.hamcrest.core.IsNull;
import org.json.JSONException;
import org.json.JSONObject;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.AuthenticationSpecification;
import io.restassured.specification.RequestSpecification;

public class SPOGDestinationInvoker {
	private String token;
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public SPOGDestinationInvoker(String baseURI, String port) {
		RestAssured.baseURI = baseURI;
		RestAssured.port = Integer.valueOf(port);
		 RestAssured.basePath = "/api";
	}

	/**
	 * call REST web service to create destination filters
	 * 
	 * @author Zhaoguo.Ma
	 * @param userID
	 * @param cloudAccountInfo
	 * @return
	 */
	public Response createDestinationFilter(String userID, Map<String, Object> destinationFilterInfo) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(destinationFilterInfo).when()
				.post("/users/" + userID + "/destinationfilters");

		response.then().log().all();
		return response;
	}
	
	/**
	 * call Rest web service to create destination filter for logged in user
	 * @author Zhaoguo.Ma
	 * @param destinationFilterInfo
	 * @return
	 */
	public Response createDestinationFilterForLoggedinUser(Map<String, Object> destinationFilterInfo) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(destinationFilterInfo).when()
				.post("/user/destinationfilters");

		response.then().log().all();
		return response;

	}

    /**
     * call REST web service to update destination filters
     * 
     * @author yin.li
     * @param 
     * @param destinationFilterInfo
     * @return
     */
    public Response updateDestinationFilter(String userID, String destFilterId, Map<String, Object> destinationFilterInfo) {
        Response response = given().header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + this.token).body(destinationFilterInfo).when()
                .put("/users/" + userID + "/destinationfilters/" + destFilterId);

        response.then().log().all();
        return response;
    }
    
    public Response updateDestinationFilterForLoggedInUser(String destFilterId, Map<String, Object> destinationFilterInfo) {
      Response response = given().header("Content-Type", "application/json")
              .header("Authorization", "Bearer " + this.token).body(destinationFilterInfo).when()
              .put("/user/destinationfilters/" + destFilterId);

      response.then().log().all();
      return response;
  }
	
    
	/**
	 * call REST web service to create destinations
	 * @author leiyu.wang
	 * @param destinationInfo
	 * @return response
	 */
	public Response createDestination( Map<String, Object> destinationInfo){
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(destinationInfo).when()
				.post("/destinations");
		response.then().log().all();
		return response;
	}
	
	/**
	 * recycle direct volume
	 * @author leiyu.wang
	 * @return
	 * @throws JSONException
	 */
	public Response RecycleDirectVolume(String volumeID) {
		
		//post https://tadmin.zetta.net/restapi/volumes/recycle?volume_id=7782528b-1572-4880-bd62-e2b48c7d613c
		RestAssured.useRelaxedHTTPSValidation();
		Response response = given().auth().preemptive().basic("admin@zetta.net","Zetta1234").header("Content-Type", "application/json")
					  //.config((RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation())))		
				//.header("Authorization", "Basic YWRtaW5AemV0dGEubmV0OlpldHRhMTIzNA==")
					  .when().post("https://10.61.4.26/restapi/volumes/recycle?volume_id="+volumeID);      
		
		response.then().log().all();
		return response;
	}
	
    public Response RecycleOrgFromCC(String orgId) {
    	//DELETE https://tadmin.zetta.net/cloudconsole/organizations?organization_id=X
		//post https://tadmin.zetta.net/restapi/volumes/recycle?volume_id=7782528b-1572-4880-bd62-e2b48c7d613c
		RestAssured.useRelaxedHTTPSValidation();
		Response response = given().auth().preemptive().basic("admin@zetta.net","Zetta1234").header("Content-Type", "application/json")
					  //.config((RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation())))		
				//.header("Authorization", "Basic YWRtaW5AemV0dGEubmV0OlpldHRhMTIzNA==")
					  .when().delete("https://10.61.4.26/cloudconsole/organizations?organization_id="+orgId);      
		
		response.then().log().all();
		return response;
	}
		
	/**
	 * call REST web service to get datacenters
	 * @author leiyu.wang
	 * @return response
	 */
	public Response getDestinationDatacenters(){
		Response response = given().header("Content-Type", "application/json")
		        .header("Authorization", "Bearer " + this.token).when().get("/destinations/datacenters");
		
		response.then().log().all();
		return response;
	}
	
	/**
	 * call REST web service to get backuptypes
	 * @author leiyu.wang
	 * @return response
	 */
	public Response getPoliciesBackuptypes(){
		Response response=given().header("Content-Type", "application/json")
		        .header("Authorization", "Bearer " + this.token).when().get("/policies/backuptypes");
		response.then().log().all();
		return response;
	}
	
	/**
	 * call Rest web service to get policy types
	 * @author leiyu.wang
	 * @return response
	 */
	public Response getPolicyTypes(){
		Response response=given().header("Content-Type","application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/policies/types");
		response.then().log().all();
		return response;
	}
	
	/**
	 * Call REST Web service to delete the destination filter for a specified filterId
	 * 
	 * @author Kiran.Sripada
	 * @param filter_Id
	 * @param user_Id
	 * @param token
	 * @return response
	 */
	public Response deletedestinationfilterbyfilterId(String filter_Id, String user_Id, String token) {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.delete("/users/" + user_Id + "/destinationfilters/" + filter_Id);
		response.then().log().all();
		return response;
	}
	
	/**
	 * Call REST Web service to delete the destination filter for logged in user
	 * 
	 * @author Kiran.Sripada
	 * @param filter_Id
	 * @param user_Id
	 * @param token
	 * @return response
	 */
	public Response deletedestinationfilterforLoggedInuser(String filter_Id, String token) {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.delete("/user/destinationfilters/" + filter_Id);
		response.then().log().all();
		return response;
	}
	
	
	/**
	 * Call REST Web service to get the destination filter for a specified filterId
	 * 
	 * @author Kiran.Sripada
	 * @param filter_Id
	 * @param user_Id
	 * @param token
	 * @return response
	 */
	public Response getdestinationfilterbyfilterId(String filter_Id, String user_Id, String token) {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get("/users/" + user_Id + "/destinationfilters/" + filter_Id);
		response.then().log().all();
		return response;
	}
	
	/**
     * Call REST Web service to get the destination filter for a specified filterId
     * 
     * @author yin.li
     * @param userId
     * @param params
     * @return response
     */
    public Response getDestinationFilterByUserId(String userId, HashMap<String, String> params) {

        RequestSpecification aRequestSpecification = given().header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + token);
        if (params != null) {
          aRequestSpecification = aRequestSpecification.params(params);
        }
        Response response = aRequestSpecification.when().get("/users/" + userId + "/destinationfilters/");
        response.then().log().all();
        
        return response;
    }
    
    /**
     * Call REST Web service to get the destination filter for a logged in user
     * 
     * @author kiran.sripada
     * @param filterId
     * @param Token
     * @param extenttest
     * @return response
     */
    public Response getspecifiedDestinationFilterForLoggedInUser(String filter_Id,String Token, ExtentTest test) {

    	/*test.log(LogStatus.INFO,
		        "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + "/user/logfilters");*/ 
          String url = "/user/destinationfilters/"+filter_Id;
		
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + Token).when()
				.get(url);	
		test.log(LogStatus.INFO,"The resposne generated is :"+response.getBody().asString());
        response.then().log().all();
        
        return response;
    }
    
    /**
	 * Call REST Web service to get all destination filters for logged in user with/without applying filter on is_default and filter_name
	 * 
	 * @author Kiran.Sripada
	 * @return response
	 */
	public Response getDestinationFiltersForLoggedInUser(String additionalURL,String token) {
		
		String requestUrl = "/user/destinationfilters";
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
	 * Call REST Web service to delete the destination for a specified destinationId
	 * 
	 * @author Kiran.Sripada
	 * @param destination_Id
	 * @param token
	 * @return response
	 */
	public Response deletedestinationbydestination_Id(String destination_Id, String token) {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.delete("/destinations/" + destination_Id);
		response.then().log().all();
		return response;
	}
	
	/**
	 * Call REST Web service to get the destination info for a specified destinationId
	 * 
	 * @author Kiran.Sripada
	 * @param destination_Id
	 * @param token
	 * @return response
	 */
	public Response updatedestinationbydestination_Id(String destination_Id, Map<String, Object> destinationInfo, String token) {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(destinationInfo)
				.when()
				.put("/destinations/" + destination_Id);
		response.then().log().all();
		return response;
	}
	/**
	 * Call the REST Web Service To create a Destination
	 * @param Token(user JWT )
	 * @param DestinationInfo
	 * @param test
	 * @return response
	 */
	public Response createDestination(String Token,HashMap<String,Object>DestinationInfo,ExtentTest test) {

		test.log(LogStatus.INFO,
				"The URI is " + RestAssured.baseURI + ":" + RestAssured.port + "/destinations");
		Response response= given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + Token)
				.body(DestinationInfo).when().log().uri()
				.post( "/destinations");
		response.then().log().all();
		test.log(LogStatus.INFO,"The response generated is :"+response.getBody().asString());                   
		return response;	
	}
	/**
	 *@author Bharadwaj.Ghadiam
	 * Call the REST Web Services to get the Destinations By id 
	 * @param Token(user JWT )
	 * @param destinationId
	 * @param test
	 * @return response
	 */
	public Response getDestinationById(String Token,String destinationId,ExtentTest test ){
		test.log(LogStatus.INFO,
				"The URI is " + RestAssured.baseURI + ":" + RestAssured.port + "/destinations/"+ destinationId);
		Response response = given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + Token)
				.when()
				.get("/destinations/"+ destinationId);
		response.then().log().all();
		test.log(LogStatus.INFO, "The resonse generated is :" + response.getBody().asString());
		return response;
	}
	/**
	 * Call The REST Web Services To get The Destinations 
	 * @author Bhardawaj.Ghadiam
	 * @param Token(user JWT )
	 * @param additionalUrl
	 * @param test
	 * @return response 
	 */
	public Response getDestinations(String Token, String additionalUrl, ExtentTest test) {
		// TODO Auto-generated method stub
		String requestUrl = "/destinations";
		if ((additionalUrl != null) && (!additionalUrl.equals(""))) {
			requestUrl += "?" + additionalUrl;
		}
		test.log(LogStatus.INFO,
				"The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestUrl);
		Response response = given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + Token)
				.when()
				.get(requestUrl);
		response.then().log().all();
		test.log(LogStatus.INFO, "The response generated is :" + response.getBody().asString());
		return response;
	}
	
	/**
	 * Call The REST Web Services To get The Destinations 
	 * @author Bhardawaj.Ghadiam
	 * @param Token(user JWT )
	 * @param additionalUrl
	 * @param test
	 * @return response 
	 */
	public Response getDestinations(String Token, String additionalUrl) {
		// TODO Auto-generated method stub
		String requestUrl = "/destinations";
		if ((additionalUrl != null) && (!additionalUrl.equals(""))) {
			requestUrl += "?" + additionalUrl;
		}
		Response response = given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + Token)
				.when()
				.get(requestUrl);
		response.then().log().all();
		return response;
	}
	
	
	/**
	 * Call The REST Web Services To get The Destinations 
	 * @author Kiran.Sripada
	 * @param Token(user JWT )
	 * @param additionalUrl
	 * @param test
	 * @return response 
	 */
	public Response getDestinationsbyTypes(String additionalUrl, String token, ExtentTest test) {
		// TODO Auto-generated method stub
		String requestUrl = "/destinations/types";
		if ((additionalUrl != null) && (!additionalUrl.equals(""))) {
			requestUrl += "?" + additionalUrl;
		}
		test.log(LogStatus.INFO,
				"The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestUrl);
		Response response = given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token)
				.when()
				.get(requestUrl);
		response.then().log().all();
		test.log(LogStatus.INFO, "The response generated is :" + response.getBody().asString());
		return response;
	}
	
	/*o GET destinations/columns: Change this to be the default behavior column behavior 
	o DELETE users/:id/destinations/columns: Resets the column preferences to the default by deleting the row 
	o POST users/:id/destinations/columns: Set the user defined column behavior 
	o PUT users/:id/destinations/columns: Update the user defined column behavior 
	o GET users/:id/destinations/columns: Get the user defined column behavior 
	o DELETE user/destinations/columns: Resets the column preferences to the default by deleting the row 
	o POST user/destinations/columns: Set the user defined column behavior for logged in user 
	o PUT user/destinations/columns: Update the user defined column behavior for logged in user 
	o GET user/destinations/columns: Get the user defined column behavior for logged in user */
	
	
	/**
	 * @author Bharadwaj.Ghadiam
	 * @param  User JwtToken
	 * @param user_id
	 * @param DestinationColumnInfo(JSONObject for the requestInfo)
	 * @param test
	 * @return response
	 */
	public Response createDestinationColumnsByUserId(String Token,String user_id,Map<String,ArrayList<HashMap<String, Object>>> DestinationColumnInfo,ExtentTest test ){	
		String requestUrl="/users/"+user_id+"/destinations/columns";
		test.log(LogStatus.INFO,
				"The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestUrl);
		Response response= given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + Token)
				.body(DestinationColumnInfo).when()
				.post(requestUrl);
		response.then().log().all();
		test.log(LogStatus.INFO,"The response generated is :"+response.getBody().asString());                   
		return response;	
	
	}
	
	/**
	 * @author Bharadwaj.Ghadiam
	 * @param User JwtToken
	 * @param user_id
	 * @param test
	 * @return response 
	 */
	public Response getDestinationsColumnsByUserId(String Token,String user_id,ExtentTest test ){	
		String requestUrl="/users/"+user_id+"/destinations/columns";
		test.log(LogStatus.INFO,
				"The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestUrl);
		Response response= given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + Token)
				.when()
				.get(requestUrl);
		response.then().log().all();
		test.log(LogStatus.INFO,"The response generated is :"+response.getBody().asString());                   
		return response;	
	
	}
	/**
	 *  @author Bharadwaj.Ghadiam
	 * @param  User JwtToken
	 * @param user_id
	 * @param test
	 * @return response
	 */
	public Response DeleteDestinationsColumnsByUserId(String Token,String user_id,ExtentTest test ){	
		String requestUrl="/users/"+user_id+"/destinations/columns";
		test.log(LogStatus.INFO,
				"The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestUrl);
		Response response= given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + Token)
			    .when()
				.delete(requestUrl);
		response.then().log().all();
		test.log(LogStatus.INFO,"The response generated is :"+response.getBody().asString());                   
		return response;	
	
	}
	/**
	 *  @author Bharadwaj.Ghadiam
	 * @param  User JwtToken
	 * @param user_id
	 * @param UpdatedDestColumnInfo
	 * @param test
	 * @return response
	 */
	public Response updateDestinationColumnsByUserId(String Token,String user_id,Map<String, ArrayList<HashMap<String, Object>>> UpdatedDestColumnInfo,ExtentTest test ){	
		String requestUrl="/users/"+user_id+"/destinations/columns";
		test.log(LogStatus.INFO,
				"The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestUrl);
		Response response= given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + Token)
				.body(UpdatedDestColumnInfo).when()
				.put(requestUrl);
		response.then().log().all();
		test.log(LogStatus.INFO,"The response generated is :"+response.getBody().asString());                   
		return response;	
	
	}
	
	/**
	 * @author Bharadwaj.Ghadiam 
	 * @param  User JwtToken
	 * @param DestinationColumnInfo
	 * @param test
	 * @return response
	 */
	public Response createDestinationColumnsForLoggedInUser(String Token,Map<String,ArrayList<HashMap<String, Object>>> DestinationColumnInfo,ExtentTest test ){	
		String requestUrl="/user/destinations/columns";
		test.log(LogStatus.INFO,
				"The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestUrl);
		Response response= given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + Token)
				.body(DestinationColumnInfo).when()
				.post(requestUrl);
		response.then().log().all();
		test.log(LogStatus.INFO,"The response generated is :"+response.getBody().asString());                   
		return response;	
	
	}
	/**
	 * @author Bharadwaj.Ghadiam
	 * @param  User JwtToken
	 * @param test
	 * @return response
	 */
	public Response getDestinationsColumnsForLoggedInUser(String Token,ExtentTest test ){	
		String requestUrl="/user/destinations/columns";
		test.log(LogStatus.INFO,
				"The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestUrl);
		Response response= given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + Token)
				.when()
				.get(requestUrl);
		response.then().log().all();
		test.log(LogStatus.INFO,"The response generated is :"+response.getBody().asString());                   
		return response;	
	
	}
	/**
	 * @author Bharadwaj.Ghadiam 
	 * @param  User JwtToken
	 * @param test
	 * @return response
	 */
	public Response DeleteDestinationsColumnsForLoggedInUser(String Token,ExtentTest test ){	
		String requestUrl="/user/destinations/columns";
		test.log(LogStatus.INFO,
				"The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestUrl);
		Response response= given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + Token)
			    .when()
				.delete(requestUrl);
		response.then().log().all();
		test.log(LogStatus.INFO,"The response generated is :"+response.getBody().asString());                   
		return response;	
	
	}
	/**
	 * @author Bharadwaj.Ghadiam 
	 * @param  User JwtToken
	 * @param logcolumnsInfo
	 * @param test
	 * @return response
	 */
	public Response updateDestinationColumnsForLoggedInUser(String Token,Map<String, ArrayList<HashMap<String, Object>>> logcolumnsInfo,ExtentTest test ){	
		String requestUrl="/user/destinations/columns";
		test.log(LogStatus.INFO,
				"The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestUrl);
		Response response= given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + Token)
				.body(logcolumnsInfo).when()
				.put(requestUrl);
		response.then().log().all();
		test.log(LogStatus.INFO,"The response generated is :"+response.getBody().asString());                   
		return response;	
	
	}
	/**
	 * @author Bharadwaj.Ghadiam
	 * Creating a cloud_direct volume for the destination
	 * @param admin_Token
	 * @param destinationInfo
	 * @param cloud_account_id
	 * @param test
	 * @return response 
 	 */

	public Response createCloudDirectDestination(String admin_Token,
			HashMap<String, Object> destinationInfo, String cloud_account_id,ExtentTest test) {
		// TODO Auto-generated method stub
		String requestURL="/clouddirect/"+cloud_account_id+"/destinations";
		test.log(LogStatus.INFO,
				"The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);
		Response response= given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + admin_Token)
				.body(destinationInfo).when()
				.post(requestURL);
		response.then().log().all();
		test.log(LogStatus.INFO,"The response generated is :"+response.getBody().asString());                   
		return response;	
	}
/**
 * @author Bharadwaj.Ghadiam
 * @param admin_Token(User JwtToken)
 * @param destination_id
 * @param test
 * @return count 
 */
	public Response getDestiantionUsageCount(String admin_Token,String destination_id, 
			String additionalURL , ExtentTest test) {
		// TODO Auto-generated method stub
		String requestURL="/destinations/" + destination_id + "/usage";
		if(additionalURL!=null&additionalURL!="") {
			requestURL += "?" + additionalURL;
		}
		test.log(LogStatus.INFO,
				"The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestURL);
		Response response= given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + admin_Token)
				.when()
				.get(requestURL);
		response.then().log().all();
		test.log(LogStatus.INFO,"The response generated is :"+response.getBody().asString());                   
		return response;
	}
	/*
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param organization id
	 * @param test
	 */
	public Response getDestinationTypesForSpecifiedOrganization(String token,String org_id,ExtentTest test) {
		
		String requestURL = "/organizations/"+org_id+"/destinations/types";
		
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" +RestAssured.port+ requestURL);
		Response response = given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token)
				.when()
				.get(requestURL);
		
		response.then().log().all();
		test.log(LogStatus.INFO, "The response generated is : "+response.getBody().toString());
		
		return response;
	}

	
	/*
	 * call REST web service to assign datastores to the destination
	 * 
	 * @author Kiran.Sripada
	 * @param token
	 * @param organization id
	 * @param test
	 */
	public Response assigndatastore(String destination_id,HashMap<String,Object> datastoreInfo, String validToken, ExtentTest test) {
		
		String requestURL = "/destinations/"+destination_id+"/assigndatastore";
		
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" +RestAssured.port+ requestURL);
		Response response = given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + validToken)
				.body(datastoreInfo)
				.when()
				.post(requestURL);
		
		response.then().log().all();
		test.log(LogStatus.INFO, "The response generated is : "+response.getBody().toString());
		
		return response;
	}
	
	/*
	 * call REST web service to unassign datastores from the destination
	 * 
	 * @author Kiran.Sripada
	 * @param token
	 * @param organization id
	 * @param test
	 */
	public Response unassigndatastore(String destination_id, String validToken, ExtentTest test) {
		
		String requestURL = "/destinations/"+destination_id+"/unassigndatastore";
		
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" +RestAssured.port+ requestURL);
		Response response = given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + validToken)
				.when()
				.post(requestURL);
		
		response.then().log().all();
		test.log(LogStatus.INFO, "The response generated is : "+response.getBody().toString());
		
		return response;
	}
	/**
	 * generate files download links for the specified recovery point on udp datastore
	 * @author yuefen.liu
	 * @param destination_id
	 * @param source_id
	 * @param recoverypoint_id
	 * return download_url
	 */
	public Response generateFilesDownloadLinkFromDestinationForSpecifiedRecoveryPoint(String destination_id, String source_id, String recoverypoint_id) {
		String url= "/destinations/"+destination_id+"/sources/"+source_id+"/recoverypoints/"+recoverypoint_id+"/generatelink";
		Response response = given().header("Content-Type", "application/json").header("Authorization", "Bearer " + this.token).when()
				.post(url.replace("//", "/"));
		response.then().log().all();
		
		return response;
		
	}
	/**
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param organization id
	 * @param composedInfo
	 * @param test
	 */
	public Response updateDestinationUsage(String token, String organization_id, ArrayList<Map> composedInfo, ExtentTest test) {
		// TODO Auto-generated method stub
		String requestURL = "/organizations/"+organization_id+"/destinations/usage";
		
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" +RestAssured.port+ requestURL);
		Response response= given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token)
				.body(composedInfo).when()
				.put(requestURL);
		
		response.then().log().all();
		test.log(LogStatus.INFO, "The response generated is : "+response.getBody().toString());
		
		return response;
	}
	
	/**
	 * @author shuo.zhang
	 * @param destinationId
	 * @param token
	 * @return
	 */
	public Response getRecoveryPointsByDestinationId(String destinationId, String token){
		
		String requestURL = "/destinations/"+destinationId+"/sources";
		Response response=given().header("Content-Type", "application/json")
		        .header("Authorization", "Bearer " + token).when().get(requestURL.replace("//", "/"));
		response.then().log().all();
		return response;
		
	}

	
	/**
	 * login as csr admin to check if the default volume created
	 * @author shan, jing
	 * @param orgID
	 * @return response
	 */
	public boolean checkDefaultVolumeCreated(String orgID, String token) {
		// TODO Auto-generated method
		boolean ret=false;
		
		setToken(token);
		Response response=getDestinations(token,"destination_type=cloud_direct_volume&organization_id="+orgID);
		int  total_size=response.then().extract().path("pagination.total_size");
		for(int loop=1;loop<60;loop++){
			if(total_size>=1){
				  ret=true;
				  break;
			}  
			try {
				Thread.sleep(5000);
			
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
		        e.printStackTrace();
			}
			response=getDestinations(token,"destination_type=cloud_direct_volume&organization_id="+orgID);
			total_size=response.then().extract().path("pagination.total_size");
	    }
		response.then().log().all();
		return ret;
	}
	
	/**
	 * @author Rakesh.Chalamala
	 * 
	 * @param volume_id
	 * @return
	 */
	public Response recycleCDVolume(String volume_id) {
		
		//POST https://tadmin.zetta.net/restapi/volumes/recycle?volume_id={DESTINATION ID}
		RestAssured.useRelaxedHTTPSValidation();
		Response response = given().header("Content-Type", "application/json").log().uri()
								.header("Authorization", "Basic YWRtaW5AemV0dGEubmV0IDogWmV0dGExMjM0")
								.when().post("https://10.61.4.26:443/restapi/volumes/recycle?volume_id="+volume_id);      
	
		response.then().log().all();
		return response;
	}
	
	/**
	 * @author Rakesh.Chalamala
	 * 
	 * @param organization_id
	 * @return
	 */
	
	public Response addToCDOrgDeletionQueue(String organization_id) {
		
		//Add the CD organization to the CD deletion queue: DELETE csrapi/orgdeletion/addtoqueue?org_id={ORGANIZATION_ID}
		RestAssured.useRelaxedHTTPSValidation();
		Response response = given().header("Content-Type", "application/json")
									.header("Authorization", "Basic ZDZWaDg5NDRjcjk5WkgyNjl4MjhWODcyOGg3cW11NzQ=")
									.when().delete("https://10.61.4.26/csrapi/orgdeletion/addtoqueue?org_id="+organization_id);      
		
		response.then().log().all();
		return response;
	}
	
	/**
	 * @author Rakesh.Chalamala
	 * 
	 * @param organization_id
	 * @return
	 */
	public Response deleteCDOrganization(String organization_id) {
		
		//Delete the CD organization: DELETE csrapi/orgdeletion?org_id={ORGANIZATION_ID}
		RestAssured.useRelaxedHTTPSValidation();
		Response response = given().header("Content-Type", "application/json")		
									.header("Authorization", "Basic ZDZWaDg5NDRjcjk5WkgyNjl4MjhWODcyOGg3cW11NzQ=")
									.when().delete("https://10.61.4.26/csrapi/orgdeletion?org_id="+organization_id);      
		
		response.then().log().all();
		return response;
	}
	

	/**
	 * getspecifiedDestinationFilterForLoggedInUser
	 * 
	 * @author Ramya.Nagepalli
	 * @param additionalURL
	 * @param filter_Id
	 * @param Token
	 * @param test
	 * @return
	 */
	public Response getspecifiedDestinationFilterForLoggedInUser(String additionalURL, String filter_Id, String Token,
			ExtentTest test) {

		String requestUrl = "/user/destinationfilters";
		if ((additionalURL != null) && (!additionalURL.equals(""))) {
			requestUrl += "?" + additionalURL;
		}
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestUrl);

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + Token).when().get(requestUrl);
		test.log(LogStatus.INFO, "The response generated is :" + response.getBody().asString());
		response.then().log().all();

		return response;
	}

}