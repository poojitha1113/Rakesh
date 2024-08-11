package api.users.filter;

import static invoker.SiteTestHelper.composeRandomUserMap;
import static invoker.SiteTestHelper.configSpogServerConnection;
import static invoker.SiteTestHelper.createOrgnaization;
import static invoker.SiteTestHelper.createSubOrgnaization;
import static invoker.SiteTestHelper.createUser;
import static invoker.SiteTestHelper.getRandomOrganizationName;
import static invoker.SiteTestHelper.getTestPassword;
import static invoker.SiteTestHelper.loginSpogServer;
import static org.hamcrest.Matchers.is;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import Constants.SourceType;
import Constants.SpogConstants;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.ServerResponseCode;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class GetSourceFilterForLoggedUserTest extends base.prepare.TestApiBase {

  private String token;
  // private ExtentReports extent;
  private String TestPrefix = getClass().getSimpleName();

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
  // private ExtentTest test;
  // private ExtentReports rep;
  // private String csrAdminUserName;
  // private String csrAdminPassword;

  private String password   = getTestPassword();
  // private String baseUri;
  // private String port;
  // private SPOGDestinationServer spogDestinationServer;
  // private UserSpogServer userSpogServer;
  private String accountAMgrId;
  private String accountBMgrId;
  private String accountMgr;


  @Override
  public void setAuthor() {

    author = "Kanamarlapudi, Chandra Kanth";

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
  // userSpogServer = new UserSpogServer(baseURI, port);
  //
  // configSpogServerConnection(baseURI, port);
  // token = loginSpogServer(username, password);
  //
  // this.csrAdminUserName = username;
  // this.csrAdminPassword = password;
  //
  // spogServer = new SPOGServer(baseURI, port);
  // spogDestinationServer = new SPOGDestinationServer(baseURI, port);
  //
  // spogServer.userLogin(username, password);
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

    test.assignAuthor("Kanamarlapudi, Chandra Kanth");
    test = rep.startTest("Prepare Job Data");

    spogServer.userLogin(csrAdminUserName, csrAdminPassword);
    token = spogServer.getJWTToken();
    String csrToken = this.token;

    // create msp org
    mspOrgId = createOrgnaization(getRandomOrganizationName(TestPrefix), SpogConstants.MSP_ORG,
        RandomStringUtils.randomAlphanumeric(8) + "@SpogQa",
        RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(4),
        RandomStringUtils.randomAlphanumeric(4), csrToken);

    // create msp admin
    Map<String, String> userInfoMap =
        composeRandomUserMap(TestPrefix, SpogConstants.MSP_ADMIN, mspOrgId);
    createUser(userInfoMap, csrToken);
    mspAdmin = userInfoMap.get("email");
    mspPassword = getTestPassword();
    // String mspToken = loginSpogServer(mspAdmin, mspPassword);
    spogServer.userLogin(mspAdmin, mspPassword);
    String mspToken = spogServer.getJWTToken();


    // create child org A admin
    accountOrgAId =
        createSubOrgnaization(getRandomOrganizationName(TestPrefix), mspOrgId, mspToken);

    userInfoMap = composeRandomUserMap(TestPrefix, SpogConstants.DIRECT_ADMIN, accountOrgAId);
    createUser(userInfoMap, mspToken);
    childOrgA_Admin = userInfoMap.get("email");
    childOrgA_Password = getTestPassword();
    spogServer.userLogin(childOrgA_Admin, childOrgA_Password);

    userSpogServer.setToken(csrToken);
    userInfoMap = composeRandomUserMap(TestPrefix, SpogConstants.MSP_ACCOUNT_ADMIN, mspOrgId);
    accountMgr = userInfoMap.get("email");
    accountAMgrId = createUser(userInfoMap, csrToken);
    userSpogServer.assignMspAccountAdmins(mspOrgId, accountOrgAId, accountAMgrId, csrToken);

    // create child org B admin
    accountOrgBId =
        createSubOrgnaization(getRandomOrganizationName(TestPrefix), mspOrgId, mspToken);

    userInfoMap = composeRandomUserMap(TestPrefix, SpogConstants.DIRECT_ADMIN, accountOrgBId);
    createUser(userInfoMap, mspToken);
    childOrgB_Admin = userInfoMap.get("email");
    childOrgB_Password = getTestPassword();
    spogServer.userLogin(childOrgB_Admin, childOrgB_Password);

    // create direct org
    directOrgId = createOrgnaization(getRandomOrganizationName(TestPrefix),
        SpogConstants.DIRECT_ORG, RandomStringUtils.randomAlphanumeric(8) + "@SpogQa",
        RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(4),
        RandomStringUtils.randomAlphanumeric(4), csrToken);
    // create direct admin
    userInfoMap = composeRandomUserMap(TestPrefix, SpogConstants.DIRECT_ADMIN, directOrgId);
    createUser(userInfoMap, csrToken);
    this.directAdmin = userInfoMap.get("email");
    this.directPassword = getTestPassword();
    spogServer.userLogin(directAdmin, directPassword);

  }


  public void generateSourceFilterForloggedUser(String logUser, String logPsw, int count) {

    spogServer.userLogin(logUser, logPsw);
    String userId = spogServer.GetLoggedinUser_UserID();

    for (int i = 0; i < count; i++) {
      String filter_name = UUID.randomUUID().toString();
      String protection_status = "protect,unprotect";
      String connection_status = "online,offline";
      String protection_policy = UUID.randomUUID().toString();
      String backup_status = "finished";
      String source_group = UUID.randomUUID().toString();
      String operating_system = "windows";
      String applications = "SQL_SERVER";
      String site_id = UUID.randomUUID().toString();
      String source_name = UUID.randomUUID().toString();
      String source_type = "machine";
      String is_default = "false";

      String sourceFilterId = spogServer.createFilterwithCheck(userId, filter_name,
          protection_status, connection_status, protection_policy, backup_status, source_group,
          operating_system, applications, site_id, source_name, source_type, is_default, test);
    }
  }


  public void generateSourceFilterForloggedUser(String logUser, String logPsw, String source_type) {

    spogServer.userLogin(logUser, logPsw);
    String userId = spogServer.GetLoggedinUser_UserID();

    String filter_name = UUID.randomUUID().toString();
    String protection_status = "protect,unprotect";
    String connection_status = "online,offline";
    String protection_policy = UUID.randomUUID().toString();
    String backup_status = "finished";
    String source_group = UUID.randomUUID().toString();
    String operating_system = "windows";
    String applications = "SQL_SERVER";
    String site_id = UUID.randomUUID().toString();
    String source_name = UUID.randomUUID().toString();
    String is_default = "false";

    String sourceFilterId = spogServer.createFilterwithCheck(userId, filter_name, protection_status,
        connection_status, protection_policy, backup_status, source_group, operating_system,
        applications, site_id, source_name, source_type, is_default, test);
  }



  @DataProvider(
      name = "get_group_pass")
  public final Object[][] getGroupByNamePass() {

    return new Object[][] {{mspAdmin}, {childOrgA_Admin}, {directAdmin}, {accountMgr}};
  }


  @Test(
      dataProvider = "get_group_pass")
  public void Given_LoggedInUser_When_GetSourceFilters_Should_Success(String loginUsr) {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("Kanamarlapudi, Chandra Kanth");

    String loginPsw = getTestPassword();

    Random r = new Random();
    int count = r.nextInt((7 - 1) + 1) + 1;

    generateSourceFilterForloggedUser(loginUsr, loginPsw, count);

    Response response = spogServer.getSourceFiltersForLoggedInUser();
    response.then().statusCode(ServerResponseCode.Success_Get);
    response.then().assertThat().body("data.size()", is(count));
  }


  @Test
  public void Given_Csr_ReadOnly_User_When_GetSourceFilters_Should_Success() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("Kanamarlapudi, Chandra Kanth");

    spogServer.userLogin(csrAdminUserName, csrAdminPassword);
    token = spogServer.getJWTToken();
    String csrToken = token;
    
    String csrOrgId = spogServer.GetLoggedinUserOrganizationID();
    String password = getTestPassword();
    
    Map<String, String> userInfoMap = composeRandomUserMap(TestPrefix, SpogConstants.CSR_READ_ONLY_ADMIN, csrOrgId);
    String csrReadOnlyUser = userInfoMap.get("email");
    
    String csrReadOnlyUserId = spogServer.createUserAndCheck(csrReadOnlyUser, password, userInfoMap.get("first_name"), 
    		userInfoMap.get("last_name"), SpogConstants.CSR_READ_ONLY_ADMIN, csrOrgId, test);
    
    spogServer.userLogin(csrReadOnlyUser, password);
	
    Response response = spogServer.getSourceFiltersForLoggedInUser();
    response.then().statusCode(ServerResponseCode.Success_Get);
    
    spogServer.userLogin(csrAdminUserName, csrAdminPassword);
    spogServer.CheckDeleteUserByIdStatus(csrReadOnlyUserId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
  }	

  
  @Test
  public void Given_LoggedInUser_When_GetSourceFiltersForAgentLess_Should_Success() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("Kanamarlapudi, Chandra Kanth");

    spogServer.userLogin(csrAdminUserName, csrAdminPassword);
    String csrToken = this.token;
    String mspOrgId_1 = createOrgnaization(getRandomOrganizationName(TestPrefix),
        SpogConstants.MSP_ORG, RandomStringUtils.randomAlphanumeric(8) + "@SpogQa",
        RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(4),
        RandomStringUtils.randomAlphanumeric(4), csrToken);

    // create msp admin
    Map<String, String> userInfoMap =
        composeRandomUserMap(TestPrefix, SpogConstants.MSP_ADMIN, mspOrgId_1);
    createUser(userInfoMap, csrToken);

    String mspAdmin_1 = userInfoMap.get("email");
    mspPassword = getTestPassword();
    spogServer.userLogin(mspAdmin_1, mspPassword);

    String loginPsw = getTestPassword();

    generateSourceFilterForloggedUser(mspAdmin_1, loginPsw, SourceType.agentless_vm.toString());

    Response response = spogServer.getSourceFiltersForLoggedInUser();
    response.then().statusCode(ServerResponseCode.Success_Get);
    response.then().assertThat().body("data.size()", is(1));
    response.then().assertThat().body("data.source_filter_type.get(0)", is("agentless_vm"));
  }


  @Test
  public void Given_NotLoggedInUser_When_GetSourceFilters_Should_Faield() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("Kanamarlapudi, Chandra Kanth");

    generateSourceFilterForloggedUser(mspAdmin, getTestPassword(), 1);

    spogServer.setToken("");

    Response response = spogServer.getSourceFiltersForLoggedInUser();
    response.then().statusCode(ServerResponseCode.Not_login);

  }

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


}
