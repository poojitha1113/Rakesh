package test.restapi.site;

import static invoker.SiteTestHelper.composeRandomOrganizationMap;
import static invoker.SiteTestHelper.composeRandomUserMap;
import static invoker.SiteTestHelper.createOrgnaization;
import static invoker.SiteTestHelper.createSite;
import static invoker.SiteTestHelper.createSubOrgnaization;
import static invoker.SiteTestHelper.createUser;
import static invoker.SiteTestHelper.getRandomOrganizationName;
import static invoker.SiteTestHelper.getRandomSiteName;
import static invoker.SiteTestHelper.getTestPassword;
import static invoker.SiteTestHelper.loginSpogServer;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.testng.Assert.assertTrue;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import Constants.SpogConstants;
import InvokerServer.ServerResponseCode;
import genericutil.ExtentManager;
import invoker.SiteTestHelper.siteType;
import io.restassured.response.Response;

public class TestCreateSite extends base.prepare.TestApiBase {
  private String token;

  // private ExtentReports extent;
  // private ExtentTest test;
  // private UserSpogServer userSpogServer;
  // private SQLServerDb bqdb1;
  // public int Nooftest;
  // private long creationTime;
  // private String BQName = null;
  // private String runningMachine;
  // private testcasescount count1;
  // private String buildVersion;
  private String TestDataPrefix = getClass().getSimpleName();
  private String NamePrefix     = TestDataPrefix;



  public void setAuthor() {

    author = "yin.li";
  }


  @BeforeClass
  public void prepareJobData() {

    this.token = spogServer.getJWTToken();
  }

  // @BeforeClass
  // @Parameters({"baseURI", "port", "csrAdminUserName", "csrAdminPassword", "logFolder",
  // "buildVersion"})
  // public void setSpogServerConnection(String baseUri, String port, String username, String
  // password,
  // String logFolder, String buildVersion) throws UnknownHostException {
  //
  // configSpogServerConnection(baseUri, port);
  // token = loginSpogServer(username, password);
  //
  // extent = ExtentManager.getInstance(this.getClass().getSimpleName(), logFolder);
  // userSpogServer = new UserSpogServer(baseUri, port);
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


  @Test
  public void Given_CsrAdmin_when_HaveOrgnizationId_Should_CreateSite() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor(author);

    String csrToken = this.token;

    // create a org
    String orgName = getRandomOrganizationName(NamePrefix);

    String orgId = createOrgnaization(orgName, SpogConstants.MSP_ORG,
        RandomStringUtils.randomAlphanumeric(8) + "@SpogQa",
        RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(4),
        RandomStringUtils.randomAlphanumeric(4), csrToken);


    // create a site for org
    String siteName = getRandomSiteName("TestCreateSite");
    Response response = createSite(siteName, siteType.cloud_direct.toString(), orgId, csrToken);

