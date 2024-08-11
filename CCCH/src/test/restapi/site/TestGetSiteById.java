package test.restapi.site;

import static invoker.SiteTestHelper.composeRandomUserMap;
import static invoker.SiteTestHelper.createOrgnaization;
import static invoker.SiteTestHelper.createSite;
import static invoker.SiteTestHelper.createSubOrgnaization;
import static invoker.SiteTestHelper.createUser;
import static invoker.SiteTestHelper.getRandomOrganizationName;
import static invoker.SiteTestHelper.getRandomSiteName;
import static invoker.SiteTestHelper.getSite;
import static invoker.SiteTestHelper.getTestPassword;
import static invoker.SiteTestHelper.loginSpogServer;
import static org.hamcrest.Matchers.equalTo;
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

public class TestGetSiteById extends base.prepare.TestApiBase {
  private String        token;
  private static String TestPrefix = "TestGetSiteById";
  // private ExtentReports extent;
  // private ExtentTest logger;
  //
  // private SQLServerDb bqdb1;
  // public int Nooftest;
  // private long creationTime;
  // private String BQName = null;
  // private String runningMachine;
  // private testcasescount count1;
  // private String buildVersion;
  // private UserSpogServer userSpogServer;


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
  // userSpogServer = new UserSpogServer(baseUri, port);
  //
  // extent = ExtentManager.getInstance(this.getClass().getSimpleName(), logFolder);
  //
  // // this is for update portal
  // this.BQName = this.getClass().getSimpleName();
  // String author = "yin.li";
  // this.runningMachine = java.net.InetAddress.getLocalHost().getHostName();
  // SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
  // java.util.Date date = new java.util.Date();
  // this.buildVersion = buildVersion + "_" + dateFormater.format(date);
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


  @BeforeClass
  public void prepareJobData() {

    this.token = spogServer.getJWTToken();
  }


  @Test
  public void Given_CsrAdmin_when_GetSite_Should_Successful() {

    String csrToken = this.token;

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor(author);
    // create a org
    String orgName = getRandomOrganizationName(TestPrefix);

    String orgId = createOrgnaization(orgName, SpogConstants.MSP_ORG,
        RandomStringUtils.randomAlphanumeric(8) + "@SpogQa",
        RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(4),
        RandomStringUtils.randomAlphanumeric(4), csrToken);


    // create a site for org
    String siteName = getRandomSiteName(TestPrefix);
    Response response = createSite(siteName, siteType.cloud_direct.toString(), orgId, csrToken);
    response.then().statusCode(ServerResponseCode.Success_Post);

    String siteId = response.then().extract().path("data.site_id");

    // delete site of org
    response = getSite(siteId, csrToken);
    response.then().log().all();

    response.then().statusCode(ServerResponseCode.Success_Get);
    response.then().assertThat().body("data.site_id", equalTo(siteId));
    response.then().assertThat().body("data.site_name", equalTo(siteName));
    response.then().assertThat().body("data.site_type", equalTo(siteType.cloud_direct.toString()));
    response.then().assertThat().body("data.organization_id", equalTo(orgId));
  }


  @Test
  public void Given_Csr_ReadOnly_User_When_GetSiteById_Should_Success() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(csrAdminUserName, csrAdminPassword);
    token = spogServer.getJWTToken();
    String csrToken = token;
    
    String csrOrgId = spogServer.GetLoggedinUserOrganizationID();
    String password = getTestPassword();
    
    Map<String, String> userInfoMap = composeRandomUserMap(TestPrefix, SpogConstants.CSR_READ_ONLY_ADMIN, csrOrgId);
    String csrReadOnlyUser = userInfoMap.get("email");
    
    String csrReadOnlyUserId = spogServer.createUserAndCheck(csrReadOnlyUser, password, userInfoMap.get("first_name"), 
    		userInfoMap.get("last_name"), SpogConstants.CSR_READ_ONLY_ADMIN, csrOrgId, test);
    
    String orgName = getRandomOrganizationName(TestPrefix);

