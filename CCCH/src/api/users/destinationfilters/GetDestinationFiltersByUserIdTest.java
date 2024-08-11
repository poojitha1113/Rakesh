package api.users.destinationfilters;


import static invoker.SiteTestHelper.composeRandomUserMap;
import static invoker.SiteTestHelper.configSpogServerConnection;
import static invoker.SiteTestHelper.createOrgnaization;
import static invoker.SiteTestHelper.createSubOrgnaization;
import static invoker.SiteTestHelper.createUser;
import static invoker.SiteTestHelper.getRandomOrganizationName;
import static invoker.SiteTestHelper.getTestPassword;
import static invoker.SiteTestHelper.loginSpogServer;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.testng.Assert.assertEquals;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang.StringUtils;
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
import Constants.DestinationType;
import Constants.SpogConstants;
import InvokerServer.GatewayServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.ServerResponseCode;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class GetDestinationFiltersByUserIdTest extends base.prepare.TestApiBase {

  private String token;
  // private ExtentReports extent;
  // private ExtentTest logger;
  private String TestDataPrefix = getClass().getSimpleName();

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
  private String csrOrgId;
  private String csrAdminUserNameA;
  private String csrAdminPasswordA;
  private String csrAdminUserNameB;
  private String csrAdminPasswordB;
  // private SPOGDestinationServer spogDestinationServer;
  private String mspAdminA;
  private String mspPasswordA;
  private String mspAdminB;
  private String mspPasswordB;
  private String directAdminA;
  private String directPasswordA;
  private String directAdminB;
  private String directPasswordB;
  private String accountAMgrId;
  // private UserSpogServer userSpogServer;
  private String accountBMgrId;
  private String mspAdminAId;
  private String directAdminId;
  private String accountAdminId;
  private String childOrgA_AdminId;
  private String childOrgB_AdminId;


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
  // userSpogServer = new UserSpogServer(baseURI, port);
  //
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



  @BeforeClass
  public void prepareJobData() {

    test.assignAuthor("Yin.Li");
    test = rep.startTest("Prepare Job Data");

    spogServer.userLogin(csrAdminUserName, csrAdminPassword);
    token = spogServer.getJWTToken();
    String csrToken = token;

    // create msp org
    mspOrgId = createOrgnaization(getRandomOrganizationName(TestDataPrefix), SpogConstants.MSP_ORG,
        RandomStringUtils.randomAlphanumeric(8) + "@SpogQa",
        RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(4),
        RandomStringUtils.randomAlphanumeric(4), csrToken);

    // create msp admin
    Map<String, String> userInfoMap =
        composeRandomUserMap(TestDataPrefix, SpogConstants.MSP_ADMIN, mspOrgId);
    createUser(userInfoMap, csrToken);
    mspAdminA = userInfoMap.get("email");
    mspPasswordA = getTestPassword();
    userInfoMap = composeRandomUserMap(TestDataPrefix, SpogConstants.MSP_ADMIN, mspOrgId);
    mspAdminAId = createUser(userInfoMap, csrToken);
    mspAdminB = userInfoMap.get("email");
    mspPasswordB = getTestPassword();

    createDestFilterInfo(mspAdminA, mspPasswordA, "");
    createDestFilterInfo(mspAdminA, mspPasswordA, "");
    String userId = getUserId(mspAdminA, mspPasswordA);
    createDestFilterInfo(csrAdminUserName, csrAdminPassword, userId);

    createDestFilterInfo(mspAdminB, mspPasswordB, "");
    createDestFilterInfo(mspAdminB, mspPasswordB, "");
    userId = getUserId(mspAdminB, mspPasswordB);
    createDestFilterInfo(csrAdminUserName, csrAdminPassword, userId);

    // create child org A admin
    accountOrgAId =
        createSubOrgnaization(getRandomOrganizationName(TestDataPrefix), mspOrgId, csrToken);

    userInfoMap = composeRandomUserMap(TestDataPrefix, SpogConstants.DIRECT_ADMIN, accountOrgAId);
    childOrgA_AdminId = createUser(userInfoMap, csrToken);
    childOrgA_Admin = userInfoMap.get("email");
    childOrgA_Password = getTestPassword();

    userInfoMap = composeRandomUserMap(TestDataPrefix, SpogConstants.MSP_ACCOUNT_ADMIN, mspOrgId);
    accountAMgrId = createUser(userInfoMap, csrToken);
    userSpogServer.setToken(csrToken);
    userSpogServer.assignMspAccountAdmins(mspOrgId, accountOrgAId, accountAMgrId, csrToken);

    // create child org B admin
    accountOrgBId =
        createSubOrgnaization(getRandomOrganizationName(TestDataPrefix), mspOrgId, csrToken);

    userInfoMap = composeRandomUserMap(TestDataPrefix, SpogConstants.DIRECT_ADMIN, accountOrgBId);
    childOrgB_AdminId = createUser(userInfoMap, csrToken);
    childOrgB_Admin = userInfoMap.get("email");
    childOrgB_Password = getTestPassword();

    userInfoMap = composeRandomUserMap(TestDataPrefix, SpogConstants.MSP_ACCOUNT_ADMIN, mspOrgId);
    accountBMgrId = createUser(userInfoMap, csrToken);
    userSpogServer.setToken(csrToken);
    userSpogServer.assignMspAccountAdmins(mspOrgId, accountOrgBId, accountBMgrId, csrToken);

    spogServer.userLogin(childOrgA_Admin, childOrgA_Password);
    createDestFilterInfo(childOrgA_Admin, childOrgA_Password, "");
    createDestFilterInfo(childOrgA_Admin, childOrgA_Password, "");
    userId = getUserId(childOrgA_Admin, childOrgA_Password);
    createDestFilterInfo(mspAdminB, mspPasswordB, userId);
    createDestFilterInfo(mspAdminB, mspPasswordB, userId);

    spogServer.userLogin(childOrgB_Admin, childOrgB_Password);
    createDestFilterInfo(childOrgB_Admin, childOrgB_Password, "");
    createDestFilterInfo(childOrgB_Admin, childOrgB_Password, "");


    // create direct org
    directOrgId = createOrgnaization(getRandomOrganizationName(TestDataPrefix),
        SpogConstants.DIRECT_ORG, RandomStringUtils.randomAlphanumeric(8) + "@SpogQa",
        RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(4),
        RandomStringUtils.randomAlphanumeric(4), csrToken);
    // create direct admin
    userInfoMap = composeRandomUserMap(TestDataPrefix, SpogConstants.DIRECT_ADMIN, directOrgId);
    createUser(userInfoMap, csrToken);
    this.directAdminA = userInfoMap.get("email");
    this.directPasswordA = getTestPassword();
    userInfoMap = composeRandomUserMap(TestDataPrefix, SpogConstants.DIRECT_ADMIN, directOrgId);
    directAdminId = createUser(userInfoMap, csrToken);
    this.directAdminB = userInfoMap.get("email");
    this.directPasswordB = getTestPassword();

    // spogServer.userLogin(directAdmin, directPassword);
    createDestFilterInfo(directAdminA, directPasswordA, "");
    createDestFilterInfo(directAdminA, directPasswordA, "");
    userId = getUserId(directAdminA, directPasswordA);
    createDestFilterInfo(csrAdminUserName, csrAdminPassword, userId);

    createDestFilterInfo(directAdminB, directPasswordB, "");
    createDestFilterInfo(directAdminB, directPasswordB, "");

  }


  @Test
  public void Given_CsrAdmin_When_GetDestinationFilterListCreateItSelf_Should_Succsfull() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    String userId = getUserId(csrAdminUserName, csrAdminPassword);

    Response response =
        getUserDestinationFilterList(csrAdminUserName, csrAdminPassword, userId, null);

    response.then().assertThat().statusCode(ServerResponseCode.Success_Get);
  }


  @Test
  public void Given_CsrAdmin_When_GetDestinationFilterListCreateByOtherCsr_Should_Succsfull() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    String userId = getUserId(csrAdminUserName, csrAdminPassword);

    Response response =
        getUserDestinationFilterList(csrAdminUserName, csrAdminPassword, userId, null);

    response.then().assertThat().statusCode(ServerResponseCode.Success_Get);
  }


  @Test
  public void Given_CsrAdmin_When_GetDestinationFilterListCreateByMspAdmin_Should_Succsfull() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    String userId = getUserId(mspAdminA, mspPasswordA);

    Response response =
        getUserDestinationFilterList(csrAdminUserName, csrAdminPassword, userId, null);

    response.then().assertThat().statusCode(ServerResponseCode.Success_Get);
  }


  @Test
  public void Given_CsrAdmin_When_GetDestinationFilterListCreateByAccountAdmin_Should_Succsfull() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    String userId = getUserId(childOrgA_Admin, childOrgA_Password);

    Response response =
        getUserDestinationFilterList(csrAdminUserName, csrAdminPassword, userId, null);

    response.then().assertThat().statusCode(ServerResponseCode.Success_Get);
  }


  @Test
  public void Given_CsrAdmin_When_GetDestinationFilterListCreateByDirectAdmin_Should_Succsfull() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    String userId = getUserId(directAdminA, directPasswordA);

    Response response =
        getUserDestinationFilterList(csrAdminUserName, csrAdminPassword, userId, null);

    response.then().assertThat().statusCode(ServerResponseCode.Success_Get);
  }


  @Test
  public void Given_MspAdmin_When_GetDestinationFilterListCreateByItself_Should_Succsfull() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    String userId = getUserId(mspAdminA, mspPasswordA);

    Response response = getUserDestinationFilterList(mspAdminA, mspPasswordA, userId, null);

    response.then().assertThat().statusCode(ServerResponseCode.Success_Get);
    response.then().assertThat().body("data.size()", is(3));

    ArrayList<HashMap<String, Object>> destinations = response.then().extract().path("data");
    int countvalue = (int) destinations.get(0).get("count");
    assertEquals(0, countvalue);
  }


  @Test
  public void Given_MspAdmin_When_GetDestinationFilterListCreateByOtherMspAdminInItsOrg_Should_Succsfull() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    String userId = getUserId(mspAdminB, mspPasswordB);

    Response response = getUserDestinationFilterList(mspAdminA, mspPasswordA, userId, null);

    response.then().assertThat().statusCode(ServerResponseCode.Success_Get);
    response.then().assertThat().body("data.size()", is(3));
  }


  @Test
  public void Given_MspAdmin_When_GetDestinationFilterListCreateByOtherAccountAdminInItsOrg_Should_Succsfull() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    String userId = getUserId(childOrgA_Admin, childOrgA_Password);

    Response response = getUserDestinationFilterList(mspAdminA, mspPasswordA, userId, null);

    response.then().assertThat().statusCode(ServerResponseCode.Success_Get);
    response.then().assertThat().body("data.size()", is(4));
  }


  @Test
  public void Given_AccountAdmin_When_GetDestinationFilterListCreateByItself_Should_Succsfull() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    String userId = getUserId(childOrgA_Admin, childOrgA_Password);

    Response response =
        getUserDestinationFilterList(childOrgA_Admin, childOrgA_Password, userId, null);

    response.then().assertThat().statusCode(ServerResponseCode.Success_Get);
  }


  @Test
  public void Given_AccountAdmin_When_GetDestinationFilterListCreateByOtherAccountAdmin_Should_Failed() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    String userId = getUserId(childOrgB_Admin, childOrgB_Password);

    Response response =
        getUserDestinationFilterList(childOrgA_Admin, childOrgA_Password, userId, null);

    response.then().assertThat().statusCode(ServerResponseCode.Insufficient_Permissions);
  }


  @Test
  public void Given_DirectAdmin_When_GetDestinationFilterListCreateByItself_Should_Succsfull() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    String userId = getUserId(directAdminA, directPasswordA);

    Response response = getUserDestinationFilterList(directAdminA, directPasswordA, userId, null);

    response.then().assertThat().statusCode(ServerResponseCode.Success_Get);
  }


  @Test
  public void Given_DirectAdmin_When_GetDestinationFilterListCreateByOtherDirectAdmin_Should_Succsfull() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    String userId = getUserId(directAdminB, directPasswordB);

    Response response = getUserDestinationFilterList(directAdminA, directPasswordA, userId, null);

    response.then().assertThat().statusCode(ServerResponseCode.Success_Get);
  }


  @Test
  public void Given_DirectAdmin_When_GetDestinationFilterListInOtherOrg_Should_Failed() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    String userId = getUserId(csrAdminUserName, csrAdminPassword);

    Response response = getUserDestinationFilterList(directAdminA, directPasswordA, userId, null);

    response.then().assertThat().statusCode(ServerResponseCode.Insufficient_Permissions); // 826198
  }


  @Test
  public void Given_MspAdmin_When_GetDestinationFilterListInOtherOrg_Should_Failed() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    String userId = getUserId(directAdminA, directPasswordA);

    Response response = getUserDestinationFilterList(mspAdminA, mspPasswordA, userId, null);

    response.then().assertThat().statusCode(ServerResponseCode.Insufficient_Permissions); // 826199
  }


  @Test
  public void Given_AccountAdmin_When_GetDestinationFilterListInOtherOrg_Should_Failed() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    String userId = getUserId(directAdminA, directPasswordA);

    Response response =
        getUserDestinationFilterList(childOrgA_Admin, childOrgA_Password, userId, null);

    response.then().assertThat().statusCode(ServerResponseCode.Insufficient_Permissions); // 826197
  }


  @Test
  public void Given_CsrAdmin_When_GetDestinationFilterListWithInvalidUsrId_Should_Failed() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    String userId = "invalidUserId";

    Response response =
        getUserDestinationFilterList(childOrgA_Admin, childOrgA_Password, userId, null);

    response.then().assertThat().statusCode(ServerResponseCode.Bad_Request);
    response.then().assertThat().body("errors.code.size()", is(1)).body("errors.code",
        hasItem("40000005"));
  }


  @Test
  public void Given_CsrAdmin_When_GetDestinationFilterListWithNotExistUsrId_Should_Failed() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    String userId = UUID.randomUUID().toString();

    Response response =
        getUserDestinationFilterList(childOrgA_Admin, childOrgA_Password, userId, null);

    response.then().assertThat().statusCode(ServerResponseCode.Resource_Does_Not_Exist);
    response.then().assertThat().body("errors.code.size()", is(1)).body("errors.code",
        hasItem("00200007")); // 00200007
  }


  @Test
  public void Given_CsrAdmin_When_GetDestinationFilterListWithInvalidIsDefaultValue_Should_Failed() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    HashMap<String, String> params = new HashMap<>();
    params.put("destination_type", "invalidType");

    String userId = getUserId(directAdminA, directPasswordA);

    Response response =
        getUserDestinationFilterList(csrAdminUserName, csrAdminPassword, userId, params);

    response.then().assertThat().statusCode(ServerResponseCode.Success_Get);
    response.then().assertThat().body("data.errors.size()", is(0));
  }


  @Test
  public void Given_CsrAdmin_When_GetDestinationFilterListWithInvalidDestType_Should_Failed() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    HashMap<String, String> params = new HashMap<>();
    params.put("is_default", "invalidValue");

    String userId = getUserId(directAdminA, directPasswordA);

    Response response =
        getUserDestinationFilterList(csrAdminUserName, csrAdminPassword, userId, params);

    response.then().assertThat().statusCode(ServerResponseCode.Success_Get);
    // response.then().assertThat().body("data.errors.size()", is(0));
  }


  @Test
  public void Given_CsrAdmin_When_GetDestinationFilterListWithValidIsDefaultValue_Should_Success() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    HashMap<String, String> params = new HashMap<>();
    params.put("is_default", "true");

    String userId = getUserId(directAdminA, directPasswordA);

    Response response =
        getUserDestinationFilterList(csrAdminUserName, csrAdminPassword, userId, params);

    response.then().assertThat().statusCode(ServerResponseCode.Success_Get);
    response.then().assertThat().body("data.size()", is(1));
  }


  @Test
  public void Given_Csr_ReadOnly_User_When_GetDestinationFilter_Should_Success() {

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
	
    String userId = getUserId(mspAdminA, mspPasswordA);
    Response response =
        getUserDestinationFilterList(csrAdminUserName, csrAdminPassword, userId, null);
    
    spogServer.userLogin(csrAdminUserName, csrAdminPassword);
    spogServer.CheckDeleteUserByIdStatus(csrReadOnlyUserId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
  }	

  @Test
  public void Given_CsrAdmin_When_GetDestinationFilterListWithValidDestType_Should_Success() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    HashMap<String, String> params = new HashMap<>();
    params.put("destination_type", "all");

    String userId = getUserId(directAdminA, directPasswordA);

    Response response =
        getUserDestinationFilterList(csrAdminUserName, csrAdminPassword, userId, params);

    response.then().assertThat().statusCode(ServerResponseCode.Success_Get);
    response.then().assertThat().body("data.size()", is(3));

    params = new HashMap<>();
    params.put("destination_type", "cloud_dedupe_store");
    response = getUserDestinationFilterList(csrAdminUserName, csrAdminPassword, userId, params);
    response.then().assertThat().statusCode(ServerResponseCode.Success_Get);
    response.then().assertThat().body("data.size()", is(0));
  }


  @DataProvider(
      name = "accountmgr")
  public final Object[][] accountMgrTest() {

    return new Object[][] {{accountAMgrId, childOrgA_AdminId, "true"},
        {accountAMgrId, childOrgB_AdminId, "false"}, {accountAMgrId, mspAdminAId, "false"},
        {accountAMgrId, directAdminId, "false"}};
  }


  @Test(
      dataProvider = "accountmgr")
  public void Given_AccountManager_when_GetDestinationFilterList_Should_work(String accountMgrId,
      String userId, String expectResult) {

    test = rep.startTest("Given_AccountManager_when_GetDestinationFilterList_Should_work");
    test.assignAuthor("yin.li");

    spogServer.userLogin(csrAdminUserName, csrAdminPassword);

    String accountMgr =
        spogServer.getUserByID(accountMgrId, test).then().extract().path("data.email");
    spogServer.userLogin(accountMgr, getTestPassword());

    Response response = getUserDestinationFilterList(accountMgr, getTestPassword(), userId, null);

    if (Boolean.parseBoolean(expectResult)) {
      response.then().statusCode(ServerResponseCode.Success_Get);
    } else {
      response.then().statusCode(ServerResponseCode.Insufficient_Permissions);
    }

  }


  public Response getUserDestinationFilterList(String username, String password, String usrId,
      HashMap<String, String> params) {

    spogServer.userLogin(username, password);
    spogDestinationServer.setToken(spogServer.getJWTToken());

    Response response = spogDestinationServer.getDestinationFilterByUserId(usrId, params);
    return response;
  }


  public String getUserId(String userName, String password) {

    spogServer.userLogin(userName, password);
    return spogServer.GetLoggedinUser_UserID();
  }


  public HashMap<String, String> composeParams(String destType, String isDefault, String pageIndex,
      String pageSize) {

    HashMap<String, String> params = new HashMap<String, String>();
    if (StringUtils.isNotEmpty(destType)) {
      params.put("destination_type", destType);
    }
    if (StringUtils.isNotEmpty(destType)) {
      params.put("is_default", isDefault);
    }
    if (StringUtils.isNotEmpty(destType)) {
      params.put("page_size", pageIndex);
    }
    if (StringUtils.isNotEmpty(destType)) {
      params.put("page", pageSize);
    }
    return params;

  }


  public void createDestFilterInfo(String userName, String password, String usrId) {

    String filterName = getRandomDestFilterName(TestDataPrefix);
    String policyID = UUID.randomUUID().toString();
    String destinationName = UUID.randomUUID().toString();
    String destinationType = DestinationType.all.toString();
    String isDefault = "true";

    spogServer.userLogin(userName, password);
    spogDestinationServer.setToken(spogServer.getJWTToken());

    if (StringUtils.isEmpty(usrId)) {
      usrId = spogServer.GetLoggedinUser_UserID();
    }

    spogDestinationServer.createDestinationFilterWithCheck(usrId, filterName, destinationName,
        policyID, destinationType, isDefault, test);

  }


  public void createDestFilterInfo(String userName, String password, String usrId, String destType,
      String is_Default) {

    String filterName = getRandomDestFilterName(TestDataPrefix);
    String policyID = UUID.randomUUID().toString();
    String destinationName = UUID.randomUUID().toString();
    String destinationType = destType;
    String isDefault = is_Default;

    spogServer.userLogin(userName, password);
    spogDestinationServer.setToken(spogServer.getJWTToken());

    if (StringUtils.isEmpty(usrId)) {
      usrId = spogServer.GetLoggedinUser_UserID();
    }

    spogDestinationServer.createDestinationFilterWithCheck(usrId, filterName, destinationName,
        policyID, destinationType, isDefault, test);

  }


  public static String getRandomDestFilterName(String prefix) {

    return prefix + "_DestFlter_" + RandomStringUtils.randomAlphanumeric(8) + "_SpogQa";
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
