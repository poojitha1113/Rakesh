package api.users.recoveredresource.columns;

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
import InvokerServer.SPOGRecoveredResourceServer;
import InvokerServer.SPOGRecoveredResourceServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class GetRecoveredResourceColumnsByUserIdTest extends base.prepare.Is4Org{
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private UserSpogServer userSpogServer;
	private SPOGRecoveredResourceServer spogRecoveredResourceServer;
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
		spogRecoveredResourceServer = new SPOGRecoveredResourceServer(baseURI, port);
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
		spogRecoveredResourceServer.setToken(ti.direct_org1_user1_token);
		Response response = spogRecoveredResourceServer.getRecoveredResourceColumns(ti.direct_org1_user1_token,SpogConstants.SUCCESS_GET_PUT_DELETE, test);	

		columnsHeadContent = response.then().extract().path("data");
		int length = columnsHeadContent.size();
		for (int i = 0; i < length; i++) {
			HashMap<String,Object> HeadContent = columnsHeadContent.get(i);
			columnIdList.add((String) HeadContent.get("column_id"));
		}

	}
	
	@DataProvider(name="getRecoveredResourceColumns_valid")
	public final Object[][] getRecoveredResourceColumns_valid() {
		
		return new Object[][] {
			// different users
				{ "csr", ti.csr_token,ti.csr_admin_user_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6","9","8","10","11"},11},
				{ "csr_readonly", ti.csr_readonly_token,ti.csr_readonly_admin_user_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6","9","8","10","11"},11},
			
				{ "direct", ti.direct_org1_user1_token,ti.direct_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6","9","8","10","11"},11},
				{ "msp", ti.root_msp_org1_user1_token, ti.root_msp_org1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"3","6","2","9","10","11","8","1","4","5","7"},7},
				{ "suborg", ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"1","2","3","4","10","11","5","6","7","8","9"},1},
				
//				Get RecoveredResource columns by user id of sub org using msp token should return  200 and validate the response
				{ "suborg-mspT", ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_user1_id,new String[] { "true", "false"}, new String[] { "true", "false"},
						new String[] { "true", "false"},new String[]{"1","2","3","4","10","11","5","6","7","8","9"},5},	
				{ "suborg-mspAccAdminT", ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp1_suborg1_user1_id,new String[] { "true", "false"}, new String[] { "true", "false"},
							new String[] { "true", "false"},new String[]{"1","2","3","4","10","11","5","6","7","8","9"},5},	
		};
	}
	
	@Test(dataProvider="getRecoveredResourceColumns_valid")
	public void getRecoveredResourceColumnForSpecifiedUserTest_200(String organizationType,
															String validToken,
															String user_Id,
															String[] sort,
															String[] filter,
															String[] visible,
															String[] orderId, 
															int noofcolumnstobecreated
															) {
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		
		test.assignAuthor("Rakesh.Chalamala");
		spogRecoveredResourceServer.setToken(validToken);
		
		ArrayList<HashMap<String,Object>> expected_columns = new ArrayList<>();
		HashMap<String,Object> temp = new HashMap<>() ;

		test.log(LogStatus.INFO, "Compose the RecoveredResource columns as per the user request");
		for(int i=0;i<noofcolumnstobecreated ;i++)
		{
			int index1 = gen_random_index(sort);
			int index2 = gen_random_index(filter);
			int index3 = gen_random_index(visible);
			//int index4 = gen_random_index(orderId);
			
			temp = spogRecoveredResourceServer.composeRecoveredResource_Column(columnIdList.get(i),sort[index1],filter[index2],visible[index3],orderId[i]);
			expected_columns.add(temp);
		}
		
		test.log(LogStatus.INFO, "Create the RecoveredResource columns for the specified user in the org: "+organizationType);
		Response response= spogRecoveredResourceServer.createRecoveredResourceColumnsForSpecifiedUser(user_Id,validToken,expected_columns,test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		
		//getRecoveredResourceColumn for specified user
		test.log(LogStatus.INFO, "Get the RecoveredResource columns for the specified user in the org: "+organizationType);
		spogRecoveredResourceServer.getRecoveredResourceColumnsForSpecifiedUser(user_Id, validToken, expected_columns, columnsHeadContent, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		
		//getRecoveredResourceColumns with csr token
		test.log(LogStatus.INFO, "Get the RecoveredResource columns for the specified user in the org: "+organizationType);
		spogRecoveredResourceServer.getRecoveredResourceColumnsForSpecifiedUser(user_Id, ti.csr_token, expected_columns, columnsHeadContent, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		
		test.log(LogStatus.INFO, "Delete the RecoveredResource columns for the specified user of the org: "+organizationType);	
		spogRecoveredResourceServer.deleteRecoveredResourceColumnsforSpecifiedUserwithCheck(user_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		if(organizationType=="suborg")
		{	//creating with suborg token
			test.log(LogStatus.INFO, "Create the RecoveredResource columns for the specified user in the org: "+organizationType);
			response= spogRecoveredResourceServer.createRecoveredResourceColumnsForSpecifiedUser(user_Id,validToken,expected_columns,test);
			//getting with msp token
			test.log(LogStatus.INFO, "Get the RecoveredResource columns for the specified user in the org: "+organizationType);
			spogRecoveredResourceServer.getRecoveredResourceColumnsForSpecifiedUser(user_Id, ti.root_msp_org1_user1_token, expected_columns, columnsHeadContent, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			
			test.log(LogStatus.INFO, "Delete the RecoveredResource columns for the specified user of the org: "+organizationType);	
			spogRecoveredResourceServer.deleteRecoveredResourceColumnsforSpecifiedUserwithCheck(user_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			
			
			//creating with msp token
			test.log(LogStatus.INFO, "Create the RecoveredResource columns for the specified user in the org: "+organizationType);
			response= spogRecoveredResourceServer.createRecoveredResourceColumnsForSpecifiedUser(user_Id,ti.root_msp_org1_user1_token,expected_columns,test);
			//getting with suborg token
			test.log(LogStatus.INFO, "Get the RecoveredResource columns for the specified user in the org: "+organizationType);
			spogRecoveredResourceServer.getRecoveredResourceColumnsForSpecifiedUser(user_Id, validToken, expected_columns, columnsHeadContent, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			
			test.log(LogStatus.INFO, "Delete the RecoveredResource columns for the specified user of the org: "+organizationType);	
			spogRecoveredResourceServer.deleteRecoveredResourceColumnsforSpecifiedUserwithCheck(user_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		}	
	}
	
	@DataProvider(name="GetRecoveredResourceColumns_401")
	public final Object[][] GetRecoveredResourceColumns_401() {
		
		return new Object[][] {
			// different users
				{ "direct", ti.direct_org1_user1_token,"invalid_token",ti.direct_org1_user1_id,new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"2","3","1","10","11","4","7","5","6","9","8"},4},
								
				{ "msp", ti.root_msp_org1_user1_token,"invalid_token",ti.root_msp_org1_user1_id,new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"3","6","2","9","8","10","11","1","4","5","7"},7},
				
				{ "suborg", ti.root_msp1_suborg1_user1_token,"invalid_token",ti.root_msp1_suborg1_user1_id,new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"5","2","9","4","1","10","11","6","7","8","3"},1},
						
		};
	}
	@Test(dataProvider="GetRecoveredResourceColumns_401",enabled=true)
	public void getRecoveredResourceColumnForSpecifiedUserTest_401(String organizationType,
															String validToken,
															String invalidToken,
															String user_Id,
															String[] sort,
															String[] filter,
															String[] visible,
															String[] orderId, 
															int noofcolumnstobecreated
															) {
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		
		test.assignAuthor("Rakesh.Chalamala");
		spogRecoveredResourceServer.setToken(validToken);
		
		ArrayList<HashMap<String,Object>> expected_columns = new ArrayList<>();
		HashMap<String,Object> temp = new HashMap<>() ;

		test.log(LogStatus.INFO, "Compose the RecoveredResource columns as per the user request");
		for(int i=0;i<noofcolumnstobecreated ;i++)
		{
			int index1 = gen_random_index(sort);
			int index2 = gen_random_index(filter);
			int index3 = gen_random_index(visible);
			//int index4 = gen_random_index(orderId);
			
			temp = spogRecoveredResourceServer.composeRecoveredResource_Column(columnIdList.get(i),sort[index1],filter[index2],visible[index3],orderId[i]);
			expected_columns.add(temp);
		}
		
		test.log(LogStatus.INFO, "Create the RecoveredResource columns for the Specified user in the org: "+organizationType);
		Response response = spogRecoveredResourceServer.createRecoveredResourceColumnsForSpecifiedUser(user_Id, validToken, expected_columns, test);
			
		test.log(LogStatus.INFO, "Get the RecoveredResource columns for the specified user in the org: "+organizationType);
		spogRecoveredResourceServer.getRecoveredResourceColumnsForSpecifiedUser(user_Id, invalidToken, expected_columns, columnsHeadContent, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);
			
		test.log(LogStatus.INFO, "Get the RecoveredResource columns for the specified user in the org: "+organizationType);
		spogRecoveredResourceServer.getRecoveredResourceColumnsForSpecifiedUser(user_Id, "", expected_columns, columnsHeadContent, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);
				
		test.log(LogStatus.INFO, "Delete the RecoveredResource columns for the logged in user in the org: "+organizationType);
		spogRecoveredResourceServer.deleteRecoveredResourceColumnsforSpecifiedUserwithCheck(user_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);		
	}
	
	//403
	@DataProvider(name="getRecoveredResourceColumns_403")
	public final Object[][] getRecoveredResourceColumns_403() {
		
		return new Object[][] {
			// different users
				{ "direct-mspT", ti.direct_org1_user1_token,ti.root_msp_org1_user1_token,ti.direct_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"2","3","1","10","11","4","7","5","6","9","8"},4},
				
				{ "direct-suborgT", ti.direct_org1_user1_token,ti.root_msp1_suborg1_user1_token,ti.direct_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
						new String[] { "true", "false","none"},new String[]{"2","3","1","4","10","11","7","5","6","9","8"},4},
					
				{ "msp-directT", ti.root_msp_org1_user1_token,ti.direct_org1_user1_token,ti.root_msp_org1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
							new String[] { "true", "false"},new String[]{"3","6","2","9","8","10","11","1","4","5","7"},7},			
				{ "msp-suborgT", ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_user1_token,ti.root_msp_org1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
								new String[] { "true", "false"},new String[]{"3","6","2","9","10","11","8","1","4","5","7"},7},
				{ "msp-mspbT", ti.root_msp_org1_user1_token,ti.root_msp_org2_user1_token,ti.root_msp_org1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
									new String[] { "true", "false"},new String[]{"3","6","2","10","11","9","8","1","4","5","7"},7},
				{ "msp-submspT", ti.root_msp_org1_user1_token,ti.root_msp1_submsp1_user1_token,ti.root_msp_org1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
										new String[] { "true", "false"},new String[]{"3","6","2","9","10","11","8","1","4","5","7"},7},
				{ "msp-submspsuborgT", ti.root_msp_org1_user1_token,ti.msp1_submsp1_suborg1_user1_token,ti.root_msp_org1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
											new String[] { "true", "false"},new String[]{"3","6","2","10","11","9","8","1","4","5","7"},7},

				{ "submsp-directT", ti.root_msp1_submsp1_user1_token,ti.direct_org1_user1_token,ti.root_msp1_submsp1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
												new String[] { "true", "false"},new String[]{"3","6","2","9","8","10","11","1","4","5","7"},7},			
				{ "submsp-suborgT", ti.root_msp1_submsp1_user1_token,ti.root_msp1_suborg1_user1_token,ti.root_msp1_submsp1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
													new String[] { "true", "false"},new String[]{"3","6","2","9","10","11","8","1","4","5","7"},7},
				{ "submsp-mspbT", ti.root_msp1_submsp1_user1_token,ti.root_msp_org2_user1_token,ti.root_msp1_submsp1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
														new String[] { "true", "false"},new String[]{"3","6","2","10","11","9","8","1","4","5","7"},7},
				{ "submsp-submsp2T", ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp2_user1_token,ti.root_msp1_submsp1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
															new String[] { "true", "false"},new String[]{"3","6","2","9","10","11","8","1","4","5","7"},7},
				{ "submsp-submspsuborgT", ti.root_msp1_submsp1_user1_token,ti.msp1_submsp1_suborg1_user1_token,ti.root_msp1_submsp1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
																new String[] { "true", "false"},new String[]{"3","6","2","10","11","9","8","1","4","5","7"},7},
				
				
				{ "suborg-directT", ti.root_msp1_suborg1_user1_token,ti.direct_org1_user1_token,ti.root_msp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"1","2","3","4","5","6","7","10","11","8","9"},1},
				
				{ "suborg-suborgbT", ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg2_user1_token,ti.root_msp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
						new String[] { "true", "false"},new String[]{"1","2","3","4","5","6","10","11","7","8","9"},1},
				
				{ "suborgb-mspAccAdminT", ti.root_msp1_suborg2_user1_token,ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp1_suborg2_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
							new String[] { "true", "false"},new String[]{"1","2","3","4","5","6","10","11","7","8","9"},1},
		
		};
	}
	@Test(dataProvider="getRecoveredResourceColumns_403",enabled=true)
	public void getRecoveredResourceColumnForSpecifiedUserTest_403(String organizationType,
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
		spogRecoveredResourceServer.setToken(validToken);
		SpogMessageCode expected_errormessage = SpogMessageCode.LOGIN_USER_ORG_NOT_SAME_AS_GET_USER;
		
		ArrayList<HashMap<String,Object>> expected_columns = new ArrayList<>();
		HashMap<String,Object> temp = new HashMap<>() ;

		test.log(LogStatus.INFO, "Compose the RecoveredResource columns as per the user request");
		for(int i=0;i<noofcolumnstobecreated ;i++)
		{
			int index1 = gen_random_index(sort);
			int index2 = gen_random_index(filter);
			int index3 = gen_random_index(visible);
			//int index4 = gen_random_index(orderId);
			
			temp = spogRecoveredResourceServer.composeRecoveredResource_Column(columnIdList.get(i),sort[index1],filter[index2],visible[index3],orderId[i]);
			expected_columns.add(temp);
		}
		
		test.log(LogStatus.INFO, "Create the RecoveredResource columns for the specified user in the org: "+organizationType);
		Response response= spogRecoveredResourceServer.createRecoveredResourceColumnsForSpecifiedUser(user_Id,validToken,expected_columns,test);
		
		if (organizationType=="msp-directT" || organizationType=="msp-suborgT") {
			expected_errormessage = SpogMessageCode.RESOURCE_PERMISSION_DENY;
		}
		else{expected_errormessage = SpogMessageCode.RESOURCE_PERMISSION_DENY;}
			
		test.log(LogStatus.INFO, "Get the RecoveredResource columns for the specified user in the org: "+organizationType);
		spogRecoveredResourceServer.getRecoveredResourceColumnsForSpecifiedUser(user_Id, otherUserValidToken, expected_columns, columnsHeadContent, SpogConstants.INSUFFICIENT_PERMISSIONS, expected_errormessage, test);

		test.log(LogStatus.INFO, "Delete the RecoveredResource columns for the specified user of the org: "+organizationType);	
		spogRecoveredResourceServer.deleteRecoveredResourceColumnsforSpecifiedUserwithCheck(user_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
	}
	
	
	//400 and 404
	@DataProvider(name="getRecoveredResourceColumns_404")
	public final Object[][] getRecoveredResourceColumns_404() {
		
		return new Object[][] {
			// different users
				{ "csr-invalidUserId", ti.csr_token,ti.csr_admin_user_id,"invalid_user_id", new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"2","3","1","4","10","11","7","5","6","9","8"},4},
				{ "csr_readonly-invalidUserId", ti.csr_readonly_token,ti.csr_readonly_admin_user_id,"invalid_user_id", new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"2","3","1","4","10","11","7","5","6","9","8"},4},			
				{ "direct-invalidUserId", ti.direct_org1_user1_token,ti.direct_org1_user1_id,"invalid_user_id", new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"2","3","1","4","10","11","7","5","6","9","8"},4},
				{ "msp-invalidUserId", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id,"invalid_user_id", new String[] { "true", "false"}, new String[] { "true", "false"},
							new String[] { "true", "false"},new String[]{"3","6","2","9","10","11","8","1","4","5","7"},7},
				{ "suborg-invalidUserId", ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id,"invalid_user_id", new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"1","2","3","4","5","6","10","11","7","8","9"},1},
				{ "csr-invalidUserId", ti.csr_token,ti.csr_admin_user_id,"invalid_user_id", new String[] { "true", "false"}, new String[] { "true", "false"},
						new String[] { "true", "false"},new String[]{"1","2","4","6","5","10","11","3","7","8","9"},1},
				
				{ "csr-randomUserId", ti.csr_token,ti.csr_admin_user_id,UUID.randomUUID().toString(), new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
							new String[] { "true", "false","none"},new String[]{"2","3","1","4","10","11","7","5","6","9","8"},4},
				{ "csr_readonly-randomUserId", ti.csr_readonly_token,ti.csr_readonly_admin_user_id,UUID.randomUUID().toString(), new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
								new String[] { "true", "false","none"},new String[]{"2","3","1","4","10","11","7","5","6","9","8"},4},
				{ "direct-randomUserId", ti.direct_org1_user1_token,ti.direct_org1_user1_id,UUID.randomUUID().toString(), new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
						new String[] { "true", "false","none"},new String[]{"2","3","1","4","10","11","7","5","6","9","8"},4},
				{ "msp-randomUserId", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id,UUID.randomUUID().toString(), new String[] { "true", "false"}, new String[] { "true", "false"},
						new String[] { "true", "false"},new String[]{"3","6","2","9","8","1","10","11","4","5","7"},7},	
				{ "suborg-randomUserId", ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id,UUID.randomUUID().toString(), new String[] { "true", "false"}, new String[] { "true", "false"},
						new String[] { "true", "false"},new String[]{"1","2","3","4","5","6","10","11","7","8","9"},1},
				{ "csr-randomUserId", ti.csr_token,ti.csr_admin_user_id,UUID.randomUUID().toString(), new String[] { "true", "false"}, new String[] { "true", "false"},
							new String[] { "true", "false"},new String[]{"1","2","4","6","10","11","5","3","7","8","9"},1},
							
		};
	}
	@Test(dataProvider="getRecoveredResourceColumns_404",enabled=true)
	public void getRecoveredResourceColumnForSpecifiedUserTest_404(String organizationType,
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
		spogRecoveredResourceServer.setToken(validToken);
		
		ArrayList<HashMap<String,Object>> expected_columns = new ArrayList<>();
		HashMap<String,Object> temp = new HashMap<>() ;

		test.log(LogStatus.INFO, "Compose the RecoveredResource columns as per the user request");
		for(int i=0;i<noofcolumnstobecreated ;i++)
		{
			int index1 = gen_random_index(sort);
			int index2 = gen_random_index(filter);
			int index3 = gen_random_index(visible);
			//int index4 = gen_random_index(orderId);
			
			temp = spogRecoveredResourceServer.composeRecoveredResource_Column(columnIdList.get(i),sort[index1],filter[index2],visible[index3],orderId[i]);
			expected_columns.add(temp);
		}
		
		test.log(LogStatus.INFO, "Create the RecoveredResource columns for the specified user in the org: "+organizationType);
		Response response= spogRecoveredResourceServer.createRecoveredResourceColumnsForSpecifiedUser(valid_user_Id,validToken,expected_columns,test);
		
		//getRecoveredResourceColumn for specified user
		
		if(invalid_user_Id=="invalid_user_id")
		{	//400
			test.log(LogStatus.INFO, "Get the RecoveredResource columns for the specified user in the org: "+organizationType);
			spogRecoveredResourceServer.getRecoveredResourceColumnsForSpecifiedUser(invalid_user_Id, validToken, expected_columns, columnsHeadContent, SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_USERID_IS_NOT_UUID, test);
		}
		
		else
		{	//404
			test.log(LogStatus.INFO, "Get the RecoveredResource columns for the specified user in the org: "+organizationType);
			spogRecoveredResourceServer.getRecoveredResourceColumnsForSpecifiedUser(invalid_user_Id, validToken, expected_columns, columnsHeadContent, SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.USER_ID_DOESNOT_EXIST, test);
		}
		
		test.log(LogStatus.INFO, "Delete the RecoveredResource columns for the specified user of the org: "+organizationType);	
		spogRecoveredResourceServer.deleteRecoveredResourceColumnsforSpecifiedUserwithCheck(valid_user_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
	}
	
	//403 csr user id
		@DataProvider(name="getRecoveredResourceColumns_403_csrUserId")
		public final Object[][] getRecoveredResourceColumns_403_csrUserId() {
			
			return new Object[][] {
				// different users
					{ "direct-csrUserId", ti.direct_org1_user1_token,ti.direct_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
						new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","10","11","5","6","9","8"},4},
					
					{ "msp-csrUserId", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
								new String[] { "true", "false"},new String[]{"3","6","10","11","2","9","8","1","4","5","7"},7},
					
					{ "suborg-csrUserId", ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
						new String[] { "true", "false"},new String[]{"1","2","3","10","11","4","5","6","7","8","9"},1}
			};
		}
		@Test(dataProvider="getRecoveredResourceColumns_403_csrUserId",enabled=true)
		public void getRecoveredResourceColumnForSpecifiedUserTest_403_csrUserId(String organizationType,
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
			spogRecoveredResourceServer.setToken(validToken);
			
			ArrayList<HashMap<String,Object>> expected_columns = new ArrayList<>();
			HashMap<String,Object> temp = new HashMap<>() ;

			test.log(LogStatus.INFO, "Compose the RecoveredResource columns as per the user request");
			for(int i=0;i<noofcolumnstobecreated ;i++)
			{
				int index1 = gen_random_index(sort);
				int index2 = gen_random_index(filter);
				int index3 = gen_random_index(visible);
				//int index4 = gen_random_index(orderId);
				
				temp = spogRecoveredResourceServer.composeRecoveredResource_Column(columnIdList.get(i),sort[index1],filter[index2],visible[index3],orderId[i]);
				expected_columns.add(temp);
			}
			
			test.log(LogStatus.INFO, "Create the RecoveredResource columns for the specified user in the org: "+organizationType);
			Response response= spogRecoveredResourceServer.createRecoveredResourceColumnsForSpecifiedUser(user_Id,validToken,expected_columns,test);
			
			//getRecoveredResourceColumn for specified user
			if(user_Id==ti.direct_org1_user1_id || user_Id==ti.root_msp1_suborg1_user1_id) {
				expectedMessageCode = SpogMessageCode.RESOURCE_PERMISSION_DENY;
				}
			else {
				expectedMessageCode = SpogMessageCode.RESOURCE_PERMISSION_DENY;
			}
			test.log(LogStatus.INFO, "Get the RecoveredResource columns for the specified user in the org: "+organizationType);
			spogRecoveredResourceServer.getRecoveredResourceColumnsForSpecifiedUser(ti.csr_admin_user_id, validToken, expected_columns, columnsHeadContent, SpogConstants.INSUFFICIENT_PERMISSIONS, expectedMessageCode, test);	
		
			test.log(LogStatus.INFO, "Delete the RecoveredResource columns for the specified user of the org: "+organizationType);	
			spogRecoveredResourceServer.deleteRecoveredResourceColumnsforSpecifiedUserwithCheck(user_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
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
