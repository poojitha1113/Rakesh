package invoker;

import static io.restassured.RestAssured.given;

import com.relevantcodes.extentreports.LogStatus;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import com.relevantcodes.extentreports.ExtentTest;

public class SPOGUserrolesInvoker {

	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	public SPOGUserrolesInvoker(String baseURI, String port) {
		RestAssured.baseURI = baseURI;
		RestAssured.port = Integer.valueOf(port);
		RestAssured.basePath = "/api";
	}	
	/**
	 * @author Eric.Yang
	 * @param orgId
	 * @param token
	 * @param test
	 * @return
	 */
	public Response getUserrolesAmount(String token, ExtentTest test){
		String url="/userroles"+"/amount";
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
	
}
