package InvokerServer;

import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.RecoveredResourceColumnConstants;
import Constants.RecoveredResourceColumnConstants;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import Constants.FilterTypes.filterType;
import dataPreparation.JsonPreparation;
import genericutil.ErrorHandler;
import invoker.SPOGDestinationInvoker;
import invoker.SPOGRecoveredResourceInvoker;
import io.restassured.response.Response;

public class SPOGRecoveredResourceServer {
	private static JsonPreparation jp = new JsonPreparation();
	private SPOGServer spogServer;
	
	
	private SPOGRecoveredResourceInvoker spogRecoveredResourceInvoker;
	public static ErrorHandler errorHandle = ErrorHandler.getErrorHandler();
	private ExtentTest test;

	private String Uuid;

	public void setToken(String token) {
		spogRecoveredResourceInvoker.setToken(token);
	}

	public SPOGRecoveredResourceServer(String baseURI, String port) {
		
		spogRecoveredResourceInvoker = new SPOGRecoveredResourceInvoker(baseURI, port);
		spogServer = new SPOGServer(baseURI, port);
	}
	
	/*
	 * @author Rakesh.Chalamala
	 * @param token
	 * @return response
	 */
	public Response getRecoveredResourceColumns(String token,int expectedStatusCode, ExtentTest test) {
		
		test.log(LogStatus.INFO, "Call the rest API to get the recovered resource columns");
		Response response = spogRecoveredResourceInvoker.getRecoveredResourceColumns(token, test);
		spogServer.checkResponseStatus(response, expectedStatusCode);
		
		return response;
	}
	
	/*
	 * @author Rakesh.Chalamala
	 * @param column_id
	 * @param long_label
	 * @param short_label
	 * @param key
	 * @param sort
	 * @param filter
	 * @param visible
	 * @param order_id
	 * @return columnHeadContent
	 */
	private HashMap<String, Object> composeRecoveredResourceColumnInfo(String column_id, String long_label,
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
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param expectedStatusCode
	 * @param ExpectedErrorMessage
	 * @param test
	 */
	public void getRecoveredResourceColumnsWithCheck(String token,int expctedStatusCode,SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

			Response response = spogRecoveredResourceInvoker.getRecoveredResourceColumns(token, test);
			spogServer.checkResponseStatus(response, expctedStatusCode);
			ArrayList<HashMap<String, Object>> expectedColumnsHeadContents = new ArrayList<>();

			if (expctedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			
			HashMap<String, Object> columnHeadContent1 = new HashMap();
			columnHeadContent1 = composeRecoveredResourceColumnInfo(RecoveredResourceColumnConstants.NAME_COLUMNID, RecoveredResourceColumnConstants.NAME_LONG,
					RecoveredResourceColumnConstants.NAME_SHORT, RecoveredResourceColumnConstants.NAME_KEY, RecoveredResourceColumnConstants.NAME_SORT,
					RecoveredResourceColumnConstants.NAME_FILTER, RecoveredResourceColumnConstants.NAME_VISIBLE, RecoveredResourceColumnConstants.NAME_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent1);
			
			HashMap<String, Object> columnHeadContent2 = new HashMap();
			columnHeadContent2 = composeRecoveredResourceColumnInfo(RecoveredResourceColumnConstants.SOURCE_COLUMNID, RecoveredResourceColumnConstants.SOURCE_LONG,
					RecoveredResourceColumnConstants.SOURCE_SHORT, RecoveredResourceColumnConstants.SOURCE_KEY, RecoveredResourceColumnConstants.SOURCE_SORT, 
					RecoveredResourceColumnConstants.SOURCE_FILTER, RecoveredResourceColumnConstants.SOURCE_VISIBLE, RecoveredResourceColumnConstants.SOURCE_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent2);
			
			HashMap<String, Object> columnHeadContent3 = new HashMap();
			columnHeadContent3 = composeRecoveredResourceColumnInfo(RecoveredResourceColumnConstants.TYPE_COLUMNID, RecoveredResourceColumnConstants.TYPE_LONG,
					RecoveredResourceColumnConstants.TYPE_SHORT, RecoveredResourceColumnConstants.TYPE_KEY, RecoveredResourceColumnConstants.TYPE_SORT,
					RecoveredResourceColumnConstants.TYPE_FILTER, RecoveredResourceColumnConstants.TYPE_VISIBLE, RecoveredResourceColumnConstants.TYPE_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent3);
			
			HashMap<String, Object> columnHeadContent4 = new HashMap();
			columnHeadContent4 = composeRecoveredResourceColumnInfo(RecoveredResourceColumnConstants.STATE_COLUMNID, RecoveredResourceColumnConstants.STATE_LONG,
					RecoveredResourceColumnConstants.STATE_SHORT, RecoveredResourceColumnConstants.STATE_KEY, RecoveredResourceColumnConstants.STATE_SORT, 
					RecoveredResourceColumnConstants.STATE_FILTER, RecoveredResourceColumnConstants.STATE_VISIBLE, RecoveredResourceColumnConstants.STATE_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent4);
			
			HashMap<String, Object> columnHeadContent5 = new HashMap();
			columnHeadContent5 = composeRecoveredResourceColumnInfo(RecoveredResourceColumnConstants.OS_COLUMNID, RecoveredResourceColumnConstants.OS_LONG,
					RecoveredResourceColumnConstants.OS_SHORT, RecoveredResourceColumnConstants.OS_KEY, RecoveredResourceColumnConstants.OS_SORT, 
					RecoveredResourceColumnConstants.OS_FILTER, RecoveredResourceColumnConstants.OS_VISIBLE, RecoveredResourceColumnConstants.OS_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent5);
			
			HashMap<String, Object> columnHeadContent6 = new HashMap();
			columnHeadContent6 = composeRecoveredResourceColumnInfo(RecoveredResourceColumnConstants.RP_COLUMNID, RecoveredResourceColumnConstants.RP_LONG,
					RecoveredResourceColumnConstants.RP_SHORT, RecoveredResourceColumnConstants.RP_KEY, RecoveredResourceColumnConstants.RP_SORT, 
					RecoveredResourceColumnConstants.RP_FILTER, RecoveredResourceColumnConstants.RP_VISIBLE, RecoveredResourceColumnConstants.RP_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent6);
			
			HashMap<String, Object> columnHeadContent9 = new HashMap();
			columnHeadContent9 = composeRecoveredResourceColumnInfo(RecoveredResourceColumnConstants.IPA_COLUMNID, RecoveredResourceColumnConstants.IPA_LONG,
					RecoveredResourceColumnConstants.IPA_SHORT, RecoveredResourceColumnConstants.IPA_KEY, RecoveredResourceColumnConstants.IPA_SORT,
					RecoveredResourceColumnConstants.IPA_FILTER, RecoveredResourceColumnConstants.IPA_VISIBLE, RecoveredResourceColumnConstants.IPA_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent9);
			
			HashMap<String, Object> columnHeadContent7 = new HashMap();
			columnHeadContent7 = composeRecoveredResourceColumnInfo(RecoveredResourceColumnConstants.AO_COLUMNID, RecoveredResourceColumnConstants.AO_LONG,
					RecoveredResourceColumnConstants.AO_SHORT, RecoveredResourceColumnConstants.AO_KEY, RecoveredResourceColumnConstants.AO_SORT,
					RecoveredResourceColumnConstants.AO_FILTER, RecoveredResourceColumnConstants.AO_VISIBLE, RecoveredResourceColumnConstants.AO_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent7);
			
			HashMap<String, Object> columnHeadContent8 = new HashMap();
			columnHeadContent8 = composeRecoveredResourceColumnInfo(RecoveredResourceColumnConstants.POLICY_COLUMNID, RecoveredResourceColumnConstants.POLICY_LONG,
					RecoveredResourceColumnConstants.POLICY_SHORT, RecoveredResourceColumnConstants.POLICY_KEY, RecoveredResourceColumnConstants.POLICY_SORT,
					RecoveredResourceColumnConstants.POLICY_FILTER, RecoveredResourceColumnConstants.POLICY_VISIBLE, RecoveredResourceColumnConstants.POLICY_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent8);
			
			HashMap<String, Object> columnHeadContent10 = new HashMap();
			columnHeadContent10 = composeRecoveredResourceColumnInfo(RecoveredResourceColumnConstants.ARAM_COLUMNID, RecoveredResourceColumnConstants.ARAM_LONG,
					RecoveredResourceColumnConstants.ARAM_SHORT, RecoveredResourceColumnConstants.ARAM_KEY, RecoveredResourceColumnConstants.ARAM_SORT, 
					RecoveredResourceColumnConstants.ARAM_FILTER, RecoveredResourceColumnConstants.ARAM_VISIBLE, RecoveredResourceColumnConstants.ARAM_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent10);
			
			HashMap<String, Object> columnHeadContent11 = new HashMap();
			columnHeadContent11 = composeRecoveredResourceColumnInfo(RecoveredResourceColumnConstants.ACPU_COLUMNID, RecoveredResourceColumnConstants.ACPU_LONG,
					RecoveredResourceColumnConstants.ACPU_SHORT, RecoveredResourceColumnConstants.ACPU_KEY, RecoveredResourceColumnConstants.ACPU_SORT,
					RecoveredResourceColumnConstants.ACPU_FILTER, RecoveredResourceColumnConstants.ACPU_VISIBLE, RecoveredResourceColumnConstants.ACPU_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent11);
			
			/*HashMap<String, Object> columnHeadContent12 = new HashMap();
			columnHeadContent12 = composeRecoveredResourceColumnInfo(RecoveredResourceColumnConstants.AACT_COLUMNID, RecoveredResourceColumnConstants.AACT_LONG,
					RecoveredResourceColumnConstants.AACT_SHORT, RecoveredResourceColumnConstants.AACT_KEY, RecoveredResourceColumnConstants.AACT_SORT,
					RecoveredResourceColumnConstants.AACT_FILTER, RecoveredResourceColumnConstants.AACT_VISIBLE, RecoveredResourceColumnConstants.AACT_ORDERID);
			expectedColumnsHeadContents.add(columnHeadContent12);*/
			
			CompareColumnsHeadContent(response, expectedColumnsHeadContents, test);	
		}
		else {
			String code = ExpectedErrorMessage.getCodeString();
	    	String message = ExpectedErrorMessage.getStatus();
	    	spogServer.checkErrorCode(response,code);
	    	test.log(LogStatus.INFO, "The error code matched with the expected "+code);
	    	spogServer.checkErrorMessage(response,message);
	    	test.log(LogStatus.PASS, "The expected error message matched " + expctedStatusCode);
		}
	}
	
	/*
	 * @author Rakesh.Chalamala
	 * @param response
	 * @param expectedColumnsHeadContents
	 * @param test
	 */
	private void CompareColumnsHeadContent(Response response,
			ArrayList<HashMap<String, Object>> expectedColumnsHeadContents, ExtentTest test) {

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
			spogServer.assertResponseItem(expectedColumnsHeadContent.get("order_id"), HeadContent.get("order_id"), test);
		}
	}
	
