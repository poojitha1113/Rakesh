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

public class UpdateReportScheduleTest extends base.prepare.Is4Org {
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
    private String buildVersion;*/
		
	String direct_source_group_id_list;
	String msp_source_group_id_list;
	String submsp_source_group_id_list;
	String suborg_source_group_id_list;
	
	String direct_source_id;
	String msp_source_id;
	String suborg_source_id;	
	
	String direct_schedule_id;
	String msp_schedule_id;
	String submsp_schedule_id;
	String suborg_schedule_id;
	String mspAccAdmin_schedule_id;

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
				direct_source_group_id_list,ti.direct_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"all_sources",SpogConstants.SUCCESS_POST,null},
			
			{"msp",ti.root_msp_org1_user1_token,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"weekly",
					msp_source_group_id_list,ti.root_msp_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"all_sources",SpogConstants.SUCCESS_POST,null},
			{"submsp",ti.root_msp1_submsp1_user1_token,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"weekly",
						submsp_source_group_id_list,ti.root_msp1_submsp_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"all_sources",SpogConstants.SUCCESS_POST,null},
				
			{"suborg",ti.root_msp1_suborg1_user1_token,"report12","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"monthly",
							suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",daily_cron,"all_sources",SpogConstants.SUCCESS_POST,null},
			
			//With msp_account_admin token
			{"suborg-mspAccAdminToken",ti.root_msp_org1_msp_accountadmin1_token,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"quarterly",
								suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",daily_cron,"all_sources",SpogConstants.SUCCESS_POST,null},
			
			
		};
	}
	
	@Test(dataProvider="reportScheduleData",dependsOnMethods= {"postSources"})
	public void createReportSchedulesValid(String organizationType, String token, String report_name, String report_type, String date_range_type, long date_range_start_ts,
												long date_range_end_ts,String schedule_frequency, String source_group_id_list, String organization_id_list, String recipient_mail,
												String cron_expression,String report_for_type, int expectedStatusCode, SpogMessageCode expectedErrorMessage
												) {
		HashMap<String, Object> scheduleInfo = new HashMap<>();
		String schedule_id ;
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		
		test.log(LogStatus.INFO, "Compose report schedule info");
		scheduleInfo = spogReportServer.composeReportScheduleInfo(report_name, report_type, date_range_type, date_range_start_ts, date_range_end_ts,schedule_frequency, source_group_id_list, organization_id_list, recipient_mail, cron_expression,report_for_type);
		
		test.log(LogStatus.INFO, "Create report schedule with check for the organization: "+organizationType);
		schedule_id = spogReportServer.createReportScheduleWithCheck(token, scheduleInfo, expectedStatusCode, expectedErrorMessage, test);
		
		if (organizationType.equals("direct")) {
			direct_schedule_id = schedule_id;			
		}else if (organizationType.equalsIgnoreCase("submsp")) {
			submsp_schedule_id = schedule_id;
		} else if(organizationType.equals("msp")) {
			msp_schedule_id = schedule_id;
		}else if (organizationType.equals("suborg")) {
			suborg_schedule_id = schedule_id;
		}else if(organizationType.equals("suborg-mspAccAdminToken")) {
			mspAccAdmin_schedule_id = schedule_id;
		}
				
	}
	
	@DataProvider(name="updateReportSchedule")
	public Object[][] updateReportSchedule(){
		return new Object[][] {
		{"direct",ti.direct_org1_user1_token,direct_schedule_id,"report1","restore_jobs","last_7_days",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
				direct_source_group_id_list,ti.direct_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"all_organizations",SpogConstants.SUCCESS_GET_PUT_DELETE,null},
		{"direct",ti.direct_org1_user1_token,direct_schedule_id,"report1","data_transfer","last_1_month",System.currentTimeMillis(),System.currentTimeMillis()+10000,"weekly",
				direct_source_group_id_list,ti.direct_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"selected_organizations",SpogConstants.SUCCESS_GET_PUT_DELETE,null},
		{"direct",ti.direct_org1_user1_token,direct_schedule_id,"report1","jobs_and_destinations","last_3_months",System.currentTimeMillis(),System.currentTimeMillis()+10000,"monthly",
				direct_source_group_id_list,ti.direct_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"selected_source_groups",SpogConstants.SUCCESS_GET_PUT_DELETE,null},
		{"direct",ti.direct_org1_user1_token,direct_schedule_id,spogServer.ReturnRandom("report1"),"capacity_usage","last_6_months",System.currentTimeMillis(),System.currentTimeMillis()+10000,"quarterly",
				direct_source_group_id_list,ti.direct_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"all_organizations",SpogConstants.SUCCESS_GET_PUT_DELETE,null},
		{"direct",ti.direct_org1_user1_token,direct_schedule_id,"report1","capacity_usage","last_1_year",System.currentTimeMillis(),System.currentTimeMillis()+10000,"monthly",
				direct_source_group_id_list,ti.direct_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"selected_organizations",SpogConstants.SUCCESS_GET_PUT_DELETE,null},
		
		{"msp",ti.root_msp_org1_user1_token,msp_schedule_id,"report1","restore_jobs","last_7_days",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
				msp_source_group_id_list,ti.root_msp_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"all_organizations",SpogConstants.SUCCESS_GET_PUT_DELETE,null},
		{"msp",ti.root_msp_org1_user1_token,msp_schedule_id,"report1","data_transfer","last_1_month",System.currentTimeMillis(),System.currentTimeMillis()+10000,"weekly",
				msp_source_group_id_list,ti.root_msp_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"selected_organizations",SpogConstants.SUCCESS_GET_PUT_DELETE,null},
		{"msp",ti.root_msp_org1_user1_token,msp_schedule_id,spogServer.ReturnRandom("report1"),"jobs_and_destinations","last_3_months",System.currentTimeMillis(),System.currentTimeMillis()+10000,"monthly",
				msp_source_group_id_list,ti.root_msp_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"selected_source_groups",SpogConstants.SUCCESS_GET_PUT_DELETE,null},
		{"msp",ti.root_msp_org1_user1_token,msp_schedule_id,"report1","capacity_usage","last_6_months",System.currentTimeMillis(),System.currentTimeMillis()+10000,"monthly",
				msp_source_group_id_list,ti.root_msp_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"all_organizations",SpogConstants.SUCCESS_GET_PUT_DELETE,null},
		{"msp",ti.root_msp_org1_user1_token,msp_schedule_id,"report1","capacity_usage","last_1_year",System.currentTimeMillis(),System.currentTimeMillis()+10000,"quarterly",
				msp_source_group_id_list,ti.root_msp_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"selected_organizations",SpogConstants.SUCCESS_GET_PUT_DELETE,null},
		
		{"msp",ti.root_msp1_submsp1_user1_token,submsp_schedule_id,"report1","restore_jobs","last_7_days",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
				submsp_source_group_id_list,ti.root_msp1_submsp_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"all_organizations",SpogConstants.SUCCESS_GET_PUT_DELETE,null},
		{"msp",ti.root_msp1_submsp1_user1_token,submsp_schedule_id,"report1","data_transfer","last_1_month",System.currentTimeMillis(),System.currentTimeMillis()+10000,"weekly",
				submsp_source_group_id_list,ti.root_msp1_submsp_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"selected_organizations",SpogConstants.SUCCESS_GET_PUT_DELETE,null},
		{"msp",ti.root_msp1_submsp1_user1_token,submsp_schedule_id,spogServer.ReturnRandom("report1"),"jobs_and_destinations","last_3_months",System.currentTimeMillis(),System.currentTimeMillis()+10000,"monthly",
				submsp_source_group_id_list,ti.root_msp1_submsp_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"selected_source_groups",SpogConstants.SUCCESS_GET_PUT_DELETE,null},
		{"msp",ti.root_msp1_submsp1_user1_token,submsp_schedule_id,"report1","capacity_usage","last_6_months",System.currentTimeMillis(),System.currentTimeMillis()+10000,"monthly",
				submsp_source_group_id_list,ti.root_msp1_submsp_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"all_organizations",SpogConstants.SUCCESS_GET_PUT_DELETE,null},
		{"msp",ti.root_msp1_submsp1_user1_token,submsp_schedule_id,"report1","capacity_usage","last_1_year",System.currentTimeMillis(),System.currentTimeMillis()+10000,"quarterly",
				submsp_source_group_id_list,ti.root_msp1_submsp_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"selected_organizations",SpogConstants.SUCCESS_GET_PUT_DELETE,null},
							
		
		{"suborg",ti.root_msp1_suborg1_user1_token,suborg_schedule_id,spogServer.ReturnRandom("report12"),"restore_jobs","last_7_days",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
				suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",daily_cron,"all_organizations",SpogConstants.SUCCESS_GET_PUT_DELETE,null},
		{"suborg",ti.root_msp1_suborg1_user1_token,suborg_schedule_id,"report12","data_transfer","last_1_month",System.currentTimeMillis(),System.currentTimeMillis()+10000,"weekly",
				suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",daily_cron,"selected_organizations",SpogConstants.SUCCESS_GET_PUT_DELETE,null},
		{"suborg",ti.root_msp1_suborg1_user1_token,suborg_schedule_id,"report12","jobs_and_destinations","last_3_months",System.currentTimeMillis(),System.currentTimeMillis()+10000,"quarterly",
				suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",daily_cron,"selected_source_groups",SpogConstants.SUCCESS_GET_PUT_DELETE,null},
		{"suborg",ti.root_msp1_suborg1_user1_token,suborg_schedule_id,"report12","capacity_usage","last_6_months",System.currentTimeMillis(),System.currentTimeMillis()+10000,"monthly",
				suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",daily_cron,"all_organizations",SpogConstants.SUCCESS_GET_PUT_DELETE,null},
		{"suborg",ti.root_msp1_suborg1_user1_token,suborg_schedule_id,"report12","capacity_usage","last_1_year",System.currentTimeMillis(),System.currentTimeMillis()+10000,"quarterly",
				suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",daily_cron,"selected_organizations",SpogConstants.SUCCESS_GET_PUT_DELETE,null},
		
		//With msp_account_admin token for a sub organization
		{"suborg-mspAccAdminToken",ti.root_msp_org1_msp_accountadmin1_token,mspAccAdmin_schedule_id,"report1","restore_jobs","last_7_days",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
				suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",daily_cron,"all_organizations",SpogConstants.SUCCESS_GET_PUT_DELETE,null},
		{"suborg-mspAccAdminToken",ti.root_msp_org1_msp_accountadmin1_token,mspAccAdmin_schedule_id,"report1","data_transfer","last_1_month",System.currentTimeMillis(),System.currentTimeMillis()+10000,"weekly",
				suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",daily_cron,"selected_organizations",SpogConstants.SUCCESS_GET_PUT_DELETE,null},
		{"suborg-mspAccAdminToken",ti.root_msp_org1_msp_accountadmin1_token,mspAccAdmin_schedule_id,"report1","jobs_and_destinations","last_3_months",System.currentTimeMillis(),System.currentTimeMillis()+10000,"monthly",
				suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",daily_cron,"selected_source_groups",SpogConstants.SUCCESS_GET_PUT_DELETE,null},
		{"suborg-mspAccAdminToken",ti.root_msp_org1_msp_accountadmin1_token,mspAccAdmin_schedule_id,"report1","capacity_usage","last_6_months",System.currentTimeMillis(),System.currentTimeMillis()+10000,"quarterly",
				suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",daily_cron,"all_organizations",SpogConstants.SUCCESS_GET_PUT_DELETE,null},
		{"suborg-mspAccAdminToken",ti.root_msp_org1_msp_accountadmin1_token,mspAccAdmin_schedule_id,"report1","capacity_usage","last_1_year",System.currentTimeMillis(),System.currentTimeMillis()+10000,"monthly",
																							suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",daily_cron,"selected_organizations",SpogConstants.SUCCESS_GET_PUT_DELETE,null},
		};
	}
	
	@Test(dataProvider="updateReportSchedule",dependsOnMethods= {"postSources","createReportSchedulesValid"},enabled=true)
	public void updateReportScheduleByIdValid(String organizationType, String token, String schedule_id, String report_name, String report_type, String date_range_type, 
												long date_range_start_ts,long date_range_end_ts,String schedule_frequency, String source_group_id_list, String organization_id_list, String recipient_mail,
												String cron_expression,String report_for_type, int expectedStatusCode, SpogMessageCode expectedErrorMessage) {
		
		HashMap<String, Object> scheduleInfo = new HashMap<>();
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		
		test.log(LogStatus.INFO, "Compose report schedule info");
		scheduleInfo = spogReportServer.composeReportScheduleInfo(report_name, report_type, date_range_type, date_range_start_ts, date_range_end_ts, schedule_frequency, source_group_id_list, organization_id_list, recipient_mail, cron_expression,report_for_type);
		
		test.log(LogStatus.INFO, "Create report schedule with check for the organization: "+organizationType);
		spogReportServer.updateReportScheduleByIdWithCheck(schedule_id, token, scheduleInfo, expectedStatusCode, expectedErrorMessage, test);
		
	}
	
	@DataProvider(name = "reportScheduleDataInvalid")
	public Object[][] reportScheduleDataInvalid(){
		return new Object[][] {
			
			//400 cases with invalid input parameters for all organizations
			{"Update report schedule in direct org with invalid report_type",ti.direct_org1_user1_token,direct_schedule_id,"report1","backup_jobs"+12,"today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
				direct_source_group_id_list,ti.direct_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"all_sources",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.INVALID_PARAMETER},
			
			{"Update report schedule in direct org with invalid date_range_type",ti.direct_org1_user1_token,direct_schedule_id,"report1","backup_jobs","today"+12,System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
					direct_source_group_id_list,ti.direct_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"all_sources",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.INVALID_PARAMETER},
			
			{"Update report schedule in direct org with invalid source_group_id_list",ti.direct_org1_user1_token,direct_schedule_id,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
						direct_source_group_id_list+131321,ti.direct_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"selected_source_groups",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_IS_NOT_UUID},
			
			{"Update report schedule in direct org with invalid organization_id_list",ti.direct_org1_user1_token,direct_schedule_id,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
							direct_source_group_id_list,ti.direct_org1_id+"456","a@gmail.com,b@gmail.com",daily_cron,"selected_organizations",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.INVALID_PARAMETER},
			
			{"Update report schedule in direct org with invalid recipient_mail format",ti.direct_org1_user1_token,direct_schedule_id,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
								direct_source_group_id_list,ti.direct_org1_id,"agmail.com,b@gmail.com",daily_cron,"all_sources",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.EMAIL_FORMAT_NOT_CORRECT},
			
			{"Update report schedule in direct org with invalid cron_expression",ti.direct_org1_user1_token,direct_schedule_id,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
									direct_source_group_id_list,ti.direct_org1_id,"a@gmail.com,b@gmail.com","Invalidcron_express","all_sources",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.BAD_REQUEST},
			
			{"Update report schedule in direct org with invalid report_for",ti.direct_org1_user1_token,direct_schedule_id,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
										direct_source_group_id_list,ti.direct_org1_id,"a@gmail.com,b@gmail.com",daily_cron,null,SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.CAN_NOT_BE_BLANK},			
			
			
			
			{"Update report schedule in msp org with invalid report_type",ti.root_msp_org1_user1_token,msp_schedule_id,"report1","backup_jobs"+12,"today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
					msp_source_group_id_list,ti.root_msp_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"all_sources",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.INVALID_PARAMETER},
			
			{"Update report schedule in msp org with invalid date_range_type",ti.root_msp_org1_user1_token,msp_schedule_id,"report1","backup_jobs","today"+12,System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
					msp_source_group_id_list,ti.root_msp_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"all_sources",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.INVALID_PARAMETER},
			
			{"Update report schedule in msp org with invalid source_group_id_list",ti.root_msp_org1_user1_token,msp_schedule_id,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
					msp_source_group_id_list+131321,ti.root_msp_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"selected_source_groups",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_IS_NOT_UUID},
			
			{"Update report schedule in msp org with invalid organization_id_list",ti.root_msp_org1_user1_token,msp_schedule_id,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
					msp_source_group_id_list,ti.root_msp_org1_id+"456","a@gmail.com,b@gmail.com",daily_cron,"selected_organizations",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.INVALID_PARAMETER},
			
			{"Update report schedule in msp org with invalid recipient_mail format",ti.root_msp_org1_user1_token,msp_schedule_id,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
					msp_source_group_id_list,ti.root_msp_org1_id,"a@gmail.com,bmailcom",daily_cron,"all_sources",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.EMAIL_FORMAT_NOT_CORRECT},
			
			{"Update report schedule in msp org with invalid cron_expression",ti.root_msp_org1_user1_token,msp_schedule_id,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
					msp_source_group_id_list,ti.root_msp_org1_id,"a@gmail.com,b@gmail.com","cron_express","all_sources",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.BAD_REQUEST},
			
			{"Update report schedule in msp org with invalid report_for",ti.root_msp_org1_user1_token,msp_schedule_id,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
					msp_source_group_id_list,ti.root_msp_org1_id,"a@gmail.com,b@gmail.com",daily_cron,null,SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.CAN_NOT_BE_BLANK},
			
			{"Update report schedule in msp org with invalid report_type",ti.root_msp1_submsp1_user1_token,submsp_schedule_id,"report1","backup_jobs"+12,"today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
					submsp_source_group_id_list,ti.root_msp1_submsp_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"all_sources",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.INVALID_PARAMETER},
										
			{"Update report schedule in msp org with invalid date_range_type",ti.root_msp1_submsp1_user1_token,submsp_schedule_id,"report1","backup_jobs","today"+12,System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
					submsp_source_group_id_list,ti.root_msp1_submsp_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"all_sources",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.INVALID_PARAMETER},
										
			{"Update report schedule in msp org with invalid source_group_id_list",ti.root_msp1_submsp1_user1_token,submsp_schedule_id,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
					submsp_source_group_id_list+131321,ti.root_msp1_submsp_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"selected_source_groups",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_IS_NOT_UUID},
										
			{"Update report schedule in msp org with invalid organization_id_list",ti.root_msp1_submsp1_user1_token,submsp_schedule_id,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
					submsp_source_group_id_list,ti.root_msp1_submsp_org1_id+"456","a@gmail.com,b@gmail.com",daily_cron,"selected_organizations",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.INVALID_PARAMETER},
										
			{"Update report schedule in msp org with invalid recipient_mail format",ti.root_msp1_submsp1_user1_token,submsp_schedule_id,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
					submsp_source_group_id_list,ti.root_msp1_submsp_org1_id,"a@gmail.com,bmailcom",daily_cron,"all_sources",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.EMAIL_FORMAT_NOT_CORRECT},
										
			{"Update report schedule in msp org with invalid cron_expression",ti.root_msp1_submsp1_user1_token,submsp_schedule_id,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
					submsp_source_group_id_list,ti.root_msp1_submsp_org1_id,"a@gmail.com,b@gmail.com","cron_express","all_sources",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.BAD_REQUEST},
										
			{"Update report schedule in msp org with invalid report_for",ti.root_msp1_submsp1_user1_token,submsp_schedule_id,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
					submsp_source_group_id_list,ti.root_msp1_submsp_org1_id,"a@gmail.com,b@gmail.com",daily_cron,null,SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.CAN_NOT_BE_BLANK},

			
			{"Update report schedule in suborg with invalid report_type",ti.root_msp1_suborg1_user1_token,suborg_schedule_id,"report1","backup_jobs"+12,"today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
							suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",daily_cron,"all_sources",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.INVALID_PARAMETER},
			
			{"Update report schedule in suborg with invalid date_range_type",ti.root_msp1_suborg1_user1_token,suborg_schedule_id,"report1","backup_jobs","today"+12,System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
								suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",daily_cron,"all_sources",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.INVALID_PARAMETER},
			
			{"Update report schedule in suborg with invalid source_group_id_list",ti.root_msp1_suborg1_user1_token,suborg_schedule_id,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
									suborg_source_group_id_list+131321,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",daily_cron,"selected_source_groups",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_IS_NOT_UUID},
			
			{"Update report schedule in suborg with invalid organization_id_list",ti.root_msp1_suborg1_user1_token,suborg_schedule_id,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
										suborg_source_group_id_list,ti.root_msp1_suborg1_id+131321,"a@gmail.com,b@gmail.com",daily_cron,"selected_organizations",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_IS_NOT_UUID},
			
			{"Update report schedule in suborg with invalid recipient_mail format",ti.root_msp1_suborg1_user1_token,suborg_schedule_id,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
											suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,bil.com",daily_cron,"all_sources",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.EMAIL_FORMAT_NOT_CORRECT},
			
			{"Update report schedule in suborg with invalid cron_expression",ti.root_msp1_suborg1_user1_token,suborg_schedule_id,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
												suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com","cron_express","all_sources",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.BAD_REQUEST},
			
			{"Update report schedule in suborg with invalid report_for",ti.root_msp1_suborg1_user1_token,suborg_schedule_id,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
													suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",daily_cron,null,SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.CAN_NOT_BE_BLANK},
			
			
			{"Update report schedule in suborg with invalid report_type and msp_account_admin token",ti.root_msp_org1_msp_accountadmin1_token,suborg_schedule_id,"report1","backup_jobs"+12,"today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
													suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",daily_cron,"all_sources",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.INVALID_PARAMETER},

			{"Update report schedule in suborg with invalid date_range_type and msp_account_admin token",ti.root_msp_org1_msp_accountadmin1_token,suborg_schedule_id,"report1","backup_jobs","today"+12,System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
														suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",daily_cron,"all_sources",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.INVALID_PARAMETER},

			{"Update report schedule in suborg with invalid source_group_id_list and msp_account_admin token",ti.root_msp_org1_msp_accountadmin1_token,suborg_schedule_id,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
															suborg_source_group_id_list+131321,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",daily_cron,"selected_source_groups",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_IS_NOT_UUID},

			{"Update report schedule in suborg with invalid organization_id_list and msp_account_admin token",ti.root_msp_org1_msp_accountadmin1_token,suborg_schedule_id,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
																suborg_source_group_id_list,ti.root_msp1_suborg1_id+131321,"a@gmail.com,b@gmail.com",daily_cron,"selected_organizations",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_IS_NOT_UUID},

			{"Update report schedule in suborg with invalid recipient_mail format and msp_account_admin token",ti.root_msp_org1_msp_accountadmin1_token,suborg_schedule_id,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
																	suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,bil.com",daily_cron,"all_sources",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.EMAIL_FORMAT_NOT_CORRECT},

			{"Update report schedule in suborg with invalid cron_expression and msp_account_admin token",ti.root_msp_org1_msp_accountadmin1_token,suborg_schedule_id,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
																		suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com","cron_express","all_sources",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.BAD_REQUEST},
			
			{"Update report schedule in suborg with invalid report_for and msp_account_admin token",ti.root_msp_org1_msp_accountadmin1_token,suborg_schedule_id,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
																			suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",daily_cron,null,SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.CAN_NOT_BE_BLANK},
			
			
			//400 with invalid schedule_id
			{"Update report schedule in direct org with invalid schedule_id",ti.direct_org1_user1_token,"invalid","report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
																			direct_source_group_id_list,ti.direct_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"all_sources",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_IS_NOT_UUID},
			{"Update report schedule in msp org with invalid schedule_id",ti.root_msp_org1_user1_token,"invalid","report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
																				msp_source_group_id_list,ti.root_msp_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"all_sources",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_IS_NOT_UUID},
			{"Update report schedule in suborg with invalid schedule_id",ti.root_msp1_suborg1_user1_token,"invlaid","report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
																					suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",daily_cron,"all_sources",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_IS_NOT_UUID},
			{"Update report schedule in suborg with invalid schedule_id and msp_account_admin token",ti.root_msp_org1_msp_accountadmin1_token,"invalid","report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
																						suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",daily_cron,"all_sources",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_IS_NOT_UUID},
			
			//401 casesl
			{"Update report schedule with invalid token","INVALIDTOKEN",UUID.randomUUID().toString(),"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
																			suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",daily_cron,"all_sources",SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT},
			
			{"Update report schedule with missing token","",UUID.randomUUID().toString(),"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
																				suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",daily_cron,"all_sources",SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTHENTICATION_FAILED},
			
			//403 cases
			{"Update report schedule in suborg with direct user token token",ti.direct_org1_user1_token,suborg_schedule_id,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
						suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",daily_cron,"selected_organizations",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Update report schedule in msp with direct user token token",ti.direct_org1_user1_token,suborg_schedule_id,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
					suborg_source_group_id_list,ti.root_msp_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"selected_organizations",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			
			{"Update report schedule in direct with msp user token",ti.root_msp_org1_user1_token,direct_schedule_id,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
					direct_source_group_id_list,ti.direct_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"selected_organizations",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Update report schedule in direct with suborg user token",ti.root_msp1_suborg1_user1_token,direct_schedule_id,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
					direct_source_group_id_list,ti.direct_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"selected_organizations",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			
			{"Update report schedule in msp org with direct token",ti.direct_org1_user1_token,msp_schedule_id,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
					msp_source_group_id_list,ti.root_msp_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"selected_organizations",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Update report schedule in msp org with suborg token",ti.root_msp1_suborg1_user1_token,msp_schedule_id,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
						msp_source_group_id_list,ti.root_msp_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"selected_organizations",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Update report schedule in msp org with msp 2 token",ti.root_msp_org2_user1_token,msp_schedule_id,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
						msp_source_group_id_list,ti.root_msp_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"selected_organizations",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Update report schedule in msp org with submsp token",ti.root_msp1_submsp1_user1_token,msp_schedule_id,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
						msp_source_group_id_list,ti.root_msp_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"selected_organizations",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Update report schedule in msp org with submsp suborg token",ti.msp1_submsp1_suborg1_user1_token,msp_schedule_id,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
						msp_source_group_id_list,ti.root_msp_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"selected_organizations",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			
			{"Update report schedule in sub msp org with direct token",ti.direct_org1_user1_token,submsp_schedule_id,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
						submsp_source_group_id_list,ti.root_msp1_submsp_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"selected_organizations",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Update report schedule in sub msp org with suborg token",ti.root_msp1_suborg1_user1_token,submsp_schedule_id,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
						submsp_source_group_id_list,ti.root_msp1_submsp_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"selected_organizations",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Update report schedule in sub msp org with msp 2 token",ti.root_msp_org2_user1_token,submsp_schedule_id,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
						submsp_source_group_id_list,ti.root_msp1_submsp_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"selected_organizations",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Update report schedule in sub msp org with submsp token",ti.root_msp1_submsp2_user1_token,submsp_schedule_id,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
						submsp_source_group_id_list,ti.root_msp1_submsp_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"selected_organizations",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Update report schedule in sub msp org with submsp suborg token",ti.msp1_submsp1_suborg1_user1_token,submsp_schedule_id,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
						submsp_source_group_id_list,ti.root_msp1_submsp_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"selected_organizations",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
							
			
			//csr readonly user token
			{"Update report schedule in direct with csr readonly user token",ti.csr_readonly_token,direct_schedule_id,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
							direct_source_group_id_list,ti.direct_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"selected_organizations",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Update report schedule in msp with csr readonly user token token",ti.csr_readonly_token,suborg_schedule_id,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
								suborg_source_group_id_list,ti.root_msp_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"selected_organizations",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Update report schedule in suborg with csr readonly user token token",ti.csr_readonly_token,suborg_schedule_id,"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
									suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",daily_cron,"selected_organizations",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			
			//404 with schedule_id that does not exist
			{"Update report schedule in direct org with schedule_id that does not exist",ti.direct_org1_user1_token,UUID.randomUUID().toString(),"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
							direct_source_group_id_list,ti.direct_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"all_sources",SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.NOT_FOUND_SCHEDULE_WITH_ID},
			{"Update report schedule in msp org with schedule_id that does not exist",ti.root_msp_org1_user1_token,UUID.randomUUID().toString(),"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
							suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",daily_cron,"all_sources",SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.NOT_FOUND_SCHEDULE_WITH_ID},
			{"Update report schedule in suborg with schedule_id that does not exist",ti.root_msp1_suborg1_user1_token,UUID.randomUUID().toString(),"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
							suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",daily_cron,"all_sources",SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.NOT_FOUND_SCHEDULE_WITH_ID},
			{"Update report schedule in suborg with schedule_id that does not exist and msp_account_admin token",ti.root_msp_org1_msp_accountadmin1_token,UUID.randomUUID().toString(),"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
							suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",daily_cron,"all_sources",SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.NOT_FOUND_SCHEDULE_WITH_ID},
			{"Update report schedule in msp org with schedule_id that does not exist",ti.root_msp1_submsp1_user1_token,UUID.randomUUID().toString(),"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
							suborg_source_group_id_list,ti.msp1_submsp1_sub_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"all_sources",SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.NOT_FOUND_SCHEDULE_WITH_ID},
			{"Update report schedule in suborg with schedule_id that does not exist",ti.msp1_submsp1_suborg1_user1_token,UUID.randomUUID().toString(),"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
							suborg_source_group_id_list,ti.msp1_submsp1_sub_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"all_sources",SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.NOT_FOUND_SCHEDULE_WITH_ID},
			{"Update report schedule in suborg with schedule_id that does not exist and msp_account_admin token",ti.root_msp1_submsp1_account_admin_token,UUID.randomUUID().toString(),"report1","backup_jobs","today",System.currentTimeMillis(),System.currentTimeMillis()+10000,"daily",
							suborg_source_group_id_list,ti.msp1_submsp1_sub_org1_id,"a@gmail.com,b@gmail.com",daily_cron,"all_sources",SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.NOT_FOUND_SCHEDULE_WITH_ID},


		};
	}
	
	@Test(dataProvider="reportScheduleDataInvalid",dependsOnMethods= {"postSources","createReportSchedulesValid"})
	public void updateReportSchedulesInvalid(String caseType, String token, String schedule_id, String report_name, String report_type, String date_range_type, long date_range_start_ts,
												long date_range_end_ts, String schedule_frequency, String source_group_id_list, String organization_id_list, String recipient_mail,
												String cron_expression,String report_for_type, int expectedStatusCode, SpogMessageCode expectedErrorMessage
												) {
		HashMap<String, Object> scheduleInfo = new HashMap<>();
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		
		test.log(LogStatus.INFO, "Compose report schedule info");
		scheduleInfo = spogReportServer.composeReportScheduleInfo(report_name, report_type, date_range_type, date_range_start_ts, date_range_end_ts, schedule_frequency, source_group_id_list, organization_id_list, recipient_mail, cron_expression,report_for_type);
		
		test.log(LogStatus.INFO, caseType);
		spogReportServer.updateReportScheduleByIdWithCheck(schedule_id, token, scheduleInfo, expectedStatusCode, expectedErrorMessage, test);
				
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
