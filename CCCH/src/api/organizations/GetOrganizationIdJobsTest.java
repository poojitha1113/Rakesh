package api.organizations;


import static invoker.SiteTestHelper.composeRandomUserMap;
import static invoker.SiteTestHelper.createSubOrgnaization;
import static invoker.SiteTestHelper.createUser;
import static invoker.SiteTestHelper.getRandomOrganizationName;
import static invoker.SiteTestHelper.getRandomSiteName;
import static invoker.SiteTestHelper.getTestPassword;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.testng.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.Ordering;
import com.relevantcodes.extentreports.LogStatus;

import Base64.Base64Coder;
import Constants.ConnectionStatus;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.ServerResponseCode;
import genericutil.ExtentManager;
import invoker.SPOGDestinationInvoker;
import io.restassured.response.Response;

public class GetOrganizationIdJobsTest extends base.prepare.TestApiBase {

  private String                 token;
  // private ExtentReports extent;
  // private ExtentTest logger;
  private String                 TestDataPrefix     = getClass().getSimpleName();

  // private SQLServerDb bqdb1;
  // public int Nooftest;
  // private long creationTime;
  // private String BQName = null;
  // private String runningMachine;
  // private testcasescount count1;
  // private String buildVersion;

  private String                 mspOrgId;
  private String                 mspAdmin;
  private String                 mspPassword;
  private String                 groupId_A_Msp;
  private String                 groupId_B_Msp;

  private String                 accountOrgAId;
  private String                 childOrgA_Admin;
  private String                 childOrgA_Password;
  private String                 groupId_AccountA;

  private String                 accountOrgBId;
  private String                 childOrgB_Admin;
  private String                 childOrgB_Password;
  private String                 groupId_AccountB;

  private String                 directOrgId;
  private String                 directAdmin;
  private String                 directPassword;
  private String                 groupId_Direct;
  private String                 groupBId_Direct;

  // private SPOGServer spogServer;
  // private GatewayServer gatewayServer;
  // private ExtentTest test;
  // private ExtentReports rep;
  // private String csrAdminUserName;
  // private String csrAdminPassword;

  // private String baseUri;
  // private String port;

  private int                    jobSize            = 2;
  private int                    mspOrgJobSize      = jobSize;
  private int                    accountAOrgJobSize = jobSize;
  private int                    accountBOrgJobSize = jobSize;
  private int                    directOrgJobSize   = 5;
  private SPOGDestinationInvoker spogDestinationInvoker;
  // private SPOGDestinationServer spogDestinationServer;
  private String                 direct_user_cloud_account_id;
  private String                 datacenterId       = "99a9b48e-6ac6-4c47-8202-614b5cdcfe0c";


  @Override
  public void setAuthor() {

    author = "Yin.Li";

  }


  // @BeforeTest
  // @Parameters({"baseURI", "port", "csrAdminUserName", "csrAdminPassword", "logFolder",
  // "buildVersion"})
  // public void setSpogServerConnection(String baseURI, String port, String username, String
  // password,
  // String logFolder, String buildVersion) throws UnknownHostException {
  //
  // baseUri = baseURI;
  // this.port = port;
  //
  // configSpogServerConnection(baseURI, port);
  // token = loginSpogServer(username, password);
  //
  // this.csrAdminUserName = username;
  // this.csrAdminPassword = password;
  //
  // spogServer = new SPOGServer(baseURI, port);
  // spogServer.userLogin(username, password);
  //
  // spogDestinationInvoker = new SPOGDestinationInvoker(baseURI, port);
  // spogDestinationServer = new SPOGDestinationServer(baseURI, port);
  //
  // gatewayServer = new GatewayServer(baseURI, port);
  //
  // rep = ExtentManager.getInstance(this.getClass().getSimpleName(), logFolder);
  // test = rep.startTest("initializing data...");
  //
  // // this is for update portal
  // this.BQName = this.getClass().getSimpleName();
  // this.runningMachine = java.net.InetAddress.getLocalHost().getHostName();
  // SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
  // java.util.Date date = new java.util.Date();
  // this.buildVersion = buildVersion + "_" + dateFormater.format(date);
  // String author = "yin.li";
  // Nooftest = 0;
  // bqdb1 = new SQLServerDb();
  // count1 = new testcasescount();
  // if (count1.isstarttimehit() == 0) {
  // creationTime = System.currentTimeMillis();
  // count1.setcreationtime(creationTime);
  // // creationTime = System.currentTimeMillis();
  // try {
  // bqdb1.updateTable(BQName, runningMachine, this.buildVersion, String.valueOf(Nooftest), "0",
  // "0", String.valueOf(Nooftest), count1.getcreationtime(), "InProgress",
  // author + " and Rest server is " + baseUri.split("//")[1]);
  // } catch (ClientProtocolException e) {
  // // TODO Auto-generated catch block
  // e.printStackTrace();
  // } catch (IOException e) {
  // // TODO Auto-generated catch block
  // e.printStackTrace();
  // }
  // }
  // }


