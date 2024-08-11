package invoker;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;

import Constants.SpogConstants;
import InvokerServer.ServerResponseCode;
import dataPreparation.JsonPreparation;
import io.restassured.RestAssured;
import io.restassured.response.Response;

/**
 * @author Administrator
 *
 */
public class SiteTestHelper {

	public static enum siteType {
		cloud_direct, gateway
	}

	public static void loginSpog(String baseUri, String port, String userId, String password) {

		RestAssured.baseURI = baseUri;
		RestAssured.port = Integer.valueOf(port);
		RestAssured.basePath = "/api";

		Map<String, String> userInfo = new HashMap<String, String>() {
			{
				put("username", userId);
				put("password", password);
			}
		};

		Response response = given().header("Content-Type", "application/json").body(userInfo).when()
				.post("/users/login");

		// response.then().log().all();
	}

	public static void configSpogServerConnection(String baseUri, String port) {

		RestAssured.baseURI = baseUri;
		RestAssured.port = Integer.valueOf(port);
		RestAssured.basePath = "/api";
	}

	public static String loginSpogServer(String username, String password) {

		Map<String, String> userInfo = new HashMap<String, String>() {
			{
				put("username", username);
				put("password", password);
			}
		};

		Response response = given().header("Content-Type", "application/json").body(userInfo).when()
				.post("/users/login");

		response.then().log().all();
		response.then().statusCode(ServerResponseCode.Success_Get);

		String token = response.then().extract().path("data.token");
		assertTrue(StringUtils.isNotEmpty(token));

		return token;
	}

	public static void createMspAdminUser(String email, String password, String first_name, String last_name,
			String organization_id, String token) {

		createUser(email, password, first_name, last_name, SpogConstants.MSP_ADMIN, organization_id, token);
	}

	public static void createDirectAdminUser(String email, String password, String first_name, String last_name,
			String organization_id, String token) {

		createUser(email, password, first_name, last_name, SpogConstants.DIRECT_ADMIN, organization_id, token);
	}

	public static void createUser(String email, String password, String first_name, String last_name, String role_id,
			String organization_id, String token) {

		JsonPreparation jp = new JsonPreparation();
		Map<String, String> userInfo = jp.getUserInfo(email, password, first_name, last_name, role_id, organization_id);

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(userInfo).when().post("/users");

		response.then().log().all();
		response.then().statusCode(ServerResponseCode.Success_Post);
	}

	public static String createUser(Map<String, String> userInfoMap, String token) {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(userInfoMap).when().post("/users");

		response.then().log().all();
		response.then().statusCode(ServerResponseCode.Success_Post);
		return response.then().extract().path("data.user_id");
	}

	public static Map<String, String> composeRandomUserMap(String prefix, String roleId, String organizationId) {

		JsonPreparation jp = new JsonPreparation();

		String username_email = getRandomUserName(prefix) + "@" + RandomStringUtils.randomAlphanumeric(8) + ".com";
		String password = getTestPassword();
		String first_name = RandomStringUtils.randomAlphanumeric(4);
		String last_name = RandomStringUtils.randomAlphanumeric(4);
		String role_id = roleId;
		String organization_id = organizationId;

		Map<String, String> userInfo = jp.getUserInfo(username_email, password, first_name, last_name, role_id,
				organization_id);

		return userInfo;
	}

	public static String createOrgnaization(String organizationName, String organizationType, String organizationEmail,
			String organizationPwd, String organizationFirstName, String organizationLastName, String token) {

		String orgnizationId;

		organizationPwd = getTestPassword();
		JsonPreparation jp = new JsonPreparation();
		Map<String, Object> orgInfo = jp.getOrganizationInfo(organizationName, organizationType, organizationEmail,
				organizationPwd, organizationFirstName, organizationLastName);

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(orgInfo).when().post("/organizations");
		response.then().log().all();
		response.then().statusCode(ServerResponseCode.Success_Post);

		orgnizationId = response.then().extract().path("data.organization_id");

		return orgnizationId;
	}

	public static String createSubOrgnaization(String subOrganizationName, String parentOrgId, String token) {

		String orgnizationId;

		JsonPreparation jp = new JsonPreparation();
		Map<String, String> accountInfo = jp.getAccountInfo(subOrganizationName, parentOrgId);

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(accountInfo).when()
				.post("/organizations/" + parentOrgId + "/accounts");
		response.then().log().all();

		response.then().statusCode(ServerResponseCode.Success_Post);

		orgnizationId = response.then().extract().path("data.organization_id");

		return orgnizationId;
	}

