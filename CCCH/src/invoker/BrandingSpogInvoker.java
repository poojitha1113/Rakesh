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

public class BrandingSpogInvoker {
	private String token;

	public String getToken() {

		return token;
	}

	public void setToken(String token) {

		this.token = token;
	}

	public BrandingSpogInvoker(String baseURI, String port) {

		RestAssured.baseURI = baseURI;
		RestAssured.port = Integer.valueOf(port);
		RestAssured.basePath = "/api";
	}

	/**
	 * Call the Rest API to create the Branding emailer for logged in user
	 * 
	 * @author malleswari.sykam
	 * @param token
	 * @param BrandingInfo
	 * @return
	 */
	public Response createBrandingemailerfororganization(String organization_id, HashMap<String, Object> BrandingInfo) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(BrandingInfo).when()
				.post("/organizations/" + organization_id + "/branding/emailer");

		response.then().log().all();
		return response;
	}

	/**
	 * Call the Rest API to create the Branding emailer for logged in user
	 * 
	 * @author malleswari.sykam
	 * @param token
	 * @param BrandingInfo
	 * @return
	 */
	public Response getbrandingemailerfororganizaton(String organization_id) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when()
				.get("/organizations/" + organization_id + "/branding/emailer");

		response.then().log().all();
		return response;
	}

	/**
	 * Call the Rest API to create the Branding emailer for logged in user
	 * 
	 * @author malleswari.sykam
	 * @param token
	 * @param BrandingInfo
	 * @return
	 */
	public Response updateBrandingemailerfororganization(String organization_id, HashMap<String, Object> BrandingInfo) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(BrandingInfo).when()
				.put("/organizations/" + organization_id + "/branding/emailer");

		response.then().log().all();
		return response;
	}

	/**
	 * Call the Rest API to create the Branding for logged in user
	 * 
	 * @author malleswari.sykam
	 * @param token
	 * @param BrandingInfo
	 * @return
	 */
	public Response createbrandingfororganiztaion(String organization_id, HashMap<String, Object> BrandingInfo) {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(BrandingInfo).when()
				.post("/organizations/" + organization_id + "/branding");

		response.then().log().all();
		return response;
	}
	
	
	/**
	 * Call the Rest API to Turnon the Branding for logged in user
	 * 
	 * @author malleswari.sykam
	 * @param token
	 * @param BrandingInfo
	 * @return
	 */
	public Response TurnonBrandingfororganiztaion(String organization_id,String token) {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when()
				.post("/organizations/" + organization_id + "/branding/enable");

		response.then().log().all();
		return response;
	}
	
	
	/**
	 * Call the Rest API toturnoff the Branding for logged in user
	 * 
	 * @author malleswari.sykam
	 * @param token
	 * @param BrandingInfo
	 * @return
	 */
	public Response TurnoffBrandingfororganiztaion(String organization_id,String token) {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when()
				.post("/organizations/" + organization_id + "/branding/disable");

		response.then().log().all();
		return response;
	}
	
	
	/**
	 * Call the Rest API to Turnoff the Branding for logged in user
	 * 
	 * @author malleswari.sykam
	 * @param token
	 * @param BrandingInfo
	 * @return
	 */
	public Response TurnonBrandingEmailerfororganiztaion(String organization_id,String token) {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when()
				.post("/organizations/" + organization_id + "/branding/emailer/enable");

		response.then().log().all();
		return response;
	}
	
	
	/**
	 * Call the Rest API toturnoff the Branding for logged in user
	 * 
	 * @author malleswari.sykam
	 * @param token
	 * @param BrandingInfo
	 * @return
	 */
	public Response TurnoffBrandingEmailerfororganiztaion(String organization_id,String token) {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when()
				.post("/organizations/" + organization_id + "/branding/emailer/disable");

		response.then().log().all();
		return response;
	}

	/**
	 * Call the Rest API to get the Branding for logged in user
	 * 
	 * @author malleswari.sykam
	 * @param token
	 * @param BrandingInfo
	 * @return
	 */
	public Response getbrandingfororganization(String organization_id) {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when()
				.get("/organizations/" + organization_id + "/branding");

		response.then().log().all();
		return response;
	}
	// GET /organizations/branding

	/**
	 * Call the Rest API to getOrganizationBranding
	 * 
	 * @author malleswari.sykam
	 * @param organization_id
	 * @param additionalURL
	 * @return response
	 */
	public Response getOrganizationBranding(String organization_id, String additionalURL, ExtentTest test) {

		String requestURL = "";
		if (additionalURL == "" || additionalURL == null) {
			requestURL = "/organizations/branding";
		} else {
			requestURL = "/organizations/branding?" + additionalURL;
		}

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when().get(requestURL);

		response.then().log().all();
		return response;
	}

	/**
	 * Call the Rest API to create the Branding for logged in user
	 * 
	 * @author malleswari.sykam
	 * @param token
	 * @param BrandingInfo
	 * @return
	 */
	public Response updateBrandingfororganization(String organization_id, HashMap<String, Object> BrandingInfo) {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(BrandingInfo).when()
				.put("/organizations/" + organization_id + "/branding");

		response.then().log().all();
		return response;
	}

	/**
	 * Call the Rest API to upload logo
	 * 
	 * @author malleswari.sykam
	 * @param token
	 * @param organization_id
	 *            logopath
	 * @return
	 */

	public Response uploadLogofororganiztaion(String organization_id, String logoPath) {
		Response response = null;
		if (logoPath != "") {
			response = given().multiPart("picture", new File(logoPath)).header("Authorization", "Bearer " + this.token)
					.when().post("/organizations/" + organization_id + "/branding/logo");
		} else {
			response = given().header("Content-Type", "multipart/form-data")
					.header("Authorization", "Bearer " + this.token).when()
					.post("/organizations/" + organization_id + "/branding/logo");
		}

		response.then().log().all();
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
	 * Call the Rest API to upload loginimage
	 * 
	 * @author malleswari.sykam
	 * @param token
	 * @param organization_id
	 *            logopath
	 * @return
	 */

	public Response uploadLoginimageforLoggedinuser(String organization_id, String loginimagePath) {
		Response response = null;
		if (loginimagePath != "") {
			response = given().multiPart("picture", new File(loginimagePath))
					.header("Authorization", "Bearer " + this.token).when()
					.post("/organizations/" + organization_id + "/branding/loginimage");
		} else {
			response = given().header("Content-Type", "multipart/form-data")
					.header("Authorization", "Bearer " + this.token).when()
					.post("/organizations/" + organization_id + "/branding/loginimage");
		}

		response.then().log().all();
		return response;
	}

	/**
	 * Call the Rest API to EnableBrandingForOrganization
	 * 
	 * @author Ramya.Nagepalli
	 * @param validtoken
	 * @param organization_id
	 * @param test
	 * @return
	 */
	public Response EnableBrandingForOrganization(String validtoken, String organization_id, ExtentTest test) {

		String requestURL = "/organizations/" + organization_id + "/branding/enable";

		test.log(LogStatus.INFO, "the request url to enable branding for organization" + requestURL);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + validtoken).when().post(requestURL);

		response.then().log().all();
		return response;
	}

	/**
	 * Call the Rest API to DisableBrandingForOrganization
	 * 
	 * @author Ramya.Nagepalli
	 * @param validtoken
	 * @param organization_id
	 * @param test
	 * @return
	 */
	public Response DisableBrandingForOrganization(String validtoken, String organization_id, ExtentTest test) {

		String requestURL = "/organizations/" + organization_id + "/branding/disable";

		test.log(LogStatus.INFO, "the request url to enable branding for organization" + requestURL);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + validtoken).when().post(requestURL);

		response.then().log().all();
		return response;
	}

	/**
	 * Call the Rest API to EnableBrandingEmailerForOrganization
	 * 
	 * @author Ramya.Nagepalli
	 * @param validtoken
	 * @param organization_id
	 * @param test
	 * @return
	 */
	public Response EnableBrandingEmailerForOrganization(String validtoken, String organization_id, ExtentTest test) {

		String requestURL = "/organizations/" + organization_id + "/branding/emailer/enable";

		test.log(LogStatus.INFO, "the request url to enable branding for organization" + requestURL);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + validtoken).when().post(requestURL);

		response.then().log().all();
		return response;
	}

	/**
	 * Call the Rest API to DisableBrandingEmailerForOrganization
	 * 
	 * @author Ramya.Nagepalli
	 * @param validtoken
	 * @param organization_id
	 * @param test
	 * @return
	 */
	public Response DisableBrandingEmailerForOrganization(String validtoken, String organization_id, ExtentTest test) {

		String requestURL = "/organizations/" + organization_id + "/branding/emailer/disable";

		test.log(LogStatus.INFO, "the request url to enable branding for organization" + requestURL);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + validtoken).when().post(requestURL);

		response.then().log().all();
		return response;
	}
}
