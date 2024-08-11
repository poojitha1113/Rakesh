package ui.protect;

import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;

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

import Constants.ConnectionStatus;
import Constants.JobStatus;
import Constants.JobType4LatestJob;
import Constants.OSMajor;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import InvokerServer.GatewayServer;
import InvokerServer.Org4SPOGServer;
import InvokerServer.SPOGServer;
import InvokerServer.Source4SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;
import ui.base.common.ContextualAction;
import ui.base.common.PageName;
import ui.base.common.TableHeaders;
import ui.spog.server.ColumnHelper;
import ui.spog.server.SPOGUIServer;
import ui.spog.server.SourcePageHelper;


public class SourcesTest extends base.prepare.Is4Org{

	private SPOGServer spogServer;
	private UserSpogServer userSpogServer;
	private SPOGUIServer spogUIServer;
	private Org4SPOGServer org4SpogServer;
	private Source4SPOGServer source4spogServer;
	private GatewayServer gatewayServer;
	private SourcePageHelper sourcePageHelper;
	private ColumnHelper columnHelper;
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
	
	private String postfix_email = "@arcserve.com";
	private String common_password = "Welcome*02";

	private String prefix_direct = "spogqa_rakesh_direct";
	private String direct_org_name = prefix_direct + "_org";
	private String direct_org_id=null;
	private String direct_user_name = prefix_direct + "_admin";
	private String direct_user_name_email = direct_user_name + postfix_email;
	private String direct_user_first_name = direct_user_name + "_first_name";
	private String direct_user_last_name = direct_user_name + "_last_name";
	private String final_direct_user_name_email = null;
	private String direct_user_id;
	private String direct_user_validToken;
	private String direct_site_id;
	
	private String prefix_msp = "spogqa_rakesh_msp";
	private String msp_org_name = prefix_msp + "_org";
	private String msp_user_name = prefix_msp + "_admin";
	private String msp_user_name_email = prefix_msp + "_admin" + postfix_email;
	private String msp_user_first_name = msp_user_name + "_first_name";
	private String msp_user_last_name = msp_user_name + "_last_name";
	private String msp_org_id=null;
	private String final_msp_user_name_email=null;
	private String msp_user_id;
	private String msp_user_validToken;
	
	private String prefix_msp_b = "spog_rakesh_msp_b";
	private String msp_org_name_b = prefix_msp_b + "_org";
	private String msp_user_name_email_b = msp_org_name_b + postfix_email;
	private String msp_user_first_name_b = msp_org_name_b + "_first_name";
	private String msp_user_last_name_b = msp_org_name_b + "_last_name";
	private String msp_org_id_b=null;
	private String final_msp_user_name_email_b=null;
	private String msp_user_id_b;
	private String msp_user_b_validToken;

	private String initial_sub_org_name_a = "SPOG_QA_RAKESH_BQ_sub_a";
	private String initial_sub_email_a = "spog_qa_sub_RAKESH_a@arcserve.com";
	private String initial_sub_first_name_a = "spog_qa_sub_RAKESH_a";
	private String initial_sub_last_name_a = "spog_qa_sub_CHALAMALA_a";
	private String sub_orga_user_validToken;
	private String suborga_id;
	private String suborga_user_id;;
	
	private String initial_sub_org_name_b = "SPOG_QA_RAKESH_BQ_sub_b";
	private String initial_sub_email_b = "spog_qa_sub_RAKESH_b@arcserve.com";
	private String initial_sub_first_name_b = "spog_qa_sub_RAKESH_b";
	private String initial_sub_last_name_b = "spog_qa_sub_CHALAMALA_b";
	private String sub_orgb_user_validToken;
	private String suborgb_id;
	private String suborgb_user_id;;
	
	private String initial_sub_org_name_1 = "SPOG_QA_RAKESH_BQ_sub_1";
	private String initial_sub_email_1 = "spog_qa_sub_RAKESH_1@arcserve.com";
	private String initial_sub_first_name_1 = "spog_qa_sub_RAKESH_1";
	private String initial_sub_last_name_1 = "spog_qa_sub_CHALAMALA_";
	private String sub_org1_user_validToken;
	private String suborg1_id;
	private String suborg1_user_id;;
	
	private String prefix_msp_account_admin = "spog_rakesh_msp_account";
	private String msp_account_admin_email = prefix_msp_account_admin+postfix_email;
	private String msp_account_admin_first_name = prefix_msp_account_admin+"_first_name";
	private String msp_account_admin_last_name = prefix_msp_account_admin+"_last_name";
	private String msp_account_admin_id;
	private String msp_account_admin_validToken;
	
	private String prefix_msp_account_admin_b = "spog_rakesh_msp_account_b";
	private String msp_account_admin_email_b = prefix_msp_account_admin_b+postfix_email;
	private String msp_account_admin_first_name_b = prefix_msp_account_admin_b+"_first_name";
	private String msp_account_admin_last_name_b = prefix_msp_account_admin_b+"_last_name";
	private String msp_account_admin_id_b;
	private String msp_account_admin_b_validToken;
	