    String orgId = createOrgnaization(orgName, SpogConstants.MSP_ORG,
        RandomStringUtils.randomAlphanumeric(8) + "@SpogQa",
        RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(4),
        RandomStringUtils.randomAlphanumeric(4), csrToken);

    // create msp admin
    userInfoMap =
        composeRandomUserMap(TestPrefix, SpogConstants.MSP_ADMIN, orgId);
    createUser(userInfoMap, csrToken);
    String mspAdmin = userInfoMap.get("email");
    String mspPassword = getTestPassword();

    // login as msp admin
    String mspToken = loginSpogServer(mspAdmin, mspPassword);

    // create a site for org as msp admin
    String siteName = getRandomSiteName(TestPrefix);
    Response response = createSite(siteName, siteType.cloud_direct.toString(), "", mspToken);

    response.then().statusCode(ServerResponseCode.Success_Post);

    String siteId = response.then().extract().path("data.site_id");
    
    
    spogServer.userLogin(csrReadOnlyUser, password);
	
    response = getSite(siteId, mspToken);
    response.then().log().all();

    response.then().statusCode(ServerResponseCode.Success_Get);
    
    spogServer.userLogin(csrAdminUserName, csrAdminPassword);
    spogServer.CheckDeleteUserByIdStatus(csrReadOnlyUserId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
  }	

  @Test
  public void Given_MspAdmin_when_GetItsSite_Should_Successful() {

    String csrToken = this.token;
    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor(author);
    // create a org
    String orgName = getRandomOrganizationName(TestPrefix);

    String orgId = createOrgnaization(orgName, SpogConstants.MSP_ORG,
        RandomStringUtils.randomAlphanumeric(8) + "@SpogQa",
        RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(4),
        RandomStringUtils.randomAlphanumeric(4), csrToken);

    // create msp admin
    Map<String, String> userInfoMap =
        composeRandomUserMap(TestPrefix, SpogConstants.MSP_ADMIN, orgId);
    createUser(userInfoMap, csrToken);
    String mspAdmin = userInfoMap.get("email");
    String mspPassword = getTestPassword();

    // login as msp admin
    String mspToken = loginSpogServer(mspAdmin, mspPassword);

    // create a site for org as msp admin
    String siteName = getRandomSiteName(TestPrefix);
    Response response = createSite(siteName, siteType.cloud_direct.toString(), "", mspToken);

    response.then().statusCode(ServerResponseCode.Success_Post);

    String siteId = response.then().extract().path("data.site_id");

    response = getSite(siteId, mspToken);
    response.then().log().all();

    response.then().statusCode(ServerResponseCode.Success_Get);
    response.then().assertThat().body("data.site_id", equalTo(siteId));
    response.then().assertThat().body("data.site_name", equalTo(siteName));
    response.then().assertThat().body("data.site_type", equalTo(siteType.cloud_direct.toString()));
    response.then().assertThat().body("data.organization_id", equalTo(orgId));
  }


  @Test
  public void Given_MspAdmin_when_GetSiteInOtherOrg_Should_Failed() {

    String csrToken = this.token;
    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor(author);
    // create a org
    String orgName = getRandomOrganizationName(TestPrefix);

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
        composeRandomUserMap(TestPrefix, SpogConstants.MSP_ADMIN, orgIdA);
    createUser(userInfoMap, csrToken);
    String mspAAdmin = userInfoMap.get("email");
    String mspAPassword = getTestPassword();

    // login as msp admin
    String mspAToken = loginSpogServer(mspAAdmin, mspAPassword);

    // create a site for org as msp admin
    String siteName = getRandomSiteName(TestPrefix);
    Response response = createSite(siteName, siteType.cloud_direct.toString(), orgIdB, csrToken);

    response.then().statusCode(ServerResponseCode.Success_Post);

    String siteId = response.then().extract().path("data.site_id");


    response = getSite(siteId, mspAToken);
    response.then().log().all();

    response.then().statusCode(ServerResponseCode.Insufficient_Permissions);

  }


