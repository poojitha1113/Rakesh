package api.jobs;


import static invoker.SiteTestHelper.composeRandomUserMap;
import static invoker.SiteTestHelper.createOrgnaization;
import static invoker.SiteTestHelper.createSubOrgnaization;
import static invoker.SiteTestHelper.createUser;
import static invoker.SiteTestHelper.getRandomOrganizationName;
import static invoker.SiteTestHelper.getRandomSiteName;
import static invoker.SiteTestHelper.getTestPassword;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

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
import io.restassured.response.Response;

public class UpdateJobTest extends base.prepare.TestApiBase {

  private String token;
  // private ExtentReports extent;
  // private ExtentTest logger;
  private String TestDataPrefix     = getClass().getSimpleName();

  // private SQLServerDb bqdb1;
  // public int Nooftest;
  // private long creationTime;
  // private String BQName = null;
  // private String runningMachine;
  // private testcasescount count1;
  // private String buildVersion;

  private String mspOrgId;
  private String mspAdmin;
  private String mspPassword;
  private String groupId_A_Msp;
  private String groupId_B_Msp;

  private String accountOrgAId;
  private String childOrgA_Admin;
  private String childOrgA_Password;
  private String groupId_AccountA;

  private String accountOrgBId;
  private String childOrgB_Admin;
  private String childOrgB_Password;
  private String groupId_AccountB;

  private String directOrgId;
  private String directAdmin;
  private String directPassword;
  private String groupId_Direct;
  private String groupBId_Direct;

  // private SPOGServer spogServer;
  // private GatewayServer gatewayServer;
  // private ExtentTest test;
  // private ExtentReports rep;
  // private String csrAdminUserName;
  // private String csrAdminPassword;

  // private String baseUri;
  // private String port;

  private int    jobSize            = 2;
  private int    mspOrgJobSize      = jobSize;
  private int    accountAOrgJobSize = jobSize;
  private int    accountBOrgJobSize = jobSize;
  private int    directOrgJobSize   = 5;

  private String mspJobId;
  private String childOrgAJobId;
  private String childOrgBJobId;
  private String directOrgJobId;


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


  public String generateJob() {

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
    String jobId = gatewayServer.postJobWithCheck(startTimeTS, endTimeTS, orgID, serverID,
        resourceID, rpsID, destinationID, policyID, jobType, jobMethod, jobStatus, siteToken, test);

    return jobId;
  }


  public String generateJob_old() {

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
    String serverID = "none";
    String resourceID = "none";
    String rpsID = "none";
    String datastoreID = "none";
    String messageID = "testJobMessage";
    String policyID = "none";
    String organizationID = orgID;
    String[] messageData = new String[] {"node", "agent", "spog", "QA"};
    String destinationID = "none";
    String jobId = gatewayServer.postJobWithCheck(startTimeTS, (startTimeTS + 1000000),
        organizationID, serverID, resourceID, rpsID, destinationID, policyID, "backup",
        "incremental", "finished", token, test);
    return jobId;
  }


  public void updateJob(String jobId) throws InterruptedException {

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
    String serverID = "none";
    String resourceID = "none";
    String rpsID = "none";
    String datastoreID = "none";
    String messageID = "testJobMessage";
    String policyID = "none";
    // String organizationID = orgID;
    String organizationID = "";
    String[] messageData = new String[] {"node", "agent", "spog", "QA"};
    String destinationID = "none";
    gatewayServer.updateJobWithCheck(jobId, startTimeTS, (startTimeTS + 1000000), organizationID,
        serverID, resourceID, rpsID, destinationID, policyID, "backup", "incremental", "finished",
        token, test);

  }


  public void updateJob(String userName, String password, String jobId)
      throws InterruptedException {

    spogServer.userLogin(userName, password);
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

    // Thread.sleep(15000);

    long startTimeTS = System.currentTimeMillis();
    String serverID = "none";
    String resourceID = "none";
    String rpsID = "none";
    String datastoreID = "none";
    String messageID = "testJobMessage";
    String policyID = "none";
    // String organizationID = orgID;
    String organizationID = orgID;
    String[] messageData = new String[] {"node", "agent", "spog", "QA"};
    String destinationID = "none";
    response = gatewayServer.updateJob(jobId, startTimeTS, (startTimeTS + 1000000), organizationID,
        serverID, resourceID, rpsID, destinationID, policyID, "backup", "incremental", "finished",
        token, test);

  }


