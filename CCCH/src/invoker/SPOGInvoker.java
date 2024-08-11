package invoker;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class SPOGInvoker {

	private String token;

	public String getToken() {

		return token;
	}

	public void setToken(String token) {

		this.token = token;
	}

	public SPOGInvoker(String baseURI, String port) {
		RestAssured.baseURI = baseURI;
		RestAssured.port = Integer.valueOf(port);
		RestAssured.basePath = "/api";

	}

	public String getBaseURL() {

		return RestAssured.baseURI;
	}

	public int getPort() {

		return RestAssured.port;
	}

	public void setBaseURL(String spog_baseURL) {

		RestAssured.baseURI = spog_baseURL;
	}

	public void setPort(int spog_port) {

		RestAssured.port = spog_port;
	}

	public Response checkSwagDocIsActive(String swag_baseURL, int swag_port) {

		String spog_baseURL = getBaseURL();
		int spog_port = getPort();
		setBaseURL(swag_baseURL);
		setPort(swag_port);
		Response response = given().header("Content-Type", "application/json").when().get("/apidocs/index.html");
		response.then().log().all();
		setBaseURL(spog_baseURL);
		setPort(spog_port);
		return response;
	}

	/**
	 * Call REST Web service to login SPOG
	 * 
	 * @author shuo.zhang
	 * @param userInfo:
	 *            login user info
	 * @return response
	 * 
	 * 
	 */
	public Response login(Map<String, String> userInfo) {

		Response response = given().header("Content-Type", "application/json").body(userInfo).when()
				.post("/users/login");

		response.then().log().all();
		return response;
	}

	/**
	 * Call REST Web service to login SPOG
	 * 
	 * @author kiran.sripada
	 * @param userInfo:
	 *            login user info
	 * @param test:
	 *            Object of ExtentTest
	 * @return response
	 * 
	 * 
	 */
	public Response login(Map<String, String> userInfo, ExtentTest test) {

		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + "/users/login");
		Response response = given().header("Content-Type", "application/json").body(userInfo).when()
				.post("/users/login");

		response.then().log().all();
		return response;
	}

	/**
	 * Call REST Web service to create organization
	 * 
	 * @author shan.jing
	 * @param orgInfo
	 * @return response
	 */
	public Response CreateOrganization(Map<String, Object> orgInfo) {

		// RestAssured.baseURI = baseURI + "/organizations/create";
		System.out.println("token:" + this.token);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(orgInfo).when().post("/organizations");

		response.then().log().all();
		return response;
	}

	/**
	 * Call REST Web service to update threshold
	 * 
	 * @author shan.jing
	 * @param parentId
	 * @param childId
	 * @param thresholdInfo
	 * @return response
	 */
	public Response UpdateAccountThreshold(String parentId, String childId, Map<String, String> thresholdInfo) {
		// replace "" from url
		String url = "/organizations/" + parentId + "/accounts/" + childId + "/threshold";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(thresholdInfo).when().post(finalUrl);
		response.then().log().all();
		return response;
	}

	/**
	 * Call REST Web service to create account
	 * 
	 * @author shan.jing
	 * @param accountInfo
	 * @param urlParentId
	 * @return response
	 */
	public Response CreateAccount(String urlParentId, Map<String, String> accountInfo) {

		// RestAssured.baseURI = baseURI + "/organizations/create";
		System.out.println("token:" + this.token);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(accountInfo).when()
				.post("/organizations/" + urlParentId + "/accounts");
		response.then().log().all();
		return response;
	}

	/**
	 * call REST API to update account information by parent id and org id
	 * 
	 * @author shan.jing
	 * @param parentId
	 * @param orgID
	 * @param updateAccountInfo
	 * @return
	 */

	public Response updateAccountByID(String parentId, String orgID, Map<String, String> updateAccountInfo) {

		System.out.println("token: " + this.token);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(updateAccountInfo).when()
				.put("/organizations/" + parentId + "/accounts/" + orgID);
		response.then().log().all();
		return response;
	}

	/**
	 * Call REST Web service to delete organization by id
	 * 
	 * @author shan.jing
	 * @param orgId
	 * @return response
	 */
	public Response DeleteOrganizationById(String orgId) {

		System.out.println("token:" + this.token);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().delete("/organizations/" + orgId);
		response.then().log().body();
		return response;
	}

	/**
	 * Call REST web service to get organization info by id
	 * 
	 * @author zhaoguo.ma
	 * @param orgID
	 * @return response
	 */
	public Response GetOrganizationInfobyID(String orgID) {

		System.out.println("token: " + this.token);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/organizations/" + orgID);

		response.then().log().all();
		return response;
	}

	/**
	 * Call REST web service to get logged in user's organization info
	 * 
	 * @author shan.jing
	 * @return response
	 */
	public Response GetLoggedInUserOrganizationInfo() {

		System.out.println("token: " + this.token);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/organization");
		response.then().log().all();
		return response;
	}

	/**
	 * Call REST web service to get logged in user's organization info by
	 * Organization ID
	 * 
	 * @author Prasad.Deverakonda
	 * @return response
	 */
	public Response GetLoggedInUserOrganizationInfoByID(String orgID) {

		System.out.println("token: " + this.token);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/organizations/" + orgID);
		response.then().log().all();
		return response;
	}

	/**
	 * Call REST web service to get all users in an given organization
	 * 
	 * @author zhaoguo.ma
	 * @param orgID
	 * @return
	 */
	public Response GetUsersByOrganizationID(String orgID) {

		System.out.println("token: " + this.token);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/organizations/" + orgID + "/users");

		response.then().log().all();
		return response;
	}

	/**
	 * @author zhaoguo.ma Call REST web service to get logged in user
	 *         information
	 * @return response
	 */

	public Response GetLoggedinUserInfo() {

		System.out.println("token: " + this.token);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/user");

		response.then().log().all();
		return response;
	}

	/**
	 * Call REST web service to get user information by ID
	 * 
	 * @author zhaoguo.ma
	 * @param userID
	 * @return
	 */
	public Response GetUserInfoByID(String userID) {

		System.out.println("token: " + this.token);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/users/" + userID);

		response.then().log().all();
		return response;
	}

	/**
	 * Call REST Web service to create user
	 * 
	 * @author shuo.zhang
	 * @param userInfo:
	 *            created user info
	 * @return response
	 * 
	 * 
	 */
	public Response createUser(Map<String, String> userInfo) {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(userInfo).when().post("/users");

		response.then().log().all();
		return response;
	}

	/**
	 * call REST API to update organization information by ID
	 * 
	 * @author zhaoguo.ma
	 * @param orgID
	 * @param updateOrganizationInfo
	 * @return
	 */

	public Response updateOrganizationInfoByID(String orgID, Map<String, String> updateOrganizationInfo) {

		System.out.println("token: " + this.token);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(updateOrganizationInfo).when()
				.put("/organizations/" + orgID);

		response.then().log().all();
		return response;
	}

	/**
	 * call REST API to update organization block status by ID
	 * 
	 * @author shan.jing
	 * @param orgID
	 * @param updateOrganizationInfo
	 * @return
	 */

	public Response updateOrganizationBlockStatusByID(String orgID, Map<String, Object> updateOrganizationInfo) {

		System.out.println("token: " + this.token);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(updateOrganizationInfo).when()
				.put("/organizations/" + orgID);

		response.then().log().all();
		return response;
	}

	/**
	 * call REST api to update organization info for logged in user
	 * 
	 * @author zhaoguo.ma
	 * @param updateOrganizationInfo
	 * @return
	 */

	public Response updateLoggedInOrganization(Map<String, String> updateOrganizationInfo) {

		System.out.println("token: " + this.token);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(updateOrganizationInfo).when()
				.put("/organization");

		response.then().log().all();
		return response;
	}

	/**
	 * call REST api to get organization info for logged in user
	 * 
	 * @author zhaoguo.ma
	 * @param updateOrganizationInfo
	 * @return
	 */
	public Response getLoggedInOrgalization() {

		System.out.println("token: " + this.token);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/organization");

		response.then().log().all();
		return response;
	}

	/**
	 * Call REST Web service to update logged in user
	 * 
	 * @author yuefen.liu
	 * @param updateUserInfo:
	 *            updated user info
	 * @return response
	 */
	public Response updateLoggedInUser(Map<String, String> updateUserInfo) {

		System.out.println("token: " + this.token);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(updateUserInfo).when().put("/user");

		response.then().log().all();
		return response;
	}

	public Response updateLoggedInUserWithBodyNull() {

		System.out.println("token: " + this.token);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().put("/user");

		response.then().log().all();
		return response;
	}

	/**
	 * Call REST Web service to delete user by id
	 * 
	 * @author liu.yuefen
	 * @param user_id
	 * @return response
	 */
	public Response DeleteUserById(String user_id) {

		System.out.println("token:" + this.token);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().delete("/users/" + user_id);
		response.then().log().body();
		return response;
	}

	/**
	 * Call REST Web Service to get the LoggedUser
	 * 
	 * @author Ghadiam.Bharadwaj
	 * @param token
	 * @param test
	 *            : object of extenttest
	 */
	public Response getLoggedinUserInfo(String token, ExtentTest test) {

		System.out.println("Token :" + token);
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + "/user");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().get("/user");

		return response;
	}

	/**
	 * Call REST Web service to update user by id
	 * 
	 * @author liu.yuefen
	 * @param user_id
	 * @param updateUserInfo
	 * @return response
	 */
	public Response updateUserById(String user_id, Map<String, String> updateUserInfo) {

		System.out.println("token:" + this.token);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(updateUserInfo).when().put("/users/" + user_id);

		response.then().log().all();
		return response;
	}

	/**
	 * Call REST Web service to update user by id
	 * 
	 * @author liu.yuefen
	 * @param user_id
	 * @return response
	 */
	public Response updateUserByIdWithBodyNull(String user_id) {

		System.out.println("token:" + this.token);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().put("/users/" + user_id);

		response.then().log().all();
		return response;
	}

	/**
	 * Call REST Web service to update user by id for blockedStatus, only csr's
	 * token can change the blocked status.
	 * 
	 * @author liu.yuefen
	 * @param user_id
	 * @param updateUserInfo
	 * @param token
	 * @return response
	 */
	public Response updateUserByIdForBlockedStatus(String user_id, Map<String, Boolean> updateUserInfo, String token) {

		System.out.println("token:" + this.token);
		String url = "/users/" + user_id;
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().body(updateUserInfo).put(finalUrl);

		response.then().log().all();
		return response;
	}

	/**
	 * Call REST web service to get user information by ID
	 * 
	 * @author BharadwajReddy
	 * @param userID
	 * @return
	 */
	public Response getLoggedUserInfoById(String token, String user_id, ExtentTest test) {

		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + "/users");
		System.out.println("token: " + this.token);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().get("/users/" + user_id);

		response.then().log().all();

		return response;
	}

	public Response getAllUsersInLoggedOrganization(String extendUrl, boolean login, ExtentTest test) {

		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + "/users");
		String requestUrl = "/users";
		if ((extendUrl != null) && (!extendUrl.equals(""))) {
			requestUrl += "?" + extendUrl;
		}
		test.log(LogStatus.INFO, "call getAllUsersInLoggedOrganization request url is " + requestUrl);
		Response response = null;
		if (login == true) {
			response = given().header("Content-Type", "application/json").header("Authorization", "Bearer " + token)
					.when().get(requestUrl);
		} else {
			response = given().header("Content-Type", "application/json").when().get(requestUrl);
		}
		System.out.println(requestUrl);
		response.then().log().all();

		return response;
	}

	// get all users in organization by ID;
	public Response getAllUsersInOrganization(String orgID, String extendUrl, ExtentTest test) {

		test.log(LogStatus.INFO,
				"The URI is " + RestAssured.baseURI + ":" + RestAssured.port + "/organizations/" + orgID + "/users");
		String requestUrl = "/organizations/" + orgID + "/users";
		if ((extendUrl != null) && (!extendUrl.equals(""))) {
			requestUrl += "?" + extendUrl;
		}
		test.log(LogStatus.INFO, "call getAllUsersInLoggedOrganization request url is " + requestUrl);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().get(requestUrl);

		response.then().log().all();

		return response;
	}

	/**
	 * create a new job
	 * 
	 * @author zhaoguo.ma
	 * @param jobInfo
	 * @return
	 */
	public Response createJob(Map<String, Object> jobInfo) {

		System.out.println("token:" + this.token);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(jobInfo).when().post("/jobs");
		response.then().log().body();

		return response;
	}

	/**
	 * create a new job data
	 * 
	 * @author zhaoguo.ma
	 * @param jobID
	 * @param jobInfo
	 * @return
	 */
	public Response createJobData(String jobID, Map<String, Object> dataInfo) {

		System.out.println("token:" + this.token);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(dataInfo).when().post("/jobs/" + jobID + "/data");
		response.then().log().body();

		return response;
	}

	/**
	 * call REST web service API to create a site
	 * 
	 * @author BhardwajReddy
	 * @param Token
	 * @siteInfo(HashMap) @return response
	 */

	public Response createSite(String Token, Map<String, String> siteInfo, ExtentTest test) {

		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + "/sites/link");
		System.out.println("Token:" + token);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(siteInfo).when().post("/sites/link");
		return response;
	}

	/**
	 * call REST API to delete an MSP Account
	 * 
	 * @author zhaoguo.ma
	 * @param mspOrgID
	 * @param accountOrgID
	 * @return
	 */
	public Response deleteMSPAccount(String mspOrgID, String accountOrgID) {

		System.out.println("token:" + this.token);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when()
				.delete("/organizations/" + mspOrgID + "/accounts/" + accountOrgID);
		response.then().log().body();

		return response;
	}

	/**
	 * call REST API to delete an MSP Account
	 * 
	 * @author shan.jing
	 * @param mspOrgID
	 * @return
	 */
	public Response deleteMSPAccounts(String mspOrgID, String[] accounts) {

		System.out.println("token:" + this.token);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(accounts).when()
				.delete("/organizations/" + mspOrgID + "/accounts");
		response.then().log().all();
		return response;
	}

	/**
	 * Call REST Web service to change password for logged in user
	 * 
	 * @author yuefen.liu
	 * @param passwordInfo
	 * @return response
	 */
	public Response ChangePasswordForLoggedInUser(Map<String, String> passwordInfo, ExtentTest test) {

		System.out.println("token:" + this.token);

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(passwordInfo).when().post("/user/changepassword");
		response.then().log().all();

		return response;
	}

	
	/**
	 * Call REST Web service to change password for logged in user
	 * 
	 * @author Nagamalleswari.Sykam
	 * @param passwordInfo
	 * @return response
	 */
	public Response ChangePasswordForLoggedInUser(HashMap<String, Object> passwordInfo, ExtentTest test) {

		System.out.println("token:" + this.token);

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(passwordInfo).when().post("/user/changepassword");
		response.then().log().all();

		return response;
	}
	public Response ChangePasswordForLoggedInUser(Map<String, String> passwordInfo) {

		System.out.println("token:" + this.token);

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(passwordInfo).when().post("/user/changepassword");
		response.then().log().all();

		return response;
	}

	/**
	 * Call REST Web service to change password for specified user
	 * 
	 * @author yuefen.liu
	 * @param user_id
	 * @param passwordInfo
	 * @return response
	 */
	public Response ChangePasswordForSpecifiedUser(String user_id, Map<String, String> passwordInfo, ExtentTest test) {

		System.out.println("token:" + this.token);

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(passwordInfo).when()
				.post("/users/" + user_id + "/changepassword");
		response.then().log().all();

		return response;
	}
	
	/**
	 * @author Nagamalleswari.Sykam
	 * @param user_id
	 * @param passwordInfo
	 * @param test
	 * @return
	 */
	public Response ChangePasswordForSpecifiedUser(String user_id, HashMap<String, Object> passwordInfo, ExtentTest test) {

		System.out.println("token:" + this.token);

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(passwordInfo).when()
				.post("/users/" + user_id + "/changepassword");
		response.then().log().all();

		return response;
	}


	public Response ChangePasswordForSpecifiedUser(String user_id, Map<String, String> passwordInfo) {

		System.out.println("token:" + this.token);

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(passwordInfo).when()
				.post("/users/" + user_id + "/changepassword");
		response.then().log().all();

		return response;
	}

	public Response ChangePasswordForSpecifiedUserWithBodyNull(String user_id, ExtentTest test) {

		System.out.println("token:" + this.token);

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().post("/users/" + user_id + "/changepassword");
		response.then().log().all();

		return response;
	}

	public Response ChangePasswordForSpecifiedUserWithBodyNull(String user_id) {

		System.out.println("token:" + this.token);

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().post("/users/" + user_id + "/changepassword");
		response.then().log().all();

		return response;
	}

	/**
	 * Call REST Web service to get the sub org details by suborgid
	 * 
	 * @author shan.jing
	 * @param parentID
	 * @param suborgId
	 * @param token
	 * @return response
	 */
	public Response getsuborgbyId(String URIparentID, String suborgId, String token) {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.get("/organizations/" + URIparentID + "/accounts/" + suborgId);
		response.then().log().all();
		return response;
	}

	/**
	 * Call REST Web service to get the sub org details
	 * 
	 * @author shan.jing
	 * @param parentID
	 * @param token
	 * @return response
	 */
	public Response getsuborg(String URIparentID, String token) {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().get("/organizations/" + URIparentID + "/accounts/");
		response.then().log().all();
		return response;
	}

	/**
	 * Call REST API to get the userrole list
	 * 
	 * @author Zhaoguo.Ma
	 * @param extentURL
	 * @param test
	 * @return
	 */
	public Response getUserroles(String extentURL, ExtentTest test) {

		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + "/userroles");
		String requestUrl = "/userroles";
		if ((extentURL != null) && (!extentURL.equals(""))) {
			requestUrl += "?" + extentURL;
		}

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().get(requestUrl);

		response.then().log().all();
		return response;
	}

	/**
	 * Call REST Web service to get the audit trail details for an organization
	 * 
	 * @author kiran.sripada
	 * @param token
	 * @param orgId
	 * @param extentURL
	 * @return response
	 */
	public Response getaudittrailbyorgId(String token, String orgId, String extentURL) {

		String requestUrl = "/organizations/" + orgId + "/audittrail";
		if ((extentURL != null) && (!extentURL.equals(""))) {
			requestUrl += "?" + extentURL;
		}
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().get(requestUrl);
		response.then().log().all();
		return response;
	}

	/**
	 * Call REST Web service to get the jobs by jobId in an organization
	 * 
	 * @author kiran.sripada
	 * @param token
	 * @param orgId
	 * @return response
	 */
	public Response getjobsbyJobId(String token, String jobId) {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().get("/jobs/" + jobId);
		response.then().log().all();
		return response;
	}

	/**
	 * @author Zhaoguo.Ma
	 * @param token
	 * @param jobId
	 * @return
	 */
	public Response getjobbyJobId(String jobId) {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().get("/jobs/" + jobId);
		response.then().log().all();
		return response;
	}

	/**
	 * Call REST Web service to get all the jobs/by a specific filter
	 * 
	 * @author kiran.sripada
	 * @param token
	 * @param orgId
	 * @return response
	 */
	public Response getjobs(String token, String additionalURL, ExtentTest test) {

		String requestURL = "/jobs";

		if ((additionalURL != null) && (!additionalURL.equals(""))) {
			requestURL += "?" + additionalURL;
		}

		test.log(LogStatus.INFO, "The URL is :" + RestAssured.baseURI + ":" + RestAssured.port + requestURL);

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().get(requestURL);
		response.then().log().all();
		return response;
	}

	/**
	 * Call REST Web service to get the jobs data by jobId in an organization
	 * 
	 * @author kiran.sripada
	 * @param token
	 * @param orgId
	 * @return response
	 */
	public Response getjobsdatabyJobId(String token, String jobId) {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().get("/jobs/" + jobId + "/data");
		response.then().log().all();
		return response;
	}

	/**
	 * create a new job data
	 * 
	 * @author zhaoguo.ma
	 * @param jobID
	 * @param jobInfo
	 * @return
	 */
	public Response createJobData_1(String jobID, Map<String, Object> dataInfo) {

		System.out.println("token:" + this.token);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(dataInfo).when().post("/jobs/" + jobID + "/data");
		response.then().log().body();

		return response;
	}

	/**
	 * call REST Web Service to getAllAcountsForSpecifiedMsp
	 * 
	 * @author BharadwajReddy
	 * @param Token
	 * @param Msporgid
	 * @return response
	 */

	/*
	 * public Response getallAccounts_SpecifiedMsp(String token,String MspOrgId
	 * ,String organization_name,String type ,long page,int page_size,ExtentTest
	 * test){ test.log(LogStatus.INFO, "The URI is "+RestAssured.baseURI + ":" +
	 * RestAssured.port+"/sites/link"); Response response=null;
	 * if(type.equalsIgnoreCase("create_ts")){ response= given()
	 * .header("Content-Type","application/json") .header("Authorization",
	 * "Bearer "+token) //.body(siteInfo) .when()
	 * .get("/organizations"+MspOrgId+"/accounts?"+"filter=organization_name:"+
	 * organization_name+",sort"+type+",page="+page+"&page_size="+page_size);
	 * }else { response= given() .header("Content-Type","application/json")
	 * .header("Authorization","Bearer "+token) //.body(siteInfo) .when()
	 * .get("/organizations"+MspOrgId+"/accounts?"+"filter=organization_name:"+
	 * organization_name+",sort"+type+",page="+page+"&page_size="+page_size); }
	 * return response; }
	 */

	public Response getallAccountsSpecifiedMsp(String token, String MspOrgid, String additionalUrl, ExtentTest test) {

		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + "/organizations/"
				+ MspOrgid + "/accounts");
		String BaseURL = "/organizations/" + MspOrgid + "/accounts";
		if ((additionalUrl != null) && (!additionalUrl.equals(""))) {
			BaseURL += "?" + additionalUrl;
		}
		test.log(LogStatus.INFO, "The usrl is :" + RestAssured.baseURI + ":" + RestAssured.port + BaseURL);
		test.log(LogStatus.INFO, " " + BaseURL);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().get(BaseURL);

		response.then().log().all();

		return response;
	}

	/**
	 * Call REST Web service to get the audit trail details for an organization
	 * 
	 * @author bharadwaj.ghadiam
	 * @param token
	 * @param userId
	 * @param extentURL
	 * @return response
	 */

	public Response getaudittrailbyuserId(String token, String userId, String extentURL) {

		String requestUrl = "/organizations/" + userId + "/audittrail";
		if ((extentURL != null) && (!extentURL.equals(""))) {
			requestUrl += "?" + extentURL;
		}
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().get(requestUrl);
		response.then().log().all();
		return response;
	}

	/**
	 * Call REST Web service to create group
	 * 
	 * @author Yuefen.liu
	 * @param groupInfo
	 * @return response
	 */
	public Response CreateGroup(Map<String, String> groupInfo) {

		// RestAssured.baseURI = baseURI + "/sources/groups";
		System.out.println("token:" + this.token);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(groupInfo).when().post("/sources/groups");

		response.then().log().all();
		return response;
	}

	/**
	 * Call REST Web service to authenticate user
	 * 
	 * @author Yuefen.liu
	 * @param userInfo
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
	 * REST API to create a filter
	 * 
	 * @author Zhaoguo.Ma
	 * @param user_id
	 * @param filterInfo
	 * @return
	 */
	public Response createFilter(String user_id, Map<String, Object> filterInfo) {

		System.out.println("token: " + this.token);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(filterInfo).when()
				.post("/users/" + user_id + "/sourcefilters");

		response.then().log().all();
		return response;
	}

	/**
	 * REST API to update a filter
	 * 
	 * @author shan.jing
	 * @param user_id
	 * @param filter_id
	 * @param filterInfo
	 * @return
	 */
	public Response updateFilter(String user_id, String filter_id, Map<String, Object> filterInfo) {

		System.out.println("token: " + this.token);
		String url = "/users/" + user_id + "/sourcefilters/" + filter_id;
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(filterInfo).when().put(url.replace("//", "/"));
		response.then().log().all();
		return response;
	}

	/**
	 * REST API to get sources columns
	 * 
	 * @author shan.jing
	 * @return
	 */
	public Response getSourcesColumns() {

		System.out.println("token: " + this.token);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/sources/columns");
		response.then().log().all();
		return response;
	}

	/**
	 * REST API to get destinations columns
	 * 
	 * @author shan.jing
	 * @return
	 */
	public Response getDestinationsColumns() {

		System.out.println("token: " + this.token);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/destinations/columns");
		response.then().log().all();
		return response;
	}

	/**
	 * REST API to get jobs columns
	 * 
	 * @author shan.jing
	 * @return
	 */
	public Response getJobsColumns() {

		System.out.println("token: " + this.token);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/jobs/columns");
		response.then().log().all();
		return response;
	}

	/**
	 * REST API to get logs columns
	 * 
	 * @author shan.jing
	 * @return
	 */
	public Response getLogsColumns() {

		System.out.println("token: " + this.token);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/logs/columns");
		response.then().log().all();
		return response;
	}

	/**
	 * REST API to get specified filter for specified user
	 * 
	 * @author Zhaoguo.Ma
	 * @param user_id
	 * @param filter_id
	 * @return
	 */
	public Response getFilterByID(String user_id, String filter_id) {

		System.out.println("token: " + this.token);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when()
				.get("/users/" + user_id + "/sourcefilters/" + filter_id);

		response.then().log().all();
		return response;
	}

	/**
	 * REST API to get all filters for specified user
	 * 
	 * @author Zhaoguo.Ma
	 * @param user_id
	 * @return
	 */
	public Response getFiltersByUserID(String user_id) {

		System.out.println("token: " + this.token);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/users/" + user_id + "/sourcefilters");

		response.then().log().all();
		return response;
	}

	public Response getSourceFiltersForLoggedInUser() {

		System.out.println("token: " + this.token);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/user/sourcefilters");

		response.then().log().all();
		return response;
	}

	public Response getSourceFilterByIdForLoggedInUser(String filterId) {

		System.out.println("token: " + this.token);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/user/sourcefilters/" + filterId);

		response.then().log().all();
		return response;
	}

	/**
	 * @author Zhaoguo.Ma
	 * @param user_id
	 * @param extendUrl
	 * @param test
	 * @return
	 */
	public Response getFiltersByUserID(String user_id, String extendUrl, ExtentTest test) {

		test.log(LogStatus.INFO,
				"The URI is " + RestAssured.baseURI + ":" + RestAssured.port + "/users" + user_id + "/sourcefilters");
		String requestUrl = "/users/" + user_id + "/sourcefilters";
		if ((extendUrl != null) && (!extendUrl.equals(""))) {
			requestUrl += "?" + extendUrl;
		}
		test.log(LogStatus.INFO, "call getAllUsersInLoggedOrganization request url is " + requestUrl);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().get(requestUrl);

		response.then().log().all();

		return response;
	}

	/**
	 * @author shuo.zhang
	 * @param resouce_id:
	 * @param test
	 * @return
	 */
	public Response getJobsBySourceID(String resouce_id, String extendUrl, boolean isLoggin, ExtentTest test) {

		String url = "/sources/" + resouce_id + "/jobs";
		/*
		 * if(resouce_id==null){ url = "/sources/jobs"; }
		 */

		if ((extendUrl != null) && (!extendUrl.equals(""))) {
			url += "?" + extendUrl;
		}
		test.log(LogStatus.INFO, "url is " + url);
		Response response = null;
		if (isLoggin) {
			response = given().header("Content-Type", "application/json").header("Authorization", "Bearer " + token)
					.when().get(url.replace("//", "/"));
		} else {
			response = given().header("Content-Type", "application/json").when().get(url);
		}

		response.then().log().all();
		System.out.println(url);

		return response;
	}

	/**
	 * @author shuo.zhang
	 * @param sourceInfo
	 * @return
	 */
	public Response createSource(Map<String, Object> sourceInfo, boolean isLoggedIn) {

		Response response = null;
		if (isLoggedIn) {
			response = given().header("Content-Type", "application/json")
					.header("Authorization", "Bearer " + this.token).body(sourceInfo).when().post("/sources");
		} else {
			response = given().header("Content-Type", "application/json").body(sourceInfo).when().post("/sources");
		}

		response.then().log().all();

		return response;
	}

	/**
	 * @author yufen.liu
	 * @param sourceInfo
	 * @return
	 */
	public Response createSourceWithCloudToken(Map<String, Object> sourceInfo, String Token) {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + Token).body(sourceInfo).when().post("/sources");

		response.then().log().all();

		return response;
	}

	/**
	 * REST API to get all jobs for organization
	 * 
	 * @author yin.li
	 * @param
	 * @return
	 */
	public Response getOrganizationJobs(String organizationId, HashMap<String, String> params) {

		Response response = null;
		if (params != null) {
			response = given().header("Content-Type", "application/json")
					.header("Authorization", "Bearer " + this.token).params(params).when()
					.get("/organizations/" + organizationId + "/jobs");
		} else {
			response = given().header("Content-Type", "application/json")
					.header("Authorization", "Bearer " + this.token).when()
					.get("/organizations/" + organizationId + "/jobs");
		}

		response.then().log().all();
		return response;
	}

	/**
	 * call REST Web Service to getalltheaudittrail
	 * 
	 * @author kiran.sripada
	 * @param token
	 * @param orgid
	 * @param filter/sort/pagination
	 *            url
	 * @param extenttest
	 *            object
	 * @return response
	 */
	public Response getaudittrailbyorgId(String token, String orgId, String additionalUrl, ExtentTest test) {

		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + "/organizations/"
				+ orgId + "/audittrail");
		String BaseURL = "/organizations/" + orgId + "/audittrail";
		if ((additionalUrl != null) && (!additionalUrl.equals(""))) {
			BaseURL += "?" + additionalUrl;
		}
		test.log(LogStatus.INFO, "The usrl is :" + RestAssured.baseURI + ":" + RestAssured.port + BaseURL);
		test.log(LogStatus.INFO, " " + BaseURL);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().get(BaseURL);

		response.then().log().all();

		return response;
	}

	/**
	 * REST API to update site info
	 * 
	 * @author kiran.sripada
	 * @param
	 * @return
	 */
	public Response updatesiteInfo(String token, Map<String, String> updateSiteInfo, String SiteID) {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(updateSiteInfo).when().put("/sites/" + SiteID);
		return response;
	}

	/**
	 * REST API to delete site
	 * 
	 * @author kiran.sripada
	 * @param
	 * @return
	 */
	public Response deleteSite(String siteId, String token) {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().delete("/sites/" + siteId);

		// response.then().log().body();

		return response;
	}

	/**
	 * Call REST Web service to get the audit trail details for an organization
	 * 
	 * @author bharadwajReddy
	 * @param token
	 * @param orgId
	 * @param extentURL
	 * @return response
	 */
	public Response getaudittrailbyuserId(String token, String userid, String extentURL, ExtentTest test) {

		String requestUrl = "/users/" + userid + "/audittrail";
		if ((extentURL != null) && (!extentURL.equals(""))) {
			requestUrl += "?" + extentURL;
		}
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().get(requestUrl);
		response.then().log().all();

		return response;
	}

	/**
	 * Call REST Web service to post the source to a source group
	 * 
	 * @author Kiran.Sripada
	 * @param sourcegroupId
	 * @param source_id
	 * @param token
	 * @return response
	 */
	public Response addSourcetoSourceGroup(Map<String, Object> addsourcetogroup, String sourcegroup_Id, String token) {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(addsourcetogroup).when()
				.post("/sources/groups/" + sourcegroup_Id + "/members");
		response.then().log().all();
		return response;
	}

	/**
	 * Call REST Web service to delete the source from a source group
	 * 
	 * @author Kiran.Sripada
	 * @param sourcegroupId
	 * @param source_id
	 * @param token
	 * @return response
	 */
	public Response deleteSourcefromSourceGroup(String sourcegroup_Id, String token,
			HashMap<String, Object> deletesourceId) {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(deletesourceId).when()
				.delete("/sources/groups/" + sourcegroup_Id + "/members");
		response.then().log().all();
		return response;
	}

	/**
	 * Call REST Web service to delete the filter for a specified user
	 * 
	 * @author Kiran.Sripada
	 * @param sourcegroupId
	 * @param source_id
	 * @param token
	 * @return response
	 */
	public Response deletefilterspecifiedbyUserId(String filter_Id, String user_Id, String token) {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.delete("/users/" + user_Id + "/sourcefilters/" + filter_Id);
		response.then().log().all();
		return response;
	}

	/**
	 * Call REST Web service to get the sources in an organization by types
	 * 
	 * @author Kiran.Sripada
	 * @param additionalURL
	 * @param token
	 * @param extenttest
	 *            object
	 * @return response
	 */
	public Response getsourcesbytypes(String additionalUrl, String token, ExtentTest test) {

		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + "/sources/types");
		String BaseURL = "/sources/types";
		if ((additionalUrl != null) && (!additionalUrl.equals(""))) {
			BaseURL += "?" + additionalUrl;
		}
		test.log(LogStatus.INFO, "The url is :" + RestAssured.baseURI + ":" + RestAssured.port + BaseURL);
		test.log(LogStatus.INFO, " " + BaseURL);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().get(BaseURL);

		response.then().log().all();

		return response;
	}

	/**
	 * call REST API to delete group
	 * 
	 * @author yuefen.liu
	 * @param group
	 *            id
	 * @return response
	 */

	public Response DeleteGroup(String group_id) {

		System.out.println("token:" + this.token);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().delete("/sources/groups/" + group_id);

		response.then().log().body();
		return response;
	}

	/**
	 * Call REST Web service to get source groups
	 * 
	 * @author yin.li
	 * @param queryStr
	 * @return response
	 */
	public Response getSourceGroups(String queryStr) {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/sources/groups" + queryStr);

		response.then().log().all();
		return response;
	}

	public Response getSourceGroups(HashMap<String, String> params) {

		RequestSpecification aRequestSpecification = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token);
		if (params != null) {
			aRequestSpecification.params(params);
		}

		Response response = aRequestSpecification.when().get("/sources/groups");

		response.then().log().all();
		return response;
	}

	/**
	 * get the source list for one specified group
	 * 
	 * @author yuefen.liu
	 * @param group
	 *            id
	 * @param extentUrl
	 * @return response
	 */
	public Response getSourceListFromOneGroup(String group_id, String extendUrl, ExtentTest test) {

		String requestUrl = "/sources/groups/" + group_id + "/members";
		if ((extendUrl != null) && (!extendUrl.equals(""))) {
			requestUrl += "?" + extendUrl;
		}

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get(requestUrl.replace("//", "/"));

		response.then().log().all();

		return response;
	}

	/**
	 * @author kiran.sripada
	 * @param sourceInfo
	 * @param source_Id
	 * @param token
	 * @return
	 */
	public Response updateSource(Map<String, Object> sourceInfo, String source_Id, String token) {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(sourceInfo).when().put("/sources/" + source_Id);
		response.then().log().all();

		return response;
	}

	/**
	 * not ready
	 * 
	 * @author shuo.zhang
	 * @param isLoggedIn
	 * @param extendUrl
	 * @param test
	 * @return
	 */
	public Response getSources(String extendUrl, boolean isLoggedIn, ExtentTest test) {

		Response response = null;

		String url = "/sources";
		if ((extendUrl != null) && (!extendUrl.equals(""))) {
			url += "?" + extendUrl;
		}

		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + url);
		System.out.println(url);

		if (isLoggedIn) {
			response = given().header("Content-Type", "application/json")
					.header("Authorization", "Bearer " + this.token).when().get(url);
		} else {
			response = given().header("Content-Type", "application/json").when().get(url);
		}

		response.then().log().all();

		return response;

	}

	/**
	 * @author yin.li
	 * @param source_Id
	 * @param sourceInfo
	 * @return
	 */
	public Response updateSourceGroup(String sourceGroupId, Map<String, String> sourceGroupInfo) {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(sourceGroupInfo).when()
				.put("/sources/groups/" + sourceGroupId);
		response.then().log().all();
		return response;
	}

	/**
	 * Call the REST Web Service to delete the sources by id
	 * 
	 * @author BharadwajGhadiam
	 * @param JwtToken
	 * @param sourceId
	 * @return response
	 */
	public Response deleteSourceById(String token, String sourceId, ExtentTest test) {

		// RestAssured.baseURI = baseURI + "/sources/id";
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + "/sources/" + sourceId);
		System.out.println("token:" + token);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().delete("/sources/" + sourceId);
		response.then().log().all();
		test.log(LogStatus.INFO, "The resonse generated is :" + response.getBody().asString());
		return response;
	}

	public Response deleteSourceByID(String sourceID) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().delete("/sources/" + sourceID);
		response.then().log().all();
		return response;
	}

	/**
	 * Gets the sites.
	 *
	 * @author yin.li
	 * @return the sites
	 */
	public Response getSites() {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().get("/sites/");
		response.then().log().all();
		return response;
	}

	public Response getSites(HashMap<String, String> params) {

		RequestSpecification aRequestSpecification = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token);

		if (params != null) {
			aRequestSpecification.params(params);
		}

		Response response = aRequestSpecification.when().get("/sites/");

		response.then().log().all();
		return response;
	}
	
	/**
	 * getSites - method used to apply filter for the API
	 * 
	 * @author Ramya.Nagepalli
	 * @param additionalURL
	 * @return Response
	 */
	public Response getSites(String additionalURL) {

		String requestURL = "/sites";
		if (!(additionalURL == "")) {
			requestURL += "?" + additionalURL;
		}
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().get(requestURL);

		response.then().log().all();
		return response;
	}

	/**
	 * Call the REST Web service get the sources by id by applying pagination
	 * sorting and filtering
	 * 
	 * @author Bharadwaj.Ghadiam
	 * @param JwtToken
	 *            of organization_id
	 * @param sourceId
	 *            generated while creating a source
	 * @return response
	 */
	public Response getSourcesById(String token, String sourceId, ExtentTest test) {

		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + "/sources/" + sourceId);
		String requestUrl = "/sources/" + sourceId;
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().get(requestUrl);
		response.then().log().all();

		return response;
	}

	public Response getSourcesById(String sourceId, ExtentTest test) {

		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + "/sources/" + sourceId);
		String requestUrl = "/sources/" + sourceId;
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get(requestUrl);
		response.then().log().all();

		return response;
	}

	/**
	 * Call REST Web service to get group by id
	 * 
	 * @author Bharadwaj.Ghadaim
	 * @param groupId
	 * @param JwtToken
	 *            of organizationId
	 * @param Extent
	 *            test
	 * @return response
	 */
	public Response getGroupById(String token, String groupId, ExtentTest test) {

		// RestAssured.baseURI = baseURI + "/sources/groups/id";
		test.log(LogStatus.INFO,
				"The URI is " + RestAssured.baseURI + ":" + RestAssured.port + "/sources/groups/" + groupId);
		System.out.println("token:" + token);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().get("/sources/groups/" + groupId);
		response.then().log().all();
		test.log(LogStatus.INFO, "The resonse generated is :" + response.getBody().asString());
		return response;
	}

	/**
	 * Call the REST API TO create a group
	 * 
	 * @param Token
	 * @param groupInfo
	 * @param test
	 * @return
	 */
	public Response createGroup(String Token, Map<String, String> groupInfo, ExtentTest test) {

		// RestAssured.baseURI = baseURI + "/sources/groups";

		System.out.println("token:" + this.token);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + Token).body(groupInfo).when().post("/sources/groups");

		response.then().log().all();
		test.log(LogStatus.INFO, "the resposne generated is :" + response.getBody().asString());
		return response;
	}
	
	/**
	 * Call the REST API TO create a group
	 * 
	 * @param Token
	 * @param composeSoucreGroupInfo
	 * @param test
	 * @return
	 */
	public Response createGroups(String Token, HashMap<String, Object> composeSoucreGroupInfo, ExtentTest test) {

		// RestAssured.baseURI = baseURI + "/sources/groups";

		System.out.println("token:" + this.token);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + Token).body(composeSoucreGroupInfo).when().post("/sources/groups");

		response.then().log().all();
		test.log(LogStatus.INFO, "the resposne generated is :" + response.getBody().asString());
		return response;
	}

	/**
	 * Call REST API to add a cloud account
	 * 
	 * @author Zhaoguo.Ma
	 * @param cloudAccountInfo
	 * @return
	 */
	public Response addCloudAccount(Map<String, String> cloudAccountInfo) {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(cloudAccountInfo).when().post("/cloudaccounts");

		response.then().log().all();
		return response;
	}

	public Response createCloudAccount(Map<String, String> cloudAccountInfo, String userToken) {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + userToken).body(cloudAccountInfo).when().post("/cloudaccounts");

		response.then().log().all();
		return response;
	}

	/**
	 * call REST API to delete cloud account
	 * 
	 * @author yuefen.liu
	 * @param cloud
	 *            account id
	 * @return response
	 */

	public Response DeleteCloudAccount(String cloud_account_id) {

		System.out.println("token:" + this.token);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().delete("/cloudaccounts/" + cloud_account_id);

		response.then().log().body();
		return response;
	}

	/**
	 * call REST API to login cloud account
	 * 
	 * @author yuefen.liu
	 * @param cloudAccountInfo
	 * @return response
	 */
	public Response CloudDirectAccountLogin(Map<String, String> cloudDirectAccountInfo, ExtentTest test) {

		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + "/cloudaccounts/login");
		Response response = given().header("Content-Type", "application/json").body(cloudDirectAccountInfo).when()
				.post("/cloudaccounts/login");

		response.then().log().all();
		return response;
	}

	/**
	 * call REST API to update cloud account
	 * 
	 * @author yuefen.liu
	 * @param cloudAccountInfo
	 * @return response
	 */
	public Response updateCloudAccount(Map<String, String> cloudAccountInfo, String cloudAccountId, ExtentTest test) {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(cloudAccountInfo).when()
				.put("/cloudaccounts/" + cloudAccountId);

		response.then().log().all();
		return response;
	}

	/**
	 * Call the Rest API To get the cloud Account by id
	 * 
	 * @author BhardawajReddy
	 * @param Token
	 * @param cloud_account_id
	 * @param ExtentTest
	 *            test
	 */
	public Response getCloudAccountById(String Token, String cloud_account_id, ExtentTest test) {

		test.log(LogStatus.INFO,
				"The URI is " + RestAssured.baseURI + ":" + RestAssured.port + "/cloudaccounts/" + cloud_account_id);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + Token).when().get("/cloudaccounts/" + cloud_account_id);
		test.log(LogStatus.INFO, "The response generated is :" + response.getBody().asString());
		response.then().log().all();
		return response;
	}

	/**
	 * Call the rest API to get all the cloudAccounts
	 * 
	 * @author bharadwajReddy
	 * @param Token
	 * @param additionalUrl
	 * @param test
	 * @return response CSR_admin returns all the cloud Accounts MSP_admin
	 *         returns all the cloud Accounts in his organization and the sub
	 *         organization DIRECT_admin returns all the cloud Accounts in his
	 *         organization
	 */

	public Response getCloudAccounts(String Token, String additionalUrl, ExtentTest test) {

		String requestUrl = "/cloudaccounts";
		if ((additionalUrl != null) && (!additionalUrl.equals(""))) {
			requestUrl += "?" + additionalUrl;
		}
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestUrl);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + Token).when().get(requestUrl);
		response.then().log().all();

		return response;
	}

	/**
	 * Call the rest API to delete the logs by log Id
	 * 
	 * @author Kiran.Sripada
	 * @param Token
	 * @param test
	 * @return response
	 */

	public Response deletelogbylogId(String Token, String log_id, ExtentTest test) {

		String requestUrl = "/logs/";
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestUrl);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + Token).when().delete(requestUrl + log_id);
		response.then().log().all();

		return response;
	}

	/**
	 * Call the rest API to get the logs by job Id
	 * 
	 * @author Kiran.Sripada
	 * @param Token
	 * @param test
	 * @return response
	 */

	public Response getjoblogbyjobId(String Token, String job_id, ExtentTest test) {

		String requestUrl = "/jobs/" + job_id + "/" + "logs";
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestUrl);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + Token).when().get(requestUrl);
		response.then().log().all();

		return response;
	}

	/**
	 * Call the rest API to get all the getLogs
	 * 
	 * @author bharadwajReddy
	 * @param Token
	 * @param additionalUrl
	 * @param test
	 * @return response CSR_admin returns all the Logs MSP_admin returns all the
	 *         Logs in his organization and the sub organization DIRECT_admin
	 *         returns all the Logs in his organization
	 */

	public Response getLogs(String token, String additionalUrl, ExtentTest test) {

		// TODO Auto-generated method stub
		String requestUrl = "/logs";
		if ((additionalUrl != null) && (!additionalUrl.equals(""))) {
			requestUrl += "?" + additionalUrl;
		}
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestUrl);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().get(requestUrl);
		response.then().log().all();
		test.log(LogStatus.INFO, "The response generated is :" + response.getBody().asString());
		return response;
	}

	/**
	 * Call the rest API to get all the getLogs by organization_id
	 * 
	 * @author bharadwajReddy
	 * @param Token
	 * @param additionalUrl
	 * @param test
	 * @return response CSR_admin returns all the Logs MSP_admin returns all the
	 *         Logs in his organization and the sub organization DIRECT_admin
	 *         returns all the Logs in his organization
	 */

	public Response getLogsByOrganizationId(String token, String organization_id, String additionalUrl,
			ExtentTest test) {

		// TODO Auto-generated method stub
		String requestUrl = "/organizations/" + organization_id + "/logs";
		if ((additionalUrl != null) && (!additionalUrl.equals(""))) {
			requestUrl += "?" + additionalUrl;
		}
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestUrl);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().get(requestUrl);
		response.then().log().all();
		test.log(LogStatus.INFO, "The response generated is :" + response.getBody().asString());
		return response;
	}

	/**
	 * Call the REST Web service get the log by id
	 * 
	 * @author Bharadwaj.Ghadiam
	 * @param JwtToken
	 *            of organization_id users
	 * @param logId
	 *            generated while creating a source
	 * @return response
	 */
	public Response getLogsById(String token, String log_id, ExtentTest test) {

		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + "/sources/" + log_id);
		String requestUrl = "/logs/" + log_id;
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().get(requestUrl);
		response.then().log().all();

		return response;
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
	 * call REST web service to create order to existing CD order
	 * 
	 * @author yuefen.liu
	 * @param orderInfo
	 * @param cloudAccountID
	 * @return response
	 */
	public Response createOrder(String cloudAccountID, Map<String, String> orderInfo) {

		String url = "/cloudaccounts/" + cloudAccountID + "/orders";

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(orderInfo).when().post(url.replace("//", "/"));

		response.then().log().all();
		return response;
	}

	/**
	 * call REST web service to retrieving the CD order history
	 * 
	 * @author yuefen.liu
	 * @param cloudAccountID
	 * @return response
	 */
	public Response getCloudDirectOrder(String cloudAccountID) {

		String url = "/cloudaccounts/" + cloudAccountID + "/orders";

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get(url.replace("//", "/"));

		response.then().log().all();
		return response;
	}

	// public Response getAgentDownloads(String cloudAccountId){
	//
	// Response response = given().header("Content-Type", "application/json")
	// .header("Authorization", "Bearer " +
	// this.token).when().get("cloudaccounts/"
	// + cloudAccountId +
	// "/agentdownloads");
	//
	// response.then().log().all();
	// return response;
	// }

	public Response getAgentDownloads(String organizationId) {
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().log().uri()
				.get("organizations/" + organizationId + "/agentdownloads");

		response.then().log().all();
		System.out.println("test");
		return response;
	}

	public Response createCloudAccountDestination(String cloudAccountId, HashMap<String, Object> cloudAccountDestinfo) {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(cloudAccountDestinfo).when()
				.post("/clouddirect/" + cloudAccountId + "/destinations");

		response.then().log().all();
		return response;
	}

	public Response getSourceSystemFilters() {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/sourcefilters");

		response.then().log().all();
		return response;
	}

	public Response getDestinationSystemFilters() {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/destinationfilters");

		response.then().log().all();
		return response;
	}

	/**
	 * Call REST Web Service to get the user session
	 * 
	 * @author Eric.Yang
	 * @param token
	 * @param test
	 *            : object of extenttest
	 * @return response
	 */
	public Response getUserSessionInfo(String token, ExtentTest test) {

		System.out.println("Token :" + token);
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + "/api/user/session");
		Response response = null;
		if (token.isEmpty())
			response = given().header("Content-Type", "application/json").header("Authorization", "" + token).when()
					.get("/user/session");
		else
			response = given().header("Content-Type", "application/json").header("Authorization", "Bearer " + token)
					.when().get("/user/session");

		return response;
	}

	/**
	 * @author Zhaoguo.Ma
	 * @param organizationID
	 * @param accountID
	 * @param picturePath
	 * @return
	 */
	public Response uploadPictureforAccount(String organizationID, String accountID, String picturePath) {
		Response response = null;
		if (picturePath != "") {
			response = given().multiPart("picture", new File(picturePath))
					.header("Authorization", "Bearer " + this.token).when()
					.post("/organizations/" + organizationID + "/accounts/" + accountID + "/picture");
		} else {
			response = given().header("Content-Type", "multipart/form-data")
					.header("Authorization", "Bearer " + this.token).when()
					.post("/organizations/" + organizationID + "/accounts/" + accountID + "/picture");
		}

		response.then().log().all();
		return response;
	}

	/**
	 * REST API to get cloudaccounts types
	 * 
	 * @author yuefen.liu
	 * @return
	 */
	public Response getCloudAccountsTypes(String token) {

		System.out.println("token: " + token);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().get("/cloudaccounts/amount");
		response.then().log().all();

		return response;
	}

	/**
	 * REST API to postOrgInfoBySearchString
	 * 
	 * @author ramya.nagepalli
	 * @param searchInfo
	 * @param token
	 * @return
	 */
	public Response postOrgInfoBySearchString(Map<String, Object> searchInfo, String token) {
		// TODO Auto-generated method stub

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(searchInfo).when().post("/organizations/search");

		response.then().log().all();

		return response;
	}

	public Response postOrgInfoBySearchStringWithCsrLogin(Map<String, Object> searchInfo, String token) {
		// TODO Auto-generated method stub
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(searchInfo).when().post("/organizations/search");

		response.then().log().all();

		return response;
	}

	/**
	 * check cloud hybrid in free trial with expected status and error code
	 * 
	 * @author shan.jing
	 * @return
	 */
	public Response checkCloudhybridInFreeTrial(String token, String org_id) {
		// TODO Auto-generated method stub
		String url = "/organizations/" + org_id + "/freetrial/cloudhybrid";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get(finalUrl);
		response.then().log().all();
		return response;

	}

	/**
	 * check cloud direct in free trial with expected status and error code
	 * 
	 * @author shan.jing
	 * @return
	 */
	public Response checkClouddirectInFreeTrial(String token, String org_id) {
		// TODO Auto-generated method stub
		String url = "/organizations/" + org_id + "/freetrial/clouddirect";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get(finalUrl);
		response.then().log().all();
		return response;

	}

	/**
	 * post cloud hybrid in free trial with expected status and error code
	 * 
	 * @author shan.jing
	 * @return
	 */
	public Response postCloudhybridInFreeTrial(String token, String org_id) {
		// TODO Auto-generated method stub
		String url = "/organizations/" + org_id + "/freetrial/cloudhybrid";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().post(finalUrl);
		response.then().log().all();
		return response;

	}

	/**
	 * post cloud direct in free trial with expected status and error code
	 * 
	 * @author shan.jing
	 * @return
	 */
	public Response postClouddirectInFreeTrial(String token, String org_id) {
		// TODO Auto-generated method stub
		String url = "/organizations/" + org_id + "/freetrial/clouddirect";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().post(finalUrl);
		response.then().log().all();
		return response;

	}

	/**
	 * post cloud direct in free trial with expected status and error code
	 * 
	 * @author shan.jing
	 * @return
	 */
	public Response getVersion() {
		// TODO Auto-generated method stub
		String url = "/version";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get(finalUrl);
		response.then().log().all();
		return response;

	}

	public Response getVersionWithoutToken() {
		// TODO Auto-generated method stub
		String url = "/version";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json").when().get(finalUrl);
		response.then().log().all();
		return response;

	}

	public Response postSourcesUpgradeagent(String source_id) {
		// TODO Auto-generated method stub
		String url = "/sources/" + source_id + "/upgradeagent";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().post(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response postSourcesUpgradeagentBulk(String[] bulkBody) {
		// TODO Auto-generated method stub
		String url = "/sources/upgradeagent";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(bulkBody).when().post(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response postSourcesStartbackup(String source_id) {
		// TODO Auto-generated method stub
		String url = "/sources/" + source_id + "/startbackup";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().post(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response postSourcesStartbackupBulk(String[] bulkBody) {
		// TODO Auto-generated method stub
		String url = "/sources/startbackup";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(bulkBody).when().post(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response postSourcesCancelbackup(String source_id) {
		// TODO Auto-generated method stub
		String url = "/sources/" + source_id + "/cancelbackup";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().post(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response postSourcesCancelbackupBulk(String[] bulkBody) {
		// TODO Auto-generated method stub
		String url = "/sources/cancelbackup";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(bulkBody).when().post(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response postSourcesStartrecovery(String source_id) {
		// TODO Auto-generated method stub
		String url = "/sources/" + source_id + "/recovery";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().post(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response postSourcesStartrecoveryBulk(String[] bulkBody) {
		// TODO Auto-generated method stub
		String url = "/sources/recovery";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(bulkBody).when().post(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response postSourcesCancelrecovery(String source_id) {
		// TODO Auto-generated method stub
		String url = "/sources/" + source_id + "/cancelrecovery";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().post(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response postSourcesCancelrecoveryBulk(String[] bulkBody) {
		// TODO Auto-generated method stub
		String url = "/sources/cancelrecovery";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(bulkBody).when().post(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response postSourcesProvision(String source_id) {
		// TODO Auto-generated method stub
		String url = "/sources/" + source_id + "/provision";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().post(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response postSourcesProvisionBulk(String[] bulkBody) {
		// TODO Auto-generated method stub
		String url = "/sources/provision";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(bulkBody).when().post(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response assignPolicy(String policy_id, String[] bulkBody) {
		// TODO Auto-generated method stub
		String url = "/policies/" + policy_id + "/sources";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(bulkBody).when().post(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response assignPolicyBulk(String[] bulkBody) {
		// TODO Auto-generated method stub
		String url = "/policies/sources";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(bulkBody).when().post(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response removePolicy(String source_id) {
		// TODO Auto-generated method stub
		String url = "/sources/" + source_id + "/removepolicy";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().post(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response removePolicyBulk(String[] bulkBody) {
		// TODO Auto-generated method stub
		String url = "/sources/removepolicies";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(bulkBody).when().post(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response deleteSourcesRemovepolicies(String source_id) {
		// TODO Auto-generated method stub
		String url = "/sources/" + source_id + "/removepolicies";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().delete(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response deleteSourcesRemovepoliciesBulk(String[] bulkBody) {
		// TODO Auto-generated method stub
		String url = "/sources/removepolicies";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(bulkBody).when().delete(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response deleteSources(String source_id) {
		// TODO Auto-generated method stub
		String url = "/sources/" + source_id;
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().delete(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response deleteSourcesBulk(String[] bulkBody) {
		// TODO Auto-generated method stub
		String url = "/sources";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(bulkBody).when().delete(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response deleteSourcesRemove(String source_id) {
		// TODO Auto-generated method stub
		String url = "/sources/" + source_id + "/remove";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().delete(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response deleteSourcesRemoveBulk(String[] bulkBody) {
		// TODO Auto-generated method stub
		String url = "/sources/remove";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(bulkBody).when().delete(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response cancelReplicationin(String source_id) {
		// TODO Auto-generated method stub
		String url = "/sources/" + source_id + "/cancelreplicationin";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().delete(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response cancelReplicationinBulk(String[] bulkBody) {
		// TODO Auto-generated method stub
		String url = "/sources/cancelreplicationin";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(bulkBody).when().delete(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response deleteDestination(String destination_id) {
		// TODO Auto-generated method stub
		String url = "/destinations/" + destination_id;
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().delete(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response deleteDestinationBulk(String[] bulkBody) {
		// TODO Auto-generated method stub
		String url = "/destinations";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(bulkBody).when().delete(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response putDestination(String destination_id) {
		// TODO Auto-generated method stub
		String url = "/destinations/" + destination_id;
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().put(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response putDestinationBulk(String[] bulkBody) {
		// TODO Auto-generated method stub
		String url = "/destinations";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(bulkBody).when().put(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response viewRecoverypoint(String destination_id) {
		// TODO Auto-generated method stub
		String url = "/destinations/" + destination_id + "/sources";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response viewRecoverypointBulk(String[] bulkBody) {
		// TODO Auto-generated method stub
		String url = "/destinations/sources";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(bulkBody).when().get(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response cancelJob(String job_id) {
		// TODO Auto-generated method stub
		String url = "/jobs/" + job_id + "/canceljob";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().post(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response cancelJobBulk(String[] bulkBody) {
		// TODO Auto-generated method stub
		String url = "/jobs/canceljob";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(bulkBody).when().post(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response viewLogs(String job_id) {
		// TODO Auto-generated method stub
		String url = "/jobs/" + job_id + "/logs";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response viewLogsBulk(String[] bulkBody) {
		// TODO Auto-generated method stub
		String url = "/jobs/logs";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(bulkBody).when().get(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response cancelReplicationjobin(String job_id) {
		// TODO Auto-generated method stub
		String url = "/jobs/" + job_id + "/canceljob";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().post(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response cancelReplicationjobinBulk(String[] bulkBody) {
		// TODO Auto-generated method stub
		String url = "/jobs/canceljob";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(bulkBody).when().post(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response postRecoveredresourcesProvision(String source_id) {
		// TODO Auto-generated method stub
		String url = "/recoveredresources/" + source_id + "/provision";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().post(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response postRecoveredresourcesProvisionBulk(String[] bulkBody) {
		// TODO Auto-generated method stub
		String url = "/recoveredresources/provision";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(bulkBody).when().post(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response postRecoveredresourcesStart(String source_id) {
		// TODO Auto-generated method stub
		String url = "/recoveredresources/" + source_id + "/start";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().post(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response postRecoveredresourcesStartBulk(String[] bulkBody) {
		// TODO Auto-generated method stub
		String url = "/recoveredresources/start";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(bulkBody).when().post(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response postRecoveredresourcesGracefulstop(String source_id) {
		// TODO Auto-generated method stub
		String url = "/recoveredresources/" + source_id + "/gracefulstop";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().post(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response postRecoveredresourcesGracefulstopBulk(String[] bulkBody) {
		// TODO Auto-generated method stub
		String url = "/recoveredresources/gracefulstop";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(bulkBody).when().post(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response postRecoveredresourcesHardstop(String source_id) {
		// TODO Auto-generated method stub
		String url = "/recoveredresources/" + source_id + "/hardstop";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().post(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response postRecoveredresourcesHardstopBulk(String[] bulkBody) {
		// TODO Auto-generated method stub
		String url = "/recoveredresources/hardstop";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(bulkBody).when().post(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response postRecoveredresourcesRestart(String source_id) {
		// TODO Auto-generated method stub
		String url = "/recoveredresources/" + source_id + "/restart";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().post(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response postRecoveredresourcesRestartBulk(String[] bulkBody) {
		// TODO Auto-generated method stub
		String url = "/recoveredresources/restart";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(bulkBody).when().post(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response postRecoveredresourcesDeprovision(String source_id) {
		// TODO Auto-generated method stub
		String url = "/recoveredresources/" + source_id + "/deprovision";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().post(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response postRecoveredresourcesDeprovisionBulk(String[] bulkBody) {
		// TODO Auto-generated method stub
		String url = "/recoveredresources/deprovision";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(bulkBody).when().post(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response deletePolicies(String policy_id) {
		// TODO Auto-generated method stub
		String url = "/policies/" + policy_id;
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().delete(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response deletePoliciesBulk(String[] bulkBody) {
		// TODO Auto-generated method stub
		String url = "/policies";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(bulkBody).when().delete(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response modifyPolicies(String policy_id) {
		// TODO Auto-generated method stub
		String url = "/policies/" + policy_id;
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().put(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response modifyPoliciesBulk(String[] bulkBody) {
		// TODO Auto-generated method stub
		String url = "/policies";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(bulkBody).when().put(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response deleteAlerts(String alert_id) {
		// TODO Auto-generated method stub
		String url = "/alerts/" + alert_id;
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().delete(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response deleteAlertsBulk(String[] bulkBody) {
		// TODO Auto-generated method stub
		String url = "/alerts";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(bulkBody).when().delete(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response deleteSourceGroup(String sourcegroup_id) {
		// TODO Auto-generated method stub
		String url = "/sources/groups/" + sourcegroup_id;
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().delete(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response deleteSourceGroupBulk(String[] bulkBody) {
		// TODO Auto-generated method stub
		String url = "/sources/groups";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(bulkBody).when().delete(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response deleteReports(String report_id) {
		// TODO Auto-generated method stub
		String url = "/reports/" + report_id;
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().delete(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response deleteReportsBulk(String[] bulkBody) {
		// TODO Auto-generated method stub
		String url = "/reports";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(bulkBody).when().delete(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response postUsersResetpassword(String user_id) {
		// TODO Auto-generated method stub
		String url = "/users/" + user_id + "/resetpassword";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().post(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response postUsersResetpasswordBulk(String[] bulkBody) {
		// TODO Auto-generated method stub
		String url = "/users/resetpassword";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(bulkBody).when().post(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response postUsersVerificationemail(String user_id) {
		// TODO Auto-generated method stub
		String url = "/users/" + user_id + "/verificationemail";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().post(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response postUsersVerificationemailBulk(String[] bulkBody) {
		// TODO Auto-generated method stub
		String url = "/users/verificationemail";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(bulkBody).when().post(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response deleteUsers(String user_id) {
		// TODO Auto-generated method stub
		String url = "/users/" + user_id;
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().delete(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response deleteUsersBulk(String[] bulkBody) {
		// TODO Auto-generated method stub
		String url = "/users";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(bulkBody).when().delete(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response getSourcesWithOrgId(String org_id) {
		// TODO Auto-generated method stub
		String url = "/sources?organization_id=" + org_id;
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response getPoliciesWithOrgId(String org_id) {
		// TODO Auto-generated method stub
		String url = "/policies?organization_id=" + org_id;
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response getJobsWithOrgId(String org_id) {
		// TODO Auto-generated method stub
		String url = "/jobs?organization_id=" + org_id;
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response getLogsWithOrgId(String org_id) {
		// TODO Auto-generated method stub
		String url = "/logs?organization_id=" + org_id;
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response getUsersWithOrgId(String org_id) {
		// TODO Auto-generated method stub
		String url = "/users?organization_id=" + org_id;
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response getDestinationsWithOrgId(String org_id) {
		// TODO Auto-generated method stub
		String url = "/destinations?organization_id=" + org_id;
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response getCloudaccountsWithOrgId(String org_id) {
		// TODO Auto-generated method stub
		String url = "/cloudaccounts?organization_id=" + org_id;
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response getReportsWithOrgId(String org_id) {
		// TODO Auto-generated method stub
		String url = "/reports?organization_id=" + org_id;
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response getAlertsWithOrgId(String org_id) {
		// TODO Auto-generated method stub
		String url = "/alerts?organization_id=" + org_id;
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get(finalUrl);
		response.then().log().all();
		return response;
	}

	/**
	 * REST API to getAuditCodesForCSR
	 * 
	 * @author ramya.nagepalli
	 * @param additionalurl
	 * @param token
	 * @return response
	 */
	public Response getAuditCodesForCSR(String csrToken, String additionalurl) {
		// TODO Auto-generated method stub
		String requestUrl = "/audittrail/codes";
		if ((additionalurl != null) && (!additionalurl.equals(""))) {
			requestUrl += "?" + additionalurl;
		}
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + csrToken).when().get(requestUrl);

		response.then().log().all();
		return response;
	}

	/**
	 * @author shuo.zhang
	 * @param userInfo
	 * @return
	 */
	public Response createUserWithFullInfo(Map<String, Object> userInfo) {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(userInfo).when().post("/users");

		response.then().log().all();
		return response;
	}

	/**
	 * postCloudHybridTrailRequest
	 * 
	 * @author Ramya.Nagepalli
	 * @param validToken
	 * @param organization_id
	 * @param test
	 * @return
	 */
	public Response postCloudHybridTrailRequest(String validToken, String organization_id, ExtentTest test) {
		// TODO Auto-generated method stub

		String request_url = "/organizations/" + organization_id + "/freetrial/cloudhybrid/request";
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + validToken).when().post(request_url);
		response.then().log().all();
		return response;
	}

	/**
	 * getOrganizationDetailsById
	 * 
	 * @author Ramya.Nagepalli
	 * @param validToken
	 * @param organization_id
	 * @param test
	 * @return
	 */
	public Response getOrganizationDetailsById(String validToken, String organization_id, ExtentTest test) {
		// TODO Auto-generated method stub
		String request_url = "/organizations/" + organization_id;
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + validToken).when().get(request_url);
		response.then().log().all();
		return response;
	}

	/**
	 * UpdateOrganizationCDcount
	 * 
	 * @author Ramya.Nagepalli
	 * @param validToken
	 * @param organization_id
	 * @param org_info
	 * @param test
	 * @return
	 */
	public Response UpdateOrganizationCDcount(String validToken, String organization_id, Map<String, Object> org_info,
			ExtentTest test) {
		// TODO Auto-generated method stub
		String request_url = "/organizations/" + organization_id + "/volume/count";
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + validToken).body(org_info).when().put(request_url);
		response.then().log().all();
		return response;
	}
	
	/**
	 * PostRecoveryForSource
	 * 
	 * @author Ramya.Nagepalli
	 * @param source_id
	 * @param token
	 * @param sourceInfo
	 * @return
	 */
	public Response PostRecoveryForSource(String token, String source_id, Map<String, Object> sourceInfo) {

		String url = "/sources/" + source_id + "/recover";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(sourceInfo).when().post(finalUrl);
		response.then().log().all();
		return response;
	}
	
	/**
	 * 
	 * getSpecifiedOrgProperties - Get properties of specified organization
	 * 
	 * Sprint 35 : API- GET: /organizations/{id}/properties
	 * 
	 * @author Ramya.Nagepalli
	 * @param Token
	 * @param organization_id
	 * @param test
	 * @return response 
	 */
	public Response getSpecifiedOrgProperties(String Token,String organization_id,ExtentTest test)
	{
		String url = " /organizations/" + organization_id + "/properties";
		String finalUrl = url.replace("//", "/");
		
		Response response = given()
							.header("Content-Type", "application/json")
							.header("Authorization", "Bearer " + Token)
							.when().get(finalUrl);
		
		response.then().log().all();
		
		return response;
	}
	
	/**
	 * API - POST: /organizations/{id}/submsp
	 * Creates a submsp under root msp
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param rootOrgId
	 * @param orgInfo
	 * @return
	 */
	public Response createSubMsp(String token, String rootOrgId, HashMap<String, Object> orgInfo) {
		
		String url = " /organizations/" + rootOrgId + "/submspaccounts";
		String finalUrl = url.replace("//", "/");

		Response response = given().log().uri()
							.header("Content-Type", "application/json")
							.header("Authorization", "Bearer " + token)
							.body(orgInfo)
							.when().post(finalUrl);
		
		response.then().log().all();
		
		return response; 
	}
	
	/**
	 * Converts an MSP organization to root MSP so that it can have SUB MSPs 
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param orgId
	 * @return
	 */
	public Response convertToRootMsp(String token, String orgId) {
		
		String url = " /organizations/" + orgId + "/converttorootmsp";
		String finalUrl = url.replace("//", "/");
		System.out.println(finalUrl);
		Response response = given()
							.header("Content-Type", "application/json")
							.header("Authorization", "Bearer " + token)
							.when().post(finalUrl);
		
		response.then().log().all();
		
		return response;
	}
	
	/**
	 * CD_API - POST: https://tadmin.zetta.net/cloudconsole/organizations/convertto3tier?organization_id=
	 * For converting an msp into 3 tier org
	 * 
	 * @author Rakesh.Chalamala
	 * @param organizationId
	 * @return 
	 */
	public static Response convertTo3Tier(String organizationId) {

		String hostName = "tadmin.zetta.net";
		String hostIP = null;
		try {
			hostIP = InetAddress.getByName(hostName).getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}		
		String URI = "https://"+hostIP+"/cloudconsole/organizations/convertto3tier?organization_id="+organizationId;

		Response response = given().relaxedHTTPSValidation().port(443).log().uri()
							.auth().preemptive().basic("admin@zetta.net", "Zetta1234")
							.when().post(URI);
				
		response.then().log().all();
		return response;
	}
	
	
	public Response postColumns(String token, HashMap<String, Object> columnInfo) {
		
		String url = "/columns";
		Response response = given().header("Content-Type", "application/json").log().uri()
				 .header("Authorization", "Bearer " + token).body(columnInfo).when().post(url);
		response.then().log().all();
		
		return response;
	}
	
	public Response putColumns(String token, String column_id, HashMap<String, Object> columnInfo) {
		
		String url = "/columns/"+column_id;
		Response response = given().header("Content-Type", "application/json").log().uri()
				 .header("Authorization", "Bearer " + token).body(columnInfo).when().put(url);
		response.then().log().all();
		
		return response;
	}
	
	public Response getColumns(String token, String filter) {
		
		String url = "/columns";
		if (filter!=null && !filter.isEmpty()) {
			filter = "?"+filter;
			url +=filter;
		}
				
		Response response = given().header("Content-Type", "application/json").log().uri()
				 .header("Authorization", "Bearer " + token).when().get(url);
		response.then().log().all();
		
		return response;
	}
	
	public Response deleteColumns(String token, String column_id) {
		
		String url = "/columns/"+column_id;
		Response response = given().header("Content-Type", "application/json").log().uri()
				.header("Authorization", "Bearer "+token).when().delete(url);
		response.then().log().all();
		
		return response;
	}

	/**
	 * createFilters
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param filterInfo
	 * @return response
	 */
	public Response createFilters(String token, Map<String, Object> filterInfo, String additionalURL) {

		String requestURL = "/filters";
		if (!additionalURL.equals(""))
			requestURL += "?" + additionalURL;
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().body(filterInfo).post(requestURL);

		response.then().log().all();
		return response;
	}
	
	/**
	 * updateFilterById
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param filterInfo
	 * @param filter_id
	 * @return response
	 */
	public Response updateFilterById(String token, Map<String, Object> filterInfo, String filter_id,String additionalURL) {

		String requestURL = "/filters/"+filter_id;
		if(!additionalURL.equals(""))
			requestURL +="?"+additionalURL;
			
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().body(filterInfo).put(requestURL);

		response.then().log().all();
		return response;
	}
	
	/**
	 * deleteFiltersById
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param filter_id
	 * @return response
	 */
	public Response deleteFiltersById(String token, String filter_id, String additionalURL) {
		String requestURL = "/filters/" + filter_id;
		if (!(additionalURL.equals("")))
			requestURL = requestURL + "?" + additionalURL;

		Response response = given().header("Content-Type", "application/json").log().uri()
				.header("Authorization", "Bearer " + token).when().delete(requestURL);
		response.then().log().all();
		return response;
	}
	
	/**
	 * getFiltersById
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param filter_id
	 * @param additionalURL
	 * @return response
	 */
	public Response getFiltersById(String token,String filter_id,String additionalURL)
	{
		String requestURL = "/filters/"+filter_id;
		if(!(additionalURL.equals("")))
			requestURL+="?"+additionalURL;
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().get(requestURL);
		response.then().log().all();
		return response;	
	}
	
	/**
	 * getFilters
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param additionalURL
	 * @return response
	 */
	public Response getFilters(String token,String additionalURL)
	{
		String requestURL = "/filters";
		
		if(!(additionalURL.equals("")))
			requestURL += "/?"+additionalURL;
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().get(requestURL);
		response.then().log().all();
		return response;	
	}

	/**
	 * Call the REST API TO create a group
	 * 
	 * @param Token
	 * @param composeSoucreGroupInfo
	 * @param test
	 * @return
	 */
	public Response createGroup(String Token, HashMap<String, Object> composeSoucreGroupInfo, ExtentTest test) {

		// RestAssured.baseURI = baseURI + "/sources/groups";

		System.out.println("token:" + this.token);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + Token).body(composeSoucreGroupInfo).when().post("/sources/groups");

		response.then().log().all();
		test.log(LogStatus.INFO, "the resposne generated is :" + response.getBody().asString());
		return response;
	}
}
