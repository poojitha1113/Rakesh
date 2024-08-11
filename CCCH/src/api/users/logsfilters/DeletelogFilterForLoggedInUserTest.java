package api.users.logsfilters;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZonedDateTime;
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

import Constants.LogSeverityType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.Log4SPOGServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;

public class DeletelogFilterForLoggedInUserTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private Log4SPOGServer spogLogServer;
	private UserSpogServer userSpogServer;
	private Log4SPOGServer log4SpogServer;
	//public int Nooftest;
	//private ExtentReports rep;
	private ExtentTest test;
	private String common_password = "Mclaren@2013";
	private TestOrgInfo ti;
	private String site_version="1.0.0";
	private String gateway_hostname="prasad";
	//used for test case count like passed,failed,remaining cases
	//private SQLServerDb bqdb1;
	//public int Nooftest;
	int passedcases=0;
	int failedcases=0;
	int skippedcases=0;
	//long creationTime;
	String buildnumber=null;
	//String BQame=null;
	//private testcasescount count1;

	private String org_model_prefix = this.getClass().getSimpleName();



	//private String runningMachine;
	//private String buildVersion;

	long current = ZonedDateTime.now().toInstant().toEpochMilli()/1000L;
	long yesterday = ZonedDateTime.now().minusDays(1).toInstant().toEpochMilli()/1000L;
	long tomorrow = ZonedDateTime.now().plusDays(1).toInstant().toEpochMilli()/1000L;
	public String logType="last_24_hours,last_7_days,last_2_weeks,last_1_month,custom";


	@BeforeClass
	@Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword","csrReadOnlyAdminName","csrReadOnlyAdminPassword","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword,String csrReadOnlyAdminName,String csrReadOnlyAdminPassword, String logFolder,String runningMachine,String buildVersion) throws UnknownHostException {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogLogServer = new Log4SPOGServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		log4SpogServer=new Log4SPOGServer(baseURI,port);
		rep = ExtentManager.getInstance("DeletelogFilterForLoggedInUserTest", logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Prasad.Deverakonda";

		Nooftest=0;

		//Used for creating a build number with dateformat
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date=new java.util.Date();
		this.buildVersion=buildVersion+"_"+dateFormater.format(date);

		this.BQName=this.getClass().getSimpleName();
		this.runningMachine=runningMachine;
		System.out.println("The value of hit is "+count1.isstarttimehit());
		ti = new TestOrgInfo(spogServer, test);

		/*if( count1.isstarttimehit( ) == 0 ) 
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
			    }*/
	}


	@DataProvider(name = "Delete_logFilters")
	public final Object[][] DeleteLogFilterValidParams() {
		return new Object[][] {
			//Direct Organization
			{"Delete log filters of logservertiy is: information,Warning,error, and is default is true with the direct user token",ti.direct_org1_user1_token,ti.direct_org1_user1_id,ti.direct_org1_id,new String[] { LogSeverityType.information.name(),LogSeverityType.warning.name(),LogSeverityType.error.name()},
				true,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Delete log filters of logservertiy is: Warning,error, and is default is true with the direct user token",ti.direct_org1_user1_token,ti.direct_org1_user1_id,ti.direct_org1_id,new String[] { LogSeverityType.warning.name(),LogSeverityType.error.name()},
					true,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Delete log filters of logservertiy is: information,error, and is default is true with the direct user token",ti.direct_org1_user1_token,ti.direct_org1_user1_id,ti.direct_org1_id,new String[] { LogSeverityType.information.name(),LogSeverityType.error.name()},
						true,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Delete log filters of logservertiy is: information,Warning, and is default is true with the direct user token",ti.direct_org1_user1_token,ti.direct_org1_user1_id,ti.direct_org1_id,new String[] {LogSeverityType.warning.name(),LogSeverityType.information.name()},
							true,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},

			{"Delete log filters of logservertiy is: information,Warning,error, and is default is false with the direct user token",ti.direct_org1_user1_token,ti.direct_org1_user1_id,ti.direct_org1_id,new String[] { LogSeverityType.information.name(),LogSeverityType.warning.name(),LogSeverityType.error.name()},
								false,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Delete log filters of logservertiy is: Warning,error, and is default is false with the direct user token",ti.direct_org1_user1_token,ti.direct_org1_user1_id,ti.direct_org1_id,new String[] { LogSeverityType.warning.name(),LogSeverityType.error.name()},
									false,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Delete log filters of logservertiy is: information,error, and is default is false with the direct user token",ti.direct_org1_user1_token,ti.direct_org1_user1_id,ti.direct_org1_id,new String[] { LogSeverityType.information.name(),LogSeverityType.error.name()},
										false,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Delete log filters of logservertiy is: information,Warning, and is default is false with the direct user token",ti.direct_org1_user1_token,ti.direct_org1_user1_id,ti.direct_org1_id,new String[] {LogSeverityType.warning.name(),LogSeverityType.information.name()},
											false,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},


			//Normal msp organization
			{"Delete log filters of logservertiy is: information,Warning,error, and is default is true with the normal msp user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id,ti.normal_msp_org1_id,new String[] { LogSeverityType.information.name(),LogSeverityType.warning.name(),LogSeverityType.error.name()},
												true,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Delete log filters of logservertiy is: Warning,error, and is default is true with the normal msp user  token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id,ti.normal_msp_org1_id,new String[] { LogSeverityType.warning.name(),LogSeverityType.error.name()},
													true,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Delete log filters of logservertiy is: information,error, and is default is true with the normal msp user  token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id,ti.normal_msp_org1_id,new String[] { LogSeverityType.information.name(),LogSeverityType.error.name()},
														true,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Delete log filters of logservertiy is: information,Warning, and is default is true with the normal msp user  token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id,ti.normal_msp_org1_id,new String[] {LogSeverityType.warning.name(),LogSeverityType.information.name()},
															true,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},

			{"Delete log filters of logservertiy is: information,Warning,error, and is default is false with the normal msp user  token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id,ti.normal_msp_org1_id,new String[] { LogSeverityType.information.name(),LogSeverityType.warning.name(),LogSeverityType.error.name()},
																false,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Delete log filters of logservertiy is: Warning,error, and is default is false with the normal msp user  token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id,ti.normal_msp_org1_id,new String[] { LogSeverityType.warning.name(),LogSeverityType.error.name()},
																	false,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Delete log filters of logservertiy is: information,error, and is default is false with the normal msp user  token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id,ti.normal_msp_org1_id,new String[] { LogSeverityType.information.name(),LogSeverityType.error.name()},
																		false,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Delete log filters of logservertiy is: information,Warning, and is default is false with the normal msp user  token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id,ti.normal_msp_org1_id,new String[] {LogSeverityType.warning.name(),LogSeverityType.information.name()},
																			false,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},


			//Normal msp account adimin user 

			{"Delete log filters of logservertiy is: information,Warning,error, and is default is true with the Normal msp account adimin user token",ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id,ti.normal_msp_org1_id,new String[] { LogSeverityType.information.name(),LogSeverityType.warning.name(),LogSeverityType.error.name()},
																				true,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Delete log filters of logservertiy is: Warning,error, and is default is true with the Normal msp account adimin user token",ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id,ti.normal_msp_org1_id,new String[] { LogSeverityType.warning.name(),LogSeverityType.error.name()},
																					true,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Delete log filters of logservertiy is: information,error, and is default is true with the Normal msp account adimin user token",ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id,ti.normal_msp_org1_id,new String[] { LogSeverityType.information.name(),LogSeverityType.error.name()},
																						true,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Delete log filters of logservertiy is: information,Warning, and is default is true with the Normal msp account adimin user token",ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id,ti.normal_msp_org1_id,new String[] {LogSeverityType.warning.name(),LogSeverityType.information.name()},
																							true,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},

			{"Delete log filters of logservertiy is: information,Warning,error, and is default is false with the Normal msp account adimin user token",ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id,ti.normal_msp_org1_id,new String[] { LogSeverityType.information.name(),LogSeverityType.warning.name(),LogSeverityType.error.name()},
																								false,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Delete log filters of logservertiy is: Warning,error, and is default is false with the Normal msp account adimin user token",ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id,ti.normal_msp_org1_id,new String[] { LogSeverityType.warning.name(),LogSeverityType.error.name()},
																									false,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Delete log filters of logservertiy is: information,error, and is default is false with the Normal msp account adimin user token",ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id,ti.normal_msp_org1_id,new String[] { LogSeverityType.information.name(),LogSeverityType.error.name()},
																										false,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Delete log filters of logservertiy is: information,Warning, and is default is false with the Normal msp account adimin user token",ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id,ti.normal_msp_org1_id,new String[] {LogSeverityType.warning.name(),LogSeverityType.information.name()},
																											false,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},

			//Customer account of Normal msp admin user 
			{"Delete log filters of logservertiy is: information,Warning,error, and is default is true with the customer account of normal msp user token",ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id,ti.normal_msp1_suborg1_id,new String[] { LogSeverityType.information.name(),LogSeverityType.warning.name(),LogSeverityType.error.name()},
																												true,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Delete log filters of logservertiy is: Warning,error, and is default is true with the customer account of normal msp  user token",ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id,ti.normal_msp1_suborg1_id,new String[] { LogSeverityType.warning.name(),LogSeverityType.error.name()},
																													true,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Delete log filters of logservertiy is: information,error, and is default is true with the customer account of normal msp user token",ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id,ti.normal_msp1_suborg1_id,new String[] { LogSeverityType.information.name(),LogSeverityType.error.name()},
																														true,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Delete log filters of logservertiy is: information,Warning, and is default is true with the customer account of normal msp  user token",ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id,ti.normal_msp1_suborg1_id,new String[] {LogSeverityType.warning.name(),LogSeverityType.information.name()},
																															true,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},

			{"Delete log filters of logservertiy is: information,Warning,error, and is default is false with the customer account of normal msp  user token",ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id,ti.normal_msp1_suborg1_id,new String[] { LogSeverityType.information.name(),LogSeverityType.warning.name(),LogSeverityType.error.name()},
																																false,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Delete log filters of logservertiy is: Warning,error, and is default is false with the customer account of normal msp  token",ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id,ti.normal_msp1_suborg1_id,new String[] { LogSeverityType.warning.name(),LogSeverityType.error.name()},
																																	false,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Delete log filters of logservertiy is: information,error, and is default is false with the customer account of normal msp  user token",ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id,ti.normal_msp1_suborg1_id,new String[] { LogSeverityType.information.name(),LogSeverityType.error.name()},
																																		false,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},
			{"Delete log filters of logservertiy is: information,Warning, and is default is false with the customer account of normal msp  user token",ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id,ti.normal_msp1_suborg1_id,new String[] {LogSeverityType.warning.name(),LogSeverityType.information.name()},
																																			false,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},};
	}

	//Filter on the Job_type ,severity_type
	@Test(dataProvider="Delete_logFilters")	 
	public void DeleteuserfiltersrLoggedInUserTest(String testcase,
			String validToken,
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
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		test.assignAuthor("Malleswari.Sykam");		  
		ArrayList<HashMap<String,Object>> expectedresponse = new ArrayList<>();
		HashMap<String,Object> expected_response = new HashMap<String,Object>();


		ArrayList<String>arrayoffilters=new ArrayList<>();
		long[] log_tsArray;

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
			String type[]=logType.split(",");
			int index=gen_random_index(type);

			filterName=spogServer.ReturnRandom("filterName");	

			log4SpogServer.setToken(validToken);
			String filter_Id=	(String) log4SpogServer.createLogFilters(user_id, org_id, filterName, type[index], log_tsArray, LogSeverity[k], isDefault,  test);


			test.log(LogStatus.INFO,"Delete the log filter by filter ID");
			spogLogServer.deleteLogFilterforloggedinUser(filter_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		}

	}


	@DataProvider(name = "Delete_log_filter_401_Secnarios")
	public final Object[][] DeleteLogFilterValidParams1() {
		return new Object[][] {
			//posted jobs  to direct_user_id :job_type =backup_full and posted logs on :severity is information
			{"Delete Direct org log filters with  invalid token", ti.direct_org1_user1_token },
			//Customer account of Normal Organization
			{"Delete Customer account of Normal msp org log filters with  invalid token", ti.normal_msp1_suborg1_user1_token },

		};
	}

	@Test (dataProvider = "Delete_log_filter_401_Secnarios")
	public void DeleteLogFiltersForloggedinuser_401(
			String organizationType,
			String validtoken

			)
	{
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		ArrayList<HashMap<String,Object>> expectedresponse = new ArrayList<>();
		log4SpogServer.setToken(validtoken);


		String randomFilter_id=UUID.randomUUID().toString();

		test.log(LogStatus.INFO,"Delete the log filter by filter ID using invalid JWT");
		spogLogServer.deleteLogFilterforloggedinUser(randomFilter_id, "J", SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

		test.log(LogStatus.INFO,"Delete the log filter by filter ID using invalid JWT");
		spogLogServer.deleteLogFilterforloggedinUser(randomFilter_id, "", SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);

	}


	@DataProvider(name = "DeleteLogFiltersForSpecifiedUser_403")
	public final Object[][] createLogFilterValidParams_403() {
		return new Object[][] {
			//posted jobs  to direct_user_id :job_type =backup_full and posted logs on :severity is information
			//Direct Organization
			{ "Delete log Filters for the direct organziation with another Direct Organization user",ti.direct_org1_user1_token,ti.direct_org2_user1_token, ti.direct_org1_id,ti.direct_org1_user1_id,"backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Delete log Filters for the direct organziation with Normal Msp user",ti.direct_org1_user1_token,ti.normal_msp_org1_user1_token, ti.direct_org1_id, ti.direct_org1_user1_id,"backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Delete log Filters for the direct organziation with  Normal MSP accout Admin user",ti.direct_org1_user1_token, ti.normal_msp_org1_msp_accountadmin1_token,ti.direct_org1_id, ti.direct_org1_user1_id,"backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Delete log Filters for the direct organziation with customer account of Normal msp user",ti.direct_org1_user1_token,ti.normal_msp1_suborg1_user1_token,ti.direct_org1_id, ti.direct_org1_user1_id,"backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Delete log Filters for the direct organziation with Sub Msp user",ti.direct_org1_user1_token,ti.root_msp1_submsp1_user1_token, ti.direct_org1_id, ti.direct_org1_user1_id,"backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Delete log Filters for the direct organziation with Msp account admin user of Sub MSP Organization",ti.direct_org1_user1_token,ti.root_msp1_submsp1_account_admin_token, ti.direct_org1_id, ti.direct_org1_user1_id,"backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Delete log Filters for the direct organziation with Customer account of Sub msp Organization",ti.direct_org1_user1_token, ti.msp1_submsp1_suborg1_user1_token,ti.direct_org1_id, ti.direct_org1_user1_id,"backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Delete log Filters for the direct organziation with Root MSP Organization",ti.direct_org1_user1_token, ti.root_msp_org1_user1_token,ti.direct_org1_id, ti.direct_org1_user1_id,"backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Delete log Filters for the direct organziation with msp account admin user",ti.direct_org1_user1_token,ti.root_msp_org1_msp_accountadmin1_token, ti.direct_org1_id, ti.direct_org1_user1_id,"backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Delete log Filters for the direct organziation with Customer account of Root Msp User ",ti.direct_org1_user1_token,ti.root_msp1_suborg1_user1_token, ti.direct_org1_id,ti.direct_org1_user1_id,"backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},


			//Normal Msp Organization
			{ "Delete log Filters for the Normal MSP  organziation with another Direct Organization user",ti.normal_msp_org1_user1_token,ti.direct_org2_user1_token, ti.normal_msp_org1_id, ti.normal_msp_org1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Delete log Filters for the Normal MSP  organziation with another Normal Msp Organization user", ti.normal_msp_org1_user1_token,ti.normal_msp_org2_user1_token,ti.normal_msp_org1_id, ti.normal_msp_org1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Delete log Filters for the Normal MSP  organziation with another Normal Msp account admin user", ti.normal_msp_org1_user1_token,ti.normal_msp_org2_msp_accountadmin1_token,ti.normal_msp_org1_id, ti.normal_msp_org1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Delete log Filters for the Normal MSP  organziation with another customer account admin user  of Normal Msp Organization",ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_user1_token,ti.normal_msp_org1_id, ti.normal_msp_org1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Delete log Filters for the Normal MSP  organziation with  Sub Msp Organization user",ti.normal_msp_org1_user1_token,ti.root_msp1_submsp1_user1_token, ti.normal_msp_org1_id, ti.normal_msp_org1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Delete log Filters for the Normal MSP  organziation with Msp account admin of Sub Msp Organization",ti.normal_msp_org1_user1_token, ti.root_msp1_submsp1_account_admin_token,ti.normal_msp_org1_id, ti.normal_msp_org1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Delete log Filters for the Normal MSP  organziation with Customer account of sub msp Organization",ti.normal_msp_org1_user1_token,ti.msp1_submsp1_suborg1_user1_token, ti.normal_msp_org1_id, ti.normal_msp_org1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Delete log Filters for the Normal MSP  organziation with  Root Msp Organization user",ti.normal_msp_org1_user1_token,ti.root_msp_org1_user1_token, ti.normal_msp_org1_id, ti.normal_msp_org1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Delete log Filters for the Normal MSP  organziation with Msp account admin of Root Msp Organization user",ti.normal_msp_org1_user1_token, ti.root_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_id, ti.normal_msp_org1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Delete log Filters for the Normal MSP  organziation with Customer account of  Root Msp Organization user",ti.normal_msp_org1_user1_token, ti.root_msp1_suborg1_user1_token,ti.normal_msp_org1_id, ti.normal_msp_org1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},


			//Root  Msp Organization
			{"Delete log Filters for the Root MSP  organziation with another Direct Organization user",ti.root_msp_org1_user1_token, ti.direct_org2_user1_token,ti.root_msp_org1_id,  ti.root_msp_org1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{"Delete log Filters for the Root MSP  organziation with  Normal MSP Organization user",ti.root_msp_org1_user1_token,ti.normal_msp_org1_user1_token, ti.root_msp_org1_id,  ti.root_msp_org1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{"Delete log Filters for the Root MSP  organziation with  Msp account admin of Normal MSP Organization user",ti.root_msp_org1_user1_token, ti.normal_msp_org2_msp_accountadmin1_token,ti.root_msp_org1_id,  ti.root_msp_org1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{"Delete log Filters for the Root MSP  organziation with  Customer Accountof Normal MSP Organization user",ti.root_msp_org1_user1_token, ti.normal_msp1_suborg1_user1_token,ti.root_msp_org1_id,  ti.root_msp_org1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{"Delete log Filters for the Root MSP  organziation with Sub Msp Organization user",ti.root_msp_org1_user1_token,ti.root_msp1_submsp1_user1_token,ti.root_msp_org1_id,  ti.root_msp_org1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{"Delete log Filters for the Root MSP  organziation with Customer account of Sub Msp Organization user",ti.root_msp_org1_user1_token,ti.msp1_submsp1_suborg1_user1_token, ti.root_msp_org1_id, ti.root_msp_org1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{"Delete log Filters for the Root MSP  organziation with Msp account of Sub MSP Organization",ti.root_msp_org1_user1_token, ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp_org1_id,  ti.root_msp_org1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{"Delete log Filters for the Root MSP  organziation with another Root Organization user",ti.root_msp_org1_user1_token,ti.root_msp_org2_user1_token, ti.root_msp_org1_id,  ti.root_msp_org1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{"Delete log Filters for the Root MSP  organziation with another root Msp account admin user",ti.root_msp_org1_user1_token, ti.root_msp_org2_msp_accountadmin1_token, ti.root_msp_org1_id, ti.root_msp_org1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{"Delete log Filters for the Root MSP  organziation with Customer account of Root MSP Organization user",ti.root_msp_org1_user1_token,ti.root_msp2_suborg1_user1_token, ti.root_msp_org1_id,  ti.root_msp_org1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},

			// Sub Msp Organization
			{ "Delete log Filters for the SUB MSP  organziation with another Direct Organization user",ti.root_msp1_submsp1_user1_token,ti.direct_org2_user1_token, ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Delete log Filters for the SUB MSP  organziation with Normal MSP Organization user",ti.root_msp1_submsp1_user1_token, ti.normal_msp_org1_user1_token,ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Delete log Filters for the SUB MSP  organziation with  Msp account admin of Normal MSP Organization user",ti.root_msp1_submsp1_user1_token,ti.normal_msp_org2_msp_accountadmin1_token, ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Delete log Filters for the SUB MSP  organziation with Customer Accountof Normal MSP Organization user",ti.root_msp1_submsp1_user1_token, ti.normal_msp1_suborg1_user1_token,ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Delete log Filters for the SUB MSP  organziation with another SUB Organization user",ti.root_msp1_submsp1_user1_token, ti.root_msp2_submsp1_user1_token,ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Delete log Filters for the SUB MSP  organziation with  Customer account of Sub Msp Organization user",ti.root_msp1_submsp1_user1_token, ti.msp2_submsp1_suborg1_user1_token,ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Delete log Filters for the SUB MSP  organziation with Msp account admin of Sub Msp Organization user",ti.root_msp1_submsp1_user1_token, ti.root_msp_org2_msp_accountadmin1_token,ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Delete log Filters for the SUB MSP  organziation with Root MSP Organization User",ti.root_msp1_submsp1_user1_token, ti.root_msp_org1_user1_token,ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Delete log Filters for the SUB MSP  organziation with Msp account admin of root Msp Organization user", ti.root_msp1_submsp1_user1_token,ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},
			{ "Delete log Filters for the SUB MSP  organziation with Customer account of root Msp Organization user",ti.root_msp1_submsp1_user1_token, ti.root_msp1_suborg1_user1_token,ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},

		};
	}

	@Test (dataProvider = "DeleteLogFiltersForSpecifiedUser_403")
	public void DeleteLogFiltersForSpecifiedUser_403(
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

		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());

		long[] log_tsArray;
		long startTimeTSStart =System.currentTimeMillis()%1000L;
		long startTimeTSEnd = System.currentTimeMillis()%1000L+1000;
		String LogSeverity = LogSeverityType.information.name();
		long log_generate_time =System.currentTimeMillis()%1000L;
		if(startTimeTSStart>0&&startTimeTSEnd>0) {
			startTimeTSStart=tomorrow;startTimeTSEnd=yesterday;
			//startTimeTSStart=start_time_ts+numbers[r.nextInt(numbers.length)];startTimeTSEnd=endTimeTS+numbers[r.nextInt(numbers.length)];
			//log_tsArray = new long[] {startTimeTSStart,startTimeTSEnd};
			log_tsArray=new long[]{tomorrow,yesterday};
		}else {
			log_tsArray = null;
		}		
		//GetThe jobs responses
		for(int k=0;k<LogSeverity.length();k++){
			log_generate_time= log_generate_time+1;		
			String type[]=logType.split(",");
			int index=gen_random_index(type);

			filterName=spogServer.ReturnRandom("filterName");	

			log4SpogServer.setToken(validtoken);
			String filter_Id=(String) log4SpogServer.createLogFilters(user_id, organization_id, filterName, type[index], log_tsArray, LogSeverity, isDefault,  test);

			test.log(LogStatus.INFO,"Delete the log filter by filter ID using other org JWT");
			spogLogServer.deleteLogFilterforloggedinUser(filter_Id, anotherorgtoken, SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.LOG_FILTER_NOT_FOUND_WITH_USER_ID, test);

			test.log(LogStatus.INFO,"Delete the log filter by filter ID");
			spogLogServer.deleteLogFilterforloggedinUser( filter_Id, validtoken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		}
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
	/*	@AfterTest
	public void aftertest() {
		test.log(LogStatus.INFO, "The total test cases passed are "+count1.getpassedcount());
		test.log(LogStatus.INFO, "the total test cases failed are "+count1.getfailedcount());

		rep.flush();

	}
	@AfterClass
	public void updatebd() {
		test.log(LogStatus.INFO, "Performing the operations to delete the user and orginzation by logging in as csr admin");	
		test.log(LogStatus.INFO, "Login in as csr admin to delete the organization");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);



		// spogServer.DeleteUserById(user_id, test);
		spogServer.DeleteOrganizationWithCheck(direct_organization_id, test);
		spogServer.DeleteOrganizationWithCheck(sub_org_Id, test);
		spogServer.DeleteOrganizationWithCheck(sub_org_Id_1, test);
		spogServer.DeleteOrganizationWithCheck(msp_organization_id, test);
		spogServer.DeleteOrganizationWithCheck(msp_organization_id_b, test);
		try {
			if(count1.getfailedcount()>0) {
				Nooftest=count1.getpassedcount()+count1.getfailedcount()+count1.getskippedcount();
				bqdb1.updateTable(BQame, runningMachine, this.buildVersion, String.valueOf(Nooftest), Integer.toString(count1.getpassedcount()), Integer.toString(count1.getfailedcount()), String.valueOf(count1.getskippedcount()), count1.getcreationtime(), "Failed");
			}else {
				Nooftest=count1.getpassedcount()+count1.getfailedcount()+count1.getskippedcount();
				bqdb1.updateTable(BQame, runningMachine, this.buildVersion, String.valueOf(Nooftest), Integer.toString(count1.getpassedcount()), Integer.toString(count1.getfailedcount()), String.valueOf(count1.getskippedcount()), count1.getcreationtime(), "Passed");
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	 */
}
