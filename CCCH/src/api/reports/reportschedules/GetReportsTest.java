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

public class GetReportsTest extends base.prepare.Is4Org {
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
	Response response;
	
	String direct_source_group_id_list;
	String msp_source_group_id_list;
	String submsp_source_group_id_list;
	String suborg_source_group_id_list;
	
	String direct_source_id;
	String msp_source_id;
	String suborg_source_id;	
	
	ArrayList<HashMap<String, Object>> direct_expected = new ArrayList<>();
	ArrayList<HashMap<String, Object>> msp_expected = new ArrayList<>();
	ArrayList<HashMap<String, Object>> submsp_expected = new ArrayList<>();
	ArrayList<HashMap<String, Object>> suborg_expected = new ArrayList<>();
	ArrayList<HashMap<String, Object>> mspAccAdmin_expected = new ArrayList<>();

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
	
	
	
	@DataProvider(name="data")
	public Object[][] data(){
		return new Object[][] {
			
			//dataprovider to create the sources in all organizations
			{"direct",ti.direct_org1_user1_token,"sourceName1", SourceType.machine, SourceProduct.cloud_direct, ti.direct_org1_id, ProtectionStatus.protect, ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange",  "Rak_vm2",  UUID.randomUUID().toString(), "Rak_agent1", "windows 2012", "64" ,"1.0.0","2.0", "http://upgrade",4},
			{"msp",ti.root_msp_org1_user1_token,"sourceName1", SourceType.machine, SourceProduct.cloud_direct, ti.root_msp_org1_id, ProtectionStatus.protect, ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange",  "Rak_vm2",  UUID.randomUUID().toString(), "Rak_agent1", "windows 2012", "64" ,"1.0.0","2.0", "http://upgrade",4},
			{"submsp",ti.root_msp1_submsp1_user1_token,"sourceName1", SourceType.machine, SourceProduct.cloud_direct, ti.root_msp1_submsp_org1_id, ProtectionStatus.protect, ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange",  "Rak_vm2",  UUID.randomUUID().toString(), "Rak_agent1", "windows 2012", "64" ,"1.0.0","2.0", "http://upgrade",4},
			{"suborg",ti.root_msp1_suborg1_user1_token,"sourceName1", SourceType.machine, SourceProduct.cloud_direct, ti.root_msp1_suborg1_id, ProtectionStatus.protect, ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange",  "Rak_vm2",  UUID.randomUUID().toString(), "Rak_agent1", "windows 2012", "64" ,"1.0.0","2.0", "http://upgrade",4},
			
		};
	}
	
	@Test(dataProvider= "data")
	public void postData(String orgType, String token,String source_name, SourceType source_type, SourceProduct source_product,
			String organization_id,ProtectionStatus protection_status,
			ConnectionStatus connection_status, String os_major, String applications, String vm_name,
			String hypervisor_id, String agent_name, String os_name, String os_architecture,
			String agent_current_version, String agent_upgrade_version, String agent_upgrade_link, 
			int noOfSourcesToCreate) {
		
		String source_group_id_list = null; 	String source_group_id  = null, source_id = null;
		HashMap<String, Object> SourceInfo = new HashMap<>();
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
		
		//Adding to the global varibles and creating reports
		if (orgType.contains("direct")) {
			direct_source_group_id_list = source_group_id_list;
			
			createReportSchedulesValid("direct",ti.direct_org1_user1_token,spogServer.ReturnRandom("dirrep").toLowerCase(),"backup_jobs","today",System.currentTimeMillis()/1000,(System.currentTimeMillis()/1000)+10000,"daily",
				direct_source_group_id_list,ti.direct_org1_id,"sykam.naga@gmail.com",null,"all_sources",SpogConstants.SUCCESS_GET_PUT_DELETE,null);
			createReportSchedulesValid("direct",ti.direct_org1_user1_token,spogServer.ReturnRandom("dirrep").toLowerCase(),"restore_jobs","last_7_days",System.currentTimeMillis()/1000,(System.currentTimeMillis()/1000)+10000,"weekly",
					direct_source_group_id_list,ti.direct_org1_id,"a@gmail.com,b@gmail.com",null,"all_organizations", SpogConstants.SUCCESS_GET_PUT_DELETE,null);
			createReportSchedulesValid("direct",ti.direct_org1_user1_token,spogServer.ReturnRandom("dirrep").toLowerCase(),"data_transfer","last_1_month",System.currentTimeMillis()/1000,(System.currentTimeMillis()/1000)+10000,"monthly",
					direct_source_group_id_list,ti.direct_org1_id,"a@gmail.com,b@gmail.com",null,"selected_organizations",SpogConstants.SUCCESS_GET_PUT_DELETE,null);
			createReportSchedulesValid("direct",ti.direct_org1_user1_token,spogServer.ReturnRandom("dirrep").toLowerCase(),"backup_jobs","last_3_months",System.currentTimeMillis()/1000,(System.currentTimeMillis()/1000)+10000,"weekly",
					direct_source_group_id_list,ti.direct_org1_id,"a@gmail.com,b@gmail.com",null,"selected_source_groups",SpogConstants.SUCCESS_GET_PUT_DELETE,null);
			createReportSchedulesValid("direct",ti.direct_org1_user1_token,spogServer.ReturnRandom("dirrep").toLowerCase(),"capacity_usage","last_6_months",System.currentTimeMillis()/1000,(System.currentTimeMillis()/1000)+10000,"daily",
					direct_source_group_id_list,ti.direct_org1_id,"a@gmail.com,b@gmail.com",null,"all_sources",SpogConstants.SUCCESS_GET_PUT_DELETE,null);
			createReportSchedulesValid("direct",ti.direct_org1_user1_token,spogServer.ReturnRandom("dirrep").toLowerCase(),"capacity_usage","last_1_year",System.currentTimeMillis()/1000,(System.currentTimeMillis()/1000)+10000,"daily",
					direct_source_group_id_list,ti.direct_org1_id,"a@gmail.com,b@gmail.com",null,"all_organizations",SpogConstants.SUCCESS_GET_PUT_DELETE,null);
			
		}else if(orgType.contains("submsp")) {
			submsp_source_group_id_list = source_group_id_list;
			
			createReportSchedulesValid("submsp",ti.root_msp1_submsp1_user1_token,spogServer.ReturnRandom("msprep").toLowerCase(),"backup_jobs","today",System.currentTimeMillis()/1000,(System.currentTimeMillis()/1000)+10000,"daily",
					submsp_source_group_id_list,ti.root_msp1_submsp_org1_id,"a@gmail.com,b@gmail.com",null,"all_sources",SpogConstants.SUCCESS_GET_PUT_DELETE,null);
			createReportSchedulesValid("submsp",ti.root_msp1_submsp1_user1_token,spogServer.ReturnRandom("msprep").toLowerCase(),"restore_jobs","last_7_days",System.currentTimeMillis()/1000,(System.currentTimeMillis()/1000)+10000,"weekly",
					submsp_source_group_id_list,ti.root_msp1_submsp_org1_id,"a@gmail.com,b@gmail.com",null,"all_sources",SpogConstants.SUCCESS_GET_PUT_DELETE,null);
			createReportSchedulesValid("submsp",ti.root_msp1_submsp1_user1_token,spogServer.ReturnRandom("msprep").toLowerCase(),"data_transfer","last_1_month",System.currentTimeMillis()/1000,(System.currentTimeMillis()/1000)+10000,"monthly",
					submsp_source_group_id_list,ti.root_msp1_submsp_org1_id,"a@gmail.com,b@gmail.com",null,"selected_organizations",SpogConstants.SUCCESS_GET_PUT_DELETE,null);
			createReportSchedulesValid("submsp",ti.root_msp1_submsp1_user1_token,spogServer.ReturnRandom("msprep").toLowerCase(),"backup_jobs","last_3_months",System.currentTimeMillis()/1000,(System.currentTimeMillis()/1000)+10000,"weekly",
					submsp_source_group_id_list,ti.root_msp1_submsp_org1_id,"a@gmail.com,b@gmail.com",null,"selected_source_groups",SpogConstants.SUCCESS_GET_PUT_DELETE,null);
			createReportSchedulesValid("submsp",ti.root_msp1_submsp1_user1_token,spogServer.ReturnRandom("msprep").toLowerCase(),"capacity_usage","last_6_months",System.currentTimeMillis()/1000,(System.currentTimeMillis()/1000)+10000,"daily",
					submsp_source_group_id_list,ti.root_msp1_submsp_org1_id,"a@gmail.com,b@gmail.com",null,"all_sources",SpogConstants.SUCCESS_GET_PUT_DELETE,null);
			createReportSchedulesValid("submsp",ti.root_msp1_submsp1_user1_token,spogServer.ReturnRandom("msprep").toLowerCase(),"capacity_usage","last_1_year",System.currentTimeMillis()/1000,(System.currentTimeMillis()/1000)+10000,"daily",
					submsp_source_group_id_list,ti.root_msp1_submsp_org1_id,"a@gmail.com,b@gmail.com",null,"all_organizations",SpogConstants.SUCCESS_GET_PUT_DELETE,null);
			
		}else if(orgType.contains("msp")) {
			msp_source_group_id_list = source_group_id_list;
			
			createReportSchedulesValid("msp",ti.root_msp_org1_user1_token,spogServer.ReturnRandom("msprep").toLowerCase(),"backup_jobs","today",System.currentTimeMillis()/1000,(System.currentTimeMillis()/1000)+10000,"daily",
					msp_source_group_id_list,ti.root_msp_org1_id,"a@gmail.com,b@gmail.com",null,"all_sources",SpogConstants.SUCCESS_GET_PUT_DELETE,null);
			createReportSchedulesValid("msp",ti.root_msp_org1_user1_token,spogServer.ReturnRandom("msprep").toLowerCase(),"restore_jobs","last_7_days",System.currentTimeMillis()/1000,(System.currentTimeMillis()/1000)+10000,"weekly",
					msp_source_group_id_list,ti.root_msp_org1_id,"a@gmail.com,b@gmail.com",null,"all_sources",SpogConstants.SUCCESS_GET_PUT_DELETE,null);
			createReportSchedulesValid("msp",ti.root_msp_org1_user1_token,spogServer.ReturnRandom("msprep").toLowerCase(),"data_transfer","last_1_month",System.currentTimeMillis()/1000,(System.currentTimeMillis()/1000)+10000,"monthly",
					msp_source_group_id_list,ti.root_msp_org1_id,"a@gmail.com,b@gmail.com",null,"selected_organizations",SpogConstants.SUCCESS_GET_PUT_DELETE,null);
			createReportSchedulesValid("msp",ti.root_msp_org1_user1_token,spogServer.ReturnRandom("msprep").toLowerCase(),"backup_jobs","last_3_months",System.currentTimeMillis()/1000,(System.currentTimeMillis()/1000)+10000,"weekly",
					msp_source_group_id_list,ti.root_msp_org1_id,"a@gmail.com,b@gmail.com",null,"selected_source_groups",SpogConstants.SUCCESS_GET_PUT_DELETE,null);
			createReportSchedulesValid("msp",ti.root_msp_org1_user1_token,spogServer.ReturnRandom("msprep").toLowerCase(),"capacity_usage","last_6_months",System.currentTimeMillis()/1000,(System.currentTimeMillis()/1000)+10000,"daily",
					msp_source_group_id_list,ti.root_msp_org1_id,"a@gmail.com,b@gmail.com",null,"all_sources",SpogConstants.SUCCESS_GET_PUT_DELETE,null);
			createReportSchedulesValid("msp",ti.root_msp_org1_user1_token,spogServer.ReturnRandom("msprep").toLowerCase(),"capacity_usage","last_1_year",System.currentTimeMillis()/1000,(System.currentTimeMillis()/1000)+10000,"daily",
					msp_source_group_id_list,ti.root_msp_org1_id,"a@gmail.com,b@gmail.com",null,"all_organizations",SpogConstants.SUCCESS_GET_PUT_DELETE,null);
			
		}else if(orgType.contains("suborg")) {
			suborg_source_group_id_list = source_group_id_list;
			
			createReportSchedulesValid("suborg",ti.root_msp1_suborg1_user1_token,spogServer.ReturnRandom("subrep").toLowerCase(),"backup_jobs","today",System.currentTimeMillis()/1000,(System.currentTimeMillis()/1000)+10000,"daily",
					suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",null,"all_sources",SpogConstants.SUCCESS_GET_PUT_DELETE,null);
			createReportSchedulesValid("suborg",ti.root_msp1_suborg1_user1_token,spogServer.ReturnRandom("subrep").toLowerCase(),"restore_jobs","last_7_days",System.currentTimeMillis()/1000,(System.currentTimeMillis()/1000)+10000,"weekly",
					suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",null,"all_organizations",SpogConstants.SUCCESS_GET_PUT_DELETE,null);
			createReportSchedulesValid("suborg",ti.root_msp1_suborg1_user1_token,spogServer.ReturnRandom("subrep").toLowerCase(),"data_transfer","last_1_month",System.currentTimeMillis()/1000,(System.currentTimeMillis()/1000)+10000,"monthly",
					suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",null,"selected_organizations",SpogConstants.SUCCESS_GET_PUT_DELETE,null);
			createReportSchedulesValid("suborg",ti.root_msp1_suborg1_user1_token,spogServer.ReturnRandom("subrep").toLowerCase(),"backup_jobs","last_3_months",System.currentTimeMillis()/1000,(System.currentTimeMillis()/1000)+10000,"weekly",
					suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",null,"selected_source_groups",SpogConstants.SUCCESS_GET_PUT_DELETE,null);
			createReportSchedulesValid("suborg",ti.root_msp1_suborg1_user1_token,spogServer.ReturnRandom("subrep").toLowerCase(),"capacity_usage","last_6_months",System.currentTimeMillis()/1000,(System.currentTimeMillis()/1000)+10000,"daily",
					suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",null,"all_sources",SpogConstants.SUCCESS_GET_PUT_DELETE,null);
			createReportSchedulesValid("suborg",ti.root_msp1_suborg1_user1_token,spogServer.ReturnRandom("subrep").toLowerCase(),"capacity_usage","last_1_year",System.currentTimeMillis()/1000,(System.currentTimeMillis()/1000)+10000,"daily",
					suborg_source_group_id_list,ti.root_msp1_suborg1_id,"a@gmail.com,b@gmail.com",null,"all_organizations",SpogConstants.SUCCESS_GET_PUT_DELETE,null);
			
		}
		
	}
	public void createReportSchedulesValid(String organizationType, String token, String report_name, String report_type, String date_range_type, long date_range_start_ts,
												long date_range_end_ts, String schedule_frequency, String source_group_id_list, String organization_id_list, String recipient_mail,
												String cron_expression, String report_for_type, int expectedStatusCode, SpogMessageCode expectedErrorMessage
												) {
		HashMap<String, Object> scheduleInfo = new HashMap<>();
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		
		test.log(LogStatus.INFO, "Compose report schedule info");
		scheduleInfo = spogReportServer.composeReportScheduleInfo(report_name, report_type, date_range_type, date_range_start_ts, date_range_end_ts,schedule_frequency, source_group_id_list, organization_id_list, recipient_mail, cron_expression, report_for_type);
		
		test.log(LogStatus.INFO, "Create report schedule with check for the organization: "+organizationType);
		Response response = spogReportServer.createReportSchedule(token, scheduleInfo, expectedStatusCode, test);
		HashMap<String, Object> data = response.then().extract().path("data");
		data.put("create_ts", String.valueOf(System.currentTimeMillis()));
				
		spogServer.setToken(token);
		data.put("create_user", spogServer.getLoggedinUser_EmailId());
		data.put("report_for_type", report_for_type);
		data.put("date_range_start_ts", String.valueOf(date_range_start_ts));
		data.put("date_range_end_ts", String.valueOf(date_range_end_ts));
		
		/* Added to sort the columns based on values not on alphabetic order
		 * 
		 * Commenting as the the sort feature is changed to alphabetic order irrespective of values to maintain consistency
		 * 
		 * if (schedule_frequency.equalsIgnoreCase("daily")) {
			data.put("sort_SF", "1");
		}else if (schedule_frequency.equalsIgnoreCase("weekly")) {
			data.put("sort_SF", "2");
		}else if (schedule_frequency.equalsIgnoreCase("monthly")) {
			data.put("sort_SF", "3");
		}else if (schedule_frequency.equalsIgnoreCase("quarterly")) {
			data.put("sort_SF", "3");
		}
		
		if (date_range_type.equalsIgnoreCase("today")) {
			data.put("sort_DRT", "1");
		}else if (date_range_type.equalsIgnoreCase("last_7_days")) {
			data.put("sort_DRT", "2");
		}else if (date_range_type.equalsIgnoreCase("last_1_month")) {
			data.put("sort_DRT", "3");
		}else if (date_range_type.equalsIgnoreCase("last_3_months")) {
			data.put("sort_DRT", "4");
		}else if (date_range_type.equalsIgnoreCase("last_6_months")) {
			data.put("sort_DRT", "5");
		}else if (date_range_type.equalsIgnoreCase("last_1_year")) {
			data.put("sort_DRT", "6");
		}
		
		if (report_type.equalsIgnoreCase("backup_jobs")) {
			data.put("sort_RT", "1");
		}else if (report_type.equalsIgnoreCase("restore_jobs")) {
			data.put("sort_RT", "2");
		}else if (report_type.equalsIgnoreCase("data_transfer")) {
			data.put("sort_RT", "3");
		}else if (report_type.equalsIgnoreCase("capacity_usage")) {
			data.put("sort_RT", "4");
		}*/
		
		if (organizationType.equalsIgnoreCase("direct")) {
			direct_expected.add(data);
		} else if(organizationType.equalsIgnoreCase("submsp")){
			submsp_expected.add(data);
		}else if(organizationType.equalsIgnoreCase("msp")){
			msp_expected.add(data);
		}else if (organizationType.equalsIgnoreCase("suborg")) {
			suborg_expected.add(data);
		}else if(organizationType.contains("mspAccAdminToken")) {
			mspAccAdmin_expected.add(data);
		}						
	}
	
	@DataProvider(name="reportsData")
	public Object[][] reportsData(){
		return new Object[][] {
			
			/*
			 * Date: 24-10-2018 Sprint 24
			 * Commented schedule_frequency cases as Reports are Generated_now not scheduled. So returning schedule_frequency as null 
			 */
			
			//csr_readonly
			{"Get reports for direct org with csr_readonly token",ti.csr_readonly_token, direct_expected, "organization_id;=;"+ti.direct_org1_id, "report_name;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for msp org with csr readonly token",ti.csr_readonly_token, msp_expected, "organization_id;=;"+ti.root_msp_org1_id, "report_name;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for sub org with csr readonly user token",ti.csr_readonly_token, suborg_expected, "organization_id;=;"+ti.root_msp1_suborg1_id, "report_name;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			
			//Direct
			{"Get reports for direct org filter by < date_range_start_ts",ti.direct_org1_user1_token, direct_expected, "date_range_start_ts;<;"+direct_expected.get(2).get("date_range_start_ts"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for direct org filter by > date_range_start_ts",ti.direct_org1_user1_token, direct_expected, "date_range_start_ts;>;"+direct_expected.get(3).get("date_range_start_ts"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for direct org filter by date_range_start_ts",ti.direct_org1_user1_token, direct_expected, "date_range_start_ts;=;"+direct_expected.get(0).get("date_range_start_ts"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for direct org filter by report_name",ti.direct_org1_user1_token, direct_expected, "report_name;=;"+direct_expected.get(0).get("report_name"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
//			{"Get reports for direct org filter by schedule_frequency",ti.direct_org1_user1_token, direct_expected, "schedule_frequency;=;"+direct_expected.get(0).get("schedule_frequency"), "create_ts;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for direct org filter by date_range_type",ti.direct_org1_user1_token, direct_expected, "date_range_type;=;"+direct_expected.get(0).get("date_range_type"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for direct org filter by date_range_end_ts",ti.direct_org1_user1_token, direct_expected, "date_range_end_ts;=;"+direct_expected.get(0).get("date_range_end_ts"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for direct org filter by create_user_id",ti.direct_org1_user1_token, direct_expected, "create_user_id;=;"+direct_expected.get(0).get("create_user_id"), "create_ts;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for direct org",ti.direct_org1_user1_token, direct_expected, null, null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
						
			{"Get reports for direct org sort by report_name",ti.direct_org1_user1_token, direct_expected, null, "report_name;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for direct org sort by report_name descending order",ti.direct_org1_user1_token, direct_expected, null, "report_name;-asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
//			{"Get reports for direct org sort by schedule_frequency",ti.direct_org1_user1_token, direct_expected, null, "schedule_frequency;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
//			{"Get reports for direct org sort by schedule_frequency descending order",ti.direct_org1_user1_token, direct_expected, null, "schedule_frequency;-asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for direct org sort by date_range_type",ti.direct_org1_user1_token, direct_expected, null, "date_range_type;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for direct org sort by date_range_type descending order",ti.direct_org1_user1_token, direct_expected, null, "date_range_type;-asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for direct org sort by create_ts",ti.direct_org1_user1_token, direct_expected, null, "create_ts;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for direct org sort by create_ts descending order",ti.direct_org1_user1_token, direct_expected, null, "create_ts;-asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
//			{"Get reports for direct org sort by report_for_type",ti.direct_org1_user1_token, direct_expected, null, "report_for_type;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
//			{"Get reports for direct org sort by report_for_type descending order",ti.direct_org1_user1_token, direct_expected, null, "report_for_type;-asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for direct org sort by generated_on",ti.direct_org1_user1_token, direct_expected, null, "generated_on;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for direct org sort by generated_on descending order",ti.direct_org1_user1_token, direct_expected, null, "generated_on;-asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for direct org sort by report_type",ti.direct_org1_user1_token, direct_expected, null, "report_type;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for direct org sort by report_type descending order",ti.direct_org1_user1_token, direct_expected, null, "report_type;-asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for direct org sort by create_user",ti.direct_org1_user1_token, direct_expected, null, "create_user;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for direct org sort by create_user descending order",ti.direct_org1_user1_token, direct_expected, null, "create_user;-asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				
			//MSP
			{"Get reports for msp org filter by < date_range_start_ts",ti.root_msp_org1_user1_token, msp_expected, "date_range_start_ts;<;"+msp_expected.get(3).get("date_range_start_ts"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for msp org filter by > date_range_start_ts",ti.root_msp_org1_user1_token, msp_expected, "date_range_start_ts;>;"+msp_expected.get(2).get("date_range_start_ts"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for msp org filter by date_range_start_ts",ti.root_msp_org1_user1_token, msp_expected, "date_range_start_ts;=;"+msp_expected.get(0).get("date_range_start_ts"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for msp org filter by report_name",ti.root_msp_org1_user1_token, msp_expected, "report_name;=;"+msp_expected.get(0).get("report_name"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
//			{"Get reports for msp org filter by schedule_frequency",ti.root_msp_org1_user1_token, msp_expected, "schedule_frequency;=;"+msp_expected.get(0).get("schedule_frequency"), "create_ts;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for msp org filter by date_range_type",ti.root_msp_org1_user1_token, msp_expected, "date_range_type;=;"+msp_expected.get(0).get("date_range_type"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for msp org filter by date_range_end_ts",ti.root_msp_org1_user1_token, msp_expected, "date_range_end_ts;=;"+msp_expected.get(0).get("date_range_end_ts"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for msp org filter by create_user_id",ti.root_msp_org1_user1_token, msp_expected, "create_user_id;=;"+msp_expected.get(0).get("create_user_id"), "create_ts;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for msp org ",ti.root_msp_org1_user1_token, msp_expected, null, null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			
			{"Get reports for msp org sort by report_name",ti.root_msp_org1_user1_token, msp_expected, null, "report_name;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for msp org sort by report_name descending order",ti.root_msp_org1_user1_token, msp_expected, null, "report_name;-asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
//			{"Get reports for msp org sort by schedule_frequency",ti.root_msp_org1_user1_token, msp_expected, null, "schedule_frequency;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
//			{"Get reports for msp org sort by schedule_frequency descending order",ti.root_msp_org1_user1_token, msp_expected, null, "schedule_frequency;-asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for msp org sort by date_range_type",ti.root_msp_org1_user1_token, msp_expected, null, "date_range_type;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for msp org sort by date_range_type descending order",ti.root_msp_org1_user1_token, msp_expected, null, "date_range_type;-asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for msp org sort by create_ts",ti.root_msp_org1_user1_token, msp_expected, null, "create_ts;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for msp org sort by create_ts descending order",ti.root_msp_org1_user1_token, msp_expected, null, "create_ts;-asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
//			{"Get reports for msp org sort by report_for_type",ti.root_msp_org1_user1_token, msp_expected, null, "report_for_type;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
//			{"Get reports for msp org sort by report_for_type descending order",ti.root_msp_org1_user1_token, msp_expected, null, "report_for_type;-asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for msp org sort by generated_on",ti.root_msp_org1_user1_token, msp_expected, null, "generated_on;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for msp org sort by generated_on descending order",ti.root_msp_org1_user1_token, msp_expected, null, "generated_on;-asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for msp org sort by report_type",ti.root_msp_org1_user1_token, msp_expected, null, "report_type;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for msp org sort by report_type descending order",ti.root_msp_org1_user1_token, msp_expected, null, "report_type;-asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for msp org sort by create_user",ti.root_msp_org1_user1_token, msp_expected, null, "create_user;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for msp org sort by create_user descending order",ti.root_msp_org1_user1_token, msp_expected, null, "create_user;-asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			
			//SUB MSP
			{"Get reports for submsp org filter by < date_range_start_ts",ti.root_msp1_submsp1_user1_token, submsp_expected, "date_range_start_ts;<;"+msp_expected.get(3).get("date_range_start_ts"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for submsp org filter by > date_range_start_ts",ti.root_msp1_submsp1_user1_token, submsp_expected, "date_range_start_ts;>;"+msp_expected.get(2).get("date_range_start_ts"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for submsp org filter by date_range_start_ts",ti.root_msp1_submsp1_user1_token, submsp_expected, "date_range_start_ts;=;"+msp_expected.get(0).get("date_range_start_ts"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for submsp org filter by report_name",ti.root_msp1_submsp1_user1_token, submsp_expected, "report_name;=;"+msp_expected.get(0).get("report_name"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
//			{"Get reports for submsp org filter by schedule_frequency",ti.root_msp1_submsp1_user1_token, msp_expected, "schedule_frequency;=;"+msp_expected.get(0).get("schedule_frequency"), "create_ts;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for submsp org filter by date_range_type",ti.root_msp1_submsp1_user1_token, submsp_expected, "date_range_type;=;"+msp_expected.get(0).get("date_range_type"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for submsp org filter by date_range_end_ts",ti.root_msp1_submsp1_user1_token, submsp_expected, "date_range_end_ts;=;"+msp_expected.get(0).get("date_range_end_ts"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for submsp org filter by create_user_id",ti.root_msp1_submsp1_user1_token, submsp_expected, "create_user_id;=;"+msp_expected.get(0).get("create_user_id"), "create_ts;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for submsp org ",ti.root_msp1_submsp1_user1_token, submsp_expected, null, null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			
			{"Get reports for submsp org sort by report_name",ti.root_msp1_submsp1_user1_token, submsp_expected, null, "report_name;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for submsp org sort by report_name descending order",ti.root_msp1_submsp1_user1_token, submsp_expected, null, "report_name;-asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
//			{"Get reports for submsp org sort by schedule_frequency",ti.root_msp1_submsp1_user1_token, submsp_expected, null, "schedule_frequency;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
//			{"Get reports for submsp org sort by schedule_frequency descending order",ti.root_msp1_submsp1_user1_token, submsp_expected, null, "schedule_frequency;-asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for submsp org sort by date_range_type",ti.root_msp1_submsp1_user1_token, submsp_expected, null, "date_range_type;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for submsp org sort by date_range_type descending order",ti.root_msp1_submsp1_user1_token, submsp_expected, null, "date_range_type;-asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for submsp org sort by create_ts",ti.root_msp1_submsp1_user1_token, submsp_expected, null, "create_ts;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for submsp org sort by create_ts descending order",ti.root_msp1_submsp1_user1_token, submsp_expected, null, "create_ts;-asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
//			{"Get reports for submsp org sort by report_for_type",ti.root_msp1_submsp1_user1_token, submsp_expected, null, "report_for_type;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
//			{"Get reports for submsp org sort by report_for_type descending order",ti.root_msp1_submsp1_user1_token, submsp_expected, null, "report_for_type;-asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for submsp org sort by generated_on",ti.root_msp1_submsp1_user1_token, submsp_expected, null, "generated_on;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for submsp org sort by generated_on descending order",ti.root_msp1_submsp1_user1_token, submsp_expected, null, "generated_on;-asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for submsp org sort by report_type",ti.root_msp1_submsp1_user1_token, submsp_expected, null, "report_type;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for submsp org sort by report_type descending order",ti.root_msp1_submsp1_user1_token, submsp_expected, null, "report_type;-asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for submsp org sort by create_user",ti.root_msp1_submsp1_user1_token, submsp_expected, null, "create_user;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for submsp org sort by create_user descending order",ti.root_msp1_submsp1_user1_token, submsp_expected, null, "create_user;-asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			
			
			//Suborg
			{"Get reports for sub org filter by < date_range_start_ts",ti.root_msp1_suborg1_user1_token, suborg_expected, "date_range_start_ts;<;"+suborg_expected.get(5).get("date_range_start_ts"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for sub org filter by > date_range_start_ts",ti.root_msp1_suborg1_user1_token, suborg_expected, "date_range_start_ts;>;"+suborg_expected.get(1).get("date_range_start_ts"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for sub org filter by date_range_start_ts",ti.root_msp1_suborg1_user1_token, suborg_expected, "date_range_start_ts;=;"+suborg_expected.get(0).get("date_range_start_ts"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for sub org filter by report_name",ti.root_msp1_suborg1_user1_token, suborg_expected, "report_name;=;"+suborg_expected.get(0).get("report_name"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
//			{"Get reports for sub org filter by schedule_frequency",ti.root_msp1_suborg1_user1_token, suborg_expected, "schedule_frequency;=;"+suborg_expected.get(0).get("schedule_frequency"), "create_ts;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for sub org filter by date_range_type",ti.root_msp1_suborg1_user1_token, suborg_expected, "date_range_type;=;"+suborg_expected.get(0).get("date_range_type"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for sub org filter by date_range_end_ts",ti.root_msp1_suborg1_user1_token, suborg_expected, "date_range_end_ts;=;"+suborg_expected.get(0).get("date_range_end_ts"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for sub org filter by create_user_id",ti.root_msp1_suborg1_user1_token, suborg_expected, "create_user_id;=;"+suborg_expected.get(0).get("create_user_id"), "create_ts;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for sub org ",ti.root_msp1_suborg1_user1_token, suborg_expected, null, null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
						
			{"Get reports for sub org sort by report_name",ti.root_msp1_suborg1_user1_token, suborg_expected, null, "report_name;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for sub org sort by report_name descending order",ti.root_msp1_suborg1_user1_token, suborg_expected, null, "report_name;-asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
//			{"Get reports for sub org sort by schedule_frequency",ti.root_msp1_suborg1_user1_token, suborg_expected, null, "schedule_frequency;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
//			{"Get reports for sub org sort by schedule_frequency descending order",ti.root_msp1_suborg1_user1_token, suborg_expected, null, "schedule_frequency;-asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for sub org sort by date_range_type",ti.root_msp1_suborg1_user1_token, suborg_expected, null, "date_range_type;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for sub org sort by date_range_type descending order",ti.root_msp1_suborg1_user1_token, suborg_expected, null, "date_range_type;-asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for sub org sort by create_ts",ti.root_msp1_suborg1_user1_token, suborg_expected, null, "create_ts;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for sub org sort by create_ts descending order",ti.root_msp1_suborg1_user1_token, suborg_expected, null, "create_ts;-asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
//			{"Get reports for sub org sort by report_for_type",ti.root_msp1_suborg1_user1_token, suborg_expected, null, "report_for_type;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
//			{"Get reports for sub org sort by report_for_type descending order",ti.root_msp1_suborg1_user1_token, suborg_expected, null, "report_for_type;-asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for sub org sort by generated_on",ti.root_msp1_suborg1_user1_token, suborg_expected, null, "generated_on;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for sub org sort by generated_on descending order",ti.root_msp1_suborg1_user1_token, suborg_expected, null, "generated_on;-asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for sub org sort by report_type",ti.root_msp1_suborg1_user1_token, suborg_expected, null, "report_type;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for sub org sort by report_type descending order",ti.root_msp1_suborg1_user1_token, suborg_expected, null, "report_type;-asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for sub org sort by create_user",ti.root_msp1_suborg1_user1_token, suborg_expected, null, "create_user;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for sub org sort by create_user descending order",ti.root_msp1_suborg1_user1_token, suborg_expected, null, "create_user;-asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			
			/*{msp_account_admin_validToken, mspAccAdmin_expected, null, null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{msp_account_admin_validToken, mspAccAdmin_expected, "report_name;=;"+mspAccAdmin_expected.get(0).get("report_name"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{msp_account_admin_validToken, mspAccAdmin_expected, "schedule_frequency;=;"+mspAccAdmin_expected.get(0).get("schedule_frequency"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{msp_account_admin_validToken, mspAccAdmin_expected, "date_range_type;=;"+mspAccAdmin_expected.get(0).get("date_range_type"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{msp_account_admin_validToken, mspAccAdmin_expected, "date_range_start_ts;=;"+mspAccAdmin_expected.get(0).get("date_range_start_ts"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{msp_account_admin_validToken, mspAccAdmin_expected, "date_range_end_ts;=;"+mspAccAdmin_expected.get(0).get("date_range_end_ts"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{msp_account_admin_validToken, mspAccAdmin_expected, "create_user_id;=;"+mspAccAdmin_expected.get(0).get("create_user_id"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
		*/
			// monitor role cases
			{"Get reports for direct org with monitor token",ti.direct_org1_monitor_user1_token, direct_expected, "organization_id;=;"+ti.direct_org1_id, "report_name;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for msp org with monitor token",ti.root_msp_org1_monitor_user1_token, msp_expected, "organization_id;=;"+ti.root_msp_org1_id, "report_name;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for sub org with monitor token",ti.root_msp1_suborg1_monitor_user1_token, suborg_expected, "organization_id;=;"+ti.root_msp1_suborg1_id, "report_name;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for submsp org sort by report_name with monitor token",ti.root_msp1_submsp1_monitor_user1_token, submsp_expected, null, "report_name;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			
			
		};
	}
	
	@Test(dataProvider="reportsData",dependsOnMethods="postData")
	public void getReportsValid(String caseType,String token, ArrayList<HashMap<String, Object>> expectedResponse, String filterStr, String sortStr, int page,
									int page_size, int expectedStatusCode, SpogMessageCode expectedErrorMessage) {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());

		test.log(LogStatus.INFO, caseType);
		spogReportServer.getReportsWithCheck(token, expectedResponse, filterStr, sortStr, page, page_size, expectedStatusCode, expectedErrorMessage, test);
		
	}
	
	@DataProvider(name="reportsData1")
	public Object[][] reportsData1(){
		return new Object[][] {
			{"Get reports for direct org filter by generated_on = current time",ti.direct_org1_user1_token, direct_expected, "generated_on;=;"+System.currentTimeMillis()/1000, null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for direct org filter by generated_on < current time",ti.direct_org1_user1_token, direct_expected, "generated_on;<;"+System.currentTimeMillis()/1000, null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for direct org filter by generated_on > current time",ti.direct_org1_user1_token, direct_expected, "generated_on;>;"+System.currentTimeMillis()/1000, null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for direct org filter by generated_on",ti.direct_org1_user1_token, direct_expected, "generated_on;=;"+direct_expected.get(3).get("create_ts"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for direct org filter by generated_on",ti.direct_org1_user1_token, direct_expected, "generated_on;<;"+direct_expected.get(3).get("create_ts"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for direct org filter by generated_on",ti.direct_org1_user1_token, direct_expected, "generated_on;>;"+(Long.valueOf(direct_expected.get(3).get("create_ts").toString())), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for direct org filter by generated_on",ti.direct_org1_user1_token, direct_expected, "generated_on;=;"+direct_expected.get(0).get("create_ts"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for direct org filter by generated_on",ti.direct_org1_user1_token, direct_expected, "generated_on;<;"+direct_expected.get(0).get("create_ts"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for direct org filter by generated_on",ti.direct_org1_user1_token, direct_expected, "generated_on;>;"+(Long.valueOf(direct_expected.get(0).get("create_ts").toString())), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for direct org filter by generated_on",ti.direct_org1_user1_token, direct_expected, "generated_on;=;"+direct_expected.get(4).get("create_ts"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for direct org filter by generated_on",ti.direct_org1_user1_token, direct_expected, "generated_on;<;"+direct_expected.get(4).get("create_ts"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for direct org filter by generated_on",ti.direct_org1_user1_token, direct_expected, "generated_on;>;"+(Long.valueOf(direct_expected.get(2).get("create_ts").toString())), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			
			
			{"Get reports for msp org filter by generated_on = current time ",ti.root_msp_org1_user1_token, msp_expected, "generated_on;=;"+System.currentTimeMillis(), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for msp org filter by generated_on < current time",ti.root_msp_org1_user1_token, msp_expected, "generated_on;<;"+System.currentTimeMillis(), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for msp org filter by generated_on > current time",ti.root_msp_org1_user1_token, msp_expected, "generated_on;>;"+System.currentTimeMillis(), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for msp org filter by generated_on",ti.root_msp_org1_user1_token, msp_expected, "generated_on;=;"+msp_expected.get(3).get("create_ts"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for msp org filter by generated_on",ti.root_msp_org1_user1_token, msp_expected, "generated_on;<;"+msp_expected.get(3).get("create_ts"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for msp org filter by generated_on",ti.root_msp_org1_user1_token, msp_expected, "generated_on;>;"+(Long.valueOf(msp_expected.get(3).get("create_ts").toString())), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for msp org filter by generated_on",ti.root_msp_org1_user1_token, msp_expected, "generated_on;=;"+msp_expected.get(0).get("create_ts"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for msp org filter by generated_on",ti.root_msp_org1_user1_token, msp_expected, "generated_on;<;"+msp_expected.get(0).get("create_ts"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for msp org filter by generated_on",ti.root_msp_org1_user1_token, msp_expected, "generated_on;>;"+(Long.valueOf(msp_expected.get(0).get("create_ts").toString())), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for msp org filter by generated_on",ti.root_msp_org1_user1_token, msp_expected, "generated_on;=;"+msp_expected.get(4).get("create_ts"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for msp org filter by generated_on",ti.root_msp_org1_user1_token, msp_expected, "generated_on;<;"+msp_expected.get(4).get("create_ts"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for msp org filter by generated_on",ti.root_msp_org1_user1_token, msp_expected, "generated_on;>;"+(Long.valueOf(msp_expected.get(4).get("create_ts").toString())), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			
			
			/*{"Get reports for sub org filter by generated_on = current time ",ti.root_msp1_suborg1_user1_token, suborg_expected, "generated_on;=;"+System.currentTimeMillis(), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for suborg filter by generated_on < current time",ti.root_msp1_suborg1_user1_token, suborg_expected, "generated_on;<;"+System.currentTimeMillis(), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for suborg filter by generated_on > current time",ti.root_msp1_suborg1_user1_token, suborg_expected, "generated_on;>;"+System.currentTimeMillis(), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for suborg filter by generated_on",ti.root_msp1_suborg1_user1_token, suborg_expected, "generated_on;=;"+suborg_expected.get(3).get("create_ts"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for suborg filter by generated_on",ti.root_msp1_suborg1_user1_token, suborg_expected, "generated_on;<;"+suborg_expected.get(3).get("create_ts"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for suborg filter by generated_on",ti.root_msp1_suborg1_user1_token, suborg_expected, "generated_on;>;"+(Long.valueOf(suborg_expected.get(3).get("create_ts").toString())), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for suborg filter by generated_on",ti.root_msp1_suborg1_user1_token, suborg_expected, "generated_on;=;"+suborg_expected.get(0).get("create_ts"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for suborg filter by generated_on",ti.root_msp1_suborg1_user1_token, suborg_expected, "generated_on;<;"+suborg_expected.get(0).get("create_ts"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for suborg filter by generated_on",ti.root_msp1_suborg1_user1_token, suborg_expected, "generated_on;>;"+(Long.valueOf(suborg_expected.get(0).get("create_ts").toString())), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for suborg filter by generated_on",ti.root_msp1_suborg1_user1_token, suborg_expected, "generated_on;=;"+suborg_expected.get(4).get("create_ts"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for suborg filter by generated_on",ti.root_msp1_suborg1_user1_token, suborg_expected, "generated_on;<;"+suborg_expected.get(4).get("create_ts"), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get reports for suborg filter by generated_on",ti.root_msp1_suborg1_user1_token, suborg_expected, "generated_on;>;"+(Long.valueOf(suborg_expected.get(4).get("create_ts").toString())), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			*/
			};
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