	private String  org_model_prefix=this.getClass().getSimpleName();
	ArrayList<HashMap<String, Object>> sources = null;
	ArrayList<String> sourceNames = new ArrayList<>();
	ArrayList<String> sourceIds = new ArrayList<>();

	@BeforeClass
	@Parameters({ "baseURI", "port",   "logFolder", "csrAdminUserName", "csrAdminPassword", "buildVersion", "uiBaseURI", "browserType", "maxWaitTimeSec", "testName"})
	public void beforeClass(String baseURI, String port, String logFolder, String adminUserName, String adminPassword,  String buildVersion,
							String uiBaseURI, String browserType, int maxWaitTimeSec, String testName) throws UnknownHostException {

		org4SpogServer = new Org4SPOGServer(baseURI, port);
		spogServer = new SPOGServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		source4spogServer = new Source4SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		this.csrAdminUserName = adminUserName;
		this.csrAdminPassword = adminPassword;
		rep = ExtentManager.getInstance(testName,logFolder);
		test = rep.startTest("beforeClass");
		
		this.BQName = "UI_"+this.getClass().getSimpleName();
		String author = "Kanamarlapudi, Chandra Kanth";
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
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// csr login
		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
		csr_token = spogServer.getJWTToken();
			
		if (uiBaseURI.contains("lccitc")) {
			sourcePageHelper = new SourcePageHelper(browserType, maxWaitTimeSec);
			columnHelper = new ColumnHelper(browserType, maxWaitTimeSec);
			sourcePageHelper.openUrl(uiBaseURI);
			sourcePageHelper.login_Spog("test.direct10001+dirs27@gmail.com", "Mclaren@2020");	
		}else {
			//Create the organizations and users
			prepareEnv();
			
			sourcePageHelper = new SourcePageHelper(browserType, maxWaitTimeSec);
			columnHelper = new ColumnHelper(browserType, maxWaitTimeSec);
			sourcePageHelper.openUrl(uiBaseURI);
			sourcePageHelper.login_Spog(final_direct_user_name_email, common_password);
		}	
		
		sourcePageHelper.navigateToSourcePageAndEnableAllColumns();		
	}

	@DataProvider(name="agentInfo")
	public Object[][] agentInfo(){
		return new Object[][] {
			{"Download agent with os_type Windows and system_type 64 bit", "Windows", "64 bit", "Replication_Agent_Setup"},
			{"Download agent with os_type Windows and system_type 32 bit", "Windows", "32 bit", "Replication_Agent_Setup"},
			{"Download agent with os_type Linux and system_type 64 bit", "Linux", "64 bit", "Arcserve_UDP_Cloud_Direct_Agent_Setup"},
			{"Download agent with os_type Linux and system_type 32 bit", "Linux", "64 bit", "Arcserve_UDP_Cloud_Direct_Agent_Setup"},
			{"Download agent with os_type Mac OS and system_type 64 bit", "Mac OS", "64 bit", "Arcserve_UDP_Cloud_Direct_Agent_Setup"},
			{"Download agent with os_type Mac OS and system_type 32 bit", "Mac OS", "64 bit", "Arcserve_UDP_Cloud_Direct_Agent_Setup"},
			
			{"Download OVA", null, null, "VMwareAppliance"},
		};
	}
	
	@Test(dataProvider="agentInfo")
	public void downloadAgentTest(String caseType, String osType, String systemType, String fileName) {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		
		String downloadPath = System.getProperty("user.home")+"\\Downloads"; 
		
		if (caseType.contains("agent")) {
			sourcePageHelper.downloadAgentWithCheck(osType, systemType, downloadPath, fileName, test);	
		} else if(caseType.contains("OVA")){	
			sourcePageHelper.downloadOVAWithCheck(downloadPath, fileName, test);
		}
		
	}
	
	@DataProvider(name="sourceInfo")
	public Object[][] sourceInfo(){
		return new Object[][] {
			{sourceNames.get(0), sources.get(0)},
			{sourceNames.get(1), sources.get(1)},
			{sourceNames.get(2), sources.get(2)},
			{sourceNames.get(3), sources.get(3)},
			{sourceNames.get(4), sources.get(4)},
			{sourceNames.get(5), sources.get(5)},
			{sourceNames.get(6), sources.get(6)},
			{sourceNames.get(7), sources.get(7)},
			{sourceNames.get(8), sources.get(8)},
			{sourceNames.get(9), sources.get(9)},
			{sourceNames.get(10), sources.get(10)},
			{sourceNames.get(11), sources.get(11)},
			{sourceNames.get(12), sources.get(12)},
			{sourceNames.get(13), sources.get(13)},
			{sourceNames.get(14), sources.get(14)},
			{sourceNames.get(15), sources.get(15)},
			{sourceNames.get(16), sources.get(16)},
			{sourceNames.get(17), sources.get(17)},
			{sourceNames.get(18), sources.get(18)},
			{sourceNames.get(19), sources.get(19)},
		};
	}
	
	@Test(dataProvider="sourceInfo", enabled=true)
	public void sourceInformatoionTest(String sourceName, HashMap<String, Object> expectedInfo){
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
				
		test.log(LogStatus.INFO, "check details for source:"+sourceName);
		sourcePageHelper.checkAddedSourceInformation(sourceName, expectedInfo, test);		
	}
	