	/* Compose the recovered resource columns
	 * @author Rakesh.Chalamala
	 * @param columnId
	 * @param sort
	 * @param filter
	 * @param visible
	 * @param orderId
	 * @return HashMap
	 */
	public HashMap<String, Object> composeRecoveredResource_Column(String columnId, String sort, String filter, String visible,
			String orderId) {
		// TODO Auto-generated method stub
		HashMap<String,Object> temp = new HashMap<>();
		temp.put("column_id", columnId);
		if(sort==null||sort=="") {
			temp.put("sort", sort);
		}else if(!sort.equals("none")){
			temp.put("sort",sort); 
		}
		if(sort==null||sort=="") {
			temp.put("sort", sort);
		}else if(!filter.equals("none"))
		{
			temp.put("filter",filter); 
		}
		if(sort==null||sort=="") {
			temp.put("sort", sort);
		}else if(!visible.equals("none"))
		{
			temp.put("visible",visible);
		}
		temp.put("order_id",Integer.parseInt(orderId));

		return temp;
	}
	
	/*
	 * create Recovered Resource Columns  For LoggedIn User
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param expected_columns
	 * @param test
	 * @return response
	 */
	public Response createRecoveredResourceColumnsForLoggedInUser(String validToken, ArrayList<HashMap<String, Object>> expected_columns, ExtentTest test) {
		
		 Map<String,ArrayList<HashMap<String, Object>>> columnsInfo =jp.jobColumnInfo(expected_columns);
		 Response response=spogRecoveredResourceInvoker.createRecoveredResourceColumnsForLoggedInUser(validToken, columnsInfo, test);
		 
		return response;	
	}
	
