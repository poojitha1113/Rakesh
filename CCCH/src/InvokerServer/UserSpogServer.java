package InvokerServer;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;
import org.hamcrest.Matchers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;

import com.google.common.collect.HashMultimap;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.AuditCodeConstants;
import Constants.ConnectionStatus;
import Constants.ErrorCode;
import Constants.JobStatusConstants;
import Constants.JobTypeConstants;
import Constants.LogColumnConstants;
import Constants.LogSeverityType;
import Constants.LogSourceType;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import Constants.UserColumnContsants;
import Constants.FilterTypes.filterType;
import dataPreparation.JsonPreparation;
import genericutil.ErrorHandler;
import invoker.SPOGInvoker;
import invoker.UserSpogInvoker;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;

public class UserSpogServer {
	private static JsonPreparation jp = new JsonPreparation();
	private UserSpogInvoker userSpogInvoker;
	private SPOGServer spogServer;
	public static ErrorHandler errorHandle = ErrorHandler.getErrorHandler();
	private ExtentTest test;

	private HashMap<String, Object> Uuid = new HashMap<>();

	public UserSpogServer(String baseURI, String port) {
		userSpogInvoker = new UserSpogInvoker(baseURI, port);
		spogServer = new SPOGServer(baseURI, port);
	}

	public void setToken(String token) {
		userSpogInvoker.setToken(token);
	}

	public void setUUID(String user_id, String filter_id) {
		
		Uuid.put("user_id", user_id);
		Uuid.put("filter_id", filter_id);
		
	}
	
	public HashMap<String, Object> getUUID() {
		
		return Uuid;
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
		Response response = userSpogInvoker.AuthenticateUser(userInfo, Token);

		return response;
	}

	/**
	 * @author shuo.zhang
	 * @param userId
	 * @param token
	 * @param expectedStatusCode
	 * @param expectedErrorCode
	 * @param test
	 */
	public void deleteUsersPictureWithCheck(String userId, String token, int expectedStatusCode,
			String expectedErrorCode, ExtentTest test) {

		Response response = userSpogInvoker.deleteUsersPicture(userId, token);
		response.then().log().all();
		response.then().statusCode(expectedStatusCode);
		if (expectedStatusCode != SpogConstants.SUCCESS_GET_PUT_DELETE) {
			spogServer.checkErrorCode(response, expectedErrorCode);
		}
	}

	/**
	 * @author shuo.zhang
	 * @param userId
	 * @param test
	 */
	public void deleteUsersPictureWithoutLogin(String userId, ExtentTest test) {

		Response response = userSpogInvoker.deleteUsersPictureWithoutLogin(userId);
		response.then().log().all();
		response.then().statusCode(SpogConstants.NOT_LOGGED_IN);
		spogServer.checkErrorCode(response, ErrorCode.AUTHORIZATION_HEADER_BLANK);

	}

	/**
	 * @author shuo.zhang
	 * @param token
	 * @param expectedStatusCode
	 * @param expectedErrorCode
	 * @param test
	 */
	public void deleteLoggedInUserPictureWithCheck(String token, int expectedStatusCode, String expectedErrorCode,
			ExtentTest test) {

		Response response = userSpogInvoker.deleteLoggedInUserPicture(token);
		response.then().log().all();
		response.then().statusCode(expectedStatusCode);
		if (expectedStatusCode != SpogConstants.SUCCESS_GET_PUT_DELETE) {
			spogServer.checkErrorCode(response, expectedErrorCode);
		}
	}

	/**
	 * @author shuo.zhang
	 * @param test
	 */
	public void deleteLoggedInUserPictureWithoutLogin(ExtentTest test) {

		Response response = userSpogInvoker.deleteLoggedInUserPictureWithoutLogin();
		response.then().statusCode(SpogConstants.NOT_LOGGED_IN);
		spogServer.checkErrorCode(response, ErrorCode.AUTHORIZATION_HEADER_BLANK);

	}

	/**
	 * upload picture for logged in user
	 * 
	 * @author Zhaoguo.Ma
	 * @param picturePath
	 * @param test
	 * @return
	 */

	public Response uploadPictureForLoginUser(String picturePath, ExtentTest test) {
		Response response = userSpogInvoker.uploadPictureForLoginUser(picturePath);

		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);

		String imageUrl = response.then().extract().path("data.image_url");

		// download the image from url in response and compare to original
		String tempImageURL = picturePath + "download";
		boolean timeOut=false;
		int count= 10000;
		int i=0;
		do{
			try {
				URL url = new URL(imageUrl);
			
				InputStream inputStream = url.openStream();
				FileOutputStream fileOutputStream = new FileOutputStream(new File(tempImageURL));
				int length = -1;
				byte[] buffer = new byte[1024];
				while ((length = inputStream.read(buffer)) > -1) {
					fileOutputStream.write(buffer, 0, length);
				}
				fileOutputStream.close();
				inputStream.close();
				timeOut=false;
			}catch(ConnectException e){ 
				timeOut=true;
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				i++;
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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

	/**
	 * upload picture for logged in user with error check
	 * 
	 * @param picturePath
	 * @param statusCode
	 * @param errorCode
	 * @param test
	 */
	public void uploadPictureForLoginUserWithCode(String picturePath, int statusCode, String errorCode,
			ExtentTest test) {
		Response response = userSpogInvoker.uploadPictureForLoginUser(picturePath);
		spogServer.checkResponseStatus(response, statusCode);
		spogServer.checkErrorCode(response, errorCode);
	}

	/**
	 * upload picture by userID
	 * 
	 * @author Zhaoguo.Ma
	 * @param userID
	 * @param picturePath
	 * @param test
	 * @return
	 */
	public Response uploadPictureByUserID(String userID, String picturePath, ExtentTest test) {
		Response response = userSpogInvoker.uploadPictureByUserID(userID, picturePath);

		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);

		String imageUrl = response.then().extract().path("data.image_url");

		// download the image from url in response and compare to original
		String tempImageURL = picturePath + "download";		
		boolean timeOut=false;
		int count= 1000;
		int i=0;
		InputStream inputStream =null;
		FileOutputStream fileOutputStream=null;
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("hydproxy.arcserve.com", 6588));
		do{
			try {
				URL url = new URL(imageUrl);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);
				connection.setConnectTimeout(120000); // 5 seconds connectTimeout
				connection.setReadTimeout(120000 ); // 5 seconds socketTimeout
				connection.connect(); 
			
				inputStream = url.openStream();
				fileOutputStream = new FileOutputStream(new File(tempImageURL));
				int length = -1;
				byte[] buffer = new byte[1024];
				System.out.println("read");
				while ((length = inputStream.read(buffer)) > -1) {
					fileOutputStream.write(buffer, 0, length);
				}
				timeOut=false;
				System.out.println("finish read");
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

	/**
	 * upload picture by user ID with error check
	 * 
	 * @author Zhaoguo.Ma
	 * @param userID
	 * @param picturePath
	 * @param statusCode
	 * @param errorCode
	 * @param test
	 */
	public void uploadPictureByUserIDWithCode(String userID, String picturePath, int statusCode, String errorCode,
			ExtentTest test) {
		Response response = userSpogInvoker.uploadPictureByUserID(userID, picturePath);
		spogServer.checkResponseStatus(response, statusCode);
		spogServer.checkErrorCode(response, errorCode);
	}

	/**
	 * @author Zhaoguo.Ma
	 * @param filterName
	 * @param status
	 * @param hypervisorProduct
	 * @param hypervisorType
	 * @param hypervisorName
	 * @param isDefault
	 * @param test
	 * @return
	 */
	public String createHypervisorFilterForLoggedinUser(String filterName, String status, String hypervisorProduct,
			String hypervisorType, String hypervisorName, String isDefault, ExtentTest test) {

		Map<String, Object> hypervisorFilterInfo = jp.composeHypervisorFilterInfo(filterName, status, hypervisorProduct,
				hypervisorType, hypervisorName, isDefault);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!hypervisorFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.hypervisor_filter.toString(),
					hypervisorFilterInfo.get("filter_name").toString(), "none", "none",
					Boolean.valueOf(isDefault),(HashMap<String, Object>) hypervisorFilterInfo);
		}

		Response response = spogServer.createFilters(userSpogInvoker.getToken(), filter_info, "", test);
		/*Response response = userSpogInvoker.createHypervisorFilterForLoggedinUser(filterInfo);*/
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);

		// TODO: add verification
		verifyHypervisorfilter(response, filterName, status, hypervisorProduct, hypervisorType, hypervisorName,
				isDefault);

