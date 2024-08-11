package InvokerServer;
import java.util.Iterator;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
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
import dataPreparation.JsonPreparation;
import genericutil.ErrorHandler;
import invoker.Linux4SPOGInvoker;
import invoker.SPOGDestinationInvoker;
import invoker.SPOGInvoker;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class Linux4SPOGServer {
	private SPOGServer spogServer;
	private Linux4SPOGInvoker linux4SPOGInvoker;
	
	public static ErrorHandler errorHandle = ErrorHandler.getErrorHandler();
	private ExtentTest test;

	public void setToken(String token) {
		linux4SPOGInvoker.setToken(token);
	}

	public Linux4SPOGServer(String baseURI, String port) {
		linux4SPOGInvoker = new Linux4SPOGInvoker(baseURI, port);
		spogServer = new SPOGServer(baseURI, port);
	}
	
	public static Map<String, Object> composeLinuxServerInfo(String server_id, String server_name, String server_protocol, String server_port, String server_username, 
															String server_password, String organization_id, String site_id, String status, String datacenter_id){
		Map<String, Object> linuxServerInfo = new HashMap<String, Object>();
		
		if (server_id == null) {
			linuxServerInfo.put("server_id", server_id);
		} else if (!server_id.equalsIgnoreCase("none")) {
			linuxServerInfo.put("server_id", server_id);
		}
		
		if (server_name == null) {
			linuxServerInfo.put("server_name", server_name);
		} else if (!server_name.equalsIgnoreCase("none")) {
			linuxServerInfo.put("server_name", server_name);
		}
		
		if (server_protocol == null) {
			linuxServerInfo.put("server_protocol", server_protocol);
		} else if (!server_protocol.equalsIgnoreCase("none")) {
			linuxServerInfo.put("server_protocol", server_protocol);
		}
		
		if (server_port == null) {
			linuxServerInfo.put("server_port", server_port);
		} else if (!server_protocol.equalsIgnoreCase("none")) {
			linuxServerInfo.put("server_port", server_port);
		}
		
		if (server_username == null) {
			linuxServerInfo.put("server_username", server_username);
		} else if (!server_username.equalsIgnoreCase("none")) {
			linuxServerInfo.put("server_username", server_username);
		}
		
		if (server_password == null) {
			linuxServerInfo.put("server_password", server_id);
		} else if (!server_password.equalsIgnoreCase("none")) {
			linuxServerInfo.put("server_password", server_password);
		}
		
		if (organization_id == null) {
			linuxServerInfo.put("organization_id", organization_id);
		} else if (!organization_id.equalsIgnoreCase("none")) {
			linuxServerInfo.put("organization_id", organization_id);
		}
		
		if (site_id == null) {
			linuxServerInfo.put("site_id", site_id);
		} else if (!site_id.equalsIgnoreCase("none")) {
			linuxServerInfo.put("site_id", site_id);
		}
		
		if (status == null) {
			linuxServerInfo.put("status", status);
		} else if (!status.equalsIgnoreCase("none")) {
			linuxServerInfo.put("status", status);
		}
		
		if (datacenter_id == null) {
			linuxServerInfo.put("datacenter_id", datacenter_id);
		} else if (!datacenter_id.equalsIgnoreCase("none")) {
			linuxServerInfo.put("datacenter_id", datacenter_id);
		}
		
		return linuxServerInfo;
	}
	
	public String createLinuxBackupServer(String server_id, String server_name, String server_protocol, String server_port, String server_username, 
			String server_password, String organization_id, String site_id, String status, String datacenter_id, ExtentTest test) {
		Map<String, Object> linuxServerInfo = composeLinuxServerInfo(server_id, server_name, server_protocol, server_port, server_username, server_password, 
				organization_id, site_id, status, datacenter_id);
		Response response = linux4SPOGInvoker.createLinuxServer(linuxServerInfo);
		
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		
		verifyLinuxServerInfo(response, server_name, server_protocol, server_port, server_username, organization_id, site_id, status, datacenter_id);
		
		String server_id_response = response.then().extract().path("data.server_id");
		return server_id_response;
	}
	
	public void verifyLinuxServerInfo(Response response, String server_name, String server_protocol, String server_port, String server_username,  
			String organization_id, String site_id, String status, String datacenter_id) {
		
		String response_server_name = response.then().extract().path("data.server_name");
		spogServer.assertResponseItem(server_name, response_server_name);
		
		String response_server_protocol = response.then().extract().path("data.server_protocol");
		spogServer.assertResponseItem(server_protocol, response_server_protocol);
		
		String response_server_port = response.then().extract().path("data.server_port");
		spogServer.assertResponseItem(response_server_port, response_server_port);
		
		String response_server_username = response.then().extract().path("data.server_username");
		spogServer.assertResponseItem(server_username, response_server_username);
		
		String response_organization_id = response.then().extract().path("data.organization.organization_id");
		spogServer.assertResponseItem(organization_id, response_organization_id);
		
		String response_site_id = response.then().extract().path("data.site.site_id");
		spogServer.assertResponseItem(site_id, response_site_id);
		
		String response_status = response.then().extract().path("data.status");
		spogServer.assertResponseItem(status, response_status);
		
		String response_datacenter_id = response.then().extract().path("data.datacenter.datacenter_id");
		spogServer.assertResponseItem(datacenter_id, response_datacenter_id);
		
	}

	public void createLinuxBackupServerWithErrorCheck(String server_id, String server_name, String server_protocol, String server_port, String server_username, 
			String server_password, String organization_id, String site_id, String status, String datacenter_id, int status_code, String error_code, ExtentTest test) {
		Map<String, Object> linuxServerInfo = composeLinuxServerInfo(server_id, server_name, server_protocol, server_port, server_username, server_password, 
				organization_id, site_id, status, datacenter_id);
		Response response = linux4SPOGInvoker.createLinuxServer(linuxServerInfo);
		
		spogServer.checkResponseStatus(response, status_code);
		spogServer.checkErrorCode(response, error_code);
		
	}
	
	public void updateLinuxBackupServer(String server_id_to_update, String server_id, String server_name, String server_protocol, String server_port, String server_username, 
			String server_password, String organization_id, String site_id, String status, String datacenter_id, ExtentTest test) {
		Map<String, Object> linuxServerInfo = composeLinuxServerInfo(server_id, server_name, server_protocol, server_port, server_username, server_password, 
				organization_id, site_id, status, datacenter_id);
		Response response = linux4SPOGInvoker.updateLinuxServer(server_id_to_update, linuxServerInfo);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		verifyLinuxServerInfo(response, server_name, server_protocol, server_port, server_username, organization_id, site_id, status, datacenter_id);
	}
	
	public void updateLinuxBackupServerWithErrorCheck(String server_id_to_update, String server_id, String server_name, String server_protocol, String server_port, String server_username, 
			String server_password, String organization_id, String site_id, String status, String datacenter_id, int status_code, String error_code, ExtentTest test) {
		Map<String, Object> linuxServerInfo = composeLinuxServerInfo(server_id, server_name, server_protocol, server_port, server_username, server_password, 
				organization_id, site_id, status, datacenter_id);
		Response response = linux4SPOGInvoker.updateLinuxServer(server_id_to_update, linuxServerInfo);
		spogServer.checkResponseStatus(response, status_code);
		spogServer.checkErrorCode(response, error_code);
	}
	
	public Response getLinuxBackupServer(String server_id, ExtentTest test) {
		Response response = linux4SPOGInvoker.getLinuxServer(server_id);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		return response;
	}
	
	public void getLinuxBackupServerWithErrorCheck(String server_id, int status_code, String error_code, ExtentTest test) {
		Response response = linux4SPOGInvoker.getLinuxServer(server_id);
		spogServer.checkResponseStatus(response, status_code);
		spogServer.checkErrorCode(response, error_code);
	}
	
	public void deleteLinuxBackupServer(String server_id, ExtentTest test) {
		Response response = linux4SPOGInvoker.deleteLinuxServer(server_id);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		
	}
	
	public void deleteLinuxBackupServerWithErrorCheck(String server_id, int status_code, String error_code, ExtentTest test) {
		Response response = linux4SPOGInvoker.deleteLinuxServer(server_id);
		spogServer.checkResponseStatus(response, status_code);
		spogServer.checkErrorCode(response, error_code);
	}
	
	public Response getLinuxBackupServers(ExtentTest test) {
		Response response = linux4SPOGInvoker.getLinuxServers();
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		return response;
	}
	
	public void getLinuxBackupServersWithErrorCheck(int status_code, String error_code, ExtentTest test) {
		Response response = linux4SPOGInvoker.getLinuxServers();
		spogServer.checkResponseStatus(response, status_code);
		spogServer.checkErrorCode(response, error_code);
	}

	//override the method to get response as return type for audittrails
	public Response createLinuxBackupServer_audit(String server_id, String server_name, String server_protocol, String server_port, String server_username, 
			String server_password, String organization_id, String site_id, String status, String datacenter_id, ExtentTest test) {
		Map<String, Object> linuxServerInfo = composeLinuxServerInfo(server_id, server_name, server_protocol, server_port, server_username, server_password, 
				organization_id, site_id, status, datacenter_id);
		Response response = linux4SPOGInvoker.createLinuxServer(linuxServerInfo);
		
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		
		verifyLinuxServerInfo(response, server_name, server_protocol, server_port, server_username, organization_id, site_id, status, datacenter_id);
		
		String server_id_response = response.then().extract().path("data.server_id");
		return response;
	}
	
	//override the method to get response as return type for audittrails
	public Response updateLinuxBackupServer_audit(String server_id_to_update, String server_id, String server_name, String server_protocol, String server_port, String server_username, 
			String server_password, String organization_id, String site_id, String status, String datacenter_id, ExtentTest test) {
		Map<String, Object> linuxServerInfo = composeLinuxServerInfo(server_id, server_name, server_protocol, server_port, server_username, server_password, 
				organization_id, site_id, status, datacenter_id);
		Response response = linux4SPOGInvoker.updateLinuxServer(server_id_to_update, linuxServerInfo);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		verifyLinuxServerInfo(response, server_name, server_protocol, server_port, server_username, organization_id, site_id, status, datacenter_id);
		return response;
	}
	
}

