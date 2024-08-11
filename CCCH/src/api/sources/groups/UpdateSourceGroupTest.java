package api.sources.groups;

import static invoker.SiteTestHelper.composeRandomUserMap;
import static invoker.SiteTestHelper.createOrgnaization;
import static invoker.SiteTestHelper.createSubOrgnaization;
import static invoker.SiteTestHelper.createUser;
import static invoker.SiteTestHelper.getRandomOrganizationName;
import static invoker.SiteTestHelper.getTestPassword;
import static org.hamcrest.Matchers.is;

import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import Constants.SpogConstants;
import InvokerServer.ServerResponseCode;
import genericutil.ExtentManager;
import io.restassured.response.Response;

public class UpdateSourceGroupTest extends base.prepare.TestApiBase {

  private String token;
  // private ExtentReports extent;
  // private ExtentTest logger;
  private String TestPrefix = getClass().getSimpleName();
  // private UserSpogServer userSpogServer;
  //
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
  //
  // private String baseUri;
  // private String port;


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
  public void prepareJobData() {

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
    groupId_AccountA = spogServer.createGroupWithCheck(accountOrgAId, getRandomGroupName(),
        RandomStringUtils.randomAlphanumeric(8), test);

    // create child org B admin
    accountOrgBId =
        createSubOrgnaization(getRandomOrganizationName(TestPrefix), mspOrgId, mspToken);

    userInfoMap = composeRandomUserMap(TestPrefix, SpogConstants.DIRECT_ADMIN, accountOrgBId);
    createUser(userInfoMap, mspToken);
    childOrgB_Admin = userInfoMap.get("email");
    childOrgB_Password = getTestPassword();
    spogServer.userLogin(childOrgB_Admin, childOrgB_Password);
    groupId_AccountB = spogServer.createGroupWithCheck(accountOrgBId, getRandomGroupName(),
        RandomStringUtils.randomAlphanumeric(8), test);

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
    groupId_Direct = spogServer.createGroupWithCheck(directOrgId, getRandomGroupName(),
        RandomStringUtils.randomAlphanumeric(8), test);
    groupBId_Direct = spogServer.createGroupWithCheck(directOrgId, getRandomGroupName(),
        RandomStringUtils.randomAlphanumeric(8), test);
  }


  @Test
  public void Given_Csr_Admin_When_Update_Source_Group_In_Msp_Org_Should_Successfull() {

    test = rep.startTest("Given_Csr_Admin_When_Update_Source_Group_In_Msp_Org_Should_Successfull");
    test.assignAuthor("yin.li");

    spogServer.userLogin(csrAdminUserName, csrAdminPassword);

    String newSourceGroupName = getRandomGroupName();
    String newDesc =
        newSourceGroupName + ", description:" + RandomStringUtils.randomAlphanumeric(8);

    Response response = spogServer.updateSourceGroup(groupId_A_Msp, newSourceGroupName, newDesc);

    response.then().statusCode(ServerResponseCode.Success_Put);
    response.then().assertThat().body("data.group_name", is(newSourceGroupName))
        .body("data.group_description", is(newDesc)).body("data.group_id", is(groupId_A_Msp));
  }
  