		String filterID = response.then().extract().path("data.filter_id");
		return filterID;
	}
	
	public Response createHypervisorFilterForLoggedinUser_audit(String filterName, String status, String hypervisorProduct, 
			String hypervisorType, String hypervisorName, String isDefault, ExtentTest test) {
		
		Map<String, Object> hypervisorFilterInfo = jp.composeHypervisorFilterInfo(filterName, status, hypervisorProduct, hypervisorType, hypervisorName, isDefault);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!hypervisorFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.hypervisor_filter.toString(),
					hypervisorFilterInfo.get("filter_name").toString(), "none", "none",
					Boolean.valueOf(isDefault),(HashMap<String, Object>) hypervisorFilterInfo);
		}

		Response response = spogServer.createFilters(userSpogInvoker.getToken(), filter_info, "", test);
		/*Response response = userSpogInvoker.createHypervisorFilterForLoggedinUser(filterInfo);*/
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		
		//TODO: add verification
		verifyHypervisorfilter(response, filterName, status, hypervisorProduct, hypervisorType, hypervisorName, isDefault);
		
		//String filterID = response.then().extract().path("data.filter_id");
		return response;
	}
	
	public Response createHypervisorFilterForSpecificUser_audit(String userID, String filterName, String status,
			String hypervisorProduct, String hypervisorType, String hypervisorName, String isDefault, ExtentTest test) {

		Map<String, Object> hypervisorFilterInfo = jp.composeHypervisorFilterInfo(filterName, status, hypervisorProduct,
				hypervisorType, hypervisorName, isDefault);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!hypervisorFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.hypervisor_filter.toString(),
					hypervisorFilterInfo.get("filter_name").toString(), userID, "none",
					Boolean.valueOf(isDefault),(HashMap<String, Object>) hypervisorFilterInfo);
		}

		Response response = spogServer.createFilters(userSpogInvoker.getToken(), filter_info, "", test);
	/*	Response response = userSpogInvoker.createHypervisorFilterForUser(userID, filterInfo);*/
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);

		// TODO: add verification
		verifyHypervisorfilter(response, filterName, status, hypervisorProduct, hypervisorType, hypervisorName, isDefault);

		//String filterID = response.then().extract().path("data.filter_id");
		return response;
	}
	

	public void verifyHypervisorfilter(Response response, String filterName, String status, String hypervisorProduct,
			String hypervisorType, String hypervisorName, String isDefault) {
		response.then().body("data.filter_name", equalTo(filterName));

		ArrayList<String> responseStatus = response.then().extract().path("data.status");
		spogServer.assertFilterItem(status, responseStatus);

		ArrayList<String> responseHypervisorProduct = response.then().extract().path("data.hypervisor_product");
		spogServer.assertFilterItem(hypervisorProduct, responseHypervisorProduct);

		ArrayList<String> responseHypervisorType = response.then().extract().path("data.hypervisor_type");
		spogServer.assertFilterItem(hypervisorType, responseHypervisorType);

		if ("true".equalsIgnoreCase(isDefault)) {
			response.then().body("data.is_default", equalTo(true));
		} else {
			response.then().body("data.is_default", equalTo(false));
		}

		if (!(hypervisorName == null || hypervisorName == "" || hypervisorName.equalsIgnoreCase("none"))) {
			response.then().body("data.hypervisor_name", equalTo(hypervisorName));
		}
	}

	/**
	 * @author Zhaoguo.Ma
	 * @param filterName
	 * @param status
	 * @param hypervisorProduct
	 * @param hypervisorType
	 * @param hypervisorName
	 * @param isDefault
	 * @param statusCode
	 * @param expectedErrorCode
	 * @param test
	 */
	public void createHypervisorFilterForLoggedinUserWithCodeCheck(String filterName, String status,
			String hypervisorProduct, String hypervisorType, String hypervisorName, String isDefault, int statusCode,
			String expectedErrorCode, ExtentTest test) {

		Map<String, Object> hypervisorFilterInfo = jp.composeHypervisorFilterInfo(filterName, status, hypervisorProduct,
				hypervisorType, hypervisorName, isDefault);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!hypervisorFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.hypervisor_filter.toString(),
					hypervisorFilterInfo.get("filter_name").toString(), "none", "none",
					Boolean.valueOf(isDefault),(HashMap<String, Object>) hypervisorFilterInfo);
		}

		Response response = spogServer.createFilters(userSpogInvoker.getToken(), filter_info, "", test);
		
		/*Response response = userSpogInvoker.createHypervisorFilterForLoggedinUser(filterInfo);*/
		spogServer.checkResponseStatus(response, statusCode);
		spogServer.checkErrorCode(response, expectedErrorCode);
	}

	/**
	 * @author Zhaoguo.Ma
	 * @param userID
	 * @param filterName
	 * @param status
	 * @param hypervisorProduct
	 * @param hypervisorType
	 * @param hypervisorName
	 * @param isDefault
	 * @param test
	 * @return
	 */
	public String createHypervisorFilterForSpecificUser(String userID, String filterName, String status,
			String hypervisorProduct, String hypervisorType, String hypervisorName, String isDefault, ExtentTest test) {

		Map<String, Object> hypervisorFilterInfo = jp.composeHypervisorFilterInfo(filterName, status, hypervisorProduct,
				hypervisorType, hypervisorName, isDefault);
		/*Response response = userSpogInvoker.createHypervisorFilterForUser(userID, filterInfo);*/
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!hypervisorFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.hypervisor_filter.toString(),
					hypervisorFilterInfo.get("filter_name").toString(),userID, "none",
					Boolean.valueOf(isDefault),(HashMap<String, Object>) hypervisorFilterInfo);
		}

		Response response = spogServer.createFilters(userSpogInvoker.getToken(), filter_info, "", test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);

		// TODO: add verification
		verifyHypervisorfilter(response, filterName, status, hypervisorProduct, hypervisorType, hypervisorName,
				isDefault);

		String filterID = response.then().extract().path("data.filter_id");
		return filterID;
	}

	public void createHypervisorFilterForSpecificUserWithCodeCheck(String userID, String filterName, String status,
			String hypervisorProduct, String hypervisorType, String hypervisorName, String isDefault, int statusCode,
			String expectedErrorCode, ExtentTest test) {

		Map<String, Object> hypervisorFilterInfo = jp.composeHypervisorFilterInfo(filterName, status, hypervisorProduct,
				hypervisorType, hypervisorName, isDefault);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!hypervisorFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.hypervisor_filter.toString(),
					hypervisorFilterInfo.get("filter_name").toString(),userID, "none",
					Boolean.valueOf(isDefault),(HashMap<String, Object>) hypervisorFilterInfo);
		}

		Response response = spogServer.createFilters(userSpogInvoker.getToken(), filter_info, "", test);
		/*Response response = userSpogInvoker.createHypervisorFilterForUser(userID, filterInfo);*/
		spogServer.checkResponseStatus(response, statusCode);
		spogServer.checkErrorCode(response, expectedErrorCode);
	}

	/**
	 * @author Zhaoguo.Ma
	 * @param filterID
	 * @param filterName
	 * @param status
	 * @param hypervisorProduct
	 * @param hypervisorType
	 * @param hypervisorName
	 * @param isDefault
	 * @param test
	 * @return Response
	 */
	public Response updateHypervisorFilterForLoggedinUser(String filterID, String filterName, String status,
			String hypervisorProduct, String hypervisorType, String hypervisorName, String isDefault, ExtentTest test) {

		Map<String, Object> hypervisorFilterInfo = jp.composeHypervisorFilterInfo(filterName, status, hypervisorProduct,
				hypervisorType, hypervisorName, isDefault);
	/*	Response responseOri = userSpogInvoker.getHypervisorFilterForLoggedinUser(filterID);*/
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!hypervisorFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.hypervisor_filter.toString(),
					hypervisorFilterInfo.get("filter_name").toString(), "none", "none",
					Boolean.valueOf(isDefault),(HashMap<String, Object>) hypervisorFilterInfo);
		}

		Response responseOri =spogServer.getFiltersById(userSpogInvoker.getToken(), filterID, filterType.hypervisor_filter.toString(), "none", "none", test);
		String oriFiltername = responseOri.then().extract().path("data.filter_name");

		Response response = spogServer.updateFilterById(userSpogInvoker.getToken(), filterID, "none", filter_info, "", test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);

		// TODO: add verification
		if (filterName == null || filterName == "" || filterName.equalsIgnoreCase("none")) {
			filterName = oriFiltername;
		}


		verifyHypervisorfilter(response, filterName, status, hypervisorProduct, hypervisorType, hypervisorName, isDefault);
		return response;

	}

	public void updateHypervisorFilterForLoggedinUserWithCodeCheck(String filterID, String filterName, String status,
			String hypervisorProduct, String hypervisorType, String hypervisorName, String isDefault, int statusCode,
			String expectedErrorCode, ExtentTest test) {

		Map<String, Object> hypervisorFilterInfo = jp.composeHypervisorFilterInfo(filterName, status, hypervisorProduct,
				hypervisorType, hypervisorName, isDefault);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!hypervisorFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.hypervisor_filter.toString(),
					hypervisorFilterInfo.get("filter_name").toString(), "none", "none",
					Boolean.valueOf(isDefault),(HashMap<String, Object>) hypervisorFilterInfo);
		}

		Response response = spogServer.updateFilterById(userSpogInvoker.getToken(), filterID, "none", filter_info, "", test);
		/*Response response = userSpogInvoker.updateHypervisorFilterForLoggedinUser(filterID, filterInfo);*/
		spogServer.checkResponseStatus(response, statusCode);
		spogServer.checkErrorCode(response, expectedErrorCode);

	}

	/**
	 * @author Zhaoguo.Ma
	 * @param userID
	 * @param filterID
	 * @param filterName
	 * @param status
	 * @param hypervisorProduct
	 * @param hypervisorType
	 * @param hypervisorName
	 * @param isDefault
	 * @param test
	 */
	public void updateHypervisorFilterForSpecificUser(String userID, String filterID, String filterName, String status,
			String hypervisorProduct, String hypervisorType, String hypervisorName, String isDefault, ExtentTest test) {

		Map<String, Object> hypervisorFilterInfo = jp.composeHypervisorFilterInfo(filterName, status, hypervisorProduct,
				hypervisorType, hypervisorName, isDefault);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!hypervisorFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.hypervisor_filter.toString(),
					hypervisorFilterInfo.get("filter_name").toString(), userID, "none",
					Boolean.valueOf(isDefault),(HashMap<String, Object>) hypervisorFilterInfo);
		}
		Response response = spogServer.updateFilterById(userSpogInvoker.getToken(), filterID, userID, filter_info, "", test);
	/*	Response response = userSpogInvoker.updateHypervisorFilterForSpecificUser(userID, filterID, filterInfo);*/
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);

		// TODO: add verification
		Response responseOri = userSpogInvoker.getHypervisorFilterForSpecificUser(userID, filterID);
		String oriFiltername = responseOri.then().extract().path("data.filter_name");

		if (filterName == null || filterName == "" || filterName.equalsIgnoreCase("none")) {
			filterName = oriFiltername;
		}

		verifyHypervisorfilter(response, filterName, status, hypervisorProduct, hypervisorType, hypervisorName,
				isDefault);

	}
	public Response updateHypervisorFilterForSpecificUser_audit(String userID, String filterID, String filterName, String status,
			String hypervisorProduct, String hypervisorType, String hypervisorName, String isDefault, ExtentTest test) {

		Map<String, Object> hypervisorFilterInfo = jp.composeHypervisorFilterInfo(filterName, status, hypervisorProduct,
				hypervisorType, hypervisorName, isDefault);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!hypervisorFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.hypervisor_filter.toString(),
					hypervisorFilterInfo.get("filter_name").toString(), userID, "none",
					Boolean.valueOf(isDefault),(HashMap<String, Object>) hypervisorFilterInfo);
		}
		Response response = spogServer.updateFilterById(userSpogInvoker.getToken(), filterID, userID, filter_info, "", test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);

		// TODO: add verification
		Response responseOri =spogServer.getFiltersById(userSpogInvoker.getToken(), filterID, filterType.hypervisor_filter.toString(), userID, "none", test);
		String oriFiltername = responseOri.then().extract().path("data.filter_name");

		if (filterName == null || filterName == "" || filterName.equalsIgnoreCase("none")) {
			filterName = oriFiltername;
		}

		verifyHypervisorfilter(response, filterName, status, hypervisorProduct, hypervisorType, hypervisorName,
				isDefault);

		return responseOri;
	}

	public void updateHypervisorFilterForSpecificUserWithCodeCheck(String userID, String filterID, String filterName,
			String status, String hypervisorProduct, String hypervisorType, String hypervisorName, String isDefault,
			int statusCode, String expectedErrorCode, ExtentTest test) {

		Map<String, Object> hypervisorFilterInfo = jp.composeHypervisorFilterInfo(filterName, status, hypervisorProduct,
				hypervisorType, hypervisorName, isDefault);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!hypervisorFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.hypervisor_filter.toString(),
					hypervisorFilterInfo.get("filter_name").toString(), userID, "none",
					Boolean.valueOf(isDefault),(HashMap<String, Object>) hypervisorFilterInfo);
		}
		Response response = spogServer.updateFilterById(userSpogInvoker.getToken(), filterID, userID, filter_info, "", test);
		/*Response response = userSpogInvoker.updateHypervisorFilterForSpecificUser(userID, filterID, filterInfo);*/
		spogServer.checkResponseStatus(response, statusCode);
		spogServer.checkErrorCode(response, expectedErrorCode);

	}

	/**
	 * @author Zhaoguo.Ma
	 * @param filterID
	 * @param test
	 */
	public void deleteHypervisorFilterForLoggedinUser(String filterID, ExtentTest test) {

		/*Response response = userSpogInvoker.deleteHypervisorFilterForLoggedinUser(filterID);*/
		Response response = spogServer.deleteFiltersByID(userSpogInvoker.getToken(), filterID, "none", test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);

		// TODO: add verification

	}

	public void deleteHypervisorFilterForLoggedinUserWithCodeCheck(String filterID, int statusCode,
			String expectedErrorCode, ExtentTest test) {

	/*	Response response = userSpogInvoker.deleteHypervisorFilterForLoggedinUser(filterID);*/
		Response response = spogServer.deleteFiltersByID(userSpogInvoker.getToken(), filterID, "none", test);
		spogServer.checkResponseStatus(response, statusCode);
		spogServer.checkErrorCode(response, expectedErrorCode);
		// TODO: add verification

	}

	/**
	 * @author Zhaoguo.Ma
	 * @param userID
	 * @param filterID
	 * @param test
	 */
	public void deleteHypervisorFilterForSpecificUser(String userID, String filterID, ExtentTest test) {

	/*	Response response = userSpogInvoker.deleteHypervisorFilterForSpecificUser(userID, filterID);*/
		Response response = spogServer.deleteFiltersByID(userSpogInvoker.getToken(), filterID, userID, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);

		// TODO: add verification

	}

	public void deleteHypervisorFilterForSpecificUserWithCodeCheck(String userID, String filterID, int statusCode,
			String expectedErrorCode, ExtentTest test) {

		/*Response response = userSpogInvoker.deleteHypervisorFilterForSpecificUser(userID, filterID);*/
		Response response = spogServer.deleteFiltersByID(userSpogInvoker.getToken(), filterID, userID, test);
		spogServer.checkResponseStatus(response, statusCode);
		spogServer.checkErrorCode(response, expectedErrorCode);
		// TODO: add verification

	}

	/**
	 * @author Zhaoguo.Ma
	 * @param filterID
	 * @param test
	 * @return
	 */
	public Response getHypervisorFilterForLoggedinUser(String filterID, ExtentTest test) {

		/*Response response = userSpogInvoker.getHypervisorFilterForLoggedinUser(filterID);*/
		Response response = spogServer.getFiltersById(userSpogInvoker.getToken(), filterID, filterType.hypervisor_filter.toString(), "none", "none", test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);

		// TODO: add verification

		return response;
	}

	public void getHypervisorFilterForLoggedinUserWithCodeCheck(String filterID, int statusCode,
			String expectedErrorCode, ExtentTest test) {

		/*Response response = userSpogInvoker.getHypervisorFilterForLoggedinUser(filterID);*/
		Response response = spogServer.getFiltersById(userSpogInvoker.getToken(), filterID, filterType.hypervisor_filter.toString(), "none", "none", test);
		spogServer.checkResponseStatus(response, statusCode);
		spogServer.checkErrorCode(response, expectedErrorCode);

	}

	/**
	 * @author Zhaoguo.Ma
	 * @param userID
	 * @param filterID
	 * @param test
	 * @return
	 */
	public Response getHypervisorFilterForSpecificUser(String userID, String filterID, ExtentTest test) {

		/*Response response = userSpogInvoker.getHypervisorFilterForSpecificUser(userID, filterID);*/
		Response response = spogServer.getFiltersById(userSpogInvoker.getToken(), filterID, filterType.hypervisor_filter.toString(), userID, "none", test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);

		// TODO: add verification

		return response;
	}

	public void getHypervisorFilterForSpecificUserWithCodeCheck(String userID, String filterID, int statusCode,
			String expectedErrorCode, ExtentTest test) {

		/*Response response = userSpogInvoker.getHypervisorFilterForSpecificUser(userID, filterID);*/
		Response response = spogServer.getFiltersById(userSpogInvoker.getToken(), filterID, filterType.hypervisor_filter.toString(), userID, "none", test);
		spogServer.checkResponseStatus(response, statusCode);
		spogServer.checkErrorCode(response, expectedErrorCode);
		// TODO: add verification

	}

	/**
	 * @author Zhaoguo.Ma
	 * @param test
	 * @return
	 */
	public Response getHypervisorFiltersForLoggedinUser(ExtentTest test) {

	/*	Response response = userSpogInvoker.getHypervisorFiltersForLoggedinUser();*/
		Response response = spogServer.getFilters(userSpogInvoker.getToken(),"none", filterType.hypervisor_filter.toString(), "",test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);

		// TODO: add verification

		return response;
	}

	public Response getHypervisorFiltersForLoggedinUser(String filterStr, String sortStr, int pageNumber, int pageSize,
			ExtentTest test) {

		String extentURL = spogServer.getUrl4FilterSortPaging(filterStr, sortStr, pageNumber, pageSize, test);
		/*Response response = userSpogInvoker.getHypervisorFiltersForLoggedinUser(extentURL);*/
		Response response = spogServer.getFilters(userSpogInvoker.getToken(),"none", filterType.hypervisor_filter.toString(), extentURL,test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);

		// TODO: add verification

		return response;
	}

	public void getHypervisorFiltersForLoggedinUserWithCodeCheck(int statusCode, String expectedErrorCode,
			ExtentTest test) {

	/*	Response response = userSpogInvoker.getHypervisorFiltersForLoggedinUser();*/
		Response response = spogServer.getFilters(userSpogInvoker.getToken(),"none", filterType.hypervisor_filter.toString(), "none",test);
		spogServer.checkResponseStatus(response, statusCode);
		spogServer.checkErrorCode(response, expectedErrorCode);
	}

	/**
	 * @author Zhaoguo.Ma
	 * @param userID
	 * @param test
	 * @return
	 */
	public Response getHypervisorFiltersForSpecificUser(String userID, ExtentTest test) {

		/*Response response = userSpogInvoker.getHypervisorFiltersForSpecificUser(userID);*/
		Response response = spogServer.getFilters(userSpogInvoker.getToken(),userID, filterType.hypervisor_filter.toString(), "",test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);

		// TODO: add verification

		return response;
	}

	public Response getHypervisorFiltersForSpecificUser(String userID, String filterStr, String sortStr, int pageNumber,
			int pageSize, ExtentTest test) {

		String extentURL = spogServer.getUrl4FilterSortPaging(filterStr, sortStr, pageNumber, pageSize, test);
		Response response = spogServer.getFilters(userSpogInvoker.getToken(),userID, filterType.hypervisor_filter.toString(), extentURL,test);
	/*	Response response = userSpogInvoker.getHypervisorFiltersForSpecificUser(userID, extentURL);*/
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);

		// TODO: add verification

		return response;
	}

	public void getHypervisorFiltersForSpecificUserWithCodeCheck(String userID, int statusCode,
			String expectedErrorCode, ExtentTest test) {

		/*Response response = userSpogInvoker.getHypervisorFiltersForSpecificUser(userID);*/
		Response response = spogServer.getFilters(userSpogInvoker.getToken(),userID, filterType.hypervisor_filter.toString(), "none",test);
		spogServer.checkResponseStatus(response, statusCode);
		spogServer.checkErrorCode(response, expectedErrorCode);
	}

	public Response getSystemHypervisorfilters(ExtentTest test) {
		Response response = spogServer.getFilters(userSpogInvoker.getToken(), "none", filterType.hypervisor_filter_global.toString(), "none",test);
		/*Response response = userSpogInvoker.getSystemHypervisorfilters();*/
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		return response;
	}

	public Response getSystemHypervisorfiltersWithErrorCheck(int statusCode, String expectedErrorCode,
			ExtentTest test) {
		/*Response response = userSpogInvoker.getSystemHypervisorfilters();*/
		Response response = spogServer.getFilters(userSpogInvoker.getToken(), "none", filterType.hypervisor_filter_global.toString(), "none",test);
		spogServer.checkResponseStatus(response, statusCode);
		spogServer.checkErrorCode(response, expectedErrorCode);
		return response;
	}

	/**
	 * Create the user filter for specified user and return the response
	 * 
	 * @author Kiran.Sripada
	 * @param userID
	 * @param filter_name
	 * @param first_name
	 * @param user_is_blocked
	 * @param user_status
	 * @param last_name
	 * @param email
	 * @param role_id
	 * @param is_default
	 * @param token
	 * @param test
	 * @return response
	 */
	public Response createUserFilterForSpecificUser(String user_Id, String filter_name, String search_string,
			String user_is_blocked, String user_status, String role_id,
			String is_default, String token, ExtentTest test) {

		test.log(LogStatus.INFO, "Compose user filter");
		HashMap<String, Object> userFilterInfo = jp.composeUserFilterInfo(filter_name, search_string, user_is_blocked,
				user_status, role_id, is_default);
		
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!userFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.user_filter.toString(),
					filter_name, user_Id, "none",
					Boolean.valueOf(is_default), userFilterInfo);
		}
		
		test.log(LogStatus.INFO, "Call the Rest API to create user filters");
		Response response = spogServer.createFilters(token, filter_info, "", test);

	/*	test.log(LogStatus.INFO, "Call the Rest API to create user filters");
		Response response = userSpogInvoker.createUserFilterForUser(user_Id, userFilterInfo, token);*/

		return response;
	}

	/**
	 * Create user filter for logged in user and return the response
	 * 
	 * @author Kiran.Sripada
	 * @param userID
	 * @param filter_name
	 * @param first_name
	 * @param user_is_blocked
	 * @param user_status
	 * @param last_name
	 * @param email
	 * @param role_id
	 * @param is_default
	 * @param token
	 * @param test
	 * @return response
	 */
	public Response createUserFilterForLoggedInUser(String filter_name, String search_string, String user_is_blocked,
			String user_status, String role_id, String is_default, ExtentTest test) {

		test.log(LogStatus.INFO, "Compose user filter");
		HashMap<String, Object> userFilterInfo = jp.composeUserFilterInfo(filter_name, search_string, user_is_blocked,
				user_status, role_id, is_default);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!userFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.user_filter.toString(),
					filter_name, "none", "none",
					Boolean.valueOf(is_default), userFilterInfo);
		}
		
		test.log(LogStatus.INFO, "Call the Rest API to create user filters");
		Response response = spogServer.createFilters(userSpogInvoker.getToken(), filter_info, "", test);
		return response;
	}

	/**
	 * update the specified user filter and return the response
	 * 
	 * @author Kiran.Sripada
	 * @param user_Id
	 * @param filter_Id
	 * @param filter_name
	 * @param first_name
	 * @param user_is_blocked
	 * @param user_status
	 * @param last_name
	 * @param email
	 * @param role_id
	 * @param is_default
	 * @param token
	 * @param test
	 */
	public Response updateUserFilterForSpecificUser(String user_Id, String filter_Id, String filter_name,
			String search_string, String user_is_blocked, String user_status,
			String role_id, String is_default, String token, ExtentTest test) {

		test.log(LogStatus.INFO, "Compose user filter");
		HashMap<String, Object> userFilterInfo = jp.composeUserFilterInfo(filter_name, search_string, user_is_blocked,
				user_status, role_id, is_default);
		spogServer.setUUID(filter_Id);
		setUUID(user_Id, filter_Id);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!userFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.user_filter.toString(),
					filter_name, user_Id, "none",
					Boolean.valueOf(is_default), userFilterInfo);
		}
		test.log(LogStatus.INFO, "Call the Rest API to update user filters");
		/*Response response = userSpogInvoker.updateUserFilterForUser(user_Id, filter_Id, userFilterInfo, token);*/
		Response response=spogServer.updateFilterById(token, filter_Id, user_Id, filter_info, "", test);

		return response;
	}

	/**
	 * update the logged in user filter and return the response
	 * 
	 * @author Kiran.Sripada
	 * @param userID
	 * @param filter_name
	 * @param first_name
	 * @param user_is_blocked
	 * @param user_status
	 * @param last_name
	 * @param email
	 * @param role_id
	 * @param is_default
	 * @param token
	 * @param test
	 * @return response
	 */
	public Response updateUserFilterForLoggedInUser(String filter_Id, String filter_name, String search_string,
			String user_is_blocked, String user_status, String role_id,
			String is_default, ExtentTest test) {

		test.log(LogStatus.INFO, "Compose user filter");
		HashMap<String, Object> userFilterInfo = jp.composeUserFilterInfo(filter_name, search_string, user_is_blocked,
				user_status, role_id, is_default);
		setUUID("", filter_Id);

		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!userFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.user_filter.toString(),
					filter_name, "none", "none",
					Boolean.valueOf(is_default), userFilterInfo);
		}
		test.log(LogStatus.INFO, "Call the Rest API to update user filters");
		/*Response response = userSpogInvoker.updateUserFilterForLoggedInUser(filter_Id, userFilterInfo);*/
		Response response =spogServer.updateFilterById(userSpogInvoker.getToken(), filter_Id, "none", filter_info, "", test);
		return response;
	}

	/**
	 * Get the user filter for specified user and return the response
	 * 
	 * @author Kiran.Sripada
	 * @param user_Id
	 * @param filter_Id
	 * @param token
	 * @return response
	 */
	public Response getUserFilterForSpecificUser(String user_Id, String filter_Id, String token, ExtentTest test) {
		// test.log(LogStatus.INFO, "Call the Rest API to get user filter for specified
		// user");
	/*	Response response = userSpogInvoker.getUserFilterForSpecifiedUser(user_Id, filter_Id, token);*/
		Response response =spogServer.getFiltersById(token, filter_Id, filterType.user_filter.toString(), user_Id, "none",test);
		setUUID(user_Id, filter_Id);
		return response;

	}

	/**
	 * Get the user filter for logged in user and return the response
	 * 
	 * @author Kiran.Sripada
	 * @param filter_Id
	 * @param token
	 * @return response
	 */
	public Response getUserFilterForLoggedInUser(String filter_Id, String token, ExtentTest test) {
		test.log(LogStatus.INFO, "Call the Rest API to get user filter for specified user");
		spogServer.setUUID(filter_Id);
		Response response =spogServer.getFiltersById(token, filter_Id, filterType.user_filter.toString(), "none", "none",test);
		/*Response response = userSpogInvoker.getUserFilterForLoggedInUser(filter_Id, token);*/
		return response;

	}

	/**
	 * Get the user filters for specified user and return the response
	 * 
	 * @author Kiran.Sripada
	 * @param user_Id
	 * @param additionalURL
	 *            can be "" or ?is_default=true or ?is_default=false or
	 *            ?filter_name=<nameofthefilter> or
	 *            ?is_default=true&filter_name=filtername
	 * @param token
	 * @param array
	 *            list of expected_response
	 * @param expectedstatuscode
	 * @param expectederrormessage
	 * @param extenttest
	 * @return void
	 */
	public void getUserFiltersForSpecificUserwithCheck(String user_Id, String additionalURL, String token,
			ArrayList<HashMap<String, Object>> expected_response, int expectedstatuscode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		// test.log(LogStatus.INFO, "Call the Rest API to get user filter for specified
		// user");

		ArrayList<HashMap<String, Object>> actual_response = new ArrayList<>();
		HashMap<String, Object> data_response = new HashMap<>();
		int actual_size = expected_response.size();
		test.log(LogStatus.INFO, "Call the Rest API to get user filter for specified user");
	/*	Response response = userSpogInvoker.getUserFiltersForSpecifiedUser(user_Id, additionalURL, token);*/
		Response response =spogServer.getFilters(token, user_Id, filterType.user_filter.toString(), additionalURL,test);
		test.log(LogStatus.INFO, "Check the response status");
		spogServer.checkResponseStatus(response, expectedstatuscode);
		test.log(LogStatus.PASS, "The response status matched and the status code is " + expectedstatuscode);
		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.INFO, "Perform the validation of the response filters with the expected values");
			validationofuserfilters(response, expected_response, additionalURL, test);

		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		}

	}

	/**
	 * Get the user filters for logged in user and validate
	 * 
	 * @author Kiran.Sripada
	 * @param additionalURL
	 *            can be "" or ?is_default=true or ?is_default=false or
	 *            ?filter_name=<nameofthefilter> or
	 *            ?is_default=true&filter_name=filtername
	 * @param token
	 * @param array
	 *            list of expectedresponse
	 * @param expectedstatuscode
	 * @param expectederrormessage
	 * @param extenttest
	 * @return void
	 */
	public void getUserFiltersForLoggedInUserwithCheck(String additionalURL, String token,
			ArrayList<HashMap<String, Object>> expected_response, int expectedstatuscode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		ArrayList<HashMap<String, Object>> actual_response = new ArrayList<>();
		HashMap<String, Object> data_response = new HashMap<>();
		int actual_size = expected_response.size();
		test.log(LogStatus.INFO, "Call the Rest API to get user filter for logged in user");
	/*	Response response = userSpogInvoker.getUserFiltersForLoggedInUser(additionalURL, token);*/
		Response response =spogServer.getFilters(token, "none", filterType.user_filter.toString(), additionalURL,test);

		test.log(LogStatus.INFO, "Check the response status");
		spogServer.checkResponseStatus(response, expectedstatuscode);
		test.log(LogStatus.PASS, "The response status matched and the status code is " + expectedstatuscode);
		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.INFO, "Perform the validation of the response filters with the expected values");
			validationofuserfilters(response, expected_response, additionalURL, test);

		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		}

	}

	private void validationofuserfilters(Response response, ArrayList<HashMap<String, Object>> expected_response,
			String additionalURL, ExtentTest test) {
		ArrayList<HashMap<String, Object>> actual_response = new ArrayList<>();
		HashMap<String, Object> data_response = new HashMap<>();
		int actual_size = expected_response.size();
		actual_response = response.then().extract().path("data");
		Collections.sort(actual_response, new Comparator<HashMap<String, Object>>() {
			@Override
			public int compare(HashMap<String, Object> o1, HashMap<String, Object> o2) {
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
		String[] expected_data = additionalURL.split("\\&");
		for (int i = 0; i < actual_response.size(); i++) {
			data_response = actual_response.get(i);
			if (additionalURL.contains("is_default") && (additionalURL.contains("true"))
					&& (!additionalURL.contains("filter_name"))) {
				boolean responseIsDefault = (boolean) data_response.get("is_default");
				if (!responseIsDefault) {
					continue;
				} else {
					checkuserFilters(data_response, expected_response.get(actual_size - 1), test);
					break;
				}
			} else if (additionalURL.contains("is_default") && (additionalURL.contains("false"))
					&& (!additionalURL.contains("filter_name"))) {
				boolean responseIsDefault = (boolean) data_response.get("is_default");
				if (responseIsDefault) {
					test.log(LogStatus.FAIL, "Still there are entries with is_default=true");
					assertTrue("Still there are entries with is_default=true", false);
				} else {
					checkuserFilters(data_response, expected_response.get(i), test);
					continue;
				}
			} else if (!additionalURL.contains("is_default") && (additionalURL.contains("filter_name"))) {
				String[] filtername = expected_data[1].split("=");

				String responseIsDefault = data_response.get("filter_name").toString();
				if (responseIsDefault.equals(filtername[1])) {

					for (int j = 0; j < expected_response.size(); j++) {
						if ((expected_response.get(j).get("filter_name").toString()).equals(filtername[1])) {
							checkuserFilters(data_response, expected_response.get(j), test);
							break;
						}
					}
				}
			} else if (additionalURL.contains("is_default") && (additionalURL.contains("filter_name"))) {
				String[] filtername = expected_data[1].split("=");
				String responseIsDefault = data_response.get("filter_name").toString();
				if (responseIsDefault.equals(filtername[1])) {
					for (int j = 0; j < expected_response.size(); j++) {
						if ((expected_response.get(j).get("filter_name").toString()).equals(filtername[1])) {
							checkuserFilters(data_response, expected_response.get(j), test);
							break;
						}
					}
				}
			} else {
				//checkuserFilters(data_response, expected_response.get(i), test);

			}

		}
	}

	private void checkuserFilters(HashMap<String, Object> actual_response, HashMap<String, Object> expected_response,
			ExtentTest test) {
		test.log(LogStatus.INFO, "Compare the filter id");

		spogServer.assertResponseItem(actual_response.get("filter_id").toString(),
				expected_response.get("filter_id").toString(), test);

		test.log(LogStatus.INFO, "Compare the filter name");
		spogServer.assertResponseItem(actual_response.get("filter_name").toString(),
				expected_response.get("filter_name").toString(), test);

		test.log(LogStatus.INFO, "Compare the user_is_blocked");
		if (actual_response.containsKey("blocked")) {
			spogServer.assertResponseItem(actual_response.get("blocked").toString(),
					expected_response.get("blocked").toString(), test);
		} else {
			test.log(LogStatus.PASS, "user has not filtered on param blocked");
		}

		test.log(LogStatus.INFO, "Compare the user_status");
		String user_status = (String) expected_response.get("status");
		if (actual_response.containsKey("status") && user_status != null && user_status != "") {
			ArrayList<String> actual_status = (ArrayList<String>) actual_response.get("status");
			checkRoleId_Status_Users(user_status, actual_status, test);
		} else {
			test.log(LogStatus.PASS, "user has not filtered on param user_status");
		}

		test.log(LogStatus.INFO, "Compare the last_name");
		if (actual_response.containsKey("last_name")) {
			spogServer.assertResponseItem(actual_response.get("last_name"), expected_response.get("last_name"), test);
		} else {
			test.log(LogStatus.PASS, "user has not filtered on param last_name");
		}

		test.log(LogStatus.INFO, "Compare the first_name");
		if (actual_response.containsKey("first_name")) {
			spogServer.assertResponseItem(actual_response.get("first_name"), expected_response.get("first_name"), test);
		} else {
			test.log(LogStatus.PASS, "user has not filtered on param first_name");
		}

		test.log(LogStatus.INFO, "Compare the email");
		if (actual_response.containsKey("email")) {
			spogServer.assertResponseItem(actual_response.get("email"), expected_response.get("email"), test);
		} else {
			test.log(LogStatus.PASS, "user has not filtered on param email");
		}

		test.log(LogStatus.INFO, "Compare the role_id");
		String role_id = (String) expected_response.get("role_id");
		if (actual_response.containsKey("role_id") && role_id != null && role_id != "") {
			ArrayList<String> actual_roles = (ArrayList<String>) actual_response.get("role_id");
			checkRoleId_Status_Users(role_id, actual_roles, test);
		} else {
			test.log(LogStatus.PASS, "user has not filtered on param role_id");
		}

		/*test.log(LogStatus.INFO, "Compare count");
		spogServer.assertResponseItem(actual_response.get("count"), expected_response.get("count"), test);
*/
	}

	public void checkRoleId_Status_Users(String expected, ArrayList<String> actual_data, ExtentTest test) {

		boolean flag = false;
		if (expected.contains(",")) {
			String[] exp = expected.split(",");
			if (exp.length == actual_data.size()) {
				for (int i = 0; i < exp.length; i++) {
					for (int j = 0; j < actual_data.size(); j++) {
						if (exp[i].equals(actual_data.get(j))) {
							spogServer.assertResponseItem(exp[i], actual_data.get(j), test);
							flag = true;
						}
					}
					if (!flag) {
						test.log(LogStatus.FAIL,
								"expected :" + exp[i] + " does not match with any of actual :" + actual_data);
						assertFalse("expected :" + exp[i] + " does not match with any of actual :" + actual_data, true);
						break;
					}
				}
			} else {
				test.log(LogStatus.FAIL, "Actual size is: " + actual_data.size() + " expected size is: " + exp.length);
				assertFalse("Actual size is: " + actual_data.size() + " expected size is: " + exp.length, true);
			}
		} else {
			spogServer.assertResponseItem(expected, actual_data.get(0), test);
		}

	}

	
	public String verifyUserfiletrsWithcheck( Response response, String filterName,int expectedstatuscode, SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
	
		String filter_Id = null;

		spogServer.checkResponseStatus(response, expectedstatuscode, test);
		if (expectedstatuscode == SpogConstants.SUCCESS_POST
				|| expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			HashMap<String, Object> actual_response = response.then().extract().path("data");
			test.log(LogStatus.INFO, "Compare the filtername");
			spogServer.assertResponseItem(filterName, actual_response.get("filter_name"), test);
			filter_Id = (String) actual_response.get("filter_id");
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			if(code.contains("00A00402")){

				message = message.replace("{0}",getUUID().get("filter_id").toString());
				if(getUUID().get("user_id").toString().equalsIgnoreCase("")) {
					spogServer.setToken(userSpogInvoker.getToken());
					message = message.replace("{1}",spogServer.GetLoggedinUser_UserID());
				}else {
					message = message.replace("{1}",getUUID().get("user_id").toString());
				}
				
				
			}
			spogServer.checkErrorCode(response, code);

			test.log(LogStatus.PASS, "The error code matched with the expected " + code);

			spogServer.checkErrorMessage(response, message);

			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
			}	

		return filter_Id;
	}
		
		
	
	public String verifyUserFilters(Response response, String filterName, String search_string, String user_is_blocked,
			String user_status, String role_id, String is_Default, int count,
			int expectedstatuscode, SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		String filter_Id = null;

		spogServer.checkResponseStatus(response, expectedstatuscode, test);
		if (expectedstatuscode == SpogConstants.SUCCESS_POST
				|| expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			HashMap<String, Object> actual_response = response.then().extract().path("data");
			test.log(LogStatus.INFO, "Compare the filtername");
			spogServer.assertResponseItem(filterName, actual_response.get("filter_name"), test);

			test.log(LogStatus.INFO, "Compare the user_is_blocked");
			if (actual_response.containsKey("blocked")) {
				spogServer.assertResponseItem(user_is_blocked, actual_response.get("blocked").toString(), test);
			} else {
				test.log(LogStatus.PASS, "user has not filtered on param blocked");
			}

			test.log(LogStatus.INFO, "Compare the user_status");
			if (actual_response.containsKey("status") && user_status != null && user_status != "") {
				ArrayList<String> actual_status = (ArrayList<String>) actual_response.get("status");
				checkRoleId_Status_Users(user_status, actual_status, test);

			} else {
				test.log(LogStatus.PASS, "user has not filtered on param user_status");
			}

			test.log(LogStatus.INFO, "Compare the search_name");
			if (actual_response.containsKey("search_string")) {
				spogServer.assertResponseItem(search_string, actual_response.get("search_string"), test);
			} else {
				test.log(LogStatus.PASS, "user has not filtered on param first_name");
			}

			test.log(LogStatus.INFO, "Compare the role_id");
			if (actual_response.containsKey("role_id") && role_id != null && role_id != "") {

				ArrayList<String> actual_roles = (ArrayList<String>) actual_response.get("role_id");
				checkRoleId_Status_Users(role_id, actual_roles, test);

			} else {
				test.log(LogStatus.PASS, "user has not filtered on param role_id");
			}

			test.log(LogStatus.INFO, "Compare is_default");
			spogServer.assertResponseItem(is_Default, actual_response.get("is_default").toString(), test);

			test.log(LogStatus.INFO, "Compare count");
			//spogServer.assertResponseItem(count, actual_response.get("count"), test);

			test.log(LogStatus.INFO, "Get the filter Id in case of post only");
			filter_Id = (String) actual_response.get("filter_id");
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			/*if(code.contains("00A00402")){
				message = message.replace("{0}",getUUID().get("filter_id").toString());
				if(getUUID().get("user_id").toString().equalsIgnoreCase("")) {
					spogServer.setToken(userSpogInvoker.getToken());
					message = message.replace("{1}",spogServer.GetLoggedinUser_UserID());
				}else {
					message = message.replace("{1}",getUUID().get("user_id").toString());
				}
			}*/
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		}	

		return filter_Id;
	}
	/**
	 * get system user filters and return response
	 * @author Kiran.Sripada
	 * @param expectedStatusCode
	 * @param ExpectedErrorMessage
	 * @param ExtentTest
	 * 
	 */

	public void getSystemUserfilters(int expectedstatuscode, SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
	/*	Response response = userSpogInvoker.getSystemUserfilters();*/
		Response response = spogServer.getFilters(userSpogInvoker.getToken(), filterType.user_filter_global.toString());
		ArrayList<HashMap> ExpectedHeadContent = new ArrayList<HashMap>();
        
        ArrayList<HashMap<String,Object>> ActualHeadContent = new ArrayList<HashMap<String,Object>>();
                    
      HashMap<String,Object> userfiltercontent = new HashMap<String, Object>();
      userfiltercontent.put("filter_id","808d192f-d2e5-4603-986f-cb6e8e9b47dd");
      userfiltercontent.put("filter_name","Blocked users");
      userfiltercontent.put("user_id","1673da5f-20a2-4959-82ff-d92db4e951d2");
      userfiltercontent.put("organization_id","44348474-db81-4a5d-9ae4-79748caa16f7");
      userfiltercontent.put("is_default", false);
      userfiltercontent.put("count", 1);
      ExpectedHeadContent.add(userfiltercontent);
      HashMap<String, Object> userfiltercontent1 = new HashMap<String, Object>();
      userfiltercontent1.put("filter_id", "808d192f-d2e5-4603-986f-cb6e8e9b47de");
      userfiltercontent1.put("filter_name","verified users");
      userfiltercontent1.put("user_id","1673da5f-20a2-4959-82ff-d92db4e951d2");
      userfiltercontent1.put("organization_id","44348474-db81-4a5d-9ae4-79748caa16f7");
      userfiltercontent1.put("is_default", false);
      userfiltercontent1.put("count", 1);
      ExpectedHeadContent.add(userfiltercontent1);

      if(expectedstatuscode == SpogConstants.SUCCESS_POST||expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE)
      {
              
        ActualHeadContent = response.then().extract().path("data");
        
        int length = ActualHeadContent.size();
        
        for (int i = 0; i < length; i++) {        
           String act=  ActualHeadContent.get(i).get("filter_id").toString(); 
           String exp= ExpectedHeadContent.get(i).get("filter_id").toString(); 
        if((act.equals(exp))
                &&(ActualHeadContent.get(i).get("filter_name").toString().equals(ExpectedHeadContent.get(i).get("filter_name").toString()))
                &&(ActualHeadContent.get(i).get("user_id").toString().equals(ExpectedHeadContent.get(i).get("user_id").toString()))
                &&(ActualHeadContent.get(i).get("organization_id").toString().equals(ExpectedHeadContent.get(i).get("organization_id").toString()))
                &&(ActualHeadContent.get(i).get("is_default").toString().equals(ExpectedHeadContent.get(i).get("is_default").toString()))
                &&(ActualHeadContent.get(i).get("count").toString().equals(ExpectedHeadContent.get(i).get("count").toString())))
              {
                    test.log(LogStatus.PASS, "comparision of system filters succeeded with default filters ");
              }
        else {
                    test.log(LogStatus.FAIL, "comparision of system filters failed with default filters ");
              }
        }
      }else {
    	  String code = ExpectedErrorMessage.getCodeString();
    	  String message = ExpectedErrorMessage.getStatus();
    	  spogServer.checkErrorCode(response,code);

    	  test.log(LogStatus.PASS, "The error code matched with the expected "+code);

    	  spogServer.checkErrorMessage(response,message);

    	  test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
      }


	}

	/*
	 * get users columns and return response
	 * 
	 * @author Rakesh.Chalamala
	 * 
	 * @param expectedStatusCode
	 * 
	 * @param ExtentTest
	 * 
	 * @return response
	 */
	public Response getUsersColumns(int expectedStatusCode, ExtentTest test) {

		Response response = userSpogInvoker.getUserColumns(test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);
		return response;
	}

	/**
	 * This API is used to return the Default User Columns
	 * 
	 * @author Bharadwaj.Ghadiam
	 * @param expectedStatusCode
	 * @param errorInfo
	 * @param test
	 * @return
	 */

	public Response getUserColumnsWithCheck(int expectedStatusCode, SpogMessageCode errorInfo, ExtentTest test) {
		// TODO Auto-generated method stub

		Response response = userSpogInvoker.getUserColumns(test);
		spogServer.checkResponseStatus(response, expectedStatusCode);
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			ArrayList<HashMap<String, Object>> expectedColumnsHeadContents = new ArrayList<HashMap<String, Object>>();
			HashMap<String, Object> columnHeadContent = new HashMap<String, Object>();
			columnHeadContent = composeUserColumnInformation(UserColumnContsants.NAME_COLUMNID,
					UserColumnContsants.NAME_LONG, UserColumnContsants.NAME_SHORT, UserColumnContsants.NAME_KEY,
					UserColumnContsants.NAME_SORT, UserColumnContsants.NAME_FILTER, UserColumnContsants.NAME_VISIBLE,
					UserColumnContsants.NAME_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent);
			columnHeadContent = composeUserColumnInformation(UserColumnContsants.EMAIL_COLUMNID,
					UserColumnContsants.EMAIL_LONG, UserColumnContsants.EMAIL_SHORT, UserColumnContsants.EMAIL_KEY,
					UserColumnContsants.EMAIL_SORT, UserColumnContsants.EMAIL_FILTER, UserColumnContsants.EMAIL_VISIBLE,
					UserColumnContsants.EMAIL_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent);

			columnHeadContent = composeUserColumnInformation(UserColumnContsants.ROLE_COLUMNID,
					UserColumnContsants.ROLE_LONG, UserColumnContsants.ROLE_SHORT, UserColumnContsants.ROLE_KEY,
					UserColumnContsants.ROLE_SORT, UserColumnContsants.ROLE_FILTER, UserColumnContsants.ROLE_VISIBLE,
					UserColumnContsants.ROLE_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent);

			columnHeadContent = composeUserColumnInformation(UserColumnContsants.STATUS_COLUMNID,
					UserColumnContsants.STATUS_LONG, UserColumnContsants.STATUS_SHORT, UserColumnContsants.STATUS_KEY,
					UserColumnContsants.STATUS_SORT, UserColumnContsants.STATUS_FILTER,
					UserColumnContsants.STATUS_VISIBLE, UserColumnContsants.STATUS_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent);

			columnHeadContent = composeUserColumnInformation(UserColumnContsants.AO_COLUMNID,
					UserColumnContsants.AO_LONG, UserColumnContsants.AO_SHORT, UserColumnContsants.AO_KEY,
					UserColumnContsants.AO_SORT, UserColumnContsants.AO_FILTER, UserColumnContsants.AO_VISIBLE,
					UserColumnContsants.AO_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent);

			columnHeadContent = composeUserColumnInformation(UserColumnContsants.LL_COLUMNID,
					UserColumnContsants.LL_LONG, UserColumnContsants.LL_SHORT, UserColumnContsants.LL_KEY,
					UserColumnContsants.LL_SORT, UserColumnContsants.LL_FILTER, UserColumnContsants.LL_VISIBLE,
					UserColumnContsants.LL_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent);

			/*columnHeadContent = composeUserColumnInformation(UserColumnContsants.ORG_COLUMNID,
					UserColumnContsants.ORG_LONG, UserColumnContsants.ORG_SHORT, UserColumnContsants.ORG_KEY,
					UserColumnContsants.ORG_SORT, UserColumnContsants.ORG_FILTER, UserColumnContsants.ORG_VISIBLE,
					UserColumnContsants.ORG_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent);*/

			columnHeadContent = composeUserColumnInformation(UserColumnContsants.IB_COLUMNID,
					UserColumnContsants.IB_LONG, UserColumnContsants.IB_SHORT, UserColumnContsants.IB_KEY,
					UserColumnContsants.IB_SORT, UserColumnContsants.IB_FILTER, UserColumnContsants.IB_VISIBLE,
					UserColumnContsants.IB_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent);
			CompareColumnsHeadContent(response, expectedColumnsHeadContents, test);

		} else {
			String expectedErrorMessage = "", expectedErrorCode = "";
			if (errorInfo.getStatus() != "0010000") {
				expectedErrorMessage = errorInfo.getStatus();
				if (expectedErrorMessage.contains("{0}")) {
					expectedErrorMessage = expectedErrorMessage.replace("{0}", spogServer.getUUId());
				}
				expectedErrorCode = errorInfo.getCodeString();
			}
			spogServer.checkErrorMessage(response, expectedErrorMessage);
			test.log(LogStatus.PASS, "The value of the message is " + expectedErrorMessage);
			spogServer.checkErrorCode(response, expectedErrorCode);
			test.log(LogStatus.PASS, "The value of the code  generated  is " + expectedErrorCode);
			test.log(LogStatus.INFO, "The value of the  response generated actually is :" + response.getStatusCode());
		}

		return response;
	}

	/**
	 * @author Bharadwaj.Ghadiam Validate the response for default user columns
	 * @param response
	 * @param expectedColumnsHeadContents
	 * @param test
	 */
	private void CompareColumnsHeadContent(Response response,
			ArrayList<HashMap<String, Object>> expectedColumnsHeadContents, ExtentTest test) {
		// TODO Auto-generated method stub

		ArrayList<HashMap<String, Object>> columnsHeadContent = new ArrayList<HashMap<String, Object>>();
		columnsHeadContent = response.then().extract().path("data");
		int length = expectedColumnsHeadContents.size();
		if (length != columnsHeadContent.size()) {
			assertTrue("compare columns head content number is " + length, false);
			test.log(LogStatus.FAIL, "compare columns head content number");
		}

		for (int i = 0; i < length; i++) {
			HashMap<String, Object> expectedColumnsHeadContent = expectedColumnsHeadContents.get(i);
			HashMap<String, Object> HeadContent = columnsHeadContent.get(i);

			test.log(LogStatus.INFO, "Validating the Response for column_id");
			spogServer.assertResponseItem(expectedColumnsHeadContent.get("column_id"), HeadContent.get("column_id"),
					test);
			test.log(LogStatus.INFO, "Validating the Response for long_label ");
			spogServer.assertResponseItem(expectedColumnsHeadContent.get("long_label"), HeadContent.get("long_label"),
					test);

			test.log(LogStatus.INFO, "Validating the Response for short_label");
			spogServer.assertResponseItem(expectedColumnsHeadContent.get("short_label"), HeadContent.get("short_label"),
					test);

			test.log(LogStatus.INFO, "Validating the Response for key");
			spogServer.assertResponseItem(expectedColumnsHeadContent.get("key"), HeadContent.get("key"), test);

			test.log(LogStatus.INFO, "Validating the Response for sort");
			spogServer.assertResponseItem(expectedColumnsHeadContent.get("sort"), HeadContent.get("sort"), test);

			test.log(LogStatus.INFO, "Validating the Response for filter");
			spogServer.assertResponseItem(expectedColumnsHeadContent.get("filter"), HeadContent.get("filter"), test);

			test.log(LogStatus.INFO, "Validating the Response for visible");
			spogServer.assertResponseItem(expectedColumnsHeadContent.get("visible"), HeadContent.get("visible"), test);

			test.log(LogStatus.INFO, "Validating the Response for order_id");
			spogServer.assertResponseItem(expectedColumnsHeadContent.get("order_id"), HeadContent.get("order_id"),
					test);

		}

	}

	/**
	 * Compose the expected user column information
	 * 
	 * @author Bharadwaj.Ghadiam
	 * @param column_id
	 * @param long_label
	 * @param short_label
	 * @param key
	 * @param sort
	 * @param filter
	 * @param visible
	 * @param order_id
	 * @return
	 */
	private HashMap<String, Object> composeUserColumnInformation(String column_id, String long_label,
			String short_label, String key, boolean sort, boolean filter, boolean visible, int order_id) {

		// TODO Auto-generated method stub
		HashMap<String, Object> columnHeadContent = new HashMap<String, Object>();
		columnHeadContent.put("long_label", long_label);
		columnHeadContent.put("short_label", short_label);
		columnHeadContent.put("filter", filter);
		columnHeadContent.put("column_id", column_id);
		columnHeadContent.put("key", key);
		columnHeadContent.put("sort", sort);
		columnHeadContent.put("visible", visible);
		columnHeadContent.put("order_id", order_id);

		return columnHeadContent;
	}

	/*
	 * Create User Columns by user id
	 * 
	 * @author Rakesh.Chalamala
	 * 
	 * @param token
	 * 
	 * @param user id
	 * 
	 * @return response
	 * 
	 */

	public Response createUserColumnsByUserId(String token, String user_id,
			ArrayList<HashMap<String, Object>> expectedColumns, ExtentTest test) {
		Map<String, ArrayList<HashMap<String, Object>>> userColumnInfo = jp.jobColumnInfo(expectedColumns);

		Response response = userSpogInvoker.createUserColumnByUserId(token, user_id, userColumnInfo, test);

		return response;
	}

	/*
	 * Update User Columns by user id
	 * 
	 * @author Rakesh.Chalamala
	 * 
	 * @param token
	 * 
	 * @param user id
	 * 
	 * @param expectedresponse
	 * 
	 * @param defaultcolumnresponse
	 * 
	 * @param expectedStatusCode
	 * 
	 * @param ExpectedErrorMessage
	 * 
	 * @return response
	 */
	public Response updateUserColumnsByUserId(String token, String user_id,
			ArrayList<HashMap<String, Object>> expectedresponse,
			ArrayList<HashMap<String, Object>> defaultcolumnresponse, int expectedStatusCode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		Map<String, ArrayList<HashMap<String, Object>>> updateColumnInfo = jp.jobColumnInfo(expectedresponse);
		Response response = userSpogInvoker.updateUserColumnsByUserId(token, user_id, updateColumnInfo, test);
		spogServer.checkResponseStatus(response, expectedStatusCode);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			ArrayList<HashMap<String, Object>> actualresponse = response.then().extract().path("data");

			test.log(LogStatus.INFO, "Sort the actual response by order_id");
			actualresponse = spogServer.sortArrayListbyInt(actualresponse, "order_id");

			test.log(LogStatus.INFO, "Sort the expected response by order_id");
			expectedresponse = spogServer.sortArrayListbyString(expectedresponse, "order_id");

			if (expectedresponse.size() == actualresponse.size()) {
				for (int i = 0; i < actualresponse.size(); i++) {
					for (int j = 0; j < defaultcolumnresponse.size(); j++) {
						if (actualresponse.get(i).get("column_id")
								.equals(defaultcolumnresponse.get(j).get("column_id"))) {
							checkUserColumns(actualresponse.get(i), expectedresponse.get(i),
									defaultcolumnresponse.get(j), test);
							break;
						}
					}

				}
			} else {
				test.log(LogStatus.FAIL, "The expected count did not match the actual count");
				assertTrue("The expected count did not match the actual count", false);
			}
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			if (code.contains("0D00006")) {
				message = message.replace("{0}",
						defaultcolumnresponse.get(defaultcolumnresponse.size() - 1).get("order_id").toString());
			}
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);

		}

		return response;
	}

	/*
	 * Create User Columns for logged in user
	 * 
	 * @author Rakesh.Chalamala
	 * 
	 * @param token
	 * 
	 * @param expectedColumns
	 * 
	 * @ return response
	 * 
	 */
	public Response createUserColumnsForLoggedInUser(String token, ArrayList<HashMap<String, Object>> expectedColumns,
			ExtentTest test) {

		Map<String, ArrayList<HashMap<String, Object>>> userColumnInfo = jp.jobColumnInfo(expectedColumns);
		Response response = userSpogInvoker.createUserColumnsForLoggedInUser(token, userColumnInfo, test);
		return response;
	}

	/*
	 * Update User Columns for logged in user
	 * 
	 * @author Rakesh.Chalamala
	 * 
	 * @param token
	 * 
	 * @param expectedresponse
	 * 
	 * @param defaultcolumnresponse
	 * 
	 * @param expectedStatusCode
	 * 
	 * @param ExpectedErrorMessage
	 * 
	 * @return response
	 */
	public Response updateUserColumnsForLoggedInUser(String token, ArrayList<HashMap<String, Object>> expectedresponse,
			ArrayList<HashMap<String, Object>> defaultcolumnresponse, int expectedStatusCode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		Map<String, ArrayList<HashMap<String, Object>>> updateColumnInfo = jp.jobColumnInfo(expectedresponse);
		Response response = userSpogInvoker.updateUserColumnsForLoggedInUser(token, updateColumnInfo, test);
		spogServer.checkResponseStatus(response, expectedStatusCode);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			ArrayList<HashMap<String, Object>> actualresponse = response.then().extract().path("data");

			test.log(LogStatus.INFO, "Sort the actual response by order_id");
			actualresponse = spogServer.sortArrayListbyInt(actualresponse, "order_id");

			test.log(LogStatus.INFO, "Sort the expected response by order_id");
			expectedresponse = spogServer.sortArrayListbyString(expectedresponse, "order_id");

			if (expectedresponse.size() == actualresponse.size()) {
				for (int i = 0; i < actualresponse.size(); i++) {
					for (int j = 0; j < defaultcolumnresponse.size(); j++) {
						if (actualresponse.get(i).get("column_id")
								.equals(defaultcolumnresponse.get(j).get("column_id"))) {
							checkUserColumns(actualresponse.get(i), expectedresponse.get(i),
									defaultcolumnresponse.get(j), test);
							break;
						}
					}

				}
			} else {
				test.log(LogStatus.FAIL, "The expected count did not match the actual count");
				assertTrue("The expected count did not match the actual count", false);
			}
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			if (code.contains("0D00006")) {
				message = message.replace("{0}",
						defaultcolumnresponse.get(defaultcolumnresponse.size() - 1).get("order_id").toString());
			}
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);

		}
		return response;
	}

	/*
	 * Compare user column content
	 * 
	 * @author Rakesh.Chalamala
	 * 
	 * @param response
	 * 
	 * @param expectedColumnsContent
	 * 
	 * @param defaultColumnsContent
	 * 
	 * @param expectedStatusCode
	 * 
	 * @param ExpectedErrorMessage
	 */
	public void compareUserColumnsContent(Response response, ArrayList<HashMap<String, Object>> expectedColumnsContent,
			ArrayList<HashMap<String, Object>> defaultColumnsContents, int expectedStatusCode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		ArrayList<HashMap<String, Object>> actualColumnsContent = new ArrayList<HashMap<String, Object>>();

		spogServer.checkResponseStatus(response, expectedStatusCode);
		if (expectedStatusCode == SpogConstants.SUCCESS_POST) {
			actualColumnsContent = response.then().extract().path("data");

			test.log(LogStatus.INFO, "Sort the actual response by order_id");
			actualColumnsContent = spogServer.sortArrayListbyInt(actualColumnsContent, "order_id");

			test.log(LogStatus.INFO, "Sort the expected response by order_id");
			expectedColumnsContent = spogServer.sortArrayListbyString(expectedColumnsContent, "order_id");

			if (expectedColumnsContent.size() == actualColumnsContent.size()) {

				for (int i = 0; i < actualColumnsContent.size(); i++) {
					for (int j = 0; j < defaultColumnsContents.size(); j++) {
						if (actualColumnsContent.get(i).get("column_id")
								.equals(defaultColumnsContents.get(j).get("column_id"))) {
							checkUserColumns(actualColumnsContent.get(i), expectedColumnsContent.get(i),
									defaultColumnsContents.get(j), test);
							break;
						}
					}

				}

			} else {
				test.log(LogStatus.FAIL, "The expected count did not match the actual count");
				assertTrue("The expected count did not match the actual count", false);
			}
		}

		else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			if (code.contains("00D00003")) {
				message = message.replace("{0}", expectedColumnsContent.get(0).get("column_id").toString());

			} else if (code.contains("00D00005")) {
				message = message.replace("{0}", expectedColumnsContent.get(0).get("order_id").toString());
			} else if (code.contains("0D00006")) {
				message = message.replace("{0}",
						defaultColumnsContents.get(defaultColumnsContents.size() - 1).get("order_id").toString());
			}
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}

	}

	/*
	 * Check user columns
	 * 
	 * @author Rakesh.Chalamala
	 * 
	 * @param actaulresponse
	 * 
	 * @param expectedresponse
	 * 
	 * @param defaultColumnvalues
	 */
	public void checkUserColumns(HashMap<String, Object> actual_response, HashMap<String, Object> expected_response,
			HashMap<String, Object> defaultcolumnvalues, ExtentTest test) {
		test.log(LogStatus.INFO, "Compare the column id");
		spogServer.assertResponseItem(actual_response.get("column_id").toString(),
				expected_response.get("column_id").toString(), test);

		test.log(LogStatus.INFO, "Compare the order id column ");
		spogServer.assertResponseItem(actual_response.get("order_id").toString(),
				expected_response.get("order_id").toString(), test);

		test.log(LogStatus.INFO, "Compare the visiblity column ");
		if (expected_response.containsKey("visible")) {
			spogServer.assertResponseItem(actual_response.get("visible").toString(),
					expected_response.get("visible").toString(), test);
		} else {
			spogServer.assertResponseItem(actual_response.get("visible").toString(),
					defaultcolumnvalues.get("visible").toString(), test);
		}

		test.log(LogStatus.INFO, "Compare the sort column");
		spogServer.assertResponseItem(actual_response.get("sort").toString(),
				defaultcolumnvalues.get("sort").toString(), test);

		test.log(LogStatus.INFO, "Compare the filter column");
		spogServer.assertResponseItem(actual_response.get("filter").toString(),
				defaultcolumnvalues.get("filter").toString(), test);

	}

	/*
	 * Compose user column
	 * 
	 * @author Rakesh.Chalamala
	 * 
	 * @param columnId
	 * 
	 * @param sort
	 * 
	 * @param filter
	 * 
	 * @param visible
	 * 
	 * @param orderId
	 */
	public HashMap<String, Object> composeUser_Column(String columnId, String sort, String filter, String visible,
			String orderId) {
		// TODO Auto-generated method stub
		HashMap<String, Object> temp = new HashMap<>();
		temp.put("column_id", columnId);
		if (sort == null || sort == "") {
			temp.put("sort", sort);
		} else if (!sort.equals("none")) {
			temp.put("sort", sort);
		}
		if (filter == null || filter == "") {
			temp.put("filter", filter);
		} else if (!filter.equals("none")) {

			temp.put("filter", filter);
		}
		if (visible == null || visible == "") {
			temp.put("visible", visible);
		} else if (!visible.equals("none")) {
			temp.put("visible", visible);
		}

		temp.put("order_id", orderId);

		return temp;
	}

	/*
	 * delete user columns by user id with check
	 * 
	 * @author Rakesh.Chalamala
	 * 
	 * @param user id
	 * 
	 * @param token
	 * 
	 * @param expectedStatusCode
	 * 
	 * @param ExpectedErrorMessage
	 */
	public void deleteUserColumnsforSpecifiedUserwithCheck(String user_Id, String token, int expectedstatuscode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		Response response = userSpogInvoker.deleteUserColumnsByUserId(token, user_Id, test);
		spogServer.checkResponseStatus(response, expectedstatuscode);
		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.PASS, "The user columns are successfully deleted");
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			/*
			 * if(code.contains("00A00302")){ message = message.replace("{0}", filterID);
			 * message= message.replace("{1}", userID); }
			 */
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		}
	}

	/*
	 * delete user columns for logged in user
	 * 
	 * @author Rakesh.Chalamala
	 * 
	 * @param token
	 * 
	 * @param expectedStatusCode
	 * 
	 * @param ExpectedErrorMessage
	 */

	public void deleteUserColumnsForLoggedInUserWithCheck(String token, int expectedstatuscode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		Response response = userSpogInvoker.deleteUserColumnsForLoggedInUser(token, test);
		spogServer.checkResponseStatus(response, expectedstatuscode);
		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.PASS, "The user columns are successfully deleted");
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			/*
			 * if(code.contains("00A00302")){ message = message.replace("{0}", filterID);
			 * message= message.replace("{1}", userID); }
			 */
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		}
	}

	/*
	 * get user columns by user id
	 * 
	 * @author Rakesh.Chalamala
	 * 
	 * @param user id
	 * 
	 * @param token
	 * 
	 * @param expectedresponse
	 * 
	 * @param defaultcolumnresponse
	 * 
	 * @param expectedStatusCode
	 * 
	 * @param ExpectedErrorMessage
	 */

	public void getUserColumnsByUserId(String token, String user_id,
			ArrayList<HashMap<String, Object>> expectedresponse,
			ArrayList<HashMap<String, Object>> defaultcolumnresponse, int expectedStatusCode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		test.log(LogStatus.INFO, "Call the rest API to get the user columns by user id");
		Response response = userSpogInvoker.getUserColumnsByUserId(token, user_id, test);
		spogServer.checkResponseStatus(response, expectedStatusCode);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			ArrayList<HashMap<String,Object>> actualresponse = response.then().extract().path("data");

			test.log(LogStatus.INFO, "Sort the actual response by order_id");
			actualresponse = spogServer.sortArrayListbyString(actualresponse, "order_id");

			test.log(LogStatus.INFO, "Sort the expected response by order_id");
			expectedresponse = spogServer.sortArrayListbyString(expectedresponse, "order_id");

			if(actualresponse.size() == defaultcolumnresponse.size() || actualresponse.size() == defaultcolumnresponse.size()-1) { // size should be same as default

				ArrayList<HashMap<String, Object>> col_respnse = null;
				boolean flag = false;
				
 				for(int i=0;i<actualresponse.size();i++) {
 					flag = false; 					
					for(int j=0;j<expectedresponse.size();j++) {
						if(actualresponse.get(i).get("column_id").equals(expectedresponse.get(j).get("column_id"))) {
							for (int k = 0; k < defaultcolumnresponse.size(); k++) {
								if(expectedresponse.get(j).get("column_id").equals(defaultcolumnresponse.get(k).get("column_id"))) {
									checkUserColumns(actualresponse.get(i),expectedresponse.get(j),defaultcolumnresponse.get(k),test);
									flag = true; // set true as comparison completes
									break;
								}
							}
							break;							
						}
					}	
					if(!flag){ // if comparison not done compare it with default columns
						for (int k = 0; k < defaultcolumnresponse.size(); k++) {
							if(actualresponse.get(i).get("column_id").equals(defaultcolumnresponse.get(k).get("column_id"))) {
								
								col_respnse = new ArrayList<>();
								col_respnse.addAll(defaultcolumnresponse); //adding to new arraylist so that default columns not get disturbed
								col_respnse.get(k).put("visible", false);
								
								checkUserColumns(actualresponse.get(i),col_respnse.get(k),col_respnse.get(k),test);
								break;
							}
						}							
					}
				}
				
			}else {
				test.log(LogStatus.FAIL, "The expected count did not match the actual count");
				assertTrue("The expected count did not match the actual count", false);
			}
		} else {

			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);

		}
	}

	/*
	 * get user columns for logged in user
	 * 
	 * @author Rakesh.Chalamala
	 * 
	 * @param token
	 * 
	 * @param expectedresponse
	 * 
	 * @param defaultcolumnresponse
	 * 
	 * @param expectedStatusCode
	 * 
	 * @param ExpectedErrorMessage
	 */
	public void getUserColumnsForLoggedinUser(String token, ArrayList<HashMap<String, Object>> expectedresponse,
			ArrayList<HashMap<String, Object>> defaultcolumnresponse, int expectedStatusCode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		test.log(LogStatus.INFO, "Call the res API to get the user columns for loggedin user");
		Response response = userSpogInvoker.getUserColumnsForLoggedInUser(token, test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			ArrayList<HashMap<String,Object>> actualresponse = response.then().extract().path("data");

			test.log(LogStatus.INFO, "Sort the actual response by order_id");
			actualresponse = spogServer.sortArrayListbyString(actualresponse, "order_id");

			test.log(LogStatus.INFO, "Sort the expected response by order_id");
			expectedresponse = spogServer.sortArrayListbyString(expectedresponse, "order_id");

			if(actualresponse.size() == defaultcolumnresponse.size() || actualresponse.size() == defaultcolumnresponse.size()-1) { // size should be same as default

				ArrayList<HashMap<String, Object>> col_respnse = null;
				boolean flag = false;
				
 				for(int i=0;i<actualresponse.size();i++) {
 					flag = false; 					
					for(int j=0;j<expectedresponse.size();j++) {
						if(actualresponse.get(i).get("column_id").equals(expectedresponse.get(j).get("column_id"))) {
							for (int k = 0; k < defaultcolumnresponse.size(); k++) {
								if(expectedresponse.get(j).get("column_id").equals(defaultcolumnresponse.get(k).get("column_id"))) {
									checkUserColumns(actualresponse.get(i),expectedresponse.get(j),defaultcolumnresponse.get(k),test);
									flag = true; // set true as comparison completes
									break;
								}
							}
							break;							
						}
					}	
					if(!flag){ // if comparison not done compare it with default columns
						for (int k = 0; k < defaultcolumnresponse.size(); k++) {
							if(actualresponse.get(i).get("column_id").equals(defaultcolumnresponse.get(k).get("column_id"))) {
								
								col_respnse = new ArrayList<>();
								col_respnse.addAll(defaultcolumnresponse); //adding to new arraylist so that default columns not get disturbed
								col_respnse.get(k).put("visible", false);
								
								checkUserColumns(actualresponse.get(i),col_respnse.get(k),col_respnse.get(k),test);
								break;
							}
						}							
					}
				}
				
			}else {
				test.log(LogStatus.FAIL, "The expected count did not match the actual count");
				assertTrue("The expected count did not match the actual count", false);
			}

		} else {

			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);

		}
	}

	/**
	 * Delete user filter by user id with check
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param user id
	 * @param filter id
	 * @param expectedstatuscode
	 * @param expectederrormessage
	 */
	public void deleteUserFilterByUserIdWithCheck(String token, String user_id, String filter_id,
			int expectedStatusCode, SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		Response response = spogServer.deleteFiltersByID(token, filter_id, user_id, test);
		spogServer.checkResponseStatus(response, expectedStatusCode);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.INFO, "User filter is successfully deleted");
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			if (code.contains("00A00402")) {
				message = message.replace("{0}", filter_id);
				message = message.replace("{1}", user_id);
			}
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
	}

	/**
	 * Delete user filter for loggedin user with check
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param user id
	 * @param filter id
	 * @param expectedstatuscode
	 * @param expectederrormessage
	 */
	public void deleteUserFilterForLoggedInUserWithCheck(String token, String filter_id, int expectedStatusCode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
	/*	Response response = userSpogInvoker.deleteUserFilterForLoggedInUser(token, filter_id, test);*/
		Response response = spogServer.deleteFiltersByID(token, filter_id, "none", test);
		spogServer.checkResponseStatus(response, expectedStatusCode);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.INFO, "User filter is successfully deleted");
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			if (code.contains("00A00A02")) {
				spogServer.setToken(token);
				String user_id = spogServer.GetLoggedinUser_UserID();
				message = message.replace("{0}", filter_id);
				message = message.replace("{1}", user_id);
			}
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
	}

	  /**
	   * Call the REST api to resetpassword
	   * @author Eric.Yang
	   * @param Token
	   * @param statuscode
	   * @param test
	   * @return response
	   */
	  public Response resetPasswordWithCheck(String userid, String Token, int statuscode,String expectedErrorCode, ExtentTest test) {

	    Response response = userSpogInvoker.resetPassword(userid, Token, test);
	    
	    spogServer.checkResponseStatus(response, statuscode, test);
	    System.out.println("The response value of POST api/users/:id/resetpassword s :" + response.getBody().asString());
	    return response;
	  }	
	  /**
	   * Call REST Web service to assign account admin for msp account
	   * @ parentId
	   * @ childId
	   * @ userIds
	   * @author yuefen.liu
	   * @return response
	   */
	  public Response assignMspAccountAdmins(String parentId, String childId, String[] userIds, String token) {
		 Map<String, Object> mspAccountAdmins = jp.assignMspAccountAdminsInfo(userIds);
		 Response response = userSpogInvoker.assignAccountAdmins(parentId, childId, mspAccountAdmins, token);
	    return response;
	  }
	  
	  public Response assignMspAccountAdmins(String parentId, String childId, String userIDs, String token) {
        String[] userIds = userIDs.split(";");
	    Map<String, Object> mspAccountAdmins = jp.assignMspAccountAdminsInfo(userIds);
        Response response = userSpogInvoker.assignAccountAdmins(parentId, childId, mspAccountAdmins, token);
       return response;
     }
	  /**
	   * Call REST Web service to assign account admin for msp account and check
	   * @ parentId
	   * @ childId
	   * @ userIds
	   * @author yuefen.liu
	   * @return response
	   */
	  public void assignMspAccountAdminsWithCheck(Response response, String[] expectedUserIds, String token) {
		  
	      spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
	    	  
		  ArrayList<Map<String,String>> list = new ArrayList<>();
		  list = response.then().extract().path("data.users");
		  for (int i=0;i<expectedUserIds.length;i++) {
			if(list.get(i).containsValue(expectedUserIds[i])) {
				 assertTrue("It's correct", true);
			}
		}
	   
	  }
	  
	  /**
	   * Call REST Web service to unassign account admin for msp account
	   * @ parentId
	   * @ childId
	   * @ userIds
	   * @author yuefen.liu
	   * @return response
	   */
	  public Response unAssignMspAccountAdmins(String parentId, String childId, String[] userIds, String token) {
		 Map<String, Object> mspAccountAdmins = jp.assignMspAccountAdminsInfo(userIds);
		 Response response = userSpogInvoker.unAssignAccountAdmins(parentId, childId, mspAccountAdmins, token);
	    return response;
	  }

	  /**
	   * Call REST Web service to get all msp account admins assigned to msp sub org
	   * @ childId
	   * @ userIds
	   * @author yuefen.liu
	   * @return response
	   */
	  public Response getAllMspAccountAdmins(String parentId, String childId, String token) {
		 Response response = userSpogInvoker.getAllMspAccountAdmins(parentId, childId, token);
	    return response;
	  }
	  /**
	   * Call REST Web service to get all msp account admins assigned to msp sub org and check
	   * @ childId
	   * @ userIds
	   * @author yuefen.liu
	   * @return response
	   */
	  public void getAllMspAccountAdminsSuccessWithCheck(String parentId, String childId, String[] expectedUserIds, String token) {
		 Response response = getAllMspAccountAdmins(parentId, childId, token);
		 spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		 
		 ArrayList<Map<String,Object>> list = new ArrayList<>();
		 list = response.then().extract().path("data");
		 
	     for (int i=0;i<expectedUserIds.length;i++) {
	    	 if (list.get(i).containsValue(expectedUserIds[i])) {
	    		 assertTrue("It's correct", true);
	    	 }
	     }
	  }
	  /**
	   * Call REST Web service to get all MSP Sub-Organization assigned to specified MSP Account Admin User
	   * @author yuefen.liu
	   * @return response
	   */ 
	  public Response getAllSubOrgsAssignedToMspAccountAdmin(String user_id, String token) {
		  Response response = userSpogInvoker.getAllSubOrgsAssignedToMspAccountAdmin(user_id, token);
		  return response;
	  }

	/**
	 * @author Zhaoguo.Ma
	 * @param response
	 * @param filter_name
	 * @param policy_name
	 * @param group_id
	 * @param last_backup_status
	 * @param status
	 * @param is_default
	 */
	public void verifyPolicyFilter(Response response, String filter_name, String policy_name, String group_id,
			String last_backup_status, String status, String is_default) {
		response.then().body("data.filter_name", equalTo(filter_name));

		ArrayList<String> response_group_id = response.then().extract().path("data.group_id");
		spogServer.assertFilterItem(group_id, response_group_id);

		ArrayList<String> response_last_backup_status = response.then().extract().path("data.last_job");
		spogServer.assertFilterItem(last_backup_status, response_last_backup_status);

		ArrayList<String> response_status = response.then().extract().path("data.status");
		spogServer.assertFilterItem(status, response_status);

		if ("true".equalsIgnoreCase(is_default)) {
			response.then().body("data.is_default", equalTo(true));
		} else {
			response.then().body("data.is_default", equalTo(false));
		}

		if (!(policy_name == null || policy_name == "" || policy_name.equalsIgnoreCase("none"))) {
			response.then().body("data.policy_name", equalTo(policy_name));
		}
	}

	/**
	 * @author Zhaoguo.Ma
	 * @param user_id
	 * @param filter_name
	 * @param policy_name
	 * @param group_id
	 * @param last_backup_status
	 * @param status
	 * @param is_default
	 * @param test
	 * @return
	 */
	public String createPolicyFilterForSpecificUserWithCheck(String user_id, String filter_name, String policy_name,
			String group_id, String last_backup_status, String status, String is_default, ExtentTest test) {
		HashMap<String, Object> policyInfo = jp.composePolicyFilter(filter_name, policy_name, group_id,
				last_backup_status, status, is_default);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!policyInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.policy_filter.toString(),
					filter_name, user_id, "none",
					Boolean.valueOf(is_default), policyInfo);
		}
		
		Response response = spogServer.createFilters(userSpogInvoker.getToken(), filter_info, "", test);
				
				/*	Response response = userSpogInvoker.createPolicyFilterForUser(user_id, filterInfo);*/
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		String filterID = response.then().extract().path("data.filter_id");

		verifyPolicyFilter(response, filter_name, policy_name, group_id, last_backup_status, status, is_default);
		return filterID;
	}

	public Response createPolicyFilterForSpecificUserWithCheck_audit(String user_id, String filter_name, String policy_name,
			String group_id, String last_backup_status, String status, String is_default, ExtentTest test) {
		HashMap<String, Object> policyInfo = jp.composePolicyFilter(filter_name, policy_name, group_id,
				last_backup_status, status, is_default);
		
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!policyInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.policy_filter.toString(),
					filter_name, user_id, "none",
					Boolean.valueOf(is_default), policyInfo);
		}
		
		Response response = spogServer.createFilters(userSpogInvoker.getToken(), filter_info, "", test); 	

		/*Response response = userSpogInvoker.createPolicyFilterForUser(user_id, filterInfo);*/
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		String filterID = response.then().extract().path("data.filter_id");

		verifyPolicyFilter(response, filter_name, policy_name, group_id, last_backup_status, status, is_default);
		return response;
	}
	/**
	 * @author Zhaoguo.Ma
	 * @param user_id
	 * @param filter_name
	 * @param policy_name
	 * @param group_id
	 * @param last_backup_status
	 * @param status
	 * @param is_default
	 * @param status_code
	 * @param error_code
	 * @param test
	 */
	public void createPolicyFilterForSpecificUserWithErrorCheck(String user_id, String filter_name, String policy_name,
			String group_id, String last_backup_status, String status, String is_default, int status_code,
			String error_code, ExtentTest test) {
		HashMap<String, Object> policyInfo = jp.composePolicyFilter(filter_name, policy_name, group_id,
				last_backup_status, status, is_default);
		
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!policyInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.policy_filter.toString(),
					filter_name, user_id, "none",
					Boolean.valueOf(is_default), policyInfo);
		}
		Response response = spogServer.createFilters(userSpogInvoker.getToken(), filter_info, "", test); 	

	/*	Response response = userSpogInvoker.createPolicyFilterForUser(user_id, filterInfo);*/
		spogServer.checkResponseStatus(response, status_code);
		spogServer.checkErrorCode(response, error_code);

	}
	
	/**
	 * @author Zhaoguo.Ma
	 * @param filter_name
	 * @param policy_name
	 * @param group_id
	 * @param last_backup_status
	 * @param status
	 * @param is_default
	 * @param test
	 * @return
	 */
	public String createPolicyFilterForLoggedinUserWithCheck(String filter_name, String policy_name,
			String group_id, String last_backup_status, String status, String is_default, ExtentTest test) {
		HashMap<String, Object> policyInfo = jp.composePolicyFilter(filter_name, policy_name, group_id,
				last_backup_status, status, is_default);
		
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!policyInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.policy_filter.toString(),
					filter_name, "none", "none",
					Boolean.valueOf(is_default), policyInfo);
		}
		
		Response response = spogServer.createFilters(userSpogInvoker.getToken(), filter_info, "", test); 	

	/*	Response response = userSpogInvoker.createPolicyFilterForLoggedinUser(filterInfo);*/
		String filterID = response.then().extract().path("data.filter_id");
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		verifyPolicyFilter(response, filter_name, policy_name, group_id, last_backup_status, status, is_default);
		return filterID;
	}
	
	/**
	 * @author Kiran.Sripada
	 * @param filter_name
	 * @param policy_name
	 * @param group_id
	 * @param last_backup_status
	 * @param status
	 * @param is_default
	 * @param test
	 * @return
	 */
	public Response createPolicyFilterForLoggedinUser(String filter_name, String policy_name,
			String group_id, String last_backup_status, String status, String is_default, ExtentTest test) {
		HashMap<String, Object> policyInfo = jp.composePolicyFilter(filter_name, policy_name, group_id,
				last_backup_status, status, is_default);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!policyInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.policy_filter.toString(),
					filter_name,  "none", "none",
					Boolean.valueOf(is_default), policyInfo);
		}
		
		Response response = spogServer.createFilters(userSpogInvoker.getToken(), filter_info, "", test); 	

		/*Response response = userSpogInvoker.createPolicyFilterForLoggedinUser(filterInfo);*/
		//String filterID = response.then().extract().path("data.filter_id");
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		verifyPolicyFilter(response, filter_name, policy_name, group_id, last_backup_status, status, is_default);
		return response;
	}

	/**
	 * @author Zhaoguo.Ma
	 * @param filter_name
	 * @param policy_name
	 * @param group_id
	 * @param last_backup_status
	 * @param status
	 * @param is_default
	 * @param status_code
	 * @param error_code
	 * @param test
	 */
	public void createPolicyFilterForLoggedinUserWithErrorCheck(String filter_name, String policy_name,
			String group_id, String last_backup_status, String status, String is_default, int status_code,
			String error_code, ExtentTest test) {
		HashMap<String, Object> policyInfo = jp.composePolicyFilter(filter_name, policy_name, group_id,
				last_backup_status, status, is_default);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!policyInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.policy_filter.toString(),
					filter_name, "none", "none",
					Boolean.valueOf(is_default), policyInfo);
		}
		Response response = spogServer.createFilters(userSpogInvoker.getToken(), filter_info, "", test); 	

		/*Response response = userSpogInvoker.createPolicyFilterForLoggedinUser(filterInfo);*/
		spogServer.checkResponseStatus(response, status_code);
		spogServer.checkErrorCode(response, error_code);

	}
	
	/**
	 * @author Zhaoguo.Ma
	 * @param user_id
	 * @param filter_id
	 * @param filter_name
	 * @param policy_name
	 * @param group_id
	 * @param last_backup_status
	 * @param status
	 * @param is_default
	 * @param test
	 */
	public void updatePolicyFilterForSpecificUserWithCheck(String user_id, String filter_id, String filter_name, String policy_name,
			String group_id, String last_backup_status, String status, String is_default, ExtentTest test) {
		HashMap<String, Object> policyInfo = jp.composePolicyFilter(filter_name, policy_name, group_id,
				last_backup_status, status, is_default);

		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!policyInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.policy_filter.toString(),
					filter_name, user_id, "none",
					Boolean.valueOf(is_default), policyInfo);
		}
		
		test.log(LogStatus.INFO, "Call the Rest API to update user filters");
		Response response = spogServer.updateFilterById(userSpogInvoker.getToken(), filter_id, user_id, filter_info, "", test);
		/*Response response = userSpogInvoker.updatePolicyFilterForUser(user_id, filter_id, filterInfo);*/
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		verifyPolicyFilter(response, filter_name, policy_name, group_id, last_backup_status, status, is_default);
	}
	
	public Response updatePolicyFilterForSpecificUserWithCheck_audit(String user_id, String filter_id, String filter_name, String policy_name,
			String group_id, String last_backup_status, String status, String is_default, ExtentTest test) {
		HashMap<String, Object> policyInfo = jp.composePolicyFilter(filter_name, policy_name, group_id,
				last_backup_status, status, is_default);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!policyInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.policy_filter.toString(),
					filter_name, user_id, "none",
					Boolean.valueOf(is_default), policyInfo);
		}
		
		test.log(LogStatus.INFO, "Call the Rest API to update user filters");
		Response response = spogServer.updateFilterById(userSpogInvoker.getToken(), filter_id, user_id, filter_info, "", test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		verifyPolicyFilter(response, filter_name, policy_name, group_id, last_backup_status, status, is_default);
		return response;
	}
	
	/**
	 * @author Zhaoguo.Ma
	 * @param user_id
	 * @param filter_id
	 * @param filter_name
	 * @param policy_name
	 * @param group_id
	 * @param last_backup_status
	 * @param status
	 * @param is_default
	 * @param status_code
	 * @param error_code
	 * @param test
	 */
	public void updatePolicyFilterForSpecificUserWithErrorCheck(String user_id, String filter_id, String filter_name, String policy_name,
			String group_id, String last_backup_status, String status, String is_default, int status_code, String error_code, ExtentTest test) {
		HashMap<String, Object> policyInfo = jp.composePolicyFilter(filter_name, policy_name, group_id,
				last_backup_status, status, is_default);
		
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!policyInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.policy_filter.toString(),
					filter_name, user_id, "none",
					Boolean.valueOf(is_default), policyInfo);
		}
		
		test.log(LogStatus.INFO, "Call the Rest API to update user filters");
		Response response = spogServer.updateFilterById(userSpogInvoker.getToken(), filter_id, user_id, filter_info, "", test);
	/*	Response response = userSpogInvoker.updatePolicyFilterForUser(user_id, filter_id, filterInfo);*/
		spogServer.checkResponseStatus(response, status_code);
		spogServer.checkErrorCode(response, error_code);
	}
	
	/**
	 * @author Zhaoguo.Ma
	 * @param filter_id
	 * @param filter_name
	 * @param policy_name
	 * @param group_id
	 * @param last_backup_status
	 * @param status
	 * @param is_default
	 * @param test
	 */
	public Response updatePolicyFilterForLoggedinUserWithCheck(String filter_id, String filter_name, String policy_name,
			String group_id, String last_backup_status, String status, String is_default, ExtentTest test) {
		HashMap<String, Object> policyInfo = jp.composePolicyFilter(filter_name, policy_name, group_id,
				last_backup_status, status, is_default);

		/*Response response = userSpogInvoker.updatePolicyFilterForLoggedinUser(filter_id, filterInfo);*/
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!policyInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.policy_filter.toString(),
					filter_name, "none", "none",
					Boolean.valueOf(is_default), policyInfo);
		}
		Response response = spogServer.updateFilterById(userSpogInvoker.getToken(), filter_id, "none", filter_info, "", test);
