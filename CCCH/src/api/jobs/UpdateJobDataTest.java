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

public class UpdateJobDataTest extends base.prepare.TestApiBase {

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
  //
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

  @Override
  public void setAuthor() {

    author = "Yin.Li";

  }


  public HashMap<String, String> composeJobDataInfo(String job_id,
      String transferred_uncompressed_bytes, String transferred_directories,
      String transferred_files, String transferred_bytes, String files,
      String processed_directories, String processed_bytes_processed,
      String processed_bytes_changed, String warning_count, String error_count, String bucket_id) {

    HashMap<String, String> jobDataInfo = new HashMap<>();
    jobDataInfo.put("job_id", job_id);
    jobDataInfo.put("transferred_uncompressed_bytes", transferred_uncompressed_bytes);
    jobDataInfo.put("transferred_directories", transferred_directories);
    jobDataInfo.put("transferred_files", transferred_files);
    jobDataInfo.put("transferred_bytes", transferred_bytes);
    jobDataInfo.put("files", files);
    jobDataInfo.put("processed_directories", processed_directories);
    jobDataInfo.put("processed_bytes_processed", processed_bytes_processed);
    jobDataInfo.put("processed_bytes_changed", processed_bytes_changed);
    jobDataInfo.put("warning_count", warning_count);
    jobDataInfo.put("error_count", error_count);
    jobDataInfo.put("bucket_id", bucket_id);
    return jobDataInfo;
  }



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
    String jobId = gatewayServer.postJobWithCheck(startTimeTS, (startTimeTS + 1000000),
        organizationID, serverID, resourceID, rpsID, destinationID, policyID, "backup",
        "incremental", "finished", token, test);
    return jobId;
  }


  public void generateAndUpdateJob_old() {

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
    String jobId = gatewayServer.postJobWithCheck(startTimeTS, (startTimeTS + 1000000),
        organizationID, serverID, resourceID, rpsID, destinationID, policyID, "backup",
        "incremental", "finished", token, test);

    updateJobData(jobId, token, true);

  }


  public void generateAndUpdateJob() {

    String jobId = generateJob();

    updateJobData(jobId, token, true);

  }


  public Response updateJobData(String jobId, String gatewayToken, boolean willCheck) {

    String job_id = jobId;
    String jobSeq = "88";
    String jobType = "backup";
    String jobMethod = "Full";
    String jobStatus = "finished";
    String endTimeTS = String.valueOf(System.currentTimeMillis());
    String protectedDataSize = "120000";
    String rawDataSize = "12200";
    String syncReadSize = "122200";
    String ntfsVolumeSize = "12220";
    String virtualDiskProvisionSize = "12222";
    String token = gatewayToken;
    String severity = "";
    String percent_complete = "1";

    Response aResponse = null;

    if (willCheck) {
      gatewayServer.updateJobDataWithCheck(job_id, jobSeq, severity, percent_complete,
          protectedDataSize, rawDataSize, syncReadSize, ntfsVolumeSize, virtualDiskProvisionSize,
          token, test);
    } else {
      aResponse = gatewayServer.updateJobData(job_id, job_id, jobSeq, jobType, jobMethod, jobStatus,
          endTimeTS, protectedDataSize, rawDataSize, syncReadSize, ntfsVolumeSize,
          virtualDiskProvisionSize, token, test);
    }

    return aResponse;

  }


  public Response updateJobData(HashMap<String, String> jobDataInfo, String gatewayToken,
      boolean willCheck) {

    Response aResponse = null;
    // gatewayServer.setToken(gatewayToken);

    aResponse =
        gatewayServer.updateJobData(jobDataInfo.get("job_id"), jobDataInfo, gatewayToken, test);
    return aResponse;

  }


  public Response updateJobData(String jobId, String gatewayToken, boolean willCheck,
      String protectedDataSize, String rawDataSize, String syncReadSize, String ntfsVolumeSize) {

    String job_id = jobId;
    String jobSeq = "88";
    String jobType = "backup";
    String jobMethod = "Full";
    String jobStatus = "finished";
    String endTimeTS = String.valueOf(System.currentTimeMillis());
    protectedDataSize = "120000";
    rawDataSize = "12200";
    syncReadSize = "122200";
    ntfsVolumeSize = "12220";
    String virtualDiskProvisionSize = "12222";
    String token = gatewayToken;
    String severity = "";
    String percent_complete = "1";

    Response aResponse = null;

    if (willCheck) {
      gatewayServer.updateJobDataWithCheck(job_id, jobSeq, severity, percent_complete,
          protectedDataSize, rawDataSize, syncReadSize, ntfsVolumeSize, virtualDiskProvisionSize,
          token, test);
    } else {
      aResponse = gatewayServer.updateJobData(job_id, job_id, jobSeq, jobType, jobMethod, jobStatus,
          endTimeTS, protectedDataSize, rawDataSize, syncReadSize, ntfsVolumeSize,
          virtualDiskProvisionSize, token, test);
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



  // @BeforeClass
  // public void prepareJobData2() {
  //
  // test.assignAuthor("Yin.Li");
  // test = rep.startTest("Prepare Job Data");
  //
  // spogServer.userLogin(csrAdminUserName, csrAdminPassword);
  // String csrToken = this.token;
  //
  // // create msp org
  // mspOrgId = createOrgnaization(getRandomOrganizationName(TestDataPrefix), SpogConstants.MSP_ORG,
  // RandomStringUtils.randomAlphanumeric(8) + "@SpogQa",
  // RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(4),
  // RandomStringUtils.randomAlphanumeric(4), csrToken);
  //
  // // create msp admin
  // Map<String, String> userInfoMap =
  // composeRandomUserMap(TestDataPrefix, SpogConstants.MSP_ADMIN, mspOrgId);
  // createUser(userInfoMap, csrToken);
  // mspAdmin = userInfoMap.get("email");
  // mspPassword = getTestPassword();
  // // String mspToken = loginSpogServer(mspAdmin, mspPassword);
  // spogServer.userLogin(mspAdmin, mspPassword);
  // String mspToken = spogServer.getJWTToken();
  //
  // mspJobId = generateJob();
  //
  // // create child org A admin
  // accountOrgAId =
  // createSubOrgnaization(getRandomOrganizationName(TestDataPrefix), mspOrgId, mspToken);
  //
  // userInfoMap = composeRandomUserMap(TestDataPrefix, SpogConstants.DIRECT_ADMIN, accountOrgAId);
  // createUser(userInfoMap, mspToken);
  // childOrgA_Admin = userInfoMap.get("email");
  // childOrgA_Password = getTestPassword();
  // spogServer.userLogin(childOrgA_Admin, childOrgA_Password);
  // childOrgAJobId = generateJob();
  //
  // // create child org B admin
  // accountOrgBId =
  // createSubOrgnaization(getRandomOrganizationName(TestDataPrefix), mspOrgId, mspToken);
  //
  // userInfoMap = composeRandomUserMap(TestDataPrefix, SpogConstants.DIRECT_ADMIN, accountOrgBId);
  // createUser(userInfoMap, mspToken);
  // childOrgB_Admin = userInfoMap.get("email");
  // childOrgB_Password = getTestPassword();
  // spogServer.userLogin(childOrgB_Admin, childOrgB_Password);
  // childOrgBJobId = generateJob();
  //
  // // create direct org
  // directOrgId = createOrgnaization(getRandomOrganizationName(TestDataPrefix),
  // SpogConstants.DIRECT_ORG, RandomStringUtils.randomAlphanumeric(8) + "@SpogQa",
  // RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(4),
  // RandomStringUtils.randomAlphanumeric(4), csrToken);
  // // create direct admin
  // userInfoMap = composeRandomUserMap(TestDataPrefix, SpogConstants.DIRECT_ADMIN, directOrgId);
  // createUser(userInfoMap, csrToken);
  // this.directAdmin = userInfoMap.get("email");
  // this.directPassword = getTestPassword();
  // spogServer.userLogin(directAdmin, directPassword);
  // generateJobs(directOrgJobSize);
  // directOrgJobId = generateJob();
  // }



  @Test
  public void Given_DirectAdmin_When_UpdateJobData_Should_Success() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(directAdmin, directPassword);

    generateAndUpdateJob(); // blocked by 853578

  }

  @Test
  public void Given_Csr_ReadOnly_User_When_UpdateJobData_Should_Failed() {

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
    
    String jobId = generateJob();

    String jobSeq = "88";
    String jobType = "backup";
    String jobMethod = "Full";
    String jobStatus = "finished";
    String endTimeTS = String.valueOf(System.currentTimeMillis());
    String protectedDataSize = "120000";
    String rawDataSize = "12200";
    String syncReadSize = "122200";
    String ntfsVolumeSize = "12220";
    String virtualDiskProvisionSize = "12222";
    String severity = "";
    String percent_complete = "1";

  //invoke api
    spogServer.userLogin(csrReadOnlyUser, password);
    gatewayServer.setToken(spogServer.getJWTToken());
    
    Response response = gatewayServer.updateJobData(jobId, jobId, jobSeq, jobType, jobMethod, jobStatus,
          endTimeTS, protectedDataSize, rawDataSize, syncReadSize, ntfsVolumeSize,
          virtualDiskProvisionSize, spogServer.getJWTToken(), test);
    
    response.then().statusCode(ServerResponseCode.Insufficient_Permissions);
    
    
    spogServer.userLogin(csrAdminUserName, csrAdminPassword);
    spogServer.CheckDeleteUserByIdStatus(csrReadOnlyUserId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
  }	


  @Test
  public void Given_DirectAdmin_When_UpdateJobDataInOtherOrg_Should_Failed() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(directAdmin, directPassword);
    String loginToken = spogServer.getJWTToken();
    String jobId = generateJob();

    Response response = updateJobData(mspJobId, loginToken, false);

    response.then().statusCode(ServerResponseCode.Insufficient_Permissions);
    response.then().assertThat().body("errors.code.size()", is(1)).body("errors.code",
        hasItem("00100101"));
  }


  @Test
  public void Given_DirectAdmin_When_UpdateJobDataNotExistJobId_Should_Failed() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(directAdmin, directPassword);

    String notEixstJobId = UUID.randomUUID().toString();
    Response response = updateJobData(notEixstJobId, spogServer.getJWTToken(), false);
    response.then().statusCode(ServerResponseCode.Resource_Does_Not_Exist);
  }


  @DataProvider(
      name = "jobData")
  public Object[][] jobData() {

    return new Object[][] {
        {"123", "123", "123", "123", "123", "123", "123", "123", "123", "123", "123"},
        {"", "123", "123", "123", "123", "123", "123", "123", "123", "123", "123"},
        {"123", "", "123", "123", "123", "123", "123", "123", "123", "123", "123"},
        {"123", "123", "", "123", "123", "123", "123", "123", "123", "123", "123"},
        {"123", "123", "123", "", "123", "123", "123", "123", "123", "123", "123"},
        {"123", "123", "123", "123", "", "123", "123", "123", "123", "123", "123"},
        {"123", "123", "123", "123", "123", "", "123", "123", "123", "123", "123"},
        {"123", "123", "123", "123", "123", "123", "", "123", "123", "123", "123"},
        {"123", "123", "123", "123", "123", "123", "123", "", "123", "123", "123"},
        {"123", "123", "123", "123", "123", "123", "123", "123", "", "123", "123"},
        {"123", "123", "123", "123", "123", "123", "123", "123", "123", "", "123"},
        {"123", "123", "123", "123", "123", "123", "123", "123", "123", "123", ""}};
  }


  @Test(
      dataProvider = "jobData")
  public void Given_DirectAdmin_When_UpdateJobDataWithEnhancementData_Should_Success(
      String transferred_uncompressed_bytes, String transferred_directories,
      String transferred_files, String transferred_bytes, String files,
      String processed_directories, String processed_bytes_processed,
      String processed_bytes_changed, String warning_count, String error_count, String bucket_id) {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(directAdmin, directPassword);

    // spogServer.userLogin(userName, password);
    String spogToken = spogServer.getJWTToken();

    String jobId = generateJob();

    HashMap<String, String> jobDataInfo = composeJobDataInfo(jobId, transferred_uncompressed_bytes,
        transferred_directories, transferred_files, transferred_bytes, files, processed_directories,
        processed_bytes_processed, processed_bytes_changed, warning_count, error_count, bucket_id);

    // response = gatewayServer.LoginSite(siteID, site_secret, gatewayID, test);
    // token = response.then().extract().path("data.token");
    Response response = updateJobData(jobDataInfo, spogToken, false);

    response.then().statusCode(ServerResponseCode.Success_Put);
  }


  @DataProvider(
      name = "invalidJobData")
  public Object[][] invalidJobData() {

    return new Object[][] {
        {"a", "123", "123", "123", "123", "123", "123", "123", "123", "123", "123"},
        {"123", "a", "123", "123", "123", "123", "123", "123", "123", "123", "123"},
        {"123", "123", "a", "123", "123", "123", "123", "123", "123", "123", "123"},
        {"123", "123", "123", "a", "123", "123", "123", "123", "123", "123", "123"},
        {"123", "123", "123", "123", "123", "a", "123", "123", "123", "123", "123"},
        {"123", "123", "123", "123", "123", "123", "a", "123", "123", "123", "123"},
        {"123", "123", "123", "123", "123", "123", "123", "a", "123", "123", "123"},
        {"123", "123", "123", "123", "123", "123", "123", "123", "a", "123", "123"},
        {"123", "123", "123", "123", "123", "123", "123", "123", "123", "a", "123"},
        {"123", "123", "123", "123", "123", "123", "123", "123", "123", "123", "a"},};
  }


  // @Test(
  // dataProvider = "invalidJobData")
  public void Given_DirectAdmin_When_UpdateJobDataWithInvalidEnhancementData_Should_Failed(
      String transferred_uncompressed_bytes, String transferred_directories,
      String transferred_files, String transferred_bytes, String files,
      String processed_directories, String processed_bytes_processed,
      String processed_bytes_changed, String warning_count, String error_count, String bucket_id) {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(directAdmin, directPassword);

    // spogServer.userLogin(userName, password);
    String spogToken = spogServer.getJWTToken();

    String jobId = generateJob();

    HashMap<String, String> jobDataInfo = composeJobDataInfo(jobId, transferred_uncompressed_bytes,
        transferred_directories, transferred_files, transferred_bytes, files, processed_directories,
        processed_bytes_processed, processed_bytes_changed, warning_count, error_count, bucket_id);

    // response = gatewayServer.LoginSite(siteID, site_secret, gatewayID, test);
    // token = response.then().extract().path("data.token");
    Response response = updateJobData(jobDataInfo, spogToken, false);

    response.then().statusCode(ServerResponseCode.Bad_Request);
  }


  @DataProvider(
      name = "updateInvalid")
  public Object[][] updateInvalid() {

    return new Object[][] {{"current_session", "1"}, {"target_cloud_hybrid_store", "1"},
        {"start_session", "1"}, {"end_session", "1"}, {"saved_bandwidth_percentage", "1"},
        {"source_recovery_point_server", "1"}, {"source_data_store_name", "1"}};
  }


  @Test(
      dataProvider = "updateInvalid")
  public void Given_DirectAdmin_When_UpdateJobDataWithInvalidEnhancementData_Should_Failed2(
      String updateKey, String updateValue) {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(directAdmin, directPassword);
    String spogToken = spogServer.getJWTToken();

    String jobId = generateJob();

    HashMap<String, String> jobDataInfo = new HashMap<String, String>();
    jobDataInfo.put("job_id", jobId);
    jobDataInfo.put(updateKey, updateValue);

    // response = gatewayServer.LoginSite(siteID, site_secret, gatewayID, test);
    // token = response.then().extract().path("data.token");
    Response response = updateJobData(jobDataInfo, spogToken, false);

    response.then().statusCode(ServerResponseCode.Success_Put);
  }


  @Test
  public void Given_DirectAdmin_When_UpdateJobSiteNotLogin_Should_Failed() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(directAdmin, directPassword);
    String jobId = UUID.randomUUID().toString();
    Response response = updateJobData(jobId, "", false);
    response.then().statusCode(ServerResponseCode.Not_login);
  }


  @Test
  public void Given_DirectAdmin_When_UpdateJobDataWithInvalidProtectedDataSize_Should_failed() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(directAdmin, directPassword);


    String spogToken = spogServer.getJWTToken();

    String jobId = generateJob();

    Response response = updateJobData(jobId, spogToken, false, "-1", "120000", "120000", "120000");
    response.then().statusCode(ServerResponseCode.Success_Put);
  }


  @Test
  public void Given_DirectAdmin_When_UpdateJobDataWithInvalidRawSize_Should_failed() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(directAdmin, directPassword);


    String spogToken = spogServer.getJWTToken();

    String jobId = generateJob();


    Response response = updateJobData(jobId, token, false, "120000", "-1", "120000", "120000");
    response.then().statusCode(ServerResponseCode.Success_Put);
  }


  @Test
  public void Given_DirectAdmin_When_UpdateJobDataWithInvalidsyncReadSize_Should_failed() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(directAdmin, directPassword);

    String spogToken = spogServer.getJWTToken();

    String jobId = generateJob();

    Response response = updateJobData(jobId, token, false, "120000", "1200000", "-1", "120000");
    response.then().statusCode(ServerResponseCode.Success_Put);
  }


  @Test
  public void Given_DirectAdmin_When_UpdateJobDataWithInvalidntfsVolumeSize_Should_failed() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(directAdmin, directPassword);

    // spogServer.userLogin(userName, password);
    String spogToken = spogServer.getJWTToken();
    String jobId = generateJob();


    Response response = updateJobData(jobId, token, false, "120000", "1200000", "12000", "120000");
    response.then().statusCode(ServerResponseCode.Success_Put);
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
