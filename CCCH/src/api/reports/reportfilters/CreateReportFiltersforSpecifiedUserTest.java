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

public class CreateReportFiltersforSpecifiedUserTest extends base.prepare.Is4Org {
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
				{ "Create report filter for backup jobs for direct org", ti.direct_org1_id, ti.direct_org1_id,
						ti.direct_org1_user1_id, ti.direct_org1_user1_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_7_days",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "backup_jobs", "7D",
						DestinationType.cloud_direct_volume.toString(), "backup",
						spogServer.ReturnRandom("filter_name"), "Tue,Wed,Mon", "backup_jobs", true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						SpogConstants.SUCCESS_POST, null },
				{ "Create report filter for jobs and destinations for direct org", ti.direct_org1_id, ti.direct_org1_id,
						ti.direct_org1_user1_id, ti.direct_org1_user1_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_1_month",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "jobs_and_destinations", "1M",
						DestinationType.cloud_direct_volume.toString(), "backup,copy",
						spogServer.ReturnRandom("filter_name"), "Thu,Sat", "jobs_and_destinations", true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						SpogConstants.SUCCESS_POST, null },
				{ "Create report filter for jobs and destinations for direct org", ti.direct_org1_id, ti.direct_org1_id,
						ti.direct_org1_user1_id, ti.direct_org1_user1_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_1_month",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "jobs_and_destinations", "1M",
						DestinationType.cloud_direct_volume.toString(),
						"rps_replicate,copy,rps_replicate_in_bound,assure_recovery,start_instant_vhd,stop_instant_vhd,merge,restore",
						spogServer.ReturnRandom("filter_name"), "Thu,Sat", "jobs_and_destinations", true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						SpogConstants.SUCCESS_POST, null },
				{ "Create report filter for restore jobs for direct org", ti.direct_org1_id, ti.direct_org1_id,
						ti.direct_org1_user1_id, ti.direct_org1_user1_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "today",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "restore_jobs", "6M",
						DestinationType.cloud_hybrid_store.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "restore_jobs", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_POST, null },
				{ "Create report filter for data transfer for direct org", ti.direct_org1_id, ti.direct_org1_id,
						ti.direct_org1_user1_id, ti.direct_org1_user1_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_3_months",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "data_transfer", "2Y",
						DestinationType.cloud_direct_volume.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "data_transfer", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_POST, null },
				{ "Create report filter for capacity usage for direct org", ti.direct_org1_id, ti.direct_org1_id,
						ti.direct_org1_user1_id, ti.direct_org1_user1_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_1_year",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "capacity_usage", "forever",
						DestinationType.cloud_direct_volume.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "capacity_usage", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_POST, null }, // MSP