	public static String createOrgnaization(Map organizationInfoMap, String token) {

		String orgnizationId;
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(organizationInfoMap).when().post("/organizations");

		response.then().statusCode(ServerResponseCode.Success_Post);

		orgnizationId = response.then().extract().path("data.organization_id");

		return orgnizationId;
	}

	public static Map<String, Object> composeRandomOrganizationMap(String prefix, String organizationType) {

		String organizationName = getRandomOrganizationName(prefix);
		String organizationEmail = organizationName + "@" + organizationName + ".com";
		String organizationFirstName = RandomStringUtils.randomAlphanumeric(8);
		String organizationLastName = RandomStringUtils.randomAlphanumeric(8);

		JsonPreparation jp = new JsonPreparation();
		Map<String, Object> organizationMap = jp.getOrganizationInfo(organizationName, organizationType,
				organizationEmail, getTestPassword(), organizationFirstName, organizationLastName);

		return organizationMap;
	}

	public static Response createSite(String siteName, String siteType, String organizationId, String token) {

		Map<String, String> orgInfo = composeSiteInfoMap(siteName, siteType, organizationId);

		String server = RestAssured.baseURI;
		int server1 = RestAssured.port;
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(orgInfo).when().post("sites/link");

		response.then().log().all();
		return response;
	}

	public static Response deleteSite(String siteId, String token) {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().delete("/sites/" + siteId);

		response.then().log().body();

		return response;
	}

	/**
	 * 
	 */
	public static void loginSite() {

	}

	public static Response getSite(String siteId, String token) {

		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).when().get("/sites/" + siteId);

		return response;
	}

	public static Map<String, String> composeSiteInfoMap(String siteName, String siteType, String organizationId) {

		Map<String, String> siteInfoMap = new HashMap<>();
		siteInfoMap.put("site_name", siteName);
		siteInfoMap.put("site_type", siteType);
		if (StringUtils.isNotEmpty(organizationId)) {
			siteInfoMap.put("organization_id", organizationId);
		}

		return siteInfoMap;
	}

	public static String getRandomOrganizationName(String prefix) {

		return prefix + "_Org_" + RandomStringUtils.randomAlphanumeric(3) + "_SpogQa";
	}

	public static String getRandomUserName(String prefix) {

		return prefix + "_User_" + RandomStringUtils.randomAlphanumeric(8) + "_SpogQa";
	}

	public static String getRandomSiteName(String prefix) {

		return prefix + "_Site_" + RandomStringUtils.randomAlphanumeric(8) + "_SpogQa";
	}

	public static String getTestPassword() {

		return "Abcdef123";
	}

	/**
	 * call REST API to update organization information by ID
	 * 
	 * @author yongjun
	 * @param SiteID
	 * @param newSiteName
	 * @return
	 */

	public static Response updateSiteById(String SiteID, String newSiteName, String token) {

		Map<String, String> updateSiteInfo = new HashMap<>();
		updateSiteInfo.put("site_name", newSiteName);
		Response response = given().header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token).body(updateSiteInfo).when().put("/sites/" + SiteID);

		response.then().log().all();
		return response;
	}

	/**
	 * call REST API to register site
	 * 
	 * @author yongjun
	 * 
	 * @return
	 */
	public static Response registerSiteWithInfo(String site_registration_key, String gateway_id,
			String gateway_hostname, String site_version, String siteId) {

		Map<String, String> registerInfo = new HashMap<>();
		registerInfo.put("site_registration_key", site_registration_key);
		registerInfo.put("gateway_id", gateway_id);
		registerInfo.put("gateway_hostname", gateway_hostname);
		registerInfo.put("site-version", site_version);
		Response response = given().header("Content-Type", "application/json").body(registerInfo).when()
				.post("/sites/" + siteId + "/register");
		response.then().log().all();

		return response;

	}

	/**
	 * call REST API to login a site
	 * 
	 * @author yongjun
	 * 
	 * @return
	 */
	public static Response LoginSiteWithInfo(String site_id, String secret_key) {

		Map<String, String> siteLoginInfoMap = new HashMap<>();
		siteLoginInfoMap.put("site_id", site_id);
		siteLoginInfoMap.put("site_secret", secret_key);
		// siteLoginInfoMap.put("gateway_id", gateway_id);
		Response response = given().header("Content-Type", "application/json").body(siteLoginInfoMap).when()
				.post("/sites/" + site_id + "/login");
		response.then().log().all();
		return response;

	}
}