	/*
	 * Compare Recovered Resource Columns Content
	 * @author Rakesh.Chalamala
	 * @param response
	 * @param expectedColumnsContents
	 * @param default columns content
	 * @param expectedstatuscode
	 * @param ExpectedErrorMessage
	 */
	public void CompareRecoveredResourceColumnsContent(Response response, ArrayList<HashMap<String,Object>> expectedColumnsContents, ArrayList<HashMap<String,Object>> defaultColumnsContents,
			int expectedstatuscode, SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		 ArrayList<HashMap<String,Object>> actualcolumnsHeadContent = new ArrayList<HashMap<String,Object>>();
		    
		 spogServer.checkResponseStatus(response, expectedstatuscode);
		 if(expectedstatuscode == SpogConstants.SUCCESS_POST) {
			 actualcolumnsHeadContent = response.then().extract().path("data");
			 int length = expectedColumnsContents.size();
			 
			 test.log(LogStatus.INFO, "Sort the actual response by order_id");
			 actualcolumnsHeadContent = spogServer.sortArrayListbyInt(actualcolumnsHeadContent, "order_id");

			 test.log(LogStatus.INFO, "Sort the expected response by order_id");
			 expectedColumnsContents = spogServer.sortArrayListbyInt(expectedColumnsContents, "order_id");
			
			 if(expectedColumnsContents.size()==actualcolumnsHeadContent.size()) {
					
					for(int i=0;i<actualcolumnsHeadContent.size();i++) {
						for(int j=0;j<defaultColumnsContents.size();j++) {
							if(actualcolumnsHeadContent.get(i).get("column_id").equals(defaultColumnsContents.get(j).get("column_id"))) {
								checkRecoveredResourceColumns(actualcolumnsHeadContent.get(i),expectedColumnsContents.get(i),defaultColumnsContents.get(j),test);
								break;
							}
						}
					}
				}else {
					test.log(LogStatus.FAIL, "The expected count did not match the actual count");
					assertTrue("The expected count did not match the actual count", false);
				}
			}else {
				String code = ExpectedErrorMessage.getCodeString();
				 String message = ExpectedErrorMessage.getStatus();
				 if(code.contains("00D00003")){
					 message = message.replace("{0}", expectedColumnsContents.get(0).get("column_id").toString());

				 }
				 else if (code.contains("00D00005")) {
					message = message.replace("{0}", expectedColumnsContents.get(0).get("order_id").toString());
				 }
				 else if(code.contains("0D00006")){
					 message = message.replace("{0}", defaultColumnsContents.get(defaultColumnsContents.size()-1).get("order_id").toString());
				 }					 				 
				 spogServer.checkErrorCode(response,code);
				 test.log(LogStatus.PASS, "The error code matched with the expected "+code);
				 spogServer.checkErrorMessage(response,message);
				 test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		 }			 
	}
	
	/*
	 * @author Rakesh.Chalamala
	 * @param actual_response
	 * @param expected_response
	 * @param defaultcolumnvalues
	 * @param test
	 */
	public void checkRecoveredResourceColumns(HashMap<String,Object> actual_response, HashMap<String,Object> expected_response, HashMap<String,Object> defaultcolumnvalues,ExtentTest test ) 
	{
		test.log(LogStatus.INFO,"Compare the column id");
		spogServer.assertResponseItem(actual_response.get("column_id").toString(),expected_response.get("column_id").toString(),test);
		
		test.log(LogStatus.INFO, "Compare the order id column ");		
		spogServer.assertResponseItem(actual_response.get("order_id").toString(),expected_response.get("order_id").toString(),test);
		
		test.log(LogStatus.INFO,"Compare the visiblity column ");
		if(expected_response.containsKey("visible")) {
			spogServer.assertResponseItem(actual_response.get("visible").toString(),expected_response.get("visible").toString(),test);
		}else {
			spogServer.assertResponseItem(actual_response.get("visible").toString(),defaultcolumnvalues.get("visible").toString(),test);
		}
		
		test.log(LogStatus.INFO,"Compare the sort column");
		spogServer.assertResponseItem(actual_response.get("sort").toString(),defaultcolumnvalues.get("sort").toString(),test);
		
		test.log(LogStatus.INFO,"Compare the filter column");
		spogServer.assertResponseItem(actual_response.get("filter").toString(),defaultcolumnvalues.get("filter").toString(),test);
		
	}
	