	@DataProvider(name="sortInfo")
	public Object[][] sortInfo(){
		return new Object[][] {
			{"Sort by source name in ascending order", "Name", "asc", "source_name;asc"},
			{"Sort by source name in descending order", "Name", "desc", "source_name;desc"},
			
		};
	}
	@Test(dataProvider="sortInfo")
	public void sortTest(String caseType, String columnName, String sortOrder, String apiSortStr) {
		
		test=ExtentManager.getNewTest(caseType);
						
		sourcePageHelper.goToPage(PageName.SOURCE);
		
		test.log(LogStatus.INFO, "Sort by: "+columnName+" in order:"+sortOrder); 
		columnHelper.sortByColumnNameWithCheck(columnName, sortOrder, test);
	}
	
	
	
	@DataProvider(name="filterInfo")
	public Object[][] filterInfo(){
		return new Object[][] {
			{"Search by source name", sourceNames.get(0), null, "source_name;=;"+sourceNames.get(0)},
					
			{"Search by Protection status protected", null, "Protection Status;Protected", "protection_status;=;"+ProtectionStatus.protect.name()},
			{"Search by Protection status not protected", null, "Protection Status;Not Protected", "protection_status;=;"+ProtectionStatus.unprotect.name()},
			{"Search by Protection status protected", null, "Protection Status;Protected|Not Protected", "protection_status;in;"+ProtectionStatus.protect.name()+"|"+ProtectionStatus.unprotect.name()},
			
			{"Search by Connection status online", null, "Connection Status;Online", "connection_status;=;"+ConnectionStatus.online},
			{"Search by Connection status offline", null, "Connection Status;Offline", "connection_status;=;"+ConnectionStatus.offline},
			{"Search by Connection status uknown", null, "Connection Status;Unknown", "connection_status;=;unknown"},
			{"Search by Connection status uknown", null, "Connection Status;Online|Offline", "connection_status;=;"+ConnectionStatus.online+"|"+ConnectionStatus.offline},
						
			{"Search by job status finished", null, "Job Status;Finished", "last_job;=;finished"},
			{"Search by job status In progress", null, "Job Status;In progress", "last_job;=;active"},
			{"Search by job status Canceled", null, "Job Status;Canceled", "last_job;=;canceled"},
			{"Search by job status Failed", null, "Job Status;Failed", "last_job;=;failed"},
			{"Search by job status Warning", null, "Job Status;Warning", "last_job;=;warning"},
			{"Search by job status Skipped", null, "Job Status;Skipped", "last_job;=;skipped"},
			{"Search by job status Stopped", null, "Job Status;Stopped", "last_job;=;stop"},
			{"Search by job status Waiting", null, "Job Status;Waiting", "last_job;=;waiting"},
			
			{"Search by os Windows", null, "OS;Windows", "operating_system;=;windows"},
			{"Search by os Mac OS", null, "OS;Mac OS", "operating_system;=;mac"},
			{"Search by os linux", null, "OS;Linux", "operating_system;=;linux"},
			{"Search by os Unknown", null, "OS;Unknown", "operating_system;=;unkown"},
			
			
			{"Search by protection status not protect and Connection status offline", null, "Protection Status;Not Protected,Connection Status;Offline",
						"protection_status;=;"+ProtectionStatus.unprotect.name()+","+"connection_status;=;"+ConnectionStatus.offline},
			{"Search by protection status protected and Connection status Online", null, "Protection Status;Protected,Connection Status;Online",
						"protection_status;=;"+ProtectionStatus.protect.name()+","+"connection_status;=;"+ConnectionStatus.online},
			{"Search by protection status protected and Connection status offline", null, "Protection Status;Protected,Connection Status;Offline",
							"protection_status;=;"+ProtectionStatus.protect.name()+","+"connection_status;=;"+ConnectionStatus.offline},
			{"Search by protection status not protect and Connection status offline and os windows", null, "Protection Status;Not Protected,Connection Status;Offline,OS;Windows",
								"protection_status;=;"+ProtectionStatus.unprotect.name()+","+"connection_status;=;"+ConnectionStatus.offline+","+"operating_system;=;windows"},
			
			{"Search by protection status not protect and Connection status offline and Job status finished", null, "Protection Status;Not Protected,Connection Status;Offline,Job Status;Finished",
						"protection_status;=;"+ProtectionStatus.unprotect.name()+","+"connection_status;=;"+ConnectionStatus.offline+","+"last_job;=;finished"},
			{"Search by protection status not protect and Connection status offline and Job status failed", null, "Protection Status;Not Protected,Connection Status;Offline,Job Status;Failed",
						"protection_status;=;"+ProtectionStatus.unprotect.name()+","+"connection_status;=;"+ConnectionStatus.offline+","+"last_job;=;failed"},
			{"Search by protection status not protect and Connection status offline and Job status skipped", null, "Protection Status;Not Protected,Connection Status;Offline,Job Status;Skipped",
						"protection_status;=;"+ProtectionStatus.unprotect.name()+","+"connection_status;=;"+ConnectionStatus.offline+","+"last_job;=;skipped"},
			{"Search by protection status not protect and Connection status offline and Job status waiting", null, "Protection Status;Not Protected,Connection Status;Offline,Job Status;Waiting",
						"protection_status;=;"+ProtectionStatus.unprotect.name()+","+"connection_status;=;"+ConnectionStatus.offline+","+"last_job;=;waiting"},
			{"Search by protection status not protect and Connection status offline and Job status active", null, "Protection Status;Not Protected,Connection Status;Offline,Job Status;In progress",
						"protection_status;=;"+ProtectionStatus.unprotect.name()+","+"connection_status;=;"+ConnectionStatus.offline+","+"last_job;=;active"},
			{"Search by protection status not protect and Connection status offline and Job status canceled", null, "Protection Status;Not Protected,Connection Status;Offline,Job Status;Canceled",
						"protection_status;=;"+ProtectionStatus.unprotect.name()+","+"connection_status;=;"+ConnectionStatus.offline+","+"last_job;=;canceled"},
			{"Search by protection status not protect and Connection status offline and Job status stopped", null, "Protection Status;Not Protected,Connection Status;Offline,Job Status;Stopped",
						"protection_status;=;"+ProtectionStatus.unprotect.name()+","+"connection_status;=;"+ConnectionStatus.offline+","+"last_job;=;stop"},
			{"Search by protection status not protect and Connection status offline and Job status warning", null, "Protection Status;Not Protected,Connection Status;Offline,Job Status;Warning",
						"protection_status;=;"+ProtectionStatus.unprotect.name()+","+"connection_status;=;"+ConnectionStatus.offline+","+"last_job;=;warning"},
			{"Search by protection status not protect and Connection status online and Job status finished", null, "Protection Status;Not Protected,Connection Status;Online,Job Status;Finished",
						"protection_status;=;"+ProtectionStatus.unprotect.name()+","+"connection_status;=;"+ConnectionStatus.online+","+"last_job;=;finished"},
			{"Search by protection status not protect and Connection status online and Job status failed", null, "Protection Status;Not Protected,Connection Status;Online,Job Status;Failed",
						"protection_status;=;"+ProtectionStatus.unprotect.name()+","+"connection_status;=;"+ConnectionStatus.online+","+"last_job;=;failed"},
			{"Search by protection status not protect and Connection status online and Job status skipped", null, "Protection Status;Not Protected,Connection Status;Online,Job Status;Skipped",
						"protection_status;=;"+ProtectionStatus.unprotect.name()+","+"connection_status;=;"+ConnectionStatus.online+","+"last_job;=;skipped"},
			{"Search by protection status not protect and Connection status online and Job status waiting", null, "Protection Status;Not Protected,Connection Status;Online,Job Status;Waiting",
						"protection_status;=;"+ProtectionStatus.unprotect.name()+","+"connection_status;=;"+ConnectionStatus.online+","+"last_job;=;waiting"},
			{"Search by protection status not protect and Connection status online and Job status active", null, "Protection Status;Not Protected,Connection Status;Online,Job Status;In progress",
						"protection_status;=;"+ProtectionStatus.unprotect.name()+","+"connection_status;=;"+ConnectionStatus.online+","+"last_job;=;active"},
			{"Search by protection status not protect and Connection status online and Job status canceld", null, "Protection Status;Not Protected,Connection Status;Online,Job Status;Canceled",
						"protection_status;=;"+ProtectionStatus.unprotect.name()+","+"connection_status;=;"+ConnectionStatus.online+","+"last_job;=;canceled"},
			{"Search by protection status not protect and Connection status online and Job status stopped", null, "Protection Status;Not Protected,Connection Status;Online,Job Status;Stopped",
						"protection_status;=;"+ProtectionStatus.unprotect.name()+","+"connection_status;=;"+ConnectionStatus.online+","+"last_job;=;stop"},
			
		};
	}
	