				{ "Create report filter for backup jobs for msporg",
						ti.normal_msp1_suborg1_id + "," + ti.normal_msp_org1_id, ti.normal_msp_org1_id,
						ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_token,
						spogServer.ReturnRandom("source_name"), spogServer.ReturnRandom("destination_name"),
						UUID.randomUUID().toString(), "last_7_days", System.currentTimeMillis(),
						System.currentTimeMillis() + 3600000, "backup_jobs", "7D",
						DestinationType.cloud_direct_volume.toString(), "backup",
						spogServer.ReturnRandom("filter_name"), "Tue,Wed,Mon", "backup_jobs", true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						SpogConstants.SUCCESS_POST, null },
				{ "Create report filter for jobs and destinations for msporg",
						ti.normal_msp1_suborg1_id + "," + ti.normal_msp_org1_id, ti.normal_msp_org1_id,
						ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_token,
						spogServer.ReturnRandom("source_name"), spogServer.ReturnRandom("destination_name"),
						UUID.randomUUID().toString(), "last_1_month", System.currentTimeMillis(),
						System.currentTimeMillis() + 3600000, "jobs_and_destinations", "1M",
						DestinationType.cloud_direct_volume.toString(), "backup,copy",
						spogServer.ReturnRandom("filter_name"), "Thu,Sat", "jobs_and_destinations", true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						SpogConstants.SUCCESS_POST, null },
				{ "Create report filter for jobs and destinations for msporg",
						ti.normal_msp1_suborg1_id + "," + ti.normal_msp_org1_id, ti.normal_msp_org1_id,
						ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_token,
						spogServer.ReturnRandom("source_name"), spogServer.ReturnRandom("destination_name"),
						UUID.randomUUID().toString(), "last_1_month", System.currentTimeMillis(),
						System.currentTimeMillis() + 3600000, "jobs_and_destinations", "1M",
						DestinationType.cloud_direct_volume.toString(),
						"rps_replicate,copy,rps_replicate_in_bound,assure_recovery,start_instant_vhd,stop_instant_vhd,merge,restore",
						spogServer.ReturnRandom("filter_name"), "Thu,Sat", "jobs_and_destinations", true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						SpogConstants.SUCCESS_POST, null },
				{ "Create report filter for restore jobs for msporg",
						ti.normal_msp1_suborg1_id + "," + ti.normal_msp_org1_id, ti.normal_msp_org1_id,
						ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_token,
						spogServer.ReturnRandom("source_name"), spogServer.ReturnRandom("destination_name"),
						UUID.randomUUID().toString(), "today", System.currentTimeMillis(),
						System.currentTimeMillis() + 3600000, "restore_jobs", "6M",
						DestinationType.cloud_hybrid_store.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "restore_jobs", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_POST, null },
				{ "Create report filter for data transfer for msporg",
						ti.normal_msp1_suborg1_id + "," + ti.normal_msp_org1_id, ti.normal_msp_org1_id,
						ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_token,
						spogServer.ReturnRandom("source_name"), spogServer.ReturnRandom("destination_name"),
						UUID.randomUUID().toString(), "last_3_months", System.currentTimeMillis(),
						System.currentTimeMillis() + 3600000, "data_transfer", "2Y",
						DestinationType.cloud_direct_volume.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "data_transfer", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_POST, null },
				{ "Create report filter for capacity usage for msporg",
						ti.normal_msp1_suborg1_id + "," + ti.normal_msp_org1_id, ti.normal_msp_org1_id,
						ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_token,
						spogServer.ReturnRandom("source_name"), spogServer.ReturnRandom("destination_name"),
						UUID.randomUUID().toString(), "last_1_year", System.currentTimeMillis(),
						System.currentTimeMillis() + 3600000, "capacity_usage", "forever",
						DestinationType.cloud_direct_volume.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "capacity_usage", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_POST, null },

				// Suborg + MSP Account Admin

				{ "Create report filter for backup jobs for suborg using msp account admin", ti.normal_msp1_suborg1_id,
						ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_id,
						ti.normal_msp_org1_msp_accountadmin1_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_7_days",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "backup_jobs", "7D",
						DestinationType.cloud_direct_volume.toString(), "backup",
						spogServer.ReturnRandom("filter_name"), "Tue,Wed,Mon", "backup_jobs", true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						SpogConstants.SUCCESS_POST, null },
				{ "Create report filter for jobs and destinations for suborg using msp account admin",
						ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_id,
						ti.normal_msp_org1_msp_accountadmin1_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_1_month",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "jobs_and_destinations", "1M",
						DestinationType.cloud_direct_volume.toString(), "backup,copy",
						spogServer.ReturnRandom("filter_name"), "Thu,Sat", "jobs_and_destinations", true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						SpogConstants.SUCCESS_POST, null },
				{ "Create report filter for jobs and destinations for suborg using msp account admin",
						ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_id,
						ti.normal_msp_org1_msp_accountadmin1_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_1_month",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "jobs_and_destinations", "1M",
						DestinationType.cloud_direct_volume.toString(),
						"rps_replicate,copy,rps_replicate_in_bound,assure_recovery,start_instant_vhd,stop_instant_vhd,merge,restore",
						spogServer.ReturnRandom("filter_name"), "Thu,Sat", "jobs_and_destinations", true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						SpogConstants.SUCCESS_POST, null },
				{ "Create report filter for restore jobs for suborg using msp account admin", ti.normal_msp1_suborg1_id,
						ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_id,
						ti.normal_msp_org1_msp_accountadmin1_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "today",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "restore_jobs", "6M",
						DestinationType.cloud_hybrid_store.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "restore_jobs", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_POST, null },
				{ "Create report filter for data transfer for suborg using msp account admin",
						ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_id,
						ti.normal_msp_org1_msp_accountadmin1_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_3_months",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "data_transfer", "2Y",
						DestinationType.cloud_direct_volume.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "data_transfer", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_POST, null },
				{ "Create report filter for capacity usage for suborg using msp account admin",
						ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_id,
						ti.normal_msp_org1_msp_accountadmin1_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_1_year",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "capacity_usage", "forever",
						DestinationType.cloud_direct_volume.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "capacity_usage", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_POST, null },

