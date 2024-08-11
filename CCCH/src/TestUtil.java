import static io.restassured.RestAssured.given;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.management.OperationsException;

import org.apache.commons.lang3.RandomStringUtils;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Base64.Base64Coder;
import Constants.ConnectionStatus;
import Constants.OSMajor;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGHypervisorsServer;
import InvokerServer.SPOGJobServer;
import InvokerServer.SPOGServer;
import InvokerServer.ServerResponseCode;
import InvokerServer.Source4SPOGServer;
import InvokerServer.UserSpogServer;
import InvokerServer.GatewayServer.siteType;
import genericutil.StringUtils;
import InvokerServer.Linux4SPOGServer;
import InvokerServer.Policy4SPOGServer;
import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;
import io.restassured.response.Response;
import ui.spog.server.SPOGUIServer;

public class TestUtil {

	public static void main(String[] args) throws Exception {
		String baseURI = "http://tspog.zetta.net";
		String port = "8080";
		String csrAdmin = "tmp_xiang.li@arcserve.com";
		String csrPassword = "Caworld_2017";
		SPOGServer spogServer = new SPOGServer(baseURI, port);
		SPOGJobServer spogJobServer = new SPOGJobServer(baseURI, port);
		SPOGHypervisorsServer spogHypervisorsServer = new SPOGHypervisorsServer(baseURI, port);
		SPOGDestinationServer spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		
		spogServer.userLogin("5rztovdgmail@arcserve.com", "Passw0rd");
		for (int i = 0; i <50; i ++) {
			spogServer.createAccountWithCheck("6dcad8b6-776a-415b-a78a-73362fdf423b", "sub_org_name" + String.valueOf(i), "6dcad8b6-776a-415b-a78a-73362fdf423b");
		}
	}

	private static void createHypervisorInSubOrg() {
		String baseURI = "http://tspog.zetta.net";
		String port = "8080";
		String csrAdmin = "tmp_xiang.li@arcserve.com";
		String csrPassword = "Caworld_2017";
		SPOGServer spogServer = new SPOGServer(baseURI, port);
		SPOGJobServer spogJobServer = new SPOGJobServer(baseURI, port);
		SPOGHypervisorsServer spogHypervisorsServer = new SPOGHypervisorsServer(baseURI, port);
		SPOGDestinationServer spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		
		ExtentTest test = new ExtentTest("testname", "description");
		spogServer.userLogin(csrAdmin, csrPassword);
		String prefix = RandomStringUtils.randomAlphanumeric(8);

		String organizationID = spogServer.CreateOrganizationWithCheck(prefix + "orgName", SpogConstants.MSP_ORG,
				prefix + "mail@arcserve.com", "Passw0rd", "Ma", "Zhaoguo");

		spogServer.userLogin(prefix + "mail@arcserve.com", "Passw0rd");
		String cloudAccountID = spogServer.createCloudAccountWithCheck("cloudAccountKey", "cloudAccountSecret", "cloudAccountName", "cloud_direct", organizationID, 
				"SKUTESTDATA_1_0_0_0_"+prefix, "SKUTESTDATA_1_0_0_0_"+prefix, "SKUTESTDATA_1_0_0_0_"+prefix, "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
		
		String subOrgnizationID = spogServer.createAccountWithCheck(organizationID, "sub_org_name", organizationID);
		
		spogDestinationServer.setToken(spogServer.getJWTToken());
		String destinationID = spogDestinationServer.createDestinationWithCheck("", subOrgnizationID, cloudAccountID,"91a9b48e-6ac6-4c47-8202-614b5cdcfe0c","cloud_direct_volume", "running", "1",
				cloudAccountID, "normal", "mabzh02", "2M", "2M", "0", "0", "31", "0", "2", "0", "","","","",
					"dest", test); 
	
		spogServer.userLogin(prefix + "mail@arcserve.com", "Passw0rd");
		spogHypervisorsServer.setToken(spogServer.getJWTToken());
		
		spogHypervisorsServer.createHypervisorWithCheck("none", "hypervisor_name" + prefix, "vmware", "cloud_direct", "none", cloudAccountID, subOrgnizationID, 
				"false", "1508458202", destinationID, "none", "0 0 * * *", "1d", 
				"agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", test);
	}

	private static void getRecoveryPoints() {
		Response response = given().auth().preemptive().basic("admin@zetta.net","Zetta1234").header("Content-Type", "application/json")
					  .config((RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation())))		
				//.header("Authorization", "Basic YWRtaW5AemV0dGEubmV0OlpldHRhMTIzNA==")
					  .when().get("https://tadmin.zetta.net/cloudconsole/recoverypoints?organization_id=a40fbcda-a684-4347-938f-d27e6efece18&source_id=cb33dae6-bf27-42c8-bbcf-eff4ad94bf8b");         
		response.then().log().all();
	}

