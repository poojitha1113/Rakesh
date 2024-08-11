package api.users.destinationfilters;


import static invoker.SiteTestHelper.composeRandomUserMap;
import static invoker.SiteTestHelper.configSpogServerConnection;
import static invoker.SiteTestHelper.createOrgnaization;
import static invoker.SiteTestHelper.createSubOrgnaization;
import static invoker.SiteTestHelper.createUser;
import static invoker.SiteTestHelper.getRandomOrganizationName;
import static invoker.SiteTestHelper.getTestPassword;
import static invoker.SiteTestHelper.loginSpogServer;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
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

public class UpdateDestinationFilterForLoggedInUserTest extends base.prepare.TestApiBase {

  private String token;
  private String TestPrefix = getClass().getSimpleName();

  // private SQLServerDb bqdb1;
  // public int Nooftest;
  // private long creationTime;
  // private String BQName = null;
  // private String runningMachine;
  // private testcasescount count1;
  // private String buildVersion;

  private String mspOrgId;
  private String mspPassword;
  private String accountOrgAId;
  private String childOrgA_Admin;
  private String childOrgA_Password;
  private String accountOrgBId;
  private String childOrgB_Admin;
  private String childOrgB_Password;
  private String directOrgId;
  // private SPOGServer spogServer;
  // private GatewayServer gatewayServer;
  // private ExtentTest test;
  // private ExtentReports rep;
  // private String csrAdminUserName;
  // private String csrAdminPassword;

  private String baseUri;
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
  private String destFiltId_CsrA_1;
  private String destFiltId_CsrA_2;
  private String destFiltId_CsrA_CreateByOther;
  private String destFiltId_CsrB_1;
  private String destFiltId_CsrB_2;
  private String destFiltId_CsrB_CreateByOther;
  private String destFiltId_MspA_1;
  private String destFiltId_MspA_2;
  private String destFiltId_MspA_Other;
  private String destFiltId_MspB_1;
  private String destFiltId_MspB_2;
  private String destFiltId_MspB_Other;
  private String destFiltId_AccountA_1;
  private String destFiltId_AccountA_2;
  private String destFiltId_AccountA_Other1;
  private String destFiltId_AccountA_Other2;
  private String destFiltId_AccountB_1;
  private String destFiltId_AccountB_2;
  private String destFiltId_Direct_1;
  private String destFiltId_DirectA_1;
  private String destFiltId_DirectA_2;
  private String destFiltId_DirectB_1;
  private String destFiltId_DirectB_2;
  private String destFiltId_DirectA_Other;
  private String password   = getTestPassword();
  // private UserSpogServer userSpogServer;
  private String accountAMgrId;
  private String accountBMgrId;


  // @BeforeTest
  // @Parameters({"baseURI", "port", "csrAdminUserName", "csrAdminPassword", "logFolder",
  // "buildVersion"})
  // public void setSpogServerConnection(String baseURI, String port, String username, String
  // password,
  // String logFolder, String buildVersion) throws UnknownHostException {
  //
  // baseUri = baseURI;
  // configSpogServerConnection(baseURI, port);
  // token = loginSpogServer(username, password);
  //
  // this.csrAdminUserName = username;
  // this.csrAdminPassword = password;
  //
  // spogServer = new SPOGServer(baseURI, port);
  // spogServer.userLogin(username, password);
  //
  // spogDestinationServer = new SPOGDestinationServer(baseURI, port);
  //
  // gatewayServer = new GatewayServer(baseURI, port);
  //
  // userSpogServer = new UserSpogServer(baseURI, port);
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

