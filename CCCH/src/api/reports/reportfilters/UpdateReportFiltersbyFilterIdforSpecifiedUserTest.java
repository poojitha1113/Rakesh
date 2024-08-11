package api.reports.reportfilters;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.DestinationType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGReportServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;

public class UpdateReportFiltersbyFilterIdforSpecifiedUserTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private SPOGReportServer spogReportServer;
	private ExtentTest test;
	private TestOrgInfo ti;
	private String destination_id = UUID.randomUUID().toString();

	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine, String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		spogReportServer = new SPOGReportServer(baseURI, port);
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(), logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Ramya.Nagepalli";

		Nooftest = 0;
		// Used for creating a build number with dateformat
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date = new java.util.Date();
		this.buildVersion = buildVersion + "_" + dateFormater.format(date);

		BQName = this.getClass().getSimpleName();
		this.runningMachine = runningMachine;
		System.out.println("The value of hit is " + count1.isstarttimehit());

		if (count1.isstarttimehit() == 0) {
			System.out.println("into creation time");
			creationTime = System.currentTimeMillis();
			count1.setcreationtime(creationTime);

			// creationTime = System.currentTimeMillis();
			try {
				bqdb1.updateTable(BQName, this.runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0",
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
		ti = new TestOrgInfo(spogServer, test);
	}

	@DataProvider(name = "createreportfilters", parallel = false)
	public final Object[][] createreportfilters() {

		return new Object[][] {

				// Direct
				{ "Update report filter for backup jobs for direct org", ti.direct_org1_id, ti.direct_org1_id,
						ti.direct_org1_user1_id, ti.direct_org1_user1_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_7_days",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "backup_jobs", "7D",
						DestinationType.cloud_direct_volume.toString(), "backup",
						spogServer.ReturnRandom("filter_name"), "Tue,Wed,Mon", "backup_jobs", true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Update report filter for jobs and destinations for direct org", ti.direct_org1_id, ti.direct_org1_id,
						ti.direct_org1_user1_id, ti.direct_org1_user1_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_1_month",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "jobs_and_destinations", "1M",
						DestinationType.cloud_direct_volume.toString(), "backup,copy",
						spogServer.ReturnRandom("filter_name"), "Thu,Sat", "jobs_and_destinations", true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Update report filter for jobs and destinations for direct org", ti.direct_org1_id, ti.direct_org1_id,
						ti.direct_org1_user1_id, ti.direct_org1_user1_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_1_month",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "jobs_and_destinations", "1M",
						DestinationType.cloud_direct_volume.toString(),
						"rps_replicate,copy,rps_replicate_in_bound,assure_recovery,start_instant_vhd,stop_instant_vhd,merge,restore",
						spogServer.ReturnRandom("filter_name"), "Thu,Sat", "jobs_and_destinations", true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Update report filter for restore jobs for direct org", ti.direct_org1_id, ti.direct_org1_id,
						ti.direct_org1_user1_id, ti.direct_org1_user1_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "today",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "restore_jobs", "6M",
						DestinationType.cloud_hybrid_store.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "restore_jobs", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Update report filter for data transfer for direct org", ti.direct_org1_id, ti.direct_org1_id,
						ti.direct_org1_user1_id, ti.direct_org1_user1_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_3_months",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "data_transfer", "2Y",
						DestinationType.cloud_direct_volume.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "data_transfer", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Update report filter for capacity usage for direct org", ti.direct_org1_id, ti.direct_org1_id,
						ti.direct_org1_user1_id, ti.direct_org1_user1_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_1_year",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "capacity_usage", "forever",
						DestinationType.cloud_direct_volume.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "capacity_usage", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				// MSP

				{ "Update report filter for backup jobs for msporg",
						ti.normal_msp1_suborg1_id + "," + ti.normal_msp_org1_id, ti.normal_msp_org1_id,
						ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_token,
						spogServer.ReturnRandom("source_name"), spogServer.ReturnRandom("destination_name"),
						UUID.randomUUID().toString(), "last_7_days", System.currentTimeMillis(),
						System.currentTimeMillis() + 3600000, "backup_jobs", "7D",
						DestinationType.cloud_direct_volume.toString(), "backup",
						spogServer.ReturnRandom("filter_name"), "Tue,Wed,Mon", "backup_jobs", true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Update report filter for jobs and destinations for msporg",
						ti.normal_msp1_suborg1_id + "," + ti.normal_msp_org1_id, ti.normal_msp_org1_id,
						ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_token,
						spogServer.ReturnRandom("source_name"), spogServer.ReturnRandom("destination_name"),
						UUID.randomUUID().toString(), "last_1_month", System.currentTimeMillis(),
						System.currentTimeMillis() + 3600000, "jobs_and_destinations", "1M",
						DestinationType.cloud_direct_volume.toString(), "backup,copy",
						spogServer.ReturnRandom("filter_name"), "Thu,Sat", "jobs_and_destinations", true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Update report filter for jobs and destinations for msporg",
						ti.normal_msp1_suborg1_id + "," + ti.normal_msp_org1_id, ti.normal_msp_org1_id,
						ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_token,
						spogServer.ReturnRandom("source_name"), spogServer.ReturnRandom("destination_name"),
						UUID.randomUUID().toString(), "last_1_month", System.currentTimeMillis(),
						System.currentTimeMillis() + 3600000, "jobs_and_destinations", "1M",
						DestinationType.cloud_direct_volume.toString(),
						"rps_replicate,copy,rps_replicate_in_bound,assure_recovery,start_instant_vhd,stop_instant_vhd,merge,restore",
						spogServer.ReturnRandom("filter_name"), "Thu,Sat", "jobs_and_destinations", true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Update report filter for restore jobs for msporg",
						ti.normal_msp1_suborg1_id + "," + ti.normal_msp_org1_id, ti.normal_msp_org1_id,
						ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_token,
						spogServer.ReturnRandom("source_name"), spogServer.ReturnRandom("destination_name"),
						UUID.randomUUID().toString(), "today", System.currentTimeMillis(),
						System.currentTimeMillis() + 3600000, "restore_jobs", "6M",
						DestinationType.cloud_hybrid_store.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "restore_jobs", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Update report filter for data transfer for msporg",
						ti.normal_msp1_suborg1_id + "," + ti.normal_msp_org1_id, ti.normal_msp_org1_id,
						ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_token,
						spogServer.ReturnRandom("source_name"), spogServer.ReturnRandom("destination_name"),
						UUID.randomUUID().toString(), "last_3_months", System.currentTimeMillis(),
						System.currentTimeMillis() + 3600000, "data_transfer", "2Y",
						DestinationType.cloud_direct_volume.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "data_transfer", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Update report filter for capacity usage for msporg",
						ti.normal_msp1_suborg1_id + "," + ti.normal_msp_org1_id, ti.normal_msp_org1_id,
						ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_token,
						spogServer.ReturnRandom("source_name"), spogServer.ReturnRandom("destination_name"),
						UUID.randomUUID().toString(), "last_1_year", System.currentTimeMillis(),
						System.currentTimeMillis() + 3600000, "capacity_usage", "forever",
						DestinationType.cloud_direct_volume.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "capacity_usage", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_GET_PUT_DELETE, null },

				// Suborg + MSP Account Admin

				{ "Update report filter for backup jobs for suborg using msp account admin", ti.normal_msp1_suborg1_id,
						ti.normal_msp_org1_id, ti.normal_msp_org1_msp_accountadmin1_id,
						ti.normal_msp_org1_msp_accountadmin1_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_7_days",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "backup_jobs", "7D",
						DestinationType.cloud_direct_volume.toString(), "backup",
						spogServer.ReturnRandom("filter_name"), "Tue,Wed,Mon", "backup_jobs", true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Update report filter for jobs and destinations for suborg using msp account admin",
						ti.normal_msp1_suborg1_id, ti.normal_msp_org1_id, ti.normal_msp_org1_msp_accountadmin1_id,
						ti.normal_msp_org1_msp_accountadmin1_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_1_month",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "jobs_and_destinations", "1M",
						DestinationType.cloud_direct_volume.toString(), "backup,copy",
						spogServer.ReturnRandom("filter_name"), "Thu,Sat", "jobs_and_destinations", true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Update report filter for jobs and destinations for suborg using msp account admin",
						ti.normal_msp1_suborg1_id, ti.normal_msp_org1_id, ti.normal_msp_org1_msp_accountadmin1_id,
						ti.normal_msp_org1_msp_accountadmin1_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_1_month",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "jobs_and_destinations", "1M",
						DestinationType.cloud_direct_volume.toString(),
						"rps_replicate,copy,rps_replicate_in_bound,assure_recovery,start_instant_vhd,stop_instant_vhd,merge,restore",
						spogServer.ReturnRandom("filter_name"), "Thu,Sat", "jobs_and_destinations", true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Update report filter for restore jobs for suborg using msp account admin", ti.normal_msp1_suborg1_id,
						ti.normal_msp_org1_id, ti.normal_msp_org1_msp_accountadmin1_id,
						ti.normal_msp_org1_msp_accountadmin1_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "today",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "restore_jobs", "6M",
						DestinationType.cloud_hybrid_store.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "restore_jobs", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Update report filter for data transfer for suborg using msp account admin",
						ti.normal_msp1_suborg1_id, ti.normal_msp_org1_id, ti.normal_msp_org1_msp_accountadmin1_id,
						ti.normal_msp_org1_msp_accountadmin1_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_3_months",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "data_transfer", "2Y",
						DestinationType.cloud_direct_volume.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "data_transfer", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Update report filter for capacity usage for suborg using msp account admin",
						ti.normal_msp1_suborg1_id, ti.normal_msp_org1_id, ti.normal_msp_org1_msp_accountadmin1_id,
						ti.normal_msp_org1_msp_accountadmin1_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_1_year",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "capacity_usage", "forever",
						DestinationType.cloud_direct_volume.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "capacity_usage", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_GET_PUT_DELETE, null },

				// Suborg

				{ "Update report filter for backup jobs for suborg using suborg token", ti.normal_msp1_suborg1_id,
						ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg1_user1_token,
						spogServer.ReturnRandom("source_name"), spogServer.ReturnRandom("destination_name"),
						UUID.randomUUID().toString(), "last_7_days", System.currentTimeMillis(),
						System.currentTimeMillis() + 3600000, "backup_jobs", "7D",
						DestinationType.cloud_direct_volume.toString(), "backup",
						spogServer.ReturnRandom("filter_name"), "Tue,Wed,Mon", "backup_jobs", true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Update report filter for jobs and destinations for suborg using suborg token",
						ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_id,
						ti.normal_msp1_suborg1_user1_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_1_month",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "jobs_and_destinations", "1M",
						DestinationType.cloud_direct_volume.toString(), "backup,copy",
						spogServer.ReturnRandom("filter_name"), "Thu,Sat", "jobs_and_destinations", true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Update report filter for jobs and destinations for suborg using suborg token",
						ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_id,
						ti.normal_msp1_suborg1_user1_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_1_month",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "jobs_and_destinations", "1M",
						DestinationType.cloud_direct_volume.toString(),
						"rps_replicate,copy,rps_replicate_in_bound,assure_recovery,start_instant_vhd,stop_instant_vhd,merge,restore",
						spogServer.ReturnRandom("filter_name"), "Thu,Sat", "jobs_and_destinations", true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Update report filter for restore jobs for suborg using suborg token", ti.normal_msp1_suborg1_id,
						ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg1_user1_token,
						spogServer.ReturnRandom("source_name"), spogServer.ReturnRandom("destination_name"),
						UUID.randomUUID().toString(), "today", System.currentTimeMillis(),
						System.currentTimeMillis() + 3600000, "restore_jobs", "6M",
						DestinationType.cloud_hybrid_store.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "restore_jobs", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Update report filter for data transfer for suborg using suborg token", ti.normal_msp1_suborg1_id,
						ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg1_user1_token,
						spogServer.ReturnRandom("source_name"), spogServer.ReturnRandom("destination_name"),
						UUID.randomUUID().toString(), "last_3_months", System.currentTimeMillis(),
						System.currentTimeMillis() + 3600000, "data_transfer", "2Y",
						DestinationType.cloud_direct_volume.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "data_transfer", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Update report filter for capacity usage for suborg using suborg token", ti.normal_msp1_suborg1_id,
						ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg1_user1_token,
						spogServer.ReturnRandom("source_name"), spogServer.ReturnRandom("destination_name"),
						UUID.randomUUID().toString(), "last_1_year", System.currentTimeMillis(),
						System.currentTimeMillis() + 3600000, "capacity_usage", "forever",
						DestinationType.cloud_direct_volume.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "capacity_usage", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_GET_PUT_DELETE, null },

				// Suborg using msp token

				{ "Update report filter for backup jobs for suborg using msp token", ti.normal_msp1_suborg1_id,
						ti.normal_msp_org1_id, ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_token,
						spogServer.ReturnRandom("source_name"), spogServer.ReturnRandom("destination_name"),
						UUID.randomUUID().toString(), "last_7_days", System.currentTimeMillis(),
						System.currentTimeMillis() + 3600000, "backup_jobs", "7D",
						DestinationType.cloud_direct_volume.toString(), "backup",
						spogServer.ReturnRandom("filter_name"), "Tue,Wed,Mon", "backup_jobs", true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Update report filter for jobs and destinations for suborg using msp token",
						ti.normal_msp1_suborg1_id, ti.normal_msp_org1_id, ti.normal_msp_org1_user1_id,
						ti.normal_msp_org1_user1_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_1_month",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "jobs_and_destinations", "1M",
						DestinationType.cloud_direct_volume.toString(), "backup,copy",
						spogServer.ReturnRandom("filter_name"), "Thu,Sat", "jobs_and_destinations", true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Update report filter for jobs and destinations for suborg using msp token",
						ti.normal_msp1_suborg1_id, ti.normal_msp_org1_id, ti.normal_msp_org1_user1_id,
						ti.normal_msp_org1_user1_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_1_month",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "jobs_and_destinations", "1M",
						DestinationType.cloud_direct_volume.toString(),
						"rps_replicate,copy,rps_replicate_in_bound,assure_recovery,start_instant_vhd,stop_instant_vhd,merge,restore",
						spogServer.ReturnRandom("filter_name"), "Thu,Sat", "jobs_and_destinations", true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Update report filter for restore jobs for suborg using msp token", ti.normal_msp1_suborg1_id,
						ti.normal_msp_org1_id, ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_token,
						spogServer.ReturnRandom("source_name"), spogServer.ReturnRandom("destination_name"),
						UUID.randomUUID().toString(), "today", System.currentTimeMillis(),
						System.currentTimeMillis() + 3600000, "restore_jobs", "6M",
						DestinationType.cloud_hybrid_store.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "restore_jobs", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Update report filter for data transfer for suborg using msp token", ti.normal_msp1_suborg1_id,
						ti.normal_msp_org1_id, ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_token,
						spogServer.ReturnRandom("source_name"), spogServer.ReturnRandom("destination_name"),
						UUID.randomUUID().toString(), "last_3_months", System.currentTimeMillis(),
						System.currentTimeMillis() + 3600000, "data_transfer", "2Y",
						DestinationType.cloud_direct_volume.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "data_transfer", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Update report filter for capacity usage for suborg using msp token", ti.normal_msp1_suborg1_id,
						ti.normal_msp_org1_id, ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_token,
						spogServer.ReturnRandom("source_name"), spogServer.ReturnRandom("destination_name"),
						UUID.randomUUID().toString(), "last_1_year", System.currentTimeMillis(),
						System.currentTimeMillis() + 3600000, "capacity_usage", "forever",
						DestinationType.cloud_direct_volume.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "capacity_usage", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				// monitor cases
				{ "Update report filter for capacity usage for direct using monitor token", ti.direct_org1_id,
						ti.direct_org1_id, ti.direct_org1_monitor_user1_id, ti.direct_org1_monitor_user1_token,
						spogServer.ReturnRandom("source_name"), spogServer.ReturnRandom("destination_name"),
						UUID.randomUUID().toString(), "last_1_year", System.currentTimeMillis(),
						System.currentTimeMillis() + 3600000, "capacity_usage", "forever",
						DestinationType.cloud_direct_volume.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "capacity_usage", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Update report filter for capacity usage for msp using monitor token", ti.normal_msp_org1_id,
						ti.normal_msp_org1_id, ti.normal_msp_org1_monitor_user1_id,
						ti.normal_msp_org1_monitor_user1_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_1_year",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "capacity_usage", "forever",
						DestinationType.cloud_direct_volume.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "capacity_usage", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Update report filter for capacity usage for msp suborg using monitor token",
						ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_monitor_user1_id,
						ti.normal_msp1_suborg1_monitor_user1_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_1_year",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "capacity_usage", "forever",
						DestinationType.cloud_direct_volume.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "capacity_usage", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Update report filter for capacity usage for root msp using monitor token", ti.root_msp_org1_id,
						ti.root_msp_org1_id, ti.root_msp_org1_monitor_user1_id, ti.root_msp_org1_monitor_user1_token,
						spogServer.ReturnRandom("source_name"), spogServer.ReturnRandom("destination_name"),
						UUID.randomUUID().toString(), "last_1_year", System.currentTimeMillis(),
						System.currentTimeMillis() + 3600000, "capacity_usage", "forever",
						DestinationType.cloud_direct_volume.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "capacity_usage", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Update report filter for capacity usage for root suborg using monitor token",
						ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_monitor_user1_id,
						ti.root_msp1_suborg1_monitor_user1_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_1_year",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "capacity_usage", "forever",
						DestinationType.cloud_direct_volume.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "capacity_usage", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Update report filter for capacity usage for root submsp using monitor token",
						ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_monitor_user1_id,
						ti.root_msp1_submsp1_monitor_user1_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_1_year",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "capacity_usage", "forever",
						DestinationType.cloud_direct_volume.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "capacity_usage", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Update report filter for capacity usage for submsp sub using monitor token",
						ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_monitor_user1_id,
						ti.msp1_submsp1_suborg1_monitor_user1_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_1_year",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "capacity_usage", "forever",
						DestinationType.cloud_direct_volume.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "capacity_usage", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_GET_PUT_DELETE, null },

		};
	}

	// Valid Cases - 200

	@Test(dataProvider = "createreportfilters")
	public void UpdateReportFiltersbyFilterId(String caseType, String organization_ids, String org_id, String user_id,
			String validToken, String source_name, String destination_name, String policy_id, String DateRangeType,
			long start_ts_dr, long end_ts_dr, String report_type, String retention_id, String destination_type,
			String job_type, String filter_name, String backup_schedule, String report_filter_type, boolean is_default,
			String groupName, String groupDescription, int noOfSourceGroupsToCreate, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage) {

		HashMap<String, Object> defaultfilterInfo = new HashMap<>();
		HashMap<String, Object> reportfilterInfo = new HashMap<>();

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		ArrayList<String> OrgIds = new ArrayList<>();

		test.assignAuthor("Ramesh Pendurthy");

		spogServer.setToken(validToken);

		// orgId for creating source_groups
		String orgId = null;
		if (caseType.contains("sub")) {
			orgId = ti.normal_msp1_suborg1_id; // hard coding the organization id for case
			// msp_account_admin
		} else {
			orgId = spogServer.GetLoggedinUserOrganizationID();
		}

		String source_group_ids = null, source_group_id = null;
		/*
		 * if (!(caseType.contains("csrreadonly"))) { for (int i = 0; i <
		 * noOfSourceGroupsToCreate; i++) { source_group_id =
		 * spogServer.createGroupWithCheck(orgId, groupName + i, groupDescription,
		 * test); if (i > 0) { source_group_ids = source_group_ids + "," +
		 * source_group_id; } else { source_group_ids = source_group_id; } } }
		 */

		spogReportServer.setToken(validToken);

		test.log(LogStatus.INFO, "Composing Report filters");
		defaultfilterInfo = spogReportServer.composeReportFilterInfo(source_name, destination_name, policy_id,
				destination_id, source_group_ids, organization_ids, DateRangeType, start_ts_dr, end_ts_dr, report_type,
				destination_type, job_type, "", backup_schedule, filter_name, is_default, report_filter_type);

		test.log(LogStatus.INFO, caseType);
		String filter_id = spogReportServer.createReportFilterForSpecifiedUserWithCheck(user_id, org_id, validToken,
				defaultfilterInfo, SpogConstants.SUCCESS_POST, null, test);

		test.log(LogStatus.INFO, "Composing Update filters");
		reportfilterInfo = spogReportServer.composeReportFilterInfo(source_name, destination_name, policy_id,
				destination_id, source_group_ids, organization_ids, DateRangeType, start_ts_dr, end_ts_dr, report_type,
				destination_type, job_type, "", backup_schedule, spogServer.ReturnRandom("filter_name_new"), is_default,
				report_filter_type);

		test.log(LogStatus.INFO, "Update Report Filters");
		spogReportServer.updateReportFilterForSpecifiedUserWithCheck(user_id, filter_id, org_id, validToken,
				reportfilterInfo, defaultfilterInfo, expectedStatusCode, expectedErrorMessage, test);

		test.log(LogStatus.INFO, " Deleting filters");
		spogReportServer.deleteReportFiltersForLoggedInUser(validToken, filter_id, SpogConstants.SUCCESS_GET_PUT_DELETE,
				null, test);

	}

	@DataProvider(name = "updateReportFiltersInvalid_401")
	public Object[][] updateReportFiltersInvalid_401() {

		return new Object[][] {
				{ "Update report filters with invalid token", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_id,
						"INVALID TOKEN", SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT },
				{ "Update report filters with missing token", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_id, "",
						SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED },
				{ "Update report filters with invalid token", ti.direct_org1_id, ti.direct_org1_user1_id,
						"INVALID TOKEN", SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT },
				{ "Update report filters with missing token", ti.direct_org1_id, ti.direct_org1_user1_id, "",
						SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED },
				{ "Update report filters with invalid token", ti.normal_msp1_suborg1_id,
						ti.normal_msp1_suborg1_user1_id, "INVALID TOKEN", SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT },
				{ "Update report filters with missing token", ti.normal_msp1_suborg1_id,
						ti.normal_msp1_suborg1_user1_id, "", SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTHENTICATION_FAILED }, };
	}

	// Invalid cases - 401

	@Test(dataProvider = "updateReportFiltersInvalid_401")
	public void updateReportFiltersInvalid_401(String caseType, String org_id, String user_id, String token,
			int expectedStatusCode, SpogMessageCode expectedErrorMessage) {

		HashMap<String, Object> reportfilterInfo = new HashMap<>();
		HashMap<String, Object> defaultfilterInfo = new HashMap<>();

		String filter_id = null;
		test.log(LogStatus.INFO, caseType);
		spogReportServer.updateReportFilterForSpecifiedUserWithCheck(user_id, filter_id, org_id, token,
				reportfilterInfo, defaultfilterInfo, expectedStatusCode, expectedErrorMessage, test);

	}

	@DataProvider(name = "updateReportFiltersInvalid_404")
	public Object[][] updateReportFiltersInvalid_404() {

		return new Object[][] {
				{ "Update report filters with filter ID that doesn't exist", UUID.randomUUID().toString(),
						ti.normal_msp_org1_id, ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_token,
						SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.FILTER_NOT_FOUND_WITH_USER_ID },
				{ "Update report filters with filter ID that doesn't exist", UUID.randomUUID().toString(),
						ti.direct_org1_id, ti.direct_org1_user1_id, ti.direct_org1_user1_token,
						SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.FILTER_NOT_FOUND_WITH_USER_ID },
				{ "Update report filters with filter ID that doesn't exist", UUID.randomUUID().toString(),
						ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg1_user1_token,
						SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.FILTER_NOT_FOUND_WITH_USER_ID },
				{ "Update report filters with filter ID that doesn't exist", UUID.randomUUID().toString(),
						ti.normal_msp1_suborg1_id, ti.normal_msp_org1_msp_accountadmin1_id,
						ti.normal_msp_org1_msp_accountadmin1_token, SpogConstants.RESOURCE_NOT_EXIST,
						SpogMessageCode.FILTER_NOT_FOUND_WITH_USER_ID },
				// csr read only cases

				{ "Update report filters with filter ID that doesn't exist", UUID.randomUUID().toString(),
						ti.csr_org_id, ti.csr_readonly_admin_user_id, ti.csr_readonly_token,
						SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.FILTER_NOT_FOUND_WITH_USER_ID }, };
	}

	// Invalid cases - 404

	@Test(dataProvider = "updateReportFiltersInvalid_404")
	public void updateReportFiltersInvalid_404(String caseType, String filter_id, String org_id, String user_id,
			String token, int expectedStatusCode, SpogMessageCode expectedErrorMessage) {

		HashMap<String, Object> reportfilterInfo = new HashMap<>();
		HashMap<String, Object> defaultfilterInfo = new HashMap<>();

		test.log(LogStatus.INFO, caseType);
		reportfilterInfo.put("filter_name", "filter_name");
		reportfilterInfo.put("is_default", false);
		spogReportServer.updateReportFilterForSpecifiedUserWithCheck(user_id, filter_id, org_id, token,
				reportfilterInfo, defaultfilterInfo, expectedStatusCode, expectedErrorMessage, test);

	}

	// Invalid cases - 403

	@DataProvider(name = "updateReportFiltersInvalid_403")
	public Object[][] updateReportFiltersInvalid_403() {

		return new Object[][] {

			/*	{ "Update report filter for backup jobs for msp using suborg token", ti.normal_msp_org1_id,
						ti.normal_msp_org1_user1_id, ti.normal_msp1_suborg1_user1_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Update report filter for jobs and destinations for msp using msp acc adm token",
						ti.normal_msp_org1_id, ti.normal_msp_org1_user1_id, ti.normal_msp_org1_msp_accountadmin1_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Update report filter for jobs and destinations for direct org using suborg token", ti.direct_org1_id,
						ti.direct_org1_user1_id, ti.normal_msp1_suborg1_user1_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				// csr readonly cases
				{ "Update report filter for jobs and destinations for csr org using direct token", ti.csr_org_id,
						ti.csr_readonly_admin_user_id, ti.direct_org1_user1_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Update report filter for backup jobs for csr using msp token", ti.csr_org_id,
						ti.csr_readonly_admin_user_id, ti.normal_msp_org1_user1_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Update report filter for backup jobs for csr using suborg token", ti.csr_org_id,
						ti.csr_readonly_admin_user_id, ti.normal_msp1_suborg1_user1_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },*/

		};
	}

/*	@Test(dataProvider = "updateReportFiltersInvalid_403")
	public void updateReportFiltersInvalid_403(String caseType, String org_id, String user_id, String token,
			int expectedStatusCode, SpogMessageCode expectedErrorMessage) {

		HashMap<String, Object> reportfilterInfo = new HashMap<>();
		HashMap<String, Object> defaultfilterInfo = new HashMap<>();

		String filter_id = UUID.randomUUID().toString();

		test.log(LogStatus.INFO, caseType);
		spogReportServer.updateReportFilterForSpecifiedUserWithCheck(user_id, filter_id, org_id, token,
				reportfilterInfo, defaultfilterInfo, expectedStatusCode, expectedErrorMessage, test);

	}*/

	@AfterMethod
	public void getResult(ITestResult result) {
		if (result.getStatus() == ITestResult.FAILURE) {
			count1.setfailedcount();
			test.log(LogStatus.FAIL, "Test Case Failed is " + result.getName() + " with parameters as "
					+ Arrays.asList(result.getParameters()));
			test.log(LogStatus.FAIL, result.getThrowable().getMessage());

		} else if (result.getStatus() == ITestResult.SKIP) {
			test.log(LogStatus.SKIP, "Test Case Skipped is " + result.getName());
			count1.setskippedcount();
		} else if (result.getStatus() == ITestResult.SUCCESS) {
			count1.setpassedcount();

		}
		rep.endTest(test);
		rep.flush();

	}

	/******************************************************************
	 * RandomFunction
	 ******************************************************************************/
	public int gen_random_index(String[] job_status) {
		Random generator = new Random();
		int randomindx = generator.nextInt(job_status.length);

		return randomindx;
	}
}