				// Suborg

				{ "Create report filter for backup jobs for suborg using suborg token", ti.normal_msp1_suborg1_id,
						ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg1_user1_token,
						spogServer.ReturnRandom("source_name"), spogServer.ReturnRandom("destination_name"),
						UUID.randomUUID().toString(), "last_7_days", System.currentTimeMillis(),
						System.currentTimeMillis() + 3600000, "backup_jobs", "7D",
						DestinationType.cloud_direct_volume.toString(), "backup",
						spogServer.ReturnRandom("filter_name"), "Tue,Wed,Mon", "backup_jobs", true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						SpogConstants.SUCCESS_POST, null },
				{ "Create report filter for jobs and destinations for suborg using suborg token",
						ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_id,
						ti.normal_msp1_suborg1_user1_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_1_month",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "jobs_and_destinations", "1M",
						DestinationType.cloud_direct_volume.toString(), "backup,copy",
						spogServer.ReturnRandom("filter_name"), "Thu,Sat", "jobs_and_destinations", true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						SpogConstants.SUCCESS_POST, null },
				{ "Create report filter for jobs and destinations for suborg using suborg token",
						ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_id,
						ti.normal_msp1_suborg1_user1_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_1_month",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "jobs_and_destinations", "1M",
						DestinationType.cloud_direct_volume.toString(),
						"rps_replicate,copy,rps_replicate_in_bound,assure_recovery,start_instant_vhd,stop_instant_vhd,merge,restore",
						spogServer.ReturnRandom("filter_name"), "Thu,Sat", "jobs_and_destinations", true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						SpogConstants.SUCCESS_POST, null },
				{ "Create report filter for restore jobs for suborg using suborg token", ti.normal_msp1_suborg1_id,
						ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg1_user1_token,
						spogServer.ReturnRandom("source_name"), spogServer.ReturnRandom("destination_name"),
						UUID.randomUUID().toString(), "today", System.currentTimeMillis(),
						System.currentTimeMillis() + 3600000, "restore_jobs", "6M",
						DestinationType.cloud_hybrid_store.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "restore_jobs", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_POST, null },
				{ "Create report filter for data transfer for suborg using suborg token", ti.normal_msp1_suborg1_id,
						ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg1_user1_token,
						spogServer.ReturnRandom("source_name"), spogServer.ReturnRandom("destination_name"),
						UUID.randomUUID().toString(), "last_3_months", System.currentTimeMillis(),
						System.currentTimeMillis() + 3600000, "data_transfer", "2Y",
						DestinationType.cloud_direct_volume.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "data_transfer", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_POST, null },
				{ "Create report filter for capacity usage for suborg using suborg token", ti.normal_msp1_suborg1_id,
						ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg1_user1_token,
						spogServer.ReturnRandom("source_name"), spogServer.ReturnRandom("destination_name"),
						UUID.randomUUID().toString(), "last_1_year", System.currentTimeMillis(),
						System.currentTimeMillis() + 3600000, "capacity_usage", "forever",
						DestinationType.cloud_direct_volume.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "capacity_usage", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_POST, null },
				// Suborg using msp token