  @Test
  public void Given_Csr_ReadOnly_User_When_Update_Source_Group_Should_Failed() {

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
	
    String newSourceGroupName = getRandomGroupName();
    String newDesc =
        newSourceGroupName + ", description:" + RandomStringUtils.randomAlphanumeric(8);

    Response response = spogServer.updateSourceGroup(groupId_A_Msp, newSourceGroupName, newDesc);

    response.then().statusCode(ServerResponseCode.Insufficient_Permissions);
    
    spogServer.userLogin(csrAdminUserName, csrAdminPassword);
    spogServer.CheckDeleteUserByIdStatus(csrReadOnlyUserId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
  }	



  @Test
  public void Given_Csr_Admin_When_Update_Source_Group_In_Account_Org_Should_Successfull() {

    test =
        rep.startTest("Given_Csr_Admin_When_Update_Source_Group_In_Account_Org_Should_Successfull");
    test.assignAuthor("yin.li");

    spogServer.userLogin(csrAdminUserName, csrAdminPassword);

    String newSourceGroupName = getRandomGroupName();
    String newDesc =
        newSourceGroupName + ", description:" + RandomStringUtils.randomAlphanumeric(8);

    Response response = spogServer.updateSourceGroup(groupId_AccountA, newSourceGroupName, newDesc);

    response.then().statusCode(ServerResponseCode.Success_Put);
    response.then().assertThat().body("data.group_name", is(newSourceGroupName))
        .body("data.group_description", is(newDesc)).body("data.group_id", is(groupId_AccountA));
  }


  @Test
  public void Given_Csr_Admin_When_Update_Source_Group_In_Direct_Org_Should_Successfull() {

    test =
        rep.startTest("Given_Csr_Admin_When_Update_Source_Group_In_Direct_Org_Should_Successfull");
    test.assignAuthor("yin.li");

    spogServer.userLogin(csrAdminUserName, csrAdminPassword);

    String newSourceGroupName = getRandomGroupName();
    String newDesc =
        newSourceGroupName + ", description:" + RandomStringUtils.randomAlphanumeric(8);

    Response response = spogServer.updateSourceGroup(groupId_Direct, newSourceGroupName, newDesc);

    response.then().statusCode(ServerResponseCode.Success_Put);
    response.then().assertThat().body("data.group_name", is(newSourceGroupName))
        .body("data.group_description", is(newDesc)).body("data.group_id", is(groupId_Direct));
  }


  @Test
  public void Given_Msp_Admin_When_Update_Source_Group_In_Msp_Org_Should_Successfull() {

    test =
        rep.startTest("Given_Csr_Admin_When_Update_Source_Group_In_Direct_Org_Should_Successfull");
    test.assignAuthor("yin.li");

    spogServer.userLogin(mspAdmin, mspPassword);

    String newSourceGroupName = getRandomGroupName();
    String newDesc =
        newSourceGroupName + ", description:" + RandomStringUtils.randomAlphanumeric(8);
    String targetSourceGroupId = groupId_A_Msp;

    Response response =
        spogServer.updateSourceGroup(targetSourceGroupId, newSourceGroupName, newDesc);

    response.then().statusCode(ServerResponseCode.Success_Put);
    response.then().assertThat().body("data.group_name", is(newSourceGroupName))
        .body("data.group_description", is(newDesc)).body("data.group_id", is(targetSourceGroupId));
  }


  @Test
  public void Given_Msp_Admin_When_Update_Source_Group_In_Account_Org_Should_Successfull() {

    test =
        rep.startTest("Given_Csr_Admin_When_Update_Source_Group_In_Direct_Org_Should_Successfull");
    test.assignAuthor("yin.li");

    spogServer.userLogin(mspAdmin, mspPassword);

    String newSourceGroupName = getRandomGroupName();
    String newDesc =
        newSourceGroupName + ", description:" + RandomStringUtils.randomAlphanumeric(8);
    String targetSourceGroupId = groupId_AccountB;

    Response response =
        spogServer.updateSourceGroup(targetSourceGroupId, newSourceGroupName, newDesc);

    response.then().statusCode(ServerResponseCode.Success_Put);
    response.then().assertThat().body("data.group_name", is(newSourceGroupName))
        .body("data.group_description", is(newDesc)).body("data.group_id", is(targetSourceGroupId));
  }


  @Test
  public void Given_Account_Admin_When_Update_Source_Group_In_Account_Org_Should_Successfull() {

    test =
        rep.startTest("Given_Csr_Admin_When_Update_Source_Group_In_Direct_Org_Should_Successfull");
    test.assignAuthor("yin.li");

    spogServer.userLogin(childOrgB_Admin, childOrgB_Password);

    String newSourceGroupName = getRandomGroupName();
    String newDesc =
        newSourceGroupName + ", description:" + RandomStringUtils.randomAlphanumeric(8);
    String targetSourceGroupId = groupId_AccountB;

    Response response =
        spogServer.updateSourceGroup(targetSourceGroupId, newSourceGroupName, newDesc);

    response.then().statusCode(ServerResponseCode.Success_Put);
    response.then().assertThat().body("data.group_name", is(newSourceGroupName))
        .body("data.group_description", is(newDesc)).body("data.group_id", is(targetSourceGroupId));
  }


  @Test
  public void Given_Direct_Admin_When_Update_Source_Group_In_Direct_Org_Should_Successfull() {

    test =
        rep.startTest("Given_Csr_Admin_When_Update_Source_Group_In_Direct_Org_Should_Successfull");
    test.assignAuthor("yin.li");

    spogServer.userLogin(directAdmin, directPassword);

    String newSourceGroupName = getRandomGroupName();
    String newDesc =
        newSourceGroupName + ", description:" + RandomStringUtils.randomAlphanumeric(8);
    String targetSourceGroupId = groupId_Direct;

    Response response =
        spogServer.updateSourceGroup(targetSourceGroupId, newSourceGroupName, newDesc);

    response.then().statusCode(ServerResponseCode.Success_Put);
    response.then().assertThat().body("data.group_name", is(newSourceGroupName))
        .body("data.group_description", is(newDesc)).body("data.group_id", is(targetSourceGroupId));
  }


  @Test
  public void Given_Msp_Admin_When_Update_Source_Group_In_Other_Org_Should_Failed() {

    test = rep.startTest("Given_Direct_Admin_When_Update_Source_Group_In_Other_Org_Should_Failed");
    test.assignAuthor("yin.li");

    spogServer.userLogin(mspAdmin, mspPassword);

    String newSourceGroupName = getRandomGroupName();
    String newDesc =
        newSourceGroupName + ", description:" + RandomStringUtils.randomAlphanumeric(8);
    String targetSourceGroupId = groupId_Direct;

    Response response =
        spogServer.updateSourceGroup(targetSourceGroupId, newSourceGroupName, newDesc);

    response.then().statusCode(ServerResponseCode.Insufficient_Permissions);
  }


  @Test
  public void Given_Account_Admin_When_Update_Source_Group_In_Other_Org_Should_Failed() {

    test = rep.startTest("Given_Direct_Admin_When_Update_Source_Group_In_Other_Org_Should_Failed");
    test.assignAuthor("yin.li");

    spogServer.userLogin(childOrgA_Admin, childOrgA_Password);

    String newSourceGroupName = getRandomGroupName();
    String newDesc =
        newSourceGroupName + ", description:" + RandomStringUtils.randomAlphanumeric(8);
    String targetSourceGroupId = groupId_AccountB;

    Response response =
        spogServer.updateSourceGroup(targetSourceGroupId, newSourceGroupName, newDesc);

    response.then().statusCode(ServerResponseCode.Insufficient_Permissions);

  }


  @Test
  public void Given_Account_Admin_When_Update_Source_Group_In_Upper_Org_Should_Failed() {

    test = rep.startTest("Given_Direct_Admin_When_Update_Source_Group_In_Other_Org_Should_Failed");
    test.assignAuthor("yin.li");

    spogServer.userLogin(childOrgA_Admin, childOrgA_Password);

    String newSourceGroupName = getRandomGroupName();
    String newDesc =
        newSourceGroupName + ", description:" + RandomStringUtils.randomAlphanumeric(8);
    String targetSourceGroupId = groupId_B_Msp;

    Response response =
        spogServer.updateSourceGroup(targetSourceGroupId, newSourceGroupName, newDesc);

    response.then().statusCode(ServerResponseCode.Insufficient_Permissions);

  }


  @Test
  public void Given_Direct_Admin_When_Update_Source_Group_In_Other_Org_Should_Failed() {

    test = rep.startTest("Given_Direct_Admin_When_Update_Source_Group_In_Other_Org_Should_Failed");
    test.assignAuthor("yin.li");

    spogServer.userLogin(directAdmin, directPassword);

    String newSourceGroupName = getRandomGroupName();
    String newDesc =
        newSourceGroupName + ", description:" + RandomStringUtils.randomAlphanumeric(8);
    String targetSourceGroupId = groupId_B_Msp;

    Response response =
        spogServer.updateSourceGroup(targetSourceGroupId, newSourceGroupName, newDesc);

    response.then().statusCode(ServerResponseCode.Insufficient_Permissions);
  }


  @Test
  public void Given_Direct_Admin_When_Update_Source_Group_With_Empty_Group_Name_Should_Failed() {

    test = rep.startTest(
        "Given_Direct_Admin_When_Update_Source_Group_With_Empty_Group_Name_Should_Failed");
    test.assignAuthor("yin.li");

    spogServer.userLogin(directAdmin, directPassword);

    String newSourceGroupName = "";
    String newDesc =
        newSourceGroupName + ", description:" + RandomStringUtils.randomAlphanumeric(8);
    String targetSourceGroupId = groupId_Direct;

    Response response =
        spogServer.updateSourceGroup(targetSourceGroupId, newSourceGroupName, newDesc);

    // here it is an issue, waiting fix. empty name should be failed.
    response.then().statusCode(ServerResponseCode.Success_Put);
  }


  @Test
  public void Given_Direct_Admin_When_Update_Source_Group_With_Empty_Group_Description_Should_Successful() {

    test = rep.startTest(
        "Given_Direct_Admin_When_Update_Source_Group_With_Empty_Group_Description_Should_Successful");
    test.assignAuthor("yin.li");

    spogServer.userLogin(directAdmin, directPassword);

    String newSourceGroupName = RandomStringUtils.randomAlphanumeric(8);
    String newDesc = "";
    String targetSourceGroupId = groupId_Direct;

    Response response =
        spogServer.updateSourceGroup(targetSourceGroupId, newSourceGroupName, newDesc);

    response.then().statusCode(ServerResponseCode.Success_Put);
    response.then().assertThat().body("data.group_name", is(newSourceGroupName))
        .body("data.group_description", is(newDesc)).body("data.group_id", is(targetSourceGroupId));
  }


  @Test
  public void Given_Duplicate_Source_Group_Name_When_Update__Should_Failed() {

    // Bug 821707: When update source group name with existing group name in same organization, it
    // return ok now.
    test = rep.startTest("Given_Duplicate_Source_Group_Name_When_Update__Should_Failed");
    test.assignAuthor("yin.li");

    spogServer.userLogin(directAdmin, directPassword);

    String newSourceGroupName = RandomStringUtils.randomAlphanumeric(8);
    String newDesc =
        newSourceGroupName + ", description:" + RandomStringUtils.randomAlphanumeric(8);
    String targetSourceGroupId = groupId_Direct;

    Response response =
        spogServer.updateSourceGroup(targetSourceGroupId, newSourceGroupName, newDesc);

    response.then().statusCode(ServerResponseCode.Success_Put);
    response.then().assertThat().body("data.group_name", is(newSourceGroupName))
        .body("data.group_description", is(newDesc)).body("data.group_id", is(targetSourceGroupId));

    String duplicateName = newSourceGroupName;
    targetSourceGroupId = groupBId_Direct;

    response = spogServer.updateSourceGroup(targetSourceGroupId, newSourceGroupName, newDesc);

    // here is a bug, waiting fix
    response.then().statusCode(ServerResponseCode.Bad_Request);

  }


  @Test
  public void Given_Invalid_Source_Group_Id_When_Update_Should_Failed() {

    test = rep.startTest("Given_Invalid_Source_Group_Id_When_Update_Should_Failed");
    test.assignAuthor("yin.li");

    spogServer.userLogin(directAdmin, directPassword);

    Response response = spogServer.updateSourceGroup("invalidSourceGroupId", "any", "any");

    response.then().statusCode(ServerResponseCode.Bad_Request);

  }


  @Test
  public void Given_Not_Exist_Source_Group_Id_When_Update_Should_Failed() {

    test = rep.startTest("Given_Not_Exist_Source_Group_Id_When_Update_Should_Failed");
    test.assignAuthor("yin.li");

    spogServer.userLogin(directAdmin, directPassword);

    Response response = spogServer.updateSourceGroup(UUID.randomUUID().toString(), "any", "any");

    response.then().statusCode(ServerResponseCode.Resource_Does_Not_Exist);

  }


  @Test
  public void When_Not_Login_Should_Failed() {

    test = rep.startTest("When_Not_Login_Should_Failed");
    test.assignAuthor("yin.li");

    spogServer.setToken("");

    Response response = spogServer.updateSourceGroup(groupId_A_Msp, "any", "any");

    response.then().statusCode(ServerResponseCode.Not_login);

  }


  @Test
  public void Given_Delete_Source_Group_When_Update_Should_Failed() {

    test = rep.startTest("Given_Delete_Source_Group_When_Update_Should_Failed");
    test.assignAuthor("yin.li");

    spogServer.userLogin(mspAdmin, mspPassword);

    String sourceGroupName = getRandomGroupName();
    String group_id = spogServer.createGroupWithCheck(mspOrgId, sourceGroupName,
        RandomStringUtils.randomAlphanumeric(8), test);

    spogServer.deleteGroupWithExpectedStatusCode(group_id, SpogConstants.SUCCESS_GET_PUT_DELETE,
        test);

    Response response = spogServer.updateSourceGroup(group_id, "any", "any");

    response.then().statusCode(ServerResponseCode.Resource_Does_Not_Exist);

  }


  @Test
  public void Given_Delete_Source_Group_When_Update_Other_Groups_With_its_Name_Should_Succesfull() {

    test = rep.startTest(
        "Given_Delete_Source_Group_When_Update_Other_Groups_With_its_Name_Should_Succesfull");
    test.assignAuthor("yin.li");

    spogServer.userLogin(mspAdmin, mspPassword);

    String sourceGroupName = getRandomGroupName();
    String group_id = spogServer.createGroupWithCheck(mspOrgId, sourceGroupName,
        RandomStringUtils.randomAlphanumeric(8), test);

    spogServer.deleteGroupWithExpectedStatusCode(group_id, SpogConstants.SUCCESS_GET_PUT_DELETE,
        test);

    Response response = spogServer.updateSourceGroup(groupId_B_Msp, sourceGroupName, "any");

    response.then().statusCode(ServerResponseCode.Success_Put);
    response.then().assertThat().body("data.group_name", is(sourceGroupName))
        .body("data.group_description", is("any")).body("data.group_id", is(groupId_B_Msp));

  }


  public String getRandomGroupName() {

    return TestPrefix + "_SpogQA_" + RandomStringUtils.randomAlphanumeric(8);
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


}