	@Test(dataProvider="filterInfo")
	public void sourceSearchTest(String caseType, String search_string, String filter, String apiFilter) {

		test=ExtentManager.getNewTest(caseType);
		ArrayList<HashMap<String, Object>> expectedInfo = null;			
		ArrayList<String> filters = new ArrayList<>();
		
		expectedInfo= getSourcesInfo(apiFilter, null, test);
		
		if (filter != null && !filter.isEmpty()) {
			String[] multipleFilters = filter.split(",");
			for (int i = 0; i < multipleFilters.length; i++) {
				filters.add(multipleFilters[i]);
			}	
		}				

		sourcePageHelper.searchSourceWithCheck(search_string, filters, expectedInfo, test);
		
		test.log(LogStatus.INFO, "Clear the search filters without saving");
		sourcePageHelper.clearSearchWithoutSaving();
	}
		
//	@DataProvider(name="manageSaveSearchInfo")
	public Object[][] manageSaveSearchInfo(){
		return new Object[][] {
			
			{"Create a save search with filters: Protection Status;Not Protected,Connection Status;Offline and update with filters: Protection Status;Not Protected,Protected&Connection Status;Offline,Online,Unknown",
				spogServer.ReturnRandom("ss"), spogServer.ReturnRandom("ss"), null, false,
				"Protection Status;Not Protected,Connection Status;Offline",												//UI filter
				"protection_status;=;"+ProtectionStatus.unprotect+",connection_status;=;"+ConnectionStatus.offline,			// api filter
				"Protection Status;Not Protected|Protected,Connection Status;Offline|Online|Unknown",						//UI update filter
				"protection_status;=;"+ProtectionStatus.unprotect+"|"+ProtectionStatus.protect+",connection_status;=;"+ConnectionStatus.offline+"|"+ConnectionStatus.online+"|unknown"}, //api update filter
				
			{"Create a save search with filters: Job Status;Waiting,Connection Status;Offline and update with filters: Job Status;Finished|Failed,Connection Status;Offline|Online",
						spogServer.ReturnRandom("ss"), spogServer.ReturnRandom("ss"), null, false,
						"Job Status;Waiting,Connection Status;Offline",												//UI filter
						"last_job;=;"+JobStatus.waiting+",connection_status;=;"+ConnectionStatus.offline,			// api filter
						"Job Status;Finished|Failed,Connection Status;Offline|Online",						//UI update filter
						"last_job;=;"+JobStatus.finished+"|"+JobStatus.failed+"|"+JobStatus.waiting+",connection_status;=;"+ConnectionStatus.offline+"|"+ConnectionStatus.online}, //api update filter
				
				
				{"Create a save search with filters: Protection Status;Not Protected,Connection Status;Offline,Job Status;Finished|Failed and update with filters: Protection Status;Not Protected,Protected&Connection Status;Offline,Online,Job Status;Skipped|Warning",
							spogServer.ReturnRandom("ss"), spogServer.ReturnRandom("ss"), null, false,
							"Protection Status;Protected,Connection Status;Online,Job Status;Finished|Failed",												//UI filter
							"protection_status;=;"+ProtectionStatus.protect+",connection_status;=;"+ConnectionStatus.online+",last_job;=;finished|failed",			// api filter
							"Protection Status;Not Protected|Protected,Connection Status;Offline|Online,Job Status;Skipped|Warning",						//UI update filter
							"protection_status;=;"+ProtectionStatus.unprotect+"|"+ProtectionStatus.protect+",connection_status;=;"+ConnectionStatus.offline+"|"+ConnectionStatus.online+",last_job;=;finished|failed|warning|skipped"}, //api update filter
				
				
				{"Create a save search with filters: Protection Status;Not Protected,Connection Status;Offline,operating_system;windows and update with filters: Protection Status;Not Protected,Protected&Connection Status;Offline,Online,Unknown,operating_system;linux",
								spogServer.ReturnRandom("ss"), spogServer.ReturnRandom("ss"), null, false,
								"Protection Status;Not Protected,Connection Status;Offline,OS;Windows",												//UI filter
								"protection_status;=;"+ProtectionStatus.unprotect+",connection_status;=;"+ConnectionStatus.offline+",operating_system;=;windows",			// api filter
								"Protection Status;Not Protected|Protected,Connection Status;Offline|Online|Unknown,OS;Linux",						//UI update filter
								"protection_status;=;"+ProtectionStatus.unprotect+"|"+ProtectionStatus.protect+",connection_status;=;"+ConnectionStatus.offline+"|"+ConnectionStatus.online+"|unknown,operating_system;=;linux|windows"}, //api update filter
				
				
				{"Create a save search with filters: Protection Status;Not Protected,Connection Status;Offline,last_job;=;canceled and update with filters: Protection Status;Not Protected,Protected&Connection Status;Offline,Online,last_job;=;waiting",
									spogServer.ReturnRandom("ss"), spogServer.ReturnRandom("ss"), null, false,
									"Protection Status;Not Protected,Connection Status;Offline,Job Status;Canceled",												//UI filter
									"protection_status;=;unprotect,connection_status;=;offline,last_job;=;canceled",			// api filter
									"Protection Status;Not Protected|Protected,Connection Status;Offline|Online,Job Status;Waiting",						//UI update filter
									"protection_status;=;unprotect|protect,connection_status;=;offline|online,last_job;=;waiting|canceled"}, //api update filter
				
				
				{"Create a save search with filters: Protection Status;Not Protected,Connection Status;Offline and update with filters: Protection Status;Not Protected,Protected&Connection Status;Offline,Online,Unknown",
										spogServer.ReturnRandom("ss"), spogServer.ReturnRandom("ss"), null, false,
										"Protection Status;Not Protected,Connection Status;Offline,OS;Windows",												//UI filter
										"protection_status;=;unprotect,connection_status;=;offline,operating_system;=;windows",			// api filter
										"Protection Status;Not Protected|Protected,Connection Status;Offline|Online,Job Status;Finished",						//UI update filter
										"protection_status;=;unprotect|protect,connection_status;=;offline|online,operating_system;=;windows,last_job;=;finished"}, //api update filter
					
			
		};
	}
	
