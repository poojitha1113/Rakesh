package ui.spog.configure;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.lang3.RandomStringUtils;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.gargoylesoftware.htmlunit.javascript.host.css.CSSSupportsRule;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.SpogConstants;
import InvokerServer.Org4SPOGServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;
import ui.spog.server.RolesHelperPage;
import ui.spog.role.constants.RoleConstants;
import ui.spog.server.CustomerAccountsPageHelper;
import ui.spog.server.SPOGUIServer;

public class RolesTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private UserSpogServer userSpogServer;
	private SPOGUIServer spogUIServer;
	private CustomerAccountsPageHelper customerAccountsPageHelper;
	private RolesHelperPage RolesHelperPage;
	private String csrAdminUserName;
	private String csrAdminPassword;
	private String csr_token;
	private ExtentTest test;

	/*private ExtentReports rep;
  private SQLServerDb bqdb1;
  public int Nooftest;
  private long creationTime;
  private String BQName=null;
  private String runningMachine;
  private testcasescount count1;
  private String buildVersion;*/
	private String url;

	private String postfix_email = "@arcserve.com";
	private String common_password = "Mclaren@2016";

	private String prefix_direct = "SPOG_QA_MALLESWARI_BQ_DIRECT_ORG";
	private String direct_org_name = prefix_direct + "_org";
	private String direct_org_email = direct_org_name + postfix_email;
	private String direct_org_first_name = direct_org_name + "_first_name";
	private String direct_org_last_name = direct_org_name + "_last_name";
	private String direct_user_name = prefix_direct + "_admin";
	private String direct_user_name_email = prefix_direct + "_admin" + postfix_email;
	private String direct_user_first_name = direct_user_name + "_first_name";
	private String direct_user_last_name = direct_user_name + "_last_name";
	private String direct_user_validToken,direct_org_id;

	private String prefix_msp = "spog_qa_malleswari_msp";
	private String msp_org_name = prefix_msp + "_org";
	private String msp_user_name = prefix_msp + "_admin";
	private String msp_user_name_email = prefix_msp + "_admin" + postfix_email;
	private String msp_user_first_name = msp_user_name + "_first_name";
	private String msp_user_last_name = msp_user_name + "_last_name";
	private String msp_org_id=null;
	private String final_msp_user_name_email=null;	  

	private String account_id;
	private String account_user_email;
	private String direct_user_id;
	private String msp_user_id;
	private String account_user_id;
	private Org4SPOGServer org4SpogServer;
	private String  org_model_prefix=this.getClass().getSimpleName();

	HashMap<String, Object> directUserInfo = new HashMap<>();


	@BeforeClass
	@Parameters({ "baseURI", "port",   "logFolder", "csrAdminUserName", "csrAdminPassword", "buildVersion", "uiBaseURI", "browserType", "maxWaitTimeSec"})
	public void beforeClass(String baseURI, String port, String logFolder, String adminUserName, String adminPassword,  String buildVersion,
			String uiBaseURI, String browserType, int maxWaitTimeSec) throws UnknownHostException {

		org4SpogServer = new Org4SPOGServer(baseURI, port);
		spogServer = new SPOGServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		this.url = uiBaseURI;
		this.csrAdminUserName = adminUserName;
		this.csrAdminPassword = adminPassword;
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(),logFolder);
		test = rep.startTest("beforeClass");

		//prepareEnv();

		RolesHelperPage = new RolesHelperPage(browserType, maxWaitTimeSec);
		RolesHelperPage.openUrl(uiBaseURI);

		this.BQName = this.getClass().getSimpleName();
		String author = "mallleswari.sykam";
		this.runningMachine =  InetAddress.getLocalHost().getHostName();
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date=new java.util.Date();
		this.buildVersion=buildVersion+"_"+dateFormater.format(date);
		Nooftest=0;
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		if(count1.isstarttimehit()==0) {

			creationTime=System.currentTimeMillis();
			count1.setcreationtime(creationTime);
			//creationTime = System.currentTimeMillis();
			/*try {
              bqdb1.updateTable(this.BQName, runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0", String.valueOf(Nooftest), count1.getcreationtime(), "InProgress",author+" and Rest server is "+baseURI.split("//")[1]);
          } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
          } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
          }*/
		}

	}
	@BeforeMethod
	@Parameters({"uiBaseURI", "browserType", "maxWaitTimeSec"})
	public void login(String uiBaseURI, String browserType, int maxWaitTimeSec) {
		RolesHelperPage.login_Spog("malleswari.sykam101@gmail.com", "Mclaren@2016");


	}

	@DataProvider(name = "Useroles-info")
	public final Object[][] Userolesinfo() {
		return new Object[][] { 	

			{"Check user roles information in direct organization","malleswari.sykam106@gmail.com", "Mclaren@2016","e79aa712-d4c2-4735-9a16-f66006", 1},


		};
	}
	@Test(dataProvider = "Useroles-info", enabled=true)
	public void checkUserRolesTest(String caseType,String Username,String password,String orgId, int noOfUserToCreate) {

		test=ExtentManager.getNewTest(caseType);
		ArrayList<HashMap<String, Object>> expUserInfo = new ArrayList<>();
		/*spogServer.userLogin(Username, password);
		test.log(LogStatus.INFO, "Getting the JWTToken for the Logged in user");
		String validToken = spogServer.getJWTToken();
		 */if (caseType.contains("direct")) {
			 //createUsers(SpogConstants.DIRECT_ADMIN, orgId, noOfUserToCreate);

			 //	Response response = spogServer.getAllUsersInOrganization(orgId, "", "", 0, 100, test);
			 //String userCount = ((ArrayList<HashMap<String, Object>>) response.then().extract().path("data")).toString();		

			 HashMap<String, Object> userInfo = composeInfo(RoleConstants.ROLE_ADMIN, String.valueOf(noOfUserToCreate), RoleConstants.DESCRIPTION_ADMIN);
			 expUserInfo.add(userInfo);
		 }else {
			 //createUsers(SpogConstants.MSP_ADMIN, orgId, noOfUserToCreate);

			 //	Response response = spogServer.getAllUsersInOrganization(orgId, "", "", 0, 100, test);
			 //String userCount = ((ArrayList<HashMap<String, Object>>) response.then().extract().path("data")).toString();

			 HashMap<String, Object> userInfo = composeInfo(RoleConstants.ROLE_MSPADMIN, String.valueOf(noOfUserToCreate), RoleConstants.DESCRIPTION_MSPADMIN);
			 expUserInfo.add(userInfo);


			 //createUsers(SpogConstants.MSP_ACCOUNT_ADMIN, orgId, noOfUserToCreate);

			 //response = spogServer.getAllUsersInOrganization(orgId, "", "", 0, 100, test);
			 //String allUserCount = ((ArrayList<HashMap<String, Object>>) response.then().extract().path("data")).toString(); // all count MSP + MSP AA

			 //	int count = (Integer.parseInt(allUserCount)-Integer.parseInt(userCount)); // substracting msp admin count from all users count

			 HashMap<String, Object> userInfo1 = composeInfo(RoleConstants.ROLE_MSPACCOUNTADMIN, String.valueOf(noOfUserToCreate), RoleConstants.DESCRIPTION_MSPACCOUNTADMIN);
			 expUserInfo.add(userInfo1);
		 }		

		 RolesHelperPage.getRolesInformation(expUserInfo, test);

	}
	//	@AfterMethod
	public void afterMethod(){
		RolesHelperPage.logout();
		//RolesHelperPage.destroy();
	}
	private void prepareEnv(){

		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
		spogServer.getJWTToken();

		//************************create msp org,user *************************************
		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		test.log(LogStatus.INFO,"create a msp org");
		msp_org_id = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name+org_model_prefix , SpogConstants.MSP_ORG, null, null, null, null, test);
		final_msp_user_name_email = prefix + msp_user_name_email;

		test.log(LogStatus.INFO,"create a admin under msp org");
		msp_user_id = spogServer.createUserAndCheck(final_msp_user_name_email, common_password, prefix + msp_user_first_name, prefix + msp_user_last_name, SpogConstants.MSP_ADMIN, msp_org_id, test);
		spogServer.userLogin(final_msp_user_name_email, common_password);

		//*********************Create Direct Org user****************************
		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);


		this.direct_org_email = prefix + this.direct_org_email;
		direct_org_id = spogServer.CreateOrganizationWithCheck(prefix + direct_org_name + org_model_prefix,
				SpogConstants.DIRECT_ORG, this.direct_org_email, common_password, prefix + direct_org_first_name,
				prefix + direct_org_last_name, test);
		spogServer.userLogin(this.direct_org_email, common_password);
		test.log(LogStatus.INFO, "Getting the JWTToken for the Logged in user");
		direct_user_validToken = spogServer.getJWTToken();
		//direct_user_validToken=validToken;
		test.log(LogStatus.INFO, "The token is :" + direct_user_validToken);

		test.log(LogStatus.INFO, "Get the logged in user id ");
		direct_user_id = spogServer.GetLoggedinUser_UserID();
		test.log(LogStatus.INFO, "The direct org user id is " + direct_user_id);





	}

	public void createUsers(String role_id, String organization_id, int noOfUsersToCreate) {

		spogServer.setToken(csr_token);
		for (int i = 0; i < noOfUsersToCreate; i++) {
			spogServer.createUserAndCheck(spogServer.ReturnRandom("dir")+"@abc.com", common_password, spogServer.ReturnRandom("FN"),
					spogServer.ReturnRandom("LM"), role_id, organization_id, test);
		}		
	}



	@AfterMethod
	public void getResult(ITestResult result){
		RolesHelperPage.logout();

		if(result.getStatus() == ITestResult.FAILURE){
			count1.setfailedcount();            
			test.log(LogStatus.FAIL, "Test Case Failed is "+result.getName()+" with parameters as "+Arrays.asList(result.getParameters()) );
			test.log(LogStatus.FAIL, result.getThrowable().getMessage());
		}else if(result.getStatus() == ITestResult.SKIP){
			count1.setskippedcount();
			test.log(LogStatus.SKIP, "Test Case Skipped is "+result.getName());
		}else if(result.getStatus()==ITestResult.SUCCESS){
			count1.setpassedcount();
		}
		// ending test
		//endTest(logger) : It ends the current test and prepares to create HTML report
		rep.endTest(test);    
	}

	@AfterClass
	public void afterClass() {
		//RolesHelperPage.destroy();
		//recycleVolumeInCDandDestroyOrg(org_model_prefix);
	}


	public HashMap<String, Object> composeInfo(String role, String noOfUsers, String description) {

		HashMap<String, Object> sample = new HashMap<>();

		sample.put(RoleConstants.HEADER_ROLE, role);
		sample.put(RoleConstants.HEADER_NOOFROLES, noOfUsers);
		sample.put(RoleConstants.HEADER_DESCRIPTION, description);

		return sample;
	}


}
