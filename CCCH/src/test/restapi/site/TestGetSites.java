package test.restapi.site;

import static invoker.SiteTestHelper.composeRandomUserMap;
import static invoker.SiteTestHelper.createOrgnaization;
import static invoker.SiteTestHelper.createSite;
import static invoker.SiteTestHelper.createSubOrgnaization;
import static invoker.SiteTestHelper.createUser;
import static invoker.SiteTestHelper.getRandomOrganizationName;
import static invoker.SiteTestHelper.getRandomSiteName;
import static invoker.SiteTestHelper.getTestPassword;
import static invoker.SiteTestHelper.loginSpogServer;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import Constants.SpogConstants;
import InvokerServer.ServerResponseCode;
import genericutil.ExtentManager;
import invoker.SiteTestHelper.siteType;
import io.restassured.response.Response;

public class TestGetSites extends base.prepare.TestApiBase {
  private String token;

  private String TestDataPrefix = getClass().getSimpleName();

  // private ExtentReports extent;
  // private ExtentTest logger;
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
  // private int mspOrg_JobCount = getRandomWithRange(80, 200);
  private String groupId_A_Msp;
  private String groupId_B_Msp;

  private String accountOrgAId;
  private String childOrgA_Admin;
  private String childOrgA_Password;
  // private int childOrgA_JobCount = getRandomWithRange(80, 200);

  private String accountOrgBId;
  private String childOrgB_Admin;
  private String childOrgB_Password;
  // private int childOrgB_JobCount = getRandomWithRange(80, 200);

  private String directOrgId;
  private String directAdmin;
  private String directPassword;
  // private int directOrg_JobCount = getRandomWithRange(80, 200);;

  // private SPOGServer spogServer;
  // private ExtentTest test;
  // private ExtentReports rep;
  // private String csrAdminUserName;
  // private String csrAdminPassword;

  // private String baseUri;
  // private String port;
  private String psw            = getTestPassword();
  // private UserSpogServer userSpogServer;


  @Override
  public void setAuthor() {

    author = "Yin.Li";

  }


  //
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
  // userSpogServer = new UserSpogServer(baseUri, port);
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
    createSite(getRandomSiteName(TestDataPrefix + "mspOrg"), siteType.cloud_direct.toString(), "",
        mspToken);
    createSite(getRandomSiteName(TestDataPrefix + "mspOrg"), siteType.cloud_direct.toString(), "",
        mspToken);
    createSite(getRandomSiteName(TestDataPrefix + "mspOrg"), siteType.gateway.toString(), "",
        mspToken);

    // create child org A admin
    accountOrgAId =
        createSubOrgnaization(getRandomOrganizationName(TestDataPrefix), mspOrgId, mspToken);

    userInfoMap = composeRandomUserMap(TestDataPrefix, SpogConstants.DIRECT_ADMIN, accountOrgAId);
    createUser(userInfoMap, mspToken);
    childOrgA_Admin = userInfoMap.get("email");
    childOrgA_Password = getTestPassword();
    spogServer.userLogin(childOrgA_Admin, childOrgA_Password);
    String accountA_Token = spogServer.getJWTToken();
    createSite(getRandomSiteName(TestDataPrefix + "accountOrgA"), siteType.cloud_direct.toString(),
        "", accountA_Token);
    createSite(getRandomSiteName(TestDataPrefix + "accountOrgA"), siteType.cloud_direct.toString(),
        "", accountA_Token);

    // create child org B admin
    accountOrgBId =
        createSubOrgnaization(getRandomOrganizationName(TestDataPrefix), mspOrgId, mspToken);

