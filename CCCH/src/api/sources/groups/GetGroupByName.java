package api.sources.groups;

import static invoker.SiteTestHelper.composeRandomUserMap;
import static invoker.SiteTestHelper.createOrgnaization;
import static invoker.SiteTestHelper.createSubOrgnaization;
import static invoker.SiteTestHelper.createUser;
import static invoker.SiteTestHelper.getRandomOrganizationName;
import static invoker.SiteTestHelper.getTestPassword;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.LogStatus;

import Constants.ConnectionStatus;
import Constants.OSMajor;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import InvokerServer.ServerResponseCode;
import genericutil.ExtentManager;
import io.restassured.response.Response;

public class GetGroupByName extends base.prepare.TestApiBase {
  private String token;
  // private String TestPrefix = getClass().getSimpleName();
  private String password            = getTestPassword();

  // private SQLServerDb bqdb1;
  // public int Nooftest;
  // private long creationTime;
  // private String BQName = null;
  // private String runningMachine;
  // private testcasescount count1;
  // private String buildVersion;

  private String mspOrgId;
  private String mspAdmin;
  // private int mspOrg_JobCount = getRandomWithRange(80, 200);
  private String groupId_A_Msp;
  private String groupId_B_Msp;

  private String accountOrgAId;
  private String childOrgA_Admin;
  // private int childOrgA_JobCount = getRandomWithRange(80, 200);

  private String accountOrgBId;
  private String childOrgB_Admin;
  // private int childOrgB_JobCount = getRandomWithRange(80, 200);

  private String directOrgId;
  private String directAdmin;
  // private int directOrg_JobCount = getRandomWithRange(80, 200);;

  // private SPOGServer spogServer;
  // private ExtentTest test;
  // private ExtentReports rep;
  // private String csrAdminUserName;
  // private String csrAdminPassword;
  private String baseUri;
  private String csrOrgId;
  private String csrAdminUserNameA;
  private String groupName_csr;
  private String groupName_A_msp;
  private String groupName_B_msp;
  private String groupName_A_account;
  private String groupName_A_direct;
  private String groupName_B_direct;
  private String deupilcateGroupName = UUID.randomUUID().toString();
  private String groupId_A_Csr;
  // private GatewayServer gatewayServer;
  // private UserSpogServer userSpogServer;
  private String accountAMgrId;
  private String accountBMgrId;
  private String accountA_groupName;
  private String groupName_B_account;


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
  // gatewayServer = new GatewayServer(baseURI, port);
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

