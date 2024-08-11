package InvokerServer;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.AssertJUnit.assertTrue;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import org.apache.commons.lang3.RandomStringUtils;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import Base64.Base64Coder;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import dataPreparation.JsonPreparation;
import genericutil.ErrorHandler;
import invoker.GatewayInvoker;
import io.restassured.response.Response;

public class GatewayServer {

  public static enum siteType {
    cloud_direct, gateway
  }

  private static JsonPreparation jp          = new JsonPreparation();
  private GatewayInvoker         gatewayInvoker;
  private String                 Uuid;
  static ErrorHandler            errorHandle = ErrorHandler.getErrorHandler();


  public GatewayServer(String baseURI, String port) {
    gatewayInvoker = new GatewayInvoker(baseURI, port);
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
  private String getUUId() {

    return Uuid;
  }


  /**
   * This method is used to Register a site:
   * 
   * @author BharadwajReddy
   * @param site_registration_key
   * @param gateway_id
   * @param gateway_hostname
   * @param site_version
   * @return response(JSON)
   */

  public Response RegisterSite(String site_registration_key, String gateway_id,
      String gateway_hostname, String site_version, String site_id, ExtentTest test) {

    setUUID(site_registration_key);

    test.log(LogStatus.INFO, "Creating a HashMap For Registering a Site");
    Map<String, String> registerInfo = jp.composeSiteRegisterInfoMap(site_registration_key,
        gateway_id, gateway_hostname, site_version);
    Response response = gatewayInvoker.registerSite(registerInfo, site_id, test);
    return response;
  }


  /**
   * general method to check response status
   * 
   * @author BharadwajReddy
   * @param response
   * @param expectedStatusCode
   */
  public void checkResponseStatus(Response response, int expectedStatusCode, ExtentTest test) {

    test.log(LogStatus.INFO, "The expected status code:" + expectedStatusCode);
    errorHandle.printDebugMessageInDebugFile("Response status is " + response.getStatusCode());
    response.then().statusCode(expectedStatusCode);
  }


  /**
   * Method to validate the body of register site
   * 
   * @author bhardwajReddy
   * @param response
   * @param expectedStatusCode
   * @param site_id
   * @param siteName
   * @param siteType
   * @param organization_id
   * @param user_id
   * @param expectedErrorMessage
   * @return site_secret/null
   */
  public String checkRegisterSite(Response response, int expectedStatusCode, String site_id,
      String siteName, String site_type, String organization_id, String user_id,
      boolean is_registered, SpogMessageCode Info, ExtentTest test) {

    String expectedErrorMessage = "", expectedErrorCode = "";
    checkResponseStatus(response, expectedStatusCode, test);

    if (expectedStatusCode == ServerResponseCode.Succes_Login) {
      String site_secret = response.then().body("data.site_id", equalTo(site_id))
          .body("data.site_name", equalTo(siteName))
          // .body("organization_id", equalTo(organization_id))
          // .body("data.create_user_id", equalTo(user_id))
          // .body("data.registered",equalTo(is_registered))
          .body("data.site_type", equalTo(site_type))
          .body("data.site_secret", not(isEmptyOrNullString())).extract().path("data.site_secret");

      return site_secret;

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
      return null;
    }
  }


  /**
   * Method to LoginSite
   * 
   * @author kiransripada
   * @param site_id
   * @param secret_key
   * @param extentTest object
   * @return response
   */
  public Response LoginSite(String site_id, String secert_key, String gateway_id, ExtentTest test) {

    test.log(LogStatus.INFO, "Build the hash map with site id and secert key");
    Map<String, String> siteLoginInfoMap =
        jp.composeSiteLoginInfoMap(site_id, secert_key, gateway_id);
    Response response = gatewayInvoker.siteLogin(siteLoginInfoMap, site_id);
    return response;

  }


  public Response LoginSite(String site_id, String secert_key, ExtentTest test) {

    test.log(LogStatus.INFO, "Build the hash map with site id and secert key");
    Map<String, String> siteLoginInfoMap = jp.composeSiteLoginInfoMap(secert_key);
    Response response = gatewayInvoker.siteLogin(siteLoginInfoMap, site_id);
    return response;

  }


  /**
   * check Login Web Service
   * 
   * @author kiran.sripada
   * @param response after call login web service, the return value
   * @param expectedStatusCode expectStatusCode
   * @param expectedErrorMessage
   * @param test : object of ExtentTest
   */
  public void checksiteLogin(Response response, int expectedStatusCode, String expectedErrorMessage,
      ExtentTest test) {

    test.log(LogStatus.INFO, "expect status code is " + expectedStatusCode);
    errorHandle.printInfoMessageInDebugFile("expect status code is " + expectedStatusCode);
    response.then().statusCode(expectedStatusCode);
    if (expectedStatusCode == ServerResponseCode.Succes_Login) {
      String token = response.then().extract().path("data.token");
      test.log(LogStatus.INFO, "token is " + token);
      errorHandle.printInfoMessageInDebugFile("token is " + token);
      if ((token == null) || (token.equals(""))) {
        test.log(LogStatus.FAIL, "token is null or empty");
        assertTrue("Token is not Null or empty", false);
      } else {
        test.log(LogStatus.PASS, "Got the token");
        assertTrue("Token is not Null or empty", true);
        setToken(token);
      }
    } else {
      // for the error handling test. no token
      // response.then().assertThat().body(equalTo("\"\""));
      test.log(LogStatus.INFO, "for error handling, check error message");
      checkErrorMessage(response, expectedErrorMessage);
      test.log(LogStatus.PASS, "No token but got the message as" + expectedErrorMessage);
    }
  }


  /**
   * set token in invoke
   * 
   * @author kiran.sripada
   * @param token: token value
   * @return
   * 
   */
  public void setToken(String token) {

    gatewayInvoker.setToken(token);;
  }


  /**
   * Get the JWT token of the logged in user
   * 
   * @author kiransripada
   * @return JWTToken
   */
  public String getJWTToken() {

    String JWTToken = null;
    JWTToken = gatewayInvoker.getToken();
    return JWTToken;
  }


  /**
   * Method to get the body of site configuration
   * 
   * @author kiransripada
   * @param site_id
   * @param secret_key
   * @param extentTest object
   * @return response
   */
  public Response getSiteConfiguration(String site_id, String valid_token, ExtentTest test) {

    Response response = gatewayInvoker.getSiteConfiguration(site_id, valid_token, test);
    return response;

  }


  /**
   * Method to validate the body of site configuration
   * 
   * @author kiransripada
   * @param response
   * @param expectedStatusCode
   * @param extenttest object
   * @return site_secret/null
   */
  public String checkSiteConfiguration(Response response, String site_id, int expectedStatusCode,
      SpogMessageCode expectedErrorMessage, ExtentTest test) {

    checkResponseStatus(response, expectedStatusCode, test);

    if (expectedStatusCode == ServerResponseCode.Success_Get) {
      // String site_configuration = response.then().extract().body().asString();
      String site_configuration =
          response.then().body("data.wsapi_url", equalTo("https://tccapi.arcserve.com:8443/api"))
              .body("data.portal_url", equalTo("https://tcc.arcserve.com"))
              .body("data.dataapi_url", equalTo("https://tccapi.arcserve.com:8443/api"))
              .body("data.queueapi_url",
                  equalTo(
                      "tcp://tmq.arcserve.com:61611,tcp://tmq2.arcserve.com:61612"))
              .body("data.from_spog_request_queue", not(isEmptyOrNullString()))
              .body("data.from_spog_response_queue", not(isEmptyOrNullString()))
              // .body("data.to_spog_request_queue", not(isEmptyOrNullString()))
              // .body("data.to_spog_response_queue", not(isEmptyOrNullString()))
              .body("data.heartbeat_interval", equalTo(1800)).extract().body().asString();
      return site_configuration;

    } else {
      String code = expectedErrorMessage.getCodeString();
      String message = expectedErrorMessage.getStatus();
      if (code.contains("00400201")) {
        message = message.replace("{0}", site_id);
      }
      checkErrorCode(response, code);
      test.log(LogStatus.INFO, "The error code matched with the expected " + code);
      checkErrorMessage(response, message);
      test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
      return null;
    }
  }


  /**
   * post the job data by jobId
   * 
   * @author kiran.sripada
   * @param job_id
   * @param start_time_ts
   * @param server_id
   * @param resource_id
   * @param rps_id
   * @param datastore_id
   * @param organization_id
   * @param plan_id
   * @param message_job_id
   * @param message_job_type
   * @param message_job_method
   * @param message_job_status
   * @param message_resource_id
   * @param message_organization_id
   * @param message_server_id
   * @param message_rps_id
   * @param message_datastore_id
   * @param message_plan_id
   * @param message_start_time_ts
   * @param message_end_time_ts
   * @param extenttest object
   * 
   */

  public void postJobAndCheck(String sitetoken, String site_id, String job_id, long start_time_ts,
      String server_id, String resource_id, String rps_id, String datastore_id,
      String organization_id, String plan_id, String message_job_id, String message_job_type,
      String message_job_method, String message_job_status, String message_resource_id,
      String message_organization_id, String message_server_id, String message_rps_id,
      String message_datastore_id, String message_plan_id, long message_start_time_ts,
      long message_end_time_ts, ExtentTest test) {

    Response response =
        gatewayInvoker.createJob(jp.composeJobInfoMap(site_id, job_id, start_time_ts, server_id,
            resource_id, rps_id, datastore_id, organization_id, plan_id, message_job_id,
            message_job_type, message_job_method, message_job_status, message_resource_id,
            message_organization_id, message_server_id, message_rps_id, message_datastore_id,
            message_plan_id, message_start_time_ts, message_end_time_ts), sitetoken);
    test.log(LogStatus.INFO, "response status for create job " + response.getStatusCode());
    checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
    errorHandle.printDebugMessageInDebugFile("response for create job " + response);

    /*test.log(LogStatus.INFO, "check response body ");
    response.then().body("data.job_id", equalTo(job_id))
        .body("data.start_time_ts", equalTo(start_time_ts))
        .body("data.server_id", equalTo(server_id)).body("data.source_id", equalTo(resource_id))
        .body("data.rps_id", equalTo(rps_id)).body("data.datastore_id", equalTo(datastore_id))
        .body("data.organization_id", equalTo(organization_id))
        .body("data.plan_id", equalTo(plan_id)).body("data.message.job_id", equalTo(message_job_id))
        // .body("data.message.job_type", equalTo(message_job_type))
        .body("data.message.job_method", equalTo(message_job_method))
        // .body("data.message.job_status", equalTo(message_job_status))
        .body("data.message.resource_id", equalTo(message_resource_id))
        .body("data.message.organization_id", equalTo(message_organization_id))
        .body("data.message.server_id", equalTo(message_server_id))
        .body("data.message.rps_id", equalTo(message_rps_id))
        .body("data.message.datastore_id", equalTo(message_datastore_id))
        .body("data.message.plan_id", equalTo(message_plan_id))
        .body("data.message.start_time_ts", equalTo(message_start_time_ts))
        .body("data.message.end_time_ts", equalTo(message_end_time_ts));*/

  }


  /**
   * post the job data by jobId
   * 
   * @author kiran.sripada
   * @param job_id
   * @param job_id
   * @param job_seq
   * @param job_type
   * @param job_method
   * @param job_status
   * @param resource_id
   * @param server_id
   * @param rps_id
   * @param datastore_id
   * @param start_time_ts
   * @param end_time_ts
   * @param protected_data_size
   * @param raw_data_size
   * @param sync_read_size
   * @param ntfs_volume_size
   * @param virtual_disk_provision_size
   * @param extenttest object
   * 
   */
  // public void postJobDatawithCheck(String job_id_url, String job_id, String
  // job_seq, String
  // job_type, String job_method, String job_status,
  // String resource_id, String server_id, String rps_id, String datastore_id,long
  // start_time_ts,long end_time_ts,
  // String protected_data_size, String raw_data_size, String sync_read_size,
  // String
  // ntfs_volume_size, String virtual_disk_provision_size, ExtentTest test) {
  // test.log(LogStatus.INFO, "Compose the hash map and invoke the URI");
  // Response response = gatewayInvoker.createJobData(job_id_url,
  // jp.composeJobDataInfoMap(job_id, job_seq,job_type, job_method, job_status,
  // resource_id,
  // server_id, rps_id, datastore_id, start_time_ts,end_time_ts,
  // protected_data_size, raw_data_size, sync_read_size, ntfs_volume_size,
  // virtual_disk_provision_size));
  //
  // test.log(LogStatus.INFO, "response status for create job data " +
  // response.getStatusCode());
  // checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
  // errorHandle.printDebugMessageInDebugFile("response for create job data " +
  // response);
  //
  // test.log(LogStatus.INFO, "check response body "
  // +response.then().extract().asString());
  // /*response.then().body("data.job_id", equalTo(job_id)).body("data.job_seq",
  // equalTo(job_seq)).body("data.job_type", equalTo("Backup - Full"))
  // .body("data.job_status", equalTo(job_status)).body("data.resource_id",
  // equalTo(resource_id)).body("data.server_id", equalTo(server_id))
  // .body("data.rps_id", equalTo(rps_id)).body("data.datastore_id",
  // equalTo(datastore_id))
  // .body("data.start_time_ts", equalTo(start_time_ts))
  // .body("data.end_time_ts", equalTo(end_time_ts))
  // .body("data.protected_data_size",equalTo(protected_data_size)).body("data.raw_data_size",
  // equalTo(raw_data_size))
  // .body("data.sync_read_size",equalTo(sync_read_size)).body("data.ntfs_volume_size",
  // equalTo(ntfs_volume_size))
  // .body("data.virtual_disk_provision_size",
  // equalTo(virtual_disk_provision_size))*/;
  // }

  /**
   * general method to check response status
   * 
   * @author kiran.sripada
   * @param response
   * @param expectedStatusCode
   */
  public void checkResponseStatus(Response response, int expectedStatusCode) {

    errorHandle.printDebugMessageInDebugFile("Response status is " + response.getStatusCode());
    response.then().statusCode(expectedStatusCode);
  }


  /**
   * get the actual jobs that user has sent
   * 
   * @author kiran.sripada
   * @return jobsInfo
   * 
   */
  public HashMap<String, HashMap<String, Object>> getJobsDetails() {

    return jp.getjobsInfo();
  }


  /**
   * get the actual jobs data that user has sent
   * 
   * @author kiran.sripada
   * @return jobsInfo
   * 
   */
  public HashMap<String, Object> getJobData() {

    return jp.getjobdata();
  }


  /**
   * Create a site/register a site/login to site under an organization
   * 
   * @author kiran.sripada
   * @param sub_org_Id -- Organization id in which the site should be created
   * @param validToken -- Token to create the site
   * @param user_id -- User that creates the site
   * @param gateway_hostname
   * @param site_version
   * @param spogServer object
   * @param ExtentTest object
   * @return site_id
   * 
   */
  public String createsite_register_login(String sub_org_Id, String validToken, String user_id,
      String gateway_hostname, String site_version, SPOGServer spogServer, ExtentTest test) {

    String site_registration_key = null;
    String siteName = spogServer.getRandomSiteName("TestCreate");
    test.log(LogStatus.INFO, "Generated a Random SiteName " + siteName);
    String sitetype = siteType.gateway.toString();
    test.log(LogStatus.INFO, "The siteType :" + sitetype);
    test.log(LogStatus.INFO, "Creating a site For Logged in user");
    Response response = spogServer.createSite(siteName, sitetype, sub_org_Id, validToken, test);
    Map<String, String> sitecreateResMap = new HashMap<>();
    test.log(LogStatus.INFO, "Check the created site");
    sitecreateResMap = spogServer.checkCreateSite(response, SpogConstants.SUCCESS_POST, siteName,
        sitetype, sub_org_Id, user_id, "", test);
    String registration_basecode = sitecreateResMap.get("registration_basecode");
    String site_id = sitecreateResMap.get("site_id");
    try {

      String decoded = URLDecoder.decode(registration_basecode.trim(), "UTF-8");
      Base64Coder base64 = new Base64Coder();
      String decrypted = base64.decode(decoded);
      String[] parts = decrypted.split("\\n", -2);
      site_registration_key = parts[1];
      // test.log(LogStatus.INFO, "Decoded base64code is : "+parts[1]);

    } catch (UnsupportedEncodingException e) {
      test.log(LogStatus.FAIL, "The value of the error Message :" + e.getMessage());
    }
    test.log(LogStatus.INFO,
        "After decoding the site_registration_key is: " + site_registration_key);
    String gateway_id = UUID.randomUUID().toString();
    test.log(LogStatus.INFO, "Randomly generated gateway_id is: " + gateway_id);
    gateway_hostname = RandomStringUtils.randomAlphanumeric(4) + "_" + gateway_hostname;
    test.log(LogStatus.INFO, "Randomly generated gateway_hostname is: " + gateway_hostname);
    boolean is_registered = true;
    test.log(LogStatus.INFO, "Registering the gateway to site");
    response = RegisterSite(site_registration_key, gateway_id, gateway_hostname, site_version,
        site_id, test);
    String site_secret =
        checkRegisterSite(response, ServerResponseCode.Succes_Login, site_id, siteName, sitetype,
            sub_org_Id, user_id, is_registered, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
    test.log(LogStatus.INFO, "The secret key is :" + site_secret);
    response = LoginSite(site_id, site_secret, gateway_id, test);
    test.log(LogStatus.INFO, "validate the login site response ");
    checksiteLogin(response, ServerResponseCode.Succes_Login, "", test);
    return site_id;
  }


  public void checkErrorMessage(Response response, String expectedErrorMessage) {

    // response.then().assertThat().body(containsString((expectedErrorMessage)));
    List<String> messageArray = response.body().jsonPath().getList("errors.message");
    List<String> detailArray = response.body().jsonPath().getList("errors.detailMessage");
    System.out.println("The value of the message :" + messageArray.get(0));
    System.out.println("The value of the detail Message:" + detailArray.get(0));
    boolean find = false;
    for (int i = 0; i < messageArray.size(); i++) {
      if (messageArray.get(i).contains(expectedErrorMessage)) {
        find = true;
        break;
      }
    }
    for (int i = 0; i < detailArray.size(); i++) {
      if (detailArray.get(i).contains(expectedErrorMessage)) {
        find = true;
        break;
      }
    }
    if (find) {
      assertTrue("error message check is correct", true);
    } else {
      assertEquals(messageArray.get(0), expectedErrorMessage);
    }

  }


  /**
   * post job, changed in sprint6;
   * 
   * @author Zhaoguo.Ma
   * @param startTimeTS
   * @param serverID
   * @param resourceID
   * @param rpsID
   * @param datastoreID
   * @param siteID
   * @param organizationID
   * @param messageID
   * @param policyID
   * @param messageData
   * @param siteToken
   */

  public String postJobWithCheck(long startTimeTS, long endTimeTS, String organizationID,
      String serverID, String resourceID, String rpsID, String destinationID, String policyID,
      String jobType, String jobMethod, String jobStatus, String siteToken, ExtentTest test) {

	  Map<String, Object> jobInfo = jp.composeJobInfo(startTimeTS, endTimeTS, organizationID,
        serverID, resourceID, rpsID, destinationID, policyID, jobType, jobMethod, jobStatus);

	  String jobID = UUID.randomUUID().toString();
	  jobInfo.put("job_id", jobID);
	  
	  Response response = gatewayInvoker.createJob(jobInfo, siteToken);
	  checkResponseStatus(response, SpogConstants.SUCCESS_POST);
    
    // TODO add check points
    /* Commenting since from Sprint 40 jobs api changed to Async
     * No data will be there in response only status code
     * 
     * String responseServerID = response.then().extract().path("data.server_id");
    assertResponseItem(serverID, responseServerID);

    String responseResourceID = response.then().extract().path("data.source_id");
    assertResponseItem(resourceID, responseResourceID);

    String responseRpsID = response.then().extract().path("data.rps_id");
    assertResponseItem(rpsID, responseRpsID);

    String responseDestinationID = response.then().extract().path("data.destination_id");
    assertResponseItem(destinationID, responseDestinationID);

    // String responsePolicyID = response.then().extract().path("data.policy_id");
    // assertResponseItem(policyID, responsePolicyID);

    String responseJobType = response.then().extract().path("data.job_type");
    validateResponseJobType(jobType, jobMethod, responseJobType);*/

    return jobID;
  }


  public void validateResponseJobType(String jobType, String jobMethod, String responseJobType) {

    if (jobType.equalsIgnoreCase("backup") || jobType.equalsIgnoreCase("vm_backup")
        || jobType.equalsIgnoreCase("office365_backup") || jobType.equalsIgnoreCase("cifs_backup")
        || jobType.equalsIgnoreCase("sharepoint_backup")) {
      if (jobMethod == null) {
        assertEquals("backup", responseJobType);
      } else if (jobMethod.equalsIgnoreCase("full") || jobMethod.equalsIgnoreCase("incremental")) {
        assertEquals("backup_" + jobMethod, responseJobType);
      } else if (jobMethod.equalsIgnoreCase("resync")) {
        assertEquals("backup_verified", responseJobType);
      } else {
        assertEquals("backup", responseJobType);
      }
    } else {
      assertEquals(jobType, responseJobType);
    }
  }


  public Response postJobWithCheck(String jobID, long startTimeTS, long endTimeTS,
      String organizationID, String serverID, String resourceID, String rpsID, String destinationID,
      String policyID, String jobType, String jobMethod, String jobStatus, String siteToken,
      ExtentTest test) {

    Map<String, Object> jobInfo = jp.composeJobInfo(jobID, startTimeTS, endTimeTS, organizationID,
        serverID, resourceID, rpsID, destinationID, policyID, jobType, jobMethod, jobStatus);

    Response response = gatewayInvoker.createJob(jobInfo, siteToken);
  
    checkResponseStatus(response, SpogConstants.SUCCESS_POST);
  
    // TODO add check points
    
    try {
		Thread.sleep(2000);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    
    return response;
  }


  public void postJobWithCodeCheck(String jobID, long startTimeTS, long endTimeTS,
      String organizationID, String serverID, String resourceID, String rpsID, String destinationID,
      String policyID, String jobType, String jobMethod, String jobStatus, String siteToken,
      int statusCode, String errorCode, ExtentTest test) {

    Map<String, Object> jobInfo = jp.composeJobInfo(jobID, startTimeTS, endTimeTS, organizationID,
        serverID, resourceID, rpsID, destinationID, policyID, jobType, jobMethod, jobStatus);

    Response response = gatewayInvoker.createJob(jobInfo, siteToken);
    checkResponseStatus(response, statusCode);
    checkErrorCode(response, errorCode);

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
        assertTrue("compare " + item + " failed, actual result is " + response_item, false);
      }
    }
  }


  public void postJobWithCodeCheck(long startTimeTS, long endTimeTS, String organizationID,
      String serverID, String resourceID, String rpsID, String destinationID, String policyID,
      String jobType, String jobMethod, String jobStatus, String siteToken, int statusCode,
      String expectedErrorCode, ExtentTest test) {

    // Map<String, Object> jobInfo = jp.composeJobInfo(startTimeTS, serverID,
    // resourceID, rpsID,
    // datastoreID, siteID, organizationID, policyID, messageID,
    // jobMessageDataInfo);
    //
    Map<String, Object> jobInfo = jp.composeJobInfo(startTimeTS, endTimeTS, organizationID,
        serverID, resourceID, rpsID, destinationID, policyID, jobType, jobMethod, jobStatus);

    Response response = gatewayInvoker.createJob(jobInfo, siteToken);
    checkResponseStatus(response, statusCode);
    checkErrorCode(response, expectedErrorCode);

  }


  // public void postJobDataWithCheck(String job_id_url, String job_id, String
  // job_seq, String job_method, String job_type, String job_status, String
  // end_time_ts,
  // String protected_data_size, String raw_data_size, String sync_read_size,
  // String ntfs_volume_size, String virtual_disk_provision_size, String token,
  // ExtentTest test) {
  //
  // Map<String, String> jobDataInfo = null;
  // jobDataInfo = jp.composeJobDataInfoMap(job_id, job_seq, job_type, job_method,
  // job_status, end_time_ts, protected_data_size, raw_data_size, sync_read_size,
  // ntfs_volume_size,
  // virtual_disk_provision_size);
  // Response response = gatewayInvoker.createJobData(job_id_url, jobDataInfo,
  // token);
  // checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
  //
  // response.then().body("data.job_id", equalTo(job_id_url));
  //
  // String responseJobSeq = response.then().extract().path("data.job_seq");
  // assertResponseItem(job_seq, responseJobSeq);
  //
  //// String responseJobMethod =
  // response.then().extract().path("data.job_method");
  //// assertResponseItem(job_method, responseJobMethod);
  ////
  //// String responseJobType = response.then().extract().path("data.job_type");
  //// assertResponseItem(job_type, responseJobType);
  ////
  //// String responseJobStatus =
  // response.then().extract().path("data.job_status");
  //// assertResponseItem(job_status, responseJobStatus);
  //
  // String responseProtectedDataSize =
  // response.then().extract().path("data.protected_data_size");
  // assertResponseItem(protected_data_size, responseProtectedDataSize);
  //
  // String responseRawDataSize =
  // response.then().extract().path("data.raw_data_size");
  // assertResponseItem(raw_data_size, responseRawDataSize);
  //
  // String responseSyncReadSize =
  // response.then().extract().path("data.sync_read_size");
  // assertResponseItem(sync_read_size, responseSyncReadSize);
  //
  // String responseNtfsVolumeSize =
  // response.then().extract().path("data.ntfs_volume_size");
  // assertResponseItem(ntfs_volume_size, responseNtfsVolumeSize);
  //
  // String responseVirtualDiskProvisionSize =
  // response.then().extract().path("data.virtual_disk_provision_size");
  // assertResponseItem(virtual_disk_provision_size,
  // responseVirtualDiskProvisionSize);
  //
  // // TODO add check points
  // }
  /*
   * public void postJobDataWithCheck(String job_id, String job_seq, String severity, Double
   * percent_complete, String protected_data_size, String raw_data_size, String sync_read_size,
   * String ntfs_volume_size, String virtual_disk_provision_size, String token, ExtentTest test) {
   * 
   * Map<String, Object> jobDataInfo = null; jobDataInfo = jp.composeJobDataInfoMap(job_seq,
   * severity, percent_complete, protected_data_size, raw_data_size, sync_read_size,
   * ntfs_volume_size, virtual_disk_provision_size); Response response =
   * gatewayInvoker.createJob(job_id, jobDataInfo, token); checkResponseStatus(response,
   * SpogConstants.SUCCESS_GET_PUT_DELETE);
   * 
   * String responseJobSeq = response.then().extract().path("data.job_seq");
   * assertResponseItem(job_seq, responseJobSeq);
   * 
   * String responseSeverity = response.then().extract().path("data.job_severity"); //
   * assertResponseItem(severity, responseSeverity);
   * 
   * // Double responsePercentComplete = response.then().extract().path("data.percent_complete"); //
   * if(percent_complete==responsePercentComplete){ //assertTrue("compare percent_complete passed",
   * true); // }else{ // assertTrue("compare percent_complete passed", false); // }
   * 
   * String responseProtectedDataSize = response.then().extract().path("data.protected_data_size");
   * assertResponseItem(protected_data_size, responseProtectedDataSize);
   * 
   * String responseRawDataSize = response.then().extract().path("data.raw_data_size");
   * assertResponseItem(raw_data_size, responseRawDataSize);
   * 
   * String responseSyncReadSize = response.then().extract().path("data.sync_read_size");
   * assertResponseItem(sync_read_size, responseSyncReadSize);
   * 
   * String responseNtfsVolumeSize = response.then().extract().path("data.ntfs_volume_size");
   * assertResponseItem(ntfs_volume_size, responseNtfsVolumeSize);
   * 
   * String responseVirtualDiskProvisionSize =
   * response.then().extract().path("data.virtual_disk_provision_size");
   * assertResponseItem(virtual_disk_provision_size, responseVirtualDiskProvisionSize);
   * 
   * // TODO add check points }
   */

  public void postJobDataWithCheck(String job_id, String job_seq, String severity,
      Double percent_complete, String protected_data_size, String raw_data_size,
      String sync_read_size, String ntfs_volume_size, String virtual_disk_provision_size,
      String token, ExtentTest test) {

    Map<String, Object> jobDataInfo = null;

    jobDataInfo = jp.composeJobDataInfoMap(job_seq, severity, percent_complete, protected_data_size,
        raw_data_size, sync_read_size, ntfs_volume_size, virtual_disk_provision_size);
    Response response = gatewayInvoker.createJob(job_id, jobDataInfo, token);
    checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);

    /*String responseJobSeq = response.then().extract().path("data.job_seq");
    assertResponseItem(job_seq, responseJobSeq);*/

  }


