package test.restapi.site;

import static invoker.SiteTestHelper.composeRandomUserMap;
import static invoker.SiteTestHelper.createOrgnaization;
import static invoker.SiteTestHelper.createSite;
import static invoker.SiteTestHelper.createSubOrgnaization;
import static invoker.SiteTestHelper.createUser;
import static invoker.SiteTestHelper.deleteSite;
import static invoker.SiteTestHelper.getRandomOrganizationName;
import static invoker.SiteTestHelper.getRandomSiteName;
import static invoker.SiteTestHelper.getTestPassword;
import static invoker.SiteTestHelper.loginSpogServer;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import Constants.SpogConstants;
import InvokerServer.ServerResponseCode;
import genericutil.ExtentManager;
import invoker.SiteTestHelper.siteType;
import io.restassured.response.Response;

public class TestDeleteSite extends base.prepare.TestApiBase {
  private String        token;
  private static String TestDataPrefix = "TestDeleteSite";
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
  // private String NamePrefix = "TestDeleteSite";
  // private UserSpogServer userSpogServer;


  public void setAuthor() {

    author = "yin.li";
  }


  @BeforeClass
  public void initTestEnv() {

    this.token = spogServer.getJWTToken();
  }


  @Test
  public void Given_CsrAdmin_when_DeleteSiteOfMspOrgnization_Should_Successful() {

    String csrToken = this.token;
    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");
    // create a org
    String orgName = getRandomOrganizationName(TestDataPrefix);

    String orgId = createOrgnaization(orgName, SpogConstants.MSP_ORG,
        RandomStringUtils.randomAlphanumeric(8) + "@SpogQa",
        RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(4),
        RandomStringUtils.randomAlphanumeric(4), csrToken);


    // create a site for org
    String siteName = getRandomSiteName(TestDataPrefix);
    Response response = createSite(siteName, siteType.cloud_direct.toString(), orgId, csrToken);
    response.then().statusCode(ServerResponseCode.Success_Post);

    String siteId = response.then().extract().path("data.site_id");

    // delete site of org
    response = deleteSite(siteId, csrToken);
    response.then().log().all();
    response.then().statusCode(ServerResponseCode.Success_Delete);

  }


  @Test
  public void Given_CsrAdmin_when_DeleteSiteOfDirectOrgnization_Should_Successful() {

    String csrToken = this.token;
    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    // create a org
    String orgName = getRandomOrganizationName(TestDataPrefix);

    String orgId = createOrgnaization(orgName, SpogConstants.DIRECT_ORG,
        RandomStringUtils.randomAlphanumeric(8) + "@SpogQa",
        RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(4),
        RandomStringUtils.randomAlphanumeric(4), csrToken);


    // create a site for org
    String siteName = getRandomSiteName(TestDataPrefix);
    Response response = createSite(siteName, siteType.cloud_direct.toString(), orgId, csrToken);
    response.then().statusCode(ServerResponseCode.Success_Post);

    String siteId = response.then().extract().path("data.site_id");

    // delete site of org
    response = deleteSite(siteId, csrToken);
    response.then().log().all();
    response.then().statusCode(ServerResponseCode.Success_Delete);
  }
  
