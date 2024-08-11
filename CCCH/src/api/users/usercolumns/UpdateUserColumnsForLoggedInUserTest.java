package api.users.usercolumns;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import org.apache.commons.lang.RandomStringUtils;
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

public class UpdateUserColumnsForLoggedInUserTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private TestOrgInfo ti;
	private GatewayServer gatewayServer;
	private UserSpogServer userSpogServer;
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
	
	ArrayList<String> columnIdList = new ArrayList<String>();
	ArrayList<HashMap<String,Object>> columnsHeadContent = new ArrayList<HashMap<String,Object>>();
	
	private String  org_model_prefix=this.getClass().getSimpleName();
	
	@BeforeClass
	@Parameters({ "baseURI", "port","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String logFolder,String runningMachine,String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		rep = ExtentManager.getInstance("UpdateUserColumnsForLoggedInUserTest", logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Rakesh.Chalamala";
		
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
		
		userSpogServer.setToken(ti.csr_token);
		Response response = userSpogServer.getUsersColumns(SpogConstants.SUCCESS_GET_PUT_DELETE, test); 

		columnsHeadContent = response.then().extract().path("data");
		int length = columnsHeadContent.size();
		for (int i = 0; i < length; i++) {
			HashMap<String,Object> HeadContent = columnsHeadContent.get(i);
			columnIdList.add((String) HeadContent.get("column_id"));
		}
	}
	
	@DataProvider(name="updateUserColumnsForLoggeInUser_201")
	public Object[][] updateUserColumnsForLoggeInUser_201(){
		
		return new Object[][] {
			//csr_readonly user token
			{"csr", ti.csr_token, new String[]{"true", "false","none"}, new String[]{"true", "false","none"},
				new String[]{"true", "false","none"}, new String[]{"2","3","1","4","7","5","6"}, new String[]{"true"}, new String[]{"4","7","5","6","1"},4,2},
			{"csr", ti.csr_readonly_token, new String[]{"true", "false","none"}, new String[]{"true", "false","none"},
					new String[]{"true", "false","none"}, new String[]{"2","3","1","4","7","5","6"}, new String[]{"true"}, new String[]{"4","7","5","6","1"},4,2},
			
			{"direct", ti.direct_org1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
				new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},new String[] { "true"},new String[]{"4","7","5","6","1"},4,2},
			{"msp", ti.root_msp_org1_user1_token,  new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
				new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},new String[] { "false"},new String[]{"1","6","3"},4,3},
			{"submsp", ti.root_msp1_submsp1_user1_token,  new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},new String[] { "false"},new String[]{"1","6","3"},4,3},
			{"suborg", ti.root_msp1_suborg1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},new String[] { "true"},new String[]{"7","5","6"},4,1},
			{"submspsuborg", ti.msp1_submsp1_suborg1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
						new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},new String[] { "true"},new String[]{"7","5","6"},4,1},
			{"suborg-mspAccAdminT", ti.root_msp_org1_msp_accountadmin1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
						new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},new String[] { "true"},new String[]{"7","5","6"},4,1},
			{"submspsuborg-mspAccAdminT", ti.root_msp1_submsp1_account_admin_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
							new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},new String[] { "true"},new String[]{"7","5","6"},4,1},
		};
	}

	@Test(dataProvider="updateUserColumnsForLoggeInUser_201",enabled=true)
	public void updateUserColumnsForLoggedInUserTest_201(String organizationType,
													String validToken,
													String[] sort,
													String[] filter,
													String[] visible,
													String[] orderId,
													String[] updatedVisbility,
													String[] updatedOrderId,
													int noofcolumnstobecreated,
													int noofcolumnstobeupdated
													) {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		
		test.assignAuthor("Rakesh.Chalamala");
		userSpogServer.setToken(validToken);
		
		ArrayList<HashMap<String,Object>> expected_columns = new ArrayList<>();
		ArrayList<HashMap<String, Object>> expected_updated_columns = new ArrayList<>();
		HashMap<String,Object> temp = new HashMap<>() ;
		HashMap<String, Object> temp1 = new HashMap<>();

		test.log(LogStatus.INFO, "Compose the user columns as per the user request");
		for(int i=0;i<noofcolumnstobecreated ;i++)
		{
			int index1 = gen_random_index(sort);
			int index2 = gen_random_index(filter);
			int index3 = gen_random_index(visible);
			//int index4 = gen_random_index(orderId);
			
			temp = userSpogServer.composeUser_Column(columnIdList.get(i), sort[index1],filter[index2],visible[index3],orderId[i]);
			expected_columns.add(temp);
		}
		test.log(LogStatus.INFO, "Create user columns for loggdin user in org : "+ organizationType);
		Response response = userSpogServer.createUserColumnsForLoggedInUser(validToken, expected_columns, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		for(int i=0;i<noofcolumnstobeupdated ;i++)
		{
			int index3 = gen_random_index(visible);
			//int index4 = gen_random_index(orderId);

			temp1 = userSpogServer.composeUser_Column(columnIdList.get(i),null,null,visible[index3],updatedOrderId[i]);
			expected_updated_columns.add(temp1);
		}

		test.log(LogStatus.INFO, "Update user columns for loggdin User in org : "+organizationType);
		userSpogServer.updateUserColumnsForLoggedInUser(validToken, expected_updated_columns, columnsHeadContent, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		test.log(LogStatus.INFO, "Delete user columns for loggdin user in org : " + organizationType);
		userSpogServer.deleteUserColumnsForLoggedInUserWithCheck(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
	}

	@DataProvider(name="UpdateUserColumns_400")
	public final Object[][] UpdateUserColumns_400() {

		return new Object[][] {
			// different users
			{ "direct-noColId", ti.direct_org1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
				new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},4},
			{ "direct-invalidOrderId_0", ti.direct_org1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},2},
			{ "direct-invalidOrderId_1", ti.direct_org1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
						new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},2},
			{ "direct-moreOrderIds", ti.direct_org1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
							new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},7},

			{ "msp-noColId", ti.root_msp_org1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
								new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},4},
			{ "msp-invalidOrderId_0", ti.root_msp_org1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
									new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},2},
			{ "msp-invalidOrderId_1", ti.root_msp_org1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
										new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},2},
			{ "msp-moreOrderIds", ti.root_msp_org1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
											new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},7},
			
			{ "submsp-noColId", ti.root_msp1_submsp1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
												new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},4},
			{ "submsp-invalidOrderId_0", ti.root_msp1_submsp1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
													new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},2},
			{ "submsp-invalidOrderId_1", ti.root_msp1_submsp1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
														new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},2},
			{ "submsp-moreOrderIds", ti.root_msp1_submsp1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
															new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},7},

			{ "suborg-noColId", ti.root_msp1_suborg1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
												new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},4},
			{ "suborg-invalidOrderId_0", ti.root_msp1_suborg1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
													new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},2},
			{ "suborg-invalidOrderId_1", ti.root_msp1_suborg1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
														new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},2},
			{ "suborg-moreOrderIds", ti.root_msp1_suborg1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
															new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},7},

			{ "submspsuborg-noColId", ti.msp1_submsp1_suborg1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
																new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},4},
			{ "submspsuborg-invalidOrderId_0", ti.msp1_submsp1_suborg1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
																	new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},2},
			{ "submspsuborg-invalidOrderId_1", ti.msp1_submsp1_suborg1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
																		new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},2},
			{ "submspsuborg-moreOrderIds", ti.msp1_submsp1_suborg1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
																			new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},7},

			{ "mspAccAdmin-noColId", ti.root_msp_org1_msp_accountadmin1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
																new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},4},
			{ "mspAccAdmin-invalidOrderId_0", ti.root_msp_org1_msp_accountadmin1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
																	new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},2},
			{ "mspAccAdmin-invalidOrderId_1", ti.root_msp_org1_msp_accountadmin1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
																		new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},2},
			{ "mspAccAdmin-moreOrderIds", ti.root_msp_org1_msp_accountadmin1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
																			new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},7},

			{ "submspAccAdmin-noColId", ti.root_msp1_submsp1_account_admin_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
																				new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},4},
			{ "submspAccAdmin-invalidOrderId_0", ti.root_msp1_submsp1_account_admin_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
																					new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},2},
			{ "submspAccAdmin-invalidOrderId_1", ti.root_msp1_submsp1_account_admin_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
																						new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},2},
			{ "submspAccAdmin-moreOrderIds", ti.root_msp1_submsp1_account_admin_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
																							new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},7},

			{ "csr-noColId", ti.csr_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
																				new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},4},
			{ "csr-invalidOrderId_0", ti.csr_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
																					new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},2},
			{ "csr-invalidOrderId_1", ti.csr_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
																						new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},2},
			{ "csr-moreOrderIds", ti.csr_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
																							new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},7},

			{ "csr_readonly-noColId", ti.csr_readonly_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
																								new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},4},
			{ "csr_readonly-invalidOrderId_0", ti.csr_readonly_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
																									new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},2},
			{ "csr_readonly-invalidOrderId_1", ti.csr_readonly_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
																										new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},2},
			{ "csr_readonly-moreOrderIds", ti.csr_readonly_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
																											new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},7},
		
		};
	}
	@Test(dataProvider="UpdateUserColumns_400",enabled=true)
	public void updateUserColumnsForLoggeInUserTest_400(String organizationType,
			  												String validToken,
			  												String[] sort,
			  												String[] filter,
			  												String[] visible,
			  												String[] orderId,
			  												int noofcolstocreate
			  												) {
		
			String[] updateOrderId_0 = new String[]{"0","2","3"};
			String[] updateOrderId_1 = new String[]{"-1","3","5"};
			String[] moreOrderIds = new String[]{"2","13","1","10","7","5","6","9","4","11","12","3"};
			
		  	SpogMessageCode expectedErrorMessage;
			test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
			
			test.assignAuthor("Rakesh.Chalamala");
			userSpogServer.setToken(validToken);
			
			
			ArrayList<HashMap<String,Object>> expected_columns = new ArrayList<>();
			HashMap<String,Object> temp = new HashMap<>() ;
			
			ArrayList<HashMap<String,Object>> expected_columns1 = new ArrayList<>();
			HashMap<String,Object> temp1 = new HashMap<>() ;

			for(int i=0;i<noofcolstocreate ;i++)
			{
				int index1 = gen_random_index(sort);
				int index2 = gen_random_index(filter);
				int index3 = gen_random_index(visible);
//				int index4 = gen_random_index(orderId);
				
				temp = userSpogServer.composeUser_Column(columnIdList.get(i), sort[index1], filter[index2], visible[index3], orderId[i]);
				
				expected_columns.add(temp);
			}

			test.log(LogStatus.INFO, "Create user columns for loggdin user in the org: "+organizationType);
			Response response= userSpogServer.createUserColumnsForLoggedInUser(validToken, expected_columns, test);
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);
			
			for(int i=0;i<noofcolstocreate ;i++)
			{
				int index1 = gen_random_index(sort);
				int index2 = gen_random_index(filter);
				int index3 = gen_random_index(visible);
//				int index4 = gen_random_index(orderId);
				if(organizationType.contains("-noColId"))
				{
					temp1 = userSpogServer.composeUser_Column("", sort[index1], filter[index2], visible[index3], orderId[i]);
				}
				else if(organizationType.contains("-invalidOrderId_0")) {
					temp1 = userSpogServer.composeUser_Column(columnIdList.get(i), sort[index1], filter[index2], visible[index3], updateOrderId_0[i]);
				}
				else if(organizationType.contains("-invalidOrderId_1")) {
					temp1 = userSpogServer.composeUser_Column(columnIdList.get(i), sort[index1], filter[index2], visible[index3], updateOrderId_1[i]);
				}
				else if(organizationType.contains("-moreOrderIds"))
				{
					temp1 = userSpogServer.composeUser_Column(columnIdList.get(i), sort[index1], filter[index2], visible[index3], moreOrderIds[i]);
				}
				else {
					temp1 = userSpogServer.composeUser_Column(columnIdList.get(i), sort[index1], filter[index2], visible[index3], orderId[i]);
				}
				expected_columns1.add(temp1);
			}
			
			if(organizationType.contains("-sameorderid")) {
				expectedErrorMessage = SpogMessageCode.ORDER_ID_ALREADY_EXIST;
			}
			else if (organizationType.contains("-sameColId")) {
				expectedErrorMessage = SpogMessageCode.COLUMN_ID_ALREADY_EXIST;
			}
			else if (organizationType.contains("-invalidOrderId")){  
				expectedErrorMessage = SpogMessageCode.ORDER_ID_ATLEAST_1;
			}
			else if(organizationType.contains("-moreOrderIds")){
				expectedErrorMessage = SpogMessageCode.ORDER_ID_SHOULD_BE_LESS_THAN_MAX_COUNT;
			}
			else {
				expectedErrorMessage = SpogMessageCode.COLUMN_CANNOT_BLANK;
			}
			
			test.log(LogStatus.INFO, "Update the user columns created for loggdin user in org: "+organizationType);
			userSpogServer.updateUserColumnsForLoggedInUser(validToken, expected_columns1, columnsHeadContent, SpogConstants.REQUIRED_INFO_NOT_EXIST, expectedErrorMessage, test);
						
			test.log(LogStatus.INFO, "Delete user columns for loggdin user in org: "+organizationType);
			userSpogServer.deleteUserColumnsForLoggedInUserWithCheck(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			
		}
	
	@DataProvider(name="UpdateUserColumns_401")
	public final Object[][] UpdateUserColumns_401() {
		
		return new Object[][] {
			// different users
				{ "direct", ti.direct_org1_user1_token,"invalid_token",new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},4},
								
				{ "msp", ti.root_msp_org1_user1_token,"invalid_token",new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"3","6","2","1","4","5","7"},7},
				
				{ "suborg", ti.root_msp1_suborg1_user1_token,"invalid_token",new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"5","2","4","1","6","7","3"},1},
						
		};
	}
	@Test(dataProvider="UpdateUserColumns_401",enabled=true)
	public void updateUserColumnsForLoggedInUserTest_401(String organizationType,
															String validToken,
															String invalidToken,
															String[] sort,
															String[] filter,
															String[] visible,
															String[] orderId, 
															int noofcolumnstobecreated
															) {
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		
		test.assignAuthor("Rakesh.Chalamala");
		userSpogServer.setToken(validToken);
		
		ArrayList<HashMap<String,Object>> expected_columns = new ArrayList<>();
		HashMap<String,Object> temp = new HashMap<>() ;

		test.log(LogStatus.INFO, "Compose the user columns as per the user request");
		for(int i=0;i<noofcolumnstobecreated ;i++)
		{
			int index1 = gen_random_index(sort);
			int index2 = gen_random_index(filter);
			int index3 = gen_random_index(visible);
			//int index4 = gen_random_index(orderId);
			
			temp = userSpogServer.composeUser_Column(columnIdList.get(i),sort[index1],filter[index2],visible[index3],orderId[i]);
			expected_columns.add(temp);
		}
		
		test.log(LogStatus.INFO, "Create the user columns for the loggdin user in the org: "+organizationType);
		Response response= userSpogServer.createUserColumnsForLoggedInUser(validToken, expected_columns, test);
			
		test.log(LogStatus.INFO, "Update the user columns for the loggdin user in the org: "+organizationType);
		userSpogServer.updateUserColumnsForLoggedInUser(invalidToken, expected_columns, columnsHeadContent, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);
			
		test.log(LogStatus.INFO, "Update the user columns for the loggdin user in the org: "+organizationType);
		userSpogServer.updateUserColumnsForLoggedInUser("", expected_columns, columnsHeadContent, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);
				
		test.log(LogStatus.INFO, "Delete the user columns for the loggdin user in the org: "+organizationType);
		userSpogServer.deleteUserColumnsForLoggedInUserWithCheck(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
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
