package api.cloudaccounts;

import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.sun.mail.handlers.message_rfc822;

import Constants.SpogConstants;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import bsh.org.objectweb.asm.Constants;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import groovyjarjarantlr.PreservingFileWriter;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import org.apache.commons.lang.ObjectUtils.Null;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;

import static org.hamcrest.Matchers.equalTo;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import Constants.SpogConstants;

import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import Constants.SourceType;

public class CreateCloudAccountTest {

	private SPOGServer spogServer;
	private UserSpogServer userSpogServer;
	private String csrAdmin;
	private String csrPwd;
	private ExtentReports rep;
	private ExtentTest test;

	private String initial_msp_org_name = "spog_qa_msp_zhaoguo";
	private String initial_msp_email = "spog_qa_msp_zhaoguo@arcserve.com";
	private String initial_msp_email_full = "";
	private String initial_msp_first_name = "spog_qa_msp_ma";
	private String initial_msp_last_name = "spog_qa_msp_zhaoguo";
	
	private String initial_msp_email_added = "spog_qa_msp_zhaoguo_added@arcserve.com";
	private String initial_msp_email_full_added = "";
	private String initial_msp_first_name_added = "spog_qa_msp_ma";
	private String initial_msp_last_name_added = "spog_qa_msp_zhaoguo";

	private String initial_msp_account_email = "spog_qa_msp_account_zhaoguo@arcserve.com";
	private String initial_msp_account_email_full = "";
	private String initial_msp_account_first_name = "spog_qa_msp_ma";
	private String initial_msp_account_last_name = "spog_qa_msp_zhaoguo";
	private String initial_msp_account_userID = "";

	private String initial_direct_org_name = "spog_qa_direct_zhaoguo";
	private String initial_direct_email = "spog_qa_direct_zhaoguo@arcserve.com";
	private String initial_direct_email_full = "";
	private String initial_direct_first_name = "spog_qa_direct_ma";
	private String initial_direct_last_name = "spog_qa_direct_zhaoguo2";
	
	private String initial_direct_email_added = "spog_qa_direct_zhaoguo_added@arcserve.com";
	private String initial_direct_email_full_added = "";
	private String initial_direct_first_name_added = "spog_qa_direct_ma";
	private String initial_direct_last_name_added = "spog_qa_direct_zhaoguo2";

	private String initial_sub_org_name_a = "spog_qa_sub_zhaoguo_a";
	private String initial_sub_email_a = "spog_qa_sub_zhaoguo_a@arcserve.com";
	private String initial_sub_email_full_a = "";
	private String initial_sub_first_name_a = "spog_qa_sub_ma_a";
	private String initial_sub_last_name_a = "spog_qa_sub_zhaoguo_a";
	
	private String initial_sub_email_a_added = "spog_qa_sub_zhaoguo_a_added@arcserve.com";
	private String initial_sub_email_full_a_added = "";
	private String initial_sub_first_name_a_added = "spog_qa_sub_ma_a";
	private String initial_sub_last_name_a_added = "spog_qa_sub_zhaoguo_a";
	
	private String initial_sub_org_name_b = "spog_qa_sub_zhaoguo_b";
	private String initial_sub_email_b = "spog_qa_sub_zhaoguo_b@arcserve.com";
	private String initial_sub_email_full_b = "";
	private String initial_sub_first_name_b = "spog_qa_sub_ma_b";
	private String initial_sub_last_name_b = "spog_qa_sub_zhaoguo_b";
	
	private String initial_sub_email_b_added = "spog_qa_sub_zhaoguo_b_added@arcserve.com";
	private String initial_sub_email_full_b_added = "";
	private String initial_sub_first_name_b_added = "spog_qa_sub_ma_b";
	private String initial_sub_last_name_b_added = "spog_qa_sub_zhaoguo_b";

	private String initial_msp_orgID;
	private String initial_direct_orgID;
	private String initial_sub_orgID_a;
	private String initial_sub_orgID_b;
	
	private String password = "Pa$$w0rd";

	private SQLServerDb bqdb1;
	public int Nooftest;
	private long creationTime;
	private String BQName = null;
	private String runningMachine;
	private testcasescount count1;
	private String buildVersion;
	private String uuid1 = UUID.randomUUID().toString();
	private String uuid2 = UUID.randomUUID().toString();
	String datacenterID = "99a9b48e-6ac6-4c47-8202-614b5cdcfe0c";

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
		userSpogServer = new UserSpogServer(baseURI, port);
		this.csrAdmin = csrAdminUserName;
		this.csrPwd = csrAdminPassword;
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		String prefix = RandomStringUtils.randomAlphanumeric(8);

		// create msp organization #1, and then 1 sub-organizations in it, create one
		// admin for the sub organization;
		this.initial_msp_email_full = prefix + this.initial_msp_email;
		this.initial_msp_orgID = spogServer.CreateOrganizationWithCheck(prefix + initial_msp_org_name,
				SpogConstants.MSP_ORG, prefix + initial_msp_email, password, prefix + initial_msp_first_name,
				prefix + initial_msp_last_name);
		