  public void generateJob2(String cloudAccountId) {

    // spogServer.userLogin(userName, password);
    String spogToken = spogServer.getJWTToken();
    String userID = spogServer.GetLoggedinUser_UserID();
    String orgID = spogServer.GetLoggedinUserOrganizationID();
    String userName = spogServer
        .getLoggedUserInfoByID(spogToken, SpogConstants.SUCCESS_GET_PUT_DELETE, userID, test).then()
        .extract().path("data.email");

    String siteName = getRandomSiteName(TestDataPrefix);
    String siteType = "cloud_direct"; // gateway
    Response response = spogServer.createSite(siteName, siteType, orgID, spogToken, test);

    Map<String, String> sitecreateResMap = new HashMap<>();
    sitecreateResMap = spogServer.checkCreateSite(response, SpogConstants.SUCCESS_POST, siteName,
        siteType, orgID, userID, "", test);

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

    String gatewayID = UUID.randomUUID().toString();
    String gatewayHostname = UUID.randomUUID().toString();
    String siteVersion = "";
    response = gatewayServer.RegisterSite(siteRegistrationKey, gatewayID, gatewayHostname,
        siteVersion, siteID, test);
    String site_secret = gatewayServer.checkRegisterSite(response, ServerResponseCode.Succes_Login,
        siteID, siteName, siteType, orgID, userID, true, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
    response = gatewayServer.LoginSite(siteID, site_secret, gatewayID, test);
    String token = response.then().extract().path("data.token");

    long startTimeTS = System.currentTimeMillis();
    String serverID = UUID.randomUUID().toString();
    String resourceID = spogServer.createSourceWithCheck(UUID.randomUUID().toString(),
        SourceType.machine, SourceProduct.udp, orgID, siteID, ProtectionStatus.unprotect,
        ConnectionStatus.online, "windows", "SQL_SERVER", test);
    String rpsID = UUID.randomUUID().toString();
    String datastoreID = UUID.randomUUID().toString();
    String messageID = "testJobMessage";
    String policyID = UUID.randomUUID().toString();
    String organizationID = orgID;
    String[] messageData = new String[] {"node", "agent", "spog", "QA"};

    String site_id = gatewayServer.createsite_register_login(orgID, spogToken, userID,
        RandomStringUtils.randomAlphabetic(5), "1.0.0", spogServer, test);

    spogDestinationServer.setToken(spogToken);
    spogDestinationInvoker = new SPOGDestinationInvoker(baseURI, port);
    spogDestinationInvoker.setToken(spogToken);

    String datacenterID = datacenterId;

    // PostDestinationTest.java, String destination_id_ret =
    // spogDestinationServer.createDestinationWithCheck
    String destination_id = "";
    String organization_id = orgID;
    String destination_type = "cloud_direct_volume";
    String destination_status = "running";
    String dedupe_savings = "1";
    String cloud_account_id = cloudAccountId;
    String volume_type = "normal";
    String hostname = RandomStringUtils.randomAlphabetic(5);
    String retention_id = "custom";
    String retention_name = "custom";
    String age_hours_max = "0";
    String age_four_hours_max = "0";
    String age_days_max = "7";
    String age_weeks_max = "0";
    String age_months_max = "0";
    String age_years_max = "0";
    String concurrent_active_node = "";
    String is_deduplicated = "";
    String block_size = "";
    String is_compressed = "";
    String destination_name = RandomStringUtils.randomAlphabetic(8);
    // String destination_id_ret =
    // spogDestinationServer.createDestinationWithCheck(destination_id, organization_id, site_id,
    // datacenterID, destination_type, destination_status, dedupe_savings, cloud_account_id,
    // volume_type, hostname, retention_id, retention_name, age_hours_max, age_four_hours_max,
    // age_days_max, age_weeks_max, age_months_max, age_years_max, concurrent_active_node,
    // is_deduplicated, block_size, is_compressed, destination_name, test);

    String destinationID = spogDestinationServer.createDestinationWithCheck("", orgID, site_id,
        datacenterID, "cloud_direct_volume", "running", "1", cloudAccountId, "normal",
        RandomStringUtils.randomAlphabetic(5), "custom", "custom", "0", "0", "7", "0", "0", "0", "",
        "", "", "", RandomStringUtils.randomAlphabetic(8), test);

    gatewayServer.postJobWithCheck(startTimeTS, (startTimeTS + 1000000), organizationID, serverID,
        resourceID, rpsID, destinationID, policyID, "backup", "incremental", "finished", token,
        test);
  }


  public String getRamdonDatacentId() {

    String[] datacenterIDs = spogDestinationServer.getDestionationDatacenterID();
    int index = (int) Math.random() * datacenterIDs.length;
    String datacenterID = datacenterIDs[index];
    return datacenterID;
  }


  public void generateJob_old() {

    // spogServer.userLogin(userName, password);
    String spogToken = spogServer.getJWTToken();
    String userID = spogServer.GetLoggedinUser_UserID();
    String orgID = spogServer.GetLoggedinUserOrganizationID();

    String siteName = getRandomSiteName(TestDataPrefix);
    String siteType = "cloud_direct"; // gateway
    Response response = spogServer.createSite(siteName, siteType, orgID, spogToken, test);

    Map<String, String> sitecreateResMap = new HashMap<>();
    sitecreateResMap = spogServer.checkCreateSite(response, SpogConstants.SUCCESS_POST, siteName,
        siteType, orgID, userID, "", test);

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

    String gatewayID = UUID.randomUUID().toString();
    String gatewayHostname = UUID.randomUUID().toString();
    String siteVersion = "";
    response = gatewayServer.RegisterSite(siteRegistrationKey, gatewayID, gatewayHostname,
        siteVersion, siteID, test);
    String site_secret = gatewayServer.checkRegisterSite(response, ServerResponseCode.Succes_Login,
        siteID, siteName, siteType, orgID, userID, true, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
    response = gatewayServer.LoginSite(siteID, site_secret, gatewayID, test);
    String token = response.then().extract().path("data.token");

    long startTimeTS = System.currentTimeMillis();
    String serverID = UUID.randomUUID().toString();
    String resourceID = spogServer.createSourceWithCheck(UUID.randomUUID().toString(),
        SourceType.machine, SourceProduct.udp, orgID, siteID, ProtectionStatus.protect,
        ConnectionStatus.online, "windows", "SQL_SERVER", test);
    String rpsID = UUID.randomUUID().toString();
    String datastoreID = UUID.randomUUID().toString();
    String messageID = "testJobMessage";
    String policyID = UUID.randomUUID().toString();
    String organizationID = orgID;
    String[] messageData = new String[] {"node", "agent", "spog", "QA"};
    String destinationID = UUID.randomUUID().toString();
    // gatewayServer.postJobWithCheck(startTimeTS, serverID, resourceID, rpsID, datastoreID, siteID,
    // organizationID, messageID, policyID, messageData, token, test);
    gatewayServer.postJobWithCheck(startTimeTS, (startTimeTS + 1000000), organizationID, serverID,
        resourceID, rpsID, destinationID, policyID, "backup", "incremental", "finished", token,
        test);
  }


  public void generateJobs(int size, String cloudAccountId) {

    for (int i = 1; i <= size; i++) {
      if (StringUtils.isEmpty(cloudAccountId)) {
        generateJob();
      } else {
        generateJob();
      }

      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }


  @BeforeClass
  public void prepareJobData() {

    test.assignAuthor("Yin.Li");
    test = rep.startTest("Prepare Job Data");

    spogServer.userLogin(csrAdminUserName, csrAdminPassword);
    token = spogServer.getJWTToken();
    String csrToken = this.token;

    // create msp admin
    Map<String, String> userInfoMap =
        composeRandomUserMap(TestDataPrefix, SpogConstants.MSP_ADMIN, mspOrgId);
    // create msp org
    mspOrgId =
        spogServer.CreateOrganizationByEnrollWithCheck(getRandomOrganizationName(TestDataPrefix),
            SpogConstants.MSP_ORG, userInfoMap.get("email"), getTestPassword(),
            RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(8));

    mspAdmin = userInfoMap.get("email");
    mspPassword = getTestPassword();
    // String mspToken = loginSpogServer(mspAdmin, mspPassword);
    spogServer.userLogin(mspAdmin, mspPassword);
    String mspToken = spogServer.getJWTToken();
    String cloud_account_id = spogServer.getCloudAccounts(spogServer.getJWTToken(), "", test).then()
        .extract().path("data[0].cloud_account_id");
    generateJobs(mspOrgJobSize, cloud_account_id);

    // create child org A admin
    accountOrgAId =
        createSubOrgnaization(getRandomOrganizationName(TestDataPrefix), mspOrgId, mspToken);

    userInfoMap = composeRandomUserMap(TestDataPrefix, SpogConstants.DIRECT_ADMIN, accountOrgAId);
    createUser(userInfoMap, mspToken);
    childOrgA_Admin = userInfoMap.get("email");
    childOrgA_Password = getTestPassword();
    spogServer.userLogin(childOrgA_Admin, childOrgA_Password);
    generateJobs(accountAOrgJobSize, "");

    // create child org B admin
    accountOrgBId =
        createSubOrgnaization(getRandomOrganizationName(TestDataPrefix), mspOrgId, mspToken);

    userInfoMap = composeRandomUserMap(TestDataPrefix, SpogConstants.DIRECT_ADMIN, accountOrgBId);
    createUser(userInfoMap, mspToken);
    childOrgB_Admin = userInfoMap.get("email");
    childOrgB_Password = getTestPassword();
    spogServer.userLogin(childOrgB_Admin, childOrgB_Password);
    generateJobs(accountBOrgJobSize, "");

    spogServer.userLogin(csrAdminUserName, csrAdminPassword);
    // create direct admin
    userInfoMap = composeRandomUserMap(TestDataPrefix, SpogConstants.DIRECT_ADMIN, directOrgId);
    // create direct org
    directOrgId =
        spogServer.CreateOrganizationByEnrollWithCheck(getRandomOrganizationName(TestDataPrefix),
            SpogConstants.DIRECT_ORG, userInfoMap.get("email"), getTestPassword(),
            RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(8));

    this.directAdmin = userInfoMap.get("email");
    this.directPassword = getTestPassword();
    spogServer.userLogin(directAdmin, directPassword);
    cloud_account_id = spogServer.getCloudAccounts(spogServer.getJWTToken(), "", test).then()
        .extract().path("data[0].cloud_account_id");
    generateJobs(directOrgJobSize, "");
  }


  @Test
  public void Given_CsrAdmin_When_GetMspOrganizationJobs_Should_Successful() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(csrAdminUserName, csrAdminPassword);

    Response response = spogServer.getOrganizationJobs(mspOrgId, null);

    response.then().statusCode(ServerResponseCode.Success_Get);
  }

  @Test
  public void Given_Csr_ReadOnly_User_When_GetMspOrganizationJobs_Should_Success() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(csrAdminUserName, csrAdminPassword);
    token = spogServer.getJWTToken();
    String csrToken = token;
    
    String csrOrgId = spogServer.GetLoggedinUserOrganizationID();
    String password = getTestPassword();
    
    Map<String, String> userInfoMap = composeRandomUserMap(TestDataPrefix, SpogConstants.CSR_READ_ONLY_ADMIN, csrOrgId);
    String csrReadOnlyUser = userInfoMap.get("email");
    
    String csrReadOnlyUserId = spogServer.createUserAndCheck(csrReadOnlyUser, password, userInfoMap.get("first_name"), 
    		userInfoMap.get("last_name"), SpogConstants.CSR_READ_ONLY_ADMIN, csrOrgId, test);
    
    spogServer.userLogin(csrReadOnlyUser, password);
	
    Response response = spogServer.getOrganizationJobs(mspOrgId, null);

    response.then().statusCode(ServerResponseCode.Success_Get);
    
    spogServer.userLogin(csrAdminUserName, csrAdminPassword);
    spogServer.CheckDeleteUserByIdStatus(csrReadOnlyUserId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
  }	

  
  @Test
  public void Given_CsrAdmin_When_GetAccountOrganizationJobs_Should_Successful() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(csrAdminUserName, csrAdminPassword);

    Response response = spogServer.getOrganizationJobs(accountOrgAId, null);

    response.then().statusCode(ServerResponseCode.Success_Get);
  }


  // @Test
  public void Given_CsrAdmin_When_GetDirectOrganizationJobs_Should_Successful() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(csrAdminUserName, csrAdminPassword);

    Response response = spogServer.getOrganizationJobs(directOrgId, null);

    response.then().statusCode(ServerResponseCode.Success_Get);
    response.then().assertThat().body("data.size()", is(directOrgJobSize));
  }



