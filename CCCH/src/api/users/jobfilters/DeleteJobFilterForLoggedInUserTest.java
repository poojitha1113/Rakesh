package api.users.jobfilters;

import java.io.IOException;
import java.net.UnknownHostException;
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

import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.SPOGJobServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;

public class DeleteJobFilterForLoggedInUserTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private SPOGJobServer spogJobServer;
	private UserSpogServer userSpogServer;
	//public int Nooftest;
	//private ExtentReports rep;
	private ExtentTest test;
	private TestOrgInfo ti;
	//used for test case count like passed,failed,remaining cases

	private String org_model_prefix = this.getClass().getSimpleName();
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
	String filtername=RandomStringUtils.randomAlphanumeric(5);

	@BeforeClass
	@Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword","csrReadOnlyAdminName","csrReadOnlyAdminPassword","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword,String csrReadOnlyAdminName,String csrReadOnlyAdminPassword, String logFolder,String runningMachine,String buildVersion) throws UnknownHostException {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogJobServer = new SPOGJobServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		rep = ExtentManager.getInstance("DeleteJobFilterForLoggedInUserTest", logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Prasad.Deverakonda";

		Nooftest=0;
		ti = new TestOrgInfo(spogServer, test);

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
			/*try
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
			    }*/
		}
	}


	@DataProvider(name = "create_job_filter_valid")
	public final Object[][] createJobFilterValidParams() {
		return new Object[][] {
			// different users

			//ti.direct_org1_user1_token,ti.direct_org1_user1_id,ti.direct_org1_id,
			//ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id,ti.normal_msp_org1_id,
			//ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id,ti.normal_msp_org1_id,
			//ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id,ti.normal_msp1_suborg1_id,
			//job_status {@link JobStatus}:active,finished,canceled,failed,incomplete,idle,waiting,crash,license_failed,backupjob_proc_exit,skipped,stop,missed.
			//restore
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, "active", null,null, null, null, null,null, null, filtername+"TEST", "true" },
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, "finished", null,null, null, null, null,null, null,filtername+"test", "true" },
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, "canceled", null,null, null, null, null,null, null,filtername+"test", "true" },
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, "failed", null,null, null, null, null,null, null,filtername+"test", "true" },
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, "incomplete", null,null, null, null, null,null, null,filtername+"test", "true" },
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, "waiting", null,null, null, null, null,null, null,filtername+"test", "true" },
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, "stop", null,null, null, null, null,null, null,filtername+"test", "true" },

			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, "active", null,null, "backup", null, null,null, null, filtername+"TEST", "true" },
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, "finished", null,null, "backup", null, null,null, null,filtername+"test", "true" },
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, "canceled", null,null, "backup", null, null,null, null,filtername+"test", "true" },
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, "failed", null,null, "backup", null, null,null, null,filtername+"test", "true" },
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, "incomplete", null,null, "backup", null, null,null, null,filtername+"test", "true" },
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, "waiting", null,null, "backup", null, null,null, null,filtername+"test", "true" },
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, "stop", null,null, "backup", null, null,null, null,filtername+"test", "true" },
			{"direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, "finished", UUID.randomUUID().toString(),UUID.randomUUID().toString(), "backup",
				"custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()+10),"sourceName", "filterName", "true" },

			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, "active", null,null, null, null, null,null, null, filtername+"TEST", "true" },
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, "finished", null,null, null, null, null,null, null,filtername+"test", "true" },
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, "canceled", null,null, null, null, null,null, null,filtername+"test", "true" },
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, "failed", null,null, null, null, null,null, null,filtername+"test", "true" },
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, "incomplete", null,null, null, null, null,null, null,filtername+"test", "true" },
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, "waiting", null,null, null, null, null,null, null,filtername+"test", "true" },
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, "stop", null,null, null, null, null,null, null,filtername+"test", "true" },

			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, "active", null,null, "restore", null, null,null, null, filtername+"TEST", "true" },
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, "finished", null,null, "restore", null, null,null, null,filtername+"test", "true" },
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, "canceled", null,null, "restore", null, null,null, null,filtername+"test", "true" },
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, "failed", null,null, "restore", null, null,null, null,filtername+"test", "true" },
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, "incomplete", null,null, "restore", null, null,null, null,filtername+"test", "true" },
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, "waiting", null,null, "restore", null, null,null, null,filtername+"test", "true" },
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, "stop", null,null, "restore", null, null,null, null,filtername+"test", "true" },
			{"direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, "finished", UUID.randomUUID().toString(),UUID.randomUUID().toString(), "restore",
				"custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()+10),"sourceName", "filterName", "true" },


			//Normal msp

			{ "Normalmsp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, "active", null,null, null, null, null,null, null, filtername+"TEST", "true" },
			{ "Normalmsp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, "finished", null,null, null, null, null,null, null,filtername+"test", "true" },
			{ "Normalmsp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, "canceled", null,null, null, null, null,null, null,filtername+"test", "true" },
			{ "Normalmsp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, "failed", null,null, null, null, null,null, null,filtername+"test", "true" },
			{ "Normalmsp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, "incomplete", null,null, null, null, null,null, null,filtername+"test", "true" },
			{ "Normalmsp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, "waiting", null,null, null, null, null,null, null,filtername+"test", "true" },
			{ "Normalmsp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, "stop", null,null, null, null, null,null, null,filtername+"test", "true" },

			{ "Normalmsp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, "active", null,null, "backup", null, null,null, null, filtername+"TEST", "true" },
			{ "Normalmsp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, "finished", null,null, "backup", null, null,null, null,filtername+"test", "true" },
			{ "Normalmsp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, "canceled", null,null, "backup", null, null,null, null,filtername+"test", "true" },
			{ "Normalmsp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, "failed", null,null, "backup", null, null,null, null,filtername+"test", "true" },
			{ "Normalmsp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, "incomplete", null,null, "backup", null, null,null, null,filtername+"test", "true" },
			{ "Normalmsp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, "waiting", null,null, "backup", null, null,null, null,filtername+"test", "true" },
			{ "Normalmsp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, "stop", null,null, "backup", null, null,null, null,filtername+"test", "true" },
			{"Normalmsp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, "finished", UUID.randomUUID().toString(),UUID.randomUUID().toString(), "backup",
				"custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()+10),"sourceName", "filterName", "true" },

			{ "Normalmsp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, "active", null,null, null, null, null,null, null, filtername+"TEST", "true" },
			{ "Normalmsp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, "finished", null,null, null, null, null,null, null,filtername+"test", "true" },
			{ "Normalmsp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, "canceled", null,null, null, null, null,null, null,filtername+"test", "true" },
			{ "Normalmsp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, "failed", null,null, null, null, null,null, null,filtername+"test", "true" },
			{ "Normalmsp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, "incomplete", null,null, null, null, null,null, null,filtername+"test", "true" },
			{ "Normalmsp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, "waiting", null,null, null, null, null,null, null,filtername+"test", "true" },
			{ "Normalmsp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, "stop", null,null, null, null, null,null, null,filtername+"test", "true" },

			{ "Normalmsp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, "active", null,null, "restore", null, null,null, null, filtername+"TEST", "true" },
			{ "Normalmsp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, "finished", null,null, "restore", null, null,null, null,filtername+"test", "true" },
			{ "Normalmsp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, "canceled", null,null, "restore", null, null,null, null,filtername+"test", "true" },
			{ "Normalmsp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, "failed", null,null, "restore", null, null,null, null,filtername+"test", "true" },
			{ "Normalmsp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, "incomplete", null,null, "restore", null, null,null, null,filtername+"test", "true" },
			{ "Normalmsp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, "waiting", null,null, "restore", null, null,null, null,filtername+"test", "true" },
			{ "Normalmsp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, "stop", null,null, "restore", null, null,null, null,filtername+"test", "true" },
			{"Normalmsp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, "finished", UUID.randomUUID().toString(),UUID.randomUUID().toString(), "restore",
				"custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()+10),"sourceName", "filterName", "true" },
			//Normal msp account admin user
			{ "Normalmsp_Accout admin user_Accout admin user", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, "active", null,null, null, null, null,null, null, filtername+"TEST", "true" },
			{ "Normalmsp_Accout admin user", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, "finished", null,null, null, null, null,null, null,filtername+"test", "true" },
			{ "Normalmsp_Accout admin user", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, "canceled", null,null, null, null, null,null, null,filtername+"test", "true" },
			{ "Normalmsp_Accout admin user", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, "failed", null,null, null, null, null,null, null,filtername+"test", "true" },
			{ "Normalmsp_Accout admin user", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, "incomplete", null,null, null, null, null,null, null,filtername+"test", "true" },
			{ "Normalmsp_Accout admin user", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, "waiting", null,null, null, null, null,null, null,filtername+"test", "true" },
			{ "Normalmsp_Accout admin user", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, "stop", null,null, null, null, null,null, null,filtername+"test", "true" },

			{ "Normalmsp_Accout admin user", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, "active", null,null, "backup", null, null,null, null, filtername+"TEST", "true" },
			{ "Normalmsp_Accout admin user", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, "finished", null,null, "backup", null, null,null, null,filtername+"test", "true" },
			{ "Normalmsp_Accout admin user", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, "canceled", null,null, "backup", null, null,null, null,filtername+"test", "true" },
			{ "Normalmsp_Accout admin user", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, "failed", null,null, "backup", null, null,null, null,filtername+"test", "true" },
			{ "Normalmsp_Accout admin user", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, "incomplete", null,null, "backup", null, null,null, null,filtername+"test", "true" },
			{ "Normalmsp_Accout admin user", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, "waiting", null,null, "backup", null, null,null, null,filtername+"test", "true" },
			{ "Normalmsp_Accout admin user", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, "stop", null,null, "backup", null, null,null, null,filtername+"test", "true" },
			{"Normalmsp_Accout admin user", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, "finished", UUID.randomUUID().toString(),UUID.randomUUID().toString(), "backup",
				"custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()+10),"sourceName", "filterName", "true" },

			{ "Normalmsp_Accout admin user", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, "active", null,null, null, null, null,null, null, filtername+"TEST", "true" },
			{ "Normalmsp_Accout admin user", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, "finished", null,null, null, null, null,null, null,filtername+"test", "true" },
			{ "Normalmsp_Accout admin user", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, "canceled", null,null, null, null, null,null, null,filtername+"test", "true" },
			{ "Normalmsp_Accout admin user", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, "failed", null,null, null, null, null,null, null,filtername+"test", "true" },
			{ "Normalmsp_Accout admin user", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, "incomplete", null,null, null, null, null,null, null,filtername+"test", "true" },
			{ "Normalmsp_Accout admin user", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, "waiting", null,null, null, null, null,null, null,filtername+"test", "true" },
			{ "Normalmsp_Accout admin user", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, "stop", null,null, null, null, null,null, null,filtername+"test", "true" },

			{ "Normalmsp_Accout admin user", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, "active", null,null, "restore", null, null,null, null, filtername+"TEST", "true" },
			{ "Normalmsp_Accout admin user", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, "finished", null,null, "restore", null, null,null, null,filtername+"test", "true" },
			{ "Normalmsp_Accout admin user", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, "canceled", null,null, "restore", null, null,null, null,filtername+"test", "true" },
			{ "Normalmsp_Accout admin user", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, "failed", null,null, "restore", null, null,null, null,filtername+"test", "true" },
			{ "Normalmsp_Accout admin user", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, "incomplete", null,null, "restore", null, null,null, null,filtername+"test", "true" },
			{ "Normalmsp_Accout admin user", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, "waiting", null,null, "restore", null, null,null, null,filtername+"test", "true" },
			{ "Normalmsp_Accout admin user", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, "stop", null,null, "restore", null, null,null, null,filtername+"test", "true" },
			{"Normalmsp_Accout admin user", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, "finished", UUID.randomUUID().toString(),UUID.randomUUID().toString(), "restore",
				"custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()+10),"sourceName", "filterName", "true" },

			
			//Customer account of normal msp 
			{ "Direct user of Normal msp", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, "active", null,null, null, null, null,null, null, filtername+"TEST", "true" },
			{ "Direct user of Normal msp", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, "finished", null,null, null, null, null,null, null,filtername+"test", "true" },
			{ "Direct user of Normal msp", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, "canceled", null,null, null, null, null,null, null,filtername+"test", "true" },
			{ "Direct user of Normal msp", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, "failed", null,null, null, null, null,null, null,filtername+"test", "true" },
			{ "Direct user of Normal msp", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, "incomplete", null,null, null, null, null,null, null,filtername+"test", "true" },
			{ "Direct user of Normal msp", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, "waiting", null,null, null, null, null,null, null,filtername+"test", "true" },
			{ "Direct user of Normal msp", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, "stop", null,null, null, null, null,null, null,filtername+"test", "true" },
			
			{ "Direct user of Normal msp", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, "active", null,null, "backup", null, null,null, null, filtername+"TEST", "true" },
			{ "Direct user of Normal msp", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, "finished", null,null, "backup", null, null,null, null,filtername+"test", "true" },
			{ "Direct user of Normal msp", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, "canceled", null,null, "backup", null, null,null, null,filtername+"test", "true" },
			{ "Direct user of Normal msp", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, "failed", null,null, "backup", null, null,null, null,filtername+"test", "true" },
			{ "Direct user of Normal msp", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, "incomplete", null,null, "backup", null, null,null, null,filtername+"test", "true" },
			{ "Direct user of Normal msp", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, "waiting", null,null, "backup", null, null,null, null,filtername+"test", "true" },
			{ "Direct user of Normal msp", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, "stop", null,null, "backup", null, null,null, null,filtername+"test", "true" },
			{"Direct user of Normal msp", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, "finished", UUID.randomUUID().toString(),UUID.randomUUID().toString(), "backup",
				"custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()+10),"sourceName", "filterName", "true" },
			
			{ "Direct user of Normal msp", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, "active", null,null, null, null, null,null, null, filtername+"TEST", "true" },
			{ "Direct user of Normal msp", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, "finished", null,null, null, null, null,null, null,filtername+"test", "true" },
			{ "Direct user of Normal msp", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, "canceled", null,null, null, null, null,null, null,filtername+"test", "true" },
			{ "Direct user of Normal msp", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, "failed", null,null, null, null, null,null, null,filtername+"test", "true" },
			{ "Direct user of Normal msp", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, "incomplete", null,null, null, null, null,null, null,filtername+"test", "true" },
			{ "Direct user of Normal msp", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, "waiting", null,null, null, null, null,null, null,filtername+"test", "true" },
			{ "Direct user of Normal msp", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, "stop", null,null, null, null, null,null, null,filtername+"test", "true" },
			
			{ "Direct user of Normal msp", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, "active", null,null, "restore", null, null,null, null, filtername+"TEST", "true" },
			{ "Direct user of Normal msp", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, "finished", null,null, "restore", null, null,null, null,filtername+"test", "true" },
			{ "Direct user of Normal msp", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, "canceled", null,null, "restore", null, null,null, null,filtername+"test", "true" },
			{ "Direct user of Normal msp", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, "failed", null,null, "restore", null, null,null, null,filtername+"test", "true" },
			{ "Direct user of Normal msp", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, "incomplete", null,null, "restore", null, null,null, null,filtername+"test", "true" },
			{ "Direct user of Normal msp", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, "waiting", null,null, "restore", null, null,null, null,filtername+"test", "true" },
			{ "Direct user of Normal msp", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, "stop", null,null, "restore", null, null,null, null,filtername+"test", "true" },
			{"Direct user of Normal msp", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, "finished", UUID.randomUUID().toString(),UUID.randomUUID().toString(), "restore",
				"custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()+10),"sourceName", "filterName", "true" },
			
		};
	}
	@Test(dataProvider = "create_job_filter_valid")
	public void deleteJobFilterforloggedinUser_Valid(String organizationType,
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
		test.log(LogStatus.INFO, "Create job filter with job status as "+jobStatus);
		String filter_Id = spogJobServer.createJobFilterWithCheckEx(user_id, jobStatus, policyID, resourceID, jobType, startTimeType, startTimeTSStart, startTimeTSEnd, sourceName, prefix + filterName, isDefault, test);


		test.log(LogStatus.INFO, "delete job filter by filter id where the filter does not exist using token of "+organizationType);
		spogJobServer.deleteJobFilterforloggedInUser(UUID.randomUUID().toString(), validToken, SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.JOB_FILTER_NOT_FOUND_WITH_CURRENT_USER, test);

		test.log(LogStatus.INFO, "delete job filter by filter id where the filter does not exist using token of csr");
		spogJobServer.deleteJobFilterforloggedInUser(UUID.randomUUID().toString(), ti.csr_token, SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.JOB_FILTER_NOT_FOUND_WITH_CURRENT_USER, test);

		test.log(LogStatus.INFO, "delete job filter by filter id");
		spogJobServer.deleteJobFilterforloggedInUser(filter_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		test.log(LogStatus.INFO, "delete job filter by filter id where the filter is invalid using token of "+organizationType);
		spogJobServer.deleteJobFilterforloggedInUser("123", validToken, SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ERROR_MESSAGE_ELEMENT_FILTER_ID_NOT_UUID, test);


	}

	@DataProvider(name = "Delete_job_filter_401_Secnarios")
	public final Object[][] DeleteLogFilterValidParams1() {
		return new Object[][] {
			//posted jobs  to direct_user_id :job_type =backup_full and posted logs on :severity is information
			{"Delete Direct org job filters with  invalid token", ti.direct_org1_user1_token },
			//Customer account of Normal Organization
			{"Delete Customer account of Normal msp org job filters with  invalid token", ti.normal_msp1_suborg1_user1_token },

		};
	}

	@Test(dataProvider = "Delete_job_filter_401_Secnarios")
	public void deleteJobFilterforloggedInUser_invalidJWT_missingJWT(	String organizationType,
			String validtoken
			) 
	{
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);

		String filter_Id=UUID.randomUUID().toString();
		test.log(LogStatus.INFO,"Delete the job filter by filter ID using invalid JWT");
		spogJobServer.deleteJobFilterforloggedInUser(filter_Id, "J", SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

		test.log(LogStatus.INFO,"Delete the job filter by filter ID using missing JWT");
		spogJobServer.deleteJobFilterforloggedInUser(filter_Id, "", SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);

	}


	@DataProvider(name = "getjobfilterbyfilterId_403_insufficeintpermission",parallel=false)
	public final Object[][] getjobfilterbyfilterId_403_insufficeintpermission() {
		return new Object[][] {
			//Direct Organization
			{ "Create Direct org user filters with another direct Org Token ",ti.direct_org2_user1_token,ti.direct_org1_user1_id},
			{ "Create Direct org user filters with Normal msp user Token ",ti.normal_msp_org2_user1_token,ti.direct_org1_user1_id},
			{ "Create Direct org user filters with Normal msp accout adim user Token ",ti.normal_msp_org2_msp_accountadmin1_token,ti.direct_org1_user1_id},
			{ "Create Direct org user filters with Normal msp's customer account user Token ",ti.normal_msp2_suborg1_user1_token,ti.direct_org1_user1_id},

			{ "Create Direct org user filters with Root msp user Token ",ti.root_msp_org1_user1_token,ti.direct_org1_user1_id},
			{ "Create Direct org user filters with  Root msp account adim user Token ",ti.root_msp_org1_msp_accountadmin1_token,ti.direct_org1_user1_id},
			{ "Create Direct org user filters with Customer accout of RootMsp user token",ti.root_msp1_suborg1_user1_token,ti.direct_org1_user1_id},

			{ "Create Direct org user filters with Sub msp user Token ",ti.direct_org2_user1_token,ti.direct_org1_user1_id},
			{ "Create Direct org user filters with Sub msp account adim user Token ",ti.direct_org2_user1_token,ti.direct_org1_user1_id},
			{ "Create Direct org user filters with Customer accout of Sub Msp user toekn",ti.direct_org2_user1_token,ti.direct_org1_user1_id},


			//Create User filters of normal  msp Organization

			{ "Create Normal msp's Customer account user filters with the Direct user token",ti.direct_org1_user1_token,ti.normal_msp1_suborg1_user1_id},
			{ "Create Normal msp's Customer account user filters with the normal user token", ti.normal_msp_org2_user1_token,ti.normal_msp1_suborg1_user1_id},
			{ "Create Normal msp's Customer account user filters with the normal msp account admin user token", ti.normal_msp_org2_msp_accountadmin1_token,ti.normal_msp1_suborg1_user1_id },
			{ "Create Normal msp's Customer account user filters with the customer account of another normal msp organzation", ti.normal_msp2_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id},
			{ "Create Normal msp's Customer account user filters with the root msp organzation", ti.root_msp1_submsp1_user1_token,ti.normal_msp1_suborg1_user1_id},
			{ "Create Normal msp's  Customer account user filters with the sub msp's account admin", ti.root_msp1_submsp1_account_admin_token,ti.normal_msp1_suborg1_user1_id},	
			{ "Create Normal msp's Customer account user filters with the sub msp's customer account",ti.msp1_submsp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id},	
			{ "Create Normal msp's Customer account user filters with the root msp organzation",ti.root_msp_org1_user1_token,ti.normal_msp1_suborg1_user1_id},	
			{ "Create Normal msp's Customer account user filters with the root msp  account adminorganzation", ti.root_msp_org1_msp_accountadmin1_token,ti.normal_msp1_suborg1_user1_id},	
			{ "Create Normal msp's Customer account user filters with the root msp's cutstomer account  organzation", ti.root_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id},

			//Create User filters of root   msp Organization
			{ "Create Root msp user filters with the Direct user token",ti.direct_org1_user1_token,ti.root_msp_org1_user1_id },
			{ "Create Root msp user filters with the normal user token",ti.normal_msp_org1_user1_token,ti.root_msp_org1_user1_id },
			{ "Create Root msp user filters with the normal msp account admin user token",ti.normal_msp_org1_msp_accountadmin1_token,ti.root_msp_org1_user1_id},
			{ "Create Root msp user filters with the customer account of another normal msp organzation",ti.normal_msp2_suborg1_user1_token,ti.root_msp_org1_user1_id},
			{ "Create Root msp user filters with the root msp organzation",ti.root_msp1_submsp1_user1_token,ti.root_msp_org1_user1_id},	
			{ "Create Root msp user filters with the sub msp's account admin", ti.root_msp1_submsp1_account_admin_token,ti.root_msp_org1_user1_id},	
			{ "Create Root msp user filters with the sub msp's customer account",ti.msp1_submsp1_suborg1_user1_token,ti.root_msp_org1_user1_id},	
			{ "Create Root msp user filters with the root msp organzation",ti.root_msp_org2_user1_token,ti.root_msp_org1_user1_id},	
			{ "Create Root msp user filters with the root msp  account adminorganzation", ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp_org1_user1_id },	
			{ "Create Root msp user filters with the root msp's cutstomer account  organzation",ti.root_msp1_suborg1_user1_token,ti.root_msp_org1_user1_id },	

			//Create User filters of normal   msp Organization
			{ "Create sub msp  user filters with the Direct user token",ti.direct_org1_user1_token,ti.root_msp1_submsp1_user1_id },
			{ "Create sub msp  user filters with the normal user token", ti.normal_msp_org1_user1_token,ti.root_msp1_submsp1_user1_id},
			{ "Create sub msp  user filters with the normal msp account admin user token",ti.normal_msp_org1_msp_accountadmin1_token,ti.root_msp1_submsp1_user1_id },
			{ "Create sub msp  user filters with the customer account of another normal msp organzation", ti.normal_msp2_suborg1_user1_token,ti.root_msp1_submsp1_user1_id },
			{ "Create sub msp  user filters with the root msp organzation",ti.root_msp2_submsp1_user1_token,ti.root_msp1_submsp1_user1_id },	
			{ "Create sub msp  user filters with the sub msp's account admin",ti.root_msp1_submsp1_account_admin_token,ti.root_msp1_submsp1_user1_id },	
			{ "Create sub msp  user filters with the sub msp's customer account",ti.msp1_submsp1_suborg1_user1_token,ti.root_msp1_submsp1_user1_id },	
			{ "Create sub msp  user filters with the root msp organzation",ti.root_msp_org2_user1_token,ti.root_msp1_submsp1_user1_id },	
			{ "Create sub msp  user filters with the root msp  account adminorganzation",ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp1_submsp1_user1_id },	
			{ "Create sub msp  user filters with the root msp's cutstomer account  organzation", ti.root_msp1_suborg1_user1_token,ti.root_msp1_submsp1_user1_id },	

		};
	}
	@Test(dataProvider = "getjobfilterbyfilterId_403_insufficeintpermission")
	public void deletejobfilterforloggedInUser_403_otherogtoken(String testcase,
			String Othertoken,
			String user_id ) {
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_");
		String prefix = RandomStringUtils.randomAlphanumeric(4);
		HashMap<String,Object>expected_response = new HashMap<>();
		SpogMessageCode expected = null;

		/*spogJobServer.setToken(validToken);
		test.log(LogStatus.INFO, "Create the job filter in org "+organizationType);
		String filter_Id = spogJobServer.createJobFilterWithCheckEx(user_id, jobStatus, policyID, resourceID, jobType, startTimeTSStart, startTimeTSEnd, endTimeTSStart, endTimeTSEnd, prefix + filterName, isDefault, test);
		 */
		String filter_Id =UUID.randomUUID().toString();
		test.log(LogStatus.INFO, "delete job filter by filter id" );
		spogJobServer.deleteJobFilterforloggedInUser(filter_Id, Othertoken, SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.JOB_FILTER_NOT_FOUND_WITH_CURRENT_USER, test);

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

	}
	 */
}
