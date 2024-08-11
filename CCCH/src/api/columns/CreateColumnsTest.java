package api.columns;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.ColumnType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.Log4SPOGServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import dataPreparation.JsonPreparation;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

/**
 * @author Nagamalleswari.Sykam
 * We are creating the all type of column in  this class
 * We are covered many scenarios for all roles 
 * Create columns for all user roles
 * Create columns for the user if the user have already columns
 * Insufficient scenarios 
 * Passing the invalid ordered_id in the body while create the columns 
 * Invalid and Missed token 
 * Create columns for the invalid user 
 * Create columns for the random uuid as user id
 */
public class CreateColumnsTest extends base.prepare.Is4Org {

	private SPOGServer spogServer;
	private JsonPreparation jp;
	private TestOrgInfo ti;
	private ExtentTest test;
	protected ExtentReports rep;

	int passedcases = 0;
	int failedcases = 0;
	int skippedcases = 0;
	String[] sort = new String[] { "true", "false" };
	String[] filter = new String[] { "true", "false"};
	String[] visible = new String[] { "true", "false" };

	ArrayList<String> columnIdList = new ArrayList<String>();
	ArrayList<HashMap<String, Object>> columnsHeadContent = new ArrayList<HashMap<String, Object>>();
	String csr_token, csr_org_id;

