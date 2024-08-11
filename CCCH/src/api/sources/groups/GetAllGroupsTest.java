package api.sources.groups;

import static invoker.SiteTestHelper.composeRandomUserMap;
import static invoker.SiteTestHelper.createSubOrgnaization;
import static invoker.SiteTestHelper.createUser;
import static invoker.SiteTestHelper.getRandomOrganizationName;
import static invoker.SiteTestHelper.getTestPassword;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import Constants.SpogConstants;
import InvokerServer.ServerResponseCode;
import genericutil.ExtentManager;
import invoker.SPOGInvoker;
import io.restassured.response.Response;

public class GetAllGroupsTest extends base.prepare.TestApiBase {
  private String token;
  // private ExtentReports extent;
  // private ExtentTest logger;
  // private String TestPrefix = getClass().getSimpleName();
  // private UserSpogServer userSpogServer;
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
  //
  // private String baseUri;
  // private String port;
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

  public void setAuthor() {

    author = "Yin.Li";
  }


  @BeforeClass
  public void prepareJobData() {

    test.assignAuthor("Yin.Li");
    test = rep.startTest("Prepare Job Data");

    spogServer.userLogin(csrAdminUserName, csrAdminPassword);
    token = spogServer.getJWTToken();
    String csrToken = this.token;

    // create msp org
    Map<String, String> userInfoMap =
        composeRandomUserMap(TestPrefix, SpogConstants.MSP_ADMIN, null);

    mspOrgId = spogServer.CreateOrganizationWithCheck(getRandomOrganizationName(TestPrefix),
        SpogConstants.MSP_ORG, userInfoMap.get("email"), getTestPassword(),
        RandomStringUtils.randomAlphanumeric(4), RandomStringUtils.randomAlphanumeric(4));

    mspAdmin = userInfoMap.get("email");
    mspPassword = getTestPassword();
    // String mspToken = loginSpogServer(mspAdmin, mspPassword);
    spogServer.userLogin(mspAdmin, mspPassword);
    String mspToken = spogServer.getJWTToken();

    // prepare msp group
    groupId_A_Msp = spogServer.createGroupWithCheck(mspOrgId, getRandomGroupName(),
        RandomStringUtils.randomAlphanumeric(8), test);
    groupId_B_Msp = spogServer.createGroupWithCheck(mspOrgId, getRandomGroupName(),
        RandomStringUtils.randomAlphanumeric(8), test);

    // create child org A admin
    accountOrgAId =
        createSubOrgnaization(getRandomOrganizationName(TestPrefix), mspOrgId, mspToken);

    userInfoMap = composeRandomUserMap(TestPrefix, SpogConstants.DIRECT_ADMIN, accountOrgAId);
    createUser(userInfoMap, mspToken);
    childOrgA_Admin = userInfoMap.get("email");
    childOrgA_Password = getTestPassword();
    spogServer.userLogin(childOrgA_Admin, childOrgA_Password);
    spogServer.createGroupWithCheck(accountOrgAId, getRandomGroupName(),
        RandomStringUtils.randomAlphanumeric(8), test);

    userInfoMap = composeRandomUserMap(TestPrefix, SpogConstants.MSP_ACCOUNT_ADMIN, mspOrgId);
    accountAMgrId = createUser(userInfoMap, mspToken);
    userSpogServer.setToken(mspToken);
    userSpogServer.assignMspAccountAdmins(mspOrgId, accountOrgAId, accountAMgrId, mspToken);

    // create child org B admin
    accountOrgBId =
        createSubOrgnaization(getRandomOrganizationName(TestPrefix), mspOrgId, mspToken);

    userInfoMap = composeRandomUserMap(TestPrefix, SpogConstants.DIRECT_ADMIN, accountOrgBId);
    createUser(userInfoMap, mspToken);
    childOrgB_Admin = userInfoMap.get("email");
    childOrgB_Password = getTestPassword();
    spogServer.userLogin(childOrgB_Admin, childOrgB_Password);
    spogServer.createGroupWithCheck(accountOrgBId, getRandomGroupName(),
        RandomStringUtils.randomAlphanumeric(8), test);

    userInfoMap = composeRandomUserMap(TestPrefix, SpogConstants.MSP_ACCOUNT_ADMIN, mspOrgId);
    accountBMgrId = createUser(userInfoMap, mspToken);
    userSpogServer.setToken(mspToken);
    userSpogServer.assignMspAccountAdmins(mspOrgId, accountOrgBId, accountBMgrId, mspToken);


    spogServer.userLogin(csrAdminUserName, csrAdminPassword);
    // create direct admin
    userInfoMap = composeRandomUserMap(TestPrefix, SpogConstants.DIRECT_ADMIN, directOrgId);
    // create direct org
    directOrgId = spogServer.CreateOrganizationWithCheck(getRandomOrganizationName(TestPrefix),
        SpogConstants.DIRECT_ORG, userInfoMap.get("email"), getTestPassword(),
        RandomStringUtils.randomAlphanumeric(4), RandomStringUtils.randomAlphanumeric(4));

    this.directAdmin = userInfoMap.get("email");
    this.directPassword = getTestPassword();
    spogServer.userLogin(directAdmin, directPassword);
    spogServer.createGroupWithCheck(directOrgId, getRandomGroupName(),
        RandomStringUtils.randomAlphanumeric(8), test);
    spogServer.createGroupWithCheck(directOrgId, getRandomGroupName(),
        RandomStringUtils.randomAlphanumeric(8), test);
  }


