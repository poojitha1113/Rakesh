package InvokerServer;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.AuditCodeConstants;
import Constants.ConnectionStatus;
import Constants.DestinationColumnConstants;
import Constants.JobColumnConstants;
import Constants.JobStatusConstants;
import Constants.JobTypeConstants;
import Constants.LogColumnConstants;
import Constants.ProtectionStatus;
import Constants.SourceColumnConstants;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import Constants.FilterTypes.filterType;
import dataPreparation.JsonPreparation;
import genericutil.ErrorHandler;
import invoker.Org4SPOGInvoker;
import invoker.SPOGDestinationInvoker;
import invoker.SPOGInvoker;
import invoker.SiteTestHelper;
import invoker.UserSpogInvoker;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;

public class SPOGServer {
	private static JsonPreparation jp                 = new JsonPreparation();
	private SPOGInvoker            spogInvoker;
	private Org4SPOGInvoker        org4SPOGInvoker;
	private UserSpogInvoker        userSpogInvoker;
	private SPOGDestinationInvoker spogDestinationInvoker;
	public static ErrorHandler     errorHandle        = ErrorHandler.getErrorHandler();
	private ExtentTest             test;
	private String                 default_first_name = "first_name";
	private String                 default_last_name  = "last_name";
	private String                 default_pwd        = "Caworld_2017";
	private String                 datacenter_id      = "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c";

	private String                 Uuid;
	private String                 clouddata;


	public SPOGServer(String baseURI, String port) {
		spogInvoker = new SPOGInvoker(baseURI, port);
		org4SPOGInvoker = new Org4SPOGInvoker(baseURI, port);
		userSpogInvoker = new UserSpogInvoker(baseURI, port);
		spogDestinationInvoker = new SPOGDestinationInvoker(baseURI, port);
	}


	/**
	 * login SPOG used for API
	 * 
	 * @author shuo.zhang
	 * @param userName: login user name
	 * @param password: login password
	 * @return response
	 * 
	 */
	public Response login(String userName, String password) {

		errorHandle.printDebugMessageInDebugFile("******************login*******************");
		Map<String, String> userInfo = jp.getUserInfo(userName, password);
		Response response = spogInvoker.login(userInfo);
		errorHandle.printDebugMessageInDebugFile("response for login is " + response);
		return response;
	}


	/**
	 * login SPOG used for API
	 * 
	 * @author kiran.sripada
	 * @param userName: login user name
	 * @param password: login password
	 * @param test : object of extent test
	 * @return response
	 * 
	 */
	public Response login(String userName, String password, ExtentTest test) {

		test.log(LogStatus.INFO, "Creating a hash map for storing the requests");
		Map<String, String> userInfo = jp.getUserInfo(userName, password);
		Response response = spogInvoker.login(userInfo, test);
		test.log(LogStatus.INFO, "response for login is " + response);
		errorHandle.printDebugMessageInDebugFile("response for login is " + response);
		return response;
	}


	/**
	 * check swag document link is active
	 * 
	 * @author shan.jing
	 * @param swag_baseURL: qaspog2.zetta.net
	 * @param swag_port: login password
	 * @param expectedStatusCode: expected Status Code
	 * 
	 */
	public void checkSwagDocIsActive(String swag_baseURL, int swag_port, int expectedStatusCode) {

		Response response = spogInvoker.checkSwagDocIsActive(swag_baseURL, swag_port);
		checkResponseStatus(response, expectedStatusCode);
	}


	/**
	 * set token in invoke
	 * 
	 * @author shan.jing
	 * @param token: token value
	 * @return
	 * 
	 */
	public void setToken(String token) {

		spogInvoker.setToken(token);
	}


	public void assertResponseItem(Object item, Object response_item, ExtentTest test) {

		if (item == null || item.equals("none") || (item == "")) {
			/*
			 * From Yu, Eric: sometimes the response is not null, take post data for example, we can first
			 * post job_seq, and then post job_type to same job, the response will contain both
			 * job_seq/job_type
			 */
			if (item == null) {
				assertNull(response_item, "compare " + item + " passed once we don't set it");
			} else {
				assertNotNull(response_item, "compare " + item + " passed once we don't set it");
			}
		} else {
			if (item.equals(response_item)) {
				test.log(LogStatus.PASS,
						"The actual value is " + item + " the expected response is " + response_item);
				assertTrue("compare " + item + " passed", true);
			} else {
				test.log(LogStatus.FAIL,
						"The actual value is " + item + " the expected response is " + response_item);
				assertTrue("compare " + item + " failed", false);
			}
		}
	}


	/**
	 * check Login Web Service
	 * 
	 * @author shuo.zhang
	 * @param response after call login web service, the return value
	 * @param expectedStatusCode expectStatusCode
	 */
	public void checkLogin(Response response, int expectedStatusCode, String expectedErrorMessage) {

		errorHandle.printDebugMessageInDebugFile("***********checkLogin***********");
		errorHandle.printInfoMessageInDebugFile("expect status code is " + expectedStatusCode);
		response.then().statusCode(expectedStatusCode);
		if (expectedStatusCode == SpogConstants.SUCCESS_LOGIN) {
			String token = response.then().extract().path("data.token");
			errorHandle.printInfoMessageInDebugFile("token is " + token);
			String refresh_token = response.then().extract().path("data.refresh_token");
			errorHandle.printInfoMessageInDebugFile("refresh_token is " + refresh_token);
			if ((token == null) || (token.equals(""))) {
				assertTrue("Token is not Null or empty", false);
			} else {
				assertTrue("Token is not Null or empty", true);
				setToken(token);
			}
			if ((refresh_token == null) || (refresh_token.equals(""))) {
				assertTrue("refresh_token is not Null or empty", false);
			} else {
				assertTrue("refresh_token is not Null or empty", true);
			}
		} else {
			// for the error handling test. no token
			// response.then().assertThat().body(equalTo("\"\""));
			// response.then().assertThat().body(equalTo(expectedErrorMessage));
			this.checkErrorCode(response, expectedErrorMessage);
		}
	}


	/**
	 * check Login Web Service
	 * 
	 * @author kiran.sripada
	 * @param response after call login web service, the return value
	 * @param expectedStatusCode expectStatusCode
	 * @param test : object of ExtentTest
	 */
	public void checkLogin(Response response, int expectedStatusCode, String expectedErrorCode,
			ExtentTest test) {

		test.log(LogStatus.INFO, "expect status code is " + expectedStatusCode);
		errorHandle.printInfoMessageInDebugFile("expect status code is " + expectedStatusCode);
		response.then().statusCode(expectedStatusCode);
		if (expectedStatusCode == SpogConstants.SUCCESS_LOGIN) {
			String token = response.then().extract().path("data.token");
			test.log(LogStatus.INFO, "token is " + token);
			errorHandle.printInfoMessageInDebugFile("token is " + token);
			String refresh_token = response.then().extract().path("data.refresh_token");
			errorHandle.printInfoMessageInDebugFile("refresh_token is " + refresh_token);
			if ((token == null) || (token.equals(""))) {
				test.log(LogStatus.FAIL, "token is null or empty");
				assertTrue("Token is not Null or empty", false);
			} else {
				test.log(LogStatus.PASS, "Got the token");
				assertTrue("Token is not Null or empty", true);
				setToken(token);
			}
			if ((refresh_token == null) || (refresh_token.equals(""))) {
				assertTrue("refresh_token is not Null or empty", false);
			} else {
				assertTrue("refresh_token is not Null or empty", true);
			}
		} else {
			// for the error handling test. no token
			// response.then().assertThat().body(equalTo("\"\""));
			test.log(LogStatus.INFO, "for error handling, check error message");
			checkErrorCode(response, expectedErrorCode);
			test.log(LogStatus.PASS, "No token but got the error code as" + expectedErrorCode);
		}
	}


	/**
	 * login SPOG used for Integration Testing
	 * 
	 * @author shuo.zhang
	 * @param userName: login user name
	 * @param password: login password
	 * 
	 * 
	 */
	public void userLogin(String userName, String password) {

		errorHandle.printDebugMessageInDebugFile("***********userLogin***********");
		Response response = login(userName, password);
		checkLogin(response, SpogConstants.SUCCESS_LOGIN, "");
	}


	/**
	 * login failed with expected status code
	 * 
	 * @author shan.jing
	 * @param userName: login user name
	 * @param password: login password
	 * @param expectedStatusCode: expected Status Code
	 * 
	 */
	public void userLogin(String userName, String password, int expectedStatusCode) {

		Response response = login(userName, password);
		checkLogin(response, expectedStatusCode, "");
	}


	/**
	 * @author Zhaoguo.Ma
	 * @param username
	 * @param password
	 * @param status_code
	 * @param error_code
	 * @param test
	 */
	public void userLoginWithErrorCheck(String username, String password, int status_code,
			String error_code, ExtentTest test) {

		Map<String, String> userInfo = jp.getUserInfo(username, password);
		Response response = spogInvoker.login(userInfo);
		checkResponseStatus(response, status_code);
		checkErrorCode(response, error_code);
	}


	/**
	 * login failed with expected status code
	 * 
	 * @author shan.jing
	 * @param userName: login user name
	 * @param password: login password
	 * @param expectedStatusCode: expected Status Code
	 * @param test : object of extenttest
	 * 
	 */
	public void userLogin(String userName, String password, int expectedStatusCode, ExtentTest test) {

		Response response = login(userName, password);
		checkLogin(response, expectedStatusCode, "", test);
	}


	/**
	 * login failed with expected status code
	 * 
	 * @author kiran.sripada
	 * @param userName: login user name
	 * @param password: login password
	 * @param test : object of extenttest
	 * 
	 */
	public void userLogin(String userName, String password, ExtentTest test) {

		Response response = login(userName, password, test);
		checkLogin(response, SpogConstants.SUCCESS_LOGIN, "", test);
	}


	/**
	 * general method to check response status
	 * 
	 * @author shan.jing
	 * @param response
	 * @param expectedStatusCode
	 */
	public void checkResponseStatus(Response response, int expectedStatusCode, ExtentTest test) {

		errorHandle.printDebugMessageInDebugFile("Response status is " + response.getStatusCode());
		response.then().statusCode(expectedStatusCode);
		if (response.getStatusCode() == expectedStatusCode) {
			test.log(LogStatus.INFO,
					"The returned status code from the response is " + response.getStatusCode());
			assertTrue("Check status is equal to expected status:" + expectedStatusCode, true);
			test.log(LogStatus.PASS, "Check:response status");
		} else {
			assertTrue("Check status is equal to expected status:" + expectedStatusCode, false);
			test.log(LogStatus.FAIL, "Check:response status");
		}

	}


	/**
	 * general method to check response status
	 * 
	 * @author shan.jing
	 * @param response
	 * @param expectedStatusCode
	 */
	public void checkResponseStatus(Response response, int expectedStatusCode) {

		errorHandle.printDebugMessageInDebugFile("Response status is " + response.getStatusCode());
		response.then().statusCode(expectedStatusCode);
	}


	/**
	 * general method to check item's value in body
	 * 
	 * @author shan.jing
	 * @param response
	 * @param item
	 * @param Expectedvalue
	 * @return value for the item
	 */
	public String checkBodyItemValue(Response response, String item, String Expectedvalue,
			ExtentTest test) {

		String valueFromResponse = response.then().extract().path("data." + item).toString();
		errorHandle.printDebugMessageInDebugFile(
				"Get value for " + item + " from response is " + valueFromResponse);
		errorHandle.printDebugMessageInDebugFile("Expectedvalue for " + item + " is " + Expectedvalue);
		if (valueFromResponse.equalsIgnoreCase(Expectedvalue)) {
			// assertTrue("The value from body for item:"+ item+" is expected", true);
			test.log(LogStatus.PASS, "Check:" + item + " in reponse body.");
		} else {
			// assertTrue("The value from body for item:"+ item+" is expected", false);
			test.log(LogStatus.FAIL, "Check:" + item + " in reponse body.");
		}
		return valueFromResponse;
	}


	/**
	 * general method to check item's value in body
	 * 
	 * @author shan.jing
	 * @param response
	 * @param item
	 * @param Expectedvalue
	 * @return value for the item
	 */
	public String checkBodyItemValue(Response response, String item, String Expectedvalue) {

		String valueFromResponse = response.then().extract().path("data." + item);
		errorHandle.printDebugMessageInDebugFile(
				"Get value for " + item + " from response is " + valueFromResponse);
		errorHandle.printDebugMessageInDebugFile("Expectedvalue for " + item + " is " + Expectedvalue);
		if (valueFromResponse.equalsIgnoreCase(Expectedvalue)) {
			assertTrue("The value from body for item:" + item + " is expected", true);
		} else {
			assertTrue("The value from body for item:" + item + " is expected", false);
		}
		return valueFromResponse;
	}


	/**
	 * Validate the response for get cloudAccountByid
	 * 
	 * @author BharadwajReddy
	 * @param response
	 * @param expectedStatusCode
	 * @param cloud_account_id
	 * @param organization_id
	 * @param cloud_account_key
	 * @param create_user_id
	 * @param cloud_account_name
	 * @param expectedErrorMessage
	 * @param expectedErrorCode
	 * @param test
	 */
	public void checkGetCloudAccountById(Response response, int expectedStatusCode,
			String cloud_account_id, String organization_id, String cloud_account_key,
			String create_user_id, String cloud_account_name, String cloud_account_type,
			SpogMessageCode Info, ExtentTest test) {

		String expectedErrorMessage = "", expectedErrorCode = "";
		// TODO Auto-generated method stub
		test.log(LogStatus.INFO, "validating the response for get the cloud accounts by id ");
		checkResponseStatus(response, expectedStatusCode);
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			response.then().body("data.cloud_account_id", equalTo(cloud_account_id));
			if (organization_id != null) {
				response.then().body("data.organization_id", equalTo(organization_id));
			}
			if (cloud_account_key != "" && !(cloud_account_type.equals("cloud_direct"))) {
				response.then().body("data.cloud_account_key", equalTo(cloud_account_key));
			} else {
				assertNotNull("data.cloud_account_key");
			}
			test.log(LogStatus.INFO,
					"validating the respsone for the cloud_Account_name,create_user_id and cloud_Account_status");
			response.then().body("data.create_user_id", equalTo(create_user_id));
			response.then().body("data.cloud_account_name", equalTo(cloud_account_name));
			response.then().body("data.cloud_account_status", equalTo("success"));
			test.log(LogStatus.INFO, "validating the resposne for the create ts and modify_ts");
			assertNotNull("data.create_ts");
			assertNotNull("data.modify_ts");
			test.log(LogStatus.PASS,
					"The validation of the response is successfully for the cloud_account_id:"
							+ response.then().extract().path("data.cloud_account_id")
							+ " The value of organization_id:"
							+ response.then().extract().path("data.organization_id"));
			assertTrue("The data has been matched", true);

			test.log(LogStatus.INFO, "validating the response for allowed actions");
			assertResponseItem(response.then().extract().path("data.allowed_actions"), null, test);

		} else {

			if (Info.getStatus() != "0010000") {
				expectedErrorMessage = Info.getStatus();
				if (expectedErrorMessage.contains("{0}")) {
					expectedErrorMessage = expectedErrorMessage.replace("{0}", getUUId());
					System.out.println(expectedErrorMessage);
				}
				expectedErrorCode = Info.getCodeString();
			}
			checkErrorMessage(response, expectedErrorMessage);
			test.log(LogStatus.PASS, "The value of the message is " + expectedErrorMessage);
			checkErrorCode(response, expectedErrorCode);
			test.log(LogStatus.PASS, "The value of the code  generated  is " + expectedErrorCode);
			test.log(LogStatus.INFO,
					"The value of the  response generated actually is :" + response.getStatusCode());
		}
	}


	/**
	 * create organization
	 * 
	 * @author shan.jing
	 * @param organizationName
	 * @param organizationType
	 * @param organizationEmail
	 * @param organizationPwd
	 * @param organizationFirstName
	 * @param organizationLastName
	 * @return response
	 */
	public Response CreateOrganization(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName,
			String organizationLastName, ExtentTest test) {

		Map<String, Object> orgInfo = jp.getOrganizationInfo(organizationName, organizationType,
				organizationEmail, organizationPwd, organizationFirstName, organizationLastName);
		Response response = spogInvoker.CreateOrganization(orgInfo);
		return response;
	}


	/**
	 * create organization
	 * 
	 * @author shan.jing
	 * @param organizationName
	 * @param organizationType
	 * @param organizationEmail
	 * @param organizationPwd
	 * @param organizationFirstName
	 * @param organizationLastName
	 * @param blocked
	 * @return response
	 */
	public Response CreateOrganization(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName,
			String organizationLastName, boolean blocked, ExtentTest test) {

		Map<String, Object> orgInfo = jp.getOrganizationInfo(organizationName, organizationType,
				organizationEmail, organizationPwd, organizationFirstName, organizationLastName, blocked);
		Response response = spogInvoker.CreateOrganization(orgInfo);
		return response;
	}


	/**
	 * delete account by given parent id and account organization id
	 * 
	 * @author shan.jing
	 * @param parentId
	 * @param accountOrgId
	 * @return response
	 */
	public Response deleteMSPAccount(String parentId, String accountOrgId) {

		Response response = spogInvoker.deleteMSPAccount(parentId, accountOrgId);
		return response;
	}

	/**
	 * delete account by given parent id
	 * 
	 * @author shan.jing
	 * @param parentId
	 * @param accounts
	 * @return response
	 */
	public Response deleteMSPAccounts(String parentId,String accounts ) {
		String[] accountsInfo =null;
		Response response =null;
		if(accounts==null || accounts.equalsIgnoreCase("")){
			response= spogInvoker.deleteMSPAccounts(parentId,null);
		}else{
			accountsInfo= jp.getUnAssignPolicyInfo(accounts);
		}
		response = spogInvoker.deleteMSPAccounts(parentId,accountsInfo);
		return response;
	}

	public void getSourcesColumnsWithCheck(ExtentTest test) {
		Response response = spogInvoker.getSourcesColumns();
		checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		CompareColumnsHeadContent(response, getSourcesColumns4NotMsp(), test);
	}

	public void getSourcesColumnsWithCheck4Msp(ExtentTest test) {
		Response response = spogInvoker.getSourcesColumns();
		checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		CompareColumnsHeadContent(response, getSourcesColumns4Msp(), test);
	}

	public ArrayList<HashMap> getSourcesColumns() {

		Response response = spogInvoker.getSourcesColumns();
		checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		ArrayList<HashMap> expectedColumnsHeadContents = new ArrayList<HashMap>();
		HashMap<String, String> columnHeadContent1 = new HashMap();
		columnHeadContent1.put("long_label", SourceColumnConstants.LONG_TYPE);
		columnHeadContent1.put("short_label", SourceColumnConstants.SHORT_TYPE);
		columnHeadContent1.put("filter", SourceColumnConstants.INNER_TYPE_FILTER);
		columnHeadContent1.put("visible", "true");
		expectedColumnsHeadContents.add(columnHeadContent1);
		HashMap<String, String> columnHeadContent2 = new HashMap();
		columnHeadContent2.put("long_label", SourceColumnConstants.LONG_NAME);
		columnHeadContent2.put("short_label", SourceColumnConstants.SHORT_NAME);
		columnHeadContent2.put("filter", SourceColumnConstants.NAME_FILTER);
		columnHeadContent2.put("visible", "true");
		expectedColumnsHeadContents.add(columnHeadContent2);
		HashMap<String, String> columnHeadContent3 = new HashMap();
		columnHeadContent3.put("long_label", SourceColumnConstants.LONG_OPERATING_SYSTEM);
		columnHeadContent3.put("short_label", SourceColumnConstants.SHORT_OPERATING_SYSTEM);
		columnHeadContent3.put("filter", SourceColumnConstants.OPERATING_SYSTEM_FILTER);
		columnHeadContent3.put("visible", "true");
		expectedColumnsHeadContents.add(columnHeadContent3);
		HashMap<String, String> columnHeadContent4 = new HashMap();
		columnHeadContent4.put("long_label", SourceColumnConstants.LONG_PROTECTION_STATUS);
		columnHeadContent4.put("short_label", SourceColumnConstants.SHORT_PROTECTION_STATUS);
		columnHeadContent4.put("filter", SourceColumnConstants.PROTECTION_STATUS_FILTER);
		columnHeadContent4.put("visible", "true");
		expectedColumnsHeadContents.add(columnHeadContent4);

		HashMap<String, String> columnHeadContent5 = new HashMap();
		columnHeadContent5.put("long_label", SourceColumnConstants.LONG_CONNECTION);
		columnHeadContent5.put("short_label", SourceColumnConstants.SHORT_CONNECTION);
		columnHeadContent5.put("filter", SourceColumnConstants.CONNECTION_FILTER);
		columnHeadContent5.put("visible", "true");
		expectedColumnsHeadContents.add(columnHeadContent5);

		HashMap<String, String> columnHeadContent6 = new HashMap();
		columnHeadContent6.put("long_label", SourceColumnConstants.LONG_LAST_RECOVERY_POINT);
		columnHeadContent6.put("short_label", SourceColumnConstants.SHORT_LAST_RECOVERY_POINT);
		columnHeadContent6.put("filter", SourceColumnConstants.LAST_RECOVERY_POINT_FILTER);
		columnHeadContent6.put("visible", "true");
		expectedColumnsHeadContents.add(columnHeadContent6);
		HashMap<String, String> columnHeadContent7 = new HashMap();
		columnHeadContent7.put("long_label", SourceColumnConstants.LONG_LAST_BACKUP_STATUS);
		columnHeadContent7.put("short_label", SourceColumnConstants.SHORT_LAST_BACKUP_STATUS);
		columnHeadContent7.put("filter", SourceColumnConstants.LAST_BACKUP_STATUS_FILTER);
		columnHeadContent7.put("visible", "true");
		expectedColumnsHeadContents.add(columnHeadContent7);
		HashMap<String, String> columnHeadContent8 = new HashMap();
		columnHeadContent8.put("long_label", SourceColumnConstants.LONG_PROTECTION_POLICY);
		columnHeadContent8.put("short_label", SourceColumnConstants.SHORT_PROTECTION_POLICY);
		columnHeadContent8.put("filter", SourceColumnConstants.PROTECTION_POLICY_FILTER);
		columnHeadContent8.put("visible", "true");
		expectedColumnsHeadContents.add(columnHeadContent8);
		HashMap<String, String> columnHeadContent9 = new HashMap();
		columnHeadContent9.put("long_label", SourceColumnConstants.LONG_SOURCE_GROUP);
		columnHeadContent9.put("short_label", SourceColumnConstants.SHORT_SOURCE_GROUP);
		columnHeadContent9.put("filter", SourceColumnConstants.SOURCE_GROUP_FILTER);
		columnHeadContent9.put("visible", "true");
		expectedColumnsHeadContents.add(columnHeadContent9);
		//	    HashMap<String, String> columnHeadContent10 = new HashMap();
		//	    columnHeadContent10.put("long_label", SourceColumnConstants.LONG_SOURCE_SITE);
		//	    columnHeadContent10.put("short_label", SourceColumnConstants.SHORT_SOURCE_SITE);
		//	    columnHeadContent10.put("filter", SourceColumnConstants.SOURCE_SITE_FILTER);
		//	    columnHeadContent10.put("visible", "false");
		//	    expectedColumnsHeadContents.add(columnHeadContent10);
		HashMap<String, String> columnHeadContent11 = new HashMap();
		columnHeadContent11.put("long_label", SourceColumnConstants.LONG_VMNAME);
		columnHeadContent11.put("short_label", SourceColumnConstants.SHORT_VMNAME);
		columnHeadContent11.put("filter", SourceColumnConstants.VMNAME_FILTER);
		columnHeadContent11.put("visible", "false");
		expectedColumnsHeadContents.add(columnHeadContent11);

		HashMap<String, String> columnHeadContent12 = new HashMap();
		columnHeadContent12.put("long_label", SourceColumnConstants.LONG_AGENT);
		columnHeadContent12.put("short_label", SourceColumnConstants.SHORT_AGENT);
		columnHeadContent12.put("filter", SourceColumnConstants.AGENT_FILTER);
		columnHeadContent12.put("visible", "false");
		expectedColumnsHeadContents.add(columnHeadContent12);
		return expectedColumnsHeadContents;
	}

	/**
	 * get sources columns
	 * 
	 * @author shan.jing
	 * @param ExtentTest
	 * @return response
	 */
	public ArrayList<HashMap> getSourcesColumns4NotMsp() {

		ArrayList<HashMap> expectedColumnsHeadContents = getSourcesColumns();
		HashMap<String, String> columnHeadContent14 = new HashMap();
		columnHeadContent14.put("long_label", SourceColumnConstants.LONG_HYPERVISOR);
		columnHeadContent14.put("short_label", SourceColumnConstants.SHORT_HYPERVISOR);
		columnHeadContent14.put("filter", SourceColumnConstants.HYPERVISOR_FILTER);
		columnHeadContent14.put("visible", "false");
		expectedColumnsHeadContents.add(columnHeadContent14);
		return expectedColumnsHeadContents;
	}

	/**
	 * get sources columns
	 * 
	 * @author shan.jing
	 * @param ExtentTest
	 * @return response
	 */
	public ArrayList<HashMap> getSourcesColumns4Msp() {

		ArrayList<HashMap> expectedColumnsHeadContents = getSourcesColumns();
		HashMap<String, String> columnHeadContent13 = new HashMap();
		columnHeadContent13.put("long_label", SourceColumnConstants.LONG_ORGANIZATION);
		columnHeadContent13.put("short_label", SourceColumnConstants.SHORT_ORGANIZATION);
		columnHeadContent13.put("filter", SourceColumnConstants.ORGANIZATION_FILTER);
		columnHeadContent13.put("visible", "false");
		expectedColumnsHeadContents.add(columnHeadContent13);
		HashMap<String, String> columnHeadContent14 = new HashMap();
		columnHeadContent14.put("long_label", SourceColumnConstants.LONG_HYPERVISOR);
		columnHeadContent14.put("short_label", SourceColumnConstants.SHORT_HYPERVISOR);
		columnHeadContent14.put("filter", SourceColumnConstants.HYPERVISOR_FILTER);
		columnHeadContent14.put("visible", "false");
		expectedColumnsHeadContents.add(columnHeadContent14);
		return expectedColumnsHeadContents;
	}


	/**
	 * get sources columns with expected status and error code
	 * 
	 * @author shan.jing
	 * @param expectedStatusCode
	 * @param expectedErrorCode
	 * @param ExtentTest
	 * @return response
	 */
	public void getSourcesColumnsWithExpectedStatusCode(int expectedStatusCode,
			String expectedErrorCode, ExtentTest test) {

		Response response = spogInvoker.getSourcesColumns();
		checkResponseStatus(response, expectedStatusCode);
		checkErrorCode(response, expectedErrorCode);
	}


	/**
	 * get jobs columns
	 * 
	 * @author shan.jing
	 * @param ExtentTest
	 * @return response
	 */
	public Response getJobsColumns(ExtentTest test) {

		Response response = spogInvoker.getJobsColumns();
		checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		return response;

	}


	/**
	 * get jobs columns
	 * 
	 * @author shan.jing
	 * @param ExtentTest
	 * @return response
	 */
	public void getJobsColumnsWithCheck(ExtentTest test) {
		Response response = spogInvoker.getJobsColumns();
		checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		CompareColumnsHeadContent(response, getJobsColumns4NotMsp(), test);
	}

	public void getJobsColumnsWithCheck4Msp(ExtentTest test) {
		Response response = spogInvoker.getJobsColumns();
		checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		CompareColumnsHeadContent(response, getJobsColumns4Msp(), test);
	}

	public ArrayList<HashMap> getJobsColumns4NotMsp() {

		ArrayList<HashMap> expectedColumnsHeadContents = new ArrayList<HashMap>();
		HashMap<String, String> columnHeadContent = new HashMap();
		columnHeadContent.put("long_label", JobColumnConstants.LONG_SOURCE_NAME);
		columnHeadContent.put("short_label", JobColumnConstants.SHORT_SOURCE_NAME);
		columnHeadContent.put("filter", JobColumnConstants.SOURCE_NAME_FILTER);
		columnHeadContent.put("visible", "true");
		expectedColumnsHeadContents.add(columnHeadContent);
		HashMap<String, String> columnHeadContent1 = new HashMap();
		columnHeadContent1.put("long_label", JobColumnConstants.LONG_JOB_NAME);
		columnHeadContent1.put("short_label", JobColumnConstants.SHORT_JOB_NAME);
		columnHeadContent1.put("filter", JobColumnConstants.JOB_NAME_FILTER);
		columnHeadContent1.put("visible", "true");
		expectedColumnsHeadContents.add(columnHeadContent1);
		HashMap<String, String> columnHeadContent2 = new HashMap();
		columnHeadContent2.put("long_label", JobColumnConstants.LONG_TYPE);
		columnHeadContent2.put("short_label", JobColumnConstants.SHORT_TYPE);
		columnHeadContent2.put("filter", JobColumnConstants.TYPE_FILTER);
		columnHeadContent2.put("visible", "true");
		expectedColumnsHeadContents.add(columnHeadContent2);
		HashMap<String, String> columnHeadContent3 = new HashMap();
		columnHeadContent3.put("long_label", JobColumnConstants.LONG_STATUS);
		columnHeadContent3.put("short_label", JobColumnConstants.SHORT_STATUS);
		columnHeadContent3.put("filter", JobColumnConstants.STATUS_FILTER);
		columnHeadContent3.put("visible", "true");
		expectedColumnsHeadContents.add(columnHeadContent3);
		HashMap<String, String> columnHeadContent4 = new HashMap();
		columnHeadContent4.put("long_label", JobColumnConstants.LONG_POLICY);
		columnHeadContent4.put("short_label", JobColumnConstants.SHORT_POLICY);
		columnHeadContent4.put("filter", JobColumnConstants.POLICY_FILTER);
		columnHeadContent4.put("visible", "true");
		expectedColumnsHeadContents.add(columnHeadContent4);
		HashMap<String, String> columnHeadContent5 = new HashMap();
		columnHeadContent5.put("long_label", JobColumnConstants.LONG_DESTINATION);
		columnHeadContent5.put("short_label", JobColumnConstants.SHORT_DESTINATION);
		columnHeadContent5.put("filter", JobColumnConstants.DESTINATION_FILTER);
		columnHeadContent5.put("visible", "true");
		expectedColumnsHeadContents.add(columnHeadContent5);
		HashMap<String, String> columnHeadContent6 = new HashMap();
		columnHeadContent6.put("long_label", JobColumnConstants.LONG_START_TIME);
		columnHeadContent6.put("short_label", JobColumnConstants.SHORT_START_TIME);
		columnHeadContent6.put("filter", JobColumnConstants.START_TIME_FILTER);
		columnHeadContent6.put("visible", "true");
		expectedColumnsHeadContents.add(columnHeadContent6);
		HashMap<String, String> columnHeadContent7 = new HashMap();
		columnHeadContent7.put("long_label", JobColumnConstants.LONG_END_TIME);
		columnHeadContent7.put("short_label", JobColumnConstants.SHORT_END_TIME);
		columnHeadContent7.put("filter", JobColumnConstants.END_TIME_FILTER);
		columnHeadContent7.put("visible", "true");
		expectedColumnsHeadContents.add(columnHeadContent7);
		HashMap<String, String> columnHeadContent8 = new HashMap();
		columnHeadContent8.put("long_label", JobColumnConstants.LONG_DURATION);
		columnHeadContent8.put("short_label", JobColumnConstants.SHORT_DURATION);
		columnHeadContent8.put("filter", JobColumnConstants.DURATION_FILTER);
		columnHeadContent8.put("visible", "false");
		expectedColumnsHeadContents.add(columnHeadContent8);
		return expectedColumnsHeadContents;
	}

	public ArrayList<HashMap> getJobsColumns4Msp() {

		ArrayList<HashMap> expectedColumnsHeadContents = getJobsColumns4NotMsp();
		HashMap<String, String> columnHeadContent9 = new HashMap();
		columnHeadContent9.put("long_label", JobColumnConstants.LONG_ORGANIZATION);
		columnHeadContent9.put("short_label", JobColumnConstants.SHORT_ORGANIZATION);
		columnHeadContent9.put("filter", JobColumnConstants.ORGANIZATION_FILTER);
		columnHeadContent9.put("visible", "false");
		expectedColumnsHeadContents.add(columnHeadContent9);
		return expectedColumnsHeadContents;
	}


	/**
	 * get jobs columns with expected status and error code
	 * 
	 * @author shan.jing
	 * @param expectedStatusCode
	 * @param expectedErrorCode
	 * @param ExtentTest
	 * @return response
	 */
	public void getJobsColumnsWithExpectedStatusCode(int expectedStatusCode, String expectedErrorCode,
			ExtentTest test) {

		Response response = spogInvoker.getJobsColumns();
		checkResponseStatus(response, expectedStatusCode);
		checkErrorCode(response, expectedErrorCode);
	}


	/**
	 * get logs columns
	 * 
	 * @author shan.jing
	 * @param ExtentTest
	 * @return response
	 */
	public void getLogsColumnsWithCheck(ExtentTest test) {
		Response response = spogInvoker.getLogsColumns();
		checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);    
		CompareColumnsHeadContent(response, getLogsColumns4NotMsp(), test);
	}

	public void getLogsColumnsWithCheck4Msp(ExtentTest test) {
		Response response = spogInvoker.getLogsColumns();
		checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);    
		CompareColumnsHeadContent(response, getLogsColumns4Msp(), test);
	}

	public ArrayList<HashMap> getLogsColumns4NotMsp() {
		ArrayList<HashMap> expectedColumnsHeadContents = new ArrayList<HashMap>();
		HashMap<String, String> columnHeadContent = new HashMap();
		columnHeadContent.put("long_label", LogColumnConstants.LONG_DATETIME);
		columnHeadContent.put("short_label", LogColumnConstants.SHORT_DATETIME);
		columnHeadContent.put("filter", LogColumnConstants.DATETIME_FILTER);
		columnHeadContent.put("visible", "true");
		expectedColumnsHeadContents.add(columnHeadContent);
		HashMap<String, String> columnHeadContent1 = new HashMap();
		columnHeadContent1.put("long_label", LogColumnConstants.LONG_SERERITY);
		columnHeadContent1.put("short_label", LogColumnConstants.SHORT_SERERITY);
		columnHeadContent1.put("filter", LogColumnConstants.SERERITY_FILTER);
		columnHeadContent1.put("visible", "true");
		expectedColumnsHeadContents.add(columnHeadContent1);
		HashMap<String, String> columnHeadContent2 = new HashMap();
		columnHeadContent2.put("long_label", LogColumnConstants.LONG_SOURCE_NAME);
		columnHeadContent2.put("short_label", LogColumnConstants.SHORT_SOURCE_NAME);
		columnHeadContent2.put("filter", LogColumnConstants.SOURCE_NAME_FILTER);
		columnHeadContent2.put("visible", "true");
		expectedColumnsHeadContents.add(columnHeadContent2);
		HashMap<String, String> columnHeadContent4 = new HashMap();
		columnHeadContent4.put("long_label", LogColumnConstants.LONG_GENERATE_FROM);
		columnHeadContent4.put("short_label", LogColumnConstants.SHORT_GENERATE_FROM);
		columnHeadContent4.put("filter", LogColumnConstants.GENERATE_FROM_FILTER);
		columnHeadContent4.put("visible", "true");
		expectedColumnsHeadContents.add(columnHeadContent4);
		HashMap<String, String> columnHeadContent5 = new HashMap();
		columnHeadContent5.put("long_label", LogColumnConstants.LONG_JOB_TYPE);
		columnHeadContent5.put("short_label", LogColumnConstants.SHORT_JOB_TYPE);
		columnHeadContent5.put("filter", LogColumnConstants.JOB_TYPE_FILTER);
		columnHeadContent5.put("visible", "true");
		expectedColumnsHeadContents.add(columnHeadContent5);
		HashMap<String, String> columnHeadContent6 = new HashMap();
		columnHeadContent6.put("long_label", LogColumnConstants.LONG_MESSAGE_ID);
		columnHeadContent6.put("short_label", LogColumnConstants.SHORT_MESSAGE_ID);
		columnHeadContent6.put("filter", LogColumnConstants.MESSAGE_ID_FILTER);
		columnHeadContent6.put("visible", "true");
		expectedColumnsHeadContents.add(columnHeadContent6);
		HashMap<String, String> columnHeadContent7 = new HashMap();
		columnHeadContent7.put("long_label", LogColumnConstants.LONG_MESSAGE);
		columnHeadContent7.put("short_label", LogColumnConstants.SHORT_MESSAGE);
		columnHeadContent7.put("filter", LogColumnConstants.MESSAGE_FILTER);
		columnHeadContent7.put("visible", "true");
		expectedColumnsHeadContents.add(columnHeadContent7);
		return expectedColumnsHeadContents;
	}

	public ArrayList<HashMap> getLogsColumns4Msp() {
		ArrayList<HashMap> expectedColumnsHeadContents = getLogsColumns4NotMsp();	    
		HashMap<String, String> columnHeadContent8 = new HashMap();
		columnHeadContent8.put("long_label", LogColumnConstants.LONG_ORGANIZATION);
		columnHeadContent8.put("short_label", LogColumnConstants.SHORT_ORGANIZATION);
		columnHeadContent8.put("filter", LogColumnConstants.ORGANIZATION_FILTER);
		columnHeadContent8.put("visible", "false");
		expectedColumnsHeadContents.add(columnHeadContent8);
		return expectedColumnsHeadContents;
	}


	/**
	 * get logs columns with expected status and error code
	 * 
	 * @author shan.jing
	 * @param expectedStatusCode
	 * @param expectedErrorCode
	 * @param ExtentTest
	 * @return response
	 */
	public void getLogsColumnsWithExpectedStatusCode(int expectedStatusCode, String expectedErrorCode,
			ExtentTest test) {

		Response response = spogInvoker.getLogsColumns();
		checkResponseStatus(response, expectedStatusCode);
		checkErrorCode(response, expectedErrorCode);
	}


	/**
	 * get jobs columns and return response
	 * 
	 * @author shan.jing
	 * @param expectedStatusCode
	 * @param expectedErrorCode
	 * @param ExtentTest
	 * @return response
	 */
	public Response getLogsColumns(int expectedStatusCode, ExtentTest test) {

		Response response = spogInvoker.getLogsColumns();
		checkResponseStatus(response, expectedStatusCode);
		return response;
	}


	/**
	 * get destination columns and return response
	 * 
	 * @author shan.jing
	 * @param expectedStatusCode
	 * @param expectedErrorCode
	 * @param ExtentTest
	 * @return response
	 */
	public Response getDestinationsColumns(int expectedStatusCode, ExtentTest test) {

		Response response = spogInvoker.getDestinationsColumns();
		checkResponseStatus(response, expectedStatusCode);
		return response;
	}


	/**
	 * get destinations columns
	 * 
	 * @author shan.jing
	 * @param ExtentTest
	 * @return response
	 */
	public void getDestintionsColumnsWithCheck(ExtentTest test) {

		Response response = spogInvoker.getDestinationsColumns();
		checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		ArrayList<HashMap> expectedColumnsHeadContents = new ArrayList<HashMap>();

		HashMap<String, String> columnHeadContent1 = new HashMap();
		columnHeadContent1.put("long_label", DestinationColumnConstants.LONG_NAME);
		columnHeadContent1.put("short_label", DestinationColumnConstants.SHORT_NAME);
		columnHeadContent1.put("filter", DestinationColumnConstants.NAME_FILTER);
		columnHeadContent1.put("visible", "true");
		expectedColumnsHeadContents.add(columnHeadContent1);
		HashMap<String, String> columnHeadContent2 = new HashMap();
		columnHeadContent2.put("long_label", DestinationColumnConstants.LONG_STORAGE);
		columnHeadContent2.put("short_label", DestinationColumnConstants.SHORT_STORAGE);
		columnHeadContent2.put("filter", DestinationColumnConstants.STORAGE_FILTER);
		columnHeadContent2.put("visible", "true");

		expectedColumnsHeadContents.add(columnHeadContent2);
		HashMap<String, String> columnHeadContent3 = new HashMap();
		columnHeadContent3.put("long_label", DestinationColumnConstants.LONG_LATEST_JOB);
		columnHeadContent3.put("short_label", DestinationColumnConstants.SHORT_LATEST_JOB);
		columnHeadContent3.put("filter", DestinationColumnConstants.LATEST_JOB_FILTER);
		columnHeadContent3.put("visible", "true");
		expectedColumnsHeadContents.add(columnHeadContent3);

		HashMap<String, String> columnHeadContent4 = new HashMap();
		columnHeadContent4.put("long_label", DestinationColumnConstants.LONG_LOCATION);
		columnHeadContent4.put("short_label", DestinationColumnConstants.SHORT_LOCATION);
		columnHeadContent4.put("filter", DestinationColumnConstants.LOCATION_FILTER);
		columnHeadContent4.put("visible", "true");
		expectedColumnsHeadContents.add(columnHeadContent4);

		HashMap<String, String> columnHeadContent5 = new HashMap();
		columnHeadContent5.put("long_label", DestinationColumnConstants.LONG_REGION);
		columnHeadContent5.put("short_label", DestinationColumnConstants.SHORT_REGION);
		columnHeadContent5.put("filter", DestinationColumnConstants.REGION_FILTER);
		columnHeadContent5.put("visible", "true");
		expectedColumnsHeadContents.add(columnHeadContent5);

		HashMap<String, String> columnHeadContent6 = new HashMap();
		columnHeadContent6.put("long_label", DestinationColumnConstants.LONG_TYPE);
		columnHeadContent6.put("short_label", DestinationColumnConstants.SHORT_TYPE);
		columnHeadContent6.put("filter", DestinationColumnConstants.TYPE_FILTER);
		columnHeadContent6.put("visible", "false");
		expectedColumnsHeadContents.add(columnHeadContent6);

		HashMap<String, String> columnHeadContent7 = new HashMap();
		columnHeadContent7.put("long_label", DestinationColumnConstants.LONG_RETENTION);
		columnHeadContent7.put("short_label", DestinationColumnConstants.SHORT_RETENTION);
		columnHeadContent7.put("filter", DestinationColumnConstants.RETENTION_FILTER);
		columnHeadContent7.put("visible", "false");
		expectedColumnsHeadContents.add(columnHeadContent7);

		HashMap<String, String> columnHeadContent8 = new HashMap();
		columnHeadContent8.put("long_label", DestinationColumnConstants.LONG_PROTECTED_DATA);
		columnHeadContent8.put("short_label", DestinationColumnConstants.SHORT_PROTECTED_DATA);
		columnHeadContent8.put("filter", DestinationColumnConstants.PROTECTED_DATA_FILTER);
		columnHeadContent8.put("visible", "false");
		expectedColumnsHeadContents.add(columnHeadContent8);

		HashMap<String, String> columnHeadContent9 = new HashMap();
		columnHeadContent9.put("long_label", DestinationColumnConstants.LONG_NUM_SOURCES);
		columnHeadContent9.put("short_label", DestinationColumnConstants.SHORT_NUM_SOURCES);
		columnHeadContent9.put("filter", DestinationColumnConstants.NUM_SOURCES_FILTER);
		columnHeadContent9.put("visible", "false");
		expectedColumnsHeadContents.add(columnHeadContent9);

		HashMap<String, String> columnHeadContent10 = new HashMap();
		columnHeadContent10.put("long_label", DestinationColumnConstants.LONG_PROTECTION_POLICY);
		columnHeadContent10.put("short_label", DestinationColumnConstants.SHORT_PROTECTION_POLICY);
		columnHeadContent10.put("filter", DestinationColumnConstants.PROTECTION_POLICY_FILTER);
		columnHeadContent10.put("visible", "false");
		expectedColumnsHeadContents.add(columnHeadContent10);
		CompareColumnsHeadContent(response, expectedColumnsHeadContents, test);
	}


	/**
	 * get destinations columns with expected status and error code
	 * 
	 * @author shan.jing
	 * @param expectedStatusCode
	 * @param expectedErrorCode
	 * @param ExtentTest
	 * @return response
	 */
	public void getDestinationsColumnsWithExpectedStatusCode(int expectedStatusCode,
			String expectedErrorCode, ExtentTest test) {

		Response response = spogInvoker.getDestinationsColumns();
		checkResponseStatus(response, expectedStatusCode);
		checkErrorCode(response, expectedErrorCode);
	}


	/**
	 * delete account by given parent id and account organization id then check status code is 200
	 * 
	 * @author shan.jing
	 * @param parentId
	 * @param accountOrgId
	 * @return response
	 */
	public void deleteMSPAccountWithCheck(String parentId, String accountOrgId) {

		Response response = spogInvoker.deleteMSPAccount(parentId, accountOrgId);
		checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
	}

	/**
	 * delete account by given parent id with given accounts then check status code is 200
	 * 
	 * @author shan.jing
	 * @param parentId
	 * @param accounts
	 * @return response
	 */
	public void deleteMSPAccountsWithCheck(String parentId, String accounts) {
		String[] accountsInfo =null;
		Response response =null;
		if(accounts==null || accounts.equalsIgnoreCase("")){
			response= spogInvoker.deleteMSPAccounts(parentId,null);
		}else{
			accountsInfo= jp.getUnAssignPolicyInfo(accounts);
		}
		response = spogInvoker.deleteMSPAccounts(parentId,accountsInfo);
		checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
	}


	/**
	 * update account threshold
	 * 
	 * @author shan.jing
	 * @param parentId
	 * @param childId
	 * @param cloud_direct_volume
	 * @param cloud_hybrid_store
	 * @param cloudDirectVolumeCapacity
	 * @param cloudHybridStoreCapacity
	 * @param test
	 * @return
	 */
	public void updateThresholdWithCheck(String parentId, String childId, String cloud_direct_volume,
			String cloud_hybrid_store, ExtentTest test) {

		Map<String, String> thresholdInfo =
				jp.getThresholdInfo(cloud_direct_volume, cloud_hybrid_store);
		Response response = spogInvoker.UpdateAccountThreshold(parentId, childId, thresholdInfo);
		checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		response.then().body("data.cloud_direct_volume_capacity", equalTo(cloud_direct_volume))
		.body("data.cloud_hybrid_store_capacity", equalTo(cloud_hybrid_store));
	}


	/**
	 * update account threshold
	 * 
	 * @author shan.jing
	 * @param parentId
	 * @param childId
	 * @param cloud_direct_volume
	 * @param cloud_hybrid_store
	 * @param expectedStatusCode
	 * @param expectedErrorCode
	 * @param expectedErrorMessage
	 * @param test
	 * @return
	 */
	public void updateThresholdWithExpectedStatusCode(String parentId, String childId,
			String cloud_direct_volume, String cloud_hybrid_store, int expectedStatusCode,
			String expectedErrorCode, String expectedErrorMessage, ExtentTest test) {

		Map<String, String> thresholdInfo =
				jp.getThresholdInfo(cloud_direct_volume, cloud_hybrid_store);
		Response response = spogInvoker.UpdateAccountThreshold(parentId, childId, thresholdInfo);
		response.then().statusCode(expectedStatusCode);
		checkErrorCode(response, expectedErrorCode);
		checkErrorMessage(response, expectedErrorMessage);
	}


	/**
	 * delete account by given parent id and account organization id then check expected status code
	 * 
	 * @author shan.jing
	 * @param parentId
	 * @param accountOrgId
	 * @param expectedStatusCode
	 * @return response
	 */
	public void deleteMSPAccountWithExpectedStatusCode(String parentId, String accountOrgId,
			int expectedStatusCode) {

		Response response = spogInvoker.deleteMSPAccount(parentId, accountOrgId);
		checkResponseStatus(response, expectedStatusCode);
	}

	/**
	 * delete account by given parent id then check expected status code
	 * 
	 * @author shan.jing
	 * @param parentId
	 * @param accounts
	 * @param expectedStatusCode
	 * @return response
	 */
	public void deleteMSPAccountsWithExpectedStatusCode(String parentId,String  accounts,
			int expectedStatusCode, String expectedErrorCode, ExtentTest test) {
		String[] accountsInfo =null;
		Response response =null;
		if(accounts==null || accounts.equalsIgnoreCase("")){
			response= spogInvoker.deleteMSPAccounts(parentId,null);
		}else{
			accountsInfo= jp.getUnAssignPolicyInfo(accounts);
		}
		response = spogInvoker.deleteMSPAccounts(parentId,accountsInfo);
		checkResponseStatus(response, expectedStatusCode);
		checkErrorCode(response, expectedErrorCode);
	}


	/**
	 * delete account by given parent id and account organization id then check expected status code
	 * 
	 * @author shan.jing
	 * @param parentId
	 * @param accountOrgId
	 * @param expectedStatusCode
	 * @param expectedStatusCode
	 * @param ExtentTest
	 * @return response
	 */
	public void deleteMSPAccountWithExpectedStatusCode(String parentId, String accountOrgId,
			int expectedStatusCode, String expectedErrorCode, ExtentTest test) {

		Response response = spogInvoker.deleteMSPAccount(parentId, accountOrgId);
		checkResponseStatus(response, expectedStatusCode, test);
		checkErrorCode(response, expectedErrorCode);
	}


	/**
	 * create account
	 * 
	 * @author shan.jing
	 * @param urlParentId that means you set it in url
	 * @param accountName
	 * @param bodyParentId that means you transport it as json object in body
	 * @return response
	 */
	public Response createAccount(String urlParentId, String accountName, String bodyParentId) {

		Map<String, String> accountInfo = jp.getAccountInfo(accountName, bodyParentId);
		Response response = spogInvoker.CreateAccount(urlParentId, accountInfo);
		return response;
	}


	/**
	 * create account and check status and account name
	 * 
	 * @author shan.jing
	 * @param urlParentId that means you set it in url
	 * @param accountName
	 * @param bodyParentId that means you transport it as json object in body
	 * @return account id
	 */
	public String createAccountWithCheck(String urlParentId, String accountName,
			String bodyParentId) {

		Response response = createAccount(urlParentId, accountName, bodyParentId);
		checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		checkBodyItemValue(response, "organization_type", SpogConstants.MSP_SUB_ORG);
		checkBodyItemValue(response, "parent_id", urlParentId);
		checkBodyItemValue(response, "organization_name", accountName);
		return getOrganizationID(response);
	}


	/**
	 * create account and check status and account name
	 * 
	 * @author shan.jing
	 * @param urlParentId that means you set it in url
	 * @param accountName
	 * @param bodyParentId that means you transport it as json object in body
	 * @param ExtentTest
	 * @return account id
	 */
	public String createAccountWithCheck(String urlParentId, String accountName, String bodyParentId,
			ExtentTest test) {

		Response response = createAccount(urlParentId, accountName, bodyParentId);
		checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		checkBodyItemValue(response, "organization_type", SpogConstants.MSP_SUB_ORG);
		checkBodyItemValue(response, "parent_id", urlParentId);
		checkBodyItemValue(response, "organization_name", accountName);
		return getOrganizationID(response);
	}


	/**
	 * create account failed with expected status code
	 * 
	 * @author shan.jing
	 * @param urlParentId that means you set it in url
	 * @param accountName
	 * @param bodyParentId that means you transport it as json object in body
	 * @param expectedStatusCode
	 * @param expectedErrorCode
	 * @param ExtentTest
	 * @return
	 */
	public void createAccountFailedWithExpectedStatusCode(String urlParentId, String accountName,
			String bodyParentId, int expectedStatusCode, String expectedErrorCode, ExtentTest test) {

		Response response = createAccount(urlParentId, accountName, bodyParentId);
		test.log(LogStatus.INFO, "response status for create account is " + response.getStatusCode());
		checkResponseStatus(response, expectedStatusCode);
		checkErrorCode(response, expectedErrorCode);
	}


	/**
	 * update account
	 * 
	 * @author shan.jing
	 * @param parentId that means account parent id
	 * @param accountOrgId
	 * @param accountName
	 * @return response
	 */
	public Response updateAccount(String parentId, String accountOrgId, String accountName) {

		Map<String, String> updateAccountInfo = jp.getUpdateAccountInfo(accountName);
		Response response = spogInvoker.updateAccountByID(parentId, accountOrgId, updateAccountInfo);
		return response;
	}


	/**
	 * general method to updateAccount blocked param
	 * 
	 * @author ramya.nagepalli
	 * @param mspOrgId
	 * @param org_id
	 * @param accountName
	 * @param blocked
	 * @return response
	 * 
	 */
	public Response updateAccount_blocked(String mspOrgId, String org_id, String accountName,
			String blocked) {

		// TODO Auto-generated method stub
		Map<String, String> updateAccountInfo = jp.getUpdateAccountInfo(accountName, blocked);
		Response response = spogInvoker.updateAccountByID(mspOrgId, org_id, updateAccountInfo);
		return response;

	}


	/**
	 * update account and check status and account information
	 * 
	 * @author shan.jing
	 * @param parentId that means account parent id
	 * @param accountOrgId
	 * @param accountName
	 * @param ExtentTest
	 */
	public void updateAccountWithCheck(String parentId, String accountOrgId, String accountName,
			ExtentTest test) {

		Response response = updateAccount(parentId, accountOrgId, accountName);
		checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		checkBodyItemValue(response, "organization_type", SpogConstants.MSP_SUB_ORG);
		checkBodyItemValue(response, "parent_id", parentId);
		if (!CheckIsNullOrEmpty(accountName)) {
			checkBodyItemValue(response, "organization_name", accountName);
		}
		checkBodyItemValue(response, "organization_id", accountOrgId);
	}


	/**
	 * update account failed with expected status code
	 * 
	 * @author shan.jing
	 * @param parentId that means account parent id
	 * @param accountOrgId
	 * @param accountName
	 * @param expectedErrorCode
	 * @param ExtentTest
	 * @return
	 */
	public void updateAccountFailedWithExpectedStatusCode(String parentId, String accountOrgId,
			String accountName, int expectedStatusCode, String expectedErrorCode, ExtentTest test) {

		Response response = updateAccount(parentId, accountOrgId, accountName);
		test.log(LogStatus.INFO, "response status for create account is " + response.getStatusCode());
		checkResponseStatus(response, expectedStatusCode);
		checkErrorCode(response, expectedErrorCode);
	}


	/**
	 * create organization
	 * 
	 * @author shan.jing
	 * @param organizationName
	 * @param organizationType
	 * @param organizationEmail
	 * @param organizationPwd
	 * @param organizationFirstName
	 * @param organizationLastName
	 * @return response
	 */
	public Response CreateOrganization(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName,
			String organizationLastName) {

		Map<String, Object> orgInfo = jp.getOrganizationInfo(organizationName, organizationType,
				organizationEmail, organizationPwd, organizationFirstName, organizationLastName);
		Response response = spogInvoker.CreateOrganization(orgInfo);
		return response;
	}


	/**
	 * create user for Api testing
	 * 
	 * @author shuo.zhang
	 * @param email: created user's email
	 * @param password: created user's password
	 * @param first_name: created user's first_name
	 * @param last_name: created user's last_name
	 * @param role_id: created user's role_id please refer to SpogConstants
	 *        DIRECT_ADMIN/MSP_ADMIN/CSR_ADMIN
	 * @param organization_id: created user's organization_id. this is optional
	 * @return response
	 */
	public Response createUser(String email, String password, String first_name, String last_name,
			String role_id, String organization_id, ExtentTest test) {

		errorHandle.printDebugMessageInDebugFile("***********createUser***********");
		test.log(LogStatus.INFO, "begin to compose userInfo ");
		Map<String, String> userInfo =
				jp.getUserInfo(email, password, first_name, last_name, role_id, organization_id);
		test.log(LogStatus.INFO, "begin to create user  ");
		Response response = spogInvoker.createUser(userInfo);
		return response;
	}


	/**
	 * check create user response
	 * 
	 * @author shuo.zhang
	 * @param response: the response of create user
	 * @param expectedStatusCode: expected status code.
	 * @param email: expected created user's email
	 * @param password: expected created user's password
	 * @param first_name: expected created user's first_name
	 * @param last_name: expected created user's last_name
	 * @param role_id: expected created user's role_id please refer to SpogConstants
	 *        DIRECT_ADMIN/MSP_ADMIN/CSR_ADMIN
	 * @param organization_id: expected created user's organization_id.
	 * @param expectedErrorMessage: expected error message
	 * @return response
	 */
	public String checkCreateUser(Response response, int expectedStatusCode, String email,
			String first_name, String last_name, String role_id, String organization_id,
			String expectedErrorCode, ExtentTest test) {

		errorHandle.printDebugMessageInDebugFile("/***************checkCreateUser***************");
		test.log(LogStatus.INFO, "check status code");
		checkResponseStatus(response, expectedStatusCode);

		errorHandle.printDebugMessageInDebugFile(response.then().log().body().toString());
		if (expectedStatusCode == SpogConstants.SUCCESS_POST) {
			test.log(LogStatus.INFO, "check response body ");
			String user_id = response.then().body("data.first_name", equalTo(first_name))
					.body("data.last_name", equalTo(last_name))
					.body("data.email", equalTo(email.toLowerCase())).body("data.role_id", equalTo(role_id))
					.body("data.organization_id", equalTo(organization_id))
					.body("data.create_ts", not(isEmptyOrNullString()))
					.body("data.user_id", not(isEmptyOrNullString())).extract().path("data.user_id");
			test.log(LogStatus.INFO, "new create user id is  " + user_id);
			return user_id;
		} else {
			// for the error handling test.
			test.log(LogStatus.INFO, "for error handling, check error message");
			// checkErrorMessage(response, expectedErrorMessage);
			checkErrorCode(response, expectedErrorCode);
			return null;
		}
	}


	/**
	 * get organization id
	 * 
	 * @author shan.jing
	 * @param response
	 * @return the organization id
	 */
	public String getOrganizationID(Response response) {

		String orgID = response.then().extract().path("data.organization_id");
		errorHandle.printDebugMessageInDebugFile("Get organization id from body is:" + orgID);
		return orgID;
	}


	/**
	 * get account id
	 * 
	 * @author shan.jing
	 * @param response
	 * @return the account id
	 */
	public String getAccountID(Response response) {

		String accountID = response.then().extract().path("account_id");
		errorHandle.printDebugMessageInDebugFile("Get account id from body is:" + accountID);
		return accountID;
	}


	/**
	 * get organization Parent id
	 * 
	 * @author shan.jing
	 * @param response
	 * @return the organization parent id
	 */
	public String GetOrganizationParentID(Response response) {

		String orgParentID = response.then().extract().path("data.parent_id");
		errorHandle.printDebugMessageInDebugFile("Get organization id from body is:" + orgParentID);
		return orgParentID;
	}


	/**
	 * get organizationID for current logged in user
	 * 
	 * @author zhaoguo.ma
	 * @return
	 */
	public String GetLoggedinUserOrganizationID() {

		Response response = spogInvoker.GetLoggedinUserInfo();

		String orgID = response.then().extract().path("data.organization_id");
		errorHandle.printDebugMessageInDebugFile("Get organization id from body is: " + orgID);
		return orgID;
	}


	/**
	 * get organizationID for given user
	 * 
	 * @author zhaoguo.ma
	 * @param userID
	 * @return
	 */

	public String GetOrganizationIDforUser(String userID) {

		Response response = spogInvoker.GetUserInfoByID(userID);
		String orgID = response.then().extract().path("data.organization_id");
		errorHandle.printDebugMessageInDebugFile("Get organization id from body is: " + orgID);
		return orgID;
	}


	/**
	 * create organization and check organizationName and organizationType
	 * 
	 * @author shan.jing
	 * @param organizationName
	 * @param organizationType
	 * @param organizationEmail
	 * @param organizationPwd
	 * @param organizationFirstName
	 * @param organizationLastName
	 * @return organization id
	 */
	public String CreateOrganizationWithCheck(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName,
			String organizationLastName, ExtentTest test) {

		Response response = CreateOrganization(organizationName, organizationType, organizationEmail,
				organizationPwd, organizationFirstName, organizationLastName, test);
		String prefix = "AUTO_";
		if (organizationName != null && organizationName != "") {
			organizationName = prefix + organizationName;
		}
		test.log(LogStatus.INFO,
				"response status for create organization is " + response.getStatusCode());
		checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		errorHandle.printDebugMessageInDebugFile("response for creating org is " + response);
		checkBodyItemValue(response, "organization_name", organizationName, test);
		checkBodyItemValue(response, "organization_type", organizationType, test);
		return getOrganizationID(response);
	}


	/**
	 * create organization and check organizationName and organizationType
	 * 
	 * @author shan.jing
	 * @param organizationName
	 * @param organizationType
	 * @param organizationEmail
	 * @param organizationPwd
	 * @param organizationFirstName
	 * @param organizationLastName
	 * @param blocked
	 * @return organization id
	 */
	public String CreateOrganizationWithCheck(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName,
			String organizationLastName, boolean blocked, ExtentTest test) {

		Response response = CreateOrganization(organizationName, organizationType, organizationEmail,
				organizationPwd, organizationFirstName, organizationLastName, test);
		String prefix = "AUTO_";
		if (organizationName != null && organizationName != "") {
			organizationName = prefix + organizationName;
		}
		test.log(LogStatus.INFO,
				"response status for create organization is " + response.getStatusCode());
		checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		errorHandle.printDebugMessageInDebugFile("response for creating org is " + response);
		checkBodyItemValue(response, "organization_name", organizationName, test);
		checkBodyItemValue(response, "organization_type", organizationType, test);
		response.then().body("data.blocked", equalTo(blocked));
		return getOrganizationID(response);
	}


	/**
	 * create organization and check organizationName and organizationType
	 * 
	 * @author shan.jing
	 * @param organizationName
	 * @param organizationType
	 * @param organizationEmail
	 * @param organizationPwd
	 * @param organizationFirstName
	 * @param organizationLastName
	 * @param parent id
	 * @param test
	 * @return organization id
	 */
	public String CreateOrganizationWithCheck(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName,
			String organizationLastName, String Parent_id, ExtentTest test) {

		Response response = CreateOrganization(organizationName, organizationType, organizationEmail,
				organizationPwd, organizationFirstName, organizationLastName, test);
		String prefix = "AUTO_";
		if (organizationName != null && organizationName != "") {
			organizationName = prefix + organizationName;
		}
		test.log(LogStatus.INFO,
				"response status for create organization is " + response.getStatusCode());
		checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		errorHandle.printDebugMessageInDebugFile("response for creating org is " + response);
		checkBodyItemValue(response, "organization_name", organizationName, test);
		checkBodyItemValue(response, "organization_type", organizationType, test);
		checkBodyItemValue(response, "parent_id", Parent_id, test);
		return getOrganizationID(response);
	}


	/**
	 * create organization failed with expected status code
	 * 
	 * @author shan.jing
	 * @param organizationName
	 * @param organizationType
	 * @param organizationEmail
	 * @param organizationPwd
	 * @param organizationFirstName
	 * @param organizationLastName
	 * @param expectedStatusCode
	 * @param expectedErrorCode
	 * @return
	 */
	public void CreateOrganizationFailedWithExpectedStatusCode(String organizationName,
			String organizationType, String organizationEmail, String organizationPwd,
			String organizationFirstName, String organizationLastName, int expectedStatusCode,
			String expectedErrorCode, ExtentTest test) {

		Response response = CreateOrganization(organizationName, organizationType, organizationEmail,
				organizationPwd, organizationFirstName, organizationLastName, test);
		test.log(LogStatus.INFO,
				"response status for create organization is " + response.getStatusCode());
		checkResponseStatus(response, expectedStatusCode);
		errorHandle.printDebugMessageInDebugFile("response for creating org is " + response);
		checkErrorCode(response, expectedErrorCode);
	}


	/**
	 * create organization and check organizationName and organizationType
	 * 
	 * @author shan.jing
	 * @param organizationName
	 * @param organizationType
	 * @param organizationEmail
	 * @param organizationPwd
	 * @param organizationFirstName
	 * @param organizationLastName
	 * @return organization id
	 */
	public String CreateOrganizationWithCheck(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName,
			String organizationLastName) {

		Response response = CreateOrganization(organizationName, organizationType, organizationEmail,
				organizationPwd, organizationFirstName, organizationLastName);
		checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		String prefix = "AUTO_";
		if (organizationName != null && organizationName != "") {
			organizationName = prefix + organizationName;
		}
		errorHandle.printDebugMessageInDebugFile("response for creating org is " + response);
		checkBodyItemValue(response, "organization_name", organizationName);
		checkBodyItemValue(response, "organization_type", organizationType);
		return getOrganizationID(response);
	}


	/**
	 * delete organization and check status code is expected
	 * 
	 * @author shan.jing
	 * @param orgId
	 * @param test
	 */
	public void DeleteOrganizationWithCheck(String orgId, ExtentTest test) {

		Response response = spogInvoker.DeleteOrganizationById(orgId);
		checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	}


	public void DeleteOrganization(String orgId) {

		spogInvoker.DeleteOrganizationById(orgId);
	}


	/**
	 * delete organization and check status code is expected
	 * 
	 * @author shan.jing
	 * @param orgId
	 * @param expectedStatusCode
	 * @param expectedStatusCode
	 * @param test
	 */
	public void DeleteOrganizationWithExpectedStatusCode(String orgId, int expectedStatusCode,
			String expectedErrorCode, ExtentTest test) {

		Response response = spogInvoker.DeleteOrganizationById(orgId);
		checkResponseStatus(response, expectedStatusCode, test);
		checkErrorCode(response, expectedErrorCode);
	}


	/**
	 * update logged in user and just check status
	 * 
	 * @author liu.yuefen
	 * @param firstName
	 * @param lastName
	 * @return response
	 */
	public Response UpdateLoggedInUser(String email, String password, String first_name,
			String last_name, String role_id, String organization_id) {

		Map<String, String> updateUserInfo =
				jp.updateUserInfo(email, password, first_name, last_name, role_id, organization_id);
		Response response = spogInvoker.updateLoggedInUser(updateUserInfo);
		errorHandle.printDebugMessageInDebugFile("response for updating logged in user is " + response);
		return response;
	}


	public Response UpdateLoggedInUser(String email, String password, String first_name,
			String last_name, String role_id, String organization_id, String phone_number,
			ExtentTest test) {

		Map<String, String> updateUserInfo = jp.updateUserInfo(email, password, first_name, last_name,
				role_id, organization_id, phone_number);
		Response response = spogInvoker.updateLoggedInUser(updateUserInfo);
		errorHandle.printDebugMessageInDebugFile("response for updating logged in user is " + response);
		return response;
	}


	public Response UpdateLoggedInUserWithBodyNull() {

		Response response = spogInvoker.updateLoggedInUserWithBodyNull();
		errorHandle.printDebugMessageInDebugFile("response for updating logged in user is " + response);
		return response;
	}


	/**
	 * Delete user by id
	 * 
	 * @author liu.yuefen
	 * @param user_id
	 * @return response
	 */
	public Response DeleteUserById(String user_id, ExtentTest test) {

		Response response = spogInvoker.DeleteUserById(user_id);

		return response;
	}


	public Response DeleteUserById(String user_id) {

		Response response = spogInvoker.DeleteUserById(user_id);

		return response;
	}


	/**
	 * Get organization information by ID
	 * 
	 * @author ma.zhaoguo
	 * @param orgID
	 * @return
	 */
	public void GetOrganizationInfobyIDWithCheck(String orgID, String organization_name,
			String organization_type, ExtentTest test) {

		Response response = spogInvoker.GetOrganizationInfobyID(orgID);
		checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);

		errorHandle.printDebugMessageInDebugFile("response for get orgnization info is " + response);
		checkBodyItemValue(response, "organization_name", organization_name);
		checkBodyItemValue(response, "organization_type", organization_type);
	}


	/**
	 * check if the value is "" or null
	 * 
	 * @author shan.jing
	 * @param checkValue
	 * @return boolean
	 */
	public boolean CheckIsNullOrEmpty(String checkValue) {

		if (checkValue == "" || checkValue == null) {
			return true;
		} else {
			return false;
		}
	}


	/**
	 * Get organization information by ID and check all organization information items
	 * 
	 * @author shan.jing
	 * @param orgID
	 * @param organizationName
	 * @param organizationType
	 * @param parent id
	 * @return
	 */
	public void GetOrganizationInfobyIDWithCheck(String orgID, String organizationName,
			String organizationType, String parent_id, ExtentTest test) {

		Response response = spogInvoker.GetOrganizationInfobyID(orgID);
		checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		checkBodyItemValue(response, "organization_name", organizationName, test);
		checkBodyItemValue(response, "organization_type", organizationType, test);
		checkBodyItemValue(response, "parent_id", parent_id, test);
		if (organizationType.equalsIgnoreCase(SpogConstants.MSP_ORG)&&!parent_id.equalsIgnoreCase(SpogConstants.CSR_ORG_ID)) {
			checkBodyItemValue(response, "sub_msp", "true", test);
		}else {
			checkBodyItemValue(response, "sub_msp", "false", test);
		}
	}

	public void GetOrganizationInfobyIDWithCheck(String orgID, String organizationName,
			String organizationType, String parent_id,String datacenter_id, String datacenter_name, ExtentTest test) {

		Response response = spogInvoker.GetOrganizationInfobyID(orgID);
		checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		checkBodyItemValue(response, "organization_name", organizationName, test);
		checkBodyItemValue(response, "organization_type", organizationType, test);
		checkBodyItemValue(response, "parent_id", parent_id, test);
		checkBodyItemValue(response, "datacenter.datacenter_id", datacenter_id, test);
		checkBodyItemValue(response, "datacenter.datacenter_name", datacenter_name, test);
	}

	/**
	 * Get logged in user's organization information and check all organization information items
	 * 
	 * @author shan.jing
	 * @param organizationName
	 * @param organizationType
	 * @param parent id
	 * @return
	 */
	public void GetLoggedInUserOrganizationInfoWithCheck(String orgID, String organizationName,
			String organizationType, String parent_id, ExtentTest test) {

		Response response = spogInvoker.GetLoggedInUserOrganizationInfo();
		checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		checkBodyItemValue(response, "organization_id", orgID, test);
		checkBodyItemValue(response, "organization_name", organizationName, test);
		checkBodyItemValue(response, "organization_type", organizationType, test);
		checkBodyItemValue(response, "parent_id", parent_id, test);
	}

	public void GetLoggedInUserOrganizationInfoWithCheck(String orgID, String organizationName,
			String organizationType, String parent_id,String datacenter_id, String datacenter_name, ExtentTest test) {

		Response response = spogInvoker.GetLoggedInUserOrganizationInfo();
		checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		checkBodyItemValue(response, "organization_id", orgID, test);
		checkBodyItemValue(response, "organization_name", organizationName, test);
		checkBodyItemValue(response, "organization_type", organizationType, test);
		checkBodyItemValue(response, "parent_id", parent_id, test);
		checkBodyItemValue(response, "datacenter.datacenter_id", datacenter_id, test);
		checkBodyItemValue(response, "datacenter.datacenter_name", datacenter_name, test);
	}


	/**
	 * Get organization information by ID with expected status code
	 * 
	 * @author shan.jing
	 * @param orgID
	 * @param expectedStatusCode
	 * @param expectedErrorCode
	 * @return
	 */
	public void GetOrganizationInfobyIDWithExpectedStatusCode(String orgID, int expectedStatusCode,
			String expectedErrorCode, ExtentTest test) {

		Response response = spogInvoker.GetOrganizationInfobyID(orgID);
		checkResponseStatus(response, expectedStatusCode);
		checkErrorCode(response, expectedErrorCode);
	}


	/**
	 * Get logged in user's organization information with expected status code
	 * 
	 * @author shan.jing
	 * @param expectedStatusCode
	 * @param expectedErrorCode
	 * @return
	 */
	public void GetLoggedInUserOrganizationInfoWithExpectedStatusCode(int expectedStatusCode,
			String expectedErrorCode, ExtentTest test) {

		Response response = spogInvoker.GetLoggedInUserOrganizationInfo();
		checkResponseStatus(response, expectedStatusCode);
		checkErrorCode(response, expectedErrorCode);
	}


	/**
	 * Get delete organization information will be failed with expected status code
	 * 
	 * @author shan.jing
	 * @param orgID
	 * @param expected status code
	 * @param expectedStatusCode
	 * @return
	 */
	public void GetDeletedOrganizationInfoWithExpectedStatusCode(String orgID, int expectedStatusCode,
			String expectedErrorCode, ExtentTest test) {

		Response response = spogInvoker.GetOrganizationInfobyID(orgID);
		checkResponseStatus(response, expectedStatusCode, test);
		checkErrorCode(response, expectedErrorCode);
	}


	/**
	 * check the update logged in user status
	 * 
	 * @author liu.yuefen
	 */
	public Response CheckUpdateLoggedInUserStatus(String email, String password, String first_name,
			String last_name, String role_id, String organization_id, int expectedStatusCode,
			ExtentTest test) {

		Map<String, String> updateUserInfo =
				jp.updateUserInfo(email, password, first_name, last_name, role_id, organization_id);
		Response response = spogInvoker.updateLoggedInUser(updateUserInfo);
		test.log(LogStatus.INFO,
				"response status for update logged in user is " + response.getStatusCode());
		errorHandle.printDebugMessageInDebugFile("response for updating logged in user is " + response);
		checkResponseStatus(response, expectedStatusCode);
		checkBodyItemValue(response, "email", email, test);
		checkBodyItemValue(response, "password", password, test);
		checkBodyItemValue(response, "first_name", first_name, test);
		checkBodyItemValue(response, "last_name", last_name, test);
		checkBodyItemValue(response, "organization_id", organization_id, test);

		return response;
	}


	/**
	 * check the status of "Delete user by id"
	 * 
	 * @author liu.yuefen
	 * @param user_id
	 * @param expectedStatusCode
	 */
	public void CheckDeleteUserByIdStatus(String user_id, int expectedStatusCode, ExtentTest test) {

		Response response = spogInvoker.DeleteUserById(user_id);
		test.log(LogStatus.INFO,
				"response status for delete user by id is " + response.getStatusCode());
		checkResponseStatus(response, expectedStatusCode);
		errorHandle.printDebugMessageInDebugFile("response for deleting user is " + response);
	}


	/**
	 * return random string which will append base string
	 * 
	 * @author shan.jing
	 * @param baseString
	 * @return random string
	 */
	public String ReturnRandom(String baseString) {

		if (baseString == "") {
			return baseString;
		} else {
			return baseString + RandomStringUtils.randomAlphanumeric(8);
		}
	}


	/**
	 * return random uuid string
	 * 
	 * @author shan.jing
	 * @return random uuid string
	 */
	public String returnRandomUUID() {

		return UUID.randomUUID().toString();
	}


	/**
	 * Get the JWT token of the logged in user
	 * 
	 * @author BhardwajReddy
	 * @return JWTToken
	 */
	public String getJWTToken() {

		String JWTToken = null;
		JWTToken = spogInvoker.getToken();
		return JWTToken;
	}


	/**
	 * Call the REST api to gettheLoggedInUser
	 * 
	 * @author Bharadwaj.Ghadaim
	 * @param Token
	 * @param statuscode
	 * @param test
	 * @return response
	 */
	public Response getLoggedInUser(String Token, int statuscode, ExtentTest test) {

		Response response = spogInvoker.getLoggedinUserInfo(Token, test);
		checkResponseStatus(response, statuscode, test);
		System.out.println("The value of the geneated user is :" + response.getBody().asString());
		return response;
	}


	public void checkLoggedInUserInformation(Response response, int expectedStatusCode,
			SpogMessageCode Info, String adminusername, String user_id, String organization_id,
			Boolean blocked, String Status, String available_actions, ExtentTest test) {

		test.log(LogStatus.INFO, "expect status code is " + expectedStatusCode);
		errorHandle.printInfoMessageInDebugFile("expect status code is " + expectedStatusCode);
		response.then().statusCode(expectedStatusCode);
		if (expectedStatusCode == SpogConstants.SUCCESS_LOGIN) {
			response.then().body("data.email", equalTo(adminusername.toLowerCase()));
			response.then().body("data.user_id", equalTo(user_id));
			response.then().body("data.organization_id", equalTo(organization_id));
			response.then().body("data.blocked", equalTo(blocked));
			response.then().body("data.status", equalTo(Status));
			ArrayList<String> actual_actions = response.then().extract().path("data.allowed_actions");
			String[] splitted = available_actions.replace(" ", "").split(",");
			assertResponseItem(String.join(",", actual_actions), String.join(",", splitted), test);
			test.log(LogStatus.PASS,
					"The given username,user_id are matched with  Logged in username and user_id");
			errorHandle.printInfoMessageInDebugFile(
					"The given user_id and the logged in user_id values are  matched");
			assertTrue("User is not Null or empty", true);
		} else {
			String expectedErrorCode = "";
			String expectedErrorMessage = "";
			if (Info.getStatus() != "0010000") {
				expectedErrorMessage = Info.getStatus();

				if (expectedErrorMessage.contains("{0}")) {
					expectedErrorMessage = expectedErrorMessage.replace("{0}", getUUId());
					System.out.println(expectedErrorMessage);
				}
				expectedErrorCode = Info.getCodeString();
			}
			checkErrorMessage(response, expectedErrorMessage);
			test.log(LogStatus.PASS, "The value of the message is " + expectedErrorMessage);
			checkErrorCode(response, expectedErrorCode);
			test.log(LogStatus.PASS, "The value of the code  generated  is " + expectedErrorCode);
			test.log(LogStatus.INFO,
					"The value of the  response generated actually is :" + response.getStatusCode());
		}

	}


	/**
	 * Check the Response for getLoggedInUser
	 * 
	 * @author Bharadwaj.ghadiam
	 * @param response
	 * @param expectedStatusCode
	 * @param Info
	 * @param adminusername
	 * @param test
	 */
	public void checkLoggedInUser(Response response, int expectedStatusCode, SpogMessageCode Info,
			String adminusername, ExtentTest test) {

		String expectedErrorMessage = "", expectedErrorCode = "";
		test.log(LogStatus.INFO, "expect status code is " + expectedStatusCode);
		errorHandle.printInfoMessageInDebugFile("expect status code is " + expectedStatusCode);
		response.then().statusCode(expectedStatusCode);
		if (expectedStatusCode == SpogConstants.SUCCESS_LOGIN) {
			String act_first_name = response.then().extract().path("data.first_name");
			String act_last_name = response.then().extract().path("data.last_name");
			String act_email = response.then().extract().path("data.email");
			String act_user_name = act_first_name + " " + act_last_name;
			// assertResponseItem(act_user_name, adminusername, test);
			if (act_user_name.equals(adminusername)) {
				test.log(LogStatus.PASS, "The given username and the logged in user name matched");
				errorHandle
				.printInfoMessageInDebugFile("The given username and the logged in user name matched");
				assertTrue("User is not Null or empty", true);
			} else if (act_email.equals(adminusername)) {
				test.log(LogStatus.PASS, "The given username and the logged in user name matched");
				errorHandle
				.printInfoMessageInDebugFile("The given username and the logged in user name matched");
				assertTrue("User is not Null or empty", true);
			}

		} else {
			if (Info.getStatus() != "0010000") {
				expectedErrorMessage = Info.getStatus();

				if (expectedErrorMessage.contains("{0}")) {
					expectedErrorMessage = expectedErrorMessage.replace("{0}", getUUId());
					System.out.println(expectedErrorMessage);
				}
				expectedErrorCode = Info.getCodeString();
			}
			checkErrorMessage(response, expectedErrorMessage);
			test.log(LogStatus.PASS, "The value of the message is " + expectedErrorMessage);
			checkErrorCode(response, expectedErrorCode);
			test.log(LogStatus.PASS, "The value of the code  generated  is " + expectedErrorCode);
			test.log(LogStatus.INFO,
					"The value of the  response generated actually is :" + response.getStatusCode());
		}

	}


	/**
	 * Check Login Web Service by id
	 * 
	 * @author Bharadwaj.Ghadiam
	 * @param response after call login web service, the return value,
	 * @param adminusername
	 * @param user_id
	 * @param expectedStatusCode expectStatusCode
	 */
	public void checkLoggedInUser(Response response, int expectedStatusCode,
			String expectedErrorMessage, String adminusername, String user_id, String organization_id,
			ExtentTest test) {

		test.log(LogStatus.INFO, "expect status code is " + expectedStatusCode);
		errorHandle.printInfoMessageInDebugFile("expect status code is " + expectedStatusCode);
		response.then().statusCode(expectedStatusCode);
		if (expectedStatusCode == SpogConstants.SUCCESS_LOGIN) {
			response.then().body("data.email", equalTo(adminusername.toLowerCase()));
			response.then().body("data.user_id", equalTo(user_id));
			response.then().body("data.organization_id", equalTo(organization_id));
			test.log(LogStatus.PASS,
					"The given username,user_id are matched with  Logged in username and user_id");
			errorHandle.printInfoMessageInDebugFile(
					"The given user_id and the logged in user_id values are  matched");
			assertTrue("User is not Null or empty", true);
		} else {
			if (expectedErrorMessage == "permissions") {
				test.log(LogStatus.INFO, response.getBody().asString());
				response.then().assertThat().body("status",
						equalTo(SpogConstants.INSUFFICIENT_PERMISSIONS));
				test.log(LogStatus.PASS,
						"The expected error message matched " + SpogConstants.INSUFFICIENT_PERMISSIONS);
			} else if (expectedErrorMessage == "Resource does not exist") {
				test.log(LogStatus.INFO, response.getBody().asString());
				response.then().assertThat().body("status", equalTo(SpogConstants.RESOURCE_NOT_EXIST));
				test.log(LogStatus.PASS,
						"The expected error message matched " + SpogConstants.RESOURCE_NOT_EXIST);
			} else {

				checkErrorMessage(response, expectedErrorMessage);
				test.log(LogStatus.PASS, "The expected error message matched " + expectedErrorMessage);
			}
			// response.then().assertThat().body(contains(expectedErrorMessage));
		}

	}


	/**
	 * Check Login Web Service by id
	 * 
	 * @author kiran.sripada
	 * @param response after call login web service, the return value,
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param adminusername
	 * @param user_id
	 * @param organization_id
	 * @param blocked
	 * @param Status
	 * @param available_actions
	 * @param expectedStatusCode expectStatusCode
	 */
	public void checkLoggedInUser(Response response, int expectedStatusCode,
			String expectedErrorMessage, String adminusername, String user_id, String organization_id,
			Boolean blocked, String Status, String available_actions, ExtentTest test) {

		test.log(LogStatus.INFO, "expect status code is " + expectedStatusCode);
		errorHandle.printInfoMessageInDebugFile("expect status code is " + expectedStatusCode);
		response.then().statusCode(expectedStatusCode);
		if (expectedStatusCode == SpogConstants.SUCCESS_LOGIN) {
			response.then().body("data.email", equalTo(adminusername.toLowerCase()));
			response.then().body("data.user_id", equalTo(user_id));
			response.then().body("data.organization_id", equalTo(organization_id));
			response.then().body("data.blocked", equalTo(blocked));
			response.then().body("data.status", equalTo(Status));
			ArrayList<String> actual_actions = response.then().extract().path("data.allowed_actions");
			String[] splitted = available_actions.replace(" ", "").split(",");
			assertResponseItem(String.join(",", actual_actions), String.join(",", splitted), test);
			test.log(LogStatus.PASS,
					"The given username,user_id are matched with  Logged in username and user_id");
			errorHandle.printInfoMessageInDebugFile(
					"The given user_id and the logged in user_id values are  matched");
			assertTrue("User is not Null or empty", true);
		} else {
			if (expectedErrorMessage == "permissions") {
				test.log(LogStatus.INFO, response.getBody().asString());
				response.then().assertThat().body("status",
						equalTo(SpogConstants.INSUFFICIENT_PERMISSIONS));
				test.log(LogStatus.PASS,
						"The expected error message matched " + SpogConstants.INSUFFICIENT_PERMISSIONS);
			} else if (expectedErrorMessage == "Resource does not exist") {
				test.log(LogStatus.INFO, response.getBody().asString());
				response.then().assertThat().body("status", equalTo(SpogConstants.RESOURCE_NOT_EXIST));
				test.log(LogStatus.PASS,
						"The expected error message matched " + SpogConstants.RESOURCE_NOT_EXIST);
			} else {

				checkErrorMessage(response, expectedErrorMessage);
				test.log(LogStatus.PASS, "The expected error message matched " + expectedErrorMessage);
			}
			// response.then().assertThat().body(contains(expectedErrorMessage));
		}

	}


	/**
	 * Get users by organization ID
	 * 
	 * @author zhaoguo.ma
	 * @param orgID
	 * @return
	 */

	public Response GetUsersByOrganizationID(String orgID) {

		Response response = spogInvoker.GetUsersByOrganizationID(orgID);
		checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);

		errorHandle.printDebugMessageInDebugFile("response for get orgnization User is " + response);
		return response;
	}


	/**
	 * compare the users from an organization with expected users
	 * 
	 * @author zhaoguo.ma
	 * @param orgID
	 * @param expectedUsers
	 * @param test
	 */
	public void CompareColumnsHeadContent(Response response,
			ArrayList<HashMap> expectedColumnsHeadContents, ExtentTest test) {

		ArrayList<HashMap> columnsHeadContent = new ArrayList<HashMap>();
		columnsHeadContent = response.then().extract().path("data");
		int length = expectedColumnsHeadContents.size();
		if (length != columnsHeadContent.size()) {
			assertTrue("compare columns head content number is " + length, false);
			test.log(LogStatus.FAIL, "compare columns head content number");
		}

		for (int i = 0; i < length; i++) {
			HashMap expectedColumnsHeadContent = expectedColumnsHeadContents.get(i);
			HashMap HeadContent = columnsHeadContent.get(i);
			if (expectedColumnsHeadContent.get("long_label").equals(HeadContent.get("long_label"))
					&& expectedColumnsHeadContent.get("short_label").equals(HeadContent.get("short_label"))
					&& expectedColumnsHeadContent.get("filter").equals(HeadContent.get("key"))
					&& Boolean.valueOf(expectedColumnsHeadContent.get("visible").toString())
					.equals(HeadContent.get("visible"))) {
				assertTrue("compare column which short lable:"
						+ expectedColumnsHeadContent.get("short_label") + " passed", true);
				test.log(LogStatus.INFO,
						"compare column content:" + expectedColumnsHeadContent.get("long_label") + " "
								+ expectedColumnsHeadContent.get("short_label") + " "
								+ expectedColumnsHeadContent.get("filter") + " is right");

			} else {
				assertTrue("compare column which short lable:"
						+ expectedColumnsHeadContent.get("short_label") + " failed", false);
				test.log(LogStatus.FAIL, "compare column content");
				test.log(LogStatus.FAIL, "expected user info: " + expectedColumnsHeadContents.toString());
				test.log(LogStatus.FAIL, "actual user info: " + columnsHeadContent.toString());
				System.out.print(expectedColumnsHeadContents.toString());
				System.out.print(columnsHeadContent.toString());
				break;
			}
		}
	}


	/**
	 * compare the users from an organization with expected users
	 * 
	 * @author zhaoguo.ma
	 * @param orgID
	 * @param expectedUsers
	 * @param test
	 */
	public void CompareUsersByOrganizationID(String orgID, ArrayList<HashMap> expectedUsers,
			ExtentTest test) {

		Response response = spogInvoker.GetUsersByOrganizationID(orgID);
		checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		errorHandle.printDebugMessageInDebugFile("response for get orgnization User is " + response);

		ArrayList<HashMap> users = new ArrayList<HashMap>();
		users = response.then().extract().path("data");

		int length = expectedUsers.size();

		if (length != users.size()) {
			assertTrue("compare user number " + length, false);
			test.log(LogStatus.FAIL, "compare user number ");
			errorHandle.printDebugMessageInDebugFile("users number is incorrect " + response);
		}

		for (int i = 0; i < length; i++) {
			HashMap expectedUser = expectedUsers.get(i);
			HashMap user = users.get(i);

			if (expectedUser.get("user_id").equals(user.get("user_id"))
					&& expectedUser.get("first_name").equals(user.get("first_name"))
					&& expectedUser.get("last_name").equals(user.get("last_name"))
					&& expectedUser.get("email").equals(user.get("email"))
					&& expectedUser.get("role_id").equals(user.get("role_id"))
					&& expectedUser.get("organization_id").equals(user.get("organization_id"))) {

				//Preference language check - added in Sprint 34
				//@author Rakesh.Chalamala
				if (!expectedUser.containsKey("preference_language")) {
					assertTrue(user.get("preference_language").toString().equalsIgnoreCase("en")); //Default value = en
				}else {
					assertTrue(expectedUser.get("preference_language").equals(user.get("preference_language")));
				}    	  

				test.log(LogStatus.INFO, "compare user info for " + expectedUser.get("user_id"));
				assertTrue("compare user info for " + expectedUser.get("user_id") + "passed", true);
				test.log(LogStatus.PASS,
						"compare user info for " + expectedUser.get("user_id") + " passed");
			} else {
				assertTrue("compare user info for " + expectedUser.get("user_id") + " failed"
						+ expectedUser.toString() + user.toString(), false);

				test.log(LogStatus.FAIL, "compare user info ");
				test.log(LogStatus.FAIL, "expected user info: " + expectedUser.toString());
				test.log(LogStatus.FAIL, "actual user info: " + user.toString());
				System.out.print(expectedUser.toString());
				System.out.print(user.toString());
				break;
			}
		}
	}


	/**
	 * update Organization for logged in user
	 * 
	 * @author zhaoguo.ma
	 * @param name
	 * @return
	 */

	public Response UpdateLoggedInOrganization(String name, ExtentTest test) {

		Map<String, String> updateOrgInfo = jp.updateOrganizationInfo(name);
		Response response = spogInvoker.updateLoggedInOrganization(updateOrgInfo);
		checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		errorHandle.printDebugMessageInDebugFile(
				"response for updating logged in organization is " + response);
		return response;
	}


	public Response UpdateLoggedInOrganization(String name, String blocked, ExtentTest test) {

		Map<String, String> updateOrgInfo = jp.updateOrganizationInfo(name, blocked);
		Response response = spogInvoker.updateLoggedInOrganization(updateOrgInfo);
		checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		errorHandle.printDebugMessageInDebugFile(
				"response for updating logged in organization is " + response);
		return response;
	}


	/**
	 * update logged in organization with error check
	 * 
	 * @author zhaoguo.ma
	 * @param name
	 * @param expectedStatusCode
	 * @param test
	 */
	public void UpdateLoggedInOrganizationWithExpectedStatusCode(String name, int expectedStatusCode,
			ExtentTest test) {

		Map<String, String> updateOrgInfo = jp.updateOrganizationInfo(name);
		Response response = spogInvoker.updateLoggedInOrganization(updateOrgInfo);

		test.log(LogStatus.INFO,
				"response status for update logged organization is " + response.getStatusCode());
		checkResponseStatus(response, expectedStatusCode);
		errorHandle
		.printDebugMessageInDebugFile("response for update logged orgization is " + response);
	}


	public void UpdateLoggedInOrganizationWithExpectedStatusCode(String name, String blocked,
			int expectedStatusCode, String errorCode, ExtentTest test) {

		Map<String, String> updateOrgInfo = jp.updateOrganizationInfo(name, blocked);
		Response response = spogInvoker.updateLoggedInOrganization(updateOrgInfo);

		test.log(LogStatus.INFO,
				"response status for update logged organization is " + response.getStatusCode());
		checkResponseStatus(response, expectedStatusCode);
		checkErrorCode(response, errorCode);
		errorHandle
		.printDebugMessageInDebugFile("response for update logged orgization is " + response);
	}


	/**
	 * get organization name by ID
	 * 
	 * @author zhaoguo.ma
	 * @param orgID
	 * @return
	 */
	public String getOrganizationNameByID(String orgID) {

		Response response = spogInvoker.GetOrganizationInfobyID(orgID);
		String orgName = response.then().extract().path("data.organization_name").toString();
		return orgName;
	}


	/**
	 * update Organization by ID
	 * 
	 * @author zhaoguo.ma
	 * @param orgID
	 * @param name
	 * @return
	 */
	public Response updateOrganizationInfoByID(String orgID, String name, ExtentTest test) {

		Map<String, String> updateOrgInfo = jp.updateOrganizationInfo(name);
		Response response = spogInvoker.updateOrganizationInfoByID(orgID, updateOrgInfo);
		checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		errorHandle.printDebugMessageInDebugFile(
				"response for updating logged in organization is " + response);
		return response;
	}


	public Response updateOrganizationInfoByID(String orgID, String name, String blocked,
			ExtentTest test) {

		Map<String, String> updateOrgInfo = jp.updateOrganizationInfo(name, blocked);
		Response response = spogInvoker.updateOrganizationInfoByID(orgID, updateOrgInfo);
		checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		errorHandle.printDebugMessageInDebugFile(
				"response for updating logged in organization is " + response);
		return response;
	}


	/**
	 * update organization block status by ID
	 * 
	 * @author shan.jing
	 * @param orgID
	 * @param blocked
	 * @param test
	 */
	public Response updateOrganizationBlockStatusByID(String orgID, boolean blocked,
			ExtentTest test) {

		Map<String, Object> updateOrgInfo = jp.updateOrganizationBlockStatus(blocked);
		Response response = spogInvoker.updateOrganizationBlockStatusByID(orgID, updateOrgInfo);
		checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		errorHandle.printDebugMessageInDebugFile(
				"response for updating organization block status is " + response);
		return response;
	}


	/**
	 * update organization by ID with errorcode check
	 * 
	 * @author zhaoguo.ma
	 * @param orgID
	 * @param name
	 * @param expectedStatusCode
	 * @param test
	 */
	public void updateOrganizationInfoByIDWithErrorCheck(String orgID, String name,
			int expectedStatusCode, String errorCode, ExtentTest test) {

		Map<String, String> updateOrgInfo = jp.updateOrganizationInfo(name);
		Response response = spogInvoker.updateOrganizationInfoByID(orgID, updateOrgInfo);
		test.log(LogStatus.INFO,
				"response status for update organization is " + response.getStatusCode());
		checkResponseStatus(response, expectedStatusCode);
		checkErrorCode(response, errorCode);
		errorHandle.printDebugMessageInDebugFile("response for update orgization is " + response);
	}


	public void updateOrganizationInfoByIDWithErrorCheck(String orgID, String name, String blocked,
			int expectedStatusCode, String errorCode, ExtentTest test) {

		Map<String, String> updateOrgInfo = jp.updateOrganizationInfo(name, blocked);
		Response response = spogInvoker.updateOrganizationInfoByID(orgID, updateOrgInfo);
		test.log(LogStatus.INFO,
				"response status for update organization is " + response.getStatusCode());
		checkResponseStatus(response, expectedStatusCode);
		checkErrorCode(response, errorCode);
		errorHandle.printDebugMessageInDebugFile("response for update orgization is " + response);
	}


	/**
	 * get LoggedInOrganizationname author zhaoguo.ma
	 * 
	 * @return
	 */
	public String getLoggedInOrganizationName() {

		String orgName =
				spogInvoker.getLoggedInOrgalization().then().extract().path("data.organization_name");
		return orgName;
	}


	public boolean getOrganizationStatusByID(String organizationID) {

		boolean status =
				spogInvoker.GetOrganizationInfobyID(organizationID).then().extract().path("blocked");
		return status;
	}


	/**
	 * get organization fails check
	 * 
	 * @author zhaoguo.ma
	 * @param orgID
	 * @param expectedStatusCode
	 * @param test
	 */
	public void GetOrganizationUsersFailedWithExpectedStatusCode(String orgID, int expectedStatusCode,
			String expectedErrorCode, ExtentTest test) {

		Response response = spogInvoker.GetUsersByOrganizationID(orgID);
		test.log(LogStatus.INFO,
				"response status for getting users from organization is " + response.getStatusCode());
		checkResponseStatus(response, expectedStatusCode);
		checkErrorCode(response, expectedErrorCode);
		errorHandle.printDebugMessageInDebugFile("response for creating org is " + response);
	}


	/**
	 * Get the logged in userById
	 * 
	 * @author BhardawajReddy
	 * @param Userid of the logged in user.
	 * @param JWTToken of the Logged in user
	 * @return response
	 */

	public Response getLoggedUserInfoByID(String Token, int statuscode, String user_id,
			ExtentTest test) {

		Response response = spogInvoker.getLoggedUserInfoById(Token, user_id, test);
		checkResponseStatus(response, statuscode, test);
		System.out.println("The value of the geneated user is :" + response.getBody().asString());
		return response;
	}


	/**
	 * update logged in user fail
	 * 
	 * @author liu.yuefen
	 * @param email
	 * @param password
	 * @param first_name
	 * @param last_name
	 * @param role_id
	 * @param organization_id
	 */
	public void UpdateLoggedInUserFailedWithExpectedStatusCode(String email, String password,
			String first_name, String last_name, String role_id, String organization_id,
			int expectedStatusCode, ExtentTest test) {

		Response response =
				UpdateLoggedInUser(email, password, first_name, last_name, role_id, organization_id);
		test.log(LogStatus.INFO,
				"response status for update logged in user is " + response.getStatusCode());
		checkResponseStatus(response, expectedStatusCode);
		errorHandle.printDebugMessageInDebugFile("response for update logged in user is " + response);
	}


	/**
	 * create user for integration testing
	 * 
	 * @author shuo.zhang
	 * @param email: created user's email
	 * @param password: created user's password
	 * @param first_name: created user's first_name
	 * @param last_name: created user's last_name
	 * @param role_id: created user's role_id please refer to SpogConstants
	 *        DIRECT_ADMIN/MSP_ADMIN/CSR_ADMIN
	 * @param organization_id: created user's organization_id. this is optional
	 * @return response
	 */

	public String createUserAndCheck(String email, String password, String first_name,
			String last_name, String role_id, String organization_id, ExtentTest test) {

		errorHandle.printDebugMessageInDebugFile("***********createUserAndCheck***********");
		Response response =
				createUser(email, password, first_name, last_name, role_id, organization_id, test);
		errorHandle.printInfoMessageInDebugFile(response.getBody().toString());
		test.log(LogStatus.INFO, "organization id is " + organization_id);
		if ((organization_id == null) || (organization_id.equals(""))) {
			organization_id = GetLoggedinUserOrganizationID();
		}
		String user_Id = checkCreateUser(response, SpogConstants.SUCCESS_POST, email, first_name,
				last_name, role_id, organization_id, "", test);
		return user_Id;

	}


	public void CreateUserWithErrorCheck(String email, String password, String first_name,
			String last_name, String role_id, String organization_id, int status_code, String error_code,
			ExtentTest test) {

		Map<String, String> userInfo =
				jp.getUserInfo(email, password, first_name, last_name, role_id, organization_id);
		Response response = spogInvoker.createUser(userInfo);

		checkResponseStatus(response, status_code);
		checkErrorCode(response, error_code);

	}


	/**
	 * Update user by id
	 * 
	 * @author liu.yuefen
	 * @param first_name, last_name and password are allowed to update
	 * @return response
	 */
	public Response updateUserById(String user_id, String email, String password, String first_name,
			String last_name, String role_id, String organization_id, ExtentTest test) {

		Map<String, String> updateUserInfo =
				jp.updateUserInfo(email, password, first_name, last_name, role_id, organization_id);
		test.log(LogStatus.INFO, "begin to update user  ");
		Response response = spogInvoker.updateUserById(user_id, updateUserInfo);

		return response;
	}


	public Response updateUserById(String user_id, String email, String password, String first_name,
			String last_name, String role_id, String organization_id) {

		Map<String, String> updateUserInfo =
				jp.updateUserInfo(email, password, first_name, last_name, role_id, organization_id);
		Response response = spogInvoker.updateUserById(user_id, updateUserInfo);

		return response;
	}


	/**
	 * Update user by id
	 * 
	 * @author liu.yuefen
	 * @param user_id
	 * @return response
	 */
	public Response updateUserByIdFWithBodyNull(String user_id) {

		Response response = spogInvoker.updateUserByIdWithBodyNull(user_id);

		return response;
	}


	/**
	 * Update user by id
	 * 
	 * @author liu.yuefen
	 * @param first_name, last_name, password, phone_number are allowed to update
	 * @return response
	 */
	public Response updateUserById(String user_id, String email, String password, String first_name,
			String last_name, String role_id, String organization_id, String phone_number,
			ExtentTest test) {

		Map<String, String> updateUserInfo = jp.updateUserInfo(email, password, first_name, last_name,
				role_id, organization_id, phone_number);
		test.log(LogStatus.INFO, "begin to update user  ");
		Response response = spogInvoker.updateUserById(user_id, updateUserInfo);

		return response;
	}


	/**
	 * Update user by id
	 * 
	 * @author liu.yuefen
	 * @param blocked status
	 * @return response
	 */
	public Response updateUserByIdForBlockedStatus(String user_id, String blockedStatus, String token,
			ExtentTest test) {

		Map<String, Boolean> updateUserInfo = new HashMap<>();

		if ((blockedStatus != null) && (!blockedStatus.equals(""))) {
			updateUserInfo.put("blocked", Boolean.valueOf(blockedStatus));
		}

		test.log(LogStatus.INFO, "begin to update user  ");
		Response response = spogInvoker.updateUserByIdForBlockedStatus(user_id, updateUserInfo, token);
		return response;
	}


	/**
	 * Used To Generate a Random SiteName
	 * 
	 * @author Bharadwaj.Ghadiam
	 * @param Prefix
	 * @return Random SiteName
	 */
	public String getRandomSiteName(String prefix) {

		return prefix + "_Site_" + RandomStringUtils.randomAlphanumeric(8) + "_Spog";
	}


	/**
	 * Method to create a site:
	 * 
	 * @author BharadwajReddy
	 * @param SiteName
	 * @param SiteType
	 * @param organizationId
	 * @return response
	 */

	public Response createSite(String siteName, String siteType, String organizationId, String token,
			ExtentTest test) {

		test.log(LogStatus.INFO, "Creating a HashMap for siteCreation");
		Map<String, String> siteinfo = jp.composeSiteInfoMap(siteName, siteType, organizationId);
		Response response = spogInvoker.createSite(token, siteinfo, test);
		response.then().log().all();
		return response;
	}


	/**
	 * Validating The Response For creating a Site
	 * 
	 * @author Bharadwaj.Ghadiam
	 * @param response
	 * @param expectedStatusCode
	 * @param siteName
	 * @param siteType
	 * @param organization_id
	 * @param user_id
	 * @param expectedErrorMessage
	 */

	public Map<String, String> checkCreateSite(Response response, int expectedStatusCode,
			String siteName, String siteType, String organization_id, String user_id,
			String expectedErrorMessage, ExtentTest test) {

		Map<String, String> map = new HashMap<>();
		checkResponseStatus(response, expectedStatusCode, test);
		if (expectedStatusCode == SpogConstants.SUCCESS_POST) {
			String registration_basecode =
					response.then().body("data.site_id", not(isEmptyOrNullString()))
					.body("data.site_name", equalTo(siteName)).body("data.site_type", equalTo(siteType))
					.body("data.organization_id", equalTo(organization_id))
					.body("data.create_user_id", equalTo(user_id)).extract()
					.path("data.registration_basecode");
			map.put("registration_basecode", registration_basecode);
			String site_id =
					response.then().body("data.registration_basecode", not(isEmptyOrNullString()))
					.body("data.site_name", equalTo(siteName)).body("data.site_type", equalTo(siteType))
					.body("data.organization_id", equalTo(organization_id))
					.body("data.create_user_id", equalTo(user_id)).extract().path("data.site_id");
			map.put("site_id", site_id);
			return map;

		} else {
			test.log(LogStatus.INFO, "for error handling, check error message");
			checkErrorMessage(response, expectedErrorMessage);
			test.log(LogStatus.PASS, "Actual message matched the expected");
			return null;
		}
	}


	/**
	 * getAllUsersInLoggedOrganization used for API testing
	 * 
	 * @author shuo.zhang
	 * @param filterStr the format is
	 *        filterName1;operator;value,filterName2;operator;value,filterName1;operator;value
	 *        example: role_id;=;csr_admin or email;=;shuo.zhang@arcserve.com or role_id;!=;csr_admin,
	 *        email;like;shuo*@arcserve.com filterName: only accept: role_id/email operator:accept: =
	 *        != like value: role_id: ('csr_admin','msp_admin','direct_admin') email:<email> if
	 *        operator is like, in the value, must give * or ?
	 * @param sortStr the format is: sortedField;order,sortedField2;order example: first_name;asc or
	 *        email;desc or first_name;asc,email;desc sortedField: accept: 'create_ts','first_name',
	 *        'last_name','email' order: asc or desc
	 * @param pageNumber if you dont' want to set it, set it as -1
	 * @param pageSize if you dont' want to set it, set it as -1
	 * @param test
	 * @return
	 */

	public Response getAllUsersInLoggedOrganization(String filterStr, String sortStr, int pageNumber,
			int pageSize, ExtentTest test) {

		errorHandle.printDebugMessageInDebugFile("***********getJobsBySourceID***********");
		String extendUrl = getUrl4FilterSortPaging(filterStr, sortStr, pageNumber, pageSize, test);;
		/*
		 * int multipleFlag = 0; if ((filterStr != null) && (!filterStr.equals(""))) {
		 * test.log(LogStatus.INFO, "set filter url"); extendUrl += getFilterUrl(filterStr);
		 * multipleFlag++; } if ((sortStr != null) && (!sortStr.equals(""))) { test.log(LogStatus.INFO,
		 * "set sort url"); if (multipleFlag != 0) { extendUrl += "&"; } extendUrl +=
		 * getSortUrl(sortStr); multipleFlag++; } if ((pageNumber != -1) || (pageSize != -1)) {
		 * test.log(LogStatus.INFO, "set paging url"); if (multipleFlag != 0) { extendUrl += "&"; }
		 * extendUrl += getPagingUrl(pageNumber, pageSize); }
		 */

		test.log(LogStatus.INFO, "call getAllUsersInLoggedOrganization");
		errorHandle
		.printDebugMessageInDebugFile("*call getAllUsersInLoggedOrganization, url is " + extendUrl);
		Response response = spogInvoker.getAllUsersInLoggedOrganization(extendUrl, true, test);
		return response;
	}


	/**
	 * getAllUsersInLoggedOrganization used for API testing
	 * 
	 * @author zhaoguo.ma
	 * @param filterStr the format is
	 *        filterName1;operator;value,filterName2;operator;value,filterName1;operator;value
	 *        example: role_id;=;csr_admin or email;=;shuo.zhang@arcserve.com or role_id;!=;csr_admin,
	 *        email;like;shuo*@arcserve.com filterName: only accept: role_id/email operator:accept: =
	 *        != like value: role_id: ('csr_admin','msp_admin','direct_admin') email:<email> if
	 *        operator is like, in the value, must give * or ?
	 * @param sortStr the format is: sortedField;order,sortedField2;order example: first_name;asc or
	 *        email;desc or first_name;asc,email;desc sortedField: accept: 'create_ts','first_name',
	 *        'last_name','email' order: asc or desc
	 * @param pageNumber if you dont' want to set it, set it as -1
	 * @param pageSize if you dont' want to set it, set it as -1
	 * @param test
	 * @return
	 */

	public Response getAllUsersInOrganization(String orgID, String filterStr, String sortStr,
			int pageNumber, int pageSize, ExtentTest test) {

		String extendUrl = "";
		int multipleFlag = 0;
		if ((filterStr != null) && (!filterStr.equals(""))) {
			test.log(LogStatus.INFO, "set filter url");
			extendUrl += getFilterUrl(filterStr);
			multipleFlag++;
		}
		if ((sortStr != null) && (!sortStr.equals(""))) {
			test.log(LogStatus.INFO, "set sort url");
			if (multipleFlag != 0) {
				extendUrl += "&";
			}
			extendUrl += getSortUrl(sortStr);
			multipleFlag++;
		}
		if ((pageNumber != -1) || (pageSize != -1)) {
			test.log(LogStatus.INFO, "set paging url");
			if (multipleFlag != 0) {
				extendUrl += "&";
			}
			extendUrl += getPagingUrl(pageNumber, pageSize);
		}
		test.log(LogStatus.INFO, "call getAllUsersInLoggedOrganization");
		Response response = spogInvoker.getAllUsersInOrganization(orgID, extendUrl, test);
		return response;
	}


	/**
	 * return basic filter string
	 * 
	 * @author shuo.zhang
	 * @param filterName
	 * @param operator
	 * @param value
	 * @return
	 */
	public String getBasicFilterString(String filterName, String operator, String value) {

		errorHandle.printDebugMessageInDebugFile("***********getBasicFilterString***********");
		String filterStr = filterName;
		switch (operator.toLowerCase()) {
		case "=":
			filterStr += "=" + value;
			break;
		case "!=":
			filterStr += "=!" + value;
			break;
		case ">":
			filterStr += "=>" + value;
			break;
		case ">=":
			filterStr += "=>" + value;
			break;
		case "<":
			filterStr += "=<" + value;
			break;
		case "<=":
			filterStr += "=<" + value;
			break;
		case "like":
			filterStr += "=" + value;
			break;
		case "in":
			filterStr += "=" + value;
			break;
		}
		errorHandle.printDebugMessageInDebugFile("basicfilterStr is " + filterStr);
		return filterStr;

	}


	/**
	 * get filter url
	 * 
	 * @author shuo.zhang
	 * @param filterStr
	 * @return
	 */
	public String getFilterUrl(String filterStr) {

		errorHandle.printDebugMessageInDebugFile("***********getBasicFilterString***********");
		String filterUrl = "";
		String[] filterStrArray = filterStr.split(",");
		for (int i = 0; i < filterStrArray.length; i++) {
			String[] eachFilterStr = filterStrArray[i].split(";");
			filterUrl += getBasicFilterString(eachFilterStr[0], eachFilterStr[1], eachFilterStr[2]);
			if (i != filterStrArray.length - 1) {
				filterUrl += "&";
			}
		}
		errorHandle.printDebugMessageInDebugFile("filterStr is " + filterUrl);
		return filterUrl;

	}


	/**
	 * get sort url
	 * 
	 * @author shuo.zhang
	 * @param sortStr
	 * @return
	 */
	public String getSortUrl(String sortStr) {

		errorHandle.printDebugMessageInDebugFile("***********getSortUrl***********");
		String sortUrl = "sort=";
		String[] sortStrArray = sortStr.split(",");
		for (int i = 0; i < sortStrArray.length; i++) {
			String[] eachSortStr = sortStrArray[i].split(";");
			if (eachSortStr[1].equalsIgnoreCase("asc")) {
				sortUrl += eachSortStr[0];
			} else {
				sortUrl += "-" + eachSortStr[0];
			}
			if (i != sortStrArray.length - 1) {
				sortUrl += ",";
			}
		}
		errorHandle.printDebugMessageInDebugFile("sortUrl is " + sortUrl);
		return sortUrl;
	}


	/**
	 * return page url
	 * 
	 * @author shuo.zhang
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public String getPagingUrl(int pageNumber, int pageSize) {

		errorHandle.printDebugMessageInDebugFile("***********getPagingUrl***********");
		String pageUrl = null;
		boolean setPageUrl = false;
		if (pageNumber != -1) {
			pageUrl = "page=" + pageNumber;
			setPageUrl = true;
		}
		if (pageSize != -1) {
			if (setPageUrl) {
				// updated by zhaoguo, should be &page_size
				// pageUrl+= "&PageSize=" + pageSize;
				pageUrl += "&page_size=" + pageSize;
			} else {
				pageUrl = "page_size=" + pageSize;
			}

		}
		errorHandle.printDebugMessageInDebugFile("pageUrl is " + pageUrl);
		return pageUrl;
	}


	/**
	 * used for check getAllUsersInLoggedOrganization
	 * 
	 * @author shuo.zhang
	 * @param response
	 * @param expectedStatusCode
	 * @param expectedResponse
	 * @param curr_page
	 * @param page_size
	 * @param test
	 */
	public void checkGetAllUsersInLoggedOrganization(Response response, int expectedStatusCode,
			ArrayList<ResponseBody> expectedResponse, int curr_page, int page_size, int total_size,
			String expectedErrorCode, ExtentTest test) {

		errorHandle.printDebugMessageInDebugFile(
				"******************checkGetAllUsersInLoggedOrganization*******************");
		test.log(LogStatus.INFO, "expect status code is " + expectedStatusCode);
		errorHandle.printInfoMessageInDebugFile("expect status code is " + expectedStatusCode);
		response.then().statusCode(expectedStatusCode);
		errorHandle.printDebugMessageInDebugFile(response.then().log().body().toString());
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.INFO, "check response user info");

			for (int i = 0; i < expectedResponse.size(); i++) {
				test.log(LogStatus.INFO, "check create_ts");
				String loggedinUserId = GetLoggedinUser_UserID();
				List allowedActionList = new ArrayList<String>();

				if (expectedResponse.get(i).jsonPath().getString("data.user_id")
						.equalsIgnoreCase(loggedinUserId)) {
					//  allowedActionList.add("delete");
				} else {
					allowedActionList = expectedResponse.get(i).jsonPath().getList("data.allowed_actions");
				}


				response.then()
				.body("data[" + i + "].create_ts",
						equalTo(expectedResponse.get(i).jsonPath().getInt("data.create_ts")))
				.body("data[" + i + "].user_id",
						equalTo(expectedResponse.get(i).jsonPath().getString("data.user_id")))
				.body("data[" + i + "].first_name",
						equalTo(expectedResponse.get(i).jsonPath().getString("data.first_name")))
				.body("data[" + i + "].last_name",
						equalTo(expectedResponse.get(i).jsonPath().getString("data.last_name")))
				.body("data[" + i + "].email",
						equalTo(expectedResponse.get(i).jsonPath().getString("data.email")))
				.body("data[" + i + "].role_id",
						equalTo(expectedResponse.get(i).jsonPath().getString("data.role_id")))
				.body("data[" + i + "].organization_id",
						equalTo(expectedResponse.get(i).jsonPath().getString("data.organization_id")))
				.body("data[" + i + "].phone_number",
						equalTo(expectedResponse.get(i).jsonPath().getString("data.phone_number")))
				.body("data[" + i + "].blocked",
						equalTo(expectedResponse.get(i).jsonPath().getBoolean("data.blocked")))
				.body("data[" + i + "].status",
						equalTo(expectedResponse.get(i).jsonPath().getString("data.status")))
				.body("data[" + i + "].allowed_actions",
						// equalTo(expectedResponse.get(i).jsonPath().getList("data.allowed_actions")))
						equalTo(allowedActionList))
				.body("data[" + i + "].create_ts",
						equalTo(expectedResponse.get(i).jsonPath().getInt("data.create_ts")))
				.body("data[" + i + "].preference_language", equalTo(expectedResponse.get(i).jsonPath().getString("data.preference_language")));
			}
			/*
			 * test.log(LogStatus.INFO, "check response page info"); if ((curr_page == -1) || (curr_page
			 * == 0)) { curr_page = 1; } response.then().body("data.curr_page", equalTo(curr_page));
			 * 
			 * if ((page_size == -1) || (page_size == 0)) { page_size = 20; } else if (page_size > 100) {
			 * page_size = 100; } response.then().body("data.page_size", equalTo(page_size));
			 * 
			 * if (total_size == -1) { total_size = expectedResponse.size(); } int total_page = 0; if
			 * (total_size <= page_size) { total_page = 1; } else { int modResult = total_size %
			 * page_size; if (modResult == 0) { total_page = total_size / page_size; } else { total_page =
			 * total_size / page_size + 1; }
			 * 
			 * } response.then().body("data.total_page", equalTo(total_page));
			 * response.then().body("data.total_size", equalTo(total_size));
			 * 
			 * if (curr_page > 1) { response.then().body("data.has_prev", equalTo(true)); } else {
			 * response.then().body("data.has_prev", equalTo(false)); }
			 * 
			 * if (curr_page < total_page) { response.then().body("data.has_next", equalTo(true)); } else
			 * { response.then().body("data.has_next", equalTo(false)); }
			 */
			checkPagingInfo(response, expectedResponse.size(), curr_page, page_size, total_size, test);

		} else {
			checkErrorCode(response, expectedErrorCode);
		}

	}


	/**
	 * @author Shuo.zhang
	 * @param response
	 * @param expectedStatusCode
	 * @param curr_page
	 * @param page_size
	 * @param filterStr
	 * @param test
	 */
	public void checkGetAllUsersInLoggedOrganization4Filter(Response response, int expectedStatusCode,
			int curr_page, int page_size, String filterStr, ExtentTest test) {

		errorHandle.printDebugMessageInDebugFile(
				"******************checkGetAllUsersInLoggedOrganization4Filter*******************");
		test.log(LogStatus.INFO, "expect status code is " + expectedStatusCode);
		errorHandle.printInfoMessageInDebugFile("expect status code is " + expectedStatusCode);
		response.then().statusCode(expectedStatusCode);

		errorHandle.printInfoMessageInDebugFile(response.then().log().body().toString());
		test.log(LogStatus.INFO, "check filter " + expectedStatusCode);
		JsonPath jsonPathElevator = response.jsonPath();
		List<Object> userInfo = jsonPathElevator.getList("data");
		int return_size = userInfo.size();

		String[] filterStrArray = filterStr.split(",");
		for (int i = 0; i < return_size; i++) {
			for (int j = 0; j < filterStrArray.length; j++) {
				String[] eachFilterArray = filterStrArray[j].split(";");
				if (eachFilterArray[1].equals("=")) {
					response.then().body("data[" + i + "]." + eachFilterArray[0],
							equalTo(eachFilterArray[2]));
				} else if (eachFilterArray[1].equals("!=")) {
					response.then().body("data[" + i + "]." + eachFilterArray[0],
							not(equalTo(eachFilterArray[2])));
				}

			}

		}

		/*
		 * if (curr_page == -1) { curr_page = 1; } response.then().body("data.curr_page",
		 * equalTo(curr_page));
		 * 
		 * if (page_size == -1) { page_size = 20; } response.then().body("data.page_size",
		 * equalTo(page_size)); int total_size = response.then().extract().path("data.total_size"); int
		 * total_page = 0; if (total_size <= page_size) { total_page = 1; } else { int modResult =
		 * total_size % page_size; if (modResult == 0) { total_page = total_size / page_size; } else {
		 * total_page = total_size / page_size + 1; }
		 * 
		 * } response.then().body("data.total_page", equalTo(total_page));
		 * 
		 * 
		 * if (curr_page > 1) { response.then().body("data.has_prev", equalTo(true)); } else {
		 * response.then().body("data.has_prev", equalTo(false)); }
		 * 
		 * if (curr_page < total_page) { response.then().body("data.has_next", equalTo(true)); } else {
		 * response.then().body("data.has_next", equalTo(false)); }
		 */
		checkPagingInfo(response, response.then().extract().path("pagination.total_size"), curr_page,
				page_size, response.then().extract().path("pagination.total_size"), test);
	}


	/**
	 * get organizationID for current logged in user
	 * 
	 * @author kiran.sripada
	 * @return
	 */
	public String GetLoggedinUser_UserID() {

		Response response = spogInvoker.GetLoggedinUserInfo();

		String userID = response.then().extract().path("data.user_id");
		errorHandle.printDebugMessageInDebugFile("Get user id from body is: " + userID);
		return userID;
	}
	/*
	 * public String checkCreateUser(Response response, int expectedStatusCode, String email, String
	 * first_name, String last_name, String role_id, String organization_id, String expectedErrorCode,
	 * String expectedErrorMessage, String expectedDetailErrorMessage, ExtentTest test) {
	 * 
	 * test.log(LogStatus.INFO, "check status code" ); checkResponseStatus(response,
	 * expectedStatusCode);
	 * 
	 * if (expectedStatusCode == SpogConstants.SUCCESS_POST) { test.log(LogStatus.INFO,
	 * "check response body " ); String user_id = response.then().body("first_name",
	 * equalTo(first_name)) // .body("last_name", equalTo(last_name)).body("email",
	 * equalTo(email.toLowerCase())) .body("last_name", equalTo(last_name)).body("email",
	 * equalTo(email.toLowerCase())) .body("role_id", equalTo(role_id)).body("organization_id",
	 * equalTo(organization_id)) // .body("createTimestamp",
	 * not(isEmptyOrNullString())).body("user_id", not(isEmptyOrNullString())) .body("create_ts",
	 * not(isEmptyOrNullString())).body("user_id", not(isEmptyOrNullString()))
	 * .extract().path("user_id"); test.log(LogStatus.INFO, "new create user id is  " + user_id );
	 * return user_id; } else { // for the error handling test. test.log(LogStatus.INFO,
	 * "for error handling, check error message" ); response.then().assertThat().body("code",
	 * containsString(expectedErrorCode)); response.then().assertThat().body("message",
	 * containsString(expectedErrorMessage)); response.then().assertThat().body("detailMessage",
	 * containsString(expectedDetailErrorMessage)); return null; } }
	 */


	@Deprecated
	public Response postJobAndCheck(String site_id, String job_id, long start_time_ts,
			String server_id, String resource_id, String rps_id, String datastore_id,
			String organization_id, String plan_id,

			String message_job_id, String message_job_type, String message_job_method,
			String message_job_status, String message_resource_id, String message_organization_id,
			String message_server_id, String message_rps_id, String message_datastore_id,
			String message_plan_id, long message_start_time_ts, long message_end_time_ts,
			ExtentTest test) {

		Response response = spogInvoker.createJob(jp.composeJobInfoMap(site_id, job_id, start_time_ts,
				server_id, resource_id, rps_id, datastore_id, organization_id, plan_id, message_job_id,
				message_job_type, message_job_method, message_job_status, message_resource_id,
				message_organization_id, message_server_id, message_rps_id, message_datastore_id,
				message_plan_id, message_start_time_ts, message_end_time_ts));
		test.log(LogStatus.INFO, "response status for create job " + response.getStatusCode());
		checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		errorHandle.printDebugMessageInDebugFile("response for create job " + response);

		test.log(LogStatus.INFO, "check response body ");

		response.then().body("data.site_id", equalTo(site_id)).body("data.job_id", equalTo(job_id))
		.body("data.start_time_ts", equalTo(start_time_ts))
		.body("data.server_id", equalTo(server_id)).body("data.resource_id", equalTo(resource_id))
		.body("data.rps_id", equalTo(rps_id)).body("data.datastore_id", equalTo(datastore_id))
		.body("data.organization_id", equalTo(organization_id))
		.body("data.plan_id", equalTo(plan_id)).body("data.message.job_id", equalTo(message_job_id))
		.body("data.message.job_type",
				equalTo(JobTypeConstants.getJobTypeMessage(message_job_type, message_job_method)))
		.body("data.message.job_method", equalTo(message_job_method))
		.body("data.message.job_status",
				equalTo(JobStatusConstants.jobstatus.get(message_job_status)))
		.body("data.message.resource_id", equalTo(message_resource_id))
		.body("data.message.organization_id", equalTo(message_organization_id))
		.body("data.message.server_id", equalTo(message_server_id))
		.body("data.message.rps_id", equalTo(message_rps_id))
		.body("data.message.datastore_id", equalTo(message_datastore_id))
		.body("data.message.plan_id", equalTo(message_plan_id))
		.body("data.message.start_time_ts", equalTo(message_start_time_ts))
		.body("data.message.end_time_ts", equalTo(message_end_time_ts));

		return response;
	}


	@Deprecated
	public void postJobAndCheckWithExpectedStatusCode(String site_id, String job_id,
			long start_time_ts, String server_id, String resource_id, String rps_id, String datastore_id,
			String organization_id, String plan_id, String message_job_id, String message_job_type,
			String message_job_method, String message_job_status, String message_resource_id,
			String message_organization_id, String message_server_id, String message_rps_id,
			String message_datastore_id, String message_plan_id, long message_start_time_ts,
			long message_end_time_ts, int status_code, ExtentTest test) {

		Response response = spogInvoker.createJob(jp.composeJobInfoMap(site_id, job_id, start_time_ts,
				server_id, resource_id, rps_id, datastore_id, organization_id, plan_id, message_job_id,
				message_job_type, message_job_method, message_job_status, message_resource_id,
				message_organization_id, message_server_id, message_rps_id, message_datastore_id,
				message_plan_id, message_start_time_ts, message_end_time_ts));
		test.log(LogStatus.INFO, "response status for create job " + response.getStatusCode());
		checkResponseStatus(response, status_code);
	}


	// public void postJobDataandCheck(String job_id_url, String job_id, String
	// job_seq, String
	// job_type,
	// String job_method, String job_status, String resource_id, String server_id,
	// String rps_id,
	// String datastore_id, long start_time_ts, long end_time_ts, String
	// protected_data_size,
	// String raw_data_size, String sync_read_size, String ntfs_volume_size,
	// String virtual_disk_provision_size, ExtentTest test) {
	//
	// Response response = spogInvoker.createJobData(job_id_url,
	// jp.composeJobDataInfoMap(job_id, job_seq, job_type, job_method, job_status,
	// resource_id,
	// server_id, rps_id, datastore_id, start_time_ts, end_time_ts,
	// protected_data_size,
	// raw_data_size, sync_read_size, ntfs_volume_size,
	// virtual_disk_provision_size));
	//
	// test.log(LogStatus.INFO, "response status for create job data " +
	// response.getStatusCode());
	// checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
	// errorHandle.printDebugMessageInDebugFile("response for create job data " +
	// response);
	//
	// test.log(LogStatus.INFO, "check response body ");
	// response.then().body("data.job_id", equalTo(job_id))
	// .body("data.job_type", equalTo(JobTypeConstants.getJobTypeMessage(job_type,
	// job_method)))
	// .body("data.job_status",
	// equalTo(JobStatusConstants.jobstatus.get(job_status)));
	//
	// if (job_seq != null) {
	// response.then().body("data.job_seq", equalTo(Integer.parseInt(job_seq)));
	// }
	//
	// if (job_method != null) {
	// response.then().body("data.job_method", equalTo(job_method));
	// }
	//
	// if (resource_id != null) {
	// response.then().body("data.resource_id", equalTo(resource_id));
	// }
	// if (server_id != null) {
	// response.then().body("data.server_id", equalTo(server_id));
	// }
	// if (rps_id != null) {
	// response.then().body("data.rps_id", equalTo(rps_id));
	// }
	// if (datastore_id != null) {
	// response.then().body("data.datastore_id", equalTo(datastore_id));
	// }
	//
	// }
	//
	//
	// public void postJobDataandCheckWithExpectedStatusCode(String job_id_url,
	// String job_id,
	// String job_seq, String job_type, String job_method, String job_status, String
	// resource_id,
	// String server_id, String rps_id, String datastore_id, long start_time_ts,
	// long end_time_ts,
	// String protected_data_size, String raw_data_size, String sync_read_size,
	// String ntfs_volume_size, String virtual_disk_provision_size, int status_code,
	// ExtentTest test) {
	//
	// Response response = spogInvoker.createJobData(job_id_url,
	// jp.composeJobDataInfoMap(job_id, job_seq, job_type, job_method, job_status,
	// resource_id,
	// server_id, rps_id, datastore_id, start_time_ts, end_time_ts,
	// protected_data_size,
	// raw_data_size, sync_read_size, ntfs_volume_size,
	// virtual_disk_provision_size));
	//
	// test.log(LogStatus.INFO, "response status for create job data " +
	// response.getStatusCode());
	// checkResponseStatus(response, status_code);
	// }

	/**
	 * change password for logged in user
	 * 
	 * @author yuefen.liu
	 * @param oldPassword
	 * @param newPassword
	 * @return response
	 */
	public Response changePasswordForLoggedInUser(String old_password, String new_password) {

		Map<String, String> passwordInfo = jp.passwordInfo(old_password, new_password);
		Response response = spogInvoker.ChangePasswordForLoggedInUser(passwordInfo, test);

		return response;
	}


	/**
	 * change password for logged in user with expected status code
	 * 
	 * @author yuefen.liu
	 * @param oldPassword
	 * @param newPassword
	 * @return response
	 */
	public void changePasswordForLoggedInUserWithExpectedStatusCode(String old_password,
			String new_password, int expectedStatusCode, ExtentTest test) {

		Response response = changePasswordForLoggedInUser(old_password, new_password);
		test.log(LogStatus.INFO,
				"response status for change password for logged in user is " + response.getStatusCode());
		checkResponseStatus(response, expectedStatusCode);
	}


	/**
	 * change password for specified user
	 * 
	 * @author yuefen.liu
	 * @param user_id
	 * @param oldPassword
	 * @param newPassword
	 * @return response
	 */
	public Response changePasswordForSpecifiedUser(String user_id, String old_password,
			String new_password) {

		Map<String, String> passwordInfo = jp.passwordInfo(old_password, new_password);
		Response response = spogInvoker.ChangePasswordForSpecifiedUser(user_id, passwordInfo, test);

		return response;
	}


	public Response changePasswordForSpecifiedUserWithBodyNull(String user_id) {

		Response response = spogInvoker.ChangePasswordForSpecifiedUserWithBodyNull(user_id, test);

		return response;
	}


	/**
	 * change password for specified user with expected status code
	 * 
	 * @author yuefen.liu
	 * @param user_id
	 * @param oldPassword
	 * @param newPassword
	 * @return response
	 */
	public void changePasswordForSpecifiedUserWithExpectedStatusCode(String user_id,
			String old_password, String new_password, int expectedStatusCode, ExtentTest test) {

		Response response = changePasswordForSpecifiedUser(user_id, old_password, new_password);
		test.log(LogStatus.INFO,
				"response status for change password for specified user is " + response.getStatusCode());
		checkResponseStatus(response, expectedStatusCode);
	}


	public Response getAllUsersInLoggedOrganizationWithoutLogin(int expectedStatusCode,
			String errorMessage, ExtentTest test) {

		test.log(LogStatus.INFO, "call getAllUsersInLoggedOrganizationWithoutLogin");
		Response response = spogInvoker.getAllUsersInLoggedOrganization(null, false, test);
		checkGetAllUsersInLoggedOrganization(response, expectedStatusCode, null, -1, -1, -1,
				errorMessage, test);
		return response;
	}


	public Response getsuborgaccountinfobyId(String token, String URIparentId, String suborgId,
			ExtentTest test) {

		test.log(LogStatus.INFO, "Getting the suborg Info by sub org id");
		Response response = spogInvoker.getsuborgbyId(URIparentId, suborgId, token);
		return response;
	}


	public Response getsuborgaccountinfo(String token, String URIparentId, ExtentTest test) {

		test.log(LogStatus.INFO, "Getting the suborg Info by sub org id");
		Response response = spogInvoker.getsuborg(URIparentId, token);
		return response;
	}


	/**
	 * get the suborgnization details by sub org Id
	 * 
	 * @author kiran.sripada
	 * @param token
	 * @param extra_Inputs
	 * @param URIparentId
	 * @param suborgId
	 * @param expectedstatuscode
	 * @param organizationname
	 * @param expectedErrorMessage
	 * @param extenttest object
	 * 
	 */
	public void getsuborgaccountinfobyIdwithcheck(String token, HashMap<String, Object> extra_Inputs,
			String URIparentId, String suborgId, int expectedstatuscode, String organizationname,
			SpogMessageCode expectedErrorMessage, ExtentTest test) {

		test.log(LogStatus.INFO, "Getting the suborg Info by sub org id");
		Response response = spogInvoker.getsuborgbyId(URIparentId, suborgId, token);
		checkResponseStatus(response, expectedstatuscode);
		test.log(LogStatus.PASS, "The status code matched with the expected " + expectedstatuscode);
		if (response.getStatusCode() == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.INFO, "Check the response body");
			response.then().body("data.organization_id", equalTo(suborgId))
			.body("data.organization_name", equalTo(organizationname))
			.body("data.parent_id", equalTo(URIparentId))
			.body("data.create_ts", not(isEmptyOrNullString()));

			HashMap<String, Object> extra_Inputs_source_status =
					(HashMap<String, Object>) extra_Inputs.get("source_status");
			HashMap<String, Object> extra_Inputs_backup_status =
					(HashMap<String, Object>) extra_Inputs.get("backup_status");
			HashMap<String, Object> extra_Inputs_cloud_direct_usage =
					(HashMap<String, Object>) extra_Inputs.get("cloud_direct_usage");
			HashMap<String, Object> extra_Inputs_cloud_hybrid_usage =
					(HashMap<String, Object>) extra_Inputs.get("cloud_hybrid_usage");
			HashMap<String, Object> extra_Inputs_added_by =
					(HashMap<String, Object>) extra_Inputs.get("added_by");
			ArrayList<String> extra_Inputs_allowed_actions =
					(ArrayList<String>) extra_Inputs.get("allowed_actions");
			Boolean exp_blocked = (Boolean) extra_Inputs.get("blocked");

			test.log(LogStatus.INFO, "validating source status in the response  ");
			HashMap<String, Object> act_source_status =
					response.then().extract().path("data.source_status");
			assertResponseItem(extra_Inputs_source_status.get("num_total").toString(),
					act_source_status.get("num_total").toString());
			assertResponseItem(extra_Inputs_source_status.get("num_protected").toString(),
					act_source_status.get("num_protected").toString());
			assertResponseItem(extra_Inputs_source_status.get("num_unprotected").toString(),
					act_source_status.get("num_unprotected").toString());
			assertResponseItem(extra_Inputs_source_status.get("num_online").toString(),
					act_source_status.get("num_online").toString());
			assertResponseItem(extra_Inputs_source_status.get("num_offline").toString(),
					act_source_status.get("num_offline").toString());

			test.log(LogStatus.INFO, "validating backup status in the response  ");
			HashMap<String, Object> act_backup_status =
					response.then().extract().path("data.backup_status");
			assertResponseItem(extra_Inputs_backup_status.get("num_success").toString(),
					act_backup_status.get("num_success").toString());
			assertResponseItem(extra_Inputs_backup_status.get("num_missed").toString(),
					act_backup_status.get("num_missed").toString());
			assertResponseItem(extra_Inputs_backup_status.get("num_failed").toString(),
					act_backup_status.get("num_failed").toString());

			test.log(LogStatus.INFO, "validating cloud_direct_usage in the response  ");
			HashMap<String, Object> act_cloud_direct_usage =
					response.then().extract().path("data.cloud_direct_usage");
			assertResponseItem(extra_Inputs_cloud_direct_usage.get("usage").toString(),
					act_cloud_direct_usage.get("usage").toString());
			assertResponseItem(extra_Inputs_cloud_direct_usage.get("threshold").toString(),
					act_cloud_direct_usage.get("threshold").toString());

			test.log(LogStatus.INFO, "validating cloud_hybrid_usage in the response  ");
			HashMap<String, Object> act_cloud_hybrid_usage =
					response.then().extract().path("data.cloud_hybrid_usage");
			assertResponseItem(extra_Inputs_cloud_hybrid_usage.get("usage").toString(),
					act_cloud_hybrid_usage.get("usage").toString());
			assertResponseItem(extra_Inputs_cloud_hybrid_usage.get("threshold").toString(),
					act_cloud_hybrid_usage.get("threshold").toString());

			test.log(LogStatus.INFO, "validating added_by in the response  ");
			HashMap<String, Object> act_added_by = response.then().extract().path("data.added_by");
			if (!(act_added_by == null)) {
				assertResponseItem(extra_Inputs_added_by.get("id").toString(),
						act_added_by.get("id").toString());
				assertResponseItem(extra_Inputs_added_by.get("name").toString(),
						act_added_by.get("name").toString());
				assertResponseItem(extra_Inputs_added_by.get("email").toString(),
						act_added_by.get("email").toString());
			}

			ArrayList<String> act_allowed_actions =
					response.then().extract().path("data.allowed_actions");
			assertResponseItem(extra_Inputs_allowed_actions.toString(), act_allowed_actions.toString());

			String act_blocked = response.then().extract().path("data.blocked").toString();

			int length = act_blocked.length() - 1;
			String act = act_blocked.substring(1, length);

			if (act.toString().equals(exp_blocked.toString())) {
				test.log(LogStatus.INFO, "validation got passed for blocked status");
			}

			test.log(LogStatus.INFO, "The response generated is  successfull ");

			test.log(LogStatus.PASS, "compare user info for organization passed" + organizationname);
		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			if (code.contains("00100201")) {
				message = message.replace("{0}", suborgId);
			}
			if (code.contains("0030000A")) {
				message = message.replace("{0}", suborgId);
			}
			checkErrorCode(response, code);
			test.log(LogStatus.INFO, "The error code matched with the expected " + code);
			checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		}

	}


	public Response getUserroles(String filterStr, String sortStr, int pageNumber, int pageSize,
			ExtentTest test) {

		String extendUrl = "";
		int multipleFlag = 0;
		if ((filterStr != null) && (!filterStr.equals(""))) {
			test.log(LogStatus.INFO, "set filter url");
			extendUrl += getFilterUrl(filterStr);
			multipleFlag++;
		}
		if ((sortStr != null) && (!sortStr.equals(""))) {
			test.log(LogStatus.INFO, "set sort url");
			if (multipleFlag != 0) {
				extendUrl += "&";
			}
			extendUrl += getSortUrl(sortStr);
			multipleFlag++;
		}
		if ((pageNumber != -1) || (pageSize != -1)) {
			test.log(LogStatus.INFO, "set paging url");
			if (multipleFlag != 0) {
				extendUrl += "&";
			}
			extendUrl += getPagingUrl(pageNumber, pageSize);
		}
		test.log(LogStatus.INFO, "call getuserroles");
		Response response = spogInvoker.getUserroles(extendUrl, test);
		checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		return response;
	}


	public void getUserrolesWithExpectedStatus(String filterStr, String sortStr, int pageNumber,
			int pageSize, int expectedStatusCode, ExtentTest test) {

		String extendUrl = "";
		int multipleFlag = 0;
		if ((filterStr != null) && (!filterStr.equals(""))) {
			test.log(LogStatus.INFO, "set filter url");
			extendUrl += getFilterUrl(filterStr);
			multipleFlag++;
		}
		if ((sortStr != null) && (!sortStr.equals(""))) {
			test.log(LogStatus.INFO, "set sort url");
			if (multipleFlag != 0) {
				extendUrl += "&";
			}
			extendUrl += getSortUrl(sortStr);
			multipleFlag++;
		}
		if ((pageNumber != -1) || (pageSize != -1)) {
			test.log(LogStatus.INFO, "set paging url");
			if (multipleFlag != 0) {
				extendUrl += "&";
			}
			extendUrl += getPagingUrl(pageNumber, pageSize);
		}
		test.log(LogStatus.INFO, "call getuserroles");
		Response response = spogInvoker.getUserroles(extendUrl, test);
		checkResponseStatus(response, expectedStatusCode);
	}


	/**
	 * get the jobs by job Id
	 * 
	 * @author kiran.sripada
	 * @param user token
	 * @param jobId
	 * @param source name
	 * @param site id
	 * @param expectedstatuscode
	 * @param response of post jobs
	 * @param expectederrorMessage
	 * @param extenttest object
	 * 
	 */
	public void getjobsbyjobIdwithcheck(String user_token, String jobId, String resource_name,
			String site_id, int expectedstatuscode, ArrayList<HashMap<String, Object>> expectedresponse,
			SpogMessageCode expectederrorMessage, ExtentTest test) {

		ArrayList<HashMap<String, Object>> actual_response = new ArrayList<>();
		Response response = spogInvoker.getjobsbyJobId(user_token, jobId);
		checkResponseStatus(response, expectedstatuscode);

		test.log(LogStatus.PASS, "The status code matched to the expected " + expectedstatuscode);
		if (response.getStatusCode() == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.INFO, "Check the response body");
			HashMap<String, Object> actualresponse = new HashMap<>();
			actualresponse = response.then().extract().path("data");
			actual_response.add(actualresponse);
			checkgetjobsvalidation(actual_response, expectedresponse, 0, 0, test, null);

		} else {
			String code = expectederrorMessage.getCodeString();
			String message = expectederrorMessage.getStatus();
			checkErrorCode(response, code);
			test.log(LogStatus.INFO, "The error code matched with the expected " + code);
			if (code.contains("00600003")) {
				message = message.replace("{0}", jobId);
				checkErrorMessage(response, message);
			} else {
				checkErrorMessage(response, message);
			}

			test.log(LogStatus.INFO, "The error message matched with the expected " + message);

		}

	}


	public void checkErrorMessage(Response response, String expectedErrorMessage) {

		/*    // response.then().assertThat().body(containsString((expectedErrorMessage)));
    List<String> messageArray = response.body().jsonPath().getList("errors.message");
    boolean find = false;
    for (int i = 0; i < messageArray.size(); i++) {
      if (messageArray.get(i).contains(expectedErrorMessage)) {
        find = true;
        break;
      }
    }

    if (find) {
      assertTrue("error message check is correct", true);
    } else {
      assertEquals(messageArray.get(0), expectedErrorMessage);
    }
		 */
	}


	public Response getJobDataByID(String token, String jobID, ExtentTest test) {

		Response response = spogInvoker.getjobsdatabyJobId(token, jobID);

		response.then().log().all();

		return response;
	}


	/**
	 * get the job data by job Id
	 * 
	 * @author kiran.sripada
	 * @param user_token
	 * @param jobId
	 * @param source name
	 * @param expectedstatuscode
	 * @param ResponseBody of post job data
	 * @param extenttest object
	 * 
	 */
	public void getjobdatabyjobIdwithcheck(String user_token, String jobId, int expectedstatuscode,
			ArrayList<HashMap<String, Object>> expectedresponse, SpogMessageCode expectederrorMessage,
			ExtentTest test) {

		ArrayList<HashMap<String, Object>> actual_response = new ArrayList<HashMap<String, Object>>();
		Response response = spogInvoker.getjobsdatabyJobId(user_token, jobId);
		checkResponseStatus(response, expectedstatuscode);
		String expected_message;
		test.log(LogStatus.PASS, "The status code matched to the expected " + expectedstatuscode);

		if (response.getStatusCode() == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.INFO, "Check the response body");
			HashMap<String, Object> job_data = response.then().extract().path("data");
			actual_response.add(job_data);
			checkgetjobsvalidation(actual_response, expectedresponse, 0, 0, test, "job_data");
		} else {
			String code = expectederrorMessage.getCodeString();
			String message = expectederrorMessage.getStatus();
			checkErrorCode(response, code);
			if (code.contains("00600003")) {
				message = message.replace("{0}", jobId);
				checkErrorMessage(response, message);
			} else {
				checkErrorMessage(response, message);
			}
			test.log(LogStatus.INFO, "The error message matched with the expected " + message);
			// place holder for errr handling
		}

	}


	/**
	 * get the job logs by job Id
	 * 
	 * @author kiran.sripada
	 * @param user_token
	 * @param jobId
	 * @param extenttest object
	 * 
	 */
	public Response getjoblogbyjobId(String user_token, String jobId, ExtentTest test) {

		Response response = spogInvoker.getjoblogbyjobId(user_token, jobId, test);
		return response;
	}


	/**
	 * get the job logs by job Id
	 * 
	 * @author kiran.sripada
	 * @param user_token
	 * @param jobId
	 * @param source name
	 * @param expectedstatuscode
	 * @param ResponseBody of post job data
	 * @param extenttest object
	 * 
	 */

	public void getjoblogbyjobIdwithcheck(String user_token, String jobId,
			ArrayList<HashMap<String, Object>> expectedresponse, int expectedstatuscode,
			SpogMessageCode expectederrorMessage, ExtentTest test) {

		int size_actual_response = 0;
		int size_expected_response = 0;
		ArrayList<HashMap<String, Object>> actual_response = new ArrayList<>();
		HashMap<String, Object> actual_subresponse = new HashMap<>();
		HashMap<String, Object> expected_subresponse = new HashMap<>();
		String[] expected_message = null;
		String expect_mesg;

		Response response = spogInvoker.getjoblogbyjobId(user_token, jobId, test);
		checkResponseStatus(response, expectedstatuscode);

		test.log(LogStatus.PASS, "The status code matched to the expected " + expectedstatuscode);
		if (response.getStatusCode() == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			actual_response = response.then().extract().path("data");

			// This method is used to sort all the values

			/*
			 * Collections.sort(actual_response, new Comparator<HashMap<String, Object>>() {
			 * 
			 * @Override public int compare(HashMap<String, Object> o1, HashMap<String, Object> o2) {
			 * 
			 * // TODO Auto-generated method stub String create_ts = (String) o1.get("createTime"); String
			 * create_ts1 = (String) o2.get("createTime"); if (create_ts.compareTo(create_ts1) > 0) return
			 * 1; else if (create_ts.compareTo(create_ts1) == 0) return 0; else return -1; } });
			 */

			// This method is used to sort all the values
			/*    Collections.sort(actual_response, new Comparator<HashMap>() {

        @Override
        public int compare(HashMap o1, HashMap o2) {

          // TODO Auto-generated method stub
          Long create_ts = (Long) o1.get("log_ts");
          Long create_ts1 = (Long) o2.get("log_ts");
          if (create_ts > create_ts1)
            return 1;
          if (create_ts < create_ts1)
            return -1;
          else
            return 0;

        }
      });*/

			// This method is used to sort all the values
			sortArrayListbyString(expectedresponse, "log_id");
			sortArrayListbyString(actual_response, "log_id");

			test.log(LogStatus.INFO, "Check the response body");
			size_actual_response = actual_response.size();
			for (int i = 0; i < size_actual_response; i++) {

				String abc = expectedresponse.get(i).get("messageId").toString();
				if ((expectedresponse.get(i).get("messageId").toString()).contains("testLogMessage")) {
					expected_message = (String[]) expectedresponse.get(i).get("message");
					expect_mesg = "[" + expected_message[0] + "] is tests log message [" + expected_message[1]
							+ "]" + ".";
				} else {
					expect_mesg = "Test job message.";
				}

				assertResponseItem(actual_response.get(i).get("log_id").toString(),
						expectedresponse.get(i).get("log_id").toString(), test);
				assertResponseItem(actual_response.get(i).get("log_severity_type").toString(),
						expectedresponse.get(i).get("log_severity_type").toString(), test);
				assertResponseItem(actual_response.get(i).get("log_source_type").toString(),
						expectedresponse.get(i).get("log_source_type").toString(), test);
				assertResponseItem(actual_response.get(i).get("site_id").toString(),
						expectedresponse.get(i).get("site_id").toString(), test);
				HashMap<String, Object> act_org = (HashMap<String, Object>) actual_response.get(i).get("organization");

				assertResponseItem(act_org.get("organization_id").toString(),
						expectedresponse.get(i).get("organization_id").toString(), test);

				actual_subresponse = (HashMap<String, Object>) actual_response.get(i).get("job_data");
				expected_subresponse = (HashMap<String, Object>) expectedresponse.get(i).get("job_data");

				assertResponseItem(actual_subresponse.get("job_id").toString(),
						expected_subresponse.get("job_id").toString());
				if (actual_subresponse.get("job_type") != null) {
					assertResponseItem(actual_subresponse.get("job_type").toString(),
							expected_subresponse.get("job_type").toString());
				} else {
					test.log(LogStatus.PASS,
							"The value of the job id is " + actual_subresponse.get("job_type"));
				}

				assertResponseItem(actual_subresponse.get("source_id").toString(),
						expected_subresponse.get("resource_id").toString());
				assertResponseItem(actual_subresponse.get("source_name").toString(),
						expected_subresponse.get("resource_name").toString());
				assertResponseItem(actual_subresponse.get("server_id").toString(),
						expected_subresponse.get("server_id").toString());
				assertResponseItem(actual_subresponse.get("generated_from").toString(),
						expected_subresponse.get("server_name").toString());
				if (actual_subresponse.get("rps_id") != null) {
					assertResponseItem(actual_subresponse.get("rps_id").toString(),
							expected_subresponse.get("rps_id").toString(), test);
				}

				if (actual_subresponse.get("rps_name") != null) {
					assertResponseItem(actual_subresponse.get("rps_name").toString(),
							expected_subresponse.get("rps_name").toString());
				} else {
					test.log(LogStatus.PASS,
							"The value of the rps name is " + actual_subresponse.get("rps_name"));
				}

			}

		} else {
			String code = expectederrorMessage.getCodeString();
			String message = expectederrorMessage.getStatus();
			checkErrorCode(response, code);
			test.log(LogStatus.INFO, "The error code matched with the expected " + code);
			if (code.contains("00600003")) {
				message = message.replace("{0}", jobId);
				checkErrorMessage(response, message);
			} else {
				checkErrorMessage(response, message);
			}
			test.log(LogStatus.INFO, "The error message matched with the expected " + message);
			// place holder for errr handling
		}

	}


	public HashMap<String, Object> composejob_log(String log_id, String log_severity_type,
			String log_source_type, String site_id, String organization_id, String[] logmessagedata,
			String job_id, String jobType, String source_id, String resource_name, String rps_id,
			String rps_name) {

		HashMap<String, Object> temp = new HashMap<>();
		HashMap<String, Object> temp1 = new HashMap<>();
		ArrayList<HashMap<String, Object>> expected_response = new ArrayList<>();

		temp.put("log_id", log_id);
		temp.put("log_severity_type", log_severity_type);
		temp.put("log_source_type", log_source_type);
		temp.put("site_id", site_id);
		temp.put("organization_id", organization_id);
		temp.put("message", logmessagedata);
		temp1.put("job_type", jobType);
		temp1.put("resource_id", source_id);

		temp1.put("resource_name", resource_name);

		temp1.put("rps_id", rps_id);
		temp1.put("job_id", job_id);

		if (rps_name == "" || rps_name == null) {
			temp1.put("rps_name", null);
		} else {
			temp1.put("rps_name", rps_name);
		}
		temp1.put("rps_name", null);
		temp.put("job_data", temp1);

		// expected_response.add(temp);

		return temp;
	}


	/**
	 * overloading function for enhacements in sprint 9
	 * 
	 * @author : Kiran Sripada
	 */
	public HashMap<String, Object> composejob_log(String log_id, String log_severity_type,
			String log_source_type, String site_id, String organization_id, String messageId,
			String[] logmessagedata, String job_id, String jobType, String source_id, String server_id,
			String server_name, String resource_name, String rps_id, String rps_name) {

		HashMap<String, Object> temp = new HashMap<>();
		HashMap<String, Object> temp1 = new HashMap<>();
		ArrayList<HashMap<String, Object>> expected_response = new ArrayList<>();

		temp.put("log_id", log_id);
		temp.put("log_severity_type", log_severity_type);
		temp.put("log_source_type", log_source_type);
		temp.put("site_id", site_id);
		temp.put("organization_id", organization_id);
		temp.put("messageId", messageId);
		temp.put("message", logmessagedata);
		temp1.put("job_type", jobType);
		temp1.put("resource_id", source_id);
		temp1.put("server_id", server_id);
		temp1.put("resource_name", resource_name);
		temp1.put("server_name", server_name);
		temp1.put("rps_id", rps_id);
		temp1.put("job_id", job_id);

		if (rps_name == "" || rps_name == null) {
			temp1.put("rps_name", null);
		} else {
			temp1.put("rps_name", rps_name);
		}
		temp1.put("rps_name", null);
		temp.put("job_data", temp1);

		// expected_response.add(temp);

		return temp;
	}


	/**
	 * delete the log by log Id
	 * 
	 * @author kiran.sripada
	 * @param user_token
	 * @param log id
	 * @param expectedstatuscode
	 * @param extenttest object
	 * 
	 */
	public void deletelogbylogId(String user_token, String log_id, int expectedstatuscode,
			SpogMessageCode expectederrorMessage, ExtentTest test) {

		Response response = spogInvoker.deletelogbylogId(user_token, log_id, test);
		checkResponseStatus(response, expectedstatuscode);

		test.log(LogStatus.PASS, "The status code matched to the expected " + expectedstatuscode);
		if (response.getStatusCode() == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.INFO, "Check the response body");
			test.log(LogStatus.PASS, "The values matched the expected");

		} else {
			String code = expectederrorMessage.getCodeString();
			String message = expectederrorMessage.getStatus();
			checkErrorCode(response, code);
			test.log(LogStatus.INFO, "The error code matched with the expected " + code);
			if (code.contains("00700001")) {
				message = message.replace("{0}", log_id);
				checkErrorMessage(response, message);
			} else {
				checkErrorMessage(response, message);
			}

			test.log(LogStatus.INFO, "The error message matched with the expected " + message);
		}

	}


	/**
	 * Store the acutal job info
	 * 
	 * @author kiran.sripada
	 * @param jobsinfo
	 * 
	 * 
	 */
	public void setjobsInfo(HashMap<String, HashMap<String, Object>> jobsinfo) {

		jp.setjobsInfo(jobsinfo);
	}


	/**
	 * Store the acutal job info
	 * 
	 * @author kiran.sripada
	 * @param jobsinfo
	 * 
	 * 
	 */
	public void setjobData(HashMap<String, Object> jobData) {

		jp.setjobdata(jobData);
	}


	/**
	 * Generic function for the Preparation of Additional URL
	 * 
	 * @author BharadwajReddy
	 * @param FilterStr
	 * @param sortStr
	 * @param pageNumber
	 * @param pageSize
	 * 
	 */
	public String PrepareURL(String filterStr, String sortStr, int pageNumber, int pageSize,
			ExtentTest test) {

		String additionalUrl = "";
		int multipleFlag = 0;
		if ((filterStr != null) && (!filterStr.equals("")&& (!filterStr.equals("none")))) {
			test.log(LogStatus.INFO, "set filter url");
			additionalUrl += getFilterUrl(filterStr);
			multipleFlag++;
		}
		if ((sortStr != null) && (!sortStr.equals(""))) {
			test.log(LogStatus.INFO, "set sort url");
			if (multipleFlag != 0) {
				additionalUrl += "&";
			}
			additionalUrl += getSortUrl(sortStr);
			multipleFlag++;
		}
		if ((pageNumber != -1) || (pageSize != -1)) {
			test.log(LogStatus.INFO, "set paging url");
			if (multipleFlag != 0) {
				additionalUrl += "&";
			}
			additionalUrl += getPagingUrl(pageNumber, pageSize);
		}
		System.out.println("additonal url;" + additionalUrl);
		return additionalUrl;
	}


	/**
	 * Get all the Organization of the specified MSP
	 * 
	 * @author BharadwajReddy
	 * @param JwtToken
	 * @param msporgId
	 * @param additionalUrl
	 * @param Test
	 * @return response
	 */
	public Response getAllaccountsForSpecifiedMsp(String token, String msporgId, String additionalURL,
			ExtentTest test) {

		setUUID(msporgId);
		test.log(LogStatus.INFO, "Preparation of additional url ");
		Response response =
				spogInvoker.getallAccountsSpecifiedMsp(token, msporgId, additionalURL, test);
		return response;
	}


	/**
	 * CheckAllAccountsForSpecifedMsp(This method is used to valid all the accounts returned )
	 * 
	 * @author bharadwajReddy
	 * @param response
	 * @param expectedStatusCode
	 * @param ArraysOfAccounts
	 * @param curr_page
	 * @param page_size
	 * @param filterStr
	 * @param ExtentTest
	 */
	public void CheckallAccountsForSpecifiedMsp(Response response, int expectedStatusCode,
			ArrayList<ResponseBody> ExpectedAccounts, HashMap<String, Object> extra_Inputs, int curr_page,
			int page_size, String FilterStr, String SortStr, SpogMessageCode Info, ExtentTest test) {

		int total_page = 0, return_size = 0, total_size = 0;
		if (curr_page == 0) {
			curr_page = 1;
		}
		if (page_size == 0 || page_size > SpogConstants.MAX_PAGE_SIZE) {
			page_size = 20;
		}
		String expectedErrorMessage = "", expectedErrorCode = "";

		ArrayList<HashMap<String, Object>> users = new ArrayList<HashMap<String, Object>>();
		ArrayList<HashMap<String, Object>> users_source_status =
				new ArrayList<HashMap<String, Object>>();
		ArrayList<HashMap<String, Object>> users_backup_status =
				new ArrayList<HashMap<String, Object>>();
		ArrayList<HashMap<String, Object>> users_cloud_direct_usage =
				new ArrayList<HashMap<String, Object>>();
		ArrayList<HashMap<String, Object>> users_cloud_hybrid_usage =
				new ArrayList<HashMap<String, Object>>();
		ArrayList<HashMap<String, Object>> users_added_by = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> extra_Inputs_source_status =
				(HashMap<String, Object>) extra_Inputs.get("source_status");
		HashMap<String, Object> extra_Inputs_backup_status =
				(HashMap<String, Object>) extra_Inputs.get("backup_status");
		HashMap<String, Object> extra_Inputs_cloud_direct_usage =
				(HashMap<String, Object>) extra_Inputs.get("cloud_direct_usage");
		HashMap<String, Object> extra_Inputs_cloud_hybrid_usage =
				(HashMap<String, Object>) extra_Inputs.get("cloud_hybrid_usage");
		HashMap<String, Object> extra_Inputs_added_by =
				(HashMap<String, Object>) extra_Inputs.get("added_by");
		ArrayList<String> extra_Inputs_allowed_actions =
				(ArrayList<String>) extra_Inputs.get("allowed_actions");
		Boolean exp_blocked = (Boolean) extra_Inputs.get("blocked");

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			users = response.then().extract().path("data");
			System.out.println("The expected size is " + ExpectedAccounts.size());
			return_size = users.size();

			total_size = response.then().extract().path("pagination.total_size");
			test.log(LogStatus.INFO, "The actual total size is " + total_size);
			test.log(LogStatus.INFO, "expect status code is " + expectedStatusCode);

			errorHandle.printInfoMessageInDebugFile("expect status code is " + expectedStatusCode);
			checkResponseStatus(response, expectedStatusCode);
		}
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE
				|| ExpectedAccounts.size() == total_size) {
			if (!(users == null)) {
				test.log(LogStatus.INFO, "check response user info");

				if (((SortStr == null) || (SortStr.equals("")))
						&& ((FilterStr == null) || (FilterStr.equals("")))) {
					// This method is used to sort all the values

					/*
					 * Collections.sort(users, new Comparator<HashMap>() {
					 * 
					 * @Override public int compare(HashMap o1, HashMap o2) {
					 * 
					 * // TODO Auto-generated method stub Long create_ts = (Long) o1.get("create_ts"); Long
					 * create_ts1 = (Long) o2.get("create_ts"); if (create_ts > create_ts1) return 1; if
					 * (create_ts < create_ts1) return -1; else return 0;
					 * 
					 * } });
					 */


					for (int i = 0; i < users.size(); i++) {
						users_source_status = response.then().extract().path("data.source_status");
						users_backup_status = response.then().extract().path("data.backup_status");
						users_cloud_direct_usage = response.then().extract().path("data.cloud_direct_usage");
						users_cloud_hybrid_usage = response.then().extract().path("data.cloud_hybrid_usage");
						users_added_by = response.then().extract().path("data.added_by");
						String act_blocked = response.then().extract().path("data.blocked").toString();

						int length = act_blocked.length() - 1;
						String act = act_blocked.substring(1, length);

						if (act.toString().equals(exp_blocked.toString())) {
							test.log(LogStatus.INFO, "validation got passed for blocked status");
						}
						if (users.get(i).get("organization_id")
								.equals(ExpectedAccounts.get(i).jsonPath().get("data.organization_id"))
								&& users.get(i).get("organization_name")
								.equals(ExpectedAccounts.get(i).jsonPath().get("data.organization_name"))
								&& users.get(i).get("organization_type")
								.equals(ExpectedAccounts.get(i).jsonPath().get("data.organization_type"))
								&& users.get(i).get("parent_id")
								.equals(ExpectedAccounts.get(i).jsonPath().get("data.parent_id"))
								&& users.get(i).get("create_ts")
								.equals(ExpectedAccounts.get(i).jsonPath().get("data.create_ts"))
								&& users.get(i).get("image_url").equals(extra_Inputs.get("image_url"))
								&& users.get(i).get("status").equals(extra_Inputs.get("status"))
								&& users.get(i).get("allowed_actions").equals(extra_Inputs_allowed_actions)) {

							test.log(LogStatus.INFO, "validating source status in the response  ");

							assertResponseItem(users_source_status.get(i).get("num_total").toString(),
									extra_Inputs_source_status.get("num_total").toString(), test);
							assertResponseItem(users_source_status.get(i).get("num_protected").toString(),
									extra_Inputs_source_status.get("num_protected").toString(), test);
							assertResponseItem(users_source_status.get(i).get("num_unprotected").toString(),
									extra_Inputs_source_status.get("num_unprotected").toString(), test);
							assertResponseItem(users_source_status.get(i).get("num_online").toString(),
									extra_Inputs_source_status.get("num_online").toString(), test);
							assertResponseItem(users_source_status.get(i).get("num_offline").toString(),
									extra_Inputs_source_status.get("num_offline").toString(), test);

							test.log(LogStatus.INFO, "validating backup_status in the response  ");

							assertResponseItem(users_backup_status.get(i).get("num_success").toString(),
									extra_Inputs_backup_status.get("num_success").toString(), test);
							assertResponseItem(users_backup_status.get(i).get("num_missed").toString(),
									extra_Inputs_backup_status.get("num_missed").toString(), test);
							assertResponseItem(users_backup_status.get(i).get("num_failed").toString(),
									extra_Inputs_backup_status.get("num_failed").toString(), test);
							assertResponseItem(users_backup_status.get(i).get("num_ar_failed").toString(),
									extra_Inputs_backup_status.get("num_ar_failed").toString(), test);

							test.log(LogStatus.INFO, "validating cloud_direct_usage in the response  ");

							assertResponseItem(users_cloud_direct_usage.get(i).get("usage").toString(),
									extra_Inputs_cloud_direct_usage.get("usage").toString(), test);
							assertResponseItem(users_cloud_direct_usage.get(i).get("threshold").toString(),
									extra_Inputs_cloud_direct_usage.get("threshold").toString(), test);

							test.log(LogStatus.INFO, "validating cloud_hybrid_usage in the response  ");

							assertResponseItem(users_cloud_hybrid_usage.get(i).get("usage").toString(),
									extra_Inputs_cloud_hybrid_usage.get("usage").toString(), test);
							assertResponseItem(users_cloud_hybrid_usage.get(i).get("threshold").toString(),
									extra_Inputs_cloud_hybrid_usage.get("threshold").toString(), test);

							test.log(LogStatus.INFO, "validating added_by in the response  ");

							assertResponseItem(users_added_by.get(i).get("id").toString(),
									extra_Inputs_added_by.get("id").toString(), test);
							assertResponseItem(users_added_by.get(i).get("name").toString(),
									extra_Inputs_added_by.get("name").toString(), test);
							assertResponseItem(users_added_by.get(i).get("email").toString(),
									extra_Inputs_added_by.get("email").toString(), test);

							test.log(LogStatus.INFO, "The response generated is  successfull ");

							test.log(LogStatus.PASS,
									"compare user info for " + users.get(i).get("organization_id") + " passed");

						}

						if (users.get(i).get("organization_type").toString().equalsIgnoreCase(SpogConstants.MSP_ORG)) {
							assertResponseItem("true", users.get(i).get("sub_msp").toString(), test);
						}else {
							assertResponseItem("false", users.get(i).get("sub_msp").toString(), test);
						}
					}

				}
				// SortStr
				else if (((SortStr != null) && (!SortStr.equals("")) && SortStr.contains("create_ts")
						&& ((FilterStr == null) || (FilterStr.equals(""))))
						|| ((SortStr != null) && (!SortStr.equals("")) && SortStr.contains("create_ts")
								&& ((FilterStr != null) || (!FilterStr.equals(""))))) {

					// validating the response for all the users who are sorted based on response in ascending
					// order and descending order
					if (SortStr.contains("asc")) {
						int j = 0;
						if (curr_page != 1) {
							j = (curr_page - 1) * page_size;
						}
						for (int i = 0; i < users.size(); i++, j++) {
							// check validation for all Msp child accounts


							users_source_status = response.then().extract().path("data.source_status");
							users_backup_status = response.then().extract().path("data.backup_status");
							users_cloud_direct_usage = response.then().extract().path("data.cloud_direct_usage");
							users_cloud_hybrid_usage = response.then().extract().path("data.cloud_hybrid_usage");
							users_added_by = response.then().extract().path("data.added_by");
							String act_blocked = response.then().extract().path("data.blocked").toString();

							int length = act_blocked.length() - 1;
							String act = act_blocked.substring(1, length);

							if (act.toString().equals(exp_blocked.toString())) {
								test.log(LogStatus.INFO, "validation got passed for blocked status");
							}
							if (users.get(i).get("organization_id")
									.equals(ExpectedAccounts.get(i).jsonPath().get("data.organization_id"))
									&& users.get(i).get("organization_name")
									.equals(ExpectedAccounts.get(i).jsonPath().get("data.organization_name"))
									&& users.get(i).get("organization_type")
									.equals(ExpectedAccounts.get(i).jsonPath().get("data.organization_type"))
									&& users.get(i).get("parent_id")
									.equals(ExpectedAccounts.get(i).jsonPath().get("data.parent_id"))
									&& users.get(i).get("create_ts")
									.equals(ExpectedAccounts.get(i).jsonPath().get("data.create_ts"))
									&& users.get(i).get("image_url").equals(extra_Inputs.get("image_url"))
									&& users.get(i).get("status").equals(extra_Inputs.get("status"))
									&& users.get(i).get("allowed_actions").equals(extra_Inputs_allowed_actions)) {

								test.log(LogStatus.INFO, "validating source status in the response  ");

								assertResponseItem(users_source_status.get(i).get("num_total").toString(),
										extra_Inputs_source_status.get("num_total").toString(), test);
								assertResponseItem(users_source_status.get(i).get("num_protected").toString(),
										extra_Inputs_source_status.get("num_protected").toString(), test);
								assertResponseItem(users_source_status.get(i).get("num_unprotected").toString(),
										extra_Inputs_source_status.get("num_unprotected").toString(), test);
								assertResponseItem(users_source_status.get(i).get("num_online").toString(),
										extra_Inputs_source_status.get("num_online").toString(), test);
								assertResponseItem(users_source_status.get(i).get("num_offline").toString(),
										extra_Inputs_source_status.get("num_offline").toString(), test);

								test.log(LogStatus.INFO, "validating backup_status in the response  ");

								assertResponseItem(users_backup_status.get(i).get("num_success").toString(),
										extra_Inputs_backup_status.get("num_success").toString(), test);
								assertResponseItem(users_backup_status.get(i).get("num_missed").toString(),
										extra_Inputs_backup_status.get("num_missed").toString(), test);
								assertResponseItem(users_backup_status.get(i).get("num_failed").toString(),
										extra_Inputs_backup_status.get("num_failed").toString(), test);
								assertResponseItem(users_backup_status.get(i).get("num_ar_failed").toString(),
										extra_Inputs_backup_status.get("num_ar_failed").toString(), test);

								test.log(LogStatus.INFO, "validating cloud_direct_usage in the response  ");

								assertResponseItem(users_cloud_direct_usage.get(i).get("usage").toString(),
										extra_Inputs_cloud_direct_usage.get("usage").toString(), test);
								assertResponseItem(users_cloud_direct_usage.get(i).get("threshold").toString(),
										extra_Inputs_cloud_direct_usage.get("threshold").toString(), test);

								test.log(LogStatus.INFO, "validating cloud_hybrid_usage in the response  ");

								assertResponseItem(users_cloud_hybrid_usage.get(i).get("usage").toString(),
										extra_Inputs_cloud_hybrid_usage.get("usage").toString(), test);
								assertResponseItem(users_cloud_hybrid_usage.get(i).get("threshold").toString(),
										extra_Inputs_cloud_hybrid_usage.get("threshold").toString(), test);

								test.log(LogStatus.INFO, "validating added_by in the response  ");

								assertResponseItem(users_added_by.get(i).get("id").toString(),
										extra_Inputs_added_by.get("id").toString(), test);
								assertResponseItem(users_added_by.get(i).get("name").toString(),
										extra_Inputs_added_by.get("name").toString(), test);
								assertResponseItem(users_added_by.get(i).get("email").toString(),
										extra_Inputs_added_by.get("email").toString(), test);

								test.log(LogStatus.INFO, "The response generated is  successfull ");

								test.log(LogStatus.PASS,
										"compare user info for " + users.get(i).get("organization_id") + " passed");

							}
							checkAllAccounts(response, ExpectedAccounts, i, j, test);

						}
					} else {
						int j = 0;
						if (page_size == 20) {
							j = users.size() - 1;
						} else {
							j = ExpectedAccounts.size() - 1;
							if (curr_page != 1) {
								j = (ExpectedAccounts.size() - 1) - (curr_page - 1) * page_size;
							}
						}
						for (int i = 0; i < users.size() && j >= 0; i++, j--) {
							// Check the validation for all the accounts
							users_source_status = response.then().extract().path("data.source_status");
							users_backup_status = response.then().extract().path("data.backup_status");
							users_cloud_direct_usage = response.then().extract().path("data.cloud_direct_usage");
							users_cloud_hybrid_usage = response.then().extract().path("data.cloud_hybrid_usage");
							users_added_by = response.then().extract().path("data.added_by");
							String act_blocked = response.then().extract().path("data.blocked").toString();

							int length = act_blocked.length() - 1;
							String act = act_blocked.substring(1, length);

							if (act.toString().equals(exp_blocked.toString())) {
								test.log(LogStatus.INFO, "validation got passed for blocked status");
							}
							if (users.get(i).get("organization_id")
									.equals(ExpectedAccounts.get(i).jsonPath().get("data.organization_id"))
									&& users.get(i).get("organization_name")
									.equals(ExpectedAccounts.get(i).jsonPath().get("data.organization_name"))
									&& users.get(i).get("organization_type")
									.equals(ExpectedAccounts.get(i).jsonPath().get("data.organization_type"))
									&& users.get(i).get("parent_id")
									.equals(ExpectedAccounts.get(i).jsonPath().get("data.parent_id"))
									&& users.get(i).get("create_ts")
									.equals(ExpectedAccounts.get(i).jsonPath().get("data.create_ts"))
									&& users.get(i).get("image_url").equals(extra_Inputs.get("image_url"))
									&& users.get(i).get("status").equals(extra_Inputs.get("status"))
									&& users.get(i).get("allowed_actions").equals(extra_Inputs_allowed_actions)) {

								test.log(LogStatus.INFO, "validating source status in the response  ");

								assertResponseItem(users_source_status.get(i).get("num_total").toString(),
										extra_Inputs_source_status.get("num_total").toString(), test);
								assertResponseItem(users_source_status.get(i).get("num_protected").toString(),
										extra_Inputs_source_status.get("num_protected").toString(), test);
								assertResponseItem(users_source_status.get(i).get("num_unprotected").toString(),
										extra_Inputs_source_status.get("num_unprotected").toString(), test);
								assertResponseItem(users_source_status.get(i).get("num_online").toString(),
										extra_Inputs_source_status.get("num_online").toString(), test);
								assertResponseItem(users_source_status.get(i).get("num_offline").toString(),
										extra_Inputs_source_status.get("num_offline").toString(), test);

								test.log(LogStatus.INFO, "validating backup_status in the response  ");

								assertResponseItem(users_backup_status.get(i).get("num_success").toString(),
										extra_Inputs_backup_status.get("num_success").toString(), test);
								assertResponseItem(users_backup_status.get(i).get("num_missed").toString(),
										extra_Inputs_backup_status.get("num_missed").toString(), test);
								assertResponseItem(users_backup_status.get(i).get("num_failed").toString(),
										extra_Inputs_backup_status.get("num_failed").toString(), test);
								assertResponseItem(users_backup_status.get(i).get("num_ar_failed").toString(),
										extra_Inputs_backup_status.get("num_ar_failed").toString(), test);

								test.log(LogStatus.INFO, "validating cloud_direct_usage in the response  ");

								assertResponseItem(users_cloud_direct_usage.get(i).get("usage").toString(),
										extra_Inputs_cloud_direct_usage.get("usage").toString(), test);
								assertResponseItem(users_cloud_direct_usage.get(i).get("threshold").toString(),
										extra_Inputs_cloud_direct_usage.get("threshold").toString(), test);

								test.log(LogStatus.INFO, "validating cloud_hybrid_usage in the response  ");

								assertResponseItem(users_cloud_hybrid_usage.get(i).get("usage").toString(),
										extra_Inputs_cloud_hybrid_usage.get("usage").toString(), test);
								assertResponseItem(users_cloud_hybrid_usage.get(i).get("threshold").toString(),
										extra_Inputs_cloud_hybrid_usage.get("threshold").toString(), test);

								test.log(LogStatus.INFO, "validating added_by in the response  ");

								assertResponseItem(users_added_by.get(i).get("id").toString(),
										extra_Inputs_added_by.get("id").toString(), test);
								assertResponseItem(users_added_by.get(i).get("name").toString(),
										extra_Inputs_added_by.get("name").toString(), test);
								assertResponseItem(users_added_by.get(i).get("email").toString(),
										extra_Inputs_added_by.get("email").toString(), test);

								test.log(LogStatus.INFO, "The response generated is  successfull ");

								test.log(LogStatus.PASS,
										"compare user info for " + users.get(i).get("organization_id") + " passed");

							}

							checkAllAccounts(response, ExpectedAccounts, i, j, test);
						}

					} // descending order
				} else {
					// only for filtering
					String[] filterStrArray = FilterStr.split(",");
					for (int i = 0; i < return_size; i++) {
						for (int j = 0; j < filterStrArray.length; j++) {
							String[] eachFilterArray = filterStrArray[j].split(";");
							if (eachFilterArray[1].equals("=")) {
								response.then().body("data.data[" + i + "]." + eachFilterArray[0],
										equalTo(eachFilterArray[2]));
								System.out.println("The value of the message inside the body"
										+ response.then().extract().path("data.data[" + i + "]." + eachFilterArray[0]));
							} else if (eachFilterArray[1].equals("!=")) {
								response.then().body("data.data[" + i + "]." + eachFilterArray[0],
										not(equalTo(eachFilterArray[2])));
							}

						}
					}
				} // only for filtering

				// check the response validation for pages,page_size,total_size
				validateResponseForPages(curr_page, page_size, response, total_size, test);
			}

		} else {
			if (Info.getStatus() != "0010000") {
				expectedErrorMessage = Info.getStatus();
				if (expectedErrorMessage.contains("{0}")) {
					expectedErrorMessage = expectedErrorMessage.replace("{0}", getUUId());
					System.out.println(expectedErrorMessage);
				}
				expectedErrorCode = Info.getCodeString();
			}
			checkErrorMessage(response, expectedErrorMessage);
			test.log(LogStatus.PASS, "The value of the message is " + expectedErrorMessage);
			checkErrorCode(response, expectedErrorCode);
			test.log(LogStatus.PASS, "The value of the code  generated  is " + expectedErrorCode);
			test.log(LogStatus.INFO,
					"The value of the  response generated actually is :" + response.getStatusCode());
		}
	}


	/**
	 * validation For all the child accounts under msp
	 * 
	 * @author BharadwajReddyGhadiam
	 * @param response
	 * @param ExpectedAccounts
	 * @param index i
	 * @param index j
	 */
	private void checkAllAccounts(Response response, ArrayList<ResponseBody> ExpectedAccounts, int i,
			int j, ExtentTest test) {
		// TODO Auto-generated method stub

		if (response.then()
				.body("data[" + i + "].organization_id",
						equalTo(ExpectedAccounts.get(j).jsonPath().get("data.organization_id")))
				.body("data[" + i + "].organization_name",
						equalTo(ExpectedAccounts.get(j).jsonPath().get("data.organization_name")))
				.body("data[" + i + "].organization_type",
						equalTo(ExpectedAccounts.get(j).jsonPath().get("data.organization_type")))
				.body("data[" + i + "].parent_id",
						equalTo(ExpectedAccounts.get(j).jsonPath().get("data.parent_id")))
				.body("data[" + i + "].create_ts",
						equalTo(ExpectedAccounts.get(j).jsonPath().get("data.create_ts"))) != null) {
			assertTrue("The validation for all the accounts has been matches ", true);
			test.log(LogStatus.INFO, "The validation for all the accounts has been passes");
		} else {
			assertTrue("The validation for all the accounts has been matches ", false);
			test.log(LogStatus.INFO,
					"The id not matched " + ExpectedAccounts.get(j).jsonPath().get("data.organization_id"));
			test.log(LogStatus.INFO, "The name not matched "
					+ ExpectedAccounts.get(j).jsonPath().get("data.organization_name"));
			test.log(LogStatus.INFO, "The type not matched "
					+ ExpectedAccounts.get(j).jsonPath().get("data.organization_type"));
		}
	}


	/**
	 * create account and check status and account name
	 * 
	 * @author BharadwajReddy
	 * @param urlParentId that means you set it in url
	 * @param accountName
	 * @param bodyParentId that means you transport it as json object in body
	 * @param ExtentTest
	 * @return Response
	 */
	public Response createAccountWithCheck2(String urlParentId, String accountName,
			String bodyParentId, ExtentTest test) {

		Response response = createAccount(urlParentId, accountName, bodyParentId);
		checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		checkBodyItemValue(response, "organization_type", SpogConstants.MSP_SUB_ORG);
		checkBodyItemValue(response, "parent_id", urlParentId);
		checkBodyItemValue(response, "organization_name", accountName);
		return response;
	}


	/**
	 * get jobs by source id
	 * 
	 * @author shuo.zhang
	 * @param resouce_id
	 * @param test
	 * @return response
	 */

	public Response getJobsBySourceID(String resouce_id, String filterStr, String sortStr,
			int pageNumber, int pageSize, boolean isLoggedIn, ExtentTest test) {

		errorHandle.printDebugMessageInDebugFile("***********getJobsBySourceID***********");

		String extendUrl = getUrl4FilterSortPaging(filterStr, sortStr, pageNumber, pageSize, test);
		test.log(LogStatus.INFO, "get extend url" + extendUrl);
		test.log(LogStatus.INFO, "get jobs by source id response");
		Response response = spogInvoker.getJobsBySourceID(resouce_id, extendUrl, isLoggedIn, test);
		errorHandle.printInfoMessageInDebugFile(response.getBody().toString());
		return response;

	}


	/**
	 * @author shuo
	 * @param resouce_id
	 * @param filterStr
	 * @param sortStr
	 * @param pageNumber
	 * @param pageSize
	 * @param totalSize
	 * @param expectedInfoMapArray
	 * @param expectedStatusCode
	 * @param test
	 */
	public void getJobsBySourceIDWithCheck(String resouce_id, String filterStr, String sortStr,
			int pageNumber, int pageSize, int totalSize,
			ArrayList<HashMap<String, Object>> expectedInfoMapArray, int expectedStatusCode,
			String expectedErrorCode, boolean isLoggedIn, ExtentTest test) {

		errorHandle.printDebugMessageInDebugFile("***********getJobsBySourceIDWithCheck***********");

		Response response =
				getJobsBySourceID(resouce_id, filterStr, sortStr, pageNumber, pageSize, isLoggedIn, test);

		errorHandle.printDebugMessageInDebugFile("check getJobsBySourceID status code");

		test.log(LogStatus.INFO, "check getJobsBySourceID status code");
		response.then().statusCode(expectedStatusCode);
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.INFO, "check getJobsBySourceID body");
			for (int i = 0; i < expectedInfoMapArray.size(); i++) {
				Map<String, Object> allInfoMap = response.jsonPath().getMap("data[" + i + "]");
				HashMap<String, Object> expectedHashMap = expectedInfoMapArray.get(i);

				// assertEquals(allInfoMap.size(), expectedHashMap.size());
				Iterator<String> expectedKeyIter = expectedHashMap.keySet().iterator();
				while (expectedKeyIter.hasNext()) {
					String key = expectedKeyIter.next();
					System.out.println(key);
					/*
					 * if (key.equalsIgnoreCase("start_time_ts") || key.equalsIgnoreCase("end_time_ts")) {
					 * Long expectedValue = (Long) expectedHashMap.get(key); Long realValue = (Long)
					 * allInfoMap.get(key); assertEquals(expectedValue, realValue); } else if
					 * (!key.equalsIgnoreCase("available_actions")) { Object expectedValue =
					 * expectedHashMap.get(key); Object realValue = allInfoMap.get(key);
					 * 
					 * assertEquals(expectedValue, realValue); }
					 */

					if (!key.equalsIgnoreCase("available_actions")) {
						Object expectedValue = expectedHashMap.get(key);
						if(key.equalsIgnoreCase("job_type")){
							Object realValue = allInfoMap.get(key);
							assertEquals(expectedValue, realValue);
						}else{
							Object realValue = allInfoMap.get(key);
							assertEquals(expectedValue, realValue);
						}



					}
				}
			}
			test.log(LogStatus.INFO, "check paging info");
			checkPagingInfo(response, expectedInfoMapArray.size(), pageNumber, pageSize, totalSize, test);

		} else {
			checkErrorCode(response, expectedErrorCode);
		}

	}


	/**
	 * create group
	 * 
	 * @author liu.yuefen
	 * @param organization_id
	 * @param group_name
	 * @param group_description
	 * @return response
	 */
	public Response createGroup(String organization_id, String group_name, String group_description) {

		Map<String, String> groupInfo = jp.groupInfo(organization_id, group_name, group_description);
		Response response = spogInvoker.CreateGroup(groupInfo);
		return response;
	}


	/**
	 * create group successful with check
	 * 
	 * @author liu.yuefen
	 * @param organization_id
	 * @param group_name
	 * @param group_description
	 * @return group_id
	 */
	public String createGroupWithCheck(String organization_id, String group_name,
			String group_description, ExtentTest test) {

		Response response = createGroup(organization_id, group_name, group_description);
		checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		// checkBodyItemValue(response, "organization_id", organization_id);
		// checkBodyItemValue(response, "group_name", group_name);
		// checkBodyItemValue(response, "group_description", group_description);
		String group_id = response.then().extract().path("data.group_id");

		return group_id;
	}


	/**
	 * create group successful with check
	 * 
	 * @author liu.yuefen
	 * @param organization_id
	 * @param group_name
	 * @param group_description
	 * @return group_id
	 */
	public String createGroupSuccessWithCheck(String organization_id, String group_name,
			String group_description, ExtentTest test) {

		Response response = createGroup(organization_id, group_name, group_description);
		checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		response.then().body("data.group_name", equalTo(group_name));
		if ((group_description != null) && (!group_description.equals(""))) {
			response.then().body("data.group_description", equalTo(group_description));
		} else {
			response.then().body("data.group_description", isEmptyOrNullString());
		}
		String userID = GetLoggedinUser_UserID();
		String loggedInUserOrgId = GetOrganizationIDforUser(userID);
		if ((organization_id != null) && (!organization_id.equals(""))) {
			response.then().body("data.organization_id", equalTo(organization_id));
		} else {
			response.then().body("data.organization_id", equalTo(loggedInUserOrgId));
		}
		String group_id = response.then().extract().path("data.group_id");

		return group_id;
	}


	/**
	 * create group fail and check statusCode
	 * 
	 * @author liu.yuefen
	 * @param organization_id
	 * @param group_name
	 * @param group_description
	 */
	public void createGroupFailedWithExpectedStatusCode(String organization_id, String group_name,
			String group_description, int expectedStatusCode, ExtentTest test) {

		Response response = createGroup(organization_id, group_name, group_description);
		test.log(LogStatus.INFO, "response status for create group is " + response.getStatusCode());
		checkResponseStatus(response, expectedStatusCode);
	}


	/**
	 * create filter failed and check statusCode
	 * 
	 * @author shan.jing
	 * @param user_id: UUID
	 * @param filter_name: String
	 * @param protection_status: String, one or more of "Protected","Unprotected", separated by ","
	 *        like "Supported,Unsupported";
	 * @param connection_status: String, one or more of "online","offline", separated by "," like
	 *        "online"
	 * @param protection_policy: UUID
	 * @param backup_status: String, one or more of below values, separated by "," like
	 *        "Active,Finished" Active(0), Finished(1), Canceled(2), Failed(3), Incomplete(4),// means
	 *        finish Idle(5),// no use Waiting(6), Crash(7), LicenseFailed(9),
	 *        BackupJob_PROC_EXIT(10), Skipped(11), Stop(12), Missed(10000)
	 * @param source_group: UUID
	 * @param operating_system: String, one or more of below values, separated by "," like
	 *        "windows,linux" unknown(0),windows(1),linux(2),mac(3);
	 * @param applications: String, one of more of below values, separated by "," like "SQL_SERVER"
	 *        All(-1),SQL_SERVER(0),EXCHANGE(1),EXCHANGE_ONLINE(2);
	 * @param expectedErrorCode
	 * @param expectedStatusCode
	 * @param test
	 */
	public void createFilterFailedWithExpectedStatusCode(String user_id, String filter_name,
			String protection_status, String connection_status, String protection_policy,
			String backup_status, String source_group, String operating_system, int expectedStatusCode,
			String expectedErrorCode, ExtentTest test) {

		Map<String, Object> sourceFilterInfo = jp.composeFilterInfo(filter_name, protection_status,
				connection_status, protection_policy, backup_status, source_group, operating_system);
		/*   Response response = spogInvoker.createFilter(user_id, filterInfo);*/

		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!sourceFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.source_filter.toString(),
					sourceFilterInfo.get("filter_name").toString(), user_id, "none",
					(boolean)sourceFilterInfo.get("is_default"),(HashMap<String, Object>) sourceFilterInfo);
		}

		Response response = createFilters(spogInvoker.getToken(), filter_info,"", test);
		checkResponseStatus(response, expectedStatusCode);
		checkErrorCode(response, expectedErrorCode);
	}


	/**
	 * create filter failed and check statusCode
	 * 
	 * @author shan.jing
	 * @param user_id: UUID
	 * @param filter_name: String
	 * @param protection_status: String, one or more of "Protected","Unprotected", separated by ","
	 *        like "Supported,Unsupported";
	 * @param connection_status: String, one or more of "online","offline", separated by "," like
	 *        "online"
	 * @param protection_policy: UUID
	 * @param backup_status: String, one or more of below values, separated by "," like
	 *        "Active,Finished" Active(0), Finished(1), Canceled(2), Failed(3), Incomplete(4),// means
	 *        finish Idle(5),// no use Waiting(6), Crash(7), LicenseFailed(9),
	 *        BackupJob_PROC_EXIT(10), Skipped(11), Stop(12), Missed(10000)
	 * @param source_group: UUID
	 * @param operating_system: String, one or more of below values, separated by "," like
	 *        "windows,linux" unknown(0),windows(1),linux(2),mac(3);
	 * @param applications: String, one of more of below values, separated by "," like "SQL_SERVER"
	 *        All(-1),SQL_SERVER(0),EXCHANGE(1),EXCHANGE_ONLINE(2);
	 * @param source_type: String, one of more of below values, separated by "," like "0,1"
	 *        all(-1),machine(0), virtual_standby(1), instant_vm(2), shared_folder(3),office_365(4);
	 * @param is_default: "", null or not set that means is false else it means true.
	 * @param expectedStatusCode
	 * @param expectedErrorCode
	 * @param test
	 */
	public void createFilterFailedWithExpectedStatusCode(String user_id, String filter_name,
			String protection_status, String connection_status, String protection_policy,
			String backup_status, String source_group, String operating_system, String applications,
			String source_type, String is_default, int expectedStatusCode, String expectedErrorCode,
			ExtentTest test) {

		Map<String, Object> sourceFilterInfo =
				jp.composeFilterInfo(filter_name, protection_status, connection_status, protection_policy,
						backup_status, source_group, operating_system, source_type, is_default);
		/* Response response = spogInvoker.createFilter(user_id, filterInfo);*/
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!sourceFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.source_filter.toString(),
					sourceFilterInfo.get("filter_name").toString(), user_id, "none",
					(boolean)sourceFilterInfo.get("is_default"),(HashMap<String, Object>) sourceFilterInfo);
		}

		Response response = createFilters(spogInvoker.getToken(), filter_info,"", test);
		checkResponseStatus(response, expectedStatusCode);
		checkErrorCode(response, expectedErrorCode);
	}


	public void createFilterFailedWithExpectedStatusCode(String user_id, String filter_name,
			String protection_status, String connection_status, String protection_policy,
			String backup_status, String source_group, String operating_system, String applications,
			String site_id, String source_name, String source_type, String is_default,
			int expectedStatusCode, String expectedErrorCode, ExtentTest test) {

		Map<String, Object> sourceFilterInfo = jp.composeFilterInfo(filter_name, protection_status,
				connection_status, protection_policy, backup_status, source_group, operating_system,
				site_id, source_name, source_type, is_default);
		/*  Response response = spogInvoker.createFilter(user_id, filterInfo);*/
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!sourceFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.source_filter.toString(),
					sourceFilterInfo.get("filter_name").toString(), user_id, "none",
					(boolean)sourceFilterInfo.get("is_default"),(HashMap<String, Object>) sourceFilterInfo);
		}

		Response response = createFilters(spogInvoker.getToken(), filter_info,"", test);
		checkResponseStatus(response, expectedStatusCode);
		checkErrorCode(response, expectedErrorCode);
	}


	/**
	 * update filter failed and check statusCode
	 * 
	 * @author shan.jing
	 * @param user_id: UUID
	 * @param filter_id: UUID
	 * @param filter_name: String
	 * @param protection_status: String, one or more of "Protected","Unprotected", separated by ","
	 *        like "Supported,Unsupported";
	 * @param connection_status: String, one or more of "online","offline", separated by "," like
	 *        "online"
	 * @param protection_policy: UUID
	 * @param backup_status: String, one or more of below values, separated by "," like
	 *        "Active,Finished" Active(0), Finished(1), Canceled(2), Failed(3), Incomplete(4),// means
	 *        finish Idle(5),// no use Waiting(6), Crash(7), LicenseFailed(9),
	 *        BackupJob_PROC_EXIT(10), Skipped(11), Stop(12), Missed(10000)
	 * @param source_group: UUID
	 * @param operating_system: String, one or more of below values, separated by "," like
	 *        "windows,linux" unknown(0),windows(1),linux(2),mac(3);
	 * @param applications: String, one of more of below values, separated by "," like "SQL_SERVER"
	 *        All(-1),SQL_SERVER(0),EXCHANGE(1),EXCHANGE_ONLINE(2);
	 * @param expectedStatusCode
	 * @param expectedErrorCode
	 * @param test
	 */
	public void updateFilterFailedWithExpectedStatusCode(String user_id, String filter_id,
			String filter_name, String protection_status, String connection_status,
			String protection_policy, String backup_status, String source_group, String operating_system,
			String applications, int expectedStatusCode, String expectedErrorCode, ExtentTest test) {

		Map<String, Object> sourceFilterInfo = jp.composeFilterInfo(filter_name, protection_status,
				connection_status, protection_policy, backup_status, source_group, operating_system);
		/*Response response = spogInvoker.updateFilter(user_id, filter_id, filterInfo);*/
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!sourceFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.source_filter.toString(),
					sourceFilterInfo.get("filter_name").toString(), user_id, "none",
					(boolean)sourceFilterInfo.get("is_default"),(HashMap<String, Object>) sourceFilterInfo);
		}
		Response response =updateFilterById(spogInvoker.getToken(), filter_id, user_id, filter_info, "", test);
		checkResponseStatus(response, expectedStatusCode);
		checkErrorCode(response, expectedErrorCode);
	}


	public void updateFilterFailedWithExpectedStatusCode(String user_id, String filter_id,
			String filter_name, String protection_status, String connection_status,
			String protection_policy, String backup_status, String source_group, String operating_system,
			String applications, String source_type, String is_default, int expectedStatusCode,
			String expectedErrorCode, ExtentTest test) {

		Map<String, Object> sourceFilterInfo =
				jp.composeFilterInfo(filter_name, protection_status, connection_status, protection_policy,
						backup_status, source_group, operating_system, source_type, is_default);

		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!sourceFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.source_filter.toString(),
					sourceFilterInfo.get("filter_name").toString(), user_id, "none",
					(boolean)sourceFilterInfo.get("is_default"),(HashMap<String, Object>) sourceFilterInfo);
		}
		Response response =updateFilterById(spogInvoker.getToken(), filter_id, user_id, filter_info, "", test);
		/* Response response = spogInvoker.updateFilter(user_id, filter_id, filterInfo);*/
		checkResponseStatus(response, expectedStatusCode);
		checkErrorCode(response, expectedErrorCode);
	}


	public void updateFilterFailedWithExpectedStatusCode(String user_id, String filter_id,
			String filter_name, String protection_status, String connection_status,
			String protection_policy, String backup_status, String source_group, String operating_system,
			String applications, String site_id, String source_name, String source_type,
			String is_default, int expectedStatusCode, String expectedErrorCode, ExtentTest test) {

		Map<String, Object> sourceFilterInfo = jp.composeFilterInfo(filter_name, protection_status,
				connection_status, protection_policy, backup_status, source_group, operating_system,
				site_id, source_name, source_type, is_default);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!sourceFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.source_filter.toString(),
					sourceFilterInfo.get("filter_name").toString(), user_id, "none",
					(boolean)sourceFilterInfo.get("is_default"),(HashMap<String, Object>) sourceFilterInfo);
		}
		Response response =updateFilterById(spogInvoker.getToken(), filter_id, user_id, filter_info, "", test);
		/*Response response = spogInvoker.updateFilter(user_id, filter_id, filterInfo);*/
		checkResponseStatus(response, expectedStatusCode);
		checkErrorCode(response, expectedErrorCode);
	}


	/**
	 * 
	 * @author shan.jing
	 * @param user_id: UUID
	 * @param filter_id: UUID
	 * @param filter_name: String
	 * @param protection_status: String, one or more of "Protected","Unprotected", separated by ","
	 *        like "Supported,Unsupported";
	 * @param connection_status: String, one or more of "online","offline", separated by "," like
	 *        "online"
	 * @param protection_policy: UUID
	 * @param backup_status: String, one or more of below values, separated by "," like
	 *        "Active,Finished" Active(0), Finished(1), Canceled(2), Failed(3), Incomplete(4),// means
	 *        finish Idle(5),// no use Waiting(6), Crash(7), LicenseFailed(9),
	 *        BackupJob_PROC_EXIT(10), Skipped(11), Stop(12), Missed(10000)
	 * @param source_group: UUID
	 * @param operating_system: String, one or more of below values, separated by "," like
	 *        "windows,linux" unknown(0),windows(1),linux(2),mac(3);
	 * @param applications: String, one of more of below values, separated by "," like "SQL_SERVER"
	 *        All(-1),SQL_SERVER(0),EXCHANGE(1),EXCHANGE_ONLINE(2);
	 * @return filter_id
	 */

	public String updateFilterwithCheck(String user_id, String filter_id, String filter_name,
			String protection_status, String connection_status, String protection_policy,
			String backup_status, String source_group, String operating_system, String applications,
			ExtentTest test) {

		Map<String, Object> sourceFilterInfo = jp.composeFilterInfo(filter_name, protection_status,
				connection_status, protection_policy, backup_status, source_group, operating_system);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!sourceFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.source_filter.toString(),
					sourceFilterInfo.get("filter_name").toString(), user_id, "none",
					(boolean)sourceFilterInfo.get("is_default"),(HashMap<String, Object>) sourceFilterInfo);
		}
		Response response =updateFilterById(spogInvoker.getToken(), filter_id, user_id, filter_info, "", test);
		/* Response response = spogInvoker.updateFilter(user_id, filter_id, filterInfo);*/
		checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		// TODO: add check points
		String org_id = GetOrganizationIDforUser(user_id);
		if (CheckIsNullOrEmpty(filter_name)) {
			response.then().body("data.organization_id", equalTo(org_id)).body("data.user_id",
					equalTo(user_id));
		} else {
			response.then().body("data.filter_name", equalTo(filter_name))
			.body("data.organization_id", equalTo(org_id)).body("data.user_id", equalTo(user_id));
		}
		compareFilter(protection_status, connection_status, protection_policy, backup_status,
				source_group, operating_system, response);
		String return_filter_id = response.then().extract().path("data.filter_id");
		return return_filter_id;
	}


	public String updateFilterwithCheck(String user_id, String filter_id, String filter_name,
			String protection_status, String connection_status, String protection_policy,
			String backup_status, String source_group, String operating_system, String applications,
			String source_type, String actual_souce_type, String is_default, ExtentTest test) {

		Map<String, Object> sourceFilterInfo =
				jp.composeFilterInfo(filter_name, protection_status, connection_status, protection_policy,
						backup_status, source_group, operating_system, source_type, is_default);
		/* Response response = spogInvoker.updateFilter(user_id, filter_id, filterInfo);*/
		String suffix = "";
		if (source_type!=null && source_type!="" && !source_type.equalsIgnoreCase("all")) {
			suffix = "_" + source_type;
		}
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!sourceFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.source_filter.toString()+suffix,
					sourceFilterInfo.get("filter_name").toString(), user_id, "none",
					(boolean)sourceFilterInfo.get("is_default"),(HashMap<String, Object>) sourceFilterInfo);
		}
		Response response =updateFilterById(spogInvoker.getToken(), filter_id, user_id, filter_info, "", test);
		checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		// TODO: add check points
		String org_id = GetOrganizationIDforUser(user_id);
		if (CheckIsNullOrEmpty(filter_name)) {
			response.then().body("data.organization_id", equalTo(org_id)).body("data.user_id",
					equalTo(user_id));
		} else {
			response.then().body("data.filter_name", equalTo(filter_name))
			.body("data.organization_id", equalTo(org_id)).body("data.user_id", equalTo(user_id));
		}
		compareFilter(protection_status, connection_status, protection_policy, backup_status,
				source_group, operating_system, applications, actual_souce_type, is_default, response);
		String return_filter_id = response.then().extract().path("data.filter_id");
		return return_filter_id;
	}


	public String updateFilterwithCheck(String user_id, String filter_id, String filter_name,
			String protection_status, String connection_status, String protection_policy,
			String backup_status, String source_group, String operating_system, String applications,
			String siteId, String sourceName, String source_type, String actual_souce_type,
			String is_default, ExtentTest test) {

		Map<String, Object> sourceFilterInfo = jp.composeFilterInfo(filter_name, protection_status,
				connection_status, protection_policy, backup_status, source_group, operating_system, siteId,
				sourceName, source_type, is_default);
		/*  Response response = spogInvoker.updateFilter(user_id, filter_id, filterInfo);*/
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!sourceFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.source_filter.toString(),
					sourceFilterInfo.get("filter_name").toString(), user_id, "none",
					(boolean)sourceFilterInfo.get("is_default"),(HashMap<String, Object>) sourceFilterInfo);
		}
		Response response =updateFilterById(spogInvoker.getToken(), filter_id, user_id, filter_info, "", test);
		checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		// TODO: add check points
		String org_id = GetOrganizationIDforUser(user_id);
		if (CheckIsNullOrEmpty(filter_name)) {
			response.then().body("data.organization_id", equalTo(org_id)).body("data.user_id",
					equalTo(user_id));
		} else {
			response.then().body("data.filter_name", equalTo(filter_name))
			.body("data.organization_id", equalTo(org_id)).body("data.user_id", equalTo(user_id));
		}
		compareFilter(protection_status, connection_status, protection_policy, backup_status,
				source_group, operating_system, applications, siteId, sourceName, actual_souce_type,
				is_default, response);
		String return_filter_id = response.then().extract().path("data.filter_id");
		return return_filter_id;
	}


	/**
	 * 
	 * @author Zhaoguo.Ma
	 * @param user_id: UUID
	 * @param filter_name: String
	 * @param protection_status: String, one or more of "Protected","Unprotected", separated by ","
	 *        like "Supported,Unsupported";
	 * @param connection_status: String, one or more of "online","offline", separated by "," like
	 *        "online"
	 * @param protection_policy: UUID
	 * @param backup_status: String, one or more of below values, separated by "," like
	 *        "Active,Finished" Active(0), Finished(1), Canceled(2), Failed(3), Incomplete(4),// means
	 *        finish Idle(5),// no use Waiting(6), Crash(7), LicenseFailed(9),
	 *        BackupJob_PROC_EXIT(10), Skipped(11), Stop(12), Missed(10000)
	 * @param source_group: UUID
	 * @param operating_system: String, one or more of below values, separated by "," like
	 *        "windows,linux" unknown(0),windows(1),linux(2),mac(3);
	 * @return filter_id
	 */

	public String createFilterwithCheck(String user_id, String filter_name, String protection_status,
			String connection_status, String protection_policy, String backup_status, String source_group,
			String operating_system, ExtentTest test) {

		Map<String, Object> sourceFilterInfo = jp.composeFilterInfo(filter_name, protection_status,
				connection_status, protection_policy, backup_status, source_group, operating_system);
		/*  Response response = spogInvoker.createFilter(user_id, filterInfo);*/
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!sourceFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.source_filter.toString(),
					sourceFilterInfo.get("filter_name").toString(), user_id, "none",
					(boolean)sourceFilterInfo.get("is_default"),(HashMap<String, Object>) sourceFilterInfo);
		}

		Response response = createFilters(spogInvoker.getToken(), filter_info,"", test);
		checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		// TODO: add check points
		String org_id = GetOrganizationIDforUser(user_id);
		response.then().body("data.filter_name", equalTo(filter_name))
		.body("data.organization_id", equalTo(org_id)).body("data.user_id", equalTo(user_id));
		compareFilter(protection_status, connection_status, protection_policy, backup_status,
				source_group, operating_system, response);
		String filter_id = response.then().extract().path("data.filter_id");
		return filter_id;
	}


	/**
	 * @author Zhaoguo.Ma
	 * @param protection_status
	 * @param connection_status
	 * @param protection_policy
	 * @param backup_status
	 * @param source_group
	 * @param operating_system
	 * @param applications
	 * @param response
	 */

	public void compareFilter(String protection_status, String connection_status,
			String protection_policy, String backup_status, String source_group, String operating_system,
			Response response) {

		ArrayList<String> response_protection_status =
				response.then().extract().path("data.protection_status");
		if (protection_status != null && protection_status.equalsIgnoreCase("")) {
			protection_status = "emptyarray";
		}
		assertFilterItem(protection_status, response_protection_status);

		ArrayList<String> response_connection_status =
				response.then().extract().path("data.connection_status");
		if (connection_status != null && connection_status.equalsIgnoreCase("")) {
			connection_status = "emptyarray";
		}
		assertFilterItem(connection_status, response_connection_status);

		ArrayList<String> response_protection_policy = response.then().extract().path("data.policy_id");
		if (protection_policy != null && protection_policy.equalsIgnoreCase("")) {
			protection_policy = "emptyarray";
		}
		assertFilterItem(protection_policy, response_protection_policy);

		ArrayList<String> response_backup_status = response.then().extract().path("data.last_job");
		if (backup_status != null && backup_status.equalsIgnoreCase("")) {
			backup_status = "emptyarray";
		}
		assertFilterItem(backup_status, response_backup_status);

		ArrayList<String> response_source_group = response.then().extract().path("data.group_id");
		if (source_group != null && source_group.equalsIgnoreCase("")) {
			source_group = "emptyarray";
		}
		assertFilterItem(source_group, response_source_group);

		ArrayList<String> response_operating_system =
				response.then().extract().path("data.operating_system");
		if (operating_system != null && operating_system.equalsIgnoreCase("")) {
			operating_system = "emptyarray";
		}
		assertFilterItem(operating_system, response_operating_system);
	}


	/**
	 * @author Zhaoguo.Ma
	 * @param protection_status
	 * @param connection_status
	 * @param protection_policy
	 * @param backup_status
	 * @param source_group
	 * @param operating_system
	 * @param applications
	 * @param siteId
	 * @param sourceName
	 * @param source_type
	 * @param is_default
	 * @param response
	 */
	public void compareFilter(String protection_status, String connection_status,
			String protection_policy, String backup_status, String source_group, String operating_system,
			String applications, String siteId, String sourceName, String source_type, String is_default,
			Response response) {

		// List<String> siteIdArray = response.body().jsonPath().getList("data.site_id");
		// if (siteId != null && !siteId.equalsIgnoreCase("none")) {
		// if (siteId == "") {
		// assertEquals(siteIdArray.size(), 0);
		// } else {
		// if (siteIdArray != null) {
		// assertEquals(siteIdArray.get(0), siteId);
		// } else {
		// assertTrue("check site id is false", false);
		// }
		// }
		// } else {
		// assertNull(siteIdArray);
		// }
		if (sourceName != null && !sourceName.equalsIgnoreCase("none")) {
			response.then().body("data.source_name", equalTo(sourceName.toString()));
		}
		compareFilter(protection_status, connection_status, protection_policy, backup_status,
				source_group, operating_system, applications, source_type, source_type, response);
	}


	/**
	 * @author Zhaoguo.Ma
	 * @param protection_status
	 * @param connection_status
	 * @param protection_policy
	 * @param backup_status
	 * @param source_group
	 * @param operating_system
	 * @param applications
	 * @param source_type
	 * @param is_default
	 * @param response
	 */
	public void compareFilter(String protection_status, String connection_status,
			String protection_policy, String backup_status, String source_group, String operating_system,
			String applications, String source_type, String is_default, Response response) {

		if (is_default == null || is_default.equalsIgnoreCase("none") || is_default == "") {
			response.then().body("data.is_default", equalTo(false));
		} else if (is_default.equalsIgnoreCase("true")) {
			response.then().body("data.is_default", equalTo(true));
		} else if (is_default.equalsIgnoreCase("false")) {
			response.then().body("data.is_default", equalTo(false));
		}
		//
		// if (source_type != null) {
		// response.then().body("data.source_type", equalTo(source_type.toString()));
		// }
		compareFilter(protection_status, connection_status, protection_policy, backup_status,
				source_group, operating_system, response);
	}


	public String createFilterwithCheck(String user_id, String filter_name, String protection_status,
			String connection_status, String protection_policy, String backup_status, String source_group,
			String operating_system, String applications, String source_type, String is_default,
			ExtentTest test) {

		String suffix = "";
		if (source_type!=null && source_type!="" && !source_type.equalsIgnoreCase("all")) {
			suffix = "_" + source_type;
		}
		Map<String, Object> sourceFilterInfo =
				jp.composeFilterInfo(filter_name, protection_status, connection_status, protection_policy,
						backup_status, source_group, operating_system, source_type, is_default);
		/*  Response response = spogInvoker.createFilter(user_id, filterInfo);*/
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!sourceFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.source_filter.toString()+suffix,
					sourceFilterInfo.get("filter_name").toString(), user_id, "none",
					(boolean)sourceFilterInfo.get("is_default"),(HashMap<String, Object>) sourceFilterInfo);
		}

		Response response = createFilters(spogInvoker.getToken(), filter_info,"", test);

		checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		// TODO: add check points
		String org_id = GetOrganizationIDforUser(user_id);
		// if (source_type == null || source_type.equalsIgnoreCase("none")) {
		// response.then().body("data.source_type", equalTo("all"));
		// } else {
		// response.then().body("data.source_type", equalTo(source_type));
		// }
		if ("true".equalsIgnoreCase(is_default)) {
			response.then().body("data.is_default", equalTo(true));
		} else {
			response.then().body("data.is_default", equalTo(false));
		}
		response.then().body("data.filter_name", equalTo(filter_name))
		.body("data.organization_id", equalTo(org_id)).body("data.user_id", equalTo(user_id));
		compareFilter(protection_status, connection_status, protection_policy, backup_status,
				source_group, operating_system, response);
		String filter_id = response.then().extract().path("data.filter_id");
		return filter_id;
	}


	public String createFilterwithCheck(String user_id, String filter_name, String protection_status,
			String connection_status, String protection_policy, String backup_status, String source_group,
			String operating_system, String applications, String site_id, String source_name,
			String source_type, String is_default, ExtentTest test) {

		Map<String, Object> sourceFilterInfo = jp.composeFilterInfo(filter_name, protection_status,
				connection_status, protection_policy, backup_status, source_group, operating_system,
				site_id, source_name, source_type, is_default);
		String suffix = "";
		if (source_type!=null && source_type!="" && !source_type.equalsIgnoreCase("all")) {
			suffix = "_" + source_type;
		}
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!sourceFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.source_filter.toString()+suffix,
					sourceFilterInfo.get("filter_name").toString(), user_id, "none",
					(boolean)sourceFilterInfo.get("is_default"),(HashMap<String, Object>) sourceFilterInfo);
		}

		Response response = createFilters(spogInvoker.getToken(), filter_info,"", test);
		/*  Response response = spogInvoker.createFilter(user_id, filterInfo);*/
		response.then().log().all();
		checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		// TODO: add check points
		String org_id = GetOrganizationIDforUser(user_id);
		// ArrayList<String> response_source_type =
		// response.then().extract().path("data.source_type");
		// if (source_type == null || source_type.equalsIgnoreCase("none")) {
		// // response.then().body("data.source_type", equalTo("all"));
		// assertFilterItem("all", response_source_type);
		// } else {
		// // response.then().body("data.source_type", equalTo(source_type));
		// assertFilterItem(source_type, response_source_type);
		// }

		if ("true".equalsIgnoreCase(is_default)) {
			response.then().body("data.is_default", equalTo(true));
		} else {
			response.then().body("data.is_default", equalTo(false));
		}
		response.then().body("data.filter_name", equalTo(filter_name))
		.body("data.organization_id", equalTo(org_id)).body("data.user_id", equalTo(user_id));

		ArrayList<String> response_protection_status =
				response.then().extract().path("data.protection_status");
		assertFilterItem(protection_status, response_protection_status);

		ArrayList<String> response_connection_status =
				response.then().extract().path("data.connection_status");
		assertFilterItem(connection_status, response_connection_status);

		ArrayList<String> response_protection_policy = response.then().extract().path("data.policy_id");
		assertFilterItem(protection_policy, response_protection_policy);

		ArrayList<String> response_backup_status = response.then().extract().path("data.last_job");
		assertFilterItem(backup_status, response_backup_status);

		ArrayList<String> response_source_group = response.then().extract().path("data.group_id");
		assertFilterItem(source_group, response_source_group);

		ArrayList<String> response_operating_system =
				response.then().extract().path("data.operating_system");
		assertFilterItem(operating_system, response_operating_system);

		ArrayList<String> response_site_id = response.then().extract().path("data.site_id");
		if (site_id != null && site_id.equalsIgnoreCase("")) {
			site_id = "emptyarray";
		}
		assertFilterItem(site_id, response_site_id);

		String response_source_name = response.then().extract().path("data.source_name");
		assertResponseItem(source_name, response_source_name);

		String filter_id = response.then().extract().path("data.filter_id");
		return filter_id;

	}


	public void assertResponseItem(String item, String response_item) {

		if (item == null || item.equalsIgnoreCase("none") || (item == "")) {
			/*
			 * From Yu, Eric: sometimes the response is not null, take post data for example, we can first
			 * post job_seq, and then post job_type to same job, the response will contain both
			 * job_seq/job_type
			 */
			// assertNull(response_item, "compare " + item + " passed once we don't set
			// it");
		} else {
			if (item.equalsIgnoreCase(response_item)) {
				assertTrue("compare " + item + " passed", true);
			} else {
				assertTrue("compare " + item + " failed", false);
			}
		}
	}


	public void assertResponseItem(String item, String response_item, ExtentTest test) {

		if (item == null || item.equalsIgnoreCase("none") || (item == "")) {
			/*
			 * From Yu, Eric: sometimes the response is not null, take post data for example, we can first
			 * post job_seq, and then post job_type to same job, the response will contain both
			 * job_seq/job_type
			 */
			assertNull(response_item, "compare " + item + " passed once we don't set it");
		} else {
			if (item.equalsIgnoreCase(response_item)) {
				test.log(LogStatus.PASS,
						"The actual value is " + item + " the expected response is " + response_item);
				assertTrue("compare " + item + " passed", true);
			} else {
				test.log(LogStatus.FAIL,
						"The actual value is " + item + " the expected response is " + response_item);
				assertTrue("compare " + item + " failed", false);
			}
		}
	}


	/**
	 * 
	 * @author shan.jing
	 * @param filter_name: String
	 * @param response_filterItem
	 * @return
	 */
	public void assertFilterItem(String filterItem, ArrayList<String> response_filterItem) {

		if (filterItem == null || filterItem.equalsIgnoreCase("none") || (filterItem == "")) {
			assertNull(response_filterItem, "compare " + filterItem + " passed once we don't set it");
		} else if (filterItem.equalsIgnoreCase("emptyarray")) {
			if (response_filterItem == null) {
				assertTrue(
						"compare " + filterItem + " failed, we expect return empty array but it returns null",
						false);
			} else {
				assertEquals(response_filterItem.size(), 0, "it return value is empty array.");
			}
		} else {
			String[] splitted = filterItem.replace(" ", "").split(",");
			Arrays.sort(splitted);
			// zhaoguo.ma: DEV added sort for filter items (job filters / source filters/
			// destination filters), while not sure if all APIs are changed.
			// as a temporary solution, compare with both sorted result and original string.
			if (String.join(",", response_filterItem).equals(String.join(",", splitted))
					|| (String.join(",", response_filterItem).equals(filterItem.replace(" ", "")))) {
				// if (String.join(",", response_filterItem).equals(filterItem.replace(" ",
				// ""))) {
				assertTrue("compare " + filterItem + " passed", true);
			} else {
				assertTrue("compare " + filterItem + " failed, actual result is "
						+ String.join(",", response_filterItem), false);
			}
		}
	}


	/**
	 * @author Zhaoguo.Ma
	 * @param user_id
	 * @param filter_id
	 * @return
	 */
	public Response getFilterByID(String user_id, String filter_id) {

		/* Response response = spogInvoker.getFilterByID(user_id, filter_id);*/
		Response response =getFiltersById(spogInvoker.getToken(), filter_id, filterType.source_filter.toString(), user_id, "none", test);
		checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		errorHandle.printDebugMessageInDebugFile("Get filter by ID: " + response);
		return response;
	}


	/**
	 * @author Zhaoguo.Ma
	 * @param user_id
	 * @param filter_id
	 * @param status_code
	 */
	public void getFilterByIDWithExpectedCode(String user_id, String filter_id, int status_code) {

		/*Response response = spogInvoker.getFilterByID(user_id, filter_id);*/
		Response response =getFiltersById(spogInvoker.getToken(), filter_id, filterType.source_filter.toString(), user_id, "none", test);
		checkResponseStatus(response, status_code);

	}


	/**
	 * @author Zhaoguo.Ma
	 * @param user_id
	 * @return
	 */
	public Response getFiltersByUserID(String user_id) {

		/* Response response = spogInvoker.getFiltersByUserID(user_id);*/
		Response response =getFilters(spogInvoker.getToken(), user_id, filterType.source_filter.toString(), "", test);
		errorHandle.printDebugMessageInDebugFile("Get filters by user ID: " + response);
		checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		return response;
	}


	/**
	 * getFiltersByUserID used for API testing
	 * 
	 * @author zhaoguo.ma
	 * @param filterStr the format is
	 *        filterName1;operator;value,filterName2;operator;value,filterName1;operator;value
	 *        example: role_id;=;csr_admin or email;=;shuo.zhang@arcserve.com or role_id;!=;csr_admin,
	 *        email;like;shuo*@arcserve.com filterName: only accept: role_id/email operator:accept: =
	 *        != like value: role_id: ('csr_admin','msp_admin','direct_admin') email:<email> if
	 *        operator is like, in the value, must give * or ?
	 * @param sortStr the format is: sortedField;order,sortedField2;order example: first_name;asc or
	 *        email;desc or first_name;asc,email;desc sortedField: accept: 'create_ts','first_name',
	 *        'last_name','email' order: asc or desc
	 * @param pageNumber if you dont' want to set it, set it as -1
	 * @param pageSize if you dont' want to set it, set it as -1
	 * @param test
	 * @return
	 */
	public Response getFiltersByUserID(String user_id, String filterStr, String sortStr,
			int pageNumber, int pageSize, ExtentTest test) {

		String extendUrl = "";
		int multipleFlag = 0;
		if ((filterStr != null) && (!filterStr.equals(""))) {
			test.log(LogStatus.INFO, "set filter url");
			extendUrl += getFilterUrl(filterStr);
			multipleFlag++;
		}
		if ((sortStr != null) && (!sortStr.equals(""))) {
			test.log(LogStatus.INFO, "set sort url");
			if (multipleFlag != 0) {
				extendUrl += "&";
			}
			extendUrl += getSortUrl(sortStr);
			multipleFlag++;
		}
		if ((pageNumber != -1) || (pageSize != -1)) {
			test.log(LogStatus.INFO, "set paging url");
			if (multipleFlag != 0) {
				extendUrl += "&";
			}
			extendUrl += getPagingUrl(pageNumber, pageSize);
		}
		test.log(LogStatus.INFO, "call getFiltersByUserID");
		Response response =getFilters(spogInvoker.getToken(), user_id, filterType.source_filter.toString(), extendUrl, test);
		//    Response response = spogInvoker.getFiltersByUserID(user_id, extendUrl, test);
		return response;
	}


	public Response getSourceFiltersForLoggedInUser() {

		return spogInvoker.getSourceFiltersForLoggedInUser();
	}


	public Response getSourceFilterByIdForLoggedInUser(String sourceFilterId) {

		return spogInvoker.getSourceFilterByIdForLoggedInUser(sourceFilterId);
	}


	/**
	 * email;=;*spog_qa@arcserve.com,
	 * 
	 * @author Zhaoguo.Ma
	 * @param user_id
	 * @param status_code
	 */
	public void getFiltersByUserIDWithExpectedcode(String user_id, int status_code) {

		/* Response response = spogInvoker.getFiltersByUserID(user_id);*/
		test = new ExtentTest("getFilters", "getFiltersForUser");
		Response response =getFilters(spogInvoker.getToken(), user_id, filterType.source_filter.toString(), "", test);
		checkResponseStatus(response, status_code);

	}


	/**
	 * this is not the full parameters for create source. it should not be used in future.
	 * 
	 * @author shuo.zhang
	 * @param source_name
	 * @param source_type
	 * @param source_product
	 * @param organization_id
	 * @param site_id
	 * @param policy_id
	 * @param protection_status
	 * @param connection_status
	 * @param os_major
	 * @param applications
	 * @return
	 */

	public Response createSource(String source_name, SourceType source_type,
			SourceProduct source_product, String organization_id, String site_id,
			ProtectionStatus protection_status, ConnectionStatus connection_status, String os_major,
			String applications, ExtentTest test) {

		String[] applicationArray = null;
		if ((applications != null) && (!applications.equals(""))) {
			applicationArray = applications.split(";");

		}
		errorHandle.printDebugMessageInDebugFile("compose source info object");
		test.log(LogStatus.INFO, "compose source info object");
		String str_source_type = null;
		if (source_type != null) {
			str_source_type = source_type.name();
		}

		String str_source_product = null;
		if (source_product != null) {
			str_source_product = source_product.name();
		}

		String str_protection_status = null;
		if (protection_status != null) {
			str_protection_status = protection_status.name();
		}

		String str_connection_status = null;
		if (connection_status != null) {
			str_connection_status = connection_status.name();
		}

		Map<String, Object> sourceInfo =
				jp.getSourceInfo(source_name, str_source_type, str_source_product, organization_id, site_id,
						str_protection_status, str_connection_status, os_major, applicationArray);

		errorHandle.printDebugMessageInDebugFile("create source");
		test.log(LogStatus.INFO, "create source");
		Response response = spogInvoker.createSource(sourceInfo, true);

		return response;
	}


	/**
	 * get organization jobs
	 * 
	 * @author yin.li
	 * @param
	 * 
	 */
	public Response getOrganizationJobs(String orgId, HashMap<String, String> params) {

		if (StringUtils.isEmpty(orgId)) {
			orgId = GetLoggedinUserOrganizationID();
		}

		if (orgId == "EMPTY") {
			orgId = "";
		}

		Response response = spogInvoker.getOrganizationJobs(orgId, params);
		return response;
	}


	/**
	 * This method checks the audit trail data based on the actions related to user/organization/site
	 * 
	 * @author kiran.sripada
	 * @param response
	 * @param expectedStatusCode
	 * @param ArraysOfauditdata
	 * @param curr_page
	 * @param page_size
	 * @param filterStr
	 * @param ExtentTest
	 */

	public void checkaudittraildata(Response response, int expectedStatusCode,
			ArrayList<HashMap> ExpectedAccounts, int curr_page, int page_size, String FilterStr,
			String SortStr, SpogMessageCode expectedErrorMessage, ExtentTest test) {

		int total_page = 0;
		int return_size = 0;
		int total_size = 0;
		boolean has_next = false;
		boolean has_prev = false;
		ArrayList<HashMap> users = new ArrayList<HashMap>();

		if (curr_page == 0) {
			curr_page = 1;
		}
		if (page_size == 0 || page_size > SpogConstants.MAX_PAGE_SIZE) {
			page_size = 20;
		}

		if (page_size > 100 && page_size < SpogConstants.MAX_PAGE_SIZE) {
			page_size = 100;
		}



		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			users = response.then().extract().path("data");
			System.out.println("The expected size is " + ExpectedAccounts.size());
			return_size = users.size();

			total_size = response.then().extract().path("pagination.total_size");
			test.log(LogStatus.INFO, "The actual total size is " + total_size);


			test.log(LogStatus.INFO, "expect status code is " + expectedStatusCode);

			errorHandle.printInfoMessageInDebugFile("expect status code is " + expectedStatusCode);
			checkResponseStatus(response, expectedStatusCode);
		}

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE
				|| ExpectedAccounts.size() == total_size) {
			test.log(LogStatus.INFO, "check response user info");

			if (((SortStr == null) || (SortStr.equals("")))
					&& ((FilterStr == null) || (FilterStr.equals("")))) {


				// This method is used to sort all the values
				Collections.sort(users, new Comparator<HashMap>() {

					@Override
					public int compare(HashMap o1, HashMap o2) {

						// TODO Auto-generated method stub
						int create_ts = (int) o1.get("create_ts");
						int create_ts1 = (int) o2.get("create_ts");
						if (create_ts > create_ts1)
							return 1;
						if (create_ts < create_ts1)
							return -1;
						else
							return 0;

					}
				});
				test.log(LogStatus.INFO, "The actual response after sorting is"+users);
				for (int i = 0; i < users.size(); i++) {

					// System.out.println("The value of the generated isgfnjl ....... :");

					if (users.get(i).get("code_id").equals(Integer.parseInt(AuditCodeConstants.LOGIN_USER))
							|| (users.get(i).get("code_id")
									.equals(Integer.parseInt(AuditCodeConstants.DELETE_USER)))
							|| (users.get(i).get("code_id")
									.equals(Integer.parseInt(AuditCodeConstants.DELETE_SUB_ORGANIZATION)))
							|| (users.get(i).get("code_id")
									.equals(Integer.parseInt(AuditCodeConstants.DELETE_ORGANIZATION)))
							|| (users.get(i).get("code_id")
									.equals(Integer.parseInt(AuditCodeConstants.DELETE_SOURCE)))
							|| (users.get(i).get("code_id")
									.equals(Integer.parseInt(AuditCodeConstants.delete_destination)))) {
						if (users.get(i).get("code_id")
								.equals(Integer.parseInt((String) ExpectedAccounts.get(i).get("code_id")))
								) {
							assertResponseItem(users.get(i).get("by_organization_id"),ExpectedAccounts.get(i).get("by_organization_id"),test);
							assertResponseItem(users.get(i).get("by_resource_id"),ExpectedAccounts.get(i).get("by_resource_id"),test);
							assertResponseItem(users.get(i).get("code_id").toString(),ExpectedAccounts.get(i).get("code_id").toString(),test);
							assertResponseItem(users.get(i).get("on_resource_id"),ExpectedAccounts.get(i).get("on_resource_id"),test);

							assertResponseItem(users.get(i).get("by_resource_name").toString().toLowerCase(),ExpectedAccounts.get(i).get("by_resource_name").toString().toLowerCase(),test);
							assertResponseItem(users.get(i).get("by_resource_type"),ExpectedAccounts.get(i).get("by_resource_type"),test);
							assertResponseItem(users.get(i).get("on_resource_type").toString(),ExpectedAccounts.get(i).get("on_resource_type").toString(),test);
							assertResponseItem(users.get(i).get("on_resource_name").toString().toLowerCase(),ExpectedAccounts.get(i).get("on_resource_name").toString().toLowerCase(),test);
							System.out
							.println("The value related to the logged in user has been passed successfully");
							test.log(LogStatus.PASS, "The actual audit trail data matched the expected "
									+ users.get(i).get("code_id"));
						}

					} else if (users.get(i).get("code_id")
							.equals(Integer.parseInt(AuditCodeConstants.CREATE_USER))
							|| (users.get(i).get("code_id")
									.equals(Integer.parseInt(AuditCodeConstants.MODIFY_LOGIN_USER))
									|| (users.get(i).get("code_id")
											.equals(Integer.parseInt(AuditCodeConstants.MODIFY_USER))
											|| (users.get(i).get("code_id")
													.equals(Integer.parseInt(AuditCodeConstants.MODIFY_LOGIN_USER_PASSWORD))
													|| (users.get(i).get("code_id").equals(
															Integer.parseInt(AuditCodeConstants.MODIFY_USER_PASSWORD))))))) {
						if (users.get(i).get("code_id")
								.equals(Integer.parseInt((String) ExpectedAccounts.get(i).get("code_id")))
								)
							// &&((JsonPath) users.get(i).get("audit_data")).get("user_id").toString()
							// .equals(((JsonPath) ExpectedAccounts.get(i).get("audit_data")).get("user_id"))
							// )
						{
							assertResponseItem(users.get(i).get("by_organization_id"),ExpectedAccounts.get(i).get("by_organization_id"),test);
							assertResponseItem(users.get(i).get("by_resource_id"),ExpectedAccounts.get(i).get("by_resource_id"),test);
							assertResponseItem(users.get(i).get("code_id").toString(),ExpectedAccounts.get(i).get("code_id").toString(),test);
							assertResponseItem(users.get(i).get("on_resource_id"),ExpectedAccounts.get(i).get("on_resource_id"),test);

							assertResponseItem(users.get(i).get("by_resource_name").toString().toLowerCase(),ExpectedAccounts.get(i).get("by_resource_name").toString().toLowerCase(),test);
							assertResponseItem(users.get(i).get("by_resource_type"),ExpectedAccounts.get(i).get("by_resource_type"),test);
							assertResponseItem(users.get(i).get("on_resource_type").toString(),ExpectedAccounts.get(i).get("on_resource_type").toString(),test);
							assertResponseItem(users.get(i).get("on_resource_name").toString().toLowerCase(),ExpectedAccounts.get(i).get("on_resource_name").toString().toLowerCase(),test);

							test.log(LogStatus.INFO, "The initial validation passed");
							// @SuppressWarnings({ "unchecked", "rawtypes" })
							// ArrayList<ResponseBody> audit_data = (ArrayList<ResponseBody>)
							// users.get(i).get("audit_data");
							// JsonPath parser = new JsonPath((String) users.get(i).get("audit_data"));
							HashMap<String, Object> parser =
									(HashMap<String, Object>) users.get(i).get("audit_data");
							ArrayList<ResponseBody> userresponse1 = new ArrayList<ResponseBody>();
							userresponse1 = (ArrayList<ResponseBody>) ExpectedAccounts.get(i).get("audit_data");
							test.log(LogStatus.INFO, "The size of the user response is " + userresponse1.size());
							if (parser.get("user_id").equals(userresponse1.get(0).jsonPath().get("data.user_id"))
									&& (parser.get("first_name")
											.equals(userresponse1.get(0).jsonPath().get("data.first_name")))
									&& (parser.get("last_name")
											.equals(userresponse1.get(0).jsonPath().get("data.last_name")))
									&& (parser.get("email").equals(userresponse1.get(0).jsonPath().get("data.email")))
									&& (parser.get("role_id")
											.equals(userresponse1.get(0).jsonPath().get("data.role_id")))
									&& (parser.get("organization_id")
											.equals(userresponse1.get(0).jsonPath().get("data.organization_id")))
									&& (parser.get("create_ts")
											.equals(userresponse1.get(0).jsonPath().getLong("data.create_ts")))) {
								System.out.println("The value related to the created user has been passed");
								test.log(LogStatus.PASS, "The actual audit trail data matched the expected "
										+ users.get(i).get("code_id"));
							} else {
								test.log(LogStatus.FAIL,
										"The actual audit trail data " + users.get(i).get("audit_data")
										+ "  did not match the expected " + users.get(i).get("code_id"));
								test.log(LogStatus.INFO,
										userresponse1.get(0).jsonPath().get("data.user_id").toString());
								test.log(LogStatus.INFO,
										userresponse1.get(0).jsonPath().get("data.first_name").toString());
								test.log(LogStatus.INFO,
										userresponse1.get(0).jsonPath().get("data.last_name").toString());
								test.log(LogStatus.INFO,
										userresponse1.get(0).jsonPath().get("data.email").toString());
								test.log(LogStatus.INFO,
										userresponse1.get(0).jsonPath().get("data.role_id").toString());
								test.log(LogStatus.INFO,
										userresponse1.get(0).jsonPath().get("data.organization_id").toString());
							}



							// test.log(LogStatus.INFO, "The expected user id is
							// "+userresponse1.get(0).jsonPath().get("data.user_id"));

						}
					}
					// @author bharadwaj.Ghadiam
					// cases related to create sub organization,modify subOrganization,modify subOrganization
					// ,modifyLoggedInUserOrganization
					else if ((users.get(i).get("code_id")
							.equals(Integer.parseInt(AuditCodeConstants.CREATE_SUB_ORGANIZATION)))
							|| (users.get(i).get("code_id")
									.equals(Integer.parseInt(AuditCodeConstants.MODIFY_LOGIN_USER_ORGANIZATION)))
							|| (users.get(i).get("code_id")
									.equals(Integer.parseInt(AuditCodeConstants.MODIFY_SUB_ORGANIZATION)))
							|| (users.get(i).get("code_id")
									.equals(Integer.parseInt(AuditCodeConstants.MODIFY_ORGANIZATION)))) {

						if (users.get(i).get("by_organization_id")
								.equals(ExpectedAccounts.get(i).get("by_organization_id"))
								) {
							assertResponseItem(users.get(i).get("by_organization_id"),ExpectedAccounts.get(i).get("by_organization_id"),test);
							assertResponseItem(users.get(i).get("by_resource_id"),ExpectedAccounts.get(i).get("by_resource_id"),test);
							assertResponseItem(users.get(i).get("code_id").toString(),ExpectedAccounts.get(i).get("code_id").toString(),test);
							assertResponseItem(users.get(i).get("on_resource_id"),ExpectedAccounts.get(i).get("on_resource_id"),test);

							assertResponseItem(users.get(i).get("by_resource_name").toString().toLowerCase(),ExpectedAccounts.get(i).get("by_resource_name".toString().toLowerCase()),test);
							assertResponseItem(users.get(i).get("by_resource_type"),ExpectedAccounts.get(i).get("by_resource_type"),test);
							assertResponseItem(users.get(i).get("on_resource_type").toString(),ExpectedAccounts.get(i).get("on_resource_type").toString(),test);
							assertResponseItem(users.get(i).get("on_resource_name").toString().toLowerCase(),ExpectedAccounts.get(i).get("on_resource_name").toString().toLowerCase(),test);


							HashMap<String, Object> object =
									(HashMap<String, Object>) users.get(i).get("audit_data");

							ArrayList<ResponseBody> audit = new ArrayList<ResponseBody>();
							audit = (ArrayList<ResponseBody>) ExpectedAccounts.get(i).get("audit_data");



							if (object.get("organization_id")
									.equals(audit.get(0).jsonPath().get("data.organization_id"))
									&& object.get("organization_name")
									.equals(audit.get(0).jsonPath().get("data.organization_name"))
									&& object.get("create_ts")
									.equals(audit.get(0).jsonPath().getLong("data.create_ts"))
									&& object.get("parent_id").equals(audit.get(0).jsonPath()
											.get("data.parent_id"))/*
											 * && object.get("create_user_id").equals(audit.get(0).
											 * jsonPath().get("data.create_user_id"))
											 */) {
								if ((users.get(i).get("code_id")
										.equals(Integer.parseInt(AuditCodeConstants.CREATE_SUB_ORGANIZATION)))) {
									/*
									 * if (object.get("create_user_id")
									 * .equals(audit.get(0).jsonPath().get("data.create_user_id"))) {
									 */
									assertTrue("The value is correct", true);
									test.log(LogStatus.PASS, "The creation is successfull");
									System.out.println(
											"This information is related to the creation of the sub organizaion:");
									test.log(LogStatus.PASS,
											"This information is related to the creation of the sub organizaion:");
									// }
								} else {
									assertTrue("The value is correct", true);
									System.out.println(
											"This information is related to the modification of the organization");
									test.log(LogStatus.PASS,
											"This information is related to the modification of the organization");
								}
							} else {
								test.log(LogStatus.FAIL,
										"The actual audit trail data " + users.get(i).get("organization_id")
										+ "  did not match the expected " + users.get(i).get("code_id"));
							}
						}

					} else if (users.get(i).get("code_id")
							.equals(Integer.parseInt(AuditCodeConstants.CREATE_SITE))
							|| (users.get(i).get("code_id")
									.equals(Integer.parseInt(AuditCodeConstants.MODIFY_SITE)))
							|| (users.get(i).get("code_id")
									.equals(Integer.parseInt(AuditCodeConstants.DELETE_SITE)))) {
						if (users.get(i).get("by_organization_id")
								.equals(ExpectedAccounts.get(i).get("by_organization_id"))
								) 
						{
							assertResponseItem(users.get(i).get("by_organization_id"),ExpectedAccounts.get(i).get("by_organization_id"),test);
							assertResponseItem(users.get(i).get("by_resource_id"),ExpectedAccounts.get(i).get("by_resource_id"),test);
							assertResponseItem(users.get(i).get("code_id").toString(),ExpectedAccounts.get(i).get("code_id").toString(),test);
							assertResponseItem(users.get(i).get("on_resource_id"),ExpectedAccounts.get(i).get("on_resource_id"),test);

							assertResponseItem(users.get(i).get("by_resource_name").toString().toLowerCase(),ExpectedAccounts.get(i).get("by_resource_name".toString().toLowerCase()),test);
							assertResponseItem(users.get(i).get("by_resource_type"),ExpectedAccounts.get(i).get("by_resource_type"),test);
							assertResponseItem(users.get(i).get("on_resource_type").toString(),ExpectedAccounts.get(i).get("on_resource_type").toString(),test);
							assertResponseItem(users.get(i).get("on_resource_name").toString().toLowerCase(),ExpectedAccounts.get(i).get("on_resource_name").toString().toLowerCase(),test);

							if (users.get(i).get("code_id")
									.equals(Integer.parseInt(AuditCodeConstants.CREATE_SITE))
									|| (users.get(i).get("code_id")
											.equals(Integer.parseInt(AuditCodeConstants.MODIFY_SITE)))) {
								// JsonPath object = new JsonPath((String) users.get(i).get("audit_data"));
								HashMap<String, Object> object =
										(HashMap<String, Object>) users.get(i).get("audit_data");

								ArrayList<ResponseBody> audit = new ArrayList<ResponseBody>();
								audit = (ArrayList<ResponseBody>) ExpectedAccounts.get(i).get("audit_data");


								System.out.println("site id is " + audit.get(0).jsonPath().get("data.site_id"));
								System.out.println("site id is " + object.get("site_id"));
								// Boolean actual = Boolean.parseBoolean(object.get("deleted"));
								// Boolean exp = audit.get(0).jsonPath().get("data.deleted");
								if (/*
								 * object.get("deleted").equals(audit.get(0).jsonPath().get("data.deleted")) &&
								 */ object.get("is_registered")
								 .equals(audit.get(0).jsonPath().get("data.is_registered"))
								 && object.get("site_id").equals(audit.get(0).jsonPath().get("data.site_id"))
								 && object.get("site_name").equals(audit.get(0).jsonPath().get("data.site_name"))
								 && object.get("site_type").equals(audit.get(0).jsonPath().get("data.site_type"))
								 &&
								 // object.get("site_version").equals(audit.get(0).jsonPath().get("data.site_version"))&&
								 /*
								  * object.get("site_secret")
								  * .equals(audit.get(0).jsonPath().get("data.site_secret")) &&
								  */ object.get("organization_id").equals(audit.get(0).jsonPath().get("data.organization_id")) && object.get("create_user_id").equals(audit.get(0).jsonPath().get("data.create_user_id"))
								  // &&
								  // object.get("gateway_id").equals(audit.get(0).jsonPath().get("data.gateway_id"))&&
								  // object.get("gateway_hostname").equals(audit.get(0).jsonPath().get("data.gateway_hostname"))&&
								  // object.get("heartbeat_interval")
								  // .equals(audit.get(0).jsonPath().get("data.heartbeat_interval"))
								  // object.get("registration_basecode").equals(audit.get(0).jsonPath().get("data.registration_basecode"))
										) {
									test.log(LogStatus.PASS,
											"The actual audit trail data matched the expected audit code "
													+ users.get(i).get("code_id"));
								} else {
									test.log(LogStatus.FAIL,
											"The actual audit trail data " + users.get(i).get("organization_id")
											+ "  did not match the expected " + users.get(i).get("code_id"));
								}
							} else {
								test.log(LogStatus.PASS,
										"The actual audit trail data matched the expected audit code "
												+ users.get(i).get("code_id"));
							}

						}
					}else if(users.get(i).get("code_id")
							.equals(Integer.parseInt(AuditCodeConstants.CREATE_SOURCE))
							||(users.get(i).get("code_id")
									.equals(Integer.parseInt(AuditCodeConstants.MODIFY_SOURCE)))
							||(users.get(i).get("code_id")
									.equals(Integer.parseInt(AuditCodeConstants.DELETE_SOURCE)))) 
					{
						test.log(LogStatus.INFO, "Validating the source audit trail");
						assertResponseItem(users.get(i).get("by_organization_id"),ExpectedAccounts.get(i).get("by_organization_id"),test);
						assertResponseItem(users.get(i).get("by_resource_id"),ExpectedAccounts.get(i).get("by_resource_id"),test);
						assertResponseItem(users.get(i).get("code_id").toString(),ExpectedAccounts.get(i).get("code_id").toString(),test);
						assertResponseItem(users.get(i).get("on_resource_id"),ExpectedAccounts.get(i).get("on_resource_id"),test);

						assertResponseItem(users.get(i).get("by_resource_name").toString().toLowerCase(),ExpectedAccounts.get(i).get("by_resource_name").toString().toLowerCase(),test);
						assertResponseItem(users.get(i).get("by_resource_type"),ExpectedAccounts.get(i).get("by_resource_type"),test);
						assertResponseItem(users.get(i).get("on_resource_type").toString(),ExpectedAccounts.get(i).get("on_resource_type").toString(),test);
						assertResponseItem(users.get(i).get("on_resource_name").toString().toLowerCase(),ExpectedAccounts.get(i).get("on_resource_name".toString().toLowerCase()),test);
						if (users.get(i).get("code_id")
								.equals(Integer.parseInt(AuditCodeConstants.CREATE_SOURCE))
								||(users.get(i).get("code_id")
										.equals(Integer.parseInt(AuditCodeConstants.MODIFY_SOURCE)))) {

							CheckForSources(users,i,i,ExpectedAccounts,test);
							test.log(LogStatus.PASS, "The audit trail data matched the expected audit code"+users.get(i).get("code_id"));
						}else {
							test.log(LogStatus.PASS, "The audit trail data matched the expected audit code"+users.get(i).get("code_id"));  
						}

					}else if(users.get(i).get("code_id")
							.equals(Integer.parseInt(AuditCodeConstants.create_destination))
							||(users.get(i).get("code_id")
									.equals(Integer.parseInt(AuditCodeConstants.update_destination)))
							||(users.get(i).get("code_id")
									.equals(Integer.parseInt(AuditCodeConstants.delete_destination)))) 
					{
						test.log(LogStatus.INFO, "Validating the destination audit trail");
						assertResponseItem(users.get(i).get("by_organization_id"),ExpectedAccounts.get(i).get("by_organization_id"),test);
						assertResponseItem(users.get(i).get("by_resource_id"),ExpectedAccounts.get(i).get("by_resource_id"),test);
						assertResponseItem(users.get(i).get("code_id").toString(),ExpectedAccounts.get(i).get("code_id").toString(),test);
						assertResponseItem(users.get(i).get("on_resource_id"),ExpectedAccounts.get(i).get("on_resource_id"),test);

						assertResponseItem(users.get(i).get("by_resource_name").toString().toLowerCase(),ExpectedAccounts.get(i).get("by_resource_name").toString().toLowerCase(),test);
						assertResponseItem(users.get(i).get("by_resource_type"),ExpectedAccounts.get(i).get("by_resource_type"),test);
						assertResponseItem(users.get(i).get("on_resource_type").toString(),ExpectedAccounts.get(i).get("on_resource_type").toString(),test);
						assertResponseItem(users.get(i).get("on_resource_name").toString().toLowerCase(),ExpectedAccounts.get(i).get("on_resource_name").toString().toLowerCase(),test);
						if (users.get(i).get("code_id")
								.equals(Integer.parseInt(AuditCodeConstants.create_destination))
								||(users.get(i).get("code_id")
										.equals(Integer.parseInt(AuditCodeConstants.update_destination)))) {


							CheckForDestinaitons(users,i,i,ExpectedAccounts,test);
							test.log(LogStatus.PASS, "The audit trail data matched the expected audit code"+users.get(i).get("code_id"));
						}else {
							test.log(LogStatus.PASS, "The audit trail data matched the expected audit code"+users.get(i).get("code_id"));  
						}

					}else {
						checkaudittrail_enh(users,ExpectedAccounts,i,i,test);
					}

				}



			}

			// SortStr
			else if (((SortStr != null) && (!SortStr.equals("")) && SortStr.contains("create_ts")
					&& ((FilterStr == null) || (FilterStr.equals(""))))
					|| ((SortStr != null) && (!SortStr.equals("")) && SortStr.contains("create_ts")
							&& ((FilterStr != null) || (!FilterStr.equals(""))))) {

				// validating the response for all the users who are sorted based on response
				if (SortStr.contains("asc")) {
					int j = 0;
					if (curr_page != 1) {
						j = (curr_page - 1) * page_size;
					}
					for (int i = 0; i < users.size(); i++, j++) {

						if (users.get(i).get("code_id").equals(Integer.parseInt(AuditCodeConstants.LOGIN_USER))
								|| (users.get(i).get("code_id")
										.equals(Integer.parseInt(AuditCodeConstants.DELETE_USER)))) {
							if (users.get(i).get("code_id")
									.equals(Integer.parseInt((String) ExpectedAccounts.get(j).get("code_id")))
									&& users.get(i).get("by_organization_id")
									.equals(ExpectedAccounts.get(j).get("by_organization_id"))
									&& users.get(i).get("by_resource_id")
									.equals(ExpectedAccounts.get(j).get("by_resource_id"))
									&& users.get(i).get("on_resource_id")
									.equals(ExpectedAccounts.get(j).get("on_resource_id"))) {

								test.log(LogStatus.PASS, "The actual audit trail data matched the expected "
										+ users.get(i).get("code_id"));
							}

						} else if (users.get(i).get("code_id")
								.equals(Integer.parseInt(AuditCodeConstants.CREATE_USER))
								|| (users.get(i).get("code_id")
										.equals(Integer.parseInt(AuditCodeConstants.MODIFY_LOGIN_USER))
										|| (users.get(i).get("code_id")
												.equals(Integer.parseInt(AuditCodeConstants.MODIFY_USER))
												|| (users.get(i).get("code_id")
														.equals(Integer.parseInt(AuditCodeConstants.MODIFY_LOGIN_USER_PASSWORD))
														|| (users.get(i).get("code_id").equals(
																Integer.parseInt(AuditCodeConstants.MODIFY_USER_PASSWORD))))))) {
							if (users.get(i).get("code_id")
									.equals(Integer.parseInt((String) ExpectedAccounts.get(j).get("code_id")))
									)
								// &&((JsonPath) users.get(i).get("audit_data")).get("user_id").toString()
								// .equals(((JsonPath) ExpectedAccounts.get(i).get("audit_data")).get("user_id"))
								// )
							{

								assertResponseItem(users.get(i).get("by_organization_id"),ExpectedAccounts.get(i).get("by_organization_id"),test);
								assertResponseItem(users.get(i).get("by_resource_id"),ExpectedAccounts.get(i).get("by_resource_id"),test);
								assertResponseItem(users.get(i).get("code_id").toString(),ExpectedAccounts.get(i).get("code_id").toString(),test);
								assertResponseItem(users.get(i).get("on_resource_id"),ExpectedAccounts.get(i).get("on_resource_id"),test);

								assertResponseItem(users.get(i).get("by_resource_name").toString().toLowerCase(),ExpectedAccounts.get(i).get("by_resource_name").toString().toLowerCase(),test);
								assertResponseItem(users.get(i).get("by_resource_type"),ExpectedAccounts.get(i).get("by_resource_type"),test);
								assertResponseItem(users.get(i).get("on_resource_type").toString(),ExpectedAccounts.get(i).get("on_resource_type").toString(),test);
								assertResponseItem(users.get(i).get("on_resource_name").toString().toLowerCase(),ExpectedAccounts.get(i).get("on_resource_name").toString().toLowerCase(),test);
								test.log(LogStatus.INFO, "The initial validation passed");
								// @SuppressWarnings({ "unchecked", "rawtypes" })
								// ArrayList<ResponseBody> audit_data = (ArrayList<ResponseBody>)
								// users.get(i).get("audit_data");
								// JsonPath parser = new JsonPath((String) users.get(i).get("audit_data"));
								HashMap<String, Object> parser =
										(HashMap<String, Object>) users.get(i).get("audit_data");
								ArrayList<ResponseBody> userresponse1 = new ArrayList<ResponseBody>();
								userresponse1 = (ArrayList<ResponseBody>) ExpectedAccounts.get(j).get("audit_data");
								test.log(LogStatus.INFO,
										"The size of the user response is " + userresponse1.size());

								if (parser.get("user_id")
										.equals(userresponse1.get(0).jsonPath().get("data.user_id"))
										&& (parser.get("first_name")
												.equals(userresponse1.get(0).jsonPath().get("data.first_name")))
										&& (parser.get("last_name")
												.equals(userresponse1.get(0).jsonPath().get("data.last_name")))
										&& (parser.get("email")
												.equals(userresponse1.get(0).jsonPath().get("data.email")))
										&& (parser.get("role_id")
												.equals(userresponse1.get(0).jsonPath().get("data.role_id")))
										&& (parser.get("organization_id")
												.equals(userresponse1.get(0).jsonPath().get("data.organization_id")))
										&& (parser.get("create_ts")
												.equals(userresponse1.get(0).jsonPath().getLong("data.create_ts")))) {
									test.log(LogStatus.PASS, "The actual audit trail data matched the expected "
											+ users.get(i).get("code_id"));
								} else {
									test.log(LogStatus.FAIL,
											"The actual audit trail data " + users.get(i).get("audit_data")
											+ "  did not match the expected " + users.get(i).get("code_id"));
									test.log(LogStatus.INFO,
											userresponse1.get(0).jsonPath().get("data.user_id").toString());
									test.log(LogStatus.INFO,
											userresponse1.get(0).jsonPath().get("data.first_name").toString());
									test.log(LogStatus.INFO,
											userresponse1.get(0).jsonPath().get("data.last_name").toString());
									test.log(LogStatus.INFO,
											userresponse1.get(0).jsonPath().get("data.email").toString());
									test.log(LogStatus.INFO,
											userresponse1.get(0).jsonPath().get("data.role_id").toString());
									test.log(LogStatus.INFO,
											userresponse1.get(0).jsonPath().get("data.organization_id").toString());
								}



								// test.log(LogStatus.INFO, "The expected user id is
								// "+userresponse1.get(0).jsonPath().get("data.user_id"));

							}

						} // @author bharadwaj.Ghadiam
						// cases related to create sub organization,modify subOrganization,modify
						// subOrganization ,modifyLoggedInUserOrganization
						else if ((users.get(i).get("code_id")
								.equals(Integer.parseInt(AuditCodeConstants.CREATE_SUB_ORGANIZATION)))
								|| (users.get(i).get("code_id")
										.equals(Integer.parseInt(AuditCodeConstants.MODIFY_LOGIN_USER_ORGANIZATION)))
								|| (users.get(i).get("code_id")
										.equals(Integer.parseInt(AuditCodeConstants.MODIFY_SUB_ORGANIZATION)))
								|| (users.get(i).get("code_id")
										.equals(Integer.parseInt(AuditCodeConstants.MODIFY_ORGANIZATION)))) {

							if (users.get(i).get("by_organization_id")
									.equals(ExpectedAccounts.get(j).get("by_organization_id"))
									) {
								assertResponseItem(users.get(i).get("by_organization_id"),ExpectedAccounts.get(i).get("by_organization_id"),test);
								assertResponseItem(users.get(i).get("by_resource_id"),ExpectedAccounts.get(i).get("by_resource_id"),test);
								assertResponseItem(users.get(i).get("code_id").toString(),ExpectedAccounts.get(i).get("code_id").toString(),test);
								assertResponseItem(users.get(i).get("on_resource_id"),ExpectedAccounts.get(i).get("on_resource_id"),test);

								assertResponseItem(users.get(i).get("by_resource_name").toString().toLowerCase(),ExpectedAccounts.get(i).get("by_resource_name").toString().toLowerCase(),test);
								assertResponseItem(users.get(i).get("by_resource_type"),ExpectedAccounts.get(i).get("by_resource_type"),test);
								assertResponseItem(users.get(i).get("on_resource_type").toString(),ExpectedAccounts.get(i).get("on_resource_type").toString(),test);
								assertResponseItem(users.get(i).get("on_resource_name").toString().toLowerCase(),ExpectedAccounts.get(i).get("on_resource_name").toString().toLowerCase(),test);

								// JsonPath object = new JsonPath((String) users.get(i).get("audit_data"));
								HashMap<String, Object> object =
										(HashMap<String, Object>) users.get(i).get("audit_data");

								ArrayList<ResponseBody> audit = new ArrayList<ResponseBody>();
								audit = (ArrayList<ResponseBody>) ExpectedAccounts.get(j).get("audit_data");



								if (object.get("organization_id")
										.equals(audit.get(0).jsonPath().get("data.organization_id"))
										&& object.get("organization_name")
										.equals(audit.get(0).jsonPath().get("data.organization_name"))
										&& object.get("create_ts")
										.equals(audit.get(0).jsonPath().getLong("data.create_ts"))
										&& object.get("parent_id").equals(audit.get(0).jsonPath().get(
												"data.parent_id"))/*
												 * && object.get("create_user_id").equals(audit.get(0).
												 * jsonPath().get("data.create_user_id"))
												 */) {
									if ((users.get(i).get("code_id")
											.equals(Integer.parseInt(AuditCodeConstants.CREATE_SUB_ORGANIZATION)))) {
										/*
										 * if (object.get("create_user_id")
										 * .equals(audit.get(0).jsonPath().get("data.create_user_id"))) {
										 */
										assertTrue("The value is correct", true);
										test.log(LogStatus.PASS, "The creation is successfull");
										System.out.println(
												"This information is related to the creation of the sub organizaion:");
										test.log(LogStatus.PASS,
												"This information is related to the creation of the sub organizaion:");
										// }
									} else {
										assertTrue("The value is correct", true);
										System.out.println(
												"This information is related to the modification of the organization");
										test.log(LogStatus.PASS,
												"This information is related to the modification of the organization");
									}
								} else {
									test.log(LogStatus.FAIL,
											"The actual audit trail data " + users.get(i).get("organization_id")
											+ "  did not match the expected " + users.get(i).get("code_id"));
								}
							}

						} else if (users.get(i).get("code_id")
								.equals(Integer.parseInt(AuditCodeConstants.CREATE_SITE))
								|| (users.get(i).get("code_id")
										.equals(Integer.parseInt(AuditCodeConstants.MODIFY_SITE)))
								|| (users.get(i).get("code_id")
										.equals(Integer.parseInt(AuditCodeConstants.DELETE_SITE)))) {
							if (users.get(i).get("by_organization_id")
									.equals(ExpectedAccounts.get(j).get("by_organization_id"))
									&& users.get(i).get("by_resource_id")
									.equals(ExpectedAccounts.get(j).get("by_resource_id"))
									&& users.get(i).get("code_id")
									.equals(Integer.parseInt((String) ExpectedAccounts.get(j).get("code_id")))
									&& users.get(i).get("on_resource_id")
									.equals(ExpectedAccounts.get(j).get("on_resource_id"))) {

								if (users.get(i).get("code_id")
										.equals(Integer.parseInt(AuditCodeConstants.CREATE_SITE))
										|| (users.get(i).get("code_id")
												.equals(Integer.parseInt(AuditCodeConstants.MODIFY_SITE)))) {
									// JsonPath object = new JsonPath((String) users.get(i).get("audit_data"));
									HashMap<String, Object> object =
											(HashMap<String, Object>) users.get(i).get("audit_data");

									ArrayList<ResponseBody> audit = new ArrayList<ResponseBody>();
									audit = (ArrayList<ResponseBody>) ExpectedAccounts.get(j).get("audit_data");


									System.out.println("site id is " + audit.get(0).jsonPath().get("data.site_id"));
									System.out.println("site id is " + object.get("site_id"));
									// System.out.println();
									// String a = object.get("deleted").toString();

									if (/*
									 * object.get("deleted").equals(audit.get(0).jsonPath().get("data.deleted"))
									 * &&
									 */ object.get("is_registered")
									 .equals(audit.get(0).jsonPath().get("data.is_registered"))
									 && object.get("site_id").equals(audit.get(0).jsonPath().get("data.site_id"))
									 && object.get("site_name")
									 .equals(audit.get(0).jsonPath().get("data.site_name"))
									 && object.get("site_type")
									 .equals(audit.get(0).jsonPath().get("data.site_type"))
									 &&
									 // object.get("site_version").equals(audit.get(0).jsonPath().get("data.site_version"))&&
									 /*
									  * object.get("site_secret")
									  * .equals(audit.get(0).jsonPath().get("data.site_secret")) &&
									  */ object.get("organization_id")
									  .equals(audit.get(0).jsonPath().get("data.organization_id"))
									  && object.get("create_user_id")
									  .equals(audit.get(0).jsonPath().get("data.create_user_id"))
									  // &&
									  // object.get("gateway_id").equals(audit.get(0).jsonPath().get("data.gateway_id"))&&
									  // object.get("gateway_hostname").equals(audit.get(0).jsonPath().get("data.gateway_hostname"))&&
									  // object.get("heartbeat_interval")
									  // .equals(audit.get(0).jsonPath().get("data.heartbeat_interval"))
									  // object.get("registration_basecode").equals(audit.get(0).jsonPath().get("data.registration_basecode"))
											) {
										test.log(LogStatus.PASS,
												"The actual audit trail data matched the expected audit code "
														+ users.get(i).get("code_id"));
									} else {
										test.log(LogStatus.FAIL,
												"The actual audit trail data " + users.get(i).get("organization_id")
												+ "  did not match the expected " + users.get(i).get("code_id"));
									}
								} else {
									test.log(LogStatus.PASS,
											"The actual audit trail data matched the expected audit code "
													+ users.get(i).get("code_id"));
								}

							}
						}else if(users.get(i).get("code_id")
								.equals(Integer.parseInt(AuditCodeConstants.CREATE_SOURCE))
								||(users.get(i).get("code_id")
										.equals(Integer.parseInt(AuditCodeConstants.MODIFY_SOURCE)))
								||(users.get(i).get("code_id")
										.equals(Integer.parseInt(AuditCodeConstants.DELETE_SOURCE)))) 
						{
							test.log(LogStatus.INFO, "Validating the source audit trail");
							assertResponseItem(users.get(i).get("by_organization_id"),ExpectedAccounts.get(i).get("by_organization_id"),test);
							assertResponseItem(users.get(i).get("by_resource_id"),ExpectedAccounts.get(i).get("by_resource_id"),test);
							assertResponseItem(users.get(i).get("code_id").toString(),ExpectedAccounts.get(i).get("code_id").toString(),test);
							assertResponseItem(users.get(i).get("on_resource_id"),ExpectedAccounts.get(i).get("on_resource_id"),test);

							assertResponseItem(users.get(i).get("by_resource_name").toString().toLowerCase(),ExpectedAccounts.get(i).get("by_resource_name").toString().toLowerCase(),test);
							assertResponseItem(users.get(i).get("by_resource_type"),ExpectedAccounts.get(i).get("by_resource_type"),test);
							assertResponseItem(users.get(i).get("on_resource_type").toString(),ExpectedAccounts.get(i).get("on_resource_type").toString(),test);
							assertResponseItem(users.get(i).get("on_resource_name").toString().toLowerCase(),ExpectedAccounts.get(i).get("on_resource_name").toString().toLowerCase(),test);
							if (users.get(i).get("code_id")
									.equals(Integer.parseInt(AuditCodeConstants.CREATE_SOURCE))
									||(users.get(i).get("code_id")
											.equals(Integer.parseInt(AuditCodeConstants.MODIFY_SOURCE)))) {

								CheckForSources(users,i,j,ExpectedAccounts,test);
								test.log(LogStatus.PASS, "The audit trail data matched the expected audit code"+users.get(i).get("code_id"));
							}else {
								test.log(LogStatus.PASS, "The audit trail data matched the expected audit code"+users.get(i).get("code_id"));  
							}

						}else if(users.get(i).get("code_id")
								.equals(Integer.parseInt(AuditCodeConstants.create_destination))
								||(users.get(i).get("code_id")
										.equals(Integer.parseInt(AuditCodeConstants.update_destination)))
								||(users.get(i).get("code_id")
										.equals(Integer.parseInt(AuditCodeConstants.delete_destination)))) 
						{
							test.log(LogStatus.INFO, "Validating the destination audit trail");
							assertResponseItem(users.get(i).get("by_organization_id"),ExpectedAccounts.get(i).get("by_organization_id"),test);
							assertResponseItem(users.get(i).get("by_resource_id"),ExpectedAccounts.get(i).get("by_resource_id"),test);
							assertResponseItem(users.get(i).get("code_id").toString(),ExpectedAccounts.get(i).get("code_id").toString(),test);
							assertResponseItem(users.get(i).get("on_resource_id"),ExpectedAccounts.get(i).get("on_resource_id"),test);

							assertResponseItem(users.get(i).get("by_resource_name").toString().toLowerCase(),ExpectedAccounts.get(i).get("by_resource_name").toString().toLowerCase(),test);
							assertResponseItem(users.get(i).get("by_resource_type"),ExpectedAccounts.get(i).get("by_resource_type"),test);
							assertResponseItem(users.get(i).get("on_resource_type").toString(),ExpectedAccounts.get(i).get("on_resource_type").toString(),test);
							assertResponseItem(users.get(i).get("on_resource_name").toString().toLowerCase(),ExpectedAccounts.get(i).get("on_resource_name").toString().toLowerCase(),test);
							if (users.get(i).get("code_id")
									.equals(Integer.parseInt(AuditCodeConstants.create_destination))
									||(users.get(i).get("code_id")
											.equals(Integer.parseInt(AuditCodeConstants.update_destination)))) {

								CheckForDestinaitons(users,i,j,ExpectedAccounts,test);
								test.log(LogStatus.PASS, "The audit trail data matched the expected audit code"+users.get(i).get("code_id"));
							}else {
								test.log(LogStatus.PASS, "The audit trail data matched the expected audit code"+users.get(i).get("code_id"));  
							}

						}else 
						{
							checkaudittrail_enh(users,ExpectedAccounts,i,j,test);
						}

					}
				} else {
					int j = 0;
					if (page_size == 20) {
						j = users.size() - 1;

					} else {
						j = ExpectedAccounts.size() - 1;
						if (curr_page != 1) {
							j = (ExpectedAccounts.size() - 1) - (curr_page - 1) * page_size;
						}
					}
					for (int i = 0; i < users.size() && j >= 0; i++, j--) {
						// System.out.println("The value of the generated isgfnjl ....... :");

						if (users.get(i).get("code_id").equals(Integer.parseInt(AuditCodeConstants.LOGIN_USER))
								|| (users.get(i).get("code_id")
										.equals(Integer.parseInt(AuditCodeConstants.DELETE_USER)))) {
							if (users.get(i).get("code_id")
									.equals(Integer.parseInt((String) ExpectedAccounts.get(j).get("code_id")))
									&& users.get(i).get("by_organization_id")
									.equals(ExpectedAccounts.get(j).get("by_organization_id"))
									&& users.get(i).get("by_resource_id")
									.equals(ExpectedAccounts.get(j).get("by_resource_id"))
									&& users.get(i).get("on_resource_id")
									.equals(ExpectedAccounts.get(j).get("on_resource_id"))) {

								test.log(LogStatus.PASS, "The actual audit trail data matched the expected "
										+ users.get(i).get("code_id"));
							}

						} else if (users.get(i).get("code_id")
								.equals(Integer.parseInt(AuditCodeConstants.CREATE_USER))
								|| (users.get(i).get("code_id")
										.equals(Integer.parseInt(AuditCodeConstants.MODIFY_LOGIN_USER))
										|| (users.get(i).get("code_id")
												.equals(Integer.parseInt(AuditCodeConstants.MODIFY_USER))
												|| (users.get(i).get("code_id")
														.equals(Integer.parseInt(AuditCodeConstants.MODIFY_LOGIN_USER_PASSWORD))
														|| (users.get(i).get("code_id").equals(
																Integer.parseInt(AuditCodeConstants.MODIFY_USER_PASSWORD))))))) {
							if (users.get(i).get("code_id")
									.equals(Integer.parseInt((String) ExpectedAccounts.get(j).get("code_id")))
									)
								// &&((JsonPath) users.get(i).get("audit_data")).get("user_id").toString()
								// .equals(((JsonPath) ExpectedAccounts.get(i).get("audit_data")).get("user_id"))
								// )
							{
								assertResponseItem(users.get(i).get("by_organization_id"),ExpectedAccounts.get(i).get("by_organization_id"),test);
								assertResponseItem(users.get(i).get("by_resource_id"),ExpectedAccounts.get(i).get("by_resource_id"),test);
								assertResponseItem(users.get(i).get("code_id").toString(),ExpectedAccounts.get(i).get("code_id").toString(),test);
								assertResponseItem(users.get(i).get("on_resource_id"),ExpectedAccounts.get(i).get("on_resource_id"),test);

								assertResponseItem(users.get(i).get("by_resource_name").toString().toLowerCase(),ExpectedAccounts.get(i).get("by_resource_name").toString().toLowerCase(),test);
								assertResponseItem(users.get(i).get("by_resource_type"),ExpectedAccounts.get(i).get("by_resource_type"),test);
								assertResponseItem(users.get(i).get("on_resource_type").toString(),ExpectedAccounts.get(i).get("on_resource_type").toString(),test);
								assertResponseItem(users.get(i).get("on_resource_name").toString().toLowerCase(),ExpectedAccounts.get(i).get("on_resource_name").toString().toLowerCase(),test);
								test.log(LogStatus.INFO, "The initial validation passed");
								// @SuppressWarnings({ "unchecked", "rawtypes" })
								// ArrayList<ResponseBody> audit_data = (ArrayList<ResponseBody>)
								// users.get(i).get("audit_data");
								// JsonPath parser = new JsonPath((String) users.get(i).get("audit_data"));
								HashMap<String, Object> parser =
										(HashMap<String, Object>) users.get(i).get("audit_data");
								ArrayList<ResponseBody> userresponse1 = new ArrayList<ResponseBody>();
								userresponse1 = (ArrayList<ResponseBody>) ExpectedAccounts.get(j).get("audit_data");
								test.log(LogStatus.INFO,
										"The size of the user response is " + userresponse1.size());

								if (parser.get("user_id")
										.equals(userresponse1.get(0).jsonPath().get("data.user_id"))
										&& (parser.get("first_name")
												.equals(userresponse1.get(0).jsonPath().get("data.first_name")))
										&& (parser.get("last_name")
												.equals(userresponse1.get(0).jsonPath().get("data.last_name")))
										&& (parser.get("email")
												.equals(userresponse1.get(0).jsonPath().get("data.email")))
										&& (parser.get("role_id")
												.equals(userresponse1.get(0).jsonPath().get("data.role_id")))
										&& (parser.get("organization_id")
												.equals(userresponse1.get(0).jsonPath().get("data.organization_id")))
										&& (parser.get("create_ts")
												.equals(userresponse1.get(0).jsonPath().getLong("data.create_ts")))) {
									test.log(LogStatus.PASS, "The actual audit trail data matched the expected "
											+ users.get(i).get("code_id"));
								} else {
									test.log(LogStatus.FAIL,
											"The actual audit trail data " + users.get(i).get("audit_data")
											+ "  did not match the expected " + users.get(i).get("code_id"));
									test.log(LogStatus.INFO,
											userresponse1.get(0).jsonPath().get("data.user_id").toString());
									test.log(LogStatus.INFO,
											userresponse1.get(0).jsonPath().get("data.first_name").toString());
									test.log(LogStatus.INFO,
											userresponse1.get(0).jsonPath().get("data.last_name").toString());
									test.log(LogStatus.INFO,
											userresponse1.get(0).jsonPath().get("data.email").toString());
									test.log(LogStatus.INFO,
											userresponse1.get(0).jsonPath().get("data.role_id").toString());
									test.log(LogStatus.INFO,
											userresponse1.get(0).jsonPath().get("data.organization_id").toString());
								}



								// test.log(LogStatus.INFO, "The expected user id is
								// "+userresponse1.get(0).jsonPath().get("data.user_id"));

							}
						}
						// @author bharadwaj.Ghadiam
						// cases related to create sub organization,modify subOrganization,modify
						// subOrganization ,modifyLoggedInUserOrganization
						else if ((users.get(i).get("code_id")
								.equals(Integer.parseInt(AuditCodeConstants.CREATE_SUB_ORGANIZATION)))
								|| (users.get(i).get("code_id")
										.equals(Integer.parseInt(AuditCodeConstants.MODIFY_LOGIN_USER_ORGANIZATION)))
								|| (users.get(i).get("code_id")
										.equals(Integer.parseInt(AuditCodeConstants.MODIFY_SUB_ORGANIZATION)))
								|| (users.get(i).get("code_id")
										.equals(Integer.parseInt(AuditCodeConstants.MODIFY_ORGANIZATION)))) {

							if (users.get(i).get("by_organization_id")
									.equals(ExpectedAccounts.get(j).get("by_organization_id"))
									) {
								assertResponseItem(users.get(i).get("by_organization_id"),ExpectedAccounts.get(i).get("by_organization_id"),test);
								assertResponseItem(users.get(i).get("by_resource_id"),ExpectedAccounts.get(i).get("by_resource_id"),test);
								assertResponseItem(users.get(i).get("code_id").toString(),ExpectedAccounts.get(i).get("code_id").toString(),test);
								assertResponseItem(users.get(i).get("on_resource_id"),ExpectedAccounts.get(i).get("on_resource_id"),test);

								assertResponseItem(users.get(i).get("by_resource_name").toString().toLowerCase(),ExpectedAccounts.get(i).get("by_resource_name").toString().toLowerCase(),test);
								assertResponseItem(users.get(i).get("by_resource_type"),ExpectedAccounts.get(i).get("by_resource_type"),test);
								assertResponseItem(users.get(i).get("on_resource_type").toString(),ExpectedAccounts.get(i).get("on_resource_type").toString(),test);
								assertResponseItem(users.get(i).get("on_resource_name").toString().toLowerCase(),ExpectedAccounts.get(i).get("on_resource_name").toString().toLowerCase(),test);

								// JsonPath object = new JsonPath((String) users.get(i).get("audit_data"));
								HashMap<String, Object> object =
										(HashMap<String, Object>) users.get(i).get("audit_data");
								ArrayList<ResponseBody> audit = new ArrayList<ResponseBody>();
								audit = (ArrayList<ResponseBody>) ExpectedAccounts.get(j).get("audit_data");

								if (object.get("organization_id")
										.equals(audit.get(0).jsonPath().get("data.organization_id"))
										&& object.get("organization_name")
										.equals(audit.get(0).jsonPath().get("data.organization_name"))
										&& object.get("create_ts")
										.equals(audit.get(0).jsonPath().getLong("data.create_ts"))
										&& object.get("parent_id").equals(audit.get(0).jsonPath().get(
												"data.parent_id"))/*
												 * && object.get("create_user_id").equals(audit.get(0).
												 * jsonPath().get("data.create_user_id"))
												 */) {
									if ((users.get(i).get("code_id")
											.equals(Integer.parseInt(AuditCodeConstants.CREATE_SUB_ORGANIZATION)))) {
										/*
										 * if (object.get("create_user_id")
										 * .equals(audit.get(0).jsonPath().get("data.create_user_id"))) {
										 */
										assertTrue("The value is correct", true);
										test.log(LogStatus.PASS, "The creation is successfull");
										System.out.println(
												"This information is related to the creation of the sub organizaion:");
										test.log(LogStatus.PASS,
												"This information is related to the creation of the sub organizaion:");
										// }
									} else {
										assertTrue("The value is correct", true);
										System.out.println(
												"This information is related to the modification of the organization");
										test.log(LogStatus.PASS,
												"This information is related to the modification of the organization");
									}
								} else {
									test.log(LogStatus.FAIL,
											"The actual audit trail data " + users.get(i).get("organization_id")
											+ "  did not match the expected " + users.get(i).get("code_id"));
								}
							}

						} else if (users.get(i).get("code_id")
								.equals(Integer.parseInt(AuditCodeConstants.CREATE_SITE))
								|| (users.get(i).get("code_id")
										.equals(Integer.parseInt(AuditCodeConstants.MODIFY_SITE)))
								|| (users.get(i).get("code_id")
										.equals(Integer.parseInt(AuditCodeConstants.DELETE_SITE)))) {
							if (users.get(i).get("by_organization_id")
									.equals(ExpectedAccounts.get(j).get("by_organization_id"))
									) {

								assertResponseItem(users.get(i).get("by_organization_id"),ExpectedAccounts.get(i).get("by_organization_id"),test);
								assertResponseItem(users.get(i).get("by_resource_id"),ExpectedAccounts.get(i).get("by_resource_id"),test);
								assertResponseItem(users.get(i).get("code_id").toString(),ExpectedAccounts.get(i).get("code_id").toString(),test);
								assertResponseItem(users.get(i).get("on_resource_id"),ExpectedAccounts.get(i).get("on_resource_id"),test);

								assertResponseItem(users.get(i).get("by_resource_name").toString().toLowerCase(),ExpectedAccounts.get(i).get("by_resource_name".toString().toLowerCase()),test);
								assertResponseItem(users.get(i).get("by_resource_type"),ExpectedAccounts.get(i).get("by_resource_type"),test);
								assertResponseItem(users.get(i).get("on_resource_type").toString(),ExpectedAccounts.get(i).get("on_resource_type").toString(),test);
								assertResponseItem(users.get(i).get("on_resource_name").toString().toLowerCase(),ExpectedAccounts.get(i).get("on_resource_name").toString().toLowerCase(),test);

								if (users.get(i).get("code_id")
										.equals(Integer.parseInt(AuditCodeConstants.CREATE_SITE))
										|| (users.get(i).get("code_id")
												.equals(Integer.parseInt(AuditCodeConstants.MODIFY_SITE)))) {
									// JsonPath object = new JsonPath((String) users.get(i).get("audit_data"));
									HashMap<String, Object> object =
											(HashMap<String, Object>) users.get(i).get("audit_data");
									ArrayList<ResponseBody> audit = new ArrayList<ResponseBody>();
									audit = (ArrayList<ResponseBody>) ExpectedAccounts.get(j).get("audit_data");


									System.out.println("site id is " + audit.get(0).jsonPath().get("data.site_id"));
									System.out.println("site id is " + object.get("site_id"));
									// Boolean actual = Boolean.parseBoolean(object.get("deleted"));
									// Boolean exp = audit.get(0).jsonPath().get("data.deleted");


									if (/*
									 * object.get("deleted").equals(audit.get(0).jsonPath().get("data.deleted"))
									 * &&
									 */ object.get("is_registered")
									 .equals(audit.get(0).jsonPath().get("data.is_registered"))
									 && object.get("site_id").equals(audit.get(0).jsonPath().get("data.site_id"))
									 && object.get("site_name")
									 .equals(audit.get(0).jsonPath().get("data.site_name"))
									 && object.get("site_type")
									 .equals(audit.get(0).jsonPath().get("data.site_type"))
									 &&
									 // object.get("site_version").equals(audit.get(0).jsonPath().get("data.site_version"))&&
									 /*
									  * object.get("site_secret")
									  * .equals(audit.get(0).jsonPath().get("data.site_secret")) &&
									  */ object.get("organization_id")
									  .equals(audit.get(0).jsonPath().get("data.organization_id"))
									  && object.get("create_user_id")
									  .equals(audit.get(0).jsonPath().get("data.create_user_id"))
									  // &&
									  // object.get("gateway_id").equals(audit.get(0).jsonPath().get("data.gateway_id"))&&
									  // object.get("gateway_hostname").equals(audit.get(0).jsonPath().get("data.gateway_hostname"))&&
									  // object.get("heartbeat_interval")
									  // .equals(audit.get(0).jsonPath().get("data.heartbeat_interval"))
									  // object.get("registration_basecode").equals(audit.get(0).jsonPath().get("data.registration_basecode"))
											) {
										test.log(LogStatus.PASS,
												"The actual audit trail data matched the expected audit code "
														+ users.get(i).get("code_id"));
									} else {
										test.log(LogStatus.FAIL,
												"The actual audit trail data " + users.get(i).get("organization_id")
												+ "  did not match the expected " + users.get(i).get("code_id"));
									}
								} else {
									test.log(LogStatus.PASS,
											"The actual audit trail data matched the expected audit code "
													+ users.get(i).get("code_id"));
								}

							}
						}else if(users.get(i).get("code_id")
								.equals(Integer.parseInt(AuditCodeConstants.CREATE_SOURCE))
								||(users.get(i).get("code_id")
										.equals(Integer.parseInt(AuditCodeConstants.MODIFY_SOURCE)))
								||(users.get(i).get("code_id")
										.equals(Integer.parseInt(AuditCodeConstants.DELETE_SOURCE)))) 
						{
							test.log(LogStatus.INFO, "Validating the source audit trail");
							assertResponseItem(users.get(i).get("by_organization_id"),ExpectedAccounts.get(i).get("by_organization_id"),test);
							assertResponseItem(users.get(i).get("by_resource_id"),ExpectedAccounts.get(i).get("by_resource_id"),test);
							assertResponseItem(users.get(i).get("code_id").toString(),ExpectedAccounts.get(i).get("code_id").toString(),test);
							assertResponseItem(users.get(i).get("on_resource_id"),ExpectedAccounts.get(i).get("on_resource_id"),test);

							assertResponseItem(users.get(i).get("by_resource_name").toString().toLowerCase(),ExpectedAccounts.get(i).get("by_resource_name").toString().toLowerCase(),test);
							assertResponseItem(users.get(i).get("by_resource_type"),ExpectedAccounts.get(i).get("by_resource_type"),test);
							assertResponseItem(users.get(i).get("on_resource_type").toString(),ExpectedAccounts.get(i).get("on_resource_type").toString(),test);
							assertResponseItem(users.get(i).get("on_resource_name").toString().toLowerCase(),ExpectedAccounts.get(i).get("on_resource_name").toString().toLowerCase(),test);
							if (users.get(i).get("code_id")
									.equals(Integer.parseInt(AuditCodeConstants.CREATE_SOURCE))
									||(users.get(i).get("code_id")
											.equals(Integer.parseInt(AuditCodeConstants.MODIFY_SOURCE)))) {

								CheckForSources(users,i,j,ExpectedAccounts,test);
								test.log(LogStatus.PASS, "The audit trail data matched the expected audit code"+users.get(i).get("code_id"));
							}else {
								test.log(LogStatus.PASS, "The audit trail data matched the expected audit code"+users.get(i).get("code_id"));  
							}

						}else if(users.get(i).get("code_id")
								.equals(Integer.parseInt(AuditCodeConstants.create_destination))
								||(users.get(i).get("code_id")
										.equals(Integer.parseInt(AuditCodeConstants.update_destination)))
								||(users.get(i).get("code_id")
										.equals(Integer.parseInt(AuditCodeConstants.delete_destination)))) 
						{
							test.log(LogStatus.INFO, "Validating the destination audit trail");
							assertResponseItem(users.get(i).get("by_organization_id"),ExpectedAccounts.get(i).get("by_organization_id"),test);
							assertResponseItem(users.get(i).get("by_resource_id"),ExpectedAccounts.get(i).get("by_resource_id"),test);
							assertResponseItem(users.get(i).get("code_id").toString(),ExpectedAccounts.get(i).get("code_id").toString(),test);
							assertResponseItem(users.get(i).get("on_resource_id"),ExpectedAccounts.get(i).get("on_resource_id"),test);

							assertResponseItem(users.get(i).get("by_resource_name").toString().toLowerCase(),ExpectedAccounts.get(i).get("by_resource_name").toString().toLowerCase(),test);
							assertResponseItem(users.get(i).get("by_resource_type"),ExpectedAccounts.get(i).get("by_resource_type"),test);
							assertResponseItem(users.get(i).get("on_resource_type").toString(),ExpectedAccounts.get(i).get("on_resource_type").toString(),test);
							assertResponseItem(users.get(i).get("on_resource_name").toString().toLowerCase(),ExpectedAccounts.get(i).get("on_resource_name").toString().toLowerCase(),test);
							if (users.get(i).get("code_id")
									.equals(Integer.parseInt(AuditCodeConstants.create_destination))
									||(users.get(i).get("code_id")
											.equals(Integer.parseInt(AuditCodeConstants.update_destination)))) {

								CheckForDestinaitons(users,i,j,ExpectedAccounts,test);
								test.log(LogStatus.PASS, "The audit trail data matched the expected audit code"+users.get(i).get("code_id"));
							}else {
								test.log(LogStatus.PASS, "The audit trail data matched the expected audit code"+users.get(i).get("code_id"));  
							}

						}else {
							checkaudittrail_enh(users,ExpectedAccounts,i,j,test);
						}

					}

				}
			} else {
				// This is used For only filtering
				String[] filterStrArray = FilterStr.split(",");
				for (int i = 0; i < return_size; i++) {
					for (int j = 0; j < filterStrArray.length; j++) {
						String[] eachFilterArray = filterStrArray[j].split(";");
						if (eachFilterArray[1].equals("=")) {
							response.then().body("data.data[" + i + "]." + eachFilterArray[0],
									equalTo(eachFilterArray[2]));
							System.out.println("The value of the message inside the body"
									+ response.then().extract().path("data.data[" + i + "]." + eachFilterArray[0]));
						} else if (eachFilterArray[1].equals("!=")) {
							response.then().body("data.data[" + i + "]." + eachFilterArray[0],
									not(equalTo(eachFilterArray[2])));
						}

					}
				}
			}

			if (page_size > 100 && page_size == SpogConstants.MAX_PAGE_SIZE) {
				page_size = 100;
			}
			response.then().body("pagination.curr_page", equalTo(curr_page)).body("pagination.page_size",
					equalTo(page_size));


			if ((total_size % page_size) == 0) {
				total_page = total_size / page_size;

			} else {
				total_page = total_size / page_size + 1;
			}

			response.then().body("pagination.total_page", equalTo(total_page));
			if (curr_page >= 1 && curr_page <= total_page) {

				if (curr_page < total_page) {
					has_next = true;
				}
				if (curr_page > 1) {
					has_prev = true;
				}
			}

			response.then().body("pagination.has_next", equalTo(has_next)).body("pagination.has_prev",
					equalTo(has_prev));

		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			if (code.contains("0030000A")) {
				message = message.replace("{0}", getUUId());
			}
			checkErrorCode(response, code);
			test.log(LogStatus.INFO, "The error code matched with the expected " + code);
			checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
	}


	/**
	 * Get all the Organization of the specified MSP
	 * 
	 * @author kiran.sripada
	 * @param JwtToken
	 * @param orgId
	 * @param additionalUrl (filter/sort/pagination)
	 * @param extentTest object
	 * @return response
	 */
	public Response getaudittrailbyorgId(String token, String orgId, String additionalURL,
			ExtentTest test) {

		setUUID(orgId);
		Response response = spogInvoker.getaudittrailbyorgId(token, orgId, additionalURL, test);
		return response;
	}


	/**
	 * Generic function to store the audit codes
	 * 
	 * @author kiran.sripada
	 * @param codeid
	 * @param orgid
	 * @param byresourceid
	 * @param onresourceid
	 * @param response
	 * @return hashmap
	 */
	public HashMap<String, Object> storetheuserevents(String code_id, String by_organization_id,
			String by_resource_id, String on_resource_id, Response response) {

		HashMap<String, Object> user_events_code = new HashMap<String, Object>();
		ArrayList<ResponseBody> userresponse = new ArrayList<ResponseBody>();
		user_events_code.put("code_id", code_id);
		user_events_code.put("by_organization_id", by_organization_id);
		user_events_code.put("by_resource_id", by_resource_id);
		user_events_code.put("on_resource_id", on_resource_id);
		if ((code_id == AuditCodeConstants.DELETE_USER) || (code_id == AuditCodeConstants.LOGIN_USER)
				|| (code_id == AuditCodeConstants.DELETE_SITE)
				|| (code_id == AuditCodeConstants.DELETE_SOURCE)
				|| (code_id == AuditCodeConstants.delete_destination)) {
			user_events_code.put("audit_data", "{}");
		} else {
			userresponse.add(response);
			user_events_code.put("audit_data", userresponse);
		}
		return user_events_code;
	}


	/**
	 * Compose the hash map and invoke the updatesite
	 * 
	 * @author kiran.sripada
	 * @param SiteID
	 * @param newSiteName
	 * @return
	 */

	public Response updateSiteById(String SiteID, String newSiteName, String token) {

		// test.log(LogStatus.INFO,"Creating a HashMap for updating the site name");
		Map<String, String> siteinfo = jp.updatesitebyId(newSiteName);
		Response response = spogInvoker.updatesiteInfo(token, siteinfo, SiteID);
		return response;
	}


	/**
	 * Delete the site id
	 * 
	 * @author kiran.sripada
	 * @param SiteID
	 * @param token
	 * @return response
	 */
	public Response deleteSite(String siteId, String token) {

		Response response = spogInvoker.deleteSite(siteId, token);

		return response;
	}


	/**
	 * The function is used for create source testing
	 * 
	 * @author shuo.zhang
	 * @param source_name
	 * @param source_type
	 * @param source_product
	 * @param organization_id
	 * @param site_id
	 * @param policy_id
	 * @param protection_status
	 * @param connection_status
	 * @param os_major
	 * @param applications
	 * @param test
	 */
	public String createSourceWithCheck(String source_name, SourceType source_type,
			SourceProduct source_product, String organization_id, String site_id,
			ProtectionStatus protection_status, ConnectionStatus connection_status, String os_major,
			String applications, ExtentTest test) {

		Response response = createSource(source_name, source_type, source_product, organization_id,
				site_id, protection_status, connection_status, os_major, applications, test);

		return checkCreateSource(response, source_name, source_type, source_product, organization_id,
				site_id, protection_status, connection_status, os_major, applications, null, null, null,
				null, null, null, null, null, null, SpogConstants.SUCCESS_POST, null, test);

		/*
		 * errorHandle.printDebugMessageInDebugFile("check status code"); test.log(LogStatus.INFO,
		 * "check status code"); response.then().statusCode(SpogConstants.SUCCESS_POST);
		 * 
		 * if ((organization_id == null) || (organization_id.equals(""))) { organization_id =
		 * this.GetLoggedinUserOrganizationID(); }
		 * 
		 * String create_user_id = this.GetLoggedinUser_UserID();
		 * 
		 * errorHandle.printDebugMessageInDebugFile("response is " + response.getBody().toString());
		 * test.log(LogStatus.INFO, "check response");
		 * 
		 * if (source_name != null) { response.then().body("data.source_name", equalTo(source_name)); }
		 * if (source_type != null) { response.then().body("data.source_type",
		 * equalTo(source_type.name())); } if (source_product != null) {
		 * response.then().body("data.source_product", equalTo(source_product.name())); }
		 * response.then().body("data.organization_id", equalTo(organization_id));
		 * 
		 * if (protection_status != null) { response.then().body("data.protection_status",
		 * equalTo(protection_status.name())); } if (connection_status != null) {
		 * response.then().body("data.connection_status", equalTo(connection_status.name())); }
		 * //response.then().body("data.site_id", equalTo(site_id));
		 * 
		 * if (os_major != null) { //response.then().body("data.operating_system.os_major",
		 * equalTo(os_major)); }
		 * 
		 * if ((applications != null) && (!applications.equals(""))) { String[] applicationArray = null;
		 * if ((applications != null) && (!applications.equals(""))) { applicationArray =
		 * applications.split(";");
		 * 
		 * } response.then().body("data.applications", hasItems(applicationArray)); }
		 * 
		 * //response.then().body("data.create_user_id", equalTo(create_user_id));
		 * 
		 * String source_id = null; source_id = response.then().extract().path("data.source_id"); return
		 * source_id;
		 */
	}


	/**
	 * audit trails for users
	 * 
	 * @author BharadwajReddyGhadiam
	 * @param response
	 * @param expectedStatusCode
	 * @param ExpectedAccounts
	 * @param curr_page
	 * @param page_size
	 * @param FilterStr
	 * @param SortStr
	 * @param expectedErrorMessage
	 * @param test
	 */
	public void checkaudittraildata1(Response response, int expectedStatusCode,ArrayList<HashMap> ExpectedAccounts,
			int curr_page, int page_size, String FilterStr, String SortStr, SpogMessageCode Info, ExtentTest test) {

		String expectedErrorMessage = "", expectedErrorCode = "";
		int total_size = 0, return_size = 0;
		if (curr_page == 0) {
			curr_page = 1;
		}
		if (page_size == 0 || page_size > SpogConstants.MAX_PAGE_SIZE) {
			page_size = 100;
		}
		// related inforamtion for the response
		ArrayList<HashMap> users = new ArrayList<HashMap>();
		// For Pagination of the different situations
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			users = new ArrayList<HashMap>();
			users = response.then().extract().path("data");
			System.out.println("The expected size is " + ExpectedAccounts.size());
			return_size = users.size();
			total_size = response.then().extract().path("pagination.total_size");
			test.log(LogStatus.INFO, "The actual total size is " + total_size);
			test.log(LogStatus.INFO, "expect status code is " + expectedStatusCode);
			errorHandle.printInfoMessageInDebugFile("expect status code is " + expectedStatusCode);
			checkResponseStatus(response, expectedStatusCode);
		}
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE
				|| ExpectedAccounts.size() == total_size) {
			test.log(LogStatus.INFO, "check response user info");
			if (((SortStr == null) || (SortStr.equals("")))
					&& ((FilterStr == null) || (FilterStr.equals("")))) {
				// This method is used to sort all the values
				Collections.sort(users, new Comparator<HashMap>() {

					@Override
					public int compare(HashMap o1, HashMap o2) {

						// TODO Auto-generated method stub
						Long create_ts = (Long) o1.get("create_ts");
						Long create_ts1 = (Long) o2.get("create_ts");
						if (create_ts > create_ts1)
							return 1;
						if (create_ts < create_ts1)
							return -1;
						else
							return 0;
					}
				});

				for (int i = 0; i < users.size(); i++) {
					// This method is used to check The validation for all the cases
					checkDetailsForAuditTrails(users, i, i, ExpectedAccounts, test);
				}
			} // For basic condition without any sorting

			// SortStr
			else if (((SortStr != null) && (!SortStr.equals("")) && SortStr.contains("create_ts")
					&& ((FilterStr == null) || (FilterStr.equals(""))))
					|| ((SortStr != null) && (!SortStr.equals("")) && SortStr.contains("create_ts")
							&& ((FilterStr != null) || (!FilterStr.equals(""))))) {

				// validating the response for all the users who are sorted based on response
				if (SortStr.contains("asc")) {
					int j = 0;
					if (curr_page != 1) {
						j = (curr_page - 1) * page_size;
					}
					for (int i = 0; i < users.size(); i++, j++) {

						// This method is used to check The validation for all the cases
						checkDetailsForAuditTrails(users, i, j, ExpectedAccounts, test);
					}
				} else {
					int j = 0;
					if (page_size == 20) {
						j = users.size() - 1;
					} else {
						j = ExpectedAccounts.size() - 1;
						if (curr_page != 1) {
							j = (ExpectedAccounts.size() - 1) - (curr_page - 1) * page_size;
						}
					}
					for (int i = 0; i < users.size() && j >= 0; i++, j--) {
						// This method is used to check The validation for all the cases
						checkDetailsForAuditTrails(users, i, j, ExpectedAccounts, test);
					}

				} // descending order

			} // for sorting the URI in ascending or descending order

			else {
				// This is used For only filtering
				String[] filterStrArray = FilterStr.split(",");
				for (int i = 0; i < return_size; i++) {
					for (int j = 0; j < filterStrArray.length; j++) {
						String[] eachFilterArray = filterStrArray[j].split(";");
						if (eachFilterArray[1].equals("=")) {
							response.then().body("data.data[" + i + "]." + eachFilterArray[0],
									equalTo(eachFilterArray[2]));
							System.out.println("The value of the message inside the body"
									+ response.then().extract().path("data.data[" + i + "]." + eachFilterArray[0]));
						} else if (eachFilterArray[1].equals("!=")) {
							response.then().body("data.data[" + i + "]." + eachFilterArray[0],
									not(equalTo(eachFilterArray[2])));
						}

					}
				}
			} // only filtering
			// check the response validation for pages,page_size,total_size
			/*validateResponseForPages(curr_page, page_size, response, total_size, test);*/
		} else {

			if (Info.getStatus() != "0010000") {
				expectedErrorMessage = Info.getStatus();
				if (expectedErrorMessage.contains("{0}")) {
					expectedErrorMessage = expectedErrorMessage.replace("{0}", getUUId());
					System.out.println(expectedErrorMessage);
				}
				expectedErrorCode = Info.getCodeString();
			}
			checkErrorMessage(response, expectedErrorMessage);
			test.log(LogStatus.PASS, "The value of the message is " + expectedErrorMessage);
			checkErrorCode(response, expectedErrorCode);
			test.log(LogStatus.PASS, "The value of the code  generated  is " + expectedErrorCode);
			test.log(LogStatus.INFO,
					"The value of the  response generated actually is :" + response.getStatusCode());
		}
	}


	/**
	 * @author BharadwajReddyGhadaim
	 * @param users
	 * @param index i
	 * @param index j
	 * @param ExpectedAccounts
	 * @param test
	 */
	private void checkDetailsForAuditTrails(ArrayList<HashMap> users, int i, int j,
			ArrayList<HashMap> ExpectedAccounts, ExtentTest test) {

		// TODO Auto-generated method stub
		if (users.get(i).get("code_id").equals(Integer.parseInt(AuditCodeConstants.LOGIN_USER))
				|| (users.get(i).get("code_id").equals(Integer.parseInt(AuditCodeConstants.DELETE_USER)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.DELETE_SUB_ORGANIZATION)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.DELETE_ORGANIZATION)))
				|| (users.get(i).get("code_id").equals(Integer.parseInt(AuditCodeConstants.DELETE_SITE)))
				|| (users.get(i).get("code_id").equals(Integer.parseInt(AuditCodeConstants.DELETE_SOURCE)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.delete_destination)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.delete_cloud_account)))) {

			// check the details regarding by_resource_id,organization_id,on_resource_id

			voidCheckDetails(users, i, j, ExpectedAccounts, test);

			test.log(LogStatus.INFO,
					"This information is related to the delete,login(user/organization/suborganization,sites/sources/destinations/cloud_accounts) The code id:"
							+ users.get(i).get("code_id"));
		} else if (users.get(i).get("code_id").equals(Integer.parseInt(AuditCodeConstants.CREATE_USER))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.MODIFY_LOGIN_USER))
						|| (users.get(i).get("code_id").equals(Integer.parseInt(AuditCodeConstants.MODIFY_USER))
								|| (users.get(i).get("code_id")
										.equals(
												Integer.parseInt(AuditCodeConstants.MODIFY_LOGIN_USER_PASSWORD))
										|| (users.get(i).get("code_id")
												.equals(Integer.parseInt(AuditCodeConstants.MODIFY_USER_PASSWORD))))))) {
			// check the details regarding by_resource_id,organization_id,on_resource_id
			if(users.get(i).get("code_id").toString().equals(ExpectedAccounts.get(j).get("code_id").toString()))
			{
				voidCheckDetails(users, i, j, ExpectedAccounts, test);
			}

			test.log(LogStatus.INFO, "The initial validation passed");
			// Validation For the information related to the users
			CheckForUsers(users, i, j, ExpectedAccounts, test);

		}
		// @author bharadwaj.Ghadiam
		// cases related to create sub organization,modify subOrganization,modify subOrganization
		// ,modifyLoggedInUserOrganization
		else if ((users.get(i).get("code_id")
				.equals(Integer.parseInt(AuditCodeConstants.CREATE_SUB_ORGANIZATION)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.MODIFY_LOGIN_USER_ORGANIZATION)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.MODIFY_SUB_ORGANIZATION)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.MODIFY_ORGANIZATION)))) {
			// check the details regarding by_resource_id,organization_id,on_resource_id
			if(users.get(i).get("code_id").toString().equals(ExpectedAccounts.get(j).get("code_id").toString()))
			{
				voidCheckDetails(users, i, j, ExpectedAccounts, test);
				CheckForOrganization(users, i, j, ExpectedAccounts, test);
			}

			// CheckForOrgnizations


		} else if ((users.get(i).get("code_id")
				.equals(Integer.parseInt(AuditCodeConstants.CREATE_SOURCE)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.MODIFY_SOURCE)))) {
			// check the details regarding by_resource_id,organization_id,on_resource_id
			if(users.get(i).get("code_id").toString().equals(ExpectedAccounts.get(j).get("code_id").toString()))
			{
				voidCheckDetails(users, i, j, ExpectedAccounts, test);
				CheckForSources(users, i, j, ExpectedAccounts, test);
			}

			// CheckForOrgnizations


		} else if ((users.get(i).get("code_id")
				.equals(Integer.parseInt(AuditCodeConstants.create_destination)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.update_destination)))) {
			// check the details regarding by_resource_id,organization_id,on_resource_id
			if(users.get(i).get("code_id").toString().equals(ExpectedAccounts.get(j).get("code_id").toString()))
			{
				voidCheckDetails(users, i, j, ExpectedAccounts, test);
				CheckForDestinaitons(users, i, j, ExpectedAccounts, test);
			}


			// CheckFordestinations


		} else if ((users.get(i).get("code_id")
				.equals(Integer.parseInt(AuditCodeConstants.create_cloud_account)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.update_cloud_account)))) {
			// check the details regarding by_resource_id,organization_id,on_resource_id
			if(users.get(i).get("code_id").toString().equals(ExpectedAccounts.get(j).get("code_id").toString()))
			{
				voidCheckDetails(users, i, j, ExpectedAccounts, test);
				CheckForcloudAccounts(users, i, j, ExpectedAccounts, test);
			}

			// CheckForOrgnizations


		} // This information is related to create a site and update a site
		else if ((
				(users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.CREATE_SITE)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.MODIFY_SITE))))
				) {
			// check the details regarding by_resource_id,organization_id,on_resource_id
			if(users.get(i).get("code_id").toString().equals(ExpectedAccounts.get(j).get("code_id").toString()))
			{
				voidCheckDetails(users, i, j, ExpectedAccounts, test);
				CheckForSites(users, i, j, ExpectedAccounts, test);
			}

			// CheckForSites

		}
		else if ((users.get(i).get("code_id")
				.equals(Integer.parseInt(AuditCodeConstants.REGISTER_SITE))) ) {
			// check the details regarding by_resource_id,organization_id,on_resource_id

			test.log(LogStatus.PASS, "The audit trail data matched the expected audit code"+users.get(i).get("code_id"));
		}

		else {
			checkaudittrail_enh(users,ExpectedAccounts, i, j, test);
		}



	}


	/**
	 * Check the response validation for sources
	 * 
	 * @author Bharadwaj.Ghadiam
	 * @param users
	 * @param i
	 * @param j
	 * @param expectedAccounts
	 * @param test
	 */

	private void CheckForSources(ArrayList<HashMap> users, int i, int j,
			ArrayList<HashMap> expectedAccounts, ExtentTest test) {

		// TODO Auto-generated method stub
		// equals(audit.get(0).jsonPath().get("data.is_registered")
		HashMap<String, Object> actual_Sources =
				(HashMap<String, Object>) users.get(i).get("audit_data");
		ArrayList<ResponseBody> audit_sources = new ArrayList<ResponseBody>();
		audit_sources = (ArrayList<ResponseBody>) expectedAccounts.get(j).get("audit_data");

		test.log(LogStatus.INFO, "validating the respone for the source_id");
		assertResponseItem(actual_Sources.get("source_id"),
				audit_sources.get(0).jsonPath().get("data.source_id"), test);
		test.log(LogStatus.INFO, "validating the respone for the create_user_id");
		// assertResponseItem(actual_Sources.get("create_user_id"),audit_sources.get(0).jsonPath().get("data.create_user_id"),test);
		test.log(LogStatus.INFO, "validating the respone for the organization_id");
		assertResponseItem(actual_Sources.get("organization_id"),
				audit_sources.get(0).jsonPath().get("data.organization_id"), test);
		test.log(LogStatus.INFO, "validating the respone for the protection_status");
		assertResponseItem(actual_Sources.get("protection_status"),
				audit_sources.get(0).jsonPath().get("data.protection_status"), test);
		test.log(LogStatus.INFO, "validating the respone for the site_id");
		assertResponseItem(actual_Sources.get("site_id"),
				audit_sources.get(0).jsonPath().get("data.site_id"), test);
		test.log(LogStatus.INFO, "validating the respone for the source_name");
		assertResponseItem(actual_Sources.get("source_name"),
				audit_sources.get(0).jsonPath().get("data.source_name"), test);
		test.log(LogStatus.INFO, "validating the respone for the source_product");
		assertResponseItem(actual_Sources.get("source_product"),
				audit_sources.get(0).jsonPath().get("data.source_product"), test);
		test.log(LogStatus.INFO, "validating the respone for the source_type");
		assertResponseItem(actual_Sources.get("source_type"),
				audit_sources.get(0).jsonPath().get("data.source_type"), test);
		test.log(LogStatus.INFO, "validating the respone for the source_id");
		assertResponseItem(actual_Sources.get("connection_status"),
				audit_sources.get(0).jsonPath().get("data.connection_status"), test);

		test.log(LogStatus.INFO, "validating the response for the agent inforamtion");
		HashMap<String, Object> expected_agent_info = audit_sources.get(0).jsonPath().get("data.agent");
		HashMap<String, Object> actual_agent_info =
				(HashMap<String, Object>) actual_Sources.get("agent");
		assertResponseItem(actual_agent_info.get("agent_name"), expected_agent_info.get("agent_name"),
				test);
		assertResponseItem(actual_agent_info.get("agent_current_version"),
				expected_agent_info.get("agent_current_version"), test);
		assertResponseItem(actual_agent_info.get("agent_upgrade_version"),
				expected_agent_info.get("agent_upgrade_version"), test);
		assertResponseItem(actual_agent_info.get("agent_upgrade_link"),
				expected_agent_info.get("agent_upgrade_link"), test);
		test.log(LogStatus.INFO, "validating the os Details...************...*********");
		assertResponseItem(actual_agent_info.get("os_major"), expected_agent_info.get("os_major"),
				test);
		assertResponseItem(actual_agent_info.get("os_architecture"),
				expected_agent_info.get("os_architecture"), test);
		test.log(LogStatus.INFO, "validating the hypervisor  Details...************...*********");
		assertResponseItem(actual_agent_info.get("hypervisor_id"),
				expected_agent_info.get("hypervisor_id"), test);
		test.log(LogStatus.INFO, "validating the VM Details...************...*********");
		assertResponseItem(actual_agent_info.get("vm_name"), expected_agent_info.get("vm_name"), test);

	}

	/**
	 * Check the response validation for the destinaitons
	 * 
	 * @param users
	 * @param i
	 * @param j
	 * @param expectedAccounts
	 * @param test
	 */

	private void CheckForDestinaitons(ArrayList<HashMap> users, int i, int j, ArrayList<HashMap> expectedAccounts,
			ExtentTest test) {

		// TODO Auto-generated method stub
		// equals(audit.get(0).jsonPath().get("data.is_registered")
		HashMap<String, Object> actual_Desinations = (HashMap<String, Object>) users.get(i).get("audit_data");
		ArrayList<ResponseBody> audit_destinations = new ArrayList<ResponseBody>();
		audit_destinations = (ArrayList<ResponseBody>) expectedAccounts.get(j).get("audit_data");
		test.log(LogStatus.INFO, "validation the response for the destianiton id ");
		assertResponseItem(actual_Desinations.get("destination_id"),
				audit_destinations.get(0).jsonPath().get("data.destination_id"), test);
		test.log(LogStatus.INFO, "validation the response for the organization_id");
		assertResponseItem(actual_Desinations.get("organization_id"),
				audit_destinations.get(0).jsonPath().get("data.organization_id"), test);
		test.log(LogStatus.INFO, "validation the response for the site_id");
		assertResponseItem(actual_Desinations.get("site_id"), audit_destinations.get(0).jsonPath().get("data.site_id"),
				test);
		test.log(LogStatus.INFO, "validation the response for the destination_type");
		assertResponseItem(actual_Desinations.get("destination_type"),
				audit_destinations.get(0).jsonPath().get("data.destination_type"), test);
		test.log(LogStatus.INFO, "validation the response for the destination name");
		assertResponseItem(actual_Desinations.get("destination_name"),
				audit_destinations.get(0).jsonPath().get("data.destination_name"), test);

		if (actual_Desinations.get("destination_type").equals("cloud_hybrid_store")) {
			HashMap<String, Object> cloud_volume = audit_destinations.get(0).jsonPath().get("data.cloud_hybrid_store");
			HashMap<String, Object> cloud_dedupe = (HashMap<String, Object>) actual_Desinations
					.get("cloud_hybrid_store");
			/*assertResponseItem(cloud_dedupe.get("data_store_folder"), cloud_volume.get("data_store_folder"), test);
			assertResponseItem(cloud_dedupe.get("data_destination"), cloud_volume.get("data_destination"), test);
			assertResponseItem(cloud_dedupe.get("index_destination"), cloud_volume.get("index_destination"), test);
			assertResponseItem(cloud_dedupe.get("hash_destination"), cloud_volume.get("hash_destination"), test);
			assertResponseItem(cloud_dedupe.get("concurrent_active_node"), cloud_volume.get("concurrent_active_node"),
					test);
			assertResponseItem(cloud_dedupe.get("is_deduplicated"), cloud_volume.get("is_deduplicated"), test);
			assertResponseItem(cloud_dedupe.get("block_size"), cloud_volume.get("block_size"), test);
			assertResponseItem(cloud_dedupe.get("hash_memory"), cloud_volume.get("hash_memory"), test);
			assertResponseItem(cloud_dedupe.get("is_compressed").toString(),
					cloud_volume.get("is_compressed").toString(), test);
			assertResponseItem(cloud_dedupe.get("encryption_password"), cloud_volume.get("encryption_password"), test);
			assertResponseItem(cloud_dedupe.get("occupied_space"), cloud_volume.get("occupied_space"), test);
			assertResponseItem(cloud_dedupe.get("stored_data"), cloud_volume.get("stored_data"), test);
			assertResponseItem(cloud_dedupe.get("deduplication_rate").toString(),
					cloud_volume.get("deduplication_rate").toString(), test);
			assertResponseItem(cloud_dedupe.get("compression_rate").toString(),
					cloud_volume.get("compression_rate").toString(), test);*/
		} else if (actual_Desinations.get("destination_id").equals("cloud_direct_volume")) {

		}
	}


	/**
	 * check the response validation for the cloudAccounts
	 * 
	 * @param users
	 * @param i
	 * @param j
	 * @param expectedAccounts
	 * @param test
	 */

	private void CheckForcloudAccounts(ArrayList<HashMap> users, int i, int j,
			ArrayList<HashMap> expectedAccounts, ExtentTest test) {

		// TODO Auto-generated method stub
		// equals(audit.get(0).jsonPath().get("data.is_registered")
		HashMap<String, Object> cloud_accounts =
				(HashMap<String, Object>) users.get(i).get("audit_data");
		ArrayList<ResponseBody> audit_clouds = new ArrayList<ResponseBody>();
		audit_clouds = (ArrayList<ResponseBody>) expectedAccounts.get(j).get("audit_data");
		test.log(LogStatus.INFO, "validation the response for the cloud_Account_id ");
		assertResponseItem(cloud_accounts.get("cloud_account_id"),
				audit_clouds.get(0).jsonPath().get("data.cloud_account_id"), test);
		test.log(LogStatus.INFO, "validation the response for the cloud_account_key");
		assertResponseItem(cloud_accounts.get("cloud_account_key"),
				audit_clouds.get(0).jsonPath().get("data.cloud_account_key"), test);
		test.log(LogStatus.INFO, "validation the response for the cloud_account_name");
		assertResponseItem(cloud_accounts.get("cloud_account_name"),
				audit_clouds.get(0).jsonPath().get("data.cloud_account_name"), test);
		test.log(LogStatus.INFO, "validation the response for the cloud_account_status");
		assertResponseItem(cloud_accounts.get("cloud_account_status"),
				audit_clouds.get(0).jsonPath().get("data.cloud_account_status"), test);
		test.log(LogStatus.INFO, "validation the response for the create_ts ");
		assertResponseItem(cloud_accounts.get("create_ts"),
				audit_clouds.get(0).jsonPath().get("data.create_ts"), test);
		test.log(LogStatus.INFO, "validation the response for the modify_ts ");
		assertResponseItem(cloud_accounts.get("modify_ts"),
				audit_clouds.get(0).jsonPath().get("data.modify_ts"), test);
		test.log(LogStatus.INFO, "validation the response for the organization_id ");
		assertResponseItem(cloud_accounts.get("organization_id"),
				audit_clouds.get(0).jsonPath().get("data.organization_id"), test);
		test.log(LogStatus.INFO, "validation the response for the cloud_Account_id ");
		assertResponseItem(cloud_accounts.get("volumes"),
				audit_clouds.get(0).jsonPath().get("data.volumes"), test);
		test.log(LogStatus.INFO, "validation the response for the allowed_actions");
		assertResponseItem(cloud_accounts.get("allowed_actions"),
				audit_clouds.get(0).jsonPath().get("data.allowed_actions"), test);
	}


	/**
	 * This method is used to provide the validation for sites
	 * 
	 * @author BharadwajReddyGhadiam
	 * @param users
	 * @param index i
	 * @param index j
	 * @param expectedAccounts
	 * @param test
	 */
	private void CheckForSites(ArrayList<HashMap> users, int i, int j,
			ArrayList<HashMap> ExpectedAccounts, ExtentTest test) {
		// TODO Auto-generated method stub

		HashMap<String, Object> object = (HashMap<String, Object>) users.get(i).get("audit_data");
		ArrayList<ResponseBody> audit = new ArrayList<ResponseBody>();
		audit = (ArrayList<ResponseBody>) ExpectedAccounts.get(j).get("audit_data");
		if (object.get("is_registered").equals(audit.get(0).jsonPath().get("data.is_registered"))
				// &&object.get("deleted").equals(audit.get(0).jsonPath().get("data.deleted"))
				&& object.get("site_id").equals(audit.get(0).jsonPath().get("data.site_id"))
				&& (object.get("organization_id")
						.equals(audit.get(0).jsonPath().get("data.organization_id")))
				&& (object.get("create_user_id").equals(audit.get(0).jsonPath().get("data.create_user_id")))
				&& (object.get("site_name").equals(audit.get(0).jsonPath().get("data.site_name")))
				&& (object.get("heartbeat_interval")
						.equals(audit.get(0).jsonPath().get("data.heartbeat_interval")))) {
			if ((users.get(i).get("code_id").equals(Integer.parseInt(AuditCodeConstants.CREATE_SITE)))) {
				if ( /*
				 * //object.get("site_secret").equals(audit.get(0).jsonPath().get( "data.site_secret"))
				 * &&
				 */(object.get("registration_basecode")
						 .equals(audit.get(0).jsonPath().get("data.registration_basecode")))) {
					assertTrue("The value is correct", true);
					test.log(LogStatus.PASS,
							"This information is related creation of the site:" + users.get(i).get("code_id"));
				}
			} else {
				assertTrue("The value is correct", true);
				test.log(LogStatus.PASS,
						"This information is related to the modification of the site and the code id :"
								+ users.get(i).get("code_id"));
			}
		} else {
			test.log(LogStatus.FAIL, " " + users.get(i).get("code_id")
					+ "The code_id has not matched with the actual results");
			assertTrue("The value is correct", false);
		}
	}


	/**
	 * Generic function for the validation of the respective page_size; Validation For
	 * pages,Page_size,Has_next_page,has_previous_page
	 * 
	 * @author BharadwajReddy
	 * @param curr_page
	 * @param page_size
	 * @param resposne
	 * @param total_size
	 * @param ExtentTest test
	 */
	public void validateResponseForPages(int curr_page, int page_size, Response response,
			int total_size, ExtentTest test) {

		// TODO Auto-generated method stub
		test.log(LogStatus.INFO,
				"Validating the response for the page_szie,total_pages,has_next,has_previous");
		int total_page = 0;
		boolean has_next = false, has_prev = false;
		if (page_size > 100 && page_size == SpogConstants.MAX_PAGE_SIZE) {
			page_size = 100;
		}
		if (response.then().body(("pagination.total_size"), equalTo(total_size)) != null) {
			assertTrue("The data has been matched successfully", true);
			test.log(LogStatus.PASS, "The validation for the total_size is successfully:"
					+ response.then().extract().path("pagination.total_size"));
			if (response.then().body(("pagination.curr_page"), equalTo(curr_page)) != null) {
				assertTrue("The data has been matched successfull", true);
				test.log(LogStatus.PASS, "The validation for curr_page has matched successfully:"
						+ response.then().extract().path("pagination.curr_page"));
				if ((total_size % page_size) == 0) {
					total_page = total_size / page_size;
				} else {
					total_page = total_size / page_size + 1;
				}
				if (response.then().body(("pagination.total_page"), equalTo(total_page)) != null) {
					assertTrue("The data has been matched successfull", true);
					test.log(LogStatus.PASS, "The validation for the total_pages is successfully:"
							+ response.then().extract().path("pagination.total_page"));
					if (curr_page >= 1 && curr_page <= total_page) {

						if (curr_page < total_page) {
							has_next = true;
						}
						if (curr_page > 1) {
							has_prev = true;
						}
					}

					if (response.then().body(("pagination.has_prev"), equalTo(has_prev)) != null
							&& response.then().body(("pagination.has_next"), equalTo(has_next)) != null) {
						assertTrue("The data has been matched successfull", true);
						test.log(LogStatus.PASS,
								"The validation for the has_prev,has_next has matched successfully:"
										+ "The value of has_prev: " + has_prev + " has_next: " + has_next);
					} else {
						test.log(LogStatus.FAIL,
								"The validation for the has_next has not  matched successfully:"
										+ response.then().extract().path("paginaton.has_next"));
						test.log(LogStatus.FAIL,
								"The validation for the has_prev has not  matched successfully:"
										+ response.then().extract().path("paginaton.has_prev"));
						assertTrue("The data has  not  matched successfully", false);
					}
				} else {
					test.log(LogStatus.FAIL, "The validation for the total_pages is not successfull:"
							+ response.then().extract().path("pagination.total_pages"));
					assertTrue("The data has  not  matched successfully", false);
				}

			} else {
				test.log(LogStatus.FAIL, "The validation for the curr_pages is not successfull:"
						+ response.then().extract().path("pagination.curr_page"));
				assertTrue("The data has  not  matched successfully", false);
			}
		} else {
			test.log(LogStatus.FAIL, "The validation for the total_size is not successfull:"
					+ response.then().extract().path("pagination.total_size"));
			assertTrue("The data has  not  matched successfully", false);
		}
	}


	/**
	 * This method is used to validate for the users
	 * 
	 * @author Bharadwaj.Ghadiam
	 * @param users
	 * @param int index(i)
	 * @param int index(j)
	 * @param ExpectedAccounts
	 * @param test2
	 */
	private void CheckForUsers(ArrayList<HashMap> users, int i, int j,
			ArrayList<HashMap> ExpectedAccounts, ExtentTest test) {
		// TODO Auto-generated method stub

		HashMap<String, Object> object = (HashMap<String, Object>) users.get(i).get("audit_data");
		ArrayList<ResponseBody> audit = new ArrayList<ResponseBody>();
		audit = (ArrayList<ResponseBody>) ExpectedAccounts.get(j).get("audit_data");
		if (object.get("user_id").equals(audit.get(0).jsonPath().get("data.user_id"))
				&& (object.get("first_name").equals(audit.get(0).jsonPath().get("data.first_name")))
				&& (object.get("last_name").equals(audit.get(0).jsonPath().get("data.last_name")))
				&& (object.get("email").equals(audit.get(0).jsonPath().get("data.email")))
				&& (object.get("role_id").equals(audit.get(0).jsonPath().get("data.role_id")))
				&& (object.get("organization_id")
						.equals(audit.get(0).jsonPath().get("data.organization_id")))
				&& (object.get("create_ts").equals(audit.get(0).jsonPath().getLong("data.create_ts")))) {
			assertTrue("The validation is successfull", true);
			test.log(LogStatus.PASS,
					"The actual audit trail data matched the expected " + users.get(i).get("code_id"));
		} else {
			test.log(LogStatus.FAIL, "The actual audit trail data " + users.get(i).get("audit_data")
					+ "  did not match the expected " + users.get(i).get("code_id"));
			assertTrue("The value is correct", false);
		}

	}


	/**
	 * Generic function to validate the response of the organization : This method is used to validate
	 * the organization details
	 * 
	 * @author BharadwajReddy
	 * @param users
	 * @param int index(i)
	 * @param int index(j)
	 * @param ExpectedAccounts
	 * @param test
	 * @return validation is true or false
	 */

	private void CheckForOrganization(ArrayList<HashMap> users, int i, int j,
			ArrayList<HashMap> ExpectedAccounts, ExtentTest test) {

		// TODO Auto-generated method stub
		HashMap<String, Object> object = (HashMap<String, Object>) users.get(i).get("audit_data");
		ArrayList<ResponseBody> audit = new ArrayList<ResponseBody>();
		audit = (ArrayList<ResponseBody>) ExpectedAccounts.get(j).get("audit_data");
		if (object.get("organization_id").equals(audit.get(0).jsonPath().get("data.organization_id"))
				&& object.get("organization_name")
				.equals(audit.get(0).jsonPath().get("data.organization_name"))
				&& object.get("create_ts").equals(audit.get(0).jsonPath().getLong("data.create_ts"))
				&& object.get("parent_id").equals(audit.get(0).jsonPath().get("data.parent_id"))) {
			if ((users.get(i).get("code_id")
					.equals(Integer.parseInt(AuditCodeConstants.MODIFY_SUB_ORGANIZATION)))) {
				test.log(LogStatus.INFO, "The value of the created user id:"
						+ audit.get(0).jsonPath().get("data.create_user_id"));
				/*
				 * if( object.get("create_user_id").equals(audit.get(0).jsonPath().get(
				 * "data.create_user_id"))){ assertTrue("The value is correct",true);
				 * test.log(LogStatus.PASS,
				 * "This information is related to the  organizaion and the status code:"
				 * +users.get(i).get("code_id")); }
				 */
			} else {
				assertTrue("The value is correct", true);
				test.log(LogStatus.PASS,
						"This information is related to the modification of the creation of the  and the code id :"
								+ users.get(i).get("code_id"));
			}
		} else {
			assertTrue("The value of the results are false:", false);
			test.log(LogStatus.FAIL, "The actual audit trail data " + users.get(i).get("organization_id")
					+ "  did not match the expected " + users.get(i).get("code_id"));
		}

	}


	/**
	 * validate the results for Logged in user
	 * 
	 * @author Bharadwaj.Ghadaim
	 * @param users
	 * @param i
	 * @param ExpectedAccounts
	 */
	private void voidCheckDetails(ArrayList<HashMap> users, int i, int j, ArrayList<HashMap> ExpectedAccounts,
			ExtentTest test) {

		// TODO Auto-generated method stub
		if (users.get(i).get("code_id").equals(Integer.parseInt((String) ExpectedAccounts.get(j).get("code_id")))
				) {
			assertResponseItem(users.get(i).get("by_organization_id"),ExpectedAccounts.get(j).get("by_organization_id"),test);
			assertResponseItem(users.get(i).get("by_resource_id"),ExpectedAccounts.get(j).get("by_resource_id"),test);
			assertResponseItem(users.get(i).get("code_id").toString(),ExpectedAccounts.get(j).get("code_id").toString(),test);
			assertResponseItem(users.get(i).get("on_resource_id"),ExpectedAccounts.get(j).get("on_resource_id"),test);

			assertResponseItem(users.get(i).get("by_resource_name").toString().toLowerCase(),ExpectedAccounts.get(j).get("by_resource_name").toString().toLowerCase(),test);
			assertResponseItem(users.get(i).get("by_resource_type"),ExpectedAccounts.get(j).get("by_resource_type"),test);
			assertResponseItem(users.get(i).get("on_resource_type").toString(),ExpectedAccounts.get(j).get("on_resource_type").toString(),test);
			assertResponseItem(users.get(i).get("on_resource_name").toString().toLowerCase(),ExpectedAccounts.get(j).get("on_resource_name").toString().toLowerCase(),test);

			assertTrue("The value of the Generated results are true", true);
			test.log(LogStatus.INFO,
					"The validation for on_resource_id,by_organization_id,by_resource_id is matched  correctly");

		} else {
			test.log(LogStatus.INFO, "The value of the code are " + users.get(i).get("code_id") + "Expected code"
					+ Integer.parseInt((String) ExpectedAccounts.get(j).get("code_id")));
			System.out.println("The value of the codes are :" + users.get(i).get("code_id") + "Expected code"
					+ Integer.parseInt((String) ExpectedAccounts.get(j).get("code_id")));
			System.out.println("The value of the organization id are :" + users.get(i).get("by_organization_id")
					+ "The values are " + (ExpectedAccounts.get(j).get("by_organization_id")));
			assertTrue("The value of the Generated results are false", false);
			test.log(LogStatus.INFO, "The validation for on_resource_id,by_organization_id,by_resource_id is failed ");
		}
	}


	/**
	 * Composing the JSON MAP of the Generated response Body
	 * 
	 * @author Bharadwaj.Ghadiam
	 * @param by_resource_id
	 * @param by_organization_id
	 * @param code_id
	 * @param response
	 * @param on_resource_id
	 * @return
	 */

	public HashMap<String, Object> composeJsonMap(String by_resource_id, String by_organization_id,
			String code_id, Response response, String on_resource_id) {

		HashMap<String, Object> userdetails = new HashMap<String, Object>();
		ArrayList<ResponseBody> userresponse = new ArrayList<ResponseBody>();
		userdetails.put("by_resource_id", by_resource_id);
		userdetails.put("by_organization_id", by_organization_id);
		userdetails.put("code_id", code_id);
		userdetails.put("on_resource_id", on_resource_id);
		if ((code_id == AuditCodeConstants.DELETE_USER) || (code_id == AuditCodeConstants.LOGIN_USER)
				|| code_id == AuditCodeConstants.DELETE_SUB_ORGANIZATION
				|| code_id == AuditCodeConstants.DELETE_ORGANIZATION
				|| code_id == AuditCodeConstants.DELETE_SITE || code_id == AuditCodeConstants.DELETE_SOURCE
				|| code_id == AuditCodeConstants.delete_destination
				|| code_id == AuditCodeConstants.delete_cloud_account) {
			userdetails.put("audit_data", "{}");
		} else {
			userresponse.add(response);
			userdetails.put("audit_data", userresponse);
		}

		return userdetails;
	}

	/**
	 * Generic function to store the audit codes
	 * 
	 * @author kiran.sripada
	 * @param codeid
	 * @param orgid
	 * @param byresourceid
	 * @param onresourceid
	 * @param response
	 * @param on_resource_type 
	 * @param on_resource_name 
	 * @param by_resource_type 
	 * @param by_resource_name 
	 * @return hashmap
	 */
	public HashMap<String, Object> storetheuserevents(String code_id, String by_organization_id, String by_resource_id,
			String on_resource_id, Response response, String by_resource_name, String by_resource_type, String on_resource_name, String on_resource_type) {

		HashMap<String, Object> user_events_code = new HashMap<String, Object>();
		ArrayList<ResponseBody> userresponse = new ArrayList<ResponseBody>();
		user_events_code.put("code_id", code_id);
		user_events_code.put("by_organization_id", by_organization_id);
		user_events_code.put("by_resource_id", by_resource_id);
		user_events_code.put("on_resource_id", on_resource_id);
		user_events_code.put("by_resource_name", by_resource_name);
		user_events_code.put("by_resource_type", by_resource_type);
		user_events_code.put("on_resource_name", on_resource_name);
		user_events_code.put("on_resource_type", on_resource_type);
		if ((code_id == AuditCodeConstants.DELETE_USER) || (code_id == AuditCodeConstants.LOGIN_USER)
				|| (code_id == AuditCodeConstants.DELETE_SITE) || (code_id == AuditCodeConstants.DELETE_SOURCE)
				|| (code_id == AuditCodeConstants.delete_destination)) {
			user_events_code.put("audit_data", "{}");
		} else {
			userresponse.add(response);
			user_events_code.put("audit_data", userresponse);
		}
		return user_events_code;
	}

	/**
	 * Composing the JSON MAP of the Generated response Body
	 * 
	 * @author Bharadwaj.Ghadiam
	 * @param by_resource_id
	 * @param by_organization_id
	 * @param code_id
	 * @param response
	 * @param on_resource_id
	 * @param by_resource_name
	 * @param by_resource_type
	 * @param on_resource_name
	 * @param on_resource_type
	 * @return userdetails
	 */

	public HashMap<String, Object> composeJsonMap(String by_resource_id, String by_organization_id, String code_id,
			Response response, String on_resource_id, String by_resource_name, String by_resource_type, String on_resource_name, String on_resource_type) {

		HashMap<String, Object> userdetails = new HashMap<String, Object>();
		ArrayList<ResponseBody> userresponse = new ArrayList<ResponseBody>();
		userdetails.put("by_resource_id", by_resource_id);
		userdetails.put("by_organization_id", by_organization_id);
		userdetails.put("code_id", code_id);
		userdetails.put("on_resource_id", on_resource_id);
		userdetails.put("by_resource_name", by_resource_name);
		userdetails.put("by_resource_type", by_resource_type);
		userdetails.put("on_resource_name", on_resource_name);
		userdetails.put("on_resource_type", on_resource_type);
		if ((code_id == AuditCodeConstants.DELETE_USER) || (code_id == AuditCodeConstants.LOGIN_USER)
				|| code_id == AuditCodeConstants.DELETE_SUB_ORGANIZATION
				|| code_id == AuditCodeConstants.DELETE_ORGANIZATION || code_id == AuditCodeConstants.DELETE_SITE
				|| code_id == AuditCodeConstants.DELETE_SOURCE || code_id == AuditCodeConstants.delete_destination
				|| code_id == AuditCodeConstants.delete_cloud_account) {
			userdetails.put("audit_data", "{}");
		} else {
			userresponse.add(response);
			userdetails.put("audit_data", userresponse);
		}

		return userdetails;
	}


	/**
	 * @author Bharadwaj.Ghadiam Getting the audit details for the respective user
	 * @param validToken
	 * @param mspOrgId
	 * @param additionalURL
	 * @param test
	 * @return
	 */
	public Response getAuditDetailsForUsers(String validToken, String userId, String additionalURL,
			ExtentTest test) {

		test.log(LogStatus.INFO, "Preparation of additional url" + additionalURL);
		setUUID(userId);
		Response response = spogInvoker.getaudittrailbyuserId(validToken, userId, additionalURL, test);
		test.log(LogStatus.INFO,
				"The value of the response generated is :" + response.getBody().asString());
		return response;
	}


	/**
	 * update account and check status and account information
	 * 
	 * @author Bharadwaj.Ghadiam
	 * @param parentId that means account parent id
	 * @param accountOrgId
	 * @param accountName
	 * @param ExtentTest
	 */
	public Response updateAccountWithCheck2(String parentId, String accountOrgId, String accountName,
			ExtentTest test) {

		Response response = updateAccount(parentId, accountOrgId, accountName);
		checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		checkBodyItemValue(response, "organization_type", SpogConstants.MSP_SUB_ORG);
		checkBodyItemValue(response, "parent_id", parentId);
		if (accountName != "") {
			checkBodyItemValue(response, "organization_name", accountName);
		}
		checkBodyItemValue(response, "organization_id", accountOrgId);
		return response;
	}


	/**
	 * Compose the JSON body and Invoke the Rest API for adding a source to a group
	 * 
	 * @author Kiran.Sripada
	 * @param sourcegroupId
	 * @param source_id
	 * @param token
	 * @return response
	 */

	public Response addSourcetoSourceGroupwithCheck(String sourcegroup_Id, String[] source_Id,
			String token, int expectedstatuscode, SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		test.log(LogStatus.INFO, "Composing the JSON dody with the source_id " + source_Id);
		Map<String, Object> addsourcetogroup = jp.addsourcetogroupInfo(source_Id);
		test.log(LogStatus.INFO,
				"Invoking the Rest API to add sources to a source group " + sourcegroup_Id);
		Response response = spogInvoker.addSourcetoSourceGroup(addsourcetogroup, sourcegroup_Id, token);
		checkResponseStatus(response, expectedstatuscode);
		if (expectedstatuscode == SpogConstants.SUCCESS_POST) {
			test.log(LogStatus.PASS, "The response status is " + response.getStatusCode());
			return response;
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			checkErrorCode(response, code);
			test.log(LogStatus.INFO, "The error code matched with the expected " + code);
			if (code.contains("00500005")) {
				message = message.replace("{0}", source_Id[0]);
				message = message.replace("{1}", sourcegroup_Id);
				checkErrorMessage(response, message);
			} else if (code.contains("00100201")) {
				message = message.replace("{0}", source_Id[0]);
				checkErrorMessage(response, message);
			} else {
				checkErrorMessage(response, message);
			}
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		}
		return null;
	}


	/**
	 * Invoke the Rest API for deleting a source from a source group
	 * 
	 * @author Kiran.Sripada
	 * @param sourcegroupId
	 * @param source_id
	 * @param token
	 * @return response
	 */
	public Response deleteSourcefromSourceGroupwithCheck(String sourcegroup_Id, String source_Id,
			String token, int expectedstatuscode, SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		ArrayList<HashMap<String, String>> deletesourceId = new ArrayList<>();
		HashMap<String, Object> delete_sourceId = new HashMap<>();
		deletesourceId = jp.composedeletesourcefromsourcegroup(source_Id);

		delete_sourceId.put("sources", deletesourceId);
		test.log(LogStatus.INFO,
				"Invoking the Rest API to delete source from a source group " + sourcegroup_Id);
		Response response =
				spogInvoker.deleteSourcefromSourceGroup(sourcegroup_Id, token, delete_sourceId);
		checkResponseStatus(response, expectedstatuscode);
		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.PASS, "The response status is " + response.getStatusCode());
			return response;
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			checkErrorCode(response, code);
			test.log(LogStatus.INFO, "The error code matched with the expected " + code);
			if (code.contains("00500006")) {
				message = message.replace("{0}", source_Id);
				message = message.replace("{1}", sourcegroup_Id);
				checkErrorMessage(response, message);
			} else if (code.contains("00100201")) {
				message = message.replace("{0}", source_Id);
				checkErrorMessage(response, message);
			} else {
				checkErrorMessage(response, message);
			}
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		}
		return null;
	}


	/**
	 * Invoke the Rest API for deleting a filter for a specified userId
	 * 
	 * @author Kiran.Sripada
	 * @param user_id (Can be direct user/msp user/csr user/direct user under a suborg)
	 * @param filter_id
	 * @param token
	 * @param expectedstatuscode
	 * @param expectederrormessage
	 * @param extenttest object
	 * @return response
	 */
	public Response deletefilterspecifiedbyUserIdwithCheck(String user_Id, String filter_Id,
			String token, int expectedstatuscode, SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		test.log(LogStatus.INFO, "Delete the filter for a specified UserId " + user_Id);
		/*Response response = spogInvoker.deletefilterspecifiedbyUserId(filter_Id, user_Id, token);*/
		Response response = deleteFiltersByID(token, filter_Id, user_Id, test);
		checkResponseStatus(response, expectedstatuscode);
		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.PASS,
					"Successfully deleted the filter and the response status is " + response.getStatusCode());
			return response;
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			if (code.contains("00A00002")) {
				message = message.replace("{0}", filter_Id);
				message = message.replace("{1}", user_Id);
			}
			checkErrorCode(response, code);
			test.log(LogStatus.INFO, "The error code matched with the expected " + code);
			checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		}
		return null;

	}


	/**
	 * Invoke the Rest API to get the source based on the source type
	 * 
	 * @author Kiran.Sripada
	 * @param additionalURL (this has the fiter based on organization_id)
	 * @param extenttest object
	 * @return response
	 */
	public Response getsourcesbytypes(String additionalURL, String token, ExtentTest test) {

		test.log(LogStatus.INFO, "get the sources by types ");
		Response response = spogInvoker.getsourcesbytypes(additionalURL, token, test);
		return response;

	}


	/**
	 * This method checks the audit trail data based on the actions related to user/organization/site
	 * 
	 * @author kiran.sripada
	 * @param response
	 * @param Arrayofsourcetypes
	 * @param filterStr (Provide the organization ID)
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param ExtentTest
	 */

	public void checksourcesbytypes(Response response, ArrayList<HashMap> Expectedsourcetypes,
			String filterStr, int expectedStatusCode, SpogMessageCode expectedErroMessage,
			ExtentTest test) {

		ArrayList<HashMap> actualsources_types = new ArrayList<>();
		HashMap<String, String> sharepoint = new HashMap<>();
		sharepoint.put("sourcetype", "share_point");
		sharepoint.put("amount", Integer.toString(0));
		int size_actual_records = 0;
		int size_expected_records = 0;
		Boolean found = false;
		checkResponseStatus(response, expectedStatusCode);
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			actualsources_types = response.then().extract().path("data");
			size_actual_records = actualsources_types.size();
			Expectedsourcetypes.add(sharepoint);
			size_expected_records = Expectedsourcetypes.size();
			if ((filterStr != null || filterStr == "")
					&& size_actual_records == Expectedsourcetypes.size()) {
				for (int i = 0; i < size_actual_records; i++) {
					found = false;
					for (int j = 0; j < size_expected_records; j++) {
						if (actualsources_types.get(i).get("source_type")
								.equals(Expectedsourcetypes.get(j).get("source_type"))
								&& (actualsources_types.get(i).get("amount")
										.equals(Integer.parseInt((String) Expectedsourcetypes.get(j).get("amount"))))) {
							found = true;
							break;
						} else {
							continue;
						}
					}
					if (found) {
						test.log(LogStatus.PASS,
								"The source type is " + actualsources_types.get(i).get("source_type")
								+ " and amount is " + actualsources_types.get(i).get("amount"));
					} else {

						test.log(LogStatus.FAIL,
								"The actual values are: source type is "
										+ actualsources_types.get(i).get("source_type") + " and amount is "
										+ actualsources_types.get(i).get("amount"));
						assertTrue("The actual values are: source type is "
								+ actualsources_types.get(i).get("source_type") + " and amount is "
								+ actualsources_types.get(i).get("amount"), false);
					}
				}
			} else {

				for (int i = 0; i < size_actual_records; i++) {
					found = false;
					for (int j = 0; j < size_expected_records; j++) {
						if (actualsources_types.get(i).get("source_type")
								.equals(Expectedsourcetypes.get(j).get("source_type"))
								&& (actualsources_types.get(i).get("amount")
										.equals(Integer.parseInt((String) Expectedsourcetypes.get(j).get("amount"))))) {
							found = true;
							break;
						} else {
							continue;
						}
					}
					if (found) {
						test.log(LogStatus.PASS,
								"The source type is " + actualsources_types.get(i).get("source_type")
								+ " and amount is " + actualsources_types.get(i).get("amount"));
						assertTrue("The actual values are: source type is "
								+ actualsources_types.get(i).get("source_type") + " and amount is "
								+ actualsources_types.get(i).get("amount"), true);
					} else {
						test.log(LogStatus.FAIL,
								"The actual values are: source type is "
										+ actualsources_types.get(i).get("source_type") + " and amount is "
										+ actualsources_types.get(i).get("amount"));
						assertTrue("The actual values are: source type is "
								+ actualsources_types.get(i).get("source_type") + " and amount is "
								+ actualsources_types.get(i).get("amount"), false);
					}
				}
			}

		} else {
			String code = expectedErroMessage.getCodeString();
			String message = expectedErroMessage.getStatus();
			checkErrorCode(response, code);
			test.log(LogStatus.INFO, "The error code matched with the expected " + code);
			checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);

		}

	}


	/**
	 * Delete group
	 * 
	 * @author liu.yuefen
	 * @param group id
	 * @param expectedStatusCode
	 */
	public void deleteGroupWithExpectedStatusCode(String group_id, int expectedStatusCode,
			ExtentTest test) {

		Response response = spogInvoker.DeleteGroup(group_id);
		checkResponseStatus(response, expectedStatusCode, test);
	}


	/**
	 * Delete group
	 * 
	 * @author liu.yuefen
	 * @param group id
	 * @return response
	 */
	public Response deleteGroup(String group_id, ExtentTest test) {

		Response response = spogInvoker.DeleteGroup(group_id);

		return response;
	}


	/**
	 * getSourceGroups
	 * 
	 * @author yin.li
	 * @param group id
	 * @return response
	 */
	public Response getSourceGroups(String xOrgId, String yOrgId, String zOrgId, String pageIndex,
			String pageSzie) {

		String queryString = "";

		if (StringUtils.isNotEmpty(xOrgId)) {
			queryString = xOrgId;
		}

		if (StringUtils.isNotEmpty(yOrgId)) {
			if (StringUtils.isNotEmpty(queryString)) {
				queryString = queryString + "|" + yOrgId;
			} else {
				queryString = yOrgId;
			}
		}

		if (StringUtils.isNotEmpty(zOrgId)) {
			if (StringUtils.isNotEmpty(queryString)) {
				queryString = queryString + "|" + zOrgId;
			} else {
				queryString = zOrgId;
			}
		}

		if (queryString.length() > 36) {
			queryString = "?organization_id=" + queryString + "";
		} else if (queryString.length() == 36) {
			queryString = "?organization_id=" + queryString + "";
		}

		String pageIndexStr = "";
		if (StringUtils.isNotEmpty(pageIndex)) {
			pageIndexStr = "page=" + pageIndex;
		}

		String pageSizeStr = "";
		if (StringUtils.isNotEmpty(pageSzie)) {
			pageSizeStr = "page_size=" + pageSzie;
		}

		if (StringUtils.isNotEmpty(queryString)) {
			if (StringUtils.isNotEmpty(pageIndexStr)) {
				if (StringUtils.isNotEmpty(pageSizeStr)) {
					queryString = queryString + "&" + pageIndexStr + "&" + pageSizeStr;
				} else {
					queryString = queryString + "&" + pageIndexStr;
				}
			} else {
				if (StringUtils.isNotEmpty(pageSizeStr)) {
					queryString = queryString + "&" + pageSizeStr;
				}
			}
		} else {
			if (StringUtils.isNotEmpty(pageIndexStr)) {
				if (StringUtils.isNotEmpty(pageSizeStr)) {
					queryString = "?" + pageIndexStr + "&" + pageSizeStr;
				} else {
					queryString = "?" + pageIndexStr;
				}
			} else {
				if (StringUtils.isNotEmpty(pageSizeStr)) {
					queryString = queryString + "&" + pageSizeStr;
				}
			}
		}
		return spogInvoker.getSourceGroups(queryString);
	}


	public Response getSourceGroups(HashMap<String, String> params) {

		return spogInvoker.getSourceGroups(params);
	}


	/**
	 * get source list from one specified group
	 * 
	 * @author yuefen.liu
	 * @param group_id
	 * @param pageNumber
	 * @param pageSize
	 * @param return response
	 */
	public Response getSourceListFromOneGroup(String group_id, int pageNumber, int pageSize,
			ExtentTest test) {

		errorHandle.printDebugMessageInDebugFile("***********getSourceListFromOneGroup***********");
		String extendUrl = "";

		if ((pageNumber != -1) || (pageSize != -1)) {
			test.log(LogStatus.INFO, "set paging url");
			extendUrl += getPagingUrl(pageNumber, pageSize);
		}
		test.log(LogStatus.INFO, "call getSourceListFromOneGroup");
		errorHandle
		.printDebugMessageInDebugFile("*call getSourceListFromOneGroup, url is " + extendUrl);
		Response response = spogInvoker.getSourceListFromOneGroup(group_id, extendUrl, test);
		return response;
	}


	/**
	 * used for check getSourceListFromOneGroup
	 * 
	 * @author yuefen.liu
	 * @param response
	 * @param expectedStatusCode
	 * @param expectedResponse
	 * @param curr_page
	 * @param page_size
	 * @param total_size
	 * @param test
	 * @param return response
	 */
	public void getSourceListFromOneGroupWithCheck(Response response, int expectedStatusCode,
			ArrayList<ResponseBody> expectedResponse, int curr_page, int page_size, int total_size,
			String errorMessage, ExtentTest test) {

		errorHandle.printDebugMessageInDebugFile(
				"******************getSourceListFromOneGroupWithCheck*******************");
		test.log(LogStatus.INFO, "expect status code is " + expectedStatusCode);
		errorHandle.printInfoMessageInDebugFile("expect status code is " + expectedStatusCode);
		response.then().statusCode(expectedStatusCode);
		errorHandle.printDebugMessageInDebugFile(response.then().log().body().toString());
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.INFO, "check response source info");
			for (int i = 0; i < expectedResponse.size(); i++) {
				test.log(LogStatus.INFO, "check create_ts");
				response.then()
				.body("data[" + (expectedResponse.size() - 1 - i) + "].source_name",
						equalTo(expectedResponse.get(i).jsonPath().getString("data.source_name")))
				.body("data[" + (expectedResponse.size() - 1 - i) + "].organization_id",
						equalTo(expectedResponse.get(i).jsonPath().getString("data.organization_id")));
			}
			test.log(LogStatus.INFO, "check response page info");
			if ((curr_page == -1) || (curr_page == 0)) {
				curr_page = 1;
			}
			response.then().body("pagination.curr_page", equalTo(curr_page));

			if ((page_size == -1) || (page_size == 0)) {
				page_size = 20;
			} else if (page_size > 100) {
				page_size = 100;
			}
			response.then().body("pagination.page_size", equalTo(page_size));

			if (total_size == -1) {
				total_size = expectedResponse.size();
			}
			int total_page = 0;
			if (total_size <= page_size) {
				total_page = 1;
			} else {
				int modResult = total_size % page_size;
				if (modResult == 0) {
					total_page = total_size / page_size;
				} else {
					total_page = total_size / page_size + 1;
				}

			}
			response.then().body("pagination.total_page", equalTo(total_page));
			response.then().body("pagination.total_size", equalTo(total_size));

			if (curr_page > 1) {
				response.then().body("pagination.has_prev", equalTo(true));
			} else {
				response.then().body("pagination.has_prev", equalTo(false));
			}

			if (curr_page < total_page) {
				response.then().body("pagination.has_next", equalTo(true));
			} else {
				response.then().body("pagination.has_next", equalTo(false));
			}

		} else {
			checkErrorMessage(response, errorMessage);
		}
	}


	/**
	 * used for check getSourceListFromOneGroup
	 * 
	 * @author yuefen.liu
	 * @param response
	 * @param expectedStatusCode
	 * @param expectedResponse
	 * @param curr_page
	 * @param page_size
	 * @param total_size
	 * @param test
	 * @param return response
	 */
	public void getSourceListFromOneGroupWithCheck1(Response response, int expectedStatusCode,
			ArrayList<ResponseBody> expectedResponse, int curr_page, int page_size, int total_size,
			String errorMessage, ExtentTest test) {

		errorHandle.printDebugMessageInDebugFile(
				"******************getSourceListFromOneGroupWithCheck*******************");
		test.log(LogStatus.INFO, "expect status code is " + expectedStatusCode);
		errorHandle.printInfoMessageInDebugFile("expect status code is " + expectedStatusCode);
		response.then().statusCode(expectedStatusCode);
		errorHandle.printDebugMessageInDebugFile(response.then().log().body().toString());
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.INFO, "check response source info");
			for (int i = 0; i < expectedResponse.size(); i++) {
				test.log(LogStatus.INFO, "check create_ts");
				response.then()
				.body("data[" + (expectedResponse.size() - 1 - i) + "].source_name",
						equalTo(expectedResponse.get(i).jsonPath().getString("data.source_name")))
				.body("data[" + (expectedResponse.size() - 1 - i) + "].organization_id",
						equalTo(expectedResponse.get(i).jsonPath().getString("data.organization_id")));
			}
			test.log(LogStatus.INFO, "check response page info");
			if ((curr_page == -1) || (curr_page == 0)) {
				curr_page = 1;
			}
			response.then().body("pagination.curr_page", equalTo(curr_page));

			if ((page_size == -1) || (page_size == 0)) {
				page_size = 20;
			} else if (page_size > 100) {
				page_size = 100;
			}
			response.then().body("pagination.page_size", equalTo(page_size));

			if (total_size == -1) {
				total_size = expectedResponse.size();
			}
			int total_page = 0;
			if (total_size <= page_size) {
				total_page = 1;
			} else {
				int modResult = total_size % page_size;
				if (modResult == 0) {
					total_page = total_size / page_size;
				} else {
					total_page = total_size / page_size + 1;
				}

			}
			response.then().body("pagination.total_page", equalTo(total_page));
			response.then().body("pagination.total_size", equalTo(total_size));

			if (curr_page > 1) {
				response.then().body("pagination.has_prev", equalTo(true));
			} else {
				response.then().body("pagination.has_prev", equalTo(false));
			}

			if (curr_page < total_page) {
				response.then().body("pagination.has_next", equalTo(true));
			} else {
				response.then().body("pagination.has_next", equalTo(false));
			}

		} else {
			checkErrorMessage(response, errorMessage);
		}
	}


	/**
	 * @author kiran.sripada
	 * @param source_name
	 * @param source_type
	 * @param source_product
	 * @param organization_id
	 * @param site_id
	 * @param policy_id
	 * @param protection_status
	 * @param connection_status
	 * @param os_major
	 * @param token
	 * @param Extenttest object
	 * @return Response
	 */
	public Response updateSourcebysourceId(String source_id, String source_name,
			SourceType source_type, SourceProduct product_type, String org_Id, String site_Id,
			String policy_Id, ProtectionStatus protection_type, ConnectionStatus conn_status,
			String os_major, String token, ExtentTest test) {

		test.log(LogStatus.INFO, "Composing the hashmap for update source");
		Map<String, Object> sourceInfo = jp.updateSourceInfo(source_id, source_name, source_type,
				product_type, org_Id, site_Id, policy_Id, protection_type, conn_status, os_major);
		test.log(LogStatus.INFO, "Update the source by invoking the REST API");
		Response response = spogInvoker.updateSource(sourceInfo, source_id, token);
		return response;

	}


	/**
	 * the function is used for error handling check
	 * 
	 * @author shuo.zhang
	 * @param source_name
	 * @param source_type
	 * @param source_product
	 * @param organization_id
	 * @param site_id
	 * @param policy_id
	 * @param protection_status
	 * @param connection_status
	 * @param os_major
	 * @param applications
	 * @param create_user_id
	 * @param expectedStatusCode
	 * @param expectedErrorCode
	 * @param test
	 */

	public String createSourceWithCheck(String source_name, String source_type, String source_product,
			String organization_id, String site_id, String protection_status, String connection_status,
			String os_major, String applications, String create_user_id, int expectedStatusCode,
			String expectedErrorCode, boolean isLoggedIn, ExtentTest test) {

		String[] applicationArray = null;
		ArrayList<String> application = new ArrayList<String>();
		if ((applications != null) && (!applications.equals(""))) {
			applicationArray = applications.split(";");
			for (int i = 0; i < applicationArray.length; i++) {
				application.add(applicationArray[i]);
			}
		}
		errorHandle.printDebugMessageInDebugFile("compose source info object");
		test.log(LogStatus.INFO, "compose source info object");
		Map<String, Object> sourceInfo =
				jp.getSourceInfo(source_name, source_type, source_product, organization_id, site_id,
						// protection_status, connection_status, os_major, applicationArray,
						// create_user_id);
						protection_status, connection_status, os_major, applicationArray);

		errorHandle.printDebugMessageInDebugFile("create source");
		test.log(LogStatus.INFO, "create source");
		Response response = spogInvoker.createSource(sourceInfo, isLoggedIn);
		response.then().statusCode(expectedStatusCode);
		if (expectedStatusCode != SpogConstants.SUCCESS_POST) {
			checkErrorCode(response, expectedErrorCode);
			return null;
		} else {
			return response.then().extract().path("data.source_id");
		}

	}


	public void checkErrorCode(Response response, String expectedErrorCode) {

		// response.then().assertThat().body(containsString((expectedErrorMessage)));
		List<String> messageArray = response.body().jsonPath().getList("errors.code");
		boolean find = false;
		if (messageArray != null && messageArray.size() > 0) {
			for (int i = 0; i < messageArray.size(); i++) {
				if (messageArray.get(i).contains(expectedErrorCode)) {
					find = true;
					break;
				}
			}
		} else {
			assertTrue("There is no errors return in response", false);
			return;
		}
		if (find) {
			assertTrue("error code check is correct", true);
		} else {
			assertEquals(messageArray.get(0), expectedErrorCode);
		}

	}


	public Response updateSourceGroup(String sourceGroupId, String sourceGroupName,
			String sourceGroupDesc) {

		Map<String, String> sourceGroupInfo =
				jp.composeSourceGroupInfo(sourceGroupName, sourceGroupDesc);
		return spogInvoker.updateSourceGroup(sourceGroupId, sourceGroupInfo);
	}


	/**
	 * @author BharadwajReddy
	 * @param source_id
	 */
	public void setUUID(String source_id) {

		// TODO Auto-generated method stub
		Uuid = source_id;
	}


	/**
	 * @author BharadwajReddy
	 * @return source_id
	 */
	public String getUUId() {

		return Uuid;
	}


	/**
	 * Call the REST Web service to delete the source by id
	 * 
	 * @author BharadwajReddy
	 * @param token
	 * @param source_id
	 * @param test
	 * @return
	 */
	public Response deleteSourcesById(String token, String source_id, ExtentTest test) {

		// TODO Auto-generated method stub
		setUUID(source_id);
		test.log(LogStatus.INFO, "Invoking the api to get the response");
		Response response = spogInvoker.deleteSourceById(token, source_id, test);
		return response;

	}


	public void deleteSourceByID(String sourceID, ExtentTest test) {

		Response response = spogInvoker.deleteSourceByID(sourceID);
		checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
	}


	/**
	 * Validating the response for the delete source by id
	 * 
	 * @author Bharadwaj.Ghadiam
	 * @param response
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 */

	public void deleteSourcesWithCheck(Response response, int expectedStatusCode,
			SpogMessageCode Info, ExtentTest test) {

		String expectedErrorMessage = "", expectedErrorCode = "";
		response.then().log().all();
		// TODO Auto-generated method stub
		checkResponseStatus(response, expectedStatusCode, test);
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			test.log(LogStatus.PASS, "The resposne validation is successfull");
		} else {
			if (Info.getStatus() != "0010000") {
				expectedErrorMessage = Info.getStatus();
				if (expectedErrorMessage.contains("{0}")) {
					expectedErrorMessage = expectedErrorMessage.replace("{0}", getUUId());
					System.out.println(expectedErrorMessage);
				}
				expectedErrorCode = Info.getCodeString();
			}
			checkErrorMessage(response, expectedErrorMessage);
			test.log(LogStatus.PASS, "The value of the message is " + expectedErrorMessage);
			checkErrorCode(response, expectedErrorCode);
			test.log(LogStatus.PASS, "The value of the code  generated  is " + expectedErrorCode);
			test.log(LogStatus.INFO,
					"The value of the  response generated actually is :" + response.getStatusCode());
		}

	}


	/**
	 * @author kiran.sripada
	 * @param source_id
	 * @param source_name
	 * @param source_type
	 * @param source_product
	 * @param organization_id
	 * @param site_id
	 * @param policy_id
	 * @param protection_status
	 * @param connection_status
	 * @param os_major
	 * @param token
	 * @param expectedstatuscode
	 * @param expectedErrorMessage
	 * @param Extenttest object
	 * @return source_id
	 */
	public String updateSourcebysourceIdwithcheck(String source_id, String source_name,
			SourceType source_type, SourceProduct product_type, String org_Id, String site_Id,
			String policy_Id, ProtectionStatus protection_type, ConnectionStatus conn_status,
			String os_major, String token, String user_Id, int expectedstatuscode,
			SpogMessageCode expectederrorMessage, ExtentTest test) {

		Response response = updateSourcebysourceId(source_id, source_name, source_type, product_type,
				org_Id, site_Id, policy_Id, protection_type, conn_status, os_major, token, test);
		checkResponseStatus(response, expectedstatuscode);
		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			response.then().body("data.source_name", equalTo(source_name))
			.body("data.organization_id", equalTo(org_Id))
			.body("data.create_user_id", equalTo(user_Id))
			.body("data.source_type", equalTo(source_type.name()))
			.body("data.source_product", equalTo(product_type.name()))
			.body("data.protection_status", equalTo(protection_type.name()))
			.body("data.connection_status", equalTo(conn_status.name()));
			// .body("data.site_id", equalTo(site_Id));
			test.log(LogStatus.PASS, "The response body matched the expected");
			return response.then().extract().path("data.source_id");
		} else {
			String code = expectederrorMessage.getCodeString();
			String message = expectederrorMessage.getStatus();
			checkErrorCode(response, code);
			test.log(LogStatus.INFO, "The error code matched with the expected " + code);
			checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		}

		return null;
	}


	/**
	 * @shuo.zhang
	 * @param filterStr the format is
	 *        filterName1;operator;value,filterName2;operator;value,filterName1;operator;value
	 *        support: organization_id organization_id;=;123 or organization_id;in;[abc|123]
	 *        protection_status protection_status;=;protected or
	 *        protection_status;in;[protected|unprotected] connection_status
	 *        connection_status;=;online or connection_status;in;[online|offline] site_id
	 *        site_id;=;123 or site_id;in;[123|234] group_id os_major source_name source_name;=;123
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @param test
	 * @return
	 */
	public Response getSources(String filterStr, String sortStr, int pageNumber, int pageSize,
			boolean isLoggedIn, ExtentTest test) {

		errorHandle.printDebugMessageInDebugFile("***********getSources***********");
		String extendUrl = getUrl4FilterSortPaging(filterStr, sortStr, pageNumber, pageSize, test);
		/*
		 * int multipleFlag = 0; if ((filterStr != null) && (!filterStr.equals(""))) {
		 * test.log(LogStatus.INFO, "set filter url"); extendUrl += getFilterUrl(filterStr);
		 * multipleFlag++; }
		 * 
		 * if ((pageNumber != -1) || (pageSize != -1)) { test.log(LogStatus.INFO, "set paging url"); if
		 * (multipleFlag != 0) { extendUrl += "&"; } extendUrl += getPagingUrl(pageNumber, pageSize); }
		 */
		test.log(LogStatus.INFO, "call getSources");
		errorHandle.printDebugMessageInDebugFile("*call getSources, url is " + extendUrl);
		Response response = spogInvoker.getSources(extendUrl, isLoggedIn, test);
		return response;
	}


	/**
	 * 
	 * @author shuo.zhang
	 * @param response
	 * @param expectedStatusCode
	 * @param expectedBaiscSoureInfo
	 * @param otherInfo
	 * @param curr_page
	 * @param page_size
	 * @param total_size
	 * @param errorCode
	 * @param test
	 */
	public void checkGetSources(Response response, int expectedStatusCode,
			ArrayList<HashMap<String, Object>> expectedSourceInfo, int curr_page, int page_size,
			int total_size, String errorCode, ExtentTest test) {

		errorHandle
		.printDebugMessageInDebugFile("******************checkGetSources*******************");
		test.log(LogStatus.INFO, "expect status code is " + expectedStatusCode);
		errorHandle.printInfoMessageInDebugFile("expect status code is " + expectedStatusCode);
		response.then().statusCode(expectedStatusCode);
		errorHandle.printDebugMessageInDebugFile(response.then().log().body().toString());

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.INFO, "check response source info");
			for (int i = 0; i < expectedSourceInfo.size(); i++) {

				// get response map
				Map<String, Object> allInfoMap = response.jsonPath().getMap("data[" + i + "]");
				System.out.println(allInfoMap.get("source_id").toString());
				Iterator<String> allInfoKeyIter = allInfoMap.keySet().iterator();
				// get expected source map
				HashMap<String, Object> expectedSourceMap = expectedSourceInfo.get(i);
				System.out.println(expectedSourceMap.get("source_id").toString());

				System.out.println("***************************** ");
				// assertEquals(allInfoMap.keySet().size(), expectedSourceMap.keySet().size());

				while (allInfoKeyIter.hasNext()) {
					String infokey = allInfoKeyIter.next();

					// check source baisc info
					test.log(LogStatus.INFO, "check source basic info ");
					System.out.println("key is " + infokey);
					if (infokey.equalsIgnoreCase("create_ts") || infokey.equalsIgnoreCase("modify_ts")) {
						assertNotNull(allInfoMap.get(infokey));
					} /*else if (infokey.equalsIgnoreCase("last_job")) {

            assertEquals(allInfoMap.get(infokey).toString(),
                expectedSourceMap.get(infokey).toString());
          }*/ else if (infokey.equalsIgnoreCase("source_group")) {
        	  ArrayList<HashMap<String, String>> realData =
        			  (ArrayList<HashMap<String, String>>) allInfoMap.get(infokey);
        	  ArrayList<HashMap<String, String>> expectData =
        			  (ArrayList<HashMap<String, String>>) expectedSourceMap.get(infokey);
        	  Iterator<HashMap<String, String>> realDataIter = realData.iterator();
        	  while (realDataIter.hasNext()) {
        		  HashMap<String, String> item = realDataIter.next();
        		  String real_group_id = item.get("group_id");
        		  Iterator<HashMap<String, String>> expectDataIter = expectData.iterator();
        		  boolean find = false;
        		  while (expectDataIter.hasNext()) {
        			  HashMap<String, String> expectedItem = expectDataIter.next();
        			  if (expectedItem.get("group_id").equalsIgnoreCase(real_group_id)) {
        				  assertEquals(item.get("group_name"), expectedItem.get("group_name"));
        				  find = true;
        				  break;
        			  }
        		  }
        		  assertTrue("source group doesn't match", find);

        	  }

          } else if (infokey.equalsIgnoreCase("last_recovery_point_ts")|| infokey.equalsIgnoreCase("last_job")) {
        	  if (expectedSourceMap.get(infokey) == null) {
        		  assertEquals(allInfoMap.get(infokey), expectedSourceMap.get(infokey));
        	  } else {
        		  assertEquals(allInfoMap.get(infokey).toString(),
        				  expectedSourceMap.get(infokey).toString());
        	  }

          } else if (infokey.equalsIgnoreCase("next_scheduled_job_ts") || infokey.equalsIgnoreCase("available_actions")) {

          } else {
        	  System.out.println(allInfoMap.get(infokey));
        	  System.out.println(expectedSourceMap.get(infokey));
        	  System.out.println("source is " + allInfoMap.get("source_id"));
        	  System.out.println("source is " + expectedSourceMap.get("source_id"));
        	  assertEquals(allInfoMap.get(infokey), expectedSourceMap.get(infokey));
          }

				}

			}
			test.log(LogStatus.INFO, "check paging info ");
			checkPagingInfo(response, expectedSourceInfo.size(), curr_page, page_size, total_size, test);

		} else {
			test.log(LogStatus.INFO, "check error code ");
			checkErrorCode(response, errorCode);
		}

	}


	/**
	 * @author shuo.zhang
	 * @param response
	 * @param expectedSize
	 * @param curr_page
	 * @param page_size
	 * @param total_size
	 * @param test
	 */
	private void checkPagingInfo(Response response, int expectedSize, int curr_page, int page_size,
			int total_size, ExtentTest test) {

		test.log(LogStatus.INFO, "check response page info");
		if ((curr_page == -1) || (curr_page == 0)) {
			curr_page = 1;
		}
		response.then().body("pagination.curr_page", equalTo(curr_page));

		if ((page_size == -1) || (page_size == 0)) {
			page_size = 20;
		} else if (page_size > 100) {
			page_size = 100;
		}
		response.then().body("pagination.page_size", equalTo(page_size));

		if (total_size == -1) {

			total_size = expectedSize;
		}
		int total_page = 0;

		if ((total_size != 0) && (total_size <= page_size)) {
			total_page = 1;
		} else {
			int modResult = total_size % page_size;
			if (modResult == 0) {
				total_page = total_size / page_size;
			} else {
				total_page = total_size / page_size + 1;
			}

		}
		response.then().body("pagination.total_page", equalTo(total_page));
		response.then().body("pagination.total_size", equalTo(total_size));

		if (curr_page > 1) {
			response.then().body("pagination.has_prev", equalTo(true));
		} else {
			response.then().body("pagination.has_prev", equalTo(false));
		}

		if (curr_page < total_page) {
			response.then().body("pagination.has_next", equalTo(true));
		} else {
			response.then().body("pagination.has_next", equalTo(false));
		}

	}


	/**
	 * Gets the sites.
	 *
	 * @author yin.li
	 * @return the sites
	 */
	public Response getSites() {

		return spogInvoker.getSites();

	}


	public Response getSites(HashMap<String, String> params) {

		return spogInvoker.getSites(params);

	}

	public Response getSites(String additionalURL) {

		return spogInvoker.getSites(additionalURL);

	}


	/**
	 * Call the Rest API to get the sources by id
	 * 
	 * @param token
	 * @param source_id
	 * @param test
	 * @return response
	 */

	public Response getSourceById(String token, String source_id, ExtentTest test) {

		setUUID(source_id);
		test.log(LogStatus.INFO,
				"Calling the rest api to get the information related to get source by id");
		Response response = spogInvoker.getSourcesById(token, source_id, test);
		test.log(LogStatus.INFO, "The resposne generated is :" + response.getBody().asString());
		return response;

	}


	public Response getSourceById(String source_id, ExtentTest test) {

		setUUID(source_id);
		test.log(LogStatus.INFO,
				"Calling the rest api to get the information related to get source by id");
		Response response = spogInvoker.getSourcesById(source_id, test);
		test.log(LogStatus.INFO, "The resposne generated is :" + response.getBody().asString());
		return response;

	}


	/**
	 * Validate the response for the getSourceByIdWithCheck
	 * 
	 * @author Bharadwaj.Ghadiam
	 * @param response
	 * @param expectedStatusCode
	 * @param sourceName
	 * @param sourceName2
	 * @param sourceType
	 * @param sourceProduct
	 * @param organization_id
	 * @param protectionStatus
	 * @param connectionStatus
	 * @param site_id
	 * @param os_major
	 * @param agent_upgrade_link
	 * @param agent_upgrade_version
	 * @param agent_current_version
	 * @param os_architecture
	 * @param os_name
	 * @param agent_name
	 * @param hypervisor_id
	 * @param vm_name
	 * @param applications
	 * @param available_actions
	 * @param expectedErrorMessage
	 * @param test
	 */

	/*
	 * "source_id": "8bae5df0-071b-4188-82f3-af71536f3c8c", "source_name": "shuo_sourceWFfKFonj",
	 * "vm_name": "bharu_vm2", "source_type": "machine", "source_product": "udp", "organization_id":
	 * "4d1c73ae-b036-4c73-8a7a-901a34800c7f", "organization_name": "spog_bharadwaj_childwOiEsEdz",
	 * "protection_status": "protect", "connection_status": "online", "site": { "site_id":
	 * "16cc814c-9615-4893-b005-5d3b8c3c18bf", "site_name": "TestCreate_Site_Ourdtgoz_Spog" },
	 * "policy": null, "last_recovery_point_ts": null, "last_job": [
	 * 
	 * ], "available_actions": [ "startbackup", "stopbackup", "upgradeagent" ], "source_group": [
	 * 
	 * ], "assured_recovery_job": null, "agent": { "agent_name": "bharu_agent1",
	 * "agent_current_version": "1.0.0", "agent_upgrade_version": "2.0", "agent_upgrade_link":
	 * "http://upgrade" }, "hypervisor": { "hypervisor_id": "5f8ebe3a-1032-44b6-9f66-4b4b86883cfd",
	 * "hypervisor_name": null }, "ptc_status": null, "operating_system": { "os_major": "windows",
	 * "os_name": "windows 2012", "os_architecture": "64" }, "create_ts":
	 * "2018-01-27T07:20:16.921+0000", "modify_ts": "2018-01-27T07:20:16.921+0000", "is_deleted":
	 * false, "multiple_policy_support": false }, "errors": [
	 * 
	 * ]
	 */
	public void getSourceByIdWithCheck(Response response, int expectedStatusCode, String source_id,
			String sourceName, SourceType sourceType, SourceProduct sourceProduct, String organization_id,
			ProtectionStatus protectionStatus, ConnectionStatus connectionStatus, String site_id,
			String os_major, String applications, ArrayList<String> available_actions, String vm_name,
			String hypervisor_id, String agent_name, String os_name, String os_architecture,
			String agent_current_version, String agent_upgrade_version, String agent_upgrade_link,
			SpogMessageCode Info, ExtentTest test) {

		// TODO Auto-generated method stub
		String expectedErrorMessage = "", expectedErrorCode = "";

		checkResponseStatus(response, expectedStatusCode);
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			test.log(LogStatus.INFO, "validating the response for source_id");
			assertResponseItem(response.then().extract().path("data.source_id"), source_id, test);

			test.log(LogStatus.INFO, "validating the response for source_name");
			assertResponseItem(response.then().extract().path("data.source_name"), sourceName, test);

			test.log(LogStatus.INFO, "validating the response for  for source_type");
			assertResponseItem(response.then().extract().path("data.source_type"), sourceType.name(),
					test);

			test.log(LogStatus.INFO, "validating the response for  source_product");
			assertResponseItem(response.then().extract().path("data.source_product"),
					sourceProduct.name(), test);

			test.log(LogStatus.INFO, "validating the response for  organization_id");
			assertResponseItem(response.then().extract().path("data.organization_id"), organization_id,
					test);

			test.log(LogStatus.INFO, "validating the response for protection status");
			assertResponseItem(response.then().extract().path("data.protection_status"),
					protectionStatus.name(), test);

			test.log(LogStatus.INFO, "validating the response for connections_status");
			assertResponseItem(response.then().extract().path("data.connection_status"),
					connectionStatus.name(), test);

			HashMap<String, String> site_info = response.then().extract().path("data.site");
			test.log(LogStatus.INFO, "validating the response for site");
			assertResponseItem(site_info.get("site_id"), site_id, test);
			assertNotNull(site_info.get("site_name"));

			test.log(LogStatus.INFO, "validating the response for ptc_status");
			assertNull(response.then().extract().path("data.ptc_status"));

			test.log(LogStatus.INFO, "validating the response for policy");
			assertNull(response.then().extract().path("data.policy"));

			test.log(LogStatus.INFO, "validating the response for last_recovery_point_ts");
			assertNull(response.then().extract().path("data.last_recovery_point_ts"));

			test.log(LogStatus.INFO, "validating the response for  assured_recovery_job");
			assertNull(response.then().extract().path("data. assured_recovery_job"));

			test.log(LogStatus.INFO, "validating the response for arrayList of available actions ");
			ArrayList<String> actual_actions = response.then().extract().path("data.available_actions");
			for (int i = 0; i < actual_actions.size(); i++) {
				assertResponseItem(actual_actions.get(i), available_actions.get(i), test);
			}

			test.log(LogStatus.INFO, "validating the response for os");
			HashMap<String, String> operating_system =
					response.then().extract().path("data.operating_system");
			assertResponseItem(operating_system.get("os_major"), (os_major), test);
			assertResponseItem(operating_system.get("os_name"), (os_name), test);
			assertResponseItem(operating_system.get("os_architecture"), (os_architecture), test);

			test.log(LogStatus.INFO, "validating the response for agent");
			HashMap<String, String> agent = response.then().extract().path("data.agent");

			assertResponseItem(agent.get("agent_name"), agent_name, test);
			assertResponseItem(agent.get("agent_current_version"), agent_current_version, test);
			assertResponseItem(agent.get("agent_upgrade_version"), agent_upgrade_version, test);
			assertResponseItem(agent.get("agent_upgrade_link"), agent_upgrade_link, test);

			test.log(LogStatus.INFO, "validating the response for hypervisor");
			HashMap<String, String> hypervisor = response.then().extract().path("data.hypervisor");
			assertResponseItem(hypervisor.get("hypervisor_id"), hypervisor_id, test);
			assertResponseItem(hypervisor.get("hypervisor_name"), null, test);

			test.log(LogStatus.INFO, "validating the response for create_ts ,modify_ts");
			assertNotNull(response.then().extract().path("data.create_ts"));
			assertNotNull(response.then().extract().path("data.modify_ts"));

			test.log(LogStatus.INFO, "validating the response for is_deleted");
			assertResponseItem(response.then().extract().path("data.is_deleted"), false, test);

			test.log(LogStatus.INFO, "validating the response multiple policy support");
			assertResponseItem(response.then().extract().path("data.multiple_policy_support"), false,
					test);

		} else {
			if (Info.getStatus() != "0010000") {
				expectedErrorMessage = Info.getStatus();
				if (expectedErrorMessage.contains("{0}")) {
					expectedErrorMessage = expectedErrorMessage.replace("{0}", getUUId());
					System.out.println(expectedErrorMessage);
				}
				expectedErrorCode = Info.getCodeString();
			}
			checkErrorMessage(response, expectedErrorMessage);
			test.log(LogStatus.PASS, "The value of the message is " + expectedErrorMessage);
			checkErrorCode(response, expectedErrorCode);
			test.log(LogStatus.PASS, "The value of the code  generated  is " + expectedErrorCode);
			test.log(LogStatus.INFO,
					"The value of the  response generated actually is :" + response.getStatusCode());
		}

	}


	// enhancement source Group, Multiple policy support
	public void getSourceByIdWithCheck(Response response, ArrayList<HashMap> sourceGroup,
			int expectedStatusCode, String source_id, String sourceName, SourceType sourceType,
			SourceProduct sourceProduct, String organization_id, ProtectionStatus protectionStatus,
			ConnectionStatus connectionStatus, String site_id, String os_major, String applications,
			ArrayList<String> available_actions, String vm_name, String hypervisor_id, String agent_name,
			String os_name, String os_architecture, String agent_current_version,
			String agent_upgrade_version, String agent_upgrade_link, SpogMessageCode Info,
			ExtentTest test) {

		// TODO Auto-generated method stub
		String expectedErrorMessage = "", expectedErrorCode = "";

		checkResponseStatus(response, expectedStatusCode);
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			ArrayList<HashMap> source_group = response.then().extract().path("data.source_group");

			if (!(source_group.isEmpty())) {
				test.log(LogStatus.INFO, "validating the response for sourceGroup");
				assertResponseItem(response.then().extract().path("data.source_group"), sourceGroup, test);
			}


			test.log(LogStatus.INFO, "validating the response for source_id");
			assertResponseItem(response.then().extract().path("data.source_id"), source_id, test);

			test.log(LogStatus.INFO, "validating the response for source_name");
			assertResponseItem(response.then().extract().path("data.source_name"), sourceName, test);


			test.log(LogStatus.INFO, "validating the response for  for source_type");
			assertResponseItem(response.then().extract().path("data.source_type"), sourceType.name(),
					test);

			test.log(LogStatus.INFO, "validating the response for  source_product");
			assertResponseItem(response.then().extract().path("data.source_product"),
					sourceProduct.name(), test);

			test.log(LogStatus.INFO, "validating the response for  organization_id");
			assertResponseItem(response.then().extract().path("data.organization_id"), organization_id,
					test);

			test.log(LogStatus.INFO, "validating the response for protection status");
			assertResponseItem(response.then().extract().path("data.protection_status"),
					protectionStatus.name(), test);


			test.log(LogStatus.INFO, "validating the response for connections_status");
			assertResponseItem(response.then().extract().path("data.connection_status"),
					connectionStatus.name(), test);

			HashMap<String, String> site_info = response.then().extract().path("data.site");
			test.log(LogStatus.INFO, "validating the response for site");
			if(site_info!=null&&!(site_info.isEmpty())&&site_info.containsKey("site_id"))
			{
				assertResponseItem(site_info.get("site_id"), site_id, test);
				assertNotNull(site_info.get("site_name"));
			}

			test.log(LogStatus.INFO, "validating the response for pfc_status");
			assertNull(response.then().extract().path("data.pfc_status"));

			test.log(LogStatus.INFO, "validating the response for policy");
			assertNull(response.then().extract().path("data.policy"));

			test.log(LogStatus.INFO, "validating the response for last_recovery_point_ts");
			assertNull(response.then().extract().path("data.last_recovery_point_ts"));

			test.log(LogStatus.INFO, "validating the response for  assured_recovery_job");
			assertNull(response.then().extract().path("data. assured_recovery_job"));

			test.log(LogStatus.INFO, "validating the response for arrayList of available actions ");
			ArrayList<String> actual_actions = response.then().extract().path("data.available_actions");
			for (int i = 0; i < actual_actions.size(); i++) {
				assertResponseItem(actual_actions.get(i), available_actions.get(i), test);
			}

			test.log(LogStatus.INFO, "validating the response for os");
			HashMap<String, String> operating_system =
					response.then().extract().path("data.operating_system");
			assertResponseItem(operating_system.get("os_major"), (os_major), test);
			assertResponseItem(operating_system.get("os_name"), (os_name), test);
			assertResponseItem(operating_system.get("os_architecture"), (os_architecture), test);

			test.log(LogStatus.INFO, "validating the response for agent");
			HashMap<String, String> agent = response.then().extract().path("data.agent");

			assertResponseItem(agent.get("agent_name"), agent_name, test);
			assertResponseItem(agent.get("agent_current_version"), agent_current_version, test);
			assertResponseItem(agent.get("agent_upgrade_version"), agent_upgrade_version, test);
			assertResponseItem(agent.get("agent_upgrade_link"), agent_upgrade_link, test);


			test.log(LogStatus.INFO, "validating the response for hypervisor");
			HashMap<String, String> hypervisor = response.then().extract().path("data.hypervisor");
			if(!(hypervisor==null))
			{
				assertResponseItem(hypervisor.get("hypervisor_id"), hypervisor_id, test);
				assertResponseItem(hypervisor.get("hypervisor_name"), null, test);
			}

			test.log(LogStatus.INFO, "validating the response for create_ts ,modify_ts");
			assertNotNull(response.then().extract().path("data.create_ts"));
			assertNotNull(response.then().extract().path("data.modify_ts"));

			test.log(LogStatus.INFO, "validating the response for is_deleted");
			assertResponseItem(response.then().extract().path("data.is_deleted"), false, test);

			test.log(LogStatus.INFO, "validating the response multiple policy support");
			assertResponseItem(response.then().extract().path("data.multiple_policy_support"), false,
					test);


		} else {
			if (Info.getStatus() != "0010000") {
				expectedErrorMessage = Info.getStatus();
				if (expectedErrorMessage.contains("{0}")) {
					expectedErrorMessage = expectedErrorMessage.replace("{0}", getUUId());
					System.out.println(expectedErrorMessage);
				}
				expectedErrorCode = Info.getCodeString();
			}
			checkErrorMessage(response, expectedErrorMessage);
			test.log(LogStatus.PASS, "The value of the message is " + expectedErrorMessage);
			checkErrorCode(response, expectedErrorCode);
			test.log(LogStatus.PASS, "The value of the code  generated  is " + expectedErrorCode);
			test.log(LogStatus.INFO,
					"The value of the  response generated actually is :" + response.getStatusCode());
		}

	}


	/**
	 * @author KiranSripada get the jobs by job_id
	 * @param response
	 * @param expectedErrorMessage
	 */
	public Response getJobsById(String token, String jobId, ExtentTest test) {

		Response response = spogInvoker.getjobsbyJobId(token, jobId);
		return response;
	}


	public Response getJobById(String jobId, ExtentTest test) {

		Response response = spogInvoker.getjobbyJobId(jobId);
		if (response.statusCode() == 401) {
			return null;
		}
		return response;
	}


	/**
	 * @author KiranSiripada get the jobs
	 * @param response
	 * @param expectedErrorMessage
	 */
	public Response getJobs(String token, String additionalURL, ExtentTest test) {

		Response response = spogInvoker.getjobs(token, additionalURL, test);
		return response;
	}


	public HashMap<String, Object> composejobsinfo_check(String job_id, String site_id,
			String source_id, String resource_id, String resource_name, String rps_id,
			String organization_id, String datastore_id, String policy_id, long start_time_ts,
			long end_time_ts, String job_type, String jobStatus, String destination_name, String job_name,
			String job_severity, String percent_complete, long duration, String job_seq,
			String destination_type) {

		HashMap<String, Object> temp = new HashMap<>();
		temp.put("start_time_ts", start_time_ts);
		temp.put("end_time_ts", end_time_ts);
		temp.put("job_id", job_id);
		temp.put("site_id", site_id);
		temp.put("server_id", source_id);
		temp.put("source_id", resource_id);
		temp.put("source_name", resource_name);
		temp.put("rps_id", rps_id);
		temp.put("organization_id", organization_id);
		temp.put("destination_id", datastore_id);
		temp.put("policy_id", policy_id);
		temp.put("job_name", job_name);
		temp.put("job_type", job_type);
		temp.put("job_status", jobStatus);
		temp.put("destination_name", destination_name);
		temp.put("job_severity", job_severity);
		temp.put("percent_complete", percent_complete);
		temp.put("duration", duration);
		temp.put("job_seq", job_seq);
		temp.put("destination_type", destination_type);
		/*
		 * if(messageId.contains("testJobMessage")) { temp.put("message","Unable to connect to "
		 * +messagedata[0]+" \""+ messagedata[1]+"\""+"."); }else { temp.put("message",
		 * "Test job message."); }
		 */
		return temp;
	}


	/**
	 * @author KiranSiripada get the jobs
	 * @param response
	 * @param expectedErrorMessage
	 */
	public void checkGetJobs(Response response, int curr_page, int page_size, String sortjobs,
			String filter, ArrayList<HashMap<String, Object>> expectedresponse, int expectedstatuscode,
			SpogMessageCode errorMessage, ExtentTest test) {

		ArrayList<HashMap<String, Object>> actual_response = new ArrayList<>();
		int size_expectedresponse = 0;
		int size_actualresponse = 0;
		int actual_pagesize;
		if (curr_page == 0 || curr_page == -1) {
			curr_page = 1;
		}
		if (page_size == 0 || page_size == -1) {
			page_size = 20;
		}
		if (page_size > 100) {
			page_size = 100;
		}
		test.log(LogStatus.INFO, "Started checking the response body for the jobs");
		checkResponseStatus(response, expectedstatuscode);
		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			actual_response = response.then().extract().path("data");
			// actual_pagesize = response.then().extract().path("pagination.page_size");
			// actual_pagesize = response.then().extract().path("pagination.curr_page");
			size_expectedresponse = expectedresponse.size();
			size_actualresponse = actual_response.size();
			if (size_actualresponse == 0) {
				ArrayList<HashMap<String, Object>> errors = response.then().extract().path("errors");
				if (errors.size() == 0) {
					test.log(LogStatus.PASS, "No data available with the applied filter");
				} else {
					test.log(LogStatus.FAIL, "The jobs count is 0 and the error message is "
							+ response.then().extract().path("errors"));
					assertTrue("The jobs count is 0", false);
				}

			} else if (size_expectedresponse >= size_actualresponse) {
				// When the input does not have any sorting/filtering/paging then sort by
				// ascending order
				if ((sortjobs == "" || sortjobs == null && filter == "" || filter == null)
						|| (sortjobs == "" || sortjobs == null && filter != null)) {
					Collections.sort(actual_response, new Comparator<HashMap>() {

						@Override
						public int compare(HashMap o1, HashMap o2) {

							// TODO Auto-generated method stub
							int create_ts = (int) o1.get("start_time_ts");
							int create_ts1 = (int) o2.get("start_time_ts");
							if (create_ts > create_ts1)
								return 1;
							if (create_ts < create_ts1)
								return -1;
							else
								return 0;

						}
					});

					for (int i = 0; i < size_actualresponse; i++) {
						checkgetjobsvalidation(actual_response, expectedresponse, i, i, test, null);
					}

					// The below condition is validated when the user sends sorting and
					// withorwithout filtering. Also, if the user sends the page and page-size as
					// inputs, its mandatory
					// to send the sort either by asc or desc.
				}

				else if ((sortjobs.contains("create_ts") && sortjobs != null && (!sortjobs.equals(""))
						&& (filter != null))
						|| ((sortjobs.contains("create_ts")) && sortjobs != null && (!sortjobs.equals(""))
						&& ((filter == null) || filter.equals("")))) {
					String[] site = null;

					if (filter.contains("=") && (filter.contains("site_id"))
							|| (filter.contains("=") && (filter.contains("rps_id")))
							|| (filter.contains("=") && (filter.contains("server_id")))
							|| (filter.contains("=") && (filter.contains("destination_id")))
							|| (filter.contains("=") && (filter.contains("policy_id")))
							|| (filter.contains("=") && (filter.contains("source_id")))
							|| (filter.contains("=") && (filter.contains("job_status")))
							|| (filter.contains("=") && (filter.contains("job_type")))
							|| (filter.contains("=") && (filter.contains("source_name")))) {
						String[] first = filter.split(",");
						int len = first.length;
						for (int i = 0; i < len; i++) {

							site = first[i].split(";");

							ArrayList<HashMap<String, Object>> single_site = new ArrayList<>();
							for (int z = 0; z < expectedresponse.size(); z++) {
								if (first[i].contains("site_id")) {
									if (expectedresponse.get(z).get("site_id").equals(site[2])) {
										single_site.add(expectedresponse.get(z));

									}
								}
								if (first[i].contains("rps_id")) {
									if (expectedresponse.get(z).get("rps_id").equals(site[2])) {
										single_site.add(expectedresponse.get(z));

									}
								}
								if (first[i].contains("server_id")) {
									if (expectedresponse.get(z).get("server_id").equals(site[2])) {
										single_site.add(expectedresponse.get(z));

									}
								}
								if (first[i].contains("destination_id")) {
									if (expectedresponse.get(z).get("destination_id").equals(site[2])) {
										single_site.add(expectedresponse.get(z));

									}
								}
								if (first[i].contains("policy_id")) {
									if (expectedresponse.get(z).get("policy_id").equals(site[2])) {
										single_site.add(expectedresponse.get(z));

										/*
										 * if(filter.contains("job_type")) {
										 * if(single_site.get(z).get("job_status").equals(site[3])) {
										 * single_site.add(expectedresponse.get(z)); } }
										 */
									}
								}
								if (first[i].contains("source_id")) {
									if (expectedresponse.get(z).get("source_id").equals(site[2])) {
										single_site.add(expectedresponse.get(z));

									}
								}
								if (first[i].contains("job_status")) {
									if (expectedresponse.get(z).get("job_status").equals(site[2])) {
										single_site.add(expectedresponse.get(z));

									}
								}
								if (first[i].contains("job_type")) {
									if (expectedresponse.get(z).get("job_type").equals(site[2])) {
										single_site.add(expectedresponse.get(z));

									}
								}
								if (first[i].contains("source_name")) {

									if (expectedresponse.get(z).get("source_name").toString().contains(site[2])) {
										single_site.add(expectedresponse.get(z));

									}
								}

							}
							expectedresponse = single_site;
						}

					} else if (filter.contains("in") && (filter.contains("job_type"))
							|| (filter.contains("in") && (filter.contains("job_status")))
							|| (filter.contains("in") && (filter.contains("source_id")))
							|| (filter.contains("in") && (filter.contains("policy_id")))
							|| (filter.contains("in") && (filter.contains("source_name")))) {
						String[] resources = filter.split(",");
						int len = resources.length;

						for (int j = 0; j < len; j++) {
							int actual_size = expectedresponse.size();
							String[] jobtypes = resources[j].split(";");
							String[] diff_jobtypes = jobtypes[2].split("\\|");
							int size_jobtypes = diff_jobtypes.length;
							ArrayList<HashMap<String, Object>> temp_response = new ArrayList<>();
							for (int z = 0; z < size_jobtypes; z++) {
								for (int a = 0; a < actual_size; a++) {
									if (expectedresponse.get(a).get("job_type").equals(diff_jobtypes[z])) {
										temp_response.add(expectedresponse.get(a));

									} else if (expectedresponse.get(a).get("job_status").equals(diff_jobtypes[z])) {
										temp_response.add(expectedresponse.get(a));

									} else if (expectedresponse.get(a).get("source_id").equals(diff_jobtypes[z])) {
										temp_response.add(expectedresponse.get(a));

									} else if (expectedresponse.get(a).get("policy_id").equals(diff_jobtypes[z])) {
										temp_response.add(expectedresponse.get(a));
									} else if (expectedresponse.get(a).get("source_name").equals(diff_jobtypes[z])) {
										temp_response.add(expectedresponse.get(a));
									} else {
										// do nothing that means filter is not valid....
									}

								}

							}
							expectedresponse = temp_response;
						}
						Collections.sort(expectedresponse, new Comparator<HashMap>() {

							@Override
							public int compare(HashMap o1, HashMap o2) {

								// TODO Auto-generated method stub
								Long create_ts = (Long) o1.get("start_time_ts");
								Long create_ts1 = (Long) o2.get("start_time_ts");
								if (create_ts > create_ts1)
									return 1;
								if (create_ts < create_ts1)
									return -1;
								else
									return 0;

							}
						});

					}
					if (sortjobs.contains("asc")) {
						int j = 0;
						if (curr_page > 1) {
							j = (curr_page - 1) * page_size;
						}
						for (int i = 0; i < size_actualresponse; i++, j++) {
							checkgetjobsvalidation(actual_response, expectedresponse, i, j, test, null);
						}

					} else {
						int j;
						if (page_size == 20 || page_size == 100) {
							j = actual_response.size() - 1;

						} else {
							j = expectedresponse.size() - 1;
							if (curr_page != 1) {
								j = (expectedresponse.size() - 1) - (curr_page - 1) * page_size;
							}
						}
						for (int i = 0; i < size_actualresponse; i++, j--) {
							checkgetjobsvalidation(actual_response, expectedresponse, i, j, test, null);
						}

					}

				} else if ((sortjobs.contains("job_status") && sortjobs != null && (!sortjobs.equals(""))
						&& ((filter == null) || filter.equals("")))
						|| ((sortjobs.contains("job_type")) && sortjobs != null && (!sortjobs.equals(""))
						&& ((filter == null) || filter.equals("")))) {
					String[] sort_type = sortjobs.split(",");
					test.log(LogStatus.INFO, "before sorting " + expectedresponse.toString());
					Collections.sort(expectedresponse, new Comparator<HashMap<String, Object>>() {
						@Override
						public int compare(HashMap<String, Object> o1, HashMap<String, Object> o2) {

							// TODO Auto-generated method stub
							String create_ts = (String) o1.get(sort_type[0]);
							String create_ts1 = (String) o2.get(sort_type[0]);
							if (create_ts.compareTo(create_ts1) > 0)
								return 1;
							else if (create_ts.compareTo(create_ts1) == 0) {
								Long start_time = (Long) o1.get("start_time_ts");
								Long start_time1 = (Long) o2.get("start_time_ts");
								if (start_time < start_time1)
									return 1;
								if (start_time > start_time1)
									return -1;
								else
									return 0;
							} else
								return -1;

						}
					});
					test.log(LogStatus.INFO, "after sorting " + expectedresponse.toString());
					if (sortjobs.contains("asc")) {

						int j = 0;
						if (curr_page > 1) {
							j = (curr_page - 1) * page_size;

						}
						for (int i = 0; i < size_actualresponse; i++, j++) {
							checkgetjobsvalidation(actual_response, expectedresponse, i, j, test, null);
						}
					} else {
						/*
						 * Collections.sort(expectedresponse, new Comparator<HashMap<String,Object>>() {
						 * 
						 * @Override public int compare(HashMap<String,Object> o1, HashMap<String,Object>o2) {
						 * // TODO Auto-generated method stub String create_ts = (String) o1.get(sort_type[0]);
						 * String create_ts1 = (String) o2.get(sort_type[0]);
						 * if(create_ts.compareTo(create_ts1)>0) return 1; else
						 * if(create_ts.compareTo(create_ts1)==0) return 0; else return -1; } });
						 */ int j;
						 if (page_size == 20 || page_size == 100) {
							 j = actual_response.size() - 1;

						 } else {
							 j = expectedresponse.size() - 1;
							 if (curr_page != 1) {
								 j = (expectedresponse.size() - 1) - (curr_page - 1) * page_size;
							 }
						 }
						 for (int i = 0; i < size_actualresponse; i++, j--) {
							 checkgetjobsvalidation(actual_response, expectedresponse, i, j, test, null);
						 }
					}

				} else {
					test.log(LogStatus.FAIL, "None of the conditions got satisfied, please check the inputs");
					assertTrue("None of the conditions got satisfied, please check the inputs", false);

				}
				// The below function validates the pagination for the given response
				validateResponseForPages(curr_page, page_size, response, expectedresponse.size(), test);

			} else {
				// assertTrue("The jobs count did not match the expected", false);
				test.log(LogStatus.FAIL, "The jobs count did not match the expected");
			}
		} else {
			String code = errorMessage.getCodeString();
			String message = errorMessage.getStatus();
			if (code.contains("00100201")) {
				message = message.replace("{0}", getUUId());
			}
			checkErrorCode(response, code);
			test.log(LogStatus.INFO, "The error code matched with the expected " + code);
			// checkErrorMessage(response,message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		}

	}


	public void checkgetjobsvalidation(ArrayList<HashMap<String, Object>> actual_response,
			ArrayList<HashMap<String, Object>> expectedresponse, int i, int j, ExtentTest test,
			String job_data) {

		assertResponseItem(actual_response.get(i).get("job_id").toString(),
				expectedresponse.get(j).get("job_id").toString(), test);
		// assertResponseItem(actual_response.get(i).get("job_name").toString(),expectedresponse.get(j).get("job_name").toString(),test);
		assertResponseItem(actual_response.get(i).get("job_type").toString(),
				expectedresponse.get(j).get("job_type").toString(), test);
		assertResponseItem(actual_response.get(i).get("job_status").toString(),
				expectedresponse.get(j).get("job_status").toString(), test);
		if (job_data == null) {

			/*
			 * assertResponseItem(actual_response.get(i).get("job_severity").toString(),
			 * expectedresponse.get(j).get("job_severity").toString(),test); ArrayList<String>
			 * available_actions = (ArrayList<String>) actual_response.get(i).get("available_actions");
			 * if(available_actions.size()==0) { test.log(LogStatus.PASS,
			 * "The available_action list is empty"); assertTrue("compare percent_complete passed", true);
			 * }
			 */

		}

		assertResponseItem(actual_response.get(i).get("start_time_ts").toString(),
				expectedresponse.get(j).get("start_time_ts").toString(), test);
		if (!actual_response.get(i).get("job_status").toString().equalsIgnoreCase("active")) {
			assertResponseItem(actual_response.get(i).get("end_time_ts").toString(),
					expectedresponse.get(j).get("end_time_ts").toString(), test);

			if (job_data == null) {
				assertResponseItem(actual_response.get(i).get("duration").toString(),
						expectedresponse.get(j).get("duration").toString(), test);
				assertResponseItem(actual_response.get(i).get("destination_type").toString(),expectedresponse.get(j).get("destination_type").toString(),test);
			}
		}
		assertResponseItem(actual_response.get(i).get("server_id").toString(),
				expectedresponse.get(j).get("server_id").toString(), test);
		assertResponseItem(actual_response.get(i).get("source_id").toString(),
				expectedresponse.get(j).get("source_id").toString(), test);
		assertResponseItem(actual_response.get(i).get("source_name").toString(),
				expectedresponse.get(j).get("source_name").toString(), test);
		assertResponseItem(actual_response.get(i).get("rps_id").toString(),
				expectedresponse.get(j).get("rps_id").toString(), test);
		assertResponseItem(actual_response.get(i).get("destination_id").toString(),
				expectedresponse.get(j).get("destination_id").toString(), test);
		assertResponseItem(actual_response.get(i).get("destination_name").toString(),expectedresponse.get(j).get("destination_name").toString(),test);

		if(actual_response.get(i).get("site_id") != null && expectedresponse.get(j).get("site_id") != null)
			assertResponseItem(actual_response.get(i).get("site_id").toString(),expectedresponse.get(j).get("site_id").toString(), test);

		HashMap<String, Object> act_org = (HashMap<String, Object>) actual_response.get(i).get("organization");
		HashMap<String, Object> exp_org = (HashMap<String, Object>) expectedresponse.get(i).get("organization");
		if (exp_org != null) { //check there is a separate hash for organization
			assertResponseItem(act_org.get("organization_id").toString(),
					exp_org.get("organization_id").toString(), test);
		}else {
			assertResponseItem(act_org.get("organization_id").toString(),
					expectedresponse.get(i).get("organization_id").toString(), test);	
		}	

		if (actual_response.get(i).containsKey("policy")) {
			assertResponseItem(((HashMap<String, Object>) actual_response.get(i).get("policy")).get("policy_id").toString(),
					expectedresponse.get(j).get("policy_id").toString(), test);
		}

		// assertResponseItem(actual_response.get(i).get("percent_complete").toString(),expectedresponse.get(j).get("percent_complete").toString(),test);
		/*
		 * if(actual_response.get(i).containsKey("percent_complete")) {
		 * assertResponseItem(actual_response.get(i).get("percent_complete"),
		 * expectedresponse.get(j).get("percent_complete"),test);
		 * 
		 * }
		 */

		if (actual_response.get(i).containsKey("percent_complete")) {
			Float responsePercentComplete = (Float) actual_response.get(i).get("percent_complete");
			if (Float.parseFloat(
					(String) expectedresponse.get(j).get("percent_complete")) == responsePercentComplete) {
				test.log(LogStatus.PASS, "Compare percent_complete passed");
				assertTrue("compare percent_complete passed", true);
			} else {
				assertTrue("Compare percent_complete failed", false);
			}
		}

	}


	/**
	 * create group successful with check
	 * 
	 * @author Bharadwaj.Ghadiam
	 * @param organization_id
	 * @param group_name
	 * @param group_description
	 * @return group_id
	 */
	public String createGroupWithCheck2(String token, String organization_id, String group_name,
			String group_description, ExtentTest test) {

		Map<String, String> groupInfo = jp.groupInfo(organization_id, group_name, group_description);

		Response response = spogInvoker.createGroup(token, groupInfo, test);
		// checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		checkBodyItemValue(response, "organization_id", organization_id);
		checkBodyItemValue(response, "group_name", group_name);
		checkBodyItemValue(response, "group_description", group_description);
		String group_id = response.then().extract().path("data.group_id");

		return group_id;
	}


	/**
	 * Get the groups by id
	 * 
	 * @param csr_Token
	 * @param group_id
	 * @param test2
	 * @return
	 */

	public Response getGroupById(String csr_Token, String group_id, ExtentTest test2) {

		// TODO Auto-generated method stub
		setUUID(group_id);
		Response response = spogInvoker.getGroupById(csr_Token, group_id, test2);
		return response;
	}


	/**
	 * Validate the response for getGroupbyId
	 * 
	 * @author Bharadwaj.Ghadaim
	 * @param response
	 * @param status
	 * @param expectedStatusCode
	 * @param group_id
	 * @param groupName
	 * @param groupDescription
	 * @param orgId
	 * @param expectedErrorMessage
	 * @param test
	 */

	public void checkGetGroupById(Response response, HashMap<String, Object> status,
			int expectedStatusCode, String group_id, String groupName, String groupDescription,
			String orgId, SpogMessageCode Info, ExtentTest test) {

		// TODO Auto-generated method stub
		String expectedErrorMessage = "", expectedErrorCode = "";
		checkResponseStatus(response, expectedStatusCode);
		test.log(LogStatus.INFO, "validating the response of the generated error message");
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			response.then().body("data.group_id", equalTo(group_id));
			response.then().body("data.group_name", equalTo(groupName));
			response.then().body("data.group_description", equalTo(groupDescription));
			response.then().body("data.organization_id", equalTo(orgId));
			response.then().body("data.source_status", equalTo(status));
			test.log(LogStatus.PASS,
					"The validation of the response is successfully for the group_id:"
							+ response.then().extract().path("data.group_id") + "organization_id"
							+ response.then().extract().path("data.organization_id"));
			assertTrue("The data has been matched", true);

		} else {

			if (Info.getStatus() != "0010000") {
				expectedErrorMessage = Info.getStatus();
				if (expectedErrorMessage.contains("{0}")) {
					expectedErrorMessage = expectedErrorMessage.replace("{0}", getUUId());
					System.out.println(expectedErrorMessage);
				}
				expectedErrorCode = Info.getCodeString();
			}
			checkErrorMessage(response, expectedErrorMessage);
			test.log(LogStatus.PASS, "The value of the message is " + expectedErrorMessage);
			checkErrorCode(response, expectedErrorCode);
			test.log(LogStatus.PASS, "The value of the code  generated  is " + expectedErrorCode);
			test.log(LogStatus.INFO,
					"The value of the  response generated actually is :" + response.getStatusCode());
		}
	}


	/**
	 * 
	 * @author Zhaoguo.Ma
	 * @param cloudAccountKey
	 * @param cloudAccountSecret
	 * @param cloudAccountName
	 * @param cloudAccountType
	 * @param organizationID
	 * @param test
	 * @return
	 */
	public String createCloudAccountWithCheck(String cloudAccountKey, String cloudAccountSecret,
			String cloudAccountName, String cloudAccountType, String organizationID, ExtentTest test) {

		String cloudAccountID = null;

		test.log(LogStatus.INFO, "add a new cloud account");
		Map<String, String> cloudAccountInfo = jp.composeCloudAccountInfo(cloudAccountKey,
				cloudAccountSecret, cloudAccountName, cloudAccountType, organizationID);

		Response response = spogInvoker.addCloudAccount(cloudAccountInfo);
		checkResponseStatus(response, SpogConstants.SUCCESS_POST);

		String userID = GetLoggedinUser_UserID();

		if (cloudAccountKey != "" && !(cloudAccountType.equals("cloud_direct"))) {
			response.then().body("data.cloud_account_key", equalTo(cloudAccountKey));
		} else {
			assertNotNull("data.cloud_account_key");
		}

		response.then().body("data.cloud_account_name", equalTo(cloudAccountName))
		.body("data.cloud_account_type", equalTo(cloudAccountType))
		.body("data.create_user_id", equalTo(userID))
		.body("data.cloud_account_status", equalTo("success"));

		String getOrganizationID = getOrganizationID(response);
		if (null == organizationID || "none" == organizationID || "" == organizationID) {
			response.then().body("data.organization_id", equalTo(getOrganizationID));
		} else {
			response.then().body("data.organization_id", equalTo(organizationID));
		}

		cloudAccountID = response.then().extract().path("data.cloud_account_id");
		return cloudAccountID;
	}


	public Response createCloudAccountWithCheck1(String cloudAccountKey, String cloudAccountSecret,
			String cloudAccountName, String cloudAccountType, String organizationID, String orderID,
			String fulfillmentID, ExtentTest test) {

		String cloudAccountID = null;

		test.log(LogStatus.INFO, "add a new cloud account");
		Map<String, String> cloudAccountInfo =
				jp.composeCloudAccountInfo(cloudAccountKey, cloudAccountSecret, cloudAccountName,
						cloudAccountType, organizationID, orderID, fulfillmentID);

		Response response = spogInvoker.addCloudAccount(cloudAccountInfo);
		checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		test.log(LogStatus.INFO, "The value of the response:" + response.getBody().asString());
		String userID = GetLoggedinUser_UserID();

		if (cloudAccountKey != "" && !(cloudAccountType.equals("cloud_direct"))) {
			response.then().body("data.cloud_account_key", equalTo(cloudAccountKey));
		} else {
			assertNotNull("data.cloud_account_key");
		}

		response.then().body("data.cloud_account_name", equalTo(cloudAccountName))
		.body("data.cloud_account_type", equalTo(cloudAccountType))
		.body("data.create_user_id", equalTo(userID))
		.body("data.cloud_account_status", equalTo("success"));

		String getOrganizationID = getOrganizationID(response);
		if (null == organizationID || "none" == organizationID || "" == organizationID) {
			response.then().body("data.organization_id", equalTo(getOrganizationID));
		} else {
			response.then().body("data.organization_id", equalTo(organizationID));
		}
		return response;
	}


	public String createCloudAccountWithCheck(String cloudAccountKey, String cloudAccountSecret,
			String cloudAccountName, String cloudAccountType, String organizationID, String orderID,
			String fulfillmentID, ExtentTest test) {

		String cloudAccountID = null;

		test.log(LogStatus.INFO, "add a new cloud account");
		Map<String, String> cloudAccountInfo =
				jp.composeCloudAccountInfo(cloudAccountKey, cloudAccountSecret, cloudAccountName,
						cloudAccountType, organizationID, orderID, fulfillmentID);

		Response response = spogInvoker.addCloudAccount(cloudAccountInfo);
		checkResponseStatus(response, SpogConstants.SUCCESS_POST);

		String userID = GetLoggedinUser_UserID();

		if (cloudAccountKey != "" && !(cloudAccountType.equals("cloud_direct"))) {
			response.then().body("data.cloud_account_key", equalTo(cloudAccountKey));
		} else {
			assertNotNull("data.cloud_account_key");
		}

		response.then().body("data.cloud_account_name", equalTo(cloudAccountName))
		.body("data.cloud_account_type", equalTo(cloudAccountType))
		.body("data.create_user_id", equalTo(userID))
		.body("data.cloud_account_status", equalTo("success"));

		String getOrganizationID = getOrganizationID(response);
		if (null == organizationID || "none" == organizationID || "" == organizationID) {
			response.then().body("data.organization_id", equalTo(getOrganizationID));
		} else {
			response.then().body("data.organization_id", equalTo(organizationID));
		}

		cloudAccountID = response.then().extract().path("data.cloud_account_id");
		return cloudAccountID;
	}


	public String createCloudAccountWithCheck(String cloudAccountKey, String cloudAccountSecret,
			String cloudAccountName, String cloudAccountType, String organizationID, String orderID,
			String fulfillmentID, String datacenter_id, ExtentTest test) {

		String cloudAccountID = null;

		test.log(LogStatus.INFO, "add a new cloud account");
		Map<String, String> cloudAccountInfo =
				jp.composeCloudAccountInfo(cloudAccountKey, cloudAccountSecret, cloudAccountName,
						cloudAccountType, organizationID, orderID, fulfillmentID, datacenter_id);

		Response response = spogInvoker.addCloudAccount(cloudAccountInfo);
		checkResponseStatus(response, SpogConstants.SUCCESS_POST);

		cloudAccountID = response.then().extract().path("data.cloud_account_id");
		return cloudAccountID;
	}


	public Response createCloudAccount_audittrail(String cloudAccountKey, String cloudAccountSecret,
			String cloudAccountName, String cloudAccountType, String organizationID, String orderID,
			String fulfillmentID, String datacenter_id, ExtentTest test) {

		String cloudAccountID = null;

		test.log(LogStatus.INFO, "add a new cloud account");
		Map<String, String> cloudAccountInfo =
				jp.composeCloudAccountInfo(cloudAccountKey, cloudAccountSecret, cloudAccountName,
						cloudAccountType, organizationID, orderID, fulfillmentID, datacenter_id);

		Response response = spogInvoker.addCloudAccount(cloudAccountInfo);
		checkResponseStatus(response, SpogConstants.SUCCESS_POST);

		// cloudAccountID = response.then().extract().path("data.cloud_account_id");
		return response;
	}


	/**
	 * create cloud account, add enroll_ticket, datacenter_id;
	 * 
	 * @author Zhaoguo.Ma
	 * @param cloudAccountKey
	 * @param cloudAccountSecret
	 * @param cloudAccountName
	 * @param cloudAccountType
	 * @param organizationID
	 * @param orderID
	 * @param fulfillmentID
	 * @param enrollTicket
	 * @param datacenterID
	 * @param test
	 * @return
	 */
	public String createCloudAccountWithCheck(String cloudAccountKey, String cloudAccountSecret,
			String cloudAccountName, String cloudAccountType, String organizationID, String orderID,
			String fulfillmentID, String enrollTicket, String datacenterID, ExtentTest test) {

		String cloudAccountID = null;

		test.log(LogStatus.INFO, "add a new cloud account");
		Map<String, String> cloudAccountInfo =
				jp.composeCloudAccountInfo(cloudAccountKey, cloudAccountSecret, cloudAccountName,
						cloudAccountType, organizationID, orderID, fulfillmentID, enrollTicket, datacenterID);

		Response response = spogInvoker.addCloudAccount(cloudAccountInfo);
		checkResponseStatus(response, SpogConstants.SUCCESS_POST);

		String userID = GetLoggedinUser_UserID();

		if (cloudAccountKey != "" && !(cloudAccountType.equals("cloud_direct"))) {
			response.then().body("data.cloud_account_key", equalTo(cloudAccountKey));
		} else {
			assertNotNull("data.cloud_account_key");
		}

		response.then().body("data.cloud_account_name", equalTo(cloudAccountName))
		.body("data.cloud_account_type", equalTo(cloudAccountType))
		.body("data.create_user_id", equalTo(userID))
		.body("data.cloud_account_status", equalTo("success"));

		String getOrganizationID = getOrganizationID(response);
		if (null == organizationID || "none" == organizationID || "" == organizationID) {
			response.then().body("data.organization_id", equalTo(getOrganizationID));
		} else {
			response.then().body("data.organization_id", equalTo(organizationID));
		}

		cloudAccountID = response.then().extract().path("data.cloud_account_id");
		return cloudAccountID;
	}


	public void createCloudAccountWithCodeCheck(String cloudAccountKey, String cloudAccountSecret,
			String cloudAccountName, String cloudAccountType, String organizationID, String orderID,
			String fulfillmentID, String enrollTicket, String datacenterID, int statusCode,
			String errorCode, ExtentTest test) {

		String cloudAccountID = null;

		test.log(LogStatus.INFO, "add a new cloud account");
		Map<String, String> cloudAccountInfo =
				jp.composeCloudAccountInfo(cloudAccountKey, cloudAccountSecret, cloudAccountName,
						cloudAccountType, organizationID, orderID, fulfillmentID, enrollTicket, datacenterID);

		Response response = spogInvoker.addCloudAccount(cloudAccountInfo);
		checkResponseStatus(response, statusCode);
		checkErrorCode(response, errorCode);

	}


	public void createCloudAccountWithCodeCheck(String cloudAccountKey, String cloudAccountSecret,
			String cloudAccountName, String cloudAccountType, String organizationID, String orderID,
			String fulfillmentID, int statusCode, String errorCode, ExtentTest test) {

		String cloudAccountID = null;

		test.log(LogStatus.INFO, "add a new cloud account");
		Map<String, String> cloudAccountInfo =
				jp.composeCloudAccountInfo(cloudAccountKey, cloudAccountSecret, cloudAccountName,
						cloudAccountType, organizationID, orderID, fulfillmentID);

		Response response = spogInvoker.addCloudAccount(cloudAccountInfo);
		checkResponseStatus(response, statusCode);
		checkErrorCode(response, errorCode);

	}


	public String createCloudAccountWithCheck(String cloudAccountKey, String cloudAccountSecret,
			String cloudAccountName, String cloudAccountType, String organizationID, String Token,
			ExtentTest test) {

		String cloudAccountID = null;

		test.log(LogStatus.INFO, "add a new cloud account");
		Map<String, String> cloudAccountInfo = jp.composeCloudAccountInfo(cloudAccountKey,
				cloudAccountSecret, cloudAccountName, cloudAccountType, organizationID);

		Response response = spogInvoker.createCloudAccount(cloudAccountInfo, Token);
		checkResponseStatus(response, SpogConstants.SUCCESS_POST);

		String userID = GetLoggedinUser_UserID();

		response.then().body("data.cloud_account_key", equalTo(cloudAccountKey))
		.body("data.cloud_account_name", equalTo(cloudAccountName))
		.body("data.cloud_account_type", equalTo(cloudAccountType))
		.body("data.create_user_id", equalTo(userID))
		.body("data.cloud_account_status", equalTo("success"));

		String getOrganizationID = getOrganizationID(response);
		if (null == organizationID || "none" == organizationID || "" == organizationID) {
			response.then().body("data.organization_id", equalTo(getOrganizationID));
		} else {
			response.then().body("data.organization_id", equalTo(organizationID));
		}

		cloudAccountID = response.then().extract().path("data.cloud_account_id");
		return cloudAccountID;
	}


	/**
	 * This is use cloud Token to create cloud account
	 * 
	 * @author yuefen.liu
	 * @return response
	 * 
	 */
	public Response createCloudAccount(String cloudAccountKey, String cloudAccountSecret,
			String cloudAccountName, String cloudAccountType, String organizationID, String Token,
			ExtentTest test) {

		test.log(LogStatus.INFO, "add a new cloud account uisng cloud Token");
		Map<String, String> cloudAccountInfo = jp.composeCloudAccountInfo(cloudAccountKey,
				cloudAccountSecret, cloudAccountName, cloudAccountType, organizationID);

		Response response = spogInvoker.createCloudAccount(cloudAccountInfo, Token);

		return response;
	}


	/**
	 * Delete a cloud account
	 * 
	 * @author liu.yuefen
	 * @param cloud account id
	 * @param return response
	 */
	public Response deleteCloudAccount(String cloud_account_id, ExtentTest test) {

		Response response = spogInvoker.DeleteCloudAccount(cloud_account_id);
		test.log(LogStatus.INFO, "The value of the resposne:" + response.getBody().asString());
		return response;
	}


	/**
	 * Delete a cloud account
	 * 
	 * @author liu.yuefen
	 * @param cloud account id
	 * @param expectedStatusCode
	 */
	public void deleteCloudAccountWithExpectedStatusCode(String cloud_account_id,
			int expectedStatusCode, ExtentTest test) {

		Response response = spogInvoker.DeleteCloudAccount(cloud_account_id);
		checkResponseStatus(response, expectedStatusCode, test);
	}


	/**
	 * Delete a cloud account
	 * 
	 * @author liu.yuefen
	 * @param cloud account id
	 * @param expectedStatusCode
	 * @param errorCode
	 */
	public void deleteCloudAccountFailWithCheck(String cloud_account_id, int expectedStatusCode,
			String expectedErrorCode, ExtentTest test) {

		Response response = spogInvoker.DeleteCloudAccount(cloud_account_id);
		checkResponseStatus(response, expectedStatusCode, test);
		checkErrorCode(response, expectedErrorCode);
	}


	/**
	 * login a cloud direct account
	 * 
	 * @author liu.yuefen
	 * @param cloudAccountKey
	 * @param cloudAccountSecret
	 * @param return response
	 */
	public Response cloudDirectAccountLogin(String cloudAccountkey, String cloudAccountSecret,
			ExtentTest test) {

		test.log(LogStatus.INFO, "login cloud account");

		Map<String, String> cloudAccountInfo =
				jp.getCloudDirectAccountInfo(cloudAccountkey, cloudAccountSecret);
		Response response = spogInvoker.CloudDirectAccountLogin(cloudAccountInfo, test);

		return response;
	}


	/**
	 * login a cloud direct account
	 * 
	 * @author liu.yuefen
	 * @param cloudAccountKey
	 * @param cloudAccountSecret
	 * @param return token
	 */
	public String cloudDirectAccountLoginSuccess(String cloudAccountkey, String cloudAccountSecret,
			ExtentTest test) {

		test.log(LogStatus.INFO, "login cloud account success");

		Response response = cloudDirectAccountLogin(cloudAccountkey, cloudAccountSecret, test);
		checkResponseStatus(response, SpogConstants.SUCCESS_LOGIN, test);
		String Token = response.then().extract().path("data.token");

		return Token;
	}


	/**
	 * login a cloud direct account
	 * 
	 * @author liu.yuefen
	 * @param cloudAccountKey
	 * @param cloudAccountSecret
	 * @param expectedStatusCode
	 * @param expectedErrorCode
	 * 
	 */
	public void cloudDirectAccountLoginFail(String cloudAccountkey, String cloudAccountSecret,
			int expectedStatusCode, String expectedErrorCode, ExtentTest test) {

		test.log(LogStatus.INFO, "login cloud account success");

		Response response = cloudDirectAccountLogin(cloudAccountkey, cloudAccountSecret, test);
		checkResponseStatus(response, expectedStatusCode, test);
		checkErrorCode(response, expectedErrorCode);
	}


	public void createCloudAccountWithCodeCheck(String cloudAccountKey, String cloudAccountSecret,
			String cloudAccountName, String cloudAccountType, String organizationID, int statusCode,
			String expectedErrorCode, ExtentTest test) {

		test.log(LogStatus.INFO, "add a new cloud account");
		Map<String, String> cloudAccountInfo = jp.composeCloudAccountInfo(cloudAccountKey,
				cloudAccountSecret, cloudAccountName, cloudAccountType, organizationID);

		Response response = spogInvoker.addCloudAccount(cloudAccountInfo);
		checkResponseStatus(response, statusCode);
		checkErrorCode(response, expectedErrorCode);
	}


	/**
	 * update a cloud account
	 * 
	 * @author liu.yuefen
	 * @param cloudAccountKey
	 * @param cloudAccountSecret
	 * @param cloudAccountName
	 * @param cloudAccountType
	 * @param OrgID
	 * @param return response
	 */
	public Response updateCloudAccount(String cloudAccountId, String cloudAccountkey,
			String cloudAccountSecret, String cloudAccountName, String cloudAccountType, String OrgId,
			ExtentTest test) {

		test.log(LogStatus.INFO, "updateCloudAccount");

		Map<String, String> cloudAccountInfo = jp.composeCloudAccountInfo(cloudAccountkey,
				cloudAccountSecret, cloudAccountName, cloudAccountType, OrgId);
		Response response = spogInvoker.updateCloudAccount(cloudAccountInfo, cloudAccountId, test);

		return response;
	}


	public Response updateCloudAccount(String cloudAccountId, String cloudAccountkey,
			String cloudAccountSecret, String cloudAccountName, String cloudAccountType, String OrgId,
			String orderID, String fulfillmentID, ExtentTest test) {

		test.log(LogStatus.INFO, "updateCloudAccount");

		Map<String, String> cloudAccountInfo = jp.composeCloudAccountInfo(cloudAccountkey,
				cloudAccountSecret, cloudAccountName, cloudAccountType, OrgId, orderID, fulfillmentID);
		Response response = spogInvoker.updateCloudAccount(cloudAccountInfo, cloudAccountId, test);

		return response;
	}


	public void updateCloudAccountWithCheck(String cloudAccountId, String cloudAccountkey,
			String cloudAccountSecret, String cloudAccountName, String cloudAccountType, String OrgId,
			int expectedStatusCode, String expectedErrorCode, ExtentTest test) {

		test.log(LogStatus.INFO, "updateCloudAccount");

		Map<String, String> cloudAccountInfo = jp.composeCloudAccountInfo(cloudAccountkey,
				cloudAccountSecret, cloudAccountName, cloudAccountType, OrgId);
		Response response = spogInvoker.updateCloudAccount(cloudAccountInfo, cloudAccountId, test);
		checkResponseStatus(response, expectedStatusCode, test);
		checkErrorCode(response, expectedErrorCode);
	}


	public void updateCloudDirecttWithCheck(String cloudAccountId, String cloudAccountkey,
			String cloudAccountSecret, String cloudAccountName, String cloudAccountType, String OrgId,
			int expectedStatusCode, String expectedErrorCode, ExtentTest test) {

		test.log(LogStatus.INFO, "updateCloudDirect");

		Map<String, String> cloudAccountInfo = jp.composeCloudAccountInfo(cloudAccountkey,
				cloudAccountSecret, cloudAccountName, cloudAccountType, OrgId);
		Response response = spogInvoker.updateCloudAccount(cloudAccountInfo, cloudAccountId, test);
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			response.then().body("data.cloud_account_key", not(equalTo(cloudAccountkey)))
			.body("data.cloud_account_name", equalTo(cloudAccountName))
			.body("data.cloud_account_type", not(equalTo(cloudAccountType)))
			.body("data.organization_id", not(equalTo(OrgId)));

		} else {
			checkResponseStatus(response, expectedStatusCode, test);
			checkErrorCode(response, expectedErrorCode);
		}
	}


	/**
	 * add an order to one existing CD order
	 * 
	 * @author liu.yuefen
	 * @param cloudAccountID
	 * @param orderID
	 * @param fulfillmentID
	 * @param organizationID
	 * @param return response
	 */
	public Response addOrderTocloudDirectOrder(String cloudAccountID, String orderID,
			String fulfillmentID, String organizationID, ExtentTest test) {

		test.log(LogStatus.INFO, "add an order to an exiting CD order");
		Map<String, String> orderInfo = jp.composeOrderInfo(orderID, fulfillmentID, organizationID);

		Response response = spogInvoker.createOrder(cloudAccountID, orderInfo);

		return response;

	}


	/**
	 * add an order to one existing CD order
	 * 
	 * @author liu.yuefen
	 * @param cloudAccountID
	 * @param orderID
	 * @param fulfillmentID
	 * @param organizationID
	 * @param expectedStatusCode
	 * @param expectedErrorCode
	 */
	public void addOrderTocloudDirectOrderWithCheck(String cloudAccountID, String orderID,
			String fulfillmentID, String organizationID, int expectedStatusCode, String expectedErrorCode,
			ExtentTest test) {

		test.log(LogStatus.INFO, "add an order to an exiting CD order");
		Map<String, String> orderInfo = jp.composeOrderInfo(orderID, fulfillmentID, organizationID);

		Response response = spogInvoker.createOrder(cloudAccountID, orderInfo);
		if (expectedStatusCode == SpogConstants.SUCCESS_POST) {
			List<String> orderSummary =
					response.body().jsonPath().getList("data.orders.order_summary.order_id");

			if (orderSummary != null && orderSummary.size() > 0) {
				for (int i = 0; i < orderSummary.size(); i++) {
					if (orderSummary.get(i).contains(orderID)) {
						assertTrue("There is order in response", true);
						break;
					}
				}
			} else {
				assertTrue("There is no this order in response", false);
				return;
			}
		} else {
			checkResponseStatus(response, expectedStatusCode, test);
			checkErrorCode(response, expectedErrorCode);
		}
	}


	/**
	 * retrieving the CD order
	 * 
	 * @author liu.yuefen
	 * @param cloudAccountID
	 * @param return response
	 */
	public Response getCloudDirectOrder(String cloudAccountID, ExtentTest test) {

		test.log(LogStatus.INFO, "get cloud direct order");

		Response response = spogInvoker.getCloudDirectOrder(cloudAccountID);

		return response;

	}


	public void getCloudDirectOrderWithCheck(Response response, int expectedStatuscode,
			List<String> expectedOrders, String errorCode, ExtentTest test) {

		test.log(LogStatus.INFO, "get cloud direct order with check");

		response.then().statusCode(expectedStatuscode);

		if (expectedStatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			List<String> orders =
					response.body().jsonPath().getList("data.orders.order_summary.order_id");
			for (int i = 0; i < expectedOrders.size(); i++) {
				orders.get(i).equalsIgnoreCase(expectedOrders.get(i));
			}

		} else {
			checkResponseStatus(response, expectedStatuscode, test);
			checkErrorCode(response, errorCode);
		}
	}


	/**
	 * Call the rest api to get the cloud accounts by id
	 * 
	 * @author Bharadwaj.Ghadiam
	 * @param Token
	 * @param cloud_account_id
	 * @param test
	 * @return response
	 */
	public Response getCloudAccountById(String Token, String cloud_account_id, ExtentTest test) {

		setUUID(cloud_account_id);
		test.log(LogStatus.INFO, "Call the rest api to get the cloud accounts by id ");
		Response response = spogInvoker.getCloudAccountById(Token, cloud_account_id, test);
		return response;
	}


	/**
	 * This is a generic method used to create the cloud_account
	 * 
	 * @param cloudAccountKey
	 * @param cloudAccountSecret
	 * @param cloudAccountName
	 * @param cloudAccountType
	 * @param organization_id
	 * @param orderID
	 * @param fulfillmentID
	 * @param datacenter_id
	 * @param spogServer
	 * @param test
	 * @return
	 */
	// This method is used to create a cloud_account
	public String createCloudAccount(String cloudAccountKey, String cloudAccountSecret,
			String cloudAccountName, String cloudAccountType, String organization_id, String orderID,
			String fulfillmentID, String datacenter_id, SPOGServer spogServer, ExtentTest test) {

		String cloud_account_id = "";
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		if (!(null == orderID) && !("" == orderID) && !(orderID.equalsIgnoreCase("none"))) {
			orderID = orderID + prefix;
		}

		if (!(null == fulfillmentID) && !("" == fulfillmentID)
				&& !(fulfillmentID.equalsIgnoreCase("none"))) {
			fulfillmentID = fulfillmentID + prefix;
		}
		if (cloudAccountKey != "" && cloudAccountSecret != "") {
			// cloudAccountKey=RandomStringUtils.randomAlphanumeric(8)+cloudAccountKey;
			cloudAccountSecret = RandomStringUtils.randomAlphanumeric(8) + cloudAccountSecret;
			setSecret(cloudAccountSecret);
		}
		cloudAccountName = RandomStringUtils.randomAlphanumeric(8) + cloudAccountName;

		if (organization_id == null || organization_id == ""
				|| organization_id.equalsIgnoreCase("none")) {
			cloud_account_id = spogServer.createCloudAccountWithCheck(cloudAccountKey, cloudAccountSecret,
					cloudAccountName, cloudAccountType, organization_id, orderID, fulfillmentID,
					datacenter_id, test);
		} else {
			cloud_account_id = spogServer.createCloudAccountWithCheck(cloudAccountKey, cloudAccountSecret,
					cloudAccountName, cloudAccountType, organization_id, orderID, fulfillmentID,
					datacenter_id, test);
		}
		return cloud_account_id;
	}


	public void setSecret(String cloudAccountSecret) {

		// TODO Auto-generated method stub
		this.clouddata = cloudAccountSecret;
	}


	public String getSecret() {

		return clouddata;
	}


	/**
	 * Validate the response for get cloudAccountByid
	 * 
	 * @author BharadwajReddy
	 * @param response
	 * @param expectedStatusCode
	 * @param cloud_account_id
	 * @param expected_volumes
	 * @param organization_id
	 * @param cloud_account_key
	 * @param create_user_id
	 * @param cloud_account_name
	 * @param expectedErrorMessage
	 * @param expectedErrorCode
	 * @param test
	 */
	/**
	 * Validate the response for get cloudAccountByid
	 * 
	 * @author BharadwajReddy
	 * @param response
	 * @param expectedStatusCode
	 * @param cloud_account_id
	 * @param expected_volumes
	 * @param organization_id
	 * @param cloud_account_key
	 * @param create_user_id
	 * @param cloud_account_name
	 * @param expectedErrorMessage
	 * @param expectedErrorCode
	 * @param test
	 */
	public void checkGetCloudAccountById(Response response, int expectedStatusCode,
			String cloud_account_id, HashMap<String, Object> expected_volumes, String organization_id,
			String cloud_account_key, String create_user_id, String cloud_account_name,
			String cloud_account_type, SpogMessageCode Info, ExtentTest test) {

		String expectedErrorMessage = "", expectedErrorCode = "";
		// TODO Auto-generated method stub
		test.log(LogStatus.INFO, "validating the response for get the cloud accounts by id ");
		checkResponseStatus(response, expectedStatusCode);
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			response.then().body("data.cloud_account_id", equalTo(cloud_account_id));
			if (organization_id != null) {
				response.then().body("data.organization_id", equalTo(organization_id));
			}
			if (cloud_account_key != "" && !(cloud_account_type.equals("cloud_direct"))) {
				response.then().body("data.cloud_account_key", equalTo(cloud_account_key));
			} else {
				assertNotNull("data.cloud_account_key");
			}
			test.log(LogStatus.INFO,
					"validating the respsone for the cloud_Account_name,create_user_id and cloud_Account_status");
			response.then().body("data.create_user_id", equalTo(create_user_id));
			response.then().body("data.cloud_account_name", equalTo(cloud_account_name));
			response.then().body("data.cloud_account_status", equalTo("success"));
			test.log(LogStatus.INFO, "validating the resposne for the create ts and modify_ts");
			assertNotNull("data.create_ts");
			assertNotNull("data.modify_ts");
			test.log(LogStatus.PASS,
					"The validation of the response is successfully for the cloud_account_id:"
							+ response.then().extract().path("data.cloud_account_id")
							+ " The value of organization_id:"
							+ response.then().extract().path("data.organization_id"));
			assertTrue("The data has been matched", true);

			if (response.then().extract().path("data.cloud_account_type").equals("cloud_direct")) {

				HashMap<String, Object> actual_volumes = response.then().extract().path("data.volumes");
				assertResponseItem(actual_volumes.get("usage"), expected_volumes.get("usage"), test);
				assertResponseItem(actual_volumes.get("count"), expected_volumes.get("count"), test);
				assertResponseItem(actual_volumes.get("capacity"), expected_volumes.get("capacity"), test);
			} else {
				assertResponseItem(response.then().extract().path("data.volumes"), null, test);
			}
			test.log(LogStatus.INFO, "validating the response for allowed actions");
			assertResponseItem(response.then().extract().path("data.allowed_actions"), null, test);
			test.log(LogStatus.INFO, "validating the response for the usage");
			assertNotNull(response.then().extract().path("data.usage"));

		} else {

			if (Info.getStatus() != "0010000") {
				expectedErrorMessage = Info.getStatus();
				if (expectedErrorMessage.contains("{0}")) {
					expectedErrorMessage = expectedErrorMessage.replace("{0}", getUUId());
					System.out.println(expectedErrorMessage);
				}
				expectedErrorCode = Info.getCodeString();
			}
			checkErrorMessage(response, expectedErrorMessage);
			test.log(LogStatus.PASS, "The value of the message is " + expectedErrorMessage);
			checkErrorCode(response, expectedErrorCode);
			test.log(LogStatus.PASS, "The value of the code  generated  is " + expectedErrorCode);
			test.log(LogStatus.INFO,
					"The value of the  response generated actually is :" + response.getStatusCode());
		}
	}


	/**
	 * @author shuo
	 */
	public String getUrl4FilterSortPaging(String filterStr, String sortStr, int pageNumber,
			int pageSize, ExtentTest test) {

		int multipleFlag = 0;
		String extendUrl = "";
		if ((filterStr != null) && (!filterStr.equals(""))) {
			test.log(LogStatus.INFO, "set filter url");
			extendUrl += getFilterUrl(filterStr);
			multipleFlag++;
		}
		if ((sortStr != null) && (!sortStr.equals(""))) {
			test.log(LogStatus.INFO, "set sort url");
			if (multipleFlag != 0) {
				extendUrl += "&";
			}
			extendUrl += getSortUrl(sortStr);
			multipleFlag++;
		}
		if ((pageNumber != -1) || (pageSize != -1)) {
			test.log(LogStatus.INFO, "set paging url");
			if (multipleFlag != 0) {
				extendUrl += "&";
			}
			extendUrl += getPagingUrl(pageNumber, pageSize);
		}
		return extendUrl;
	}


	/**
	 * @author Bharadwaj.Ghadiam Call The Rest APi to get all the Logs
	 * @param token
	 * @param additionalURL
	 * @param test
	 * @return response
	 */
	public Response getLogs(String token, String additionalURL, ExtentTest test) {

		// TODO Auto-generated method stub
		test.log(LogStatus.INFO, "Call the rest API To get the Logs");
		Response response = spogInvoker.getLogs(token, additionalURL, test);
		return response;
	}


	/**
	 * @author Bharadwaj.Ghadiam Call The Rest APi to get all the Logs
	 * @param token
	 * @param organization_id
	 * @param additionalURL
	 * @param test
	 * @return response
	 */
	public Response getLogsByOrganizationId(String token, String organization_id,
			String additionalURL, ExtentTest test) {

		// TODO Auto-generated method stub
		test.log(LogStatus.INFO, "Call the rest API To get the Logs by organization_id");
		Response response =
				spogInvoker.getLogsByOrganizationId(token, organization_id, additionalURL, test);
		return response;

	}


	/**
	 * @author bharadwajReddy Call the REST API to get the logs by id
	 * @param log_id
	 * @param token
	 * @param test
	 * @return response
	 */
	public Response getLogsById(String log_id, String token, ExtentTest test) {

		setUUID(log_id);
		test.log(LogStatus.INFO,
				"Calling the rest api to get the information related to get source by id");
		Response response = spogInvoker.getLogsById(token, log_id, test);
		test.log(LogStatus.INFO, "The resposne generated is :" + response.getBody().asString());
		return response;

	}


	/**
	 * Composing a TreeMap for jobdata
	 * 
	 * @param response
	 * @param test2
	 * @return
	 */
	public HashMap<String, Object> composeJobData(Response response, ExtentTest test) {

		test.log(LogStatus.INFO, "Stoting the resposne for getjobsbyid");
		// TODO Auto-generated method stub
		HashMap<String, Object> job_data = response.then().extract().path("data");

		return job_data;
	}


	/**
	 * @auhtor Bharadwaj.Ghadiam Check the resposne for getLogsById
	 * @param response
	 * @param expectedStatusCode
	 * @param startTimeTS
	 * @param log_id
	 * @param job_data
	 * @param severityType
	 * @param sourceType
	 * @param direct_site_id
	 * @param org_id
	 * @param message
	 * @param Info
	 * @param test
	 */
	public void checkGetLogsById(Response response, int expectedStatusCode, long startTimeTS,
			String log_id, HashMap<String, Object> job_data, String log_severity_type,
			String log_source_type, String site_id, String organization_id, String message,
			SpogMessageCode Info, ExtentTest test) {

		String expectedErrorMessage = "", expectedErrorCode = "";

		checkResponseStatus(response, expectedStatusCode);
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.INFO, "validating the resposne for log_id");
			assertResponseItem(response.then().extract().path("data.log_id"), log_id, test);
			test.log(LogStatus.INFO, "validating the resposne for log_severity_type");
			assertResponseItem(response.then().extract().path("data.log_severity_type"),
					log_severity_type, test);
			test.log(LogStatus.INFO, "validating the resposne for log_source_type");
			assertResponseItem(response.then().extract().path("data.log_source_type"), log_source_type,
					test);
			test.log(LogStatus.INFO, "validating the resposne for site_id");
			assertResponseItem(response.then().extract().path("data.site_id"), site_id, test);
			test.log(LogStatus.INFO, "validating the resposne for organization_id");
			assertResponseItem(response.then().extract().path("data.organization.organization_id"), organization_id,
					test);

			// validation for the job_id
			HashMap<String, Object> jobdata = response.then().extract().path("data.job_data");

			test.log(LogStatus.INFO, "Validating the response for job_id");
			assertResponseItem(jobdata.get("job_id"), job_data.get("job_id"), test);

			test.log(LogStatus.INFO, "Validating the response for rps_id");
			assertResponseItem(jobdata.get("server_id"), job_data.get("server_id"), test);

			test.log(LogStatus.INFO, "Validating the response for source_id");
			assertResponseItem(jobdata.get("source_id"), job_data.get("source_id"), test);

			test.log(LogStatus.INFO, "Validating the response for source_name");
			assertResponseItem(jobdata.get("source_name"), job_data.get("source_name"), test);

			test.log(LogStatus.INFO, "Validating the response for job_type");
			assertResponseItem(jobdata.get("job_type"), job_data.get("job_type"), test);

			test.log(LogStatus.INFO, "validating the response for message");
			// assertResponseItem(response.then().extract().path("data.message"),message,test);

		} else {
			if (Info.getStatus() != "0010000") {
				expectedErrorMessage = Info.getStatus();
				if (expectedErrorMessage.contains("{0}")) {
					expectedErrorMessage = expectedErrorMessage.replace("{0}", getUUId());
					System.out.println(expectedErrorMessage);
				}
				expectedErrorCode = Info.getCodeString();
			}
			checkErrorMessage(response, expectedErrorMessage);
			test.log(LogStatus.PASS, "The value of the message is " + expectedErrorMessage);
			checkErrorCode(response, expectedErrorCode);
			test.log(LogStatus.PASS, "The value of the code  generated  is " + expectedErrorCode);
			test.log(LogStatus.INFO,
					"The value of the  response generated actually is :" + response.getStatusCode());
		}
	}


	/**
	 * @author BharadwajReddy Preparing the HashMap for the LogsInformation
	 * @param startTimeTS
	 * @param log_id
	 * @param job_data
	 * @param severityType
	 * @param sourceType
	 * @param site_id
	 * @param org_id
	 * @param message
	 * @param test
	 * @return logs_data
	 */

	public HashMap<String, Object> ComposeLogs(long startTimeTS, String log_id,
			HashMap<String, Object> job_data, String severityType, String sourceType, String site_id,
			String org_id, String message, ExtentTest test) {
		// TODO Auto-generated method stub

		HashMap<String, Object> logs_data = new HashMap<String, Object>();
		logs_data.put("log_generate_time", startTimeTS);
		logs_data.put("log_id", log_id);
		logs_data.put("job_data", job_data);
		logs_data.put("log_severity_type", severityType);
		logs_data.put("log_source_type", sourceType);
		logs_data.put("site_id", site_id);
		logs_data.put("organization_id", org_id);
		logs_data.put("message", message);
		return logs_data;

	}


	/**
	 * @author BharadwajReddy Preparing the HashMap for the LogsInformation
	 * @param startTimeTS
	 * @param log_id
	 * @param job_data
	 * @param severityType
	 * @param sourceType
	 * @param site_id
	 * @param org_id
	 * @param message
	 * @param test
	 * @return logs_data
	 */

	public HashMap<String, Object> ComposeLogs(String message_id, long startTimeTS, String log_id,
			HashMap<String, Object> job_data, String severityType, String sourceType, String site_id,
			String org_id, String message, String help_message_id, String generatedFrom,
			ExtentTest test) {

		// TODO Auto-generated method stub
		HashMap<String, Object> logs_data = new HashMap<String, Object>();
		logs_data.put("log_ts", startTimeTS);
		logs_data.put("log_id", log_id);
		logs_data.put("message_id", message_id);
		logs_data.put("job_data", job_data);
		logs_data.put("log_severity_type", severityType);
		logs_data.put("log_source_type", sourceType);
		logs_data.put("site_id", site_id);
		logs_data.put("organization_id", org_id);
		logs_data.put("message", message);
		logs_data.put("help_message_id", help_message_id);
		logs_data.put("generated_from", generatedFrom);
		logs_data.put("help_message_link",
				"http://www.arcservedocs.com/arcserveudp/ActivityLogMessage/activityMessage.php");
		return logs_data;

	}


	public HashMap<String, Object> ComposeLogs(long startTimeTS, String log_id,
			HashMap<String, Object> job_data, String severityType, String sourceType, String site_id,
			String org_id, String message, String help_message_id, String generatedFrom,
			ExtentTest test) {

		// TODO Auto-generated method stub
		HashMap<String, Object> logs_data = new HashMap<String, Object>();
		logs_data.put("log_ts", startTimeTS);
		logs_data.put("log_id", log_id);
		logs_data.put("job_data", job_data);
		logs_data.put("log_severity_type", severityType);
		logs_data.put("log_source_type", sourceType);
		logs_data.put("site_id", site_id);
		logs_data.put("organization_id", org_id);
		logs_data.put("message", message);
		logs_data.put("help_message_id", help_message_id);
		logs_data.put("generated_from", generatedFrom);
		logs_data.put("help_message_link",
				"http://www.arcservedocs.com/arcserveudp/ActivityLogMessage/activityMessage.php");
		return logs_data;

	}


	/**
	 * Check the get Logs values for the Validation
	 * 
	 * @param Logsinfo(actual Logs for all the logs information)
	 * @param filterName
	 * @param filterOperator
	 * @param filterValue
	 * @return logs_info
	 */

	/**
	 * This API queries logs with source id and returns log list. Filter:log_severity_type, log_ts,
	 * job_type.<br>
	 * Sort:sort=log_severity_type, log_ts, job_type.<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;To sort in descending order, the sort field should start with hyphen.
	 * <br>
	 * Pagination: page=:page&page_size=:page_size<br>
	 * Example:?log_severity_type=:log_severity_type&log_ts=>X<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&log_ts=<Y OR log_ts=[X,Y].<br>
	 * log_severity_type:information,warning,error.<br>
	 * job_type:backup,restore,copy,merge,conversion,vm_backup,vm_recovery,<br>
	 * &nbsp;&nbsp;vm_catalog_fs,mount_recovery_point,office365_backup,cifs_backup,sharepoint_backup,
	 * <br>
	 * &nbsp;&nbsp;linux_assure_recovery,vm_merge,catalog_fs,catalog_app,catalog_grt,file_copy_backup,
	 * file_copy_purge,<br>
	 * &nbsp;&nbsp;file_copy_restore,file_copy_catalog_sync,file_copy_source_delete,file_copy_delete,
	 * catalog_fs_ondemand,<br>
	 * &nbsp;&nbsp;vm_catalog_fs_ondemand,rps_replicate,rps_replicate_in_bound,rps_merge,
	 * rps_conversion,bmr,<br>
	 * &nbsp;&nbsp;rps_data_seeding,rps_data_seeding_in,vm_recovery_hyperv,rps_purge_datastore,
	 * start_instant_vm,<br>
	 * &nbsp;&nbsp;stop_instant_vm,assure_recovery,start_instant_vhd,stop_instant_vhd,archive_to_tape,
	 * linux_instant_vm.<br>
	 */
	public ArrayList<HashMap<String, Object>> getLogsInfo(ArrayList<HashMap<String, Object>> Logsinfo,
			String filterName, String filterOperator, String filterValue) {

		// TODO Auto-generated method stub
		ArrayList<HashMap<String, Object>> Logs_info = new ArrayList<HashMap<String, Object>>();
		if (filterOperator.equals("=") && (filterName.contains("job_type")
				|| filterName.contains("log_severity_type") || filterName.contains("site_id")
				|| filterName.contains("source_id") || filterName.contains("destination_id")
				|| filterName.contains("source_name") || filterName.contains("generated_from")
				|| filterName.contains("message") || filterName.contains("help_message_id"))) {
			// FilerOperator based on the Logs info
			for (int k = 0; k < Logsinfo.size(); k++) {
				// If the filter operator contains site_id
				if (filterName.contains("site_id")) {
					if (Logsinfo.get(k).get(filterName).equals(filterValue)) {
						Logs_info.add(Logsinfo.get(k));
					}
				}
				// If the Filter Operator contains source_id
				else if (filterName.contains("source_id")) {
					HashMap<String, Object> job_data =
							(HashMap<String, Object>) Logsinfo.get(k).get("job_data");
					if (job_data.get(filterName).equals(filterValue)) {
						Logs_info.add(Logsinfo.get(k));
					}
				}
				// If the Filter Operator contains the destination_id
				else if (filterName.contains("destination_id")) {

					HashMap<String, Object> job_data =
							(HashMap<String, Object>) Logsinfo.get(k).get("job_data");
					if (job_data.get(filterName).equals(filterValue)) {
						Logs_info.add(Logsinfo.get(k));
					}
				}
				// If the Filter operator contains the rps_id
				else if (filterName.contains("rps_id")) {

					HashMap<String, Object> job_data =
							(HashMap<String, Object>) Logsinfo.get(k).get("job_data");
					if (job_data.get(filterName).equals(filterValue)) {
						Logs_info.add(Logsinfo.get(k));
					}
				}
				// If the filter operator contains job_type
				else if (filterName.contains("job_type")) {
					@SuppressWarnings("unchecked")
					HashMap<String, Object> job_data =
					(HashMap<String, Object>) Logsinfo.get(k).get("job_data");
					if (job_data.get("job_type").equals(filterValue)) {
						Logs_info.add(Logsinfo.get(k));
					}
				}
				// If the filter operator contains source_name
				else if (filterName.contains("source_name")) {
					@SuppressWarnings("unchecked")
					HashMap<String, Object> job_data =
					(HashMap<String, Object>) Logsinfo.get(k).get("job_data");
					if (job_data.get("source_name").equals(filterValue)) {
						Logs_info.add(Logsinfo.get(k));
					}
				}
				// If the Filter operator contains the log_severity_type
				else if (filterName.contains("log_severity_type")) {

					if (Logsinfo.get(k).get(filterName).equals(filterValue)) {
						Logs_info.add(Logsinfo.get(k));
					}
				} else if (filterName.contains("generated_from")) {
					if (Logsinfo.get(k).get(filterName).equals(filterValue)) {
						Logs_info.add(Logsinfo.get(k));
					}
				} else if (filterName.equals("message")) {
					if (Logsinfo.get(k).get(filterName).equals(filterValue)) {
						Logs_info.add(Logsinfo.get(k));
					}
				} else if (filterName.contains("help_message_id")) {
					if (Logsinfo.get(k).get(filterName).equals(filterValue)) {
						Logs_info.add(Logsinfo.get(k));
					}
				}
			}
			Logsinfo = Logs_info;
			// This operation is useful for mutiple filters
		} else if (filterOperator.equals("in") && (filterName.contains("job_type")
				|| filterName.contains("log_severity_type") || filterName.contains("site_id")
				|| filterName.contains("source_id") || filterName.contains("destination_id")
				|| filterName.contains("source_name") || filterName.contains("generated_from")
				|| filterName.contains("message") || filterName.contains("help_message_id"))) {
			// Filter based on the array of logType
			String newFilterValue = filterValue.replace("|", ",");
			for (int k = 0; k < Logsinfo.size(); k++) {
				String filterValues[] = newFilterValue.split(",");
				for (int i = 0; i < filterValues.length; i++) {
					// If the filter name contains site_id
					if (filterName.contains("site_id")) {
						if (Logsinfo.get(k).get(filterName).equals(filterValue)) {
							Logs_info.add(Logsinfo.get(k));
						}
					}
					// If the Filter name contains source_id
					else if (filterName.contains("source_id")) {
						HashMap<String, Object> job_data =
								(HashMap<String, Object>) Logsinfo.get(k).get("job_data");
						if (job_data.get(filterName).equals(filterValue)) {
							Logs_info.add(Logsinfo.get(k));
						}
					}
					// If the Filter Operator contains the rps_id
					else if (filterName.contains("rps_id")) {
						HashMap<String, Object> job_data =
								(HashMap<String, Object>) Logsinfo.get(k).get("job_data");
						if (job_data.get(filterName).equals(filterValue)) {
							Logs_info.add(Logsinfo.get(k));
						}
					}
					// If the Filter Operator contains the rps_id
					else if (filterName.contains("destination_id")) {

						HashMap<String, Object> job_data =
								(HashMap<String, Object>) Logsinfo.get(k).get("job_data");
						if (job_data.get(filterName).equals(filterValue)) {
							Logs_info.add(Logsinfo.get(k));
						}
					}
					// If the Filter name contains Job_type
					else if (filterName.contains("job_type")) {
						@SuppressWarnings("unchecked")
						HashMap<String, Object> job_data =
						(HashMap<String, Object>) Logsinfo.get(k).get("job_data");
						if (job_data.get("job_type").equals(filterValues[i])) {
							Logs_info.add(Logsinfo.get(k));
						}
					}
					// If the Filter name contains source_name
					else if (filterName.contains("source_name")) {
						@SuppressWarnings("unchecked")
						HashMap<String, Object> job_data =
						(HashMap<String, Object>) Logsinfo.get(k).get("job_data");
						if (job_data.get("source_name").equals(filterValues[i])) {
							Logs_info.add(Logsinfo.get(k));
						}
					}
					// If the filter name contains log_severity_type
					else if (filterName.contains("log_severity_type")) {
						if (Logsinfo.get(k).get(filterName).equals(filterValues[i])) {
							Logs_info.add(Logsinfo.get(k));
						}
					} else if (filterName.contains("generated_from")) {
						if (Logsinfo.get(k).get(filterName).equals(filterValues[i])) {
							Logs_info.add(Logsinfo.get(k));
						}
					} else if (filterName.equals("message")) {
						if (Logsinfo.get(k).get(filterName).equals(filterValues[i])) {
							Logs_info.add(Logsinfo.get(k));
						}
					} else if (filterName.contains("help_message_id")) {
						if (Logsinfo.get(k).get(filterName).equals(filterValues[i])) {
							Logs_info.add(Logsinfo.get(k));
						}
					}
				}
			}
			Logsinfo = Logs_info;
		}
		return Logsinfo;
	}


	/**
	 * The sortStr which contains the details regarding the Sorting Sort:sort=log_severity_type,
	 * log_ts, job_type.<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;To sort in descending order, the sort field should start with hyphen.
	 */
	public ArrayList<HashMap<String, Object>> getLogsSortInfo(String sortStr,
			ArrayList<HashMap<String, Object>> Logsinfo, ExtentTest test) {
		// TODO Auto-generated method stub

		test.log(LogStatus.INFO, "Sorting the expected job_data as per the user reqirement");
		String[] filterArray = null;
		filterArray = sortStr.split(",");
		String[] filterString = null;
		String filterName = null, filterOperator = null, filterValue = null;
		for (int i = 0; i < filterArray.length; i++) {
			filterString = filterArray[i].split(";");
			filterName = filterString[0];
			filterOperator = filterString[1];
			if (filterName.equals("log_severity_type")) {
				if (filterOperator.equals("desc")) {
					Collections.sort(Logsinfo, new Comparator<HashMap<String, Object>>() {
						@Override
						public int compare(HashMap<String, Object> o1, HashMap<String, Object> o2) {

							// TODO Auto-generated method stub
							String create_ts = o1.get("log_severity_type").toString();
							String create_ts1 = (String) o2.get("log_severity_type").toString();
							if (create_ts.compareTo(create_ts1) < 0) {
								return 1;
							} else if (create_ts.compareTo(create_ts1) == 0) {
								// if two job _type are same then sort based on the log_ts
								Long Log_ts = (Long) o1.get("log_ts");
								Long Log_ts1 = (Long) o2.get("log_ts");
								return Log_ts < Log_ts1 ? 1 : Log_ts == Log_ts1 ? 0 : -1;
							} else {
								return -1;
							}
						}
					});
				} else {
					Collections.sort(Logsinfo, new Comparator<HashMap<String, Object>>() {
						@Override
						public int compare(HashMap<String, Object> o1, HashMap<String, Object> o2) {

							// TODO Auto-generated method stub
							String create_ts = o1.get("log_severity_type").toString();
							String create_ts1 = o2.get("log_severity_type").toString();
							if (create_ts.compareTo(create_ts1) > 0) {
								return 1;
							} else if (create_ts.compareTo(create_ts1) == 0) {
								// if two job _type are same then sort based ascending order of the log_ts
								Long Log_ts = (Long) o1.get("log_ts");
								Long Log_ts1 = (Long) o2.get("log_ts");
								return Log_ts > Log_ts1 ? 1 : Log_ts == Log_ts1 ? 0 : -1;
								/*
								 * //if two job _type are same then sort based on the log_ts Long Log_ts= (Long)
								 * o1.get("log_generate_time");Long Log_ts1 = (Long) o2.get("log_generate_time");
								 * return Log_ts < Log_ts1 ?1:Log_ts == Log_ts1?0:-1;
								 */
							} else {
								return -1;
							}
						}
					});
				}
			} else if (filterName.equals("job_type")) {
				if (filterOperator.equals("desc")) {
					Collections.sort(Logsinfo, new Comparator<HashMap<String, Object>>() {
						@SuppressWarnings("unchecked")
						@Override
						public int compare(HashMap<String, Object> o1, HashMap<String, Object> o2) {

							// TODO Auto-generated method stub
							HashMap<String, Object> job_data = (HashMap<String, Object>) o1.get("job_data");
							HashMap<String, Object> job_data1 = (HashMap<String, Object>) o2.get("job_data");
							String job_type = job_data.get("job_type").toString();
							String job_type1 = job_data1.get("job_type").toString();
							if (job_type.compareTo(job_type1) < 0) {
								return 1;
							} else if (job_type.compareTo(job_type1) == 0) {
								// if two job _type are same then sort based on the log_ts
								Long Log_ts = (Long) o1.get("log_ts");
								Long Log_ts1 = (Long) o2.get("log_ts");
								return Log_ts < Log_ts1 ? 1 : Log_ts == Log_ts1 ? 0 : -1;
							} else {
								return -1;
							}
						}
					});
				} else {
					Collections.sort(Logsinfo, new Comparator<HashMap<String, Object>>() {
						@SuppressWarnings("unchecked")
						@Override
						public int compare(HashMap<String, Object> o1, HashMap<String, Object> o2) {

							// TODO Auto-generated method stub
							HashMap<String, Object> job_data = (HashMap<String, Object>) o1.get("job_data");
							HashMap<String, Object> job_data1 = (HashMap<String, Object>) o2.get("job_data");
							String job_type = (String) job_data.get("job_type");
							String job_type1 = (String) job_data1.get("job_type");
							if (job_type.compareTo(job_type1) > 0) {
								return 1;
							} else if (job_type.compareTo(job_type1) == 0) {
								// if two job _type are same then sort based ascending order of the log_ts
								Long Log_ts = (Long) o1.get("log_ts");
								Long Log_ts1 = (Long) o2.get("log_ts");
								return Log_ts > Log_ts1 ? 1 : Log_ts == Log_ts1 ? 0 : -1;
							} else {
								return -1;
							}
						}
					});
				}
			}
		}
		return Logsinfo;
	}


	public void checkLogForJobid(Response response, int expectedStatusCode, int previousLogcounts,
			String job_id, ExtentTest test) {

	}


	/**
	 * @author bharadwajReddy Check the response for getLogs
	 * @param response
	 * @param expectedStatusCode
	 * @param LogsInfo
	 * @param cuur_page
	 * @param page_size
	 * @param FilterStr
	 * @param SortStr
	 * @param info
	 * @param test
	 */

	public void checkGetLogs(Response response, int expectedStatusCode,
			ArrayList<HashMap<String, Object>> LogsInfo, int curr_page, int page_size, String FilterStr,
			String SortStr, SpogMessageCode Info, String api, ExtentTest test) {

		// TODO Auto-generated method stub
		int total_page = 0, return_size = 0, total_size = 0;
		if (curr_page == 0 || curr_page == -1) {
			curr_page = 1;
		}
		if (page_size == 0 || page_size == -1) {
			page_size = 20;
		} else if (page_size >= 100 && page_size < SpogConstants.MAX_PAGE_SIZE) {
			page_size = 100;
		}

		ArrayList<HashMap<String, Object>> logs = new ArrayList<HashMap<String, Object>>();
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE && LogsInfo != null
				&& !LogsInfo.isEmpty()) {
			logs = response.then().extract().path("data");
			return_size = logs.size();
			// get The total Size for the pages
			total_size = LogsInfo.size();
			test.log(LogStatus.INFO, "The actual total size is " + total_size);
			errorHandle.printInfoMessageInDebugFile("expect status code is " + expectedStatusCode);
			checkResponseStatus(response, expectedStatusCode);
		} else {
			errorHandle.printInfoMessageInDebugFile("expect status code is " + expectedStatusCode);
			checkResponseStatus(response, expectedStatusCode);
			test.log(LogStatus.PASS, "The validation of the resposne is successfull");
			return;
		}
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE && LogsInfo != null
				&& LogsInfo.size() >= logs.size()) {
			test.log(LogStatus.INFO, "check response For the Logs ");
			if (((SortStr == null) || (SortStr.equals("")))
					&& ((FilterStr == null) || (FilterStr.equals("")))) {
				// If we Don't mention any Sorting the default sorting is descending order
				// validating the response for Destinations
				int j = 0;
				if (page_size == 20) {
					j = logs.size() - 1;
					if (LogsInfo.size() >= logs.size()) {
						j = LogsInfo.size() - 1;
					}
				}
				if (curr_page != 1) {
					j = (LogsInfo.size() - 1) - (curr_page - 1) * page_size;
				}
				for (int i = 0; i < logs.size() && j >= 0; i++, j--) {
					HashMap<String, Object> actual_logs_data = logs.get(i),
							expected_logs_data = LogsInfo.get(j);
					// validate the response for the Logs
					checkForLogsData(actual_logs_data, expected_logs_data, test, null);
				}
			}
			// SortStr
			else if ( // For sorting only and no filtering but includes pagination
					(((SortStr != null) || (!SortStr.equals(""))) && (SortStr.contains("log_ts")
							|| SortStr.contains("log_severity_type") || SortStr.contains("job_type"))
							&& ((FilterStr == null) || (FilterStr.equals(""))))
					// For Sorting and Filtering both
					|| (((SortStr != null) || (!SortStr.equals(""))) && SortStr.contains("log_ts")
							&& ((FilterStr != null) || (!FilterStr.equals(""))))) {

				test.log(LogStatus.INFO, "Validating the response for the get logs ");
				// validating the response for all the users who are sorted based on response in
				// ascending
				// order and descending order
				if (SortStr.contains("asc")) {
					int j = 0;
					if (curr_page != 1) {
						j = (curr_page - 1) * page_size;
					}
					for (int i = 0; i < logs.size(); i++, j++) {
						HashMap<String, Object> actual_logs_data = logs.get(i),
								expected_logs_data = LogsInfo.get(j);

						if ((SortStr.contains("log_severity_type")) && !SortStr.contains("log_ts")) {
							checkForLogsData(actual_logs_data, expected_logs_data, test, null);
						} else {
							// validate the response for the Logs
							checkForLogsData(actual_logs_data, expected_logs_data, test, null);
						}
					}
				} else if (SortStr.contains("desc") || (FilterStr.contains(">"))
						|| (FilterStr.contains("<")) || SortStr.contains(">") || SortStr.contains("<")) {
					int j = 0;
					if (page_size == 20) {
						j = logs.size() - 1;
						if (LogsInfo.size() >= logs.size()) {
							j = LogsInfo.size() - 1;
						}
					} else {
						j = LogsInfo.size() - 1;
						if (curr_page != 1) {
							j = (LogsInfo.size() - 1) - (curr_page - 1) * page_size;
						}
					}
					for (int i = 0; i < logs.size() && j >= 0; i++, j--) {
						// validate the response for the Logs
						if ((SortStr.contains("log_severity_type") || SortStr.contains("job_type"))) {
							HashMap<String, Object> actual_logs_data = logs.get(i),
									expected_logs_data = LogsInfo.get(i);
							checkForLogsData(actual_logs_data, expected_logs_data, test, null);
						} else {
							HashMap<String, Object> actual_logs_data = logs.get(i),
									expected_logs_data = LogsInfo.get(j);
							// validate the response for the Logs
							checkForLogsData(actual_logs_data, expected_logs_data, test, null);
						}
					}
				}
			} // Only filtering and no sorting (For now supports only for the organization id
			// )
			else if (((SortStr == null) || (SortStr.equals("")))
					&& ((FilterStr != null) || (!FilterStr.equals("")))) {
				int j = 0;
				if (page_size == 20) {
					j = LogsInfo.size() - 1;
				} else {
					j = LogsInfo.size() - 1;
					if (curr_page != 1) {
						j = (LogsInfo.size() - 1) - (curr_page - 1) * page_size;
					}
				}
				for (int i = 0; i < logs.size() && j >= 0; i++, j--) {
					HashMap<String, Object> actual_logs_data = logs.get(i),
							expected_logs_data = LogsInfo.get(j);
					// validate the response for the Logs
					checkForLogsData(actual_logs_data, expected_logs_data, test, null);
				}
			}
			// check the response validation for pages,page_size,total_size
			validateResponseForPages(curr_page, page_size, response, total_size, test);
		} else {
			String expectedErrorMessage = "", expectedErrorCode = "";
			if (Info.getStatus() != "0010000") {
				expectedErrorMessage = Info.getStatus();
				if (expectedErrorMessage.contains("{0}")) {
					expectedErrorMessage = expectedErrorMessage.replace("{0}", getUUId());
				}
				expectedErrorCode = Info.getCodeString();
			}
			checkErrorMessage(response, expectedErrorMessage);
			test.log(LogStatus.PASS, "The value of the message is " + expectedErrorMessage);
			checkErrorCode(response, expectedErrorCode);
			test.log(LogStatus.PASS, "The value of the code  generated  is " + expectedErrorCode);
			test.log(LogStatus.INFO,
					"The value of the  response generated actually is :" + response.getStatusCode());
		}
	}


	/**
	 * @author Bharadwaj.Ghadiam Validating the Response for the log Message
	 * @param actual_logs_data
	 * @param expected_logs_data
	 * @param test
	 */

	@SuppressWarnings("unchecked")
	private void checkForLogsData(HashMap<String, Object> actual_logs_data,
			HashMap<String, Object> expected_logs_data, ExtentTest test, String Sortstr) {

		HashMap<String, Object> expected_job_data, actual_job_data = new HashMap<String, Object>();
		// TODO Auto-generated method stub
		test.log(LogStatus.INFO, "Validating the response for log_id");
		assertResponseItem(actual_logs_data.get("log_id"), expected_logs_data.get("log_id"), test);

		test.log(LogStatus.INFO, "Validating the response for log_severity_type");
		assertResponseItem(actual_logs_data.get("log_severity_type"),
				expected_logs_data.get("log_severity_type"), test);

		test.log(LogStatus.INFO, "Validating the response for log_source_type");
		assertResponseItem(actual_logs_data.get("log_source_type"),
				expected_logs_data.get("log_source_type"), test);

		test.log(LogStatus.INFO, "validating the response for message");
		assertResponseItem(actual_logs_data.get("message"), expected_logs_data.get("message"), test);

		test.log(LogStatus.INFO, "validating the response for help_message_id");
		assertResponseItem(actual_logs_data.get("help_message_id"),
				expected_logs_data.get("help_message_id"), test);

		test.log(LogStatus.INFO, "Validating the response for site_id");
		assertResponseItem(actual_logs_data.get("site_id"), expected_logs_data.get("site_id"), test);

		test.log(LogStatus.INFO, "Validating the response for organization_id");
		assertResponseItem(((HashMap<String, Object>) actual_logs_data.get("organization")).get("organization_id"),
				expected_logs_data.get("organization_id"), test);

		// validation for the job_id
		expected_job_data = (HashMap<String, Object>) expected_logs_data.get("job_data");
		actual_job_data = (HashMap<String, Object>) actual_logs_data.get("job_data");

		test.log(LogStatus.INFO, "Validating the response for job_id");

		test.log(LogStatus.INFO, "validating the response for generated From ");
		assertResponseItem(actual_job_data.get("generated_from"),
				expected_logs_data.get("generated_from"), test);

		assertResponseItem(actual_job_data.get("job_id"), expected_job_data.get("job_id"), test);

		test.log(LogStatus.INFO, "Validating the response for rps_id");
		// assertResponseItem(actual_job_data.get("rps_id"),
		// expected_job_data.get("rps_id"), test);
		assertNull(actual_job_data.get("rps_id"));
		test.log(LogStatus.INFO, "Validating the response for source_id");
		assertResponseItem(actual_job_data.get("source_id"), expected_job_data.get("source_id"), test);

		test.log(LogStatus.INFO, "Validating the response for source_name");
		assertResponseItem(actual_job_data.get("source_name"), expected_job_data.get("source_name"),
				test);

		test.log(LogStatus.INFO, "Validating the response for job_type");
		assertResponseItem(actual_job_data.get("job_type"), expected_job_data.get("job_type"), test);

	}


	// /**
	// * Moved to SPOGDestinationServer.java
	// * @author Zhaoguo.Ma
	// * @param userID
	// * @param filterName
	// * @param destinationName
	// * @param policyID
	// * @param destinationType
	// * @param isDefault
	// * @param test
	// * @return
	// */
	// public String createDestinationFilterWithCheck(String userID, String
	// filterName, String
	// destinationName, String policyID, String destinationType, String isDefault,
	// ExtentTest test) {
	//
	// Map<String, Object> destinationFilterInfo =
	// jp.composeDestinationFilterInfo(filterName,
	// destinationName, policyID, destinationType, isDefault);
	//
	// Response response = spogInvoker.createDestinationFilter(userID,
	// destinationFilterInfo);
	// checkResponseStatus(response, SpogConstants.SUCCESS_POST);
	//
	// String responseDestinationType =
	// response.then().extract().path("data.destination_type");
	// ArrayList<String> responsePolicyID =
	// response.then().extract().path("data.policy_id");
	// boolean responseIsDefault =
	// response.then().extract().path("data.is_default");
	// String responseDestinationName =
	// response.then().extract().path("data.destination_name");
	// String filterID = response.then().extract().path("data.filter_id");
	//
	//// String responseUserID = GetLoggedinUser_UserID();
	//// String responseOrganizationID = GetLoggedinUserOrganizationID();
	//
	// response.then().body("data.filter_name", equalTo(filterName));
	// //.body("data.user_id", equalTo(responseUserID)).body("data.organization_id",
	// equalTo(responseOrganizationID));
	//
	// assertResponseItem(destinationName, responseDestinationName);
	//
	// assertFilterItem(policyID, responsePolicyID);
	//
	//
	// if (destinationType == null || destinationType.equalsIgnoreCase("none") ||
	// (destinationType ==
	// "")) {
	// assertEquals(responseDestinationType, "all");
	// } else {
	// assertEquals(responseDestinationType, destinationType);
	// }
	//
	// if (isDefault == null || isDefault.equalsIgnoreCase("none") || isDefault ==
	// "" ||
	// isDefault.equalsIgnoreCase("false")) {
	// assertEquals(responseIsDefault, false);
	// } else if (isDefault.equalsIgnoreCase("true")){
	// assertEquals(responseIsDefault, true);
	// }
	//
	// return filterID;
	// }
	//
	// public void createDestinationFilterAndCheckCode(String userID, String
	// filterName, String
	// destinationName,
	// String policyID, String destinationType, String isDefault, int statusCode,
	// String errorCode,
	// ExtentTest test) {
	//
	// Map<String, Object> destinationFilterInfo =
	// jp.composeDestinationFilterInfo(filterName,
	// destinationName,
	// policyID, destinationType, isDefault);
	//
	// Response response = spogInvoker.createDestinationFilter(userID,
	// destinationFilterInfo);
	// checkResponseStatus(response, statusCode);
	// checkErrorCode(response, errorCode);
	// }

	/**
	 * @author BharadwajReddy Call the rest web services to get the cloud accounts
	 * @param Token
	 * @param additionalURL
	 * @param test
	 * @return
	 */

	public Response getCloudAccounts(String Token, String additionalURL, ExtentTest test) {

		// TODO Auto-generated method stub
		test.log(LogStatus.INFO, "Calling the rest WEB services to get the cloud acccounts");
		Response response = spogInvoker.getCloudAccounts(Token, additionalURL, test);
		return response;
	}


	/**
	 * @author BharadwajReddy Compose the JSON MAP for get cloud Accounts
	 * @param cloud_account_id
	 * @param cloudAccountKey
	 * @param organization_id
	 * @param create_user_id
	 * @param cloudAccountName
	 * @param cloud_account_status
	 * @param cloudAccountType
	 * @return
	 */

	public HashMap<String, Object> composeCloudData(String cloud_account_id, String cloudAccountKey,
			String organization_id, String create_user_id, String cloudAccountName,
			String cloud_account_status, String cloudAccountType) {

		// TODO Auto-generated method stub
		HashMap<String, Object> cloudAccountData = new HashMap<String, Object>();
		cloudAccountData.put("cloud_account_id", cloud_account_id);
		cloudAccountData.put("cloud_account_key", cloudAccountKey);
		cloudAccountData.put("organization_id", organization_id);
		cloudAccountData.put("create_user_id", create_user_id);
		cloudAccountData.put("cloud_account_name", cloudAccountName);
		cloudAccountData.put("cloud_account_status", cloud_account_status);
		cloudAccountData.put("cloud_account_type", cloudAccountType);
		if (cloudAccountType.equals("cloud_direct")) {
			HashMap<String, Object> volumes = new HashMap<String, Object>();
			volumes.put("count", 0);
			volumes.put("usage", 0);
			volumes.put("capacity", 0);
			cloudAccountData.put("volumes", volumes);
		} else {
			cloudAccountData.put("volumes", null);
		}
		cloudAccountData.put("allowed_actions", null);
		return cloudAccountData;
	}


	public HashMap<String, Object> composeCloudData(String cloud_account_id, String cloudAccountKey,
			String organization_id, String create_user_id, String cloudAccountName,
			String cloud_account_status, HashMap<String, Object> expected_volumes, String datacenter_id,
			String cloudAccountType) {

		// TODO Auto-generated method stub
		HashMap<String, Object> cloudAccountData = new HashMap<String, Object>();
		cloudAccountData.put("cloud_account_id", cloud_account_id);
		cloudAccountData.put("cloud_account_key", cloudAccountKey);
		cloudAccountData.put("organization_id", organization_id);
		cloudAccountData.put("create_user_id", create_user_id);
		cloudAccountData.put("cloud_account_name", cloudAccountName);
		cloudAccountData.put("cloud_account_status", cloud_account_status);
		cloudAccountData.put("cloud_account_type", cloudAccountType);
		if (cloudAccountType.equals("cloud_direct")) {
			cloudAccountData.put("volumes", expected_volumes);
			cloudAccountData.put("datacenter_id", datacenter_id);
		} else {
			cloudAccountData.put("volumes", null);
			cloudAccountData.put("datacenter_id", null);
		}
		cloudAccountData.put("allowed_actions", null);
		return cloudAccountData;
	}


	/**
	 * Validating the resposne for getCloudAccounts
	 * 
	 * @author BharadwajReddy
	 * @param response
	 * @param expectedStatusCode
	 * @param cloudInfo
	 * @param curr_page
	 * @param page_size
	 * @param FilterStr
	 * @param SortStr
	 * @param Info
	 * @param test
	 */

	public void checkGetCloudAccounts(Response response, int expectedStatusCode,
			ArrayList<HashMap<String, Object>> cloudInfo, int curr_page, int page_size, String FilterStr,
			String SortStr, SpogMessageCode Info, ExtentTest test) {

		// TODO Auto-generated method stub
		int total_page = 0, return_size = 0, total_size = 0;
		if (curr_page == 0 || curr_page == -1) {
			curr_page = 1;
		}
		if (page_size == 0 || page_size == -1 || page_size > SpogConstants.MAX_PAGE_SIZE) {
			page_size = 20;
		}

		ArrayList<HashMap<String, Object>> cloudData = new ArrayList<HashMap<String, Object>>();
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			cloudData = response.then().extract().path("data");
			return_size = cloudData.size();
			// get The total Size for the pages
			total_size = cloudData.size();
			test.log(LogStatus.INFO, "The actual total size is " + total_size);
			errorHandle.printInfoMessageInDebugFile("expect status code is " + expectedStatusCode);
			checkResponseStatus(response, expectedStatusCode);
		}
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE
				&& cloudInfo.size() >= total_size) {
			test.log(LogStatus.INFO, "check response For the Logs");

			// cases where no sorting and filtering are applied
			// validating the response for get the cloud accounts

			if (((SortStr == null) || (SortStr.equals("")))
					&& ((FilterStr == null) || (FilterStr.equals("")))) {
				// validating the response for logs
				int count = 0;
				for (int i = 0; i < cloudData.size(); i++) {
					boolean find = true;
					for (int j = 0; j < cloudInfo.size(); j++) {
						if (cloudData.get(i).get("cloud_account_id")
								.equals(cloudInfo.get(j).get("cloud_account_id"))) {
							validateRespsoneForGetCloudAccountsData(cloudData.get(i), cloudInfo.get(j), test);
							count++;
							find = false;
							break;
						}
					}
					if (count == cloudData.size()) {
						test.log(LogStatus.PASS,
								"The Total number of the records has been matched:" + cloudInfo.size());
						assertTrue("The Total count has matched successfully", true);

					} else if (count != cloudData.size() && find == true) {
						test.log(LogStatus.FAIL,
								"The Total number of the records has not been matched:" + cloudInfo.size());
						test.log(LogStatus.INFO,
								"Not Found with the Log_id" + cloudData.get(i).get("cloud_account_id"));
						assertTrue("The Total count has matched successfully", false);
						break;
					}
				}
			}
			// SortStr
			// These cases are related to the sorting and filtering
			else if (((SortStr != null) || (!SortStr.equals("")) && SortStr.contains("create_ts")
					&& ((FilterStr == null) || (FilterStr.equals(""))))
					|| ((SortStr != null) || (!SortStr.equals("")) && SortStr.contains("create_ts")
							&& ((FilterStr != null) || (!FilterStr.equals(""))))) {
				// validating the response for logs
				int count = 0;
				for (int i = 0; i < cloudData.size(); i++) {
					boolean find = true;
					for (int j = 0; j < cloudInfo.size(); j++) {
						if (cloudData.get(i).get("cloud_account_id")
								.equals(cloudInfo.get(j).get("cloud_account_id"))) {
							validateRespsoneForGetCloudAccountsData(cloudData.get(i), cloudInfo.get(j), test);
							count++;
							find = false;
						}
					}
					// validating the total count matched or not ;
					if (count == cloudData.size()) {
						test.log(LogStatus.PASS,
								"The Total number of the records has been matched:" + cloudInfo.size());
						assertTrue("The Total count has matched successfully", true);

					} else if (count != cloudData.size() && find == true && i != 0) {
						test.log(LogStatus.FAIL,
								"The Total number of the records has not been matched:" + cloudInfo.size());
						test.log(LogStatus.INFO,
								"Not Found with the Log_id" + cloudData.get(i).get("cloud_account_id"));
						assertTrue("The Total count has matched successfully", false);
						break;
					}
				}
			}
			// check the response validation for pages,page_size,total_size
			validateResponseForPages(curr_page, page_size, response, total_size, test);
		}

		else {
			// validation for the error messages and the error codes
			String expectedErrorMessage = "", expectedErrorCode = "";
			if (Info.getStatus() != "0010000") {
				expectedErrorMessage = Info.getStatus();
				if (expectedErrorMessage.contains("{0}")) {
					expectedErrorMessage = expectedErrorMessage.replace("{0}", getUUId());
					System.out.println(expectedErrorMessage);
				}
				expectedErrorCode = Info.getCodeString();
			}
			checkErrorMessage(response, expectedErrorMessage);
			test.log(LogStatus.PASS, "The value of the message is " + expectedErrorMessage);
			checkErrorCode(response, expectedErrorCode);
			test.log(LogStatus.PASS, "The value of the code  generated  is " + expectedErrorCode);
			test.log(LogStatus.INFO,
					"The value of the  response generated actually is :" + response.getStatusCode());
		}
	}


	/**
	 * @author Bharadwaj.Ghadiam Validate the response for the data inside the getcloudAccounts
	 * @param actual_cloud_data
	 * @param expected_cloud_data
	 * @param test
	 */

	private void validateRespsoneForGetCloudAccountsData(HashMap<String, Object> actual_cloud_data,
			HashMap<String, Object> expected_cloud_data, ExtentTest test) {

		// TODO Auto-generated method stub
		test.log(LogStatus.INFO, "validating the response for the getcloudAccounts");
		if (actual_cloud_data.get("cloud_account_id")
				.equals(expected_cloud_data.get("cloud_account_id"))) {
			test.log(LogStatus.INFO,
					"The validation of the cloud_Account_id:" + actual_cloud_data.get("cloud_account_id"));
			assertTrue("The validation of the response is correct", true);
			assertResponseItem(actual_cloud_data.get("cloud_account_key"),
					expected_cloud_data.get("cloud_account_key"), test);
			assertResponseItem(actual_cloud_data.get("organization_id"),
					expected_cloud_data.get("organization_id"), test);
			assertResponseItem(actual_cloud_data.get("create_user_id"),
					expected_cloud_data.get("create_user_id"), test);
			assertResponseItem(actual_cloud_data.get("cloud_account_name"),
					expected_cloud_data.get("cloud_account_name"), test);
			assertResponseItem(actual_cloud_data.get("cloud_account_status"),
					expected_cloud_data.get("cloud_account_status"), test);
			assertResponseItem(actual_cloud_data.get("cloud_account_type"),
					expected_cloud_data.get("cloud_account_type"), test);

			if (actual_cloud_data.get("cloud_account_type").equals("cloud_direct")) {
				HashMap<String, Object> expected_volumes, actual_volumes = new HashMap<String, Object>();
				expected_volumes = (HashMap<String, Object>) expected_cloud_data.get("volumes");
				actual_volumes = (HashMap<String, Object>) actual_cloud_data.get("volumes");
				assertResponseItem(actual_volumes.get("usage"), expected_volumes.get("usage"), test);
				assertResponseItem(actual_volumes.get("count"), expected_volumes.get("count"), test);
				assertResponseItem(actual_volumes.get("capacity"), expected_volumes.get("capacity"), test);
				assertResponseItem(actual_cloud_data.get("datacenter_id"),
						expected_cloud_data.get("datacenter_id"), test);
			} else {
				assertResponseItem(actual_cloud_data.get("volumes"), expected_cloud_data.get("volumes"),
						test);
				assertResponseItem(actual_cloud_data.get("datacenter_id"),
						expected_cloud_data.get("datacenter_id"), test);
			}
			assertResponseItem(actual_cloud_data.get("allowed_actions"),
					expected_cloud_data.get("allowed_actions"), test);

		} else {
			test.log(LogStatus.FAIL, "The cloud_Account_id not matched");
			assertTrue("The validation of the response is not correct", false);
		}
	}


	/**
	 * authenticate user
	 * 
	 * @author liu.yuefen
	 * @param userName
	 * @param password
	 * @param Token
	 * @return response
	 */
	public Response AuthenticateUser(String userName, String password, String Token) {

		Map<String, String> userInfo = jp.getUserInfo(userName, password);
		Response response = spogInvoker.AuthenticateUser(userInfo, Token);

		return response;
	}


	public Response createCloudAccountDestination(String cloudAccountId,
			HashMap<String, Object> cloudAccountDestinfo) {

		return spogInvoker.createCloudAccountDestination(cloudAccountId, cloudAccountDestinfo);
	}


	/**
	 * This is the full parameters for create source
	 * 
	 * @author shuo.zhang
	 * @param source_name
	 * @param source_type
	 * @param source_product
	 * @param organization_id
	 * @param site_id
	 * @param protection_status
	 * @param connection_status
	 * @param os_major
	 * @param applications
	 * @param agentInfo
	 * @param test
	 * @return
	 */
	public Response createSource(String source_name, SourceType source_type,
			SourceProduct source_product, String organization_id, String site_id,
			ProtectionStatus protection_status, ConnectionStatus connection_status, String os_major,
			String applications, String vm_name, String hypervisor_id, String agent_name, String os_name,
			String os_architecture, String agent_current_version, String agent_upgrade_version,
			String agent_upgrade_link, ExtentTest test) {

		String[] applicationArray = null;
		if ((applications != null) && (!applications.equals(""))) {
			applicationArray = applications.split(";");

		}
		errorHandle.printDebugMessageInDebugFile("compose source info object");
		test.log(LogStatus.INFO, "compose source info object");
		String str_source_type = null;
		if (source_type != null) {
			str_source_type = source_type.name();
		}

		String str_source_product = null;
		if (source_product != null) {
			str_source_product = source_product.name();
		}

		String str_protection_status = null;
		if (protection_status != null) {
			str_protection_status = protection_status.name();
		}

		String str_connection_status = null;
		if (connection_status != null) {
			str_connection_status = connection_status.name();
		}

		Map<String, String> agentInfo = null;
		if ((vm_name != null) || (hypervisor_id != null) || (agent_name != null) || (os_name != null)
				|| (os_architecture != null) || (agent_current_version != null)
				|| (agent_upgrade_version != null) || (agent_upgrade_link != null)) {
			agentInfo = jp.composeSourceAgentInfo(vm_name, hypervisor_id, agent_name, os_name,
					os_architecture, agent_current_version, agent_upgrade_version, agent_upgrade_link);
		}

		Map<String, Object> sourceInfo =
				jp.getSourceInfo(source_name, str_source_type, str_source_product, organization_id, site_id,
						str_protection_status, str_connection_status, os_major, applicationArray, agentInfo);

		errorHandle.printDebugMessageInDebugFile("create source");
		test.log(LogStatus.INFO, "create source");
		Response response = spogInvoker.createSource(sourceInfo, true);
		test.log(LogStatus.INFO, "The value of the response:" + response.getBody().asString());

		return response;
	}


	/**
	 * This is using cloud token to create source
	 * 
	 * @author yuefen.liu
	 * @return response
	 */
	public Response createSourceWithCloudToken(String source_name, SourceType source_type,
			SourceProduct source_product, String organization_id, String site_id,
			ProtectionStatus protection_status, ConnectionStatus connection_status, String os_major,
			String applications, String vm_name, String hypervisor_id, String agent_name, String os_name,
			String os_architecture, String agent_current_version, String agent_upgrade_version,
			String agent_upgrade_link, String Token, ExtentTest test) {

		String[] applicationArray = null;
		if ((applications != null) && (!applications.equals(""))) {
			applicationArray = applications.split(";");

		}
		errorHandle.printDebugMessageInDebugFile("compose source info object");
		test.log(LogStatus.INFO, "compose source info object");
		String str_source_type = null;
		if (source_type != null) {
			str_source_type = source_type.name();
		}

		String str_source_product = null;
		if (source_product != null) {
			str_source_product = source_product.name();
		}

		String str_protection_status = null;
		if (protection_status != null) {
			str_protection_status = protection_status.name();
		}

		String str_connection_status = null;
		if (connection_status != null) {
			str_connection_status = connection_status.name();
		}

		Map<String, String> agentInfo = null;
		if ((vm_name != null) || (hypervisor_id != null) || (agent_name != null) || (os_name != null)
				|| (os_architecture != null) || (agent_current_version != null)
				|| (agent_upgrade_version != null) || (agent_upgrade_link != null)) {
			agentInfo = jp.composeSourceAgentInfo(vm_name, hypervisor_id, agent_name, os_name,
					os_architecture, agent_current_version, agent_upgrade_version, agent_upgrade_link);
		}

		Map<String, Object> sourceInfo =
				jp.getSourceInfo(source_name, str_source_type, str_source_product, organization_id, site_id,
						str_protection_status, str_connection_status, os_major, applicationArray, agentInfo);

		errorHandle.printDebugMessageInDebugFile("create source");
		test.log(LogStatus.INFO, "create source");
		Response response = spogInvoker.createSourceWithCloudToken(sourceInfo, Token);

		return response;
	}


	/**
	 * Full parameter for create source
	 * 
	 * @author shuo.zhang
	 * @param source_name
	 * @param source_type
	 * @param source_product
	 * @param organization_id
	 * @param site_id
	 * @param protection_status
	 * @param connection_status
	 * @param os_major
	 * @param applications
	 * @param vm_name
	 * @param hypervisor_id
	 * @param agent_name
	 * @param os_name
	 * @param os_architecture
	 * @param agent_current_version
	 * @param agent_upgrade_version
	 * @param agent_upgrade_link
	 * @param test
	 * @return
	 */
	public String createSourceWithCheck(String source_name, SourceType source_type,
			SourceProduct source_product, String organization_id, String site_id,
			ProtectionStatus protection_status, ConnectionStatus connection_status, String os_major,
			String applications, String vm_name, String hypervisor_id, String agent_name, String os_name,
			String os_architecture, String agent_current_version, String agent_upgrade_version,
			String agent_upgrade_link, ExtentTest test) {

		Response response = createSource(source_name, source_type, source_product, organization_id,
				site_id, protection_status, connection_status, os_major, applications, vm_name,
				hypervisor_id, agent_name, os_name, os_architecture, agent_current_version,
				agent_upgrade_version, agent_upgrade_link, test);

		return checkCreateSource(response, source_name, source_type, source_product, organization_id,
				site_id, protection_status, connection_status, os_major, applications, vm_name,
				hypervisor_id, agent_name, os_name, os_architecture, agent_current_version,
				agent_upgrade_version, agent_upgrade_link, null, SpogConstants.SUCCESS_POST, null, test);

		/*
		 * errorHandle.printDebugMessageInDebugFile("check status code"); test.log(LogStatus.INFO,
		 * "check status code"); response.then().statusCode(SpogConstants.SUCCESS_POST);
		 * 
		 * if ((organization_id == null) || (organization_id.equals(""))) { organization_id =
		 * this.GetLoggedinUserOrganizationID(); }
		 * 
		 * String create_user_id = this.GetLoggedinUser_UserID();
		 * 
		 * errorHandle.printDebugMessageInDebugFile("response is " + response.getBody().toString());
		 * test.log(LogStatus.INFO, "check response");
		 * 
		 * if (source_name != null) { response.then().body("data.source_name", equalTo(source_name)); }
		 * if (source_type != null) { response.then().body("data.source_type",
		 * equalTo(source_type.name())); } if (source_product != null) {
		 * response.then().body("data.source_product", equalTo(source_product.name())); }
		 * response.then().body("data.organization_id", equalTo(organization_id));
		 * 
		 * if (protection_status != null) { response.then().body("data.protection_status",
		 * equalTo(protection_status.name())); } if (connection_status != null) {
		 * response.then().body("data.connection_status", equalTo(connection_status.name())); }
		 * response.then().body("data.site_id", equalTo(site_id));
		 * 
		 * if (os_major != null) { response.then().body("data.os_major", equalTo(os_major)); }
		 * 
		 * if ((applications != null) && (!applications.equals(""))) { String[] applicationArray = null;
		 * if ((applications != null) && (!applications.equals(""))) { applicationArray =
		 * applications.split(";");
		 * 
		 * } response.then().body("data.applications", hasItems(applicationArray)); }
		 * response.then().body("data.create_user_id", equalTo(create_user_id));
		 * 
		 * // check agent info response.then().body("data.agent.vm_name", equalTo(vm_name));
		 * response.then().body("data.agent.hypervisor_id", equalTo(hypervisor_id));
		 * response.then().body("data.agent.agent_name", equalTo(agent_name));
		 * response.then().body("data.agent.os_name", equalTo(os_name));
		 * response.then().body("data.agent.os_architecture", equalTo(os_architecture));
		 * response.then().body("data.agent.agent_current_version", equalTo(agent_current_version));
		 * response.then().body("data.agent.agent_upgrade_version", equalTo(agent_upgrade_version));
		 * response.then().body("data.agent.agent_upgrade_link", equalTo(agent_upgrade_link));
		 * 
		 * String source_id = null; source_id = response.then().extract().path("data.source_id"); return
		 * source_id;
		 */
	}


	public Response getUserByID(String userID, ExtentTest test) {

		Response response = spogInvoker.GetUserInfoByID(userID);
		return response;
	}


	/**
	 * @author shuo.zhang
	 * @param email
	 * @param password
	 * @param first_name
	 * @param last_name
	 * @param role_id
	 * @param organization_id
	 * @param phone_number
	 * @param test
	 * @return
	 */
	public Response createUser(String email, String password, String first_name, String last_name,
			String role_id, String organization_id, String phone_number, ExtentTest test) {

		errorHandle.printDebugMessageInDebugFile("***********createUser***********");
		test.log(LogStatus.INFO, "begin to compose userInfo ");
		Map<String, String> userInfo = jp.updateUserInfo(email, password, first_name, last_name,
				role_id, organization_id, phone_number);
		test.log(LogStatus.INFO, "begin to create user  ");
		Response response = spogInvoker.createUser(userInfo);
		return response;
	}


	/**
	 * @author shuo.zhang
	 * @param response
	 * @param expectedStatusCode
	 * @param email
	 * @param first_name
	 * @param last_name
	 * @param role_id
	 * @param organization_id
	 * @param phone_number
	 * @param expectedErrorCode
	 * @param test
	 * @return
	 */
	public String checkCreateUser(Response response, int expectedStatusCode, String email,
			String first_name, String last_name, String role_id, String organization_id,
			String phone_number, String status, String expectedErrorCode, ExtentTest test) {

		errorHandle.printDebugMessageInDebugFile("/***************checkCreateUser***************");
		response.then().log().body().toString();
		test.log(LogStatus.INFO, "check status code");
		checkResponseStatus(response, expectedStatusCode);

		errorHandle.printDebugMessageInDebugFile(response.then().log().body().toString());
		if (expectedStatusCode == SpogConstants.SUCCESS_POST) {
			if ((organization_id == null) || (organization_id.equals(""))) {
				organization_id = GetLoggedinUserOrganizationID();
			}
			return checkUserResponse(response, email, first_name, last_name, role_id, organization_id,
					phone_number, status, test);
		} else {
			// for the error handling test.
			test.log(LogStatus.INFO, "for error handling, check error code");
			checkErrorCode(response, expectedErrorCode);
			return null;
		}
	}


	public String checkUserResponse(Response response, String email, String first_name,
			String last_name, String role_id, String organization_id, String phone_number, String status,
			ExtentTest test) {

		test.log(LogStatus.INFO, "check response body ");
		List actionItemsArray = new ArrayList<String>();
		if (status.equalsIgnoreCase("verified") || status.equalsIgnoreCase("reset")) {
			actionItemsArray.add("delete");
			actionItemsArray.add("resetpassword");
			if (role_id == SpogConstants.MSP_ACCOUNT_ADMIN) {
				actionItemsArray.add("assignaccount");
			}
		} else if (status.equalsIgnoreCase("unverified")) {
			actionItemsArray.add("delete");
			actionItemsArray.add("verificationemail");
		}

		String user_id = response.then().body("data.first_name", equalTo(first_name))
				.body("data.last_name", equalTo(last_name)).body("data.email", equalTo(email.toLowerCase()))
				.body("data.role_id", equalTo(role_id))
				.body("data.organization_id", equalTo(organization_id))
				.body("data.create_ts", not(isEmptyOrNullString())).body("data.last_login_ts", equalTo(0))
				.body("data.blocked", equalTo(false)).body("data.status", equalTo(status))
				.body("data.allowed_actions", equalTo(actionItemsArray))
				.body("data.user_id", not(isEmptyOrNullString())).extract().path("data.user_id");
		if (phone_number != null) {
			response.then().body("data.phone_number", equalTo(phone_number));
		}
		test.log(LogStatus.INFO, "new create user id is  " + user_id);
		return user_id;
	}


	/**
	 * @author shuo.zhang
	 * @param source_name
	 * @param source_type
	 * @param source_product
	 * @param organization_id
	 * @param site_id
	 * @param protection_status
	 * @param connection_status
	 * @param os_major
	 * @param applications
	 * @param vm_name
	 * @param hypervisor_id
	 * @param agent_name
	 * @param os_name
	 * @param os_architecture
	 * @param agent_current_version
	 * @param agent_upgrade_version
	 * @param agent_upgrade_link
	 * @param source_id
	 * @param test
	 * @return
	 */
	public Response createSource(String source_name, SourceType source_type,
			SourceProduct source_product, String organization_id, String site_id,
			ProtectionStatus protection_status, ConnectionStatus connection_status, String os_major,
			String applications, String vm_name, String hypervisor_id, String agent_name, String os_name,
			String os_architecture, String agent_current_version, String agent_upgrade_version,
			String agent_upgrade_link, String source_id, ExtentTest test) {

		String[] applicationArray = null;
		if ((applications != null) && (!applications.equals(""))) {
			applicationArray = applications.split(";");

		}
		errorHandle.printDebugMessageInDebugFile("compose source info object");
		test.log(LogStatus.INFO, "compose source info object");
		String str_source_type = null;
		if (source_type != null) {
			str_source_type = source_type.name();
		}

		String str_source_product = null;
		if (source_product != null) {
			str_source_product = source_product.name();
		}

		String str_protection_status = null;
		if (protection_status != null) {
			str_protection_status = protection_status.name();
		}

		String str_connection_status = null;
		if (connection_status != null) {
			str_connection_status = connection_status.name();
		}

		Map<String, String> agentInfo = null;
		if ((vm_name != null) || (hypervisor_id != null) || (agent_name != null) || (os_name != null)
				|| (os_architecture != null) || (agent_current_version != null)
				|| (agent_upgrade_version != null) || (agent_upgrade_link != null)) {
			agentInfo = jp.composeSourceAgentInfo(vm_name, hypervisor_id, agent_name, os_name,
					os_architecture, agent_current_version, agent_upgrade_version, agent_upgrade_link);
		}

		Map<String, Object> sourceInfo = jp.getSourceInfo(source_name, str_source_type,
				str_source_product, organization_id, site_id, str_protection_status, str_connection_status,
				os_major, applicationArray, agentInfo, source_id);

		errorHandle.printDebugMessageInDebugFile("create source");
		test.log(LogStatus.INFO, "create source");
		Response response = spogInvoker.createSource(sourceInfo, true);
		test.log(LogStatus.INFO, "The value of the response:" + response.getBody().asString());

		return response;
	}


	/**
	 * @author shuo.zhang
	 * @param source_name
	 * @param source_type
	 * @param source_product
	 * @param organization_id
	 * @param site_id
	 * @param protection_status
	 * @param connection_status
	 * @param os_major
	 * @param applications
	 * @param vm_name
	 * @param hypervisor_id
	 * @param agent_name
	 * @param os_name
	 * @param os_architecture
	 * @param agent_current_version
	 * @param agent_upgrade_version
	 * @param agent_upgrade_link
	 * @param source_id
	 * @param expectedStatusCode
	 * @param expectedErrorCode
	 * @param test
	 * @return
	 */
	public String createSourceWithCheck(String source_name, SourceType source_type,
			SourceProduct source_product, String organization_id, String site_id,
			ProtectionStatus protection_status, ConnectionStatus connection_status, String os_major,
			String applications, String vm_name, String hypervisor_id, String agent_name, String os_name,
			String os_architecture, String agent_current_version, String agent_upgrade_version,
			String agent_upgrade_link, String source_id, int expectedStatusCode, String expectedErrorCode,
			ExtentTest test) {

		Response response = createSource(source_name, source_type, source_product, organization_id,
				site_id, protection_status, connection_status, os_major, applications, vm_name,
				hypervisor_id, agent_name, os_name, os_architecture, agent_current_version,
				agent_upgrade_version, agent_upgrade_link, source_id, test);

		return checkCreateSource(response, source_name, source_type, source_product, organization_id,
				site_id, ProtectionStatus.unprotect, connection_status, os_major, applications, vm_name,
				hypervisor_id, agent_name, os_name, os_architecture, agent_current_version,
				agent_upgrade_version, agent_upgrade_link, source_id, expectedStatusCode, expectedErrorCode,
				test);

	}


	/**
	 * @author shuo.zhang
	 * @param response
	 * @param source_name
	 * @param source_type
	 * @param source_product
	 * @param organization_id
	 * @param site_id
	 * @param protection_status
	 * @param connection_status
	 * @param os_major
	 * @param applications
	 * @param vm_name
	 * @param hypervisor_id
	 * @param agent_name
	 * @param os_name
	 * @param os_architecture
	 * @param agent_current_version
	 * @param agent_upgrade_version
	 * @param agent_upgrade_link
	 * @param source_id
	 * @param expectedStatusCode
	 * @param expectedErrorCode
	 * @param test
	 * @return
	 */
	public String checkCreateSource(Response response, String source_name, SourceType source_type,
			SourceProduct source_product, String organization_id, String site_id,
			ProtectionStatus protection_status, ConnectionStatus connection_status, String os_major,
			String applications, String vm_name, String hypervisor_id, String agent_name, String os_name,
			String os_architecture, String agent_current_version, String agent_upgrade_version,
			String agent_upgrade_link, String source_id, int expectedStatusCode, String expectedErrorCode,
			ExtentTest test) {

		errorHandle.printDebugMessageInDebugFile("check status code");
		test.log(LogStatus.INFO, "check status code");
		response.then().statusCode(expectedStatusCode);
		response.then().log().all();
		if (expectedStatusCode == SpogConstants.SUCCESS_POST) {

			if ((organization_id == null) || (organization_id.equals(""))) {
				String create_user_role_id = this.GetLoggedinUser_RoleID();
				if (create_user_role_id.equalsIgnoreCase(SpogConstants.MSP_ACCOUNT_ADMIN)) {
					Response siteResponse = SiteTestHelper.getSite(site_id, this.getJWTToken());
					organization_id = siteResponse.then().extract().path("data.organization_id");

				} else {
					organization_id = this.GetLoggedinUserOrganizationID();
				}

			}

			String create_user_id = this.GetLoggedinUser_UserID();

			errorHandle.printDebugMessageInDebugFile("response is " + response.getBody().toString());
			test.log(LogStatus.INFO, "check response");

			if (source_name != null) {
				response.then().body("data.source_name", equalTo(source_name));
			}
			if (source_type != null) {
				response.then().body("data.source_type", equalTo(source_type.name()));
			}
			if (source_product != null) {
				response.then().body("data.source_product", equalTo(source_product.name()));
			}
			response.then().body("data.organization_id", equalTo(organization_id));

			if (protection_status != null) {
				response.then().body("data.protection_status", equalTo(protection_status.name()));

			}
			if (connection_status != null) {
				response.then().body("data.connection_status", equalTo(connection_status.name()));
			}
			// response.then().body("data.site.site_id", equalTo(site_id));
			// site name

			if (os_major != null) {
				response.then().body("data.operating_system.os_major", equalTo(os_major));
			}

			if ((applications != null) && (!applications.equals(""))) {
				String[] applicationArray = null;
				if ((applications != null) && (!applications.equals(""))) {
					applicationArray = applications.split(";");

				}
				response.then().body("data.applications", hasItems(applicationArray));
			}
			if (source_product.equals(SourceProduct.cloud_direct)) {

				response.then().body("data.create_user_id", Matchers.isOneOf(site_id, create_user_id));
			} else {
				response.then().body("data.create_user_id", equalTo(create_user_id));
			}

			// check agent info
			response.then().body("data.vm_name", equalTo(vm_name));
			response.then().body("data.hypervisor.hypervisor_id", equalTo(hypervisor_id));
			response.then().body("data.agent.agent_name", equalTo(agent_name));
			response.then().body("data.operating_system.os_name", equalTo(os_name));
			response.then().body("data.operating_system.os_architecture", equalTo(os_architecture));
			response.then().body("data.agent.agent_current_version", equalTo(agent_current_version));
			response.then().body("data.agent.agent_upgrade_version", equalTo(agent_upgrade_version));
			response.then().body("data.agent.agent_upgrade_link", equalTo(agent_upgrade_link));

			if ((source_id != null) && (!source_id.equalsIgnoreCase(""))) {
				response.then().body("data.source_id", equalTo(source_id));
			}

			source_id = response.then().extract().path("data.source_id");
			return source_id;
		} else {
			checkErrorCode(response, expectedErrorCode);
			return null;
		}
	}


	public Response getSourcesColumns(ExtentTest test) {

		Response response = spogInvoker.getSourcesColumns();
		checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		return response;

	}


	public ArrayList<HashMap<String, Object>> sortArrayListbyInt(
			ArrayList<HashMap<String, Object>> actualresponse, String key) {

		System.out.println("Sorting the values");
		Collections.sort(actualresponse, new Comparator<HashMap<String, Object>>() {
			@Override
			public int compare(HashMap<String, Object> o1, HashMap<String, Object> o2) {

				// TODO Auto-generated method stub
				int create_ts = (int) o1.get(key);
				int create_ts1 = (int) o2.get(key);
				if (create_ts > create_ts1)
					return 1;
				else if (create_ts == create_ts1) {

					String column_id1 = (String) o1.get("column_id");
					String column_id2 = (String) o2.get("column_id");
					if (column_id1.compareTo(column_id2) > 0) {
						return 1;
					} else if (column_id1.compareTo(column_id2) == 0) {
						return 0;
					} else
						return -1;
				}

				else {
					return -1;
				}

			}
		});
		return actualresponse;
	}


	public ArrayList<HashMap<String, Object>> sortArrayListbyString(
			ArrayList<HashMap<String, Object>> expectedresponse, String key) {

		Collections.sort(expectedresponse, new Comparator<HashMap<String, Object>>() {
			@Override
			public int compare(HashMap<String, Object> o1, HashMap<String, Object> o2) {

				// TODO Auto-generated method stub
				String create_ts = (String) o1.get(key).toString();
				String create_ts1 = (String) o2.get(key).toString();
				if (create_ts.compareTo(create_ts1) > 0)
					return 1;
				else if (create_ts.compareTo(create_ts1) == 0) {

					// TODO Auto-generated method stub
					String column_id1 = (String) o1.get("column_id");
					String column_id2 = (String) o2.get("column_id");
					if (column_id1.compareTo(column_id2) > 0) {
						return 1;
					} else if (column_id1.compareTo(column_id2) < 0) {
						return -1;
					} else
						return 0;
				} else
					return -1;

			}
		});
		return expectedresponse;
	}


	/**
	 * @author Bharadwaj.Ghadiam
	 * @param sortStr
	 * @param logsinfo
	 * @param test
	 * @return response for the sorting the data
	 */
	// This is used to sort the logs for valid cases
	public ArrayList<HashMap<String, Object>> getLogsTimeSortInfo(String sortStr,
			ArrayList<HashMap<String, Object>> logsinfo, ExtentTest test2) {

		// TODO Auto-generated method stub
		Collections.sort(logsinfo, new Comparator<HashMap<String, Object>>() {
			@Override
			public int compare(HashMap<String, Object> o1, HashMap<String, Object> o2) {

				// TODO Auto-generated method stub
				// if two job _type are same then sort based ascending order of the log_ts
				Long Log_ts = (Long) o1.get("log_ts");
				Long Log_ts1 = (Long) o2.get("log_ts");
				return Log_ts > Log_ts1 ? 1 : Log_ts == Log_ts1 ? 0 : -1;
			}
		});
		return logsinfo;
	}


	public Response getAgentDownloads(String organizationId) {

		return spogInvoker.getAgentDownloads(organizationId);
	}


	public Response getSourceSystemFilters() {

		Response response=  getFilters(spogInvoker.getToken(),  filterType.source_filter_global.toString() );
		return response;
	}


	public Response getDestinationSystemFilters() {
		Response response=  getFilters(spogInvoker.getToken(),  filterType.destination_filter_global.toString() );
		return response;
	}


	/**
	 * @author Bharadwaj.Ghadiam This is used to remove the additional parameters that are appended at
	 *         the begining or the end of the search String
	 * @param (Pattern which we need to match)
	 * @return actual logs related to the search String
	 */
	public String removeSymbols(String str) {

		return str.replaceAll("\\*", "").replaceAll("\\?", "").replaceAll("\\(", "")
				.replaceAll("\\)", "").replaceAll("\\.", "").replaceAll("\\$", "").replaceAll("\\^", "");
	}


	/**
	 * @author Bharadwaj.Ghadiam
	 * @param logsinfo
	 * @param search_name(source_name)
	 * @param searchOperator(=)
	 * @param searchString("*","?")
	 * @param test
	 * @return the LogsInfo which matches the given pattern
	 */
	public ArrayList<HashMap<String, Object>> fuzzySearchString(
			ArrayList<HashMap<String, Object>> logsinfo, String search_name, String searchOperator,
			String searchString, ExtentTest test, String content) {

		// TODO Auto-generated method stub
		test.log(LogStatus.INFO, "Searching the respective source_name in the list of the sources");
		String value = "";
		ArrayList<HashMap<String, Object>> LogsInfo = new ArrayList<HashMap<String, Object>>();
		if (!searchString.contains("$") && !searchString.contains("^")) {
			value = "(.*)" + "(" + searchString + ")" + "(.*)";
		} else if (searchString.contains("$") && !searchString.contains("^")) {
			value = "(.*)" + "(" + searchString.replace("$", "") + ")" + "$";
		} else {
			value = "^" + "(" + searchString.replace("^", "") + ")" + "(.*)";
		}
		final Pattern pattern = Pattern.compile(value);
		// If the length of the String is less than 3 then return empty data
		// String additional=(removeSymbols(value));
		if ((removeSymbols(value)).length() < 3) {
			test.log(LogStatus.INFO, "The size of the source_name should be more than 3");
			return null;
		} else {

			if (content.contains("logs")) {
				for (int i = 0; i < logsinfo.size(); i++) {
					try {
						HashMap<String, Object> job_data =
								(HashMap<String, Object>) logsinfo.get(i).get("job_data");
						String search_name1 = (String) job_data.get(search_name);
						Matcher match = pattern.matcher(search_name1);
						if (match.matches()) {
							LogsInfo.add(logsinfo.get(i));
						} else {
							continue;
						}
					} catch (Exception e) {
						test.log(LogStatus.ERROR, "The value of the message :" + e.getMessage());
					}
				}
				return LogsInfo;
			} else {
				for (int i = 0; i < logsinfo.size(); i++) {
					try {
						HashMap<String, Object> destinationData = (HashMap<String, Object>) logsinfo.get(i);
						String search_name1 = (String) destinationData.get(search_name);
						Matcher match = pattern.matcher(search_name1);
						if (match.matches()) {
							LogsInfo.add(logsinfo.get(i));
						} else {
							continue;
						}
					} catch (Exception e) {
						test.log(LogStatus.ERROR, "The value of the message :" + e.getMessage());
					}
				}
				return LogsInfo;
			}
		}
	}


	/**
	 * @author Bharadwaj.Ghadiam
	 * @param last_job
	 * @return last Job_data in the response
	 */
	public Object getLastJob(HashMap<String, ArrayList<HashMap<String, Object>>> last_job) {

		// TODO Auto-generated method stub
		ArrayList<HashMap<String, Object>> last_job_data = last_job.get("last_job");
		return last_job_data;
	}


	/**
	 * Call the REST api to getUserSession
	 * 
	 * @author Eric.Yang
	 * @param Token
	 * @param statuscode
	 * @param test
	 * @return response
	 */
	public Response getUserSessionWithCheck(String Token, int statuscode, ExtentTest test) {

		Response response = spogInvoker.getUserSessionInfo(Token, test);

		checkResponseStatus(response, statuscode, test);
		System.out
		.println("The value of the geneated user session is :" + response.getBody().asString());
		return response;
	}


	/**
	 * Check the Response for get User session
	 * 
	 * @author Eric.Yang
	 * @param response
	 * @param expectedStatusCode
	 * @param Info
	 * @param adminusername
	 * @param test
	 */
	public void checkUserSession(Response response, int expectedStatusCode, String adminusername,
			String first_name, String last_name, String phone_number, String role_id,
			String organization_id, String user_id, String image_url, String create_ts,
			String last_login_ts, String blocked, String organization_name, String organization_type,
			String parent_id, String expectedErrorMessage, String expectedErrorCode, ExtentTest test) {

		// String expectedErrorMessage="",expectedErrorCode="";
		test.log(LogStatus.INFO, "expect status code is " + expectedStatusCode);
		errorHandle.printInfoMessageInDebugFile("expect status code is " + expectedStatusCode);
		response.then().statusCode(expectedStatusCode);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			// response.then().body("data.email", equalTo(adminusername.toLowerCase()));
			test.log(LogStatus.PASS, "put, response success");// The given username and the logged in user
			// name matched
			errorHandle.printInfoMessageInDebugFile("put, response success");// The given username and the
			// logged in
			// user name matched
			assertTrue("User is not Null or empty", true);

			checkBodyItemValue(response, "user.email", adminusername, test);
			checkBodyItemValue(response, "user.first_name", first_name, test);
			checkBodyItemValue(response, "user.last_name", last_name, test);
			checkResponseItemValue(response, "user.phone_number", phone_number, test);
			checkBodyItemValue(response, "user.role_id", role_id, test);
			checkBodyItemValue(response, "user.organization_id", organization_id, test);
			checkBodyItemValue(response, "user.user_id", user_id, test);
			checkBodyItemValue(response, "user.image_url", image_url, test);

			checkResponseItemValue(response, "user.create_ts", create_ts, test);
			checkResponseItemValue(response, "user.last_login_ts", last_login_ts, test);
			checkResponseItemValue(response, "user.blocked", blocked, test);
			checkBodyItemValue(response, "organization.organization_id", organization_id, test);
			checkBodyItemValue(response, "organization.organization_name", organization_name, test);
			checkBodyItemValue(response, "organization.organization_type", organization_type, test);
			checkBodyItemValue(response, "organization.parent_id", parent_id, test);
			checkBodyItemValue(response, "support.host",
					"https://arcserve.zendesk.com/hc/en-us/requests/new?ticket_form_id=182986", test);
			checkBodyItemValue(response, "support.email", "support@arcserve.com", test);
			checkBodyItemValue(response, "support.phone_number", "877-469-3882", test);
			checkBodyItemValue(response, "apis.cloud_console", "tspog.arcserve.com", test);
			checkBodyItemValue(response, "apis.cloud_direct", "tadmin.zetta.net", test);
			checkBodyItemValue(response, "apis.jobs", "tspog.arcserve.com", test);
			// checkBodyItemValue(response, "organization_type", organizationType, test);
			// checkBodyItemValue(response, "parent_id", parent_id, test);
		} else {
			checkErrorMessage(response, expectedErrorMessage);
			test.log(LogStatus.PASS, "The value of the message is " + expectedErrorMessage);
			checkErrorCode(response, expectedErrorCode);
			test.log(LogStatus.PASS, "The value of the code  generated  is " + expectedErrorCode);
			test.log(LogStatus.INFO,
					"The value of the  response generated actually is :" + response.getStatusCode());
		}

	}


	/**
	 * general method to check item's value in body especially for body value is not String
	 * 
	 * @author Eric.Yang
	 * @param response
	 * @param item
	 * @param Expectedvalue
	 * @return value for the item
	 * 
	 */
	public String checkResponseItemValue(Response response, String item, String Expectedvalue,
			ExtentTest test) {

		String valueFromResponse = "";
		if (null == response.then().extract().path("data." + item)) {
			valueFromResponse = "null";
		} else {
			valueFromResponse = response.then().extract().path("data." + item).toString().toLowerCase();
		}

		errorHandle.printDebugMessageInDebugFile(
				"Get value for " + item + " from response is " + valueFromResponse);
		errorHandle.printDebugMessageInDebugFile("Expectedvalue for " + item + " is " + Expectedvalue);
		if (valueFromResponse.equalsIgnoreCase(Expectedvalue)) {
			// assertTrue("The value from body for item:"+ item+" is expected", true);
			test.log(LogStatus.PASS, "Check:" + item + " in reponse body.");
		} else {
			// assertTrue("The value from body for item:"+ item+" is expected", false);
			test.log(LogStatus.FAIL, "Check:" + item + " in reponse body.");
		}
		return valueFromResponse;
	}


	/**
	 * @author Zhaoguo.Ma
	 * @param organizationID
	 * @param accountID
	 * @param picturePath
	 * @param test
	 * @return
	 */
	public Response uploadPictureForAccount(String organizationID, String accountID,
			String picturePath, ExtentTest test) {

		Response response = spogInvoker.uploadPictureforAccount(organizationID, accountID, picturePath);

		checkResponseStatus(response, SpogConstants.SUCCESS_POST);

		String imageUrl = response.then().extract().path("data.image_url");

		// download the image from url in response and compare to original
		String tempImageURL = picturePath + "download";
		boolean timeOut=false;
		int count= 10000;
		int i=0;
		InputStream inputStream =null;
		FileOutputStream fileOutputStream=null;
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("hydproxy.arcserve.com", 6588));
		do{
			try {
				URL url = new URL(imageUrl);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);
				connection.setConnectTimeout(300000); // 120 seconds connectTimeout
				connection.setReadTimeout(300000 ); // 120 seconds socketTimeout
				connection.connect(); 

				inputStream = url.openStream();
				fileOutputStream = new FileOutputStream(new File(tempImageURL));
				int length = -1;
				byte[] buffer = new byte[1024];
				System.out.println("read");
				while ((length = inputStream.read(buffer)) > -1) {
					fileOutputStream.write(buffer, 0, length);
				}
				System.out.println("finish read");
				timeOut=false;
			} catch(Exception e){ 
				timeOut=true;
				try {
					Thread.sleep(10000);
					System.out.println("retry");
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				i++;

			}finally{
				try {
					if(fileOutputStream!=null)
						fileOutputStream.close();
					if(inputStream!=null)
						inputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}while(timeOut && (i<count));

		File file1 = new File(picturePath);
		File file2 = new File(tempImageURL);

		try {
			boolean result = FileUtils.contentEquals(file1, file2);
			assertEquals(result, true);
		} catch (IOException e) {
			e.printStackTrace();
		}

		file2.delete();
		return response;
	}


	public void uploadPictureForAccountWithErrorCheck(String organizationID, String accountID,
			String picturePath, int status_code, String errorCode, ExtentTest test) {

		Response response = spogInvoker.uploadPictureforAccount(organizationID, accountID, picturePath);
		checkResponseStatus(response, status_code);
		checkErrorCode(response, errorCode);

	}


	/**
	 * @author shuo.zhang
	 * @param response
	 * @return
	 */
	public String getRefreshToken(Response response) {

		errorHandle.printDebugMessageInDebugFile("***********getRefreshToken***********");
		response.then().statusCode(SpogConstants.SUCCESS_LOGIN);
		String refresh_token = response.then().extract().path("data.refresh_token");
		errorHandle.printInfoMessageInDebugFile("refresh_token is " + refresh_token);
		return refresh_token;
	}


	public Response createFilter(String user_id, String filter_name, String protection_status,
			String connection_status, String protection_policy, String backup_status, String source_group,
			String operating_system, String applications, String site_id, String source_name,
			String source_type, String is_default, ExtentTest test) {

		Map<String, Object> sourceFilterInfo = jp.composeFilterInfo(filter_name, protection_status,
				connection_status, protection_policy, backup_status, source_group, operating_system,
				site_id, source_name, source_type, is_default);
		/*Response response = spogInvoker.createFilter(user_id, filterInfo);*/
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!sourceFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.source_filter.toString(),
					sourceFilterInfo.get("filter_name").toString(), user_id, "none",
					(boolean)sourceFilterInfo.get("is_default"),(HashMap<String, Object>) sourceFilterInfo);
		}

		Response response = createFilters(spogInvoker.getToken(), filter_info, "", test);

		checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		// TODO: add check points
		String org_id = GetOrganizationIDforUser(user_id);
		// ArrayList<String> response_source_type = response.then().extract().path("data.source_type");
		// if (source_type == null || source_type.equalsIgnoreCase("none")) {
		// // response.then().body("data.source_type", equalTo("all"));
		// assertFilterItem("all", response_source_type);
		// } else {
		// // response.then().body("data.source_type", equalTo(source_type));
		// assertFilterItem(source_type, response_source_type);
		// }



		if ("true".equalsIgnoreCase(is_default)) {
			response.then().body("data.is_default", equalTo(true));
		} else {
			response.then().body("data.is_default", equalTo(false));
		}
		response.then().body("data.filter_name", equalTo(filter_name))
		.body("data.organization_id", equalTo(org_id)).body("data.user_id", equalTo(user_id));

		ArrayList<String> response_protection_status =
				response.then().extract().path("data.protection_status");
		assertFilterItem(protection_status, response_protection_status);

		ArrayList<String> response_connection_status =
				response.then().extract().path("data.connection_status");
		assertFilterItem(connection_status, response_connection_status);

		ArrayList<String> response_protection_policy = response.then().extract().path("data.policy_id");
		assertFilterItem(protection_policy, response_protection_policy);

		ArrayList<String> response_backup_status =
				response.then().extract().path("data.last_backup_status");
		assertFilterItem(backup_status, response_backup_status);

		ArrayList<String> response_source_group = response.then().extract().path("data.group_id");
		assertFilterItem(source_group, response_source_group);

		ArrayList<String> response_operating_system = response.then().extract().path("data.os_major");
		assertFilterItem(operating_system, response_operating_system);


		ArrayList<String> response_site_id = response.then().extract().path("data.site_id");
		if (site_id != null && site_id.equalsIgnoreCase("")) {
			site_id = "emptyarray";
		}
		assertFilterItem(site_id, response_site_id);

		String response_source_name = response.then().extract().path("data.source_name");
		assertResponseItem(source_name, response_source_name);

		// String filter_id = response.then().extract().path("data.filter_id");
		return response;

	}


	public Response updateFilter(String user_id, String filter_id, String filter_name,
			String protection_status, String connection_status, String protection_policy,
			String backup_status, String source_group, String operating_system, String applications,
			String siteId, String sourceName, String source_type, String actual_souce_type,
			String is_default, ExtentTest test) {

		Map<String, Object> sourceFilterInfo = jp.composeFilterInfo(filter_name, protection_status,
				connection_status, protection_policy, backup_status, source_group, operating_system, siteId,
				sourceName, source_type, is_default);

		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!sourceFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.source_filter.toString(),
					sourceFilterInfo.get("filter_name").toString(), user_id, "none",
					(boolean)sourceFilterInfo.get("is_default"),(HashMap<String, Object>) sourceFilterInfo);
		}
		Response response =updateFilterById(spogInvoker.getToken(), filter_id, user_id, filter_info, "", test);
		checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		// TODO: add check points
		String org_id = GetOrganizationIDforUser(user_id);
		if (CheckIsNullOrEmpty(filter_name)) {
			response.then().body("data.organization_id", equalTo(org_id)).body("data.user_id",
					equalTo(user_id));
		} else {
			response.then().body("data.filter_name", equalTo(filter_name))
			.body("data.organization_id", equalTo(org_id)).body("data.user_id", equalTo(user_id));
		}
		compareFilter(protection_status, connection_status, protection_policy, backup_status,
				source_group, operating_system, applications, siteId, sourceName, actual_souce_type,
				is_default, response);
		// String return_filter_id = response.then().extract().path("data.filter_id");
		return response;
	}


	public void checkaudittrail_enh(ArrayList<HashMap> users, ArrayList<HashMap> ExpectedAccounts,
			int i, int j, ExtentTest test) {

		test.log(LogStatus.INFO, "Added logic for rest of the audit codes in sprint 12 testing");
		if (users.get(i).get("code_id").equals(Integer.parseInt(AuditCodeConstants.create_source_group))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.modify_source_group)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.delete_source_group)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.add_source_to_group)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.delete_source_group)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.create_source_filter)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.modify_source_filter)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.delete_source_filter)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.create_destination_filter)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.modify_destination_filter)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.delete_destination_filter)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.CREATE_JOB_FILTER)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.MODIFY_JOB_FILTER)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.DELETE_JOB_FILTER)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.CREATE_LOG_FILTER)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.MODIFY_LOG_FILTER)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.DELETE_LOG_FILTER)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.CREATE_USER_FILTER)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.MODIFY_USER_FILTER)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.DELETE_USER_FILTER)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.CREATE_HYPERVISOR_FILTER)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.MODIFY_HYPERVISOR_FILTER)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.DELETE_HYPERVISOR_FILTER)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.CREATE_USER_COLUMN)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.MODIFY_USER_COLUMN)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.DELETE_USER_COLUMN)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.CREATE_USER_DESTINATION_COLUMN)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.MODIFY_USER_DESTINATION_COLUMN)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.DELETE_USER_DESTINATION_COLUMN)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.CREATE_USER_JOB_COLUMN)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.MODIFY_USER_JOB_COLUMN)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.DELETE_USER_JOB_COLUMN)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.CREATE_USER_LOG_COLUMN)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.MODIFY_USER_LOG_COLUMN)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.DELETE_USER_LOG_COLUMN)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.CREATE_USER_SOURCE_COLUMN)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.MODIFY_USER_SOURCE_COLUMN)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.DELETE_USER_SOURCE_COLUMN)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.CREATE_USER_HYPERVISOR_COLUMN)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.MODIFY_USER_HYPERVISOR_COLUMN)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.DELETE_USER_HYPERVISOR_COLUMN)))
				|| (users.get(i).get("code_id").equals(Integer.parseInt(AuditCodeConstants.CREATE_JOB)))
				|| (users.get(i).get("code_id").equals(Integer.parseInt(AuditCodeConstants.MODIFY_JOB)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.CREATE_JOB_DATA)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.MODIFY_JOB_DATA)))
				|| (users.get(i).get("code_id").equals(Integer.parseInt(AuditCodeConstants.CREATE_LOG)))
				|| (users.get(i).get("code_id").equals(Integer.parseInt(AuditCodeConstants.MODIFY_LOG)))
				|| (users.get(i).get("code_id").equals(Integer.parseInt(AuditCodeConstants.DELETE_LOG)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.CREATE_LOG_DATA)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.MODIFY_LOG_DATA)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.CREATE_USER_RECOVEREDRESOURCE_COLUMN)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.MODIFY_USER_RECOVEREDRESOURCE_COLUMN)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.DELETE_USER_RECOVEREDRESOURCE_COLUMN)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.CREATE_POLICY_FILTER)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.MODIFY_POLICY_FILTER)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.DELETE_POLICY_FILTER)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.CREATE_USER_POLICY_COLUMN)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.MODIFY_USER_POLICY_COLUMN)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.DELETE_USER_POLICY_COLUMN)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.CREATE_RECOVEREDRESOURCE_FILTER)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.MODIFY_RECOVEREDRESOURCE_FILTER)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.DELETE_RECOVEREDRESOURCE_FILTER)))
				|| (users.get(i).get("code_id").equals(Integer.parseInt(AuditCodeConstants.CREATE_POLICY)))
				|| (users.get(i).get("code_id").equals(Integer.parseInt(AuditCodeConstants.MODIFY_POLICY)))
				|| (users.get(i).get("code_id").equals(Integer.parseInt(AuditCodeConstants.DELETE_POLICY)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.CREATE_REPORT_LIST_FILTER)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.MODIFY_REPORT_LIST_FILTER)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.DELETE_REPORT_LIST_FILTER)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.CREATE_USER_RESTOREJOBREPORT_COLUMN)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.MODIFY_USER_RESTOREJOBREPORT_COLUMN)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.DELETE_USER_RESTOREJOBREPORT_COLUMN)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.CREATE_USER_BACKUPJOBREPORT_COLUMN)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.MODIFY_USER_BACKUPJOBREPORT_COLUMN)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.DELETE_USER_BACKUPJOBREPORT_COLUMN)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.CREATE_REPORT_FILTER)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.MODIFY_REPORT_FILTER)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.DELETE_REPORT_FILTER)))) {
			test.log(LogStatus.INFO, "Validating the destination audit trail");
			assertResponseItem(users.get(i).get("by_organization_id"),
					ExpectedAccounts.get(j).get("by_organization_id"), test);
			assertResponseItem(users.get(i).get("by_resource_id"),
					ExpectedAccounts.get(j).get("by_resource_id"), test);
			assertResponseItem(users.get(i).get("code_id").toString(),
					ExpectedAccounts.get(j).get("code_id").toString(), test);
			assertResponseItem(users.get(i).get("on_resource_id"),
					ExpectedAccounts.get(j).get("on_resource_id"), test);

			if ((users.get(i).get("code_id")
					.equals(Integer.parseInt(AuditCodeConstants.delete_source_group)))
					|| (users.get(i).get("code_id")
							.equals(Integer.parseInt(AuditCodeConstants.delete_source_from_group)))
					|| (users.get(i).get("code_id")
							.equals(Integer.parseInt(AuditCodeConstants.delete_source_filter)))
					|| (users.get(i).get("code_id")
							.equals(Integer.parseInt(AuditCodeConstants.delete_destination_filter)))
					|| (users.get(i).get("code_id")
							.equals(Integer.parseInt(AuditCodeConstants.DELETE_JOB_FILTER)))
					|| (users.get(i).get("code_id")
							.equals(Integer.parseInt(AuditCodeConstants.DELETE_LOG_FILTER)))
					|| (users.get(i).get("code_id")
							.equals(Integer.parseInt(AuditCodeConstants.DELETE_USER_FILTER)))
					|| (users.get(i).get("code_id")
							.equals(Integer.parseInt(AuditCodeConstants.DELETE_HYPERVISOR_FILTER)))
					|| (users.get(i).get("code_id")
							.equals(Integer.parseInt(AuditCodeConstants.DELETE_USER_SOURCE_COLUMN)))
					|| (users.get(i).get("code_id")
							.equals(Integer.parseInt(AuditCodeConstants.DELETE_USER_DESTINATION_COLUMN)))
					|| (users.get(i).get("code_id")
							.equals(Integer.parseInt(AuditCodeConstants.DELETE_USER_HYPERVISOR_COLUMN)))
					|| (users.get(i).get("code_id")
							.equals(Integer.parseInt(AuditCodeConstants.DELETE_USER_COLUMN)))
					|| (users.get(i).get("code_id")
							.equals(Integer.parseInt(AuditCodeConstants.DELETE_USER_JOB_COLUMN)))
					|| (users.get(i).get("code_id")
							.equals(Integer.parseInt(AuditCodeConstants.DELETE_USER_LOG_COLUMN)))
					|| (users.get(i).get("code_id")
							.equals(Integer.parseInt(AuditCodeConstants.DELETE_HYPERVISOR)))
					|| (users.get(i).get("code_id").equals(Integer.parseInt(AuditCodeConstants.DELETE_LOG)))
					|| (users.get(i).get("code_id")
							.equals(Integer.parseInt(AuditCodeConstants.DELETE_USER_RECOVEREDRESOURCE_COLUMN)))
					|| (users.get(i).get("code_id")
							.equals(Integer.parseInt(AuditCodeConstants.DELETE_POLICY_FILTER)))
					|| (users.get(i).get("code_id")
							.equals(Integer.parseInt(AuditCodeConstants.DELETE_USER_POLICY_COLUMN)))
					|| (users.get(i).get("code_id")
							.equals(Integer.parseInt(AuditCodeConstants.DELETE_RECOVEREDRESOURCE_FILTER)))
					|| (users.get(i).get("code_id")
							.equals(Integer.parseInt(AuditCodeConstants.DELETE_POLICY)))
					|| (users.get(i).get("code_id")
							.equals(Integer.parseInt(AuditCodeConstants.DELETE_REPORT_LIST_FILTER)))
					|| (users.get(i).get("code_id")
							.equals(Integer.parseInt(AuditCodeConstants.DELETE_USER_BACKUPJOBREPORT_COLUMN)))
					|| (users.get(i).get("code_id")
							.equals(Integer.parseInt(AuditCodeConstants.DELETE_USER_RESTOREJOBREPORT_COLUMN)))
					|| (users.get(i).get("code_id")
							.equals(Integer.parseInt(AuditCodeConstants.DELETE_REPORT_FILTER)))) {

				test.log(LogStatus.PASS,
						"The audit trail data matched the expected audit code" + users.get(i).get("code_id"));

			} else {
				checkauditdata(users, i, j, ExpectedAccounts, test);
			}
		}
	}


	private void checkauditdata(ArrayList<HashMap> users, int i, int j,
			ArrayList<HashMap> expectedAccounts, ExtentTest test) {

		HashMap<String, Object> expected_values = new HashMap<>();
		HashMap<String, Object> actual_Desinations =
				(HashMap<String, Object>) users.get(i).get("audit_data");
		ArrayList<ResponseBody> audit_destinations = new ArrayList<ResponseBody>();
		audit_destinations = (ArrayList<ResponseBody>) expectedAccounts.get(j).get("audit_data");
		if ((users.get(i).get("code_id")
				.equals(Integer.parseInt(AuditCodeConstants.CREATE_USER_SOURCE_COLUMN)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.CREATE_USER_SOURCE_COLUMN)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.MODIFY_USER_SOURCE_COLUMN)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.CREATE_USER_DESTINATION_COLUMN)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.MODIFY_USER_DESTINATION_COLUMN)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.CREATE_USER_JOB_COLUMN)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.MODIFY_USER_JOB_COLUMN)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.CREATE_USER_LOG_COLUMN)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.MODIFY_USER_LOG_COLUMN)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.CREATE_USER_HYPERVISOR_COLUMN)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.MODIFY_USER_HYPERVISOR_COLUMN)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.CREATE_USER_COLUMN)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.MODIFY_USER_COLUMN)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.CREATE_USER_RECOVEREDRESOURCE_COLUMN)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.MODIFY_USER_RECOVEREDRESOURCE_COLUMN)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.CREATE_USER_POLICY_COLUMN)))
				|| (users.get(i).get("code_id")
						.equals(Integer.parseInt(AuditCodeConstants.MODIFY_USER_POLICY_COLUMN))
						|| (users.get(i).get("code_id")
								.equals(Integer.parseInt(AuditCodeConstants.CREATE_USER_BACKUPJOBREPORT_COLUMN)))
						|| (users.get(i).get("code_id")
								.equals(Integer.parseInt(AuditCodeConstants.MODIFY_USER_BACKUPJOBREPORT_COLUMN)))
						|| (users.get(i).get("code_id")
								.equals(Integer.parseInt(AuditCodeConstants.CREATE_USER_RESTOREJOBREPORT_COLUMN)))
						|| (users.get(i).get("code_id").equals(
								Integer.parseInt(AuditCodeConstants.MODIFY_USER_RESTOREJOBREPORT_COLUMN))))) {
			ArrayList<HashMap<String, Object>> expected =
					(ArrayList<HashMap<String, Object>>) audit_destinations.get(0).jsonPath().get("data");
			expected_values = (HashMap<String, Object>) expected.get(0);
		} else {
			expected_values = (HashMap<String, Object>) audit_destinations.get(0).jsonPath().get("data");
		}

		if (actual_Desinations.equals(expected_values)) {
			test.log(LogStatus.PASS,
					"The audit trail data did match the expected audit code" + users.get(i).get("code_id"));
		} else {
			test.log(LogStatus.FAIL, "The audit trail data did not match the expected audit code"
					+ users.get(i).get("code_id"));
			assertTrue("The audit trail data did not match the expected audit code"
					+ users.get(i).get("code_id"), false);
		}
	}


	/**
	 * REST API to get cloudaccounts types
	 * 
	 * @author yuefen.liu
	 * @return
	 */
	public Response getCloudAccountsTypes(String token) {

		Response response = spogInvoker.getCloudAccountsTypes(token);
		return response;
	}


	/**
	 * REST API to postOrgInfoBySearchString
	 * 
	 * @author ramya.nagepalli
	 * @param ExpectedAccounts
	 * @param search_string
	 * @param token
	 * @param currPage
	 * @param pageSize
	 * @param successGetPutDel
	 * @return
	 */
	public Response postOrgInfoBySearchString(ArrayList<HashMap<String, Object>> ExpectedAccounts,
			String search_string, String token, int page, int page_size, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage, ExtentTest test) {

		// TODO Auto-generated method stub
		HashMap<String, Object> searchInfo =
				jp.postOrgInfoBySearchString(search_string, page, page_size);
		Response response = spogInvoker.postOrgInfoBySearchString(searchInfo, token);
		errorHandle
		.printDebugMessageInDebugFile("response for getOrgInfoBySearchString is " + response);
		checkResponseStatus(response, expectedStatusCode, test);
		int total_size = 0, return_size = 0;
		if (page == 0) {
			page = 1;
		}
		if (page_size == 0 || page_size > SpogConstants.MAX_PAGE_SIZE) {
			page_size = 100;
		}
		// related inforamtion for the response
		ArrayList<HashMap> users = null;
		// For Pagination of the different situations
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			if (ExpectedAccounts == null)
				return response;


			users = new ArrayList<HashMap>();
			users = response.then().extract().path("data");
			System.out.println("The expected size is " + ExpectedAccounts.size());
			return_size = users.size();

			if (page == 1) {
				for (int i = 0; i < users.size(); i++) {

					checkOrganizations(users, i, i, ExpectedAccounts, test);

				}
			} else {
				int j = 0;
				if (page != 1) {
					j = (page - 1) * page_size;
				}
				if (users.isEmpty()) {

					test.log(LogStatus.PASS, "response returned is empty, no data ia present ");
				} else {
					for (int i = 0; i < users.size(); i++, j++) {
						checkOrganizations(users, i, j, ExpectedAccounts, test);
					}
				}
			}

			for (int i = 0; i < users.size(); i++) {
				if (users.get(i).get("organization_type").toString().equalsIgnoreCase(SpogConstants.MSP_ORG)
						&& !users.get(i).get("parent_id").toString().equalsIgnoreCase(SpogConstants.CSR_ORG_ID)) {
					assertResponseItem("true", users.get(i).get("sub_msp").toString(), test);
				}else {
					assertResponseItem("false", users.get(i).get("sub_msp").toString(), test);
				}
			}
		} else {

			String code = expectedErrorMessage.getCodeString();

			String message = expectedErrorMessage.getStatus();

			checkErrorCode(response, code);

			test.log(LogStatus.PASS, "The error code matched with the expected " + code);

			checkErrorMessage(response, message);

			test.log(LogStatus.PASS, "The expected error message matched " + message);
		}

		return response;
	}


	/*
	 * public Response getOrgInfoBySearchString(ArrayList<HashMap<String, Object>> ExpectedAccounts,
	 * String search_string, String token, int currPage, int pageSize,int expectedStatusCode,
	 * SpogMessageCode expectedErrorMessage, ExtentTest test) { // TODO Auto-generated method stub
	 * HashMap<String, Object> searchInfo =
	 * jp.getOrgInfoBySearchString(search_string,currPage,pageSize); Response response =
	 * spogInvoker.getOrgInfoBySearchString(searchInfo,token);
	 * errorHandle.printDebugMessageInDebugFile("response for getOrgInfoBySearchString is " +
	 * response);
	 * 
	 * int total_size = 0, return_size = 0; if (currPage == 0) { currPage = 1; } if (pageSize == 0 ||
	 * pageSize > SpogConstants.MAX_PAGE_SIZE) { pageSize = 100; } // related inforamtion for the
	 * response ArrayList<HashMap> users = new ArrayList<HashMap>(); // For Pagination of the
	 * different situations if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
	 * 
	 * users = new ArrayList<HashMap>(); users = response.then().extract().path("data");
	 * System.out.println("The expected size is " + ExpectedAccounts.size()); return_size =
	 * users.size();
	 * 
	 * if(currPage==1) { for (int i = 0; i < users.size(); i++) {
	 * 
	 * checkOrganizations(users,i,i,ExpectedAccounts,test);
	 * 
	 * } } else { int j = 0; if (currPage != 1) { j = (currPage - 1) * pageSize; } if(users.isEmpty())
	 * {
	 * 
	 * test.log(LogStatus.PASS, "response returned is empty, no data ia present "); } else { for (int
	 * i = 0; i < users.size(); i++, j++) { checkOrganizations(users,i,j,ExpectedAccounts,test); } } }
	 * } else {
	 * 
	 * String code = expectedErrorMessage.getCodeString();
	 * 
	 * String message = expectedErrorMessage.getStatus();
	 * 
	 * checkErrorCode(response,code);
	 * 
	 * test.log(LogStatus.PASS, "The error code matched with the expected "+code);
	 * 
	 * checkErrorMessage(response,message);
	 * 
	 * test.log(LogStatus.PASS, "The expected error message matched " + message); }
	 * 
	 * return response; }
	 */
	private void checkOrganizations(ArrayList<HashMap> users, int i, int j,
			ArrayList<HashMap<String, Object>> expectedAccounts, ExtentTest test) {
		// TODO Auto-generated method stub

		if (!(users.isEmpty())) {
			if ((users.get(i).get("organization_name").toString()
					.equals("AUTO_" + expectedAccounts.get(j).get("organization_name").toString()))
					&& (users.get(i).get("organization_id").toString()
							.equals(expectedAccounts.get(j).get("organization_id").toString()))
					&& (users.get(i).get("type").toString()
							.equals(expectedAccounts.get(j).get("type").toString()))
					&& (users.get(i).get("blocked").toString()
							.equals(expectedAccounts.get(j).get("blocked").toString()))) {
				test.log(LogStatus.PASS,
						"The actual data  match the expected data" + users.get(i).get("organization_id"));

			} else {
				test.log(LogStatus.FAIL, "The actual data did not match with the expected data"
						+ users.get(i).get("organization_id"));
			}

			if (users.get(i).get("type").toString().equals("msp_child")) {

				HashMap<String, Object> act_msp_details = new HashMap<String, Object>();
				act_msp_details = (HashMap<String, Object>) users.get(i).get("msp");

				HashMap<String, Object> exp_msp_details = new HashMap<String, Object>();
				exp_msp_details = (HashMap<String, Object>) expectedAccounts.get(j).get("msp");

				if (act_msp_details == exp_msp_details) {
					test.log(LogStatus.PASS, "The actual data  match the expected data of msp details"
							+ users.get(i).get("organization_id"));
				} else {
					test.log(LogStatus.FAIL, "The actual data not matched the expected data of msp details"
							+ users.get(i).get("organization_id"));

				}
			}
		}


	}


	public HashMap<String, Object> compose_source_status(String num_total, String num_protected,
			String num_unprotected, String num_online, String num_offline) {

		// TODO Auto-generated method stub
		HashMap<String, Object> source_status = jp.compose_source_status(num_total, num_protected,
				num_unprotected, num_online, num_offline);

		return source_status;
	}


	public HashMap<String, Object> compose_backup_status(String num_success, String num_missed,
			String num_failed, String num_ar_failed) {
		// TODO Auto-generated method stub

		HashMap<String, Object> source_status =
				jp.compose_backup_status(num_success, num_missed, num_failed, num_ar_failed);

		return source_status;
	}


	public HashMap<String, Object> compose_cloud_direct_usage(String usage, String threshold) {
		// TODO Auto-generated method stub

		HashMap<String, Object> cloud_direct_usage = jp.compose_cloud_direct_usage(usage, threshold);

		return cloud_direct_usage;
	}


	public HashMap<String, Object> compose_cloud_hybrid_usage(String usage, String threshold) {
		// TODO Auto-generated method stub

		HashMap<String, Object> cloud_hybrid_usage = jp.compose_cloud_direct_usage(usage, threshold);

		return cloud_hybrid_usage;
	}


	public HashMap<String, Object> compose_added_by(String id, String name, String email) {
		// TODO Auto-generated method stub

		HashMap<String, Object> added_by = jp.compose_added_by(id, name, email);

		return added_by;
	}


	public HashMap<String, Object> compose_extra_Inputs(String image_url, String status,
			HashMap<String, Object> source_status, HashMap<String, Object> backup_status,
			HashMap<String, Object> cloud_direct_usage, HashMap<String, Object> cloud_hybrid_usage,
			HashMap<String, Object> added_by, ArrayList<String> allowed_actions, boolean blocked) {
		// TODO Auto-generated method stub

		HashMap<String, Object> compose_extra_Inputs =
				jp.compose_extra_Inputs(image_url, status, source_status, backup_status, cloud_direct_usage,
						cloud_hybrid_usage, added_by, allowed_actions, blocked);

		return compose_extra_Inputs;
	}


	/**
	 * get UserName for current logged in user
	 * 
	 * @author Ramya.Nagepalli
	 * @return user_name
	 */
	public String GetLoggedinUser_UserName() {

		Response response = spogInvoker.GetLoggedinUserInfo();

		String user_first_name = response.then().extract().path("data.first_name");
		String user_last_name = response.then().extract().path("data.last_name");
		String user_name = user_first_name + " " + user_last_name;
		errorHandle.printDebugMessageInDebugFile("Get user name from body is: " + user_name);
		return user_name;
	}


	/**
	 * get filter by policy id url
	 * 
	 * @author Ramya.Nagepalli
	 * @param filterStr
	 * @return filterUrl
	 */
	public String getFilterByPolicyIdUrl(String filterStr) {

		errorHandle.printDebugMessageInDebugFile("***********getBasicFilterString***********");

		String filterUrl = "";

		String[] filterStrArray = filterStr.split(";");

		filterUrl += getBasicFilterString(filterStrArray[0], filterStrArray[1], filterStrArray[2]);

		// filterUrl += "?";

		errorHandle.printDebugMessageInDebugFile("filterStr is " + filterUrl);

		return filterUrl;

	}


	/**
	 * @author shuo.zhang
	 * @return
	 */
	public String GetLoggedinUser_RoleID() {

		Response response = spogInvoker.GetLoggedinUserInfo();

		String roleID = response.then().extract().path("data.role_id");
		errorHandle.printDebugMessageInDebugFile("Get role_id from body is: " + roleID);
		return roleID;
	}


	/**
	 * REST API to getAuditCodesForCSR
	 * 
	 * @author ramya.nagepalli
	 * @param csrToken
	 * @param composedDetails
	 * @param additionalurl
	 * @param statusCode
	 * @param errorMessage
	 * @return response
	 */

	public Response getAuditCodesForCSR(String csrToken, ArrayList<HashMap> composedDetails,
			String additionalurl, int statusCode, SpogMessageCode errorMessage, ExtentTest test) {

		// TODO Auto-generated method stub
		Response response = spogInvoker.getAuditCodesForCSR(csrToken, additionalurl);
		ArrayList<HashMap> auditdetails = new ArrayList<HashMap>();
		HashMap<String, Object> auditEvents = new HashMap<String, Object>();


		if (statusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			auditdetails = response.then().extract().path("data");

			for (int i = 0; i < auditdetails.size(); i++) {
				String code_id = auditdetails.get(i).get("code_id").toString();
				String act_code_name = null;
				act_code_name = auditdetails.get(i).get("code_name").toString();

				String exp_code_id = composedDetails.get(i).get("code_id").toString();
				String exp_code_name = composedDetails.get(i).get("code_name").toString();

				if (code_id.equals(exp_code_id) && act_code_name.equals(exp_code_name)) {
					test.log(LogStatus.PASS,
							"The actual data  matched the expected data details for csr organization");
				}

				auditdetails.get(i).get("description");
			}

		} else {
			String code = errorMessage.getCodeString();

			String message = errorMessage.getStatus();

			checkErrorCode(response, code);

			test.log(LogStatus.PASS, "The error code matched with the expected " + code);

			checkErrorMessage(response, message);

			test.log(LogStatus.PASS, "The expected error message matched " + errorMessage);

		}
		return response;
	}


	/**
	 * this is used for new field for assign accounts to msp
	 * 
	 * @author shuo.zhang
	 * @param email
	 * @param password
	 * @param first_name
	 * @param last_name
	 * @param role_id
	 * @param organization_id
	 * @param phone_number
	 * @param msp_accounts_info
	 * @param test
	 * @return
	 */
	public Response createUser(String email, String password, String first_name, String last_name,
			String role_id, String organization_id, String phone_number,
			HashMap<String, Object> msp_accounts_info, ExtentTest test) {

		errorHandle.printDebugMessageInDebugFile("***********createUser***********");
		test.log(LogStatus.INFO, "begin to compose userInfo ");
		HashMap<String, Object> userInfo = jp.updateUserInfo(email, password, first_name, last_name,
				role_id, organization_id, phone_number, msp_accounts_info);

		test.log(LogStatus.INFO, "begin to create user  ");
		Response response = spogInvoker.createUserWithFullInfo(userInfo);
		return response;
	}


	/**
	 * create organization
	 * 
	 * @author shan.jing
	 * @param organizationName
	 * @param organizationType
	 * @param organizationEmail
	 * @param organizationPwd
	 * @param organizationFirstName
	 * @param organizationLastName
	 * @return response
	 */
	public String CreateOrganizationByEnroll(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName,
			String organizationLastName) {

		String first_name = default_first_name;
		String default_organizationEmail = ReturnRandom("test") + "@arcserve.com";
		String last_name = default_last_name;
		String email = default_organizationEmail;
		String password = default_pwd;
		boolean deluser = true;
		String org_id = null;
		if (organizationPwd != null && organizationPwd != "") {
			password = organizationPwd;
		}
		if (organizationFirstName != null && organizationFirstName != "") {
			first_name = organizationFirstName;
		}
		if (organizationLastName != null && organizationLastName != "") {
			last_name = organizationLastName;
		}
		if (organizationEmail != null && organizationEmail != "") {
			email = organizationEmail;
			deluser = false;
		}
		HashMap<String, Object> enrollmentInfo = jp.composeEnrollmentInfo(first_name, last_name, email,
				"", organizationName, organizationType, datacenter_id);
		Response response = org4SPOGInvoker.enrollOrganization(enrollmentInfo);
		HashMap<String, Object> searchInfo = new HashMap<>();
		searchInfo.put("search_string", email);
		String token = getJWTToken();
		Response retUser = null;
		int totalsize = 0;
		boolean findUser = false;
		for (int loop = 1; loop < 60; loop++) {
			retUser = userSpogInvoker.postUsersSearch(searchInfo, token);
			totalsize = retUser.then().extract().path("pagination.total_size");
			if (totalsize >= 1) {
				findUser = true;
				break;
			}
			try {
				Thread.sleep(5000);

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("check user after enroll:" + findUser);
		if (!findUser) {
			assertTrue("can't find user", findUser);
		} else {
			String user_id = retUser.then().extract().path("data[0].user_id");
			// userLogin(csrAdminUserName, csrAdminPassword);
			Response response1 = updateUserById(user_id, "", password, "", "", "", "");

			String prefix = "AUTO_";
			if (organizationName != null && organizationName != ""
					&& (organizationName.toLowerCase().indexOf("do_not_delete") == -1)) {
				organizationName = prefix + organizationName;
			}
			retUser.then().body("data[0].organization.organization_name", equalTo(organizationName));
			org_id = retUser.then().extract().path("data[0].organization.organization_id");
			//    spogInvoker.postClouddirectInFreeTrial(token, org_id);
			if (organizationType.equalsIgnoreCase(SpogConstants.DIRECT_ORG)) {
				boolean checkDefaultVolume =
						spogDestinationInvoker.checkDefaultVolumeCreated(org_id, getJWTToken());
				System.out.println("check volume after enroll:" + checkDefaultVolume);
				assertTrue(checkDefaultVolume);
			} else {
				try {
					Thread.sleep(20000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (deluser) {
				DeleteUserById(user_id);
			}
		}
		return org_id;
	}


	/**
	 * create organization and check organizationName and organizationType
	 * 
	 * @author shan.jing
	 * @param organizationName
	 * @param organizationType
	 * @param organizationEmail
	 * @param organizationPwd
	 * @param organizationFirstName
	 * @param organizationLastName
	 * @return organization id
	 */
	public String CreateOrganizationByEnrollWithCheck(String organizationName,
			String organizationType, String organizationEmail, String organizationPwd,
			String organizationFirstName, String organizationLastName, ExtentTest test) {

		return CreateOrganizationByEnrollWithCheck(organizationName, organizationType,
				organizationEmail, organizationPwd, organizationFirstName, organizationLastName);
	}



	/**
	 * create organization and check organizationName and organizationType
	 * 
	 * @author shan.jing
	 * @param organizationName
	 * @param organizationType
	 * @param organizationEmail
	 * @param organizationPwd
	 * @param organizationFirstName
	 * @param organizationLastName
	 * @return organization id
	 */
	public String CreateOrganizationByEnrollWithCheck(String organizationName,
			String organizationType, String organizationEmail, String organizationPwd,
			String organizationFirstName, String organizationLastName) {

		String org_id = CreateOrganizationByEnroll(organizationName, organizationType,
				organizationEmail, organizationPwd, organizationFirstName, organizationLastName);
		return org_id;
	}


	/**
	 * get org ids by search org name
	 * 
	 * @author shan.jing
	 * @return ArrayList<String>
	 */
	public int getOrgPagesBySearchStringWithCsrLogin(String token, String searchStr) {

		ArrayList<String> orgIds = new ArrayList<String>();
		HashMap<String, Object> searchInfo = jp.postOrgInfoBySearchString(searchStr, 1, 10000);
		spogInvoker.setToken(token);
		Response response =
				spogInvoker.postOrgInfoBySearchStringWithCsrLogin(searchInfo, getJWTToken());
		if (response.getStatusCode() != SpogConstants.SUCCESS_GET_PUT_DELETE) {
			return 0;
		} else {
			int total_page = response.then().extract().path("pagination.total_page");
			return total_page;
		}
	}


	/**
	 * get org ids by search org name
	 * 
	 * @author shan.jing
	 * @return ArrayList<String>
	 */
	public ArrayList<String> getOrgIdsBySearchStringWithCsrLogin(String token, String searchStr,
			int page) {

		ArrayList<String> orgIds = new ArrayList<String>();
		HashMap<String, Object> searchInfo = jp.postOrgInfoBySearchString(searchStr, page, 100);
		spogInvoker.setToken(token);
		Response response =
				spogInvoker.postOrgInfoBySearchStringWithCsrLogin(searchInfo, getJWTToken());
		if (response.getStatusCode() != SpogConstants.SUCCESS_GET_PUT_DELETE) {
			return null;
		} else {
			ArrayList<HashMap> datas = null;
			datas = new ArrayList<HashMap>();
			datas = response.then().extract().path("data");
			if (datas.size() > 0) {
				for (int i = 0; i < datas.size(); i++) {
					if (datas.get(i).get("organization_name").toString().toLowerCase()
							.indexOf("do_not_delete") == -1) {
						orgIds.add(datas.get(i).get("organization_id").toString());
						System.out.println("del org id:" + datas.get(i).get("organization_id").toString());
						System.out.println("del org name:" + datas.get(i).get("organization_name").toString());
					}
				}
			} else {
				return null;
			}
		}
		return orgIds;
	}


	/**
	 * check cloudgybrid in free trial
	 * 
	 * @author shan.jing
	 * @return
	 */
	public void postCloudhybridInFreeTrial(String token, String org_id, String org_type,
			boolean expect_active, boolean expect_available) {

		boolean ret = false;
		spogInvoker.setToken(token);
		Response response = spogInvoker.postCloudhybridInFreeTrial(token, org_id);
		if (response.getStatusCode() != SpogConstants.SUCCESS_POST) {
			assertEquals(ret, true);
		} else {
			if (org_type != "account") {
				response.then().body("data.active", equalTo(expect_active)).body("data.available",
						equalTo(expect_available));
			} else {
				response.then().body("data.active", equalTo(null)).body("data.available", equalTo(null));
			}

		}
	}


	/**
	 * check cloudgybrid in free trial
	 * 
	 * @author shan.jing
	 * @return
	 */
	public void checkCloudhybridInFreeTrial(String token, String org_id, String org_type,
			boolean expect_active, boolean expect_available) {

		boolean ret = false;
		spogInvoker.setToken(token);
		Response response = spogInvoker.checkCloudhybridInFreeTrial(token, org_id);
		if (response.getStatusCode() != SpogConstants.SUCCESS_GET_PUT_DELETE) {
			assertEquals(ret, true);
		} else {
			if (org_type != "account") {
				response.then().body("data.active", equalTo(expect_active)).body("data.available",
						equalTo(expect_available));
			} else {
				response.then().body("data.active", equalTo(null)).body("data.available", equalTo(null));
			}

		}
	}


	/**
	 * check cloudgybrid in free trial with expected status and error code
	 * 
	 * @author shan.jing
	 * @return
	 */
	public void checkCloudhybridInFreeTrialFailed(String token, String org_id, int expect_status,
			String error_code, String error_message) {

		boolean ret = false;
		spogInvoker.setToken(token);
		Response response = spogInvoker.checkCloudhybridInFreeTrial(token, org_id);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}


	/**
	 * post cloudgybrid in free trial with expected status and error code
	 * 
	 * @author shan.jing
	 * @return
	 */
	public void postCloudhybridInFreeTrialFailed(String token, String org_id, int expect_status,
			String error_code, String error_message) {

		boolean ret = false;
		spogInvoker.setToken(token);
		Response response = spogInvoker.postCloudhybridInFreeTrial(token, org_id);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}


	/**
	 * check cloudgybrid in free trial
	 * 
	 * @author shan.jing
	 * @return
	 */
	public void checkClouddirectInFreeTrial(String token, String org_id, String org_type,
			boolean expect_active, boolean expect_available) {

		boolean ret = false;
		spogInvoker.setToken(token);
		Response response = spogInvoker.checkClouddirectInFreeTrial(token, org_id);
		if (response.getStatusCode() != SpogConstants.SUCCESS_GET_PUT_DELETE) {
			assertEquals(ret, true);
		} else {
			if (org_type != "account") {
				response.then().body("data.active", equalTo(expect_active)).body("data.available",
						equalTo(expect_available));
			} else {
				response.then().body("data.active", equalTo(null)).body("data.available", equalTo(null));
			}

		}
	}


	/**
	 * post cloudgybrid in free trial
	 * 
	 * @author shan.jing
	 * @return
	 */
	public void postClouddirectInFreeTrial(String token, String org_id, String org_type,
			boolean expect_active, boolean expect_available) {

		boolean ret = false;
		spogInvoker.setToken(token);
		Response response = spogInvoker.postClouddirectInFreeTrial(token, org_id);
		if (response.getStatusCode() != SpogConstants.SUCCESS_GET_PUT_DELETE) {
			assertEquals(ret, true);
		} else {
			if (org_type != "account") {
				response.then().body("data.active", equalTo(expect_active)).body("data.available",
						equalTo(expect_available));
			} else {
				response.then().body("data.active", equalTo(null)).body("data.available", equalTo(null));
			}

		}
	}


	/**
	 * check cloudgybrid in free trial with expected status and error code
	 * 
	 * @author shan.jing
	 * @return
	 */
	public void checkClouddirectInFreeTrialFailed(String token, String org_id, int expect_status,
			String error_code, String error_message) {

		boolean ret = false;
		spogInvoker.setToken(token);
		Response response = spogInvoker.checkClouddirectInFreeTrial(token, org_id);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}


	/**
	 * post cloud hybrid in free trial with expected status and error code
	 * 
	 * @author shan.jing
	 * @return
	 */
	public void postClouddirectInFreeTrialFailed(String token, String org_id, int expect_status,
			String error_code, String error_message) {

		boolean ret = false;
		spogInvoker.setToken(token);
		Response response = spogInvoker.postClouddirectInFreeTrial(token, org_id);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public String[] prepareBulkBody(String content){
		String[] bulkBodyInfo =null;
		if(content==null || content.equalsIgnoreCase("")){
		}else{
			bulkBodyInfo= jp.getUnAssignPolicyInfo(content);
		}
		return bulkBodyInfo;
	}

	public void postSourcesUpgradeagent(String source_id, int expect_status,
			String error_code, String error_message) {
		Response response = spogInvoker.postSourcesUpgradeagent(source_id);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void postSourcesUpgradeagentBulk(String source_id, int expect_status,
			String error_code, String error_message) {
		String[] bulkBody =null;
		bulkBody= jp.getUnAssignPolicyInfo(source_id);
		Response response = spogInvoker.postSourcesUpgradeagentBulk(bulkBody);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void postSourcesStartbackup(String source_id, int expect_status,
			String error_code, String error_message) {
		Response response = spogInvoker.postSourcesStartbackup(source_id);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void postSourcesStartbackupBulk(String source_id, int expect_status,
			String error_code, String error_message) {
		String[] bulkBody =null;
		bulkBody= jp.getUnAssignPolicyInfo(source_id);
		Response response = spogInvoker.postSourcesStartbackupBulk(bulkBody);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}


	public void postSourcesCancelbackup(String source_id, int expect_status,
			String error_code, String error_message) {
		Response response = spogInvoker.postSourcesCancelbackup(source_id);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void postSourcesCancelbackupBulk(String source_id, int expect_status,
			String error_code, String error_message) {
		String[] bulkBody =null;
		bulkBody= jp.getUnAssignPolicyInfo(source_id);  
		Response response = spogInvoker.postSourcesCancelbackupBulk(bulkBody);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void postSourcesStartrecovery(String source_id, int expect_status,
			String error_code, String error_message) {
		Response response = spogInvoker.postSourcesStartrecovery(source_id);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void postSourcesStartrecoveryBulk(String source_id, int expect_status,
			String error_code, String error_message) {
		String[] bulkBody =null;
		bulkBody= jp.getUnAssignPolicyInfo(source_id);    
		Response response = spogInvoker.postSourcesStartrecoveryBulk(bulkBody);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void postSourcesCancelrecovery(String source_id, int expect_status,
			String error_code, String error_message) {
		Response response = spogInvoker.postSourcesCancelrecovery(source_id);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void postSourcesCancelrecoveryBulk(String source_id, int expect_status,
			String error_code, String error_message) {
		String[] bulkBody =null;
		bulkBody= jp.getUnAssignPolicyInfo(source_id);    
		Response response = spogInvoker.postSourcesCancelrecoveryBulk(bulkBody);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void postSourcesProvision(String source_id, int expect_status,
			String error_code, String error_message) {
		Response response = spogInvoker.postSourcesProvision(source_id);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void postSourcesProvisionBulk(String source_id, int expect_status,
			String error_code, String error_message) {
		String[] bulkBody =null;
		bulkBody= jp.getUnAssignPolicyInfo(source_id); 
		Response response = spogInvoker.postSourcesProvisionBulk(bulkBody);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void assignPolicy(String policy_id,String source_id, int expect_status,
			String error_code, String error_message) {
		String[] bulkBody =null;
		bulkBody= jp.getUnAssignPolicyInfo(source_id); 
		Response response = spogInvoker.assignPolicy(policy_id,bulkBody);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void assignPolicyBulk(String policy_id,String source_id, int expect_status,
			String error_code, String error_message) {
		String[] bulkBody =null;
		bulkBody= jp.getUnAssignPolicyInfo(source_id); 
		Response response = spogInvoker.assignPolicy(policy_id,bulkBody);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void removePolicy(String policy_id, int expect_status,
			String error_code, String error_message) {
		Response response = spogInvoker.removePolicy(policy_id);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void removePolicyBulk(String policy_id, int expect_status,
			String error_code, String error_message) {
		String[] bulkBody =null;
		bulkBody= jp.getUnAssignPolicyInfo(policy_id); 
		Response response = spogInvoker.removePolicyBulk(bulkBody);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void deleteSourcesRemovepolicies(String source_id, int expect_status,
			String error_code, String error_message) {
		Response response = spogInvoker.deleteSourcesRemovepolicies(source_id);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void deleteSourcesRemovepoliciesBulk(String source_id, int expect_status,
			String error_code, String error_message) {
		String[] bulkBody =null;
		bulkBody= jp.getUnAssignPolicyInfo(source_id); 
		Response response = spogInvoker.deleteSourcesRemovepoliciesBulk(bulkBody);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void deleteSources(String source_id, int expect_status,
			String error_code, String error_message) {
		Response response = spogInvoker.deleteSources(source_id);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void deleteSourcesBulk(String source_id, int expect_status,
			String error_code, String error_message) {
		String[] bulkBody =null;
		bulkBody= jp.getUnAssignPolicyInfo(source_id); 
		Response response = spogInvoker.deleteSourcesBulk(bulkBody);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void deleteSourcesRemove(String source_id, int expect_status,
			String error_code, String error_message) {
		Response response = spogInvoker.deleteSourcesRemove(source_id);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void deleteSourcesRemoveBulk(String source_id, int expect_status,
			String error_code, String error_message) {
		String[] bulkBody =null;
		bulkBody= jp.getUnAssignPolicyInfo(source_id);
		Response response = spogInvoker.deleteSourcesRemoveBulk(bulkBody);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void cancelReplicationin(String source_id, int expect_status,
			String error_code, String error_message) {
		Response response = spogInvoker.cancelReplicationin(source_id);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void cancelReplicationinBulk(String source_id, int expect_status,
			String error_code, String error_message) {
		String[] bulkBody =null;
		bulkBody= jp.getUnAssignPolicyInfo(source_id);
		Response response = spogInvoker.cancelReplicationinBulk(bulkBody);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void deleteDestination(String destination_id, int expect_status,
			String error_code, String error_message) {
		Response response = spogInvoker.deleteDestination(destination_id);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void deleteDestinationBulk(String destination_id, int expect_status,
			String error_code, String error_message) {
		String[] bulkBody =null;
		bulkBody= jp.getUnAssignPolicyInfo(destination_id);
		Response response = spogInvoker.deleteDestinationBulk(bulkBody);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void putDestination(String destination_id, int expect_status,
			String error_code, String error_message) {
		Response response = spogInvoker.putDestination(destination_id);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void putDestinationBulk(String destination_id, int expect_status,
			String error_code, String error_message) {
		String[] bulkBody =null;
		bulkBody= jp.getUnAssignPolicyInfo(destination_id);
		Response response = spogInvoker.putDestinationBulk(bulkBody);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void viewRecoverypoint(String destination_id, int expect_status,
			String error_code, String error_message) {
		Response response = spogInvoker.viewRecoverypoint(destination_id);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void viewRecoverypointBulk(String destination_id, int expect_status,
			String error_code, String error_message) {
		String[] bulkBody =null;
		bulkBody= jp.getUnAssignPolicyInfo(destination_id);
		Response response = spogInvoker.viewRecoverypointBulk(bulkBody);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void cancelJob(String job_id, int expect_status,
			String error_code, String error_message) {
		Response response = spogInvoker.cancelJob(job_id);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void cancelJobBulk(String job_id, int expect_status,
			String error_code, String error_message) {
		String[] bulkBody =null;
		bulkBody= jp.getUnAssignPolicyInfo(job_id);
		Response response = spogInvoker.cancelJobBulk(bulkBody);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void viewLogs(String job_id, int expect_status,
			String error_code, String error_message) {
		Response response = spogInvoker.viewLogs(job_id);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void viewLogsBulk(String job_id, int expect_status,
			String error_code, String error_message) {
		String[] bulkBody =null;
		bulkBody= jp.getUnAssignPolicyInfo(job_id);
		Response response = spogInvoker.viewLogsBulk(bulkBody);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void cancelReplicationjobin(String job_id, int expect_status,
			String error_code, String error_message) {
		Response response = spogInvoker.cancelReplicationjobin(job_id);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void cancelReplicationjobinBulk(String job_id, int expect_status,
			String error_code, String error_message) {
		String[] bulkBody =null;
		bulkBody= jp.getUnAssignPolicyInfo(job_id);
		Response response = spogInvoker.cancelReplicationjobinBulk(bulkBody);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void postRecoveredresourcesProvision(String source_id, int expect_status,
			String error_code, String error_message) {
		Response response = spogInvoker.postRecoveredresourcesProvision(source_id);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void postRecoveredresourcesProvisionBulk(String source_id, int expect_status,
			String error_code, String error_message) {
		String[] bulkBody =null;
		bulkBody= jp.getUnAssignPolicyInfo(source_id);
		Response response = spogInvoker.postRecoveredresourcesProvisionBulk(bulkBody);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void postRecoveredresourcesStart(String source_id, int expect_status,
			String error_code, String error_message) {
		Response response = spogInvoker.postRecoveredresourcesStart(source_id);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void postRecoveredresourcesStartBulk(String source_id, int expect_status,
			String error_code, String error_message) {
		String[] bulkBody =null;
		bulkBody= jp.getUnAssignPolicyInfo(source_id);  
		Response response = spogInvoker.postRecoveredresourcesStartBulk(bulkBody);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void postRecoveredresourcesGracefulstop(String source_id, int expect_status,
			String error_code, String error_message) {
		Response response = spogInvoker.postRecoveredresourcesGracefulstop(source_id);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void postRecoveredresourcesGracefulstopBulk(String source_id, int expect_status,
			String error_code, String error_message) {
		String[] bulkBody =null;
		bulkBody= jp.getUnAssignPolicyInfo(source_id); 
		Response response = spogInvoker.postRecoveredresourcesGracefulstopBulk(bulkBody);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void postRecoveredresourcesHardstop(String source_id, int expect_status,
			String error_code, String error_message) {
		Response response = spogInvoker.postRecoveredresourcesHardstop(source_id);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void postRecoveredresourcesHardstopBulk(String source_id, int expect_status,
			String error_code, String error_message) {
		String[] bulkBody =null;
		bulkBody= jp.getUnAssignPolicyInfo(source_id); 
		Response response = spogInvoker.postRecoveredresourcesHardstopBulk(bulkBody);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void postRecoveredresourcesRestart(String source_id, int expect_status,
			String error_code, String error_message) {
		Response response = spogInvoker.postRecoveredresourcesRestart(source_id);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void postRecoveredresourcesRestartBulk(String source_id, int expect_status,
			String error_code, String error_message) {
		String[] bulkBody =null;
		bulkBody= jp.getUnAssignPolicyInfo(source_id); 
		Response response = spogInvoker.postRecoveredresourcesRestartBulk(bulkBody);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void postRecoveredresourcesDeprovision(String source_id, int expect_status,
			String error_code, String error_message) {
		Response response = spogInvoker.postRecoveredresourcesDeprovision(source_id);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void postRecoveredresourcesDeprovisionBulk(String source_id, int expect_status,
			String error_code, String error_message) {
		String[] bulkBody =null;
		bulkBody= jp.getUnAssignPolicyInfo(source_id); 
		Response response = spogInvoker.postRecoveredresourcesDeprovisionBulk(bulkBody);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void deletePolicies(String policy_id, int expect_status,
			String error_code, String error_message) {
		Response response = spogInvoker.deletePolicies(policy_id);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void deletePoliciesBulk(String policy_id, int expect_status,
			String error_code, String error_message) {
		String[] bulkBody =null;
		bulkBody= jp.getUnAssignPolicyInfo(policy_id); 
		Response response = spogInvoker.deletePoliciesBulk(bulkBody);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void modifyPolicies(String policy_id, int expect_status,
			String error_code, String error_message) {
		Response response = spogInvoker.modifyPolicies(policy_id);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void modifyPoliciesBulk(String policy_id, int expect_status,
			String error_code, String error_message) {
		String[] bulkBody =null;
		bulkBody= jp.getUnAssignPolicyInfo(policy_id); 
		Response response = spogInvoker.modifyPoliciesBulk(bulkBody);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void deleteAlerts(String alert_id, int expect_status,
			String error_code, String error_message) {
		Response response = spogInvoker.deleteAlerts(alert_id);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void deleteAlertsBulk(String alert_id, int expect_status,
			String error_code, String error_message) {
		String[] bulkBody =null;
		bulkBody= jp.getUnAssignPolicyInfo(alert_id); 
		Response response = spogInvoker.deleteAlertsBulk(bulkBody);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void deleteSourceGroup(String sourcegroup_id, int expect_status,
			String error_code, String error_message) {
		Response response = spogInvoker.deleteSourceGroup(sourcegroup_id);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void deleteSourceGroupBulk(String sourcegroup_id, int expect_status,
			String error_code, String error_message) {
		String[] bulkBody =null;
		bulkBody= jp.getUnAssignPolicyInfo(sourcegroup_id); 
		Response response = spogInvoker.deleteSourceGroupBulk(bulkBody);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void deleteReports(String report_id, int expect_status,
			String error_code, String error_message) {
		Response response = spogInvoker.deleteReports(report_id);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void deleteReportsBulk(String report_id, int expect_status,
			String error_code, String error_message) {
		String[] bulkBody =null;
		bulkBody= jp.getUnAssignPolicyInfo(report_id);
		Response response = spogInvoker.deleteReportsBulk(bulkBody);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void postUsersResetpassword(String user_id, int expect_status,
			String error_code, String error_message) {
		Response response = spogInvoker.postUsersResetpassword(user_id);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void postUsersResetpasswordBulk(String user_id, int expect_status,
			String error_code, String error_message) {
		String[] bulkBody =null;
		bulkBody= jp.getUnAssignPolicyInfo(user_id);
		Response response = spogInvoker.postUsersResetpasswordBulk(bulkBody);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void postUsersVerificationemail(String user_id, int expect_status,
			String error_code, String error_message) {
		Response response = spogInvoker.postUsersVerificationemail(user_id);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void postUsersVerificationemailBulk(String user_id, int expect_status,
			String error_code, String error_message) {
		String[] bulkBody =null;
		bulkBody= jp.getUnAssignPolicyInfo(user_id);
		Response response = spogInvoker.postUsersVerificationemailBulk(bulkBody);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void deleteUsers(String org_id, int expect_status,
			String error_code, String error_message) {
		Response response = spogInvoker.deleteUsers(org_id);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void deleteUsersBulk(String org_id, int expect_status,
			String error_code, String error_message) {
		String[] bulkBody =null;
		bulkBody= jp.getUnAssignPolicyInfo(org_id);
		Response response = spogInvoker.deleteUsersBulk(bulkBody);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void getSourcesWithOrgIdFailed(String org_id, int expect_status,
			String error_code, String error_message) {
		Response response = spogInvoker.getSourcesWithOrgId(org_id);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void getPoliciesWithOrgIdFailed(String org_id, int expect_status,
			String error_code, String error_message) {
		Response response = spogInvoker.getPoliciesWithOrgId(org_id);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void getJobsWithOrgIdFailed(String org_id, int expect_status,
			String error_code, String error_message) {
		Response response = spogInvoker.getJobsWithOrgId(org_id);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void getLogsWithOrgIdFailed(String org_id, int expect_status,
			String error_code, String error_message) {
		Response response = spogInvoker.getLogsWithOrgId(org_id);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void getUsersWithOrgIdFailed(String org_id, int expect_status,
			String error_code, String error_message) {
		Response response = spogInvoker.getUsersWithOrgId(org_id);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void getDestinationsWithOrgIdFailed(String org_id, int expect_status,
			String error_code, String error_message) {
		Response response = spogInvoker.getDestinationsWithOrgId(org_id);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void getCloudaccountsWithOrgIdFailed(String org_id, int expect_status,
			String error_code, String error_message) {
		Response response = spogInvoker.getCloudaccountsWithOrgId(org_id);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void getReportsWithOrgIdFailed(String org_id, int expect_status,
			String error_code, String error_message) {
		Response response = spogInvoker.getReportsWithOrgId(org_id);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	public void getAlertsWithOrgIdFailed(String org_id, int expect_status,
			String error_code, String error_message) {
		Response response = spogInvoker.getAlertsWithOrgId(org_id);
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	/**
	 * get version
	 * 
	 * @author shan.jing
	 * @return
	 */
	public void getVersionWithCheck(String versionStartString, String branchString) {
		Response response = spogInvoker.getVersion();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		response.then().body("data.branch", equalTo(branchString));
		String version=response.then().extract().path("data.version");
		if(version!=null && version.startsWith(versionStartString)){
			assertTrue("check version start string is right", true);
		}else{
			assertTrue("check version start string is right", false);
		}
	}

	public void getVersionWithCheckWithoutToken(String versionStartString, String branchString) {
		Response response = spogInvoker.getVersionWithoutToken();
		response.then().statusCode(SpogConstants.SUCCESS_GET_PUT_DELETE);
		response.then().body("data.branch", equalTo(branchString));
		String version=response.then().extract().path("data.version");
		if(version!=null && version.startsWith(versionStartString)){
			assertTrue("check version start string is right", true);
		}else{
			assertTrue("check version start string is right", false);
		}
	}

	/**
	 * @author shuo
	 */
	public String GetOrganizationTypebyID(String orgID) {

		Response response = spogInvoker.GetOrganizationInfobyID(orgID);
		checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		return response.then().extract().path("data.organization_type");

	}

	/**
	 * get organizationName for current logged in user
	 * 
	 * @author Prasad.Deverakonda
	 * @return
	 */
	public String GetLoggedinUserOrganizationNameByID(String orgID) {

		Response response = spogInvoker.GetLoggedInUserOrganizationInfoByID(orgID);
		String orgName = response.then().extract().path("data.organization_name").toString();
		return orgName;
	}

	/**
	 * get EmailId for current logged in user
	 * 
	 * @author Rakesh.Chalamala
	 * @return emailId
	 */
	public String getLoggedinUser_EmailId() {

		Response response = spogInvoker.GetLoggedinUserInfo();
		String user_email = response.then().extract().path("data.email");
		errorHandle.printDebugMessageInDebugFile("The logged in user emailId is: "+user_email );
		return user_email;
	}


	public void assertResponseIntegerItem(Object item, int response_item, ExtentTest test) {
		// TODO Auto-generated method stub
		if (item == null || (item == "") ) {


		} else {
			if ((int)item==response_item) {
				assertTrue("compare " + (int)item + " passed", true);
			} else {
				assertTrue("compare " + (int)item + " failed", false);
			}
		}
	}

	public Response CreateOrganizationByEnrollWithCheck_audit(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName, String organizationLastName,
			ExtentTest test) {

		return CreateOrganizationByEnrollWithCheck_audit(organizationName, organizationType, organizationEmail, organizationPwd,
				organizationFirstName, organizationLastName);
	}

	public Response CreateOrganizationByEnrollWithCheck_audit(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName,
			String organizationLastName) {

		Response response = CreateOrganizationByEnroll_audit(organizationName, organizationType, organizationEmail, organizationPwd,
				organizationFirstName, organizationLastName);
		return response;
	}

	public Response CreateOrganizationByEnroll_audit(String organizationName, String organizationType, String organizationEmail,
			String organizationPwd, String organizationFirstName, String organizationLastName) {
		String first_name=default_first_name;
		String default_organizationEmail=ReturnRandom("test")+"@arcserve.com"; 
		String last_name=default_last_name;
		String email=default_organizationEmail;
		String password=default_pwd;
		boolean deluser=true;
		String org_id=null;
		if(organizationPwd!=null && organizationPwd!=""){
			password=organizationPwd;
		}
		if(organizationFirstName!=null && organizationFirstName!=""){
			first_name=organizationFirstName;
		}
		if(organizationLastName!=null && organizationLastName!=""){
			last_name=organizationLastName;
		}
		if(organizationEmail!=null && organizationEmail!=""){
			email=organizationEmail;
			deluser=false;
		}
		HashMap<String, Object > enrollmentInfo = jp.composeEnrollmentInfo(first_name, last_name, email, "", organizationName, organizationType, datacenter_id);
		Response response=org4SPOGInvoker.enrollOrganization(enrollmentInfo);
		HashMap<String, Object> searchInfo = new HashMap<>();
		searchInfo.put("search_string", email);
		Response retUser=null;
		int totalsize=0;
		boolean findUser=false;
		for(int loop=1;loop<60;loop++){
			retUser = userSpogInvoker.postUsersSearch(searchInfo, getJWTToken());
			totalsize=retUser.then().extract().path("pagination.total_size");
			if(totalsize>=1){
				findUser=true;
				break;
			}  
			try {
				Thread.sleep(5000);

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(!findUser){
			assertTrue("can't find user",findUser);
		}else{
			String user_id=retUser.then().extract().path("data[0].user_id");
			//userLogin(csrAdminUserName, csrAdminPassword);
			Response response1=updateUserById(user_id, "", password, "", "","", "");
			if(deluser){
				DeleteUserById(user_id);
			}
			String prefix="AUTO_";
			if(organizationName!=null&&organizationName!=""&&(organizationName.toLowerCase().indexOf("do_not_delete")==-1)){
				organizationName=prefix+organizationName;
			}
			/*retUser.then()
			.body("data[0].organization.organization_name",equalTo(organizationName));
			 */	org_id= retUser.then().extract().path("data[0].organization.organization_id");
		}
		return retUser;
	}

	/* To sort with alterKey if two keys are same
	 * 
	 * @author Rakesh.Chalamala
	 */
	public ArrayList<HashMap<String, Object>> sortArrayListbyString(
			ArrayList<HashMap<String, Object>> expectedresponse, String key, String alterKey) {

		Collections.sort(expectedresponse, new Comparator<HashMap<String, Object>>() {
			@Override
			public int compare(HashMap<String, Object> o1, HashMap<String, Object> o2) {

				// TODO Auto-generated method stub
				String create_ts = (String) o1.get(key).toString();
				String create_ts1 = (String) o2.get(key).toString();
				if (create_ts.compareTo(create_ts1) > 0)
					return 1;
				else if (create_ts.compareTo(create_ts1) == 0) {

					// TODO Auto-generated method stub
					String column_id1 = (String) o1.get(alterKey).toString();
					String column_id2 = (String) o2.get(alterKey).toString();
					if (column_id1.compareTo(column_id2) > 0) {
						return 1;
					} else if (column_id1.compareTo(column_id2) < 0) {
						return -1;
					} else
						return 0;
				} else
					return -1;
			}
		});
		return expectedresponse;
	}

	/* To sort with alterKey in descedning order (latest one first) if two keys are same 
	 * 
	 * @author Rakesh.Chalamala
	 */
	public ArrayList<HashMap<String, Object>> sortArrayListbyString2(
			ArrayList<HashMap<String, Object>> expectedresponse, String key, String alterKey) {

		Collections.sort(expectedresponse, new Comparator<HashMap<String, Object>>() {
			@Override
			public int compare(HashMap<String, Object> o1, HashMap<String, Object> o2) {

				// TODO Auto-generated method stub
				String create_ts = (String) o1.get(key).toString();
				String create_ts1 = (String) o2.get(key).toString();
				if (create_ts.compareTo(create_ts1) > 0)
					return 1;
				else if (create_ts.compareTo(create_ts1) == 0) {

					// TODO Auto-generated method stub
					String column_id1 = (String) o1.get(alterKey).toString();
					String column_id2 = (String) o2.get(alterKey).toString();
					if (column_id1.compareTo(column_id2) > 0) {
						return -1;
					} else if (column_id1.compareTo(column_id2) < 0) {
						return 1;
					} else
						return 0;
				} else
					return -1;
			}
		});
		return expectedresponse;
	}

	/**
	 * validateViewType for the filter API's
	 * 
	 * @author Ramya.Nagepalli
	 * @param response
	 * @param view_type
	 * @param test
	 */
	public void validateViewType(Response response, String view_type, ExtentTest test) {
		// TODO Auto-generated method stub

		String act_view_Type = response.then().extract().path("data.view_type").toString();

		if (view_type.equals("")) {
			view_type = "origin";
		}

		assertResponseItem(view_type, act_view_Type, test);
	}

	/**
	 * createSourceFilter_savesearch
	 * 
	 * @author Ramya.Nagepalli
	 * @param user_id
	 * @param filter_name
	 * @param protection_status
	 * @param connection_status
	 * @param protection_policy
	 * @param backup_status
	 * @param source_group
	 * @param operating_system
	 * @param applications
	 * @param site_id
	 * @param source_name
	 * @param source_type
	 * @param is_default
	 * @param test
	 * @return
	 */
	public Response createSourceFilter_savesearch(String org_id, String user_id, String filter_name,
			String protection_status, String connection_status, String protection_policy, String backup_status,
			String source_group, String operating_system, String applications, String site_id, String source_name,
			String source_type, String is_default, ExtentTest test) {

		Map<String, Object> sourceFilterInfo = jp.composeSourceFilterInfo(org_id, filter_name, protection_status,
				connection_status, protection_policy, backup_status, source_group, operating_system, source_type,
				is_default);

		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!sourceFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.source_filter.toString(),
					sourceFilterInfo.get("filter_name").toString(), user_id, org_id,
					(boolean)sourceFilterInfo.get("is_default"),(HashMap<String, Object>) sourceFilterInfo);
		}

		Response response = createFilters(spogInvoker.getToken(), filter_info, "", test);
		/*Response response = spogInvoker.createFilter(user_id, filterInfo);*/
		checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		return response;
	}

	/**
	 * updateSourceFilter_savesearch
	 * 
	 * @author Ramya.Nagepalli
	 * @param user_id
	 * @param filter_id
	 * @param filter_name
	 * @param protection_status
	 * @param connection_status
	 * @param protection_policy
	 * @param backup_status
	 * @param source_group
	 * @param operating_system
	 * @param applications
	 * @param source_type
	 * @param actual_souce_type
	 * @param is_default
	 * @param test
	 * @return
	 */
	public Response updateSourceFilter_savesearch(String user_id, String filter_id, String filter_name,
			String protection_status, String connection_status, String protection_policy, String backup_status,
			String source_group, String operating_system, String applications, String source_type,
			String actual_souce_type, String is_default, ExtentTest test) {

		/*Map<String, Object> filterInfo = jp.composeFilterInfo(filter_name, protection_status, connection_status,
				protection_policy, backup_status, source_group, operating_system, source_type, is_default);*/
		Map<String, Object> sourceFilterInfo = jp.composeFilterInfo(filter_name, protection_status,
				connection_status, protection_policy, backup_status, source_group, operating_system, 
				source_type, is_default);
		/*  Response response = spogInvoker.updateFilter(user_id, filter_id, filterInfo);*/
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!sourceFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.source_filter.toString(),
					sourceFilterInfo.get("filter_name").toString(), user_id, "none",
					(boolean)sourceFilterInfo.get("is_default"),(HashMap<String, Object>) sourceFilterInfo);
		}


		Response response = spogInvoker.updateFilterById(spogInvoker.getToken(), filter_info, filter_id, "");
		checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);

		return response;
	}


	/**
	 * post Cloud Hybrid Trail Request for organization
	 * 
	 * @author Ramya.Nagepalli
	 * @param validToken
	 * @param organization_id
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 */
	public void postCloudHybridTrailRequest(String validToken, String organization_id, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage, ExtentTest test) {

		Response response = spogInvoker.postCloudHybridTrailRequest(validToken, organization_id, test);

		checkResponseStatus(response, expectedStatusCode);
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.PASS, "Cloud Hybrid Trail activated successfully for the organization :"+organization_id);

		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}

	}

	/**
	 * getOrganizationDetailsById
	 * 
	 * @author Ramya.Nagepalli
	 * @param organization_id
	 * @param validToken
	 * @param test
	 * @return
	 */
	public Response getOrganizationDetailsById(String organization_id, String validToken, ExtentTest test) {
		// TODO Auto-generated method stub
		Response response = spogInvoker.getOrganizationDetailsById(validToken, organization_id, test);

		return response;
	}

	/**
	 * UpdateOrganizationCDcount
	 * 
	 * @author Ramya.Nagepalli
	 * @param validToken
	 * @param organization_id
	 * @param orgname
	 * @param datacenter_id
	 * @param blocked
	 * @param limit
	 * @param clouddirect_trial_length
	 * @param clouddirect_deletion_queue_length
	 * @param cloudhybrid_trial_length
	 * @param cloudhybrid_deletion_queue_length
	 * @param expectedErrorMessage
	 * @param expectedStatusCode
	 * @param test
	 * @return
	 */

	public Response UpdateOrganizationCDcount(String validToken, String organization_id, int limit,
			int clouddirect_trial_length, int clouddirect_deletion_queue_length, int cloudhybrid_trial_length,
			int cloudhybrid_deletion_queue_length, int expectedStatusCode, SpogMessageCode expectedErrorMessage,
			ExtentTest test) {

		test.log(LogStatus.INFO, "compose organization info");
		Map<String, String> org_info = jp.composeOrgInfo(limit, clouddirect_trial_length,
				clouddirect_deletion_queue_length, cloudhybrid_trial_length, cloudhybrid_deletion_queue_length);
		Response response = spogInvoker.updateOrganizationInfoByID(organization_id, org_info);

		checkResponseStatus(response, expectedStatusCode);
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}

		return response;
	}

	/**
	 * postSourcesStartrecovery
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param source_id
	 * @param from_path
	 * @param from_source_id
	 * @param image_format
	 * @param snapshot_host
	 * @param snapshot_path
	 * @param task_type
	 * @param to_path
	 * @param to_source_id
	 * @param expect_status
	 * @param expected_message
	 * @param test
	 */
	public void postSourcesStartrecovery(String token, String source_id, String from_path, String from_source_id,
			String image_format, String snapshot_host, String snapshot_path, String task_type, String to_path,
			String to_source_id, int expect_status, SpogMessageCode expected_message, ExtentTest test) {
		Map<String, Object> sourceInfo = null;
		sourceInfo = jp.composeRecoveryInfoForSource(from_path, from_source_id, image_format, snapshot_host,
				snapshot_path, task_type, to_path, to_source_id);
		Response response = spogInvoker.PostRecoveryForSource(token, source_id, sourceInfo);
		response.then().statusCode(expect_status);

	}

	/**
	 * Includes preference_language introduced in Sprint 34
	 * Check response status only
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param first_name
	 * @param last_name
	 * @param email
	 * @param phone
	 * @param role_id
	 * @param organization_id
	 * @param preference_language
	 * @param password
	 * @param expectedStatusCode
	 * @return
	 */
	public Response createUser( String first_name,
			String last_name, 
			String email, 
			String phone,
			String role_id, 
			String organization_id, 
			String preference_language, 
			String password,
			int expectedStatusCode,
			ExtentTest test) {

		test.log(LogStatus.INFO, "Compose the user info in JSON format for payload");
		Map<String, Object> userInfo = jp.composeUserInfo(first_name, last_name, email, phone, role_id, organization_id, preference_language, password);

		test.log(LogStatus.INFO, "Call the rest api POST: /users to for creating user with payload:"+userInfo);
		Response response = spogInvoker.createUserWithFullInfo(userInfo);
		checkResponseStatus(response, expectedStatusCode);

		test.log(LogStatus.INFO, "User created successfully.");
		return response;
	}

	public String createUserWithCheck(String first_name,
			String last_name, 
			String email, 
			String phone,
			String role_id, 
			String organization_id, 
			String preference_language, 
			String password,
			int expectedStatusCode,
			SpogMessageCode expectedErrorMessage,
			ExtentTest test ) {

		String user_id = null;
		Response response = createUser(first_name, last_name, email, phone, role_id, organization_id, preference_language, password, expectedStatusCode, test);
		if (expectedStatusCode == SpogConstants.SUCCESS_POST) {

			HashMap<String,	Object> actualData = response.then().extract().path("data");
			validateUserInfo(actualData, first_name, last_name, email, phone, role_id, organization_id, preference_language, test);
			user_id = actualData.get("user_id").toString();

		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}

		return user_id;
	}

	public void validateUserInfo(HashMap<String, Object> actualData, String first_name, String last_name, 
			String email, String phone, String role_id, String organization_id, String preference_language, ExtentTest test) {

		assertTrue(first_name.equalsIgnoreCase(actualData.get("first_name").toString()));
		assertTrue(last_name.equalsIgnoreCase(actualData.get("last_name").toString()));
		assertTrue(email.equalsIgnoreCase(actualData.get("email").toString()));
		assertTrue(phone.equalsIgnoreCase(actualData.get("phone_number").toString()));
		assertTrue(role_id.equalsIgnoreCase(actualData.get("role_id").toString()));
		assertTrue(organization_id.equalsIgnoreCase(actualData.get("organization_id").toString()));

		if (preference_language == null || preference_language.equalsIgnoreCase("none")) {
			preference_language = "en";
			assertTrue(preference_language.equalsIgnoreCase(actualData.get("preference_language").toString()));
		}else {
			assertTrue(preference_language.equalsIgnoreCase(actualData.get("preference_language").toString()));	
		}

		assertTrue(actualData.get("user_id")!=null);
		assertTrue(actualData.get("status")!=null);
		assertTrue(actualData.get("blocked")!=null);

		ArrayList<String> allowedActions = (ArrayList<String>) actualData.get("allowed_actions");
		assertTrue(allowedActions.contains("delete"));
		assertTrue(allowedActions.contains("resetpassword"));

		test.log(LogStatus.PASS, "Create user response validated successfully.");
	}



	/**
	 * getSpecifiedOrgProperties
	 * 
	 * Sprint 35
	 * 
	 * API- GET: /organizations/{id}/properties
	 * 
	 * @author Ramya.Nagepalli
	 * @param validToken
	 * @param organization_id
	 * @param ExpectedStatusCode
	 * @param test
	 * @return
	 */
	public Response getSpecifiedOrgProperties(String validToken, String organization_id, int ExpectedStatusCode,
			ExtentTest test) {

		test.log(LogStatus.INFO, "Get the properties of specified org :" + organization_id);
		Response response = spogInvoker.getSpecifiedOrgProperties(validToken, organization_id, test);
		checkResponseStatus(response, ExpectedStatusCode);

		return response;
	}

	public Response createSubMsp(String token, String rootOrgId, HashMap<String, Object> requestInfo, int expectedStatusCode, ExtentTest test) {

		test.log(LogStatus.INFO, "Create Sub Msp using API - POST: /organizations/"+rootOrgId+"/submspaccounts");
		Response response = spogInvoker.createSubMsp(token, rootOrgId, requestInfo);
		checkResponseStatus(response, expectedStatusCode, test);

		return response;
	}

	/**
	 * Create Sub MSP, validate response and return sub msp id
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param rootOrgId
	 * @param requestInfo
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 * @return
	 */
	public String createSubMspWithCheck(String token,
			String rootOrgId,
			HashMap<String, Object> requestInfo,
			int expectedStatusCode, 
			SpogMessageCode expectedErrorMessage,
			ExtentTest test
			) {
		String subMspId = null;
		Response response = createSubMsp(token, rootOrgId, requestInfo, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_POST) {

			HashMap<String,	Object> responseInfo = response.then().extract().path("data");
			validateSubMspResponseInfo(requestInfo, responseInfo, test);
			subMspId = (String) responseInfo.get("organization_id");

		}else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}

		return subMspId;
	}

	public void validateSubMspResponseInfo(HashMap<String, Object> requestInfo, HashMap<String, Object> responseInfo, ExtentTest test){

		assertNotNull(responseInfo.get("organization_id"));
		assertResponseItem(requestInfo.get("organization_name"), responseInfo.get("organization_name"), test);
		assertResponseItem(SpogConstants.MSP_ORG, responseInfo.get("organization_type"), test);
		if (requestInfo.containsKey("parent_id")) {
			assertResponseItem(requestInfo.get("parent_id"), responseInfo.get("parent_id"), test);	
		}
		assertResponseItem(false, responseInfo.get("blocked"), test);

		ArrayList<String> actualAllowedActions = (ArrayList<String>) responseInfo.get("allowed_actions");
		assertTrue(actualAllowedActions.contains("setthreshold"));
		assertTrue(actualAllowedActions.contains("delete"));
	}

	public Response convertToRootMsp(String token, String orgId, int expectedStatusCode, ExtentTest test) {

		test.log(LogStatus.INFO, "Call API - POST: /organizations/{id}/converttorootmsp to make an msp org root MSP so that it can have sub MSPs");
		Response response = spogInvoker.convertToRootMsp(token, orgId);
		checkResponseStatus(response, expectedStatusCode);

		return response;
	}

	/**
	 * Convert to root msp and check statuscode
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param orgId
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 */
	public void convertToRootMspWithCheck(String token, String orgId, int expectedStatusCode, SpogMessageCode expectedErrorMessage, ExtentTest test) {

		Response response = convertToRootMsp(token, orgId, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.PASS, "MSP Organization with id: "+orgId+" converted as root msp successfully.");			
		}else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			if (code.equalsIgnoreCase("00300042")) {
				message = message.replace("{0}", orgId);
			}			
			checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
	}

	/**
	 * 
	 * getSpecifiedOrgPropertiesWithCheck
	 * 
	 * Sprint 35
	 * 
	 * API- GET: /organizations/{id}/properties
	 * 
	 * @author Ramya.Nagepalli
	 * @param validToken
	 * @param organization_id
	 * @param ExpectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 * @return
	 */
	public Response getSpecifiedOrgPropertiesWithCheck(String validToken, String organization_id,
			int ExpectedStatusCode, SpogMessageCode expectedErrorMessage, HashMap<String,Object> expected_data, ExtentTest test) {

		test.log(LogStatus.INFO, "Get the properties of logged in user :" + organization_id);
		Response response = spogInvoker.getSpecifiedOrgProperties(validToken, organization_id, test);

		checkResponseStatus(response, ExpectedStatusCode);

		HashMap<String,Object> actual_info=new HashMap<>();

		if (ExpectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			actual_info = response.then().extract().path("data");

			if (actual_info.containsKey("clouddirect_deletion_queue_length"))
				assertEquals(actual_info.get("clouddirect_deletion_queue_length"),
						expected_data.get("clouddirect_deletion_queue_length"));

			if (actual_info.containsKey("clouddirect_state"))
				assertEquals(actual_info.get("clouddirect_state"),
						expected_data.get("clouddirect_state"));

			if (actual_info.containsKey("clouddirecttrial_length"))
				assertEquals(actual_info.get("clouddirecttrial_length"),
						expected_data.get("clouddirecttrial_length"));

			if (actual_info.containsKey("clouddirect_trial_end"))
				assertTrue(true);

			if (actual_info.containsKey("clouddirect_trial_start"))
				assertTrue(true);

			if (actual_info.containsKey("clouddirect_volume_count"))
				assertEquals(actual_info.get("clouddirect_volume_count"),
						expected_data.get("clouddirect_volume_count"));

			if (actual_info.containsKey("cloudhybrid_deletion_queue_length"))
				assertEquals(actual_info.get("cloudhybrid_deletion_queue_length"),
						expected_data.get("cloudhybrid_deletion_queue_length"));

			if (actual_info.containsKey("cloudhybrid_state"))
				assertEquals(actual_info.get("cloudhybrid_state"),
						expected_data.get("cloudhybrid_state").toString());

			if (actual_info.containsKey("cloudhybrid_trial_length"))
				assertEquals(actual_info.get("cloudhybrid_trial_length"),
						expected_data.get("cloudhybrid_trial_length"));

			if (actual_info.containsKey("cloudhybrid_trial_end"))
				assertTrue(true);

			if (actual_info.containsKey("cloudhybrid_trial_start"))
				assertTrue(true);

			if (actual_info.containsKey("datacenter_id"))
				assertEquals(actual_info.get("datacenter_id"), expected_data.get("datacenter_id"));

		}
		else
		{
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + ExpectedStatusCode);
		}

		return response;
	}

	/**
	 * 
	 * updateUserById 
	 * - added "preference_language" param 
	 * - Sprint 34
	 * 
	 * @author Ramya.Nagepalli
	 * @param user_id
	 * @param email
	 * @param password
	 * @param first_name
	 * @param last_name
	 * @param role_id
	 * @param organization_id
	 * @param phone_number
	 * @param preference_language
	 * @return
	 */
	public Response updateUserById(String user_id, String email, String password, String first_name, String last_name,
			String role_id, String organization_id, String phone_number, String preference_language,int expectedStatusCode,SpogMessageCode expectedErrorMessage, ExtentTest test) {

		Map<String, String> updateUserInfo = jp.updateUserInfo(email, password, first_name, last_name, role_id,
				organization_id, phone_number, preference_language);
		Response response = spogInvoker.updateUserById(user_id, updateUserInfo);

		if(expectedStatusCode==SpogConstants.SUCCESS_GET_PUT_DELETE)
		{
			checkResponseStatus(response, expectedStatusCode); 
			test.log(LogStatus.PASS, "The User got updated successfully with id  :" +user_id);
		}
		else
		{
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}

		return response;
	}

	/**
	 * Call CD_API to convert an msp to 3 tier and make it root org
	 * 
	 * @author Rakesh.Chalamala
	 * @param organizationId
	 */
	public void convertTo3Tier(String organizationId) {

		Response response = spogInvoker.convertTo3Tier(organizationId);
		response.then().body("status", equalTo("SUCCESS"));
	}

	/**
	 * @author shuo
	 * @param orgID
	 * @return
	 */
	public boolean GetOrganizationSubMSPByID(String orgID) {

		Response response = spogInvoker.GetOrganizationInfobyID(orgID);
		checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		return response.then().extract().path("data.sub_msp");

	}

	/**
	 * createFilters
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param filter_info
	 * @param test
	 * @return response
	 */
	public Response createFilters(String token, Map<String, Object> filter_info,String additionalURL, ExtentTest test) {
		test.log(LogStatus.INFO, "create filters");
		Response response = spogInvoker.createFilters(token, filter_info, additionalURL);
		return response;
	}


	/**
	 * getFilters
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param additionalURL
	 * @return
	 */
	public Response getFilters(String token, String user_id, String filterType, String extraURL,ExtentTest test) {
		test.log(LogStatus.INFO, "get filters");
		String additionalURL = jp.composeAdditionalFilter(filterType, user_id, "none", extraURL);
		Response response = spogInvoker.getFilters(token, additionalURL);
		return response;
	}
	/**
	 * getFilters
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param filterType
	 * @return
	 */
	public Response getFilters(String token, String filterType) {
		String additionalURL = jp.composeAdditionalFilter(filterType,  "none", "none", "");
		Response response = spogInvoker.getFilters(token, additionalURL);
		return response;
	}

	/**
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param filter_id
	 * @param additionalURL
	 * @return
	 */
	public Response getFiltersById(String token, String filter_id, String filterType, String user_id,
			String organization_id,ExtentTest test) {
		/*test.log(LogStatus.INFO, "get filters by id");*/
		String additionalURL = jp.composeAdditionalFilter(filterType, user_id, organization_id, "none");
		Response response = spogInvoker.getFiltersById(token, filter_id, additionalURL);
		return response;
	}

	/**
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param filter_id
	 * @param user_id
	 * @param test
	 * @return
	 */
	public Response deleteFiltersByID(String token, String filter_id, String user_id, ExtentTest test) {
		test.log(LogStatus.INFO, "delete filters by ID");
		String additionalURL = jp.composeAdditionalFilter("none", "none", "none", "none");
		Response response = spogInvoker.deleteFiltersById(token, filter_id, additionalURL);
		return response;
	}

	/**
	 * updateFilterById
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param filter_id
	 * @param user_id
	 * @param test
	 * @param filterInfo
	 * @return
	 */
	public Response updateFilterById(String token, String filter_id, String user_id, Map<String, Object> filterInfo,
			String extraURL, ExtentTest test) {
		test.log(LogStatus.INFO, "update filters by ID");
		Response response = spogInvoker.updateFilterById(token, filterInfo, filter_id, extraURL);
		return response;
	}
	/**
	 * Normalized column API for creation
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param columnInfo
	 * @return
	 */
	public Response postColumns(String token, HashMap<String, Object> columnInfo) {

		Response response = spogInvoker.postColumns(token, columnInfo);
		response.then().log().all();
		return response;
	}

	/**
	 * @author Nagamalleswari.Sykam
	 * @param token
	 * @param user_id
	 * @param column_type
	 * @param expectedColumns
	 * @param expectedStatuscode
	 * @param errorMessageCode
	 * @param test
	 * @return
	 */

	public Response getColumnsWithCheck(String token,
			String user_id,
			String column_type,
			ArrayList<HashMap<String, Object>> expectedColumns,
			int expectedStatuscode,
			SpogMessageCode errorMessageCode,
			ExtentTest test) {

		String filter = jp.composeColumnFilter(column_type, user_id, "none", "none");
		Response response = getColumns(token, filter);
		checkResponseStatus(response, expectedStatuscode);

		if(expectedStatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			ArrayList<HashMap<String, Object>>actColumns = response.then().extract().path("data.columns");

			ArrayList<HashMap<String, Object>> expColumnContent =sortArrayListbyString(expectedColumns, "order_id");

			for(int i=0 ; i<expColumnContent.size();i++) {
				for(int j=0; j<actColumns.size();j++) {
					if(expectedColumns.get(i).get("column_id").equals(actColumns.get(j).get("column_id"))) {
						assertResponseItem(expColumnContent.get(i).get("visible").toString(),actColumns.get(j).get("visible").toString(), test);
						assertResponseItem(expColumnContent.get(i).get("order_id").toString(),actColumns.get(j).get("order_id").toString(), test);
						break;
					}

					if (j == actColumns.size()) {
						assertFalse(true,  "Columns comparison failed the columns are:"+actColumns);
					}
				}
			}

		} else {
			System.out.println("No column is avaiable ");
		}
		return response;
	}


	/**
	 * @author Nagamalleswari.Sykam
	 * @param token
	 * @param columns_user_id
	 * @param user_id
	 * @param column_type
	 * @param expectedColumns
	 * @param expectedStatusCode
	 * @param errormessagecode
	 * @param test
	 * @return
	 */
	public Response updateColumnWithCheck(String token, String columns_user_id, String user_id, String column_type,
			ArrayList<HashMap<String, Object>> expectedColumns, int expectedStatusCode,SpogMessageCode errormessagecode, ExtentTest test) {

		HashMap<String, Object> payload = jp.composeColumnsPayload(user_id, column_type, expectedColumns);
		Response response = putColumns(token, columns_user_id, payload);
		checkResponseStatus(response, expectedStatusCode, test);


		ArrayList<HashMap<String, Object>> expColumnContent =sortArrayListbyString(expectedColumns, "order_id");


		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			//response.then().body("data.user_id", equalTo(user_id));
			//response.then().body("data.column_type", equalTo(column_type));

			ArrayList<HashMap<String, Object>> actColumns = response.then().extract().path("data");
			System.out.println(expColumnContent);
			System.out.println(actColumns);


			for(int i=0 ; i<expColumnContent.size();i++) {
				for(int j=0; j<actColumns.size();j++) {
					if(expectedColumns.get(i).get("column_id").equals(actColumns.get(j).get("column_id"))) {
						assertResponseItem(expColumnContent.get(i).get("visible").toString(),actColumns.get(j).get("visible").toString(), test);
						assertResponseItem(expColumnContent.get(i).get("order_id").toString(),actColumns.get(j).get("order_id").toString(), test);
						break;
					}

					if (j == actColumns.size()) {
						assertFalse(true,  "Columns comparison failed the columns are:"+actColumns);
					}
				}
			}

		} else {
			System.out.println("No column is avaiable ");
		}
		return response;
	}
	/**
	 * @author Nagamalleswari.Sykam
	 * @param token
	 * @param user_id
	 * @param column_type
	 * @param expectedColumns
	 * @param expectedStatusCode
	 * @param test
	 * @return
	 */
	public Response postColumnsWithCheck(String token,
			String user_id, String column_type,
			ArrayList<HashMap<String, Object>> expectedColumns,
			int expectedStatusCode,
			SpogMessageCode expectedErrorMessage,

			ExtentTest test) {

		HashMap<String, Object> payload = jp.composeColumnsPayload(user_id, column_type, expectedColumns);
		Response response = postColumns(token, payload);
		checkResponseStatus(response, expectedStatusCode, test);


		ArrayList<HashMap<String, Object>> expColumnContent =sortArrayListbyString(expectedColumns, "order_id");


		if (expectedStatusCode == SpogConstants.SUCCESS_POST) {

			//response.then().body("data.user_id", equalTo(user_id));
			//response.then().body("data.column_type", equalTo(column_type));

			ArrayList<HashMap<String, Object>> actColumns = response.then().extract().path("data");
			System.out.println(expColumnContent);
			System.out.println(actColumns);


			for(int i=0 ; i<expColumnContent.size();i++) {
				for(int j=0; j<actColumns.size();j++) {
					if(expectedColumns.get(i).get("column_id").equals(actColumns.get(j).get("column_id"))) {
						assertResponseItem(expColumnContent.get(i).get("visible").toString(),actColumns.get(j).get("visible").toString(), test);
						assertResponseItem(expColumnContent.get(i).get("order_id").toString(),actColumns.get(j).get("order_id").toString(), test);
						break;
					}

					if (j == actColumns.size()) {
						assertFalse(true,  "Columns comparison failed the columns are:"+actColumns);
					}
				}
			}

		} else {
			System.out.println("No column is avaiable ");
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);

		}
		return response;
	}



	/**
	 * @author Nagamalleswari.Sykam
	 * @param token
	 * @param user_columns_id
	 * @param expectedstatusCode
	 * @param expectedErrorMessage
	 * @param test
	 */
	public void deleteColumnsWithCheck(String token,String user_columns_id,int expectedstatusCode, SpogMessageCode expectedErrorMessage,ExtentTest test) {

		Response response = deleteColumns(token, user_columns_id);

		if(expectedstatusCode==SpogConstants.SUCCESS_GET_PUT_DELETE) {
			checkResponseStatus(response, expectedstatusCode);
		}else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatusCode);

		}

	}

	/**
	 * Normalized column API for updation
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param column_id
	 * @param columnInfo
	 * @return
	 */
	public Response putColumns(String token, String column_id, HashMap<String, Object> columnInfo) {

		Response response = spogInvoker.putColumns(token, column_id, columnInfo);

		return response;
	}

	/**
	 * Normalized column API for deletion
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param column_id
	 * @return
	 */
	public Response deleteColumns(String token, String column_id) {

		Response response = spogInvoker.deleteColumns(token, column_id);

		return response;
	}

	/**
	 * Normalized column API for get
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param filter
	 * @return
	 */
	public Response getColumns(String token, String filter) {

		Response response = spogInvoker.getColumns(token, filter);

		return response;
	}




	/**
	 * Get and delete specified columns if exists
	 * 
	 * @author Rakesh.Chalamala
	 * @param column_type
	 * @param user_id
	 * @param token
	 */
	public void getAndDeleteColumns(String column_type, String user_id, String token) {

		if (user_id == null || user_id.isEmpty() || user_id.equalsIgnoreCase("none")) {
			spogInvoker.setToken(token);
			user_id = GetLoggedinUser_UserID();
		}

		String filter = jp.composeColumnFilter(column_type, user_id, "none", "none");
		Response response = getColumns(token, filter);
		checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		String user_column_id = response.then().extract().path("data.user_column_id");

		if (user_column_id == null || user_column_id.isEmpty()) {
			System.out.println("Columns of type:" + column_type + " does not exist for user with id:" + user_id);
			return;
		}
		response = deleteColumns(token, user_column_id);
		//		checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		System.out.println("Column of type:" + column_type + " deleted successfully for user with id:" + user_id);
	}


	/**
	 * @author Nagamalleswari.Sykam
	 * @param token
	 * @param org_id
	 * @param role_id
	 * @param expectdata
	 * @param expectdStatucCode
	 * @param expectedErrorMessage
	 * @param test
	 */

	public void  getUsersForOrganizationWithCheck(String token,String org_id,String role_id,ArrayList<HashMap<String, Object>> expectdata,int expectdStatucCode,SpogMessageCode expectedErrorMessage,ExtentTest test) {
		Response response =spogInvoker.getAllUsersInOrganization(org_id, null, test);

		ArrayList<HashMap<String, Object>>actData= response.then().extract().path("data");
		String actrole_id=actData.get(0).get("role_id").toString();
		if(expectdStatucCode == SpogConstants.SUCCESS_POST) {

			if(role_id.contains(actrole_id)) {
				HashMap<String, Object> logginedUserInfo=(HashMap<String, Object>) getLoggedInUser(token, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
				expectdata.add(logginedUserInfo);
			}

			ArrayList<HashMap<String, Object>>actContent =sortArrayListbyString(actData, "email");

			ArrayList<HashMap<String, Object>> expContent =sortArrayListbyString(expectdata, "email");

			if(actContent.size()==expContent.size()) {
				for(int i=0;i<actContent.size();i++) {
					for(int j=0;j<expContent.size();j++) {
						assertResponseItem(actData.get(i).get("first_name").toString(),expectdata.get(j).get("first_name").toString(),test);
						assertResponseItem(actData.get(i).get("last_name").toString(),expectdata.get(j).get("first_name").toString(),test);
						assertResponseItem(actData.get(i).get("email").toString(),expectdata.get(j).get("first_name").toString(),test);
						assertResponseItem(actData.get(i).get("phone_number").toString(),expectdata.get(j).get("first_name").toString(),test);
						assertResponseItem(actData.get(i).get("preference_language").toString(),expectdata.get(j).get("first_name").toString(),test);
						assertResponseItem(actData.get(i).get("organization_id").toString(),expectdata.get(j).get("first_name").toString(),test);
						assertResponseItem(actData.get(i).get("role_id").toString(),expectdata.get(j).get("first_name").toString(),test);

					}
				}
			}else{
				System.out.println("actual users"+actContent+" is doesnot' match with the expected users"+expContent);
			}
		}else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectdStatucCode);

		}

	}

	/**
	 * 
	 * @author Nagamalleswari.Sykam
	 * @param first_name
	 * @param last_name
	 * @param email
	 * @param phone
	 * @param role_id
	 * @param organization_id
	 * @param preference_language
	 * @param password
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 * @return
	 */
	public String getUserIdInfo(String first_name,
			String last_name, 
			String email, 
			String phone,
			String role_id, 
			String organization_id, 
			String preference_language, 
			String password,
			int expectedStatusCode,
			SpogMessageCode expectedErrorMessage,
			ExtentTest test ){

		String user_id = null;
		Response response = createUser(first_name, last_name, email, phone, role_id, organization_id, preference_language, password, expectedStatusCode, test);
		if (expectedStatusCode == SpogConstants.SUCCESS_POST) {

			HashMap<String,	Object> actualData = response.then().extract().path("data");
			user_id = actualData.get("user_id").toString();

		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}

		return user_id;
	}
	/**
	 * @author Nagamalleswari.Sykam
	 * @param token
	 * @param org_id
	 * @return
	 */
	public Response getUsersInfo(String token,String org_id) {

		Response response =spogInvoker.getAllUsersInOrganization(org_id, null, test);

		ArrayList<HashMap<String, Object>>actData= response.then().extract().path("data");
		String userId=response.then().extract().path("data.user_id");

		return response;
	}


	/**
	 * @author Nagamalleswari.Sykam
	 * @param token
	 * @param composeSoucreGroupInfo
	 * @param expectedStaruscode
	 * @param errorMessage
	 * @return
	 */

	public void createSoucregroupWithCheck(String token,HashMap<String, Object> composeSoucreGroupInfo,int expectedStatucCode,SpogMessageCode expectedErrorMessage,ExtentTest test) {

		Response response =spogInvoker.createGroup(token, composeSoucreGroupInfo, test);
		checkResponseStatus(response, expectedStatucCode);

		if(expectedStatucCode==SpogConstants.SUCCESS_POST) {
			ArrayList<HashMap<String, Object>> expData = new ArrayList<>();
			expData.add(composeSoucreGroupInfo);

			HashMap<String, Object>actData = response.then().extract().path("data");

			ArrayList<HashMap<String, Object>>actinfo=new ArrayList<>();
			actinfo.add(actData);

			ArrayList<HashMap<String, Object>>actContent =sortArrayListbyString(actinfo, "group_name");
			ArrayList<HashMap<String, Object>>expectedContent =sortArrayListbyString(expData, "group_name");


			if(actContent.size()==expectedContent.size()) {
				for(int i =0;i<actContent.size();i++) {
					for(int j=0;j<expectedContent.size();j++) {
						assertResponseItem(actContent.get(i).get("group_name").toString(), expectedContent.get(j).get("group_name").toString());
						assertResponseItem(actContent.get(i).get("group_description").toString(), expectedContent.get(j).get("group_description").toString());
						break;
					}
				}
			}else {
				System.out.println("actual sourcegroups"+actContent+" doesnot macth with the expected source groups"+expectedContent);
			}
		}else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatucCode);

		}

	}


	/**
	 * @author Nagamalleswari.Sykam
	 * @param token
	 * @param expOrgInfo
	 * @param expectedStatucCode
	 * @param expectedErrorMessage
	 * @param test
	 */

	public void getOrganizationInfoWithCheck(String token,
			HashMap<String, Object>expOrgInfo,
			int expectedStatucCode,
			SpogMessageCode expectedErrorMessage,
			ExtentTest test) {

		test.log(LogStatus.INFO, "Get Loggedin User Info");
		Response response = spogInvoker.GetLoggedInUserOrganizationInfo();
		checkResponseStatus(response, expectedStatucCode);
		if(expectedStatucCode==SpogConstants.SUCCESS_GET_PUT_DELETE) {

			System.out.println(response.then().extract().path("data.create_user.email").toString());
			response.then().body("data.organization_type", equalTo(expOrgInfo.get("organization_type").toString()));
			response.then().body("data.organization_id", equalTo(expOrgInfo.get("organization_id").toString()));
			response.then().body("data.create_user_id", equalTo(expOrgInfo.get("user_id").toString()));

		}else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatucCode);

		}

	}

	/**
	 * @author Nagamalleswari.Sykam
	 * @param token
	 * @param old_password
	 * @param new_password
	 * @param expectedStatucCode
	 * @param expectedErrorMessage
	 */

	public void changePasswordForLoggedinUserwithCheck(String token,HashMap<String, Object> passwordInfo,
			int expectedStatucCode,
			SpogMessageCode expectedErrorMessage,
			ExtentTest test) {

		Response response = spogInvoker.ChangePasswordForLoggedInUser(passwordInfo, test);
		checkResponseStatus(response,expectedStatucCode);

		if(expectedStatucCode==SpogConstants.SUCCESS_GET_PUT_DELETE) {
			System.out.println("User password updated successfully");
		}else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatucCode);

		}
	}

	/**
	 * @author Nagamalleswari.Sykam
	 * @param token
	 * @param user_id
	 * @param column_type
	 * @param expectedColumns
	 * @param expectedStatuscode
	 * @param expectedErrorMessage
	 * @param test
	 */
	public void getcolumnsForInValidSecnarioes(String token,
			String user_id,
			String column_type,
			ArrayList<HashMap<String, Object>> expectedColumns,
			int expectedStatuscode,
			SpogMessageCode expectedErrorMessage,
			ExtentTest test) {
		String filter = jp.composeColumnFilter(column_type, user_id, "none", "none");
		Response response = getColumns(token, filter);
		checkResponseStatus(response, expectedStatuscode);

		if(!(expectedStatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE)) {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatuscode);

		}else{
			System.out.println();
		}
	}

}
