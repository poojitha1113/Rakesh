package InvokerServer;

import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.SpogConstants;
import Constants.SpogMessageCode;
import dataPreparation.JsonPreparation;
import genericutil.ErrorHandler;
import invoker.LicenseInvoker;
import invoker.Org4SPOGInvoker;
import invoker.SPOGDestinationInvoker;
import invoker.SPOGInvoker;
import invoker.UserSpogInvoker;
import io.restassured.response.Response;

public class LicenseServer {
	private static JsonPreparation jp = new JsonPreparation();
	private SPOGServer spogServer;
	private LicenseInvoker licenseInvoker;
	public static ErrorHandler errorHandle = ErrorHandler.getErrorHandler();
	private ExtentTest test;
	private String default_first_name="first_name";
	private String default_last_name="last_name"; 
	private String default_pwd="Caworld_2017"; 
	private String datacenter_id="91a9b48e-6ac6-4c47-8202-614b5cdcfe0c";

	private String Uuid;
	private String clouddata;

	public LicenseServer(String baseURI, String port) {
		spogServer = new SPOGServer(baseURI, port);
		licenseInvoker = new LicenseInvoker(baseURI, port);
	}

	/**
	 *  @author Nagamalleswari Sykam
	 * @param token
	 */
	public void setToken(String token) {
		licenseInvoker.setToken(token);
	}

	public HashMap<String, Object> composebiling_info(String ProductType,int billingdate,String capacity) {
		HashMap<String, Object> temp = new HashMap<>();

		temp.put("product_type", ProductType);
		temp.put("capacity", capacity);
		temp.put("billingdate", billingdate);

		return temp;

	}
	public Response ActivateLicensefororagnziation(String validToken,
			String organziation_id,
			HashMap<String, Object> composebiling_info,
			int expctedststatuccode,
			SpogMessageCode expectedErrorMessage,
			ExtentTest test
			) {
		Response response=licenseInvoker.ActivateLicensefororagnziation(validToken,organziation_id, composebiling_info);
		spogServer.checkResponseStatus(response, expctedststatuccode,test);

		if(expctedststatuccode==SpogConstants.SUCCESS_GET_PUT_DELETE)
		{
			test.log(LogStatus.PASS, "LicenseActivated Sucessfully");
		}else
		{
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();

			if(code.contains("00A00302")){
				message = message.replace("{0}", organziation_id);
			}



			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + message);
		}
		return response;
	}

	}



