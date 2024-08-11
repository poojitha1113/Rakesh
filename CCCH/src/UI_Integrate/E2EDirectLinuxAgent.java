package UI_Integrate;

import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.SpogConstants;
import Constants.UIConstants;
import InvokerServer.Org4SPOGServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.FolderOrFileUtils;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;
import ui.base.common.TableHeaders;
import ui.spog.pages.analyze.JobsPage;
import ui.spog.server.DestinationHelper;
import ui.spog.server.JobsHelper;
import ui.spog.server.MonitorHelper;
import ui.spog.server.PolicyHelper;
import ui.spog.server.SPOGUIServer;
import ui.spog.server.SourcePageHelper;

public class E2EDirectLinuxAgent extends base.prepare.Is4Org {

	private SPOGServer spogServer;
	// private UserSpogServer userSpogServer;
	private SourcePageHelper sourceHelper;
	private MonitorHelper monitorHelper;
	private DestinationHelper destinationHelper;
	private PolicyHelper policyHelper;
	private JobsHelper jobsHelper;
	private Policy4SPOGServer policy4SpogServer;
	private SPOGDestinationServer spogDestinationServer;
	private FolderOrFileUtils folderOrFileUtils;

	private String csrAdminUserName;
	private String csrAdminPassword;
	private ExtentTest test;

	private String url;

	private String org_model_prefix = this.getClass().getSimpleName();
	private String restore_file="/source/ESX.tp";
	private String direct_user_name = "jing_direct_ui@arcserve.com";
	private String direct_password = "Caworld_2017";
	private String baas_destination_volume = "vol_ztst-3502.zetta.net";
	private String draas_destination_volume = "Disaster Recovery";
	private String account_user_name = "ss.zz@arcserve.com";
	private String account_password = "Cnbjrdqa1!";
	private String direct_sources = "RamyaRPS2";

	private ArrayList<HashMap<String, String>> multipleThrottleScheduleArray1, multipleMergeScheduleArray;
	private ArrayList<String> direct_source_ids, direct_sources_name;
	private ArrayList<HashMap> direct_sources_data;
	
	String Sources, direct_cloud_account_id, hybrid_destination_id, hybrid_destination_name;
	String linux_user,linux_pwd,linux_copyfilescript,sourceFilepath, destFilePath,sourceFilepath1,create_destpath;
	String linux_deleteFileOrDirectorScript,destFilePath1,linux_checkFileExistScript,linux_createfolderscript;

	private HashMap<String, String> retentionInputs = new HashMap<>();

	String cloud_rps_name = null;
	String cloud_rps_port = null;
	String cloud_rps_protocol = null;
	String cloud_rps_username = null;
	String cloud_rps_password = null;
	String csr_site_id = null;
	String csr_site_name = null;
	String csrToken;
	
	

	String server_name = null;
	String server_username = null;
	String server_password = null;
	String server_port = null;
	String server_protocol = null;
	String remote_policy = null;
	String retry_minutes = null;
	String retry_num = null;
	

	String rps_server_name = null, rps_server_id = null, datastore_id = null;

	String direct_org_email, common_password = "Caworld_2017", direct_organization_id, direct_user_validToken,
			direct_user_id, direct_site_id;

	int i = 0;
	/* private ArrayList<HashMap<String, String>> multipleThrottleScheduleArray1; */

