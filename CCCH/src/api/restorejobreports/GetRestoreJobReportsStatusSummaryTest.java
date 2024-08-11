package api.restorejobreports;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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

import Constants.ConnectionStatus;
import Constants.DestinationStatus;
import Constants.DestinationType;
import Constants.LogSeverityType;
import Constants.LogSourceType;
import Constants.OSMajor;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
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


public class GetRestoreJobReportsStatusSummaryTest extends base.prepare.Is4Org {


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

	private ArrayList<String> jobs=new ArrayList<String>();
	private ArrayList<String> servers=new ArrayList<String>();
	private Log4SPOGServer log4SpogServer;
	private ArrayList<String> logs=new ArrayList<String>();
	private ArrayList<String> filters=new ArrayList<String>();

	String direct_site_id;

	String sourceName;
	String direct_server_id;
	String normal_msp_site_id,normal_msp_site_token;

	String root_msp_site_id,root_msp_site_token,sub_msp_site_id,sub_msp_site_token;
	String root_subOrg_Server_id,submsp_subOrg_Server_id;
	String normal_subOrg_Server_id;
	String source_id,destination_id;

	@BeforeClass
	@Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword, String logFolder,String runningMachine,String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		policy4SpogServer=new Policy4SPOGServer(baseURI,port); 
		spogDestinationServer=	new SPOGDestinationServer(baseURI,port); 
		spogreportServer=new  SPOGReportServer(baseURI,port); 
		rep = ExtentManager.getInstance("GetRestoreJobReportsStatusSummaryTest", logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Sykam.Naga Malleswari";

		Nooftest=0;
		//Used for creating a build number with dateformat
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date=new java.util.Date();
		this.buildVersion=buildVersion+"_"+dateFormater.format(date);

		BQName=this.getClass().getSimpleName();
		this.runningMachine=runningMachine;
		System.out.println("The value of hit is "+count1.isstarttimehit());
		ti = new TestOrgInfo(spogServer, test);

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
			sourceName = spogServer.ReturnRandom("Malleswari");	

			//Direct Organization
			Response response = spogServer.getCloudAccounts(ti.direct_org1_user1_token, "", test);
			direct_site_id = response.then().extract().path("data[0].cloud_account_id");

			servers.add(sourceName);
			spogServer.setToken(ti.direct_org1_user1_token);
			test.log(LogStatus.INFO,"create source");
			direct_server_id = spogServer.createSourceWithCheck(sourceName, SourceType.machine, SourceProduct.udp, ti.direct_org1_id, direct_site_id, ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(), 	"sql;exchange",  "Malleswari_vm2",  null, "Malleswari_agent1", "windows 2012", "64" ,"1.0.0","2.0", "http://upgrade",  test); 


			//Normal msp 
			response = spogServer.getCloudAccounts(ti.normal_msp_org1_user1_token, "", test);
			normal_msp_site_id = response.then().extract().path("data[0].cloud_account_id");
			spogServer.setToken(ti.normal_msp_org1_user1_token);
			normal_subOrg_Server_id = spogServer.createSourceWithCheck(sourceName, SourceType.machine, SourceProduct.udp, ti.normal_msp1_suborg1_id, normal_msp_site_id, ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(), 	"sql;exchange",  "Malleswari_vm2",  null, "Malleswari_agent1", "windows 2012", "64" ,"1.0.0","2.0", "http://upgrade",  test); 

			//root msp
			response = spogServer.getCloudAccounts(ti.root_msp_org1_user1_token, "", test);
			root_msp_site_id = response.then().extract().path("data[0].cloud_account_id");
			spogServer.setToken(ti.root_msp_org1_user1_token);
			root_subOrg_Server_id = spogServer.createSourceWithCheck(sourceName, SourceType.machine, SourceProduct.udp, ti.root_msp1_suborg1_id, root_msp_site_id, ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(), 	"sql;exchange",  "Malleswari_vm2", null, "Malleswari_agent1", "windows 2012", "64" ,"1.0.0","2.0", "http://upgrade",  test); 


			//Sub msp
			spogServer.setToken(ti.csr_token);
			response = spogServer.getCloudAccounts(ti.root_msp1_submsp1_user1_token, "", test);
			sub_msp_site_id = response.then().extract().path("data[0].cloud_account_id");
			submsp_subOrg_Server_id = spogServer.createSourceWithCheck(sourceName, SourceType.machine, SourceProduct.udp, ti.msp1_submsp1_sub_org1_id, sub_msp_site_id, ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(), 	"sql;exchange",  "Malleswari_vm2",  null, "Malleswari_agent1", "windows 2012", "64" ,"1.0.0","2.0", "http://upgrade",  test); 

		}
	}

	@DataProvider(name="getRestorejobreports")
	public final Object[][] getRestorejobreports(){
		return new Object[][]{
			{"Direct",ti.direct_org1_user1_token,ti.direct_org1_user1_token,ti.direct_org1_id,ti.direct_org1_user1_id,direct_site_id,direct_site_id,System.currentTimeMillis()/1000,System.currentTimeMillis()/1000,ti.direct_org1_user1_token,"1",30.20, "3","02","2", "3","3","3"}, 
			{"Sub Organization of Normal Msp",ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_id,normal_msp_site_id,normal_msp_site_id,System.currentTimeMillis()/1000,System.currentTimeMillis()/1000,ti.normal_msp1_suborg1_user1_token,"1",30.20, "3","02","2", "3","3","3"}, 
			{"Sub Organization of Root Msp",ti.root_msp_org1_user1_token,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_id,ti.root_msp1_suborg1_user1_id,root_msp_site_id,root_msp_site_id,System.currentTimeMillis()/1000,System.currentTimeMillis()/1000,ti.root_msp1_suborg1_user1_token,"1",30.20, "3","02","2", "3","3","3"}, 
			{"Sub Organization of Sub  Msp",ti.root_msp1_submsp1_user1_token,ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_sub_org1_id,ti.msp1_submsp1_suborg1_user1_id,sub_msp_site_id,sub_msp_site_id,System.currentTimeMillis()/1000,System.currentTimeMillis()/1000,ti.msp1_submsp1_suborg1_user1_token,"1",30.20, "3","02","2", "3","3","3"}, 

		};
	}    

	@Test(dataProvider="getRestorejobreports")    
	public void GetRestoreJobReportsStatusSummary(String organization_type,String token_Destination_creation,String adminToken,String organization_id,String create_user_id,
			String site_id,String cloud_id,Long start_time_ts,Long endTimeTS,String site_Token,String job_seq, Double percent_complete, 
			String protected_data_size, String raw_data_size, String sync_read_size,String ntfs_volume_size, String virtual_disk_provision_size,String dedupe_savings
			){

		ArrayList<HashMap<String,Object>> expectedsources=new 	ArrayList<HashMap<String,Object>>();
		ArrayList<String> soucres=new ArrayList<String>();
		String job_id=null,log_id=null, group_id=null;

		HashMap<String,ArrayList<HashMap<String,Object>>> last_job=new HashMap<String,ArrayList<HashMap<String,Object>>>();
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());

		test.assignAuthor("malleswari.sykam");     

		ArrayList<String> jobs=new ArrayList<String>();
		ArrayList<HashMap<String,Object>> ExpectedReports=new ArrayList<HashMap<String,Object>> ();
		HashMap<String,Object> temp=new HashMap<String,Object>();

		HashMap<String, String> retention = spogDestinationServer.composeRetention("0","0","0","0","0","0");
		HashMap<String, Object> cloud_direct_volume = spogDestinationServer.composeCloudDirectInfo(cloud_id,"dest_cloud_direct","7D","7 Days",26.0,24.0,50.0,volume_type.normal.toString(),"windows8",retention);	

		String destination_name="eswari_cloud_direct_volume";
		//create destination for direct
		spogDestinationServer.setToken(token_Destination_creation);
		Response  response=spogDestinationServer.createDestination(UUID.randomUUID().toString(),token_Destination_creation,cloud_id,organization_id,cloud_id,"91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", "20","cloud_direct_volume",DestinationStatus.running.toString(),destination_name,cloud_direct_volume,test);
		String destiantion_id=response.then().extract().path("data.destination_id");

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

		int finished=0,failed=0,canceled=0,count=0;
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


			String job_Type = "restore,restore,restore,restore,restore,restore,restore,restore,restore,restore,restore,restore";/*conversionvm_backup,vm_recovery,vm_catalog_fs,mount_recovery_point,office365_backup,cifs_backup,sharepoint_backup,vm_merge,catalog_fs,catalog_app,catalog_grt,file_copy_backup,file_copy_purge,file_copy_restore,file_copy_catalog_sync,file_copy_source_delete,file_copy_delete,catalog_fs_ondemand,vm_catalog_fs_ondemand,rps_replicate,rps_replicate_in_bound,rps_merge,rps_conversion,bmr,rps_data_seeding,rps_data_seeding_in,vm_recovery_hyperv,rps_purge_datastore,start_instant_vm,stop_instant_vm,assure_recovery,start_instant_vhd,stop_instant_vhd,archive_to_tape,linux_instant_vm";*/
			String job_Status= "idle,canceled,failed,incomplete,finished,waiting,crash,license_failed,backupjob_proc_exit,skipped,stop,missed";
			String job_Method = "full,incremental,resync,archive,copy,full,incremental,full,incremental,resync,archive,copy";
			String Job_severity="success,warning,error,critical,informationsuccess, warning,success, warning, error, critical, information";
			String job_type[]=job_Type.split(",");String job_method1[]=job_Method.split(",");String job_status[]=job_Status.split(",");
			String job_severity[]=Job_severity.split(",");


			for (int k=0;k<job_type.length;k++) {
				test.log(LogStatus.INFO, "creating job_tye "+job_type[k]+"job_staus"+job_status[k]+"job_method"+job_method1[k]);
				job_id = gatewayServer.postJobWithCheck(start_time_ts,endTimeTS, organization_id, source_id, source_id, rps_id,destination_id, policy_id,
						job_type[k], job_method1[k], job_status[k], site_Token, test);	
				last_job=spogDestinationServer.composeLastJob(start_time_ts,endTimeTS,percent_complete,job_status[k],job_type[k],job_method1[k]);


				gatewayServer.postJobDataWithCheck(job_id,job_seq,Job_severity, percent_complete, protected_data_size, raw_data_size, sync_read_size, ntfs_volume_size, virtual_disk_provision_size, adminToken, test);
				spogreportServer.setToken(adminToken);

				//active,finished,canceled,failed,incomplete,idle,waiting,crash,license_failed,backupjob_proc_exit,skipped,stop,missed	
				if(job_status[k].equals("finished")||job_status[k].equals("incomplete")||job_status[k].equals("backupjob_proc_exit"))
				{
					job_status[k]="finished";
					finished++;
				}
				else if(job_status[k].equals("waiting")||job_status[k].equals("failed")||job_status[k].equals("license_failed")||job_status[k].equals("skipped")||job_status[k].equals("stop")||job_status[k].equals("idle")||job_status[k].equals("waiting"))
				{
					job_status[k]="failed";
					failed++;
				}
				else if(job_status[k].equals("canceled"))
				{
					job_status[k]="canceled";
					canceled++;
				}

				//	",,,,,waiting,,license_failed,,skipped,stop,"
				if(k<10)
				{
					if(!(job_status[k].equals("active")||job_status[k].equals("skipped")||job_status[k].equals("stop")||job_status[k].equals("idle")||job_status[k].equals("waiting")))
					{
						/*if(job_status[k].equals("failed"))
							count=failed;

						if(job_status[k].equals("finished"))
							count=finished;

						if(job_status[k].equals("canceled"))
							count=canceled;*/

					}


				} 

			}
			temp=composeTopSources(finished,"finished",finished);

			expectedsources.add(temp);

			temp=composeTopSources(canceled,"canceled",canceled);

			expectedsources.add(temp);

			temp=composeTopSources(failed,"failed",failed);

			expectedsources.add(temp);

			count=0;
			failed=0;
			finished=0;
			canceled=0;

		}


		//		String time_resolution="hour,day,month,year";
		String time_resolution="day,month,year";
		String []additionalUrl=time_resolution.split(",");
		String Date_range="last_24_hours,last_7_days,last_1_month";
		String []Daterange=Date_range.split(",");

		for(int s=0;s<Daterange.length;s++) {


			//create backup jobs report for the organization.
			test.log(LogStatus.INFO,"create report schedule ");
			HashMap<String, Object> scheduleInfo=new  HashMap<String, Object>();
			scheduleInfo = spogreportServer.composeReportScheduleInfo("report_name", "restore_jobs", Daterange[s], System.currentTimeMillis(), System.currentTimeMillis(),"monthly","", organization_id, "a@gmail.com,b@gmail.com", null,"all_sources");
			spogreportServer.createReportScheduleWithCheck_audit(adminToken, scheduleInfo,  SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

			ArrayList<HashMap<String, Object>> expectedReportsInfo=new ArrayList<HashMap<String, Object>>();
			expectedReportsInfo.add(scheduleInfo);

			response = spogreportServer.getReportsWithCheck(adminToken, expectedReportsInfo, "", "", 1, 20,SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			String report_id=response.then().extract().path("data.get(0).report_id");



			for (int j=0;j<additionalUrl.length;j++) {

				int random=gen_random_index(additionalUrl);
				spogreportServer.setToken(adminToken);
				test.log(LogStatus.INFO,"random "+random );
				spogreportServer.GetRestoreJobReportsStatusSummaryTest(adminToken,expectedsources,additionalUrl[j],SpogConstants.SUCCESS_GET_PUT_DELETE,null,test);

				if(j<soucres.size())
				{
					test.log(LogStatus.INFO,"Apply filter with source name "+soucres );
					String filter= "&source_name="+soucres.get(j);
					spogreportServer.GetRestoreJobReportsStatusSummaryTest(adminToken,expectedsources,additionalUrl[j]+filter,SpogConstants.SUCCESS_GET_PUT_DELETE,null,test);

					//	Appliy filter on the not existed source name

					filter= "&source_name="+"eswari";
					spogreportServer.GetRestoreJobReportsStatusSummaryTest(adminToken,expectedsources,additionalUrl[j]+filter,SpogConstants.SUCCESS_GET_PUT_DELETE,null,test);

					//Appliy filter source name is null 

					filter= "&source_name="+"";
					spogreportServer.GetRestoreJobReportsStatusSummaryTest(adminToken,expectedsources,additionalUrl[j]+filter,SpogConstants.SUCCESS_GET_PUT_DELETE,null,test);

				}
				//Get restoreJobs status summary with the Valid report id 
				String additionalurl="&report_id="+report_id;
				spogServer.setUUID(report_id);
				spogreportServer.GetRestoreJobReportsStatusSummaryTest(adminToken,expectedsources,additionalUrl[j]+additionalurl,SpogConstants.SUCCESS_GET_PUT_DELETE,null,test);

				//Get restoreJobs status summary with the invalid report id 
				//additionalurl="&report_id="+123+report_id;
				//spogreportServer.GetRestoreJobReportsStatusSummaryTest(adminToken,expectedsources,additionalUrl[j]+additionalurl,SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.UUID_IS_INVALID,test);

				//Get restoreJobs status summary with the report id is not UUID 
				/*additionalurl="&report_id="+123;
				spogServer.setUUID("123");
				spogreportServer.GetRestoreJobReportsStatusSummaryTest(adminToken,expectedsources,additionalUrl[j]+additionalurl,SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.UUID_IS_INVALID,test);


				//Get restoreJobs status summary with the report id is null
				additionalurl="&report_id="+"";
				spogServer.setUUID("");
				spogreportServer.GetRestoreJobReportsStatusSummaryTest(adminToken,expectedsources,additionalUrl[j]+additionalurl,SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.UUID_IS_INVALID,test);
				 *//*

				//Delete filter id
				spogreportServer.deleteReportListFiltersForLoggedInUserByFilterIdWithCheck(adminToken,report_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null,test);
				spogreportServer.deleteReportsByIdWithCheck(adminToken, report_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

				//Get restoreJobs status summary with the deleted report_id
				additionalurl="&report_id="+report_id;
				spogreportServer.GetRestoreJobReportsStatusSummaryTest(adminToken,expectedsources,additionalUrl[j]+additionalurl,SpogConstants.SUCCESS_GET_PUT_DELETE,null,test);


				  */	}

		}

	}


	@DataProvider(name="getRestorejobreports_401")
	public final Object[][] getRestore(){
		return new Object[][]{
			{"invalid token to get the getRestorejobreports"}, 
			{"invalid token to get the getRestorejobreports"}, 

		};
	}    


	@Test(dataProvider="getRestorejobreports_401")    
	public void getRestorejobreportsforloggedinuser_invalidtoken(String testcase

			){

		String time_resolution="hour, day, month, year";

		String []additionalUrl=time_resolution.split(",");

		int random=gen_random_index(additionalUrl);

		//missed token
		spogreportServer.GetRestoreJobReportsStatusSummaryTest("",null, additionalUrl[random],SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTHENTICATION_FAILED,test);
		//in valid token
		spogreportServer.GetRestoreJobReportsStatusSummaryTest("JUNK",null,additionalUrl[random],SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT,test);
	}



	//compose top sources for the Organization for validation

	private HashMap<String, Object> composeTopSources(int job_count, String job_status, int value) {
		// TODO Auto-generated method stub

		HashMap<String, Object> exp=new HashMap<String, Object>();
		exp.put("job_count", job_count);
		exp.put("job_status", job_status);

		Date current_date=new Date();

		int exp_day=current_date.getDate();
		int exp_month=current_date.getMonth();
		exp_month=exp_month+1;
		int exp_year=2018;

		exp.put("time_instance", new SimpleDateFormat("yyyy,M,d,hh:mm:ss").format(new Date()));

		return exp;
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




