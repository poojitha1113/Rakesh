package InvokerServer;


import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matchers;
import org.testng.Assert;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.ExtentTestInterruptedException;
import com.relevantcodes.extentreports.LogStatus;

import Constants.ErrorCode;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import dataPreparation.JsonPreparation;
import genericutil.ErrorHandler;
import invoker.Org4SPOGInvoker;
import invoker.UserSpogInvoker;
import io.restassured.response.Response;
import ui.base.common.SPOGMenuTreePath;


public class Org4SPOGServer {

	private static JsonPreparation jp = new JsonPreparation();
	private SPOGServer spogServer;
	private Org4SPOGInvoker org4SpogInvoker;
	public static ErrorHandler errorHandle = ErrorHandler.getErrorHandler();
	private ExtentTest test;
	private UserSpogInvoker userSpogInvoker;
	
	public void setToken(String token) {
		org4SpogInvoker.setToken(token);
		spogServer.setToken(token);
		
	}
	
	public void test() {
		
	}

	public Org4SPOGServer(String baseURI, String port) {
		userSpogInvoker=new UserSpogInvoker(baseURI, port);	
		org4SpogInvoker = new Org4SPOGInvoker(baseURI, port);
		spogServer = new SPOGServer(baseURI, port);
	}
	
	/**
	 * @author shuo.zhang
	 * @param parentId
	 * @param childId
	 * @param token
	 * @param expectedStatusCode
	 * @param expectedErrorCode
	 */
	public void deleteAccountPictureWithCheck(String parentId, String childId, String token,
			int expectedStatusCode, String expectedErrorCode){
		
		Response response = org4SpogInvoker.deleteAccountPicture(parentId, childId, token);
		response.then().statusCode(expectedStatusCode);
		if(expectedStatusCode != SpogConstants.SUCCESS_GET_PUT_DELETE){
			spogServer.checkErrorCode(response, expectedErrorCode);
		}
	}
	