		this.initial_msp_email_full_added = prefix + this.initial_msp_email_added;
		spogServer.createUserAndCheck(initial_msp_email_full_added, password, prefix+ initial_msp_first_name_added, 
				prefix+ initial_msp_last_name, "msp_admin", initial_msp_orgID, test);

		this.initial_sub_orgID_a = spogServer.createAccountWithCheck(initial_msp_orgID, initial_sub_org_name_a,
				initial_msp_orgID);

		spogServer.userLogin(initial_msp_email_full, password);
		this.initial_sub_email_full_a = prefix + initial_sub_email_a;
		spogServer.createUserAndCheck(prefix + this.initial_sub_email_a, password,
				prefix + this.initial_sub_first_name_a, prefix + this.initial_sub_last_name_a,
				SpogConstants.DIRECT_ADMIN, initial_sub_orgID_a, test);
		
		this.initial_sub_email_full_a_added = prefix + this.initial_sub_email_a_added;
		spogServer.createUserAndCheck(prefix + this.initial_sub_email_a_added, password,
				prefix + this.initial_sub_first_name_a_added, prefix + this.initial_sub_last_name_a_added,
				SpogConstants.DIRECT_ADMIN, initial_sub_orgID_a, test);
		
		this.initial_sub_orgID_b = spogServer.createAccountWithCheck(initial_msp_orgID, initial_sub_org_name_b,
				initial_msp_orgID);

		spogServer.userLogin(initial_msp_email_full, password);
		this.initial_sub_email_full_b = prefix + initial_sub_email_b;
		spogServer.createUserAndCheck(prefix + this.initial_sub_email_b, password,
				prefix + this.initial_sub_first_name_b, prefix + this.initial_sub_last_name_b,
				SpogConstants.DIRECT_ADMIN, initial_sub_orgID_b, test);
		
		this.initial_sub_email_full_b_added = prefix + this.initial_sub_email_b_added;
		spogServer.createUserAndCheck(prefix + this.initial_sub_email_b_added, password,
				prefix + this.initial_sub_first_name_b_added, prefix + this.initial_sub_last_name_b_added,
				SpogConstants.DIRECT_ADMIN, initial_sub_orgID_b, test);

		this.initial_msp_account_email_full = prefix + initial_msp_account_email;
		this.initial_msp_account_userID = spogServer.createUserAndCheck(initial_msp_account_email_full, password, initial_msp_account_first_name, 
				initial_msp_account_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, initial_msp_orgID, test);
		String[] userIds = new String[] {initial_msp_account_userID}; 
		userSpogServer.assignMspAccountAdmins(initial_msp_orgID, initial_sub_orgID_a, userIds, spogServer.getJWTToken());
		
		// create 1 direct organizations;
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		this.initial_direct_email_full = prefix + this.initial_direct_email;
		this.initial_direct_orgID = spogServer.CreateOrganizationWithCheck(prefix + initial_direct_org_name,
				SpogConstants.DIRECT_ORG, prefix + initial_direct_email, password, prefix + initial_direct_first_name,
				prefix + initial_direct_last_name);
		