				{ "Create report filter for backup jobs for suborg using msp token",
						ti.normal_msp1_suborg1_id + "," + ti.normal_msp_org1_id, ti.normal_msp_org1_id,
						ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_token,
						spogServer.ReturnRandom("source_name"), spogServer.ReturnRandom("destination_name"),
						UUID.randomUUID().toString(), "last_7_days", System.currentTimeMillis(),
						System.currentTimeMillis() + 3600000, "backup_jobs", "7D",
						DestinationType.cloud_direct_volume.toString(), "backup",
						spogServer.ReturnRandom("filter_name"), "Tue,Wed,Mon", "backup_jobs", true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						SpogConstants.SUCCESS_POST, null },
				{ "Create report filter for jobs and destinations for suborg using msp token",
						ti.normal_msp1_suborg1_id, ti.normal_msp_org1_id, ti.normal_msp_org1_user1_id,
						ti.normal_msp_org1_user1_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_1_month",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "jobs_and_destinations", "1M",
						DestinationType.cloud_direct_volume.toString(), "backup,copy",
						spogServer.ReturnRandom("filter_name"), "Thu,Sat", "jobs_and_destinations", true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						SpogConstants.SUCCESS_POST, null },
				{ "Create report filter for jobs and destinations for suborg using msp token",
						ti.normal_msp1_suborg1_id, ti.normal_msp_org1_id, ti.normal_msp_org1_user1_id,
						ti.normal_msp_org1_user1_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_1_month",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "jobs_and_destinations", "1M",
						DestinationType.cloud_direct_volume.toString(),
						"rps_replicate,copy,rps_replicate_in_bound,assure_recovery,start_instant_vhd,stop_instant_vhd,merge,restore",
						spogServer.ReturnRandom("filter_name"), "Thu,Sat", "jobs_and_destinations", true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						SpogConstants.SUCCESS_POST, null },
				{ "Create report filter for restore jobs for suborg using msp token", ti.normal_msp1_suborg1_id,
						ti.normal_msp_org1_id, ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_token,
						spogServer.ReturnRandom("source_name"), spogServer.ReturnRandom("destination_name"),
						UUID.randomUUID().toString(), "today", System.currentTimeMillis(),
						System.currentTimeMillis() + 3600000, "restore_jobs", "6M",
						DestinationType.cloud_hybrid_store.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "restore_jobs", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_POST, null },
				{ "Create report filter for data transfer for suborg using msp token", ti.normal_msp1_suborg1_id,
						ti.normal_msp_org1_id, ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_token,
						spogServer.ReturnRandom("source_name"), spogServer.ReturnRandom("destination_name"),
						UUID.randomUUID().toString(), "last_3_months", System.currentTimeMillis(),
						System.currentTimeMillis() + 3600000, "data_transfer", "2Y",
						DestinationType.cloud_direct_volume.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "data_transfer", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_POST, null },
				{ "Create report filter for capacity usage for suborg using msp token", ti.normal_msp1_suborg1_id,
						ti.normal_msp_org1_id, ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_token,
						spogServer.ReturnRandom("source_name"), spogServer.ReturnRandom("destination_name"),
						UUID.randomUUID().toString(), "last_1_year", System.currentTimeMillis(),
						System.currentTimeMillis() + 3600000, "capacity_usage", "forever",
						DestinationType.cloud_direct_volume.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "capacity_usage", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_POST, null },

				// csr read only cases-suborg

