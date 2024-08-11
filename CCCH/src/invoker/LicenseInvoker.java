package invoker;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class LicenseInvoker {
	
	private String token;

	public String getToken() {

		return token;
	}

	public void setToken(String token) {

		this.token = token;
	}

	public LicenseInvoker(String baseURI, String port) {
		// TODO Auto-generated constructor stub
		RestAssured.baseURI = baseURI;
		RestAssured.port = Integer.valueOf(port);
		RestAssured.basePath = "/api";

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

	public Response ActivateLicensefororagnziation(String token,String Oragzanition_id,HashMap<String, Object> billingiNFO) {

		System.out.println("token: " + this.token);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(billingiNFO).when()
				.post("/organizations/" + Oragzanition_id + "/subscription/activate");
		response.then().log().all();
		return response;
	}
}