	private static void submitRestore() {
		String baseURI = "http://tspog.zetta.net";
		String port = "8080";
		String csrAdmin = "tmp_xiang.li@arcserve.com";
		String csrPassword = "Caworld_2017";
		SPOGServer spogServer = new SPOGServer(baseURI, port);
		Policy4SPOGServer policy4SPOGServer = new Policy4SPOGServer(baseURI, port);
		Source4SPOGServer source4spogServer = new Source4SPOGServer(baseURI, port);
		ExtentTest test = new ExtentTest("testName", "description");
		spogServer.userLogin("vfhn8rwomail@arcserve.com", "Pa$$w0rd");
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		String policy_id1 = UUID.randomUUID().toString();

		String task_id = UUID.randomUUID().toString();
		String directOrgId1 = "a40fbcda-a684-4347-938f-d27e6efece18";
		String destination_id_ret1 = "e9639f91-61ce-452b-84c0-0a0880c59865";
		String cloud_direct_account_id = "dcef5f30-7217-4d00-a14a-bc82ff168c9d";
		String cloud_source_id = "cb33dae6-bf27-42c8-bbcf-eff4ad94bf8b";
//
//		HashMap<String, Object> cloudDirectScheduleDTO = policy4SPOGServer.createCloudDirectScheduleDTO("0 0 * * *", test);
//		HashMap<String, Object> scheduleSettingDTO = policy4SPOGServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null, test);
//		ArrayList<HashMap<String, Object>> schedules = policy4SPOGServer.createPolicyScheduleDTO(null,
//				spogServer.returnRandomUUID(), "1d", task_id, destination_id_ret1, scheduleSettingDTO, "06:00", "12:00",
//				"cloud_direct_file_folder_backup", "dest", test);
//		ArrayList<HashMap<String, Object>> excludes = policy4SPOGServer.createExcludeInfoDTO(null, "path", "c:\\tmp", test);
//		HashMap<String, Object> cloudDirectLocalBackupDTO = policy4SPOGServer.createCloudDirectLocalBackupDTO("d:\\tmp", "true", test);
//		HashMap<String, Object> cloudDirectFileBackupTaskInfoDTO = policy4SPOGServer
//				.createCloudDirectFileBackupTaskInfoDTO("d:\\backup", null, excludes, test);
//		ArrayList<HashMap<String, Object>> destinations = policy4SPOGServer.createPolicyTaskDTO(null, task_id,
//				"cloud_direct_file_folder_backup", destination_id_ret1, "none", null, cloudDirectFileBackupTaskInfoDTO,	null, test);
//		ArrayList<HashMap<String, Object>> throttles = policy4SPOGServer.createPolicyThrottleDTO(null,
//				spogServer.returnRandomUUID(), task_id, "network", "600000", "1", "06:00", "18:00",
//				"cloud_direct_file_folder_backup", destination_id_ret1, "dest", test);
//
//		policy4SPOGServer.setToken(spogServer.getJWTToken());
//		Response response = policy4SPOGServer.createPolicy(prefix + "_policyName", prefix + "_description",
//				"backup_recovery", null, "true", cloud_source_id, destinations, schedules, null, policy_id1, directOrgId1, test);
//		policy4SPOGServer.checkPolicyDestinations(response, SpogConstants.SUCCESS_POST, destinations, test);
//		
		source4spogServer.setToken(spogServer.getJWTToken());
//		source4spogServer.submitBackupForSource(cloud_source_id, spogServer.getJWTToken());
//		Thread.sleep(60000);
		source4spogServer.submitCDRecoveryJob(cloud_source_id, cloud_source_id, "cloud_direct_file_folder_backup", 
				"/.snapshot/sync-age_2018-04-26_072501_UTC/zetta/ZettaMirror/zsystem20/D_drive/backup/", "ztst-3766.zetta.net", "/.snapshot/sync-age_2018-04-26_072501_UTC/zetta", cloud_source_id, "D:\\restore3", "string", spogServer.getJWTToken(), test);
	}

	private static void createHypervisorForGlobant() {
		String baseURI = "http://tspog.zetta.net";
		String port = "8080";
		String csrAdmin = "tmp_xiang.li@arcserve.com";
		String csrPassword = "Caworld_2017";
		SPOGServer spogServer = new SPOGServer(baseURI, port);
		SPOGJobServer spogJobServer = new SPOGJobServer(baseURI, port);
		SPOGHypervisorsServer spogHypervisorsServer = new SPOGHypervisorsServer(baseURI, port);
		SPOGDestinationServer spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		
		ExtentTest test = new ExtentTest("testname", "description");
		spogServer.userLogin(csrAdmin, csrPassword);
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		
		spogServer.userLogin("y5j4pvxhmail@arcserve.com", "Pa$$w0rd");
		spogHypervisorsServer.setToken(spogServer.getJWTToken());
		
		
		
		spogHypervisorsServer.createHypervisorWithCheck("none", "hypervisor_name" + prefix + "updated_again", "vmware", "cloud_direct", "none", "2b411f42-6802-48ec-9d5e-1430b4af03ab", "8f7c1f78-35ed-4cfe-b896-842966732653", 
				"false", "1508458202", "c6abfdaf-2f32-4050-b896-af459547ceca", "none", "0 0 * * *", "1d", 
				"agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", test);
	}