				{ "Create report filter for backup jobs for suborg using csrreadonly token",
						ti.normal_msp1_suborg1_id + "," + ti.normal_msp_org1_id, ti.csr_org_id,
						ti.csr_readonly_admin_user_id, ti.csr_readonly_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_7_days",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "backup_jobs", "7D",
						DestinationType.cloud_direct_volume.toString(), "backup",
						spogServer.ReturnRandom("filter_name"), "Tue,Wed,Mon", "backup_jobs", true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						SpogConstants.SUCCESS_POST, null },
				{ "Create report filter for jobs and destinations for suborg using csrreadonly token",
						ti.normal_msp1_suborg1_id, ti.csr_org_id, ti.csr_readonly_admin_user_id, ti.csr_readonly_token,
						spogServer.ReturnRandom("source_name"), spogServer.ReturnRandom("destination_name"),
						UUID.randomUUID().toString(), "last_1_month", System.currentTimeMillis(),
						System.currentTimeMillis() + 3600000, "jobs_and_destinations", "1M",
						DestinationType.cloud_direct_volume.toString(), "backup,copy",
						spogServer.ReturnRandom("filter_name"), "Thu,Sat", "jobs_and_destinations", true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						SpogConstants.SUCCESS_POST, null },
				{ "Create report filter for jobs and destinations for suborg using csrreadonly token",
						ti.normal_msp1_suborg1_id, ti.csr_org_id, ti.csr_readonly_admin_user_id, ti.csr_readonly_token,
						spogServer.ReturnRandom("source_name"), spogServer.ReturnRandom("destination_name"),
						UUID.randomUUID().toString(), "last_1_month", System.currentTimeMillis(),
						System.currentTimeMillis() + 3600000, "jobs_and_destinations", "1M",
						DestinationType.cloud_direct_volume.toString(),
						"rps_replicate,copy,rps_replicate_in_bound,assure_recovery,start_instant_vhd,stop_instant_vhd,merge,restore",
						spogServer.ReturnRandom("filter_name"), "Thu,Sat", "jobs_and_destinations", true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						SpogConstants.SUCCESS_POST, null },
				{ "Create report filter for restore jobs for suborg using csrreadonly token", ti.normal_msp1_suborg1_id,
						ti.csr_org_id, ti.csr_readonly_admin_user_id, ti.csr_readonly_token,
						spogServer.ReturnRandom("source_name"), spogServer.ReturnRandom("destination_name"),
						UUID.randomUUID().toString(), "today", System.currentTimeMillis(),
						System.currentTimeMillis() + 3600000, "restore_jobs", "6M",
						DestinationType.cloud_hybrid_store.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "restore_jobs", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_POST, null },
				{ "Create report filter for data transfer for suborg using csrreadonly token",
						ti.normal_msp1_suborg1_id, ti.csr_org_id, ti.csr_readonly_admin_user_id, ti.csr_readonly_token,
						spogServer.ReturnRandom("source_name"), spogServer.ReturnRandom("destination_name"),
						UUID.randomUUID().toString(), "last_3_months", System.currentTimeMillis(),
						System.currentTimeMillis() + 3600000, "data_transfer", "2Y",
						DestinationType.cloud_direct_volume.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "data_transfer", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_POST, null },
				{ "Create report filter for capacity usage for suborg using csrreadonly token",
						ti.normal_msp1_suborg1_id, ti.csr_org_id, ti.csr_readonly_admin_user_id, ti.csr_readonly_token,
						spogServer.ReturnRandom("source_name"), spogServer.ReturnRandom("destination_name"),
						UUID.randomUUID().toString(), "last_1_year", System.currentTimeMillis(),
						System.currentTimeMillis() + 3600000, "capacity_usage", "forever",
						DestinationType.cloud_direct_volume.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "capacity_usage", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_POST, null },

				// csr read only cases- direct

				{ "Create report filter for backup jobs for direct using csrreadonly token",
						ti.direct_org1_id + "," + ti.direct_org1_id, ti.csr_org_id, ti.csr_readonly_admin_user_id,
						ti.csr_readonly_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_7_days",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "backup_jobs", "7D",
						DestinationType.cloud_direct_volume.toString(), "backup",
						spogServer.ReturnRandom("filter_name"), "Tue,Wed,Mon", "backup_jobs", true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						SpogConstants.SUCCESS_POST, null },
				{ "Create report filter for jobs and destinations for direct using csrreadonly token",
						ti.direct_org1_id + "," + ti.direct_org1_id, ti.csr_org_id, ti.csr_readonly_admin_user_id,
						ti.csr_readonly_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_1_month",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "jobs_and_destinations", "1M",
						DestinationType.cloud_direct_volume.toString(), "backup,copy",
						spogServer.ReturnRandom("filter_name"), "Thu,Sat", "jobs_and_destinations", true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						SpogConstants.SUCCESS_POST, null },
				{ "Create report filter for jobs and destinations for direct using csrreadonly token",
						ti.direct_org1_id + "," + ti.direct_org1_id, ti.csr_org_id, ti.csr_readonly_admin_user_id,
						ti.csr_readonly_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_1_month",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "jobs_and_destinations", "1M",
						DestinationType.cloud_direct_volume.toString(),
						"rps_replicate,copy,rps_replicate_in_bound,assure_recovery,start_instant_vhd,stop_instant_vhd,merge,restore",
						spogServer.ReturnRandom("filter_name"), "Thu,Sat", "jobs_and_destinations", true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						SpogConstants.SUCCESS_POST, null },
				{ "Create report filter for restore jobs for direct using csrreadonly token",
						ti.direct_org1_id + "," + ti.direct_org1_id, ti.csr_org_id, ti.csr_readonly_admin_user_id,
						ti.csr_readonly_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "today",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "restore_jobs", "6M",
						DestinationType.cloud_hybrid_store.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "restore_jobs", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_POST, null },
				{ "Create report filter for data transfer for direct using csrreadonly token",
						ti.direct_org1_id + "," + ti.direct_org1_id, ti.csr_org_id, ti.csr_readonly_admin_user_id,
						ti.csr_readonly_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_3_months",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "data_transfer", "2Y",
						DestinationType.cloud_direct_volume.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "data_transfer", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_POST, null },
				{ "Create report filter for capacity usage for direct using csrreadonly token",
						ti.direct_org1_id + "," + ti.direct_org1_id, ti.csr_org_id, ti.csr_readonly_admin_user_id,
						ti.csr_readonly_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_1_year",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "capacity_usage", "forever",
						DestinationType.cloud_direct_volume.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "capacity_usage", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_POST, null },

