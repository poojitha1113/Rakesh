package api.users.jobfilters;

import java.io.IOException;
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

import Constants.DestinationStatus;
import Constants.DestinationType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGJobServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;

public class DeleteJobFilterByFilterIDTest extends base.prepare.Is4Org{
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private SPOGJobServer spogJobServer;
	private UserSpogServer userSpogServer;
	private TestOrgInfo ti;	
	//public int Nooftest;
	//private ExtentReports rep;
	private ExtentTest test;
	
	
	private String org_model_prefix = this.getClass().getSimpleName();
	private String site_version="1.0.0";
	private String gateway_hostname="prasad";
	//used for test case count like passed,failed,remaining cases
	//private SQLServerDb bqdb1;
	//public int Nooftest;
	int passedcases=0;
	int failedcases=0;
	int skippedcases=0;
	//long creationTime;
	String buildnumber=null;
	//String BQame=null;
	//private testcasescount count1;
	
	
	
	LocalDate date = LocalDate.now();  
	LocalDate yesterday = date.minusDays(1);  
	LocalDate tomorrow = yesterday.plusDays(2);
	
	
	@BeforeClass
	@Parameters({ "baseURI", "port","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String logFolder,String runningMachine,String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogJobServer = new SPOGJobServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		rep = ExtentManager.getInstance("DeleteJobFilterByFilterIDTest", logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Prasad.Deverakonda";
		
		Nooftest=0;
		
		
		//Used for creating a build number with dateformat
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date=new java.util.Date();
		this.buildVersion=buildVersion+"_"+dateFormater.format(date);
		
		this.BQName=this.getClass().getSimpleName();
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
			    
			    ti = new TestOrgInfo(spogServer, test);
		}
		
	}
		
	
	@DataProvider(name = "create_job_filter_valid")
	public final Object[][] createJobFilterValidParams() {
		return new Object[][] {
			// different users
				{ "csr", ti.csr_org_id,ti.csr_token,ti.csr_admin_user_id, "active,finished", null,
					null, null, null, null,
					null, null, "filterName", "true" },
				{ "csr_readonly", ti.csr_org_id,ti.csr_readonly_token,ti.csr_readonly_admin_user_id, "active,finished", null,
					null, null, null, null,
					null, null, "filterName", "true" },
				{ "direct", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id, "active,finished", null,
						null, null, null, null,
						null, null, "filterName", "true" },
				{ "msp",ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id, "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()+10),"sourceName", "filterName", "true" },
				{ "suborg",ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token , ti.normal_msp1_suborg1_user1_id, "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()+10),"sourceName", "filterName", "true" },
				{ "mspAccAdmin",ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, "finished", UUID.randomUUID().toString(),
							UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()+10),"sourceName", "filterName", "true" },
				{"suborgmsptoken",ti.normal_msp1_suborg1_id, ti.normal_msp_org1_user1_token, ti.normal_msp1_suborg1_user1_id, "finished", UUID.randomUUID().toString(),
							UUID.randomUUID().toString(), "backup","custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()+10),"sourceName", "filterName", "true" },
				{"suborgmspAccAdminT",ti.normal_msp1_suborg1_id, ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp1_suborg1_user1_id, "finished", UUID.randomUUID().toString(),
								UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()+10),"sourceName", "filterName", "true" },
				
		};
	}
	@Test(dataProvider = "create_job_filter_valid")
	public void deleteJobFilterValid(String organizationType,
									 String organization_id,
									 String validToken,
									 String user_id, 
									 String jobStatus, 
									 String policyID, 
									 String resourceID, 
									 String jobType, 
									 String startTimeType,
									 String startTimeTSStart, 
									 String startTimeTSEnd, 
									 String sourceName, 
									 String filterName, 
									 String isDefault) 
	{
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		ArrayList<HashMap<String,Object>> expectedresponse = new ArrayList<>();
		HashMap<String,Object> expected_response = new HashMap<String,Object>();
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		
		spogJobServer.setToken(validToken);
		String filter_Id = spogJobServer.createJobFilterWithCheckEx(user_id, jobStatus, policyID, resourceID, jobType, startTimeType, startTimeTSStart, startTimeTSEnd, sourceName, prefix + filterName, isDefault, test);
	
		test.log(LogStatus.INFO, "Compose the expected job filter");
		expected_response = spogJobServer.composeExpectedJobFilter(filter_Id, prefix+filterName, user_id, organization_id, jobStatus, jobType, policyID, resourceID, startTimeTSStart, startTimeTSEnd, startTimeType, sourceName,isDefault,0);
		expectedresponse.add(expected_response);
		
		
		test.log(LogStatus.INFO,"Delete the job filter by filter ID");
		spogJobServer.deleteJobFilterByFilterID(user_id, filter_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		
	}
	
	
	@Test(dataProvider = "create_job_filter_valid")
	public void deleteJobFilter_invalidJWT_missingJWT(String organizationType,
									 String organization_id,
									 String validToken,
									 String user_id, 
									 String jobStatus, 
									 String policyID, 
									 String resourceID, 
									 String jobType, 
									 String startTimeType,
									 String startTimeTSStart, 
									 String startTimeTSEnd, 
									 String sourceName, 
									 String filterName, 
									 String isDefault) 
	{
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		ArrayList<HashMap<String,Object>> expectedresponse = new ArrayList<>();
		HashMap<String,Object> expected_response = new HashMap<String,Object>();
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		
		spogJobServer.setToken(validToken);
		String filter_Id = spogJobServer.createJobFilterWithCheckEx(user_id, jobStatus, policyID, resourceID, jobType, startTimeType,startTimeTSStart, startTimeTSEnd, sourceName, prefix + filterName, isDefault, test);
		
		test.log(LogStatus.INFO, "Compose the expected job filter");
		expected_response = spogJobServer.composeExpectedJobFilter(filter_Id, prefix+filterName, user_id, organization_id, jobStatus, jobType, policyID, resourceID, startTimeTSStart, startTimeTSEnd, startTimeType, sourceName,isDefault,0);
		expectedresponse.add(expected_response);
		
		test.log(LogStatus.INFO,"Delete the job filter by filter ID using invalid JWT");
		spogJobServer.deleteJobFilterByFilterID(user_id, filter_Id, validToken+"J", SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);
		
		test.log(LogStatus.INFO,"Delete the job filter by filter ID using missing JWT");
		spogJobServer.deleteJobFilterByFilterID(user_id, filter_Id, "", SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);
		
		test.log(LogStatus.INFO,"Delete the job filter by filter ID");
		spogJobServer.deleteJobFilterByFilterID(user_id, filter_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		
	}
	
	@DataProvider(name = "getjobfilterbyfilterId_403_404",parallel=false)
	public final Object[][] getjobfilterbyfilterId_403_404() {
		return new Object[][] {
			{ "direct_msp", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id, ti.normal_msp_org1_user1_token,
				ti.normal_msp_org1_user1_id,"active,finished", null,null, null, null, null,null, null, "filterName", "true" },
			{"direct_suborg", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id,
				ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id,"active,finished", null,null, null, null, null,null, null, "filterName", "true" },
			{"msp_direct",ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id, ti.direct_org1_user1_token,
				ti.direct_org1_user1_id, "active,finished", null,null, null, null, null,null, null, "filterName", "true" },
			{"msp_suborg",ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id, ti.normal_msp1_suborg1_user1_token,
				ti.normal_msp1_suborg1_user1_id,"active,finished", null,null, null, null, null,null, null, "filterName", "true" },
			{"msp_mspb", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id, ti.normal_msp_org2_user1_token,
				ti.normal_msp_org2_user1_id, "active,finished", null,null, null, null, null,null, null, "filterName", "true" },
			{"suborg_direct",ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id,
				ti.direct_org1_user1_token, ti.direct_org1_user1_id, "active,finished", null,null, null, null, null,null, null, "filterName", "true" },
			{"suborg_suborgb",ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg2_user1_token,
				ti.normal_msp1_suborg2_user1_id, "active,finished", null,null, null, null, null,null, null, "filterName", "true" },
			{"suborg_mspb",ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id,ti.normal_msp_org2_user1_token,
				ti.normal_msp_org2_user1_id,"active,finished", null,null, null, null, null,null, null, "filterName", "true" },
			{"direct_msp_csr",ti.direct_org1_id,ti.csr_token,ti.direct_org1_user1_id,ti.csr_token,ti.normal_msp_org1_user1_id,
			  "active,finished", null,null, null, null, null,null, null, "filterName", "true" },
			{"direct_csr",ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id,ti.direct_org1_user1_token,ti.csr_admin_user_id,
			  "active,finished", null,null, null, null, null,null, null, "filterName", "true" },
			{"msp_csr",ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,ti.normal_msp_org1_user1_token,ti.csr_admin_user_id,
			  "active,finished", null,null, null, null, null,null, null, "filterName", "true" },
			/*{"suborg",sub_org_Id,sub_orga_user_validToken,suborga_user_id,suborga_site_id,DestinationType.cloud_direct_volume.toString(),DestinationStatus.creating.toString(),"20.1","2","22","normal","host-abc","1M",
				"0","0","0","0","0","0",RandomStringUtils.randomAlphanumeric(4)+"prasad-test34","none",DestinationType.cloud_direct_volume.toString(),"none"},
			{"suborgusingmsptoken",sub_org_Id,msp_user_validToken,suborga_user_id,suborga_site_id,DestinationType.cloud_direct_volume.toString(),DestinationStatus.creating.toString(),"20.1","2","22","normal","host-abc","1M",
					"0","0","0","0","0","0",RandomStringUtils.randomAlphanumeric(4)+"prasad-test34",RandomStringUtils.randomAlphanumeric(4)+"prasad-test34","none","none"},*/
			
			
		};
	}
	
	@Test(dataProvider = "getjobfilterbyfilterId_403_404")
	public void deletejobfilterbyfilterId_403_otheroguserid(String organizationType,
			 								   String organization_id,
			 								   String validToken,
			 								   String user_id,
			 								   String otherorgtoken,
			 								   String otherorguserid,
			 								   String jobStatus, 
											   String policyID, 
											   String resourceID, 
											   String jobType, 
											   String startTimeTSStart, 
											   String startTimeTSEnd, 
											   String endTimeTSStart, 
											   String endTimeTSEnd, 
											   String filterName, 
											   String isDefault
											   ) {
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		String prefix = RandomStringUtils.randomAlphanumeric(4);
		HashMap<String,Object>expected_response = new HashMap<>();
		SpogMessageCode expected = null;
		int expectedstatuscode = SpogConstants.INSUFFICIENT_PERMISSIONS;
		
		spogJobServer.setToken(validToken);
		test.log(LogStatus.INFO, "Create the job filter in org "+organizationType);
		String filter_Id = spogJobServer.createJobFilterWithCheckEx(user_id, jobStatus, policyID, resourceID, jobType, startTimeTSStart, startTimeTSEnd, endTimeTSStart, endTimeTSEnd, prefix + filterName, isDefault, test);
		expected = SpogMessageCode.RESOURCE_PERMISSION_DENY;
		if(organizationType=="msp_suborg"||organizationType=="direct_msp_csr") {
			expected = SpogMessageCode.JOB_FILTER_NOT_FOUND_WITH_CURRENT_USER;
			expectedstatuscode=SpogConstants.RESOURCE_NOT_EXIST;
		}
		/*else if(organizationType=="direct_msp"||organizationType=="suborg_mspb"||organizationType=="direct_csr") {
			expected = SpogMessageCode.DIRECT_ADMIN_CANNOT_VIEW_MSP_CSR;
		}else if(organizationType=="msp_csr") {
			expected = SpogMessageCode.MSP_ADMIN_CANNOT_VIEW_CSR;
		}else {
			expected = SpogMessageCode.LOGIN_USER_ORG_NOT_SAME_AS_GET_USER;
			
		}
		expected = SpogMessageCode.RESOURCE_PERMISSION_DENY;*/
		test.log(LogStatus.INFO, "delete job filter by filter id where the filter Id and token are from organization "+organizationType + " and user id from other org "+organizationType);
		spogJobServer.deleteJobFilterByFilterID(otherorguserid, filter_Id, validToken, expectedstatuscode, expected, test);
		
		test.log(LogStatus.INFO, "delete job filter by filter id");
		spogJobServer.deleteJobFilterByFilterID(user_id, filter_Id, ti.csr_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		
	}
	
	@DataProvider(name = "getjobfilterbyfilterId_403_insufficientpermission",parallel=false)
	public final Object[][] getjobfilterbyfilterId_403_insufficientpermission() {
		return new Object[][] {
			{ "direct_msp", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id, ti.normal_msp_org1_user1_token,
				ti.normal_msp_org1_user1_id,"active,finished", null,null, null, null, null,null, null, "filterName", "true" },
			{"direct_suborg",ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id,ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id,
			  "active,finished", null,null, null, null, null,null, null, "filterName", "true" },
			{"msp_direct",ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id, ti.direct_org1_user1_token,
				ti.direct_org1_user1_id, "active,finished", null,null, null, null, null,null, null, "filterName", "true" },
			{"msp_suborg",ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id, ti.normal_msp1_suborg1_user1_token,
				ti.normal_msp1_suborg1_user1_id,"active,finished", null,null, null, null, null,null, null, "filterName", "true" },
			{"msp_mspb",ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id, ti.normal_msp_org2_user1_token,
				ti.normal_msp_org2_user1_id,"active,finished", null,null, null, null, null,null, null, "filterName", "true" },
			{"suborg_direct",ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id,
				ti.direct_org1_user1_token, ti.direct_org1_user1_id,"active,finished", null,null, null, null, null,null, null, "filterName", "true" },
			{"suborg_suborgb",ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg2_user1_token,
				ti.normal_msp1_suborg2_user1_id,"active,finished", null,null, null, null, null,null, null, "filterName", "true" },
			{"suborg_mspb",ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id,ti.normal_msp_org2_user1_token,
				ti.normal_msp_org2_user1_id,"active,finished", null,null, null, null, null,null, null, "filterName", "true" },
			{"suborgb_mspAccAdminT",ti.normal_msp1_suborg2_id,ti.normal_msp1_suborg2_user1_token,ti.normal_msp1_suborg2_user1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp1_suborg1_user1_id,
				  "active,finished", null,null, null, null, null,null, null, "filterName", "true" },
			
		};
	}
	@Test(dataProvider = "getjobfilterbyfilterId_403_insufficientpermission")
	public void deletejobfilterbyfilterId_403_otherogtoken(String organizationType,
			 								   String organization_id,
			 								   String validToken,
			 								   String user_id,
			 								   String otherorgtoken,
			 								   String otherorguserid,
			 								   String jobStatus, 
											   String policyID, 
											   String resourceID, 
											   String jobType, 
											   String startTimeTSStart, 
											   String startTimeTSEnd, 
											   String endTimeTSStart, 
											   String endTimeTSEnd, 
											   String filterName, 
											   String isDefault
											   ) {
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		String prefix = RandomStringUtils.randomAlphanumeric(4);
		HashMap<String,Object>expected_response = new HashMap<>();
		SpogMessageCode expected = null;
		
		spogJobServer.setToken(validToken);
		test.log(LogStatus.INFO, "Create the job filter in org "+organizationType);
		String filter_Id = spogJobServer.createJobFilterWithCheckEx(user_id, jobStatus, policyID, resourceID, jobType, startTimeTSStart, startTimeTSEnd, endTimeTSStart, endTimeTSEnd, prefix + filterName, isDefault, test);
		
		if(organizationType=="msp_direct"||organizationType=="msp_suborg") {
			expected = SpogMessageCode.DIRECT_ADMIN_CANNOT_VIEW_MSP_CSR;
		}else {
			expected = SpogMessageCode.LOGIN_USER_ORG_NOT_SAME_AS_GET_USER;
		}
		expected = SpogMessageCode.RESOURCE_PERMISSION_DENY;
		test.log(LogStatus.INFO, "delete job filter by filter id where the filter Id and the user id are from organization "+organizationType + " and token from other org" );
		spogJobServer.deleteJobFilterByFilterID(user_id, filter_Id, otherorgtoken, SpogConstants.INSUFFICIENT_PERMISSIONS, expected, test);
		
		test.log(LogStatus.INFO, "delete job filter by filter id");
		spogJobServer.deleteJobFilterByFilterID(user_id, filter_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		
	}
	
	
	@Test(dataProvider = "create_job_filter_valid")
	public void deletejobfilterbyfilterId_doesnotexist(String organizationType,
			 								   String organization_id,
			 								   String validToken,
			 								   String user_id,
			 								   String jobStatus, 
											   String policyID, 
											   String resourceID, 
											   String jobType, 
											   String startTimeTSStart, 
											   String startTimeTSEnd, 
											   String endTimeTSStart, 
											   String endTimeTSEnd, 
											   String filterName, 
											   String isDefault
											   ) {
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		String prefix = RandomStringUtils.randomAlphanumeric(4);
		HashMap<String,Object>expected_response = new HashMap<>();
		SpogMessageCode expected = null;
		
		spogJobServer.setToken(validToken);
		test.log(LogStatus.INFO, "Create the job filter in org "+organizationType);
		String filter_Id = spogJobServer.createJobFilterWithCheckEx(user_id, jobStatus, policyID, resourceID, jobType, startTimeTSStart, startTimeTSEnd, endTimeTSStart, endTimeTSEnd, prefix + filterName, isDefault, test);
		
		test.log(LogStatus.INFO, "delete job filter by filter id where the filter does not exist using token of "+organizationType);
		spogJobServer.deleteJobFilterByFilterID(user_id, UUID.randomUUID().toString(), validToken, SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.JOB_FILTER_NOT_FOUND_WITH_CURRENT_USER, test);
		
		test.log(LogStatus.INFO, "delete job filter by filter id where the userid does not exist using token of "+organizationType);
		spogJobServer.deleteJobFilterByFilterID(UUID.randomUUID().toString(), filter_Id, validToken, SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.USER_ID_DOESNOT_EXIST, test);
		
		test.log(LogStatus.INFO, "delete job filter by filter id where the filter does not exist using token of csr");
		spogJobServer.deleteJobFilterByFilterID(user_id, UUID.randomUUID().toString(), ti.csr_token, SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.JOB_FILTER_NOT_FOUND_WITH_CURRENT_USER, test);
		
		test.log(LogStatus.INFO, "delete job filter by filter id");
		spogJobServer.deleteJobFilterByFilterID(user_id, filter_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		
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
}