  public void postJobDataWithCheck(String job_id, String job_seq, String severity,
      String percent_complete, String protected_data_size, String raw_data_size,
      String sync_read_size, String ntfs_volume_size, String virtual_disk_provision_size,
      String token, ExtentTest test) {

    Map<String, String> jobDataInfo = null;
    jobDataInfo = jp.composeJobDataInfoMap(job_seq, severity, percent_complete, protected_data_size,
        raw_data_size, sync_read_size, ntfs_volume_size, virtual_disk_provision_size);
    Response response = gatewayInvoker.createJobData(job_id, jobDataInfo, token);
    checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);

    /*String responseJobSeq = response.then().extract().path("data.job_data.job_seq");
    assertResponseItem(job_seq, responseJobSeq);

    String responseSeverity = response.then().extract().path("data.job_data.severity");
    assertResponseItem(severity, responseSeverity);

    String responsePercentComplete =
        response.then().extract().path("data.job_data.percent_complete");
    if (percent_complete == null || percent_complete == ""
        || percent_complete.equalsIgnoreCase("none")) {
      assertNull(responsePercentComplete);
    } else {
      if (percent_complete.equals(responsePercentComplete)) {
        assertTrue("compare percent_complete passed", true);
      } else {
        assertTrue("compare percent_complete passed", false);
      }
    }

    String responseProtectedDataSize =
        response.then().extract().path("data.job_data.protected_data_size");
    assertResponseItem(protected_data_size, responseProtectedDataSize);

    String responseRawDataSize = response.then().extract().path("data.job_data.raw_data_size");
    assertResponseItem(raw_data_size, responseRawDataSize);

    String responseSyncReadSize = response.then().extract().path("data.job_data.sync_read_size");
    assertResponseItem(sync_read_size, responseSyncReadSize);

    String responseNtfsVolumeSize =
        response.then().extract().path("data.job_data.ntfs_volume_size");
    assertResponseItem(ntfs_volume_size, responseNtfsVolumeSize);

    String responseVirtualDiskProvisionSize =
        response.then().extract().path("data.job_data.virtual_disk_provision_size");
    assertResponseItem(virtual_disk_provision_size, responseVirtualDiskProvisionSize);*/