  @Test
  public void Given_DirectAdmin_when_GetSiteInItsOrg_Should_Successful() {

    String csrToken = this.token;
    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor(author);
    // create a org
    String orgName = getRandomOrganizationName(TestPrefix);

    String orgId = createOrgnaization(orgName, SpogConstants.MSP_ORG,
        RandomStringUtils.randomAlphanumeric(8) + "@SpogQa",
        RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(4),
        RandomStringUtils.randomAlphanumeric(4), csrToken);

    // create msp admin
    Map<String, String> userInfoMap =
        composeRandomUserMap(TestPrefix, SpogConstants.MSP_ADMIN, orgId);
    createUser(userInfoMap, csrToken);
    String mspAdmin = userInfoMap.get("email");
    String mspPassword = getTestPassword();

    // login as msp admin
    String mspToken = loginSpogServer(mspAdmin, mspPassword);

    String subOrgId_A = createOrgnaization(orgName, SpogConstants.DIRECT_ORG,
        RandomStringUtils.randomAlphanumeric(8) + "@SpogQa",
        RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(4),
        RandomStringUtils.randomAlphanumeric(4), csrToken);

    String subOrgId_B = createOrgnaization(orgName, SpogConstants.DIRECT_ORG,
        RandomStringUtils.randomAlphanumeric(8) + "@SpogQa",
        RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(4),
        RandomStringUtils.randomAlphanumeric(4), csrToken);


    userInfoMap = composeRandomUserMap(TestPrefix, SpogConstants.DIRECT_ADMIN, subOrgId_A);
    createUser(userInfoMap, csrToken);
    String directAdmin_A = userInfoMap.get("email");
    String directPassword_A = getTestPassword();
    String directToken_A = loginSpogServer(directAdmin_A, directPassword_A);

    // create a site for org as msp admin
    String siteName_A = getRandomSiteName(TestPrefix);
    Response response = createSite(siteName_A, siteType.cloud_direct.toString(), "", directToken_A);

    response.then().statusCode(ServerResponseCode.Success_Post);

    String siteId_A = response.then().extract().path("data.site_id");

    userInfoMap = composeRandomUserMap(TestPrefix, SpogConstants.DIRECT_ADMIN, subOrgId_B);
    createUser(userInfoMap, csrToken);
    String directAdmin_B = userInfoMap.get("email");
    String directPassword_B = getTestPassword();

    String directToken_B = loginSpogServer(directAdmin_B, directPassword_B);

    response = getSite(siteId_A, directToken_A);

    response.then().statusCode(ServerResponseCode.Success_Get);
    response.then().assertThat().body("data.site_id", equalTo(siteId_A));
    response.then().assertThat().body("data.site_name", equalTo(siteName_A));
    response.then().assertThat().body("data.site_type", equalTo(siteType.cloud_direct.toString()));
    response.then().assertThat().body("data.organization_id", equalTo(subOrgId_A));
  }