	@Test(dataProvider="manageSaveSearchInfo", dataProviderClass = SourceDataProvider.class)
	public void manageSaveSearchTest(String caseType,
										String searchName,
										String newSearchName,
										String search_string,
										boolean makeDefault,
										String filter,
										String apiFilter,
										String updateFilter,
										String apiUpdateFilter
										) {
		
		test=ExtentManager.getNewTest(caseType);
		ArrayList<HashMap<String, Object>> expectedInfo = null;
		ArrayList<String> filters = null;
		ArrayList<String> updatefilters = null;
		
		filters = getFilterArray(filter);	
		sourcePageHelper.saveSearchWithCheck(searchName, search_string, filters, test);
		sourcePageHelper.applySaveSearchWithCheck(searchName, test);
		
		test.log(LogStatus.INFO, "validate the savesearch info after creating");
		expectedInfo = getSourcesInfo(apiFilter, null, test);
		sourcePageHelper.validateSourceInfomation(expectedInfo, test);
		
		
		test.log(LogStatus.INFO, "Modify the save search");
		updatefilters = getFilterArray(updateFilter);
		sourcePageHelper.manageSaveSearchWithCheck(searchName, newSearchName, search_string, makeDefault, updatefilters, test);
		
		if (!(newSearchName != null && !newSearchName.isEmpty())) {
			newSearchName = searchName;
		}
		sourcePageHelper.applySaveSearchWithCheck(newSearchName, test);
		
		test.log(LogStatus.INFO, "validate the savesearch info after updating");
		expectedInfo = getSourcesInfo(apiUpdateFilter, null, test);
		sourcePageHelper.validateSourceInfomation(expectedInfo, test);		
		
		sourcePageHelper.deleteSaveSearchWithCheck(newSearchName, test);	
		
	}
	
	
	
	
	@DataProvider(name="makeSaveSearchDefaultInfo")
	public Object[][] makeSaveSearchDefaultInfo(){
		return new Object[][] {
			{"Create a save search with filters Protection Status;Not Protected,Protected&Connection Status;Offline,Online,Unknown and make it default",
				spogServer.ReturnRandom("as"), "src", true, "Protection Status;Not Protected|Protected,Connection Status;Offline|Online|Unknown"}
		};
	}
	
