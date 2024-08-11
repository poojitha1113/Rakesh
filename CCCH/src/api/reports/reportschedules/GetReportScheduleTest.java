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
import org.apache.xalan.xsltc.compiler.sym;
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

public class GetReportScheduleTest extends base.prepare.Is4Org {
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
	
	ArrayList<String> direct_source_group_id_list = new ArrayList<>();
	ArrayList<String> submsp_suborg_source_group_id_list = new ArrayList<>();
	ArrayList<String> suborg_source_group_id_list = new ArrayList<>();
	
	String direct_source_id;
	String msp_source_id;
	String suborg_source_id;	
	
	ArrayList<HashMap<String, Object>> expDirScheduleInfo = new ArrayList<>();
	ArrayList<HashMap<String, Object>> expMspScheduleInfo = new ArrayList<>();
	ArrayList<HashMap<String, Object>> expSubMspScheduleInfo = new ArrayList<>();
	ArrayList<HashMap<String, Object>> expSubMspSubOrgScheduleInfo = new ArrayList<>();
	ArrayList<HashMap<String, Object>> expSuborgScheduleInfo = new ArrayList<>();
	private String  org_model_prefix=this.getClass().getSimpleName();
	private TestOrgInfo ti;
	
	String daily_cron = "0 0 19 ? * *"; //daily 7 PM
	String weekly_cron = "0 30 19 ? * 1"; //weekly 7.30 PM
	String monthly_cron = "0 0 20 13 * ?"; //monthly 8 PM, 13th of every month
	
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
		