    userInfoMap = composeRandomUserMap(TestDataPrefix, SpogConstants.DIRECT_ADMIN, accountOrgBId);
    createUser(userInfoMap, mspToken);
    childOrgB_Admin = userInfoMap.get("email");
    childOrgB_Password = getTestPassword();
    spogServer.userLogin(childOrgB_Admin, childOrgB_Password);
    String accountB_Token = spogServer.getJWTToken();
    createSite(getRandomSiteName(TestDataPrefix + "accountOrgB"), siteType.cloud_direct.toString(),
        "", accountB_Token);

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
    String directAdminToken = spogServer.getJWTToken();
    createSite(getRandomSiteName(TestDataPrefix), siteType.cloud_direct.toString(), "",
        directAdminToken);
    createSite(getRandomSiteName(TestDataPrefix), siteType.cloud_direct.toString(), "",
        directAdminToken);
  }


  @DataProvider(
      name = "getSiteSuccess")
  public final Object[][] getSiteSuccess() {

    return new Object[][] {{csrAdminUserName, csrAdminPassword, -1}, {mspAdmin, psw, 6},
        {childOrgA_Admin, psw, 2}, {childOrgB_Admin, psw, 1}, {directAdmin, psw, 2}};
  }


  @Test(
      dataProvider = "getSiteSuccess")
  public void Given_User_When_GetSites_Should_Successfull(String loginUsr, String loginPsw,
      int count) {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor(author);

    spogServer.userLogin(loginUsr, loginPsw);

    if (count > 0) {
      spogServer.getSites().then().assertThat().statusCode(ServerResponseCode.Success_Get)
          .body("data.size()", is(count));
    } else {
      spogServer.getSites().then().assertThat().statusCode(ServerResponseCode.Success_Get);
    }

  }


  @DataProvider(
      name = "getSiteSuccessWithOrgId")
  public final Object[][] getSiteSuccessWithOrgId() {

    return new Object[][] {{csrAdminUserName, csrAdminPassword, mspOrgId},
        {csrAdminUserName, csrAdminPassword, accountOrgAId},
        {csrAdminUserName, csrAdminPassword, accountOrgBId},
        {csrAdminUserName, csrAdminPassword, directOrgId}, {mspAdmin, psw, mspOrgId},
        {mspAdmin, psw, accountOrgAId}, {childOrgA_Admin, psw, accountOrgAId},
        {directAdmin, psw, directOrgId}};
  }


  @Test(
      dataProvider = "getSiteSuccessWithOrgId")
  public void Given_User_When_GetSitesWithOrgId_Should_Successfull(String loginUsr, String loginPsw,
      String orgId) {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor(author);

    spogServer.userLogin(loginUsr, loginPsw);

    HashMap<String, String> params = new HashMap<>();
    params.put("organization_id", orgId);

    spogServer.getSites(params).then().assertThat().statusCode(ServerResponseCode.Success_Get);

  }


  @DataProvider(
      name = "getSiteFailedWithOrgId")
  public final Object[][] getSiteFailedWithOrgId() {

    return new Object[][] {{mspAdmin, psw, directOrgId}, {childOrgA_Admin, psw, mspOrgId},
        {childOrgA_Admin, psw, accountOrgBId}, {childOrgA_Admin, psw, directOrgId},
        {childOrgA_Admin, psw, mspOrgId}, {directAdmin, psw, accountOrgAId}};
  }


  @Test(
      dataProvider = "getSiteFailedWithOrgId")
  public void Given_User_When_GetSitesWithOrgId_Should_Failed(String loginUsr, String loginPsw,
      String orgId) {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor(author);

    spogServer.userLogin(loginUsr, loginPsw);

    HashMap<String, String> params = new HashMap<>();
    params.put("organization_id", orgId);

    spogServer.getSites(params).then().assertThat()
        .statusCode(ServerResponseCode.Insufficient_Permissions);
  }


  @Test
  public void Given_CsrAdmin_When_GetMspOrgSites_Should_Successfull() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor(author);

    spogServer.userLogin(csrAdminUserName, csrAdminPassword);
    spogServer.getSites().then().assertThat().statusCode(ServerResponseCode.Success_Get);
  }

  @Test
  public void Given_Csr_ReadOnly_User_When_GetSites_Should_Success() {

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
	
    spogServer.getSites().then().assertThat().statusCode(ServerResponseCode.Success_Get);
    
    spogServer.userLogin(csrAdminUserName, csrAdminPassword);
    spogServer.CheckDeleteUserByIdStatus(csrReadOnlyUserId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
  }	


  @Test
  public void Given_MspAdmin_When_GetMspOrgSites_Should_Successfull() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor(author);

    spogServer.userLogin(mspAdmin, mspPassword);
    spogServer.getSites().then().assertThat().statusCode(ServerResponseCode.Success_Get);
  }


  @Test
  public void Given_AccountAdmin_When_GetAccountOrgSites_Should_Successfull() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor(author);

    spogServer.userLogin(childOrgA_Admin, childOrgA_Password);
    spogServer.getSites().then().assertThat().statusCode(ServerResponseCode.Success_Get);
  }


  @Test
  public void Given_DirectAdmin_When_GetDirectSites_Should_Successfull() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor(author);

    spogServer.userLogin(directAdmin, directPassword);
    spogServer.getSites().then().assertThat().statusCode(ServerResponseCode.Success_Get);
  }


  @Test
  public void Given_NotLogin_When_GetDirectSites_Should_Failed() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor(author);
    spogServer.setToken("");
    spogServer.getSites().then().assertThat().statusCode(ServerResponseCode.Not_login);

  }


  @Test
  public void Given_AccountManager_when_GetSitesForitsOrg_Should_Successful() {

    String csrToken = this.token;
    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor(author);
    // create a org
    String orgName = getRandomOrganizationName(TestDataPrefix);

    String orgId = createOrgnaization(orgName, SpogConstants.MSP_ORG,
        RandomStringUtils.randomAlphanumeric(8) + "@SpogQa",
        RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(4),
        RandomStringUtils.randomAlphanumeric(4), csrToken);

    // create msp admin
    Map<String, String> userInfoMap =
        composeRandomUserMap(TestDataPrefix, SpogConstants.MSP_ADMIN, orgId);
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
    String siteName = getRandomSiteName("TestCreateSiteABC");
    Response response =
        createSite(siteName, siteType.cloud_direct.toString(), accountOrgAId, accountManagerToken);

    response.then().statusCode(ServerResponseCode.Success_Post);

    String siteId = response.then().extract().path("data.site_id");

    spogServer.setToken(accountManagerToken);
    response = spogServer.getSites();
    response.then().log().all();
    spogServer.getSites().then().statusCode(ServerResponseCode.Success_Get);

  }

  //
  //
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

  // when org do not has group
  // when filter is empty
}