	@Test(dataProvider="makeSaveSearchDefaultInfo")
	public void makeSavedSearchDefault(String caseType,
										String searchName,
										String search_string,
										boolean makeDefault,
										String filter
										) {
		
		test=ExtentManager.getNewTest(caseType);
		ArrayList<String> filters = new ArrayList<>();
		
		filters = getFilterArray(filter);		
		sourcePageHelper.saveSearchWithCheck(searchName, search_string, filters, test);

		sourcePageHelper.makeSaveSearchDefaultWithCheck(searchName, makeDefault, test);
		sourcePageHelper.deleteSaveSearchWithCheck(searchName, test);
	}
	
	
	@DataProvider(name="deleteInfo")
	public Object[][] deleteInfo(){
		return new Object[][] {
			{"Delete source with name:"+sourceNames.get(0), sourceNames.get(0), true},
		};
	}
	
	@Test(dataProvider="deleteInfo")
	public void deleteSourceTest(String caseType, String sourceName, boolean delete) {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		
		
		test.log(LogStatus.INFO, caseType);
		sourcePageHelper.deleteSourceWithCheck(sourceName, delete, test);
		
	}
	
	
	/** Method to get the sources information from API and compose as required for UI validation
	 * 
	 * @author Rakesh.Chalamala
	 * @param filter
	 * @param sort
	 * @param test
	 * @return
	 */
	public ArrayList<HashMap<String, Object>> getSourcesInfo(String filter, String sort, ExtentTest test) {
		
//		String direct_org_id = "5d1a5a55-7434-459d-a70e-1d089e472e60";
		String filterStr = null;
		HashMap<String, Object> sourceInfo = null;
		ArrayList<HashMap<String, Object>> expectedSourcesInfo = new ArrayList<>(); 
		int sourceCount;
		
		if (filter != null && !filter.isEmpty()) {
			filterStr = "organization_id;=;"+direct_org_id+","+filter;	
		}else {
			filterStr = "organization_id;=;"+direct_org_id;
		}
		
		filterStr = filterStr.replace("job_status", "last_job");
		filterStr = filterStr.replace("stopped", "stop");
		filterStr = filterStr.replace("not_protected", "unprotect");
		filterStr = filterStr.replace("protected", "protect");
		filterStr = filterStr.replace("in_progress", "active");
		filterStr = filterStr.replace("mac_os", "mac");
		filterStr = filterStr.replace("os", "operating_system");
		
			
		spogServer.setToken(csr_token);
		Response response = spogServer.getSources(filterStr, sort, 1, 20, true, test);
		ArrayList<HashMap<String, Object>> actualInfo = response.then().extract().path("data");
		
		sourceCount = actualInfo.size();		
		
		for (int i = 0; i < sourceCount; i++) {
			
			sourceInfo = new HashMap<>();
			
			sourceInfo.put(TableHeaders.type, response.then().extract().path("data["+i+"].source_type"));
			sourceInfo.put(TableHeaders.name, response.then().extract().path("data["+i+"].source_name"));
			sourceInfo.put(TableHeaders.os, response.then().extract().path("data["+i+"].operating_system.os_major"));
			sourceInfo.put(TableHeaders.status, response.then().extract().path("data["+i+"].protection_status"));
			
			String connectionStatus = response.then().extract().path("data["+i+"].connection_status");
			if (connectionStatus == null) {
				sourceInfo.put(TableHeaders.connection, "-");	
			}else {
				sourceInfo.put(TableHeaders.connection, connectionStatus);
			}
						
			if (response.then().extract().path("data["+i+"].last_recovery_point_ts") == null) {
				sourceInfo.put(TableHeaders.latest_recovery_point, "-");	
			}else {
				int latest_recovery_point = response.then().extract().path("data["+i+"].last_recovery_point_ts");	
				sourceInfo.put(TableHeaders.latest_recovery_point, convertTimeStampToDateAndTime(latest_recovery_point));	
			}			
			
			ArrayList<HashMap<String, Object>> last_jobs = response.then().extract().path("data["+i+"].last_job");
			
			if (last_jobs.size() == 0) {
				sourceInfo.put(TableHeaders.latest_job, "-");	
			} else if (last_jobs.size() == 1) {
				
				String job_type = last_jobs.get(0).get("type").toString();
						
				if (job_type.equals(JobType4LatestJob.backup_full.toString())) {
					job_type = "Backup-Full";
				}else if (job_type.equals(JobType4LatestJob.backup_incremental.toString())) {
					job_type = "Backup-Incremental";
				}else if (job_type.equals(JobType4LatestJob.restore.toString())) {
					job_type = "Restore";
				}else if (job_type.equals(JobType4LatestJob.rps_replicate_in_bound.toString())) {
					job_type = "Replication(IN)";
				}else if (job_type.equals(JobType4LatestJob.rps_replicate.toString())) {
					job_type = "Replication(OUT)";
				}else if (job_type.equals(JobType4LatestJob.rps_merge.toString())) {
					job_type = "Merge";
				}
				sourceInfo.put(TableHeaders.latest_job, job_type);
				
			}else if(last_jobs.size()>1){
				sourceInfo.put(TableHeaders.latest_job, last_jobs.size()+ " Jobs");
			}
			
			if (response.then().extract().path("data["+i+"].policy") == null) {
				sourceInfo.put(TableHeaders.policy, "-");
			}else {
				sourceInfo.put(TableHeaders.policy, ((ArrayList<String>)response.then().extract().path("data["+i+"].policy.policy_name")).get(0));
			}
			
			
			
			ArrayList<String> SG = response.then().extract().path("data["+i+"].source_group");
			
			if (SG.size() == 0) {
				sourceInfo.put(TableHeaders.source_group, "-");	
			}else if (SG.size() == 1) {
				sourceInfo.put(TableHeaders.source_group, SG.get(0));
			}else if (SG.size() > 1) {
				sourceInfo.put(TableHeaders.source_group, SG.size()+" Groups");
			}
			
			String vmName = response.then().extract().path("data["+i+"].vm_name");
			if (vmName == null) {
				sourceInfo.put(TableHeaders.vm_name, "-");	
			}else {
				sourceInfo.put(TableHeaders.vm_name, vmName);
			}
			
			if (response.then().extract().path("data["+i+"].agent.agent_name") == null) {
				sourceInfo.put(TableHeaders.agent, "-");
			}else {
				sourceInfo.put(TableHeaders.agent, response.then().extract().path("data["+i+"].agent.agent_name"));
			}
			
			
			String hypervisorName = response.then().extract().path("data["+i+"].hypervisor");
			if (hypervisorName == null) {
				sourceInfo.put(TableHeaders.hypervisor, "-");	
			}else {
				sourceInfo.put(TableHeaders.hypervisor, hypervisorName);
			}
			
			ArrayList<String> available_actions = response.then().extract().path("data["+i+"].available_actions");
			ArrayList<String> expAvailableActions = new ArrayList<>();
			
			for (int j = 0; j < available_actions.size(); j++) {
				if (available_actions.get(j).equalsIgnoreCase("startbackup")) {
					expAvailableActions.add(ContextualAction.START_BACKUP);
				}else if (available_actions.get(j).equalsIgnoreCase("cancelbackup")) {
					expAvailableActions.add(ContextualAction.CANCEL_BACKUP);
				}else if (available_actions.get(j).equalsIgnoreCase("startrecovery")) {
					expAvailableActions.add(ContextualAction.START_RECOVERY);
				}else if (available_actions.get(j).equalsIgnoreCase("assignpolicy")) {
					expAvailableActions.add(ContextualAction.ASSIGN_POLICY);
				}else if (available_actions.get(j).equalsIgnoreCase("unassignpolicy")) {
					expAvailableActions.add(ContextualAction.REMOVE_POLICY);
				}else if (available_actions.get(j).equalsIgnoreCase("delete")) {
					expAvailableActions.add(ContextualAction.DELETE);
				}else if (available_actions.get(j).equalsIgnoreCase("startrepliationout")) {
					expAvailableActions.add(ContextualAction.START_REPLICATION);
				}else if (available_actions.get(j).equalsIgnoreCase("cancelrepliationout")) {
					expAvailableActions.add(ContextualAction.CANCEL_REPLICATION_OUT);
				}else if (available_actions.get(j).equalsIgnoreCase("provision")) {
					expAvailableActions.add(ContextualAction.PROVISION);
				}else {
					assertTrue(false, "contextual action:"+available_actions.get(j)+" does not match");
				}
			}
			
			sourceInfo.put("available_actions", expAvailableActions);
			
			sourceNames.add(sourceInfo.get(TableHeaders.name).toString());
			expectedSourcesInfo.add(sourceInfo);
			
		}
		
		return expectedSourcesInfo;
	}
	
