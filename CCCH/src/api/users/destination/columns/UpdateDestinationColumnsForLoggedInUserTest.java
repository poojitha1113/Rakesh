package api.users.destination.columns;

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
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class UpdateDestinationColumnsForLoggedInUserTest extends base.prepare.Is4Org{
	private SPOGServer spogServer;
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
	private TestOrgInfo ti;
	
	@BeforeClass
	@Parameters({ "baseURI", "port","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String logFolder,String runningMachine,String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		rep = ExtentManager.getInstance("UpdateDestinationColumsForLoggedinUserTest", logFolder);
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
	

	@DataProvider(name="updateDestinationColumnsForLoggedInUser_201")
	public Object[][] updateDestinationColumnsForLoggedInUser_201(){
		
		return new Object[][] {
			{"direct", ti.direct_org1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
				new String[] { "true", "false","none"},new String[]{"2","3","10","1","4","7","5","6","9","8"},new String[] { "true"},new String[]{"4","7","5","6","9","8"},4,2},
			{"msp", ti.root_msp_org1_user1_token,  new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
				new String[] { "true", "false","none"},new String[]{"2","3","10","1","4","7","5","6","9","8"},new String[] { "false"},new String[]{"1","6","9","8"},4,3},
			{"suborg", ti.root_msp1_suborg1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"2","3","1","10","4","7","5","6","9","8"},new String[] { "true"},new String[]{"7","5","6","9","8"},4,1},
				
			{"suborg-mspT", ti.root_msp_org1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"2","3","1","10","4","7","5","6","9","8"},new String[] { "false"},new String[]{"7","5","6","9","8"},4,2},
			{"suborg-mspAccAdminT", ti.root_msp_org1_msp_accountadmin1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
						new String[] { "true", "false","none"},new String[]{"2","3","1","10","4","7","5","6","9","8"},new String[] { "false"},new String[]{"7","5","6","9","8"},4,2}
		};
	}

	@Test(dataProvider="updateDestinationColumnsForLoggedInUser_201")
	public void updateDestinationColumnsForLoggedInUser_201(String organizationType,
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
		spogDestinationServer.setToken(validToken);
		
		ArrayList<HashMap<String,Object>> expected_columns = new ArrayList<>();
		ArrayList<HashMap<String, Object>> expected_updated_columns = new ArrayList<>();
		HashMap<String,Object> temp = new HashMap<>() ;
		HashMap<String, Object> temp1 = new HashMap<>();

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
		test.log(LogStatus.INFO, "Create destination columns for Loggedin user in org : "+ organizationType);
		Response response = spogDestinationServer.createDestinationColumnsForLoggedInUser(validToken, expected_columns, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		for(int i=0;i<noofcolumnstobeupdated ;i++)
		{
			int index3 = gen_random_index(visible);
			//int index4 = gen_random_index(orderId);
			
			temp1 = spogDestinationServer.composedestination_Column(columnIdList.get(i),null,null,visible[index3],updatedOrderId[i]);
			expected_updated_columns.add(temp1);
		}
		
		test.log(LogStatus.INFO, "Update destination columns for Loggedin User in org : "+organizationType);
		spogDestinationServer.updateDestinationColumnsForLoggedinUser(validToken, expected_updated_columns, columnsHeadContent, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		
		test.log(LogStatus.INFO, "Delete destination columns for Loggedin user in org : " + organizationType);
		spogDestinationServer.deleteDestinatinColumnsforLoggedInUserwithCheck(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
	}
	@DataProvider(name="UpdateDestinationColumns_400")
	public final Object[][] UpdateDestinationColumns_400() {
		
		return new Object[][] {
			// different users
			
				{ "direct-noColId", ti.direct_org1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
						new String[] { "true", "false","none"},new String[]{"2","3","1","10","4","7","5","6","9","8"},4},
				
				{ "direct-invalidOrderId_0", ti.direct_org1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
								new String[] { "true", "false","none"},new String[]{"2","10","3","1","4","7","5","6","9","8"},2},
				
				{ "direct-invalidOrderId_1", ti.direct_org1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
									new String[] { "true", "false","none"},new String[]{"2","10","3","1","4","7","5","6","9","8"},2},				
				
				{ "direct-moreOrderIds", ti.direct_org1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
										new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6","9","8","10"},10},
		
		};
	}
	@Test(dataProvider="UpdateDestinationColumns_400")
	public void updateDestinationColumnsByUserIdTest_400(String organizationType,
			  												String validToken,
			  												String[] sort,
			  												String[] filter,
			  												String[] visible,
			  												String[] orderId,
			  												int noofcolstocreate
			  												) {
		
			String[] updateOrderId_0 = new String[]{"0","2","3"};
			String[] updateOrderId_1 = new String[]{"-1","3","5"};
			String[] moreOrderIds = new String[]{"2","11","1","10","7","5","6","9","8","4","12","3"};
			
		  	SpogMessageCode expectedErrorMessage;
			test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
			
			test.assignAuthor("Rakesh.Chalamala");
			spogDestinationServer.setToken(validToken);
			
			
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
				
				temp = spogDestinationServer.composedestination_Column(columnIdList.get(i), sort[index1], filter[index2], visible[index3], orderId[i]);
				
				expected_columns.add(temp);
			}

			test.log(LogStatus.INFO, "Create destination columns for Loggedin user in the org: "+organizationType);
			Response response= spogDestinationServer.createDestinationColumnsForLoggedInUser(validToken, expected_columns, test);
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);
			
			for(int i=0;i<noofcolstocreate ;i++)
			{
				int index1 = gen_random_index(sort);
				int index2 = gen_random_index(filter);
				int index3 = gen_random_index(visible);
//				int index4 = gen_random_index(orderId);
				if(organizationType=="direct-noColId")
				{
					temp1 = spogDestinationServer.composedestination_Column("", sort[index1], filter[index2], visible[index3], orderId[i]);
				}
				else if(organizationType.contains("direct-invalidOrderId_0")) {
					temp1 = spogDestinationServer.composedestination_Column(columnIdList.get(i), sort[index1], filter[index2], visible[index3], updateOrderId_0[i]);
				}
				else if(organizationType.contains("direct-invalidOrderId_1")) {
					temp1 = spogDestinationServer.composedestination_Column(columnIdList.get(i), sort[index1], filter[index2], visible[index3], updateOrderId_1[i]);
				}
				else if(organizationType=="direct-moreOrderIds")
				{
					temp1 = spogDestinationServer.composedestination_Column(columnIdList.get(i), sort[index1], filter[index2], visible[index3], moreOrderIds[i]);
				}
				else {
					temp1 = spogDestinationServer.composedestination_Column(columnIdList.get(i), sort[index1], filter[index2], visible[index3], orderId[i]);
				}
				expected_columns1.add(temp1);
			}
			
			if(organizationType=="direct-sameorderid") {
				expectedErrorMessage = SpogMessageCode.ORDER_ID_ALREADY_EXIST;
			}
			else if (organizationType.contains("direct-sameColId")) {
				expectedErrorMessage = SpogMessageCode.COLUMN_ID_ALREADY_EXIST;
			}
			else if (organizationType.contains("direct-invalidOrderId")){  
				expectedErrorMessage = SpogMessageCode.ORDER_ID_ATLEAST_1;
			}
			else if(organizationType=="direct-moreOrderIds"){
				expectedErrorMessage = SpogMessageCode.ORDER_ID_SHOULD_BE_LESS_THAN_MAX_COUNT;
			}
			else {
				expectedErrorMessage = SpogMessageCode.COLUMN_CANNOT_BLANK;
			}
			
			test.log(LogStatus.INFO, "Update the destination columns created for Loggedin user in org: "+organizationType);
			spogDestinationServer.updateDestinationColumnsForLoggedinUser(validToken, expected_columns1, columnsHeadContent, SpogConstants.REQUIRED_INFO_NOT_EXIST, expectedErrorMessage, test);
									
			test.log(LogStatus.INFO, "Delete destination columns for Loggedin user in org: "+organizationType);
			spogDestinationServer.deleteDestinatinColumnsforLoggedInUserwithCheck(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
						
		}
	@DataProvider(name="UpdateDestinationColumnsForLoggedinUser_401")
	public final Object[][] UpdateDestinationColumnsForLoggedinUser_401() {
		
		return new Object[][] {
			// different users
				{ "direct", ti.direct_org1_user1_token,"invalid_token",new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"2","3","1","4","10","7","5","6","9","8"},4},
								
				{ "msp", ti.root_msp_org1_user1_token,"invalid_token",new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"3","6","2","9","8","1","10","4","5","7"},7},
				
				{ "suborg", ti.root_msp1_suborg1_user1_token,"invalid_token",new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"5","2","9","4","1","6","10","7","8","3"},1},
						
		};
	}
	@Test(dataProvider="UpdateDestinationColumnsForLoggedinUser_401")
	public void updateDestinationColumnsForLoggedinUserTest_401(String organizationType,
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
		
		test.log(LogStatus.INFO, "Create the destination columns for the Loggedin user in the org: "+organizationType);
		Response response= spogDestinationServer.createDestinationColumnsForLoggedInUser(validToken, expected_columns, test);
			
		test.log(LogStatus.INFO, "Update the destination columns for the Loggedin user in the org: "+organizationType);
		spogDestinationServer.updateDestinationColumnsForLoggedinUser(invalidToken, expected_columns, columnsHeadContent, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);
					
		test.log(LogStatus.INFO, "Update the destination columns for the Loggedin user in the org: "+organizationType);
		spogDestinationServer.updateDestinationColumnsForLoggedinUser("", expected_columns, columnsHeadContent, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);
						
		test.log(LogStatus.INFO, "Delete the destination columns for the Loggedin user in the org: "+organizationType);
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
