package api.backupjobreports;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZonedDateTime;
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

import Constants.DestinationStatus;
import Constants.DestinationType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import Constants.volume_type;
import InvokerServer.GatewayServer;
import InvokerServer.Log4SPOGServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGReportServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class GetBackupJobReportsDetailsTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private UserSpogServer userSpogServer;
	private Policy4SPOGServer policy4SpogServer; 
	private SPOGDestinationServer spogDestinationServer;
	private SPOGReportServer spogreportServer;
	private TestOrgInfo ti;
	//public int Nooftest;
	//private ExtentReports rep;
	private ExtentTest test;

	int passedcases=0;
	int failedcases=0;
	int skippedcases=0;
	private String  org_model_prefix=this.getClass().getSimpleName();

	private String test_log_Message_1 = "testLogMessage";
	private String test_log_Message_2 = "connect_node_failed_test_message";
	private String test_log_Message_3 = "testLogMessageWithoutData";
	private	String job_Type = "backup_full,restore,backup_full" ;/*conversionvm_backup,vm_recovery,vm_catalog_fs,mount_recovery_point,office365_backup,cifs_backup,sharepoint_backup,vm_merge,catalog_fs,catalog_app,catalog_grt,file_copy_backup,file_copy_purge,file_copy_restore,file_copy_catalog_sync,file_copy_source_delete,file_copy_delete,catalog_fs_ondemand,vm_catalog_fs_ondemand,rps_replicate,rps_replicate_in_bound,rps_merge,rps_conversion,bmr,rps_data_seeding,rps_data_seeding_in,vm_recovery_hyperv,rps_purge_datastore,start_instant_vm,stop_instant_vm,assure_recovery,start_instant_vhd,stop_instant_vhd,archive_to_tape,linux_instant_vm";*/
	private	String job_Status= "finished";
	private	String job_Method = "full,incremental,resync";


	//Information related to the sites
	ArrayList<String>sites=new ArrayList<String>();
	ArrayList<String>directsites=new ArrayList<String>();
	ArrayList<String>mspsites=new ArrayList<String>();

	//For storing the retention,cloud_direct_vloume information 

	HashMap<String,String >retention=new HashMap<String,String>();
	HashMap<String,Object>cloud_direct_volume,cloud_dedupe_volume=new HashMap<String,Object>();

	//ArrayList of Logs for direct,MSP and SubOrg
	ArrayList<String> direct_id=new ArrayList<String>();
	ArrayList<String> msp_id=new ArrayList<String>();

	//List of the source_id
	ArrayList<String>sources=new ArrayList<String>();

	public String JobSeverity= "success,warning,error,critical,information";
	public String logType="last_24_hours,last_7_days,last_2_weeks,last_1_month,custom";

	ArrayList<String>normalmspsites=new ArrayList<String>();
	ArrayList<String>rootmspsites=new ArrayList<String>();
	ArrayList<String>submspsites=new ArrayList<String>();
	private Random r;
	//Sorting based on the create_ts 
	//Sorting based on the create_ts 
	long current = ZonedDateTime.now().toInstant().toEpochMilli()/1000L;
	long yesterday = ZonedDateTime.now().minusDays(1).toInstant().toEpochMilli()/1000L;
	long tomorrow = ZonedDateTime.now().plusDays(1).toInstant().toEpochMilli()/1000L;

	private ArrayList<String> servers=new ArrayList<String>();
	
	String direct_site_id;

	String sourceName;
	String direct_server_id;
	String normal_msp_site_id,normal_msp_site_token;

	String root_msp_site_id,root_msp_site_token,sub_msp_site_id,sub_msp_site_token;
	String root_subOrg_Server_id,submsp_subOrg_Server_id;
	String normal_subOrg_Server_id;
	String source_id;

	@BeforeClass
	@Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword, String logFolder,String runningMachine,String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);

		policy4SpogServer=new Policy4SPOGServer(baseURI,port); 
		spogDestinationServer=	new SPOGDestinationServer(baseURI,port); 
		spogreportServer=new  SPOGReportServer(baseURI,port); 
		rep = ExtentManager.getInstance("GetBackupJobReportsDetailsTest", logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Malleswari.Sykam";
		ti = new TestOrgInfo(spogServer, test);	
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



			test = rep.startTest("beforeClass");

			ti = new TestOrgInfo(spogServer, test);	
			sourceName = spogServer.ReturnRandom("Malleswari");	

			//Direct Organization
			Response response = spogServer.getCloudAccounts(ti.direct_org1_user1_token, "", test);
			direct_site_id = response.then().extract().path("data[0].cloud_account_id");

			servers.add(sourceName);
			spogServer.setToken(ti.direct_org1_user1_token);
			test.log(LogStatus.INFO,"create source");


			//Normal msp 
			response = spogServer.getCloudAccounts(ti.normal_msp_org1_user1_token, "", test);
			normal_msp_site_id = response.then().extract().path("data[0].cloud_account_id");
			spogServer.setToken(ti.normal_msp_org1_user1_token);

			//root msp
			response = spogServer.getCloudAccounts(ti.root_msp_org1_user1_token, "", test);
			root_msp_site_id = response.then().extract().path("data[0].cloud_account_id");
			spogServer.setToken(ti.root_msp_org1_user1_token);


			//Sub msp
			spogServer.setToken(ti.csr_token);
			response = spogServer.getCloudAccounts(ti.root_msp1_submsp1_user1_token, "", test);
			sub_msp_site_id = response.then().extract().path("data[0].cloud_account_id");

		}

	}


	@DataProvider(name="getbakupjobreports")
	public final Object[][] getbakupjobreports(){
		return new Object[][]{
			{"Direct",ti.csr_token,ti.direct_org1_user1_token,ti.direct_org1_id,ti.direct_org1_user1_id,direct_site_id,direct_site_id,System.currentTimeMillis()/1000,System.currentTimeMillis()/1000,ti.direct_org1_user1_token,"1",30.20, "3","02","2", "3","3","3"}, 
			{"Sub Organization of Normal Msp",ti.csr_token,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_id,normal_msp_site_id,normal_msp_site_id,System.currentTimeMillis()/1000,System.currentTimeMillis()/1000,ti.normal_msp1_suborg1_user1_token,"1",30.20, "3","02","2", "3","3","3"}, 
			{"Sub Organization of Root Msp",ti.csr_token,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_id,root_msp_site_id,root_msp_site_id,System.currentTimeMillis()/1000,System.currentTimeMillis()/1000,ti.root_msp1_suborg1_user1_token,"1",30.20, "3","02","2", "3","3","3"}, 
			{"Sub Organization of Sub  Msp",ti.csr_token,ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_sub_org1_id,ti.msp1_submsp1_suborg1_user1_id,sub_msp_site_id,sub_msp_site_id,System.currentTimeMillis()/1000,System.currentTimeMillis()/1000,ti.msp1_submsp1_suborg1_user1_token,"1",30.20, "3","02","2", "3","3","3"}, 


		};
	}    

	@Test(dataProvider="getbakupjobreports")    
	public void GetBackupJobReportsStatusSummary(String organization_type,String token_Destination_creation,String adminToken,String organization_id,String create_user_id,
			String site_id,String cloud_id,Long start_time_ts,Long endTimeTS,String site_Token,String job_seq, Double percent_complete, 
			String protected_data_size, String raw_data_size, String sync_read_size,String ntfs_volume_size, String virtual_disk_provision_size,String dedupe_savings
			){

		String job_id=null,log_id=null, group_id=null;

		ArrayList<String> soucres=new ArrayList<String>();

		HashMap<String,ArrayList<HashMap<String,Object>>> last_job=new HashMap<String,ArrayList<HashMap<String,Object>>>();
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());

		test.assignAuthor("malleswari.sykam");     

		ArrayList<String> jobs=new ArrayList<String>();
		ArrayList<HashMap<String,Object>> ExpectedReports=new ArrayList<HashMap<String,Object>> ();
		HashMap<String,Object> temp=new HashMap<String,Object>();

		HashMap<String, String> retention = spogDestinationServer.composeRetention("0","0","0","0","0","0");
		HashMap<String, Object> cloud_direct_volume = spogDestinationServer.composeCloudDirectInfo(cloud_id,"dest_cloud_direct","7D","7 Days",26.0,24.0,50.0,volume_type.normal.toString(),"windows8",retention);	


		//create destination for direct
		spogDestinationServer.setToken(token_Destination_creation);
		Response  response=spogDestinationServer.createDestination(UUID.randomUUID().toString(),token_Destination_creation,cloud_id,organization_id,cloud_id,"91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", "20","cloud_direct_volume",DestinationStatus.running.toString(),"destination_direct_volume",cloud_direct_volume,test);
		String destiantion_id=response.then().extract().path("data.destination_id");
		String destination_name="eswari_cloud_direct_volume";
		String policy_id= null;


		spogServer.setToken(adminToken);
		String protectionStatus="unprotect,unprotect,unprotect,unprotect,unprotect,unprotect,unprotect,unprotect,unprotect,unprotect,unprotect,unprotect,unprotect,unprotect,unprotect";
		String connectionStatus="online,offline,online,offline,online,online,offline,online,offline,online,online,offline,online,offline,online";
		String osmajor="windows,linux,mac,unknown,windows,windows,linux,mac,unknown,windows,windows,linux,mac,unknown,windows";
		String SourceType="machine,machine,machine,machine,machine,machine,machine,machine,machine,machine,machine,machine,machine,machine,machine";
		String SourceProduct="cloud_direct,cloud_direct,cloud_direct,cloud_direct,cloud_direct,cloud_direct,cloud_direct,cloud_direct,cloud_direct,cloud_direct,cloud_direct,cloud_direct,cloud_direct,cloud_direct,cloud_direct";

		String []protection=protectionStatus.split(",");
		String []connection=connectionStatus.split(",");
		String []Osmajor=osmajor.split(","); 
		String []sourceType=SourceType.split(","); 
		String []sourceProduct=SourceProduct.split(","); 
		ArrayList<HashMap<String, Object>> expectedsources=new ArrayList<HashMap<String, Object>>();

		String[] arrayofsourcenodes= new String[1];

		for(int i=0;i<1;i++) {


			//String destination_name="cloud_direct_hybrid"+i;
			String schedule_id=UUID.randomUUID().toString();
			String task_id=UUID.randomUUID().toString();
			String throttle_id=UUID.randomUUID().toString();
			String destination_id=UUID.randomUUID().toString();
			String rps_id = UUID.randomUUID().toString();

			spogServer.setToken(adminToken);
			String sourceName = spogServer.ReturnRandom("SPOG_ARCSEVRE");    
			test.log(LogStatus.INFO,"create source");

			source_id =spogServer.createSourceWithCheck(sourceName+i,sourceType[i], sourceProduct[i], organization_id, cloud_id, protection[i], connection[i], Osmajor[i], "SQL_SERVER", create_user_id,SpogConstants.SUCCESS_POST, null,true, test);
			soucres.add(sourceName+i);
			arrayofsourcenodes[0]=source_id;
			String group_name="test"+i;
			//CREATED SOURCE TO THE GROUP
			test.log(LogStatus.INFO, "Create a source group");
			group_id = spogServer.createGroupWithCheck(organization_id, group_name, "add sources",test);
			spogServer.setToken(adminToken);
			//add sourcs to the sourcegroup
			test.log(LogStatus.INFO, "Add the sources to the sourcegroup "+group_id);
			spogServer.addSourcetoSourceGroupwithCheck(group_id, arrayofsourcenodes, adminToken, SpogConstants.SUCCESS_POST, SpogMessageCode.SUCCESS_POST, test);

			//create cloud hybrid destination
			spogDestinationServer.setToken(adminToken);


			if (organization_type.equals("sub")) {
				spogDestinationServer.setToken(token_Destination_creation);
			}

			String prefix = RandomStringUtils.randomAlphanumeric(8);

			//create schedule for policy

			//HashMap<String, String> retention= spogDestinationServer.composeRetention("0","0","0","0","0","0");
			//HashMap<String, Object> cloud_direct_volume=spogDestinationServer.composeCloudDirectInfo(cloud_id,"dest_cloud_direct_direct","7D","7 Days",26.0,24.0,50.0,volume_type.normal.toString(),"windows8",retention);	


			test.log(LogStatus.INFO, "Create custom settings");
			HashMap<String, Object> customScheduleSettingDTO=policy4SpogServer.createCustomScheduleDTO("1522068700422","full","1","true","10","minutes" ,test);

			HashMap<String, Object>  scheduleSettingDTO=policy4SpogServer.createScheduleSettingDTO(null,customScheduleSettingDTO,null, null,test);

			test.log(LogStatus.INFO, "Create cloud direct schedule");

			HashMap<String, Object> cloudDirectScheduleDTO=policy4SpogServer.createCloudDirectScheduleDTO("0 6 * * *", test);

			test.log(LogStatus.INFO, "Create schedule settings");
			scheduleSettingDTO=policy4SpogServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null,null,null,test);

			ArrayList<HashMap<String,Object>> schedules =policy4SpogServer.createPolicyScheduleDTO(null,schedule_id, 
					"1d", task_id, destiantion_id, scheduleSettingDTO, "06:00", "12:00", "cloud_direct_file_folder_backup",destination_name,test);

			test.log(LogStatus.INFO, "Exclude the folders from cloud direct file backup");  

			ArrayList<HashMap<String,Object>> excludes=policy4SpogServer.createExcludeInfoDTO(null, "path", "c:\\tmp", test);

			HashMap<String, Object> cloudDirectLocalBackupDTO=policy4SpogServer.createCloudDirectLocalBackupDTO("d:\\tmp", "true", test);

			HashMap<String, Object> cloudDirectFileBackupTaskInfoDTO=policy4SpogServer.createCloudDirectFileBackupTaskInfoDTO("d:\\tmp", cloudDirectLocalBackupDTO,excludes, test);
			HashMap<String, Object> perform_ar_test= policy4SpogServer.createPerformARTestOption("1", "1", "1", "1", test);

			HashMap<String, Object> retention_policy = policy4SpogServer.createRetentionPolicyOption("2", "2", "2", "2", test);

			HashMap<String, Object> udp_replication_from_remote_DTO = policy4SpogServer.createUdpReplicationFromRemoteInfoDTO(perform_ar_test,retention_policy,test);

			ArrayList<HashMap<String,Object>>  destinations= policy4SpogServer.createPolicyTaskDTO(null, task_id, "cloud_direct_file_folder_backup", destiantion_id, "none",null, cloudDirectFileBackupTaskInfoDTO, null, test);
			ArrayList<HashMap<String,Object>> throttles =policy4SpogServer.createPolicyThrottleDTO(null, throttle_id, task_id, "network", "1200", "1", "06:00", "18:00","cloud_direct_file_folder_backup",destiantion_id,destination_name, test);

			//Policy4SPOGServer policy4SpogServer= new Policy4SPOGServer("http://tcc.arcserve.com", "8080");


			//create cloud direct policy
			policy4SpogServer.setToken(adminToken);
			String policy_name=spogServer.ReturnRandom("ramya");
			response=policy4SpogServer.createPolicy(prefix+policy_name+i, policy_name, "cloud_direct_baas", null, "true",source_id, destinations, schedules, throttles, policy_id, organization_id, test);
			policy_id=response.then().extract().path("data.policy_id").toString();



			String job_Type = "backup,backup,backup,backup,backup,backup,backup,backup,backup,backup,backup,backup";/*conversionvm_backup,vm_recovery,vm_catalog_fs,mount_recovery_point,office365_backup,cifs_backup,sharepoint_backup,vm_merge,catalog_fs,catalog_app,catalog_grt,file_copy_backup,file_copy_purge,file_copy_restore,file_copy_catalog_sync,file_copy_source_delete,file_copy_delete,catalog_fs_ondemand,vm_catalog_fs_ondemand,rps_replicate,rps_replicate_in_bound,rps_merge,rps_conversion,bmr,rps_data_seeding,rps_data_seeding_in,vm_recovery_hyperv,rps_purge_datastore,start_instant_vm,stop_instant_vm,assure_recovery,start_instant_vhd,stop_instant_vhd,archive_to_tape,linux_instant_vm";*/
			String job_Status= "idle,canceled,failed,incomplete,finished,waiting,crash,license_failed,backupjob_proc_exit,skipped,stop,missed";
			String job_Method = "full,incremental,resync,archive,copy,full,incremental,full,incremental,resync,archive,copy";
			String Job_severity="success,warning,error,critical,informationsuccess, warning,success, warning, error, critical, information";
			String job_type[]=job_Type.split(",");String job_method1[]=job_Method.split(",");String job_status[]=job_Status.split(",");
			String job_severity[]=Job_severity.split(",");
			//spogServer.setToken(adminToken);
			//String organization_name=spogServer.getLoggedInOrganizationName();

			for (int k=0;k<1;k++) {
				gatewayServer.setToken(site_Token);
				test.log(LogStatus.INFO, "creating job_tye "+job_type[k]+"job_staus"+job_status[k]+"job_method"+job_method1[k]);
				job_id = gatewayServer.postJobWithCheck(start_time_ts,endTimeTS, organization_id, source_id, source_id, rps_id,destination_id, policy_id,
						job_type[k], job_method1[k], job_status[k], site_Token, test);	
				spogDestinationServer.setToken(site_Token);

				last_job=spogDestinationServer.composeLastJob(start_time_ts,endTimeTS,percent_complete,job_status[k],job_type[k],job_method1[k]);

				//gatewayServer.postJobDataWithCheck(job_id,job_seq,Job_severity, percent_complete, protected_data_size, raw_data_size, sync_read_size, ntfs_volume_size, virtual_disk_provision_size, adminToken, test);
				spogreportServer.setToken(adminToken);
				jobs.add(job_id);

				temp=composeExpectedReports(job_id,job_status[k],source_id,soucres.get(k),destiantion_id,destination_name,policy_id,policy_name,group_id,group_name,organization_id,organization_type);

				ExpectedReports.add(temp);
				//get backupjobreportsTopsourcesfor logged in user 
			}
		}

		spogreportServer.getBackupReportsDetails(adminToken,"",SpogConstants.SUCCESS_GET_PUT_DELETE,null,test);


		//String time_resolution="hour,day,month,year";
		//String []additionalUrl=time_resolution.split(",");

		for (int j=0;j<1;j++) {
			//int random=gen_random_index(additionalUrl);
			spogreportServer.setToken(adminToken);

			//filer on the policy_id
			String url=spogServer.PrepareURL("policy_id;=;"+policy_id, "", 1, 20, test);
			response=spogreportServer.getBackupReportsDetails(adminToken,url,SpogConstants.SUCCESS_GET_PUT_DELETE,null,test);
			spogreportServer.checkJobReportDetails(response, 1, 20, "","policy_id;=;"+policy_id, ExpectedReports, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

			//filter on the source_group_id
			String url_1=spogServer.PrepareURL("source_group_id;=;"+group_id, "", 1, 20, test);
			response=spogreportServer.getBackupReportsDetails(adminToken,url_1,SpogConstants.SUCCESS_GET_PUT_DELETE,null,test);
			spogreportServer.checkJobReportDetails(response, 1, 20, "","source_group_id;=;"+group_id, ExpectedReports, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

			//filter on the destination_id
			String url_2=spogServer.PrepareURL("destination_id;=;"+destiantion_id, "", 1, 20, test);
			response=spogreportServer.getBackupReportsDetails(adminToken,url_2,SpogConstants.SUCCESS_GET_PUT_DELETE,null,test);
			spogreportServer.checkJobReportDetails(response, 1, 20, "","destination_id;=;"+destiantion_id, ExpectedReports, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);


			//filter on the organization_id
			String url_3=spogServer.PrepareURL("organization_id;=;"+organization_id, "", 1, 20, test);
			response=spogreportServer.getBackupReportsDetails(adminToken,url_3,SpogConstants.SUCCESS_GET_PUT_DELETE,null,test);
			spogreportServer.checkJobReportDetails(response, 1, 20, "","organization_id;=;"+organization_id, ExpectedReports, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

			//filter on the random policy id
			String url_4=spogServer.PrepareURL("policy_id;=;"+UUID.randomUUID().toString(), "", 1, 20, test);
			response=spogreportServer.getBackupReportsDetails(adminToken,url_4,SpogConstants.SUCCESS_GET_PUT_DELETE,null,test);
			spogreportServer.checkJobReportDetails(response, 1, 20, "","policy_id;=;"+UUID.randomUUID().toString(), ExpectedReports, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);


			//filter on the random source_group_id
			String url_5=spogServer.PrepareURL("source_group_id;=;"+UUID.randomUUID().toString(), "", 1, 20, test);
			response=spogreportServer.getBackupReportsDetails(adminToken,url_5,SpogConstants.SUCCESS_GET_PUT_DELETE,null,test);
			spogreportServer.checkJobReportDetails(response, 1, 20, "","source_group_id;=;"+UUID.randomUUID().toString(), ExpectedReports, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);


			//filter on the random organziation_id
			String url_6=spogServer.PrepareURL("organization_id;=;"+UUID.randomUUID().toString(), "", 1, 20, test);
			response=spogreportServer.getBackupReportsDetails(adminToken,url_6,SpogConstants.SUCCESS_GET_PUT_DELETE,null,test);
			spogreportServer.checkJobReportDetails(response, 1, 20, "","source_group_id;=;"+UUID.randomUUID().toString(), ExpectedReports, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

			//filter on the sourcename
			String url_7=spogServer.PrepareURL("source_name;=;"+soucres.get(j), "", 1, 20, test);
			response=spogreportServer.getBackupReportsDetails(adminToken,url_7,SpogConstants.SUCCESS_GET_PUT_DELETE,null,test);
			spogreportServer.checkJobReportDetails(response, 1, 20, "","source_name;=;"+soucres.get(j), ExpectedReports, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

			//create backup jobs report for the organization.
			test.log(LogStatus.INFO,"create report schedule ");
			HashMap<String, Object> scheduleInfo=new  HashMap<String, Object>();
			scheduleInfo = spogreportServer.composeReportScheduleInfo("report_name", "backup_jobs", "last_7_days", System.currentTimeMillis(), System.currentTimeMillis(),"monthly","", organization_id, "a@gmail.com,b@gmail.com", null,"all_sources");
			spogreportServer.createReportScheduleWithCheck_audit(adminToken, scheduleInfo,  SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			
			ArrayList<HashMap<String, Object>> expectedReportsInfo=new ArrayList<HashMap<String, Object>>();
			expectedReportsInfo.add(scheduleInfo);
			
			response = spogreportServer.getReportsWithCheck(adminToken, expectedReportsInfo, "", "", 1, 20,SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			String report_id=response.then().extract().path("data.get(0).report_id");


			//get backup job reports details filter with report_id
			String additionalUrl="report_id;=;"+report_id;
			response=spogreportServer.getBackupReportsDetails(adminToken,additionalUrl,SpogConstants.SUCCESS_GET_PUT_DELETE,null,test);
			spogreportServer.checkJobReportDetails(response, 1, 20, "",additionalUrl, ExpectedReports, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			
			String token = null;
			if (organization_type.contains("Direct"))
				token = ti.direct_org1_monitor_user1_token;
			else if (organization_type.contains("Normal"))
				token = ti.normal_msp1_suborg1_monitor_user1_token;
			else if (organization_type.contains("Root"))
				token = ti.root_msp1_suborg1_monitor_user1_token;
			else
				token = ti.msp1_submsp1_suborg1_monitor_user1_token;
			test.log(LogStatus.INFO, "get backup report details using monitor role");
			response=spogreportServer.getBackupReportsDetails(token,"",SpogConstants.SUCCESS_GET_PUT_DELETE,null,test);
			spogreportServer.checkJobReportDetails(response, 1, 20, "","", ExpectedReports, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			
		}
	}



	@DataProvider(name="getbakupjobreports_401")
	public final Object[][] getDestination_invalid(){
		return new Object[][]{
			{"invalid token to get the BackupReportsDetails"}, 
			{"invalid token to get the BackupReportsDetails"}, 

		};
	}    


	@Test(dataProvider="getbakupjobreports_401")    
	public void getbackupjobreportsforloggedinuser_invalidtoken(String testcase

			){

		String time_resolution="hour,day,month,year";
		String []additionalUrl=time_resolution.split(",");
		int random=gen_random_index(additionalUrl);

		//missed token
		spogreportServer.getBackupReportsDetails("",additionalUrl[random],SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTHENTICATION_FAILED,test);
		//in valid token
		spogreportServer.getBackupReportsDetails("JUNK",additionalUrl[random],SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT,test);
	}

	private HashMap<String, Object> composeExpectedReports(String job_id, String jobstatus, String source_id,
			String source_name, String destination_id, String destination_name, String policy_id,
			String policy_name, String group_id, String group_name, String organization_id, String organization_name) {
		// TODO Auto-generated method stub

		HashMap<String,Object> composed_reports=new  HashMap<String,Object>();
		composed_reports.put("job_id", job_id);
		composed_reports.put("job_status", jobstatus);
		composed_reports.put("source_id", source_id);
		composed_reports.put("source_name", source_name);

		composed_reports.put("destination_id", destination_id);
		composed_reports.put("destination_name", destination_name);
		composed_reports.put("policy_id", policy_id);
		composed_reports.put("policy_name", policy_name);

		composed_reports.put("group_id", group_id);
		composed_reports.put("group_name", group_name);
		composed_reports.put("organization_id", organization_id);
		composed_reports.put("organization_name", organization_name);


		return composed_reports;
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