    // TODO add check points
  }


  // Kiran.Sripada
  // This is a overloading function that has all the parameters as per sprint 12
  public void postJobDataWithCheck(String job_id, String job_seq, String severity,
      String percent_complete, String protected_data_size, String raw_data_size,
      String sync_read_size, String ntfs_volume_size, String virtual_disk_provision_size,
      String error_count, String warning_count, String processed_bytes_changed,
      String processed_bytes_processed, String processed_directories, String processed_files,
      String transferred_bytes, String transferred_directories, String transferred_files,
      String transferred_uncompressed_bytes, String token, ExtentTest test) {

    Map<String, String> jobDataInfo = null;
    jobDataInfo = jp.composeJobDataInfoMap(job_id, job_seq, severity, percent_complete,
        protected_data_size, raw_data_size, sync_read_size, ntfs_volume_size,
        virtual_disk_provision_size, error_count, warning_count, processed_bytes_changed,
        processed_bytes_processed, processed_directories, processed_files, transferred_bytes,
        transferred_directories, transferred_files, transferred_uncompressed_bytes);
    Response response = gatewayInvoker.createJobData(job_id, jobDataInfo, token);
    checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);

    /*String responseJobSeq = response.then().extract().path("data.job_data.job_seq");
    assertResponseItem(job_seq, responseJobSeq);

    String responseSeverity = response.then().extract().path("data.job_data.severity");
    assertResponseItem(severity, responseSeverity);

    String responsePercentComplete =
        response.then().extract().path("data.job_data.percent_complete");
    if (percent_complete == null || percent_complete == ""
        || percent_complete.equalsIgnoreCase("none")) {
      assertNull(responsePercentComplete);
    } else {
      if (Float.parseFloat(percent_complete) == Float.valueOf(responsePercentComplete)) {
        assertTrue("compare percent_complete passed", true);
      } else {
        assertTrue("compare percent_complete passed", false);
      }
    }

    String responseProtectedDataSize =
        response.then().extract().path("data.job_data.protected_data_size");
    assertResponseItem(protected_data_size, responseProtectedDataSize);

    String responseRawDataSize = response.then().extract().path("data.job_data.raw_data_size");
    assertResponseItem(raw_data_size, responseRawDataSize);

    String responseSyncReadSize = response.then().extract().path("data.job_data.sync_read_size");
    assertResponseItem(sync_read_size, responseSyncReadSize);

    String responseNtfsVolumeSize =
        response.then().extract().path("data.job_data.ntfs_volume_size");
    assertResponseItem(ntfs_volume_size, responseNtfsVolumeSize);

    String responseVirtualDiskProvisionSize =
        response.then().extract().path("data.job_data.virtual_disk_provision_size");
    assertResponseItem(virtual_disk_provision_size, responseVirtualDiskProvisionSize);

    String response_error_count = response.then().extract().path("data.job_data.error_count");
    assertResponseItem(error_count, response_error_count);
    String response_warning_count = response.then().extract().path("data.job_data.warning_count");
    assertResponseItem(warning_count, response_warning_count);
    String response_processed_bytes_changed =
        response.then().extract().path("data.job_data.processed_bytes_changed");
    assertResponseItem(processed_bytes_changed, response_processed_bytes_changed);
    String response_processed_bytes_processed =
        response.then().extract().path("data.job_data.processed_bytes_processed");
    assertResponseItem(processed_bytes_processed, response_processed_bytes_processed);
    String response_processed_directories =
        response.then().extract().path("data.job_data.processed_directories");
    assertResponseItem(processed_directories, response_processed_directories);
    String response_processed_files =
        response.then().extract().path("data.job_data.processed_files");
    assertResponseItem(processed_files, response_processed_files);
    String response_transferred_bytes =
        response.then().extract().path("data.job_data.transferred_bytes");
    assertResponseItem(transferred_bytes, response_transferred_bytes);
    String response_transferred_directories =
        response.then().extract().path("data.job_data.transferred_directories");
    assertResponseItem(transferred_directories, response_transferred_directories);
    String response_transferred_files =
        response.then().extract().path("data.job_data.transferred_files");
    assertResponseItem(transferred_files, response_transferred_files);
    String response_transferred_uncompressed_bytes =
        response.then().extract().path("data.job_data.transferred_uncompressed_bytes");
    assertResponseItem(transferred_uncompressed_bytes, response_transferred_uncompressed_bytes);*/
  }


  public void postJobDataWithCodeCheck(String job_id, String job_seq, String severity,
      String percent_complete, String protected_data_size, String raw_data_size,
      String sync_read_size, String ntfs_volume_size, String virtual_disk_provision_size,
      String bucket_id, String error_count, String warning_count, String processed_bytes_changed,
      String processed_bytes_processed, String processed_directories, String processed_files,
      String transferred_bytes, String transferred_directories, String transferred_files,
      String transferred_uncompressed_bytes, int status_code, String errorCode, String token,
      ExtentTest test) {

    Map<String, String> jobDataInfo = null;
    jobDataInfo = jp.composeJobDataInfoMap(job_id, job_seq, severity, percent_complete,
        protected_data_size, raw_data_size, sync_read_size, ntfs_volume_size,
        virtual_disk_provision_size, error_count, warning_count, processed_bytes_changed,
        processed_bytes_processed, processed_directories, processed_files, transferred_bytes,
        transferred_directories, transferred_files, transferred_uncompressed_bytes);
    Response response = gatewayInvoker.createJobData(job_id, jobDataInfo, token);
    checkResponseStatus(response, status_code);
    checkErrorCode(response, errorCode);

  }


  public Response postJobWithStatusCode(long startTimeTS, long endTimeTS, String organizationID,
      String serverID, String resourceID, String rpsID, String destinationID, String policyID,
      String jobType, String jobMethod, String jobStatus, String siteToken, int statusCode,
      ExtentTest test) {

    // Map<String, String> jobMessageDataInfo = null;
    // if (null != messageData) {
    // jobMessageDataInfo = jp.composeJobMessageDataInfo(messageData);
    // }

    // Map<String, Object> jobInfo = jp.composeJobInfo(startTimeTS, serverID,
    // resourceID, rpsID,
    // datastoreID, siteID, organizationID, policyID, messageID,
    // jobMessageDataInfo);

    Map<String, Object> jobInfo = jp.composeJobInfo(startTimeTS, endTimeTS, organizationID,
        serverID, resourceID, rpsID, destinationID, policyID, jobType, jobMethod, jobStatus);

    Response response = gatewayInvoker.createJob(jobInfo, siteToken);
    checkResponseStatus(response, statusCode);
    return response;
    // TODO add check points
  }


  public void checkErrorCode(Response response, String expectedErrorCode) {

    // response.then().assertThat().body(containsString((expectedErrorMessage)));
    List<String> messageArray = response.body().jsonPath().getList("errors.code");
    boolean find = false;
    for (int i = 0; i < messageArray.size(); i++) {
      if (messageArray.get(i).contains(expectedErrorCode)) {
        find = true;
        break;
      }
    }

    if (find) {
      assertTrue("error code check is correct", true);
    } else {
      assertEquals(messageArray.get(0), expectedErrorCode);
    }

  }


  public void postJobDataWithCodeCheck(String job_id, String job_seq, String severity,
      String percent_complete, String protected_data_size, String raw_data_size,
      String sync_read_size, String ntfs_volume_size, String virtual_disk_provision_size,
      String token, int status_code, String expectedErrorCode, ExtentTest test) {

    Map<String, String> jobDataInfo = null;
    jobDataInfo = jp.composeJobDataInfoMap(job_seq, severity, percent_complete, protected_data_size,
        raw_data_size, sync_read_size, ntfs_volume_size, virtual_disk_provision_size);
    Response response = gatewayInvoker.createJobData(job_id, jobDataInfo, token);
    checkResponseStatus(response, status_code);
    checkErrorCode(response, expectedErrorCode);
  }


  /**
   * post job
   * 
   * @author Kiran.sripada
   * @param startTimeTS
   * @param serverID
   * @param resourceID
   * @param rpsID
   * @param datastoreID
   * @param siteID
   * @param organizationID
   * @param messageID
   * @param policyID
   * @param messageData
   * @param siteToken
   * @return response
   */

  public Response postJob(long startTimeTS, long endTimeTS, String organizationID, String serverID,
	      String resourceID, String rpsID, String destinationID, String policyID, String jobType,
	      String jobMethod, String jobStatus, String siteToken, ExtentTest test) {

		  String job_id = UUID.randomUUID().toString();
		  Map<String, Object> jobInfo = jp.composeJobInfo(startTimeTS, endTimeTS, organizationID,
	        serverID, resourceID, rpsID, destinationID, policyID, jobType, jobMethod, jobStatus);
		  jobInfo.put("job_id", job_id);
		  Response response = gatewayInvoker.createJob(jobInfo, siteToken);
		  checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		  return response;
	  }


  /**
   * post job
   * 
   * @author Kiran.sripada
   * @param job_id_url
   * @param job_id
   * @param job_seq
   * @param job_type
   * @param job_method
   * @param job_status
   * @param end_time_ts
   * @param protected_data_size
   * @param raw_data_size
   * @param sync_read_size
   * @param ntfs_volume_size
   * @param virtual_disk_provision_size
   * @param site token
   * @param ExtentTest
   * @return response
   */
  public Response postJobData(String job_id, String job_seq, String severity, String percent_complete,
			String protected_data_size, String raw_data_size, String sync_read_size, String ntfs_volume_size,
			String virtual_disk_provision_size, String token, ExtentTest test) {

		Map<String, String> jobDataInfo = null;
		jobDataInfo = jp.composeJobDataInfoMap(job_seq, severity, percent_complete, protected_data_size,
				raw_data_size, sync_read_size, ntfs_volume_size, virtual_disk_provision_size);
		Response response = gatewayInvoker.createJobData(job_id, jobDataInfo, token);
		checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);

		/*HashMap<String, Object> actual_response = response.then().extract().path("data.job_data");

		 String responseJobSeq = response.then().extract().path("data.job_data.job_seq");
		 assertResponseItem(job_seq, responseJobSeq);
		assertResponseItem(actual_response.get("job_seq").toString(), job_seq);

		
		 * String responseSeverity =
		 * response.then().extract().path("data.job_severity");
		 * assertResponseItem(severity, responseSeverity);
		 

		assertResponseItem(actual_response.get("severity").toString(), severity);

		// String per = actual_response.get("percent_complete").toString();
		
		assertResponseItem(actual_response.get("percent_complete").toString(), percent_complete);

		if (actual_response.containsKey("percent_complete")) {
			Float responsePercentComplete = (Float) actual_response.get("percent_complete");
			if (Float.parseFloat(percent_complete) == responsePercentComplete) {
				test.log(LogStatus.PASS, "Compare percent_complete passed");
				assertTrue("compare percent_complete passed", true);
			} else {
				assertTrue("Compare percent_complete failed", false);
			}
		}

		
		 * Float responsePercentComplete =
		 * response.then().extract().path("data.percent_complete");
		 * if(Float.parseFloat(percent_complete)==responsePercentComplete){
		 * test.log(LogStatus.PASS, "Compare percent_complete passed");
		 * assertTrue("compare percent_complete passed", true); }else{
		 * test.log(LogStatus.FAIL, "Compare percent_complete failed");
		 * assertTrue("compare percent_complete failed", false); }
		 

		// String responseJobMethod = response.then().extract().path("data.job_method");
		// assertResponseItem(job_method, responseJobMethod);
		//
		// String responseJobType = response.then().extract().path("data.job_type");
		// assertResponseItem(job_type, responseJobType);
		//
		// String responseJobStatus = response.then().extract().path("data.job_status");
		// assertResponseItem(job_status, responseJobStatus);

		assertResponseItem(actual_response.get("protected_data_size").toString(), protected_data_size);

		assertResponseItem(actual_response.get("raw_data_size").toString(), raw_data_size);

		assertResponseItem(actual_response.get("sync_read_size").toString(), sync_read_size);

		assertResponseItem(actual_response.get("ntfs_volume_size").toString(), ntfs_volume_size);

		assertResponseItem(actual_response.get("virtual_disk_provision_size").toString(), virtual_disk_provision_size);*/

		return response;

		// TODO add check points
  }


  public Response postJobData(String job_id, String job_seq, String severity,
      String percent_complete, String protected_data_size, String raw_data_size,
      String sync_read_size, String ntfs_volume_size, String virtual_disk_provision_size,
      String error_count, String warning_count, String processed_bytes_changed,
      String processed_bytes_processed, String processed_directories, String processed_files,
      String transferred_bytes, String transferred_directories, String transferred_files,
      String transferred_uncompressed_bytes, String token, ExtentTest test) {

    Map<String, String> jobDataInfo = null;
    jobDataInfo = jp.composeJobDataInfoMap(job_id, job_seq, severity, percent_complete,
        protected_data_size, raw_data_size, sync_read_size, ntfs_volume_size,
        virtual_disk_provision_size, error_count, warning_count, processed_bytes_changed,
        processed_bytes_processed, processed_directories, processed_files, transferred_bytes,
        transferred_directories, transferred_files, transferred_uncompressed_bytes);
    Response response = gatewayInvoker.createJobData(job_id, jobDataInfo, token);
    checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);

   /* Commenting since from Sprint 40 jobs api changed to Async
    * No data will be there in response only status code
    * 
    * String responseJobSeq = response.then().extract().path("data.job_data.job_seq");
    assertResponseItem(job_seq, responseJobSeq);

    String responseSeverity = response.then().extract().path("data.job_data.severity");
    assertResponseItem(severity, responseSeverity);

    String responsePercentComplete =
        response.then().extract().path("data.job_data.percent_complete");
    if (percent_complete == null || percent_complete == ""
        || percent_complete.equalsIgnoreCase("none")) {
      assertNull(responsePercentComplete);
    } else {
      if (Float.parseFloat(percent_complete) == Float.valueOf(responsePercentComplete)) {
        assertTrue("compare percent_complete passed", true);
      } else {
        assertTrue("compare percent_complete passed", false);
      }
    }

    String responseProtectedDataSize =
        response.then().extract().path("data.job_data.protected_data_size");
    assertResponseItem(protected_data_size, responseProtectedDataSize);

    String responseRawDataSize = response.then().extract().path("data.job_data.raw_data_size");
    assertResponseItem(raw_data_size, responseRawDataSize);

    String responseSyncReadSize = response.then().extract().path("data.job_data.sync_read_size");
    assertResponseItem(sync_read_size, responseSyncReadSize);

    String responseNtfsVolumeSize =
        response.then().extract().path("data.job_data.ntfs_volume_size");
    assertResponseItem(ntfs_volume_size, responseNtfsVolumeSize);

    String responseVirtualDiskProvisionSize =
        response.then().extract().path("data.job_data.virtual_disk_provision_size");
    assertResponseItem(virtual_disk_provision_size, responseVirtualDiskProvisionSize);

    String response_error_count = response.then().extract().path("data.job_data.error_count");
    assertResponseItem(error_count, response_error_count);
    String response_warning_count = response.then().extract().path("data.job_data.warning_count");
    assertResponseItem(warning_count, response_warning_count);
    String response_processed_bytes_changed =
        response.then().extract().path("data.job_data.processed_bytes_changed");
    assertResponseItem(processed_bytes_changed, response_processed_bytes_changed);
    String response_processed_bytes_processed =
        response.then().extract().path("data.job_data.processed_bytes_processed");
    assertResponseItem(processed_bytes_processed, response_processed_bytes_processed);
    String response_processed_directories =
        response.then().extract().path("data.job_data.processed_directories");
    assertResponseItem(processed_directories, response_processed_directories);
    String response_processed_files =
        response.then().extract().path("data.job_data.processed_files");
    assertResponseItem(processed_files, response_processed_files);
    String response_transferred_bytes =
        response.then().extract().path("data.job_data.transferred_bytes");
    assertResponseItem(transferred_bytes, response_transferred_bytes);
    String response_transferred_directories =
        response.then().extract().path("data.job_data.transferred_directories");
    assertResponseItem(transferred_directories, response_transferred_directories);
    String response_transferred_files =
        response.then().extract().path("data.job_data.transferred_files");
    assertResponseItem(transferred_files, response_transferred_files);
    String response_transferred_uncompressed_bytes =
        response.then().extract().path("data.job_data.transferred_uncompressed_bytes");
    assertResponseItem(transferred_uncompressed_bytes, response_transferred_uncompressed_bytes);*/

    return response;
  }
  
  
  

  /**
   * 
   * @author shan.jing
   * @param job_id, String
   * @param expected_organization_id, String
   * @param organization_id, String
   * @param log_severity_type, 'spog', 'udp', 'cloud_direct'
   * @param log_source_type, 'information', 'warning', 'error'
   * @param message_id, String
   * @param message_data, String
   * @param token, String
   * @return log_id
   */

  public String createLogwithCheck(long log_generate_time, String log_id,String job_id,
	      String expected_organization_id, String organization_id, String source_id,
	      String log_severity_type, String log_source_type, String message_id, String message_data,
	      String token, ExtentTest test) {

	    String logID = UUID.randomUUID().toString();
	    Map<String, Object> logInfo = jp.composeLogInfo(log_generate_time, log_id,job_id, organization_id,
	        source_id, log_severity_type, log_source_type, message_id, message_data);
	    logInfo.put("log_id", logID);    
	    
	    Response response = gatewayInvoker.createLog(logInfo, token);
	    checkResponseStatus(response, SpogConstants.SUCCESS_POST);    
	        
	    /*checkLog(response, log_generate_time, job_id, expected_organization_id, organization_id,
	        source_id, log_severity_type, log_source_type, message_id, message_data);
	    String logID = response.then().extract().path("data.log_id");*/
	    return logID;
	  }
  
  public String createLogwithCheck(long log_generate_time, String job_id,
	      String expected_organization_id, String organization_id, String source_id,
	      String log_severity_type, String log_source_type, String message_id, String message_data,
	      String token, ExtentTest test) {

	    String logID = UUID.randomUUID().toString();
	    Map<String, Object> logInfo = jp.composeLogInfo(log_generate_time, logID, job_id, organization_id,
	        source_id, log_severity_type, log_source_type, message_id, message_data);
	    logInfo.put("log_id", logID);    
	    
	    Response response = gatewayInvoker.createLog(logInfo, token);
	    checkResponseStatus(response, SpogConstants.SUCCESS_POST);    
	        
	    /*checkLog(response, log_generate_time, job_id, expected_organization_id, organization_id,
	        source_id, log_severity_type, log_source_type, message_id, message_data);
	    String logID = response.then().extract().path("data.log_id");*/
	    return logID;
	  }
   
  public Response getGeneatedFroms4Log(String token, ExtentTest test) {
	    Response response=gatewayInvoker.getGeneratedFroms4Log(token);
	    return response;
	  }
  
  public void getGeneatedFroms4LogwithExpectedError(
	      String token,int expect_status,
	      String error_code, String error_message, ExtentTest test) {
	    Response response=gatewayInvoker.getGeneratedFroms4Log(token);
	    checkResponseStatus(response, expect_status);
	    checkErrorCode(response, error_code);
	    checkErrorMessage(response, error_message);
	  }
  
  public void checkGeneatedFromsContent(
		  Response response, String generatedfromnames, ExtentTest test) {
	  ArrayList<HashMap<String, Object>> generatedfromsInfo = response.then().extract().path("data");
	  String[] eachgeneratedfromnames;
	  boolean find=false;
	  if(generatedfromnames!=null){
		  eachgeneratedfromnames = generatedfromnames.split(";");
	  }else{
		  if(generatedfromsInfo!=null){
			 assertTrue("genereated froms is not expected",false); 			 
		  }else{
			  assertTrue("genereated froms is expected,the content is null",true); 
		  }
		  return;
	  }
	  if(generatedfromsInfo!=null){
		  for(int i=0; i<eachgeneratedfromnames.length; i++ ){
			  find=false;
			  for(int j=0; j<generatedfromsInfo.size(); j++ ){
				  if(generatedfromsInfo.get(j).get("generated_from_name").toString().equalsIgnoreCase(eachgeneratedfromnames[i])){
					  find=true;
					  break;
				  }
			  }
			  if(find){
				  assertTrue("can find generated from name is:"+eachgeneratedfromnames[i],true); 
			  }else{
				  assertTrue("can find generated from name is:"+eachgeneratedfromnames[i],false); 
				  return;
			  }
		  }
	  }
  }
  
  public Response createLogInbulk(String log_generate_time, String log_id,String job_id,
	      String expected_organization_id, String organization_id, String source_id,
	      String log_severity_type, String log_source_type, String message_id, String message_data,
	      String token, ExtentTest test) {
	    ArrayList<HashMap<String, Object>> logInbulkInfo = new ArrayList<HashMap<String, Object>>();
	    Response response=null;
	    HashMap<String, Object> log = new HashMap<String, Object>();
	    if(log_generate_time!=null){
	    	String[] log_generate_times=log_generate_time.split(",");
	    	int length=log_generate_times.length;
	    	String[] log_ids=log_id.split(",");
	    	String[] job_ids=job_id.split(",");
			String[] organization_ids=organization_id.split(",");
			String[] source_ids=source_id.split(",");
			String[] log_severity_types=log_severity_type.split(",");
			String[] log_source_types=log_source_type.split(",");
			String[] message_ids=message_id.split(",");
			String[] message_datas=message_data.split(",");
			for(int i=0;i<length;i++){
				logInbulkInfo.add(jp.composeLogInbulkInfo(Long.valueOf(log_generate_times[i]), log_ids[i],job_ids[i], organization_ids[i],
	        source_ids[i], log_severity_types[i], log_source_types[i], message_ids[i], message_datas[i]));
			}
	    }else{
	    	response = gatewayInvoker.createLogInbulk(null, token);
	    }
	    response = gatewayInvoker.createLogInbulk(logInbulkInfo, token);
	    return response;
	  }
  
  public void createLogInbulkwithCheck(String log_generate_time, String log_id,String job_id,
	      String expected_organization_id, String organization_id, String source_id,
	      String log_severity_type, String log_source_type, String message_id, String message_data,
	      String token, ExtentTest test) {
	    ArrayList<HashMap<String, Object>> logInbulkInfo = new ArrayList<HashMap<String, Object>>();
	    Response response=createLogInbulk( log_generate_time, log_id, job_id,
	       expected_organization_id,  organization_id,  source_id,
	       log_severity_type,  log_source_type,  message_id,  message_data,
	       token,  test);
	    checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
	  }
  
  public void createLogInbulkwithExpectedError(String log_generate_time, String log_id, String job_id,
	      String expected_organization_id, String organization_id, String source_id,
	      String log_severity_type, String log_source_type, String message_id, String message_data,
	      String token,int expect_status,
	      String error_code, String error_message, ExtentTest test) {
	    ArrayList<HashMap<String, Object>> logInbulkInfo = new ArrayList<HashMap<String, Object>>();
	    Response response=createLogInbulk( log_generate_time,  log_id,job_id,
	       expected_organization_id,  organization_id,  source_id,
	       log_severity_type,  log_source_type,  message_id,  message_data,
	       token,  test);
	    checkResponseStatus(response, expect_status);
	    checkErrorCode(response, error_code);
	    checkErrorMessage(response, error_message);
	  }
  
  public String createLogwithCheck(long log_generate_time, String log_id,String job_id,
      String expected_organization_id, String organization_id, String source_id,
      String log_severity_type, String log_source_type, String message_id, String message_data,
      String help_message_id, String token, ExtentTest test) {

//    Map<String, String> logMessageDataInfo = null;
    Map<String, Object> logInfo = jp.composeLogInfo(log_generate_time, log_id,job_id, organization_id,
        source_id, log_severity_type, log_source_type, message_id, help_message_id, message_data);
    String logID = UUID.randomUUID().toString();
    logInfo.put("log_id", logID);
    
    Response response = gatewayInvoker.createLog(logInfo, token);
    checkResponseStatus(response, SpogConstants.SUCCESS_POST);
    // TODO: add check points
    /*checkLog(response, log_generate_time, job_id, expected_organization_id, organization_id,
        source_id, log_severity_type, log_source_type, message_id, message_data, help_message_id);*/
    
    return logID;
  }


  public String createLogwithCheck(long log_generate_time,String log_id, String job_id,
      String expected_organization_id, String organization_id, String source_id,
      String log_severity_type, String log_source_type, String message_id, String message_data,
      String help_message_id, String job_type, String token, ExtentTest test) {

    Map<String, String> logMessageDataInfo = null;
    Map<String, Object> logInfo =
        jp.composeLogInfo(log_generate_time,log_id, job_id, organization_id, source_id, log_severity_type,
            log_source_type, message_id, help_message_id, message_data, job_type);
    Response response = gatewayInvoker.createLog(logInfo, token);
    checkResponseStatus(response, SpogConstants.SUCCESS_POST);
    // TODO: add check points
//    checkLog(response, log_generate_time, job_id, expected_organization_id, organization_id,
//        source_id, log_severity_type, log_source_type, message_id, message_data, help_message_id,
//        job_type);
//    String logID = response.then().extract().path("data.log_id");
    return log_id;
  }


  public void checkLog(Response response, long log_generate_time, String job_id,
      String expected_organization_id, String organization_id, String source_id,
      String log_severity_type, String log_source_type, String message_id, String message_data) {

    // TODO: add check points
    response.then().extract().path("data.log_ts").equals(log_generate_time);

    String responseJobID = response.then().extract().path("data.job_data.job_id");
    assertResponseItem(job_id, responseJobID);

    String responseOrganizationID = response.then().extract().path("data.organization.organization_id");
    assertResponseItem(expected_organization_id, responseOrganizationID);

    String responseSourceID = response.then().extract().path("data.job_data.source_id");
    assertResponseItem(source_id, responseSourceID);

    String responseLogseverityType = response.then().extract().path("data.log_severity_type");
    assertResponseItem(log_severity_type, responseLogseverityType);

    String responseLogSourceType = response.then().extract().path("data.log_source_type");
    assertResponseItem(log_source_type, responseLogSourceType);

    // String responseMessageID = response.then().extract().path("data.message_id");
    // assertResponseItem(message_id, responseMessageID);
    //
    // ArrayList<String> response_message_data =
    // response.then().extract().path("data.message_data");
    // if (message_data != null && message_data.equalsIgnoreCase("")) {
    // message_data = "emptyarray";
    // }
    // assertFilterItem(message_data, response_message_data);
  }


  public Response createLog(long log_generate_time, String log_id,String job_id, String expected_organization_id,
      String organization_id, String source_id, String log_severity_type, String log_source_type,
      String message_id, String message_data, String help_message_id, String token,
      ExtentTest test) {

    Map<String, String> logMessageDataInfo = null;
    Map<String, Object> logInfo = jp.composeLogInfo(log_generate_time, log_id,job_id, organization_id,
        source_id, log_severity_type, log_source_type, message_id, help_message_id, message_data);
    Response response = gatewayInvoker.createLog(logInfo, token);
    checkResponseStatus(response, SpogConstants.SUCCESS_POST);
    // TODO: add check points
//    checkLog(response, log_generate_time, job_id, expected_organization_id, organization_id,
//        source_id, log_severity_type, log_source_type, message_id, message_data, help_message_id);
    // String logID = response.then().extract().path("data.log_id");
    return response;
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


  public void checkLog(Response response, long log_generate_time, String job_id,
      String expected_organization_id, String organization_id, String source_id,
      String log_severity_type, String log_source_type, String message_id, String message_data,
      String help_message_id) {

    checkLog(response, log_generate_time, job_id, expected_organization_id, organization_id,
        source_id, log_severity_type, log_source_type, message_id, message_data);
    String responseHelpMessageID = response.then().extract().path("data.help_message_id");
    assertResponseItem(help_message_id, responseHelpMessageID);
  }


  public void checkLog(Response response, long log_generate_time, String job_id,
      String expected_organization_id, String organization_id, String source_id,
      String log_severity_type, String log_source_type, String message_id, String message_data,
      String help_message_id, String job_type) {

    checkLog(response, log_generate_time, job_id, expected_organization_id, organization_id,
        source_id, log_severity_type, log_source_type, message_id, message_data, help_message_id);
    String responseJobType = response.then().extract().path("data.job_type");
    assertResponseItem(job_type, responseJobType);
  }


  /**
   * create log failed and check statusCode
   * 
   * @author shan.jing
   * @param job_id, String
   * @param organization_id, String
   * @param log_severity_type, 'spog', 'udp', 'cloud_direct'
   * @param log_source_type, 'information', 'warning', 'error'
   * @param message_id, String
   * @param message_data, String
   * @param token, String
   * @param expectedStatusCode
   * @param expectedErrorCode
   * @param test
   */
  public void createLogFailedWithExpectedStatusCode(long log_generate_time, String log_id,String job_id,
      String organization_id, String source_id, String log_severity_type, String log_source_type,
      String message_id, String message_data, String token, int expectedStatusCode,
      String expectedErrorCode, ExtentTest test) {

    Map<String, String> logMessageDataInfo = null;
    Map<String, Object> logInfo = jp.composeLogInfo(log_generate_time, log_id,job_id, organization_id,
        source_id, log_severity_type, log_source_type, message_id, message_data);
    Response response = gatewayInvoker.createLog(logInfo, token);
    checkResponseStatus(response, expectedStatusCode);
    checkErrorCode(response, expectedErrorCode);
  }


  public void createLogFailedWithExpectedStatusCode(long log_generate_time, String log_id,String job_id,
      String organization_id, String source_id, String log_severity_type, String log_source_type,
      String message_id, String message_data, String help_message_id, String token,
      int expectedStatusCode, String expectedErrorCode, ExtentTest test) {

    Map<String, String> logMessageDataInfo = null;
    Map<String, Object> logInfo = jp.composeLogInfo(log_generate_time, log_id,job_id, organization_id,
        source_id, log_severity_type, log_source_type, message_id, help_message_id, message_data);
    Response response = gatewayInvoker.createLog(logInfo, token);
    checkResponseStatus(response, expectedStatusCode);
    checkErrorCode(response, expectedErrorCode);
  }


  /****
   * call the rest API to post the Logs
   * 
   * @param create_ts
   * @param site_token
   * @param job_id
   * @param site_id
   * @param organization_id
   * @param severityType
   * @param sourceType
   * @param message_id
   * @param messagedata
   * @param test
   * @return
   */

  public Response postLogs(long create_ts, String site_token, String log_id,String job_id, String source_id,
      String site_id, String organization_id, String severityType, String sourceType,
      String message_id, String messagedata, ExtentTest test) {

    TreeMap<String, String> message_data = null;
    if (messagedata != null) {
      message_data = jp.composeMessageData(messagedata, test);
    }
    // compose the TreeMap for logs
    TreeMap<String, Object> logInfo = jp.composeLogInfo(create_ts,log_id, job_id, organization_id,
        source_id, site_id, severityType, sourceType, message_id, message_data, test);

    Response response = gatewayInvoker.postJobs(site_token, logInfo, test);
    return response;

  }


  public void updateJobDataWithCheck(String job_id, String job_seq, String severity,
      String percent_complete, String protected_data_size, String raw_data_size,
      String sync_read_size, String ntfs_volume_size, String virtual_disk_provision_size,
      String token, ExtentTest test) {

    Map<String, String> jobDataInfo = null;
    jobDataInfo = jp.composeJobDataInfoMap(job_seq, severity, percent_complete, protected_data_size,
        raw_data_size, sync_read_size, ntfs_volume_size, virtual_disk_provision_size);
    Response response = gatewayInvoker.UpdateJobData(job_id, jobDataInfo, token);
    checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);

    // response.then().body("data.job_id", equalTo(job_id_url));
    String responseJobSeq = response.then().extract().path("data.job_data.job_seq");
    assertResponseItem(job_seq, responseJobSeq);
    //
    // String responseJobMethod = response.then().extract().path("data.job_method");
    // assertResponseItem(job_method, responseJobMethod);
    //
    // String responseJobType = response.then().extract().path("data.job_type");
    // assertResponseItem(job_type, responseJobType);
    //
    // String responseJobStatus = response.then().extract().path("data.job_status");
    // assertResponseItem(job_status, responseJobStatus);

    // String responseSeverity = response.then().extract().path("data.severity");
    // assertResponseItem(severity, responseSeverity);

    String responsePercentComplete =
        response.then().extract().path("data.job_data.percent_complete");
    assertResponseItem(percent_complete, responsePercentComplete);

    // String responseJobMethod = response.then().extract().path("data.job_method");
    // assertResponseItem(job_method, responseJobMethod);

    // String responseJobType = response.then().extract().path("data.job_type");
    // assertResponseItem(job_type, responseJobType);

    // String responseJobStatus = response.then().extract().path("data.job_status");
    // assertResponseItem(job_status, responseJobStatus);

    String responseProtectedDataSize =
        response.then().extract().path("data.job_data.protected_data_size");
    assertResponseItem(protected_data_size, responseProtectedDataSize);

    String responseRawDataSize = response.then().extract().path("data.job_data.raw_data_size");
    assertResponseItem(raw_data_size, responseRawDataSize);

    String responseSyncReadSize = response.then().extract().path("data.job_data.sync_read_size");
    assertResponseItem(sync_read_size, responseSyncReadSize);

    String responseNtfsVolumeSize =
        response.then().extract().path("data.job_data.ntfs_volume_size");
    assertResponseItem(ntfs_volume_size, responseNtfsVolumeSize);

    String responseVirtualDiskProvisionSize =
        response.then().extract().path("data.job_data.virtual_disk_provision_size");
    assertResponseItem(virtual_disk_provision_size, responseVirtualDiskProvisionSize);

  }


  /**
   * update job, changed in sprint6;
   * 
   * @author yin.li
   * @param startTimeTS
   * @param serverID
   * @param resourceID
   * @param rpsID
   * @param datastoreID
   * @param siteID
   * @param organizationID
   * @param messageID
   * @param policyID
   * @param messageData
   * @param siteToken
   */

  public void updateJobWithCheck(String jobID, long startTimeTS, long endTimeTS,
      String organizationID, String serverID, String resourceID, String rpsID, String destinationID,
      String policyID, String jobType, String jobMethod, String jobStatus, String siteToken,
      ExtentTest test) {

    // Map<String, String> jobMessageDataInfo = null;
    // if (null != messageData) {
    // jobMessageDataInfo = jp.composeJobMessageDataInfo(messageData);
    // }

    // Map<String, Object> jobInfo = jp.composeJobInfo(startTimeTS, serverID,
    // resourceID, rpsID,
    // datastoreID, siteID, organizationID, policyID, messageID,
    // jobMessageDataInfo);
    //
    Map<String, Object> jobInfo = jp.composeJobInfo(startTimeTS, endTimeTS, organizationID,
        serverID, resourceID, rpsID, destinationID, policyID, jobType, jobMethod, jobStatus);

    Response response = gatewayInvoker.updateJob(jobID, jobInfo, siteToken);
    checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
    // TODO add check points
    String responseServerID = response.then().extract().path("data.server_id");
    assertResponseItem(serverID, responseServerID);

    String responseResourceID = response.then().extract().path("data.source_id");
    assertResponseItem(resourceID, responseResourceID);

    String responseRpsID = response.then().extract().path("data.rps_id");
    assertResponseItem(rpsID, responseRpsID);

    String responseDatastoreID = response.then().extract().path("data.destination_id");
    assertResponseItem(destinationID, responseDatastoreID);

    String responsePolicyID = response.then().extract().path("data.policy_policy_id");
    assertResponseItem(policyID, responsePolicyID);

    // HashMap<String, String> responseMessageData =
    // response.then().extract().path("data.message_data");
    // if (null == messageData || (messageData.toString() == "" ||
    // messageData.length == 0)) {
    // assertNull(responseMessageData, "compare " + messageData + " passed once we
    // don't set it");
    // } else {
    // int length = messageData.length;
    // if (length != responseMessageData.size()) {
    // assertTrue("compare message_data length passed", false);
    // }
    // for (int i = 0; i < length; i++) {
    // if
    // (!responseMessageData.get(String.valueOf(i)).equalsIgnoreCase(messageData[i]))
    // {
    // assertTrue("compare message_data " + messageData[i] + "failed", false);
    // }
    // }
    // }
  }


  public Response updateJob(String jobID, long startTimeTS, long endTimeTS, String organizationID,
      String serverID, String resourceID, String rpsID, String destinationID, String policyID,
      String jobType, String jobMethod, String jobStatus, String siteToken, ExtentTest test) {

    // Map<String, String> jobMessageDataInfo = null;
    // if (null != messageData) {
    // jobMessageDataInfo = jp.composeJobMessageDataInfo(messageData);
    // }

    // Map<String, Object> jobInfo = jp.composeJobInfo(startTimeTS, serverID,
    // resourceID, rpsID,
    // datastoreID, siteID, organizationID, policyID, messageID,
    // jobMessageDataInfo);

    Map<String, Object> jobInfo = jp.composeJobInfo(startTimeTS, endTimeTS, organizationID,
        serverID, resourceID, rpsID, destinationID, policyID, jobType, jobMethod, jobStatus);

    Response response = gatewayInvoker.updateJob(jobID, jobInfo, siteToken);
    return response;
  }


  public Response updateJobData(String job_id_url, String job_id, String job_seq, String job_type,
      String job_method, String job_status, String end_time_ts, String protected_data_size,
      String raw_data_size, String sync_read_size, String ntfs_volume_size,
      String virtual_disk_provision_size, String token, ExtentTest test) {

    Map<String, String> jobDataInfo = null;
    jobDataInfo = jp.composeJobDataInfoMap(job_id, job_seq, job_type, job_method, job_status,
        end_time_ts, protected_data_size, raw_data_size, sync_read_size, ntfs_volume_size,
        virtual_disk_provision_size);
    Response response = gatewayInvoker.UpdateJobData(job_id_url, jobDataInfo, token);
    return response;
  }


  public Response updateJobData(String job_id_url, Map<String, String> jobDataInfo, String token,
      ExtentTest test) {

    Response response = gatewayInvoker.UpdateJobData(job_id_url, jobDataInfo, token);
    return response;
  }


  public Response postJobDataForCDWithCheck(String job_id, String percent_complete,
      String start_time_ts, String end_time_ts, String elapsed_time, String error_count,
      String warning_count, String severity, String job_status, String processed_bytes_processed,
      String processed_bytes_changed, String processed_files, String transferred_uncompressed_bytes,
      String backup_throughput, String restore_data_source, String restore_data_location,
      String restore_data_path, String token, ExtentTest test) {

    Map<String, String> jobDataInfo = null;
    jobDataInfo = jp.composeJobDataInfoMapForCD(percent_complete, start_time_ts, end_time_ts,
        elapsed_time, error_count, warning_count, severity, job_status, processed_bytes_processed,
        processed_bytes_changed, processed_files, transferred_uncompressed_bytes, backup_throughput,
        restore_data_source, restore_data_location, restore_data_path);
    Response response = gatewayInvoker.createJobData(job_id, jobDataInfo, token);
    checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
    
    try {
		Thread.sleep(2000);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

    /*
    String responsePercentComplete =
        response.then().extract().path("data.job_data.percent_complete");
    assertResponseItem(percent_complete, responsePercentComplete);

    String response_error_count = response.then().extract().path("data.job_data.error_count");
    assertResponseItem(error_count, response_error_count);

    String response_warning_count = response.then().extract().path("data.job_data.warning_count");
    assertResponseItem(warning_count, response_warning_count);

    String response_severity = response.then().extract().path("data.job_data.severity");
    assertResponseItem(severity, response_severity);

    // String response_job_status = response.then().extract().path("data.job_status");
    // assertResponseItem(job_status, response_job_status);

    String response_processed_bytes_processed =
        response.then().extract().path("data.job_data.processed_bytes_processed");
    assertResponseItem(processed_bytes_processed, response_processed_bytes_processed);

    String response_processed_bytes_changed =
        response.then().extract().path("data.job_data.processed_bytes_changed");
    assertResponseItem(processed_bytes_changed, response_processed_bytes_changed);

    String response_processed_files =
        response.then().extract().path("data.job_data.processed_files");
    assertResponseItem(processed_files, response_processed_files);

    String response_transferred_uncompressed_bytes =
        response.then().extract().path("data.job_data.transferred_uncompressed_bytes");
    assertResponseItem(transferred_uncompressed_bytes, response_transferred_uncompressed_bytes);

    // zhaoguo: this field is not in response, DEV is confirming the design 2018/04/09
    // String response_backup_throughput =
    // response.then().extract().path("data.job_data.backup_throughput");
    // assertResponseItem(backup_throughput, response_backup_throughput);

    String response_restore_data_source =
        response.then().extract().path("data.job_data.restore_data_source");
    assertResponseItem(restore_data_source, response_restore_data_source);

    String response_restore_data_location =
        response.then().extract().path("data.job_data.restore_data_location");
    assertResponseItem(restore_data_location, response_restore_data_location);

    String response_restore_data_path =
        response.then().extract().path("data.job_data.restore_data_path");
    assertResponseItem(restore_data_path, response_restore_data_path);
*/
    return response;

  }


  public void postJobDataForCDWithErrorCheck(String job_id, String percent_complete,
      String start_time_ts, String end_time_ts, String elapsed_time, String error_count,
      String warning_count, String severity, String job_status, String processed_bytes_processed,
      String processed_bytes_changed, String processed_files, String transferred_uncompressed_bytes,
      String backup_throughput, String restore_data_source, String restore_data_location,
      String restore_data_path, String token, int status_code, String error_code, ExtentTest test) {

    Map<String, String> jobDataInfo = null;
    jobDataInfo = jp.composeJobDataInfoMapForCD(percent_complete, start_time_ts, end_time_ts,
        elapsed_time, error_count, warning_count, severity, job_status, processed_bytes_processed,
        processed_bytes_changed, processed_files, transferred_uncompressed_bytes, backup_throughput,
        restore_data_source, restore_data_location, restore_data_path);
    Response response = gatewayInvoker.createJobData(job_id, jobDataInfo, token);
    checkResponseStatus(response, status_code);
    checkErrorCode(response, error_code);
  }


  public Response postReplicationJobDataWithCheck(String job_id, String phase,
      String percent_complete, String start_time_ts, String elapsed_time,
      String estimated_time_remaining, String protection, String compression_level,
      String target_recovery_point_server, String target_data_store_name, String current_session,
      String target_cloud_hybrid_store, String start_session, String end_session,
      String saved_bandwidth_percentage, String source_recovery_point_server,
      String source_data_store_name, String network_throttle, String pysical_throughput,
      String logical_throughput, String job_seq, String token, ExtentTest test) {

    Map<String, String> jobDataInfo = null;
    jobDataInfo = jp.composeJobDataInfoMapForUDPReplication(phase, percent_complete, start_time_ts,
        elapsed_time, estimated_time_remaining, protection, compression_level,
        target_recovery_point_server, target_data_store_name, current_session,
        target_cloud_hybrid_store, start_session, end_session, saved_bandwidth_percentage,
        source_recovery_point_server, source_data_store_name, network_throttle, pysical_throughput,
        logical_throughput, job_seq);

    Response response = gatewayInvoker.createJobData(job_id, jobDataInfo, token);
    checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);

    // String response_phase = response.then().extract().path("data.job_data.phase");
    // assertResponseItem(phase, response_phase);

    String response_percent_complete =
        response.then().extract().path("data.job_data.percent_complete");
    assertResponseItem(percent_complete, response_percent_complete);

    // String response_start_time_ts = response.then().extract().path("data.start_time_ts");
    // assertResponseItem(start_time_ts, response_start_time_ts);

    // String response_elapsed_time = response.then().extract().path("data.elapsed_time");
    // assertResponseItem(elapsed_time, response_elapsed_time);

    // String response_estimated_time_remaining =
    // response.then().extract().path("data.job_data.estimated_time_remaining");
    // assertResponseItem(estimated_time_remaining, response_estimated_time_remaining);

    // String response_protection = response.then().extract().path("data.job_data.protection");
    // assertResponseItem(protection, response_protection);
    //
    // String response_compression_level =
    // response.then().extract().path("data.job_data.compression_level");
    // assertResponseItem(compression_level, response_compression_level);
    //
    // String response_target_recovery_point_server =
    // response.then().extract().path("data.job_data.target_recovery_point_server");
    // assertResponseItem(target_recovery_point_server, response_target_recovery_point_server);
    //
    // String response_target_data_store_name =
    // response.then().extract().path("data.job_data.target_data_store_name");
    // assertResponseItem(target_data_store_name, response_target_data_store_name);
    //
    // String response_current_session =
    // response.then().extract().path("data.job_data.current_session");
    // assertResponseItem(current_session, response_current_session);
    //
    // String response_target_cloud_hybrid_store =
    // response.then().extract().path("data.job_data.target_cloud_hybrid_store");
    // assertResponseItem(target_cloud_hybrid_store, response_target_cloud_hybrid_store);
    //
    // String response_start_session =
    // response.then().extract().path("data.job_data.start_session");
    // assertResponseItem(start_session, response_start_session);
    //
    // String response_end_session = response.then().extract().path("data.job_data.end_session");
    // assertResponseItem(end_session, response_end_session);
    //
    // String response_saved_bandwidth_percentage =
    // response.then().extract().path("data.job_data.saved_bandwidth_percentage");
    // assertResponseItem(saved_bandwidth_percentage, response_saved_bandwidth_percentage);

    // String response_source_recovery_point_server =
    // response.then().extract().path("data.job_data.source_recovery_point_server");
    // assertResponseItem(source_recovery_point_server, response_source_recovery_point_server);
    //
    // String response_source_data_store_name =
    // response.then().extract().path("data.job_data.source_data_store_name");
    // assertResponseItem(source_data_store_name, response_source_data_store_name);
    //
    // String response_network_throttle =
    // response.then().extract().path("data.job_data.network_throttle");
    // assertResponseItem(network_throttle, response_network_throttle);
    //
    // String response_pysical_throughput =
    // response.then().extract().path("data.job_data.pysical_throughput");
    // assertResponseItem(pysical_throughput, response_pysical_throughput);
    //
    // String response_logical_throughput =
    // response.then().extract().path("data.job_data.logical_throughput");
    // assertResponseItem(logical_throughput, response_logical_throughput);

    String response_job_seq = response.then().extract().path("data.job_data.job_seq");
    assertResponseItem(job_seq, response_job_seq);

    return response;
  }
  
  

}