//		Response response = userSpogInvoker.updatePolicyFilterForUser("none", filter_id, filter_info);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		verifyPolicyFilter(response, filter_name, policy_name, group_id, last_backup_status, status, is_default);
		return response;
	}
	
	/**
	 * @author Zhaoguo.Ma
	 * @param filter_id
	 * @param filter_name
	 * @param policy_name
	 * @param group_id
	 * @param last_backup_status
	 * @param status
	 * @param is_default
	 * @param status_code
	 * @param error_code
	 * @param test
	 */
	public void updatePolicyFilterForLoggedinUserWithErrorCheck(String filter_id, String filter_name, String policy_name,
			String group_id, String last_backup_status, String status, String is_default, int status_code, String error_code, ExtentTest test) {
		HashMap<String, Object> policyInfo = jp.composePolicyFilter(filter_name, policy_name, group_id,
				last_backup_status, status, is_default);

		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!policyInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.policy_filter.toString(),
					filter_name, "none", "none",
					Boolean.valueOf(is_default), policyInfo);
		}
		Response response = spogServer.updateFilterById(userSpogInvoker.getToken(), filter_id, "none", filter_info, "", test);
	/*	Response response = userSpogInvoker.updatePolicyFilterForLoggedinUser(filter_id, filterInfo);*/
		spogServer.checkResponseStatus(response, status_code);
		spogServer.checkErrorCode(response, error_code);
	}
	
	/**
	 * @author Zhaoguo.Ma
	 * @param userID
	 * @param test
	 * @return
	 */
	public Response getPolicyFiltersForSpecificUser(String userID, ExtentTest test) {
		/*Response response = userSpogInvoker.getPolicyFiltersForUser(userID);*/
		Response response=spogServer.getFilters(userSpogInvoker.getToken(), userID, filterType.policy_filter.toString(), "",test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		return response;
	}
	
	/**
	 * @author Zhaoguo.Ma
	 * @param userID
	 * @param status_code
	 * @param error_code
	 * @param test
	 */
	public void getPolicyFiltersForSpecificUserWithErrorCheck(String userID, int status_code, String error_code, ExtentTest test) {
		/*Response response = userSpogInvoker.getPolicyFiltersForUser(userID);*/
		Response response=spogServer.getFilters(userSpogInvoker.getToken(), userID, filterType.policy_filter.toString(), "",test);
		spogServer.checkResponseStatus(response, status_code);
		spogServer.checkErrorCode(response, error_code);
	}
		
	/**
	 * @author Zhaoguo.Ma
	 * @param test
	 * @return
	 */
	public Response getPolicyFiltersForLoggedinUser(ExtentTest test) {
		Response response=spogServer.getFilters(userSpogInvoker.getToken(), "none", filterType.policy_filter.toString(), "",test);
		/*Response response = userSpogInvoker.getPolicyFiltersForLoggedinUser();*/
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		return response;
	}
	
	/**
	 * @author Zhaoguo.Ma
	 * @param status_code
	 * @param error_code
	 * @param test
	 */
	public void getPolicyFiltersForLoggedinUserWithErrorCheck(int status_code, String error_code, ExtentTest test) {
		/*Response response = userSpogInvoker.getPolicyFiltersForLoggedinUser();*/
		Response response=spogServer.getFilters(userSpogInvoker.getToken(), "none", filterType.policy_filter.toString(), "",test);
		spogServer.checkResponseStatus(response, status_code);
		spogServer.checkErrorCode(response, error_code);
	}
	
	/**
	 * @author Zhaoguo.Ma
	 * @param userID
	 * @param filterID
	 * @param test
	 * @return
	 */
	public Response getSpecificPolicyFilterForSpecificUser(String userID, String filterID, ExtentTest test) {
		Response response=spogServer.getFiltersById(userSpogInvoker.getToken(), filterID, filterType.policy_filter.toString(), userID, "none", test);
		/*Response response = userSpogInvoker.getSpecificPolicyFilterForUser(userID, filterID);*/
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		return response;
	}
	
	/**
	 * @author Zhaoguo.Ma
	 * @param userID
	 * @param filterID
	 * @param status_code
	 * @param error_code
	 * @param test
	 */
	public void getSpecificPolicyFilterForSpecificUserWithErrorCheck(String userID, String filterID, int status_code, String error_code, ExtentTest test) {
		Response response=spogServer.getFiltersById(userSpogInvoker.getToken(), filterID, filterType.policy_filter.toString(), userID, "none", test);
	/*	Response response = userSpogInvoker.getSpecificPolicyFilterForUser(userID, filterID);*/
		spogServer.checkResponseStatus(response, status_code);
		spogServer.checkErrorCode(response, error_code);
	}
	
	/**
	 * @author Zhaoguo.Ma
	 * @param filterID
	 * @param test
	 * @return
	 */
	public Response getSpecificPolicyFilterForLoggedinUser(String filterID, ExtentTest test) {
		/*Response response = userSpogInvoker.getSpecificPolicyFilterForLoggedinUser(filterID);*/
		Response response = spogServer.getFiltersById(userSpogInvoker.getToken(), filterID, filterType.policy_filter.toString(), "none", "none", test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		return response;
	} 
	
	/**
	 * @author Zhaoguo.Ma
	 * @param filterID
	 * @param status_code
	 * @param error_code
	 * @param test
	 */
	public void getSpecificPolicyFilterForLoggedinUserWithErrorCheck(String filterID, int status_code, String error_code, ExtentTest test) {
	/*	Response response = userSpogInvoker.getSpecificPolicyFilterForLoggedinUser(filterID);*/
		Response response = spogServer.getFiltersById(userSpogInvoker.getToken(), filterID, filterType.policy_filter.toString(), "none", "none", test);
		spogServer.checkResponseStatus(response, status_code);
		spogServer.checkErrorCode(response, error_code);
	} 

	/**
	 * @author Zhaoguo.Ma
	 * @param test
	 * @return
	 */
	public Response getSystemPolicyFilter(ExtentTest test) {
	/*	Response response = userSpogInvoker.getSystemPolicyFilters();*/
		Response response = spogServer.getFilters(userSpogInvoker.getToken(), filterType.policy_filter_global.toString());
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		return response;
	}
	
	/**
	 * @author Zhaoguo.Ma
	 * @param status_code
	 * @param error_code
	 * @param test
	 */
	public void getSystemPolicyFilterWithErrorCheck(int status_code, String error_code, ExtentTest test) {
		/*Response response = userSpogInvoker.getSystemPolicyFilters();*/
		Response response = spogServer.getFilters(userSpogInvoker.getToken(), filterType.policy_filter_global.toString());
		spogServer.checkResponseStatus(response, status_code);
		spogServer.checkErrorCode(response, error_code);
	}

	/**
	 * @author Zhaoguo.Ma
	 * @param userID
	 * @param filterID
	 * @param test
	 */
	public void deleteSpecificPolicyFilterForSpecificUser(String userID, String filterID, ExtentTest test) {
		/*Response response = userSpogInvoker.deleteSpecificPolicyFilterForUser(userID, filterID);*/
		Response response =spogServer.deleteFiltersByID(userSpogInvoker.getToken(), filterID, userID, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
	}
	
	/**
	 * @author Zhaoguo.Ma
	 * @param userID
	 * @param filterID
	 * @param status_code
	 * @param error_code
	 * @param test
	 */
	public void deleteSpecificPolicyFilterForSpecificUserWithErrorCheck(String userID, String filterID, int status_code, String error_code, ExtentTest test) {
	/*	Response response = userSpogInvoker.deleteSpecificPolicyFilterForUser(userID, filterID);*/
		Response response =spogServer.deleteFiltersByID(userSpogInvoker.getToken(), filterID, userID, test);
		spogServer.checkResponseStatus(response, status_code);
		spogServer.checkErrorCode(response, error_code);
	}
	
	/**
	 * @author Zhaoguo.Ma
	 * @param filterID
	 * @param test
	 */
	public void deleteSpecificPolicyFilterForLoggedinUser(String filterID, ExtentTest test) {
		Response response =spogServer.deleteFiltersByID(userSpogInvoker.getToken(), filterID, "none", test);
		/*Response response = userSpogInvoker.deleteSpecificPolicyFilterForLoggedinUser(filterID);*/
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
	}
	
	/**
	 * @author Zhaoguo.Ma
	 * @param filterID
	 * @param status_code
	 * @param error_code
	 * @param test
	 */
	public void deleteSpecificPolicyFilterForLoggedinUserWithErrorCheck(String filterID, int status_code, String error_code, ExtentTest test) {
	/*	Response response = userSpogInvoker.deleteSpecificPolicyFilterForLoggedinUser(filterID);*/
		Response response =spogServer.deleteFiltersByID(userSpogInvoker.getToken(), filterID, "none", test);
		spogServer.checkResponseStatus(response, status_code);
		spogServer.checkErrorCode(response, error_code);
	}
	
	/**
	 * @author Zhaoguo.Ma
	 * @param filterStr
	 * @param sortStr
	 * @param pageNumber
	 * @param pageSize
	 * @param test
	 * @return
	 */
	public Response getPolicyFiltersForLoggedinUser(String filterStr, String sortStr, int pageNumber, int pageSize,
			ExtentTest test) {

		String extentURL = spogServer.getUrl4FilterSortPaging(filterStr, sortStr, pageNumber, pageSize, test);
		/*Response response = userSpogInvoker.getPolicyFiltersForLoggedinUser(extentURL);*/
		Response response=spogServer.getFilters(userSpogInvoker.getToken(), "none", filterType.policy_filter.toString(), extentURL,test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);

		// TODO: add verification

		return response;
	}
	
	/**
	 * @author Zhaoguo.Ma
	 * @param userID
	 * @param filterStr
	 * @param sortStr
	 * @param pageNumber
	 * @param pageSize
	 * @param test
	 * @return
	 */
	public Response getPolicyFiltersForSpecificUser(String userID, String filterStr, String sortStr, int pageNumber,
			int pageSize, ExtentTest test) {

		
		String extentURL = spogServer.getUrl4FilterSortPaging(filterStr, sortStr, pageNumber, pageSize, test);
/*		Response response = userSpogInvoker.getPolicyFiltersForSpecificUser(userID, extentURL);*/
		Response response=spogServer.getFilters(userSpogInvoker.getToken(), userID, filterType.policy_filter.toString(), extentURL,test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		return response;
	}
	/**
	 * @author shuo.zhang
	 * @param refreshToken
	 * @return
	 */
	  public Response postRefreshToken(String refreshToken){
		  
		  return userSpogInvoker.refreshToken(refreshToken);
	  }
	  
	  /**
	   * @author shuo.zhang
	   * @param refreshToken
	   * @param expectedStatusCode
	   * @param expectedErrorCode
	   * @param test
	   */
	  public void postRefreshTokenWithCheck(String refreshToken, int expectedStatusCode, String expectedErrorCode, ExtentTest test ){
		  
		  Response response = postRefreshToken(refreshToken);
		  spogServer.checkLogin(response, expectedStatusCode, expectedErrorCode, test);
	  }
		/**
		 * get recovered resources 
		 * 
		 * @author yuefen.liu
		 * @param pageNumber
		 * @param pageSize
		 * @param return
		 *            response
		 */
		public Response getRecoveredResources( int pageNumber, int pageSize, String token) {

			errorHandle.printDebugMessageInDebugFile("***********getRecoveredResources***********");
			String extendUrl = "";

			if ((pageNumber != -1) || (pageSize != -1)) {
				test.log(LogStatus.INFO, "set paging url");
				extendUrl += spogServer.getPagingUrl(pageNumber, pageSize);
			}
			test.log(LogStatus.INFO, "call getRecoveredResources");
			errorHandle.printDebugMessageInDebugFile("*call getRecoveredResources, url is " + extendUrl);
			Response response = userSpogInvoker.getRecoveredResources(extendUrl, token);
			
			return response;
		}
		/**
		 * get recovered resource by Id 
		 * 
		 * @author yuefen.liu
		 * @param pageNumber
		 * @param pageSize
		 * @param return
		 *            response
		 */
		public Response getRecoveredResourceById(String recoveredId, int pageNumber, int pageSize, String token) {

			test.log(LogStatus.INFO, "call etRecoveredResourceById");
		
			Response response = userSpogInvoker.getRecoveredResourceById(recoveredId, token);
			
			return response;
		}
		  /**
		   * Call REST Web service to create order for logged in user
		   * @author yuefen.liu
		   * @param orderInfo
		   * @return response
		   */
		  public Response addOrderForLoggedInUser(String orderID, String fulfillmentID, ExtentTest test) {
			  

				test.log(LogStatus.INFO, "add an order to org");
				Map<String, String> orderInfo = jp.composeOrderInfoForUser(orderID, fulfillmentID);

				Response response = userSpogInvoker.addOrderForLoggedInUser(orderInfo);

				return response;
		  }
		  /**
		   * Call REST Web service to create order for logged in user
		   * @author yuefen.liu
		   * @param orderInfo
		   * @return response
		   */
		  public void addOrderForLoggedInUserAndCheck(String orderID, String fulfillmentID, ExtentTest test) {
			  

				test.log(LogStatus.INFO, "add an order to org");
				Map<String, String> orderInfo = jp.composeOrderInfoForUser(orderID, fulfillmentID);

				Response response = userSpogInvoker.addOrderForLoggedInUser(orderInfo);
				spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);
				
				response.then().body("data.order_id", equalTo(orderID)).body("data.fulfillment_id", equalTo(fulfillmentID))
				.body("data.start_ts", not(isEmptyOrNullString())).body("data.end_ts", not(isEmptyOrNullString()));
				
		  }
		  /**
		   * Call REST Web service to create order for specified organization
		   * @author yuefen.liu
		   * @param orgId
		   * @param orderInfo
		   * @return response
		   */
		  public Response addOrderByOrgId(String orgId, String orderID, String fulfillmentID,ExtentTest test) {

				test.log(LogStatus.INFO, "add an order to org");
				Map<String, String> orderInfo = jp.composeOrderInfoForUser(orderID, fulfillmentID);

				Response response = userSpogInvoker.addOrderByOrgId(orgId, orderInfo);

				return response;

		  }
		  /**
		   * Call REST Web service to create order for specified organization
		   * @author yuefen.liu
		   * @param orgId
		   * @param orderInfo
		   * @return response
		   */
		  public void addOrderByOrgIdAndCheck(String orgId, String orderID, String fulfillmentID,ExtentTest test) {

				test.log(LogStatus.INFO, "add an order to org");
				Map<String, String> orderInfo = jp.composeOrderInfoForUser(orderID, fulfillmentID);

				Response response = userSpogInvoker.addOrderByOrgId(orgId, orderInfo);
				spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);
				
				response.then().body("data.order_id", equalTo(orderID)).body("data.fulfillment_id", equalTo(fulfillmentID))
				.body("data.start_ts", not(isEmptyOrNullString())).body("data.end_ts", not(isEmptyOrNullString()))
				.body("data.organization_id", equalTo(orgId));
		  }
		  /**
		   * Call REST Web service to get orders for logged in user
		   * @author yuefen.liu
		   * @return response
		   */
		  public Response getOrdersForLoggedInUser() {
			    Response response = userSpogInvoker.getOrdersForLoggedInUser();

				return response;
		  }
		  /**
		   * Call REST Web service to get orders for specified organization
		   * @author yuefen.liu
		   * @param orgId
		   * @return response
		   */
		  public Response getOrderByOrgId(String orgId) {
			    Response response = userSpogInvoker.getOrderByOrgId(orgId);

				return response;
		  }
		  /**
		   * Call REST Web service to get orders by order id for logged in user
		   * @author yuefen.liu
		   * @param orderId
		   * @return response
		   */
		  public Response getOrderByIdForLoggedInUser(String orderId) {
			    
				Response response = userSpogInvoker.getOrderByIdForLoggedInUser(orderId);

				return response;
		  }
		  /**
		   * Call REST Web service to get order by rorder id for specified organization
		   * @author yuefen.liu
		   * @param orgId
		   * @param orderId
		   * @return response
		   */
		  public Response getOrderByIdForSpecifiedOrg(String orgId, String orderId ) {
			  
			  Response response = userSpogInvoker.getOrderByIdForSpecifiedOrg(orgId, orderId);

				return response;
		  }
		  /* Compose the UserInfo
		   * 
		   * @author Rakesh.Chalamala
		   * @param first_name
		   * @param last_name
		   * @param email
		   * @param password
		   * @param role_id
		   * @param blocked
		   * @param status
		   * @param organization_name
		   * @param org_type
		   * @param org_blocked
		   * @param mspDetails
		   * @param test
		   * @return userInfo
		   */
		  public HashMap<String, Object> composeUserInfo(String first_name, String last_name,String email,String password,String role_id,boolean blocked,String status,
				  						String organization_name, String org_type, boolean org_blocked,HashMap<String, Object> mspDetails, ExtentTest test) {
			 
			  HashMap<String, Object> userInfo = new HashMap<>();
			  HashMap<String, Object> orgInfo = new HashMap<>(); 
			  spogServer.userLogin(email, password);
			  String user_id = spogServer.GetLoggedinUser_UserID();
			  
			  userInfo.put("user_id", user_id);
			  userInfo.put("first_name" , first_name);
			  userInfo.put("last_name", last_name);
			  userInfo.put("email", email);
			  userInfo.put("role_id", role_id);
			  userInfo.put("create_ts", spogServer.getUserByID(user_id, test).then().extract().path("data.create_ts"));
			  userInfo.put("blocked", blocked);
			  userInfo.put("status", status);
			  
			  orgInfo.put("organization_id", spogServer.GetLoggedinUserOrganizationID());
			  orgInfo.put("organization_name", organization_name);
			  orgInfo.put("type", org_type);
			  orgInfo.put("blocked", org_blocked);
			  userInfo.put("organization", orgInfo);
			  
			  if (!mspDetails.isEmpty()) {
				userInfo.put("msp", mspDetails);
			  }
			  return userInfo;
		  }
		  
		  
		  /* POST USERSSEARCH with validation
		   * 
		   * @author Rakesh.Chalamala
		   * @param expected_response
		   * @param search_string
		   * @param page
		   * @param page_size
		   * @param token
		   * @param expectedStatusCode
		   * @param expectedErrorMessage
		   * @param test
		   */
		  public void postUsersSearch(ArrayList<HashMap<String, Object>> expected_response,String search_string, String page,
				  String page_size, String token, int expectedStatusCode, SpogMessageCode expectedErrorMessage,
				  						ExtentTest test) {
			  
			  String firstname,lastname,email,userid;
			  
			  test.log(LogStatus.INFO, "Preparing the body for the search request");
			  HashMap<String, Object> searchInfo = new HashMap<>();
			  searchInfo.put("search_string", search_string);
			  if (!page.equals("") || !page.equals(null)) {
				  searchInfo.put("page", page);
			  }
			  if (!page_size.equals("") || !page_size.equals(null)) {
				  searchInfo.put("page_size", page_size);
			  }
			  
			  test.log(LogStatus.INFO, "Call the Rest API to get the response");
			  Response response = userSpogInvoker.postUsersSearch(searchInfo, token);
			  spogServer.checkResponseStatus(response, expectedStatusCode, test);
			  
			  if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
				
				  ArrayList<HashMap<String, Object>> expectedresponse = new ArrayList<>();
				  for (int i = 0; i < expected_response.size(); i++) {
					  
					  firstname = expected_response.get(i).get("first_name").toString();
					  lastname = expected_response.get(i).get("last_name").toString();
					  email = expected_response.get(i).get("email").toString();
					  userid = expected_response.get(i).get("user_id").toString();
					  
					  if (firstname.contains(search_string) || lastname.contains(search_string) 
							  || email.contains(search_string) || userid.contains(search_string)) {
						  
						expectedresponse.add(expected_response.get(i));
					  }
				  }
				  
				  ArrayList<HashMap<String, Object>> actualresponse = response.then().extract().path("data");
				  
				  test.log(LogStatus.INFO, "Sort the expected response by email id");
				  spogServer.sortArrayListbyInt(expectedresponse, "create_ts");
					  
				  int j = 0, page1 = Integer.parseInt(page), page_size1 = Integer.parseInt(page_size);
				  if((page1 <= 0) || (page_size1 <= 0)) {
					  page1 = 1;
					  page_size1 = 20;
				  }
				  j = page_size1 * (page1 - 1); 
				  if (j <= expectedresponse.size() ) {

					  for (int i = 0; j < actualresponse.size(); i++, j++) {

						  test.log(LogStatus.INFO, " Compare the users in expected and actual.");
						  checkSearchResult(expectedresponse.get(j), actualresponse.get(i), test);
					  }
				  }else {
					  test.log(LogStatus.INFO, "The page number "+page+" exceeded the actual number of pages");
				  }
			  } else {
				String code = expectedErrorMessage.getCodeString();
				String message = expectedErrorMessage.getStatus();
				spogServer.checkErrorCode(response, code);
				test.log(LogStatus.PASS, "The error code matched with the expected " + code);
				spogServer.checkErrorMessage(response, message);
				test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
			}
		  }
		  
		  /* POST USERSSEARCH with response
		   * 
		   * @author shan.jing
		   * @param search_string
		   * @param page
		   * @param page_size
		   * @param test
		   */
		  public Response postUsersSearch(String search_string, String page,
				  String page_size, String token, ExtentTest test) {
			  
			  HashMap<String, Object> searchInfo = new HashMap<>();
			  searchInfo.put("search_string", search_string);
			  if (!page.equals("") || !page.equals(null)) {
				  searchInfo.put("page", page);
			  }
			  if (!page_size.equals("") || !page_size.equals(null)) {
				  searchInfo.put("page_size", page_size);
			  }
			  
			  test.log(LogStatus.INFO, "Call the Rest API to get the response");
			  Response response = userSpogInvoker.postUsersSearch(searchInfo, token);
			  return response;
		  }
		  
		  /*Compare the actual search result with the expected
		   * 
		   * @author Rakesh.Chalamala
		   * @param expected
		   * @param actual
		   * @param test
		   */
		  public void checkSearchResult(HashMap<String, Object> expected, HashMap<String, Object> actual,ExtentTest test) {
			  
			  test.log(LogStatus.INFO, "Compare the user_id");
			  spogServer.assertResponseItem(expected.get("user_id"), actual.get("user_id"),test);
			  
			  test.log(LogStatus.INFO, "Compare the first_name");
			  spogServer.assertResponseItem(expected.get("first_name"), actual.get("first_name"), test);
			  
			  test.log(LogStatus.INFO, "Compare the last_name");
			  spogServer.assertResponseItem(expected.get("last_name"), actual.get("last_name"), test);
			  
			  test.log(LogStatus.INFO, "Compare the email");
			  spogServer.assertResponseItem(expected.get("email"), actual.get("email"), test);
			  
			  test.log(LogStatus.INFO, "Compare the role_id");
			  spogServer.assertResponseItem(expected.get("role_id"), actual.get("role_id"), test);
			  
			  test.log(LogStatus.INFO, "Compare the blocked");
			  spogServer.assertResponseItem(expected.get("blocked"), actual.get("blocked"), test);
			  
			  test.log(LogStatus.INFO, "Compare the status");
			  spogServer.assertResponseItem(expected.get("status"), actual.get("status"), test);
			  
			  HashMap<String , Object> exp_org = (HashMap<String, Object>) expected.get("organization");
			  HashMap<String, Object> act_org = (HashMap<String, Object>) actual.get("organization");
			  
			  test.log(LogStatus.INFO, "Compare the organization_id");
			  spogServer.assertResponseItem(exp_org.get("organization_id"), act_org.get("organization_id"), test);
			  
			  test.log(LogStatus.INFO, "Compare the organization name");
			  spogServer.assertResponseItem(exp_org.get("organization_name"), act_org.get("organization_name"), test);
			  
			  test.log(LogStatus.INFO, "Compar the organization type");
			  spogServer.assertResponseItem(exp_org.get("type"), act_org.get("type"), test);
			  
			  test.log(LogStatus.INFO, "Comapre the blocked status of organization");
			  spogServer.assertResponseItem(exp_org.get("blocked"), act_org.get("blocked"), test);
			  
			  if (actual.get("msp") == "" || actual.get("msp") == null || !(act_org.get("type").equals("msp_child"))) {
				  test.log(LogStatus.INFO, "msp hash is empty");
			  } else {

				  HashMap<String , Object> exp_msp = (HashMap<String, Object>) expected.get("msp");
				  HashMap<String, Object> act_msp = (HashMap<String, Object>) actual.get("msp");

				  test.log(LogStatus.INFO, "Compare the organization_id");
				  spogServer.assertResponseItem(exp_msp.get("organization_id"), act_msp.get("organization_id"), test);

				  test.log(LogStatus.INFO, "Compare the organization name");
				  spogServer.assertResponseItem(exp_msp.get("organization_name"), act_msp.get("organization_name"), test);

				  test.log(LogStatus.INFO, "Compar the organization type");
				  spogServer.assertResponseItem(exp_msp.get("type"), act_msp.get("type"), test);

				  test.log(LogStatus.INFO, "Comapre the blocked status of organization");
				  spogServer.assertResponseItem(exp_msp.get("blocked"), act_msp.get("blocked"), test);
			  }
		  }
		  /*GET users/Amount with check 
		   * 
		   * @author Eric.Yang
		   * @param expectedStatusCode
		   * @param expected_num
		   * @param test
		   */		  
			public Response getUsersAmountWithCheck(String token,int expectedStatusCode,int expected_num,ExtentTest test) {
				
				Response response = userSpogInvoker.getUsersAmount(token, test);

				errorHandle.printDebugMessageInDebugFile("Response status is " + response.getStatusCode());
				response.then().statusCode(expectedStatusCode);
				if (response.getStatusCode() == expectedStatusCode) {
					test.log(LogStatus.INFO, "The returned status code from the response is " + response.getStatusCode());
					//assertTrue("Check status is equal to expected status:" + expectedStatusCode, true);
					test.log(LogStatus.PASS, "Check:response status");
				} else {
					assertTrue("Check status is equal to expected status:" + expectedStatusCode, false);
					test.log(LogStatus.FAIL, "Check:response status");
					return response;
				}		

				if (expectedStatusCode == response.getStatusCode()) {
					if (expectedStatusCode==403||expectedStatusCode==401){
						test.log(LogStatus.PASS, "Check:" + "check response status " + " in reponse body.");
						assertTrue("Check expected status : "+expectedStatusCode+" is equal to expected status:" + expectedStatusCode, true);
						return response;
					}
					else{
						response.then().log().all();
						int amount = response.then().extract().path("data.amount");
						if(expected_num==amount){
							test.log(LogStatus.PASS, "equal to expected value :"+expected_num+"Check:" + "users amount " +amount+ " in reponse body.");
							assertTrue("Check value: "+amount+" is equal to expected amount:" + expected_num, true);
							return response;
							}
						else{
							test.log(LogStatus.FAIL, "not equal to expected value :"+expected_num+"Check:" + "users amount " +amount+ " in reponse body.");
							assertTrue("Check value: "+amount+" is not equal to expected amount:" + expected_num, false);
							return response;
							}
					}

				}
				else
					test.log(LogStatus.FAIL, "statusCode:" +response.getStatusCode()+ ", not equal expectedStatusCode: "+expectedStatusCode+ " in reponse body.");
				
				System.out.println("The value of the getOrganizationsIdRecoversourceTypes is :" + response.getBody().asString());
				return response;
			}		  
			
			
			public Response postUsersSearchResponse(String search_string, String page, String page_size, String token){
				
			
				  HashMap<String, Object> searchInfo = new HashMap<>();
				  searchInfo.put("search_string", search_string);
				  if ((page!=null )&& (!page.equals(""))) {
					  searchInfo.put("page", page);
				  }
				  if ((page_size!=null )&& (!page_size.equals(""))) {
					  searchInfo.put("page_size", page_size);
				  }
		
				  Response response = userSpogInvoker.postUsersSearch(searchInfo, token);
				  return response;
				//  return response.then().extract().path("data.organization.organization_id");
				  
			}
			/**
			 * @author shuo.zhang
			 * @param userId
			 * @param orgInfo
			 * @param masteredAccountIds
			 * @param token
			 * @param test
			 * @return
			 */
			public  Response  assignAccounts(String userId, Object orgInfo, String masteredAccountIds, String token, ExtentTest test){
				
				if((orgInfo!=null) && !orgInfo.equals("")){
					orgInfo = jp.composeAssignAccountsIdsInfo(masteredAccountIds);
				}
				Map<String, Object > accountInfo = jp.composeAssignAccountsInfo(orgInfo);
				return userSpogInvoker.assignAccount(userId, accountInfo, token, test);
			}
			
			/**
			 * @author shuo.zhang
			 * @param response
			 * @param masteredAccountIds
			 * @param expectedStatusCode
			 * @param expectedErrorCode
			 * @param test
			 */
			public void checkAssignAccounts(Response response, String masteredAccountIds, int expectedStatusCode, String expectedErrorCode,   ExtentTest test){
				
				response.then().statusCode(expectedStatusCode);
				if(expectedStatusCode==SpogConstants.SUCCESS_GET_PUT_DELETE){
					String[] expectedOrgIds = masteredAccountIds.split(";");
					  ArrayList<String> orgIdsList = new ArrayList<>();
					  orgIdsList= response.then().extract().path("data.organizations.organization_id");
					 for(int j=0;j<expectedOrgIds.length;j++)
					  {
						  if (orgIdsList.get(j).equalsIgnoreCase(expectedOrgIds[j])){
							  assertTrue("It's correct", true);
						  }
					  }
					
				}else{
					spogServer.checkErrorCode(response, expectedErrorCode);
				}
				
			}
			
			/**
			 * @author shuo.zhang
			 * @param userId
			 * @param orgInfo
			 * @param masteredAccountIds
			 * @param test
			 * @return
			 */
			public  Response  assignAccountsWithoutLogin(String userId, Object orgInfo, String masteredAccountIds,  ExtentTest test){
				
				if((orgInfo!=null) && !orgInfo.equals("")){
					orgInfo = jp.composeAssignAccountsIdsInfo(masteredAccountIds);
				}
				Map<String, Object > accountInfo = jp.composeAssignAccountsInfo(orgInfo);
				return userSpogInvoker.assignAccountWithoutLogin(userId, accountInfo,  test);
			}
			
			/**
			 * @author shuo.zhang
			 * @param email
			 * @return
			 */
			public Response forgetPassword(String email,  ExtentTest test){
				
				Map<String, Object> emailInfo = jp.composeEmailInfo(email);
				return userSpogInvoker.forgetPassword(email, emailInfo);
			}
			
			/**
			 * @author shuo.zhang
			 * @param response
			 * @param expectedStatusCode
			 * @param expectedErrorCode
			 * @param test
			 */
			public void checkForgetPassword(Response response,  int expectedStatusCode, String expectedErrorCode,   ExtentTest test){
				response.then().statusCode(expectedStatusCode);
				if(expectedStatusCode!=SpogConstants.SUCCESS_GET_PUT_DELETE){
					spogServer.checkErrorCode(response, expectedErrorCode);
				}
			}

	/**
	 * createPolicyFilterForSpecificUserWithCheck_savesearch
	 * 
	 * @author Ramya.Nagepalli
	 * @param user_id
	 * @param filter_name
	 * @param policy_name
	 * @param group_id
	 * @param last_backup_status
	 * @param status
	 * @param is_default
	 * @param test
	 * @return
	 */
	public Response createPolicyFilterForSpecificUserWithCheck_savesearch(String org_id, String user_id,
			String filter_name, String status, String is_default, String policy_filter_type, ExtentTest test) {

		HashMap<String, Object> policyInfo = jp.composePolicyFilter_savesearch(filter_name, org_id, status, is_default,
				policy_filter_type);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!policyInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.policy_filter.toString(),
					filter_name, user_id, "none",
					Boolean.valueOf(is_default), policyInfo);
		}
		Response response = spogServer.createFilters(userSpogInvoker.getToken(), filter_info, "", test); 	
		/*
		 * Response response = userSpogInvoker.createPolicyFilterForUser(user_id,
		 * filterInfo);
		 */
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		return response;
	}

	/**
	 * createUserFilterForSpecificUser_savesearch
	 * 
	 * @author Ramya.Nagepalli
	 * @param user_Id
	 * @param filter_name
	 * @param search_string
	 * @param user_is_blocked
	 * @param user_status
	 * @param role_id
	 * @param is_default
	 * @param token
	 * @param organization_id
	 * @param user_filter_type
	 * @param test
	 * @return
	 */
	public Response createUserFilterForSpecificUser_savesearch(String user_Id, String filter_name, String search_string,
			String user_is_blocked, String user_status, String role_id, String is_default, String token,
			String organization_id, String user_filter_type, ExtentTest test) {

		test.log(LogStatus.INFO, "Compose user filter");
		HashMap<String, Object> userFilterInfo = jp.composeUserFilterInfo_savesearch(filter_name, search_string,
				user_is_blocked, user_status, role_id, is_default, organization_id, user_filter_type);

		test.log(LogStatus.INFO, "Call the Rest API to create user filters");
		/*
		 * Response response = userSpogInvoker.createUserFilterForUser(user_Id,
		 * userFilterInfo, token);
		 */
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!userFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.user_filter.toString(), filter_name, user_Id, "none",
					Boolean.valueOf(is_default), userFilterInfo);
		}
		test.log(LogStatus.INFO, "Call the Rest API to create user filters");
		Response response = spogServer.createFilters(token, filter_info, "", test);
		return response;
	}

	/**
	 * add order with check for Logged in user organization
	 * 
	 * @author Rakesh.Chalamala
	 * @param orderID
	 * @param fulfillmentID
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 */
	public void addOrderForLoggedInUserWithCheck(String orderID, String fulfillmentID, int expectedStatusCode, SpogMessageCode expectedErrorMessage, ExtentTest test) {
				
		test.log(LogStatus.INFO, "compose JSON with order and fulfillment details for payload");
		Map<String, String> orderInfo = jp.composeOrderInfoForUser(orderID, fulfillmentID);

		test.log(LogStatus.INFO, "Call rest api POST: /organization/orders ");
		Response response = userSpogInvoker.addOrderForLoggedInUser(orderInfo);
		spogServer.checkResponseStatus(response, expectedStatusCode);
		
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
		
			response.then().body("data.order_id", equalTo(orderID)).body("data.fulfillment_id", equalTo(fulfillmentID))
			.body("data.start_ts", not(isEmptyOrNullString())).body("data.end_ts", not(isEmptyOrNullString()));
			
			test.log(LogStatus.PASS, "Order activated successfully");
		}else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
	}
	
	/** Add order with check for a specified org 
	 * 
	 * @author Rakesh.Chalamala
	 * @param orgId
	 * @param orderID
	 * @param fulfillmentID
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 */
	public void addOrderByOrgIdWithCheck(String orgId, String orderID, String fulfillmentID, int expectedStatusCode, SpogMessageCode expectedErrorMessage, ExtentTest test) {
		
		test.log(LogStatus.INFO, "compose JSON with order and fulfillment details for payload");
		Map<String, String> orderInfo = jp.composeOrderInfoForUser(orderID, fulfillmentID);

		test.log(LogStatus.INFO, "Call rest api POST: /organizations/{id}/orders ");
		Response response = userSpogInvoker.addOrderByOrgId(orgId, orderInfo);
		spogServer.checkResponseStatus(response, expectedStatusCode);
		
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
		
			response.then().body("data.order_id", equalTo(orderID)).body("data.fulfillment_id", equalTo(fulfillmentID))
			.body("data.start_ts", not(isEmptyOrNullString())).body("data.end_ts", not(isEmptyOrNullString()))
			.body("data.organization_id", equalTo(orgId));
			
			test.log(LogStatus.PASS, "Order activated successfully");
		}else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
	}
	
}
 