				// csr read only cases- msp

				{ "Create report filter for backup jobs for msp using csrreadonly token",
						ti.normal_msp_org1_id + "," + ti.normal_msp_org1_id, ti.csr_org_id,
						ti.csr_readonly_admin_user_id, ti.csr_readonly_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_7_days",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "backup_jobs", "7D",
						DestinationType.cloud_direct_volume.toString(), "backup",
						spogServer.ReturnRandom("filter_name"), "Tue,Wed,Mon", "backup_jobs", true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						SpogConstants.SUCCESS_POST, null },
				{ "Create report filter for jobs and destinations for msp using csrreadonly token",
						ti.normal_msp_org1_id + "," + ti.normal_msp_org1_id, ti.csr_org_id,
						ti.csr_readonly_admin_user_id, ti.csr_readonly_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_1_month",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "jobs_and_destinations", "1M",
						DestinationType.cloud_direct_volume.toString(), "backup,copy",
						spogServer.ReturnRandom("filter_name"), "Thu,Sat", "jobs_and_destinations", true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						SpogConstants.SUCCESS_POST, null },
				{ "Create report filter for jobs and destinations for msp using csrreadonly token",
						ti.normal_msp_org1_id + "," + ti.normal_msp_org1_id, ti.csr_org_id,
						ti.csr_readonly_admin_user_id, ti.csr_readonly_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_1_month",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "jobs_and_destinations", "1M",
						DestinationType.cloud_direct_volume.toString(),
						"rps_replicate,copy,rps_replicate_in_bound,assure_recovery,start_instant_vhd,stop_instant_vhd,merge,restore",
						spogServer.ReturnRandom("filter_name"), "Thu,Sat", "jobs_and_destinations", true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						SpogConstants.SUCCESS_POST, null },
				{ "Create report filter for restore jobs for msp using csrreadonly token",
						ti.normal_msp_org1_id + "," + ti.normal_msp_org1_id, ti.csr_org_id,
						ti.csr_readonly_admin_user_id, ti.csr_readonly_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "today",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "restore_jobs", "6M",
						DestinationType.cloud_hybrid_store.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "restore_jobs", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_POST, null },
				{ "Create report filter for data transfer for msp using csrreadonly token",
						ti.normal_msp_org1_id + "," + ti.normal_msp_org1_id, ti.csr_org_id,
						ti.csr_readonly_admin_user_id, ti.csr_readonly_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_3_months",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "data_transfer", "2Y",
						DestinationType.cloud_direct_volume.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "data_transfer", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_POST, null },
				{ "Create report filter for capacity usage for msp using csrreadonly token",
						ti.normal_msp_org1_id + "," + ti.normal_msp_org1_id, ti.csr_org_id,
						ti.csr_readonly_admin_user_id, ti.csr_readonly_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_1_year",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "capacity_usage", "forever",
						DestinationType.cloud_direct_volume.toString(), "merge", spogServer.ReturnRandom("filter_name"),
						"Thu,Sat", "capacity_usage", true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, SpogConstants.SUCCESS_POST, null },

				// monitor cases

