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

public class CreateRecoveredResourceColumnsForLoggedInUserTest extends base.prepare.Is4Org{
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
	public void beforeClass(String baseURI, String port,String logFolder,String runningMachine,String buildVersion) {
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
	
	@DataProvider(name="CreateRecoveredResourceColumns_201")
	public final Object[][] CreateRecoveredResourceColumns_201() {
		
		return new Object[][] {
			// different users
				{ "csr", ti.csr_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"2","10","11","3","1","4","7","5","6","9","8"},4},
				{ "csr_readonly", ti.csr_readonly_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"2","10","11","3","1","4","7","5","6","9","8"},4},
			
			
				{ "direct", ti.direct_org1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"2","10","11","3","1","4","7","5","6","9","8"},4},		
				{ "direct-csrT", ti.csr_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
						new String[] { "true", "false","none"},new String[]{"2","10","11","3","1","4","7","5","6","9","8"},4},
				//visible default true and false
				{ "direct", ti.direct_org1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
							new String[] { "true"},new String[]{"2","3","1","10","11","4","7","5","6","9","8"},4},
				{ "direct", ti.direct_org1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
								new String[] {"false"},new String[]{"2","3","10","11","1","4","7","5","6","9","8"},4},
				
				{ "msp", ti.root_msp_org1_user1_token, new String[] { "true", "false"}, new String[] { "true", "false"},
							new String[] { "true", "false"},new String[]{"3","10","11","6","2","9","8","1","4","5","7"},7},
				{ "msp-csrT", ti.csr_token, new String[] { "true", "false"}, new String[] { "true", "false"},
								new String[] { "true", "false"},new String[]{"3","10","11","6","2","9","8","1","4","5","7"},7},
				
				{ "suborg", ti.root_msp1_suborg1_user1_token, new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"3","2","1","4","10","11","5","7","6","8","9"},1},
				{ "suborg-mspT", ti.root_msp_org1_user1_token, new String[] { "true", "false"}, new String[] { "true", "false"},
						new String[] { "true", "false"},new String[]{"3","2","1","10","11","4","5","7","6","8","9"},1},
				{ "suborg-csrT", ti.csr_token, new String[] { "true", "false"}, new String[] { "true", "false"},
							new String[] { "true", "false"},new String[]{"3","2","10","11","1","4","5","7","6","8","9"},1},
				{ "suborg-mspAccAdminT", ti.root_msp_org1_msp_accountadmin1_token, new String[] { "true", "false"}, new String[] { "true", "false"},
								new String[] { "true", "false"},new String[]{"3","2","10","11","1","4","5","7","6","8","9"},1},
				