	/**
	 * @author shuo.zhang
	 * @param parentId
	 * @param childId
	 */
	public void deleteAccountPictureWithoutLoginWithCheck(String parentId, String childId){
		
		Response response = org4SpogInvoker.deleteAccountPictureWithoutLogin(parentId, childId);
		response.then().statusCode(SpogConstants.NOT_LOGGED_IN);
		spogServer.checkErrorCode(response, ErrorCode.AUTHORIZATION_HEADER_BLANK);
	}
	/**
	 * Call the REST api to getOrganizationsIdRecoversourceTypes
	 * 
	 * @author Eric.Yang
	 * @param Token
	 * @param statuscode
	 * @param test
	 * @return response
	 */
	public Response getOrganizationsIdRecoversourceTypesWithCheck(String token,String orgId, int expectedStatusCode,int expectedIntVM_num,int expectedVSB_num, ExtentTest test) {

		
		Response response = org4SpogInvoker.getOrganizationsIdRecoversourceTypes(orgId, token, test);

		//checkResponseStatus(response, statuscode, test);
		errorHandle.printDebugMessageInDebugFile("Response status is " + response.getStatusCode());
		response.then().statusCode(expectedStatusCode);
		if (response.getStatusCode() == expectedStatusCode) {
			test.log(LogStatus.INFO, "The returned status code from the response is " + response.getStatusCode());
			assertTrue("Check status is equal to expected status:" + expectedStatusCode, true);
			test.log(LogStatus.PASS, "Check:response status");
		} else {
			assertTrue("Check status is equal to expected status:" + expectedStatusCode, false);
			test.log(LogStatus.FAIL, "Check:response status");
		}		
		
		ArrayList<HashMap> data = new ArrayList<HashMap>();
		if (expectedStatusCode == response.getStatusCode()) {
			if (expectedStatusCode==403||expectedStatusCode==401){
				test.log(LogStatus.PASS, "Check:" + "check response status " + " in reponse body.");
				return response;
			}
			data = new ArrayList<HashMap>();
			data = response.then().extract().path("data");
			if(data.get(0).get("amount").equals(expectedIntVM_num))
				test.log(LogStatus.PASS, "Check:" + "instantVM amount " + " in reponse body.");
			else
				test.log(LogStatus.FAIL, "Check:" + "instantVM amount " + " in reponse body.");
			if(data.get(1).get("amount").equals(expectedVSB_num))
				test.log(LogStatus.PASS, "Check:" + "VSB amount " + " in reponse body.");
			else
				test.log(LogStatus.FAIL, "Check:" + "VSB amount " + " in reponse body.");
		}
		else
			test.log(LogStatus.FAIL, "statusCode:" +response.getStatusCode()+ ", not equal expectedStatusCode: "+expectedStatusCode+ " in reponse body.");
			
		
		
		System.out.println("The value of the getOrganizationsIdRecoversourceTypes is :" + response.getBody().asString());
		return response;
	}
	/**
	 * general method to check item's value in body especially for body value is not
	 * String
	 * 
	 * @author Eric.Yang
	 * @param response
	 * @param item
	 * @param Expectedvalue
	 * @return value for the item
	 * 
	 */
	public String checkResponseItemValue(Response response, String item, String Expectedvalue, ExtentTest test) {
		String valueFromResponse = "";
		if (null == response.then().extract().path("data." + item)) {
			valueFromResponse = "null";
		} else {
			valueFromResponse = response.then().extract().path("data." + item).toString().toLowerCase();
		}

		errorHandle.printDebugMessageInDebugFile("Get value for " + item + " from response is " + valueFromResponse);
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
	 * @param organization_id
	 * @param test
	 */
	public void deactiveOrganization(String organization_id, ExtentTest test) {
		Response response = org4SpogInvoker.deactiveOrganization(organization_id);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
	}
	
	/**
	 * @author Zhaoguo.Ma
	 * @param organization_id
	 * @param status_code
	 * @param error_code
	 * @param test
	 */
	public void deactiveOrganizationWithErrorCheck(String organization_id, int status_code, String error_code, ExtentTest test) {
		Response response = org4SpogInvoker.deactiveOrganization(organization_id);
		spogServer.checkResponseStatus(response, status_code);
		spogServer.checkErrorCode(response, error_code);
	}
	
	/**
	 * @author Zhaoguo.Ma
	 * @param organization_id
	 * @param test
	 */
	public void destroyOrganization(String organization_id, ExtentTest test) {
		Response response = org4SpogInvoker.destroyOrganization(organization_id);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
	}
	
	public void destroyOrganization(String organization_id) {
		Response response = org4SpogInvoker.destroyOrganization(organization_id);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
	}
	
	public void destroyOrganizationWithoutCheck(String organization_id) {
		Response response = org4SpogInvoker.destroyOrganization(organization_id);
	}
	
	public ArrayList<String> getOrgIdsBySearchStringWithCsrLogin(String token,String searchStr,int page){
		ArrayList<String> orgIds=new ArrayList<String>();
		HashMap<String, Object> searchInfo = jp.postOrgInfoBySearchString(searchStr,page,100);
		Response response = org4SpogInvoker.postOrgInfoBySearchStringWithCsrLogin(searchInfo,token);
		if(response.getStatusCode()!=SpogConstants.SUCCESS_GET_PUT_DELETE) {
			return null;
		}else{
			ArrayList<HashMap> datas = null;
			datas = new ArrayList<HashMap>();
			datas = response.then().extract().path("data");
			if(datas.size()>0){
				for(int i=0;i<datas.size();i++){
					if(datas.get(i).get("organization_name").toString().toLowerCase().indexOf("do_not_delete")==-1){
						orgIds.add(datas.get(i).get("organization_id").toString());
						System.out.println("del org id:"+datas.get(i).get("organization_id").toString());
						System.out.println("del org name:"+datas.get(i).get("organization_name").toString());
					}
				}
			}else{
				return null;
			}
		}
		return orgIds;
	}
	
	public ArrayList<String> getOrgIdNameBySearchStringWithCsrLogin(String token,String searchStr,int page){
		ArrayList<String> orgIds=new ArrayList<String>();
		HashMap<String, Object> searchInfo = jp.postOrgInfoBySearchString(searchStr,page,100);
		Response response = org4SpogInvoker.postOrgInfoBySearchStringWithCsrLogin(searchInfo,token);
		if(response.getStatusCode()!=SpogConstants.SUCCESS_GET_PUT_DELETE) {
			return null;
		}else{
			ArrayList<HashMap> datas = null;
			datas = new ArrayList<HashMap>();
			datas = response.then().extract().path("data");
			if(datas.size()>0){
				for(int i=0;i<datas.size();i++){
					if(datas.get(i).get("organization_name").toString().toLowerCase().indexOf("do_not_delete")==-1){
						orgIds.add(datas.get(i).get("organization_id").toString()+", "+datas.get(i).get("organization_name").toString());
						System.out.println("del org id:"+datas.get(i).get("organization_id").toString());
						System.out.println("del org name:"+datas.get(i).get("organization_name").toString());
					}
				}
			}else{
				return null;
			}
		}
		return orgIds;
	}
	

	
	/**
	 * get org ids by search org name
	 * 
	 * @author shan.jing
	 * @return ArrayList<String>
	 */
	public int getOrgPagesBySearchStringWithCsrLogin(String token,String searchStr){
		ArrayList<String> orgIds=new ArrayList<String>();
		HashMap<String, Object> searchInfo = jp.postOrgInfoBySearchString(searchStr,1,10000);
		Response response = org4SpogInvoker.postOrgInfoBySearchStringWithCsrLogin(searchInfo,token);
		if(response.getStatusCode()!=SpogConstants.SUCCESS_GET_PUT_DELETE) {
			return 0;
		}else{
			int  total_page=response.then().extract().path("pagination.total_page");
			return total_page;
		}
	}
	
	/**
	 * @author Zhaoguo.Ma
	 * @param organization_id
	 * @param status_code
	 * @param error_code
	 * @param test
	 */
	public void destroyOrganizationWithErrorCheck(String organization_id, int status_code, String error_code, ExtentTest test) {
		Response response = org4SpogInvoker.destroyOrganization(organization_id);
		spogServer.checkResponseStatus(response, status_code);
		spogServer.checkErrorCode(response, error_code);
	}
	
	/**
	 * @author shuo.zhang
	 * @param first_name
	 * @param last_name
	 * @param email
	 * @param phone_number
	 * @param organization_name
	 * @param organization_type
	 * @param datacenter_id
	 * @param test
	 * @return
	 */
	public Response enrollOrganizations(String first_name, String last_name, String email, String phone_number,
			String organization_name, String organization_type, String datacenter_id, ExtentTest test){
		
		HashMap<String, Object > enrollmentInfo = jp.composeEnrollmentInfo(first_name, last_name, email, phone_number, organization_name, organization_type, datacenter_id);
		return org4SpogInvoker.enrollOrganization(enrollmentInfo);
		
	}
	
	/**
	 * @author shuo.zhang
	 * @param response
	 * @param first_name
	 * @param last_name
	 * @param email
	 * @param phone_number
	 * @param organization_name
	 * @param organization_type
	 * @param datacenter_id
	 * @param status_code
	 * @param error_code
	 * @param test
	 * @return
	 */
	public String checkEnrollOrganizations(Response response,String first_name, String last_name, String email, String phone_number,
			String organization_name, String organization_type, String datacenter_id, int status_code,  String error_code, ExtentTest test){
		
		response.then().statusCode(status_code);
		if(status_code!=SpogConstants.SUCCESS_POST){
			spogServer.checkErrorCode(response, error_code);
			return null;
		}else{
			/*String prefix="spog_udp_qa_";
			if(organization_name!=null&&organization_name!=""){
				organization_name=prefix+organization_name;
			}
			response.then().body("data.user.first_name", equalTo(first_name));
			response.then().body("data.user.last_name", equalTo(last_name));
			response.then().body("data.user.email", equalTo(email.toLowerCase()));
			response.then().body("data.organization.organization_name", equalTo(organization_name));
			response.then().body("data.organization.organization_type", equalTo(organization_type));
			if((datacenter_id!=null) && (!datacenter_id.equals(""))){
				response.then().body("data.organization.datacenter_id", equalTo(datacenter_id));
			}
			if((phone_number!=null) && (!phone_number.equals(""))){
				response.then().body("data.user.phone_number", equalTo(phone_number));
			}*/
			HashMap<String, Object> searchInfo = new HashMap<>();
			searchInfo.put("search_string", email);
			Response retUser=null;
			int totalsize=0;
			boolean findUser=false;
		
			for(int loop=1;loop<60;loop++){
				retUser = userSpogInvoker.postUsersSearch(searchInfo, spogServer.getJWTToken());
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
				return null;
			}else{
				
				String prefix="AUTO_";
				if(organization_name!=null&&organization_name!=""){
					organization_name=prefix+organization_name;
				}
				retUser.then()
				.body("data[0].organization.organization_name",equalTo(organization_name));
				return retUser.then().extract().path("data[0].organization.organization_id");
			}
			
		}
	}
	
	/**
	 * @author shuo.zhang
	 * @param first_name
	 * @param last_name
	 * @param email
	 * @param phone_number
	 * @param organization_name
	 * @param organization_type
	 * @param datacenter_id
	 * @param status_code
	 * @param error_code
	 * @param test
	 * @return
	 */
	public String enrollOrganizationsWithCheck(String first_name, String last_name, String email, String phone_number,
			String organization_name, String organization_type, String datacenter_id,int status_code,  String error_code, ExtentTest test){
		
		Response response = enrollOrganizations(first_name, last_name, email, phone_number, organization_name, organization_type, datacenter_id, test);
		return checkEnrollOrganizations(response, first_name, last_name, email, phone_number, organization_name, organization_type, datacenter_id,
				status_code,error_code , test);
		
	}
	
	/**
	 * @author shuo.zhang
	 * @param orgid
	 * @param token
	 * @return
	 */
	public Response getSpecifiedOrganizationEntitlement(String orgid, String token){
		
		Response response = org4SpogInvoker.getSpecifiedOrganizationEntitlement(orgid, token);
		return response;
	}
	
	/**
	 * @author shuo.zhang
	 * @param response
	 * @param cloudDirectInfo
	 * @param cloudHybridInfo
	 * @param statusCode
	 * @param errorCode
	 */
	public void checkGetOrganizationEntitlement(Response response, HashMap<String, Object> cloudDirectInfo,  HashMap<String, Object> cloudHybridInfo, int statusCode, String errorCode){
		
		response.then().statusCode(statusCode);
		if(statusCode==SpogConstants.SUCCESS_GET_PUT_DELETE){
			response.then().body("data.clouddirect.state", Matchers.equalToIgnoringCase(cloudDirectInfo.get("state").toString()));
			if(cloudDirectInfo.get("billing_type")==null){
				response.then().body("data.clouddirect.billing_type", Matchers.isEmptyOrNullString());
			}else{
				response.then().body("data.clouddirect.billing_type", Matchers.equalToIgnoringCase(cloudDirectInfo.get("billing_type").toString()));
			}
			
			response.then().body("data.clouddirect.enable", Matchers.equalTo(cloudDirectInfo.get("enable")));
		//	response.then().body("data.clouddirect.baas", Matchers.equalTo(cloudDirectInfo.get("baas")));
			HashMap<String, Object>  baasObject= (HashMap<String, Object>) cloudDirectInfo.get("baas");
			response.then().body("data.clouddirect.baas.usage", Matchers.equalTo(baasObject.get("usage")));	
			
			if(baasObject.get("capacity")==null)
				response.then().body("data.clouddirect.baas", Matchers.equalTo(cloudDirectInfo.get("baas")));
			else{
				Long capacity=response.then().extract().jsonPath().getLong("data.clouddirect.baas.capacity");
				Assert.assertEquals(capacity, baasObject.get("capacity"), "clouddirect baas capacity is not correct");
			}
			
		
			response.then().body("data.clouddirect.baas.enable", Matchers.equalTo(baasObject.get("enable")));
			response.then().body("data.clouddirect.baas.configured", Matchers.equalTo(baasObject.get("configured")));
			
			response.then().body("data.clouddirect.draas", Matchers.equalTo(cloudDirectInfo.get("draas")));
			/*HashMap<String, Object>  baasObject= (HashMap<String, Object>) cloudDirectInfo.get("draas");
			response.then().body("data.clouddirect.draas.ram", Matchers.equalTo(baasObject.get("ram")));
			response.then().body("data.clouddirect.draas.ad", Matchers.equalTo(baasObject.get("ad")));
			response.then().body("data.clouddirect.draas.public_ip", Matchers.equalTo(baasObject.get("public_ip")));
			response.then().body("data.clouddirect.draas.vcpu", Matchers.equalTo(baasObject.get("vcpu")));*/
			
			
			if(cloudDirectInfo.get("state").toString().equalsIgnoreCase("trial")){
				response.then().body("data.clouddirect.trial_start", Matchers.notNullValue());
			}else{
				response.then().body("data.clouddirect.trial_start", Matchers.nullValue());
			}
			
			
			
			response.then().body("data.cloudhybrid.state", Matchers.equalToIgnoringCase(cloudHybridInfo.get("state").toString()));
			if(cloudHybridInfo.get("billing_type")==null){
				response.then().body("data.cloudhybrid.billing_type", Matchers.isEmptyOrNullString());
			}else{
				response.then().body("data.cloudhybrid.billing_type", Matchers.equalToIgnoringCase(cloudHybridInfo.get("billing_type").toString()));
			}
			
			response.then().body("data.cloudhybrid.enable", Matchers.equalTo(cloudHybridInfo.get("enable")));
		//	response.then().body("data.cloudhybrid.baas", Matchers.equalTo(cloudHybridInfo.get("baas")));
			response.then().body("data.cloudhybrid.draas", Matchers.equalTo(cloudHybridInfo.get("draas")));
			if(cloudHybridInfo.get("state").toString().equalsIgnoreCase("trial")){
				response.then().body("data.cloudhybrid.trial_start", Matchers.notNullValue());
			}else{
				response.then().body("data.cloudhybrid.trial_start", Matchers.nullValue());
			}
			
			
		}else{
			spogServer.checkErrorCode(response, errorCode);
		}
	}
	
	/**
	 * @author shuo.zhang
	 * @param orgid
	 * @return
	 */
	public Response getSpecifiedOrganizationEntitlementWithoutLogin(String orgid){
		
		Response response = org4SpogInvoker.getSpecifiedOrganizationEntitlementWithoutLogin(orgid);
		return response;
	}
	
	/**
	 * @author shuo.zhang
	 * @param token
	 * @return
	 */
	public Response getLoggedInUserOrganizationEntitlement(String token){
		
		Response response = org4SpogInvoker.getLogginUserOrganizationEntitlement(token);
		return response;
	}
	
	/**
	 * @author shuo.zhang
	 * @return
	 */
	public Response getLoggedInUserOrganizationEntitlementWithoutLogin(){
		
		Response response = org4SpogInvoker.getLogginUserOrganizationEntitlementWithoutLogin();
		return response;
	}
	
	/**
	 * @author shuo.zhang
	 * @param state
	 * @param cloud_direct_baas
	 * @param cloud_direct_draas
	 * @param cloud_direct_ad
	 * @param cloud_direct_ip
	 * @param orgType 
	 * @param cd_billing_type 
	 * @return
	 */
	  public HashMap<String, Object> getCloudDirectInfo(String state, int cloud_direct_baas, int cloud_direct_draas,
			  int cloud_direct_ad, int cloud_direct_ip, int cloud_direct_vcpu, String orgType, String cd_billing_type, boolean parentRootMSP){
		 
		  HashMap<String, Object> cloudDirectInfo = new HashMap<String, Object> ();
		  cloudDirectInfo.put("state", state);

		  
		  
		  if(state.equalsIgnoreCase("trial")){
			  cloudDirectInfo.put("billing_type", null);
			  cloudDirectInfo.put("enable", true);
			  HashMap<String, Object> baasInfo =  new HashMap<String, Object> ();
			  baasInfo.put("usage", 0);
			  baasInfo.put("capacity", null);
			  baasInfo.put("enable", true);
			  baasInfo.put("configured", true);
			  cloudDirectInfo.put("baas", baasInfo );
			 
			  HashMap<String, Object> draasInfo =  new HashMap<String, Object> ();
			 
			  if(orgType.equalsIgnoreCase(SpogConstants.MSP_ORG)){
				  draasInfo.put("enable", true);
				  draasInfo.put("configured", true);
			  }else{
				  draasInfo.put("enable", false);
				  draasInfo.put("configured", false);
			  }
			 
			  HashMap<String, Object> ramInfo =  new HashMap<String, Object> ();
			  ramInfo.put("usage", 0);
			  ramInfo.put("capacity", null);
			  draasInfo.put("ram", ramInfo);
			  HashMap<String, Object> adInfo =  new HashMap<String, Object> ();
			  adInfo.put("usage", 0);
			  adInfo.put("capacity", null);
			  draasInfo.put("ad", adInfo);
			  HashMap<String, Object> ipInfo =  new HashMap<String, Object> ();
			  ipInfo.put("usage", 0);
			  ipInfo.put("capacity", null);
			  draasInfo.put("public_ip", ipInfo);
			  
			  HashMap<String, Object> vcpuInfo =  new HashMap<String, Object> ();
			  vcpuInfo.put("usage", 0);
			  vcpuInfo.put("capacity", null);
			  draasInfo.put("vcpu", vcpuInfo);
			  
			  cloudDirectInfo.put("draas", draasInfo );
		  }else if(state.equalsIgnoreCase("active")){
			  //cloudDirectInfo.put("billing_type", "LICENSE");
			  cloudDirectInfo.put("billing_type", cd_billing_type);
			  cloudDirectInfo.put("enable", true);
			  HashMap<String, Object> baasInfo =  new HashMap<String, Object> ();
			  baasInfo.put("usage", 0);
			
			  Long capacity= new Long(1024L*1024*1024*1024*cloud_direct_baas);			  
			  baasInfo.put("capacity", capacity);
			  baasInfo.put("enable", true);
			  baasInfo.put("configured", true);
			  cloudDirectInfo.put("baas", baasInfo );
			 
			  HashMap<String, Object> draasInfo =  new HashMap<String, Object> ();
			  draasInfo.put("enable", true);
			  
		/*	  if(orgType.equalsIgnoreCase(SpogConstants.MSP_ORG)||orgType.equalsIgnoreCase(SpogConstants.MSP_SUB_ORG)){
				  draasInfo.put("configured", true);
			  }else{
				  draasInfo.put("configured", false);
			  }*/
			  if(orgType.equalsIgnoreCase(SpogConstants.MSP_ORG)||orgType.equalsIgnoreCase(SpogConstants.MSP_SUB_ORG)){
				  draasInfo.put("configured", true);
			  }else{
				  draasInfo.put("configured", false);
			  }
			  
			  if(parentRootMSP==true){
				  if(orgType.equalsIgnoreCase(SpogConstants.MSP_SUB_ORG)){
					  draasInfo.put("configured", false); 
					  draasInfo.put("enable", false);
				  }
			
			  }
		
			  
			  
			 
			  HashMap<String, Object> ramInfo =  new HashMap<String, Object> ();
			  ramInfo.put("usage", 0);
			  ramInfo.put("capacity", cloud_direct_draas);
			  draasInfo.put("ram", ramInfo);
			 
			  HashMap<String, Object> adInfo =  new HashMap<String, Object> ();
			  adInfo.put("usage", cloud_direct_ad);
			  adInfo.put("capacity", cloud_direct_ad);
			  draasInfo.put("ad", adInfo);
			  
			  HashMap<String, Object> ipInfo =  new HashMap<String, Object> ();
			  ipInfo.put("usage", cloud_direct_ip);
			  ipInfo.put("capacity", cloud_direct_ip);
			  draasInfo.put("public_ip", ipInfo);
			  
			  HashMap<String, Object> vcpuInfo =  new HashMap<String, Object> ();
			  vcpuInfo.put("usage", 0);
			  vcpuInfo.put("capacity", cloud_direct_vcpu);
			  draasInfo.put("vcpu", vcpuInfo);
			  
			  
			  cloudDirectInfo.put("draas", draasInfo );
		  }
		
		 
		  return cloudDirectInfo;
		  
	  }
	  /**
	   * @author shuo.zhang
	   * @param state
	   * @param cloud_hybrid_baas
	   * @param cloud_hybrid_draas
	   * @return
	   */
	  public HashMap<String, Object> getCloudHybridInfo(String state, int cloud_hybrid_baas, int cloud_hybrid_draas,
			  int cloud_hybrid_ad, int cloud_hybrid_ip, int cloud_hybrid_vcpu, String orgType){
		
		  HashMap<String, Object> cloudHybridInfo = new HashMap<String, Object> ();
		  cloudHybridInfo.put("state", state);
	
		 
		  if(state.equalsIgnoreCase("trial")){
			  cloudHybridInfo.put("billing_type", null);
			  cloudHybridInfo.put("enable", true);
			  HashMap<String, Object> baasInfo =  new HashMap<String, Object> ();
			  baasInfo.put("usage", 0);
			  baasInfo.put("capacity", null);
		
			  if(orgType.equalsIgnoreCase(SpogConstants.MSP_ORG)){
				  baasInfo.put("enable", true);
				  baasInfo.put("configured", true);
			  }else{
				  baasInfo.put("enable", false);
				  baasInfo.put("configured", false);
			  }
		
			  cloudHybridInfo.put("baas", baasInfo );
			 
			  HashMap<String, Object> draasInfo =  new HashMap<String, Object> ();
			 
	
			  draasInfo.put("enable", false);
			  draasInfo.put("configured", false);
			  
			 
			  HashMap<String, Object> ramInfo =  new HashMap<String, Object> ();
			  ramInfo.put("usage", 0);
			  ramInfo.put("capacity", null);
			  draasInfo.put("ram", ramInfo);			
		
			  HashMap<String, Object> adInfo =  new HashMap<String, Object> ();
			  adInfo.put("usage", 0);
			  adInfo.put("capacity", null);
			  draasInfo.put("ad", adInfo);
			  HashMap<String, Object> ipInfo =  new HashMap<String, Object> ();
			  ipInfo.put("usage", 0);
			  ipInfo.put("capacity", null);
			  draasInfo.put("public_ip", ipInfo);
			  
			  HashMap<String, Object> vcpuInfo =  new HashMap<String, Object> ();
			  vcpuInfo.put("usage", 0);
			  vcpuInfo.put("capacity",null);
			  draasInfo.put("vcpu", vcpuInfo);
			  
			  cloudHybridInfo.put("draas", draasInfo );
			  
		  }else if(state.equalsIgnoreCase("active")){
			  cloudHybridInfo.put("billing_type", "LICENSE");
			  cloudHybridInfo.put("enable", true);
			  cloudHybridInfo.put("configured", false);
		
			  HashMap<String, Object> baasInfo =  new HashMap<String, Object> ();
			  baasInfo.put("usage", 0);
			  baasInfo.put("capacity", cloud_hybrid_baas*1024*1024*1024*1024);
			  baasInfo.put("enable", true);
			  if(orgType.equalsIgnoreCase(SpogConstants.MSP_ORG)){
				  baasInfo.put("configured", true);
			  }else{
				  baasInfo.put("configured", false);
			  }
		
			  cloudHybridInfo.put("baas", baasInfo );
			 
			  HashMap<String, Object> draasInfo =  new HashMap<String, Object> ();
			  
			 
				  draasInfo.put("enable", false);
				  draasInfo.put("configured", false);
			  
			  
			  HashMap<String, Object> ramInfo =  new HashMap<String, Object> ();
			  ramInfo.put("usage", 0);
			  ramInfo.put("capacity", cloud_hybrid_draas);
			  draasInfo.put("ram", ramInfo);
			  
			 
			  HashMap<String, Object> adInfo =  new HashMap<String, Object> ();
			  adInfo.put("usage", cloud_hybrid_ad);
			  adInfo.put("capacity", cloud_hybrid_ad);
			  draasInfo.put("ad", adInfo);
			  
			  HashMap<String, Object> ipInfo =  new HashMap<String, Object> ();
			  if(cloud_hybrid_ip!=0){
				  ipInfo.put("usage", cloud_hybrid_ip);
				  ipInfo.put("capacity", cloud_hybrid_ip);
			  }else{
				  ipInfo.put("usage", 0);
				  ipInfo.put("capacity", 0);
			  }
			  
			  draasInfo.put("public_ip", ipInfo);
			
			  HashMap<String, Object> vcpuInfo =  new HashMap<String, Object> ();
			  vcpuInfo.put("usage", 0);
		//	  vcpuInfo.put("capacity", Math.max(1, cloud_hybrid_draas/4));
			  vcpuInfo.put("capacity", cloud_hybrid_vcpu);
			  draasInfo.put("vcpu", vcpuInfo);
		  
			  cloudHybridInfo.put("draas", draasInfo );
		  }
		 
		  return cloudHybridInfo;
		  
	  }
	  
	  /**
	   * @author shuo.zhang
	   * @param response
	   * @param test
	   * @return
	   */
	  public String getUserIdFromEnrollOrganizationsResponse(Response response,ExtentTest test){

			return response.then().extract().path("data.user.user_id") ;
		
	}
	  
	  public void destroyOrgWithNameLike(String orgString){
			int total_page=getOrgPagesBySearchStringWithCsrLogin(spogServer.getJWTToken(),orgString);
			if(total_page>0){
				for(int i=1;i<=total_page;i++ ){
					ArrayList<String> ret=getOrgIdsBySearchStringWithCsrLogin(spogServer.getJWTToken(),orgString,i);
					if(ret!=null){
						if(ret.size()>0){
							for(int del=0;del<ret.size();del++ ){
								destroyOrganizationWithoutCheck(ret.get(del).toString());
							}
						}
					}						
				}
		    }		
		}
		
		public Response postOrgFreeTrialCloudHybrid(String organizationId, String token) {
			return org4SpogInvoker.postOrgFreeTrialCloudHybrid(organizationId, token);

		}
		
		public void checkPostOrgFreeTrialCloudHybrid(Response response, int expectedStatusCode, String expectedErrorCode) {
			response.then().statusCode(expectedStatusCode);
		}

		/**
		 * Deletes the specified organization's cache resources and checks the status code of response
		 * @author Rakesh.Chalamala
		 * 
		 * @param token
		 * @param organization_id
		 * @param expectedStatusCode
		 * @return
		 */
		public Response deleteOrgCacheResourcesByOrgId(String token, String organization_id, int expectedStatusCode, ExtentTest test) {

			test.log(LogStatus.INFO, "Call API - Delete: organizations/{id}/cache/resources and get the response");
			Response response = org4SpogInvoker.deleteOrgCacheResourcesByOrgId(token, organization_id, test);

			test.log(LogStatus.INFO, "Validate the response status code with expected");
			spogServer.checkResponseStatus(response, expectedStatusCode);

			return response;
		}

		/**
		 * Deletes the specified organization's cache resources with check
		 * 
		 * @author Rakesh.Chalamala
		 * 
		 * @param token
		 * @param organization_id
		 * @param expectedStatusCode
		 * @param expectedErrorMessage
		 * @param test
		 */
		public void deleteOrgCacheResourcesByOrgIdWithCheck(String token, String organization_id,
																int expectedStatusCode, SpogMessageCode expectedErrorMessage,
																ExtentTest test) {

			Response response = deleteOrgCacheResourcesByOrgId(token, organization_id, expectedStatusCode, test);
			
			if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
				test.log(LogStatus.PASS, "Cache resources of organization with id: "+organization_id+" are delete successfully.");
			}else {
				String code = expectedErrorMessage.getCodeString();
				String message = expectedErrorMessage.getStatus();
				spogServer.checkErrorCode(response, code);
				test.log(LogStatus.INFO, "The error code matched with the expected " + code);
				spogServer.checkErrorMessage(response,message);
				test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
			}
		}
		
