package api.preparedata;

import java.util.ArrayList;

import org.apache.commons.lang3.RandomStringUtils;

import com.relevantcodes.extentreports.ExtentTest;

import Constants.SpogConstants;
import InvokerServer.Org4SPOGServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import io.restassured.response.Response;

public class InitialTestDataImpl {
	private String baseURI;
	private String port;
	private String csrUser;
	private String csrPassword;
	private String orgPrefix;
	private String password;
	private SPOGServer spogServer;
	private UserSpogServer userSpogServer;
	private Org4SPOGServer org4spogServer;
	public InitialTestDataImpl(String baseURI, String port, String csrUser, String csrPassword, String orgPrefix, String password) {
		super();
		this.baseURI = baseURI;
		this.csrUser = csrUser;
		this.csrPassword = csrPassword;
		this.orgPrefix = orgPrefix;
		this.password = password;
		this.spogServer = new SPOGServer(baseURI, port);
		this.userSpogServer = new UserSpogServer(baseURI, port);
		this.org4spogServer = new Org4SPOGServer(baseURI, port);
	}

	public InitialTestData initialize() {
		InitialTestData dto = new InitialTestData();
		ExtentTest test = new ExtentTest("initialize organizations", "description");

		String prefix = RandomStringUtils.randomAlphanumeric(8);
		spogServer.userLogin(this.csrUser, this.csrPassword);
		// create direct organization #1
		String direct_org_1 = spogServer.CreateOrganizationWithCheck(orgPrefix + "direct_1", SpogConstants.DIRECT_ORG, prefix + "direct_1_user_1@arcserve.com", password, "firstName", "lastname");
		dto.setDirect_org_1(direct_org_1);
		dto.setDirect_org_1_user_1_email(prefix + "direct_1_user_1@arcserve.com");
		spogServer.userLogin(dto.getDirect_org_1_user_1_email(), password);
		dto.setDirect_org_1_user_1(spogServer.GetLoggedinUser_UserID());
		
		// create another user in direct organization #1
		String direct_org_1_user_2 = spogServer.createUserAndCheck(prefix + "direct_1_user_2@arcserve.com", password, "first_name", "last_name", SpogConstants.DIRECT_ADMIN, direct_org_1, test);
		dto.setDirect_org_1_user_2(direct_org_1_user_2);
		dto.setDirect_org_1_user_2_email(prefix + "direct_1_user_2@arcserve.com");
		
		// create monitor user in direct organization #1
		String direct_org_1_monitor_user_1 = spogServer.createUserAndCheck(prefix + "direct_1_monitor_user_1@arcserve.com", password, "first_name", "last_name", SpogConstants.DIRECT_MONITOR, direct_org_1, test);
		dto.setDirect_org_1_monitor_user_1(direct_org_1_monitor_user_1);
		dto.setDirect_org_1_monitor_user_1_email(prefix + "direct_1_monitor_user_1@arcserve.com");
		
		spogServer.userLogin(this.csrUser, this.csrPassword);
		
		// create direct organization #2
		String direct_org_2 = spogServer.CreateOrganizationWithCheck(orgPrefix + "direct_2", SpogConstants.DIRECT_ORG, prefix + "direct_2_user_1@arcserve.com", password, "firstName", "lastname");
		dto.setDirect_org_2(direct_org_2);
		dto.setDirect_org_2_user_1_email(prefix + "direct_2_user_1@arcserve.com");
		spogServer.userLogin(dto.getDirect_org_2_user_1_email(), password);
		dto.setDirect_org_2_user_1(spogServer.GetLoggedinUser_UserID());
		
		// create monitor user in direct organization #2
//		String direct_org_2_monitor_user_1 = spogServer.createUserAndCheck(prefix + "direct_2_monitor_user_1@arcserve.com", password, "first_name", "last_name", SpogConstants.DIRECT_MONITOR, direct_org_2, test);
//		dto.setDirect_org_2_monitor_user_1(direct_org_2_monitor_user_1);
//		dto.setDirect_org_2_monitor_user_1_email(prefix + "direct_2_monitor_user_1@arcserve.com");
		
		// create msp organization #1
		spogServer.userLogin(this.csrUser, this.csrPassword);
		String msp_org_1 = spogServer.CreateOrganizationWithCheck(orgPrefix + "msp_1", SpogConstants.MSP_ORG, prefix + "msp_1_user_1@arcserve.com", password, "firstName", "lastname");
		dto.setMsp_org_1(msp_org_1);
		dto.setMsp_org_1_user_1_email(prefix + "msp_1_user_1@arcserve.com");
		spogServer.userLogin(dto.getMsp_org_1_user_1_email(), password);
		dto.setMsp_org_1_user_1(spogServer.GetLoggedinUser_UserID());
		
		// create another user in msp organization #1
		String msp_org_1_user_2 = spogServer.createUserAndCheck(prefix + "msp_1_user_2@arcserve.com", password, "first_name", "last_name", SpogConstants.MSP_ADMIN, msp_org_1, test);
		dto.setMsp_org_1_user_2(msp_org_1_user_2);
		dto.setMsp_org_1_user_2_email(prefix + "msp_1_user_2@arcserve.com");
		
		// create monitor user in msp organization #1
		String msp_org_1_monitor_user_1 = spogServer.createUserAndCheck(prefix + "msp_1_monitor_user_1@arcserve.com", password, "first_name", "last_name", SpogConstants.MSP_MONITOR, msp_org_1, test);
		dto.setMsp_org_1_monitor_user_1(msp_org_1_monitor_user_1);
		dto.setMsp_org_1_monitor_user_1_email(prefix + "msp_1_monitor_user_1@arcserve.com");
		
		// create 2 account admins in msp organization #1
		String msp_org_1_account_admin_1 = spogServer.createUserAndCheck(prefix + "msp_1_account_admin_1@arcserve.com", password, "first_name", "last_name", SpogConstants.MSP_ACCOUNT_ADMIN, msp_org_1, test);
		dto.setMsp_org_1_account_admin_1(msp_org_1_account_admin_1);
		dto.setMsp_org_1_account_admin_1_email(prefix + "msp_1_account_admin_1@arcserve.com");
		
		String msp_org_1_account_admin_2 = spogServer.createUserAndCheck(prefix + "msp_1_account_admin_2@arcserve.com", password, "first_name", "last_name", SpogConstants.MSP_ACCOUNT_ADMIN, msp_org_1, test);
		dto.setMsp_org_1_account_admin_2(msp_org_1_account_admin_2);
		dto.setMsp_org_1_account_admin_2_email(prefix + "msp_1_account_admin_2@arcserve.com");
		
		// create sub organization #1 and 2 users in the sub organization;
		String msp_org_1_sub_1 = spogServer.createAccountWithCheck(msp_org_1, orgPrefix + "msp_1_sub_1", msp_org_1);
		dto.setMsp_org_1_sub_1(msp_org_1_sub_1);
		String msp_org_1_sub_1_user_1 = spogServer.createUserAndCheck(prefix + "msp_1_sub_1_user_1@arcserve.com", password, "first_name", "last_name", SpogConstants.DIRECT_ADMIN, msp_org_1_sub_1, test);
		dto.setMsp_org_1_sub_1_user_1(msp_org_1_sub_1_user_1);
		dto.setMsp_org_1_sub_1_user_1_email(prefix + "msp_1_sub_1_user_1@arcserve.com");
		String msp_org_1_sub_1_user_2 = spogServer.createUserAndCheck(prefix + "msp_1_sub_1_user_2@arcserve.com", password, "first_name", "last_name", SpogConstants.DIRECT_ADMIN, msp_org_1_sub_1, test);
		dto.setMsp_org_1_sub_1_user_2(msp_org_1_sub_1_user_2);
		dto.setMsp_org_1_sub_1_user_2_email(prefix + "msp_1_sub_1_user_2@arcserve.com");	
		
		// create monitor user in sub organization #1 
		String msp_org_1_sub_1_monitor_user_1 = spogServer.createUserAndCheck(prefix + "msp_1_sub_1_monitor_user_1@arcserve.com", password, "first_name", "last_name", SpogConstants.DIRECT_MONITOR, msp_org_1_sub_1, test);
		dto.setMsp_org_1_sub_1_monitor_user_1(msp_org_1_sub_1_monitor_user_1);
		dto.setMsp_org_1_sub_1_monitor_user_1_email(prefix + "msp_1_sub_1_monitor_user_1@arcserve.com");
		
		// create sub organization #2 and 1 user in the sub organization;
		String msp_org_1_sub_2 = spogServer.createAccountWithCheck(msp_org_1, orgPrefix + "msp_1_sub_2", msp_org_1);
		dto.setMsp_org_1_sub_2(msp_org_1_sub_2);
		String msp_org_1_sub_2_user_1 = spogServer.createUserAndCheck(prefix + "msp_1_sub_2_user_1@arcserve.com", password, "first_name", "last_name", SpogConstants.DIRECT_ADMIN, msp_org_1_sub_2, test);
		dto.setMsp_org_1_sub_2_user_1(msp_org_1_sub_2_user_1);
		dto.setMsp_org_1_sub_2_user_1_email(prefix + "msp_1_sub_2_user_1@arcserve.com");
		
		// assign sub organization #1 to msp account admin #1
		String[] userIds = new String[] {msp_org_1_account_admin_1}; 
		userSpogServer.assignMspAccountAdmins(msp_org_1, msp_org_1_sub_1, userIds, spogServer.getJWTToken());

		// create msp organization #2
		spogServer.userLogin(this.csrUser, this.csrPassword);
		String msp_org_2 = spogServer.CreateOrganizationWithCheck(orgPrefix + "msp_2", SpogConstants.MSP_ORG, prefix + "msp_2_user_1@arcserve.com", password, "firstName", "lastname");
		dto.setMsp_org_2(msp_org_2);
		dto.setMsp_org_2_user_1_email(prefix + "msp_2_user_1@arcserve.com");
		spogServer.userLogin(dto.getMsp_org_2_user_1_email(), password);
		dto.setMsp_org_2_user_1(spogServer.GetLoggedinUser_UserID());
		String msp_org_2_account_admin_1 = spogServer.createUserAndCheck(prefix + "msp_2_account_admin_1@arcserve.com", password, "first_name", "last_name", SpogConstants.MSP_ACCOUNT_ADMIN, msp_org_2, test);
		dto.setMsp_org_2_account_admin_1(msp_org_2_account_admin_1);
		dto.setMsp_org_2_account_admin_1_email(prefix + "msp_2_account_admin_1@arcserve.com");
		
		String msp_org_2_sub_1 = spogServer.createAccountWithCheck(msp_org_2, orgPrefix + "msp_2_sub_1", msp_org_2);
		dto.setMsp_org_2_sub_1(msp_org_2_sub_1);
		String msp_org_2_sub_1_user_1 = spogServer.createUserAndCheck(prefix + "msp_2_sub_1_user_1@arcserve.com", password, "first_name", "last_name", SpogConstants.DIRECT_ADMIN, msp_org_2_sub_1, test);
		dto.setMsp_org_2_sub_1_user_1(msp_org_2_sub_1_user_1);
		dto.setMsp_org_2_sub_1_user_1_email(prefix + "msp_2_sub_1_user_1@arcserve.com");
		
		// create root msp organization #1
		spogServer.userLogin(this.csrUser, this.csrPassword);
		String root_msp_org_1 = spogServer.CreateOrganizationWithCheck(orgPrefix + "root_msp_1", SpogConstants.MSP_ORG, prefix + "root_msp_1_user_1@arcserve.com", password, "firstName", "lastname");
		org4spogServer.setToken(spogServer.getJWTToken());
		org4spogServer.convertToRootMSP(root_msp_org_1);
		dto.setRoot_msp_org_1(root_msp_org_1);
		spogServer.userLogin(prefix + "root_msp_1_user_1@arcserve.com", password);
		dto.setRoot_msp_org_1_user_1(spogServer.GetLoggedinUser_UserID());
		dto.setRoot_msp_org_1_user_1_email(prefix + "root_msp_1_user_1@arcserve.com");
		String root_msp_org_1_user_2 = spogServer.createUserAndCheck(prefix + "root_msp_1_user_2@arcserve.com", password, "first_name", "last_name", SpogConstants.MSP_ADMIN, root_msp_org_1, test);
		dto.setRoot_msp_org_1_user_2(root_msp_org_1_user_2);
		dto.setRoot_msp_org_1_user_2_email(prefix + "root_msp_1_user_2@arcserve.com");
		
		// create monitor user for root msp organization #1
		String root_msp_org_1_monitor_user_1 = spogServer.createUserAndCheck(prefix + "root_msp_org_1_monitor_user_1@arcserve.com", password, "first_name", "last_name", SpogConstants.MSP_MONITOR, root_msp_org_1, test);
		dto.setRoot_msp_org_1_monitor_user_1(root_msp_org_1_monitor_user_1);
		dto.setRoot_msp_org_1_monitor_user_1_email(prefix + "root_msp_org_1_monitor_user_1@arcserve.com");
		
		String datacenterId = "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c";
		String root_msp_org_1_sub_msp_1 = org4spogServer.createSubMSPAccountincc(orgPrefix + "root_msp_1_sub_msp_1", root_msp_org_1, "firstName", "lastName", prefix + "root_msp_1_sub_msp_1_user_1@arcserve.com", datacenterId, test);
		dto.setRoot_msp_org_1_sub_msp_1(root_msp_org_1_sub_msp_1);

		spogServer.userLogin(csrUser, csrPassword);
		Response response = spogServer.GetUsersByOrganizationID(root_msp_org_1_sub_msp_1);
		String root_msp_org_1_sub_msp_1_user_1 = ((ArrayList<String>)response.then().extract().path("data.user_id")).get(0);
		spogServer.updateUserById(root_msp_org_1_sub_msp_1_user_1, "", password, "", "", "", "");
		
		spogServer.userLogin(prefix + "root_msp_1_sub_msp_1_user_1@arcserve.com", password);
		dto.setRoot_msp_org_1_sub_msp_1_user_1(spogServer.GetLoggedinUser_UserID());
		dto.setRoot_msp_org_1_sub_msp_1_user_1_email(prefix + "root_msp_1_sub_msp_1_user_1@arcserve.com");
		
		String root_msp_org_1_sub_msp_1_user_2 = spogServer.createUserAndCheck(prefix + "root_msp_1_sub_msp_1_user_2@arcserve.com", password, "first_name", "last_name", SpogConstants.MSP_ADMIN, root_msp_org_1_sub_msp_1, test);
		dto.setRoot_msp_org_1_sub_msp_1_user_2(root_msp_org_1_sub_msp_1_user_2);
		dto.setRoot_msp_org_1_sub_msp_1_user_2_email(prefix + "root_msp_1_sub_msp_1_user_2@arcserve.com");
		
		//create monitor user for root organization # 1, sub msp # 1
		String root_msp_org_1_sub_msp_1_monitor_user_1 = spogServer.createUserAndCheck(prefix + "root_msp_1_sub_msp_1_monitor_user_1@arcserve.com", password, "first_name", "last_name", SpogConstants.MSP_MONITOR, root_msp_org_1_sub_msp_1, test);
		dto.setRoot_msp_org_1_sub_msp_1_monitor_user_1(root_msp_org_1_sub_msp_1_monitor_user_1);
		dto.setRoot_msp_org_1_sub_msp_1_monitor_user_1_email(prefix + "root_msp_1_sub_msp_1_monitor_user_1@arcserve.com");
		
		spogServer.userLogin(prefix + "root_msp_1_user_1@arcserve.com", password);
		String root_msp_org_1_sub_msp_2 = org4spogServer.createSubMSPAccountincc(orgPrefix + "root_msp_1_sub_msp_2", root_msp_org_1, "firstName", "lastName", prefix + "root_msp_1_sub_msp_2_user_1@arcserve.com", datacenterId, test);
		dto.setRoot_msp_org_1_sub_msp_2(root_msp_org_1_sub_msp_2);

		spogServer.userLogin(csrUser, csrPassword);
		response = spogServer.GetUsersByOrganizationID(root_msp_org_1_sub_msp_2);
		String root_msp_org_1_sub_msp_2_user_1 = ((ArrayList<String>)response.then().extract().path("data.user_id")).get(0);
		spogServer.updateUserById(root_msp_org_1_sub_msp_2_user_1, "", password, "", "", "", "");
		
		spogServer.userLogin(prefix + "root_msp_1_sub_msp_2_user_1@arcserve.com", password);
		dto.setRoot_msp_org_1_sub_msp_2_user_1(spogServer.GetLoggedinUser_UserID());
		dto.setRoot_msp_org_1_sub_msp_2_user_1_email(prefix + "root_msp_1_sub_msp_2_user_1@arcserve.com");
		
		String root_msp_org_1_sub_msp_2_user_2 = spogServer.createUserAndCheck(prefix + "root_msp_1_sub_msp_2_user_2@arcserve.com", password, "first_name", "last_name", SpogConstants.MSP_ADMIN, root_msp_org_1_sub_msp_2, test);
		dto.setRoot_msp_org_1_sub_msp_2_user_2(root_msp_org_1_sub_msp_2_user_2);
		dto.setRoot_msp_org_1_sub_msp_2_user_2_email(prefix + "root_msp_1_sub_msp_2_user_2@arcserve.com");
		
		spogServer.userLogin(prefix + "root_msp_1_sub_msp_1_user_1@arcserve.com", password);
		String root_msp_org_1_sub_msp_1_account_1 = spogServer.createAccountWithCheck(root_msp_org_1_sub_msp_1, orgPrefix + "root_msp_org_1_sub_msp_1_account_1", root_msp_org_1_sub_msp_1);
		dto.setRoot_msp_org_1_sub_msp_1_account_1(root_msp_org_1_sub_msp_1_account_1);
		String root_msp_org_1_sub_msp_1_account_1_user_1 = spogServer.createUserAndCheck(prefix + "root_msp_org_1_sub_msp_1_account_1_user_1@arcserve.com", password, "first_name", "last_name", SpogConstants.DIRECT_ADMIN, root_msp_org_1_sub_msp_1_account_1, test);
		dto.setRoot_msp_org_1_sub_msp_1_account_1_user_1(root_msp_org_1_sub_msp_1_account_1_user_1);
		dto.setRoot_msp_org_1_sub_msp_1_account_1_user_1_email(prefix + "root_msp_org_1_sub_msp_1_account_1_user_1@arcserve.com");
		String root_msp_org_1_sub_msp_1_account_1_user_2 = spogServer.createUserAndCheck(prefix + "root_msp_org_1_sub_msp_1_account_1_user_2@arcserve.com", password, "first_name", "last_name", SpogConstants.DIRECT_ADMIN, root_msp_org_1_sub_msp_1_account_1, test);
		dto.setRoot_msp_org_1_sub_msp_1_account_1_user_2(root_msp_org_1_sub_msp_1_account_1_user_2);
		dto.setRoot_msp_org_1_sub_msp_1_account_1_user_2_email(prefix + "root_msp_org_1_sub_msp_1_account_1_user_2@arcserve.com");
		
		// create monitor user for root msp #1, sub msp #1,  account #1
		String root_msp_org_1_sub_msp_1_account_1_monitor_user_1 = spogServer.createUserAndCheck(prefix + "root_msp_org_1_sub_msp_1_account_1_monitor_user_1@arcserve.com", password, "first_name", "last_name", SpogConstants.DIRECT_MONITOR, root_msp_org_1_sub_msp_1_account_1, test);
		dto.setRoot_msp_org_1_sub_msp_1_account_1_monitor_user_1(root_msp_org_1_sub_msp_1_account_1_monitor_user_1);
		dto.setRoot_msp_org_1_sub_msp_1_account_1_monitor_user_1_email(prefix + "root_msp_org_1_sub_msp_1_account_1_monitor_user_1@arcserve.com");

		String root_msp_org_1_sub_msp_1_account_2 = spogServer.createAccountWithCheck(root_msp_org_1_sub_msp_1, orgPrefix + "root_msp_org_1_sub_msp_1_account_2", root_msp_org_1_sub_msp_1);
		dto.setRoot_msp_org_1_sub_msp_1_account_2(root_msp_org_1_sub_msp_1_account_2);
		String root_msp_org_1_sub_msp_1_account_2_user_1 = spogServer.createUserAndCheck(prefix + "root_msp_org_1_sub_msp_1_account_2_user_1@arcserve.com", password, "first_name", "last_name", SpogConstants.DIRECT_ADMIN, root_msp_org_1_sub_msp_1_account_2, test);
		dto.setRoot_msp_org_1_sub_msp_1_account_2_user_1(root_msp_org_1_sub_msp_1_account_2_user_1);
		dto.setRoot_msp_org_1_sub_msp_1_account_2_user_1_email(prefix + "root_msp_org_1_sub_msp_1_account_2_user_1@arcserve.com");
		String root_msp_org_1_sub_msp_1_account_2_user_2 = spogServer.createUserAndCheck(prefix + "root_msp_org_1_sub_msp_1_account_2_user_2@arcserve.com", password, "first_name", "last_name", SpogConstants.DIRECT_ADMIN, root_msp_org_1_sub_msp_1_account_2, test);
		dto.setRoot_msp_org_1_sub_msp_1_account_2_user_2(root_msp_org_1_sub_msp_1_account_2_user_2);
		dto.setRoot_msp_org_1_sub_msp_1_account_2_user_2_email(prefix + "root_msp_org_1_sub_msp_1_account_2_user_2@arcserve.com");
		
		spogServer.userLogin(prefix + "root_msp_1_sub_msp_2_user_1@arcserve.com", password);
		String root_msp_org_1_sub_msp_2_account_1 = spogServer.createAccountWithCheck(root_msp_org_1_sub_msp_2, orgPrefix + "root_msp_org_1_sub_msp_2_account_1", root_msp_org_1_sub_msp_2);
		dto.setRoot_msp_org_1_sub_msp_2_account_1(root_msp_org_1_sub_msp_2_account_1);
		String root_msp_org_1_sub_msp_2_account_1_user_1 = spogServer.createUserAndCheck(prefix + "root_msp_org_1_sub_msp_2_account_1_user_1@arcserve.com", password, "first_name", "last_name", SpogConstants.DIRECT_ADMIN, root_msp_org_1_sub_msp_2_account_1, test);
		dto.setRoot_msp_org_1_sub_msp_2_account_1_user_1(root_msp_org_1_sub_msp_2_account_1_user_1);
		dto.setRoot_msp_org_1_sub_msp_2_account_1_user_1_email(prefix + "root_msp_org_1_sub_msp_2_account_1_user_1@arcserve.com");
		
		spogServer.userLogin(prefix + "root_msp_1_user_1@arcserve.com", password);
		String root_msp_org_1_account_1 = spogServer.createAccountWithCheck(root_msp_org_1, orgPrefix + "root_msp_org_1_account_1", root_msp_org_1);
		dto.setRoot_msp_org_1_account_1(root_msp_org_1_account_1);
		String root_msp_org_1_account_1_user_1 = spogServer.createUserAndCheck(prefix + "root_msp_org_1_account_1_user_1@arcserve.com", password, "first_name", "last_name", SpogConstants.DIRECT_ADMIN, root_msp_org_1_account_1, test);
		dto.setRoot_msp_org_1_account_1_user_1(root_msp_org_1_account_1_user_1);
		dto.setRoot_msp_org_1_account_1_user_1_email(prefix + "root_msp_org_1_account_1_user_1@arcserve.com");
		String root_msp_org_1_account_1_user_2 = spogServer.createUserAndCheck(prefix + "root_msp_org_1_account_1_user_2@arcserve.com", password, "first_name", "last_name", SpogConstants.DIRECT_ADMIN, root_msp_org_1_account_1, test);
		dto.setRoot_msp_org_1_account_1_user_2(root_msp_org_1_account_1_user_2);
		dto.setRoot_msp_org_1_account_1_user_2_email(prefix + "root_msp_org_1_account_1_user_2@arcserve.com");
		
		// create monitor user for root msp #1 account #1
		String root_msp_org_1_account_1_monitor_user_1 = spogServer.createUserAndCheck(prefix + "root_msp_org_1_account_1_monitor_user_1@arcserve.com", password, "first_name", "last_name", SpogConstants.DIRECT_MONITOR, root_msp_org_1_account_1, test);
		dto.setRoot_msp_org_1_account_1_monitor_user_1(root_msp_org_1_account_1_monitor_user_1);
		dto.setRoot_msp_org_1_account_1_monitor_user_1_email(prefix + "root_msp_org_1_account_1_monitor_user_1@arcserve.com");

		String root_msp_org_1_account_2 = spogServer.createAccountWithCheck(root_msp_org_1, orgPrefix + "root_msp_org_1_account_2", root_msp_org_1);
		dto.setRoot_msp_org_1_account_2(root_msp_org_1_account_2);
		String root_msp_org_1_account_2_user_1 = spogServer.createUserAndCheck(prefix + "root_msp_org_1_account_2_user_1@arcserve.com", password, "first_name", "last_name", SpogConstants.DIRECT_ADMIN, root_msp_org_1_account_2, test);
		dto.setRoot_msp_org_1_account_2_user_1(root_msp_org_1_account_2_user_1);
		dto.setRoot_msp_org_1_account_2_user_1_email(prefix + "root_msp_org_1_account_2_user_1@arcserve.com");
		String root_msp_org_1_account_2_user_2 = spogServer.createUserAndCheck(prefix + "root_msp_org_1_account_2_user_2@arcserve.com", password, "first_name", "last_name", SpogConstants.DIRECT_ADMIN, root_msp_org_1_account_2, test);
		dto.setRoot_msp_org_1_account_2_user_2(root_msp_org_1_account_2_user_2);
		dto.setRoot_msp_org_1_account_2_user_2_email(prefix + "root_msp_org_1_account_2_user_2@arcserve.com");

		
		String root_msp_org_1_account_admin_1 = spogServer.createUserAndCheck(prefix + "root_msp_org_1_account_admin_1@arcserve.com", password, "first_name", "last_name", SpogConstants.MSP_ACCOUNT_ADMIN, root_msp_org_1, test);
		dto.setRoot_msp_org_1_account_admin_1(root_msp_org_1_account_admin_1);
		dto.setRoot_msp_org_1_account_admin_1_email(prefix + "root_msp_org_1_account_admin_1@arcserve.com");
		
		String root_msp_org_1_account_admin_2 = spogServer.createUserAndCheck(prefix + "root_msp_org_1_account_admin_2@arcserve.com", password, "first_name", "last_name", SpogConstants.MSP_ACCOUNT_ADMIN, root_msp_org_1, test);
		dto.setRoot_msp_org_1_account_admin_2(root_msp_org_1_account_admin_2);
		dto.setRoot_msp_org_1_account_admin_2_email(prefix + "root_msp_org_1_account_admin_2@arcserve.com");
		
		userIds = new String[] {root_msp_org_1_account_admin_1}; 
		userSpogServer.setToken(spogServer.getJWTToken());
		userSpogServer.assignMspAccountAdmins(root_msp_org_1, root_msp_org_1_account_1, userIds, spogServer.getJWTToken());

		spogServer.userLogin(dto.getRoot_msp_org_1_sub_msp_1_user_1_email(), password);
		String root_msp_org_1_sub_msp_1_account_admin_1 = spogServer.createUserAndCheck(prefix + "root_msp_org_1_sub_msp_1_account_admin_1@arcserve.com", password, "first_name", "last_name", SpogConstants.MSP_ACCOUNT_ADMIN, root_msp_org_1_sub_msp_1, test);
		dto.setRoot_msp_org_1_sub_msp_1_account_admin_1(root_msp_org_1_sub_msp_1_account_admin_1);
		dto.setRoot_msp_org_1_sub_msp_1_account_admin_1_email(prefix + "root_msp_org_1_sub_msp_1_account_admin_1@arcserve.com");
		
		String root_msp_org_1_sub_msp_1_account_admin_2 = spogServer.createUserAndCheck(prefix + "root_msp_org_1_sub_msp_1_account_admin_2@arcserve.com", password, "first_name", "last_name", SpogConstants.MSP_ACCOUNT_ADMIN, root_msp_org_1_sub_msp_1, test);
		dto.setRoot_msp_org_1_sub_msp_1_account_admin_2(root_msp_org_1_sub_msp_1_account_admin_2);
		dto.setRoot_msp_org_1_sub_msp_1_account_admin_2_email(prefix + "root_msp_org_1_sub_msp_1_account_admin_2@arcserve.com");
		
		userIds = new String[] {root_msp_org_1_sub_msp_1_account_admin_1}; 
		userSpogServer.setToken(spogServer.getJWTToken());
		userSpogServer.assignMspAccountAdmins(root_msp_org_1_sub_msp_1, root_msp_org_1_sub_msp_1_account_1, userIds, spogServer.getJWTToken());
		
		spogServer.userLogin(this.csrUser, this.csrPassword);
		String root_msp_org_2 = spogServer.CreateOrganizationWithCheck(orgPrefix + "root_msp_2", SpogConstants.MSP_ORG, prefix + "root_msp_2_user_1@arcserve.com", password, "firstName", "lastname");
		org4spogServer.setToken(spogServer.getJWTToken());
		org4spogServer.convertToRootMSP(root_msp_org_2);
		dto.setRoot_msp_org_2(root_msp_org_2);
		spogServer.userLogin(prefix + "root_msp_2_user_1@arcserve.com", password);
		dto.setRoot_msp_org_2_user_1(spogServer.GetLoggedinUser_UserID());
		dto.setRoot_msp_org_2_user_1_email(prefix + "root_msp_2_user_1@arcserve.com");

		String root_msp_org_2_sub_msp_1 = org4spogServer.createSubMSPAccountincc(orgPrefix + "root_msp_2_sub_msp_1", root_msp_org_1, "firstName", "lastName", prefix + "root_msp_2_sub_msp_1_user_1@arcserve.com", datacenterId, test);
		dto.setRoot_msp_org_2_sub_msp_1(root_msp_org_2_sub_msp_1);
		
		spogServer.userLogin(csrUser, csrPassword);
		response = spogServer.GetUsersByOrganizationID(root_msp_org_2_sub_msp_1);
		String root_msp_org_2_sub_msp_1_user_1 = ((ArrayList<String>)response.then().extract().path("data.user_id")).get(0);
		spogServer.updateUserById(root_msp_org_2_sub_msp_1_user_1, "", password, "", "", "", "");
		
		spogServer.userLogin(prefix + "root_msp_2_sub_msp_1_user_1@arcserve.com", password);
		dto.setRoot_msp_org_2_sub_msp_1_user_1(spogServer.GetLoggedinUser_UserID());
		dto.setRoot_msp_org_2_sub_msp_1_user_1_email(prefix + "root_msp_2_sub_msp_1_user_1@arcserve.com");
		
		spogServer.userLogin(dto.getRoot_msp_org_2_user_1_email(), password);
		String root_msp_org_2_account_1 = spogServer.createAccountWithCheck(root_msp_org_2, orgPrefix + "root_msp_org_2_account_1", root_msp_org_2);
		dto.setRoot_msp_org_2_account_1(root_msp_org_2_account_1);
		String root_msp_org_2_account_1_user_1 = spogServer.createUserAndCheck(prefix + "root_msp_org_2_account_1_user_1@arcserve.com", password, "first_name", "last_name", SpogConstants.DIRECT_ADMIN, root_msp_org_2_account_1, test);
		dto.setRoot_msp_org_2_account_1_user_1(root_msp_org_2_account_1_user_1);
		dto.setRoot_msp_org_2_account_1_user_1_email(prefix + "root_msp_org_2_account_1_user_1@arcserve.com");

		String root_msp_org_2_account_admin_1 = spogServer.createUserAndCheck(prefix + "root_msp_org_2_account_admin_1@arcserve.com", password, "first_name", "last_name", SpogConstants.MSP_ACCOUNT_ADMIN, root_msp_org_2, test);
		dto.setRoot_msp_org_2_account_admin_1(root_msp_org_2_account_admin_1);
		dto.setRoot_msp_org_2_account_admin_1_email(prefix + "root_msp_org_2_account_admin_1@arcserve.com");
		
		spogServer.userLogin(dto.getRoot_msp_org_2_sub_msp_1_user_1_email(), password);
		String root_msp_org_2_sub_msp_1_account_1 = spogServer.createAccountWithCheck(root_msp_org_2_sub_msp_1, orgPrefix + "root_msp_org_2_sub_msp_1_account_1", root_msp_org_2_sub_msp_1);
		dto.setRoot_msp_org_2_sub_msp_1_account_1(root_msp_org_2_sub_msp_1_account_1);
		String root_msp_org_2_sub_msp_1_account_1_user_1 = spogServer.createUserAndCheck(prefix + "root_msp_org_2_sub_msp_1_account_1_user_1@arcserve.com", password, "first_name", "last_name", SpogConstants.DIRECT_ADMIN, root_msp_org_2_sub_msp_1_account_1, test);
		dto.setRoot_msp_org_2_sub_msp_1_account_1_user_1(root_msp_org_2_sub_msp_1_account_1_user_1);
		dto.setRoot_msp_org_2_sub_msp_1_account_1_user_1_email(prefix + "root_msp_org_2_sub_msp_1_account_1_user_1@arcserve.com");
		
		String root_msp_org_2_sub_msp_1_account_admin_1 = spogServer.createUserAndCheck(prefix + "root_msp_org_2_sub_msp_1_account_admin_1@arcserve.com", password, "first_name", "last_name", SpogConstants.MSP_ACCOUNT_ADMIN, root_msp_org_2_sub_msp_1, test);
		dto.setRoot_msp_org_2_sub_msp_1_account_admin_1(root_msp_org_2_sub_msp_1_account_admin_1);
		dto.setRoot_msp_org_2_sub_msp_1_account_admin_1_email(prefix + "root_msp_org_2_sub_msp_1_account_admin_1@arcserve.com");
		
		return dto;
	}
}






