  @Test
  public void Given_DirectAdmin_when_GetSiteInOtherOrg_Should_Failed() {

    String csrToken = this.token;
    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor(author);
    // create a org
    String orgName = getRandomOrganizationName(TestPrefix);

    String orgId = createOrgnaization(orgName, SpogConstants.MSP_ORG,
        RandomStringUtils.randomAlphanumeric(8) + "@SpogQa",
        RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(4),
        RandomStringUtils.randomAlphanumeric(4), csrToken);

    // create msp admin
    Map<String, String> userInfoMap =
        composeRandomUserMap(TestPrefix, SpogConstants.MSP_ADMIN, orgId);
    createUser(userInfoMap, csrToken);
    String mspAdmin = userInfoMap.get("email");
    String mspPassword = getTestPassword();

    // login as msp admin
    String mspToken = loginSpogServer(mspAdmin, mspPassword);

    String subOrgId_A = createOrgnaization(orgName, SpogConstants.DIRECT_ORG,
        RandomStringUtils.randomAlphanumeric(8) + "@SpogQa",
        RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(4),
        RandomStringUtils.randomAlphanumeric(4), csrToken);

    String subOrgId_B = createOrgnaization(orgName, SpogConstants.DIRECT_ORG,
        RandomStringUtils.randomAlphanumeric(8) + "@SpogQa",
        RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(4),
        RandomStringUtils.randomAlphanumeric(4), csrToken);


    userInfoMap = composeRandomUserMap(TestPrefix, SpogConstants.DIRECT_ADMIN, subOrgId_A);
    createUser(userInfoMap, csrToken);
    String directAdmin_A = userInfoMap.get("email");
    String directPassword_A = getTestPassword();

    String directToken_A = loginSpogServer(directAdmin_A, directPassword_A);

    // create a site for org as msp admin
    String siteName_A = getRandomSiteName(TestPrefix);
    Response response = createSite(siteName_A, siteType.cloud_direct.toString(), "", directToken_A);

    response.then().statusCode(ServerResponseCode.Success_Post);

    String siteId_A = response.then().extract().path("data.site_id");

    userInfoMap = composeRandomUserMap(TestPrefix, SpogConstants.DIRECT_ADMIN, subOrgId_B);
    createUser(userInfoMap, csrToken);
    String directAdmin_B = userInfoMap.get("email");
    String directPassword_B = getTestPassword();

    String directToken_B = loginSpogServer(directAdmin_B, directPassword_B);

    response = getSite(siteId_A, directToken_B);

    response.then().statusCode(ServerResponseCode.Insufficient_Permissions);
  }


  @Test
  public void Given_Site_Securet_When_Get_Site_Should_Be_Empty() {

    String csrToken = this.token;
    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor(author);
    // create a org
    String orgName = getRandomOrganizationName(TestPrefix);

    String orgId = createOrgnaization(orgName, SpogConstants.MSP_ORG,
        RandomStringUtils.randomAlphanumeric(8) + "@SpogQa",
        RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(4),
        RandomStringUtils.randomAlphanumeric(4), csrToken);


    // create a site for org
    String siteName = getRandomSiteName(TestPrefix);
    Response response = createSite(siteName, siteType.cloud_direct.toString(), orgId, csrToken);
    response.then().statusCode(ServerResponseCode.Success_Post);

    String siteId = response.then().extract().path("data.site_id");

    // delete site of org
    response = getSite(siteId, csrToken);
    response.then().log().all();

    response.then().statusCode(ServerResponseCode.Success_Get);

    String site_secret = response.then().extract().path("data.secret");

    assertTrue(StringUtils.isBlank(site_secret));

  }


  @Test
  public void Given_CsrAdmin_when_SiteIdNotExist_Should_Failed() {

    String csrToken = this.token;
    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor(author);
    String siteId = "fb475eab-9999-9999-9999-3b0009408dc3";

    Response response = getSite(siteId, csrToken);
    response.then().log().all();
    response.then().statusCode(ServerResponseCode.Resource_Does_Not_Exist);

    siteId = "invalid format";

    response = getSite(siteId, csrToken);
    response.then().log().all();
    response.then().statusCode(ServerResponseCode.Insufficient_Permissions);
  }


  @Test
  public void Given_AccountManager_when_GetSiteForitsOrg_Should_Successful() {

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
        composeRandomUserMap(TestPrefix, SpogConstants.MSP_ADMIN, orgId);
    createUser(userInfoMap, csrToken);
    String mspAdmin = userInfoMap.get("email");
    String mspPassword = getTestPassword();

    // login as msp admin
    String mspToken = loginSpogServer(mspAdmin, mspPassword);

    String accountOrgAId =
        createSubOrgnaization(getRandomOrganizationName(TestPrefix), orgId, mspToken);

    userInfoMap = composeRandomUserMap(TestPrefix, SpogConstants.MSP_ACCOUNT_ADMIN, orgId);
    String accountManagerId = createUser(userInfoMap, mspToken);


    userSpogServer.setToken(mspToken);
    userSpogServer.assignMspAccountAdmins(orgId, accountOrgAId, accountManagerId, mspToken);

    String accountManagerToken = loginSpogServer(userInfoMap.get("email"), getTestPassword());

    // create a site for org as msp admin
    String siteName = getRandomSiteName("TestCreateSite");
    Response response =
        createSite(siteName, siteType.cloud_direct.toString(), accountOrgAId, accountManagerToken);

    response.then().statusCode(ServerResponseCode.Success_Post);

    String siteId = response.then().extract().path("data.site_id");

    response = getSite(siteId, accountManagerToken);

    response.then().statusCode(ServerResponseCode.Success_Get);

  }