		/**
		 * @author Zhaoguo.Ma
		 * @param organizationId
		 * @return
		 */
	public Response convertToRootMSP(String organizationId) {
		Response response = org4SpogInvoker.convertToRootMSP(organizationId);
		return response;
	}

	/**
	 * createSubMSPAccount
	 * @author Zhaoguo.Ma
	 * @param organizationName
	 * @param parantId
	 * @param firstName
	 * @param lastName
	 * @param email
	 * @param datacenterId
	 * @param test
	 * @return
	 */
	public String createSubMSPAccountincc(String organizationName, String parantId, String firstName, String lastName,
			String email, String datacenterId, ExtentTest test) {

		Map<String, Object> orgInfo = jp.createSubMSPAccount(organizationName, parantId, firstName, lastName, email,
				datacenterId);
		Response response = org4SpogInvoker.createSubMSPAccountsincc(orgInfo, parantId);
		String subMSPAccountId = response.then().extract().path("data.organization_id");
		return subMSPAccountId;
	}
	
	/**
	 * 
	 * if org is created by enroll, please call this function to create sub msp
	 * @author shuo
	 * @param organizationName
	 * @param parantId
	 * @param firstName
	 * @param lastName
	 * @param email
	 * @param datacenterId
	 * @param test
	 * @return
	 */
	public String createSubMSPAccount(String organizationName, String parantId, String firstName, String lastName,
			String email, String datacenterId, ExtentTest test) {

		Map<String, Object> orgInfo = jp.createSubMSPAccount(organizationName, parantId, firstName, lastName, email,
				datacenterId);
		Response response = org4SpogInvoker.createSubMSPAccounts(orgInfo, parantId);
		String subMSPAccountId = response.then().extract().path("data.organization_id");
		return subMSPAccountId;
	}
	
