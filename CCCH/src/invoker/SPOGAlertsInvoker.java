package invoker;

import static io.restassured.RestAssured.given;

import java.util.Map;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class SPOGAlertsInvoker {
	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public SPOGAlertsInvoker(String baseURI, String port) {
		RestAssured.baseURI = baseURI;
		RestAssured.port = Integer.valueOf(port);
		RestAssured.basePath = "/api";
	}

	/**
	 * submitEmailsForAlerts
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param expectedData
	 * @param test
	 * @return
	 */
	public Response submitEmailsForAlerts(String token, Map<String, Object> expectedData, ExtentTest test) {
		String requestUrl = "/alerts/recipients";
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestUrl);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(expectedData).when().post(requestUrl);
		response.then().log().all();
		return response;
	}

	/**
	 * deleteEmailsForSpecifiedAlert
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param alert_email_id
	 * @param test
	 * @return
	 */
	public Response deleteEmailsForSpecifiedAlert(String token, String alert_email_id, ExtentTest test) {
		String requestUrl = "/alerts/recipients/" + alert_email_id;
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestUrl);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().delete(requestUrl);
		response.then().log().all();
		return response;
	}

	/**
	 * putEmailsForSpecifiedAlert
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param expectedData
	 * @param test
	 * @return
	 */
	public Response putEmailsForSpecifiedAlert(String token, String alert_email_id, Map<String, Object> expectedData,
			ExtentTest test) {
		String requestUrl = "/alerts/recipients/" + alert_email_id;
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestUrl);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(expectedData).when().put(requestUrl);
		response.then().log().all();
		return response;
	}

	/**
	 * getEmailsForSpecifiedAlert
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param alert_email_id
	 * @param test
	 * @return
	 */
	public Response getEmailsForSpecifiedAlert(String token, String alert_email_id, ExtentTest test) {
		String requestUrl = "/alerts/recipients/" + alert_email_id;
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestUrl);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().get(requestUrl);
		response.then().log().all();
		return response;
	}

	/**
	 * getEmailsForAlerts
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param test
	 * @return
	 */
	public Response getEmailsForAlerts(String token, ExtentTest test) {
		String requestUrl = "/alerts/recipients";
		test.log(LogStatus.INFO, "The URI is " + RestAssured.baseURI + ":" + RestAssured.port + requestUrl);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().get(requestUrl);
		response.then().log().all();
		return response;
	}

	/**
	 * Call REST Web service to acknowledgeAllOrganizationAlerts
	 * 
	 * @author Ramya.Nagepalli
	 * @param adminToken
	 * @param test
	 * @return response
	 */
	public Response acknowledgeAllOrganizationAlerts(String adminToken, ExtentTest test) {
		// TODO Auto-generated method stub
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + adminToken).when().delete("/alerts");
		response.then().log().all();
		return response;
	}

	/**
	 * Call REST Web service to deleteAlertsByAlertId
	 * 
	 * @author Ramya.Nagepalli
	 * @param adminToken
	 * @param test
	 * @return response
	 */
	public Response deleteAlertsByAlertId(String adminToken, String alert_id, ExtentTest test) {
		// TODO Auto-generated method stub
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + adminToken).when().delete("/alerts/" + alert_id);
		response.then().log().all();
		return response;
	}

	/**
	 * Call REST Web service to getAlertsForLoggedInUser
	 * 
	 * @author Ramya.Nagepalli
	 * @param adminToken
	 * @param additionalURL
	 * @param test
	 * @return response
	 */
	public Response getAlertsForLoggedInUser(String adminToken, String additionalURL, ExtentTest test) {

		String requestURL = "";

		if (additionalURL.equals("")) {
			requestURL = "/alerts";
		} else {
			requestURL = "/alerts?" + additionalURL;
		}

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + adminToken).when().get(requestURL);
		response.then().log().all();
		return response;
	}

	/**
	 * Call REST Web service to generateOrganizationCapacityAlert API-POST
	 * /organizations/{organization_id}/capacityalert
	 * 
	 * @author Ramya.Nagepalli
	 * @param adminToken
	 * @param organization_id
	 * @param test
	 * @return response
	 */
	public Response generateOrganizationCapacityAlert(String token, String organization_id, ExtentTest test) {

		String requestUrl = "/organizations/" + organization_id + "/capacityalert";

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().post(requestUrl);
		response.then().log().all();
		return response;
	}

	/**
	 * Call REST Web service to create Organization alerts
	 * 
	 * @author Ramya.Nagepalli
	 * @param adminToken
	 * @param alertInfo
	 * @param org_id
	 * @return response
	 */
	public Response createOrganizationAlerts(String adminToken, String org_id, Map<String, Object> alertInfo) {
		// TODO Auto-generated method stub
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + adminToken).body(alertInfo).when()
				.post("/organizations/" + org_id + "/alerts");

		response.then().log().all();

		return response;
	}

	/**
	 * Call REST Web service to update Organization alerts
	 * 
	 * @author Ramya.Nagepalli
	 * @param adminToken
	 * @param alertInfo
	 * @param org_id
	 * @param alert_id
	 * @return response
	 */
	public Response updateOrganizationAlerts(String adminToken, String org_id, Map<String, Object> alertInfo,
			String alert_id) {
		// TODO Auto-generated method stub
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + adminToken).body(alertInfo).when()
				.put("/organizations/" + org_id + "/alerts/" + alert_id);

		response.then().log().all();

		return response;
	}

	/**
	 * Call REST Web service to get Organization alerts by alert id
	 * 
	 * @author Ramya.Nagepalli
	 * @param adminToken
	 * @param org_id
	 * @param alert_id
	 * @return response
	 */
	public Response getOrganizationAlertsByAlertId(String adminToken, String org_id, String alert_id) {
		// TODO Auto-generated method stub
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + adminToken).when()
				.get("/organizations/" + org_id + "/alerts/" + alert_id);

		response.then().log().all();
		return response;
	}

	/**
	 * Call REST Web service to delete Organization alerts by alert id
	 * 
	 * @author Ramya.Nagepalli
	 * @param adminToken
	 * @param org_id
	 * @param alert_id
	 * @return response
	 */
	public Response deleteOrganizationAlertsByAlertId(String adminToken, String org_id, String alert_id) {
		// TODO Auto-generated method stub
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + adminToken).when()
				.delete("/organizations/" + org_id + "/alerts/" + alert_id);

		response.then().log().all();
		return response;
	}

	/**
	 * Call REST Web service to get Organization alerts
	 * 
	 * @author Ramya.Nagepalli
	 * @param adminToken
	 * @param org_id
	 * @return response
	 */
	public Response getOrganizationAlerts(String adminToken, String org_id) {
		// TODO Auto-generated method stub
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + adminToken).when().get("/organizations/" + org_id + "/alerts");

		response.then().log().all();
		return response;
	}
}
