package api.reports.reportschedules;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
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

public class DeleteReportsByScheduleIdTest extends base.prepare.Is4Org {
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

    /*private ExtentReports rep;
    private SQLServerDb bqdb1;
    public int Nooftest;
    private long creationTime;
    private String BQName=null;
    private String runningMachine;
    private testcasescount count1;
    private String buildVersion;*/
	
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
	
	String daily_cron = "0 0 19 ? * *"; //daily 7 PM
	String weekly_cron = "0 30 19 ? * 1"; //weekly 7.30 PM
	String monthly_cron = "0 0 20 13 * ?"; //monthly 8 PM, 13th of every month

	private String  org_model_prefix=this.getClass().getSimpleName();
	private TestOrgInfo ti;
	
	@BeforeClass
	@Parameters({ "baseURI", "port","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String logFolder,String runningMachine,String buildVersion) {
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

			// creationTime = System.currentTimeMillis();
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
	
	
	
	@DataProvider(name="postSource")
	public Object[][] postSource(){
		return new Object[][] {
			
			//dataprovider to create the sources in all organizations
			{"direct",ti.direct_org1_user1_token,"sourceName1", SourceType.machine, SourceProduct.cloud_direct, ti.direct_org1_id, ProtectionStatus.protect, ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange",  "Rak_vm2",  UUID.randomUUID().toString(), "Rak_agent1", "windows 2012", "64" ,"1.0.0","2.0", "http://upgrade",4},
			{"msp",ti.root_msp_org1_user1_token,"sourceName1", SourceType.machine, SourceProduct.cloud_direct, ti.root_msp_org1_id, ProtectionStatus.protect, ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange",  "Rak_vm2",  UUID.randomUUID().toString(), "Rak_agent1", "windows 2012", "64" ,"1.0.0","2.0", "http://upgrade",4},
			{"submsp",ti.root_msp1_submsp1_user1_token,"sourceName1", SourceType.machine, SourceProduct.cloud_direct, ti.root_msp1_submsp_org1_id, ProtectionStatus.protect, ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange",  "Rak_vm2",  UUID.randomUUID().toString(), "Rak_agent1", "windows 2012", "64" ,"1.0.0","2.0", "http://upgrade",4},
			{"suborg",ti.root_msp1_suborg1_user1_token,"sourceName1", SourceType.machine, SourceProduct.cloud_direct, ti.root_msp1_suborg1_id, ProtectionStatus.protect, ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange",  "Rak_vm2",  UUID.randomUUID().toString(), "Rak_agent1", "windows 2012", "64" ,"1.0.0","2.0", "http://upgrade",4},
			
		};
	}
	
	@Test(dataProvider= "postSource")
	public void postSources(String orgType, String token, String source_name, SourceType source_type, SourceProduct source_product,
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
				direct_source_group_id_list,ti.direct_org1_id,"sykam.naga@gmail.com",daily_cron,"all_sources",SpogConstants.SUCCESS_POST,null},
			{"direct",ti.direct_org1_user1_token,"report1","restore_jobs","last_7_days",System.currentTimeMillis(),System.currentTimeMillis()+10000,"weekly",
					direct_source_group_id_list,ti.direct_org1_id,"a@gmail.com,b@gmail.com",weekly_cron,"all_organizations", SpogConstants.SUCCESS_POST,null},
			{"direct",ti.direct_org1_user1_token,"report1","data_transfer","last_1_month",System.currentTimeMillis(),System.currentTimeMillis()+10000,"monthly",
						direct_source_group_id_list,ti.direct_org1_id,"a@gmail.com,b@gmail.com",monthly_cron,"selected_organizations",SpogConstants.SUCCESS_POST,null},
			{"direct",ti.direct_org1_user1_token,"report1","jobs_and_destinations","last_3_months",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
						direct_source_group_id_list,ti.direct_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"selected_source_groups",SpogConstants.SUCCESS_POST,null},
			{"direct",ti.direct_org1_user1_token,"report1","capacity_usage","last_6_months",System.currentTimeMillis(),System.currentTimeMillis()+10000,"weekly",
						direct_source_group_id_list,ti.direct_org1_id,"a@gmail.com,b@gmail.com",weekly_cron,"all_sources",SpogConstants.SUCCESS_POST,null},
			{"direct",ti.direct_org1_user1_token,"report1","capacity_usage","last_1_year",System.currentTimeMillis(),System.currentTimeMillis()+10000,"monthly",
						direct_source_group_id_list,ti.direct_org1_id,"a@gmail.com,b@gmail.com",monthly_cron,"all_organizations",SpogConstants.SUCCESS_POST,null},

			{"msp",ti.root_msp_org1_user1_token,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
						msp_source_group_id_list,ti.root_msp_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"all_sources",SpogConstants.SUCCESS_POST,null},
			{"msp",ti.root_msp_org1_user1_token,"report1","restore_jobs","last_7_days",System.currentTimeMillis(),System.currentTimeMillis()+10000,"weekly",
						msp_source_group_id_list,ti.root_msp_org1_id,"a@gmail.com,b@gmail.com",weekly_cron,"all_organizations",SpogConstants.SUCCESS_POST,null},
			{"msp",ti.root_msp_org1_user1_token,"report1","data_transfer","last_1_month",System.currentTimeMillis(),System.currentTimeMillis()+10000,"monthly",
						msp_source_group_id_list,ti.root_msp_org1_id,"a@gmail.com,b@gmail.com",monthly_cron,"selected_organizations",SpogConstants.SUCCESS_POST,null},
			{"msp",ti.root_msp_org1_user1_token,"report1","jobs_and_destinations","last_3_months",System.currentTimeMillis(),System.currentTimeMillis()+10000,"quarterly",
						msp_source_group_id_list,ti.root_msp_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"selected_source_groups",SpogConstants.SUCCESS_POST,null},
			{"msp",ti.root_msp_org1_user1_token,"report1","capacity_usage","last_6_months",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
						msp_source_group_id_list,ti.root_msp_org1_id,"a@gmail.com,b@gmail.com",weekly_cron,"all_sources",SpogConstants.SUCCESS_POST,null},
			{"msp",ti.root_msp_org1_user1_token,"report1","capacity_usage","last_1_year",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
						msp_source_group_id_list,ti.root_msp_org1_id,"a@gmail.com,b@gmail.com",monthly_cron,"all_organizations",SpogConstants.SUCCESS_POST,null},

			{"submsp",ti.root_msp1_submsp1_user1_token,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
						submsp_source_group_id_list,ti.root_msp1_submsp_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"all_sources",SpogConstants.SUCCESS_POST,null},
			{"submsp",ti.root_msp1_submsp1_user1_token,"report1","restore_jobs","last_7_days",System.currentTimeMillis(),System.currentTimeMillis()+10000,"weekly",
						submsp_source_group_id_list,ti.root_msp1_submsp_org1_id,"a@gmail.com,b@gmail.com",weekly_cron,"all_organizations",SpogConstants.SUCCESS_POST,null},
			{"submsp",ti.root_msp1_submsp1_user1_token,"report1","data_transfer","last_1_month",System.currentTimeMillis(),System.currentTimeMillis()+10000,"monthly",
						submsp_source_group_id_list,ti.root_msp1_submsp_org1_id,"a@gmail.com,b@gmail.com",monthly_cron,"selected_organizations",SpogConstants.SUCCESS_POST,null},
			{"submsp",ti.root_msp1_submsp1_user1_token,"report1","jobs_and_destinations","last_3_months",System.currentTimeMillis(),System.currentTimeMillis()+10000,"quarterly",
						submsp_source_group_id_list,ti.root_msp1_submsp_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"selected_source_groups",SpogConstants.SUCCESS_POST,null},
			{"submsp",ti.root_msp1_submsp1_user1_token,"report1","capacity_usage","last_6_months",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
						submsp_source_group_id_list,ti.root_msp1_submsp_org1_id,"a@gmail.com,b@gmail.com",weekly_cron,"all_sources",SpogConstants.SUCCESS_POST,null},
			{"submsp",ti.root_msp1_submsp1_user1_token,"report1","capacity_usage","last_1_year",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
						submsp_source_group_id_list,ti.root_msp1_submsp_org1_id,"a@gmail.com,b@gmail.com",monthly_cron,"all_organizations",SpogConstants.SUCCESS_POST,null},

			
			{"suborg",ti.root_msp1_suborg1_user1_token,"report12","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
						suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",daily_cron,"all_sources",SpogConstants.SUCCESS_POST,null},
			{"suborg",ti.root_msp1_suborg1_user1_token,spogServer.ReturnRandom("report12"),"restore_jobs","last_7_days",System.currentTimeMillis(),System.currentTimeMillis()+10000,"weekly",
						suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",weekly_cron,"all_organizations",SpogConstants.SUCCESS_POST,null},
			{"suborg",ti.root_msp1_suborg1_user1_token,"report12","data_transfer","last_1_month",System.currentTimeMillis(),System.currentTimeMillis()+10000,"monthly",
						suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",monthly_cron,"selected_organizations",SpogConstants.SUCCESS_POST,null},
			{"suborg",ti.root_msp1_suborg1_user1_token,"report12","jobs_and_destinations","last_3_months",System.currentTimeMillis(),System.currentTimeMillis()+10000,"quarterly",
						suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",daily_cron,"selected_source_groups",SpogConstants.SUCCESS_POST,null},
			{"suborg",ti.root_msp1_suborg1_user1_token,"report12","capacity_usage","last_6_months",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
						suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",weekly_cron,"all_sources",SpogConstants.SUCCESS_POST,null},
			{"suborg",ti.root_msp1_suborg1_user1_token,"report12","capacity_usage","last_1_year",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
						suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",monthly_cron,"all_organizations",SpogConstants.SUCCESS_POST,null},

			//With msp_account_admin token
			{"suborg-mspAccAdminToken",ti.root_msp_org1_msp_accountadmin1_token,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
						suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",daily_cron,"all_sources",SpogConstants.SUCCESS_POST,null},
			{"suborg-mspAccAdminToken",ti.root_msp_org1_msp_accountadmin1_token,"report1","restore_jobs","last_7_days",System.currentTimeMillis(),System.currentTimeMillis()+10000,"weekly",
						suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",weekly_cron,"all_organizations",SpogConstants.SUCCESS_POST,null},
			{"suborg-mspAccAdminToken",ti.root_msp_org1_msp_accountadmin1_token,"report1","data_transfer","last_1_month",System.currentTimeMillis(),System.currentTimeMillis()+10000,"monthly",
						suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",monthly_cron,"selected_organizations",SpogConstants.SUCCESS_POST,null},
			{"suborg-mspAccAdminToken",ti.root_msp_org1_msp_accountadmin1_token,"report1","jobs_and_destinations","last_3_months",System.currentTimeMillis(),System.currentTimeMillis()+10000,"quarterly",
						suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",daily_cron,"selected_source_groups",SpogConstants.SUCCESS_POST,null},
			{"suborg-mspAccAdminToken",ti.root_msp_org1_msp_accountadmin1_token,"report1","capacity_usage","last_6_months",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
						suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",weekly_cron,"all_sources",SpogConstants.SUCCESS_POST,null},
			{"suborg-mspAccAdminToken",ti.root_msp_org1_msp_accountadmin1_token,"report1","capacity_usage","last_1_year",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
						suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",monthly_cron,"all_organizations",SpogConstants.SUCCESS_POST,null},

		};
	}	
	@Test(dataProvider="reportScheduleData",dependsOnMethods= {"postSources"},enabled=true)
	public void deleteReportsByScheduleIdValid(String organizationType, String token, String report_name, String report_type, String date_range_type, long date_range_start_ts,
												long date_range_end_ts, String schedule_frequency, String source_group_id_list, String organization_id_list, String recipient_mail,
												String cron_expression, String report_for_type, int expectedStatusCode, SpogMessageCode expectedErrorMessage
												) {
		HashMap<String, Object> scheduleInfo = new HashMap<>();
		String schedule_id ;
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		
		test.log(LogStatus.INFO, "Compose report schedule info");
		scheduleInfo = spogReportServer.composeReportScheduleInfo(report_name, report_type, date_range_type, date_range_start_ts, date_range_end_ts,schedule_frequency, source_group_id_list, organization_id_list, recipient_mail, cron_expression, report_for_type);
		
		test.log(LogStatus.INFO, "Create report schedule with check for the organization: "+organizationType);
		schedule_id = spogReportServer.createReportScheduleWithCheck(token, scheduleInfo, expectedStatusCode, expectedErrorMessage, test);
		
		test.log(LogStatus.INFO, "Delete reports by schedule id: "+schedule_id);
		spogReportServer.deleteReportsByScheduleIdWithCheck(schedule_id, token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		
		test.log(LogStatus.INFO, "Delete reports by schedule id: "+schedule_id+" using csr_readonly token");
		spogReportServer.deleteReportsByScheduleIdWithCheck(schedule_id, ti.csr_readonly_token, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY, test);
					
		//Deleting the schedule as above api only deletes the created reports under the schedule
		test.log(LogStatus.INFO, "Delete the report schedule with id: "+schedule_id);
		spogReportServer.deleteReportScheduleByIdWithCheck(schedule_id, token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
	}
 
	@DataProvider(name = "reportScheduleDataInvalid")
	public Object[][] reportScheduleDataInvalid(){
		return new Object[][] {
			
			//400 cases
			{"Delete reports by schedule id with invalid schedule id for direct org",ti.direct_org1_user1_token,"INVALIDID",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_IS_NOT_UUID},
			{"Delete reports by schedule id with invalid schedule id for msp org",ti.root_msp_org1_user1_token,"INVALIDID",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_IS_NOT_UUID},
			{"Delete reports by schedule id with invalid schedule id for sub org",ti.root_msp1_suborg1_user1_token,"INVALIDID",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_IS_NOT_UUID},
			{"Delete reports by schedule id with invalid schedule id for sub org with msp_account_admin token",ti.root_msp_org1_msp_accountadmin1_token,"INVALIDID",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_IS_NOT_UUID},
			{"Delete reports by schedule id with invalid schedule id for sub msp org",ti.root_msp1_submsp1_user1_token,"INVALIDID",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_IS_NOT_UUID},
			{"Delete reports by schedule id with invalid schedule id for sub msp sub org",ti.msp1_submsp1_suborg1_user1_token,"INVALIDID",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_IS_NOT_UUID},
			{"Delete reports by schedule id with invalid schedule id for sub org with sub msp_account_admin token",ti.root_msp1_submsp1_account_admin_token,"INVALIDID",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_IS_NOT_UUID},
			
			//401 cases
			{"Delete reports by schedule id with invalid token","INVALID",UUID.randomUUID().toString(),SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT},
			{"Delete reports by schedule id with missing token","",UUID.randomUUID().toString(),SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTHENTICATION_FAILED},
			
			//404 cases
			{"Delete reports by schedule id with schedule id that does not exist for direct org",ti.direct_org1_user1_token,UUID.randomUUID().toString(),SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.NOT_FOUND_SCHEDULE_WITH_ID},
			{"Delete reports by schedule id with schedule id that does not exist for msp org",ti.root_msp_org1_user1_token,UUID.randomUUID().toString(),SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.NOT_FOUND_SCHEDULE_WITH_ID},
			{"Delete reports by schedule id with schedule id that does not exist for sub org",ti.root_msp1_suborg1_user1_token,UUID.randomUUID().toString(),SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.NOT_FOUND_SCHEDULE_WITH_ID},
			{"Delete reports by schedule id with schedule id that does not exist for sub org with msp_account_admin token",ti.root_msp_org1_msp_accountadmin1_token,UUID.randomUUID().toString(),SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.NOT_FOUND_SCHEDULE_WITH_ID},
			{"Delete reports by schedule id with schedule id that does not exist for sub msp org",ti.root_msp1_submsp1_user1_token,UUID.randomUUID().toString(),SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.NOT_FOUND_SCHEDULE_WITH_ID},
			{"Delete reports by schedule id with schedule id that does not exist for sub msp sub org",ti.msp1_submsp1_suborg1_user1_token,UUID.randomUUID().toString(),SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.NOT_FOUND_SCHEDULE_WITH_ID},
			{"Delete reports by schedule id with schedule id that does not exist for sub org with sub msp_account_admin token",ti.root_msp1_submsp1_account_admin_token,UUID.randomUUID().toString(),SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.NOT_FOUND_SCHEDULE_WITH_ID},
		};
	}
	
	@Test(dataProvider="reportScheduleDataInvalid")
	public void deleteReportsByScheduleIdInvalid(String caseType, String token, String schedule_id, int expectedStatusCode, SpogMessageCode expectedErrorMessage ) {

		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());

		test.log(LogStatus.INFO, caseType);
		spogReportServer.deleteReportsByScheduleIdWithCheck(schedule_id, token, expectedStatusCode, expectedErrorMessage, test);		
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
