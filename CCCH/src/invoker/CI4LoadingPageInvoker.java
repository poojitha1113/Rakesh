package invoker;

import static io.restassured.RestAssured.given;

import Constants.SpogConstants;
import groovy.lang.TracingInterceptor;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class CI4LoadingPageInvoker {
	private String token;

	public String getToken() {

		return token;
	}

	public void setToken(String token) {

		this.token = token;
	}
	public CI4LoadingPageInvoker(String baseURI, String port) {
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
	
	public void monitorPageCheck(String orgId){
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/dashboards/topsources");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/dashboards/topsources?filter_type=job_status");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/jobs?job_status=active");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/capacityusage/cloudhybrid/usage");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/dashboards/sourcesummary");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/dashboards/backupjobstatussummary");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/datatransferreport/summary");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/capacityusage/cloudhybrid/dedupesavings");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/dashboards/toppolicies");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/organizations/"+orgId+"/entitlements");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/dashboards/policysummary");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
	}
	
	public void protectPageCheck(String orgId,String userId){
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/sources/groups");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/sources/types");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/policies?page_size=1");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/policies");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/recoveredresources/types");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/sourcefilters?source_filter_type=all");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/sourcefilters?source_filter_type=machine");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/sources?source_type=machine");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/sourcefilters?source_filter_type=agentless_vm");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/sources?source_type=agentless_vm");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/recoveredresourcesfilters?undefined=recovered_vms");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/recoveredresources");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/destinations");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/destinationfilters?destination_type=all");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/destinations?destination_type=cloud_direct_volume");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/destinationfilters?destination_type=cloud_direct_volume");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/users/"+userId+"/destinationfilters?destination_type=cloud_direct_volume");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/destinationfilters?destination_type=cloud_hybrid_store");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/users/"+userId+"/destinationfilters?destination_type=cloud_hybrid_store");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/policyfilters?policy_filter_type=all");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/policies");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/policyfilters?policy_filter_type=all");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/users/"+userId+"/policyfilters?policy_filter_type=all");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
	}
	
	public void analyzeCheck(String orgId,String userId){
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/logs");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/reports?page=1");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/reports?page=1&page_size=20");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/reportfilters?reports_filter_type=reports");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/reports");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/reportfilters?reports_filter_type=reports");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/users/"+userId+"/reportfilters?reports_filter_type=reports");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/backupjobreports/topsources");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/backupjobreports/topsources?filter-type=job_status");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/backupjobreports/details?page=1&page_size=20");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/backupjobreports/topsources?filter-type=job_status");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/backupjobreports/topsources?filter-type=job_status");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/backupjobreports/topsources");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/backupjobreports/jobstatussummary");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/reportfilters?reports_filter_type=backup_jobs");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/backupjobreports/details");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/restorejobreports/topsources?filter-type=job_status");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/restorejobreports/details?page=1&page_size=20");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/restorejobreports/topsources");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/reportfilters?reports_filter_type=backup_jobs");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/backupjobreports/details");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/users/"+userId+"/reportfilters?reports_filter_type=backup_jobs");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/reportfilters?reports_filter_type=restore_jobs");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/restorejobreports/details");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/datatransferreport/details?page=1&page_size=20");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/dashboards/topsources?filter_type=job_status");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/datatransferreport/details?page=1&page_size=20");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/datatransferreport/summary");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/reportfilters?reports_filter_type=data_transfer");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/datatransferreport/details");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/reportfilters?reports_filter_type=data_transfer");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/users/"+userId+"/reportfilters?reports_filter_type=data_transfer");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/jobsdestinationreports/details?page=1&page_size=20");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/jobsdestinationreports/summary");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/jobsdestinationreports/topdestinations");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/jobsdestinationreports/summary");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/capacityusage/report/details?page=1&page_size=20");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/capacityusage/cloudhybrid/usage");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/capacityusage/cloudhybrid/dedupesavings");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/capacityusage/report/details?page=1&page_size=20");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/capacityusage/cloudhybrid/usage");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/capacityusage/cloudhybrid/dedupesavings");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
	}
	
	public void configurationCheck(String orgId,String userId){
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/hypervisors/types");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/hypervisors");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/sources/groups/amount");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/users/amount");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/userroles/amount");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/userroles");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/userfilters?user_filter_type=user_accounts");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/users");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/userfilters?user_filter_type=user_accounts");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get("/users/"+userId+"/userfilters?user_filter_type=user_accounts");
		//response.then().log().all();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		
	}

}