//				Delete the existing user recovered resource columns and create the same columns 
				{ "direct-deleteCreateSame", ti.direct_org1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
								new String[] { "true", "false","none"},new String[]{"2","10","11","3","1","4","7","5","6","9","8"},4},
				
				{ "direct-sameColId", ti.direct_org1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
									new String[] { "true", "false","none"},new String[]{"2","10","11","3","1","4","7","5","6","9","8"},2},
				
				{ "direct-sameColIdOrderId", ti.direct_org1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
									new String[] { "true", "false","none"},new String[]{"2","2","2","2","2","3","3","3","2","2"},2},
				
				{ "direct-sameorderid", ti.direct_org1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
										new String[] { "true", "false","none"},new String[]{"2","3","2","2","3","3","2","3","8"},4},
				
		};
	}
	@Test(dataProvider="CreateRecoveredResourceColumns_201")
	public void createRecoveredResourceColumnsForLoggedinUserTest_201(String organizationType,
			  												String validToken,
			  												String[] sort,
			  												String[] filter,
			  												String[] visible,
			  												String[] orderId, 
			  												int noofcolstocreate
			  												) {
		  
			test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
			
			test.assignAuthor("Rakesh.Chalamala");
			spogRecoveredResourceServer.setToken(validToken);
			
			ArrayList<HashMap<String,Object>> expected_columns = new ArrayList<>();
			ArrayList<HashMap<String,Object>> expected_columns1 = new ArrayList<>();
			HashMap<String,Object> temp = new HashMap<>() ;

			for(int i=0;i<noofcolstocreate ;i++)
			{
				int index1 = gen_random_index(sort);
				int index2 = gen_random_index(filter);
				int index3 = gen_random_index(visible);
//				int index4 = gen_random_index(orderId);
				
				if(organizationType.contains("direct-sameColId"))
				{
					temp = spogRecoveredResourceServer.composeRecoveredResource_Column(columnIdList.get(0), sort[index1], filter[index2], visible[index3], orderId[i]);
					if(i!=1) {
					expected_columns1.add(temp);
					}
				}
				else {
					temp = spogRecoveredResourceServer.composeRecoveredResource_Column(columnIdList.get(i), sort[index1], filter[index2], visible[index3], orderId[i]);
				}
				expected_columns.add(temp);
			}

			test.log(LogStatus.INFO, "Create RecoveredResource columns for Loggedin user in the org: "+organizationType);
			Response response= spogRecoveredResourceServer.createRecoveredResourceColumnsForLoggedInUser(validToken, expected_columns, test);
			
			test.log(LogStatus.INFO, "Compare the RecoveredResource columns created for loggedin user in org: "+organizationType);
			if(organizationType.contains("direct-sameColId"))
			{
				spogRecoveredResourceServer.CompareRecoveredResourceColumnsContent(response, expected_columns1, columnsHeadContent, SpogConstants.SUCCESS_POST, null, test);
			}
			else {
			spogRecoveredResourceServer.CompareRecoveredResourceColumnsContent(response, expected_columns, columnsHeadContent, SpogConstants.SUCCESS_POST, null, test);
			}
			
			test.log(LogStatus.INFO, "delete RecoveredResource columns for loggedin user in org: "+organizationType);
			spogRecoveredResourceServer.deleteRecoveredResourceColumnsforLoggedInUserwithCheck(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			
//			Delete the existing user job columns and create the same columns 
			if(organizationType=="direct-deleteCreateSame")
			{
				test.log(LogStatus.INFO, "Create RecoveredResource columns for loggedin user in the org: "+organizationType);
				response= spogRecoveredResourceServer.createRecoveredResourceColumnsForLoggedInUser(validToken, expected_columns, test);
				
				test.log(LogStatus.INFO, "Compare the RecoveredResource columns created for loggedin user in org: "+organizationType);
				spogRecoveredResourceServer.CompareRecoveredResourceColumnsContent(response, expected_columns, columnsHeadContent, SpogConstants.SUCCESS_POST, null, test);
				
				test.log(LogStatus.INFO, "delete RecoveredResource columns for loggedin user in org: "+organizationType);
				spogRecoveredResourceServer.deleteRecoveredResourceColumnsforLoggedInUserwithCheck(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
				
			}
	}

	@DataProvider(name="CreateRecoveredResourceColumns_400")
	public final Object[][] CreateRecoveredResourceColumns_400() {

		return new Object[][] {
			// different users		
			{ "direct-noColId", ti.direct_org1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
				new String[] { "true", "false","none"},new String[]{"2","3","10","11","1","4","7","5","6","9","8"},4},
			{ "direct-invalidOrderId", ti.direct_org1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"0","-1"},2},
			{ "direct-moreOrderIds", ti.direct_org1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
						new String[] { "true", "false","none"},new String[]{"2","13","1","10","7","5","6","9","8","4","11","3"},10},

			{ "msp-noColId", ti.root_msp_org1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
							new String[] { "true", "false","none"},new String[]{"2","3","10","11","1","4","7","5","6","9","8"},4},
			{ "msp-invalidOrderId", ti.root_msp_org1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
								new String[] { "true", "false","none"},new String[]{"0","-1"},2},
			{ "msp-moreOrderIds", ti.root_msp_org1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
									new String[] { "true", "false","none"},new String[]{"2","13","1","10","7","5","6","9","8","4","11","3"},10},

			{ "suborg-noColId", ti.root_msp1_suborg1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
										new String[] { "true", "false","none"},new String[]{"2","3","10","11","1","4","7","5","6","9","8"},4},
			{ "suborg-invalidOrderId", ti.root_msp1_suborg1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
											new String[] { "true", "false","none"},new String[]{"0","-1"},2},
			{ "suborg-moreOrderIds", ti.root_msp1_suborg1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
												new String[] { "true", "false","none"},new String[]{"2","13","1","10","7","5","6","9","8","4","11","3"},10},

			{ "mspAccAdmin-noColId", ti.root_msp_org1_msp_accountadmin1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
													new String[] { "true", "false","none"},new String[]{"2","3","10","11","1","4","7","5","6","9","8"},4},
			{ "mspAccAdmin-invalidOrderId", ti.root_msp_org1_msp_accountadmin1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
														new String[] { "true", "false","none"},new String[]{"0","-1"},2},
			{ "mspAccAdmin-moreOrderIds", ti.root_msp_org1_msp_accountadmin1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
															new String[] { "true", "false","none"},new String[]{"2","13","1","10","7","5","6","9","8","4","11","3"},10},

			{ "csr-noColId", ti.csr_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
																new String[] { "true", "false","none"},new String[]{"2","3","10","11","1","4","7","5","6","9","8"},4},
			{ "csr-invalidOrderId", ti.csr_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
																	new String[] { "true", "false","none"},new String[]{"0","-1"},2},
			{ "csr-moreOrderIds", ti.csr_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
																		new String[] { "true", "false","none"},new String[]{"2","13","1","10","7","5","6","9","8","4","11","3"},10},

			{ "csr_readonly-noColId", ti.csr_readonly_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
																			new String[] { "true", "false","none"},new String[]{"2","3","10","11","1","4","7","5","6","9","8"},4},
			{ "csr_readonly-invalidOrderId", ti.csr_readonly_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
																				new String[] { "true", "false","none"},new String[]{"0","-1"},2},
			{ "csr_readonly-moreOrderIds", ti.csr_readonly_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
																					new String[] { "true", "false","none"},new String[]{"2","13","1","10","7","5","6","9","8","4","11","3"},10},
		};
	}
	@Test(dataProvider="CreateRecoveredResourceColumns_400")
	public void createRecoveredResourceColumnsForLoggedinUserTest_400(String organizationType,
			  												String validToken,
			  												String[] sort,
			  												String[] filter,
			  												String[] visible,
			  												String[] orderId, 
			  												int noofcolstocreate
			  												) {
		  	SpogMessageCode expectedErrorMessage;
			test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
			
			test.assignAuthor("Rakesh.Chalamala");
			spogRecoveredResourceServer.setToken(validToken);
			
			
			ArrayList<HashMap<String,Object>> expected_columns = new ArrayList<>();
			HashMap<String,Object> temp = new HashMap<>();

			for(int i=0;i<noofcolstocreate ;i++)
			{
				int index1 = gen_random_index(sort);
				int index2 = gen_random_index(filter);
				int index3 = gen_random_index(visible);
//				int index4 = gen_random_index(orderId);
				if(organizationType.contains("-noColId"))
				{
				temp = spogRecoveredResourceServer.composeRecoveredResource_Column("", sort[index1], filter[index2], visible[index3], orderId[i]);
				}
				else {
					temp = spogRecoveredResourceServer.composeRecoveredResource_Column(columnIdList.get(i), sort[index1], filter[index2], visible[index3], orderId[i]);
				}
				expected_columns.add(temp);
			}

			test.log(LogStatus.INFO, "Create RecoveredResource columns for Loggedin user in the org: "+organizationType);
			Response response= spogRecoveredResourceServer.createRecoveredResourceColumnsForLoggedInUser(validToken, expected_columns, test);
			
			if (organizationType.contains("-noColId")) {
				expectedErrorMessage = SpogMessageCode.COLUMN_CANNOT_BLANK;
			}
			else if (organizationType.contains("-invalidOrderId")){  
				expectedErrorMessage = SpogMessageCode.ORDER_ID_ATLEAST_1;
			}
			else if(organizationType.contains("-moreOrderIds"))
			{
				expectedErrorMessage = SpogMessageCode.ORDER_ID_SHOULD_BE_LESS_THAN_MAX_COUNT;
			}
			else {
				expectedErrorMessage = SpogMessageCode.COLUMN_CANNOT_BLANK;
			}
			
			test.log(LogStatus.INFO, "Compare the RecoveredResource columns created for Loggedin user in org: "+organizationType);
			spogRecoveredResourceServer.CompareRecoveredResourceColumnsContent(response, expected_columns, columnsHeadContent, SpogConstants.REQUIRED_INFO_NOT_EXIST, expectedErrorMessage, test);
		}
	
	@DataProvider(name="CreateRecoveredResourceColumns_401")
	public final Object[][] CreateRecoveredResourceColumns_401() {
		
		return new Object[][] {
			// different users
				{ "direct-invalidT", "invalid", new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"2","10","11","3","1","4","7","5","6","9","8"},4},
				{ "direct-missingT", "", new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
						new String[] { "true", "false","none"},new String[]{"2","3","10","11","1","4","7","5","6","9","8"},4},
		};
	}
	@Test(dataProvider="CreateRecoveredResourceColumns_401")
	public void createRecoveredResourceColumnsForLoggedinUserTest_401(String organizationType,
			  												String validToken,
			  												String[] sort,
			  												String[] filter,
			  												String[] visible,
			  												String[] orderId, 
			  												int noofcolstocreate
			  												) {
			SpogMessageCode expectedErrorMessage;
			test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
			
			test.assignAuthor("Rakesh.Chalamala");
			spogRecoveredResourceServer.setToken(validToken);
			
			
			ArrayList<HashMap<String,Object>> expected_columns = new ArrayList<>();
			HashMap<String,Object> temp = new HashMap<>() ;

			for(int i=0;i<noofcolstocreate ;i++)
			{
				int index1 = gen_random_index(sort);
				int index2 = gen_random_index(filter);
				int index3 = gen_random_index(visible);
//				int index4 = gen_random_index(orderId);
				temp = spogRecoveredResourceServer.composeRecoveredResource_Column(columnIdList.get(i), sort[index1], filter[index2], visible[index3], orderId[i]);
				expected_columns.add(temp);
			}

			test.log(LogStatus.INFO, "Create RecoveredResource columns for Loggedin user in the org: "+organizationType);
			Response response= spogRecoveredResourceServer.createRecoveredResourceColumnsForLoggedInUser(validToken, expected_columns, test);
			
			if(validToken=="invalid") {
				expectedErrorMessage=SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT;
			}
			else {
				expectedErrorMessage=SpogMessageCode.COMMON_AUTHENTICATION_FAILED;
			}
			
			test.log(LogStatus.INFO, "Compare the RecoveredResource columns created for Loggedin user in org: "+organizationType);
			spogRecoveredResourceServer.CompareRecoveredResourceColumnsContent(response, expected_columns, columnsHeadContent, SpogConstants.NOT_LOGGED_IN, expectedErrorMessage, test);
							
	}
	
	
	@DataProvider(name="CreateRecoveredResourceColumns_404")
	public final Object[][] CreateRecoveredResourceColumns_404() {
		
		return new Object[][] {
				{ "csr-colIdNotExist", ti.csr_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
						new String[] { "true", "false","none"},new String[]{"2","3","1","10","11","4","7","5","6","9","8"},1},
				{ "csr_readonly-colIdNotExist", ti.csr_readonly_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
						new String[] { "true", "false","none"},new String[]{"2","3","1","10","11","4","7","5","6","9","8"},1},
			
				{ "direct-colIdNotExist", ti.direct_org1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
						new String[] { "true", "false","none"},new String[]{"2","3","1","10","11","4","7","5","6","9","8"},1},
				{ "msp-colIdNotExist", ti.root_msp_org1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
							new String[] { "true", "false","none"},new String[]{"2","3","1","10","11","4","7","5","6","9","8"},1},
				{ "suborg-colIdNotExist", ti.root_msp1_suborg1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
								new String[] { "true", "false","none"},new String[]{"2","3","10","11","1","4","7","5","6","9","8"},1},
								
		};
	}
	@Test(dataProvider="CreateRecoveredResourceColumns_404")
	public void createRecoveredResourceColumnsForLoggedinUserTest_404(String organizationType,
			  												String validToken,
			  												String[] sort,
			  												String[] filter,
			  												String[] visible,
			  												String[] orderId, 
			  												int noofcolstocreate
			  												) {
		  
			test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		
			
			test.assignAuthor("Rakesh.Chalamala");
			spogRecoveredResourceServer.setToken(validToken);
			
			
			ArrayList<HashMap<String,Object>> expected_columns = new ArrayList<>();
			HashMap<String,Object> temp = new HashMap<>() ;
			String columnid = UUID.randomUUID().toString();

			for(int i=0;i<noofcolstocreate ;i++)
			{
				int index1 = gen_random_index(sort);
				int index2 = gen_random_index(filter);
				int index3 = gen_random_index(visible);
//				int index4 = gen_random_index(orderId);
				
				if(organizationType.contains("colIdNotExist"))
				{
					temp = spogRecoveredResourceServer.composeRecoveredResource_Column(columnid, sort[index1], filter[index2], visible[index3], orderId[i]);
				}
				else {
					temp = spogRecoveredResourceServer.composeRecoveredResource_Column(columnIdList.get(i), sort[index1], filter[index2], visible[index3], orderId[i]);	
				}
				expected_columns.add(temp);
			}

			test.log(LogStatus.INFO, "Create RecoveredResource columns for Loggedin user in the org: "+organizationType);
			Response response= spogRecoveredResourceServer.createRecoveredResourceColumnsForLoggedInUser(validToken, expected_columns, test);
			
			test.log(LogStatus.INFO, "Compare the RecoveredResource columns created for Loggedin user in org: "+organizationType);
			spogRecoveredResourceServer.CompareRecoveredResourceColumnsContent(response, expected_columns, columnsHeadContent, SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.COLUMN_ID_DOESNOT_EXIST, test);
			
					
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
