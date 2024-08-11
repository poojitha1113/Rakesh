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

public class UpdateHypervisorTest extends base.prepare.Is4Org {

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
	private String username;
	private String siteID;
	private String organizationID;
	private String baas_destionation_ID_normal;
	private String baas_destionation_ID_zerocopy;
	private String cloudAccountSecret;
	private String  org_model_prefix=this.getClass().getSimpleName();

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
//				"91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", "cloud_direct_volume", "running",null, "dcef5f30-7217-4d00-a14a-bc82ff168c9d", "zero_copy", "mabzh02-vm001", "custom","custom", "0", "0", "7", "0",
//				"0", "0", "", "", "", "", "", "", "","", "", "", "", "", "", "", "ddd", test);

		this.username = "zhaoguo.ma@arcserve.com";
		spogServer.userLogin(username, password);
		spogHypervisorsServer.setToken(spogServer.getJWTToken());
		this.siteID = "a47b216f-17c5-422d-bede-a7f309124612";
		this.organizationID = "dee4021e-f854-486e-a9b4-080abd11840d";
		this.cloudAccountSecret = "0c24f474-6d1b-4922-8652-3f05df8afe86";
		this.baas_destionation_ID_normal = "2cd4567f-4319-4737-b4a7-361c2e53962f";
		this.gatewayServer = new GatewayServer(baseURI, port);
		
	}
	@DataProvider(name = "update_hypervisor_params_gateway_token_valid")
	public final Object[][] updateHypervisorValidGatewayTokenParams() {
		return new Object[][] {
			// update hypervisor name
			{"none", "hypervisor_name1", "vmware", "cloud_direct", "none", siteID, organizationID, "false", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host",
				"hypervisor_name1_updated", "vmware", "cloud_direct", "none", siteID, organizationID, "false", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
			
			// update schedule/schedule_type - from values to values
			{"none", "hypervisor_name3", "vmware", "cloud_direct", "none", siteID, organizationID, "true", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host",
				"hypervisor_name1_updated", "vmware", "cloud_direct", "none", siteID, organizationID, "true", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
		
			// update schedule/schedule_type - from values to none
			{"none", "hypervisor_name3a", "vmware", "cloud_direct", "none", siteID, organizationID, "true", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host",
				"hypervisor_name1_updated", "vmware", "cloud_direct", "none", siteID, organizationID, "true", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "none", "none", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
			// update schedule/schedule_type - from values to null
			{"none", "hypervisor_name3b", "vmware", "cloud_direct", "none", siteID, organizationID, "true", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host",
				"hypervisor_name1_updated", "vmware", "cloud_direct", "none", siteID, organizationID, "true", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", null, null, "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
			{"none", "hypervisor_name1", "vmware", "cloud_direct", "none", siteID, organizationID, "false", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host",
				"", "vmware", "cloud_direct", "none", siteID, organizationID, "false", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "", "", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
			// update agent settings - from values to none
			{"none", "hypervisor_name4", "vmware", "cloud_direct", "none", siteID, organizationID, "true", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host",
				"hypervisor_name1_updated", "vmware", "cloud_direct", "none", siteID, organizationID, "true", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "none", "none", "none", "none", "none",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
			// update agent settings - from values to empty
			{"none", "hypervisor_name4a", "vmware", "cloud_direct", "none", siteID, organizationID, "true", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host",
				"hypervisor_name1_updated", "vmware", "cloud_direct", "none", siteID, organizationID, "true", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "", "", "", "", "",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
			// update agent settings - from values to null
			{"none", "hypervisor_name4b", "vmware", "cloud_direct", "none", siteID, organizationID, "true", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "false",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host",
				"hypervisor_name1_updated", "vmware", "cloud_direct", "none", siteID, organizationID, "true", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", null, null, null, null, null,
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
			// update agent settings - from none to values
			{"none", "hypervisor_name5", "vmware", "cloud_direct", "none", siteID, organizationID, "true", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "none", "none", "none", "none",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host",
				"hypervisor_name1_updated", "vmware", "cloud_direct", "none", siteID, organizationID, "true", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "false",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
			// update hypervisor settings - from none to values
			{"none", "hypervisor_name6", "vmware", "cloud_direct", "none", siteID, organizationID, "true", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "none", "none", "none", "none",
				"none", "none", "none", "none", "none", "none", "none", "none",
				"hypervisor_name1_updated", "vmware", "cloud_direct", "none", siteID, organizationID, "true", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "false",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "none", "none"},
			// update hypervisor settings - from values to none
			{"none", "hypervisor_name7", "vmware", "cloud_direct", "none", siteID, organizationID, "true", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "none", "none", "none", "none",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", 				
				"hypervisor_name1_updated", "vmware", "cloud_direct", "none", siteID, organizationID, "true", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "false", 
				"none", "none", "none", "none", "none", "none", "none", "none"},
			// update hypervisor settings - from values to empty
			{"none", "hypervisor_name8", "vmware", "cloud_direct", "none", siteID, organizationID, "true", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "none", "none", "none", "none",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", 				
				"hypervisor_name1_updated", "vmware", "cloud_direct", "none", siteID, organizationID, "true", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "false", 
				"", "", "emptyarray", "emptyarray", "emptyarray", "emptyarray", "", ""},
			// update hypervisor settings - from values to null
			{"none", "hypervisor_name9", "vmware", "cloud_direct", "none", siteID, organizationID, "true", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "none", "none", "none", "none",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", 				
				"hypervisor_name1_updated", "vmware", "cloud_direct", "none", siteID, organizationID, "true", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "false", 
				null, null, null, null, null, null, null, null},
		};	
	}

//	@Test (dataProvider = "update_hypervisor_params_gateway_token_valid")
	public void updateHypervisorValidGatewayTokenTest(String hypervisor_id, String hypervisor_name, String hypervisor_type, String hypervisor_product, 
			String status, String site, String organization_id, String sync_discovered_vms, String vm_refresh_ts, 
			String cloud_direct_baas_destination, String cloud_direct_draas_destination, String schedule, String schedule_type, 
			String agent_name, String agent_current_version, String agent_upgrade_version, String agent_upgrade_link, String agent_autoupgrade, 
			String vmware_api_version, String vmware_vcenter_host, String vmware_resource_pools, String vmware_data_stores, String vmware_data_centers, String vmware_host_systems, 
			String bind_datacenter, String bind_host, String hypervisor_name_update, String hypervisor_type_update, String hypervisor_product_update, 
			String status_update, String site_update, String organization_id_update, String sync_discovered_vms_update, String vm_refresh_ts_update, 
			String cloud_direct_baas_destination_update, String cloud_direct_draas_destination_update, String schedule_update, String schedule_type_update, 
			String agent_name_update, String agent_current_version_update, String agent_upgrade_version_update, String agent_upgrade_link_update, String agent_autoupgrade_update, 
			String vmware_api_version_update, String vmware_vcenter_host_update, String vmware_resource_pools_update, String vmware_data_stores_update, String vmware_data_centers_update, String vmware_host_systems_update, 
			String bind_datacenter_update, String bind_host_update) {
		Response response = gatewayServer.LoginSite(siteID, cloudAccountSecret, test);
		String token = response.then().extract().path("data.token");
		
		spogHypervisorsServer.setToken(token);
		
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		String hypervisorID = spogHypervisorsServer.createHypervisorWithCheck(hypervisor_id, prefix + hypervisor_name, hypervisor_type, hypervisor_product, status, site, 
				organization_id, sync_discovered_vms, vm_refresh_ts, cloud_direct_baas_destination, cloud_direct_draas_destination, schedule, schedule_type, 
				agent_name, agent_current_version, agent_upgrade_version, agent_upgrade_link, agent_autoupgrade, 
				vmware_api_version, vmware_vcenter_host, vmware_resource_pools, vmware_data_stores, vmware_data_centers, vmware_host_systems, bind_datacenter, bind_host, test);
		spogHypervisorsServer.updateHypervisorWithCheck(hypervisorID, hypervisorID, hypervisor_name_update, hypervisor_type_update, hypervisor_product_update, status_update, site_update, 
				organization_id_update, sync_discovered_vms_update, vm_refresh_ts_update, cloud_direct_baas_destination_update, cloud_direct_draas_destination_update, schedule_update, schedule_type_update, 
				agent_name_update, agent_current_version_update, agent_upgrade_version_update, agent_upgrade_link_update, agent_autoupgrade_update, 
				vmware_api_version_update, vmware_vcenter_host_update, vmware_resource_pools_update, vmware_data_stores_update, vmware_data_centers_update, vmware_host_systems_update, bind_datacenter_update, bind_host_update, test);
		spogHypervisorsServer.deleteHypervisorWithCheck(hypervisorID, test);
	}
	
	@DataProvider(name = "update_hypervisor_params_spog_token_valid")
	public final Object[][] updateHypervisorValidSpogTokenParams() {
		return new Object[][] {
			// update hypervisor name
			{"none", "hypervisor_name1", "vmware", "cloud_direct", "none", siteID, organizationID, "false", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host",
				"hypervisor_name1_updated", "vmware", "cloud_direct", "none", siteID, organizationID, "false", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
			
			// update schedule/schedule_type - from values to values
			{"none", "hypervisor_name3", "vmware", "cloud_direct", "none", siteID, organizationID, "true", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host",
				"hypervisor_name1_updated", "vmware", "cloud_direct", "none", siteID, organizationID, "true", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
		
			// update schedule/schedule_type - from values to none
			{"none", "hypervisor_name3a", "vmware", "cloud_direct", "none", siteID, organizationID, "true", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host",
				"hypervisor_name1_updated", "vmware", "cloud_direct", "none", siteID, organizationID, "true", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "none", "none", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
			// update schedule/schedule_type - from values to null
			{"none", "hypervisor_name3b", "vmware", "cloud_direct", "none", siteID, organizationID, "true", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host",
				"hypervisor_name1_updated", "vmware", "cloud_direct", "none", siteID, organizationID, "true", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", null, null, "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
			{"none", "hypervisor_name1", "vmware", "cloud_direct", "none", siteID, organizationID, "false", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host",
				"", "vmware", "cloud_direct", "none", siteID, organizationID, "false", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "", "", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
			// update agent settings - from values to none
			{"none", "hypervisor_name4", "vmware", "cloud_direct", "none", siteID, organizationID, "true", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host",
				"hypervisor_name1_updated", "vmware", "cloud_direct", "none", siteID, organizationID, "true", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "none", "none", "none", "none", "none",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
			// update agent settings - from values to empty
			{"none", "hypervisor_name4a", "vmware", "cloud_direct", "none", siteID, organizationID, "true", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host",
				"hypervisor_name1_updated", "vmware", "cloud_direct", "none", siteID, organizationID, "true", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "", "", "", "", "",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
			// update agent settings - from values to null
			{"none", "hypervisor_name4b", "vmware", "cloud_direct", "none", siteID, organizationID, "true", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "false",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host",
				"hypervisor_name1_updated", "vmware", "cloud_direct", "none", siteID, organizationID, "true", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", null, null, null, null, null,
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
			// update agent settings - from none to values
			{"none", "hypervisor_name5", "vmware", "cloud_direct", "none", siteID, organizationID, "true", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "none", "none", "none", "none",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host",
				"hypervisor_name1_updated", "vmware", "cloud_direct", "none", siteID, organizationID, "true", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "false",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
			// update hypervisor settings - from none to values
			{"none", "hypervisor_name6", "vmware", "cloud_direct", "none", siteID, organizationID, "true", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "none", "none", "none", "none",
				"none", "none", "none", "none", "none", "none", "none", "none",
				"hypervisor_name1_updated", "vmware", "cloud_direct", "none", siteID, organizationID, "true", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "false",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
			// update hypervisor settings - from values to none
			{"none", "hypervisor_name7", "vmware", "cloud_direct", "none", siteID, organizationID, "true", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "none", "none", "none", "none",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", 				
				"hypervisor_name1_updated", "vmware", "cloud_direct", "none", siteID, organizationID, "true", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "false", 
				"none", "none", "none", "none", "none", "none", "none", "none"},
			// update hypervisor settings - from values to empty
			{"none", "hypervisor_name8", "vmware", "cloud_direct", "none", siteID, organizationID, "true", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "none", "none", "none", "none",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", 				
				"hypervisor_name1_updated", "vmware", "cloud_direct", "none", siteID, organizationID, "true", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "false", 
				"", "", "emptyarray", "emptyarray", "emptyarray", "emptyarray", "", ""},
			// update hypervisor settings - from values to null
			{"none", "hypervisor_name9", "vmware", "cloud_direct", "none", siteID, organizationID, "true", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "none", "none", "none", "none",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", 				
				"hypervisor_name1_updated", "vmware", "cloud_direct", "none", siteID, organizationID, "true", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true", 
				null, null, null, null, null, null, null, null},
		};	
	}

//	@Test (dataProvider = "update_hypervisor_params_spog_token_valid")
	public void updateHypervisorValidSpogTokenTest(String hypervisor_id, String hypervisor_name, String hypervisor_type, String hypervisor_product, 
			String status, String site, String organization_id, String sync_discovered_vms, String vm_refresh_ts, 
			String cloud_direct_baas_destination, String cloud_direct_draas_destination, String schedule, String schedule_type, 
			String agent_name, String agent_current_version, String agent_upgrade_version, String agent_upgrade_link, String agent_autoupgrade, 
			String vmware_api_version, String vmware_vcenter_host, String vmware_resource_pools, String vmware_data_stores, String vmware_data_centers, String vmware_host_systems, 
			String bind_datacenter, String bind_host, String hypervisor_name_update, String hypervisor_type_update, String hypervisor_product_update, 
			String status_update, String site_update, String organization_id_update, String sync_discovered_vms_update, String vm_refresh_ts_update, 
			String cloud_direct_baas_destination_update, String cloud_direct_draas_destination_update, String schedule_update, String schedule_type_update, 
			String agent_name_update, String agent_current_version_update, String agent_upgrade_version_update, String agent_upgrade_link_update, String agent_autoupgrade_update, 
			String vmware_api_version_update, String vmware_vcenter_host_update, String vmware_resource_pools_update, String vmware_data_stores_update, String vmware_data_centers_update, String vmware_host_systems_update, 
			String bind_datacenter_update, String bind_host_update) {
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
		
		spogHypervisorsServer.updateHypervisorWithCheck(hypervisorID, hypervisorID, hypervisor_name_update, hypervisor_type_update, hypervisor_product_update, status_update, site_update, 
				organization_id_update, sync_discovered_vms_update, vm_refresh_ts_update, cloud_direct_baas_destination_update, cloud_direct_draas_destination_update, schedule_update, schedule_type_update, 
				agent_name_update, agent_current_version_update, agent_upgrade_version_update, agent_upgrade_link_update, agent_autoupgrade_update, 
				vmware_api_version_update, vmware_vcenter_host_update, vmware_resource_pools_update, vmware_data_stores_update, vmware_data_centers_update, vmware_host_systems_update, bind_datacenter_update, bind_host_update, test);
		spogHypervisorsServer.deleteHypervisorWithCheck(hypervisorID, test);
	}
	
	
	@DataProvider(name = "update_hypervisor_params_invalid")
	public final Object[][] updateHypervisorInvalidParams() {
		return new Object[][] {
			// invalid baas_destination_ID
			{"none", "hypervisor_name2", "vmware", "cloud_direct", "none", siteID, organizationID, "false", String.valueOf(System.currentTimeMillis()), 
				baas_destionation_ID_normal, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host",
				"", "vmware", "cloud_direct", "none", siteID, organizationID, "false", String.valueOf(System.currentTimeMillis()), 
				"baas_destionation_ID_normal", "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", 400, "40000005"},
		};}
	
	@Test (dataProvider = "update_hypervisor_params_invalid")
	public void updateHypervisorInvalidtest(String hypervisor_id, String hypervisor_name, String hypervisor_type, String hypervisor_product, 
			String status, String site, String organization_id, String sync_discovered_vms, String vm_refresh_ts, 
			String cloud_direct_baas_destination, String cloud_direct_draas_destination, String schedule, String schedule_type, 
			String agent_name, String agent_current_version, String agent_upgrade_version, String agent_upgrade_link, String agent_autoupgrade, 
			String vmware_api_version, String vmware_vcenter_host, String vmware_resource_pools, String vmware_data_stores, String vmware_data_centers, String vmware_host_systems, 
			String bind_datacenter, String bind_host, String hypervisor_name_update, String hypervisor_type_update, String hypervisor_product_update, 
			String status_update, String site_update, String organization_id_update, String sync_discovered_vms_update, String vm_refresh_ts_update, 
			String cloud_direct_baas_destination_update, String cloud_direct_draas_destination_update, String schedule_update, String schedule_type_update, 
			String agent_name_update, String agent_current_version_update, String agent_upgrade_version_update, String agent_upgrade_link_update, String agent_autoupgrade_update, 
			String vmware_api_version_update, String vmware_vcenter_host_update, String vmware_resource_pools_update, String vmware_data_stores_update, String vmware_data_centers_update, String vmware_host_systems_update, 
			String bind_datacenter_update, String bind_host_update, int status_code, String errorCode) {
		Response response = gatewayServer.LoginSite(siteID, cloudAccountSecret, test);
		String token = response.then().extract().path("data.token");
		spogHypervisorsServer.setToken(token);
		
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		String hypervisorID = spogHypervisorsServer.createHypervisorWithCheck(hypervisor_id, prefix + hypervisor_name, hypervisor_type, hypervisor_product, status, site, 
				organization_id, sync_discovered_vms, vm_refresh_ts, cloud_direct_baas_destination, cloud_direct_draas_destination, schedule, schedule_type, 
				agent_name, agent_current_version, agent_upgrade_version, agent_upgrade_link, agent_autoupgrade, 
				vmware_api_version, vmware_vcenter_host, vmware_resource_pools, vmware_data_stores, vmware_data_centers, vmware_host_systems, bind_datacenter, bind_host, test);
		spogHypervisorsServer.updateHypervisorWithCodeCheck(hypervisorID, hypervisorID, hypervisor_name_update, hypervisor_type_update, hypervisor_product_update, status_update, site_update, 
				organization_id_update, sync_discovered_vms_update, vm_refresh_ts_update, cloud_direct_baas_destination_update, cloud_direct_draas_destination_update, schedule_update, schedule_type_update, 
				agent_name_update, agent_current_version_update, agent_upgrade_version_update, agent_upgrade_link_update, agent_autoupgrade_update, 
				vmware_api_version_update, vmware_vcenter_host_update, vmware_resource_pools_update, vmware_data_stores_update, vmware_data_centers_update, vmware_host_systems_update, bind_datacenter_update, bind_host_update, status_code, errorCode, test);
		
		spogServer.userLogin(csr_readonly_email, csr_readonly_password);
		spogHypervisorsServer.setToken(spogServer.getJWTToken());
		spogHypervisorsServer.updateHypervisorWithCodeCheck(hypervisorID, hypervisorID, hypervisor_name_update, hypervisor_type_update, hypervisor_product_update, status_update, site_update, 
				organization_id_update, sync_discovered_vms_update, vm_refresh_ts_update, cloud_direct_baas_destination_update, cloud_direct_draas_destination_update, schedule_update, schedule_type_update, 
				agent_name_update, agent_current_version_update, agent_upgrade_version_update, agent_upgrade_link_update, agent_autoupgrade_update, 
				vmware_api_version_update, vmware_vcenter_host_update, vmware_resource_pools_update, vmware_data_stores_update, vmware_data_centers_update, vmware_host_systems_update, bind_datacenter_update, bind_host_update, 403, "00100101", test);
		
		spogHypervisorsServer.setToken(token);
		spogHypervisorsServer.deleteHypervisorWithCheck(hypervisorID, test);
	}
	
	@Test
	public void updateHypervisorWithInvalidID() {
		Response response = gatewayServer.LoginSite(siteID, cloudAccountSecret, test);
		String token = response.then().extract().path("data.token");
		spogHypervisorsServer.setToken(token);
		
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		String non_exist_hypervisorID = UUID.randomUUID().toString();
		String invalidHypervisorID = non_exist_hypervisorID.substring(0, non_exist_hypervisorID.length()-2);
		
		spogHypervisorsServer.updateHypervisorWithCodeCheck(non_exist_hypervisorID, non_exist_hypervisorID, "", "vmware", "cloud_direct", "none", siteID, organizationID, "false", String.valueOf(System.currentTimeMillis()), 
				UUID.randomUUID().toString(), "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host",  404, "01000003", test);
		spogHypervisorsServer.updateHypervisorWithCodeCheck(invalidHypervisorID, invalidHypervisorID, "", "vmware", "cloud_direct", "none", siteID, organizationID, "false", String.valueOf(System.currentTimeMillis()), 
				UUID.randomUUID().toString(), "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host",  400, "40000005", test);
		spogHypervisorsServer.setToken("");
		spogHypervisorsServer.updateHypervisorWithCodeCheck(invalidHypervisorID, invalidHypervisorID, "", "vmware", "cloud_direct", "none", siteID, organizationID, "false", String.valueOf(System.currentTimeMillis()), 
				UUID.randomUUID().toString(), "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host",  401, "00900006", test);
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
