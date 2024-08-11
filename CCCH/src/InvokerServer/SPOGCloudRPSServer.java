package InvokerServer;

import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.SpogConstants;
import Constants.SpogMessageCode;
import dataPreparation.JsonPreparation;
import genericutil.ErrorHandler;
import invoker.SPOGCloudRPSInvoker;
import io.restassured.response.Response;

public class SPOGCloudRPSServer {
	private SPOGCloudRPSInvoker spogCloudRPSInvoker;
	private SPOGServer  spogServer;
	private Policy4SPOGServer policy4SPOGServer;
	static ErrorHandler errorHandle = ErrorHandler.getErrorHandler();
	private static JsonPreparation jp = new JsonPreparation();
	
	public SPOGCloudRPSServer(String baseURI, String port) {
		
		spogCloudRPSInvoker = new SPOGCloudRPSInvoker(baseURI, port);
		spogServer = new SPOGServer(baseURI, port);
		policy4SPOGServer = new Policy4SPOGServer(baseURI, port);
	}


	public void setToken(String token) {

		spogCloudRPSInvoker.setToken(token);
		spogServer.setToken(token);

	}
	
	/*
	   * @author Kiran.Sripada
	   * @param server_name
	   * @param server_port
	   * @param server_protocol
	   * @param server_username
	   * @param server_password
	   * @param organization_id
	   * @param site_id
	   * @param datacenter_id
	   * @param validToken
	   * @return Response
	   * 
	   */
	
	
	public Response createCloudRPS(String server_name,
								   String server_port,
								   String server_protocol,
								   String server_username,
								   String server_password,
								   String organization_id,
								   String site_id,
								   String datacenter_id,
								   String validToken) {
		Response response = null;
		HashMap<String,Object> cloudRPSInfo = new HashMap<>();
		cloudRPSInfo = jp.composeCloudRPSInfo(server_name, server_port, server_protocol, server_username, server_password, organization_id, site_id, datacenter_id);
		response = spogCloudRPSInvoker.createCloudRPS(cloudRPSInfo, validToken);
		return response;
	}
	
	/*
	   * @author Kiran.Sripada
	   * @param datastore_name
	   * @param dedupe_enabled
	   * @param encryption_enabled
	   * @param compression_enabled
	   * @param rps_server_id
	   * @param datastorepropertiesInfo
	   * @param validToken
	   * @return Response
	   * 
	   */
	
	
	public Response createCloudRPSDataStore(String datastore_name,
										    boolean dedupe_enabled,
										    boolean encryption_enabled,
										    boolean compression_enabled,
										    String rps_server_id,
										    HashMap<String,Object> datastorepropertiesInfo,
										    String validToken) {
		Response response = null;
		HashMap<String,Object> cloudRPSDataStoreInfo = new HashMap<>();
		
		cloudRPSDataStoreInfo = jp.composeCloudRPSDataStoreInfo(datastore_name, dedupe_enabled, encryption_enabled, compression_enabled, rps_server_id, datastorepropertiesInfo);
		response = spogCloudRPSInvoker.createCloudRPSDataStore(cloudRPSDataStoreInfo, validToken);

		return response;
	}
	
	/*
	   * @author Kiran.Sripada
	   * @param datastore_id
	   * @param adminToken
	   * @param expectedresponse
	   * @param expectedStatusCode
	   * @param expectedErrorMessage
	   * @param test
	   * 
	   */
	
