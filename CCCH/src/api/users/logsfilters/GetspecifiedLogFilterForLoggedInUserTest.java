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
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import InvokerServer.GatewayServer.siteType;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import invoker.SiteTestHelper;
import io.restassured.response.Response;

public class GetspecifiedLogFilterForLoggedInUserTest extends base.prepare.Is4Org {
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

	//For storing the retention,cloud_direct_vloume information 


	public String JobSeverity= "success,warning,error,critical,information";

	public String logType="last_24_hours,last_7_days,last_2_weeks,last_1_month,custom";
	private Random r;
	//Sorting based on the create_ts 
	//Sorting based on the create_ts 
	long current = ZonedDateTime.now().toInstant().toEpochMilli()/1000L;
	long yesterday = ZonedDateTime.now().minusDays(1).toInstant().toEpochMilli()/1000L;
	long tomorrow = ZonedDateTime.now().plusDays(1).toInstant().toEpochMilli()/1000L;

	

@BeforeClass
	@Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword","csrReadOnlyAdminName","csrReadOnlyAdminPassword","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword,String csrReadOnlyAdminName,String csrReadOnlyAdminPassword, String logFolder,String runningMachine,String buildVersion) throws UnknownHostException {
		spogServer = new SPOGServer(baseURI, port);
		log4SpogServer=new Log4SPOGServer(baseURI,port);
		gatewayServer =new GatewayServer(baseURI,port);
		spogDestinationServer=new SPOGDestinationServer(baseURI,port);
		userSpogServer = new UserSpogServer(baseURI, port);
		r = new Random();
		rep = ExtentManager.getInstance("GetspecifiedLogFilterForLoggedInUserTest",logFolder);
		test = rep.startTest("Setup");
		ti = new TestOrgInfo(spogServer, test);	
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
																																			false,System.currentTimeMillis()%1000L,"direct",0,0,RandomStringUtils.randomAlphanumeric(2),"true"},

																																				
		};
	}

	//Filter on the Job_type ,severity_type
	@Test(dataProvider="log_data")	 
	public void getspecifiedLogFilterForLoggedInUserTest(String testcase,
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
		ArrayList<HashMap<String,Object>> actual_Logs_data=new ArrayList<HashMap<String,Object>>(); 
		ArrayList<String> filter=new ArrayList<String>();
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		test.assignAuthor("Malleswari.Sykam");		  
		ArrayList<HashMap<String,Object>> expectedresponse = new ArrayList<>();
		HashMap<String,Object> expected_response = new HashMap<String,Object>();

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
			String log_data="";

			String type[]=logType.split(",");

			int index=gen_random_index(type);

			filterName=spogServer.ReturnRandom("filterName");	
			filter.add(filterName);


			log4SpogServer.setToken(validToken);
			String filter_id_1 =log4SpogServer.createLogFilters(user_id, org_id, filterName, type[index], log_tsArray, LogSeverity[k], isDefault,  test);
			test.log(LogStatus.INFO, "Compose the expected log filter for the user");					
			expected_response = log4SpogServer.composeExpectedLogFiltersWithCheck(filter_id_1,filterName,user_id,org_id,LogSeverity[k],startTimeTSStart, startTimeTSEnd,null,null,isDefault,0);

			expectedresponse.add(expected_response);
			expectedresponse=log4SpogServer.UpdateCountForFilters(expectedresponse,actual_Logs_data,test);

			//get the specified filter for logged in user
			test.log(LogStatus.INFO, "Get specified log filter for loggedin user");
			log4SpogServer.getspecifiedLogFilterForLoggedInUser(filter_id_1, validToken, expected_response, SpogConstants.SUCCESS_GET_PUT_DELETE,null, test);

			//deleted the filters

			test.log(LogStatus.INFO,"Delete the log filter by filter ID with the valid token ");
			log4SpogServer.deleteLogFilterByFilterID(user_id,filter_id_1, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);


		}

	}					

	@DataProvider(name = "get_log_filter_valid_401",parallel=false)
	public final Object[][] getLogFilterValidParams() {
		return new Object[][] {
			// different users

			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id,"backup_full", "information",0, 0, RandomStringUtils.randomAlphanumeric(4)+"filterName", "true" ,2},

		};
	}


	//missed token and invalid user_id 

	@Test(dataProvider = "get_log_filter_valid_401")
	public void getspecifiedLogFilterForLoggedInUserTest_401_404(String organizationType,
			String organization_id,
			String validToken,
			String user_id, 
			String jobType,
			String log_severity,
			long startTimeTSStart, 
			long startTimeTSEnd,
			String filterName, 
			String isDefault,
			int count) 
	{


		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		ArrayList<HashMap<String,Object>> expectedresponse = new ArrayList<>();
		HashMap<String,Object> expected_response = new HashMap<String,Object>();

		int i;
		int responsecount = 0;
		String[] arrayoffilters= new String[count];
		long[]  log_tsArray = null;

		log4SpogServer.setToken(validToken);

		if(startTimeTSStart>0&&startTimeTSEnd>0) {

			log_tsArray = null;
		}
		String Orgin=null;
		String[] type=logType.split(",");
		int index=gen_random_index(type);

		//created filter on information and job_type is backup_full
		//String filter_id = log4SpogServer.createLogFilterwithCheck(user_id, organization_id,filterName,type[index], log_tsArray, filterjobType, log_severity,isDefault,LogmessageId,source_ID,message_data,source_name,Orgin,test);

		String filter_id = log4SpogServer.createLogFilters(user_id, organization_id, filterName, type[index], log_tsArray,  log_severity, isDefault,  test);
		test.log(LogStatus.INFO, "Compose the expected log filter for the user");					
		expected_response = log4SpogServer.composeExpectedLogFiltersWithCheck(filter_id,filterName,user_id,organization_id,log_severity,startTimeTSStart, startTimeTSEnd,null,null,isDefault,1);
		expectedresponse.add(expected_response);

		//get the specified filter for logged in user
		test.log(LogStatus.INFO, "Get specified log filter for loggedin user");
		log4SpogServer.getspecifiedLogFilterForLoggedInUser(filter_id,validToken+"123", expected_response, SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);


		//get the specified filter for logged in user
		test.log(LogStatus.INFO, "Get specified log filter for loggedin user");
		log4SpogServer.getspecifiedLogFilterForLoggedInUser(filter_id,"", expected_response, SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);

		//generated ramdom filterid id
		test.log(LogStatus.INFO, "Generated random filterid");
		String random_filter_Id=UUID.randomUUID().toString();

		//get the specified filter for logged in user
		test.log(LogStatus.INFO, "Get specified log filter for loggedin user");
		log4SpogServer.getspecifiedLogFilterForLoggedInUser( random_filter_Id,validToken, expected_response, SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.LOG_FILTER_NOT_FOUND_WITH_USER_ID, test);

		test.log(LogStatus.INFO,"Delete the log filter by filter ID with the valid token ");
		log4SpogServer.deleteLogFilterByFilterID(user_id,filter_id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		//get the specified filter for logged in user
		test.log(LogStatus.INFO, "Get specified log filter for loggedin user");
		log4SpogServer.getspecifiedLogFilterForLoggedInUser( filter_id,validToken, expected_response,  SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.LOG_FILTER_NOT_FOUND_WITH_USER_ID, test);


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
