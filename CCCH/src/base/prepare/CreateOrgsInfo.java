package base.prepare;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang3.RandomStringUtils;
import org.seleniumhq.jetty9.security.SpnegoLoginService;
import org.seleniumhq.jetty9.server.UserIdentity;
import org.testng.ISuite;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.SpogConstants;
import InvokerServer.GatewayServer;
import InvokerServer.Org4SPOGServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGCloudRPSServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGHypervisorsServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import dataPreparation.JsonPreparation;

/**
 * Creates organizations and writes info to properties file
 * 
 * Direct organizations - 2
 * MSP organizations - 4 (root-2, normal-2)
 * SUB organizations - 2 (under each MSP)
 * SUB MSPs - 2 (under each root msp)
 * 
 * 2 users under each organization
 * MSP Account Admin under each msp and assign 1st sub org of each msp
 * 
 * @author Rakesh.Chalamala
 * @Sprint 36
 */
public class CreateOrgsInfo extends base.prepare.Is4Org{

	private Boolean createdOrgs=false;
	private SPOGServer spogServer;
	private UserSpogServer userSpogServer;
	private Org4SPOGServer org4SPOGServer;
	private SPOGDestinationServer spogDestinationServer;
	private JsonPreparation jp;
	private String csrAdminName;
	private String csrReadOnlyAdminName;
	private String csrAdminPassword;
	private String csrReadOnlyAdminPassword;
	private String csrToken;
	private PrintWriter writer;
	private String common_password = "Mclaren@2013"; 
	private ExtentTest test;
	private String suiteName;
	private String[] datacenters;
	
	private String baseURI;
	private String port;
	
	public static final String CSR_ORG_ID = "csr_org_id";
	public static final String CSR_ORG_NAME = "csr_org_name";
	public static final String CSR_ADMIN_EMAIL = "csr_admin_email";
	public static final String CSR_ADMIN_PASSWORD = "csr_admin_password";
	public static final String CSR_ADMIN_USER_ID = "csr_admin_user_id";
	public static final String CSR_READONLY_ADMIN_EMAIL = "csr_readonly_admin_email";
	public static final String CSR_READONLY_ADMIN_PASSWORD = "csr_readonly_admin_password";
	public static final String CSR_READONLY_ADMIN_USER_ID = "csr_readonly_admin_user_id";
	
	
	public static final String DIRECT_ORG1_ID = "direct_org1_id";
	public static final String DIRECT_ORG1_NAME = "direct_org1_name";
	public static final String DIRECT_ORG1_USER1_EMAIL = "direct_org1_user1_email";
	public static final String DIRECT_ORG1_USER1_ID = "direct_org1_user1_id";
	public static final String DIRECT_ORG1_MONITOR1_USER1_EMAIL = "direct_org1_monitor1_user1_email";
	public static final String DIRECT_ORG1_MONITOR1_USER1_ID = "direct_org1_monitor1_user1_id";
	public static final String DIRECT_ORG1_USER2_EMAIL = "direct_org1_user2_email";
	public static final String DIRECT_ORG1_USER2_ID = "direct_org1_user2_id";
	public static final String DIRECT_ORG2_ID = "direct_org2_id";
	public static final String DIRECT_ORG2_NAME = "direct_org2_name";
	public static final String DIRECT_ORG2_USER1_EMAIL = "direct_org2_user1_email";
	public static final String DIRECT_ORG2_USER1_ID = "direct_org2_user1_id";
	public static final String DIRECT_ORG2_MONITOR1_USER1_EMAIL = "direct_org2_monitor1_user1_email";
	public static final String DIRECT_ORG2_MONITOR1_USER1_ID = "direct_org2_monitor1_user1_id";
	public static final String DIRECT_ORG2_USER2_EMAIL = "direct_org2_user2_email";
	public static final String DIRECT_ORG2_USER2_ID = "direct_org2_user2_id";
			
	
	//root msps
	public static final String ROOT_MSP_ORG1_ID = "root_msp_org1_id";
	public static final String ROOT_MSP_ORG1_NAME = "root_msp_org1_name";
	public static final String ROOT_MSP_ORG1_USER1_EMAIL = "root_msp_org1_user1_email";
	public static final String ROOT_MSP_ORG1_USER1_ID = "root_msp_org1_user1_id"; 
	public static final String ROOT_MSP_ORG1_MONITOR1_USER1_EMAIL = "root_msp_org1_monitor1_user1_email";
	public static final String ROOT_MSP_ORG1_MONITOR1_USER1_ID = "root_msp_org1_monitor1_user1_id"; 
	public static final String ROOT_MSP_ORG1_USER2_EMAIL = "root_msp_org1_user2_email";
	public static final String ROOT_MSP_ORG1_USER2_ID = "root_msp_org1_user2_id";
	public static final String ROOT_MSP_ORG1_MSP_ACCOUNTADMIN1_EMAIL = "root_msp_org1_msp_accountadmin1_email";
	public static final String ROOT_MSP_ORG1_MSP_ACCOUNTADMIN1_ID = "root_msp_org1_msp_accountadmin1_id";
	public static final String ROOT_MSP_ORG1_MSP_ACCOUNTADMIN2_EMAIL = "root_msp_org1_msp_accountadmin2_email";
	public static final String ROOT_MSP_ORG1_MSP_ACCOUNTADMIN2_ID= "root_msp_org1_msp_accountadmin2_id";
	
	public static final String ROOT_MSP_ORG2_ID = "root_msp_org2_id";
	public static final String ROOT_MSP_ORG2_NAME = "root_msp_org2_name";
	public static final String ROOT_MSP_ORG2_USER1_EMAIL = "root_msp_org2_user1_email";
	public static final String ROOT_MSP_ORG2_USER1_ID = "root_msp_org2_user1_id"; 
	public static final String ROOT_MSP_ORG2_MONITOR1_USER1_EMAIL = "root_msp_org2_monitor1_user1_email";
	public static final String ROOT_MSP_ORG2_MONITOR1_USER1_ID = "root_msp_org2_monitor1_user1_id"; 
	public static final String ROOT_MSP_ORG2_USER2_EMAIL = "root_msp_org2_user2_email";
	public static final String ROOT_MSP_ORG2_USER2_ID = "root_msp_org2_user2_id";
	public static final String ROOT_MSP_ORG2_MSP_ACCOUNTADMIN1_EMAIL = "root_msp_org2_msp_accountadmin1_email";
	public static final String ROOT_MSP_ORG2_MSP_ACCOUNTADMIN1_ID = "root_msp_org2_msp_accountadmin1_id";
	public static final String ROOT_MSP_ORG2_MSP_ACCOUNTADMIN2_EMAIL = "root_msp_org2_msp_accountadmin2_email";
	public static final String ROOT_MSP_ORG2_MSP_ACCOUNTADMIN2_ID= "root_msp_org2_msp_accountadmin2_id";
	
	//normal msps
	public static final String NORMAL_MSP_ORG1_ID = "normal_msp_org1_id";
	public static final String NORMAL_MSP_ORG1_NAME = "normal_msp_org1_name";
	public static final String NORMAL_MSP_ORG1_USER1_EMAIL = "normal_msp_org1_user1_email";
	public static final String NORMAL_MSP_ORG1_USER1_ID = "normal_msp_org1_user1_id"; 
	public static final String NORMAL_MSP_ORG1_MONITOR1_USER1_EMAIL = "normal_msp_org1_monitor1_user1_email";
	public static final String NORMAL_MSP_ORG1_MONITOR1_USER1_ID = "normal_msp_org1_monitor1_user1_id"; 
	public static final String NORMAL_MSP_ORG1_USER2_EMAIL = "normal_msp_org1_user2_email";
	public static final String NORMAL_MSP_ORG1_USER2_ID = "normal_msp_org1_user2_id";
	public static final String NORMAL_MSP_ORG1_MSP_ACCOUNTADMIN1_EMAIL = "normal_msp_org1_msp_accountadmin1_email";
	public static final String NORMAL_MSP_ORG1_MSP_ACCOUNTADMIN1_ID = "normal_msp_org1_msp_accountadmin1_id";
	public static final String NORMAL_MSP_ORG1_MSP_ACCOUNTADMIN2_EMAIL = "normal_msp_org1_msp_accountadmin2_email";
	public static final String NORMAL_MSP_ORG1_MSP_ACCOUNTADMIN2_ID= "normal_msp_org1_msp_accountadmin2_id";
	
	public static final String NORMAL_MSP_ORG2_ID = "normal_msp_org2_id";
	public static final String NORMAL_MSP_ORG2_NAME = "normal_msp_org2_name";
	public static final String NORMAL_MSP_ORG2_USER1_EMAIL = "normal_msp_org2_user1_email";
	public static final String NORMAL_MSP_ORG2_USER1_ID = "normal_msp_org2_user1_id"; 
	public static final String NORMAL_MSP_ORG2_MONITOR1_USER1_EMAIL = "normal_msp_org2_monitor1_user1_email";
	public static final String NORMAL_MSP_ORG2_MONITOR1_USER1_ID = "normal_msp_org2_monitor1_user1_id";
	public static final String NORMAL_MSP_ORG2_USER2_EMAIL = "normal_msp_org2_user2_email";
	public static final String NORMAL_MSP_ORG2_USER2_ID = "normal_msp_org2_user2_id";
	public static final String NORMAL_MSP_ORG2_MSP_ACCOUNTADMIN1_EMAIL = "normal_msp_org2_msp_accountadmin1_email";
	public static final String NORMAL_MSP_ORG2_MSP_ACCOUNTADMIN1_ID = "normal_msp_org2_msp_accountadmin1_id";
	public static final String NORMAL_MSP_ORG2_MSP_ACCOUNTADMIN2_EMAIL = "normal_msp_org2_msp_accountadmin2_email";
	public static final String NORMAL_MSP_ORG2_MSP_ACCOUNTADMIN2_ID= "normal_msp_org2_msp_accountadmin2_id";
	
		
	//Direct sub orgs
	public static final String ROOT_MSP1_SUBORG1_ID = "root_msp1_suborg1_id";
	public static final String ROOT_MSP1_SUBORG1_NAME = "root_msp1_suborg1_name";
	public static final String ROOT_MSP1_SUBORG1_USER1_EMAIL = "root_msp1_suborg1_user1_email";
	public static final String ROOT_MSP1_SUBORG1_USER1_ID = "root_msp1_suborg1_user1_id";
	public static final String ROOT_MSP1_SUBORG1_MONITOR1_USER1_EMAIL = "root_msp1_suborg1_monitor1_user1_email";
	public static final String ROOT_MSP1_SUBORG1_MONITOR1_USER1_ID = "root_msp1_suborg1_monitor1_user1_id";
	public static final String ROOT_MSP1_SUBORG1_USER2_EMAIL = "root_msp1_suborg1_user2_email";
	public static final String ROOT_MSP1_SUBORG1_USER2_ID = "root_msp1_suborg1_user2_id";
	
	public static final String ROOT_MSP1_SUBORG2_ID = "root_msp1_suborg2_id";
	public static final String ROOT_MSP1_SUBORG2_NAME = "root_msp1_suborg2_name";
	public static final String ROOT_MSP1_SUBORG2_USER1_EMAIL = "root_msp1_suborg2_user1_email";
	public static final String ROOT_MSP1_SUBORG2_USER1_ID = "root_msp1_suborg2_user1_id";
	public static final String ROOT_MSP1_SUBORG2_MONITOR1_USER1_EMAIL = "root_msp1_suborg2_monitor1_user1_email";
	public static final String ROOT_MSP1_SUBORG2_MONITOR1_USER1_ID = "root_msp1_suborg2_monitor1_user1_id";
	