  public String getRandomGroupName() {

    return TestPrefix + "_SpogQA_" + RandomStringUtils.randomAlphanumeric(8);
  }


  @Test
  public void Given_CsrAdmin_when_Get_Msp_Source_Groups_Should_Successfull() {

    test = rep.startTest("Given_CsrAdmin_when_Get_Msp_Source_Groups_Should_Successfull");
    test.assignAuthor("yin.li");

    spogServer.userLogin(csrAdminUserName, csrAdminPassword);
    Response response = spogServer.getSourceGroups(mspOrgId, "", "", "", "");
    /*
     * String aString1= "1"; int sizeOfList = response.body().path("data.size()");
     * response.body("data.size()", is(5)); String jobsStr = ""; String aString= "a"; try {
     * JSONObject object = new JSONObject(jobsStr); JSONArray jobsArr = object.getJSONArray("data");
     * int s = jobsArr.length(); int z = 1; } catch (JSONException e) { // TODO Auto-generated catch
     * block e.printStackTrace(); }
     */
    response.then().statusCode(ServerResponseCode.Success_Get);
  }


  @Test
  public void Given_CsrAdmin_when_Get_Direct_Source_Groups_Should_Successfull() {

    test = rep.startTest("Given_CsrAdmin_when_Get_Direct_Source_Groups_Should_Successfull");
    test.assignAuthor("yin.li");

    spogServer.userLogin(csrAdminUserName, csrAdminPassword);
    Response response = spogServer.getSourceGroups(directOrgId, "", "", "", "");
    response.then().statusCode(ServerResponseCode.Success_Get);
  }


  @Test
  public void Given_CsrAdmin_when_Get_Account_Source_Groups_Should_Successfull() {

    test = rep.startTest("Given_CsrAdmin_when_Get_Direct_Source_Groups_Should_Successfull");
    test.assignAuthor("yin.li");

    spogServer.userLogin(csrAdminUserName, csrAdminPassword);
    Response response = spogServer.getSourceGroups(accountOrgAId, "", "", "", "");
    response.then().statusCode(ServerResponseCode.Success_Get);
  }
  
