package invoker;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import com.relevantcodes.extentreports.LogStatus;

import io.restassured.RestAssured;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.response.Response;
import com.relevantcodes.extentreports.ExtentTest;

public class Org4SPOGInvoker {

	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	public Org4SPOGInvoker(String baseURI, String port) {
		RestAssured.baseURI = baseURI;
		RestAssured.port = Integer.valueOf(port);
		RestAssured.basePath = "/api";
	}
	
	/**
	 * delete account's picture
	 * @author shuo.zhang
	 * @param parentId
	 * @param childId
	 * @param token
	 * @return
	 */
	public Response deleteAccountPicture(String parentId, String childId, String token){
		
		String url = "/organizations/" + parentId+ "/accounts/" + childId+ "/picture";
		String finalUrl =  url.replace("//", "/");
		
		Response response = given().header("Content-Type", "application/json")
		        .header("Authorization", "Bearer " + token).when().delete(finalUrl);
		System.out.println(finalUrl);
		response.then().log().all();
	
		return response;
		
	}
	
	/**
	 * @author shuo.zhang
	 * @param parentId
	 * @param childId
	 * @return
	 */
	public Response deleteAccountPictureWithoutLogin(String parentId, String childId){
		
		String url = "/organizations/" + parentId+ "/accounts/" + childId+ "/picture";
		String finalUrl =  url.replace("//", "/");
		
		Response response = given().header("Content-Type", "application/json")
		       .when().delete(finalUrl);
		System.out.println(finalUrl);
		response.then().log().all();
	
		return response;
		
	}
	/**
	 * @author Eric.Yang
	 * @param orgId
	 * @param token
	 * @param test
	 * @return
	 */
	public Response getOrganizationsIdRecoversourceTypes(String orgId,String token, ExtentTest test){
		String url="/organizations/"+orgId+"/recoveredresources"+"/types";
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
	 * @author Zhaoguo.Ma
	 * @param organizationID
	 * @return
	 */
	public Response deactiveOrganization(String organizationID) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when()
				.post("/organizations/" + organizationID + "/deactive");

		response.then().log().all();
		return response;
	}
	
	/**
	 * @author Zhaoguo.Ma
	 * @param organizationID
	 * @return
	 */
	public Response destroyOrganization(String organizationID) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when()
				.post("/organizations/" + organizationID + "/destroy");

		response.then().log().all();
		return response;
	}
	
	/**
	 * @author shuo.zhang
	 * @return
	 */
	public Response enrollOrganization(Map<String, Object > enrollmentInfo){
			
		Response response = given().header("Content-Type", "application/json")
				  .body(enrollmentInfo).when().post("/organizations/enroll");

		response.then().log().all();
		return response;
	}

	/*
	 * shan,jing
	 */
	public Response postOrgInfoBySearchStringWithCsrLogin(Map<String, Object> searchInfo,String token) {
		// TODO Auto-generated method stub
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(searchInfo).when()
				.post("/organizations/search");

		response.then().log().all();
		
		return response;
	}

	
	/**
	 * @author shuo.zhang
	 * @param orgid
	 * @param token
	 * @return
	 */
	public Response getSpecifiedOrganizationEntitlement(String orgid, String token){
		
		String url = "/organizations/" +orgid + "/entitlements";
		String finalUrl =  url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().get(finalUrl);

		response.then().log().all();
		return response;
	}

	/**
	 * @author shuo.zhang
	 * @param orgid
	 * @return
	 */
	public Response getSpecifiedOrganizationEntitlementWithoutLogin(String orgid) {
		// TODO Auto-generated method stub
		String url = "/organizations/" +orgid + "/entitlements";
		String finalUrl =  url.replace("//", "/");
		Response response = given().header("Content-Type", "application/json")
			.when().get(finalUrl);

		response.then().log().all();
		return response;
	}
	
	/**
	 * @author shuo.zhang
	 * @param token
	 * @return
	 */
	public Response getLogginUserOrganizationEntitlement(String token){
		
		String finalUrl = "/organization/entitlements";
	
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().get(finalUrl);
		System.out.println(finalUrl);
		response.then().log().all();
		return response;
	}
	
	/**
	 * @author shuo.zhang
	 * @return
	 */
	public Response getLogginUserOrganizationEntitlementWithoutLogin(){
		
		String finalUrl = "/organization/entitlements";
	
		Response response = given().header("Content-Type", "application/json")
				.when().get(finalUrl);
		
		response.then().log().all();
		return response;
	}
	
	/**
	 * shuo.zhang
	 * @param organizationId
	 * @param token
	 * @return
	 */
	public Response postOrgFreeTrialCloudHybrid(String organizationId, String token) {
		// TODO Auto-generated method stub
		String url= "/organizations/"+ organizationId + "/freetrial/cloudhybrid";
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.post(url);

		response.then().log().all();
		
		return response;
	}
	
	/**
	 * Deletes the specified organization's cache resources 
	 * @author Rakesh.Chalamala
	 * 
	 * @param organization_id
	 * @param token
	 * @return
	 */
	public Response deleteOrgCacheResourcesByOrgId(String token, String organization_id, ExtentTest test) {
		
		String requestURL= "/organizations/"+ organization_id + "/cache/resources";
		
		test.log(LogStatus.INFO, "The URI is "+ RestAssured.baseURI+":"+ RestAssured.port+requestURL);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.delete(requestURL);

		response.then().log().all();
		
		return response;		
	}
	
	/**
	 * convert an msp to rootmsp
	 * @author Zhaoguo.Ma
	 * @param organizationId
	 * @return
	 */
	public Response convertToRootMSP(String organizationId) {
		String url= "/organizations/"+ organizationId + "/converttorootmsp";
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when()
				.post(url);

		response.then().log().all();
		return response;
	}
	
	/**
	 * create SubMSPAccounts
	 * @author Zhaoguo.Ma
	 * @param orgInfo
	 * @param organizationId
	 * @return
	 */
	public Response createSubMSPAccountsincc(Map<String, Object> orgInfo, String organizationId) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(orgInfo).when().post("/organizations/" + organizationId + "/submspaccountsincc");

		response.then().log().all();
		return response;
	}
	
	public Response createSubMSPAccounts(Map<String, Object> orgInfo, String organizationId) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(orgInfo).when().post("/organizations/" + organizationId + "/submspaccounts");

		response.then().log().all();
		return response;
	}
	
	
	/**
	 * @author ChandraKanth.Kanamar
	 * @param token
	 * @param organizationID
	 * @return
	 */
	public Response suspendOrganization(String token,String organizationID) {
		
		Response response = given().header("Content-Type", "application/json").log().uri()
				.header("Authorization", "Bearer " + token).when()
				.post("/organizations/" + organizationID + "/suspend");

		response.then().log().all();
		return response;
	}
	
	/**
	 * @author ChandraKanth.Kanamar
	 * @param token
	 * @param organizationID
	 * @return
	 */
	public Response resumeOrganization(String token,String organizationID) {
		
		Response response = given().header("Content-Type", "application/json").log().uri()
				.header("Authorization", "Bearer " + token).when()
				.post("/organizations/" + organizationID + "/resume");

		response.then().log().all();
		return response;
	}
}