  public Response generateAndUpdateJob(long startTimeTS, long endTimeTS, String organizationID,
      String serverID, String resourceID, String rpsID, String destinationID, String policyID,
      String jobType, String jobMethod, String jobStatus, boolean isCheckResponse) {

    // spogServer.userLogin(userName, password);
    String spogToken = spogServer.getJWTToken();
    String userID = spogServer.GetLoggedinUser_UserID();
    String orgID = spogServer.GetLoggedinUserOrganizationID();

    if (organizationID == "NoProvide") {
      organizationID = orgID;
    }

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

    String jobId = generateJob();
    Response aResponse = null;
    if (isCheckResponse) {
      organizationID = "none";
      serverID = "none";
      resourceID = "none";
      rpsID = "none";
      destinationID = "none";
      policyID = "none";
      jobType = "none";
      jobStatus = "none";
      gatewayServer.updateJobWithCheck(jobId, startTimeTS, (startTimeTS + 1000000), organizationID,
          serverID, resourceID, rpsID, destinationID, policyID, jobType, jobMethod, jobStatus,
          token, test);
    } else {
      organizationID = "none";
      serverID = "none";
      resourceID = "none";
      rpsID = "none";
      destinationID = "none";
      policyID = "none";
      jobType = "none";
      jobStatus = "none";
      aResponse = gatewayServer.updateJob(jobId, startTimeTS, endTimeTS, organizationID, serverID,
          resourceID, rpsID, destinationID, policyID, jobType, jobMethod, jobStatus, token, test);
    }

    return aResponse;
  }


  public void generateJobs(int size) {

    for (int i = 1; i <= size; i++) {
      generateJob();
    }
  }


  @BeforeClass
  public void prepareJobData() {

    test.assignAuthor("Yin.Li");
    test = rep.startTest("Prepare Job Data");

    spogServer.userLogin(csrAdminUserName, csrAdminPassword);
    token = spogServer.getJWTToken();
    String csrToken = this.token;

    // create msp org
    mspOrgId = createOrgnaization(getRandomOrganizationName(TestDataPrefix), SpogConstants.MSP_ORG,
        RandomStringUtils.randomAlphanumeric(8) + "@SpogQa",
        RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(4),
        RandomStringUtils.randomAlphanumeric(4), csrToken);

    // create msp admin
    Map<String, String> userInfoMap =
        composeRandomUserMap(TestDataPrefix, SpogConstants.MSP_ADMIN, mspOrgId);
    createUser(userInfoMap, csrToken);
    mspAdmin = userInfoMap.get("email");
    mspPassword = getTestPassword();
    // String mspToken = loginSpogServer(mspAdmin, mspPassword);
    spogServer.userLogin(mspAdmin, mspPassword);
    String mspToken = spogServer.getJWTToken();

    mspJobId = generateJob();

    // create child org A admin
    accountOrgAId =
        createSubOrgnaization(getRandomOrganizationName(TestDataPrefix), mspOrgId, mspToken);

    userInfoMap = composeRandomUserMap(TestDataPrefix, SpogConstants.DIRECT_ADMIN, accountOrgAId);
    createUser(userInfoMap, mspToken);
    childOrgA_Admin = userInfoMap.get("email");
    childOrgA_Password = getTestPassword();
    spogServer.userLogin(childOrgA_Admin, childOrgA_Password);
    childOrgAJobId = generateJob();

    // create child org B admin
    accountOrgBId =
        createSubOrgnaization(getRandomOrganizationName(TestDataPrefix), mspOrgId, mspToken);

    userInfoMap = composeRandomUserMap(TestDataPrefix, SpogConstants.DIRECT_ADMIN, accountOrgBId);
    createUser(userInfoMap, mspToken);
    childOrgB_Admin = userInfoMap.get("email");
    childOrgB_Password = getTestPassword();
    spogServer.userLogin(childOrgB_Admin, childOrgB_Password);
    childOrgBJobId = generateJob();

    // create direct org
    directOrgId = createOrgnaization(getRandomOrganizationName(TestDataPrefix),
        SpogConstants.DIRECT_ORG, RandomStringUtils.randomAlphanumeric(8) + "@SpogQa",
        RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(4),
        RandomStringUtils.randomAlphanumeric(4), csrToken);
    // create direct admin
    userInfoMap = composeRandomUserMap(TestDataPrefix, SpogConstants.DIRECT_ADMIN, directOrgId);
    createUser(userInfoMap, csrToken);
    this.directAdmin = userInfoMap.get("email");
    this.directPassword = getTestPassword();
    spogServer.userLogin(directAdmin, directPassword);
    // generateJobs(directOrgJobSize);
    directOrgJobId = generateJob();
  }