	/**
	 * @author ChandraKanth.Kanamar
	 * @param token
	 * @param organization_id
	 * @param expectedStatusCode
	 * @param test
	 * @return Response
	 */
	public Response suspendOrganization(String token, String organization_id, int expectedStatusCode, ExtentTest test) {
		
		test.log(LogStatus.INFO, "Call API - POST: /organizations/{id}/suspend");
		Response response = org4SpogInvoker.suspendOrganization(token,organization_id);
		
		spogServer.checkResponseStatus(response, expectedStatusCode);
		
		return response;
	}
	
	/**
	 * @author ChandraKanth.Kanamar
	 * @param token
	 * @param organization_id
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 */
	public void suspendOrganizationWithCheck(String token, String organization_id, int expectedStatusCode, SpogMessageCode expectedErrorMessage, ExtentTest test)
	{
		Response response=suspendOrganization(token, organization_id, expectedStatusCode, test);
		
		if(expectedStatusCode==SpogConstants.SUCCESS_GET_PUT_DELETE)
		{
			test.log(LogStatus.PASS, "Organization with id: "+organization_id+" is suspeneded successfully.");
			
		}else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.INFO, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response,message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
			
	}
	
	

	/**
	 * @author ChandraKanth.Kanamar
	 * @param token
	 * @param organization_id
	 * @param expectedStatusCode
	 * @param test
	 * @return Response
	 */
	public Response resumeOrganization(String token, String organization_id, int expectedStatusCode, ExtentTest test) {
		
		test.log(LogStatus.INFO, "Call API - POST: /organizations/{id}/resume");
		Response response = org4SpogInvoker.resumeOrganization(token,organization_id);
		
		spogServer.checkResponseStatus(response, expectedStatusCode);
		
		return response;
	}
	
	/**
	 * @author ChandraKanth.Kanamar
	 * @param token
	 * @param organization_id
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 */
	public void resumeOrganizationWithCheck(String token, String organization_id, int expectedStatusCode, SpogMessageCode expectedErrorMessage, ExtentTest test)
	{
		Response response=resumeOrganization(token, organization_id, expectedStatusCode, test);
		
		if(expectedStatusCode==SpogConstants.SUCCESS_GET_PUT_DELETE)
		{
			test.log(LogStatus.PASS, "Organization with id: "+organization_id+" is resumed successfully.");
			
		}else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.INFO, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response,message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
			
	}
	
	 
}
