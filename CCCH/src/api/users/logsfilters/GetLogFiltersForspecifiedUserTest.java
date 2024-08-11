package api.users.logsfilters;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
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

public class GetLogFiltersForspecifiedUserTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private Log4SPOGServer log4SpogServer;
	private UserSpogServer userSpogServer;
	private TestOrgInfo ti;
	private ExtentTest test;
	private SPOGDestinationServer spogDestinationServer;

	//this is for update portal, each testng class is taken as BQ set
	//private testcasescount count1;
	int passedcases=0;
	int failedcases=0;
	int skippedcases=0;
	public String logType="last_24_hours,last_7_days,last_2_weeks,last_1_month,custom";
	public String JobSeverity= "success,warning,error,critical,information";

	private Random r;
	long current = ZonedDateTime.now().toInstant().toEpochMilli()/1000L;
	long yesterday = ZonedDateTime.now().minusDays(1).toInstant().toEpochMilli()/1000L;
	long tomorrow = ZonedDateTime.now().plusDays(1).toInstant().toEpochMilli()/1000L;
	
	ArrayList<Object> filterId= new ArrayList<>();
	@BeforeClass
	@Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword","csrReadOnlyAdminName","csrReadOnlyAdminPassword","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword,String csrReadOnlyAdminName,String csrReadOnlyAdminPassword, String logFolder,String runningMachine,String buildVersion) throws UnknownHostException {
		spogServer = new SPOGServer(baseURI, port);
		r = new Random();
		spogServer = new SPOGServer(baseURI, port);
		log4SpogServer=new Log4SPOGServer(baseURI,port);
		gatewayServer =new GatewayServer(baseURI,port);
		spogDestinationServer=new SPOGDestinationServer(baseURI,port);
		userSpogServer = new UserSpogServer(baseURI, port);
		rep = ExtentManager.getInstance("GetLogFilterForspecifiedUserTest",logFolder);
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
			String filter1=	(String) log4SpogServer.createLogFilters(user_id, org_id, filterName, type[index], log_tsArray, LogSeverity[k], isDefault,  test);
			test.log(LogStatus.INFO, "Compose the expected log filter for the user");					
			expected_response = log4SpogServer.composeExpectedLogFiltersWithCheck(filter1,filterName,user_id,org_id,LogSeverity[k],startTimeTSStart, startTimeTSEnd,null,null,isDefault,0);
			filterId.add(filter1);

			expectedresponse = new ArrayList<>();
			expectedresponse.add(expected_response);

			//call the API of getLogFilters by user id 		
			log4SpogServer.getlogFiltersForUserwithCheck(user_id,validToken,expectedresponse, SpogConstants.SUCCESS_GET_PUT_DELETE,null, test);

			test.log(LogStatus.INFO,"Delete the log filter by filter ID with the valid token ");
			log4SpogServer.deleteLogFilterByFilterID(user_id,filter1, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		}

	}
	@DataProvider(name = "GetLogFiltersForSpecifiedUser_401")
	public final Object[][] GetLogFilterValidParams1() {
		return new Object[][] {
			{ "direct",ti.direct_org1_user1_token,ti.direct_org1_user1_id},

		};
	}

	@Test (dataProvider = "GetLogFiltersForSpecifiedUser_401")
	public void GetLogFiltersForSpecifiedUser_401(
			String organizationType,
			String validtoken,
			String user_id

			)
	{
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+organizationType);

		//invalid token
		//test.log(LogStatus.INFO, "Get log filters for a specified user id  with other invalid token");
		log4SpogServer.getlogFiltersForUserwithCheck(user_id,validtoken+"123",new ArrayList<>() , SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

		//missed token

		test.log(LogStatus.INFO, "Get log filters for a specified user id  with other invalid token");
		log4SpogServer.getlogFiltersForUserwithCheck(user_id,"", new ArrayList<>() , SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);

		//generated random user id
		test.log(LogStatus.INFO, "Generated random user id");
		String random_user_id=UUID.randomUUID().toString();


		test.log(LogStatus.INFO, "Get specified log filter for specified user with valid token ");
		log4SpogServer.getlogFiltersForUserwithCheck(random_user_id,validtoken, new ArrayList<>() , SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.USER_ID_DOESNOT_EXIST, test);

	}


	@DataProvider(name = "GetLogFiltersForSpecifiedUser_403")
	public final Object[][] GetLogFilterValidParams2() {
		return new Object[][] {
			//posted jobs  to direct_user_id :job_type =backup_full and posted logs on :severity is information
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
			{"Get log Filters for the Root MSP  organziation with Customer account of Root MSP Organization user",ti.root_msp_org1_user1_token,ti.root_msp2_suborg1_user1_token, ti.root_msp_org1_id,  ti.root_msp_org1_user1_id, "backup_full", "information",RandomStringUtils.randomAlphanumeric(4)+"filterName", "false",2},

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

	@Test (dataProvider = "GetLogFiltersForSpecifiedUser_403")
	public void GetLogFiltersForSpecifiedUser_403(
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

		log4SpogServer.setToken(anotherorgtoken);
		test.log(LogStatus.INFO,"The value of the Token is:"+anotherorgtoken);

		test.log(LogStatus.INFO, "Get job filters for a specified user id  with other sub/direct/msp");
		log4SpogServer.getlogFiltersForUserwithCheck(user_id,anotherorgtoken,new ArrayList<>(), SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY, test);



	}



	public int gen_random_index(String[] job_status) {
		Random generator = new Random();
		int randomindx = generator.nextInt(job_status.length);

		return randomindx;
	}
	//This  is used to know the results regarding the total number of failed passed skipped test cases 
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
