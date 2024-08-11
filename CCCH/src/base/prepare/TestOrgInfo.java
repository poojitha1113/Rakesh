package base.prepare;

import static org.testng.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import InvokerServer.SPOGServer;
import genericutil.ExtentManager;

/**
 * Initializes all the variables by reading properties file Performs login for
 * all users and stores tokens
 * 
 * By creating object for this class one can use all initialized variables
 * 
 * @author Rakesh.Chalamala
 *
 */
public class TestOrgInfo {

	private SPOGServer spogServer;
	private ExtentTest test;

	// variables
	public String csr_org_id;
	public String csr_org_name;
	public String csr_admin_email;
	public String csr_admin_password;
	public String csr_admin_user_id;
	public String csr_readonly_admin_email;
	public String csr_readonly_admin_password;
	public String csr_readonly_admin_user_id;

	public String direct_org1_id;
	public String direct_org1_name;
	public String direct_org1_user1_email;
	public String direct_org1_user1_id;
	public String direct_org1_user2_email;
	public String direct_org1_user2_id;
	public String direct_org2_id;
	public String direct_org2_name;
	public String direct_org2_user1_email;
	public String direct_org2_user1_id;
	public String direct_org2_user2_email;
	public String direct_org2_user2_id;

	// root msps
	public String root_msp_org1_id;
	public String root_msp_org1_name;
	public String root_msp_org1_user1_email;
	public String root_msp_org1_user1_id;
	public String root_msp_org1_user2_email;
	public String root_msp_org1_user2_id;
	public String root_msp_org1_msp_accountadmin1_email;
	public String root_msp_org1_msp_accountadmin1_id;
	public String root_msp_org1_msp_accountadmin2_email;
	public String root_msp_org1_msp_accountadmin2_id;

	public String root_msp_org2_id;
	public String root_msp_org2_name;
	public String root_msp_org2_user1_email;
	public String root_msp_org2_user1_id;
	public String root_msp_org2_user2_email;
	public String root_msp_org2_user2_id;
	public String root_msp_org2_msp_accountadmin1_email;
	public String root_msp_org2_msp_accountadmin1_id;
	public String root_msp_org2_msp_accountadmin2_email;
	public String root_msp_org2_msp_accountadmin2_id;

	// normal msps
	public String normal_msp_org1_id;
	public String normal_msp_org1_name;
	public String normal_msp_org1_user1_email;
	public String normal_msp_org1_user1_id;
	public String normal_msp_org1_user2_email;
	public String normal_msp_org1_user2_id;
	public String normal_msp_org1_msp_accountadmin1_email;
	public String normal_msp_org1_msp_accountadmin1_id;
	public String normal_msp_org1_msp_accountadmin2_email;
	public String normal_msp_org1_msp_accountadmin2_id;

	public String normal_msp_org2_id;
	public String normal_msp_org2_name;
	public String normal_msp_org2_user1_email;
	public String normal_msp_org2_user1_id;
	public String normal_msp_org2_user2_email;
	public String normal_msp_org2_user2_id;
	public String normal_msp_org2_msp_accountadmin1_email;
	public String normal_msp_org2_msp_accountadmin1_id;
	public String normal_msp_org2_msp_accountadmin2_email;
	public String normal_msp_org2_msp_accountadmin2_id;

	// Direct sub orgs
	public String root_msp1_suborg1_id;
	public String root_msp1_suborg1_name;
	public String root_msp1_suborg1_user1_email;
	public String root_msp1_suborg1_user1_id;
	public String root_msp1_suborg1_user2_email;
	public String root_msp1_suborg1_user2_id;

	public String root_msp1_suborg2_id;
	public String root_msp1_suborg2_name;
	public String root_msp1_suborg2_user1_email;
	public String root_msp1_suborg2_user1_id;

	public String root_msp2_suborg1_id;
	public String root_msp2_suborg1_name;
	public String root_msp2_suborg1_user1_email;
	public String root_msp2_suborg1_user1_id;
	public String root_msp2_suborg1_user2_email;
	public String root_msp2_suborg1_user2_id;

	public String root_msp2_suborg2_id;
	public String root_msp2_suborg2_name;
	public String root_msp2_suborg2_user1_email;
	public String root_msp2_suborg2_user1_id;

	public String normal_msp1_suborg1_id;
	public String normal_msp1_suborg1_name;
	public String normal_msp1_suborg1_user1_email;
	public String normal_msp1_suborg1_user1_id;
	public String normal_msp1_suborg1_user2_email;
	public String normal_msp1_suborg1_user2_id;

	public String normal_msp1_suborg2_id;
	public String normal_msp1_suborg2_name;
	public String normal_msp1_suborg2_user1_email;
	public String normal_msp1_suborg2_user1_id;

	public String normal_msp2_suborg1_id;
	public String normal_msp2_suborg1_name;
	public String normal_msp2_suborg1_user1_email;
	public String normal_msp2_suborg1_user1_id;
	public String normal_msp2_suborg1_user2_email;
	public String normal_msp2_suborg1_user2_id;

	public String normal_msp2_suborg2_id;
	public String normal_msp2_suborg2_name;
	public String normal_msp2_suborg2_user1_email;
	public String normal_msp2_suborg2_user1_id;

	// sub msps
	public String root_msp1_submsp_org1_id;
	public String root_msp1_submsp_org1_name;
	public String root_msp1_submsp1_user1_email;
	public String root_msp1_submsp1_user1_id;
	public String root_msp1_submsp1_user2_email;
	public String root_msp1_submsp1_user2_id;
	public String root_msp1_submsp1_account_admin_1;
	public String root_msp1_submsp1_account_admin_1_id;

	public String root_msp1_submsp_org2_id;
	public String root_msp1_submsp_org2_name;
	public String root_msp1_submsp2_user1_email;
	public String root_msp1_submsp2_user1_id;
	public String root_msp1_submsp2_user2_email;
	public String root_msp1_submsp2_user2_id;
	public String root_msp1_submsp2_account_admin_1;
	public String root_msp1_submsp2_account_admin_1_id;

	public String root_msp2_submsp_org1_id;
	public String root_msp2_submsp_org1_name;
	public String root_msp2_submsp1_user1_email;
	public String root_msp2_submsp1_user1_id;
	public String root_msp2_submsp1_user2_email;
	public String root_msp2_submsp1_user2_id;
	public String root_msp2_submsp1_account_admin_1;
	public String root_msp2_submsp1_account_admin_1_id;

	public String root_msp2_submsp_org2_id;
	public String root_msp2_submsp_org2_name;
	public String root_msp2_submsp2_user1_email;
	public String root_msp2_submsp2_user1_id;
	public String root_msp2_submsp2_user2_email;
	public String root_msp2_submsp2_user2_id;
	public String root_msp2_submsp2_account_admin_1;
	public String root_msp2_submsp2_account_admin_1_id;

	// sub orgs under sub msps
	public String msp1_submsp1_sub_org1_id;
	public String msp1_submsp1_sub_org1_name;
	public String msp1_submsp1_suborg1_user1_email;
	public String msp1_submsp1_suborg1_user1_id;
	public String msp1_submsp1_suborg1_user2_email;
	public String msp1_submsp1_suborg1_user2_id;

	public String msp1_submsp1_sub_org2_id;
	public String msp1_submsp1_sub_org2_name;
	public String msp1_submsp1_suborg2_user1_email;
	public String msp1_submsp1_suborg2_user1_id;

	public String msp1_submsp2_sub_org1_id;
	public String msp1_submsp2_sub_org1_name;
	public String msp1_submsp2_suborg1_user1_email;
	public String msp1_submsp2_suborg1_user1_id;
	public String msp1_submsp2_suborg1_user2_email;
	public String msp1_submsp2_suborg1_user2_id;

	public String msp1_submsp2_sub_org2_id;
	public String msp1_submsp2_sub_org2_name;
	public String msp1_submsp2_suborg2_user1_email;
	public String msp1_submsp2_suborg2_user1_id;

	public String msp2_submsp1_sub_org1_id;
	public String msp2_submsp1_sub_org1_name;
	public String msp2_submsp1_suborg1_user1_email;
	public String msp2_submsp1_suborg1_user1_id;
	public String msp2_submsp1_suborg1_user2_email;
	public String msp2_submsp1_suborg1_user2_id;

	public String msp2_submsp1_sub_org2_id;
	public String msp2_submsp1_sub_org2_name;
	public String msp2_submsp1_suborg2_user1_email;
	public String msp2_submsp1_suborg2_user1_id;

	public String msp2_submsp2_sub_org1_id;
	public String msp2_submsp2_sub_org1_name;
	public String msp2_submsp2_suborg1_user1_email;
	public String msp2_submsp2_suborg1_user1_id;
	public String msp2_submsp2_suborg1_user2_email;
	public String msp2_submsp2_suborg1_user2_id;

	public String msp2_submsp2_sub_org2_id;
	public String msp2_submsp2_sub_org2_name;
	public String msp2_submsp2_suborg2_user1_email;
	public String msp2_submsp2_suborg2_user1_id;