	private void prepareEnv(){

		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
		csr_token = spogServer.getJWTToken();
		
		//************************create direct organization and users*************************************

		String prefix = RandomStringUtils.randomAlphanumeric(8);
		spogServer.setToken(csr_token);
		this.final_direct_user_name_email = prefix+this.direct_user_name_email;
		direct_org_id = spogServer.CreateOrganizationWithCheck(prefix+direct_org_name+org_model_prefix, SpogConstants.DIRECT_ORG, this.final_direct_user_name_email, common_password, prefix+direct_user_first_name, prefix+direct_user_last_name,test);
		spogServer.userLogin(this.final_direct_user_name_email, common_password);
		test.log(LogStatus.INFO,"Getting the JWTToken for the Logged in user");
		direct_user_validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The token is :"+ direct_user_validToken);
		direct_user_id = spogServer.GetLoggedinUser_UserID();
		
		test.log(LogStatus.INFO,"Creating a site For direct org and logging and registering and logging a site ");
		direct_site_id = gatewayServer.createsite_register_login(direct_org_id, direct_user_validToken, direct_user_id, "rakesh", "1.0.0", spogServer, test);
		test.log(LogStatus.INFO,"The direct_site_id:"+direct_site_id);
		
		createSources();
		sources = getSourcesInfo(null, null, test);
		
	}
	
