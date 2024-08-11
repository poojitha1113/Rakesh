package api.reports.reportschedules;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.ConnectionStatus;
import Constants.OSMajor;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGReportServer;
import InvokerServer.SPOGServer;
import InvokerServer.Source4SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class DeleteReportsTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private UserSpogServer userSpogServer;
	private SPOGReportServer spogReportServer;
	private SPOGDestinationServer spogDestinationServer;
	private Source4SPOGServer source4SPOGServer;
	//public int Nooftest;
	private ExtentTest test;
	//used for test case count like passed,failed,remaining cases
	int passedcases=0;
	int failedcases=0;
	int skippedcases=0;
	String buildnumber=null;

   /* private ExtentReports rep;
    private SQLServerDb bqdb1;
    public int Nooftest;
    private long creationTime;
    private String BQName=null;
    private String runningMachine;
    private testcasescount count1;
    private String buildVersion;	*/
	
	Response response;
	
	String direct_source_group_id_list;
	String msp_source_group_id_list;
	String submsp_source_group_id_list;
	String suborg_source_group_id_list;
	
	String direct_source_id;
	String msp_source_id;
	String submsp_source_id;
	String suborg_source_id;	

	LocalDate date = LocalDate.now();  
	LocalDate yesterday = date.minusDays(1);  
	LocalDate tomorrow = yesterday.plusDays(2);

	private String  org_model_prefix=this.getClass().getSimpleName();
	private TestOrgInfo ti;
	
	@BeforeClass
	@Parameters({ "baseURI", "port","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port,String logFolder,String runningMachine,String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		spogReportServer = new SPOGReportServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		source4SPOGServer = new Source4SPOGServer(baseURI, port);
		
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(), logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Kanamarlapudi, Chandra Kanth";

		Nooftest=0;
		//Used for creating a build number with dateformat
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date=new java.util.Date();
		this.buildVersion=buildVersion+"_"+dateFormater.format(date);

		BQName=this.getClass().getSimpleName();
		this.runningMachine=runningMachine;
		System.out.println("The value of hit is "+count1.isstarttimehit());


		if( count1.isstarttimehit( ) == 0 ) 
		{
			System.out.println("into creation time");
			creationTime=System.currentTimeMillis();
			count1.setcreationtime(creationTime);

			creationTime = System.currentTimeMillis();
			try
			{
				bqdb1.updateTable(BQName, this.runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0", String.valueOf(Nooftest), count1.getcreationtime(), "InProgress",author+" and Rest server is "+baseURI.split("//")[1]);
			} 
			catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		ti = new TestOrgInfo(spogServer, test);
	}
	
	@DataProvider(name="postData")
	public Object[][] postData(){
		return new Object[][] {
			
			//dataprovider to create the sources in all organizations
			{"direct",ti.direct_org1_user1_token,"sourceName1", SourceType.machine, SourceProduct.cloud_direct, ti.direct_org1_id, ProtectionStatus.protect, ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange",  "Rak_vm2",  UUID.randomUUID().toString(), "Rak_agent1", "windows 2012", "64" ,"1.0.0","2.0", "http://upgrade",4},
			{"msp",ti.root_msp_org1_user1_token,"sourceName1", SourceType.machine, SourceProduct.cloud_direct, ti.root_msp_org1_id, ProtectionStatus.protect, ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange",  "Rak_vm2",  UUID.randomUUID().toString(), "Rak_agent1", "windows 2012", "64" ,"1.0.0","2.0", "http://upgrade",4},
			{"submsp",ti.root_msp1_submsp1_user1_token,"sourceName1", SourceType.machine, SourceProduct.cloud_direct, ti.root_msp1_submsp_org1_id, ProtectionStatus.protect, ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange",  "Rak_vm2",  UUID.randomUUID().toString(), "Rak_agent1", "windows 2012", "64" ,"1.0.0","2.0", "http://upgrade",4},
			{"suborg",ti.root_msp1_suborg1_user1_token,"sourceName1", SourceType.machine, SourceProduct.cloud_direct, ti.root_msp1_suborg1_id, ProtectionStatus.protect, ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange",  "Rak_vm2",  UUID.randomUUID().toString(), "Rak_agent1", "windows 2012", "64" ,"1.0.0","2.0", "http://upgrade",4},
			
		};
	}
	
	@Test(dataProvider= "postData")
	public void postData(String orgType, String token,String source_name, SourceType source_type, SourceProduct source_product,
			String organization_id, ProtectionStatus protection_status,
			ConnectionStatus connection_status, String os_major, String applications, String vm_name,
			String hypervisor_id, String agent_name, String os_name, String os_architecture,
			String agent_current_version, String agent_upgrade_version, String agent_upgrade_link, 
			int noOfSourcesToCreate) {
		
		String source_group_id_list = null; 	String source_group_id  = null, source_id = null;
		HashMap<String, Object> SourceInfo = new HashMap<>();
		String site_name = "cloudAccountName";//common for all cloud_accounts	
		String hypervisor_name = null;
		ArrayList<HashMap<String, Object>> SourceList = new ArrayList<>();
		
		/*spogServer.setToken(site_token);
		test.log(LogStatus.INFO, "Creating source in the organization of type: "+ orgType);
		source_id = spogServer.createSourceWithCheck(source_name, source_type, source_product, organization_id, site_id,
				protection_status, connection_status, os_major, applications, vm_name, hypervisor_id, agent_name,
				os_name, os_architecture, agent_current_version, agent_upgrade_version, agent_upgrade_link, test);*/
		
		spogServer.setToken(token);
		for (int i = 0; i < noOfSourcesToCreate; i++) {
			
			test.log(LogStatus.INFO, "Create source group number: "+i);
			source_group_id = spogServer.createGroupWithCheck(organization_id, spogServer.ReturnRandom("group_name"), spogServer.ReturnRandom("group_description"), test);
			
			if (source_group_id_list == (null)) {
				source_group_id_list = source_group_id;
			} else {
				source_group_id_list = source_group_id_list+","+source_group_id ;
			}		
			
		}
		
		
		SourceInfo.put("source_group", source_group_id);
		
		//Adding to the global varibles to call them and delete in after class
		if (orgType.contains("direct")) {
			direct_source_group_id_list = source_group_id_list;
			direct_source_id = source_id;
		}else if (orgType.contains("submsp")) {
			submsp_source_group_id_list = source_group_id_list;
			submsp_source_id = source_id;
		}else if(orgType.contains("msp")) {
			msp_source_group_id_list = source_group_id_list;
			msp_source_id = source_id;
		}else if(orgType.contains("suborg")) {
			suborg_source_group_id_list = source_group_id_list;
			suborg_source_id = source_id;
		}
		
	}

	@DataProvider(name = "reportScheduleData")
	public Object[][] reportScheduleData(){
		return new Object[][] {
			{"direct",ti.direct_org1_user1_token,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
				direct_source_group_id_list,ti.direct_org1_id,"a@gmail.com,b@gmail.com",null,"all_sources",SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"msp",ti.root_msp_org1_user1_token,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"weekly",
					msp_source_group_id_list,ti.root_msp_org1_id,"a@gmail.com,b@gmail.com",null,"all_sources",SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"submsp",ti.root_msp1_submsp1_user1_token,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"weekly",
						msp_source_group_id_list,ti.root_msp1_submsp_org1_id,"a@gmail.com,b@gmail.com",null,"all_sources",SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"suborg",ti.root_msp1_suborg1_user1_token,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"quarterly",
							suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",null,"all_sources",SpogConstants.SUCCESS_GET_PUT_DELETE,null},
		};
	}
	
	@Test(dataProvider="reportScheduleData",dependsOnMethods= {"postData"})
	public void deleteReportsValid(String organizationType, String token, String report_name, String report_type, String date_range_type, long date_range_start_ts,
			long date_range_end_ts,String schedule_frequency, String source_group_id_list, String organization_id_list, String recipient_mail,
			String cron_expression,String report_for_type, int expectedStatusCode, SpogMessageCode expectedErrorMessage
			) {
		
		HashMap<String, Object> scheduleInfo = new HashMap<>();
		ArrayList<String> report_ids = new ArrayList<>();
		int noofreports = 4;
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());

		for (int i = 0; i < noofreports; i++) {
			test.log(LogStatus.INFO, "Compose report schedule info");
			scheduleInfo = spogReportServer.composeReportScheduleInfo(report_name, report_type, date_range_type, date_range_start_ts, date_range_end_ts, schedule_frequency, source_group_id_list, organization_id_list, recipient_mail, cron_expression,report_for_type);

			test.log(LogStatus.INFO, "Create report schedule with check for the organization: "+organizationType);
			spogReportServer.createReportScheduleWithCheck(token, scheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE,null, test);
		}		

		test.log(LogStatus.INFO, "Get report schedule data for the organization: "+organizationType);
		response = spogReportServer.getReports(token,expectedStatusCode, test);
		
		for (int j = 0; j < noofreports; j++) {
			report_ids.add(response.then().extract().path("data["+j+"].report_id"));
		}		
		
		test.log(LogStatus.INFO, "Get report schedule data for the organization: "+organizationType);
		spogReportServer.deleteReportsWithCheck(token, report_ids, expectedStatusCode, expectedErrorMessage, test);						
	}
	
	
	@DataProvider(name = "reportScheduleData1")
	public Object[][] reportScheduleData1(){
		return new Object[][] {
			{"direct",ti.direct_org1_user1_token,ti.root_msp_org1_user1_token,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
				direct_source_group_id_list,ti.direct_org1_id,"a@gmail.com,b@gmail.com",null,"all_sources",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"direct",ti.direct_org1_user1_token,ti.root_msp1_suborg1_user1_token,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
					direct_source_group_id_list,ti.direct_org1_id,"a@gmail.com,b@gmail.com",null,"all_sources",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"direct",ti.direct_org1_user1_token,ti.root_msp_org1_msp_accountadmin1_token,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
						direct_source_group_id_list,ti.direct_org1_id,"a@gmail.com,b@gmail.com",null,"all_sources",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
					
			{"msp",ti.root_msp_org1_user1_token,ti.direct_org1_user1_token,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"weekly",
					msp_source_group_id_list,ti.root_msp_org1_id,"a@gmail.com,b@gmail.com",null,"all_sources",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"msp",ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_user1_token,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"weekly",
						msp_source_group_id_list,ti.root_msp_org1_id,"a@gmail.com,b@gmail.com",null,"all_sources",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"msp",ti.root_msp_org1_user1_token,ti.root_msp_org2_user1_token,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"weekly",
							msp_source_group_id_list,ti.root_msp_org1_id,"a@gmail.com,b@gmail.com",null,"all_sources",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"msp",ti.root_msp_org1_user1_token,ti.root_msp1_submsp1_user1_token,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"weekly",
								msp_source_group_id_list,ti.root_msp_org1_id,"a@gmail.com,b@gmail.com",null,"all_sources",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"msp",ti.root_msp_org1_user1_token,ti.msp1_submsp1_suborg1_user1_token,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"weekly",
									msp_source_group_id_list,ti.root_msp_org1_id,"a@gmail.com,b@gmail.com",null,"all_sources",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			
			{"submsp",ti.root_msp1_submsp1_user1_token,ti.direct_org1_user1_token,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"weekly",
					submsp_source_group_id_list,ti.root_msp1_submsp_org1_id,"a@gmail.com,b@gmail.com",null,"all_organizations",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"submsp",ti.root_msp1_submsp1_user1_token,ti.root_msp1_suborg1_user1_token,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"weekly",
					submsp_source_group_id_list,ti.root_msp1_submsp_org1_id,"a@gmail.com,b@gmail.com",null,"all_organizations",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"submsp",ti.root_msp1_submsp1_user1_token,ti.root_msp_org2_user1_token,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"weekly",
					submsp_source_group_id_list,ti.root_msp1_submsp_org1_id,"a@gmail.com,b@gmail.com",null,"all_organizations",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"submsp",ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp2_user1_token,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"weekly",
					submsp_source_group_id_list,ti.root_msp1_submsp_org1_id,"a@gmail.com,b@gmail.com",null,"all_organizations",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"submsp",ti.root_msp1_submsp1_user1_token,ti.msp1_submsp1_suborg1_user1_token,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"weekly",
					submsp_source_group_id_list,ti.root_msp1_submsp_org1_id,"a@gmail.com,b@gmail.com",null,"all_organizations",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
								
			
			{"suborg",ti.root_msp1_suborg1_user1_token,ti.direct_org1_user1_token,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"quarterly",
							suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",null,"all_sources",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"suborg",ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg2_user1_token,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"quarterly",
								suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",null,"all_sources",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			
			//csr readonly user token
			{"direct",ti.direct_org1_user1_token,ti.csr_readonly_token,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
									direct_source_group_id_list,ti.direct_org1_id,"a@gmail.com,b@gmail.com",null,"all_sources",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"msp",ti.root_msp_org1_user1_token,ti.csr_readonly_token,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"weekly",
										msp_source_group_id_list,ti.root_msp_org1_id,"a@gmail.com,b@gmail.com",null,"all_sources",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"suborg",ti.root_msp1_suborg1_user1_token,ti.csr_readonly_token,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"quarterly",
											suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",null,"all_sources",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
		};
	}
	
	@Test(dataProvider="reportScheduleData1",dependsOnMethods= {"postData"})
	public void deleteReportsInvalid_403(String organizationType, String validToken, String otherOrgToken, String report_name, String report_type, String date_range_type, long date_range_start_ts,
			long date_range_end_ts,String schedule_frequency, String source_group_id_list, String organization_id_list, String recipient_mail,
			String cron_expression,String report_for_type, int expectedStatusCode, SpogMessageCode expectedErrorMessage
			) {
		HashMap<String, Object> scheduleInfo = new HashMap<>();
		int noofreports  = 4;
		ArrayList<String> report_ids = new ArrayList<>();
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());

		for (int i = 0; i < noofreports; i++) {
			test.log(LogStatus.INFO, "Compose report schedule info");
			scheduleInfo = spogReportServer.composeReportScheduleInfo(report_name, report_type, date_range_type, date_range_start_ts, date_range_end_ts, schedule_frequency, source_group_id_list, organization_id_list, recipient_mail, cron_expression,report_for_type);

			test.log(LogStatus.INFO, "Create report schedule with check for the organization: "+organizationType);
			spogReportServer.createReportScheduleWithCheck(validToken, scheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE,null, test);
		}		

		test.log(LogStatus.INFO, "Get report schedule data for the organization: "+organizationType);
		response = spogReportServer.getReports(validToken,SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		
		for (int j = 0; j < noofreports; j++) {
			report_ids.add(response.then().extract().path("data["+j+"].report_id"));
		}		
		
		test.log(LogStatus.INFO, "Get report schedule data for the organization: "+organizationType);
		spogReportServer.deleteReportsWithCheck(otherOrgToken, report_ids, expectedStatusCode, expectedErrorMessage, test);	
				
		test.log(LogStatus.INFO, "Get report schedule data for the organization: "+organizationType);
		spogReportServer.deleteReportsWithCheck(validToken, report_ids, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
	}
	
	
	@DataProvider(name = "reportsDataInvalid")
	public Object[][] reportsDataInvalid(){
		return new Object[][] {
			//400
			{"Delete reports with invalid report_id with direct org user token",ti.direct_org1_user1_token,"invalidreportid",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_IS_NOT_UUID},
			{"Delete reports with invalid report_id with msp user token",ti.root_msp_org1_user1_token,"invalidreportid",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_IS_NOT_UUID},
			{"Delete reports with invalid report_id with sub org user token",ti.root_msp1_suborg1_user1_token,"invalidreportid",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_IS_NOT_UUID},
			{"Delete reports with invalid report_id with msp account admin token",ti.root_msp1_suborg1_user1_token,"invalidreportid",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_IS_NOT_UUID},
			{"Delete reports with invalid report_id with sub msp user token",ti.root_msp1_submsp1_user1_token,"invalidreportid",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_IS_NOT_UUID},
			{"Delete reports with invalid report_id with sub msp sub org user token",ti.msp1_submsp1_suborg1_user1_token,"invalidreportid",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_IS_NOT_UUID},
			{"Delete reports with invalid report_id with sub msp account admin token",ti.root_msp1_submsp1_account_admin_token,"invalidreportid",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_IS_NOT_UUID},
			
			//401
			{"Delete reports with invalid token","INVALID TOKEN",UUID.randomUUID().toString(),SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT},
			{"Delete reports with missing token","",UUID.randomUUID().toString(),SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTHENTICATION_FAILED},
			
			//404
			{"Delete reports with report_ids that does not exist with direct org user token",ti.direct_org1_user1_token,UUID.randomUUID().toString(),SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.UNABLE_TO_FIND_REPORT_WITH_ID},
			{"Delete reports with report_ids that does not exist with msp user token",ti.root_msp_org1_user1_token,UUID.randomUUID().toString(),SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.UNABLE_TO_FIND_REPORT_WITH_ID},
			{"Delete reports with report_ids that does not exist with sub org user token",ti.root_msp1_suborg1_user1_token,UUID.randomUUID().toString(),SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.UNABLE_TO_FIND_REPORT_WITH_ID},
			{"Delete reports with report_ids that does not exist with msp account admin user token",ti.root_msp_org1_msp_accountadmin1_token,UUID.randomUUID().toString(),SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.UNABLE_TO_FIND_REPORT_WITH_ID},
			{"Delete reports with report_ids that does not exist with sub msp user token",ti.root_msp1_submsp1_user1_token,UUID.randomUUID().toString(),SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.UNABLE_TO_FIND_REPORT_WITH_ID},
			{"Delete reports with report_ids that does not exist with sub msp sub org user token",ti.msp1_submsp1_suborg1_user1_token,UUID.randomUUID().toString(),SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.UNABLE_TO_FIND_REPORT_WITH_ID},
			{"Delete reports with report_ids that does not exist with sub msp account admin user token",ti.root_msp1_submsp1_account_admin_token,UUID.randomUUID().toString(),SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.UNABLE_TO_FIND_REPORT_WITH_ID},
		};
	}
	
	@Test(dataProvider="reportsDataInvalid",dependsOnMethods= {"postData"})
	public void deleteReportsInvalid(String caseType, String token, String report_id, int expectedStatusCode, SpogMessageCode expectedErrorMessage ) {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		ArrayList<String> report_ids = new ArrayList<>();
		report_ids.add(report_id);
		
		test.log(LogStatus.INFO, caseType);
		spogReportServer.deleteReportsWithCheck(token, report_ids, expectedStatusCode, expectedErrorMessage, test);
		
	}	
	
	@AfterMethod
	public void getResult(ITestResult result){
		if(result.getStatus() == ITestResult.FAILURE){
			count1.setfailedcount();
			//remaincases=Nooftest-passedcases-failedcases;
			test.log(LogStatus.FAIL, "Test Case Failed is "+result.getName()+" with parameters as "+Arrays.asList(result.getParameters()) );
			test.log(LogStatus.FAIL, result.getThrowable().getMessage());

		}else if(result.getStatus() == ITestResult.SKIP){
			test.log(LogStatus.SKIP, "Test Case Skipped is "+result.getName());
			count1.setskippedcount();
		}else if(result.getStatus()==ITestResult.SUCCESS){
			count1.setpassedcount();
			//remaincases=Nooftest-passedcases-failedcases;

		}
		// ending test
		//endTest(logger) : It ends the current test and prepares to create HTML report
		rep.endTest(test);
		//rep.flush();
	}
	/******************************************************************RandomFunction******************************************************************************/
	public int gen_random_index(String[] job_status) {
		Random generator = new Random();
		int randomindx = generator.nextInt(job_status.length);

		return randomindx;
	}
}
