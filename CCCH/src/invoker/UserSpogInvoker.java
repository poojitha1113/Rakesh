package invoker;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class UserSpogInvoker {
	private String token;

	public String getToken() {

		return token;
	}

	public void setToken(String token) {

		this.token = token;
	}

	public UserSpogInvoker(String baseURI, String port) {

		RestAssured.baseURI = baseURI;
		RestAssured.port = Integer.valueOf(port);
		RestAssured.basePath = "/api";
	}

	/**
	 * Call REST Web service to authenticate user
	 * 
	 * @author Yuefen.liu
	 * @param userInfo
	 * @param Token
	 *            (cloud direct JWT)
	 * @return response
	 */
	public Response AuthenticateUser(Map<String, String> userInfo, String Token) {

		// RestAssured.baseURI = baseURI + "/users/authenticate";

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + Token).body(userInfo).when().post("/users/authenticate");

		response.then().log().all();
		return response;
	}

	/**
	 * delete specified user' picture w
	 * 
	 * @author shuo.zhang
	 * @param userId
	 * @param token
	 * @return
	 */
	public Response deleteUsersPicture(String userId, String token) {

		String url = "/users/" + userId + "/picture";
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().delete(url.replace("//", "/"));
		return response;
	}

	/**
	 * delete specified user' picture without login
	 * 
	 * @author shuo.zhang
	 * @param userId
	 * @return
	 */
	public Response deleteUsersPictureWithoutLogin(String userId) {

		String url = "/users/" + userId + "/picture";
		Response response = given().header("Content-Type", "application/json").when().delete(url);
		return response;
	}

	/**
	 * @author shuo.zhang
	 * @param token
	 * @return
	 */
	public Response deleteLoggedInUserPicture(String token) {

		String url = "/user/picture";
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().delete(url);
		return response;
	}

	/**
	 * @author shuo.zhang
	 * @return
	 */
	public Response deleteLoggedInUserPictureWithoutLogin() {

		String url = "/user/picture";
		Response response = given().header("Content-Type", "application/json").when().delete(url);
		return response;
	}

	/**
	 * upload picture for logged in user
	 * 
	 * @author Zhaoguo.Ma
	 * @param picturePath
	 * @return
	 */
	public Response uploadPictureForLoginUser(String picturePath) {
		Response response = null;
		if (picturePath != "") {
			response = given().multiPart("picture", new File(picturePath))
					.header("Authorization", "Bearer " + this.token).when().post("/user/picture");
		} else {
			response = given().header("Content-Type", "multipart/form-data")
					.header("Authorization", "Bearer " + this.token).when().post("/user/picture");
		}

		response.then().log().all();
		return response;
	}

	/**
	 * upload picture for user by ID
	 * 
	 * @author Zhaoguo.Ma
	 * @param userID
	 * @param picturePath
	 * @return
	 */
	public Response uploadPictureByUserID(String userID, String picturePath) {
		Response response = null;
		if (picturePath != "") {
			response = given().multiPart("picture", new File(picturePath))
					.header("Authorization", "Bearer " + this.token).when().post("/users/" + userID + "/picture");
		} else {
			response = given().header("Content-Type", "multipart/form-data")
					.header("Authorization", "Bearer " + this.token).when().post("/users/" + userID + "/picture");
		}

		response.then().log().all();
		return response;
	}

	/**
	 * @author Zhaoguo.Ma
	 * @param filterInfo
	 * @return
	 */
	public Response createHypervisorFilterForLoggedinUser(Map<String, Object> filterInfo) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(filterInfo).when()
				.post("/user/hypervisorfilters");

		response.then().log().all();
		return response;
	}

	/**
	 * @author Zhaoguo.Ma
	 * @param userID
	 * @param filterInfo
	 * @return
	 */
	public Response createHypervisorFilterForUser(String userID, Map<String, Object> filterInfo) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(filterInfo).when()
				.post("/users/" + userID + "/hypervisorfilters");

		response.then().log().all();
		return response;
	}

	/**
	 * @author Zhaoguo.Ma
	 * @param filterID
	 * @param filterInfo
	 * @return
	 */
	public Response updateHypervisorFilterForLoggedinUser(String filterID, Map<String, Object> filterInfo) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(filterInfo).when()
				.put("/user/hypervisorfilters/" + filterID);

		response.then().log().all();
		return response;
	}

	/**
	 * @author Zhaoguo.Ma
	 * @param userID
	 * @param filterID
	 * @param filterInfo
	 * @return
	 */
	public Response updateHypervisorFilterForSpecificUser(String userID, String filterID,
			Map<String, Object> filterInfo) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(filterInfo).when()
				.put("/users/" + userID + "/hypervisorfilters/" + filterID);

		response.then().log().all();
		return response;
	}

	/**
	 * @author Zhaoguo.Ma
	 * @param filterID
	 * @param filterInfo
	 * @return
	 */
	public Response deleteHypervisorFilterForLoggedinUser(String filterID) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().delete("/user/hypervisorfilters/" + filterID);

		response.then().log().all();
		return response;
	}

	/**
	 * @author Zhaoguo.Ma
	 * @param userID
	 * @param filterID
	 * @param filterInfo
	 * @return
	 */
	public Response deleteHypervisorFilterForSpecificUser(String userID, String filterID) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when()
				.delete("/users/" + userID + "/hypervisorfilters/" + filterID);

		response.then().log().all();
		return response;
	}

	/**
	 * @author Zhaoguo.Ma
	 * @param filterID
	 * @param filterInfo
	 * @return
	 */
	public Response getHypervisorFilterForLoggedinUser(String filterID) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/user/hypervisorfilters/" + filterID);

		response.then().log().all();
		return response;
	}

	/**
	 * @author Zhaoguo.Ma
	 * @param userID
	 * @param filterID
	 * @param filterInfo
	 * @return
	 */
	public Response getHypervisorFilterForSpecificUser(String userID, String filterID) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when()
				.get("/users/" + userID + "/hypervisorfilters/" + filterID);

		response.then().log().all();
		return response;
	}

	/**
	 * @author Zhaoguo.Ma
	 * @param filterID
	 * @param filterInfo
	 * @return
	 */
	public Response getHypervisorFiltersForLoggedinUser() {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/user/hypervisorfilters");

		response.then().log().all();
		return response;
	}

	public Response getHypervisorFiltersForLoggedinUser(String extentURL) {
		String requestURL = "/user/hypervisorfilters";

		if ((extentURL != null) && (!extentURL.equals(""))) {
			requestURL += "?" + extentURL;
		}
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get(requestURL);

		response.then().log().all();
		return response;
	}

	/**
	 * @author Zhaoguo.Ma
	 * @param userID
	 * @param filterID
	 * @param filterInfo
	 * @return
	 */
	public Response getHypervisorFiltersForSpecificUser(String userID) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/users/" + userID + "/hypervisorfilters");

		response.then().log().all();
		return response;
	}

	public Response getHypervisorFiltersForSpecificUser(String userID, String extentURL) {
		String requestURL = "/users/" + userID + "/hypervisorfilters";

		if ((extentURL != null) && (!extentURL.equals(""))) {
			requestURL += "?" + extentURL;
		}
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get(requestURL);

		response.then().log().all();
		return response;
	}

	public Response getSystemHypervisorfilters() {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/hypervisorfilters");

		response.then().log().all();
		return response;

	}

	
	/**
	 * Call the Rest API to create the user filters
	 * @author Kiran.Sripada
	 * @param userID
	 * @param userfilterInfo
	 * @return
	 */
	public Response createUserFilterForUser(String userID, HashMap<String, Object> filterInfo, String token) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(filterInfo).when()
				.post("/users/" + userID + "/userfilters");

		response.then().log().all();
		return response;
	}
	
	/**
	 * Call the Rest API to update the user filters
	 * @author Kiran.Sripada
	 * @param userID
	 * @param userfilterInfo
	 * @return
	 */
	public Response updateUserFilterForUser(String userID, String filter_Id, HashMap<String, Object> filterInfo, String token) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(filterInfo).when()
				.put("/users/" + userID + "/userfilters/"+filter_Id);

		response.then().log().all();
		return response;
	}
	
	/**
	 * Call the Rest API to create the user filters for logged in user
	 * @author Kiran.Sripada
	 * @param userID
	 * @param userfilterInfo
	 * @return
	 */
	public Response createUserFilterForLoggedInUser(HashMap<String, Object> filterInfo) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(filterInfo).when()
				.post("/user/userfilters");

		response.then().log().all();
		return response;
	}
	
	/**
	 * Call the Rest API to update the user filters for logged in user
	 * @author Kiran.Sripada
	 * @param userID
	 * @param userfilterInfo
	 * @return
	 */
	public Response updateUserFilterForLoggedInUser(String filter_Id,HashMap<String, Object> filterInfo) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(filterInfo).when()
				.put("/user/userfilters/"+filter_Id);

		response.then().log().all();
		return response;
	}
	
	/**
	 * Call the Rest API to get the user filter for logged in user
	 * @author Kiran.Sripada
	 * @param filterID
	 * @param token
	 * @return response
	 */
	public Response getUserFilterForLoggedInUser(String filter_Id, String token) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get("/user/userfilters/"+filter_Id);

		response.then().log().all();
		return response;
	}
	
	/**
	 * Call the Rest API to get the user filter for specified in user
	 * @author Kiran.Sripada
	 * @param user_Id
	 * @param filter_Id
	 * @param token
	 * @return response
	 */
	public Response getUserFilterForSpecifiedUser(String user_Id, String filter_Id, String token) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get("/users/" +user_Id +"/userfilters/"+filter_Id);

		response.then().log().all();
		return response;
	}
	
	/**
	 * Call the Rest API to get the user filters for specified in user
	 * @author Kiran.Sripada
	 * @param user_Id
	 * @param filter_Id
	 * @param token
	 * @return response
	 */
	public Response getUserFiltersForSpecifiedUser(String user_Id, String extentURL, String token) {
		
		String requestURL = "/users/" +user_Id +"/userfilters";

		if ((extentURL != null) && (!extentURL.equals(""))) {
			requestURL += "?" + extentURL;
		}
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get(requestURL);

		response.then().log().all();
		return response;
	}
	
	
	/**
	 * Call the Rest API to get the user filters for logged in user
	 * @author Kiran.Sripada
	 * @param user_Id
	 * @param filter_Id
	 * @param token
	 * @return response
	 */
	public Response getUserFiltersForLoggedInUser(String extentURL, String token) {
		
		String requestURL = "/user/userfilters";

		if ((extentURL != null) && (!extentURL.equals(""))) {
			requestURL += "?" + extentURL;
		}
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get(requestURL);

		response.then().log().all();
		return response;
	}
	/**
	 * Call the Rest API to get the system user filters
	 * @author Kiran.Sripada
	 * @return response
	 */
	public Response getSystemUserfilters() {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/userfilters");

		response.then().log().all();
		return response;

	}

	public Response getUserColumns(ExtentTest test) {
		// TODO Auto-generated method stub
		    String URL="/users/columns";
            test.log(LogStatus.INFO,"Call the Rest API To Get the Default User columns"+URL);
		    System.out.println("token: " + this.token);
		    Response response = given().header("Content-Type", "application/json")
		        .header("Authorization", "Bearer " + this.token).when().get(URL);
		    response.then().log().all();
		    return response;
		 
	}
	
	/**
	 * Call the Rest API to create the user columns by user id
	 * @author Rakesh.Chalamala
	 * @param userID
	 * @param userColumnInfo
	 * @return
	 */
	public Response createUserColumnByUserId(String token,String user_id,Map<String, ArrayList<HashMap<String, Object>>> userColumnInfo,ExtentTest test)
	{
		String requestUrl = "/users/"+user_id+"/users/columns";
		
		test.log(LogStatus.INFO,"The URI is "+RestAssured.baseURI+":"+RestAssured.port+requestUrl);
		Response response = given().header("Content-Type","application/json")
									.header("Authorization","Bearer "+token)
									.body(userColumnInfo).when().post(requestUrl);
		
		response.then().log().all();
		test.log(LogStatus.INFO, "The response generated is : "+response.getBody().asString());
		return response;
	}
	
	/**
	 * Call the Rest API to update the user columns by user id
	 * @author Rakesh.Chalamala
	 * @param userID
	 * @param userColumnInfo
	 * @return
	 */
	public Response updateUserColumnsByUserId(String token,String user_id,Map<String, ArrayList<HashMap<String, Object>>> updateColumnInfo,ExtentTest test) {
		
		String requestUrl = "/users/"+user_id+"/users/columns";
		
		test.log(LogStatus.INFO, "The URI is "+ RestAssured.baseURI+":"+RestAssured.port+requestUrl);
		Response response = given().header("Content-Type","application/json")
				.header("Authorization","Bearer "+token)
				.body(updateColumnInfo).when().put(requestUrl);
		
		response.then().log().all();
		test.log(LogStatus.INFO, "The response generated is : "+response.getBody().asString());
		return response;
	}
	
	/**
	 * Call the Rest API to create the user columns for logged in user
	 * @author Rakesh.Chalamala
	 * @param userColumnInfo
	 * @return
	 */
	public Response createUserColumnsForLoggedInUser(String token,Map<String, ArrayList<HashMap<String, Object>>> userColumnInfo, ExtentTest test) {
		String requstUrl = "/user/users/columns";
		
		test.log(LogStatus.INFO,"The URI is "+RestAssured.baseURI+":"+RestAssured.port+requstUrl);
		Response response = given().header("Content-Type","application/json")
				.header("Authorization","Bearer "+token).body(userColumnInfo).when().post(requstUrl);
		
		response.then().log().all();
		test.log(LogStatus.INFO, "The response generated is : "+response.getBody().asString());
		return response;
	}
	
	/**
	 * Call the Rest API to update the user columns for logged in user
	 * @author Rakesh.Chalamala
	 * @param userColumnInfo
	 * @return
	 */
	public Response updateUserColumnsForLoggedInUser(String token,Map<String, ArrayList<HashMap<String, Object>>> updateColumnInfo,ExtentTest test) {
		
		String requestUrl = "/user/users/columns";
		
		test.log(LogStatus.INFO, "The URI is "+ RestAssured.baseURI+":"+RestAssured.port+requestUrl);
		Response response = given().header("Content-Type","application/json")
				.header("Authorization","Bearer "+token)
				.body(updateColumnInfo).when().put(requestUrl);
		
		response.then().log().all();
		test.log(LogStatus.INFO, "The response generated is : "+response.getBody().asString());
		return response;
	}
	
	/**
	 * Call the Rest API to delete the user columns by user id
	 * @author Rakesh.Chalamala
	 * @param userID
	 * @return
	 */
	public Response deleteUserColumnsByUserId(String token,String user_id,ExtentTest test){
		String requestUrl = "/users/"+user_id+"/users/columns";
		test.log(LogStatus.INFO, "The URI is "+ RestAssured.baseURI +":"+RestAssured.port+requestUrl);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token)
			    .when()
				.delete(requestUrl);
		response.then().log().all();
		test.log(LogStatus.INFO, "The response generated is :"+response.getBody().asString());
		
		return response;
	}

	/**
	 * Call the Rest API to delete the user columns for logged in user
	 * @author Rakesh.Chalamala
	 * @return
	 */
	public Response deleteUserColumnsForLoggedInUser(String token,ExtentTest test){
		String requestUrl = "/user/users/columns";
		test.log(LogStatus.INFO, "The URI is "+ RestAssured.baseURI +":"+RestAssured.port+requestUrl);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token)
			    .when()
				.delete(requestUrl);
		response.then().log().all();
		test.log(LogStatus.INFO, "The response generated is :"+response.getBody().asString());
		
		return response;
	}
	
	/**
	 * Call the Rest API to get the user columns by user id
	 * @author Rakesh.Chalamala
	 * @param userID
	 * @return
	 */
	public Response getUserColumnsByUserId(String token,String user_id,ExtentTest test) {
		
		String requestUrl = "/users/"+user_id+"/users/columns";
		test.log(LogStatus.INFO, "The URI is "+RestAssured.baseURI+":"+RestAssured.port+requestUrl);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token)
				.when().get(requestUrl);
		response.then().log().all();
		test.log(LogStatus.INFO, "The response generated is :"+response.getBody().asString());
		
		return response;
		
	}
	
	/**
	 * Call the Rest API to get the user columns for logged in user
	 * @author Rakesh.Chalamala
	 * @return
	 */
	public Response getUserColumnsForLoggedInUser(String token,ExtentTest test) {
		String requestUrl = "/user/users/columns";
		test.log(LogStatus.INFO, "The URI is "+RestAssured.baseURI+":"+RestAssured.port+requestUrl);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token)
				.when().get(requestUrl);
		response.then().log().all();
		test.log(LogStatus.INFO, "The response generated is :"+response.getBody().asString());
		
		return response;
	}

	/*
	 * Call the Rest API to delete the user filter by user id 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param user id
	 * @param filter id
	 * @return response
	 */
	public Response deleteUserFilterByUserId(String token,String user_id,String filter_id,ExtentTest test) {
		String requestUrl = "/users/"+user_id+"/userfilters/"+filter_id;
		test.log(LogStatus.INFO, "The URI is "+RestAssured.baseURI+":"+RestAssured.port+requestUrl);
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token)
				.when().delete(requestUrl);
		response.then().log().all();
		test.log(LogStatus.INFO, "The response generated is :"+response.getBody().asString());
		return response;
	}
	
	/*
	 * Call the Rest API to delete the user filter for logged in user 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param filter id
	 * @return response
	 */
	public Response deleteUserFilterForLoggedInUser(String token, String filter_id,ExtentTest test) {
		String requestUrl = "/user/userfilters/"+filter_id;
		test.log(LogStatus.INFO, "The URI is "+RestAssured.baseURI+":"+RestAssured.port+requestUrl);
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token)
				.when().delete(requestUrl);
		response.then().log().all();
		test.log(LogStatus.INFO, "The response generated is :"+response.getBody().asString());
		
		return response;
	}

	  /**
	   * Call REST Web service to assign account admin to msp account
	   * @ parentId
	   * @ childId
	   * @ MspAccountAdmins
	   * @author yuefen.liu
	   * @return response
	   */
	  public Response assignAccountAdmins(String parentId, String childId, Map<String, Object> mspAccountAdmins, String token) {
		  
		  String url = "/organizations/" + parentId + "/accounts/" + childId +"/assignadmins";

	    Response response = given().header("Content-Type", "application/json")
	        .header("Authorization", "Bearer " + token).body(mspAccountAdmins).when()
	        .post(url.replace("//", "/"));
	    response.then().log().all();
	    return response;
	  }
	  /**
	   * Call REST Web service to get all MSP account admins to specific msp sub org
	   * @ parentId
	   * @ childId
	   * @author yuefen.liu
	   * @return response
	   */
	  public Response getAllMspAccountAdmins(String parentId, String childId,String token) {
		  
		  String url = "/organizations/" + parentId + "/accounts/" + childId +"/assignadmins";

	    Response response = given().header("Content-Type", "application/json")
	        .header("Authorization", "Bearer " + token).when()
	        .get(url.replace("//", "/"));
	    response.then().log().all();
	    return response;
	  }
	  /**
	   * Call REST Web service to unassign  MSP account admins for specific msp sub org
	   * @ parentId
	   * @ childId
	   * @author yuefen.liu
	   * @return response
	   */
	  public Response unAssignAccountAdmins(String parentId, String childId, Map<String, Object> mspAccountAdmins, String token) {
		  
		  String url = "/organizations/" + parentId + "/accounts/" + childId +"/unassignadmins";

	    Response response = given().header("Content-Type", "application/json")
	        .header("Authorization", "Bearer " + token).body(mspAccountAdmins).when()
	        .post(url.replace("//", "/"));
	    response.then().log().all();
	    return response;
	  }

	/*
	 * Call the Rest API to reset the user
	 * POST /apis/users/:id/resetpassword , is success, this user's status will be set unverified
	 * Send password reset email, Click link in email to reset password, Link: https://tspog.arcserve.com/users/setpassword?name={name}&email={email}&token={enrollment_token}
	 * @author Eric Yang
	 * @param Userid 
	 * @param token
	 * @return response
	 */
	public Response resetPassword(String userid, String token,ExtentTest test) {
		String requestUrl = "/users/"+userid+"/resetpassword";
		test.log(LogStatus.INFO, "The URI is "+RestAssured.baseURI+":"+RestAssured.port+requestUrl);
		Response response =given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token)
				.when().post(requestUrl);
		response.then().log().all();
		test.log(LogStatus.INFO, "The response generated is :"+response.getBody().asString());
		return response;
	
	}

	  /**
	   * Call REST Web service to get all MSP Sub-Organization assigned to specified MSP Account Admin User
	   * @author yuefen.liu
	   * @return response
	   */
	  public Response getAllSubOrgsAssignedToMspAccountAdmin(String user_id, String token) {
		  String url = "/users/"+ user_id + "/assignedaccounts";
		  String finalUrl = url.replace("//", "/");
		  Response response = given().header("Content-Type", "application/json")
			        .header("Authorization", "Bearer " + token).when()
				    .get(finalUrl);
		  response.then().log().all();
		  return response;
	  }

	

	/**
	 * Call post refresh token api
	 * @author shuo.zhang
	 * @param refreshToken
	 * @return
	 */
	public Response refreshToken(String refreshToken){
		
		String requestUrl = "/user/refreshtoken";
		Response response =given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + refreshToken)
				.when().post(requestUrl);
		response.then().log().all();
		return response;
	}

	/**
	 * @author Zhaoguo.Ma
	 * @param userID
	 * @param filterInfo
	 * @return
	 */
	public Response createPolicyFilterForUser(String userID, Map<String, Object> filterInfo) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(filterInfo).when()
				.post("/users/" + userID + "/policyfilters");

		response.then().log().all();
		return response;
	}

	/**
	 * @author Zhaoguo.Ma
	 * @param filterInfo
	 * @return
	 */
	public Response createPolicyFilterForLoggedinUser(Map<String, Object> filterInfo) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(filterInfo).when()
				.post("/user/policyfilters");

		response.then().log().all();
		return response;
	}
	
	/**
	 * @author Zhaoguo.Ma
	 * @param userID
	 * @param filterID
	 * @param filterInfo
	 * @return
	 */
	public Response updatePolicyFilterForUser(String userID, String filterID, Map<String, Object> filterInfo) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(filterInfo).when()
				.put("/users/" + userID + "/policyfilters/" + filterID);

		response.then().log().all();
		return response;
	}

	/**
	 * @author Zhaoguo.Ma
	 * @param filterID
	 * @param filterInfo
	 * @return
	 */
	public Response updatePolicyFilterForLoggedinUser(String filterID, Map<String, Object> filterInfo) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(filterInfo).when()
				.put("/user/policyfilters/" + filterID);

		response.then().log().all();
		return response;
	}
	
	/**
	 * @author Zhaoguo.Ma
	 * @param userID
	 * @return
	 */
	public Response getPolicyFiltersForUser(String userID) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when()
				.get("/users/" + userID + "/policyfilters");

		response.then().log().all();
		return response;
	}

	/**
	 * @author Zhaoguo.Ma
	 * @return
	 */
	public Response getPolicyFiltersForLoggedinUser() {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when()
				.get("/user/policyfilters");

		response.then().log().all();
		return response;
	}
	
	/**
	 * @author Zhaoguo.Ma
	 * @param userID
	 * @param filterID
	 * @return
	 */
	public Response getSpecificPolicyFilterForUser(String userID, String filterID) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when()
				.get("/users/" + userID + "/policyfilters/" + filterID);

		response.then().log().all();
		return response;
	}

	/**
	 * @author Zhaoguo.Ma
	 * @param filterID
	 * @return
	 */
	public Response getSpecificPolicyFilterForLoggedinUser(String filterID) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when()
				.get("/user/policyfilters/" + filterID);

		response.then().log().all();
		return response;
	}
	
	/**
	 * @author Zhaoguo.Ma
	 * @param userID
	 * @param filterID
	 * @return
	 */
	public Response deleteSpecificPolicyFilterForUser(String userID, String filterID) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when()
				.delete("/users/" + userID + "/policyfilters/" + filterID);

		response.then().log().all();
		return response;
	}

	/**
	 * @author Zhaoguo.Ma
	 * @param filterID
	 * @return
	 */
	public Response deleteSpecificPolicyFilterForLoggedinUser(String filterID) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when()
				.delete("/user/policyfilters/" + filterID);

		response.then().log().all();
		return response;
	}
	
	/**
	 * @author Zhaoguo.Ma
	 * @return
	 */
	public Response getSystemPolicyFilters() {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when()
				.get("/policyfilters");

		response.then().log().all();
		return response;
	}
	
	/**
	 * @author Zhaoguo.Ma
	 * @param userID
	 * @param extentURL
	 * @return
	 */
	public Response getPolicyFiltersForSpecificUser(String userID, String extentURL) {
		String requestURL = "/users/" + userID + "/policyfilters";

		if ((extentURL != null) && (!extentURL.equals(""))) {
			requestURL += "?" + extentURL;
		}
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get(requestURL);

		response.then().log().all();
		return response;
	}

	/**
	 * @author Zhaoguo.Ma
	 * @param extentURL
	 * @return
	 */
	public Response getPolicyFiltersForLoggedinUser(String extentURL) {
		String requestURL = "/user/policyfilters";

		if ((extentURL != null) && (!extentURL.equals(""))) {
			requestURL += "?" + extentURL;
		}
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get(requestURL);

		response.then().log().all();
		return response;
	}

	/* API - POST: /users/search 
	 * 
	 * @author Rakesh.Chalamala
	 * @param searchInfo
	 * @param token
	 * @return response
	 */
	public Response postUsersSearch(HashMap<String, Object> searchInfo, String token) {
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token)
				.body(searchInfo).when()
				.post("/users/search");
		
		response.then().log().all();
		return response;
	}


	  /**
	   * Call REST Web service to get all recovered resources.
	   * @author yuefen.liu
	   * @return response
	   */
	  public Response getRecoveredResources(String extendUrl, String token) {
		    String requestUrl = "/recoveredresources";
			if ((extendUrl != null) && (!extendUrl.equals(""))) {
				requestUrl += "?" + extendUrl;
			}
			
			Response response = given().header("Content-Type", "application/json")
					.header("Authorization", "Bearer " + token).when()
					.get(requestUrl);

			response.then().log().all();

			return response;
	  }
	  /**
	   * Call REST Web service to get the recovered resource by ID.
	   * @author yuefen.liu
	   * @return response
	   */
	  public Response getRecoveredResourceById(String recoveredId, String token) {
		    String requestUrl = "/recoveredresources" + recoveredId;
			Response response = given().header("Content-Type", "application/json")
					.header("Authorization", "Bearer " + token).when()
					.get(requestUrl.replace("//", "/"));

			response.then().log().all();

			return response;
	  }
	  /**
	   * Call REST Web service to create order for logged in user
	   * @author yuefen.liu
	   * @param orderInfo
	   * @return response
	   */
	  public Response addOrderForLoggedInUser(Map<String, String> orderInfo) {
		    String url = "/organization/orders";
			Response response = given().header("Content-Type", "application/json")
					.header("Authorization", "Bearer " + this.token).body(orderInfo).when()
					.post(url);

			response.then().log().all();

			return response;
	  }
	  /**
	   * Call REST Web service to create order for specified organization
	   * @author yuefen.liu
	   * @param orgId
	   * @param orderInfo
	   * @return response
	   */
	  public Response addOrderByOrgId(String orgId, Map<String, String> orderInfo) {
		    String url = "/organizations/"+ orgId +"/orders";
			Response response = given().header("Content-Type", "application/json")
					.header("Authorization", "Bearer " + this.token).body(orderInfo).when()
					.post(url.replace("//", "/"));

			response.then().log().all();

			return response;
	  }
	  /**
	   * Call REST Web service to get orders for logged in user
	   * @author yuefen.liu
	   * @return response
	   */
	  public Response getOrdersForLoggedInUser() {
		    String url = "/organization/orders";
			Response response = given().header("Content-Type", "application/json")
					.header("Authorization", "Bearer " + this.token).when()
					.get(url);

			response.then().log().all();

			return response;
	  }
	  /**
	   * Call REST Web service to get orders for specified organization
	   * @author yuefen.liu
	   * @param orgId
	   * @return response
	   */
	  public Response getOrderByOrgId(String orgId) {
		    String url = "/organizations/"+ orgId +"/orders";
			Response response = given().header("Content-Type", "application/json")
					.header("Authorization", "Bearer " + this.token).when()
					.get(url.replace("//", "/"));

			response.then().log().all();

			return response;
	  }

	  /**
	   * Call REST Web service to get orders by order id for logged in user
	   * @author yuefen.liu
	   * @param orderId
	   * @return response
	   */
	  public Response getOrderByIdForLoggedInUser(String orderId) {
		    String url = "/organization/orders/"+ orderId;
			Response response = given().header("Content-Type", "application/json")
					.header("Authorization", "Bearer " + this.token).when()
					.get(url);

			response.then().log().all();

			return response;
	  }
	  /**
	   * Call REST Web service to get order by rorder id for specified organization
	   * @author yuefen.liu
	   * @param orgId
	   * @param orderId
	   * @return response
	   */
	  public Response getOrderByIdForSpecifiedOrg(String orgId, String orderId ) {
		    String url = "/organizations/"+ orgId +"/orders/"+orderId;
			Response response = given().header("Content-Type", "application/json")
					.header("Authorization", "Bearer " + this.token).when()
					.get(url.replace("//", "/"));

			response.then().log().all();

			return response;
	  }
		/**
		 * @author Eric.Yang
		 * @param token
		 * @param test
		 * @return
		 */
		public Response getUsersAmount(String token, ExtentTest test){
			String url="/users"+"/amount";
			String finalUrl =  url.replace("//", "/");
			//this.setToken(token);
		    System.out.println("Token :" + token);
		    test.log(LogStatus.INFO,
		        "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + finalUrl);
		    Response response=null;
		    if (token.isEmpty())
		    	 response = given().header("Content-Type", "application/json")
		        .header("Authorization", "" + token).when().get(finalUrl);
		    else
		     response = given().header("Content-Type", "application/json")
		        .header("Authorization", "Bearer " + token).when().get(finalUrl);
		    	

		    return response;		
			
			
		}
		
		
		/**
		 * @author shuo.zhang
		 * @param userId
		 * @param accountInfo
		 * @param token
		 * @param test
		 * @return
		 */
		public Response assignAccount(String userId, Map<String, Object > accountInfo, String token, ExtentTest test){
			
			String url = "/users/" + userId + "/assignaccounts";
			String finalUrl =  url.replace("//", "/");
			Response response = given().header("Content-Type", "application/json")
					 .header("Authorization", "Bearer " + token).body(accountInfo).when().post(finalUrl);

			response.then().log().all();
			return response;		
			
		}
		
		/**
		 *  @author shuo.zhang
		 * @param userId
		 * @param accountInfo
		 * @param test
		 * @return
		 */
		public Response assignAccountWithoutLogin(String userId, Map<String, Object > accountInfo, ExtentTest test){
			
			String url = "/users/" + userId + "/assignaccounts";
			String finalUrl =  url.replace("//", "/");
			Response response = given().header("Content-Type", "application/json")
				.body(accountInfo).when().post(finalUrl);

			response.then().log().all();
			return response;		
			
		}
		
		/**
		 * @author shuo.zhang
		 * @param email
		 * @param emailInfo
		 * @return
		 */
		public Response forgetPassword(String email, Map<String, Object> emailInfo){
			
			String url = "/user/resetpassword";

			Response response = given().header("Content-Type", "application/json")
				.body(emailInfo).when().post(url);

			response.then().log().all();
			return response;		
		}
}
