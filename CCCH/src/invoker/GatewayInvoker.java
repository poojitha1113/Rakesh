package invoker;
import static io.restassured.RestAssured.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import io.restassured.RestAssured;
import io.restassured.response.Response;


public class GatewayInvoker {


    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public GatewayInvoker(String baseURI, String port){
        RestAssured.baseURI = baseURI;
        RestAssured.port  = Integer.valueOf(port);
        RestAssured.basePath = "/api";
    }

    /**
     *  Call REST Web service to login to Site 
     * @author BhardwajReddy
     * @param userInfo: login user info
     * @return response 
     */
    public Response siteLogin(Map<String,String> siteInfo,String site_id){

        Response response=given()
                .header("Content-Type", "application/json")
                .body(siteInfo)
                .when()
                .post("/sites/"+site_id+"/login");
        response.then().log().all();
        return response;

    }

    /**
     * Call REST Web service to Register a Site 
     * @author BharadwajReddy
     * @param site_id
     * @param GatewayRegisterInformation(HashMap)
     * @return response
     */
    public Response registerSite(Map<String,String>registerInfo,String site_id,ExtentTest test ){
        test.log(LogStatus.INFO, "The URI is "+RestAssured.baseURI+":"+RestAssured.port+"/sites/link");
        Response response=given()
                .header("Content-Type", "application/json")
                .body(registerInfo)
                .when()
                .post("/sites/"+site_id+"/register");
        response.then().log().all();
        
        test.log(LogStatus.INFO,"The value of the response is:"+response.getBody().asString());
        return response;

    }
    /**
     * Call REST Web Service to configuration a Site 
     * @author BhardwajReddy
     * @param site_id
     * @response
     */
    public Response siteConfiguration(String site_id,ExtentTest test)
    {
        Response response=given()
                .header("Content-Type", "application/json")
                .when()
                .get("/sites/"+site_id+"/configuration");
        response.then().log().all();
        return response;
    }

    /**
     * Call REST Web Service to configuration a Site 
     * @author kiransripada
     * @param site_id
     * @param token
     * @param extent test
     * @response
     */
    public Response getSiteConfiguration(String site_id,String valid_token,ExtentTest test){
        test.log(LogStatus.INFO, "The URI is "+RestAssured.baseURI+":"+RestAssured.port+"/sites/"+site_id+"/configuration");
        Response response=given()
                .header("Content-Type", "application/json")
                .header("Authorization","Bearer "+valid_token)
                .when()
                .get("/sites/"+site_id+"/configuration");
        response.then().log().all();
        return response;
    }


    /**
     * create a new job
     * @author kiran.sripada
     * @param jobInfo
     * @return
     */
    public Response createJob(Map<String,Object> jobInfo, String token) {
        System.out.println("token:" + token);
        Response response = given()     
                .header("Content-Type", "application/json")
                .header("Authorization","Bearer "+ token)
                .body(jobInfo)
                .when()
                .post("/jobs");
        response.then().log().body();

        return response;
    }
    public Response createJob(String jobID, Map<String,Object> dataInfo, String token) {
        System.out.println("token:" + token);
        Response response = given()     
                .header("Content-Type", "application/json")
                .header("Authorization","Bearer " + token)
                .body(dataInfo)
                .when()
                .post("/jobs/" + jobID + "/data");
        response.then().log().body();

        return response;
    }
    /**
     * create a new job data
     * @author kiran.sripada
     * @param jobID
     * @param jobInfo
     * @return
     */
    public Response createJobData(String jobID, Map<String,String> dataInfo, String token) {
        System.out.println("token:" + token);
        Response response = given()     
                .header("Content-Type", "application/json")
                .header("Authorization","Bearer " + token)
                .body(dataInfo)
                .when()
                .post("/jobs/" + jobID + "/data");
        response.then().log().body();

        return response;
    }
    
    /**
     * Call the rest web service to post the jobs
     * @param site_token
     * @param logInfo
     * @param test
     * @return
     */
    public Response postJobs(String site_token,
            TreeMap<String, Object> logInfo, ExtentTest test) {
        // TODO Auto-generated method stub
        test.log(LogStatus.INFO, "The URI is "+RestAssured.baseURI+":"+RestAssured.port+"/logs");
        Response response=given()
                .header("Content-Type", "application/json")
                .header("Authorization","Bearer "+site_token)
                .body(logInfo)
                .when()
                .post("/logs");
        response.then().log().all();
        test.log(LogStatus.INFO,"The response for post Logs is:"+response.getBody().asString());
        return response;
    }


    /**
    * REST API to create log
    * @author shan.jing
    * @param logInfo
    * @return
    */
    public Response createLog(Map<String, Object> logInfo, String token) {

        System.out.println("log token: " + token);
        Response response = given().header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + token).body(logInfo).when().post("/logs");
        response.then().log().all();
        return response;
    }
        
    /**
     * REST API to create log inbulk
     * @author shan.jing
     * @param logInfo
     * @return
     */
     public Response createLogInbulk(ArrayList<HashMap<String, Object>> logInfo, String token) {

         System.out.println("log token: " + token);
         Response response = given().header("Content-Type", "application/json")
             .header("Authorization", "Bearer " + token).body(logInfo).when().post("/logs/inbulk");
         response.then().log().all();
         return response;
     }
     
     /**
      * REST API to get generated froms for log
      * @author shan.jing
      * @return
      */
      public Response getGeneratedFroms4Log(String token_id) {

          System.out.println("log token: " + token);
          Response response = given().header("Content-Type", "application/json")
              .header("Authorization", "Bearer " + token_id).when().get("/logs/generatedfroms");
          response.then().log().all();
          return response;
      }
    
    
    /**
     * create a new job
     * 
     * @author yin.li
     * @param jobInfo
     * @return
     */
    public Response updateJob(String jobID, Map<String, Object> jobInfo, String token) {

      System.out.println("token:" + token);
      Response response = given().header("Content-Type", "application/json")
          .header("Authorization", "Bearer " + token).body(jobInfo).when().put("/jobs/" + jobID);
      response.then().log().body();

      return response;
    }
    
    /**
     * update a new job data
     * 
     * @author yin.li
     * @param jobID
     * @param jobInfo
     * @return
     */
    public Response UpdateJobData(String jobID, Map<String, String> dataInfo, String token) {

      System.out.println("token:" + token);
      Response response = given().header("Content-Type", "application/json")
          .header("Authorization", "Bearer " + token).body(dataInfo).when()
          .put("/jobs/" + jobID + "/data");
      response.then().log().body();

      return response;
    }      
}