	private static void createPolicy() {
		String baseURI = "http://tspog.zetta.net";
		String port = "8080";
		String csrAdmin = "tmp_xiang.li@arcserve.com";
		String csrPassword = "Caworld_2017";
		SPOGServer spogServer = new SPOGServer(baseURI, port);
		Policy4SPOGServer policy4SPOGServer = new Policy4SPOGServer(baseURI, port);
		ExtentTest test = new ExtentTest("testName", "description");
		spogServer.userLogin("vfhn8rwomail@arcserve.com", "Pa$$w0rd");
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		String policy_id1 = UUID.randomUUID().toString();

		String task_id = UUID.randomUUID().toString();
		String directOrgId1 = "a40fbcda-a684-4347-938f-d27e6efece18";
		String destination_id_ret1 = "e9639f91-61ce-452b-84c0-0a0880c59865";
		String cloud_direct_account_id = "dcef5f30-7217-4d00-a14a-bc82ff168c9d";
		String cloud_source_id = "cb33dae6-bf27-42c8-bbcf-eff4ad94bf8b";

		HashMap<String, Object> cloudDirectScheduleDTO = policy4SPOGServer.createCloudDirectScheduleDTO("0 0 * * *", test);
		HashMap<String, Object> scheduleSettingDTO = policy4SPOGServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null, test);
		ArrayList<HashMap<String, Object>> schedules = policy4SPOGServer.createPolicyScheduleDTO(null,
				spogServer.returnRandomUUID(), "1d", task_id, destination_id_ret1, scheduleSettingDTO, "06:00", "12:00",
				"cloud_direct_file_folder_backup", "dest", test);
		ArrayList<HashMap<String, Object>> excludes = policy4SPOGServer.createExcludeInfoDTO(null, "path", "c:\\tmp", test);
		HashMap<String, Object> cloudDirectLocalBackupDTO = policy4SPOGServer.createCloudDirectLocalBackupDTO("d:\\tmp", "true", test);
		HashMap<String, Object> cloudDirectFileBackupTaskInfoDTO = policy4SPOGServer
				.createCloudDirectFileBackupTaskInfoDTO("d:\\backup", null, excludes, test);
		ArrayList<HashMap<String, Object>> destinations = policy4SPOGServer.createPolicyTaskDTO(null, task_id,
				"cloud_direct_file_folder_backup", destination_id_ret1, "none", null, cloudDirectFileBackupTaskInfoDTO,	null, test);
		ArrayList<HashMap<String, Object>> throttles = policy4SPOGServer.createPolicyThrottleDTO(null,
				spogServer.returnRandomUUID(), task_id, "network", "600000", "1", "06:00", "18:00",
				"cloud_direct_file_folder_backup", destination_id_ret1, "dest", test);

