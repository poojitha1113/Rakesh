package invoker;

import static io.restassured.RestAssured.given;

import java.util.Map;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class Log4GatewayInvoker  {

	public Log4GatewayInvoker(String baseURI, String port) {
		// TODO Auto-generated constructor stub
		 RestAssured.baseURI = baseURI;
		 RestAssured.port = Integer.valueOf(port);
		 RestAssured.basePath = "/api";
	}

	  private String token;
	  
	  public String getToken() {
	    return token;
	  }
	 
	  public void setToken(String token) {
	    this.token = token;
	  }
	  

	    /**
	    * REST API to update log
	    * @author shuo.zhang
	    * @param log_id 
	    * @param logInfo
	    * @param token; site token
	    * @return
	    */
	    public Response updateLog(String log_id, Map<String, Object> logInfo, String token ) {

	      
	        Response response = given().header("Content-Type", "application/json")
	            .header("Authorization", "Bearer " + token).body(logInfo).when().put("/logs/" + log_id);
	        response.then().log().all();
	        return response;
	    }
	    
	    /**
	     * @author shuo.zhang
	     * @param log_id
	     * @param logInfo
	     * @return
	     */
	    public Response updateLogWithoutLogin(String log_id, Map<String, Object> logInfo ) {

		      
	        Response response = given().header("Content-Type", "application/json")
	           .body(logInfo).when().put("/logs/" + log_id);
	        response.then().log().all();
	        return response;
	    }

}