  @Test
  public void Given_AccountManager_when_GetSiteForOtherOrg_Should_Failed() {

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
        composeRandomUserMap(TestPrefix, SpogConstants.MSP_ADMIN, orgId);
    createUser(userInfoMap, csrToken);
    String mspAdmin = userInfoMap.get("email");
    String mspPassword = getTestPassword();

    // login as msp admin
    String mspToken = loginSpogServer(mspAdmin, mspPassword);

    String accountOrgAId =
        createSubOrgnaization(getRandomOrganizationName(TestPrefix), orgId, mspToken);

    String accountOrgAIdB =
        createSubOrgnaization(getRandomOrganizationName(TestPrefix), orgId, mspToken);

    userInfoMap = composeRandomUserMap(TestPrefix, SpogConstants.MSP_ACCOUNT_ADMIN, orgId);
    String accountManagerAId = createUser(userInfoMap, mspToken);


    userSpogServer.setToken(mspToken);
    userSpogServer.assignMspAccountAdmins(orgId, accountOrgAId, accountManagerAId, mspToken);

    String accountManagerToken = loginSpogServer(userInfoMap.get("email"), getTestPassword());

    // create a site for org as msp admin
    String siteName = getRandomSiteName("TestCreateSite");
    Response response =
        createSite(siteName, siteType.cloud_direct.toString(), accountOrgAId, accountManagerToken);

    response.then().statusCode(ServerResponseCode.Success_Post);
    String siteId = response.then().extract().path("data.site_id");

    userSpogServer.setToken(mspToken);

    userInfoMap = composeRandomUserMap(TestPrefix, SpogConstants.MSP_ACCOUNT_ADMIN, orgId);
    String accountManagerBId = createUser(userInfoMap, mspToken);

    userSpogServer.assignMspAccountAdmins(orgId, accountOrgAIdB, accountManagerBId, mspToken);

    String accountManagerBToken = loginSpogServer(userInfoMap.get("email"), getTestPassword());

    response = getSite(siteId, accountManagerBToken);
    response.then().log().all();
    response.then().statusCode(ServerResponseCode.Insufficient_Permissions);
  }


  // @AfterMethod
  // public void getResult(ITestResult result) {
  //
  // if (result.getStatus() == ITestResult.FAILURE) {
  // count1.setfailedcount();
  // logger.log(LogStatus.FAIL, "Test Case Failed is " + result.getName() + " with parameters as "
  // + Arrays.asList(result.getParameters()));
  // logger.log(LogStatus.FAIL, result.getThrowable().getMessage());
  // } else if (result.getStatus() == ITestResult.SKIP) {
  // count1.setskippedcount();
  // logger.log(LogStatus.SKIP, "Test Case Skipped is " + result.getName());
  // } else if (result.getStatus() == ITestResult.SUCCESS) {
  // count1.setpassedcount();
  // }
  // // ending test
  // // endTest(logger) : It ends the current test and prepares to create HTML report
  // extent.endTest(logger);
  // }
  //
  //
  // @AfterTest
  // public void aftertest() {
  //
  // logger.log(LogStatus.INFO, "The total test cases passed are " + count1.getpassedcount());
  // logger.log(LogStatus.INFO, "the total test cases failed are " + count1.getfailedcount());
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