		this.initial_direct_email_full_added = prefix + this.initial_direct_email_added;
		spogServer.createUserAndCheck(prefix + this.initial_direct_email_added, password,
				prefix + this.initial_direct_first_name_added, prefix + this.initial_direct_last_name_added,
				SpogConstants.DIRECT_ADMIN, initial_direct_orgID, test);
		
	}
	
	@Test
	public void mspAccountAdminTest() {
		spogServer.userLogin(initial_msp_account_email_full, password);
		spogServer.createCloudAccountWithCodeCheck("cloudAccountKey", "cloudAccountSecret", "cloudAccountName", "aws_s3", 
				initial_msp_orgID, "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_", datacenterID, 403, "00100101", test);
		spogServer.createCloudAccountWithCodeCheck("cloudAccountKey", "cloudAccountSecret", "cloudAccountName", "aws_s3", 
				initial_sub_orgID_b, "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_", datacenterID, 403, "00100101", test);
		spogServer.createCloudAccountWithCodeCheck("cloudAccountKey", "cloudAccountSecret", "cloudAccountName", "aws_s3", 
				initial_direct_orgID, "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_", datacenterID, 403, "00100101", test);
		spogServer.createCloudAccountWithCheck("cloudAccountKey", "cloudAccountSecret", "cloudAccountName", "aws_s3", 
				initial_sub_orgID_a, "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_", datacenterID, test);
	}
	
	@DataProvider(name = "create_cloud_account_valid")
	public final Object[][] createCloudAccountValidParams() {
		return new Object[][] {
			// different organization_type / cloud_account_type
			{"msp", "email@arcserve.com", "cloudAccountKey", "cloudAccountSecret", "cloudAccountName", "aws_s3", "organizationID", "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_"},
//			{"msp", "email@arcserve.com", "cloudAccountKey", "cloudAccountSecret", "cloudAccountName", "cloud_direct", "organizationID", "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_"},
			{"direct", "email@arcserve.com", "cloudAccountKey", "cloudAccountSecret", "cloudAccountName", "aws_s3", "organizationID", "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_"},
//			{"direct", "email@arcserve.com", "cloudAccountKey", "cloudAccountSecret", "cloudAccountName", "cloud_direct", "organizationID", "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_"},
			{"child", "email@arcserve.com", "cloudAccountKey", "cloudAccountSecret", "cloudAccountName", "aws_s3", "organizationID", "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_"},
			
			// different organization_id
			{"direct", "email@arcserve.com", "cloudAccountKey", "cloudAccountSecret", "cloudAccountName", "aws_s3", "", "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_"},
			{"direct", "email@arcserve.com", "cloudAccountKey", "cloudAccountSecret", "cloudAccountName", "aws_s3", null, "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_"},
			{"direct", "email@arcserve.com", "cloudAccountKey", "cloudAccountSecret", "cloudAccountName", "aws_s3", "none", "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_"},
			
			// different cloud_account_key / cloud_account secret
			{"direct", "email@arcserve.com", "cloudAccountKey", "cloudAccountSecret", "cloudAccountName", "aws_s3", "organizationID", "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_"},
//			{"direct", "email@arcserve.com", "", "", "cloudAccountName", "cloud_direct", "organizationID", "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_"},
//			{"direct", "email@arcserve.com", "none", "none", "cloudAccountName", "cloud_direct", "organizationID", "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_"},
//			{"direct", "email@arcserve.com", null, null, "cloudAccountName", "cloud_direct", "organizationID", "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_"},
//			{"direct", "email@arcserve.com", "cloudAccountKey", "cloudAccountSecret", "cloudAccountName", "cloud_direct", "organizationID", "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_"},
			
			// different order_id / fulfillment_id
//			{"direct", "email@arcserve.com", "cloudAccountKey", "cloudAccountSecret", "cloudAccountName", "cloud_direct", "organizationID", "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_"},

			{"direct", "email@arcserve.com", "cloudAccountKey", "cloudAccountSecret", "cloudAccountName", "aws_s3", "organizationID", "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_"},
			{"direct", "email@arcserve.com", "cloudAccountKey", "cloudAccountSecret", "cloudAccountName", "aws_s3", "organizationID", "", ""},
			{"direct", "email@arcserve.com", "cloudAccountKey", "cloudAccountSecret", "cloudAccountName", "aws_s3", "organizationID", "none", "none"},
			{"direct", "email@arcserve.com", "cloudAccountKey", "cloudAccountSecret", "cloudAccountName", "aws_s3", "organizationID", null, null},
		
		};
	}

	@Test(dataProvider = "create_cloud_account_valid")
	public void createCloudAccountValid(String organizationType, String userEmail, String cloudAccountKey,
			String cloudAccountSecret, String cloudAccountName, String cloudAccountType, String organizationID,
			String orderID, String fulfillmentID) {
		spogServer.userLogin(csrAdmin, csrPwd);
		String prefix = RandomStringUtils.randomAlphanumeric(8);

		String organizationName = prefix + "organizationName";
		String organizationEmail = prefix + userEmail;
		String organizationPwd = "Passw0rd";
		String organizationFirstName = "Ma";
		String organizationLastName = "Zhaoguo";
		String createOrgID = "";

		if (organizationType.equalsIgnoreCase("child")) {
			String mspOrgID = spogServer.CreateOrganizationWithCheck("msp" + organizationName, "msp",
					"msp" + organizationEmail, organizationPwd, organizationFirstName, organizationLastName, test);
			createOrgID = spogServer.createAccountWithCheck(mspOrgID, organizationName, mspOrgID);
			spogServer.createUserAndCheck(organizationEmail, organizationPwd, "Ma", "Zhaoguo",
					SpogConstants.DIRECT_ADMIN, createOrgID, test);
		} else {
			createOrgID = spogServer.CreateOrganizationWithCheck(organizationName, organizationType, organizationEmail,
					organizationPwd, organizationFirstName, organizationLastName, test);
		}

		spogServer.userLogin(organizationEmail, organizationPwd);

		if (!(null == orderID) && !("" == orderID) && !(orderID.equalsIgnoreCase("none"))) {
			orderID = orderID + prefix;
		}

		if (!(null == fulfillmentID) && !("" == fulfillmentID) && !(fulfillmentID.equalsIgnoreCase("none"))) {
			fulfillmentID = fulfillmentID + prefix;
		}

		if (organizationID == null || organizationID == "" || organizationID.equalsIgnoreCase("none")) {
			spogServer.createCloudAccountWithCheck(cloudAccountKey, cloudAccountSecret, cloudAccountName,
					cloudAccountType, organizationID, orderID, fulfillmentID, test);
		} else {
			spogServer.createCloudAccountWithCheck(cloudAccountKey, cloudAccountSecret, cloudAccountName,
					cloudAccountType, createOrgID, orderID, fulfillmentID, test);
		}
	}
	
	@DataProvider(name = "create_cloud_account_invalid")
	public final Object[][] createCloudAccountInvalidParams() {
		return new Object[][] {
			// invalid cloud_account_type
			{"msp", "email@arcserve.com", "cloudAccountKey", "cloudAccountSecret", "cloudAccountName", "cloud_account_type", "organizationID", "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_", 400, "40000006"},
			{"direct", "email@arcserve.com", "cloudAccountKey", "cloudAccountSecret", "cloudAccountName", "cloud_account_type", "organizationID", "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_", 400, "40000006"},
			{"child", "email@arcserve.com", "cloudAccountKey", "cloudAccountSecret", "cloudAccountName", "cloud_account_type", "organizationID", "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_", 400, "40000006"},
			// child organization cannot create cloud_direct account, it seems the design changed. 2018/02/23 
			{"child", "email@arcserve.com", "cloudAccountKey", "cloudAccountSecret", "cloudAccountName", "cloud_direct", "organizationID", "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_", 403, "00B00004"},
			
			// invalid cloud account name
			{"direct", "email@arcserve.com", "cloudAccountKey", "cloudAccountSecret", "", "cloud_direct", "organizationID", "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_", 400, "40000001"},
			{"direct", "email@arcserve.com", "cloudAccountKey", "cloudAccountSecret", "none", "cloud_direct", "organizationID", "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_", 400, "40000001"},
			{"direct", "email@arcserve.com", "cloudAccountKey", "cloudAccountSecret", null, "cloud_direct", "organizationID", "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_", 400, "40000001"},
			{"direct", "email@arcserve.com", "cloudAccountKey", "cloudAccountSecret", "none", "aws_s3", "organizationID", "none", "none", 400, "40000001"},

			// invalid cloud account key/secret
			{"direct", "email@arcserve.com", "", "cloudAccountSecret", "cloudAccountName", "aws_s3", "organizationID", "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_", 400, "00B00008"},
			{"direct", "email@arcserve.com", "none", "cloudAccountSecret", "cloudAccountName", "aws_s3", "organizationID", "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_", 400, "00B00008"},
			{"direct", "email@arcserve.com", null, "cloudAccountSecret", "cloudAccountName", "aws_s3", "organizationID", "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_", 400, "00B00008"},
			{"direct", "email@arcserve.com", "cloudAccountKey", "", "cloudAccountName", "aws_s3", "organizationID", "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_", 400, "00B00008"},
			{"direct", "email@arcserve.com", "cloudAccountKey", "none", "cloudAccountName", "aws_s3", "organizationID", "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_", 400, "00B00008"},
			{"direct", "email@arcserve.com", "cloudAccountKey", null,"cloudAccountName", "aws_s3", "organizationID", "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_", 400, "00B00008"},

			// invalid order_id/fulfillnumber_id
			{"direct", "email@arcserve.com", "cloudAccountKey", "cloudAccountSecret", "cloudAccountName", "cloud_direct", "organizationID", "order_id", "SKUTESTDATA_1_0_0_0_", 400, "00B0000B"},
//			{"direct", "email@arcserve.com", "cloudAccountKey", "cloudAccountSecret", "cloudAccountName", "cloud_direct", "organizationID", "SKUTESTDATA_1_0_0_0_", "fulfillnumber_id", 400, "40000001"},
			
			{"direct", "email@arcserve.com", "cloudAccountKey", "cloudAccountSecret", "cloudAccountName", "cloud_direct", "organizationID", "", "", 400, "00B0000A"},
			{"direct", "email@arcserve.com", "cloudAccountKey", "cloudAccountSecret", "cloudAccountName", "cloud_direct", "organizationID", "none", "none", 400, "00B0000A"},
			{"direct", "email@arcserve.com", "cloudAccountKey", "cloudAccountSecret", "cloudAccountName", "cloud_direct", "organizationID", null, null, 400, "00B0000A"},
		
			// invalid organization_id
			{"direct", "email@arcserve.com", "cloudAccountKey", "cloudAccountSecret", "cloudAccountName", "cloud_direct", UUID.randomUUID().toString(), "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_", 404, "0030000A"},
			{"direct", "email@arcserve.com", "cloudAccountKey", "cloudAccountSecret", "cloudAccountName", "cloud_direct", "invalidUUID", "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_", 400, "40000005"},
		};}
	
	@Test(dataProvider = "create_cloud_account_invalid")
	public void createCloudAccountInvalid(String organizationType, String userEmail, String cloudAccountKey,
			String cloudAccountSecret, String cloudAccountName, String cloudAccountType, String organizationID,
			String orderID, String fulfillmentID, int statusCode, String errorCode) {
		spogServer.userLogin(csrAdmin, csrPwd);
		String prefix = RandomStringUtils.randomAlphanumeric(8);

		String organizationName = prefix + "organizationName";
		String organizationEmail = prefix + userEmail;
		String organizationPwd = "Passw0rd";
		String organizationFirstName = "Ma";
		String organizationLastName = "Zhaoguo";
		String createOrgID = "";

		if (organizationType.equalsIgnoreCase("child")) {
			String mspOrgID = spogServer.CreateOrganizationWithCheck("msp" + organizationName, "msp",
					"msp" + organizationEmail, organizationPwd, organizationFirstName, organizationLastName, test);
			createOrgID = spogServer.createAccountWithCheck(mspOrgID, organizationName, mspOrgID);
			spogServer.createUserAndCheck(organizationEmail, organizationPwd, "Ma", "Zhaoguo",
					SpogConstants.DIRECT_ADMIN, createOrgID, test);
		} else {
			createOrgID = spogServer.CreateOrganizationWithCheck(organizationName, organizationType, organizationEmail,
					organizationPwd, organizationFirstName, organizationLastName, test);
		}

		spogServer.userLogin(organizationEmail, organizationPwd);

		if (!(null == orderID) && !("" == orderID) && !(orderID.equalsIgnoreCase("none"))) {
			orderID = orderID + prefix;
		}

		if (!(null == fulfillmentID) && !("" == fulfillmentID) && !(fulfillmentID.equalsIgnoreCase("none"))) {
			fulfillmentID = fulfillmentID + prefix;
		}

		if (organizationID == null || organizationID == "" || organizationID.equalsIgnoreCase("none")) {
			spogServer.createCloudAccountWithCodeCheck(cloudAccountKey, cloudAccountSecret, cloudAccountName,
					cloudAccountType, organizationID, orderID, fulfillmentID, statusCode, errorCode, test);
		} else if(organizationID.equalsIgnoreCase("organizationID")) {
			spogServer.createCloudAccountWithCodeCheck(cloudAccountKey, cloudAccountSecret, cloudAccountName,
					cloudAccountType, createOrgID, orderID, fulfillmentID, statusCode, errorCode, test);
		} else {
			spogServer.createCloudAccountWithCodeCheck(cloudAccountKey, cloudAccountSecret, cloudAccountName,
					cloudAccountType, organizationID, orderID, fulfillmentID, statusCode, errorCode, test);
		}
	}
	
	@DataProvider(name =  "create_cloud_account_duplicate_name")
	public final Object[][] createCloudAccountduplicateNameParams(){
		return new Object[][] {
			{"msp", "email@arcserve.com", "cloudAccountKey", "cloudAccountSecret", "cloudAccountName", "aws_s3", "aws_s3", "none", "none", 400, "00B00001"},
			{"direct", "email@arcserve.com", "cloudAccountKey", "cloudAccountSecret", "cloudAccountName", "aws_s3", "aws_s3", "none", "none", 400, "00B00001"},
//			{"msp", "email@arcserve.com", "cloudAccountKey", "cloudAccountSecret", "cloudAccountName", "aws_s3", "cloud_direct", "none", "none", 400, "00B00001"},
//			{"direct", "email@arcserve.com", "cloudAccountKey", "cloudAccountSecret", "cloudAccountName", "aws_s3", "cloud_direct", "none", "none", 400, "00B00001"},
//			{"msp", "email@arcserve.com", "cloudAccountKey", "cloudAccountSecret", "cloudAccountName", "cloud_direct", "aws_s3", "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_", 400, "00B00001"},
//			{"direct", "email@arcserve.com", "cloudAccountKey", "cloudAccountSecret", "cloudAccountName", "cloud_direct", "aws_s3", "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_", 400, "00B00001"},
			{"child", "email@arcserve.com", "cloudAccountKey", "cloudAccountSecret", "cloudAccountName", "aws_s3", "aws_s3", "none", "none", 400, "00B00001"},
		};
	}
	@Test(dataProvider = "create_cloud_account_duplicate_name")
	public void createDuplicateCloudAccountTest(String organizationType, String userEmail, String cloudAccountKey,
			String cloudAccountSecret, String cloudAccountName, String cloudAccountType, String cloudAccountTypeAdd,
			String orderID, String fulfillmentID, int statusCode, String errorCode) {
		spogServer.userLogin(csrAdmin, csrPwd);
		String prefix = RandomStringUtils.randomAlphanumeric(8);

		String organizationName = prefix + "organizationName";
		String organizationEmail = prefix + userEmail;
		String organizationPwd = "Passw0rd";
		String organizationFirstName = "Ma";
		String organizationLastName = "Zhaoguo";
		String createOrgID = "";

		if (organizationType.equalsIgnoreCase("child")) {
			String mspOrgID = spogServer.CreateOrganizationWithCheck("msp" + organizationName, "msp",
					"msp" + organizationEmail, organizationPwd, organizationFirstName, organizationLastName, test);
			createOrgID = spogServer.createAccountWithCheck(mspOrgID, organizationName, mspOrgID);
			spogServer.createUserAndCheck(organizationEmail, organizationPwd, "Ma", "Zhaoguo",
					SpogConstants.DIRECT_ADMIN, createOrgID, test);
		} else {
			createOrgID = spogServer.CreateOrganizationWithCheck(organizationName, organizationType, organizationEmail,
					organizationPwd, organizationFirstName, organizationLastName, test);
		}

		spogServer.userLogin(organizationEmail, organizationPwd);

		if (!(null == orderID) && !("" == orderID) && !(orderID.equalsIgnoreCase("none"))) {
			orderID = orderID + prefix;
		}

		if (!(null == fulfillmentID) && !("" == fulfillmentID) && !(fulfillmentID.equalsIgnoreCase("none"))) {
			fulfillmentID = fulfillmentID + prefix;
		}

		spogServer.createCloudAccountWithCheck(cloudAccountKey, cloudAccountSecret, cloudAccountName,
				cloudAccountType, createOrgID, orderID, fulfillmentID, test);
		spogServer.createCloudAccountWithCodeCheck(cloudAccountKey, cloudAccountSecret, cloudAccountName,
				cloudAccountTypeAdd, createOrgID, orderID, fulfillmentID, statusCode, errorCode, test);
	}
	
	@DataProvider(name =  "create_cloud_account_no_token")
	public final Object[][] createCloudAccountNoTokenParams(){
		return new Object[][] {
			{"msp", "email@arcserve.com", "cloudAccountKey", "cloudAccountSecret", "cloudAccountName", "aws_s3", "aws_s3", "none", "none", 401, "00900006"},
			{"direct", "email@arcserve.com", "cloudAccountKey", "cloudAccountSecret", "cloudAccountName", "aws_s3", "aws_s3", "none", "none", 401, "00900006"},
			{"child", "email@arcserve.com", "cloudAccountKey", "cloudAccountSecret", "cloudAccountName", "aws_s3", "aws_s3", "none", "none", 401, "00900006"},
		};
	}
	
	@Test(dataProvider = "create_cloud_account_no_token")
	public void createCloudAccountNoTokenTest(String organizationType, String userEmail, String cloudAccountKey,
			String cloudAccountSecret, String cloudAccountName, String cloudAccountType, String organizationID,
			String orderID, String fulfillmentID, int statusCode, String errorCode) {
		spogServer.userLogin(csrAdmin, csrPwd);
		String prefix = RandomStringUtils.randomAlphanumeric(8);

		String organizationName = prefix + "organizationName";
		String organizationEmail = prefix + userEmail;
		String organizationPwd = "Passw0rd";
		String organizationFirstName = "Ma";
		String organizationLastName = "Zhaoguo";
		String createOrgID = "";

		if (organizationType.equalsIgnoreCase("child")) {
			String mspOrgID = spogServer.CreateOrganizationWithCheck("msp" + organizationName, "msp",
					"msp" + organizationEmail, organizationPwd, organizationFirstName, organizationLastName, test);
			createOrgID = spogServer.createAccountWithCheck(mspOrgID, organizationName, mspOrgID);
			spogServer.createUserAndCheck(organizationEmail, organizationPwd, "Ma", "Zhaoguo",
					SpogConstants.DIRECT_ADMIN, createOrgID, test);
		} else {
			createOrgID = spogServer.CreateOrganizationWithCheck(organizationName, organizationType, organizationEmail,
					organizationPwd, organizationFirstName, organizationLastName, test);
		}

		spogServer.userLogin(organizationEmail, organizationPwd);

		if (!(null == orderID) && !("" == orderID) && !(orderID.equalsIgnoreCase("none"))) {
			orderID = orderID + prefix;
		}

		if (!(null == fulfillmentID) && !("" == fulfillmentID) && !(fulfillmentID.equalsIgnoreCase("none"))) {
			fulfillmentID = fulfillmentID + prefix;
		}
		spogServer.setToken("");
		
		if (organizationID == null || organizationID == "" || organizationID.equalsIgnoreCase("none")) {
			spogServer.createCloudAccountWithCodeCheck(cloudAccountKey, cloudAccountSecret, cloudAccountName,
					cloudAccountType, organizationID, orderID, fulfillmentID, statusCode, errorCode, test);
		} else if(organizationID.equalsIgnoreCase("organizationID")) {
			spogServer.createCloudAccountWithCodeCheck(cloudAccountKey, cloudAccountSecret, cloudAccountName,
					cloudAccountType, createOrgID, orderID, fulfillmentID, statusCode, errorCode, test);
		} else {
			spogServer.createCloudAccountWithCodeCheck(cloudAccountKey, cloudAccountSecret, cloudAccountName,
					cloudAccountType, organizationID, orderID, fulfillmentID, statusCode, errorCode, test);
		}
	}
	
	@DataProvider(name =  "create_cloud_account_auth_error")
	public final Object[][] createCloudAccountAuthErrorParams(){
		return new Object[][] {
			{initial_direct_email_full, password, initial_msp_orgID, 403, "00100101"},
			{initial_direct_email_full, password, initial_sub_orgID_a, 403, "00100101"},
			{initial_msp_email_full, password, initial_direct_orgID, 403, "00100101"},
			{initial_sub_email_full_a, password, initial_msp_orgID, 403, "00100101"},
			{initial_sub_email_full_a, password, initial_direct_orgID, 403, "00100101"},
		};
	}
	
	@Test(dataProvider = "create_cloud_account_auth_error")
	public void createCloudAccountAuthError(String username, String password, String organizationID, int statusCode, String errorCode) {
		spogServer.userLogin(username, password);
		spogServer.createCloudAccountWithCodeCheck("cloudAccountKey", "cloudAccountSecret", "cloudAccountName", 
				"aws_s3", organizationID, "none", "none", statusCode, errorCode, test);
	}
	

	@Test 
	public void createMultipleCloudAccount() {
		
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		String organizationEmail = prefix+  "email@arcserve.com";
		String organizationName = prefix + "organizationName";
		String organizationPwd = "Passw0rd";
		String organizationFirstName = "Ma";
		String organizationLastName = "Zhaoguo";
		
		String cloudAccountKey = "cloudAccountKey";
		String cloudAccountSecret = "cloudAccountSecret";
		String cloudAccountName = "cloudAccountName";
		
		String mspOrganizationID  = spogServer.CreateOrganizationWithCheck("msp" + organizationName, SpogConstants.MSP_ORG, 
				"msp" + organizationEmail, organizationPwd, organizationFirstName, organizationLastName, test);
		String directOrganizationID = spogServer.CreateOrganizationWithCheck("direct" + organizationName, SpogConstants.DIRECT_ORG, 
				"direct" + organizationEmail, organizationPwd, organizationFirstName, organizationLastName, test);
		
		spogServer.userLogin("msp" + organizationEmail, organizationPwd);
		String childOrganizationID1 = spogServer.createAccountWithCheck(mspOrganizationID, "child1" + organizationName, mspOrganizationID, test);
		String childOrganizationID2 = spogServer.createAccountWithCheck(mspOrganizationID, "child1" + organizationName, mspOrganizationID, test);
		
		spogServer.userLogin(csrAdmin, csrPwd);
		String mspCloudAccountS3 = spogServer.createCloudAccountWithCheck(cloudAccountKey, cloudAccountSecret, "msp_s3" + cloudAccountName, 
				"aws_s3", mspOrganizationID, "none", "none", test);
//		String mspCloudAccountDirect = spogServer.createCloudAccountWithCheck(cloudAccountKey, cloudAccountSecret, "msp_direct" + cloudAccountName, 
//				"cloud_direct", mspOrganizationID, spogServer.ReturnRandom("SKUTESTDATA_1_0_0_0_"), spogServer.ReturnRandom("SKUTESTDATA_1_0_0_0_"), test);
		
		String directCloudAccountS3 = spogServer.createCloudAccountWithCheck(cloudAccountKey, cloudAccountSecret, "direct_s3" + cloudAccountName, 
				"aws_s3", directOrganizationID, "none", "none", test);
//		String directCloudAccountDirect = spogServer.createCloudAccountWithCheck(cloudAccountKey, cloudAccountSecret, "direct_direct" + cloudAccountName, 
//				"cloud_direct", directOrganizationID, spogServer.ReturnRandom("SKUTESTDATA_1_0_0_0_"), spogServer.ReturnRandom("SKUTESTDATA_1_0_0_0_"), test);
		
		// create more aws_s3 account should succeed;
		String mspCloudAccountS3_added = spogServer.createCloudAccountWithCheck(cloudAccountKey, cloudAccountSecret, "msp_s3_added" + cloudAccountName, 
				"aws_s3", mspOrganizationID, "none", "none", test);
		// create another cloud_direct account should fail;
//		spogServer.createCloudAccountWithCodeCheck(cloudAccountKey, cloudAccountSecret, "msp_direct_added" + cloudAccountName, 
//				"cloud_direct", mspOrganizationID, spogServer.ReturnRandom("SKUTESTDATA_1_0_0_0_"), spogServer.ReturnRandom("SKUTESTDATA_1_0_0_0_"), 400, "00B00009", test);
		
		String directCloudAccountS3_added = spogServer.createCloudAccountWithCheck(cloudAccountKey, cloudAccountSecret, "direct_s3_added" + cloudAccountName, 
				"aws_s3", directOrganizationID, "none", "none", test);
//		spogServer.createCloudAccountWithCodeCheck(cloudAccountKey, cloudAccountSecret, "direct_direct_added" + cloudAccountName, 
//				"cloud_direct", directOrganizationID, spogServer.ReturnRandom("SKUTESTDATA_1_0_0_0_"), spogServer.ReturnRandom("SKUTESTDATA_1_0_0_0_"), 400, "00B00009", test);
		
		//only one cloud_direct can be added to one organization, it cannot add even though original one is deleted; 
//		spogServer.deleteCloudAccount(mspCloudAccountDirect, test);
//		spogServer.deleteCloudAccount(directCloudAccountDirect, test);
		
//		spogServer.createCloudAccountWithCodeCheck(cloudAccountKey, cloudAccountSecret, "msp_direct_added" + cloudAccountName, 
//				"cloud_direct", mspOrganizationID, spogServer.ReturnRandom("SKUTESTDATA_1_0_0_0_"), spogServer.ReturnRandom("SKUTESTDATA_1_0_0_0_"), 400, "00B0000C", test);	
//		spogServer.createCloudAccountWithCodeCheck(cloudAccountKey, cloudAccountSecret, "direct_direct_added" + cloudAccountName, 
//				"cloud_direct", directOrganizationID, spogServer.ReturnRandom("SKUTESTDATA_1_0_0_0_"), spogServer.ReturnRandom("SKUTESTDATA_1_0_0_0_"), 400, "00B0000C", test);
		
		// create duplicate cloud account name in different organization should succeed;
		spogServer.createCloudAccountWithCheck(cloudAccountKey, cloudAccountSecret, "sameName" + cloudAccountName, "aws_s3", 
				mspOrganizationID, "none", "none", test);
		spogServer.createCloudAccountWithCheck(cloudAccountKey, cloudAccountSecret, "sameName" + cloudAccountName, "aws_s3", 
				directOrganizationID, "none", "none", test);
		spogServer.createCloudAccountWithCheck(cloudAccountKey, cloudAccountSecret, "sameName" + cloudAccountName, "aws_s3", 
				childOrganizationID1, "none", "none", test);
		spogServer.createCloudAccountWithCheck(cloudAccountKey, cloudAccountSecret, "sameName" + cloudAccountName, "aws_s3", 
				childOrganizationID2, "none", "none", test);
	}