  // @Test
  public void Given_MspAdmin_When_GetMspOrganizationJobs_Should_Successful() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(mspAdmin, mspPassword);

    Response response = spogServer.getOrganizationJobs(mspOrgId, null);

    response.then().statusCode(ServerResponseCode.Success_Get);
    response.then().assertThat().body("data.size()", is(mspOrgJobSize));
  }


  // @Test
  public void Given_MspAdmin_When_GetAccountOrganizationJobs_Should_Successful() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(mspAdmin, mspPassword);

    Response response = spogServer.getOrganizationJobs(accountOrgAId, null);

    response.then().assertThat().body("data.size()", is(accountAOrgJobSize)).body("$",
        hasKey("destination_type"));
    response.then().statusCode(ServerResponseCode.Success_Get);

  }


  // @Test
  public void Given_AccountAdmin_When_GetAccountOrganizationJobs_Should_Successful() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(childOrgA_Admin, childOrgA_Password);

    Response response = spogServer.getOrganizationJobs(accountOrgAId, null);

    response.then().statusCode(ServerResponseCode.Success_Get);
    response.then().assertThat().body("data.size()", is(accountAOrgJobSize));
  }


  // @Test
  public void Given_DirectAdmin_When_GetDirectOrganizationJobs_Should_Successful() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(directAdmin, directPassword);

    Response response = spogServer.getOrganizationJobs(directOrgId, null);

    response.then().statusCode(ServerResponseCode.Success_Get);
    response.then().assertThat().body("data.size()", is(accountAOrgJobSize)).body("$",
        hasKey("destination_type"));
    response.then().assertThat().body("data.size()", is(directOrgJobSize));
  }


  @Test
  public void Given_MspAdmin_When_GetOtherOrganizationJobs_Should_Failed() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(mspAdmin, mspPassword);

    Response response = spogServer.getOrganizationJobs(directOrgId, null);

    response.then().statusCode(ServerResponseCode.Insufficient_Permissions);
  }


  @Test
  public void Given_AccountAdmin_When_GetOtherOrganizationJobs_Should_Failed() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(childOrgA_Admin, childOrgA_Password);

    Response response = spogServer.getOrganizationJobs(accountOrgBId, null);

    response.then().statusCode(ServerResponseCode.Insufficient_Permissions);
  }


  @Test
  public void Given_NotLoginUser_When_GetOtherOrganizationJobs_Should_Failed() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.setToken("");

    Response response = spogServer.getOrganizationJobs(accountOrgBId, null);

    response.then().statusCode(ServerResponseCode.Not_login);
  }


  @Test
  public void Given_DirectAdmin_When_GetOtherOrganizationJobs_Should_Failed() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(directAdmin, directPassword);

    Response response = spogServer.getOrganizationJobs(mspOrgId, null);

    response.then().statusCode(ServerResponseCode.Insufficient_Permissions);
  }


  @Test
  public void Given_AccountAdmin_When_GetOtherOrganizationJobsWithInvalidOrganizationId_Should_Failed() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(childOrgA_Admin, childOrgA_Password);

    String orgId = "invalidOrgId";
    Response response = spogServer.getOrganizationJobs(orgId, null);

    response.then().assertThat().statusCode(ServerResponseCode.Bad_Request); // 40000005

    // 00100201,

  }


  @Test
  public void Given_AccountAdmin_When_GetOtherOrganizationJobsEmptyOrganizationId_Should_Failed() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(childOrgA_Admin, childOrgA_Password);

    Response response = spogServer.getOrganizationJobs("EMPTY", null);
    response.then().assertThat().statusCode(ServerResponseCode.Bad_Request); // 00100201,
                                                                             // opened
                                                                             // 822985
  }


  @Test
  public void Given_AccountAdmin_When_GetOtherOrganizationJobsWithNotExistOrganizationId_Should_Failed() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(childOrgA_Admin, childOrgA_Password);

    String orgId = UUID.randomUUID().toString();
    Response response = spogServer.getOrganizationJobs(orgId, null);

    response.then().assertThat().statusCode(ServerResponseCode.Resource_Does_Not_Exist); // 00100201
  }


  // @Test
  public void Given_DirectAdmin_When_GetOtherOrganizationJobsWithRpsId_Should_Success() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(directAdmin, directPassword);

    Response response = spogServer.getOrganizationJobs(directOrgId, null);

    response.then().statusCode(ServerResponseCode.Success_Get);

    String searchStr = "rps_id";
    List<String> abc = response.path("data." + searchStr);
    String target = abc.get(0);

    HashMap<String, String> params = new HashMap<>();
    params.put(searchStr, target);

    response = spogServer.getOrganizationJobs(directOrgId, params);

    response.then().statusCode(ServerResponseCode.Success_Get);

  }


  // @Test
  public void Given_DirectAdmin_When_GetOtherOrganizationJobsWithSiteId_Should_Success() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(directAdmin, directPassword);

    Response response = spogServer.getOrganizationJobs(directOrgId, null);

    response.then().statusCode(ServerResponseCode.Success_Get);

    String searchStr = "site_id";
    List<String> abc = response.path("data." + searchStr);
    String target = abc.get(0);

    HashMap<String, String> params = new HashMap<>();
    params.put(searchStr, target);

    response = spogServer.getOrganizationJobs(directOrgId, params);

    response.then().statusCode(ServerResponseCode.Success_Get);

  }


  // @Test
  public void Given_DirectAdmin_When_GetOtherOrganizationJobsWithServerId_Should_Success() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(directAdmin, directPassword);

    Response response = spogServer.getOrganizationJobs(directOrgId, null);

    response.then().statusCode(ServerResponseCode.Success_Get);

    String searchStr = "server_id";
    List<String> abc = response.path("data." + searchStr);
    String target = abc.get(0);

    HashMap<String, String> params = new HashMap<>();
    params.put(searchStr, target);

    response = spogServer.getOrganizationJobs(directOrgId, params);

    response.then().statusCode(ServerResponseCode.Success_Get);

  }


  // @Test
  public void Given_DirectAdmin_When_GetOtherOrganizationJobsWithServerIds_Should_Success() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(directAdmin, directPassword);

    Response response = spogServer.getOrganizationJobs(directOrgId, null);

    response.then().statusCode(ServerResponseCode.Success_Get);

    String searchStr = "server_id";
    List<String> targetValueList = response.path("data." + searchStr);
    String target = targetValueList.get(0) + "," + targetValueList.get(1);

    HashMap<String, String> params = new HashMap<>();
    params.put(searchStr, target);

    response = spogServer.getOrganizationJobs(directOrgId, params);

    response.then().statusCode(ServerResponseCode.Success_Get);

  }


  // @Test
  public void Given_DirectAdmin_When_GetOtherOrganizationJobsWithRpsIds_Should_Success() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(directAdmin, directPassword);

    Response response = spogServer.getOrganizationJobs(directOrgId, null);

    response.then().statusCode(ServerResponseCode.Success_Get);

    String searchStr = "rps_id";
    List<String> targetValueList = response.path("data." + searchStr);
    String target = targetValueList.get(0) + "," + targetValueList.get(1);

    HashMap<String, String> params = new HashMap<>();
    params.put(searchStr, target);

    response = spogServer.getOrganizationJobs(directOrgId, params);

    response.then().statusCode(ServerResponseCode.Success_Get);

  }


  // @Test
  public void Given_DirectAdmin_When_GetOtherOrganizationJobsWithSiteIds_Should_Success() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(directAdmin, directPassword);

    Response response = spogServer.getOrganizationJobs(directOrgId, null);

    response.then().statusCode(ServerResponseCode.Success_Get);

    String searchStr = "site_id";
    List<String> targetValueList = response.path("data." + searchStr);
    String target = targetValueList.get(0) + "," + targetValueList.get(1);

    HashMap<String, String> params = new HashMap<>();
    params.put(searchStr, target);

    response = spogServer.getOrganizationJobs(directOrgId, params);

    response.then().statusCode(ServerResponseCode.Success_Get);

  }


  @Test
  public void Given_DirectAdmin_When_GetOtherOrganizationJobsWithSortStartTimeAccend_Should_Success() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(directAdmin, directPassword);

    String sortStr = "start_time_ts";
    HashMap<String, String> params = new HashMap<>();
    params.put("sort", sortStr);

    Response response = spogServer.getOrganizationJobs(directOrgId, params);

    response.then().statusCode(ServerResponseCode.Success_Get);

    List<String> targetValueList = response.path("data." + sortStr);

    assertEquals(Ordering.natural().nullsLast().isOrdered(targetValueList), true);
  }


  @Test
  public void Given_DirectAdmin_When_GetOtherOrganizationJobsWithSortStartTimeDeccend_Should_Success() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(directAdmin, directPassword);

    String sortStr = "start_time_ts";
    HashMap<String, String> params = new HashMap<>();
    params.put("sort", "-" + sortStr);

    Response response = spogServer.getOrganizationJobs(directOrgId, params);

    response.then().statusCode(ServerResponseCode.Success_Get);

    List<String> targetValueList = response.path("data." + sortStr);

    assertEquals(Ordering.natural().reverse().isOrdered(targetValueList), true);
  }


  // @Test
  public void Given_DirectAdmin_When_GetOtherOrganizationJobsWithSortStartTimeGreaterAndLessThan_Should_Success() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(directAdmin, directPassword);

    String sortStr = "start_time_ts";
    HashMap<String, String> params = new HashMap<>();
    params.put("sort", "-" + sortStr);

    Response response = spogServer.getOrganizationJobs(directOrgId, params); // 833306
    response = spogServer.getOrganizationJobs(directOrgId, null);
    List<Long> targetValueList = response.path("data." + sortStr);
    long max = targetValueList.get(0) - 1;
    long min = targetValueList.get(targetValueList.size() - 1) + 1;
    String minTime = String.valueOf(min);
    String maxTime = String.valueOf(max);

    HashMap<String, String> params2 = new HashMap<>();
    params2.put("sort", "-" + sortStr);
    params2.put("sortStr", ">" + minTime);
    params2.put("sortStr", "<" + maxTime);

    response = spogServer.getOrganizationJobs(directOrgId, params2);
    response.then().statusCode(ServerResponseCode.Success_Get);
    response.then().assertThat().body("data.size()", is(directOrgJobSize - 2));

    targetValueList = response.path("data." + sortStr);
    assertEquals(Ordering.natural().reverse().isOrdered(targetValueList), true);
  }


  @Test
  public void Given_DirectAdminGetDirectOrganizationJobs_When_PageSizeOver100_Should_ReturnPageSize100() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(directAdmin, directPassword);

    HashMap<String, String> params = new HashMap<>();
    params.put("page_size", "200");

    Response response = spogServer.getOrganizationJobs(directOrgId, params);

    response.then().statusCode(ServerResponseCode.Success_Get);
    response.then().assertThat().body("pagination.page_size", is(100));
  }


  @Test
  public void Given_DirectAdminGetDirectOrganizationJobs_When_PageSize50_Should_ReturnPageSize50() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(directAdmin, directPassword);

    HashMap<String, String> params = new HashMap<>();
    params.put("page_size", "50");

    Response response = spogServer.getOrganizationJobs(directOrgId, params);

    response.then().statusCode(ServerResponseCode.Success_Get);
    response.then().assertThat().body("pagination.page_size", is(50));
  }


  // @Test
  public void Given_DirectAdminGetOtherOrganizationJobs_When_FilterAndPage_Should_Success() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(directAdmin, directPassword);

    Response response = spogServer.getOrganizationJobs(directOrgId, null);

    response.then().statusCode(ServerResponseCode.Success_Get);

    String searchStr = "site_id";
    List<String> targetValueList = response.path("data." + searchStr);
    String target = targetValueList.get(0) + "," + targetValueList.get(1);

    int pagesize = 3;
    HashMap<String, String> params = new HashMap<>();
    params.put(searchStr, target);
    params.put("page_size", "3"); // opened 826442

    double expectedTotalPages = Math.ceil((double) directOrgJobSize / (double) pagesize);
    response = spogServer.getOrganizationJobs(directOrgId, params);

    response.then().statusCode(ServerResponseCode.Success_Get);
    response.then().assertThat().body("pagination.page_size", is(pagesize));
    response.then().assertThat().body("pagination.total_page",
        is(new Double(expectedTotalPages).intValue()));
    response.then().assertThat().body("pagination.total_size", is(directOrgJobSize));
  }


  // @Test
  public void Given_DirectAdminGetOtherOrganizationJobs_When_FilterStartTimeAndPage_Should_Success() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(directAdmin, directPassword);

    Response response = spogServer.getOrganizationJobs(directOrgId, null);

    response.then().statusCode(ServerResponseCode.Success_Get);

    String searchStr = "site_id";
    List<String> targetValueList = response.path("data." + searchStr);
    String target = targetValueList.get(0) + "," + targetValueList.get(1);

    int pagesize = 3;
    HashMap<String, String> params = new HashMap<>();
    params.put(searchStr, target);
    params.put("page_size", "3");
    // params.put("start_time_ts", ">" + Long.toString(System.currentTimeMillis() - 1000000000));
    params.put("start_time_ts", ">2017-01-01");

    double expectedTotalPages = Math.ceil((double) directOrgJobSize / (double) pagesize);
    response = spogServer.getOrganizationJobs(directOrgId, params);

    response.then().statusCode(ServerResponseCode.Success_Get);
    response.then().assertThat().body("pagination.page_size", is(pagesize));
    response.then().assertThat().body("pagination.total_page",
        is(new Double(expectedTotalPages).intValue()));
    response.then().assertThat().body("pagination.total_size", is(directOrgJobSize));
  }


  public void generateJob() {

    // create site
    String spogToken = spogServer.getJWTToken();
    String userID = spogServer.GetLoggedinUser_UserID();
    String siteName = RandomStringUtils.randomAlphanumeric(8);
    String siteType = "gateway";
    String orgID = spogServer.GetLoggedinUserOrganizationID();
    Response response = spogServer.createSite(siteName, siteType, orgID, spogToken, test);

    Map<String, String> sitecreateResMap = new HashMap<>();
    sitecreateResMap = spogServer.checkCreateSite(response, SpogConstants.SUCCESS_POST, siteName,
        siteType, orgID, userID, "", test);

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

    String gatewayID = UUID.randomUUID().toString();
    String gatewayHostname = RandomStringUtils.randomAlphanumeric(8);
    String siteVersion = "";
    response = gatewayServer.RegisterSite(siteRegistrationKey, gatewayID, gatewayHostname,
        siteVersion, siteID, test);
    String site_secret = gatewayServer.checkRegisterSite(response, ServerResponseCode.Succes_Login,
        siteID, siteName, siteType, orgID, userID, true, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
    response = gatewayServer.LoginSite(siteID, site_secret, gatewayID, test);
    String siteToken = response.then().extract().path("data.token");

    // create job
    String prefix = RandomStringUtils.randomAlphanumeric(8);
    String resourceID = spogServer.createSourceWithCheck(prefix + "sourceName", SourceType.machine,
        SourceProduct.cloud_direct, orgID, siteID, ProtectionStatus.unprotect,
        ConnectionStatus.online, "windows", "SQL_SERVER", test);

    long startTimeTS = System.currentTimeMillis();
    long endTimeTS = System.currentTimeMillis();
    String serverID = UUID.randomUUID().toString();
    String rpsID = UUID.randomUUID().toString();
    String destinationID = UUID.randomUUID().toString();
    String policyID = UUID.randomUUID().toString();
    String jobType = "backup";
    String jobMethod = "incremental";
    String jobStatus = "finished";
    gatewayServer.postJobWithCheck(startTimeTS, endTimeTS, orgID, serverID, resourceID, rpsID,
        destinationID, policyID, jobType, jobMethod, jobStatus, siteToken, test);
  }


  public String createSite() {

    String spogToken = spogServer.getJWTToken();
    String userID = spogServer.GetLoggedinUser_UserID();
    String siteName = RandomStringUtils.randomAlphanumeric(8);
    String siteType = "gateway";
    String orgID = spogServer.GetLoggedinUserOrganizationID();
    Response response = spogServer.createSite(siteName, siteType, orgID, spogToken, test);

    Map<String, String> sitecreateResMap = new HashMap<>();
    sitecreateResMap = spogServer.checkCreateSite(response, SpogConstants.SUCCESS_POST, siteName,
        siteType, orgID, userID, "", test);

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

    String gatewayID = UUID.randomUUID().toString();
    String gatewayHostname = RandomStringUtils.randomAlphanumeric(8);
    String siteVersion = "";
    response = gatewayServer.RegisterSite(siteRegistrationKey, gatewayID, gatewayHostname,
        siteVersion, siteID, test);
    String site_secret = gatewayServer.checkRegisterSite(response, ServerResponseCode.Succes_Login,
        siteID, siteName, siteType, orgID, userID, true, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
    response = gatewayServer.LoginSite(siteID, site_secret, gatewayID, test);
    String siteToken = response.then().extract().path("data.token");

    return siteToken;
  }

  // @AfterMethod
  // public void getResult(ITestResult result) {
  //
  // if (result.getStatus() == ITestResult.FAILURE) {
  // count1.setfailedcount();
  // test.log(LogStatus.FAIL, "Test Case Failed is " + result.getName() + " with parameters as "
  // + Arrays.asList(result.getParameters()));
  // test.log(LogStatus.FAIL, result.getThrowable().getMessage());
  // } else if (result.getStatus() == ITestResult.SKIP) {
  // count1.setskippedcount();
  // test.log(LogStatus.SKIP, "Test Case Skipped is " + result.getName());
  // } else if (result.getStatus() == ITestResult.SUCCESS) {
  // count1.setpassedcount();
  // }
  // // ending test
  // // endTest(logger) : It ends the current test and prepares to create HTML report
  // rep.endTest(test);
  // }
  //
  //
  // @AfterTest
  // public void aftertest() {
  //
  // test.log(LogStatus.INFO, "The total test cases passed are " + count1.getpassedcount());
  // test.log(LogStatus.INFO, "the total test cases failed are " + count1.getfailedcount());
  // rep.flush();
  // }
  //
  //
  // @AfterClass
  // public void updatebd() {
  //
  // try {
  // if (count1.getfailedcount() > 0) {
  // Nooftest = count1.getpassedcount() + count1.getfailedcount() + count1.getskippedcount();
  // bqdb1.updateTable(this.BQName, this.runningMachine, this.buildVersion,
  // String.valueOf(Nooftest), Integer.toString(count1.getpassedcount()),
  // Integer.toString(count1.getfailedcount()), String.valueOf(count1.getskippedcount()),
  // count1.getcreationtime(), "Failed");
  // } else {
  // Nooftest = count1.getpassedcount() + count1.getfailedcount() + count1.getskippedcount();
  // bqdb1.updateTable(this.BQName, this.runningMachine, this.buildVersion,
  // String.valueOf(Nooftest), Integer.toString(count1.getpassedcount()),
  // Integer.toString(count1.getfailedcount()), String.valueOf(count1.getskippedcount()),
  // count1.getcreationtime(), "Passed");
  // }
  // } catch (ClientProtocolException e) {
  // // TODO Auto-generated catch block
  // e.printStackTrace();
  // } catch (IOException e) {
  // // TODO Auto-generated catch block
  // e.printStackTrace();
  // }
  // }


}
