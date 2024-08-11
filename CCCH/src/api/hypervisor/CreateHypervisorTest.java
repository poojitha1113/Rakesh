package api.hypervisor;

import org.testng.annotations.Test;

import com.google.inject.PrivateBinder;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.sun.mail.handlers.message_rfc822;

import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGHypervisorsServer;
import InvokerServer.SPOGJobServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import bsh.org.objectweb.asm.Constants;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.User;
import genericutil.testcasescount;
import groovyjarjarantlr.PreservingFileWriter;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.codehaus.groovy.ast.expr.PrefixExpression;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import Constants.SpogConstants;

import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;

import Constants.SourceProduct;
import Constants.SourceType;

public class CreateHypervisorTest extends base.prepare.Is4Org{

	private SPOGServer spogServer;
	private SPOGHypervisorsServer spogHypervisorsServer;
	private SPOGDestinationServer spogDestinationServer;
	private GatewayServer gatewayServer;
	private String csrAdmin;
	private String csrPwd;
//	private ExtentReports rep;
	private ExtentTest test;
	
	private String csr_readonly_email = "csr_readonly_zhaoguo@arcserve.com";
	private String csr_readonly_password = "Caworld_2017";

	private String initial_msp_org_name = "spog_qa_msp_zhaoguo";
	private String initial_msp_email = "spog_qa_msp_zhaoguo@arcserve.com";
	private String initial_msp_email_full = "";
	private String initial_msp_first_name = "spog_qa_msp_ma";
	private String initial_msp_last_name = "spog_qa_msp_zhaoguo";
	
	private String initial_msp_email_added = "spog_qa_msp_zhaoguo_added@arcserve.com";
	private String initial_msp_email_full_added = "";
	private String initial_msp_first_name_added = "spog_qa_msp_ma";
	private String initial_msp_last_name_added = "spog_qa_msp_zhaoguo";
	private String initial_msp_user_ID_added = "";

	private String initial_direct_org_name = "spog_qa_direct_zhaoguo";
	private String initial_direct_email = "spog_qa_direct_zhaoguo@arcserve.com";
	private String initial_direct_email_full = "";
	private String initial_direct_first_name = "spog_qa_direct_ma";
	private String initial_direct_last_name = "spog_qa_direct_zhaoguo2";
	
	private String initial_direct_email_added = "spog_qa_direct_zhaoguo_added@arcserve.com";
	private String initial_direct_email_full_added = "";
	private String initial_direct_first_name_added = "spog_qa_direct_ma";
	private String initial_direct_last_name_added = "spog_qa_direct_zhaoguo2";
	private String initial_direct_user_ID_added = "";

	private String initial_sub_org_name_a = "spog_qa_sub_zhaoguo_a";
	private String initial_sub_email_a = "spog_qa_sub_zhaoguo_a@arcserve.com";
	private String initial_sub_email_full_a = "";
	private String initial_sub_first_name_a = "spog_qa_sub_ma_a";
	private String initial_sub_last_name_a = "spog_qa_sub_zhaoguo_a";
	private String initial_sub_user_ID_a = "";
	
	private String initial_sub_email_a_added = "spog_qa_sub_zhaoguo_a_added@arcserve.com";
	private String initial_sub_email_full_a_added = "";
	private String initial_sub_first_name_a_added = "spog_qa_sub_ma_a";
	private String initial_sub_last_name_a_added = "spog_qa_sub_zhaoguo_a";
	private String initial_sub_user_ID_a_added = "";

	private String initial_msp_orgID;
	private String initial_direct_orgID;
	private String initial_sub_orgID_a;

	private String password = "Zetta1234";

//	private SQLServerDb bqdb1;
//	public int Nooftest;
//	private long creationTime;
//	private String BQName = null;
//	private String runningMachine;
//	private testcasescount count1;
//	private String buildVersion;
	private String uuid1 = UUID.randomUUID().toString();
	private String uuid2 = UUID.randomUUID().toString();
	private String  org_model_prefix=this.getClass().getSimpleName();
	private String username;
	private String siteID;
	private String organizationID;
	private String baas_destionation_ID_normal;
	private String baas_destionation_ID_zerocopy;
	private String cloudAccountSecret;
	

