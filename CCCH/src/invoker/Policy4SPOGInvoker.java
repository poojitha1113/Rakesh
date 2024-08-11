package invoker;

import static io.restassured.RestAssured.given;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class Policy4SPOGInvoker {
	private String token;

	public Policy4SPOGInvoker(String baseURI, String port) {
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
	 * REST API to get remote policies
	 * 
	 * @author shan.jing
	 * @param policyInfo
	 * @param token;
	 *            user token
	 * @return
	 */
	public Response getRemotePolicies(Map<String, Object> udpConsoleServerDTO) {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(udpConsoleServerDTO).when()
				.post("/policies/remotepolicies");

		response.then().log().all();
		return response;
	}

	/**
	 * REST API to create policy
	 * 
	 * @author shan.jing
	 * @param policyInfo
	 * @param token;
	 *            user token
	 * @return
	 */
	public Response createPolicy(Map<String, Object> policyInfo) {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(policyInfo).when().post("/policies");

		response.then().log().all();
		return response;
	}

	/**
	 * REST API to assign policy to given sources
	 * 
	 * @author shan.jing
	 * @param policy_id
	 * @param sources
	 * @return
	 */
	public Response assignPolicy(String policy_id, String[] sources) {
		String url = "/policies/" + policy_id + "/sources";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(sources).when().post(finalUrl);
		response.then().log().all();
		return response;
	}

	/**
	 * REST API to assign policy to given sources
	 * 
	 * @author shan.jing
	 * @param policy_id
	 * @param sources
	 * @return
	 */
	public Response unassignPolicy(String policy_id, String[] sources) {
		String url = "/policies/" + policy_id + "/sources";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(sources).when().delete(finalUrl);
		response.then().log().all();
		return response;
	}

	/**
	 * REST API to get source under org's policy
	 * 
	 * @author shan.jing
	 * @param org_id
	 * @param policy_type
	 * @param response
	 * @return
	 */
	public Response getSourcesUnderOrgByPolicyType(String org_id, String policy_type) {
		String url = "/organizations/" + org_id + "/policies/types/" + policy_type + "/sources";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get(finalUrl);
		response.then().log().all();
		return response;
	}

	/**
	 * REST API to remove source from policy
	 * 
	 * @author shan.jing
	 * @param source_id
	 * @param response
	 * @return
	 */
	public Response removeSourceFromPolicy(String source_id) {
		String url = "/sources/" + source_id + "/removepolicy";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().post(finalUrl);
		response.then().log().all();
		return response;
	}

	public Response getPolicies(HashMap<String, String> params) {

		RequestSpecification aRequestSpecification = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token);

		if (params != null) {
			aRequestSpecification.params(params);
		}

		Response response = aRequestSpecification.when().get("/policies");

		response.then().log().all();
		return response;
	}

	/**
	 * REST API to call the getPolicy
	 * 
	 * @author Bharadwaj.Ghadiam
	 * @param admin_Token
	 * @param policy_id
	 * @param test
	 * @return response
	 */
	public Response getPolicy(String admin_Token, String policy_id, ExtentTest test) {

		String requestUrl = "/policies/" + policy_id;
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestUrl);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + admin_Token).when().get(requestUrl);
		response.then().log().all();
		test.log(LogStatus.INFO, "The value of the response generated is :" + response.getBody().asString());
		return response;
	}

	/**
	 * REST API to call the update policy by policyId
	 * 
	 * @author Kiran.Sripada
	 * @param admin_Token
	 * @param policy_id
	 * @param policyInfo
	 * @param test
	 * @return response
	 */
	public Response updatePolicy(String admin_Token, String policy_id, Map<String, Object> policyInfo,
			ExtentTest test) {

		String requestUrl = "/policies/" + policy_id;
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestUrl);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + admin_Token).body(policyInfo).when().put(requestUrl);
		response.then().log().all();
		test.log(LogStatus.INFO, "The value of the response generated is :" + response.getBody().asString());
		return response;
	}

	/**
	 * REST API to call the update policy by policyId
	 * 
	 * @author Kiran.Sripada
	 * @param admin_Token
	 * @param policy_id
	 * @param policyInfo
	 * @param test
	 * @return response
	 */
	public Response deletePolicy(String admin_Token, String policy_id, ExtentTest test) {

		String requestUrl = "/policies/" + policy_id;
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestUrl);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + admin_Token).when().delete(requestUrl);
		response.then().log().all();
		test.log(LogStatus.INFO, "The value of the response generated is :" + response.getBody().asString());
		return response;
	}

	public Response deletePolicy(String admin_Token, String policy_id) {

		String requestUrl = "/policies/" + policy_id;
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + admin_Token).when().delete(requestUrl);
		response.then().log().all();
		return response;
	}

	/**
	 * call Rest API to get amount and policy type
	 * 
	 * @author leiyu.wang
	 * @param org_id
	 * @return
	 */
	public Response getAmountAndPolicyType(String org_id) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when()
				.get("organizations/" + org_id + "/policies/types");

		response.then().log().all();
		return response;

	}

	/**
	 * call api to get policy columns
	 * 
	 * @author shuo.zhang
	 * @param token
	 * @return
	 */
	public Response getPolicyColumns(String token) {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().get("/policy/columns");

		response.then().log().all();
		return response;
	}

	/**
	 * call api to get policy columns without login
	 * 
	 * @author shuo.zhang
	 * @return
	 */
	public Response getPolicyColumnsWithoutLogin() {

		Response response = given().header("Content-Type", "application/json").when().get("/policy/columns");

		response.then().log().all();
		return response;
	}

	/**
	 * @author shuo.zhang
	 * @param userId
	 * @param columnsInfo
	 * @param token
	 * @return
	 */
	public Response createUsersPolicyColumns(String userId, Map<String, Object> columnsInfo, String token) {

		String url = "/users/" + userId + "/policy/columns";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(columnsInfo).when().post(finalUrl);
		System.out.println(finalUrl);
		response.then().log().all();
		return response;
	}

	/**
	 * @author shuo.zhang
	 * @param userId
	 * @param token
	 * @return
	 */
	public Response deleteUsersPolicyColumns(String userId, String token) {

		String url = "/users/" + userId + "/policy/columns";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().delete(finalUrl);
		response.then().log().all();
		return response;
	}

	/**
	 * call Rest API to get policies amount
	 * 
	 * @author leiyu.wang
	 * @return
	 */
	public Response getPoliciesAmount() {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("policies/amount");

		response.then().log().all();
		return response;
	}

	/**
	 * @author shuo.zhang
	 * @param userId
	 * @param columnsInfo
	 * @return
	 */
	public Response createUsersPolicyColumnsWithoutLogin(String userId, Map<String, Object> columnsInfo) {

		String url = "/users/" + userId + "/policy/columns";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json").body(columnsInfo).when().post(finalUrl);
		System.out.println(finalUrl);
		response.then().log().all();
		return response;
	}

	/**
	 * @author shuo.zhang
	 * @param userId
	 * @return
	 */
	public Response deleteUsersPolicyColumnsWithoutLogin(String userId) {

		String url = "/users/" + userId + "/policy/columns";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json").when().delete(finalUrl);
		response.then().log().all();
		return response;
	}

	/**
	 * @author shuo.zhang
	 * @param userId
	 * @param columnsInfo
	 * @param token
	 * @return
	 */
	public Response updateUsersPolicyColumns(String userId, Map<String, Object> columnsInfo, String token) {

		String url = "/users/" + userId + "/policy/columns";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(columnsInfo).when().put(finalUrl);
		response.then().log().all();
		return response;
	}

	/**
	 * @author shuo.zhang
	 * @param userId
	 * @param columnsInfo
	 * @return
	 */
	public Response updateUsersPolicyColumnsWithoutLogin(String userId, Map<String, Object> columnsInfo) {

		String url = "/users/" + userId + "/policy/columns";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json").body(columnsInfo).when().put(finalUrl);
		response.then().log().all();
		return response;
	}

	/**
	 * @author shuo.zhang
	 * @param userId
	 * @param token
	 * @return
	 */
	public Response getUsersPolicyColumns(String userId, String token) {

		String url = "/users/" + userId + "/policy/columns";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().get(finalUrl);
		response.then().log().all();
		return response;
	}

	/**
	 * 
	 * @author shuo.zhang
	 * @param userId
	 * @return
	 */
	public Response getUsersPolicyColumnsWithoutLogin(String userId) {

		String url = "/users/" + userId + "/policy/columns";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json").when().get(finalUrl);
		response.then().log().all();
		return response;
	}

	/**
	 * @author shuo.zhang
	 *
	 * @param columnsInfo
	 * @param token
	 * @return
	 */
	public Response createLoggedInUserPolicyColumns(Map<String, Object> columnsInfo, String token) {

		String url = "/user/policy/columns";
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().body(columnsInfo).post(url);
		response.then().log().all();
		return response;
	}

	/**
	 * @author shuo.zhang
	 *
	 * @param columnsInfo
	 * @return
	 */
	public Response createLoggedInUserPolicyColumnsWithoutLogin(Map<String, Object> columnsInfo) {

		String url = "/user/policy/columns";
		Response response = given().header("Content-Type", "application/json").when().post(url);
		response.then().log().all();
		return response;
	}

	/**
	 * @author shuo.zhang
	 * @param token
	 * @return
	 */
	public Response deleteLoggedInUserPolicyColumns(String token) {

		String url = "/user/policy/columns";
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().delete(url);
		response.then().log().all();
		return response;
	}

	/**
	 * @author shuo.zhang
	 * @return
	 */
	public Response deleteLoggedInUserPolicyColumnsWithoutLogin() {

		String url = "/user/policy/columns";
		Response response = given().header("Content-Type", "application/json").when().delete(url);
		response.then().log().all();
		return response;
	}

	/**
	 * @author shuo.zhang
	 * @param columnsInfo
	 * @param token
	 * @return
	 */
	public Response updateLoggedInUserPolicyColumns(Map<String, Object> columnsInfo, String token) {

		String url = "/user/policy/columns";

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(columnsInfo).when().put(url);
		response.then().log().all();
		return response;
	}

	/**
	 * @author shuo.zhang
	 * @param columnsInfo
	 * @return
	 */
	public Response updateLoggedInUserPolicyColumnsWithoutLogin(Map<String, Object> columnsInfo) {

		String url = "/user/policy/columns";

		Response response = given().header("Content-Type", "application/json").body(columnsInfo).when().put(url);
		response.then().log().all();
		return response;
	}

	/**
	 * @author shuo.zhang
	 * @param token
	 * @return
	 */
	public Response getLoggedInUserPolicyColumns(String token) {

		String url = "/user/policy/columns";

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().get(url);
		response.then().log().all();
		return response;
	}

	/**
	 * 
	 * @author shuo.zhang
	 * @return
	 */
	public Response getLoggedInUserPolicyColumnsWithoutLogin() {

		String url = "/user/policy/columns";
		Response response = given().header("Content-Type", "application/json").when().get(url);
		response.then().log().all();
		return response;
	}

	/**
	 * REST API to call the getPolicy
	 * 
	 * @author Bharadwaj.Ghadiam
	 * @param admin_Token
	 * @param policy_id
	 * @param test
	 * @return response
	 */
	public Response getPolicyById(String admin_Token, String policy_id, ExtentTest test) {

		String requestUrl = "/policies/" + policy_id;
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestUrl);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + admin_Token).when().get(requestUrl);
		response.then().log().all();
		test.log(LogStatus.INFO, "The value of the response generated is :" + response.getBody().asString());
		return response;
	}

	/**
	 * REST API to call the getPoliciesByTypeUnderSource
	 * 
	 * @author shan.jing
	 * @param source_id
	 * @param test
	 * @return response
	 */
	public Response getPoliciesByTypeUnderSource(String source_id, ExtentTest test) {
		String requestUrl = "/sources/" + source_id + "/type/policies";
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestUrl);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().get(requestUrl);
		response.then().log().all();
		return response;
	}

	/**
	 * REST API to call the post policies under policy type for sources
	 * 
	 * @author shan.jing
	 * @param sources
	 * @param test
	 * @return response
	 */
	public Response postPoliciesByTypeUnderSources(String[] sources, ExtentTest test) {
		String requestUrl = "/sources/type/policies";
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestUrl);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(sources).when().post(requestUrl);
		response.then().log().all();
		return response;
	}

	/**
	 * REST API to get source under org's policy
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param org_id
	 * @param policy_type
	 * @param filter
	 * @param response
	 * @return
	 */
	public Response getSourcesUnderOrgByPolicyType(String token, String org_id, String policy_type, String filter) {

		String url = "/organizations/" + org_id + "/policies/types/" + policy_type + "/sources";
		String finalUrl = url.replace("//", "/");

		if (!(filter == null || filter.equals("")))
			finalUrl += "?" + filter;

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().get(finalUrl);
		response.then().log().all();
		return response;
	}

	/**
	 * getPolicies
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param filter
	 * @return
	 */
	public Response getPolicies(String token, String filter) {
		String url = "/policies";
		if (filter != null && filter != "")
			url += "?" + filter;

		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().get(finalUrl);
		response.then().log().all();
		return response;
	}

	/**
	 * Get all the policies of logged in user by for specified filter
	 * 
	 * @author Rakesh.Chalamala
	 * 
	 * @param token
	 * @param filter
	 * @param test
	 * @return
	 */
	public Response getPolicies(String token, String filter, ExtentTest test) {

		String requestUrl = "/policies";
		if (filter != null && !filter.isEmpty()) {
			requestUrl += "?" + filter;
		}
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestUrl);

		Response response = given().header("Content-Type", "application/json").log().uri()
				.header("Authorization", "Bearer " + token).when().get(requestUrl);
		response.then().log().all();
		return response;
	}

	/**
	 * postDeployPolicyById - to deploy policy configuration to the sources under it
	 * using policy_id
	 * 
	 * @author Ramya.Nagepalli
	 * @param validToken
	 * @param policy_id
	 * @param test
	 * @return response
	 */
	public Response postDeployPolicyById(String validToken, String policy_id, ExtentTest test) {

		String requestUrl = "/policies/" + policy_id + "/deploy";
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestUrl);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + validToken).when().post(requestUrl);
		response.then().log().all();
		return response;
	}

	/**
	 * 
	 * postDeploySourceById- to deploy policy configuration for a source using
	 * source_id
	 * 
	 * @author Ramya.Nagepalli
	 * @param validToken
	 * @param source_id
	 * @param test
	 * @return
	 */
	public Response postDeploySourceById(String validToken, String source_id, ExtentTest test) {

		String requestUrl = "/sources/" + source_id + "/deploy";
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestUrl);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + validToken).when().post(requestUrl);
		response.then().log().all();
		return response;
	}

	/**
	 * getSharedPoliciesofLoggedInUser - Get the shared policies of UDP
	 * 
	 * Sprint 35
	 * 
	 * @author Ramya.Nagepalli
	 * @param validtoken
	 * @param test
	 * @return
	 */

	public Response getSharedPoliciesofLoggedInUser(String validtoken, ExtentTest test) {

		String requestUrl = "/policies/sharedpolicies";

		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestUrl);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + validtoken).when().get(requestUrl);
		response.then().log().all();
		return response;
	}

	/**
	 * 
	 * enableSpecifiedOrgPolicies
	 * 
	 * Sprint 35 - API- POST :/organizations/{id}/policies/enable
	 * 
	 * @author Ramya.Nagepalli
	 * @param validtoken
	 * @param organization_id
	 * @param test
	 * @return
	 */
	public Response enableSpecifiedOrgPolicies(String validtoken, String organization_id, ExtentTest test) {

		String requestUrl = "/organizations/" + organization_id + "/policies/enable";
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestUrl);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + validtoken).when().post(requestUrl);
		response.then().log().all();
		return response;
	}

	/**
	 * 
	 * disableSpecifiedOrgPolicies
	 * 
	 * Sprint 35 - API- POST :/organizations/{id}/policies/disable
	 * 
	 * @author Ramya.Nagepalli
	 * @param validtoken
	 * @param organization_id
	 * @param test
	 * @return
	 */
	public Response disableSpecifiedOrgPolicies(String validtoken, String organization_id, ExtentTest test) {

		String requestUrl = "/organizations/" + organization_id + "/policies/disable";
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestUrl);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + validtoken).when().post(requestUrl);
		response.then().log().all();
		return response;
	}

	/**
	 * Disable a specified policy by it's id API - POST: /policies/{id}/disable
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param policy_id
	 * @param test
	 * @return
	 */
	public Response disablePolicyById(String token, String policy_id, ExtentTest test) {

		String requestUrl = "/policies/" + policy_id + "/disable";
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestUrl);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().post(requestUrl);
		response.then().log().all();
		return response;
	}

	/**
	 * Enable a specified policy by it's id API - POST: /policies/{id}/enable
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param policy_id
	 * @param test
	 * @return
	 */
	public Response enablePolicyById(String token, String policy_id, ExtentTest test) {

		String requestUrl = "/policies/" + policy_id + "/enable";
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestUrl);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().post(requestUrl);
		response.then().log().all();
		return response;
	}

}