	public static final String ROOT_MSP2_SUBORG1_ID = "root_msp2_suborg1_id";
	public static final String ROOT_MSP2_SUBORG1_NAME = "root_msp2_suborg1_name";
	public static final String ROOT_MSP2_SUBORG1_USER1_EMAIL = "root_msp2_suborg1_user1_email";
	public static final String ROOT_MSP2_SUBORG1_USER1_ID = "root_msp2_suborg1_user1_id";
	public static final String ROOT_MSP2_SUBORG1_MONITOR1_USER1_EMAIL = "root_msp2_suborg1_monitor1_user1_email";
	public static final String ROOT_MSP2_SUBORG1_MONITOR1_USER1_ID = "root_msp2_suborg1_monitor1_user1_id";
	public static final String ROOT_MSP2_SUBORG1_USER2_EMAIL = "root_msp2_suborg1_user2_email";
	public static final String ROOT_MSP2_SUBORG1_USER2_ID = "root_msp2_suborg1_user2_id";
	
	public static final String ROOT_MSP2_SUBORG2_ID = "root_msp2_suborg2_id";
	public static final String ROOT_MSP2_SUBORG2_NAME = "root_msp2_suborg2_name";
	public static final String ROOT_MSP2_SUBORG2_USER1_EMAIL = "root_msp2_suborg2_user1_email";
	public static final String ROOT_MSP2_SUBORG2_USER1_ID = "root_msp2_suborg2_user1_id";
	public static final String ROOT_MSP2_SUBORG2_MONITOR1_USER1_EMAIL = "root_msp2_suborg2_monitor1_user1_email";
	public static final String ROOT_MSP2_SUBORG2_MONITOR1_USER1_ID = "root_msp2_suborg2_monitor1_user1_id";
	
	public static final String NORMAL_MSP1_SUBORG1_ID = "normal_msp1_suborg1_id";
	public static final String NORMAL_MSP1_SUBORG1_NAME = "normal_msp1_suborg1_name";
	public static final String NORMAL_MSP1_SUBORG1_USER1_EMAIL = "normal_msp1_suborg1_user1_email";
	public static final String NORMAL_MSP1_SUBORG1_USER1_ID = "normal_msp1_suborg1_user1_id";
	public static final String NORMAL_MSP1_SUBORG1_MONITOR1_USER1_EMAIL = "normal_msp1_suborg1_monitor1_user1_email";
	public static final String NORMAL_MSP1_SUBORG1_MONITOR1_USER1_ID = "normal_msp1_suborg1_monitor1_user1_id";
	public static final String NORMAL_MSP1_SUBORG1_USER2_EMAIL = "normal_msp1_suborg1_user2_email";
	public static final String NORMAL_MSP1_SUBORG1_USER2_ID = "normal_msp1_suborg1_user2_id";
	
	public static final String NORMAL_MSP1_SUBORG2_ID = "normal_msp1_suborg2_id";
	public static final String NORMAL_MSP1_SUBORG2_NAME = "normal_msp1_suborg2_name";
	public static final String NORMAL_MSP1_SUBORG2_USER1_EMAIL = "normal_msp1_suborg2_user1_email";
	public static final String NORMAL_MSP1_SUBORG2_USER1_ID = "normal_msp1_suborg2_user1_id";
	public static final String NORMAL_MSP1_SUBORG2_MONITOR1_USER1_EMAIL = "normal_msp1_suborg2_monitor1_user1_email";
	public static final String NORMAL_MSP1_SUBORG2_MONITOR1_USER1_ID = "normal_msp1_suborg2_monitor1_user1_id";
	
	
	public static final String NORMAL_MSP2_SUBORG1_ID = "normal_msp2_suborg1_id";
	public static final String NORMAL_MSP2_SUBORG1_NAME = "normal_msp2_suborg1_name";
	public static final String NORMAL_MSP2_SUBORG1_USER1_EMAIL = "normal_msp2_suborg1_user1_email";
	public static final String NORMAL_MSP2_SUBORG1_USER1_ID = "normal_msp2_suborg1_user1_id";
	public static final String NORMAL_MSP2_SUBORG1_MONITOR1_USER1_EMAIL = "normal_msp2_suborg1_monitor1_user1_email";
	public static final String NORMAL_MSP2_SUBORG1_MONITOR1_USER1_ID = "normal_msp2_suborg1_monitor1_user1_id";
	public static final String NORMAL_MSP2_SUBORG1_USER2_EMAIL = "normal_msp2_suborg1_user2_email";
	public static final String NORMAL_MSP2_SUBORG1_USER2_ID = "normal_msp2_suborg1_user2_id";
	
	public static final String NORMAL_MSP2_SUBORG2_ID = "normal_msp2_suborg2_id";
	public static final String NORMAL_MSP2_SUBORG2_NAME = "normal_msp2_suborg2_name";
	public static final String NORMAL_MSP2_SUBORG2_USER1_EMAIL = "normal_msp2_suborg2_user1_email";
	public static final String NORMAL_MSP2_SUBORG2_USER1_ID = "normal_msp2_suborg2_user1_id";
	public static final String NORMAL_MSP2_SUBORG2_MONITOR1_USER1_EMAIL = "normal_msp2_suborg2_monitor1_user1_email";
	public static final String NORMAL_MSP2_SUBORG2_MONITOR1_USER1_ID = "normal_msp2_suborg2_monitor1_user1_id";
			
	//sub msps
	public static final String ROOT_MSP1_SUBMSP_ORG1_ID = "root_msp1_submsp_org1_id";
	public static final String ROOT_MSP1_SUBMSP_ORG1_NAME = "root_msp1_submsp_org1_name";
	public static final String ROOT_MSP1_SUBMSP_ORG1_USER1_EMAIL = "root_msp1_submsp1_user1_email";
	public static final String ROOT_MSP1_SUBMSP_ORG1_USER1_ID = "root_msp1_submsp1_user1_id";
	public static final String ROOT_MSP1_SUBMSP_ORG1_MONITOR1_USER1_EMAIL = "root_msp1_submsp1_monitor1_user1_email";
	public static final String ROOT_MSP1_SUBMSP_ORG1_MONITOR1_USER1_ID = "root_msp1_submsp1_monitor1_user1_id";
	public static final String ROOT_MSP1_SUBMSP_ORG1_USER2_EMAIL = "root_msp1_submsp1_user2_email";
	public static final String ROOT_MSP1_SUBMSP_ORG1_USER2_ID = "root_msp1_submsp1_user2_id";
	public static final String ROOT_MSP1_SUBMSP_ORG1_ACCOUNT_ADMIN_EMAIL = "root_msp1_submsp1_account_admin_1";
	public static final String ROOT_MSP1_SUBMSP_ORG1_ACCOUNT_ADMIN_ID = "root_msp1_submsp1_account_admin_1_id";

	public static final String ROOT_MSP1_SUBMSP_ORG2_ID = "root_msp1_submsp_org2_id";
	public static final String ROOT_MSP1_SUBMSP_ORG2_NAME = "root_msp1_submsp_org2_name";
	public static final String ROOT_MSP1_SUBMSP_ORG2_USER1_EMAIL = "root_msp1_submsp2_user1_email";
	public static final String ROOT_MSP1_SUBMSP_ORG2_USER1_ID = "root_msp1_submsp2_user1_id";
	public static final String ROOT_MSP1_SUBMSP_ORG2_MONITOR1_USER1_EMAIL = "root_msp1_submsp2_monitor1_user1_email";
	public static final String ROOT_MSP1_SUBMSP_ORG2_MONITOR1_USER1_ID = "root_msp1_submsp2_monitor1_user1_id";
	public static final String ROOT_MSP1_SUBMSP_ORG2_USER2_EMAIL = "root_msp1_submsp2_user2_email";
	public static final String ROOT_MSP1_SUBMSP_ORG2_USER2_ID = "root_msp1_submsp2_user2_id";
	public static final String ROOT_MSP1_SUBMSP_ORG2_ACCOUNT_ADMIN_EMAIL = "root_msp1_submsp2_account_admin_1";
	public static final String ROOT_MSP1_SUBMSP_ORG2_ACCOUNT_ADMIN_ID = "root_msp1_submsp2_account_admin_1_id";
	
		
	public static final String ROOT_MSP2_SUBMSP_ORG1_ID = "root_msp2_submsp_org1_id";
	public static final String ROOT_MSP2_SUBMSP_ORG1_NAME = "root_msp2_submsp_org1_name";
	public static final String ROOT_MSP2_SUBMSP_ORG1_USER1_EMAIL = "root_msp2_submsp1_user1_email";
	public static final String ROOT_MSP2_SUBMSP_ORG1_USER1_ID = "root_msp2_submsp1_user1_id";
	public static final String ROOT_MSP2_SUBMSP_ORG1_MONITOR1_USER1_EMAIL = "root_msp2_submsp1_monitor1_user1_email";
	public static final String ROOT_MSP2_SUBMSP_ORG1_MONITOR1_USER1_ID = "root_msp2_submsp1_monitor1_user1_id";
	public static final String ROOT_MSP2_SUBMSP_ORG1_USER2_EMAIL = "root_msp2_submsp1_user2_email";
	public static final String ROOT_MSP2_SUBMSP_ORG1_USER2_ID = "root_msp2_submsp1_user2_id";
	public static final String ROOT_MSP2_SUBMSP_ORG1_ACCOUNT_ADMIN_EMAIL = "root_msp2_submsp1_account_admin_1";
	public static final String ROOT_MSP2_SUBMSP_ORG1_ACCOUNT_ADMIN_ID = "root_msp2_submsp1_account_admin_1_id";

	public static final String ROOT_MSP2_SUBMSP_ORG2_ID = "root_msp2_submsp_org2_id";
	public static final String ROOT_MSP2_SUBMSP_ORG2_NAME = "root_msp2_submsp_org2_name";
	public static final String ROOT_MSP2_SUBMSP_ORG2_USER1_EMAIL = "root_msp2_submsp2_user1_email";
	public static final String ROOT_MSP2_SUBMSP_ORG2_USER1_ID = "root_msp2_submsp2_user1_id";
	public static final String ROOT_MSP2_SUBMSP_ORG2_MONITOR1_USER1_EMAIL = "root_msp2_submsp2_monitor1_user1_email";
	public static final String ROOT_MSP2_SUBMSP_ORG2_MONITOR1_USER1_ID = "root_msp2_submsp2_monitor1_user1_id";
	public static final String ROOT_MSP2_SUBMSP_ORG2_USER2_EMAIL = "root_msp2_submsp2_user2_email";
	public static final String ROOT_MSP2_SUBMSP_ORG2_USER2_ID = "root_msp2_submsp2_user2_id";
	public static final String ROOT_MSP2_SUBMSP_ORG2_ACCOUNT_ADMIN_EMAIL = "root_msp2_submsp2_account_admin_1";
	public static final String ROOT_MSP2_SUBMSP_ORG2_ACCOUNT_ADMIN_ID = "root_msp2_submsp2_account_admin_1_id";


	
	//sub orgs under sub msps
	public static final String MSP1_SUBMSP1_SUB_ORG1_ID = "msp1_submsp1_sub_org1_id";
	public static final String MSP1_SUBMSP1_SUB_ORG1_NAME = "msp1_submsp1_sub_org1_name";
	public static final String MSP1_SUBMSP1_SUBORG1_USER1_EMAIL = "msp1_submsp1_suborg1_user1_email";
	public static final String MSP1_SUBMSP1_SUBORG1_USER1_ID = "msp1_submsp1_suborg1_user1_id";
	public static final String MSP1_SUBMSP1_SUBORG1_MONITOR_USER1_EMAIL = "msp1_submsp1_suborg1_monitor1_user1_email";
	public static final String MSP1_SUBMSP1_SUBORG1_MONITOR_USER1_ID = "msp1_submsp1_suborg1_monitor1_user1_id";
	public static final String MSP1_SUBMSP1_SUBORG1_USER2_EMAIL = "msp1_submsp1_suborg1_user2_email";
	public static final String MSP1_SUBMSP1_SUBORG1_USER2_ID = "msp1_submsp1_suborg1_user2_id";
	
