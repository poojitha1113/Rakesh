package InvokerServer;

import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.ErrorCode;
import Constants.SpogConstants;
import dataPreparation.JsonPreparation;
import genericutil.ErrorHandler;
import invoker.Org4SPOGInvoker;
import invoker.SPOGUserrolesInvoker;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;


public class SPOGUserrolesServer {
	private static JsonPreparation jp = new JsonPreparation();
	private SPOGServer spogServer;
	private SPOGUserrolesInvoker spogUserrolesInvoker;
	public static ErrorHandler errorHandle = ErrorHandler.getErrorHandler();
	private ExtentTest test;

	public void setToken(String token) {
		spogUserrolesInvoker.setToken(token);
		spogServer.setToken(token);
	}

	public SPOGUserrolesServer(String baseURI, String port) {
		
		spogUserrolesInvoker = new SPOGUserrolesInvoker(baseURI, port);
		spogServer = new SPOGServer(baseURI, port);
	}	

	/*GET /userroles/Amount with check 
	   * 
	   * @author Eric.Yang
	   * @param expectedStatusCode
	   * @param expected_num
	   * @param test
	   */		
	
	public Response getUserrolesAmountWithCheck(String token,int expectedStatusCode,int expected_num,ExtentTest test) {
		
		Response response = spogUserrolesInvoker.getUserrolesAmount(token, test);

		errorHandle.printDebugMessageInDebugFile("Response status is " + response.getStatusCode());
		/*
		response.then().statusCode(expectedStatusCode);
		if ( expectedStatusCode==response.getStatusCode()) {
			test.log(LogStatus.INFO, "The returned status code from the response is " + response.getStatusCode());
			assertTrue("Check status is equal to expected status:" + expectedStatusCode, true);
			test.log(LogStatus.PASS, "Check:response status");
		} else {
			assertTrue("Check status is equal to expected status:" + expectedStatusCode, false);
			test.log(LogStatus.FAIL, "Check:response status"+response.getStatusCode());
			return response;
		}
		**/		

		if (expectedStatusCode == response.getStatusCode()) {
			if (expectedStatusCode==403||expectedStatusCode==401){
				test.log(LogStatus.PASS, "Check:" + "check response status " +response.getStatusCode()+ " in reponse body.");
				return response;
			}
			response.then().log().all();
			int amount = response.then().extract().path("data.amount");
			if(expected_num==amount)
			{
				test.log(LogStatus.PASS, "Check:" + "userroles amount " + " in reponse body.");
				assertTrue("Check value: "+amount+" is equal to expected amount:" + expected_num, true);
			}
			else{
				test.log(LogStatus.FAIL, "Check:" + "userroles amount " + " in reponse body.");
				assertTrue("Check value: "+amount+" is equal to expected amount:" + expected_num, false);
				return response;
			}
		}
		else{
			test.log(LogStatus.FAIL, "statusCode:" +response.getStatusCode()+ ", not equal expectedStatusCode: "+expectedStatusCode+ " in reponse body.");
			assertTrue("Check value: "+response.getStatusCode()+" is equal to expected status:" + expectedStatusCode, false);
			return response;
		}
		System.out.println("The value of the getOrganizationsIdRecoversourceTypes is :" + response.getBody().asString());
		return response;
	}	
}