    author = "Kanamarlapudi, Chandra Kanth";

  }


  @BeforeClass
  public void prepareJobData() {

    test.assignAuthor("Kanamarlapudi, Chandra Kanth");
    test = rep.startTest("Prepare Job Data");

    spogServer.userLogin(csrAdminUserName, csrAdminPassword);
    token = spogServer.getJWTToken();
    String csrToken = this.token;

    // create msp org
    // csrOrgId = createOrgnaization(getRandomOrganizationName(TestPrefix),
    // SpogConstants.CSR_ORG,
    // RandomStringUtils.randomAlphanumeric(8) + "@SpogQa",
    // RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(4),
    // RandomStringUtils.randomAlphanumeric(4), csrToken);
    // Map<String, String> userInfoMap =
    // composeRandomUserMap(TestPrefix, SpogConstants.CSR_ADMIN, csrOrgId);
    // createUser(userInfoMap, csrToken);
    // csrAdminUserNameA = userInfoMap.get("email");
    // csrAdminPasswordA = getTestPassword();
    // // String mspToken = loginSpogServer(mspAdmin, mspPassword);
    // spogServer.userLogin(csrAdminUserNameA, csrAdminPasswordA);
    // spogServer.getJWTToken();

    // Map<String, String> userInfoMap =
    // composeRandomUserMap(TestPrefix, SpogConstants.CSR_ADMIN, csrOrgId);
    // createUser(userInfoMap, csrToken);
    // csrAdminUserNameB = userInfoMap.get("email");
    // csrAdminPasswordB = getTestPassword();
    // // String mspToken = loginSpogServer(mspAdmin, mspPassword);
    // spogServer.userLogin(csrAdminUserNameA, csrAdminPasswordA);
    // spogServer.getJWTToken();
    //
    // destFiltId_CsrA_1 = createDestFilterInfo(csrAdminUserNameA, csrAdminPasswordA, "");
    // destFiltId_CsrA_2 = createDestFilterInfo(csrAdminUserNameA, csrAdminPasswordA, "");
    // String userId = getUserId(csrAdminUserNameA, csrAdminPasswordA);
    // destFiltId_CsrA_CreateByOther =
    // createDestFilterInfo(csrAdminUserName, csrAdminPassword, userId);
    //
    // destFiltId_CsrB_1 = createDestFilterInfo(csrAdminUserNameB, csrAdminPasswordB, "");
    // destFiltId_CsrB_2 = createDestFilterInfo(csrAdminUserNameB, csrAdminPasswordB, "");
    // userId = getUserId(csrAdminUserNameB, csrAdminPasswordB);
    // destFiltId_CsrB_CreateByOther =
    // createDestFilterInfo(csrAdminUserName, csrAdminPassword, userId);


    destFiltId_CsrA_1 = createDestFilterInfo(csrAdminUserName, csrAdminPassword, "");
    destFiltId_CsrA_2 = createDestFilterInfo(csrAdminUserName, csrAdminPassword, "");
    destFiltId_CsrB_1 = createDestFilterInfo(csrAdminUserName, csrAdminPassword, "");
    destFiltId_CsrB_2 = createDestFilterInfo(csrAdminUserName, csrAdminPassword, "");
    destFiltId_CsrA_CreateByOther = destFiltId_CsrA_1;
    // create msp org
    mspOrgId = createOrgnaization(getRandomOrganizationName(TestPrefix), SpogConstants.MSP_ORG,
        RandomStringUtils.randomAlphanumeric(8) + "@SpogQa",
        RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(4),
        RandomStringUtils.randomAlphanumeric(4), csrToken);

    // create msp admin
    Map<String, String> userInfoMap =
        composeRandomUserMap(TestPrefix, SpogConstants.MSP_ADMIN, mspOrgId);
    createUser(userInfoMap, csrToken);
    mspAdminA = userInfoMap.get("email");
    mspPasswordA = getTestPassword();
    userInfoMap = composeRandomUserMap(TestPrefix, SpogConstants.MSP_ADMIN, mspOrgId);
    createUser(userInfoMap, csrToken);
    mspAdminB = userInfoMap.get("email");
    mspPasswordB = getTestPassword();

    destFiltId_MspA_1 = createDestFilterInfo(mspAdminA, mspPasswordA, "");
    destFiltId_MspA_2 = createDestFilterInfo(mspAdminA, mspPasswordA, "");
    String userId = getUserId(mspAdminA, mspPasswordA);
    destFiltId_MspA_Other = createDestFilterInfo(csrAdminUserName, csrAdminPassword, userId);

    destFiltId_MspB_1 = createDestFilterInfo(mspAdminB, mspPasswordB, "");
    destFiltId_MspB_2 = createDestFilterInfo(mspAdminB, mspPasswordB, "");
    userId = getUserId(mspAdminB, mspPasswordB);
    destFiltId_MspB_Other = createDestFilterInfo(csrAdminUserName, csrAdminPassword, userId);

    // create child org A admin
    accountOrgAId =
        createSubOrgnaization(getRandomOrganizationName(TestPrefix), mspOrgId, csrToken);

    userInfoMap = composeRandomUserMap(TestPrefix, SpogConstants.DIRECT_ADMIN, accountOrgAId);
    createUser(userInfoMap, csrToken);
    childOrgA_Admin = userInfoMap.get("email");
    childOrgA_Password = getTestPassword();
    // spogServer.userLogin(childOrgA_Admin, childOrgA_Password);
    userInfoMap = composeRandomUserMap(TestPrefix, SpogConstants.MSP_ACCOUNT_ADMIN, mspOrgId);
    accountAMgrId = createUser(userInfoMap, csrToken);
    userSpogServer.setToken(csrToken);
    userSpogServer.assignMspAccountAdmins(mspOrgId, accountOrgAId, accountAMgrId, csrToken);

    // create child org B admin
    accountOrgBId =
        createSubOrgnaization(getRandomOrganizationName(TestPrefix), mspOrgId, csrToken);

    userInfoMap = composeRandomUserMap(TestPrefix, SpogConstants.DIRECT_ADMIN, accountOrgBId);
    createUser(userInfoMap, csrToken);
    childOrgB_Admin = userInfoMap.get("email");
    childOrgB_Password = getTestPassword();
    // spogServer.userLogin(childOrgB_Admin, childOrgB_Password);
    userInfoMap = composeRandomUserMap(TestPrefix, SpogConstants.MSP_ACCOUNT_ADMIN, mspOrgId);
    accountBMgrId = createUser(userInfoMap, csrToken);
    userSpogServer.setToken(csrToken);
    userSpogServer.assignMspAccountAdmins(mspOrgId, accountOrgBId, accountBMgrId, csrToken);

    destFiltId_AccountA_1 = createDestFilterInfo(childOrgA_Admin, childOrgA_Password, "");
    destFiltId_AccountA_2 = createDestFilterInfo(childOrgA_Admin, childOrgA_Password, "");
    userId = getUserId(childOrgA_Admin, childOrgA_Password);
    destFiltId_AccountA_Other1 = createDestFilterInfo(mspAdminB, mspPasswordB, userId);
    destFiltId_AccountA_Other2 = createDestFilterInfo(mspAdminB, mspPasswordB, userId);

    destFiltId_AccountB_1 = createDestFilterInfo(childOrgB_Admin, childOrgB_Password, "");
    destFiltId_AccountB_2 = createDestFilterInfo(childOrgB_Admin, childOrgB_Password, "");


    // create direct org
    directOrgId = createOrgnaization(getRandomOrganizationName(TestPrefix),
        SpogConstants.DIRECT_ORG, RandomStringUtils.randomAlphanumeric(8) + "@SpogQa",
        RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(4),
        RandomStringUtils.randomAlphanumeric(4), csrToken);
    // create direct admin
    userInfoMap = composeRandomUserMap(TestPrefix, SpogConstants.DIRECT_ADMIN, directOrgId);
    createUser(userInfoMap, csrToken);
    this.directAdminA = userInfoMap.get("email");
    this.directPasswordA = getTestPassword();
    userInfoMap = composeRandomUserMap(TestPrefix, SpogConstants.DIRECT_ADMIN, directOrgId);
    createUser(userInfoMap, csrToken);
    this.directAdminB = userInfoMap.get("email");
    this.directPasswordB = getTestPassword();

    // spogServer.userLogin(directAdmin, directPassword);
    destFiltId_DirectA_1 = createDestFilterInfo(directAdminA, directPasswordA, "");
    destFiltId_DirectA_2 = createDestFilterInfo(directAdminA, directPasswordA, "");
    userId = getUserId(directAdminA, directPasswordA);
    destFiltId_DirectA_Other = createDestFilterInfo(csrAdminUserName, csrAdminPassword, userId);

    destFiltId_DirectB_1 = createDestFilterInfo(directAdminB, directPasswordB, "");
    destFiltId_DirectB_2 = createDestFilterInfo(directAdminB, directPasswordB, "");

  }


  @DataProvider(
      name = "UpdateDestFilterId")
  public final Object[][] csrUpdateDestFilterId() {

    return new Object[][] {{csrAdminUserName, csrAdminPassword, destFiltId_CsrA_1},
        {csrAdminUserName, csrAdminPassword, destFiltId_CsrA_CreateByOther},
        {mspAdminA, password, destFiltId_MspA_1}, {mspAdminA, password, destFiltId_MspA_Other},
        {childOrgA_Admin, password, destFiltId_AccountA_1},
        {childOrgA_Admin, password, destFiltId_AccountA_Other1},
        {directAdminA, password, destFiltId_DirectA_1},
        {directAdminA, password, destFiltId_DirectA_Other}};
  }


  @Test(
      dataProvider = "UpdateDestFilterId")
  public void Given_LoggedInUser_When_UpdateDestFilter_Should_Successful(String userName,
      String password, String destFilterId) {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("Kanamarlapudi, Chandra Kanth");

    login(userName, password);
    Response response = updateDestinationFilterForLoggedInUser(destFilterId, "", "", true);
    response.then().assertThat().body("data.errors", is(nullValue()));
  }

  @Test
  public void Given_Csr_ReadOnly_User_When_UpdateDestFilter_Should_Failed() {

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
    
    //mspAdminA, password, destFiltId_MspA_1
    
    spogServer.userLogin(csrReadOnlyUser, password);
    spogDestinationServer.setToken(spogServer.getJWTToken());
    
    Response response = updateDestinationFilterForLoggedInUser(destFiltId_MspA_1, "", "", false);
    response.then().assertThat().statusCode(SpogConstants.RESOURCE_NOT_EXIST);
    
    spogServer.userLogin(csrAdminUserName, csrAdminPassword);
    spogServer.CheckDeleteUserByIdStatus(csrReadOnlyUserId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
  }	

  
  @DataProvider(
      name = "UpdateDestFilterIdFailed")
  public final Object[][] UpdateDestFilterIdFailed() {

    return new Object[][] {{csrAdminUserName, csrAdminPassword, destFiltId_MspA_1},
        {csrAdminUserName, csrAdminPassword, destFiltId_AccountA_1},
        {csrAdminUserName, csrAdminPassword, destFiltId_DirectA_1},
        {mspAdminA, password, destFiltId_AccountA_1}, {mspAdminA, password, destFiltId_DirectA_1},
        {directAdminA, password, destFiltId_MspA_1},
        {directAdminA, password, destFiltId_AccountA_1}};
  }


  @Test(
      dataProvider = "UpdateDestFilterIdFailed")
  public void Given_MspAdmin_When_UpdateDestFilterInOtherOrg_Should_Failed(String userName,
      String password, String destFilterId) {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("Kanamarlapudi, Chandra Kanth");

    login(userName, password);

    Response response = updateDestinationFilterForLoggedInUser(destFilterId, "", "", false);
    response.then().assertThat().statusCode(ServerResponseCode.Resource_Does_Not_Exist);

  }


  @Test
  public void Given_UserNotLogin_When_UpdateDestFilterInOtherOrg_Should_Failed() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("Kanamarlapudi, Chandra Kanth");

    spogServer.setToken("");
    spogDestinationServer.setToken("");

    Response response =
        updateDestinationFilterForLoggedInUser(UUID.randomUUID().toString(), "", "", false);
    response.then().assertThat().statusCode(ServerResponseCode.Not_login);

  }


  @Test
  public void Given_User_When_UpdateDestFilterWithInvalidDestinationId_Should_Failed() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("Kanamarlapudi, Chandra Kanth");

    login(mspAdminA, password);

    Response response = updateDestinationFilterForLoggedInUser("abc", "", "", false);
    response.then().assertThat().statusCode(ServerResponseCode.Bad_Request);

  }


  @DataProvider(
      name = "accountmgr")
  public final Object[][] accountMgrTest() {


    return new Object[][] {{destFiltId_AccountA_1, "false"}, {destFiltId_CsrA_1, "false"},
        {destFiltId_MspA_1, "false"}, {destFiltId_DirectA_1, "false"}};
  }


  @Test(
      dataProvider = "accountmgr")
  public void Given_AccountManager_when_GetDestinationFilterList_Should_work(String destFilterId,
      String expectResult) {

    test = rep.startTest("Given_AccountManager_when_GetDestinationFilterList_Should_work");
    test.assignAuthor("Kanamarlapudi, Chandra Kanth");

    spogServer.userLogin(csrAdminUserName, csrAdminPassword);

    String accountMgrId = accountAMgrId;
    String accountMgr =
        spogServer.getUserByID(accountMgrId, test).then().extract().path("data.email");

    spogServer.userLogin(accountMgr, getTestPassword());
    spogDestinationServer.setToken(spogServer.getJWTToken());

    Response response = updateDestinationFilterForLoggedInUser(destFilterId, "", "",
        Boolean.parseBoolean(expectResult));
    response.then().log().all();
    response.then().statusCode(ServerResponseCode.Resource_Does_Not_Exist);    

  }


  public void login(String userName, String password) {

    spogServer.userLogin(userName, password);
    spogDestinationServer.setToken(spogServer.getJWTToken());
  }


  public Response updateDestinationFilterForLoggedInUser(String destFilterId,
      String destinationType, String isDefault, boolean checkValue) {

    String filterName = getRandomDestFilterName(TestPrefix);
    String destinationName = UUID.randomUUID().toString();
    String policyID = UUID.randomUUID().toString();

    if (StringUtils.isEmpty(destinationType)) {
      destinationType = DestinationType.cloud_direct_volume.toString();
    }

    if (StringUtils.isEmpty(isDefault)) {
      isDefault = "false";
    }

    Response response =
        spogDestinationServer.updateDestinationFilterForLoggedInUserWithCheck(destFilterId,
            filterName, destinationName, policyID, destinationType, isDefault, test, checkValue);
    return response;
  }


  public Response updateDestinationFilter(String userID, String destFilterId, String policyID,
      String destinationType, String isDefault, boolean checkValue) {

    String filterName = getRandomDestFilterName(TestPrefix);
    String destinationName = UUID.randomUUID().toString();
    // String policyID = UUID.randomUUID().toString();

    // if (StringUtils.isEmpty(destinationType)) {
    // destinationType = DestinationType.cloud_direct_volume.toString();
    // }

    if (StringUtils.isEmpty(isDefault)) {
      isDefault = "false";
    }

    Response response = spogDestinationServer.updateDestinationFilterWithCheck(userID, destFilterId,
        filterName, destinationName, policyID, destinationType, isDefault, test, checkValue);
    return response;
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


  public String createDestFilterInfo(String userName, String password, String usrId) {

    String filterName = getRandomDestFilterName(TestPrefix);
    String policyID = UUID.randomUUID().toString();
    String destinationName = UUID.randomUUID().toString();
    String destinationType = DestinationType.all.toString();
    String isDefault = "true";

    spogServer.userLogin(userName, password);
    spogDestinationServer.setToken(spogServer.getJWTToken());

    if (StringUtils.isEmpty(usrId)) {
      usrId = spogServer.GetLoggedinUser_UserID();
    }

    String destFilterId = spogDestinationServer.createDestinationFilterWithCheck(usrId, filterName,
        destinationName, policyID, destinationType, isDefault, test);

    return destFilterId;
  }


  public void createDestFilterInfo(String userName, String password, String usrId, String destType,
      String is_Default) {

    String filterName = getRandomDestFilterName(TestPrefix);
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
