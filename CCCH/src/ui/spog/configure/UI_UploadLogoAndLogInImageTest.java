package ui.spog.configure;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.SpogConstants;
import InvokerServer.Org4SPOGServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import ui.spog.pages.configure.UserAccountsPage;
import ui.spog.server.BrandingHelperpage;
import ui.spog.server.RolesHelperPage;

import ui.spog.server.CustomerAccountsPageHelper;
import ui.spog.server.ReportHelperPage;
import ui.spog.server.SPOGUIServer;

public class UI_UploadLogoAndLogInImageTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private UserSpogServer userSpogServer;
	private SPOGUIServer spogUIServer;
	private CustomerAccountsPageHelper customerAccountsPageHelper;
	private BrandingHelperpage brandingpageHelper;
	private ReportHelperPage rolesHelperPage;


	private String csrAdminUserName;
	private String csrAdminPassword;
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

		prepareEnv();

		brandingpageHelper = new BrandingHelperpage(browserType, maxWaitTimeSec);
		brandingpageHelper.openUrl(uiBaseURI);

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
			try {
              bqdb1.updateTable(this.BQName, runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0", String.valueOf(Nooftest), count1.getcreationtime(), "InProgress",author+" and Rest server is "+baseURI.split("//")[1]);
          } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
          } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
          }
		}
		brandingpageHelper.login_Spog(final_msp_user_name_email, common_password);
	}

	//	@BeforeMethod
	@Parameters({"uiBaseURI", "browserType", "maxWaitTimeSec"})
	public void login(String uiBaseURI, String browserType, int maxWaitTimeSec) {
		brandingpageHelper.login_Spog(final_msp_user_name_email, common_password);

	}


	@DataProvider(name = "Upload_logo")
	public final Object[][] UploadlogoInfo() {
		return new Object[][] { 	


			{"Upload logo with png file has 2kb size msp user",final_msp_user_name_email,common_password,"\\testdata\\images\\1.png","png",9168},
			{"Upload logo with png file has 7MB size msp user",final_msp_user_name_email,common_password,"\\testdata\\images\\exceed_size.png","png",7378053},
			{"Upload logo with png file has 8kb size msp user",final_msp_user_name_email,common_password,"\\testdata\\images\\4.png","png",4330},

			{"Upload logo with jpg file has 30kb size msp user",final_msp_user_name_email,common_password,"\\testdata\\images\\2.JPG","jpg",30193487},
			{"Upload logo with jpg file has 24kb size msp user",final_msp_user_name_email,common_password,"\\testdata\\images\\5.JPG","jpg",24812},
			{"Upload logo with jpg file has 12MB size msp user",final_msp_user_name_email,common_password,"\\testdata\\images\\exceed_size.jpg","jpg",13518328},
			/*{"Upload logo with jpg file has 180KB size msp user",final_msp_user_name_email,common_password,"\\testdata\\images\\fakeimg.JPG","jpg",184581},
			 */
			{"Upload logo with gif file has 65kb size msp user",final_msp_user_name_email,common_password,"\\testdata\\images\\bitmap.bmp","bmp",67510},
			{"Upload logo with gif file has 8kb size msp user",final_msp_user_name_email,common_password,"\\testdata\\images\\3.GIF","gif",8631},
			{"Upload logo with gif file has 8MB size msp user",final_msp_user_name_email,common_password,"\\testdata\\images\\exceed_size.gif","gif",8693537},

			{"Upload logo with exe file has 190KB size msp user",final_msp_user_name_email,common_password,"\\testdata\\images\\py2exe.exe","exe",201234},
			{"Upload logo with txt file has 190KB size msp user",final_msp_user_name_email,common_password,"\\testdata\\images\\test.txt","txt",201234},

		};
	}
	@Test(dataProvider = "Upload_logo", enabled=true)
	public void UploadLogo(String caseType,String Username,String password,String LogoPath,String FileType,int FileSize) {

		test=ExtentManager.getNewTest(caseType);
		brandingpageHelper.checkUploadLogo(LogoPath,FileType,FileSize,test);
	}


	@DataProvider(name = "UploadLogin_Image")
	public final Object[][]LoginImageInfo() {
		return new Object[][] { 	

			{"Upload logo with png file has 2kb size msp user",final_msp_user_name_email,common_password,"http://123.com","\\testdata\\images\\1.png","png",9168},
			{"Upload logo with png file has 7MB size msp user",final_msp_user_name_email,common_password,"http://123.com","\\testdata\\images\\exceed_size.png","png",7378053},
			{"Upload logo with png file has 8kb size msp user",final_msp_user_name_email,common_password,"http://123.com","\\testdata\\images\\4.png","png",4330},

			{"Upload logo with jpg file has 30kb size msp user",final_msp_user_name_email,common_password,"http://123.com","\\testdata\\images\\2.JPG","jpg",30193487},
			{"Upload logo with jpg file has 24kb size msp user",final_msp_user_name_email,common_password,"http://123.com","\\testdata\\images\\5.JPG","jpg",24812},
			{"Upload logo with jpg file has 12MB size msp user",final_msp_user_name_email,common_password,"http://123.com","\\testdata\\images\\exceed_size.jpg","jpg",13518328},
			 /*{"Upload logo with jpg file has 180KB size msp user",final_msp_user_name_email,common_password,"http://123.com","\\testdata\\images\\fakeimg.JPG","jpg",184581},
			 */
			{"Upload logo with gif file has 65kb size msp user",final_msp_user_name_email,common_password,"http://123.com","\\testdata\\images\\bitmap.bmp","gif",67510},
			{"Upload logo with gif file has 8kb size msp user",final_msp_user_name_email,common_password,"http://123.com","\\testdata\\images\\3.GIF","gif",8631},
			{"Upload logo with gif file has 8MB size msp user",final_msp_user_name_email,common_password,"http://123.com","\\testdata\\images\\exceed_size.gif","gif",8693537},

			{"Upload logo with exe file has 190KB size msp user",final_msp_user_name_email,common_password,"http://123.com","\\testdata\\images\\py2exe.exe","exe",201234},
			{"Upload logo with txt file has 190KB size msp user",final_msp_user_name_email,common_password,"http://123.com","\\testdata\\images\\test.txt","txt",201234},
		};
	}
	@Test(dataProvider = "UploadLogin_Image", enabled=true)
	public void upLoadLogInImage(String caseType,String Username,String password,String PortalURL,String uploadloginImagepath,String FileType,int FileSize) {

		test=ExtentManager.getNewTest(caseType);
		brandingpageHelper.checkUploadLogInImageTest(PortalURL,uploadloginImagepath,FileType,FileSize,test);

	}
	//	@AfterMethod
	public void afterMethod(){
		//brandingpageHelper.logout();
		//brandingpageHelper.destroy();
	}
	private void prepareEnv(){

		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);

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
		// direct_user_validToken=validToken;
		test.log(LogStatus.INFO, "The token is :" + direct_user_validToken);

		test.log(LogStatus.INFO, "Get the logged in user id ");
		direct_user_id = spogServer.GetLoggedinUser_UserID();
		test.log(LogStatus.INFO, "The direct org user id is " + direct_user_id);


	}

	@AfterMethod
	public void getResult(ITestResult result){
		//brandingpageHelper.logout();

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
		brandingpageHelper.destroy();
		recycleVolumeInCDandDestroyOrg(org_model_prefix);
	}



}