  @Test
  public void Given_DirectAdmin_When_UpdateJob_Should_Success() throws InterruptedException {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(directAdmin, directPassword);

    String directOrgJobId2 = generateJob();
    updateJob(directAdmin, directPassword, directOrgJobId2);
  }

  @Test
  public void Given_Csr_ReadOnly_User_When_UpdateJob_Should_Failed() {

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
    
    //prepare
    spogServer.userLogin(directAdmin, directPassword);
    String directOrgJobId2 = generateJob();
    
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

    // Thread.sleep(15000);

    long startTimeTS = System.currentTimeMillis();
    String serverID = "none";
    String resourceID = "none";
    String rpsID = "none";
    String datastoreID = "none";
    String messageID = "testJobMessage";
    String policyID = "none";
    // String organizationID = orgID;
    String organizationID = orgID;
    String[] messageData = new String[] {"node", "agent", "spog", "QA"};
    String destinationID = "none";
    
    //call api
    spogServer.userLogin(csrReadOnlyUser, password);
	gatewayServer.setToken(spogServer.getJWTToken());
	token = spogServer.getJWTToken();
	
    response = gatewayServer.updateJob(directOrgJobId2, startTimeTS, (startTimeTS + 1000000), organizationID,
            serverID, resourceID, rpsID, destinationID, policyID, "backup", "incremental", "finished",
            token, test);
    response.then().statusCode(ServerResponseCode.Insufficient_Permissions);
    
    spogServer.userLogin(csrAdminUserName, csrAdminPassword);
    spogServer.CheckDeleteUserByIdStatus(csrReadOnlyUserId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
  }	


  @Test
  public void Given_DirectAdmin_When_UpdateJobWithNotExistJobId_Should_Failed() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(directAdmin, directPassword);

    String jobId = UUID.randomUUID().toString();;
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
    String serverID = "none";
    String resourceID = "none";
    String rpsID = "none";
    String datastoreID = "none";
    String messageID = "testJobMessage";
    String policyID = "none";
    String organizationID = orgID;
    String[] messageData = new String[] {"node", "agent", "spog", "QA"};
    String destinationID = "none";
    response = gatewayServer.updateJob(jobId, startTimeTS, (startTimeTS + 1000000), organizationID,
        serverID, resourceID, rpsID, destinationID, policyID, "backup", "incremental", "finished",
        token, test);

  }


  @Test
  public void Given_DirectAdmin_When_UpdateJobInOtherOrg_Should_Failed() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(directAdmin, directPassword);

