package api.users.userfilters;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class UpdateUserFilterForLoggedInUserTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private UserSpogServer userSpogServer;
	//public int Nooftest;
	//	private ExtentReports rep;
	private ExtentTest test;

	private String site_version="1.0.0";
	private String gateway_hostname="Prasad";
	//used for test case count like passed,failed,remaining cases
	//	private SQLServerDb bqdb1;
	//	public int Nooftest;
	int passedcases=0;
	int failedcases=0;
	int skippedcases=0;
	//long creationTime;
	String buildnumber=null;
	//String BQame=null;
	//private testcasescount count1;


	//	private String runningMachine;
	//	private String buildVersion;
	private String  org_model_prefix=this.getClass().getSimpleName();

	LocalDate date = LocalDate.now();  
	LocalDate yesterday = date.minusDays(1);  
	LocalDate tomorrow = yesterday.plusDays(2);
	private TestOrgInfo ti;


	@BeforeClass
	@Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword","csrReadOnlyAdminName","csrReadOnlyAdminPassword","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword,String csrReadOnlyAdminName,String csrReadOnlyAdminPassword, String logFolder,String runningMachine,String buildVersion) throws UnknownHostException {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		rep = ExtentManager.getInstance("UpdateUserFilterForLoggedInUserTest", logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Prasad.Deverakonda";

		Nooftest=0;
		ti = new TestOrgInfo(spogServer, test);	

		//Used for creating a build number with dateformat
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date=new java.util.Date();
		this.buildVersion=buildVersion+"_"+dateFormater.format(date);

		this.BQName=this.getClass().getSimpleName();
		this.runningMachine=runningMachine;
		System.out.println("The value of hit is "+count1.isstarttimehit());


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
				        String organizationType,
			String organization_id,
			String validToken,
			String user_id,
			String filter_name,
			String search_string,
			String user_is_blocked,
			String user_status,
			String role_d, 
			String is_default,
			String update_filter_name,
			String update_search_string,
			String update_user_is_blocked,
			String update_user_status,
			String update_role_id, 
			String update_is_default,
			int count
			    }*/
	}

	@DataProvider(name = "update_user_filter_valid")
	public final Object[][] updateUserFilterValidParams() {
		return new Object[][] {

			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
				"false","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
				"true","verified",SpogConstants.DIRECT_ADMIN,"true",0},
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcs",
					"true","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
					"false","verified",SpogConstants.DIRECT_ADMIN,"true" ,0},
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", ti.direct_org1_user1_email,
						"true","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
						"false","verified",SpogConstants.DIRECT_ADMIN,"true" ,0},
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
							"true","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
							"false","verified",SpogConstants.DIRECT_ADMIN,"true",0},
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "firstname" ,
								"true","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
								"false","verified",SpogConstants.DIRECT_ADMIN,"true" ,0},
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "lastname" ,
									"true","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
									"true","verified",SpogConstants.DIRECT_ADMIN,"true",0 },


			//Get User filters for the normal msp organization

			{ "Normal_msp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null ,
										"false","verified",SpogConstants.MSP_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
										"true","verified",SpogConstants.MSP_ADMIN,"true",0 },
			{ "Normal_msp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "firstname" ,
											"false","verified",SpogConstants.MSP_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
											"true","verified",SpogConstants.MSP_ADMIN,"true",0 },
			{ "Normal_msp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "lastname" ,
												"false","verified",SpogConstants.MSP_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
												"true","verified",SpogConstants.MSP_ADMIN,"true",0 },
			{ "Normal_msp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName",ti.normal_msp1_suborg1_user1_email,
													"false","verified",SpogConstants.MSP_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
													"true","verified",SpogConstants.MSP_ADMIN,"true",0 },
			{ "Normal_msp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName","@arcserve.com",
														"false","verified",SpogConstants.MSP_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
														"true","verified",SpogConstants.MSP_ADMIN,"true",0 },	


			//Get User filters of Normal msp account admin 
			{ "Normal_msp_account_admin", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null ,
															"false","verified",SpogConstants.MSP_ACCOUNT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
															"true","verified",SpogConstants.MSP_ACCOUNT_ADMIN,"true",0 },
			{ "Normal_msp_account_admin", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "firstname" ,
																"false","verified",SpogConstants.MSP_ACCOUNT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																"true","verified",SpogConstants.MSP_ACCOUNT_ADMIN,"true",0 },
			{ "Normal_msp_account_admin", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "lastname" ,
																	"false","verified",SpogConstants.MSP_ACCOUNT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																	"true","verified",SpogConstants.MSP_ACCOUNT_ADMIN,"true",0 },
			{ "Normal_msp_account_admin", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, 
																		RandomStringUtils.randomAlphanumeric(4)+"filterName",ti.normal_msp_org1_msp_accountadmin1_email,
																		"false","verified",SpogConstants.MSP_ACCOUNT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																		"true","verified",SpogConstants.MSP_ACCOUNT_ADMIN,"true",0 },
			{ "Normal_msp_account_admin", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName","@arcserve.com",
																			"false","verified",SpogConstants.MSP_ACCOUNT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																			"true","verified",SpogConstants.MSP_ACCOUNT_ADMIN,"true",0 },	


			//Get User filters of normal sub msp Organization
			{ "NormalMsp_Suborg", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null ,
																				"false","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																				"true","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "NormalMsp_Suborg", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "firstname" ,
																					"false","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																					"true","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "NormalMsp_Suborg", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "lastname" ,
																						"false","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																						"true","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "NormalMsp_Suborg", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName",ti.normal_msp1_suborg1_user1_email,
																							"false","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																							"true","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "NormalMsp_Suborg", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName","@arcserve.com",
																								"false","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																								"true","verified",SpogConstants.DIRECT_ADMIN,"true",0 },	


			//Get User Filters for Root MSP organization
			{ "root_msp", ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null ,
																									"false","verified",SpogConstants.MSP_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																									"true","verified",SpogConstants.MSP_ADMIN,"true",0 },
			{ "root_msp", ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "firstname" ,
																										"false","verified",SpogConstants.MSP_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																										"true","verified",SpogConstants.MSP_ADMIN,"true",0 },
			{ "root_msp", ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "lastname" ,
																											"false","verified",SpogConstants.MSP_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																											"true","verified",SpogConstants.MSP_ADMIN,"true",0 },
			{ "root_msp", ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName",ti.root_msp_org1_user1_email,
																												"false","verified",SpogConstants.MSP_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", 
																												"@arcserve.com",
																												"true","verified",SpogConstants.MSP_ADMIN,"true",0 },
			{ "root_msp", ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName","@arcserve.com",
																													"false","verified",SpogConstants.MSP_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", 
																													"@arcserve.com",
																													"true","verified",SpogConstants.MSP_ADMIN,"true",0 },	

			//Get User Filters for Customer account of Root MSP Organization
			{ "Customer_account_Root_Msp", ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null ,
																														"false","verified",SpogConstants.MSP_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", 
																														"@arcserve.com",
																														"true","verified",SpogConstants.MSP_ADMIN,"true",0 },
			{ "Customer_account_Root_Msp", ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "firstname" ,
																															"false","verified",SpogConstants.MSP_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", 
																															"@arcserve.com",
																															"true","verified",SpogConstants.MSP_ADMIN,"true",0 },
			{ "Customer_account_Root_Msp", ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "lastname" ,

																																"false","verified",SpogConstants.MSP_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName"
																																, "@arcserve.com",
																																"true","verified",SpogConstants.MSP_ADMIN,"true",0 },
			{ "Customer_account_Root_Msp", ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName",ti.root_msp_org1_user1_email,

																																	"false","verified",SpogConstants.MSP_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																																	"true","verified",SpogConstants.MSP_ADMIN,"true",0 },
			{ "Customer_account_Root_Msp", ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName","@arcserve.com",

																																		"false","verified",SpogConstants.MSP_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																																		"true","verified",SpogConstants.MSP_ADMIN,"true",0 },	

			//Get User Filters for Sub MSP organization
			{ "sub_msp", ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null ,

																																			"false","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(
																																					4)+"filterName", "@arcserve.com",
																																			"true","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "sub_msp", ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "firstname" ,

																																				"false","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																																				"true","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "sub_msp", ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "lastname" ,

																																					"false","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																																					"true","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "sub_msp", ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName",ti.root_msp1_submsp1_user1_email,

																																						"false","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																																						"true","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "sub_msp", ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName","@arcserve.com",

																																							"false","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																																							"true","verified",SpogConstants.DIRECT_ADMIN,"true",0 },	
			//Get User filters for Customer account of Sub msp

			{ "Customeraccoutn_SUBMSP", ti.msp1_submsp1_sub_org1_id,ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null ,

																																								"false","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils
																																								.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																																								"true","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "sub_msp", ti.msp1_submsp1_sub_org1_id,ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "firstname" ,

																																									"false","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName",
																																									"@arcserve.com",
																																									"true","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "sub_msp", ti.msp1_submsp1_sub_org1_id,ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "lastname" ,

																																										"false","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName",
																																										"@arcserve.com",
																																										"true","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "sub_msp", ti.msp1_submsp1_sub_org1_id,ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName",ti.root_msp1_submsp1_user1_email,

																																											"false","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", 

																																											"@arcserve.com",
																																											"true","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
		};
	}

	@Test(dataProvider = "update_user_filter_valid")
	public void updateUserFilterForLoggedInUserValid(String organizationType,
			String organization_id,
			String validToken,
			String user_id,
			String filter_name,
			String search_string,
			String user_is_blocked,
			String user_status,
			String role_d, 
			String is_default,
			String update_filter_name,
			String update_search_string,
			String update_user_is_blocked,
			String update_user_status,
			String update_role_id, 
			String update_is_default,
			int count) 
	{
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		String filter_Id = null;
		ArrayList<HashMap<String,Object>> expectedresponse = new ArrayList<>();
		HashMap<String,Object> expected_response = new HashMap<String,Object>();


		userSpogServer.setToken(validToken);
		test.log(LogStatus.INFO, "Create user filter in org of type "+organizationType);
		Response response = userSpogServer.createUserFilterForLoggedInUser(filter_name, search_string, user_is_blocked, user_status, role_d, is_default, test);

		test.log(LogStatus.INFO, "Validate the response");
		filter_Id = userSpogServer.verifyUserFilters(response, filter_name, search_string, user_is_blocked, user_status,  role_d, is_default, count, SpogConstants.SUCCESS_POST, null, test);

		test.log(LogStatus.INFO, "update user filter in org of type "+organizationType);
		response = userSpogServer.updateUserFilterForLoggedInUser(filter_Id,update_filter_name, update_search_string, update_user_is_blocked, update_user_status,  update_role_id, update_is_default, test);

		test.log(LogStatus.INFO, "Validate the response");
		userSpogServer.verifyUserFilters(response, update_filter_name, update_search_string, update_user_is_blocked, update_user_status, update_role_id, update_is_default, count, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);


	}

	@DataProvider(name = "create_user_filter_401")
	public final Object[][] createUserFilter_401() {
		return new Object[][] {

			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
				"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "sub", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null ,
					"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
		};
	}
	@Test(dataProvider = "create_user_filter_401")
	public void updateUserFilterForLoggedInUser_401(String organizationType,
			String organization_id,
			String validToken,
			String user_id,
			String update_search_string,
			String update_first_name,
			String update_user_is_blocked,
			String update_user_status,
			String update_role_id, 
			String update_is_default,
			int count) 
	{
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		String filter_Id = null;
		ArrayList<HashMap<String,Object>> expectedresponse = new ArrayList<>();
		HashMap<String,Object> expected_response = new HashMap<String,Object>();


		userSpogServer.setToken("");
		test.log(LogStatus.INFO, "Create user filter in org of type "+organizationType + " using missing token");
		Response response = userSpogServer.updateUserFilterForLoggedInUser(UUID.randomUUID().toString(),update_search_string, update_first_name, update_user_is_blocked, update_user_status, update_role_id, update_is_default, test);
		//Response response = userSpogServer.createUserFilterForLoggedInUser(filter_name, first_name, user_is_blocked, user_status, last_name, email, role_id, is_default,test);

		test.log(LogStatus.INFO, "Validate the response");
		userSpogServer.verifyUserFilters(response, update_search_string, update_first_name, update_user_is_blocked, update_user_status, update_role_id, update_is_default, count, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);

		test.log(LogStatus.INFO, "Create user filter in org of type "+organizationType + " using invalid token");
		userSpogServer.setToken(validToken+"J");
		response = userSpogServer.updateUserFilterForLoggedInUser(UUID.randomUUID().toString(),update_search_string, update_first_name, update_user_is_blocked, update_user_status, update_role_id, update_is_default, test);
		//response = userSpogServer.createUserFilterForLoggedInUser(filter_name, first_name, user_is_blocked, user_status, last_name, email, role_id, is_default, test);

		test.log(LogStatus.INFO, "Validate the response");
		userSpogServer.verifyUserFilters(response, update_search_string, update_first_name, update_user_is_blocked, update_user_status,  update_role_id, update_is_default, count, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT,test);		
	}


	@DataProvider(name = "UpdateUserFilres_400")
	public final Object[][] createUserFilter_400() {
		return new Object[][] {
			//Direct Org
			{ "direct_searchString", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName","@arcserve.com",
							"false","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "12",
							"true","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "direct_status", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName","@arcserve.com",
								"false","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", null,
								"123","verified",SpogConstants.DIRECT_ADMIN,"true",0 },


			//Get User filters for the normal msp organization

			{ "Normal_msp_searchString", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName","@arcserve.com",
											"false","verified",SpogConstants.MSP_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "12",
											"true","verified",SpogConstants.MSP_ADMIN,"true",0 },
			{ "Normal_msp_status", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName","@arcserve.com",
												"false","verified",SpogConstants.MSP_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", null,
												"123","verified",SpogConstants.MSP_ADMIN,"true",0 },


			//Get User filters of Normal msp account admin 
			{ "Normal_msp_account_admin_searchString", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName","@arcserve.com",
															"false","verified",SpogConstants.MSP_ACCOUNT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "12" ,
															"false","verified",SpogConstants.MSP_ACCOUNT_ADMIN,"true",0 },
			{ "Normal_msp_account_admin_status", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName","@arcserve.com",
																"false","verified",SpogConstants.MSP_ACCOUNT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", null ,
																"123","verified",SpogConstants.MSP_ACCOUNT_ADMIN,"true",0 },


			//Get User filters of normal sub msp Organization
			{ "NormalMsp_Suborg_searchString", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName","@arcserve.com",
																			"false","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "12",
																			"true","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "NormalMsp_Suborg_status", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName","@arcserve.com",
																				"false","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", null,
																				"123","verified",SpogConstants.DIRECT_ADMIN,"true",0 },


			//Get User Filters for Root MSP organization
			{ "root_msp_searchString", ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName","@arcserve.com",
																							"false","verified",SpogConstants.MSP_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "12",
																							"true","verified",SpogConstants.MSP_ADMIN,"true",0 },
			{ "root_msp_status", ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName","@arcserve.com",
																								"false","verified",SpogConstants.MSP_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", null,
																								"123","verified",SpogConstants.MSP_ADMIN,"true",0 },

			//Get User Filters for Customer account of Root MSP Organization
			{ "Customer_account_Root_Msp_searchString", ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName","@arcserve.com",
																											"false","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "12",
																											"true","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "Customer_account_Root_Msp_status", ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName","@arcserve.com",
																												"false","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName",null,
																												"123","verified",SpogConstants.DIRECT_ADMIN,"true",0 },

			//Get User Filters for Sub MSP organization
			{ "sub_msp_searchString", ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName","@arcserve.com",
																															"false","verified",SpogConstants.MSP_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "12",
																															"true","verified",SpogConstants.MSP_ADMIN,"true",0 },
			{ "sub_msp_status", ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName","@arcserve.com",
																																"false","verified",SpogConstants.MSP_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", null,
																																"123","verified",SpogConstants.MSP_ADMIN,"true",0 },
			
			//Get User filters for Customer account of Sub msp

			{ "Customeraccoutn_SUBMSP_searchString", ti.msp1_submsp1_sub_org1_id,ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName","arcserve.com",
																																			"false","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "12",
																																			"true","verified",SpogConstants.DIRECT_ADMIN,"true",0 },

			{ "Customeraccoutn_status", ti.msp1_submsp1_sub_org1_id,ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName","arcserve.com",
																																			"false","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@12",
																																			"123","verified",SpogConstants.DIRECT_ADMIN,"true",0 },

		};
	}
	@Test(dataProvider = "UpdateUserFilres_400")
	public void updateUserFilterForLoggedInUser_400(String organizationType,
			String organization_id,
			String validToken,
			String user_id,
			String filter_name,
			String search_string,
			String user_is_blocked,
			String user_status,
			String role_id, 
			String is_default,
			String update_filter_name,
			String update_search_string,
			String update_user_is_blocked,
			String update_user_status, 
			String update_role_id, 
			String update_is_default,
			int count) 
	{
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		String filter_Id = null;
		ArrayList<HashMap<String,Object>> expectedresponse = new ArrayList<>();
		HashMap<String,Object> expected_response = new HashMap<String,Object>();


		userSpogServer.setToken(validToken);
		test.log(LogStatus.INFO, "Create user filter in org of type "+organizationType + " using valid params");
		Response response = userSpogServer.createUserFilterForLoggedInUser(filter_name, search_string, user_is_blocked, user_status, role_id, is_default, test);

		test.log(LogStatus.INFO, "Validate the response");
		filter_Id = userSpogServer.verifyUserFilters(response, filter_name, search_string, user_is_blocked, user_status, role_id, is_default, count, SpogConstants.SUCCESS_POST, null, test);

		test.log(LogStatus.INFO, "update user filter in org of type "+organizationType);
		response = userSpogServer.updateUserFilterForLoggedInUser(filter_Id,update_filter_name, update_search_string, update_user_is_blocked, update_user_status, update_role_id, update_is_default, test);

		test.log(LogStatus.INFO, "Validate the response");
		if(organizationType.contains("-blocked")) {
			userSpogServer.verifyUserFilters(response, update_filter_name, update_search_string, update_user_is_blocked, update_user_status,  
					update_role_id, update_is_default, count, SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.INVALID_PARAMETER, test);

		}else if(organizationType.contains("_searchString")) {
			userSpogServer.verifyUserFilters(response, update_filter_name, update_search_string, update_user_is_blocked, update_user_status,  
					update_role_id, update_is_default, count, SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.SEARCH_STRING_MIN_THREE, test);

		}else if(organizationType.contains("_status")) {
			userSpogServer.verifyUserFilters(response, update_filter_name, update_search_string, update_user_is_blocked, update_user_status,  
					update_role_id, update_is_default, count, SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.INVALID_PARAMETER, test);
		}
		/*else {
			userSpogServer.verifyUserFilters(response, update_filter_name, update_search_string, update_user_is_blocked, update_user_status,  
					update_role_id, update_is_default, count, SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.SEARCH_STRING_MIN_THREE, test);
		}
		*/
	}


	@DataProvider(name = "create_user_filter_404")
	public final Object[][] createUserFilter_404() {
		return new Object[][] {
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
				"false","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
				"true","verified",SpogConstants.DIRECT_ADMIN,"true",0},
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcs",
					"true","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
					"false","verified",SpogConstants.DIRECT_ADMIN,"true" ,0},
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", ti.direct_org1_user1_email,
						"true","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
						"false","verified",SpogConstants.DIRECT_ADMIN,"true" ,0},
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
							"true","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
							"false","verified",SpogConstants.DIRECT_ADMIN,"true",0},
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "firstname" ,
								"true","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
								"false","verified",SpogConstants.DIRECT_ADMIN,"true" ,0},
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "lastname" ,
									"true","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
									"true","verified",SpogConstants.DIRECT_ADMIN,"true",0 },


			//Get User filters for the normal msp organization

			{ "Normal_msp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null ,
										"false","verified",SpogConstants.MSP_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
										"true","verified",SpogConstants.MSP_ADMIN,"true",0 },
			{ "Normal_msp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "firstname" ,
											"false","verified",SpogConstants.MSP_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
											"true","verified",SpogConstants.MSP_ADMIN,"true",0 },
			{ "Normal_msp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "lastname" ,
												"false","verified",SpogConstants.MSP_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
												"true","verified",SpogConstants.MSP_ADMIN,"true",0 },
			{ "Normal_msp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName",ti.normal_msp1_suborg1_user1_email,
													"false","verified",SpogConstants.MSP_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
													"true","verified",SpogConstants.MSP_ADMIN,"true",0 },
			{ "Normal_msp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName","@arcserve.com",
														"false","verified",SpogConstants.MSP_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
														"true","verified",SpogConstants.MSP_ADMIN,"true",0 },	


			//Get User filters of Normal msp account admin 
			{ "Normal_msp_account_admin", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null ,
															"false","verified",SpogConstants.MSP_ACCOUNT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
															"true","verified",SpogConstants.MSP_ACCOUNT_ADMIN,"true",0 },
			{ "Normal_msp_account_admin", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "firstname" ,
																"false","verified",SpogConstants.MSP_ACCOUNT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																"true","verified",SpogConstants.MSP_ACCOUNT_ADMIN,"true",0 },
			{ "Normal_msp_account_admin", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "lastname" ,
																	"false","verified",SpogConstants.MSP_ACCOUNT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																	"true","verified",SpogConstants.MSP_ACCOUNT_ADMIN,"true",0 },
			{ "Normal_msp_account_admin", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, 
																		RandomStringUtils.randomAlphanumeric(4)+"filterName",ti.normal_msp_org1_msp_accountadmin1_email,
																		"false","verified",SpogConstants.MSP_ACCOUNT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																		"true","verified",SpogConstants.MSP_ACCOUNT_ADMIN,"true",0 },
			{ "Normal_msp_account_admin", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName","@arcserve.com",
																			"false","verified",SpogConstants.MSP_ACCOUNT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																			"true","verified",SpogConstants.MSP_ACCOUNT_ADMIN,"true",0 },	


			//Get User filters of normal sub msp Organization
			{ "NormalMsp_Suborg", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null ,
																				"false","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																				"true","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "NormalMsp_Suborg", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "firstname" ,
																					"false","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																					"true","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "NormalMsp_Suborg", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "lastname" ,
																						"false","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																						"true","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "NormalMsp_Suborg", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName",ti.normal_msp1_suborg1_user1_email,
																							"false","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																							"true","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "NormalMsp_Suborg", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName","@arcserve.com",
																								"false","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																								"true","verified",SpogConstants.DIRECT_ADMIN,"true",0 },	


			//Get User Filters for Root MSP organization
			{ "root_msp", ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null ,
																									"false","verified",SpogConstants.MSP_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																									"true","verified",SpogConstants.MSP_ADMIN,"true",0 },
			{ "root_msp", ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "firstname" ,
																										"false","verified",SpogConstants.MSP_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																										"true","verified",SpogConstants.MSP_ADMIN,"true",0 },
			{ "root_msp", ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "lastname" ,
																											"false","verified",SpogConstants.MSP_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																											"true","verified",SpogConstants.MSP_ADMIN,"true",0 },
			{ "root_msp", ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName",ti.root_msp_org1_user1_email,
																												"false","verified",SpogConstants.MSP_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", 
																												"@arcserve.com",
																												"true","verified",SpogConstants.MSP_ADMIN,"true",0 },
			{ "root_msp", ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName","@arcserve.com",
																													"false","verified",SpogConstants.MSP_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", 
																													"@arcserve.com",
																													"true","verified",SpogConstants.MSP_ADMIN,"true",0 },	

			//Get User Filters for Customer account of Root MSP Organization
			{ "Customer_account_Root_Msp", ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null ,
																														"false","verified",SpogConstants.MSP_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", 
																														"@arcserve.com",
																														"true","verified",SpogConstants.MSP_ADMIN,"true",0 },
			{ "Customer_account_Root_Msp", ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "firstname" ,
																															"false","verified",SpogConstants.MSP_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", 
																															"@arcserve.com",
																															"true","verified",SpogConstants.MSP_ADMIN,"true",0 },
			{ "Customer_account_Root_Msp", ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "lastname" ,

																																"false","verified",SpogConstants.MSP_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName"
																																, "@arcserve.com",
																																"true","verified",SpogConstants.MSP_ADMIN,"true",0 },
			{ "Customer_account_Root_Msp", ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName",ti.root_msp_org1_user1_email,

																																	"false","verified",SpogConstants.MSP_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																																	"true","verified",SpogConstants.MSP_ADMIN,"true",0 },
			{ "Customer_account_Root_Msp", ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName","@arcserve.com",

																																		"false","verified",SpogConstants.MSP_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																																		"true","verified",SpogConstants.MSP_ADMIN,"true",0 },	

			//Get User Filters for Sub MSP organization
			{ "sub_msp", ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null ,

																																			"false","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(
																																					4)+"filterName", "@arcserve.com",
																																			"true","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "sub_msp", ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "firstname" ,

																																				"false","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																																				"true","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "sub_msp", ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "lastname" ,

																																					"false","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																																					"true","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "sub_msp", ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName",ti.root_msp1_submsp1_user1_email,

																																						"false","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																																						"true","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "sub_msp", ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName","@arcserve.com",

																																							"false","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																																							"true","verified",SpogConstants.DIRECT_ADMIN,"true",0 },	
			//Get User filters for Customer account of Sub msp

			{ "Customeraccoutn_SUBMSP", ti.msp1_submsp1_sub_org1_id,ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null ,

																																								"false","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils
																																								.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																																								"true","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "sub_msp", ti.msp1_submsp1_sub_org1_id,ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "firstname" ,

																																									"false","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName",
																																									"@arcserve.com",
																																									"true","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "sub_msp", ti.msp1_submsp1_sub_org1_id,ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "lastname" ,

																																										"false","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName",
																																										"@arcserve.com",
																																										"true","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "sub_msp", ti.msp1_submsp1_sub_org1_id,ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName",ti.root_msp1_submsp1_user1_email,

																																											"false","verified",SpogConstants.DIRECT_ADMIN,"true",RandomStringUtils.randomAlphanumeric(4)+"filterName", 

																																											"@arcserve.com",
																																											"true","verified",SpogConstants.DIRECT_ADMIN,"true",0 },

		};
	}
	@Test(dataProvider = "create_user_filter_404")
	public void updateUserFilterForLoggedInUser_404(String organizationType,
			String organization_id,
			String validToken,
			String user_id,
			String filter_name,
			String search_string,
			String user_is_blocked,
			String user_status, 
			String role_id, 
			String is_default,
			String update_filter_name,
			String update_search_string,
			String update_user_is_blocked,
			String update_user_status,
			String update_role_id, 
			String update_is_default,
			int count) 
	{
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		String filter_Id = null;
		ArrayList<HashMap<String,Object>> expectedresponse = new ArrayList<>();
		HashMap<String,Object> expected_response = new HashMap<String,Object>();


		userSpogServer.setToken(validToken);
		test.log(LogStatus.INFO, "Create user filter in org of type "+organizationType + " using valid params");
		Response response = userSpogServer.createUserFilterForLoggedInUser(filter_name, search_string, user_is_blocked, user_status, role_id, is_default, test);

		test.log(LogStatus.INFO, "Validate the response");
		filter_Id = userSpogServer.verifyUserFilters(response, filter_name, search_string, user_is_blocked, user_status, role_id, is_default, count, SpogConstants.SUCCESS_POST, null, test);

		test.log(LogStatus.INFO, "update user filter in org of type "+organizationType);
		response = userSpogServer.updateUserFilterForLoggedInUser(UUID.randomUUID().toString(),update_filter_name, update_search_string, update_user_is_blocked, update_user_status, update_role_id, update_is_default, test);

		test.log(LogStatus.INFO, "Validate the response");
		userSpogServer.verifyUserFilters(response, update_filter_name, update_search_string, update_user_is_blocked, update_user_status,  
				update_role_id, update_is_default, count, SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.USER_FILTER_NOT_FOUND_WITH_USER_ID, test);

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

	}*/


	/****************************************************************Generic Function***************************************************************************/
	public HashMap<String,Object> composeExpectedUserFilter(String filter_id,
			String filter_name,
			String user_id,
			String organization_id,
			String first_name, 
			String user_is_blocked, 
			String user_status, 
			String last_name, 
			String email, 
			String role_id, 
			String is_defaul,
			int count) {
		HashMap<String,Object> expected_response = new HashMap<>();
		expected_response.put("filter_id", filter_id);
		expected_response.put("filter_name", filter_name);
		expected_response.put("user_id", user_id);
		expected_response.put("organization_id", organization_id);
		expected_response.put("first_name",first_name);
		expected_response.put("blocked",user_is_blocked);
		expected_response.put("status",user_status);
		expected_response.put("last_name",last_name);
		expected_response.put("email",email);
		expected_response.put("role_id",role_id);
		expected_response.put("is_defaul",is_defaul);
		expected_response.put("count",count);
		return expected_response;
	}

}