	// monitor role
	public String direct_org1_monitor_user1_id;
	public String direct_org2_monitor_user1_id;
	public String root_msp_org1_monitor_user1_id;
	public String root_msp_org2_monitor_user1_id;
	public String normal_msp_org1_monitor_user1_id;
	public String normal_msp_org2_monitor_user1_id;
	public String normal_msp1_suborg1_monitor_user1_id;
	public String normal_msp1_suborg2_monitor_user1_id;
	public String normal_msp2_suborg1_monitor_user1_id;
	public String normal_msp2_suborg2_monitor_user1_id;
	public String root_msp1_suborg1_monitor_user1_id;
	public String root_msp1_suborg2_monitor_user1_id;
	public String root_msp2_suborg1_monitor_user1_id;
	public String root_msp2_suborg2_monitor_user1_id;
	public String root_msp1_submsp1_monitor_user1_id;
	public String root_msp1_submsp2_monitor_user1_id;
	public String root_msp2_submsp1_monitor_user1_id;
	public String root_msp2_submsp2_monitor_user1_id;
	public String msp1_submsp1_suborg1_monitor_user1_id;
	public String msp1_submsp1_suborg2_monitor_user1_id;
	public String msp1_submsp2_suborg1_monitor_user1_id;
	public String msp1_submsp2_suborg2_monitor_user1_id;
	public String msp2_submsp2_suborg1_monitor_user1_id;
	public String msp2_submsp2_suborg2_monitor_user1_id;
	public String msp2_submsp1_suborg1_monitor_user1_id;
	public String msp2_submsp1_suborg2_monitor_user1_id;
	
	public String direct_org1_monitor_user1_email;
	public String direct_org2_monitor_user1_email;
	public String root_msp_org1_monitor1_user1_email;
	public String root_msp_org2_monitor_user1_email;
	public String normal_msp_org1_monitor_user1_email;
	public String normal_msp_org2_monitor_user1_email;
	public String normal_msp1_suborg1_monitor_user1_email;
	public String normal_msp1_suborg2_monitor_user1_email;
	public String normal_msp2_suborg1_monitor_user1_email;
	public String normal_msp2_suborg2_monitor_user1_email;
	public String root_msp1_suborg1_monitor_user1_email;
	public String root_msp1_suborg2_monitor_user1_email;
	public String root_msp2_suborg1_monitor_user1_email;
	public String root_msp2_suborg2_monitor_user1_email;
	public String root_msp1_submsp1_monitor_user1_email;
	public String root_msp1_submsp2_monitor_user1_email;
	public String root_msp2_submsp1_monitor_user1_email;
	public String root_msp2_submsp2_monitor_user1_email;
	public String msp1_submsp1_suborg1_monitor_user1_email;
	public String msp1_submsp1_suborg2_monitor_user1_email;
	public String msp1_submsp2_suborg1_monitor_user1_email;
	public String msp1_submsp2_suborg2_monitor_user1_email;
	public String msp2_submsp2_suborg1_monitor_user1_email;
	public String msp2_submsp2_suborg2_monitor_user1_email;
	public String msp2_submsp1_suborg1_monitor_user1_email;
	public String msp2_submsp1_suborg2_monitor_user1_email;

	public String common_password;

	// token variables for all users

	public String csr_token;
	public String csr_readonly_token;
	public String direct_org1_user1_token;
	public String direct_org1_user2_token;
	public String direct_org2_user1_token;
	public String direct_org2_user2_token;
	public String root_msp_org1_user1_token;
	public String root_msp_org1_user2_token;
	public String root_msp_org1_msp_accountadmin1_token;
	public String root_msp_org1_msp_accountadmin2_token;
	public String root_msp_org2_user1_token;
	public String root_msp_org2_user2_token;
	public String root_msp_org2_msp_accountadmin1_token;
	public String root_msp_org2_msp_accountadmin2_token;
	public String normal_msp_org1_user1_token;
	public String normal_msp_org1_user2_token;
	public String normal_msp_org1_msp_accountadmin1_token;
	public String normal_msp_org1_msp_accountadmin2_token;
	public String normal_msp_org2_user1_token;
	public String normal_msp_org2_user2_token;
	public String normal_msp_org2_msp_accountadmin1_token;
	public String normal_msp_org2_msp_accountadmin2_token;
	public String root_msp1_suborg1_user1_token;
	public String root_msp1_suborg1_user2_token;
	public String root_msp1_suborg2_user1_token;
	public String root_msp2_suborg1_user1_token;
	public String root_msp2_suborg1_user2_token;
	public String root_msp2_suborg2_user1_token;
	public String normal_msp1_suborg1_user1_token;
	public String normal_msp1_suborg1_user2_token;
	public String normal_msp1_suborg2_user1_token;
	public String normal_msp2_suborg1_user1_token;
	public String normal_msp2_suborg1_user2_token;
	public String normal_msp2_suborg2_user1_token;
	public String root_msp1_submsp1_user1_token;
	public String root_msp1_submsp1_user2_token;
	public String root_msp1_submsp1_account_admin_token;
	public String root_msp1_submsp2_user1_token;
	public String root_msp1_submsp2_user2_token;
	public String root_msp1_submsp2_account_admin_token;
	public String root_msp2_submsp1_user1_token;
	public String root_msp2_submsp1_user2_token;
	public String root_msp2_submsp1_account_admin_token;
	public String root_msp2_submsp2_user1_token;
	public String root_msp2_submsp2_user2_token;
	public String root_msp2_submsp2_account_admin_token;
	public String msp1_submsp1_suborg1_user1_token;
	public String msp1_submsp1_suborg1_user2_token;
	public String msp1_submsp1_suborg2_user1_token;
	public String msp1_submsp2_suborg1_user1_token;
	public String msp1_submsp2_suborg1_user2_token;
	public String msp1_submsp2_suborg2_user1_token;
	public String msp2_submsp1_suborg1_user1_token;
	public String msp2_submsp1_suborg1_user2_token;
	public String msp2_submsp1_suborg2_user1_token;
	public String msp2_submsp2_suborg1_user1_token;
	public String msp2_submsp2_suborg1_user2_token;
	public String msp2_submsp2_suborg2_user1_token;

	// monitor role
	public String direct_org1_monitor_user1_token;
	public String direct_org2_monitor_user1_token;
	public String root_msp_org1_monitor_user1_token;
	public String root_msp_org2_monitor_user1_token;
	public String normal_msp_org1_monitor_user1_token;
	public String normal_msp_org2_monitor_user1_token;
	public String normal_msp1_suborg1_monitor_user1_token;
	public String normal_msp1_suborg2_monitor_user1_token;
	public String normal_msp2_suborg1_monitor_user1_token;
	public String normal_msp2_suborg2_monitor_user1_token;
	
	public String root_msp1_suborg1_monitor_user1_token;
	public String root_msp1_suborg2_monitor_user1_token;
	public String root_msp2_suborg1_monitor_user1_token;
	public String root_msp2_suborg2_monitor_user1_token;
	public String root_msp1_submsp1_monitor_user1_token;
	public String root_msp1_submsp2_monitor_user1_token;
	public String root_msp2_submsp1_monitor_user1_token;
	public String root_msp2_submsp2_monitor_user1_token;
	public String msp1_submsp1_suborg1_monitor_user1_token;
	public String msp1_submsp1_suborg2_monitor_user1_token;
	public String msp1_submsp2_suborg1_monitor_user1_token;
	public String msp1_submsp2_suborg2_monitor_user1_token;
	public String msp2_submsp2_suborg1_monitor_user1_token;
	public String msp2_submsp2_suborg2_monitor_user1_token;
	public String msp2_submsp1_suborg1_monitor_user1_token;
	public String msp2_submsp1_suborg2_monitor_user1_token;

	public TestOrgInfo(SPOGServer spogServer, ExtentTest test) {

		this.spogServer = spogServer;
		this.test = test;
		readProp();
		allLogin();
	}

	public void allLogin() {
		csrUsersLogin();
		directOrgUsersLogin();
		rootMspOrgUsersLogin();
		normalMspOrgUsersLogin();
		subMspOrgUsersLogin();
		subOrgUsersLogin();
	}