	@SuppressWarnings("unchecked")
	public void createSources() {
		
		String org_id = direct_org_id;
		String site_id = direct_site_id;
		SourceType[] sourceTypes = {SourceType.machine, SourceType.agentless_vm};
		SourceProduct[] sourceProducts = {SourceProduct.udp, SourceProduct.cloud_direct};
		ProtectionStatus[] protectionStatus = {/*ProtectionStatus.protect,*/ ProtectionStatus.unprotect/*, ProtectionStatus.partial_protect*/};
		ConnectionStatus[] connectionStatus = {ConnectionStatus.online, ConnectionStatus.offline};
		String[] os = {OSMajor.windows.name(), OSMajor.linux.name(), OSMajor.mac.name()};
		
		String[] allJobType = {"backup", "restore", "rps_merge", "rps_replicate", "rps_replicate_in_bound"};
		String[] allJobMethod = {"full","incremental"};
		String[] allJobStatus = {/*"active",*/ "finished", "canceled", "failed", "waiting", "skipped", "stop", "missed"};
		
		
		
		for (int i = 0; i < sourceTypes.length; i++) {
			for (int j = 0; j < sourceProducts.length; j++) {
				for (int j2 = 0; j2 < protectionStatus.length; j2++) {
					for (int k = 0; k < connectionStatus.length; k++) {
						for (int k2 = 0; k2 < os.length; k2++) {
							
							String srcName =spogServer.ReturnRandom("src"); 
							
							Response response = spogServer.createSource(srcName, sourceTypes[i], sourceProducts[j], org_id, site_id, protectionStatus[j2], connectionStatus[k], os[k2], "", test);
							spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);
							
							String source_id = response.then().extract().path("data.source_id");
							
							//sourceIds.add(source_id);
							
							String jobType = allJobType[new Random().nextInt(allJobType.length)];
							String jobMethod = allJobMethod[new Random().nextInt(allJobMethod.length)];
							String jobStatus = allJobStatus[new Random().nextInt(allJobStatus.length)];
							
							gatewayServer.postJob(System.currentTimeMillis()/1000, (System.currentTimeMillis()/1000)+10, org_id, source_id, source_id, null, UUID.randomUUID().toString(), UUID.randomUUID().toString(), jobType, jobMethod, jobStatus, csr_token, test);
						}
					}
				}
			}
		}
	}
	
		
		
	@AfterMethod
	public void getResult(ITestResult result){
		
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
		sourcePageHelper.destroy();
		recycleVolumeInCDandDestroyOrg(org_model_prefix);
	}

	/*****************************Generic**********************************/
	private static String convertTimeStampToDateAndTime(int timeStamp) {
			
		Date dateTime = new Date(timeStamp * 1000L );
		
		SimpleDateFormat dateFormater = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
		dateFormater.setTimeZone(TimeZone.getTimeZone("IST"));
		String date = dateFormater.format(dateTime);
		
		return date;
	}
	
	private static ArrayList<String> getFilterArray(String filter) {
		
		ArrayList<String> filters = new ArrayList<>();
		
		if (filter != null && !filter.isEmpty()) {
			String[] multipleFilters = filter.split(",");
			for (int i = 0; i < multipleFilters.length; i++) {
				filters.add(multipleFilters[i]);
			}	
		}	
		
		return filters;
	}
		
}