	/*
	 * Get Destination columns for logged in user
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param expectedresponse arraylist of expected columns
	 * @param defaultcolumnresponse arraylist of default columns
	 * @param expectedstatuscode
	 * @param expected errormessage
	 * @param extenttest object
	 * 
	 */
	public void getRecoveredResourceColumnsForLoggedinUser(String token, 
													ArrayList<HashMap<String,Object>> expectedresponse,
													ArrayList<HashMap<String,Object>> defaultcolumnresponse,
													int expectedStatusCode, 
													SpogMessageCode ExpectedErrorMessage , 
													ExtentTest test) {
		test.log(LogStatus.INFO, "Call the res API to get the RecoveredResource columns for loggedin user");
		Response response = spogRecoveredResourceInvoker.getRecoveredResourceColumnsForLoggedInUser(token, test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);
		
		if(expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			ArrayList<HashMap<String,Object>> actualresponse = response.then().extract().path("data");

			test.log(LogStatus.INFO, "Sort the actual response by order_id");
			actualresponse = spogServer.sortArrayListbyInt(actualresponse, "order_id");

			test.log(LogStatus.INFO, "Sort the expected response by order_id");
			expectedresponse = spogServer.sortArrayListbyInt(expectedresponse, "order_id");

			if(actualresponse.size() == defaultcolumnresponse.size() || actualresponse.size() == defaultcolumnresponse.size()-1) { // size should be same as default

				ArrayList<HashMap<String, Object>> col_respnse = null;
				boolean flag = false;
				
 				for(int i=0;i<actualresponse.size();i++) {
 					flag = false; 					
					for(int j=0;j<expectedresponse.size();j++) {
						if(actualresponse.get(i).get("column_id").equals(expectedresponse.get(j).get("column_id"))) {
							for (int k = 0; k < defaultcolumnresponse.size(); k++) {
								if(expectedresponse.get(j).get("column_id").equals(defaultcolumnresponse.get(k).get("column_id"))) {
									checkRecoveredResourceColumns(actualresponse.get(i),expectedresponse.get(j),defaultcolumnresponse.get(k),test);
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
								
								checkRecoveredResourceColumns(actualresponse.get(i),col_respnse.get(k),col_respnse.get(k),test);
								break;
							}
						}							
					}
				}
				
			}else {
				test.log(LogStatus.FAIL, "The expected count did not match the actual count");
				assertTrue("The expected count did not match the actual count", false);
			}
			
		}else {
			String code = ExpectedErrorMessage.getCodeString();
	    	String message = ExpectedErrorMessage.getStatus();
	    	spogServer.checkErrorCode(response,code);
	    	test.log(LogStatus.PASS, "The error code matched with the expected "+code);
	    	spogServer.checkErrorMessage(response,message);
	    	test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}	
	}
	
	/*
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param expectedResponse
	 * @param defaultcolumnresponse
	 * @param expectedstatuscode
	 * @param ExpectedErrorMessage
	 * @param test
	 * @return response
	 */
	public Response updateRecoveredResourceColumnsForLoggedinUser(String token,
															ArrayList<HashMap<String, Object>> expectedresponse,
															ArrayList<HashMap<String, Object>> defaultcolumnresponse,
															int expectedStatusCode,
															SpogMessageCode ExpectedErrorMessage,
															ExtentTest test
															) {
		Map<String, ArrayList<HashMap<String, Object>>> columnsInfo =jp.jobColumnInfo(expectedresponse);
		Response response = spogRecoveredResourceInvoker.updateRecoveredResourceColumnsForLoggedInUser(token, columnsInfo, test);
		spogServer.checkResponseStatus(response, expectedStatusCode);

		if (expectedStatusCode==SpogConstants.SUCCESS_GET_PUT_DELETE) 
		{
			ArrayList<HashMap<String, Object>> actualresponse = response.then().extract().path("data");

			test.log(LogStatus.INFO, "Sort the actual response by order_id");
			actualresponse = spogServer.sortArrayListbyInt(actualresponse, "order_id");

			test.log(LogStatus.INFO, "Sort the expected response by order_id");
			expectedresponse = spogServer.sortArrayListbyInt(expectedresponse, "order_id");

			if(expectedresponse.size()==actualresponse.size())
			{
				for(int i=0;i<actualresponse.size();i++) {
					for(int j=0;j<defaultcolumnresponse.size();j++) {
						if(actualresponse.get(i).get("column_id").equals(defaultcolumnresponse.get(j).get("column_id"))) {
							checkRecoveredResourceColumns(actualresponse.get(i),expectedresponse.get(i),defaultcolumnresponse.get(j),test);
							break;
						}
					}

				}
			}
			else {
				test.log(LogStatus.FAIL, "The expected count did not match the actual count");
				assertTrue("The expected count did not match the actual count", false);
			}	
		}	
		else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			if(code.contains("0D00006")){
				message = message.replace("{0}", defaultcolumnresponse.get(defaultcolumnresponse.size()-1).get("order_id").toString());
			}
			spogServer.checkErrorCode(response,code);
			test.log(LogStatus.PASS, "The error code matched with the expected "+code);
			spogServer.checkErrorMessage(response,message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);

		}	
		return response;
	}
	