//	@Test
	public void createCloudAccountWithDataCenterTest() {
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		String organizationEmail = prefix+  "email@arcserve.com";
		String organizationName = prefix + "organizationName";
		String organizationPwd = "Passw0rd";
		String organizationFirstName = "Ma";
		String organizationLastName = "Zhaoguo";
		
		String cloudAccountKey = "cloudAccountKey";
		String cloudAccountSecret = "cloudAccountSecret";
		String cloudAccountName = "cloudAccountName";
		String orderID = "SKUTESTDATA_1_0_0_0_";
		String fulfillmentID = "SKUTESTDATA_1_0_0_0_";
		String enrollTicket = "SKUTESTDATA_1_0_0_0_";

		String organizationID = spogServer.CreateOrganizationWithCheck(organizationName, SpogConstants.DIRECT_ORG, 
				organizationEmail, organizationPwd, organizationFirstName, organizationLastName, test);
		spogServer.userLogin(organizationEmail, organizationPwd);
		String datacenterID = "99a9b48e-6ac6-4c47-8202-614b5cdcfe0c";
		String invalidDatacenterID = "99a9b48e";
		String nonExistingDataCenterID = "99a9b48e-6ac6-4c47-8202-614b5cdcfe0d";
		spogServer.createCloudAccountWithCodeCheck(cloudAccountKey, cloudAccountSecret, cloudAccountName, "cloud_direct", organizationID, orderID, fulfillmentID, enrollTicket, invalidDatacenterID, 400, "40000005", test);
		spogServer.createCloudAccountWithCodeCheck(cloudAccountKey, cloudAccountSecret, cloudAccountName, "cloud_direct", organizationID, orderID, fulfillmentID, enrollTicket, nonExistingDataCenterID, 400, "00B00012", test);
		spogServer.createCloudAccountWithCheck(cloudAccountKey, cloudAccountSecret, cloudAccountName, "cloud_direct", organizationID, orderID + prefix, fulfillmentID + prefix, 
				enrollTicket + prefix, datacenterID, test);
	}
	
	@AfterMethod
	public void afterMethodTest() {
		rep.endTest(test);
		rep.flush();
	}

	@AfterClass
	public void deleteOrgs() {
		
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

	@AfterClass
	public void aftertest() {
		test.log(LogStatus.INFO, "The total test cases passed are " + count1.getpassedcount());
		test.log(LogStatus.INFO, "the total test cases failed are " + count1.getfailedcount());
		rep.flush();
	}

	@AfterClass
	public void updatebd() {
		try {
			if (count1.getfailedcount() > 0) {
				Nooftest = count1.getpassedcount() + count1.getfailedcount() + count1.getskippedcount();
				bqdb1.updateTable(this.BQName, this.runningMachine, this.buildVersion, String.valueOf(Nooftest),
						Integer.toString(count1.getpassedcount()), Integer.toString(count1.getfailedcount()),
						String.valueOf(count1.getskippedcount()), count1.getcreationtime(), "Failed");
			} else {
				Nooftest = count1.getpassedcount() + count1.getfailedcount() + count1.getskippedcount();
				bqdb1.updateTable(this.BQName, this.runningMachine, this.buildVersion, String.valueOf(Nooftest),
						Integer.toString(count1.getpassedcount()), Integer.toString(count1.getfailedcount()),
						String.valueOf(count1.getskippedcount()), count1.getcreationtime(), "Passed");
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