    String jobId = mspJobId;
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
    String serverID = "none";
    String resourceID = "none";
    String rpsID = "none";
    String datastoreID = "none";
    String messageID = "testJobMessage";
    String policyID = "none";
    String organizationID = "";
    String[] messageData = new String[] {"node", "agent", "spog", "QA"};
    String destinationID = "none";
    response = gatewayServer.updateJob(jobId, startTimeTS, (startTimeTS + 1000000), organizationID,
        serverID, resourceID, rpsID, destinationID, policyID, "backup", "incremental", "finished",
        token, test);
    response.then().assertThat().body("errors.code.size()", is(1)).body("errors.code",
        hasItem("00100101"));
  }


  @Test
  public void Given_DirectAdmin_When_NotLoginSite_Should_Failed() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(directAdmin, directPassword);

    String jobId = directOrgJobId;
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
    String serverID = "none";
    String resourceID = "none";
    String rpsID = "none";
    String datastoreID = "none";
    String messageID = "testJobMessage";
    String policyID = "none";
    String organizationID = "none";
    token = "";
    String[] messageData = new String[] {"node", "agent", "spog", "QA"};
    String destinationID = UUID.randomUUID().toString();
    response = gatewayServer.updateJob(jobId, startTimeTS, (startTimeTS + 1000000), organizationID,
        serverID, resourceID, rpsID, destinationID, policyID, "backup", "incremental", "finished",
        token, test);
    response.then().assertThat().body("errors.code.size()", is(1)).body("errors.code",
        hasItem("00900006"));
  }


  @Test
  public void Given_DirectAdmin_When_GetOtherOrganizationJobsWithEmptyOrgId_Should_Success() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(directAdmin, directPassword);

    String jobId = directOrgJobId;
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
    String serverID = "none";
    String resourceID = "none";
    String rpsID = "none";
    String datastoreID = "none";
    String messageID = "testJobMessage";
    String policyID = "none";
    String organizationID = "none";
    String[] messageData = new String[] {"node", "agent", "spog", "QA"};
    String destinationID = "none";
    gatewayServer.updateJobWithCheck(jobId, startTimeTS, (startTimeTS + 1000000), organizationID,
        serverID, resourceID, rpsID, destinationID, policyID, "backup", "incremental", "finished",
        token, test);

  }


  @Test
  public void Given_DirectAdmin_When_UpdateJobWithInvalidStartTime_Should_Failed() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(directAdmin, directPassword);

    String jobId = generateJob();
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

    long startTimeTS = (long) -1;
    String serverID = "none";
    String resourceID = "none";
    String rpsID = "none";
    String datastoreID = "none";
    String messageID = "testJobMessage";
    String policyID = "none";
    String organizationID = orgID;
    String[] messageData = new String[] {"node", "agent", "spog", "QA"};
    String destinationID = "none";
    response = gatewayServer.updateJob(jobId, startTimeTS, (startTimeTS + 1000000), organizationID,
        serverID, resourceID, rpsID, destinationID, policyID, "backup", "incremental", "finished",
        token, test);

  }


  @Test
  public void Given_DirectAdmin_When_UpdateJobWithInvalidJobType_Should_Failed() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(directAdmin, directPassword);

    String jobId = generateJob();
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

    long startTimeTS = (long) -1;
    String serverID = "none";
    String resourceID = "none";
    String rpsID = "none";
    String datastoreID = "none";
    String messageID = "testJobMessage";
    String policyID = "none";
    String organizationID = "";
    String[] messageData = new String[] {"node", "agent", "spog", "QA"};
    String destinationID = "none";
    response = gatewayServer.updateJob(jobId, startTimeTS, (startTimeTS + 1000000), organizationID,
        serverID, resourceID, rpsID, destinationID, policyID, "invalidJobType", "incremental",
        "finished", token, test);

    response.then().statusCode(ServerResponseCode.Bad_Request);
  }


  @Test
  public void Given_DirectAdmin_When_UpdateJobWithInvalidJobMetod_Should_Failed() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(directAdmin, directPassword);


    String jobId = generateJob();
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

    long startTimeTS = (long) -1;
    String serverID = "none";
    String resourceID = "none";
    String rpsID = "none";
    String datastoreID = "none";
    String messageID = "testJobMessage";
    String policyID = "none";
    String organizationID = "";
    String[] messageData = new String[] {"node", "agent", "spog", "QA"};
    String destinationID = "none";
    response = gatewayServer.updateJob(jobId, startTimeTS, (startTimeTS + 1000000), organizationID,
        serverID, resourceID, rpsID, destinationID, policyID, "backup", "invalidMethod", "finished",
        token, test);

    response.then().statusCode(ServerResponseCode.Bad_Request);

  }


  @Test
  public void Given_DirectAdmin_When_UpdateJobWithInvalidOrgId_Should_Failed() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(directAdmin, directPassword);

    String jobId = directOrgJobId;
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
    String serverID = "none";
    String resourceID = "none";
    String rpsID = "none";
    String datastoreID = "none";
    String messageID = "testJobMessage";
    String policyID = "none";
    String organizationID = "none";
    String[] messageData = new String[] {"node", "agent", "spog", "QA"};
    String destinationID = "none";
    response = gatewayServer.updateJob(jobId, startTimeTS, (startTimeTS + 1000000), organizationID,
        serverID, resourceID, rpsID, destinationID, policyID, "backup", "incremental", "finished",
        token, test);

  }


  @DataProvider(
      name = "correctData")
  public final Object[][] correctJobData() {

    return new Object[][] {
        {csrAdminUserName, csrAdminPassword, 100000, 200000, "NoProvide",
            UUID.randomUUID().toString(), UUID.randomUUID().toString(),
            UUID.randomUUID().toString(), UUID.randomUUID().toString(),
            UUID.randomUUID().toString(), "backup", "incremental", "finished"},
        {mspAdmin, mspPassword, 100000, 200000, "NoProvide", UUID.randomUUID().toString(),
            UUID.randomUUID().toString(), UUID.randomUUID().toString(),
            UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "incremental",
            "finished"},
        {childOrgA_Admin, childOrgA_Password, 100000, 200000, "NoProvide",
            UUID.randomUUID().toString(), UUID.randomUUID().toString(),
            UUID.randomUUID().toString(), UUID.randomUUID().toString(),
            UUID.randomUUID().toString(), "backup", "incremental", "finished"},
        {directAdmin, directPassword, 100000, 200000, "NoProvide", UUID.randomUUID().toString(),
            UUID.randomUUID().toString(), UUID.randomUUID().toString(),
            UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "incremental",
            "finished"},};
  }


  @Test(
      dataProvider = "correctData")
  public void Given_User_When_UpdateJob_Should_Success(String loginUser, String loginPsw,
      long startTimeTS, long endTimeTS, String organizationID, String serverID, String resourceID,
      String rpsID, String destinationID, String policyID, String jobType, String jobMethod,
      String jobStatus) throws InterruptedException {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    startTimeTS = System.currentTimeMillis() - 300000000;
    endTimeTS = System.currentTimeMillis();
    spogServer.userLogin(loginUser, loginPsw);
    generateAndUpdateJob(startTimeTS, endTimeTS, organizationID, serverID, resourceID, rpsID,
        destinationID, policyID, jobType, jobMethod, jobStatus, true);
    // bug 851124
  }


  @DataProvider(
      name = "invalidJobData")
  public final Object[][] invalidJobData() {

    return new Object[][] {
        {"invalid starttime", mspAdmin, mspPassword, -1, 200000, "NoProvide", "none", "none",
            "none", "none", "none", "backup", "incremental", "finished"}, // invalid starttime
        {"invalid endtime", mspAdmin, mspPassword, 100000, -1, "NoProvide", "none", "none", "none",
            "none", "none", "backup", "incremental", "finished"}, // invalid end time
        {"not exist org id", mspAdmin, mspPassword, 100000, 200000, UUID.randomUUID().toString(),
            "none", "none", "none", "none", "none", "backup", "incremental", "finished"}, // no
                                                                                          // exist
                                                                                          // org id
        {"empty server id", mspAdmin, mspPassword, 100000, 200000, "NoProvide", "", "none", "none",
            "none", "none", "backup", "incremental", "finished"}, // empty server id
        {"empty resource id", mspAdmin, mspPassword, 100000, 200000, "none", "none", "",
            UUID.randomUUID().toString(), "none", "none", "backup", "incremental", "finished"}, // empty
                                                                                                // resource
                                                                                                // id
        {"empty rps id", mspAdmin, mspPassword, 100000, 200000, "none", "none", "none", "", "none",
            "none", "backup", "incremental", "finished"}, // empty rps id
        {"empty dest id", mspAdmin, mspPassword, 100000, 200000, "", "none", "none", "none", "",
            "none", "backup", "incremental", "finished"}, // empty dest ID
        {"empty policy id", mspAdmin, mspPassword, 100000, 200000, "", "none", "none", "none",
            "none", "", "backup", "incremental", "finished"}, // empty
                                                              // policyID

        {"invalid job type", mspAdmin, mspPassword, 100000, 200000, "", "none", "none", "none",
            "none", "none", "invalid", "incremental", "finished"}, // invalid job type
        {"empty job type", mspAdmin, mspPassword, 100000, 200000, "", "none", "none", "none",
            "none", "none", "invalid", "incremental", "finished"}, // empty job type
        {"invalid job method", mspAdmin, mspPassword, 100000, 200000, "", "none", "none", "none",
            "none", "none", "backup", "invalid", "finished"}, // invalid job method
        {"empty job type", mspAdmin, mspPassword, 100000, 200000, "", "none", "none", "none",
            "none", "none", "backup", "", "finished"}, // empty
        // job
        // type
        {"invalid job status", mspAdmin, mspPassword, 100000, 200000, "", "none", "none", "none",
            "none", "none", "backup", "incremental", "invalid"}, // invalid job status
        {"empty job status", mspAdmin, mspPassword, 100000, 200000, "", "none", "none", "none",
            "none", "none", "backup", "incremental", ""},};// empty job status
  }


  // @Test(
  // dataProvider = "invalidJobData")
  public void Given_User_When_UpdateJobWithInvalidData_Should_Failed(String desc, String loginUser,
      String loginPsw, long startTimeTS, long endTimeTS, String organizationID, String serverID,
      String resourceID, String rpsID, String destinationID, String policyID, String jobType,
      String jobMethod, String jobStatus) throws InterruptedException {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(loginUser, loginPsw);
    Response response = generateAndUpdateJob(startTimeTS, endTimeTS, organizationID, serverID,
        resourceID, rpsID, destinationID, policyID, jobType, jobMethod, jobStatus, false);

    response.then().statusCode(ServerResponseCode.Bad_Request);
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
