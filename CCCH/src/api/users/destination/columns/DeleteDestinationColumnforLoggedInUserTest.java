package api.users.destination.columns;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

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
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class DeleteDestinationColumnforLoggedInUserTest extends base.prepare.Is4Org{
	private SPOGServer spogServer;
	private TestOrgInfo ti;
	private GatewayServer gatewayServer;
	private SPOGDestinationServer spogDestinationServer;
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
	
	LocalDate date = LocalDate.now();  
	LocalDate yesterday = date.minusDays(1);  
	LocalDate tomorrow = yesterday.plusDays(2);
	
	ArrayList<String> columnIdList = new ArrayList<String>();
	ArrayList<HashMap<String,Object>> columnsHeadContent = new ArrayList<HashMap<String,Object>>();
	
	private String  org_model_prefix=this.getClass().getSimpleName();
	
	@BeforeClass
	@Parameters({ "baseURI", "port","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String logFolder,String runningMachine,String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		rep = ExtentManager.getInstance("DeleteDestinationColumnforLoggedInUserTest", logFolder);
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
		Response response = spogServer.getDestinationsColumns(SpogConstants.SUCCESS_GET_PUT_DELETE,test);	

		columnsHeadContent = response.then().extract().path("data");
		int length = columnsHeadContent.size();
		for (int i = 0; i < length; i++) {
			HashMap<String,Object> HeadContent = columnsHeadContent.get(i);

			columnIdList.add((String) HeadContent.get("column_id"));
		}

	}
	
	
	@DataProvider(name = "Delete_destination_coloumns_valid")
	public final Object[][] deleteDestinationColumnsValidData() {
		
		return new Object[][] {
			// different users
				{ "direct", ti.direct_org1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"2","3","1","4","10","7","5","6","9","8"},4},
				{ "msp", ti.root_msp_org1_user1_token, new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"2","1","3","7","10","9","6","4","8","5"},7},
				{ "suborg", ti.root_msp1_suborg1_user1_token, new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"5","8","3","4","1","10","6","7","2","9"},1},

				{"csr", ti.csr_token,new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"2","3","1","4","10","7","5","6","9","8"},4},
				{"csr_readonly", ti.csr_readonly_token,new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
						new String[] { "true", "false","none"},new String[]{"2","3","1","4","10","7","5","6","9","8"},4},
				
				{ "direct-csrT",ti.direct_org1_user1_token,  new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"2","3","1","10","4","7","5","6","9","8"},4},
				{ "msp-csrT", ti.root_msp_org1_user1_token, new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"2","1","3","7","10","9","6","4","8","5"},7},
				{ "suborg-csrT", ti.root_msp1_suborg1_user1_token, new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"5","8","3","4","10","1","6","7","2","9"},1},	
					
				{ "suborg-mspAccAdminT", ti.root_msp_org1_msp_accountadmin1_token, new String[] { "true", "false"}, new String[] { "true", "false"},
						new String[] { "true", "false"},new String[]{"5","8","3","4","10","1","6","7","2","9"},1},	
				
		};
	}
	
	@Test(dataProvider = "Delete_destination_coloumns_valid")
	public void deleteDestinationColumnsForLoggedinUser(String organizationType,
												 String validToken,
												 String[] sort,
												 String[] filter,
												 String[] visible,
												 String[] orderId, 
												 int noofcolumnstobecreated 
												 ){
		
		String token;
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		
		test.assignAuthor("Rakesh.Chalamala");
		spogDestinationServer.setToken(validToken);
		
		ArrayList<HashMap<String,Object>> expected_columns = new ArrayList<>();
		HashMap<String,Object> temp = new HashMap<>() ;

		test.log(LogStatus.INFO, "Compose the destination columns as per the user request");
		for(int i=0;i<noofcolumnstobecreated ;i++)
		{
			int index1 = gen_random_index(sort);
			int index2 = gen_random_index(filter);
			int index3 = gen_random_index(visible);
			//int index4 = gen_random_index(orderId);
			
			temp = spogDestinationServer.composedestination_Column(columnIdList.get(i),sort[index1],filter[index2],visible[index3],orderId[i]);
			expected_columns.add(temp);
		}
		
//		delete without creating columns
		test.log(LogStatus.INFO, "Delete the destination columns for the logged in user for the org: "+organizationType);	
		spogDestinationServer.deleteDestinatinColumnsforLoggedInUserwithCheck(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		
		test.log(LogStatus.INFO, "Create the destination columns for the loggedin user in the org: "+organizationType);
		Response response= spogDestinationServer.createDestinationColumnsForLoggedInUser(validToken, expected_columns, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		
		
		if(organizationType.contains("csrT"))
		{
			token = ti.csr_token;
		}
		else {
			token = validToken;
		}
		
		test.log(LogStatus.INFO, "Delete the destination columns for the logged in user for the org: "+organizationType);	
		spogDestinationServer.deleteDestinatinColumnsforLoggedInUserwithCheck(token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		
		test.log(LogStatus.INFO, "Delete the destination columns for the logged in user for the org: "+organizationType);	
		spogDestinationServer.deleteDestinatinColumnsforLoggedInUserwithCheck(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		
		
	}
	
	@DataProvider(name = "Delete_destination_coloumns_InvalidToken")
	public final Object[][] deleteDestinationColumnsValidData_InvalidToken() {
		
		return new Object[][] {
			// different users
				{ "direct", ti.direct_org1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"8","6","7","1","10","4","3","5","9","2"},4},
				{ "msp", ti.root_msp_org1_user1_token, new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"1","5","3","4","10","2","6","8","7","9"},7},
				{ "suborg", ti.root_msp1_suborg1_user1_token, new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"5","2","8","9","10","1","6","7","3","4"},1},	
		};
	}
	
//	1. Delete user destination columns for logged in user of direct org should be fail with 401 when provide the invalid jwt token
	@Test(dataProvider="Delete_destination_coloumns_InvalidToken")
	public void deleteDestinationColumnsForLoggedinUser_401(String organizationType,
														String validToken,
														String[] sort,
														String[] filter,
														String[] visible,
														String[] orderId, 
														int noofcolumnstobecreated
														){
		String invalidToken="InvalidData";
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		
		test.assignAuthor("Rakesh.Chalamala");
		spogDestinationServer.setToken(validToken);
		
		ArrayList<HashMap<String,Object>> expected_columns = new ArrayList<>();
		HashMap<String,Object> temp = new HashMap<>() ;

		test.log(LogStatus.INFO, "Compose the destination columns as per the user request");
		for(int i=0;i<noofcolumnstobecreated ;i++)
		{
			int index1 = gen_random_index(sort);
			int index2 = gen_random_index(filter);
			int index3 = gen_random_index(visible);
			//int index4 = gen_random_index(orderId);
			
			temp = spogDestinationServer.composedestination_Column(columnIdList.get(i),sort[index1],filter[index2],visible[index3],orderId[i]);
			expected_columns.add(temp);
		}
		
		test.log(LogStatus.INFO, "Create the destination columns for the loggedin user in the org: "+organizationType);
		Response response= spogDestinationServer.createDestinationColumnsForLoggedInUser(validToken, expected_columns, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		
		test.log(LogStatus.INFO, "Delete the destination columns for the logged in user for the org: "+organizationType);	
		spogDestinationServer.deleteDestinatinColumnsforLoggedInUserwithCheck(invalidToken, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);
		
		test.log(LogStatus.INFO, "Delete the destination columns for the logged in user for the org: "+organizationType);	
		spogDestinationServer.deleteDestinatinColumnsforLoggedInUserwithCheck(null, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);
		
		test.log(LogStatus.INFO, "Delete the destination columns for the logged in user for the org: "+organizationType);	
		spogDestinationServer.deleteDestinatinColumnsforLoggedInUserwithCheck("", SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);
		
		test.log(LogStatus.INFO, "Delete the destination columns for the logged in user for the org: "+organizationType);	
		spogDestinationServer.deleteDestinatinColumnsforLoggedInUserwithCheck(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);		
		
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
