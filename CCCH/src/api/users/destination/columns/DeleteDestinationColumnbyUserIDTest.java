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

public class DeleteDestinationColumnbyUserIDTest extends base.prepare.Is4Org {
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
	
  /*  private ExtentReports rep;
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
		rep = ExtentManager.getInstance("DeleteDestinationColumnbyUserIDTest", logFolder);
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
	public final Object[][] Delete_destination_coloumns_valid() {
		
		return new Object[][] {
			// different users
				{ "csr", ti.csr_org_id,ti.csr_token,ti.csr_admin_user_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"2","4","3","1","5","13","7","5","10","6","9","12","8"},4},
				{ "csr_readonly", ti.csr_org_id,ti.csr_readonly_token,ti.csr_readonly_admin_user_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"2","4","3","1","5","13","7","5","10","6","9","12","8"},4},
			
				{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"2","4","3","1","5","13","7","5","10","6","9","12","8"},4},
				{ "msp", ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"1","2","3","4","6","7","8","5","10","14","6","9","12","8"},7},
				{ "suborg", ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"1","4","2","8","3","13","7","5","10","6","9","14"},1},
				{ "suborg-mspT", ti.root_msp1_suborg1_id,ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
						new String[] { "true", "false"},new String[]{"1","4","2","8","3","13","7","5","10","6","9","14"},1},
				{ "suborg-mspAccAdminT", ti.root_msp1_suborg1_id,ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
						new String[] { "true", "false"},new String[]{"1","4","2","8","3","13","7","5","10","6","9","14"},1},
				
		};
	}
	
	@Test(dataProvider = "Delete_destination_coloumns_valid")
	public void deleteDestinationColumnsForSpecifiedUser_valid(String organizationType,
												 String org_Id,
												 String validToken,
												 String user_Id,
												 String[] sort,
												 String[] filter,
												 String[] visible,
												 String[] orderId, 
												 int noofcolumnstobecreated 
												 ){	 
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
		
		test.log(LogStatus.INFO, "Create the destination columns for the specified user in the org: "+organizationType);
		Response response= spogDestinationServer.createDestinationColumnsForSpecifiedUser(user_Id,validToken,expected_columns,test);
		
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		
		test.log(LogStatus.INFO, "Delete the destination columns for the specified user of the org: "+organizationType);	
		spogDestinationServer.deleteDestinationColumnsforSpecifiedUserwithCheck(user_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		
		
	}
	
	@Test(dataProvider = "Delete_destination_coloumns_valid")
	public void deleteDestinationColumnsForSpecifiedUser_401(String organizationType,
												 String org_Id,
												 String validToken,
												 String user_Id,
												 String[] sort,
												 String[] filter,
												 String[] visible,
												 String[] orderId, 
												 int noofcolumnstobecreated 
												 ){	 
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
			int index4 = gen_random_index(orderId);
			
			temp = spogDestinationServer.composedestination_Column(columnIdList.get(i),sort[index1],filter[index2],visible[index3],orderId[i+1]);
			expected_columns.add(temp);
		}
		
		/*test.log(LogStatus.INFO, "Create the destination columns for the specified user in the org: "+organizationType);
		Response response= spogDestinationServer.createDestinationColumnsForSpecifiedUser(user_Id,validToken,expected_columns,test);*/
			    
		test.log(LogStatus.INFO, "Delete the destination columns for the specified user in the org: "+organizationType);	
		spogDestinationServer.deleteDestinationColumnsforSpecifiedUserwithCheck(user_Id, validToken+"A", SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);
		