		//Create report schedules
		createReportSchedules("direct",ti.direct_org1_user1_token,ti.direct_org1_id, ti.direct_org1_id, "a@gmail.com,b@gmail.com",daily_cron);
		createReportSchedules("msp",ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_id,ti.root_msp_org1_id,"a@gmail.com,b@gmail.com",daily_cron);
		ti.allLogin();
		createReportSchedules("submsp",ti.root_msp1_submsp1_user1_token,ti.msp1_submsp1_sub_org1_id,ti.root_msp1_submsp_org1_id,"a@gmail.com,b@gmail.com",daily_cron);
		createReportSchedules("suborg",ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",daily_cron);
		ti.allLogin();
		createReportSchedules("submsp_suborg",ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_sub_org1_id,ti.msp1_submsp1_sub_org1_id,"a@gmail.com,b@gmail.com",daily_cron);
		
		ti.allLogin();
	}
	
	
	public void createReportSchedules(String orgType, String token, String orgId, 
										String organization_id_list, String recipient_mail,
										String cron_expression) {
		
		String report_name = null;
		Response response = null;
		long date_range_start_ts = System.currentTimeMillis();
		long date_range_end_ts = System.currentTimeMillis()+10000;
		ArrayList<String> schedule_ids = new ArrayList<>();
		HashMap<String, Object> scheduleInfo = null;
		ArrayList<HashMap<String, Object>> expectedScheduleInfo = new ArrayList<>();
		
		/******************* Create source groups ***************************/
		String source_group_id_list = null; 	
		String source_group_id  = null;
		
		spogServer.setToken(token);
		for (int i = 0; i < 6; i++) {
			
			test.log(LogStatus.INFO, "Create source group number: "+i);
			source_group_id = spogServer.createGroupWithCheck(orgId, spogServer.ReturnRandom("group_name"), spogServer.ReturnRandom("group_description"), test);
			
			if (source_group_id_list == (null)) {
				source_group_id_list = source_group_id;
			} else {
				source_group_id_list = source_group_id_list+","+source_group_id ;
			}
			
			if (orgType.equalsIgnoreCase("direct")) {
				direct_source_group_id_list.add(source_group_id);
			} else if (orgType.equalsIgnoreCase("submsp_suborg")) {
				submsp_suborg_source_group_id_list.add(source_group_id);
			}else if(orgType.equalsIgnoreCase("suborg")){
				suborg_source_group_id_list.add(source_group_id);
			}
		}
		/************** End of create source groups ***********************/
		
		String[] report_types = {"backup_jobs","restore_jobs", "data_transfer", "capacity_usage"};
		String[] date_range_types = {"today","last_7_days","last_1_month","last_3_months","last_6_months","last_1_year"};
		String[] schedule_frequencies = {"daily", "weekly", "monthly","quarterly"};
		String[] report_for_types = {"all_sources", "selected_source_groups", "all_organizations", "selected_organizations"};
		
		for (int i = 0; i < report_types.length; i++) {
			for (int i2 = 0; i2 < date_range_types.length; i2++) {
				for (int i3 = 0; i3 < schedule_frequencies.length; i3++) {
					for (int i4 = 0; i4 < report_for_types.length; i4++) {
						
						report_name = spogServer.ReturnRandom("report").toLowerCase();
						scheduleInfo = new HashMap<>();
						
						test.log(LogStatus.INFO, "Compose report schedule info");
						scheduleInfo = spogReportServer.composeReportScheduleInfo(report_name, report_types[i], date_range_types[i2], date_range_start_ts, date_range_end_ts, schedule_frequencies[i3], source_group_id_list, organization_id_list, recipient_mail, cron_expression,report_for_types[i4]);
						
						test.log(LogStatus.INFO, "Create report schedule with check for the organization: "+orgType);
						response = spogReportServer.createReportSchedule(token, scheduleInfo, SpogConstants.SUCCESS_POST, test);
						scheduleInfo = response.then().extract().path("data");
						
						//adding report_for_type to expected for sorting
						scheduleInfo.put("report_for_type", report_for_types[i4]);
						
						schedule_ids.add(scheduleInfo.get("schedule_id").toString());
						expectedScheduleInfo.add(scheduleInfo);
						
						try { // wait 1 second to differentiate create_ts value
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
					}
				}
			}
		}
		
		if (orgType.contains("direct")) {
			expDirScheduleInfo = expectedScheduleInfo;	
		}else if(orgType.contains("submsp_suborg")){
			expSubMspSubOrgScheduleInfo = expectedScheduleInfo;
		}else if(orgType.contains("submsp")){
			expSubMspScheduleInfo = expectedScheduleInfo;
		} else if(orgType.contains("msp")){
			expMspScheduleInfo = expectedScheduleInfo;
		}else if(orgType.contains("suborg")){
			expSuborgScheduleInfo = expectedScheduleInfo;
		}
	}
	
	@DataProvider(name = "reportScheduleData")
	public Object[][] reportScheduleData(){
		return new Object[][] {
			//filters
			//csr readonly token
			{"Get report schedules in direct organization with csr_readonly token",
				ti.csr_readonly_token,"organization_id;=;"+ti.direct_org1_id, "create_ts;asc", 1, 20, expDirScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in direct organization filter by schedule_frequency=daily with csr_readonly token",
					ti.csr_readonly_token,"schedule_frequency;=;daily,organization_id;=;"+ti.direct_org1_id, "create_ts;asc", 1, 20, expDirScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in direct organization filter by schedule_frequency=weekly with csr_readonly token",
						ti.csr_readonly_token,"schedule_frequency;=;weekly,organization_id;=;"+ti.direct_org1_id, "create_ts;asc", 1, 20, expDirScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in direct organization filter by schedule_frequency=monthly with csr_readonly token",
							ti.csr_readonly_token,"schedule_frequency;=;monthly,organization_id;=;"+ti.direct_org1_id, "create_ts;asc", 1, 20, expDirScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in direct organization filter by schedule_frequency=quarterly with csr_readonly token",
							ti.csr_readonly_token,"schedule_frequency;=;quarterly,organization_id;=;"+ti.direct_org1_id, "create_ts;asc", 1, 20, expDirScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in direct organization filter by report_type=backup_jobs with csr_readonly token",
							ti.csr_readonly_token,"report_type;=;backup_jobs,organization_id;=;"+ti.direct_org1_id, "create_ts;asc", 1, 20, expDirScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in direct organization filter by report_type=restore_jobs with csr_readonly token",
							ti.csr_readonly_token,"report_type;=;restore_jobs,organization_id;=;"+ti.direct_org1_id, "create_ts;asc", 1, 20, expDirScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in direct organization filter by report_type=data_transfer with csr_readonly token",
							ti.csr_readonly_token,"report_type;=;data_transfer,organization_id;=;"+ti.direct_org1_id, "create_ts;asc", 1, 20, expDirScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in direct organization filter by report_type=capacity_usage with csr_readonly token",
							ti.csr_readonly_token,"report_type;=;capacity_usage,organization_id;=;"+ti.direct_org1_id, "create_ts;asc", 1, 20, expDirScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},

			{"Get report schedules in msp organization filter by organization_id with csr_readonly token",
								ti.csr_readonly_token,"organization_id;=;"+ti.root_msp_org1_id, "create_ts;asc", 1, 20, expMspScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in msp organization filter by schedule_frequency=daily with csr_readonly token",
								ti.csr_readonly_token,"schedule_frequency;=;daily,organization_id;=;"+ti.root_msp_org1_id, "create_ts;asc", 1, 20, expMspScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in msp organization filter by schedule_frequency=weekly with csr_readonly token",
								ti.csr_readonly_token,"schedule_frequency;=;weekly,organization_id;=;"+ti.root_msp_org1_id, "create_ts;asc", 1, 20, expMspScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in msp organization filter by schedule_frequency=monthly with csr_readonly token",
								ti.csr_readonly_token,"schedule_frequency;=;monthly,organization_id;=;"+ti.root_msp_org1_id, "create_ts;asc", 1, 20, expMspScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in msp organization filter by schedule_frequency=quarterly with csr_readonly token",
								ti.csr_readonly_token,"schedule_frequency;=;quarterly,organization_id;=;"+ti.root_msp_org1_id, "create_ts;asc", 1, 20, expMspScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in msp organization filter by report_type=backup_jobs with csr_readonly token",
								ti.csr_readonly_token,"report_type;=;backup_jobs,organization_id;=;"+ti.root_msp_org1_id, "create_ts;asc", 1, 20, expMspScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in msp organization filter by report_type=restore_jobs with csr_readonly token",
								ti.csr_readonly_token,"report_type;=;restore_jobs,organization_id;=;"+ti.root_msp_org1_id, "create_ts;asc", 1, 20, expMspScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in msp organization filter by report_type=data_transfer with csr_readonly token",
								ti.csr_readonly_token,"report_type;=;data_transfer,organization_id;=;"+ti.root_msp_org1_id, "create_ts;asc", 1, 20, expMspScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in msp organization filter by report_type=capacity_usage with csr_readonly token",
								ti.csr_readonly_token,"report_type;=;capacity_usage,organization_id;=;"+ti.root_msp_org1_id, "create_ts;asc", 1, 20, expMspScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},

			{"Get report schedules in suborg organization filter by organization_id with csr_readonly token",
								ti.csr_readonly_token,"organization_id;=;"+ti.root_msp1_suborg1_id, "create_ts;asc", 1, 20, expSuborgScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in suborg organization filter by schedule_frequency=daily with csr_readonly token",
								ti.csr_readonly_token,"schedule_frequency;=;daily,organization_id;=;"+ti.root_msp1_suborg1_id, "create_ts;asc", 1, 20, expSuborgScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in suborg organization filter by schedule_frequency=weekly with csr_readonly token",
								ti.csr_readonly_token,"schedule_frequency;=;weekly,organization_id;=;"+ti.root_msp1_suborg1_id, "create_ts;asc", 1, 20, expSuborgScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in suborg organization filter by schedule_frequency=monthly with csr_readonly token",
								ti.csr_readonly_token,"schedule_frequency;=;monthly,organization_id;=;"+ti.root_msp1_suborg1_id, "create_ts;asc", 1, 20, expSuborgScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in suborg organization filter by schedule_frequency=quarterly with csr_readonly token",
								ti.csr_readonly_token,"schedule_frequency;=;quarterly,organization_id;=;"+ti.root_msp1_suborg1_id, "create_ts;asc", 1, 20, expSuborgScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in suborg organization filter by report_type=backup_jobs with csr_readonly token",
								ti.csr_readonly_token,"report_type;=;backup_jobs,organization_id;=;"+ti.root_msp1_suborg1_id, "create_ts;asc", 1, 20, expSuborgScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in suborg organization filter by report_type=restore_jobs with csr_readonly token",
								ti.csr_readonly_token,"report_type;=;restore_jobs,organization_id;=;"+ti.root_msp1_suborg1_id, "create_ts;asc", 1, 20, expSuborgScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in suborg organization filter by report_type=data_transfer with csr_readonly token",
								ti.csr_readonly_token,"report_type;=;data_transfer,organization_id;=;"+ti.root_msp1_suborg1_id, "create_ts;asc", 1, 20, expSuborgScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in suborg organization filter by report_type=capacity_usage with csr_readonly token",
								ti.csr_readonly_token,"report_type;=;capacity_usage,organization_id;=;"+ti.root_msp1_suborg1_id, "create_ts;asc", 1, 20, expSuborgScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},

										
			{"Get report schedules in direct organization",
					ti.direct_org1_user1_token,null, "create_ts;asc", 1, 20, expDirScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in direct organization filter by organization_id",
						ti.direct_org1_user1_token,"organization_id;=;"+ti.direct_org1_id, "create_ts;asc", 1, 20, expDirScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in direct organization filter by schedule_frequency=daily",
							ti.direct_org1_user1_token,"schedule_frequency;=;daily", "create_ts;asc", 1, 20, expDirScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in direct organization filter by schedule_frequency=weekly",
								ti.direct_org1_user1_token,"schedule_frequency;=;weekly", "create_ts;asc", 1, 20, expDirScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in direct organization filter by schedule_frequency=monthly",
									ti.direct_org1_user1_token,"schedule_frequency;=;monthly", "create_ts;asc", 1, 20, expDirScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in direct organization filter by schedule_frequency=quarterly",
										ti.direct_org1_user1_token,"schedule_frequency;=;quarterly", "create_ts;asc", 1, 20, expDirScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in direct organization filter by report_type=backup_jobs",
								ti.direct_org1_user1_token,"report_type;=;backup_jobs", "create_ts;asc", 1, 20, expDirScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in direct organization filter by report_type=restore_jobs",
									ti.direct_org1_user1_token,"report_type;=;restore_jobs", "create_ts;asc", 1, 20, expDirScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in direct organization filter by report_type=data_transfer",
										ti.direct_org1_user1_token,"report_type;=;data_transfer", "create_ts;asc", 1, 20, expDirScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in direct organization filter by report_type=capacity_usage",
											ti.direct_org1_user1_token,"report_type;=;capacity_usage", "create_ts;asc", 1, 20, expDirScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			/*
			 * Filter by source group id & report_for_organization are dropped hence commenting the cases - Sprint 24
			 * 
			 * {"Get report schedules in direct organization filter by group_id 1",
									ti.direct_org1_user1_token,"group_id;=;"+direct_source_group_id_list.get(0), "create_ts;asc", 1, 20, expDirScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in direct organization filter by group_id 2",
										ti.direct_org1_user1_token,"group_id;=;"+direct_source_group_id_list.get(1), "create_ts;asc", 1, 20, expDirScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in direct organization filter by report_for_organization",
										ti.direct_org1_user1_token,"report_for_organization;=;"+ti.direct_org1_id, "create_ts;asc", 1, 20, expDirScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},*/
			
			{"Get report schedules in msp organization",
					ti.root_msp_org1_user1_token,null, "create_ts;asc", 1, 20, expMspScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in msp organization filter by organization_id",
						ti.root_msp_org1_user1_token,"organization_id;=;"+ti.root_msp_org1_id, "create_ts;asc", 1, 20, expMspScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in msp organization filter by schedule_frequency=daily",
							ti.root_msp_org1_user1_token,"schedule_frequency;=;daily", "create_ts;asc", 1, 20, expMspScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in msp organization filter by schedule_frequency=weekly",
								ti.root_msp_org1_user1_token,"schedule_frequency;=;weekly", "create_ts;asc", 1, 20, expMspScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in msp organization filter by schedule_frequency=monthly",
									ti.root_msp_org1_user1_token,"schedule_frequency;=;monthly", "create_ts;asc", 1, 20, expMspScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in msp organization filter by schedule_frequency=quarterly",
										ti.root_msp_org1_user1_token,"schedule_frequency;=;quarterly", "create_ts;asc", 1, 20, expMspScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in msp organization filter by report_type=backup_jobs",
								ti.root_msp_org1_user1_token,"report_type;=;backup_jobs", "create_ts;asc", 1, 20, expMspScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in msp organization filter by report_type=restore_jobs",
									ti.root_msp_org1_user1_token,"report_type;=;restore_jobs", "create_ts;asc", 1, 20, expMspScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in msp organization filter by report_type=data_transfer",
										ti.root_msp_org1_user1_token,"report_type;=;data_transfer", "create_ts;asc", 1, 20, expMspScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in msp organization filter by report_type=capacity_usage",
											ti.root_msp_org1_user1_token,"report_type;=;capacity_usage", "create_ts;asc", 1, 20, expMspScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			/*{"Get report schedules in msp organization filter by group_id",
									ti.root_msp_org1_user1_token,"group_id;=;"+msp_source_group_id_list.get(1), "create_ts;asc", 1, 20, expMspScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in msp organization filter by report_for_organization",
										ti.root_msp_org1_user1_token,"report_for_organization;=;"+ti.root_msp_org1_id, "create_ts;asc", 1, 20, expMspScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},*/
			
			{"Get report schedules in sub msp organization",
						ti.root_msp1_submsp1_user1_token,null, "create_ts;asc", 1, 20, expSubMspScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in sub msp organization filter by organization_id",
						ti.root_msp1_submsp1_user1_token,"organization_id;=;"+ti.root_msp1_submsp_org1_id, "create_ts;asc", 1, 20, expSubMspScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in sub msp organization filter by schedule_frequency=daily",
						ti.root_msp1_submsp1_user1_token,"schedule_frequency;=;daily", "create_ts;asc", 1, 20, expSubMspScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in sub msp organization filter by schedule_frequency=weekly",
						ti.root_msp1_submsp1_user1_token,"schedule_frequency;=;weekly", "create_ts;asc", 1, 20, expSubMspScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in sub msp organization filter by schedule_frequency=monthly",
						ti.root_msp1_submsp1_user1_token,"schedule_frequency;=;monthly", "create_ts;asc", 1, 20, expSubMspScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in sub msp organization filter by schedule_frequency=quarterly",
						ti.root_msp1_submsp1_user1_token,"schedule_frequency;=;quarterly", "create_ts;asc", 1, 20, expSubMspScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in sub msp organization filter by report_type=backup_jobs",
						ti.root_msp1_submsp1_user1_token,"report_type;=;backup_jobs", "create_ts;asc", 1, 20, expSubMspScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in sub msp organization filter by report_type=restore_jobs",
						ti.root_msp1_submsp1_user1_token,"report_type;=;restore_jobs", "create_ts;asc", 1, 20, expSubMspScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in sub msp organization filter by report_type=data_transfer",
						ti.root_msp1_submsp1_user1_token,"report_type;=;data_transfer", "create_ts;asc", 1, 20, expSubMspScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in sub msp organization filter by report_type=capacity_usage",
						ti.root_msp1_submsp1_user1_token,"report_type;=;capacity_usage", "create_ts;asc", 1, 20, expSubMspScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
										
			
			{"Get report schedules in suborg organization",
					ti.root_msp1_suborg1_user1_token,null, "create_ts;asc", 1, 20, expSuborgScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in suborg organization filter by organization_id",
						ti.root_msp1_suborg1_user1_token,"organization_id;=;"+ti.root_msp1_suborg1_id, "create_ts;asc", 1, 20, expSuborgScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in suborg organization filter by schedule_frequency=daily",
							ti.root_msp1_suborg1_user1_token,"schedule_frequency;=;daily", "create_ts;asc", 1, 20, expSuborgScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in suborg organization filter by schedule_frequency=weekly",
								ti.root_msp1_suborg1_user1_token,"schedule_frequency;=;weekly", "create_ts;asc", 1, 20, expSuborgScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in suborg organization filter by schedule_frequency=monthly",
									ti.root_msp1_suborg1_user1_token,"schedule_frequency;=;monthly", "create_ts;asc", 1, 20, expSuborgScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in suborg organization filter by schedule_frequency=quarterly",
										ti.root_msp1_suborg1_user1_token,"schedule_frequency;=;quarterly", "create_ts;asc", 1, 20, expSuborgScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in suborg organization filter by report_type=backup_jobs",
								ti.root_msp1_suborg1_user1_token,"report_type;=;backup_jobs", "create_ts;asc", 1, 20, expSuborgScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in suborg organization filter by report_type=restore_jobs",
									ti.root_msp1_suborg1_user1_token,"report_type;=;restore_jobs", "create_ts;asc", 1, 20, expSuborgScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in suborg organization filter by report_type=data_transfer",
										ti.root_msp1_suborg1_user1_token,"report_type;=;data_transfer", "create_ts;asc", 1, 20, expSuborgScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in suborg organization filter by report_type=capacity_usage",
											ti.root_msp1_suborg1_user1_token,"report_type;=;capacity_usage", "create_ts;asc", 1, 20, expSuborgScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			/*{"Get report schedules in suborg organization filter by group_id",
									ti.root_msp1_suborg1_user1_token,"group_id;=;"+suborg_source_group_id_list.get(2), "create_ts;asc", 1, 20, expSuborgScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in suborg organization filter by report_for_organization",
										ti.root_msp1_suborg1_user1_token,"report_for_organization;=;"+ti.root_msp1_suborg1_id, "create_ts;asc", 1, 20, expSuborgScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},*/
	
			{"Get report schedules in sub msp suborg organization",
						ti.msp1_submsp1_suborg1_user1_token,null, "create_ts;asc", 1, 20, expSubMspSubOrgScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in sub msp suborg organization filter by organization_id",
						ti.msp1_submsp1_suborg1_user1_token,"organization_id;=;"+ti.msp1_submsp1_sub_org1_id, "create_ts;asc", 1, 20, expSubMspSubOrgScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in sub msp suborg organization filter by schedule_frequency=daily",
						ti.msp1_submsp1_suborg1_user1_token,"schedule_frequency;=;daily", "create_ts;asc", 1, 20, expSubMspSubOrgScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in sub msp suborg organization filter by schedule_frequency=weekly",
						ti.msp1_submsp1_suborg1_user1_token,"schedule_frequency;=;weekly", "create_ts;asc", 1, 20, expSubMspSubOrgScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in sub msp suborg organization filter by schedule_frequency=monthly",
						ti.msp1_submsp1_suborg1_user1_token,"schedule_frequency;=;monthly", "create_ts;asc", 1, 20, expSubMspSubOrgScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in sub msp suborg organization filter by schedule_frequency=quarterly",
						ti.msp1_submsp1_suborg1_user1_token,"schedule_frequency;=;quarterly", "create_ts;asc", 1, 20, expSubMspSubOrgScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in sub msp suborg organization filter by report_type=backup_jobs",
						ti.msp1_submsp1_suborg1_user1_token,"report_type;=;backup_jobs", "create_ts;asc", 1, 20, expSubMspSubOrgScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in sub msp suborg organization filter by report_type=restore_jobs",
						ti.msp1_submsp1_suborg1_user1_token,"report_type;=;restore_jobs", "create_ts;asc", 1, 20, expSubMspSubOrgScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in sub msp suborg organization filter by report_type=data_transfer",
						ti.msp1_submsp1_suborg1_user1_token,"report_type;=;data_transfer", "create_ts;asc", 1, 20, expSubMspSubOrgScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in sub msp suborg organization filter by report_type=capacity_usage",
						ti.msp1_submsp1_suborg1_user1_token,"report_type;=;capacity_usage", "create_ts;asc", 1, 20, expSubMspSubOrgScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
										
			
			//sort
			{"Get report schedules in direct organization sort by create_ts ascending order",
				ti.direct_org1_user1_token,null, "create_ts;asc", 1, 20, expDirScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in direct organization sort by create_ts descending order",
					ti.direct_org1_user1_token,null, "create_ts;desc", 1, 20, expDirScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in direct organization sort by report_name ascending order",
					ti.direct_org1_user1_token,null, "report_name;asc", 1, 20, expDirScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in direct organization sort by report_name descending order",
						ti.direct_org1_user1_token,null, "report_name;desc", 1, 20, expDirScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in direct organization sort by report_type in ascending order",
						ti.direct_org1_user1_token,null, "report_type;asc", 1, 20, expDirScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in direct organization sort by report_type in descending order",
							ti.direct_org1_user1_token,null, "report_type;desc", 1, 20, expDirScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in direct organization sort by schedule_frequency ascending order",
							ti.direct_org1_user1_token,null, "schedule_frequency;asc", 1, 20, expDirScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in direct organization sort by schedule_frequency descending order",
								ti.direct_org1_user1_token,null, "schedule_frequency;desc", 1, 20, expDirScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in direct organization sort by date_range_type ascending order",
								ti.direct_org1_user1_token,null, "date_range_type;asc", 1, 20, expDirScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in direct organization sort by date_range_type descending order",
									ti.direct_org1_user1_token,null, "date_range_type;desc", 1, 20, expDirScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in direct organization sort by report_for ascending order",
									ti.direct_org1_user1_token,null, "report_for_type;asc", 1, 20, expDirScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in direct organization sort by report_for descending order",
									ti.direct_org1_user1_token,null, "report_for_type;desc", 1, 20, expDirScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			
			//pagination
			{"Get report schedules in direct organization page 1 page size 5",
				ti.direct_org1_user1_token,null, "create_ts;asc", 1, 5, expDirScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in direct organization page 2 page size 5",
					ti.direct_org1_user1_token,null, "create_ts;asc", 2, 5, expDirScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in direct organization page 3 page size 4",
						ti.direct_org1_user1_token,null, "create_ts;asc", 3, 4, expDirScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in direct organization page 4 page size 2",
							ti.direct_org1_user1_token,null, "create_ts;asc", 4, 2, expDirScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in direct organization page 2 page size 10",
								ti.direct_org1_user1_token,null, "create_ts;asc", 2, 10, expDirScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get report schedules in direct organization page 1 page size 20",
									ti.direct_org1_user1_token,null, "create_ts;asc", 1, 20, expDirScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
		
				// monitor role cases
				{ "Get report schedules in direct organization using monitor token page 1 page size 5",
						ti.direct_org1_monitor_user1_token, null, "create_ts;asc", 1, 5, expDirScheduleInfo,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get report schedules in suborg organization using monitor token",
						ti.root_msp1_suborg1_monitor_user1_token, null, "create_ts;asc", 1, 20, expSuborgScheduleInfo,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get report schedules in msp organization filter by schedule_frequency=daily using monitor token",
						ti.root_msp_org1_monitor_user1_token, "schedule_frequency;=;daily", "create_ts;asc", 1, 20,
						expMspScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get report schedules in sub msp organization filter by report_type=capacity_usage using monitor token",
						ti.root_msp1_submsp1_monitor_user1_token, "report_type;=;capacity_usage", "create_ts;asc", 1,
						20, expSubMspScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null }, 
				{ "Get report schedules in sub msp suborg organization filter by schedule_frequency=quarterly using monitor token",
						ti.msp1_submsp1_suborg1_monitor_user1_token, "schedule_frequency;=;quarterly", "create_ts;asc",
						1, 20, expSubMspSubOrgScheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null },	
	
		};
	}
	
	@Test(dataProvider="reportScheduleData")
	public void getReportSchedulesValid(String caseType, 
										String token,
										String filterStr,
										String sortStr,
										int page,
										int page_size,
										ArrayList<HashMap<String, Object>> expectedScheduleInfo,
										int expectedStatusCode,
										SpogMessageCode expectedErrorMessage 
										){
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
				
  		test.log(LogStatus.INFO, "Get report schedule data for the organization: "+caseType);
  		ArrayList<HashMap<String, Object>> temp=new ArrayList<HashMap<String, Object>>();
  		temp.addAll(expectedScheduleInfo);
  		spogServer.setToken(token);
  		expectedScheduleInfo.forEach(exp->{
  			HashMap<String, Object> act=new HashMap<String, Object>();
  			act=(HashMap<String, Object>) exp.get("create_user");
  			act.put("user_id", spogServer.GetLoggedinUser_UserID());
  		});
		spogReportServer.getReportSchedulesWithCheck(token,temp, filterStr,sortStr, page, page_size, expectedStatusCode, expectedErrorMessage, test);
						
	}
	
	@DataProvider(name = "reportScheduleDataInvalid")
	public Object[][] reportScheduleDataInvalid(){
		return new Object[][] {
			{"Get report schedule with invalid token","INVALID TOKEN",SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT},
			{"Get report schedule with missing token","",SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTHENTICATION_FAILED}
		};
	}
	
	@Test(dataProvider="reportScheduleDataInvalid",enabled=true)
	public void getReportSchedulesInvalid(String caseType, String token, int expectedStatusCode, SpogMessageCode expectedErrorMessage ) {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		ArrayList<HashMap<String, Object>> scheduleInfo = null;
		
		test.log(LogStatus.INFO, caseType);
		spogReportServer.getReportSchedulesWithCheck(token,scheduleInfo, null,null, 1, 20, expectedStatusCode, expectedErrorMessage, test);
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
	
	@AfterClass
	public void deleteInfo() {
		
	}
	
	/******************************************************************RandomFunction******************************************************************************/
	public int gen_random_index(String[] job_status) {
		Random generator = new Random();
		int randomindx = generator.nextInt(job_status.length);

		return randomindx;
	}
}