	@BeforeClass
	@Parameters({ "baseURI", "port", "browserType", "maxWaitTimeSec", "logFolder", "csrAdminUserName",
			"csrAdminPassword", "buildVersion", "uiBaseURI" })
	public void beforeClass(String baseURI, String port, String browserType, String maxWaitTimeSec, String logFolder,
			String adminUserName, String adminPassword, String buildVersion, String uiBaseURI)
			throws UnknownHostException {
		
        folderOrFileUtils=new FolderOrFileUtils();
        linux_user="root";
    	linux_pwd="123.com";
    	linux_copyfilescript="/mnt/copyFile.sh";
    	linux_createfolderscript="/mnt/createFolder.sh";
    	linux_checkFileExistScript="/mnt/checkFileExist.sh";
    	linux_deleteFileOrDirectorScript="/mnt/deleteFileOrDirector.sh";
    	sourceFilepath="/source1/jdk-8u92-windows-x64.exe";
    	create_destpath="/source"+ RandomStringUtils.randomAlphanumeric(8);
    	destFilePath=create_destpath+"/jdk-8u92-windows-x64"+ RandomStringUtils.randomAlphanumeric(8)+".exe";
    	sourceFilepath1="/source1/ESX.tp";
    	destFilePath1=create_destpath+"/ESX.tp";
		
		policyHelper = new PolicyHelper(browserType, Integer.valueOf(maxWaitTimeSec));
		jobsHelper = new JobsHelper(browserType, Integer.valueOf(maxWaitTimeSec));
		sourceHelper = new SourcePageHelper(browserType, Integer.valueOf(maxWaitTimeSec));
		monitorHelper= new MonitorHelper(browserType, Integer.valueOf(maxWaitTimeSec));
		destinationHelper= new DestinationHelper(browserType, Integer.valueOf(maxWaitTimeSec));
		policy4SpogServer = new Policy4SPOGServer(baseURI, port);
		spogServer = new SPOGServer(baseURI, port);
		// userSpogServer = new UserSpogServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		this.url = uiBaseURI;
		this.csrAdminUserName = adminUserName;
		this.csrAdminPassword = adminPassword;
		rep = ExtentManager.getInstance("Test", logFolder);
		test = rep.startTest("beforeClass");
		Sources="sj-red76-b";
		
		
		//prepare backup source file
		try {
			folderOrFileUtils.CreateFolderOnLinuxNode(Sources, linux_user, linux_pwd, linux_createfolderscript, create_destpath);
			folderOrFileUtils.CopyFileOnLinuxNode(Sources, linux_user, linux_pwd, linux_copyfilescript, sourceFilepath, destFilePath);
			//folderOrFileUtils.CopyFileOnLinuxNode(Sources, linux_user, linux_pwd, linux_copyfilescript, sourceFilepath, create_destpath+"/jdk-8u92-windows-x64"+ RandomStringUtils.randomAlphanumeric(8)+".exe");
			folderOrFileUtils.CopyFileOnLinuxNode(Sources, linux_user, linux_pwd, linux_copyfilescript, sourceFilepath1, destFilePath1);
			boolean fileExists=folderOrFileUtils.CheckFileExistOnLinuxNode(Sources, linux_user, linux_pwd,linux_checkFileExistScript, destFilePath1, "true");
			assertTrue(fileExists, "file is existing.");
		}catch(Exception e) {
			e.printStackTrace();
		}
		policyHelper.openUrl(url);

		this.BQName = this.getClass().getSimpleName();
		String author = "Jing.shan";
		this.runningMachine = InetAddress.getLocalHost().getHostName();
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date = new java.util.Date();
		this.buildVersion = buildVersion + "_" + dateFormater.format(date);
		Nooftest = 0;
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		if (count1.isstarttimehit() == 0) {

			creationTime = System.currentTimeMillis();
			count1.setcreationtime(creationTime);
			// creationTime = System.currentTimeMillis();
			try {
				bqdb1.updateTable(this.BQName, runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0",
						String.valueOf(Nooftest), count1.getcreationtime(), "InProgress",
						author + " and Rest server is " + baseURI.split("//")[1]);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		prepareExistingEnv();
	}

	private void prepareExistingEnv() {

		direct_organization_id = "cb2ccd2d-7227-41b7-90e6-0da1dd09375f";
		hybrid_destination_id = "2a02b295-4d10-477a-82d5-45765c42e30f";
		hybrid_destination_name = "Sub1DS1";

		server_name = "10.60.14.166";
		server_username = "Administrator";
		server_password = "Mclaren@2020";
		server_port = "8015";
		server_protocol = "https";
		remote_policy = "Reverse Replication Policy";
		retry_minutes = "5";
		retry_num = "3";

		spogServer.userLogin(this.direct_user_name, this.direct_password);
		test.log(LogStatus.INFO, "Getting the JWTToken for the Logged in user");
		direct_user_validToken = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Get the logged in user id ");
		direct_user_id = spogServer.GetLoggedinUser_UserID();
		test.log(LogStatus.INFO, "The direct org user id is " + direct_user_id);

//		test.log(LogStatus.INFO, "get direct cloud account");
//		// get direct cloud account
//		Response response = spogServer.getCloudAccounts(direct_user_validToken, "", test);
//		direct_cloud_account_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");
//		direct_site_id = direct_cloud_account_id;

//		response = spogDestinationServer.getDestinations(spogServer.getJWTToken(),
//				"destination_id=715ff56d-1047-4ccf-9c58-ae58f846c1fa", test);
//		baas_destination_volume = response.then().extract().path("data[" + 0 + "].destination_name").toString();

		direct_source_ids = new ArrayList<String>();
		direct_sources_name = new ArrayList<String>();
		direct_sources_data = new ArrayList<>();
		policyHelper.login_Spog(this.direct_user_name, this.direct_password);

	}

	@DataProvider(name = "organizationAndUserInfo1")
	public final Object[][] getOrganizationAndUserInfo1() {
		return new Object[][] {

				{  this.direct_org_email, common_password, "baas file test" + RandomStringUtils.randomAlphanumeric(8),
					null, Sources, UIConstants.ACTIVE_TYPE_FILE, null, null, this.create_destpath, baas_destination_volume,
					"false", null, "12", "00", UIConstants.TIME_PM, null, null,
					UIConstants.POLICY_STATUS_SUCCESS }

		};
	}
	
	@Test(dataProvider = "organizationAndUserInfo1", enabled = true)
	public void UITestForDirectUser(String userName, String password, String policyName, String description,
			String sourceNames, String activeType, String activeOption, String drives, String paths,
			String destVolumeName, String enableLocal, String localDestination, String hour, String minute,
			String clock, ArrayList<HashMap<String, String>> throttleScheduleArray, String excludeRules,
			String expectedStatus) {

		
		policyHelper.addBaasPolicy(policyName, description, sourceNames, activeType, activeOption, drives, paths,
				destVolumeName, enableLocal, localDestination, hour, minute, clock, throttleScheduleArray, excludeRules,
				expectedStatus);
		test.log(LogStatus.INFO, "get policy_id of the created policy to delete");
		Response response = policy4SpogServer.getPolicies(spogServer.getJWTToken(),"policy_name="+policyName);
		String policy_id = response.then().extract().path("data[0].policy_id");
		HashMap<String, Object> expectedPolicyJobInfo = new HashMap<>();
		expectedPolicyJobInfo.put(TableHeaders.source, sourceNames);
		expectedPolicyJobInfo.put(TableHeaders.job_name, "Policy Deployment for "+sourceNames);
		expectedPolicyJobInfo.put(TableHeaders.job_type, "Policy Deployment");
		expectedPolicyJobInfo.put(TableHeaders.status, "Finished");
		expectedPolicyJobInfo.put(TableHeaders.policy, policyName);
		//policy4SpogServer.deletePolicybyPolicyId(spogServer.getJWTToken(), policy_id);
		//checkJobDetails("Policy Development for "+sourceNames,expectedPolicyJobInfo);
		jobsHelper.checkPolicyJobInformation("Policy Development for "+sourceNames,expectedPolicyJobInfo,test);
		//get previous job time from destination
		destinationHelper.goToCloudDirectDestinationsPage();
		String previous_dest_last_time=destinationHelper.getLastJobTimeFromLatestJobColume( 1,test);
		
		//get last backup job time from source 2 jobs
		String last_job_time=sourceHelper.getLastJobTimeFromLatestJobColume(sourceNames, test);
		System.out.println("last_job_time is:"+last_job_time);
		
		
		
		//String policyName="baas file test4WroAqw3";
		//submit backup job in source page
		sourceHelper.startBackup(sourceNames, test);
		
		//wait job to in progress status in job page
		jobsHelper.waitExpectedJobInSavedSearch("Jobs in Progress", "Backup from "+sourceNames+" to "+baas_destination_volume, 20, test);		
		String currentStartTime=jobsHelper.getCurrentJobStartTime(TableHeaders.start_time, test);
		System.out.println("currentStartTime is:"+currentStartTime);
		
		try {
			Thread.sleep(25000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//check in progress job in monitor page
		monitorHelper.checkInProgessJobInformation("Backup from "+sourceNames+" to "+baas_destination_volume,sourceNames,test);
				
		//check the job status is expected in source 2 jobs
		//sourceHelper.checkJobinformationFromLatestJobColume("LinuxCIM2","backup-incremental","In progress", test);
		sourceHelper.waitUntilJobUpdatedFromLatestJobColume(sourceNames,"backup",last_job_time,"In progress","2 Jobs",2, test);
		
		
		//wait job to finish
		jobsHelper.waitJobDone("Backup from "+sourceNames+" to "+baas_destination_volume, 500, test);
		HashMap<String, Object> expectedBackupJobInfo = new HashMap<>();
		expectedBackupJobInfo.put(TableHeaders.source, sourceNames);
		expectedBackupJobInfo.put(TableHeaders.destination, baas_destination_volume);
		expectedBackupJobInfo.put(TableHeaders.job_name, "Backup from "+sourceNames+" to "+baas_destination_volume);
		expectedBackupJobInfo.put(TableHeaders.job_type, "Backup");
		expectedBackupJobInfo.put(TableHeaders.status, "Finished");
		expectedBackupJobInfo.put(TableHeaders.policy, policyName);
		expectedBackupJobInfo.put(TableHeaders.start_time, currentStartTime);
		//check the essential job information from all jobs
		jobsHelper.checkEssentialJobInformation("Backup from "+sourceNames+" to "+baas_destination_volume, expectedBackupJobInfo, test);
		//check job status is expected in source 2 jobs
		//sourceHelper.checkJobinformationFromLatestJobColume("LinuxCIM2","backup-incremental","Finish", test);
		sourceHelper.waitUntilJobUpdatedFromLatestJobColume(sourceNames,"backup",last_job_time,"Finished","2 Jobs",2, test);
		
		//wait job to in progress status
		jobsHelper.clickViewLogsForGivenJob("Backup from "+sourceNames+" to "+baas_destination_volume, test);
		HashMap<String, Object> expectedFirstLogInfo = new HashMap<>();
		expectedFirstLogInfo.put(TableHeaders.source, sourceNames);
		expectedFirstLogInfo.put(TableHeaders.generated_from, sourceNames);
		expectedFirstLogInfo.put(TableHeaders.job_name, "Backup from "+sourceNames+" to "+baas_destination_volume);
		expectedFirstLogInfo.put(TableHeaders.job_type, "Backup");
		expectedFirstLogInfo.put(TableHeaders.message, "Backup job completed successfully");
		
		//check the essential job information from all jobs
		jobsHelper.checkFirstLogDetailsInTable( expectedFirstLogInfo, test);
//		HashMap<String, Object> expectedLastLogInfo = new HashMap<>();
//		expectedLastLogInfo.put(TableHeaders.source, sourceNames);
//		expectedLastLogInfo.put(TableHeaders.generated_from, sourceNames);
//		expectedLastLogInfo.put(TableHeaders.job_name, "Backup from "+sourceNames+" to "+baas_destination_volume);
//		expectedLastLogInfo.put(TableHeaders.job_type, "Backup");
//		expectedLastLogInfo.put(TableHeaders.message, "Incremental backup");
//		jobsHelper.checkLastLogDetailsInTable( expectedLastLogInfo, test);
		
		//check latest job status from destination
		destinationHelper.goToCloudDirectDestinationsPage();
		destinationHelper.checkJobInformationFromLatestJobColume(1,previous_dest_last_time,"Backup","Finished");
		previous_dest_last_time=destinationHelper.getLastJobTimeFromLatestJobColume( test);
//		
		//delete restore source file
		try {
			folderOrFileUtils.deleteFile(restore_file);
		}catch(Exception e) {
			e.printStackTrace();
		}
		String previousStartTime=jobsHelper.getLastColumeValueFromSuccessfulJobs(TableHeaders.start_time,sourceNames, test);
		System.out.println("currentStartTime is:"+previousStartTime);
		sourceHelper.startRecover(sourceNames, test);
		test.log(LogStatus.INFO, "start recover job");
		jobsHelper.clickRecoveryFromFristRow(test);
		jobsHelper.recoveryToOriginal("/source/",test);
		//wait job to finish
		jobsHelper.waitJobDone("Restore from "+sourceNames+" to "+sourceNames,"Restore",previousStartTime, 10, test);		
		currentStartTime=jobsHelper.getCurrentJobStartTime(TableHeaders.start_time,test);
		System.out.println("currentStartTime for restore job is:"+currentStartTime);
		expectedBackupJobInfo = new HashMap<>();
		expectedBackupJobInfo.put(TableHeaders.source, sourceNames);
		expectedBackupJobInfo.put(TableHeaders.destination, baas_destination_volume);
		expectedBackupJobInfo.put(TableHeaders.job_name, "Restore from "+sourceNames+" to "+sourceNames);
		expectedBackupJobInfo.put(TableHeaders.job_type, "Restore");
		expectedBackupJobInfo.put(TableHeaders.status, "Finished");
		expectedBackupJobInfo.put(TableHeaders.policy, policyName);
		expectedBackupJobInfo.put(TableHeaders.start_time, currentStartTime);
		//check the essential job information from all jobs
		jobsHelper.checkEssentialJobInformation("Restore from "+sourceNames+" to "+sourceNames, expectedBackupJobInfo, test);
		//check job status is expected in source 2 jobs
		//sourceHelper.checkJobinformationFromLatestJobColume("LinuxCIM2","backup-incremental","Finish", test);
		sourceHelper.waitUntilJobUpdatedFromLatestJobColume(sourceNames,"Restore",previous_dest_last_time,"Finished","2 Jobs",2, test);
		
		//wait job to in progress status
		jobsHelper.clickViewLogsForGivenJob("Restore from "+sourceNames+" to "+sourceNames, test);
		expectedFirstLogInfo = new HashMap<>();
		expectedFirstLogInfo.put(TableHeaders.source, sourceNames);
		expectedFirstLogInfo.put(TableHeaders.generated_from, sourceNames);
		expectedFirstLogInfo.put(TableHeaders.job_name, "Restore from "+sourceNames+" to "+sourceNames);
		expectedFirstLogInfo.put(TableHeaders.job_type, "Restore");
		expectedFirstLogInfo.put(TableHeaders.message, "Restore job completed successfully");
		jobsHelper.checkFirstLogDetailsInTable( expectedFirstLogInfo, test);
		
		//check latest job status from destination
		destinationHelper.goToCloudDirectDestinationsPage();
		//destinationHelper.checkJobInformationFromLatestJobColume(previous_dest_last_time,"Restore","Finished");
		destinationHelper.checkJobInformationFromLatestJobColume(1,previous_dest_last_time,"Restore","Finished");
		
		//check restore file is restored back
		boolean fileExists=folderOrFileUtils.CheckFileExistOnLinuxNode(Sources, linux_user, linux_pwd,linux_checkFileExistScript, restore_file, "true");
		assertTrue(fileExists, "file is existing.");
		
		//delete policy from api
		spogServer.userLogin(this.direct_user_name, this.direct_password);
		policy4SpogServer.setToken(spogServer.getJWTToken());
		
		response = policy4SpogServer.getPolicies(spogServer.getJWTToken(),"policy_name="+policyName);
		policy_id = response.then().extract().path("data[0].policy_id");
		policy4SpogServer.deletePolicybyPolicyId(spogServer.getJWTToken(), policy_id);
		
		//delete the added source file
		try {
			folderOrFileUtils.DeleteFileOrDirectorOnLinuxNode(Sources, linux_user, linux_pwd, linux_deleteFileOrDirectorScript, create_destpath );
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
		
	@AfterClass
	public void destroyEnv() {

		this.updatebd();
		policyHelper.destroy();
	}
	/**
	 * waitInMilliSec - method used to wait for milli seconds
	 * 
	 * @author Ramya.Nagepalli
	 * @param ms
	 */
	public void waitInSec(int ms) {

		ms = ms * 1000;
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@AfterMethod
	public void getResult(ITestResult result) {

		if (result.getStatus() == ITestResult.FAILURE) {
			count1.setfailedcount();
			test.log(LogStatus.FAIL, "Test Case Failed is " + result.getName() + " with parameters as "
					+ Arrays.asList(result.getParameters()));
			test.log(LogStatus.FAIL, result.getThrowable().getMessage());
		} else if (result.getStatus() == ITestResult.SKIP) {
			count1.setskippedcount();
			test.log(LogStatus.SKIP, "Test Case Skipped is " + result.getName());
		} else if (result.getStatus() == ITestResult.SUCCESS) {
			count1.setpassedcount();
		}
		// ending test
		// endTest(logger) : It ends the current test and prepares to create HTML report
		rep.endTest(test);
	}

}
