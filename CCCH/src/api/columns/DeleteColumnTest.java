package api.columns;

import java.io.IOException;

/**
 * @author Nagamalleswari.Sykam
 * We are Delete columns for the all type of column in  this class
 * We are covered many scenarios for all roles 
 * Delete columns for all user roles
 * Delete columns for the user if the user have already columns 
 * Insufficient scenarios 
 * Invalid and Missed token
 * Invalid and Missed token
 * Delete columns for the random uuid as user id
 * Delete columns for with invalid user columns id 
 */
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
import InvokerServer.SPOGServer;
import base.prepare.TestOrgInfo;
import dataPreparation.JsonPreparation;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class DeleteColumnTest  extends base.prepare.Is4Org {

	private SPOGServer spogServer;
	private JsonPreparation jp;
	private TestOrgInfo ti;
	private ExtentTest test;



	int passedcases = 0;
	int failedcases = 0;
	int skippedcases = 0;
	ArrayList<String> columnIdList = new ArrayList<String>();
	ArrayList<HashMap<String, Object>> columnsHeadContent = new ArrayList<HashMap<String, Object>>();

	String[] sort = new String[] { "true", "false"};
	String[] filter = new String[] { "true", "false"};
	String[] visible = new String[] { "true", "false"};


	@BeforeClass
	@Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword","csrReadOnlyAdminName","csrReadOnlyAdminPassword","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword,String csrReadOnlyAdminName,String csrReadOnlyAdminPassword, String logFolder,String runningMachine,String buildVersion) {
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
		
	}


	public ArrayList<String> getDefaultColumns(String token, String user_id, String columnType){

		String filter = jp.composeColumnFilter(columnType, user_id, "none", "none");
		Response response = spogServer.getColumns(token, filter);

		columnsHeadContent = response.then().extract().path("data.columns");
		int length = columnsHeadContent.size();
		for (int i = 0; i < length; i++) {
			HashMap<String, Object> HeadContent = columnsHeadContent.get(i);

			columnIdList.add((String) HeadContent.get("column_id"));
		}

		return columnIdList;
	}

	@DataProvider(name = "deleteColumns")
	public final Object[][] createlogcoloumnsValidParams() {

		return new Object[][] {
			// Alert Email Recipient columns
			{ "Delete alert_email_recipient columns for the direct organization", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.ALERT_EMAIL_RECIPIENT.toString(),new String[] { "2", "1", "5", "4", "3" }, 2, SpogConstants.SUCCESS_POST },
			{ "Delete alert_email_recipient columns for the  normal msp  organization",ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,ColumnType.ALERT_EMAIL_RECIPIENT.toString(), new String[] { "2", "1", "3", "4", "5" }, 3,SpogConstants.SUCCESS_POST },
			{ "Delete alert_email_recipient columns for the normal msp account admin ",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.ALERT_EMAIL_RECIPIENT.toString(), new String[] { "2", "1", "5", "3", "4" }, 4,SpogConstants.SUCCESS_POST },
			{ "Delete alert_email_recipient columns for the customer account of normal msp ",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.ALERT_EMAIL_RECIPIENT.toString(), new String[] { "3", "2", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Delete alert_email_recipient columns for the root msp organization", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, ColumnType.ALERT_EMAIL_RECIPIENT.toString(),new String[] { "2", "1", "3", "4", "5" }, 4, SpogConstants.SUCCESS_POST },
			{ "Delete alert_email_recipient columns  for the msp account admin of root  msp organziation",ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id,ColumnType.ALERT_EMAIL_RECIPIENT.toString(), new String[] { "2", "1", "5", "4", "2" }, 5,SpogConstants.SUCCESS_POST },
			{ "Delete alert_email_recipient columns for the customer account of root  msp organziation",ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,ColumnType.ALERT_EMAIL_RECIPIENT.toString(), new String[] { "2", "1", "3", "4", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Delete alert_email_recipient columns for the sub msp organization",ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,ColumnType.ALERT_EMAIL_RECIPIENT.toString(), new String[] { "2", "1", "3", "4", "5" }, 4,SpogConstants.SUCCESS_POST },
			{ "Delete alert_email_recipient columns for the msp account admin of sub  msp organziation",ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_1_id,ColumnType.ALERT_EMAIL_RECIPIENT.toString(), new String[] { "2", "3", "1", "4" }, 3,SpogConstants.SUCCESS_POST },
			{ "Delete alert_email_recipient columns for the customer account of sub  msp organziation",ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,ColumnType.ALERT_EMAIL_RECIPIENT.toString(), new String[] { "2", "1", "3", "4", "5" }, 5,SpogConstants.SUCCESS_POST },

			// CSR user Scenario
			{ "Delete  alert_email_recipient columns for the direct organization with the csr user token ",ti.csr_token, ti.msp1_submsp1_suborg1_user1_id, ColumnType.ALERT_EMAIL_RECIPIENT.toString(),new String[] { "2", "1", "5", "4", "6" }, 4, SpogConstants.SUCCESS_POST },

			// Hypervisor columns
			{ "Delete hyperviosr columns for the direct organization", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.HYPERVISOR.toString(),new String[] { "2", "1", "5", "4", "3" }, 2, SpogConstants.SUCCESS_POST },
			{ "Delete hyperviosr columns for the  normal msp  organization", ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ColumnType.HYPERVISOR.toString(),new String[] { "2", "1", "3", "4", "5" }, 3, SpogConstants.SUCCESS_POST },
			{ "Delete  hyperviosr columns for the normal msp account admin ",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.HYPERVISOR.toString(), new String[] { "2", "1", "5", "3", "4" }, 4,SpogConstants.SUCCESS_POST },
			{ "Delete  hyperviosr columns for the customer account of normal msp ",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.HYPERVISOR.toString(), new String[] { "3", "2", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Delete  hyperviosr columns for the root msp organization", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, ColumnType.HYPERVISOR.toString(),new String[] { "2", "1", "3", "4", "5" }, 4, SpogConstants.SUCCESS_POST },
			{ "Delete  hyperviosr columns  for the msp account admin of root  msp organziation",ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id,ColumnType.HYPERVISOR.toString(), new String[] { "2", "1", "5", "4", "2" }, 5,SpogConstants.SUCCESS_POST },
			{ "Delete  hyperviosr columns for the customer account of root  msp organziation",ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,ColumnType.HYPERVISOR.toString(), new String[] { "2", "1", "3", "4", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Delete  hyperviosr columns for the sub msp organization", ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp1_user1_id, ColumnType.HYPERVISOR.toString(),new String[] { "2", "1", "3", "4", "5" }, 4, SpogConstants.SUCCESS_POST },
			{ "Delete hyperviosr columns for the msp account admin of sub  msp organziation",ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_1_id,ColumnType.HYPERVISOR.toString(), new String[] { "2", "3", "1", "4" }, 3,SpogConstants.SUCCESS_POST },
			{ "Delete  hyperviosr columns for the customer account of sub  msp organziation",ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,ColumnType.HYPERVISOR.toString(), new String[] { "2", "1", "3", "4", "5" }, 5,SpogConstants.SUCCESS_POST },

			// CSR user Scenario
			{ "Delete  hyperviosr columns for the direct organization with the csr user token ", ti.csr_token,ti.msp1_submsp1_suborg1_user1_id, ColumnType.HYPERVISOR.toString(),new String[] { "2", "1", "5", "4", "6" }, 4, SpogConstants.SUCCESS_POST },



			//Recovered resources columns
			{ "Delete recovered_resources columns for the direct organization", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.RECOVEREDRESOURCE.toString(),new String[] { "2", "1", "5", "4", "3","6","7","8","9","10","11"}, 11, SpogConstants.SUCCESS_POST },
			{ "Delete recovered_resources columns for the  normal msp  organization", ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ColumnType.RECOVEREDRESOURCE.toString(),new String[] { "4", "3","6","7","8","9"}, 5, SpogConstants.SUCCESS_POST },
			{ "Delete  recovered_resources columns for the normal msp account admin ",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.RECOVEREDRESOURCE.toString(), new String[] { "3","6","7","8","9","10","11" }, 4,SpogConstants.SUCCESS_POST },
			{ "Delete  recovered_resources columns for the customer account of normal msp ",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.RECOVEREDRESOURCE.toString(), new String[] { "9","10","11"}, 2,SpogConstants.SUCCESS_POST },
			{ "Delete  recovered_resources columns for the root msp organization", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, ColumnType.RECOVEREDRESOURCE.toString(),new String[] { "3","10","11","6","7","8","9"}, 4, SpogConstants.SUCCESS_POST },
			{ "Delete  recovered_resources columns  for the msp account admin of root  msp organziation",ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id,ColumnType.RECOVEREDRESOURCE.toString(), new String[] {  "2", "1", "5", "3","7","8","10","11"}, 5,SpogConstants.SUCCESS_POST },
			{ "Delete  recovered_resources columns for the customer account of root  msp organziation",ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,ColumnType.RECOVEREDRESOURCE.toString(), new String[] { "2", "1", "5", "4", "3","6","7","8","9","10","11" },7,SpogConstants.SUCCESS_POST },
			{ "Delete  recovered_resources columns for the sub msp organization", ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp1_user1_id, ColumnType.RECOVEREDRESOURCE.toString(),new String[] {  "2", "1", "5", "4", "3","6","7","9","8","11","10",}, 4, SpogConstants.SUCCESS_POST },
			{ "Delete recovered_resources columns for the msp account admin of sub  msp organziation",ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_1_id,ColumnType.RECOVEREDRESOURCE.toString(), new String[] {  "3","6","7","8", }, 3,SpogConstants.SUCCESS_POST },
			{ "Delete  recovered_resources columns for the customer account of sub  msp organziation",ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,ColumnType.RECOVEREDRESOURCE.toString(), new String[] {  "3", "7", "9","8","11" }, 5,SpogConstants.SUCCESS_POST },

			// CSR user Scenario
			{ "Delete  recovered_resources columns for the direct organization with the csr user token ", ti.csr_token,ti.msp1_submsp1_suborg1_user1_id, ColumnType.RECOVEREDRESOURCE.toString(),new String[] { "2", "1", "5", "4", "6" }, 4, SpogConstants.SUCCESS_POST },


			//Policy columns
			{ "Delete policy columns for the direct organization", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.POLICY.toString(),new String[] { "2", "1", "5", "4", "3" }, 2, SpogConstants.SUCCESS_POST },
			{ "Delete policy columns for the  normal msp  organization",ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,ColumnType.POLICY.toString(), new String[] { "2", "1", "3", "4", "5" }, 3,SpogConstants.SUCCESS_POST },
			{ "Delete policy columns for the normal msp account admin ",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.POLICY.toString(), new String[] { "2", "1", "5", "3", "4" }, 4,SpogConstants.SUCCESS_POST },
			{ "Delete policy columns for the customer account of normal msp ",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.POLICY.toString(), new String[] { "3", "2", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Delete policy columns for the root msp organization", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, ColumnType.POLICY.toString(),new String[] { "2", "1", "3", "4", "5" }, 4, SpogConstants.SUCCESS_POST },
			{ "Delete policy columns  for the msp account admin of root  msp organziation",ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id,ColumnType.POLICY.toString(), new String[] { "2", "1", "5", "4", "2" }, 5,SpogConstants.SUCCESS_POST },
			{ "Delete policy columns for the customer account of root  msp organziation",ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,ColumnType.POLICY.toString(), new String[] { "2", "1", "3", "4", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Delete policy columns for the sub msp organization",ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,ColumnType.POLICY.toString(), new String[] { "2", "1", "3", "4", "5" }, 4,SpogConstants.SUCCESS_POST },
			{ "Delete policy columns for the msp account admin of sub  msp organziation",ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_1_id,ColumnType.POLICY.toString(), new String[] { "2", "3", "1", "4" }, 3,SpogConstants.SUCCESS_POST },
			{ "Delete policy columns for the customer account of sub  msp organziation",ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,ColumnType.POLICY.toString(), new String[] { "2", "1","6","7", "3", "4", "5" },7,SpogConstants.SUCCESS_POST },


			//CSR user 
			{ "Delete  policy columns for the direct organization with the csr user token ", ti.csr_token,ti.msp1_submsp1_suborg1_user1_id, ColumnType.POLICY.toString(),new String[] { "2", "1", "5", "4", "6" }, 4, SpogConstants.SUCCESS_POST },


			//Log columns
			{ "Delete log columns for the direct organization", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.LOG.toString(),new String[] { "2", "1", "5", "4", "3" }, 2, SpogConstants.SUCCESS_POST },
			{ "Delete log columns for the  normal msp  organization",ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,ColumnType.LOG.toString(), new String[] { "2", "1", "3", "4", "5" }, 3,SpogConstants.SUCCESS_POST },
			{ "Delete log columns for the normal msp account admin ",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.LOG.toString(), new String[] { "2", "1", "5", "3", "4" }, 4,SpogConstants.SUCCESS_POST },
			{ "Delete log columns for the customer account of normal msp ",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.LOG.toString(), new String[] { "3", "2", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Delete log columns for the root msp organization", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, ColumnType.LOG.toString(),new String[] { "2", "1", "3", "4", "5" }, 4, SpogConstants.SUCCESS_POST },
			{ "Delete log columns  for the msp account admin of root  msp organziation",ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id,ColumnType.LOG.toString(), new String[] { "2", "1", "5", "4", "2" }, 5,SpogConstants.SUCCESS_POST },
			{ "Delete log columns for the customer account of root  msp organziation",ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,ColumnType.LOG.toString(), new String[] { "2", "1", "3", "4", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Delete log columns for the sub msp organization",ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,ColumnType.LOG.toString(), new String[] { "2", "1", "3", "4", "5" }, 4,SpogConstants.SUCCESS_POST },
			{ "Delete log columns for the msp account admin of sub  msp organziation",ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_1_id,ColumnType.LOG.toString(), new String[] { "2", "3", "1", "4" }, 3,SpogConstants.SUCCESS_POST },
			{ "Delete log columns for the customer account of sub  msp organziation",ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,ColumnType.LOG.toString(), new String[] { "2", "1","6","7", "3", "4", "5" },7,SpogConstants.SUCCESS_POST },


			//Csr 
			{ "Delete  log columns for the direct organization with the csr user token ", ti.csr_token,ti.msp1_submsp1_suborg1_user1_id, ColumnType.LOG.toString(),new String[] { "2", "1", "5", "4", "6" }, 4, SpogConstants.SUCCESS_POST },

			//User Columns
			{ "Delete user columns for the direct organization", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.USER.toString(),new String[] { "2", "1", "5", "4", "3" }, 2, SpogConstants.SUCCESS_POST },
			{ "Delete user columns for the  normal msp  organization",ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,ColumnType.USER.toString(), new String[] { "2", "1", "3", "4", "5" }, 3,SpogConstants.SUCCESS_POST },
			{ "Delete user columns for the normal msp account admin ",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.USER.toString(), new String[] { "2", "1", "5", "3", "4" }, 4,SpogConstants.SUCCESS_POST },
			{ "Delete user columns for the customer account of normal msp ",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.USER.toString(), new String[] { "3", "2", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Delete user columns for the root msp organization", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, ColumnType.USER.toString(),new String[] { "2", "1", "3", "4", "5" }, 4, SpogConstants.SUCCESS_POST },
			{ "Delete user columns  for the msp account admin of root  msp organziation",ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id,ColumnType.USER.toString(), new String[] { "2", "1", "5", "4", "2" }, 5,SpogConstants.SUCCESS_POST },
			{ "Delete user columns for the customer account of root  msp organziation",ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,ColumnType.USER.toString(), new String[] { "2", "1", "3", "4", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Delete user columns for the sub msp organization",ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,ColumnType.USER.toString(), new String[] { "2", "1", "3", "4", "5" }, 4,SpogConstants.SUCCESS_POST },
			{ "Delete user columns for the msp account admin of sub  msp organziation",ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_1_id,ColumnType.USER.toString(), new String[] { "2", "3", "1", "4" }, 3,SpogConstants.SUCCESS_POST },
			{ "Delete user columns for the customer account of sub  msp organziation",ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,ColumnType.USER.toString(), new String[] { "2", "1","6","7", "3", "4", "5" },7,SpogConstants.SUCCESS_POST },


			//Csr 
			{ "Delete  user columns for the direct organization with the csr user token ", ti.csr_token,ti.msp1_submsp1_suborg1_user1_id, ColumnType.USER.toString(),new String[] { "2", "1", "5", "4", "6" }, 4, SpogConstants.SUCCESS_POST },

			//Source columns 
			{ "Delete source columns for the direct organization", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.SOURCE.toString(),new String[] { "2", "1", "5", "4", "3" }, 2, SpogConstants.SUCCESS_POST },
			{ "Delete source columns for the  normal msp  organization",ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,ColumnType.SOURCE.toString(), new String[] { "2", "1", "3", "4", "5" }, 3,SpogConstants.SUCCESS_POST },
			{ "Delete source columns for the normal msp account admin ",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.SOURCE.toString(), new String[] { "2", "1", "5", "3", "4","8","12","10","7" }, 9,SpogConstants.SUCCESS_POST },
			{ "Delete source columns for the customer account of normal msp ",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.SOURCE.toString(), new String[] { "3", "2", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Delete source columns for the root msp organization", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, ColumnType.SOURCE.toString(),new String[] { "2", "1", "3", "4", "5" }, 4, SpogConstants.SUCCESS_POST },
			{ "Delete source columns  for the msp account admin of root  msp organziation",ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id,ColumnType.SOURCE.toString(), new String[] { "2", "1", "5", "4", "2" }, 5,SpogConstants.SUCCESS_POST },
			{ "Delete source columns for the customer account of root  msp organziation",ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,ColumnType.SOURCE.toString(), new String[] { "2", "1", "3", "4", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Delete source columns for the sub msp organization",ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,ColumnType.SOURCE.toString(), new String[] { "2", "1", "3", "4", "5" }, 4,SpogConstants.SUCCESS_POST },
			{ "Delete source columns for the msp account admin of sub  msp organziation",ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_1_id,ColumnType.SOURCE.toString(), new String[] { "2", "3", "1", "4" }, 3,SpogConstants.SUCCESS_POST },
			{ "Delete source columns for the customer account of sub  msp organziation",ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,ColumnType.SOURCE.toString(), new String[] { "2", "1","6","7", "3", "4", "5","9","11","8","10","12" },12,SpogConstants.SUCCESS_POST },


			//Csr 
			{ "Delete  source columns for the direct organization with the csr user token ", ti.csr_token,ti.msp1_submsp1_suborg1_user1_id, ColumnType.SOURCE.toString(),new String[] { "2", "1", "5", "4", "6" }, 4, SpogConstants.SUCCESS_POST },

			//Recovery job report columns 
			{ "Delete recovery_job_report columns for the direct organization", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.RESTORE_JOB_REPORT.toString(),new String[] { "2", "1", "5", "4", "3" }, 2, SpogConstants.SUCCESS_POST },
			{ "Delete recovery_job_report columns for the  normal msp  organization",ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,ColumnType.RESTORE_JOB_REPORT.toString(), new String[] { "2", "1", "3", "4", "5" }, 3,SpogConstants.SUCCESS_POST },
			{ "Delete recovery_job_report columns for the normal msp account admin ",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.RESTORE_JOB_REPORT.toString(), new String[] { "2", "1", "5", "3", "4","8","12","10","7" }, 9,SpogConstants.SUCCESS_POST },
			{ "Delete recovery_job_report columns for the customer account of normal msp ",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.RESTORE_JOB_REPORT.toString(), new String[] { "3", "2", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Delete recovery_job_report columns for the root msp organization", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, ColumnType.RESTORE_JOB_REPORT.toString(),new String[] { "2", "1", "3", "4", "5" }, 4, SpogConstants.SUCCESS_POST },
			{ "Delete recovery_job_report columns  for the msp account admin of root  msp organziation",ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id,ColumnType.RESTORE_JOB_REPORT.toString(), new String[] { "2", "1", "5", "4", "2" }, 5,SpogConstants.SUCCESS_POST },
			{ "Delete recovery_job_report columns for the customer account of root  msp organziation",ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,ColumnType.RESTORE_JOB_REPORT.toString(), new String[] { "2", "1", "3", "4", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Delete recovery_job_report columns for the sub msp organization",ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,ColumnType.RESTORE_JOB_REPORT.toString(), new String[] { "2", "1", "3", "4", "5" }, 4,SpogConstants.SUCCESS_POST },
			{ "Delete recovery_job_report columns for the msp account admin of sub  msp organziation",ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_1_id,ColumnType.RESTORE_JOB_REPORT.toString(), new String[] { "2", "3", "1", "4" }, 3,SpogConstants.SUCCESS_POST },
			{ "Delete recovery_job_report columns for the customer account of sub  msp organziation",ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,ColumnType.RESTORE_JOB_REPORT.toString(), new String[] { "2", "1","6","7", "3", "4", "5","9","11","8","10","12" },12,SpogConstants.SUCCESS_POST },


			//Csr 
			{ "Delete  recovery_job_report columns for the direct organization with the csr user token ", ti.csr_token,ti.msp1_submsp1_suborg1_user1_id, ColumnType.RESTORE_JOB_REPORT.toString(),new String[] { "2", "1", "5", "4", "6" }, 4, SpogConstants.SUCCESS_POST },

			//Backup job report columns 
			{ "Delete backup_job_report columns for the direct organization", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.BACKUP_JOB_REPORT.toString(),new String[] { "2", "1", "5", "4", "3" }, 2, SpogConstants.SUCCESS_POST },
			{ "Delete backup_job_report columns for the  normal msp  organization",ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,ColumnType.BACKUP_JOB_REPORT.toString(), new String[] { "2", "1", "3", "4", "5" }, 3,SpogConstants.SUCCESS_POST },
			{ "Delete backup_job_report columns for the normal msp account admin ",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.BACKUP_JOB_REPORT.toString(), new String[] { "2", "1", "5", "3", "4","8","12","10","7","13" }, 9,SpogConstants.SUCCESS_POST },
			{ "Delete backup_job_report columns for the customer account of normal msp ",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.BACKUP_JOB_REPORT.toString(), new String[] { "3", "2", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Delete backup_job_report columns for the root msp organization", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, ColumnType.BACKUP_JOB_REPORT.toString(),new String[] { "2", "1", "3", "4", "5" }, 4, SpogConstants.SUCCESS_POST },
			{ "Delete backup_job_report columns  for the msp account admin of root  msp organziation",ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id,ColumnType.BACKUP_JOB_REPORT.toString(), new String[] { "2", "1", "12", "4", "2" }, 5,SpogConstants.SUCCESS_POST },
			{ "Delete backup_job_report columns for the customer account of root  msp organziation",ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,ColumnType.BACKUP_JOB_REPORT.toString(), new String[] { "2", "13", "12", "4", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Delete backup_job_report columns for the sub msp organization",ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,ColumnType.BACKUP_JOB_REPORT.toString(), new String[] { "2", "1", "3", "4", "5" }, 4,SpogConstants.SUCCESS_POST },
			{ "Delete backup_job_report columns for the msp account admin of sub  msp organziation",ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_1_id,ColumnType.BACKUP_JOB_REPORT.toString(), new String[] { "2", "3", "1", "4" }, 3,SpogConstants.SUCCESS_POST },
			{ "Delete backup_job_report columns for the customer account of sub  msp organziation",ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,ColumnType.BACKUP_JOB_REPORT.toString(), new String[] { "2", "1","6","13", "3", "4", "5","9","11","8","10","12" },12,SpogConstants.SUCCESS_POST },


			//Csr 
			{ "Delete  backup_job_report columns for the direct organization with the csr user token ", ti.csr_token,ti.msp1_submsp1_suborg1_user1_id, ColumnType.BACKUP_JOB_REPORT.toString(),new String[] { "2", "1", "5", "4", "6" }, 4, SpogConstants.SUCCESS_POST },



			//Job columns 
			{ "Delete job columns for the direct organization", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.JOB.toString(),new String[] { "2", "1", "5", "4", "3" }, 2, SpogConstants.SUCCESS_POST },
			{ "Delete job columns for the  normal msp  organization",ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,ColumnType.JOB.toString(), new String[] { "2", "1", "3", "4", "5" }, 3,SpogConstants.SUCCESS_POST },
			{ "Delete job columns for the normal msp account admin ",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.JOB.toString(), new String[] { "2", "1", "5", "3", "4","8","9","7"}, 6,SpogConstants.SUCCESS_POST },
			{ "Delete job columns for the customer account of normal msp ",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.JOB.toString(), new String[] { "3", "2", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Delete job columns for the root msp organization", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, ColumnType.JOB.toString(),new String[] { "2", "1", "3", "4", "5" }, 4, SpogConstants.SUCCESS_POST },
			{ "Delete job columns  for the msp account admin of root  msp organziation",ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id,ColumnType.JOB.toString(), new String[] { "2", "1", "7", "4", "2" }, 5,SpogConstants.SUCCESS_POST },
			{ "Delete job columns for the customer account of root  msp organziation",ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,ColumnType.JOB.toString(), new String[] { "2", "8", "9", "4", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Delete job columns for the sub msp organization",ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,ColumnType.JOB.toString(), new String[] { "2", "1", "3", "4", "5" }, 4,SpogConstants.SUCCESS_POST },
			{ "Delete job columns for the msp account admin of sub  msp organziation",ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_1_id,ColumnType.JOB.toString(), new String[] { "2", "3", "1", "4" }, 3,SpogConstants.SUCCESS_POST },
			{ "Delete job columns for the customer account of sub  msp organziation",ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,ColumnType.JOB.toString(), new String[] { "2", "1","6","3", "4", "5","9","8",},5,SpogConstants.SUCCESS_POST },


			//Csr 
			{ "Delete  job columns for the direct organization with the csr user token ", ti.csr_token,ti.msp1_submsp1_suborg1_user1_id, ColumnType.JOB.toString(),new String[] { "2", "1", "5", "4", "6" }, 4, SpogConstants.SUCCESS_POST },


			///Destination columns 
			{ "Delete destination columns for the direct organization", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.DESTINATION.toString(),new String[] { "10", "1", "5", "4", "3" }, 2, SpogConstants.SUCCESS_POST },
			{ "Delete destination columns for the  normal msp  organization",ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,ColumnType.DESTINATION.toString(), new String[] { "2", "1", "3", "4", "5" }, 3,SpogConstants.SUCCESS_POST },
			{ "Delete destination columns for the normal msp account admin ",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.DESTINATION.toString(), new String[] { "2", "1", "5", "3", "4","8","9","7"}, 6,SpogConstants.SUCCESS_POST },
			{ "Delete destination columns for the customer account of normal msp ",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.DESTINATION.toString(), new String[] { "3", "10", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Delete destination columns for the root msp organization", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, ColumnType.DESTINATION.toString(),new String[] { "2", "1", "3", "4", "5" }, 4, SpogConstants.SUCCESS_POST },
			{ "Delete destination columns  for the msp account admin of root  msp organziation",ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id,ColumnType.DESTINATION.toString(), new String[] { "2", "1", "7", "4", "2" }, 5,SpogConstants.SUCCESS_POST },
			{ "Delete destination columns for the customer account of root  msp organziation",ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,ColumnType.DESTINATION.toString(), new String[] { "2", "10", "9", "4", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Delete destination columns for the sub msp organization",ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,ColumnType.DESTINATION.toString(), new String[] { "2", "1", "3", "4", "5" }, 4,SpogConstants.SUCCESS_POST },
			{ "Delete destination columns for the msp account admin of sub  msp organziation",ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_1_id,ColumnType.DESTINATION.toString(), new String[] { "2", "3", "1", "4" }, 3,SpogConstants.SUCCESS_POST },
			{ "Delete destination columns for the customer account of sub  msp organziation",ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,ColumnType.DESTINATION.toString(), new String[] { "2", "1","6","3", "4", "5","9","8",},5,SpogConstants.SUCCESS_POST },


			//Csr 
			{ "Delete  destination columns for the direct organization with the csr user token ", ti.csr_token,ti.msp1_submsp1_suborg1_user1_id, ColumnType.DESTINATION.toString(),new String[] { "2", "1", "5", "10", "6" }, 4, SpogConstants.SUCCESS_POST },

			//POLICY_TASK_REPORT columns 
			{ "Delete policy_task_report  columns for the direct organization", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.POLICY_TASK_REPORT.toString(),new String[] { "10", "1", "5", "4", "3" }, 2, SpogConstants.SUCCESS_POST },
			{ "Delete policy_task_report  columns for the  normal msp  organization",ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,ColumnType.POLICY_TASK_REPORT.toString(), new String[] { "11", "1", "3", "4", "5" }, 3,SpogConstants.SUCCESS_POST },
			{ "Delete policy_task_report  columns for the normal msp account admin ",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.POLICY_TASK_REPORT.toString(), new String[] { "2", "1", "5", "3", "4","8","9","7"}, 6,SpogConstants.SUCCESS_POST },
			{ "Delete policy_task_report  columns for the customer account of normal msp ",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.POLICY_TASK_REPORT.toString(), new String[] { "3", "10", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Delete policy_task_report  columns for the root msp organization", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, ColumnType.POLICY_TASK_REPORT.toString(),new String[] { "2", "1", "11", "4", "5" }, 4, SpogConstants.SUCCESS_POST },
			{ "Delete policy_task_report  columns  for the msp account admin of root  msp organziation",ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id,ColumnType.POLICY_TASK_REPORT.toString(), new String[] { "2", "1", "7", "4", "2" }, 5,SpogConstants.SUCCESS_POST },
			{ "Delete policy_task_report  columns for the customer account of root  msp organziation",ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,ColumnType.POLICY_TASK_REPORT.toString(), new String[] { "2", "10", "9", "4", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Delete policy_task_report  columns for the sub msp organization",ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,ColumnType.POLICY_TASK_REPORT.toString(), new String[] { "2", "1", "3", "11", "5" }, 4,SpogConstants.SUCCESS_POST },
			{ "Delete policy_task_report  columns for the msp account admin of sub  msp organziation",ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_1_id,ColumnType.POLICY_TASK_REPORT.toString(), new String[] { "2", "3", "1", "4" }, 3,SpogConstants.SUCCESS_POST },
			{ "Delete policy_task_report  columns for the customer account of sub  msp organziation",ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,ColumnType.POLICY_TASK_REPORT.toString(), new String[] { "2", "1","6","3", "4", "5","9","8",},5,SpogConstants.SUCCESS_POST },


			//Csr 
			{ "Delete  policy_task_report  columns for the direct organization with the csr user token ", ti.csr_token,ti.msp1_submsp1_suborg1_user1_id, ColumnType.POLICY_TASK_REPORT.toString(),new String[] { "2", "1", "5", "10", "6" }, 4, SpogConstants.SUCCESS_POST },
		
			//Report columns 
			{ "Delete report columns for the direct organization", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.REPORT.toString(),new String[] {  "1", "5", "4", "3" }, 2, SpogConstants.SUCCESS_POST },
			{ "Delete report columns for the  normal msp  organization",ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,ColumnType.REPORT.toString(), new String[] {  "1", "3", "4", "5" }, 3,SpogConstants.SUCCESS_POST },
			{ "Delete report columns for the normal msp account admin ",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.REPORT.toString(), new String[] { "2", "1", "5", "3", "4","8","7"}, 6,SpogConstants.SUCCESS_POST },
			{ "Delete report columns for the customer account of normal msp ",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.REPORT.toString(), new String[] { "3","5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Delete report columns for the root msp organization", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, ColumnType.REPORT.toString(),new String[] { "2", "1", "4", "5" }, 4, SpogConstants.SUCCESS_POST },
			{ "Delete report columns  for the msp account admin of root  msp organziation",ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id,ColumnType.REPORT.toString(), new String[] { "2", "1", "7", "4", "2" }, 5,SpogConstants.SUCCESS_POST },
			{ "Delete report columns for the customer account of root  msp organziation",ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,ColumnType.REPORT.toString(), new String[] { "2", "4", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Delete report columns for the sub msp organization",ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,ColumnType.REPORT.toString(), new String[] { "2", "1", "3","5" }, 4,SpogConstants.SUCCESS_POST },
			{ "Delete report columns for the msp account admin of sub  msp organziation",ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_1_id,ColumnType.REPORT.toString(), new String[] { "2", "3", "1", "4" }, 3,SpogConstants.SUCCESS_POST },
			{ "Delete report columns for the customer account of sub  msp organziation",ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,ColumnType.REPORT.toString(), new String[] { "2", "1","6","3", "4", "5",},5,SpogConstants.SUCCESS_POST },


			//Csr 
			{ "Delete report columns for the direct organization with the csr user token ", ti.csr_token,ti.msp1_submsp1_suborg1_user1_id, ColumnType.REPORT.toString(),new String[] { "2", "1", "5", "6" }, 4, SpogConstants.SUCCESS_POST },
		
			//ManagedScheduleReport columns 
			{ "Delete ManagedreportSchedules columns for the direct organization", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.REPORT_SCHEDULE.toString(),new String[] { "1", "5", "4", "3" }, 2, SpogConstants.SUCCESS_POST },
			{ "Delete ManagedreportSchedules columns for the  normal msp  organization",ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,ColumnType.REPORT_SCHEDULE.toString(), new String[] {  "1", "3", "4", "5" }, 3,SpogConstants.SUCCESS_POST },
			{ "Delete ManagedreportSchedules columns for the normal msp account admin ",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.REPORT_SCHEDULE.toString(), new String[] { "2", "1", "5", "3", "4","7"}, 6,SpogConstants.SUCCESS_POST },
			{ "Delete ManagedreportSchedules columns for the customer account of normal msp ",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.REPORT_SCHEDULE.toString(), new String[] { "3", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Delete ManagedreportSchedules columns for the root msp organization", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, ColumnType.REPORT_SCHEDULE.toString(),new String[] { "2", "1","4", "5" }, 4, SpogConstants.SUCCESS_POST },
			{ "Delete ManagedreportSchedules columns  for the msp account admin of root  msp organziation",ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id,ColumnType.REPORT_SCHEDULE.toString(), new String[] { "2", "1", "7", "4", "2" }, 5,SpogConstants.SUCCESS_POST },
			{ "Delete ManagedreportSchedules columns for the customer account of root  msp organziation",ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,ColumnType.REPORT_SCHEDULE.toString(), new String[] { "2", "4", "5" }, 2,SpogConstants.SUCCESS_POST },
			{ "Delete ManagedreportSchedules columns for the sub msp organization",ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,ColumnType.REPORT_SCHEDULE.toString(), new String[] { "2", "1", "3", "5" }, 4,SpogConstants.SUCCESS_POST },
			{ "Delete ManagedreportSchedules columns for the msp account admin of sub  msp organziation",ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_1_id,ColumnType.REPORT_SCHEDULE.toString(), new String[] { "2", "3", "1", "4" }, 3,SpogConstants.SUCCESS_POST },
			{ "Delete ManagedreportSchedules columns for the customer account of sub  msp organziation",ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,ColumnType.REPORT_SCHEDULE.toString(), new String[] { "2", "1","6","3", "4", "5",},5,SpogConstants.SUCCESS_POST },


			//Csr 
			{ "Delete ManagedreportSchedules columns for the direct organization with the csr user token ", ti.csr_token,ti.msp1_submsp1_suborg1_user1_id, ColumnType.REPORT_SCHEDULE.toString(),new String[] { "2", "1", "5", "10", "6" }, 4, SpogConstants.SUCCESS_POST },
		
		
		
		
		};
	}

	@Test(dataProvider = "deleteColumns",enabled=true)
	public void deleteColumns(String testCase,
			String token,
			String user_id,
			String columnType,
			String[] orderId,
			int noofcolumstobecreated,
			int expectedStatusCode
			) {
		test = ExtentManager.getNewTest(testCase);
		test.assignAuthor("sykam.nagamalleswari");		

		spogServer.getAndDeleteColumns(columnType, user_id, token);

		ArrayList<String> columnIdList = getDefaultColumns(token, user_id, columnType);

		test.log(LogStatus.INFO, "Compose the log columns as per the user request");
		ArrayList<HashMap<String, Object>>expected_columns=jp.composeColumnsInfo(noofcolumstobecreated, columnIdList, orderId, sort, filter, visible);

		test.log(LogStatus.INFO, "Create the log columns for the specified user");
		Response response =	spogServer.postColumnsWithCheck(token, user_id,columnType, expected_columns, expectedStatusCode,null, test);

		test.log(LogStatus.INFO, "Compose data to delet the column");
		String deleteColuminfo = jp.composeColumnFilter(columnType, user_id, "none", "none");

		test.log(LogStatus.INFO, "Get the columns to know the user_column_id");
		response = spogServer.getColumns(token, deleteColuminfo);

		test.log(LogStatus.INFO, "the User column id Info");
		String delete_Column_id= response.then().extract().path("data.user_column_id");

		test.log(LogStatus.INFO, "Delete the user Column Info");
		spogServer.deleteColumnsWithCheck(token, delete_Column_id,SpogConstants.SUCCESS_GET_PUT_DELETE,null,test);


		test.log(LogStatus.INFO, "Delete the already deleted columns");
		spogServer.deleteColumnsWithCheck(token, delete_Column_id,SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.COLUMN_ID_DOESNOT_EXIST,test);

	}



	@DataProvider(name = "deleteColumns_insufficientPermissions")
	public final Object[][] deleteColumnsInsufficientParams() {

		return new Object[][] {
			//Policy task reports 
			{ "delete policy task columns for the direct organization with the another direct user  ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.direct_org2_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "delete policy task columns for the direct organization with the normal msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "delete policy task columns for the direct organization with the normal msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "delete policy task columns for the direct organization with the customer account of normsl msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "delete policy task columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "delete policy task columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Delete policy task columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Delete policy task columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Delete policy task columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Delete policy task columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},


			//Recovered resources 
			{ "Delete recovered resources columns for the direct organization with the another direct user  ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.direct_org2_user1_token,ColumnType.RECOVEREDRESOURCE.toString()},
			{ "Delete recovered resources columns for the direct organization with the normal msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.RECOVEREDRESOURCE.toString()},
			{ "Delete recovered resources columns for the direct organization with the normal msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.RECOVEREDRESOURCE.toString()},
			{ "Delete recovered resources columns for the direct organization with the customer account of normsl msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.RECOVEREDRESOURCE.toString()},
			{ "Delete recovered resources columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.RECOVEREDRESOURCE.toString()},
			{ "Delete recovered resources columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.RECOVEREDRESOURCE.toString()},
			{ "Delete recovered resources columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.RECOVEREDRESOURCE.toString()},
			{ "Delete recovered resources columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.RECOVEREDRESOURCE.toString()},
			{ "Delete recovered resources columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.RECOVEREDRESOURCE.toString()},
			{ "Delete recovered resources columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.RECOVEREDRESOURCE.toString()},


			//Backup job reports 
			{ "Delete backup job report columns for the direct organization with the another direct user  ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.direct_org2_user1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "Delete backup job report columns for the direct organization with the normal msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "Delete backup job report columns for the direct organization with the normal msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "Delete backup job report columns for the direct organization with the customer account of normsl msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "Delete backup job report columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "Delete backup job report columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "Delete backup job report columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "Delete backup job report columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "Delete backup job report columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "Delete backup job report columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.BACKUP_JOB_REPORT.toString()},

			//log columns
			{ "Delete log columns for the direct organization with the another direct user  ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.direct_org2_user1_token,ColumnType.LOG.toString()},
			{ "Delete log columns for the direct organization with the normal msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.LOG.toString()},
			{ "Delete log columns for the direct organization with the normal msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.LOG.toString()},
			{ "Delete log columns for the direct organization with the customer account of normsl msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.LOG.toString()},
			{ "Delete log columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.LOG.toString()},
			{ "Delete log columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.LOG.toString()},
			{ "Delete log columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.LOG.toString()},
			{ "Delete log columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.LOG.toString()},
			{ "Delete log columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.LOG.toString()},
			{ "Delete log columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.LOG.toString()},


			//Recovery job reports 
			{ "Delete recovery_job_report columns for the direct organization with the another direct user  ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.direct_org2_user1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "Delete recovery_job_report columns for the direct organization with the normal msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "Delete recovery_job_report columns for the direct organization with the normal msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "Delete recovery_job_report columns for the direct organization with the customer account of normsl msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "Delete recovery_job_report columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "Delete recovery_job_report columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "Delete recovery_job_report columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "Delete recovery_job_report columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "Delete recovery_job_report columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "Delete lrecovery_job_report  columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.RESTORE_JOB_REPORT.toString()},

			//Source columns 
			{ "Delete source columns for the direct organization with the another direct user  ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.direct_org2_user1_token,ColumnType.SOURCE.toString()},
			{ "Delete source columns for the direct organization with the normal msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.SOURCE.toString()},
			{ "Delete source columns for the direct organization with the normal msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.SOURCE.toString()},
			{ "Delete source columns for the direct organization with the customer account of normsl msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.SOURCE.toString()},
			{ "Delete source columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.SOURCE.toString()},
			{ "Delete source columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.SOURCE.toString()},
			{ "Delete source columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.SOURCE.toString()},
			{ "Delete source columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.SOURCE.toString()},
			{ "Delete source columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.SOURCE.toString()},
			{ "Delete source  columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.SOURCE.toString()},


			//Job columns 
			{ "Delete job columns for the direct organization with the another direct user  ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.direct_org2_user1_token,ColumnType.JOB.toString()},
			{ "Delete job columns for the direct organization with the normal msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.JOB.toString()},
			{ "Delete job columns for the direct organization with the normal msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.JOB.toString()},
			{ "Delete job columns for the direct organization with the customer account of normsl msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.JOB.toString()},
			{ "Delete job columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.JOB.toString()},
			{ "Delete job columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.JOB.toString()},
			{ "Delete job columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.JOB.toString()},
			{ "Delete job columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.JOB.toString()},
			{ "Delete job columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.JOB.toString()},
			{ "Delete job columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.JOB.toString()},

			//Destination columns 
			{ "Delete destiantion columns for the direct organization with the another direct user  ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.direct_org2_user1_token,ColumnType.DESTINATION.toString()},
			{ "Delete destiantion columns for the direct organization with the normal msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.DESTINATION.toString()},
			{ "Delete destiantion columns for the direct organization with the normal msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.DESTINATION.toString()},
			{ "Delete destiantion columns for the direct organization with the customer account of normsl msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.DESTINATION.toString()},
			{ "Delete destiantion columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.DESTINATION.toString()},
			{ "Delete destiantion columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.DESTINATION.toString()},
			{ "Delete destiantion columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.DESTINATION.toString()},
			{ "Delete destiantion columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.DESTINATION.toString()},
			{ "Delete destiantion columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.DESTINATION.toString()},
			{ "Delete destiantion columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.DESTINATION.toString()},

			//PolicyTask columns 
			{ "Delete policytaskReport columns for the direct organization with the another direct user  ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.direct_org2_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Delete policytaskReport columns for the direct organization with the normal msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Delete policytaskReport columns for the direct organization with the normal msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Delete policytaskReport columns for the direct organization with the customer account of normsl msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Delete policytaskReport columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Delete policytaskReport columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Delete policytaskReport columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Delete policytaskReport columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Delete policytaskReport columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Delete policytaskReport columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},


			//user columns 
			{ "Delete user columns for the direct organization with the another direct user  ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.direct_org2_user1_token,ColumnType.USER.toString()},
			{ "Delete user columns for the direct organization with the normal msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.USER.toString()},
			{ "Delete user columns for the direct organization with the normal msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.USER.toString()},
			{ "Delete user columns for the direct organization with the customer account of normsl msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.USER.toString()},
			{ "Delete user columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.USER.toString()},
			{ "Delete user columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.USER.toString()},
			{ "Delete user columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.USER.toString()},
			{ "Delete user columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.USER.toString()},
			{ "Delete user columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.USER.toString()},
			{ "Delete user columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.USER.toString()},


			{ "Delete user columns for the direct organization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.USER.toString()},

			
			//report columns 
			{ "Delete report columns for the direct organization with the another direct user  ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.direct_org2_user1_token,ColumnType.REPORT.toString()},
			{ "Delete report columns for the direct organization with the normal msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.REPORT.toString()},
			{ "Delete report columns for the direct organization with the normal msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.REPORT.toString()},
			{ "Delete report columns for the direct organization with the customer account of normsl msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.REPORT.toString()},
			{ "Delete report columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.REPORT.toString()},
			{ "Delete report columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.REPORT.toString()},
			{ "Delete report columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.REPORT.toString()},
			{ "Delete report columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.REPORT.toString()},
			{ "Delete report columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.REPORT.toString()},
			{ "Delete report columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.REPORT.toString()},

			
			//ManagedReportSchedulesReports columns 
			{ "Delete ManagedReportSchedulesReports columns for the direct organization with the another direct user  ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.direct_org2_user1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "Delete ManagedReportSchedulesReports columns for the direct organization with the normal msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "Delete ManagedReportSchedulesReports columns for the direct organization with the normal msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "Delete ManagedReportSchedulesReports columns for the direct organization with the customer account of normsl msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "Delete ManagedReportSchedulesReports columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "Delete ManagedReportSchedulesReports columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "Delete ManagedReportSchedulesReports columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "Delete ManagedReportSchedulesReports columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "Delete ManagedReportSchedulesReports columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "Delete ManagedReportSchedulesReports columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.REPORT_SCHEDULE.toString()},


			
			
			//Normal msp 

			//Policy task reports 
			{ "Delete policy task columns for the normal msp organization with the another direct user  ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.direct_org2_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Delete policy task columns for the normal msp organization with the normal msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Delete policy task columns for the normal msp organization with the normal msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_msp_accountadmin1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Delete policy task columns for the normal msp organization with the customer account of normsl msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Delete policy task columns for the normal msp organization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Delete policy task columns for the normal msp organization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Delete policy task columns for the normal msp organization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Delete policy task columns for the normal msp organization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Delete policy task columns for the normal msp organization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Delete policy task columns for the normal msp organization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},


			//Recovered resources 
			{ "Delete recovered resources columns for the normal msporganization with the another direct user  ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.direct_org2_user1_token,ColumnType.RECOVEREDRESOURCE.toString()},
			{ "Delete recovered resources columns for the normal msporganization with the normal msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_user1_token,ColumnType.RECOVEREDRESOURCE.toString()},
			{ "Delete recovered resources columns for the normal msporganization with the normal msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_msp_accountadmin1_token,ColumnType.RECOVEREDRESOURCE.toString()},
			{ "Delete recovered resources columns for the normal msporganization with the customer account of normsl msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.RECOVEREDRESOURCE.toString()},
			{ "Delete recovered resources columns for the normal msporganization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.RECOVEREDRESOURCE.toString()},
			{ "Delete recovered resources columns for the normal msporganization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.RECOVEREDRESOURCE.toString()},
			{ "Delete recovered resources columns for the normal msporganization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.RECOVEREDRESOURCE.toString()},
			{ "Delete recovered resources columns for the normal msporganization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.RECOVEREDRESOURCE.toString()},
			{ "Delete recovered resources columns for the normal msporganization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.RECOVEREDRESOURCE.toString()},
			{ "Delete recovered resources columns for the normal msporganization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.RECOVEREDRESOURCE.toString()},


			//Backup job reports 
			{ "Delete backup job report columns for the normal msporganization with the another direct user  ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.direct_org2_user1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "Delete backup job report columns for the normal msporganization with the normal msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_user1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "Delete backup job report columns for the normal msporganization with the normal msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_msp_accountadmin1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "Delete backup job report columns for the normal msporganization with the customer account of normsl msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "Delete backup job report columns for the normal msporganization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "Delete backup job report columns for the normal msporganization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "Delete backup job report columns for the normal msporganization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "Delete backup job report columns for the normal msporganization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "Delete backup job report columns for the normal msporganization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "Delete backup job report columns for the normal msporganization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.BACKUP_JOB_REPORT.toString()},

			//log columns
			{ "Delete log columns for the normal msporganization with the another direct user  ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.direct_org2_user1_token,ColumnType.LOG.toString()},
			{ "Delete log columns for the normal msporganization with the normal msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_user1_token,ColumnType.LOG.toString()},
			{ "Delete log columns for the normal msporganization with the normal msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_msp_accountadmin1_token,ColumnType.LOG.toString()},
			{ "Delete log columns for the normal msporganization with the customer account of normsl msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.LOG.toString()},
			{ "Delete log columns for the normal msporganization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.LOG.toString()},
			{ "Delete log columns for the normal msporganization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.LOG.toString()},
			{ "Delete log columns for the normal msporganization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.LOG.toString()},
			{ "Delete log columns for the normal msporganization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.LOG.toString()},
			{ "Delete log columns for the normal msporganization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.LOG.toString()},
			{ "Delete log columns for the normal msporganization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.LOG.toString()},


			//Recovery job reports 
			{ "Delete recovery_job_report columns for the normal msporganization with the another direct user  ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.direct_org2_user1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "Delete recovery_job_report columns for the normal msporganization with the normal msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_user1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "Delete recovery_job_report columns for the normal msporganization with the normal msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_msp_accountadmin1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "Delete recovery_job_report columns for the normal msporganization with the customer account of normsl msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "Delete recovery_job_report columns for the normal msporganization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "Delete recovery_job_report columns for the normal msporganization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "Delete recovery_job_report columns for the normal msporganization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "Delete recovery_job_report columns for the normal msporganization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "Delete recovery_job_report columns for the normal msporganization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "Delete lrecovery_job_report  columns for the normal msporganization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.RESTORE_JOB_REPORT.toString()},

			//Source columns 
			{ "Delete source columns for the normal msporganization with the another direct user  ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.direct_org2_user1_token,ColumnType.SOURCE.toString()},
			{ "Delete source columns for the normal msporganization with the normal msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_user1_token,ColumnType.SOURCE.toString()},
			{ "Delete source columns for the normal msporganization with the normal msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_msp_accountadmin1_token,ColumnType.SOURCE.toString()},
			{ "Delete source columns for the normal msporganization with the customer account of normsl msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.SOURCE.toString()},
			{ "Delete source columns for the normal msporganization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.SOURCE.toString()},
			{ "Delete source columns for the normal msporganization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.SOURCE.toString()},
			{ "Delete source columns for the normal msporganization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.SOURCE.toString()},
			{ "Delete source columns for the normal msporganization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.SOURCE.toString()},
			{ "Delete source columns for the normal msporganization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.SOURCE.toString()},
			{ "Delete source  columns for the normal msporganization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.SOURCE.toString()},


			//Job columns 
			{ "Delete job columns for the normal msporganization with the another direct user  ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.direct_org2_user1_token,ColumnType.JOB.toString()},
			{ "Delete job columns for the normal msporganization with the normal msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_user1_token,ColumnType.JOB.toString()},
			{ "Delete job columns for the normal msporganization with the normal msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_msp_accountadmin1_token,ColumnType.JOB.toString()},
			{ "Delete job columns for the normal msporganization with the customer account of normsl msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.JOB.toString()},
			{ "Delete job columns for the normal msporganization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.JOB.toString()},
			{ "Delete job columns for the normal msporganization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.JOB.toString()},
			{ "Delete job columns for the normal msporganization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.JOB.toString()},
			{ "Delete job columns for the normal msporganization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.JOB.toString()},
			{ "Delete job columns for the normal msporganization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.JOB.toString()},
			{ "Delete job columns for the normal msporganization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.JOB.toString()},

			//Destination columns 
			{ "Delete destiantion columns for the normal msporganization with the another direct user  ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.direct_org2_user1_token,ColumnType.DESTINATION.toString()},
			{ "Delete destiantion columns for the normal msporganization with the normal msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_user1_token,ColumnType.DESTINATION.toString()},
			{ "Delete destiantion columns for the normal msporganization with the normal msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_msp_accountadmin1_token,ColumnType.DESTINATION.toString()},
			{ "Delete destiantion columns for the normal msporganization with the customer account of normsl msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.DESTINATION.toString()},
			{ "Delete destiantion columns for the normal msporganization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.DESTINATION.toString()},
			{ "Delete destiantion columns for the normal msporganization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.DESTINATION.toString()},
			{ "Delete destiantion columns for the normal msporganization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.DESTINATION.toString()},
			{ "Delete destiantion columns for the normal msporganization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.DESTINATION.toString()},
			{ "Delete destiantion columns for the normal msporganization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.DESTINATION.toString()},
			{ "Delete destiantion columns for the normal msporganization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.DESTINATION.toString()},

			//PolicyTask columns 
			{ "Delete policytaskReport columns for the normal msporganization with the another direct user  ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.direct_org2_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Delete policytaskReport columns for the normal msporganization with the normal msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Delete policytaskReport columns for the normal msporganization with the normal msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_msp_accountadmin1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Delete policytaskReport columns for the normal msporganization with the customer account of normsl msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Delete policytaskReport columns for the normal msporganization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Delete policytaskReport columns for the normal msporganization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Delete policytaskReport columns for the normal msporganization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Delete policytaskReport columns for the normal msporganization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Delete policytaskReport columns for the normal msporganization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "Delete policytaskReport columns for the normal msporganization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},


			//user columns 
			{ "Delete user columns for the normal msporganization with the another direct user  ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.direct_org2_user1_token,ColumnType.USER.toString()},
			{ "Delete user columns for the normal msporganization with the normal msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_user1_token,ColumnType.USER.toString()},
			{ "Delete user columns for the normal msporganization with the normal msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_msp_accountadmin1_token,ColumnType.USER.toString()},
			{ "Delete user columns for the normal msporganization with the customer account of normsl msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.USER.toString()},
			{ "Delete user columns for the normal msporganization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.USER.toString()},
			{ "Delete user columns for the normal msporganization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.USER.toString()},
			{ "Delete user columns for the normal msporganization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.USER.toString()},
			{ "Delete user columns for the normal msporganization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.USER.toString()},
			{ "Delete user columns for the normal msporganization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.USER.toString()},
			{ "Delete user columns for the normal msporganization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.USER.toString()},
		
			
			//report columns 
			{ "Delete report columns for the normal msporganization with the another direct user  ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.direct_org2_user1_token,ColumnType.REPORT.toString()},
			{ "Delete report columns for the normal msporganization with the normal msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_user1_token,ColumnType.REPORT.toString()},
			{ "Delete report columns for the normal msporganization with the normal msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_msp_accountadmin1_token,ColumnType.REPORT.toString()},
			{ "Delete report columns for the normal msporganization with the customer account of normsl msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.REPORT.toString()},
			{ "Delete report columns for the normal msporganization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.REPORT.toString()},
			{ "Delete report columns for the normal msporganization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.REPORT.toString()},
			{ "Delete report columns for the normal msporganization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.REPORT.toString()},
			{ "Delete report columns for the normal msporganization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.REPORT.toString()},
			{ "Delete report columns for the normal msporganization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.REPORT.toString()},
			{ "Delete report columns for the normal msporganization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.REPORT.toString()},
		
		//Managed Report Schedules columns 
			
			//report columns 
			{ "Delete managedReportsSchedules columns for the normal msporganization with the another direct user  ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.direct_org2_user1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "Delete managedReportsSchedules columns for the normal msporganization with the normal msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_user1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "Delete managedReportsSchedules columns for the normal msporganization with the normal msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_msp_accountadmin1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "Delete managedReportsSchedules columns for the normal msporganization with the customer account of normsl msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "Delete managedReportsSchedules columns for the normal msporganization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "Delete managedReportsSchedules columns for the normal msporganization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "Delete managedReportsSchedules columns for the normal msporganization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "Delete managedReportsSchedules columns for the normal msporganization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "Delete managedReportsSchedules columns for the normal msporganization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "Delete managedReportsSchedules columns for the normal msporganization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.REPORT_SCHEDULE.toString()},
		
		
		
		
		};
	}

	@Test(dataProvider = "deleteColumns_insufficientPermissions",enabled=true)
	public void deleteColumns_InsufficientPermissions(String testCase,
			String validtoken,
			String user_id,
			String ortherOrgToken,
			String columnType
			) {
		test = ExtentManager.getNewTest(testCase);
		test.assignAuthor("sykam.nagamalleswari");

		spogServer.setToken(validtoken);
		spogServer.getAndDeleteColumns(columnType, user_id, validtoken);

		String [] orderId = new String [] {"2","1"};

		ArrayList<String> columnIdList = getDefaultColumns(validtoken, user_id, columnType);

		test.log(LogStatus.INFO, "Compose the log columns as per the user request");
		ArrayList<HashMap<String, Object>>expected_columns=jp.composeColumnsInfo(1, columnIdList, orderId, sort, filter, visible);


		test.log(LogStatus.INFO, "Create the log columns for the specified user");
		spogServer.setToken(validtoken);
		spogServer.postColumnsWithCheck(validtoken, user_id, columnType, expected_columns, SpogConstants.SUCCESS_POST, null, test);

		test.log(LogStatus.INFO, "Compose data to delet the column");
		String deleteColuminfo = jp.composeColumnFilter(columnType, user_id, "none", "none");

		test.log(LogStatus.INFO, "Get the columns to know the user_column_id");
		Response response = spogServer.getColumns(validtoken, deleteColuminfo);

		test.log(LogStatus.INFO, "the User column id Info");
		String delete_Column_id= response.then().extract().path("data.user_column_id");

		test.log(LogStatus.INFO, "Delete columns with the other orgnanziation token ");
		spogServer.deleteColumnsWithCheck(ortherOrgToken, delete_Column_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY,test);


		test.log(LogStatus.INFO, "Delete columns with the invalid columsn id ");
		spogServer.deleteColumnsWithCheck(ortherOrgToken, UUID.randomUUID().toString(),SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.COLUMN_ID_DOESNOT_EXIST,test);

		test.log(LogStatus.INFO, "Delete columns with token");
		spogServer.deleteColumns(validtoken, delete_Column_id);


		test.log(LogStatus.INFO, "Delete columns with Multiple times ");
		spogServer.deleteColumnsWithCheck(validtoken, delete_Column_id,SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.COLUMN_ID_DOESNOT_EXIST,test);


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

}
