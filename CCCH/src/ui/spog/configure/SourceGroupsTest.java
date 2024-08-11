package ui.spog.configure;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.ConnectionStatus;
import Constants.ErrorCode;
import Constants.OSMajor;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import InvokerServer.GatewayServer;
import InvokerServer.Org4SPOGServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import api.sources.groups.CreateGroupTest;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;
import net.bytebuddy.utility.RandomString;
import ui.base.common.PageName;
import ui.spog.role.constants.RoleConstants;
import ui.spog.server.CustomerAccountsPageHelper;
import ui.spog.server.SourceGroupsHelperPage;
import ui.spog.server.SPOGUIServer;

public class SourceGroupsTest  extends base.prepare.Is4Org {

	private SPOGServer spogServer;
	private UserSpogServer userSpogServer;
	private SPOGUIServer spogUIServer;
	private GatewayServer gatewayServer;
	private CustomerAccountsPageHelper customerAccountsPageHelper;
	private SourceGroupsHelperPage sourceGroupsHelperPage;
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
	private String direct_user_id,Direct_validToken,Direct_site_id ;
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
		gatewayServer = new GatewayServer(baseURI, port);
		this.url = uiBaseURI;
		this.csrAdminUserName = adminUserName;
		this.csrAdminPassword = adminPassword;
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(),logFolder);
		test = rep.startTest("beforeClass");

	//	prepareEnv();

		sourceGroupsHelperPage = new SourceGroupsHelperPage(browserType, maxWaitTimeSec);
		sourceGroupsHelperPage.openUrl(uiBaseURI);

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
		spogServer.userLogin("malleswari.sykam101@gmail.com", "Mclaren@2016");
		test.log(LogStatus.INFO, "Getting the JWTToken for the Logged in user");
		Direct_validToken = spogServer.getJWTToken();
		sourceGroupsHelperPage.login_Spog("malleswari.sykam101@gmail.com", "Mclaren@2016");
	}
	@BeforeMethod
	@Parameters({"uiBaseURI", "browserType", "maxWaitTimeSec"})
	public void login(String uiBaseURI, String browserType, int maxWaitTimeSec) {
		//sourceGroupsHelperPage.login_Spog("malleswari.sykam101@gmail.com", "Mclaren@2016");


	}

	@DataProvider(name = "Create_DeleteSoucreGroup-info")
	public final Object[][] Create_DeleteSoucreGroupinfo() {
		return new Object[][] { 	



			{"Direct -Create soucregroup with vaid characters","malleswari.sykam101@gmail.com", "Mclaren@2016","5f278502-c98c-42f8-a564-99343db56a6d", "SPOG_TEST1234","created"},
			{"Valid-Create SourceGroup for the direct Organization;groupname has 8 characters with all lowercases ","malleswari.sykam101@gmail.com", "Mclaren@2016","e79aa712-d4c2-4735-9a16-f66006", "eswari1235"  ,"created"},
			{"Valid-Create SourceGroup for the direct Organization;groupname has 4 characters with all lowercases and upperCase ","malleswari.sykam101@gmail.com", "Mclaren@2016","e79aa712-d4c2-4735-9a16-f66006", "TEST_12345"  ,"created"},
			{"Valid-Create SourceGroup for the direct Organization;groupname has mixed charatces","malleswari.sykam101@gmail.com", "Mclaren@2016","e79aa712-d4c2-4735-9a16-f66006", "@TeSt"  ,"created"},
			{"Valid-Create SourceGroup for the direct Organization;groupname has numbers onlly ","malleswari.sykam101@gmail.com", "Mclaren@2016","e79aa712-d4c2-4735-9a16-f66006", "@TESt"  ,"created"},
			{"Valid-Create SourceGroup for the direct Organization;groupname has  numbers onlly ","malleswari.sykam101@gmail.com", "Mclaren@2016","e79aa712-d4c2-4735-9a16-f66006", "123456"  ,"created"},

		};
	}
	@Test(dataProvider = "Create_DeleteSoucreGroup-info", enabled=true)
	public void CreateanddeleteSourceGroupTest( String TestCase,String Username,
			String password,String orgId,
			String SourceGroupName,
			String toastmessage
			) {

		test=ExtentManager.getNewTest(TestCase);
		ArrayList<HashMap<String, Object>> soucregroupinfo = new ArrayList<>();
		sourceGroupsHelperPage.createSoucregroup(SourceGroupName,toastmessage);
		sourceGroupsHelperPage.checksourcegroupList(SourceGroupName , test);
		sourceGroupsHelperPage.searchbygroupname(SourceGroupName);
		sourceGroupsHelperPage.deleteSoucreGroup(SourceGroupName,test);
		sourceGroupsHelperPage.checksourcegroupList(SourceGroupName , test);

	}



	@DataProvider(name = "CloseSoucreGroup-info")
	public final Object[][] CloseSoucreGroupinfo() {
		return new Object[][] { 	



			{"Direct -Cancel the creating soucregroup ","malleswari.sykam101@gmail.com", "Mclaren@2016","5f278502-c98c-42f8-a564-99343db56a6d", "UI_Soucregroup_cancel"},
			{"Direct -Close the creating soucre group dialog menu","malleswari.sykam101@gmail.com", "Mclaren@2016","5f278502-c98c-42f8-a564-99343db56a6d", "UI_Soucregroup_close"},

		};
	}
	@Test(dataProvider = "CloseSoucreGroup-info", enabled=true)
	public void ClosecreatesoucreGroupdialog( String TestCase,String Username,
			String password,String orgId,
			String SourceGroupName,
			String toastmessage
			) {

		test=ExtentManager.getNewTest(TestCase);
		sourceGroupsHelperPage.createSoucregroup(SourceGroupName,null);
	}



	@DataProvider(name = "addSourcesToSoucreGroup-info")
	public final Object[][] addSourcesToSoucreGroupinfo() {
		return new Object[][] { 	



			{"Direct- AddsoucresTosoucreGroup where soucreType is machine","malleswari.sykam101@gmail.com", "Mclaren@2016","5f278502-c98c-42f8-a564-99343db56a6d","UISOUCREGROUP"+UUID.randomUUID(),SourceType.machine,SourceProduct.cloud_direct,
				ProtectionStatus.unprotect,ConnectionStatus.online,"windows","SQLSERVER","created","Selected sources have been successfully assigned to the Source Group"},
			/*{"Direct- AddsoucresTosoucreGroup where soucreType is instant_vm","malleswari.sykam101@gmail.com", "Mclaren@2016","5f278502-c98c-42f8-a564-99343db56a6d","UISOUCREGROUP"+UUID.randomUUID(),SourceType.instant_vm,SourceProduct.cloud_direct,
					ProtectionStatus.unprotect,ConnectionStatus.online,"windows","SQLSERVER","created","Selected sources have been successfully assigned to the Source Group"},
			{"Direct- AddsoucresTosoucreGroup where soucreType is instant_vm","malleswari.sykam101@gmail.com", "Mclaren@2016","5f278502-c98c-42f8-a564-99343db56a6d","UISOUCREGROUP"+UUID.randomUUID(),SourceType.agentless_vm,SourceProduct.cloud_direct,
						ProtectionStatus.unprotect,ConnectionStatus.online,"windows","SQLSERVER","created","Selected sources have been successfully assigned to the Source Group"},
			{"Direct- AddsoucresTosoucreGroup where soucreproduce udp","malleswari.sykam101@gmail.com", "Mclaren@2016","5f278502-c98c-42f8-a564-99343db56a6d","UISOUCREGROUP"+UUID.randomUUID(),SourceType.machine,SourceProduct.udp,
							ProtectionStatus.unprotect,ConnectionStatus.online,"windows","SQLSERVER","created","Selected sources have been successfully assigned to the Source Group"},
			{"Direct- AddsoucresTosoucreGroup where soucreproduce udp","malleswari.sykam101@gmail.com", "Mclaren@2016","5f278502-c98c-42f8-a564-99343db56a6d","UISOUCREGROUP"+UUID.randomUUID(),SourceType.instant_vm,SourceProduct.udp,
								ProtectionStatus.unprotect,ConnectionStatus.online,"windows","SQLSERVER","created","Selected sources have been successfully assigned to the Source Group"},
			{"Direct- AddsoucresTosoucreGroup where soucreproduce udp","malleswari.sykam101@gmail.com", "Mclaren@2016","5f278502-c98c-42f8-a564-99343db56a6d","UISOUCREGROUP"+UUID.randomUUID(),SourceType.agentless_vm,SourceProduct.udp,
									ProtectionStatus.unprotect,ConnectionStatus.online,"windows","SQLSERVER","created","Selected sources have been successfully assigned to the Source Group"},

			{"Direct- AddsoucresTosoucreGroup where protectionStatus is protect ","malleswari.sykam101@gmail.com", "Mclaren@2016","5f278502-c98c-42f8-a564-99343db56a6d","UISOUCREGROUP"+UUID.randomUUID(),SourceType.machine,SourceProduct.cloud_direct,
										ProtectionStatus.protect,ConnectionStatus.online,"windows","SQLSERVER","created","Selected sources have been successfully assigned to the Source Group"},
			{"Direct- AddsoucresTosoucreGroup where protectionStatus is protect ","malleswari.sykam101@gmail.com", "Mclaren@2016","5f278502-c98c-42f8-a564-99343db56a6d","UISOUCREGROUP"+UUID.randomUUID(),SourceType.instant_vm,SourceProduct.cloud_direct,
											ProtectionStatus.protect,ConnectionStatus.online,"windows","SQLSERVER","created","Selected sources have been successfully assigned to the Source Group"},
			{"Direct- AddsoucresTosoucreGroup where protectionStatus is protect ","malleswari.sykam101@gmail.com", "Mclaren@2016","5f278502-c98c-42f8-a564-99343db56a6d","UISOUCREGROUP"+UUID.randomUUID(),SourceType.agentless_vm,SourceProduct.cloud_direct,
												ProtectionStatus.protect,ConnectionStatus.online,"windows","SQLSERVER","created","Selected sources have been successfully assigned to the Source Group"},
			{"Direct- AddsoucresTosoucreGroup where protectionStatus is protect","malleswari.sykam101@gmail.com", "Mclaren@2016","5f278502-c98c-42f8-a564-99343db56a6d","UISOUCREGROUP"+UUID.randomUUID(),SourceType.machine,SourceProduct.udp,
													ProtectionStatus.protect,ConnectionStatus.online,"windows","SQLSERVER","created","Selected sources have been successfully assigned to the Source Group"},
			{"Direct- AddsoucresTosoucreGroup where protectionStatus is protect","malleswari.sykam101@gmail.com", "Mclaren@2016","5f278502-c98c-42f8-a564-99343db56a6d","UISOUCREGROUP"+UUID.randomUUID(),SourceType.instant_vm,SourceProduct.udp,
														ProtectionStatus.protect,ConnectionStatus.online,"windows","SQLSERVER","created","Selected sources have been successfully assigned to the Source Group"},
			{"Direct- AddsoucresTosoucreGroup where protectionStatus is protect","malleswari.sykam101@gmail.com", "Mclaren@2016","5f278502-c98c-42f8-a564-99343db56a6d","UISOUCREGROUP"+UUID.randomUUID(),SourceType.agentless_vm,SourceProduct.udp,
															ProtectionStatus.protect,ConnectionStatus.online,"windows","SQLSERVER","created","Selected sources have been successfully assigned to the Source Group"},

			{"Direct- AddsoucresTosoucreGroup where soucreType is machine","malleswari.sykam101@gmail.com", "Mclaren@2016","5f278502-c98c-42f8-a564-99343db56a6d","UISOUCREGROUP"+UUID.randomUUID(),SourceType.machine,SourceProduct.cloud_direct,
																ProtectionStatus.unprotect,ConnectionStatus.offline,"windows","SQLSERVER","created","Selected sources have been successfully assigned to the Source Group"},
			{"Direct- AddsoucresTosoucreGroup where soucreType is instant_vm","malleswari.sykam101@gmail.com", "Mclaren@2016","5f278502-c98c-42f8-a564-99343db56a6d","UISOUCREGROUP"+UUID.randomUUID(),SourceType.instant_vm,SourceProduct.cloud_direct,
																	ProtectionStatus.unprotect,ConnectionStatus.offline,"windows","SQLSERVER","created","Selected sources have been successfully assigned to the Source Group"},
			{"Direct- AddsoucresTosoucreGroup where soucreType is instant_vm","malleswari.sykam101@gmail.com", "Mclaren@2016","5f278502-c98c-42f8-a564-99343db56a6d","UISOUCREGROUP"+UUID.randomUUID(),SourceType.agentless_vm,SourceProduct.cloud_direct,
																		ProtectionStatus.unprotect,ConnectionStatus.offline,"windows","SQLSERVER","created","Selected sources have been successfully assigned to the Source Group"},
			{"Direct- AddsoucresTosoucreGroup where soucreproduce udp","malleswari.sykam101@gmail.com", "Mclaren@2016","5f278502-c98c-42f8-a564-99343db56a6d","UISOUCREGROUP"+UUID.randomUUID(),SourceType.machine,SourceProduct.udp,
																			ProtectionStatus.unprotect,ConnectionStatus.offline,"windows","SQLSERVER","created","Selected sources have been successfully assigned to the Source Group"},
			{"Direct- AddsoucresTosoucreGroup where soucreproduce udp","malleswari.sykam101@gmail.com", "Mclaren@2016","5f278502-c98c-42f8-a564-99343db56a6d","UISOUCREGROUP"+UUID.randomUUID(),SourceType.instant_vm,SourceProduct.udp,
																				ProtectionStatus.unprotect,ConnectionStatus.offline,"windows","SQLSERVER","created","Selected sources have been successfully assigned to the Source Group"},
			{"Direct- AddsoucresTosoucreGroup where soucreproduce udp","malleswari.sykam101@gmail.com", "Mclaren@2016","5f278502-c98c-42f8-a564-99343db56a6d","UISOUCREGROUP"+UUID.randomUUID(),SourceType.agentless_vm,SourceProduct.udp,
																					ProtectionStatus.unprotect,ConnectionStatus.offline,"windows","SQLSERVER","created","Selected sources have been successfully assigned to the Source Group"},

			{"Direct- AddsoucresTosoucreGroup where protectionStatus is protect ","malleswari.sykam101@gmail.com", "Mclaren@2016","5f278502-c98c-42f8-a564-99343db56a6d","UISOUCREGROUP"+UUID.randomUUID(),SourceType.machine,SourceProduct.cloud_direct,
																						ProtectionStatus.protect,ConnectionStatus.offline,"windows","SQLSERVER","created","Selected sources have been successfully assigned to the Source Group"},
			{"Direct- AddsoucresTosoucreGroup where protectionStatus is protect ","malleswari.sykam101@gmail.com", "Mclaren@2016","5f278502-c98c-42f8-a564-99343db56a6d","UISOUCREGROUP"+UUID.randomUUID(),SourceType.instant_vm,SourceProduct.cloud_direct,
																							ProtectionStatus.protect,ConnectionStatus.offline,"windows","SQLSERVER","created","Selected sources have been successfully assigned to the Source Group"},
			{"Direct- AddsoucresTosoucreGroup where protectionStatus is protect ","malleswari.sykam101@gmail.com", "Mclaren@2016","5f278502-c98c-42f8-a564-99343db56a6d","UISOUCREGROUP"+UUID.randomUUID(),SourceType.agentless_vm,SourceProduct.cloud_direct,
																								ProtectionStatus.protect,ConnectionStatus.offline,"windows","SQLSERVER","created","Selected sources have been successfully assigned to the Source Group"},
			{"Direct- AddsoucresTosoucreGroup where protectionStatus is protect","malleswari.sykam101@gmail.com", "Mclaren@2016","5f278502-c98c-42f8-a564-99343db56a6d","UISOUCREGROUP"+UUID.randomUUID(),SourceType.machine,SourceProduct.udp,
																									ProtectionStatus.protect,ConnectionStatus.offline,"windows","SQLSERVER","created","Selected sources have been successfully assigned to the Source Group"},
			{"Direct- AddsoucresTosoucreGroup where protectionStatus is protect","malleswari.sykam101@gmail.com", "Mclaren@2016","5f278502-c98c-42f8-a564-99343db56a6d","UISOUCREGROUP"+UUID.randomUUID(),SourceType.instant_vm,SourceProduct.udp,
																										ProtectionStatus.protect,ConnectionStatus.offline,"windows","SQLSERVER","created","Selected sources have been successfully assigned to the Source Group"},
			{"Direct- AddsoucresTosoucreGroup where protectionStatus is protect","malleswari.sykam101@gmail.com", "Mclaren@2016","5f278502-c98c-42f8-a564-99343db56a6d","UISOUCREGROUP"+UUID.randomUUID(),SourceType.agentless_vm,SourceProduct.udp,
																											ProtectionStatus.protect,ConnectionStatus.offline,"windows","SQLSERVER","created","Selected sources have been successfully assigned to the Source Group"},
			 */
		};
	}
	@Test(dataProvider = "addSourcesToSoucreGroup-info", enabled=true)
	public void addSourcesToSoucreGroupTest( String TestCase,String Username,
			String password,String orgId,
			String SourceGroupName,
			SourceType SourceType,
			SourceProduct SourceProduct,
			ProtectionStatus ProtectionStatus,
			ConnectionStatus ConnectionStatus,
			String OSMajor ,
			String application,
			String toastmessage,
			String DeletedsoucretoastMessgae) {

		test=ExtentManager.getNewTest(TestCase);
		//ArrayList<HashMap<String, Object>> soucregroupinfo = new ArrayList<>();

		String[] arrayofsourcenodes= new String[2];

		ArrayList<String> SoucreIds = new ArrayList<>();

		

		sourceGroupsHelperPage.createSoucregroup(SourceGroupName, toastmessage);
		test.log(LogStatus.INFO, "Adding sources to org");
		for(int i=0;i<arrayofsourcenodes.length;i++) {

			Response response= spogServer.createSource("UI_automation"+UUID.randomUUID(), SourceType,SourceProduct,orgId, "c64603be-6c3f-4e5e-bfce-e355f6c8d46a",
					ProtectionStatus,ConnectionStatus,OSMajor,application,test);
			arrayofsourcenodes[i] = response.then().extract().path("data.source_name");

			SoucreIds.add(response.then().extract().path("data.source_id"));
			System.out.println(SoucreIds.get(i));
		}
		//Add soucres to teh soucre group
		sourceGroupsHelperPage.addSoucrestoSourceGroup(SourceGroupName,arrayofsourcenodes,toastmessage);
		//sourceGroupsHelperPage.removeSourcesFromSoucreGroup(SourceGroupName, arrayofsourcenodes, toastmessage);
		//sourceGroupsHelperPage.deleteSoucreGroup(SourceGroupName, test);

		System.out.println("Again add some soucres to the same group");

		//Again add some soucres to the same group 
		for(int i=0;i<arrayofsourcenodes.length;i++) {

			Response response= spogServer.createSource("UI_automation"+UUID.randomUUID(), SourceType,SourceProduct,orgId, "c64603be-6c3f-4e5e-bfce-e355f6c8d46a",
					ProtectionStatus,ConnectionStatus,OSMajor,application,test);
			arrayofsourcenodes[i] = response.then().extract().path("data.source_name");

			SoucreIds.add(response.then().extract().path("data.source_id"));
			System.out.println(SoucreIds.get(i));
		}
		sourceGroupsHelperPage.addSoucrestoSourceGroup(SourceGroupName,arrayofsourcenodes,toastmessage);
		sourceGroupsHelperPage.deleteSoucreGroup(SourceGroupName, test);

		//Delete the soucres 
		for(int i=0;i<arrayofsourcenodes.length;i++) {
			spogServer.deleteSourcesById(Direct_validToken, SoucreIds.get(i), test);
			spogServer.deleteSourcesRemove( SoucreIds.get(i), SpogConstants.SUCCESS_GET_PUT_DELETE, "", "");
		}
	}










	@DataProvider(name = "SoucreGroupInvalid-info")
	public final Object[][] SoucreGroupInavlidinfo() {
		return new Object[][] { 	

			{"Group name has 2 characters ","malleswari.sykam101@gmail.com", "Mclaren@2016","e79aa712-d4c2-4735-9a16-f66006", "12","Must be 3 characters or more"},
			{"Group name has 1 character ","malleswari.sykam101@gmail.com", "Mclaren@2016","e79aa712-d4c2-4735-9a16-f66006", "1","Must be 3 characters or more"},
			//{"Griupname has more than 128 characters","malleswari.sykam101@gmail.com","Mclaren@2016","e79aa712-d4c2-4735-9a16-f66006","Nagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykam","Must be 128 characters or less"},


		};
	}
	@Test(dataProvider = "SoucreGroupInvalid-info", enabled=true)
	public void checkSourceGroupInvalidTest(String TestCase,String Username,String password,String orgId, String SourceGroupName,String errormessage) {

		test=ExtentManager.getNewTest(TestCase);
		sourceGroupsHelperPage.CreateSoucreGroupWithCheck(SourceGroupName, errormessage);
	}


	@DataProvider(name = "AddAllsources-info")
	public final Object[][] addAllsoucresinfo() {
		return new Object[][] { 	

			{"Direct- add allsources to the selected soucregroup",direct_org_email, common_password,direct_org_id, "AddAllsoucres"+UUID.randomUUID(),null},

		};
	}
	@Test(dataProvider = "AddAllsources-info", enabled=true)
	public void AddAllSoucrestoGroupTest(String TestCase,String Username,String password,String orgId, String sourceGroupName,String toastMessage) {

		test=ExtentManager.getNewTest(TestCase);
		ArrayList<HashMap<String, Object>> sourcesInfo = null;
		ArrayList<String > sourceNames = new ArrayList<>();
		sourceGroupsHelperPage.login_Spog(Username, password);

		/*spogServer.userLogin("malleswari.sykam101@gmail.com", "Mclaren@2016");
		test.log(LogStatus.INFO, "Getting the JWTToken for the Logged in user");
		Direct_validToken = spogServer.getJWTToken();
		 */

		//Create sources
		spogServer.createSource("UI_automation"+UUID.randomUUID(), SourceType.machine,SourceProduct.udp,orgId, Direct_site_id,
				ProtectionStatus.protect,ConnectionStatus.online,OSMajor.windows.name(),"SQLSERVER",test);

		Response response=	spogServer.getSources("", "", 1, 0, true, test);

		sourcesInfo = response.then().extract().path("data");

		int sourcesSize = sourcesInfo.size();

		if(!(sourcesSize==0)) {
			for (int i = 0; i < sourcesInfo.size(); i++) {
				sourceNames.add(sourcesInfo.get(i).get("source_name").toString());
			}	

			sourceGroupsHelperPage.createSoucregroup(sourceGroupName, toastMessage);
			sourceGroupsHelperPage.addAllsoucrestotheSoucregroup(sourceGroupName, sourceNames, null);
			sourceGroupsHelperPage.deleteSoucreGroup(sourceGroupName, test);
		}
	}



	@DataProvider(name = "Navigation-info")
	public final Object[][] Navigation() {
		return new Object[][] { 	

			{"Direct- add allsources to the selected soucregroup",direct_org_email, common_password,direct_org_id,PageName.BACKUPJOB_REPORTS, "AddAllsoucres"+UUID.randomUUID(),null},

		};
	}
	@Test(dataProvider = "Navigation-info", enabled=true)
	public void CreateSoucreGroupOnceNavigateFormanotherPageTest(String TestCase,
			String Username,
			String password,
			String orgId,
			PageName PageName,
			String sourceGroupName,
			String toastMessage) {

		test=ExtentManager.getNewTest(TestCase);
		ArrayList<HashMap<String, Object>> sourcesInfo = null;
		ArrayList<String > sourceNames = new ArrayList<>();
		sourceGroupsHelperPage.login_Spog(Username, password);
		//Create sources
		spogServer.createSource("UI_automation"+UUID.randomUUID(), SourceType.machine,SourceProduct.udp,orgId, Direct_site_id,
				ProtectionStatus.protect,ConnectionStatus.online,OSMajor.windows.name(),"SQLSERVER",test);

		Response response=	spogServer.getSources("", "", 1, 0, true, test);

		sourcesInfo = response.then().extract().path("data");

		int sourcesSize = sourcesInfo.size();

		if(!(sourcesSize==0)) {
			for (int i = 0; i < sourcesInfo.size(); i++) {
				sourceNames.add(sourcesInfo.get(i).get("source_name").toString());
			}	

			sourceGroupsHelperPage.createSoucregroup(sourceGroupName, toastMessage);
			sourceGroupsHelperPage.addAllsoucrestotheSoucregroup(sourceGroupName, sourceNames, null);
			sourceGroupsHelperPage.deleteSoucreGroup(sourceGroupName, test);
		}




	}






	//	@AfterMethod
	public void afterMethod(){
		sourceGroupsHelperPage.logout();
		//sourceGroupsHelperPage.destroy();
	}
	private void prepareEnv(){

		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
		spogServer.getJWTToken();



		/*spogServer.userLogin("malleswari.sykam101@gmail.com", "Mclaren@2016");
		test.log(LogStatus.INFO, "Getting the JWTToken for the Logged in user");
		Direct_validToken = spogServer.getJWTToken();

		String direct_id=	spogServer.GetLoggedinUser_UserID();
		String  Direct_Org_id = spogServer.GetOrganizationIDforUser(direct_id);
		//created site for the direct user 
		test.log(LogStatus.INFO, "Create a site/register/login to the site");
		Direct_site_id = gatewayServer.createsite_register_login(Direct_Org_id, Direct_validToken, direct_id, "ts", "1.0.0", spogServer, test);

		 */
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


		//created site for the direct user 
		test.log(LogStatus.INFO, "Create a site/register/login to the site");
		Direct_site_id = gatewayServer.createsite_register_login(direct_org_id, direct_user_validToken, direct_user_id, "ts", "1.0.0", spogServer, test);





	}





	@AfterMethod
	public void getResult(ITestResult result){
		sourceGroupsHelperPage.logout();

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
		//sourceGroupsHelperPage.destroy();
		//recycleVolumeInCDandDestroyOrg(org_model_prefix);
	}




}