	public void csrUsersLogin() {

		// login in as csr admin
		test.log(LogStatus.INFO, "Logging with csrAdmin");
		spogServer.userLogin(this.csr_admin_email, this.csr_admin_password);
		csr_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "csr_readonly user login");
		spogServer.userLogin(csr_readonly_admin_email, csr_readonly_admin_password);
		csr_readonly_token = spogServer.getJWTToken();
	}

	public void directOrgUsersLogin() {

		// direct org users login
		test.log(LogStatus.INFO, "Direct org1 user1 login");
		spogServer.userLogin(this.direct_org1_user1_email, common_password);
		direct_org1_user1_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Direct org1 user2 login");
		spogServer.userLogin(this.direct_org1_user2_email, common_password);
		direct_org1_user2_token = spogServer.getJWTToken();

		// direct org users login
		test.log(LogStatus.INFO, "Direct org2 user1 login");
		spogServer.userLogin(this.direct_org2_user1_email, common_password);
		direct_org2_user1_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Direct org2 user2 login");
		spogServer.userLogin(this.direct_org2_user2_email, common_password);
		direct_org2_user2_token = spogServer.getJWTToken();
		
		//monitor login
		test.log(LogStatus.INFO, "Direct org1 monitor user1 login");
		spogServer.userLogin(this.direct_org1_monitor_user1_email, common_password);
		direct_org1_monitor_user1_token = spogServer.getJWTToken();
		
		test.log(LogStatus.INFO, "Direct org2 monitor user1 login");
		spogServer.userLogin(this.direct_org2_monitor_user1_email, common_password);
		direct_org2_monitor_user1_token = spogServer.getJWTToken();
	}

	public void rootMspOrgUsersLogin() {

		// root msp - 1
		test.log(LogStatus.INFO, "ROOT MSP org1 user1 login");
		spogServer.userLogin(this.root_msp_org1_user1_email, common_password);
		root_msp_org1_user1_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "ROOT MSP org1 user2 login");
		spogServer.userLogin(this.root_msp_org1_user2_email, common_password);
		root_msp_org1_user2_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "ROOT MSP org1 account admin user1 login");
		spogServer.userLogin(root_msp_org1_msp_accountadmin1_email, common_password);
		this.root_msp_org1_msp_accountadmin1_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "ROOT MSP org1 account admin user2 login");
		spogServer.userLogin(root_msp_org1_msp_accountadmin2_email, common_password);
		this.root_msp_org1_msp_accountadmin2_token = spogServer.getJWTToken();

		// root msp - 2
		test.log(LogStatus.INFO, "ROOT MSP org2 user1 login");
		spogServer.userLogin(this.root_msp_org2_user1_email, common_password);
		root_msp_org2_user1_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "ROOT MSP org2 user2 login");
		spogServer.userLogin(this.root_msp_org2_user2_email, common_password);
		root_msp_org2_user2_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "ROOT MSP org2 account admin user1 login");
		spogServer.userLogin(root_msp_org2_msp_accountadmin1_email, common_password);
		this.root_msp_org2_msp_accountadmin1_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "ROOT MSP org2 account admin user2 login");
		spogServer.userLogin(root_msp_org2_msp_accountadmin2_email, common_password);
		this.root_msp_org2_msp_accountadmin2_token = spogServer.getJWTToken();
		
		//monitor login
		
		test.log(LogStatus.INFO, "root msp org1 monitor user1 login");
		spogServer.userLogin(this.root_msp_org1_monitor1_user1_email, common_password);
		root_msp_org1_monitor_user1_token = spogServer.getJWTToken();
		
		test.log(LogStatus.INFO, "root msp org2 monitor user1 login");
		spogServer.userLogin(this.root_msp_org2_monitor_user1_email, common_password);
		root_msp_org2_monitor_user1_token = spogServer.getJWTToken();
	}

	public void normalMspOrgUsersLogin() {

		// normal msp - 1
		test.log(LogStatus.INFO, "Normal MSP org1 user1 login");
		spogServer.userLogin(this.normal_msp_org1_user1_email, common_password);
		normal_msp_org1_user1_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Normal MSP org1 user2 login");
		spogServer.userLogin(this.normal_msp_org1_user2_email, common_password);
		normal_msp_org1_user2_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Normal MSP org1 account admin user1 login");
		spogServer.userLogin(normal_msp_org1_msp_accountadmin1_email, common_password);
		this.normal_msp_org1_msp_accountadmin1_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Normal MSP org1 account admin user2 login");
		spogServer.userLogin(normal_msp_org1_msp_accountadmin2_email, common_password);
		this.normal_msp_org1_msp_accountadmin2_token = spogServer.getJWTToken();

		// normal msp - 2
		test.log(LogStatus.INFO, "Normal MSP org2 user1 login");
		spogServer.userLogin(this.normal_msp_org2_user1_email, common_password);
		normal_msp_org2_user1_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Normal MSP org2 user2 login");
		spogServer.userLogin(this.normal_msp_org2_user2_email, common_password);
		normal_msp_org2_user2_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Normal MSP org2 account admin user1 login");
		spogServer.userLogin(normal_msp_org2_msp_accountadmin1_email, common_password);
		this.normal_msp_org2_msp_accountadmin1_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Normal MSP org2 account admin user2 login");
		spogServer.userLogin(normal_msp_org2_msp_accountadmin2_email, common_password);
		this.normal_msp_org2_msp_accountadmin2_token = spogServer.getJWTToken();
		
		//monitor login
		test.log(LogStatus.INFO, "normal msp org1 monitor user1 login");
		spogServer.userLogin(this.normal_msp_org1_monitor_user1_email, common_password);
		normal_msp_org1_monitor_user1_token = spogServer.getJWTToken();
		
		test.log(LogStatus.INFO, "normal msp org2 monitor user1 login");
		spogServer.userLogin(this.normal_msp_org2_monitor_user1_email, common_password);
		normal_msp_org2_monitor_user1_token = spogServer.getJWTToken();
		
	}

	public void subMspOrgUsersLogin() {

		// MSP - 1 --> sub msp - 1
		test.log(LogStatus.INFO, "root MSP1 Sub msp1 user1 login");
		spogServer.userLogin(root_msp1_submsp1_user1_email, common_password);
		this.root_msp1_submsp1_user1_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "root MSP1 Sub msp1 user2 login");
		spogServer.userLogin(root_msp1_submsp1_user2_email, common_password);
		this.root_msp1_submsp1_user2_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "root MSP1 Sub msp1 account admin login");
		spogServer.userLogin(root_msp1_submsp1_account_admin_1, common_password);
		this.root_msp1_submsp1_account_admin_token = spogServer.getJWTToken();

		// MSP - 1 --> sub msp - 2
		test.log(LogStatus.INFO, "root MSP1 Sub msp2 user1 login");
		spogServer.userLogin(root_msp1_submsp2_user1_email, common_password);
		this.root_msp1_submsp2_user1_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "root MSP1 Sub msp2 user2 login");
		spogServer.userLogin(root_msp1_submsp2_user2_email, common_password);
		this.root_msp1_submsp2_user2_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "root MSP1 Sub msp2 account admin login");
		spogServer.userLogin(root_msp1_submsp2_account_admin_1, common_password);
		this.root_msp1_submsp2_account_admin_token = spogServer.getJWTToken();

		// MSP - 2 --> sub msp - 1
		test.log(LogStatus.INFO, "root MSP2 Sub msp1 user1 login");
		spogServer.userLogin(root_msp2_submsp1_user1_email, common_password);
		this.root_msp2_submsp1_user1_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "root MSP2 Sub msp1 user2 login");
		spogServer.userLogin(root_msp2_submsp1_user2_email, common_password);
		this.root_msp2_submsp1_user2_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "root MSP2 Sub msp1 account admin login");
		spogServer.userLogin(root_msp2_submsp1_account_admin_1, common_password);
		this.root_msp2_submsp1_account_admin_token = spogServer.getJWTToken();

		// MSP - 2 --> sub msp - 2
		test.log(LogStatus.INFO, "root MSP2 Sub msp2 user1 login");
		spogServer.userLogin(root_msp2_submsp2_user1_email, common_password);
		this.root_msp2_submsp2_user1_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "root MSP2 Sub msp2 user2 login");
		spogServer.userLogin(root_msp2_submsp2_user2_email, common_password);
		this.root_msp2_submsp2_user2_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "root MSP2 Sub msp2 account admin login");
		spogServer.userLogin(root_msp2_submsp2_account_admin_1, common_password);
		this.root_msp2_submsp2_account_admin_token = spogServer.getJWTToken();
		
		// monitor login
		test.log(LogStatus.INFO, "root MSP1 Sub msp1 monitor user1 login");
		spogServer.userLogin(root_msp1_submsp1_monitor_user1_email, common_password);
		this.root_msp1_submsp1_monitor_user1_token = spogServer.getJWTToken();
		
		test.log(LogStatus.INFO, "root MSP1 Sub msp2 monitor user1 login");
		spogServer.userLogin(root_msp1_submsp2_monitor_user1_email, common_password);
		this.root_msp1_submsp2_monitor_user1_token = spogServer.getJWTToken();
		
		test.log(LogStatus.INFO, "root MSP2 Sub msp1 monitor user1 login");
		spogServer.userLogin(root_msp2_submsp1_monitor_user1_email, common_password);
		this.root_msp2_submsp1_monitor_user1_token = spogServer.getJWTToken();
		
		test.log(LogStatus.INFO, "root MSP2 Sub msp2 monitor user1 login");
		spogServer.userLogin(root_msp2_submsp2_monitor_user1_email, common_password);
		this.root_msp2_submsp2_monitor_user1_token = spogServer.getJWTToken();
	}

	public void subOrgUsersLogin() {

		// root msp #1 ---> sub orgs
		test.log(LogStatus.INFO, "Login in to sub org 1 user 1 of root msp 1");
		spogServer.userLogin(this.root_msp1_suborg1_user1_email, common_password);
		root_msp1_suborg1_user1_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Login in to sub org 1 user 2 of root msp 1");
		spogServer.userLogin(this.root_msp1_suborg1_user2_email, common_password);
		root_msp1_suborg1_user2_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Login in to sub org 2 user 1 of root msp 1");
		spogServer.userLogin(this.root_msp1_suborg2_user1_email, common_password);
		root_msp1_suborg2_user1_token = spogServer.getJWTToken();
		
		test.log(LogStatus.INFO, "Login in to sub org 1 user 1 of root msp 1");
		spogServer.userLogin(this.root_msp1_suborg1_user1_email, common_password);
		root_msp1_suborg1_user1_token = spogServer.getJWTToken();
		
		//monitor login
		test.log(LogStatus.INFO, "Login in to sub org 1 monitor user 1 of root msp 1");
		spogServer.userLogin(this.root_msp1_suborg1_monitor_user1_email, common_password);
		root_msp1_suborg1_monitor_user1_token = spogServer.getJWTToken();
		
		test.log(LogStatus.INFO, "Login in to sub org 2 monitor user 1 of root msp 1");
		spogServer.userLogin(this.root_msp1_suborg2_monitor_user1_email, common_password);
		root_msp1_suborg2_monitor_user1_token = spogServer.getJWTToken();
		

		// root msp #2 ---> sub orgs
		test.log(LogStatus.INFO, "Login in to sub org 1 user 1 of root msp 2");
		spogServer.userLogin(this.root_msp2_suborg1_user1_email, common_password);
		root_msp2_suborg1_user1_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Login in to sub org 1 user 2 of root msp 2");
		spogServer.userLogin(this.root_msp2_suborg1_user2_email, common_password);
		root_msp2_suborg1_user2_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Login in to sub org 2 user 1 of root msp 2");
		spogServer.userLogin(this.root_msp2_suborg2_user1_email, common_password);
		root_msp2_suborg2_user1_token = spogServer.getJWTToken();
		
		// monitor login
		test.log(LogStatus.INFO, "Login in to sub org 1 monitor user 1 of root msp 1");
		spogServer.userLogin(this.root_msp2_suborg1_monitor_user1_email, common_password);
		root_msp2_suborg1_monitor_user1_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Login in to sub org 2 monitor user 1 of root msp 1");
		spogServer.userLogin(this.root_msp2_suborg2_monitor_user1_email, common_password);
		root_msp2_suborg2_monitor_user1_token = spogServer.getJWTToken();

		// normal msp #1 ---> sub orgs
		test.log(LogStatus.INFO, "Login in to sub org 1 user 1 of normal msp 1");
		spogServer.userLogin(this.normal_msp1_suborg1_user1_email, common_password);
		normal_msp1_suborg1_user1_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Login in to sub org 1 user 2 of normal msp 1");
		spogServer.userLogin(this.normal_msp1_suborg1_user2_email, common_password);
		normal_msp1_suborg1_user2_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Login in to sub org 2 user 1 of normal msp 1");
		spogServer.userLogin(this.normal_msp1_suborg2_user1_email, common_password);
		normal_msp1_suborg2_user1_token = spogServer.getJWTToken();
		
		// monitor login
		test.log(LogStatus.INFO, "Login in to sub org 1 monitor user 1 of normal msp 1");
		spogServer.userLogin(this.normal_msp1_suborg1_monitor_user1_email, common_password);
		normal_msp1_suborg1_monitor_user1_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Login in to sub org 2 monitor user 1 of normal msp 1");
		spogServer.userLogin(this.normal_msp1_suborg2_monitor_user1_email, common_password);
		normal_msp1_suborg2_monitor_user1_token = spogServer.getJWTToken();

		// normal msp #2 ---> sub orgs
		test.log(LogStatus.INFO, "Login in to sub org 1 user 1 of normal msp 2");
		spogServer.userLogin(this.normal_msp2_suborg1_user1_email, common_password);
		normal_msp2_suborg1_user1_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Login in to sub org 1 user 2 of normal msp 2");
		spogServer.userLogin(this.normal_msp2_suborg1_user2_email, common_password);
		normal_msp2_suborg1_user2_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Login in to sub org 2 user 1 of normal msp 2");
		spogServer.userLogin(this.normal_msp2_suborg2_user1_email, common_password);
		normal_msp2_suborg2_user1_token = spogServer.getJWTToken();
		
		// monitor login
		test.log(LogStatus.INFO, "Login in to sub org 1 monitor user 1 of normal msp 2");
		spogServer.userLogin(this.normal_msp2_suborg1_monitor_user1_email, common_password);
		normal_msp2_suborg1_monitor_user1_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Login in to sub org 2 monitor user 1 of normal msp 2");
		spogServer.userLogin(this.normal_msp2_suborg2_monitor_user1_email, common_password);
		normal_msp2_suborg2_monitor_user1_token = spogServer.getJWTToken();

		// msp #1 ---> sub msp #1 ---> sub orgs
		test.log(LogStatus.INFO, "Login in to sub org 1 user 1 of sub msp 1 of root msp 1");
		spogServer.userLogin(this.msp1_submsp1_suborg1_user1_email, common_password);
		msp1_submsp1_suborg1_user1_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Login in to sub org 1 user 2 of sub msp 1 of root msp 1");
		spogServer.userLogin(this.msp1_submsp1_suborg1_user2_email, common_password);
		msp1_submsp1_suborg1_user2_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Login in to sub org 2 user 1 of sub msp 1 of root msp 1");
		spogServer.userLogin(this.msp1_submsp1_suborg2_user1_email, common_password);
		msp1_submsp1_suborg2_user1_token = spogServer.getJWTToken();
		
		//monitor login
		test.log(LogStatus.INFO, "Login in to sub org 1 monitor user 1 of sub msp 1 of root msp 1");
		spogServer.userLogin(this.msp1_submsp1_suborg1_monitor_user1_email, common_password);
		msp1_submsp1_suborg1_monitor_user1_token = spogServer.getJWTToken();
		
		test.log(LogStatus.INFO, "Login in to sub org 2 monitor user 1 of sub msp 1 of root msp 1");
		spogServer.userLogin(this.msp1_submsp1_suborg2_monitor_user1_email, common_password);
		msp1_submsp1_suborg2_monitor_user1_token = spogServer.getJWTToken();

		// msp #1 ---> sub msp #2 ---> sub orgs
		test.log(LogStatus.INFO, "Login in to sub org 1 user 1 of sub msp 2 of root msp 1");
		spogServer.userLogin(this.msp1_submsp2_suborg1_user1_email, common_password);
		msp1_submsp2_suborg1_user1_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Login in to sub org 1 user 2 of sub msp 2 of root msp 1");
		spogServer.userLogin(this.msp1_submsp2_suborg1_user2_email, common_password);
		msp1_submsp2_suborg1_user2_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Login in to sub org 2 user 1 of sub msp 2 of root msp 1");
		spogServer.userLogin(this.msp1_submsp2_suborg2_user1_email, common_password);
		msp1_submsp2_suborg2_user1_token = spogServer.getJWTToken();

		// monitor login
		test.log(LogStatus.INFO, "Login in to sub org 1 monitor user 1 of sub msp 2 of root msp 1");
		spogServer.userLogin(this.msp1_submsp2_suborg1_monitor_user1_email, common_password);
		msp1_submsp2_suborg1_monitor_user1_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Login in to sub org 2 monitor user 1 of sub msp 2 of root msp 1");
		spogServer.userLogin(this.msp1_submsp2_suborg2_monitor_user1_email, common_password);
		msp1_submsp2_suborg2_monitor_user1_token = spogServer.getJWTToken();

		// msp #2 ---> sub msp #1 ---> sub orgs
		test.log(LogStatus.INFO, "Login in to sub org 1 user 1 of sub msp 1 of root msp 2");
		spogServer.userLogin(this.msp2_submsp1_suborg1_user1_email, common_password);
		msp2_submsp1_suborg1_user1_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Login in to sub org 1 user 2 of sub msp 1 of root msp 2");
		spogServer.userLogin(this.msp2_submsp1_suborg1_user2_email, common_password);
		msp2_submsp1_suborg1_user2_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Login in to sub org 2 user 1 of sub msp 1 of root msp 2");
		spogServer.userLogin(this.msp2_submsp1_suborg2_user1_email, common_password);
		msp2_submsp1_suborg2_user1_token = spogServer.getJWTToken();
		
		// monitor login
		test.log(LogStatus.INFO, "Login in to sub org 1 monitor user 1 of sub msp 1 of root msp 2");
		spogServer.userLogin(this.msp2_submsp1_suborg1_monitor_user1_email, common_password);
		msp2_submsp1_suborg1_monitor_user1_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Login in to sub org 2 monitor user 1 of sub msp 1 of root msp 2");
		spogServer.userLogin(this.msp2_submsp1_suborg2_monitor_user1_email, common_password);
		msp2_submsp1_suborg2_monitor_user1_token = spogServer.getJWTToken();

		// msp #1 ---> sub msp #2 ---> sub orgs
		test.log(LogStatus.INFO, "Login in to sub org 1 user 1 of sub msp 2 of root msp 2");
		spogServer.userLogin(this.msp2_submsp2_suborg1_user1_email, common_password);
		msp2_submsp2_suborg1_user1_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Login in to sub org 1 user 2 of sub msp 2 of root msp 2");
		spogServer.userLogin(this.msp2_submsp2_suborg1_user2_email, common_password);
		msp2_submsp2_suborg1_user2_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Login in to sub org 2 user 1 of sub msp 2 of root msp 2");
		spogServer.userLogin(this.msp2_submsp2_suborg2_user1_email, common_password);
		msp2_submsp2_suborg2_user1_token = spogServer.getJWTToken();
		
		// monitor login
		test.log(LogStatus.INFO, "Login in to sub org 1 monitor user 1 of sub msp 2 of root msp 2");
		spogServer.userLogin(this.msp2_submsp2_suborg1_monitor_user1_email, common_password);
		msp2_submsp2_suborg1_monitor_user1_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Login in to sub org 2 monitor user 1 of sub msp 2 of root msp 2");
		spogServer.userLogin(this.msp2_submsp2_suborg2_monitor_user1_email, common_password);
		msp2_submsp2_suborg2_monitor_user1_token = spogServer.getJWTToken();
	}

	public void readProp() {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(BQSummaryReport.getFilePath()));

			csr_org_id = prop.getProperty(CreateOrgsInfo.CSR_ORG_ID);
			csr_org_name = prop.getProperty(CreateOrgsInfo.CSR_ORG_NAME);
			csr_admin_email = prop.getProperty(CreateOrgsInfo.CSR_ADMIN_EMAIL);
			csr_admin_password = prop.getProperty(CreateOrgsInfo.CSR_ADMIN_PASSWORD);
			csr_admin_user_id = prop.getProperty(CreateOrgsInfo.CSR_ADMIN_USER_ID);
			csr_readonly_admin_email = prop.getProperty(CreateOrgsInfo.CSR_READONLY_ADMIN_EMAIL);
			csr_readonly_admin_password = prop.getProperty(CreateOrgsInfo.CSR_READONLY_ADMIN_PASSWORD);
			csr_readonly_admin_user_id = prop.getProperty(CreateOrgsInfo.CSR_READONLY_ADMIN_USER_ID);

			direct_org1_id = prop.getProperty(CreateOrgsInfo.DIRECT_ORG1_ID);
			direct_org1_name = prop.getProperty(CreateOrgsInfo.DIRECT_ORG1_NAME);
			direct_org1_user1_email = prop.getProperty(CreateOrgsInfo.DIRECT_ORG1_USER1_EMAIL);
			direct_org1_user1_id = prop.getProperty(CreateOrgsInfo.DIRECT_ORG1_USER1_ID);
			direct_org1_user2_email = prop.getProperty(CreateOrgsInfo.DIRECT_ORG1_USER2_EMAIL);
			direct_org1_user2_id = prop.getProperty(CreateOrgsInfo.DIRECT_ORG1_USER2_ID);
			direct_org2_id = prop.getProperty(CreateOrgsInfo.DIRECT_ORG2_ID);
			direct_org2_name = prop.getProperty(CreateOrgsInfo.DIRECT_ORG2_NAME);
			direct_org2_user1_email = prop.getProperty(CreateOrgsInfo.DIRECT_ORG2_USER1_EMAIL);
			direct_org2_user1_id = prop.getProperty(CreateOrgsInfo.DIRECT_ORG2_USER1_ID);
			direct_org2_user2_email = prop.getProperty(CreateOrgsInfo.DIRECT_ORG2_USER2_EMAIL);
			direct_org2_user2_id = prop.getProperty(CreateOrgsInfo.DIRECT_ORG2_USER2_ID);

			root_msp_org1_id = prop.getProperty(CreateOrgsInfo.ROOT_MSP_ORG1_ID);
			root_msp_org1_name = prop.getProperty(CreateOrgsInfo.ROOT_MSP_ORG1_NAME);
			root_msp_org1_user1_email = prop.getProperty(CreateOrgsInfo.ROOT_MSP_ORG1_USER1_EMAIL);
			root_msp_org1_user1_id = prop.getProperty(CreateOrgsInfo.ROOT_MSP_ORG1_USER1_ID);
			root_msp_org1_user2_email = prop.getProperty(CreateOrgsInfo.ROOT_MSP_ORG1_USER2_EMAIL);
			root_msp_org1_user2_id = prop.getProperty(CreateOrgsInfo.ROOT_MSP_ORG1_USER2_ID);
			root_msp_org1_msp_accountadmin1_email = prop
					.getProperty(CreateOrgsInfo.ROOT_MSP_ORG1_MSP_ACCOUNTADMIN1_EMAIL);
			root_msp_org1_msp_accountadmin1_id = prop.getProperty(CreateOrgsInfo.ROOT_MSP_ORG1_MSP_ACCOUNTADMIN1_ID);
			root_msp_org1_msp_accountadmin2_email = prop
					.getProperty(CreateOrgsInfo.ROOT_MSP_ORG1_MSP_ACCOUNTADMIN2_EMAIL);
			root_msp_org1_msp_accountadmin2_id = prop.getProperty(CreateOrgsInfo.ROOT_MSP_ORG1_MSP_ACCOUNTADMIN2_ID);

			root_msp_org2_id = prop.getProperty(CreateOrgsInfo.ROOT_MSP_ORG2_ID);
			root_msp_org2_name = prop.getProperty(CreateOrgsInfo.ROOT_MSP_ORG2_NAME);
			root_msp_org2_user1_email = prop.getProperty(CreateOrgsInfo.ROOT_MSP_ORG2_USER1_EMAIL);
			root_msp_org2_user1_id = prop.getProperty(CreateOrgsInfo.ROOT_MSP_ORG2_USER1_ID);
			root_msp_org2_user2_email = prop.getProperty(CreateOrgsInfo.ROOT_MSP_ORG2_USER2_EMAIL);
			root_msp_org2_user2_id = prop.getProperty(CreateOrgsInfo.ROOT_MSP_ORG2_USER2_ID);
			root_msp_org2_msp_accountadmin1_email = prop
					.getProperty(CreateOrgsInfo.ROOT_MSP_ORG2_MSP_ACCOUNTADMIN1_EMAIL);
			root_msp_org2_msp_accountadmin1_id = prop.getProperty(CreateOrgsInfo.ROOT_MSP_ORG2_MSP_ACCOUNTADMIN1_ID);
			root_msp_org2_msp_accountadmin2_email = prop
					.getProperty(CreateOrgsInfo.ROOT_MSP_ORG2_MSP_ACCOUNTADMIN2_EMAIL);
			root_msp_org2_msp_accountadmin2_id = prop.getProperty(CreateOrgsInfo.ROOT_MSP_ORG2_MSP_ACCOUNTADMIN2_ID);

			normal_msp_org1_id = prop.getProperty(CreateOrgsInfo.NORMAL_MSP_ORG1_ID);
			normal_msp_org1_name = prop.getProperty(CreateOrgsInfo.NORMAL_MSP_ORG1_NAME);
			normal_msp_org1_user1_email = prop.getProperty(CreateOrgsInfo.NORMAL_MSP_ORG1_USER1_EMAIL);
			normal_msp_org1_user1_id = prop.getProperty(CreateOrgsInfo.NORMAL_MSP_ORG1_USER1_ID);
			normal_msp_org1_user2_email = prop.getProperty(CreateOrgsInfo.NORMAL_MSP_ORG1_USER2_EMAIL);
			normal_msp_org1_user2_id = prop.getProperty(CreateOrgsInfo.NORMAL_MSP_ORG1_USER2_ID);
			normal_msp_org1_msp_accountadmin1_email = prop
					.getProperty(CreateOrgsInfo.NORMAL_MSP_ORG1_MSP_ACCOUNTADMIN1_EMAIL);
			normal_msp_org1_msp_accountadmin1_id = prop
					.getProperty(CreateOrgsInfo.NORMAL_MSP_ORG1_MSP_ACCOUNTADMIN1_ID);
			normal_msp_org1_msp_accountadmin2_email = prop
					.getProperty(CreateOrgsInfo.NORMAL_MSP_ORG1_MSP_ACCOUNTADMIN2_EMAIL);
			normal_msp_org1_msp_accountadmin2_id = prop
					.getProperty(CreateOrgsInfo.NORMAL_MSP_ORG1_MSP_ACCOUNTADMIN2_ID);

			normal_msp_org2_id = prop.getProperty(CreateOrgsInfo.NORMAL_MSP_ORG2_ID);
			normal_msp_org2_name = prop.getProperty(CreateOrgsInfo.NORMAL_MSP_ORG2_NAME);
			normal_msp_org2_user1_email = prop.getProperty(CreateOrgsInfo.NORMAL_MSP_ORG2_USER1_EMAIL);
			normal_msp_org2_user1_id = prop.getProperty(CreateOrgsInfo.NORMAL_MSP_ORG2_USER1_ID);
			normal_msp_org2_user2_email = prop.getProperty(CreateOrgsInfo.NORMAL_MSP_ORG2_USER2_EMAIL);
			normal_msp_org2_user2_id = prop.getProperty(CreateOrgsInfo.NORMAL_MSP_ORG2_USER2_ID);
			normal_msp_org2_msp_accountadmin1_email = prop
					.getProperty(CreateOrgsInfo.NORMAL_MSP_ORG2_MSP_ACCOUNTADMIN1_EMAIL);
			normal_msp_org2_msp_accountadmin1_id = prop
					.getProperty(CreateOrgsInfo.NORMAL_MSP_ORG2_MSP_ACCOUNTADMIN1_ID);
			normal_msp_org2_msp_accountadmin2_email = prop
					.getProperty(CreateOrgsInfo.NORMAL_MSP_ORG2_MSP_ACCOUNTADMIN2_EMAIL);
			normal_msp_org2_msp_accountadmin2_id = prop
					.getProperty(CreateOrgsInfo.NORMAL_MSP_ORG2_MSP_ACCOUNTADMIN2_ID);

			root_msp1_suborg1_id = prop.getProperty(CreateOrgsInfo.ROOT_MSP1_SUBORG1_ID);
			root_msp1_suborg1_name = prop.getProperty(CreateOrgsInfo.ROOT_MSP1_SUBORG1_NAME);
			root_msp1_suborg1_user1_email = prop.getProperty(CreateOrgsInfo.ROOT_MSP1_SUBORG1_USER1_EMAIL);
			root_msp1_suborg1_user1_id = prop.getProperty(CreateOrgsInfo.ROOT_MSP1_SUBORG1_USER1_ID);
			root_msp1_suborg1_user2_email = prop.getProperty(CreateOrgsInfo.ROOT_MSP1_SUBORG1_USER2_EMAIL);
			root_msp1_suborg1_user2_id = prop.getProperty(CreateOrgsInfo.ROOT_MSP1_SUBORG1_USER2_ID);

			root_msp1_suborg2_id = prop.getProperty(CreateOrgsInfo.ROOT_MSP1_SUBORG2_ID);
			root_msp1_suborg2_name = prop.getProperty(CreateOrgsInfo.ROOT_MSP1_SUBORG2_NAME);
			root_msp1_suborg2_user1_email = prop.getProperty(CreateOrgsInfo.ROOT_MSP1_SUBORG2_USER1_EMAIL);
			root_msp1_suborg2_user1_id = prop.getProperty(CreateOrgsInfo.ROOT_MSP1_SUBORG2_USER1_ID);

			root_msp2_suborg1_id = prop.getProperty(CreateOrgsInfo.ROOT_MSP2_SUBORG1_ID);
			root_msp2_suborg1_name = prop.getProperty(CreateOrgsInfo.ROOT_MSP2_SUBORG1_NAME);
			root_msp2_suborg1_user1_email = prop.getProperty(CreateOrgsInfo.ROOT_MSP2_SUBORG1_USER1_EMAIL);
			root_msp2_suborg1_user1_id = prop.getProperty(CreateOrgsInfo.ROOT_MSP2_SUBORG1_USER1_ID);
			root_msp2_suborg1_user2_email = prop.getProperty(CreateOrgsInfo.ROOT_MSP2_SUBORG1_USER2_EMAIL);
			root_msp2_suborg1_user2_id = prop.getProperty(CreateOrgsInfo.ROOT_MSP2_SUBORG1_USER2_ID);

			root_msp2_suborg2_id = prop.getProperty(CreateOrgsInfo.ROOT_MSP2_SUBORG2_ID);
			root_msp2_suborg2_name = prop.getProperty(CreateOrgsInfo.ROOT_MSP2_SUBORG2_NAME);
			root_msp2_suborg2_user1_email = prop.getProperty(CreateOrgsInfo.ROOT_MSP2_SUBORG2_USER1_EMAIL);
			root_msp2_suborg2_user1_id = prop.getProperty(CreateOrgsInfo.ROOT_MSP2_SUBORG2_USER1_ID);

			normal_msp1_suborg1_id = prop.getProperty(CreateOrgsInfo.NORMAL_MSP1_SUBORG1_ID);
			normal_msp1_suborg1_name = prop.getProperty(CreateOrgsInfo.NORMAL_MSP1_SUBORG1_NAME);
			normal_msp1_suborg1_user1_email = prop.getProperty(CreateOrgsInfo.NORMAL_MSP1_SUBORG1_USER1_EMAIL);
			normal_msp1_suborg1_user1_id = prop.getProperty(CreateOrgsInfo.NORMAL_MSP1_SUBORG1_USER1_ID);
			normal_msp1_suborg1_user2_email = prop.getProperty(CreateOrgsInfo.NORMAL_MSP1_SUBORG1_USER2_EMAIL);
			normal_msp1_suborg1_user2_id = prop.getProperty(CreateOrgsInfo.NORMAL_MSP1_SUBORG1_USER2_ID);

			normal_msp1_suborg2_id = prop.getProperty(CreateOrgsInfo.NORMAL_MSP1_SUBORG2_ID);
			normal_msp1_suborg2_name = prop.getProperty(CreateOrgsInfo.NORMAL_MSP1_SUBORG2_NAME);
			normal_msp1_suborg2_user1_email = prop.getProperty(CreateOrgsInfo.NORMAL_MSP1_SUBORG2_USER1_EMAIL);
			normal_msp1_suborg2_user1_id = prop.getProperty(CreateOrgsInfo.NORMAL_MSP1_SUBORG2_USER1_ID);

			normal_msp2_suborg1_id = prop.getProperty(CreateOrgsInfo.NORMAL_MSP2_SUBORG1_ID);
			normal_msp2_suborg1_name = prop.getProperty(CreateOrgsInfo.NORMAL_MSP2_SUBORG1_NAME);
			normal_msp2_suborg1_user1_email = prop.getProperty(CreateOrgsInfo.NORMAL_MSP2_SUBORG1_USER1_EMAIL);
			normal_msp2_suborg1_user1_id = prop.getProperty(CreateOrgsInfo.NORMAL_MSP2_SUBORG1_USER1_ID);
			normal_msp2_suborg1_user2_email = prop.getProperty(CreateOrgsInfo.NORMAL_MSP2_SUBORG1_USER2_EMAIL);
			normal_msp2_suborg1_user2_id = prop.getProperty(CreateOrgsInfo.NORMAL_MSP2_SUBORG1_USER2_ID);

			normal_msp2_suborg2_id = prop.getProperty(CreateOrgsInfo.NORMAL_MSP2_SUBORG2_ID);
			normal_msp2_suborg2_name = prop.getProperty(CreateOrgsInfo.NORMAL_MSP2_SUBORG2_NAME);
			normal_msp2_suborg2_user1_email = prop.getProperty(CreateOrgsInfo.NORMAL_MSP2_SUBORG2_USER1_EMAIL);
			normal_msp2_suborg2_user1_id = prop.getProperty(CreateOrgsInfo.NORMAL_MSP2_SUBORG2_USER1_ID);

			root_msp1_submsp_org1_id = prop.getProperty(CreateOrgsInfo.ROOT_MSP1_SUBMSP_ORG1_ID);
			root_msp1_submsp_org1_name = prop.getProperty(CreateOrgsInfo.ROOT_MSP1_SUBMSP_ORG1_NAME);
			root_msp1_submsp1_user1_email = prop.getProperty(CreateOrgsInfo.ROOT_MSP1_SUBMSP_ORG1_USER1_EMAIL);
			root_msp1_submsp1_user1_id = prop.getProperty(CreateOrgsInfo.ROOT_MSP1_SUBMSP_ORG1_USER1_ID);
			root_msp1_submsp1_user2_email = prop.getProperty(CreateOrgsInfo.ROOT_MSP1_SUBMSP_ORG1_USER2_EMAIL);
			root_msp1_submsp1_user2_id = prop.getProperty(CreateOrgsInfo.ROOT_MSP1_SUBMSP_ORG1_USER2_ID);
			root_msp1_submsp1_account_admin_1 = prop
					.getProperty(CreateOrgsInfo.ROOT_MSP1_SUBMSP_ORG1_ACCOUNT_ADMIN_EMAIL);
			root_msp1_submsp1_account_admin_1_id = prop
					.getProperty(CreateOrgsInfo.ROOT_MSP1_SUBMSP_ORG1_ACCOUNT_ADMIN_ID);

			root_msp1_submsp_org2_id = prop.getProperty(CreateOrgsInfo.ROOT_MSP1_SUBMSP_ORG2_ID);
			root_msp1_submsp_org2_name = prop.getProperty(CreateOrgsInfo.ROOT_MSP1_SUBMSP_ORG2_NAME);
			root_msp1_submsp2_user1_email = prop.getProperty(CreateOrgsInfo.ROOT_MSP1_SUBMSP_ORG2_USER1_EMAIL);
			root_msp1_submsp2_user1_id = prop.getProperty(CreateOrgsInfo.ROOT_MSP1_SUBMSP_ORG2_USER1_ID);
			root_msp1_submsp2_user2_email = prop.getProperty(CreateOrgsInfo.ROOT_MSP1_SUBMSP_ORG2_USER2_EMAIL);
			root_msp1_submsp2_user2_id = prop.getProperty(CreateOrgsInfo.ROOT_MSP1_SUBMSP_ORG2_USER2_ID);
			root_msp1_submsp2_account_admin_1 = prop
					.getProperty(CreateOrgsInfo.ROOT_MSP1_SUBMSP_ORG2_ACCOUNT_ADMIN_EMAIL);
			root_msp1_submsp2_account_admin_1_id = prop
					.getProperty(CreateOrgsInfo.ROOT_MSP1_SUBMSP_ORG2_ACCOUNT_ADMIN_ID);

			root_msp2_submsp_org1_id = prop.getProperty(CreateOrgsInfo.ROOT_MSP2_SUBMSP_ORG1_ID);
			root_msp2_submsp_org1_name = prop.getProperty(CreateOrgsInfo.ROOT_MSP2_SUBMSP_ORG1_NAME);
			root_msp2_submsp1_user1_email = prop.getProperty(CreateOrgsInfo.ROOT_MSP2_SUBMSP_ORG1_USER1_EMAIL);
			root_msp2_submsp1_user1_id = prop.getProperty(CreateOrgsInfo.ROOT_MSP2_SUBMSP_ORG1_USER1_ID);
			root_msp2_submsp1_user2_email = prop.getProperty(CreateOrgsInfo.ROOT_MSP2_SUBMSP_ORG1_USER2_EMAIL);
			root_msp2_submsp1_user2_id = prop.getProperty(CreateOrgsInfo.ROOT_MSP2_SUBMSP_ORG1_USER2_ID);
			root_msp2_submsp1_account_admin_1 = prop
					.getProperty(CreateOrgsInfo.ROOT_MSP2_SUBMSP_ORG1_ACCOUNT_ADMIN_EMAIL);
			root_msp2_submsp1_account_admin_1_id = prop
					.getProperty(CreateOrgsInfo.ROOT_MSP2_SUBMSP_ORG1_ACCOUNT_ADMIN_ID);

			root_msp2_submsp_org2_id = prop.getProperty(CreateOrgsInfo.ROOT_MSP2_SUBMSP_ORG2_ID);
			root_msp2_submsp_org2_name = prop.getProperty(CreateOrgsInfo.ROOT_MSP2_SUBMSP_ORG2_NAME);
			root_msp2_submsp2_user1_email = prop.getProperty(CreateOrgsInfo.ROOT_MSP2_SUBMSP_ORG2_USER1_EMAIL);
			root_msp2_submsp2_user1_id = prop.getProperty(CreateOrgsInfo.ROOT_MSP2_SUBMSP_ORG2_USER1_ID);
			root_msp2_submsp2_user2_email = prop.getProperty(CreateOrgsInfo.ROOT_MSP2_SUBMSP_ORG2_USER2_EMAIL);
			root_msp2_submsp2_user2_id = prop.getProperty(CreateOrgsInfo.ROOT_MSP2_SUBMSP_ORG2_USER2_ID);
			root_msp2_submsp2_account_admin_1 = prop
					.getProperty(CreateOrgsInfo.ROOT_MSP2_SUBMSP_ORG2_ACCOUNT_ADMIN_EMAIL);
			root_msp2_submsp2_account_admin_1_id = prop
					.getProperty(CreateOrgsInfo.ROOT_MSP2_SUBMSP_ORG2_ACCOUNT_ADMIN_ID);

			msp1_submsp1_sub_org1_id = prop.getProperty(CreateOrgsInfo.MSP1_SUBMSP1_SUB_ORG1_ID);
			msp1_submsp1_sub_org1_name = prop.getProperty(CreateOrgsInfo.MSP1_SUBMSP1_SUB_ORG1_NAME);
			msp1_submsp1_suborg1_user1_email = prop.getProperty(CreateOrgsInfo.MSP1_SUBMSP1_SUBORG1_USER1_EMAIL);
			msp1_submsp1_suborg1_user1_id = prop.getProperty(CreateOrgsInfo.MSP1_SUBMSP1_SUBORG1_USER1_ID);
			msp1_submsp1_suborg1_user2_email = prop.getProperty(CreateOrgsInfo.MSP1_SUBMSP1_SUBORG1_USER2_EMAIL);
			msp1_submsp1_suborg1_user2_id = prop.getProperty(CreateOrgsInfo.MSP1_SUBMSP1_SUBORG1_USER2_ID);

			msp1_submsp1_sub_org2_id = prop.getProperty(CreateOrgsInfo.MSP1_SUBMSP1_SUB_ORG2_ID);
			msp1_submsp1_sub_org2_name = prop.getProperty(CreateOrgsInfo.MSP1_SUBMSP1_SUB_ORG2_NAME);
			msp1_submsp1_suborg2_user1_email = prop.getProperty(CreateOrgsInfo.MSP1_SUBMSP1_SUBORG2_USER1_EMAIL);
			msp1_submsp1_suborg2_user1_id = prop.getProperty(CreateOrgsInfo.MSP1_SUBMSP1_SUBORG2_USER1_ID);

			msp1_submsp2_sub_org1_id = prop.getProperty(CreateOrgsInfo.MSP1_SUBMSP2_SUB_ORG1_ID);
			msp1_submsp2_sub_org1_name = prop.getProperty(CreateOrgsInfo.MSP1_SUBMSP2_SUB_ORG1_NAME);
			msp1_submsp2_suborg1_user1_email = prop.getProperty(CreateOrgsInfo.MSP1_SUBMSP2_SUBORG1_USER1_EMAIL);
			msp1_submsp2_suborg1_user1_id = prop.getProperty(CreateOrgsInfo.MSP1_SUBMSP2_SUBORG1_USER1_ID);
			msp1_submsp2_suborg1_user2_email = prop.getProperty(CreateOrgsInfo.MSP1_SUBMSP2_SUBORG1_USER2_EMAIL);
			msp1_submsp2_suborg1_user2_id = prop.getProperty(CreateOrgsInfo.MSP1_SUBMSP2_SUBORG1_USER2_ID);

			msp1_submsp2_sub_org2_id = prop.getProperty(CreateOrgsInfo.MSP1_SUBMSP2_SUB_ORG2_ID);
			msp1_submsp2_sub_org2_name = prop.getProperty(CreateOrgsInfo.MSP1_SUBMSP2_SUB_ORG2_NAME);
			msp1_submsp2_suborg2_user1_email = prop.getProperty(CreateOrgsInfo.MSP1_SUBMSP2_SUBORG2_USER1_EMAIL);
			msp1_submsp2_suborg2_user1_id = prop.getProperty(CreateOrgsInfo.MSP1_SUBMSP2_SUBORG2_USER1_ID);

			msp2_submsp1_sub_org1_id = prop.getProperty(CreateOrgsInfo.MSP2_SUBMSP1_SUB_ORG1_ID);
			msp2_submsp1_sub_org1_name = prop.getProperty(CreateOrgsInfo.MSP2_SUBMSP1_SUB_ORG1_NAME);
			msp2_submsp1_suborg1_user1_email = prop.getProperty(CreateOrgsInfo.MSP2_SUBMSP1_SUBORG1_USER1_EMAIL);
			msp2_submsp1_suborg1_user1_id = prop.getProperty(CreateOrgsInfo.MSP2_SUBMSP1_SUBORG1_USER1_ID);
			msp2_submsp1_suborg1_user2_email = prop.getProperty(CreateOrgsInfo.MSP2_SUBMSP1_SUBORG1_USER2_EMAIL);
			msp2_submsp1_suborg1_user2_id = prop.getProperty(CreateOrgsInfo.MSP2_SUBMSP1_SUBORG1_USER2_ID);

			msp2_submsp1_sub_org2_id = prop.getProperty(CreateOrgsInfo.MSP2_SUBMSP1_SUB_ORG2_ID);
			msp2_submsp1_sub_org2_name = prop.getProperty(CreateOrgsInfo.MSP2_SUBMSP1_SUB_ORG2_NAME);
			msp2_submsp1_suborg2_user1_email = prop.getProperty(CreateOrgsInfo.MSP2_SUBMSP1_SUBORG2_USER1_EMAIL);
			msp2_submsp1_suborg2_user1_id = prop.getProperty(CreateOrgsInfo.MSP2_SUBMSP1_SUBORG2_USER1_ID);

			msp2_submsp2_sub_org1_id = prop.getProperty(CreateOrgsInfo.MSP2_SUBMSP2_SUB_ORG1_ID);
			msp2_submsp2_sub_org1_name = prop.getProperty(CreateOrgsInfo.MSP2_SUBMSP2_SUB_ORG1_NAME);
			msp2_submsp2_suborg1_user1_email = prop.getProperty(CreateOrgsInfo.MSP2_SUBMSP2_SUBORG1_USER1_EMAIL);
			msp2_submsp2_suborg1_user1_id = prop.getProperty(CreateOrgsInfo.MSP2_SUBMSP2_SUBORG1_USER1_ID);
			msp2_submsp2_suborg1_user2_email = prop.getProperty(CreateOrgsInfo.MSP2_SUBMSP2_SUBORG1_USER2_EMAIL);
			msp2_submsp2_suborg1_user2_id = prop.getProperty(CreateOrgsInfo.MSP2_SUBMSP2_SUBORG1_USER2_ID);

			msp2_submsp2_sub_org2_id = prop.getProperty(CreateOrgsInfo.MSP2_SUBMSP2_SUB_ORG2_ID);
			msp2_submsp2_sub_org2_name = prop.getProperty(CreateOrgsInfo.MSP2_SUBMSP2_SUB_ORG2_NAME);
			msp2_submsp2_suborg2_user1_email = prop.getProperty(CreateOrgsInfo.MSP2_SUBMSP2_SUBORG2_USER1_EMAIL);
			msp2_submsp2_suborg2_user1_id = prop.getProperty(CreateOrgsInfo.MSP2_SUBMSP2_SUBORG2_USER1_ID);

			common_password = prop.getProperty(CreateOrgsInfo.COMMON_PASSWORD);

			direct_org1_monitor_user1_email = prop.getProperty(CreateOrgsInfo.DIRECT_ORG1_MONITOR1_USER1_EMAIL);
			direct_org2_monitor_user1_email = prop.getProperty(CreateOrgsInfo.DIRECT_ORG2_MONITOR1_USER1_EMAIL);
			root_msp_org1_monitor1_user1_email = prop.getProperty(CreateOrgsInfo.ROOT_MSP_ORG1_MONITOR1_USER1_EMAIL);
			root_msp_org2_monitor_user1_email = prop.getProperty(CreateOrgsInfo.ROOT_MSP_ORG2_MONITOR1_USER1_EMAIL);
			normal_msp_org1_monitor_user1_email = prop.getProperty(CreateOrgsInfo.NORMAL_MSP_ORG1_MONITOR1_USER1_EMAIL);
			normal_msp_org2_monitor_user1_email = prop.getProperty(CreateOrgsInfo.NORMAL_MSP_ORG2_MONITOR1_USER1_EMAIL);
			root_msp1_suborg1_monitor_user1_email = prop
					.getProperty(CreateOrgsInfo.ROOT_MSP1_SUBORG1_MONITOR1_USER1_EMAIL);
			root_msp1_suborg2_monitor_user1_email = prop
					.getProperty(CreateOrgsInfo.ROOT_MSP1_SUBORG2_MONITOR1_USER1_EMAIL);
			root_msp2_suborg1_monitor_user1_email = prop
					.getProperty(CreateOrgsInfo.ROOT_MSP2_SUBORG1_MONITOR1_USER1_EMAIL);
			root_msp2_suborg2_monitor_user1_email = prop
					.getProperty(CreateOrgsInfo.ROOT_MSP2_SUBORG2_MONITOR1_USER1_EMAIL);
			root_msp1_submsp1_monitor_user1_email = prop
					.getProperty(CreateOrgsInfo.ROOT_MSP1_SUBMSP_ORG1_MONITOR1_USER1_EMAIL);
			root_msp1_submsp2_monitor_user1_email = prop
					.getProperty(CreateOrgsInfo.ROOT_MSP1_SUBMSP_ORG2_MONITOR1_USER1_EMAIL);
			root_msp2_submsp1_monitor_user1_email = prop
					.getProperty(CreateOrgsInfo.ROOT_MSP2_SUBMSP_ORG1_MONITOR1_USER1_EMAIL);
			root_msp2_submsp2_monitor_user1_email = prop
					.getProperty(CreateOrgsInfo.ROOT_MSP2_SUBMSP_ORG2_MONITOR1_USER1_EMAIL);
			msp1_submsp1_suborg1_monitor_user1_email = prop
					.getProperty(CreateOrgsInfo.MSP1_SUBMSP1_SUBORG1_MONITOR_USER1_EMAIL);
			msp1_submsp1_suborg2_monitor_user1_email = prop
					.getProperty(CreateOrgsInfo.MSP1_SUBMSP1_SUBORG2_MONITOR_USER1_EMAIL);
			msp1_submsp2_suborg1_monitor_user1_email = prop
					.getProperty(CreateOrgsInfo.MSP1_SUBMSP2_SUBORG1_MONITOR_USER1_EMAIL);
			msp1_submsp2_suborg2_monitor_user1_email = prop
					.getProperty(CreateOrgsInfo.MSP1_SUBMSP2_SUBORG2_MONITOR_USER1_EMAIL);
			msp2_submsp2_suborg1_monitor_user1_email = prop
					.getProperty(CreateOrgsInfo.MSP2_SUBMSP2_SUBORG1_MONITOR_USER1_EMAIL);
			msp2_submsp2_suborg2_monitor_user1_email = prop
					.getProperty(CreateOrgsInfo.MSP2_SUBMSP2_SUBORG2_MONITOR_USER1_EMAIL);
			msp2_submsp1_suborg1_monitor_user1_email = prop
					.getProperty(CreateOrgsInfo.MSP2_SUBMSP1_SUBORG1_MONITOR_USER1_EMAIL);
			msp2_submsp1_suborg2_monitor_user1_email = prop
					.getProperty(CreateOrgsInfo.MSP2_SUBMSP1_SUBORG2_MONITOR_USER1_EMAIL);

			direct_org1_monitor_user1_id = prop.getProperty(CreateOrgsInfo.DIRECT_ORG1_MONITOR1_USER1_ID);
			direct_org2_monitor_user1_id = prop.getProperty(CreateOrgsInfo.DIRECT_ORG2_MONITOR1_USER1_ID);
			root_msp_org1_monitor_user1_id = prop.getProperty(CreateOrgsInfo.ROOT_MSP_ORG1_MONITOR1_USER1_ID);
			root_msp_org2_monitor_user1_id = prop.getProperty(CreateOrgsInfo.ROOT_MSP_ORG2_MONITOR1_USER1_ID);
			normal_msp_org1_monitor_user1_id = prop.getProperty(CreateOrgsInfo.NORMAL_MSP_ORG1_MONITOR1_USER1_ID);
			normal_msp_org2_monitor_user1_id = prop.getProperty(CreateOrgsInfo.NORMAL_MSP_ORG2_MONITOR1_USER1_ID);
		
			normal_msp1_suborg1_monitor_user1_id = prop.getProperty(CreateOrgsInfo.NORMAL_MSP1_SUBORG1_MONITOR1_USER1_ID);
			normal_msp1_suborg2_monitor_user1_id = prop.getProperty(CreateOrgsInfo.NORMAL_MSP1_SUBORG2_MONITOR1_USER1_ID);
			normal_msp2_suborg1_monitor_user1_id = prop.getProperty(CreateOrgsInfo.NORMAL_MSP2_SUBORG1_MONITOR1_USER1_ID);
			normal_msp2_suborg2_monitor_user1_id = prop.getProperty(CreateOrgsInfo.NORMAL_MSP2_SUBORG2_MONITOR1_USER1_ID);
			
			normal_msp1_suborg1_monitor_user1_email = prop.getProperty(CreateOrgsInfo.NORMAL_MSP1_SUBORG1_MONITOR1_USER1_EMAIL);
			normal_msp1_suborg2_monitor_user1_email = prop.getProperty(CreateOrgsInfo.NORMAL_MSP1_SUBORG2_MONITOR1_USER1_EMAIL);
			normal_msp2_suborg1_monitor_user1_email = prop.getProperty(CreateOrgsInfo.NORMAL_MSP2_SUBORG1_MONITOR1_USER1_EMAIL);
			normal_msp2_suborg2_monitor_user1_email = prop.getProperty(CreateOrgsInfo.NORMAL_MSP2_SUBORG2_MONITOR1_USER1_EMAIL);
			
			
			root_msp1_suborg1_monitor_user1_id = prop.getProperty(CreateOrgsInfo.ROOT_MSP1_SUBORG1_MONITOR1_USER1_ID);
			root_msp1_suborg2_monitor_user1_id = prop.getProperty(CreateOrgsInfo.ROOT_MSP1_SUBORG2_MONITOR1_USER1_ID);
			root_msp2_suborg1_monitor_user1_id = prop.getProperty(CreateOrgsInfo.ROOT_MSP2_SUBORG1_MONITOR1_USER1_ID);
			root_msp2_suborg2_monitor_user1_id = prop.getProperty(CreateOrgsInfo.ROOT_MSP2_SUBORG2_MONITOR1_USER1_ID);
			root_msp1_submsp1_monitor_user1_id = prop
					.getProperty(CreateOrgsInfo.ROOT_MSP1_SUBMSP_ORG1_MONITOR1_USER1_ID);
			root_msp1_submsp2_monitor_user1_id = prop
					.getProperty(CreateOrgsInfo.ROOT_MSP1_SUBMSP_ORG2_MONITOR1_USER1_ID);
			root_msp2_submsp1_monitor_user1_id = prop
					.getProperty(CreateOrgsInfo.ROOT_MSP2_SUBMSP_ORG1_MONITOR1_USER1_ID);
			root_msp2_submsp2_monitor_user1_id = prop
					.getProperty(CreateOrgsInfo.ROOT_MSP2_SUBMSP_ORG2_MONITOR1_USER1_ID);
			msp1_submsp1_suborg1_monitor_user1_id = prop
					.getProperty(CreateOrgsInfo.MSP1_SUBMSP1_SUBORG1_MONITOR_USER1_ID);
			msp1_submsp1_suborg2_monitor_user1_id = prop
					.getProperty(CreateOrgsInfo.MSP1_SUBMSP1_SUBORG2_MONITOR_USER1_ID);
			msp1_submsp2_suborg1_monitor_user1_id = prop
					.getProperty(CreateOrgsInfo.MSP1_SUBMSP2_SUBORG1_MONITOR_USER1_ID);
			msp1_submsp2_suborg2_monitor_user1_id = prop
					.getProperty(CreateOrgsInfo.MSP1_SUBMSP2_SUBORG2_MONITOR_USER1_ID);
			msp2_submsp2_suborg1_monitor_user1_id = prop
					.getProperty(CreateOrgsInfo.MSP2_SUBMSP2_SUBORG1_MONITOR_USER1_ID);
			msp2_submsp2_suborg2_monitor_user1_id = prop
					.getProperty(CreateOrgsInfo.MSP2_SUBMSP2_SUBORG2_MONITOR_USER1_ID);
			msp2_submsp1_suborg1_monitor_user1_id = prop
					.getProperty(CreateOrgsInfo.MSP2_SUBMSP1_SUBORG1_MONITOR_USER1_ID);
			msp2_submsp1_suborg2_monitor_user1_id = prop
					.getProperty(CreateOrgsInfo.MSP2_SUBMSP1_SUBORG2_MONITOR_USER1_ID);

		} catch (IOException e) {
			e.printStackTrace();
			assertTrue(false, "Failed to load RPS properties file");
		}
	}

}