    author = "Yin.Li";

  }


  @BeforeClass
  public void prepareTestData() {

    test.assignAuthor("Yin.Li");
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
    // String mspToken = loginSpogServer(mspAdmin, mspPassword);
    spogServer.userLogin(mspAdmin, password);
    String mspToken = spogServer.getJWTToken();

    // prepare msp group
    groupName_A_msp = getRandomGroupName();
    groupId_A_Msp = spogServer.createGroupWithCheck(mspOrgId, groupName_A_msp,
        RandomStringUtils.randomAlphanumeric(8), test);
    groupName_B_msp = getRandomGroupName();
    groupId_B_Msp = spogServer.createGroupWithCheck(mspOrgId, groupName_B_msp,
        RandomStringUtils.randomAlphanumeric(8), test);
    spogServer.createGroupWithCheck(mspOrgId, deupilcateGroupName,
        RandomStringUtils.randomAlphanumeric(8), test);

    // create child org A admin
    accountOrgAId =
        createSubOrgnaization(getRandomOrganizationName(TestPrefix), mspOrgId, mspToken);

    userInfoMap = composeRandomUserMap(TestPrefix, SpogConstants.DIRECT_ADMIN, accountOrgAId);
    createUser(userInfoMap, mspToken);
    childOrgA_Admin = userInfoMap.get("email");
    spogServer.userLogin(childOrgA_Admin, password);
    groupName_A_account = getRandomGroupName();
    spogServer.createGroupWithCheck(accountOrgAId, groupName_A_account,
        RandomStringUtils.randomAlphanumeric(8), test);

    userInfoMap = composeRandomUserMap(TestPrefix, SpogConstants.MSP_ACCOUNT_ADMIN, mspOrgId);
    accountAMgrId = createUser(userInfoMap, csrToken);
    userSpogServer.setToken(csrToken);
    userSpogServer.assignMspAccountAdmins(mspOrgId, accountOrgAId, accountAMgrId, csrToken);

    // create child org B admin
    accountOrgBId =
        createSubOrgnaization(getRandomOrganizationName(TestPrefix), mspOrgId, mspToken);

    userInfoMap = composeRandomUserMap(TestPrefix, SpogConstants.DIRECT_ADMIN, accountOrgBId);
    createUser(userInfoMap, mspToken);
    childOrgB_Admin = userInfoMap.get("email");
    spogServer.userLogin(childOrgB_Admin, password);
    groupName_B_account = getRandomGroupName();
    spogServer.createGroupWithCheck(accountOrgBId, groupName_B_account,
        RandomStringUtils.randomAlphanumeric(8), test);
    spogServer.createGroupWithCheck(accountOrgBId, deupilcateGroupName,
        RandomStringUtils.randomAlphanumeric(8), test);

    userInfoMap = composeRandomUserMap(TestPrefix, SpogConstants.MSP_ACCOUNT_ADMIN, mspOrgId);
    accountBMgrId = createUser(userInfoMap, csrToken);
    userSpogServer.setToken(csrToken);
    userSpogServer.assignMspAccountAdmins(mspOrgId, accountOrgBId, accountBMgrId, csrToken);

    // create direct org
    directOrgId = createOrgnaization(getRandomOrganizationName(TestPrefix),
        SpogConstants.DIRECT_ORG, RandomStringUtils.randomAlphanumeric(8) + "@SpogQa",
        RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(4),
        RandomStringUtils.randomAlphanumeric(4), csrToken);
    // create direct admin
    userInfoMap = composeRandomUserMap(TestPrefix, SpogConstants.DIRECT_ADMIN, directOrgId);
    createUser(userInfoMap, csrToken);
    this.directAdmin = userInfoMap.get("email");
    spogServer.userLogin(directAdmin, password);
    groupName_A_direct = getRandomGroupName();
    groupName_B_direct = getRandomGroupName();
    String groupId_direct = spogServer.createGroupWithCheck(directOrgId, groupName_A_direct,
        RandomStringUtils.randomAlphanumeric(8), test);
    spogServer.createGroupWithCheck(directOrgId, groupName_B_direct,
        RandomStringUtils.randomAlphanumeric(8), test);
    spogServer.createGroupWithCheck(directOrgId, deupilcateGroupName,
        RandomStringUtils.randomAlphanumeric(8), test);
    addSourceIntoGroup(groupId_direct);

  }


  @DataProvider(
      name = "get_group_pass")
  public final Object[][] getGroupByNamePass() {

    return new Object[][] {{csrAdminUserName, csrAdminPassword, groupName_csr},
        {csrAdminUserName, csrAdminPassword, groupName_A_msp},
        {csrAdminUserName, csrAdminPassword, groupName_A_account},
        {csrAdminUserName, csrAdminPassword, groupName_A_direct},
        {mspAdmin, password, groupName_A_msp}, {mspAdmin, password, groupName_A_account},
        {childOrgB_Admin, password, groupName_A_account},
        {directAdmin, password, groupName_A_direct}};


  }


  @DataProvider(
      name = "get_group_pass1")
  public final Object[][] getGroupByNamePass1() {

    return new Object[][] {{password, groupName_A_msp}};


  }


  // @Test(
  // dataProvider = "get_group_pass")
  // public void Given_User_When_GetGroupByName_Should_Successful(String loginUser, String loginPsw,
  // String sourceGroupName) {
  //
  // test = ExtentManager.getNewTest(this.getClass().getName() + "."
  // + Thread.currentThread().getStackTrace()[1].getMethodName());
  // test.assignAuthor("yin.li");
  //
  // spogServer.userLogin(loginUser, loginPsw);
  //
  // Response response = spogServer.getSourceGroups(composeParams(sourceGroupName));
  // response.then().assertThat().statusCode(ServerResponseCode.Success_Get);
  // response.then().assertThat().body("data.size()", is(1));
  // }


  @DataProvider(
      name = "get_group_failed")
  public final Object[][] getGroupByNameFailed() {

    return new Object[][] {{mspAdmin, password, groupName_A_direct},
        {childOrgB_Admin, password, groupName_A_msp}, {directAdmin, password, groupName_A_account}};


  }


  @Test(
      dataProvider = "get_group_failed")
  public void Given_User_When_GetGroupByName_Should_GotEmptyList(String loginUser, String loginPsw,
      String sourceGroupName) {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(loginUser, loginPsw);

    Response response = spogServer.getSourceGroups(composeParams(sourceGroupName));
    response.then().assertThat().statusCode(ServerResponseCode.Success_Get);
    response.then().assertThat().body("data.size()", is(0));
    response.then().assertThat().body("data.errors.size()", is(0));

  }


  @Test
  public void Given_User_When_GetGroupByNotExistName_Should_GotEmptyList() {

    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    spogServer.userLogin(csrAdminUserName, csrAdminPassword);

    Response response = spogServer.getSourceGroups(composeParams(UUID.randomUUID().toString()));
    response.then().assertThat().statusCode(ServerResponseCode.Success_Get);
    response.then().assertThat().body("data.size()", is(0));
    response.then().assertThat().body("errors.size()", is(0));
  }


  // @Test
  // public void Given_User_When_GetGroupByDeupilcateName_Should_Success() {
  //
  // test = ExtentManager.getNewTest(this.getClass().getName() + "."
  // + Thread.currentThread().getStackTrace()[1].getMethodName());
  // test.assignAuthor("yin.li");
  //
  // spogServer.userLogin(csrAdminUserNameA, password);
  //
  // Response response = spogServer.getSourceGroups(composeParams(deupilcateGroupName));
  // response.then().assertThat().statusCode(ServerResponseCode.Success_Get);
  // response.then().assertThat().body("data.size()", is(4));
  // }


  // @Test
  // public void Given_User_When_GetGroupHasSource_Should_ShowCorrectSourceNumber() {
  //
  // test = ExtentManager.getNewTest(this.getClass().getName() + "."
  // + Thread.currentThread().getStackTrace()[1].getMethodName());
  // test.assignAuthor("yin.li");
  //
  // spogServer.userLogin(csrAdminUserNameA, password);
  //
  // Response response = spogServer.getSourceGroups(composeParams(groupName_A_msp));
  //
  // response = spogServer.getSourceGroups(composeParams(groupName_A_direct));
  // response.then().assertThat().statusCode(ServerResponseCode.Success_Get);
  // response.then().assertThat().body("data.size()", is(1));
  // response.then().assertThat().body("data.get(0).group_name", is(groupName_A_direct));
  // response.then().assertThat().body("data.get(0).source_count", is(1));
  // }


  // @Test
  // public void Given_User_When_GroupHasNotSource_Should_SourceCountIs0() {
  //
  // test = ExtentManager.getNewTest(this.getClass().getName() + "."
  // + Thread.currentThread().getStackTrace()[1].getMethodName());
  // test.assignAuthor("yin.li");
  //
  // spogServer.userLogin(csrAdminUserNameA, password);
  //
  // Response response = spogServer.getSourceGroups(composeParams(groupName_A_msp));
  // response.then().assertThat().statusCode(ServerResponseCode.Success_Get);
  // response.then().assertThat().body("data.size()", is(1));
  // response.then().assertThat().body("data.get(0).group_name", is(groupName_A_msp));
  // response.then().assertThat().body("data.get(0).source_count", is(0));
  //
  // }


  @DataProvider(
      name = "accountmgr")
  public final Object[][] accountMgrTest() {

    return new Object[][] {{accountAMgrId, groupName_A_account, "true"},
        {accountAMgrId, groupName_B_account, "false"}, {accountAMgrId, groupName_A_msp, "false"},
        {accountAMgrId, groupName_A_direct, "false"}};
  }


  @Test(
      dataProvider = "accountmgr")
  public void Given_AccountManager_when_GetGroupByName_Should_work(String accountMgrId,
      String groupName, String expectResult) {

    test = rep.startTest("Given_AccountManager_when_GetDestinationFilterList_Should_work");
    test.assignAuthor("yin.li");

    spogServer.userLogin(csrAdminUserName, csrAdminPassword);

    String accountMgr =
        spogServer.getUserByID(accountMgrId, test).then().extract().path("data.email");
    spogServer.userLogin(accountMgr, getTestPassword());

    Response response = spogServer.getSourceGroups(composeParams(groupName));

    if (Boolean.parseBoolean(expectResult)) {
      response.then().statusCode(ServerResponseCode.Success_Get);
    } else {
      response.then().statusCode(ServerResponseCode.Success_Get);
    }

  }

  @Test
  public void Given_Csr_ReadOnly_User_When_GetGroupByName_Should_Success() {

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
    
    spogServer.userLogin(csrReadOnlyUser, password);
	
    Response response = spogServer.getSourceGroups(composeParams(groupName_A_direct));
    response.then().assertThat().statusCode(ServerResponseCode.Success_Get);
       
    spogServer.userLogin(csrAdminUserName, csrAdminPassword);
    spogServer.CheckDeleteUserByIdStatus(csrReadOnlyUserId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
  }	

  
  public HashMap<String, String> composeParams(String groupName) {

    HashMap<String, String> params = new HashMap<>();

    params.put("group_name", groupName);

    return params;

  }


  public String getRandomGroupName() {

    return TestPrefix + "_SpogQA_" + RandomStringUtils.randomAlphanumeric(8);
  }


  public String createTestSource() {

    String soureceName = UUID.randomUUID().toString();
    SourceType sourceType = SourceType.shared_folder;
    SourceProduct sourceProduct = SourceProduct.udp;
    String org_id = spogServer.GetLoggedinUserOrganizationID();
    // String siteId = UUID.randomUUID().toString();
    ProtectionStatus protectionStatus = ProtectionStatus.protect;
    ConnectionStatus connectionStatus = ConnectionStatus.online;
    String os_major = OSMajor.windows.name();
    String applications = "";
    String vm_name = null;
    String hypervisor_id = null;
    String agent_name = null;
    String os_name = null;
    String os_architecture = null;
    String agent_current_version = null;
    String agent_upgrade_version = null;
    String agent_upgrade_link = null;

    // Response response = createSite(UUID.randomUUID().toString(), siteType.gateway.toString(),
    // org_id, spogServer.getJWTToken());
    // String siteId = response.then().extract().path("data.site_id");
    //
    String siteId = gatewayServer.createsite_register_login(org_id, spogServer.getJWTToken(),
        spogServer.GetLoggedinUser_UserID(), "shuo", "1.0.0", spogServer, test);

    Response createSourceResponse = spogServer.createSource(soureceName, sourceType, sourceProduct,
        org_id, siteId, protectionStatus, connectionStatus, os_major, applications, vm_name,
        hypervisor_id, agent_name, os_name, os_architecture, agent_current_version,
        agent_upgrade_version, agent_upgrade_link, test);

    String source_id = createSourceResponse.then().extract().path("data.source_id");
    return source_id;
  }


  public void addSourceIntoGroup(String group_id) {

    String[] arrayOfSourceNodes = new String[] {createTestSource()};
    spogServer.addSourcetoSourceGroupwithCheck(group_id, arrayOfSourceNodes,
        spogServer.getJWTToken(), SpogConstants.SUCCESS_POST, null, test);
  }


  @AfterMethod
  public void getResult(ITestResult result) {

    if (result.getStatus() == ITestResult.FAILURE) {
      count1.setfailedcount();
      test.log(LogStatus.FAIL, "Test Case Failed is " + result.getName() + " with parameters as "
          + Arrays.asList(result.getParameters()));
      test.log(LogStatus.FAIL, result.getThrowable().getMessage());
    } else if (result.getStatus() == ITestResult.SKIP) {
      count1.setskippedcount();
      test.log(LogStatus.SKIP, "Test Case Skipped is " + result.getName());
    } else if (result.getStatus() == ITestResult.SUCCESS) {
      count1.setpassedcount();
    }
    // ending test
    // endTest(logger) : It ends the current test and prepares to create HTML report
    rep.endTest(test);
  }


  @AfterTest
  public void aftertest() {

    test.log(LogStatus.INFO, "The total test cases passed are " + count1.getpassedcount());
    test.log(LogStatus.INFO, "the total test cases failed are " + count1.getfailedcount());
    rep.flush();
  }


  @AfterClass
  public void updatebd() {

    try {
      if (count1.getfailedcount() > 0) {
        Nooftest = count1.getpassedcount() + count1.getfailedcount() + count1.getskippedcount();
        bqdb1.updateTable(this.BQName, this.runningMachine, this.buildVersion,
            String.valueOf(Nooftest), Integer.toString(count1.getpassedcount()),
            Integer.toString(count1.getfailedcount()), String.valueOf(count1.getskippedcount()),
            count1.getcreationtime(), "Failed");
      } else {
        Nooftest = count1.getpassedcount() + count1.getfailedcount() + count1.getskippedcount();
        bqdb1.updateTable(this.BQName, this.runningMachine, this.buildVersion,
            String.valueOf(Nooftest), Integer.toString(count1.getpassedcount()),
            Integer.toString(count1.getfailedcount()), String.valueOf(count1.getskippedcount()),
            count1.getcreationtime(), "Passed");
      }
    } catch (ClientProtocolException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  // when org do not has group
  // when filter is empty
}
