package api.users.logsfilters;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
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
import Constants.LogSeverityType;
import Constants.LogSourceType;
import Constants.OSMajor;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.Log4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGJobServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import InvokerServer.GatewayServer.siteType;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import invoker.SiteTestHelper;
import io.restassured.response.Response;

public class GetspecifiedlogfilterbyspecifieduserIDTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private Log4SPOGServer log4SpogServer;
	private UserSpogServer userSpogServer;
	//private ExtentReports rep;
	private ExtentTest test;
	private TestOrgInfo ti;
	private SPOGDestinationServer spogDestinationServer;

	//this is for update portal, each testng class is taken as BQ set
	//private testcasescount count1;
	int passedcases=0;
	int failedcases=0;
	int skippedcases=0;

	private String test_log_Message_1 = "testLogMessage";
	private String test_log_Message_2 = "connect_node_failed_test_message";
	private String test_log_Message_3 = "testLogMessageWithoutData";
	private	String job_Type = "backup_full,restore,backup_full" ;/*conversionvm_backup,vm_recovery,vm_catalog_fs,mount_recovery_point,office365_backup,cifs_backup,sharepoint_backup,vm_merge,catalog_fs,catalog_app,catalog_grt,file_copy_backup,file_copy_purge,file_copy_restore,file_copy_catalog_sync,file_copy_source_delete,file_copy_delete,catalog_fs_ondemand,vm_catalog_fs_ondemand,rps_replicate,rps_replicate_in_bound,rps_merge,rps_conversion,bmr,rps_data_seeding,rps_data_seeding_in,vm_recovery_hyperv,rps_purge_datastore,start_instant_vm,stop_instant_vm,assure_recovery,start_instant_vhd,stop_instant_vhd,archive_to_tape,linux_instant_vm";*/
	private	String job_Status= "finished";
	private	String job_Method = "full,incremental,resync";

	public String JobSeverity= "success,warning,error,critical,information";
	public String logType="last_24_hours,last_7_days,last_2_weeks,last_1_month,custom";

	private Random r;
	//Sorting based on the create_ts 
	//Sorting based on the create_ts 
	long current = ZonedDateTime.now().toInstant().toEpochMilli()/1000L;
	long yesterday = ZonedDateTime.now().minusDays(1).toInstant().toEpochMilli()/1000L;
	long tomorrow = ZonedDateTime.now().plusDays(1).toInstant().toEpochMilli()/1000L;

	private ArrayList<String> jobs=new ArrayList<String>();
	private ArrayList<String> servers=new ArrayList<String>();
	private ArrayList<String> logs=new ArrayList<String>();
	private ArrayList<String> filters=new ArrayList<String>();


	@BeforeClass
	@Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword","csrReadOnlyAdminName","csrReadOnlyAdminPassword","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword,String csrReadOnlyAdminName,String csrReadOnlyAdminPassword, String logFolder,String runningMachine,String buildVersion) throws UnknownHostException {
		spogServer = new SPOGServer(baseURI, port);
		r = new Random();
		log4SpogServer=new Log4SPOGServer(baseURI,port);
		gatewayServer =new GatewayServer(baseURI,port);
		spogDestinationServer=new SPOGDestinationServer(baseURI,port);
		userSpogServer = new UserSpogServer(baseURI, port);

		rep = ExtentManager.getInstance("GetspecifiedlogfilterbyspecifieduserIDTest",logFolder);
		test = rep.startTest("beforeClass");
		//this is for update portal
		this.BQName = this.getClass().getSimpleName();
		String author = "Malleswari,Sykam";

		this.runningMachine =  InetAddress.getLocalHost().getHostName();
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date=new java.util.Date();
		this.buildVersion=buildVersion+"_"+dateFormater.format(date);
		Nooftest=0;
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();

		ti = new TestOrgInfo(spogServer, test);	
		if(count1.isstarttimehit()==0) {      
			creationTime=System.currentTimeMillis();
			count1.setcreationtime(creationTime);
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
	}
	@DataProvider(name = "log_data")
	public final Object[][] getLogDataParams() {
		return new Object[][] {
			//Direct Organization
			{"Get log filters of logservertiy is: information,Warning,error, and is default is true with the direct user token",ti.direct_org1_user1_token,ti.direct_org1_user1_id,ti.direct_org1_id,new String[] { LogSeverityType.information.name(),LogSeverityType.warning.name(),LogSeverityType.error.name()},
				true,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Get log filters of logservertiy is: Warning,error, and is default is true with the direct user token",ti.direct_org1_user1_token,ti.direct_org1_user1_id,ti.direct_org1_id,new String[] { LogSeverityType.warning.name(),LogSeverityType.error.name()},
					true,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Get log filters of logservertiy is: information,error, and is default is true with the direct user token",ti.direct_org1_user1_token,ti.direct_org1_user1_id,ti.direct_org1_id,new String[] { LogSeverityType.information.name(),LogSeverityType.error.name()},
						true,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Get log filters of logservertiy is: information,Warning, and is default is true with the direct user token",ti.direct_org1_user1_token,ti.direct_org1_user1_id,ti.direct_org1_id,new String[] {LogSeverityType.warning.name(),LogSeverityType.information.name()},
							true,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},

			{"Get log filters of logservertiy is: information,Warning,error, and is default is false with the direct user token",ti.direct_org1_user1_token,ti.direct_org1_user1_id,ti.direct_org1_id,new String[] { LogSeverityType.information.name(),LogSeverityType.warning.name(),LogSeverityType.error.name()},
								false,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Get log filters of logservertiy is: Warning,error, and is default is false with the direct user token",ti.direct_org1_user1_token,ti.direct_org1_user1_id,ti.direct_org1_id,new String[] { LogSeverityType.warning.name(),LogSeverityType.error.name()},
									false,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Get log filters of logservertiy is: information,error, and is default is false with the direct user token",ti.direct_org1_user1_token,ti.direct_org1_user1_id,ti.direct_org1_id,new String[] { LogSeverityType.information.name(),LogSeverityType.error.name()},
										false,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Get log filters of logservertiy is: information,Warning, and is default is false with the direct user token",ti.direct_org1_user1_token,ti.direct_org1_user1_id,ti.direct_org1_id,new String[] {LogSeverityType.warning.name(),LogSeverityType.information.name()},
											false,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},

			//Normal msp organization
			{"Get log filters of logservertiy is: information,Warning,error, and is default is true with the normal msp user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id,ti.normal_msp_org1_id,new String[] { LogSeverityType.information.name(),LogSeverityType.warning.name(),LogSeverityType.error.name()},
												true,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Get log filters of logservertiy is: Warning,error, and is default is true with the normal msp user  token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id,ti.normal_msp_org1_id,new String[] { LogSeverityType.warning.name(),LogSeverityType.error.name()},
													true,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Get log filters of logservertiy is: information,error, and is default is true with the normal msp user  token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id,ti.normal_msp_org1_id,new String[] { LogSeverityType.information.name(),LogSeverityType.error.name()},
														true,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Get log filters of logservertiy is: information,Warning, and is default is true with the normal msp user  token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id,ti.normal_msp_org1_id,new String[] {LogSeverityType.warning.name(),LogSeverityType.information.name()},
															true,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},

			{"Get log filters of logservertiy is: information,Warning,error, and is default is false with the normal msp user  token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id,ti.normal_msp_org1_id,new String[] { LogSeverityType.information.name(),LogSeverityType.warning.name(),LogSeverityType.error.name()},
																false,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Get log filters of logservertiy is: Warning,error, and is default is false with the normal msp user  token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id,ti.normal_msp_org1_id,new String[] { LogSeverityType.warning.name(),LogSeverityType.error.name()},
																	false,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Get log filters of logservertiy is: information,error, and is default is false with the normal msp user  token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id,ti.normal_msp_org1_id,new String[] { LogSeverityType.information.name(),LogSeverityType.error.name()},
																		false,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Get log filters of logservertiy is: information,Warning, and is default is false with the normal msp user  token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id,ti.normal_msp_org1_id,new String[] {LogSeverityType.warning.name(),LogSeverityType.information.name()},
																			false,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},


			//Normal msp account adimin user 

			{"Get log filters of logservertiy is: information,Warning,error, and is default is true with the Normal msp account adimin user token",ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id,ti.normal_msp_org1_id,new String[] { LogSeverityType.information.name(),LogSeverityType.warning.name(),LogSeverityType.error.name()},
																				true,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Get log filters of logservertiy is: Warning,error, and is default is true with the Normal msp account adimin user token",ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id,ti.normal_msp_org1_id,new String[] { LogSeverityType.warning.name(),LogSeverityType.error.name()},
																					true,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Get log filters of logservertiy is: information,error, and is default is true with the Normal msp account adimin user token",ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id,ti.normal_msp_org1_id,new String[] { LogSeverityType.information.name(),LogSeverityType.error.name()},
																						true,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Get log filters of logservertiy is: information,Warning, and is default is true with the Normal msp account adimin user token",ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id,ti.normal_msp_org1_id,new String[] {LogSeverityType.warning.name(),LogSeverityType.information.name()},
																							true,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},

			{"Get log filters of logservertiy is: information,Warning,error, and is default is false with the Normal msp account adimin user token",ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id,ti.normal_msp_org1_id,new String[] { LogSeverityType.information.name(),LogSeverityType.warning.name(),LogSeverityType.error.name()},
																								false,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Get log filters of logservertiy is: Warning,error, and is default is false with the Normal msp account adimin user token",ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id,ti.normal_msp_org1_id,new String[] { LogSeverityType.warning.name(),LogSeverityType.error.name()},
																									false,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Get log filters of logservertiy is: information,error, and is default is false with the Normal msp account adimin user token",ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id,ti.normal_msp_org1_id,new String[] { LogSeverityType.information.name(),LogSeverityType.error.name()},
																										false,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Get log filters of logservertiy is: information,Warning, and is default is false with the Normal msp account adimin user token",ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id,ti.normal_msp_org1_id,new String[] {LogSeverityType.warning.name(),LogSeverityType.information.name()},
																											false,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},

			//Customer account of Normal msp admin user 
			{"Get log filters of logservertiy is: information,Warning,error, and is default is true with the customer account of normal msp user token",ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id,ti.normal_msp1_suborg1_id,new String[] { LogSeverityType.information.name(),LogSeverityType.warning.name(),LogSeverityType.error.name()},
																												true,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Get log filters of logservertiy is: Warning,error, and is default is true with the customer account of normal msp  user token",ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id,ti.normal_msp1_suborg1_id,new String[] { LogSeverityType.warning.name(),LogSeverityType.error.name()},
																													true,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Get log filters of logservertiy is: information,error, and is default is true with the customer account of normal msp user token",ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id,ti.normal_msp1_suborg1_id,new String[] { LogSeverityType.information.name(),LogSeverityType.error.name()},
																														true,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Get log filters of logservertiy is: information,Warning, and is default is true with the customer account of normal msp  user token",ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id,ti.normal_msp1_suborg1_id,new String[] {LogSeverityType.warning.name(),LogSeverityType.information.name()},
																															true,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},

			{"Get log filters of logservertiy is: information,Warning,error, and is default is false with the customer account of normal msp  user token",ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id,ti.normal_msp1_suborg1_id,new String[] { LogSeverityType.information.name(),LogSeverityType.warning.name(),LogSeverityType.error.name()},
																																false,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Get log filters of logservertiy is: Warning,error, and is default is false with the customer account of normal msp  token",ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id,ti.normal_msp1_suborg1_id,new String[] { LogSeverityType.warning.name(),LogSeverityType.error.name()},
																																	false,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Get log filters of logservertiy is: information,error, and is default is false with the customer account of normal msp  user token",ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id,ti.normal_msp1_suborg1_id,new String[] { LogSeverityType.information.name(),LogSeverityType.error.name()},
																																		false,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Get log filters of logservertiy is: information,Warning, and is default is false with the customer account of normal msp  user token",ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id,ti.normal_msp1_suborg1_id,new String[] {LogSeverityType.warning.name(),LogSeverityType.information.name()},
																																			false,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},};																																												
	}

	//Filter on the Job_type ,severity_type
	@Test(dataProvider="log_data")	 
	public void getspecifiedLogFilterForLoggedInUserTest(
			String testcase,String validToken,
			String user_id,
			String org_id,
			String LogSeverity[],
			boolean isLogExist,
			long log_generate_time,
			String orgType,
			long startTimeTSStart,
			long startTimeTSEnd,
			String filterName, 
			String isDefault
			){		  
		ArrayList<HashMap<String,Object>> actual_Logs_data=new ArrayList<HashMap<String,Object>>(); 
		ArrayList<String> filter=new ArrayList<String>();
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		test.assignAuthor("Malleswari.Sykam");		  
		ArrayList<HashMap<String,Object>> expectedresponse = new ArrayList<>();
		HashMap<String,Object> expected_response = new HashMap<String,Object>();

		int count =2;

		String[] arrayoffilters= new String[count];

		long[] log_tsArray;
		//String[] arrayoffilters= new String[count];

		if(startTimeTSStart>0&&startTimeTSEnd>0) {
			startTimeTSStart=tomorrow;startTimeTSEnd=yesterday;
			//startTimeTSStart=start_time_ts+numbers[r.nextInt(numbers.length)];startTimeTSEnd=endTimeTS+numbers[r.nextInt(numbers.length)];
			//log_tsArray = new long[] {startTimeTSStart,startTimeTSEnd};
			log_tsArray=new long[]{tomorrow,yesterday};
		}else {
			log_tsArray = null;
		}		
		//GetThe jobs responses
		for(int k=0;k<LogSeverity.length;k++){
			log_generate_time= log_generate_time+1;		
			test.log(LogStatus.INFO,"create log for the job");		  
			String type[]=logType.split(",");
			int index=gen_random_index(type);

			filterName=spogServer.ReturnRandom("filterName");	
			filter.add(filterName);

			//created filter on  log severity ,source_name,message_data and origin
			log4SpogServer.setToken(validToken);
			if(startTimeTSStart>0&&startTimeTSEnd>0) {
				startTimeTSStart=tomorrow;startTimeTSEnd=yesterday;
				//startTimeTSStart=start_time_ts+numbers[r.nextInt(numbers.length)];startTimeTSEnd=endTimeTS+numbers[r.nextInt(numbers.length)];
				//log_tsArray = new long[] {startTimeTSStart,startTimeTSEnd};
				log_tsArray=new long[]{tomorrow,yesterday};
			}else {
				log_tsArray = null;
			}				
			filterName=spogServer.ReturnRandom("filterName");	
			filter.add(filterName);

			log4SpogServer.setToken(validToken);
			String filter_id =log4SpogServer.createLogFilters(user_id, org_id, filterName, type[index], log_tsArray, LogSeverity[k], isDefault,  test);
			test.log(LogStatus.INFO, "Compose the expected log filter for the user");					
			expected_response = log4SpogServer.composeExpectedLogFiltersWithCheck(filter_id ,filterName,user_id,org_id,LogSeverity[k],startTimeTSStart, startTimeTSEnd,null,null,isDefault,0);

			expectedresponse.add(expected_response);

			log4SpogServer.getspecifiedLogFilterByUserIDwithCheck(filter_id,UUID.randomUUID().toString(), validToken, new HashMap<>(), SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.USER_ID_DOESNOT_EXIST, test);

			expectedresponse=log4SpogServer.UpdateCountForFilters(expectedresponse,actual_Logs_data,test);
			test.log(LogStatus.INFO,"Delete the log filter by filter ID with the valid token ");
			log4SpogServer.deleteLogFilterByFilterID(user_id,filter_id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		}
	}


	@DataProvider(name = "get_log_filter_valid_401",parallel=false)
	public final Object[][] getLogFilterValidParams() {
		return new Object[][] {
			//posted jobs  to direct_user_id :job_type =backup_full and posted logs on :severity is information
			{"Get Direct org log filters with  invalid token", ti.direct_org1_user1_token,ti.direct_org1_user1_id },
			//Customer account of Normal Organization
			{"Get Customer account of Normal msp org log filters with  invalid token", ti.normal_msp1_suborg1_user1_token ,ti.normal_msp1_suborg1_user1_id},

		};
	}

	@Test (dataProvider = "get_log_filter_valid_401")
	public void GetLogFiltersForloggedinuser_401(
			String organizationType,
			String validtoken,
			String user_id

			)
	{
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		ArrayList<HashMap<String,Object>> expectedresponse = new ArrayList<>();



		log4SpogServer.setToken(validtoken);

		test.log(LogStatus.INFO, "Get specified log filter for specified user with valid token ");
		log4SpogServer.getspecifiedLogFilterByUserIDwithCheck(user_id,UUID.randomUUID().toString(),validtoken+"123", new HashMap<>(), SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);


		test.log(LogStatus.INFO, "Get specified log filter for specified user with valid token ");
		log4SpogServer.getspecifiedLogFilterByUserIDwithCheck(user_id,UUID.randomUUID().toString(),"", new HashMap<>(), SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);

		//generated random user id
		test.log(LogStatus.INFO, "Generated random user id");
		String random_user_id=UUID.randomUUID().toString();

		log4SpogServer.setToken(validtoken);
		test.log(LogStatus.INFO, "Get specified log filter for specified user with valid token " );
		log4SpogServer.getspecifiedLogFilterByUserIDwithCheck(random_user_id,UUID.randomUUID().toString(), validtoken, new HashMap<>(), SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.USER_ID_DOESNOT_EXIST, test);

		//generated ramdom filterid id
		test.log(LogStatus.INFO, "Generated random filterid");
		String random_filter_Id=UUID.randomUUID().toString();

		//get the specified filer for specified user 
		test.log(LogStatus.INFO, "Get specified log filter for specified user");
		log4SpogServer.getspecifiedLogFilterByUserIDwithCheck(user_id, random_filter_Id,validtoken, new HashMap<>(), SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.LOG_FILTER_NOT_FOUND_WITH_USER_ID, test);



	}



	@DataProvider(name = "Getspecifiedlogfilterbyspecifieduserid_403")
	public final Object[][] GetLogFilterValidParams2() {
		return new Object[][] {
			//Direct Organization
			{ "Get log Filters for the direct organziation with another Direct Organization user",ti.direct_org1_user1_token,ti.direct_org2_user1_token, ti.direct_org1_id,ti.direct_org1_user1_id,"backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Get log Filters for the direct organziation with Normal Msp user",ti.direct_org1_user1_token,ti.normal_msp_org1_user1_token, ti.direct_org1_id, ti.direct_org1_user1_id,"backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Get log Filters for the direct organziation with  Normal MSP accout Admin user",ti.direct_org1_user1_token, ti.normal_msp_org1_msp_accountadmin1_token,ti.direct_org1_id, ti.direct_org1_user1_id,"backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Get log Filters for the direct organziation with customer account of Normal msp user",ti.direct_org1_user1_token,ti.normal_msp1_suborg1_user1_token,ti.direct_org1_id, ti.direct_org1_user1_id,"backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Get log Filters for the direct organziation with Sub Msp user",ti.direct_org1_user1_token,ti.root_msp1_submsp1_user1_token, ti.direct_org1_id, ti.direct_org1_user1_id,"backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Get log Filters for the direct organziation with Msp account admin user of Sub MSP Organization",ti.direct_org1_user1_token,ti.root_msp1_submsp1_account_admin_token, ti.direct_org1_id, ti.direct_org1_user1_id,"backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Get log Filters for the direct organziation with Customer account of Sub msp Organization",ti.direct_org1_user1_token, ti.msp1_submsp1_suborg1_user1_token,ti.direct_org1_id, ti.direct_org1_user1_id,"backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Get log Filters for the direct organziation with Root MSP Organization",ti.direct_org1_user1_token, ti.root_msp_org1_user1_token,ti.direct_org1_id, ti.direct_org1_user1_id,"backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Get log Filters for the direct organziation with msp account admin user",ti.direct_org1_user1_token,ti.root_msp_org1_msp_accountadmin1_token, ti.direct_org1_id, ti.direct_org1_user1_id,"backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Get log Filters for the direct organziation with Customer account of Root Msp User ",ti.direct_org1_user1_token,ti.root_msp1_suborg1_user1_token, ti.direct_org1_id,ti.direct_org1_user1_id,"backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},


			//Normal Msp Organization
			{ "Get log Filters for the Normal MSP  organziation with another Direct Organization user",ti.normal_msp_org1_user1_token,ti.direct_org2_user1_token, ti.normal_msp_org1_id, ti.normal_msp_org1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Get log Filters for the Normal MSP  organziation with another Normal Msp Organization user", ti.normal_msp_org1_user1_token,ti.normal_msp_org2_user1_token,ti.normal_msp_org1_id, ti.normal_msp_org1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Get log Filters for the Normal MSP  organziation with another Normal Msp account admin user", ti.normal_msp_org1_user1_token,ti.normal_msp_org2_msp_accountadmin1_token,ti.normal_msp_org1_id, ti.normal_msp_org1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Get log Filters for the Normal MSP  organziation with another customer account admin user  of Normal Msp Organization",ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_user1_token,ti.normal_msp_org1_id, ti.normal_msp_org1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Get log Filters for the Normal MSP  organziation with  Sub Msp Organization user",ti.normal_msp_org1_user1_token,ti.root_msp1_submsp1_user1_token, ti.normal_msp_org1_id, ti.normal_msp_org1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Get log Filters for the Normal MSP  organziation with Msp account admin of Sub Msp Organization",ti.normal_msp_org1_user1_token, ti.root_msp1_submsp1_account_admin_token,ti.normal_msp_org1_id, ti.normal_msp_org1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Get log Filters for the Normal MSP  organziation with Customer account of sub msp Organization",ti.normal_msp_org1_user1_token,ti.msp1_submsp1_suborg1_user1_token, ti.normal_msp_org1_id, ti.normal_msp_org1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Get log Filters for the Normal MSP  organziation with  Root Msp Organization user",ti.normal_msp_org1_user1_token,ti.root_msp_org1_user1_token, ti.normal_msp_org1_id, ti.normal_msp_org1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Get log Filters for the Normal MSP  organziation with Msp account admin of Root Msp Organization user",ti.normal_msp_org1_user1_token, ti.root_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_id, ti.normal_msp_org1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Get log Filters for the Normal MSP  organziation with Customer account of  Root Msp Organization user",ti.normal_msp_org1_user1_token, ti.root_msp1_suborg1_user1_token,ti.normal_msp_org1_id, ti.normal_msp_org1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},


			//Root  Msp Organization
			{"Get log Filters for the Root MSP  organziation with another Direct Organization user",ti.root_msp_org1_user1_token, ti.direct_org2_user1_token,ti.root_msp_org1_id,  ti.root_msp_org1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{"Get log Filters for the Root MSP  organziation with  Normal MSP Organization user",ti.root_msp_org1_user1_token,ti.normal_msp_org1_user1_token, ti.root_msp_org1_id,  ti.root_msp_org1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{"Get log Filters for the Root MSP  organziation with  Msp account admin of Normal MSP Organization user",ti.root_msp_org1_user1_token, ti.normal_msp_org2_msp_accountadmin1_token,ti.root_msp_org1_id,  ti.root_msp_org1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{"Get log Filters for the Root MSP  organziation with  Customer Accountof Normal MSP Organization user",ti.root_msp_org1_user1_token, ti.normal_msp1_suborg1_user1_token,ti.root_msp_org1_id,  ti.root_msp_org1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{"Get log Filters for the Root MSP  organziation with Sub Msp Organization user",ti.root_msp_org1_user1_token,ti.root_msp1_submsp1_user1_token,ti.root_msp_org1_id,  ti.root_msp_org1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{"Get log Filters for the Root MSP  organziation with Customer account of Sub Msp Organization user",ti.root_msp_org1_user1_token,ti.msp1_submsp1_suborg1_user1_token, ti.root_msp_org1_id, ti.root_msp_org1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{"Get log Filters for the Root MSP  organziation with Msp account of Sub MSP Organization",ti.root_msp_org1_user1_token, ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp_org1_id,  ti.root_msp_org1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{"Get log Filters for the Root MSP  organziation with another Root Organization user",ti.root_msp_org1_user1_token,ti.root_msp_org2_user1_token, ti.root_msp_org1_id,  ti.root_msp_org1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{"Get log Filters for the Root MSP  organziation with another root Msp account admin user",ti.root_msp_org1_user1_token, ti.root_msp_org2_msp_accountadmin1_token, ti.root_msp_org1_id, ti.root_msp_org1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{"Get log Filters for the Root MSP  organziation with Customer account of Root MSP Organization user",ti.root_msp_org1_user1_token, ti.root_msp2_suborg1_user1_token,ti.root_msp_org1_id,  ti.root_msp_org1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},

			// Sub Msp Organization
			{ "Get log Filters for the SUB MSP  organziation with another Direct Organization user",ti.root_msp1_submsp1_user1_token,ti.direct_org2_user1_token, ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Get log Filters for the SUB MSP  organziation with Normal MSP Organization user",ti.root_msp1_submsp1_user1_token, ti.normal_msp_org1_user1_token,ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Get log Filters for the SUB MSP  organziation with  Msp account admin of Normal MSP Organization user",ti.root_msp1_submsp1_user1_token,ti.normal_msp_org2_msp_accountadmin1_token, ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Get log Filters for the SUB MSP  organziation with Customer Accountof Normal MSP Organization user",ti.root_msp1_submsp1_user1_token, ti.normal_msp1_suborg1_user1_token,ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Get log Filters for the SUB MSP  organziation with another SUB Organization user",ti.root_msp1_submsp1_user1_token, ti.root_msp2_submsp1_user1_token,ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Get log Filters for the SUB MSP  organziation with  Customer account of Sub Msp Organization user",ti.root_msp1_submsp1_user1_token, ti.msp2_submsp1_suborg1_user1_token,ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Get log Filters for the SUB MSP  organziation with Msp account admin of Sub Msp Organization user",ti.root_msp1_submsp1_user1_token, ti.root_msp_org2_msp_accountadmin1_token,ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Get log Filters for the SUB MSP  organziation with Root MSP Organization User",ti.root_msp1_submsp1_user1_token, ti.root_msp_org1_user1_token,ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Get log Filters for the SUB MSP  organziation with Msp account admin of root Msp Organization user", ti.root_msp1_submsp1_user1_token,ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Get log Filters for the SUB MSP  organziation with Customer account of root Msp Organization user",ti.root_msp1_submsp1_user1_token, ti.root_msp1_suborg1_user1_token,ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},

		};
	}

	@Test (dataProvider = "Getspecifiedlogfilterbyspecifieduserid_403")
	public void getspecifiedlogfilterbyspecifieduserid_403(
			String organizationType,
			String validtoken,
			String anotherorgtoken,
			String organization_id,
			String user_id, 
			String jobType,
			String log_severity,
			String filterName, 
			String isDefault,
			int count 
			)
	{

		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+organizationType);
		ArrayList<HashMap<String,Object>> expectedresponse = new ArrayList<>();
		HashMap<String,Object> expected_response = new HashMap<String,Object>();

		long[] log_tsArray=null;
		long startTimeTSStart=0; 
		long  startTimeTSEnd=0; 
		int responsecount = 0;

		log4SpogServer.setToken(validtoken);
		test.log(LogStatus.INFO,"The value of the Token is:"+validtoken);

		String generatedFrom_1=null;
		String message_data_1=null;
		String sourceName_1=null;
		String log_message_id_1=null;
		String source_id_1=null;
		String[] type=logType.split(",");
		int index=gen_random_index(type);

		String filter_id = log4SpogServer.createLogFilterwithCheck(user_id, organization_id,filterName,type[index], log_tsArray,jobType, log_severity,isDefault,log_message_id_1,source_id_1,message_data_1,sourceName_1,generatedFrom_1,test);
		test.log(LogStatus.INFO, "Compose the expected log filter for the user");					
		expected_response = log4SpogServer.composeExpectedLogFilter(filter_id,filterName,user_id,organization_id,jobType,log_severity,log_message_id_1,source_id_1,message_data_1,sourceName_1,generatedFrom_1,startTimeTSStart, startTimeTSEnd,null,null,isDefault,0);
		expectedresponse.add(expected_response);

		log4SpogServer.setToken(anotherorgtoken);
		test.log(LogStatus.INFO,"The value of the Token is:"+anotherorgtoken);


		test.log(LogStatus.INFO, "Get specified log filter for loggedin user");
		log4SpogServer.getspecifiedLogFilterByUserIDwithCheck(user_id, filter_id,anotherorgtoken, expected_response, SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY, test);


		//delete the filters 

		test.log(LogStatus.INFO,"Delete the log filter by filter ID with the valid token ");
		log4SpogServer.deleteLogFilterByFilterID(user_id,filter_id, validtoken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);



	}


	public int gen_random_index(String[] job_status) {
		Random generator = new Random();
		int randomindx = generator.nextInt(job_status.length);

		return randomindx;
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

}
