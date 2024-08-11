package api.sources;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

import Constants.ConnectionStatus;
import Constants.OSMajor;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGJobServer;
import InvokerServer.SPOGReportServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class GetSourceById extends base.prepare.Is4Org {

	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private SPOGJobServer spogJobServer;
	private SPOGDestinationServer spogDestinationServer;
	private SPOGReportServer spogreportServer;
	private Policy4SPOGServer policy4SpogServer;

	private ExtentTest test;
	private TestOrgInfo ti;

	String[] type = new String[12];
	private String job_Type = "backup";
	private String job_Status = "finished";
	private String job_Method = "full,incremental,resync,full,incremental,resync,full,incremental,resync,full,incremental,resync";
	public String JobSeverity = "warning,information,critical,warning,information,critical,warning,information,critical,warning,information,critical";
	public String JobStatus = "canceled,failed,finished,active,canceled,idle,waiting,crash,license_failed,backupjob_proc_exit,skipped,stop,active,finished";

	private String Direct_cloud_id;
	private String msp_cloud_account_id;
	private String root_cloud_id;

	ArrayList<String> available_actions = new ArrayList<String>();
	ArrayList<String> available_actions1 = new ArrayList<String>();
	private ArrayList<HashMap> source_group = new ArrayList<HashMap>();

	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine, String buildVersion) {

		spogServer = new SPOGServer(baseURI, port);
		spogDestinationServer=new SPOGDestinationServer(baseURI, port);
		policy4SpogServer=new Policy4SPOGServer(baseURI, port);
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(), logFolder);
		Nooftest = 0;
		bqdb1 = new SQLServerDb();
		String author = "Ramya.Nagepalli";
		count1 = new testcasescount();
		test = rep.startTest("beforeClass");
		test.assignAuthor("Ramya");
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date = new java.util.Date();
		this.buildVersion = buildVersion + "_" + dateFormater.format(date);
		this.BQName = this.getClass().getSimpleName();
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

		// get cloud account for the direct organization
		Response response = spogServer.getCloudAccounts(ti.direct_org1_user1_token, "", test);
		Direct_cloud_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");

		// get cloud account for the msp organization
		response = spogServer.getCloudAccounts(ti.normal_msp_org1_user1_token, "", test);
		msp_cloud_account_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");

		// get cloud account for the root msp organization
		response = spogServer.getCloudAccounts(ti.root_msp_org1_user1_token, "", test);
		root_cloud_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");

		available_actions.add("startrecovery");
		available_actions.add("delete");

		/*
		 * available_actions.add("cancelrpsmerge");
		 * available_actions.add("cancelrepliationin");
		 */
		available_actions1.add("startrecovery");
		available_actions1.add("delete");

		/*
		 * available_actions1.add("cancelrepliationin");
		 */

	}

	@DataProvider(name = "sourceInfo")
	public final Object[][] getSourceInfo() {
		return new Object[][] {
				{ ti.normal_msp1_suborg1_user1_email, ti.common_password, "suborg", SourceType.machine,
						SourceProduct.udp, ti.normal_msp1_suborg1_id, msp_cloud_account_id, ProtectionStatus.unprotect,
						ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange", "Ramya_vm2", null,
						"Ramya_agent1", "windows 2012", "64", "1.0.0", "2.0", "http://upgrade" },
				{ ti.direct_org1_user1_email, ti.common_password, "direct", SourceType.machine, SourceProduct.udp,
						ti.direct_org1_id, Direct_cloud_id, ProtectionStatus.unprotect, ConnectionStatus.online,
						OSMajor.windows.name(), "", "Ramya_vm1", null, null, null, null, null, null, null },

				{ ti.normal_msp1_suborg1_user1_email, ti.common_password, "suborg", SourceType.machine,
						SourceProduct.udp, ti.normal_msp1_suborg1_id, msp_cloud_account_id, ProtectionStatus.unprotect,
						ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange", null, null, null, null, "64",
						null, null, null },
				{ ti.normal_msp1_suborg1_user1_email, ti.common_password, "suborg", SourceType.machine,
						SourceProduct.udp, ti.normal_msp1_suborg1_id, msp_cloud_account_id, ProtectionStatus.unprotect,
						ConnectionStatus.online, OSMajor.windows.name(), "", null, null, null, null, null, "1.0.0",
						null, null },
				{ ti.normal_msp1_suborg1_user1_email, ti.common_password, "suborg", SourceType.machine,
						SourceProduct.udp, ti.normal_msp1_suborg1_id, msp_cloud_account_id, ProtectionStatus.unprotect,
						ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange", "Ramya_vm2", null,
						"Ramya_agent1", "windows 2012", "64", "1.0.0", "2.0", "http://upgrade" },
				{ ti.normal_msp1_suborg1_user1_email, ti.common_password, "suborg", SourceType.machine,
						SourceProduct.udp, ti.normal_msp1_suborg1_id, msp_cloud_account_id, ProtectionStatus.unprotect,
						ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange", null, null, null, null, null,
						"1.0.0", null, null },
				{ ti.normal_msp_org1_msp_accountadmin1_email, ti.common_password, "msp_account_admin",
						SourceType.machine, SourceProduct.udp, ti.normal_msp1_suborg1_id, msp_cloud_account_id,
						ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange",
						null, null, null, null, null, "1.0.0", null, null },
				// one drive cases
				{ ti.direct_org1_user1_email, ti.common_password, "direct", SourceType.one_drive, SourceProduct.udp,
						ti.direct_org1_id, Direct_cloud_id, ProtectionStatus.unprotect, ConnectionStatus.online,
						OSMajor.windows.name(), "sql;exchange", null, null, null, null, null, "1.0.0", null, null },
				{ ti.normal_msp1_suborg1_user1_email, ti.common_password, "suborg", SourceType.one_drive,
						SourceProduct.udp, ti.normal_msp1_suborg1_id, msp_cloud_account_id, ProtectionStatus.unprotect,
						ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange", null, null, null, null, null,
						"1.0.0", null, null },
				// 3 tier cases
				{ ti.root_msp1_suborg1_user1_email, ti.common_password, "suborg", SourceType.machine, SourceProduct.udp,
						ti.root_msp1_suborg1_id, root_cloud_id, ProtectionStatus.unprotect, ConnectionStatus.online,
						OSMajor.windows.name(), "sql;exchange", null, null, null, null, null, "1.0.0", null, null },
				{ ti.root_msp1_suborg1_user1_email, ti.common_password, "suborg", SourceType.machine, SourceProduct.udp,
						ti.root_msp1_suborg1_id, root_cloud_id, ProtectionStatus.unprotect, ConnectionStatus.online,
						OSMajor.windows.name(), "sql;exchange", null, null, null, null, null, "1.0.0", null, null },
				{ ti.root_msp1_suborg1_user1_email, ti.common_password, "suborg", SourceType.machine, SourceProduct.udp,
						ti.root_msp1_suborg1_id, root_cloud_id, ProtectionStatus.unprotect, ConnectionStatus.online,
						OSMajor.windows.name(), "sql;exchange", null, null, null, null, null, "1.0.0", null, null },
				{ ti.root_msp1_suborg1_user1_email, ti.common_password, "suborg", SourceType.machine, SourceProduct.udp,
						ti.root_msp1_suborg1_id, root_cloud_id, ProtectionStatus.unprotect, ConnectionStatus.online,
						OSMajor.windows.name(), "sql;exchange", null, null, null, null, null, "1.0.0", null, null },
				{ ti.root_msp_org1_msp_accountadmin1_email, ti.common_password, "msp_account_admin", SourceType.machine,
						SourceProduct.udp, ti.root_msp1_suborg1_id, root_cloud_id, ProtectionStatus.unprotect,
						ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange", null, null, null, null, null,
						"1.0.0", null, null },

				{ ti.msp1_submsp1_suborg1_user1_email, ti.common_password, "suborg", SourceType.machine,
						SourceProduct.udp, ti.msp1_submsp1_sub_org1_id, root_cloud_id, ProtectionStatus.unprotect,
						ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange", null, null, null, null, null,
						"1.0.0", null, null },
				{ ti.msp1_submsp1_suborg1_user1_email, ti.common_password, "suborg", SourceType.machine,
						SourceProduct.udp, ti.msp1_submsp1_sub_org1_id, root_cloud_id, ProtectionStatus.unprotect,
						ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange", null, null, null, null, null,
						"1.0.0", null, null },
				{ ti.msp1_submsp1_suborg1_user1_email, ti.common_password, "suborg", SourceType.machine,
						SourceProduct.udp, ti.msp1_submsp1_sub_org1_id, root_cloud_id, ProtectionStatus.unprotect,
						ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange", null, null, null, null, null,
						"1.0.0", null, null },
				{ ti.msp1_submsp1_suborg1_user1_email, ti.common_password, "suborg", SourceType.machine,
						SourceProduct.udp, ti.msp1_submsp1_sub_org1_id, root_cloud_id, ProtectionStatus.unprotect,
						ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange", null, null, null, null, null,
						"1.0.0", null, null },
				{ ti.root_msp1_submsp1_account_admin_1, ti.common_password, "msp_account_admin", SourceType.machine,
						SourceProduct.udp, ti.msp1_submsp1_sub_org1_id, root_cloud_id, ProtectionStatus.unprotect,
						ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange", null, null, null, null, null,
						"1.0.0", null, null }, };
	}

	// get Source by id For different scenarios
	@Test(dataProvider = "sourceInfo", dependsOnMethods = "deleteResources")

	public void GetSourceByIdForValidToken(String userName, String password, String org_type, SourceType sourceType,
			SourceProduct sourceProduct, String org_id, String siteId, ProtectionStatus protectionStatus,
			ConnectionStatus connectionStatus, String os_major, String applications, String vm_name,
			String hypervisor_id, String agent_name, String os_name, String os_architecture,
			String agent_current_version, String agent_upgrade_version, String agent_upgrade_link) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Ramya.Nagepalli");
		ArrayList<HashMap> sourceGroup = new ArrayList<HashMap>();
		HashMap<String, Object> compose_source_group = new HashMap<String, Object>();
		spogServer.errorHandle.printInfoMessageInDebugFile("creating a source");

		test.log(LogStatus.INFO, "admin login");
		spogServer.errorHandle.printInfoMessageInDebugFile("admin login");
		spogServer.userLogin(userName, password);
		String Token = spogServer.getJWTToken();
		String sourceName = spogServer.ReturnRandom("ramya_source");

		test.log(LogStatus.INFO, "create source and check");
		spogServer.errorHandle.printInfoMessageInDebugFile("create source and check");

		String source_id = spogServer.createSourceWithCheck(sourceName, sourceType, sourceProduct, org_id, siteId,
				protectionStatus, connectionStatus, os_major, applications, vm_name, hypervisor_id, agent_name, os_name,
				os_architecture, agent_current_version, agent_upgrade_version, agent_upgrade_link, test);

		// create a source group
		String Group = spogServer.ReturnRandom("group");
		Response response = spogServer.createGroup(org_id, Group, Group);
		String GroupId = response.then().extract().path("data.group_id").toString();

		String[] sources = new String[1];
		sources[0] = source_id;

		spogServer.addSourcetoSourceGroupwithCheck(GroupId, sources, Token, SpogConstants.SUCCESS_POST, null, test);

		compose_source_group.put("group_id", GroupId);
		compose_source_group.put("group_name", Group);

		sourceGroup.add(compose_source_group);

		// Get the source by id
		test.log(LogStatus.INFO, "Get the source by related id ");

		spogServer.errorHandle.printInfoMessageInDebugFile("get source and check");
		response = spogServer.getSourceById(Token, source_id, test);

		test.log(LogStatus.INFO, "the value of the organization_id:" + org_id);
		spogServer.getSourceByIdWithCheck(response, sourceGroup, SpogConstants.SUCCESS_GET_PUT_DELETE, source_id,
				sourceName, sourceType, sourceProduct, org_id, protectionStatus, connectionStatus, siteId, os_major,
				applications, available_actions1, vm_name, hypervisor_id, agent_name, os_name, os_architecture,
				agent_current_version, agent_upgrade_version, agent_upgrade_link, SpogMessageCode.SUCCESS_GET_PUT_DEL,
				test);

		// get source by id with csr read only valid token

		response = spogServer.getSourceById(ti.csr_readonly_token, source_id, test);
		test.log(LogStatus.INFO, "validating the response for the get sources by id");

		test.log(LogStatus.INFO, "the value of the organization_id:" + org_id);
		spogServer.getSourceByIdWithCheck(response, sourceGroup, SpogConstants.SUCCESS_GET_PUT_DELETE, source_id,
				sourceName, sourceType, sourceProduct, org_id, protectionStatus, connectionStatus, siteId, os_major,
				applications, available_actions1, vm_name, hypervisor_id, agent_name, os_name, os_architecture,
				agent_current_version, agent_upgrade_version, agent_upgrade_link, SpogMessageCode.SUCCESS_GET_PUT_DEL,
				test);

		// Deleting the source by id
		test.log(LogStatus.INFO, "delete source by id");
		response = spogServer.deleteSourcesById(ti.csr_token, source_id, test);
		spogServer.setToken(ti.csr_token);
		response = spogServer.deleteGroup(GroupId, test);

	}

	// Data Provider for the invalid cases
	@DataProvider(name = "sourceInfo1")
	public final Object[][] getSourceInfo1() {
		return new Object[][] {

				{ "invalid token", ti.direct_org1_user1_email, ti.common_password, SourceType.machine,
						SourceProduct.udp, ti.direct_org1_id, Direct_cloud_id, ProtectionStatus.unprotect,
						ConnectionStatus.online, OSMajor.windows.name(), "", "Ramya_vm1", null, null, null, null, null,
						null, null, "adsf", SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT },
				{ "missing token", ti.normal_msp1_suborg1_user1_email, ti.common_password, SourceType.machine,
						SourceProduct.udp, ti.normal_msp1_suborg1_id, msp_cloud_account_id, ProtectionStatus.unprotect,
						ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange", "Ramya_vm2", null,
						"Ramya_agent1", "windows 2012", "64", "1.0.0", "2.0", "http://upgrade", "",
						SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED },
				{ "Insufficient permissions direct-suborg", ti.direct_org1_user1_email, ti.common_password,
						SourceType.machine, SourceProduct.udp, ti.direct_org1_id, Direct_cloud_id,
						ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange",
						"Ramya_vm2", null, "Ramya_agent1", "windows 2012", "64", "1.0.0", "2.0", "http://upgrade",
						ti.normal_msp1_suborg1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Insufficient permissions direct-msp", ti.direct_org1_user1_email, ti.common_password,
						SourceType.machine, SourceProduct.udp, ti.direct_org1_id, Direct_cloud_id,
						ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange",
						"Ramya_vm2", null, "Ramya_agent1", "windows 2012", "64", "1.0.0", "2.0", "http://upgrade",
						ti.normal_msp1_suborg1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Insufficient permissions direct-rootmsp", ti.direct_org1_user1_email, ti.common_password,
						SourceType.machine, SourceProduct.udp, ti.direct_org1_id, Direct_cloud_id,
						ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange",
						"Ramya_vm2", null, "Ramya_agent1", "windows 2012", "64", "1.0.0", "2.0", "http://upgrade",
						ti.root_msp1_suborg1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Insufficient permissions direct-submsp_sub", ti.direct_org1_user1_email, ti.common_password,
						SourceType.machine, SourceProduct.udp, ti.direct_org1_id, Direct_cloud_id,
						ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange",
						"Ramya_vm2", null, "Ramya_agent1", "windows 2012", "64", "1.0.0", "2.0", "http://upgrade",
						ti.msp1_submsp1_suborg1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Insufficient permissions direct-submsp", ti.direct_org1_user1_email, ti.common_password,
						SourceType.machine, SourceProduct.udp, ti.direct_org1_id, Direct_cloud_id,
						ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange",
						"Ramya_vm2", null, "Ramya_agent1", "windows 2012", "64", "1.0.0", "2.0", "http://upgrade",
						ti.root_msp1_submsp1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Insufficient permissions suborg-direct", ti.normal_msp1_suborg1_user1_email, ti.common_password,
						SourceType.machine, SourceProduct.udp, ti.normal_msp1_suborg1_id, msp_cloud_account_id,
						ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange",
						"Ramya_vm2", null, "Ramya_agent1", "windows 2012", "64", "1.0.0", "2.0", "http://upgrade",
						ti.direct_org1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Insufficient permissions suborg-mspb", ti.normal_msp1_suborg1_user1_email, ti.common_password,
						SourceType.machine, SourceProduct.udp, ti.normal_msp1_suborg1_id, msp_cloud_account_id,
						ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange",
						"Ramya_vm2", null, "Ramya_agent1", "windows 2012", "64", "1.0.0", "2.0", "http://upgrade",
						ti.normal_msp_org2_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Insufficient permissions suborg-suborgb", ti.normal_msp1_suborg1_user1_email, ti.common_password,
						SourceType.machine, SourceProduct.udp, ti.normal_msp1_suborg1_id, msp_cloud_account_id,
						ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange",
						"Ramya_vm2", null, "Ramya_agent1", "windows 2012", "64", "1.0.0", "2.0", "http://upgrade",
						ti.normal_msp1_suborg2_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },

				{ "Insufficient permissions rootsuborg-direct", ti.root_msp1_suborg1_user1_email, ti.common_password,
						SourceType.machine, SourceProduct.udp, ti.root_msp1_suborg1_id, root_cloud_id,
						ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange",
						"Ramya_vm2", null, "Ramya_agent1", "windows 2012", "64", "1.0.0", "2.0", "http://upgrade",
						ti.direct_org1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Insufficient permissions rootsuborg-mspb", ti.root_msp1_suborg1_user1_email, ti.common_password,
						SourceType.machine, SourceProduct.udp, ti.root_msp1_suborg1_id, root_cloud_id,
						ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange",
						"Ramya_vm2", null, "Ramya_agent1", "windows 2012", "64", "1.0.0", "2.0", "http://upgrade",
						ti.root_msp_org2_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Insufficient permissions rootsuborg-rootsuborgb", ti.root_msp1_suborg1_user1_email,
						ti.common_password, SourceType.machine, SourceProduct.udp, ti.root_msp1_suborg1_id,
						root_cloud_id, ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(),
						"sql;exchange", "Ramya_vm2", null, "Ramya_agent1", "windows 2012", "64", "1.0.0", "2.0",
						"http://upgrade", ti.root_msp1_suborg2_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Insufficient permissions rootsuborg-submsp", ti.root_msp1_suborg1_user1_email, ti.common_password,
						SourceType.machine, SourceProduct.udp, ti.root_msp1_suborg1_id, root_cloud_id,
						ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange",
						"Ramya_vm2", null, "Ramya_agent1", "windows 2012", "64", "1.0.0", "2.0", "http://upgrade",
						ti.root_msp1_submsp1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Insufficient permissions rootsuborg-submsp_sub", ti.root_msp1_suborg1_user1_email,
						ti.common_password, SourceType.machine, SourceProduct.udp, ti.root_msp1_suborg1_id,
						root_cloud_id, ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(),
						"sql;exchange", "Ramya_vm2", null, "Ramya_agent1", "windows 2012", "64", "1.0.0", "2.0",
						"http://upgrade", ti.msp1_submsp1_suborg1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Insufficient permissions rootsuborg-submspmaa", ti.root_msp1_suborg1_user1_email, ti.common_password,
						SourceType.machine, SourceProduct.udp, ti.root_msp1_suborg1_id, root_cloud_id,
						ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange",
						"Ramya_vm2", null, "Ramya_agent1", "windows 2012", "64", "1.0.0", "2.0", "http://upgrade",
						ti.root_msp1_submsp1_account_admin_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },

				{ "Insufficient permissions submspsuborg-submsp", ti.msp1_submsp1_suborg1_user1_email,
						ti.common_password, SourceType.machine, SourceProduct.udp, ti.msp1_submsp1_sub_org1_id,
						root_cloud_id, ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(),
						"sql;exchange", "Ramya_vm2", null, "Ramya_agent1", "windows 2012", "64", "1.0.0", "2.0",
						"http://upgrade", ti.root_msp1_submsp2_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Insufficient permissions submspsuborg-rootsub", ti.msp1_submsp1_suborg1_user1_email,
						ti.common_password, SourceType.machine, SourceProduct.udp, ti.msp1_submsp1_sub_org1_id,
						root_cloud_id, ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(),
						"sql;exchange", "Ramya_vm2", null, "Ramya_agent1", "windows 2012", "64", "1.0.0", "2.0",
						"http://upgrade", ti.root_msp1_suborg1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Insufficient permissions submspsuborg-rootmsp", ti.msp1_submsp1_suborg1_user1_email,
						ti.common_password, SourceType.machine, SourceProduct.udp, ti.msp1_submsp1_sub_org1_id,
						root_cloud_id, ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(),
						"sql;exchange", "Ramya_vm2", null, "Ramya_agent1", "windows 2012", "64", "1.0.0", "2.0",
						"http://upgrade", ti.root_msp_org1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Insufficient permissions submspsuborg-rootmspmaa", ti.msp1_submsp1_suborg1_user1_email,
						ti.common_password, SourceType.machine, SourceProduct.udp, ti.msp1_submsp1_sub_org1_id,
						root_cloud_id, ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(),
						"sql;exchange", "Ramya_vm2", null, "Ramya_agent1", "windows 2012", "64", "1.0.0", "2.0",
						"http://upgrade", ti.root_msp_org1_msp_accountadmin1_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },

		};
	}

	// Get source by id with Invalid JWT token
	@Test(dataProvider = "sourceInfo1")
	public void GetSourceByIdForInValidToken(String testcase, String username, String password, SourceType sourceType,
			SourceProduct sourceProduct, String org_id, String siteId, ProtectionStatus protectionStatus,
			ConnectionStatus connectionStatus, String os_major, String applications, String vm_name,
			String hypervisor_id, String agent_name, String os_name, String os_architecture,
			String agent_current_version, String agent_upgrade_version, String agent_upgrade_link, String invalidToken,
			int spogContant, SpogMessageCode errorMessage) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Ramya.Nagepalli");
		String source_id = UUID.randomUUID().toString();
		String sourceName = spogServer.ReturnRandom("sourceName");

		test.log(LogStatus.INFO, "admin login");
		spogServer.errorHandle.printInfoMessageInDebugFile("admin login");
		spogServer.userLogin(username, password);
		String Token = spogServer.getJWTToken();
		sourceName = spogServer.ReturnRandom("ramya_source");

		test.log(LogStatus.INFO, "create source and check");
		spogServer.errorHandle.printInfoMessageInDebugFile("create source and check");

		source_id = spogServer.createSourceWithCheck(sourceName, sourceType, sourceProduct, org_id, siteId,
				protectionStatus, connectionStatus, os_major, applications, vm_name, hypervisor_id, agent_name, os_name,
				os_architecture, agent_current_version, agent_upgrade_version, agent_upgrade_link, test);

		// Get the source by id
		test.log(LogStatus.INFO, "get source by id with invalid scenario : " + testcase);
		Response response = spogServer.getSourceById(invalidToken, source_id, test);
		test.log(LogStatus.INFO, "validating the response for the get sources by id");
		test.log(LogStatus.INFO, "the value of the organization_id:" + org_id);

		spogServer.getSourceByIdWithCheck(response, source_group, spogContant, source_id, sourceName, sourceType,
				sourceProduct, org_id, protectionStatus, connectionStatus, siteId, os_major, applications,
				available_actions, vm_name, hypervisor_id, agent_name, os_name, os_architecture, agent_current_version,
				agent_upgrade_version, agent_upgrade_link, errorMessage, test);

		// Get the source by id
		test.log(LogStatus.INFO, "get source by id should fail for invalid source_id");
		String source_id1 = UUID.randomUUID().toString();
		response = spogServer.getSourceById(Token, source_id1, test);
		test.log(LogStatus.INFO, "validating the response for the get sources by id");
		test.log(LogStatus.INFO, "the value of the organization_id:" + org_id);

		spogServer.getSourceByIdWithCheck(response, source_group, SpogConstants.RESOURCE_NOT_EXIST, source_id,
				sourceName, sourceType, sourceProduct, org_id, protectionStatus, connectionStatus, siteId, os_major,
				applications, available_actions, vm_name, hypervisor_id, agent_name, os_name, os_architecture,
				agent_current_version, agent_upgrade_version, agent_upgrade_link, SpogMessageCode.RESOURCE_NOT_FOUND,
				test);
		// Deleting the source by id
		test.log(LogStatus.INFO, "delete source by id");
		response = spogServer.deleteSourcesById(ti.csr_token, source_id, test);

	}

	@DataProvider(name = "org_info")
	public final Object[][] org_info() {
		return new Object[][] {

				{ ti.direct_org1_id, ti.direct_org1_user1_token },
				{ ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token },
				{ ti.root_msp_org1_id, ti.root_msp_org1_user1_token },
				{ ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_token },
				{ ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_token },
				{ ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_token },

		};
	}

	@Test(dataProvider = "org_info")
	public void deleteResources(String org_id, String validToken) {

		policy4SpogServer.setToken(validToken);
		Response response = policy4SpogServer.getPolicies(null);
		ArrayList<String> policies = new ArrayList<>();
		policies = response.then().extract().path("data.policy_id");

		if (!policies.isEmpty()) {
			policies.stream().forEach(policy -> {
				policy4SpogServer.deletePolicybyPolicyId(ti.csr_token, policy);
			});
		}

		spogServer.setToken(validToken);
		response = spogServer.getSources("", "", 1, 20, true, test);

		ArrayList<String> sources = new ArrayList<>();
		sources = response.then().extract().path("data.source_id");
		if (!sources.isEmpty()) {
			sources.stream().forEach(source -> {
				spogServer.deleteSourceByID(source, test);
			});
		}

	}

	// passing the information to the BQ
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
		rep.endTest(test);
		rep.flush();
	}

}