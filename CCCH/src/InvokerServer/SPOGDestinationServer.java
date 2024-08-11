package InvokerServer;
import java.util.Iterator;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.print.DocFlavor.STRING;

import java.util.Map.Entry;

import org.apache.bcel.generic.NEW;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import api.Datacenter;


import org.testng.Assert;

import com.google.inject.PrivateBinder;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.CloudDirectRetentionValues;
import Constants.ConnectionStatus;
import Constants.DestinationStatus;
import Constants.DestinationType;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import Constants.FilterTypes.filterType;
import dataPreparation.JsonPreparation;
import genericutil.ErrorHandler;
import invoker.SPOGDestinationInvoker;
import invoker.SPOGInvoker;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class SPOGDestinationServer {

	private static JsonPreparation jp = new JsonPreparation();
	private SPOGServer spogServer;
	private Org4SPOGServer org4SPOGServer;
	private Policy4SPOGServer policy4SpogServer;
	private String csrAdmin="xiang_csr@arcserve.com";
	private String csrPwd="Caworld_2017";
	
	HashMap<String,String >retention;
	HashMap<String,Object>cloud_direct_volume;
	HashMap<String,Object>cloud_dedupe_volume;

	private SPOGDestinationInvoker spogDestinationInvoker;
	public static ErrorHandler errorHandle = ErrorHandler.getErrorHandler();
	private ExtentTest test;

	private String Uuid;
	private HashMap<String, Object> Retention=new HashMap<String,Object>();

	public void setToken(String token) {
		spogDestinationInvoker.setToken(token);
	}

	public SPOGDestinationServer(String baseURI, String port) {
		
		spogDestinationInvoker = new SPOGDestinationInvoker(baseURI, port);
		org4SPOGServer= new Org4SPOGServer(baseURI, port);
		spogServer = new SPOGServer(baseURI, port);
		policy4SpogServer = new Policy4SPOGServer(baseURI, port);
		
		retention=new HashMap<String,String>();
		cloud_direct_volume=new HashMap<String,Object>();
	    cloud_dedupe_volume=new HashMap<String,Object>();
	}

	/**
	 * create a destination filter and return filter_id
	 * 
	 * @author Zhaoguo.Ma
	 * @param userID
	 * @param filterName
	 * @param destinationName
	 * @param policyID
	 * @param destinationType
	 * @param isDefault
	 * @param test
	 * @return
	 */
	public String createDestinationFilterWithCheck(String userID, String filterName, String destinationName,
			String policyID, String destinationType, String isDefault, ExtentTest test) {

		Map<String, Object> destinationFilterInfo = jp.composeDestinationFilterInfo(filterName, destinationName,
				policyID, destinationType, isDefault);
		
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!destinationFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.destination_filter.toString(),
					filterName, userID, "none",
					Boolean.valueOf(isDefault), (HashMap<String,Object>)destinationFilterInfo);
		}

		Response response = spogServer.createFilters(spogDestinationInvoker.getToken(), filter_info, "", test);
		/*Response response = spogDestinationInvoker.createDestinationFilter(userID, destinationFilterInfo);*/
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);

		String responseDestinationType = response.then().extract().path("data.destination_type").toString().replace("[", "").replace("]", "");
		ArrayList<String> responsePolicyID = response.then().extract().path("data.policy_id");
		boolean responseIsDefault = response.then().extract().path("data.is_default");
		String responseDestinationName = response.then().extract().path("data.destination_name");
		String filterID = response.then().extract().path("data.filter_id");

		// String responseUserID = GetLoggedinUser_UserID();
		// String responseOrganizationID = GetLoggedinUserOrganizationID();

		response.then().body("data.filter_name", equalTo(filterName));
		// .body("data.user_id", equalTo(responseUserID)).body("data.organization_id",
		// equalTo(responseOrganizationID));

		spogServer.assertResponseItem(destinationName, responseDestinationName);

		spogServer.assertFilterItem(policyID, responsePolicyID);

		if (destinationType == null || destinationType.equalsIgnoreCase("none") || (destinationType == "")) {
			assertEquals(responseDestinationType, "all");
		} else {
			assertEquals(responseDestinationType, destinationType);
		}

		if (isDefault == null || isDefault.equalsIgnoreCase("none") || isDefault == ""
				|| isDefault.equalsIgnoreCase("false")) {
			assertEquals(responseIsDefault, false);
		} else if (isDefault.equalsIgnoreCase("true")) {
			assertEquals(responseIsDefault, true);
		}

		return filterID;
	}
	
	/**
	 * create a destination filter and return filter_id.. Used for audit trail
	 * 
	 * @author Kiran.Sripada
	 * @param userID
	 * @param filterName
	 * @param destinationName
	 * @param policyID
	 * @param destinationType
	 * @param isDefault
	 * @param test
	 * @return Response
	 */
	
	public Response createDestinationFilter(String userID, String filterName, String destinationName,
			String policyID, String destinationType, String isDefault, ExtentTest test) {

		Map<String, Object> destinationFilterInfo = jp.composeDestinationFilterInfo(filterName, destinationName,
				policyID, destinationType, isDefault);
		
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!destinationFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.destination_filter.toString(),
					filterName, userID, "none",
					Boolean.valueOf(isDefault), (HashMap<String,Object>)destinationFilterInfo);
		}
		Response response = spogServer.createFilters(spogDestinationInvoker.getToken(), filter_info, "", test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);

		String responseDestinationType = response.then().extract().path("data.destination_type");
		ArrayList<String> responsePolicyID = response.then().extract().path("data.policy_id");
		boolean responseIsDefault = response.then().extract().path("data.is_default");
		String responseDestinationName = response.then().extract().path("data.destination_name");
		//String filterID = response.then().extract().path("data.filter_id");

		// String responseUserID = GetLoggedinUser_UserID();
		// String responseOrganizationID = GetLoggedinUserOrganizationID();

		response.then().body("data.filter_name", equalTo(filterName));
		// .body("data.user_id", equalTo(responseUserID)).body("data.organization_id",
		// equalTo(responseOrganizationID));

		spogServer.assertResponseItem(destinationName, responseDestinationName);

		spogServer.assertFilterItem(policyID, responsePolicyID);

		if (destinationType == null || destinationType.equalsIgnoreCase("none") || (destinationType == "")) {
			assertEquals(responseDestinationType, "all");
		} else {
			assertEquals(responseDestinationType, destinationType);
		}

		if (isDefault == null || isDefault.equalsIgnoreCase("none") || isDefault == ""
				|| isDefault.equalsIgnoreCase("false")) {
			Assert.assertEquals(responseIsDefault, false);
		} else if (isDefault.equalsIgnoreCase("true")) {
			Assert.assertEquals(responseIsDefault, true);
		}

		return response;
	}

	/**
     * update a destination filter and return filter_id
     * 
     * @author yin.li
     * @param userID
     * @param filterName
     * @param destinationName
     * @param policyID
     * @param destinationType
     * @param isDefault
     * @param test
     * @return
     */
    public Response updateDestinationFilterWithCheck(String userID, String destFilterId, String filterName, String destinationName,
            String policyID, String destinationType, String isDefault, ExtentTest test, boolean verifyValue) {

        Map<String, Object> destinationFilterInfo = jp.composeDestinationFilterInfo(filterName, destinationName,
                policyID, destinationType, isDefault);
        
        HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!destinationFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.destination_filter.toString(),
					filterName, userID, "none",
					Boolean.valueOf(isDefault), (HashMap<String,Object>)destinationFilterInfo);
		}
		Response response = spogServer.updateFilterById(spogDestinationInvoker.getToken(), destFilterId, userID, filter_info, "", test);
     /*   Response response = spogDestinationInvoker.updateDestinationFilter(userID, destFilterId, destinationFilterInfo);*/
        if (! verifyValue) {
          return response;
        }
        spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
        String responseDestinationType = response.then().extract().path("data.destination_type");
        ArrayList<String> responsePolicyID = response.then().extract().path("data.policy_id");
        boolean responseIsDefault = response.then().extract().path("data.is_default");
        String responseDestinationName = response.then().extract().path("data.destination_name");
        String filterID = response.then().extract().path("data.filter_id");

        response.then().body("data.filter_name", equalTo(filterName));
        spogServer.assertResponseItem(destinationName, responseDestinationName);
        spogServer.assertFilterItem(policyID, responsePolicyID);
        if (destinationType == null || destinationType.equalsIgnoreCase("none") || (destinationType == "")) {
            assertEquals(responseDestinationType, "all");
        } else {
            assertEquals(responseDestinationType, destinationType);
        }

        if (isDefault == null || isDefault.equalsIgnoreCase("none") || isDefault == ""
                || isDefault.equalsIgnoreCase("false")) {
            assertEquals(responseIsDefault, false);
        } else if (isDefault.equalsIgnoreCase("true")) {
            assertEquals(responseIsDefault, true);
        }

        return response;
    }
    
    public Response updateDestinationFilterForLoggedInUserWithCheck(String destFilterId, String filterName, String destinationName,
        String policyID, String destinationType, String isDefault, ExtentTest test, boolean verifyValue) {

    Map<String, Object> destinationFilterInfo = jp.composeDestinationFilterInfo(filterName, destinationName,
            policyID, destinationType, isDefault);

   /* Response response = spogDestinationInvoker.updateDestinationFilterForLoggedInUser(destFilterId, destinationFilterInfo);*/
    HashMap<String, Object> filter_info = new HashMap<String, Object>();
  		if (!destinationFilterInfo.isEmpty()) {
  			filter_info = jp.composeFilterInfo(filterType.destination_filter.toString(),
  					filterName,  "none", "none",
  					Boolean.valueOf(isDefault), (HashMap<String,Object>)destinationFilterInfo);
  		}
  		Response response = spogServer.updateFilterById(spogDestinationInvoker.getToken(), destFilterId,  "none", filter_info, "", test);
    
    if (! verifyValue) {
      return response;
    }

    spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);

    String responseDestinationType = response.then().extract().path("data.destination_type");
    ArrayList<String> responsePolicyID = response.then().extract().path("data.policy_id");
    boolean responseIsDefault = response.then().extract().path("data.is_default");
    String responseDestinationName = response.then().extract().path("data.destination_name");
    String filterID = response.then().extract().path("data.filter_id");


    response.then().body("data.filter_name", equalTo(filterName));

    spogServer.assertResponseItem(destinationName, responseDestinationName);

    spogServer.assertFilterItem(policyID, responsePolicyID);

    if (destinationType == null || destinationType.equalsIgnoreCase("none") || (destinationType == "")) {
        assertEquals(responseDestinationType, "all");
    } else {
        assertEquals(responseDestinationType, destinationType);
    }

    if (isDefault == null || isDefault.equalsIgnoreCase("none") || isDefault == ""
            || isDefault.equalsIgnoreCase("false")) {
        Assert.assertEquals(responseIsDefault, false);
    } else if (isDefault.equalsIgnoreCase("true")) {
        Assert.assertEquals(responseIsDefault, true);
    }

    return response;
}

	public void createDestinationFilterAndCheckCode(String userID, String filterName, String destinationName,
			String policyID, String destinationType, String isDefault, int statusCode, String errorCode,
			ExtentTest test) {

		Map<String, Object> destinationFilterInfo = jp.composeDestinationFilterInfo(filterName, destinationName,
				policyID, destinationType, isDefault);
		HashMap<String, Object> filter_info = new HashMap<String, Object>();
		if (!destinationFilterInfo.isEmpty()) {
			filter_info = jp.composeFilterInfo(filterType.destination_filter.toString(),
					filterName, userID, "none",
					Boolean.valueOf(isDefault), (HashMap<String,Object>)destinationFilterInfo);
		}
		Response response = spogServer.createFilters(spogDestinationInvoker.getToken(), filter_info, "", test);

		/*Response response = spogDestinationInvoker.createDestinationFilter(userID, destinationFilterInfo);*/
		spogServer.checkResponseStatus(response, statusCode);
		spogServer.checkErrorCode(response, errorCode);
	}

	
	
	
	
	/**
	 * create destination
	 * @author leiyu.wang
	 * @param organization_id
	 * @param site_id
	 * @param datacenterID
	 * @param destination_type
	 * @param destination_status
	 * @param primary_usage
	 * @param snapshot_usage
	 * @param total_usage
	 * @param volume_type
	 * @param hostname
	 * @param retention_id
	 * @param age_hours_max
	 * @param age_four_hours_max
	 * @param age_days_max
	 * @param age_weeks_max
	 * @param age_months_max
	 * @param age_years_max
	 * @param data_store_folder
	 * @param data_destination
	 * @param index_destination
	 * @param hash_destination
	 * @param concurrent_active_node
	 * @param is_deduplicated
	 * @param block_size
	 * @param hash_memory
	 * @param is_compressed
	 * @param encryption_password
	 * @param occupied_space
	 * @param store_data
	 * @param deduplication_rate
	 * @param compression_rate
	 * @param destination_name
	 * @param test
	 * @return
	 */
	
	public Response createDestination(String destination_id,String organization_id,String site_id, String datacenterID,String destination_type, String destination_status,String dedupe_savings,
			  //cloud_direct_volume parameters
			  String cloud_account_id, String volume_type,String hostname, String  retention_id, String retention_name,
			  String age_hours_max,String age_four_hours_max,String age_days_max,String age_weeks_max,String age_months_max, String age_years_max,
			  //cloud_dedupe_volume parameters
			  String concurrent_active_node, String is_deduplicated, String block_size, String is_compressed,String destination_name,ExtentTest test) {

			
		    errorHandle.printDebugMessageInDebugFile("compose destination info object");
		    test.log(LogStatus.INFO, "compose destination info object");
		    
		    
		     
		    Map<String, String> retentionInfo = null;
		    retentionInfo=jp.composeCloudDirectVolumeRetentionInfo(age_hours_max, age_four_hours_max, age_days_max, age_weeks_max, age_months_max, age_years_max);
		    
		    Map<String, Object> cloudDirectVolumeInfo= null;
		    cloudDirectVolumeInfo=jp.composeDestinationCloudDirectVolumeInfo(cloud_account_id, volume_type, hostname,retention_id,retention_name,  retentionInfo);
		    
		    Map<String, String> cloudDedupVolumeInfo= null;
		    cloudDedupVolumeInfo=jp.composeDestinationCloudDedupVolumeInfo(concurrent_active_node,is_deduplicated,  block_size,  is_compressed);
		    
		    
		    Map<String, Object> destinationInfo = jp.getDestinationInfo(destination_id,organization_id, site_id, datacenterID, destination_type, destination_status,dedupe_savings, cloudDirectVolumeInfo, cloudDedupVolumeInfo,
		    		destination_name);
		    errorHandle.printDebugMessageInDebugFile("create destination");
		    test.log(LogStatus.INFO, "create destination");
		    Response response = spogDestinationInvoker.createDestination(destinationInfo);

		    return response;
	}
	
	
	/**
	 * create destination with valid inputs will succeed
	 * @author leiyu.wang
	 * 
	 * @return destinationID
	 */
	public String createDestinationWithCheck(String destination_id,String organization_id,String site_id ,String datacenterID, String destination_type, String destination_status,String dedupe_savings,
			  //cloud_direct_volume parameters
			  String cloud_account_id, String volume_type,String hostname, String  retention_id, String retention_name,
			  String age_hours_max,String age_four_hours_max,String age_days_max,String age_weeks_max,String age_months_max, String age_years_max,
			  //cloud_dedupe_volume parameters
			  String concurrent_active_node, String is_deduplicated, String block_size, String is_compressed,String destination_name, ExtentTest test) {
		
		Response response = createDestination(destination_id,organization_id, site_id,datacenterID, destination_type, destination_status,dedupe_savings,
				cloud_account_id, volume_type, hostname, retention_id,retention_name,
				age_hours_max, age_four_hours_max, age_days_max, age_weeks_max, age_months_max, age_years_max,
				concurrent_active_node, is_deduplicated,
				   block_size, is_compressed, destination_name,		
				test);
		

		errorHandle.printDebugMessageInDebugFile("check status code");
		test.log(LogStatus.INFO, "check status code");
		response.then().statusCode(SpogConstants.SUCCESS_POST);
		
		errorHandle.printDebugMessageInDebugFile("response is "
				+ response.getBody().toString());		
		test.log(LogStatus.INFO, "create destination response data:"+response.then().extract().path("data"));
		
		response.then().body("data.destination_name", equalTo(destination_name));		
		response.then().body("data.destination_type",equalTo(destination_type));
		if(destination_type.equals("cloud_direct_volume")){
			response.then().body("data.cloud_direct_volume.volume_type", equalTo(volume_type));
			response.then().body("data.cloud_direct_volume.retention_id", equalTo(retention_id));
		}else if(destination_type.equals("cloud_hybrid_store")){
			
		}
		

		String ret_destination_id = null;
		ret_destination_id = response.then().extract().path("data.destination_id");
		
		errorHandle.printDebugMessageInDebugFile("get destination id:"+ret_destination_id);
		test.log(LogStatus.INFO, "get destination id:"+ret_destination_id);
		
		return ret_destination_id;		
				
	}

	/**
	 * @author leiyu.wang
	 * @param volumeID
	 * @param test
	 * @return
	 * @throws JSONException
	 */
	public Response RecycleDirectVolume(String volumeID,ExtentTest test) {
		Response response = spogDestinationInvoker.RecycleDirectVolume(volumeID);
		errorHandle.printDebugMessageInDebugFile("Recycle direct volume");
		if(response.then().extract().path("status").equals("SUCCESS"))	
			test.log(LogStatus.INFO, "recysle direct volume succeed");
		else
			test.log(LogStatus.WARNING,"resycle direct volume failed");
		
		return response;
		
	}
	
	public Response RecycleOrgFromCCWithoutCheck(String orgId) {
		Response response = spogDestinationInvoker.RecycleOrgFromCC(orgId);
		
		return response;
		
	}
	
	/**
	 * create destination with 403 error, need statusCode and errorCode input
	 * @author leiyu.wang
	 * @param organization_id
	 * @param site_id
	 * @param datacenterID
	 * @param destination_type
	 * @param destination_status
	 * @param primary_usage
	 * @param snapshot_usage
	 * @param total_usage
	 * @param volume_type
	 * @param hostname
	 * @param retention_id
	 * @param age_hours_max
	 * @param age_four_hours_max
	 * @param age_days_max
	 * @param age_weeks_max
	 * @param age_months_max
	 * @param age_years_max
	 * @param data_store_folder
	 * @param data_destination
	 * @param index_destination
	 * @param hash_destination
	 * @param concurrent_active_node
	 * @param is_deduplicated
	 * @param block_size
	 * @param hash_memory
	 * @param is_compressed
	 * @param encryption_password
	 * @param occupied_space
	 * @param store_data
	 * @param deduplication_rate
	 * @param compression_rate
	 * @param destination_name
	 * @param statusCode
	 * @param errorCode
	 * @param test
	 * @return 
	 */
	public String createDestinationWithCheckErrorCase(String destination_id,String organization_id,String site_id ,String datacenterID, String destination_type, String destination_status,String dedupe_savings,
			  //cloud_direct_volume parameters
			String cloud_account_id, String volume_type,String hostname, String  retention_id, String retention_name,
			  String age_hours_max,String age_four_hours_max,String age_days_max,String age_weeks_max,String age_months_max, String age_years_max,
			  //cloud_dedupe_volume parameters
			  String concurrent_active_node, String is_deduplicated, String block_size, String is_compressed,String destination_name,
			  int statusCode, String errorCode, ExtentTest test) {
		
		Response response = createDestination(destination_id,organization_id, site_id,datacenterID, destination_type, destination_status,dedupe_savings,
				cloud_account_id, volume_type, hostname, retention_id,retention_name,
				age_hours_max, age_four_hours_max, age_days_max, age_weeks_max, age_months_max, age_years_max,
				concurrent_active_node, is_deduplicated,  block_size,  is_compressed,destination_name,		
				test);	
		
	    
	    
	    response.then().statusCode(statusCode);
	    if (statusCode != SpogConstants.SUCCESS_POST) {	 
	    	errorHandle.printDebugMessageInDebugFile("create destination fail case");
		    test.log(LogStatus.INFO, "create destination fail case. "+statusCode);
	      
	      List<String> messageArray = response.body().jsonPath().getList("errors.code");
	      boolean find = false;
	      for (int i = 0; i < messageArray.size(); i++) {
	        if (messageArray.get(i).contains(errorCode)) {
	          find = true;
	          break;
	        }
	      }

	      if (find) {
	        assertTrue("error code check is correct", true);	        
	      } else {
	        assertEquals(messageArray.get(0), errorCode);
	      }
	      test.log(LogStatus.INFO, "create destination failed with error code " +errorCode);
	      return null;
	    } else {
	      return response.then().extract().path("data.destination_id");
	    }    
		
				
	}
	
	/**
	 * get destination datacenters
	 * @author leiyu.wang
	 * @param statusCode
	 * @param test
	 * @return 
	 * @throws JSONException 
	 */
	public Response getDestinationDatacenter(int statusCode, ExtentTest test) throws JSONException{
		Response response=spogDestinationInvoker.getDestinationDatacenters();
		errorHandle.printDebugMessageInDebugFile("Get datacenter from body is:" + response);
		spogServer.checkResponseStatus(response, statusCode);
		test.log(LogStatus.INFO, "Datacenters:"+response.then().extract().path("data"));
		
		if(statusCode==200){	
			test.log(LogStatus.INFO, "valid datacenters info");
			JSONObject jObj=new JSONObject(response.getBody().asString());
			JSONArray jArray=jObj.getJSONArray("data");
			if (jArray!=null){
				JSONObject obj=(JSONObject) jArray.get(0);
				assertEquals(obj.getString("datacenter_id"),"91a9b48e-6ac6-4c47-8202-614b5cdcfe0c");
				assertEquals(obj.getString("datacenter_location"),"Zetta Test");
				assertEquals(obj.getString("datacenter_region"),"US");
				/*JSONObject pro_obj=(JSONObject) obj.get("product_details");
				assertEquals(pro_obj.getBoolean("clouddirect_baas"),true);
				assertEquals(pro_obj.getBoolean("clouddirect_draas"),true);
				assertEquals(pro_obj.getBoolean("cloudhybrid_baas"),true);
				assertEquals(pro_obj.getBoolean("cloudhybrid_draas"),true);*/
								
				obj=(JSONObject) jArray.get(1);
				assertEquals(obj.getString("datacenter_id"),"99a9b48e-6ac6-4c47-8202-614b5cdcfe0c");
				assertEquals(obj.getString("datacenter_location"),"Zetta Stage");
				assertEquals(obj.getString("datacenter_region"),"US");
				/*pro_obj=(JSONObject) obj.get("product_details");
				assertEquals(pro_obj.getBoolean("clouddirect_baas"),true);
				assertEquals(pro_obj.getBoolean("clouddirect_draas"),true);
				assertEquals(pro_obj.getBoolean("cloudhybrid_baas"),true);
				assertEquals(pro_obj.getBoolean("cloudhybrid_draas"),true);*/
				

//				obj=(JSONObject) jArray.get(2);
//				assertEquals(obj.getString("datacenter_id"),"91a9b48e-6ac6-4c47-8202-614b5cdcfe0d");
//				assertEquals(obj.getString("datacenter_location"),"Private DC");
//				assertEquals(obj.getString("datacenter_region"),"US");
				
				
				obj=(JSONObject) jArray.get(2);
				assertEquals(obj.getString("datacenter_id"),"d193e09c-efff-45f7-b929-ea138cd3687b");
				assertEquals(obj.getString("datacenter_location"),"London,UK");
				assertEquals(obj.getString("datacenter_region"),"EU");
				/*pro_obj=(JSONObject) obj.get("product_details");
				assertEquals(pro_obj.getBoolean("clouddirect_baas"),true);
				assertEquals(pro_obj.getBoolean("clouddirect_draas"),true);
				assertEquals(pro_obj.getBoolean("cloudhybrid_baas"),true);
				assertEquals(pro_obj.getBoolean("cloudhybrid_draas"),true);*/
			}else{
				errorHandle.printDebugMessageInDebugFile("Get datacenter from body is null");
				test.log(LogStatus.FAIL, "Datacenters not found");
			}
		}
			
			
		return response;		
	}
	
	/**
	 * get destination datacenterID
	 * @author leiyu.wang
	 * @return
	 */
	public String[] getDestionationDatacenterID(){
		Response response=spogDestinationInvoker.getDestinationDatacenters();
		String id=response.then().extract().path("data.datacenter_id").toString();
		String[] datacenterIDs=((String) id.subSequence(1, id.length()-1)).split(", ");
	    return datacenterIDs;			
	}
	
	/**
	 * get destination datacenter name
	 * @author Prasad.Deverakonda
	 * @return
	 */
	public String[] getDestionationDatacenterName(){
		Response response=spogDestinationInvoker.getDestinationDatacenters();
		String Name = response.then().extract().path("data.datacenter_name").toString();
		String[] datacenterNames=((String) Name.subSequence(1, Name.length()-1)).split(", ");
	    return datacenterNames;			
	}
	
	
	/**
	 * get policy/backuptypes
	 * @author leiyu.wang
	 * @param statusCode
	 * @param test
	 * @return
	 */
	public Response getPoliciesBackuptypes(int statusCode, ExtentTest test){
		Response response=spogDestinationInvoker.getPoliciesBackuptypes();
		spogServer.checkResponseStatus(response, statusCode);
		
		if(statusCode==200){
			test.log(LogStatus.INFO, "valid backuptypes info");
			JsonPath jsonPathEvaluator= response.jsonPath();
    		System.out.println("response body:"+jsonPathEvaluator.get("data"));
			List<String> backupTypes=jsonPathEvaluator.getList("data");
			if(backupTypes.contains("windows_image")&&backupTypes.contains("remote_rps_replication")&&backupTypes.contains("cloud_direct_agentless"))
				test.log(LogStatus.INFO, "calid backuptypes succeed");
			else{
				errorHandle.printDebugMessageInDebugFile("Get backupTypes from body is incorrect");
				test.log(LogStatus.FAIL, "backupTypes incorrect");
			}
		}
		return response;
	}
	
	/**
	 * get policy types
	 * @author leiyu.wang
	 * @param statusCode
	 * @param test
	 * @return
	 */
	public Response getPolicyTypes(int statusCode, ExtentTest test){
		Response response=spogDestinationInvoker.getPolicyTypes();
		spogServer.checkResponseStatus(response, statusCode);
		if(statusCode==200){
			 JsonPath jsonPathEvaluator= response.jsonPath();		 

			  List<Object> dsList=jsonPathEvaluator.getList("data");
			  List<String> policy_type=jsonPathEvaluator.getList("data.policy_type");

			  Assert.assertEquals(policy_type.get(0), "cloud_direct_baas");
			  Assert.assertEquals(policy_type.get(1), "cloud_direct_draas");
			  Assert.assertEquals(policy_type.get(2), "cloud_direct_hypervisor");
			  Assert.assertEquals(policy_type.get(3), "cloud_hybrid_replication");
			  
			  List<String> name=jsonPathEvaluator.getList("data.name");
			  Assert.assertEquals(name.get(0), "cloud direct baas");
			  Assert.assertEquals(name.get(1), "cloud direct draas");
			  Assert.assertEquals(name.get(2), "cloud direct hypervisor");
			  Assert.assertEquals(name.get(3), "cloud hybrid replication");			  
		}
		
		return response;
	}
	
	/**
	 * delete a destination filter by filterId
	 * 
	 * @author Kiran.Sripada
	 * @param userID
	 * @param filter_Id
	 * @param token
	 * @param expectedstatuscode
	 * @param ExpectedErrorMessage
	 * 
	 */
	public void deletedestinationfilterbyfilterId(String user_Id, String filter_Id, String token, int expectedstatuscode, SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		
		test.log(LogStatus.INFO, "Delete the filter for a specified UserId " + user_Id);
	   /* Response response = spogDestinationInvoker.deletedestinationfilterbyfilterId(filter_Id, user_Id, token);*/
		 Response response = spogServer.deleteFiltersByID(token, filter_Id, user_Id, test);
	    spogServer.checkResponseStatus(response, expectedstatuscode);
	    if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
	      test.log(LogStatus.PASS,
	          "Successfully deleted the filter and the response status is " + response.getStatusCode());
	      
	    } else {
	    	String code = ExpectedErrorMessage.getCodeString();
	    	String message = ExpectedErrorMessage.getStatus();
	    	if(code.contains("00A00102")){
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
	public void deletedestinationfilterforLoggedInuser(String filter_Id, String token, int expectedstatuscode, SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		
		
	   /* Response response = spogDestinationInvoker.deletedestinationfilterforLoggedInuser(filter_Id, token);*/
		 Response response = spogServer.deleteFiltersByID(token, filter_Id, "none", test);
	    spogServer.checkResponseStatus(response, expectedstatuscode);
	    if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
	      test.log(LogStatus.PASS,
	          "Successfully deleted the filter and the response status is " + response.getStatusCode());
	      
	    } else {
	    	String code = ExpectedErrorMessage.getCodeString();
	    	String message = ExpectedErrorMessage.getStatus();
	    	if(code.contains("00A00102")){
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
	 * get the destination filter by filterId
	 * 
	 * @author Kiran.Sripada
	 * @param userID
	 * @param filter_Id
	 * @param token
	 * @param expectedresponse
	 * @param expectedstatuscode
	 * @param ExpectedErrorMessage
	 * @param extenttest
	 * 
	 */
	public void getdestinationfilterbyfilterId(String user_Id, String filter_Id, String token, 
			HashMap<String,Object>expected_response,int expectedstatuscode, SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		HashMap<String,Object> actual_response = new HashMap<>();
		
		int actual_size = expected_response.size();
		
		Response response = spogServer.getFiltersById(token, filter_Id, filterType.destination_filter.toString(), user_Id, "none",test);
	/*	Response response = spogDestinationInvoker.getdestinationfilterbyfilterId(filter_Id, user_Id, token);*/
		
		test.log(LogStatus.INFO, "Check the response status");
		spogServer.checkResponseStatus(response, expectedstatuscode);
		test.log(LogStatus.PASS, "The response status matched and the status code is "+expectedstatuscode);
		if(expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.INFO, "Extract the data from the response");
			actual_response = response.then().extract().path("data");
			
			test.log(LogStatus.INFO, "Compare the filter id");
			spogServer.assertResponseItem(actual_response.get("filter_id").toString(),expected_response.get("filter_id").toString(),test);
			
			test.log(LogStatus.INFO,"Compare the filter name");
			spogServer.assertResponseItem(actual_response.get("filter_name").toString(),expected_response.get("filter_name").toString(),test);
			
			test.log(LogStatus.INFO,"Compare the user id");
			spogServer.assertResponseItem(actual_response.get("user_id").toString(),expected_response.get("user_id").toString(),test);
			
			test.log(LogStatus.INFO,"Compare the organization id");
			spogServer.assertResponseItem(actual_response.get("organization_id").toString(),expected_response.get("organization_id").toString(),test);
			
			test.log(LogStatus.INFO,"Compare the destination name");
			if(expected_response.get("destination_name") == null || expected_response.get("destination_name") == "none") {
				test.log(LogStatus.PASS, "filter not applied on destination name");
				assertNull(null," filter not applied on destination name");
			}else {
				spogServer.assertResponseItem(actual_response.get("destination_name").toString(),expected_response.get("destination_name").toString(),test);
			}
			
			
			test.log(LogStatus.INFO,"Compare the policy id");
			ArrayList<String> policyids= (ArrayList<String>) actual_response.get("policy_id");
			
			if(policyids==null) {
				test.log(LogStatus.PASS, "filter not applied on policy id");
				assertNull(null," filter not applied on policy id");
			}else {
				if(policyids.size()>1) {
					for(int i=0; i<policyids.size(); i++) {
						actual_response.get(policyids.get(i));
						spogServer.assertResponseItem(actual_response.get(policyids.get(i)).toString(),expected_response.get("policy_id").toString(),test);
					}
				}else {
						spogServer.assertResponseItem(policyids.get(0).toString(),expected_response.get("policy_id").toString(),test);
				}
			}
			
			test.log(LogStatus.INFO,"Compare the destinatin type");
			if(expected_response.get("destination_type") == null || expected_response.get("destination_type") == "none")
				spogServer.assertResponseItem(actual_response.get("destination_type").toString(),"all",test);
			else {
				spogServer.assertResponseItem(actual_response.get("destination_type").toString(),expected_response.get("destination_type").toString(),test);
			}
			
			test.log(LogStatus.INFO,"Compare the default value");
			//spogServer.assertResponseItem(actual_response.get("is_default").toString(),expected_response.get("is_default").toString(),test);
			test.log(LogStatus.INFO,"Compare the count");
			//spogServer.assertResponseItem(actual_response.get("count").toString(),"0",test);
			
		}else {
			String code = ExpectedErrorMessage.getCodeString();
	    	String message = ExpectedErrorMessage.getStatus();
	    	if(code.contains("00A00102")){
	    		message = message.replace("{0}", filter_Id);
	    		message= message.replace("{1}", user_Id);
	    	}
	    	spogServer.checkErrorCode(response,code);
	    	test.log(LogStatus.INFO, "The error code matched with the expected "+code);
	    	spogServer.checkErrorMessage(response,message);
	    	test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		}
		
		
	}
	
	public Response getDestinationFilterByUserId(String userId, HashMap<String, String> params){
		  Response response;
		  
		  response = spogDestinationInvoker.getDestinationFilterByUserId(userId, params);
		  return response;
	}
	
	/**
	 * get destination filters for logged in user
	 * @author Kiran.Sripada
	 * @param filter_Id
	 * @param token
	 * @param HashMap of expectedresponse
	 * @param expectedstatuscode
	 * @param expectederrormessage
	 * @param extenttest
	 * @return void
	 */
	public void getspecifiedDestinationFilterForLoggedInUserwithCheck(String filter_Id,String token,HashMap<String,Object>expected_response,
													int expectedstatuscode, SpogMessageCode ExpectedErrorMessage, ExtentTest test)
	{
		HashMap<String,Object> actual_response = new HashMap<>();
		int actual_size = expected_response.size();
		Response response = spogServer.getFiltersById(token, filter_Id, filterType.destination_filter.toString(), "none", "none", test);
	/*	Response response = spogDestinationInvoker.getspecifiedDestinationFilterForLoggedInUser(filter_Id,token,test);*/
		
		test.log(LogStatus.INFO, "Check the response status");
		spogServer.checkResponseStatus(response, expectedstatuscode);
		test.log(LogStatus.PASS, "The response status matched and the status code is "+expectedstatuscode);
		if(expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			//place holder for validating the response
			actual_response = response.then().extract().path("data");
			checkDestinationFilters(actual_response,expected_response,test);	
		}else {
			String code = ExpectedErrorMessage.getCodeString();
	    	String message = ExpectedErrorMessage.getStatus();
	    	if(code.contains("00A00102")){
	    		Response response1 = spogServer.getLoggedInUser(token, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
                String response_user_id = response1.then().extract().path("data.user_id");
                message = message.replace("{0}", filter_Id);
                message = message.replace("{1}", response_user_id); 

	    	}
	    	spogServer.checkErrorCode(response,code);
	    	test.log(LogStatus.INFO, "The error code matched with the expected "+code);
	    	spogServer.checkErrorMessage(response,message);
	    	test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		}
	}
	
	
	/**
	 * get destination filters for logged in user by applying filter on is_default
	 * @author Kiran.Sripada
	 * @param additionalURL can be "" or ?is_default=true or ?is_default=false 
	 * @param array list of expectedresponse
	 * @param expectedstatuscode
	 * @param expectederrormessage
	 * @param extenttest
	 * @return void
	 */
	public void getDestinationFiltersForLoggedInUserwithCheck(String additionalURL, String token,ArrayList<HashMap<String,Object>>expected_response,int expectedstatuscode, SpogMessageCode ExpectedErrorMessage, ExtentTest test) 
	{
		ArrayList<HashMap<String,Object>> actual_response = new ArrayList<>();
		HashMap<String,Object> data_response = new HashMap<>();
		boolean found = false;
		int count = 0;
		int actual_size = expected_response.size();
		
		Response response = spogServer.getFilters(token, "none", filterType.destination_filter.toString(), additionalURL, test);
	/*	Response response = spogDestinationInvoker.getDestinationFiltersForLoggedInUser(additionalURL,token);*/
		
		test.log(LogStatus.INFO, "Check the response status");
		spogServer.checkResponseStatus(response, expectedstatuscode);
		test.log(LogStatus.PASS, "The response status matched and the status code is "+expectedstatuscode);
		
		if(expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			actual_response = response.then().extract().path("data");
			
			if(actual_response.contains("null")||(expected_response.size()==0)) {
				test.log(LogStatus.PASS, "No filters available");
				assertTrue("No filters available",true);
				
			}
			Collections.sort(actual_response, new Comparator<HashMap<String,Object>>() {
				@Override
				public int compare(HashMap<String,Object> o1, HashMap<String,Object> o2) {
					// TODO Auto-generated method stub
					int create_ts  = (int) o1.get("create_ts");
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
			for(int i=0;i<actual_response.size();i++) {
				data_response = actual_response.get(i);
				if(additionalURL.contains("is_default")&&(additionalURL.contains("true"))&&(additionalURL.contains("destination_type"))) {
					boolean responseIsDefault = (boolean) data_response.get("is_default");
					if(!responseIsDefault) {
						continue;
					}else {
						checkDestinationFilters(data_response,expected_response.get(actual_size-1),test);
						count++;
						
						break;
					}
				}else if(additionalURL.contains("is_default")&&(additionalURL.contains("false"))&&(additionalURL.contains("destination_type"))) {
					boolean responseIsDefault = (boolean) data_response.get("is_default");
					if(responseIsDefault) {
						test.log(LogStatus.FAIL, "Still there are entries with is_default=true");
						assertTrue("Still there are entries with is_default=true",false);
					}else {
						checkDestinationFilters(data_response,expected_response.get(i),test);
						count++;
						continue;
					}
				}else if(!additionalURL.contains("is_default")&&(additionalURL.contains("destination_type"))) {
					String[] filtername = expected_data[0].split("=");
					
					String responseIsDefault =  data_response.get("destination_type").toString();
					if(responseIsDefault.equals(filtername[1])) {
						
						for(int j=0;j<expected_response.size();j++) {
							if((expected_response.get(j).get("destination_type").toString()).equals(filtername[1])) {
								checkDestinationFilters(data_response,expected_response.get(j),test);
								
								break;
							}else {
								continue;
							}
						}
						
					}else {
						continue;
					} 
				}else if(additionalURL.contains("is_default")&&(additionalURL.contains("destination_type"))) {
					String[] filtername = expected_data[1].split("=");
					String responseIsDefault =  data_response.get("destination_type").toString();
					if(responseIsDefault.equals(filtername[1])) {
						for(int j=0;j<expected_response.size();j++) {
							if((expected_response.get(j).get("destination_type").toString()).equals(filtername[1])) {
								checkDestinationFilters(data_response,expected_response.get(j),test);
								
								break;
							}else {
								continue;
							}
						}
					}else {
						continue;
					} 
				}else {
					checkDestinationFilters(data_response,expected_response.get(i),test);
				}
				
			}
			if(additionalURL.contains("is_default")&&(additionalURL.contains("true"))&&(!additionalURL.contains("destination_type"))) {
				if((count>1||count==0)&&expected_response.size()!=0) {
					test.log(LogStatus.FAIL, "There are more entires with is_default as true or no records found "+count);
					assertTrue("There are more entires withe is_default as true",false);
				}
			}
			if(additionalURL.contains("is_default")&&(additionalURL.contains("false"))&&(!additionalURL.contains("destination_type"))&&(expected_response.size()!=0)) {
				if(count!=actual_size-1) {
					test.log(LogStatus.FAIL, "There are more entries with is_default as false "+count);
					assertTrue("There are more entries with is_default as false",false);
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
	
	/**
	 * update the destination info by destination Id
	 * 
	 * @author Kiran.Sripada
	 * @param destination_Id
	 * @param token
	 * @param organization_id
	 * @param site_id
	 * @param datacenter_id
	 * @param destination_type
	 * @param destination_status
	 * @param destination_name
	 * @param cloudvolumeInfo
	 * @param clouddedupevolumeInfo
	 * @param expectedstatuscode
	 * @param ExpectedErrorMessage
	 * @param extenttest
	 * 
	 */
	public void updatedestinationinfobydestinationIdwithCheck(String destination_Id, 
													 String token, 
													 String organization_id, 
													 String site_id, 
													 String datacenter_id, 
													 String destination_type, 
													 String destination_status,
													 String destination_name,
													 Map<String,Object> cloudvolumeInfo,
													 Map<String,Object> clouddedupevolumeInfo,
													 String user_id,
													 int expectedstatuscode,
													 SpogMessageCode ExpectedErrorMessage,
													 CloudDirectRetentionValues Expectedretention,
													 ExtentTest test
													 ) {
		
		
		Map<String, Object> destinationInfo= new HashMap<String, Object>();
		destinationInfo.put("organization_id", organization_id);
		destinationInfo.put("site_id", site_id);
		destinationInfo.put("datacenter_id", datacenter_id);
		destinationInfo.put("destination_type", destination_type);
		destinationInfo.put("destination_status", destination_status);
		destinationInfo.put("cloud_direct_volume", cloudvolumeInfo);
		destinationInfo.put("cloud_dedupe_volume", clouddedupevolumeInfo);
		destinationInfo.put("destination_name", destination_name);
		
		Response response = spogDestinationInvoker.updatedestinationbydestination_Id(destination_Id, destinationInfo,token);
		test.log(LogStatus.INFO, "Check the response status");
		spogServer.checkResponseStatus(response, expectedstatuscode, test);
		if(expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.INFO, "Extract the data from the response");
			HashMap<String,Object> actual_response = response.then().extract().path("data");
			
			test.log(LogStatus.INFO, "Compare the destination_name");
			spogServer.assertResponseItem(actual_response.get("destination_name").toString(),destination_name,test);
			
			test.log(LogStatus.INFO, "Compare the destination_type");
			spogServer.assertResponseItem(actual_response.get("destination_type").toString(),destination_type,test);
			
			test.log(LogStatus.INFO, "Compare the destination_status");
			spogServer.assertResponseItem(actual_response.get("destination_status").toString(),destination_status,test);
			
			test.log(LogStatus.INFO, "Compare the organization id");
			spogServer.assertResponseItem(actual_response.get("organization_id").toString(),organization_id,test);
			
			if(actual_response.get("destination_type").equals("cloud_direct_volume")) {
				
				test.log(LogStatus.INFO, "Compare the site id");
				spogServer.assertResponseItem(actual_response.get("cloud_account_id").toString(),site_id,test);
				
				test.log(LogStatus.INFO, "Get the cloud direct volume details");
				HashMap<String,Object> cloud_direct_actual_response = response.then().extract().path("data.cloud_direct_volume");
				
				//Kiran - Latest API does not have primary usage,snapshot usage and total usage.
				/*test.log(LogStatus.INFO, "Compare the primary usage");
				spogServer.assertResponseItem(cloud_direct_actual_response.get("primary_usage").toString(),cloudvolumeInfo.get("primary_usage").toString(),test);

				test.log(LogStatus.INFO, "Compare the snapshot usage");
				spogServer.assertResponseItem(cloud_direct_actual_response.get("snapshot_usage").toString(),cloudvolumeInfo.get("snapshot_usage").toString(),test);

				test.log(LogStatus.INFO, "Compare the total usage");
				spogServer.assertResponseItem(cloud_direct_actual_response.get("total_usage").toString(),cloudvolumeInfo.get("total_usage").toString(),test);
				 */
				
				test.log(LogStatus.INFO, "Compare the volume_type");
				spogServer.assertResponseItem(cloud_direct_actual_response.get("volume_type").toString(),cloudvolumeInfo.get("volume_type").toString(),test);
				
				test.log(LogStatus.INFO, "Compare the retention_id");
				spogServer.assertResponseItem(cloud_direct_actual_response.get("retention_id").toString(),cloudvolumeInfo.get("retention_id").toString(),test);
				
				HashMap<String,Object> cloud_retention_actual_response = response.then().extract().path("data.cloud_direct_volume.retention");
				if(cloud_direct_actual_response.get("retention_id").equals("custom")) {
					//HashMap<String,Object> cloud_retention_actual_response = response.then().extract().path("data.cloud_direct_volume.retention");
					HashMap<String,String> cloud_retention_expected_response = (HashMap<String, String>) cloudvolumeInfo.get("retentionInfo");
					
					//Kiran - As per the API implementation, only the csr user can change the values
					/*test.log(LogStatus.INFO, "Compare the age_hours_max");
					spogServer.assertResponseItem(cloud_retention_actual_response.get("age_hours_max").toString(),cloud_retention_expected_response.get("age_hours_max").toString(),test);
					
					test.log(LogStatus.INFO, "Compare the age_four_hours_max");
					spogServer.assertResponseItem(cloud_retention_actual_response.get("age_four_hours_max").toString(),cloud_retention_expected_response.get("age_four_hours_max").toString(),test);
					
					*/
					test.log(LogStatus.INFO, "Compare the age_days_max");
					spogServer.assertResponseItem(cloud_retention_actual_response.get("age_days_max").toString(),cloud_retention_expected_response.get("age_days_max").toString(),test);
					
					test.log(LogStatus.INFO, "Compare the age_weeks_max");
					spogServer.assertResponseItem(cloud_retention_actual_response.get("age_weeks_max").toString(),cloud_retention_expected_response.get("age_weeks_max").toString(),test);
					
					test.log(LogStatus.INFO, "Compare the age_months_max");
					spogServer.assertResponseItem(cloud_retention_actual_response.get("age_months_max").toString(),cloud_retention_expected_response.get("age_months_max").toString(),test);
					
					test.log(LogStatus.INFO, "Compare the age_years_max");
					spogServer.assertResponseItem(cloud_retention_actual_response.get("age_years_max").toString(),cloud_retention_expected_response.get("age_years_max").toString(),test);
					
					
				}else {
					test.log(LogStatus.INFO, "Compare the age_hours_max");
					spogServer.assertResponseItem(cloud_retention_actual_response.get("age_hours_max").toString(),Expectedretention.gethours().toString(),test);
					
					test.log(LogStatus.INFO, "Compare the age_four_hours_max");
					spogServer.assertResponseItem(cloud_retention_actual_response.get("age_four_hours_max").toString(),Expectedretention.getmax4hours().toString(),test);
					
					test.log(LogStatus.INFO, "Compare the age_days_max");
					spogServer.assertResponseItem(cloud_retention_actual_response.get("age_days_max").toString(),Expectedretention.getdays().toString(),test);
					
					test.log(LogStatus.INFO, "Compare the age_weeks_max");
					spogServer.assertResponseItem(cloud_retention_actual_response.get("age_weeks_max").toString(),Expectedretention.getweeks().toString(),test);
					
					test.log(LogStatus.INFO, "Compare the age_months_max");
					spogServer.assertResponseItem(cloud_retention_actual_response.get("age_months_max").toString(),Expectedretention.getmonths().toString(),test);
					
					test.log(LogStatus.INFO, "Compare the age_years_max");
					spogServer.assertResponseItem(cloud_retention_actual_response.get("age_years_max").toString(),Expectedretention.getyears().toString(),test);
				}
			}
			if(actual_response.get("destination_type").equals("cloud_hybrid_store")) {
				test.log(LogStatus.INFO, "Get the cloud dedupe volume details");
				HashMap<String,Object> cloud_direct_actual_response = response.then().extract().path("data.cloud_hybrid_store");
				
				/*test.log(LogStatus.INFO, "Compare the data_store_folder");
				spogServer.assertResponseItem(cloud_direct_actual_response.get("data_store_folder").toString(),clouddedupevolumeInfo.get("data_store_folder").toString(),test);
				
				test.log(LogStatus.INFO, "Compare the data_destination");
				spogServer.assertResponseItem(cloud_direct_actual_response.get("data_destination").toString(),clouddedupevolumeInfo.get("data_destination").toString(),test);
				
				test.log(LogStatus.INFO, "Compare the index_destination");
				spogServer.assertResponseItem(cloud_direct_actual_response.get("index_destination").toString(),clouddedupevolumeInfo.get("index_destination").toString(),test);
				
				test.log(LogStatus.INFO, "Compare the hash_destination");
				spogServer.assertResponseItem(cloud_direct_actual_response.get("hash_destination").toString(),clouddedupevolumeInfo.get("hash_destination").toString(),test);
				
				test.log(LogStatus.INFO, "Compare the concurrent_active_node");
				spogServer.assertResponseItem(cloud_direct_actual_response.get("concurrent_active_node").toString(),clouddedupevolumeInfo.get("concurrent_active_node").toString(),test);
				
				test.log(LogStatus.INFO, "Compare the is_deduplicated");
				spogServer.assertResponseItem(cloud_direct_actual_response.get("is_deduplicated").toString(),clouddedupevolumeInfo.get("is_deduplicated").toString(),test);
				
				test.log(LogStatus.INFO, "Compare the block_size");
				spogServer.assertResponseItem(cloud_direct_actual_response.get("block_size").toString(),clouddedupevolumeInfo.get("block_size").toString(),test);
				
				test.log(LogStatus.INFO, "Compare the hash_memory");
				spogServer.assertResponseItem(cloud_direct_actual_response.get("hash_memory").toString(),clouddedupevolumeInfo.get("hash_memory").toString(),test);
				
				test.log(LogStatus.INFO, "Compare the is_compressed");
				spogServer.assertResponseItem(cloud_direct_actual_response.get("is_compressed").toString(),clouddedupevolumeInfo.get("is_compressed").toString(),test);
				
				test.log(LogStatus.INFO, "Compare the occupied_space");
				spogServer.assertResponseItem(cloud_direct_actual_response.get("occupied_space").toString(),clouddedupevolumeInfo.get("occupied_space").toString(),test);
				
				test.log(LogStatus.INFO, "Compare the store_data");
				//spogServer.assertResponseItem(cloud_direct_actual_response.get("store_data").toString(),clouddedupevolumeInfo.get("store_data").toString(),test);
				
				test.log(LogStatus.INFO, "Compare the deduplication_rate");
				spogServer.assertResponseItem(cloud_direct_actual_response.get("deduplication_rate").toString(),clouddedupevolumeInfo.get("deduplication_rate").toString(),test);
				
				test.log(LogStatus.INFO, "Compare the compression_rate");
				spogServer.assertResponseItem(cloud_direct_actual_response.get("compression_rate").toString(),clouddedupevolumeInfo.get("compression_rate").toString(),test);*/
			}
		}else {
			String code = ExpectedErrorMessage.getCodeString();
	    	String message = ExpectedErrorMessage.getStatus();
	    	/*if(code.contains("0C00001")){
	    		message = message.replace("{0}", destination_Id);
	    	}*/
	    	spogServer.checkErrorCode(response,code);
	    	test.log(LogStatus.INFO, "The error code matched with the expected "+code);
	    	spogServer.checkErrorMessage(response,message);
	    	test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		}
		
		
	}
	
	/**
	 * update the destination info by destination Id
	 * 
	 * @author Kiran.Sripada
	 * @param destination_Id
	 * @param token
	 * @param organization_id
	 * @param site_id
	 * @param datacenter_id
	 * @param destination_type
	 * @param destination_status
	 * @param destination_name
	 * @param cloudvolumeInfo
	 * @param clouddedupevolumeInfo
	 * @return response
	 * 
	 */
	public Response updatedestinationinfobydestinationId(String destination_Id, 
													 String token, 
													 String organization_id, 
													 String site_id, 
													 String datacenter_id, 
													 String destination_type, 
													 String destination_status,
													 String destination_name,
													 Map<String,Object> cloudvolumeInfo,
													 Map<String,Object> clouddedupevolumeInfo
													 ) {
		
		
		Map<String, Object> destinationInfo= new HashMap<String, Object>();
		destinationInfo.put("organization_id", organization_id);
		destinationInfo.put("site_id", site_id);
		destinationInfo.put("datacenter_id", datacenter_id);
		destinationInfo.put("destination_type", destination_type);
		destinationInfo.put("destination_status", destination_status);
		destinationInfo.put("cloud_direct_volume", cloudvolumeInfo);
		destinationInfo.put("cloud_dedupe_volume", clouddedupevolumeInfo);
		destinationInfo.put("destination_name", destination_name);
		
		Response response = spogDestinationInvoker.updatedestinationbydestination_Id(destination_Id, destinationInfo,token);
		return response;
		
		
		
		
	}
	
	
	/**
	   * Invoke the Rest API to get the destination based on types
	   * 
	   * @author Kiran.Sripada
	   * @param additionalURL 
	   * @param extenttest object
	   * @return response
	   */
	  public Response getDestinationsbyTypes(String additionalURL, String token, ExtentTest test) {

	    test.log(LogStatus.INFO, "get the destination by types ");
	    Response response = spogDestinationInvoker.getDestinationsbyTypes(additionalURL, token, test);
	    return response;

	  }
	
	  /**
	   * Validation for destination types
	   * 
	   * @author Kiran.Sripada
	   * @param response from getDestinationbyTypes
	   * @param Expecteddestinationtypes 
	   * @param filterStr Default is ""
	   * @param expectedStatusCode
	   * @param Expectederrormessage
	   * @param extenttest object
	   * @return response
	   */
	  public void checkdestinationbyTypes(Response response, ArrayList<HashMap> Expecteddestinationtypes,
		      String filterStr, int expectedStatusCode, SpogMessageCode expectedErroMessage, ExtentTest test) {
		  ArrayList<HashMap> actualdestination_types = new ArrayList<>();
		    int size_actual_records = 0;
		    int size_expected_records = 0;
		    Boolean found = false;
		    spogServer.checkResponseStatus(response, expectedStatusCode);
		    if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) 
		    {
		    	actualdestination_types = response.then().extract().path("data");
		      size_actual_records = actualdestination_types.size();
		      size_expected_records = Expecteddestinationtypes.size();
		      if ((filterStr != null || filterStr == "") && !filterStr.contains("csr")
		          && size_actual_records == Expecteddestinationtypes.size()) 
		      {
		        for (int i = 0; i < size_actual_records; i++) {
		        	found = false;
		        
		          for (int j = 0; j < size_expected_records; j++) {
		              if (actualdestination_types.get(i).get("id")
		                  .equals(Expecteddestinationtypes.get(j).get("id"))
		                  && (actualdestination_types.get(i).get("count")
		                      .equals(Expecteddestinationtypes.get(j).get("count")))
		                  && (actualdestination_types.get(i).get("name")
			                      .equals( Expecteddestinationtypes.get(j).get("name")))) {
		                found = true;
		                break;
		              } else {
		                continue;
		              }
		            }
		            if (found) {
		              test.log(LogStatus.PASS,
		                  "The source type is " + actualdestination_types.get(i).get("id")
		                      + " and amount is " + actualdestination_types.get(i).get("count"));
		            } else {

		              test.log(LogStatus.FAIL,
		                  "The actual values are: destination type is "
		                      + actualdestination_types.get(i).get("id") + " and amount is "
		                      + actualdestination_types.get(i).get("count"));
		              assertTrue("The actual values are: destination type is "
		                      + actualdestination_types.get(i).get("id") + " and amount is "
		                      + actualdestination_types.get(i).get("count"),false);
		            }
		          } 
		      } else if (filterStr.contains("csr")) {
		    	  
		    	  if (size_actual_records == size_expected_records) {
					for (int i = 0; i < size_actual_records; i++) {
						
						assertTrue(!actualdestination_types.get(i).get("id").equals(null), "id: "+actualdestination_types.get(i).get("id"));
						assertTrue(!actualdestination_types.get(i).get("count").equals(null), "count: "+actualdestination_types.get(i).get("count"));
						assertTrue(!actualdestination_types.get(i).get("name").equals(null), "name: "+actualdestination_types.get(i).get("name"));
					}
				}
				
			}
		    } else {
		    	String code = expectedErroMessage.getCodeString();
		    	String message = expectedErroMessage.getStatus();
		    	spogServer.checkErrorCode(response,code);
		    	test.log(LogStatus.INFO, "The error code matched with the expected "+code);
		    	spogServer.checkErrorMessage(response,message);
		    	test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);

		    }
	  }
	
	/**
	 * delete the destination info by destination Id
	 * 
	 * @author Kiran.Sripada
	 * @param destination_Id
	 * @param token
	 * @param expectedstatuscode
	 * @param ExpectedErrorMessage
	 * @param extenttest
	 * 
	 */
	public void deletedestinationbydestination_Id(String destination_Id, String token, int expectedstatuscode, SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		Response response = spogDestinationInvoker.deletedestinationbydestination_Id(destination_Id,token);
		test.log(LogStatus.INFO, "Check the response status");
		spogServer.checkResponseStatus(response, expectedstatuscode, test);
		if(expectedstatuscode==SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.PASS, "Succesfully deleted the destination");
		}else {
			String code = ExpectedErrorMessage.getCodeString();
	    	String message = ExpectedErrorMessage.getStatus();
	    	if(code.contains("0C00001")){
	    		message = message.replace("{0}", destination_Id);
	    	}
	    	spogServer.checkErrorCode(response,code);
	    	test.log(LogStatus.INFO, "The error code matched with the expected "+code);
	    	spogServer.checkErrorMessage(response,message);
	    	test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
	    }
	}
	
	public void deletedestinationbydestination_Id(String destination_Id, String token,  ExtentTest test) {
		Response response = spogDestinationInvoker.deletedestinationbydestination_Id(destination_Id,token);
		test.log(LogStatus.INFO, "Check the response status");
	}

	
	/**
	 * @author Bharadwaj.Ghadiam
	 * HashMap For CloudDedupe Volume
	 * @param data_store_folder
	 * @param data_destination
	 * @param index_destination
	 * @param hash_destination
	 * @param concurrent_active_node
	 * @param is_deduplicated
	 * @param block_size
	 * @param hash_memory
	 * @param is_compressed
	 * @param encryption_password
	 * @param occupied_space
	 * @param stored_data
	 * @param deduplication_rate
	 * @param compression_rate
	 * @return HashMap(cloud_dedupe_storage)
	 */
	public HashMap<String, Object> composeCloudDedupeInfo(String data_store_folder ,
			String data_destination, String index_destination,
			String hash_destination, int concurrent_active_node,
		    boolean is_deduplicated, int block_size, String hash_memory,
		    boolean is_compressed, String encryption_password,
			String occupied_space, String stored_data,
			Double deduplication_rate, Double compression_rate) {
		// TODO Auto-generated method stub
		HashMap<String,Object> cloud_dedupe_volume=new HashMap<String,Object>();
		cloud_dedupe_volume.put("data_store_folder",data_store_folder);
		cloud_dedupe_volume.put("data_destination",data_destination);
		cloud_dedupe_volume.put("index_destination",index_destination);
		cloud_dedupe_volume.put("hash_destination",hash_destination);
		cloud_dedupe_volume.put("concurrent_active_node",concurrent_active_node);
		cloud_dedupe_volume.put("is_deduplicated",is_deduplicated);
		cloud_dedupe_volume.put("block_size",block_size);
		cloud_dedupe_volume.put("hash_memory",hash_memory);
		cloud_dedupe_volume.put("is_compressed",is_compressed);
		cloud_dedupe_volume.put("encryption_password",encryption_password);
		cloud_dedupe_volume.put("occupied_space",occupied_space);
		cloud_dedupe_volume.put("stored_data",stored_data);
		cloud_dedupe_volume.put("deduplication_rate",deduplication_rate);
		cloud_dedupe_volume.put("compression_rate",compression_rate);	
		return cloud_dedupe_volume;
	}
	
	/**
	 * @author Ramya.Nagepalli
	 * HashMap For CloudHybrid Volume
	 * @param concurrent_active_node
	 * @param is_deduplicated
	 * @param block_size
	 * @return HashMap(cloud_hybrid_store)
	 */
	public HashMap<String, Object> composeCloudHybridInfo(int concurrent_active_node,
		    boolean is_deduplicated, int block_size,
		    boolean is_compressed) {
		// TODO Auto-generated method stub
		HashMap<String,Object> cloud_hybrid_store=new HashMap<String,Object>();	
		cloud_hybrid_store.put("concurrent_active_node",concurrent_active_node);
		cloud_hybrid_store.put("is_deduplicated",is_deduplicated);
		cloud_hybrid_store.put("block_size",block_size);
		cloud_hybrid_store.put("is_compressed",is_compressed);	
		return cloud_hybrid_store;
	}
	
	/**
	 * @author Bharadwaj.Ghadiam
	 * HashMap For CloudDedupe Volume all parameters are string
	 * @param data_store_folder
	 * @param data_destination
	 * @param index_destination
	 * @param hash_destination
	 * @param concurrent_active_node
	 * @param is_deduplicated
	 * @param block_size
	 * @param hash_memory
	 * @param is_compressed
	 * @param encryption_password
	 * @param occupied_space
	 * @param stored_data
	 * @param deduplication_rate
	 * @param compression_rate
	 * @return HashMap(cloud_dedupe_storage)
	 */
	public HashMap<String, Object> composeCloudDedupeInfo(String data_store_folder ,
			String data_destination, String index_destination,
			String hash_destination, String concurrent_active_node,
		    String is_deduplicated, String block_size, String hash_memory,
		    String is_compressed, String encryption_password,
			String occupied_space, String stored_data,
			String deduplication_rate, String compression_rate) {
		// TODO Auto-generated method stub
		HashMap<String,Object> cloud_dedupe_volume=new HashMap<String,Object>();
		cloud_dedupe_volume.put("data_store_folder",data_store_folder);
		cloud_dedupe_volume.put("data_destination",data_destination);
		cloud_dedupe_volume.put("index_destination",index_destination);
		cloud_dedupe_volume.put("hash_destination",hash_destination);
		cloud_dedupe_volume.put("concurrent_active_node",concurrent_active_node);
		cloud_dedupe_volume.put("is_deduplicated",is_deduplicated);
		cloud_dedupe_volume.put("block_size",block_size);
		cloud_dedupe_volume.put("hash_memory",hash_memory);
		cloud_dedupe_volume.put("is_compressed",is_compressed);
		cloud_dedupe_volume.put("encryption_password",encryption_password);
		cloud_dedupe_volume.put("occupied_space",occupied_space);
		cloud_dedupe_volume.put("stored_data",stored_data);
		cloud_dedupe_volume.put("deduplication_rate",deduplication_rate);
		cloud_dedupe_volume.put("compression_rate",compression_rate);	
		return cloud_dedupe_volume;
	}
		
		/**
		 * @author Bharadwaj.Ghadiam
		 * Compose the Retention Info(JSONObject)
		 * @param age_hours_max
		 * @param age_four_hours_max
		 * @param age_days_max
		 * @param age_weeks_max
		 * @param age_months_max
		 * @param age_years_max
		 * @return retention details
		 */
		public HashMap<String,String> composeRetention(String age_hours_max, String age_four_hours_max,String age_days_max ,
			String age_weeks_max, String age_months_max , String age_years_max ) {
			// TODO Auto-generated method stub

			HashMap<String,String> retention= new HashMap<String,String>();
			retention.put("age_hours_max" ,age_hours_max);   
			retention.put("age_four_hours_max" ,age_four_hours_max); 
			retention.put("age_days_max",age_days_max); 
			retention.put("age_weeks_max" , age_weeks_max); 
			retention.put("age_months_max",age_months_max); 
			retention.put("age_years_max", age_years_max );
			return retention;
		}
		/**
		 * @author Bharadwaj.Ghadiam
		 * Compose the cloudDirectInfo HashMap(JSONObject) all parameters are string
		 * @param cloud_account_id
		 * @param cloud_direct_volume_name
		 * @param datacenter_id
		 * @param retention_id
		 * @param Retention
		 * @return cloud_direct_volume information
		 */

		public  HashMap<String, Object> composeCloudDirectInfo(
				String retention_id,String retention_name,
				String primary_usage,String snapshot_usage,String total_usage,
				String volume_type,String hostname,HashMap<String, String> Retention) {
			// TODO Auto-generated method stub
			HashMap<String,Object> cloud_direct_volume =new HashMap<String,Object>();
			/*cloud_direct_volume.put("cloud_account_id",cloud_account_id);
			cloud_direct_volume.put("cloud_direct_volume_name",cloud_direct_volume_name);
			cloud_direct_volume.put("datacenter_id",datacenter_id);
			*/cloud_direct_volume.put("retention_id",retention_id);
			cloud_direct_volume.put("primary_usage",primary_usage);
			cloud_direct_volume.put("snapshot_usage",snapshot_usage);
			cloud_direct_volume.put("total_usage",total_usage);
			cloud_direct_volume.put("volume_type",volume_type);
			cloud_direct_volume.put("hostname",hostname);
			cloud_direct_volume.put("retention",Retention);
			return cloud_direct_volume;

		}
		
		/**
		 * @author Bharadwaj.Ghadiam
		 * Compose the cloudDirectInfo HashMap(JSONObject)
		 * @param cloud_account_id
		 * @param cloud_direct_volume_name
		 * @param datacenter_id
		 * @param retention_id
		 * @param Retention
		 * @return cloud_direct_volume information
		 */

		public  HashMap<String, Object> composeCloudDirectInfo(
			     String cloud_account_id,String cloud_direct_volume_name,String retention_id,String retention_name,
				Double primary_usage,Double snapshot_usage,Double total_usage,
				String volume_type,String hostname,HashMap<String, String> Retention) {
			// TODO Auto-generated method stub
			HashMap<String,Object> cloud_direct_volume =new HashMap<String,Object>();
			cloud_direct_volume.put("cloud_account_id",cloud_account_id);
			cloud_direct_volume.put("retention_id",retention_id);
			cloud_direct_volume.put("retention_name",retention_name);
			cloud_direct_volume.put("primary_usage",primary_usage);
			cloud_direct_volume.put("snapshot_usage",snapshot_usage);
			cloud_direct_volume.put("total_usage",total_usage);
			cloud_direct_volume.put("volume_type",volume_type);
			cloud_direct_volume.put("hostname",hostname);
			cloud_direct_volume.put("retention",Retention);
			return cloud_direct_volume;

		}
		
		/**
		 * @author Bharadwaj.Ghadiam
		 * Call the Rest Web Service to create a Destination
		 * @param Token(user JWT)
		 * @param organization_id
		 * @param site_id
		 * @param destinationType
		 * @param destination_name
		 * @param cloud_direct_volume
		 * @param test
		 * @return response
		 */
		public  Response  createDestination(String Token,String organization_id,String site_id,String datacenter_id,
				String destinationType,String destination_status,String destination_name,HashMap<String, Object> cloud_volume,ExtentTest test) {
			// TODO Auto-generated method stub
			test.log(LogStatus.INFO,"Creatig a Destination Filter");
			HashMap<String,Object> DestinationInfo=jp.ComposeDestinationInfo(organization_id,site_id,datacenter_id,destinationType,destination_status,destination_name,cloud_volume,test);
			Response response=spogDestinationInvoker.createDestination(Token,DestinationInfo,test);
			return response;
		}
		
		//This is a generic method used to post destination
		  public  String createDestination(String adminToken,String organization_id,String cloud_account_id,
		  String DestinationType,String destination_status,String site_id,String destination_name,String cloud_direct_volume_name,String retention_id,String retention_name,String age_hours_max ,String age_four_hours_max,
		  String age_days_max , String 	age_weeks_max, String age_months_max,String age_years_max , Double primary_usage,Double  snapshot_usage,Double total_usage,String volume_type,String hostname,String datacenter_id,String dedupe_savings){
	        //creating  a destination 
			test.log(LogStatus.INFO,"creating a destination of type :"+DestinationType);
			retention= composeRetention(age_hours_max,age_four_hours_max,age_days_max ,age_weeks_max,age_months_max,age_years_max);
			cloud_direct_volume=composeCloudDirectInfo(UUID.randomUUID().toString(),cloud_direct_volume_name,retention_id,retention_name,primary_usage,snapshot_usage,total_usage,volume_type,hostname,retention);	
			test.log(LogStatus.INFO,"Creating a destination of type cloud_direct_volume");
			Response response=createDestination(UUID.randomUUID().toString(),adminToken,cloud_account_id,organization_id,site_id,datacenter_id, dedupe_savings,DestinationType,destination_status,destination_name,cloud_direct_volume,test);
			String destination_id=response.then().extract().path("data.destination_id");
			return destination_id;
		  }
		public  Response  createDestination(String destination_id,String Token,String cloud_account_id,String organization_id,String site_id,String datacenter_id,
				String dedupe_savings,String destinationType,String destination_status,String destination_name,HashMap<String, Object> cloud_volume,ExtentTest test) {
			// TODO Auto-generated method stub
			test.log(LogStatus.INFO,"Creatig a Destination Filter");
			HashMap<String,Object> DestinationInfo=jp.ComposeDestinationInfo(destination_id, cloud_account_id,organization_id,site_id,datacenter_id,dedupe_savings,destinationType,destination_status,destination_name,cloud_volume,test);
			Response response=spogDestinationInvoker.createDestination(Token,DestinationInfo,test);
			spogServer.checkResponseStatus(response,SpogConstants.SUCCESS_POST);
			return response;
		}
		/**
		 * Call the API Get Destinations By Id(sprint 7)
		 * @author Bharadwaj.ghadiam
		 * @param token
		 * @param destination_id
		 * @param test
		 * @return
		 */
		public Response getDestinationById(String token, String destination_id,
				ExtentTest test) {
			// TODO Auto-generated method stub
			spogServer.setUUID(destination_id);
			test.log(LogStatus.INFO,"call the API getDestnationById");
			Response response=spogDestinationInvoker.getDestinationById(token, destination_id, test);	
			return response;
		}

		public  HashMap<String, Object> composeCloudDirectInfo(
				   String retention_id,
					Double primary_usage,Double snapshot_usage,Double total_usage,
					String volume_type,String hostname,HashMap<String, String> Retention) {
				// TODO Auto-generated method stub
				HashMap<String,Object> cloud_direct_volume =new HashMap<String,Object>();
				cloud_direct_volume.put("retention_id",retention_id);
				cloud_direct_volume.put("primary_usage",primary_usage);
				cloud_direct_volume.put("snapshot_usage",snapshot_usage);
				cloud_direct_volume.put("total_usage",total_usage);
				cloud_direct_volume.put("volume_type",volume_type);
				cloud_direct_volume.put("hostname",hostname);
				cloud_direct_volume.put("retention",Retention);
				return cloud_direct_volume;
			}
		
		public Response getDestinationUsageCount(String admin_Token,
				String destination_id,String additionalURl, ExtentTest test) {
			// TODO Auto-generated method stub
		    spogServer.setUUID(destination_id);
			test.log(LogStatus.INFO,"The rest api to find the usage_count");		
			Response response=spogDestinationInvoker.getDestiantionUsageCount(admin_Token,destination_id,"",test);
			//spogServer.checkResponseStatus(response,SpogConstants.SUCCESS_GET_PUT_DELETE);
			return response;

		}
		/**
		 * Validation of the response for get destinations By id 
		 * @param destination_id
		 * @param successGetPutDelete
		 * @param destinationType
		 * @param destination_name
		 * @param organization_id
		 * @param organization_name
		 * @param cloud_direct_volume
		 * @param successGetPutDel
		 * @param test
		 */
		@SuppressWarnings("unchecked")
		public void checkgetDestinationById(Response response,String destination_id,
					int expectedStatusCode, String destinationType,
					String destination_name, String organization_id,
					String organization_name,String site_id ,HashMap<String, Object> cloud_volume,
					SpogMessageCode Info, HashMap<String, ArrayList<HashMap<String, Object>>> last_job,ExtentTest test) {

				//String expectedErrorMessage and expectedErrorCode for error codes and error Messages validation 
				String expectedErrorMessage = "", expectedErrorCode = "";


				test.log(LogStatus.INFO,"Check the response code of the result");
				spogServer.checkResponseStatus(response, expectedStatusCode);
				
				//Validation of the 200(response)
				if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
					if(response.then().body("data.destination_id", equalTo(destination_id))!=null&&response.then().body("data.destination_type", equalTo(destinationType))!=null&&
							response.then().body("data.organization_id", equalTo(organization_id))!=null/*&&response.then().body("data.organization_name", equalTo(organization_name))!=null*/&&
							response.then().body("data.destination_name", equalTo(destination_name))!=null/*&&response.then().body("data.cloud_account_id", equalTo(site_id))!=null*/){
						test.log(LogStatus.PASS, "The organization id has matched:"+response.then().extract().path("data.organization_id")+"The destination id has matched:"+response.then().extract().path("data.destination_id"));	
						assertTrue("The validation of the response is sucessfull",true);

						//validation of the response for the cloud_direct_volume
						if(destinationType.equals("cloud_direct_volume")) {
							HashMap<String,Object> cloud_data=response.then().extract().path("data.cloud_direct_volume");

							//validation of the information inside the cloud_direct_volume
							
							/*test.log(LogStatus.INFO,"validating the response for primary snapshot ,total usage");
							assertNotNull(cloud_data.get("primary_usage"));
							assertNotNull(cloud_data.get("total_usage"));
							assertNotNull(cloud_data.get("snapshot_usage"));*/
	
							spogServer.assertResponseItem(cloud_data.get("volume_type").toString(),cloud_volume.get("volume_type").toString(),test);
//							spogServer.assertResponseItem(cloud_data.get("hostname").toString(),cloud_volume.get("hostname").toString(),test);
							spogServer.assertResponseItem(cloud_data.get("retention_id").toString(),cloud_volume.get("retention_id").toString(),test);		
							//validation for the retention details 
							//HashMap's for the actual and Expected retention values 

							HashMap<String,Object> retention,Retention= new HashMap<String,Object>();
							retention=(HashMap<String, Object>) cloud_data.get("retention");
							if(!(cloud_data.get("retention_id").equals("custom"))){
							Retention=getRetention();
							Iterator<Entry<String, Object>> entries = Retention.entrySet().iterator();   						
							while(entries.hasNext()){
								 Entry<String,Object> entry= entries.next();
								 System.out.println("The values of the entry is :"+entry.getKey());
								 if(retention.get("retention_id").equals(entry.getKey())){
									HashMap<String,String> Retention1 =new HashMap<String,String>();
									Retention1=(HashMap<String, String>) entry.getValue();
									spogServer.assertResponseItem(retention.get("age_hours_max"),Retention1.get("age_hours_max"),test);
									spogServer.assertResponseItem(retention.get("age_four_hours_max"),Retention1.get("age_four_hours_max"),test);
									spogServer.assertResponseItem(retention.get("age_days_max"),Retention1.get("age_days_max"),test);
									spogServer.assertResponseItem(retention.get("age_weeks_max "),Retention1.get("age_weeks_max "),test);
									spogServer.assertResponseItem(retention.get("age_months_max"),Retention1.get("age_months_max"),test);
									spogServer.assertResponseItem(retention.get("age_years_max"),Retention1.get("age_years_max"),test);
									break;
								 }
							}
							}
							else {
							Retention=(HashMap<String, Object>) cloud_volume.get("retention");
							spogServer.assertResponseItem(retention.get("age_hours_max"),Retention.get("age_hours_max"),test);
							spogServer.assertResponseItem(retention.get("age_four_hours_max"),Retention.get("age_four_hours_max"),test);
							spogServer.assertResponseItem(retention.get("age_days_max"),Retention.get("age_days_max"),test);
							spogServer.assertResponseItem(retention.get("age_weeks_max "),Retention.get("age_weeks_max "),test);
							spogServer.assertResponseItem(retention.get("age_months_max"),Retention.get("age_months_max"),test);
							spogServer.assertResponseItem(retention.get("age_years_max"),Retention.get("age_years_max"),test);
							}
						}

						//Related to cloud_dedupe data Store 
						else if(destinationType.equals("cloud_hybrid_store")){
							
							HashMap<String,Object> cloud_hybrid=response.then().extract().path("data.cloud_hybrid_store");
							
							if (cloud_hybrid != null) {
								spogServer.assertResponseItem(cloud_hybrid.get("data_store_folder"),cloud_volume.get("data_store_folder"),test);
//								spogServer.assertResponseItem(cloud_hybrid.get("data_destination"),cloud_volume.get("data_destination"),test);
//								spogServer.assertResponseItem(cloud_hybrid.get("index_destination"),cloud_volume.get("index_destination"),test);
//								spogServer.assertResponseItem(cloud_hybrid.get("hash_destination"),cloud_volume.get("hash_destination"),test);
								spogServer.assertResponseItem(cloud_hybrid.get("concurrent_active_node"),cloud_volume.get("concurrent_active_node"),test);
							    spogServer.assertResponseItem(cloud_hybrid.get("is_deduplicated"),cloud_volume.get("is_deduplicated"),test);
								spogServer.assertResponseItem(cloud_hybrid.get("block_size"),cloud_volume.get("block_size"),test);
//								spogServer.assertResponseItem(cloud_hybrid.get("hash_memory"),cloud_volume.get("hash_memory"),test);
								if (!cloud_volume.get("is_compressed").toString().equals("false")) {
									spogServer.assertResponseItem(cloud_hybrid.get("is_compressed").toString(),cloud_volume.get("is_compressed").toString(),test);
								}
								
//								spogServer.assertResponseItem(cloud_hybrid.get("encryption_password"),cloud_volume.get("encryption_password"),test);
//								spogServer.assertResponseItem(cloud_hybrid.get("occupied_space"),cloud_volume.get("occupied_space"),test);
							//	spogServer.assertResponseItem(cloud_hybrid.get("stored_data"),cloud_volume.get("stored_data"),test);
//								spogServer.assertResponseItem(cloud_hybrid.get("deduplication_rate").toString(),cloud_volume.get("deduplication_rate").toString(),test);
//								spogServer.assertResponseItem(cloud_hybrid.get("compression_rate").toString(),cloud_volume.get("compression_rate").toString(),test);
							}
//							                 
						}

						//Related to the validation of the shared folder 
						else{
							test.log(LogStatus.INFO,"This information is related to the shared folder");
						}					
						//Validating the last_job_status
				    	ArrayList<HashMap<String, Object>> actual_last_job=response.then().extract().path("data.last_job");
						HashMap<String,Object> actual_last_job_data=actual_last_job.get(0);
						HashMap<String,Object>expected_last_job_data=last_job.get("last_job").get(0);
						spogServer.assertResponseItem(actual_last_job_data.get("type"),expected_last_job_data.get("job_type"),test);
						spogServer.assertResponseItem(actual_last_job_data.get("start_time_ts"),expected_last_job_data.get("start_time_ts"),test);			
						spogServer.assertResponseItem(actual_last_job_data.get("status"),expected_last_job_data.get("job_status"),test);
						spogServer.assertResponseItem(actual_last_job_data.get("end_time_ts"),expected_last_job_data.get("end_time_ts"),test);
						spogServer.assertResponseItem(actual_last_job_data.get("percent_complete").toString(),expected_last_job_data.get("percent_complete").toString(),test);
						
						
						

					}else{
						test.log(LogStatus.FAIL, "The destination id has not  matched:"+response.then().extract().path("data.destination_id")+"The organization id has not matched:"+response.then().extract().path("data.organization_id"));	 
						assertTrue("The validation of the response is sucessfull",false);
					}
				} 
				else {
					if (Info.getStatus() != "0010000") {
						expectedErrorMessage = Info.getStatus();
						if (expectedErrorMessage.contains("{0}")) {
							expectedErrorMessage = expectedErrorMessage.replace("{0}", spogServer.getUUId());
							System.out.println(expectedErrorMessage);
						}
						expectedErrorCode = Info.getCodeString();
					}
					spogServer.checkErrorMessage(response, expectedErrorMessage);
					test.log(LogStatus.PASS, "The value of the message is " + expectedErrorMessage);
					spogServer.checkErrorCode(response, expectedErrorCode);
					test.log(LogStatus.PASS, "The value of the code  generated  is " + expectedErrorCode);
					test.log(LogStatus.INFO,
							"The value of the  response generated actually is :" + response.getStatusCode());
				}	
			}
	
        /**
         * @author Bharadwaj.Ghadaim 
         * This method is used to store the retention values 
         * @param retention
         */
		public void setRetention(HashMap<String, Object> retention) {
			// TODO Auto-generated method stub
	       this. Retention=retention;
		}
                  
		/**
		 * @author Bharadwaj.Ghadiam
		 * @return This method is used to return the cloud Direct retention values 
		 */
		public HashMap<String, Object> getRetention(){
                	   return Retention;
        }
		
		/**
		 * Call the API Get Destinations(sprint 7)
		 * @author Bharadwaj.ghadiam
		 * @param token(user JWT)
		 * @param additional URL
		 * @param test
		 * @return response
		 */
		public Response getDestinations(String token, String additional_url,
				ExtentTest test) {
			// TODO Auto-generated method
			test.log(LogStatus.INFO,"call the API getDestnations");
			Response response=spogDestinationInvoker.getDestinations(token,additional_url, test);	
			return response;
		}
		
		
		
		/**
		 * login as csr admin to recycle cloud volumes by org id and delete this org
		 * @author shan, jing
		 * @param orgID
		 * @param test
		 * @return response
		 */
		public void recycleCloudVolumesAndDelOrg(String orgID,ExtentTest test) {
			// TODO Auto-generated method
			test.log(LogStatus.INFO,"call the API to delete all cloud volumes by org name");
			spogServer.userLogin(this.csrAdmin, this.csrPwd);
			String user_token=spogServer.getJWTToken();
			spogDestinationInvoker.setToken(user_token);
			String des_id,org_id=null;
			Response response=spogDestinationInvoker.getDestinations(user_token,"destination_type=cloud_direct_volume&organization_id="+orgID, test);
			int  total_page=response.then().extract().path("pagination.total_page");
			int  total_size=response.then().extract().path("pagination.total_size");
			for(int page=1;page<=total_page;page++){
				if(page>1){
					response=spogDestinationInvoker.getDestinations(user_token,"destination_type=cloud_direct_volume&page="+page+"&page_size=100", test);
				}
				for(int i=0;i<total_size;i++){
					org_id=response.then().extract().path("data["+i+"].organization_id");
				  if(org_id!=null){
					  if (org_id.equalsIgnoreCase(org_id)){
						  des_id=response.then().extract().path("data["+i+"].destination_id");
						  RecycleDirectVolume(des_id, test);
					  }
				  }
			    }
			}
			if(org_id!=null){
				spogServer.DeleteOrganization(org_id);
			}
		}
		
		/**
		 * login as csr admin to recycle all useless cloud volumes of org and delete this org
		 * @author shan, jing
		 * @param test
		 * @return response
		 */
		public void recycleAllUselessCloudVolumes(ExtentTest test) {
			// TODO Auto-generated method
			test.log(LogStatus.INFO,"call the API to delete all useless cloud volumes");
			spogServer.userLogin(this.csrAdmin, this.csrPwd);
			String user_token=spogServer.getJWTToken();
			spogDestinationInvoker.setToken(user_token);
			org4SPOGServer.setToken(user_token);
			String current_org_name,previous_org_name="",des_id,org_id=null;
			boolean detroyOrgFlag=false;
			
			//Response response=spogDestinationInvoker.getDestinations(user_token,"organization_name=.*spog_qa.*&destination_type=cloud_direct_volume&page=1&page_size=3000", test);	
			Response response=spogDestinationInvoker.getDestinations(user_token,"destination_type=cloud_direct_volume&page_size=100", test);
			int  total_page=response.then().extract().path("pagination.total_page");
			int  total_size=response.then().extract().path("pagination.page_size");
			String prevous_del_orgid=null, next_del_orgid=null;
			String prevous_del_volid=null, next_del_volid=null;
			for(int page=1;page<=total_page;page++){
				if(page>1){
					response=spogDestinationInvoker.getDestinations(user_token,"destination_type=cloud_direct_volume&page="+page+"&page_size=100", test);
					total_size=response.then().extract().path("pagination.total_size");
				}
				for(int i=0;i<total_size;i++){
				  current_org_name=response.then().extract().path("data["+i+"].organization_name");
				  if(current_org_name!=null){
					  if (current_org_name.toLowerCase().indexOf("spogqa")!=-1|| current_org_name.toLowerCase().indexOf("spog_qa")!=-1|| current_org_name.toLowerCase().indexOf("auto_")!=-1|| current_org_name.toLowerCase().indexOf("spog_sairathan")!=-1|| current_org_name.toLowerCase().indexOf("spog_ramesh")!=-1|| current_org_name.toLowerCase().indexOf("spog_rakesh")!=-1|| current_org_name.toLowerCase().indexOf("spog_bharadwaj")!=-1){
						  if (current_org_name.toLowerCase().indexOf("do_not_delete")==-1){
							  org_id = response.then().extract().path("data["+i+"].organization_id");
							  des_id=response.then().extract().path("data["+i+"].destination_id");
							  if(prevous_del_orgid==null){
								  //for the first time to set del org id
								  prevous_del_orgid=org_id;
								  prevous_del_volid=des_id;
								  previous_org_name=current_org_name;
							  }else if(!org_id.equalsIgnoreCase(prevous_del_orgid)){
								  next_del_orgid=org_id;
								  next_del_volid=des_id;
								  org4SPOGServer.destroyOrganizationWithoutCheck(prevous_del_orgid);
								  System.out.println("destroy org id is:"+prevous_del_orgid);
								  System.out.println("destroy vol id is:"+prevous_del_volid);
								  System.out.println("destroy orf name is:"+previous_org_name);
								  previous_org_name=current_org_name;
								  prevous_del_volid=next_del_volid;
								  prevous_del_orgid=next_del_orgid;
							  }
							  
							  //RecycleDirectVolume(des_id, test);			
//							  deletedestinationbydestination_Id(des_id, user_token,  test);							  
						  }
					  }
//					  if(!current_org_name.equalsIgnoreCase(previous_org_name)&&org_id!=null &&detroyOrgFlag ){
//						  detroyOrgFlag=false;
//						  org4SPOGServer.destroyOrganizationWithoutCheck(org_id);
//						  previous_org_name=current_org_name;
//					  }
				  }
			    }
				if(page==total_page){
					//spogServer.DeleteOrganization(org_id);
					org4SPOGServer.destroyOrganizationWithoutCheck(org_id);
				}
			}
//			ArrayList<HashMap<String,Object>> Destinations = new ArrayList<HashMap<String,Object>>();
//			Destinations = response.then().extract().path("data");	
			//response.then().body("pagination.total_page", 
			//get The total Size for the pages
//			int return_size = Destinations.size();
//			test.log(LogStatus.INFO, "The actual total size is " + return_size);
			
		}
		
		public void recycleJingCloudVolumes(ExtentTest test) {
			// TODO Auto-generated method
			test.log(LogStatus.INFO,"call the API to delete all useless cloud volumes");
			spogServer.userLogin(this.csrAdmin, this.csrPwd);
			String user_token=spogServer.getJWTToken();
			spogDestinationInvoker.setToken(user_token);
			org4SPOGServer.setToken(user_token);
			String current_org_name,previous_org_name="",des_id,org_id=null;
			boolean detroyOrgFlag=false;
			
			//Response response=spogDestinationInvoker.getDestinations(user_token,"organization_name=.*spog_qa.*&destination_type=cloud_direct_volume&page=1&page_size=3000", test);	
			Response response=spogDestinationInvoker.getDestinations(user_token,"destination_type=cloud_direct_volume&organization_name=UpdateAccountTest&page_size=100", test);
			int  total_page=response.then().extract().path("pagination.total_page");
			int  total_size=response.then().extract().path("pagination.page_size");
			String prevous_del_orgid=null, next_del_orgid=null;
			String prevous_del_volid=null, next_del_volid=null;
			for(int page=1;page<=total_page;page++){
				if(page>1){
					response=spogDestinationInvoker.getDestinations(user_token,"destination_type=cloud_direct_volume&organization_name=UpdateAccountTest&page="+page+"&page_size=100", test);
					total_size=response.then().extract().path("pagination.total_size");
				}
				for(int i=0;i<total_size;i++){
				  current_org_name=response.then().extract().path("data["+i+"].organization_name");
				  if(current_org_name!=null){
					  if (current_org_name.toLowerCase().indexOf("updateaccounttest")!=-1){
						  if (current_org_name.toLowerCase().indexOf("do_not_delete")==-1){
							  org_id = response.then().extract().path("data["+i+"].organization_id");
							  des_id=response.then().extract().path("data["+i+"].destination_id");
							  if(prevous_del_orgid==null){
								  //for the first time to set del org id
								  prevous_del_orgid=org_id;
								  prevous_del_volid=des_id;
								  previous_org_name=current_org_name;
							  }else if(!org_id.equalsIgnoreCase(prevous_del_orgid)){
								  next_del_orgid=org_id;
								  next_del_volid=des_id;
								  org4SPOGServer.destroyOrganizationWithoutCheck(prevous_del_orgid);
								  System.out.println("destroy org id is:"+prevous_del_orgid);
								  System.out.println("destroy vol id is:"+prevous_del_volid);
								  System.out.println("destroy orf name is:"+previous_org_name);
								  previous_org_name=current_org_name;
								  prevous_del_volid=next_del_volid;
								  prevous_del_orgid=next_del_orgid;
							  }
							  
							  //RecycleDirectVolume(des_id, test);			
//							  deletedestinationbydestination_Id(des_id, user_token,  test);							  
						  }
					  }
//					  if(!current_org_name.equalsIgnoreCase(previous_org_name)&&org_id!=null &&detroyOrgFlag ){
//						  detroyOrgFlag=false;
//						  org4SPOGServer.destroyOrganizationWithoutCheck(org_id);
//						  previous_org_name=current_org_name;
//					  }
				  }
			    }
				if(page==total_page){
					//spogServer.DeleteOrganization(org_id);
					org4SPOGServer.destroyOrganizationWithoutCheck(org_id);
				}
			}
//			ArrayList<HashMap<String,Object>> Destinations = new ArrayList<HashMap<String,Object>>();
//			Destinations = response.then().extract().path("data");	
			//response.then().body("pagination.total_page", 
			//get The total Size for the pages
//			int return_size = Destinations.size();
//			test.log(LogStatus.INFO, "The actual total size is " + return_size);
			
		}
		
		public void recycleAllUselessCloudVolumesByComplex(ExtentTest test) {
			// TODO Auto-generated method
			test.log(LogStatus.INFO,"call the API to delete all useless cloud volumes");
			spogServer.userLogin(this.csrAdmin, this.csrPwd);
			String user_token=spogServer.getJWTToken();
			spogDestinationInvoker.setToken(user_token);
			org4SPOGServer.setToken(user_token);
			String current_org_name,previous_org_name="",des_id,org_id=null;
			boolean detroyOrgFlag=false;
			
			//Response response=spogDestinationInvoker.getDestinations(user_token,"organization_name=.*spog_qa.*&destination_type=cloud_direct_volume&page=1&page_size=3000", test);	
			Response response=spogDestinationInvoker.getDestinations(user_token,"destination_type=cloud_direct_volume&page_size=100", test);
			int  total_page=response.then().extract().path("pagination.total_page");
			int  total_size=response.then().extract().path("pagination.page_size");
			String prevous_del_orgid=null, next_del_orgid=null;
			String prevous_del_volid=null, next_del_volid=null;
			RecycleDirectVolume("36318152-8846-4f2d-b6e2-5c5cf50400b8", test);	
			//deletedestinationbydestination_Id("36318152-8846-4f2d-b6e2-5c5cf50400b8", user_token,  test);	
			for(int page=1;page<=total_page;page++){
				if(page>1){
					response=spogDestinationInvoker.getDestinations(user_token,"destination_type=cloud_direct_volume&page="+page+"&page_size=100", test);
					total_size=response.then().extract().path("pagination.total_size");
				}
				for(int i=0;i<total_size;i++){
				  current_org_name=response.then().extract().path("data["+i+"].organization_name");
				  if(current_org_name!=null){
					  if (current_org_name.toLowerCase().indexOf("spogqa")!=-1|| current_org_name.toLowerCase().indexOf("spog_qa")!=-1|| current_org_name.toLowerCase().indexOf("auto_")!=-1|| current_org_name.toLowerCase().indexOf("spog_sairathan")!=-1|| current_org_name.toLowerCase().indexOf("spog_ramesh")!=-1|| current_org_name.toLowerCase().indexOf("spog_rakesh")!=-1|| current_org_name.toLowerCase().indexOf("spog_bharadwaj")!=-1){
						  if (current_org_name.toLowerCase().indexOf("do_not_delete")==-1){
							  org_id = response.then().extract().path("data["+i+"].organization_id");
							  des_id=response.then().extract().path("data["+i+"].destination_id");
							  if(prevous_del_volid==null){
								  //for the first time to set del org id
								  prevous_del_orgid=org_id;
								  prevous_del_volid=des_id;
							  }else if(!des_id.equalsIgnoreCase(prevous_del_volid)){
								  RecycleDirectVolume(prevous_del_volid, test);	
								  //deletedestinationbydestination_Id(prevous_del_volid, user_token,  test);	
//								  next_del_orgid=org_id;
//								  next_del_volid=des_id;
//								  org4SPOGServer.destroyOrganizationWithoutCheck(prevous_del_orgid);
								  System.out.println("destroy org id is:"+prevous_del_orgid);
								  System.out.println("destroy vol id is:"+prevous_del_volid);
								  prevous_del_volid=des_id;
								  
							  }
							  if(prevous_del_orgid!=org_id){
								  spogServer.DeleteOrganization(org_id);
								  prevous_del_orgid=org_id;
							  }
							  //RecycleDirectVolume(des_id, test);			
//							  deletedestinationbydestination_Id(des_id, user_token,  test);							  
						  }
					  }
//					  if(!current_org_name.equalsIgnoreCase(previous_org_name)&&org_id!=null &&detroyOrgFlag ){
//						  detroyOrgFlag=false;
//						  org4SPOGServer.destroyOrganizationWithoutCheck(org_id);
//						  previous_org_name=current_org_name;
//					  }
				  }
			    }
				if(page==total_page){
					//spogServer.DeleteOrganization(org_id);
					//org4SPOGServer.destroyOrganizationWithoutCheck(org_id);
					spogServer.DeleteOrganization(org_id);
				}
			}
//			ArrayList<HashMap<String,Object>> Destinations = new ArrayList<HashMap<String,Object>>();
//			Destinations = response.then().extract().path("data");	
			//response.then().body("pagination.total_page", 
			//get The total Size for the pages
//			int return_size = Destinations.size();
//			test.log(LogStatus.INFO, "The actual total size is " + return_size);
			
		}
		
		public void recycleOrgFromCC(String token,ExtentTest test) {
			// TODO Auto-generated method
			test.log(LogStatus.INFO,"call the API to delete all useless cloud volumes");
			spogServer.userLogin(this.csrAdmin, this.csrPwd);
			String user_token=spogServer.getJWTToken();
			spogDestinationInvoker.setToken(user_token);
			org4SPOGServer.setToken(user_token);
			String current_org_name,previous_org_name="",des_id,org_id=null;
			boolean detroyOrgFlag=false;
			ArrayList<String> searchs=new ArrayList<String>();
			searchs.add("spogqa");
			searchs.add("spog_qa");
			searchs.add("spog_udp");
			//searchs.add("spogqa_account_0403");
			//Response response=spogDestinationInvoker.getDestinations(user_token,"organization_name=.*spog_qa.*&destination_type=cloud_direct_volume&page=1&page_size=3000", test);	
			
			for (int j=0;j<searchs.size();j++){
				int total_page=spogServer.getOrgPagesBySearchStringWithCsrLogin(token,searchs.get(j));
				if(total_page>0){
					for(int i=1;i<=total_page;i++ ){
						ArrayList<String> ret=spogServer.getOrgIdsBySearchStringWithCsrLogin(token,searchs.get(j),i);
						if(ret!=null){
							if(ret.size()>0){
								for(int del=0;del<ret.size();del++ ){
									org4SPOGServer.destroyOrganizationWithoutCheck(ret.get(del).toString());
									//spogDestinationInvoker.RecycleOrgFromCC(ret.get(del).toString());
								}
							}
						}						
					}
			    }
			}
		}
		
		/**
		 * login as csr admin to recycle all useless cloud volumes under organization which name has specified string
		 * @author shan, jing
		 * @param OrgNameLike
		 * @param test
		 * @return response
		 */
		public void recycleUselessCloudVolumesUnderOrg(String OrgNameLike, ExtentTest test) {
			// TODO Auto-generated method
			test.log(LogStatus.INFO,"call the API to delete useless cloud volumes under organization which name has specified string");
			spogServer.userLogin(this.csrAdmin, this.csrPwd);
			String user_token=spogServer.getJWTToken();
			spogDestinationInvoker.setToken(user_token);
			String current_org_name,previous_org_name="",des_id,org_id=null;
			String prevous_del_orgid=null;
			String prevous_del_volid=null;
			//Response response=spogDestinationInvoker.getDestinations(user_token,"organization_name=.*spog_qa.*&destination_type=cloud_direct_volume&page=1&page_size=3000", test);	
			Response response=spogDestinationInvoker.getDestinations(user_token,"destination_type=cloud_direct_volume&page_size=100", test);
			int  total_page=response.then().extract().path("pagination.total_page");
			int  total_size=response.then().extract().path("pagination.page_size");
			for(int page=1;page<=total_page;page++){
				if(page>1){
					response=spogDestinationInvoker.getDestinations(user_token,"destination_type=cloud_direct_volume&page="+page+"&page_size=100", test);
					total_size=response.then().extract().path("pagination.total_size");
				}
				for(int i=0;i<total_size;i++){
				  current_org_name=response.then().extract().path("data["+i+"].organization_name");
				  if(current_org_name!=null){
					  if(!current_org_name.equalsIgnoreCase(OrgNameLike)&&org_id!=null){
						  org_id = response.then().extract().path("data["+i+"].organization_id");
						  des_id=response.then().extract().path("data["+i+"].destination_id");
						  if(prevous_del_volid==null){
							  //for the first time to set del org id
							  prevous_del_orgid=org_id;
							  prevous_del_volid=des_id;
						  }else if(!des_id.equalsIgnoreCase(prevous_del_volid)){
							  RecycleDirectVolume(prevous_del_volid, test);	
							  //deletedestinationbydestination_Id(prevous_del_volid, user_token,  test);	
							  System.out.println("destroy org id is:"+prevous_del_orgid);
							  System.out.println("destroy vol id is:"+prevous_del_volid);
							  prevous_del_volid=des_id;
						  }
						  if(prevous_del_orgid!=org_id){
							  spogServer.DeleteOrganization(org_id);
							  prevous_del_orgid=org_id;
						  }
					  }
				  }
			    }
				if(page==total_page){
					spogServer.DeleteOrganization(org_id);
				}
			}			
		}
		
		public void destroyOrgByOrgName(String token,String OrgNameLike, ExtentTest test) {
			// TODO Auto-generated method
			test.log(LogStatus.INFO,"call the API to destroy organization which name has specified string");
			spogDestinationInvoker.setToken(token);
			org4SPOGServer.setToken(token);
			ArrayList<String> searchs=new ArrayList<String>();
			searchs.add(OrgNameLike);
			for (int j=0;j<searchs.size();j++){
				int total_page=spogServer.getOrgPagesBySearchStringWithCsrLogin(token,searchs.get(j));
				if(total_page>0){
					for(int i=1;i<=total_page;i++ ){
						ArrayList<String> ret=spogServer.getOrgIdsBySearchStringWithCsrLogin(token,searchs.get(j),i);
						if(ret!=null){
							if(ret.size()>0){
								for(int del=0;del<ret.size();del++ ){
									System.out.println("destroy org id:"+ret.get(del));
									org4SPOGServer.destroyOrganizationWithoutCheck(ret.get(del).toString());									
								}
							}
						}						
					}
			    }
			}		
		}
		
		/**
		 * @author bharadwajReddy
		 * Check the response for getCloudAccounts
		 * @param response
		 * @param expectedStatusCode
		 * @param DestinationsInfo
		 * @param curr_page
		 * @param page_size
		 * @param FilterStr
		 * @param SortStr
		 * @param Info
		 * @param api
		 * @param test
		 */
		@SuppressWarnings("null")
	
		public void checkGetDestinations(Response response, int expectedStatusCode,
				ArrayList<HashMap<String, Object>> DestinationsInfo, int curr_page, 
				int page_size,String FilterStr, String SortStr, SpogMessageCode Info,
				ExtentTest test) {
			// TODO Auto-generated method stub
			@SuppressWarnings("unused")
			int total_page = 0, return_size = 0, total_size = 0;
			if (curr_page == 0||curr_page==-1) {
				curr_page = 1;
			}
			if (page_size == 0 ||page_size==-1|| page_size > SpogConstants.MAX_PAGE_SIZE) {
				page_size = 20;
			}

			ArrayList<HashMap<String,Object>> Destinations = new ArrayList<HashMap<String,Object>>();
			if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE&&DestinationsInfo!=null&&!DestinationsInfo.isEmpty() ){
				Destinations = response.then().extract().path("data");		
				return_size = Destinations.size();
				//get The total Size for the pages
				total_size = DestinationsInfo.size();
				test.log(LogStatus.INFO, "The actual total size is " + total_size);
				errorHandle.printInfoMessageInDebugFile("expect status code is " + expectedStatusCode);
				spogServer.checkResponseStatus(response, expectedStatusCode);
			} else {
				
				 	errorHandle.printInfoMessageInDebugFile("expect status code is " + expectedStatusCode);
					spogServer.checkResponseStatus(response, expectedStatusCode);
					test.log(LogStatus.PASS,"The validation of the resposne is successfull");
				    return ;
				
			}
			if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE
					&& DestinationsInfo.size() >= Destinations.size()) {
				test.log(LogStatus.INFO, "check response For the Logs ");
	 
				 //1.pagination:http://xiang-gitlab:9080/destinations?page=1&page_size=10
				 //1.Default:http://xiang-gitlab:9080/destinations
				  
				if (((SortStr == null) || (SortStr.equals("")))
						&& ((FilterStr == null) || (FilterStr.equals("")))) {

					// If we Don't mention any Sorting the default sorting is descending order 
					//validating the response for Destinations	
					int j = 0;
					if (page_size == 20) {
						j = Destinations.size() - 1;
					} else {
						j = DestinationsInfo.size() - 1;
						if (curr_page != 1) {
							j = (Destinations.size() - 1) - (curr_page - 1) * page_size;
						}
					}						
					//response validation --some changes are there 
					for (int i = 0; i < Destinations.size() && j >= 0; i++, j--) {
						HashMap<String,Object> actual_Destinations_data,expected_Destinations_data=new HashMap<String,Object>();
					   actual_Destinations_data=Destinations.get(i);expected_Destinations_data=DestinationsInfo.get(j);

						//validate the response for the Destinations
						checkForDestinationsData(actual_Destinations_data,expected_Destinations_data,test);						
					}
				}
				
				// SortStr
				//Supports URI of type :http://xiang-gitlab:9080/destinations/?organization_id=000be072-a303-4646-bb2d-cd6a273d6ad7|56a13ef1-4b51-4aa9-86e6-09151f244e28&sort=create_ts
				//2.http://xiang-gitlab:9080/destinations?sort=create_ts
				//3.http://xiang-gitlab:9080/destinations/?organization_id=000be072-a303-4646-bb2d-cd6a273d6ad7|56a13ef1-4b51-4aa9-86e6-09151f244e28&page=1&page_size=10&sort=create_ts
			
				else if ( //For sorting only and no filtering but includes pagination
						(((SortStr != null) || (!SortStr.equals(""))) && SortStr.contains("create_ts")
						&& ((FilterStr == null) || (FilterStr.equals(""))))

						//For Sorting and Filtering both 

						|| (((SortStr != null)||(!SortStr.equals(""))) && SortStr.contains("create_ts")
								&& ((FilterStr != null) || (!FilterStr.equals(""))))
								) {
	                    
					// validating the response for all the users who are sorted based on response in ascending
					// order and descending order
					if (SortStr.contains("asc")) {
						int j = 0;
						if (curr_page != 1) {
							j = (curr_page - 1) * page_size;
						}	

						//validate the response --some changes are there 
						for(int i=0;i<Destinations.size();i++,j++){
							HashMap<String,Object> actual_Destinations_data,expected_Destinations_data=new HashMap<String,Object>();
							actual_Destinations_data=Destinations.get(i);expected_Destinations_data=DestinationsInfo.get(j);

							//validate the response for the Destinations
							checkForDestinationsData(actual_Destinations_data,expected_Destinations_data,test);				
						}
					} else if(SortStr.contains("desc")||(FilterStr.contains(">"))||(FilterStr.contains("<"))){
						int j = 0;
						if (page_size == 20) {
							j = Destinations.size() - 1;
						} else {
							j = DestinationsInfo.size() - 1;
							if (curr_page != 1) {
								j = (Destinations.size() - 1) - (curr_page - 1) * page_size;
							}
						}						
						//response validation --some changes are there 
						for (int i = 0; i < Destinations.size() && j >= 0; i++, j--) {
							HashMap<String,Object> actual_Destinations_data,expected_Destinations_data=new HashMap<String,Object>();
							actual_Destinations_data=Destinations.get(i);
							expected_Destinations_data=	DestinationsInfo.get(j);

							//validate the response for the Destinations
							checkForDestinationsData(actual_Destinations_data,expected_Destinations_data,test);						
						}
					} 
				}//Only filtering and no sorting (For now supports only for the organization id )
				else if(((SortStr == null) || (SortStr.equals("")))&&((FilterStr != null) || (!FilterStr.equals("")))){

					int j = 0;
					if (page_size == 20) {
						j = Destinations.size() - 1;
					} else {
						j = DestinationsInfo.size() - 1;
						if (curr_page != 1) {
							j = (Destinations.size() - 1) - (curr_page - 1) * page_size;
						}
					}						
					//response validation --some changes are there 
					for (int i = 0; i < Destinations.size() && j >= 0; i++, j--) {
						HashMap<String,Object> actual_Destinations_data=Destinations.get(i),expected_Destinations_data=Destinations.get(j);

						//validate the response for the Destinations
						checkForDestinationsData(actual_Destinations_data,expected_Destinations_data,test);						
					}
				}
				// check the response validation for pages,page_size,total_size
				spogServer.validateResponseForPages(curr_page, page_size, response, total_size, test);
			} else  {
				String expectedErrorMessage="",expectedErrorCode="";
				if(Info.getStatus()!="0010000"){
					expectedErrorMessage=Info.getStatus();
					if(expectedErrorMessage.contains("{0}")){
						System.out.println(expectedErrorMessage);
					}
					expectedErrorCode=Info.getCodeString();
				}
				spogServer.checkErrorMessage(response, expectedErrorMessage);
				test.log(LogStatus.PASS, "The value of the message is "+expectedErrorMessage);
				spogServer.checkErrorCode(response, expectedErrorCode);
				test.log(LogStatus.PASS, "The value of the code  generated  is "+expectedErrorCode);
				test.log(LogStatus.INFO,"The value of the  response generated actually is :"+response.getStatusCode());
			}
		}
		
		/**
		 * Validating the response for the destinations data
		 * @author Bharadwaj.Ghadiam
		 * @param actual_Destinations_data(Destinations information)
		 * @param expected_Destinations_data(expected Destinations information)
		 * @param test
		 */
		private void checkForDestinationsData(
				HashMap<String, Object> actual_Destinations_data,
				HashMap<String, Object> expected_Destinations_data,
				ExtentTest test)  {
			ArrayList<String> available_actions=new ArrayList<String>();
			//compose available actions
			available_actions.add("edit");
			available_actions.add("view_recovery_point");
			/*available_actions.add("delete");
			available_actions.add("start");
			available_actions.add("rps_jumpstart");*/
		            
			      test.log(LogStatus.INFO,"validating the destinaiton_id");
	              spogServer.assertResponseItem(actual_Destinations_data.get("destination_id"),expected_Destinations_data.get("destination_id"),test);
	              test.log(LogStatus.INFO,"validating the destinaiton_name");
	              spogServer.assertResponseItem(actual_Destinations_data.get("destination_name"), expected_Destinations_data.get("destination_name"),test);
	              test.log(LogStatus.INFO,"validating the destination_type");
	              spogServer.assertResponseItem(actual_Destinations_data.get("destination_type"), expected_Destinations_data.get("destination_type"),test);
	              test.log(LogStatus.INFO,"validating the organization_id");
	              spogServer.assertResponseItem(actual_Destinations_data.get("organization_id"), expected_Destinations_data.get("organization_id"),test);
	              test.log(LogStatus.INFO,"validating the organization_name");
	              spogServer.assertResponseItem(actual_Destinations_data.get("organization_name"), expected_Destinations_data.get("organization_name"),test);
	              test.log(LogStatus.INFO,"validating the site_id");
	              spogServer.assertResponseItem(actual_Destinations_data.get("site_id"), expected_Destinations_data.get("site_id"),test);
	                         
	              test.log(LogStatus.INFO,"validating the available actions");
	              spogServer.assertResponseItem(actual_Destinations_data.get("available_actions"), available_actions,test);
	            
				//validation of the response for the cloud_direct_volume
				if(actual_Destinations_data.get("destination_type").equals("cloud_direct_volume")) {
					   test.log(LogStatus.INFO,"validating the datacenters data");
		      //     spogServer.assertResponseItem(actual_Destinations_data.get("datacenter_location"), expected_Destinations_data.get("datacenter_location"),test);
		      //     spogServer.assertResponseItem(actual_Destinations_data.get("datacenter_region"), expected_Destinations_data.get("datacenter_region"),test); 
					
		           HashMap<String,Object> cloud_direct=(HashMap<String, Object>) actual_Destinations_data.get("cloud_direct_volume");
					HashMap<String,Object> cloud_volume=(HashMap<String, Object>) expected_Destinations_data.get("cloud_direct_volume");


					//validation of the information inside the cloud_direct_volume   
	                test.log(LogStatus.INFO,"validating the response for cloud_direct_volume");
					//	spogServer.assertResponseItem(cloud_direct.get("cloud_direct_volume_name"),cloud_volume.get("cloud_direct_volume_name"),test);	
					spogServer.assertResponseItem(cloud_direct.get("volume_type").toString(),cloud_volume.get("volume_type").toString(),test);
					spogServer.assertResponseItem(cloud_direct.get("hostname").toString(),cloud_volume.get("hostname").toString(),test);
					spogServer.assertResponseItem(cloud_direct.get("retention_id").toString(),cloud_volume.get("retention_id").toString(),test);		

                   /* test.log(LogStatus.INFO, "validating the response for total_usage");
                    assertNotNull(cloud_direct.get("total_usage"));
                    test.log(LogStatus.INFO, "validating the response for primary usage");
                    assertNotNull(cloud_direct.get("primary usage"));
                    test.log(LogStatus.INFO, "validating the response for snapshot usage");
                    assertNotNull(cloud_direct.get("snapshot usage"));*/
					//validation For The Response for cloud_direct_retention_dictionary details
					//HashMap's for the actual and Expected retention values 
	                
					test.log(LogStatus.INFO,"validating the retention values for cloud_direct volumes");
					HashMap<String,Object> retention,Retention= new HashMap<String,Object>();
					retention=(HashMap<String, Object>) cloud_direct.get("retention");
					if(!(cloud_direct.get("retention_id").equals("custom"))){
						Retention=getRetention();
						Iterator<Entry<String, Object>> entries = Retention.entrySet().iterator();   						
						while(entries.hasNext()){
							Entry<String,Object> entry= entries.next();
							if(cloud_direct.get("retention_id").equals(entry.getKey())){
								HashMap<String,String> Retention1 =new HashMap<String,String>();
								Retention1=(HashMap<String, String>) entry.getValue();
								spogServer.assertResponseItem(retention.get("age_hours_max"),Retention1.get("age_hours_max"),test);
								spogServer.assertResponseItem(retention.get("age_four_hours_max"),Retention1.get("age_four_hours_max"),test);
								spogServer.assertResponseItem(retention.get("age_days_max"),Retention1.get("age_days_max"),test);
								spogServer.assertResponseItem(retention.get("age_weeks_max "),Retention1.get("age_weeks_max "),test);
								spogServer.assertResponseItem(retention.get("age_months_max"),Retention1.get("age_months_max"),test);
								spogServer.assertResponseItem(retention.get("age_years_max"),Retention1.get("age_years_max"),test);
								break;
							}
						}
					}
				     //Retention data for the custom retention data dictionary 
					else {
						Retention=(HashMap<String, Object>) cloud_volume.get("retention");
						test.log(LogStatus.INFO,"validating the retention_custom for cloud_direct_volume");
						spogServer.assertResponseItem(retention.get("age_hours_max"),Retention.get("age_hours_max"),test);
						spogServer.assertResponseItem(retention.get("age_four_hours_max"),Retention.get("age_four_hours_max"),test);
						spogServer.assertResponseItem(retention.get("age_days_max"),Retention.get("age_days_max"),test);
						spogServer.assertResponseItem(retention.get("age_weeks_max "),Retention.get("age_weeks_max "),test);
						spogServer.assertResponseItem(retention.get("age_months_max"),Retention.get("age_months_max"),test);
						spogServer.assertResponseItem(retention.get("age_years_max"),Retention.get("age_years_max"),test);
					}
				}
				//Related to cloud_hybrid data Store 
				else if(actual_Destinations_data.get("destination_type").equals("cloud_hybrid_store")){
					test.log(LogStatus.INFO,"validating for the cloud_hybrid_data_store");
					HashMap<String,Object> cloud_dedupe=(HashMap<String, Object>) actual_Destinations_data.get("cloud_hybrid_store");
					HashMap<String,Object> cloud_volume=(HashMap<String, Object>) expected_Destinations_data.get("cloud_hybrid_store");
	                 
					//
					/*spogServer.assertResponseItem(cloud_dedupe.get("data_store_folder"),cloud_volume.get("data_store_folder"),test);
					spogServer.assertResponseItem(cloud_dedupe.get("data_destination"),cloud_volume.get("data_destination"),test);
					spogServer.assertResponseItem(cloud_dedupe.get("index_destination"),cloud_volume.get("index_destination"),test);
					spogServer.assertResponseItem(cloud_dedupe.get("hash_destination"),cloud_volume.get("hash_destination"),test);
					*/spogServer.assertResponseItem(cloud_dedupe.get("concurrent_active_node"),cloud_volume.get("concurrent_active_node"),test);
					spogServer.assertResponseItem(cloud_dedupe.get("is_deduplicated"),cloud_volume.get("is_deduplicated"),test);
					spogServer.assertResponseItem(cloud_dedupe.get("block_size"),cloud_volume.get("block_size"),test);
					
					if(cloud_dedupe.containsKey("is_compressed"))
					spogServer.assertResponseItem(cloud_dedupe.get("is_compressed").toString(),cloud_volume.get("is_compressed").toString(),test);
					
					/*spogServer.assertResponseItem(cloud_dedupe.get("hash_memory"),cloud_volume.get("hash_memory"),test);
					spogServer.assertResponseItem(cloud_dedupe.get("encryption_password"),cloud_volume.get("encryption_password"),test);
					spogServer.assertResponseItem(cloud_dedupe.get("occupied_space"),cloud_volume.get("occupied_space"),test);
					spogServer.assertResponseItem(cloud_dedupe.get("stored_data"),cloud_volume.get("stored_data"),test);
					spogServer.assertResponseItem(cloud_dedupe.get("deduplication_rate").toString(),cloud_volume.get("deduplication_rate").toString(),test);
					spogServer.assertResponseItem(cloud_dedupe.get("compression_rate").toString(),cloud_volume.get("compression_rate").toString(),test);                 
			*/	}

				/*	//Related to the validation of the shared folder 
						else{
							test.log(LogStatus.INFO,"This information is related to the shared folder");
						}*/

				
				//Validating the last_job_status
				
				test.log(LogStatus.INFO,"Validating the response for last_job");
		    	ArrayList<HashMap<String, Object>> actual_last_job= (ArrayList<HashMap<String, Object>>) actual_Destinations_data.get("last_job");
		    	if(!actual_last_job.isEmpty())
		    	{
				HashMap<String,Object> actual_last_job_data=actual_last_job.get(0);
				ArrayList<HashMap<String,Object>>expected_last_job_data=(ArrayList<HashMap<String, Object>>)  expected_Destinations_data.get("last_job");
				HashMap<String,Object>expected_last_job=expected_last_job_data.get(0);
				
				test.log(LogStatus.INFO,"validating the response for type,start_time_ts,status,end_time_ts,percentage_complete");
				spogServer.assertResponseItem(actual_last_job_data.get("type"),expected_last_job.get("job_type"),test);
				spogServer.assertResponseItem(actual_last_job_data.get("start_time_ts").toString(),expected_last_job.get("start_time_ts").toString(),test);			
				spogServer.assertResponseItem(actual_last_job_data.get("status"),expected_last_job.get("job_status"),test);
				spogServer.assertResponseItem(actual_last_job_data.get("end_time_ts").toString(),expected_last_job.get("end_time_ts").toString(),test);
				spogServer.assertResponseItem(actual_last_job_data.get("percent_complete").toString(),expected_last_job.get("percent_complete").toString(),test);
		    	}
				
			}


		
		/**
		 * @author Bharadwaj.Ghadiam
		 * @return cloud_volume information
		 */
		public HashMap<String, Object> getComposedInforamtion() {
			// TODO Auto-generated method stub
			HashMap<String,Object> cloud_info=jp.getDestinationInfo();
			return cloud_info;
		}
		
		
		/**
		 * Create Destination columns for specified user
		 * @author Kiran.Sripada
		 * @param userid
		 * @param token
		 * @param List of columns
		 * @param ExtentTest
		 */
		public Response createDestinationColumnsForSpecifiedUser(String user_id, String validToken, ArrayList<HashMap<String, Object>> expected_columns, ExtentTest test) {
			
			 Map<String,ArrayList<HashMap<String, Object>>> logcolumnsInfo =jp.jobColumnInfo(expected_columns);
			 Response response=spogDestinationInvoker.createDestinationColumnsByUserId(validToken,user_id,logcolumnsInfo,test);
			 
			return response;
			
		}
		
		/**
		 * Create destination columns for specified user
		 * @author Kiran.Sripada
		 * @param userid
		 * @param token
		 * @param List of columns
		 * @param ExtentTest
		 */
		public Response createDestinationColumnsForLoggedInUser(String validToken, ArrayList<HashMap<String, Object>> expected_columns, ExtentTest test) {
			
			 Map<String,ArrayList<HashMap<String, Object>>> logcolumnsInfo =jp.jobColumnInfo(expected_columns);
			 Response response=spogDestinationInvoker.createDestinationColumnsForLoggedInUser(validToken,logcolumnsInfo,test);
			 
			return response;
			
		}
		
		/*
		 * Update Destination columns for specified user
		 * @author Rakesh.Chalamala
		 * @param token
		 * @param user_id
		 * @param test
		 */
		public Response updateDestinationColumnsByUserId(String user_Id,
														String token,
														ArrayList<HashMap<String, Object>> expectedresponse,
														ArrayList<HashMap<String, Object>> defaultcolumnresponse,
														int expectedStatusCode,
														SpogMessageCode ExpectedErrorMessage,
														ExtentTest test
														) {
		
			Map<String, ArrayList<HashMap<String, Object>>> logcolumnsInfo =jp.jobColumnInfo(expectedresponse);
			Response response = spogDestinationInvoker.updateDestinationColumnsByUserId(token, user_Id, logcolumnsInfo, test);
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
								checkdestinationcolumns(actualresponse.get(i),expectedresponse.get(i),defaultcolumnresponse.get(j),test);
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
		
		public Response updateDestinationColumnsForLoggedinUser(String token,
														ArrayList<HashMap<String, Object>> expectedresponse,
														ArrayList<HashMap<String, Object>> defaultcolumnresponse,
														int expectedStatusCode,
														SpogMessageCode ExpectedErrorMessage,
														ExtentTest test
														) {
			Map<String, ArrayList<HashMap<String, Object>>> logcolumnsInfo =jp.jobColumnInfo(expectedresponse);
			Response response = spogDestinationInvoker.updateDestinationColumnsForLoggedInUser(token, logcolumnsInfo, test);
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
								checkdestinationcolumns(actualresponse.get(i),expectedresponse.get(i),defaultcolumnresponse.get(j),test);
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
		
		public HashMap<String, Object> composedestination_Column(String columnId, String sort, String filter, String visible,
				String orderId) {
			// TODO Auto-generated method stub
			HashMap<String,Object> temp = new HashMap<>();
			temp.put("column_id", columnId);
			if(sort==null||sort=="") {
				temp.put("sort", sort);
			}else if(!sort.equals("none")){
				temp.put("sort",sort); 
			}
			if(filter==null||filter=="") {
				temp.put("sort", sort);
			}else if(!filter.equals("none"))
			{

				temp.put("filter",filter); 
			}
			if(visible==null||visible=="") {
				temp.put("sort", sort);
			}else if(!visible.equals("none"))
			{
				temp.put("visible",visible);
			}

			temp.put("order_id",Integer.parseInt(orderId));

			return temp;

			
		}
		
		
		/**
		 * Compare the destination columns
		 * @author Kiran.Sripada
		 * @param response -- Actual response 
		 * @param expected columns 
		 * @param default columns
		 * @param expected status code
		 * @param expectederrormessage
		 * @param test
		 */
		
		public void CompareDestinationColumnsContent(Response response, ArrayList<HashMap<String,Object>> expectedColumnsContents, ArrayList<HashMap<String,Object>> defaultColumnsContents,
				int expectedstatuscode, SpogMessageCode ExpectedErrorMessage, ExtentTest test) 
		{
			// TODO Auto-generated method stub
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
									checkdestinationcolumns(actualcolumnsHeadContent.get(i),expectedColumnsContents.get(i),defaultColumnsContents.get(j),test);
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
		
		
		/**
		 * Delete Destination columns for specified user ID
		 * @author Kiran.Sripada
		 * @param userID
		 * @param token
		 * @param expectedstatuscode
		 * @param expectederrormessage
		 * @param test
		 */
		public void deleteDestinationColumnsforSpecifiedUserwithCheck(String user_Id, String token, int expectedstatuscode, SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
			Response response = spogDestinationInvoker.DeleteDestinationsColumnsByUserId(token,user_Id, test);
			spogServer.checkResponseStatus(response, expectedstatuscode);
			if(expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
				test.log(LogStatus.PASS, "The log columns are successfully deleted");
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


		/**
		 * Delete Destination columns for logged in user
		 * @author Kiran.Sripada
		 * @param token
		 * @param expectedstatuscode
		 * @param expectederrormessage
		 * @param test
		 */
		public void deleteDestinatinColumnsforLoggedInUserwithCheck(String token, int expectedstatuscode, SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
			Response response = spogDestinationInvoker.DeleteDestinationsColumnsForLoggedInUser(token,test);
			spogServer.checkResponseStatus(response, expectedstatuscode);
			if(expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
				test.log(LogStatus.PASS, "The log columns are successfully deleted");
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
		 * Get Destination columns for specified user
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
		public void getDestinationColumnsForSpecifiedUser(String user_Id, 
															String token, 
															ArrayList<HashMap<String,Object>> expectedresponse,
															ArrayList<HashMap<String,Object>> defaultcolumnresponse,
															int expectedStatusCode, 
															SpogMessageCode ExpectedErrorMessage , 
															ExtentTest test)
		{
			test.log(LogStatus.INFO, "Call the res API to get the destination columns for specified user");
			Response response = spogDestinationInvoker.getDestinationsColumnsByUserId(token, user_Id, test);
			
			spogServer.checkResponseStatus(response, expectedStatusCode, test);
			
			if(expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
				ArrayList<HashMap<String,Object>> actualresponse = response.then().extract().path("data");
				
				test.log(LogStatus.INFO, "Sort the actual response by order_id");
				actualresponse = spogServer.sortArrayListbyInt(actualresponse, "order_id");
				
//				if(!(expectedresponse.get(0).get("order_id").getClass().getTypeName()==defaultcolumnresponse.get(0).get("order_id").getClass().getTypeName())) {
				test.log(LogStatus.INFO, "Sort the expected response by order_id");
				expectedresponse = spogServer.sortArrayListbyInt(expectedresponse, "order_id");
//			//	}
				
				if(actualresponse.size() == defaultcolumnresponse.size() || actualresponse.size() == defaultcolumnresponse.size()-1) { // size should be same as default

					ArrayList<HashMap<String, Object>> col_respnse = null;
					boolean flag = false;
					
	 				for(int i=0;i<actualresponse.size();i++) {
	 					flag = false; 					
						for(int j=0;j<expectedresponse.size();j++) {
							if(actualresponse.get(i).get("column_id").equals(expectedresponse.get(j).get("column_id"))) {
								for (int k = 0; k < defaultcolumnresponse.size(); k++) {
									if(expectedresponse.get(j).get("column_id").equals(defaultcolumnresponse.get(k).get("column_id"))) {
										checkdestinationcolumns(actualresponse.get(i),expectedresponse.get(j),defaultcolumnresponse.get(k),test);
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
									
									checkdestinationcolumns(actualresponse.get(i),col_respnse.get(k),col_respnse.get(k),test);
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
		public void getDestinationColumnsForLoggedinUser(String token, 
														ArrayList<HashMap<String,Object>> expectedresponse,
														ArrayList<HashMap<String,Object>> defaultcolumnresponse,
														int expectedStatusCode, 
														SpogMessageCode ExpectedErrorMessage , 
														ExtentTest test) {
			test.log(LogStatus.INFO, "Call the res API to get the destination columns for loggedin user");
			Response response = spogDestinationInvoker.getDestinationsColumnsForLoggedInUser(token, test);
			
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
										checkdestinationcolumns(actualresponse.get(i),expectedresponse.get(j),defaultcolumnresponse.get(k),test);
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
									
									checkdestinationcolumns(actualresponse.get(i),col_respnse.get(k),col_respnse.get(k),test);
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
		
		public void checkdestinationcolumns(HashMap<String,Object> actual_response, HashMap<String,Object> expected_response, HashMap<String,Object> defaultcolumnvalues,ExtentTest test ) 
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
		
		public void checkDestinationFilters(HashMap<String,Object> actual_response, HashMap<String,Object> expected_response, ExtentTest test) {
			test.log(LogStatus.INFO, "Compare the filter id");
			spogServer.assertResponseItem(actual_response.get("filter_id").toString(),expected_response.get("filter_id").toString(),test);
			
			test.log(LogStatus.INFO,"Compare the filter name");
			spogServer.assertResponseItem(actual_response.get("filter_name").toString(),expected_response.get("filter_name").toString(),test);
			
			test.log(LogStatus.INFO,"Compare the user id");
			spogServer.assertResponseItem(actual_response.get("user_id").toString(),expected_response.get("user_id").toString(),test);
			
			test.log(LogStatus.INFO,"Compare the organization id");
			spogServer.assertResponseItem(actual_response.get("organization_id").toString(),expected_response.get("organization_id").toString(),test);
			
			test.log(LogStatus.INFO,"Compare the destination name");
			if(expected_response.get("destination_name") == null || expected_response.get("destination_name") == "none") {
				test.log(LogStatus.PASS, "filter not applied on destination name");
				assertNull(null," filter not applied on destination name");
			}else {
				spogServer.assertResponseItem(actual_response.get("destination_name").toString(),expected_response.get("destination_name").toString(),test);
			}
			
			
			test.log(LogStatus.INFO,"Compare the policy id");
			ArrayList<String> policyids= (ArrayList<String>) actual_response.get("policy_id");
			
			if(policyids==null) {
				test.log(LogStatus.PASS, "filter not applied on policy id");
				assertNull(null," filter not applied on policy id");
			}else {
				if(policyids.size()>1) {
					for(int i=0; i<policyids.size(); i++) {
						actual_response.get(policyids.get(i));
						spogServer.assertResponseItem(actual_response.get(policyids.get(i)).toString(),expected_response.get("policy_id").toString(),test);
					}
				}else {
						spogServer.assertResponseItem(policyids.get(0).toString(),expected_response.get("policy_id").toString(),test);
				}
			}
			
			test.log(LogStatus.INFO,"Compare the destinatin type");
			if(expected_response.get("destination_type") == null || expected_response.get("destination_type") == "none")
				spogServer.assertResponseItem(actual_response.get("destination_type").toString(),"all",test);
			else {
				spogServer.assertResponseItem(actual_response.get("destination_type").toString(),expected_response.get("destination_type").toString(),test);
			}
			
			test.log(LogStatus.INFO,"Compare the default value");
			//spogServer.assertResponseItem(actual_response.get("is_default").toString(),expected_response.get("is_default").toString(),test);
			test.log(LogStatus.INFO,"Compare the count");
			//spogServer.assertResponseItem(actual_response.get("count"),expected_response.get("count"),test);
			
		}
		
		/**
		 * create destinationfilter for loggedin user and return filter ID
		 * @author Zhaoguo.Ma
		 * @param filterName
		 * @param destinationName
		 * @param policyID
		 * @param destinationType
		 * @param isDefault
		 * @param test
		 * @return
		 */
		public String createDestinationFilterForLoggedinUserWithCheck(String filterName, String destinationName,
				String policyID, String destinationType, String isDefault, ExtentTest test) {

			Map<String, Object> destinationFilterInfo = jp.composeDestinationFilterInfo(filterName, destinationName,
					policyID, destinationType, isDefault);
			
			HashMap<String, Object> filter_info = new HashMap<String, Object>();
			if (!destinationFilterInfo.isEmpty()) {
				filter_info = jp.composeFilterInfo(filterType.destination_filter.toString(),
						filterName,  "none", "none",
						Boolean.valueOf(isDefault), (HashMap<String,Object>)destinationFilterInfo);
			}
			Response response = spogServer.createFilters(spogDestinationInvoker.getToken(), filter_info, "", test);
/*
			Response response = spogDestinationInvoker.createDestinationFilterForLoggedinUser(destinationFilterInfo);*/
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);

			String responseDestinationType = response.then().extract().path("data.destination_type").toString().replace("[", "").replace("]", "");
			ArrayList<String> responsePolicyID = response.then().extract().path("data.policy_id");
			boolean responseIsDefault = response.then().extract().path("data.is_default");
			String responseDestinationName = response.then().extract().path("data.destination_name");
			String filterID = response.then().extract().path("data.filter_id");

			// String responseUserID = GetLoggedinUser_UserID();
			// String responseOrganizationID = GetLoggedinUserOrganizationID();

			response.then().body("data.filter_name", equalTo(filterName));
			// .body("data.user_id", equalTo(responseUserID)).body("data.organization_id",
			// equalTo(responseOrganizationID));

			spogServer.assertResponseItem(destinationName, responseDestinationName);

			spogServer.assertFilterItem(policyID, responsePolicyID);

			if (destinationType == null || destinationType.equalsIgnoreCase("none") || (destinationType == "")) {
				assertEquals(responseDestinationType, "all");
			} else {
				assertEquals(responseDestinationType, destinationType);
			}

			if (isDefault == null || isDefault.equalsIgnoreCase("none") || isDefault == ""
					|| isDefault.equalsIgnoreCase("false")) {
				Assert.assertEquals(responseIsDefault, false);
			} else if (isDefault.equalsIgnoreCase("true")) {
				Assert.assertEquals(responseIsDefault, true);
			}

			return filterID;
		}	
		
		/**
		 * create destinationfilter for loggedin user and return filter ID
		 * @author Zhaoguo.Ma
		 * @param filterName
		 * @param destinationName
		 * @param policyID
		 * @param destinationType
		 * @param isDefault
		 * @param test
		 * @return
		 */
		public Response createDestinationFilterForLoggedinUser(String filterName, String destinationName,
				String policyID, String destinationType, String isDefault, ExtentTest test) {

			Map<String, Object> destinationFilterInfo = jp.composeDestinationFilterInfo(filterName, destinationName,
					policyID, destinationType, isDefault);
			
			HashMap<String, Object> filter_info = new HashMap<String, Object>();
			if (!destinationFilterInfo.isEmpty()) {
				filter_info = jp.composeFilterInfo(filterType.destination_filter.toString(),
						filterName, "none", "none",
						Boolean.valueOf(isDefault), (HashMap<String,Object>)destinationFilterInfo);
			}
			Response response = spogServer.createFilters(spogDestinationInvoker.getToken(), filter_info, "", test);

		/*	Response response = spogDestinationInvoker.createDestinationFilterForLoggedinUser(destinationFilterInfo);*/
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);

			String responseDestinationType = response.then().extract().path("data.destination_type").toString().replace("[", "").replace("]", "");
			ArrayList<String> responsePolicyID = response.then().extract().path("data.policy_id");
			boolean responseIsDefault = response.then().extract().path("data.is_default");
			String responseDestinationName = response.then().extract().path("data.destination_name");
			String filterID = response.then().extract().path("data.filter_id");

			// String responseUserID = GetLoggedinUser_UserID();
			// String responseOrganizationID = GetLoggedinUserOrganizationID();

			response.then().body("data.filter_name", equalTo(filterName));
			// .body("data.user_id", equalTo(responseUserID)).body("data.organization_id",
			// equalTo(responseOrganizationID));

			spogServer.assertResponseItem(destinationName, responseDestinationName);

			spogServer.assertFilterItem(policyID, responsePolicyID);

			if (destinationType == null || destinationType.equalsIgnoreCase("none") || (destinationType == "")) {
				assertEquals(responseDestinationType, "all");
			} else {
				assertEquals(responseDestinationType, destinationType);
			}

			if (isDefault == null || isDefault.equalsIgnoreCase("none") || isDefault == ""
					|| isDefault.equalsIgnoreCase("false")) {
				Assert.assertEquals(responseIsDefault, false);
			} else if (isDefault.equalsIgnoreCase("true")) {
				Assert.assertEquals(responseIsDefault, true);
			}

			return response;
		}
		
		/**
		 * create destinationfilter for loggedin user and check response/error code
		 * @author Zhaoguo.Ma
		 * @param filterName
		 * @param destinationName
		 * @param policyID
		 * @param destinationType
		 * @param isDefault
		 * @param statusCode
		 * @param errorCode
		 * @param test
		 */
		public void createDestinationFilterForLoggedinUserAndCheckCode(String filterName, String destinationName,
				String policyID, String destinationType, String isDefault, int statusCode, String errorCode,
				ExtentTest test) {

			Map<String, Object> destinationFilterInfo = jp.composeDestinationFilterInfo(filterName, destinationName,
					policyID, destinationType, isDefault);

			HashMap<String, Object> filter_info = new HashMap<String, Object>();
			if (!destinationFilterInfo.isEmpty()) {
				filter_info = jp.composeFilterInfo(filterType.destination_filter.toString(),
						filterName,  "none", "none",
						Boolean.valueOf(isDefault), (HashMap<String,Object>)destinationFilterInfo);
			}
			Response response = spogServer.createFilters(spogDestinationInvoker.getToken(), filter_info, "", test);
			/*Response response = spogDestinationInvoker.createDestinationFilterForLoggedinUser(destinationFilterInfo);*/
			spogServer.checkResponseStatus(response, statusCode);
			spogServer.checkErrorCode(response, errorCode);
		}
		/**
		 * @author Bharadwaj.Ghadiam
		 * This method is used to compose the last_job_information
		 * @param start_time_ts
		 * @param endTimeTS
		 * @param percent_complete
		 * @param job_status
		 * @param job_type
		 * @param job_method
		 * @return
		 */
		public HashMap<String, ArrayList<HashMap<String, Object>>> composeLastJob(
				long start_time_ts, long endTimeTS,Double percent_complete, String job_status,
				String job_type, String job_method) {
			// TODO Auto-generated method stub
			HashMap<String,ArrayList<HashMap<String,Object>>> last_job=new HashMap<String,ArrayList<HashMap<String,Object>>>();
			ArrayList<HashMap<String,Object>> log=new ArrayList<HashMap<String,Object>>();
			HashMap<String,Object> log_data=new HashMap<String,Object>();
			if(job_type.equals("backup")||job_type.equals("vm_backup") ||job_type.equals("vmware_vapp_backup")||job_type.equals("file_copy_backup")||	
					job_type.equals ("office365_backup")|| job_type.equals ("cifs_backup")||job_type.equals ("sharepoint_backup")) {				
				if(job_method.equals("full")){
					job_type=job_type+"_"+job_method;  
				}else if(job_method.equals("incremental")){
					job_type=job_type+"_"+job_method;  
				}else if(job_method.equals("verified")){
					job_type=job_type+"_"+job_method;   
				}
			}
			log_data.put("start_time_ts",start_time_ts);
			log_data.put("end_time_ts", endTimeTS);
			log_data.put("percent_complete", percent_complete);
			log_data.put("job_status",job_status);
			log_data.put("job_type",job_type);
	        log.add(log_data); 
			last_job.put("last_job",log);
			return last_job;
		}
       
		
	/*	{
		    "status": 200,
		    "data": {
		        "storage_level": "volume",
		        "id": "a789859c-ae34-49fb-bfdf-c3eee78b15e9",
		        "name": "dest1",
		        "usage": [
		            {
		                "ts": 1520467200000,
		                "usage": 483520
		            }
		        ],
		        "primary": [
		            {
		                "ts": 1520467200000,
		                "usage": 483520
		            }
		        ],
		        "latest_usage": 483520,
		        "latest_ts": 1520499600000
		    },
		    "errors": []
		}*/
		/**
		 * Validating the response for get destinations usage Count
		 * @author Bharadwaj.Ghadiam
		 * @param response
		 * @param expectedStatusCode
		 * @param expectedInfo
		 * @param test
		 */
		public void checkgetDestinationUsage(Response response,
				String destination_id, String destination_name,
				int expectedStatusCode, SpogMessageCode expectedInfo,
				ExtentTest test ) {
			
			String expectedErrorMessage = "", expectedErrorCode = "";
			
			test.log(LogStatus.INFO,"validating the response for the get destination usage");
			spogServer.checkResponseStatus(response, expectedStatusCode,test);
			
			if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
				HashMap<String,Object> usage_data=response.then().extract().path("data");
				
				test.log(LogStatus.INFO, "Compare the destination_id");
				spogServer.assertResponseItem(destination_id, usage_data.get("id"), test);
				
				test.log(LogStatus.INFO, "Compare the destination_id");
				spogServer.assertResponseItem(destination_name, usage_data.get("name"), test);
				if (usage_data.containsKey("usage")) {
					assertTrue(true, "Response contains the usage hash");
					ArrayList<HashMap<String, Object>> details = (ArrayList<HashMap<String, Object>>) usage_data.get("usage");
					
					assertTrue(true, "The usage at time "+details.get(0).get("ts")+" is "+details.get(0).get("usage"));
					
				}
				if (usage_data.containsKey("primary")) {
					assertTrue(true, "Response contains the primary hash");
					ArrayList<HashMap<String, Object>> details = (ArrayList<HashMap<String, Object>>) usage_data.get("primary");
					
					assertTrue(true, "The usage at time "+details.get(0).get("ts")+" is "+details.get(0).get("usage"));
					
				}				
				assertTrue(usage_data.get("storage_level").toString().equals("volume"),"Storage_level is volume");
			   
			} 
			else {
				if (expectedInfo.getStatus() != "0010000") {
					expectedErrorMessage = expectedInfo.getStatus();
					if (expectedErrorMessage.contains("{0}")) {
						expectedErrorMessage = expectedErrorMessage.replace("{0}", spogServer.getUUId());
						System.out.println(expectedErrorMessage);
					}
					expectedErrorCode = expectedInfo.getCodeString();
				}
				spogServer.checkErrorMessage(response, expectedErrorMessage);
				test.log(LogStatus.PASS, "The value of the message is " + expectedErrorMessage);
				spogServer.checkErrorCode(response, expectedErrorCode);
				test.log(LogStatus.PASS, "The value of the code  generated  is " + expectedErrorCode);
				test.log(LogStatus.INFO,
						"The value of the  response generated actually is :" + response.getStatusCode());
			}	
		}
		/*
		 * @author Rakesh.Chalamala
		 * @param token
		 * @param organization_id
		 * @param expected response
		 * @param expectedStatusCode
		 * @param ExpectedStatusCode
		 * @param test
		 */
		public void getDestinationTypesForSpecifiedOrgWithCheck(String token, String org_id, ArrayList<HashMap<String, Object>> expected_response,
																int expectedStatusCode,SpogMessageCode ExpectedErrorMessage, ExtentTest test
																) {
			
			test.log(LogStatus.INFO, "Call rest API to get the destination types in a specified organization");
			Response response = spogDestinationInvoker.getDestinationTypesForSpecifiedOrganization(token, org_id, test);
			spogServer.checkResponseStatus(response, expectedStatusCode);
			
			if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
				ArrayList<HashMap<String, Object>> actual_response = response.then().extract().path("data");
				if(expected_response.size() == actual_response.size()) {
						
					for (int i = 0; i < actual_response.size() ; i++) {
						for (int j = 0; j < expected_response.size(); j++) {
							spogServer.assertResponseItem(actual_response.get(i).get("amount"),expected_response.get(i).get("amount"),test);
							spogServer.assertResponseItem(actual_response.get(i).get("destination_type"), expected_response.get(i).get("destination_type"), test);
						}
					}
					
				}else {
					
					test.log(LogStatus.FAIL, "Actual destination types size is: "+actual_response.size() +", expected destination types size is: "+expected_response.size());
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
		 * This method will associate the given datstore id to the given destination id
		 * 
		 * @author Kiran.Sripada
		 * @param destination_id
		 * @param datastore_id
		 * @param validToken
		 * @param test
		 * return response
		 */
		public Response assigndatastore(String destination_id, String datastore_id, String validToken, ExtentTest test) {
			
			test.log(LogStatus.INFO, "Start associating the datastore to the destination");
			HashMap<String,Object> datastoreInfo = jp.composedatastoreInfo(datastore_id);
			Response response = spogDestinationInvoker.assigndatastore(destination_id, datastoreInfo, validToken, test);
			//spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
			return response;
		}
		
		public void checkassigneddatastore(Response response, HashMap<String,Object> expectedresponse, int expectedStatusCode, SpogMessageCode expectedErroMessage, ExtentTest test) 
		{
			test.log(LogStatus.INFO, "Start the response validation");
			spogServer.checkResponseStatus(response, expectedStatusCode, test);
			
			if(expectedStatusCode==SpogConstants.SUCCESS_GET_PUT_DELETE) {

				HashMap<String, Object>testResponse = response.then().extract().path("data.cloud_hybrid_store");
				if(testResponse.containsKey("concurrent_active_node"))
				{
					assertTrue(true, "Concurrent active nodes: "+testResponse.get("concurrent_active_node").toString());
				}else
				{
					assertTrue(false, "concurrent_active_node not available");
				}
				
							
				response.then()
				.body("data.destination_id",equalTo(expectedresponse.get("destination_id")))
				.body("data.destination_name", equalTo(expectedresponse.get("destination_name")))
				.body("data.destination_type", equalTo(expectedresponse.get("destination_type")))
				.body("data.datastore_id", equalTo(expectedresponse.get("datastore_id")))
				.body("data.organization_id", equalTo(expectedresponse.get("organization_id")));
//				.body("data.site_id", equalTo(expectedresponse.get("site_id")));
				
				HashMap<String, Object> actualCHstore = response.then().extract().path("data.cloud_hybrid_store");
				HashMap<String, Object> expectedCHstore = (HashMap<String, Object>) expectedresponse.get("cloud_hybrid_store");
				
				spogServer.assertResponseItem(expectedCHstore.get("storage_capacity").toString(), actualCHstore.get("storage_capacity").toString(), test);
				spogServer.assertResponseItem(expectedCHstore.get("is_compressed").toString(), actualCHstore.get("is_compressed").toString(), test);
				spogServer.assertResponseItem(expectedCHstore.get("is_deduplicated").toString(), actualCHstore.get("is_deduplicated").toString(), test);
				spogServer.assertResponseItem(expectedCHstore.get("concurrent_active_node").toString(), actualCHstore.get("concurrent_active_node").toString(), test);
				spogServer.assertResponseItem(expectedCHstore.get("compression").toString(), actualCHstore.get("compression").toString(), test);
				spogServer.assertResponseItem(expectedCHstore.get("block_size").toString(), actualCHstore.get("block_size").toString(), test);
				
				HashMap<String, Object> expectedCHstore_datastore = (HashMap<String, Object>) expectedCHstore.get("datastore");
				HashMap<String, Object> actualCHstore_datastore = (HashMap<String, Object>) actualCHstore.get("datastore");
				spogServer.assertResponseItem(expectedCHstore_datastore.get("datastore_id").toString(), actualCHstore_datastore.get("datastore_id").toString(), test);
				spogServer.assertResponseItem(expectedCHstore_datastore.get("datastore_name").toString(), actualCHstore_datastore.get("datastore_name").toString(), test);
				
				test.log(LogStatus.PASS, "Validation passed for assign datastore");
				assertTrue("Validataion passed",true);
			}else {
				String code = expectedErroMessage.getCodeString();
		    	String message = expectedErroMessage.getStatus();
		    	if(code.contains("00C0000B")) {
		    		message = message.replace("{0}", expectedresponse.get("destination_id").toString());
		    	}
		    	spogServer.checkErrorCode(response,code);
		    	test.log(LogStatus.INFO, "The error code matched with the expected "+code);
		    	spogServer.checkErrorMessage(response,message);
		    	test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
			}
		}
		
		
		/*
		 * This method will unassign the given datstore id from the given destination id
		 * 
		 * @author Kiran.Sripada
		 * @param destination_id
		 * @param datastore_id
		 * @param validToken
		 * @param test
		 * return response
		 */
		public Response unassigndatastore(String destination_id, String validToken, ExtentTest test) {
			
			test.log(LogStatus.INFO, "Start associating the datastore to the destination");
			
			Response response = spogDestinationInvoker.unassigndatastore(destination_id, validToken, test);
			
			return response;
		}
		
		public void checkunassigneddatastore(Response response, String destination_id, int expectedStatusCode, SpogMessageCode expectedErroMessage, ExtentTest test) 
		{
			test.log(LogStatus.INFO, "Start the response validation");
			spogServer.checkResponseStatus(response, expectedStatusCode, test);
			if(expectedStatusCode==SpogConstants.SUCCESS_GET_PUT_DELETE) {
				test.log(LogStatus.PASS, "The datastore is unassigned from the destination");
			}else {
				String code = expectedErroMessage.getCodeString();
		    	String message = expectedErroMessage.getStatus();
		    	if(code.contains("00C00001")) {
		    		message = message.replace("{0}", destination_id);
		    	}
		    	spogServer.checkErrorCode(response,code);
		    	test.log(LogStatus.INFO, "The error code matched with the expected "+code);
		    	spogServer.checkErrorMessage(response,message);
		    	test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
			}
		}
		

		/**
		 * generate files download links for the specified recovery point on Destination.
		 * @author yuefen.liu
		 * @param destination_id
		 * @param source_id
		 * @param recoverypoint_id
		 * return download_url
		 */
		public Response generateFilesDownloadLinkFromDestinationForSpecifiedRecoveryPoint(String destination_id, String source_id, String recoverypoint_id) {
			
			Response response = spogDestinationInvoker.generateFilesDownloadLinkFromDestinationForSpecifiedRecoveryPoint(destination_id, source_id, recoverypoint_id);
		
			return response;
			
		}
		/**
		 * @author Ramya.Nagepalli
		 * @param token
		 * @param organization_id
		 * @param destination_id
		 * @param TimeStamp
		 * @param primary_usage
		 * @param snapshot_usage
		 * @param expectedStatusCode
		 * @param ExpectedErrorMessage
		 * @param test
		 */

		public Response updateDestinationUsage(String token,String organization_id, String destination_id,String TimeStamp, String primary_usage,
				String snapshot_usage,int expectedStatusCode,SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
			// TODO Auto-generated method stub
			ArrayList<Map> composedInfo=new ArrayList<Map>();

			Map<String, Object> destination_details=jp.composeDestinationUsageDetails(destination_id,TimeStamp,primary_usage,snapshot_usage);

			composedInfo.add(destination_details);

			Response response=spogDestinationInvoker.updateDestinationUsage(token,organization_id,composedInfo,test);
			
			spogServer.checkResponseStatus(response, expectedStatusCode);
			
			if(expectedStatusCode==SpogConstants.SUCCESS_GET_PUT_DELETE)
			{
				test.log(LogStatus.PASS, "Successfully updated the destination usage for the organization ");
			}
			else
			{
				String code = ExpectedErrorMessage.getCodeString();
		    	String message = ExpectedErrorMessage.getStatus();
		    	if(code.contains("00C00001")) {
		    		message = message.replace("{0}", destination_id);
		    	}
		    	if(code.contains("0x0030000A")) {
		    		message = message.replace("{0}", organization_id);
		    	}
		    	spogServer.checkErrorCode(response,code);
		    	test.log(LogStatus.INFO, "The error code matched with the expected "+code);
		    	spogServer.checkErrorMessage(response,message);
		    	test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
			
			}

			return response;
		}
		
		/* Create hybrid destination
		 * 
		 * @author Rakesh.Chalamala
		 */
		public String createHybridDestination(String token, String orgId, String datacenterId, String destinationType, String destination_name, String destinationStatus, ExtentTest test) {
			
			HashMap<String, Object> DestinationInfo = new HashMap<>();
			
			DestinationInfo.put("organization_id", orgId);
			DestinationInfo.put("datacenter_id", datacenterId);
			DestinationInfo.put("destination_type", destinationType);
			DestinationInfo.put("destination_name", destination_name);
			DestinationInfo.put("destination_status", destinationStatus);
			
			Response response = spogDestinationInvoker.createDestination(token, DestinationInfo, test);
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
			
			return response.then().extract().path("data.destination_id");
		}
		
		public String createCdDestination(String token, String orgId, String datacenterId, String destinationType, String destinationName, String destinationStatus,
									String volumeType, String retentionId, String retentionName,HashMap<String, String> retention, int expectedStatusCode, ExtentTest test
									) {
			HashMap<String, Object> DestinationInfo = new HashMap<>();
			
			DestinationInfo.put("organization_id", orgId);
			DestinationInfo.put("datacenter_id", datacenterId);
			DestinationInfo.put("destination_type", destinationType);
			DestinationInfo.put("destination_name", destinationName);
			DestinationInfo.put("destination_status", destinationStatus);
			
			HashMap<String, Object> volumeInfo = new HashMap<>();
			volumeInfo.put("volume_type", volumeType);
			volumeInfo.put("retention_id", retentionId);
			volumeInfo.put("retention_name", retentionName);
			volumeInfo.put("retention", retention);
			
			DestinationInfo.put("cloud_direct_volume", volumeInfo);
			
			
			Response response = spogDestinationInvoker.createDestination(token, DestinationInfo, test);
			spogServer.checkResponseStatus(response, expectedStatusCode, test);
			
			return response.then().extract().path("data.destination_id");			
		}
		
		public Response  getRecoveryPointsByDestinationId(String destinationId, String token){
			
			return spogDestinationInvoker.getRecoveryPointsByDestinationId(destinationId, token);
		}

		public void checkGetRecoveryPointsByDestinationId(){
			
		}
		//enhancement available actions
		public void checkGetDestinations (Response response,ArrayList<String> available_actions, int expectedStatusCode,
				ArrayList<HashMap<String, Object>> DestinationsInfo, int curr_page, 
				int page_size,String FilterStr, String SortStr, SpogMessageCode Info,
				ExtentTest test) {
			// TODO Auto-generated method stub
			@SuppressWarnings("unused")
			int total_page = 0, return_size = 0, total_size = 0;
			if (curr_page == 0||curr_page==-1) {
				curr_page = 1;
			}
			if (page_size == 0 ||page_size==-1|| page_size > SpogConstants.MAX_PAGE_SIZE) {
				page_size = 20;
			}

			ArrayList<HashMap<String,Object>> Destinations = new ArrayList<HashMap<String,Object>>();
			if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE&&DestinationsInfo!=null&&!DestinationsInfo.isEmpty() ){
				Destinations = response.then().extract().path("data");		
				return_size = Destinations.size();
				//get The total Size for the pages
				total_size = DestinationsInfo.size();
				test.log(LogStatus.INFO, "The actual total size is " + total_size);
				errorHandle.printInfoMessageInDebugFile("expect status code is " + expectedStatusCode);
				spogServer.checkResponseStatus(response, expectedStatusCode);
			} else {

				errorHandle.printInfoMessageInDebugFile("expect status code is " + expectedStatusCode);
				spogServer.checkResponseStatus(response, expectedStatusCode);
				test.log(LogStatus.PASS,"The validation of the resposne is successfull");
				return ;

			}
			if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE
					) {
				test.log(LogStatus.INFO, "check response For the Logs ");

					if (((SortStr == null) || (SortStr.equals("")))
							&& ((FilterStr == null) || (FilterStr.equals("")))) {

						// If we Don't mention any Sorting the default sorting is descending order 
						//validating the response for Destinations	
						int j = 0;
						if (page_size == 20) {
							j = Destinations.size() - 1;
						} else {
							j = DestinationsInfo.size() - 1;
							if (curr_page != 1) {
								j = (Destinations.size() - 1) - (curr_page - 1) * page_size;
							}
						}						
						//response validation --some changes are there 
						for (int i = 0; i < Destinations.size() && j >= 0; i++, j--) {
							HashMap<String,Object> actual_Destinations_data,expected_Destinations_data=new HashMap<String,Object>();
							actual_Destinations_data=Destinations.get(i);expected_Destinations_data=DestinationsInfo.get(j);

							//validate the response for the Destinations
							String act=actual_Destinations_data.get("destination_id").toString();
							String exp=expected_Destinations_data.get("destination_id").toString();
							if(act.equals(exp))
							{
							checkForDestinationsData(actual_Destinations_data,expected_Destinations_data,available_actions,test);	
							}						
						}
					}

					// SortStr
					//Supports URI of type :http://xiang-gitlab:9080/destinations/?organization_id=000be072-a303-4646-bb2d-cd6a273d6ad7|56a13ef1-4b51-4aa9-86e6-09151f244e28&sort=create_ts
					//2.http://xiang-gitlab:9080/destinations?sort=create_ts
					//3.http://xiang-gitlab:9080/destinations/?organization_id=000be072-a303-4646-bb2d-cd6a273d6ad7|56a13ef1-4b51-4aa9-86e6-09151f244e28&page=1&page_size=10&sort=create_ts

					else if ( //For sorting only and no filtering but includes pagination
							(((SortStr != null) || (!SortStr.equals(""))) && SortStr.contains("create_ts")
									&& ((FilterStr == null) || (FilterStr.equals(""))))

							//For Sorting and Filtering both 

							|| (((SortStr != null)||(!SortStr.equals(""))) && SortStr.contains("create_ts")
									&& ((FilterStr != null) || (!FilterStr.equals(""))))
							) {

						// validating the response for all the users who are sorted based on response in ascending
						// order and descending order
						if (SortStr.contains("asc")) {
							int j = 0;
							if (curr_page != 1) {
								j = (curr_page - 1) * page_size;
							}	

							//validate the response --some changes are there 
							for(int i=0;i<Destinations.size();i++,j++){
								HashMap<String,Object> actual_Destinations_data,expected_Destinations_data=new HashMap<String,Object>();
								actual_Destinations_data=Destinations.get(i);expected_Destinations_data=DestinationsInfo.get(j);

								//validate the response for the Destinations
								String act=actual_Destinations_data.get("destination_id").toString();
								String exp=expected_Destinations_data.get("destination_id").toString();
								if(act.equals(exp))
								{
								checkForDestinationsData(actual_Destinations_data,expected_Destinations_data,available_actions,test);	
								}		
							}
						} else if(SortStr.contains("desc")||(FilterStr.contains(">"))||(FilterStr.contains("<"))){
							int j = 0;
							if (page_size == 20) {
								j = Destinations.size() - 1;
							} else {
								j = DestinationsInfo.size() - 1;
								if (curr_page != 1) {
									j = (Destinations.size() - 1) - (curr_page - 1) * page_size;
								}
							}						
							//response validation --some changes are there 
							for (int i = 0; i < Destinations.size() && j >= 0; i++, j--) {
								HashMap<String,Object> actual_Destinations_data,expected_Destinations_data=new HashMap<String,Object>();
								actual_Destinations_data=Destinations.get(i);
								expected_Destinations_data=	DestinationsInfo.get(j);

								//validate the response for the Destinations
								String act=actual_Destinations_data.get("destination_id").toString();
								String exp=expected_Destinations_data.get("destination_id").toString();
								if(act.equals(exp))
								{
								checkForDestinationsData(actual_Destinations_data,expected_Destinations_data,available_actions,test);	
								}
							}
						} 
					}//Only filtering and no sorting (For now supports only for the organization id )
					else if(((SortStr == null) || (SortStr.equals("")))&&((FilterStr != null) || (!FilterStr.equals("")))){

						int j = 0;
						if (page_size == 20) {
							j = Destinations.size() - 1;
						} else {
							j = DestinationsInfo.size() - 1;
							if (curr_page != 1) {
								j = (Destinations.size() - 1) - (curr_page - 1) * page_size;
							}
						}						
						//response validation --some changes are there 
						for (int i = 0; i < Destinations.size() && j >= 0; i++, j--) {
							HashMap<String,Object> actual_Destinations_data=Destinations.get(i),expected_Destinations_data=Destinations.get(j);

							//validate the response for the Destinations
							checkForDestinationsData(actual_Destinations_data,expected_Destinations_data,available_actions,test);						
						}
					}

					if(DestinationsInfo.size() == Destinations.size())
					{
					// check the response validation for pages,page_size,total_size
					spogServer.validateResponseForPages(curr_page, page_size, response, total_size, test);
					}
				
			}
			else  {
				String expectedErrorMessage="",expectedErrorCode="";
				if(Info.getStatus()!="0010000"){
					expectedErrorMessage=Info.getStatus();
					if(expectedErrorMessage.contains("{0}")){
						System.out.println(expectedErrorMessage);
					}
					expectedErrorCode=Info.getCodeString();
				}
				spogServer.checkErrorMessage(response, expectedErrorMessage);
				test.log(LogStatus.PASS, "The value of the message is "+expectedErrorMessage);
				spogServer.checkErrorCode(response, expectedErrorCode);
				test.log(LogStatus.PASS, "The value of the code  generated  is "+expectedErrorCode);
				test.log(LogStatus.INFO,"The value of the  response generated actually is :"+response.getStatusCode());
			}
		}
		private void checkForDestinationsData(HashMap<String, Object> actual_Destinations_data,
				HashMap<String, Object> expected_Destinations_data, ArrayList<String> available_actions,
				ExtentTest test) {
			// TODO Auto-generated method stub
			test.log(LogStatus.INFO,"validating the destinaiton_id");
			spogServer.assertResponseItem(actual_Destinations_data.get("destination_id"),expected_Destinations_data.get("destination_id"),test);
			test.log(LogStatus.INFO,"validating the destinaiton_name");
			//spogServer.assertResponseItem(actual_Destinations_data.get("destination_name"), expected_Destinations_data.get("destination_name"),test);
			test.log(LogStatus.INFO,"validating the destination_type");
			spogServer.assertResponseItem(actual_Destinations_data.get("destination_type"), expected_Destinations_data.get("destination_type"),test);
			test.log(LogStatus.INFO,"validating the organization_id");
			spogServer.assertResponseItem(actual_Destinations_data.get("organization_id"), expected_Destinations_data.get("organization_id"),test);
			
			//validation of the response for the cloud_direct_volume
			if(actual_Destinations_data.get("destination_type").equals("cloud_direct_volume")) {
				test.log(LogStatus.INFO,"validating the datacenters data");
			
				HashMap<String,Object> cloud_direct=(HashMap<String, Object>) actual_Destinations_data.get("cloud_direct_volume");
				HashMap<String,Object> cloud_volume=(HashMap<String, Object>) expected_Destinations_data.get("cloud_direct_volume");


				//validation of the information inside the cloud_direct_volume   
				test.log(LogStatus.INFO,"validating the response for cloud_direct_volume");
				//	spogServer.assertResponseItem(cloud_direct.get("cloud_direct_volume_name"),cloud_volume.get("cloud_direct_volume_name"),test);	
				spogServer.assertResponseItem(cloud_direct.get("volume_type").toString(),cloud_volume.get("volume_type").toString(),test);
				spogServer.assertResponseItem(cloud_direct.get("hostname").toString(),cloud_volume.get("hostname").toString(),test);
				spogServer.assertResponseItem(cloud_direct.get("retention_id").toString(),cloud_volume.get("retention_id").toString(),test);		

				test.log(LogStatus.INFO, "validating the response for total_usage");
				assertNotNull(cloud_direct.get("total_usage"));
				test.log(LogStatus.INFO, "validating the response for primary usage");
				assertNotNull(cloud_direct.get("primary usage"));
				test.log(LogStatus.INFO, "validating the response for snapshot usage");
				assertNotNull(cloud_direct.get("snapshot usage"));
				//validation For The Response for cloud_direct_retention_dictionary details
				//HashMap's for the actual and Expected retention values 

				test.log(LogStatus.INFO,"validating the retention values for cloud_direct volumes");
				HashMap<String,Object> retention,Retention= new HashMap<String,Object>();
				retention=(HashMap<String, Object>) cloud_direct.get("retention");
				if(!(cloud_direct.get("retention_id").equals("custom"))){
					Retention=getRetention();
					Iterator<Entry<String, Object>> entries = Retention.entrySet().iterator();   						
					while(entries.hasNext()){
						Entry<String,Object> entry= entries.next();
						if(cloud_direct.get("retention_id").equals(entry.getKey())){
							HashMap<String,String> Retention1 =new HashMap<String,String>();
							Retention1=(HashMap<String, String>) entry.getValue();
							spogServer.assertResponseItem(retention.get("age_hours_max"),Retention1.get("age_hours_max"),test);
							spogServer.assertResponseItem(retention.get("age_four_hours_max"),Retention1.get("age_four_hours_max"),test);
							spogServer.assertResponseItem(retention.get("age_days_max"),Retention1.get("age_days_max"),test);
							spogServer.assertResponseItem(retention.get("age_weeks_max "),Retention1.get("age_weeks_max "),test);
							spogServer.assertResponseItem(retention.get("age_months_max"),Retention1.get("age_months_max"),test);
							spogServer.assertResponseItem(retention.get("age_years_max"),Retention1.get("age_years_max"),test);
							break;
						}
					}
				}
				//Retention data for the custom retention data dictionary 
				else {
					Retention=(HashMap<String, Object>) cloud_volume.get("retention");
					test.log(LogStatus.INFO,"validating the retention_custom for cloud_direct_volume");
					spogServer.assertResponseItem(retention.get("age_hours_max"),Retention.get("age_hours_max"),test);
					spogServer.assertResponseItem(retention.get("age_four_hours_max"),Retention.get("age_four_hours_max"),test);
					spogServer.assertResponseItem(retention.get("age_days_max"),Retention.get("age_days_max"),test);
					spogServer.assertResponseItem(retention.get("age_weeks_max "),Retention.get("age_weeks_max "),test);
					spogServer.assertResponseItem(retention.get("age_months_max"),Retention.get("age_months_max"),test);
					spogServer.assertResponseItem(retention.get("age_years_max"),Retention.get("age_years_max"),test);
				}
			}
			//Related to cloud_dedupe data Store 
			else if(actual_Destinations_data.get("destination_type").equals("cloud_hybrid_store")){
				test.log(LogStatus.INFO,"validating for the cloud_dedupe_data_store");
				HashMap<String,Object> cloud_dedupe=(HashMap<String, Object>) actual_Destinations_data.get("cloud_hybrid_store");
				HashMap<String,Object> cloud_volume=(HashMap<String, Object>) expected_Destinations_data.get("cloud_hybrid_store");

					}

			test.log(LogStatus.INFO,"Validating the response for last_job");
			ArrayList<HashMap<String, Object>> actual_last_job= (ArrayList<HashMap<String, Object>>) actual_Destinations_data.get("last_job");
			HashMap<String,Object> actual_last_job_data=actual_last_job.get(0);
			ArrayList<HashMap<String,Object>>expected_last_job_data=(ArrayList<HashMap<String, Object>>)  expected_Destinations_data.get("last_job");
			HashMap<String,Object>expected_last_job=expected_last_job_data.get(0);

			test.log(LogStatus.INFO,"validating the response for type,start_time_ts,status,end_time_ts,percentage_complete");
			spogServer.assertResponseItem(actual_last_job_data.get("type"),expected_last_job.get("job_type"),test);
			spogServer.assertResponseItem(actual_last_job_data.get("start_time_ts").toString(),expected_last_job.get("start_time_ts").toString(),test);			
			spogServer.assertResponseItem(actual_last_job_data.get("status"),expected_last_job.get("job_status"),test);
			spogServer.assertResponseItem(actual_last_job_data.get("end_time_ts").toString(),expected_last_job.get("end_time_ts").toString(),test);
			spogServer.assertResponseItem(actual_last_job_data.get("percent_complete").toString(),expected_last_job.get("percent_complete").toString(),test);

		}

		/**
		 * @author Rakesh.Chalamala
		 * 
		 * @param token
		 * @param orgID
		 * @param test
		 */
		public void recycleCloudVolumesAndDestroyOrg(String token, String orgID) {

			spogDestinationInvoker.setToken(token);
			String des_id,org_id=null;
			
			System.out.println("Get all CD destinations under the org with id:"+orgID);
			Response response=spogDestinationInvoker.getDestinations(token,"destination_type=cloud_direct_volume&organization_id="+orgID);
			int  total_page=response.then().extract().path("pagination.total_page");
			int  total_size=response.then().extract().path("pagination.total_size");
			for(int page=1;page<=total_page;page++){
				if(page>1){
					response=spogDestinationInvoker.getDestinations(token,"destination_type=cloud_direct_volume&organization_id="+orgID);
				}
				for(int i=0;i<total_size;i++){		
					ArrayList<HashMap<String, Object>> policies = response.then().extract().path("data["+i+"].policies");
					if (policies != null && !policies.isEmpty()) {
						for (int j = 0; j < policies.size(); j++) {
							//Delete the policies to which destination assigned before recycling volume					
							for (int k = 0; k < 5; k++) {
								
								System.out.println("Delete the policy with id:"+policies.get(j).get("policy_id") +" which is mapped to the destination");
								Response response1 = policy4SpogServer.deletePolicybyPolicyId(token, policies.get(j).get("policy_id").toString());
								
								if (response1.getStatusCode() != SpogConstants.SUCCESS_GET_PUT_DELETE) {
									try {
										Thread.sleep(2000); //wait for the policy to update from deploying state and try again
									} catch (InterruptedException e1) {
										e1.printStackTrace();
									}
								} else {
									break;
								}
							}
							
						}
					}					
					org_id=response.then().extract().path("data["+i+"].organization_id");
					if(org_id!=null){
						des_id=response.then().extract().path("data["+i+"].destination_id");		
						//Recycle the volumes
						System.out.println("Recycle destination with id:"+des_id);
						spogDestinationInvoker.recycleCDVolume(des_id);
						errorHandle.printInfoMessageInDebugFile("Recycle destination with id:"+des_id);
					}
				}
			}
			if (org_id != null) {
				
				//Delete enrolled orgs from CD
				System.out.println("Add organization with id:"+ orgID +" to CD deletion queue");
				spogDestinationInvoker.addToCDOrgDeletionQueue(orgID);
				errorHandle.printInfoMessageInDebugFile("Add organization with id:"+ orgID +" to CD deletion queue");

				System.out.println("Delete organization with id:"+ orgID +" from CD ");
				spogDestinationInvoker.deleteCDOrganization(orgID);
				errorHandle.printInfoMessageInDebugFile("Delete organization with id:"+ orgID +" from CD ");
			}
			
			//Delete from CC
			org4SPOGServer.setToken(token);
			System.out.println("Destroy organization with id:"+orgID);
			org4SPOGServer.destroyOrganizationWithoutCheck(orgID);
			errorHandle.printInfoMessageInDebugFile("Destroy organization with id:"+orgID);
			
		}
		
		public void recycleCDVolume(String volume_id) {
			Response response = spogDestinationInvoker.recycleCDVolume(volume_id);
			response.then().body("status", equalTo("SUCCESS"));
		}

		/**
		 * createDestinationFilter
		 * 
		 * @author Ramya.Nagepalli
		 * @param org_id
		 * @param userID
		 * @param filterName
		 * @param destinationName
		 * @param policyID
		 * @param destinationType
		 * @param isDefault
		 * @param test
		 * @return
		 */
		public Response createDestinationFilter(String org_id, String userID, String filterName, String destinationName,
				String policyID, String destinationType, String isDefault, ExtentTest test) {

			Map<String, Object> destinationFilterInfo = jp.composeDestinationFilterInfo(org_id, filterName, destinationName,
					policyID, destinationType, isDefault);
			HashMap<String, Object> filter_info = new HashMap<String, Object>();
			if (!destinationFilterInfo.isEmpty()) {
				filter_info = jp.composeFilterInfo(filterType.destination_filter.toString(),
						filterName, userID, "none",
						Boolean.valueOf(isDefault), (HashMap<String,Object>)destinationFilterInfo);
			}
			Response response = spogServer.createFilters(spogDestinationInvoker.getToken(), filter_info, "", test);
			/*Response response = spogDestinationInvoker.createDestinationFilter(userID, destinationFilterInfo);*/
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);

			return response;
		}


		/**
		 * getspecifiedDestinationFilterForLoggedInUserwithCheck
		 * 
		 * @author Ramya.Nagepalli
		 * @param additionalURL
		 * @param filter_Id
		 * @param token
		 * @param expectedstatuscode
		 * @param ExpectedErrorMessage
		 * @param test
		 * @return
		 */
		public Response getspecifiedDestinationFilterForLoggedInUserwithCheck(String additionalURL, String filter_Id,
				String token, int expectedstatuscode, SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

			Response response = spogServer.getFiltersById(token, filter_Id, filterType.destination_filter.toString(), "none", "none", test);
			/*Response response = spogDestinationInvoker.getspecifiedDestinationFilterForLoggedInUser(additionalURL,
					filter_Id, token, test);*/
			test.log(LogStatus.INFO, "Check the response status");
			spogServer.checkResponseStatus(response, expectedstatuscode);

			return response;
		}

		public HashMap<String, Object> composeCdDestinationInfo(String orgId, String datacenterId, String destinationType,
				String destinationName, String destinationStatus, String volumeType, String retentionId,
				String retentionName, HashMap<String, String> retention) {

			HashMap<String, Object> DestinationInfo = new HashMap<>();

			DestinationInfo.put("organization_id", orgId);
			DestinationInfo.put("datacenter_id", datacenterId);
			DestinationInfo.put("destination_type", destinationType);
			DestinationInfo.put("destination_name", destinationName);
			DestinationInfo.put("destination_status", destinationStatus);

			HashMap<String, Object> volumeInfo = new HashMap<>();
			volumeInfo.put("volume_type", volumeType);
			volumeInfo.put("retention_id", retentionId);
			volumeInfo.put("retention_name", retentionName);
			volumeInfo.put("retention", retention);

			DestinationInfo.put("cloud_direct_volume", volumeInfo);

			return DestinationInfo;
		}
		
		public Response createCdDestinationWithCheck(String token, String orgId, String datacenterId,
				String destinationType, String destinationName, String destinationStatus, String volumeType,
				String retentionId, String retentionName, HashMap<String, String> retention, int expectedStatusCode,
				SpogMessageCode expectedErrorMessage, ExtentTest test) {

			HashMap<String, Object> DestinationInfo = composeCdDestinationInfo(orgId, datacenterId, destinationType,
					destinationName, destinationStatus, volumeType, retentionId, retentionName, retention);
			Response response = spogDestinationInvoker.createDestination(token, DestinationInfo, test);
			spogServer.checkResponseStatus(response, expectedStatusCode, test);

			if (expectedStatusCode == SpogConstants.SUCCESS_POST) {
				HashMap<String, Object> actualResponse = response.then().extract().path("data");
				validateCdDestinationInfo(actualResponse, DestinationInfo, test);
			} else {
				String code = expectedErrorMessage.getCodeString();
				String message = expectedErrorMessage.getStatus();
				spogServer.checkErrorCode(response, code);
				test.log(LogStatus.INFO, "The error code matched with the expected " + code);
				spogServer.checkErrorMessage(response, message);
				test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
			}

			return response;
		}
		public void validateCdDestinationInfo(HashMap<String, Object> actualData, HashMap<String, Object> expectedData,
				ExtentTest test) {

			assertNotNull(actualData.get("destination_id"), "Destination id is not null");
			spogServer.assertResponseItem(actualData.get("destination_name"), expectedData.get("destination_name"), test);
			spogServer.assertResponseItem(actualData.get("organization_id"),expectedData.get("organization_id"), test);
			/*spogServer.assertResponseItem(((HashMap<String, Object>) actualData.get("organization")).get("organization_id"), expectedData.get("organization_id"), test);*/
			/*removed datacenterid tag
			 * spogServer.assertResponseItem(actualData.get("datacenter_id"), expectedData.get("datacenter_id"), test);
			*/spogServer.assertResponseItem(actualData.get("destination_type"), expectedData.get("destination_type"), test);
			spogServer.assertResponseItem(actualData.get("destination_status"), expectedData.get("destination_status"), test);
			
			HashMap<String, Object> actualVolumeInfo = (HashMap<String, Object>) actualData.get("cloud_direct_volume");
			HashMap<String, Object> expectedVolumeInfo = (HashMap<String, Object>) expectedData.get("cloud_direct_volume");
				
			spogServer.assertResponseItem(actualVolumeInfo.get("volume_type"), expectedVolumeInfo.get("volume_type"), test);
			spogServer.assertResponseItem(actualVolumeInfo.get("retention_id"), expectedVolumeInfo.get("retention_id"), test);
			spogServer.assertResponseItem(actualVolumeInfo.get("retention_name"), expectedVolumeInfo.get("retention_name"), test);
		
		}
		
	  	/**
		 * @author Rakesh.Chalamala
		 * 
		 * @param token
		 * @param destination_Id
		 * @param destinationInfo
		 * @param expectedStatusCode
		 * @return
		 */
		public Response updateDestinationById(String token, String destination_Id, HashMap<String, Object> destinationInfo,
				int expectedStatusCode) {

			Response response = spogDestinationInvoker.updatedestinationbydestination_Id(destination_Id, destinationInfo,
					token);
			spogServer.checkResponseStatus(response, expectedStatusCode);

			return response;
		}
}