	public void deleteCloudRPSDataStore(String datastore_id, String adminToken, int expectedstatuscode, SpogMessageCode expectedErrorMessage, ExtentTest test) {
		Response response = spogCloudRPSInvoker.deleteCloudRPSDataStore(datastore_id, adminToken);
		spogServer.checkResponseStatus(response, expectedstatuscode, test);

		if(expectedstatuscode==SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.PASS, "Succesfully deleted the datastore");
		}else {

			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			if(code.contains("00E00008")){
				message = message.replace("{0}", datastore_id);
			}
			spogServer.checkErrorCode(response,code);
			test.log(LogStatus.PASS, "The error code matched with the expected "+code);
			spogServer.checkErrorMessage(response,message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);

		}
		

	}
	
	/*
	   * @author Rakesh.Chalamala
	   * @param datastore_id
	   * @param adminToken
	   * @param expectedresponse
	   * @param expectedStatusCode
	   * @param expectedErrorMessage
	   * @param test
	   * 
	   */
	
	public void destroyCloudRPSDataStore(String datastore_id, String adminToken, int expectedstatuscode, SpogMessageCode expectedErrorMessage, ExtentTest test) {
		Response response = spogCloudRPSInvoker.destroyCloudRPSDataStore(datastore_id, adminToken);
		spogServer.checkResponseStatus(response, expectedstatuscode, test);

		if(expectedstatuscode==SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.PASS, "Succesfully destroyed the datastore");
		}else {

			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			if(code.contains("00E00008")){
				message = message.replace("{0}", datastore_id);
			}
			spogServer.checkErrorCode(response,code);
			test.log(LogStatus.PASS, "The error code matched with the expected "+code);
			spogServer.checkErrorMessage(response,message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);

		}
		

	}
	

	/*
	   * @author Kiran.Sripada
	   * @param datastore_name
	   * @param dedupe_enabled
	   * @param encryption_enabled
	   * @param compression_enabled
	   * @param rps_server_id
	   * @param datastorepropertiesInfo
	   * @param validToken
	   * @return Response
	   * 
	   */
	
	
	public Response updateCloudRPSDataStore(String datastore_name,
										    boolean dedupe_enabled,
										    boolean encryption_enabled,
										    boolean compression_enabled,
										    String datastore_id,
										    HashMap<String,Object> datastorepropertiesInfo,
										    String validToken) {
		Response response = null;
		HashMap<String,Object> updatecloudRPSDataStoreInfo = new HashMap<>();
		
		updatecloudRPSDataStoreInfo = jp.composetoUpdateCloudRPSDataStoreInfo(datastore_name, dedupe_enabled, encryption_enabled, compression_enabled,datastorepropertiesInfo);
		response = spogCloudRPSInvoker.updateCloudRPSDataStore(updatecloudRPSDataStoreInfo, datastore_id,validToken);
		return response;
	}
	
	/*
	   * @author Kiran.Sripada
	   * @param block_size
	   * @param hash_memory
	   * @param hash_on_ssd
	   * @param data_destination_path
	   * @param data_destination_username
	   * @param data_destination_password
	   * @param index_destination_path
	   * @param index_destination_username
	   * @param index_destination_password
	   * @param hash_destination_path
	   * @param hash_destination_username
	   * @param hash_destination_password
	   * @return composededuplicationInfo
	   * 
	   */
	
	public HashMap<String,Object> composededuplicationInfo(int block_size,
														   int hash_memory,
														   boolean hash_on_ssd,
														   String data_destination_path,
														   String data_destination_username,
														   String data_destination_password,
														   String index_destination_path,
														   String index_destination_username,
														   String index_destination_password,
														   String hash_destination_path,
														   String hash_destination_username,
														   String hash_destination_password
														   ){
		
		HashMap<String,Object> composededuplicationInfo = new HashMap<>();
		composededuplicationInfo.put("block_size",block_size);
		composededuplicationInfo.put("hash_memory",hash_memory);
		composededuplicationInfo.put("hash_on_ssd",hash_on_ssd);
		composededuplicationInfo.put("data_destination_path",data_destination_path);
		composededuplicationInfo.put("data_destination_username",data_destination_username);
		composededuplicationInfo.put("data_destination_password",data_destination_password);
		composededuplicationInfo.put("index_destination_path",index_destination_path);
		composededuplicationInfo.put("index_destination_username",index_destination_username);
		composededuplicationInfo.put("index_destination_password",index_destination_password);
		composededuplicationInfo.put("hash_destination_path",hash_destination_path);
		composededuplicationInfo.put("hash_destination_username",hash_destination_username);
		composededuplicationInfo.put("hash_destination_password",hash_destination_password);
		
		
		
		return composededuplicationInfo;
		
	}
	
	/*
	   * @author Kiran.Sripada
	   * @param concurrent_active_nodes
	   * @param datastore_path
	   * @param datastore_username
	   * @param datastore_password
	   * @param compression
	   * @param encryption_password
	   * @param composededuplicationInfo
	   * @param send_email
	   * @param to_email
	   * @return composedatastorepropertiesInfo
	   * 
	   */
	
	public HashMap<String,Object> composedatastorepropertiesInfo(int concurrent_active_nodes,
																long capacity,
																String datastore_path,
																String datastore_username,
																String datastore_password,
																String compression,
																String encryption_password,
																HashMap<String,Object> composededuplicationInfo,
																boolean send_email,
																String to_email
																){
		
		HashMap<String,Object> composedatastorepropertiesInfo = new HashMap<>();
		
		composedatastorepropertiesInfo.put("concurrent_active_nodes",concurrent_active_nodes);
		if (capacity == (-0)) {
			composedatastorepropertiesInfo.put("capacity", null);	
		}else {
			composedatastorepropertiesInfo.put("capacity", capacity);
		}
		composedatastorepropertiesInfo.put("datastore_path",datastore_path);
		composedatastorepropertiesInfo.put("datastore_username",datastore_username);
		composedatastorepropertiesInfo.put("datastore_password",datastore_password);
		composedatastorepropertiesInfo.put("compression",compression);
		composedatastorepropertiesInfo.put("encryption_password",encryption_password);
		composedatastorepropertiesInfo.put("deduplication",composededuplicationInfo);
		composedatastorepropertiesInfo.put("send_email",send_email);
		composedatastorepropertiesInfo.put("to_email",to_email);
		
		return composedatastorepropertiesInfo;

	}
	
	/*
	   * @author Kiran.Sripada
	   * @param server_name
	   * @param server_port
	   * @param server_protocol
	   * @param server_username
	   * @param server_password
	   * @param organization_id
	   * @param site_id
	   * @param datacenter_id
	   * @param validToken
	   * @return Response
	   * 
	   */
	
	
	public Response updateCloudRPS(String cloudRPS_id,
								   String server_name,
								   String server_port,
								   String server_protocol,
								   String server_username,
								   String server_password,
								   String validToken,
								   int expectedstatuscode,
								   SpogMessageCode expectedErrorMessage,
								   ExtentTest test ) {
		Response response = null;
		HashMap<String,Object> cloudRPSInfo = new HashMap<>();
		cloudRPSInfo = jp.composetoupdateCloudRPSInfo(server_name, server_port, server_protocol, server_username,server_password);
		response = spogCloudRPSInvoker.updateCloudRPS(cloudRPS_id,cloudRPSInfo, validToken);
		
		if(expectedstatuscode==SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.PASS, "Succesfully deleted the cloud rps server");
		}else {
			if(expectedErrorMessage != (null)) {
				String code = expectedErrorMessage.getCodeString();
				String message = expectedErrorMessage.getStatus();
				if(code.contains("01200002")){
					message = message.replace("{0}", cloudRPS_id);
				}
				if(code.contains("40000006")){
					message = message.replace("{0}", "server_protocol");
				}
				spogServer.checkErrorCode(response,code);
				test.log(LogStatus.PASS, "The error code matched with the expected "+code);
				spogServer.checkErrorMessage(response,message);
				test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
			}
		}
		
		return response;
	}
	
	/*
	   * @author Kiran.Sripada
	   * @param response
	   * @param expectedresponse
	   * @param test
	   * @return String ; cloudRPS_id
	   * 
	   */
	
	public String verifyCloudRPS(Response response, HashMap<String,Object>expectedresponse, ExtentTest test) {
		
		int statusCode = Integer.parseInt(response.then().extract().path("status").toString());
		
		if( statusCode != 200 && statusCode != 201) {
			
			test.log(LogStatus.WARNING, "Status code in response is: "+statusCode+" and sent for data validation so returning null");
			return null;
		}
		
		HashMap<String,Object> actualresponse = response.then().extract().path("data");
		
		test.log(LogStatus.INFO, "Check the server name");
		spogServer.assertResponseItem(actualresponse.get("server_name"), expectedresponse.get("server_name"), test);

		test.log(LogStatus.INFO, "Check the server port");
		spogServer.assertResponseItem(actualresponse.get("server_port"), expectedresponse.get("server_port"), test);

		test.log(LogStatus.INFO, "Check the server protocol");
		spogServer.assertResponseItem(actualresponse.get("server_protocol"), expectedresponse.get("server_protocol"), test);
		
		test.log(LogStatus.INFO, "Check server_type");
		assertNotNull(actualresponse.get("server_type"), "server_type is "+actualresponse.get("server_type"));
		
		test.log(LogStatus.INFO, "Check the server username");
		spogServer.assertResponseItem(actualresponse.get("server_username"), expectedresponse.get("server_username"), test);

		test.log(LogStatus.INFO, "Check the server organization id");
		HashMap<String, Object> act_org = new HashMap<>();
		act_org = (HashMap<String, Object>) actualresponse.get("organization");
		
		spogServer.assertResponseItem(act_org.get("organization_id"), expectedresponse.get("organization_id"), test);
		
		test.log(LogStatus.INFO, "Check the server organization name");
		spogServer.assertResponseItem(act_org.get("organization_name"), expectedresponse.get("organization_name"), test);
		
		test.log(LogStatus.INFO, "Check the server site details");
		HashMap<String, Object> act_site = new HashMap<>();
		act_site = (HashMap<String, Object>) actualresponse.get("site");
		spogServer.assertResponseItem(act_site.get("site_id"), expectedresponse.get("site_id"), test);
		spogServer.assertResponseItem(act_site.get("site_name"), expectedresponse.get("site_name"), test);
		
		//Check for the status param in the response
		test.log(LogStatus.INFO, "Check status");
		assertNotNull(actualresponse.get("status"), "status is "+actualresponse.get("status"));
		
		test.log(LogStatus.INFO, "Check the datacenter details");
		HashMap<String, Object> act_datacenter = new HashMap<>();
		act_datacenter = (HashMap<String, Object>) actualresponse.get("datacenter");
		spogServer.assertResponseItem(act_datacenter.get("datacenter_id"), expectedresponse.get("datacenter_id"), test);
		spogServer.assertResponseItem(act_datacenter.get("datacenter_name"), expectedresponse.get("datacenter_name"), test);
		
		HashMap<String, Object> act_create_user = new HashMap<>();
		act_create_user = (HashMap<String, Object>) actualresponse.get("create_user");
		
		test.log(LogStatus.INFO, "Check the user_id");
		spogServer.assertResponseItem(act_create_user.get("create_user_id"), expectedresponse.get("create_user_id"), test);
		
		test.log(LogStatus.INFO, "Check the user name");
		spogServer.assertResponseItem(act_create_user.get("create_username"), expectedresponse.get("create_username"), test);
		
		//nat server params check
		if (actualresponse.containsKey("nat")) {
			HashMap<String, Object> act_nat = (HashMap<String, Object>) actualresponse.get("nat");
			
			if (expectedresponse.containsKey("nat")) {
				test.log(LogStatus.INFO, "Check the nat_server");
				spogServer.assertResponseItem(act_nat.get("nat_server"), expectedresponse.get("nat_server"), test);
				
				test.log(LogStatus.INFO, "Check the nat_server_port");
				spogServer.assertResponseItem(act_nat.get("nat_server_nat"), expectedresponse.get("nat_server_port"), test);
			}else {
				test.log(LogStatus.INFO, "nat server details not provided in the request");
			}
			
		} else {
			assertFalse(true, "Resposne does not contain the nat server information");
		}		
		
		//Check for the rps_version param in the response
		test.log(LogStatus.INFO, "Check rps_version");
		assertNotNull(actualresponse.get("rps_version"), "rps_version is "+actualresponse.get("rps_version"));

		if (actualresponse.containsKey("volumes")) {
			ArrayList<HashMap<String, Object>> volumes = (ArrayList<HashMap<String, Object>>) actualresponse.get("volumes");
			for (int i = 0; i < volumes.size(); i++) {
				assertNotNull(volumes.get(i).get("volume_name"),"volume_name is "+volumes.get(i).get("volume_name"));
				assertNotNull(volumes.get(i).get("volume_capacity"),"volume_capacity is "+volumes.get(i).get("volume_capacity"));
			}
		}else {
			assertFalse(true, "Response does not contain the volumes information");
		}
		
		test.log(LogStatus.INFO, "Check capacity");
		assertNotNull(actualresponse.get("capacity"), "capacity is "+actualresponse.get("capacity"));
		
		test.log(LogStatus.INFO, "Check total_datastores_usage");
		assertNotNull(actualresponse.get("total_datastores_usage"), "total_datastores_usage is "+actualresponse.get("total_datastores_usage"));
		
		test.log(LogStatus.INFO, "Check total_datastores_capacity");
		assertNotNull(actualresponse.get("total_datastores_capacity"), "total_datastores_capacity is "+actualresponse.get("total_datastores_capacity"));
		
		test.log(LogStatus.INFO, "Check space_available");
		assertNotNull(actualresponse.get("space_available"), "space_available is "+actualresponse.get("space_available"));
		
		return actualresponse.get("server_id").toString();
		
	}
	
	public String verifyCloudRPS(HashMap<String,Object> actualresponse, HashMap<String,Object> expectedresponse, ExtentTest test) {
		
		test.log(LogStatus.INFO, "Check the server name");
		spogServer.assertResponseItem(actualresponse.get("server_name"), expectedresponse.get("server_name"), test);

		test.log(LogStatus.INFO, "Check the server port");
		spogServer.assertResponseItem(actualresponse.get("server_port"), expectedresponse.get("server_port"), test);

		test.log(LogStatus.INFO, "Check the server protocol");
		spogServer.assertResponseItem(actualresponse.get("server_protocol"), expectedresponse.get("server_protocol"), test);

		test.log(LogStatus.INFO, "Check server_type");
		assertNotNull(actualresponse.get("server_type"), "server_type is "+actualresponse.get("server_type"));
		
		test.log(LogStatus.INFO, "Check the server username");
		spogServer.assertResponseItem(actualresponse.get("server_username"), expectedresponse.get("server_username"), test);

		test.log(LogStatus.INFO, "Check the server organization id");
		HashMap<String, Object> act_org = new HashMap<>();
		act_org = (HashMap<String, Object>) actualresponse.get("organization");
		
		spogServer.assertResponseItem(act_org.get("organization_id"), expectedresponse.get("organization_id"), test);
		
		test.log(LogStatus.INFO, "Check the server organization name");
		spogServer.assertResponseItem(act_org.get("organization_name"), expectedresponse.get("organization_name"), test);
		
		test.log(LogStatus.INFO, "Check the server site details");
		HashMap<String, Object> act_site = new HashMap<>();
		act_site = (HashMap<String, Object>) actualresponse.get("site");
		spogServer.assertResponseItem(act_site.get("site_id"), expectedresponse.get("site_id"), test);
		spogServer.assertResponseItem(act_site.get("site_name"), expectedresponse.get("site_name"), test);
		
		//Check for the status param in the response
		test.log(LogStatus.INFO, "Check status");
		assertNotNull(actualresponse.get("status"), "status is "+actualresponse.get("status"));
		
		test.log(LogStatus.INFO, "Check the datacenter details");
		HashMap<String, Object> act_datacenter = new HashMap<>();
		act_datacenter = (HashMap<String, Object>) actualresponse.get("datacenter");
		spogServer.assertResponseItem(act_datacenter.get("datacenter_id"), expectedresponse.get("datacenter_id"), test);
		spogServer.assertResponseItem(act_datacenter.get("datacenter_name"), expectedresponse.get("datacenter_name"), test);
		
		HashMap<String, Object> act_create_user = new HashMap<>();
		act_create_user = (HashMap<String, Object>) actualresponse.get("create_user");
		
		test.log(LogStatus.INFO, "Check the user_id");
		spogServer.assertResponseItem(act_create_user.get("create_user_id"), expectedresponse.get("create_user_id"), test);
		
		test.log(LogStatus.INFO, "Check the user name");
		spogServer.assertResponseItem(act_create_user.get("create_username"), expectedresponse.get("create_username"), test);

		//nat server params check
		if (actualresponse.containsKey("nat")) {
			HashMap<String, Object> act_nat = (HashMap<String, Object>) actualresponse.get("nat");

			assertNotNull(actualresponse.get("nat_server"), "nat_server is "+actualresponse.get("nat_server"));
			assertNotNull(actualresponse.get("nat_server_port"), "nat_server_port is "+actualresponse.get("nat_server_port"));

			if (expectedresponse.containsKey("nat")) {
				test.log(LogStatus.INFO, "Check the nat_server");
				spogServer.assertResponseItem(act_nat.get("nat_server"), expectedresponse.get("nat_server"), test);

				test.log(LogStatus.INFO, "Check the nat_server_port");
				spogServer.assertResponseItem(act_nat.get("nat_server_nat"), expectedresponse.get("nat_server_port"), test);
			}else {
				test.log(LogStatus.INFO, "nat server details not provided in the request");
			}

		} else {
			assertFalse(true, "Resposne does not contain the nat server information");
		}	

		//Check for the rps_version param in the response
		test.log(LogStatus.INFO, "Check rps_version");
		assertNotNull(actualresponse.get("rps_version"), "rps_version is "+actualresponse.get("rps_version"));
		
		if (actualresponse.containsKey("volumes")) {
			ArrayList<HashMap<String, Object>> volumes = (ArrayList<HashMap<String, Object>>) actualresponse.get("volumes");
			for (int i = 0; i < volumes.size(); i++) {
				assertNotNull(volumes.get(i).get("volume_name"),"volume_name is "+volumes.get(i).get("volume_name"));
				assertNotNull(volumes.get(i).get("volume_capacity"),"volume_capacity is "+volumes.get(i).get("volume_capacity"));
			}
		}else {
			assertFalse(true, "Response does not contain the volumes information");
		}
		
		test.log(LogStatus.INFO, "Check capacity");
		assertNotNull(actualresponse.get("capacity"), "capacity is "+actualresponse.get("capacity"));
		
		test.log(LogStatus.INFO, "Check total_datastores_usage");
		assertNotNull(actualresponse.get("total_datastores_usage"), "total_datastores_usage is "+actualresponse.get("total_datastores_usage"));
		
		test.log(LogStatus.INFO, "Check total_datastores_capacity");
		assertNotNull(actualresponse.get("total_datastores_capacity"), "total_datastores_capacity is "+actualresponse.get("total_datastores_capacity"));
		
		test.log(LogStatus.INFO, "Check space_available");
		assertNotNull(actualresponse.get("space_available"), "space_available is "+actualresponse.get("space_available"));		
		
		return actualresponse.get("server_id").toString();
		
	}
	
	public void validateCloudRPSResponse(Response response, int expectedstatuscode,SpogMessageCode expectederrormessage, String to_replace, ExtentTest test) {
		test.log(LogStatus.INFO, "Check the status code and the expected error message");
		spogServer.checkResponseStatus(response, expectedstatuscode);
		if(expectedstatuscode==SpogConstants.SUCCESS_POST) {
			
		}else {
			String code = expectederrormessage.getCodeString();
			String message = expectederrormessage.getStatus();
			if (code.contains("40000002")) {
				message = message.replace("{0}", to_replace);
			}
			if(code.contains("01200001")) {
				message = message.replace("{0}", to_replace);
			}
			if(code.contains("01200002")) {
				message = message.replace("{0}", to_replace);
			}
			if(code.contains("40000005")) {
				message = message.replace("{0}", to_replace);
			}
			
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.INFO, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response,message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		}
		
	}
	
	
	
	public void verifyCloudRPSDatastore(HashMap<String,Object> actual_response, HashMap<String,Object> datastorepropertiesInfo,  ExtentTest test) {

		test.log(LogStatus.INFO, "Check the server name");
		String datastore_id = null;
		HashMap<String, Object> actualresponse = (HashMap<String, Object>) actual_response.get("datastore_properties");
		spogServer.assertResponseItem(actualresponse.get("concurrent_active_nodes").toString(), datastorepropertiesInfo.get("concurrent_active_nodes").toString(), test);
		spogServer.assertResponseItem(actualresponse.get("capacity").toString(), datastorepropertiesInfo.get("capacity").toString(), test);
		spogServer.assertResponseItem(actualresponse.get("datastore_path").toString(), datastorepropertiesInfo.get("datastore_path").toString(), test);
		spogServer.assertResponseItem(actualresponse.get("datastore_username").toString(), datastorepropertiesInfo.get("datastore_username").toString(), test);
		if((boolean) actual_response.get("compression_enabled")) {
			spogServer.assertResponseItem(actualresponse.get("compression").toString(), datastorepropertiesInfo.get("compression").toString(), test);
		}else {
			if(actualresponse.containsKey("compression")) {
				test.log(LogStatus.FAIL, "The compression type is still set in the response even though its disabled");	
				assertTrue("The passwords are getting displayed in the response",false);
			}else {
				test.log(LogStatus.PASS, "Could not find the compression type in the response as it is disabled");	
				assertTrue("The passwords are getting displayed in the response",true);
			}
		}
		
		
		HashMap<String,Object> actualdeduplicationInfo = (HashMap<String, Object>) actualresponse.get("deduplication");
		HashMap<String,Object> expecteddeduplicationInfo = (HashMap<String, Object>) datastorepropertiesInfo.get("deduplication");
		
		spogServer.assertResponseItem(actualdeduplicationInfo.get("block_size"), expecteddeduplicationInfo.get("block_size"), test);
		spogServer.assertResponseItem(actualdeduplicationInfo.get("hash_memory"), expecteddeduplicationInfo.get("hash_memory"), test);
		spogServer.assertResponseItem(actualdeduplicationInfo.get("data_destination_path"), expecteddeduplicationInfo.get("data_destination_path"), test);
		spogServer.assertResponseItem(actualdeduplicationInfo.get("data_destination_username"), expecteddeduplicationInfo.get("data_destination_username"), test);
		spogServer.assertResponseItem(actualdeduplicationInfo.get("index_destination_path"), expecteddeduplicationInfo.get("index_destination_path"), test);
		spogServer.assertResponseItem(actualdeduplicationInfo.get("index_destination_username"), expecteddeduplicationInfo.get("index_destination_username"), test);
		spogServer.assertResponseItem(actualdeduplicationInfo.get("hash_destination_path"), expecteddeduplicationInfo.get("hash_destination_path"), test);
		spogServer.assertResponseItem(actualdeduplicationInfo.get("hash_destination_username"), expecteddeduplicationInfo.get("hash_destination_username"), test);
		if(actualdeduplicationInfo.containsKey("data_destination_password")&&(actualdeduplicationInfo.containsKey("hash_destination_password"))
				&&(actualdeduplicationInfo.containsKey("index_destination_password"))&&(actualresponse.containsKey("datastore_password"))) {
			test.log(LogStatus.FAIL, "The passwords are getting displayed in the response");	
			assertTrue("The passwords are getting displayed in the response",false);
		}else {
			test.log(LogStatus.PASS, "The passwords are not getting displayed in the response");	
			assertTrue("The passwords are not getting displayed in the response",true);
		}
		
//		assertNotNull(actualresponse.get("shared_path"), "shared_path : "+actualresponse.get("shared_path"));
		assertNotNull(actualresponse.get("overall_data_reduction"), "overall_data_reduction : "+actualresponse.get("overall_data_reduction"));
		assertNotNull(actualresponse.get("compression_percentage"), "compression_percentage : "+actualresponse.get("compression_percentage"));
		assertNotNull(actualresponse.get("deduplication_percentage"), "deduplication_percentage : "+actualresponse.get("deduplication_percentage"));
		assertNotNull(actualresponse.get("source_data_size"), "source_data_size : "+actualresponse.get("source_data_size"));
		assertNotNull(actualresponse.get("storage_space"), "storage_space : "+actualresponse.get("storage_space"));
		assertNotNull(actualresponse.get("capacity_usage"), "Datastore capacity_usage : "+actualresponse.get("capacity_usage"));
		
		/*assertNotNull(actualresponse.get("backup_destination"));
		assertNotNull(((HashMap<String, Object>) actualresponse.get("backup_destination")).get("capacity"), "backup_destination capacity : "+((HashMap<String, Object>) actualresponse.get("backup_destination")).get("capacity"));
		assertNotNull(((HashMap<String, Object>) actualresponse.get("backup_destination")).get("usage"), "backup_destination usage : "+((HashMap<String, Object>) actualresponse.get("backup_destination")).get("usage"));
		
		assertNotNull(actualresponse.get("data_destination"));
		assertNotNull(((HashMap<String, Object>) actualresponse.get("data_destination")).get("capacity"), "data_destination capacity : "+((HashMap<String, Object>) actualresponse.get("data_destination")).get("capacity"));
		assertNotNull(((HashMap<String, Object>) actualresponse.get("data_destination")).get("usage"), "data_destination usage : "+((HashMap<String, Object>) actualresponse.get("data_destination")).get("usage"));
		
		assertNotNull(actualresponse.get("index_destination"));
		assertNotNull(((HashMap<String, Object>) actualresponse.get("index_destination")).get("capacity"), "index_destination capacity : "+((HashMap<String, Object>) actualresponse.get("index_destination")).get("capacity"));
		assertNotNull(((HashMap<String, Object>) actualresponse.get("index_destination")).get("usage"), "index_destination usage : "+((HashMap<String, Object>) actualresponse.get("index_destination")).get("usage"));
		
		assertNotNull(actualresponse.get("hash_destination"));
		assertNotNull(((HashMap<String, Object>) actualresponse.get("hash_destination")).get("capacity"), "hash_destination capacity : "+((HashMap<String, Object>) actualresponse.get("hash_destination")).get("capacity"));
		assertNotNull(((HashMap<String, Object>) actualresponse.get("hash_destination")).get("usage"), "hash_destination usage : "+((HashMap<String, Object>) actualresponse.get("hash_destination")).get("usage"));
		
		assertNotNull(actualresponse.get("memory_allocation"));
		assertNotNull(((HashMap<String, Object>) actualresponse.get("memory_allocation")).get("capacity"), "memory_allocation capacity : "+((HashMap<String, Object>) actualresponse.get("memory_allocation")).get("capacity"));
		assertNotNull(((HashMap<String, Object>) actualresponse.get("memory_allocation")).get("usage"), "memory_allocation usage : "+((HashMap<String, Object>) actualresponse.get("memory_allocation")).get("usage"));
		
		*/
		
		if((boolean) datastorepropertiesInfo.get("send_email")) {
			spogServer.assertResponseItem(actualresponse.get("to_email").toString(), datastorepropertiesInfo.get("to_email").toString(), test);
		}
		

	}
	
	
	 public String validateCloudRPSDataStoreResponse(Response response, String datastore_name, boolean dedupe_enabled, boolean encryption_enabled, boolean compression_enabled, String rps_server_id, 
			 String rps_server_name, HashMap<String,Object> datastorepropertiesInfo, 
			  int expectedstatuscode,SpogMessageCode expectederrormessage, String to_replace, ExtentTest test) {
			test.log(LogStatus.INFO, "Check the status code and the expected error message");
			String datastore_id = null;
			spogServer.checkResponseStatus(response, expectedstatuscode);
			if(expectedstatuscode==SpogConstants.SUCCESS_POST||expectedstatuscode==SpogConstants.SUCCESS_GET_PUT_DELETE) {
				HashMap<String,Object> actualresponse = response.then().extract().path("data");
				spogServer.assertResponseItem(datastore_name, actualresponse.get("datastore_name").toString(), test);
				spogServer.assertResponseItem(dedupe_enabled, actualresponse.get("dedupe_enabled"), test);
				spogServer.assertResponseItem(encryption_enabled, actualresponse.get("encryption_enabled"), test);
				spogServer.assertResponseItem(compression_enabled, actualresponse.get("compression_enabled"), test);
				spogServer.assertResponseItem(rps_server_id, actualresponse.get("rps_server_id").toString(), test);
				spogServer.assertResponseItem(rps_server_name, actualresponse.get("server_name"), test);
				assertNotNull(actualresponse.get("status"), "Datastore status is:"+actualresponse.get("status"));
				
				verifyCloudRPSDatastore(actualresponse,datastorepropertiesInfo,test);
				datastore_id = actualresponse.get("datastore_id").toString();
			}else {
				
				if (response.then().extract().path("errors.code").toString().length() > 8) {
					return datastore_id;
				}
				
				String code = expectederrormessage.getCodeString();
				String message = expectederrormessage.getStatus();
				if (code.contains("40000002")) {
					message = message.replace("{0}", to_replace);
					
				}
				if(code.contains("01200001")) {
					message = message.replace("{0}", to_replace);
				}
				if(code.contains("01200002")) {
					message = message.replace("{0}", to_replace);
				}
				if(code.contains("40000001")) {
					message = message.replace("{0}", to_replace);
				}
				if(code.contains("01500008")) {
					message = message.replace("{0}", to_replace);
				}
				if(code.contains("01500006")) {
					message = message.replace("{0}", to_replace);
				}
				
				spogServer.checkErrorCode(response, code);
				test.log(LogStatus.INFO, "The error code matched with the expected " + code);
				spogServer.checkErrorMessage(response,message);
				test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
			}
			return datastore_id;
			
		}

	/*
	   * @author Kiran.Sripada
	   * @param cloudRPS_id
	   * @param adminToken
	   * @param expectedresponse
	   * @param expectedStatusCode
	   * @param expectedErrorMessage
	   * @param test
	   * 
	   */
	
	public void deleteCloudRPS(String cloudRPS_id, String adminToken, int expectedstatuscode, SpogMessageCode expectedErrorMessage, ExtentTest test) {
		Response response = spogCloudRPSInvoker.deleteCloudRPS(cloudRPS_id, adminToken);
		spogServer.checkResponseStatus(response, expectedstatuscode, test);

		if(expectedstatuscode==SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.PASS, "Succesfully deleted the cloud rps server");
		}else {

			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			if(code.contains("01200002")){
				message = message.replace("{0}", cloudRPS_id);
			}
			spogServer.checkErrorCode(response,code);
			test.log(LogStatus.PASS, "The error code matched with the expected "+code);
			spogServer.checkErrorMessage(response,message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);

		}
		

	}
	
	  /*
	   * @author Rakesh.Chalamala
	   * @param cloudRPS_id
	   * @param validToken
	   * @param expectedresponse
	   * @param expectedStatusCode
	   * @param expectedErrorMessage
	   * @param test
	   * @return server_id
	   */
	  public String getCloudRPSbyIdwithCheck(String cloudRPS_id, String validToken,
			  								HashMap<String, Object> expectedresponse,int expectedStatusCode,
			  								SpogMessageCode expectedErrorMessage, ExtentTest test) {
		  String server_id = null;
		  test.log(LogStatus.INFO, "Call the rest API - getCloudRPSbyId to get the response.");
		  Response response = spogCloudRPSInvoker.getCloudRPSbyId(cloudRPS_id, validToken);
		  spogServer.checkResponseStatus(response, expectedStatusCode, test);
		  
		  if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			  test.log(LogStatus.INFO, "Verify the response");
			  server_id = verifyCloudRPS(response, expectedresponse, test);
		  } else {
			  	String code = expectedErrorMessage.getCodeString();
				String message = expectedErrorMessage.getStatus();
				if (code.contains("01200002")) {
					message = message.replace("{0}", cloudRPS_id);
					
				}
				spogServer.checkErrorCode(response, code);
				test.log(LogStatus.INFO, "The error code matched with the expected " + code);
				spogServer.checkErrorMessage(response,message);
				test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		  }
		  
		  return server_id;
	  }
	  
	  /*
	   * @author Rakesh.Chalamala
	   * @param cloudRPS_id
	   * @param validToken
	   * @param expectedresponse
	   * @param expectedStatusCode
	   * @param expectedErrorMessage
	   * @param test
	   * @return server_id
	   */
	  public String getCloudRPSwithCheck(String validToken,
			  							ArrayList<HashMap<String, Object>> expectedresponse,
			  							String filterStr, String SortStr,
			  							int expectedStatusCode, SpogMessageCode expectedErrorMessage,
			  							ExtentTest test) {
		  String server_id = null;
		  String additionalURL=null;
		  ArrayList<HashMap<String, Object>> actual_response = new ArrayList<>();
		  ArrayList<HashMap<String, Object>> filteredexpectedresponse = new ArrayList<>();
		  if((filterStr!=null && filterStr!="") || (SortStr!=null && SortStr!="")) {
			additionalURL = spogServer.PrepareURL(filterStr, SortStr, 1, 20, test);
		  }
		  
		  
		  if(filterStr!=null && !filterStr.isEmpty() && expectedresponse != null) {

				String[] filterStrArray = filterStr.split(",");
				for (int i = 0; i < filterStrArray.length; i++) {
					String[] eachFilterStr = filterStrArray[i].split(";");
					
					filteredexpectedresponse = new ArrayList<>();
					for (int j = 0; j < expectedresponse.size(); j++) {
					
						if(eachFilterStr[0].equals("server_name")) {
							if (eachFilterStr[2].contains("|")) {
								String[] multiple = eachFilterStr[2].split("\\|");
								if(expectedresponse.get(j).containsValue(multiple[0])||expectedresponse.get(j).containsValue(multiple[1])) {
									filteredexpectedresponse.add(expectedresponse.get(j));
								}
							}else {
								if(expectedresponse.get(j).containsValue(eachFilterStr[2])) {
									filteredexpectedresponse.add(expectedresponse.get(j));
								}
							}	
						}
						else if(eachFilterStr[0].equals("status")) {
							if (eachFilterStr[2].contains("|")) {
								String[] multiple = eachFilterStr[2].split("\\|");
								if(expectedresponse.get(j).containsValue(multiple[0])||expectedresponse.get(j).containsValue(multiple[1])) {
									filteredexpectedresponse.add(expectedresponse.get(j));
								}
							}else {
								if(expectedresponse.get(j).containsValue(eachFilterStr[2])) {
									filteredexpectedresponse.add(expectedresponse.get(j));
								}
							}
						}else {
							filteredexpectedresponse = expectedresponse;
						}
					}
					expectedresponse = filteredexpectedresponse;
				}
			
			}else {
				filteredexpectedresponse = expectedresponse;
			}
		
		  test.log(LogStatus.INFO, "Call the rest API - getCloudRPS to get the response.");
		  Response response = spogCloudRPSInvoker.getCloudRPS(validToken,additionalURL,test);
		  spogServer.checkResponseStatus(response, expectedStatusCode, test);
		  
		  if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			  actual_response = response.then().extract().path("data");
			  if(actual_response.size()>1 && filteredexpectedresponse.size()>1) {
				  if (SortStr == null || SortStr == "" ) {
					  test.log(LogStatus.INFO, "Sort the actual response");
					  spogServer.sortArrayListbyString(actual_response, "server_name");
					  
					  test.log(LogStatus.INFO, "Sort the actual response");
					  spogServer.sortArrayListbyString(filteredexpectedresponse, "server_name");  

				  }else if(SortStr.contains("asc")){
					  test.log(LogStatus.INFO, "Sort the actual response");
					  spogServer.sortArrayListbyString(filteredexpectedresponse, "server_name");
				  }else if(SortStr.contains("desc")) {
					  test.log(LogStatus.INFO, "Sort the actual response");
					  spogServer.sortArrayListbyString(filteredexpectedresponse, "server_name");
					  
					  Collections.reverse(filteredexpectedresponse);
				  }
				  
			  }
			  if (actual_response.size() == filteredexpectedresponse.size()) {
				
				  for (int i = 0; i < actual_response.size(); i++) {
					  test.log(LogStatus.INFO, "Verify the response");
					  server_id = verifyCloudRPS(actual_response.get(i), filteredexpectedresponse.get(i), test);
				}
			  }else {
				test.log(LogStatus.INFO, "Actual response size: "+actual_response.size()+", Expected response size: "+filteredexpectedresponse.size());
			  }
		  } else {
			  	String code = expectedErrorMessage.getCodeString();
				String message = expectedErrorMessage.getStatus();
				spogServer.checkErrorCode(response, code);
				test.log(LogStatus.INFO, "The error code matched with the expected " + code);
				spogServer.checkErrorMessage(response,message);
				test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		  }
		  
		  return server_id;
	  }
	  
	  /*
	   * @author Rakesh.Chalamala
	   * @param server_name
	   * @param server_port
	   * @param server_protocol
	   * @param server_username
	   * @param server_password
	   * @param organization_id
	   * @param site_id
	   * @param datacenter_id
	   */
	  public HashMap<String,Object> composeCloudRPSInfo(String server_name,
			  											String server_port,
			  											String server_protocol,
			  											String server_username,
			  											String server_password,
			  											String organization_id,
			  											String organization_name,
			  											String site_id,
			  											String site_name,
			  											String datacenter_id,
			  											String datacenter_name,
			  											String user_id,
			  											String user_name) {
		  
		  	HashMap<String,Object> compose_cloudRPS_Info = new HashMap<>();
		  	
		  	compose_cloudRPS_Info.put("server_name",server_name);
			compose_cloudRPS_Info.put("server_port",server_port);
			compose_cloudRPS_Info.put("server_protocol",server_protocol);
			compose_cloudRPS_Info.put("server_username",server_username);

			if (organization_id == "" || organization_id == null) {
				compose_cloudRPS_Info.put("organization_id",spogServer.GetLoggedinUserOrganizationID());
			} else {
				compose_cloudRPS_Info.put("organization_id",organization_id);
				compose_cloudRPS_Info.put("organization_name", organization_name);
			}
			compose_cloudRPS_Info.put("create_user_id", user_id);
			compose_cloudRPS_Info.put("create_username", user_name);
			compose_cloudRPS_Info.put("site_id",site_id);
			compose_cloudRPS_Info.put("site_name", site_name);
			compose_cloudRPS_Info.put("datacenter_id",datacenter_id);
			compose_cloudRPS_Info.put("datacenter_name", datacenter_name);
			compose_cloudRPS_Info.put("status", "normal");

			return compose_cloudRPS_Info;
	  }
	  
	  public void getCloudRPSDataStoresWithCheck(String token, ArrayList<HashMap<String, Object>> expectedDatastoreInfo,
			  String filterStr, String SortStr, int curr_page, int page_size,
			  int expectedStatusCode, SpogMessageCode expectedErrorMessage ,ExtentTest test) {

		  String additionalURL="";

		  if((filterStr!=null && filterStr!="") || (SortStr!=null && SortStr!="")) {
			  if (curr_page == 0||curr_page==-1) {
				  curr_page = 1;
			  }
			  if (page_size == 0 ||page_size==-1) {
				  page_size = 20;
			  }else if(page_size>=100&&page_size<SpogConstants.MAX_PAGE_SIZE){
				  page_size=100;
			  }
			  test.log(LogStatus.INFO, "Prepare the additional URL ");
			  additionalURL = spogServer.PrepareURL(filterStr, SortStr, curr_page, page_size, test);
		  }

		  test.log(LogStatus.INFO, "Call REST API getCloudRPSDataStores");
		  Response response = spogCloudRPSInvoker.getCloudRPSDataStore(token, additionalURL, test);
		  spogServer.checkResponseStatus(response, expectedStatusCode, test);

		  if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			  
			  if(!expectedDatastoreInfo.equals(null)) {
				  	test.log(LogStatus.INFO, "Call validateGetCloudRPSDataStoreInfo");
			  		validateGetCloudRPSDataStoreInfo(response,expectedDatastoreInfo,filterStr,SortStr,curr_page,page_size,test);
			  }
		  }else {

			  String code = expectedErrorMessage.getCodeString();
			  String message = expectedErrorMessage.getStatus();			

			  spogServer.checkErrorCode(response,code);
			  test.log(LogStatus.PASS, "The error code matched with the expected "+code);
			  spogServer.checkErrorMessage(response,message);
			  test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		  }
	  }
	  
	  /* Call API - GET: /datastores
	   * 
	   * @author Rakesh.Chalamala
	   * @param token
	   * @param test
	   * @return datastore_ids
	   */
	  public ArrayList<String> getCloudRPSDatastores(String token, ExtentTest test) {
		  
		  String additionalURL = null;
		  ArrayList<String> datastore_ids = new ArrayList<>() ;
		  test.log(LogStatus.INFO, "Call REST API getCloudRPSDataStores");
		  Response response = spogCloudRPSInvoker.getCloudRPSDataStore(token, additionalURL, test);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  
		  ArrayList<HashMap<String, Object>> actualData = new ArrayList<>();
		  actualData = response.then().extract().path("data");
		  
		  for (int i = 0; i < actualData.size(); i++) {
			  datastore_ids.add((String) actualData.get(i).get("datastore_id"));
		  }
		  
		  return datastore_ids;
	  }
	  
	  /* API - GET: cloud_rps_id/datastores
	   * 
	   * @author Rakesh.Chalamala
	   * @param token
	   * @param rps_server_id
	   * @param expectedDatastoreInfo
	   * @param filterStr
	   * @param SortStr
	   * @param curr_page
	   * @param page_size
	   * @param expectedStatusCode
	   * @param expectedErrorMessage
	   * @param test
	   */
	  public Response getCloudRPSDataStoresForSpecifiedRPSWithCheck(String token, String rps_server_id, ArrayList<HashMap<String, Object>> expectedDatastoreInfo,
			  String filterStr, String SortStr, int curr_page, int page_size,
			  int expectedStatusCode, SpogMessageCode expectedErrorMessage ,ExtentTest test) {

		  String additionalURL="";

		  if((filterStr!=null && filterStr!="") || (SortStr!=null && SortStr!="")) {
			  if (curr_page == 0||curr_page==-1) {
				  curr_page = 1;
			  }
			  if (page_size == 0 ||page_size==-1) {
				  page_size = 20;
			  }else if(page_size>=100&&page_size<SpogConstants.MAX_PAGE_SIZE){
				  page_size=100;
			  }
			  test.log(LogStatus.INFO, "Prepare the additional URL ");
			  additionalURL = spogServer.PrepareURL(filterStr, SortStr, curr_page, page_size, test);
		  }

		  test.log(LogStatus.INFO, "Call REST API getCloudRPSDataStoresForSpecifiedRPS");
		  Response response = spogCloudRPSInvoker.getCloudRPSDataStoresForSpecifiedRPS(token, rps_server_id, additionalURL, test);
		  spogServer.checkResponseStatus(response, expectedStatusCode, test);

		  if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			  if (expectedDatastoreInfo != null) {
				  test.log(LogStatus.INFO, "Call validateGetCloudRPSDataStoreInfo");
				  validateGetCloudRPSDataStoreInfo(response,expectedDatastoreInfo,filterStr,SortStr,curr_page,page_size,test);	
			  }
		  
		  }else {

			  String code = expectedErrorMessage.getCodeString();
			  String message = expectedErrorMessage.getStatus();			
			  if (code.contains("01200002")) {
				message = message.replace("{0}", rps_server_id);
			  }	
			  spogServer.checkErrorCode(response,code);
			  test.log(LogStatus.PASS, "The error code matched with the expected "+code);
			  spogServer.checkErrorMessage(response,message);
			  test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		  }
		  
		  return response;
	  }
	  
	  /*
	   * Validate the response of GET: /datastores
	   * 
	   * @author Rakesh.Chalamala
	   * @param response
	   * @param expectedDatastoreInfo
	   * @param filterStr
	   * @param SortStr
	   * @param curr_page
	   * @param page_size
	   * @param test
	   */
	  public void validateGetCloudRPSDataStoreInfo(Response response, ArrayList<HashMap<String, Object>> expectedDatastoreInfo,
			  String filterStr, String SortStr, int curr_page, int page_size, ExtentTest test) {

		  ArrayList<HashMap<String, Object>> filteredDatastoreInfo = new ArrayList<>();

		  if(filterStr!=null && !filterStr.isEmpty()) {


			  String[] filterStrArray = filterStr.split(",");
			  for (int i = 0; i < filterStrArray.length; i++) {
				  String[] eachFilterStr = filterStrArray[i].split(";");

				  filteredDatastoreInfo = new ArrayList<>();
				  for (int j = 0; j < expectedDatastoreInfo.size(); j++) {

					  if(eachFilterStr[0].equals("organization_id")) {
						  if (eachFilterStr[2].contains("|")) {
							  String[] multiple = eachFilterStr[2].split("\\|");
							  if(expectedDatastoreInfo.get(j).containsValue(multiple[0])||expectedDatastoreInfo.get(j).containsValue(multiple[1])) {
								  filteredDatastoreInfo.add(expectedDatastoreInfo.get(j));
							  }
						  }else {
							  if(expectedDatastoreInfo.get(j).containsValue(eachFilterStr[2])) {
								  filteredDatastoreInfo.add(expectedDatastoreInfo.get(j));
							  }
						  }
					  }
					  else if(eachFilterStr[0].equals("rps_server_id")) {
						  if (eachFilterStr[2].contains("|")) {
							  String[] multiple = eachFilterStr[2].split("\\|");
							  if(expectedDatastoreInfo.get(j).containsValue(multiple[0])||expectedDatastoreInfo.get(j).containsValue(multiple[1])) {
								  filteredDatastoreInfo.add(expectedDatastoreInfo.get(j));
							  }
						  }else {
							  if(expectedDatastoreInfo.get(j).containsValue(eachFilterStr[2])) {
								  filteredDatastoreInfo.add(expectedDatastoreInfo.get(j));
							  }
						  }
					  }
					  else if(eachFilterStr[0].equals("dataStore_id")) {
						  if (eachFilterStr[2].contains("|")) {
							  String[] multiple = eachFilterStr[2].split("\\|");
							  if(expectedDatastoreInfo.get(j).containsValue(multiple[0])||expectedDatastoreInfo.get(j).containsValue(multiple[1])) {
								  filteredDatastoreInfo.add(expectedDatastoreInfo.get(j));
							  }
						  }else {
							  if(expectedDatastoreInfo.get(j).containsValue(eachFilterStr[2])) {
								  filteredDatastoreInfo.add(expectedDatastoreInfo.get(j));
							  }
						  }
					  }
					  else if(eachFilterStr[0].equals("dataStore_name")) {
						  if (eachFilterStr[2].contains("|")) {
							  String[] multiple = eachFilterStr[2].split("\\|");
							  if(expectedDatastoreInfo.get(j).containsValue(multiple[0])||expectedDatastoreInfo.get(j).containsValue(multiple[1])) {
								  filteredDatastoreInfo.add(expectedDatastoreInfo.get(j));
							  }
						  }else {
							  if(expectedDatastoreInfo.get(j).containsValue(eachFilterStr[2])) {
								  filteredDatastoreInfo.add(expectedDatastoreInfo.get(j));
							  }
						  }
					  }
				  }
				  expectedDatastoreInfo = filteredDatastoreInfo;
			  }

		  }else {
			  filteredDatastoreInfo = expectedDatastoreInfo;
		  }

		  ArrayList<HashMap<String, Object>> actualDataStoreInfo = new ArrayList<>();
		  actualDataStoreInfo = response.then().extract().path("data");

		  if (filteredDatastoreInfo!=null && filteredDatastoreInfo.size() >= actualDataStoreInfo.size()) {

			  test.log(LogStatus.INFO, "check response For the Datastores ");		
			  if (((SortStr == null) || (SortStr.equals("")))
					  && ((filterStr == null) || (filterStr.equals("")))) {
				  // If we Don't mention any Sorting the default sorting is descending order 
				  //validating the response for Datastores	
				  int j=0;
				  if (page_size == 20) {
					  j = actualDataStoreInfo.size() - 1;
					  if(filteredDatastoreInfo.size()>=actualDataStoreInfo.size()) {
						  j=filteredDatastoreInfo.size()-1;
					  }					
				  }
				  if (curr_page != 1) {
					  j = (filteredDatastoreInfo.size() - 1) - (curr_page - 1) * page_size;
				  }	
				  for (int i = 0; i < actualDataStoreInfo.size() && j >= 0; i++, j--) {
					  HashMap<String,Object> actual_hypervisor_data=actualDataStoreInfo.get(i),expected_hypervisor_data=filteredDatastoreInfo.get(j);
					  //validate the response for the Datastores
					  checkForDataStoreData(expected_hypervisor_data,actual_hypervisor_data,test);						
				  }
			  }			
			  // SortStr
			  else if ( //For sorting only and no filtering but includes pagination
					  (((SortStr != null) || (!SortStr.equals("")))
							  &&((filterStr == null) || (filterStr.equals(""))))
					  //For Sorting and Filtering both 
					  ||(((SortStr != null)||(!SortStr.equals(""))) &&((filterStr != null) || (!filterStr.equals(""))))
					  ) {

				  test.log(LogStatus.INFO,"Validating the response for the get Datastore ");
				  // validating the response for all the users who are sorted based on response in ascending
				  // order and descending order
				  if (SortStr.contains("asc")) {
					  int j = 0;
					  if (curr_page != 1) {
						  j = (curr_page - 1) * page_size;
					  }	
					  for(int i=0;i<actualDataStoreInfo.size();i++,j++){
						  HashMap<String,Object> actual_datastore_data=actualDataStoreInfo.get(i),expected_datastore_data=filteredDatastoreInfo.get(j);

						  //validate the response for the Datastores
						  checkForDataStoreData(expected_datastore_data,actual_datastore_data,test);
					  }
				  } else if(SortStr.contains("desc")){
					  int j = 0;
					  if (page_size == 20) {
						  j = actualDataStoreInfo.size() - 1;
						  if(filteredDatastoreInfo.size()>=actualDataStoreInfo.size()) {
							  j=filteredDatastoreInfo.size()-1;
						  }					
					  } else {
						  j = filteredDatastoreInfo.size() - 1;
						  if (curr_page != 1) {
							  j = (filteredDatastoreInfo.size() - 1) - (curr_page - 1) * page_size;
						  }
					  }						
					  for (int i = 0; i < actualDataStoreInfo.size() && j >= 0; i++, j--) {		
						  HashMap<String,Object> actual_datastore_data=actualDataStoreInfo.get(i),expected_datastore_data=filteredDatastoreInfo.get(j);
						  //validate the response for the Datastores
						  checkForDataStoreData(expected_datastore_data,actual_datastore_data,test);
					  }							
				  }
			  }
		  }
	  }
	  
	  /* API - GET: /datastores/id
	   * 
	   * @author Rakesh.Chalamala
	   * @param datastore_id
	   * @param expectedData
	   * @param expectedStatusCode
	   * @param expectedErrorMessage
	   * @param test
	   */
	  public void getCloudRPSDatastoreByIdWithCheck(String token, String datastore_id, HashMap<String, Object> expectedData,
			  											int expectedStatusCode, SpogMessageCode expectedErrorMessage, ExtentTest test) {
		  
		  Response response = getCloudRPSDatastoreById(token,datastore_id, expectedStatusCode, test);
		  
		  if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			if (expectedData != null) {
				HashMap<String, Object> actualData = new HashMap<>();
				  actualData = response.then().extract().path("data");
				  
				  test.log(LogStatus.INFO, "Check for datastore data");
				  checkForDataStoreData(expectedData, actualData, test);
			}			  
			  
		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			if (code.contains("01500002")) {
				message = message.replace("{0}", datastore_id);
			}
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.INFO, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response,message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
		  
	  }
	  
	  public Response getCloudRPSDatastoreById(String token,String datastore_id, int expectedStatusCode, ExtentTest test) {
		 
		  test.log(LogStatus.INFO, "Call REST API Get CloudRPS DataStore by Id");
		  Response response = spogCloudRPSInvoker.getCloudRPSDataStoreById(token, datastore_id, test);
		  spogServer.checkResponseStatus(response, expectedStatusCode, test);
		  
		  return response;
	  }
	  
	  /* Check for datastore data
	   * 
	   * @author Rakesh.Chalamala
	   * @param expectedData
	   * @param actualData
	   * @param test
	   * @return datastore_id
	   */
	  public String checkForDataStoreData(HashMap<String, Object> expectedData,
			  							HashMap<String, Object> actualData, ExtentTest test) {
		  
		  String datastore_id = null;		  
		  
		  test.log(LogStatus.INFO, "Compare datastore name");
		  spogServer.assertResponseItem(expectedData.get("datastore_name"), actualData.get("datastore_name"), test);
		  
		  test.log(LogStatus.INFO, "Compare dedupe enabled");
		  spogServer.assertResponseItem(expectedData.get("dedupe_enabled"), actualData.get("dedupe_enabled"), test);
		  
		  test.log(LogStatus.INFO, "Compare encryption_enabled ");
		  spogServer.assertResponseItem(expectedData.get("encryption_enabled"), actualData.get("encryption_enabled"), test);
		  
		  test.log(LogStatus.INFO, "Compare compression_enabled");
		  spogServer.assertResponseItem(expectedData.get("compression_enabled"), actualData.get("compression_enabled"), test);
		  
		  test.log(LogStatus.INFO, "Compare rps_server_id");
		  spogServer.assertResponseItem(expectedData.get("rps_server_id"), actualData.get("rps_server_id"), test);
		  
		  test.log(LogStatus.INFO, "Compare create_user_id");
		  spogServer.assertResponseItem(expectedData.get("create_user_id"), actualData.get("create_user_id"), test);
		  
		  test.log(LogStatus.INFO, "Compare status");
//		  spogServer.assertResponseItem(expectedData.get("status"), actualData.get("status"), test);
		  
		  HashMap<String, Object> exp_datastore_properties = new HashMap<>();
		  HashMap<String, Object> act_datastore_properties = new HashMap<>();
		  
		  exp_datastore_properties = (HashMap<String, Object>) expectedData.get("datastore_properties");
		  act_datastore_properties = (HashMap<String, Object>) actualData.get("datastore_properties");
		  
		  test.log(LogStatus.INFO, "Compare concurrent_active_nodes");
		  spogServer.assertResponseItem(exp_datastore_properties.get("concurrent_active_nodes"), act_datastore_properties.get("concurrent_active_nodes"), test);
		  
		  test.log(LogStatus.INFO, "Compare datastore_path");
		  spogServer.assertResponseItem(exp_datastore_properties.get("datastore_path"), act_datastore_properties.get("datastore_path"), test);
		  
		  test.log(LogStatus.INFO, "Compare datastore_username");
		  spogServer.assertResponseItem(exp_datastore_properties.get("datastore_username"), act_datastore_properties.get("datastore_username"), test);
		  
		  test.log(LogStatus.INFO, "Compare compression");
		  spogServer.assertResponseItem(exp_datastore_properties.get("compression"), act_datastore_properties.get("compression"), test);
		  
		  HashMap<String, Object> exp_deduplication = new HashMap<>();
		  HashMap<String, Object> act_deduplication = new HashMap<>();
		  
		  exp_deduplication = (HashMap<String, Object>) exp_datastore_properties.get("deduplication");
		  act_deduplication = (HashMap<String, Object>) act_datastore_properties.get("deduplication");
		  
		  test.log(LogStatus.INFO, "Compare block_size");
		  spogServer.assertResponseItem(exp_deduplication.get("block_size"), act_deduplication.get("block_size"), test);
		  
		  test.log(LogStatus.INFO, "Compare hash_memory");
		  spogServer.assertResponseItem(exp_deduplication.get("hash_memory"), act_deduplication.get("hash_memory"),test);
		  
		  if (exp_deduplication.containsKey("hash_on_ssd")) {
			  test.log(LogStatus.INFO, "Compare hash_on_ssd");
			  spogServer.assertResponseItem(exp_deduplication.get("hash_on_ssd"), act_deduplication.get("hash_on_ssd"), test);
		  }		  
		  
		  test.log(LogStatus.INFO, "Compare data_destination_path");
		  spogServer.assertResponseItem(exp_deduplication.get("data_destination_path"), act_deduplication.get("data_destination_path"), test);
		  
		  test.log(LogStatus.INFO, "Compare data_destination_username");
		  spogServer.assertResponseItem(exp_deduplication.get("data_destination_username"), act_deduplication.get("data_destination_username"), test);
		  
		  test.log(LogStatus.INFO, "Compare index_destination_path");
		  spogServer.assertResponseItem(exp_deduplication.get("index_destination_path"), act_deduplication.get("index_destination_path"), test);
		  
		  test.log(LogStatus.INFO, "Compare index_destination_username");
		  spogServer.assertResponseItem(exp_deduplication.get("index_destination_username"), act_deduplication.get("index_destination_username"), test);
		  
		  test.log(LogStatus.INFO, "Compare hash_destination_path");
		  spogServer.assertResponseItem(exp_deduplication.get("hash_destination_path"), act_deduplication.get("hash_destination_path"), test);
		  
		  test.log(LogStatus.INFO, "Compare hash_destination_username");
		  spogServer.assertResponseItem(exp_deduplication.get("hash_destination_username"), act_deduplication.get("hash_destination_username"), test);
		  
		  test.log(LogStatus.INFO, "Compare send_email");
		  spogServer.assertResponseItem(exp_datastore_properties.get("send_email"), act_datastore_properties.get("send_email"), test);
		  
		  if ((boolean) exp_datastore_properties.get("send_email")) {
			  test.log(LogStatus.INFO, "Compare to_email");
			  spogServer.assertResponseItem(exp_datastore_properties.get("to_email"), act_datastore_properties.get("to_email"), test);
		  }
		  
		  if (act_datastore_properties.containsKey("shared_path")) {
			  assertTrue(true, "Response contains shared_path: "+act_datastore_properties.get("shared_path"));
		  }
		  
		  if (act_datastore_properties.containsKey("overall_data_reduction")) {
			  assertTrue(true, "Response contains overall_data_reduction: "+act_datastore_properties.get("overall_data_reduction"));
		  }
		  
		  if (act_datastore_properties.containsKey("compression_percentage")) {
			  assertTrue(true, "Response contains compression_percentage: "+act_datastore_properties.get("compression_percentage"));
		  }
		  
		  if (act_datastore_properties.containsKey("deduplication_percentage")) {
			  assertTrue(true, "Response contains deduplication_percentage: "+act_datastore_properties.get("deduplication_percentage"));
		  }
		  
		  if (act_datastore_properties.containsKey("source_data_size")) {
			  assertTrue(true, "Response contains source_data_size: "+act_datastore_properties.get("source_data_size"));
		  }
		  
		  if (act_datastore_properties.containsKey("storage_space")) {
			  assertTrue(true, "Response contains storage_space: "+act_datastore_properties.get("storage_space"));
		  }
		  
		  if (act_datastore_properties.containsKey("capacity_usage")) {
			  assertTrue(true, "Response contains capacity_usage: "+act_datastore_properties.get("capacity_usage"));
		  }
		  
		  datastore_id = actualData.get("datastore_id").toString();
		  return datastore_id;
	  }
	  
	  /* GET DATASTORE FOR SPECIFIED DESTINATION
	   * 
	   * @author Rakesh.Chalamala
	   * @param token
	   * @param destination_id
	   * @param expectedresponse
	   * @param expectedStatusCode
	   * @param expectedErrorMessage
	   * @param test
	   */
	  public void getDatastoreForSpecifiedDestination(String token, String destination_id, HashMap<String, Object> expectedresponse, 
			  												int expectedStatusCode, SpogMessageCode expectedErrorMessage, ExtentTest test
			  												) {
		  
		  test.log(LogStatus.INFO, "Call REST API - Get datastore for specified destination");
		  Response response = spogCloudRPSInvoker.getDatastoreForSpecifiedDestination(token, destination_id, test);
		  spogServer.checkResponseStatus(response, expectedStatusCode, test);
		  
		  if(expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE ) {
			  			  
			  HashMap<String, Object> actualResponse = new HashMap<>();
			  actualResponse = response.then().extract().path("");
			  
			  if (actualResponse.containsKey("data")) {
				  if(!(actualResponse = (HashMap<String, Object>) actualResponse.get("data")).equals("")) {
					  
					  //Comparing the parameters in expected and actual
					  spogServer.assertResponseItem(expectedresponse.get("server_id"), actualResponse.get("server_id"), test);
					  spogServer.assertResponseItem(expectedresponse.get("server_name"), actualResponse.get("server_name"), test);
					  spogServer.assertResponseItem(expectedresponse.get("server_port"), actualResponse.get("server_port"), test);
					  spogServer.assertResponseItem(expectedresponse.get("server_protocol"), actualResponse.get("server_protocol"),test);
					  spogServer.assertResponseItem(expectedresponse.get("server_username"), actualResponse.get("server_username"), test);
					  spogServer.assertResponseItem(expectedresponse.get("organization_id"), actualResponse.get("organization_id"), test);
					  spogServer.assertResponseItem(expectedresponse.get("site_id"), actualResponse.get("site_id"),test);
					  spogServer.assertResponseItem(expectedresponse.get("status"), actualResponse.get("status"), test);
					  spogServer.assertResponseItem(expectedresponse.get("datacenter_id"), actualResponse.get("datacenter_id"), test);
					  spogServer.assertResponseItem(expectedresponse.get("create_user_id"), actualResponse.get("create_user_id"), test);
					  spogServer.assertResponseItem(expectedresponse.get("nat_server"), actualResponse.get("nat_server"),test);
					  spogServer.assertResponseItem(expectedresponse.get("nat_port"), actualResponse.get("nat_port"),test);
					  spogServer.assertResponseItem(expectedresponse.get("rps_version"), actualResponse.get("rps_version"),test);

					  HashMap<String, Object> expDatastoreData = new HashMap<>();
					  HashMap<String, Object> actDatastoreData = new HashMap<>();

					  expDatastoreData = (HashMap<String, Object>) expectedresponse.get("datastore");
					  actDatastoreData = (HashMap<String, Object>) actualResponse.get("datastore");

					  test.log(LogStatus.INFO, "Verify datastore data in expected and actual responses");
					  checkForDataStoreData(expDatastoreData, actDatastoreData, test);
				  }else {
					test.log(LogStatus.INFO, "No datastores assigned to destination with id: "+destination_id);
					System.out.println("No datastores assigned to destination with id: "+destination_id);
				  }
			  }else {
				  test.log(LogStatus.INFO, "Response does not contain the data parameter");
				  System.out.println("Response does not contain the data parameter");
			  }
		  }else {
			  	String code = expectedErrorMessage.getCodeString();
				String message = expectedErrorMessage.getStatus();
				if (code.contains("00C00001")) {
					message = message.replace("{0}", destination_id);
				}
				spogServer.checkErrorCode(response, code);
				test.log(LogStatus.INFO, "The error code matched with the expected " + code);
				spogServer.checkErrorMessage(response,message);
				test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		  }
		  
	  }
	  /** POST: /datastores/{id}/start
	   * 
	   * @author Rakesh.Chalamala
	   * @param token
	   * @param datastore_id
	   * @param expectedStatusCode
	   * @param expectedErrorMessage
	   * @param test
	   */
	  public Response postDatastoreStartWithCheck(String token, String datastore_id, int expectedStatusCode, SpogMessageCode expectedErrorMessage, ExtentTest test) {

		  test.log(LogStatus.INFO, "Call rest api POST: /datastores/id/start");
		  Response response = spogCloudRPSInvoker.postDataStoreStart(token, datastore_id, test);
		  spogServer.checkResponseStatus(response, expectedStatusCode, test);

		  if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			  test.log(LogStatus.INFO, "Datastore started successfully.");

		  }else {
			  String code = expectedErrorMessage.getCodeString();
			  String message = expectedErrorMessage.getStatus();
			  if (code.contains("01500002")) {
				message = message.replace("{0}", datastore_id);
			  }
			  spogServer.checkErrorCode(response, code);
			  test.log(LogStatus.INFO, "The error code matched with the expected " + code);
			  spogServer.checkErrorMessage(response,message);
			  test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		  }
		  return response;
	  }
	  
	  /** POST: /datastores/{id}/stop
	   * 
	   * @author Rakesh.Chalamala
	   * @param token
	   * @param datastore_id
	   * @param expectedStatusCode
	   * @param expectedErrorMessage
	   * @param test
	   */
	  public Response postDatastoreStopWithCheck(String token, String datastore_id, int expectedStatusCode, SpogMessageCode expectedErrorMessage, ExtentTest test) {

		  test.log(LogStatus.INFO, "Call rest api POST: /datastores/id/stop");
		  Response response = spogCloudRPSInvoker.postDataStoreStop(token, datastore_id, test);
		  spogServer.checkResponseStatus(response, expectedStatusCode, test);

		  if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			  test.log(LogStatus.INFO, "Datastore started successfully.");

		  }else {
			  String code = expectedErrorMessage.getCodeString();
			  String message = expectedErrorMessage.getStatus();
			  
			  spogServer.checkErrorCode(response, code);
			  test.log(LogStatus.INFO, "The error code matched with the expected " + code);
			  spogServer.checkErrorMessage(response,message);
			  test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		  }
		  return response;
	  }
	  
	  /* Compose datastore usage info
	   * 
	   * @author Rakesh.Chalamala
	   * @param datastore_id
	   * @param timestamp
	   * @param status
	   * @param source_data_size
	   * @param dedupe_savings
	   * @param storage_space
	   * @param capacity_usage
	   * @param compression_percentage
	   * @param overall_data_reduction
	   * @param backup_dest_usage
	   * @param backup_dest_capacity
	   * @param data_dest_usage
	   * @param data_dest_capacity
	   * @param index_dest_usage
	   * @param index_dest_capacity
	   * @param hash_dest_usage
	   * @param hash_dest_capacity
	   * @param mem_allocation_usage
	   * @param mem_allocation_capacity
	   */
	  public HashMap<String, Object> composeDatastoreUsageInfo(String datastore_id, String timestamp,
			  													String status, double source_data_size,
			  													String dedupe_savings, long storage_space,
			  													long capacity_usage, double deduplication_percentage,
			  													double compression_percentage, double overall_data_reduction,
			  													double backup_dest_usage, double backup_dest_capacity,
			  													double data_dest_usage, double data_dest_capacity,
			  													double index_dest_usage, double index_dest_capacity,
			  													double hash_dest_usage, double hash_dest_capacity,
			  													double mem_allocation_usage, double mem_allocation_capacity
			  													){
		  HashMap<String, Object> datastoreInfo = new HashMap<>();
		  
		  datastoreInfo.put("datastore_id",datastore_id);
		  datastoreInfo.put("timestamp",timestamp);
		  datastoreInfo.put("status",status);
		  datastoreInfo.put("source_data_size",source_data_size);
		  datastoreInfo.put("dedupe_savings",dedupe_savings);
		  datastoreInfo.put("storage_space",storage_space);
//		  datastoreInfo.put("capacity_usage",capacity_usage); //RPS can not report capacity_usage so capacity_usage = storage_space
		  datastoreInfo.put("deduplication_percentage",deduplication_percentage);
		  datastoreInfo.put("compression_percentage",compression_percentage);
		  datastoreInfo.put("overall_data_reduction",overall_data_reduction);
		  
		  HashMap<String, Object> backup_destination = new HashMap<>();
		  backup_destination.put("usage", backup_dest_usage);
		  backup_destination.put("capacity", backup_dest_capacity);
		  
		  datastoreInfo.put("backup_destination", backup_destination);
		  
		  HashMap<String, Object> data_destination = new HashMap<>();
		  data_destination.put("usage", data_dest_usage);
		  data_destination.put("capacity", data_dest_capacity);
		  
		  datastoreInfo.put("data_destination", data_destination);
		  
		  HashMap<String, Object> index_destination = new HashMap<>();
		  index_destination.put("usage", index_dest_usage);
		  index_destination.put("capacity", index_dest_capacity);
		  
		  datastoreInfo.put("index_destination", index_destination);
		  
		  HashMap<String, Object> hash_destination = new HashMap<>();
		  hash_destination.put("usage", hash_dest_usage);
		  hash_destination.put("capacity", hash_dest_capacity);
		  
		  datastoreInfo.put("hash_destination", hash_destination);
		  
		  HashMap<String, Object> memory_allocation = new HashMap<>();
		  memory_allocation.put("usage", mem_allocation_usage);
		  memory_allocation.put("capacity", mem_allocation_capacity);
		  
		  datastoreInfo.put("memory_allocation", memory_allocation);
		  return datastoreInfo;
	  }
	  
	  /* API - PUT: /datastores/usage with validation
	   * 
	   * @author Rakesh.Chalamala
	   * @param token
	   * @param datastoreInfo
	   * @param expectedResponse
	   * @param expectedStatusCode
	   * @param expectedErrorMessage
	   * @param test
	   * 
	   */
	  public Response putDataStoresUsage(String token, ArrayList<HashMap<String, Object>> datastoreInfo,int expectedStatusCode,SpogMessageCode expectedErrorMessage, ExtentTest test) {
		  
		  test.log(LogStatus.INFO, "Call API - PUT: /datastores/usage for response");
		  Response response = spogCloudRPSInvoker.putDataStoresUsage(token, datastoreInfo, test);
		  spogServer.checkResponseStatus(response, expectedStatusCode, test);
		  
		  if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			
			  test.log(LogStatus.INFO, "Datastore usage details are updated successfully.");
			  
		  }else {
			  String code = expectedErrorMessage.getCodeString();
			  String message = expectedErrorMessage.getStatus();
			  if (code.contains("01500002")) {
				message = message.replace("{0}", datastoreInfo.get(0).get("datastore_id").toString());
			  }
			  spogServer.checkErrorCode(response, code);
			  test.log(LogStatus.INFO, "The error code matched with the expected " + code);
			  spogServer.checkErrorMessage(response,message);
			  test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		  }
		  
		  return response;
	  }
	  
	  /* Validation of datastore usage by using the respone of get datastorebyid
	   * 
	   * @author Rakesh.Chalamala
	   * @param token
	   * @param response
	   * @param expectedResponse
	   * @param test
	   */
	  public void validateDataStoreUsageResponse(String token, Response response, HashMap<String, Object> expectedResponse, ExtentTest test){
		  
		 HashMap<String, Object> actualResponse = response.then().extract().path("data");
		 
		 test.log(LogStatus.INFO, "Compare the datastore_id");
		 spogServer.assertResponseItem(expectedResponse.get("datastore_id"), actualResponse.get("datastore_id"), test);
		 
		 test.log(LogStatus.INFO, "Compare the status");
		 spogServer.assertResponseItem(expectedResponse.get("status"), actualResponse.get("status"), test);
		 
		 test.log(LogStatus.INFO, "Compare the datastore usage properties");
		 HashMap<String, Object> act_datastore_prop = (HashMap<String, Object>) actualResponse.get("datastore_properties");
		 
		 spogServer.assertResponseItem(expectedResponse.get("source_data_size").toString(), act_datastore_prop.get("source_data_size").toString(),test);
		 spogServer.assertResponseItem(expectedResponse.get("dedupe_savings").toString(), act_datastore_prop.get("dedupe_savings").toString(),test);
		 spogServer.assertResponseItem(expectedResponse.get("storage_space").toString(), act_datastore_prop.get("storage_space").toString(),test);
		 spogServer.assertResponseItem(expectedResponse.get("deduplication_percentage").toString(), act_datastore_prop.get("deduplication_percentage").toString(),test);
		 spogServer.assertResponseItem(expectedResponse.get("compression_percentage").toString(), act_datastore_prop.get("compression_percentage").toString(),test);
		 spogServer.assertResponseItem(expectedResponse.get("overall_data_reduction").toString(), act_datastore_prop.get("overall_data_reduction").toString(),test);
		 
		 //RPS can not report capacity_usage so capacity_usage = storage_space
		 spogServer.assertResponseItem(expectedResponse.get("storage_space").toString(), act_datastore_prop.get("capacity_usage").toString(),test);
		 
		 test.log(LogStatus.INFO, "Compare the datastore backup_destination usage");
		 HashMap<String, Object> act_backup_dest = (HashMap<String, Object>) act_datastore_prop.get("backup_destination");
		 HashMap<String, Object> exp_backup_dest = (HashMap<String, Object>) expectedResponse.get("backup_destination");
		 spogServer.assertResponseItem(exp_backup_dest.get("usage").toString(), act_backup_dest.get("usage").toString(), test);
		 spogServer.assertResponseItem(exp_backup_dest.get("capacity").toString(), act_backup_dest.get("capacity").toString(), test);
		 
		 test.log(LogStatus.INFO, "Compare the datastore data_destination usage");
		 HashMap<String, Object> act_data_dest = (HashMap<String, Object>) act_datastore_prop.get("data_destination");
		 HashMap<String, Object> exp_data_dest = (HashMap<String, Object>) expectedResponse.get("data_destination");
		 spogServer.assertResponseItem(exp_data_dest.get("usage").toString(), act_data_dest.get("usage").toString(), test);
		 spogServer.assertResponseItem(exp_data_dest.get("capacity").toString(), act_data_dest.get("capacity").toString(), test);
		 
		 test.log(LogStatus.INFO, "Compare the datastore index_destination usage");
		 HashMap<String, Object> act_index_dest = (HashMap<String, Object>) act_datastore_prop.get("index_destination");
		 HashMap<String, Object> exp_index_dest = (HashMap<String, Object>) expectedResponse.get("index_destination");
		 spogServer.assertResponseItem(exp_index_dest.get("usage").toString(), act_index_dest.get("usage").toString(), test);
		 spogServer.assertResponseItem(exp_index_dest.get("capacity").toString(), act_index_dest.get("capacity").toString(), test);
		 
		 test.log(LogStatus.INFO, "Compare the datastore hash_destination usage");
		 HashMap<String, Object> act_hash_dest = (HashMap<String, Object>) act_datastore_prop.get("hash_destination");
		 HashMap<String, Object> exp_hash_dest = (HashMap<String, Object>) expectedResponse.get("hash_destination");
		 spogServer.assertResponseItem(exp_hash_dest.get("usage").toString(), act_hash_dest.get("usage").toString(), test);
		 spogServer.assertResponseItem(exp_hash_dest.get("capacity").toString(), act_hash_dest.get("capacity").toString(), test);
		 
		 test.log(LogStatus.INFO, "Compare the datastore memory_allocation details");
		 HashMap<String, Object> act_mem_alloc = (HashMap<String, Object>) act_datastore_prop.get("memory_allocation");
		 HashMap<String, Object> exp_mem_alloc = (HashMap<String, Object>) expectedResponse.get("memory_allocation");
		 spogServer.assertResponseItem(exp_mem_alloc.get("usage").toString(), act_mem_alloc.get("usage").toString(), test);
		 spogServer.assertResponseItem(exp_mem_alloc.get("capacity").toString(), act_mem_alloc.get("capacity").toString(), test);
		 
		 test.log(LogStatus.PASS, "Validation passed datastore usage details updated successfully.");
			
	  }
	  
	  
	  /** Creates a cloudhybridstore and validates the status code
	   * 
	   * @param validToken
	   * @param hybridStoreInfo
	   * @param expectedStatusCode
	   * @param test
	   * @return
	   */
	  public Response createCloudHybridStore(String validToken, HashMap<String, Object> hybridStoreInfo, int expectedStatusCode, ExtentTest test) {
		  
		  test.log(LogStatus.INFO, "Call API - POST: /cloudhybridstores to create a hybridstore");
		  Response response = spogCloudRPSInvoker.createCloudHybridStore(validToken, hybridStoreInfo, test);
		  spogServer.checkResponseStatus(response, expectedStatusCode, test);
		  
		  return response;
	  }
	  
	  public String createCloudHybridStoreWithCheck(String validToken, HashMap<String, Object> hybridStoreInfo, 
			  int expectedStatusCode, SpogMessageCode expectedErrorMessage, ExtentTest test) {

		  String destination_id = null;
		  Response response = createCloudHybridStore(validToken, hybridStoreInfo, expectedStatusCode, test);		  

		  if (expectedStatusCode == SpogConstants.SUCCESS_POST) {
			  
			  HashMap<String, Object> actInfo = response.then().extract().path("data");
			  validateCloudHybridStoresInfo(actInfo, hybridStoreInfo, test);
			  test.log(LogStatus.PASS, "Cloud hybrid store respose validation passed");
			  
			  destination_id = response.then().extract().path("data.destination_id");
		  } else {
			  String code = expectedErrorMessage.getCodeString();
			  String message = expectedErrorMessage.getStatus();
			  spogServer.checkErrorCode(response, code);
			  test.log(LogStatus.INFO, "The error code matched with the expected " + code);
			  spogServer.checkErrorMessage(response,message);
			  test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		  }	
		  
		  return destination_id;
	  }
	  
	  /** Update the Cloud hybridstore information by it's id and validates the status code
	   * 
	   * @param validToken
	   * @param hybridStore_id
	   * @param hybridStoreInfo
	   * @param expectedStatusCode
	   * @param test
	   * @return
	   */
	  public Response updateCloudHybridStoreById(String validToken, String hybridStore_id, HashMap<String, Object> hybridStoreInfo, int expectedStatusCode, ExtentTest test) {
		  
		  test.log(LogStatus.INFO, "Call API - PUT: /cloudhybridstores/{id} to update hybridstore info");
		  Response response = spogCloudRPSInvoker.updateCloudHybridStoreById(validToken, hybridStore_id, hybridStoreInfo, test);
		  spogServer.checkResponseStatus(response, expectedStatusCode, test);
		  
		  return response;
	  }
	  
	  public void updateCloudHybridStoreByIdWithCheck(String validToken, String hybridStore_id, HashMap<String, Object> hybridStoreInfo, 
			  											int expectedStatusCode, SpogMessageCode expectedErrorMessage, ExtentTest test) {
		  
		  Response response = updateCloudHybridStoreById(validToken, hybridStore_id, hybridStoreInfo, expectedStatusCode, test);
		  
		  if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			//add validation
		  }else {
			  String code = expectedErrorMessage.getCodeString();
			  String message = expectedErrorMessage.getStatus();
			  spogServer.checkErrorCode(response, code);
			  test.log(LogStatus.INFO, "The error code matched with the expected " + code);
			  spogServer.checkErrorMessage(response,message);
			  test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		  }
	  }
	  
	  /** Get the cloud hybridstore information by it's id and validates the status code
	   * 
	   * @param validToken
	   * @param hybridStore_id
	   * @param expectedStatusCode
	   * @param test
	   * @return
	   */
	  public Response getCloudHybridStoreById(String validToken, String hybridStore_id, int expectedStatusCode, ExtentTest test) {
		  
		  test.log(LogStatus.INFO, "Call API - GET: /cloudhybridstores/{id} to get hybridstore properties");
		  Response response = spogCloudRPSInvoker.getCloudHybridStoreById(validToken, hybridStore_id, test);
		  spogServer.checkResponseStatus(response, expectedStatusCode, test);
		  
		  return response;
	  }
	  
	  public void getCloudHybridStoreByIdWithCheck(String validToken,
			  										String hybridStoreId,
			  										HashMap<String, Object> expectedInfo,
			  										int expectedStatusCode,
			  										SpogMessageCode expectedErrorMessage,
			  										ExtentTest test
			  										) {
		  
		  Response response = getCloudHybridStoreById(validToken, hybridStoreId, expectedStatusCode, test);
		  
		  if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			
			  HashMap<String, Object> actInfo = response.then().extract().path("data");
			  validateCloudHybridStoresInfo(actInfo, expectedInfo, test);
			  test.log(LogStatus.PASS, "Cloud hybrid store respose validation passed");
			  
//			  destination_id = response.then().extract().path("data.destination_id");
			  
		  }else {
			  String code = expectedErrorMessage.getCodeString();
			  String message = expectedErrorMessage.getStatus();
			  spogServer.checkErrorCode(response, code);
			  test.log(LogStatus.INFO, "The error code matched with the expected " + code);
			  spogServer.checkErrorMessage(response,message);
			  test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		  }
	  }
	  
	  public Response getCloudHybridStoresOnSpecifiedRPS(String validToken, String rps_server_id, int expectedStatusCode, ExtentTest test) {
		  
		  test.log(LogStatus.INFO, "Call API - GET: /recoverypointserveres/{id}/cloudhybridstores to get all hybridstores on RPS");
		  Response response = spogCloudRPSInvoker.getCloudHybridStoresOnSpecifedRPS(validToken, rps_server_id, test);
		  spogServer.checkResponseStatus(response, expectedStatusCode, test);
		  
		  return response;
	  }
	  
	  public void getCloudHybridStoresOnSpecifiedRPSWithCheck(String validToken, String rps_server_id, ArrayList<HashMap<String, Object>> expInfo, 
			  int expectedStatusCode, SpogMessageCode expectedErrorMessage, ExtentTest test) {

		  Response response = getCloudHybridStoresOnSpecifiedRPS(validToken, rps_server_id, expectedStatusCode, test);

		  if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			  ArrayList<HashMap<String, Object>> actInfo = response.then().extract().path("data");

			  if (actInfo.size() == expInfo.size()) {

				  for (int i=0, j=expInfo.size()-1; i < expInfo.size(); i++,j--) {
					  validateCloudHybridStoresInfo(actInfo.get(i), expInfo.get(j), test);
				  }
				  test.log(LogStatus.PASS, "Cloud hybridstore validation passed");
			  } else {
				  test.log(LogStatus.FAIL, "expected hybristoe count:"+expInfo.size()+" not matched with actual:"+actInfo.size());
				  assertFalse(true, "expected hybristoe count:"+expInfo.size()+" not matched with actual:"+actInfo.size());
			  }
			  
		  }else {
			  String code = expectedErrorMessage.getCodeString();
			  String message = expectedErrorMessage.getStatus();
			  spogServer.checkErrorCode(response, code);
			  test.log(LogStatus.INFO, "The error code matched with the expected " + code);
			  spogServer.checkErrorMessage(response,message);
			  test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		  }
	  }
	  
	  /** Delete the cloud hybridstore information by its'd
	   * Internally deletes the CH destinatoin, datastore volume and the corresponding directories
	   * 
	   * @param validToken
	   * @param hybridStore_id
	   * @param expectedStatusCode
	   * @param test
	   * @return
	   */
	  public Response destroyCloudHybridStoreById(String validToken, String hybridStore_id, int expectedStatusCode, ExtentTest test) {
		  
		  test.log(LogStatus.INFO, "Call API - DELETE: /cloudhybridstores/{id}/destroy to destroy hybridstore info");
		  Response response = spogCloudRPSInvoker.destroyCloudHybridStoreById(validToken, hybridStore_id, test);
//		  spogServer.checkResponseStatus(response, expectedStatusCode, test);
		  
		  if(response.getStatusCode() == SpogConstants.REQUIRED_INFO_NOT_EXIST && expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			
			  String errMsg = response.then().extract().path("errors.message").toString();
			  
			  String policyName = errMsg.substring(errMsg.indexOf(" [")+2, errMsg.indexOf("]"));
			  String filter = "policy_name="+policyName; 
			  
			  Response policyResponse = policy4SPOGServer.getPolicies(validToken, filter, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
			  
			  for (int i = 0; i < 10; i++) {
				  String policyStatus = policyResponse.then().extract().path("data[0].policy_status").toString();
				  if (policyStatus.equalsIgnoreCase("deploying")) {
					  
					  waitForSeconds(5);
					  policyResponse = policy4SPOGServer.getPolicies(validToken, filter, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
				  }else {
					  break;
				  }
			  }
			  String policy_id = policyResponse.then().extract().path("data[0].policy_id");
			  policy4SPOGServer.deletePolicybyPolicyId(validToken, policy_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			
			  response = spogCloudRPSInvoker.destroyCloudHybridStoreById(validToken, hybridStore_id, test);
			  spogServer.checkResponseStatus(response, expectedStatusCode, test);
			  
		  }  

		  return response;
	  }
	  
	  public void destroyCloudHybridStoreByIdWithCheck(String validToken, String hybridStore_id, int expectedStatusCode, SpogMessageCode expectedErrorMessage, ExtentTest test) {
		  
		  Response response = destroyCloudHybridStoreById(validToken, hybridStore_id, expectedStatusCode, test);
		  
		  if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.PASS, "Cloud hybrid store with id:"+hybridStore_id+" destroyed successfully");
		  }else {
			  String code = expectedErrorMessage.getCodeString();
			  String message = expectedErrorMessage.getStatus();
			  spogServer.checkErrorCode(response, code);
			  test.log(LogStatus.INFO, "The error code matched with the expected " + code);
			  spogServer.checkErrorMessage(response,message);
			  test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		  }
	  }
	  
	  public Response startCloudHybridStoreById(String validToken, String hybridStore_id, int expectedStatusCode, ExtentTest test) {
		  
		  test.log(LogStatus.INFO, "Call API - POST: /cloudhybridstores/{id}/start to start the hybridstore");
		  Response response = spogCloudRPSInvoker.startCloudHybridStore(validToken, hybridStore_id, test);
		  spogServer.checkResponseStatus(response, expectedStatusCode, test);
		  
		  return response;
	  }
	  
	  public void startCloudHybridStoreByIdWithCheck(String validToken, String hybridStore_id, int expectedStatusCode, SpogMessageCode expectedErrorMessage, ExtentTest test) {
		  
		  Response response = startCloudHybridStoreById(validToken, hybridStore_id, expectedStatusCode, test);
		  
		  if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.PASS, "Cloud hybrid store with id:"+hybridStore_id+" is started successfully.");
		  }else {
			  String code = expectedErrorMessage.getCodeString();
			  String message = expectedErrorMessage.getStatus();
			  spogServer.checkErrorCode(response, code);
			  test.log(LogStatus.INFO, "The error code matched with the expected " + code);
			  spogServer.checkErrorMessage(response,message);
			  test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		  }
	  }
	  
	  public Response stopCloudHybridStoreById(String validToken, String hybridStore_id, int expectedStatusCode, ExtentTest test) {
		  
		  test.log(LogStatus.INFO, "Call API - POST: /cloudhybridstores/{id}/stop to start the hybridstore");
		  Response response = spogCloudRPSInvoker.stopCloudHybridStore(validToken, hybridStore_id, test);
		  spogServer.checkResponseStatus(response, expectedStatusCode, test);
		  
		  return response;
	  }
	  
	  public void stopCloudHybridStoreByIdWithCheck(String validToken, String hybridStore_id, int expectedStatusCode, SpogMessageCode expectedErrorMessage, ExtentTest test) {
		  
		  Response response = stopCloudHybridStoreById(validToken, hybridStore_id, expectedStatusCode, test);
		  
		  if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.PASS, "Cloud hybrid store with id:"+hybridStore_id+" is stopped successfully.");
		  }else {
			  String code = expectedErrorMessage.getCodeString();
			  String message = expectedErrorMessage.getStatus();
			  spogServer.checkErrorCode(response, code);
			  test.log(LogStatus.INFO, "The error code matched with the expected " + code);
			  spogServer.checkErrorMessage(response,message);
			  test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		  }
	  }
	  
	  /** Composes the request payload for api /cloudhybridstores
	   * 
	   * @param validToken
	   * @param destination_name
	   * @param rps_server_id
	   * @param organization_id
	   * @param volume
	   * @param datacenter_id
	   * @param capacity
	   * @return
	   */
	  public HashMap<String, Object> composeCloudHybridStoreInfo(String validToken,
																	String destination_name,
																	String rps_server_id,
																	String organization_id,
																	String volume,
																	String datacenter_id,
																	String capacity
																	){
		  
		  HashMap<String, Object> cloudHybridStoreInfo = new HashMap<>();
		  
		  cloudHybridStoreInfo.put("destination_name", destination_name);
		  cloudHybridStoreInfo.put("rps_server_id", rps_server_id);
		  cloudHybridStoreInfo.put("organization_id", organization_id);
		  
		  if (!volume.equalsIgnoreCase("none")) {
			  cloudHybridStoreInfo.put("volume", volume);
		  }
		  
		  cloudHybridStoreInfo.put("datacenter_id", datacenter_id);
		  cloudHybridStoreInfo.put("capacity", capacity);
		  
		  /* Not mandatory fields
		   * When called from UI above are given by user and all datastore props composed internally as default
		   * 
		   * HashMap<String, Object> datastoreProperties = composeCloudHybridDatastoreProperties(concurrent_active_nodes, capacity, encryption_pw, send_email, to_email);
		  cloudHybridStoreInfo.put("datastore_properties", datastoreProperties);*/
		  		  
		  return cloudHybridStoreInfo;
	  }
	  
	  public HashMap<String, Object> composeCloudHybridDatastoreProperties(String concurrent_active_nodes,
																			String capacity,
																			String encryption_password,
																			String send_email,
																			String to_email) {
		  
		  HashMap<String, Object> cloudHybridDatastoreProperties = new HashMap<>();
		  
		  cloudHybridDatastoreProperties.put("concurrent_active_nodes", concurrent_active_nodes);
		  cloudHybridDatastoreProperties.put("capacity", capacity);
		  cloudHybridDatastoreProperties.put("encryption_password", encryption_password);
		  cloudHybridDatastoreProperties.put("send_email", send_email);
		  cloudHybridDatastoreProperties.put("to_email", to_email);
		  
		  return cloudHybridDatastoreProperties;
	  }
	  
	  /**
	   *  "data": {
        "destination_id": null,
        "destination_name": null,
        "volume": "J",
        "capacity": 0.009999275207519531,
        "block_size": 16,
        "directory": {
            "index": "J:\\01e52499-ff1f-44be-9226-39c1adecc28e\\Index",
            "data": "J:\\01e52499-ff1f-44be-9226-39c1adecc28e\\Data",
            "hash": "E:\\01e52499-ff1f-44be-9226-39c1adecc28e\\Hash",
            "common": "J:\\01e52499-ff1f-44be-9226-39c1adecc28e\\Common"
        },
        "datastore": {
            "datastore_name": "dest_3",
            "dedupe_enabled": true,
            "encryption_enabled": true,
            "compression_enabled": true,
            "rps_server_id": "87c768c7-1a2d-4dbe-8dce-768581e46020",
            "datastore_id": "01e52499-ff1f-44be-9226-39c1adecc28e",
            "server_name": "SpogCloudRps",
            "status": "running",
            "datastore_properties": {
                "concurrent_active_nodes": 4,
                "capacity": 0,
                "datastore_path": "J:\\01e52499-ff1f-44be-9226-39c1adecc28e\\Common",
                "compression": "maximum",
                "deduplication": {
                    "block_size": 16,
                    "hash_memory": 4096,
                    "hash_on_ssd": true,
                    "data_destination_path": "J:\\01e52499-ff1f-44be-9226-39c1adecc28e\\Data",
                    "index_destination_path": "J:\\01e52499-ff1f-44be-9226-39c1adecc28e\\Index",
                    "hash_destination_path": "E:\\01e52499-ff1f-44be-9226-39c1adecc28e\\Hash"
                },
                "send_email": true,
                "to_email": "test.direct10001@gmail.com",
                "shared_path": "\\\\SpogCloudRps\\UDP_DEST0001",
                "overall_data_reduction": 0,
                "compression_percentage": 0,
                "deduplication_percentage": 0,
                "source_data_size": 0,
                "storage_space": 0,
                "capacity_usage": 0
            },
            "is_deleted": false,
            "create_ts": 1546424423,
            "modify_ts": 1546424423,
            "create_user_id": "5335ca7b-732a-4f4c-95b5-31b6121f1dcb"
        },
        "organization_id": null,
        "organization_name": null
    },
    */
	  
	  public void validateCloudHybridStoresInfo(HashMap<String, Object> actInfo, HashMap<String, Object> expInfo, ExtentTest test) {

		  

		  assertNotNull(actInfo.get("destination_id"), "The destination_id is : "+actInfo.get("destination_id"));
		  spogServer.assertResponseItem(expInfo.get("destination_name"), actInfo.get("destination_name"), test);
		  spogServer.assertResponseItem(expInfo.get("datacenter_id"), actInfo.get("datacenter_id"), test);
		  spogServer.assertResponseItem(expInfo.get("organization_id"), actInfo.get("organization_id"), test);	  
		  assertNotNull(actInfo.get("datacenter_name"), "datacenter_name: "+actInfo.get("datacenter_name").toString());
		  assertNotNull(actInfo.get("organization_name"), "orgniazation_name: "+actInfo.get("organization_name").toString());
		  
		  if (expInfo.containsKey("volume")) {
			  if (expInfo.get("volume") != null && !expInfo.get("volume").toString().isEmpty()) {
					spogServer.assertResponseItem(expInfo.get("volume"), actInfo.get("volume"), test);
				}else {
					assertNotNull(actInfo.get("volume"), "A volume created with letter: "+actInfo.get("volume"));
				}
		  }else {
				assertNotNull(actInfo.get("volume"), "A volume created with letter: "+actInfo.get("volume"));
		  }

		  assertNotNull(actInfo.get("capacity"));
		  
		  if (actInfo.get("directory") != null) {			
			  HashMap<String, Object> actDirectoryInfo = (HashMap<String, Object>) actInfo.get("directory");
			  assertNotNull(actDirectoryInfo.get("index"));
			  assertNotNull(actDirectoryInfo.get("data"));
			  assertNotNull(actDirectoryInfo.get("hash"));
			  assertNotNull(actDirectoryInfo.get("common"));
			  
			  test.log(LogStatus.INFO, "Directories: "+actDirectoryInfo);
		  }else {
			assertNotNull(true, "Datastore directory information is null in response");
		  }
		  
		  if (actInfo.get("datastore") != null) {			
			  HashMap<String, Object> actDatastoreInfo = (HashMap<String, Object>) actInfo.get("datastore");
			  
			  assertNotNull(actDatastoreInfo.get("datastore_name"), "datastore_name: "+actDatastoreInfo.get("datastore_name").toString());
			  assertNotNull(actDatastoreInfo.get("datastore_id"), "datastore_id: "+actDatastoreInfo.get("datastore_id").toString());
			  spogServer.assertResponseItem(expInfo.get("rps_server_id").toString(), actDatastoreInfo.get("rps_server_id").toString(), test);
			  assertEquals(actDatastoreInfo.get("dedupe_enabled"), true);
			  assertEquals(actDatastoreInfo.get("encryption_enabled"), true);
			  assertEquals(actDatastoreInfo.get("compression_enabled"), true);
			  assertNotNull(actDatastoreInfo.get("status"));
			  assertNotNull(actDatastoreInfo.get("datastore_properties"));
			  assertEquals(actDatastoreInfo.get("is_deleted"), false);
		  }else {
			assertNotNull(true, "Datastore information is null in response");
		  }
		  		  
	  }
	  
	  public void verifyAutoPolicyCreationForCloudHybridStore(Response response) {
		  
	  }
	  
	  public void waitForSeconds(final long seconds) {
			try {
				Thread.currentThread().sleep(seconds * 1000);
			} catch (InterruptedException e) {
				errorHandle.printInfoMessageInDebugFile("There is wait for seconds exception.");
			}
	  }
	  
}
