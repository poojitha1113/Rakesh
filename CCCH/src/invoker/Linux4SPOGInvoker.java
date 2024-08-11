package invoker;

import static io.restassured.RestAssured.given;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.hamcrest.core.IsNull;
import org.json.JSONException;
import org.json.JSONObject;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.AuthenticationSpecification;
import io.restassured.specification.RequestSpecification;

public class Linux4SPOGInvoker {
	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Linux4SPOGInvoker(String baseURI, String port) {
		RestAssured.baseURI = baseURI;
		RestAssured.port = Integer.valueOf(port);
		RestAssured.basePath = "/api";
	}

	/**
	 * @author Zhaoguo.Ma
	 * @param linuxServerInfo
	 * @return
	 */
	public Response createLinuxServer(Map<String, Object> linuxServerInfo) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(linuxServerInfo).when()
				.post("/linuxbackupservers");

		response.then().log().all();
		return response;
	}
	
	/**
	 * @author Zhaoguo.Ma
	 * @param serverID
	 * @param linuxServerInfo
	 * @return
	 */
	public Response updateLinuxServer(String serverID, Map<String, Object> linuxServerInfo) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).body(linuxServerInfo).when()
				.put("/linuxbackupservers/" + serverID);

		response.then().log().all();
		return response;
	}
	
	/**
	 * @author Zhaoguo.Ma
	 * @param serverID
	 * @return
	 */
	public Response getLinuxServer(String serverID) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when()
				.get("/linuxbackupservers/" + serverID);

		response.then().log().all();
		return response;
	}
	
	/**
	 * @author Zhaoguo.Ma
	 * @return
	 */
	public Response getLinuxServers() {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when()
				.get("/linuxbackupservers");

		response.then().log().all();
		return response;
	}
	
	/**
	 * @author Zhaoguo.Ma
	 * @param serverID
	 * @return
	 */
	public Response deleteLinuxServer(String serverID) {
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + this.token).when()
				.delete("/linuxbackupservers/" + serverID);

		response.then().log().all();
		return response;
	}

 
}