	@BeforeClass
	@Parameters({ "baseURI", "port", "csrAdminUserName", "csrAdminPassword", "csrReadOnlyAdminName",
		"csrReadOnlyAdminPassword", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword,
			String csrReadOnlyAdminName, String csrReadOnlyAdminPassword, String logFolder, String runningMachine,
			String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		jp = new JsonPreparation();
		rep = ExtentManager.getInstance(getClass().getName(), logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Sykam.Naga Malleswari";
		ti = new TestOrgInfo(spogServer, test);
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

			try {
				bqdb1.updateTable(BQName, this.runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0",
						String.valueOf(Nooftest), count1.getcreationtime(), "InProgress",
						author + " and Rest server is " + baseURI.split("//")[1]);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		ti = new TestOrgInfo(spogServer, test);
		spogServer.setToken(ti.direct_org1_user1_token);


	}

	public ArrayList<String> getDefaultColumns(String token, String user_id, String columnType) {

		String filter = jp.composeColumnFilter(columnType, user_id, "none", "none");
		Response response = spogServer.getColumns(token, filter);

		;		columnsHeadContent = response.then().extract().path("data.columns");
		int length = columnsHeadContent.size();
		for (int i = 0; i < length; i++) {
			HashMap<String, Object> HeadContent = columnsHeadContent.get(i);

			columnIdList.add((String) HeadContent.get("column_id"));
		}

		return columnIdList;
	}

	@DataProvider(name = "create_columns_valid")
	public final Object[][] createlogcoloumnsValidParams() {

		return new Object[][] {
			// Alert Email Recipient columns
			{ "Create alert_email_recipient columns for the direct organization", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.ALERT_EMAIL_RECIPIENT.toString(),new String[] { "2", "5", "1", "4", "3" }, 2, SpogConstants.SUCCESS_POST },
			{ "Create alert_email_recipient columns for the  normal msp  organization",ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,ColumnType.ALERT_EMAIL_RECIPIENT.toString(), new String[] { "2", "1", "3", "4", "5" }, 3,SpogConstants.SUCCESS_POST },
			{ "Create alert_email_recipient columns for the normal msp account admin ",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.ALERT_EMAIL_RECIPIENT.toString(), new String[] { "2", "1", "5", "3", "4" }, 4,SpogConstants.SUCCESS_POST },
			{ "Create alert_email_recipient columns for the customer account of normal msp ",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.ALERT_EMAIL_RECIPIENT.toString(), new String[] { "3", "2", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Create alert_email_recipient columns for the root msp organization", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, ColumnType.ALERT_EMAIL_RECIPIENT.toString(),new String[] { "2", "1", "3", "4", "5" }, 4, SpogConstants.SUCCESS_POST },
			{ "Create alert_email_recipient columns  for the msp account admin of root  msp organziation",ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id,ColumnType.ALERT_EMAIL_RECIPIENT.toString(), new String[] { "2", "1", "5", "4", "2" }, 5,SpogConstants.SUCCESS_POST },
			{ "Create alert_email_recipient columns for the customer account of root  msp organziation",ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,ColumnType.ALERT_EMAIL_RECIPIENT.toString(), new String[] { "2", "1", "3", "4", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Create alert_email_recipient columns for the sub msp organization",ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,ColumnType.ALERT_EMAIL_RECIPIENT.toString(), new String[] { "2", "1", "3", "4", "5" }, 4,SpogConstants.SUCCESS_POST },
			{ "Create alert_email_recipient columns for the msp account admin of sub  msp organziation",ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_1_id,ColumnType.ALERT_EMAIL_RECIPIENT.toString(), new String[] { "2", "3", "1", "4" }, 3,SpogConstants.SUCCESS_POST },
			{ "Create alert_email_recipient columns for the customer account of sub  msp organziation",ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,ColumnType.ALERT_EMAIL_RECIPIENT.toString(), new String[] { "2", "1", "3", "4", "5" }, 5,SpogConstants.SUCCESS_POST },

			// CSR user Scenario
			{ "Create  alert_email_recipient columns for the direct organization with the csr user token ",ti.csr_token, ti.msp1_submsp1_suborg1_user1_id, ColumnType.ALERT_EMAIL_RECIPIENT.toString(),new String[] { "2", "1", "5", "4", "6" }, 4, SpogConstants.SUCCESS_POST },

			// Hypervisor columns
			{ "Create hyperviosr columns for the direct organization", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.HYPERVISOR.toString(),new String[] { "2", "1", "5", "4", "3" }, 2, SpogConstants.SUCCESS_POST },
			{ "Create hyperviosr columns for the  normal msp  organization", ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ColumnType.HYPERVISOR.toString(),new String[] { "2", "1", "3", "4", "5" }, 3, SpogConstants.SUCCESS_POST },
			{ "Create  hyperviosr columns for the normal msp account admin ",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.HYPERVISOR.toString(), new String[] { "2", "1", "5", "3", "4" }, 4,SpogConstants.SUCCESS_POST },
			{ "Create  hyperviosr columns for the customer account of normal msp ",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.HYPERVISOR.toString(), new String[] { "3", "2", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Create  hyperviosr columns for the root msp organization", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, ColumnType.HYPERVISOR.toString(),new String[] { "2", "1", "3", "4", "5" }, 4, SpogConstants.SUCCESS_POST },
			{ "Create  hyperviosr columns  for the msp account admin of root  msp organziation",ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id,ColumnType.HYPERVISOR.toString(), new String[] { "2", "1", "5", "4", "2" }, 5,SpogConstants.SUCCESS_POST },
			{ "Create  hyperviosr columns for the customer account of root  msp organziation",ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,ColumnType.HYPERVISOR.toString(), new String[] { "2", "1", "3", "4", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Create  hyperviosr columns for the sub msp organization", ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp1_user1_id, ColumnType.HYPERVISOR.toString(),new String[] { "2", "1", "3", "4", "5" }, 4, SpogConstants.SUCCESS_POST },
			{ "Create hyperviosr columns for the msp account admin of sub  msp organziation",ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_1_id,ColumnType.HYPERVISOR.toString(), new String[] { "2", "3", "1", "4" }, 3,SpogConstants.SUCCESS_POST },
			{ "Create  hyperviosr columns for the customer account of sub  msp organziation",ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,ColumnType.HYPERVISOR.toString(), new String[] { "2", "1", "3", "4", "5" }, 5,SpogConstants.SUCCESS_POST },

			// CSR user Scenario
			{ "Create  hyperviosr columns for the direct organization with the csr user token ", ti.csr_token,ti.msp1_submsp1_suborg1_user1_id, ColumnType.HYPERVISOR.toString(),new String[] { "2", "1", "5", "4", "6" }, 4, SpogConstants.SUCCESS_POST },



			//Recovered resources columns
			{ "Create recovered_resources columns for the direct organization", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.RECOVEREDRESOURCE.toString(),new String[] { "2", "1", "5", "4", "3","6","7","8","9","10","11"}, 11, SpogConstants.SUCCESS_POST },
			{ "Create recovered_resources columns for the  normal msp  organization", ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ColumnType.RECOVEREDRESOURCE.toString(),new String[] { "4", "3","6","7","8","9"}, 5, SpogConstants.SUCCESS_POST },
			{ "Create  recovered_resources columns for the normal msp account admin ",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.RECOVEREDRESOURCE.toString(), new String[] { "3","6","7","8","9","10","11" }, 4,SpogConstants.SUCCESS_POST },
			{ "Create  recovered_resources columns for the customer account of normal msp ",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.RECOVEREDRESOURCE.toString(), new String[] { "9","10","11"}, 2,SpogConstants.SUCCESS_POST },
			{ "Create  recovered_resources columns for the root msp organization", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, ColumnType.RECOVEREDRESOURCE.toString(),new String[] { "3","10","11","6","7","8","9"}, 4, SpogConstants.SUCCESS_POST },
			{ "Create  recovered_resources columns  for the msp account admin of root  msp organziation",ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id,ColumnType.RECOVEREDRESOURCE.toString(), new String[] {  "2", "1", "5", "3","7","8","10","11"}, 5,SpogConstants.SUCCESS_POST },
			{ "Create  recovered_resources columns for the customer account of root  msp organziation",ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,ColumnType.RECOVEREDRESOURCE.toString(), new String[] { "2", "1", "5", "4", "3","6","7","8","9","10","11" },7,SpogConstants.SUCCESS_POST },
			{ "Create  recovered_resources columns for the sub msp organization", ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp1_user1_id, ColumnType.RECOVEREDRESOURCE.toString(),new String[] {  "2", "1", "5", "4", "3","6","7","9","8","11","10",}, 4, SpogConstants.SUCCESS_POST },
			{ "Create recovered_resources columns for the msp account admin of sub  msp organziation",ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_1_id,ColumnType.RECOVEREDRESOURCE.toString(), new String[] {  "3","6","7","8", }, 3,SpogConstants.SUCCESS_POST },
			{ "Create  recovered_resources columns for the customer account of sub  msp organziation",ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,ColumnType.RECOVEREDRESOURCE.toString(), new String[] {  "3", "7", "9","8","11" }, 5,SpogConstants.SUCCESS_POST },

			// CSR user Scenario
			{ "Create  recovered_resources columns for the direct organization with the csr user token ", ti.csr_token,ti.msp1_submsp1_suborg1_user1_id, ColumnType.RECOVEREDRESOURCE.toString(),new String[] { "2", "1", "5", "4", "6" }, 4, SpogConstants.SUCCESS_POST },


			//Policy columns
			{ "Create policy columns for the direct organization", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.POLICY.toString(),new String[] { "2", "1", "5", "4", "3" }, 2, SpogConstants.SUCCESS_POST },
			{ "Create policy columns for the  normal msp  organization",ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,ColumnType.POLICY.toString(), new String[] { "2", "1", "3", "4", "5" }, 3,SpogConstants.SUCCESS_POST },
			{ "Create policy columns for the normal msp account admin ",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.POLICY.toString(), new String[] { "2", "1", "5", "3", "4" }, 4,SpogConstants.SUCCESS_POST },
			{ "Create policy columns for the customer account of normal msp ",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.POLICY.toString(), new String[] { "3", "2", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Create policy columns for the root msp organization", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, ColumnType.POLICY.toString(),new String[] { "2", "1", "3", "4", "5" }, 4, SpogConstants.SUCCESS_POST },
			{ "Create policy columns  for the msp account admin of root  msp organziation",ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id,ColumnType.POLICY.toString(), new String[] { "2", "1", "5", "4", "2" }, 5,SpogConstants.SUCCESS_POST },
			{ "Create policy columns for the customer account of root  msp organziation",ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,ColumnType.POLICY.toString(), new String[] { "2", "1", "3", "4", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Create policy columns for the sub msp organization",ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,ColumnType.POLICY.toString(), new String[] { "2", "1", "3", "4", "5" }, 4,SpogConstants.SUCCESS_POST },
			{ "Create policy columns for the msp account admin of sub  msp organziation",ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_1_id,ColumnType.POLICY.toString(), new String[] { "2", "3", "1", "4" }, 3,SpogConstants.SUCCESS_POST },
			{ "Create policy columns for the customer account of sub  msp organziation",ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,ColumnType.POLICY.toString(), new String[] { "2", "1","6","7", "3", "4", "5" },7,SpogConstants.SUCCESS_POST },


			//CSR user 
			{ "Create  policy columns for the direct organization with the csr user token ", ti.csr_token,ti.msp1_submsp1_suborg1_user1_id, ColumnType.POLICY.toString(),new String[] { "2", "1", "5", "4", "6" }, 4, SpogConstants.SUCCESS_POST },


			//Log columns
			{ "Create log columns for the direct organization", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.LOG.toString(),new String[] { "2", "1", "5", "4", "3" }, 2, SpogConstants.SUCCESS_POST },
			{ "Create log columns for the  normal msp  organization",ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,ColumnType.LOG.toString(), new String[] { "2", "1", "3", "4", "5" }, 3,SpogConstants.SUCCESS_POST },
			{ "Create log columns for the normal msp account admin ",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.LOG.toString(), new String[] { "2", "1", "5", "3", "4" }, 4,SpogConstants.SUCCESS_POST },
			{ "Create log columns for the customer account of normal msp ",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.LOG.toString(), new String[] { "3", "2", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Create log columns for the root msp organization", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, ColumnType.LOG.toString(),new String[] { "2", "1", "3", "4", "5" }, 4, SpogConstants.SUCCESS_POST },
			{ "Create log columns  for the msp account admin of root  msp organziation",ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id,ColumnType.LOG.toString(), new String[] { "2", "1", "5", "4", "2" }, 5,SpogConstants.SUCCESS_POST },
			{ "Create log columns for the customer account of root  msp organziation",ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,ColumnType.LOG.toString(), new String[] { "2", "1", "3", "4", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Create log columns for the sub msp organization",ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,ColumnType.LOG.toString(), new String[] { "2", "1", "3", "4", "5" }, 4,SpogConstants.SUCCESS_POST },
			{ "Create log columns for the msp account admin of sub  msp organziation",ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_1_id,ColumnType.LOG.toString(), new String[] { "2", "3", "1", "4" }, 3,SpogConstants.SUCCESS_POST },
			{ "Create log columns for the customer account of sub  msp organziation",ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,ColumnType.LOG.toString(), new String[] { "2", "1","6","7", "3", "4", "5" },7,SpogConstants.SUCCESS_POST },


			//Csr 
			{ "Create  log columns for the direct organization with the csr user token ", ti.csr_token,ti.msp1_submsp1_suborg1_user1_id, ColumnType.LOG.toString(),new String[] { "2", "1", "5", "4", "6" }, 4, SpogConstants.SUCCESS_POST },

			//User Columns
			{ "Create user columns for the direct organization", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.USER.toString(),new String[] { "2", "1", "5", "4", "3" }, 2, SpogConstants.SUCCESS_POST },
			{ "Create user columns for the  normal msp  organization",ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,ColumnType.USER.toString(), new String[] { "2", "1", "3", "4", "5" }, 3,SpogConstants.SUCCESS_POST },
			{ "Create user columns for the normal msp account admin ",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.USER.toString(), new String[] { "2", "1", "5", "3", "4" }, 4,SpogConstants.SUCCESS_POST },
			{ "Create user columns for the customer account of normal msp ",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.USER.toString(), new String[] { "3", "2", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Create user columns for the root msp organization", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, ColumnType.USER.toString(),new String[] { "2", "1", "3", "4", "5" }, 4, SpogConstants.SUCCESS_POST },
			{ "Create user columns  for the msp account admin of root  msp organziation",ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id,ColumnType.USER.toString(), new String[] { "2", "1", "5", "4", "2" }, 5,SpogConstants.SUCCESS_POST },
			{ "Create user columns for the customer account of root  msp organziation",ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,ColumnType.USER.toString(), new String[] { "2", "1", "3", "4", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Create user columns for the sub msp organization",ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,ColumnType.USER.toString(), new String[] { "2", "1", "3", "4", "5" }, 4,SpogConstants.SUCCESS_POST },
			{ "Create user columns for the msp account admin of sub  msp organziation",ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_1_id,ColumnType.USER.toString(), new String[] { "2", "3", "1", "4" }, 3,SpogConstants.SUCCESS_POST },
			{ "Create user columns for the customer account of sub  msp organziation",ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,ColumnType.USER.toString(), new String[] { "2", "1","6","7", "3", "4", "5" },7,SpogConstants.SUCCESS_POST },


			//Csr 
			{ "Create  user columns for the direct organization with the csr user token ", ti.csr_token,ti.msp1_submsp1_suborg1_user1_id, ColumnType.USER.toString(),new String[] { "2", "1", "5", "4", "6" }, 4, SpogConstants.SUCCESS_POST },

			//Source columns 
			{ "Create source columns for the direct organization", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.SOURCE.toString(),new String[] { "2", "1", "5", "4", "3" }, 2, SpogConstants.SUCCESS_POST },
			{ "Create source columns for the  normal msp  organization",ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,ColumnType.SOURCE.toString(), new String[] { "2", "1", "3", "4", "5" }, 3,SpogConstants.SUCCESS_POST },
			{ "Create source columns for the normal msp account admin ",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.SOURCE.toString(), new String[] { "2", "1", "5", "3", "4","8","12","10","7" }, 9,SpogConstants.SUCCESS_POST },
			{ "Create source columns for the customer account of normal msp ",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.SOURCE.toString(), new String[] { "3", "2", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Create source columns for the root msp organization", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, ColumnType.SOURCE.toString(),new String[] { "2", "1", "3", "4", "5" }, 4, SpogConstants.SUCCESS_POST },
			{ "Create source columns  for the msp account admin of root  msp organziation",ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id,ColumnType.SOURCE.toString(), new String[] { "2", "1", "5", "4", "2" }, 5,SpogConstants.SUCCESS_POST },
			{ "Create source columns for the customer account of root  msp organziation",ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,ColumnType.SOURCE.toString(), new String[] { "2", "1", "3", "4", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Create source columns for the sub msp organization",ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,ColumnType.SOURCE.toString(), new String[] { "2", "1", "3", "4", "5" }, 4,SpogConstants.SUCCESS_POST },
			{ "Create source columns for the msp account admin of sub  msp organziation",ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_1_id,ColumnType.SOURCE.toString(), new String[] { "2", "3", "1", "4" }, 3,SpogConstants.SUCCESS_POST },
			{ "Create source columns for the customer account of sub  msp organziation",ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,ColumnType.SOURCE.toString(), new String[] { "2", "1","6","7", "3", "4", "5","9","11","8","10","12" },12,SpogConstants.SUCCESS_POST },


			//Csr 
			{ "Create  source columns for the direct organization with the csr user token ", ti.csr_token,ti.msp1_submsp1_suborg1_user1_id, ColumnType.SOURCE.toString(),new String[] { "2", "1", "5", "4", "6" }, 4, SpogConstants.SUCCESS_POST },

			//Recovery job report columns 
			{ "Create recovery_job_report columns for the direct organization", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.RESTORE_JOB_REPORT.toString(),new String[] { "2", "1", "5", "4", "3" }, 2, SpogConstants.SUCCESS_POST },
			{ "Create recovery_job_report columns for the  normal msp  organization",ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,ColumnType.RESTORE_JOB_REPORT.toString(), new String[] { "2", "1", "3", "4", "5" }, 3,SpogConstants.SUCCESS_POST },
			{ "Create recovery_job_report columns for the normal msp account admin ",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.RESTORE_JOB_REPORT.toString(), new String[] { "2", "1", "5", "3", "4","8","12","10","7" }, 9,SpogConstants.SUCCESS_POST },
			{ "Create recovery_job_report columns for the customer account of normal msp ",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.RESTORE_JOB_REPORT.toString(), new String[] { "3", "2", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Create recovery_job_report columns for the root msp organization", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, ColumnType.RESTORE_JOB_REPORT.toString(),new String[] { "2", "1", "3", "4", "5" }, 4, SpogConstants.SUCCESS_POST },
			{ "Create recovery_job_report columns  for the msp account admin of root  msp organziation",ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id,ColumnType.RESTORE_JOB_REPORT.toString(), new String[] { "2", "1", "5", "4", "2" }, 5,SpogConstants.SUCCESS_POST },
			{ "Create recovery_job_report columns for the customer account of root  msp organziation",ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,ColumnType.RESTORE_JOB_REPORT.toString(), new String[] { "2", "1", "3", "4", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Create recovery_job_report columns for the sub msp organization",ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,ColumnType.RESTORE_JOB_REPORT.toString(), new String[] { "2", "1", "3", "4", "5" }, 4,SpogConstants.SUCCESS_POST },
			{ "Create recovery_job_report columns for the msp account admin of sub  msp organziation",ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_1_id,ColumnType.RESTORE_JOB_REPORT.toString(), new String[] { "2", "3", "1", "4" }, 3,SpogConstants.SUCCESS_POST },
			{ "Create recovery_job_report columns for the customer account of sub  msp organziation",ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,ColumnType.RESTORE_JOB_REPORT.toString(), new String[] { "2", "1","6","7", "3", "4", "5","9","11","8","10","12" },12,SpogConstants.SUCCESS_POST },


			//Csr 
			{ "Create  recovery_job_report columns for the direct organization with the csr user token ", ti.csr_token,ti.msp1_submsp1_suborg1_user1_id, ColumnType.RESTORE_JOB_REPORT.toString(),new String[] { "2", "1", "5", "4", "6" }, 4, SpogConstants.SUCCESS_POST },

			//Backup job report columns 
			{ "Create backup_job_report columns for the direct organization", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.BACKUP_JOB_REPORT.toString(),new String[] { "2", "1", "5", "4", "3" }, 2, SpogConstants.SUCCESS_POST },
			{ "Create backup_job_report columns for the  normal msp  organization",ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,ColumnType.BACKUP_JOB_REPORT.toString(), new String[] { "2", "1", "3", "4", "5" }, 3,SpogConstants.SUCCESS_POST },
			{ "Create backup_job_report columns for the customer account of normal msp ",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.BACKUP_JOB_REPORT.toString(), new String[] { "3", "2", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Create backup_job_report columns for the root msp organization", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, ColumnType.BACKUP_JOB_REPORT.toString(),new String[] { "2", "1", "3", "4", "5" }, 4, SpogConstants.SUCCESS_POST },
			{ "Create backup_job_report columns for the customer account of root  msp organziation",ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,ColumnType.BACKUP_JOB_REPORT.toString(), new String[] { "2", "13", "12", "4", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Create backup_job_report columns for the sub msp organization",ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,ColumnType.BACKUP_JOB_REPORT.toString(), new String[] { "2", "1", "3", "4", "5" }, 4,SpogConstants.SUCCESS_POST },
			{ "Create backup_job_report columns for the customer account of sub  msp organziation",ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,ColumnType.BACKUP_JOB_REPORT.toString(), new String[] { "2", "1","6","13", "3", "4", "5","9","11","8","10","12" },12,SpogConstants.SUCCESS_POST },


			//Csr 
			{ "Create  backup_job_report columns for the direct organization with the csr user token ", ti.csr_token,ti.msp1_submsp1_suborg1_user1_id, ColumnType.BACKUP_JOB_REPORT.toString(),new String[] { "2", "1", "5", "4", "6" }, 4, SpogConstants.SUCCESS_POST },



			//Job columns 
			{ "Create job columns for the direct organization", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.JOB.toString(),new String[] { "2", "1", "5", "4", "3" }, 2, SpogConstants.SUCCESS_POST },
			{ "Create job columns for the  normal msp  organization",ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,ColumnType.JOB.toString(), new String[] { "2", "1", "3", "4", "5" }, 3,SpogConstants.SUCCESS_POST },
			{ "Create job columns for the normal msp account admin ",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.JOB.toString(), new String[] { "2", "1", "5", "3", "4","8","9","7"}, 6,SpogConstants.SUCCESS_POST },
			{ "Create job columns for the customer account of normal msp ",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.JOB.toString(), new String[] { "3", "2", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Create job columns for the root msp organization", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, ColumnType.JOB.toString(),new String[] { "2", "1", "3", "4", "5" }, 4, SpogConstants.SUCCESS_POST },
			{ "Create job columns  for the msp account admin of root  msp organziation",ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id,ColumnType.JOB.toString(), new String[] { "2", "1", "7", "4", "2" }, 5,SpogConstants.SUCCESS_POST },
			{ "Create job columns for the customer account of root  msp organziation",ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,ColumnType.JOB.toString(), new String[] { "2", "8", "9", "4", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Create job columns for the sub msp organization",ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,ColumnType.JOB.toString(), new String[] { "2", "1", "3", "4", "5" }, 4,SpogConstants.SUCCESS_POST },
			{ "Create job columns for the msp account admin of sub  msp organziation",ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_1_id,ColumnType.JOB.toString(), new String[] { "2", "3", "1", "4" }, 3,SpogConstants.SUCCESS_POST },
			{ "Create job columns for the customer account of sub  msp organziation",ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,ColumnType.JOB.toString(), new String[] { "2", "1","6","3", "4", "5","9","8",},5,SpogConstants.SUCCESS_POST },


			//Csr 
			{ "Create  job columns for the direct organization with the csr user token ", ti.csr_token,ti.msp1_submsp1_suborg1_user1_id, ColumnType.JOB.toString(),new String[] { "2", "1", "5", "4", "6" }, 4, SpogConstants.SUCCESS_POST },


			///Destination columns 
			{ "Create destination columns for the direct organization", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.DESTINATION.toString(),new String[] { "10", "1", "5", "4", "3" }, 2, SpogConstants.SUCCESS_POST },
			{ "Create destination columns for the  normal msp  organization",ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,ColumnType.DESTINATION.toString(), new String[] { "2", "1", "3", "4", "5" }, 3,SpogConstants.SUCCESS_POST },
			{ "Create destination columns for the normal msp account admin ",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.DESTINATION.toString(), new String[] { "2", "1", "5", "3", "4","8","9","7"}, 6,SpogConstants.SUCCESS_POST },
			{ "Create destination columns for the customer account of normal msp ",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.DESTINATION.toString(), new String[] { "3", "10", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Create destination columns for the root msp organization", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, ColumnType.DESTINATION.toString(),new String[] { "2", "1", "3", "4", "5" }, 4, SpogConstants.SUCCESS_POST },
			{ "Create destination columns  for the msp account admin of root  msp organziation",ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id,ColumnType.DESTINATION.toString(), new String[] { "2", "1", "7", "4", "2" }, 5,SpogConstants.SUCCESS_POST },
			{ "Create destination columns for the customer account of root  msp organziation",ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,ColumnType.DESTINATION.toString(), new String[] { "2", "10", "9", "4", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Create destination columns for the sub msp organization",ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,ColumnType.DESTINATION.toString(), new String[] { "2", "1", "3", "4", "5" }, 4,SpogConstants.SUCCESS_POST },
			{ "Create destination columns for the msp account admin of sub  msp organziation",ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_1_id,ColumnType.DESTINATION.toString(), new String[] { "2", "3", "1", "4" }, 3,SpogConstants.SUCCESS_POST },
			{ "Create destination columns for the customer account of sub  msp organziation",ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,ColumnType.DESTINATION.toString(), new String[] { "2", "1","6","3", "4", "5","9","8",},5,SpogConstants.SUCCESS_POST },


			//Csr 
			{ "Create  destination columns for the direct organization with the csr user token ", ti.csr_token,ti.msp1_submsp1_suborg1_user1_id, ColumnType.DESTINATION.toString(),new String[] { "2", "1", "5", "10", "6" }, 4, SpogConstants.SUCCESS_POST },

			//POLICY_TASK_REPORT columns 
			{ "Create policy_task_report  columns for the direct organization", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.POLICY_TASK_REPORT.toString(),new String[] { "10", "1", "5", "4", "3" }, 2, SpogConstants.SUCCESS_POST },
			{ "Create policy_task_report  columns for the  normal msp  organization",ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,ColumnType.POLICY_TASK_REPORT.toString(), new String[] { "11", "1", "3", "4", "5" }, 3,SpogConstants.SUCCESS_POST },
			{ "Create policy_task_report  columns for the normal msp account admin ",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.POLICY_TASK_REPORT.toString(), new String[] { "2", "1", "5", "3", "4","8","9","7"}, 6,SpogConstants.SUCCESS_POST },
			{ "Create policy_task_report  columns for the customer account of normal msp ",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.POLICY_TASK_REPORT.toString(), new String[] { "3", "10", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Create policy_task_report  columns for the root msp organization", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, ColumnType.POLICY_TASK_REPORT.toString(),new String[] { "2", "1", "11", "4", "5" }, 4, SpogConstants.SUCCESS_POST },
			{ "Create policy_task_report  columns  for the msp account admin of root  msp organziation",ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id,ColumnType.POLICY_TASK_REPORT.toString(), new String[] { "2", "1", "7", "4", "2" }, 5,SpogConstants.SUCCESS_POST },
			{ "Create policy_task_report  columns for the customer account of root  msp organziation",ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,ColumnType.POLICY_TASK_REPORT.toString(), new String[] { "2", "10", "9", "4", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Create policy_task_report  columns for the sub msp organization",ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,ColumnType.POLICY_TASK_REPORT.toString(), new String[] { "2", "1", "3", "11", "5" }, 4,SpogConstants.SUCCESS_POST },
			{ "Create policy_task_report  columns for the msp account admin of sub  msp organziation",ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_1_id,ColumnType.POLICY_TASK_REPORT.toString(), new String[] { "2", "3", "1", "4" }, 3,SpogConstants.SUCCESS_POST },
			{ "Create policy_task_report  columns for the customer account of sub  msp organziation",ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,ColumnType.POLICY_TASK_REPORT.toString(), new String[] { "2", "1","6","3", "4", "5","9","8",},5,SpogConstants.SUCCESS_POST },


			//Csr 
			{ "Create  policy_task_report  columns for the direct organization with the csr user token ", ti.csr_token,ti.msp1_submsp1_suborg1_user1_id, ColumnType.POLICY_TASK_REPORT.toString(),new String[] { "2", "1", "5", "10", "6" }, 4, SpogConstants.SUCCESS_POST },
			 
			
			
			//Managed Report schedules page  columns 
			{ "Create manageReportSchedule  columns for the direct organization", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.REPORT_SCHEDULE.toString(),new String[] {  "5", "4", "3" }, 2, SpogConstants.SUCCESS_POST },
			{ "Create manageReportSchedule  columns for the  normal msp  organization",ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,ColumnType.REPORT_SCHEDULE.toString(), new String[] {"1", "3", "4", "5" }, 3,SpogConstants.SUCCESS_POST },
			{ "Create manageReportSchedule  columns for the normal msp account admin ",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.REPORT_SCHEDULE.toString(), new String[] { "2", "1", "5", "3", "4","7"}, 6,SpogConstants.SUCCESS_POST },
			{ "Create manageReportSchedule  columns for the customer account of normal msp ",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.REPORT_SCHEDULE.toString(), new String[] { "3",  "5" ,"7"}, 2,SpogConstants.SUCCESS_POST },
			{ "Create manageReportSchedule  columns for the root msp organization", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, ColumnType.REPORT_SCHEDULE.toString(),new String[] { "2", "1","4", "5" }, 4, SpogConstants.SUCCESS_POST },
			{ "Create manageReportSchedule  columns  for the msp account admin of root  msp organziation",ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id,ColumnType.REPORT_SCHEDULE.toString(), new String[] { "2", "1", "7", "4", "2" }, 5,SpogConstants.SUCCESS_POST },
			{ "Create manageReportSchedule  columns for the customer account of root  msp organziation",ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,ColumnType.REPORT_SCHEDULE.toString(), new String[] { "2", "4", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Create manageReportSchedule  columns for the sub msp organization",ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,ColumnType.REPORT_SCHEDULE.toString(), new String[] { "2", "1", "3", "5" }, 4,SpogConstants.SUCCESS_POST },
			{ "Create manageReportSchedule  columns for the msp account admin of sub  msp organziation",ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_1_id,ColumnType.REPORT_SCHEDULE.toString(), new String[] { "2", "3", "1", "4" }, 3,SpogConstants.SUCCESS_POST },
			{ "Create manageReportSchedule  columns for the customer account of sub  msp organziation",ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,ColumnType.REPORT_SCHEDULE.toString(), new String[] { "2", "1","6","3", "4", "5",},5,SpogConstants.SUCCESS_POST },


			//Csr 
			{ "Create  manageReportSchedule  columns for the direct organization with the csr user token ", ti.csr_token,ti.msp1_submsp1_suborg1_user1_id, ColumnType.REPORT_SCHEDULE.toString(),new String[] { "2", "1", "5", "6" }, 4, SpogConstants.SUCCESS_POST },
			
			
			//Managed Report schedules page  columns 
			{ "Create ReportPage  columns for the direct organization", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.REPORT.toString(),new String[] { "1", "5", "4", "3" }, 2, SpogConstants.SUCCESS_POST },
			{ "Create ReportPage  columns for the  normal msp  organization",ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,ColumnType.REPORT.toString(), new String[] { "1", "3", "4", "5" }, 3,SpogConstants.SUCCESS_POST },
			{ "Create ReportPage  columns for the normal msp account admin ",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.REPORT.toString(), new String[] { "2", "1", "5", "3", "4","8",}, 6,SpogConstants.SUCCESS_POST },
			{ "Create ReportPage  columns for the customer account of normal msp ",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.REPORT.toString(), new String[] { "3", "5" ,"6"}, 2,SpogConstants.SUCCESS_POST },
			{ "Create ReportPage  columns for the root msp organization", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, ColumnType.REPORT.toString(),new String[] { "2", "1", "4", "5" }, 4, SpogConstants.SUCCESS_POST },
			{ "Create ReportPage  columns  for the msp account admin of root  msp organziation",ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id,ColumnType.REPORT.toString(), new String[] { "2", "1", "7", "4", "2" }, 5,SpogConstants.SUCCESS_POST },
			{ "Create ReportPage  columns for the customer account of root  msp organziation",ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,ColumnType.REPORT.toString(), new String[] { "2", "4", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Create ReportPage  columns for the sub msp organization",ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,ColumnType.REPORT.toString(), new String[] { "2", "1", "3", "5" ,"6"}, 4,SpogConstants.SUCCESS_POST },
			{ "Create ReportPage  columns for the msp account admin of sub  msp organziation",ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_1_id,ColumnType.REPORT.toString(), new String[] { "2", "3", "1", "4" }, 3,SpogConstants.SUCCESS_POST },
			{ "Create ReportPage  columns for the customer account of sub  msp organziation",ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,ColumnType.REPORT.toString(), new String[] { "2", "1","6","3", "4", "5","8",},5,SpogConstants.SUCCESS_POST },


			//Csr 
			{ "Create  ReportPage  columns for the direct organization with the csr user token ", ti.csr_token,ti.msp1_submsp1_suborg1_user1_id, ColumnType.REPORT.toString(),new String[] { "2", "1", "5", "6" }, 4, SpogConstants.SUCCESS_POST },
			
		};
	}

	@Test(dataProvider = "create_columns_valid", enabled = true)
	public void createColumnsForLoggedInUser_valid(String testCase,
			String token,
			String user_id,
			String columnType,
			String[] orderId,
			int noofcolumstobecreated,
			int expectedStatusCode) {

		test = ExtentManager.getNewTest(testCase);
		test.assignAuthor("sykam.nagamalleswari");

		//ArrayList<HashMap<String, Object>> expected_columns = new ArrayList<>();
		//HashMap<String, Object> temp = new HashMap<>();

		test.log(LogStatus.INFO, "Get and deleting the exsting columns");
		spogServer.getAndDeleteColumns(columnType, user_id, token);

		ArrayList<String> columnIdList = getDefaultColumns(token, user_id, columnType);

		ArrayList<HashMap<String, Object>>expected_columns=jp.composeColumnsInfo(noofcolumstobecreated, columnIdList, orderId, sort, filter, visible);

		test.log(LogStatus.INFO, "Create the columns  for the specified user");
		spogServer.postColumnsWithCheck(token, user_id, columnType, expected_columns, expectedStatusCode, null, test);

		test.log(LogStatus.INFO, "Create columns for user where user have already created columns");
		spogServer.postColumnsWithCheck(token, user_id, columnType, expected_columns, SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.COLUMN_EXIST, test);

		test.log(LogStatus.INFO, "Get and delete log columns ");
		spogServer.setToken(token);
		spogServer.getAndDeleteColumns(columnType, user_id, token);

	}

	@DataProvider(name = "create_columns_insufficientPermessions")
	public final Object[][] createcoloumnsinValidParams() {

		return new Object[][] {

			// Insufficient permissions

			//Policy task reports 
			{ "Create policy task columns for the direct organization with the another direct user  ",ti.direct_org1_user1_id, ti.direct_org2_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Create policy task columns for the direct organization with the normal msp user token ",ti.direct_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Create policy task columns for the direct organization with the normal msp account admin user token ",ti.direct_org1_user1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Create policy task columns for the direct organization with the customer account of normsl msp ",ti.direct_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Create policy task columns for the direct organization with the root msp user token ",ti.direct_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Create policy task columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Create policy task columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Create policy task columns for the direct organization with the root msp user token ",ti.direct_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Create policy task columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Create policy task columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},


			//Recovered resources 
			{ "Create recovered resources columns for the direct organization with the another direct user  ",ti.direct_org1_user1_id, ti.direct_org2_user1_token,ColumnType.RECOVEREDRESOURCE.toString()},
			{ "Create recovered resources columns for the direct organization with the normal msp user token ",ti.direct_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.RECOVEREDRESOURCE.toString()},
			{ "Create recovered resources columns for the direct organization with the normal msp account admin user token ",ti.direct_org1_user1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.RECOVEREDRESOURCE.toString()},
			{ "Create recovered resources columns for the direct organization with the customer account of normsl msp ",ti.direct_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.RECOVEREDRESOURCE.toString()},
			{ "Create recovered resources columns for the direct organization with the root msp user token ",ti.direct_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.RECOVEREDRESOURCE.toString()},
			{ "Create recovered resources columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.RECOVEREDRESOURCE.toString()},
			{ "Create recovered resources columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.RECOVEREDRESOURCE.toString()},
			{ "Create recovered resources columns for the direct organization with the root msp user token ",ti.direct_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.RECOVEREDRESOURCE.toString()},
			{ "Create recovered resources columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.RECOVEREDRESOURCE.toString()},
			{ "Create recovered resources columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.RECOVEREDRESOURCE.toString()},


			//Backup job reports 
			{ "Create backup job report columns for the direct organization with the another direct user  ",ti.direct_org1_user1_id, ti.direct_org2_user1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "Create backup job report columns for the direct organization with the normal msp user token ",ti.direct_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "Create backup job report columns for the direct organization with the normal msp account admin user token ",ti.direct_org1_user1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "Create backup job report columns for the direct organization with the customer account of normsl msp ",ti.direct_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "Create backup job report columns for the direct organization with the root msp user token ",ti.direct_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "Create backup job report columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "Create backup job report columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "Create backup job report columns for the direct organization with the root msp user token ",ti.direct_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "Create backup job report columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "Create backup job report columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.BACKUP_JOB_REPORT.toString()},

			//log columns
			{ "Create log columns for the direct organization with the another direct user  ",ti.direct_org1_user1_id, ti.direct_org2_user1_token,ColumnType.LOG.toString()},
			{ "Create log columns for the direct organization with the normal msp user token ",ti.direct_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.LOG.toString()},
			{ "Create log columns for the direct organization with the normal msp account admin user token ",ti.direct_org1_user1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.LOG.toString()},
			{ "Create log columns for the direct organization with the customer account of normsl msp ",ti.direct_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.LOG.toString()},
			{ "Create log columns for the direct organization with the root msp user token ",ti.direct_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.LOG.toString()},
			{ "Create log columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.LOG.toString()},
			{ "Create log columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.LOG.toString()},
			{ "Create log columns for the direct organization with the root msp user token ",ti.direct_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.LOG.toString()},
			{ "Create log columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.LOG.toString()},
			{ "Create log columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.LOG.toString()},


			//Recovery job reports 
			{ "Create recovery_job_report columns for the direct organization with the another direct user  ",ti.direct_org1_user1_id, ti.direct_org2_user1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "Create recovery_job_report columns for the direct organization with the normal msp user token ",ti.direct_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "Create recovery_job_report columns for the direct organization with the normal msp account admin user token ",ti.direct_org1_user1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "Create recovery_job_report columns for the direct organization with the customer account of normsl msp ",ti.direct_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "Create recovery_job_report columns for the direct organization with the root msp user token ",ti.direct_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "Create recovery_job_report columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "Create recovery_job_report columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "Create recovery_job_report columns for the direct organization with the root msp user token ",ti.direct_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "Create recovery_job_report columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "Create lrecovery_job_report  columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.RESTORE_JOB_REPORT.toString()},

			//Source columns 
			{ "Create source columns for the direct organization with the another direct user  ",ti.direct_org1_user1_id, ti.direct_org2_user1_token,ColumnType.SOURCE.toString()},
			{ "Create source columns for the direct organization with the normal msp user token ",ti.direct_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.SOURCE.toString()},
			{ "Create source columns for the direct organization with the normal msp account admin user token ",ti.direct_org1_user1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.SOURCE.toString()},
			{ "Create source columns for the direct organization with the customer account of normsl msp ",ti.direct_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.SOURCE.toString()},
			{ "Create source columns for the direct organization with the root msp user token ",ti.direct_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.SOURCE.toString()},
			{ "Create source columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.SOURCE.toString()},
			{ "Create source columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.SOURCE.toString()},
			{ "Create source columns for the direct organization with the root msp user token ",ti.direct_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.SOURCE.toString()},
			{ "Create source columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.SOURCE.toString()},
			{ "Create source  columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.SOURCE.toString()},


			//Job columns 
			{ "Create job columns for the direct organization with the another direct user  ",ti.direct_org1_user1_id, ti.direct_org2_user1_token,ColumnType.JOB.toString()},
			{ "Create job columns for the direct organization with the normal msp user token ",ti.direct_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.JOB.toString()},
			{ "Create job columns for the direct organization with the normal msp account admin user token ",ti.direct_org1_user1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.JOB.toString()},
			{ "Create job columns for the direct organization with the customer account of normsl msp ",ti.direct_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.JOB.toString()},
			{ "Create job columns for the direct organization with the root msp user token ",ti.direct_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.JOB.toString()},
			{ "Create job columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.JOB.toString()},
			{ "Create job columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.JOB.toString()},
			{ "Create job columns for the direct organization with the root msp user token ",ti.direct_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.JOB.toString()},
			{ "Create job columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.JOB.toString()},
			{ "Create job columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.JOB.toString()},

			//Destination columns 
			{ "Create destiantion columns for the direct organization with the another direct user  ",ti.direct_org1_user1_id, ti.direct_org2_user1_token,ColumnType.DESTINATION.toString()},
			{ "Create destiantion columns for the direct organization with the normal msp user token ",ti.direct_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.DESTINATION.toString()},
			{ "Create destiantion columns for the direct organization with the normal msp account admin user token ",ti.direct_org1_user1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.DESTINATION.toString()},
			{ "Create destiantion columns for the direct organization with the customer account of normsl msp ",ti.direct_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.DESTINATION.toString()},
			{ "Create destiantion columns for the direct organization with the root msp user token ",ti.direct_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.DESTINATION.toString()},
			{ "Create destiantion columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.DESTINATION.toString()},
			{ "Create destiantion columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.DESTINATION.toString()},
			{ "Create destiantion columns for the direct organization with the root msp user token ",ti.direct_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.DESTINATION.toString()},
			{ "Create destiantion columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.DESTINATION.toString()},
			{ "Create destiantion columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.DESTINATION.toString()},

			//PolicyTask columns 
			{ "Create policytaskReport columns for the direct organization with the another direct user  ",ti.direct_org1_user1_id, ti.direct_org2_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Create policytaskReport columns for the direct organization with the normal msp user token ",ti.direct_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Create policytaskReport columns for the direct organization with the normal msp account admin user token ",ti.direct_org1_user1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Create policytaskReport columns for the direct organization with the customer account of normsl msp ",ti.direct_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Create policytaskReport columns for the direct organization with the root msp user token ",ti.direct_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Create policytaskReport columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Create policytaskReport columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Create policytaskReport columns for the direct organization with the root msp user token ",ti.direct_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Create policytaskReport columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Create policytaskReport columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},


			//user columns 
			{ "Create user columns for the direct organization with the another direct user  ",ti.direct_org1_user1_id, ti.direct_org2_user1_token,ColumnType.USER.toString()},
			{ "Create user columns for the direct organization with the normal msp user token ",ti.direct_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.USER.toString()},
			{ "Create user columns for the direct organization with the normal msp account admin user token ",ti.direct_org1_user1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.USER.toString()},
			{ "Create user columns for the direct organization with the customer account of normsl msp ",ti.direct_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.USER.toString()},
			{ "Create user columns for the direct organization with the root msp user token ",ti.direct_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.USER.toString()},
			{ "Create user columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.USER.toString()},
			{ "Create user columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.USER.toString()},
			{ "Create user columns for the direct organization with the root msp user token ",ti.direct_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.USER.toString()},
			{ "Create user columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.USER.toString()},
			{ "Create user columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.USER.toString()},

			
			//Reports columns 
			{ "Create reports columns for the direct organization with the another direct user  ",ti.direct_org1_user1_id, ti.direct_org2_user1_token,ColumnType.REPORT.toString()},
			{ "Create reports columns for the direct organization with the normal msp user token ",ti.direct_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.REPORT.toString()},
			{ "Create reports columns for the direct organization with the normal msp account admin user token ",ti.direct_org1_user1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.REPORT.toString()},
			{ "Create reports columns for the direct organization with the customer account of normsl msp ",ti.direct_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.REPORT.toString()},
			{ "Create reports columns for the direct organization with the root msp user token ",ti.direct_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.REPORT.toString()},
			{ "Create reports columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.REPORT.toString()},
			{ "Create reports columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.REPORT.toString()},
			{ "Create reports columns for the direct organization with the root msp user token ",ti.direct_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.REPORT.toString()},
			{ "Create reports columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.REPORT.toString()},
			{ "Create reports columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.REPORT.toString()},


			
			//ManagedReportSchedules columns 
			{ "Create ManagedReportSchedules columns for the direct organization with the another direct user  ",ti.direct_org1_user1_id, ti.direct_org2_user1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "Create ManagedReportSchedules columns for the direct organization with the normal msp user token ",ti.direct_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "Create ManagedReportSchedules columns for the direct organization with the normal msp account admin user token ",ti.direct_org1_user1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "Create ManagedReportSchedules columns for the direct organization with the customer account of normsl msp ",ti.direct_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "Create ManagedReportSchedules columns for the direct organization with the root msp user token ",ti.direct_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "Create ManagedReportSchedules columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "Create ManagedReportSchedules columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "Create ManagedReportSchedules columns for the direct organization with the root msp user token ",ti.direct_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "Create ManagedReportSchedules columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "Create ManagedReportSchedules columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.REPORT_SCHEDULE.toString()},


		};
	}

	@Test(dataProvider = "create_columns_insufficientPermessions", enabled = true)
	public void createColumnsInsufficientPermissions(String testCase,
			String user_id,
			String ortherOrgToken,
			String columnType 
			)
	{

		test = ExtentManager.getNewTest(testCase);
		test.assignAuthor("sykam.nagamalleswari");
		ArrayList<HashMap<String, Object>> expected_columns = new ArrayList<>();
		HashMap<String, Object> temp = new HashMap<>();

		test.log(LogStatus.INFO, "Compose data to delet the column");
		String getColumnfInfo = jp.composeColumnFilter(columnType, user_id, "none", "none");


		getDefaultColumns(ti.csr_token, user_id, columnType);
		spogServer.getColumns(ti.csr_token, getColumnfInfo);
		spogServer.setToken(csr_token);

		test.log(LogStatus.INFO, "Compose the log columns as per the user request");
		temp = jp.composeColumn(columnIdList.get(1), "true", "true", "true", "2");
		expected_columns.add(temp);

		System.out.println(expected_columns.toString());
		test.log(LogStatus.INFO, "Create the log columns for the specified user");
		spogServer.setToken(ortherOrgToken);
		spogServer.postColumnsWithCheck(ortherOrgToken, user_id,columnType,expected_columns, SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY , test);
	}



	@DataProvider(name = "invalidUserInfo")
	public final Object[][] InvlaidUserIdParams() {

		return new Object[][] {
			//Policy task reports 
			{ "Create user columns for the invalid user of direct organziation with valid token ",ti.direct_org1_user1_id, ti.direct_org1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Create user columns for the invalid user of direct organziation with the normal msp user token ",ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Create user columns for the invalid user of direct organziation with the normal msp account admin user token ",ti.normal_msp_org1_msp_accountadmin1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Create USER columns for the invalid user of direct organziation with the customer account of normsl msp ",ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Create user columns for the invalid user of direct organziation with the root msp user token ",ti.root_msp_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Create user columns for the invalid user of direct organziation with the root msp account admin user token ",ti.root_msp_org1_msp_accountadmin1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Create user columns for the invalid user of direct organziation with the customer account of root msp ",ti.root_msp1_suborg1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Create user columns for the invalid user of direct organziation with the root msp user token ",ti.root_msp1_submsp1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Create user columns for the invalid user of direct organziation with the root msp account admin user token ",ti.root_msp1_submsp1_account_admin_1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Create user columns for the invalid user of direct organziation with the customer account of root msp ",ti.msp1_submsp1_suborg1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},

			//Source Columns
			{ "Create source columns for the invalid user of direct organziation with valid token ",ti.direct_org1_user1_id, ti.direct_org1_user1_token,ColumnType.SOURCE.toString()},
			{ "Create source columns for the invalid user of direct organziation with the normal msp user token ",ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.SOURCE.toString()},
			{ "Create source columns for the invalid user of direct organziation with the normal msp account admin user token ",ti.normal_msp_org1_msp_accountadmin1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.SOURCE.toString()},
			{ "Create source columns for the invalid user of direct organziation with the customer account of normsl msp ",ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.SOURCE.toString()},
			{ "Create source columns for the invalid user of direct organziation with the root msp user token ",ti.root_msp_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.SOURCE.toString()},
			{ "Create source columns for the invalid user of direct organziation with the root msp account admin user token ",ti.root_msp_org1_msp_accountadmin1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.SOURCE.toString()},
			{ "Create source columns for the invalid user of direct organziation with the customer account of root msp ",ti.root_msp1_suborg1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.SOURCE.toString()},
			{ "Create source columns for the invalid user of direct organziation with the root msp user token ",ti.root_msp1_submsp1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.SOURCE.toString()},
			{ "Create source columns for the invalid user of direct organziation with the root msp account admin user token ",ti.root_msp1_submsp1_account_admin_1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.SOURCE.toString()},
			{ "Create source columns for the invalid user of direct organziation with the customer account of root msp ",ti.msp1_submsp1_suborg1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.SOURCE.toString()},

			//Policy Columns
			{ "Create policy columns for the invalid user of direct organziation with valid token ",ti.direct_org1_user1_id, ti.direct_org1_user1_token,ColumnType.POLICY.toString()},
			{ "Create policy columns for the invalid user of direct organziation with the normal msp user token ",ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.POLICY.toString()},
			{ "Create policy columns for the invalid user of direct organziation with the normal msp account admin user token ",ti.normal_msp_org1_msp_accountadmin1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.POLICY.toString()},
			{ "Create policy columns for the invalid user of direct organziation with the customer account of normsl msp ",ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.POLICY.toString()},
			{ "Create policy columns for the invalid user of direct organziation with the root msp user token ",ti.root_msp_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.POLICY.toString()},
			{ "Create policy columns for the invalid user of direct organziation with the root msp account admin user token ",ti.root_msp_org1_msp_accountadmin1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.POLICY.toString()},
			{ "Create policy columns for the invalid user of direct organziation with the customer account of root msp ",ti.root_msp1_suborg1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.POLICY.toString()},
			{ "Create policy columns for the invalid user of direct organziation with the root msp user token ",ti.root_msp1_submsp1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.POLICY.toString()},
			{ "Create policy columns for the invalid user of direct organziation with the root msp account admin user token ",ti.root_msp1_submsp1_account_admin_1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.POLICY.toString()},
			{ "Create policy columns for the invalid user of direct organziation with the customer account of root msp ",ti.msp1_submsp1_suborg1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.POLICY.toString()},


			//Backup job report columns 
			{ "Create backupjobreport columns for the invalid user of direct organziation with valid token ",ti.direct_org1_user1_id, ti.direct_org1_user1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "Create backupjobreport columns for the invalid user of direct organziation with the normal msp user token ",ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "Create backupjobreport columns for the invalid user of direct organziation with the normal msp account admin user token ",ti.normal_msp_org1_msp_accountadmin1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "Create backupjobreport columns for the invalid user of direct organziation with the customer account of normsl msp ",ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "Create backupjobreport columns for the invalid user of direct organziation with the root msp user token ",ti.root_msp_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "Create backupjobreport columns for the invalid user of direct organziation with the root msp account admin user token ",ti.root_msp_org1_msp_accountadmin1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "Create backupjobreport columns for the invalid user of direct organziation with the customer account of root msp ",ti.root_msp1_suborg1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "Create backupjobreport columns for the invalid user of direct organziation with the root msp user token ",ti.root_msp1_submsp1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "Create backupjobreport columns for the invalid user of direct organziation with the root msp account admin user token ",ti.root_msp1_submsp1_account_admin_1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "Create backupjobreport columns for the invalid user of direct organziation with the customer account of root msp ",ti.msp1_submsp1_suborg1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.BACKUP_JOB_REPORT.toString()},


			//Log columns 
			{ "Create log columns for the invalid user of direct organziation with valid token ",ti.direct_org1_user1_id, ti.direct_org1_user1_token,ColumnType.LOG.toString()},
			{ "Create log columns for the invalid user of direct organziation with the normal msp user token ",ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.LOG.toString()},
			{ "Create log columns for the invalid user of direct organziation with the normal msp account admin user token ",ti.normal_msp_org1_msp_accountadmin1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.LOG.toString()},
			{ "Create log columns for the invalid user of direct organziation with the customer account of normsl msp ",ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.LOG.toString()},
			{ "Create log columns for the invalid user of direct organziation with the root msp user token ",ti.root_msp_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.LOG.toString()},
			{ "Create log columns for the invalid user of direct organziation with the root msp account admin user token ",ti.root_msp_org1_msp_accountadmin1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.LOG.toString()},
			{ "Create log columns for the invalid user of direct organziation with the customer account of root msp ",ti.root_msp1_suborg1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.LOG.toString()},
			{ "Create log columns for the invalid user of direct organziation with the root msp user token ",ti.root_msp1_submsp1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.LOG.toString()},
			{ "Create log columns for the invalid user of direct organziation with the root msp account admin user token ",ti.root_msp1_submsp1_account_admin_1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.LOG.toString()},
			{ "Create log columns for the invalid user of direct organziation with the customer account of root msp ",ti.msp1_submsp1_suborg1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.LOG.toString()},


			//RecoveryJob report columns
			{ "Create recovery_job_report columns for the invalid user of direct organziation with valid token ",ti.direct_org1_user1_id, ti.direct_org1_user1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "Create recovery_job_report columns for the invalid user of direct organziation with the normal msp user token ",ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "Create recovery_job_report columns for the invalid user of direct organziation with the normal msp account admin user token ",ti.normal_msp_org1_msp_accountadmin1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "Create recovery_job_report columns for the invalid user of direct organziation with the customer account of normsl msp ",ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "Create recovery_job_report columns for the invalid user of direct organziation with the root msp user token ",ti.root_msp_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "Create recovery_job_report columns for the invalid user of direct organziation with the root msp account admin user token ",ti.root_msp_org1_msp_accountadmin1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "Create recovery_job_report columns for the invalid user of direct organziation with the customer account of root msp ",ti.root_msp1_suborg1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "Create recovery_job_report columns for the invalid user of direct organziation with the root msp user token ",ti.root_msp1_submsp1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "Create recovery_job_report columns for the invalid user of direct organziation with the root msp account admin user token ",ti.root_msp1_submsp1_account_admin_1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "Create recovery_job_report columns for the invalid user of direct organziation with the customer account of root msp ",ti.msp1_submsp1_suborg1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.RESTORE_JOB_REPORT.toString()},


			//Job columns 
			{ "Create job columns for the invalid user of direct organziation with valid token ",ti.direct_org1_user1_id, ti.direct_org1_user1_token,ColumnType.JOB.toString()},
			{ "Create job columns for the invalid user of direct organziation with the normal msp user token ",ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.JOB.toString()},
			{ "Create job columns for the invalid user of direct organziation with the normal msp account admin user token ",ti.normal_msp_org1_msp_accountadmin1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.JOB.toString()},
			{ "Create job columns for the invalid user of direct organziation with the customer account of normsl msp ",ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.JOB.toString()},
			{ "Create job columns for the invalid user of direct organziation with the root msp user token ",ti.root_msp_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.JOB.toString()},
			{ "Create job columns for the invalid user of direct organziation with the root msp account admin user token ",ti.root_msp_org1_msp_accountadmin1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.JOB.toString()},
			{ "Create job columns for the invalid user of direct organziation with the customer account of root msp ",ti.root_msp1_suborg1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.JOB.toString()},
			{ "Create job columns for the invalid user of direct organziation with the root msp user token ",ti.root_msp1_submsp1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.JOB.toString()},
			{ "Create job columns for the invalid user of direct organziation with the root msp account admin user token ",ti.root_msp1_submsp1_account_admin_1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.JOB.toString()},
			{ "Create job columns for the invalid user of direct organziation with the customer account of root msp ",ti.msp1_submsp1_suborg1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.JOB.toString()},

			//Destination Columns
			{ "Create destination columns for the invalid user of direct organziation with valid token ",ti.direct_org1_user1_id, ti.direct_org1_user1_token,ColumnType.DESTINATION.toString()},
			{ "Create destination columns for the invalid user of direct organziation with the normal msp user token ",ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.DESTINATION.toString()},
			{ "Create destination columns for the invalid user of direct organziation with the normal msp account admin user token ",ti.normal_msp_org1_msp_accountadmin1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.DESTINATION.toString()},
			{ "Create destination columns for the invalid user of direct organziation with the customer account of normsl msp ",ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.DESTINATION.toString()},
			{ "Create destination columns for the invalid user of direct organziation with the root msp user token ",ti.root_msp_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.DESTINATION.toString()},
			{ "Create destination columns for the invalid user of direct organziation with the root msp account admin user token ",ti.root_msp_org1_msp_accountadmin1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.DESTINATION.toString()},
			{ "Create destination columns for the invalid user of direct organziation with the customer account of root msp ",ti.root_msp1_suborg1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.DESTINATION.toString()},
			{ "Create destination columns for the invalid user of direct organziation with the root msp user token ",ti.root_msp1_submsp1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.DESTINATION.toString()},
			{ "Create destination columns for the invalid user of direct organziation with the root msp account admin user token ",ti.root_msp1_submsp1_account_admin_1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.DESTINATION.toString()},
			{ "Create destination columns for the invalid user of direct organziation with the customer account of root msp ",ti.msp1_submsp1_suborg1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.DESTINATION.toString()},

			//Alert email recipient columns
			{ "Create alert_email_recipient columns for the invalid user of direct organziation with valid token ",ti.direct_org1_user1_id, ti.direct_org1_user1_token,ColumnType.ALERT_EMAIL_RECIPIENT.toString()},
			{ "Create alert_email_recipient columns for the invalid user of direct organziation with the normal msp user token ",ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.ALERT_EMAIL_RECIPIENT.toString()},
			{ "Create alert_email_recipient columns for the invalid user of direct organziation with the normal msp account admin user token ",ti.normal_msp_org1_msp_accountadmin1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.ALERT_EMAIL_RECIPIENT.toString()},
			{ "Create alert_email_recipient columns for the invalid user of direct organziation with the customer account of normsl msp ",ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.ALERT_EMAIL_RECIPIENT.toString()},
			{ "Create alert_email_recipient columns for the invalid user of direct organziation with the root msp user token ",ti.root_msp_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.ALERT_EMAIL_RECIPIENT.toString()},
			{ "Create alert_email_recipient columns for the invalid user of direct organziation with the root msp account admin user token ",ti.root_msp_org1_msp_accountadmin1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.ALERT_EMAIL_RECIPIENT.toString()},
			{ "Create alert_email_recipient columns for the invalid user of direct organziation with the customer account of root msp ",ti.root_msp1_suborg1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.ALERT_EMAIL_RECIPIENT.toString()},
			{ "Create alert_email_recipient columns for the invalid user of direct organziation with the root msp user token ",ti.root_msp1_submsp1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.ALERT_EMAIL_RECIPIENT.toString()},
			{ "Create alert_email_recipient columns for the invalid user of direct organziation with the root msp account admin user token ",ti.root_msp1_submsp1_account_admin_1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.ALERT_EMAIL_RECIPIENT.toString()},
			{ "Create alert_email_recipient columns for the invalid user of direct organziation with the customer account of root msp ",ti.msp1_submsp1_suborg1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.ALERT_EMAIL_RECIPIENT.toString()},

			//Hypervisor columns
			{ "Create hypervisor columns for the invalid user of direct organziation with valid token ",ti.direct_org1_user1_id, ti.direct_org1_user1_token,ColumnType.HYPERVISOR.toString()},
			{ "Create hypervisor columns for the invalid user of direct organziation with the normal msp user token ",ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.HYPERVISOR.toString()},
			{ "Create hypervisor columns for the invalid user of direct organziation with the normal msp account admin user token ",ti.normal_msp_org1_msp_accountadmin1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.HYPERVISOR.toString()},
			{ "Create hypervisor columns for the invalid user of direct organziation with the customer account of normsl msp ",ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.HYPERVISOR.toString()},
			{ "Create hypervisor columns for the invalid user of direct organziation with the root msp user token ",ti.root_msp_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.HYPERVISOR.toString()},
			{ "Create hypervisor columns for the invalid user of direct organziation with the root msp account admin user token ",ti.root_msp_org1_msp_accountadmin1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.HYPERVISOR.toString()},
			{ "Create hypervisor columns for the invalid user of direct organziation with the customer account of root msp ",ti.root_msp1_suborg1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.HYPERVISOR.toString()},
			{ "Create hypervisor columns for the invalid user of direct organziation with the root msp user token ",ti.root_msp1_submsp1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.HYPERVISOR.toString()},
			{ "Create hypervisor columns for the invalid user of direct organziation with the root msp account admin user token ",ti.root_msp1_submsp1_account_admin_1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.HYPERVISOR.toString()},
			{ "Create hypervisor columns for the invalid user of direct organziation with the customer account of root msp ",ti.msp1_submsp1_suborg1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.HYPERVISOR.toString()},

			//User Columns
			{ "Create user columns for the invalid user of direct organziation with valid token ",ti.direct_org1_user1_id, ti.direct_org1_user1_token,ColumnType.USER.toString()},
			{ "Create user columns for the invalid user of direct organziation with the normal msp user token ",ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.USER.toString()},
			{ "Create user columns for the invalid user of direct organziation with the normal msp account admin user token ",ti.normal_msp_org1_msp_accountadmin1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.USER.toString()},
			{ "Create USER columns for the invalid user of direct organziation with the customer account of normsl msp ",ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.USER.toString()},
			{ "Create user columns for the invalid user of direct organziation with the root msp user token ",ti.root_msp_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.USER.toString()},
			{ "Create user columns for the invalid user of direct organziation with the root msp account admin user token ",ti.root_msp_org1_msp_accountadmin1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.USER.toString()},
			{ "Create user columns for the invalid user of direct organziation with the customer account of root msp ",ti.root_msp1_suborg1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.USER.toString()},
			{ "Create user columns for the invalid user of direct organziation with the root msp user token ",ti.root_msp1_submsp1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.USER.toString()},
			{ "Create user columns for the invalid user of direct organziation with the root msp account admin user token ",ti.root_msp1_submsp1_account_admin_1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.USER.toString()},
			{ "Create user columns for the invalid user of direct organziation with the customer account of root msp ",ti.msp1_submsp1_suborg1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.USER.toString()},

			
			//ReportColumns
			{ "Create report columns for the invalid user of direct organziation with valid token ",ti.direct_org1_user1_id, ti.direct_org1_user1_token,ColumnType.REPORT.toString()},
			{ "Create report columns for the invalid user of direct organziation with the normal msp user token ",ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.REPORT.toString()},
			{ "Create report columns for the invalid user of direct organziation with the normal msp account admin user token ",ti.normal_msp_org1_msp_accountadmin1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.REPORT.toString()},
			{ "Create report columns for the invalid user of direct organziation with the customer account of normsl msp ",ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.REPORT.toString()},
			{ "Create report columns for the invalid user of direct organziation with the root msp user token ",ti.root_msp_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.REPORT.toString()},
			{ "Create report columns for the invalid user of direct organziation with the root msp account admin user token ",ti.root_msp_org1_msp_accountadmin1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.REPORT.toString()},
			{ "Create report columns for the invalid user of direct organziation with the customer account of root msp ",ti.root_msp1_suborg1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.REPORT.toString()},
			{ "Create report columns for the invalid user of direct organziation with the root msp user token ",ti.root_msp1_submsp1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.REPORT.toString()},
			{ "Create report columns for the invalid user of direct organziation with the root msp account admin user token ",ti.root_msp1_submsp1_account_admin_1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.REPORT.toString()},
			{ "Create report columns for the invalid user of direct organziation with the customer account of root msp ",ti.msp1_submsp1_suborg1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.REPORT.toString()},

			//ManageReportSchedules Columns
			{ "Create ManagedReportSchedules columns for the invalid user of direct organziation with valid token ",ti.direct_org1_user1_id, ti.direct_org1_user1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "Create ManagedReportSchedules columns for the invalid user of direct organziation with the normal msp user token ",ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "Create ManagedReportSchedules columns for the invalid user of direct organziation with the normal msp account admin user token ",ti.normal_msp_org1_msp_accountadmin1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "Create ManagedReportSchedules columns for the invalid user of direct organziation with the customer account of normsl msp ",ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "Create ManagedReportSchedules columns for the invalid user of direct organziation with the root msp account admin user token ",ti.root_msp_org1_msp_accountadmin1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "Create ManagedReportSchedules columns for the invalid user of direct organziation with the customer account of root msp ",ti.root_msp1_suborg1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "Create ManagedReportSchedules columns for the invalid user of direct organziation with the root msp user token ",ti.root_msp1_submsp1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "Create ManagedReportSchedules columns for the invalid user of direct organziation with the root msp account admin user token ",ti.root_msp1_submsp1_account_admin_1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "Create ManagedReportSchedules columns for the invalid user of direct organziation with the customer account of root msp ",ti.msp1_submsp1_suborg1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.REPORT_SCHEDULE.toString()},

			
		};
	}
	@Test(dataProvider = "invalidUserInfo", enabled = true)
	public void createColumnsForIvalidUser(String testCase,
			String user_id,
			String token,
			String columnType 
			)
	{

		test = ExtentManager.getNewTest(testCase);
		test.assignAuthor("sykam.nagamalleswari");
		ArrayList<HashMap<String, Object>> expected_columns = new ArrayList<>();
		HashMap<String, Object> temp = new HashMap<>();

		test.log(LogStatus.INFO, "Compose data to delet the column");
		String getColumnfInfo = jp.composeColumnFilter(columnType, user_id, "none", "none");


		getDefaultColumns(token, user_id, columnType);
		spogServer.getColumns(token, getColumnfInfo);
		spogServer.setToken(token);

		test.log(LogStatus.INFO, "Compose the log columns as per the user request");
		temp = jp.composeColumn(columnIdList.get(1), "true", "true", "true", "2");
		expected_columns.add(temp);

		System.out.println(expected_columns.toString());
		test.log(LogStatus.INFO, "Create the log columns for the specified user");
		spogServer.setToken(token);
		user_id = UUID.randomUUID().toString();
		spogServer.postColumnsWithCheck(token, user_id,columnType,expected_columns, SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.USER_ID_DOESNOT_EXIST , test);
	}

	@DataProvider(name = "create_Columns_invalid_order_id_secnarioes")
	public final Object[][] createColumnsforLoggedInUser4() {

		return new Object[][] {
			{ "Create backupjobreport columns for the direct organization","order_id_0", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.BACKUP_JOB_REPORT.toString(),new String[] { "0", "0", "0", "0", "0" }, 2, SpogConstants.SUCCESS_POST },
			{ "Create backupjobreport columns for the direct organization","order_id_negative_values", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.BACKUP_JOB_REPORT.toString(),new String[] { "-1", "-1", "-1", "-1", "-1" }, 2, SpogConstants.SUCCESS_POST },
			{ "Create backupjobreport columns for the normal msp account admin ","order_id_0",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.BACKUP_JOB_REPORT.toString(), new String[] { "0", "0", "0", "0", "0" }, 1,SpogConstants.SUCCESS_POST },
			{"Create backupjobreport columns for the normal msp account admin ", "order_id_negative_values",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.BACKUP_JOB_REPORT.toString(), new String[] { "-1", "-1", "-1", "-1", "-1" }, 4,SpogConstants.SUCCESS_POST },
			{ "Create backupjobreport columns for the customer account of normal msp ","order_id_0",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.BACKUP_JOB_REPORT.toString(), new String[] { "0", "0", "0" },1,SpogConstants.SUCCESS_POST },
			{ "Create backumpjobreport columns for the customer account of normal msp ","order_id_negative_values",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.BACKUP_JOB_REPORT.toString(), new String[] { "-1", "-1", "-1" }, 2,SpogConstants.SUCCESS_POST },

			{ "Create email_alert_recepient columns for the direct organization","order_id_0", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.ALERT_EMAIL_RECIPIENT.toString(),new String[] { "0", "0", "0", "0", "0" }, 2, SpogConstants.SUCCESS_POST },
			{ "Create email_alert_recepient columns for the direct organization","order_id_negative_values", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.ALERT_EMAIL_RECIPIENT.toString(),new String[] { "-1", "-1", "-1", "-1", "-1" }, 2, SpogConstants.SUCCESS_POST },
			{ "Create email_alert_recepient columns for the normal msp account admin ","order_id_0",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.ALERT_EMAIL_RECIPIENT.toString(), new String[] { "0", "0", "0", "0", "0" }, 1,SpogConstants.SUCCESS_POST },
			{"Create email_alert_recepient columns for the normal msp account admin ", "order_id_negative_values",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.ALERT_EMAIL_RECIPIENT.toString(), new String[] { "-1", "-1", "-1", "-1", "-1" }, 4,SpogConstants.SUCCESS_POST },
			{ "Create email_alert_recepient columns for the customer account of normal msp ","order_id_0",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.ALERT_EMAIL_RECIPIENT.toString(), new String[] { "0", "0", "0" },1,SpogConstants.SUCCESS_POST },
			{ "Create email_alert_recepient columns for the customer account of normal msp ","order_id_negative_values",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.ALERT_EMAIL_RECIPIENT.toString(), new String[] { "-1", "-1", "-1" }, 2,SpogConstants.SUCCESS_POST },

			{ "Create destination columns for the direct organization","order_id_0", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.DESTINATION.toString(),new String[] { "0", "0", "0", "0", "0" }, 2, SpogConstants.SUCCESS_POST },
			{ "Create destination columns for the direct organization","order_id_negative_values", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.DESTINATION.toString(),new String[] { "-1", "-1", "-1", "-1", "-1" }, 2, SpogConstants.SUCCESS_POST },
			{ "Create destination columns for the normal msp account admin ","order_id_0",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.DESTINATION.toString(), new String[] { "0", "0", "0", "0", "0" }, 1,SpogConstants.SUCCESS_POST },
			{"Create destination columns for the normal msp account admin ", "order_id_negative_values",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.DESTINATION.toString(), new String[] { "-1", "-1", "-1", "-1", "-1" }, 4,SpogConstants.SUCCESS_POST },
			{ "Create destination columns for the customer account of normal msp ","order_id_0",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.DESTINATION.toString(), new String[] { "0", "0", "0" },1,SpogConstants.SUCCESS_POST },
			{ "Create destination columns for the customer account of normal msp ","order_id_negative_values",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.DESTINATION.toString(), new String[] { "-1", "-1", "-1" }, 2,SpogConstants.SUCCESS_POST },

			{ "Create hypervisor columns for the direct organization","order_id_0", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.HYPERVISOR.toString(),new String[] { "0", "0", "0", "0", "0" }, 2, SpogConstants.SUCCESS_POST },
			{ "Create hypervisor columns for the direct organization","order_id_negative_values", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.HYPERVISOR.toString(),new String[] { "-1", "-1", "-1", "-1", "-1" }, 2, SpogConstants.SUCCESS_POST },
			{ "Create hypervisor columns for the normal msp account admin ","order_id_0",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.HYPERVISOR.toString(), new String[] { "0", "0", "0", "0", "0" }, 1,SpogConstants.SUCCESS_POST },
			{"Create hypervisor columns for the normal msp account admin ", "order_id_negative_values",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.HYPERVISOR.toString(), new String[] { "-1", "-1", "-1", "-1", "-1" }, 4,SpogConstants.SUCCESS_POST },
			{ "Create hypervisor columns for the customer account of normal msp ","order_id_0",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.HYPERVISOR.toString(), new String[] { "0", "0", "0" },1,SpogConstants.SUCCESS_POST },
			{ "Create hypervisor columns for the customer account of normal msp ","order_id_negative_values",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.HYPERVISOR.toString(), new String[] { "-1", "-1", "-1" }, 2,SpogConstants.SUCCESS_POST },

			{ "Create job columns for the direct organization","order_id_0", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.JOB.toString(),new String[] { "0", "0", "0", "0", "0" }, 2, SpogConstants.SUCCESS_POST },
			{ "Create job columns for the direct organization","order_id_negative_values", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.JOB.toString(),new String[] { "-1", "-1", "-1", "-1", "-1" }, 2, SpogConstants.SUCCESS_POST },
			{ "Create job columns for the normal msp account admin ","order_id_0",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.JOB.toString(), new String[] { "0", "0", "0", "0", "0" }, 1,SpogConstants.SUCCESS_POST },
			{"Create job columns for the normal msp account admin ", "order_id_negative_values",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.JOB.toString(), new String[] { "-1", "-1", "-1", "-1", "-1" }, 4,SpogConstants.SUCCESS_POST },
			{ "Create job columns for the customer account of normal msp ","order_id_0",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.JOB.toString(), new String[] { "0", "0", "0" },1,SpogConstants.SUCCESS_POST },
			{ "Create job columns for the customer account of normal msp ","order_id_negative_values",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.JOB.toString(), new String[] { "-1", "-1", "-1" }, 2,SpogConstants.SUCCESS_POST },


			{ "Create log columns for the direct organization","order_id_0", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.LOG.toString(),new String[] { "0", "0", "0", "0", "0" }, 2, SpogConstants.SUCCESS_POST },
			{ "Create log columns for the direct organization","order_id_negative_values", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.LOG.toString(),new String[] { "-1", "-1", "-1", "-1", "-1" }, 2, SpogConstants.SUCCESS_POST },
			{ "Create log columns for the normal msp account admin ","order_id_0",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.LOG.toString(), new String[] { "0", "0", "0", "0", "0" }, 1,SpogConstants.SUCCESS_POST },
			{"Create log columns for the normal msp account admin ", "order_id_negative_values",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.LOG.toString(), new String[] { "-1", "-1", "-1", "-1", "-1" }, 4,SpogConstants.SUCCESS_POST },
			{ "Create log columns for the customer account of normal msp ","order_id_0",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.LOG.toString(), new String[] { "0", "0", "0" },1,SpogConstants.SUCCESS_POST },
			{ "Create log columns for the customer account of normal msp ","order_id_negative_values",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.LOG.toString(), new String[] { "-1", "-1", "-1" }, 2,SpogConstants.SUCCESS_POST },

			{ "Create policy columns for the direct organization","order_id_0", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.POLICY.toString(),new String[] { "0", "0", "0", "0", "0" }, 2, SpogConstants.SUCCESS_POST },
			{ "Create policy columns for the direct organization","order_id_negative_values", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.POLICY.toString(),new String[] { "-1", "-1", "-1", "-1", "-1" }, 2, SpogConstants.SUCCESS_POST },
			{ "Create policy columns for the normal msp account admin ","order_id_0",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.POLICY.toString(), new String[] { "0", "0", "0", "0", "0" }, 1,SpogConstants.SUCCESS_POST },
			{"Create policy columns for the normal msp account admin ", "order_id_negative_values",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.POLICY.toString(), new String[] { "-1", "-1", "-1", "-1", "-1" }, 4,SpogConstants.SUCCESS_POST },
			{ "Create policy columns for the customer account of normal msp ","order_id_0",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.POLICY.toString(), new String[] { "0", "0", "0" },1,SpogConstants.SUCCESS_POST },
			{ "Create policy columns for the customer account of normal msp ","order_id_negative_values",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.POLICY.toString(), new String[] { "-1", "-1", "-1" }, 2,SpogConstants.SUCCESS_POST },


			{ "Create policytaskReport columns for the direct organization","order_id_0", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.POLICY_TASK_REPORT.toString(),new String[] { "0", "0", "0", "0", "0" }, 2, SpogConstants.SUCCESS_POST },
			{ "Create policytaskReport columns for the direct organization","order_id_negative_values", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.POLICY_TASK_REPORT.toString(),new String[] { "-1", "-1", "-1", "-1", "-1" }, 2, SpogConstants.SUCCESS_POST },
			{ "Create policytaskReport columns for the normal msp account admin ","order_id_0",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.POLICY_TASK_REPORT.toString(), new String[] { "0", "0", "0", "0", "0" }, 1,SpogConstants.SUCCESS_POST },
			{"Create policytaskReport columns for the normal msp account admin ", "order_id_negative_values",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.POLICY_TASK_REPORT.toString(), new String[] { "-1", "-1", "-1", "-1", "-1" }, 4,SpogConstants.SUCCESS_POST },
			{ "Create policytaskReport columns for the customer account of normal msp ","order_id_0",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.POLICY_TASK_REPORT.toString(), new String[] { "0", "0", "0" },1,SpogConstants.SUCCESS_POST },
			{ "Create policytaskReport columns for the customer account of normal msp ","order_id_negative_values",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.POLICY_TASK_REPORT.toString(), new String[] { "-1", "-1", "-1" }, 2,SpogConstants.SUCCESS_POST },


			{ "Create recoveredresource columns for the direct organization","order_id_0", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.RECOVEREDRESOURCE.toString(),new String[] { "0", "0", "0", "0", "0" }, 2, SpogConstants.SUCCESS_POST },
			{ "Create recoveredresource columns for the direct organization","order_id_negative_values", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.RECOVEREDRESOURCE.toString(),new String[] { "-1", "-1", "-1", "-1", "-1" }, 2, SpogConstants.SUCCESS_POST },
			{ "Create recoveredresource columns for the normal msp account admin ","order_id_0",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.RECOVEREDRESOURCE.toString(), new String[] { "0", "0", "0", "0", "0" }, 1,SpogConstants.SUCCESS_POST },
			{"Create recoveredresource columns for the normal msp account admin ", "order_id_negative_values",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.RECOVEREDRESOURCE.toString(), new String[] { "-1", "-1", "-1", "-1", "-1" }, 4,SpogConstants.SUCCESS_POST },
			{ "Create recoveredresource columns for the customer account of normal msp ","order_id_0",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.RECOVEREDRESOURCE.toString(), new String[] { "0", "0", "0" },1,SpogConstants.SUCCESS_POST },
			{ "Create recoveredresource columns for the customer account of normal msp ","order_id_negative_values",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.RECOVEREDRESOURCE.toString(), new String[] { "-1", "-1", "-1" }, 2,SpogConstants.SUCCESS_POST },

			{ "Create restorejobReport columns for the direct organization","order_id_0", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.RESTORE_JOB_REPORT.toString(),new String[] { "0", "0", "0", "0", "0" }, 2, SpogConstants.SUCCESS_POST },
			{ "Create restorejobReport columns for the direct organization","order_id_negative_values", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.RESTORE_JOB_REPORT.toString(),new String[] { "-1", "-1", "-1", "-1", "-1" }, 2, SpogConstants.SUCCESS_POST },
			{ "Create restorejobReport columns for the normal msp account admin ","order_id_0",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.RESTORE_JOB_REPORT.toString(), new String[] { "0", "0", "0", "0", "0" }, 1,SpogConstants.SUCCESS_POST },
			{"Create restorejobReport columns for the normal msp account admin ", "order_id_negative_values",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.RESTORE_JOB_REPORT.toString(), new String[] { "-1", "-1", "-1", "-1", "-1" }, 4,SpogConstants.SUCCESS_POST },
			{ "Create restorejobReport columns for the customer account of normal msp ","order_id_0",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.RESTORE_JOB_REPORT.toString(), new String[] { "0", "0", "0" },1,SpogConstants.SUCCESS_POST },
			{ "Create restorejobReport columns for the customer account of normal msp ","order_id_negative_values",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.RESTORE_JOB_REPORT.toString(), new String[] { "-1", "-1", "-1" }, 2,SpogConstants.SUCCESS_POST },

			{ "Create source columns for the direct organization","order_id_0", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.SOURCE.toString(),new String[] { "0", "0", "0", "0", "0" }, 2, SpogConstants.SUCCESS_POST },
			{ "Create source columns for the direct organization","order_id_negative_values", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.SOURCE.toString(),new String[] { "-1", "-1", "-1", "-1", "-1" }, 2, SpogConstants.SUCCESS_POST },
			{ "Create source columns for the normal msp account admin ","order_id_0",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.SOURCE.toString(), new String[] { "0", "0", "0", "0", "0" }, 1,SpogConstants.SUCCESS_POST },
			{"Create source columns for the normal msp account admin ", "order_id_negative_values",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.SOURCE.toString(), new String[] { "-1", "-1", "-1", "-1", "-1" }, 4,SpogConstants.SUCCESS_POST },
			{ "Create source columns for the customer account of normal msp ","order_id_0",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.SOURCE.toString(), new String[] { "0", "0", "0" },1,SpogConstants.SUCCESS_POST },
			{ "Create source columns for the customer account of normal msp ","order_id_negative_values",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.SOURCE.toString(), new String[] { "-1", "-1", "-1" }, 2,SpogConstants.SUCCESS_POST },

			{ "Create user columns for the direct organization","order_id_0", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.USER.toString(),new String[] { "0", "0", "0", "0", "0" }, 2, SpogConstants.SUCCESS_POST },
			{ "Create user columns for the direct organization","order_id_negative_values", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.USER.toString(),new String[] { "-1", "-1", "-1", "-1", "-1" }, 2, SpogConstants.SUCCESS_POST },
			{ "Create user columns for the normal msp account admin ","order_id_0",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.USER.toString(), new String[] { "0", "0", "0", "0", "0" }, 1,SpogConstants.SUCCESS_POST },
			{"Create user columns for the normal msp account admin ", "order_id_negative_values",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.USER.toString(), new String[] { "-1", "-1", "-1", "-1", "-1" }, 4,SpogConstants.SUCCESS_POST },
			{ "Create user columns for the customer account of normal msp ","order_id_0",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.USER.toString(), new String[] { "0", "0", "0" },1,SpogConstants.SUCCESS_POST },
			{ "Create user columns for the customer account of normal msp ","order_id_negative_values",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.USER.toString(), new String[] { "-1", "-1", "-1" }, 2,SpogConstants.SUCCESS_POST },

			{ "Create reports columns for the direct organization","order_id_0", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.REPORT.toString(),new String[] { "0", "0", "0", "0", "0" }, 2, SpogConstants.SUCCESS_POST },
			{ "Create reports columns for the direct organization","order_id_negative_values", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.REPORT.toString(),new String[] { "-1", "-1", "-1", "-1", "-1" }, 2, SpogConstants.SUCCESS_POST },
			{ "Create reports columns for the normal msp account admin ","order_id_0",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.REPORT.toString(), new String[] { "0", "0", "0", "0", "0" }, 1,SpogConstants.SUCCESS_POST },
			{"Create reports columns for the normal msp account admin ", "order_id_negative_values",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.REPORT.toString(), new String[] { "-1", "-1", "-1", "-1", "-1" }, 4,SpogConstants.SUCCESS_POST },
			{ "Create reports columns for the customer account of normal msp ","order_id_0",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.REPORT.toString(), new String[] { "0", "0", "0" },1,SpogConstants.SUCCESS_POST },
			{ "Create reports columns for the customer account of normal msp ","order_id_negative_values",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.REPORT.toString(), new String[] { "-1", "-1", "-1" }, 2,SpogConstants.SUCCESS_POST },

			
			{ "Create managedReportsSchedulesPage columns for the direct organization","order_id_0", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.REPORT_SCHEDULE.toString(),new String[] { "0", "0", "0", "0", "0" }, 2, SpogConstants.SUCCESS_POST },
			{ "Create managedReportsSchedulesPage columns for the direct organization","order_id_negative_values", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.REPORT_SCHEDULE.toString(),new String[] { "-1", "-1", "-1", "-1", "-1" }, 2, SpogConstants.SUCCESS_POST },
			{ "Create managedReportsSchedulesPage columns for the normal msp account admin ","order_id_0",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.REPORT_SCHEDULE.toString(), new String[] { "0", "0", "0", "0", "0" }, 1,SpogConstants.SUCCESS_POST },
			{"Create managedReportsSchedulesPage columns for the normal msp account admin ", "order_id_negative_values",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.REPORT_SCHEDULE.toString(), new String[] { "-1", "-1", "-1", "-1", "-1" }, 4,SpogConstants.SUCCESS_POST },
			{ "Create managedReportsSchedulesPage columns for the customer account of normal msp ","order_id_0",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.REPORT_SCHEDULE.toString(), new String[] { "0", "0", "0" },1,SpogConstants.SUCCESS_POST },
			{ "Create managedReportsSchedulesPage columns for the customer account of normal msp ","order_id_negative_values",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.REPORT_SCHEDULE.toString(), new String[] { "-1", "-1", "-1" }, 2,SpogConstants.SUCCESS_POST },

			
			

		};
	}
	@Test(dataProvider = "create_Columns_invalid_order_id_secnarioes")
	public void createCloumswithInvalid_Order_Ids(String testCase,
			String orderid_value,
			String token,
			String user_id,
			String columnType,
			String[] orderId,
			int noofcolumstobecreated,
			int expectedStatusCode

			) {
		test = ExtentManager.getNewTest(testCase);


		spogServer.setToken(token);
		spogServer.getAndDeleteColumns(columnType, user_id, token);

		ArrayList<String> columnIdList = getDefaultColumns(token, user_id, columnType);

		ArrayList<HashMap<String, Object>>expected_columns=jp.composeColumnsInfo(noofcolumstobecreated, columnIdList, orderId, sort, filter, visible);

		if (orderid_value == "order_id_negative_values" || orderid_value == "order_id_0") {

			spogServer.postColumnsWithCheck(token, user_id, columnType, expected_columns,SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ORDER_ID_ATLEAST_1, test);


		} else if (orderid_value.contains("order_id_greate_that_7")) {
			test.log(LogStatus.INFO, "update the log columns for the specified user in the org: " );
			spogServer.postColumnsWithCheck(token, user_id, columnType, expected_columns,SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ORDER_ID_SHOULD_BE_LESS_THAN_7, test);

		}
		test.log(LogStatus.INFO, "Delete the log columns for the specified user in the org: " );
		spogServer.getAndDeleteColumns(columnType, user_id, token);
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
		rep.endTest(test);
		rep.flush();
	}

	/******************************************************************
	 * RandomFunction
	 ******************************************************************************/



}