	@BeforeClass
	@Parameters({ "baseURI", "port", "csrAdminUserName", "csrAdminPassword", "logFolder", "runningMachine",
			"buildVersion" })
	public void beforeClass(String baseURI, String port, String csrAdminUserName, String csrAdminPassword,
			String logFolder, String runningMachine, String buildVersion) {
		rep = ExtentManager.getInstance("GetSpecificFilterFromSpecificUsersTest", logFolder);
		test = rep.startTest("initializing data...");

		this.BQName = this.getClass().getSimpleName();
		String author = "Zhaoguo.Ma";
		this.runningMachine = runningMachine;
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date = new java.util.Date();
		this.buildVersion = buildVersion + "_" + dateFormater.format(date);
		Nooftest = 0;
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		if (count1.isstarttimehit() == 0) {
			System.out.println("Into get loggedInUserById");
			creationTime = System.currentTimeMillis();
			count1.setcreationtime(creationTime);
			// creationTime = System.currentTimeMillis();
			try {
				bqdb1.updateTable(BQName, runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0",
						String.valueOf(Nooftest), count1.getcreationtime(), "InProgress", author + " and Rest server is "+baseURI.split("//")[1]);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		spogServer = new SPOGServer(baseURI, port);
		spogHypervisorsServer = new SPOGHypervisorsServer(baseURI, port);
		this.csrAdmin = csrAdminUserName;
		this.csrPwd = csrAdminPassword;
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		String prefix = RandomStringUtils.randomAlphanumeric(8);

		// create msp organization #1, and then 1 sub-organizations in it, create one
		// admin for the sub organization;
		this.initial_msp_email_full = prefix + this.initial_msp_email;
		this.initial_msp_orgID = spogServer.CreateOrganizationWithCheck(prefix + initial_msp_org_name + org_model_prefix,
				SpogConstants.MSP_ORG, prefix + initial_msp_email, password, prefix + initial_msp_first_name,
				prefix + initial_msp_last_name);
		
		this.initial_msp_email_full_added = prefix + this.initial_msp_email_added;
		this.initial_msp_user_ID_added = spogServer.createUserAndCheck(initial_msp_email_full_added, password, prefix+ initial_msp_first_name_added, 
				prefix+ initial_msp_last_name_added, "msp_admin", initial_msp_orgID, test);

		this.initial_sub_orgID_a = spogServer.createAccountWithCheck(initial_msp_orgID, initial_sub_org_name_a,
				initial_msp_orgID);

		spogServer.userLogin(initial_msp_email_full, password);
		this.initial_sub_email_full_a = prefix + initial_sub_email_a;
		this.initial_sub_user_ID_a = spogServer.createUserAndCheck(prefix + this.initial_sub_email_a, password,
				prefix + this.initial_sub_first_name_a, prefix + this.initial_sub_last_name_a,
				SpogConstants.DIRECT_ADMIN, initial_sub_orgID_a, test);
		
		this.initial_sub_email_full_a_added = prefix + this.initial_sub_email_a_added;
		this.initial_sub_user_ID_a_added = spogServer.createUserAndCheck(prefix + this.initial_sub_email_a_added, password,
				prefix + this.initial_sub_first_name_a_added, prefix + this.initial_sub_last_name_a_added,
				SpogConstants.DIRECT_ADMIN, initial_sub_orgID_a, test);

		// create 1 direct organizations;
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		this.initial_direct_email_full = prefix + this.initial_direct_email;
		this.initial_direct_orgID = spogServer.CreateOrganizationWithCheck(prefix + initial_direct_org_name + org_model_prefix,
				SpogConstants.DIRECT_ORG, prefix + initial_direct_email, password, prefix + initial_direct_first_name,
				prefix + initial_direct_last_name);
		
		this.initial_direct_email_full_added = prefix + this.initial_direct_email_added;
		this.initial_direct_user_ID_added = spogServer.createUserAndCheck(prefix + this.initial_direct_email_added, password,
				prefix + this.initial_direct_first_name_added, prefix + this.initial_direct_last_name_added,
				SpogConstants.DIRECT_ADMIN, initial_direct_orgID, test);

		ExtentTest test = new ExtentTest("testname", "description");
		
//		spogServer.userLogin(csrAdmin, csrPwd);
//
//		String organizationID = spogServer.CreateOrganizationWithCheck(prefix + "orgName", SpogConstants.DIRECT_ORG,
//				prefix + "mail@arcserve.com", password, "Ma", "Zhaoguo");
//
//		spogServer.userLogin(prefix + "mail@arcserve.com", "Pa$$w0rd");
//		String cloudAccountID = spogServer.createCloudAccountWithCheck("cloudAccountKey", "cloudAccountSecret", "cloudAccountName", "cloud_direct", organizationID, 
//				"SKUTESTDATA_1_4_0_0_"+prefix, "SKUTESTDATA_1_0_0_0_"+prefix, "SKUTESTDATA_1_0_0_0_"+prefix, "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
	
//		spogServer.userLogin("vfhn8rwomail@arcserve.com", "Pa$$w0rd");
//		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
//		spogDestinationServer.setToken(spogServer.getJWTToken());
//		String destinationID = spogDestinationServer.createDestinationWithCheck(null, "a40fbcda-a684-4347-938f-d27e6efece18", "dcef5f30-7217-4d00-a14a-bc82ff168c9d",
//				"91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", "cloud_direct_volume", "running",null, "dcef5f30-7217-4d00-a14a-bc82ff168c9d", "zero_copy", "mabzh02-vm001", "7Dhf","7Dhf", "0", "0", "7", "0",
//				"0", "0", "", "", "", "", "", "", "","", "", "", "", "", "", "", "ddd", test);

		this.username = "zhaoguo.ma@arcserve.com";
		spogServer.userLogin(username, password);
		spogHypervisorsServer.setToken(spogServer.getJWTToken());
		this.siteID = "a47b216f-17c5-422d-bede-a7f309124612";
		this.organizationID = "dee4021e-f854-486e-a9b4-080abd11840d";
		this.cloudAccountSecret = "0c24f474-6d1b-4922-8652-3f05df8afe86";
		this.baas_destionation_ID_normal = "2cd4567f-4319-4737-b4a7-361c2e53962f";
		this.baas_destionation_ID_zerocopy = "540a93f9-39bf-4291-99f4-a30fb0cc4498";
		this.gatewayServer = new GatewayServer(baseURI, port);
		
	}
	@DataProvider(name = "create_hypervisor_params_valid")
	public final Object[][] postHypervisorValidParams() {
		return new Object[][] {
			// different hypervisor type
			{"none", "hypervisor_name1", "vmware", "cloud_direct", "none", siteID, organizationID, "false", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
			{"none", "hypervisor_name2", "hyperv", "cloud_direct", "none", siteID, organizationID, "false", String.valueOf(System.currentTimeMillis()),
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
			// set hypervisor_id 
			{UUID.randomUUID().toString(), "hypervisor_name3", "hyperv", "cloud_direct", "none", siteID, organizationID, "false", String.valueOf(System.currentTimeMillis()),
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
			// different value of pool/store/datacenter/system (which could be multiple values)
			{UUID.randomUUID().toString(), "hypervisor_name4", "vmware", "cloud_direct", "none", siteID, organizationID, "false", String.valueOf(System.currentTimeMillis()),
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1", "store1", "datacenter1", "system1", "bind_datacenter", "bind_host"},
			{UUID.randomUUID().toString(), "hypervisor_name5", "vmware", "cloud_direct", "none", siteID, organizationID, "false", String.valueOf(System.currentTimeMillis()),
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "none", "none", "none", "none", "bind_datacenter", "bind_host"},
			{UUID.randomUUID().toString(), "hypervisor_name6", "vmware", "cloud_direct", "none", siteID, organizationID, "false", String.valueOf(System.currentTimeMillis()),
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", null, null, null, null, "bind_datacenter", "bind_host"},
			{UUID.randomUUID().toString(), "hypervisor_name7", "vmware", "cloud_direct", "none", siteID, organizationID, "false", String.valueOf(System.currentTimeMillis()),
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "emptyarray", "emptyarray", "emptyarray", "emptyarray", "bind_datacenter", "bind_host"},
			// set different values for vmware
			{UUID.randomUUID().toString(), "hypervisor_name8a", "vmware", "cloud_direct", "none", siteID, organizationID, "false", String.valueOf(System.currentTimeMillis()),
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"none", "none", "emptyarray", "emptyarray", "emptyarray", "emptyarray", "none", "none"},
			{UUID.randomUUID().toString(), "hypervisor_name8b", "vmware", "cloud_direct", "none", siteID, organizationID, "false", String.valueOf(System.currentTimeMillis()),
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"", "", "emptyarray", "emptyarray", "emptyarray", "emptyarray", "", ""},
			{UUID.randomUUID().toString(), "hypervisor_name8c", "vmware", "cloud_direct", "none", siteID, organizationID, "false", String.valueOf(System.currentTimeMillis()),
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				null, null, "emptyarray", "emptyarray", "emptyarray", "emptyarray", null, null},
			// set different values for agent
			{UUID.randomUUID().toString(), "hypervisor_name9a", "hyperv", "cloud_direct", "none", siteID, organizationID, "false", String.valueOf(System.currentTimeMillis()),
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "none", "none", "none", "none", "none",
				"none", "none", "emptyarray", "emptyarray", "emptyarray", "emptyarray", "none", "none"},
			{UUID.randomUUID().toString(), "hypervisor_name9b", "hyperv", "cloud_direct", "none", siteID, organizationID, "false", String.valueOf(System.currentTimeMillis()),
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "", "", "", "", "",
				"none", "none", "emptyarray", "emptyarray", "emptyarray", "emptyarray", "none", "none"},
			{UUID.randomUUID().toString(), "hypervisor_name9c", "hyperv", "cloud_direct", "none", siteID, organizationID, "false", String.valueOf(System.currentTimeMillis()),
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", null, null, null, null, null,
				"none", "none", "emptyarray", "emptyarray", "emptyarray", "emptyarray", "none", "none"},
			
			//@author Rakesh.Chalamala       enhancement of Sprint 28      added date: 21-01-2019
			// set draas destination only - 200
			{"none", "hypervisor_name1", "vmware", "cloud_direct", "none", siteID, organizationID, "false", String.valueOf(System.currentTimeMillis()), 
				"none", baas_destionation_ID_zerocopy, "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
		};
	}

	@Test (dataProvider = "create_hypervisor_params_valid")
	public void createHypervisorValidtest(String hypervisor_id, String hypervisor_name, String hypervisor_type, String hypervisor_product, 
			String status, String site, String organization_id, String sync_discovered_vms, String vm_refresh_ts, 
			String cloud_direct_baas_destination, String cloud_direct_draas_destination, String schedule, String schedule_type, 
			String agent_name, String agent_current_version, String agent_upgrade_version, String agent_upgrade_link, String agent_autoupgrade, 
			String vmware_api_version, String vmware_vcenter_host, String vmware_resource_pools, String vmware_data_stores, String vmware_data_centers, String vmware_host_systems, 
			String bind_datacenter, String bind_host) {
		Response response = gatewayServer.LoginSite(siteID, cloudAccountSecret, test);
		String token = response.then().extract().path("data.token");
		spogHypervisorsServer.setToken(token);
		
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		String hypervisorID = spogHypervisorsServer.createHypervisorWithCheck(hypervisor_id, prefix + hypervisor_name, hypervisor_type, hypervisor_product, status, site, 
				organization_id, sync_discovered_vms, vm_refresh_ts, cloud_direct_baas_destination, cloud_direct_draas_destination, schedule, schedule_type, 
				agent_name, agent_current_version, agent_upgrade_version, agent_upgrade_link, agent_autoupgrade, 
				vmware_api_version, vmware_vcenter_host, vmware_resource_pools, vmware_data_stores, vmware_data_centers, vmware_host_systems, bind_datacenter, bind_host, test);
		spogServer.userLogin(username, password);
		spogHypervisorsServer.setToken(spogServer.getJWTToken());
		spogHypervisorsServer.deleteHypervisorWithCheck(hypervisorID, test);
	}
	
	@DataProvider(name = "create_hypervisor_params_invalid")
	public final Object[][] postHypervisorInvalidParams() {
		return new Object[][] {
			// do not set hypervisor name
			{"none", "", "vmware", "cloud_direct", "none", siteID, organizationID, "false", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", 400, "40000001"},
			// set invalid hypervisorType
			{"none", "hypervisor_name1", "xen", "cloud_direct", "none", siteID, organizationID, "false", String.valueOf(System.currentTimeMillis()),
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", 400, "01000008"},
			// set invalid product_type
			{"none", "hypervisor_name2", "xen", "hypervisor_product", "none", siteID, organizationID, "false", String.valueOf(System.currentTimeMillis()),
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", 400, "40000006"},
			// set invalid organizatoinID
			{"none", "hypervisor_name3", "vmware", "cloud_direct", "none", siteID, "organizationID", "false", String.valueOf(System.currentTimeMillis()),
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", 400, "40000005"},
			{"none", "hypervisor_name4", "vmware", "cloud_direct", "none", siteID, UUID.randomUUID().toString(), "false", String.valueOf(System.currentTimeMillis()),
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", 404, "0030000A"},
			// set invalid sync_discovered_vms
			{"none", "hypervisor_name5", "vmware", "cloud_direct", "none", siteID, organizationID, "invalid", String.valueOf(System.currentTimeMillis()),
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", 400, "00100001"},
			// set invalid baas_destionation_ID
			{"none", "hypervisor_name6", "vmware", "cloud_direct", "none", siteID, organizationID, "false", String.valueOf(System.currentTimeMillis()),
				"baas_destionation_ID_normal", "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", 400, "40000005"},
			{"none", "hypervisor_name7", "vmware", "cloud_direct", "none", siteID, organizationID, "false", String.valueOf(System.currentTimeMillis()),
				UUID.randomUUID().toString(), "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", 404, "00C00001"},
			// set invalid draas_destination_ID
			{"none", "hypervisor_name8", "vmware", "cloud_direct", "none", siteID, organizationID, "true", String.valueOf(System.currentTimeMillis()),
				baas_destionation_ID_normal, "draas_destination_ID", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", 400, "40000005"},
			// set invalid schedule/schedule_type
			{ UUID.randomUUID().toString(), "hypervisor_name10a", "hyperv", "cloud_direct", "none", siteID,
				organizationID, "none", "none", baas_destionation_ID_normal, "none", "none", "none", null, null,
				null, null, null, "none", "none", "emptyarray", "emptyarray", "emptyarray", "emptyarray",
				"none", "none", 400, "40000001" },
			{ UUID.randomUUID().toString(), "hypervisor_name10b", "hyperv", "cloud_direct", "none", siteID,
				organizationID, "", "", baas_destionation_ID_normal, "none", "", "", null, null, null, null,
				null, "none", "none", "emptyarray", "emptyarray", "emptyarray", "emptyarray", "none", "none", 400, "40000001" },
			{ UUID.randomUUID().toString(), "hypervisor_name10c", "hyperv", "cloud_direct", "none", siteID,
				organizationID, null, null, baas_destionation_ID_normal, "none", null, null, null, null, null,
				null, null, "none", "none", "emptyarray", "emptyarray", "emptyarray", "emptyarray", "none",
				"none", 400, "40000001" },
			
			
			//@author Rakesh.Chalamala 		enhancement of Sprint 28		Date:18-01-2019		using the destination ids of org:dee4021e-f854-486e-a9b4-080abd11840d
			//creation with two destinations should fail - The hypervisor should only have one backup destination.
			{"none", "hypervisor_twoDest", "vmware", "cloud_direct", "none", siteID, organizationID, "false", String.valueOf(System.currentTimeMillis()), 
					baas_destionation_ID_normal, baas_destionation_ID_zerocopy, "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
					"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", 400, "01000009"},
			{"none", "hypervisor_twoDest", "vmware", "cloud_direct", "none", siteID, organizationID, "false", String.valueOf(System.currentTimeMillis()), 
						baas_destionation_ID_normal, "invalid", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
						"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", 400, "40000005"},
			{"none", "hypervisor_twoDest", "vmware", "cloud_direct", "none", siteID, organizationID, "false", String.valueOf(System.currentTimeMillis()), 
							UUID.randomUUID().toString(), baas_destionation_ID_zerocopy, "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
							"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", 400, "01000009"},
			{"none", "hypervisor_twoDest", "vmware", "cloud_direct", "none", siteID, organizationID, "false", String.valueOf(System.currentTimeMillis()), 
								baas_destionation_ID_normal, UUID.randomUUID().toString(), "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
								"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", 400, "01000009"},
		};
	}
	@Test (dataProvider = "create_hypervisor_params_invalid")
	public void createHypervisorInvalidtest(String hypervisor_id, String hypervisor_name, String hypervisor_type, String hypervisor_product, 
			String status, String site, String organization_id, String sync_discovered_vms, String vm_refresh_ts, 
			String cloud_direct_baas_destination, String cloud_direct_draas_destination, String schedule, String schedule_type, 
			String agent_name, String agent_current_version, String agent_upgrade_version, String agent_upgrade_link, String agent_autoupgrade, 
			String vmware_api_version, String vmware_vcenter_host, String vmware_resource_pools, String vmware_data_stores, String vmware_data_centers, String vmware_host_systems, 
			String bind_datacenter, String bind_host, int status_code, String errorCode) {
		Response response = gatewayServer.LoginSite(siteID, cloudAccountSecret, test);
		String token = response.then().extract().path("data.token");
		spogHypervisorsServer.setToken(token);
		
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		spogHypervisorsServer.createHypervisorWithCodeCheck(hypervisor_id, hypervisor_name, hypervisor_type, hypervisor_product, status, site, 
				organization_id, sync_discovered_vms, vm_refresh_ts, cloud_direct_baas_destination, cloud_direct_draas_destination, schedule, schedule_type, 
				agent_name, agent_current_version, agent_upgrade_version, agent_upgrade_link, agent_autoupgrade, 
				vmware_api_version, vmware_vcenter_host, vmware_resource_pools, vmware_data_stores, vmware_data_centers, vmware_host_systems, bind_datacenter, bind_host, status_code, errorCode, test);
	}
	
	@Test
	public void createHypervisorNoToken() {
		Response response = gatewayServer.LoginSite(siteID, cloudAccountSecret, test);
		String token = response.then().extract().path("data.token");
		spogHypervisorsServer.setToken("");
		
		spogHypervisorsServer.createHypervisorWithCodeCheck(UUID.randomUUID().toString(), "hypervisor_name10c", "hyperv", "cloud_direct", "none", siteID,
				organizationID, null, null, baas_destionation_ID_normal, "none", null, null, null, null, null,
				null, null, "none", "none", "emptyarray", "emptyarray", "emptyarray", "emptyarray", "none",
				"none", 401, "00900006",  test);
	}
	
	@AfterMethod
	public void afterMethodTest() {
		rep.endTest(test);
		rep.flush();
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