	/*
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param expectedStatusCode
	 * @param EpectedErrorMesssage
	 * @param test
	 */
	public void deleteRecoveredResourceColumnsforLoggedInUserwithCheck(String token, int expectedstatuscode, SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		Response response = spogRecoveredResourceInvoker.deleteRecoveredResourceColumnsForLoggedInUser(token, test);
		spogServer.checkResponseStatus(response, expectedstatuscode);
		if(expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.PASS, "The recovered resource columns are successfully deleted");
		}else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			/*if(code.contains("00A00302")){
	    		message = message.replace("{0}", filterID);
	    		message= message.replace("{1}", userID);
	    	}*/
			spogServer.checkErrorCode(response,code);
			test.log(LogStatus.PASS, "The error code matched with the expected "+code);
			spogServer.checkErrorMessage(response,message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		}
	}
	
	/*
	 * @author Rakesh.Chalamala
	 * @param user_id
	 * @param token
	 * @param expected_columns
	 * @param test
	 * @return response
	 */
	public Response createRecoveredResourceColumnsForSpecifiedUser(String user_id, String validToken, ArrayList<HashMap<String, Object>> expected_columns, ExtentTest test) {
		
		 Map<String,ArrayList<HashMap<String, Object>>> columnsInfo =jp.jobColumnInfo(expected_columns);
		 Response response=spogRecoveredResourceInvoker.createRecoveredResourceColumnsByUserId(user_id,validToken,columnsInfo,test);
		 
		return response;	
	}
	
	/*
	 * @author Rakesh.Chalamala
	 * @param user_id
	 * @param token
	 * @param expectedStatusCode
	 * @param ExpectedErrorMessage
	 */
	public void deleteRecoveredResourceColumnsforSpecifiedUserwithCheck(String user_Id, String token, int expectedstatuscode, SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		Response response = spogRecoveredResourceInvoker.deleteRecoveredResourceColumnsByUserId(user_Id,token, test);
		spogServer.checkResponseStatus(response, expectedstatuscode);
		if(expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.PASS, "The RecoveredResource columns are successfully deleted");
		}else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			/*if(code.contains("00A00302")){
	    		message = message.replace("{0}", filterID);
	    		message= message.replace("{1}", userID);
	    	}*/
			spogServer.checkErrorCode(response,code);
			test.log(LogStatus.PASS, "The error code matched with the expected "+code);
			spogServer.checkErrorMessage(response,message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		}
	}
	
	/*
	 * Get RecoveredResource columns for specified user
	 * @author Rakesh.Chalamala
	 * @param user_id
	 * @param token
	 * @param expectedresponse arraylist of expected columns
	 * @param defaultcolumnresponse arraylist of default columns
	 * @param expectedstatuscode
	 * @param expected errormessage
	 * @param extenttest object
	 * 
	 */
	public void getRecoveredResourceColumnsForSpecifiedUser(String user_Id, 
														String token, 
														ArrayList<HashMap<String,Object>> expectedresponse,
														ArrayList<HashMap<String,Object>> defaultcolumnresponse,
														int expectedStatusCode, 
														SpogMessageCode ExpectedErrorMessage , 
														ExtentTest test)
	{
		test.log(LogStatus.INFO, "Call the res API to get the RecoveredResource columns for specified user");
		Response response = spogRecoveredResourceInvoker.getRecoveredResourceColumnsByUserId(user_Id, token, test);
		
		spogServer.checkResponseStatus(response, expectedStatusCode, test);
		
		if(expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			ArrayList<HashMap<String,Object>> actualresponse = response.then().extract().path("data");
			
			test.log(LogStatus.INFO, "Sort the actual response by order_id");
			actualresponse = spogServer.sortArrayListbyInt(actualresponse, "order_id");
			
//			if(!(expectedresponse.get(0).get("order_id").getClass().getTypeName()==defaultcolumnresponse.get(0).get("order_id").getClass().getTypeName())) {
			test.log(LogStatus.INFO, "Sort the expected response by order_id");
			expectedresponse = spogServer.sortArrayListbyInt(expectedresponse, "order_id");
//			
			if(actualresponse.size() == defaultcolumnresponse.size() || actualresponse.size() == defaultcolumnresponse.size()-1) { // size should be same as default

				ArrayList<HashMap<String, Object>> col_respnse = null;
				boolean flag = false;
				
 				for(int i=0;i<actualresponse.size();i++) {
 					flag = false; 					
					for(int j=0;j<expectedresponse.size();j++) {
						if(actualresponse.get(i).get("column_id").equals(expectedresponse.get(j).get("column_id"))) {
							for (int k = 0; k < defaultcolumnresponse.size(); k++) {
								if(expectedresponse.get(j).get("column_id").equals(defaultcolumnresponse.get(k).get("column_id"))) {
									checkRecoveredResourceColumns(actualresponse.get(i),expectedresponse.get(j),defaultcolumnresponse.get(k),test);
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
								
								checkRecoveredResourceColumns(actualresponse.get(i),col_respnse.get(k),col_respnse.get(k),test);
								break;
							}
						}							
					}
				}
				
			}else {
				test.log(LogStatus.FAIL, "The expected count did not match the actual count");
				assertTrue("The expected count did not match the actual count", false);
			}
			
		}else {
			String code = ExpectedErrorMessage.getCodeString();
	    	String message = ExpectedErrorMessage.getStatus();
	    	spogServer.checkErrorCode(response,code);
	    	test.log(LogStatus.PASS, "The error code matched with the expected "+code);
	    	spogServer.checkErrorMessage(response,message);
	    	test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}	
	}
	
	/*
	 * Update RecoveredResource columns for specified user
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param user_id
	 * @param test
	 */
	public Response updateRecoveredResourceColumnsByUserId(String user_Id,
													String token,
													ArrayList<HashMap<String, Object>> expectedresponse,
													ArrayList<HashMap<String, Object>> defaultcolumnresponse,
													int expectedStatusCode,
													SpogMessageCode ExpectedErrorMessage,
													ExtentTest test
													) {
	
		Map<String, ArrayList<HashMap<String, Object>>> columnsInfo =jp.jobColumnInfo(expectedresponse);
		Response response = spogRecoveredResourceInvoker.updateRecoveredResourceColumnsByUserId(user_Id, token, columnsInfo, test);
		spogServer.checkResponseStatus(response, expectedStatusCode);
		
		if (expectedStatusCode==SpogConstants.SUCCESS_GET_PUT_DELETE) 
		{
			ArrayList<HashMap<String, Object>> actualresponse = response.then().extract().path("data");
			
			test.log(LogStatus.INFO, "Sort the actual response by order_id");
			actualresponse = spogServer.sortArrayListbyInt(actualresponse, "order_id");
			
			test.log(LogStatus.INFO, "Sort the expected response by order_id");
			expectedresponse = spogServer.sortArrayListbyInt(expectedresponse, "order_id");
			
			if(expectedresponse.size()==actualresponse.size())
			{
				for(int i=0;i<actualresponse.size();i++) {
					for(int j=0;j<defaultcolumnresponse.size();j++) {
						if(actualresponse.get(i).get("column_id").equals(defaultcolumnresponse.get(j).get("column_id"))) {
							checkRecoveredResourceColumns(actualresponse.get(i),expectedresponse.get(i),defaultcolumnresponse.get(j),test);
							break;
						}
					}
				
				}
			}
			else {
				test.log(LogStatus.FAIL, "The expected count did not match the actual count");
				assertTrue("The expected count did not match the actual count", false);
			 }	
		}	
		else {
			String code = ExpectedErrorMessage.getCodeString();
	    	String message = ExpectedErrorMessage.getStatus();
	    	if(code.contains("0D00006")){
				 message = message.replace("{0}", defaultcolumnresponse.get(defaultcolumnresponse.size()-1).get("order_id").toString());
			 }
	    	
	    	spogServer.checkErrorCode(response,code);
	    	test.log(LogStatus.PASS, "The error code matched with the expected "+code);
	    	spogServer.checkErrorMessage(response,message);
	    	test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
			
		}
		return response;
	}
	/**
	 * get system recovered resources filters 
	 * @author Ramya.Nagepalli
	 * @param user_id
	 * @param org_id
	 * @param validToken
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * return response
	 */

	public Response getSystemRecoveredResourcesFilters(String user_id, String org_id,
			String validToken, int expectedStatusCode, SpogMessageCode expectedErrorMessage, ExtentTest test) {
		
		
		Response response = spogRecoveredResourceInvoker.getSystemRecoveredResourcesFilters(validToken);
		
		if(expectedStatusCode==SpogConstants.SUCCESS_GET_PUT_DELETE)
		{
	
		}
		else
		{
			String code = expectedErrorMessage.getCodeString();
			
			String message = expectedErrorMessage.getStatus();
			
			spogServer.checkErrorCode(response,code);
			
			
			test.log(LogStatus.PASS, "The error code matched with the expected "+code);
			
			spogServer.checkErrorMessage(response,message);
			
			test.log(LogStatus.PASS, "The expected error message matched " + message);
		}
		return response;
	}
	/**
	 * create specified user recovered resources filters
	 * @author Ramya.Nagepalli
	 * @param user_id
	 * @param policy_id
	 * @param state
	 * @param oSmajor
	 * @param recoveredResourceType
	 * @param filter_name
	 * @param validToken
	 * @param is_default
	 * @param test
	 * return response
	 */

	public Response createSpecifiedUserRecoveredResourcesFilters(String user_id, String validToken, String policy_id,
			String state, String oSmajor, String recoveredResourceType, String filter_name, String is_default,
			ExtentTest test) {
		HashMap<String, Object> resourceFilterInfo = new HashMap<String, Object>();
		resourceFilterInfo = jp.composeRecoveredResourcesFilter(policy_id, state, oSmajor, recoveredResourceType, filter_name,
				is_default);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!resourceFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.recoveredresource_filter.toString(),
					filter_name, user_id, "none",
					Boolean.valueOf(is_default), resourceFilterInfo);
		}
		
		test.log(LogStatus.INFO, "Call the Rest API to create recovered resource filters");
		Response response = spogServer.createFilters(validToken, filter_info, "", test);

		/*Response response = spogRecoveredResourceInvoker.createSpecifiedUserRecoveredResourcesFilters(user_id,
				validToken, FilterInfo, test);*/

		return response;
	}
	