  @Test
  public void Given_Csr_ReadOnly_User_When_DeleteSite_Should_Failed() {

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
    
    String orgName = getRandomOrganizationName(TestDataPrefix);

    String orgId = createOrgnaization(orgName, SpogConstants.DIRECT_ORG,
        RandomStringUtils.randomAlphanumeric(8) + "@SpogQa",
        RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(4),
        RandomStringUtils.randomAlphanumeric(4), csrToken);

    // create a site for org
    String siteName = getRandomSiteName(TestDataPrefix);
    Response response = createSite(siteName, siteType.cloud_direct.toString(), orgId, csrToken);
    response.then().statusCode(ServerResponseCode.Success_Post);

    String siteId = response.then().extract().path("data.site_id");

    spogServer.userLogin(csrReadOnlyUser, password);
    
    // delete site of org
    response = deleteSite(siteId, spogServer.getJWTToken());
    response.then().log().all();
    response.then().statusCode(ServerResponseCode.Insufficient_Permissions);
        
    spogServer.userLogin(csrAdminUserName, csrAdminPassword);
    spogServer.CheckDeleteUserByIdStatus(csrReadOnlyUserId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
  }	


  @Test
  public void Given_MspAdmin_when_DeleteItsSite_Should_Successful() {

    String csrToken = this.token;
    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");
    // create a org
    String orgName = getRandomOrganizationName("TestCreateSite");

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

    // create a site for org as msp admin
    String siteName = getRandomSiteName("TestCreateSite");
    Response response = createSite(siteName, siteType.cloud_direct.toString(), "", mspToken);

    response.then().statusCode(ServerResponseCode.Success_Post);

    String siteId = response.then().extract().path("data.site_id");
    response = deleteSite(siteId, mspToken);

    response.then().statusCode(ServerResponseCode.Success_Delete);
  }


  @Test
  public void Given_MspAdmin_when_DeleteSiteInSubOrg_Should_Successful() {

    String csrToken = this.token;
    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");
    // create a org
    String orgName = getRandomOrganizationName("TestCreateSite");

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

    String subOrgId =
        createSubOrgnaization(getRandomOrganizationName("TestCreateSite"), orgId, mspToken);

    userInfoMap = composeRandomUserMap(TestDataPrefix, SpogConstants.DIRECT_ADMIN, subOrgId);
    createUser(userInfoMap, mspToken);
    String directAdmin = userInfoMap.get("email");
    String directPassword = getTestPassword();

    String directToken = loginSpogServer(directAdmin, directPassword);

    // create a site for org as msp admin
    String siteName = getRandomSiteName("TestCreateSite");
    Response response = createSite(siteName, siteType.cloud_direct.toString(), "", directToken);

    response.then().statusCode(ServerResponseCode.Success_Post);

    String siteId = response.then().extract().path("data.site_id");

    mspToken = loginSpogServer(mspAdmin, mspPassword);

    response = deleteSite(siteId, mspToken);

    response.then().statusCode(ServerResponseCode.Success_Delete);
  }


  @Test
  public void Given_MspAdmin_when_DeleteSiteInOtherOrg_Should_Failed() {

    String csrToken = this.token;
    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");
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
        composeRandomUserMap(TestDataPrefix, SpogConstants.MSP_ADMIN, orgIdA);
    createUser(userInfoMap, csrToken);
    String mspAAdmin = userInfoMap.get("email");
    String mspAPassword = getTestPassword();

    // login as msp admin
    String mspAToken = loginSpogServer(mspAAdmin, mspAPassword);

    // create a site for org as msp admin
    String siteName = getRandomSiteName("TestCreateSite");
    Response response = createSite(siteName, siteType.cloud_direct.toString(), orgIdB, csrToken);

    response.then().statusCode(ServerResponseCode.Success_Post);

    String siteId = response.then().extract().path("data.site_id");

    response = deleteSite(siteId, mspAToken);

    response.then().statusCode(ServerResponseCode.Insufficient_Permissions);
    response.then().assertThat().body("errors.code.size()", is(1)).body("errors.code",
        hasItem("00100101"));

  }


  @Test
  public void Given_DirectAdmin_when_DeleteSiteInOtherOrg_Should_Failed() {

    String csrToken = this.token;
    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");
    // create a org
    String orgName = getRandomOrganizationName("TestCreateSite");

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

    String subOrgId_A =
        createSubOrgnaization(getRandomOrganizationName("TestCreateSite"), orgId, mspToken);

    String subOrgId_B =
        createSubOrgnaization(getRandomOrganizationName("TestCreateSite"), orgId, mspToken);


    userInfoMap = composeRandomUserMap(TestDataPrefix, SpogConstants.DIRECT_ADMIN, subOrgId_A);
    createUser(userInfoMap, mspToken);
    String directAdmin_A = userInfoMap.get("email");
    String directPassword_A = getTestPassword();
    String directToken_A = loginSpogServer(directAdmin_A, directPassword_A);

    // create a site for org as msp admin
    String siteName_A = getRandomSiteName("TestCreateSite");
    Response response = createSite(siteName_A, siteType.cloud_direct.toString(), "", directToken_A);

    response.then().statusCode(ServerResponseCode.Success_Post);

    String siteId = response.then().extract().path("data.site_id");

    userInfoMap = composeRandomUserMap(TestDataPrefix, SpogConstants.DIRECT_ADMIN, subOrgId_B);
    createUser(userInfoMap, mspToken);
    String directAdmin_B = userInfoMap.get("email");
    String directPassword_B = getTestPassword();

    String directToken_B = loginSpogServer(directAdmin_B, directPassword_B);

    response = deleteSite(siteId, directToken_B);

    response.then().statusCode(ServerResponseCode.Insufficient_Permissions);
    response.then().assertThat().body("errors.code.size()", is(1)).body("errors.code",
        hasItem("00100101"));
  }


  @Test
  public void Given_MspAdmin_when_NotLogin_Should_Failed() {

    String csrToken = this.token;
    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");

    // create a org
    String orgName = getRandomOrganizationName("TestCreateSite");

    String orgId = createOrgnaization(orgName, SpogConstants.MSP_ORG,
        RandomStringUtils.randomAlphanumeric(8) + "@SpogQa",
        RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(4),
        RandomStringUtils.randomAlphanumeric(4), csrToken);

    // create msp admin
    Map<String, String> userInfoMap =
        composeRandomUserMap(TestDataPrefix, SpogConstants.MSP_ADMIN, orgId);
    String mspAdmin = userInfoMap.get("email");
    String mspPassword = getTestPassword();
    createUser(userInfoMap, csrToken);

    // login as msp admin
    String mspToken = loginSpogServer(mspAdmin, mspPassword);

    // create a site for org as msp admin
    String siteName = getRandomSiteName("TestCreateSite");
    Response response = createSite(siteName, siteType.cloud_direct.toString(), "", mspToken);
    response.then().statusCode(ServerResponseCode.Success_Post);

    String siteId = response.then().extract().path("data.site_id");

    response = deleteSite(siteId, "");
    response.then().statusCode(ServerResponseCode.Not_login);
    response.then().assertThat().body("errors.code.size()", is(1)).body("errors.code",
        hasItem("00900006"));
  }


  @Test
  public void Given_NoExistSiteId_When_Delete_Should_Failed() {

    String csrToken = this.token;
    String notExist_SiteId = "008d283f-9999-9999-acb3-7641e895eede";
    String invalid_SiteId = "invalid site id";

    Response response = deleteSite(notExist_SiteId, csrToken);
    response.then().log().body();
    response.then().statusCode(ServerResponseCode.Resource_Does_Not_Exist);

    response = deleteSite(invalid_SiteId, csrToken);
    response.then().log().body();

    response.then().statusCode(ServerResponseCode.Insufficient_Permissions);
    // response.then().assertThat().body("errors.code.size()", is(1)).body("errors.code",
    // hasItem("40000005"));
  }


  @Test
  public void Given_AccountManager_when_DeleteSiteForitsOrg_Should_Successful() {

    String csrToken = this.token;
    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");
    // create a org
    String orgName = getRandomOrganizationName("TestCreateSite");

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
    String siteName = getRandomSiteName("TestCreateSite");

    Response response =
        createSite(siteName, siteType.cloud_direct.toString(), accountOrgAId, accountManagerToken);

    String siteId = response.then().extract().path("data.site_id");
    response = deleteSite(siteId, accountManagerToken);

    response.then().log().all();
    response.then().statusCode(ServerResponseCode.Success_Delete);
  }


  @Test
  public void Given_AccountManager_when_DeleteSiteForOtherOrg_Should_Failed() {

    String csrToken = this.token;
    test = ExtentManager.getNewTest(this.getClass().getName() + "."
        + Thread.currentThread().getStackTrace()[1].getMethodName());
    test.assignAuthor("yin.li");
    // create a org
    String orgName = getRandomOrganizationName("TestCreateSite");

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

    String accountOrgAIdB =
        createSubOrgnaization(getRandomOrganizationName(TestDataPrefix), orgId, mspToken);

    userInfoMap = composeRandomUserMap(TestDataPrefix, SpogConstants.MSP_ACCOUNT_ADMIN, orgId);
    String accountManagerId = createUser(userInfoMap, mspToken);

    Map<String, String> userInfoMapB =
        composeRandomUserMap(TestDataPrefix, SpogConstants.MSP_ACCOUNT_ADMIN, orgId);
    String accountManagerIdB = createUser(userInfoMapB, mspToken);

    userSpogServer.setToken(mspToken);
    userSpogServer.assignMspAccountAdmins(orgId, accountOrgAId, accountManagerId, mspToken);
    userSpogServer.assignMspAccountAdmins(orgId, accountOrgAIdB, accountManagerIdB, mspToken);

    String accountManagerToken = loginSpogServer(userInfoMap.get("email"), getTestPassword());

    // create a site for org as msp admin
    String siteName = getRandomSiteName("TestCreateSite");
    Response response =
        createSite(siteName, siteType.cloud_direct.toString(), accountOrgAId, accountManagerToken);
    response.then().statusCode(ServerResponseCode.Success_Post);

    String siteId = response.then().extract().path("data.site_id");
    String accountManagerTokenB = loginSpogServer(userInfoMapB.get("email"), getTestPassword());
    response = deleteSite(siteId, accountManagerTokenB);

    response.then().statusCode(ServerResponseCode.Insufficient_Permissions);
  }

}