				{ "Create report filter for backup jobs for direct org", ti.direct_org1_id, ti.direct_org1_id,
						ti.direct_org1_monitor_user1_id, ti.direct_org1_monitor_user1_token,
						spogServer.ReturnRandom("source_name"), spogServer.ReturnRandom("destination_name"),
						UUID.randomUUID().toString(), "last_7_days", System.currentTimeMillis(),
						System.currentTimeMillis() + 3600000, "backup_jobs", "7D",
						DestinationType.cloud_direct_volume.toString(), "backup",
						spogServer.ReturnRandom("filter_name"), "Tue,Wed,Mon", "backup_jobs", true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						SpogConstants.SUCCESS_POST, null },
				{ "Create report filter for jobs and destinations for direct monitor", ti.direct_org1_id, ti.direct_org1_id,
						ti.direct_org1_monitor_user1_id, ti.direct_org1_monitor_user1_token,
						spogServer.ReturnRandom("source_name"), spogServer.ReturnRandom("destination_name"),
						UUID.randomUUID().toString(), "last_1_month", System.currentTimeMillis(),
						System.currentTimeMillis() + 3600000, "jobs_and_destinations", "1M",
						DestinationType.cloud_direct_volume.toString(), "backup,copy",
						spogServer.ReturnRandom("filter_name"), "Thu,Sat", "jobs_and_destinations", true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						SpogConstants.SUCCESS_POST, null },
				{ "Create report filter for backup jobs for suborg using monitor", ti.normal_msp1_suborg1_id,
						ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_monitor_user1_id,
						ti.normal_msp1_suborg1_monitor_user1_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_7_days",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "backup_jobs", "7D",
						DestinationType.cloud_direct_volume.toString(), "backup",
						spogServer.ReturnRandom("filter_name"), "Tue,Wed,Mon", "backup_jobs", true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						SpogConstants.SUCCESS_POST, null },
				{ "Create report filter for jobs and destinations for suborg using monitor", ti.root_msp1_suborg1_id,
						ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_monitor_user1_id,
						ti.root_msp1_suborg1_monitor_user1_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_1_month",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "jobs_and_destinations", "1M",
						DestinationType.cloud_direct_volume.toString(), "backup,copy",
						spogServer.ReturnRandom("filter_name"), "Thu,Sat", "jobs_and_destinations", true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						SpogConstants.SUCCESS_POST, null },
				{ "Create report filter for jobs and destinations for submsp using monitor",
						ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_monitor_user1_id,
						ti.root_msp1_submsp1_monitor_user1_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_1_month",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "jobs_and_destinations", "1M",
						DestinationType.cloud_direct_volume.toString(), "backup,copy",
						spogServer.ReturnRandom("filter_name"), "Thu,Sat", "jobs_and_destinations", true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						SpogConstants.SUCCESS_POST, null },

