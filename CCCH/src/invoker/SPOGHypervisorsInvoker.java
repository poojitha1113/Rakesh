package invoker;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import api.hypervisors.columns.CreateHypervisorColumnsForLoggedInUserTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class SPOGHypervisorsInvoker {
	private String token;

	public String getToken() {

		return token;
	}

	public void setToken(String token) {

		this.token = token;
	}

	public SPOGHypervisorsInvoker(String baseURI, String port) {

		RestAssured.baseURI = baseURI;
		RestAssured.port = Integer.valueOf(port);
		RestAssured.basePath = "/api";
	}
	
	public Response createHypervisor(Map<String, Object> hypervisorInfo) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(hypervisorInfo).when()
				.post("/hypervisors");

		response.then().log().all();
		return response;

	}
	
	public Response updateHypervisor(String hypervisorID,Map<String, Object> hypervisorInfo) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(hypervisorInfo).when()
				.put("/hypervisors/" + hypervisorID);

		response.then().log().all();
		return response;

	}
	
	public Response deleteHypervisor(String hypervisorID) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when()
				.delete("/hypervisors/" + hypervisorID);

		response.then().log().all();
		return response;

	}

	/*
	 * @author Rakesh.Chalamala
	 * @param additionalURL
	 * @param test
	 * 
	 * Call the Rest API with filter,sort,page details in the URL
	 */
	public Response getHypervisors(String additionalURL, ExtentTest test) {
		
		String requestUrl = "/hypervisors";
		if((additionalURL != null) && (!additionalURL.equals(""))) {
			requestUrl += "?" + additionalURL;
		}		
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + "/hypervisors");
		Response response= given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token)
				.when().get(requestUrl);
		response.then().log().all();
		test.log(LogStatus.INFO,"The response generated is :"+response.getBody().asString());                   
		return response;	
	}
	
	/*
	 * @author Rakesh.Chalamala
	 * @param hypervisor_id
	 * @param test
	 * 
	 * Call the Rest API to get the Specified hypervisor using id
	 */
	public Response getHypervisorsById(String hypervisor_id,ExtentTest test) {
		String requestUrl = "/hypervisors/"+hypervisor_id;
		test.log(LogStatus.INFO,
				"The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestUrl);
		Response response= given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token)
				.when()
				.get(requestUrl);
		response.then().log().all();
		test.log(LogStatus.INFO,"The response generated is :"+response.getBody().asString());                   
		return response;
	}
	
	/*
	 * @author Rakesh.Chalamala
	 * @param hypervisor_id
	 * @param expectedStatusCode
	 * @param ExpectedErrorMessage
	 * @param test
	 * 
	 * Refresh the vms in the specified hypervisor ( Time stamp will be updated )
	 */
	public Response refreshVMSForSpecifiedHypervisor(String hypervisor_id, ExtentTest test) {
		String requestUrl = "/hypervisors/"+hypervisor_id+"/refreshvms";
		test.log(LogStatus.INFO,
				"The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestUrl);
		Response response= given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token)
				.when().post(requestUrl);
		response.then().log().all();
		test.log(LogStatus.INFO,"The response generated is :"+response.getBody().asString());                   
		return response;
	}
	
	/*
	 * @author Rakesh.Chalamala
	 * @param hypervisor_id
	 * @test
	 * 
	 * Get vms in a specified hypervisor
	 */
	public Response getVMSForSpecifiedHypervisor(String hypervisor_id,ExtentTest test) {
		String requestUrl = "/hypervisors/"+hypervisor_id+"/vms";
		test.log(LogStatus.INFO,
				"The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestUrl);
		Response response= given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token)
				.when()
				.get(requestUrl);
		response.then().log().all();
		test.log(LogStatus.INFO,"The response generated is :"+response.getBody().asString());                   
		return response;
	}
	
	/*
	 * @author Rakesh.Chalamala
	 * @param hypervisor_id
	 * @param vm_ids[]
	 * @param test
	 * 
	 * Enable the vms in the hypervisor
	 */
	public Response enableVMSToSpecifiedHypervisor(String hypervisor_id,HashMap<String, Object> vm_ids,ExtentTest test) {
		String requestUrl = "/hypervisors/"+hypervisor_id+"/vms/enablevms";
		test.log(LogStatus.INFO,
				"The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestUrl);
		Response response= given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token)
				.body(vm_ids).when()
				.post(requestUrl);
		response.then().log().all();
		test.log(LogStatus.INFO,"The response generated is :"+response.getBody().asString());                   
		return response;
	}

	
	public Response getHypervisor(String hypervisorID) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when()
				.get("/hypervisors/" + hypervisorID);

		response.then().log().all();
		return response;

	}
	
	/* Call the REST API GET: /hypervisors/types
	 * 
	 * @author Rakesh.Chalamala
	 * 
	 */
	public Response getHypervisorsAmount() {
		
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when()
				.get("/hypervisors/types");
		
		response.then().log().all();
		return response;
	}

}
