package api.restorejobreports.columns;

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

import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.SPOGReportServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class UpdateRestoreJobReportsColumnsForSpecifiedUserTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private UserSpogServer userSpogServer;
	private SPOGReportServer spogReportServer;
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
	
	LocalDate date = LocalDate.now();  
	LocalDate yesterday = date.minusDays(1);  
	LocalDate tomorrow = yesterday.plusDays(2);
	
	ArrayList<String> columnIdList = new ArrayList<String>();
	ArrayList<HashMap<String,Object>> columnsHeadContent = new ArrayList<HashMap<String,Object>>();
	
	private String  org_model_prefix=this.getClass().getSimpleName();
	private TestOrgInfo ti;
	
	@BeforeClass
	@Parameters({ "baseURI", "port","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String logFolder,String runningMachine,String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		spogReportServer = new SPOGReportServer(baseURI, port);
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(), logFolder);
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
		
		spogServer.setToken(ti.direct_org1_user1_token);
		spogReportServer.setToken(ti.direct_org1_user1_token);
		Response response = spogReportServer.getSystemRestoreJobReportsColumnsWithCheck(ti.csr_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);	

		columnsHeadContent = response.then().extract().path("data");
		int length = columnsHeadContent.size();
		for (int i = 0; i < length; i++) {
			HashMap<String,Object> HeadContent = columnsHeadContent.get(i);
			columnIdList.add((String) HeadContent.get("column_id"));
		}

	}
	
	@DataProvider(name="updateRestoreJobReportsColumnsByUserId_201")
	public Object[][] updateRestoreJobReportsColumnsByUserId_201(){
		
		return new Object[][] {
			{"csr", ti.csr_token,ti.csr_admin_user_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
				new String[] { "true", "false","none"},new String[]{"2","3","12","1","4","10","11","7","5","6","9","8"},new String[] { "true"},new String[]{"4","7","5","6","9","8"},4,2},
			{"csr_readonly", ti.csr_readonly_token,ti.csr_readonly_admin_user_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"2","3","12","1","4","10","11","7","5","6","9","8"},new String[] { "true"},new String[]{"4","7","5","6","9","8"},4,2},
			
			{"direct", ti.direct_org1_user1_token,ti.direct_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
				new String[] { "true", "false","none"},new String[]{"2","3","12","1","4","10","11","7","5","6","9","8"},new String[] { "true"},new String[]{"4","7","5","6","9","8"},4,2},
			{"msp", ti.root_msp_org1_user1_token, ti.root_msp_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
				new String[] { "true", "false","none"},new String[]{"2","3","12","1","4","7","10","11","13","5","6","9","8"},new String[] { "false"},new String[]{"1","6","9","8"},4,3},
			{"suborg", ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"2","3","1","12","4","7","10","11","5","6","9","8"},new String[] { "true"},new String[]{"7","5","6","9","8"},4,1},
				
			{"suborg-mspT", ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"2","3","1","4","12","10","11","7","5","6","9","8"},new String[] { "false"},new String[]{"7","5","6","9","8"},4,2},
			{"suborg-mspAccAdminT", ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp1_suborg1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
						new String[] { "true", "false","none"},new String[]{"2","3","1","4","12","10","11","7","5","6","9","8"},new String[] { "false"},new String[]{"7","5","6","9","8"},4,2},
				
		};
	}

	@Test(dataProvider="updateRestoreJobReportsColumnsByUserId_201")
	public void updateRestoreJobReportsColumnsByUserId_201(String organizationType,
													String validToken,
													String user_Id,
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
		spogReportServer.setToken(validToken);
		
		ArrayList<HashMap<String,Object>> expected_columns = new ArrayList<>();
		ArrayList<HashMap<String, Object>> expected_updated_columns = new ArrayList<>();
		HashMap<String,Object> temp = new HashMap<>() ;
		HashMap<String, Object> temp1 = new HashMap<>();

		test.log(LogStatus.INFO, "Compose the RestoreJobReports columns as per the user request");
		for(int i=0;i<noofcolumnstobecreated ;i++)
		{
			int index1 = gen_random_index(sort);
			int index2 = gen_random_index(filter);
			int index3 = gen_random_index(visible);
			//int index4 = gen_random_index(orderId);
			
			temp = spogReportServer.composeRestoreJobReports_Column(columnIdList.get(i),sort[index1],filter[index2],visible[index3],orderId[i]);
			expected_columns.add(temp);
		}
		test.log(LogStatus.INFO, "Create RestoreJobReports columns for specified user in org : "+ organizationType);
		Response response = spogReportServer.createRestoreJobReportsColumnsForSpecifiedUser(user_Id, validToken, expected_columns, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		for(int i=0;i<noofcolumnstobeupdated ;i++)
		{
			int index3 = gen_random_index(visible);
			//int index4 = gen_random_index(orderId);
			
			temp1 = spogReportServer.composeRestoreJobReports_Column(columnIdList.get(i),null,null,visible[index3],updatedOrderId[i]);
			expected_updated_columns.add(temp1);
		}
		
		test.log(LogStatus.INFO, "Update RestoreJobReports columns for specified User in org : "+organizationType);
		spogReportServer.updateRestoreJobReportsColumnsByUserId(user_Id, validToken, expected_updated_columns, columnsHeadContent, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		
		test.log(LogStatus.INFO, "Delete RestoreJobReports columns for specified user in org : " + organizationType);
		spogReportServer.deleteRestoreJobReportsColumnsforSpecifiedUserwithCheck(user_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
	}
	@DataProvider(name="UpdateRestoreJobReportsColumns_400")
	public final Object[][] UpdateRestoreJobReportsColumns_400() {

		return new Object[][] {
			// different users
			{ "direct-noColId", ti.direct_org1_user1_token,ti.direct_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
				new String[] { "true", "false","none"},new String[]{"2","3","1","10","12","11","4","7","5","6","9","8"},4},
			{ "direct-invalidOrderId_0", ti.direct_org1_user1_token,ti.direct_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"2","10","11","12","3","1","4","7","5","6","9","8"},2},
			{ "direct-invalidOrderId_1", ti.direct_org1_user1_token,ti.direct_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
						new String[] { "true", "false","none"},new String[]{"2","3","10","12","11","1","4","7","5","6","9","8"},2},
			{ "direct-moreOrderIds", ti.direct_org1_user1_token,ti.direct_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
							new String[] { "true", "false","none"},new String[]{"2","10","11","3","1","4","7","5","6","9","8","10","12"},11},

			{ "msp-noColId", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
								new String[] { "true", "false","none"},new String[]{"2","3","1","10","12","11","4","7","5","6","9","8"},4},
			{ "msp-invalidOrderId_0", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
									new String[] { "true", "false","none"},new String[]{"2","10","11","12","3","1","4","7","5","6","9","8"},2},
			{ "msp-invalidOrderId_1", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
										new String[] { "true", "false","none"},new String[]{"2","3","10","12","11","1","4","7","5","6","9","8"},2},
			{ "msp-moreOrderIds", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
											new String[] { "true", "false","none"},new String[]{"2","10","11","3","1","4","7","5","6","9","8","10","12"},11},

			{ "suborg-noColId", ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
												new String[] { "true", "false","none"},new String[]{"2","3","1","10","12","11","4","7","5","6","9","8"},4},
			{ "suborg-invalidOrderId_0", ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
													new String[] { "true", "false","none"},new String[]{"2","10","11","12","3","1","4","7","5","6","9","8"},2},
			{ "suborg-invalidOrderId_1", ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
														new String[] { "true", "false","none"},new String[]{"2","3","10","12","11","1","4","7","5","6","9","8"},2},
			{ "suborg-moreOrderIds", ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
															new String[] { "true", "false","none"},new String[]{"2","10","11","3","1","4","7","5","6","9","8","10","12"},11},

			{ "mspAccAdmin-noColId", ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp_org1_msp_accountadmin1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
																new String[] { "true", "false","none"},new String[]{"2","3","1","10","12","11","4","7","5","6","9","8"},4},
			{ "mspAccAdmin-invalidOrderId_0", ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp_org1_msp_accountadmin1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
																	new String[] { "true", "false","none"},new String[]{"2","10","11","12","3","1","4","7","5","6","9","8"},2},
			{ "mspAccAdmin-invalidOrderId_1", ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp_org1_msp_accountadmin1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
																		new String[] { "true", "false","none"},new String[]{"2","3","10","12","11","1","4","7","5","6","9","8"},2},
			{ "mspAccAdmin-moreOrderIds", ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp_org1_msp_accountadmin1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
																			new String[] { "true", "false","none"},new String[]{"2","10","11","3","1","4","7","5","6","9","8","10","12"},11},

			{ "csr-noColId", ti.csr_token,ti.csr_admin_user_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
																				new String[] { "true", "false","none"},new String[]{"2","3","1","10","12","11","4","7","5","6","9","8"},4},
			{ "csr-invalidOrderId_0", ti.csr_token,ti.csr_admin_user_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
																					new String[] { "true", "false","none"},new String[]{"2","10","11","12","3","1","4","7","5","6","9","8"},2},
			{ "csr-invalidOrderId_1", ti.csr_token,ti.csr_admin_user_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
																						new String[] { "true", "false","none"},new String[]{"2","3","10","12","11","1","4","7","5","6","9","8"},2},
			{ "csr-moreOrderIds", ti.csr_token,ti.csr_admin_user_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
																							new String[] { "true", "false","none"},new String[]{"2","10","11","3","1","4","7","5","6","9","8","10","12"},11},

			{ "csr_readonly-noColId", ti.csr_readonly_token,ti.csr_readonly_admin_user_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
																								new String[] { "true", "false","none"},new String[]{"2","3","1","10","12","11","4","7","5","6","9","8"},4},
			{ "csr_readonly-invalidOrderId_0", ti.csr_readonly_token,ti.csr_readonly_admin_user_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
																									new String[] { "true", "false","none"},new String[]{"2","10","11","12","3","1","4","7","5","6","9","8"},2},
			{ "csr_readonly-invalidOrderId_1", ti.csr_readonly_token,ti.csr_readonly_admin_user_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
																										new String[] { "true", "false","none"},new String[]{"2","3","10","12","11","1","4","7","5","6","9","8"},2},
			{ "csr_readonly-moreOrderIds", ti.csr_readonly_token,ti.csr_readonly_admin_user_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
																											new String[] { "true", "false","none"},new String[]{"2","10","11","3","1","4","7","5","6","9","8","10","12"},11},
		};
	}
	@Test(dataProvider="UpdateRestoreJobReportsColumns_400")
	public void updateRestoreJobReportsColumnsByUserIdTest_400(String organizationType,
			  												String validToken,
			  												String user_Id,
			  												String[] sort,
			  												String[] filter,
			  												String[] visible,
			  												String[] orderId,
			  												int noofcolstocreate
			  												) {
		
			String[] updateOrderId_0 = new String[]{"0","2","3"};
			String[] updateOrderId_1 = new String[]{"-1","3","5"};
			String[] moreOrderIds = new String[]{"2","14","13","1","10","7","5","6","9","8","4","11","12","3"};
			
		  	SpogMessageCode expectedErrorMessage;
			test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
			
			test.assignAuthor("Rakesh.Chalamala");
			spogReportServer.setToken(validToken);
			
			
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
				
				temp = spogReportServer.composeRestoreJobReports_Column(columnIdList.get(i), sort[index1], filter[index2], visible[index3], orderId[i]);
				
				expected_columns.add(temp);
			}

			test.log(LogStatus.INFO, "Create RestoreJobReports columns for specified user in the org: "+organizationType);
			Response response= spogReportServer.createRestoreJobReportsColumnsForSpecifiedUser(user_Id, validToken, expected_columns, test);
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);
			
			for(int i=0;i<noofcolstocreate ;i++)
			{
				int index1 = gen_random_index(sort);
				int index2 = gen_random_index(filter);
				int index3 = gen_random_index(visible);
//				int index4 = gen_random_index(orderId);
				if(organizationType.contains("-noColId"))
				{
					temp1 = spogReportServer.composeRestoreJobReports_Column("", sort[index1], filter[index2], visible[index3], orderId[i]);
				}
				else if(organizationType.contains("-invalidOrderId_0")) {
					temp1 = spogReportServer.composeRestoreJobReports_Column(columnIdList.get(i), sort[index1], filter[index2], visible[index3], updateOrderId_0[i]);
				}
				else if(organizationType.contains("-invalidOrderId_1")) {
					temp1 = spogReportServer.composeRestoreJobReports_Column(columnIdList.get(i), sort[index1], filter[index2], visible[index3], updateOrderId_1[i]);
				}
				else if(organizationType.contains("-moreOrderIds"))
				{
					temp1 = spogReportServer.composeRestoreJobReports_Column(columnIdList.get(i), sort[index1], filter[index2], visible[index3], moreOrderIds[i]);
				}
				else {
					temp1 = spogReportServer.composeRestoreJobReports_Column(columnIdList.get(i), sort[index1], filter[index2], visible[index3], orderId[i]);
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
			
			test.log(LogStatus.INFO, "Update the RestoreJobReports columns for specified user in org: "+organizationType);
			spogReportServer.updateRestoreJobReportsColumnsByUserId(user_Id, validToken, expected_columns1, columnsHeadContent, SpogConstants.REQUIRED_INFO_NOT_EXIST, expectedErrorMessage, test);
						
			test.log(LogStatus.INFO, "Delete RestoreJobReports columns for specified user in org: "+organizationType);
			spogReportServer.deleteRestoreJobReportsColumnsforSpecifiedUserwithCheck(user_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			
		}
	
	
	@DataProvider(name="UpdateRestoreJobReportsColumnsForSpecifiedUser_401")
	public final Object[][] UpdateRestoreJobReportsColumnsForSpecifiedUser_401() {
		
		return new Object[][] {
			// different users
				{ "direct", ti.direct_org1_user1_token,"invalid_token",ti.direct_org1_user1_id,new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"2","10","11","3","1","4","7","5","6","9","8","12"},4},
								
				{ "msp", ti.root_msp_org1_user1_token,"invalid_token",ti.root_msp_org1_user1_id,new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"3","6","2","13","10","11","9","8","1","4","5","7","12"},7},
				
				{ "suborg", ti.root_msp1_suborg1_user1_token,"invalid_token",ti.root_msp1_suborg1_user1_id,new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"5","2","9","10","11","4","1","6","7","8","3","12"},1},
						
		};
	}
	@Test(dataProvider="UpdateRestoreJobReportsColumnsForSpecifiedUser_401")
	public void updateRestoreJobReportsColumnsForSpecifiedUserTest_401(String organizationType,
															String validToken,
															String invalidToken,
															String user_id,
															String[] sort,
															String[] filter,
															String[] visible,
															String[] orderId, 
															int noofcolumnstobecreated
															) {
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		
		test.assignAuthor("Rakesh.Chalamala");
		spogReportServer.setToken(validToken);
		
		ArrayList<HashMap<String,Object>> expected_columns = new ArrayList<>();
		HashMap<String,Object> temp = new HashMap<>() ;

		test.log(LogStatus.INFO, "Compose the RestoreJobReports columns as per the user request");
		for(int i=0;i<noofcolumnstobecreated ;i++)
		{
			int index1 = gen_random_index(sort);
			int index2 = gen_random_index(filter);
			int index3 = gen_random_index(visible);
			//int index4 = gen_random_index(orderId);
			
			temp = spogReportServer.composeRestoreJobReports_Column(columnIdList.get(i),sort[index1],filter[index2],visible[index3],orderId[i]);
			expected_columns.add(temp);
		}
		
		test.log(LogStatus.INFO, "Create the RestoreJobReports columns for the Specified user in the org: "+organizationType);
		Response response= spogReportServer.createRestoreJobReportsColumnsForSpecifiedUser(user_id, validToken, expected_columns, test);
			
		test.log(LogStatus.INFO, "Update the RestoreJobReports columns for the specified user in the org: "+organizationType);
		spogReportServer.updateRestoreJobReportsColumnsByUserId(user_id, invalidToken, expected_columns, columnsHeadContent, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);
			
		test.log(LogStatus.INFO, "Update the RestoreJobReports columns for the specified user in the org: "+organizationType);
		spogReportServer.updateRestoreJobReportsColumnsByUserId(user_id, "",	expected_columns, columnsHeadContent, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);
				
		test.log(LogStatus.INFO, "Delete the RestoreJobReports columns for the Specified user in the org: "+organizationType);
		spogReportServer.deleteRestoreJobReportsColumnsforSpecifiedUserwithCheck(user_id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
	}
	
	@DataProvider(name="updateRestoreJobReportsColumns_403")
	public final Object[][] updateRestoreJobReportsColumns_403() {
		
		return new Object[][] {
			// different users
				{ "direct-mspT", ti.direct_org1_user1_token,ti.root_msp_org1_user1_token,ti.direct_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"2","3","1","10","11","4","7","5","6","9","8","12"},4},
				
				{ "direct-suborgT", ti.direct_org1_user1_token,ti.root_msp1_suborg1_user1_token,ti.direct_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
						new String[] { "true", "false","none"},new String[]{"2","3","10","11","1","4","7","5","6","9","8","12"},4},
					
				{ "msp-directT", ti.root_msp_org1_user1_token,ti.direct_org1_user1_token,ti.root_msp_org1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
							new String[] { "true", "false"},new String[]{"3","6","2","13","10","11","9","8","1","4","5","7","12"},7},
				{ "msp-suborgT", ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_user1_token,ti.root_msp_org1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
								new String[] { "true", "false"},new String[]{"3","6","10","13","11","2","9","8","1","4","5","7","12"},7},
				{ "msp-mspbT", ti.root_msp_org1_user1_token,ti.root_msp_org2_user1_token,ti.root_msp_org1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
									new String[] { "true", "false"},new String[]{"3","6","10","13","11","2","9","8","1","4","5","7","12"},7},
				{ "msp-submspT", ti.root_msp_org1_user1_token,ti.root_msp1_submsp1_user1_token,ti.root_msp_org1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
										new String[] { "true", "false"},new String[]{"3","6","10","13","11","2","9","8","1","4","5","7","12"},7},
				{ "msp-submspsuborgT", ti.root_msp_org1_user1_token,ti.msp1_submsp1_suborg1_user1_token,ti.root_msp_org1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
											new String[] { "true", "false"},new String[]{"3","6","10","13","11","2","9","8","1","4","5","7","12"},7},

				{ "submsp-directT", ti.root_msp1_submsp1_user1_token,ti.direct_org1_user1_token,ti.root_msp1_submsp1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
												new String[] { "true", "false"},new String[]{"3","6","2","13","10","11","9","8","1","4","5","7","12"},7},
				{ "submsp-suborgT", ti.root_msp1_submsp1_user1_token,ti.root_msp1_suborg1_user1_token,ti.root_msp1_submsp1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
													new String[] { "true", "false"},new String[]{"3","6","10","13","11","2","9","8","1","4","5","7","12"},7},
				{ "submsp-mspbT", ti.root_msp1_submsp1_user1_token,ti.root_msp_org2_user1_token,ti.root_msp1_submsp1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
														new String[] { "true", "false"},new String[]{"3","6","10","13","11","2","9","8","1","4","5","7","12"},7},
				{ "submsp-submsp2T", ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp2_user1_token,ti.root_msp1_submsp1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
															new String[] { "true", "false"},new String[]{"3","6","10","13","11","2","9","8","1","4","5","7","12"},7},
				{ "submsp-submspsuborgT", ti.root_msp1_submsp1_user1_token,ti.msp1_submsp1_suborg1_user1_token,ti.root_msp1_submsp1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
																new String[] { "true", "false"},new String[]{"3","6","10","13","11","2","9","8","1","4","5","7","12"},7},
				
				
				{ "suborg-directT", ti.root_msp1_suborg1_user1_token,ti.direct_org1_user1_token,ti.root_msp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"1","2","3","4","5","6","10","11","7","8","9","12"},1},
				
				{ "suborg-suborgbT", ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg2_user1_token,ti.root_msp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
						new String[] { "true", "false"},new String[]{"1","2","3","4","10","11","5","12","6","7","8","9"},1},
				
				{ "suborgb-mspAccAdminT", ti.root_msp1_suborg2_user1_token,ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp1_suborg2_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
							new String[] { "true", "false"},new String[]{"1","2","3","4","10","11","12","5","6","7","8","9"},1},
		
		};
	}
	@Test(dataProvider="updateRestoreJobReportsColumns_403")
	public void updateRestoreJobReportsColumnForSpecifiedUserTest_403(String organizationType,
															String validToken,
															String otherUserValidToken,
															String user_Id,
															String[] sort,
															String[] filter,
															String[] visible,
															String[] orderId, 
															int noofcolumnstobecreated
															) {
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		
		test.assignAuthor("Rakesh.Chalamala");
		spogReportServer.setToken(validToken);
		SpogMessageCode expected_errormessage = SpogMessageCode.LOGIN_USER_ORG_NOT_SAME_AS_GET_USER;
		
		ArrayList<HashMap<String,Object>> expected_columns = new ArrayList<>();
		HashMap<String,Object> temp = new HashMap<>() ;

		test.log(LogStatus.INFO, "Compose the RestoreJobReports columns as per the user request");
		for(int i=0;i<noofcolumnstobecreated ;i++)
		{
			int index1 = gen_random_index(sort);
			int index2 = gen_random_index(filter);
			int index3 = gen_random_index(visible);
			//int index4 = gen_random_index(orderId);
			
			temp = spogReportServer.composeRestoreJobReports_Column(columnIdList.get(i),sort[index1],filter[index2],visible[index3],orderId[i]);
			expected_columns.add(temp);
		}
		
		test.log(LogStatus.INFO, "Create the RestoreJobReports columns for the specified user in the org: "+organizationType);
		Response response= spogReportServer.createRestoreJobReportsColumnsForSpecifiedUser(user_Id,validToken,expected_columns,test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		
		if (organizationType=="msp-directT" || organizationType=="msp-suborgT") {
			expected_errormessage = SpogMessageCode.RESOURCE_PERMISSION_DENY;
		}
		else{expected_errormessage = SpogMessageCode.RESOURCE_PERMISSION_DENY;}
			
		test.log(LogStatus.INFO, "Update the RestoreJobReports columns for the specified user in the org: "+organizationType);
		spogReportServer.updateRestoreJobReportsColumnsByUserId(user_Id, otherUserValidToken, expected_columns, columnsHeadContent, SpogConstants.INSUFFICIENT_PERMISSIONS, expected_errormessage, test);
		
		test.log(LogStatus.INFO, "Delete the RestoreJobReports columns for the specified user of the org: "+organizationType);	
		spogReportServer.deleteRestoreJobReportsColumnsforSpecifiedUserwithCheck(user_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
	}
	//400 and 404
		@DataProvider(name="updateRestoreJobReportsColumns_404")
		public final Object[][] updateRestoreJobReportsColumns_404() {
			
			return new Object[][] {
				// different users
					{ "direct-invalidUserId", ti.direct_org1_user1_token,ti.direct_org1_user1_id,"invalid_user_id", new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
						new String[] { "true", "false","none"},new String[]{"2","3","10","11","12","1","4","7","5","6","9","8"},4},
					{ "msp-invalidUserId", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id,"invalid_user_id", new String[] { "true", "false"}, new String[] { "true", "false"},
								new String[] { "true", "false"},new String[]{"3","6","10","11","13","2","12","9","8","1","4","5","7"},7},
					{ "suborg-invalidUserId", ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id,"invalid_user_id", new String[] { "true", "false"}, new String[] { "true", "false"},
						new String[] { "true", "false"},new String[]{"1","2","3","4","10","12","11","5","6","7","8","9"},1},
					{ "csr-invalidUserId", ti.csr_token,ti.csr_admin_user_id,"invalid_user_id", new String[] { "true", "false"}, new String[] { "true", "false"},
							new String[] { "true", "false"},new String[]{"1","2","4","10","11","12","6","5","3","7","8","9"},1},
					{ "csr_Readonly-invalidUserId", ti.csr_readonly_token,ti.csr_readonly_admin_user_id,"invalid_user_id", new String[] { "true", "false"}, new String[] { "true", "false"},
								new String[] { "true", "false"},new String[]{"1","2","4","10","11","12","6","5","3","7","8","9"},1},
					
					{ "direct-randomUserId", ti.direct_org1_user1_token,ti.direct_org1_user1_id,UUID.randomUUID().toString(), new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
							new String[] { "true", "false","none"},new String[]{"2","3","10","11","12","1","4","7","5","6","9","8"},4},
					{ "msp-randomUserId", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id,UUID.randomUUID().toString(), new String[] { "true", "false"}, new String[] { "true", "false"},
							new String[] { "true", "false"},new String[]{"3","6","2","9","10","13","11","8","12","1","4","5","7"},7},	
					{ "suborg-randomUserId", ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id,UUID.randomUUID().toString(), new String[] { "true", "false"}, new String[] { "true", "false"},
							new String[] { "true", "false"},new String[]{"1","2","3","4","10","11","5","12","6","7","8","9"},1},
					{ "csr-randomUserId", ti.csr_token,ti.csr_admin_user_id,UUID.randomUUID().toString(), new String[] { "true", "false"}, new String[] { "true", "false"},
								new String[] { "true", "false"},new String[]{"1","2","4","10","11","6","12","5","3","7","8","9"},1},
					{ "csr_readonly-randomUserId", ti.csr_readonly_token,ti.csr_readonly_admin_user_id,UUID.randomUUID().toString(), new String[] { "true", "false"}, new String[] { "true", "false"},
									new String[] { "true", "false"},new String[]{"1","2","4","10","11","6","12","5","3","7","8","9"},1},
								
			};
		}
		@Test(dataProvider="updateRestoreJobReportsColumns_404")
		public void updateRestoreJobReportsColumnForSpecifiedUserTest_404(String organizationType,
																String validToken,
																String valid_user_Id,
																String invalid_user_Id,
																String[] sort,
																String[] filter,
																String[] visible,
																String[] orderId, 
																int noofcolumnstobecreated
																) {
			test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
			
			test.assignAuthor("Rakesh.Chalamala");
			spogReportServer.setToken(validToken);
			
			ArrayList<HashMap<String,Object>> expected_columns = new ArrayList<>();
			HashMap<String,Object> temp = new HashMap<>() ;

			test.log(LogStatus.INFO, "Compose the RestoreJobReports columns as per the user request");
			for(int i=0;i<noofcolumnstobecreated ;i++)
			{
				int index1 = gen_random_index(sort);
				int index2 = gen_random_index(filter);
				int index3 = gen_random_index(visible);
				//int index4 = gen_random_index(orderId);
				
				temp = spogReportServer.composeRestoreJobReports_Column(columnIdList.get(i),sort[index1],filter[index2],visible[index3],orderId[i]);
				expected_columns.add(temp);
			}
			
			test.log(LogStatus.INFO, "Create the RestoreJobReports columns for the specified user in the org: "+organizationType);
			Response response= spogReportServer.createRestoreJobReportsColumnsForSpecifiedUser(valid_user_Id,validToken,expected_columns,test);
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);
			//getRestoreJobReportsColumn for specified user
			
			if(invalid_user_Id=="invalid_user_id")
			{	//400
				test.log(LogStatus.INFO, "Update the RestoreJobReports columns for the specified user in the org: "+organizationType);
				spogReportServer.updateRestoreJobReportsColumnsByUserId(invalid_user_Id, validToken, expected_columns, columnsHeadContent, SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_USERID_IS_NOT_UUID, test);
			}
			
			else
			{	//404
				test.log(LogStatus.INFO, "Update the RestoreJobReports columns for the specified user in the org: "+organizationType);
				spogReportServer.updateRestoreJobReportsColumnsByUserId(invalid_user_Id, validToken, expected_columns, columnsHeadContent, SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.USER_ID_DOESNOT_EXIST, test);
			}
			
			test.log(LogStatus.INFO, "Delete the RestoreJobReports columns for the specified user of the org: "+organizationType);	
			spogReportServer.deleteRestoreJobReportsColumnsforSpecifiedUserwithCheck(valid_user_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
	}
		//403 csr user id
		@DataProvider(name="getRestoreJobReportsColumns_403_csrUserId")
		public final Object[][] getRestoreJobReportsColumns_403_csrUserId() {
		
			return new Object[][] {
			// different users
				{ "direct-csrUserId", ti.direct_org1_user1_token,ti.direct_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
						new String[] { "true", "false","none"},new String[]{"2","3","10","11","1","12","4","7","5","6","9","8"},4},
							
				{ "msp-csrUserId", ti.root_msp_org1_user1_token, ti.root_msp_org1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
						new String[] { "true", "false"},new String[]{"3","6","2","9","10","11","13","8","12","1","4","5","7"},7},
							
				{ "suborg-csrUserId", ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
						new String[] { "true", "false"},new String[]{"1","2","3","4","10","11","5","12","6","7","8","9"},1}
				};
		}
		@Test(dataProvider="getRestoreJobReportsColumns_403_csrUserId")
		public void getRestoreJobReportsColumnForSpecifiedUserTest_403_csrUserId(String organizationType,
																		String validToken,
																		String user_Id,
																		String[] sort,
																		String[] filter,
																		String[] visible,
																		String[] orderId, 
																		int noofcolumnstobecreated
																		) {
			SpogMessageCode expectedMessageCode;
				
			test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
			
			test.assignAuthor("Rakesh.Chalamala");
			spogReportServer.setToken(validToken);
				
			ArrayList<HashMap<String,Object>> expected_columns = new ArrayList<>();
			HashMap<String,Object> temp = new HashMap<>() ;

			test.log(LogStatus.INFO, "Compose the RestoreJobReports columns as per the user request");
			for(int i=0;i<noofcolumnstobecreated ;i++)
			{
				int index1 = gen_random_index(sort);
				int index2 = gen_random_index(filter);
				int index3 = gen_random_index(visible);
				//int index4 = gen_random_index(orderId);
					
				temp = spogReportServer.composeRestoreJobReports_Column(columnIdList.get(i),sort[index1],filter[index2],visible[index3],orderId[i]);
				expected_columns.add(temp);
			}
				
			test.log(LogStatus.INFO, "Create the RestoreJobReports columns for the specified user in the org: "+organizationType);
			Response response= spogReportServer.createRestoreJobReportsColumnsForSpecifiedUser(user_Id,validToken,expected_columns,test);
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);
			
			//getRestoreJobReportsColumn for specified user
			if(user_Id==ti.direct_org1_user1_id || user_Id==ti.root_msp1_suborg1_user1_id) {
				expectedMessageCode = SpogMessageCode.RESOURCE_PERMISSION_DENY;
				}
			else {
				expectedMessageCode = SpogMessageCode.RESOURCE_PERMISSION_DENY;
				}
			test.log(LogStatus.INFO, "Update the RestoreJobReports columns for the specified user in the org: "+organizationType);
			spogReportServer.updateRestoreJobReportsColumnsByUserId(ti.csr_admin_user_id, validToken, expected_columns, columnsHeadContent, SpogConstants.INSUFFICIENT_PERMISSIONS, expectedMessageCode, test);
							
			test.log(LogStatus.INFO, "Delete the RestoreJobReports columns for the specified user of the org: "+organizationType);	
			spogReportServer.deleteRestoreJobReportsColumnsforSpecifiedUserwithCheck(user_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
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