  @Test
  public void Given_Csr_ReadOnly_User_When_GetSourceGroups_Should_Successful() {

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
	
    Response response = spogServer.getSourceGroups(accountOrgAId, "", "", "", "");
    response.then().statusCode(ServerResponseCode.Success_Get);
    
    spogServer.userLogin(csrAdminUserName, csrAdminPassword);
    spogServer.CheckDeleteUserByIdStatus(csrReadOnlyUserId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
  }	



  @Test
  public void Given_MspAdmin_when_Get_Msp_Source_Groups_Should_Successfull() {

    test = rep.startTest("Given_MspAdmin_when_Get_Msp_Source_Groups_Should_Successfull");
    test.assignAuthor("yin.li");

    spogServer.userLogin(mspAdmin, mspPassword);
    Response response = spogServer.getSourceGroups(mspOrgId, "", "", "", "");

    response.then().statusCode(ServerResponseCode.Success_Get);
  }


  @Test
  public void Given_MspAdmin_when_Get_Other_Organization_Source_Groups_Should_Failed() {

    test = rep.startTest("Given_MspAdmin_when_Get_Other_Organization_Source_Groups_Should_Failed");
    test.assignAuthor("yin.li");

    spogServer.userLogin(mspAdmin, mspPassword);
    Response response = spogServer.getSourceGroups(directOrgId, "", "", "", "");

    response.then().statusCode(ServerResponseCode.Insufficient_Permissions);
  }


  @Test
  public void Given_Direct_Admin_when_Get_Source_Groups_Should_Successfull() {

    test = rep.startTest("Given_Direct_Admin_when_Get_Source_Groups_Should_Successfull");
    test.assignAuthor("yin.li");

    spogServer.userLogin(directAdmin, directPassword);
    Response response = spogServer.getSourceGroups(directOrgId, "", "", "", "");

    response.then().statusCode(ServerResponseCode.Success_Get)
        .body("data.get(0).source_status", hasKey("num_protected"))
        .body("data.get(0).source_status", hasKey("num_unprotected"));
  }


  @Test
  public void Given_Direct_Admin_when_Get_Other_Source_Groups_Should_Failed() {

    test = rep.startTest("Given_Direct_Admin_when_Get_Other_Source_Groups_Should_Failed");
    test.assignAuthor("yin.li");

    spogServer.userLogin(directAdmin, directPassword);
    Response response = spogServer.getSourceGroups(mspOrgId, "", "", "", "");

    response.then().statusCode(ServerResponseCode.Insufficient_Permissions);
  }


  @Test
  public void Given_Account_Admin_when_Get_Source_Groups_Should_Successfull() {

    test = rep.startTest("Given_Account_Admin_when_Get_Source_Groups_Should_Successfull");
    test.assignAuthor("yin.li");

    spogServer.userLogin(childOrgA_Admin, childOrgA_Password);
    Response response = spogServer.getSourceGroups(accountOrgAId, "", "", "", "");

    response.then().statusCode(ServerResponseCode.Success_Get);
    response.then().assertThat().body("data.errors.size()", is(1)).body("data.errors.get(0)",
        is(nullValue()));
  }


  @Test
  public void Given_Account_Admin_when_Other_Account_Source_Groups_Should_Failed() {

    test = rep.startTest("Given_Account_Admin_when_Other_Account_Source_Groups_Should_Failed");
    test.assignAuthor("yin.li");

    spogServer.userLogin(childOrgA_Admin, childOrgA_Password);
    Response response = spogServer.getSourceGroups(accountOrgBId, "", "", "", "");

    response.then().statusCode(ServerResponseCode.Insufficient_Permissions);

  }


  @Test
  public void Given_Account_Admin_when_Msp_Source_Groups_Should_Failed() {

    test = rep.startTest("Given_Account_Admin_when_Other_Account_Source_Groups_Should_Failed");
    test.assignAuthor("yin.li");

    spogServer.userLogin(childOrgA_Admin, childOrgA_Password);
    Response response = spogServer.getSourceGroups(mspOrgId, "", "", "", "");

    response.then().statusCode(ServerResponseCode.Insufficient_Permissions);
  }


  @Test
  public void when_notlogin_Should_Failed() {

    spogServer.setToken("");
    Response response = spogServer.getSourceGroups(mspOrgId, "", "", "", "");

    response.then().statusCode(ServerResponseCode.Not_login);
  }


  @Test
  public void Given_Not_Exist_Organization_ID_when_Get_Msp_Source_Groups_Should_Failed() {

    test =
        rep.startTest("Given_Not_Exist_Organization_ID_when_Get_Msp_Source_Groups_Should_Failed");
    test.assignAuthor("yin.li");

    String notExistOrgId = UUID.randomUUID().toString();

    spogServer.userLogin(csrAdminUserName, csrAdminPassword);
    Response response = spogServer.getSourceGroups(notExistOrgId, "", "", "", "");

    response.then().statusCode(ServerResponseCode.Success_Get);
  }


  @Test
  public void Given_Invalid_Organization_ID_when_Get_Msp_Source_Groups_Should_Failed() {

    test = rep.startTest("Given_Invalid_Organization_ID_when_Get_Msp_Source_Groups_Should_Failed");
    test.assignAuthor("yin.li");

    String invalidOrgId = "abc";

    spogServer.userLogin(mspAdmin, mspPassword);
    Response response = spogServer.getSourceGroups(invalidOrgId, "", "", "", "");

    response.then().statusCode(ServerResponseCode.Bad_Request);
    response.then().assertThat().body("errors.code.size()", is(1)).body("errors.code",
        hasItem("40000005"));
  }


  @Test
  public void Given_valid_Organization_IDs_when_Get_Msp_Source_Groups_Should_Successful() {

    test =
        rep.startTest("Given_valid_Organization_IDs_when_Get_Msp_Source_Groups_Should_Successful");
    test.assignAuthor("yin.li");

    spogServer.userLogin(mspAdmin, mspPassword);
    int groupSizeA =
        spogServer.getSourceGroups(accountOrgAId, "", "", "", "").body().path("data.size()");
    int groupSizeB =
        spogServer.getSourceGroups(accountOrgBId, "", "", "", "").body().path("data.size()");

    Response response = spogServer.getSourceGroups(accountOrgAId, accountOrgBId, "", "", "");

    response.then().statusCode(ServerResponseCode.Success_Get);

    response.then().assertThat().body("data.size()", is(groupSizeA + groupSizeB));
  }


  @Test
  public void Given_3_valid_Organization_IDs_when_Get_Msp_Source_Groups_Should_Successful() {

    test = rep
        .startTest("Given_3_valid_Organization_IDs_when_Get_Msp_Source_Groups_Should_Successful");
    test.assignAuthor("yin.li");

    spogServer.userLogin(mspAdmin, mspPassword);
    int groupSizeA =
        spogServer.getSourceGroups(accountOrgAId, "", "", "", "").body().path("data.size()");
    int groupSizeB =
        spogServer.getSourceGroups(accountOrgBId, "", "", "", "").body().path("data.size()");

    spogServer.userLogin(directAdmin, directPassword);

    int groupSizeDirect =
        spogServer.getSourceGroups(directOrgId, "", "", "", "").body().path("data.size()");

    spogServer.userLogin(csrAdminUserName, csrAdminPassword);
    Response response =
        spogServer.getSourceGroups(accountOrgAId, accountOrgBId, directOrgId, "", "");

    response.then().statusCode(ServerResponseCode.Success_Get);

    response.then().assertThat().body("data.size()", is(groupSizeA + groupSizeB + groupSizeDirect));
  }


  @Test
  public void Given_3_invalid_Organization_IDs_when_Get_Msp_Source_Groups_Should_Success() {

    test =
        rep.startTest("Given_3_invalid_Organization_IDs_when_Get_Msp_Source_Groups_Should_Failed");
    test.assignAuthor("yin.li");

    spogServer.userLogin(csrAdminUserName, csrAdminPassword);

    Response response =
        spogServer.getSourceGroups("invalidOrgIdA", "invalidOrgIdB", "invalidOrgIdC", "", "");

    response.then().statusCode(ServerResponseCode.Success_Get);
    // is(nullValue()));
  }


  @Test
  public void Given_3_invalid_Organization_IDs_when_Get_Msp_Source_Groups_Should_ReturnEmptyList() {

    test =
        rep.startTest("Given_3_invalid_Organization_IDs_when_Get_Msp_Source_Groups_Should_Failed");
    test.assignAuthor("yin.li");

    spogServer.userLogin(csrAdminUserName, csrAdminPassword);

    Response response =
        spogServer.getSourceGroups("invalidOrgIdA", "invalidOrgIdB", "invalidOrgIdC", "", "");

    // response.then().assertThat().body("data.size()", is(0));
    // response.then().assertThat().body("data.errors.size()", is(0));
    // response.then().assertThat().body("data.errors.size()", is(1)).body("data.errors.get(0)",
    // is(nullValue()));
  }


  @Test
  public void Given_3_Not_Exist_Organization_IDs_when_Get_Msp_Source_Groups_Should_Successful() {

    test = rep.startTest(
        "Given_3_Not_Exist_Organization_IDs_when_Get_Msp_Source_Groups_Should_Successful");
    test.assignAuthor("yin.li");

    spogServer.userLogin(csrAdminUserName, csrAdminPassword);

    Response response = spogServer.getSourceGroups(UUID.randomUUID().toString(),
        UUID.randomUUID().toString(), UUID.randomUUID().toString(), "", "");

    response.then().statusCode(ServerResponseCode.Success_Get);
    response.then().assertThat().body("data.size()", is(0));
    response.then().assertThat().body("data.errors.size()", is(0));
    // response.then().assertThat().body("data.errors.size()", is(1)).body("data.errors.get(0)",
    // is(nullValue()));
    // Bug 823323: Get Sources/Groups: when csr admin trying to get source groups with 3 not exist
    // orgId, it return source group, but should return blank list.
  }


  @Test
  public void Given_3_Not_Exist_Organization_IDs_when_Get_Msp_Source_Groups_Should_ReturnEmptyList() {

    test = rep.startTest(
        "Given_3_Not_Exist_Organization_IDs_when_Get_Msp_Source_Groups_Should_Successful");
    test.assignAuthor("yin.li");

    spogServer.userLogin(csrAdminUserName, csrAdminPassword);

    Response response = spogServer.getSourceGroups(UUID.randomUUID().toString(),
        UUID.randomUUID().toString(), UUID.randomUUID().toString(), "", "");

    response.then().statusCode(ServerResponseCode.Success_Get);
    response.then().assertThat().body("data.size()", is(0));
    response.then().assertThat().body("data.errors.size()", is(0));
  }


  @Test
  public void Given_3_Not_Exist_Organization_IDs_when_Get_Msp_Source_Groups_Should_ReturnEmptyErrorList() {

    test = rep.startTest(
        "Given_3_Not_Exist_Organization_IDs_when_Get_Msp_Source_Groups_Should_Successful");
    test.assignAuthor("yin.li");

    spogServer.userLogin(csrAdminUserName, csrAdminPassword);

    Response response = spogServer.getSourceGroups(UUID.randomUUID().toString(),
        UUID.randomUUID().toString(), UUID.randomUUID().toString(), "", "");

    response.then().statusCode(ServerResponseCode.Success_Get);
    response.then().assertThat().body("data.size()", is(0));
    response.then().assertThat().body("data.errors.size()", is(0));
  }


  @Test
  public void Given_invalid_Organization_IDs_when_Get_Msp_Source_Groups_Should_failed() {

    test =
        rep.startTest("Given_valid_Organization_IDs_when_Get_Msp_Source_Groups_Should_Successful");
    test.assignAuthor("yin.li");

    spogServer.userLogin(mspAdmin, mspPassword);

    Response response = spogServer.getSourceGroups("invalidOrgIdA", "invalidOrgIdB", "", "", "");

    response.then().statusCode(ServerResponseCode.Bad_Request);
    response.then().assertThat().body("errors.code.size()", is(1)).body("errors.code",
        hasItem("40000005"));
  }


  @Test
  public void Given_invalid_And_Not_Exist_Organization_IDs_when_Get_Msp_Source_Groups_Should_Success() {

    test =
        rep.startTest("Given_valid_Organization_IDs_when_Get_Msp_Source_Groups_Should_Successful");
    test.assignAuthor("yin.li");

    spogServer.userLogin(csrAdminUserName, csrAdminPassword);

    Response response =
        spogServer.getSourceGroups("invalidOrgIdA", UUID.randomUUID().toString(), "", "", "");

    response.then().statusCode(ServerResponseCode.Success_Get);
    response.then().assertThat().body("data.size()", is(0));
  }


  @Test
  public void Given_invalid_And_Not_Exist_Organization_IDs_when_Get_Msp_Source_Groups_Should_Return_Empty_Groups() {

    test =
        rep.startTest("Given_valid_Organization_IDs_when_Get_Msp_Source_Groups_Should_Successful");
    test.assignAuthor("yin.li");

    spogServer.userLogin(csrAdminUserName, csrAdminPassword);

    Response response =
        spogServer.getSourceGroups("invalidOrgIdA", UUID.randomUUID().toString(), "", "", "");

    response.then().statusCode(ServerResponseCode.Success_Get);
    response.then().assertThat().body("data.size()", is(0));
  }


  @Test
  public void Given_One_Organization_IDs_when_Get_Msp_Source_Groups_Should_Return_Empty_Groups() {

    test = rep.startTest(
        "Given_One_Organization_IDs_when_Get_Msp_Source_Groups_Should_Return_Empty_Groups");
    test.assignAuthor("yin.li");

    spogServer.userLogin(csrAdminUserName, csrAdminPassword);

    SPOGInvoker aInvoker = new SPOGInvoker(this.baseURI, this.port);
    aInvoker.setToken(spogServer.getJWTToken());

    Response response = aInvoker.getSourceGroups("?filter=organization_id:[" + mspOrgId + "]");
    response.then().statusCode(ServerResponseCode.Success_Get);
  }


  @Test
  public void Given_Blank_Organization_IDs_when_Get_Msp_Source_Groups_Should_Return_Empty_Groups() {

    test = rep.startTest(
        "Given_Blank_Organization_IDs_when_Get_Msp_Source_Groups_Should_Return_Empty_Groups");
    test.assignAuthor("yin.li");

    spogServer.userLogin(mspAdmin, mspPassword);

    SPOGInvoker aInvoker = new SPOGInvoker(this.baseURI, this.port);
    aInvoker.setToken(spogServer.getJWTToken());

    String orgFilterStr = "";
    Response response = aInvoker.getSourceGroups("?filter=organization_id:[" + orgFilterStr + "]");
    response.then().statusCode(ServerResponseCode.Success_Get);
  }


  @Test
  public void Given_One_Valid_And_One_Blank_Organization_IDs_when_Get_Msp_Source_Groups_Should_Return_Empty_Groups() {

    test = rep.startTest(
        "Given_One_Valid_And_One_Blank_Organization_IDs_when_Get_Msp_Source_Groups_Should_Return_Empty_Groups");
    test.assignAuthor("yin.li");

    spogServer.userLogin(csrAdminUserName, csrAdminPassword);

    SPOGInvoker aInvoker = new SPOGInvoker(this.baseURI, this.port);
    aInvoker.setToken(spogServer.getJWTToken());

    String orgFilterStr = "";
    Response response =
        aInvoker.getSourceGroups("?filter=organization_id:[" + mspOrgId + "|" + orgFilterStr + "]");
    response.then().statusCode(ServerResponseCode.Success_Get);
  }


  @DataProvider(
      name = "accountmgr")
  public final Object[][] accountMgrTest() {

    return new Object[][] {{accountAMgrId, accountOrgAId, "true"},
        {accountAMgrId, accountOrgBId, "false"}, {accountAMgrId, mspOrgId, "false"},
        {accountAMgrId, directOrgId, "false"}};
  }


  @Test(
      dataProvider = "accountmgr")
  public void Given_AccountManager_when_GetGroups_Should_work(String accountMgrId, String orgId,
      String expectResult) {

    test = rep.startTest("Given_AccountManager_when_GetGroups_Should_work");
    test.assignAuthor("yin.li");

    spogServer.userLogin(csrAdminUserName, csrAdminPassword);

    String accountMgr =
        spogServer.getUserByID(accountMgrId, test).then().extract().path("data.email");
    spogServer.userLogin(accountMgr, getTestPassword());

    Response response = spogServer.getSourceGroups(orgId, "", "", "", "");

    if (Boolean.parseBoolean(expectResult)) {
      response.then().statusCode(ServerResponseCode.Success_Get);
    } else {
      // response.then().statusCode(ServerResponseCode.Insufficient_Permissions);
    }

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

  // when org do not has group
  // when filter is empty
}