	public static final String MSP1_SUBMSP1_SUB_ORG2_ID = "msp1_submsp1_sub_org2_id";
	public static final String MSP1_SUBMSP1_SUB_ORG2_NAME = "msp1_submsp1_sub_org2_name";
	public static final String MSP1_SUBMSP1_SUBORG2_USER1_EMAIL = "msp1_submsp1_suborg2_user1_email";
	public static final String MSP1_SUBMSP1_SUBORG2_USER1_ID = "msp1_submsp1_suborg2_user1_id";
	public static final String MSP1_SUBMSP1_SUBORG2_MONITOR_USER1_EMAIL = "msp1_submsp1_suborg2_monitor1_user1_email";
	public static final String MSP1_SUBMSP1_SUBORG2_MONITOR_USER1_ID = "msp1_submsp1_suborg2_monitor1_user1_id";
	
	public static final String MSP1_SUBMSP2_SUB_ORG1_ID = "msp1_submsp2_sub_org1_id";
	public static final String MSP1_SUBMSP2_SUB_ORG1_NAME = "msp1_submsp2_sub_org1_name";
	public static final String MSP1_SUBMSP2_SUBORG1_USER1_EMAIL = "msp1_submsp2_suborg1_user1_email";
	public static final String MSP1_SUBMSP2_SUBORG1_USER1_ID = "msp1_submsp2_suborg1_user1_id";
	public static final String MSP1_SUBMSP2_SUBORG1_MONITOR_USER1_EMAIL = "msp1_submsp2_suborg1_monitor1_user1_email";
	public static final String MSP1_SUBMSP2_SUBORG1_MONITOR_USER1_ID = "msp1_submsp2_suborg1_monitor1_user1_id";
	public static final String MSP1_SUBMSP2_SUBORG1_USER2_EMAIL = "msp1_submsp2_suborg1_user2_email";
	public static final String MSP1_SUBMSP2_SUBORG1_USER2_ID = "msp1_submsp2_suborg1_user2_id";
	
	public static final String MSP1_SUBMSP2_SUB_ORG2_ID = "msp1_submsp2_sub_org2_id";
	public static final String MSP1_SUBMSP2_SUB_ORG2_NAME = "msp1_submsp2_sub_org2_name";
	public static final String MSP1_SUBMSP2_SUBORG2_USER1_EMAIL = "msp1_submsp2_suborg2_user1_email";
	public static final String MSP1_SUBMSP2_SUBORG2_USER1_ID = "msp1_submsp2_suborg2_user1_id";
	public static final String MSP1_SUBMSP2_SUBORG2_MONITOR_USER1_EMAIL = "msp1_submsp2_suborg2_monitor1_user1_email";
	public static final String MSP1_SUBMSP2_SUBORG2_MONITOR_USER1_ID = "msp1_submsp2_suborg2_monitor1_user1_id";
	
	
	public static final String MSP2_SUBMSP1_SUB_ORG1_ID = "msp2_submsp1_sub_org1_id";
	public static final String MSP2_SUBMSP1_SUB_ORG1_NAME = "msp2_submsp1_sub_org1_name";
	public static final String MSP2_SUBMSP1_SUBORG1_USER1_EMAIL = "msp2_submsp1_suborg1_user1_email";
	public static final String MSP2_SUBMSP1_SUBORG1_USER1_ID = "msp2_submsp1_suborg1_user1_id";
	public static final String MSP2_SUBMSP1_SUBORG1_MONITOR_USER1_EMAIL = "msp2_submsp1_suborg1_monitor_user1_email";
	public static final String MSP2_SUBMSP1_SUBORG1_MONITOR_USER1_ID = "msp2_submsp1_suborg1_monitor_user1_id";
	public static final String MSP2_SUBMSP1_SUBORG1_USER2_EMAIL = "msp2_submsp1_suborg1_user2_email";
	public static final String MSP2_SUBMSP1_SUBORG1_USER2_ID = "msp2_submsp1_suborg1_user2_id";
	
	public static final String MSP2_SUBMSP1_SUB_ORG2_ID = "msp2_submsp1_sub_org2_id";
	public static final String MSP2_SUBMSP1_SUB_ORG2_NAME = "msp2_submsp1_sub_org2_name";
	public static final String MSP2_SUBMSP1_SUBORG2_USER1_EMAIL = "msp2_submsp1_suborg2_user1_email";
	public static final String MSP2_SUBMSP1_SUBORG2_USER1_ID = "msp2_submsp1_suborg2_user1_id";
	public static final String MSP2_SUBMSP1_SUBORG2_MONITOR_USER1_EMAIL = "msp2_submsp1_suborg2_monitor_user1_email";
	public static final String MSP2_SUBMSP1_SUBORG2_MONITOR_USER1_ID = "msp2_submsp1_suborg2_monitor_user1_id";
	
	
	public static final String MSP2_SUBMSP2_SUB_ORG1_ID = "msp2_submsp2_sub_org1_id";
	public static final String MSP2_SUBMSP2_SUB_ORG1_NAME = "msp2_submsp2_sub_org1_name";
	public static final String MSP2_SUBMSP2_SUBORG1_USER1_EMAIL = "msp2_submsp2_suborg1_user1_email";
	public static final String MSP2_SUBMSP2_SUBORG1_USER1_ID = "msp2_submsp2_suborg1_user1_id";
	public static final String MSP2_SUBMSP2_SUBORG1_MONITOR_USER1_EMAIL = "msp2_submsp2_suborg1_monitor_user1_email";
	public static final String MSP2_SUBMSP2_SUBORG1_MONITOR_USER1_ID = "msp2_submsp2_suborg1_monitor_user1_id";
	public static final String MSP2_SUBMSP2_SUBORG1_USER2_EMAIL = "msp2_submsp2_suborg1_user2_email";
	public static final String MSP2_SUBMSP2_SUBORG1_USER2_ID = "msp2_submsp2_suborg1_user2_id";
	
	public static final String MSP2_SUBMSP2_SUB_ORG2_ID = "msp2_submsp2_sub_org2_id";
	public static final String MSP2_SUBMSP2_SUB_ORG2_NAME = "msp2_submsp2_sub_org2_name";
	public static final String MSP2_SUBMSP2_SUBORG2_USER1_EMAIL = "msp2_submsp2_suborg2_user1_email";
	public static final String MSP2_SUBMSP2_SUBORG2_USER1_ID = "msp2_submsp2_suborg2_user1_id";
	public static final String MSP2_SUBMSP2_SUBORG2_MONITOR_USER1_EMAIL = "msp2_submsp2_suborg2_monitor_user1_email";
	public static final String MSP2_SUBMSP2_SUBORG2_MONITOR_USER1_ID = "msp2_submsp2_suborg2_monitor_user1_id";
	
	
	public static final String COMMON_PASSWORD = "common_password";

	public static String FILE_PATH;
	
	public void setSuiteName(String name) {
		this.suiteName = name;
	}
	
	public String getSuiteName() {
		return suiteName;
	}
	
	public CreateOrgsInfo(ISuite suite, PrintWriter writer) {
		
		baseURI = suite.getParameter("baseURI");
		port = suite.getParameter("port");
		csrAdminName = suite.getParameter("csrAdminUserName");
		csrAdminPassword = suite.getParameter("csrAdminPassword");
		csrReadOnlyAdminName = suite.getParameter("csrReadOnlyAdminName");
		csrReadOnlyAdminPassword = suite.getParameter("csrReadOnlyAdminPassword");
		
		setSuiteName(suite.getName());
		setWriter(writer);
		
		writeToFile(COMMON_PASSWORD, common_password);
		writeToFile(CSR_ADMIN_EMAIL, csrAdminName);
		writeToFile(CSR_ADMIN_PASSWORD, csrAdminPassword);
		writeToFile(CSR_READONLY_ADMIN_EMAIL, csrReadOnlyAdminName);
		writeToFile(CSR_READONLY_ADMIN_PASSWORD, csrReadOnlyAdminPassword);
	}
	