/**
 * @author Ramya.Nagepalli
 * 
 * - Added view_type param
 * @param user_id
 * @param validToken
 * @param policy_id
 * @param state
 * @param oSmajor
 * @param recoveredResourceType
 * @param filter_name
 * @param is_default
 * @param view_type
 * @param test
 * @return
 */
	public Response createSpecifiedUserRecoveredResourcesFilters(String user_id, String validToken, String policy_id,
			String state, String oSmajor, String recoveredResourceType, String filter_name, String is_default,
			String view_type, ExtentTest test) {
		HashMap<String, Object> resourceFilterInfo = new HashMap<String, Object>();
		resourceFilterInfo = jp.composeRecoveredResourcesFilter(policy_id, state, oSmajor, recoveredResourceType, filter_name,
				is_default, view_type);
		
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!resourceFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.recoveredresource_filter.toString(),
					filter_name, user_id, "none",
					Boolean.valueOf(is_default), resourceFilterInfo);
		}
		
		test.log(LogStatus.INFO, "Call the Rest API to create recovered resource filters");
		Response response = spogServer.createFilters(validToken, filter_info, "", test);
/*
		Response response = spogRecoveredResourceInvoker.createSpecifiedUserRecoveredResourcesFilters(user_id,
				validToken, FilterInfo, test);*/

		return response;
	}

	/**
	 * check recovered resources filters
	 * 
	 * @author Ramya.Nagepalli
	 * @param response
	 * @param filter_id
	 * @param policy_id
	 * @param state
	 * @param oSmajor
	 * @param recoveredResourceType
	 * @param filter_name
	 * @param is_default
	 * @param org_id
	 * @param user_id
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 * @param expected_data
	 */
	public void checkRecoveredResourceFilters(Response response, String filter_id, String policy_id, String state,
			String oSmajor, String recoveredResourceType, String filter_name, String is_default, String org_id,
			String user_id, int expectedStatusCode, SpogMessageCode expectedErrorMessage, ExtentTest test,
			ArrayList<HashMap<String, Object>> expected_data/*, String view_type*/) {
		// TODO Auto-generated method stub
		String view_type = "origin";
		
		HashMap<String, Object> actualresponse = null;
		ArrayList<HashMap<String, Object>> actual_data = null;

		spogServer.checkResponseStatus(response, expectedStatusCode);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE
				|| expectedStatusCode == SpogConstants.SUCCESS_POST) {

			if (!(expected_data.isEmpty())) {
				actual_data = response.then().extract().path("data");

				int length = actual_data.size();

				for (int i = 0; i < actual_data.size(); i++) {
					for (int j = 0; j < expected_data.size(); j++) {

						String response_filter_name = response.then().extract().path("data[" + i + "].filter_name")
								.toString();

						if (response_filter_name.equals(expected_data.get(j).get("filter_name").toString())) {

							String act_view_type = response.then().extract().path("data[" + i + "].view_type");
							/*if (view_type.equals(""))
								view_type = "origin";*/

							spogServer.assertResponseItem(view_type, act_view_type, test);

							String response_org_id = response.then().extract().path("data[" + i + "].organization_id");
							spogServer.assertResponseItem(org_id, response_org_id, test);

							String response_user_id = response.then().extract().path("data[" + i + "].user_id");
							spogServer.assertResponseItem(user_id, response_user_id, test);

							ArrayList<String> response_policy_id = response.then().extract()
									.path("data[" + i + "].policy_id");
							spogServer.assertResponseItem(expected_data.get(j).get("policy_id"), response_policy_id,
									test);

							ArrayList<String> response_state = response.then().extract().path("data[" + i + "].state");
							spogServer.assertResponseItem(expected_data.get(j).get("state"), response_state, test);

							response_filter_name = response.then().extract().path("data[" + i + "].filter_name");
							spogServer.assertResponseItem(expected_data.get(j).get("filter_name"), response_filter_name,
									test);

							String response_is_default = response.then().extract().path("data[" + i + "].is_default")
									.toString();
							if (i == length - 1) {
								spogServer.assertResponseItem(expected_data.get(j).get("is_default"),
										response_is_default, test);
							} else {
								// spogServer.assertResponseItem("false",
								// response_is_default,test);
							}
						}
					}

				}
				test.log(LogStatus.PASS, "The actual response data matched with the expected data ");
			} else {
				String response_org_id = response.then().extract().path("data.organization_id");
				spogServer.assertResponseItem(org_id, response_org_id, test);

				String response_user_id = response.then().extract().path("data.user_id");
				spogServer.assertResponseItem(user_id, response_user_id, test);

				ArrayList<String> exp_policy_id = new ArrayList<String>();
				exp_policy_id.add(policy_id);

				ArrayList<String> response_policy_id = response.then().extract().path("data.policy_id");
				spogServer.assertResponseItem(exp_policy_id, response_policy_id, test);

				ArrayList<String> exp_state = new ArrayList<String>();
				exp_state.add(state);

				ArrayList<String> response_state = response.then().extract().path("data.state");
				spogServer.assertResponseItem(exp_state, response_state, test);

				ArrayList<String> exp_os_major = new ArrayList<String>();
				exp_os_major.add(oSmajor);

				String response_filter_name = response.then().extract().path("data.filter_name");
				spogServer.assertResponseItem(filter_name, response_filter_name, test);

				String response_is_default = response.then().extract().path("data.is_default").toString();
				spogServer.assertResponseItem(is_default, response_is_default, test);

				test.log(LogStatus.PASS, "The actual response data matched with the expected data ");

			}

		}

		else {
			String code = expectedErrorMessage.getCodeString();

			String message = expectedErrorMessage.getStatus();

			spogServer.checkErrorCode(response, code);
			if (code.contains("00A00402")) {
				message = message.replace("{0}", filter_id);
				message = message.replace("{1}", user_id);
			}

			test.log(LogStatus.PASS, "The error code matched with the expected " + code);

			spogServer.checkErrorMessage(response, message);

			test.log(LogStatus.PASS, "The expected error message matched " + message);

		}
	}

	/**
	 * delete specified user recovered resources filters
	 * @author Ramya.Nagepalli
	 * @param user_id
	 * @param filter_id
	 * @param validToken
	 * @param expectedStatusCode
	 * return response
	 */
	public Response deleteSpecifiedUserRecoveredResourcesFiltersByFilterId(String user_id,String filter_id, String validToken,int expectedStatusCode, ExtentTest test) {
		
		Response response=spogServer.deleteFiltersByID(validToken, filter_id, user_id, test);
		/*Response response=spogRecoveredResourceInvoker.deleteSpecifiedUserRecoveredResourcesFiltersByFilterId(user_id,filter_id,validToken,test);*/
		spogServer.checkResponseStatus(response, expectedStatusCode);
		return response;
	
	}
	/**
	 * create logged in user  recovered resources filters
	 * @author Ramya.Nagepalli
	 * @param validToken
	 * @param policy_id
	 * @param state
	 * @param oSmajor
	 * @param recoveredResourceType
	 * @param filter_name
	 * @param is_default
	 * @param test
	 * return response
	 */

	public Response createLoggedInUserRecoveredResourcesFilters(String validToken,String policy_id,String state,String oSmajor,String recoveredResourceType,String filter_name,String is_default,
			 ExtentTest test) {
		HashMap<String, Object> resourceFilterInfo = new HashMap<String, Object>();
		resourceFilterInfo = jp.composeRecoveredResourcesFilter(policy_id, state, oSmajor, recoveredResourceType, filter_name, is_default);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!resourceFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.recoveredresource_filter.toString(),
					filter_name, "none", "none",
					Boolean.valueOf(is_default), resourceFilterInfo);
		}
		test.log(LogStatus.INFO, "Call the Rest API to create recovered resource filters");
		Response response = spogServer.createFilters(validToken, filter_info, "", test);
		
		/*Response response=spogRecoveredResourceInvoker.createLoggedInUserRecoveredResourcesFilters(validToken,FilterInfo,test);*/
		
		return response;
	}
	
	/**
	 * createLoggedInUserRecoveredResourcesFilters
	 * 
	 * @author Ramya.Nagepalli
	 * @param validToken
	 * @param policy_id
	 * @param state
	 * @param oSmajor
	 * @param recoveredResourceType
	 * @param filter_name
	 * @param is_default
	 * @param view_type
	 * @param test
	 * @return
	 */
	public Response createLoggedInUserRecoveredResourcesFilters(String validToken, String policy_id, String state,
			String oSmajor, String recoveredResourceType, String filter_name, String is_default, String view_type,
			ExtentTest test) {
		HashMap<String, Object> resourceFilterInfo = new HashMap<String, Object>();
		resourceFilterInfo = jp.composeRecoveredResourcesFilter(policy_id, state, oSmajor, recoveredResourceType, filter_name,
				is_default, view_type);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!resourceFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.recoveredresource_filter.toString(),
					filter_name, "none", "none",
					Boolean.valueOf(is_default), resourceFilterInfo);
		}
		test.log(LogStatus.INFO, "Call the Rest API to create recovered resource filters");
		Response response = spogServer.createFilters(validToken, filter_info, "", test);
		/*Response response = spogRecoveredResourceInvoker.createLoggedInUserRecoveredResourcesFilters(validToken,
				FilterInfo, test);*/

		return response;
	}
	/**
	 * delete logged in user  recovered resources filters
	 * @author Ramya.Nagepalli
	 * @param validToken
	 * @param filter_id
	 * @param expectedStatusCode
	 * @param test
	 * return response
	 */

	public Response deleteLoggedInUserRecoveredResourcesFilters(String validToken, String filter_id,
			int expectedStatusCode, ExtentTest test) {
		
		Response response=spogServer.deleteFiltersByID(validToken, filter_id, "none", test);
		/*Response response=spogRecoveredResourceInvoker.deleteLoggedInUserRecoveredResourcesFilters(validToken,filter_id,test);
		*/spogServer.checkResponseStatus(response, expectedStatusCode);
		return response;
	}
	
	
	/**
	 * update specified user  recovered resources filters
	 * @author Ramya.Nagepalli
	 * @param user_id
	 * @param validToken
	 * @param filter_id
	 * @param policy_id
	 * @param state
	 * @param oSmajor
	 * @param recoveredResourceType
	 * @param filter_name
	 * @param is_default
	 * @param expectedStatusCode
	 * @param test
	 * return response
	 */

	public Response updateSpecifiedUserRecoveredResourcesFilters(String user_id, String validToken, String filter_id,
			String policy_id,String state,String oSmajor,String recoveredResourceType,String filter_name,String is_default,int expectedStatusCode, ExtentTest test) {
		HashMap<String, Object> resourceFilterInfo = new HashMap<String, Object>();
		resourceFilterInfo = jp.composeRecoveredResourcesFilter(policy_id, state, oSmajor, recoveredResourceType, filter_name, is_default);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!resourceFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.recoveredresource_filter.toString(),
					filter_name, user_id, "none",
					Boolean.valueOf(is_default), resourceFilterInfo);
		}
		test.log(LogStatus.INFO, "Call the Rest API to update recovered resource filters");
		Response response=spogServer.updateFilterById(validToken, filter_id, user_id, filter_info, "", test);
	/*	Response response=spogRecoveredResourceInvoker.updateSpecifiedUserRecoveredResourcesFilters(user_id,validToken,filter_id,FilterInfo,test);
	*/	spogServer.checkResponseStatus(response, expectedStatusCode);
		return response;
	}
	
	/**
	 * updateSpecifiedUserRecoveredResourcesFilters
	 * - Added view_type param
	 * 
	 * @author Ramya.Nagepalli
	 * @param user_id
	 * @param validToken
	 * @param filter_id
	 * @param policy_id
	 * @param state
	 * @param oSmajor
	 * @param recoveredResourceType
	 * @param filter_name
	 * @param is_default
	 * @param expectedStatusCode
	 * @param view_type
	 * @param test
	 * @return
	 */
	public Response updateSpecifiedUserRecoveredResourcesFilters(String user_id, String validToken, String filter_id,
			String policy_id, String state, String oSmajor, String recoveredResourceType, String filter_name,
			String is_default, int expectedStatusCode, String view_type, ExtentTest test) {

		HashMap<String, Object> resourceFilterInfo = new HashMap<String, Object>();
		resourceFilterInfo = jp.composeRecoveredResourcesFilter(policy_id, state, oSmajor, recoveredResourceType, filter_name,
				is_default, view_type);

		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!resourceFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.recoveredresource_filter.toString(),
					filter_name, user_id, "none",
					Boolean.valueOf(is_default), resourceFilterInfo);
		}
		test.log(LogStatus.INFO, "Call the Rest API to update recovered resource filters");
		Response response=spogServer.updateFilterById(validToken, filter_id, user_id, filter_info, "", test);
		/*Response response = spogRecoveredResourceInvoker.updateSpecifiedUserRecoveredResourcesFilters(user_id,
				validToken, filter_id, FilterInfo, test);*/

		spogServer.checkResponseStatus(response, expectedStatusCode);

		return response;
	}
	
	/**
	 * update specified user  recovered resources filters
	 * 
	 * @author Ramya.Nagepalli 
	 * @param validToken
	 * @param filter_id
	 * @param policy_id
	 * @param state
	 * @param oSmajor
	 * @param recoveredResourceType
	 * @param filter_name
	 * @param is_default
	 * @param expectedStatusCode
	 * @param test
	 * return response
	 */

	public Response updateLoggedInUserRecoveredResourcesFilters(String validToken, String filter_id,
			String policy_id,String state,String oSmajor,String recoveredResourceType,String filter_name,String is_default, int expectedStatusCode, ExtentTest test) {
		HashMap<String, Object> resourceFilterInfo = new HashMap<String, Object>();
		resourceFilterInfo = jp.composeRecoveredResourcesFilter(policy_id, state, oSmajor, recoveredResourceType, filter_name, is_default);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!resourceFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.recoveredresource_filter.toString(),
					filter_name,  "none", "none",
					Boolean.valueOf(is_default), resourceFilterInfo);
		}
		test.log(LogStatus.INFO, "Call the Rest API to update recovered resource filters");
		Response response=spogServer.updateFilterById(validToken, filter_id,  "none", filter_info, "", test);
		/*Response response=spogRecoveredResourceInvoker.updateLoggedInUserRecoveredResourcesFilters(validToken,filter_id,FilterInfo,test);
		*/
		spogServer.checkResponseStatus(response, expectedStatusCode);
		return response;
	}
	
	/**
	 * updateLoggedInUserRecoveredResourcesFilters
	 * 
	 * @param validToken
	 * @param filter_id
	 * @param policy_id
	 * @param state
	 * @param oSmajor
	 * @param recoveredResourceType
	 * @param filter_name
	 * @param is_default
	 * @param expectedStatusCode
	 * @param view_type
	 * @param test
	 * @return
	 */
	public Response updateLoggedInUserRecoveredResourcesFilters(String validToken, String filter_id, String policy_id,
			String state, String oSmajor, String recoveredResourceType, String filter_name, String is_default,
			int expectedStatusCode, String view_type, ExtentTest test) {
		// TODO Auto-generated method stub
		HashMap<String, Object> resourceFilterInfo = new HashMap<String, Object>();
		resourceFilterInfo = jp.composeRecoveredResourcesFilter(policy_id, state, oSmajor, recoveredResourceType, filter_name,
				is_default, view_type);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!resourceFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.recoveredresource_filter.toString(),
					filter_name,  "none", "none",
					Boolean.valueOf(is_default), resourceFilterInfo);
		}
		test.log(LogStatus.INFO, "Call the Rest API to update recovered resource filters");
		Response response=spogServer.updateFilterById(validToken, filter_id,  "none", filter_info, "", test);
