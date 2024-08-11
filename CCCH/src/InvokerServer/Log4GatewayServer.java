package InvokerServer;

import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.SpogConstants;
import dataPreparation.JsonPreparation;
import genericutil.ErrorHandler;
import invoker.Log4GatewayInvoker;
import io.restassured.response.Response;


public class Log4GatewayServer {

	private Log4GatewayInvoker   log4GatewayInvoker;
	private GatewayServer gatewayServer;
	private static JsonPreparation jp = new JsonPreparation();
	static ErrorHandler errorHandle = ErrorHandler.getErrorHandler();
	
	  
	public Log4GatewayServer(String baseURI, String port) {
		
		log4GatewayInvoker = new Log4GatewayInvoker(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
	}

	  public void setToken(String token){
		  log4GatewayInvoker.setToken(token);
	  }
	
	  /**
       * update log, if log id is not existed create a log. otherwise update the log
       * @author shuo.zhang
       * @param log_id, the id you want to update, it should not be empty.
       * @param job_id, String 
       * @param organization_id, String
       * @param expected_organization_id, String
       * @param log_severity_type, 'spog', 'udp', 'cloud_direct'
       * @param log_source_type, 'information', 'warning', 'error'
       * @param message_id, String
       * @param message_data, String
       * @param isLogExisted  indicate whether it is update or create a new log
       * @param token, String
       * @param expectedStatusCode
       * @param expectedErrorCode 
       * @return log_id
       */

      public String updateLogWithCheck(long log_generate_time,String log_id, String job_id,
              String organization_id, String expected_organization_id, String source_id, String log_severity_type,
          String log_source_type, String message_id, String[] message_data, String job_type, String source_name,
          String server_name, String help_message_id, boolean isLogExisted, String token, int expectedStatusCode, String expectedErrorCode, ExtentTest test) {
    	  
    	  String real_log_id=null;
    	  
    	  test.log(LogStatus.INFO, "compose log info");
       /*   Map<String, Object> logInfo = jp.composeLogInfo(log_generate_time, job_id, organization_id,source_id, log_severity_type,
                  log_source_type, message_id, logMessageDataInfo);
           Map<String, Object> logInfo = jp.composeLogInfo(log_generate_time, job_id, expected_organization_id, source_id, log_severity_type,
        		  log_source_type, message_id, help_message_id, logMessageDataInfo);*/
           String mes_data= "";
           if(message_data!=null){
        	   for(int i=0; i< message_data.length; i++){
            	   mes_data+= message_data[i];
            	   if(i!= message_data.length-1){
            		   mes_data+= ",";
            	   }
            	 
               }
           }
           
           Map<String, Object> logInfo = jp.composeLogInfo(log_generate_time,job_id,organization_id,source_id,
		    		log_severity_type, log_source_type, message_id,help_message_id, mes_data);
    	  
          test.log(LogStatus.INFO, "update log ");
          Response response =null;
          if(token==null){
        	  response= log4GatewayInvoker.updateLogWithoutLogin(log_id, logInfo);
          }else{
        	  response = log4GatewayInvoker.updateLog(log_id, logInfo, token);
          }
      
          
          test.log(LogStatus.INFO, "check response ");
    
          response.then().statusCode(expectedStatusCode);
          if(expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE){
              test.log(LogStatus.INFO, "check returned body ");
        	   if(isLogExisted){
             	  response.then().body("data.log_id", equalTo(log_id));
               }
               response.then().body("data.job_data.job_id", equalTo(job_id));
               
               if(job_type!=null){
            	   response.then().body("data.job_data.job_type", equalTo(job_type));
               }
              
               response.then().body("data.job_data.source_id", equalTo(source_id));
               response.then().body("data.job_data.source_name", equalTo(source_name));
               if(server_name!=null){
            	   response.then().body("data.job_data.generated_from", equalTo(server_name));
               }
               response.then().body("data.log_severity_type", equalTo(log_severity_type));
               response.then().body("data.log_source_type", equalTo(log_source_type));
               response.then().body("data.organization.organization_id", equalTo(expected_organization_id));
               String message = getLogMessage(message_id, message_data);
               response.then().body("data.message", equalTo(message));
          /*     ArrayList<String>  responseMessageData= response.then().extract().path("data.message_data");
               if (null == message_data  || (message_data.toString() =="" ||  message_data.length==0)) {
                   assertNull(responseMessageData, "compare " + message_data + " passed once we don't set it");
               } else {
                   int length = message_data.length;
                   if (length != responseMessageData.size()) {
                       assertTrue("compare message_data length passed", false);
                   }
                   for(int i = 0; i <length; i++) {
                       if (! responseMessageData.get(i).equalsIgnoreCase(message_data[i])) {
                           assertTrue("compare message_data " + message_data[i] + "failed", false);
                       }
                   }
               }  */        
               if(help_message_id!=null){
            	   response.then().body("data.help_message_id", equalTo(help_message_id));
               }
               
               real_log_id = response.then().extract().path("data.log_id");
          }else{
        	  //error
        	  test.log(LogStatus.INFO, "check error code");
        	  gatewayServer.checkErrorCode(response, expectedErrorCode);
          }
                  
    	  return real_log_id;

      }
  
      private String getLogMessage(String message_id, String[] message_data) {
		// TODO Auto-generated method stub
    	  String message= null;
    	  if(message_id!=null){
    		
    		  if( message_id.equalsIgnoreCase("failedToConnectLicenseServer")){
    			  message = "Failed to connect to server " + message_data[0]+ ". Verify that the network connection to this server is good.";
    			  
    		  }else if(message_id.equalsIgnoreCase("connectDestinationFolderError")){
    			 
    			  message= "Failed to connect remote folder " + message_data[0] + " with user : " + message_data[1] + "."+  message_data[2] +".";
    			  
    		  }else if(message_id.equalsIgnoreCase("failedToRegisterToRPS")){
    			  message = "Failed to register Arcserve UDP Agent to ^AU_ProductName_RPS^.";
    		  }
    		  
    	  }
    	
		return message;
	}

	/**
       * update log, if log id is not existed create a log. otherwise update the log
       * @author shuo.zhang
       * @param log_id, the id you want to update, it should not be empty.
       * @param job_id, String 
       * @param organization_id, String
       * @param expected_organization_id, String
       * @param log_severity_type, 'spog', 'udp', 'cloud_direct'
       * @param log_source_type, 'information', 'warning', 'error'
       * @param message_id, String
       * @param message_data, String
       * @param isLogExisted  indicate whether it is update or create a new log
       * @param token, String
       * @param expectedStatusCode
       * @param expectedErrorCode 
       * @return log_id
       */

      public String updateLogWithCheck(long log_generate_time,String log_id, String job_id,
              String organization_id, String expected_organization_id, String source_id, String log_severity_type,
          String log_source_type, String message_id, String[] message_data, 
          boolean isLogExisted, String token, int expectedStatusCode, String expectedErrorCode, ExtentTest test) {
    	  
    	  String real_log_id=null;
    
          test.log(LogStatus.INFO, "compose log info");
          /*Map<String, Object> logInfo = jp.composeLogInfo(log_generate_time, job_id, organization_id,source_id, log_severity_type,
                  log_source_type, message_id, logMessageDataInfo);*/
          String mes_data= null;
          for(int i=0; i< message_data.length; i++){
       	   mes_data+= message_data[i];
       	   if(i!= message_data.length-1){
       		   mes_data+= ",";
       	   }
       	 
          }
          
          
          Map<String, Object> logInfo = jp.composeLogInfo(log_generate_time,log_id,job_id,organization_id,source_id,
		    		log_severity_type, log_source_type, message_id, mes_data);
  	  
          test.log(LogStatus.INFO, "update log ");
          Response response =null;
          if(token==null){
        	  response= log4GatewayInvoker.updateLogWithoutLogin(log_id, logInfo);
          }else{
        	  response = log4GatewayInvoker.updateLog(log_id, logInfo, token);
          }
      
          
          test.log(LogStatus.INFO, "check response ");
    
          response.then().statusCode(expectedStatusCode);
          if(expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE){
              test.log(LogStatus.INFO, "check returned body ");
        	   if(isLogExisted){
             	  response.then().body("data.log_id", equalTo(log_id));
               }
               response.then().body("data.job_id", equalTo(job_id));
               response.then().body("data.log_severity_type", equalTo(log_severity_type));
               response.then().body("data.log_source_type", equalTo(log_source_type));
               response.then().body("data.organization_id", equalTo(expected_organization_id));
               response.then().body("data.message_id", equalTo(message_id));
               HashMap <String, String> responseMessageData = response.then().extract().path("data.message_data");
               if (null == message_data  || (message_data.toString() =="" ||  message_data.length==0)) {
                   assertNull(responseMessageData, "compare " + message_data + " passed once we don't set it");
               } else {
                   int length = message_data.length;
                   if (length != responseMessageData.size()) {
                       assertTrue("compare message_data length passed", false);
                   }
                   for(int i = 0; i <length; i++) {
                       if (! responseMessageData.get(String.valueOf(i)).equalsIgnoreCase(message_data[i])) {
                           assertTrue("compare message_data " + message_data[i] + "failed", false);
                       }
                   }
               }           
               real_log_id = response.then().extract().path("data.log_id");
          }else{
        	  //error
        	  test.log(LogStatus.INFO, "check error code");
        	  gatewayServer.checkErrorCode(response, expectedErrorCode);
          }
                  
    	  return real_log_id;

      }
      
      
      public Response updateLog(long log_generate_time,String log_id, String job_id,
              String organization_id, String expected_organization_id, String source_id, String log_severity_type,
          String log_source_type, String message_id, String[] message_data, String job_type, String source_name,
          String server_name, String help_message_id, boolean isLogExisted, String token, int expectedStatusCode, String expectedErrorCode, ExtentTest test) {
    	  
    	  String real_log_id=null;
    	  
    	  test.log(LogStatus.INFO, "compose log info");
       /*   Map<String, Object> logInfo = jp.composeLogInfo(log_generate_time, job_id, organization_id,source_id, log_severity_type,
                  log_source_type, message_id, logMessageDataInfo);
           Map<String, Object> logInfo = jp.composeLogInfo(log_generate_time, job_id, expected_organization_id, source_id, log_severity_type,
        		  log_source_type, message_id, help_message_id, logMessageDataInfo);*/
           String mes_data= "";
           if(message_data!=null){
        	   for(int i=0; i< message_data.length; i++){
            	   mes_data+= message_data[i];
            	   if(i!= message_data.length-1){
            		   mes_data+= ",";
            	   }
            	 
               }
           }
           
           Map<String, Object> logInfo = jp.composeLogInfo(log_generate_time,job_id,organization_id,source_id,
		    		log_severity_type, log_source_type, message_id,help_message_id, mes_data);
    	  
          test.log(LogStatus.INFO, "update log ");
          Response response =null;
          if(token==null){
        	  response= log4GatewayInvoker.updateLogWithoutLogin(log_id, logInfo);
          }else{
        	  response = log4GatewayInvoker.updateLog(log_id, logInfo, token);
          }
      
          
          test.log(LogStatus.INFO, "check response ");
    
          response.then().statusCode(expectedStatusCode);
          if(expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE){
              test.log(LogStatus.INFO, "check returned body ");
        	   if(isLogExisted){
             	  response.then().body("data.log_id", equalTo(log_id));
               }
               response.then().body("data.job_id", equalTo(job_id));
               
               if(job_type!=null){
            	   response.then().body("data.job_type", equalTo(job_type));
               }
              
               response.then().body("data.source_id", equalTo(source_id));
               response.then().body("data.source_name", equalTo(source_name));
               if(server_name!=null){
            	   response.then().body("data.generated_from", equalTo(server_name));
               }
               response.then().body("data.log_severity_type", equalTo(log_severity_type));
               response.then().body("data.log_source_type", equalTo(log_source_type));
               response.then().body("data.organization_id", equalTo(expected_organization_id));
               response.then().body("data.message_id", equalTo(message_id));
               ArrayList<String>  responseMessageData= response.then().extract().path("data.message_data");
               if (null == message_data  || (message_data.toString() =="" ||  message_data.length==0)) {
                   assertNull(responseMessageData, "compare " + message_data + " passed once we don't set it");
               } else {
                   int length = message_data.length;
                   if (length != responseMessageData.size()) {
                       assertTrue("compare message_data length passed", false);
                   }
                   for(int i = 0; i <length; i++) {
                       if (! responseMessageData.get(i).equalsIgnoreCase(message_data[i])) {
                           assertTrue("compare message_data " + message_data[i] + "failed", false);
                       }
                   }
               }          
               if(help_message_id!=null){
            	   response.then().body("data.help_message_id", equalTo(help_message_id));
               }
               
               real_log_id = response.then().extract().path("data.log_id");
          }else{
        	  //error
        	  test.log(LogStatus.INFO, "check error code");
        	  gatewayServer.checkErrorCode(response, expectedErrorCode);
          }
                  
    	  return response;

      }
  

}