	public void create() {
		
		spogServer = new SPOGServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		org4SPOGServer = new Org4SPOGServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		jp = new JsonPreparation();
		test = new ExtentTest("create organizations", "Create and store orgs in properties file before starting suite and for use across suite");
		
		try {
		spogServer.userLogin(this.csrAdminName, this.csrAdminPassword);
		csrToken = spogServer.getJWTToken();
		
		writeToFile(CSR_ORG_ID, spogServer.GetLoggedinUserOrganizationID());
		writeToFile(CSR_ORG_NAME, spogServer.getLoggedInOrganizationName());
		writeToFile(CSR_ADMIN_USER_ID, spogServer.GetLoggedinUser_UserID());
		spogServer.userLogin(this.csrReadOnlyAdminName, this.csrReadOnlyAdminPassword);
		writeToFile(CSR_READONLY_ADMIN_USER_ID, spogServer.GetLoggedinUser_UserID());
		
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		spogServer.setToken(csrToken);
		spogDestinationServer.setToken(csrToken);
		datacenters = spogDestinationServer.getDestionationDatacenterID();
		String orgPrefix = spogServer.ReturnRandom("suiteOrg")+"_"+suiteName;
		
		
		/********************************** Direct orgs ******************************************/			
		//create direct organization #1
		test.log(LogStatus.INFO, "create direct organization #1");
		String direct_org_1_name = orgPrefix + "direct_1";
		String direct_org_1 = spogServer.CreateOrganizationByEnrollWithCheck(direct_org_1_name, SpogConstants.DIRECT_ORG, prefix + "direct_1_user_1@arcserve.com", common_password, "firstName", "lastname");
		spogServer.postCloudhybridInFreeTrial(csrToken, direct_org_1, SpogConstants.DIRECT_ORG, false, false);
		test.log(LogStatus.INFO, "create user in direct organization #1");
		String user_id1 = spogServer.createUserAndCheck(prefix + "direct_1_monitor_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.DIRECT_MONITOR, direct_org_1, test);
		spogServer.userLogin(prefix + "direct_1_user_1@arcserve.com", common_password);
		
		writeToFile(DIRECT_ORG1_ID, direct_org_1);
		writeToFile(DIRECT_ORG1_NAME, direct_org_1_name);
		writeToFile(DIRECT_ORG1_USER1_EMAIL, prefix + "direct_1_user_1@arcserve.com");	
		writeToFile(DIRECT_ORG1_USER1_ID, spogServer.GetLoggedinUser_UserID());
		writeToFile(DIRECT_ORG1_MONITOR1_USER1_EMAIL, prefix + "direct_1_monitor_1@arcserve.com");	
		writeToFile(DIRECT_ORG1_MONITOR1_USER1_ID, user_id1);
		
		//create another user in direct organization #1
		test.log(LogStatus.INFO, "create another user in direct organization #1");
		String user_id = spogServer.createUserAndCheck(prefix + "direct_1_user_2@arcserve.com", common_password, "first_name", "last_name", SpogConstants.DIRECT_ADMIN, direct_org_1, test);
		
		writeToFile(DIRECT_ORG1_USER2_EMAIL, prefix + "direct_1_user_2@arcserve.com");
		writeToFile(DIRECT_ORG1_USER2_ID, user_id);
				
		//create direct organization #2
		spogServer.setToken(csrToken);
		String direct_org_2_name = orgPrefix + "direct_2";
		String direct_org_2 = spogServer.CreateOrganizationByEnrollWithCheck(direct_org_2_name, SpogConstants.DIRECT_ORG, prefix + "direct_2_user_1@arcserve.com", common_password, "firstName", "lastname");
//		spogServer.postCloudhybridInFreeTrial(csrToken, direct_org_2, SpogConstants.DIRECT_ORG, false, false);
		user_id1 = spogServer.createUserAndCheck(prefix + "direct_2_monitor_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.DIRECT_MONITOR, direct_org_2, test);
		
		spogServer.userLogin(prefix + "direct_2_user_1@arcserve.com", common_password);
		
		writeToFile(DIRECT_ORG2_ID, direct_org_2);
		writeToFile(DIRECT_ORG2_NAME, direct_org_2_name);
		writeToFile(DIRECT_ORG2_USER1_EMAIL, prefix + "direct_2_user_1@arcserve.com");
		writeToFile(DIRECT_ORG2_USER1_ID, spogServer.GetLoggedinUser_UserID());
		writeToFile(DIRECT_ORG2_MONITOR1_USER1_EMAIL, prefix + "direct_2_monitor_1@arcserve.com");	
		writeToFile(DIRECT_ORG2_MONITOR1_USER1_ID, user_id1);

		//create another user in direct organization #2
		user_id = spogServer.createUserAndCheck(prefix + "direct_2_user_2@arcserve.com", common_password, "first_name", "last_name", SpogConstants.DIRECT_ADMIN, direct_org_2, test);

		writeToFile(DIRECT_ORG2_USER2_EMAIL, prefix + "direct_2_user_2@arcserve.com");
		writeToFile(DIRECT_ORG2_USER2_ID, user_id);
				
		/****************************************** end ********************************************/
		
		
		
		
		
		/****************************************************** MSP orgs ********************************************************/		
		// create root msp organization #1
		spogServer.setToken(csrToken);
		String root_msp_org_1_name = orgPrefix + "root_msp_1";
		String root_msp_org_1 = spogServer.CreateOrganizationByEnrollWithCheck(root_msp_org_1_name, SpogConstants.MSP_ORG, prefix + "root_msp_1_user_1@arcserve.com", common_password, "firstName", "lastname");
		spogServer.postCloudhybridInFreeTrial(csrToken, root_msp_org_1, SpogConstants.MSP_ORG, false, false);
		spogServer.convertTo3Tier(root_msp_org_1);
		spogServer.userLogin(prefix + "root_msp_1_user_1@arcserve.com", common_password);
		
		writeToFile(ROOT_MSP_ORG1_ID, root_msp_org_1);
		writeToFile(ROOT_MSP_ORG1_NAME, root_msp_org_1_name);
		writeToFile(ROOT_MSP_ORG1_USER1_EMAIL, prefix + "root_msp_1_user_1@arcserve.com");
		writeToFile(ROOT_MSP_ORG1_USER1_ID, spogServer.GetLoggedinUser_UserID());
		
		user_id1 = spogServer.createUserAndCheck(prefix + "root_msp_1_monitor_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.MSP_MONITOR, root_msp_org_1, test);
		
		writeToFile(ROOT_MSP_ORG1_MONITOR1_USER1_EMAIL,prefix + "root_msp_1_monitor_1@arcserve.com");
		writeToFile(ROOT_MSP_ORG1_MONITOR1_USER1_ID,user_id1);
		
				
		// create another user in msp organization #1
		user_id = spogServer.createUserAndCheck(prefix + "root_msp_1_user_2@arcserve.com", common_password, "first_name", "last_name", SpogConstants.MSP_ADMIN, root_msp_org_1, test);
		writeToFile(ROOT_MSP_ORG1_USER2_EMAIL, prefix + "root_msp_1_user_2@arcserve.com");
		writeToFile(ROOT_MSP_ORG1_USER2_ID, user_id);
		
		// create 2 account admins in msp organization #1
		String root_msp_org_1_account_admin_1 = spogServer.createUserAndCheck(prefix + "root_msp_1_account_admin_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.MSP_ACCOUNT_ADMIN, root_msp_org_1, test);
		writeToFile(ROOT_MSP_ORG1_MSP_ACCOUNTADMIN1_EMAIL, prefix + "root_msp_1_account_admin_1@arcserve.com");
		writeToFile(ROOT_MSP_ORG1_MSP_ACCOUNTADMIN1_ID, root_msp_org_1_account_admin_1);

		String root_msp_org_1_account_admin_2 = spogServer.createUserAndCheck(prefix + "root_msp_1_account_admin_2@arcserve.com", common_password, "first_name", "last_name", SpogConstants.MSP_ACCOUNT_ADMIN, root_msp_org_1, test);
		writeToFile(ROOT_MSP_ORG1_MSP_ACCOUNTADMIN2_EMAIL, prefix + "root_msp_1_account_admin_2@arcserve.com");
		writeToFile(ROOT_MSP_ORG1_MSP_ACCOUNTADMIN2_ID, root_msp_org_1_account_admin_2);
		


		// create root msp organization #2
		spogServer.setToken(csrToken);
		String root_msp_org_2_name = orgPrefix + "root_msp_2";
		String root_msp_org_2 = spogServer.CreateOrganizationByEnrollWithCheck(root_msp_org_2_name, SpogConstants.MSP_ORG, prefix + "root_msp_2_user_1@arcserve.com", common_password, "firstName", "lastname");
//		spogServer.postCloudhybridInFreeTrial(csrToken, root_msp_org_2, SpogConstants.MSP_ORG, false, false);
		spogServer.convertTo3Tier(root_msp_org_2);
		
		user_id1 = spogServer.createUserAndCheck(prefix + "root_msp_2_monitor_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.MSP_MONITOR, root_msp_org_2, test);
		spogServer.userLogin(prefix + "root_msp_2_user_1@arcserve.com", common_password);

		writeToFile(ROOT_MSP_ORG2_ID, root_msp_org_2);
		writeToFile(ROOT_MSP_ORG2_NAME, root_msp_org_2_name);
		writeToFile(ROOT_MSP_ORG2_USER1_EMAIL, prefix + "root_msp_2_user_1@arcserve.com");
		writeToFile(ROOT_MSP_ORG2_USER1_ID, spogServer.GetLoggedinUser_UserID());
		writeToFile(ROOT_MSP_ORG2_MONITOR1_USER1_EMAIL, prefix + "root_msp_2_monitor_1@arcserve.com");
		writeToFile(ROOT_MSP_ORG2_MONITOR1_USER1_ID, user_id1);

		// create another user in msp organization #2
		user_id = spogServer.createUserAndCheck(prefix + "root_msp_2_user_2@arcserve.com", common_password, "first_name", "last_name", SpogConstants.MSP_ADMIN, root_msp_org_2, test);
		writeToFile(ROOT_MSP_ORG2_USER2_EMAIL, prefix + "root_msp_2_user_2@arcserve.com");
		writeToFile(ROOT_MSP_ORG2_USER2_ID, user_id);

		// create 2 account admins in msp organization #2
		String root_msp_org_2_account_admin_1 = spogServer.createUserAndCheck(prefix + "root_msp_2_account_admin_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.MSP_ACCOUNT_ADMIN, root_msp_org_2, test);
		writeToFile(ROOT_MSP_ORG2_MSP_ACCOUNTADMIN1_EMAIL, prefix + "root_msp_2_account_admin_1@arcserve.com");
		writeToFile(ROOT_MSP_ORG2_MSP_ACCOUNTADMIN1_ID, root_msp_org_2_account_admin_1);

		String root_msp_org_2_account_admin_2 = spogServer.createUserAndCheck(prefix + "root_msp_2_account_admin_2@arcserve.com", common_password, "first_name", "last_name", SpogConstants.MSP_ACCOUNT_ADMIN, root_msp_org_2, test);
		writeToFile(ROOT_MSP_ORG2_MSP_ACCOUNTADMIN2_EMAIL, prefix + "root_msp_2_account_admin_2@arcserve.com");
		writeToFile(ROOT_MSP_ORG2_MSP_ACCOUNTADMIN2_ID, root_msp_org_2_account_admin_2);

		
	
		// create normal msp organization #1
		spogServer.setToken(csrToken);
		String normal_msp_org_1_name = orgPrefix + "normal_msp_1";
		String normal_msp_org_1 = spogServer.CreateOrganizationByEnrollWithCheck(normal_msp_org_1_name, SpogConstants.MSP_ORG, prefix + "normal_msp_1_user_1@arcserve.com", common_password, "firstName", "lastname");
		spogServer.postCloudhybridInFreeTrial(csrToken, normal_msp_org_1, SpogConstants.MSP_ORG, false, false);
		
		user_id1 = spogServer.createUserAndCheck(prefix + "normal_msp_1_monitor_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.MSP_MONITOR, normal_msp_org_1, test);
		spogServer.userLogin(prefix + "normal_msp_1_user_1@arcserve.com", common_password);

		writeToFile(NORMAL_MSP_ORG1_ID, normal_msp_org_1);
		writeToFile(NORMAL_MSP_ORG1_NAME, normal_msp_org_1_name);
		writeToFile(NORMAL_MSP_ORG1_USER1_EMAIL, prefix + "normal_msp_1_user_1@arcserve.com");
		writeToFile(NORMAL_MSP_ORG1_USER1_ID, spogServer.GetLoggedinUser_UserID());
		writeToFile(NORMAL_MSP_ORG1_MONITOR1_USER1_EMAIL, prefix + "normal_msp_1_monitor_1@arcserve.com");
		writeToFile(NORMAL_MSP_ORG1_MONITOR1_USER1_ID,user_id1);

		// create another user in msp organization 
		user_id = spogServer.createUserAndCheck(prefix + "normal_msp_1_user_2@arcserve.com", common_password, "first_name", "last_name", SpogConstants.MSP_ADMIN, normal_msp_org_1, test);
		writeToFile(NORMAL_MSP_ORG1_USER2_EMAIL, prefix + "normal_msp_1_user_2@arcserve.com");
		writeToFile(NORMAL_MSP_ORG1_USER2_ID, user_id);

		// create 2 account admins in msp organization 
		String normal_msp_org_1_account_admin_1 = spogServer.createUserAndCheck(prefix + "normal_msp_1_account_admin_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.MSP_ACCOUNT_ADMIN, normal_msp_org_1, test);
		writeToFile(NORMAL_MSP_ORG1_MSP_ACCOUNTADMIN1_EMAIL, prefix + "normal_msp_1_account_admin_1@arcserve.com");
		writeToFile(NORMAL_MSP_ORG1_MSP_ACCOUNTADMIN1_ID, normal_msp_org_1_account_admin_1);

		String normal_msp_org_1_account_admin_2 = spogServer.createUserAndCheck(prefix + "normal_msp_1_account_admin_2@arcserve.com", common_password, "first_name", "last_name", SpogConstants.MSP_ACCOUNT_ADMIN, normal_msp_org_1, test);
		writeToFile(NORMAL_MSP_ORG1_MSP_ACCOUNTADMIN2_EMAIL, prefix + "normal_msp_1_account_admin_2@arcserve.com");
		writeToFile(NORMAL_MSP_ORG1_MSP_ACCOUNTADMIN2_ID, normal_msp_org_1_account_admin_2);


		
		// create normal msp organization #2
		spogServer.setToken(csrToken);
		String normal_msp_org_2_name = orgPrefix + "normal_msp_2";
		String normal_msp_org_2 = spogServer.CreateOrganizationByEnrollWithCheck(normal_msp_org_2_name, SpogConstants.MSP_ORG, prefix + "normal_msp_2_user_1@arcserve.com", common_password, "firstName", "lastname");
//		spogServer.postCloudhybridInFreeTrial(csrToken, normal_msp_org_2, SpogConstants.MSP_ORG, false, false);
		user_id1 = spogServer.createUserAndCheck(prefix + "normal_msp_2_monitor_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.MSP_MONITOR, normal_msp_org_2, test);
		spogServer.userLogin(prefix + "normal_msp_2_user_1@arcserve.com", common_password);

		writeToFile(NORMAL_MSP_ORG2_ID, normal_msp_org_2);
		writeToFile(NORMAL_MSP_ORG2_NAME, normal_msp_org_2_name);
		writeToFile(NORMAL_MSP_ORG2_USER1_EMAIL, prefix + "normal_msp_2_user_1@arcserve.com");
		writeToFile(NORMAL_MSP_ORG2_USER1_ID, spogServer.GetLoggedinUser_UserID());
		writeToFile(NORMAL_MSP_ORG2_MONITOR1_USER1_EMAIL, prefix + "normal_msp_2_monitor_1@arcserve.com");
		writeToFile(NORMAL_MSP_ORG2_USER1_ID,user_id1);

		// create another user in msp organization #2
		user_id = spogServer.createUserAndCheck(prefix + "normal_msp_2_user_2@arcserve.com", common_password, "first_name", "last_name", SpogConstants.MSP_ADMIN, normal_msp_org_2, test);
		writeToFile(NORMAL_MSP_ORG2_USER2_EMAIL, prefix + "normal_msp_2_user_2@arcserve.com");
		writeToFile(NORMAL_MSP_ORG2_USER2_ID, user_id);

		// create 2 account admins in msp organization #2
		String normal_msp_org_2_account_admin_1 = spogServer.createUserAndCheck(prefix + "normal_msp_2_account_admin_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.MSP_ACCOUNT_ADMIN, normal_msp_org_2, test);
		writeToFile(NORMAL_MSP_ORG2_MSP_ACCOUNTADMIN1_EMAIL, prefix + "normal_msp_2_account_admin_1@arcserve.com");
		writeToFile(NORMAL_MSP_ORG2_MSP_ACCOUNTADMIN1_ID, normal_msp_org_2_account_admin_1);

		String normal_msp_org_2_account_admin_2 = spogServer.createUserAndCheck(prefix + "normal_msp_2_account_admin_2@arcserve.com", common_password, "first_name", "last_name", SpogConstants.MSP_ACCOUNT_ADMIN, normal_msp_org_2, test);
		writeToFile(NORMAL_MSP_ORG2_MSP_ACCOUNTADMIN2_EMAIL, prefix + "normal_msp_2_account_admin_2@arcserve.com");
		writeToFile(NORMAL_MSP_ORG2_MSP_ACCOUNTADMIN2_ID, normal_msp_org_2_account_admin_2);

		/*********************************************** MSP orgs end ************************************************************/
		
		
		
		/******************************************** SUB Orgs *****************************************************/
		// create sub organizations #ROOT_MSP1 and 2 users in the sub organization;
		spogServer.setToken(csrToken);
		String root_msp_org_1_sub_1_name = orgPrefix + "root_msp_1_sub_1";
		String root_msp_org_1_sub_1 = spogServer.createAccountWithCheck(root_msp_org_1, root_msp_org_1_sub_1_name, root_msp_org_1);
		writeToFile(ROOT_MSP1_SUBORG1_ID, root_msp_org_1_sub_1);	
		writeToFile(ROOT_MSP1_SUBORG1_NAME, root_msp_org_1_sub_1_name);
		
		user_id1 = spogServer.createUserAndCheck(prefix + "root_msp_1_sub_1_monitor_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.DIRECT_MONITOR, root_msp_org_1_sub_1, test);
		writeToFile(ROOT_MSP1_SUBORG1_MONITOR1_USER1_EMAIL, prefix + "root_msp_1_sub_1_monitor_1@arcserve.com");
		writeToFile(ROOT_MSP1_SUBORG1_MONITOR1_USER1_ID, user_id1);
		
		user_id = spogServer.createUserAndCheck(prefix + "root_msp_1_sub_1_user_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.DIRECT_ADMIN, root_msp_org_1_sub_1, test);
		writeToFile(ROOT_MSP1_SUBORG1_USER1_EMAIL, prefix + "root_msp_1_sub_1_user_1@arcserve.com");
		writeToFile(ROOT_MSP1_SUBORG1_USER1_ID, user_id);
		
		user_id = spogServer.createUserAndCheck(prefix + "root_msp_1_sub_1_user_2@arcserve.com", common_password, "first_name", "last_name", SpogConstants.DIRECT_ADMIN, root_msp_org_1_sub_1, test);
		writeToFile(ROOT_MSP1_SUBORG1_USER2_EMAIL, prefix + "root_msp_1_sub_1_user_2@arcserve.com");
		writeToFile(ROOT_MSP1_SUBORG1_USER2_ID, user_id);
		
		// create sub organization #2 and 1 user in the sub organization;
		spogServer.setToken(csrToken);
		String root_msp_org_1_sub_2_name = orgPrefix + "root_msp_1_sub_2";
		String root_msp_org_1_sub_2 = spogServer.createAccountWithCheck(root_msp_org_1, root_msp_org_1_sub_2_name, root_msp_org_1);
		writeToFile(ROOT_MSP1_SUBORG2_ID, root_msp_org_1_sub_2);
		writeToFile(ROOT_MSP1_SUBORG2_NAME, root_msp_org_1_sub_2_name);
		
		user_id1 = spogServer.createUserAndCheck(prefix + "root_msp_1_sub_2_monitor_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.DIRECT_MONITOR, root_msp_org_1_sub_2, test);
		writeToFile(ROOT_MSP1_SUBORG2_MONITOR1_USER1_EMAIL, prefix + "root_msp_1_sub_2_monitor_1@arcserve.com");
		writeToFile(ROOT_MSP1_SUBORG2_MONITOR1_USER1_ID, user_id1);
		
		user_id = spogServer.createUserAndCheck(prefix + "root_msp_1_sub_2_user_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.DIRECT_ADMIN, root_msp_org_1_sub_2, test);
		writeToFile(ROOT_MSP1_SUBORG2_USER1_EMAIL, prefix + "root_msp_1_sub_2_user_1@arcserve.com");
		writeToFile(ROOT_MSP1_SUBORG2_USER1_ID, user_id);

		// assign sub organization #1 to msp account admin #1
		String[] userIds = new String[] {root_msp_org_1_account_admin_1}; 
		userSpogServer.assignMspAccountAdmins(root_msp_org_1, root_msp_org_1_sub_1, userIds, spogServer.getJWTToken());
		userIds = new String[] {root_msp_org_1_account_admin_2};
		userSpogServer.assignMspAccountAdmins(root_msp_org_1, root_msp_org_1_sub_2, userIds, spogServer.getJWTToken());
		

		
		// create sub organizations #ROOT_MSP2 and 2 users in the sub organization;
		spogServer.setToken(csrToken);
		String root_msp_org_2_sub_1_name = orgPrefix + "root_msp_2_sub_1";
		String root_msp_org_2_sub_1 = spogServer.createAccountWithCheck(root_msp_org_2, root_msp_org_2_sub_1_name, root_msp_org_2);
		writeToFile(ROOT_MSP2_SUBORG1_ID, root_msp_org_2_sub_1);		
		writeToFile(ROOT_MSP2_SUBORG1_NAME, root_msp_org_2_sub_1_name);		
		
		user_id1 = spogServer.createUserAndCheck(prefix + "root_msp_2_sub_1_monitor_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.DIRECT_MONITOR, root_msp_org_2_sub_1, test);
		writeToFile(ROOT_MSP2_SUBORG1_MONITOR1_USER1_EMAIL, prefix + "root_msp_2_sub_1_monitor_1@arcserve.com");
		writeToFile(ROOT_MSP2_SUBORG1_MONITOR1_USER1_ID, user_id1);
		
		user_id = spogServer.createUserAndCheck(prefix + "root_msp_2_sub_1_user_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.DIRECT_ADMIN, root_msp_org_2_sub_1, test);
		writeToFile(ROOT_MSP2_SUBORG1_USER1_EMAIL, prefix + "root_msp_2_sub_1_user_1@arcserve.com");
		writeToFile(ROOT_MSP2_SUBORG1_USER1_ID, user_id);

		user_id = spogServer.createUserAndCheck(prefix + "root_msp_2_sub_1_user_2@arcserve.com", common_password, "first_name", "last_name", SpogConstants.DIRECT_ADMIN, root_msp_org_2_sub_1, test);
		writeToFile(ROOT_MSP2_SUBORG1_USER2_EMAIL, prefix + "root_msp_2_sub_1_user_2@arcserve.com");
		writeToFile(ROOT_MSP2_SUBORG1_USER2_ID, user_id);

		// create sub organization #2 and 1 user in the sub organization;
		spogServer.setToken(csrToken);
		String root_msp_org_2_sub_2_name = orgPrefix + "root_msp_2_sub_2";
		String root_msp_org_2_sub_2 = spogServer.createAccountWithCheck(root_msp_org_2, root_msp_org_2_sub_2_name, root_msp_org_2);
		writeToFile(ROOT_MSP2_SUBORG2_ID, root_msp_org_2_sub_2);
		writeToFile(ROOT_MSP2_SUBORG2_NAME, root_msp_org_2_sub_2_name);
		
		user_id1 = spogServer.createUserAndCheck(prefix + "root_msp_2_sub_2_monitor_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.DIRECT_MONITOR, root_msp_org_2_sub_2, test);
		writeToFile(ROOT_MSP2_SUBORG2_MONITOR1_USER1_EMAIL, prefix + "root_msp_2_sub_2_monitor_1@arcserve.com");
		writeToFile(ROOT_MSP2_SUBORG2_MONITOR1_USER1_ID, user_id1);

		user_id = spogServer.createUserAndCheck(prefix + "root_msp_2_sub_2_user_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.DIRECT_ADMIN, root_msp_org_2_sub_2, test);
		writeToFile(ROOT_MSP2_SUBORG2_USER1_EMAIL, prefix + "root_msp_2_sub_2_user_1@arcserve.com");
		writeToFile(ROOT_MSP2_SUBORG2_USER1_ID, user_id);

		// assign sub organization #1 to msp account admin #1
		userIds = new String[] {root_msp_org_2_account_admin_1}; 
		userSpogServer.setToken(csrToken);
		userSpogServer.assignMspAccountAdmins(root_msp_org_2, root_msp_org_2_sub_1, userIds, spogServer.getJWTToken());
		userIds = new String[] {root_msp_org_2_account_admin_2};
		userSpogServer.assignMspAccountAdmins(root_msp_org_2, root_msp_org_2_sub_2, userIds, spogServer.getJWTToken());



		// create sub organizations #Normal MSP1 and 2 users in the sub organization;
		spogServer.setToken(csrToken);
		String normal_msp_org_1_sub_1_name = orgPrefix + "normal_msp1_sub_1";
		String normal_msp_org_1_sub_1 = spogServer.createAccountWithCheck(normal_msp_org_1, normal_msp_org_1_sub_1_name, normal_msp_org_1);
		writeToFile(NORMAL_MSP1_SUBORG1_ID, normal_msp_org_1_sub_1);		
		writeToFile(NORMAL_MSP1_SUBORG1_NAME, normal_msp_org_1_sub_1_name);	
		
		user_id1 = spogServer.createUserAndCheck(prefix + "normal_msp_org_1_sub_1_monitor_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.DIRECT_MONITOR, normal_msp_org_1_sub_1, test);
		writeToFile(NORMAL_MSP1_SUBORG1_MONITOR1_USER1_EMAIL, prefix + "normal_msp_org_1_sub_1_monitor_1@arcserve.com");
		writeToFile(NORMAL_MSP1_SUBORG1_MONITOR1_USER1_ID, user_id1);
		
		user_id = spogServer.createUserAndCheck(prefix + "normal_msp1_sub_1_user_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.DIRECT_ADMIN, normal_msp_org_1_sub_1, test);
		writeToFile(NORMAL_MSP1_SUBORG1_USER1_EMAIL, prefix + "normal_msp1_sub_1_user_1@arcserve.com");
		writeToFile(NORMAL_MSP1_SUBORG1_USER1_ID, user_id);

		user_id = spogServer.createUserAndCheck(prefix + "normal_msp1_sub_1_user_2@arcserve.com", common_password, "first_name", "last_name", SpogConstants.DIRECT_ADMIN, normal_msp_org_1_sub_1, test);
		writeToFile(NORMAL_MSP1_SUBORG1_USER2_EMAIL, prefix + "normal_msp1_sub_1_user_2@arcserve.com");
		writeToFile(NORMAL_MSP1_SUBORG1_USER2_ID, user_id);

		// create sub organization #2 and 1 user in the sub organization;
		spogServer.setToken(csrToken);
		String normal_msp_org_1_sub_2_name = orgPrefix + "normal_msp1_sub_2";
		String normal_msp_org_1_sub_2 = spogServer.createAccountWithCheck(normal_msp_org_1, normal_msp_org_1_sub_2_name, normal_msp_org_1);
		writeToFile(NORMAL_MSP1_SUBORG2_ID, normal_msp_org_1_sub_2);
		writeToFile(NORMAL_MSP1_SUBORG2_NAME, normal_msp_org_1_sub_2_name);
		
		user_id1 = spogServer.createUserAndCheck(prefix + "normal_msp_org_1_sub_2_monitor_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.DIRECT_MONITOR, normal_msp_org_1_sub_2, test);
		writeToFile(NORMAL_MSP1_SUBORG2_MONITOR1_USER1_EMAIL, prefix + "normal_msp_org_1_sub_2_monitor_1@arcserve.com");
		writeToFile(NORMAL_MSP1_SUBORG2_MONITOR1_USER1_ID, user_id1);

		user_id = spogServer.createUserAndCheck(prefix + "normal_msp1_sub_2_user_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.DIRECT_ADMIN, normal_msp_org_1_sub_2, test);
		writeToFile(NORMAL_MSP1_SUBORG2_USER1_EMAIL, prefix + "normal_msp1_sub_2_user_1@arcserve.com");
		writeToFile(NORMAL_MSP1_SUBORG2_USER1_ID, user_id);

		// assign sub organization #1 to msp account admin #1
		userIds = new String[] {normal_msp_org_1_account_admin_1}; 
		userSpogServer.setToken(csrToken);
		userSpogServer.assignMspAccountAdmins(normal_msp_org_1, normal_msp_org_1_sub_1, userIds, spogServer.getJWTToken());
		userIds = new String[] {normal_msp_org_1_account_admin_2};
		userSpogServer.assignMspAccountAdmins(normal_msp_org_1, normal_msp_org_1_sub_2, userIds, spogServer.getJWTToken());



		// create sub organizations #Normal MSP2 and 2 users in the sub organization;
		spogServer.setToken(csrToken);
		String normal_msp_org_2_sub_1_name = orgPrefix + "msp_4_sub_1";
		String normal_msp_org_2_sub_1 = spogServer.createAccountWithCheck(normal_msp_org_2, normal_msp_org_2_sub_1_name, normal_msp_org_2);
		writeToFile(NORMAL_MSP2_SUBORG1_ID, normal_msp_org_2_sub_1);		
		writeToFile(NORMAL_MSP2_SUBORG1_NAME, normal_msp_org_2_sub_1_name);	
		
		user_id1 = spogServer.createUserAndCheck(prefix + "normal_msp_org_2_sub_1_monitor_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.DIRECT_MONITOR, normal_msp_org_2_sub_1, test);
		writeToFile(NORMAL_MSP2_SUBORG1_MONITOR1_USER1_EMAIL, prefix + "normal_msp_org_2_sub_1_monitor_1@arcserve.com");
		writeToFile(NORMAL_MSP2_SUBORG1_MONITOR1_USER1_ID, user_id1);

		user_id = spogServer.createUserAndCheck(prefix + "msp_4_sub_1_user_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.DIRECT_ADMIN, normal_msp_org_2_sub_1, test);
		writeToFile(NORMAL_MSP2_SUBORG1_USER1_EMAIL, prefix + "msp_4_sub_1_user_1@arcserve.com");
		writeToFile(NORMAL_MSP2_SUBORG1_USER1_ID, user_id);

		user_id = spogServer.createUserAndCheck(prefix + "msp_4_sub_1_user_2@arcserve.com", common_password, "first_name", "last_name", SpogConstants.DIRECT_ADMIN, normal_msp_org_2_sub_1, test);
		writeToFile(NORMAL_MSP2_SUBORG1_USER2_EMAIL, prefix + "msp_4_sub_1_user_2@arcserve.com");
		writeToFile(NORMAL_MSP2_SUBORG1_USER2_ID, user_id);

		// create sub organization #2 and 1 user in the sub organization;
		spogServer.setToken(csrToken);
		String normal_msp_org_2_sub_2_name = orgPrefix + "msp_4_sub_2";
		String normal_msp_org_2_sub_2 = spogServer.createAccountWithCheck(normal_msp_org_2, normal_msp_org_2_sub_2_name, normal_msp_org_2);
		writeToFile(NORMAL_MSP2_SUBORG2_ID, normal_msp_org_2_sub_2);
		writeToFile(NORMAL_MSP2_SUBORG2_NAME, normal_msp_org_2_sub_2_name);

		user_id1 = spogServer.createUserAndCheck(prefix + "normal_msp_org_2_sub_2_monitor_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.DIRECT_MONITOR, normal_msp_org_2_sub_2, test);
		writeToFile(NORMAL_MSP2_SUBORG2_MONITOR1_USER1_EMAIL, prefix + "normal_msp_org_2_sub_2_monitor_1@arcserve.com");
		writeToFile(NORMAL_MSP2_SUBORG2_MONITOR1_USER1_ID, user_id1);
		
		user_id = spogServer.createUserAndCheck(prefix + "msp_4_sub_2_user_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.DIRECT_ADMIN, normal_msp_org_2_sub_2, test);
		writeToFile(NORMAL_MSP2_SUBORG2_USER1_EMAIL, prefix + "msp_4_sub_2_user_1@arcserve.com");
		writeToFile(NORMAL_MSP2_SUBORG2_USER1_ID, user_id);

		// assign sub organization #1 to msp account admin #1
		userIds = new String[] {normal_msp_org_2_account_admin_1}; 
		userSpogServer.setToken(csrToken);
		userSpogServer.assignMspAccountAdmins(normal_msp_org_2, normal_msp_org_2_sub_1, userIds, spogServer.getJWTToken());
		userIds = new String[] {normal_msp_org_2_account_admin_2};
		userSpogServer.assignMspAccountAdmins(normal_msp_org_2, normal_msp_org_2_sub_2, userIds, spogServer.getJWTToken());
		
		/*********************************************** sub orgs end ***************************************************/
		
		
		/******************************************************** sub msps *************************************************/			
		// create sub msp organization #1 under msp 1
		spogServer.setToken(csrToken);
		String msp_org1_submsp_org1_name = orgPrefix + "msp1_submsp_org1";
		HashMap<String, Object> requestInfo = jp.composeSubMspRqstInfo(msp_org1_submsp_org1_name, root_msp_org_1,
				"first_name", "last_name", prefix + "msp1_submsp1_user1@arcserve.com", datacenters[0]);
		String msp_org1_submsp_org1_id = spogServer.createSubMspWithCheck(csrToken, root_msp_org_1, requestInfo,
				SpogConstants.SUCCESS_POST, null, test);
		writeToFile(ROOT_MSP1_SUBMSP_ORG1_ID, msp_org1_submsp_org1_id);
		writeToFile(ROOT_MSP1_SUBMSP_ORG1_NAME, msp_org1_submsp_org1_name);
	
		user_id1 = spogServer.createUserAndCheck(prefix + "msp_org1_submsp_org1_monitor_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.MSP_MONITOR, msp_org1_submsp_org1_id, test);
		writeToFile(ROOT_MSP1_SUBMSP_ORG1_MONITOR1_USER1_EMAIL, prefix + "msp_org1_submsp_org1_monitor_1@arcserve.com");
		writeToFile(ROOT_MSP1_SUBMSP_ORG1_MONITOR1_USER1_ID, user_id1);
		
		// create users under the sub msp organization #1
		user_id = spogServer.createUserAndCheck(prefix + "root_msp_1_submsp_1_user_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.MSP_ADMIN, msp_org1_submsp_org1_id, test);
		writeToFile(ROOT_MSP1_SUBMSP_ORG1_USER1_EMAIL, prefix + "root_msp_1_submsp_1_user_1@arcserve.com");
		writeToFile(ROOT_MSP1_SUBMSP_ORG1_USER1_ID, user_id);
		
		user_id = spogServer.createUserAndCheck(prefix + "root_msp_1_submsp_1_user_2@arcserve.com", common_password, "first_name", "last_name", SpogConstants.MSP_ADMIN, msp_org1_submsp_org1_id, test);
		writeToFile(ROOT_MSP1_SUBMSP_ORG1_USER2_EMAIL, prefix + "root_msp_1_submsp_1_user_2@arcserve.com");
		writeToFile(ROOT_MSP1_SUBMSP_ORG1_USER2_ID, user_id);
		
		// create account admin under the sub msp org #1
		String msp1_submsp1_account_admin_1_id = spogServer.createUserAndCheck(prefix + "msp1_submsp1_account_admin_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.MSP_ACCOUNT_ADMIN, msp_org1_submsp_org1_id, test);
		writeToFile(ROOT_MSP1_SUBMSP_ORG1_ACCOUNT_ADMIN_EMAIL, prefix + "msp1_submsp1_account_admin_1@arcserve.com");
		writeToFile(ROOT_MSP1_SUBMSP_ORG1_ACCOUNT_ADMIN_ID, msp1_submsp1_account_admin_1_id);



		// create sub msp organization #2 under msp1
		spogServer.setToken(csrToken);
		String msp_org1_submsp_org2_name = orgPrefix+"msp1_submsp_org2";
		requestInfo = jp.composeSubMspRqstInfo(msp_org1_submsp_org2_name, root_msp_org_1, "first_name", "last_name", prefix+"msp1_submsp2_user1@arcserve.com", datacenters[0]);
		String msp_org1_submsp_org2_id = spogServer.createSubMspWithCheck(csrToken, root_msp_org_1, requestInfo, SpogConstants.SUCCESS_POST, null, test);
		writeToFile(ROOT_MSP1_SUBMSP_ORG2_ID, msp_org1_submsp_org2_id);
		writeToFile(ROOT_MSP1_SUBMSP_ORG2_NAME, msp_org1_submsp_org2_name);

		user_id1 = spogServer.createUserAndCheck(prefix + "msp_org1_submsp_org2_monitor_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.MSP_MONITOR, msp_org1_submsp_org2_id, test);
		writeToFile(ROOT_MSP1_SUBMSP_ORG2_MONITOR1_USER1_EMAIL, prefix + "msp_org1_submsp_org2_monitor_1@arcserve.com");
		writeToFile(ROOT_MSP1_SUBMSP_ORG2_MONITOR1_USER1_ID, user_id1);
		
		// create users under the sub msp organization #1
		user_id = spogServer.createUserAndCheck(prefix + "root_msp_1_submsp_2_user_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.MSP_ADMIN, msp_org1_submsp_org2_id, test);
		writeToFile(ROOT_MSP1_SUBMSP_ORG2_USER1_EMAIL, prefix + "root_msp_1_submsp_2_user_1@arcserve.com");
		writeToFile(ROOT_MSP1_SUBMSP_ORG2_USER1_ID, user_id);

		user_id = spogServer.createUserAndCheck(prefix + "root_msp_1_submsp_2_user_2@arcserve.com", common_password, "first_name", "last_name", SpogConstants.MSP_ADMIN, msp_org1_submsp_org2_id, test);
		writeToFile(ROOT_MSP1_SUBMSP_ORG2_USER2_EMAIL, prefix + "root_msp_1_submsp_2_user_2@arcserve.com");
		writeToFile(ROOT_MSP1_SUBMSP_ORG2_USER2_ID, user_id);

		// create account admin under the sub msp org #1
		String msp1_submsp2_account_admin_1_id = spogServer.createUserAndCheck(prefix + "msp1_submsp2_account_admin_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.MSP_ACCOUNT_ADMIN, msp_org1_submsp_org2_id, test);
		writeToFile(ROOT_MSP1_SUBMSP_ORG2_ACCOUNT_ADMIN_EMAIL, prefix + "msp1_submsp2_account_admin_1@arcserve.com");
		writeToFile(ROOT_MSP1_SUBMSP_ORG2_ACCOUNT_ADMIN_ID, msp1_submsp2_account_admin_1_id);





		// create sub msp organization #1 under msp2
		spogServer.setToken(csrToken);
		String msp_org2_submsp_org1_name = orgPrefix+"msp2_submsp_org1";
		requestInfo = jp.composeSubMspRqstInfo(msp_org2_submsp_org1_name, root_msp_org_2, "first_name", "last_name", prefix+"msp2_submsp1_user1@arcserve.com", datacenters[0]);
		String msp_org2_submsp_org1_id = spogServer.createSubMspWithCheck(csrToken, root_msp_org_2, requestInfo, SpogConstants.SUCCESS_POST, null, test);
		writeToFile(ROOT_MSP2_SUBMSP_ORG1_ID, msp_org2_submsp_org1_id);
		writeToFile(ROOT_MSP2_SUBMSP_ORG1_NAME, msp_org2_submsp_org1_name);

		user_id1 = spogServer.createUserAndCheck(prefix + "msp_org2_submsp_org1_monitor_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.MSP_MONITOR, msp_org2_submsp_org1_id, test);
		writeToFile(ROOT_MSP2_SUBMSP_ORG1_MONITOR1_USER1_EMAIL, prefix + "msp_org2_submsp_org1_monitor_1@arcserve.com");
		writeToFile(ROOT_MSP2_SUBMSP_ORG1_MONITOR1_USER1_ID, user_id1);

		// create users under the sub msp organization #1
		user_id = spogServer.createUserAndCheck(prefix + "root_msp_2_submsp_1_user_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.MSP_ADMIN, msp_org2_submsp_org1_id, test);
		writeToFile(ROOT_MSP2_SUBMSP_ORG1_USER1_EMAIL, prefix + "root_msp_2_submsp_1_user_1@arcserve.com");
		writeToFile(ROOT_MSP2_SUBMSP_ORG1_USER1_ID, user_id);

		user_id = spogServer.createUserAndCheck(prefix + "root_msp_2_submsp_1_user_2@arcserve.com", common_password, "first_name", "last_name", SpogConstants.MSP_ADMIN, msp_org2_submsp_org1_id, test);
		writeToFile(ROOT_MSP2_SUBMSP_ORG1_USER2_EMAIL, prefix + "root_msp_2_submsp_1_user_2@arcserve.com");
		writeToFile(ROOT_MSP2_SUBMSP_ORG1_USER2_ID, user_id);

		// create account admin under the sub msp org #1
		String msp2_submsp1_account_admin_1_id = spogServer.createUserAndCheck(prefix + "msp2_submsp1_account_admin_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.MSP_ACCOUNT_ADMIN, msp_org2_submsp_org1_id, test);
		writeToFile(ROOT_MSP2_SUBMSP_ORG1_ACCOUNT_ADMIN_EMAIL, prefix + "msp2_submsp1_account_admin_1@arcserve.com");
		writeToFile(ROOT_MSP2_SUBMSP_ORG1_ACCOUNT_ADMIN_ID, msp2_submsp1_account_admin_1_id);



		// create sub msp organization #2 under msp2
		spogServer.setToken(csrToken);
		String msp_org2_submsp_org2_name = orgPrefix+"msp2_submsp_org2";
		requestInfo = jp.composeSubMspRqstInfo(msp_org2_submsp_org2_name, root_msp_org_2, "first_name", "last_name", prefix+"msp2_submsp2_user1@arcserve.com", datacenters[0]);
		String msp_org2_submsp_org2_id = spogServer.createSubMspWithCheck(csrToken, root_msp_org_2, requestInfo, SpogConstants.SUCCESS_POST, null, test);
		writeToFile(ROOT_MSP2_SUBMSP_ORG2_ID, msp_org2_submsp_org2_id);
		writeToFile(ROOT_MSP2_SUBMSP_ORG2_NAME, msp_org2_submsp_org2_name);
		
		user_id1 = spogServer.createUserAndCheck(prefix + "msp_org2_submsp_org2_monitor_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.MSP_MONITOR, msp_org2_submsp_org2_id, test);
		writeToFile(ROOT_MSP2_SUBMSP_ORG2_MONITOR1_USER1_EMAIL, prefix + "msp_org2_submsp_org2_monitor_1@arcserve.com");
		writeToFile(ROOT_MSP2_SUBMSP_ORG2_MONITOR1_USER1_ID, user_id1);

		// create users under the sub msp organization #1
		user_id = spogServer.createUserAndCheck(prefix + "root_msp_2_submsp_2_user_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.MSP_ADMIN, msp_org2_submsp_org2_id, test);
		writeToFile(ROOT_MSP2_SUBMSP_ORG2_USER1_EMAIL, prefix + "root_msp_2_submsp_2_user_1@arcserve.com");
		writeToFile(ROOT_MSP2_SUBMSP_ORG2_USER1_ID, user_id);

		user_id = spogServer.createUserAndCheck(prefix + "root_msp_2_submsp_2_user_2@arcserve.com", common_password, "first_name", "last_name", SpogConstants.MSP_ADMIN, msp_org2_submsp_org2_id, test);
		writeToFile(ROOT_MSP2_SUBMSP_ORG2_USER2_EMAIL, prefix + "root_msp_2_submsp_2_user_2@arcserve.com");
		writeToFile(ROOT_MSP2_SUBMSP_ORG2_USER2_ID, user_id);

		// create account admin under the sub msp org #1
		String msp2_submsp2_account_admin_1_id = spogServer.createUserAndCheck(prefix + "msp2_submsp2_account_admin_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.MSP_ACCOUNT_ADMIN, msp_org2_submsp_org2_id, test);
		writeToFile(ROOT_MSP2_SUBMSP_ORG2_ACCOUNT_ADMIN_EMAIL, prefix + "msp2_submsp2_account_admin_1@arcserve.com");
		writeToFile(ROOT_MSP2_SUBMSP_ORG2_ACCOUNT_ADMIN_ID, msp2_submsp2_account_admin_1_id);
		
		/**************************************** sub msps end *********************************************/
		
		
		/****************************************** sub orgs under sub msp ********************************************/		
		// create sub org1 under the sub msp #1 of msp1
		spogServer.setToken(csrToken);
		String msp1_submsp1_sub_1_name = orgPrefix + "msp1_submsp1_sub_1";
		String msp1_submsp1_sub_1 = spogServer.createAccountWithCheck(msp_org1_submsp_org1_id, msp1_submsp1_sub_1_name, msp_org1_submsp_org1_id);
		writeToFile(MSP1_SUBMSP1_SUB_ORG1_ID, msp1_submsp1_sub_1);
		writeToFile(MSP1_SUBMSP1_SUB_ORG1_NAME, msp1_submsp1_sub_1_name);
		
		user_id1 = spogServer.createUserAndCheck(prefix + "msp1_submsp1_sub_1_monitor_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.DIRECT_MONITOR, msp1_submsp1_sub_1, test);
		writeToFile(MSP1_SUBMSP1_SUBORG1_MONITOR_USER1_EMAIL, prefix + "msp1_submsp1_sub_1_monitor_1@arcserve.com");
		writeToFile(MSP1_SUBMSP1_SUBORG1_MONITOR_USER1_ID, user_id1);
		
		// create users under the sub org1 of sub msp #1
		user_id = spogServer.createUserAndCheck(prefix + "msp1_submsp1_sub1_user_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.DIRECT_ADMIN, msp1_submsp1_sub_1, test);
		writeToFile(MSP1_SUBMSP1_SUBORG1_USER1_EMAIL, prefix + "msp1_submsp1_sub1_user_1@arcserve.com");
		writeToFile(MSP1_SUBMSP1_SUBORG1_USER1_ID, user_id );
		
		user_id = spogServer.createUserAndCheck(prefix + "msp1_submsp1_sub1_user_2@arcserve.com", common_password, "first_name", "last_name", SpogConstants.DIRECT_ADMIN, msp1_submsp1_sub_1, test);
		writeToFile(MSP1_SUBMSP1_SUBORG1_USER2_EMAIL, prefix + "msp1_submsp1_sub1_user_2@arcserve.com");
		writeToFile(MSP1_SUBMSP1_SUBORG1_USER2_ID , user_id);

		// create sub org2 under the sub msp #1
		spogServer.setToken(csrToken);
		String msp1_submsp1_sub_2_name = orgPrefix + "msp1_submsp1_sub_2";
		String msp1_submsp1_sub_2 = spogServer.createAccountWithCheck(msp_org1_submsp_org1_id, msp1_submsp1_sub_2_name, msp_org1_submsp_org1_id);
		writeToFile(MSP1_SUBMSP1_SUB_ORG2_ID, msp1_submsp1_sub_2);
		writeToFile(MSP1_SUBMSP1_SUB_ORG2_NAME, msp1_submsp1_sub_2_name);
		
		user_id1 = spogServer.createUserAndCheck(prefix + "msp1_submsp1_sub_2_monitor_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.DIRECT_MONITOR, msp1_submsp1_sub_2, test);
		writeToFile(MSP1_SUBMSP1_SUBORG2_MONITOR_USER1_EMAIL, prefix + "msp1_submsp1_sub_2_monitor_1@arcserve.com");
		writeToFile(MSP1_SUBMSP1_SUBORG2_MONITOR_USER1_ID, user_id1);
		
		user_id = spogServer.createUserAndCheck(prefix + "msp1_submsp1_sub2_user_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.DIRECT_ADMIN, msp1_submsp1_sub_2, test);
		writeToFile(MSP1_SUBMSP1_SUBORG2_USER1_EMAIL, prefix + "msp1_submsp1_sub2_user_1@arcserve.com");
		writeToFile(MSP1_SUBMSP1_SUBORG2_USER1_ID, user_id);
		
		// assign sub organization #1 to msp account admin #1 of sub msp organizaiton 1
		userIds = new String[] {msp1_submsp1_account_admin_1_id}; 
		userSpogServer.assignMspAccountAdmins(msp_org1_submsp_org1_id, msp1_submsp1_sub_1, userIds, spogServer.getJWTToken());
		
		
		// create sub org1 under the sub msp #2 of msp1
		spogServer.setToken(csrToken);
		String msp1_submsp2_sub_1_name = orgPrefix + "msp1_submsp2_sub_1";
		String msp1_submsp2_sub_1 = spogServer.createAccountWithCheck(msp_org1_submsp_org2_id, msp1_submsp2_sub_1_name, msp_org1_submsp_org2_id);
		writeToFile(MSP1_SUBMSP2_SUB_ORG1_ID, msp1_submsp2_sub_1);
		writeToFile(MSP1_SUBMSP2_SUB_ORG1_NAME, msp1_submsp2_sub_1_name);
		
		user_id1 = spogServer.createUserAndCheck(prefix + "msp1_submsp2_sub_1_monitor_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.DIRECT_MONITOR, msp1_submsp2_sub_1, test);
		writeToFile(MSP1_SUBMSP2_SUBORG1_MONITOR_USER1_EMAIL, prefix + "msp1_submsp2_sub_1_monitor_1@arcserve.com");
		writeToFile(MSP1_SUBMSP2_SUBORG1_MONITOR_USER1_ID, user_id1);
		
		// create users under the sub org1 of sub msp #2
		user_id = spogServer.createUserAndCheck(prefix + "msp1_submsp2_sub1_user_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.DIRECT_ADMIN, msp1_submsp2_sub_1, test);
		writeToFile(MSP1_SUBMSP2_SUBORG1_USER1_EMAIL, prefix + "msp1_submsp2_sub1_user_1@arcserve.com");
		writeToFile(MSP1_SUBMSP2_SUBORG1_USER1_ID, user_id );
		
		user_id = spogServer.createUserAndCheck(prefix + "msp1_submsp2_sub1_user_2@arcserve.com", common_password, "first_name", "last_name", SpogConstants.DIRECT_ADMIN, msp1_submsp2_sub_1, test);
		writeToFile(MSP1_SUBMSP2_SUBORG1_USER2_EMAIL, prefix + "msp1_submsp2_sub1_user_2@arcserve.com");
		writeToFile(MSP1_SUBMSP2_SUBORG1_USER2_ID , user_id);

		// create sub org2 under the sub msp #1
		spogServer.setToken(csrToken);
		String msp1_submsp2_sub_2_name = orgPrefix + "msp1_submsp2_sub_2";
		String msp1_submsp2_sub_2 = spogServer.createAccountWithCheck(msp_org1_submsp_org2_id, msp1_submsp2_sub_2_name, msp_org1_submsp_org2_id);
		writeToFile(MSP1_SUBMSP2_SUB_ORG2_ID, msp1_submsp2_sub_2);
		writeToFile(MSP1_SUBMSP2_SUB_ORG2_NAME, msp1_submsp2_sub_2_name);
		
		user_id1 = spogServer.createUserAndCheck(prefix + "msp1_submsp2_sub_2_monitor_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.DIRECT_MONITOR, msp1_submsp2_sub_2, test);
		writeToFile(MSP1_SUBMSP2_SUBORG2_MONITOR_USER1_EMAIL, prefix + "msp1_submsp2_sub_2_monitor_1@arcserve.com");
		writeToFile(MSP1_SUBMSP2_SUBORG2_MONITOR_USER1_ID, user_id1);

		user_id = spogServer.createUserAndCheck(prefix + "msp1_submsp2_sub2_user_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.DIRECT_ADMIN, msp1_submsp2_sub_2, test);
		writeToFile(MSP1_SUBMSP2_SUBORG2_USER1_EMAIL, prefix + "msp1_submsp2_sub1_user_1@arcserve.com");
		writeToFile(MSP1_SUBMSP2_SUBORG2_USER1_ID, user_id);

		// assign sub organization #1 to msp account admin #1 of sub msp organizaiton 1
		userIds = new String[] {msp1_submsp2_account_admin_1_id}; 
		userSpogServer.assignMspAccountAdmins(msp_org1_submsp_org2_id, msp1_submsp2_sub_1, userIds, spogServer.getJWTToken());




		// create sub org1 under the sub msp #1 of msp 2
		spogServer.setToken(csrToken);
		String msp2_submsp1_sub_1_name = orgPrefix + "msp2_submsp1_sub_1";
		String msp2_submsp1_sub_1 = spogServer.createAccountWithCheck(msp_org2_submsp_org1_id, msp2_submsp1_sub_1_name, msp_org2_submsp_org1_id);
		writeToFile(MSP2_SUBMSP1_SUB_ORG1_ID, msp2_submsp1_sub_1);
		writeToFile(MSP2_SUBMSP1_SUB_ORG1_NAME, msp2_submsp1_sub_1_name);
		
		user_id1 = spogServer.createUserAndCheck(prefix + "msp2_submsp1_sub_1_monitor_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.DIRECT_MONITOR, msp2_submsp1_sub_1, test);
		writeToFile(MSP2_SUBMSP1_SUBORG1_MONITOR_USER1_EMAIL, prefix + "msp2_submsp1_sub_1_monitor_1@arcserve.com");
		writeToFile(MSP2_SUBMSP1_SUBORG1_MONITOR_USER1_ID, user_id1);

		// create users under the sub org1 of sub msp #1
		user_id = spogServer.createUserAndCheck(prefix + "msp2_submsp1_sub1_user_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.DIRECT_ADMIN, msp2_submsp1_sub_1, test);
		writeToFile(MSP2_SUBMSP1_SUBORG1_USER1_EMAIL, prefix + "msp2_submsp1_sub1_user_1@arcserve.com");
		writeToFile(MSP2_SUBMSP1_SUBORG1_USER1_ID, user_id );

		user_id = spogServer.createUserAndCheck(prefix + "msp2_submsp1_sub1_user_2@arcserve.com", common_password, "first_name", "last_name", SpogConstants.DIRECT_ADMIN, msp2_submsp1_sub_1, test);
		writeToFile(MSP2_SUBMSP1_SUBORG1_USER2_EMAIL, prefix + "msp2_submsp1_sub1_user_2@arcserve.com");
		writeToFile(MSP2_SUBMSP1_SUBORG1_USER2_ID , user_id);

		// create sub org2 under the sub msp #1
		spogServer.setToken(csrToken);
		String msp2_submsp1_sub_2_name = orgPrefix + "msp2_submsp1_sub_2";
		String msp2_submsp1_sub_2 = spogServer.createAccountWithCheck(msp_org2_submsp_org1_id, msp2_submsp1_sub_2_name, msp_org2_submsp_org1_id);
		writeToFile(MSP2_SUBMSP1_SUB_ORG2_ID, msp2_submsp1_sub_2);
		writeToFile(MSP2_SUBMSP1_SUB_ORG2_NAME, msp2_submsp1_sub_2_name);
		
		user_id1 = spogServer.createUserAndCheck(prefix + "msp2_submsp1_sub_2_monitor_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.DIRECT_MONITOR, msp2_submsp1_sub_2, test);
		writeToFile(MSP2_SUBMSP1_SUBORG2_MONITOR_USER1_EMAIL, prefix + "msp2_submsp1_sub_2_monitor_1@arcserve.com");
		writeToFile(MSP2_SUBMSP1_SUBORG2_MONITOR_USER1_ID, user_id1);

		user_id = spogServer.createUserAndCheck(prefix + "msp2_submsp1_sub2_user_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.DIRECT_ADMIN, msp2_submsp1_sub_2, test);
		writeToFile(MSP2_SUBMSP1_SUBORG2_USER1_EMAIL, prefix + "msp2_submsp1_sub1_user_1@arcserve.com");
		writeToFile(MSP2_SUBMSP1_SUBORG2_USER1_ID, user_id);

		// assign sub organization #1 to msp account admin #1 of sub msp organizaiton 1
		userIds = new String[] {msp2_submsp1_account_admin_1_id}; 
		userSpogServer.assignMspAccountAdmins(msp_org2_submsp_org1_id, msp2_submsp1_sub_1, userIds, spogServer.getJWTToken());


		// create sub org1 under the sub msp #2 of msp2
		spogServer.setToken(csrToken);
		String msp2_submsp2_sub_1_name = orgPrefix + "msp1_submsp2_sub_1";
		String msp2_submsp2_sub_1 = spogServer.createAccountWithCheck(msp_org2_submsp_org2_id, msp2_submsp2_sub_1_name, msp_org2_submsp_org2_id);
		writeToFile(MSP2_SUBMSP2_SUB_ORG1_ID, msp2_submsp2_sub_1);
		writeToFile(MSP2_SUBMSP2_SUB_ORG1_NAME, msp2_submsp2_sub_1_name);

		user_id1 = spogServer.createUserAndCheck(prefix + "msp2_submsp2_sub_1_monitor_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.DIRECT_MONITOR, msp2_submsp2_sub_1, test);
		writeToFile(MSP2_SUBMSP2_SUBORG1_MONITOR_USER1_EMAIL, prefix + "msp2_submsp2_sub_1_monitor_1@arcserve.com");
		writeToFile(MSP2_SUBMSP2_SUBORG1_MONITOR_USER1_ID, user_id1);
		
		// create users under the sub org1 of sub msp #2
		user_id = spogServer.createUserAndCheck(prefix + "msp2_submsp2_sub1_user_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.DIRECT_ADMIN, msp2_submsp2_sub_1, test);
		writeToFile(MSP2_SUBMSP2_SUBORG1_USER1_EMAIL, prefix + "msp2_submsp2_sub1_user_1@arcserve.com");
		writeToFile(MSP2_SUBMSP2_SUBORG1_USER1_ID, user_id );

		user_id = spogServer.createUserAndCheck(prefix + "msp2_submsp2_sub1_user_2@arcserve.com", common_password, "first_name", "last_name", SpogConstants.DIRECT_ADMIN, msp2_submsp2_sub_1, test);
		writeToFile(MSP2_SUBMSP2_SUBORG1_USER2_EMAIL, prefix + "msp2_submsp2_sub1_user_2@arcserve.com");
		writeToFile(MSP2_SUBMSP2_SUBORG1_USER2_ID , user_id);

		// create sub org2 under the sub msp #1
		spogServer.setToken(csrToken);
		String msp2_submsp2_sub_2_name = orgPrefix + "msp2_submsp2_sub_2";
		String msp2_submsp2_sub_2 = spogServer.createAccountWithCheck(msp_org2_submsp_org2_id, msp2_submsp2_sub_2_name, msp_org2_submsp_org2_id);
		writeToFile(MSP2_SUBMSP2_SUB_ORG2_ID, msp2_submsp2_sub_2);
		writeToFile(MSP2_SUBMSP2_SUB_ORG2_NAME, msp2_submsp2_sub_2_name);
		
		user_id1 = spogServer.createUserAndCheck(prefix + "msp2_submsp2_sub_2_monitor_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.DIRECT_MONITOR, msp2_submsp2_sub_2, test);
		writeToFile(MSP2_SUBMSP2_SUBORG2_MONITOR_USER1_EMAIL, prefix + "msp2_submsp2_sub_2_monitor_1@arcserve.com");
		writeToFile(MSP2_SUBMSP2_SUBORG2_MONITOR_USER1_ID, user_id1);

		user_id = spogServer.createUserAndCheck(prefix + "msp2_submsp2_sub2_user_1@arcserve.com", common_password, "first_name", "last_name", SpogConstants.DIRECT_ADMIN, msp2_submsp2_sub_2, test);
		writeToFile(MSP2_SUBMSP2_SUBORG2_USER1_EMAIL, prefix + "msp2_submsp2_sub1_user_1@arcserve.com");
		writeToFile(MSP2_SUBMSP2_SUBORG2_USER1_ID, user_id);

		// assign sub organization #1 to msp account admin #1 of sub msp organizaiton 1
		userIds = new String[] {msp2_submsp2_account_admin_1_id}; 
		userSpogServer.assignMspAccountAdmins(msp_org2_submsp_org2_id, msp2_submsp2_sub_1, userIds, spogServer.getJWTToken());

		/*********************************** SUB ORGS under the sub msps ******************************************/
		
		createdOrgs=true;
		//flush and close the writer
		closeWriter();
		}catch (Exception e) {
			recycleVolumeInCDandDestroyOrg(getSuiteName());
		}finally {
			if(!createdOrgs)
				recycleVolumeInCDandDestroyOrg(getSuiteName());
		}
	
	}
	
	
	public void closeWriter() {
		writer.flush();
		writer.close();		
	}
	
	public void writeToFile(String key, String value) {
		writer.println(key+"="+value);
	}

	public void setWriter(PrintWriter writer) {
		this.writer = writer;
	}
	
	public PrintWriter getWriter() {
		return this.writer;
	}
	
}