		policy4SPOGServer.setToken(spogServer.getJWTToken());
		Response response = policy4SPOGServer.createPolicy(prefix + "_policyName", prefix + "_description",
				"backup_recovery", null, "true", cloud_source_id, destinations, schedules, null, policy_id1, directOrgId1, test);
		policy4SPOGServer.checkPolicyDestinations(response, SpogConstants.SUCCESS_POST, destinations, test);
	}

	private static void createHypervisor() {
		String baseURI = "http://tspog.zetta.net";
		String port = "8080";
		String csrAdmin = "tmp_xiang.li@arcserve.com";
		String csrPassword = "Caworld_2017";
		SPOGServer spogServer = new SPOGServer(baseURI, port);
		SPOGJobServer spogJobServer = new SPOGJobServer(baseURI, port);
		SPOGHypervisorsServer spogHypervisorsServer = new SPOGHypervisorsServer(baseURI, port);
		SPOGDestinationServer spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		
		ExtentTest test = new ExtentTest("testname", "description");
		spogServer.userLogin(csrAdmin, csrPassword);
		String prefix = RandomStringUtils.randomAlphanumeric(8);

//		String organizationID = spogServer.CreateOrganizationWithCheck(prefix + "orgName", SpogConstants.DIRECT_ORG,
//				prefix + "mail@arcserve.com", "Passw0rd", "Ma", "Zhaoguo");
//
//		spogServer.userLogin(prefix + "mail@arcserve.com", "Passw0rd");
//		String cloudAccountID = spogServer.createCloudAccountWithCheck("cloudAccountKey", "cloudAccountSecret", "cloudAccountName", "cloud_direct", organizationID, 
//				"SKUTESTDATA_1_0_0_0_"+prefix, "SKUTESTDATA_1_0_0_0_"+prefix, "SKUTESTDATA_1_0_0_0_"+prefix, "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
//		
//		spogDestinationServer.setToken(spogServer.getJWTToken());
//		String destinationID = spogDestinationServer.createDestinationWithCheck(UUID.randomUUID().toString(), organizationID, cloudAccountID,
//				"91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", "cloud_dedupe_store", "running","9", "", "normal", "wanle05-win7", "2M", "2M", 
//						"0","0", "31", "0", "2", "0","E:\\destination", "E:\\data", "E:\\index", "E:\\hash", "5", "true", "1", "512", "true", "123", "120", "300", "10", "20", "ddd", test);
//	
		spogServer.userLogin("y5j4pvxhmail@arcserve.com", "Passw0rd");
		spogHypervisorsServer.setToken(spogServer.getJWTToken());
		
		spogHypervisorsServer.createHypervisorWithCheck("none", "hypervisor_name" + prefix, "vmware", "cloud_direct", "none", "4a29de1c-cad6-4619-812c-f62575268a9e", "cf9a488a-bb92-4a09-ac89-75c1a6da4f78", 
				"false", "1508458202", "cf38db46-2206-4343-bdc1-e71cd6d6f507", "none", "0 0 * * *", "1d", 
				"agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", test);
	}

	private static void siteTokenToManageOrganization() {
		String baseURI = "http://tspog.zetta.net";
		String port = "8080";
		ExtentTest test = new ExtentTest("testName", "description");
		String organizationID = "4d64ecf3-4f8a-4482-914f-a2e87db0d242";
		String cloudAccountID = "093a7512-649b-41db-b06b-739e2ec623cc";
		String siteID = "093a7512-649b-41db-b06b-739e2ec623cc";
		
		String email = "vbpylyfemail@arcserve.com";
		String password = "Passw0rd";
		String cloudAccountSecret = "cloudAccountSecret";
		
		Map<String,String > siteLoginInfoMap=new HashMap<>();
		siteLoginInfoMap.put("site_secret", cloudAccountSecret);
		
		RestAssured.baseURI = baseURI;
        RestAssured.port  = Integer.valueOf(port);
        RestAssured.basePath = "/api";
		
		Response response=given()
                .header("Content-Type", "application/json")
                .body(siteLoginInfoMap)
                .when()
                .post("/sites/"+siteID+"/login");
		
		String token = response.then().extract().path("data.token");
		
		SPOGServer spogServer = new SPOGServer(baseURI, port);
		
		spogServer.setToken(token);
		spogServer.getLogsByOrganizationId(token, organizationID, null, test);
		
		createCloudAccount();
	}

	public static void createhypervisorfilters() {
		String baseURI = "http://tspog.zetta.net";
		String port = "8080";
		String csrAdmin = "tmp_xiang.li@arcserve.com";
		String csrPassword = "Caworld_2017";
		SPOGServer spogServer = new SPOGServer(baseURI, port);
		UserSpogServer userSpogServer = new UserSpogServer(baseURI, port);
		spogServer.userLogin(csrAdmin, csrPassword);
		ExtentTest test = new ExtentTest("testName", "description");
		
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		String organizationEmail = prefix+  "email@arcserve.com";
		String organizationName = prefix + "organizationName";
		String organizationPwd = "Passw0rd";
		String organizationFirstName = "Ma";
		String organizationLastName = "Zhaoguo";

		String organizationID = spogServer.CreateOrganizationWithCheck(organizationName, SpogConstants.DIRECT_ORG, 
				organizationEmail, organizationPwd, organizationFirstName, organizationLastName, test);
		spogServer.userLogin(organizationEmail, organizationPwd);
		
		userSpogServer.setToken(spogServer.getJWTToken());
		String userID = spogServer.GetLoggedinUser_UserID();
		String filterID = userSpogServer.createHypervisorFilterForLoggedinUser("filterName", "status", "hypervisorProduct", "xen", "hypervisorName", "true", test);
		userSpogServer.createHypervisorFilterForLoggedinUserWithCodeCheck("filterName", "status", "hypervisorProduct", "xen", "hypervisorName", "true", 400, "00A00501", test);
		String filterID2 = userSpogServer.createHypervisorFilterForSpecificUser(userID, "filterName1", "status", "hypervisorProduct", "xen", "hypervisorName", "true", test);
		userSpogServer.createHypervisorFilterForSpecificUserWithCodeCheck(userID, "filterName1", "status", "hypervisorProduct", "xen", "hypervisorName", "true",  400, "00A00501", test);
		
		Response response = userSpogServer.getHypervisorFiltersForLoggedinUser(test);
		response = userSpogServer.getHypervisorFilterForLoggedinUser(filterID, test);
		response = userSpogServer.getHypervisorFilterForSpecificUser(userID, filterID, test);
		response = userSpogServer.getHypervisorFiltersForSpecificUser(userID, test);
		
		userSpogServer.updateHypervisorFilterForLoggedinUser(filterID, "filterName", "status", "hypervisorProduct", "xen", "hypervisorName", "true", test);
		userSpogServer.updateHypervisorFilterForLoggedinUserWithCodeCheck(filterID, "filterName1", "status", "hypervisorProduct", "xen", "hypervisorName", "true", 400, "00A00501", test);
		userSpogServer.updateHypervisorFilterForSpecificUser(userID, filterID, "filterName", "status", "hypervisorProduct", "xen", "hypervisorName", "true", test);
		userSpogServer.updateHypervisorFilterForSpecificUserWithCodeCheck(userID, filterID, "filterName1", "status", "hypervisorProduct", "xen", "hypervisorName", "true", 400, "00A00501", test);
		
		userSpogServer.getHypervisorFilterForLoggedinUserWithCodeCheck(UUID.randomUUID().toString(), 404, "00A00502", test);
		userSpogServer.getHypervisorFilterForSpecificUserWithCodeCheck(userID, UUID.randomUUID().toString(), 404, "00A00502", test);
		
		userSpogServer.getHypervisorFilterForSpecificUserWithCodeCheck(UUID.randomUUID().toString(), UUID.randomUUID().toString(), 404, "00200007", test);
		userSpogServer.setToken("");
		userSpogServer.getHypervisorFiltersForLoggedinUserWithCodeCheck(401, "00900006", test);
	}

	private static void createSourceFilterForLoggedinUser() {
		String baseURI = "http://tspog.zetta.net";
		String port = "8080";
		String csrAdmin = "tmp_xiang.li@arcserve.com";
		String csrPassword = "Caworld_2017";
		SPOGServer spogServer = new SPOGServer(baseURI, port);
		GatewayServer gatewayServer = new GatewayServer(baseURI, port);
		Source4SPOGServer source4spogServer = new Source4SPOGServer(baseURI, port);
		
		ExtentTest test = new ExtentTest("testname", "description");
		spogServer.userLogin(csrAdmin, csrPassword);
		
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		String organizationID = spogServer.CreateOrganizationWithCheck(prefix + "orgName", SpogConstants.DIRECT_ORG,
				prefix + "mail@arcserve.com", "Pa$$w0rd", "Ma", "Zhaoguo");

		spogServer.userLogin(prefix + "mail@arcserve.com", "Pa$$w0rd");
		String token = spogServer.getJWTToken();
		String userID = spogServer.GetLoggedinUser_UserID();
		
		Response response = spogServer.createSite("siteName", siteType.cloud_direct.name(), organizationID, token, test);
		Map<String, String> sitecreateResMap = new HashMap<>();
		sitecreateResMap = spogServer.checkCreateSite(response, SpogConstants.SUCCESS_POST, "siteName", siteType.cloud_direct.name(),
				organizationID, userID, "", test);
		String registrationBasecode = sitecreateResMap.get("registration_basecode");
		String siteID = sitecreateResMap.get("site_id");
		
		String siteRegistrationKey = "";
		try {
			String decoded = URLDecoder.decode(registrationBasecode.trim(), "UTF-8");
			Base64Coder base64 = new Base64Coder();
			String decrypted = base64.decode(decoded);
			String[] parts = decrypted.split("\\n", -2);
			siteRegistrationKey = parts[1];
		} catch (UnsupportedEncodingException e) {
			test.log(LogStatus.FAIL, "The value of the error Message :" + e.getMessage());
		}

		// test register site (POST sites/:/id/register)
		String gatewayID = UUID.randomUUID().toString();
		String gatewayHostName = prefix + "hostName";
		String siteVersion = "";
		test.log(LogStatus.INFO, "register a site");
		response = gatewayServer.RegisterSite(siteRegistrationKey, gatewayID, gatewayHostName, siteVersion, siteID,
				test);
		String site_secret = gatewayServer.checkRegisterSite(response, ServerResponseCode.Succes_Login, siteID,
				"siteName", siteType.cloud_direct.name(), organizationID, userID, true, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		
		String sourceID1 = spogServer.createSourceWithCheck("source_name1", SourceType.machine, SourceProduct.udp, 
				organizationID, siteID, ProtectionStatus.protect, ConnectionStatus.online, 
				OSMajor.windows.name(), "sql;exchange", "vm_name", UUID.randomUUID().toString(), 
				"agent_name", "os_name", "64" ,"1.0.0","2.0", "http://upgrade", UUID.randomUUID().toString(), 
				SpogConstants.SUCCESS_POST, "", test);
		
		source4spogServer.setToken(token);
		
		String filterID = source4spogServer.createSourcefilterForLoggedinUserWithCheck("filter_name2", ProtectionStatus.protect.name(), ConnectionStatus.online.name(), 
				"none", "none", "none", OSMajor.windows.name(), 
				siteID, "source_name", "none", "true", test);
		
		source4spogServer.createSourcefilterForLoggedinUserWithCodeCheck("filter_name2", ProtectionStatus.protect.name(), ConnectionStatus.online.name(), 
				"none", "none", "none", OSMajor.windows.name(), 
				siteID, "source_name", "none", "true", 400, "00A00001", test);
		
		source4spogServer.updateSourcefilterForLoggedinUserWithCodeCheck(filterID, "filter_name3", ProtectionStatus.protect.name(), ConnectionStatus.online.name(), 
				"none", "none", "none", OSMajor.windows.name(),  
				"siteID", "source_name", "none", "true", 400, "40000005", test);
	}

	private static void getsourcefilters() {
		String baseURI = "http://xiang-gitlab";
		String port = "9080";
		String csrAdmin = "xiang.li@arcserve.com";
		String csrPassword = "Caworld_2017";
		SPOGServer spogServer = new SPOGServer(baseURI, port);
		GatewayServer gatewayServer = new GatewayServer(baseURI, port);
		ExtentTest test = new ExtentTest("testname", "description");
		spogServer.userLogin(csrAdmin, csrPassword);
		
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		String organizationID = spogServer.CreateOrganizationWithCheck(prefix + "orgName", SpogConstants.DIRECT_ORG,
				prefix + "mail@arcserve.com", "Pa$$w0rd", "Ma", "Zhaoguo");

		spogServer.userLogin(prefix + "mail@arcserve.com", "Pa$$w0rd");
		String token = spogServer.getJWTToken();
		String userID = spogServer.GetLoggedinUser_UserID();
		
		Response response = spogServer.createSite("siteName", siteType.cloud_direct.name(), organizationID, token, test);
		Map<String, String> sitecreateResMap = new HashMap<>();
		sitecreateResMap = spogServer.checkCreateSite(response, SpogConstants.SUCCESS_POST, "siteName", siteType.cloud_direct.name(),
				organizationID, userID, "", test);
		String registrationBasecode = sitecreateResMap.get("registration_basecode");
		String siteID = sitecreateResMap.get("site_id");
		
		String siteRegistrationKey = "";
		try {
			String decoded = URLDecoder.decode(registrationBasecode.trim(), "UTF-8");
			Base64Coder base64 = new Base64Coder();
			String decrypted = base64.decode(decoded);
			String[] parts = decrypted.split("\\n", -2);
			siteRegistrationKey = parts[1];
		} catch (UnsupportedEncodingException e) {
			test.log(LogStatus.FAIL, "The value of the error Message :" + e.getMessage());
		}

		// test register site (POST sites/:/id/register)
		String gatewayID = UUID.randomUUID().toString();
		String gatewayHostName = prefix + "hostName";
		String siteVersion = "";
		test.log(LogStatus.INFO, "register a site");
		response = gatewayServer.RegisterSite(siteRegistrationKey, gatewayID, gatewayHostName, siteVersion, siteID,
				test);
		String site_secret = gatewayServer.checkRegisterSite(response, ServerResponseCode.Succes_Login, siteID,
				"siteName", siteType.cloud_direct.name(), organizationID, userID, true, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		
		String sourceID1 = spogServer.createSourceWithCheck("source_name1", SourceType.machine, SourceProduct.udp, 
				organizationID, siteID, ProtectionStatus.protect, ConnectionStatus.online, 
				OSMajor.windows.name(), "sql;exchange", "vm_name", UUID.randomUUID().toString(), 
				"agent_name", "os_name", "64" ,"1.0.0","2.0", "http://upgrade", UUID.randomUUID().toString(), 
				SpogConstants.SUCCESS_POST, "", test);
		
		String sourceID2 = spogServer.createSourceWithCheck("source_name2", SourceType.machine, SourceProduct.udp, 
				organizationID, siteID, ProtectionStatus.protect, ConnectionStatus.online, 
				OSMajor.windows.name(), "sql;exchange", "vm_name", UUID.randomUUID().toString(), 
				"agent_name", "os_name", "64" ,"1.0.0","2.0", "http://upgrade", UUID.randomUUID().toString(), 
				SpogConstants.SUCCESS_POST, "", test);
		
		String filterID1 = spogServer.createFilterwithCheck(userID, "filter_name1", ProtectionStatus.protect.name(), ConnectionStatus.online.name(), 
				"none", "none", "none", OSMajor.windows.name(), "exchange", 
				siteID, "source_name", SourceType.machine.name(), "true", test);
		
		String filterID2 = spogServer.createFilterwithCheck(userID, "filter_name2", ProtectionStatus.protect.name(), ConnectionStatus.online.name(), 
				"none", "none", "none", OSMajor.windows.name(), "exchange", 
				siteID, "source_name", "none", "true", test);
		
		response = spogServer.getFilterByID(userID, filterID1);
		response = spogServer.getFiltersByUserID(userID);
	}

	private static void submitbackup() {
		String baseURI = "http://xiang-gitlab";
		String port = "9080";
		String csrAdmin = "xiang.li@arcserve.com";
		String csrPassword = "Caworld_2017";
		SPOGServer spogServer = new SPOGServer(baseURI, port);
		GatewayServer gatewayServer = new GatewayServer(baseURI, port);
		ExtentTest test = new ExtentTest("testname", "description");
		spogServer.userLogin(csrAdmin, csrPassword);
		
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		String organizationID = spogServer.CreateOrganizationWithCheck(prefix + "orgName", SpogConstants.DIRECT_ORG,
				prefix + "mail@arcserve.com", "Pa$$w0rd", "Ma", "Zhaoguo");

		spogServer.userLogin(prefix + "mail@arcserve.com", "Pa$$w0rd");
		String token = spogServer.getJWTToken();
		String userID = spogServer.GetLoggedinUser_UserID();
		
		Response response = spogServer.createSite("siteName", siteType.cloud_direct.name(), organizationID, token, test);
		Map<String, String> sitecreateResMap = new HashMap<>();
		sitecreateResMap = spogServer.checkCreateSite(response, SpogConstants.SUCCESS_POST, "siteName", siteType.cloud_direct.name(),
				organizationID, userID, "", test);
		String registrationBasecode = sitecreateResMap.get("registration_basecode");
		String siteID = sitecreateResMap.get("site_id");
		
		String siteRegistrationKey = "";
		try {
			String decoded = URLDecoder.decode(registrationBasecode.trim(), "UTF-8");
			Base64Coder base64 = new Base64Coder();
			String decrypted = base64.decode(decoded);
			String[] parts = decrypted.split("\\n", -2);
			siteRegistrationKey = parts[1];
		} catch (UnsupportedEncodingException e) {
			test.log(LogStatus.FAIL, "The value of the error Message :" + e.getMessage());
		}

		// test register site (POST sites/:/id/register)
		String gatewayID = UUID.randomUUID().toString();
		String gatewayHostName = prefix + "hostName";
		String siteVersion = "";
		test.log(LogStatus.INFO, "register a site");
		response = gatewayServer.RegisterSite(siteRegistrationKey, gatewayID, gatewayHostName, siteVersion, siteID, test);
		String site_secret = gatewayServer.checkRegisterSite(response, ServerResponseCode.Succes_Login, siteID,
				"siteName", siteType.cloud_direct.name(), organizationID, userID, true, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		
		String sourceID = spogServer.createSourceWithCheck("source_name", SourceType.machine, SourceProduct.cloud_direct, 
				organizationID, siteID, ProtectionStatus.protect, ConnectionStatus.online, 
				OSMajor.windows.name(), "sql;exchange", "vm_name", UUID.randomUUID().toString(), 
				"agent_name", "os_name", "64" ,"1.0.0","2.0", "http://upgrade", UUID.randomUUID().toString(), 
				SpogConstants.SUCCESS_POST, "", test);

		Source4SPOGServer source4spogServer = new Source4SPOGServer(baseURI, port);
		source4spogServer.submitBackupForSource(sourceID, token);
	}

	private static void createDestinationFilterForLoggedinUser() {
		String baseURI = "http://tspog.zetta.net";
		String port = "8080";
		String csrAdmin = "tmp_xiang.li@arcserve.com";
		String csrPassword = "Caworld_2017";
		SPOGServer spogServer = new SPOGServer(baseURI, port);
		SPOGDestinationServer spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		ExtentTest test = new ExtentTest("testname", "description");
		spogServer.userLogin(csrAdmin, csrPassword);
		
		String prefix = RandomStringUtils.randomAlphanumeric(8);

		String organizationID = spogServer.CreateOrganizationWithCheck(prefix + "orgName", SpogConstants.DIRECT_ORG,
				prefix + "mail@arcserve.com", "Pa$$w0rd", "Ma", "Zhaoguo");

		spogServer.userLogin(prefix + "mail@arcserve.com", "Pa$$w0rd");
		String token = spogServer.getJWTToken();
		spogDestinationServer.setToken(token);
		
		spogDestinationServer.createDestinationFilterForLoggedinUserWithCheck("filterName0", "destinationName", UUID.randomUUID().toString(), "share_folder", "true", test);
		spogDestinationServer.createDestinationFilterForLoggedinUserAndCheckCode("filterName0", "destinationName", UUID.randomUUID().toString(), "share_folder", "true", 400, "00A00101", test);
	}

	private static void uploadPictureForUserByID() {
		String baseURI = "http://tspog.zetta.net";
		String port = "8080";
		String csrAdmin = "tmp_xiang.li@arcserve.com";
		String csrPassword = "Caworld_2017";
		SPOGServer spogServer = new SPOGServer(baseURI, port);
		
		spogServer.userLogin(csrAdmin, csrPassword);
		String sourceImageURL = "./testdata/images/3.gif";
		
		
		UserSpogServer userSpogServer = new UserSpogServer(baseURI, port);
		userSpogServer.setToken(spogServer.getJWTToken());
		ExtentTest test = new ExtentTest("testname", "description");
		
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		String msp_organization_id = spogServer.CreateOrganizationWithCheck(prefix + "organizationName", SpogConstants.MSP_ORG, 
				prefix + "mspemail@arcserve.com", "Passw0rd", "organizationFirstName", "organizationLastName");
		
		String msp_userID = spogServer.createUserAndCheck(prefix + "msp_added_email@arcserve.com", "Passw0rd", 
				"organizationFirstName", "organizationLastName", SpogConstants.MSP_ADMIN, msp_organization_id, test);
		
		String organization_id = spogServer.CreateOrganizationWithCheck(prefix + "organizationName", SpogConstants.DIRECT_ORG, 
				prefix + "email@arcserve.com", "Passw0rd", "organizationFirstName", "organizationLastName");
		spogServer.userLogin(prefix + "email@arcserve.com", "Passw0rd");
		String userID1 = spogServer.GetLoggedinUser_UserID();
		String userID2 = spogServer.createUserAndCheck(prefix + "_added_email@arcserve.com", "Passw0rd", 
				"organizationFirstName", "organizationLastName", SpogConstants.DIRECT_ADMIN, organization_id, test);
		
		userSpogServer.setToken(spogServer.getJWTToken());
		userSpogServer.uploadPictureByUserID(msp_userID, sourceImageURL, test);
		spogServer.getUserByID(userID1, test);
		spogServer.getUserByID(userID2, test);
		
		System.out.println("=============================================================");
		spogServer.userLogin(prefix + "_added_email@arcserve.com", "Passw0rd");
		userSpogServer.setToken(spogServer.getJWTToken());
		userSpogServer.uploadPictureForLoginUser(sourceImageURL, test);
		spogServer.getUserByID(userID2, test);
	}

	private static void createCloudAccount() {
		String baseURI = "http://tspog.zetta.net";
		String port = "8080";
		String csrAdmin = "tmp_xiang.li@arcserve.com";
		String csrPassword = "Caworld_2017";
		SPOGServer spogServer = new SPOGServer(baseURI, port);
		SPOGJobServer spogJobServer = new SPOGJobServer(baseURI, port);
		ExtentTest test = new ExtentTest("testname", "description");
		spogServer.userLogin(csrAdmin, csrPassword);
		
		String prefix = RandomStringUtils.randomAlphanumeric(8);

		String organizationID = spogServer.CreateOrganizationWithCheck(prefix + "orgName", SpogConstants.DIRECT_ORG,
				prefix + "mail@arcserve.com", "Passw0rd", "Ma", "Zhaoguo");

		spogServer.userLogin(prefix + "mail@arcserve.com", "Passw0rd");
		
		// "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_"
		spogServer.createCloudAccountWithCheck("cloudAccountKey", "cloudAccountSecret", "cloudAccountName", "cloud_direct", organizationID, 
				"SKUTESTDATA_1_0_0_0_"+prefix, "SKUTESTDATA_1_0_0_0_"+prefix, "SKUTESTDATA_1_0_0_0_"+prefix, "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
		
		spogServer.createCloudAccountWithCheck("cloudAccountKey", "cloudAccountSecret", "cloudAccountName2", "cloud_direct", organizationID, 
				"SKUTESTDATA_1_0_0_0_"+prefix, "invalid"+prefix, test);
	}

	private static void createJobFilter() {
		String baseURI = "http://tspog.zetta.net";
		String port = "8080";
		String csrAdmin = "tmp_xiang.li@arcserve.com";
		String csrPassword = "Caworld_2017";
		SPOGServer spogServer = new SPOGServer(baseURI, port);
		SPOGJobServer spogJobServer = new SPOGJobServer(baseURI, port);
		ExtentTest test = new ExtentTest("testname", "description");
		
		spogServer.userLogin(csrAdmin, csrPassword);
		String prefix = RandomStringUtils.randomAlphanumeric(8);

		String organizationID = spogServer.CreateOrganizationWithCheck(prefix + "orgName", SpogConstants.DIRECT_ORG,
				prefix + "mail@arcserve.com", "Password", "Ma", "Zhaoguo");

		spogServer.userLogin(prefix + "mail@arcserve.com", "Password");
		String token = spogServer.getJWTToken();
		String userID = spogServer.GetLoggedinUser_UserID();
		spogJobServer.setToken(token);
		
		String uuid1 = UUID.randomUUID().toString();
		String uuid2 = UUID.randomUUID().toString();
		
		String filterID1 = spogJobServer.createJobFilterWithCheck(userID, "finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", 
				"2017-01-01 12:00:00", "2018-01-01 12:00:00", prefix + "filterName1", "true", test);

		spogJobServer.createJobFilterWithCodeCheck(userID, "active,finished", uuid1 + "," + uuid2, uuid1 + "," + uuid2, "backup, restore", 
				"2018-01-01 12:00:00", "2018-01-01 12:00:00", "", "true", 400, "40000001", test);
		
		spogJobServer.updateJobFilterWithCheck(userID, filterID1, "finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", 
				"2017-01-01 12:00:00", "2018-01-01 12:00:00", prefix + "filterName10", "true", test);
		
		spogJobServer.updateJobFilterWithCodeCheck(userID, filterID1, "finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", 
				"2017-01-01 12:00:00", "2018-01-01 12:00:00", prefix + "filterName10", "invalid", 400, "00100001", test);

	}


	private static void createSourceGroup() {
		String baseURI = "http://tspog.zetta.net";
		String port = "8080";
		String csrAdmin = "tmp_xiang.li@arcserve.com";
		String csrPassword = "Caworld_2017";
		SPOGServer spogServer = new SPOGServer(baseURI, port);
		GatewayServer gatewayServer = new GatewayServer(baseURI, port);
		ExtentTest test = new ExtentTest("testname", "description");
		spogServer.userLogin(csrAdmin, csrPassword);

	}
	
	private static void postJob() {
		String baseURI = "http://tspog.zetta.net";
		String port = "8080";
		String csrAdmin = "tmp_xiang.li@arcserve.com";
		String csrPassword = "Caworld_2017";
		
		SPOGServer spogServer = new SPOGServer(baseURI, port);
		GatewayServer gatewayServer = new GatewayServer(baseURI, port);
		ExtentTest test = new ExtentTest("testname", "description");

		spogServer.userLogin(csrAdmin, csrPassword);
		String prefix = RandomStringUtils.randomAlphanumeric(8);

		String organizationID = spogServer.CreateOrganizationWithCheck(prefix + "orgName", SpogConstants.DIRECT_ORG,
				prefix + "mail@arcserve.com", "Passw0rd", "Ma", "Zhaoguo");

		spogServer.userLogin(prefix + "mail@arcserve.com", "Passw0rd");
		String spogToken = spogServer.getJWTToken();
		String userID = spogServer.GetLoggedinUser_UserID();

		// define the params for createSite - site will be used for jobs;
		String siteName = prefix + "siteName";
		String siteType = "gateway";

		// define the params for registerSite;
		String gatewayID = UUID.randomUUID().toString();
		String gatewayHostName = prefix + "hostName";
		String siteVersion = "";

		// define the params for postJobs;
		long startTimeTS = System.currentTimeMillis();
		long endTimeTS = System.currentTimeMillis();
		String serverID = UUID.randomUUID().toString();
		String rpsID = UUID.randomUUID().toString();
		String destinationID = UUID.randomUUID().toString();
		String jobType = "backup";
		String jobMethod = "incremental";
		String jobStatus = "finished";
		String policyID = UUID.randomUUID().toString();
		
		// define the params for postJobData;
		String jobSeq = "1000";

		// test create a site (POST sites/link)
		test.log(LogStatus.INFO, "create a site");
		Response response = spogServer.createSite(siteName, siteType, organizationID, spogToken, test);
		Map<String, String> sitecreateResMap = new HashMap<>();
		sitecreateResMap = spogServer.checkCreateSite(response, SpogConstants.SUCCESS_POST, siteName, siteType,
				organizationID, userID, "", test);
		String registrationBasecode = sitecreateResMap.get("registration_basecode");
		String siteID = sitecreateResMap.get("site_id");

		// define the params to create a resource;
		String sourceName = "sourceName";
		String sourceType = "machine";
		String sourceProduct = "udp";
		String protectionStatus = "protect";
		String connectionStatus = "online";
		String osMajor = "windows";
		String applications = "SQL_SERVER";
		String resourceID = spogServer.createSourceWithCheck(sourceName, sourceType, sourceProduct, organizationID,
				siteID, protectionStatus, connectionStatus, osMajor, applications, userID, SpogConstants.SUCCESS_POST,
				null, true, test);
		
		String siteRegistrationKey = "";
		try {
			String decoded = URLDecoder.decode(registrationBasecode.trim(), "UTF-8");
			Base64Coder base64 = new Base64Coder();
			String decrypted = base64.decode(decoded);
			String[] parts = decrypted.split("\\n", -2);
			siteRegistrationKey = parts[1];
		} catch (UnsupportedEncodingException e) {
			test.log(LogStatus.FAIL, "The value of the error Message :" + e.getMessage());
		}

		// test register site (POST sites/:/id/register)
		test.log(LogStatus.INFO, "register a site");
		response = gatewayServer.RegisterSite(siteRegistrationKey, gatewayID, gatewayHostName, siteVersion, siteID,
				test);
		String site_secret = gatewayServer.checkRegisterSite(response, ServerResponseCode.Succes_Login, siteID,
				siteName, siteType, organizationID, userID, true, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

		// test login site (POST sites/:id/login)
		test.log(LogStatus.INFO, "login a site");
		response = gatewayServer.LoginSite(siteID, site_secret, gatewayID, test);
		String token = response.then().extract().path("data.token");

		// test post job (POST jobs)
		test.log(LogStatus.INFO, "post job");
		
		String jobID = gatewayServer.postJobWithCheck(startTimeTS, endTimeTS, organizationID, serverID, resourceID, rpsID, 
				null, policyID, jobType, "full", jobStatus, token, test);

		gatewayServer.postJobWithCodeCheck(startTimeTS, endTimeTS, organizationID, serverID, resourceID, rpsID, 
				destinationID, policyID, "jobType", jobMethod, jobStatus, token, 400, "40000006", test);
		
		// test post job data (POST jobs/:id/data)
		test.log(LogStatus.INFO, "post job data");
		gatewayServer.postJobDataWithCheck(jobID, jobSeq, "information", "20", "120000", "130000", "140000", "150000", "160000", 
				"10","20","120000","130000","140000","150000","160000","170000","180000","190000", token, test);
		
	}
}
