package InvokerServer;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertTrue;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import org.omg.CORBA.NO_RESPONSE;
import org.testng.Assert;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.relevantcodes.extentreports.model.Log;

import Constants.SpogConstants;
import Constants.SpogMessageCode;
import Constants.FilterTypes.filterType;
import Constants.ErrorCode;
import Constants.HypervisorColumnConstants;
import Constants.SourceType;
import dataPreparation.JsonPreparation;
import genericutil.ErrorHandler;
import invoker.SPOGInvoker;
import invoker.Source4SPOGInvoker;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class Source4SPOGServer {
	private static JsonPreparation jp = new JsonPreparation();
	private SPOGServer spogServer;
	private Source4SPOGInvoker source4SpogInvoker;
	public static ErrorHandler errorHandle = ErrorHandler.getErrorHandler();
	private ExtentTest test;


	public void setToken(String token) {
		source4SpogInvoker.setToken(token);
		spogServer.setToken(token);
	}

	public Source4SPOGServer(String baseURI, String port) {
		
		source4SpogInvoker = new Source4SPOGInvoker(baseURI, port);
		spogServer = new SPOGServer(baseURI, port);
	}
	
	/**
	 * create specified user source columns
	 * @author shuo.zhang
	 * @param userId
	 * @param columnsList

	 * @param token
	 * @param columnsList 
	 * @return
	 */
	public Response createUsersSourcesColumns(String userId,  ArrayList<HashMap<String, Object>> columnsList, String token ){

		errorHandle.printDebugMessageInDebugFile("******************createUsersSourcesColumns*******************");

		Map<String, Object> columnsInfo = jp.getUsersSourcesColumnsInfo(columnsList );
		return source4SpogInvoker.createUsersSourcesColumns(userId, columnsInfo , token);
	}

	/**
	 * set user source column info
	 * @author shuo.zhang
	 * @param column_id
	 * @param visible
	 * @param order_id
	 * @return
	 */
	public HashMap<String, Object> getSourceColumnInfo(String column_id, String visible, String order_id){
		
		HashMap<String, Object> sourceColumnInfo = new HashMap<String, Object> ();
		sourceColumnInfo.put("column_id", column_id);
		if(!visible.equalsIgnoreCase("none")){
			sourceColumnInfo.put("visible", Boolean.valueOf(visible));
		}
		if(!order_id.equalsIgnoreCase("none")){
			sourceColumnInfo.put("order_id", Integer.valueOf(order_id));		
		}
	
		return sourceColumnInfo;
	}

	
	/**
	 * delete a destination filter for logged in user
	 * 
	 * @author Kiran.Sripada
	 * @param userID
	 * @param filter_Id
	 * @param token
	 * @param expectedstatuscode
	 * @param ExpectedErrorMessage
	 * 
	 */
	public void deletesourcefilterforLoggedInuser(String filter_Id, String token, int expectedstatuscode, SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		
		
	    Response response = source4SpogInvoker.deletesourcefilterforLoggedInuser(filter_Id, token);
	    spogServer.checkResponseStatus(response, expectedstatuscode);
	    if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
	      test.log(LogStatus.PASS,
	          "Successfully deleted the filter and the response status is " + response.getStatusCode());
	      
	    } else {
	    	String code = ExpectedErrorMessage.getCodeString();
	    	String message = ExpectedErrorMessage.getStatus();
	    	if(code.contains("00A00002")){
	    		Response response1 = spogServer.getLoggedInUser(token, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	            String user_Id = response1.then().extract().path("data.user_id");
	    		message = message.replace("{0}", filter_Id);
	    		message= message.replace("{1}", user_Id);
	    	}
	    	spogServer.checkErrorCode(response,code);
	    	test.log(LogStatus.INFO, "The error code matched with the expected "+code);
	    	spogServer.checkErrorMessage(response,message);
	    	test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
	    }
	    
		
	}

	
	
	
	/**
	 * get user source column array list, it is used for preparing column info
	 * @author shuo.zhang
	 * @param specifiedSourceColumns  
	 * column_short_name;visible;order_id
	 * ex:"Name;true;1,Organization;true;2,Type;false;3"
	 * @param token
	 * @return
	 */
	public ArrayList<HashMap<String, Object>>  getSourceColumnArrayList( String specifiedSourceColumns, String token){
		
		errorHandle.printDebugMessageInDebugFile("******************getSourceColumnArrayList*******************");
		spogServer.setToken(token);
		ArrayList<HashMap<String, Object>> columnsList = new ArrayList<HashMap<String, Object>> ();
		String[] specifiedSourceColumnArray = specifiedSourceColumns.split(",");		  
		Response response = spogServer.getSourcesColumns(test);
		ArrayList<HashMap<String, Object>> sourceColumnsInfo = response.then().extract().path("data");
		
		for(int j=0; j<specifiedSourceColumnArray.length; j++ ){
			  String eachSpecifiedSourceColumn = specifiedSourceColumnArray[j];
			  String[] eachSpecifiedSourceColumnDetailInfo = eachSpecifiedSourceColumn.split(";");
			  for(int i=0; i< sourceColumnsInfo.size(); i++){
				  String key = String.valueOf(sourceColumnsInfo.get(i).get("short_label"));
				  if(key.equalsIgnoreCase(eachSpecifiedSourceColumnDetailInfo[0])){
				
					 HashMap<String, Object> sourceColumnMap = getSourceColumnInfo(String.valueOf(sourceColumnsInfo.get(i).get("column_id")),
							 eachSpecifiedSourceColumnDetailInfo[1],  eachSpecifiedSourceColumnDetailInfo[2]);
					 
					 columnsList.add(sourceColumnMap);
					 break;
				  }
				  if(i==sourceColumnsInfo.size()-1){
					  errorHandle.printDebugMessageInDebugFile("can't find the source column " + key );
					  return null;
				  }
			  }
		}
		return columnsList;
		 
	}
	
	/**
	 * get expected source columns, it is used for compare response
	 *  @author shuo.zhang
	 * @param columnsList
	 * @param token
	 * @return
	 */
	public ArrayList<HashMap<String, Object>> getExpectedUserSourceColumns(ArrayList<HashMap<String, Object>> columnsList, String token){
		
		errorHandle.printDebugMessageInDebugFile("******************getExpectedUserSourceColumns*******************");
		spogServer.setToken(token);
		
		ArrayList<HashMap<String, Object>> expectedColumnsList = new ArrayList<HashMap<String, Object>> ();
		/*String[] specifiedSourceColumnArray = specifiedSourceColumns.split(",");		  
		Response response = spogServer.getSourcesColumns(test);
		ArrayList<HashMap<String, Object>> sourceColumnsInfo = response.then().extract().path("data");
		
		for(int j=0; j<specifiedSourceColumnArray.length; j++ ){
			  String eachSpecifiedSourceColumn = specifiedSourceColumnArray[j];
			  String[] eachSpecifiedSourceColumnDetailInfo = eachSpecifiedSourceColumn.split(";");
			  for(int i=0; i< sourceColumnsInfo.size(); i++){
				  String key = String.valueOf(sourceColumnsInfo.get(i).get("short_label"));
				  if(key.equalsIgnoreCase(eachSpecifiedSourceColumnDetailInfo[0])){
					 HashMap<String, Object> sourceColumnMap = new  HashMap<String, Object> ();
					 sourceColumnMap.put("column_id", String.valueOf(sourceColumnsInfo.get(i).get("column_id")));
					 sourceColumnMap.put("long_label", String.valueOf(sourceColumnsInfo.get(i).get("long_label")));
					 sourceColumnMap.put("short_label", String.valueOf(sourceColumnsInfo.get(i).get("short_label")));
					 sourceColumnMap.put("key", String.valueOf(sourceColumnsInfo.get(i).get("key")));
					 sourceColumnMap.put("sort", (boolean) sourceColumnsInfo.get(i).get("sort"));
					 sourceColumnMap.put("filter", (boolean) sourceColumnsInfo.get(i).get("filter"));
					 sourceColumnMap.put("visible",eachSpecifiedSourceColumnDetailInfo[1]);					
					 sourceColumnMap.put("order_id", eachSpecifiedSourceColumnDetailInfo[2]);
					 expectedColumnsList.add(sourceColumnMap);
					 break;
				  }
				  if(i==sourceColumnsInfo.size()-1){
					  errorHandle.printDebugMessageInDebugFile("can't find the source column " + key );
					  return null;
				  }
			  }
		}*/
		  
		Response response = spogServer.getSourcesColumns(test);
		ArrayList<HashMap<String, Object>> sourceColumnsInfo = response.then().extract().path("data");
			
		for(int j=0; j<columnsList.size(); j++ ){
			   HashMap<String, Object> eachSpecifiedSourceColumn = columnsList.get(j);
			   String column_id = eachSpecifiedSourceColumn.get("column_id").toString();
			   Iterator<HashMap<String, Object>> expectedIter = expectedColumnsList.iterator();
			   boolean isExisted= false;
			   while(expectedIter.hasNext()){
				   if(expectedIter.next().get("column_id").toString().equals(column_id)){
					   isExisted= true;
				   }
			   }
			  if(!isExisted){
				  for(int i=0; i< sourceColumnsInfo.size(); i++){
					  String key = String.valueOf(sourceColumnsInfo.get(i).get("column_id"));
					  if(key.equalsIgnoreCase(eachSpecifiedSourceColumn.get("column_id").toString())){
						 HashMap<String, Object> sourceColumnMap = new  HashMap<String, Object> ();
						 sourceColumnMap.put("column_id", String.valueOf(sourceColumnsInfo.get(i).get("column_id")));
						 sourceColumnMap.put("long_label", String.valueOf(sourceColumnsInfo.get(i).get("long_label")));
						 sourceColumnMap.put("short_label", String.valueOf(sourceColumnsInfo.get(i).get("short_label")));
						 sourceColumnMap.put("key", String.valueOf(sourceColumnsInfo.get(i).get("key")));
						 sourceColumnMap.put("sort", (boolean) sourceColumnsInfo.get(i).get("sort"));
						 sourceColumnMap.put("filter", (boolean) sourceColumnsInfo.get(i).get("filter"));
						 if(eachSpecifiedSourceColumn.containsKey("visible")){
							 sourceColumnMap.put("visible", eachSpecifiedSourceColumn.get("visible"));		
						 }else{
							 sourceColumnMap.put("visible", sourceColumnsInfo.get(i).get("visible"));		
						 }
						 if(eachSpecifiedSourceColumn.containsKey("order_id"))	{
							 sourceColumnMap.put("order_id", eachSpecifiedSourceColumn.get("order_id"));
						 }else{
							 sourceColumnMap.put("order_id", null);
						 }
				
						 expectedColumnsList.add(sourceColumnMap);
						 break;
					  }
					  if(i==sourceColumnsInfo.size()-1){
						  errorHandle.printDebugMessageInDebugFile("can't find the source column " + key );
						  return null;
					  }
				  }
			  }
		
		}
		
		return expectedColumnsList;
	}
	
	/**
	 * create specified user source columns with check
	 * @author shuo.zhang
	 * @param userId
	 * @param columnsList
	 * @param token
	 * @param statusCode
	 * @param errorCode
	 */
	public void createUsersSourcesColumnsWithCheck(String userId, ArrayList<HashMap<String, Object>> columnsList,String token, int statusCode, String errorCode){
		
		errorHandle.printDebugMessageInDebugFile("******************createUsersSourcesColumnsWithCheck*******************");
		Response response = createUsersSourcesColumns( userId, columnsList, token );
		checkUsersSourcesColumns(response, columnsList,token, statusCode,  errorCode);
	/*	response.then().statusCode(statusCode);
		
		if(statusCode == SpogConstants.SUCCESS_POST){
			ArrayList<HashMap<String, Object>> sourceColumnsInfo = response.then().extract().path("data");
			ArrayList<HashMap<String, Object>> expectedSourceColumns = getExpectedUserSourceColumns(columnsList, token );
			assertEquals(expectedSourceColumns.size(), sourceColumnsInfo.size() );
			response.then().log().all();
			for(int i=0; i<sourceColumnsInfo.size(); i++) {
				HashMap<String, Object> responseInfo = sourceColumnsInfo.get(i);
				HashMap<String, Object> expectedSourceColumn = expectedSourceColumns.get(i);
				Iterator<String> keyIter = responseInfo.keySet().iterator();
				while(keyIter.hasNext()){
					String key = keyIter.next();
					System.out.println(key);
					assertEquals(responseInfo.get(key).toString(), expectedSourceColumn.get(key).toString());
				}
			}
		}else{
			spogServer.checkErrorCode(response, errorCode);
		}		*/	
	}
	
	/**
	 * create specified user source columns without login and check
	 * @author shuo.zhang
	 * @param userId
	 * @param token
	 */
	public void createUsersSourcesColumnsWithoutLoginAndCheck(String userId ){
		
		errorHandle.printDebugMessageInDebugFile("******************createUsersSourcesColumnsWithoutLoginAndCheck*******************");
	//	ArrayList<HashMap<String, Object>> columnsList = getSourceColumnArrayList(specifiedSourceColumns, token);
		ArrayList<HashMap<String, Object>> columnsList = new ArrayList<HashMap<String, Object>>();
		Map<String, Object> columnsInfo = jp.getUsersSourcesColumnsInfo(columnsList );
		Response response = source4SpogInvoker.createUsersSourcesColumnsWithoutLogin(userId, columnsInfo);
		checkUsersSourcesColumns(response, columnsList,null, SpogConstants.NOT_LOGGED_IN,  ErrorCode.AUTHORIZATION_HEADER_BLANK);
	/*	response.then().statusCode(SpogConstants.NOT_LOGGED_IN);		
		spogServer.checkErrorCode(response, ErrorCode.AUTHORIZATION_HEADER_BLANK);
		
		source4SpogInvoker.createUsersSourcesColumns(userId, columnsInfo, UUID.randomUUID().toString());
		response.then().statusCode(SpogConstants.NOT_LOGGED_IN);		
		spogServer.checkErrorCode(response, ErrorCode.AUTHORIZATION_HEADER_BLANK);*/
	}
	
	public Response submitBackupForSource(String sourceID, String token) {
		Response response = source4SpogInvoker.startBackupForSource(sourceID, token);
		return response;
	}
	
	public Response submitBackupForSource(String sourceID) {
		Response response = source4SpogInvoker.startBackupForSource(sourceID);
		return response;
	}
	
	public Response cancelBackupForSource(String sourceID, String token) {
		Response response = source4SpogInvoker.cancelBackupForSource(sourceID, token);
		return response;
	}
	
	public Response cancelBackupForSource(String sourceID) {
		Response response = source4SpogInvoker.cancelBackupForSource(sourceID);
		return response;
	}

	/**
	 * update specified user source columns
	 * @author shuo.zhang
	 * @param userId
	 * @param columnsList
	 * @param token
	 * @return
	 */
	public Response updateUsersSourcesColumns(String userId,  ArrayList<HashMap<String, Object>> columnsList, String token ){

		errorHandle.printDebugMessageInDebugFile("******************updateUsersSourcesColumns*******************");
		Map<String, Object> columnsInfo = jp.getUsersSourcesColumnsInfo(columnsList );
		return source4SpogInvoker.updateUsersSourcesColumns(userId, columnsInfo , token);
	}

	/**
	 * update specified user source columns and check
	 * @author shuo.zhang
	 * @param userId
	 * @param specifiedSourceColumns
	 * @param token
	 * @param statusCode
	 * @param errorCode
	 */
	public void updateUsersSourcesColumnsWithCheck(String userId, ArrayList<HashMap<String, Object>> columnsList,String token, int statusCode, String errorCode){
		
		errorHandle.printDebugMessageInDebugFile("******************updateUsersSourcesColumnsWithCheck*******************");
		Response response = updateUsersSourcesColumns( userId, columnsList, token );
		checkUsersSourcesColumns(response, columnsList,token, statusCode,  errorCode);
	}
	
	/**
	 * check source column response
	 * @author shuo.zhang
	 * @param response
	 * @param columnsList
	 * @param token
	 * @param statusCode
	 * @param errorCode
	 */
	public void checkUsersSourcesColumns(Response response, ArrayList<HashMap<String, Object>> columnsList,String token, int statusCode, String errorCode){
		
		errorHandle.printDebugMessageInDebugFile("******************checkUsersSourcesColumns*******************");
		response.then().statusCode(statusCode);
		
		/*if(statusCode == SpogConstants.SUCCESS_POST || statusCode == SpogConstants.SUCCESS_GET_PUT_DELETE ){
			ArrayList<HashMap<String, Object>> sourceColumnsInfo = response.then().extract().path("data");
			ArrayList<HashMap<String, Object>> expectedSourceColumns = getExpectedUserSourceColumns(columnsList, token );
		//	assertEquals(expectedSourceColumns.size(), sourceColumnsInfo.size() );
			response.then().log().all();

			for(int i=0; i<sourceColumnsInfo.size(); i++) {
				HashMap<String, Object> responseInfo = sourceColumnsInfo.get(i);
				String column_id = String.valueOf(responseInfo.get("column_id"));
				boolean find = false;
				for(int j=0; j<expectedSourceColumns.size();j++){
					HashMap<String, Object> expectedSourceColumn = expectedSourceColumns.get(j);
					if(String.valueOf(expectedSourceColumn.get("column_id")).equalsIgnoreCase(column_id)){
						//find 
						find = true;
						Iterator<String> keyIter = responseInfo.keySet().iterator();
						while(keyIter.hasNext()){
							String key = keyIter.next();
							System.out.println(key);
							Object expectedValue =  expectedSourceColumn.get(key);
							if(expectedValue != null){
								assertEquals(responseInfo.get(key).toString(), expectedValue.toString());
							}else{
								assertNull(responseInfo.get(key));
							}
					
						}
						break;
					}
				}
				
				Assert.assertTrue(find);
				
			}

				
		}else{
			spogServer.checkErrorCode(response, errorCode);
		}		*/
		
		if(statusCode == SpogConstants.SUCCESS_POST || statusCode == SpogConstants.SUCCESS_GET_PUT_DELETE ){
			ArrayList<HashMap<String, Object>> sourceColumnsInfo = response.then().extract().path("data");
			ArrayList<HashMap<String, Object>> expectedSourceColumns = getExpectedUserSourceColumns(columnsList, token );
			response.then().log().all();
			
			for(int j=0; j<expectedSourceColumns.size();j++){
				HashMap<String, Object> expectedSourceColumn = expectedSourceColumns.get(j);
				boolean find=false;
				for(int i=0; i<sourceColumnsInfo.size(); i++) {
					HashMap<String, Object> responseInfo = sourceColumnsInfo.get(i);
					String column_id = String.valueOf(responseInfo.get("column_id"));
				
					if(String.valueOf(expectedSourceColumn.get("column_id")).equalsIgnoreCase(column_id)){
						//find 
						find=true;
						Iterator<String> keyIter = responseInfo.keySet().iterator();
						while(keyIter.hasNext()){
							String key = keyIter.next();
							System.out.println(key);
							Object expectedValue =  expectedSourceColumn.get(key);
							if(expectedValue != null){
								assertEquals(responseInfo.get(key).toString(), expectedValue.toString());
							}else{
								assertNull(responseInfo.get(key));
							}
					
						}
						break;
					}
				}
				Assert.assertTrue(find);
			}
			

				
		}else{
			spogServer.checkErrorCode(response, errorCode);
		}
	}
	

	/**
	 * update specified user source columns without login and check
	 * @author shuo.zhang
	 * @param userId
	 * @param token
	 */
	public void updateUsersSourcesColumnsWithoutLoginAndCheck(String userId ){
		
		errorHandle.printDebugMessageInDebugFile("******************updateUsersSourcesColumnsWithoutLoginAndCheck*******************");
		ArrayList<HashMap<String, Object>> columnsList = new ArrayList<HashMap<String, Object>>();
		Map<String, Object> columnsInfo = jp.getUsersSourcesColumnsInfo(columnsList );
		Response response = source4SpogInvoker.updateUsersSourcesColumnsWithoutLogin(userId, columnsInfo);
		checkUsersSourcesColumns(response, columnsList,null, SpogConstants.NOT_LOGGED_IN,  ErrorCode.AUTHORIZATION_HEADER_BLANK);
	
	}

	/**
	 * delete specified user source columns
	 *  @author shuo.zhang
	 * @param userId
	 * @param token
	 * @return
	 */
	public Response deleteUsersSourcesColumns(String userId, String token ){
		
		errorHandle.printDebugMessageInDebugFile("******************deleteUsersSourcesColumns*******************");
		return source4SpogInvoker.deleteUsersSourcesColumns(userId, token);
	}
	
	/**
	 * delete specified user source columns with check
	 *  @author shuo.zhang
	 * @param userId
	 * @param token
	 * @param statusCode
	 * @param errorCode
	 */
	public void deleteUsersSourcesColumnsWithCheck(String userId, String token, int statusCode, String errorCode ){
		
		errorHandle.printDebugMessageInDebugFile("******************deleteUsersSourcesColumnsWithCheck*******************");
		Response response = deleteUsersSourcesColumns(userId, token);
		checkDeleteUsersSourcesColumns(response, statusCode, errorCode);
	}
	
	/**
	 * check delete  source columns response
	 *  @author shuo.zhang
	 * @param response
	 * @param statusCode
	 * @param errorCode
	 */
	public void checkDeleteUsersSourcesColumns(Response response, int statusCode, String errorCode){
		
		errorHandle.printDebugMessageInDebugFile("******************checkDeleteUsersSourcesColumns*******************");
		response.then().statusCode(statusCode);
		if(statusCode != SpogConstants.SUCCESS_GET_PUT_DELETE ){
			spogServer.checkErrorCode(response, errorCode);
		}
	}
	
	/**
	 * delete specified user source columns without login and check
	 *  @author shuo.zhang
	 * @param userId
	 */
	public void deleteUsersSourcesColumnsWithoutLoginAndCheck(String userId){
		
		errorHandle.printDebugMessageInDebugFile("******************deleteUsersSourcesColumnsWithoutLoginAndCheck*******************");
		Response response = source4SpogInvoker.deleteUsersSourcesColumnsWithoutLogin(userId);
		checkDeleteUsersSourcesColumns(response, SpogConstants.NOT_LOGGED_IN,  ErrorCode.AUTHORIZATION_HEADER_BLANK);
	}
	
	/**
	 * Create Source filter for logged in user and check the response, return the filterID;
	 * @author Zhaoguo.Ma
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
	public String createSourcefilterForLoggedinUserWithCheck(String filter_name, String protection_status,
			String connection_status, String protection_policy, String backup_status, String source_group,
			String operating_system, String site_id, String source_name, String source_type,
			String is_default, ExtentTest test) {

		String suffix = "";
		if (source_type!=null && source_type!="") {
			suffix = "_" + source_type;
		}
		
		Map<String, Object> filterInfo = jp.composeFilterInfo(filter_name, protection_status, connection_status,
				protection_policy, backup_status, source_group, operating_system, site_id, source_name, source_type,
				is_default);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!filterInfo.isEmpty()) {
	   		filter_info = jp.composeFilterInfo(filterType.source_filter.toString()+suffix,
	   				filterInfo.get("filter_name").toString(), "none", "none",
	   				(boolean)filterInfo.get("is_default"),(HashMap<String, Object>) filterInfo);
	   	}
//		Response response = source4SpogInvoker.createSourceFilterForLoggedinUser(filterInfo);
		Response response = spogServer.createFilters(source4SpogInvoker.getToken(), filter_info,"", test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		String userID = spogServer.GetLoggedinUser_UserID();
		String org_id = spogServer.GetOrganizationIDforUser(userID);

//		ArrayList<String> response_source_type = response.then().extract().path("data.source_type");
//	    if (source_type == null || source_type.equalsIgnoreCase("none")) {
//	    	spogServer.assertFilterItem("all", response_source_type);
//	    } else {
//	    	spogServer.assertFilterItem(source_type, response_source_type);
//	    }
//	    
		if ("true".equalsIgnoreCase(is_default)) {
			response.then().body("data.is_default", equalTo(true));
		} else {
			response.then().body("data.is_default", equalTo(false));
		}
		response.then().body("data.filter_name", equalTo(filter_name)).body("data.organization_id", equalTo(org_id))
				.body("data.user_id", equalTo(userID));

		ArrayList<String> response_protection_status = response.then().extract().path("data.protection_status");
		spogServer.assertFilterItem(protection_status, response_protection_status);

		ArrayList<String> response_connection_status = response.then().extract().path("data.connection_status");
		spogServer.assertFilterItem(connection_status, response_connection_status);

		ArrayList<String> response_protection_policy = response.then().extract().path("data.policy_id");
		spogServer.assertFilterItem(protection_policy, response_protection_policy);

		ArrayList<String> response_backup_status = response.then().extract().path("data.last_job");
		spogServer.assertFilterItem(backup_status, response_backup_status);

		ArrayList<String> response_source_group = response.then().extract().path("data.group_id");
		spogServer.assertFilterItem(source_group, response_source_group);

		ArrayList<String> response_operating_system = response.then().extract().path("data.operating_system");
		spogServer.assertFilterItem(operating_system, response_operating_system);

		ArrayList<String> response_site_id = response.then().extract().path("data.site_id");
		if (site_id != null && site_id.equalsIgnoreCase("")) {
			site_id = "emptyarray";
		}
		spogServer.assertFilterItem(site_id, response_site_id);

		String response_source_name = response.then().extract().path("data.source_name");
		spogServer.assertResponseItem(source_name, response_source_name);

		String filter_id = response.then().extract().path("data.filter_id");
		return filter_id;
	}
	
	/**
	 * create source filter for logged in user and check the response code / error code;
	 * @author Zhaoguo.Ma
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
	 * @param statusCode
	 * @param errorCode
	 * @param test
	 */
	public void createSourcefilterForLoggedinUserWithCodeCheck(String filter_name, String protection_status,
			String connection_status, String protection_policy, String backup_status, String source_group,
			String operating_system, String site_id, String source_name, String source_type,
			String is_default, int statusCode, String errorCode, ExtentTest test) {

		String suffix = "";
		if (source_type!=null && source_type!="" && !source_type.equalsIgnoreCase("all")) {
			suffix = "_" + source_type;
		}
		
		Map<String, Object> filterInfo = jp.composeFilterInfo(filter_name, protection_status, connection_status,
				protection_policy, backup_status, source_group, operating_system, site_id, source_name, source_type,
				is_default);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!filterInfo.isEmpty()) {
	   		filter_info = jp.composeFilterInfo(filterType.source_filter.toString()+suffix,
	   				filterInfo.get("filter_name").toString(), "none", "none",
	   				(boolean)filterInfo.get("is_default"),(HashMap<String, Object>) filterInfo);
	   	}

//		Response response = source4SpogInvoker.createSourceFilterForLoggedinUser(filterInfo);
		Response response = spogServer.createFilters(source4SpogInvoker.getToken(), filter_info, "", test);
		spogServer.checkResponseStatus(response, statusCode);
		spogServer.checkErrorCode(response, errorCode);
	}
	
	/**
	 * update source filter for logged in user and check the response;
	 * @author Zhaoguo.Ma 
	 * @param filterID
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
	public String updateSourcefilterForLoggedinUserWithCheck(String filterID, String filter_name, String protection_status,
			String connection_status, String protection_policy, String backup_status, String source_group,
			String operating_system, String site_id, String source_name, String source_type,
			String is_default, ExtentTest test) {
		String suffix = "";
		if (source_type!=null && source_type!="" && !source_type.equalsIgnoreCase("all")) {
			suffix = "_" + source_type;
		}
		Map<String, Object> sourceFilterInfo = jp.composeFilterInfo(filter_name, protection_status,
		        connection_status, protection_policy, backup_status, source_group, operating_system, source_type, is_default);
		    HashMap<String, Object> filter_info = new HashMap<String, Object>();
		   	if (!sourceFilterInfo.isEmpty()) {
		   		filter_info = jp.composeFilterInfo(filterType.source_filter.toString()+suffix,
		   				sourceFilterInfo.get("filter_name").toString(), "none", "none",
		   				(boolean)sourceFilterInfo.get("is_default"),(HashMap<String, Object>) sourceFilterInfo);
		   	}
		    Response response =spogServer.updateFilterById(source4SpogInvoker.getToken(), filterID, "none", filter_info, "", test);
		   /* Response response = spogInvoker.updateFilter(user_id, filter_id, filterInfo);*/
		    spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		    // TODO: add check points
		    String user_id = spogServer.GetLoggedinUser_UserID();
		    String org_id = spogServer.GetOrganizationIDforUser(user_id);
		    if (spogServer.CheckIsNullOrEmpty(filter_name)) {
		      response.then().body("data.organization_id", equalTo(org_id)).body("data.user_id",
		          equalTo(user_id));
		    } else {
		      response.then().body("data.filter_name", equalTo(filter_name))
		          .body("data.organization_id", equalTo(org_id)).body("data.user_id", equalTo(user_id));
		    }
		    spogServer.compareFilter(protection_status, connection_status, protection_policy, backup_status,
		        source_group, operating_system, response);
		    String return_filter_id = response.then().extract().path("data.filter_id");
		    return return_filter_id;
	}
	
	/**
	 * update source filter for logged in user and check the error code 
	 * @author Zhaoguo.Ma
	 * @param filterID
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
	 * @param statusCode
	 * @param errorCode
	 * @param test
	 */
	public void updateSourcefilterForLoggedinUserWithCodeCheck(String filterID, String filter_name, String protection_status,
			String connection_status, String protection_policy, String backup_status, String source_group,
			String operating_system, String site_id, String source_name, String source_type,
			String is_default, int statusCode, String errorCode, ExtentTest test) {
		String suffix = "";
		if (source_type!=null && source_type!="") {
			suffix = "_" + source_type;
		}
		Map<String, Object> sourceFilterInfo = jp.composeFilterInfo(filter_name, protection_status,
		        connection_status, protection_policy, backup_status, source_group, operating_system, source_type, is_default);
		    HashMap<String, Object> filter_info = new HashMap<String, Object>();
		   	if (!sourceFilterInfo.isEmpty()) {
		   		filter_info = jp.composeFilterInfo(filterType.source_filter.toString()+suffix,
		   				sourceFilterInfo.get("filter_name").toString(), "none", "none",
		   				(boolean)sourceFilterInfo.get("is_default"),(HashMap<String, Object>) sourceFilterInfo);
		   	}
		    Response response =spogServer.updateFilterById(source4SpogInvoker.getToken(), filterID, "none", filter_info, "", test);
		   /* Response response = spogInvoker.updateFilter(user_id, filter_id, filterInfo);*/
		    spogServer.checkResponseStatus(response, statusCode);
		    spogServer.checkErrorCode(response, errorCode);
	}
	
	/**
	 * Get specified user source columns
	 * @author shuo.zhang
	 * @param userId
	 * @param token
	 * @return
	 */
	public Response getUsersSourcesColumns(String userId, String token){
		
		errorHandle.printDebugMessageInDebugFile("******************getUsersSourcesColumns*******************");
		return source4SpogInvoker.getUsersSourcesColumns(userId, token);
	}
	

	/**
	 * Get specified user source columns with check
	 * @author shuo.zhang
	 * @param userId
	 * @param columnsList
	 * @param token
	 * @param statusCode
	 * @param errorCode
	 */
	public void getUsersSourcesColumnsWithCheck(String userId,  ArrayList<HashMap<String, Object>> columnsList, String token, int statusCode, String errorCode){
		
		errorHandle.printDebugMessageInDebugFile("******************getUsersSourcesColumnsWithCheck*******************");
		Response response = getUsersSourcesColumns(userId,token);
		checkUsersSourcesColumns(response, columnsList,token, statusCode,  errorCode);
	}
	
	/**
	 * Get specified user source columns without login and check
	 *  @author shuo.zhang
	 * @param userId
	 */
	public void getUsersSourcesColumnsWithoutLoginAndCheck(String userId){
		
		errorHandle.printDebugMessageInDebugFile("******************getUsersSourcesColumnsWithoutLoginAndCheck*******************");
		Response response = source4SpogInvoker.getUsersSourcesColumnsWithoutLogin(userId);
		checkUsersSourcesColumns(response, null, null, SpogConstants.NOT_LOGGED_IN,  ErrorCode.AUTHORIZATION_HEADER_BLANK);
		
	}
	
	/**
	 * create logged in user source columns
	 * @author shuo.zhang
	 * @param columnsList
	 * @param token
	 * @param columnsList 
	 * @return
	 */
	public Response createLoggedInUserSourcesColumns(ArrayList<HashMap<String, Object>> columnsList, String token ){

		errorHandle.printDebugMessageInDebugFile("******************createUserSourcesColumns*******************");

		Map<String, Object> columnsInfo = jp.getUsersSourcesColumnsInfo(columnsList );
		return source4SpogInvoker.createLoggedInUserSourcesColumns(columnsInfo, token);
	}
	
	/**
	 * create logged in user source columns with check
	 * @author shuo.zhang
	 * @param userId
	 * @param columnsList
	 * @param token
	 * @param statusCode
	 * @param errorCode
	 */
	public void createLoggedInUserSourcesColumnsWithCheck( ArrayList<HashMap<String, Object>> columnsList,String token, int statusCode, String errorCode){
		
		errorHandle.printDebugMessageInDebugFile("******************createLoggedInUserSourcesColumnsWithCheck*******************");
		Response response = createLoggedInUserSourcesColumns( columnsList, token );
		checkUsersSourcesColumns(response, columnsList,token, statusCode,  errorCode);
	
	}
	
	/**
	 * delete logged in user source columns
	 * @author shuo.zhang
	 * @param token
	 * @return
	 */
	public Response deleteLoggedInUserSourcesColumns( String token ){
		
		errorHandle.printDebugMessageInDebugFile("******************deleteLoggedInUserSourcesColumns*******************");
		return source4SpogInvoker.deleteLoggedInUserSourcesColumns(token);
	}
	
	/**
	 * delete logged in user source columns with check
	 * @author shuo.zhang
	 * @param token
	 * @param statusCode
	 * @param errorCode
	 */
	public void deleteLoggedInUserSourcesColumnsWithCheck(String token, int statusCode, String errorCode){
		
		errorHandle.printDebugMessageInDebugFile("******************deleteLoggedInUserSourcesColumnsWithCheck*******************");
		Response response = deleteLoggedInUserSourcesColumns( token);
		checkDeleteUsersSourcesColumns(response, statusCode, errorCode);
		
		
	}

	/**
	 * create logged in user source columns without login and check 
	 * @author shuo.zhang
	 */
	public void createLoggedInUserSourcesColumnsWithoutLoginAndCheck(){
		
		errorHandle.printDebugMessageInDebugFile("******************createLoggedInUserSourcesColumnsWithoutLoginAndCheck*******************");
		ArrayList<HashMap<String, Object>> columnsList = new ArrayList<HashMap<String, Object>>();
		Map<String, Object> columnsInfo = jp.getUsersSourcesColumnsInfo(columnsList );
		Response response = source4SpogInvoker.createLoggedInUserSourcesColumnsWithoutLogin(columnsInfo);
		checkUsersSourcesColumns(response, columnsList,null, SpogConstants.NOT_LOGGED_IN,  ErrorCode.AUTHORIZATION_HEADER_BLANK);
	
	}
	
	/**
	 * delete logged in user sources without login and check
	 * @author shuo.zhang
	 */
	public void deleteLoggedInUserSourcesColumnsWithoutLoginAndCheck(){
		
		errorHandle.printDebugMessageInDebugFile("******************deleteLoggedInUserSourcesColumnsWithoutLoginAndCheck*******************");
		Response response = source4SpogInvoker.deleteLoggedInUserSourcesColumnsWithoutLogin();
		checkDeleteUsersSourcesColumns(response, SpogConstants.NOT_LOGGED_IN,  ErrorCode.AUTHORIZATION_HEADER_BLANK);
	}

	
	/**
	 * update logged in user source columns
	 * @author shuo.zhang
	 * @param columnsList
	 * @param token
	 * @return
	 */
	public Response updateLoggedInUserSourcesColumns( ArrayList<HashMap<String, Object>> columnsList, String token ){

		errorHandle.printDebugMessageInDebugFile("******************updateLoggedInUserSourcesColumns*******************");
		Map<String, Object> columnsInfo = jp.getUsersSourcesColumnsInfo(columnsList );
		return source4SpogInvoker.updateLoggedInUserSourcesColumns(columnsInfo, token);
	}
	
	/**
	 * update logged in user source columns with check
	 * @author shuo.zhang
	 * @param columnsList
	 * @param token
	 * @param statusCode
	 * @param errorCode
	 */
	public void updateLoggedInUserSourcesColumnsWithCheck( ArrayList<HashMap<String, Object>> columnsList, String token, int statusCode, String errorCode ){
		
		errorHandle.printDebugMessageInDebugFile("******************updateLoggedInUserSourcesColumnsWithCheck*******************");
		Response response = updateLoggedInUserSourcesColumns(columnsList, token);
		checkUsersSourcesColumns(response, columnsList,token, statusCode,  errorCode);
	}
	
	/**
	 * update logged in user source columns without login and check
	 * @author shuo.zhang
	 */
	public void updateLoggedInUserSourcesColumnsWithoutLoginAndCheck( ){
		
		errorHandle.printDebugMessageInDebugFile("******************updateLoggedInUserSourcesColumnsWithoutLoginAndCheck*******************");
		ArrayList<HashMap<String, Object>> columnsList = new ArrayList<HashMap<String, Object>>();
		Map<String, Object> columnsInfo = jp.getUsersSourcesColumnsInfo(columnsList );
		
		Response response = source4SpogInvoker.updateLoggedInUserSourcesColumnsWithoutLogin(columnsInfo);
		checkUsersSourcesColumns(response, columnsList,null, SpogConstants.NOT_LOGGED_IN,  ErrorCode.AUTHORIZATION_HEADER_BLANK);
	}
	
	/**
	 * get logged in user source columns
	 * @author shuo.zhang
	 * @param token
	 * @return
	 */
	public Response getLoggedInUserSourcesColumns(String token){
		
		errorHandle.printDebugMessageInDebugFile("******************getLoggedInUserSourcesColumns*******************");
		return source4SpogInvoker.getLoggedInUserSourcesColumns(token);
	}
	
	/**
	 * get logged in user source columns with check
	 * @author shuo.zhang
	 * @param columnsList
	 * @param token
	 * @param statusCode
	 * @param errorCode
	 */
	public void getLoggedInUserSourcesColumnsWithCheck(ArrayList<HashMap<String, Object>> columnsList, String token, int statusCode, String errorCode){
		
		errorHandle.printDebugMessageInDebugFile("******************getLoggedInUserSourcesColumnsWithCheck*******************");
		Response response = getLoggedInUserSourcesColumns(token);
		checkUsersSourcesColumns(response, columnsList,token, statusCode,  errorCode);
	}
	
	/**
	 * get logged in user source columns without login and check
	 * @author shuo.zhang
	 */
	public void getLoggedInUserSourcesColumnsWithoutLoginAndCheck(){
		
		errorHandle.printDebugMessageInDebugFile("******************getLoggedInUserSourcesColumnsWithoutLoginAndCheck*******************");
		ArrayList<HashMap<String, Object>> columnsList = new ArrayList<HashMap<String, Object>>();
		
		Response response = source4SpogInvoker.getLoggedInUserSourcesColumnsWithoutLogin();
		checkUsersSourcesColumns(response, columnsList	,null, SpogConstants.NOT_LOGGED_IN,  ErrorCode.AUTHORIZATION_HEADER_BLANK);
	}
	

	/**
	 * get the default hypervisor columns
	 * @author yuefen.liu
	 */
	public Response getDefaultHypervisorColumns(String token, ExtentTest test) {

		  Response response = source4SpogInvoker.getDefaultHypervisorColumns(token);
//		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		  return response;
	}
	/**
	 * get the default hypervisor columns and check
	 * @author yuefen.liu
	 */  
	public void getDefaultHypervisorColumnsWithCheck(String token, ExtentTest test) {
		
		    Response response = source4SpogInvoker.getDefaultHypervisorColumns(token);
		    spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		    
		    ArrayList<HashMap> expectedColumnsHeadContents = new ArrayList<HashMap>();
		    HashMap<String, String> columnHeadContent1 = new HashMap();
		    columnHeadContent1.put("column_id", HypervisorColumnConstants.NAME_COLUMNID);
		    columnHeadContent1.put("long_label", HypervisorColumnConstants.NAME_LONG);
		    columnHeadContent1.put("short_label", HypervisorColumnConstants.NAME_SHORT);
		    columnHeadContent1.put("key", HypervisorColumnConstants.NAME_KEY);
//		    columnHeadContent1.put("sort", HypervisorColumnConstants.NAME_SORT);
//		    columnHeadContent1.put("filter", HypervisorColumnConstants.NAME_FILTER);
//		    columnHeadContent1.put("visible", HypervisorColumnConstants.NAME_VISIBLE);
//		    columnHeadContent1.put("order_id",HypervisorColumnConstants.NAME_ORDERID);
		    expectedColumnsHeadContents.add(columnHeadContent1);
		    
		    HashMap<String, String> columnHeadContent2 = new HashMap();
		    columnHeadContent2.put("column_id", HypervisorColumnConstants.STATUS_COLUMNID);
		    columnHeadContent2.put("long_label", HypervisorColumnConstants.STATUS_LONG);
		    columnHeadContent2.put("short_label", HypervisorColumnConstants.STATUS_SHORT);
		    columnHeadContent2.put("key", HypervisorColumnConstants.STATUS_KEY);
//		    columnHeadContent2.put("sort", HypervisorColumnConstants.STATUS_SORT);
//		    columnHeadContent2.put("filter", HypervisorColumnConstants.STATUS_FILTER);
//		    columnHeadContent2.put("visible", HypervisorColumnConstants.STATUS_VISIBLE);
//		    columnHeadContent2.put("order_id",HypervisorColumnConstants.STATUS_ORDERID);
		    expectedColumnsHeadContents.add(columnHeadContent2);
		    
		    HashMap<String, String> columnHeadContent3 = new HashMap();
		    columnHeadContent3.put("column_id", HypervisorColumnConstants.TYPE_COLUMNID);
		    columnHeadContent3.put("long_label", HypervisorColumnConstants.TYPE_LONG);
		    columnHeadContent3.put("short_label", HypervisorColumnConstants.TYPE_SHORT);
		    columnHeadContent3.put("key", HypervisorColumnConstants.TYPE_KEY);
//		    columnHeadContent3.put("sort", HypervisorColumnConstants.TYPE_SORT);
//		    columnHeadContent3.put("filter", HypervisorColumnConstants.TYPE_FILTER);
//		    columnHeadContent3.put("visible", HypervisorColumnConstants.TYPE_VISIBLE);
//		    columnHeadContent3.put("order_id",HypervisorColumnConstants.TYPE_ORDERID);
		    expectedColumnsHeadContents.add(columnHeadContent3);
		    
		    HashMap<String, String> columnHeadContent4 = new HashMap();
		    columnHeadContent4.put("column_id", HypervisorColumnConstants.DC_COLUMNID);
		    columnHeadContent4.put("long_label", HypervisorColumnConstants.DC_LONG);
		    columnHeadContent4.put("short_label", HypervisorColumnConstants.DC_SHORT);
		    columnHeadContent4.put("key", HypervisorColumnConstants.DC_KEY);
//		    columnHeadContent4.put("sort", HypervisorColumnConstants.DC_SORT);
//		    columnHeadContent4.put("filter", HypervisorColumnConstants.DC_FILTER);
//		    columnHeadContent4.put("visible", HypervisorColumnConstants.DC_VISIBLE);
//		    columnHeadContent4.put("order_id",HypervisorColumnConstants.DC_ORDERID);
		    expectedColumnsHeadContents.add(columnHeadContent4);
		    
		    HashMap<String, String> columnHeadContent5 = new HashMap();
		    columnHeadContent5.put("column_id", HypervisorColumnConstants.HOST_COLUMNID);
		    columnHeadContent5.put("long_label", HypervisorColumnConstants.HOST_LONG);
		    columnHeadContent5.put("short_label", HypervisorColumnConstants.HOST_SHORT);
		    columnHeadContent5.put("key", HypervisorColumnConstants.HOST_KEY);
//		    columnHeadContent5.put("sort", HypervisorColumnConstants.HOST_SORT);
//		    columnHeadContent5.put("filter", HypervisorColumnConstants.HOST_FILTER);
//		    columnHeadContent5.put("visible", HypervisorColumnConstants.HOST_VISIBLE);
//		    columnHeadContent5.put("order_id",HypervisorColumnConstants.HOST_ORDERID);
		    expectedColumnsHeadContents.add(columnHeadContent5);
		    
		    HashMap<String, String> columnHeadContent6 = new HashMap();
		    columnHeadContent6.put("column_id", HypervisorColumnConstants.ENV_COLUMNID);
		    columnHeadContent6.put("long_label", HypervisorColumnConstants.ENV_LONG);
		    columnHeadContent6.put("short_label", HypervisorColumnConstants.ENV_SHORT);
		    columnHeadContent6.put("key", HypervisorColumnConstants.ENV_KEY);
//		    columnHeadContent6.put("sort", HypervisorColumnConstants.ENV_SORT);
//		    columnHeadContent6.put("filter", HypervisorColumnConstants.ENV_FILTER);
//		    columnHeadContent6.put("visible", HypervisorColumnConstants.ENV_VISIBLE);
//		    columnHeadContent6.put("order_id",HypervisorColumnConstants.ENV_ORDERID);
		    expectedColumnsHeadContents.add(columnHeadContent6);
		    
		    HashMap<String, String> columnHeadContent7 = new HashMap();
		    columnHeadContent7.put("column_id", HypervisorColumnConstants.ORG_COLUMNID);
		    columnHeadContent7.put("long_label", HypervisorColumnConstants.ORG_LONG);
		    columnHeadContent7.put("short_label", HypervisorColumnConstants.ORG_SHORT);
		    columnHeadContent7.put("key", HypervisorColumnConstants.ORG_KEY);
//		    columnHeadContent7.put("sort", HypervisorColumnConstants.ORG_SORT);
//		    columnHeadContent7.put("filter", HypervisorColumnConstants.ORG_FILTER);
//		    columnHeadContent7.put("visible", HypervisorColumnConstants.ORG_VISIBLE);
//		    columnHeadContent7.put("order_id",HypervisorColumnConstants.ORG_ORDERID);
		    expectedColumnsHeadContents.add(columnHeadContent7);
		    
		    HashMap<String, String> columnHeadContent8 = new HashMap();
		    columnHeadContent8.put("column_id", HypervisorColumnConstants.AO_COLUMNID);
		    columnHeadContent8.put("long_label", HypervisorColumnConstants.AO_LONG);
		    columnHeadContent8.put("short_label", HypervisorColumnConstants.AO_SHORT);
		    columnHeadContent8.put("key", HypervisorColumnConstants.AO_KEY);
//		    columnHeadContent8.put("sort", HypervisorColumnConstants.AO_SORT);
//		    columnHeadContent8.put("filter", HypervisorColumnConstants.AO_FILTER);
//		    columnHeadContent8.put("visible", HypervisorColumnConstants.AO_VISIBLE);
//		    columnHeadContent8.put("order_id",HypervisorColumnConstants.AO_ORDERID);
		    expectedColumnsHeadContents.add(columnHeadContent8);
		    
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
		      if (expectedColumnsHeadContent.get("column_id").equals(HeadContent.get("column_id"))
		          && expectedColumnsHeadContent.get("long_label").equals(HeadContent.get("long_label"))
		          && expectedColumnsHeadContent.get("short_label").equals(HeadContent.get("short_label"))
		          && expectedColumnsHeadContent.get("key").equals(HeadContent.get("key"))) {
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
	  public void CompareHypervisorColumns(Response response,
		      ArrayList<HashMap<String,Object>> expectedColumns, ExtentTest test) {

		    ArrayList<HashMap<String,Object>> responseColumns = new ArrayList<HashMap<String,Object>>();
		    responseColumns = response.then().extract().path("data");
		    int length = expectedColumns.size();
		    if (length != responseColumns.size()) {
		      assertTrue("compare columns number is " + length, false);
		      test.log(LogStatus.FAIL, "compare columns number");
		    }

		    for (int i = 0; i < length; i++) {
		      HashMap expectedColumn = expectedColumns.get(i);
		      HashMap responseColumn = responseColumns.get(i);
		      if (expectedColumn.get("column_id").equals(responseColumn.get("column_id"))
		          && expectedColumn.get("visible").equals(responseColumn.get("visible"))
		          && expectedColumn.get("order_id").equals(responseColumn.get("order_id"))) {
		        assertTrue("compare column passed", true);
		        test.log(LogStatus.INFO, "compare column passed");

		      } else {
		        assertTrue("compare column faied", false);
		        test.log(LogStatus.FAIL, "compare column fail");
		        test.log(LogStatus.FAIL, "expected user info: " + expectedColumn.toString());
		        test.log(LogStatus.FAIL, "actual user info: " + responseColumn.toString());
		        System.out.print(expectedColumn.toString());
		        System.out.print(responseColumn.toString());
		        break;
		      }
		    }
		  }
	/**
	 * create logged in user hypervisor columns
	 * @author yuefen.liu
	 * @param columnsList
	 * @return
	 */
	public Response createHypervisorColumnsForLoggedInUser(ArrayList<HashMap<String, Object>> columnsList, String token){

		errorHandle.printDebugMessageInDebugFile("******************createHpervisorColumnsForLoggedInUser*******************");

		Map<String, Object> columnsInfo = jp.getHypervisorColumnsInfo(columnsList);
		Response response = source4SpogInvoker.createHypervisorColumnsForLoggedInUser(columnsInfo, token);
		return response;
	}
	/**
	 * create hypervisor columns for specified user
	 * @author yuefen.liu
	 * @param columnsList
	 * @param userId
	 * @return
	 */
	public Response createHypervisorColumnsForSpecifiedUser(String userId, ArrayList<HashMap<String, Object>> columnsList, String token){

		errorHandle.printDebugMessageInDebugFile("******************createHpervisorColumnsForLoggedInUser*******************");

		Map<String, Object> columnsInfo = jp.getHypervisorColumnsInfo(columnsList);
		Response response = source4SpogInvoker.createHypervisorColumnsForSpecifiedUser(userId, columnsInfo, token);
		return response;
	}
	/**
	 * get user hypervisor column array list, it is used for preparing column info
	 * @author yuefen.liu
	 * @param specifiedHypervisorColumns  
	 * column_id;visible;order_id
	 * ex:"31dfe327-b9fe-432a-a119-24b584a85263;true;1,d065d8a3-699d-4c14-834d-65953942e778;true;2,023283e9-0027-4f28-a260-573eed6b1b62;false;3"
	 * @param token
	 * @return
	 */
	public ArrayList<HashMap<String, Object>>  getHypervisorColumnArrayList( String specifiedHypervisorColumns, String token){
		
		errorHandle.printDebugMessageInDebugFile("******************getSourceColumnArrayList*******************");
		spogServer.setToken(token);

		ArrayList<HashMap<String, Object>> columnsList = new ArrayList<HashMap<String, Object>> ();
		
		String[] specifiedHypervisorColumnArray = specifiedHypervisorColumns.split(",");
		
		for(int i=0;i<specifiedHypervisorColumnArray.length; i++) {
			String eachSpecifiedHypervisorColumn = specifiedHypervisorColumnArray[i];
			String[] eachSpecifiedHypervisorColumnDetailInfo = eachSpecifiedHypervisorColumn.split(";");
			HashMap<String, Object> hypervisorColumnMap = getHypervisorColumnInfo(eachSpecifiedHypervisorColumnDetailInfo[0],
				 eachSpecifiedHypervisorColumnDetailInfo[1],  eachSpecifiedHypervisorColumnDetailInfo[2]);
			columnsList.add(hypervisorColumnMap);
		}
		
		return columnsList;
		 
	}
	/**
	 * set hypervisor column info
	 * @author yuefen.liu
	 * @param column_id
	 * @param visible
	 * @param order_id
	 * @return
	 */
	public HashMap<String, Object> getHypervisorColumnInfo(String column_id, String visible, String order_id){
		
		HashMap<String, Object> hypervisorColumnInfo = new HashMap<String, Object> ();
		hypervisorColumnInfo.put("column_id", column_id);
		if(!visible.equalsIgnoreCase("none")){
			hypervisorColumnInfo.put("visible", Boolean.valueOf(visible));
		}
		if(!order_id.equalsIgnoreCase("none")){
			hypervisorColumnInfo.put("order_id", Integer.valueOf(order_id));		
		}
	
		return hypervisorColumnInfo;
	}
	/**
	 * delete hypervisor columns for logged in user
	 * @author yuefen.liu
	 * @param token
	 * @return
	 */
	public Response deleteHypervisorColumnsForLoggedInUser( String token ){
		
		errorHandle.printDebugMessageInDebugFile("******************deleteHypervisorColumnsForLoggedInUser*******************");
		Response response= source4SpogInvoker.deleteHypervisorColumnsForLoggedInUser(token);
		return response;
	}
	
	/**
	 * delete hypervisor columns for specified user
	 *  @author yuefen.liu
	 * @param userId
	 * @param token
	 * @return
	 */
	public Response deleteHypervisorColumnsForSpecifiedUser(String userId, String token ){
		
		errorHandle.printDebugMessageInDebugFile("******************deleteHypervisorColumnsForSpecifiedUser*******************");
		Response response = source4SpogInvoker.deleteHypervisorColumnsForSpecifiedUser(userId, token);
		return response;
	}
	/**
	 * get logged in user hypervisor columns
	 * @author yuefen.liu
	 * @param token
	 * @return
	 */
	public Response getHypervisorColumnsForLoggedInUser(String token){
		
		errorHandle.printDebugMessageInDebugFile("******************getHypervisorColumnsForLoggedInUser*******************");
		Response response = source4SpogInvoker.getHypervisorColumnsForLoggedInUser(token);
		return response;
	}
	/**
	 * get specified user hypervisor columns
	 * @author yuefen.liu
	 * @param userId
	 * @param token
	 * @return
	 */
	public Response getHypervisorColumnsForSpecifiedUser(String userId, String token){
		
		errorHandle.printDebugMessageInDebugFile("******************getHypervisorColumnsForSpecifiedUser*******************");
		Response response = source4SpogInvoker.getHypervisorColumnsForSpecifiedUser(userId, token);
		return response;
	}
	/**
	 * update hypervisor columns for logged in user
	 * @author yuefen.liu
	 * @param columnsList
	 * @param token
	 * @return
	 */
	public Response updateHypervisorColumnsForLoggedInUser(ArrayList<HashMap<String, Object>> columnsList, String token ){

		errorHandle.printDebugMessageInDebugFile("******************updateHypervisorColumnsForLoggedInUser*******************");
		Map<String, Object> columnsInfo = jp.getHypervisorColumnsInfo(columnsList);
		Response response= source4SpogInvoker.updateHypervisorColumnsForLoggedInUser(columnsInfo, token);
		return response;
	}
	/**
	 * update specified hypervisor columns
	 * @author yuefen.liu
	 * @param userId
	 * @param columnsList
	 * @param token
	 * @return
	 */
	public Response updateHypervisorColumnsForSpecifiedUser(String userId,  ArrayList<HashMap<String, Object>> columnsList, String token ){

		errorHandle.printDebugMessageInDebugFile("******************updateHypervisorColumnsForSpecifiedUser*******************");
		Map<String, Object> columnsInfo = jp.getHypervisorColumnsInfo(columnsList);
		Response response= source4SpogInvoker.updateHypervisorColumnsForSpecifiedUser(userId, columnsInfo, token);
		return response;
	}
	
	/**
	 * getSourcesTypesAmountByOrgId
	 * @author shuo.zhang
	 * 
	 * @param orgId
	 * @param token
	 * @return
	 */
	public Response getSourcesTypesAmountByOrgId(String orgId, String token ){
		
		errorHandle.printDebugMessageInDebugFile("******************getSourcesTypesAmountByOrgId*******************");
		return source4SpogInvoker.getSourcesTypesAmountByOrgId(orgId, token);
	}
	
	/**
	 * check response of getSourcesTypesAmountByOrgId
	 * @author shuo.zhang
	 * @param response
	 * @param expectedStatusCode
	 * @param expectedErrorCode
	 * @param expectedResult
	 */
	public void checkGetSourcesTypesAmountByOrgId(Response response, int expectedStatusCode, String expectedErrorCode,
			List<	HashMap<String, Object> >expectedResult){
		
		errorHandle.printDebugMessageInDebugFile("******************checkGetSourcesTypesAmountByOrgId*******************");
		response.then().statusCode(expectedStatusCode);
		if(expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE){
			List<	HashMap<String, Object> > dataList = response.jsonPath().getList("data");
			for(int j=0; j< expectedResult.size(); j++){
				String expectedSourceType = expectedResult.get(j).get("source_type").toString();
				boolean find = false;
				for(int i=0; i< dataList.size(); i++){
					HashMap<String, Object> sourceTypeMap = dataList.get(i);
					String sourceType = sourceTypeMap.get("source_type").toString();								
					if(expectedSourceType.equalsIgnoreCase(sourceType)){
						find = true;
						assertEquals((int)expectedResult.get(j).get("amount"),(int)sourceTypeMap.get("amount") );
						break;
					}
				}
			
				if(!find){
					assertFalse( true, "find the source type in result");	
				}
			}
		
		}else{
	    	spogServer.checkErrorCode(response,expectedErrorCode);
		}
	}
	
	/**
	 * @author shuo.zhang
	 * @param orgId
	 * @return
	 */
	public Response getSourcesTypesAmountByOrgIdWithoutLogin(String orgId ){
		
		errorHandle.printDebugMessageInDebugFile("******************getSourcesTypesAmountByOrgIdWithoutLogin*******************");
		return source4SpogInvoker.getSourcesTypesAmountByOrgIdWithoutLogin(orgId);
		
	}
	
	
	/**
	 * get amount for VSB and IVM source and check
	 * @author leiyu.wang
	 * @param IVMCount
	 * @param VSBCount
	 * @param test
	 */
	public void getAmountForVSBAndIVMSourceWithCheck(String orgID, int IVMCount, int VSBCount, ExtentTest test){
		test.log(LogStatus.INFO, "get amount for VSB and IVM source");
		Response response=source4SpogInvoker.getAmountForSourceType(orgID);
		
		ArrayList<Integer> amount= response.then().extract().path("data.amount");
		if(amount.size()==2){
			if(amount.get(0)==IVMCount&&amount.get(1)==VSBCount)
				test.log(LogStatus.INFO, "verify the IVM/VSB source count is correct");
			else
				test.log(LogStatus.FAIL, "verify the IVM/VSB source count is not correct");
		}else
			test.log(LogStatus.FAIL, "fail to get VSB/IVM source count");
	}
	
	
	/**
	 * get amount for VSB and IVM fail case
	 * @author leiyu.wang
	 * @param statusCode
	 * @param test
	 */
	public void getAmountForVSBAndIVMSourceFail(String orgID, int statusCode,ExtentTest test ){
		test.log(LogStatus.INFO, "get amount for VSB and IVM source");
		Response response=source4SpogInvoker.getAmountForSourceType(orgID);
		spogServer.checkResponseStatus(response, statusCode);				
	}
	
	
	/**
	 * get source group mount with check
	 * @author leiyu.wang
	 * @param amount
	 * @param test
	 */
	public void getSourceGroupAmountWithCheck(int amount, ExtentTest test){
		test.log(LogStatus.INFO, "get amount for source group");
		Response response= source4SpogInvoker.getSourceGroupAmount();
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		int count=response.then().extract().path("data.amount");
		if(amount==count)
			test.log(LogStatus.INFO, "verify source group amount is correct");
		else
			test.log(LogStatus.FAIL, "verify source group amount is not correct");		
	}
	
	/**
	 * get source group amount fail
	 * @author leiyu.wang
	 * @param statusCode
	 * @param test
	 */
	public void getSourceGroupAmountFail(int statusCode, ExtentTest test){
		test.log(LogStatus.INFO, "get amount for source group");
		Response response= source4SpogInvoker.getSourceGroupAmount();
		spogServer.checkResponseStatus(response, statusCode);		
	}
	
	/**
	 * @author Zhaoguo.Ma
	 * @param sourceID
	 * @param test
	 * @return
	 */
	public Response getRecoveryImageFormats(String sourceID, ExtentTest test) {
		test.log(LogStatus.INFO, "get recovery image formats for source");
		Response response = source4SpogInvoker.getRecoveryImageFormats(sourceID);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		return response;
		
	}
	
	public void getRecoveryImageFormatsWithErrorCheck(String sourceID, int statusCode, String errorCode, ExtentTest test) {
		test.log(LogStatus.INFO, "get recovery image formats for source");
		Response response = source4SpogInvoker.getRecoveryImageFormats(sourceID);
		spogServer.checkResponseStatus(response, statusCode);
		spogServer.checkErrorCode(response, errorCode);	
	}
	
	/**
	 * 
	 * @author Zhaoguo.Ma
	 * @param sourceID
	 * @param taskType
	 * @param imageType
	 * @param test
	 * @return
	 */
	public Response getRecoveryTargets(String sourceID, String taskType, String imageType, ExtentTest test) {
		if (taskType==null) taskType = "";
		if (imageType==null) imageType = "";
		
		Response response = source4SpogInvoker.getRecoveryTargets(sourceID, taskType, imageType);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		return response;
	}
	
	public Response getRecoveryTargets(String filterStr, String sortStr, int pageNumber, int pageSize, String sourceID, String taskType, String imageType, ExtentTest test) {
		if (taskType==null) taskType = "";
		if (imageType==null) imageType = "";
		
		Response response = source4SpogInvoker.getRecoveryTargets(sourceID, taskType, imageType, spogServer.getUrl4FilterSortPaging(filterStr, sortStr, pageNumber, pageSize, test));
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		return response;
	}
	
	public void getRecoveryTargetsWithErrorCheck(String sourceID, String taskType, String imageType, int statusCode, String errorCode, ExtentTest test) {
		if (taskType==null) taskType = "";
		if (imageType==null) imageType = "";
		
		Response response = source4SpogInvoker.getRecoveryTargets(sourceID, taskType, imageType);
		spogServer.checkResponseStatus(response, statusCode);
		spogServer.checkErrorCode(response, errorCode);
	}
	
	/**
	 * @author Zhaoguo.Ma
	 * @param from_source_id
	 * @param task_type
	 * @param from_path
	 * @param snapshot_host
	 * @param snapshot_path
	 * @param to_source_id
	 * @param to_path
	 * @param image_format
	 * @return
	 */
	public Map<String, Object> composeCDRecoverjobInfo(String from_source_id, String task_type, String policy_type, String from_path, String snapshot_host, 
			String snapshot_path, String to_source_id, String to_path, String image_format) {
		Map<String, Object> jobInfo = new HashMap<String, Object>();
		
		if (from_source_id == null || from_source_id == "") {
			jobInfo.put("from_source_id", from_source_id);
		} else if (!from_source_id.equalsIgnoreCase("none")) {
			jobInfo.put("from_source_id", from_source_id);
		}
		
		if (task_type == null || task_type == "") {
			jobInfo.put("task_type", task_type);
		} else if (!task_type.equalsIgnoreCase("none")) {
			jobInfo.put("task_type", task_type);
		}
		
		if (policy_type == null || policy_type == "") {
			jobInfo.put("policy_type", policy_type);
		} else if (!policy_type.equalsIgnoreCase("none")) {
			jobInfo.put("policy_type", policy_type);
		}
		
		if (from_path == null || from_path == "") {
			jobInfo.put("from_path", from_path);
		} else if (!from_path.equalsIgnoreCase("none")) {
			jobInfo.put("from_path", from_path);
		}
		
		if (snapshot_host == null || snapshot_host == "") {
			jobInfo.put("snapshot_host", snapshot_host);
		} else if (!snapshot_host.equalsIgnoreCase("none")) {
			jobInfo.put("snapshot_host", snapshot_host);
		}
		
		if (snapshot_path == null || snapshot_path == "") {
			jobInfo.put("snapshot_path", snapshot_path);
		} else if (!snapshot_path.equalsIgnoreCase("none")) {
			jobInfo.put("snapshot_path", snapshot_path);
		}
		
		if (to_source_id == null || to_source_id == "") {
			jobInfo.put("to_source_id", to_source_id);
		} else if (!to_source_id.equalsIgnoreCase("none")) {
			jobInfo.put("to_source_id", to_source_id);
		}
		
		if (to_path == null || to_path == "") {
			jobInfo.put("to_path", to_path);
		} else if (!to_path.equalsIgnoreCase("none")) {
			jobInfo.put("to_path", to_path);
		}
		
		if (image_format == null || image_format == "") {
			jobInfo.put("image_format", image_format);
		} else if (!image_format.equalsIgnoreCase("none")) {
			jobInfo.put("image_format", image_format);
		}
		
		return jobInfo;
	}
	
	public void submitCDRecoveryJob(String source_id, String from_source_id, String task_type, String policy_type, String from_path, String snapshot_host, 
			String snapshot_path, String to_source_id, String to_path, String image_format, ExtentTest test) {
		Map<String, Object> jobInfo = composeCDRecoverjobInfo(from_source_id, task_type, policy_type, from_path, snapshot_host, snapshot_path, to_source_id, to_path, image_format);
		Response response = source4SpogInvoker.submitCDRecoveryJob(source_id, jobInfo);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);
	}
	
	public void cancelCDRecoveryJob(String source_id, String org_id,ExtentTest test) {
		Response response = source4SpogInvoker.cancelCDRecoveryJob(source_id, org_id);
		//spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		spogServer.checkResponseStatus(response, 200);
	}
	
	public void removeSource(String source_id,ExtentTest test) {
		Response response = source4SpogInvoker.removeSource(source_id);
		//spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
	}
	
	public Response getRecoveryPoints(String organization_id, String source_id, ExtentTest test) {
		RestAssured.useRelaxedHTTPSValidation();
		Response response = given().auth().preemptive().basic("admin@zetta.net","Zetta1234").header("Content-Type", "application/json")
				 .when().get("https://10.61.4.26/cloudconsole/recoverypoints?organization_id=" + organization_id + "&source_id=" + source_id);
		response.then().log().all();	
		return response;
	}
	
	public String submitCDBackupJob(String sourceId, boolean waitForJobStart, boolean waitForJobComplete, ExtentTest test)
			throws InterruptedException {
		Response response = spogServer.getJobsBySourceID(sourceId,
				"source_id;=;" + sourceId + ",job_type;=;backup_incremental", "start_time_ts;desc", 1, 1, true, test);
		String lastJobId = "";
		ArrayList<String> existingJob = response.then().extract().path("data.job_id");
		if (existingJob.size() != 0) {
			lastJobId = existingJob.get(0);
		}
		String job_id=null;
		submitBackupForSource(sourceId);

		if (false == waitForJobStart) {
			System.out.println("job submitted. do not need to wait for job start, thus return.");
			test.log(LogStatus.INFO, "job submitted. do not need to wait for job start, thus return.");
			return null;
		} else {
			int countStart = 0;
			while (countStart < 20) {
				response = spogServer.getJobsBySourceID(sourceId, "job_type;=;backup_incremental", "start_time_ts;desc",
						1, 1, true, test);
				ArrayList<String> currentJob = response.then().extract().path("data.job_id");
				if (currentJob.size() != 0 && !currentJob.get(0).equals(lastJobId)) {
					// job started
					job_id=currentJob.get(0).toString();
					System.out.println("job started");
					test.log(LogStatus.INFO, "job started");
					if (false == waitForJobComplete) {
						System.out.println("job started, and do not need to wait for job completed, thus return.");
						test.log(LogStatus.INFO,
								"job started, and do not need to wait for job completed, thus return.");
						return job_id;
					} else {
						int countFinished = 0;
						while (countFinished < 100) {
							response = spogServer.getJobById(currentJob.get(0), test);
							String job_status = response.then().extract().path("data.job_status");
							if (job_status.equalsIgnoreCase("finished")) {
								System.out.println("job finished");
								test.log(LogStatus.INFO, "job completed.");
								return job_id;
							} else if (!job_status.equalsIgnoreCase("active")) {
								System.out.println("unexpected job status");
								test.log(LogStatus.ERROR, "unexpected job status.");
								return job_id;
							}
							test.log(LogStatus.INFO, "job in progress... " + countFinished + " time(s)");
							System.out.println("job in progress... " + countFinished + " time(s)");
							Thread.sleep(30000);
							countFinished++;
						}
						test.log(LogStatus.ERROR, "job does not finish in 50 minutes.");
						System.out.println("job does not finish in 50 minutes.");
						assertTrue("job not finished in 50 minutes", false);
					}
				}
				test.log(LogStatus.INFO, "job is not started... " + countStart + " time(s)");
				System.out.println("job not started... " + countStart + " time(s)");
				Thread.sleep(30000);
				countStart++;
			}
			test.log(LogStatus.ERROR, "job is not launched in 10 minutes.");
			System.out.println("job not started in 10 minutes");
			assertTrue("job not started in 10 minutes", false);
		}
		return job_id;
	}
	
	public String cancelCDBackupJob(String sourceId, boolean waitForJobCancel, ExtentTest test)
			throws InterruptedException {
		Response response = spogServer.getJobsBySourceID(sourceId,
				"source_id;=;" + sourceId + ",job_type;=;backup_incremental", "start_time_ts;desc", 1, 1, true, test);
		String lastJobId = "";
		ArrayList<String> existingJob = response.then().extract().path("data.job_id");
		if (existingJob.size() != 0) {
			lastJobId = existingJob.get(0);
		}
		String job_id=null;
		submitBackupForSource(sourceId);

		int countStart = 0;
		while (countStart < 200) {
			response = spogServer.getJobsBySourceID(sourceId, "job_type;=;backup_incremental", "start_time_ts;desc",
					1, 1, true, test);
			ArrayList<String> currentJob = response.then().extract().path("data.job_id");
			if (currentJob.size() != 0 && !currentJob.get(0).equals(lastJobId)) {
				// job started
				job_id=currentJob.get(0).toString();
				System.out.println("job status is:"+response.then().extract().path("data.job_status"));
				test.log(LogStatus.INFO, "backup job started");
				cancelBackupForSource(sourceId);
				if (false == waitForJobCancel) {
					System.out.println("job is cancelled, and do not need to wait for job cancelled completely, thus return.");
					test.log(LogStatus.INFO,
							"job is cancelled, and do not need to wait for job cancelled completely, thus return.");
					return job_id;
				} else {
					int countFinished = 0;
					while (countFinished < 1000) {
						response = spogServer.getJobById(currentJob.get(0), test);
						String job_status = response.then().extract().path("data.job_status");
						if (job_status.equalsIgnoreCase("cancelled")) {
							System.out.println("job cancelled");
							test.log(LogStatus.INFO, "job cancelled completed.");
							return job_id;
						} else if (!job_status.equalsIgnoreCase("active")) {
							System.out.println("unexpected job status");
							test.log(LogStatus.ERROR, "unexpected job status.");
							return job_id;
						}
						test.log(LogStatus.INFO, "job in progress... " + countFinished + " time(s)");
						System.out.println("job in progress... " + countFinished + " time(s)");
						Thread.sleep(3000);
						countFinished++;
					}
					test.log(LogStatus.ERROR, "job does not cancel in 50 minutes.");
					System.out.println("job does not cancel in 50 minutes.");
					assertTrue("job not cancel in 50 minutes", false);
				}
			}
			test.log(LogStatus.INFO, "job is not started... " + countStart + " time(s)");
			System.out.println("job not started... " + countStart + " time(s)");
			Thread.sleep(3000);
			countStart++;
		}
		test.log(LogStatus.ERROR, "job is not launched in 10 minutes.");
		System.out.println("job not started in 10 minutes");
		assertTrue("job not started in 10 minutes", false);
		
		return job_id;
	}

	public String submitCDBackupJobAndWaitForRecoveryPoint(String sourceId, ExtentTest test) throws InterruptedException {
		test.log(LogStatus.INFO, "get last recovery points");
		Response response = getRecoveryPoints(spogServer.GetLoggedinUserOrganizationID(), sourceId, test);
		ArrayList<Integer> existingRecoveryPoints = response.then().extract().path("data.recovery_point_id");
		int latestRecoveryPoint = 0;
		
		if (existingRecoveryPoints.size()!=0) {
			latestRecoveryPoint = existingRecoveryPoints.get(0);
		}
		test.log(LogStatus.INFO, "latest recovery point id is " + latestRecoveryPoint + " before backup");
		System.out.println("latest recovery point id is " + latestRecoveryPoint + " before backup");
		String job_id=null;
		job_id=submitCDBackupJob(sourceId, true, true, test);

		test.log(LogStatus.INFO, "backup job completed, now wait for the recovery point ready");
		System.out.println("backup job completed, now wait for the recovery point ready");
		int recoveryPointCount = 0;
		while (recoveryPointCount < 20) {
			response = getRecoveryPoints(spogServer.GetLoggedinUserOrganizationID(), sourceId, test);
			existingRecoveryPoints = response.then().extract().path("data.recovery_point_id");
			if (existingRecoveryPoints.size()!=0 && existingRecoveryPoints.get(0) != latestRecoveryPoint) {
				test.log(LogStatus.INFO,
						"Recovery point is ready, and recovery point id is " + existingRecoveryPoints.get(0));
				System.out
						.println("Recovery point is ready, and recovery point id is " + existingRecoveryPoints.get(0));
				return job_id;
			}
			test.log(LogStatus.INFO, "Recovery point is not ready yet, " + recoveryPointCount + " time(s)");
			System.out.println("Recovery point is not ready yet, " + recoveryPointCount + " time(s)");
			Thread.sleep(30000);
			recoveryPointCount++;
		}
		test.log(LogStatus.ERROR, "recovery point is not ready in 10 minutes");
		assertTrue("recovery point is not ready in 10 minutes", false);
		return job_id;
	}
	
	public String cancelCDBackupJobAndWaitForRecoveryPoint(String sourceId, ExtentTest test) throws InterruptedException {
		String job_id=null;
		job_id=cancelCDBackupJob(sourceId,  true, test);

		test.log(LogStatus.INFO, "backup job cancelled, now wait for the recovery point ready");
		System.out.println("backup job cancelled, now wait for the recovery point ready");
		return job_id;
	}

	public String submitCDRestoreJob(String sourceId, String toSourceId, String fromPath, String toPath,
			Integer recoveryPointId, boolean waitForStart, boolean waitForComplete, ExtentTest test) throws InterruptedException {
		test.log(LogStatus.INFO, "get last recovery points");
		Response response = getRecoveryPoints(spogServer.GetLoggedinUserOrganizationID(), sourceId, test);
		response.then().log().all();
		ArrayList<Integer> existingRecoveryPointIds = response.then().extract().path("data.recovery_point_id");
		String currentJobId=null;
		if (existingRecoveryPointIds.size()==0) {
			test.log(LogStatus.ERROR, "no sessions available");
			return currentJobId;
		}
		if (Objects.isNull(recoveryPointId) || recoveryPointId == 0) {
			recoveryPointId = existingRecoveryPointIds.get(0);
		}

		ArrayList<HashMap<String, Object>> recoveryPoints = response.then().extract().path("data");
		for (Map<String, Object> recoveryPoint : recoveryPoints) {
			System.out.println(recoveryPoint.get("recovery_point_id"));
			if (recoveryPoint.get("recovery_point_id").toString().equalsIgnoreCase(recoveryPointId.toString())) {
				String snapshot_host = ((HashMap<String, String>) recoveryPoint.get("destination")).get("host");
				String task_type = (String) recoveryPoint.get("task_type");

				String browser_url = (String) recoveryPoint.get("browser_url");
				String snapshot_path = browser_url.split("path=")[1].split("ZettaMirror")[0];
				String from_path = browser_url.split("path=")[1].split("zetta")[1] + fromPath;

				response = spogServer.getJobsBySourceID(sourceId, "source_id;=;" + sourceId + ",job_type;=;restore",
						"start_time_ts;desc", 1, 1, true, test);
				String lastJobId = "";
				
				ArrayList<String> existingJob = response.then().extract().path("data.job_id");
				if (existingJob.size() != 0) {
					lastJobId = existingJob.get(0);
				}
				submitCDRecoveryJob(sourceId, sourceId, task_type, "cloud_direct_baas", from_path, snapshot_host, snapshot_path,
						toSourceId, toPath, "", test);
				if (false == waitForStart) {
					System.out.println("restore job submitted. do not need to wait for job start, thus return.");
					test.log(LogStatus.INFO, "restore job submitted. do not need to wait for job start, thus return.");
					return currentJobId;
				} else {
					int countStart = 0;
					while (countStart < 20) {
						response = spogServer.getJobsBySourceID(sourceId, "job_type;=;restore", "start_time_ts;desc", 1,
								1, true, test);
						ArrayList<String> currentJob = response.then().extract().path("data.job_id");
						if (currentJob.size() != 0 && !currentJob.get(0).equals(lastJobId)) {
							// job started
							currentJobId=currentJob.get(0);
							System.out.println("restore job status is:"+response.then().extract().path("data.job_status"));
							test.log(LogStatus.INFO, "restore job started");
							if (false == waitForComplete) {
								System.out.println(
										"restore job started, and do not need to wait for job completed, thus return.");
								test.log(LogStatus.INFO,
										"restore job started, and do not need to wait for job completed, thus return.");
								return currentJobId;
							} else {
								int countFinished = 0;
								while (countFinished < 100) {
									response = spogServer.getJobById(currentJob.get(0), test);
									String job_status = response.then().extract().path("data.job_status");
									if (job_status.equalsIgnoreCase("finished")) {
										System.out.println("restore job finished");
										test.log(LogStatus.INFO, "restore job completed.");
										return currentJobId;
									} else if (!job_status.equalsIgnoreCase("active")) {
										System.out.println("unexpected job status");
										test.log(LogStatus.ERROR, "unexpected job status.");
										return currentJobId;
									}
									test.log(LogStatus.INFO,
											"restore job in progress... " + countFinished + " time(s)");
									System.out.println("restore job in progress... " + countFinished + " time(s)");
									Thread.sleep(30000);
									countFinished++;
								}
								test.log(LogStatus.ERROR, "restore job does not finish in 50 minutes.");
								System.out.println("restore job does not finish in 50 minutes.");
								assertTrue("restore job not finished in 50 minutes", false);
							}
						}
						test.log(LogStatus.INFO, "restore job is not started... " + countStart + " time(s)");
						System.out.println("restore job not started... " + countStart + " time(s)");
						Thread.sleep(30000);
						countStart++;
					}
					test.log(LogStatus.ERROR, "restore job is not launched in 10 minutes.");
					System.out.println("restore job not started in 10 minutes");
					assertTrue("restore job not started in 10 minutes", false);
				}
			}

			test.log(LogStatus.ERROR, "the recovery point is invalid");
			assertTrue("the recovery point is invalid", false);
		}
		return currentJobId;
	}
	
	public String submitCDRestoreJob(String sourceId, String toSourceId, String fromPath, String toPath,
			Integer recoveryPointId, boolean waitForStart, boolean waitForComplete,String taskType, ExtentTest test) throws InterruptedException {
		test.log(LogStatus.INFO, "get last recovery points");
		Response response = getRecoveryPoints(spogServer.GetLoggedinUserOrganizationID(), sourceId, test);
		response.then().log().all();
		ArrayList<Integer> existingRecoveryPointIds = response.then().extract().path("data.recovery_point_id");
		String currentJobId=null;
		if (existingRecoveryPointIds.size()==0) {
			test.log(LogStatus.ERROR, "no sessions available");
			return currentJobId;
		}
		if (Objects.isNull(recoveryPointId) || recoveryPointId == 0) {
			recoveryPointId = existingRecoveryPointIds.get(0);
		}

		ArrayList<HashMap<String, Object>> recoveryPoints = response.then().extract().path("data");
		for (Map<String, Object> recoveryPoint : recoveryPoints) {
			System.out.println(recoveryPoint.get("recovery_point_id"));
			if (recoveryPoint.get("recovery_point_id").toString().equalsIgnoreCase(recoveryPointId.toString())) {
				String snapshot_host = ((HashMap<String, String>) recoveryPoint.get("destination")).get("host");
				String task_type = (String) recoveryPoint.get("task_type");

				String browser_url = (String) recoveryPoint.get("browser_url");
				String snapshot_path = browser_url.split("path=")[1].split("ZettaMirror")[0];
				String from_path = browser_url.split("path=")[1].split("zetta")[1] + fromPath;

				response = spogServer.getJobsBySourceID(sourceId, "source_id;=;" + sourceId + ",job_type;=;restore",
						"start_time_ts;desc", 1, 1, true, test);
				String lastJobId = "";
				
				ArrayList<String> existingJob = response.then().extract().path("data.job_id");
				if (existingJob.size() != 0) {
					lastJobId = existingJob.get(0);
				}
				submitCDRecoveryJob(sourceId, sourceId, task_type, taskType, from_path, snapshot_host, snapshot_path,
						toSourceId, toPath, "", test);
				if (false == waitForStart) {
					System.out.println("restore job submitted. do not need to wait for job start, thus return.");
					test.log(LogStatus.INFO, "restore job submitted. do not need to wait for job start, thus return.");
					return currentJobId;
				} else {
					int countStart = 0;
					while (countStart < 20) {
						response = spogServer.getJobsBySourceID(sourceId, "job_type;=;restore", "start_time_ts;desc", 1,
								1, true, test);
						ArrayList<String> currentJob = response.then().extract().path("data.job_id");
						if (currentJob.size() != 0 && !currentJob.get(0).equals(lastJobId)) {
							// job started
							currentJobId=currentJob.get(0);
							System.out.println("restore job status is:"+response.then().extract().path("data.job_status"));
							test.log(LogStatus.INFO, "restore job started");
							if (false == waitForComplete) {
								System.out.println(
										"restore job started, and do not need to wait for job completed, thus return.");
								test.log(LogStatus.INFO,
										"restore job started, and do not need to wait for job completed, thus return.");
								return currentJobId;
							} else {
								int countFinished = 0;
								while (countFinished < 100) {
									response = spogServer.getJobById(currentJob.get(0), test);
									String job_status = response.then().extract().path("data.job_status");
									if (job_status.equalsIgnoreCase("finished")) {
										System.out.println("restore job finished");
										test.log(LogStatus.INFO, "restore job completed.");
										return currentJobId;
									} else if (!job_status.equalsIgnoreCase("active")) {
										System.out.println("unexpected job status");
										test.log(LogStatus.ERROR, "unexpected job status.");
										return currentJobId;
									}
									test.log(LogStatus.INFO,
											"restore job in progress... " + countFinished + " time(s)");
									System.out.println("restore job in progress... " + countFinished + " time(s)");
									Thread.sleep(30000);
									countFinished++;
								}
								test.log(LogStatus.ERROR, "restore job does not finish in 50 minutes.");
								System.out.println("restore job does not finish in 50 minutes.");
								assertTrue("restore job not finished in 50 minutes", false);
							}
						}
						test.log(LogStatus.INFO, "restore job is not started... " + countStart + " time(s)");
						System.out.println("restore job not started... " + countStart + " time(s)");
						Thread.sleep(30000);
						countStart++;
					}
					test.log(LogStatus.ERROR, "restore job is not launched in 10 minutes.");
					System.out.println("restore job not started in 10 minutes");
					assertTrue("restore job not started in 10 minutes", false);
				}
			}

			test.log(LogStatus.ERROR, "the recovery point is invalid");
			assertTrue("the recovery point is invalid", false);
		}
		return currentJobId;
	}
	
	public void simpleSubmitCDRestoreJob(String sourceId, String toSourceId, String fromPath, String toPath,
			Integer recoveryPointId, boolean waitForStart, boolean waitForComplete,String taskType, ExtentTest test) throws InterruptedException {
		test.log(LogStatus.INFO, "get last recovery points");
		Response response = getRecoveryPoints(spogServer.GetLoggedinUserOrganizationID(), sourceId, test);
		
				String task_type = "cloud_direct_image_backup";

				String browser_url = "https://ztst-3502.zetta.net/Browser/index.html?path=/.snapshot/sync-age_2019-07-11_083514_UTC/zetta/ZettaMirror/zsystem4/VOLUME/C.img";
				String snapshot_path = browser_url.split("path=")[1].split("ZettaMirror")[0];
				String from_path = browser_url.split("path=")[1].split("zetta")[1] + fromPath;

				response = spogServer.getJobsBySourceID(sourceId, "source_id;=;" + sourceId + ",job_type;=;restore",
						"start_time_ts;desc", 1, 1, true, test);
				String lastJobId = "";
				
				ArrayList<String> existingJob = response.then().extract().path("data.job_id");
				if (existingJob.size() != 0) {
					lastJobId = existingJob.get(0);
				}
				submitCDRecoveryJob(sourceId, sourceId, task_type, taskType, from_path, "ztst-3502.zetta.net", snapshot_path,
						toSourceId, toPath, "", test);
				if (false == waitForStart) {
					System.out.println("restore job submitted. do not need to wait for job start, thus return.");
					test.log(LogStatus.INFO, "restore job submitted. do not need to wait for job start, thus return.");
				} else {
					int countStart = 0;
					while (countStart < 20) {
						response = spogServer.getJobsBySourceID(sourceId, "job_type;=;restore", "start_time_ts;desc", 1,
								1, true, test);
						ArrayList<String> currentJob = response.then().extract().path("data.job_id");
						if (currentJob.size() != 0 && !currentJob.get(0).equals(lastJobId)) {
							// job started
							String currentJobId=currentJob.get(0);
							System.out.println("restore job status is:"+response.then().extract().path("data.job_status"));
							test.log(LogStatus.INFO, "restore job started");
							if (false == waitForComplete) {
								System.out.println(
										"restore job started, and do not need to wait for job completed, thus return.");
								test.log(LogStatus.INFO,
										"restore job started, and do not need to wait for job completed, thus return.");
							} else {
								int countFinished = 0;
								while (countFinished < 100) {
									response = spogServer.getJobById(currentJob.get(0), test);
									String job_status = response.then().extract().path("data.job_status");
									if (job_status.equalsIgnoreCase("finished")) {
										System.out.println("restore job finished");
										test.log(LogStatus.INFO, "restore job completed.");
									} else if (!job_status.equalsIgnoreCase("active")) {
										System.out.println("unexpected job status");
										test.log(LogStatus.ERROR, "unexpected job status.");
									}
									test.log(LogStatus.INFO,
											"restore job in progress... " + countFinished + " time(s)");
									System.out.println("restore job in progress... " + countFinished + " time(s)");
									Thread.sleep(30000);
									countFinished++;
								}
								test.log(LogStatus.ERROR, "restore job does not finish in 50 minutes.");
								System.out.println("restore job does not finish in 50 minutes.");
								assertTrue("restore job not finished in 50 minutes", false);
							}
						}
						test.log(LogStatus.INFO, "restore job is not started... " + countStart + " time(s)");
						System.out.println("restore job not started... " + countStart + " time(s)");
						Thread.sleep(30000);
						countStart++;
					}
					test.log(LogStatus.ERROR, "restore job is not launched in 10 minutes.");
					System.out.println("restore job not started in 10 minutes");
					assertTrue("restore job not started in 10 minutes", false);

			test.log(LogStatus.ERROR, "the recovery point is invalid");
			assertTrue("the recovery point is invalid", false);
		}
	}
	
	public void cancelCDRestoreJob(String sourceId, ExtentTest test) throws InterruptedException {
		test.log(LogStatus.INFO, "cancel restore job");
		cancelCDRecoveryJob(sourceId,spogServer.GetLoggedinUserOrganizationID(),test);
		
	}
	
	/* REST API Get Sources for specified policy
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param policy_id
	 * @param expectedSources
	 * @param expectedStatusCode
	 * @param expectedErrorMessages
	 * @param test
	 */
	public void getSourcesForSpecifiedPolicyWithCheck(String token, String policy_id, ArrayList<HashMap<String, Object>> expectedSources,
															int expectedStatusCode, SpogMessageCode expectedErrorMessage, ExtentTest test) {
		
		test.log(LogStatus.INFO, "CaLL API Get Sources for Specified policy");
		Response response = source4SpogInvoker.getSourcesByPolicyId(token, policy_id, test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);
		
		ArrayList<HashMap<String, Object>> actualSources = new ArrayList<>();
		
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			actualSources = response.then().extract().path("data");
			
			test.log(LogStatus.INFO, "Sorting the sources in the expected response by source_id in ascending order");
			spogServer.sortArrayListbyString(expectedSources, "source_id");
			test.log(LogStatus.INFO, "Sorting the sources in the actual response by source_id in ascending order");
			spogServer.sortArrayListbyString(actualSources, "source_id");
			
			for (int i = 0; i < actualSources.size(); i++) {
				verifySourceInfo(expectedSources.get(i),actualSources.get(i),test);
			}
			
		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			if (code.contains("00E00008")) {
				message = message.replace("{0}", policy_id);
			}
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.INFO, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response,message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
	}	
	
	/* Verify the Source information in the response of the API - GET: /policies/policy_id/sources
	 * 
	 * @author Rakesh.Chalamala
	 * @param expectedSources
	 * @param actualSources
	 * @param test
	 */
	public void verifySourceInfo(HashMap<String, Object> expectedSources, HashMap<String, Object> actualSources, ExtentTest test) {
			
		test.log(LogStatus.INFO, "Compare source_id");
		spogServer.assertResponseItem(expectedSources.get("source_id"), actualSources.get("source_id"), test);
		
		test.log(LogStatus.INFO, "Compare source_type");
		spogServer.assertResponseItem(expectedSources.get("source_type").toString(), actualSources.get("source_type").toString(), test);
		
		test.log(LogStatus.INFO, "Compare source_name");
		spogServer.assertResponseItem(expectedSources.get("source_name").toString(), actualSources.get("source_name").toString(), test);
		
		HashMap<String, Object> act_operating_system = (HashMap<String, Object>) actualSources.get("operating_system");
		
		test.log(LogStatus.INFO, "Compare os_major");
		spogServer.assertResponseItem(expectedSources.get("os_major").toString(), act_operating_system.get("os_major").toString(), test);
		
		test.log(LogStatus.INFO, "Compare os_major");
		spogServer.assertResponseItem(expectedSources.get("os_name").toString(), act_operating_system.get("os_name").toString(), test);
		
		test.log(LogStatus.INFO, "Compare os_major");
		spogServer.assertResponseItem(expectedSources.get("os_architecture").toString(), act_operating_system.get("os_architecture").toString(), test);
		
		test.log(LogStatus.INFO, "Compare site_id");
		if(actualSources.get("site_id") != null && actualSources.get("site_id") != "") {
			spogServer.assertResponseItem(expectedSources.get("site_id").toString(), actualSources.get("site_id").toString(), test);
			assertNotNull(actualSources.get("site_name"));
		}
		
		test.log(LogStatus.INFO, "Compare hypervisor_id");
		spogServer.assertResponseItem(expectedSources.get("hypervisor_id"), actualSources.get("hypervisor_id"), test);
		
		test.log(LogStatus.INFO, "Compare hypervisor_name");
		spogServer.assertResponseItem(expectedSources.get("hypervisor_name"), actualSources.get("hypervisor_name"), test);
		
		if (expectedSources.get("source_group") != null) {
			
			test.log(LogStatus.INFO, "Compare the source_group");
		}
	}
	
	/*
	 * Compose the source info required for the API - GET: /policies/policy_id/sources
	 * 
	 * @author Rakesh.Chalamala
	 * @param source_id
	 * @param source_name
	 * @param source_type
	 * @param organization_id
	 * @param site_id
	 * @param os_major
	 * @param hypervisor_id
	 * @param site_name
	 * @param hypervisor_name
	 * @param source_group
	 * @param test
	 * 
	 * return SourceInfo
	 */
	public HashMap<String, Object> composeSourceInfo(String source_id, String source_name, SourceType source_type, 
									String organization_id, String site_id, String os_major,String os_name,
									String os_architecture, String hypervisor_id, String site_name, 
									String hypervisor_name, HashMap<String, Object> source_group,
									ExtentTest test) {
		
		HashMap<String, Object> sourceInfo = new HashMap<>();
		
		sourceInfo.put("source_id", source_id);
		sourceInfo.put("source_type", source_type);
		sourceInfo.put("source_name", source_name);
		sourceInfo.put("os_major", os_major);
		sourceInfo.put("os_name", os_name);
		sourceInfo.put("os_architecture", os_architecture);
		sourceInfo.put("site_id", site_id);
		sourceInfo.put("site_name", site_name);
		sourceInfo.put("hypervisor_id", hypervisor_id);
		sourceInfo.put("hypervisor_name", hypervisor_name);
		sourceInfo.put("source_group", source_group);
		
		return sourceInfo;
	}

	/**
	 * Convert specified source to agentless hypervisor
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param source_id
	 * @param expectedStatusCode
	 * @param test
	 * @return
	 */
	public Response convertToAgentlessBySourceId(String token, String source_id, int expectedStatusCode, ExtentTest test) {
		
		test.log(LogStatus.INFO, "Call API - POST: /sources/{id}/convert_to_agentless");
		Response response = source4SpogInvoker.convertToAgentlessBySourceId(token, source_id, test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);
		
		return response;
	}
	
	/**
	 * Convert specified source to agentless hypervisor
	 * verifies the status code and error messages for invalid cases
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param source_id
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 */
	public void convertToAgentlessBySourceIdWithCheck(String token, String source_id, int expectedStatusCode, SpogMessageCode expectedErrorMessage, ExtentTest test) {
		
		Response response = convertToAgentlessBySourceId(token, source_id, expectedStatusCode, test);
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.PASS, "Source with ID: "+source_id+" converted to agentless successfully.");
			
		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.INFO, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response,message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
		
	}
 
	
}