    response.then().log().all();
    response.then().statusCode(ServerResponseCode.Success_Post);

  }


  @Test
  public void Given_CsrAdmin_when_NoOrgnizationId_Should_CreateSite() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor(author);
    String csrToken = this.token;

    // create a org
    String orgName = getRandomOrganizationName("TestCreateSite");

    String orgId = createOrgnaization(orgName, SpogConstants.MSP_ORG,
        RandomStringUtils.randomAlphanumeric(8) + "@SpogQa",
        RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(4),
        RandomStringUtils.randomAlphanumeric(4), csrToken);


    // create a site for org
    String siteName = getRandomSiteName("TestCreateSite");
    Response response = createSite(siteName, siteType.cloud_direct.toString(), "", csrToken);

    response.then().statusCode(ServerResponseCode.Success_Post);
  }


  @Test
  public void Given_MspAdmin_when_HaveOrgnizationId_Should_CreateSite() {

    String csrToken = this.token;
    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor(author);
    // create a org
    String orgName = getRandomOrganizationName("TestCreateSite");

    String orgId = createOrgnaization(orgName, SpogConstants.MSP_ORG,
        RandomStringUtils.randomAlphanumeric(8) + "@SpogQa",
        RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(4),
        RandomStringUtils.randomAlphanumeric(4), csrToken);

    // create msp admin
    Map<String, String> userInfoMap =
        composeRandomUserMap(NamePrefix, SpogConstants.MSP_ADMIN, orgId);
    createUser(userInfoMap, csrToken);
    String mspAdmin = userInfoMap.get("email");
    String mspPassword = getTestPassword();

    // login as msp admin
    String mspToken = loginSpogServer(mspAdmin, mspPassword);

    // create a site for org as msp admin
    String siteName = getRandomSiteName("TestCreateSite");
    Response response = createSite(siteName, siteType.cloud_direct.toString(), orgId, mspToken);

    response.then().log().all();
    response.then().statusCode(ServerResponseCode.Success_Post);
  }


  @Test
  public void Given_MspAdmin_when_NoOrgnizationId_Should_CreateSite() {

    String csrToken = this.token;
    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor(author);
    // create a org
    String orgName = getRandomOrganizationName("TestCreateSite");

    String orgId = createOrgnaization(orgName, SpogConstants.MSP_ORG,
        RandomStringUtils.randomAlphanumeric(8) + "@SpogQa",
        RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(4),
        RandomStringUtils.randomAlphanumeric(4), csrToken);

    // create msp admin
    Map<String, String> userInfoMap =
        composeRandomUserMap(NamePrefix, SpogConstants.MSP_ADMIN, orgId);
    createUser(userInfoMap, csrToken);
    String mspAdmin = userInfoMap.get("email");
    String mspPassword = getTestPassword();

    // login as msp admin
    String mspToken = loginSpogServer(mspAdmin, mspPassword);

    // create a site for org as msp admin
    String siteName = getRandomSiteName("TestCreateSite");
    Response response = createSite(siteName, siteType.cloud_direct.toString(), "", mspToken);

    response.then().log().all();
    response.then().statusCode(ServerResponseCode.Success_Post);
  }


  @Test
  public void Given_MspAdmin_when_CreateForOtherOrg_Should_Failed() {

    String csrToken = this.token;
    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor(author);
    // create a org
    String orgName = getRandomOrganizationName("TestCreateSite");

    String orgIdA = createOrgnaization(orgName, SpogConstants.MSP_ORG,
        RandomStringUtils.randomAlphanumeric(8) + "@SpogQa",
        RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(4),
        RandomStringUtils.randomAlphanumeric(4), csrToken);

    String orgIdB = createOrgnaization(orgName, SpogConstants.MSP_ORG,
        RandomStringUtils.randomAlphanumeric(8) + "@SpogQa",
        RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(4),
        RandomStringUtils.randomAlphanumeric(4), csrToken);

    // create msp admin
    Map<String, String> userInfoMap =
        composeRandomUserMap(NamePrefix, SpogConstants.MSP_ADMIN, orgIdA);
    createUser(userInfoMap, csrToken);
    String mspAdmin = userInfoMap.get("email");
    String mspPassword = getTestPassword();

    // login as msp admin
    String mspToken = loginSpogServer(mspAdmin, mspPassword);

    // create a site for org as msp admin
    String siteName = getRandomSiteName("TestCreateSite");
    Response response = createSite(siteName, siteType.cloud_direct.toString(), orgIdB, mspToken);

    response.then().log().all();
    response.then().statusCode(ServerResponseCode.Insufficient_Permissions);
    response.then().assertThat().body("errors.code.size()", is(1)).body("errors.code",
        hasItem("00100101"));
  }


  @Test
  public void Given_DirectAdmin_when_CreateSiteAndProvideOrgID_Should_Successful() {

    String csrToken = this.token;
    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor(author);
    // create a org
    String orgName = getRandomOrganizationName("TestCreateSite");

    String orgId = createOrgnaization(orgName, SpogConstants.MSP_ORG,
        RandomStringUtils.randomAlphanumeric(8) + "@SpogQa",
        RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(4),
        RandomStringUtils.randomAlphanumeric(4), csrToken);

    // create msp admin
    Map<String, String> userInfoMap =
        composeRandomUserMap(NamePrefix, SpogConstants.MSP_ADMIN, orgId);
    createUser(userInfoMap, csrToken);
    String mspAdmin = userInfoMap.get("email");
    String mspPassword = getTestPassword();

    // login as msp admin
    String mspToken = loginSpogServer(mspAdmin, mspPassword);

    // CreateDirectAdmin
    Map<String, String> directAdminInfoMap =
        composeRandomUserMap(NamePrefix, SpogConstants.MSP_ADMIN, orgId);
    createUser(directAdminInfoMap, mspToken);
    String directAdmin = userInfoMap.get("email");
    String directPassword = getTestPassword();

    String directAdminToken = loginSpogServer(directAdmin, directPassword);
    Response response = createSite(getRandomSiteName(NamePrefix), siteType.cloud_direct.toString(),
        orgId, directAdminToken);

    response.then().log().all();
    response.then().statusCode(ServerResponseCode.Success_Post);
  }


  @Test
  public void Given_DirectAdmin_when_CreateSiteAndNoOrgID_Should_Successful() {

    String csrToken = this.token;
    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor(author);
    // create a org
    String orgName = getRandomOrganizationName("TestCreateSite");

    String orgId = createOrgnaization(orgName, SpogConstants.MSP_ORG,
        RandomStringUtils.randomAlphanumeric(8) + "@SpogQa",
        RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(4),
        RandomStringUtils.randomAlphanumeric(4), csrToken);

    // create msp admin
    Map<String, String> userInfoMap =
        composeRandomUserMap(NamePrefix, SpogConstants.MSP_ADMIN, orgId);
    createUser(userInfoMap, csrToken);
    String mspAdmin = userInfoMap.get("email");
    String mspPassword = getTestPassword();

    // login as msp admin
    String mspToken = loginSpogServer(mspAdmin, mspPassword);

    // CreateDirectAdmin
    Map<String, String> directAdminInfoMap =
        composeRandomUserMap(NamePrefix, SpogConstants.MSP_ADMIN, orgId);
    createUser(directAdminInfoMap, mspToken);
    String directAdmin = userInfoMap.get("email");
    String directPassword = getTestPassword();

    String directAdminToken = loginSpogServer(directAdmin, directPassword);
    Response response = createSite(getRandomSiteName(NamePrefix), siteType.cloud_direct.toString(),
        "", directAdminToken);

    response.then().log().all();
    response.then().statusCode(ServerResponseCode.Success_Post);
  }


  @Test
  public void Given_DirectAdmin_when_CreateSiteForOtherOrg_Should_Failed() {

    String csrToken = this.token;
    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor(author);
    // create a org
    String orgName = getRandomOrganizationName("TestCreateSite");

    String orgId = createOrgnaization(orgName, SpogConstants.MSP_ORG,
        RandomStringUtils.randomAlphanumeric(8) + "@SpogQa",
        RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(4),
        RandomStringUtils.randomAlphanumeric(4), csrToken);

    String orgIdB = createOrgnaization(orgName, SpogConstants.MSP_ORG,
        RandomStringUtils.randomAlphanumeric(8) + "@SpogQa",
        RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(4),
        RandomStringUtils.randomAlphanumeric(4), csrToken);

    // create msp admin
    Map<String, String> userInfoMap =
        composeRandomUserMap(NamePrefix, SpogConstants.MSP_ADMIN, orgId);
    createUser(userInfoMap, csrToken);
    String mspAdmin = userInfoMap.get("email");
    String mspPassword = getTestPassword();

    // login as msp admin
    String mspToken = loginSpogServer(mspAdmin, mspPassword);

    // CreateDirectAdmin
    Map<String, String> directAdminInfoMap =
        composeRandomUserMap(NamePrefix, SpogConstants.MSP_ADMIN, orgId);
    createUser(directAdminInfoMap, mspToken);
    String directAdmin = userInfoMap.get("email");
    String directPassword = getTestPassword();

    String directAdminToken = loginSpogServer(directAdmin, directPassword);
    Response response = createSite(getRandomSiteName(NamePrefix), siteType.cloud_direct.toString(),
        orgIdB, directAdminToken);

    response.then().log().all();
    response.then().statusCode(ServerResponseCode.Insufficient_Permissions);
    response.then().assertThat().body("errors.code.size()", is(1)).body("errors.code",
        hasItem("00100101"));
  }


  @Test
  public void Given_MspAdmin_when_NotLogin_Should_Failed() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor(author);
    String csrToken = this.token;

    Map<String, Object> organizationMap =
        composeRandomOrganizationMap(NamePrefix, SpogConstants.MSP_ORG);

    String orgName = (String) organizationMap.get("organization_name");

    String orgId = createOrgnaization(organizationMap, csrToken);

    // create msp admin
    Map<String, String> userInfoMap =
        composeRandomUserMap(NamePrefix, SpogConstants.MSP_ADMIN, orgId);
    createUser(userInfoMap, csrToken);

    // login as msp admin
    String mspToken = "";

    // create a site for org as msp admin
    String siteName = getRandomSiteName("TestCreateSite");
    Response response = createSite(siteName, siteType.cloud_direct.toString(), orgId, mspToken);

    response.then().log().all();
    response.then().statusCode(ServerResponseCode.Not_login);
    response.then().assertThat().body("errors.code.size()", is(1)).body("errors.code",
        hasItem("00900006"));
  }


  @Test
  public void Given_Site_Securet_When_Create_Site_Should_Be_Empty() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor(author);
    String csrToken = this.token;

    // create a org
    String orgName = getRandomOrganizationName("TestCreateSite");

    String orgId = createOrgnaization(orgName, SpogConstants.MSP_ORG,
        RandomStringUtils.randomAlphanumeric(8) + "@SpogQa",
        RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(4),
        RandomStringUtils.randomAlphanumeric(4), csrToken);


    // create a site for org
    String siteName = getRandomSiteName("TestCreateSite");
    Response response = createSite(siteName, siteType.cloud_direct.toString(), "", csrToken);

    response.then().statusCode(ServerResponseCode.Success_Post);

    String site_secret = response.then().extract().path("data.secret");

    assertTrue(StringUtils.isBlank(site_secret));

  }


  @Test
  public void Given_CsrAdmin_when_InvalidSiteType_Should_Failed() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor(author);

    String csrToken = this.token;

    // create a org
    String orgName = getRandomOrganizationName(NamePrefix);

    String orgId = createOrgnaization(orgName, SpogConstants.MSP_ORG,
        RandomStringUtils.randomAlphanumeric(8) + "@SpogQa",
        RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(4),
        RandomStringUtils.randomAlphanumeric(4), csrToken);


    // create a site for org
    String siteName = getRandomSiteName("TestCreateSite");
    Response response = createSite(siteName, "InvalidSiteType", orgId, csrToken);

    response.then().log().body();
    response.then().statusCode(ServerResponseCode.Bad_Request);
    response.then().assertThat().body("errors.code.size()", is(1)).body("errors.code",
        hasItem("40000006"));
  }


  @Test
  public void Given_CsrAdmin_when_Not_Exist_Org_Id_Should_Failed() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor(author);

    String csrToken = this.token;

    String notExistOrgId = "008d283f-9999-9999-acb3-7641e895eede";


    // create a site for org
    String siteName = getRandomSiteName("TestCreateSite");
    Response response =
        createSite(siteName, siteType.cloud_direct.toString(), notExistOrgId, csrToken);

    response.then().log().body();
    response.then().statusCode(ServerResponseCode.Resource_Does_Not_Exist);
    response.then().assertThat().body("errors.code.size()", is(1)).body("errors.code",
        hasItem("0030000A"));

  }


  @Test
  public void Given_CsrAdmin_when_Invalid_Org_Id_Should_Failed() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor(author);

    String csrToken = this.token;

    // create a site for org
    String siteName = getRandomSiteName("TestCreateSite");
    Response response =
        createSite(siteName, siteType.cloud_direct.toString(), "invalidOrgId", csrToken);

    response.then().log().body();
    response.then().statusCode(ServerResponseCode.Bad_Request);
    response.then().assertThat().body("errors.code.size()", is(1)).body("errors.code",
        hasItem("40000005"));

  }


  @Test
  public void Given_AccountManager_when_CreateSiteForitsOrg_Should_Successful() {

    String csrToken = this.token;
    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor(author);
    // create a org
    String orgName = getRandomOrganizationName("TestCreateSite");

    String orgId = createOrgnaization(orgName, SpogConstants.MSP_ORG,
        RandomStringUtils.randomAlphanumeric(8) + "@SpogQa",
        RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(4),
        RandomStringUtils.randomAlphanumeric(4), csrToken);

    // create msp admin
    Map<String, String> userInfoMap =
        composeRandomUserMap(NamePrefix, SpogConstants.MSP_ADMIN, orgId);
    createUser(userInfoMap, csrToken);
    String mspAdmin = userInfoMap.get("email");
    String mspPassword = getTestPassword();

    // login as msp admin
    String mspToken = loginSpogServer(mspAdmin, mspPassword);

    String accountOrgAId =
        createSubOrgnaization(getRandomOrganizationName(TestDataPrefix), orgId, mspToken);

    userInfoMap = composeRandomUserMap(TestDataPrefix, SpogConstants.MSP_ACCOUNT_ADMIN, orgId);
    String accountManagerId = createUser(userInfoMap, mspToken);


    userSpogServer.setToken(mspToken);
    userSpogServer.assignMspAccountAdmins(orgId, accountOrgAId, accountManagerId, mspToken);

    String accountManagerToken = loginSpogServer(userInfoMap.get("email"), getTestPassword());

    // create a site for org as msp admin
    String siteName = getRandomSiteName("TestCreateSite");
    Response response =
        createSite(siteName, siteType.cloud_direct.toString(), accountOrgAId, accountManagerToken);

    response.then().log().all();
    response.then().statusCode(ServerResponseCode.Success_Post);
  }


  @Test
  public void Given_AccountManager_when_CreateSiteForOtherOrg_Should_Failed() {

    String csrToken = this.token;
    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor(author);
    // create a org
    String orgName = getRandomOrganizationName("TestCreateSite");

    String orgId = createOrgnaization(orgName, SpogConstants.MSP_ORG,
        RandomStringUtils.randomAlphanumeric(8) + "@SpogQa",
        RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(4),
        RandomStringUtils.randomAlphanumeric(4), csrToken);

    // create msp admin
    Map<String, String> userInfoMap =
        composeRandomUserMap(NamePrefix, SpogConstants.MSP_ADMIN, orgId);
    createUser(userInfoMap, csrToken);
    String mspAdmin = userInfoMap.get("email");
    String mspPassword = getTestPassword();

    // login as msp admin
    String mspToken = loginSpogServer(mspAdmin, mspPassword);

    String accountOrgAId =
        createSubOrgnaization(getRandomOrganizationName(TestDataPrefix), orgId, mspToken);

    String accountOrgAIdB =
        createSubOrgnaization(getRandomOrganizationName(TestDataPrefix), orgId, mspToken);

    userInfoMap = composeRandomUserMap(TestDataPrefix, SpogConstants.MSP_ACCOUNT_ADMIN, orgId);
    String accountManagerAId = createUser(userInfoMap, mspToken);


    userSpogServer.setToken(mspToken);
    userSpogServer.assignMspAccountAdmins(orgId, accountOrgAId, accountManagerAId, mspToken);

    String accountManagerToken = loginSpogServer(userInfoMap.get("email"), getTestPassword());

    // create a site for org as msp admin
    String siteName = getRandomSiteName("TestCreateSite");
    Response response =
        createSite(siteName, siteType.cloud_direct.toString(), accountOrgAIdB, accountManagerToken);

    response.then().log().all();
    response.then().statusCode(ServerResponseCode.Insufficient_Permissions);
  }


  @Test
  public void Given_Csr_ReadOnly_User_When_CreateSite_Should_Failed() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(csrAdminUserName, csrAdminPassword);
    token = spogServer.getJWTToken();
    String csrToken = token;
    
    String password = getTestPassword();
    String csrOrgId = spogServer.GetLoggedinUserOrganizationID(); 
    
    Map<String, String> userInfoMap = composeRandomUserMap(TestDataPrefix, SpogConstants.CSR_READ_ONLY_ADMIN, csrOrgId);
    String csrReadOnlyUser = userInfoMap.get("email");
    
    String csrReadOnlyUserId = spogServer.createUserAndCheck(csrReadOnlyUser, password, userInfoMap.get("first_name"), 
    		userInfoMap.get("last_name"), SpogConstants.CSR_READ_ONLY_ADMIN, csrOrgId, test);
    
    spogServer.userLogin(csrReadOnlyUser, password);
    
    String siteName = getRandomSiteName("TestCreateSite");
    Response response = createSite(siteName, siteType.cloud_direct.toString(), csrOrgId, spogServer.getJWTToken());

    response.then().log().all();
    response.then().statusCode(ServerResponseCode.Insufficient_Permissions);
    
    spogServer.userLogin(csrAdminUserName, csrAdminPassword);
    spogServer.CheckDeleteUserByIdStatus(csrReadOnlyUserId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
  }	
  
  // @AfterMethod
  // public void getResult(ITestResult result) {
  // if (result.getStatus() == ITestResult.FAILURE) {
  // test.log(LogStatus.FAIL, "Test Case Failed is " + result.getName());
  // test.log(LogStatus.FAIL, "Test Case Failed is " + result.getThrowable());
  // } else if (result.getStatus() == ITestResult.SKIP) {
  // test.log(LogStatus.SKIP, "Test Case Skipped is " + result.getName());
  // } else if (result.getStatus() == ITestResult.SUCCESS) {
  // test.log(LogStatus.PASS, "Test Case Passed is " + result.getName());
  // }
  // // ending test
  // // endTest(test) : It ends the current test and prepares to create HTML report
  // extent.endTest(test);
  // }
  //
  // @AfterTest
  // public void endReport() {
  // // writing everything to document
  // // flush() - to write or update test information to your report.
  // extent.flush();
  // // Call close() at the very end of your session to clear all resources.
  // // If any of your test ended abruptly causing any side-affects (not all logs sent to
  // // ExtentReports, information missing), this method will ensure that the test is still appended
  // // to the report with a warning message.
  // // You should call close() only once, at the very end (in @AfterSuite for example) as it closes
  // // the underlying stream.
  // // Once this method is called, calling any Extent method will throw an error.
  // // close() - To close all the operation
  // extent.close();
  // }

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
  // // endTest(test) : It ends the current test and prepares to create HTML report
  // extent.endTest(test);
  // }
  //
  //
  // @AfterTest
  // public void aftertest() {
  //
  // test.log(LogStatus.INFO, "The total test cases passed are " + count1.getpassedcount());
  // test.log(LogStatus.INFO, "the total test cases failed are " + count1.getfailedcount());
  // extent.flush();
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