				{ "Create report filter for jobs and destinations for suborg using monitor",
						ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_monitor_user1_id,
						ti.msp1_submsp1_suborg1_monitor_user1_token, spogServer.ReturnRandom("source_name"),
						spogServer.ReturnRandom("destination_name"), UUID.randomUUID().toString(), "last_1_month",
						System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "jobs_and_destinations", "1M",
						DestinationType.cloud_direct_volume.toString(), "backup,copy",
						spogServer.ReturnRandom("filter_name"), "Thu,Sat", "jobs_and_destinations", true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						SpogConstants.SUCCESS_POST, null },

		};
	}

	// Valid Cases - 200

	@Test(dataProvider = "createreportfilters")
	public void createreportfilters(String caseType, String organization_ids, String org_id, String user_id,
			String validToken, String source_name, String destination_name, String policy_id, String DateRangeType,
			long start_ts_dr, long end_ts_dr, String report_type, String retention_id, String destination_type,
			String job_type, String filter_name, String backup_schedule, String report_filter_type, boolean is_default,
			String groupName, String groupDescription, int noOfSourceGroupsToCreate, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage) {

		HashMap<String, Object> reportfilterInfo = new HashMap<>();

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		spogServer.setToken(validToken);

		String source_group_ids = null;

		spogReportServer.setToken(validToken);

		test.log(LogStatus.INFO, "Composing Report filters");
		reportfilterInfo = spogReportServer.composeReportFilterInfo(source_name, destination_name, policy_id,
				destination_id, source_group_ids, organization_ids, DateRangeType, start_ts_dr, end_ts_dr, report_type,
				destination_type, job_type, retention_id, backup_schedule, filter_name, is_default, report_filter_type);

		test.log(LogStatus.INFO, caseType);
		String filter_id = spogReportServer.createReportFilterForSpecifiedUserWithCheck(user_id, org_id, validToken,
				reportfilterInfo, expectedStatusCode, expectedErrorMessage, test);

		test.log(LogStatus.INFO, " Deleting filters");
		spogReportServer.deleteReportFiltersForSpecifiedUser(ti.csr_token, user_id, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

	}

	@DataProvider(name = "createReportFiltersInvalid_401")
	public Object[][] createReportListFiltersInvalid_401() {

		return new Object[][] {
				{ "Create report filters with invalid token", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_id,
						"INVALID TOKEN", SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT },
				{ "Create report filters with missing token", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_id, "",
						SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED },
				{ "Create report filters with invalid token", ti.direct_org1_id, ti.direct_org1_user1_id,
						"INVALID TOKEN", SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT },
				{ "Create report filters with missing token", ti.direct_org1_id, ti.direct_org1_user1_id, "",
						SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED },
				{ "Create report filters with invalid token", ti.normal_msp1_suborg1_id,
						ti.normal_msp1_suborg1_user1_id, "INVALID TOKEN", SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT },
				{ "Create report filters with missing token", ti.normal_msp1_suborg1_id,
						ti.normal_msp1_suborg1_user1_id, "", SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTHENTICATION_FAILED }, };
	}

	// Invalid cases - 401

	@Test(dataProvider = "createReportFiltersInvalid_401")
	public void createReportFiltersInvalid_401(String caseType, String org_id, String user_id, String token,
			int expectedStatusCode, SpogMessageCode expectedErrorMessage) {

		HashMap<String, Object> reportfilterInfo = new HashMap<>();

		test.log(LogStatus.INFO, caseType);
		spogReportServer.createReportFilterForSpecifiedUserWithCheck(user_id, org_id, token, reportfilterInfo,
				expectedStatusCode, expectedErrorMessage, test);

	}

	@DataProvider(name = "createReportFiltersInvalid_403")
	public Object[][] createReportFiltersInvalid_403() {

		return new Object[][] {
				{ "Create report filters in msp org using sub org token", ti.normal_msp_org1_id,
						ti.normal_msp_org1_user1_id, ti.normal_msp1_suborg1_user1_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Create report filters in msp org using msp account admin token", ti.normal_msp_org1_id,
						ti.normal_msp_org1_user1_id, ti.normal_msp_org1_msp_accountadmin1_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Create report filters in direct org using suborg token", ti.direct_org1_id, ti.direct_org1_user1_id,
						ti.normal_msp1_suborg1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Create report filters in suborg org using unassigned msp account admin token",
						ti.normal_msp1_suborg2_id, ti.normal_msp1_suborg2_user1_id,
						ti.normal_msp_org1_msp_accountadmin1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				// csr read only cases
				{ "Create report filters in csr org using direct token", ti.csr_org_id, ti.csr_readonly_admin_user_id,
						ti.direct_org1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Create report filters in csr org using msp token", ti.csr_org_id, ti.csr_readonly_admin_user_id,
						ti.normal_msp_org1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Create report filters in csr org using suborg token", ti.csr_org_id, ti.csr_readonly_admin_user_id,
						ti.normal_msp1_suborg1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY }, };

	}

	// Invalid cases - 403

	@Test(dataProvider = "createReportFiltersInvalid_403")
	public void createReportFiltersInvalid_403(String caseType, String org_id, String user_id, String token,
			int expectedStatusCode, SpogMessageCode expectedErrorMessage) {

		HashMap<String, Object> reportfilterInfo = new HashMap<>();

		reportfilterInfo.put("filter_name", spogServer.ReturnRandom("filter_name"));
		reportfilterInfo.put("is_default", false);

		test.log(LogStatus.INFO, caseType);
		spogReportServer.createReportFilterForSpecifiedUserWithCheck(user_id, org_id, token, reportfilterInfo,
				expectedStatusCode, expectedErrorMessage, test);

	}

	@AfterMethod
	public void getResult(ITestResult result) {
		if (result.getStatus() == ITestResult.FAILURE) {
			count1.setfailedcount();
			// remaincases=Nooftest-passedcases-failedcases;
			test.log(LogStatus.FAIL, "Test Case Failed is " + result.getName() + " with parameters as "
					+ Arrays.asList(result.getParameters()));
			test.log(LogStatus.FAIL, result.getThrowable().getMessage());

		} else if (result.getStatus() == ITestResult.SKIP) {
			test.log(LogStatus.SKIP, "Test Case Skipped is " + result.getName());
			count1.setskippedcount();
		} else if (result.getStatus() == ITestResult.SUCCESS) {
			count1.setpassedcount();
			// remaincases=Nooftest-passedcases-failedcases;

		}
	/*	rep.endTest(test);
		rep.flush();
*/
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