		test.log(LogStatus.INFO, "Delete the destination columns for the specified user in the org: "+organizationType);	
		spogDestinationServer.deleteDestinationColumnsforSpecifiedUserwithCheck(user_Id,"", SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);
		
	}
	
	@DataProvider(name = "Delete_destination_coloumns_403")
	public final Object[][] Delete_destination_coloumns_403() {
		
		return new Object[][] {
			{ "direct-mspT", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id,ti.root_msp_org1_user1_token, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
				new String[] { "true", "false","none"},new String[]{"1","2","3","4","5","7","5","10","14","6","9","12","8"},5},
			{ "direct-suborgT", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_suborg1_user1_token,new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"1","2","3","4","5","7","6","7","8","9","14","12","10"},5},
			
			{ "msp-directT", ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id,ti.direct_org1_user1_token, new String[] { "true", "false"}, new String[] { "true", "false"},
						new String[] { "true", "false"},new String[]{"1","2","3","4","5","7","5","10","14","6","9","12","8"},6},
			{ "msp-suborgT", ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id,ti.root_msp1_suborg1_user1_token, new String[] { "true", "false"}, new String[] { "true", "false"},
							new String[] { "true", "false"},new String[]{"1","2","3","4","5","7","5","10","14","6","9","12","8"},6},
			{ "msp-submspT", ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id,ti.root_msp1_submsp1_user1_token, new String[] { "true", "false"}, new String[] { "true", "false"},
								new String[] { "true", "false"},new String[]{"1","2","3","4","5","7","5","10","14","6","9","12","8"},6},
			{ "msp-submspsuborgT", ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id,ti.msp1_submsp1_suborg1_user1_token, new String[] { "true", "false"}, new String[] { "true", "false"},
									new String[] { "true", "false"},new String[]{"1","2","3","4","5","7","5","10","14","6","9","12","8"},6},

			{ "submsp-directT", ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp1_user1_id,ti.direct_org1_user1_token, new String[] { "true", "false"}, new String[] { "true", "false"},
										new String[] { "true", "false"},new String[]{"1","2","3","4","5","7","5","10","14","6","9","12","8"},6},
			{ "submsp-suborgT", ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp1_user1_id,ti.root_msp1_suborg1_user1_token, new String[] { "true", "false"}, new String[] { "true", "false"},
											new String[] { "true", "false"},new String[]{"1","2","3","4","5","7","5","10","14","6","9","12","8"},6},
			{ "submsp-submspT", ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp1_user1_id,ti.root_msp_org1_user1_token, new String[] { "true", "false"}, new String[] { "true", "false"},
												new String[] { "true", "false"},new String[]{"1","2","3","4","5","7","5","10","14","6","9","12","8"},6},
			{ "submsp-submspsuborgT", ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp1_user1_id,ti.msp1_submsp1_suborg1_user1_token, new String[] { "true", "false"}, new String[] { "true", "false"},
													new String[] { "true", "false"},new String[]{"1","2","3","4","5","7","5","10","14","6","9","12","8"},6},
			
			{ "suborg-suborg_bT", ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id,ti.root_msp1_suborg2_user1_token, new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"1","2","3","4","5","7","5","10","14","6","9","12","8"},4},
			{ "suborg-msp_bT", ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id,ti.root_msp_org2_user1_token, new String[] { "true", "false"}, new String[] { "true", "false"},
						new String[] { "true", "false"},new String[]{"1","2","3","4","5","7","5","10","14","6","9","12","8"},4},
			{ "suborg-directT", ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id,ti.direct_org1_user1_token, new String[] { "true", "false"}, new String[] { "true", "false"},
							new String[] { "true", "false"},new String[]{"1","2","3","4","5","7","5","10","14","6","9","12","8"},4},
			{ "suborgb-mspAccAdminT", ti.root_msp1_suborg2_id,ti.root_msp1_suborg2_user1_token,ti.root_msp1_suborg2_user1_id,ti.root_msp_org1_msp_accountadmin1_token, new String[] { "true", "false"}, new String[] { "true", "false"},
								new String[] { "true", "false"},new String[]{"1","2","3","4","5","7","5","10","14","6","9","12","8"},4},
			
			{ "csr-directT", ti.csr_org_id,ti.csr_token,ti.csr_admin_user_id,ti.direct_org1_user1_token, new String[] { "true", "false"}, new String[] { "true", "false"},
								new String[] { "true", "false"},new String[]{"1","2","3","4","5","7","5","10","14","6","9","12","8"},4},
			{ "csr-msp_bT", ti.csr_org_id,ti.csr_token,ti.csr_admin_user_id,ti.root_msp_org2_user1_token, new String[] { "true", "false"}, new String[] { "true", "false"},
									new String[] { "true", "false"},new String[]{"1","2","3","4","5","7","5","10","14","6","9","12","8"},4},
			{ "csr-suborgT", ti.csr_org_id,ti.csr_token,ti.csr_admin_user_id,ti.root_msp1_suborg1_user1_token, new String[] { "true", "false"}, new String[] { "true", "false"},
										new String[] { "true", "false"},new String[]{"1","2","3","4","5","7","5","10","14","6","9","12","8"},4},	
	};
}
	
	@Test(dataProvider = "Delete_destination_coloumns_403")
	public void deleteDestinationColumnsForSpecifiedUser_403(String organizationType,
												 String org_Id,
												 String validToken,
												 String user_Id,
												 String Othertoken,
												 String[] sort,
												 String[] filter,
												 String[] visible,
												 String[] orderId, 
												 int noofcolumnstobecreated 
												 ){	 
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
			int index4 = gen_random_index(orderId);
			
			temp = spogDestinationServer.composedestination_Column(columnIdList.get(i),sort[index1],filter[index2],visible[index3],orderId[i+1]);
			expected_columns.add(temp);
		}
		
		if(organizationType.contains("msp-")) {
			test.log(LogStatus.INFO, "Delete the destination columns for the specified user in the org: "+organizationType);	
			spogDestinationServer.deleteDestinationColumnsforSpecifiedUserwithCheck(user_Id, Othertoken, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY, test);
			
		}
		if(organizationType.contains("direct-") || organizationType.contains("suborg-")) {
			test.log(LogStatus.INFO, "Delete the destination columns for the specified user in the org: "+organizationType);	
			spogDestinationServer.deleteDestinationColumnsforSpecifiedUserwithCheck(user_Id, Othertoken, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY, test);
			
		}
		if(organizationType.contains("csr-msp")) {
			test.log(LogStatus.INFO, "Delete the destination columns for the specified user in the org: "+organizationType);	
			spogDestinationServer.deleteDestinationColumnsforSpecifiedUserwithCheck(user_Id, Othertoken, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY, test);	
		}
		else if(organizationType.contains("csr-")) {
			test.log(LogStatus.INFO, "Delete the destination columns for the specified user in the org: "+organizationType);	
			spogDestinationServer.deleteDestinationColumnsforSpecifiedUserwithCheck(user_Id, Othertoken, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY, test);
		}else {
			test.log(LogStatus.INFO, "Delete the destination columns for the specified user in the org: "+organizationType);	
			spogDestinationServer.deleteDestinationColumnsforSpecifiedUserwithCheck(user_Id, Othertoken, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY, test);
		}
			    
		test.log(LogStatus.INFO, "Delete the destination columns for the specified user in the org: "+organizationType);	
		spogDestinationServer.deleteDestinationColumnsforSpecifiedUserwithCheck(user_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE,null, test);
		
	}
	
	@Test(dataProvider = "Delete_destination_coloumns_valid")
	public void deleteDestinationColumnsForSpecifiedUser_400(String organizationType,
												 String org_Id,
												 String validToken,
												 String user_Id,
												 String[] sort,
												 String[] filter,
												 String[] visible,
												 String[] orderId, 
												 int noofcolumnstobecreated
												 ){	 
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
			int index4 = gen_random_index(orderId);
			
			temp = spogDestinationServer.composedestination_Column(columnIdList.get(i),sort[index1],filter[index2],visible[index3],orderId[i]);
			expected_columns.add(temp);
		}
		
		/*test.log(LogStatus.INFO, "Create the destination columns for the specified user in the org: "+organizationType);
		Response response= spogDestinationServer.createDestinationColumnsForSpecifiedUser(user_Id,validToken,expected_columns,test);*/
		
		String user_id2=UUID.randomUUID().toString();
		
		test.log(LogStatus.INFO, "Delete the destination columns for the specified user in the org invalid user_id");	
		spogDestinationServer.deleteDestinationColumnsforSpecifiedUserwithCheck(user_id2, validToken, SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.USER_ID_DOESNOT_EXIST, test);
		
		test.log(LogStatus.INFO, "Delete the destination columns for the specified user in the org user id is not UUID");	
		spogDestinationServer.deleteDestinationColumnsforSpecifiedUserwithCheck("INVALIDUSER", validToken, SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_USERID_IS_NOT_UUID, test);
		
		
	}
	
	@DataProvider(name = "Delete_destinatin_coloumns_404")
	public final Object[][] Delete_destinatin_coloumns_404() {
		
		return new Object[][] {
			// different users
				{ "csr", ti.csr_org_id,ti.csr_token,ti.csr_admin_user_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"1","2","3","4","5","6","7",},5},
				{ "csr_readonly", ti.csr_org_id,ti.csr_readonly_token,ti.csr_readonly_admin_user_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"1","2","3","4","5","6","7",},5},
			
				{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"1","2","3","4","5","6","7",},5},
				{ "msp", ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"1","2","3","4","5","6","7","8","9"},6},
				{ "suborg", ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"1","2","3","4","5","6","7","8","9"},4},
				{ "suborg-mspAccAdminT", ti.root_msp1_suborg1_id,ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp_org1_msp_accountadmin1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
						new String[] { "true", "false"},new String[]{"1","2","3","4","5","6","7","8","9"},4},
		
		};
	}
	
	@Test(dataProvider = "Delete_destinatin_coloumns_404")
	public void deleteDestinationColumnsForSpecifiedUser_valid_404(String organizationType,
												 String org_Id,
												 String validToken,
												 String user_Id,
												 String[] sort,
												 String[] filter,
												 String[] visible,
												 String[] orderId, 
												 int noofcolumnstobecreated 
												 ){	 
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
			int index4 = gen_random_index(orderId);
			
			temp = spogDestinationServer.composedestination_Column(columnIdList.get(i),sort[index1],filter[index2],visible[index3],orderId[i+1]);
			expected_columns.add(temp);
		}
		
		test.log(LogStatus.INFO, "Delete the destination columns for the specified user with random uuid in the org: "+organizationType);	
		spogDestinationServer.deleteDestinationColumnsforSpecifiedUserwithCheck(UUID.randomUUID().toString(), validToken, SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.USER_ID_DOESNOT_EXIST, test);

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
