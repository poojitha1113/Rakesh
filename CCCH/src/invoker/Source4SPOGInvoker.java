package invoker;

import static io.restassured.RestAssured.given;

import java.util.Map;

import org.apache.commons.io.filefilter.AndFileFilter;
import org.codehaus.groovy.ast.stmt.ContinueStatement;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class Source4SPOGInvoker {
	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Source4SPOGInvoker(String baseURI, String port) {
		RestAssured.baseURI = baseURI;
		RestAssured.port = Integer.valueOf(port);
		RestAssured.basePath = "/api";
	}
	
	/**
	 * @author shuo.zhang
	 * @param userId
	 * @param columnsInfo
	 * @param token
	 * @return
	 */
	public Response createUsersSourcesColumns(String userId, Map<String, Object> columnsInfo, String token ){
		
		String url = "/users/" + userId + "/sources/columns";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
			        .header("Authorization", "Bearer " + token).body(columnsInfo).when()
			       .post(finalUrl);
		System.out.println(finalUrl);
		response.then().log().all();
		return response;
	}
	
	/**
	 * @author shuo.zhang
	 * @param userId
	 * @param columnsInfo
	 * @return
	 */
	public Response createUsersSourcesColumnsWithoutLogin(String userId, Map<String, Object> columnsInfo ){
		
		String url = "/users/" + userId + "/sources/columns";
		Response response = given().header("Content-Type", "application/json")
			        .body(columnsInfo).when()
			        .post(url);

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
	public Response deletesourcefilterforLoggedInuser(String filter_Id, String token) {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.delete("/user/sourcefilters/" + filter_Id);
		response.then().log().all();
		return response;
	}
	
	/**
	 * Call TEST web service to start backup for a source
	 * @author Zhaoguo.Ma
	 * @param sourceID
	 * @param token
	 * @return
	 */
	public Response startBackupForSource(String sourceID, String token) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.post("/sources/" + sourceID + "/startbackup");
		response.then().log().all();
		return response;
	}
	
	public Response startBackupForSource(String sourceID) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when()
				.post("/sources/" + sourceID + "/startbackup");
		response.then().log().all();
		return response;
	}
	
	public Response cancelBackupForSource(String sourceID, String token) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.post("/sources/" + sourceID + "/cancelbackup");
		response.then().log().all();
		return response;
	}
	
	public Response cancelBackupForSource(String sourceID) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when()
				.post("/sources/" + sourceID + "/cancelbackup");
		response.then().log().all();
		return response;
	}
	
	/**
	 * Call REST web service to create source filter for logged in user
	 * @author Zhaoguo.Ma
	 * @param filterInfo
	 * @param token
	 * @return
	 */
	public Response createSourceFilterForLoggedinUser(Map<String, Object> filterInfo) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(filterInfo).when()
				.post("/user/sourcefilters");
		response.then().log().all();
		return response;
	}
	
	/**
	 * Call REST web service to update source filter for logged in user
	 * @param filterID
	 * @param filterInfo
	 * @param token
	 * @return
	 */
	public Response updateSourceFilterForLoggedinUser(String filterID, Map<String, Object> filterInfo) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(filterInfo).when()
				.put("/user/sourcefilters/" + filterID);
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
	public Response updateUsersSourcesColumns(String userId, Map<String, Object> columnsInfo, String token ){
		
		String url = "/users/" + userId + "/sources/columns";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
			        .header("Authorization", "Bearer " + token).body(columnsInfo).when()
			       .put(finalUrl);
		response.then().log().all();
		return response;
	}
	
	/**
	 * @author shuo.zhang
	 * @param userId
	 * @param columnsInfo
	 * @return
	 */
	public Response updateUsersSourcesColumnsWithoutLogin(String userId, Map<String, Object> columnsInfo ){
		
		String url = "/users/" + userId + "/sources/columns";
		Response response = given().header("Content-Type", "application/json")
			        .body(columnsInfo).when()
			        .put(url);

		response.then().log().all();
		return response;
	}
	
	/**
	 * @author shuo.zhang
	 * @param userId
	 * @param token
	 * @return
	 */
	public Response deleteUsersSourcesColumns(String userId, String token ){
		
		String url = "/users/" + userId + "/sources/columns";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
			        .header("Authorization", "Bearer " + token).when()
			        .delete(finalUrl);
		response.then().log().all();
		return response;
	}
	
	/**
	 * @author shuo.zhang
	 * @param userId
	 * @return
	 */
	public Response deleteUsersSourcesColumnsWithoutLogin(String userId ){
		
		String url = "/users/" + userId + "/sources/columns";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
			        .when().delete(finalUrl);
		response.then().log().all();
		return response;
	}
	

	/**
	 * @author shuo.zhang
	 * @param userId
	 * @param token
	 * @return
	 */
	public Response getUsersSourcesColumns(String userId, String token){
		
		String url = "/users/" + userId + "/sources/columns";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				 .header("Authorization", "Bearer " + token).when().get(finalUrl);
		response.then().log().all();
		return response;
	}

	/**
	 * @author shuo.zhang
	 * @param userId
	 * @return
	 */
	public Response getUsersSourcesColumnsWithoutLogin(String userId) {
		// TODO Auto-generated method stub
		String url = "/users/" + userId + "/sources/columns";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				 .when().get(finalUrl);
		response.then().log().all();
		return response;
	}
	

	
	/**
	 * Call Create user source columns for logged in user
	 * @author shuo.zhang
	 * @param columnsInfo
	 * @param token
	 * @return
	 */
	public Response createLoggedInUserSourcesColumns(Map<String, Object> columnsInfo, String token ){
		
		String url = "/user/sources/columns";
		Response response = given().header("Content-Type", "application/json")
			        .header("Authorization", "Bearer " + token).body(columnsInfo).when()
			       .post(url);
		response.then().log().all();
		return response;
	}
	
	/**
	 *  Call Create user source columns for logged in user without token
	 * @param columnsInfo
	 * @return
	 */
	public Response createLoggedInUserSourcesColumnsWithoutLogin(Map<String, Object> columnsInfo){
		
		String url = "/user/sources/columns";
		Response response = given().header("Content-Type", "application/json")
			      	.body(columnsInfo).when().post(url);
		response.then().log().all();
		return response;
	}
	
	
	/**
	 * Call delete user source columns for logged in user
	 * @author shuo.zhang
	 * @param token
	 * @return
	 */
	public Response deleteLoggedInUserSourcesColumns(String token ){
		
		String url = "/user/sources/columns";
		Response response = given().header("Content-Type", "application/json")
			        .header("Authorization", "Bearer " + token).when()
			        .delete(url);
		response.then().log().all();
		return response;
	}
	
	/**
	 *  Call delete user source columns for logged in user without login
	 * @author shuo.zhang
	 * @return
	 */
	public Response deleteLoggedInUserSourcesColumnsWithoutLogin( ){
		
		String url = "/user/sources/columns";
		Response response = given().header("Content-Type", "application/json")
			       .when().delete(url);
		response.then().log().all();
		return response;
	}



	/**
	 * @author yuefen.liu
	 * @return
	 */
	public Response getDefaultHypervisorColumns(String token){
		
		Response response = given().header("Content-Type", "application/json")
				 .header("Authorization", "Bearer " + token).when()
				 .get("/hypervisors/columns");
		
		response.then().log().all();
		return response;
	}
	/**
	 * @author yuefen.liu
	 * @param columnsInfo
	 * @return
	 */
	public Response createHypervisorColumnsForLoggedInUser( Map<String, Object> columnsInfo, String token ){
		
		Response response = given().header("Content-Type", "application/json")
			        .header("Authorization", "Bearer " + token).body(columnsInfo).when()
			       .post("/user/hypervisors/columns");
		
		response.then().log().all();
		return response;
	}
	/**
	 * @author yuefen.liu
	 * @param userID
	 * @param columnsInfo
	 * @param token
	 * @return
	 */
	public Response createHypervisorColumnsForSpecifiedUser(String userId, Map<String, Object> columnsInfo, String token ){
		
		String url = "/users/" + userId + "/hypervisors/columns";
		String finalUrl = url.replace("//", "/");
		
		Response response = given().header("Content-Type", "application/json")
			        .header("Authorization", "Bearer " + token).body(columnsInfo).when()
			       .post(finalUrl);
		
		response.then().log().all();
		return response;
	}
	/**
	 * Call update user hypervisor columns for logged in user 
	 * @author yuefen.liu
	 * @param columnsInfo
	 * @param token
	 * @return
	 */
	public Response updateHypervisorColumnsForLoggedInUser( Map<String, Object> columnsInfo, String token ){
		
		Response response = given().header("Content-Type", "application/json")
			        .header("Authorization", "Bearer " + token).body(columnsInfo).when()
			       .put("/user/hypervisors/columns");
		response.then().log().all();
		return response;
	}
	/**
	 *call update hypervisor columns for specified user
	 * @author yuefen.liu
	 * @param userId
	 * @param columnsInfo
	 * @param token
	 * @return
	 */
	public Response updateHypervisorColumnsForSpecifiedUser(String userId, Map<String, Object> columnsInfo, String token ){
		
		String url = "/users/" + userId + "/hypervisors/columns";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
			        .header("Authorization", "Bearer " + token).body(columnsInfo).when()
			       .put(finalUrl);
		response.then().log().all();
		return response;
	}
	/**
	 * Call delete hypervisor columns for logged in user
	 * @author yuefen.liu
	 * @param token
	 * @return
	 */
	public Response deleteHypervisorColumnsForLoggedInUser(String token ){
		
		Response response = given().header("Content-Type", "application/json")
			        .header("Authorization", "Bearer " + token).when()
			        .delete("/user/hypervisors/columns");
		response.then().log().all();
		return response;
	}
	/**
	 * @author yuefen.liu
	 * @param userId
	 * @param token
	 * @return
	 */
	public Response deleteHypervisorColumnsForSpecifiedUser(String userId, String token ){
		
		String url = "/users/" + userId + "/hypervisors/columns";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
			        .header("Authorization", "Bearer " + token).when()
			        .delete(finalUrl);
		response.then().log().all();
		return response;
	}

	/**
	 * call get logged in user hypervisor columns
	 * @author yuefen.liu
	 * @param token
	 * @return
	 */
	public Response getHypervisorColumnsForLoggedInUser(String token){
		
		Response response = given().header("Content-Type", "application/json")
				 .header("Authorization", "Bearer " + token).when()
				 .get("/user/hypervisors/columns");
		response.then().log().all();
		return response;
	}
	/**
	 * call get specified users hypervisor columns
	 * @author yuefen.liu
	 * @param userId
	 * @param token
	 * @return
	 */
	public Response getHypervisorColumnsForSpecifiedUser(String userId, String token){
		
		String url = "/users/" + userId + "/hypervisors/columns";
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				 .header("Authorization", "Bearer " + token).when().get(finalUrl);
		response.then().log().all();
		return response;
	}
	/**
	 * Call update columns for logged in user 
	 * @author shuo.zhang
	 * @param columnsInfo
	 * @param token
	 * @return
	 */
	public Response updateLoggedInUserSourcesColumns( Map<String, Object> columnsInfo, String token ){
		
		String url = "/user/sources/columns";
		
		Response response = given().header("Content-Type", "application/json")
			        .header("Authorization", "Bearer " + token).body(columnsInfo).when()
			       .put(url);
		response.then().log().all();
		return response;
	}
	
	/**
	 * Call update user source columns for logged in user without login
	 * @author shuo.zhang
	 * @param columnsInfo
	 * @return
	 */
	public Response updateLoggedInUserSourcesColumnsWithoutLogin( Map<String, Object> columnsInfo ){
		
		String url = "/user/sources/columns";	
		Response response = given().header("Content-Type", "application/json")
			        .body(columnsInfo).when().put(url);
		response.then().log().all();
		return response;
	}

	/**
	 * call get logged in user source columns
	 * @author shuo.zhang
	 * @param token
	 * @return
	 */
	public Response getLoggedInUserSourcesColumns(String token){
		
		String url = "/user/sources/columns";	
		Response response = given().header("Content-Type", "application/json")
				 .header("Authorization", "Bearer " + token).when().get(url);
		response.then().log().all();
		return response;
	}

	/**
	 * call get logged in user source columns without login
	 * @author shuo.zhang
	 * @return
	 */
	public Response getLoggedInUserSourcesColumnsWithoutLogin() {
		
		String url = "/user/sources/columns";	
		Response response = given().header("Content-Type", "application/json")
				.when().get(url);
		response.then().log().all();
		return response;
	}


	/**
	 * get amount for source type of instant_vm/virtual_standby
	 * @author leiyu.wang
	 * @return
	 */
	public Response getAmountForSourceType(String orgID){
		Response response=given().header("Content-Type","application/json")
				.header("Authorization","Bearer "+token).when().get("/recoveredresources/types?organization_id="+orgID);
		response.then().log().all();
		return response;
	}	

	/**
	 * call get source type amount by org id
	 * @author shuo.zhang
	 * @param orgId
	 * @return
	 */
	public Response getSourcesTypesAmountByOrgId(String orgId, String token){
	
		String url = "/organizations/"+ orgId +"/sources/types";	
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				 .header("Authorization", "Bearer " + token).when().get(finalUrl);
		response.then().log().all();
		return response;
	}
	
	/**
	 * @author shuo.zhang
	 * @param orgId
	 * @return
	 */
	public Response getSourcesTypesAmountByOrgIdWithoutLogin(String orgId){
		
		String url = "/organizations/"+ orgId +"/sources/types";	
		String finalUrl = url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				 .when().get(finalUrl);
		response.then().log().all();
		return response;
	}
	
	
	/**
	 * call REST API to  get source groups amount
	 * @author leiyu.wang
	 * @return
	 */
	public Response getSourceGroupAmount(){
		Response response=given().header("Content-Type","application/json")
				.header("Authorization","Bearer "+token).when().get("/sources/groups/amount");
		response.then().log().all();
		return response;
	}
	
	/**
	 * @author Zhaoguo.Ma
	 * @param sourceID
	 * @return
	 */
	public Response getRecoveryImageFormats(String sourceID) {
		Response response=given().header("Content-Type","application/json")
				.header("Authorization","Bearer "+ token).when().get("/sources/" + sourceID + "/recoveryimageformats");
		response.then().log().all();
		return response;
	}
	
	public Response getRecoveryTargets(String sourceID, String taskType, String imageType) {
		String url = "/sources/" + sourceID + "/recoverytargets";
		String extendURL = "";
		int count = 0;
		if (taskType != null && taskType !="") {
			count ++;
			extendURL += "task_type=" + taskType;
		}
		if (imageType !=null && imageType !="") {
			if (count != 0) {
				extendURL += "&";
			}
			count ++;
			extendURL += "image_type=" + imageType;
		}
		
		if (count != 0) {
			extendURL = "?" + extendURL;
		}
		
		System.out.println(url + extendURL);
		
		Response response = given().header("Content-Type","application/json")
				.header("Authorization","Bearer "+ token).when().get(url + extendURL);
		response.then().log().all();
		return response;
	}
	
	public Response getRecoveryTargets(String sourceID, String taskType, String imageType, String extraURL) {
		String url = "/sources/" + sourceID + "/recoverytargets";
		String extendURL = "";
		int count = 0;
		if (taskType != null && taskType !="") {
			count ++;
			extendURL += "task_type=" + taskType;
		}
		if (imageType !=null && imageType !="") {
			if (count != 0) {
				extendURL += "&";
			}
			count ++;
			extendURL += "image_type=" + imageType;
		}
		
		if (count != 0) {
			extendURL = "&" + extendURL;
		}
		

		if ((extraURL != null) && (!extraURL.equals(""))) {
			url += "?" + extraURL;
		}
		
		System.out.println(url + extendURL);
		Response response = given().header("Content-Type","application/json")
				.header("Authorization","Bearer "+ token).when().get(url + extendURL);
		response.then().log().all();
		return response;
	}
	
	
	/**
	 * @author Zhaoguo.Ma
	 * @param sourceID
	 * @param jobInfo
	 * @return
	 */
	public Response submitCDRecoveryJob(String sourceID, Map<String, Object> jobInfo) {
		Response response=given().header("Content-Type","application/json")
				.header("Authorization","Bearer "+ this.token).when().body(jobInfo)
				.post("/sources/" + sourceID + "/recover");
		response.then().log().all();
		return response;
	}
	
	public Response cancelCDRecoveryJob(String sourceID, String org_id) {
		Response response=given().header("Content-Type","application/json")
				.header("Authorization","Bearer "+ this.token).when()
				.post("/sources/" + sourceID + "/cancelrecovery?organization_id="+org_id);
		response.then().log().all();
		return response;
	}
	
	public Response removeSource(String sourceID) {
		Response response=given().header("Content-Type","application/json")
				.header("Authorization","Bearer "+ this.token).when()
				.delete("/sources/" + sourceID + "/remove");
		response.then().log().all();
		return response;
	}
	
	/* Get Sources by policy id
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param policy_id
	 * @param test
	 * @return response
	 */
	public Response getSourcesByPolicyId(String token, String policy_id, ExtentTest test) {
		
		String requestUrl = "/policies/"+policy_id+"/sources"; 
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestUrl);
		Response response=given().header("Content-Type","application/json")
				.header("Authorization","Bearer "+ token).when()
				.get(requestUrl);
		response.then().log().all();
		return response; 
	}
	
	/**
	 * Converts the specified source to agentless hyperv
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param source_id
	 * @param test
	 * @return
	 */
	public Response convertToAgentlessBySourceId(String token, String source_id, ExtentTest test) {
		
		String requestUrl = "/sources/"+source_id+"/convert_to_agentless"; 
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestUrl);
		Response response=given().header("Content-Type","application/json")
				.header("Authorization","Bearer "+ token).when()
				.post(requestUrl);
		response.then().log().all();
		return response;	
	}
}