/*
		Response response = spogRecoveredResourceInvoker.updateLoggedInUserRecoveredResourcesFilters(validToken,
				filter_id, FilterInfo, test);*/
		spogServer.checkResponseStatus(response, expectedStatusCode);
		return response;
	}

	/**
	 * get specified user  recovered resources filters
	 * @author Ramya.Nagepalli 
	 * @param user_id
	 * @param validToken
	 * @param expectedStatusCode
	 * @param test
	 * return response
	 */
	public Response getSpecifiedUserRecoveredResourcesFilters(String user_id, String validToken,
			int expectedStatusCode, ExtentTest test) {
		Response response =spogServer.getFilters(validToken, user_id,  filterType.recoveredresource_filter.toString(), "none", test);
		spogServer.checkResponseStatus(response, expectedStatusCode);
		return response;
	}
	
	/**
	 * get specified user  recovered resources filters by filter Id
	 * @author Ramya.Nagepalli 
	 * @param user_id
	 * @param filter_id
	 * @param validToken
	 * @param expectedStatusCode
	 * @param test
	 * return response
	 */
	public Response getSpecifiedUserRecoveredResourcesFilterByFilterId(String user_id, String filter_id,
			String validToken, int expectedStatusCode, ExtentTest test) {
		Response response =spogServer.getFiltersById(validToken, filter_id, filterType.recoveredresource_filter.toString(), user_id, "none", test);
		spogServer.checkResponseStatus(response, expectedStatusCode);
		return response;
	}
	
	/**
	 * get logged in user  recovered resources filters 
	 * @author Ramya.Nagepalli 
	 * @param validToken
	 * @param expectedStatusCode
	 * @param test
	 * return response
	 */
	public Response getLoggedInUserRecoveredResourcesFilters(String validToken, int expectedStatusCode,
			ExtentTest test) {
		Response response =spogServer.getFilters(validToken, "none", filterType.recoveredresource_filter.toString(), "", test);
		spogServer.checkResponseStatus(response, expectedStatusCode);
		
		return response;
	}
	
	
	/**
	 * get logged in  user  recovered resources filters by filter Id
	 * @author Ramya.Nagepalli 
	 * @param validToken
	 * @param filter_id
	 * @param expectedStatusCode
	 * @param test
	 * return response
	 */

	public Response getLoggedInUserRecoveredResourcesFilterByFilterId(String validToken, String filter_id,
			int expectedStatusCode, ExtentTest test) {
	/*	Response response=spogRecoveredResourceInvoker.getLoggedInUserRecoveredResourcesFilterByFilterId(validToken,filter_id,test);*/
		Response response =spogServer.getFiltersById(validToken, filter_id, filterType.recoveredresource_filter.toString(), "none", "none", test);
		spogServer.checkResponseStatus(response, expectedStatusCode);
		return response;
	}
	
}
